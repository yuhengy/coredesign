package mycore
package writeBack

import chisel3._

import common.constants._
import common.configurations._

class writeBackTOP extends Module
{
  io = IO(new Bundle{
  //memTowbData
    val memTowbDataIO = new memTowbDataIO
  //memToWbCtrl
    val memToWbCtrl = new memToWbCtrl

  //wbToDecRegWrite
    val wbToDecWbAddr = Output(UInt(WID_REG_ADDR.W))
    val wbToDecWbdata = Output(UInt(XLEN.W))
    val wbToDecWRfWen = Output(Bool())
  })

//--------------execute global status start--------------
  val regDataIO = Reg(new Packet)
  regDataIO <> io.memTowbDataIO

  val regCtrlIO = RegInit({
    val temp = wire(new memToWbCtrl)
    temp.init
    temp
  })
  regCtrlIO <> io.memToWbCtrl
//^^^^^^^^^^^^^^execute global status end^^^^^^^^^^^^^^

//--------------io.output start--------------
  io.wbToDecWbAddr := regDataIO.wbAddr
  io.wbToDecWbdata := regDataIO.wbData
  io.wbToDecWRfWen := regCtrlIO.rfWen
//^^^^^^^^^^^^^^io.output end^^^^^^^^^^^^^^

}
