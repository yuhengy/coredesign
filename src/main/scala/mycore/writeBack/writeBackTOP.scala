package mycore
package writeBack

import scala.language.reflectiveCalls

import chisel3._
import chisel3.util._
import chisel3.experimental.ChiselEnum
import chisel3.util.experimental.BoringUtils

import common.constants._
import common.configurations._

import mycore.memory.memToWbDataIO
import mycore.memory.memToWbCtrlIO

class writeBackTOP extends Module
{
  val io = IO(new Bundle{
  //memTowbData
    val inDataIO = Input(new memToWbDataIO)
  //memToWbCtrl
    val inCtrlIO = Flipped(Decoupled(new memToWbCtrlIO))

  //wbToDecRegWrite
    val wbToDecWbAddr = Output(UInt(WID_REG_ADDR.W))
    val wbToDecWbdata = Output(UInt(XLEN.W))
    val wbToDecWRfWen = Output(Bool())
  })

//--------------execute global status start--------------
//input
  object stateEnum extends ChiselEnum {
    val reset, idle, regIsUpdated = Value
  }
  val state = RegInit(stateEnum.reset)
//output
  val regDataIO = Reg(new memToWbDataIO)
  // regCtrlIO
//private
  val regCtrlIO_r = RegInit(memToWbCtrlIO.init)
  when (io.inCtrlIO.valid && io.inCtrlIO.ready) {
    regDataIO <> io.inDataIO
    regCtrlIO_r <> io.inCtrlIO.bits
  }
  val regCtrlIO = Mux(state === stateEnum.regIsUpdated, regCtrlIO_r, memToWbCtrlIO.init)
//^^^^^^^^^^^^^^execute global status end^^^^^^^^^^^^^^

//--------------state machine start--------------
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
      otherwise
        {state := stateEnum.idle}
    }
  }
//^^^^^^^^^^^^^^state machine end^^^^^^^^^^^^^^

//--------------control signal start--------------
  io.inCtrlIO.ready := true.B
//^^^^^^^^^^^^^^control signal end^^^^^^^^^^^^^^

//--------------io.output start--------------
  io.wbToDecWbAddr := regDataIO.wbAddr
  io.wbToDecWbdata := regDataIO.wbData
  io.wbToDecWRfWen := regCtrlIO.rfWen
//^^^^^^^^^^^^^^io.output end^^^^^^^^^^^^^^

  if (DEBUG) {
    printf(s"PC to update regFile = 0x%x; stageValid = %d; instValid = %d\n", regDataIO.PC, state === stateEnum.regIsUpdated, regCtrlIO.cs_val_inst)
    assert(!(state === stateEnum.regIsUpdated && !regCtrlIO.cs_val_inst))
    
    BoringUtils.addSource(regCtrlIO.goodTrapNemu, "GoodTrapNemu")

    val commitedPC = RegNext(regDataIO.PC, 0.U)
    BoringUtils.addSource(io.inCtrlIO.valid && io.inCtrlIO.ready, "diffTestCommit")
    BoringUtils.addSource(commitedPC, "diffTestPC")
  }

}
