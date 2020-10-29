package mycore
package memory

import scala.language.reflectiveCalls

import chisel3._
import chisel3.util._

import common.constants._
import common.configurations._
import chisel3.experimental.ChiselEnum

import mycore.execute.exeToMemDataIO
import mycore.execute.exeToMemCtrlIO

class memoryTOP extends Module
{
  val io = IO(new Bundle{
  //exeToMemData
    val inDataIO = Input(new exeToMemDataIO)
  //exeToMenCtrl
    val inCtrlIO = Flipped(Decoupled(new exeToMemCtrlIO))

  //memTowbData
    val outDataIO = Output(new memToWbDataIO)
  //memToWbCtrl
    val outCtrlIO = Decoupled(new memToWbCtrlIO)

  //memToDecFeedback
    val memDest = Valid(UInt(WID_REG_ADDR.W))

  //fromRam
    val dataReadIO = new Bundle{
      val data = Input(UInt(XLEN.W))
      val respValid = Input(Bool())
    }
    val dataWriteIO = new Bundle{
      val respValid = Input(Bool())
    }
  })

//--------------execute global status start--------------
//input
  object stateEnum extends ChiselEnum {
    val reset, idle, regIsUpdated, resultIsBuffered = Value
  }
  val state = RegInit(stateEnum.reset)
//output
  val regDataIO = Reg(new exeToMemDataIO)
  // regCtrlIO
//private
  val regCtrlIO_r = RegInit(exeToMemCtrlIO.init)
  when (io.inCtrlIO.valid && io.inCtrlIO.ready) {
    regDataIO <> io.inDataIO
    regCtrlIO_r <> io.inCtrlIO.bits
  }
  val regCtrlIO = Mux(state === stateEnum.regIsUpdated || state === stateEnum.resultIsBuffered,
                      regCtrlIO_r, exeToMemCtrlIO.init)
//^^^^^^^^^^^^^^execute global status end^^^^^^^^^^^^^^

//--------------writeBack data start--------------
//private
  val dataAlign      = io.dataReadIO.data >> (regDataIO.addrAlign << 3.U)
  val maskedReadData = MuxCase(dataAlign, Array(
                       (regCtrlIO.memExt === EXT_BS) -> Cat(Fill(56, dataAlign( 7)), dataAlign(7,0)),
                       (regCtrlIO.memExt === EXT_BU) -> Cat(Fill(56, 0.U          ), dataAlign(7,0)),
                       (regCtrlIO.memExt === EXT_HS) -> Cat(Fill(48, dataAlign(15)), dataAlign(15,0)),
                       (regCtrlIO.memExt === EXT_HU) -> Cat(Fill(48, 0.U          ), dataAlign(15,0)),
                       (regCtrlIO.memExt === EXT_WS) -> Cat(Fill(32, dataAlign(31)), dataAlign(31,0)),
                       (regCtrlIO.memExt === EXT_WU) -> Cat(Fill(32, 0.U          ), dataAlign(31,0)),
                       (regCtrlIO.memExt === EXT_D ) ->                              dataAlign
                       ))
//output
  val wbData = MuxCase(regDataIO.wbData, Array(
              (regCtrlIO.wbSel === WB_ALU)  -> regDataIO.wbData,
              (regCtrlIO.wbSel === WB_ALUW) -> Cat(Fill(32,regDataIO.wbData(31)), regDataIO.wbData(31,0)),
              (regCtrlIO.wbSel === WB_PC4)  -> regDataIO.wbData,
              (regCtrlIO.wbSel === WB_MEM)  -> maskedReadData,
              //(regCtrlIO.wbSel === WB_CSR) -> csr.io.rw.rdata
              ))
//^^^^^^^^^^^^^^writeBack data end^^^^^^^^^^^^^^

//--------------state machine start--------------
//input
  val stall = Wire(Bool())
//output
  //object stateEnum extends ChiselEnum {
  //  val reset, idle, regIsUpdated = Value
  //}
  //val state = RegInit(stateEnum.reset)

//private
  switch (state) {
    is (stateEnum.reset) {
      state := stateEnum.idle
    }
    is (stateEnum.idle) {
      when (io.inCtrlIO.valid && io.inCtrlIO.ready)
        {state := stateEnum.regIsUpdated}
    }
    is (stateEnum.regIsUpdated) {
      when (io.inCtrlIO.valid && io.inCtrlIO.ready)
        {state := stateEnum.regIsUpdated}.
      elsewhen (io.outCtrlIO.valid && io.outCtrlIO.ready)
        {state := stateEnum.idle}.
      elsewhen (!stall)
        {state := stateEnum.resultIsBuffered}
    }
    is (stateEnum.resultIsBuffered) {
      when (io.inCtrlIO.valid && io.inCtrlIO.ready)
        {state := stateEnum.regIsUpdated}.
      elsewhen (io.outCtrlIO.valid && io.outCtrlIO.ready)
        {state := stateEnum.idle}
    }
  }
//^^^^^^^^^^^^^^state machine end^^^^^^^^^^^^^^

//--------------control signal start--------------
  stall := state === stateEnum.regIsUpdated &&
           (regCtrlIO.memRd && !io.dataReadIO.respValid ||
            regCtrlIO.memWr && !io.dataWriteIO.respValid)

  io.inCtrlIO.ready := state === stateEnum.reset || state === stateEnum.idle ||
                       io.outCtrlIO.ready && io.outCtrlIO.valid
  io.outCtrlIO.valid := state === stateEnum.regIsUpdated && !stall ||
                        state === stateEnum.resultIsBuffered
//^^^^^^^^^^^^^^control signal end^^^^^^^^^^^^^^

//--------------io.output start--------------
//memToWbData
  io.outDataIO.PC     := regDataIO.PC
  io.outDataIO.wbData := wbData
  io.outDataIO.wbAddr := regDataIO.wbAddr

//memToWbCtrl
  io.outCtrlIO.bits.rfWen := regCtrlIO.rfWen
  io.outCtrlIO.bits.cs_val_inst := regCtrlIO.cs_val_inst
  if (DEBUG) {
    io.outCtrlIO.bits.goodTrapNemu := regCtrlIO.goodTrapNemu
  }

//exeToDecFeedback
  io.memDest.bits  := regDataIO.wbAddr
  io.memDest.valid := regCtrlIO.rfWen
//^^^^^^^^^^^^^^io.output end^^^^^^^^^^^^^^

}
