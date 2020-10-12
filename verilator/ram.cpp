#include "ram.h"

#include <stdio.h>
#include <assert.h>

int readImage(wordLen_t* array, char* imgPath)
{
//#ifdef DEBUG
//  array[0] = 0x800002b7;  // lui t0,0x80000
//  //array[1] = 0x0002a023;  // sw  zero,0(t0)
//  //array[2] = 0x0002a503;  // lw  a0,0(t0)
//  return 1;

//#else
  assert(imgPath && "No image file.");
  FILE *fp = fopen(imgPath, "rb");
  assert(fp && "Can not open image file.");

  fseek(fp, 0, SEEK_END);
  int imgSize = ftell(fp);

  fseek(fp, 0, SEEK_SET);
  int ret = fread(array, imgSize, 1, fp);
  assert(ret == 1 && "Can not read image file.");
  fclose(fp);

  return imgSize;
//#endif
}

ram_c::ram_c(char* imgPath, CPU_RAM_IO_t inputCPU_RAM_IO)
{
  readImage(ram, imgPath);
  CPU_RAM_IO = inputCPU_RAM_IO;
//#ifdef DEBUG
//  printf("RAM: %x\n", (unsigned int)ram[0]);
//#endif

}

void ram_c::eval()
{
  // STEP1 Clock come, update status
  //       (Since reg_new do not depend on reg_old, we can merge reg_new and reg_old indeed)
  instReadReqBuff_old = instReadReqBuff_new;
  dataReadReqBuff_old = dataReadReqBuff_new;

  //STEP2 Compute IO
  if (instReadReqBuff_old.en) {
    *(CPU_RAM_IO.instReadIO_data) = memRead(instReadReqBuff_old.addr) >> ((instReadReqBuff_old.addr % sizeof(wordLen_t)) * 8);  //TODO: maybe need implemented in Chisel
  }
  if (dataReadReqBuff_old.en) {
    *(CPU_RAM_IO.dataReadIO_data) = memRead(dataReadReqBuff_old.addr) >> ((dataReadReqBuff_old.addr % sizeof(wordLen_t)) * 8);  //TODO: maybe need implemented in Chisel
  }

  //STEP3 Compute logic
  instReadReqBuff_new.en   = *(CPU_RAM_IO.instReadIO_en);
  instReadReqBuff_new.addr = *(CPU_RAM_IO.instReadIO_addr);
  dataReadReqBuff_new.en   = *(CPU_RAM_IO.dataReadIO_en);
  dataReadReqBuff_new.addr = *(CPU_RAM_IO.dataReadIO_addr);

  if (*(CPU_RAM_IO.dataWriteIO_en)) {
    wordLen_t fullMask = 0;
    for(int i = 0; i < sizeof(wordLen_t); i++) {
      wordLen_t byteEn = ((*(CPU_RAM_IO.dataWriteIO_mask) & (1<<i)) != 0);
      for (int j = 0; j < 8; j++) {
        fullMask |= (byteEn << (8*i+j));
      }
    }
    wordLen_t dataToWrite = (fullMask & *(CPU_RAM_IO.dataWriteIO_data)) 
                          | ((~fullMask) & memRead(*(CPU_RAM_IO.dataWriteIO_addr)));
    memWrite(*(CPU_RAM_IO.dataWriteIO_addr), dataToWrite);
  }
}

wordLen_t ram_c::memRead(wordLen_t addr)
{
  if (ADDR_START <= addr 
                   && addr <= ADDR_START + RAMSIZE / sizeof(wordLen_t)
        && "Addr out of range") {
    return ram[(addr - ADDR_START) / sizeof(wordLen_t)];
  } else {
    printf("Warning: Addr out of range!!!\n");
    return 0;
  }
}

void ram_c::memWrite(wordLen_t addr, wordLen_t data)
{
  assert(ADDR_START <= addr 
                   && addr <= ADDR_START + RAMSIZE / sizeof(wordLen_t)
        && "Addr out of range");
  ram[(addr - ADDR_START) / sizeof(wordLen_t)] = data;
}
