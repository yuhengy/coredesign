package mycore
package execute

import chisel3._

import common.constants._

class exeToMemCtrlIO extends Bundle()
{
  val wbSel       = UInt(wbSel_w.W)
  val rfWen       = Bool()
  val memMask     = UInt(memMask_w.W)
  val memExt      = UInt(memExt_w.W)
  val cs_val_inst = Bool()

  def init = {
    rfWen := REN_0

    //TODO: following init is useless
    wbSel       := WB_X
    memMask     := MSK_X
    memExt      := EXT_X
    cs_val_inst := false.B
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
