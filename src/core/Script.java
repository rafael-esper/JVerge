package core;

import static core.VergeEngine.*;
import static core.Controls.*;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.List;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorConvertOp;
import java.awt.image.RescaleOp;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import audio.VMusic;

import domain.VSound;
import domain.VImage;
import domain.Entity;
import domain.Map;

import static core.Script.screen;
import static core.Script.setlucent;
import static core.Script.showpage;
import static core.Script.timer;
import static core.SinCos.*;

public class Script {

	//public static final int VCFILES		=		51;
	public static final int  VC_READ	=			1;
	public static final int  VC_WRITE		=	2;
	public static final int  VC_WRITE_APPEND	=	3; // Overkill (2006-07-05): Append mode added.	
	

	public static final int CF_GRAY = 1;
	public static final int CF_INV_GRAY = 2;
	public static final int CF_INV = 3;
	public static final int CF_RED = 4;
	public static final int CF_GREEN = 5;
	public static final int CF_BLUE = 6;
	public static final int CF_CUSTOM = 7;	
	
	// VERGE ENGINE VARIABLES: Moved to Script for easy of use
	/**
	 * This is a hardcoded image handle for the screen. It is a pointer to a
	 * bitmap of the screen's current dimensions (set in v3.cfg or by
	 * SetResolution() function at runtime). Anything you want to appear in the
	 * verge window should be blitted here with one of the graphics functions.
	 * When ShowPage() is called the screen bitmap is transfered to the display.
	 */
	public static VImage screen;
	
	// For partial screen rendering
	static VImage virtualScreen = null;

	/** read-only timer variable constantly increasing*/
	public static int systemtime;

	/** read/write timer variable*/
	public static int timer;

	// internal use only
	static int vctimer = 0; // [Rafael, the Esper]
	static int hooktimer = 0;

	public static List<Entity> entity = new ArrayList<Entity>();
	public static Entity myself = null;

	/**
	 * The number of entities currently on the map. Use this an the upper bound
	 * any time you need to loop through and check entites for something.
	 */
	public static int numentities;

	public static int player;
	public static int playerstep = 1;
	public static boolean playerdiagonals = true;
	public static boolean smoothdiagonals = true; // [Rafael, the Esper]

	public static int xwin, ywin;
	public static Map current_map = null;

	public static int cameratracking = 1;
	public static int cameratracker = 0;
	public static int lastplayerdir = 0;

	public static boolean entitiespaused = false;
	
	// END OF VERGE ENGINE VARIABLES
	
	public static String renderfunc, timerfunc; // was VergeCallback (struct)

	public static DefaultPalette palette = new DefaultPalette();
	public static int transcolor = -65281; // Color(255, 0, 255);
	public static int currentLucent = 255;
	
	public static int event_tx;
	public static int event_ty;
	public static int event_zone;
	public static int event_entity;
	public static int event_param;
	public static int event_sprite;
	public static int event_entity_hit;

	public static int __grue_actor_index;
	
	private static VMusic musicplayer; // [Rafael, the Esper] 
	
	public static int invc;

	public static String _trigger_onStep = "", _trigger_afterStep = "";
	public static String _trigger_beforeEntityScript = "", _trigger_afterEntityScript = "";
	public static String _trigger_onEntityCollide = "";
	public static String _trigger_afterPlayerMove = "";

	public static int vc_GetYear()
	{
		  Calendar cal=Calendar.getInstance();
		  return cal.get(Calendar.YEAR);
	}

	public static int vc_GetMonth()
	{
		  Calendar cal=Calendar.getInstance();
		  return cal.get(Calendar.MONTH);

	}

	public static int vc_GetDay()
	{
		  Calendar cal=Calendar.getInstance();
		  return cal.get(Calendar.DAY_OF_MONTH);
	}

	public static int vc_GetDayOfWeek()
	{
		  Calendar cal=Calendar.getInstance();
		  return cal.get(Calendar.DAY_OF_WEEK);
	}

	public static int vc_GetHour()
	{
		  Calendar cal=Calendar.getInstance();
		  return cal.get(Calendar.HOUR);
	}

	public static int vc_GetMinute()
	{
		  Calendar cal=Calendar.getInstance();
		  return cal.get(Calendar.MINUTE);
	}

	public static int vc_GetSecond()
	{
		  Calendar cal=Calendar.getInstance();
		  return cal.get(Calendar.SECOND);
	}


	public static void error(String str) { 
	  	System.err.println(str);
	}

	static void hooktimer()
	{
		// To prevent hooktimer from happening before the script engine is loaded.
		//if(se == null) return;

		while (hooktimer != 0)
		{
			callfunction(timerfunc);
			hooktimer--;
		}
	}

	public static void hooktimer(String cb) {
		hooktimer = 0;
		timerfunc = cb;
	}
	
	public static void hookretrace()
	{
		if(renderfunc != null) {
			callfunction(renderfunc);
		}
	}

	public static void hookretrace(String cb) {
		renderfunc = cb;
	}

	
	// Rafael: Changed to ExecuteFunctionString 
	/*public void ExecuteCallback(String function, boolean callingFromLibrary) */
	
	public static void exit(String message) { 
		System.err.println(message);
		System.exit(-1); 
	}

	/* Rafael: TODO Implement this.
	 public static void SetButtonJB(int b, int jb) {
		switch (b)
		{
			case 1: j_b1 = jb; break;
			case 2: j_b2 = jb; break;
			case 3: j_b3 = jb; break;
			case 4: j_b4 = jb; break;
		}
	}*/

	// Overkill (2007-08-25): HookButton is supposed to start at 1, not 0.
	// It's meant to be consistent with Unpress().
	public static void hookbutton(int b, String s) {
		if (b<1 || b>4) return;
		bindbutton[b-1] = s;
	}

	public static void hookkey(int k, String s) {
		if (k<0 || k>127) return;
		bindarray[k] = s;
	}

	public static void log(String s) { 
		System.out.println(s); 
	}

	/*
	static void MessageBox(String msg) { showMessageBox(msg); }*/

	public static int random(int min, int max) { 
		Random r = new Random();
		return r.nextInt(max+1-min) + min; 
	}
	
	
	public static void setappname(String s) { 
		getGUI().setTitle(s);
	}

	public static void unpress(int n) {
		switch (n)
		{
			case 0: 
				if (b1) UnB1(); 
				if (b2) UnB2(); 
				if (b3) UnB3(); 
				if (b4) UnB4(); 	
				break;
			case 1: if (b1) UnB1(); break;
			case 2: if (b2) UnB2(); break;
			case 3: if (b3) UnB3(); break;
			case 4: if (b4) UnB4(); break;
			case 5: if (up) UnUp(); break;
			case 6: if (down) UnDown(); break;
			case 7: if (left) UnLeft(); break;
			case 8: if (right) UnRight(); break;
			case 9: 
				if (b1) UnB1(); 
				if (b2) UnB2(); 
				if (b3) UnB3(); 
				if (b4) UnB4();
				if (up) UnUp();
				if (down) UnDown();
				if (left) UnLeft();
				if (right) UnRight();
				break;
		}
	}

	public static void updatecontrols() { 
		Controls.UpdateControls(); 
	}

	public static int asc(String s) { 
		if(s.length() == 0) 
			return 0; 
		else 
			return (int)s.charAt(0); 
	}
	
	public static String chr(int c) { 
		return Character.toString((char) c);
	}
	
	public static String gettoken(String s, String d, int i) { // Reimplemented by [Rafael, the Esper]
		String[] retorno = s.split(d);
		if(retorno.length <= i)
			return "";
		
		return retorno[i];
	}
	public static String left(String str, int len) { 
		return str.substring(0, str.length()>len?len:str.length());
	}
	
	public static int len(String s) { 
		return s.length(); 
	}
	
	public static String mid(String str, int pos, int len) { 
		return str.substring(pos, pos+len);
	}
	
	public static String right(String str, int len) { 
		return len > str.length() ? str : str.substring(str.length() - len);
	}
	
	public static String str(int d) { 
		return Integer.toString(d);
	}
	
	public static boolean strcmp(String s1, String s2) { 
		return s1.equals(s2); // ? 1 : 0;
	}
	
	public static String capitalize(String s) { // [Rafael, the Esper]
		if (s.length() == 0) return s;
		return s.substring(0, 1).toUpperCase() + s.substring(1);		
	}
	
	public static String strdup(String s, int times) {
		String ret = "";
		for (int i=0; i<times; i++)
			ret = ret.concat(s);
		return ret;
	}
	
	public static int tokencount(String s, String d) {
		String[] retorno = s.split(d);
		return retorno.length;
	}
	
	public static String trim(String s) { 
		return s.trim(); 
	}
	
	public static String tolower(String str) { 
		return str.toLowerCase();
	}

	public static String toupper(String str) {
		return str.toUpperCase();
	}

	public static int val(String s) { 
		if(s==null || s.isEmpty() || s.trim().equals("-"))
			return 0;
		
		return Integer.valueOf(s.replace('+', ' ').trim());
	}

	//VI.d. Map Functions
	public static void map(String map) {
		mapname = map;
		die = true;
		done = true;

		/* Hookretrace carries over between maps!
		/ According to http://verge-rpg.com/docs/the-verge-3-manual/general-utility-functions/hookretrace/
		  hookretrace(""); */ 
	}
	
	//VI.e. Entity Functions
	public static void changeCHR(int e, String c) {
		if (e<0 || e >= numentities) return;
		else entity.get(e).set_chr(c);
	}
	public static void entitymove(int e, String s) {
		if (e<0 || e >= numentities) return;
		else entity.get(e).SetMoveScript(s);
	}
	public static void entitysetwanderdelay(int e, int d) {
		if (e<0 || e >= numentities) return;
		else entity.get(e).SetWanderDelay(d);
	}
	public static void entitysetwanderrect(int e, int x1, int y1, int x2, int y2) {
		if (e<0 || e >= numentities) return;
		else entity.get(e).SetWanderBox(x1, y1, x2, y2);
	}
	public static void entitysetwanderzone(int e) {
		if (e<0 || e >= numentities) return;
		else entity.get(e).SetWanderZone();
	}
	public static int entityspawn(int x, int y, String s) { 
		return AllocateEntity(x*16,y*16,s); 
	}
	public static void entitystalk(int stalker, int stalkee) {
		if (stalker<0 || stalker>=numentities)
			return;
		if (stalkee<0 || stalkee>=numentities)
		{
			entity.get(stalker).clear_stalk();
			return;
		}
		entity.get(stalker).setx(entity.get(stalkee).getx()); // [Rafael, the Esper]
		entity.get(stalker).sety(entity.get(stalkee).gety()); // [Rafael, the Esper]
		entity.get(stalker).stalk(entity.get(stalkee));
	}
	public static void entitystop(int e) {
		if (e<0 || e >= numentities) return;
		else entity.get(e).SetMotionless();
	}
	public static void hookentityrender(int i, String s) {
		if (i<0 || i>=numentities) 
			System.err.printf("vc_HookEntityRender() - no such entity %d", i);
		entity.get(i).hookrender = s;
	}
	
	public static void playermove(String s) {
		if (myself==null) 
			return;
		myself.SetMoveScript( s );

		while(myself.movecode != 0 )
		{
			screen.render();
			showpage();
		}

		playerentitymovecleanup();
	}

	public static void playerentitymovecleanup() {
		if (myself==null) return;

		myself.movecode = 0;
		//[Rafael, the Esper] implementar afterPlayerMove();
	}

	public static void pauseplayerinput() { // [Rafael, the Esper]
		invc = 1;
	}
	public static void unpauseplayerinput() { // [Rafael, the Esper]
		invc = 0;
	}
	
	public static void setentitiespaused(boolean b) {
		entitiespaused = b;
		if (!entitiespaused)
			lastentitythink = systemtime;
	}
	
	public static void setplayer(int e) {
		if (e<0 || e>=numentities)
		{
			player = -1;
			myself = null;
			System.err.println("invalid Player.");
			return;
		}
		myself = entity.get(e);
		player = e;
		myself.SetMotionless();
		myself.obstructable = true;
	}

	public static int getplayer()
	{
		return player;
	}
	
/*
	//VI.g. Sprite Functions
	static int GetSprite() { return GetSprite(); }
	static void ResetSprites() { return ResetSprites(); }

	//VI.h. Sound/Music Functions
	static void FreeSong(int handle) { FreeSong(handle); }
	static void FreeSound(int slot) { FreeSample((void*)slot); }
	static int GetSongPos(int handle) { return GetSongPos(handle); }
	static int GetSongVolume(int handle) { return GetSongVol(handle); }
	static int LoadSong(String fn) { return LoadSong(fn); }
	static int LoadSound(String fn) { return (int)LoadSample(fn); }*/
	public static void playsound(VSound sound) {
		playsound(sound, 100);
	}
	public static void playsound(VSound sound, int volume) {
		if(sound==null || VergeEngine.config.isNosound())
			return;

		if (volume < 0)
			volume = 0;
		else if (volume > 100)
			volume = 100;
		sound.start(volume);
	}

	public static void playmusic(URL fn) { 
		if(fn==null || VergeEngine.config==null || VergeEngine.config.isNosound())
			return;
		
		if(musicplayer!=null) {
			musicplayer.stop();
		}
		try {
			musicplayer = new VMusic();
			log("Playing..." + fn);
			musicplayer.start(fn);
		}
		catch(Exception e) {
			System.err.println("Error when playing " + fn);
		}
	}
	
	/*
	static void PlaySong(int handle) { PlaySong(handle); }*/
	/*public static int playsound(String name, int volume) { 
		return 0;
		//return PlaySample((void*) slot, volume * 255 / 100); 
	}*/
	
	public static void setmusicvolume(int v) { 
		// TODO Implement
	}

	/*static void StopSong(int handle) { StopSong(handle); }
	static void StopSound(int chan) { StopSound(chan); }
*/
	
	/*static void SetSongPaused(int h, int p) { SetPaused(h,p); }
	static void SetSongPos(int h, int p) { SetSongPos(h,p); }
	static void SetSongVolume(int h, int v) { SetSongVol(h,v); } */
	public static void stopmusic() { 
		if(musicplayer!=null) {
			musicplayer.stop();
		}
	}

	// Graphics
	
	public static void setcustomcolorfilter(Color c1, Color c2) {
		/*GetColor(c1, cf_r1, cf_g1, cf_b1);
		GetColor(c2, cf_r2, cf_g2, cf_b2);
		cf_rr = cf_r2 - cf_r1;
		cf_gr = cf_g2 - cf_g1;
		cf_br = cf_b2 - cf_b1;*/
		// TODO [Rafael, the Esper] Implement this
		graycolorfilter(screen.getImage());
		error("Non implemented function: setcustomcolorfilter");
	}
	
	public static void setlucent(int p) { 
		if(p < 0 || p > 100)
			return;
		currentLucent = (100-p) * 255 / 100;
		if(getGUI()!=null)
			getGUI().setAlpha ((float)(100-p) / 100);
	}

	static int lastchangetime = 0;
	
	public static void showpage() {

		if(virtualScreen!=null) {
			screen.blit(0, 0, virtualScreen);
		}
		//flipblit(0,0,FlipType.FLIP_HORIZONTALLY,screen,screen);
		//System.out.println("showpage");
		Controls.UpdateControls();
		// Check if the player pressed a special key
		//VergeEngine.checkFunctionKeys();
		
		//VEngine.updateGUI();
		DefaultTimer();//[Rafael, the Esper]
		GUI.paintFrame();
		//VEngine.synchFramerate();
		//VergeEngine.PaintToScreen();
		//getGUI().getCanvas().setCanvas_screen(screen.getImage()); //[Rafael, the Esper]
	}
	

	public static void lightfilter(int scalefactor, VImage vimage) {
		RescaleOp op = new RescaleOp((float)scalefactor/100, 0, null);
		op.filter(vimage.image, vimage.image);
	}
	
	private static BufferedImageOp op = null;
	
	public static void graycolorfilter(BufferedImage img) {
		if(op==null)
			op = new ColorConvertOp (ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
		img = op.filter(img, img);
		return;
	}
	
	
	public static void colorfilter(int filter, VImage img) { 
		if(filter>6) return;
		if(filter==1) {
			if(op==null)
				op = new ColorConvertOp (ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
			op.filter(img.getImage(), img.getImage());
			return;
		}
		
		int rr, gg, bb, z; Color c = null;

		int x1,x2,y1,y2;
		// [Rafael, the Esper] img.GetClip(x1,y1,x2,y2);
		x1 = y1 = 0;
		x2 = img.width;
		y2 = img.height;

		//PT ptr = (PT)img.data;
		//PT data = (PT)&ptr[(y1 * img.pitch) + x1];

		for (int y=y1; y<y2; y++)
		{
			//int* data_end = data+x2+1;
			for(int x=x1;x<x2;x++) {
				int rgb = img.getImage().getRGB(x, y);
				//Color col = new Color(img.getImage().getRGB(x, y));
				if (rgb == transcolor) continue; // Overkill (2006-07-27): Ignore trans pixels
				rr = (rgb >> 16) & 0x000000FF;				
				gg = (rgb >>8 ) & 0x000000FF;
				bb = (rgb) & 0x000000FF;
				//GetColor(col, rr, gg, bb);
				//if(filter==2) System.out.printf("%d %d %d %d\n", rr, gg, bb, 255-((rr+gg+bb)/3));
				switch (filter)
				{
					case 0: 
					case 1: z = (rr+gg+bb)/3; c = new Color(z,z,z); break; // GRAY
					case 2: z = 255-((rr+gg+bb)/3); c = new Color(z,z,z); break;
					case 3: c = new Color(255-rr, 255-gg, 255-bb); break;
					case 4: z = (rr+gg+bb)/3; c = new Color(z, 0, 0); break; // RED
					case 5: z = (rr+gg+bb)/3; c = new Color(0, z, 0); break; // GREEN
					case 6: z = (rr+gg+bb)/3; c = new Color(0, 0, z); break; // BLUE
					// [Rafael, the Esper] Custom color filter case 7: z = (rr+gg+bb)/3; c = new Color(cf_r1+((cf_rr*z)>>8), cf_g1+((cf_gr*z)>>8), cf_b1+((cf_br*z)>>8)).getRGB(); break;
				}
				img.setpixel(x, y, c);
			}
		}
	}	
	
	public static Color RGB(int r, int g, int b) {
		return new Color(r, g, b);
	}

	public static Color mixcolor(Color c1, Color c2, int p) {
		if (p>255) p=255;
		if (p<0) p=0;

		int r1 = c1.getRed();
		int g1 = c1.getGreen();
		int b1 = c1.getBlue();
		int r2 = c2.getRed();
		int g2 = c2.getGreen();
		int b2 = c2.getBlue();

		return new Color((r1*(255-p)/255)+(r2*p/255), (g1*(255-p)/255)+(g2*p/255), (b1*(255-p)/255)+(b2*p/255));
	}	


	public static int getB(int c) {
		return palette.getColor(c, currentLucent).getBlue();
	}
	public static int getG(int c) {
		return palette.getColor(c, currentLucent).getGreen();
	}
	public static int getR(int c) {
		return palette.getColor(c, currentLucent).getRed(); 
	}	
	
	/*

	static int HSV(int h, int s, int v) { return HSVtoColor(h,s,v); }
	static int GetH(int col) {
		int h, s, v;
		GetHSV(col, h, s, v);
		return h;
	}
	static int GetS(int col) {
		int h, s, v;
		GetHSV(col, h, s, v);
		return s;
	}
	static int GetV(int col) {
		int h, s, v;
		GetHSV(col, h, s, v);
		return v;
	}
	static void HueReplace(int hue_find, int hue_tolerance, int hue_replace, int image) {
		HueReplace(hue_find, hue_tolerance, hue_replace, ImageForHandle(image));
	}
	static void ColorReplace(int find, int replace, int image)
	{
		ColorReplace(find, replace, ImageForHandle(image));
	}
*/
	

	//VI.j. Math Functions
	//helper:
	public static int abs(int i) {
		return Math.abs(i);
	}
	public static int sgn(int i) {
		return (int) Math.signum(i);
	}
	public static int mydtoi(double d) { return (int)Math.floor(d + 0.5); }
	static int acos(int val) {
		double dv = (double) val / 65535;
		double ac = Math.acos(dv);
		ac = ac * 180 / 3.14159265358979; // convert radians to degrees
		return mydtoi(ac);
	}
	public static int facos(int val) {
		double dv = (double) val / 65535;
		double ac = Math.acos(dv);
		ac *= 65536; // Convert to 16.16 fixed point
		return mydtoi(ac);
	}
	public static int asin(int val) {
		double dv = (double) val / 65535;
		double as = Math.asin(dv);
		as = as * 180 / 3.14159265358979; // convert radians to degrees
		return mydtoi(as);
	}
	public static int fasin(int val) {
		double dv = (double) val / 65535;
		double as = Math.asin(dv);
		as *= 65536; // Convert to 16.16 fixed point
		return mydtoi(as);
	}
	public static int atan(int val) {
		double dv = (double) val / 65535;
		double at = Math.atan(dv);
		at = at * 180 / 3.14159265358979; // convert radians to degrees
		return mydtoi(at);
	}
	public static int fatan(int val) {
		double dv = (double) val / 65535;
		double at = Math.atan(dv);
		at *= 65536; // Convert to 16.16 fixed point
		return mydtoi(at);
	}
	public static int atan2(int y, int x) {
		float f = (float) Math.atan2((float)y,(float)x);
		return (int)(f/2.0/3.14159265358979*360.0);
	}
	public static int fatan2(int y, int x) {
		double theta = Math.atan2((double) y, (double) x);
		return mydtoi(theta * 65536);
	}
	public static int sin(int n) {
	    while (n < 0) n += 360;
	    while (n >= 360) n -= 360;
		return sintbl[n];
	}
	public static int cos(int n) {
	    while (n < 0) n += 360;
	    while (n >= 360) n -= 360;
		return costbl[n];
	}
	public static int tan(int n) {
	    while (n < 0) n += 360;
	    while (n >= 360) n -= 360;
		return tantbl[n];
	}
	public static int fsin(int val) {
		double magnitude = Math.sin((double) val / 65536);
		return mydtoi(magnitude * 65536);
	}
	public static int fcos(int val) {
		double magnitude = Math.cos((double) val / 65536);
		return mydtoi(magnitude * 65536);
	}
	public static int ftan(int val) {
		double magnitude = Math.tan((double) val / 65536);
		return mydtoi(magnitude * 65536);
	}
	public static int pow(int a, int b) {
		return (int) Math.pow((double)a, (double)b);
	}
	public static int sqrt(int val) {
		return (int) (float) Math.sqrt((float) val);
	}

	// Util Functions 

	private static boolean isLetterDigitOrSignal(char c) {
		if(Character.isLetterOrDigit(c) || c=='+' || c=='-')
			return true;
		return false;
	}
	
	// Split String in trimmed words
	public static List<String> splitTextIntoWords(String text) { 
		int initial = 0;
		List<String> words = new ArrayList<String>();
		if(text==null) 
			return words;
		
		for(int i=0; i<text.length(); i++) {
			while(i<text.length() && (isLetterDigitOrSignal(text.charAt(i)) || text.charAt(i) == '\'')) {
				i++;
			}
			while(i<text.length() && !isLetterDigitOrSignal(text.charAt(i))) {
				i++;
			}
			words.add(text.substring(initial, i).trim());
			initial = i;
		}
		return words;
	}	

	// Split list of words into rows 
	public static List<String> splitTextIntoRows(String text, int maxperrow) {
		
		List<String> words = splitTextIntoWords(text);
		List<String> rows = new ArrayList<String>();
		int i = 0;
		String str;
		while (i < words.size()) {
			str = words.get(i);
		    while (i < words.size()-1 && str.length()+ 1 + words.get(i+1).length() <= maxperrow) {
		       str = str.concat(" " + words.get(i+1));
		       i += 1;
			}
		    rows.add(str);
		    str = "";i+=1;
		}
		return rows;
	}	
	
/*	
	// Overkill (2006-07-20):
	// Saves a CHR file, using an open file handle, saving the specified entity.
	static void FileWriteCHR(int handle, int ent) {
		if (!handle || handle > VCFILES || !vcfiles[handle].active)
			se.Error("FileWriteCHR() - file handle is either invalid or file is not open.");
		if (vcfiles[handle].mode != VC_WRITE)
			se.Error("FileWriteCHR() - given file handle is a read-mode file.");
		if (ent < 0 || ent >= entities)
			se.Error("Tried saving an invalid or inactive ent index (%d).", ent);

		entity[ent].chr.save(vcfiles[handle].fptr);	
	}

	// Overkill (2006-07-20):
	// Saves a MAP file, using an open file handle, saving the current map.
	static void FileWriteMAP(int handle) {
		if (!handle || handle > VCFILES || !vcfiles[handle].active)
			se.Error("FileWriteMAP() - file handle is either invalid or file is not open.");
		if (vcfiles[handle].mode != VC_WRITE)
			se.Error("FileWriteMAP() - given file handle is a read-mode file.");
		if (!current_map)
			se.Error("FileWriteMAP() - There is no active map, therefore making it not possible to save this map.");

		current_map.save(vcfiles[handle].fptr);	
	}
	// Overkill (2006-07-20):
	// Saves a VSP file, using an open file handle, saving the current map's VSP.
	static void FileWriteVSP(int handle) {
		if (!handle || handle > VCFILES || !vcfiles[handle].active)
			se.Error("FileWriteVSP() - file handle is either invalid or file is not open.");
		if (vcfiles[handle].mode != VC_WRITE)
			se.Error("FileWriteVSP() - given file handle is a read-mode file.");
		if (!current_map)
			se.Error("FileWriteVSP() - There is no active map, therefore making it not possible to save the map's vsp.");

		current_map.tileset.save(vcfiles[handle].fptr);	
	}

	//VI.l. Window Managment Functions
	//helper"
	static void checkhandle(char *func, int handle, AuxWindow *auxwin) {
		if(!handle)
			se.Error("%s() - cannot access a null window handle!",func);
		if(!auxwin)
			se.Error("%s() - invalid window handle!",func);
	}
	static void WindowClose(int win) {
		if(win == 1) se.Error("WindowClose() - cannot close gameWindow");
		AuxWindow *auxwin = vid_findAuxWindow(win);
		checkhandle("WindowClose",win,auxwin);
		auxwin.dispose();
	}
	static int WindowCreate(int x, int y, int w, int h, String s) {
		AuxWindow *auxwin = vid_createAuxWindow();
		auxwin.setTitle(s);
		auxwin.setPosition(x,y);
		auxwin.setResolution(w,h);
		auxwin.setSize(w,h);
		auxwin.setVisibility(true);
		return auxwin.getHandle();
	}
	static int WindowGetHeight(int win) {
		AuxWindow *auxwin = vid_findAuxWindow(win);
		checkhandle("WindowGetHeight",win,auxwin);
		return auxwin.getHeight();
	}
	static int WindowGetImage(int win) {
		AuxWindow *auxwin = vid_findAuxWindow(win);
		checkhandle("WindowGetImage",win,auxwin);
		return auxwin.getImageHandle();
	}
	static int WindowGetWidth(int win) {
		AuxWindow *auxwin = vid_findAuxWindow(win);
		checkhandle("WindowGetWidth",win,auxwin);
		return auxwin.getWidth();
	}
	static int WindowGetXRes(int win) {
		AuxWindow *auxwin = vid_findAuxWindow(win);
		checkhandle("WindowGetXRes",win,auxwin);
		return auxwin.getXres();
	}
	static int WindowGetYRes(int win) {
		AuxWindow *auxwin = vid_findAuxWindow(win);
		checkhandle("WindowGetYRes",win,auxwin);
		return auxwin.getYres();
	}
	static void WindowHide(int win) {
		AuxWindow *auxwin = vid_findAuxWindow(win);
		checkhandle("WindowHide",win,auxwin);
		auxwin.setVisibility(false);
	}
	static void WindowPositionCommand(int win, int command, int arg1, int arg2) {
		AuxWindow *auxwin = vid_findAuxWindow(win);
		checkhandle("WindowPositionCommand",win,auxwin);
		auxwin.positionCommand(command,arg1,arg2);
	}
	static void WindowSetPosition(int win, int x, int y) {
		AuxWindow *auxwin = vid_findAuxWindow(win);
		checkhandle("WindowSetPosition",win,auxwin);
		auxwin.setPosition(x,y);
	}
	static void WindowSetResolution(int win, int w, int h) {
		AuxWindow *auxwin = vid_findAuxWindow(win);
		checkhandle("WindowSetResolution",win,auxwin);
		auxwin.setResolution(w,h);
		auxwin.setSize(w,h);
	}
	static void WindowSetSize(int win, int w, int h) {
		AuxWindow *auxwin = vid_findAuxWindow(win);
		checkhandle("WindowSetSize",win,auxwin);
		auxwin.setSize(w,h);
	}
	static void WindowSetTitle(int win, String s) {
		AuxWindow *auxwin = vid_findAuxWindow(win);
		checkhandle("WindowSetTitle",win,auxwin);
		auxwin.setTitle(s);
	}
	static void WindowShow(int win) {
		AuxWindow *auxwin = vid_findAuxWindow(win);
		checkhandle("WindowShow",win,auxwin);
		auxwin.setVisibility(true);
	}
	//VI.m. Movie Playback Functions
	static void AbortMovie() { win_movie_abortSimple(); }
	static void MovieClose(int m) { win_movie_close(m); }
	static int MovieGetCurrFrame(int m) { return win_movie_getCurrFrame(m); }
	static int MovieGetFramerate(int m) { return win_movie_getFramerate(m); }
	static int MovieGetImage(int m) { return win_movie_getImage(m); }
	static int MovieLoad(String s, bool mute) { return win_movie_load(s, mute); }
	static void MovieNextFrame(int m) { win_movie_nextFrame(m); }
	static void MoviePlay(int m, bool loop) { win_movie_play(m, loop?1:0); }
	static void MovieRender(int m) { win_movie_render(m); }
	static void MovieSetFrame(int m, int f) { win_movie_setFrame(m,f); }
	static int PlayMovie(String s){ return win_movie_playSimple(s); }
*/
	//VI.n. Netcode Functions
	static ServerSocket vcserver = null;

	// Overkill (2008-04-17): Socket port can be switched to something besides 45150.
	static int vcsockport = 45150;
	static void SetConnectionPort(int port)
	{
		vcsockport = port;
	}

	// Overkill (2008-04-17): Socket port can be switched to something besides 45150.
	static Socket Connect(String ip) {
		Socket s;
		try
		{
			s = new Socket(ip, vcsockport);
		}
		catch (Exception ne) {
			return null;
		}
		return s;
	}

	// Overkill (2008-04-17): Socket port can be switched to something besides 45150.
	// Caveat: The server currently may not switch listen ports once instantiated.
	static Socket GetConnection() {
		try {
			if (vcserver != null)
				vcserver = new ServerSocket(vcsockport);
			Socket s = vcserver.accept();
			return s;
		}
		catch(Exception e) {
			return null;
	    }
	}

	public static VImage geturlimage(String url) { 
		// TODO Implement this mechanism!
		error("Non implemented function: geturlimage");
		return new VImage(10, 10);
		//return getUrlImage(url); 
	}
	
	public static String geturltext(String url) { 
		error("Non implemented function: geturltext");
		return "";
		//return getUrlText(url); 
	}
/*	static void SocketClose(int sh) { delete ((Socket *)sh); }
	static boolean SocketConnected(int sh) { return ((Socket*)sh).connected()!=0; }
	static String SocketGetFile(int sh, String override) {
		static char stbuf[4096];
		Socket *s = (Socket *) sh;
		String retstr;

		EnforceNoDirectories(override);

		int stlen = 0, ret;
		ret = s.blockread(2, &stlen);
		if (!ret)
			return String();

		ret = s.blockread(stlen, stbuf);
		stbuf[stlen] = 0;

		String fn = stbuf;
		EnforceNoDirectories(fn);

		int fl;
		s.blockread(4, &fl);

		char *buf = new char[fl];
		s.blockread(fl, buf);

		FILE *f;
		if (override.length())
		{
			retstr = override;
			f = fopen(override, "wb");
		}
		else
		{
			retstr = fn;
			f = fopen(fn, "wb");
		}
		if (!f)
			err("SocketGetFile: couldn't open file for writing!");
		fwrite(buf, 1, fl, f);
		fclose(f);
		delete[] buf;

		return retstr;
	}
	static int SocketGetInt(int sh) {
		Socket *s = (Socket *) sh;
		int ret;
		char t;
		ret = s.blockread(1, &t);
		if (t != '1')
			err("SocketGetInt() - packet being received is not an int");
		int temp;
		ret = s.blockread(4, &temp);
		return temp;
	}
	static String SocketGetString(int sh) {
		static char buf[4096];
		Socket *s = (Socket *) sh;
		int stlen = 0, ret;
		char t;
		ret = s.blockread(1, &t);
		if (t != '3')
			err("SocketGetString() - packet being received is not a string");
		ret = s.blockread(2, &stlen);
		if (!ret)
			return String();

	//[Rafael, the Esper] #ifdef __BIG_ENDIAN__
	//	stlen >>= 16;
	//#endif

		if (stlen>4095) err("yeah uh dont send such big strings thru the network plz0r");
		ret = s.blockread(stlen, buf);
		buf[stlen] = 0;
		return buf;
	}
	bool SocketHasData(int sh) { return ((Socket*)sh).dataready()!=0; }
	static void SocketSendFile(int sh, String fn) {
		Socket *s = (Socket *) sh;

		EnforceNoDirectories(fn);

		VFILE *f = vopen(fn);
		if (!f)
			err("ehhhhhh here's a tip. SocketSendFile can't send a file that doesnt exist (you tried to send %s)", fn);

		int i = fn.length();
		s.write(2, &i);
		s.write(i, fn);

		int l = filesize(f);
		s.write(4, &l);
		char *buf = new char[l];
		vread(buf, l, f);
		s.write(l, buf);
		delete[] buf;
		vclose(f);
	}
	static void SocketSendInt(int sh, int i) {
		Socket *s = (Socket *) sh;
		char t = '1';
		s.write(1, &t);
		s.write(4, &i);
	}
	static void SocketSendString(int sh, String str) {
		Socket *s = (Socket *) sh;
		int len = str.length();
		if (len>4095) err("yeah uh dont send such big strings thru the network plz0r");
		char t = '3';
		s.write(1, &t);

	//[Rafael, the Esper] #ifdef __BIG_ENDIAN__
	//	len <<= 16;
	//#endif

		s.write(2, &len);

	//[Rafael, the Esper] #ifdef __BIG_ENDIAN__
	//	len >>= 16;
	//#endif

		s.write(len, str);
	}

	// Overkill (2008-04-17): Sockets can send and receive raw length-delimited strings
	static String SocketGetRaw(int sh, int len)
	{
		static char buf[4096];
		Socket *s = (Socket *) sh;
		if (len > 4095)
		{
			err("SocketGetRaw() - can only receive a maximum of 4095 characters at a time. You've tried to get %d", len);
		}
		int ret = s.nonblockread(len, buf);
		buf[ret] = 0;
		return buf;
	}

	// Overkill (2008-04-17): Sockets can send and receive raw length-delimited strings
	static void SocketSendRaw(int sh, String str)
	{
		Socket *s = (Socket *) sh;
		int len = str.length();
		s.write(len, str);
	}

	// Overkill (2008-04-20): Peek at how many bytes are in buffer. Requested by ustor.
	static int SocketByteCount(int sh)
	{
		Socket *s = (Socket *) sh;
		return s.byteCount();
	}
*/

	public static boolean up, down, left, right;
	public static boolean b1, b2, b3, b4;
	
	public static final int SCAN_A = java.awt.event.KeyEvent.VK_A;
	public static final int SCAN_B = java.awt.event.KeyEvent.VK_B;
	public static final int SCAN_C = java.awt.event.KeyEvent.VK_C;
	public static final int SCAN_D = java.awt.event.KeyEvent.VK_D;
	public static final int SCAN_E = java.awt.event.KeyEvent.VK_E;
	public static final int SCAN_F = java.awt.event.KeyEvent.VK_F;
	public static final int SCAN_G = java.awt.event.KeyEvent.VK_G;
	public static final int SCAN_H = java.awt.event.KeyEvent.VK_H;
	public static final int SCAN_I = java.awt.event.KeyEvent.VK_I;
	public static final int SCAN_J = java.awt.event.KeyEvent.VK_J;
	public static final int SCAN_K = java.awt.event.KeyEvent.VK_K;
	public static final int SCAN_L = java.awt.event.KeyEvent.VK_L;
	public static final int SCAN_M = java.awt.event.KeyEvent.VK_M;
	public static final int SCAN_N = java.awt.event.KeyEvent.VK_N;
	public static final int SCAN_O = java.awt.event.KeyEvent.VK_O;
	public static final int SCAN_P = java.awt.event.KeyEvent.VK_P;
	public static final int SCAN_Q = java.awt.event.KeyEvent.VK_Q;
	public static final int SCAN_R = java.awt.event.KeyEvent.VK_R;
	public static final int SCAN_S = java.awt.event.KeyEvent.VK_S;
	public static final int SCAN_T = java.awt.event.KeyEvent.VK_T;
	public static final int SCAN_U = java.awt.event.KeyEvent.VK_U;
	public static final int SCAN_V = java.awt.event.KeyEvent.VK_V;
	public static final int SCAN_W = java.awt.event.KeyEvent.VK_W;
	public static final int SCAN_X = java.awt.event.KeyEvent.VK_X;
	public static final int SCAN_Y = java.awt.event.KeyEvent.VK_Y;
	public static final int SCAN_Z = java.awt.event.KeyEvent.VK_Z;
	public static final int SCAN_0 = java.awt.event.KeyEvent.VK_0;
	public static final int SCAN_1 = java.awt.event.KeyEvent.VK_1;
	public static final int SCAN_2 = java.awt.event.KeyEvent.VK_2;
	public static final int SCAN_3 = java.awt.event.KeyEvent.VK_3;
	public static final int SCAN_4 = java.awt.event.KeyEvent.VK_4;
	public static final int SCAN_5 = java.awt.event.KeyEvent.VK_5;
	public static final int SCAN_6 = java.awt.event.KeyEvent.VK_6;
	public static final int SCAN_7 = java.awt.event.KeyEvent.VK_7;
	public static final int SCAN_8 = java.awt.event.KeyEvent.VK_8;
	public static final int SCAN_9 = java.awt.event.KeyEvent.VK_9;	
	
	public static boolean getkey(int key) {
		return Controls.getKey(key);
	}

	/*
	// Overkill (2006-06-30): Gets the contents of the key buffer.
	// TODO: Implement for other platforms.
	static String GetKeyBuffer()
	{
		//#ifdef __WIN32__
			return keybuffer;
		//#else 
			//err("The function GetKeyBuffer() is not defined for this platform.");
			//return String();
		//#endif
	}

	// Overkill (2006-06-30): Clears the contents of the key buffer.
	// TODO: Implement for other platforms.
	static void FlushKeyBuffer()
	{
		//#ifdef __WIN32__
			FlushKeyBuffer();
		//#else 
			//err("The function FlushKeyBuffer() is not defined for this platform.");
		//#endif
	}

	// Overkill (2006-06-30): Sets the delay in centiseconds before key repeat.
	// TODO: Implement for other platforms.
	static void SetKeyDelay(int d)
	{
		if (d < 0)
		{
			d = 0;
		}
		//#ifdef __WIN32__
			key_input_delay = d;
		//#else 
		//	err("The function SetKeyDelay() is not defined for this platform.");
		//#endif
	}	
	*/

	public static void setVirtualScreen(VImage dest) {
		virtualScreen = dest;
	}
	public static VImage getVirtualScreen() {
		return virtualScreen;
	}

	
	public static void fadeout(int delay, boolean rendermap) {
		unpress(9);
		timer = 0;	
		while (timer<delay)
		{
			if(rendermap)
				screen.render();
			setlucent(100 - (timer*100/delay));
			screen.rectfill(0, 0, screen.getWidth(), screen.getHeight(), Color.BLACK);
			setlucent(0);	
			showpage();
		}
	}
	
	public static void fadein(int delay, boolean rendermap) {
		unpress(9);
		timer = 0;
		while (timer<delay)
		{
			if(rendermap)
				screen.render();
			setlucent(timer*100/delay);
			screen.rectfill(0, 0, screen.getWidth(), screen.getHeight(), Color.BLACK);
			setlucent(0);
			showpage();
		}
	}
	
	// Handy code by [Rafael, the Esper]
	public static void fade(int delay, boolean black) { // fade in and out
		if(black) {
			fadeout(delay, true);
			screen.rectfill(0,0,screen.width, screen.height, Color.BLACK);
			fadein(delay, false);
		}
		else {
			fadeout(delay, false);
			fadein(delay, true);
		}
		
	}
	
	
	// Function (method) calling
	
	public static boolean functionexists(String function) {
		return executefunction(function, true);
	}
	
	/** Check methods in the following order:
	 * 
	 * 1. Direct Class-method (ex: sully.vc.v1_menu.Menu_System.DrawMenu)
	 * 2. System Lib (executed class, ex: Sully.class + method)
	 * 3. Loaded Map Class (ex: Bumsville.class + method)
	 *
	 * The called function must be public and without parameters.
	 * The capitalized version is also checked (ex: "entStart" checks also for "EntStart") 
	 * If the function is not found, nothing happens
	 * 	 
	 */
	public static void callfunction(String function) {
		executefunction(function, false);
	}
	
	private static boolean executefunction(String function, boolean justCheck) {
		
		if(function==null || function.isEmpty()) 
			return false;

		Class path = null;
		// This means that it is a direct class-method
		if (function.lastIndexOf(".") != -1) {
			String s = function.substring(function.lastIndexOf(".") + 1);
			String t = function.substring(0, function.lastIndexOf("."));
			try { 
				path = systemclass.forName(t);
			}
			catch(ClassNotFoundException cnfe) {
				error("Class " + path + " not found for direct execution (" + function + ")");
				return false;
			}
			invokeMethod(path, s, justCheck);
			return true;
		}
		else { // Try to find the class in the current_map
			 boolean notFoundInMap = false;
			 StringBuilder cName = new StringBuilder();
			 if(current_map != null && current_map.getFilename() != null) {
				 	cName.append(systemclass.getPackage().getName() + ".");
				 	
				 	int pos = current_map.getFilename().lastIndexOf('\\');
				 	if(pos==-1)
				 		pos = 0;
				 	
			 		StringBuilder b = new StringBuilder(current_map.getFilename().toLowerCase());
			 		b.replace(pos, pos+1, String.valueOf(Character.toUpperCase(b.charAt(pos))));
			 		String s = b.toString().substring(0, b.indexOf(".map")).replace('\\', '.');
			 		cName.append(s);
			 		
			 		try {
			 			path = systemclass.forName(cName.toString());
			 		}
					catch(ClassNotFoundException cnfe) {
						error("Class " + path + " not found for map execution.");
						notFoundInMap = true; //return;
					}
					if(path!=null) {
				 		if (!invokeMethod(path, function, justCheck))
							notFoundInMap = true;
						else
							return true; // Success
					}
			 }
			
			 // Try to find the method directly in the System class
			 if(current_map == null || current_map.getFilename() == null || notFoundInMap) {
			 
				 path = systemclass;
				 if (invokeMethod(path, function, justCheck)) {
					 return true; // Success
				 }
				 else {
					 error("Error invoking " + function + " in path " + path);
				 }
			 }
		}
		return false;
	}
	
	private static boolean invokeMethod(Class c, String function, boolean justCheck) { 

		Method[] allMethods = c.getDeclaredMethods();
		for (Method m : allMethods) {
			String mname = m.getName();
			if(mname.equals(function) || mname.equals(capitalize(function))) {

				if(justCheck)
					return true;
				
				try {
					//log("Found method " + mname + " in path " + c); // just for debug
					m.invoke(null);
					return true;
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}
	
	/**
	 * Method for loading resources from the classpath, like images, fonts, sounds, etc 
	 */
	public static URL load(String url) {
		log("(" + systemclass + ")" + ", reading: " + url);
		URL resource = systemclass.getResource(url);
		
		// Optional code, a little robustness to avoid case-sensitive issues
		if(resource == null) { // try to capitalize
			String newUrl;
			if(url.lastIndexOf('/') != -1) 
				newUrl = url.substring(0, url.lastIndexOf('/')+1) +
					capitalize(url.substring( url.lastIndexOf('/')+1));
			else
				newUrl = capitalize(url);
			log("WARNING! Resource not found. Trying to read: " + newUrl);
			resource = systemclass.getResource(newUrl);
			
			if(resource==null) { // try uppercase 
				if(url.lastIndexOf('/') != -1) 
					newUrl = url.substring(0,  url.lastIndexOf('/')+1) +
						url.substring( url.lastIndexOf('/')+1).toUpperCase();
				else
					newUrl = url.toUpperCase();
				log("WARNING! Resource not found. Trying to read: " + newUrl);
				resource = systemclass.getResource(newUrl);				
			}
			
			if(resource==null) { // try lowercase 
				if(url.lastIndexOf('/') != -1) 
					newUrl = url.substring(0,  url.lastIndexOf('/')+1) +
						url.substring( url.lastIndexOf('/')+1).toLowerCase();
				else
					newUrl = url.toLowerCase();
				log("WARNING! Resource not found. Trying to read: " + newUrl);
				resource = systemclass.getResource(newUrl);				
			}			
			
			if(resource==null) {
				error("ERROR! Resource not found: " + url);
			}
			
		}
		
		return resource;
	}
	public static void setSystemPath(Class c) {
		systemclass = c;
	}
	

	
}
