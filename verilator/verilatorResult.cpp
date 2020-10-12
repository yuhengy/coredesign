#include "diffTestIO.h"
#include "VverilatorTOP.h"  //TODO name of this file should be defined in makefile
#include "verilated_vcd_c.h"
#include "verilatorResult.h"

verilatorResult_c::verilatorResult_c(ram_c* inputRam, long* inputSc_time)
{
  ram = inputRam;
  sc_time = inputSc_time;
  cycleCounter = 0;
  
  verilatorTOP = new VverilatorTOP;

  if(vcdTrace)
  {
    Verilated::traceEverOn(true);

    tfp = new VerilatedVcdC;
    verilatorTOP->trace(tfp, 99);

    tfp->open("build/generated-cpp/myVCD.vcd");
  }


  verilatorTOP->eval();
  verilatorTOP->eval();
  verilatorTOP->clock = 0;
  verilatorTOP->reset = 1;
  for (int i = 0; i < 9; i++) {
    verilatorTOP->clock = verilatorTOP->clock ? 0 : 1;
    verilatorTOP->eval();
    cycleCounter++; (*sc_time)++; if(vcdTrace) { tfp->dump((double)*sc_time); }
  }
  verilatorTOP->clock = 0;
  verilatorTOP->reset = 0;
  verilatorTOP->eval();
  cycleCounter++; (*sc_time)++; if(vcdTrace) { tfp->dump((double)*sc_time); }

  step(1);
  getDiffTestResult();
}

void verilatorResult_c::step(int i)
{
  for (; i > 0; i--) {
    while (!verilatorTOP->io_diffTestIO_commit) {
      verilatorTOP->clock = 1;
      verilatorTOP->eval();
      evalRam();
      verilatorTOP->eval();  //TODO: Why this eval can be will be done implicitly?
      cycleCounter++; (*sc_time)++; if(vcdTrace) { tfp->dump((double)*sc_time); }
      
      verilatorTOP->clock = 0;
      verilatorTOP->eval();
      cycleCounter++; (*sc_time)++; if(vcdTrace) { tfp->dump((double)*sc_time); }
    }

    verilatorTOP->clock = 1;
    verilatorTOP->eval();
    evalRam();
    verilatorTOP->eval();
    cycleCounter++; (*sc_time)++; if(vcdTrace) { tfp->dump((double)*sc_time); }
      
    verilatorTOP->clock = 0;
    verilatorTOP->eval();
    cycleCounter++; (*sc_time)++; if(vcdTrace) { tfp->dump((double)*sc_time); }
  }
  getDiffTestResult();
}

int verilatorResult_c::getCycleCounter()
{
  return cycleCounter;
}

void verilatorResult_c::getDiffTestResult()
{
  regFile[ 0] = verilatorTOP->io_diffTestIO_regFile_0;
  regFile[ 1] = verilatorTOP->io_diffTestIO_regFile_1;
  regFile[ 2] = verilatorTOP->io_diffTestIO_regFile_2;
  regFile[ 3] = verilatorTOP->io_diffTestIO_regFile_3;
  regFile[ 4] = verilatorTOP->io_diffTestIO_regFile_4;
  regFile[ 5] = verilatorTOP->io_diffTestIO_regFile_5;
  regFile[ 6] = verilatorTOP->io_diffTestIO_regFile_6;
  regFile[ 7] = verilatorTOP->io_diffTestIO_regFile_7;
  regFile[ 8] = verilatorTOP->io_diffTestIO_regFile_8;
  regFile[ 9] = verilatorTOP->io_diffTestIO_regFile_9;
  regFile[10] = verilatorTOP->io_diffTestIO_regFile_10;
  regFile[11] = verilatorTOP->io_diffTestIO_regFile_11;
  regFile[12] = verilatorTOP->io_diffTestIO_regFile_12;
  regFile[13] = verilatorTOP->io_diffTestIO_regFile_13;
  regFile[14] = verilatorTOP->io_diffTestIO_regFile_14;
  regFile[15] = verilatorTOP->io_diffTestIO_regFile_15;
  regFile[16] = verilatorTOP->io_diffTestIO_regFile_16;
  regFile[17] = verilatorTOP->io_diffTestIO_regFile_17;
  regFile[18] = verilatorTOP->io_diffTestIO_regFile_18;
  regFile[19] = verilatorTOP->io_diffTestIO_regFile_19;
  regFile[20] = verilatorTOP->io_diffTestIO_regFile_20;
  regFile[21] = verilatorTOP->io_diffTestIO_regFile_21;
  regFile[22] = verilatorTOP->io_diffTestIO_regFile_22;
  regFile[23] = verilatorTOP->io_diffTestIO_regFile_23;
  regFile[24] = verilatorTOP->io_diffTestIO_regFile_24;
  regFile[25] = verilatorTOP->io_diffTestIO_regFile_25;
  regFile[26] = verilatorTOP->io_diffTestIO_regFile_26;
  regFile[27] = verilatorTOP->io_diffTestIO_regFile_27;
  regFile[28] = verilatorTOP->io_diffTestIO_regFile_28;
  regFile[29] = verilatorTOP->io_diffTestIO_regFile_29;
  regFile[30] = verilatorTOP->io_diffTestIO_regFile_30;
  regFile[31] = verilatorTOP->io_diffTestIO_regFile_31;
  PC = verilatorTOP->io_diffTestIO_PC;
}

verilatorResult_c::~verilatorResult_c()
{
  tfp->close();
  verilatorTOP->final();
  delete verilatorTOP;
}

void verilatorResult_c::evalRam()
{
  verilatorTOP->io_mycoreTOPIO_instReadIO_data = ram->InstReadResp();
  verilatorTOP->io_mycoreTOPIO_dataReadIO_data = ram->DataReadResp();

  ram->InstReadReq(verilatorTOP->io_mycoreTOPIO_instReadIO_addr, verilatorTOP->io_mycoreTOPIO_instReadIO_en);
  ram->DataReadReq(verilatorTOP->io_mycoreTOPIO_dataReadIO_addr, verilatorTOP->io_mycoreTOPIO_dataReadIO_en);
  ram->DataWrite(verilatorTOP->io_mycoreTOPIO_dataWriteIO_addr, 
                 verilatorTOP->io_mycoreTOPIO_dataWriteIO_data, 
                 verilatorTOP->io_mycoreTOPIO_dataWriteIO_en, 
                 verilatorTOP->io_mycoreTOPIO_dataWriteIO_mask);

}

bool verilatorResult_c::hitGoodTrap()
{
  if (verilatorTOP->io_goodTrapIO_nemu == true) {
    return true;
  }
  return false;
}




