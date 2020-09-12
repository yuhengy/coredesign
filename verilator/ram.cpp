#include "ram.h"

#include <stdio.h>

ram_c::ram_c(char* imgPath)
{
#ifdef DEBUG
  imgSize = 3;
  ram[0] = 0x800002b7;  // lui t0,0x80000
  ram[1] = 0x0002a023;  // sw  zero,0(t0)
  ram[2] = 0x0002a503;  // lw  a0,0(t0)

#else
  assert(imgPath && "No image file.");
  FILE *fp = fopen(imgPath, "rb");
  assert(fp && "Can not open image file.");

  fseek(fp, 0, SEEK_END);
  imgSize = ftell(fp);

  fseek(fp, 0, SEEK_SET);
  int ret = fread(ram, imgSize, 1, fp);
  assert(ret == 1 && "Can not read image file.");
  fclose(fp);

#endif
}

void* ram_c::getImgStart()
{
  return ram;
}

int ram_c::getImgSize()
{
  return imgSize;
}