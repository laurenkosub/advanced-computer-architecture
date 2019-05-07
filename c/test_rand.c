// TODO : see how LFSR compares to rand() c function using die-c lib

#include <machine/patmos.h>
#include <stdio.h> 
#include <stdlib.h> 
#include <time.h>

#define LFSR_ADDR 0xf00b0000

/*
 * Tests the A5/1 cipher stream protocol
 * Uses a preset key and outputs a 114 bit a/51 cipher stream
 * author: Lauren Kosub s186193
 */
int main() {
    volatile _IODEV int *io_ptr = (volatile _IODEV int *) LFSR_ADDR;
    int n, val2;
    double t1, t2;

    clock_t b1, e1, b2, e2;  
     // Stores time seconds 
    printf("Test the time!\n");

    b1 = clock();
    n = *io_ptr;
    e1 = clock();
    t1 = (double)(e1 - b1) / CLOCKS_PER_SEC;

    b2 = clock();
    val2 = rand() % 114;
    e2 = clock();
    t2 = (double)(e2 - b2) / CLOCKS_PER_SEC;

    printf("LFSR Time: %f\n", t1);
    printf("rand() Time: %f\n", t2);

    if (t2 > t1) {
        printf("LFSR runs faster!\n");
    } else {
        printf("rand() runs faster :( \n");
    }

}
