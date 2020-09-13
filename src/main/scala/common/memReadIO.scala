package common

import chisel3._

import common.configurations._

class memReadIO extends Bundle {
  val addr = Output(UInt(XLEN.W))
  val data = Input(UInt(XLEN.W))
  val en = Output(Bool())
}
