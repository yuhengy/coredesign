package mycore
package writeBack

import scala.language.reflectiveCalls

import chisel3._
import chisel3.util._
import chisel3.util.experimental.BoringUtils

import common.constants._
import common.configurations._

import mycore.memory.memToWbDataIO
import mycore.memory.memToWbCtrlIO

class writeBackTOP extends Module
{
  val io = IO(new Bundle{
  //memTowbData
    val memToWbDataIO = Input(new memToWbDataIO)
  //memToWbCtrl
    val memToWbCtrlIO = Flipped(Decoupled(new memToWbCtrlIO))

  //wbToDecRegWrite
    val wbToDecWbAddr = Output(UInt(WID_REG_ADDR.W))
    val wbToDecWbdata = Output(UInt(XLEN.W))
    val wbToDecWRfWen = Output(Bool())
  })

//--------------execute global status start--------------
//input
  val regIsUpdated = RegInit(false.B)
//output
  val regDataIO = Reg(new memToWbDataIO)
  // regCtrlIO
//private
  val regCtrlIO_r = RegInit(memToWbCtrlIO.init)
  when (io.memToWbCtrlIO.valid && io.memToWbCtrlIO.ready) {
    regDataIO <> io.memToWbDataIO
    regCtrlIO_r <> io.memToWbCtrlIO.bits
  }
  val regCtrlIO = Mux(regIsUpdated, regCtrlIO_r, memToWbCtrlIO.init)
//^^^^^^^^^^^^^^execute global status end^^^^^^^^^^^^^^

//--------------stall&kill start--------------
  //val regIsUpdated = RegInit(false.B)
  when (io.memToWbCtrlIO.valid && io.memToWbCtrlIO.ready)
    {regIsUpdated := true.B}.
  otherwise
    {regIsUpdated := false.B}  //TODO: check otherwise can be left

  io.memToWbCtrlIO.ready := true.B
//^^^^^^^^^^^^^^stall&kill end^^^^^^^^^^^^^^

//--------------io.output start--------------
  io.wbToDecWbAddr := regDataIO.wbAddr
  io.wbToDecWbdata := regDataIO.wbData
  io.wbToDecWRfWen := regCtrlIO.rfWen
//^^^^^^^^^^^^^^io.output end^^^^^^^^^^^^^^

  val commitedPC = RegNext(regDataIO.PC, 0.U)
  BoringUtils.addSource(io.memToWbCtrlIO.valid && io.memToWbCtrlIO.ready, "diffTestCommit")
  BoringUtils.addSource(commitedPC, "diffTestPC")


  if (DEBUG) {
    printf(s"PC to update regFile = 0x%x; stageValid = %d; instValid = %d\n", regDataIO.PC, regIsUpdated, regCtrlIO.cs_val_inst)
    assert(!(regIsUpdated && !regCtrlIO.cs_val_inst))
  }

}
