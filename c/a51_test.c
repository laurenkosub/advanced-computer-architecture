#include <machine/patmos.h>
#include <stdio.h>

#define A51_ADDR 0xf00b0000
/*
 * Test the A5/1 Cipher Stream
 * Takes a hex number as input (key) to generate a 114 bit cipher stream
 * author: Lauren Kosub
 */
int main() {
    volatile _IODEV int *io_ptr = (volatile _IODEV int *) A51_ADDR;
    
    long val1;
    long key;
    int secretBit;

    printf("Welcome to A5/1\n");
    printf("Enter 32-bit hexadecimal value without \"0x\". This will generate your 64 bit key: ");
    scanf("%lx", &key);
  
    // let the key be the last 2 bytes of ocp
    printf("your key is: %lx%lx\n", key, key);
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
