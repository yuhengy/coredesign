#ifndef __RAM_H__
#define __RAM_H__

#include "common.h"

class ram_c {
public:
  ram_c(char* imgPath);
  void* getImgStart();
  int getImgSize();

  void InstReadReq(wordLen_t addr, bool en);
  void DataReadReq(wordLen_t addr, bool en);
  wordLen_t InstReadResp();
  wordLen_t DataReadResp();
  void DataWrite(wordLen_t addr, wordLen_t data, bool en, mask_t mask);



private:
  wordLen_t ram[RAMSIZE / sizeof(wordLen_t)];
  int imgSize;
  struct readReqBuffer {
    wordLen_t addr;
    bool en;
  };
  struct readReqBuffer instReadReqBuff, dataReadReqBuff;

  //This is a quick version, for read, it return ram[addr-ADDR_START]
  wordLen_t memRead(wordLen_t addr);
  void memWrite(wordLen_t addr, wordLen_t data);
  //end

};

#endif
