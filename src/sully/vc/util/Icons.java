package sully.vc.util;

import static core.Script.*;

import java.awt.font.ImageGraphicAttribute;

import domain.VImage;

public class Icons {
	
	//
	// Grue's Icon library, mark I.
	//
	// -I'd like to add multiple icon sourcefiles, variable sized icons, and other neat things 
	// in the future, but in all honesty I'll probably only finish the basic library.
	//
	// To the intrested, feel free to flesh this out into something more useful, and many cookies
	// shall be yours.
	//
	
	// extracts the first dimension of a flat-indexed 2 dimensionally array given 
	// the second dimension's maximum value and the value of the flat index you
	// wish to extract the first dimension's value from.
	//
	static int x_from_flat( int flatval, int yMax ) {
		return flatval%yMax;
	}
	
	// extracts the second dimension of a flat-indexed 2 dimensionally array given 
	// the second dimension's maximum value and the value of the flat index you
	// wish to extract the second dimension's value from.
	//
	static int y_from_flat( int flatval, int yMax ) {
		flatval = flatval - x_from_flat( flatval,yMax );
		return flatval/yMax;
	}
	
	public static final int ICON_WIDTH	= 16;
	public static final int ICON_HEIGHT	= 16;
	
	static VImage im_iconsource;
	static int _icon_rows;
	static int _icon_cols;
	static int _icon_max;
	
	// returns the number of icons that live inside the given source image.
	int icon_max( VImage src_img ) {
		return (imagewidth(src_img)/(ICON_WIDTH+1))*(imageheight(src_img)/(ICON_HEIGHT+1));
	}
	
	// Initializes the icon library.
	//
	public static void icon_init( String src_file ) {
		im_iconsource = new VImage(load(src_file));
		
		_icon_cols	= imagewidth(im_iconsource)/(ICON_WIDTH+1);
		_icon_rows	= imageheight(im_iconsource)/(ICON_HEIGHT+1);
		_icon_max	= _icon_rows * _icon_cols;
	}
	
	// Returns an imageshell reference to the specified icon.
	// does not do bounds checking.
	// 
	// remember to FreeImage() the imageshell reference when you are done with it.
	public static VImage icon_get( int idx ) {
		int x, y;
		
		x = x_from_flat( idx, _icon_cols );
		y = y_from_flat( idx, _icon_cols );
		
		x = (x*(ICON_WIDTH+1)) +1;
		y = (y*(ICON_HEIGHT+1)) +1;
			
		// Rbp
		VImage dst = new VImage(ICON_WIDTH, ICON_HEIGHT);
		grabregion(x, y, x+ICON_WIDTH, y+ICON_HEIGHT, 0, 0, im_iconsource, dst);
		return dst;
		//return imageshell(x, y, ICON_WIDTH, ICON_HEIGHT, im_iconsource );
	}
}