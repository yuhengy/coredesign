package mycore
package execute

import chisel3._

import common.configurations._
import common.constants._

class decToExeDataIO extends bundle()
{
  val PC        = Input(UInt(XLEN.W))
  val inst      = Input(UInt(WID_INST.W))
  val aluop1    = Input(UInt(XLEN.W))
  val aluop2    = Input(UInt(XLEN.W))
  val rfRs2Data = Input(UInt(XLEN.W))
  val wbAddr    = Input(UInt(XLEN.W))

}
