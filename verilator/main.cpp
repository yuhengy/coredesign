#include "ram.h"
#include "diffTestIO.h"
#include "verilatorResult.h"
#include "nemuResult.h"

#include <iostream>
#include <verilated.h>

double sc_time_stamp () {       // Called by $time in Verilog
    return 0;
}

int main(int argc, char** argv)
{
  ram_c* ram = new ram_c(argv[1]);
  diffTestIO_t verilatorResultIO;
  diffTestIO_t nemuResultIO;
  verilatorResult_c* verilatorResult = new verilatorResult_c(ram);
  nemuResult_c* nemuResult = new nemuResult_c(ram);

  while (!Verilated::gotFinish()) {
#ifdef DEBUG
    verilatorResult->getDiffTestResult(&verilatorResultIO);
    nemuResult->getDiffTestResult(&nemuResultIO);
    std::cout << "verilatorCycle " << verilatorResult->getCycleCounter()
    << ": PC=" << (void*)verilatorResultIO.PC
    << "---reg[5] = " << (void*)verilatorResultIO.regFile[5] << std::endl;
    std::cout << "nemuCycle " << nemuResult->getCycleCounter()
    << ": PC=" << (void*)nemuResultIO.PC
    << "---reg[5] = " << (void*)nemuResultIO.regFile[5] << std::endl;
#endif

//#ifdef DEBUG
//#else
    verilatorResult->step(1);
    verilatorResult->getDiffTestResult(&verilatorResultIO);
//#endif
    nemuResult->step(1);
    nemuResult->getDiffTestResult(&nemuResultIO);

#ifdef DEBUG
    if (nemuResult->getCycleCounter() == 100) {
      break;
    }
#endif
  }

  delete ram;
  delete verilatorResult;
  delete nemuResult;
  return 0;
}
