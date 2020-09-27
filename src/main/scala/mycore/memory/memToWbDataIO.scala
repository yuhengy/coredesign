package mycore
package memory

import chisel3._

import common.configurations._

class memToWbDataIO extends Bundle()
{
  val PC     = UInt(XLEN.W)
  val wbData = UInt(XLEN.W)
  val wbAddr = UInt(XLEN.W)
}
