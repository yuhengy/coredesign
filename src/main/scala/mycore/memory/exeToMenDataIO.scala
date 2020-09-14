package mycore
package memory

import chisel3._

import common.configurations._

class exeToMemDataIO extends bundle()
{
    val inst   = Input(UInt(XLEN.W))
    val wbData = Input(UInt(XLEN.W))
    val wbAddr = Input(UInt(XLEN.W))
}
