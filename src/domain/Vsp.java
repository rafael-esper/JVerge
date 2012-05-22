package domain;

import static core.Script.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import persist.ExtendedDataInputStream;
import persist.ExtendedDataOutputStream;
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
	
	private int signature = VSP_SIGNATURE;
	private int version = VSP_VERSION;
	private int tileSize = 16;
	private int format = 1;
	private int compression = 1;
	private int numtiles = 0;
	
	private Animation[] anims = new Animation[0];
	private byte[] obsPixels = new byte[16*16]; // width * height * 1 bytes!
	int numobs;
	
	int vadelay[], tileidx[], flipped[];
	
	// [Rafael, the Esper]
	private BufferedImage [] tiles;
	
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
			
			System.out.println(this.signature + ";"+this.version+";"+this.getNumtiles()+";"+this.compression);
			
			byte[] vspdata = f.readCompressedUnsignedShortsIntoBytes(); // tileCount * width * height * 3 bytes!
				
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
			tileidx = new int[getNumtiles()];
			flipped = new int[getNumtiles()];
			vadelay = new int[numAnim];
			int i;
			for (i=0; i<numAnim; i++)
				vadelay[i]=0;
			for (i=0; i<getNumtiles(); i++)
			{
				flipped[i] = 0;
				tileidx[i] = i;
			}
			mytimer = systemtime;
			
			
			// Obtém tiles a partir dos vetores (vspdata)
			System.out.println("Numtiles: " + getNumtiles() + "(" + vspdata.length + " bytes)");
			this.tiles = f.getBufferedImageArrayFromPixels(vspdata, getNumtiles(), 16, 16);
			//for(int x=0; x<tiles.length; x++)
				//Script.graycolorfilter(tiles[x]);
			
			
		 }catch (IOException e) {
			 System.out.println("IOException : " + e);
		 }		

	}
	
	private void save(String filename) {

		System.out.println("VSP::save at " + filename);
		ExtendedDataOutputStream f = null;
		try {
			OutputStream os = new FileOutputStream(filename);
			f = new ExtendedDataOutputStream(os);
			
			
			f.writeSignedIntegerLittleEndian(this.signature);
			f.writeSignedIntegerLittleEndian(this.version);
			f.writeSignedIntegerLittleEndian(this.tileSize);
			f.writeSignedIntegerLittleEndian(this.format);
			f.writeSignedIntegerLittleEndian(this.getNumtiles());
			f.writeSignedIntegerLittleEndian(this.compression);
			
			System.out.println(this.signature + ";"+this.version+";"+this.getNumtiles()+";"+this.compression);
			byte[] pixels = f.getPixelArrayFromFrames(tiles, tiles.length, this.tileSize, this.tileSize);
			f.writeCompressedBytes(pixels);

			f.writeSignedIntegerLittleEndian(this.anims.length);
	        
	        for(int i=0; i<this.anims.length; i++) {
	        	Animation a = anims[i];
	        	f.writeFixedString(a.name, 256);
	        	f.writeSignedIntegerLittleEndian(a.start);
	        	f.writeSignedIntegerLittleEndian(a.finish);
	        	f.writeSignedIntegerLittleEndian(a.delay);
	        	f.writeSignedIntegerLittleEndian(a.mode);
	        }			

	        f.writeSignedIntegerLittleEndian(this.numobs);
	        
			f.writeCompressedBytes(this.obsPixels);
	
		}
		catch(IOException e) {
			System.err.println("VSP::save " + e.getMessage());
		}
		finally {
			try {
				f.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}	

	public void exportToClipboard(int tiles_per_row) {
		
		int row_size = tiles_per_row*16;
		VImage clipboard = new VImage(row_size, (this.getNumtiles()/tiles_per_row+1) * 16);
		Font font = new Font("Serif", Font.PLAIN, 7);
		
		for(int i=0; i<this.getNumtiles(); i++) {
			clipboard.blit((i*16)%row_size, i/tiles_per_row*16, getTiles()[i]);
			//if(i%tiles_per_row == 0)
				//clipboard.printstring(0, i/tiles_per_row*16+7, font, Integer.toString(i/tiles_per_row)); 
		}
		clipboard.copyImageToClipboard();

	}
	
	
	public static void main(String args[]) throws MalformedURLException {

		Vsp v = new Vsp(new URL("file:///C:\\JavaRef3\\EclipseWorkspace\\PS\\src\\ps\\psg.vsp"));
		v.exportToClipboard(32);
		/*
		int tiles_per_row = 32;
		
		boolean marcados[] = new boolean[v.getNumtiles()];
		int newNumTiles = v.getNumtiles();
		for(int t1=0; t1<v.getNumtiles()/tiles_per_row; t1++) {
			
			for(int t2=t1+1; t2<v.getNumtiles()/tiles_per_row; t2++) {

				System.out.println("Comparando linha " + t1 + " com " + t2);
				
				int equal = 0;
				for(int idx=0; idx<tiles_per_row; idx++) {
					System.out.println("\tTile " + idx);

					int tile1 = 534;//t1*tiles_per_row + idx;
					int tile2 = 3606;//t2*tiles_per_row + idx;
					if(v.getTiles()[tile1].getRGB(11, 12) != v.getTiles()[tile2].getRGB(11, 12)) {
						System.out.println(v.getTiles()[tile1].getRGB(11, 12) +"\t"+ v.getTiles()[tile2].getRGB(11, 12));
						break;
					}
					else
					{
						equal++;
					}
				}
				if(equal == tiles_per_row) {
					System.out.println("Row " + t1 + " EQUAL to row " + t2);
					newNumTiles-=tiles_per_row;
					for(int p=t2;p<t2+tiles_per_row;p++)
						marcados[p] = true;
				}
				
			}
			
		}
		
		BufferedImage[] newTiles = new BufferedImage[newNumTiles];
		int pos = 0;
		for(int x=0; x<v.getNumtiles(); x++) {
			if(pos < newNumTiles && !marcados[x]) {
				newTiles[pos] = v.getTiles()[x];
				pos++;
			}
		}*/
		//v.numtiles = newNumTiles;
		//v.tiles = newTiles;
		//v.save("C:\\PSG.VSP");
		
		//Vsp v = new Vsp(new URL("file:///C:\\JavaRef3\\EclipseWorkspace\\PS\\src\\ps\\ps1.vsp"));
		//v.save("c:\\temp.vsp");
		//Vsp v1 = new Vsp(new URL("file:///C:\\TEMP.VSP"));
		
		/*createVspFromImages(new VImage[]{
				new VImage(new URL("file:///C:\\Rbp\\Rpg\\PS\\Generation\\mapdat\\psg1_sprite_mapdat_015.png")),
				new VImage(new URL("file:///C:\\Rbp\\Rpg\\PS\\Generation\\mapdat\\psg1_sprite_mapdat_016.png")),
				new VImage(new URL("file:///C:\\Rbp\\Rpg\\PS\\Generation\\mapdat\\psg1_sprite_mapdat_017.png")),
				//new VImage(new URL("file:///C:\\Rbp\\Rpg\\PS\\Generation\\mapdat\\psg1_sprite_mapdat_019.png")),
				//new VImage(new URL("file:///C:\\Rbp\\Rpg\\PS\\Generation\\mapdat\\psg1_sprite_mapdat_020.png")),
				//new VImage(new URL("file:///C:\\Rbp\\Rpg\\PS\\Generation\\mapdat\\psg1_sprite_mapdat_021.png")),
				//new VImage(new URL("file:///C:\\Rbp\\Rpg\\PS\\Generation\\mapdat\\psg1_sprite_mapdat_023.png")),
				//new VImage(new URL("file:///C:\\Rbp\\Rpg\\PS\\Generation\\mapdat\\psg1_sprite_mapdat_024.png")),
				//new VImage(new URL("file:///C:\\Rbp\\Rpg\\PS\\Generation\\mapdat\\psg1_sprite_mapdat_025.png")),
				//new VImage(new URL("file:///C:\\Rbp\\Rpg\\PS\\Generation\\mapdat\\psg1_sprite_mapdat_027.png")),
				//new VImage(new URL("file:///C:\\Rbp\\Rpg\\PS\\Generation\\mapdat\\psg1_sprite_mapdat_028.png")),
				//new VImage(new URL("file:///C:\\Rbp\\Rpg\\PS\\Generation\\mapdat\\psg1_sprite_mapdat_029.png"))
				
				
				
								new VImage(new URL("file:///C:\\Rbp\\Rpg\\PS\\Generation\\mapdat\\psg1_sprite_mapdat_171.png")),
								new VImage(new URL("file:///C:\\Rbp\\Rpg\\PS\\Generation\\mapdat\\psg1_sprite_mapdat_172.png")),
								new VImage(new URL("file:///C:\\Rbp\\Rpg\\PS\\Generation\\mapdat\\psg1_sprite_mapdat_173.png")),
								new VImage(new URL("file:///C:\\Rbp\\Rpg\\PS\\Generation\\mapdat\\psg1_sprite_mapdat_175.png")),
								new VImage(new URL("file:///C:\\Rbp\\Rpg\\PS\\Generation\\mapdat\\psg1_sprite_mapdat_176.png")),
								new VImage(new URL("file:///C:\\Rbp\\Rpg\\PS\\Generation\\mapdat\\psg1_sprite_mapdat_177.png")),
								new VImage(new URL("file:///C:\\Rbp\\Rpg\\PS\\Generation\\mapdat\\psg1_sprite_mapdat_178.png")),
								new VImage(new URL("file:///C:\\Rbp\\Rpg\\PS\\Generation\\mapdat\\psg1_sprite_mapdat_180.png")),
								new VImage(new URL("file:///C:\\Rbp\\Rpg\\PS\\Generation\\mapdat\\psg1_sprite_mapdat_181.png")),
								new VImage(new URL("file:///C:\\Rbp\\Rpg\\PS\\Generation\\mapdat\\psg1_sprite_mapdat_182.png")),
								new VImage(new URL("file:///C:\\Rbp\\Rpg\\PS\\Generation\\mapdat\\psg1_sprite_mapdat_170.png")),
		});*/
		
	}
	
	static void createVspFromImages(VImage[] images) {
		
		// First pixel is default transparent color
		//Color transC = new Color(images[0].image.getRGB(0, 0));
		
		ArrayList<VImage> allTiles = new ArrayList<VImage>();
				
		for(int img=0; img<images.length; img++) {
		
			int posx=0, posy=0;	
			int sizex = images[img].width;
			int sizey = images[img].height;
			System.out.println("Analysing image " + img);
			for(int j=0; j<sizey/16;j++) {
				for(int i=0; i<sizex/16;i++) {
					VImage newTile = new VImage(16, 16);
					newTile.grabregion(posx, posy, posx+16, posy+16, 0, 0, images[img].image);					
					posx+=16;

					// Checks for repeated tile 
					/*int repeated = 0;
					for(BufferedImage bi: allTiles) {
						for(int py=0;py<16;py++)
							for(int px=0;px<16;px++)
								if(bi.getRGB(px,  py) == newTile.getRGB(px, py)) {
									repeated++;
								}
								else
									{px=20;py=20;}
					}
					if(repeated < 256)*/
						allTiles.add(newTile);
					/*else
						allTiles.add(new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB));
					repeated = 0;*/
				}
				posy+=16;
				posx=0;
			}				
			
			
		}
		
		VImage clipboard = new VImage(512, (allTiles.size()/32+1) * 16);
		Font font = new Font("Serif", Font.PLAIN, 7);
		
		Vsp v = new Vsp();
		v.tiles = new BufferedImage[allTiles.size()];
		System.out.println("Got " + allTiles.size() + " tiles");
		for(int i=0; i<allTiles.size(); i++) {
			v.getTiles()[i] = allTiles.get(i).image;
			clipboard.blit((i*16)%512, i/32*16, v.getTiles()[i]);
			clipboard.printstring((i*16)%512, i/32*16+16, font, Integer.toString(i)); 
		}
		clipboard.copyImageToClipboard();
		
		v.numtiles = v.tiles.length;
		v.obsPixels = new byte[512];
		for(int i=0; i<512; i++) {
			v.obsPixels[i] = (byte) ((i >= 256) ? 1: 0);
		}
		v.save("C:\\TEMP.VSP");
	}
	
	public int getNumtiles() {
		return numtiles;
	}

	public BufferedImage [] getTiles() {
		return tiles;
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
		// tileidx[index] = the actual pointer to a tile, can change due to VSP animation
		if (index >= getNumtiles() || tileidx[index] >= getNumtiles()) {
			System.err.printf("VSP::BlitTile(), tile %d exceeds %d", index, getNumtiles());
			return;
		}
		dest.blit(x, y, current_map.tileset.getTiles()[tileidx[index]]);
		//dest.g.drawImage(current_map.tileset.tiles[index], x, y, Color.BLACK, null);
		
	}

	public void TBlit(int x, int y, int index, VImage dest)
	{
		while (mytimer < systemtime)
		{
			AnimateTiles();
			mytimer++;
		}
		//if (index >= numtiles) err("VSP::BlitTile(), tile %d exceeds %d", index, numtiles);
		if (index >= getNumtiles() || tileidx[index] >= getNumtiles()) {
			System.err.printf("VSP::TBlitTile(), tile %d exceeds %d", index, getNumtiles());
			return;
		}
		dest.tblit(x, y, current_map.tileset.getTiles()[tileidx[index]]);
		//dest.g.drawImage(current_map.tileset.tiles[index], x, y, null);
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
			if (anims[i].start<0 || anims[i].start>=getNumtiles() || anims[i].finish<0 || anims[i].finish>=getNumtiles())
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

