package domain;

import static core.VergeEngine.*;
import static core.Script.*;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;

import core.Script;

import persist.ExtendedDataInputStream;
import persist.ExtendedDataOutputStream;

import domain.Entity;
import domain.Layer;
import domain.Zone;

public class Map {

	// .map format: https://github.com/Bananattack/v3tiled/wiki/.map-file

	public static final String MAP_SIGNATURE = "V3MAP";
	public static final int MAP_VERSION = 2;
	public static final int DEFAULT_X = 30;
	public static final int DEFAULT_Y = 20;

	private String filename = "";
	private String mapname = "dummy";
	private String vspname = "";
	private String musicname= "";
	public String renderstring = "R";
	private String startupscript = "";
	public int startX, startY; // Unsigned short

	private Layer[] layers;
	private byte[] obsLayer; 
	private int[] zoneLayer; 									// width * height, Unsigned shorts!

	public Zone[] zones;
	private Entity[] entities;

	public Vsp tileset;

	public boolean horizontalWrapable = false;  // [Rafael, the Esper]
	public boolean verticalWrapable = false;  // [Rafael, the Esper]
	
	
	public String toString() {
		return "Mapname: " + filename + "; vspFile:" + vspname + "; music:"
				+ musicname + "; render:" + renderstring + "; startEvent: "
				+ startupscript + "; start:" + startX + "," + startY;
	}

	public Map(String strFilename) {
		this(Script.load(strFilename));
	}
	
	public Map(URL url) {
		try {
			if(url==null)
				throw new IOException();
			
			this.filename =  url.getFile().substring( url.getFile().lastIndexOf('/')+1);
				
			this.load(url.openStream());
			//FileInputStream fis = new FileInputStream(path + "\\" + filename);
			//this.load(fis);
			
			// Load the vsp (map URL minus the map file plus the vsp file)
			//this.tileset = new Vsp(url.getFile().substring(0, url.getFile().lastIndexOf('/')+1) + this.vspname);
			this.tileset = new Vsp(Script.load(this.vspname));
			
			// Diassociated with loading the map
			startMap();
			
		} catch (IOException ioe) {
			System.err.println("MAP::IOException (" + filename + "), url = " + url);
		}
	}

	public Map(URL mapUrl, URL vspUrl) {
		try {
			this.load(mapUrl.openStream());
		} catch (IOException e) {
			System.err.println("MAP::IOException (" + filename + "), mapurl = " + mapUrl);
		}
		this.tileset = new Vsp(vspUrl);
		
	}

	/**
	 * Loads a Map from an InputStream
	 * 
	 */
	private void load(InputStream is) {

		try {

			ExtendedDataInputStream f = new ExtendedDataInputStream(is);

			// Begin to read
			String mapSignature = f.readFixedString(6);
			if (!mapSignature.equals(MAP_SIGNATURE)) {
				throw new IOException("Map doesn't contain V3MAP signature: " + mapSignature);
			}

			int mapVersion = f.readSignedIntegerLittleEndian();
			int vcOffset = f.readSignedIntegerLittleEndian();
			System.out.println("Map version:" + mapVersion + "; Vcoffset: "
					+ vcOffset);

			// Map information
			this.mapname = f.readFixedString(256);
			this.vspname = f.readFixedString(256);
			this.musicname = f.readFixedString(256);
			this.renderstring = f.readFixedString(256);
			this.startupscript = f.readFixedString(256);
			this.startX = f.readUnsignedShortLittleEndian();
			this.startY = f.readUnsignedShortLittleEndian();

			int numLayers = f.readSignedIntegerLittleEndian(); // layers.length
			this.layers = new Layer[numLayers];

			for (int i = 0; i < numLayers; i++) {
				Layer l = new Layer();
				l.name = f.readFixedString(256);
				l.parallax_x = f.readDoubleLittleEndian();
				l.parallax_y = f.readDoubleLittleEndian();
				l.width = f.readUnsignedShortLittleEndian();
				l.height = f.readUnsignedShortLittleEndian();
				l.lucent = f.readUnsignedByte();
				l.tiledata = f.readCompressedUnsignedShorts();

				this.layers[i] = l;
			}

			// Read compressed (oLayer)
			this.obsLayer = f.readCompressedBytes();

			// Read compressed (zLayer)
			this.zoneLayer = f.readCompressedUnsignedShorts();

			int numZones = f.readSignedIntegerLittleEndian(); // zones.length
			this.zones = new Zone[numZones];
			System.out.println("numZones = " + numZones);

			for (int i = 0; i < numZones; i++) {
				Zone z = new Zone();
				z.name = f.readFixedString(256);
				z.script = f.readFixedString(256);
				z.percent = f.readUnsignedByte();
				z.delay = f.readUnsignedByte();
				z.method = f.readUnsignedByte();

				this.zones[i] = z;
			}

			int numEntities = f.readSignedIntegerLittleEndian(); // entities.length
			this.entities = new Entity[numEntities];
			System.out.println("numEntities = " + numEntities);

			for (int i = 0; i < numEntities; i++) {
				//Entity e = new Entity();
				int x = f.readUnsignedShortLittleEndian();
				int y = f.readUnsignedShortLittleEndian();
				Entity e = new Entity(x*16, y*16, null);
				e.face = f.readByte();
				e.obstructable = f.readByte() == 0 ? false : true;
				e.obstruction = f.readByte() == 0 ? false : true;
				e.autoface = f.readByte() == 0 ? false : true;
				e.speed = f.readUnsignedShortLittleEndian();
				f.readByte(); // unused
				e.movecode = f.readByte();
				e.wx1 = f.readUnsignedShortLittleEndian();
				e.wy1 = f.readUnsignedShortLittleEndian();
				e.wx2 = f.readUnsignedShortLittleEndian();
				e.wy2 = f.readUnsignedShortLittleEndian();
				e.wdelay = f.readUnsignedShortLittleEndian();
				f.readInt(); // unused
				e.movescript = f.readFixedString(256);

				switch(e.movecode) {
					case 0: e.SetMotionless(); break;
					case 1: e.SetWanderZone(); break;
					case 2: e.SetWanderBox(e.wx1, e.wy1, e.wx2, e.wy2); break; //FIXME
					case 3: e.SetMoveScript(e.movescript); break;				
				}
				
				e.chrname = f.readFixedString(256);
				e.description = f.readFixedString(256);
				e.script = f.readFixedString(256);  // this is the actual script

				this.entities[i] = e;
			}

			// VC Code
			
			f.close();
			
		} catch (IOException e) {
			System.out.println("IOException : " + e);
		}
	}

	/**
	 * Saves a Map to a specified file path
	 * 
	 */
	public void save(String strFilePath) {

		// create FileOutputStream object
		try {
			FileOutputStream fos = new FileOutputStream(strFilePath);
			int vcoffset = this.save(this, fos);
			
			RandomAccessFile raf = new RandomAccessFile(strFilePath, "rw");
			raf.seek(10);
			raf.writeInt(Integer.reverseBytes(vcoffset));
			raf.close();
		}
		catch(FileNotFoundException fnfe) {
			System.err.println("Map::FileNotFoundException : " + strFilePath);
			fnfe.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Saves a Map to a specified output stream
	 * 
	 * @param Map m
	 * @param OutputStream
	 */
	public int save(Map m, OutputStream os) {

		ExtendedDataOutputStream f = null;
		try {
			f = new ExtendedDataOutputStream(os);

			// Begin to write
			f.writeString(Map.MAP_SIGNATURE);
			f.writeSignedIntegerLittleEndian(Map.MAP_VERSION);

			// Write a dummy offset for now, but this needs to be backpatched,
			// once the real map is completed.
			int vc = f.size();
			f.writeSignedIntegerLittleEndian(0);
			f.writeFixedString(m.mapname, 256);
			f.writeFixedString(m.vspname, 256);
			f.writeFixedString(m.musicname, 256);
			f.writeFixedString(m.renderstring, 256);
			f.writeFixedString(m.startupscript, 256);
			f.writeUnsignedShortLittleEndian(m.startX);
			f.writeUnsignedShortLittleEndian(m.startY);

			f.writeSignedIntegerLittleEndian(m.layers.length);
			for (Layer l : m.layers) {
				f.writeFixedString(l.name, 256);
				f.writeDoubleLittleEndian(l.parallax_x);
				f.writeDoubleLittleEndian(l.parallax_y);
				f.writeUnsignedShortLittleEndian(l.width);
				f.writeUnsignedShortLittleEndian(l.height);
				f.writeUnsignedByte(100 - (int) (l.lucent * 100.0 + 0.5));
				//l.tiledata[0] = 1;
				//l.tiledata[1] = 15;
				//l.tiledata[2] = 800;
				//l.tiledata[3] = 14000;
				f.writeCompressedUnsignedShorts(l.tiledata);
			}

			f.writeCompressedBytes(m.obsLayer);
			f.writeCompressedUnsignedShorts(m.zoneLayer);

			f.writeSignedIntegerLittleEndian(m.zones.length);
			for (Zone z : m.zones) {
				f.writeFixedString(z.name, 256);
				f.writeFixedString(z.script, 256);
				f.writeUnsignedByte(z.percent);
				f.writeUnsignedByte(z.delay);
				f.writeUnsignedByte(z.method);
			}

			f.writeSignedIntegerLittleEndian(m.entities.length);
			for (Entity e : m.entities) {
				f.writeUnsignedShortLittleEndian(e.getx()/256);
				f.writeUnsignedShortLittleEndian(e.gety()/256);
				f.writeByte(e.face); // 0 or 1
				f.writeByte(e.obstructable == false ? 0 : 1);
				f.writeByte(e.obstruction == false ? 0 : 1);
				f.writeByte(e.autoface == false ? 0 : 1);
				f.writeUnsignedShortLittleEndian(e.speed);
				f.writeByte(0);
				f.writeByte(e.movecode);
				f.writeUnsignedShortLittleEndian(e.wx1);
				f.writeUnsignedShortLittleEndian(e.wy1);
				f.writeUnsignedShortLittleEndian(e.wx2);
				f.writeUnsignedShortLittleEndian(e.wy2);
				f.writeUnsignedShortLittleEndian(e.wdelay);
				f.writeSignedIntegerLittleEndian(0);
				f.writeFixedString(e.movescript, 256);
				f.writeFixedString(e.chrname, 256);
				f.writeFixedString(e.description, 256);
				f.writeFixedString(e.script, 256);
			}

			// Write the vc offset. (Don't need, but see RandomAccessFile)
			 int end = f.size();
			// f.seek(vc);
			f.writeSignedIntegerLittleEndian(0);

			System.err.println("[Rafael, the Esper]SAVE: " + f.size());
			f.close();

			return end;
			
		} catch (IOException e) {
			System.out.println("IOException : " + e);
		}
		return 0;
	}
	

	// Rafael: Code diassociated with map loading
	void startMap() {

		if(!musicname.trim().isEmpty())
			playmusic(Script.load(musicname));
		
		current_map = this;
		//se.LoadMapScript(f, mapfname);
		
		for(int i=0; i<current_map.entities.length; i++) {
			Entity e = current_map.entities[i];
			e.chr = new CHR(e.chrname); //RequestCHR(e.chrname);
			
			e.index = Script.numentities++;
			entity.add(e);
			
		}

		//TODO Check if this is needed
		//if(this.tileset.numobs == 0)
			//this.tileset.numobs = 1;
		
		if(startupscript != null && !startupscript.trim().equals(""))
			callfunction(startupscript);

	}
	
	
	// Use

	public int getzone(int x, int y) {
		if (x < 0 || y < 0 || x >= getWidth() || y >= getHeight())
			return 0;
		return zoneLayer[(y * getWidth()) + x];
	}

	public boolean getobs(int x, int y) {
		if (x < 0 || y < 0 || x >= getWidth() || y >= getHeight())
			return true;
		if (obsLayer[(y * getWidth()) + x] == 0)
			return false;
		return true;
	}

	public boolean getobspixel(int x, int y) { // modified by [Rafael, the Esper]
		if (!horizontalWrapable && (x < 0 || (x >> 4) >= getWidth()))
				return true;
		if (!verticalWrapable && (y < 0 || (y >> 4) >= getHeight()))
				return true;
		if(horizontalWrapable && x < 0)
			x+= (getWidth() << 4); 
		if(horizontalWrapable && (x >> 4) >= getWidth())
			x-= (getWidth() << 4);
		if(verticalWrapable && y < 0)
			y+= (getHeight() << 4); 
		if(verticalWrapable && (y >> 4) >= getHeight())
			y-= (getHeight() << 4);

		int t = obsLayer[((y >> 4) * getWidth()) + (x >> 4)];
		return tileset.GetObs(t, x&15, y&15);
	}
	
	public int gettile(int x, int y, int i) { 
		if(i>=this.layers.length) 
			return 0; 
		return this.layers[i].GetTile(x,y); 
	}
		

	public void render(int x, int y, VImage dest) {
		boolean first = true;
		int tx = (dest.width / 16) + 2;
		int ty = (dest.height / 16) + 2;
		String src = new String(renderstring);

		for (int i = 0; i < src.length(); i++) {
			char token = src.charAt(i);
			if (token == ',') {
				continue;
			}

			if (token == 'E') {
				RenderEntities(dest);
				continue;
			}
			if (token == 'R') {
				hookretrace();
				continue;
			}
			if (Character.isDigit(token)) {
				int layer = Integer.parseInt(Character.toString(token))-1;
				if (first) {
					BlitLayer(false, layer, tx, ty, x, y, dest);
					//dest.rectfill(0,0,dest.width,dest.height,java.awt.Color.GRAY);
					first = false;
					continue;
				}
				BlitLayer(true, layer, tx, ty, x, y, dest);
			}
		}

	}

	public void setzone(int x, int y, int z) {
		if (x < 0 || y < 0 || x >= getWidth() || y >= getHeight())
			return;
		if (z >= zones.length)
			return;
		zoneLayer[(y * getWidth()) + x] = z;
	}

	public void setobs(int x, int y, int t) {
		if (x < 0 || y < 0 || x >= getWidth() || y >= getHeight())
			return;
		if (t >= tileset.numobs && t!=0)
			return;
		obsLayer[(y * getWidth()) + x] = (byte) t;
	}
	
	public void settile(int x, int y, int i, int z) { 
		if(i>=this.layers.length) 
			return; 
		else this.layers[i].SetTile(x,y,z); 
	}
	

	void BlitLayer(boolean transparent, int l, int tx, int ty, int xwin, int ywin, VImage dest) {
		if(l >= layers.length || layers[l] == null) 
			return; //[Rafael, the Esper] 
		
		// we add offsets here because if the parallax changes while the
		// xwin and ywin are non-zero, we would jump unless we compensate
		int oxw = layers[l].x_offset + (int) ((float) xwin * layers[l].parallax_x);
		int oyw = layers[l].y_offset + (int) ((float) ywin * layers[l].parallax_y);
		int xofs = -(oxw & 15);
		int yofs = -(oyw & 15);
		int xtc = oxw >> 4;
		int ytc = oyw >> 4;

		if (transparent)
			if (layers[l].lucent != 0)
				setlucent(layers[l].lucent); 

		tileset.UpdateAnimations();

		for (int y = 0; y < ty; y++) {
			for (int x = 0; x < tx; x++) {
				int c = 0;
				if(horizontalWrapable && verticalWrapable)  // Changed by [Rafael, the Esper]
					c = layers[l].GetTile((xtc + x+getWidth())%(getWidth()), (ytc + y+getHeight())%(getHeight()));
				else if(!horizontalWrapable && verticalWrapable)
					c = layers[l].GetTile((xtc + x), (ytc + y+getHeight())%(getHeight()));
				else if(horizontalWrapable && !verticalWrapable)
					c = layers[l].GetTile((xtc + x+getWidth())%(getWidth()), (ytc + y));
				else if(!horizontalWrapable && !verticalWrapable)
					c = layers[l].GetTile(xtc + x, ytc + y);
				
				if (transparent) {
					if (c != 0) {
						tileset.TBlit((x * 16) + xofs, (y * 16) + yofs, c, dest);
					}
				} else {
					tileset.Blit((x * 16) + xofs, (y * 16) + yofs, c, dest);
				}
			}
		}
		//if (dest == screen) {
			// TODO Uncomment RenderLayerSprites(l);
		//}

		if (transparent)
			setlucent(0);
	}

	public int getWidth() {
		if (layers != null && layers[0] != null) {
			return layers[0].width;
		}
		return 0;
	}

	public int getHeight() {
		if (layers != null && layers[0] != null) {
			return layers[0].height;
		}
		return 0;
	}

	public static void main(String args[]) throws MalformedURLException {
		/* Save map to clipboard*/ 
		Map m = new Map(new URL("file:///C:\\JavaRef3\\EclipseWorkspace\\PS\\src\\ps\\Palma.map"),
				new URL("file:///C:\\JavaRef3\\EclipseWorkspace\\PS\\src\\ps\\ps1.vsp"));
		  current_map = m;
		  VImage img = new VImage(m.getWidth()*16, m.getHeight()*16);
		  m.render(0, 0, img);
		  img.copyImageToClipboard();
		

/*		Map m = new Map(new URL("file:///C:\\JavaRef3\\EclipseWorkspace\\PS\\src\\ps\\Palma.map"),
					new URL("file:///C:\\JavaRef3\\EclipseWorkspace\\PS\\src\\ps\\palma.vsp"));

		m.renderstring = "1,2,E,3";
	*/	
		/*for(int j=0;j<m.getHeight();j++)
			for(int i=0;i<m.getWidth();i++) {
				
				if(m.gettile(i, j, 1)>1) {
					m.settile(i, j, 2, m.gettile(i, j, 1));
					m.settile(i, j, 1, 0);
				}

				if(m.gettile(i, j, 1)==1)
					m.settile(i, j, 1,0);
				
				if(m.gettile(i, j, 0)==2140) // grass
					m.settile(i, j, 0, 148 + (i%4) + ((j%4) * 32));
				else if(m.gettile(i, j, 0)==2141) // wood
					m.settile(i, j, 0, 493 + (i%4) + ((j%4) * 32));
				else if(m.gettile(i, j, 0)==2173) // sand
					m.settile(i, j, 0, 516 + (i%2) + ((j%2) * 32));
				else if(m.gettile(i, j, 0)==2205) // high grass
					m.settile(i, j, 0, 282 + (i%2) + ((j%2) * 32));
				else if(m.gettile(i, j, 0)==2175) // rock
					m.settile(i, j, 0, 2 + (i%4) + ((j%2) * 32));
				else if(m.gettile(i, j, 0)==2172 || m.gettile(i, j, 0)==2204) // water
					m.settile(i, j, 0, 2136 + (i%4) + ((j%4) * 32));
				else if(m.gettile(i, j, 0)==2174) // lava
					m.settile(i, j, 0, 196 + (i%2) + ((j%2) * 32));
				else if(m.gettile(i, j, 0)==2206) { // mountain base 1
					m.settile(i, j, 1, 1964 + (i%3) + ((j%3) * 32));
					m.settile(i, j, 0, 148 + (i%4) + ((j%4) * 32));}
				else if(m.gettile(i, j, 0)==2207) { // mountain base 2
					m.settile(i, j, 1, 1967 + (i%3) + ((j%3) * 32));
					m.settile(i, j, 0, 148 + (i%4) + ((j%4) * 32));}
				else if(m.gettile(i, j, 0)==2142) { // mountain 1
					m.settile(i, j, 2, 1868 + (i%3) + ((j%3) * 32));
					m.settile(i, j, 0, 148 + (i%4) + ((j%4) * 32));}
				else if(m.gettile(i, j, 0)==2143) { // mountain 2
					m.settile(i, j, 2, 1871 + (i%3) + ((j%3) * 32));
					m.settile(i, j, 0, 148 + (i%4) + ((j%4) * 32));}
			}*/
			
		
		
		/*m.layers = new Layer[2];
		m.layers[0] = createLayerFromImage(new URL("file:///C:\\a\\palma1.png"), m.tileset);
		m.layers[1] = createLayerFromImage(new URL("file:///C:\\a\\palma2.png"), m.tileset);
		//m.layers[2] = createLayerFromImage(new URL("file:///C:\\a\\camineet3.png"), m.tileset);
		m.renderstring = "1,E,2";
		m.vspname = "palma.vsp";
		if(m.startupscript == null)
			m.startupscript = "";
		if(m.filename == null)
			m.filename = "";

		if(m.zoneLayer==null)
			m.zoneLayer = new int[m.getHeight() * m.getWidth()];
		if(m.obsLayer==null)
			m.obsLayer = new byte[m.getHeight() * m.getWidth()];
		*/
		
		m.save("C:\\JavaRef3\\EclipseWorkspace\\PS\\src\\ps\\Palma.map");
		
	}

	private static Layer createLayerFromImage(URL url, Vsp vsp) {

		Layer l = new Layer();
		VImage source = new VImage(url);
		l.width = source.width / 16;
		l.height = source.height / 16;
		
		l.tiledata = new int[l.width * l.height];
		
		for(int posy=0;posy < l.height; posy++) {
			for(int posx=0;posx < l.width; posx++) {	
				System.out.println("Testing (" + posy + ", " + posx + ")");
				for(int i=0; i<vsp.getNumtiles(); i++) {
				//	if(i==0) {System.err.println(vsp.tiles[i].getRGB(0, 0));System.exit(0);}
					int repeated = 0;
					for(int py=0;py<16;py++) {
						for(int px=0;px<16;px++) {
							if(vsp.getTiles()[i].getRGB(px,  py) == source.image.getRGB(posx*16+px, posy*16+py)
							|| vsp.getTiles()[i].getRGB(px,  py) == source.image.getRGB(posx*16+px, posy*16+py) + 16711935	
									) {
								repeated++;
							}
							else
							{
								px=20;py=20;
							}
						}
					}
				
					if(repeated >= 200) {
						System.out.println("Found tile " + i);
						l.tiledata[(posy*l.width)+posx] = i;
						break;
					}
					else
						l.tiledata[(posy*l.width)+posx] = 0;
				}
			}
		}
		

		return l;
	}

	public String getFilename() {
		return this.filename;
	}

	public String getMapname() {
		return this.mapname;
	}
	
	
}
