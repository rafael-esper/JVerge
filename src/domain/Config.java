package domain;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;

public class Config {

	/// The VERGE 3 Project is originally by Ben Eirich and is made available via
	///  the BSD License.
	///
	/// Please see LICENSE in the project's root directory for the text of the
	/// licensing agreement.  The CREDITS file in the same directory enumerates the
	/// folks involved in this public build.
	///
	/// If you have altered this source file, please log your name, date, and what
	/// changes you made below this line.


	/******************************************************************
	 * verge3: g_startup.cpp                                          *
	 * copyright (c) 2001 vecna                                       *
	 ******************************************************************/

	/****************************** data ******************************/

	private int v3_xres=320, v3_yres=240, v3_bpp;
	private int v3_window_xres=0, v3_window_yres = 0;
	// Overkill (2010-04-29): Aspect ratio enforcing.
	//[Rafael, the Esper] ScaleFormat v3_scale_win = SCALE_FORMAT_ASPECT, v3_scale_full = SCALE_FORMAT_STRETCH;

	private boolean windowmode=true;
	private boolean doublewindowmode=false; // [Rafael, the Esper]
	private boolean nosound=false;
	private int soundengine = 0;
	private boolean cheats=false;
	private String mapname;
	private boolean releasemode=false;
	private boolean automax = true;
	private boolean decompile = false;
	private boolean editcode = false;
	private int gamerate = 100;
	private boolean use_lua = false;
	private boolean vc_oldstring = false;

	/****************************** code ******************************/

	public Config(URL configFilePath) {
		
		// Tries to load the file externally to the jar, in the classpath (ex: .)
		// http://stackoverflow.com/questions/3627426/loading-a-file-relative-to-the-executing-jar-file
		InputStream resourceAsStream = Config.class.getResourceAsStream("/verge.cfg");
		if(resourceAsStream != null) {
			System.out.println("Found local Config file (verge.cfg).");
			this.LoadConfig(resourceAsStream);
			return;
		}
		
		System.out.println("Reading standard config file (JAR!/" + configFilePath + ").");
		InputStream openStream;
		try {
			openStream = configFilePath.openStream();
			this.LoadConfig(openStream);
			openStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	void LoadConfig(InputStream configStream)
	{
		try {
			BufferedReader fin = new BufferedReader(new InputStreamReader(configStream));
			
			String strD = "";
			while ((strD = fin.readLine()) != null) {
				strD = strD.trim();
				
				if(strD.equals("")) // empty line
					continue;

				if(strD.startsWith("#")) // comment
					continue;
				
				if(strD.toLowerCase().startsWith("xres")) {
					this.v3_xres = Integer.parseInt(strD.substring(4).trim());
				}
				if(strD.toLowerCase().startsWith("yres")) {
					this.v3_yres = Integer.parseInt(strD.substring(4).trim());
				}
				if(strD.toLowerCase().startsWith("windowmode")) {
					int i = Integer.parseInt(strD.substring("windowmode".length()).trim());
					this.setWindowmode(i == 1 ? true : false);
				}
				if(strD.toLowerCase().startsWith("doublewindowmode")) {
					int i = Integer.parseInt(strD.substring("doublewindowmode".length()).trim());
					this.setDoubleWindowmode(i == 1 ? true : false);
				}
				if(strD.toLowerCase().startsWith("startmap")) {
					this.mapname = strD.substring("startmap".length()).trim();
				}
				if(strD.toLowerCase().startsWith("lua")) {
					int i = Integer.parseInt(strD.substring(4).trim());
					this.use_lua = i == 1 ? true : false;
				}
				if(strD.toLowerCase().startsWith("nosound")) {
					int i = Integer.parseInt(strD.substring("nosound".length()).trim());
					this.setNosound(i == 0 ? false : true);
				}	
				
			}
			
		} catch (IOException e) {
			System.err.println("Config:: IOException: " + e.getMessage());
			e.printStackTrace();
		}
			
		/*
	    // Overkill (2010-04-29): Scaling policies.
	    if (cfg_KeyPresent("scalewin"))
	    {
	        int value = cfg_GetIntKeyValue("scalewin");
	        if(value >= 0 && value < SCALE_FORMAT_COUNT)
	        {
	            v3_scale_win = (ScaleFormat) value;
	        }
	    }
	    if (cfg_KeyPresent("scalefull"))
	    {
	        int value = cfg_GetIntKeyValue("scalefull");
	        if(value >= 0 && value < SCALE_FORMAT_COUNT)
	        {
	            v3_scale_full = (ScaleFormat) value;
	        }
	    }
		if (cfg_KeyPresent("bpp"))
			v3_bpp = cfg_GetIntKeyValue("bpp");

		if (cfg_KeyPresent("window_x_res"))
			v3_window_xres = cfg_GetIntKeyValue("window_x_res");
		if (cfg_KeyPresent("window_y_res"))
			v3_window_yres = cfg_GetIntKeyValue("window_y_res");

		if (cfg_KeyPresent("nosound"))
			sound = cfg_GetIntKeyValue("nosound") ? false : true;
		if (cfg_KeyPresent("soundengine"))
			soundengine = cfg_GetIntKeyValue("soundengine");
		if (cfg_KeyPresent("automax"))
			automax = cfg_GetIntKeyValue("automax") ? true : false;
		if (cfg_KeyPresent("vcverbose"))
			verbose = cfg_GetIntKeyValue("vcverbose");
//		if (cfg_KeyPresent("paranoid"))               
//			vc_paranoid = cfg_GetIntKeyValue("paranoid");
		if (cfg_KeyPresent("arraycheck"))
			vc_arraycheck = cfg_GetIntKeyValue("arraycheck");
		if (cfg_KeyPresent("appname"))
			setWindowTitle(cfg_GetKeyValue("appname"));
		if (cfg_KeyPresent("releasemode"))
			releasemode = cfg_GetIntKeyValue("releasemode") ? true : false;
		if (cfg_KeyPresent("gamerate"))
			gamerate = cfg_GetIntKeyValue("gamerate");
		if (cfg_KeyPresent("v3isuberlikethetens"))
			cheats = true;
		if (cfg_KeyPresent("decompile"))
			decompile = true;
		if (cfg_KeyPresent("editcode"))
			editcode = cfg_GetIntKeyValue("editcode") ? true : false;
		if (cfg_KeyPresent("logconsole"))
		{
			logconsole = true;
			initConsole();
		} else if (cfg_KeyPresent("logconsole-normalstdout")) {
			logconsole = true;
		}
	    if (cfg_KeyPresent("oldstring"))
	        vc_oldstring = true;

		if (cfg_KeyPresent("mount1"))
			MountVFile(cfg_GetKeyValue("mount1"));
		if (cfg_KeyPresent("mount2"))
			MountVFile(cfg_GetKeyValue("mount2"));
		if (cfg_KeyPresent("mount3"))
			MountVFile(cfg_GetKeyValue("mount3"));
*/
		
		//[Rafael, the Esper] void platform_ProcessConfig();
		//platform_ProcessConfig();

		//#ifndef ALLOW_SCRIPT_COMPILATION
		//[Rafael, the Esper] releasemode = true;
		//[Rafael, the Esper] editcode = false;
		//#endif

		//#ifndef ENABLE_LUA
		//[Rafael, the Esper] if(use_lua) err("User asked for lua, but build does not have lua enabled!");
		//#endif
	}

	public int getV3_xres() {
		return v3_xres;
	}
	public int getV3_yres() {
		return v3_yres;
	}
	public boolean isWindowmode() {
		return windowmode;
	}
	public String getMapname() {
		return mapname;
	}

	public boolean isNosound() {
		return nosound;
	}

	public void setNosound(boolean nosound) {
		this.nosound = nosound;
	}

	public void setWindowmode(boolean windowmode) {
		this.windowmode = windowmode;
	}

	public boolean isDoubleWindowmode() {
		return this.doublewindowmode;
	}

	public void setDoubleWindowmode(boolean b) {
		this.doublewindowmode = b;
		
	}

	
}
