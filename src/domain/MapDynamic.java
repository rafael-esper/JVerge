package domain;

import static core.Script.callfunction;
import static core.Script.current_map;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Properties;

import core.Script;

public class MapDynamic extends MapAbstract implements Map {

	private VImage[] layers;
	private String mapName;
	private HashMap<Integer, Integer> mappings;
	private int scaleFactor = 1;
	
	
	public MapDynamic(String strFilename) {
		this(Script.load(strFilename));
	}
	
	public MapDynamic(URL properties) {
		Properties mapProperties = new Properties();
		try {
			mapProperties.load(properties.openStream());
			this.renderstring = mapProperties.getProperty("RenderString");
			this.startX = Integer.parseInt(mapProperties.getProperty("StartX"));
			this.startY = Integer.parseInt(mapProperties.getProperty("StartY"));
			
			String strTileset = mapProperties.getProperty("Tileset");
			this.tileset = new Vsp(Script.load(strTileset));
			
			String strLayers[] = mapProperties.getProperty("Layers").split(",");
			this.layers = new VImage[strLayers.length];
			for(int i=0; i<layers.length; i++) {
				layers[i] = new VImage(Script.load(strLayers[i].trim()));
			}
			
			this.mappings = new HashMap<Integer, Integer>() {
			{ // Associates each pixel color to a tile from the given tileset
				put(new Color(0, 150, 0).getRGB(), 106);
				put(new Color(30, 100, 30).getRGB(), 37);
				put(new Color(0, 200, 0).getRGB(), 51);
				put(new Color(0, 255, 0).getRGB(), 58);
				put(new Color(0, 0, 255).getRGB(), 7);
				put(new Color(240, 240, 0).getRGB(), 32);
				put(new Color(150, 150, 50).getRGB(), 35);
				put(new Color(200, 200, 240).getRGB(), 76);
				put(new Color(200, 0, 0).getRGB(), 103);
				put(new Color(201, 0, 0).getRGB(), 104);
			}};
			
			current_map = this;
			callfunction("mapinit");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getMapname() {
		return "Dynamic map: " + getFilename();
	}
	
	public int getWidth() {
		if(layers!=null && layers[0]!=null) {
			return layers[0].width * scaleFactor;
		}
		return 0;
	}

	public int getHeight() {
		if(layers!=null && layers[0]!=null) {
			return layers[0].height * scaleFactor;
		}
		return 0;
	}

	public int gettile(int x, int y, int i) {
		if(x<0 || y<0 || x >= this.getWidth() || y >= this.getHeight()) {
			return 0;
		}
		
		if(layers[i] != null) {
			Integer c = mappings.get(layers[i].getImage().getRGB(x/scaleFactor, y/scaleFactor));
			if(c!=null)
				return c;
		}
		return 0;
	}

	
	public void settile(int x, int y, int i, int z) {
		if(layers[i] != null) {
			layers[i].getImage().setRGB(x * scaleFactor, y * scaleFactor, z);
		}
	}

	
	public boolean getobs(int x, int y) {
		if(x<0 || y<0 || x >= this.getWidth() || y >= this.getHeight())
			return true;
		
		return false;
	}

	public void setobs(int x, int y, int t) {
		// TODO Auto-generated method stub
		
	}
	
	public boolean getobspixel(int x, int y) {
		if(x<0 || y<0 || x>>4 >= this.getWidth() || y>>4 >= this.getHeight())
			return true;
		
		return false;
	}
	
	public String getFilename() {
		return mapName;
	}
	
	public Entity[] getEntities() {
		return null;
	}
	
	public String getRenderstring() {
		return this.renderstring;
	}
	
	public void setRenderstring(String string) {
		this.renderstring = string;
	}
	
	public int getzone(int x, int y) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public void setzone(int x, int y, int z) {
		// TODO Auto-generated method stub
	}

	public String getScriptZone(int zone) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getPercentZone(int zone) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public int getMethodZone(int zone) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getNumLayers() {
		return layers.length;
	}

	public int getLayerLucent(int layer) {
		return 0;
	}

}
