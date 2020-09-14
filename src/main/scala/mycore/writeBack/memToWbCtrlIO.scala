package mycore
package writeBack

import chisel3._

class memToWbCtrlIO extends bundle()
{
  val rfWen   = Input(Bool())

  def init = {
    rfWen := REN_0
  }
}
