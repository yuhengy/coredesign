package mycore
package execute

import chisel3._

import common.configurations._
import common.constants._

class decToExeCtrlIO extends bundle()
{
  val brType  = Input(UInt(brType_w.W))
  val aluFunc = Input(UInt(aluFunc_w.W))
  val wbSel   = Input(UInt(wbSel_w.W))
  val rfWen   = Input(Bool())
  val memRd   = Input(Bool())
  val memWr   = Input(Bool())
  val memMask = Input(UInt(memMask_w.W))

  
}
