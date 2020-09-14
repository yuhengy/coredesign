package mycore
package writeBack

import chisel3._

import common.configurations._

class memToWbDataIO extends Bundle()
{
    val wbData = UInt(XLEN.W)
    val wbAddr = UInt(XLEN.W)
}
