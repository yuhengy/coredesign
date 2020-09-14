package mycore
package writeBack

import scala.language.reflectiveCalls

import chisel3._

import common.constants._
import common.configurations._

class writeBackTOP extends Module
{
  val io = IO(new Bundle{
  //memTowbData
    val memToWbDataIO = Input(new memToWbDataIO)
  //memToWbCtrl
    val memToWbCtrlIO = Input(new memToWbCtrlIO)

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
  regCtrlIO <> io.memToWbCtrlIO
//^^^^^^^^^^^^^^execute global status end^^^^^^^^^^^^^^

//--------------io.output start--------------
  io.wbToDecWbAddr := regDataIO.wbAddr
  io.wbToDecWbdata := regDataIO.wbData
  io.wbToDecWRfWen := regCtrlIO.rfWen
//^^^^^^^^^^^^^^io.output end^^^^^^^^^^^^^^

}
