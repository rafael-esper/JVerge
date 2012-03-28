package core;

import java.awt.AWTException;
import java.awt.AlphaComposite;
import java.awt.BufferCapabilities;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
	private static final int FRAME_DELAY = 20; // 20ms. implies 50fps (1000/20) = 50

	
	public GUI(int w, int h) {
		// build and display your GUI

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
		
		addComponentListener(this);
		VergeEngine.gui = this;
		
		canvas.setBackground(Color.black);
		control.clearKeymap();
		canvas.addMouseListener(control);
		canvas.addMouseMotionListener(control);
		canvas.addFocusListener(control);
		canvas.addKeyListener(control);

		this.add(canvas);
		
		/* create window to `emulate' an applet's frame */
		//super.setLayout(new FlowLayout(FlowLayout.CENTER,0,0));
		if (!win_decoration) {
			this.setUndecorated(true);
			this.setSize(winwidth, winheight);
		} else {
			// setting the size of the canvas or applet has no effect
			// we need to add the height of the title bar to the height
			// We use the insets now. Originally, we used:
			// 24 is the empirically determined height in WinXP
			// 48 enables us to have the whole window with title bar on-screen
			// 8 is the empirically determined width in win and linux
			
			//RBP Does not work
			//Insets insets = super.getInsets();
			//super.setSize(winwidth+insets.left+insets.right, winheight+insets.top+insets.bottom);
			//super.setSize(winwidth, winheight);
			this.setVisible(true);
			System.out.println(super.getInsets());

			this.setSize(winwidth+super.getInsets().left+super.getInsets().right,
					winheight+super.getInsets().top+super.getInsets().bottom);
			System.out.println(super.getBounds());
		}
		this.addWindowListener(control);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		canvas.requestFocus();
		
		/*try {
			this.createBufferStrategy(2, new BufferCapabilities(new ImageCapabilities(true), new ImageCapabilities(true), FlipContents.UNDEFINED));
			} catch (AWTException e){
			// flip is unavailable, create the best you have
				this.createBufferStrategy(2);
			}*/		

		canvas.createBufferStrategy(2);
		strategy = getGUI().canvas.getBufferStrategy();			
		cycleTime = System.currentTimeMillis();
			
		System.out.println("GUI Initialized.");
		gameThread = new VergeEngine();
		gameThread.setPriority(Thread.MIN_PRIORITY);
		gameThread.start(); // start Game processing.
	}
	
	public static void paintFrame() {
		updateGUI();
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
			g.drawImage(screen.getImage(), 0, 0, curwidth, curheight, null);
		}
		g.dispose();
		strategy.show();
	}
	
	public static void synchFramerate() {
		cycleTime = cycleTime + FRAME_DELAY;
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
	public void componentHidden(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentMoved(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentShown(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void setAlpha(float f) {
		this.alpha = f;		
	}

}
