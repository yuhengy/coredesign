package mycore

import chisel3._
import chisel3.util.experimental.BoringUtils

import common.constants._
import common.configurations._

class regfile extends Module {
  val io = IO(new Bundle{
  })
  // Register File
  val regfile = Mem(NUM_REG, UInt(XLEN.W))
  BoringUtils.addSource(VecInit((0 to NUM_REG-1).map(i => regfile(i.U))), "diffTestRegfile")
} 
