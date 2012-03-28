package sully.vc.Special_effects;

import static core.Script.*;

import static sully.vc.Special_effects.Blur.radialBlur;
import java.awt.Color;
import domain.VImage;

public class Funk_o_rama {

	int speed;
	int delay;
	Color color;
	int radius;
	int stamp;
	int x;
	int y;

	static Funk_o_rama disco[] = new Funk_o_rama[3];
	
	public static void loadFunkOrama(){
		loadColors();
		loadDisco(0);
		loadDisco(1);
		loadDisco(2);
		hookretrace("funkOrama");
	}
	
	
	public static void funkOrama() {
	
	/*	
		int i = Random(1,12);
		
		if( i <= 6 )
		{
			ColorFilter(i, screen);
		}
		else
		{
			ColorFilter(0, screen);
		}
	*/	
		
		borderGlow();
		processDisco(0);
		processDisco(1);
		processDisco(2);
		radialBlur(2,timer/2,screen);
		radialBlur(2,0-timer/2,screen);
	}
	
	
	static void loadDisco(int index){
		disco[index] = new Funk_o_rama();
		disco[index].delay=random(1,3);
		disco[index].speed=random(5,20);
		disco[index].color=funk_c[random(0,2)];
		disco[index].radius=random(10,100);
		disco[index].y=0-disco[index].radius;
		disco[index].x=random(0,320);
		disco[index].stamp=timer;
	}
	
	static void processDisco(int index){
		setlucent(75);
		circlefill(disco[index].x,disco[index].y,disco[index].radius,disco[index].radius,disco[index].color,screen);
		setlucent(0);
		
		if(timer-disco[index].stamp >= disco[index].delay){
			disco[index].y+=disco[index].speed;
			disco[index].stamp=timer;
		}
		
		if(disco[index].y > 240+disco[index].radius){
			loadDisco(index);
		}
	}
	
	static VImage borderMask=new VImage(load("res/system/special_effects/borderGlow.png"));
	static int borderStamp;
	static int mixnum;
	static Color funk_c[] = new Color[4];
	static Color mixC[] = new Color[2];

	static void borderGlow(){
		VImage scrn= new VImage(320,240);
		
		if(timer-borderStamp >= 1){
			borderStamp=timer;
			mixnum+=7;
		}
		if(mixnum >= 255){
			mixnum=0;
			funk_c[3]=funk_c[0];
			funk_c[0]=funk_c[1];
			funk_c[1]=funk_c[2];
			funk_c[2]=funk_c[3];
			
			mixC[0]=funk_c[0];
			mixC[1]=funk_c[1];
		}
		
		rectfill(0,0,320,240,mixcolor(mixC[0], mixC[1], mixnum),scrn);
		alphablit(0,0,scrn,borderMask,screen);
		//freeImage(scrn);
	}
	
	static void loadColors(){
		funk_c[0]=RGB(255,0,0);
		funk_c[1]=RGB(0,255,0);
		funk_c[2]=RGB(0,0,255);
		
		mixC[0]=funk_c[0];
		mixC[1]=funk_c[1];
	}

}