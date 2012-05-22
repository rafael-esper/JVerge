package domain;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.SystemMenuBar;

import core.DefaultPalette;
import static core.Script.*;

import persist.ExtendedDataInputStream;
import persist.ExtendedDataOutputStream;

public class CHR {

	public final static String  CHR_SIGNATURE	=	"5392451";
	public final static int  CHR_VERSION5	=		5;
	public final static int  CHR_VERSION4	=		4;
	public final static int  CHR_VERSION2	=		2;
	
	
	//private byte[] pixels = new byte[16*16*3]; // frames * width * height * 3 bytes!
	
	int fxsize, fysize;					// frame x/y dimensions
	public int hx, hy;						// x/y obstruction hotspot
	public int hw;							// hotspot width/height
	public int hh;
	int totalframes;					// total # of frames.
    int idle[] = new int[5];			// idle frames

	int animsize[] = new int[9];
	int anims[][] = new int[9][];
    //String movescript[] = new String[8];

	String filename;                        // the filename this was loaded from
	
	// [Rafael, the Esper]
	public BufferedImage [] frames;
	
	
	public CHR(String strFilename) {
		this(load(strFilename.replace('\\', '/')));
	}
	
	public CHR(URL url) {
			try {
				if(url==null)
					return;
				
				this.filename =  url.getFile().substring( url.getFile().lastIndexOf('/')+1);
				this.loadChr(url.openStream());
				//FileInputStream fis = new FileInputStream(path + "\\" + filename);
				//this.loadChr(fis);
				
			} catch (IOException ioe) {
				System.err.println("CHR::IOException (" + filename + "), url = " + url);
			} catch (Exception e) {		
				System.err.println("CHR::Exception ( " + e + "). When loading " + url);
				System.exit(-1);
			}
	}

	public CHR() { }
	
	private void loadChr (InputStream is) {

		try    {

			ExtendedDataInputStream f = new ExtendedDataInputStream(is);
			
			Integer first = f.readUnsignedByte();
			
			int version = 0;
			if(first == CHR_VERSION4) { // Version CHR4
				version = CHR_VERSION4;
				System.out.println("CHR: Reading version " + version);
				this.loadChrVersion4(f);
			}
			else if(first == CHR_VERSION2) { // Version CHR2
				version = CHR_VERSION2;
				System.out.println("CHR: Reading version " + version);
				this.loadChrVersion2(f);
			}
			else if(first == 67 || first == CHR_VERSION5) { // letter 'c'
					f.readUnsignedByte(); // 'H'
					f.readUnsignedByte(); // 'R'
					f.readUnsignedByte(); // '\0'
					version = f.readUnsignedIntegerLittleEndian();
					System.out.println("CHR: Reading version " + version);
					this.loadChrVersion5(f);
			}
			else {
				System.err.println("Version " + version + " not supported.");
				System.exit(-1);
			}
						
			f.close();
			
		 } catch (IOException e) {
			 System.out.println("IOException : " + e);
			 e.printStackTrace();
		 }
	}

	// Based on: chr_file.cpp (vopenchr)
	private void loadChrVersion2(ExtendedDataInputStream f) throws IOException {

		this.fxsize = f.readUnsignedShortLittleEndian();
		this.fysize = f.readUnsignedShortLittleEndian();

		System.out.println(this.fxsize + " " + this.fysize);
		
		this.hx = f.readUnsignedShortLittleEndian();
		this.hy = f.readUnsignedShortLittleEndian();
		this.hw = f.readUnsignedShortLittleEndian();
		this.hh = f.readUnsignedShortLittleEndian();
		
		System.out.println(this.hx + " " + this.hy);
		
		this.totalframes = f.readUnsignedShortLittleEndian();
		System.out.println(this.totalframes);
		String animbuf;
		
		// Pixels
		int bufsize = f.readSignedIntegerLittleEndian();
		int [] data = new int[bufsize];
		for(int i=0; i<bufsize; i++) {
			data[i] = f.readUnsignedByte();
		}
	
		int pTemp[] = new int[fxsize*fysize*totalframes];
		ReadRLE1(pTemp, fxsize * fysize * totalframes, data);

		frames = new BufferedImage[totalframes];
		WritableRaster wr;
		DefaultPalette pal = new DefaultPalette();
		for (int t=0; t<totalframes; t++) //the current frame
		{
			frames[t] = new BufferedImage(fxsize, fysize, BufferedImage.TYPE_INT_ARGB);
			wr = frames[t].getRaster();
			
			int pixels[][]=new int[fxsize*fysize][4];
			for (int x = 0; x < fxsize * fysize; x++)
			{
				int u = pTemp[t * fxsize * fysize + x];
				pixels[x][0] = pal.getDefaultPaletteRedColor(u);; // red
				pixels[x][1] = pal.getDefaultPaletteGreenColor(u);; // green
				pixels[x][2] = pal.getDefaultPaletteBlueColor(u);; // blue
				pixels[x][3] = 255;
				if(u==0) // transparent
					pixels[x][3] = 0;
				wr.setPixel(x%fxsize, (int)Math.floor(x/fxsize), pixels[x]);
				//System.out.printf("x:%d y:%d pix:(%d,%d,%d)", x%fxsize, (int)Math.floor(x/fxsize), pixels[x][0], pixels[x][1], pixels[x][2]);
			}
		}

		this.idle[Entity.WEST] = f.readSignedIntegerLittleEndian();
		this.idle[Entity.EAST] = f.readSignedIntegerLittleEndian();
		this.idle[Entity.NORTH] = f.readSignedIntegerLittleEndian();
		this.idle[Entity.SOUTH] = f.readSignedIntegerLittleEndian();

		// Creates an array with size equal to the total "wait" time of the animation
		// Each index in the anims array points to a frame
		// So a F1W5F2W5 will insert in the array the values 1 1 1 1 1 2 2 2 2 2
		int indexes[] = { 0, 3, 4, 1, 2};
		for(int b=1; b<=4; b++) {
			int length = f.readSignedIntegerLittleEndian(); // animation length
			animbuf = f.readFixedString(length);
			this.animsize[indexes[b]] = this.GetAnimLength(animbuf);
			this.anims[indexes[b]] = new int[this.animsize[indexes[b]]];
			this.ParseAnimation(indexes[b], animbuf);
		}
	}	
	
	// Based on: chr_file.cpp (vopenchr)
	private void loadChrVersion4(ExtendedDataInputStream f) throws IOException {

		this.fxsize = f.readUnsignedShortLittleEndian();
		this.fysize = f.readUnsignedShortLittleEndian();

		System.out.println(this.fxsize + " " + this.fysize);
		
		this.hx = f.readUnsignedShortLittleEndian();
		this.hy = f.readUnsignedShortLittleEndian();
		this.hw = f.readUnsignedShortLittleEndian();
		this.hh = f.readUnsignedShortLittleEndian();
		
		System.out.println(this.hx + " " + this.hy);
		
		this.idle[Entity.WEST] = f.readUnsignedShortLittleEndian();
		this.idle[Entity.EAST] = f.readUnsignedShortLittleEndian();
		this.idle[Entity.NORTH] = f.readUnsignedShortLittleEndian();
		this.idle[Entity.SOUTH] = f.readUnsignedShortLittleEndian();

		this.totalframes = f.readUnsignedShortLittleEndian();
		System.out.println(this.totalframes);
		String animbuf;
		
		// Creates an array with size equal to the total "wait" time of the animation
		// Each index in the anims array points to a frame
		// So a F1W5F2W5 will insert in the array the values 1 1 1 1 1 2 2 2 2 2
		int indexes[] = { 0, 3, 4, 1, 2};
		for(int b=1; b<=4; b++) {
			int length = f.readSignedIntegerLittleEndian(); // animation length
			animbuf = f.readFixedString(length);
			this.animsize[indexes[b]] = this.GetAnimLength(animbuf);
			this.anims[indexes[b]] = new int[this.animsize[indexes[b]]];
			this.ParseAnimation(indexes[b], animbuf);
		}
		
		// Pixels
		int bufsize = f.readSignedIntegerLittleEndian();
		int [] data = new int[bufsize/2];
		for(int i=0; i<bufsize/2; i++) {
			//data[i+1] = f.readUnsignedByte();
			//data[i] = f.readUnsignedByte();
			//data[i] = f.readUnsignedShort();
			data[i] = f.readUnsignedShortLittleEndian();
		}
	
		int pTemp[] = new int[fxsize*fysize*totalframes*2];
		ReadRLE2(pTemp, fxsize * fysize * totalframes, data);

		frames = new BufferedImage[totalframes];
		WritableRaster wr;
		for (int t=0; t<totalframes; t++) //the current frame
		{
			frames[t] = new BufferedImage(fxsize, fysize, BufferedImage.TYPE_INT_ARGB);
			wr = frames[t].getRaster();
			
			int pixels[][]=new int[fxsize*fysize][4];
			for (int x = 0; x < fxsize * fysize; x++)
			{ //System.out.println(x);
				
				int u = Short.reverseBytes((short) pTemp[t * fxsize * fysize + x]) & 0xffff;
				pixels[x][0] = ((u >> 11) & 31) << 3; // red
				pixels[x][1] = ((u >> 5) & 63) << 2; // green
				pixels[x][2] = (u & 31) << 3; // blue
				pixels[x][3] = 255;
				if(pixels[x][0] == 248 && pixels[x][1] == 0 && pixels[x][2]==248)
				{
					pixels[x][0]=255;
					pixels[x][1]=0;
					pixels[x][2]=255;
					pixels[x][3]=0;
				}
				wr.setPixel(x%fxsize, (int)Math.floor(x/fxsize), pixels[x]);
				System.out.printf("x:%d y:%d pix:(%d,%d,%d)", x%fxsize, (int)Math.floor(x/fxsize), pixels[x][0], pixels[x][1], pixels[x][2]);
			}
		}
		
	}

	// Based on http://kenai.com/projects/tilem/sources/tilem-jsr-296/content/src/tilem/imageformats/PCXReader.java?rev=1

	void ReadRLE2(int dest[], int numwords, int src[])
	{
		
		/*for(int i: src) {
			System.out.println(i + "\t" + (i & 0x00FF) + "\t" + (i>>>8 & 0xff) + "\t" + ((i & 0xFF)));
		}
		System.exit(0);*/
		
		int pos = 0;
		int run = 0;
		int n = 0;
		while(pos < src.length-1) {
		
			if((src[pos] >>>8 & 0xff) == 255) {
				run = (src[pos] & 0x00FF);
				pos++;
				for(int j=0; j<run; j++) {
					//System.out.println("R: " + (src[pos] & 0x00FF));
					dest[n++] = src[pos] & 0x00FF;
				}
			}
			else {
				//System.out.println("U: " + (src[pos] & 0x00FF));
				dest[n++] = src[++pos] & 0x00FF;
			}
			
		}
		//System.exit(0);
		/*
		int mode = 1, nbytes=0;
		int abyte =0; int pos =0;

		for(int i = 0; i<numwords;i++) {
			
			if(mode == 1) {
				abyte = src[pos++];
				if(abyte > 191) {
					nbytes=abyte-192;
					abyte =(byte)(src[pos++]);
					if (--nbytes > 0) {
						mode = 2;
					}
				}
			}
			else if(--nbytes == 0) {
				mode = 1;
			}
			dest[i] = (int)(abyte);
			if(dest[i] < 0) dest[i] += 256;
			System.out.println(dest[i]);
			
		}*/
	}
		
		/*System.out.println(dest.length + "  " + src.length + " " + 0x00FF);

		int n = 0, run, w;
		int posbuf=0;
		do
		{
			w=src[posbuf++];
			System.out.println(w);
			if (w > 127) //(w & 0xFF00)==0xFF00)
			{
				run = w & 0x00FF;
				w=src[posbuf++];
				System.out.println("\tEspecial, run " + w + " for: " + run);
				for(int j = 0; j < run; j++)
					dest[n + j]= (short) w;
				n += run;
			}
			else
			{
				System.out.println("\tNormal: " + w);
				dest[n]= (short) w;
				n++;
			}
		} while(n < numwords);
	}	*/
	
	private void ReadRLE1(int dest[], int numbytes, int src[]) {
		int j, n = 0;
		int run;
		int w;
		int pos = 0;
		do
		{
			w=src[pos++];
			if (w==0xFF)
			{
				run=src[pos++];
				w=src[pos++];
				for (j = 0; j < run; j++)
					dest[n + j]=w;
				n += run;
			}
			else
			{
				dest[n]=w;
				n++;
			}
		} while (n < numbytes);		
		//for(int i: dest) {
			//System.out.println(i);
		//}
	}
	
	
	private String[] animbuf = new String[9];
	private int[] length = new int[9];
	private void loadChrVersion5(ExtendedDataInputStream f) throws IOException {

		f.readUnsignedIntegerLittleEndian(); // bitDepth
		f.readUnsignedIntegerLittleEndian(); // unused, poss. alpha blend
		
		// Transparent color
		f.readUnsignedByte(); // Red
		f.readUnsignedByte(); // Green
		f.readUnsignedByte(); // Blue
		f.readUnsignedByte(); // Alpha
		
		this.hx = f.readUnsignedIntegerLittleEndian();
		this.hy = f.readUnsignedIntegerLittleEndian();
		this.hw = f.readUnsignedIntegerLittleEndian();
		this.hh = f.readUnsignedIntegerLittleEndian();
		this.fxsize = f.readUnsignedIntegerLittleEndian();
		this.fysize = f.readUnsignedIntegerLittleEndian();
		this.totalframes = f.readSignedIntegerLittleEndian();
		
		System.out.println(this.hw+";"+this.hh+";"+this.hx+";"+this.hy);
		System.out.println(this.fxsize+";"+this.fysize+";"+this.totalframes);

		this.idle[Entity.SOUTH] = f.readSignedIntegerLittleEndian();
		this.idle[Entity.NORTH] = f.readSignedIntegerLittleEndian();
		this.idle[Entity.WEST] = f.readSignedIntegerLittleEndian();
		this.idle[Entity.EAST] = f.readSignedIntegerLittleEndian();
		
		int indexes[] = { 0, 2, 1, 3, 4, 5, 6, 7, 8 };
		
		// Creates an array with size equal to the total "wait" time of the animation
		// Each index in the anims array points to a frame
		// So a F1W5F2W5 will insert in the array the values 1 1 1 1 1 2 2 2 2 2
		for(int b=1; b<9; b++) {
			length[b] = f.readSignedIntegerLittleEndian(); // animation length
			System.out.println(length[b]);
			animbuf[b] = f.readFixedString(length[b]+1);
			System.out.println(animbuf[b]);
			this.animsize[indexes[b]] = this.GetAnimLength(animbuf[b]);
			if(this.animsize[indexes[b]] == 0)
				this.animsize[indexes[b]]=1; // [Rafael, the Esper]
			this.anims[indexes[b]] = new int[this.animsize[indexes[b]]];
			this.ParseAnimation(indexes[b], animbuf[b]);
		}
		
		// Pixels
		f.readSignedIntegerLittleEndian();
		//int uncompressedSize = f.readSignedIntegerLittleEndian();
		f.readSignedIntegerLittleEndian();
		byte pixels[] = f.readCompressedUnsignedShortsIntoBytes();
		
		// Get frames from the pixels array
		System.out.println("Frames (" + fxsize + ", " + fysize + "): " + totalframes);
		frames = f.getBufferedImageArrayFromPixels(pixels, totalframes, fxsize, fysize); 
		
	}

	private void saveChrVersion5(String filename) {

		System.out.println("CHR::save at " + filename);
		ExtendedDataOutputStream f = null;
		try {
			OutputStream os = new FileOutputStream(filename);
			f = new ExtendedDataOutputStream(os);
			
			f.writeFixedString("CHR", 3);
			f.writeInt(Integer.reverseBytes(5)); // version
				
			f.writeInt(Integer.reverseBytes(24)); // bitDepth		
			f.writeInt(Integer.reverseBytes(0)); // unused, poss. alpha blend
			
			// Transparent color
			f.writeUnsignedByte(255); // Red
			f.writeUnsignedByte(0); // Green
			f.writeUnsignedByte(255); // Blue
			f.writeUnsignedByte(0); // Alpha
			
			f.writeInt(Integer.reverseBytes(this.hx));
			f.writeInt(Integer.reverseBytes(this.hy));
			f.writeInt(Integer.reverseBytes(this.hw));
			f.writeInt(Integer.reverseBytes(this.hh));
			f.writeInt(Integer.reverseBytes(this.fxsize));
			f.writeInt(Integer.reverseBytes(this.fysize));
			f.writeSignedIntegerLittleEndian(this.totalframes);
			
			f.writeSignedIntegerLittleEndian(this.idle[Entity.SOUTH]);
			f.writeSignedIntegerLittleEndian(this.idle[Entity.NORTH]);
			f.writeSignedIntegerLittleEndian(this.idle[Entity.WEST]);
			f.writeSignedIntegerLittleEndian(this.idle[Entity.EAST]);
			
			for(int b=1; b<9; b++) {
				f.writeSignedIntegerLittleEndian(this.length[b]); // animation length
				f.writeFixedString(animbuf[b], length[b]+1); // +1?
			}
			
			f.writeSignedIntegerLittleEndian(0);
			f.writeSignedIntegerLittleEndian(0);
			
			// Pixels
			byte[] pixels = f.getPixelArrayFromFrames(frames, totalframes, fxsize, fysize);
			f.writeCompressedBytes(pixels);
			System.out.println("CHR::save concluded successfully.");
	
		}
		catch(IOException e) {
			System.err.println("CHR::save " + e.getMessage());
		}
		finally {
			try {
				f.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}	
	
	/// Method to make easier to export CHRs from images
	public void setAnimBufs(int[] lengths, String[] animbufs) {
		this.animbuf = animbufs;
		this.length = lengths;
	}
	
	
	public void render(int x, int y, int frame, VImage dest)
	{
		x -= hx;
		y -= hy;
		if (frame <0 || frame >= totalframes)
			System.err.printf("CHR::render(), frame requested is undefined (%d of %d)", frame, totalframes);
		
		dest.tblit(x, y, this.frames[frame]);
	}
	
	int GetFrame(int d, int framect)
	{
		if (d<1 || d>4) {
			System.err.printf("CHR::GetFrame() - invalid direction %d", d);
			return 0;
		}
		framect %= animsize[d];
		return anims[d][framect];
	}
	
	int GetFrameConst(int d, int framect)
	{
		if (d<1 || d>4)
			System.err.printf("CHR::GetFrame() - invalid direction %d", d);
		return anims[d][framect % animsize[d]];
	}
	
	void ParseAnimation(int d, String anim)
	{
		int frame=0, len, i, ofs=0;
		String parsestr = anim;
	
		int parsecount = 0;
		while (parsecount < parsestr.length())
		{
			switch (parsestr.charAt(parsecount))
			{
				case 'f':
				case 'F':
					parsecount++;
					frame = GetArg(parsestr.substring(parsecount));
					parsecount+=Integer.toString(frame).length();
					//System.out.println("Anim(F" + frame + "), resting " + parsestr.substring(parsecount));
					break;
				case 'w':
				case 'W':
					parsecount++;
					len = GetArg(parsestr.substring(parsecount));
					for (i=ofs; i<ofs+len; i++)
						this.anims[d][i] = frame;
					ofs += len;
					parsecount+=Integer.toString(len).length();
					//System.out.println("Anim(W" + len + "), resting " + parsestr.substring(parsecount));
					break;
				default:
					System.err.printf("CHR::ParseAnimation() - invalid animscript command! %c", parsestr.charAt(parsecount));
			}
		}
	}
	
	int GetAnimLength(String anim)
	{
		int length = 0;
		String parsestr = anim;
	
		int parsecount = 0;
		while (parsecount < parsestr.length())
		{
			switch (parsestr.charAt(parsecount))
			{
				case 'f':
				case 'F':
					parsecount++;
					int frame = GetArg(parsestr.substring(parsecount));
					parsecount+=Integer.toString(frame).length();
					//System.out.println("Parse(F):" + frame + ", sobrou " + parsestr.substring(parsecount));
					break;
				case 'w':
				case 'W':
					parsecount++;
					int wait = GetArg(parsestr.substring(parsecount));
					length+=wait;
					parsecount+=Integer.toString(wait).length();
					//System.out.println("Parse(W):" + wait + ", sobrou " + parsestr.substring(parsecount));					
					break;
				default:
					System.err.printf("CHR::GetAnimLength() - invalid animscript command! %c", parsestr.charAt(parsecount));
			}
		}
		return length;
	}
	
	int GetArg(String str)
	{
		String retorno = "";
	
		int parsecount = 0;
		while (str.charAt(parsecount) == ' ' && parsecount < str.length())
			parsecount++;
	
		while (parsecount < str.length() && str.charAt(parsecount) >= '0' && str.charAt(parsecount) <= '9')
			retorno = retorno.concat(Character.toString(str.charAt(parsecount++)));
	
		if(retorno.trim().equals("")) // [Rafael, the Esper]
			return 0;
		return Integer.parseInt(retorno);
	}
	
	/**Rafael:
	 * New method implemented to allow bypassing .chr files and use an image file instead
	 */
	public static CHR createCHRFromImage(int startx, int starty, int sizex, int sizey, int columns, int totalframes, boolean padding, VImage image) {
		log("createCHRFromImage (" + sizex + "x" + sizey + ": " + totalframes + " frames.");
		VImage[] images = new VImage[totalframes];
		
		int frames = 0, posx = 0, posy = 0, column = 0;

		if(padding)
			posy++;

		// First pixel is default transparent color
		Color transC = new Color(image.image.getRGB(0+(padding?1:0), 0+(padding?1:0)));
		
		while(frames < totalframes) {
			
			if(padding)
				posx++;
				 
			images[frames] = new VImage(sizex, sizey);
			images[frames].tgrabregion(startx+posx, starty+posy, startx+posx+sizex, starty+posy+sizey, 0, 0, transC, image); 
			column++;
			posx+=sizex;
			if(column >= columns) {
				column = 0;
				posx = 0;
				posy+=sizey;
				if(padding)
					posy++;
			}
			frames++;
		}
			
		return createCHRFromImage(sizex, sizey, images);
	}
	
	public static CHR createCHRFromImage(int sizex, int sizey, VImage[] images) {
		CHR c = new CHR();
		
		c.fxsize = sizex;
		c.fysize = sizey;
		c.totalframes = images.length;

		c.animsize = new int[]{0,1,1,1,1,1,1,1,1};
		c.anims = new int[][]{new int[]{0}, new int[]{0}, new int[]{0}, new int[]{0}, new int[]{0}, new int[]{0}, new int[]{0}, new int[]{0}};
	
		c.frames = new BufferedImage[c.totalframes];
		for(int i=0; i<c.totalframes; i++)
			c.frames[i] = images[i].image;
		
		return c;
	}

	
	public static void main (String args[]) throws MalformedURLException {
		//CHR c = new CHR(new URL("file:///C:\\JavaRef3\\EclipseWorkspace\\PS\\src\\ps\\alis.chr"));
		//c.saveChrVersion5("c:\\temp.chr");
		CHR c = new CHR(new URL("file:///C:\\JavaRef3\\EclipseWorkspace\\PS\\src\\ps\\chars\\vehicle.chr"));
		c.hx = 0;
		c.hy = 0;
		c.hw = 64;
		c.hh = 64;
		c.saveChrVersion5("C:\\JavaRef3\\EclipseWorkspace\\PS\\src\\ps\\chars\\vehicle.chr");
		
	}

	public static void processMultipleCharsFromImage() throws MalformedURLException {
		//for(int count=190; count<211; count++) {
			VImage image = new VImage(new URL("file:///C:\\Rbp\\Rpg\\PS\\Generation\\mapdat\\psg1_sprite_mapdat_006" + ".png"));
			CHR c = createCHRFromImage(360, 216, 40, 72, 9, 9, false, image);
			BufferedImage[] newBuffer = new BufferedImage[12];
			for(int i=0;i<9;i++) {
				newBuffer[i] = c.frames[i];
			}
			newBuffer[9] = VImage.flipimage(40, 72, c.frames[3]); 
			newBuffer[10] = VImage.flipimage(40, 72, c.frames[4]);
			newBuffer[11] =	VImage.flipimage(40, 72, c.frames[5]);	
			c.frames = newBuffer;
			c.totalframes = 12;
			
			c.setAnimBufs(new int[]{0,20,20,20,23,20,23,20,23},
					new String[]{"", "F0W30F1W10F2W30F1W10", "F6W30F7W10F8W30F7W10", "F3W30F4W10F5W30F4W10", "F9W30F10W10F11W30F10W10",
					"F3W30F4W10F5W30F4W10", "F9W30F10W10F11W30F10W10", "F3W30F4W10F5W30F4W10", "F9W30F10W10F11W30F10W10"});
			c.idle = new int[]{0, 7, 1, 4, 10}; 
	
			c.hx = 8;
			c.hy = 48;
			c.hw = 24;
			c.hh = 24;
			
			c.saveChrVersion5("C:\\JavaRef3\\EclipseWorkspace\\PS\\src\\ps\\chars\\esper.chr");
		//}

	}
	
}
