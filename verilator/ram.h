#ifndef __RAM_H__
#define __RAM_H__

#include "common.h"

class ram_c {
public:
  ram_c(char* imgPath);
  void* getImgStart();
  int getImgSize();


private:
  wordLen_t ram[RAMSIZE / sizeof(wordLen_t)];
  int imgSize;

};

#endif
