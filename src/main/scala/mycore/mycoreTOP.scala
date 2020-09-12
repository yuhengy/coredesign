package mycore

import scala.language.reflectiveCalls

import chisel3._

//if (DEBUG)
import common.constants._
import common.configurations._
//endif

class mycoreTOP extends Module
{
  val io = IO(new mycoreTOPIO)
  io := DontCare
  val regfile = Module(new regfile())
  regfile.io := DontCare

  if (DEBUG) {
  regfile.io.wEn := true.B
  regfile.io.wAddr := 5.asUInt(WID_REG_ADDR.W)
  regfile.io.wData := io.instReadIO.data
  io.instReadIO.addr := ADDR_START.asUInt(XLEN.W)
  io.instReadIO.en := true.B
  }
}
