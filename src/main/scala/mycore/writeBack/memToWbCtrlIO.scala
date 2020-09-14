package mycore
package writeBack

import chisel3._

import common.constants._

class memToWbCtrlIO extends Bundle()
{
  val rfWen   = Bool()

  def init = {
    rfWen := REN_0
  }
}
