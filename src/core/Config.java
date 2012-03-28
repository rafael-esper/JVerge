package core;

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

	int v3_xres=320, v3_yres=240, v3_bpp;
	int v3_window_xres=0, v3_window_yres = 0;
	// Overkill (2010-04-29): Aspect ratio enforcing.
	//rbp ScaleFormat v3_scale_win = SCALE_FORMAT_ASPECT, v3_scale_full = SCALE_FORMAT_STRETCH;

	boolean windowmode=true;
	boolean sound=true;
	boolean cheats=false;
	String mapname;
	boolean releasemode=false;
	boolean automax = true;
	boolean decompile = false;
	boolean editcode = false;
	int gamerate = 100;
	int soundengine = 0;
	boolean use_lua = false;
	boolean vc_oldstring = false;

	/****************************** code ******************************/

	public Config(String configFile) {
		this.LoadConfig(configFile);
	}
	
	void LoadConfig(String configFile)
	{
		System.out.println("LoadConfig: " + configFile);
		// rbp Implementar via Properties
		/*
		cfg_Init("verge.cfg");
		cfg_SetDefaultKeyValue("startmap", "");

		if (cfg_KeyPresent("lua"))
			use_lua = cfg_GetIntKeyValue("lua") ? true : false;
		if (cfg_KeyPresent("xres"))
			v3_xres = cfg_GetIntKeyValue("xres");
		if (cfg_KeyPresent("yres"))
			v3_yres = cfg_GetIntKeyValue("yres");
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

		if (cfg_KeyPresent("windowmode"))
			windowmode = cfg_GetIntKeyValue("windowmode") ? true : false;
		if (cfg_KeyPresent("nosound"))
			sound = cfg_GetIntKeyValue("nosound") ? false : true;
		if (cfg_KeyPresent("soundengine"))
			soundengine = cfg_GetIntKeyValue("soundengine");
		if (cfg_KeyPresent("automax"))
			automax = cfg_GetIntKeyValue("automax") ? true : false;
		if (cfg_KeyPresent("startmap"))
			strcpy(mapname, cfg_GetKeyValue("startmap"));
		if (cfg_KeyPresent("vcverbose"))
			verbose = cfg_GetIntKeyValue("vcverbose");
//		if (cfg_KeyPresent("paranoid"))                FIXME
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
		
		//rbp void platform_ProcessConfig();
		//platform_ProcessConfig();

		//#ifndef ALLOW_SCRIPT_COMPILATION
		//rbp releasemode = true;
		//rbp editcode = false;
		//#endif

		//#ifndef ENABLE_LUA
		//rbp if(use_lua) err("User asked for lua, but build does not have lua enabled!");
		//#endif
	}

	int getInitialWindowXres() {
		return v3_window_xres;
	}

	int getInitialWindowYres() {
		return v3_window_yres;
	}
	
}
