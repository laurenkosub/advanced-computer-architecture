const int NOC_MASTER = 0;
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <machine/patmos.h>
#include "libcorethread/corethread.h"
#include "libmp/mp.h"

#define MP_CHAN_NUM_BUF 2
#define MP_CHAN_BUF_SIZE 40

//blink function, period=0 -> ~10Hz, period=255 -> ~1Hz
void blink(int period) {//int
    // The hardware address of the LED
    #define LED ( *( ( volatile _IODEV unsigned * ) 0xF0090000 ) )
    for (int i=400000+14117*period; i!=0; --i){LED = 1;}
    for (int i=400000+14117*period; i!=0; --i){LED = 0;}

    return;
}

void slave1(int param) {
    // Create the port for channel 1
    qpd_t * chan1 = mp_create_qport(1, SOURCE, MP_CHAN_BUF_SIZE, MP_CHAN_NUM_BUF);
    mp_init_ports();

