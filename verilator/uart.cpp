#include "uart.h"

#include <stdio.h>
#include <assert.h>

uart_c::uart_c()
{
}

wordLen_t uart_c::read(wordLen_t addr)
{
  lineStatusRegister = UARTLITE_RX_VALID;
  
  if (addr == UART0_CTRL_ADDR) {
    char temp;
    printf("uartInput-->");
    scanf("%c", &temp);
    return (wordLen_t)temp;
  }
  else if (addr == UART0_CTRL_ADDR + 8) {
    return (wordLen_t)lineStatusRegister;
  }
  else {
    printf("Warning: Uart Read Addr out of range!!!\n");
  }
}

void uart_c::write(wordLen_t addr, wordLen_t data)
{
  if (addr == UART0_CTRL_ADDR) {
    printf("[UART OUTPUT:] ->%c<- from %d\n", (char)data, (char)data);
    lineStatusRegister = (char)(data >> 32);
  }
  else {
    printf("Warning: Uart Write Addr out of range!!!\n");
  }
}
