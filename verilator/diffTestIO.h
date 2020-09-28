#ifndef __DIFFTESTIO_H__
#define __DIFFTESTIO_H__

#include "common.h"

class diffTestIO_c
{
public:
  wordLen_t regFile[NUM_REG];

  // PC must follow regFile continuously in address space
  //
  // This PC is the commited PC, which means
  // In the current cycle, regFile has already been updated by the commited PC
  // while, The PC register is the new PC.
  //
  // Tricky part is:
  //      1. For Chisel, we want to see the commited PC and the regFile updated by commited PC
  //  while, for nemu, it provides the new PC and the regFile updated by commited PC
  //      2. For Chisel, the first step(1) after reset output reseted regFile
  //  while, for nemu, the first step(1) after reset output regFile after first instruction
  //
  // For example, after the first step(1) after reset, 
  //        Chisel provides commited PC = 0x00000000 and reseted regFile
  //              after the second step(1),
  //        Chisel provides commited PC = 0x80000000 and regFile updated by PC = 0x80000000
  //       while, after the first step(1) after reset, 
  //        nemu provides new PC = 0x80000004(if no jump) and regFile updated by PC = 0x80000000
  //
  // To solve this, we save the commited PC for nemu and update this PC into diffTestTO.PC
  wordLen_t PC;  

  wordLen_t empty[6];

  bool compareWith(diffTestIO_c* diffTestIO);
  void dump();
};

#endif