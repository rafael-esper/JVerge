package core;

import java.awt.AWTException;
import java.awt.AlphaComposite;
import java.awt.BufferCapabilities;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.ImageCapabilities;
import java.awt.Toolkit;
import java.awt.BufferCapabilities.FlipContents;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferStrategy;

import static core.VergeEngine.*;
import static core.Script.*;

import javax.swing.JFrame;

public class GUI extends JFrame implements ComponentListener {
	
	Canvas canvas = new Canvas();
	Controls control = new Controls();
	static VergeEngine gameThread; 
	static BufferStrategy strategy;
	
	private int winwidth, winheight;
	private static int curwidth;
	private static int curheight;
	boolean win_decoration = false;
	private static float alpha = 1f;

	static long cycleTime;
	private static int frameDelay = 20; // 20ms. implies 50fps (1000/20) = 50

	
	public GUI(int w, int h) {
		// build and display your GUI

		addComponentListener(this);
		VergeEngine.gui = this;
		
		canvas.setBackground(Color.black);
		control.clearKeymap();
		canvas.addMouseListener(control);
		canvas.addMouseMotionListener(control);
		canvas.addFocusListener(control);
		canvas.addKeyListener(control);

		this.add(canvas);

		setDimensions(this, w, h);
		
		this.addWindowListener(control);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		cycleTime = System.currentTimeMillis();
			
		System.out.println("GUI Initialized.");
		gameThread = new VergeEngine();
		gameThread.setPriority(Thread.MIN_PRIORITY);
		gameThread.start(); // start Game processing.
	}
	
	void setDimensions(GUI gui, int w, int h) {
		if (w==0) {
			Dimension scrsize = Toolkit.getDefaultToolkit().getScreenSize();
			winwidth = scrsize.width;
			winheight = scrsize.height;
			win_decoration=false;
		} else {
			winwidth = w;
			winheight = h;
			win_decoration=true;
		}
		
		/* create window to `emulate' an applet's frame */
		//super.setLayout(new FlowLayout(FlowLayout.CENTER,0,0));
		setVisible(false);
		dispose();
		
		if (!win_decoration) {
			this.setUndecorated(true);
			this.setSize(winwidth, winheight);
			GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setFullScreenWindow(gui);			
			
		} else {
			// setting the size of the canvas or applet has no effect
			// we need to add the height of the title bar to the height
			// We use the insets now. Originally, we used:
			// 24 is the empirically determined height in WinXP
			// 48 enables us to have the whole window with title bar on-screen
			// 8 is the empirically determined width in win and linux
			
			//[Rafael, the Esper] Does not work
			//Insets insets = super.getInsets();
			//super.setSize(winwidth+insets.left+insets.right, winheight+insets.top+insets.bottom);
			//super.setSize(winwidth, winheight);
			GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setFullScreenWindow(null);
			this.setUndecorated(false);
			this.setVisible(true);
			System.out.println("Winwidth: " + winwidth + ", Winheight: " + winheight + " I: " + super.getInsets());
			
			this.setSize(winwidth+super.getInsets().left+super.getInsets().right,
					winheight+super.getInsets().top+super.getInsets().bottom);
			System.out.println(super.getBounds());
		}

		this.setVisible(true);
		
		canvas.requestFocus();
		
		/*try {
			this.createBufferStrategy(2, new BufferCapabilities(new ImageCapabilities(true), new ImageCapabilities(true), FlipContents.UNDEFINED));
			} catch (AWTException e){
			// flip is unavailable, create the best you have
				this.createBufferStrategy(2);
			}*/		

		canvas.createBufferStrategy(2);
		strategy = getGUI().canvas.getBufferStrategy();
		
		
	}

	public static void paintFrame() {
		updateGUI();
		updateFPS();
		synchFramerate();
	}
	
	 
	public static void updateGUI() {
		Graphics g = strategy.getDrawGraphics();
		if(alpha != 1f) {
			Graphics2D g2d = (Graphics2D) g;
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_IN, alpha));
			g2d.drawImage(screen.getImage(), 0, 0, curwidth, curheight, null);
		}
		else {
			/* Do this to rotate 180 
			Graphics2D g2d = (Graphics2D) g;
			g2d.rotate(Math.PI, curwidth/2, curheight/2);
			g2d.drawImage(screen.getImage(), 0, 0, curwidth, curheight, null);*/
			g.drawImage(screen.getImage(), 0, 0, curwidth, curheight, null);			
		}

		// Show FPS
		g.setFont(fps_font);
		g.setColor(Color.WHITE);
		g.drawString("FPS: " + Float.toString(frameInLastSecond), 10, 20);
		
		g.dispose();
		strategy.show();
	}
	
	public static void synchFramerate() {
		cycleTime = cycleTime + frameDelay;
		long difference = cycleTime - System.currentTimeMillis();
		try {
			Thread.sleep(Math.max(0, difference));
		}
		catch(InterruptedException e) {
			e.printStackTrace();
		}
	}	
	

	void closeWindow() {
		super.setVisible(false);
		System.exit(0);
	}
	
	public Canvas getCanvas() {
		return this.canvas;
	}

	public void componentResized(ComponentEvent e)
	{
		Dimension scrsize = Toolkit.getDefaultToolkit().getScreenSize();
		winwidth = scrsize.width;
		winheight = scrsize.height;		
		//System.out.println(getWidth());	
		updateCanvasSize();
		//VergeEngine.scaledBI = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);//this.createVolatileImage(this.getWidth(), this.getHeight());
		//VergeEngine.g = VergeEngine.scaledBI.createGraphics();
	}
	
	public void updateCanvasSize() {
		this.curwidth = this.getWidth()-super.getInsets().left-super.getInsets().right;
		this.curheight = this.getHeight()-super.getInsets().top-super.getInsets().bottom;
		//canvas.updateSize(
			//	this.getWidth()-super.getInsets().left-super.getInsets().right,
				//this.getHeight()-super.getInsets().top-super.getInsets().bottom);
	}

	
	@Override
	public void componentHidden(ComponentEvent arg0) {	}

	@Override
	public void componentMoved(ComponentEvent arg0) {	}

	@Override
	public void componentShown(ComponentEvent arg0) {	}

	public void setAlpha(float f) {
		this.alpha = f;		
	}
	
	public static void incFrameDelay(int i) {
		frameDelay = frameDelay + i;
		if(frameDelay < 5)
			frameDelay = 5;
		else if(frameDelay > 100)
			frameDelay = 100;
	}

	
	protected final static Font fps_font = new Font("Monospaced", Font.PLAIN, 12);
	static long nextSecond = System.currentTimeMillis() + 1000;
	static int frameInLastSecond = 0;
	static int framesInCurrentSecond = 0;
	static void updateFPS() {
		long currentTime = System.currentTimeMillis();
	    if (currentTime > nextSecond) {
	        nextSecond += 1000;
	        frameInLastSecond = framesInCurrentSecond;
	        framesInCurrentSecond = 0;
	    }
	    framesInCurrentSecond++;	
	}
}
