#include <machine/patmos.h>
#include <stdio.h>
#include <unistd.h>

#define A51_ADDR 0xf00c0000

/*
 * Tests the A5/1 cipher stream protocol
 * Uses a preset key and outputs a 114 bit a/51 cipher stream
 * author: Lauren Kosub s186193
 */
int main() {
    volatile _IODEV int *io_ptr = (volatile _IODEV int *) A51_ADDR;
    printf("Welcome to A5/1\n");
    printf("Key: 0x1fabcd1f1fabcd1f\n");

    int val1;

    // wait for output to be printed ? ... 
    /*printf("bitstream: ");
    for (int i = 0 ; i < 114; i++) {
        for (int d = 0; d < 1000; d++ ) {
            val1 = *io_ptr;
            printf("%d", val1);
        }
    }*/

    printf("%d\n", *io_ptr);
    sleep(2);
    printf("%d\n", *io_ptr);
    sleep(2);
    printf("%d\n", *io_ptr);
    sleep(2);
    printf("%d\n", *io_ptr);
    sleep(2);
    printf("%d\n", *io_ptr);
    sleep(2);
    printf("%d\n", *io_ptr);
    sleep(2);
    printf("%d\n", *io_ptr);
    sleep(2);
    printf("%d\n", *io_ptr);
    sleep(2);
    printf("%d\n", *io_ptr);
    sleep(2);
    printf("%d\n", *io_ptr);
    sleep(2);
    printf("%d\n", *io_ptr); 
}
