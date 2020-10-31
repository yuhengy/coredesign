package mycore
package writeBack

import chisel3._

import common.configurations._
import common.constants._

// different from CSRCtrlIO, these singals can be random and don not care
class CSRDataIO extends Bundle()
{
  val PC           = Input(UInt(XLEN.W))
  val CSRWriteData = Input(UInt(XLEN.W))
  val CSRAddr      = Input(UInt(CSR_ADDR_w.W))

  val CSRReadData     = Output(UInt(XLEN.W))
  val exceptionTarget = Output(UInt(XLEN.W))
}
