package sully;

import static core.Script.*;

import static sully.vc.simpletype_rpg.Party.*;
import static sully.vc.simpletype_rpg.Data.*;
import static sully.vc.simpletype_rpg.Inventory.*;
import static sully.vc.v1_rpg.V1_RPG.*;
import static sully.vc.v1_rpg.V1_Maineffects.*;
import static sully.vc.v1_rpg.V1_Weather.*;
import static sully.vc.v1_rpg.V1_Textbox.*;
import static sully.vc.v1_menu.Menu_System.*;

import static sully.vc.simpletype_rpg.parser.Data_front.*;

import static sully.vc.Special_effects.Bouncy.*;
import static sully.vc.Special_effects.Funk_o_rama.*;

import java.awt.Color;

import core.VergeEngine;
import domain.VImage;

import sully.vc.Sfx;
import sully.vc.simpletype_rpg.Cast;
import sully.vc.util.Animation;
import sully.vc.util.Credits;
import sully.vc.v1_rpg.V1_RPG;
import sully.vc.v1_rpg.V1_Simpletype;

public class Sully extends VergeEngine {
	
	
	// Version number in 127.255.255.255 format, byte 4 ->1
	
	public static final int VERSION_4_BYTE = 0;
	public static final int VERSION_3_BYTE = 0;
	public static final int VERSION_2_BYTE = 1;
	public static final int VERSION_1_BYTE = 0;
	public static final String VERSION_STRING = "(0.0.1.0) - Sully savefile format rc1";
	
	
	// This file is where all the story flag defines live
	// Flags are awesome and important!
	// Open this file to learn more if you don't already know!
	//#include "flags.vc"
	
	
	
	// The master include file for all the vc libraries.  Open this up to
	// See where all of the vc system source is.
	//#include "vc/includes.vc"
	
	
	
	// This is a file that holds Targetting and effect functions 
	// that items.dat and skills.dat would ask for,
	//#include "effects.vc"
	
	
	// a global array of ints and one of Strings for use in functions,
	// since you can't have local arrays in mapfunctions.
	// Also useful scratch.  This data is by definition *not* saved... 
	// it's just a small playground of scrtach space!
	public static final int MAX_SCRATCH	= 100;
	
	public static int 	arTemp[] = new int[MAX_SCRATCH];
	public static String 	arTempStr[] = new String[MAX_SCRATCH];
	public static VImage 	arTempImg[] = new VImage[MAX_SCRATCH]; // RBP
	
	
	// Some various Story-related variables and effects. 
	//
	static String sfx_waves;	//used in the island.map flashback
	static int wavetimer;	//used in the island.map flashback
	
	//used in cottage.map Stan-intro scene.
	static int sparkle1, sparkle2, sparkle3;
	
	// The AutoExec Function: The first function that gets called.
	//  Everything starts right here when you start up a game.
	//
	public static void autoexec()
	{
		sparkle1 = Animation.LoadAnimation("res//images//story_fx//sparkle1.gif", 16, 32);
		sparkle2 = Animation.LoadAnimation("res//images//story_fx//sparkle2.gif", 16, 16);
		sparkle3 = Animation.LoadAnimation("res//images//story_fx//sparkle3.gif", 16, 16);
		
		//the following functions initialize things in the various libraries
		// that need initializing... images, sounds, whatever.
		V1RPG_LoadResources();	//loads resources for the v1rpg library.
		//SSAC_LoadResources();	//loads resources for the Sully: Simple and Clean library
		SSAC_InitData(); //loads datafiles.

		initInventories(); //cleans inventory
		SMENU_InitMenus(); //SMENU_LoadResources();	//loads resources for the Simple Menu library
		initSpeechPortsInData(); //OMG HAX!
		
		loadFunkOrama();	//loads the FUNK!
		
		MenuOff();	//let's not let anyone touch the menu until we're in-game, eh?
		
		//we load all the data first so we can do the introduction without pause.
	
		hookretrace("sully.vc.v1_rpg.V1_RPG.V1RPG_RenderFunc");// this is the v1_rpg library's default
						// Hookretrace function, defined in "vc/v1_rpg/v1_rpg.vc"
						//
						// It allows for the handly-dandy vclayer variable
						// (softcode, not a system feature) to be used, providing
						// a universal temporary area to draw to the screen with.
						//
						// If this confuses you, don't worry about it for now.
	
		
	
		//This allows alt-f4 to exit verge.
		//HookKey( SCAN_F4, "alt_f4" );
		
		initIntro();
		DoIntro();
	}
	
	void help()
	{
		int i;
		
		for( i=0; i<PartySize(); i++ )
		{
			master_cast[party[i]].cur_hp = 1;
			master_cast[party[i]].cur_mp = 1;
		}	
	}
	
	// Melds all the map-upkeep needs of the three libraries
	// this game uses.
	//
	public static void InitMap() {
	
		// from the Simpletype library... this makes
		// sure that your party is on the map!
		//
		// note: mapswitchx, mapswitchy are parts of the v1_rpg library
		//       set by v1_MpaSwitch()
		SpawnParty( mapswitchx, mapswitchy );
	
		// from the menu library, this makes sure that if you're in
		// menu-off mode, it stays off, or vica versa.
		if (_menu_on) MenuOn();
		else MenuOff();
	
		// from the v1_rpg effects library
		// this makes sure that the second half of any transition effect
		// started by v1_MapSwitch() is completed!
		V1_InitMap();
	}
	
	
	// The opening menu of the game!
	//
	//
	static void DoIntro()
	{
		//map("intro.map"); //rbp
		
		String sav_rString = ""; // RBP current_map.renderstring;
		
		ResetPreferences();
		
		//current_map.renderstring = "R";
	
		ClearVCLayer();	//part of the v1_rpg.vc lib.  Clears the vclayer.
		
		stopmusic();
		
		//calls the v3splash() function, if it exists.
		//callfunction("v3splash");	
		
		//play the unofficial VERGE theme song, Hymn to Aurora!
		playmusic( "res/music/AURORA.MOD" ); 
		setmusicvolume( 70 ) ;
		
		int delay = 40;
		int longdelay = 100;
		
		String _im = "res/images/story_fx/intro/retro.gif";
		VImage im = new VImage(load(_im ));
		
		
		if( sav_rString.equals(""))
		{
			rectfill( 0,0, imagewidth(screen), imageheight(screen), RGB(0,0,0), screen );
			setlucent(75);
			blit( 0,0, im, screen );
			Wait(delay);
	
			setlucent(0);
			rectfill( 0,0, imagewidth(screen), imageheight(screen), RGB(0,0,0), screen );
			setlucent(50);
			blit( 0,0, im, screen );
			Wait(delay);
	
			setlucent(0);
			rectfill( 0,0, imagewidth(screen), imageheight(screen), RGB(0,0,0), screen );
			setlucent(25);
			blit( 0,0, im, screen );
			Wait(delay);
	
			setlucent(0);
			rectfill( 0,0, imagewidth(screen), imageheight(screen), RGB(0,0,0), screen );
			blit( 0,0, im, screen );
			
			Wait(longdelay);	
	
			setlucent(0);
			rectfill( 0,0, imagewidth(screen), imageheight(screen), RGB(0,0,0), screen );
			setlucent(25);
			blit( 0,0, im, screen );
			Wait(delay);
	
			setlucent(0);
			rectfill( 0,0, imagewidth(screen), imageheight(screen), RGB(0,0,0), screen );
			setlucent(50);
			blit( 0,0, im, screen );
			Wait(delay);
	
			setlucent(0);
			rectfill( 0,0, imagewidth(screen), imageheight(screen), RGB(0,0,0), screen );
			setlucent(75);
			blit( 0,0, im, screen );
			Wait(delay);
	
			setlucent(0);
			rectfill( 0,0, imagewidth(screen), imageheight(screen), RGB(0,0,0), screen );
			Wait(delay);
		}
		else
		{
			setlucent(0);
			FillVCLayer( RGB(0,0,0) );
			Wait(delay);
		
			setlucent(75);
			VCPutIMG( _im, 0,0 );
			Wait(delay);
			
			setlucent(0);
			FillVCLayer( RGB(0,0,0) );
	
			setlucent(50);
			VCPutIMG( _im, 0,0 );
			Wait(delay);
			
			setlucent(0);
			FillVCLayer( RGB(0,0,0) );
	
			setlucent(25);
			VCPutIMG( _im, 0,0 );
			Wait(delay);
			
			setlucent(0);
			FillVCLayer( RGB(0,0,0) );
	
			setlucent(0);
			VCPutIMG( _im, 0,0 );
			Wait(longdelay);
			
			setlucent(0);
			FillVCLayer( RGB(0,0,0) );
			
			setlucent(25);
			VCPutIMG( _im, 0,0 );
			Wait(delay);
			
			setlucent(0);
			FillVCLayer( RGB(0,0,0) );
			
			setlucent(50);
			VCPutIMG( _im, 0,0 );
			Wait(delay);
			
			setlucent(0);
			FillVCLayer( RGB(0,0,0) );
			
			setlucent(75);
			VCPutIMG( _im, 0,0 );
			Wait(delay);
			
			setlucent(0);
			FillVCLayer( RGB(0,0,0) );
			
			Wait(delay);
		}
	
		//current_map.renderstring = sav_rString;
	
		//let's go to intro.map so we can play with entities!
		//
		// To follow along the execution of the program, open up intro.vc.
		map( "intro.map" );
	}
	
	
	// This is temporary.  The integration of the speech portraits and the
	// master_cast system is loose at best for now.  This initialization
	// keeps the data as-is synched so you can get a speech portrait based on
	// party position at the moment.
	//
	// Question to all: we should rethink the speech port system maybe?
	// -Grue
	static void initSpeechPortsInData() {
		int i;
		String name;
	
		for( i=0; i<MAX_CAST; i++ ) {
	
			name = master_cast[i].name;
	
			if( name.equals("darin")) {
				master_cast[i].portrait_idx = T_DARIN;
			} else if( name.equals("dexter") ) {
				master_cast[i].portrait_idx = T_DEXTER;
			} else if( name.equals("sara") ) {
				master_cast[i].portrait_idx = T_SARA;
			} else if( name.equals("crystal") ) {
				master_cast[i].portrait_idx = T_CRYSTAL;
			} else if( name.equals("galfrey") ) {
				master_cast[i].portrait_idx = T_GALFREY;
			}
		}
	}
	
	// returns a speech portrait index based on party position
	//
	// I placed it here because it's the only thing relying upon that
	// horribleness above. -Grue
	public static int SpcByPos( int party_pos )
	{
		if( party_pos < 0 || party_pos >= PartySize() )
		{
			System.err.println( "SpcByPos(): "+str(party_pos)+" is an invalid party index." );
			return 0-1;
		}
	
		return master_cast[party[party_pos]].portrait_idx;
	}
	
	
	
	//This allows alt-f4 to exit verge.
	/*void alt_f4()
	{
		if( key[SCAN_ALT] ) {
			exit( "" );
		}
	}*/
	
	void logGameFlags() {
	
		int i;
	
		for( i=0; i<256; i++ ) {
			log( "flags["+str(i)+"]: " + Flags.flags[i] );
		}
	
		log( "Done logging." );
	}
	
	// Quick workarounds to get loadgames into the main title screen.  
	public static int _title_menu;
	public static boolean _title_menu_load_done;
	public static boolean _title_menu_load_cancel; // rbp
	
	
	static void credits()
	{
		String sav_rString = current_map.renderstring;
	
		Credits credits = new Credits();
		
		current_map.renderstring = "R";	
		FillVCLayer( RGB(0,0,0) );
		
		int y=0;	
		credits.resetCredits();
	
		credits.addIntroLine( y,   "The Sully Chronicles, v3 Simple Edition." );
		y+=10;
	
		credits.addIntroLine( y,   "========================================" );
		y+=30;
	
		credits.addIntroLine( y,   "Original game by Brian 'Hahn' Peterson" );
		y+=30;
	
		credits.addIntroLine( y,   "v3 revision of the game by:" );
		y+=20;
	
		credits.addIntroLine( y,   "Ben 'McGrue' McGraw" );
		y+=20;
	
		credits.addIntroLine( y,  "Martin 'Zip' P." );
		y+=30;
	
		credits.addIntroLine( y,  "With additional code by:" );
		y+=20;
		credits.addIntroLine( y,  "Benjamin 'vecna' Eirich" );
		y+=20;
		credits.addIntroLine( y,  "Tristan 'RageCage' Michael" );
		y+=20;
		credits.addIntroLine( y,  "Shamus 'Kildorf' Peveril" );
	
		y+=50;
		credits.addIntroLine( y,  "Originally tile and sprite art by:" );
	
		y+=20;
		credits.addIntroLine( y,  "Brian 'Hahn' Peterson" );
	
		y+=30;
		credits.addIntroLine( y,  "Additional art by:" );
	
		y+=20;
		credits.addIntroLine( y,  "AJ 'Gayo' Joas" );
	
		y+=20;
		credits.addIntroLine( y,  "Ben 'McGrue' McGraw" );
	
		y+=50;
		credits.addIntroLine( y,  "Music" );
		y+=10;
		credits.addIntroLine( y,  "=====" );
		y+=25;
		credits.addIntroLine( y,  "Hymn to Aurora, Copyright Horace Wimp" );
		y+=25;
		credits.addIntroLine( y,  "Bad Experiences, Copyright 1994 Frog" );
		y+=25;
		credits.addIntroLine( y,  "D.O.S. tune #3, Copyright 1993 lotf interface" );
		y+=25;
		credits.addIntroLine( y,  "Exage, Copyright 1990 Peter J. Salomonsen" );
		y+=25;
		credits.addIntroLine( y,  "Medioeval, Copyright Fabio Barzagli" );
		y+=25;
		credits.addIntroLine( y,  "Vangelis Remix #02, Author Unknown" );
		y+=25;
		credits.addIntroLine( y,  "Song of Crying Guitar, Copyright 1995 Vadim VS" );
		y+=25;
		credits.addIntroLine( y,  "Soul-o-matic, Copyright 1993" );
		y+=10;
		credits.addIntroLine( y,   "by Purple Motion of Future Crew" );
		y+=25;
		credits.addIntroLine( y,  "Dreams of the Acropolis, Copyright E.J.James" );
		y+=25;
		credits.addIntroLine( y,  "Mystical Waters, Copyright 1993 H.T.H." );
		y+=25;
		credits.addIntroLine( y,  "Simplicity 2, Copyright 1995 Sirius of Bass Productions" );
		y+=25;
		credits.addIntroLine( y,  "Symphony, Copyright Skaven of Future Crew" );
		y+=25;
		credits.addIntroLine( y,  "Morning Call, Copyright Michiko Naruke" );
	
		y+=40;
		credits.addIntroLine( y,  "All music and sound effects are the property of" );
		y+=10;
		credits.addIntroLine( y,  "their respective owners and used here without" );
		y+=10;
		credits.addIntroLine( y,  "permission." );
	
		y+=40;
		credits.addIntroLine( y,  "Additional sounds by:" );
		y+=25;
		credits.addIntroLine( y,  "Matthew 'Zathras' Steele" );
	
		y+=50;
		credits.addIntroLine( y,   "========================================" );
		y+=10;
		credits.addIntroLine( y,   "VERGE 3 engine copyright 1997-2004 Ben Eirich" );
		y+=10;
		credits.addIntroLine( y,   "========================================" );
	
		y+=25;
		credits.addIntroLine( y,   "All source and custom graphics provided in this game" );
		y+=10;
		credits.addIntroLine( y,   "are released into the Public Domain." );
		y+=10;
		credits.addIntroLine( y,   "Use them as you see fit.");
	
		y+=25;
		credits.addIntroLine( y,   "========================================" );
	
		y+=25;
		credits.addIntroLine( y,   "Come visit us at: http://www.verge-rpg.com/" );
		y+=10;
		credits.addIntroLine( y,   "We hope you enjoyed the game, ");
		y+=10;
		credits.addIntroLine( y,   "and have fun making your own!" );
		y+=25;
		credits.addIntroLine( y,   "========================================" );
		y+=120;
		credits.addIntroLine( y,   "This game dedicated to the memory of" );
		y+=25;
		credits.addIntroLine( y,   "Chris 'Kao Megura' MacDonald" );
		y+=25;
		credits.addIntroLine( y,   "Driving force behind GameFAQs.com's growth," );
		y+=10;
		credits.addIntroLine( y,   "and early VERGEr." );
		
	
		y+=350;
		credits.addIntroLine( y, "" );
	
		FadeIn( 30 );
		Credits.doSimpleCredits( menu_font[0] );
		FadeOut(30);
		
		current_map.renderstring = sav_rString;
		
		//and... return to the very beginning of the game!
		DoIntro();
	}
	
	
	
	// Resets all of the various game preferences to their default state.
	//
	// Mainly for the use of the NewGame() function on the intro map.
	static void ResetPreferences()
	{
		global_gametime = 0;
	//	systemtime 		= 0;
		
	
		Sfx.global_music_volume = 85;
		Sfx.sfx_volume = 85;
		Sfx.interface_volume = 85;
		global_noscroll = false;
		global_menuluc = 2;
		
		menu_colour[0] = new Color(0, 0, 0);
		menu_colour[1] = new Color(112, 112, 112);
		menu_colour[2] = new Color(144, 144, 144);
		menu_colour[3] = new Color(0, 0, 255);
		
		ClearVCLayer();
		
		vc_quake_on = false;
		tint_on = false;
		vc_filter_on = false;
		vc_custom_filter_on = false;
		SetWeather(0);
	}
	
	
	static int _Ent_tmpvar;
	
	// Call this at the beginning of any entity event or zone-event.
	//
	// This makes sure that the entity you're talking to (if any) stops 
	// moving while you're talking to them, and turns off the ability to open your 
	// menu while the conversation is going on.  VERY USEFUL.
	public static void EntStart() {
		
		MenuOff();
		if(event_entity >= 0) {
			_Ent_tmpvar = entity.get(event_entity).speed;
			entity.get(event_entity).speed=0;
		}
		
		pauseplayerinput(); // rbp	
	}
	
	// Call this at the end of any function where you called EntStart().
	//
	// if you fail to do this, the entity (if any) will not start moving 
	// again, nor will the player be able to open his or her menu.
	//
	// In general, these are useful functions, but be careful in their use, lest 
	// you allow menus when you don't want them, or disallow menus when you do want
	// them.
	public static void EntFinish() {
		MenuOn();
		if(event_entity >= 0) {
			entity.get(event_entity).speed = _Ent_tmpvar;
		}
		unpauseplayerinput(); // rbp
	}

	// Rbp (These zone functions weren't in Sully class, so it is necessary to be defined here in order to be called by other maps)
	public static void Heal_Well() {
		V1_Simpletype.Heal_Well();
	}
	public static void SavePoint() {
		V1_Simpletype.SavePoint();
	}
	public static void SaveDisable() {
		V1_RPG.SaveDisable();
	}

	
	
	// The only new code required to run the JVerge engine
	public static void main(String args[]) {

		setSystemPath(new Sully().getClass());
		
		// Path, configfile and args
		initVergeEngine(args);
	}




}