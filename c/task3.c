const int NOC_MASTER = 0;
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <machine/patmos.h>
#include "libcorethread/corethread.h"
#include "libmp/mp.h"

#define MP_CHAN_NUM_BUF 2
#define MP_CHAN_BUF_SIZE 40

void blink(int period);

void slave1(void* param);
void slave2(void* param);

int main() {
    int worker_id = 1;
    int parameter = 255;
    int valid = 1;
    int empty = 0;

    corethread_create(1, &slave1, valid);
    corethread_create(2, &slave1, valid);
    corethread_create(3, &slave1, empty);
    corethread_create(4, &slave1, empty);

    int *res;
    corethread_join(worker_id, &res);
  	return *res;
}

//blink function, period=0 -> ~10Hz, period=255 -> ~1Hz
void blink(int period) {
	#define LED ( *( ( volatile _IODEV unsigned * )	0xF0090000 ) )

        for (;;)
        {
		for (int i=400000+14117*period; i!=0; --i){LED = 1;}
		for (int i=400000+14117*period; i!=0; --i){LED = 0;}

        }
	return;
}
/*
void slave(void* param) {
	blink(rand_r(255) % 255);
	return;
}
*/
// Slave function running on core 1
void slave1(void* param) {
	printf("creating slave1\n");

    // Create the source port for channel 1
    qpd_t * chan1 = mp_create_qport(1, SOURCE,
            MP_CHAN_BUF_SIZE, MP_CHAN_NUM_BUF);
    mp_init_ports();

    // create channel 4
    qpd_t * chan4 = mp_create_qport(1, SOURCE,
            MP_CHAN_BUF_SIZE, MP_CHAN_NUM_BUF);
    mp_init_ports();

    // message passing
    for (;;) {
        if (seed == 1) { blink(freq); }
        //write
        *( volatile int _SPM * ) ( chan1->write_buf ) = seed;
        mp_send(chan1,0);
        //read
        mp_recv(chan4,0);
        seed = *((volatile int _SPM *) (chan4->read_buf));
        mp_ack(chan4,0);
    }
    return;
}

// Slave function running on core 1
void slave2(void* param) {
	printf("creating slave2\n");

    // Create the source port for channel 1
    qpd_t * chan1 = mp_create_qport(1, SOURCE,
            MP_CHAN_BUF_SIZE, MP_CHAN_NUM_BUF);
    mp_init_ports();

    // create channel 4
    qpd_t * chan4 = mp_create_qport(1, SOURCE,
            MP_CHAN_BUF_SIZE, MP_CHAN_NUM_BUF);
    mp_init_ports();

    // message passing
    for (;;) {
        if (seed == 1) { blink(freq); }
        //write
        *( volatile int _SPM * ) ( chan1->write_buf ) = seed;
        mp_send(chan1,0);
        //read
        mp_recv(chan4,0);
        seed = *((volatile int _SPM *) (chan4->read_buf));
        mp_ack(chan4,0);
    }
    return;
}
// Slave function running on core 2
void slave2(void* param) {
printf("creating slave2\n");
    // Create the sink port for channel 1
    qpd_t * chan1 = mp_create_qport(1, SINK,
            MP_CHAN_BUF_SIZE, MP_CHAN_NUM_BUF);
    mp_init_ports();

    // message passing
    mp_recv(chan1,0);
    mp_ack(chan1,0);
    printf("message received!");

    return;
}

