#ifndef __DIFFTESTIO_H__
#define __DIFFTESTIO_H__

#include "common.h"

struct diffTestIO_t
{
  wordLen_t regFile[NUM_REG];
  wordLen_t PC;  //PC must follow regFile continuously in address space
  wordLen_t empty[6];
};

#endif