package mycore
package decode

import chisel3._

import common.configurations._
import common.constants._

class ifToDecDataIO extends Bundle()
{
  val PC   = UInt(XLEN.W)
  val inst = UInt(WID_INST.W)
}
