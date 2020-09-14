package mycore
package execute

import chisel3._
import chisel3.util.MuxCase

import common.configurations._

class alu extends Module
{
  io = IO(new Bundle{
    val aluFunc  = Input(UInt(aluFunc_w.W))
    val op1      = Input(UInt(XLEN.W))
    val op2      = Input(UInt(XLEN.W))

    val out      = Output(UInt(XLEN.W))
    val adderOut = Output(UInt(XLEN.W))
  })

   // ALU
  val adderOut := (io.op1 + io.op2)(XLEN-1,0)
  val shamt     = io.op2(4,0).asUInt()
  out := MuxCase(0.U, Array(
                  (io.aluFunc === ALU_ADD)   -> exe_adder_out,
                  (io.aluFunc === ALU_SUB)   -> (io.op1 - io.op2).asUInt(),
                  (io.aluFunc === ALU_AND)   -> (io.op1 & io.op2).asUInt(),
                  (io.aluFunc === ALU_OR)    -> (io.op1 | io.op2).asUInt(),
                  (io.aluFunc === ALU_XOR)   -> (io.op1 ^ io.op2).asUInt(),
                  (io.aluFunc === ALU_SLT)   -> (io.op1.asSInt() < io.op2.asSInt()).asUInt(),
                  (io.aluFunc === ALU_SLTU)  -> (io.op1 < io.op2).asUInt(),
                  (io.aluFunc === ALU_SLL)   -> ((io.op1 << shamt)(conf.xprlen-1, 0)).asUInt(),
                  (io.aluFunc === ALU_SRA)   -> (io.op1.asSInt() >> shamt).asUInt(),
                  (io.aluFunc === ALU_SRL)   -> (io.op1 >> shamt).asUInt(),
                  (io.aluFunc === ALU_COPY_1)-> io.op1,
                  (io.aluFunc === ALU_COPY_2)-> io.op2
                  ))

}
