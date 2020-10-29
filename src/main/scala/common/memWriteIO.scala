package common

import chisel3._

import common.configurations._

class memWriteIO extends Bundle {
  val reqReady = Input(Bool())
  val addr = Output(UInt(XLEN.W))
  val data = Output(UInt(XLEN.W))
  val mask = Output(UInt((XLEN/8).W))
  val en = Output(Bool())
  
  val respValid = Input(Bool())
}
