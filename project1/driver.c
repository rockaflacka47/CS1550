#include "graphics.h"

int curr_x = 0;
int curr_y = 0;
int main(int argc, char ** argv){
    init_graphics();

    void *buffer = new_offscreen_buffer();

    char key;
    int n = 1;
    //draw the H in hi
    draw_h(buffer);
    blit(buffer);
    //wait for key press. quit on q or erase the H and draw the i on +.
    do {
		curr_x = 0;
		curr_y = 0;

		key = getkey();
		if (key == 'q')
			break;
		
		else if (key == '+') {
			clear_screen(buffer);
			draw_i(buffer);
			blit(buffer);
		}
		sleep_ms(200);
	}
	while (1);

    exit_graphics();
    return 0;

}

//method to draw the H based of parameters given in pdf.
void draw_h(void *buf){
    draw_line(buf,curr_x,curr_y, 0, 360, RGB(31,0,0));
    draw_line(buf,0,160, 100, 160, RGB(31,10,14));
    draw_line(buf,100,0, 100, 360, RGB(12,16,7));

}
//method to draw the i based of parameters given in pdf.
//the dot on the i is slanted to show any slope works.
void draw_i(void *buf){
    draw_line(buf, 150, 50, 150, 320, RGB(5, 6, 31));
    draw_line(buf, 150,10,155,20,RGB(8,16,3));
    draw_line(buf, 155,20, 150, 30, RGB(14,17,9));
    draw_line(buf, 150, 30, 145, 20, RGB(10,20,30));
    draw_line(buf, 145, 20, 150, 10, RGB(30,20,10));
}