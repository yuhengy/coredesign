#ifndef __COMMON_H__
#define __COMMON_H__

#define DEBUG
#define __RV64__

typedef unsigned int uint32_t;
typedef unsigned long int uint64_t;

#ifdef __RV32__
typedef uint32_t wordLen_t;

#elif defined __RV64__
typedef uint64_t wordLen_t;

#endif

typedef uint32_t mask_t;

#define RAMSIZE (128 * 1024 * 1024)

#include "configurations.h"
#include "constants.h"


#endif
