package domain;

import static core.Script.*;

import java.net.MalformedURLException;
import java.net.URL;


/// The VERGE 3 Project is originally by Ben Eirich and is made available via
///  the BSD License.
///
/// Please see LICENSE in the project's root directory for the text of the
/// licensing agreement.  The CREDITS file in the same directory enumerates the
/// folks involved in this public build.
///
/// If you have altered this source file, please log your name, date, and what
/// changes you made below this line.


/****************************************************************
	xerxes engine
	g_font.cpp
****************************************************************/

public class VFont {

	VImage rawdata[];
	public int width, height;
	int subsets, selected;
	int totalframes;
	int fwidth[] = new int[100];
	boolean incolor;

	public VFont(URL url, int xsize, int ysize)
	{
		width = xsize;
		height = ysize;

		readFont(url, xsize, ysize);
	}
	
	public VFont(URL url)
	{
		int w, h;
		
		// this constructor autodetected cell dimensions, B
		VImage workingimage = new VImage(url);

		// Analyze image and guess cell dimensions.
		int bgcolor = readpixel(0, 0, workingimage);   // This is the image bg color;
		for (w=1; w<workingimage.width; w++)
		{
			int z = readpixel(w, 1, workingimage);
			if (z == bgcolor)
				break;
		}
		for (h=1; h<workingimage.height; h++)
		{
			int z = readpixel(1, h, workingimage);
			if (z == bgcolor)
				break;
		}
		width = w-1;
		height = h-1;
		readFont(url, width, height);
	}

	private void readFont(URL url, int xsize, int ysize) {
		VImage workingimage = new VImage(url);
		subsets = workingimage.width / ((ysize*5)+4);
		selected = 0;
		incolor = false;

		rawdata = new VImage[100*subsets];
		int imageindex = 0;
		for (int yl = 0; yl<5 * subsets; yl++)
			for (int xl = 0; xl<20; xl++) {
				rawdata[imageindex] = new VImage(xsize, ysize);
				grabregion(1+(xl*(xsize+1)), 1+(yl*(ysize+1)), width+1+(xl*(xsize+1)), height+1+(yl*(ysize+1)),
					0, 0, workingimage, rawdata[imageindex++]);
			}

		for (int i=0; i<100; i++)
			fwidth[i] = xsize; // + 1 (commented by [Rafael, the Esper])

	}
	
	boolean ColumnEmpty(int cell, int column, int tcolor)
	{
		//container.data = ((int) rawdata.data + ((cell)*width*height*vid_bytesperpixel));
		for (int y=0; y<rawdata[cell].height; y++)
			if (readpixel(column, y, rawdata[cell]) != tcolor)
				return false;
		return true;
	}

	public void EnableVariableWidth()
	{
		fwidth[0] = width * 60 / 100;
		int tcolor = readpixel(0, 0, rawdata[0]);
		for (int i=1; i<100; i++)
		{
			fwidth[i] = -1;
			for (int x=width-1; x>=0; x--)
			{
				if (!ColumnEmpty(i, x, tcolor))
				{
					fwidth[i] = x + 1;
					break;
				}
			}
			if (fwidth[i]==-1) 
				fwidth[i] = width * 60 / 100;
			//System.out.println(((char)(i+32)) + " " + fwidth[i]);
		}
	}

	void SetCharacterWidth(int character, int width)
	{
		fwidth[character] = width;
	}

	public void PrintChar(char c, int x, int y, VImage dest)
	{
		if (c<32 || c>=128) 
			return;  
		
		tblit(x, y, rawdata[c-32].image, dest.getImage());
		//dest.g.drawImage(rawdata[c-32].image, x, y, null);
		//TBlit(x, y, container, dest);
	}

	// print a chunk of a string; doesn't care about newlines
	// called from PrintString, PrintCenter, and PrintRight, which DO care about newlines
	public void PrintLine(String s, int x, int y, VImage dest)
	{
		for (int pos=0; pos < s.length(); pos++)
		{
			if (s.charAt(pos) == '\f')
			{
				/*
				// what's wrong with just using \f0?
				if (incolor)
				{
					selected = 0;
					incolor = false;
					continue;
				}
				*/
				selected = s.charAt(pos) - '0';
				if (selected >= subsets || selected < 0)
					selected = 0;
				else
					incolor = true;
				continue;
			}
			PrintChar(s.charAt(pos), x, y, dest);
			if (s.charAt(pos) < 32) continue;
			x += fwidth[s.charAt(pos) - 32]+1;
		}
	}

	public void PrintString(String msg, int x, int y, VImage dest)
	{
		int start = 0, end = 0;

	    for (end = 0; end < msg.length(); end++)
		{
			if (msg.charAt(end) == '\n' || msg.charAt(end) == '\r')
			{
				PrintLine(msg.substring(0, end),x,y,dest);

				// Check for \r\n so they aren't parsed as two separate line breaks.
				if (msg.charAt(end) == '\r' && msg.length() > end+1 && msg.charAt(end+1) == '\n')
					end++;
				start = end + 1;

				y += height;
			}
		}
		PrintLine(msg.substring(start, end),x,y,dest);
	}

	public void PrintRight(String msg, int x, int y, VImage dest)
	{
		int xsize = 0;
		int start = 0, end = 0;

		if(msg==null)
			return;
		
	    for (end = 0; end < msg.length(); end++)
		{
			if (msg.charAt(end) == '\n' || msg.charAt(end) == '\r')
			{
				xsize = Pixels(msg.substring(0, end));
				PrintLine(msg.substring(0, end),x - (xsize),y,dest);

				// Check for \r\n so they aren't parsed as two separate line breaks.
				if (msg.charAt(end) == '\r' && msg.length() > end+1 && msg.charAt(end+1) == '\n')
					end++;
				start = end + 1;

				y += height;
			}
		}
		xsize = Pixels(msg.substring(start, end));
		PrintLine(msg.substring(start, end),x - xsize,y,dest);

		selected=0;
		incolor = false;
	}

	public void PrintCenter(String msg, int x, int y, VImage dest)
	{
		int xsize = 0;
		int start = 0, end = 0;

	    for (end = 0; end < msg.length(); end++)
		{
			if (msg.charAt(end) == '\n' || msg.charAt(end) == '\r')
			{
				xsize = Pixels(msg.substring(0, end));
				PrintLine(msg.substring(0, end),x - (xsize/2),y,dest);

				// Check for \r\n so they aren't parsed as two separate line breaks.
				if (msg.charAt(end) == '\r' && msg.length() > end+1 && msg.charAt(end+1) == '\n')
					end++;
				start = end + 1;

				y += height;
			}
		}
		xsize = Pixels(msg.substring(start, end));
		PrintLine(msg.substring(start, end),x - (xsize/2),y,dest);

		selected=0;
		incolor = false;
	}

	public int Pixels(String str)
	{
		int xsize = 0;
		boolean ic = incolor;

	    for (int i=0; i<str.length(); i++)
		{
			if (str.charAt(i) == '\f')
			{
				/*
				// what's wrong with just using \f0?
				if (incolor)
				{
					incolor = false;
					continue;
				}
				*/
				if (i+1 > str.length()) break;
				incolor = true;
				continue;
			}
			else
			{
				if (str.charAt(i) < 32) continue;
				xsize += fwidth[str.charAt(i) - 32]+1;
			}
		}

		incolor = ic; // reset the incolor flag
		return xsize;
	}
}
