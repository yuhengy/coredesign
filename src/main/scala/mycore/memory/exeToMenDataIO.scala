package mycore
package memory

import chisel3._

import common.configurations._

class exeToMemDataIO extends Bundle()
{
  val inst   = UInt(XLEN.W)
  val wbData = UInt(XLEN.W)
  val wbAddr = UInt(XLEN.W)
}