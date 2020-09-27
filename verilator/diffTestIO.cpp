#include "common.h"
#include "diffTestIO.h"

#include "stdio.h"

char const *regName[] = {
  " $0", " ra", " sp", " gp", " tp", " t0", " t1", " t2",
  " s0", " s1", " a0", " a1", " a2", " a3", " a4", " a5",
  " a6", " a7", " s2", " s3", " s4", " s5", " s6", " s7",
  " s8", " s9", "s10", "s11", " t3", " t4", " t5", " t6"
};

bool diffTestIO_c::compareWith(diffTestIO_c* diffTestIO)
{
  for (int i = 0; i < NUM_REG; i++) {
    if (regFile[i] != diffTestIO->regFile[i]) {
      return true;
    }
  }

  if (PC != diffTestIO->PC) {
    return true;
  }

  return false;
}

void diffTestIO_c::dump()
{
  printf("                  ---------------------------> PC = 0x%016lx <---------------------------\n", PC);
  for (int i = 0; i < NUM_REG; i += 4) {
    printf("%s : 0x%016lx\t", regName[i+0], regFile[i+0]);
    printf("%s : 0x%016lx\t", regName[i+1], regFile[i+1]);
    printf("%s : 0x%016lx\t", regName[i+2], regFile[i+2]);
    printf("%s : 0x%016lx\n", regName[i+3], regFile[i+3]);
  }
}
