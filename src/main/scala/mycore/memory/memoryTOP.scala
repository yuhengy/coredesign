package mycore
package memory

import scala.language.reflectiveCalls

import chisel3._
import chisel3.util.MuxCase

import common.constants._
import common.configurations._
import mycore.writeBack.memToWbDataIO
import mycore.writeBack.memToWbCtrlIO

class memoryTOP extends Module
{
  val io = IO(new Bundle{
  //exeToMemData
    val exeToMemDataIO = Input(new exeToMemDataIO)
  //exeToMenCtrl
    val exeToMemCtrlIO = Input(new exeToMemCtrlIO)

  //memTowbData
    val memToWbDataIO = Output(new memToWbDataIO)
  //memToWbCtrl
    val memToWbCtrlIO = Output(new memToWbCtrlIO)

  //fromRam
    val dataReadIO = new Bundle{
      val data = Input(UInt(XLEN.W))
    }
  })

//--------------execute global status start--------------
  val regDataIO = Reg(new exeToMemDataIO)
  regDataIO <> io.exeToMemDataIO

  val regCtrlIO = RegInit({
    val temp = Wire(new exeToMemCtrlIO)
    temp.init
    temp
  })
  regCtrlIO <> io.exeToMemCtrlIO
//^^^^^^^^^^^^^^execute global status end^^^^^^^^^^^^^^

//--------------writeBack data start--------------
//output
  val wbData = MuxCase(regDataIO.wbData, Array(
              (regCtrlIO.wbSel === WB_ALU) -> regDataIO.wbData,
              (regCtrlIO.wbSel === WB_PC4) -> regDataIO.wbData,
              (regCtrlIO.wbSel === WB_MEM) -> io.dataReadIO.data,
              //(regCtrlIO.wbSel === WB_CSR) -> csr.io.rw.rdata
              ))
//^^^^^^^^^^^^^^writeBack data end^^^^^^^^^^^^^^

//--------------io.output start--------------
//memTowbData
  io.memToWbDataIO.wbData := wbData
  io.memToWbDataIO.wbAddr := regDataIO.wbAddr

//memToWbCtrl
  io.memToWbCtrlIO.rfWen := regCtrlIO.rfWen
//^^^^^^^^^^^^^^io.output end^^^^^^^^^^^^^^

}
