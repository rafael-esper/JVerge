package sully.vc.v1_rpg;

import static core.Script.*;
import static sully.vc.v1_menu.Menu_System.*;
import static sully.vc.Sfx.*;

import java.awt.Color;

import sully.Flags;

import domain.VFont;
import domain.VImage;

/***************************************************************************
*                                                                          *
*                    V1RPG VergeC Library for Verge3                       *
*                        Copyright (C)2004 vecna                           *
*                                                                          *
***************************************************************************/

/*#include "vc/v1_rpg/v1_music.vc"
#include "vc/v1_rpg/v1_textbox.vc"
#include "vc/v1_rpg/v1_maineffects.vc"
#include "vc/v1_rpg/v1_weather.vc"
#include "vc/v1_rpg/v1_simpletype.vc"
*/

public class V1_RPG {
	
	
	/******************* Different fades for the fade-system *********************/
	
	public static final int TNONE		=	0;
	public static final int TBLACK		=	1;
	public static final int TWHITE		=	2;
	public static final int TCROSS		=	3;
	public static final int TBOX		=	4;
	public static final int TWIPE		=	5;
	public static final int TCIRCLE		=	6;
	
	
	/***************************** gamestate data *****************************/
	
	// the two vc layers!  
	// These are basically optional persistant images you can draw to.
	public static VImage v1_vclayer = duplicateimage(screen);
	public static VImage v1_vclayer2 = duplicateimage(screen);
	
	/****************************** internal data *****************************/
	
	static VImage textboxframe;
	static VImage textmore;
	
	public static VFont v1rpg_LargeFont; //stores the original largefont.
	public static VFont _v1rpg_LargeFont;
	public static VFont v1rpg_SmallFont; //stores the original smallfont.
	public static VFont _v1rpg_SmallFont;
	
	//something to switch the font used by the textbox.
	// initially set to whatever you want.
	public static VFont textBox_font = v1rpg_LargeFont; 
	
	//something to switch the font used by the promptbox.
	// initially set to whatever you want.
	public static VFont promptBox_font = v1rpg_LargeFont;
	
	// Notes if the player is presently allowed to save his game or not.
	public static boolean can_save;
	
	// The (x,y) coordinates for the mapswitch, and the transition mode
	public static int mapswitchx;
	public static int mapswitchy;
	static int mapswitche = TBLACK;
	
	//variables concerning the V1RPG_RenderFunc quake options.
	public static boolean vc_quake_on = false;
	static int vc_quake_x;
	static int vc_quake_y;
	int q_original_x, q_original_y;
	
	//variables concerning the V1RPG_RenderFunc tinting options.
	public static boolean tint_on = false;
	static Color tint_color;
	static int tint_lucent;
	
	//variables concerning V1RPG_RenderFunc
	public static boolean vc_filter_on = false;
	public static boolean vc_custom_filter_on = false;
	static int vc_filter;
	static int vc_filter_lucent;
	
	//variables that store the V1RPG_RenderFunc custom filter colors.
	static Color _VCCustomFilter_color_1;
	static Color _VCCustomFilter_color_2;
	
	//variables concerning the V1RPG Dual Render Mode.
	static String _old_rstring;
	static int _dualmode_on;
	static int _dualmode_counter;
	
	
	/********************************** code **********************************/
	
	// The main renderfunction for the v1 RPG effects library.  
	// Every time Render() is called or an R is reached on the map's 
	// Renderstring, this function is called... as long as hookretrace("V1RPG_RenderFunc")
	// was set beforehand (by default in The Sully Chronicles, this line is placed in 
	// the system.vc autoexec function())
	//
	// This function checks for and does the following events:
	// * Renders the Weather if any is on (see SetWeather() in v1_weather.vc)
	// * if there's a color filter on, applies it. ( see the VCScreenFilter() functions below)
	// * TBlits the vclayer over the screen. (see VCPutIMG(), ClearVCLayer(), VCText() below)
	// * Applies a tint if it's on. (see VCLayerTintScreen() below)
	// * shakes the screen if the quake has been set (see VCQuake() below)
	//
	//
	public static void V1RPG_RenderFunc()
	{
		//for weather effects
		V1_Weather.RenderWeather();
	
		if( vc_filter_on ) {
			
			if( vc_filter_lucent != 0)
			{
				setlucent(vc_filter_lucent);
				colorfilter( vc_filter, screen );
				setlucent(0);	
			}
			else
			{
				colorfilter( vc_filter, screen );
			}
		}
		
		tblit(0, 0, v1_vclayer, screen);
		
		//for vclayer tinting.
		if (tint_on )
		{
			setlucent(tint_lucent);
			rectfill(0, 0, 320, 240, tint_color, screen);//RBP
			setlucent(0);
		}
		
		//for continuous quaking!
		if( vc_quake_on )
		{
			//toggles cameratracking.  Since we need to have xwin and ywin update
			//and they only update during Render(), and this is a HookRender function,
			//we need to do this activity every-other render.
			if( cameratracking != 0 ) {
				cameratracking = 0;
			} else {
				cameratracking = 1;
			}
					
			xwin =  xwin+random(0-vc_quake_x,vc_quake_x);
			ywin =  ywin+random(0-vc_quake_y,vc_quake_y);	
		}
	}
	
	// Sets the colorfilter option for V1RPG_RenderFunc.
	// 
	//
	void VCScreenFilter( int color_filter ) {
		vc_filter_on = true;
		vc_filter = color_filter;
	}
	
	// returns true if there's a color filter on of any type.
	//
	boolean VCScreenFilterOn()
	{
		return vc_filter_on;
	}
	
	void VCScreenFilterOff() {
		vc_filter_on = false;
		vc_custom_filter_on = false;
		_VCCustomFilter_color_1 = null;
		_VCCustomFilter_color_2 = null;
		vc_filter_lucent = 0;
	}
	
	// applies a lucency to the colorfilter.  
	// Remember: 100% lucent is nothing, 
	// 0% lucent is solid, 25% lucent is lightly see-though, 
	// 75% lucent is very see-through, etc etc.
	void VCScreenFilterLucent( int perc ) {
		vc_filter_lucent = perc;
	}
	
	// This sets a custom color filter.  
	// color_1 defines the darkest color on a screen, 
	// color_2 defines the brightest color on a screen.
	//
	// Make sure to supply both arguments using RGB()
	//
	// Example:
	//	VCCustomFilter( RGB(0,0,0), RGB(255,255,0) ); //this will make the screen 
	//		//all shades of Yellow... as long as V1RPG_RenderFunc is in the hookretrace.
	void VCCustomFilter( Color color_1, Color color_2 )
	{
		vc_custom_filter_on = true;
	
		vc_filter_on = true;
		vc_filter = CF_CUSTOM;	
		
		setcustomcolorfilter( color_1, color_2 );
		
		_VCCustomFilter_color_1 = color_1;
		_VCCustomFilter_color_2 = color_2;
	}
	
	// If something else accesses SetCustomColorFilter() directly without accessing 
	// VCCustomFilter(), that something else can just call VCCustomFilterRestore() after 
	// it's done and the V1RPG library will continue to function as normal with it's custom
	// color filter.
	//
	// In the Sully Chronicles, the menu function uses SetCustomColorFilter() to draw it's 
	// colored menu backgrounds, and so it calls this function after it's done to make sure
	// any custom screen filtering resumes as normal (like in Sara's flashback).
	public static void VCCustomFilterRestore()
	{
		vc_custom_filter_on = true;
		vc_filter_on = true;
		vc_filter = CF_CUSTOM;
		setcustomcolorfilter(_VCCustomFilter_color_1, _VCCustomFilter_color_2);
	}
	
	// returns 1 if VCCustomFilter() is active, 0 if it is not.
	public static boolean VCCustomFilterOn()
	{
		return vc_custom_filter_on;
	}
	
	// returns the integer color-value of the current VCCustomFilter's "dark" color.
	public static Color getVCCustomFilterColor1()
	{
		return _VCCustomFilter_color_1;
	}
	
	// returns the integer color-value of the current VCCustomFilter's "light" color.
	public static Color getVCCustomFilterColor2()
	{
		return _VCCustomFilter_color_2;
	}
	
	
	// Tints the vc layer the given color a given percent.
	// Useful for fading too and from a specific color.
	public static void VCLayerTintScreen(Color color, int percent)
	{
		tint_on = true;
		tint_color = color;
		tint_lucent = percent;
	}
	
	// Turns off the vc layer tinting, if it was on.
	public static void VCLayerTintOff()
	{
		tint_on = false;
	}
	
		
	
	// Clears the VC Layer of any and all images.
	//
	public static void ClearVCLayer() 
	{
		rectfill(0, 0, imagewidth(v1_vclayer), imageheight(v1_vclayer), transcolor, v1_vclayer); //blanks the vc layer
		//v1_vclayer.g.clearRect(0, 0, imagewidth(v1_vclayer), imageheight(v1_vclayer));
		
	//	rectfill(0, 0, ImageWidth(v1_vclayer), 20, RGB(0,0,0), v1_vclayer); //Letterboxing!
	//	rectfill(0, ImageHeight(v1_vclayer)-20, ImageWidth(v1_vclayer), ImageHeight(v1_vclayer), RGB(0,0,0), v1_vclayer); //Letterboxing!
	}
	
	// Takes an image's path and filename, and the position to put it onto the 
	// VC Layer.  It then loads it, blits it only the vc layer, and deletes the 
	// image.
	//
	// No fuss, no muss.  Very useful for drawing things to the screen to stick 
	// around for a while in a simple manner.
	//
	// If you ever want to wipe the screen after calling this, use ClearVCLayer().
	public static void VCPutIMG( String img_name, int x_pos, int y_pos )
	{
		VImage img = new VImage(load(img_name));
		tblit( x_pos, y_pos, img, v1_vclayer );
	}
	
	// Fills the vclayer with an RGB()-defined colorvalue.
	public static void FillVCLayer( Color matte_color ) {
		rectfill(0, 0, imagewidth(v1_vclayer), imageheight(v1_vclayer), matte_color, v1_vclayer); //fills the vc layer with matte_color
	}
	
	// Prints string s at (x,y) on the VCLayer in the v1-rpg lib's SmallFont.
	//
	// the v1-rpg lib's SmallFont.can be changed with v1_setSmallfont()
	public static void VCText( int x, int y, String s ) 
	{
		printstring( x,y, v1_vclayer, v1rpg_SmallFont, s);
	}
	
	
	// Prints string s centered on the vclayer at height y.
	// the font is the v1rpg smallfont.
	//
	public static void VCCenterText( int y, String s ) 
	{
		printcenter( imagewidth(v1_vclayer)/2, y, v1_vclayer, v1rpg_SmallFont, s );
	}
	
	// Turns on vc-layer quaking with a horizontal shake of quake_x and a vertical
	// shake of quake_y.
	// 
	// The screen will shake but loosely follow the player until VCQuakeOff() is 
	// called.
	//
	// This is similar to EarthQuake() in v1_weather.vc, but allows the player to 
	// walk around and do stuff wile the screen is shaking.
	public static void VCQuake( int quake_x, int quake_y ) 
	{
		vc_quake_on = true;
		vc_quake_x = quake_x;
		vc_quake_y = quake_y;
		cameratracking = 0;
	}
	
	// Turns off the vclayer quake effect.
	public static void VCQuakeOff() 
	{
		vc_quake_on = false;
		cameratracking = 1;
	}
		
	
	
	// This function enables the V1RPG Dual VC-layer mode!  It's totally wacky!
	// It basically allows for two Rs in current_map.renderstring, with V1RPG_RenderFunc() being 
	// called when the first R triggers, and a straight-up TBlit of the second vc layer 
	// image on the second R.
	//
	// This mode is used on lab.map to blit things in the foreground, since lab.map puts 
	// the regular R-layer behind the entities and the tiles to make the neat translucent
	// fog effect work.
	//
	public static void V1_StartDualMode( String newrstring ) {
		_dualmode_on = 1;
		_dualmode_counter = 0;
		_old_rstring = current_map.renderstring;
		current_map.renderstring = newrstring;
		
	
		//clear vclayer2 before we start!
		rectfill(0, 0, imagewidth(v1_vclayer2), imageheight(v1_vclayer2), transcolor, v1_vclayer2); //clear vclayer2
		
		hookretrace("sully.vc.v1_rpg.V1_RPG", "V1RPG_RenderFunc_DUALMODE");
	}
	
	// If Dual-rendering mode is on when this is called, it turns the mode off and 
	// restores the original current_map.renderstring.
	//
	// Otherwise does nothing at all.
	public static void V1_StopDualMode() {
		
		if( _dualmode_on != 0)
		{
			_dualmode_on = 0;
	
			current_map.renderstring = _old_rstring;
	
			hookretrace("sully.vc.v1_rpg.V1_RPG", "V1RPG_RenderFunc");
		}
	}
	
	// This is the function that gets set to the hookretrace by V1_StartDualMode()
	// Do not call this yourself unless you Know What You're Doing.
	//
	// ...generally, you shouldn't use the Dual rendering mofde unless you Know What 
	// You're Doing, too.
	public static void V1RPG_RenderFunc_DUALMODE() {
		if( _dualmode_counter == 0 ) {
			V1RPG_RenderFunc();
			_dualmode_counter = 1;
		} else {
			tblit(0, 0, v1_vclayer2, screen);
			_dualmode_counter = 0;
		}	
	}
	
	
	
	
	// This function takes care of all the loading and initialization for the v1_RPG 
	// library.  
	//
	// You may recall seeing it in The Sully Chronicles's system.vc at the top of the
	// autoexec() function with all the other library load functions!
	public static void V1RPG_LoadResources()
	{
		textboxframe = new VImage(load("res\\system\\textbox2.gif"));
		textmore = new VImage(load("res\\system\\more.gif"));
			
		v1rpg_LargeFont = new VFont(load("res\\system\\font3_vw.gif"));
		_v1rpg_LargeFont = v1rpg_LargeFont; //stores the original largefont.
	
		v1rpg_SmallFont = new VFont(load("res\\system\\smallFont3.gif"));
		_v1rpg_SmallFont = v1rpg_SmallFont; //stores the original smallfont.
	
		enablevariablewidth(v1rpg_LargeFont);
		enablevariablewidth(v1rpg_SmallFont);
	}
	
	
	// One of the cornerstones of the v1RPG effect library, this function takes 
	// care of all of the awesome things this library can do on the mapswitch.
	//
	// It stores the x and y that the party goes to on the new map, and the effect
	// to use when the new map loads.  The it does the first half of the effect, 
	// sets the music volume to the global value (set by the v1_music.vc functions)
	// just incase the volume was altered manually using SetVolume(), and turns off
	// any weather that was going on.
	//
	// At that point, it calls Map() with the specified mapname.  Map(), if you've 
	// read the v3 manual, ceases all activity on the present map, loads the new map, 
	// and calls the new map's personal autoexec function (you set that in maped.  
	// All of the Sully maps have theirs set to "start()", but you can name yours 
	// whatever.)
	//
	// To have a V1_MapSwitch() complete successfully on the new map, make sure 
	// v1_InitMap(); is called.  In The Sully Chronicles, v1_InitMap() is called 
	// inside InitMap(), which is defined in system.vc
	//
	public static void V1_MapSwitch(String mapn, int x, int y, int effect)
	{
		mapswitche = effect;
		mapswitchx = x;
		mapswitchy = y;
		
		switch (effect)
		{
			case 1: V1_Maineffects.FadeOut(30);break;
			case 2: V1_Maineffects.WhiteOut(30);break;
			case 3: blit(0, 0, screen, V1_Maineffects.crossfade_img);break;
			case 4: V1_Maineffects.BoxOut(30);break;
			case 5: blit(0, 0, screen, V1_Maineffects.crossfade_img);break;
			case 6: V1_Maineffects.CircleOut(50);break;
		}
		
		setmusicvolume( global_music_volume );
		V1_Weather.SetWeather(V1_Weather.WEATHER_NONE);
		map(mapn);
	}
	
	
	
	// The v1_rpg library's map upkeep function.
	//
	// Put this function in your map's autoexec function if you want 
	// your v1_mapswitch transitions to properly work.
	//
	// To have a V1_MapSwitch() complete successfully on the new map, make sure 
	// v1_InitMap(); is called.  
	
	// In The Sully Chronicles, v1_InitMap() is called inside InitMap(), which is 
	// defined in system.vc
	public static void V1_InitMap()
	{
		setmusicvolume( global_music_volume );
	
		// Do fade transition
		switch (mapswitche)
		{
			case 1: V1_Maineffects.FadeIn(30);break;
			case 2: V1_Maineffects.WhiteIn(30);break;
			case 3: V1_Maineffects.CrossFade(50);break;
			case 4: V1_Maineffects.BoxIn(30);break;
			case 5: V1_Maineffects.TransWipe(100);break;
			case 6: V1_Maineffects.CircleIn(50);break;
		}	
	}
	
	
	
	// Warps the player to tile coordinates (x,y), using a specified fade effect.
	// The fade effects have the following valid defines:
	//   TNONE, TWHITE, TCROSS, TBOX, TWIPE, TCIRCLE
	// 
	// NOTE: References simpletype library's GetPlayer() to get the mapindex of 
	//       the player entity.  If you decide to use a different partyhandling 
	//       system with this library, make sure to change those to the proper 
	//       player entity index.
	public static void Warp(int x, int y, int effect)
	{
		switch (effect)
		{
			case 1:	V1_Maineffects.FadeOut(30);break;
			case 2: V1_Maineffects.WhiteOut(30);break;
			case 3: blit(0, 0, screen, V1_Maineffects.crossfade_img);break;
			case 4: V1_Maineffects.BoxOut(30);break;
			case 5: blit(0, 0, screen, V1_Maineffects.crossfade_img);break;
			case 6: V1_Maineffects.CircleOut(50);break;
		}
	
		entity.get(getplayer()).setx(x*16);
		entity.get(getplayer()).sety(y*16);
		render();
			
		switch (effect)
		{
			case 1: V1_Maineffects.FadeIn(30);break;
			case 2: V1_Maineffects.WhiteIn(30);break;
			case 3: V1_Maineffects.CrossFade(50);break;
			case 4: V1_Maineffects.BoxIn(30);break;
			case 5: V1_Maineffects.TransWipe(50);break;
			case 6: V1_Maineffects.CircleIn(50);break;
		}
	}
	
	
	
	// Box-drawing tool for this library.
	// Draws a box at screen coordinates (x,y) with width and height of (w,h)
	//
	// NOTE: This is presently a wrapper around the v1_Menu's MenuDrawBackground() function.
	//       if you wish to use this library without v1_Menu, alter the contents of this 
	//       function as you see fit.
	//
	public static void V1_Box(int x, int y, int w, int h)
	{
		MenuDrawBackground(x, y, x+w, y+h, true);
	}
	
	
	// Takes a one-line message and prints it in a centered MenuBox with the specified font.
	//
	// Does not do stringlength-checking, so you can make a long string draw off of the screen.
	// Does not factor in newlines or tabs, either.
	static void CenterMessageBox( VFont font, String msg ) {
		int wid, high;
		
		high = fontheight( font );
		wid = textwidth( font, msg );
	
		V1_Box( (imagewidth(screen)/2)-(wid/2)-high, (imageheight(screen)/2)-high, wid+(high*2), high*3 );
		
		printcenter(imagewidth(screen)/2, imageheight(screen)/2, screen, font, msg);
	}
	
	
	
	// Takes a one-line message and prints it in a centered MenuBox 
	// with the v1rpg Small Font.  The message stays up for duration, or until 
	// b1 is pressed.
	//
	// the menu is disabled during this function.
	public static void Banner( String msg, int duration ) 
	{
		boolean menu_mode = MenuCanBeOn();
		MenuOff();
	
		timer = 0;
		while ( timer<duration && !b1   )
		{
			render();
			CenterMessageBox( v1rpg_SmallFont, msg  );
			
			showpage();
		}
	
		unpress(0);
		
		if(menu_mode)	MenuOn();
	}
	
	
	
	
	
	// Turns on the ability to save.
	// 
	public static void SaveEnable() {
		can_save = true;
	}
	
	// Turns off the ability to save.
	// 
	public static void SaveDisable() {
		can_save = false;
	}
	
	// Places tile t onto map layer 0 at (x,y).
	//
	// obstructs the tile if obs is 1, unobstructs the tile if obs is 0
	// leaves the obstruction alone if obs is any other value.
	public static void AlterBTile(int x, int y, int t, int obs) {
		settile(x,y,0,t);
		
		if( obs==0 || obs == 1  ) 
			setobs(x,y,obs);
	}
	
	// Places tile t onto map layer 1 at (x,y).
	//
	// obstructs the tile if obs is 1, unobstructs the tile if obs is 0
	// leaves the obstruction alone if obs is any other value.
	public static void AlterFTile(int x, int y, int t, int obs) {
		settile(x,y,1,t);
		
		if( obs==0 || obs == 1  ) 
			setobs(x,y,obs);
	}
	
	// The master Chest-opening script.
	// returns 1 if the chest referenced at flags[flag] was not open, and sets the tile at 
	// (x,y) to t, while playing an opening-the-chest sound.
	//
	// else returns 0.
	//
	// See any treasure script in Sully for use example.
	//
	public static boolean OpenTreasure( int flag, int x, int y, int open_tile ) {
		if( Flags.flags[flag] == 0 ) {
			Flags.flags[flag] = 1;
			settile(x,y,0,open_tile);
			SoundSwitch();
			return true;
		} else {
			return false;
		}
	}
}