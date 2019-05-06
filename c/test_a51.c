#include <machine/patmos.h>
#include <stdio.h>
int main() {
    volatile _IODEV int *io_ptr = (volatile _IODEV int *) 0xf00c0000;
    printf("Welcome to A5/1\n");
    printf("Let the key be 0x89ABCDEF...\n");

    long val1, val2;

    // let the key be the last 2 bytes of ocp
    long key = 0xdeadbeef;
    *io_ptr = key;
   
    // wait for output to be printed ? ... 
    sleep(30);
    
    val1 = *io_ptr;
    printf("bitstream: %lx\n", val1);
/*
    sleep(3);

    printf("now let's try a different key: 0xabababab...\n");
    long key2 = 0xabababab;
    *io_ptr = key2;
    long val2;

    sleep(10);

    val2 = *io_ptr;
    printf("bitstream: %lx\n", val2);*/
}
