package mycore
package decode

import chisel3._

import common.configurations._
import common.constants._

class ifToDecDataIO extends bundle()
{
  val PC   = Input(UInt(XLEN.W))
  val inst = Input(UInt(WID_INST.W))
}
