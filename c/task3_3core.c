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

//1- ch1 -2- ch2 -3- chan3 -4- chan4 -5- chan5 -6- chan6 -7- chan7 -8- chan8 -1
// Slave function running on core 1
void slave1(int param) {
    // Create the port for channel 1
    qpd_t * chan1 = mp_create_qport(1, SOURCE, MP_CHAN_BUF_SIZE, MP_CHAN_NUM_BUF);
    mp_init_ports();

    // Create the port for channel 8
    qpd_t * chan8 = mp_create_qport(8, SINK, MP_CHAN_BUF_SIZE, MP_CHAN_NUM_BUF);
    mp_init_ports();

    int freq = 255;
    int seed = param;


    for( ;; ){
        if(seed == 1){
            blink(freq);
        }
        // Writing an unsigned integer value to the channel
        // write buffer and sending it.
        *( volatile int _SPM * ) ( chan1->write_buf ) = seed;
        mp_send(chan1,0);       

        // Receiving, reading and acknowledge reception of
        // an unsigned integer value from the channel read buffer
        mp_recv(chan8,0);
        seed = *(( volatile int _SPM * ) ( chan8->read_buf ));
        mp_ack(chan8,0);
    }
    return;
}

// Slave function running on core 2
void slave2(int param) {
    // Create the port for channel 1
    qpd_t * chan1 = mp_create_qport(1, SINK, MP_CHAN_BUF_SIZE, MP_CHAN_NUM_BUF);
    mp_init_ports();

    // Create the port for channel 2
    qpd_t * chan2 = mp_create_qport(2, SOURCE, MP_CHAN_BUF_SIZE, MP_CHAN_NUM_BUF);
    mp_init_ports();


    int freq = 255;
    int seed = param;

    for( ;; ){
        if(seed == 1){
            blink(freq);
        }
        // Writing an unsigned integer value to the channel
        // write buffer and sending it.
        *( volatile int _SPM * ) ( chan2->write_buf ) = seed;
        mp_send(chan2,0);       

        // Receiving, reading and acknowledge reception of
        // an unsigned integer value from the channel read buffer
        mp_recv(chan1,0);
        seed = *(( volatile int _SPM * ) ( chan1->read_buf ));
        mp_ack(chan1,0);
    }
    return;
}

// Slave function running on core 3
void slave3(int param) {
    // Create the port for channel 2
    qpd_t * chan2 = mp_create_qport(2, SINK, MP_CHAN_BUF_SIZE, MP_CHAN_NUM_BUF);
    mp_init_ports();

    // Create the port for channel 3
    qpd_t * chan3 = mp_create_qport(3, SOURCE, MP_CHAN_BUF_SIZE, MP_CHAN_NUM_BUF);
    mp_init_ports();


    int freq = 255;
    int seed = param;

    for( ;; ){
        if(seed == 1){
            blink(freq);
        }
        // Writing an unsigned integer value to the channel
        // write buffer and sending it.
        *( volatile int _SPM * ) ( chan3->write_buf ) = seed;
        mp_send(chan3,0);       

        // Receiving, reading and acknowledge reception of
        // an unsigned integer value from the channel read buffer
        mp_recv(chan2,0);
        seed = *(( volatile int _SPM * ) ( chan2->read_buf ));
        mp_ack(chan2,0);
    }
    return;
}

// Slave function running on core 4
void slave4(int param) {
    // Create the port for channel 3
    qpd_t * chan3 = mp_create_qport(3, SINK, MP_CHAN_BUF_SIZE, MP_CHAN_NUM_BUF);
    mp_init_ports();

    // Create the port for channel 4
    qpd_t * chan4 = mp_create_qport(4, SOURCE, MP_CHAN_BUF_SIZE, MP_CHAN_NUM_BUF);
    mp_init_ports();

    int freq = 255;
    int seed = param;

    for( ;; ){
        if(seed == 1){
            blink(freq);
        }
        // Writing an unsigned integer value to the channel
        // write buffer and sending it.
        *( volatile int _SPM * ) ( chan4->write_buf ) = seed;
        mp_send(chan4,0);       

        // Receiving, reading and acknowledge reception of
        // an unsigned integer value from the channel read buffer
        mp_recv(chan3,0);
        seed = *(( volatile int _SPM * ) ( chan3->read_buf ));
        mp_ack(chan3,0);
    }
    return;
}

// Slave function running on core 5
void slave5(int param) {
    // Create the port for channel 4
    qpd_t * chan4 = mp_create_qport(4, SINK, MP_CHAN_BUF_SIZE, MP_CHAN_NUM_BUF);
    mp_init_ports();

    // Create the port for channel 5
    qpd_t * chan5 = mp_create_qport(5, SOURCE, MP_CHAN_BUF_SIZE, MP_CHAN_NUM_BUF);
    mp_init_ports();

    int freq = 255;
    int seed = param;

    for( ;; ){
        if(seed == 1){
            blink(freq);
        }
        // Writing an unsigned integer value to the channel
        // write buffer and sending it.
        *( volatile int _SPM * ) ( chan5->write_buf ) = seed;
        mp_send(chan5,0);       

        // Receiving, reading and acknowledge reception of
        // an unsigned integer value from the channel read buffer
        mp_recv(chan4,0);
        seed = *(( volatile int _SPM * ) ( chan4->read_buf ));
        mp_ack(chan4,0);
    }
    return;
}

// Slave function running on core 6
void slave6(int param) {
    // Create the port for channel 5
    qpd_t * chan5 = mp_create_qport(5, SINK, MP_CHAN_BUF_SIZE, MP_CHAN_NUM_BUF);
    mp_init_ports();

    // Create the port for channel 6
    qpd_t * chan6 = mp_create_qport(6, SOURCE, MP_CHAN_BUF_SIZE, MP_CHAN_NUM_BUF);
    mp_init_ports();

    int freq = 255;
    int seed = param;

    for( ;; ){
        if(seed == 1){
            blink(freq);
        }
        // Writing an unsigned integer value to the channel
        // write buffer and sending it.
        *( volatile int _SPM * ) ( chan6->write_buf ) = seed;
        mp_send(chan6,0);       

        // Receiving, reading and acknowledge reception of
        // an unsigned integer value from the channel read buffer
        mp_recv(chan5,0);
        seed = *(( volatile int _SPM * ) ( chan5->read_buf ));
        mp_ack(chan5,0);
    }

    return;
}

// Slave function running on core 7
void slave7(int param) {
    // Create the port for channel 6
    qpd_t * chan6 = mp_create_qport(6, SINK, MP_CHAN_BUF_SIZE, MP_CHAN_NUM_BUF);
    mp_init_ports();

    // Create the port for channel 7
    qpd_t * chan7 = mp_create_qport(7, SOURCE, MP_CHAN_BUF_SIZE, MP_CHAN_NUM_BUF);
    mp_init_ports();

    int freq = 255;
    int seed = param;

    for( ;; ){
        if(seed == 1){
            blink(freq);
        }
        // Writing an unsigned integer value to the channel
        // write buffer and sending it.
        *( volatile int _SPM * ) ( chan7->write_buf ) = seed;
        mp_send(chan7,0);       

        // Receiving, reading and acknowledge reception of
        // an unsigned integer value from the channel read buffer
        mp_recv(chan6,0);
        seed = *(( volatile int _SPM * ) ( chan6->read_buf ));
        mp_ack(chan6,0);
    }
    return;
}

// Slave function running on core 8
void slave8(int param) {
    // Create the port for channel 7
    qpd_t * chan7 = mp_create_qport(7, SINK, MP_CHAN_BUF_SIZE, MP_CHAN_NUM_BUF);
    mp_init_ports();

    // Create the port for channel 8
    qpd_t * chan8 = mp_create_qport(8, SOURCE, MP_CHAN_BUF_SIZE, MP_CHAN_NUM_BUF);
    mp_init_ports();

    int freq = 255;
    int seed = param;

    for( ;; ){
        if(seed == 1){
            blink(freq);
        }
        // Writing an unsigned integer value to the channel
        // write buffer and sending it.
        *( volatile int _SPM * ) ( chan8->write_buf ) = seed;
        mp_send(chan8,0);       

        // Receiving, reading and acknowledge reception of
        // an unsigned integer value from the channel read buffer
        mp_recv(chan7,0);
        seed = *(( volatile int _SPM * ) ( chan7->read_buf ));
        mp_ack(chan7,0);
    }
    return;
}

int main() {
    int worker_id = 1;
    int parameter = 255;
    int valid = 1;
    int empty = 0; 

    corethread_create(1, &slave1, valid);
    corethread_create(2, &slave2, valid);
    corethread_create(3, &slave3, valid);
    corethread_create(4, &slave4, valid);
    corethread_create(5, &slave5, empty);
    corethread_create(6, &slave6, empty);
    corethread_create(7, &slave7, empty);
    corethread_create(8, &slave8, empty);
    int * res;
    corethread_join(worker_id,&res);
    return *res;
}
