package mycore
package memory

import chisel3._
import chisel3.util.MuxCase

import common.constants._
import common.configurations._
import mycore.writeBack.memTowbDataIO
import mycore.writeBack.memToWbCtrl

class memoryTOP extends Module
{
  io = IO(new Bundle{
  //exeToMemData
    val exeToMenDataIO = new exeToMenDataIO
  //exeToMenCtrl
    val exeToMemCtrlIO = new exeToMemCtrlIO

  //memTowbData
    val memTowbDataIO = Flipped(new memTowbDataIO)
  //memToWbCtrl
    val memToWbCtrl = Flipped(new memToWbCtrl)

  //fromRam
    val dataReadIO = new Bundle{
      val data = Input(UInt(XLEN.W))
    }
  })

//--------------execute global status start--------------
  val regDataIO = Reg(new Packet)
  regDataIO <> io.exeToMenDataIO

  val regCtrlIO = RegInit({
    val temp = wire(new exeToMemCtrlIO)
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
  io.memTowbDataIO <> regDataIO
  io.memTowbDataIO := wbData  //TODO: check this will rewrite the assignment above

//memToWbCtrl
  io.memToWbCtrlIO <> regCtrlIO
//^^^^^^^^^^^^^^io.output end^^^^^^^^^^^^^^

}
