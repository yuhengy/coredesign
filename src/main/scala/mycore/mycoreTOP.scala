package mycore

import chisel3._

class mycoreTOP extends Module
{
  val io = IO(new Bundle{
  })
  val regfile = Module(new regfile())
}