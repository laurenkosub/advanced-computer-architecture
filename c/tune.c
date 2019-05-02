#include <machine/patmos.h>
#include <stdio.h>
int main() {
    int val1;
    volatile _IODEV int *io_ptr = (volatile _IODEV int *) 0xf00b0000;
    val1 = *io_ptr;
    printf("value: %d\n", val1);
    printf("Done\n");
}
