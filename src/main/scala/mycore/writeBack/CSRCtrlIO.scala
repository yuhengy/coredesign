package mycore
package writeBack

import chisel3._

import common.configurations._
import common.constants._

// different from CSRDataIO, these singals must always be specified
class CSRCtrlIO extends Bundle()
{
  val CSRWriteType = Input(UInt(CSRWriteType_w.W))
  val CSRSupported = Output(Bool())

}
