package mycore
package writeBack

import chisel3._

import common.configurations._

class memTowbDataIO extends bundle()
{
    val wbData = Input(UInt(XLEN.W))
    val wbAddr = Input(UInt(XLEN.W))
}
