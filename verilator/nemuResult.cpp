#ifndef __NEMURESULT_H__
#define __NEMURESULT_H__

#include "common.h"
#include "ram.h"
#include "diffTestIO.h"
#include "nemuResult.h"

#include <dlfcn.h>

nemuResult_c::nemuResult_c(char* imgPath)
{
  cycleCounter = 0;

  #ifndef NEMU_SO
    #error Please define NEMU_SO to the path of NEMU shared object file
  #endif
  void *handle;
  handle = dlopen(NEMU_SO, RTLD_LAZY | RTLD_DEEPBIND);
  // --------------APT Begin--------------
  refMemcpyFromDUT = (refMemcpyFromDUT_t) dlsym(handle, "difftest_memcpy_from_dut");
  refSetregs = (refSetregs_t) dlsym(handle, "difftest_setregs");
  refGetregs = (refGetregs_t) dlsym(handle, "difftest_getregs");
  refStep = (refStep_t) dlsym(handle, "difftest_exec");
  refInit = (refInit_t) dlsym(handle, "difftest_init");
  // --------------APT End--------------

  refInit();
  wordLen_t* array = new wordLen_t[RAMSIZE / sizeof(wordLen_t)];
  int imgSize = readImage(array, imgPath);
  refMemcpyFromDUT(ADDR_START, array, imgSize);
  delete array;
}

void nemuResult_c::step(int i)
{
  cycleCounter += i;
  refStep(i);
  getDiffTestResult();
}

int nemuResult_c::getCycleCounter()
{
  return cycleCounter;
}

void nemuResult_c::getDiffTestResult()
{
  refGetregs(regFile);  //32 regs and followed &(PC)
  wordLen_t temp = PC;
  PC = commitedPC;
  commitedPC = temp;

}

void nemuResult_c::setDiffTestStatus()
{
  refSetregs(regFile);  //32 regs and followed &(PC)
  commitedPC = PC;
}

#endif
