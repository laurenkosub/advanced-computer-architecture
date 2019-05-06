#include <machine/patmos.h>
#include <stdio.h>
int main() {
    volatile _IODEV int *io_ptr = (volatile _IODEV int *) 0xf00c0000;
    printf("Welcome to A5/1\n");
    printf("Let the key be 0x1223456789ABCDEF...\n");

    long long key = 0x1223456789ABCDEF;
    *io_ptr = key;
    long long val1;
    val1 = *io_ptr;

    printf("bitstream: %llx\n", val1);
    printf("bit length:%d\n", sizeof(val1));

    sleep(3);

    printf("now let's try a different key...");
    long long key2 = 0xabababababababab;
    *io_ptr = key2;
    long long val2;
    val2 = *io_ptr;

    printf("bitstream: %llx\n", val2);
    printf("bit length:%d\n", sizeof(val2));
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

