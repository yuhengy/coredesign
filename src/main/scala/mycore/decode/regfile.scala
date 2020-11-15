package mycore
package decode

import scala.language.reflectiveCalls

import chisel3._
import chisel3.util.experimental.BoringUtils

import common.constants._
import common.configurations._

class regfile extends Module {
  val io = IO(new Bundle{
    val rAddr2 = Input(UInt(WID_REG_ADDR.W))
    val rData2 = Output(UInt(XLEN.W))
    val rAddr1 = Input(UInt(WID_REG_ADDR.W))
    val rData1 = Output(UInt(XLEN.W))
    val wAddr = Input(UInt(WID_REG_ADDR.W))
    val wData = Input(UInt(XLEN.W))
    val wEn = Input(Bool())
  })
  val regfile = Mem(NUM_REG, UInt(XLEN.W))
  
  io.rData2 := Mux(io.rAddr2 =/= 0.U, regfile(io.rAddr2), 0.asUInt(XLEN.W))
  io.rData1 := Mux(io.rAddr1 =/= 0.U, regfile(io.rAddr1), 0.asUInt(XLEN.W))
  when (io.wEn && (io.wAddr =/= 0.U)) {
    regfile(io.wAddr) := io.wData
  }

  if (DEBUG) {
    if (DIFFTEST) {
      BoringUtils.addSource(VecInit((0 to NUM_REG-1).map(i => regfile(i.U))), "diffTestRegfile")
    }
  }
} 
