#include <machine/patmos.h>
#include <stdio.h>
int main() {
    volatile _IODEV int *io_ptr = (volatile _IODEV int *) 0xf00c0000;
    printf("Welcome to A5/1\n");
    printf("Let the key be 0x1223456789ABCDEF...\n");

    long long key = 0x1223456789ABCDEF;
    *io_ptr = key;
    long long val1;
    
    sleep(10);
    
    val1 = *io_ptr;
    printf("bitstream: %llx\n", val1);

    sleep(3);

    printf("now let's try a different key: 0xabababababababab...\n");
    long long key2 = 0xabababababababab;
    *io_ptr = key2;
    long long val2;

    sleep(10);

    val2 = *io_ptr;
    printf("bitstream: %llx\n", val2);
}
