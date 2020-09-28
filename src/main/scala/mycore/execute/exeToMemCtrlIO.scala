package mycore
package execute

import chisel3._

import common.constants._

class exeToMemCtrlIO extends Bundle()
{
  val wbSel       = UInt(wbSel_w.W)
  val rfWen       = Bool()
  val cs_val_inst = Bool()

  def init = {
    rfWen := REN_0

    //TODO: following init is useless
    wbSel       := WB_X
    cs_val_inst := false.B
  }
}
