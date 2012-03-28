package sully.vc.Special_effects;

import static core.Script.*;

import java.awt.Color;
import domain.VImage;

/****************************************************
*****Blur Library v1.5 by Tristan Michael(ragecage)**********
*************completed: sept 1, 2004********************
****************************************************/
public class Blur {
	
	public static final int BLUR_ITERATIONS = 6;
	public static final int BLUR_DLY = 5;
	
	VImage blur_buffer[] = new VImage[BLUR_ITERATIONS];
	int blur_stamp;
	
	void loadBlurBuffer(){
		int i;
		for(i=0;i<BLUR_ITERATIONS;i++){
			blur_buffer[i]=new VImage(imagewidth(screen),imageheight(screen));
		}
	}
	
	/*void freeBlurBuffer(){
		int i;
		for(i=0;i<BLUR_ITERATIONS;i++){
			//freeImage(blur_buffer[i]);
		}
	}*/
	
	void renderMotionBlur(){
		int i;
	//screen buffering
		if(timer>blur_stamp+BLUR_DLY){
			blur_stamp=timer;     
			for(i=1;i<BLUR_ITERATIONS;i++){
				blit(0,0,blur_buffer[i],blur_buffer[i-1]);
			}
			blit(0,0,screen,blur_buffer[i-1]);
		}
	//blur blitting
		for(i=0;i<BLUR_ITERATIONS-1;i++){
			setlucent(100/(BLUR_ITERATIONS-i));
			blit(0,0,blur_buffer[i],screen);
		}
		setlucent(0);
	}
	
	void focalBlur(int iterations, int distance, VImage src){
		int i;
		VImage tempImage=new VImage(imagewidth(src),imageheight(src));
		blit(0,0,src,tempImage);
		
		if(iterations >= distance) iterations=distance-1;
		
		for(i=1;i<=iterations;i++){  
			setlucent(100/iterations *i);
			tscaleblit(0-(distance/iterations *i /2), 
				0-(distance/iterations*i /2), 
				imagewidth(tempImage)+(distance/iterations*i), 
				imageheight(tempImage)+(distance/iterations*i), 
				tempImage, src);
		}
		setlucent(0);
		//freeImage(tempImage);
	}
	
	void xyBlur(int x, int y, int iterations, int distance, VImage src){
		int i;
	
	//check for offscreen extremes
		if(x > imagewidth(screen)) x=imageheight(screen);
		if(y > imageheight(screen)) y=imageheight(screen);
		if(x < 0) x=0;
		if(y < 0) y=0;
	
	//grab focus region
		VImage tempImage=new VImage(imagewidth(screen)*2, imageheight(screen)*2);
		grabregion(x-(imagewidth(tempImage)/2), y-(imageheight(tempImage)/2), x+(imagewidth(tempImage)/2), y+(imageheight(tempImage)/2),0,0,src, tempImage);
		
	//Make sure that the iterations does not exceed the distance
		if(iterations >= distance) iterations=distance-1;
	
	//streach focus region to create blur
		for(i=1;i<=iterations;i++){  
			setlucent(100/iterations *i);
			scaleblit(		x - (imagewidth(tempImage)/2)-(distance/iterations *i /2), 
						y - (imageheight(tempImage)/2)-(distance/iterations *i /2), 
						imagewidth(tempImage)+(distance/iterations*i), 
						imageheight(tempImage)+(distance/iterations*i), 
						tempImage, src);
		}
		
	//end of function
		setlucent(0);
		////freeImage(tempImage);
	}
	
	static void radialBlur(int iterations, int distance, VImage src){
		int i;
		VImage tempImage=new VImage(imagewidth(src),imageheight(src));
		blit(0,0,src,tempImage);
		
		for(i=0;i<iterations;i++){
			setlucent(100/iterations *i);
			rotscale(imagewidth(src)/2, imageheight(src)/2, distance/(iterations-i), 1000, tempImage, src);
		}
		setlucent(0);
		////freeImage(tempImage);
	}
	
	void crossBlur(int iterations, int distance, VImage src){
		int i;
		VImage tempImage=new VImage(imagewidth(src),imageheight(src));
		blit(0,0,src,tempImage);
		
		if(iterations >= distance) iterations=distance-1;
		
		for(i=1;i<=iterations;i++){  
			setlucent(100/iterations *i);
			blit(0-(distance/iterations *i), 0-(distance/iterations *i), tempImage, src);
		}
		setlucent(0);
		//freeImage(tempImage);
	}
	
	void imageBlur(int iterations, int distance, VImage src){
		int i;
		VImage tempImage=new VImage(imagewidth(src),imageheight(src));
		blit(0,0,src,tempImage);
		
		if(iterations >= distance) iterations=distance-1;
		
		for(i=1;i<=iterations;i++){  
			setlucent(100/iterations *i);
			blit(0-(distance/iterations *i), 0, tempImage, src);
		}
		setlucent(0);
		blit(0,0,src,tempImage);
		for(i=1;i<=iterations;i++){  
			setlucent(100/iterations *i);
			blit(0, 0-(distance/iterations *i), tempImage, src);
		}
		setlucent(0);
		//freeImage(tempImage);
	}
}