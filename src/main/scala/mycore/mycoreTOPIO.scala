package mycore

import chisel3._

import common.memReadIO
import common.memWriteIO

class mycoreTOPIO extends Bundle()
{
  val instReadIO = new memReadIO
  val dataReadIO = new memReadIO
  val dataWriteIO = new memWriteIO
}
