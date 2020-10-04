#include "ram.h"

#include <stdio.h>
#include <assert.h>

ram_c::ram_c(char* imgPath)
{
//#ifdef DEBUG
//  imgSize = 3;
//  ram[0] = 0x800002b7;  // lui t0,0x80000
//  //ram[1] = 0x0002a023;  // sw  zero,0(t0)
//  //ram[2] = 0x0002a503;  // lw  a0,0(t0)

//#else
  assert(imgPath && "No image file.");
  FILE *fp = fopen(imgPath, "rb");
  assert(fp && "Can not open image file.");

  fseek(fp, 0, SEEK_END);
  imgSize = ftell(fp);

  fseek(fp, 0, SEEK_SET);
  int ret = fread(ram, imgSize, 1, fp);
  assert(ret == 1 && "Can not read image file.");
  fclose(fp);

//#ifdef DEBUG
//  printf("RAM: %x\n", (unsigned int)ram[0]);
//#endif

//#endif
}

void* ram_c::getImgStart()
{
  return ram;
}

int ram_c::getImgSize()
{
  return imgSize;
}


void ram_c::InstReadReq(wordLen_t addr, bool en)
{
  instReadReqBuff.en   = en;
  instReadReqBuff.addr = addr;
}

void ram_c::DataReadReq(wordLen_t addr, bool en)
{
  dataReadReqBuff.en   = en;
  dataReadReqBuff.addr = addr;
}

wordLen_t ram_c::InstReadResp()
{
  if (instReadReqBuff.en) {
    return memRead(instReadReqBuff.addr) >> ((instReadReqBuff.addr % sizeof(wordLen_t)) * 8);
  } else {
    return 0;
  }
}

wordLen_t ram_c::DataReadResp()
{
  if (dataReadReqBuff.en) {
    return memRead(dataReadReqBuff.addr);
  } else {
    return 0;
  }
}

void ram_c::DataWrite(wordLen_t addr, wordLen_t data, bool en, mask_t mask)
{
  if (en) {
    wordLen_t fullMask = 0;
    for(int i = 0; i < sizeof(wordLen_t); i++) {
      wordLen_t byteEn = ((mask & (1<<i)) != 0);
      for (int j = 0; j < 8; j++) {
        fullMask |= (byteEn << (8*i+j));
      }
    }
    wordLen_t dataToWrite = (fullMask & data) | ((~fullMask) & memRead(addr));
    memWrite(addr, dataToWrite);
  }
}


wordLen_t ram_c::memRead(wordLen_t addr)
{
  assert(ADDR_START <= addr 
                   && addr <= ADDR_START + RAMSIZE / sizeof(wordLen_t)
        && "Addr out of range");
  return ram[(addr - ADDR_START) / sizeof(wordLen_t)];
}

void ram_c::memWrite(wordLen_t addr, wordLen_t data)
{
  assert(ADDR_START <= addr 
                   && addr <= ADDR_START + RAMSIZE / sizeof(wordLen_t)
        && "Addr out of range");
  ram[(addr - ADDR_START) / sizeof(wordLen_t)] = data;
}
