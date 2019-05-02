
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
    char *dataAnalogue ="000010010";
    char *addrAnalogue ="0000100";
    short dataL;
    short dataR;
    int exit = 0;    
    
    setup(0);    
    exit = setInputBufferSize(65536);
    if(exit){
        return exit;
    }    
    
    dataL = (short) malloc(65536 * sizeof(short));
    dataR = (short) malloc(65536 * sizeof(short));    
    exit = writeToI2C(addrAnalogue, dataAnalogue);

    if(exit){
        return exit;
    }    
    
    exit = getInputBufferSPM(dataL, dataR);
    
    if(exit){
        return exit;
    }    
    
    int freq = getFreq(dataL);
    return freq;
}


