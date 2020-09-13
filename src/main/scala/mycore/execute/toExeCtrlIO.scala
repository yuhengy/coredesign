package mycore
package decode

import chisel3._

class toExeCtrlIO extends bundle()
{
  val brType  = Output(UInt(brType_w.W))
  val aluFunc = Output(UInt(aluFunc_w.W))
  val wbSel   = Output(UInt(wbSel_w.W))
  val rfWen   = Output(Bool())
  val memRd   = Output(Bool())
  val memWr   = Output(Bool())
  val memMask = Output(UInt(memMask_w.W))
}
