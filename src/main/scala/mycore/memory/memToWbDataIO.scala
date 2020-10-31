package mycore
package memory

import chisel3._

import common.configurations._
import common.constants._

class memToWbDataIO extends Bundle()
{
  val PC     = UInt(XLEN.W)
  val wbData = UInt(XLEN.W)
  val wbAddr = UInt(XLEN.W)

  val CSRWriteData = UInt(XLEN.W)
  val CSRAddr = UInt(CSR_ADDR_w.W)
}
