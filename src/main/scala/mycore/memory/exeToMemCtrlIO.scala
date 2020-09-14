package mycore
package memory

import chisel3._

import common.constants._

class exeToMemCtrlIO extends bundle()
{
  val wbSel   = Input(UInt(wbSel_w.W))
  val rfWen   = Input(Bool())
}
