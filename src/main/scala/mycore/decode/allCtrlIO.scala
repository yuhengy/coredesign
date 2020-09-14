package mycore
package decode

import chisel3._

import common.constants._

class allCtrlIO extends bundle()
{
  val brType  = Output(UInt(brType_w.W))
  val op1Sel  = Output(UInt(op1Sel_w.W))
  val op2Sel  = Output(UInt(op2Sel_w.W))
  val aluFunc = Output(UInt(aluFunc_w.W))
  val wbSel   = Output(UInt(wbSel_w.W))
  val rfWen   = Output(Bool())
  val memRd   = Output(Bool())
  val memWr   = Output(Bool())
  val memMask = Output(UInt(memMask_w.W))
}
