package mycore
package execute

import scala.language.reflectiveCalls

import chisel3._
import chisel3.util._

import common.configurations._
import common.constants._

class alu extends Module
{
  val io = IO(new Bundle{
    val aluFunc  = Input(UInt(aluFunc_w.W))
    val op1      = Input(UInt(XLEN.W))
    val op2      = Input(UInt(XLEN.W))

    val out      = Output(UInt(XLEN.W))
    val adderOut = Output(UInt(XLEN.W))
  })

   // ALU
  val adderOut = (io.op1 + io.op2)(XLEN-1,0)
  val shamt    = io.op2(5,0).asUInt()
  io.out      := MuxCase(0.U, Array(
                  (io.aluFunc === ALU_ADD)   -> adderOut,
                  (io.aluFunc === ALU_SUB)   -> (io.op1 - io.op2).asUInt(),
                  (io.aluFunc === ALU_AND)   -> (io.op1 & io.op2).asUInt(),
                  (io.aluFunc === ALU_OR)    -> (io.op1 | io.op2).asUInt(),
                  (io.aluFunc === ALU_XOR)   -> (io.op1 ^ io.op2).asUInt(),
                  (io.aluFunc === ALU_SLT)   -> (io.op1.asSInt() < io.op2.asSInt()).asUInt(),
                  (io.aluFunc === ALU_SLTU)  -> (io.op1 < io.op2).asUInt(),
                  (io.aluFunc === ALU_SLL)   -> ((io.op1 << shamt)(XLEN-1, 0)).asUInt(),
                  (io.aluFunc === ALU_SRA)   -> (io.op1.asSInt() >> shamt).asUInt(),
                  (io.aluFunc === ALU_SRAW)  -> Cat(Fill(32, 0.U), (io.op1(31,0).asSInt() >> shamt).asUInt()),
                  (io.aluFunc === ALU_SRL)   -> (io.op1 >> shamt).asUInt(),
                  (io.aluFunc === ALU_COPY_1)-> io.op1,
                  (io.aluFunc === ALU_COPY_2)-> io.op2
                  ))
  io.adderOut := adderOut

}
