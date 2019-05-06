#include <machine/patmos.h>
#include <stdio.h>
int main() {
    volatile _IODEV int *io_ptr = (volatile _IODEV int *) 0xf00c0000;
    printf("Welcome to A5/1\n");
    printf("Let the key be 0x1223456789ABCDEF...\n");
    printf("and let the frame number be 0x000134...\n");

    long long key = 0x1223456789ABCDEF;
    int frame = 0x000134;
    *io_ptr = key;
    sleep(60);
    long long val1;
    val1 = *io_ptr;

    printf("val 1: %llx\n", val1);
    printf("bit length:%d\n", sizeof(val1));
}

int countBits(long long number) {
    int count = 0;
    while (number != 0)
    {
        if ((number & 1) == 1)
            count++;
        number = number >> 1;
    }
}

