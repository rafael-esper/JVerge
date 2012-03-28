package core;

import java.awt.AlphaComposite;
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;

import javax.swing.JComponent;


/**
 * Better performance with Volatile Image
 * 
 * - http://java.sun.com/j2se/1.4/pdf/VolatileImage.pdf
 * - http://www.javalobby.org/java/forums/m91823967.html
 * - http://docs.oracle.com/javase/1.4.2/docs/api/java/awt/image/VolatileImage.html
 * - http://content.gpwiki.org/index.php/Java:Tutorials:VolatileImage
 *  *
 */

public class OldGUICanvas extends Canvas { //javax.swing.JPanel {
	
	private GraphicsConfiguration gc = null;
	private Image canvas_screen;
	private VolatileImage vImg;
	private int curWidth, curHeight, valCode;
	private float alpha = 1;
	
	public void update(Graphics g) {
		paint(g);
	}

	/** Don't call directly. Use repaint().*/
	@Override
	public void paint(Graphics g) {

		if(canvas_screen == null)
			return;

		if(alpha != 1f) {
			Graphics2D g2d = (Graphics2D) g;
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_IN, alpha));
		}
		if(vImg == null)
			createBackBuffer();
		do {
			// Test if surface is lost and restore it.
			gc = this.getGraphicsConfiguration();
			valCode = vImg.validate(gc);
			// No need to check for IMAGE_RESTORED since we are
			// to re-render the image anyway.
			if(valCode==VolatileImage.IMAGE_INCOMPATIBLE){
				createBackBuffer();
			}
			// Render to the Image
			//((Graphics2D)vImg.getGraphics()).setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			vImg.getGraphics().drawImage(canvas_screen, 0, 0, curWidth, curHeight, this);
			// Render image to screen.
			g.drawImage(vImg, 0, 0, this);
			//java.awt.Toolkit.getDefaultToolkit().sync();
			// Test if content is lost
			} while(vImg.contentsLost());

	}

	void createBackBuffer() {
		GraphicsConfiguration gc = getGraphicsConfiguration();
		if(gc!=null && curWidth > 0 && curHeight > 0)
			vImg = gc.createCompatibleVolatileImage(curWidth,curHeight);
	}
	
	void updateSize(int w, int h) {
		this.curWidth = w;
		this.curHeight = h;
		createBackBuffer();
	}
	

	public Image getCanvas_screen() {
		return canvas_screen;
	}
	public void setCanvas_screen(Image canvas_screen) {
		this.canvas_screen = canvas_screen;
	}
	public void setAlpha(float i) {
		if(i < 0 || i > 1)
			return;
		this.alpha = i;
	}



}
