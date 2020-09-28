#include "ram.h"
#include "diffTestIO.h"
#include "verilatorResult.h"
#include "nemuResult.h"

#include <iostream>
#include <stdio.h>
#include <verilated.h>

double sc_time_stamp () {       // Called by $time in Verilog
    return 0;
}

int main(int argc, char** argv)
{
  ram_c* ram = new ram_c(argv[1]);
  diffTestIO_c* verilatorResultIO = new diffTestIO_c();
  diffTestIO_c* nemuResultIO = new diffTestIO_c();
  verilatorResult_c* verilatorResult = new verilatorResult_c(ram);
  nemuResult_c* nemuResult = new nemuResult_c(ram);

  printf("                          **************************************************************\n");
  printf("                          ***************Reset Stage of Verilator RegFile***************\n");
  printf("                          **************************************************************\n");
  verilatorResult->getDiffTestResult(verilatorResultIO);
  verilatorResultIO->dump();

  while (!Verilated::gotFinish()) {
    verilatorResult->step(1);
    verilatorResult->getDiffTestResult(verilatorResultIO);
    nemuResult->step(1);
    nemuResult->getDiffTestResult(nemuResultIO);
    verilatorResultIO->dump();
    
    if (verilatorResultIO->compareWith(nemuResultIO)) {
      printf("                          **************************************************************\n");
      printf("                          ***************Compare Verilator and Nemu Error***************\n");
      printf("                          **************************************************************\n");

      printf("                          ************************Nemu Result is************************\n");
      nemuResultIO->dump();
      break;
    }

#ifdef DEBUG
    if (nemuResult->getCycleCounter() == 10) {
      break;
    }
#endif
  }

  delete ram;
  delete verilatorResultIO;
  delete nemuResultIO;
  delete verilatorResult;
  delete nemuResult;
  return 0;
}
