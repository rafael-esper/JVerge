package sully.vc.v1_rpg;

import static core.Script.*;
import domain.VImage;


/***************************************************************************
*                                                                          *
*                    V1RPG VergeC Library for Verge3                       *
*                        Copyright (C)2004 vecna                           *
*                                                                          *
***************************************************************************/
public class V1_Weather {
	
	// Valid weather modes.
	//
	public static final int WEATHER_NONE	=	0;
	public static final int WEATHER_CLOUDS	=	1;
	public static final int WEATHER_RAIN	=	2;
	public static final int WEATHER_SNOW	=	3;
	public static final int WEATHER_FOG		=	4;
	public static final int WEATHER_NIGHT	=	5;
	
	public static V1_Weather weather = new V1_Weather(); // rbp, to instanciate inner classes
	/********************************** data **********************************/
	
	static int weather_mode;
	static int lxwin;
	static int lywin;
	
	/********************************** code **********************************/
	
	// Sets a weather mode to be displayed via the V1RPG_Renderfunc()
	// function from v1_rpg.vc
	//
	// valid weather modes are:
	//
	// WEATHER_NONE, WEATHER_CLOUDS, , 
	// WEATHER_FOG, WEATHER_NIGHT
	//
	// WEATHER_RAIN and WEATHER_SNOW are not yet implemented.
	public static void SetWeather(int mode)
	{
		weather_mode = mode;
		switch (mode)
		{
			case WEATHER_CLOUDS: InitializeClouds();break;
			case WEATHER_RAIN: InitializeRain();break;
			case WEATHER_SNOW: InitializeSnow();break;
		}
		
		lxwin = xwin;
		lywin = ywin;
	}
	
	// Renders the weather.  Called by V1RPG_Renderfunc();
	//
	static void RenderWeather()
	{
		switch (weather_mode)
		{
			case WEATHER_CLOUDS: RenderClouds();break;
			case WEATHER_FOG: RenderFog();break;
			case WEATHER_NIGHT: colorfilter(CF_BLUE, screen);break;
		}
	}
	
	
	
	// shakes the screen horizontally with a magnitude of x_intensity and 
	// vertically with a magnitude of y_intensity for duration time 
	public static void Earthquake( int x_intensity, int y_intensity, int duration ) 
	{
		int original_x, original_y, original_camera;
		
		original_camera = cameratracking;
		cameratracking = 0; //turn off cameratracking
		original_x = xwin;
		original_y = ywin;
		
		timer = 0;
		while ( timer < duration )
		{
			xwin = original_x;
			ywin = original_y;	
			
			xwin =  xwin+random(0-x_intensity,x_intensity);
			ywin =  ywin+random(0-y_intensity,y_intensity);
			
			render();
			showpage();
		}
		
		xwin = original_x; //restore original camera position
		ywin = original_y;
		
		cameratracking = original_camera;	//restore cameratracking to the player-entity
	}
	
	/********************************* clouds *********************************/
	
	public static final int  NUM_CLOUDS 	=	15;
	public static final int  CLOUDSX 		=	640;
	public static final int  CLOUDSY 		=	480;
	
	static int cloudthink;
	static boolean clouds_initd;
	
	class CloudType
	{
		int x, y, c;
		int speed, cnt;
	}
	
	static CloudType _clouds[] = new CloudType[NUM_CLOUDS];
	
	static VImage _cloud1;
	static VImage _cloud2;
	static VImage _cloud3;
	static VImage _cloud1s;
	static VImage _cloud2s;
	static VImage _cloud3s;
	
	// Takes care of setting up the clouds system.
	//
	static void InitializeClouds()
	{
		if (!clouds_initd)
		{
			_cloud1 = new VImage(load("res\\system\\cloud1.gif"));
			_cloud2 = new VImage(load("res\\system\\cloud2.gif"));
			_cloud3 = new VImage(load("res\\system\\cloud3.gif"));
			
			_cloud1s= duplicateimage(_cloud1); silhouette(0, 0, RGB(40, 40, 40), _cloud1, _cloud1s);
			_cloud2s= duplicateimage(_cloud2); silhouette(0, 0, RGB(40, 40, 40), _cloud2, _cloud2s);
			_cloud3s= duplicateimage(_cloud3); silhouette(0, 0, RGB(40, 40, 40), _cloud3, _cloud3s);
			clouds_initd = true;
		}
		
		int i;
		for (i=0; i<NUM_CLOUDS; i++)
		{
			_clouds[i] = weather.new CloudType();
			_clouds[i].x = random(0, CLOUDSX) * 65536;
			_clouds[i].y = random(0, CLOUDSY) * 65536;		
			_clouds[i].c = random(0,2);
			_clouds[i].speed = random(10, 30) * 65536 / 100;
			_clouds[i].cnt = 0;
		}
		cloudthink = systemtime;
	}
	
	// Does the cloud processesing.
	//
	static void ThinkClouds()
	{
		int i;
		
		if (abs(lxwin - xwin) > 50 || abs(lywin - ywin) > 50)
		{
			InitializeClouds();
			return;
		}
			
		for (i=0; i<NUM_CLOUDS; i++)
		{
			_clouds[i].x -= _clouds[i].speed  - ((lxwin-xwin) * 65536);
			_clouds[i].y -= _clouds[i].speed  - ((lywin-ywin) * 65536);
			
			if (_clouds[i].x < 0) _clouds[i].x = random(480, 640) * 65536;
			if (_clouds[i].y < 0) _clouds[i].y = random(360, 480) * 65536;
			if (_clouds[i].x > 640*65536) _clouds[i].x = random(0, 60) * 65535;
			if (_clouds[i].y > 480*65536) _clouds[i].y = random(0, 10) * 65535;
		}
		lxwin=xwin;
		lywin=ywin;
	}
	
	//draws the clouds.  called by RenderWeather()
	static void RenderClouds()
	{
		while (cloudthink<systemtime)
		{
			ThinkClouds();
			cloudthink++;
		}
		
		setlucent(50);
		int i, zx, zy;
		for (i=0; i<NUM_CLOUDS; i++)
		{
		setlucent(25);
			zx = _clouds[i].x / 65536 - 160;
			zy = _clouds[i].y / 65536 - 120;
			
			switch (_clouds[i].c)
			{
				case 0: tblit(zx, zy, _cloud1, screen);
				setlucent(75);
						tblit(zx+3, zy+18, _cloud1s, screen);break;
				case 1: tblit(zx, zy, _cloud2, screen);
				setlucent(75);
						tblit(zx+3, zy+32, _cloud2s, screen);break;
				case 2: tblit(zx, zy, _cloud3, screen);
				setlucent(75);
						tblit(zx+3, zy+64, _cloud3s, screen);break;
			}		
		}
		setlucent(0);
		lxwin = xwin;
		lywin = ywin;
	}
	
	/********************************** fog ***********************************/
	
	static VImage _fog = new VImage(load("res\\system\\fog.png"));
	
	// Draws the fog.  Called by RenderWeather();
	//
	static void RenderFog()
	{
		setlucent(50);
		wrapblit((systemtime/4)+xwin, (systemtime/4)+ywin, _fog, screen);
		setlucent(0);
	}
	
	/********************************** rain **********************************/
	
	class SnowNode 
	{
		int x, y;
	}
	
	static void InitializeRain()
	{
	
	}
	
	static void InitializeSnow()
	{
	/*	for (int i=0; i<SnowParticles; i++)
		{
			snow[i].x = rnd(0, SnowScrX);
			snow[i].y = rnd(0, SnowScrY);
		}*/
	}
	
}