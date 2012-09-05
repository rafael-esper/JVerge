package domain;

import domain.Entity;

/**
 * A map is an object that groups tiles, obstructions, zones and entities. 
 * Each map has one tileset.
 * Each map points to N entities.
 * 
 *     --------       -------       ---------
 *     |Entity| N...1 | Map | 1...1 |TileSet|
 *     --------       -------       ---------
 */


public interface Map {

	// Default map size
	static final int DEFAULT_X = 30;
	static final int DEFAULT_Y = 20;

	String getMapname();

	int getWidth();
	int getHeight();
	
	int getNumLayers();

	int getStartX();
	int getStartY();

	int gettile(int x, int y, int i);
	void settile(int x, int y, int i, int z);

	boolean getobs(int x, int y);
	void setobs(int x, int y, int t);

	boolean getobspixel(int x, int y);
	
	void render(int x, int y, VImage dest);
	
	String getFilename(); // ??
	
	Vsp getTileSet();
	
	boolean getHorizontalWrapable();
	boolean getVerticalWrapable();
	
	Entity[] getEntities();

	String getRenderstring();
	void setRenderstring(String string);

	// Methods related to zones
	int getzone(int x, int y);
	void setzone(int x, int y, int z);

	String getScriptZone(int zone);
	int getPercentZone(int zone);
	int getMethodZone(int zone);

	// Methods related to layers
	int getLayerLucent(int layer);
		
	public void setHorizontalWrapable(boolean b);
	public void setVerticalWrapable(boolean b);
	

}
