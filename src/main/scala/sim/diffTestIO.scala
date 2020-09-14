package sim

import chisel3._

import common.constants._
import common.configurations._

class diffTestIO extends Bundle {
  val regFile = Output(Vec(NUM_REG, UInt(XLEN.W)))
  val PC      = Output(UInt(XLEN.W))
}