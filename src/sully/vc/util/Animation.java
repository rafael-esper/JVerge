package sully.vc.util;

import static core.Script.*;
import domain.VImage;

/***************************************
 * animation.vc                        *
 * author: vecna                       *
 * a quick utility library to simplify *
 * using an 'animation-strip' style    *
 * image in animations.                *
 ***************************************/

public class Animation {
	
	/**************************** data ****************************/
	
	public static final int MAX_ANIMATIONS = 16;	// Tweak this to your liking!
	public static final int NO_INDEX	= 65535;	// This is just a sentinel.
	
	int sizex, sizey;	// the width andh height of a single frame of this animation.
	int numframes;		// the total framecount of this animation
	boolean active;		// the status of this animation (false means the slot is free for plundering!)
	VImage image;			// the reference to the base image data.
	
	VImage bufimage;		// a new image one frame in size that is a copy of the current frame.
	int bufferedframe;	//.the index of the presently buffered frame.  65535 is a sentinel.
	
	static Animation anims[] = new Animation[MAX_ANIMATIONS];
	
	/**************************** code ****************************/
	
	
	// Loads an animation from a 'filmstrip'-style file.
	// it auto-calculates how many frames there are based on the overall image size
	// and the specific frame size you specify.  This does not allow for whitespace 
	// in the base image.
	//
	// returns an anims[] index to be used in other functions in this library.
	public static int LoadAnimation(String filename, int sizex, int sizey)
	{
		int i, index;
		
		// find a free animation slot thingy
		index = NO_INDEX;
		for (i=0; i<MAX_ANIMATIONS; i++) {
			if ((anims[i]==null || !anims[i].active) && i<index) {
				anims[i] = new Animation();
				index = i;
			}
		}
	
		// exit if none available
		if (index == NO_INDEX) {
			exit("LoadAnimation: No free slots available! Increase MAX_ANIMATIONS!");
		}
	
		// load up this animation, detect number of frames
		anims[index].image = new VImage(load(filename));
		anims[index].active = true;
		anims[index].sizex = sizex;
		anims[index].sizey = sizey;
		anims[index].numframes = imageheight(anims[index].image) / sizey;
		anims[index].bufimage =  new VImage(sizex, sizey);
		anims[index].bufferedframe = 65535;
		return index;
	}
	
	// Takes an anims[] index.
	// 
	// frees that slot of the anims[] array, releasing it's data back into the 
	// wild.
	void FreeAnimation(int index)
	{
		if (!anims[index].active)
		{
			log("FreeAnimation() - Requested index is not in use!!!");
			return; 
		}
		anims[index].image = null;
		anims[index].bufimage = null;
		anims[index].image = null;
		anims[index].sizex = 0;
		anims[index].sizey = 0;
		anims[index].numframes = 0;
		anims[index].active = false;
		anims[index].bufimage = null;
		anims[index].bufferedframe = 65535;
	}
	
	// Blits the specified frame of the specified animation to (x,y) of Dest.
	//
	// This function buffers the frame the first time it blits a new frame for an
	// image, and continues to blit that buffered image as long as the frame 
	// doesn't change.
	//
	// Fatally errors if you attempt to render from an inactive animation.
	// quietly does nothing if the frame index was invalid for this animation
	public static void BlitFrame(int x, int y, int anim, int frame, VImage dest)
	{
		int frametop;
	
		if (!anims[anim].active)
			exit("BlitFrame() - Requested animation is empty!!");
	
		if (frame > anims[anim].numframes)
			return;
			
		if (frame < 0)
			return;
			
		if (frame == anims[anim].bufferedframe)
		{
			blit(x, y, anims[anim].bufimage, dest);
			return;
		}
				
		frametop = anims[anim].sizey * frame;
		grabregion(0, frametop, anims[anim].sizex-1, frametop+anims[anim].sizey-1, 0, 0, anims[anim].image, anims[anim].bufimage);
		anims[anim].bufferedframe = frame;
		blit(x, y, anims[anim].bufimage, dest);
	}
	
	// TBlits the specified frame of the specified animation to (x,y) of Dest.
	//
	// This function buffers the frame the first time it blits a new frame for an
	// image, and continues to blit that buffered image as long as the frame 
	// doesn't change.
	//
	// Fatally errors if you attempt to render from an inactive animation.
	// quietly does nothing if the frame index was invalid for this animation
	public static void TBlitFrame(int x, int y, int anim, int frame, VImage dest)
	{
		int frametop;
	
		if (!anims[anim].active)
			exit("TBlitFrame() - Requested animation is empty!!");
	
		if (frame > anims[anim].numframes)
			return;
			
		if (frame < 0)
			return;
			
		if (frame == anims[anim].bufferedframe)
		{
			tblit(x, y, anims[anim].bufimage, dest);
			return;
		}
				
		frametop = anims[anim].sizey * frame;
		grabregion(0, frametop, anims[anim].sizex-1, frametop+anims[anim].sizey-1, 0, 0, anims[anim].image, anims[anim].bufimage);
		anims[anim].bufferedframe = frame;
		tblit(x, y, anims[anim].bufimage, dest);
	}
	
	
	// Transparently draws frame #'frame of animation 'anim' onto 'dest' with the frame centered on (x,y)
	// 'dest' must be a valid v3 image reference.
	// 'anim' must be a valid animation index, does nothing if invalid.
	//
	// Fatally errors if you attempt to render from an inactive animation.
	// quietly does nothing if the frame index was invalid for this animation
	void BlitFrameAt(int x, int y, int anim, int frame, VImage dest)
	{
		int frametop;
	
		if (!anims[anim].active)
			exit("BlitFrameAt() - Requested animation is empty!!");
	
		if (frame > anims[anim].numframes)
			return;
			
		if (frame < 0)
			return;
	
		if (frame == anims[anim].bufferedframe)
		{
			tblit(x-(anims[anim].sizex/2), y-(anims[anim].sizey/2), anims[anim].bufimage, dest);
			return;
		}
				
		frametop = anims[anim].sizey * frame;
		grabregion(0, frametop, anims[anim].sizex-1, frametop+anims[anim].sizey-1, 0, 0, anims[anim].image, anims[anim].bufimage);
		anims[anim].bufferedframe = frame;
		tblit(x-(anims[anim].sizex/2),  y-(anims[anim].sizey/2), anims[anim].bufimage, dest);
		
	}
	
	// Transparently draws 'img' onto 'dest' with 'img' centered on (x,y)
	// 'img' and 'dest' must be valid v3 image references.
	void BlitAt(int x, int y, VImage img, VImage dest)
	{
		tblit(x-(imagewidth(img)/2), y-(imagewidth(img)/2), img, dest);
	}

}