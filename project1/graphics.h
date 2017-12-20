//header file to declare all necessary things.
#define RGB(r,g,b) ((r<<11) | (g<<5) | (b))

typedef unsigned short color_t;

void init_graphics();
void set_settings(int toDo);
void exit_graphics();
char getkey();
void sleep_ms(long ms);
void clear_screen(void *img);
void draw_pixel(void *img, int x, int y, color_t color);
void draw_line(void *img, int x1, int y1, int width, int height, color_t color);
void *new_offscreen_buffer();
void blit(void *src);
int abs(int value);
