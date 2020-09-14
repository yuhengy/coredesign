package mycore
package decode

import chisel3._

import common.constants._

class allCtrlIO extends Bundle()
{
  val brType  = UInt(brType_w.W)
  val op1Sel  = UInt(op1Sel_w.W)
  val op2Sel  = UInt(op2Sel_w.W)
  val aluFunc = UInt(aluFunc_w.W)
  val wbSel   = UInt(wbSel_w.W)
  val rfWen   = Bool()
  val memRd   = Bool()
  val memWr   = Bool()
  val memMask = UInt(memMask_w.W)
}
