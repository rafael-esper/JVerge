package domain;

public class Layer {
	
	public static final int DEFAULT_X = 30;
	public static final int DEFAULT_Y = 20;
	
	public String name = "";
	public double parallax_x = 1.0, parallax_y = 1.0;
	public int width = DEFAULT_X, height = DEFAULT_Y; // Unsigned short
	public int lucent = 0; // Unsigned Byte

	public int x_offset, y_offset; // used to account for changing parallax
	
	public int[] tiledata = new int[DEFAULT_X*DEFAULT_Y]; // width * height Unsigned shorts!


	public int GetTile(int x, int y)
	{
		if (x<0 || y<0 || x>=width || y>=height) return 0;
		return tiledata[(y*width)+x];
	}

	public void SetTile(int x, int y, int t)
	{
		if (x<0 || y<0 || x>=width || y>=height) return;
		tiledata[(y*width)+x] = t;
	}
	
	void SetParallaxX(double p, int xwin) // rbp: changed to receive xwin
	{
	    // increase the x_offset to the current layer pos given the current parallax
	    x_offset += (int) ((float) xwin * parallax_x);

	    // then reduce it by what the parallax will be
	    x_offset -= (int) ((float) xwin * p);

	    // then we can set the parallax
	    parallax_x = p;

	}

	void SetParallaxY(double p, int ywin) // rbp: changed to receive ywin
	{
	    // increase the x_offset to the current layer pos given the current parallax
	    y_offset += (int) ((float) ywin * parallax_y);

	    // then reduce it by what the parallax will be
	    y_offset -= (int) ((float) ywin * p);

	    // then we can set the parallax
	    parallax_y = p;
	}
	
	
	public String toString() {
		return "Layer " + name + ": (" + parallax_x + ", " + parallax_y + ") (" + width + ", " + height + ") " + lucent + " Data: " + tiledata;
	}
}
