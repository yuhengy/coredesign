package mycore

import chisel3._

class mycoreTOPIO extends Bundle()
{
  val instReadIO = new memReadIO
  val dataReadIO = new memReadIO
  val dataWriteIO = new memWriteIO
}
