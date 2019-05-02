#include <machine/patmos.h>
#include <stdio.h>
int main() {
    int val1, val2;
    volatile _IODEV int *io_ptr = (volatile _IODEV int *) 0xf00b0000;
    val1 = *io_ptr;
    printf("value: %d\n", val1);
    val2 = *io_ptr;
    printf("value: %d\n", val2);
    printf("Done\n");
}
