const int NOC_MASTER = 0;
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <machine/patmos.h>
#include "libcorethread/corethread.h"
#include "libmp/mp.h"

void blink(int period);

void slave(void* param);

int main() {
	int slave_param = 1;
	printf("I am alive!\n");
	for(int i = 0; i < get_cpucnt(); i++) {
		if (i != NOC_MASTER) {
			if(corethread_create(i,&slave,(void*)slave_param) != 0){
				printf("Corethread %d not created\n",i);
			}
		}
	}
	printf("I started the threads!\n");
	for(;;){}

  	return 0;
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

void slave(void* param) {
	blink(255);

	return;
}
