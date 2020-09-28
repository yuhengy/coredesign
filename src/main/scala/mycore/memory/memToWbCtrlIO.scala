package mycore
package memory

import chisel3._

import common.constants._

class memToWbCtrlIO extends Bundle()
{
  val rfWen       = Bool()
  val cs_val_inst = Bool()

  def init = {
    rfWen := REN_0
    rfWen := REN_0

    //TODO: following init is useless
    cs_val_inst := false.B
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
