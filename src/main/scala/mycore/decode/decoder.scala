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
                            List(N, BR_N  , OP1_X  , OP2_X     , OEN_0, OEN_0, ALU_X     , WB_X  , REN_0, MRD_0, MWR_0, MSK_X , EXT_X,CSRWT_U, N),
               Array(     /* val  |  BR   |   op1   |    op2    |  R1  |  R2  |    ALU    |   wb   | rf   | mem  | mem  | mask |extend | csr  | fence.i */
                          /* inst | type  |   sel   |    sel    |  oen |  oen |    fcn    |   sel  | wen  |  rd  |  wr  | type | type  | wen  |         */
                  //LW     -> List(Y, BR_N  , OP1_RS1, OP2_ITYPE , OEN_1, OEN_0, ALU_ADD   , WB_MEM, REN_1, MRD_1, MWR_0, MSK_W, EXT_X ,CSRWT_U, N),
                  //SW     -> List(Y, BR_N  , OP1_RS1, OP2_STYPE , OEN_1, OEN_1, ALU_ADD   , WB_X  , REN_0, MRD_0, MWR_1, MSK_W, EXT_X ,CSRWT_U, N),

                  // RV32
                  LB     -> List(Y, BR_N  , OP1_RS1 , OP2_ITYPE , OEN_1, OEN_0, ALU_ADD   , WB_MEM , REN_1, MRD_1, MWR_0, MSK_X, EXT_BS,CSRWT_U, N),
                  LH     -> List(Y, BR_N  , OP1_RS1 , OP2_ITYPE , OEN_1, OEN_0, ALU_ADD   , WB_MEM , REN_1, MRD_1, MWR_0, MSK_X, EXT_HS,CSRWT_U, N),
                  LW     -> List(Y, BR_N  , OP1_RS1 , OP2_ITYPE , OEN_1, OEN_0, ALU_ADD   , WB_MEM , REN_1, MRD_1, MWR_0, MSK_X, EXT_WS,CSRWT_U, N),
                  LBU    -> List(Y, BR_N  , OP1_RS1 , OP2_ITYPE , OEN_1, OEN_0, ALU_ADD   , WB_MEM , REN_1, MRD_1, MWR_0, MSK_X, EXT_BU,CSRWT_U, N),
                  LHU    -> List(Y, BR_N  , OP1_RS1 , OP2_ITYPE , OEN_1, OEN_0, ALU_ADD   , WB_MEM , REN_1, MRD_1, MWR_0, MSK_X, EXT_HU,CSRWT_U, N),
                  LWU    -> List(Y, BR_N  , OP1_RS1 , OP2_ITYPE , OEN_1, OEN_0, ALU_ADD   , WB_MEM , REN_1, MRD_1, MWR_0, MSK_X, EXT_WU,CSRWT_U, N),
                  SB     -> List(Y, BR_N  , OP1_RS1 , OP2_STYPE , OEN_1, OEN_1, ALU_ADD   , WB_X   , REN_0, MRD_0, MWR_1, MSK_B, EXT_X ,CSRWT_U, N),
                  SH     -> List(Y, BR_N  , OP1_RS1 , OP2_STYPE , OEN_1, OEN_1, ALU_ADD   , WB_X   , REN_0, MRD_0, MWR_1, MSK_H, EXT_X ,CSRWT_U, N),
                  SW     -> List(Y, BR_N  , OP1_RS1 , OP2_STYPE , OEN_1, OEN_1, ALU_ADD   , WB_X   , REN_0, MRD_0, MWR_1, MSK_W, EXT_X ,CSRWT_U, N),
                  LUI    -> List(Y, BR_N  , OP1_X   , OP2_UTYPE , OEN_0, OEN_0, ALU_COPY_2, WB_ALU , REN_1, MRD_0, MWR_0, MSK_X, EXT_X ,CSRWT_U, N),
                  AUIPC  -> List(Y, BR_N  , OP1_PC  , OP2_UTYPE , OEN_0, OEN_0, ALU_ADD   , WB_ALU , REN_1, MRD_0, MWR_0, MSK_X, EXT_X ,CSRWT_U, N),

                  JAL    -> List(Y, BR_J  , OP1_X   , OP2_UJTYPE, OEN_0, OEN_0, ALU_COPY_2, WB_PC4 , REN_1, MRD_0, MWR_0, MSK_X, EXT_X ,CSRWT_U, N),
                  JALR   -> List(Y, BR_JR , OP1_RS1 , OP2_ITYPE , OEN_1, OEN_0, ALU_ADD   , WB_PC4 , REN_1, MRD_0, MWR_0, MSK_X, EXT_X ,CSRWT_U, N),
                  BEQ    -> List(Y, BR_EQ , OP1_RS1 , OP2_SBTYPE, OEN_1, OEN_1, ALU_X     , WB_X   , REN_0, MRD_0, MWR_0, MSK_X, EXT_X ,CSRWT_U, N),
                  BNE    -> List(Y, BR_NE , OP1_RS1 , OP2_SBTYPE, OEN_1, OEN_1, ALU_X     , WB_X   , REN_0, MRD_0, MWR_0, MSK_X, EXT_X ,CSRWT_U, N),
                  BLT    -> List(Y, BR_LT , OP1_RS1 , OP2_SBTYPE, OEN_1, OEN_1, ALU_X     , WB_X   , REN_0, MRD_0, MWR_0, MSK_X, EXT_X ,CSRWT_U, N),
                  BGE    -> List(Y, BR_GE , OP1_RS1 , OP2_SBTYPE, OEN_1, OEN_1, ALU_X     , WB_X   , REN_0, MRD_0, MWR_0, MSK_X, EXT_X ,CSRWT_U, N),
                  BLTU   -> List(Y, BR_LTU, OP1_RS1 , OP2_SBTYPE, OEN_1, OEN_1, ALU_X     , WB_X   , REN_0, MRD_0, MWR_0, MSK_X, EXT_X ,CSRWT_U, N),
                  BGEU   -> List(Y, BR_GEU, OP1_RS1 , OP2_SBTYPE, OEN_1, OEN_1, ALU_X     , WB_X   , REN_0, MRD_0, MWR_0, MSK_X, EXT_X ,CSRWT_U, N),

                  ADDI   -> List(Y, BR_N  , OP1_RS1 , OP2_ITYPE , OEN_1, OEN_0, ALU_ADD   , WB_ALU , REN_1, MRD_0, MWR_0, MSK_X, EXT_X ,CSRWT_U, N),
                  SLTI   -> List(Y, BR_N  , OP1_RS1 , OP2_ITYPE , OEN_1, OEN_0, ALU_SLT   , WB_ALU , REN_1, MRD_0, MWR_0, MSK_X, EXT_X ,CSRWT_U, N),
                  SLTIU  -> List(Y, BR_N  , OP1_RS1 , OP2_ITYPE , OEN_1, OEN_0, ALU_SLTU  , WB_ALU , REN_1, MRD_0, MWR_0, MSK_X, EXT_X ,CSRWT_U, N),
                  XORI   -> List(Y, BR_N  , OP1_RS1 , OP2_ITYPE , OEN_1, OEN_0, ALU_XOR   , WB_ALU , REN_1, MRD_0, MWR_0, MSK_X, EXT_X ,CSRWT_U, N),
                  ORI    -> List(Y, BR_N  , OP1_RS1 , OP2_ITYPE , OEN_1, OEN_0, ALU_OR    , WB_ALU , REN_1, MRD_0, MWR_0, MSK_X, EXT_X ,CSRWT_U, N),
                  ANDI   -> List(Y, BR_N  , OP1_RS1 , OP2_ITYPE , OEN_1, OEN_0, ALU_AND   , WB_ALU , REN_1, MRD_0, MWR_0, MSK_X, EXT_X ,CSRWT_U, N),
                  ADD    -> List(Y, BR_N  , OP1_RS1 , OP2_RS2   , OEN_1, OEN_1, ALU_ADD   , WB_ALU , REN_1, MRD_0, MWR_0, MSK_X, EXT_X ,CSRWT_U, N),
                  SUB    -> List(Y, BR_N  , OP1_RS1 , OP2_RS2   , OEN_1, OEN_1, ALU_SUB   , WB_ALU , REN_1, MRD_0, MWR_0, MSK_X, EXT_X ,CSRWT_U, N),
                  SLL    -> List(Y, BR_N  , OP1_RS1 , OP2_RS2   , OEN_1, OEN_1, ALU_SLL   , WB_ALU , REN_1, MRD_0, MWR_0, MSK_X, EXT_X ,CSRWT_U, N),
                  SLT    -> List(Y, BR_N  , OP1_RS1 , OP2_RS2   , OEN_1, OEN_1, ALU_SLT   , WB_ALU , REN_1, MRD_0, MWR_0, MSK_X, EXT_X ,CSRWT_U, N),
                  SLTU   -> List(Y, BR_N  , OP1_RS1 , OP2_RS2   , OEN_1, OEN_1, ALU_SLTU  , WB_ALU , REN_1, MRD_0, MWR_0, MSK_X, EXT_X ,CSRWT_U, N),
                  XOR    -> List(Y, BR_N  , OP1_RS1 , OP2_RS2   , OEN_1, OEN_1, ALU_XOR   , WB_ALU , REN_1, MRD_0, MWR_0, MSK_X, EXT_X ,CSRWT_U, N),
                  SRL    -> List(Y, BR_N  , OP1_RS1 , OP2_RS2   , OEN_1, OEN_1, ALU_SRL   , WB_ALU , REN_1, MRD_0, MWR_0, MSK_X, EXT_X ,CSRWT_U, N),
                  SRA    -> List(Y, BR_N  , OP1_RS1 , OP2_RS2   , OEN_1, OEN_1, ALU_SRA   , WB_ALU , REN_1, MRD_0, MWR_0, MSK_X, EXT_X ,CSRWT_U, N),
                  OR     -> List(Y, BR_N  , OP1_RS1 , OP2_RS2   , OEN_1, OEN_1, ALU_OR    , WB_ALU , REN_1, MRD_0, MWR_0, MSK_X, EXT_X ,CSRWT_U, N),
                  AND    -> List(Y, BR_N  , OP1_RS1 , OP2_RS2   , OEN_1, OEN_1, ALU_AND   , WB_ALU , REN_1, MRD_0, MWR_0, MSK_X, EXT_X ,CSRWT_U, N),
                  FENCE  -> List(Y, BR_N  , OP1_X   , OP2_X     , OEN_0, OEN_0, ALU_X     , WB_X   , REN_0, MRD_0, MWR_0, MSK_X, EXT_X ,CSRWT_U, N),

                  // Zicsr
                  CSRRW  -> List(Y, BR_N  , OP1_RS1 , OP2_ITYPE , OEN_1, OEN_0, ALU_X     , WB_CSR , REN_1, MRD_0, MWR_0, MSK_X, EXT_X ,CSRWT_W, N),
                  CSRRS  -> List(Y, BR_N  , OP1_RS1 , OP2_ITYPE , OEN_1, OEN_0, ALU_X     , WB_CSR , REN_1, MRD_0, MWR_0, MSK_X, EXT_X ,CSRWT_S, N),
                  CSRRC  -> List(Y, BR_N  , OP1_RS1 , OP2_ITYPE , OEN_1, OEN_0, ALU_X     , WB_CSR , REN_1, MRD_0, MWR_0, MSK_X, EXT_X ,CSRWT_C, N),
                  CSRRWI -> List(Y, BR_N  , OP1_IMZ , OP2_ITYPE , OEN_0, OEN_0, ALU_X     , WB_CSR , REN_1, MRD_0, MWR_0, MSK_X, EXT_X ,CSRWT_W, N),
                  CSRRSI -> List(Y, BR_N  , OP1_IMZ , OP2_ITYPE , OEN_0, OEN_0, ALU_X     , WB_CSR , REN_1, MRD_0, MWR_0, MSK_X, EXT_X ,CSRWT_S, N),
                  CSRRCI -> List(Y, BR_N  , OP1_IMZ , OP2_ITYPE , OEN_0, OEN_0, ALU_X     , WB_CSR , REN_1, MRD_0, MWR_0, MSK_X, EXT_X ,CSRWT_C, N),

                  // RV64
                  LD     -> List(Y, BR_N  , OP1_RS1 , OP2_ITYPE , OEN_1, OEN_0, ALU_ADD   , WB_MEM , REN_1, MRD_1, MWR_0, MSK_X, EXT_D ,CSRWT_U, N),
                  SD     -> List(Y, BR_N  , OP1_RS1 , OP2_STYPE , OEN_1, OEN_1, ALU_ADD   , WB_X   , REN_0, MRD_0, MWR_1, MSK_D, EXT_X ,CSRWT_U, N),
                  SLLI   -> List(Y, BR_N  , OP1_RS1 , OP2_ITYPE , OEN_1, OEN_0, ALU_SLL   , WB_ALU , REN_1, MRD_0, MWR_0, MSK_X, EXT_X ,CSRWT_U, N),
                  SRLI   -> List(Y, BR_N  , OP1_RS1 , OP2_ITYPE , OEN_1, OEN_0, ALU_SRL   , WB_ALU , REN_1, MRD_0, MWR_0, MSK_X, EXT_X ,CSRWT_U, N),
                  SRAI   -> List(Y, BR_N  , OP1_RS1 , OP2_ITYPE , OEN_1, OEN_0, ALU_SRA   , WB_ALU , REN_1, MRD_0, MWR_0, MSK_X, EXT_X ,CSRWT_U, N),
                  ADDIW  -> List(Y, BR_N  , OP1_RS1W, OP2_ITYPE , OEN_1, OEN_0, ALU_ADD   , WB_ALUW, REN_1, MRD_0, MWR_0, MSK_X, EXT_X ,CSRWT_U, N), 
                  //TODO check whether have overflow expection problem
                  SLLIW  -> List(Y, BR_N  , OP1_RS1W, OP2_ITYPE , OEN_1, OEN_0, ALU_SLLW  , WB_ALUW, REN_1, MRD_0, MWR_0, MSK_X, EXT_X ,CSRWT_U, N),
                  SRLIW  -> List(Y, BR_N  , OP1_RS1W, OP2_ITYPE , OEN_1, OEN_0, ALU_SRLW  , WB_ALUW, REN_1, MRD_0, MWR_0, MSK_X, EXT_X ,CSRWT_U, N),
                  SRAIW  -> List(Y, BR_N  , OP1_RS1W, OP2_ITYPE , OEN_1, OEN_0, ALU_SRAW  , WB_ALUW, REN_1, MRD_0, MWR_0, MSK_X, EXT_X ,CSRWT_U, N),
                  ADDW   -> List(Y, BR_N  , OP1_RS1W, OP2_RS2W  , OEN_1, OEN_1, ALU_ADD   , WB_ALUW, REN_1, MRD_0, MWR_0, MSK_X, EXT_X ,CSRWT_U, N),
                  SUBW   -> List(Y, BR_N  , OP1_RS1W, OP2_RS2W  , OEN_1, OEN_1, ALU_SUB   , WB_ALUW, REN_1, MRD_0, MWR_0, MSK_X, EXT_X ,CSRWT_U, N),
                  SLLW   -> List(Y, BR_N  , OP1_RS1W, OP2_RS2   , OEN_1, OEN_1, ALU_SLLW  , WB_ALUW, REN_1, MRD_0, MWR_0, MSK_X, EXT_X ,CSRWT_U, N),
                  SRLW   -> List(Y, BR_N  , OP1_RS1W, OP2_RS2   , OEN_1, OEN_1, ALU_SRLW  , WB_ALUW, REN_1, MRD_0, MWR_0, MSK_X, EXT_X ,CSRWT_U, N),
                  SRAW   -> List(Y, BR_N  , OP1_RS1W, OP2_RS2   , OEN_1, OEN_1, ALU_SRAW  , WB_ALUW, REN_1, MRD_0, MWR_0, MSK_X, EXT_X ,CSRWT_U, N),

                  // Machine
                  MRET   -> List(Y, BR_N  , OP1_X   , OP2_X     , OEN_0, OEN_0, ALU_X     , WB_X   , REN_0, MRD_0, MWR_0, MSK_X, EXT_X ,CSRWT_IMP, N),

                  // if (DEBUG)
                  NEMUHALT->List(Y, BR_N  , OP1_X   , OP2_X     , OEN_0, OEN_0, ALU_X     , WB_X   , REN_0, MRD_0, MWR_0, MSK_X, EXT_X ,CSRWT_U, N),  //For AMCPUTest
                  ECALL   ->List(Y, BR_N  , OP1_X   , OP2_X     , OEN_0, OEN_0, ALU_X     , WB_X   , REN_0, MRD_0, MWR_0, MSK_X, EXT_X ,CSRWT_U, N)  //For officialTest
                  ))

  // Put these control signals in variables
  // TODO: all val within () is useless for now
  val (cs_val_inst: Bool) :: temp_brType :: temp_op1Sel :: temp_op2Sel :: (cs_rs1_oen: Bool) :: (cs_rs2_oen: Bool) :: subCsignals = csignals
  val temp_aluFunc :: temp_wbSel :: temp_rfWen :: temp_memRd :: temp_memWr :: temp_memMask :: temp_memExt :: temp_CSRWriteType :: (cs_fencei: Bool) :: Nil = subCsignals

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
  io.allCtrlIO.memExt  := temp_memExt
  io.allCtrlIO.cs_val_inst := cs_val_inst
  io.allCtrlIO.cs_rs1_oen  := cs_rs1_oen
  io.allCtrlIO.cs_rs2_oen  := cs_rs2_oen
  io.allCtrlIO.CSRWriteType := temp_CSRWriteType
  io.allCtrlIO.exception := io.inst === MRET
  if (DEBUG) { 
    io.allCtrlIO.goodTrapNemu := io.inst === NEMUHALT | io.inst === ECALL
  }
}