package domain;

import static core.VergeEngine.*;
import static core.Script.*;

import java.awt.Color;

public abstract class MapAbstract implements Map {
	
	protected String renderstring = "R";
	protected Vsp tileset;
	protected int startX, startY; // Unsigned short
	
	protected boolean horizontalWrapable = false;  // [Rafael, the Esper]
	protected boolean verticalWrapable = false;  // [Rafael, the Esper]

	

	
	public boolean getHorizontalWrapable() {
		return this.horizontalWrapable;
	}
	public boolean getVerticalWrapable() {
		return this.verticalWrapable;
	}
	public void setHorizontalWrapable(boolean b) {
		this.horizontalWrapable = b;
	}
	public void setVerticalWrapable(boolean b) {
		this.verticalWrapable = b;
	}

	public Vsp getTileSet() {
		return this.tileset;
	}

	public int getStartX() {
		return this.startX;
	}

	public int getStartY() {
		return this.startY;
	}

	
	
	
	public void render(int x, int y, VImage dest) {
		boolean first = true;
		int tx = (dest.width / 16) + 2;
		int ty = (dest.height / 16) + 2;

		for (int i = 0; i < renderstring.length(); i++) {
			char token = renderstring.charAt(i);
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
					
					// Rafael: For an unknown reason, it's better to draw the first layer as a
					// transparent one than the original code drawing it non-transparent (2x FPS)
					//dest.rectfill(0,0,dest.width,dest.height,java.awt.Color.BLACK);
					blitLayer(true, layer, tx, ty, x, y, dest);
					first = false;
					continue;
				}
				blitLayer(true, layer, tx, ty, x, y, dest);
			}
		}
		//dest.g.drawString(myself.getx()/16 + "/" + getWidth(), 0, 30);
	}

	private void blitLayer(boolean transparent, int l, int tx, int ty, int xwin, int ywin, VImage dest) {
		if(l >= this.getNumLayers()) 
			return; //[Rafael, the Esper] 
		
		// we add offsets here because if the parallax changes while the
		// xwin and ywin are non-zero, we would jump unless we compensate
		//int oxw = this.layers[l].x_offset + (int) ((float) xwin * this.layers[l].parallax_x);
		//int oyw = this.layers[l].y_offset + (int) ((float) ywin * this.layers[l].parallax_y);
		int oxw = xwin; // TODO Change this simplification to the code above
		int oyw = ywin; // TODO Change this simplification to the code above
		int xofs = -(oxw & 15);
		int yofs = -(oyw & 15);
		int xtc = oxw >> 4;
		int ytc = oyw >> 4;

		if (transparent)
			if (this.getLayerLucent(l) != 0)
				setlucent(this.getLayerLucent(l)); 

		if(this.tileset.UpdateAnimations()) {
			resetCacheArray();
		}

		// Initialize cache arrays
		if(imgcache.length < this.getNumLayers()) {
			xcache = getCacheArray(getNumLayers());
			ycache = getCacheArray(getNumLayers());
			imgcache = new VImage[getNumLayers()];
		}
		
		if(imgcache[l]==null) {
			imgcache[l] = new VImage(dest.width+16, dest.height+16);
		}
		
		// Draw layer into the cache
		if(xtc!=xcache[l] || ytc!=ycache[l]) {
			if(transparent) {
				imgcache[l].g.setBackground(new Color(255, 255, 255, 0));
				imgcache[l].g.clearRect(0, 0, imgcache[l].width, imgcache[l].height);
			}
			for (int y = 0; y < ty+1; y++) {
				for (int x = 0; x < tx+1; x++) {
					int c = 0;
					if(horizontalWrapable && verticalWrapable)  // Changed by [Rafael, the Esper]
						c = gettile((xtc + x+getWidth())%(getWidth()), (ytc + y+getHeight())%(getHeight()), l);
					else if(!horizontalWrapable && verticalWrapable)
						c = gettile((xtc + x), (ytc + y+getHeight())%(getHeight()), l);
					else if(horizontalWrapable && !verticalWrapable)
						c = gettile((xtc + x+getWidth())%(getWidth()), (ytc + y), l);
					else if(!horizontalWrapable && !verticalWrapable)
						c = gettile(xtc + x, ytc + y, l);
					
					if (transparent) {
						if (c != 0 || l==0) {
							tileset.TBlit((x * 16), (y * 16), c, imgcache[l]);
							//tileset.TBlit((x * 16) + xofs, (y * 16) + yofs, c, dest);
						}
					} else {
							tileset.Blit((x * 16), (y * 16), c, imgcache[l]);
							//tileset.Blit((x * 16) + xofs, (y * 16) + yofs, c, dest);
					}
				}
			}
		}

		// New code to allow blitting the whole image, instead of tile per tile
		if(transparent)
			dest.tblit(xofs, yofs, imgcache[l]);
		else
			dest.blit(xofs, yofs, imgcache[l]);
		xcache[l] = xtc;
		ycache[l] = ytc;

		
		//if (dest == screen) {
			// TODO Uncomment RenderLayerSprites(l);
		//}

		if (transparent)
			setlucent(0);
	}

	static int[] xcache = getCacheArray(1);
	static int[] ycache = getCacheArray(1);
	static VImage[] imgcache = new VImage[1];
	

	private static int[] getCacheArray(int size) {
		int[] ret = new int[size];
		for(int i=0; i<ret.length; i++)
			ret[i] = -1;
		return ret;
	}
	protected static void resetCacheArray() {
		xcache = getCacheArray(1);
		ycache = getCacheArray(1);
		imgcache = new VImage[1];
	}	

}
