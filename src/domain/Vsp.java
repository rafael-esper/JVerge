package domain;

import static core.Script.*;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import persist.ExtendedDataInputStream;
import core.Script;

public class Vsp {

	public static final int  VID_BYTESPERPIXEL	=	3;
	
	public static final int  VSP_SIGNATURE	=	5264214;
	public static final int  VSP_VERSION	=		6;
	
	public static final int ANIM_MODE_FORWARD   = 0;
	public static final int ANIM_MODE_BACKWARD  = 1;
	public static final int ANIM_MODE_RANDOM    = 2;
	public static final int ANIM_MODE_PINGPONG  = 3;

	public static int mytimer;
	
	// .vsp format: 	https://github.com/Bananattack/v3tiled/wiki/.vsp-file
	
	public int signature = VSP_SIGNATURE;
	public int version = VSP_VERSION;
	public int tileSize = 16;
	public int format = 1;
	public int compression = 1;

	public int numtiles = 0;
	
	public byte[] vspdata = new byte[numtiles*16*16*3]; // tileCount * width * height * 3 bytes!
	public Animation[] anims = new Animation[0];
	public byte[] obsPixels = new byte[numtiles*16*16]; // tileCount * width * height * 1 bytes!
	int numobs;
	
	int vadelay[], tileidx[], flipped[];
	
	// [Rafael, the Esper]
	public BufferedImage [] tiles;
	
	public Vsp() {
		
	}
	
	public Vsp(URL urlpath) {
		try {
			this.load(urlpath.openStream());
			
		} catch (FileNotFoundException fnfe) {
			error("VSP::FileNotFoundException : " + urlpath);
		} catch (IOException e) {
			error("VSP::IOException : " + e.getMessage());
		}		
	}
	
	
	private void load (InputStream fis) {

		try    
		{
			ExtendedDataInputStream f = new ExtendedDataInputStream(fis);
			
			this.signature = f.readSignedIntegerLittleEndian();
			this.version = f.readSignedIntegerLittleEndian();
			this.tileSize = f.readSignedIntegerLittleEndian();
			this.format = f.readSignedIntegerLittleEndian();
			this.numtiles = f.readSignedIntegerLittleEndian();
			this.compression = f.readSignedIntegerLittleEndian();
			
			System.out.println(this.signature + ";"+this.version+";"+this.numtiles+";"+this.compression);
			this.vspdata =  f.readCompressedUnsignedShortsIntoBytes(); // f.readCompressedUnsignedShorts();
				
	        int numAnim = f.readSignedIntegerLittleEndian();	// anim.length
	        this.anims = new Vsp.Animation[numAnim];	
	        System.out.println("numAnim = " + numAnim);
	        
	        for(int i=0; i<numAnim; i++) {
	        	Vsp.Animation a = this.new Animation();
	        	a.name = f.readFixedString(256);
	        	a.start = f.readSignedIntegerLittleEndian();
	        	a.finish = f.readSignedIntegerLittleEndian();
	        	a.delay = f.readSignedIntegerLittleEndian();
	        	a.mode = f.readSignedIntegerLittleEndian();
	        	
	        	this.anims[i] = a;
	        }			

	        this.numobs = f.readSignedIntegerLittleEndian();	// obs.length
	        System.out.println("numObs = " + numobs);
	        
			this.obsPixels = f.readCompressedUnsignedShortsIntoBytes();

			/* Obs array DEBUG
			 * for(int i=0;i<obsPixels.length;i++) {
	        	if(i%16==0)
	        		System.out.println();
	        	if(i%256==0)
	        		System.out.println();
	        	System.out.print(obsPixels[i]);
	        }*/
	        
			f.close();
			
			// initialize tile anim stuff
			tileidx = new int[numtiles];
			flipped = new int[numtiles];
			vadelay = new int[numAnim];
			int i;
			for (i=0; i<numAnim; i++)
				vadelay[i]=0;
			for (i=0; i<numtiles; i++)
			{
				flipped[i] = 0;
				tileidx[i] = i;
			}
			mytimer = systemtime;
			
			
			// Obtém tiles a partir dos vetores (vspdata)
			System.out.println("Numtiles: " + numtiles + "(" + vspdata.length + " bytes)");
			tiles = f.getBufferedImageArrayFromPixels(vspdata, numtiles, 16, 16);
			//for(int x=0; x<tiles.length; x++)
				//Script.graycolorfilter(tiles[x]);
			
			
		 }catch (IOException e) {
			 System.out.println("IOException : " + e);
		 }		

	}
	
	
	
	
	boolean GetObs(int t, int x, int y)
	{
		if (t==0) return false;
		if (t>=numobs || t<0) return true;
		if (x<0 || y<0 || x>15 || y>15) return true;
		return obsPixels[(t*256)+(y*16)+x] == 0 ? false: true;
	}
	
	public void UpdateAnimations()
	{
		while (mytimer < systemtime)
		{
			AnimateTiles();
			mytimer++;
		}
	}
	public void Blit(int x, int y, int index, VImage dest)
	{
		//if (index >= numtiles) err("VSP::BlitTile(), tile %d exceeds %d", index, numtiles);
		if (index >= numtiles) return;
		index = tileidx[index]; // Get the actual pointer to a tile, can change due to VSP animation
		if (index >= numtiles) return;
		if (index >= numtiles) System.err.printf("VSP::BlitTile(), tile %d exceeds %d", index, numtiles);
		//[Rafael, the Esper] char tile = (char)vspdata.data + (index<<8) * VID_BYTESPERPIXEL;
		//[Rafael, the Esper] BlitTile(x, y, tile, dest);
		dest.g.drawImage(current_map.tileset.tiles[index], x, y, Color.BLACK, null);
		//getJGEngine().buf_gfx.drawImage(current_map.tileset.tiles[index], x, y, getJGEngine());
		
	}

	public void TBlit(int x, int y, int index, VImage dest)
	{
		while (mytimer < systemtime)
		{
			AnimateTiles();
			mytimer++;
		}
		//if (index >= numtiles) err("VSP::BlitTile(), tile %d exceeds %d", index, numtiles);
		if (index >= numtiles) return;
		index = tileidx[index];
		if (index >= numtiles) return;
		if (index >= numtiles) System.err.printf("VSP::BlitTile(), tile %d exceeds %d", index, numtiles);
		//[Rafael, the Esper] char tile = (char)vspdata.data + (index<<8) * VID_BYTESPERPIXEL;
		//[Rafael, the Esper] TBlitTile(x, y, tile, dest);
		dest.g.drawImage(current_map.tileset.tiles[index], x, y, null);
		//getJGEngine().buf_gfx.drawImage(current_map.tileset.tiles[index], x, y, getJGEngine());
	}

	void BlitObs(int x, int y, int index, VImage dest)
	{
		if (index >= numobs) return;
		//[Rafael, the Esper] char c[] = (char) obs + (index * 256);
		//[Rafael, the Esper] int white = MakeColor(255,255,255);
		for (int yy=0; yy<16; yy++)
			for (int xx=0; xx<16; xx++)
				;//[Rafael, the Esper] if (c[(yy*16)+xx]>0) PutPixel(x+xx, y+yy, white, dest);
	}

	void AnimateTile(int i, int l)
	{
		switch (anims[i].mode)
		{
		    case ANIM_MODE_FORWARD:
				if (tileidx[l]<anims[i].finish) tileidx[l]++;
	            else tileidx[l]=anims[i].start;
	            break;
			case ANIM_MODE_BACKWARD:
				if (tileidx[l]>anims[i].start) tileidx[l]--;
	            else tileidx[l]=anims[i].finish;
	            break;
			case ANIM_MODE_RANDOM:
				tileidx[l]=Script.random(anims[i].start, anims[i].finish);
	            break;
			case ANIM_MODE_PINGPONG:
				if (flipped[l]>0)
	            {
					if (tileidx[l]!=anims[i].start) tileidx[l]--;
					else { tileidx[l]++; flipped[l]=0; }
	            }
	            else
	            {
					if (tileidx[l]!=anims[i].finish) tileidx[l]++;
					else { tileidx[l]--; flipped[l]=1; }
	            }
				break;
		}
	}

	void AnimateTiles()
	{
		for (int i=0; i<anims.length; i++)
		{
			if(anims[i] == null || vadelay==null)		// [Rafael, the Esper]
				return;
			
			if ((anims[i].delay>0) && (anims[i].delay<vadelay[i]))
			{
				vadelay[i]=0;
				for (int l=anims[i].start; l<=anims[i].finish; l++)
					AnimateTile(i,l);
			}
			vadelay[i]++;
		}
	}

	void ValidateAnimations()
	{
		for (int i=0; i<anims.length; i++)
			if (anims[i].start<0 || anims[i].start>=numtiles || anims[i].finish<0 || anims[i].finish>=numtiles)
				System.err.printf("VSP::ValidateAnimations() - animation %d references out of index tiles", i);
	}
	
	
	
	
	
	public class Animation {
		
		public String name = "";
		public int start = 0, finish = 0;
		
		public int delay, mode; 
		
		public String toString() {
			return "Animation: " + name + "; startTile:" + start + "; endTile:" + finish + "; delay:" + delay + "; mode: " + mode;
		}

	}
	
}

