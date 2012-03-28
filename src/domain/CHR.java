package domain;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import core.DefaultPalette;
import core.Script;

import persist.ExtendedDataInputStream;

public class CHR {

	public final static String  CHR_SIGNATURE	=	"5392451";
	public final static int  CHR_VERSION5	=		5;
	public final static int  CHR_VERSION4	=		4;
	public final static int  CHR_VERSION2	=		2;
	
	
	//private byte[] pixels = new byte[16*16*3]; // frames * width * height * 3 bytes!
	
	int fxsize, fysize;					// frame x/y dimensions
	int hx, hy;							// x/y obstruction hotspot
	public int hw;							// hotspot width/hieght
	public int hh;
	int totalframes;					// total # of frames.
    int idle[] = new int[5];			// idle frames

	int animsize[] = new int[9];
	int anims[][] = new int[9][];
    //String movescript[] = new String[8];

	String filename;                        // the filename this was loaded from
	
	String parsestr;

	// rbp
	public BufferedImage [] frames;
	
	
	public CHR(String strFilename) {
		this(Script.load(strFilename));
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
				System.err.println("Versão " + version + " diferente do suportado.");
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
			//System.out.println("Leu: " + animbuf);
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
			//System.out.println("Leu: " + animbuf);
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
	
	private void loadChrVersion5(ExtendedDataInputStream f) throws IOException {

		f.readUnsignedIntegerLittleEndian(); // bitDepth
		f.readUnsignedIntegerLittleEndian(); // unused, poss. alpha blend
		
		//c.transr = (char) 
		f.readUnsignedByte();
		//c.transg = (char) 
		f.readUnsignedByte();
		//c.transb = (char) 
		f.readUnsignedByte();
		//c.transa = (char) 
		f.readUnsignedByte();
		
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
		
		String animbuf;
		int indexes[] = { 0, 2, 1, 3, 4, 5, 6, 7, 8 };
		
		// Creates an array with size equal to the total "wait" time of the animation
		// Each index in the anims array points to a frame
		// So a F1W5F2W5 will insert in the array the values 1 1 1 1 1 2 2 2 2 2
		for(int b=1; b<9; b++) {
			int length = f.readSignedIntegerLittleEndian(); // animation length
			animbuf = f.readFixedString(length+1);
			System.out.println("Leu: " + animbuf);
			this.animsize[indexes[b]] = this.GetAnimLength(animbuf);
			if(this.animsize[indexes[b]] == 0)
				this.animsize[indexes[b]]=1; // rbp
			this.anims[indexes[b]] = new int[this.animsize[indexes[b]]];
			this.ParseAnimation(indexes[b], animbuf);
		}
		
		// Pixels
		f.readSignedIntegerLittleEndian();
		//int uncompressedSize = f.readSignedIntegerLittleEndian();
		f.readSignedIntegerLittleEndian();
		byte pixels[] = f.readCompressedUnsignedShortsIntoBytes();
		
		// Obtém frames a partir dos vetores (pixels)
		System.err.println("Frames (" + fxsize + ", " + fysize + "): " + totalframes);
		System.err.println(pixels.length);
		frames = f.getBufferedImageArrayFromPixels(pixels, totalframes, fxsize, fysize); 
		
	}

	public void render(int x, int y, int frame, VImage dest)
	{
		x -= hx;
		y -= hy;
		if (frame <0 || frame >= totalframes)
			System.err.printf("CHR::render(), frame requested is undefined (%d of %d)", frame, totalframes);
		
		//RBP container.data = (quad *) ((int) rawdata->data + (frame*fxsize*fysize*vid_bytesperpixel));
		//RBP TBlit(x, y, container, dest);
		dest.g.drawImage(this.frames[frame], x, y, null);
		
	//SetLucent(50);
	//Rect(x+hx, y+hy, x+hx+hw-1, y+hy+hh-1, 0, dest);
	//SetLucent(0);
	}
	
	int GetFrame(int d, int framect)
	{
		if (d<1 || d>4)
			System.err.printf("CHR::GetFrame() - invalid direction %d", d);
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
		parsestr = anim;
	
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
					//System.out.println("Anim(F" + frame + "), sobrou " + parsestr.substring(parsecount));
					break;
				case 'w':
				case 'W':
					parsecount++;
					len = GetArg(parsestr.substring(parsecount));
					for (i=ofs; i<ofs+len; i++)
						this.anims[d][i] = frame;
					ofs += len;
					parsecount+=Integer.toString(len).length();
					//System.out.println("Anim(W" + len + "), sobrou " + parsestr.substring(parsecount));
					break;
				default:
					System.err.printf("CHR::ParseAnimation() - invalid animscript command! %c", parsestr.charAt(parsecount));
			}
		}
	}
	
	int GetAnimLength(String anim)
	{
		int length = 0;
		parsestr = anim;
	
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
	
		if(retorno.trim().equals("")) // rbp
			return 0;
		return Integer.parseInt(retorno);
	}
	
	
	public static void main(String[] args) {
		String strFilePath = "D:\\RBP\\PESSOAL\\VERGE\\V3TILED\\CRYSTAL.CHR";
		
		CHR chr = new CHR(strFilePath);

	}	

	
}
