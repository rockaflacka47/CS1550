#include <fcntl.h>
#include <sys/mman.h>
#include <sys/ioctl.h>
#include <termios.h>
#include <time.h>
#include <unistd.h>
#include <linux/fb.h>
#include <sys/types.h>


//define rgb macro using shifts and or's to mask
#define RGB(r,g,b) ((r<<11) | (g<<5) | (b))

//define all lgobal variables
int fb;
int x_size;
int y_size;
int size;
typedef unsigned short color_t;
color_t *buffer;
//init graphics method using mmap and ioctl
void init_graphics(){
    struct fb_var_screeninfo screen_info;
    struct fb_fix_screeninfo fix_info;

   fb = open("/dev/fb0", O_RDWR);
  

   ioctl(fb, FBIOGET_VSCREENINFO, &screen_info);
   ioctl(fb, FBIOGET_FSCREENINFO, &fix_info);
   //get display width
   x_size = (fix_info.line_length/(sizeof *buffer));
   //get display height
   y_size = screen_info.yres_virtual;
  
   size = fix_info.line_length;
   

   buffer = mmap(NULL, size * y_size, PROT_WRITE | PROT_READ, MAP_SHARED, fb, 0);
   set_settings(0);
   clear_screen();
  
   

}

//method to set ICANON and echo on and off. 
void set_settings(int toDo){
    struct termios settings;
    ioctl(1, TCGETS, &settings);

    if(toDo){
        settings.c_lflag |= ICANON;
        settings.c_lflag |= ECHO;
    }
    else{
        settings.c_lflag &= ICANON;
        settings.c_lflag &= ECHO;
    }

    ioctl(1, TCSETS, &settings);
}

//clear screen, turn settings, back on unmap the buffer and close the FB.
void exit_graphics(){
    clear_screen();       
    set_settings(1);      
    munmap(buffer, size); 
    close(fb);             
}

//confirm valid numbers using abs
int abs(int value) {
    if (value < 0) { value = -value; }
    return value;
}
//reads stdin(0) (keyboard) for input. fd is set to stdin(0) and constantly polls for input. once input is received ret is 
//set to that input and returned.
char getkey(){

    char ret = '\0';
    fd_set fd;
    FD_ZERO(&fd);
    FD_SET(0, &fd);

    struct timeval wait;
    wait.tv_sec = 0;
    wait.tv_usec = 0;
    
    if(select(1, &fd, NULL, NULL, &wait)){
        read(0, &ret, sizeof(char));
    }
   
    return ret;
}

//sleep for x ms 
void sleep_ms(long ms){
    
    struct timespec timer;
    timer.tv_nsec = ms * 1000000;
    nanosleep(timer.tv_nsec, NULL);
    
}

    //clear screen using the escape sequence 
void clear_screen(void *img){
    write(1, "\033[2J", 8);
   
}

//confirm valid input then set byte at the correct pixel to right color.
void draw_pixel(void *img, int x, int y, color_t color){
  
    if (x< 0 || x > x_size) {
        x = abs(x % x_size);
    }
    if(y<0 || y > y_size){
        y = abs(y % y_size);
    }

    buffer[(y * x_size) + x] = color;
    
}

// https://rosettacode.org/wiki/Bitmap/Bresenham%27s_line_algorithm#C
//implements bresenhams algorithm as learned from link above

void draw_line(void *img, int x1, int y1, int width, int height, color_t color){
    
    int x_dir = x1 < width ? 1 : -1;
    int y_dir = y1 < height ? 1 : -1;
    int dx = abs(width-x1);
    int dy = abs(height-y1);
    int stdErr = (dx>dy ? dx : -dy)/2, e2;

    for(;;){
        draw_pixel(img, x1,y1,color);
        if(x1==width && y1==height) break;
        e2 = stdErr;
        if(e2>-dx) {
            stdErr -=dy;
            x1 += x_dir;
        }
        if(e2 < dy) {
            stdErr +=dx;
            y1 += y_dir;
        }
    }
}

//creates a new buffer for double buffering
void *new_offscreen_buffer(){
    void *ret;
    ret = mmap(NULL, size * y_size, PROT_WRITE | PROT_READ, MAP_PRIVATE | MAP_ANONYMOUS, -1, 0);
    return ret;
}
//copies offscreen buffer to display
void blit(void *src){
   
    int i;
    for(i = 0;i < size; i++){
        buffer[i] = &src[i];
    }
   
}