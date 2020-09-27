#ifndef __DIFFTESTIO_H__
#define __DIFFTESTIO_H__

#include "common.h"

class diffTestIO_c
{
public:
  wordLen_t regFile[NUM_REG];
  wordLen_t PC;  //PC must follow regFile continuously in address space
  wordLen_t empty[6];

  bool compareWith(diffTestIO_c* diffTestIO);
  void dump();
};

#endif