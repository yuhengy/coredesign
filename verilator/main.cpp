#include "ram.h"
#include "diffTestIO.h"
#include "verilatorResult.h"
#include "nemuResult.h"

#include <iostream>
#include <verilated.h>

int main(int argc, char** argv)
{
  ram_c* ram = new ram_c(argv[1]);
  diffTestIO_t verilatorResultIO;
  diffTestIO_t nemuResultIO;
  verilatorResult_c* verilatorResult = new verilatorResult_c();
  nemuResult_c* nemuResult = new nemuResult_c(ram);

  while (!Verilated::gotFinish()) {
#ifdef DEBUG
    verilatorResult->getDiffTestResult(&verilatorResultIO);
    nemuResult->getDiffTestResult(&nemuResultIO);
    std::cout << "verilatorCycle " << verilatorResult->getCycleCounter() 
    << ": reg[5] = " << (void*)verilatorResultIO.regFile[5] << std::endl;
    std::cout << "nemuCycle " << nemuResult->getCycleCounter() 
    << ": reg[5] = " << (void*)nemuResultIO.regFile[5] << std::endl;
#endif

    verilatorResult->step(1);
    verilatorResult->getDiffTestResult(&verilatorResultIO);
    nemuResult->step(1);
    nemuResult->getDiffTestResult(&nemuResultIO);

#ifdef DEBUG
    std::cout << "verilatorCycle " << verilatorResult->getCycleCounter() 
    << ": reg[5] = " << (void*)verilatorResultIO.regFile[5] << std::endl;
    std::cout << "nemuCycle " << nemuResult->getCycleCounter() 
    << ": reg[5] = " << (void*)nemuResultIO.regFile[5] << std::endl;

    if (verilatorResult->getCycleCounter() == 10) {
      break;
    }
#endif
  }

  delete ram;
  delete verilatorResult;
  delete nemuResult;
  return 0;
}
