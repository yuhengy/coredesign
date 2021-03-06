package mycore
package execute

import chisel3._

import common.configurations._
import common.constants._

class exeToMemCtrlIO extends Bundle()
{
  val wbSel       = UInt(wbSel_w.W)
  val rfWen       = Bool()
  val memRd       = Bool()
  val memWr       = Bool()
  val memMask     = UInt(memMask_w.W)
  val memExt      = UInt(memExt_w.W)
  val cs_val_inst = Bool()
  val CSRWriteType = UInt(CSRWriteType_w.W)
  //if (DEBUG) {
    val goodTrapNemu = Bool()
  //}

  def init = {
    rfWen := REN_0
    memRd  := MRD_0
    memWr  := MWR_0
    cs_val_inst := false.B
    CSRWriteType := CSRWT_U

    //TODO: following init is useless
    wbSel       := WB_X
    memMask     := MSK_X
    memExt      := EXT_X
    if (DEBUG) {
      goodTrapNemu := false.B
    }
  }
}

object exeToMemCtrlIO
{
  val init = {
    val temp = Wire(new exeToMemCtrlIO)
    temp.init
    temp
  }
}
