
/*
 * File for testing FAT32 on SD card.
 *
 * Authors: Max Rishoej (maxrishoej@gmail.com)
 */

#include <stdio.h>
#include <stdlib.h>

#include <machine/spm.h>
#include <machine/patmos.h>

#include "libaudio/audio.c"
#include "libaudio/audio.h"

const int LIM = 1000;
const int NOC_MASTER = 0;
const int BUFFERSIZE = 65536;

int findMax(short * data){
    int i, max;
    i = 0;
    max = 0;

    while (i < 65536) {
        if(data[i] > max) {max = data[i];}
        i += 1;
    }

    return i;
}

int getFreq(short * data) {
    char *dataAnalogue ="000010010";
    char *addrAnalogue ="0000100";
    short tol = 100;
    int count = 0;
    int ans = 0;    
    int j = 0;

    int i = findMax(data);
    double norm  = 32767/data[i];    

    for (j = i; j < 65536; j++) {data[j] *= norm;}    

    int * zeroes = malloc(100 * sizeof(int));

    j = i;
    while (j < 65536 && count < 100){
        if (data[j] < tol && data[j] > (0 - tol)){
            zeroes[count] = j;
            count += 1;
            j += 9;
        }
        j++;
    }    

    for(j = 0; j < count; j++){
        ans += zeroes[j + 1] - zeroes[j];
    }

    ans /= count;
    ans = 44100/ans;    
    return ans;
}

int listen() {
    arguments to thread 1 function
        int exit = 0;
    int allocsDone[AUDIO_CORES] = {0};
    int send_chans_con = 0;
    int recv_chans_con = 0;
    int reconfigDone[AUDIO_CORES] = {0};
    volatile _UNCACHED int *exitP           = (volatile _UNCACHED int *) &exit;
    volatile _UNCACHED int *current_modeP   = (volatile _UNCACHED int *) &current_mode;
    volatile _UNCACHED int *allocsDoneP     = (volatile _UNCACHED int *) &allocsDone;
    volatile _UNCACHED int *send_chans_conP = (volatile _UNCACHED int *) &send_chans_con;
    volatile _UNCACHED int *recv_chans_conP = (volatile _UNCACHED int *) &recv_chans_con;
    volatile _UNCACHED int *reconfigDoneP   = (volatile _UNCACHED int *) &reconfigDone;
    volatile _UNCACHED int (*threadFunc_args[2+AUDIO_CORES+2+AUDIO_CORES]);
    threadFunc_args[0] = exitP;
    threadFunc_args[1] = current_modeP;
    threadFunc_args[2] = allocsDoneP;
    threadFunc_args[2+AUDIO_CORES] = send_chans_conP;
    threadFunc_args[2+AUDIO_CORES+1] = recv_chans_conP;
    threadFunc_args[2+AUDIO_CORES+2] = reconfigDoneP;

    *ledReg = 0;

    //check if amount of FX cores exceeds available cores
    if(AUDIO_CORES > NOC_CORES) {
        printf("ERROR: need %d audio cores, but current platform has %d\n", AUDIO_CORES, NOC_CORES);
        exit = 1;
    }
/*
    printf("starting thread and NoC channels...\n");
    //set thread function and start thread
    int threads[AUDIO_CORES-1];
    for(int i=0; i<(AUDIO_CORES-1); i++) {
        threads[i] = (corethread_t) (i+1);
        if (corethread_create(threads[i], &threadFunc, (void*) threadFunc_args) != 0) {
            printf("ERROR: Thread %d was not creaded correctly\n", i+1);
            exit = 1;
        }
        printf("Thread created on core %d\n", i+1);
    }
*/
    setup(1); //for guitar

    // enable input
    *audioAdcEnReg = 1;
    //let input buffer fill in before starting to output
    for(unsigned int i=0; i<(BUFFER_SIZE * 1536); i++) { //wait for BUFFER_SIZE samples
        *audioDacEnReg = 0;
    }
    //finally, enable output
    *audioDacEnReg = 1;

    setInputBufferSize(BUFFER_SIZE);
    setOutputBufferSize(BUFFER_SIZE); 

    int freq = getFreq(dataL);
    return freq;
}
/*
   int main() {
   int l = listen();
   printf("%d", l);
   }*/


