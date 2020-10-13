package mycore
package preInstFetch

import chisel3._

import common.configurations._
import common.constants._

class preifToIfDataIO extends Bundle()
{
  val PC        = UInt(XLEN.W)
  val addrAlign = UInt(addrAlign_w.W)
}
