#include <machine/patmos.h>
#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>

#define A51_ADDR 0xf00b0000

/*
 * Tests the A5/1 cipher stream protocol
 * Uses a preset key and outputs a 114 bit a/51 cipher stream
 * author: Lauren Kosub s186193
 */
int main() {
    volatile _IODEV int *io_ptr = (volatile _IODEV int *) A51_ADDR;
    unsigned long val1, secKey = 0;
    unsigned long plaintext = 0xbaddadd1;
    printf("Welcome to A5/1\n");
    printf("key is 0x1fabcd1f1fabcd1f\n");
    printf("plain text to encrypt is %lx\n", plaintext);
    
    /*
    printf("enter a message to encrypt: ");
    char line[1024];    
    gets(line);

    // turn plaintext to hex cipher text input
    for (int j = 0; j < strlen(line) ; j++) {
        plaintext = (plaintext | (strtoul(line[j], NULL, 16) << (4*j)));
    }
    */

    //printf("114 bit secret key found via bit stream: ");
    for (int i = 0 ; i < 114; i++) {
        val1 = (*io_ptr) % 2; // get LSB
        //printf("%u", val1);
        secKey = secKey | (val1 << (113-i));
    }

    // encrypt the message by XORing the message and the generated secret key
    printf("secret key printed in hex: %lx\n", secKey);
    printf("encrypted message printed in hex: %lx\n", secKey ^ plaintext); 

}
