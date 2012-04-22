package sully;

import static core.Script.*;
import static sully.vc.simpletype_rpg.Party.*;
import static sully.vc.simpletype_rpg.Data.*;
import static sully.vc.simpletype_rpg.Inventory.*;
import static sully.vc.simpletype_rpg.Equipment.*;
import static sully.vc.simpletype_rpg.Cast.*;
import sully.vc.util.Credits;
import sully.vc.v1_rpg.V1_Maineffects;

import static sully.vc.Special_effects.Bouncy.*;

import static sully.vc.v1_rpg.V1_RPG.*;
import static sully.vc.v1_rpg.V1_Maineffects.*;
import static sully.vc.v1_rpg.V1_Weather.*;
import static sully.vc.v1_rpg.V1_Textbox.*;

import static sully.vc.util.General.*;
import static sully.vc.util.Credits.*;

import static sully.Flags.F_COT_STAN;
import static sully.Flags.F_CRYS_JOIN;
import static sully.Flags.F_LAB_FOUND_CRYSTAL;
import static sully.Flags.F_MOUNT_DEX_JOIN;
import static sully.Flags.flags;
import static sully.Sully.*;
import static sully.vc.v1_menu.Menu_System.*;
import static sully.vc.v1_menu.Menu_Save.*;
import domain.VImage;


public class Intro {
	
	static boolean runningIntro = true; // RBP: Necessary to control the execution flow
	//
	//
	//
	
	static int darin;
	static int crystal;
	
	public static void startIntro()
	{
		MenuOff();
		cameratracking = 0;
		xwin = 10*16;
		
		darin	= entityspawn(29, 12, "res/chrs/darin.chr");
		crystal	= entityspawn(10, 12, "res/chrs/crystal.chr");
		
		entity.get(darin).face 		= FACE_LEFT;
		entity.get(crystal).face 	= FACE_RIGHT;
		
		V1_StartDualMode( "2,R,1,E,R" );
		hookretrace( "introrender" ); //hookrender the 
		
		arTempImg[0] = new VImage(load( "res/images/story_fx/intro/curtain.gif" ));
		arTempImg[1] = new VImage(load( "res/images/story_fx/intro/title-1.gif" ));
		arTempImg[2] = new VImage(load( "res/images/story_fx/intro/background-2.gif" ));
		arTempImg[3] =	new VImage(load( "res/images/story_fx/intro/castles.gif" ));
		arTempImg[4] = new VImage(load( "res/images/story_fx/intro/title-2.pcx" ));
	

		Thread thread = new Thread() {
			public void run() {
					try {
						doIntroAnimation();
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					} // RBP
					runningIntro = false;
			}
		};
		thread.start();
		
		while(runningIntro) {

		}
		System.out.println("Interrupted!");
		//thread.interrupt();
		thread.stop();
		//thread.interrupt();
		//try { thread.join(); } catch(Exception e) {}
		
		tblit(74, 17, arTempImg[4], v1_vclayer);
		
		Wait( 100 );
		
		DoMenu();
	}
	
	public static void doIntroAnimation() throws InterruptedException {
	
			int acc = 99;
			int vel = 98;
			int posy_100 = 97;
			int last_time = 96;	
			
			arTemp[acc] = 100;
			arTemp[posy_100] = (0-7700);
			arTemp[vel] = 1;
			arTemp[last_time] = 0;
			
			
			VCPutIMG( "res/images/story_fx/intro/background-2.gif",0,0 );
			VCPutIMG( "res/images/story_fx/intro/castles.gif",0,113 );
			
			VCLayerTintScreen(RGB(0,0,0), 0);
			
			DrawVC2(0,0,arTempImg[0]);
	
			FadeIn( 100 );
	
			Wait(75);
			
			entity.get(darin).speed = 50;
			entitymove(darin, "W20 L8 W10 R0 W10 L0 W10 R0 W10 L0");
			
			timer = 0;
			while( timer < imageheight(arTempImg[0]) )
			{
				render();
				DrawVC2(0,0-timer,arTempImg[0]);
				showpage();
			}
			
		
			rectfill( 0,0, imagewidth(v1_vclayer2), imageheight(v1_vclayer2), transcolor, v1_vclayer2 );
			
			current_map.renderstring = "2,R,1,E,R";
			
			//runny and jumpy!
			int darin_hit;
			timer = 0;
			while( timer < 160 )
			{
				render();
				
				entity.get(crystal).setx((10*16) + (_iCry[timer].x_100/100));
				entity.get(crystal).sety((12*16) + (_iCry[timer].y_100/100));
				entity.get(crystal).specframe = _iCry[timer].frame;
				
				if( timer > 94 && timer < 110 )
				{
					entitymove(darin, "");
					
					darin_hit = 1;
					
					if( entity.get(darin).face  == FACE_LEFT )
						entity.get(darin).specframe = 30;
					else
						entity.get(darin).specframe = 31;
				}
				
				if( timer >= 130 )
				{
					doDroppy();
				}
					
				showpage();
			}
	
			entity.get(crystal).speed = 80;
			entity.get(crystal).setx(28*16);
			entity.get(crystal).sety(12*16);
			entitymove(crystal, "R1 W20 L0 W30 L2 W40 R4");
			entitymove(darin, "Z0");
			//entitymove(Crystal, "");
			
			while( arTemp[vel]!=0 )
			{
				render();
				
				doDroppy();
				
				showpage();
			}
	
			timer=0;
			while( timer < 100 )
			{
				render();
				VCLayerTintScreen(RGB(0,0,0), timer);
				showpage();
			}
			
			VCLayerTintOff();
			tblit( 74, (arTemp[posy_100]/100), arTempImg[1], v1_vclayer );
		
			int stan		= entityspawn(0, 0, "res/chrs/stan.chr");
			int lil_stan	= entityspawn(0, 0, "res/chrs/lil_stan.chr");
			int sully		= entityspawn(0, 0, "res/chrs/sully.chr");
			
			entitymove( darin, "R0 W15 Z32 W15" );
			
			WaitForEntity( darin );
			
			entity.get(stan).setx(16*26);
			entity.get(stan).sety(16*(0-1));
			entity.get(stan).speed	= 150;
			entitymove( stan, "D13" );
			
			entity.get(sully).setx(16*13);
			entity.get(sully).sety(16*(0-1));
			entity.get(sully).speed	= 225;
			entitymove( sully, "D14" );
			
			WaitForEntity( sully );
			entity.get(sully).sety(entity.get(sully).gety()-9);
			
			WaitForEntity( stan );		
			
			entity.get(darin).specframe = 0;
			entity.get(darin).face 		= FACE_RIGHT;
			entity.get(darin).speed 	= 250;
			
			entitymove( darin, "W15 Z15 U2 D2 U2 D2 Z0" );
			WaitForEntity(darin);
		
			entity.get(darin).speed 	= 150;
			
			entitymove( darin, "L10 W15 R0 W20 R1" );
			WaitForEntity(darin);
			
			entitymove( sully, "R13" );
			entitymove( darin, "R2" );
			WaitForEntity(sully);
			
		
			entitymove( sully, "R13" );
			switchAndWait( stan, lil_stan, 8 );
			switchAndWait( stan, lil_stan, 8 );
			switchAndWait( stan, lil_stan, 8 );
			switchAndWait( stan, lil_stan, 8 );
			switchAndWait( stan, lil_stan, 8 );
			
			//entity.get(stan).visible = false; // RBP: To avoid strange behavior of Stan
			
			entitymove( lil_stan, "W10 L0 W10" );
			WaitForEntity( lil_stan );
			
		
			entity.get(lil_stan).speed	= 250;
			entitymove( lil_stan, "Z6 U2 D2 U2 D2 U2 D2 Z0" );
			WaitForEntity( lil_stan );
			entity.get(lil_stan).speed	= 200;
			entitymove( lil_stan, "L6" );
			WaitForEntity( lil_stan );
		
			DrawVC2( 0,240-32-95,arTempImg[3] );
			
			entitymove( lil_stan, 	"L15" );
			entitymove( darin, 		"L15" );
			
			WaitForEntity( lil_stan );
			
			tblit(74, (arTemp[posy_100]/100), arTempImg[1], v1_vclayer);
			
			timer = 0;
			while( timer < 100 )
			{
				render();
				
				setlucent( 100-timer );
				tblit(74, 17, arTempImg[4], screen);
				setlucent( 0 );
				
				showpage();
			}
	}
	
	
	public static void introrender()
	{
		V1RPG_RenderFunc_DUALMODE();
	
		if( b3 )
		{
			runningIntro = false;
			//DoMenu();
		}
	}
	
	// This takes care of drawing the entire screen properly
	// if the intro was cut short or watched through to the end, 
	// and also handles the main menu options.
	static void DoMenu()
	{
		// Cleanup everything that could've been changed during the intro....
		//
		V1_StopDualMode();
		
		current_map.renderstring = "2,R,1,E";	
		hookretrace("sully.vc.v1_rpg.V1_RPG.V1RPG_RenderFunc" );
		
		VCLayerTintOff();	//the tint could've been on during the intro, so turn it off.
		//SetEntitiesPaused(1);
		
		int i;	//Let's hide all of the entities.
		for( i=0; i<entity.size(); i++ )
		{
			entity.get(i).visible = false;
		}
		
		//cleanupIntro(); Not needed
		
		// Wipe the vc layer.
		//
		ClearVCLayer();
		
		// Reconstruct the vc layer as it should've been, plus add the menu.
		//
		VCPutIMG( "res/images/story_fx/intro/background-2.gif", 0, 0 );
		VCPutIMG( "res/images/story_fx/intro/castles.gif",0,113 );
		VCPutIMG( "res/images/story_fx/intro/title-1.gif",74, 44 );
		VCPutIMG( "res/images/story_fx/intro/title-2.pcx",74, 17 );
		VCPutIMG( "res/images/story_fx/intro/menu.pcx", 125, 160 );
		
		VImage ptr = new VImage(load( "res/images/story_fx/intro/pointer.pcx" ));
		
		int done, arrow = 0;
		
		Menu1ArrowSetSounds( "MenuHappyBeep" );
			
		while( !MenuConfirm() )
		{
			render();
			
			arrow = MenuControlArrows(arrow, 4);	
			
			tblit( 118, 161+(arrow*9), ptr, screen );
			
			showpage();
		}
		
	
		Menu1ArrowSetSounds( "" );
		
		if( arrow == 3 ) exit( "Thanks for trying out VERGE 3!"+chr(10)+chr(10)+"Visit us at http://www.verge-rpg.com/" );
		else if( arrow == 2 ) credits();
		else if( arrow == 1 ) newgame();
		else if( arrow == 0 ) {
			Menu1ArrowSetSounds( "MenuHappyBeep" );
			LoadGame();
			Menu1ArrowSetSounds( "" );
		}
	}
	
	static void LoadGame()
	{
		menu_idx = MenuGet("Save");
		menu_sub = MenuInitSave();
		menu_item = 0;	
		menu_option = 6; 
		
		_title_menu = 1;
		
		_title_menu_load_done = false;
		_title_menu_load_cancel = false; // rbp
		
		while( !_title_menu_load_done && !_title_menu_load_cancel)
		{
			MenuBackGroundDraw(); //draw universal things
			callfunction( "sully.vc.v1_menu.Menu_Save.MenuDrawSave" );
	
			showpage();
			callfunction( "sully.vc.v1_menu.Menu_Save.MenuControlSave" );
		}
		_title_menu = 0;
		if(_title_menu_load_done) { // RBP (because map doesn't interrupt execution anymore)
			MenuOn();
			return;
		}
		DoMenu();
	}
	
	
	// helper function: draws an image to vc layer 2
	static void DrawVC2( int x, int y, VImage im )
	{
		rectfill( 0,0, imagewidth(v1_vclayer2), imageheight(v1_vclayer2), transcolor, v1_vclayer2 );
		//v1_vclayer2.g.clearRect(0,0, imagewidth(v1_vclayer2), imageheight(v1_vclayer2));
		tblit(x, y, im, v1_vclayer2);
	}
	
	// a helper function to clean up the temporary images.
	/*void cleanupIntro() RBP: Not needed 
	{
		int i;
		
		for( i=0; i<5; i++ )
		{
			if( arTemp[i] )
			{
				freeImage( arTemp[i] );	
				arTemp[i] = 0;	
			}
		}
	}*/ 
	
	
	
	// This function starts it all!  Oh boy!
	//
	//
	static void newgame() {
		
		FadeOut(30);
	
		//reset everything! :o
		Flags.ClearFlags();
		ClearInventory();
		ClearCast();
		
		ResetPreferences();
		
		//kill the party if anyone's in there.
		curpartysize = 0;
		
		//let's give everyone their initial equipment....
		ForceEquip( "Darin", "Dagger", SLOT_RHAND );
		ForceEquip( "Darin", "Headband", SLOT_ACC1 );
		ForceEquip( "Darin", "Garment", SLOT_BODY );
	
		ForceEquip( "Sara", "Brass_Pipe", SLOT_RHAND );
		ForceEquip( "Sara", "LongCap", SLOT_ACC1 );
		ForceEquip( "Sara", "Bracer", SLOT_ACC2 );
		ForceEquip( "Sara", "Garment", SLOT_BODY );
	
		ForceEquip( "Dexter", "Dagger",  SLOT_RHAND );
		ForceEquip( "Dexter", "Headband", SLOT_ACC1 );
		ForceEquip( "Dexter", "Garment",  SLOT_BODY );
	
		ForceEquip( "Crystal", "Staff",  SLOT_RHAND );
		ForceEquip( "Crystal", "Garment", SLOT_BODY );
	
		ForceEquip("Galfrey","Spear",SLOT_RHAND);
		ForceEquip("Galfrey","Gold_Helmet",SLOT_ACC1);
		ForceEquip("Galfrey","Leather_Vest",SLOT_BODY);
		ForceEquip("Galfrey","Buckler",SLOT_ACC2);
	
	
		//we need to have a party member to start off with, or else the partyhandling 
		// system cries.  Let's make it Darin! :D
		JoinParty( "Darin", 1 );
		
		FillVCLayer( RGB(0,0,0) );
	
		// Let's put up a pretty picture and then scroll some pretty 
		// text in front of it!
		VCPutIMG("res/images/cells/island.gif",80,50);
		
		current_map.renderstring = "R";
		Credits c = new Credits();
		c.resetCredits();
	
		int y = 0;	
	
		c.addIntroLine( y,   "Our world is but one in the universe." );	y+=25;
		c.addIntroLine( y,  "The imagination is vast and limitless." );	y+=25;
		c.addIntroLine( y,  "An astral force known as VERGE allows" );	y+=25;
		c.addIntroLine( y,  "us to shape our dreams into reality." );		y+=25;
		c.addIntroLine( y, "Darin's is but one small world in the" );		y+=25;
		c.addIntroLine( y, "endless reaches of space. Darin sailed" );	y+=25;
		c.addIntroLine( y, "from his home in search of a new world," );	y+=25;
		c.addIntroLine( y, "but alas, he found each of the worlds" );		y+=25;
		c.addIntroLine( y, "still undergoing construction. Sad, yet" );	y+=25;
		c.addIntroLine( y, "hopeful, Darin returns to spend time" );		y+=25;
		c.addIntroLine( y, "in peace with his eternal soulmate..." );		y+=240;
		c.addIntroLine( y, "" );
	
		FadeIn(30);
		
		doSimpleCredits( menu_font[0] );
		
		FadeOut(30);
		ClearVCLayer();
	
		VCCenterText(82,  "- VERGE -" );
		VCCenterText(110, "The Sully Chronicles" );
	
		FadeIn( 120 );
		Wait( 300 );
		FadeOut( 120 );
		ClearVCLayer();
		
	
		MenuOn();
		cameratracking = 1;
	
		V1_MapSwitch( "Island.map",3,5, TBLACK );
	}
	
	
	
	
	static void switchAndWait( int e1, int e2, int delay )
	{
		int tx, ty;
		
		tx = entity.get(e1).getx();
		ty = entity.get(e1).gety();
		
		entity.get(e1).setx(entity.get(e2).getx());
		entity.get(e1).sety(entity.get(e2).gety());
		
		entity.get(e2).setx(tx);
		entity.get(e2).sety(ty);
		
		Wait(delay);
	}
	
	
	
	static int doDroppy()
	{
		int acc = 99;
		int vel = 98;
		int posy_100 = 97;
		int last_time = 96;
		int bouncecount = 95;
		
		if( systemtime > (arTemp[last_time]+2) )
		{
			arTemp[last_time] = systemtime;
			
			arTemp[posy_100] += arTemp[vel];
			arTemp[vel] += arTemp[acc];
			
			if( arTemp[posy_100] > 4400 )
			{
				arTemp[posy_100] = 4400;
				
				if( arTemp[bouncecount] < 3 )
				{
				
					arTemp[vel] = arTemp[vel] /2;
					arTemp[vel] = arTemp[vel] * (0-1);
					arTemp[bouncecount]++;
				}
				else
				{
					arTemp[vel] = 0;
				}
			}
		}
		
		DrawVC2( 74, (arTemp[posy_100]/100), arTempImg[1] );
		
		return arTemp[vel];
	}
}