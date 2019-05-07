#include <machine/patmos.h>
#include <stdio.h>

#define A51_ADDR 0xf00b0000

/*
 * Tests the A5/1 cipher stream protocol
 * Uses a preset key and outputs a 114 bit a/51 cipher stream
 * author: Lauren Kosub s186193
 */
int main() {
    volatile _IODEV int *io_ptr = (volatile _IODEV int *) A51_ADDR;
    printf("Welcome to A5/1\n");
    printf("Let the key be 0x123abcde123abcde...\n");

    long val1;
    int secretBit;

    // let the key be the last 2 bytes of ocp
    long key = 0xaaabcdef;
    *io_ptr = key;
   
    // wait for output to be printed ? ... 
    printf("bitstream: ");
    for (int i = 0; i < 114; i++ ) {
        for (int j = 0; j < 100000000; j++) {} //delay
        val1 =  *io_ptr;
        secretBit = (val1) & 1; // extract 1st bit where data is stored
        printf("%d", secretBit);
    }
}
