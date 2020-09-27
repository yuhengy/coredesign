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
  val regDataIO = Reg(new memToWbDataIO)
  regDataIO <> io.memToWbDataIO

  val regCtrlIO = RegInit({
    val temp = Wire(new memToWbCtrlIO)
    temp.init
    temp
  })
  regCtrlIO <> io.memToWbCtrlIO.bits
//^^^^^^^^^^^^^^execute global status end^^^^^^^^^^^^^^

//--------------stall&kill start--------------
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

}
