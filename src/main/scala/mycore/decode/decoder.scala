package mycore
package decode

import scala.language.reflectiveCalls

import chisel3._
import chisel3.util._

import common.constants._
import common.instructions._

//if (DEBUG)
import common.configurations._
//endif

class decoder extends Module
{
  val io = IO(new Bundle{
    val inst      = Input(UInt(WID_INST.W))
    val allCtrlIO = Output(new allCtrlIO)
  })

  // TODO: csrCmd is a foo one for now.
  val csignals = ListLookup(io.inst,
                            List(N, BR_N  , OP1_X  , OP2_X     , OEN_0, OEN_0, ALU_X     , WB_X  , REN_0, MRD_0, MWR_0, MSK_X ,    N, N),
               Array(     /* val  |  BR   |   op1  |    op2    |  R1  |  R2  |    ALU    |  wb   | rf   | mem  | mem  | mask | csr  | fence.i */
                          /* inst | type  |   sel  |    sel    |  oen |  oen |    fcn    |  sel  | wen  |  rd  |  wr  | type | cmd  |         */
                  //LW     -> List(Y, BR_N  , OP1_RS1, OP2_ITYPE , OEN_1, OEN_0, ALU_ADD   , WB_MEM, REN_1, MRD_1, MWR_0, MSK_W,     N, N),
                  //SW     -> List(Y, BR_N  , OP1_RS1, OP2_STYPE , OEN_1, OEN_1, ALU_ADD   , WB_X  , REN_0, MRD_0, MWR_1, MSK_W,     N, N),
                  SB     -> List(Y, BR_N  , OP1_RS1, OP2_STYPE , OEN_1, OEN_1, ALU_ADD   , WB_X  , REN_0, MRD_0, MWR_1, MSK_B,     N, N),
                  LUI    -> List(Y, BR_N  , OP1_X  , OP2_UTYPE , OEN_0, OEN_0, ALU_COPY_2, WB_ALU, REN_1, MRD_0, MWR_0, MSK_X,     N, N),
                  AUIPC  -> List(Y, BR_N  , OP1_PC , OP2_UTYPE , OEN_0, OEN_0, ALU_ADD   , WB_ALU, REN_1, MRD_0, MWR_0, MSK_X,     N, N),
                  JAL    -> List(Y, BR_J  , OP1_X  , OP2_UJTYPE, OEN_0, OEN_0, ALU_COPY_2, WB_PC4, REN_1, MRD_0, MWR_0, MSK_X,     N, N),
                  JALR   -> List(Y, BR_JR , OP1_RS1, OP2_ITYPE , OEN_1, OEN_0, ALU_ADD   , WB_PC4, REN_1, MRD_0, MWR_0, MSK_X,     N, N),
                  BEQ    -> List(Y, BR_EQ , OP1_RS1, OP2_RS2   , OEN_1, OEN_1, ALU_X     , WB_X  , REN_0, MRD_0, MWR_0, MSK_X,     N, N),
                  ADDI   -> List(Y, BR_N  , OP1_RS1, OP2_ITYPE , OEN_1, OEN_0, ALU_ADD   , WB_ALU, REN_1, MRD_0, MWR_0, MSK_X,     N, N),

                  SD     -> List(Y, BR_N  , OP1_RS1, OP2_STYPE , OEN_1, OEN_1, ALU_ADD   , WB_X  , REN_0, MRD_0, MWR_1, MSK_D,     N, N),

                  NEMUHALT->List(Y, BR_N  , OP1_X  , OP2_X     , OEN_0, OEN_0, ALU_X     , WB_X  , REN_0, MRD_0, MWR_0, MSK_X ,    N, N)  //For AMCPUTest
                  ))

  // Put these control signals in variables
  // TODO: all val within () is useless for now
  val (cs_val_inst: Bool) :: temp_brType :: temp_op1Sel :: temp_op2Sel :: (cs_rs1_oen: Bool) :: (cs_rs2_oen: Bool) :: subCsignals = csignals
  val temp_aluFunc :: temp_wbSel :: temp_rfWen :: temp_memRd :: temp_memWr :: temp_memMask :: (cs_csr_cmd: Bool) :: (cs_fencei: Bool) :: Nil = subCsignals

  // TODO: there may be a better solution
  io.allCtrlIO.brType := temp_brType
  io.allCtrlIO.op1Sel := temp_op1Sel
  io.allCtrlIO.op2Sel := temp_op2Sel
  io.allCtrlIO.aluFunc := temp_aluFunc
  io.allCtrlIO.wbSel := temp_wbSel
  io.allCtrlIO.rfWen := temp_rfWen
  io.allCtrlIO.memRd := temp_memRd
  io.allCtrlIO.memWr := temp_memWr
  io.allCtrlIO.memMask := temp_memMask
  io.allCtrlIO.cs_val_inst := cs_val_inst

}