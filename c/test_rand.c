#include <machine/patmos.h>
#include <stdio.h>
int main() {
    volatile _IODEV int *io_ptr = (volatile _IODEV int *) 0xf00c0000;
    int val1, val2;
    val1 = *io_ptr;
    printf("val 1: %d\n", val1);
    if (val1 < 65535 && val1 > 0) { 
        printf("true\n"); 
    } else { printf("false\n"); }
    val2 = *io_ptr;
    printf("val 2: %d\n", val2);
    if (val2 < 65535 && val2 > 0) { 
        printf("true\n"); 
    } else { printf("false\n"); }
}
