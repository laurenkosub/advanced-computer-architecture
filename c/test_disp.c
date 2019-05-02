/* Test Tuner's IO to Sev Segment Display */

#include <stdio.h>
#include <stdlib.h>
#include <machine/patmos.h>
#include <machine/spm.h>
#include "test_aud.c"

#define DISP_SYM_MASK 0x80
#define NOC_MASTER 1

void printSegmentInt(unsigned base_addr, int number, int displayCount)__attribute__((noinline));

void resetDisp(unsigned disp_addr, int from, int to){
	volatile _IODEV unsigned *disp_ptr = (volatile _IODEV unsigned *) disp_addr;
	unsigned pos = 0;
	for(pos=from; pos < to; pos++){
		*disp_ptr = DISP_SYM_MASK | 0x7F;
		disp_ptr++;
	}
}

void printSegmentInt(unsigned base_addr, int number, int displayCount) {
	volatile _IODEV unsigned *disp_ptr = (volatile _IODEV unsigned *) base_addr;
	unsigned pos = 0;
	unsigned byte_mask = 0x0000000F;
	unsigned range = (number > 0) ? displayCount : displayCount-1;	//reserve one digit for '-' symbol
	unsigned value = abs(number);
	for(pos=0; pos < range; pos++) {
		*disp_ptr = (unsigned)((value & byte_mask) >> (pos*4));
		byte_mask = byte_mask << 4;
		disp_ptr += 1;
	}
	if (number < 0) {
		*disp_ptr = DISP_SYM_MASK | 0x3F;
	}
}

int main(int argc, char **argv)
{
	volatile _IODEV int *uart_ptr = (volatile _IODEV int *)	PATMOS_IO_UART;
	volatile _IODEV int *led_ptr  = (volatile _IODEV int *) PATMOS_IO_LED;
	volatile _IODEV int *disp_ptr = (volatile _IODEV int *)	0xf0050000;

    printf("Listening ... \n");
    int x = listen();

	printf("\nDisplaying frequency ...\n");
	resetDisp(0xf0050000, 0, 8);
	printSegmentInt(0xf0050000, x, 8);

	return 0;
}   
