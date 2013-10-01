/*
 * driver.cpp
 *
 *  Created on: April 11, 2012
 *      Author: Nathan Floor
 *      St_Num: FLRNAT001
 *
 *		OpenGL project. Rotating the tea-pot.
 *
 *		Reference: nehe.gamedev.net/tutorial/
 *		Reference: gamedev.stackexchange.com
 *		Reference: www.swiftless.com/tutorials/opengl/camera.html
 *		Reference: www.swiftless.com/tutorials/opengltuts.html
 *		Reference: www.lighthouse3d.com/tutorials/glut-tutorial/keyboard
 *
 */
#include "GL/glew.h"
#include <GL/glut.h>
#include <GL/gl.h>
#include "SOIL/SOIL.h"

#include <math.h>
#include <vector>
#include <iostream>
#include <unistd.h>//header for sleeping

#include "textfile.h" //header for reading in a textfile

#define window_width 640
#define window_height 480

//tea-pot variables
//rotation variables/angles
float rotate_x;
float rotate_y;
float rotate_z;
//determine which axis to rotate around
bool x_rot = true;
bool y_rot;
bool z_rot;
//translation variables
float trans_x;
float trans_y;
float trans_z;
bool has_moved = false;//indicates that the object has been moved and needs to be updated

//camera variables
//viewpoint variables
float view_x = 0;
float view_y = 0;
float view_z = 0;
//rotation angles
float xrot;//up& down
float yrot;//left& right
float PI = 3.141592654;
//distance from origin
float cam_dist = 15;

//color variables
float redc = 1;
float greenc;
float bluec;

//textures
GLuint texture,bumpMap;
GLint shader_tex;
GLint shader_bump;

//shader variables
GLuint vertex,fragment,fragment2,program;

//loads textures in using SOIL libraries
GLuint load_Textures(std::string text_name){
	GLuint texture = SOIL_load_OGL_texture(text_name.c_str(), SOIL_LOAD_AUTO, SOIL_CREATE_NEW_ID, SOIL_FLAG_MIPMAPS);

	glBindTexture(GL_TEXTURE_2D, texture);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

	return texture;
}

//set materials for an object
void setMaterial ( GLfloat ambientR, GLfloat ambientG, GLfloat ambientB, 
		   GLfloat diffuseR, GLfloat diffuseG, GLfloat diffuseB, 
		   GLfloat specularR, GLfloat specularG, GLfloat specularB,
		   GLfloat shininess ) {

    GLfloat ambient[] = { ambientR, ambientG, ambientB };
    GLfloat diffuse[] = { diffuseR, diffuseG, diffuseB };
    GLfloat specular[] = { specularR, specularG, specularB };

    glMaterialfv(GL_FRONT_AND_BACK,GL_AMBIENT,ambient);
    glMaterialfv(GL_FRONT_AND_BACK,GL_DIFFUSE,diffuse);
    glMaterialfv(GL_FRONT_AND_BACK,GL_SPECULAR,specular);
    glMaterialf(GL_FRONT_AND_BACK,GL_SHININESS,shininess);
}

//enables user to rotate lights
float light_angle = (PI*3/2);
float light_angle2 = PI/2;
void set_lighting(void){
	glMatrixMode(GL_MODELVIEW);
	
	//setting up light source 1
	GLfloat light0_pos[4] = {100.0*sin(light_angle),20.0,100.0*cos(light_angle), 1.0};
	GLfloat light0_color[] = {1,0,0};

	//setting up light source 2
	GLfloat light1_pos[] = {100.0*sin(light_angle2),20.0,100.0*cos(light_angle2), 1.0};
	GLfloat light1_color[] = {0,0,1};

	GLfloat ambient_color[] = {0,0,0};
//	glEnable(GL_LIGHTING);
	glLightModelfv(GL_LIGHT_MODEL_AMBIENT,ambient_color);

	//enable lights
//	glEnable(GL_LIGHT0);
	glLightfv(GL_LIGHT0,GL_POSITION,light0_pos);
    	glLightfv(GL_LIGHT0,GL_AMBIENT,light0_color);
    	glLightfv(GL_LIGHT0,GL_DIFFUSE,light0_color);
    	glLightfv(GL_LIGHT0,GL_SPECULAR,light0_color);

//	glEnable(GL_LIGHT1);
	glLightfv(GL_LIGHT1,GL_POSITION,light1_pos);
        glLightfv(GL_LIGHT1,GL_AMBIENT,light1_color);
        glLightfv(GL_LIGHT1,GL_DIFFUSE,light1_color);
        glLightfv(GL_LIGHT1,GL_SPECULAR,light1_color);
}

void draw_axis(void){
	//draw axis lines(used to indicate orientation
	glColor3f(1,1,0);//yellow
	glBegin(GL_LINE);
		glVertex3f(-1000,0,0);//x-axis
		glVertex3f(1000,0,0);

		glVertex3f(0,-1000,0);//y-axis
		glVertex3f(0,1000,0);

		glVertex3f(0,0,-1000);//z-axis
		glVertex3f(0,0,1000);
	glEnd();
}

//setup textures in GL
void set_textures(void){
	glActiveTexture(GL_TEXTURE1);
	glBindTexture(GL_TEXTURE_2D, bumpMap);
	glUniform1i(shader_bump, 1);

	glActiveTexture(GL_TEXTURE0);
	glBindTexture(GL_TEXTURE_2D, texture);
	glUniform1i(shader_tex, 0);
}

//main loop for rotation of object
void draw_loop(void){//do all the drawing
	glClearColor(0,0,0,0);
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); //clears buffers
	glLoadIdentity(); //reset view

	//calculate camera view-point vector
	//reference: gamedev.com.stackexchange.com/question/20758/how-can-i-rotate-a-camera-about-its-target-point
	view_x = cam_dist*-sin(xrot*(PI/180))*cos(yrot*PI/180);
	view_y = cam_dist*-sin(yrot*(PI/180));
	view_z = -cam_dist*cos(xrot*(PI/180))*cos(yrot*PI/180);//camera vector

	gluLookAt(-view_x,-view_y,-view_z,0,0,0,0,1,0);//camera, target, and up vectors

	set_lighting();

	//tea-pot manipulation
	//translate tea-pot
	glPushMatrix();

	if(has_moved)
		glTranslatef(trans_x,trans_y,trans_z);
	//rotations
	glRotatef(rotate_x,1,0,0);
	glRotatef(rotate_y,0,1,0);
	glRotatef(rotate_z,0,0,1);
	
	//set texturing TODO
	set_textures();

	//draw tea-pot
	glColor3f(redc,greenc,bluec);
	glFrontFace(GL_CW);
	glutSolidTeapot(1.5);
	glFrontFace(GL_CCW);
//	glutWireTeapot(1.5);
	glPopMatrix();

//	draw_axis();//draw axis

	glutSwapBuffers();//display new draw items
}

//handle key control
void handle_controls(unsigned char key,int x,int y){
	usleep(100);

	if(key==27){ //press escape
		exit(0);//terminate program
	}

	//translate tea-pot
	if(key == 'u'){//left
		trans_x -= 0.5;
		has_moved = true;
	}
	else if(key == 'j'){//right
		trans_x += 0.5;
		has_moved = true;
	}
	else if(key == 'i'){//up
		trans_y += 0.5;
		has_moved = true;
	}
	else if(key == 'k'){//down
		trans_y -= 0.5;
		has_moved = true;
	}
	else if(key == 'o'){//forward
		trans_z += 0.5;
		has_moved = true;
	}
	else if(key == 'l'){//backward
		trans_z -= 0.5;
		has_moved = true;
	}

	//change color of pot
/*	if(key == '1'){
		redc = 1;
		greenc = 0;
		bluec = 0;
	}
	else if(key == '2'){
		redc = 0;
		greenc = 1;
		bluec = 0;
	}
	else if(key == '3'){
		redc = 0;
		greenc = 0;
		bluec = 1;
	}
*/
	//rotate teapot
	//select whether to rotate clock/anti-clock wise
	//reset to origin before starting
	if(key == 'a'){
		if(x_rot)
			rotate_x += 1.0;
		else if(y_rot)
			rotate_y += 1;
		else if(z_rot)
			rotate_z += 1;
	}
	else if(key == 's'){//anti-clock
		if(x_rot)
			rotate_x -= 1;
		else if(y_rot)
			rotate_y -= 1;
		else if(z_rot)
			rotate_z -= 1;
	}
	//select what axis to rotate around
	if(key == 'q'){//x-axis
		x_rot = true;
		y_rot = false;
		z_rot = false;
//		rotate_y = 0; //resets teapot to original position
//		rotate_z = 0;
	}
	else if(key == 'w'){//y-axis
		x_rot = false;
		y_rot = true;
		z_rot = false;
//		rotate_x = 0;
//		rotate_z = 0;
	}
	else if(key == 'e'){//z-axis
		x_rot = false;
		y_rot = false;
		z_rot = true;
//		rotate_x = 0;
//		rotate_y = 0;
	}

	//zoom camera in& out
	if(key == 'z'){//track in
		cam_dist += 1;
	}
	else if(key == 'x'){//track out
		cam_dist -=1;
	}

	//rotate light sources clockwise/anti-clock
	if(key == 'm'){//anti-clock
 		light_angle += PI/32;
	        if (light_angle > PI*2)
        		light_angle -= PI*2;

		light_angle2 += PI/32;
	        if (light_angle2 > PI*2)
        		light_angle2 -= PI*2;
        }
        else if(key == 'n'){//clockwise
 		light_angle -= PI/32;
	        if (light_angle > PI*2)
        		light_angle += PI*2;

		light_angle2 -= PI/32;
	        if (light_angle2 > PI*2)
        		light_angle2 += PI*2;
        }
}

//handle built in key strokes
void handle_special_keys(int key, int x,int y){
	usleep(100);

	//move camera around
	switch(key){
	case GLUT_KEY_DOWN://track down
		yrot -= 1;
		break;
	case GLUT_KEY_UP://track up
		yrot += 1;
		break;
	case GLUT_KEY_LEFT://pan left
		xrot -=1;
		break;
	case GLUT_KEY_RIGHT://pan right
		xrot += 1;
		break;
	}
}

//initialize the GL window and perspective
void setup_window(int width,int height){
	glViewport(0,0,width,height);//set current view-port
	glMatrixMode(GL_PROJECTION);
	glLoadIdentity();//reset projection matrix
	gluPerspective(45.0,(float)width/height,0.1f,100.0f);
	glMatrixMode(GL_MODELVIEW);
}

//setup shader program here, with both vertex and fragment shader
void setShaders(){
	char *vertexShader,*fragmentShader;

	vertex = glCreateShader(GL_VERTEX_SHADER);
	fragment = glCreateShader(GL_FRAGMENT_SHADER);

	vertexShader = textFileRead("vertexShader");
	fragmentShader = textFileRead("fragmentShader");

	const char * vv = vertexShader;
	const char * ff = fragmentShader;

	glShaderSource(vertex,1,&vv,NULL);
	glShaderSource(fragment,1,&ff,NULL);

	free(vertexShader);free(fragmentShader);

	glCompileShader(vertex);
	glCompileShader(fragment);

	program = glCreateProgram();

	glAttachShader(program,vertex);
	glAttachShader(program,fragment);

	glLinkProgram(program);
	glUseProgram(program);
	std::cout << "Shaders configured..." << std::endl;
}

//initialize the GUI and start the main loop
int main(int argc,char** argv){
	glutInit(&argc,argv);
	glutInitDisplayMode(GLUT_RGBA | GLUT_DOUBLE | GLUT_ALPHA | GLUT_DEPTH);
	glutInitWindowSize(window_width,window_height);
	glutCreateWindow("GLUT TeaPot Rotation, with shaders...");//create window

	glEnable(GL_DEPTH_TEST);

	//event functions
	glutDisplayFunc(draw_loop);
	glutIdleFunc(draw_loop);//continuous draw and update function
	glutReshapeFunc(setup_window);//initialize window and perspective
	glutKeyboardFunc(&handle_controls);//handle keyboard input
	glutSpecialFunc(&handle_special_keys);//handle special key input
	
	glShadeModel(GL_SMOOTH);
	glClearColor(0.0f,0.0f,0.0f,0.0f); //black background
	glClearDepth(1.0f);
	glDepthFunc(GL_LEQUAL);
//	glEnable(GL_CULL_FACE);

	glewInit();
	if(GLEW_ARB_vertex_shader && GLEW_ARB_fragment_shader)
		std::cout << "Ready for GLSL\n";
	else{
		std::cout << "Not totally ready \n";
		exit(1);
	}
	if(glewIsSupported("GL_VERSION_2_0"))
		std::cout << "Ready for OpenGL 2.0\n";
	else{
		std::cout << "OpenGL 2.0 not supported\n";
		exit(1);
	}

	//loadTextures into memory
	texture = load_Textures("surface.jpg");
	bumpMap = load_Textures("bump.jpg");
	std::cout << "Textures loaded..." << std::endl;

	setShaders();

	//set texture data
	shader_tex = glGetUniformLocation(program, "texture");
	shader_bump = glGetUniformLocation(program, "bumpTexture");

	glutMainLoop();//start GL main loop

	return 1;
}


