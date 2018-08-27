/*
  Include file for all SPM programs.

*/

#ifndef DATA_LEN
#define DATA_LEN  4096 // words
#endif

#ifndef BUFFER_SIZE
#define BUFFER_SIZE 128 // words
#endif

#define CNT 4 //cores
#define STATUS_OFFSET (CNT-1) // no of status flags for buffer1
#define STATUS_LEN (STATUS_OFFSET*2) // no of status flags for buffer1
#define FLAG_OFFSET STATUS_LEN

_iodev_ptr_t spm_ptr = (_iodev_ptr_t) PATMOS_IO_OWNSPM;

#define NEXT 0x10000/4 // SPMs are placed every 64 KB 
