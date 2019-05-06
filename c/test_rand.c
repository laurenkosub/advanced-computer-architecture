#include <machine/patmos.h>
#include <stdio.h>
int main() {

    volatile _IODEV int *io_ptr = (volatile _IODEV int *) 0xf00c0000;
    int val1;
    val1 = *io_ptr;
    return val1;
}
