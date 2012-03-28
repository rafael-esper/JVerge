package domain;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import persist.PCXReader;

import core.VergeEngine;

public class VImage implements Transferable {

	public BufferedImage image;
	public Graphics g;
	
	public int width, height;
	
	public VImage(int x, int y) {
		this.width = x;
		this.height = y;
		//GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		//GraphicsDevice gs = ge.getDefaultScreenDevice();
		//GraphicsConfiguration gc = gs.getDefaultConfiguration();
		//image = gc.createCompatibleImage(x, y);
		image = new BufferedImage(x, y, BufferedImage.TYPE_INT_ARGB);
		g = image.getGraphics();
	}
	
	 public VImage(URL url) {
		  try {
			  if(url==null) {
				  System.err.println("Unable to find image from URL " + url);
				  return;
			  }
			  if(url.getFile().toUpperCase().endsWith("PCX")) {
				  image = PCXReader.loadImage(url.openStream());
			  } else
			  {			  
				  image = ImageIO.read(url);
			  }
			  //if(image==null)
				//  image = new BufferedImage(320, 240, BufferedImage.TYPE_INT_ARGB); // RBP Temp
		  } catch (IOException e) {
			  System.err.println("Unable to read image from URL " + url);
		  }
		  this.width = image.getWidth();
		  this.height = image.getHeight();

		  // Make pink = transparent
		  Image img = makeColorTransparent(image, new Color(255, 0, 255));
		  this.image = imageToBufferedImage(img);		  
		  
		  g = image.getGraphics();
	 }
	 
	 /*public VImage(URL url, boolean transparent) {
		 this(url);

		 // Post-processing for transparent pixels
		 if(transparent) {
			 int tcolor = new Color(255, 0, 255).getRGB();
			 Image img = makeColorTransparent(image, new Color(tcolor));
			 this.image = imageToBufferedImage(img);
		 }
		 // End-code
	 }*/
	
	public BufferedImage getImage() {
		return image;
	}

	
	   private static BufferedImage imageToBufferedImage(Image image) {

	        BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
	        Graphics2D g2 = bufferedImage.createGraphics();
	        g2.drawImage(image, 0, 0, null);
	        g2.dispose();

	        return bufferedImage;

	    }

	   //http://stackoverflow.com/questions/665406/how-to-make-a-color-transparent-in-a-bufferedimage-and-save-as-png 
	   public static Image makeColorTransparent(BufferedImage im, final Color color) {
	        ImageFilter filter = new RGBImageFilter() {

	                // the color we are looking for... Alpha bits are set to opaque
	                public int markerRGB = color.getRGB() | 0xFF000000;

	                public final int filterRGB(int x, int y, int rgb) {
	                        if ((rgb | 0xFF000000) == markerRGB) {
	                                // Mark the alpha bits as zero - transparent
	                                return 0x00FFFFFF & rgb;
	                        } else {
	                                // nothing to do
	                                return rgb;
	                        }
	                }
	        };

	        ImageProducer ip = new FilteredImageSource(im.getSource(), filter);
	        return Toolkit.getDefaultToolkit().createImage(ip);
	    }

		// Fast copy of a BufferedImage 
	   // http://stackoverflow.com/questions/2825837/java-how-to-do-fast-copy-of-a-bufferedimages-pixels-unit-test-included
	   public static void copySrcIntoDstAt(final BufferedImage src, final BufferedImage dst, final int dx, final int dy) 
	   {
		    int[] srcbuf = ((java.awt.image.DataBufferInt) src.getRaster().getDataBuffer()).getData();
		    int[] dstbuf = ((java.awt.image.DataBufferInt) dst.getRaster().getDataBuffer()).getData();
		    int width = src.getWidth();
		    int height = src.getHeight();
		    int dstoffs = dx + dy * dst.getWidth();
		    int srcoffs = 0;
		    for (int y = 0 ; y < height ; y++ , dstoffs+= dst.getWidth(), srcoffs += width ) {
		        System.arraycopy(srcbuf, srcoffs , dstbuf, dstoffs, width);
		    }
		}

	   // Transfer to Clipboard
	   // http://elliotth.blogspot.com/2005/09/copying-images-to-clipboard-with-java.html
	   public void copyImageToClipboard() {
	        //VImage imageSelection = new VImage(image.getWidth(null), image.getHeight(null));
	        //imageSelection.image = (BufferedImage) image;
	        Toolkit toolkit = Toolkit.getDefaultToolkit();
	        toolkit.getSystemClipboard().setContents(this, null);
	    }
	    
	   public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
	        if (flavor.equals(DataFlavor.imageFlavor) == false) {
	            throw new UnsupportedFlavorException(flavor);
	        }
	        return image;
	    }
	   
	    public boolean isDataFlavorSupported(DataFlavor flavor) {
	        return flavor.equals(DataFlavor.imageFlavor);
	    }
	   
	    public DataFlavor[] getTransferDataFlavors() {
	        return new DataFlavor[] {
	            DataFlavor.imageFlavor
	        };
	    }	   
	    
}
