package mycore
package execute

import chisel3._

import common.configurations._
import common.constants._

class decToExeDataIO extends Bundle()
{
  val PC        = UInt(XLEN.W)
  val inst      = UInt(WID_INST.W)
  val aluop1    = UInt(XLEN.W)
  val aluop2    = UInt(XLEN.W)
  val rfRs2Data = UInt(XLEN.W)
  val wbAddr    = UInt(XLEN.W)

}
