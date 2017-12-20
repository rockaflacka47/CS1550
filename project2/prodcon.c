#include <stdlib.h>
#include <asm/errno.h>
#include <asm/unistd.h>
#include <asm/mman.h>

//struct for list of processes
struct proc_node{
    struct task_struct *process;
    struct proc_node *next;
};

//semaphore struct
struct cs1550_sem{
    int val;
    struct proc_node head;
    struct proc_node tail;
};

//declare variables
void *empty;
void *full;
void *lock;
struct cs1550_sem *semEmpty;
struct cs1550_sem *semFull;
struct cs1550_sem *semLock;
int *consumerNum;
int *producerNum;
int *endBuffer;
int bufferSize;
int *add;
int *sub;
void *buffer;


int main(int argc, char *argv[]){
    int numConsumers;
    int numProducers;
    
    //check if correct number of args then serparate them
    if(argc != 4){
        perror("Error please run with the correct number of parameters\n");
        exit(1);
    }
    numConsumers = atoi(argv[1]);
    numProducers = atoi(argv[2]);
    bufferSize = atoi(argv[3]);

    //calculate the size of the region needed. 3 structs, two ints in each, plus one buffer
    int mmapSize =  (sizeof(struct cs1550_sem)* 3)* (sizeof(int)*2) + bufferSize;
    buffer = mmap(NULL, mmapSize, PROT_READ|PROT_WRITE,MAP_SHARED|MAP_ANONYMOUS,0,0);
    if(buffer == -1){
        perror("Error mapping memory\n");
        exit(1);
    }
    //divy up the memory region then allocate them as the appropriate structure
    int semSize = sizeof(struct cs1550_sem);
    empty = buffer;
    full = buffer + semSize;
    lock = full + semSize;
    consumerNum = lock + semSize;
    producerNum = consumerNum + sizeof(int);
    endBuffer = producerNum + sizeof(int);

    add = producerNum;
    sub = consumerNum;

    semEmpty = (struct cs1550_sem *) empty;
    semFull = (struct cs1550_sem *) full;
    semLock = (struct cs1550_sem *) lock;

    semEmpty->val = bufferSize;
    semFull->val = 0;
    semLock->val = 1;

    //create the correct numbers of prodcuers and consumers
    int i;
    for(i = 0; i < numProducers; i++){
        if(fork() == 0){
            producer(i);
        }
    }
    for(i = 0; i < numConsumers; i++){
        if(fork() == 0){
            consumer(i);
        }
    }
    for(;;){}
    return 0;

}

void producer(int threadNum){
    for(;;){
       
        down(semEmpty);

        down(semLock);

        endBuffer[*add] = *add;
        *add = ((*add)+1) % bufferSize;

        printf("Producer %d produced pancake %d\n", threadNum, *add);

        up(semLock);

        up(semFull);

    }

}

void consumer(int threadNum){
    int item;
    
    for(;;){
        down(semFull);

        down(semLock);

        item = endBuffer[*sub];
        *sub = ((*sub)+1) % bufferSize;

        printf("Consumer %d ate pancake %d\n", threadNum, *sub);

        up(semLock);

        up(semEmpty);
    }
}

void down(struct cs1550_sem *s){
    int i;
    if(i = syscall(__NR_sys_cs1550_down, s) < 0){
    //if(i = syscall(325, s) < 0){
        //perror("Error %d returned\n", i);
    }

}
void up(struct cs1550_sem *s){
    int i;
    if(i = syscall(__NR_sys_cs1550_up, s) < 0){
    //if(i = syscall(326, s) < 0){
        //perror("Error %d returned\n", i);
    }
    
}


