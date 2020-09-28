package mycore
package decode

import chisel3._

import common.configurations._
import common.constants._

class decToExeCtrlIO extends Bundle()
{
  val brType      = UInt(brType_w.W)
  val aluFunc     = UInt(aluFunc_w.W)
  val wbSel       = UInt(wbSel_w.W)
  val rfWen       = Bool()
  val memRd       = Bool()
  val memWr       = Bool()
  val memMask     = UInt(memMask_w.W)
  val cs_val_inst = Bool()

  def init = {
    brType := BR_N
    rfWen  := REN_0
    memRd  := MRD_0
    memWr  := MWR_0

    //TODO: following init is useless
    aluFunc     := ALU_X
    wbSel       := WB_X
    memMask     := MSK_X
    cs_val_inst := false.B
  }
  
}
