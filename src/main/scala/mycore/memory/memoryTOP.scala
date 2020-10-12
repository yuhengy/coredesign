package mycore
package memory

import scala.language.reflectiveCalls

import chisel3._
import chisel3.util._

import common.constants._
import common.configurations._

import mycore.execute.exeToMemDataIO
import mycore.execute.exeToMemCtrlIO

class memoryTOP extends Module
{
  val io = IO(new Bundle{
  //exeToMemData
    val exeToMemDataIO = Input(new exeToMemDataIO)
  //exeToMenCtrl
    val exeToMemCtrlIO = Flipped(Decoupled(new exeToMemCtrlIO))

  //memTowbData
    val memToWbDataIO = Output(new memToWbDataIO)
  //memToWbCtrl
    val memToWbCtrlIO = Decoupled(new memToWbCtrlIO)

  //memToDecFeedback
    val memDest = Valid(UInt(WID_REG_ADDR.W))

  //fromRam
    val dataReadIO = new Bundle{
      val data = Input(UInt(XLEN.W))
    }
  })

//--------------execute global status start--------------
//input
  val regIsUpdated = RegInit(false.B)
//output
  val regDataIO = Reg(new exeToMemDataIO)
  // regCtrlIO
//private
  val regCtrlIO_r = RegInit(exeToMemCtrlIO.init)
  when (io.exeToMemCtrlIO.valid && io.exeToMemCtrlIO.ready) {
    regDataIO <> io.exeToMemDataIO
    regCtrlIO_r <> io.exeToMemCtrlIO.bits
  }
  val regCtrlIO = Mux(regIsUpdated, regCtrlIO_r, exeToMemCtrlIO.init)
//^^^^^^^^^^^^^^execute global status end^^^^^^^^^^^^^^

//--------------writeBack data start--------------
//private
  val maskedReadData = MuxCase(io.dataReadIO.data, Array(
                       (regCtrlIO.memExt === EXT_BS) -> Cat(Fill(56, io.dataReadIO.data( 7)), io.dataReadIO.data(7,0)),
                       (regCtrlIO.memExt === EXT_BU) -> Cat(Fill(56, 0.U                   ), io.dataReadIO.data(7,0)),
                       (regCtrlIO.memExt === EXT_HS) -> Cat(Fill(48, io.dataReadIO.data(15)), io.dataReadIO.data(15,0)),
                       (regCtrlIO.memExt === EXT_HU) -> Cat(Fill(48, 0.U                   ), io.dataReadIO.data(15,0)),
                       (regCtrlIO.memExt === EXT_WS) -> Cat(Fill(32, io.dataReadIO.data(31)), io.dataReadIO.data(31,0)),
                       (regCtrlIO.memExt === EXT_WU) -> Cat(Fill(32, 0.U                   ), io.dataReadIO.data(31,0)),
                       (regCtrlIO.memExt === EXT_D ) ->                                       io.dataReadIO.data
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

//--------------stall&kill start--------------
  val stall = false.B
  //val regIsUpdated = RegInit(false.B)
  when (io.exeToMemCtrlIO.valid && io.exeToMemCtrlIO.ready)
    {regIsUpdated := true.B}.
  elsewhen (io.memToWbCtrlIO.valid && io.memToWbCtrlIO.ready)
    {regIsUpdated := false.B}  //TODO: check otherwise can be left

  io.exeToMemCtrlIO.ready := !stall
  io.memToWbCtrlIO.valid := regIsUpdated && !stall
//^^^^^^^^^^^^^^stall&kill end^^^^^^^^^^^^^^

//--------------io.output start--------------
//memToWbData
  io.memToWbDataIO.PC     := regDataIO.PC
  io.memToWbDataIO.wbData := wbData
  io.memToWbDataIO.wbAddr := regDataIO.wbAddr

//memToWbCtrl
  io.memToWbCtrlIO.bits.rfWen := regCtrlIO.rfWen
  io.memToWbCtrlIO.bits.cs_val_inst := regCtrlIO.cs_val_inst
  if (DEBUG) {
    io.memToWbCtrlIO.bits.goodTrapNemu := regCtrlIO.goodTrapNemu
  }

//exeToDecFeedback
  io.memDest.bits  := regDataIO.wbAddr
  io.memDest.valid := regCtrlIO.rfWen
//^^^^^^^^^^^^^^io.output end^^^^^^^^^^^^^^

}
