package mycore
package execute

import chisel3._

import common.configurations._

class exeToMemDataIO extends Bundle()
{
  val PC        = UInt(XLEN.W)
  val inst      = UInt(XLEN.W)
  val wbData    = UInt(XLEN.W)
  val wbAddr    = UInt(XLEN.W)
  val addrAlign = UInt(addrAlign_w.W)
}
