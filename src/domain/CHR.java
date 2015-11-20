package domain;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;

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
	
	public int fxsize, fysize;					// frame x/y dimensions
	public int hx, hy;						// x/y obstruction hotspot
	public int hw;							// hotspot width/height
	public int hh;
	int totalframes;					// total # of frames.
    public int idle[] = new int[5];			// idle frames

	private int animsize[] = new int[9];
	private int anims[][] = new int[9][];
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
			 System.err.println("IOException : " + e);
			 e.printStackTrace();
		 }
	}

	// Based on: chr_file.cpp (vopenchr)
	private void loadChrVersion2(ExtendedDataInputStream f) throws IOException {

		this.fxsize = f.readUnsignedShortLittleEndian();
		this.fysize = f.readUnsignedShortLittleEndian();

		this.hx = f.readUnsignedShortLittleEndian();
		this.hy = f.readUnsignedShortLittleEndian();
		this.hw = f.readUnsignedShortLittleEndian();
		this.hh = f.readUnsignedShortLittleEndian();
		
		this.totalframes = f.readUnsignedShortLittleEndian();
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

		this.hx = f.readUnsignedShortLittleEndian();
		this.hy = f.readUnsignedShortLittleEndian();
		this.hw = f.readUnsignedShortLittleEndian();
		this.hh = f.readUnsignedShortLittleEndian();
		
		this.idle[Entity.WEST] = f.readUnsignedShortLittleEndian();
		this.idle[Entity.EAST] = f.readUnsignedShortLittleEndian();
		this.idle[Entity.NORTH] = f.readUnsignedShortLittleEndian();
		this.idle[Entity.SOUTH] = f.readUnsignedShortLittleEndian();

		this.totalframes = f.readUnsignedShortLittleEndian();
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
			{
				
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
		
		//System.out.println(this.hw+";"+this.hh+";"+this.hx+";"+this.hy);
		//System.out.println(this.fxsize+";"+this.fysize+";"+this.totalframes);

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
			animbuf[b] = f.readFixedString(length[b]+1);
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
		//System.out.println("Frames (" + fxsize + ", " + fysize + "): " + totalframes);
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
	public void setAnimBufs(String[] animbufs) {
		int lengths[] = new int[animbufs.length];
		for(int i=0; i<lengths.length; i++) {
			lengths[i] = animbufs[i].length();
		}
		
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
	
	
	public int getAnimSize(int animIndex) { //[Rafael, the Esper]
		if (animIndex<0 || animIndex >= anims.length) {
			System.err.printf("CHR::getAnimSize() - invalid direction %d", animIndex);
			return 0;
		}
		return animsize[animIndex];
	}
	
	public int getFrame(int d, int framect)
	{
		if (d<0 || d >= anims.length) {
			System.err.printf("CHR::GetFrame() - invalid direction %d", d);
			return 0;
		}
		framect %= animsize[d];
		return anims[d][framect];
	}
	
	int GetFrameConst(int d, int framect)
	{
		if (d<0 || d >= anims.length) {
			System.err.printf("CHR::GetFrame() - invalid direction %d", d);
			return 0;
		}
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
	public static CHR createCHRFromImage(int startx, int starty, int sizex, int sizey, int skipx, int skipy, int columns, int totalframes, boolean padding, VImage image) {
		log("createCHRFromImage (" + sizex + "x" + sizey + ": " + totalframes + " frames)");
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
			images[frames].grabRegion(startx+posx, starty+posy, startx+posx+sizex, starty+posy+sizey, 0, 0, image); 
			//images[frames].tgrabregion(startx+posx, starty+posy, startx+posx+sizex, starty+posy+sizey, 0, 0, transC, image);
			column++;
			posx+=sizex+skipx;
			if(column >= columns) {
				column = 0;
				posx = 0;
				posy+=sizey+skipy;
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

	
	public static void main (String args[]) throws IOException {
		//processCharFromImage();
		processCharFromSpecificImage();
		//processWeaponAnimationFromImage();
		//findImageLimits();
		//processMonsterAnimationFromImages();
		
	}
	
	private static void findImageLimits() throws IOException {

		String path = "c:\\jogos\\xeen\\xe\\";
		String file = "018.MON.";
		int numImages = 12;
		
		//This code finds minx, miny, maxx and maxy
		int minx = Integer.MAX_VALUE, miny = Integer.MAX_VALUE;
		int maxx = Integer.MIN_VALUE, maxy = Integer.MIN_VALUE;
		for(int k=0; k<numImages; k++) {
			VImage image = new VImage(new URL("file:///" + path + file + (k) + ".png"), false);
			for(int j=0; j<image.height; j++) {
				for(int i=0; i<image.width; i++) {
					if(image.readPixel(i, j) != Color.MAGENTA.getRGB()) {
						if(i <= minx) minx = i; 
						if(j <= miny) miny = j; 
						if(i >= maxx) maxx = i;
						if(j >= maxy) maxy = j;
					}
				}
			}
		}
		System.out.println("R_" + file + "\t" + "(" + minx + "," + miny + ") (" + maxx + "," + maxy + ")");
		
		for(int k=0; k<numImages; k++) {
			VImage image = new VImage(new URL("file:///" + path + file + (k) + ".png"), false);
			VImage saidaImage = new VImage(maxx-minx, maxy-miny);
			saidaImage.rectfill(0, 0, saidaImage.width, saidaImage.height, Color.MAGENTA);
			saidaImage.grabRegion(minx, miny, maxx, maxy, 0, 0, image);
			ImageIO.write(saidaImage.image, "png", new File(path + "R_" + file + (k) + ".png"));
		}
	}

	public static void processMonsterAnimationFromImages() throws IOException {
		String path = "c:\\jogos\\xeen\\xe\\";
		String file = "048.MON.";
		int numImages = 12;
		
		VImage[] images = new VImage[numImages];
		for(int k=0; k<numImages; k++) {
			images[k] = new VImage(new URL("file:///" + path + "R_" + file + (k) + ".png"), false);
		}
		CHR c = createCHRFromImage(images[0].width, images[0].height, images);

		c.setAnimBufs(new String[]{"", 	"F11W12",  // ANIM1 (DAMAGED)
				"F0W4F1W4F2W4F3W4F4W4F5W4F6W4F7W4", // IDLE
				"F8W8F9W8F10W16F9W4", // ANIM2 (ATTACK1) 
				"", // ATTACK2
				"",	"", "","", ""});

		c.saveChrVersion5("C:\\" + file + ".chr");			
	}
	
	public static void processWeaponAnimationFromImage() throws IOException {
		
		String path = "C:\\Verge\\PS\\ps1_extra_stuff\\Weapons\\";
		String file = "Fang";
		int numImages = 7;
		
		VImage[] images = new VImage[numImages];
		String strAnim = "";
		for(int k=0; k<numImages; k++) {
			images[k] = new VImage(new URL("file:///" + path + "Wp_" + file + (k+1) + ".png"), false);
			strAnim = strAnim + "F" + k + "W4";
		}
		CHR c = createCHRFromImage(images[0].width, images[0].height, images);

		c.setAnimBufs(new String[]{"", 	"",  // ANIM1
				"", // IDLE
				strAnim, // ANIM2 
				"", // ANIM3
				"",	"", "","", ""});

		//c.hy = 0; // for pistols
		c.hy = 20; // for all other weapons
		
		c.saveChrVersion5("C:\\" + file + ".chr");		
		
	}

	public static void processCharFromSpecificImage() throws MalformedURLException {
		
		VImage image;
		CHR c;
		

		/*// MYAU FLAPPING
		image = new VImage(new URL("file:///C:\\Verge\\PS\\ps1_extra_stuff\\Flapping.png"), false);
		c = createCHRFromImage(0, 0, 24, 16, 0, 0, 6, 6, true, image);
		c.setAnimBufs(new String[]{"", 	"F0W4F1W4F2W4F1W4F0W4F1W4F2W4F1W4F0W4F1W4F2W4F1W4F0W4F1W4F2W4F1W4",  // ANIM1 (DAMAGED)
										"F0W1", // IDLE
										"F3W4F4W4F5W4F4W4F3W4F4W4F5W4F4W4F3W4F4W4F5W4F4W4F3W4F4W4F5W4F4W4", // ANIM2 (ATTACK1) 
										"", // ATTACK2
										"",	"", "","", ""});
		c.idle = new int[]{0, 0, 0, 0, 0};
		c.hx = c.hy = c.hw = c.hh = 0;		
		c.saveChrVersion5("C:\\myau_flapping.chr");*/
		
		// ENTITIES
		/*image = new VImage(new URL("file:///C:\\Verge\\PS\\ps1_extra_stuff\\Entities.png"), false);
		c = createCHRFromImage(0, 0, 35, 90, 0, 0, 8, 88, true, image);
		c.setAnimBufs(new String[]{"", 	"F0W1",  // ANIM1 (DAMAGED)
										"F0W1", // IDLE
										"", // ANIM2 (ATTACK1) 
										"", // ATTACK2
										"",	"", "","", ""});
		c.idle = new int[]{0, 0, 0, 0, 0};
		c.hx = c.hy = c.hw = c.hh = 0;		
		c.saveChrVersion5("C:\\entities.chr");*/
		
		/*// LARGE ENTITIES
		image = new VImage(new URL("file:///C:\\Verge\\PS\\ps1_extra_stuff\\Entities_Large.png"), false);
		c = createCHRFromImage(0, 0, 56, 112, 0, 0, 8, 8, true, image);
		c.setAnimBufs(new String[]{"", 	"F0W1",  // ANIM1 (DAMAGED)
										"F0W1", // IDLE
										"", // ANIM2 (ATTACK1) 
										"", // ATTACK2
										"",	"", "","", ""});
		c.idle = new int[]{0, 0, 0, 0, 0};
		c.hx = c.hy = c.hw = c.hh = 0;		
		c.saveChrVersion5("C:\\lentities.chr");*/
		
		
		//SKY Castle
		/*image = new VImage(new URL("file:///C:\\Verge\\PS\\ps1_extra_stuff\\Sky_castle.png"), false);
		c = createCHRFromImage(0, 0, 60, 100, 0, 0, 13, 13, true, image);
		c.setAnimBufs(new String[]{"", 	"F0W4F1W4F2W4F3W4F4W4F5W4F6W4F7W4F8W4F9W4F10W4F11W4F12W4",  // ANIM1
										"F12W1", // IDLE
										"", // ANIM2 
										"", // ANIM3
										"",	"", "","", ""});
		c.idle = new int[]{0, 0, 12, 0, 0};
		c.hx = c.hy = c.hw = c.hh = 0;		
		c.saveChrVersion5("C:\\sky_castle.chr");*/

		
		//CHEST
		image = new VImage(new URL("file:///C:\\Verge\\PS\\ps1_extra_stuff\\Chest.png"), false);
		c = createCHRFromImage(0, 0, 64, 72, 0, 0, 13, 13, true, image);
		c.setAnimBufs(new String[]{"", 	"F0W10F1W8F2W8F3W16",  // ANIM1
										"F0W1", // IDLE
										"F3W16F4W4F5W4F6W4F7W8F8W4F3W8", // ANIM2 
										"F9W4F10W4F11W4F12W8F3W8", // ANIM3
										"",	"", "","", ""});
		c.idle = new int[]{0, 0, 3, 0, 0};
		c.hx = c.hy = c.hw = c.hh = 0;		
		c.saveChrVersion5("C:\\chest.chr");
		
		/*// SCORPION
		image = new VImage(new URL("file:///C:\\Verge\\PS\\ps1_extra_stuff\\GoldScorpion.png"), false);
		c = createCHRFromImage(0, 0, 48, 48, 0, 0, 4, 4, true, image);
		c.setAnimBufs(new String[]{"", 	"F3W2F0W2F3W2F0W2F3W2F0W2",  // ANIM1 (DAMAGED)
										"F0W1", // IDLE
										"F0W4F1W2F2W2F1W2F0W4F1W2F2W2F1W2", // ANIM2 (ATTACK1) 
										"", // ATTACK2
										"",	"", "","", ""});
		c.idle = new int[]{0, 0, 0, 0, 0};
		c.hx = c.hy = c.hw = c.hh = 0;		
		c.saveChrVersion5("C:\\gscorpion.chr");*/		
		/*// PSIV SCORPION/YELLOW_SCORPION/BLUE_SCORPION
		image = new VImage(new URL("file:///C:\\Verge\\PS\\ps1_extra_stuff\\PS4_Blue_Scorpion.png"), false);
		c = createCHRFromImage(0, 0, 78, 102, 0, 0, 9, 9, true, image);
		c.setAnimBufs(new String[]{"", 	"F8W2F0W2F8W2F0W2F8W2F0W2",  // ANIM1 (DAMAGED)
										"F0W6F1W6F2W12F1W6", // IDLE
										"F3W4F4W4F5W4F6W4F7W12F6W4F5W4F4W4F3W4", // ANIM2 (ATTACK1) 
										"", // ATTACK2
										"",	"", "","", ""});
		c.idle = new int[]{0, 0, 0, 0, 0};
		c.hx = c.hy = c.hw = c.hh = 0;		
		c.saveChrVersion5("C:\\ps4_blue_scorpion.chr");*/		

		/*// SWORM/GIANTFLY
		image = new VImage(new URL("file:///C:\\Verge\\PS\\ps1_extra_stuff\\sworm.png"), false);
		c = createCHRFromImage(0, 0, 48, 48, 0, 0, 4, 4, true, image);
		c.setAnimBufs(new String[]{"", 	"F3W2F0W2F3W2F0W2F3W2F0W2",  // ANIM1 (DAMAGED)
										"F0W1", // IDLE
										"F0W2F1W2F0W2F2W2F0W2F1W2F0W2F2W2", // ANIM2 (ATTACK1) 
										"F0W2F1W2F0W2F2W2F0W2F1W2F0W2F2W2F0W32", // ATTACK2
										"",	"", "","", ""});
		c.idle = new int[]{0, 0, 0, 0, 0};
		c.hx = c.hy = c.hw = c.hh = 0;		
		c.saveChrVersion5("C:\\sworm.chr");*/

		/*// BEACH
		image = new VImage(new URL("file:///C:\\Verge\\PS\\ps1_extra_stuff\\Beaches.png"), false);
		c = createCHRFromImage(0, 0, 320, 240, 0, 0, 3, 12, true, image);
		c.setAnimBufs(new String[]{"", 	"",  // ANIM1 (DAMAGED)
										"F0W16F1W8F2W8F3W8F4W16F5W8F6W8F7W16F8W8F9W8F10W16F11W16F10W16F9W8F8W8F7W16F6W8F5W8F4W16F3W8F2W8F1W16", // IDLE
										"", // ANIM2 (ATTACK1) 
										"", // ATTACK2
										"",	"", "","", ""});
		c.saveChrVersion5("C:\\beaches.chr");*/
		/*// LAVA
		image = new VImage(new URL("file:///C:\\Verge\\PS\\ps1_extra_stuff\\Lava.png"), false);
		c = createCHRFromImage(0, 0, 320, 240, 0, 0, 3, 12, true, image);
		c.setAnimBufs(new String[]{"", 	"",  // ANIM1 (DAMAGED)
										"F0W16F1W16F2W16F3W16F4W16F5W16", // IDLE
										"", // ANIM2 (ATTACK1) 
										"", // ATTACK2
										"",	"", "","", ""});
		c.idle = new int[]{0, 0, 0, 0, 0};
		c.hx = c.hy = c.hw = c.hh = 0;		
		c.saveChrVersion5("C:\\lava.chr");		*/
		// GAS
		/*image = new VImage(new URL("file:///C:\\Verge\\PS\\ps1_extra_stuff\\Gas.png"), false);
		c = createCHRFromImage(0, 0, 320, 240, 0, 0, 5, 15, true, image);
		c.setAnimBufs(new String[]{"", 	"",  // ANIM1 (DAMAGED)
			 							"F0W8F1W8F2W8F3W8F4W8F5W8F6W8F7W8F0W8F8W8F9W8F10W8F11W8F12W8F13W8F14W8", // IDLE
										"", // ANIM2 (ATTACK1) 
										"", // ATTACK2
										"",	"", "","", ""});
		c.saveChrVersion5("C:\\gas.chr");*/
		/*// SEA
		image = new VImage(new URL("file:///C:\\Verge\\PS\\ps1_extra_stuff\\Sea.png"), false);
		c = createCHRFromImage(0, 0, 320, 240, 0, 0, 3, 9, true, image);
		c.setAnimBufs(new String[]{"", 	"",  // ANIM1 (DAMAGED)
			 							"F0W8F1W8F2W8F3W8F4W8F5W8F6W8F7W8F8W8", // IDLE
										"", // ANIM2 (ATTACK1) 
										"", // ATTACK2
										"",	"", "","", ""});
		c.saveChrVersion5("C:\\Sea.chr");*/
		/*// TARANTUL/ANT_LION/GIANT_SPIDER
		image = new VImage(new URL("file:///C:\\Verge\\PS\\ps1_extra_stuff\\Giant_spider.png"), false);
		c = createCHRFromImage(0, 0, 64, 72, 0, 0, 10, 10, true, image);
		c.setAnimBufs(new String[]{"", 	"F9W2F0W2F9W2F0W2F9W2F0W2",  // ANIM1 (DAMAGED)
										"F0W1", // IDLE
										"F0W8F1W8F2W8F3W8F4W8F5W8F6W8F7W8F8W16", // ANIM2 (ATTACK1) 
										"", // ATTACK2
										"",	"", "","", ""});
		c.idle = new int[]{0, 0, 0, 0, 0};
		c.hx = c.hy = c.hw = c.hh = 0;		
		c.saveChrVersion5("C:\\giantspider.chr");*/		
		/*// ROBOTCOP/ANDROCOP/NANOCOP
		image = new VImage(new URL("file:///C:\\Verge\\PS\\ps1_extra_stuff\\nano_guard.png"), false);
		c = createCHRFromImage(0, 0, 42, 80, 0, 0, 9, 9, true, image);
		c.setAnimBufs(new String[]{"", 	"F8W2F0W2F8W2F0W2F8W2F0W2",  // ANIM1 (DAMAGED)
										"F0W1", // IDLE
										//"F0W4F1W4F2W4F3W4F4W4F5W4F6W4F7W8F2W4F1W4", // ANIM2 (ATTACK1) 
										"F0W3F1W3F2W3F3W3F4W3F5W3F6W3F7W6F2W3F1W3", // ANIM2 (ATTACK1) NANOCOP
										"", // ATTACK2
										"",	"", "","", ""});
		c.idle = new int[]{0, 0, 0, 0, 0};
		c.hx = c.hy = c.hw = c.hh = 0;		
		c.saveChrVersion5("C:\\nanocop.chr");		*/		
		/*//ODIN
		image = new VImage(new URL("file:///C:\\Verge\\PS\\ps1_extra_stuff\\Odin.png"), false);
		c = createCHRFromImage(0, 0, 40, 88, 0, 0, 7, 7, true, image);
		c.setAnimBufs(new String[]{"", 	"F0W16F1W16F2W16F3W16F4W16F5W16F6W16",  // ANIM1 (DAMAGED)
										"F0W1", // STONED ODIN
										"F6W1", // NORMAL ODIN 
										"", // ATTACK2
										"",	"", "","", ""});
		c.idle = new int[]{0, 0, 6, 0, 0};
		c.hx = c.hy = c.hw = c.hh = 0;		
		c.saveChrVersion5("C:\\odin_stone.chr");*/
		/*// BARBARIAN/MOTA_SHOOTER
		image = new VImage(new URL("file:///C:\\Verge\\PS\\ps1_extra_stuff\\mota_shooter.png"), false);
		c = createCHRFromImage(0, 0, 40, 64, 0, 0, 7, 7, true, image);
		c.setAnimBufs(new String[]{"", 	"F6W2F0W2F6W2F0W2F6W2F0W2",  // ANIM1 (DAMAGED)
										"F0W1", // IDLE
										"F0W3F1W3F2W6F3W4F4W6F5W3F2W3", // ANIM2 (ATTACK) SHOOTER
										//"F0W4F1W4F2W8F3W4F4W8F5W4F2W4F0W4", // ANIM2 (ATTACK) BARBRIAN 
										"", // ATTACK2
										"",	"", "","", ""});
		c.idle = new int[]{0, 0, 0, 0, 0};
		c.hx = c.hy = c.hw = c.hh = 0;		
		c.saveChrVersion5("C:\\mota_shooter.chr");*/
		/*image = new VImage(new URL("file:///C:\\Verge\\PS\\ps1_extra_stuff\\Sword.png"), false);
		c = createCHRFromImage(0, 0, 40, 82, 0, 0, 4, 4, true, image);
		c.setAnimBufs(new String[]{"", 	"F0W1",  // ANIM1 (DAMAGED)
										"F0W1", // IDLE
										"F0W4F1W4F2W4F3W4", // ANIM2 (ATTACK) 
										"", // ATTACK2
										"",	"", "","", ""});
		c.idle = new int[]{0, 0, 0, 0, 0};
		c.hx = c.hy = c.hw = c.hh = 0;		
		c.saveChrVersion5("C:\\sword.chr");*/
		/*// MANEATER/DEADTREE/POISONPLANT		
		image = new VImage(new URL("file:///C:\\Verge\\PS\\ps1_extra_stuff\\Poison_plant.png"), false);
		//image = new VImage(new URL("file:///C:\\Verge\\PS\\ps1_extra_stuff\\Dead_Tree.png"), false);
		c = createCHRFromImage(0, 0, 48, 48, 0, 0, 7, 7, true, image);
		c.setAnimBufs(new String[]{"", 	"F6W2F0W2F6W2F0W2F6W2F0W2",  // ANIM1 (DAMAGED)
										"F0W1", // IDLE
										"F0W4F1W4F2W4F3W4F4W4F5W4F4W4F3W4F2W4F1W4F0W4", // ANIM2 (ATTACK) 
										"", // ATTACK2
										"",	"", "","", ""});
		c.idle = new int[]{0, 0, 0, 0, 0};
		c.hx = c.hy = c.hw = c.hh = 0;		
		c.saveChrVersion5("C:\\poisonplant.chr");*/

		/*//YOZ SKELETON GUARD
		image = new VImage(new URL("file:///C:\\Verge\\PS\\ps1_extra_stuff\\Yoz_Skeleton.png"), false);
		c = createCHRFromImage(0, 0, 94, 108, 0, 0, 7, 7, true, image);
		c.setAnimBufs(new String[]{"", 	"F6W2F0W2F6W2F0W2F6W2F0W2",  // ANIM1 (DAMAGED)
										"F0W1", // IDLE
										"F0W8F1W8F2W8F3W8F4W16F5W4F0W6", // ANIM2 (ATTACK1) 
										"", // ATTACK2
										"",	"", "","", ""});
		c.idle = new int[]{0, 0, 0, 0, 0};
		c.hx = c.hy = c.hw = c.hh = 0;		
		c.saveChrVersion5("C:\\skeleton_guard.chr");

		//YOZ REVENANT
		image = new VImage(new URL("file:///C:\\Verge\\PS\\ps1_extra_stuff\\Yoz_Revenant.png"), false);
		c = createCHRFromImage(0, 0, 52, 94, 0, 0, 6, 6, true, image);
		c.setAnimBufs(new String[]{"", 	"F5W2F0W2F5W2F0W2F5W2F0W2",  // ANIM1 (DAMAGED)
										"F0W1", // IDLE
										"F0W8F1W8F2W8F3W8F4W16F0W8", // ANIM2 (ATTACK1) 
										"", // ATTACK2
										"",	"", "","", ""});
		c.idle = new int[]{0, 0, 0, 0, 0};
		c.hx = c.hy = c.hw = c.hh = 0;		
		c.saveChrVersion5("C:\\revenant.chr");
		 */
		
		/*//CYCLOP
		image = new VImage(new URL("file:///C:\\Verge\\PS\\ps1_extra_stuff\\Yoz_Cyclop.png"), false);
		c = createCHRFromImage(0, 0, 64, 120, 0, 0, 5, 5, true, image);
		c.setAnimBufs(new String[]{"", 	"F4W2F0W2F4W2F0W2F4W2F0W2",  // ANIM1 (DAMAGED)
										"F0W1", // IDLE
										"F0W6F1W6F2W6F3W12F0W6", // ANIM2 (ATTACK1) 
										"", // ATTACK2
										"",	"", "","", ""});
		c.idle = new int[]{0, 0, 0, 0, 0};
		c.hx = c.hy = c.hw = c.hh = 0;		
		c.saveChrVersion5("C:\\cyclop.chr");*/
		
		
		/*// TITAN/GOLEM/GIANT
		image = new VImage(new URL("file:///C:\\Verge\\PS\\ps1_extra_stuff\\Titan.png"), false);
		c = createCHRFromImage(0, 0, 60, 112, 0, 0, 5, 5, true, image);
		c.setAnimBufs(new String[]{"", 	"F4W2F0W2F4W2F0W2F4W2F0W2",  // ANIM1 (DAMAGED)
										"F0W1", // IDLE
										"F0W6F1W6F2W6F3W12F0W6", // ANIM2 (ATTACK1) 
										"", // ATTACK2
										"",	"", "","", ""});
		c.idle = new int[]{0, 0, 0, 0, 0};
		c.hx = c.hy = c.hw = c.hh = 0;		
		c.saveChrVersion5("C:\\titan.chr");*/
		/*// CRAWLER/SANDWORM/LEECH
		image = new VImage(new URL("file:///C:\\Verge\\PS\\ps1_extra_stuff\\crawler.png"), false);
		c = createCHRFromImage(0, 0, 38, 82, 0, 0, 6, 6, true, image);
		c.setAnimBufs(new String[]{"", 	"F5W2F0W2F5W2F0W2F5W2F0W2",  // ANIM1 (DAMAGED)
										"F0W1", // IDLE
										"F0W6F1W6F2W6F3W6F4W12F3W6F2W6F1W6F0W6", // ANIM2 (ATTACK1) 
										"", // ATTACK2
										"",	"", "","", ""});
		c.idle = new int[]{0, 0, 0, 0, 0};
		c.hx = c.hy = c.hw = 0; c.hh = 35; 		
		c.saveChrVersion5("C:\\crawler.chr");			*/
		/*// SKELETON/SKULL-EN/STALKER
		image = new VImage(new URL("file:///C:\\Verge\\PS\\ps1_extra_stuff\\skullen.png"), false);
		c = createCHRFromImage(0, 0, 48, 96, 0, 0, 7, 7, true, image);
		c.setAnimBufs(new String[]{"", 	"F6W2F0W2F6W2F0W2F6W2F0W2",  // ANIM1 (DAMAGED)
										"F0W1", // IDLE
										"F0W8F1W8F2W8F3W8F4W16F5W8F0W4", // ANIM2 (ATTACK1) 
										"", // ATTACK2
										"",	"", "","", ""});
		c.idle = new int[]{0, 0, 0, 0, 0};
		c.hx = c.hy = c.hw = c.hh = 0;		
		c.saveChrVersion5("C:\\skullen.chr");*/
		/*// FISHMAN/MARSHMAN
				image = new VImage(new URL("file:///C:\\Verge\\PS\\ps1_extra_stuff\\fishman.png"), false);
				c = createCHRFromImage(0, 0, 56, 64, 0, 0, 5, 5, true, image);
				c.setAnimBufs(new String[]{"", 	"F4W2F0W2F4W2F0W2F4W2F0W2",  // ANIM1 (DAMAGED)
												"F0W1", // IDLE
												"F0W8F1W8F2W8F3W16F2W8F1W8F0W4", // ANIM2 (ATTACK1) 
												"", // ATTACK2
												"",	"", "","", ""});
				c.idle = new int[]{0, 0, 0, 0, 0};
				c.hx = c.hy = c.hw = c.hh = 0;		
				c.saveChrVersion5("C:\\fishman.chr");	*/	
		/*// WINGEYE/OWLBEAR/GOLDLENS
		image = new VImage(new URL("file:///C:\\Verge\\PS\\ps1_extra_stuff\\goldlens.png"), false);
		c = createCHRFromImage(0, 0, 56, 64, 0, 0, 5, 5, true, image);
		c.setAnimBufs(new String[]{"", 	"F4W2F0W2F4W2F0W2F4W2F0W2",  // ANIM1 (DAMAGED)
										"F0W1", // IDLE
										"F0W6F1W6F2W6F3W6F1W6F0W6F1W6F2W6F3W6F1W6F0W6", // ANIM2 (ATTACK1) 
										"", // ATTACK2
										"",	"", "","", ""});
		c.idle = new int[]{0, 0, 0, 0, 0};
		c.hx = c.hy = c.hw = c.hh = 0;		
		c.saveChrVersion5("C:\\goldlens.chr");*/
		/*// WEREBAT/VAMPIRE
				image = new VImage(new URL("file:///C:\\Verge\\PS\\ps1_extra_stuff\\werebat_green.png"), false);
				c = createCHRFromImage(0, 0, 64, 112, 0, 0, 8, 8, true, image);
				c.setAnimBufs(new String[]{"", 	"F7W2F0W2F7W2F0W2F7W2F0W2",  // ANIM1 (DAMAGED)
												"F0W1", // IDLE
												"F0W8F1W8F2W8F3W4F4W4F5W4F6W4F4W4F5W4F6W4F3W4F2W8F0W4", // ANIM2 (ATTACK1) 
												"", // ATTACK2
												"",	"", "","", ""});
				c.idle = new int[]{0, 0, 0, 0, 0};
				c.hx = c.hy = c.hw = c.hh = 0;		
				c.saveChrVersion5("C:\\werebat_green.chr");*/
		/*// EVILDEAD/WIGHT/LICH
		image = new VImage(new URL("file:///C:\\Verge\\PS\\ps1_extra_stuff\\lich.png"), false);
		c = createCHRFromImage(0, 0, 48, 92, 0, 0, 6, 6, true, image);
		c.setAnimBufs(new String[]{"", 	"F5W2F0W2F5W2F0W2F5W2F0W2",  // ANIM1 (DAMAGED)
										"F0W1", // IDLE
										"F0W8F1W8F2W8F3W8F4W8F1W8F2W8F3W8F4W8F0W4", // ANIM2 (ATTACK1) 
										"", // ATTACK2
										"",	"", "","", ""});
		c.idle = new int[]{0, 0, 0, 0, 0};
		c.hx = c.hy = c.hw = c.hh = 0;		
		c.saveChrVersion5("C:\\lich.chr");*/
		/*// SLIMES
				image = new VImage(new URL("file:///C:\\Verge\\PS\\ps1_extra_stuff\\redslime.png"), false);
				c = createCHRFromImage(0, 0, 48, 56, 0, 0, 8, 8, true, image);
				c.setAnimBufs(new String[]{"", 	"F7W2F0W2F7W2F0W2F7W2F0W2",  // ANIM1 (DAMAGED)
												"F0W1", // IDLE
												"F0W8F1W8F2W8F3W8F4W16F5W8F6W8F4W8F6W8F5W8F3W8F2W8F0W4", // ANIM2 (ATTACK1) 
												"", // ATTACK2
												"",	"", "","", ""});
				c.idle = new int[]{0, 0, 0, 0, 0};
				c.hx = c.hy = c.hw = c.hh = 0;		
				c.saveChrVersion5("C:\\redslime.chr");*/		
		/*// SERPENT/NESSIE/WYVERN
		image = new VImage(new URL("file:///C:\\Verge\\PS\\ps1_extra_stuff\\serpent.png"), false);
		c = createCHRFromImage(0, 0, 64, 104, 0, 0, 9, 9, true, image);
		c.setAnimBufs(new String[]{"", 	"F8W2F0W2F8W2F0W2F8W2F0W2",  // ANIM1 (DAMAGED)
										"F0W1", // IDLE
										"F0W4F1W4F2W4F3W4F4W4F5W4F6W4F5W4F6W4F7W4F2W4F1W2F0W4", // ANIM2 (ATTACK1) 
										"", // ATTACK2
										"",	"", "","", ""});
		c.idle = new int[]{0, 0, 0, 0, 0};
		c.hx = c.hy = c.hw = c.hh = 0;		
		c.saveChrVersion5("C:\\serpent.chr");*/
		/*// ZOMBIE/GHOUL/BATTALION
				image = new VImage(new URL("file:///C:\\Verge\\PS\\ps1_extra_stuff\\batalion.png"), false);
				c = createCHRFromImage(0, 0, 34, 88, 0, 0, 8, 8, true, image);
				c.setAnimBufs(new String[]{"", 	"F7W2F0W2F7W2F0W2F7W2F0W2",  // ANIM1 (DAMAGED)
												"F0W1", // IDLE
												"F0W4F1W8F0W8F1W8F2W8F3W8F4W8F5W8F6W12F5W8F4W8F3W8F2W8F0W4", // ANIM2 (ATTACK1) 
												"", // ATTACK2
												"",	"", "","", ""});
				c.idle = new int[]{0, 0, 0, 0, 0};
				c.hx = c.hy = c.hw = c.hh = 0;		
				c.saveChrVersion5("C:\\batalion.chr");*/
			/*//LANDROVER
			image = new VImage(new URL("file:///C:\\Verge\\PS\\ps1_extra_stuff\\Landrover.png"), false);
			c = createCHRFromImage(0, 0, 32, 32, 0, 0, 12, 12, true, image);
			c.setAnimBufs(new String[]{"", "F0W5F1W5F2W8F1W5F0W5", "F3W5F4W5F5W8F4W5F3W5", "F6W5F7W5F8W8F7W5F6W5", "F9W5F10W5F11W8F10W5F9W5",
					"", "","", ""});
	
			c.idle = new int[]{0, 3, 0, 6, 9}; // Up down left right
		
			c.hx = 4;
			c.hy = 4;
			c.hw = 24;
			c.hh = 24;
			c.saveChrVersion5("C:\\Landrover.chr");*/
		/*// AMUNDSEN/FROSTMAN/GAIA
		image = new VImage(new URL("file:///C:\\Verge\\PS\\ps1_extra_stuff\\gaia.png"), false);
		c = createCHRFromImage(0, 0, 64, 112, 0, 0, 9, 9, true, image);
		c.setAnimBufs(new String[]{"", 	"F8W2F0W2F8W2F0W2F8W2F0W2",  // ANIM1 (DAMAGED)
										"F0W1", // IDLE
										"F0W8F1W8F2W16F1W8F0W8F3W16F4W8F5W8F6W8F7W16F0W4", // ANIM2 (ATTACK1) 
										"", // ATTACK2
										"",	"", "","", ""});
		c.idle = new int[]{0, 0, 0, 0, 0};
		c.hx = c.hy = c.hw = c.hh = 0;		
		c.saveChrVersion5("C:\\gaia.chr");*/
		/*// BIGCLUB/EXECUTER
		image = new VImage(new URL("file:///C:\\Verge\\PS\\ps1_extra_stuff\\gold_club.png"), false);
		c = createCHRFromImage(0, 0, 40, 104, 0, 0, 8, 8, true, image);
		c.setAnimBufs(new String[]{"", 	"F7W2F0W2F7W2F0W2F7W2F0W2",  // ANIM1 (DAMAGED)
										"F0W1", // IDLE
										"F0W8F1W8F2W8F3W8F4W8F5W8F6W16F5W8F0W4", // ANIM2 (ATTACK1) 
										"F0W8F1W8F2W8F3W8F4W8F5W8F6W16F5W8F0W4", // ATTACK2
										"",	"", "","", ""});
		c.idle = new int[]{0, 0, 0, 0, 0};
		c.hx = c.hy = c.hw = c.hh = 0;		
		c.saveChrVersion5("C:\\gold_club.chr");*/
		/*// DRAGONS	
		image = new VImage(new URL("file:///C:\\Verge\\PS\\ps1_extra_stuff\\white_dragon.png"), false);
		c = createCHRFromImage(0, 0, 102, 136, 0, 0, 8, 8, true, image);
		c.setAnimBufs(new String[]{"", 	"F7W2F0W2F7W2F0W2F7W2F0W2",  // ANIM1 (DAMAGED)
										"F0W1", // IDLE
										"F0W4F1W4F2W4F3W4F4W4F5W4F6W8", // ANIM2 (ATTACK1) 
										"", // ATTACK2
										"",	"", "","", ""});
		c.idle = new int[]{0, 0, 0, 0, 0};
		c.hx = c.hy = c.hw = c.hh = 0;		
		c.saveChrVersion5("C:\\white_dragon.chr");*/
		/*// CENTAUR/HORSEMAN	
		image = new VImage(new URL("file:///C:\\Verge\\PS\\ps1_extra_stuff\\horseman.png"), false);
		c = createCHRFromImage(0, 0, 50, 104, 0, 0, 7, 7, true, image);
		c.setAnimBufs(new String[]{"", 	"F6W2F0W2F6W2F0W2F6W2F0W2",  // ANIM1 (DAMAGED)
										"F0W1", // IDLE
										"F0W8F1W8F2W16F3W8F0W4", // ANIM2 (ATTACK1) 
										"F0W8F4W8F5W32F4W8F0W4", // ATTACK2
										"",	"", "","", ""});
		c.idle = new int[]{0, 0, 0, 0, 0};
		c.hx = c.hy = c.hw = c.hh = 0;		
		c.saveChrVersion5("C:\\horseman.chr");*/
		/*// DEZORIAN/EVILHEAD/DEZO_PRIEST	
		image = new VImage(new URL("file:///C:\\Verge\\PS\\ps1_extra_stuff\\dezorian_alt.png"), false);
		c = createCHRFromImage(0, 0, 24, 88, 0, 0, 6, 6, true, image);
		c.setAnimBufs(new String[]{"", 	"F5W2F0W2F5W2F0W2F5W2F0W2",  // ANIM1 (DAMAGED)
										"F0W1", // IDLE
										"F0W8F1W8F2W8F3W8F4W16F3W8F2W8F0W4", // ANIM2 (ATTACK1) 
										"", // ATTACK2
										"",	"", "","", ""});
		c.idle = new int[]{0, 0, 0, 0, 0};
		c.hx = c.hy = c.hw = c.hh = 0;		
		c.saveChrVersion5("C:\\dezo_alt.chr");*/
		/*// DR_MAD/SHADOW	
		image = new VImage(new URL("file:///C:\\Verge\\PS\\ps1_extra_stuff\\shadow.png"), false);
		c = createCHRFromImage(0, 0, 48, 80, 0, 0, 9, 9, true, image);
		c.setAnimBufs(new String[]{"", 	"F8W2F0W2F8W2F0W2F8W2F0W2",  // ANIM1 (DAMAGED)
										"F0W1", // IDLE
										"F0W4F1W4F2W8F3W8F4W6F5W4F6W4F7W12F1W4", // ANIM2 (ATTACK1) 
										"", // ATTACK2
										"",	"", "","", ""});
		c.idle = new int[]{0, 0, 0, 0, 0};
		c.hx = c.hy = c.hw = c.hh = 0;		
		c.saveChrVersion5("C:\\shadow.chr");*/
		/*// EFARMER/NFARMER	
		image = new VImage(new URL("file:///C:\\Verge\\PS\\ps1_extra_stuff\\nfarmer.png"), false);
		c = createCHRFromImage(0, 0, 40, 64, 0, 0, 5, 5, true, image);
		c.setAnimBufs(new String[]{"", 	"F4W2F0W2F4W2F0W2F4W2F0W2",  // ANIM1 (DAMAGED)
										"F0W1", // IDLE
										"F0W4F1W6F2W8F3W8F0W4", // ANIM2 (ATTACK1) 
										"", // ATTACK2
										"",	"", "","", ""});
		c.idle = new int[]{0, 0, 0, 0, 0};
		c.hx = c.hy = c.hw = c.hh = 0;		
		c.saveChrVersion5("C:\\nfarmer.chr");*/
		// ELEPHANT/MAMMOTH/OLIPHANT	
		/*image = new VImage(new URL("file:///C:\\Verge\\PS\\ps1_extra_stuff\\mammoth.png"), false);
		c = createCHRFromImage(0, 0, 80, 96, 0, 0, 7, 7, true, image);
		c.setAnimBufs(new String[]{"", 	"F6W2F0W2F6W2F0W2F6W2F0W2",  // ANIM1 (DAMAGED)
										"F0W1", // IDLE
										"F0W8F1W8F2W8F3W8F4W8F5W16", // ANIM2 (ATTACK1) 
										"F0W4F1W4F2W4F3W36F2W8F1W8", // ATTACK2
										"",	"", "","", ""});
		c.idle = new int[]{0, 0, 0, 0, 0};
		c.hx = c.hy = c.hw = c.hh = 0;		
		c.saveChrVersion5("C:\\mammoth.chr");	*/
		/*// OCTOPUS/TENTACLE
		image = new VImage(new URL("file:///C:\\Verge\\PS\\ps1_extra_stuff\\octopus.png"), false);
		c = createCHRFromImage(0, 0, 64, 80, 0, 0, 5, 5, true, image);
		c.setAnimBufs(new String[]{"", 	"F4W2F0W2F4W2F0W2F4W2F0W2",  // ANIM1 (DAMAGED)
										"F0W1", // IDLE
										"F0W6F1W6F2W6F3W12F2W6F1W6F0W4", // ANIM2 (ATTACK1) 
										"", // ATTACK2
										"",	"", "","", ""});
		c.idle = new int[]{0, 0, 0, 0, 0};
		c.hx = c.hy = c.hw = c.hh = 0;		
		c.saveChrVersion5("C:\\octopus.chr");	*/		
		/*// SACCUBUS
		image = new VImage(new URL("file:///C:\\Verge\\PS\\ps1_extra_stuff\\saccubus.png"), false);
		c = createCHRFromImage(0, 0, 28, 45, 0, 0, 6, 6, true, image);
		c.setAnimBufs(new String[]{"", 	"F5W2F0W2F5W2F0W2F5W2F0W2",  // ANIM1 (DAMAGED)
										"F0W1", // IDLE
										"F0W4F1W4F2W8F3W8F4W8F3W8F4W8", // ANIM2 (ATTACK1) 
										"", // ATTACK2
										"",	"", "","", ""});
		c.idle = new int[]{0, 0, 0, 0, 0};
		c.hx = c.hy = c.hw = c.hh = 0;		
		c.saveChrVersion5("C:\\saccubus.chr");*/
		/*// MEDUSA
		image = new VImage(new URL("file:///C:\\Verge\\PS\\ps1_extra_stuff\\medusa.png"), false);
		c = createCHRFromImage(0, 0, 64, 108, 0, 0, 8, 8, true, image);
		c.setAnimBufs(new String[]{"", 	"F7W2F0W2F7W2F0W2F7W2F0W2",  // ANIM1 (DAMAGED)
										"F0W1", // IDLE
										"F0W4F1W4F2W4F3W4F4W4F5W4F0W4", // ANIM2 (ATTACK1) 
										"", // ATTACK2
										"",	"", "","", ""});
		c.idle = new int[]{0, 0, 0, 0, 0};
		c.hx = c.hy = c.hw = c.hh = 0;		
		c.saveChrVersion5("C:\\medusa.chr");*/			
		/*// GOLDEN DRAGON
		image = new VImage(new URL("file:///C:\\Verge\\PS\\ps1_extra_stuff\\golden_dragon.png"), false);
		c = createCHRFromImage(0, 0, 208, 104, 0, 0, 10, 10, true, image);
		c.setAnimBufs(new String[]{"", 	"F9W2F0W2F9W2F0W2F9W2F0W2",  // ANIM1 (DAMAGED)
										"F0W1", // IDLE
										"F0W4F1W4F2W4F3W4F4W4F5W4F6W4F7W4F8W4F7W4F8W4F6W4F5W4F3W4F2W4F1W4F0W4", // ANIM2 (ATTACK1) 
										"", // ATTACK2
										"",	"", "","", ""});
		c.idle = new int[]{0, 0, 0, 0, 0};
		c.hx = c.hy = c.hw = c.hh = 0;		
		c.saveChrVersion5("C:\\golden_dragon.chr");*/			
		/*// PLAYER FIRE / FIRE_GI
		image = new VImage(new URL("file:///C:\\Verge\\PS\\ps1_extra_stuff\\fire_gi.png"), false);
		c = createCHRFromImage(0, 0, 60, 112, 0, 0, 9, 9, true, image);
		c.setAnimBufs(new String[]{"", 	"F0W1",  // ANIM1 (DAMAGED)
										"F0W1", // IDLE
										"F0W8F1W4F2W4F3W4F4W4F5W4F6W4F7W4F8W4", // ANIM2 (ATTACK1)
										"", // ATTACK2
										"",	"", "","", ""});
		c.idle = new int[]{0, 0, 0, 0, 0};
		c.hx = c.hy = c.hw = c.hh = 0;		
		c.saveChrVersion5("C:\\pl_gifire.chr");*/		
		/*// PLAYER WIND
		image = new VImage(new URL("file:///C:\\Verge\\PS\\ps1_extra_stuff\\wind.png"), false);
		c = createCHRFromImage(0, 0, 64, 112, 0, 0, 9, 9, true, image);
		c.setAnimBufs(new String[]{"", 	"F0W1",  // ANIM1 (DAMAGED)
										"F0W1", // IDLE
										"F0W8F1W4F2W4F3W4F4W4F5W4F6W4F7W4F8W4", // ANIM2 (ATTACK1) 
										"", // ATTACK2
										"",	"", "","", ""});
		c.idle = new int[]{0, 0, 0, 0, 0};
		c.hx = c.hy = c.hw = c.hh = 0;		
		c.saveChrVersion5("C:\\pl_wind.chr");*/						
		/*// PLAYER THUNDER
		image = new VImage(new URL("file:///C:\\Verge\\PS\\ps1_extra_stuff\\thunder.png"), false);
		c = createCHRFromImage(0, 0, 64, 112, 0, 0, 10, 10, true, image);
		c.setAnimBufs(new String[]{"", 	"F0W1",  // ANIM1 (DAMAGED)
										"F0W1", // IDLE
										"F0W8F1W4F2W4F3W4F4W4F5W4F6W4F7W4F8W4F9W4", // ANIM2 (ATTACK1) 
										"", // ATTACK2
										"",	"", "","", ""});
		c.idle = new int[]{0, 0, 0, 0, 0};
		c.hx = c.hy = c.hw = c.hh = 0;		
		c.saveChrVersion5("C:\\pl_thunder.chr");*/
		/*// ENEMY FIRE
		image = new VImage(new URL("file:///C:\\Verge\\PS\\ps1_extra_stuff\\enemy_fire.png"), false);
		c = createCHRFromImage(0, 0, 40, 84, 0, 0, 9, 9, true, image);
		c.setAnimBufs(new String[]{"", 	"F0W1",  // ANIM1 (DAMAGED)
										"F0W1", // IDLE
										"F0W16F1W4F2W4F3W4F4W4F5W4F6W4F7W4F8W4", // ANIM2 (ATTACK1) 
										"", // ATTACK2
										"",	"", "","", ""});
		c.idle = new int[]{0, 0, 0, 0, 0};
		c.hx = c.hy = c.hw = c.hh = 0;		
		c.saveChrVersion5("C:\\enemy_fire.chr");*/						
		/*// ENEMY THUNDER
		image = new VImage(new URL("file:///C:\\Verge\\PS\\ps1_extra_stuff\\enemy_thunder.png"), false);
		c = createCHRFromImage(0, 0, 40, 84, 0, 0, 9, 9, true, image);
		c.setAnimBufs(new String[]{"", 	"F0W1",  // ANIM1 (DAMAGED)
										"F0W1", // IDLE
										"F0W16F1W4F2W4F3W4F4W4F5W4F6W4F7W4F8W4", // ANIM2 (ATTACK1) 
										"", // ATTACK2
										"",	"", "","", ""});
		c.idle = new int[]{0, 0, 0, 0, 0};
		c.hx = c.hy = c.hw = c.hh = 0;		
		c.saveChrVersion5("C:\\enemy_thunder.chr");		*/		
		/*// SORCERER/MAGICIAN/WIZARD	
		image = new VImage(new URL("file:///C:\\Verge\\PS\\ps1_extra_stuff\\magician.png"), false);
		c = createCHRFromImage(0, 0, 52, 88, 0, 0, 5, 5, true, image);
		c.setAnimBufs(new String[]{"", 	"F4W2F0W2F4W2F0W2F4W2F0W2",  // ANIM1 (DAMAGED)
										"F0W1", // IDLE
										"F0W4F1W12F2W12F1W12F0W4", // ANIM2 (ATTACK1) 
										"F0W4F3W50F0W4", // ATTACK2
										"",	"", "","", ""});
		c.idle = new int[]{0, 0, 0, 0, 0};
		c.hx = c.hy = c.hw = c.hh = 0;		
		c.saveChrVersion5("C:\\magician.chr");*/
		/*// REAPER/MARAUDER/DEATH_KNIGHT	
		image = new VImage(new URL("file:///C:\\Verge\\PS\\ps1_extra_stuff\\death_knight.png"), false);
		c = createCHRFromImage(0, 0, 64, 104, 0, 0, 6, 6, true, image);
		c.setAnimBufs(new String[]{"", 	"F5W2F0W2F5W2F0W2F5W2F0W2",  // ANIM1 (DAMAGED)
										"F0W1", // IDLE
										"F0W12F1W12F2W6F3W6F4W6", // ANIM2 (ATTACK1) 
										"F0W52", // ATTACK2
										"",	"", "","", ""});
		c.idle = new int[]{0, 0, 0, 0, 0};
		c.hx = c.hy = c.hw = c.hh = 0;		
		c.saveChrVersion5("C:\\death_knight.chr");*/				
		/*// SPHINX/MANTICORE/SNOWLION	
		image = new VImage(new URL("file:///C:\\Verge\\PS\\ps1_extra_stuff\\snow_lion.png"), false);
		c = createCHRFromImage(0, 0, 56, 88, 0, 0, 7, 7, true, image);
		c.setAnimBufs(new String[]{"", 	"F6W2F0W2F6W2F0W2F6W2F0W2",  // ANIM1 (DAMAGED)
										"F0W1", // IDLE
										"F0W6F1W6F2W12F3W6F4W6F5W12F4W6F3W6F0W4", // ANIM2 (ATTACK1) 
										"F1W4F2W4F3W4F4W4F5W32F4W6F3W6F0W4", // ATTACK2
										"",	"", "","", ""});
		c.idle = new int[]{0, 0, 0, 0, 0};
		c.hx = c.hy = c.hw = c.hh = 0;		
		c.saveChrVersion5("C:\\snow_lion.chr");*/				
		/*// TARZIMAL	
		image = new VImage(new URL("file:///C:\\Verge\\PS\\ps1_extra_stuff\\tarzimal.png"), false);
		c = createCHRFromImage(0, 0, 48, 60, 0, 0, 5, 5, true, image);
		c.setAnimBufs(new String[]{"", 	"F4W2F0W2F4W2F0W2F4W2F0W2",  // ANIM1 (DAMAGED)
										"F0W1", // IDLE
										"F0W8F1W8F2W8F3W8F2W8F3W16F2W8F1W8F0W4", // ANIM2 (ATTACK1) 
										"F1W4F2W4F3W4F2W4F3W36F2W8F1W8F0W4", // ATTACK2
										"",	"", "","", ""});
		c.idle = new int[]{0, 0, 0, 0, 0};
		c.hx = c.hy = c.hw = c.hh = 0;		
		c.saveChrVersion5("C:\\tarzimal.chr");*/
		/*// SHELFISH/AMMONITE
		image = new VImage(new URL("file:///C:\\Verge\\PS\\ps1_extra_stuff\\ammonite.png"), false);
		c = createCHRFromImage(0, 0, 40, 120, 0, 0, 9, 9, true, image);
		c.setAnimBufs(new String[]{"", 	"F8W2F0W2F8W2F0W2F8W2F0W2",  // ANIM1 (DAMAGED)
										"F0W1", // IDLE
										"F0W4F1W6F2W6F3W6F4W6F5W6F6W6F7W12F1W6", // ANIM2 (ATTACK1) 
										"", // ATTACK2
										"",	"", "","", ""});
		c.idle = new int[]{0, 0, 0, 0, 0};
		c.hx = c.hy = c.hw = c.hh = 0;		
		c.saveChrVersion5("C:\\ammonite.chr");*/			
		/*// DARKFALZ
		image = new VImage(new URL("file:///C:\\Verge\\PS\\ps1_extra_stuff\\darkfalz.png"), false);
		c = createCHRFromImage(0, 0, 220, 173, 0, 0, 14, 14, true, image);
		c.setAnimBufs(new String[]{"", 	"F13W2F0W2F13W2F0W2F13W2F0W2",  // ANIM1 (DAMAGED)
										"F0W1", // IDLE
										"F0W4F1W6F2W6F3W4F4W4F5W4F6W4F4W4F5W4F6W4F4W4F5W4F6W4F7W4F8W4F9W4F10W4F11W4F12W6F1W4", // ANIM2 (ATTACK1) 
										"", // ATTACK2
										"",	"", "","", ""});
		c.idle = new int[]{0, 0, 0, 0, 0};
		c.hx = c.hy = c.hw = c.hh = 0;		
		c.saveChrVersion5("C:\\darkfalz.chr");*/
		// LASSIC
		/*image = new VImage(new URL("file:///C:\\Verge\\PS\\ps1_extra_stuff\\lassic.png"), false);
		c = createCHRFromImage(0, 0, 182, 168, 0, 0, 14, 14, true, image);
		c.setAnimBufs(new String[]{"", 	"F13W2F0W2F13W2F0W2F13W2F0W2",  // ANIM1 (DAMAGED)
										"F0W1", // IDLE
										"F0W6F1W4F2W4F3W4F4W4F5W4F6W4F7W4F8W4F9W4F10W4F11W4F12W4", // ANIM2 (ATTACK1) 
										"", // ATTACK2
										"",	"", "","", ""});
		c.idle = new int[]{0, 0, 0, 0, 0};
		c.hx = c.hy = c.hw = c.hh = 0;		
		c.saveChrVersion5("C:\\lassic.chr");*/
		/*// SPACESHIP1/SPACESHIP2
		image = new VImage(new URL("file:///C:\\Verge\\PS\\ps1_extra_stuff\\spaceship2.png"), false);
		c = createCHRFromImage(0, 0, 32, 32, 0, 0, 1, 1, true, image);
		c.setAnimBufs(new String[]{"", 	"F0W1",  // ANIM1 (DAMAGED)
										"F0W1", // IDLE
										"F0W1", // ANIM2 (ATTACK1) 
										"F0W1", // ATTACK2
										"",	"", "","", ""});
		c.idle = new int[]{0, 0, 0, 0, 0};
		c.hx = c.hy = c.hw = c.hh = 0; 		
		c.saveChrVersion5("C:\\spaceship2.chr");*/		
		/*// SPACESHIP1/SPACESHIP2
		image = new VImage(new URL("file:///C:\\Verge\\PS\\ps1_extra_stuff\\planet_palma.png"), false);
		c = createCHRFromImage(0, 0, 148, 64, 0, 0, 2, 2, true, image);
		c.setAnimBufs(new String[]{"", 	"F0W1",  // ANIM1 (DAMAGED)
										"F0W1", // IDLE
										"F0W1", // ANIM2 (ATTACK1) 
										"F0W1", // ATTACK2
										"",	"", "","", ""});
		c.idle = new int[]{0, 0, 1, 1, 1};
		c.hx = c.hy = c.hw = c.hh = 0; 		
		c.saveChrVersion5("C:\\palma.chr");		*/

		
		
		
		
		
		
	}
	
	public static void extractCharFromIrregularImage() throws MalformedURLException {
		//CHR c = createCHRFromImage(0, 28, 16, 32, 14, 14, 12, 12, true, image);

		int totalframes = 12;
		int sizex = 16, sizey = 32;

		VImage image = new VImage(new URL("file:///C:\\Verge\\PS\\PS\\new2\\ParmanianNPCs.png"), false);

		int[] startx = new int[]{	0, 30, 63,  // down
									340, 381, 422, // left
									218, 259, 303, // up
									97, 135, 174,  // right
									};
		
		int[] starty = new int[]{60, 106, 153, 196, 248, 299, 350, 402, 456, 504, 547, 594, 649, 693, 739, 792, 849, 910, 969, 1033, 1093, 1146, 1205, 1251, 1303, 1348, 1397, 1448, 1497, 1543 };
		
		for(int entityn=0; entityn <= 29; entityn++) {
		
			VImage[] images = new VImage[totalframes];
			for(int frames=0; frames<totalframes; frames++) {
				
				images[frames] = new VImage(sizex, sizey);
				images[frames].grabRegion(startx[frames], starty[entityn]-sizey, startx[frames]+sizex, starty[entityn], 0, 0, image); 
			}
				
			CHR c = createCHRFromImage(sizex, sizey, images);		
			
			c.setAnimBufs(new String[]{"", "F1W30F0W10F2W30F0W10", "F7W30F6W10F8W30F6W10", "F4W30F3W10F5W30F3W10", "F10W30F9W10F11W30F9W10",
					"F4W30F3W10F5W30F3W10", "F10W30F9W10F11W30F9W10","F4W30F3W10F5W30F3W10", "F10W30F9W10F11W30F9W10"});
	
			c.idle = new int[]{0, 6, 0, 3, 9};
	
			c.hx = 0;
			c.hy = 16;
			c.hw = 16;
			c.hh = 16;		
			//if(entityn>28)
			c.saveChrVersion5("C:\\ent" + entityn + ".chr");
		}
	}
	
	public static void processCharFromImage() throws MalformedURLException {
/*
		//for(int count=190; count<211; count++) {
			VImage image = new VImage(new URL("file:///C:\\Verge\\PS\\ps1_extra_stuff\\Dezorians.png"), false);
			//CHR c = createCHRFromImage(360, 216, 40, 72, 9, 9, false, image);
			
			for(int i=0;i<=10;i++) {
				CHR c = createCHRFromImage(0, 0+(i*33), 16, 32, 0, 0, 12, 12, true, image);
				
				c.setAnimBufs(new String[]{"", "F1W30F0W10F2W30F0W10", "F7W30F6W10F8W30F6W10", "F4W30F3W10F5W30F3W10", "F10W30F9W10F11W30F9W10",
						"F4W30F3W10F5W30F3W10", "F10W30F9W10F11W30F9W10","F4W30F3W10F5W30F3W10", "F10W30F9W10F11W30F9W10"});
	
				c.idle = new int[]{0, 6, 0, 3, 9};
		
				c.hx = 0;
				c.hy = 16;
				c.hw = 16;
				c.hh = 16;
				
				c.saveChrVersion5("C:\\Dezo" + i + ".chr");
			}
			*/
		// MOTAVIANS
		VImage image = new VImage(new URL("file:///C:\\Verge\\PS\\ps1_extra_stuff\\Motavians.png"), false);
		for(int i=0;i<=8;i++) {
			CHR c = createCHRFromImage(0, 0+(i*33), 16, 32, 0, 0, 12, 12, true, image);
			
			c.setAnimBufs(new String[]{"", "F1W30F0W10F2W30F0W10", "F7W30F6W10F8W30F6W10", "F4W30F3W10F5W30F3W10", "F10W30F9W10F11W30F9W10",
					"F4W30F3W10F5W30F3W10", "F10W30F9W10F11W30F9W10","F4W30F3W10F5W30F3W10", "F10W30F9W10F11W30F9W10"});

			c.idle = new int[]{0, 6, 0, 3, 9};
	
			c.hx = 0;
			c.hy = 16;
			c.hw = 16;
			c.hh = 16;
			
			c.saveChrVersion5("C:\\Mota" + i + ".chr");
		}
		
		
		
			/* MYAU
			VImage image = new VImage(new URL("file:///C:\\Myau.png"), false);
			CHR c = createCHRFromImage(0, 0, 16, 32, 4, 16+1, true, image);
			c.setAnimBufs(new String[]{"", "F0W16F1W16F2W16F3W16", "F8W16F9W16F10W16F11W16", "F4W16F5W32F6W16", "F12W16F13W32F14W16",
					"F4W16F5W32F6W16", "F12W16F13W32F14W16","F4W16F5W32F6W16", "F12W16F13W32F14W16"});
			c.idle = new int[]{0, 11, 3, 7, 15}; 
			c.hx = 0;			c.hy = 16;			c.hw = 16;			c.hh = 16;
			c.saveChrVersion5("C:\\myau.chr");
			*/

			/*// TARZIMAL
			VImage image = new VImage(new URL("file:///C:\\Verge\\PS\\ps1_extra_stuff\\Chr_tarzimal.png"), false);
			CHR c = createCHRFromImage(0, 0, 16, 32, 0, 0, 3, 12, true, image);
			c.setAnimBufs(new String[]{"", "F1W30F0W10F2W30F0W10", "F7W30F6W10F8W30F6W10", "F4W30F3W10F5W30F3W10", "F10W30F9W10F11W30F9W10",
					"F4W30F3W10F5W30F3W10", "F10W30F9W10F11W30F9W10","F4W30F3W10F5W30F3W10", "F10W30F9W10F11W30F9W10"});
			c.idle = new int[]{0, 6, 0, 3, 9}; 
			c.hx = 0;			c.hy = 16;			c.hw = 16;			c.hh = 16;
			c.saveChrVersion5("C:\\tarzimal.chr");
			*/
			
			
		//}

	}
	
	
	public static void processMultipleCharsFromImageGenerations() throws MalformedURLException {
		//for(int count=190; count<211; count++) {
			VImage image = new VImage(new URL("file:///C:\\Rbp\\Rpg\\PS\\Generation\\mapdat\\psg1_sprite_mapdat_006" + ".png"));
			//CHR c = createCHRFromImage(360, 216, 40, 72, 9, 9, false, image);
			CHR c = createCHRFromImage(0, 0, 40, 72, 0, 0, 3, 9, false, image);
			BufferedImage[] newBuffer = new BufferedImage[12];
			for(int i=0;i<9;i++) {
				newBuffer[i] = c.frames[i];
			}
			newBuffer[9] = VImage.flipImage(40, 72, c.frames[3]); 
			newBuffer[10] = VImage.flipImage(40, 72, c.frames[4]);
			newBuffer[11] =	VImage.flipImage(40, 72, c.frames[5]);	
			c.frames = newBuffer;
			c.totalframes = 12;
			
			c.setAnimBufs(new String[]{"", "F0W30F1W10F2W30F1W10", "F6W30F7W10F8W30F7W10", "F3W30F4W10F5W30F4W10", "F9W30F10W10F11W30F10W10",
					"F3W30F4W10F5W30F4W10", "F9W30F10W10F11W30F10W10", "F3W30F4W10F5W30F4W10", "F9W30F10W10F11W30F10W10"});
			c.idle = new int[]{0, 7, 1, 4, 10}; 
	
			c.hx = 8;
			c.hy = 48;
			c.hw = 24;
			c.hh = 24;
			
			c.saveChrVersion5("C:\\JavaRef3\\EclipseWorkspace\\PS\\src\\ps\\chars\\entity.chr");
		//}

	}

}
