package sim

import chisel3._
import chisel3.util.experimental.BoringUtils

import common.constants._
import common.configurations._

class diffTestIO extends Bundle {
  val regFile = Output(Vec(NUM_REG, UInt(XLEN.W)))
}