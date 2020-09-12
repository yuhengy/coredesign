#ifndef __RAM_H__
#define __RAM_H__

#include "common.h"

class ram_c {
public:
  ram_c(char* imgPath);
  void* getImgStart();
  int getImgSize();

  wordLen_t InstRead(wordLen_t addr, bool en);
  wordLen_t DataRead(wordLen_t addr, bool en);
  void DataWrite(wordLen_t addr, wordLen_t data, bool en, mask_t mask);



private:
  wordLen_t ram[RAMSIZE / sizeof(wordLen_t)];
  int imgSize;

  //This is a quick version, for read, it return ram[addr-ADDR_START]
  wordLen_t memRead(wordLen_t addr);
  void memWrite(wordLen_t addr, wordLen_t data);
  //end

};

#endif
