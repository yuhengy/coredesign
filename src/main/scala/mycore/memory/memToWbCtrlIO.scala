package mycore
package memory

import chisel3._

import common.configurations._
import common.constants._

class memToWbCtrlIO extends Bundle()
{
  val wbSel       = UInt(wbSel_w.W)
  val rfWen       = Bool()
  val cs_val_inst = Bool()
  val CSRWriteType = UInt(CSRWriteType_w.W)
  //if (DEBUG) {
    val goodTrapNemu = Bool()
  //}

  def init = {
    rfWen := REN_0
    rfWen := REN_0
    cs_val_inst := false.B
    CSRWriteType := CSRWT_U

    //TODO: following init is useless
    wbSel := WB_X

    if (DEBUG) {
      goodTrapNemu := false.B
    }
  }
}

object memToWbCtrlIO
{
  val init = {
    val temp = Wire(new memToWbCtrlIO)
    temp.init
    temp
  }
}
