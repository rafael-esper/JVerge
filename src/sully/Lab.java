package sully;

import static core.Script.*;
import static sully.Flags.*;
import static sully.Sully.*;

import java.awt.Color;

import domain.VImage;

import static sully.vc.v1_rpg.V1_RPG.*;
import static sully.vc.v1_rpg.V1_Music.*;

import static sully.vc.simpletype_rpg.Party.*;
import static sully.vc.simpletype_rpg.Cast.*;
import static sully.vc.simpletype_rpg.Inventory.*;
import static sully.vc.v1_rpg.V1_Weather.*;
import static sully.vc.v1_rpg.V1_Maineffects.*;
import static sully.vc.v1_rpg.V1_Textbox.*;
import static sully.vc.v1_menu.Menu_System.*;
import static sully.vc.v1_menu.Menu_Shop.*;
import static sully.vc.Sfx.*;
import static sully.vc.util.General.*;
import static sully.vc.util.Camscroll.*;
import static sully.vc.v1_rpg.V1_Simpletype.*;

public class Lab {
	
	
	public static void Start()
	{
		Sully.SaveDisable();
		
		SetWeather(WEATHER_FOG);
		
		if( flags[F_BUM_FLASHBACK] == 1 ) 
		{
			
			dramatic_optional_cutscene(); //do the DARAMTIC OPTIONAL CUTSCNE!
			
			flags[F_BUM_FLASHBACK] = 2; //increase it to 2 so this cutscene doesn't get
										//triggered ever again!
										
			V1_MapSwitch("BUMVILLE.MAP",3,98,0); //To bumsville!
		}
		
		//sets all the tiles as they should be.
		upkeepery();
		
		//if we got this far, move all the on-map entities off the map
		entity.get(0).sety(30000); // map-stan
		entity.get(1).sety(30000); // map-galfrey
		entity.get(2).sety(30000); // map-crystal 
		InitMap();
		V1_StartMusic("res/music/NONEXIST.MOD");
		
		Banner("High-Performance Switch-Testing Labs",300);
	}
	
	public static void dramatic_optional_cutscene() 
	{
		int crystal, galfrey, stan, i;
		
		//we're using the vclayer for the mist effects on this map, so we need to turn on 
		// vclayer2 for some drawing above the map!
		// 
		// we only use vclayer2 near the end of this scene, but we're starting it now
		// so there aren't any visual artifacts later.
		V1_StartDualMode( current_map.renderstring+",R" );
		
		stan = 0;		//stan is entity #0 on this map.
		galfrey = 1;	//galfrey is entity #1 on this map.
		crystal = 2;	//crystal is entity #2 on this map.
		
		settile(18,91,0,226);
		settile(18,92,0,226); //change some tiles...
		
		entity.get(crystal).setx(18*16);
		entity.get(crystal).sety(92*16);
		
		entity.get(stan).setx(18*16);
		entity.get(stan).sety(87*16);	
		entity.get(stan).face = FACE_DOWN;
		entity.get(stan).specframe = 5;
			
		setplayer( stan );
		setplayer( crystal );
		
		entity.get(playerent).face = FACE_UP;
	
		settile(18,87,0,226);
	    
		cameratracking = 1; //track the 
	
		ClearVCLayer();
	
		setmusicvolume(0);
	    playmusic("res/music/BADEXPER.MOD");
	    FadeInWSound();
	
		TextBox(T_CRYSTAL,	"Lord Stan, you monster!",
							"You shall never get away with this!", "");
	
		TextBoxM(T_STAN,	"Nya ha ha! Quiet, you impudent girl!",
							"You are the key to my plan of",
							"global domination!");
		TextBoxM(T_STAN,	"I shall put you into stasis and use ",
							"this machine to extract your DNA.","");
		TextBoxM(T_STAN,	"You come from a family line of great",
							"warriors.",
							"I will be able to clone them!");
		TextBox(T_STAN,	"They will bend to my fiendish will, and",
						"this entire world will belong to me! ",
						"Mwa ha ha!");
		
		entity.get(playerent).face = FACE_DOWN;
	
		TextBox(T_CRYSTAL,	"Galfrey, how can you help him with a",
			  				"mad plan like this?","");
		TextBox(T_GALFREY,	"Silence, wench!",
							"I have nothing to say to you,",
							"or that maggot boyfriend of yours.");
		TextBox(T_CRYSTAL,	"But I... wait a minute... you look familiar...",
							"...",
							"Oh my! You're that one guy from school!");
		TextBox(T_GALFREY,	"Yes. I asked you out to our senior prom,",
			  				"but *Darin* had already asked you.",
			  				"Grr...");
		TextBox(T_CRYSTAL,	"You were a nice guy and all, but...",
			  				"...geez, get over it!",
			  				"Why are you helping Lord Stan?");
		Wait(100);
		
		entity.get(galfrey).face = FACE_DOWN;
		
		Wait(100);
		
	  	entity.get(galfrey).specframe=25;
		
		TextBox(T_GALFREY,	"You cannot know how much pain you",
							"caused me that night.",
							"I had to seek some recourse.");
		
		entity.get(crystal).specframe=23;
		Wait(100);
		entity.get(crystal).specframe=0;
	
		TextBox(T_CRYSTAL,	"Do you really think this whole",
							"'dark servant' bit is impressing me?",
							"Get a clue.");
	
		Wait(100);
		entity.get(galfrey).specframe = 0;
		entity.get(galfrey).face = FACE_UP;
		
		TextBox(T_GALFREY,	"I got all buffed up for you, Crystal!",
							"We were going to have a life together!","");
	          
		TextBox(T_CRYSTAL,	"Umm... hello?",
							"Did I even know you back then?",
							"Just try and get your life back together.");
		Wait(100);
		entity.get(galfrey).face = FACE_DOWN;
		Wait(100);
		entity.get(galfrey).specframe=29;
	
		TextBox(T_GALFREY,	"I'm afraid it's too late for me,",
							"dear Crystal.",
							"My path lies with darkness now.");
		TextBoxM(T_STAN,	"Are you two done making kissy face yet?",
							"I'm almost ready to initiate the process.","");
		TextBoxM(T_STAN,	"The water I've been draining from the ",
							"earth and sky is collecting underground.", "");
		TextBoxM(T_STAN,	"I shall boil the lands, and lava shall",
							"devastate every human civilization!", "");
		TextBoxM(T_STAN,	"My invincible soldiers from Crystal's",
							"blood will build a new utopia for me!", "");
		TextBox(T_STAN,		"Galfrey! We'll need that",
							"[Thermal Activator] to wake her up.",
							"Where is it?");
		
		entity.get(galfrey).specframe=10;
		Wait(50);
		entity.get(galfrey).specframe=0;
		entity.get(galfrey).face = FACE_UP;
	
		TextBox(T_GALFREY,	"I had to sell it in Rodne, remember?",
							"Evil empires don't fund themselves,",
							"you know.");
		TextBox(T_STAN,	"Blast it, you dunderhead!",
						"I guess we'll just worry about that when",
						"we get there.");
		
		TextBox(T_GALFREY,	"Speaking of which, how does that new",
							"emergency exit system work again?", "");
		TextBoxM(T_STAN,	"I told you already!",
							"You have to flip the levers in ",
							"sequence from left to right.");
		TextBoxM(T_STAN,	"If you flip the correct lever",
							"in a row, then you will be able to",
							"go to the next row.");
		TextBox(T_STAN,		"If you flip the wrong lever,",
							"you'll need to go down the catwalk to",
							"reset them.");
	
		entity.get(crystal).specframe = 0;
		entity.get(crystal).face = FACE_UP;
	  
		TextBox(T_CRYSTAL,	"Darin will come for me, Lord Stan!",
							"Your days are numbered.","");
							
		TextBox(T_STAN,		"Well, then perhaps you can dream about",
							"him during your long slumber.",
							"Nightie night!");
	
		timer=0;
		
		while( timer <= 100 ) {
			render();
			setmusicvolume(100 - (timer*100/100));
			showpage();
		}
	
		entity.get(crystal).specframe = 21;
	 
	 	//the sparklies!
		SoundHealingWell();
		LabPutIMG("res/images/story_fx/SPARKLE7.PCX",GetEntScrX(crystal),GetEntScrY(crystal));
		Wait(20);
		rectfill(0, 0, imagewidth(v1_vclayer2), imageheight(v1_vclayer2), transcolor, v1_vclayer2); //clear vclayer2
		LabPutIMG("res/images/story_fx/SPARKLE8.PCX",GetEntScrX(crystal),GetEntScrY(crystal));
		Wait(20);
		rectfill(0, 0, imagewidth(v1_vclayer2), imageheight(v1_vclayer2), transcolor, v1_vclayer2); //clear vclayer2
		LabPutIMG("res/images/story_fx/SPARKLE7.PCX",GetEntScrX(crystal),GetEntScrY(crystal));
		Wait(20);
		rectfill(0, 0, imagewidth(v1_vclayer2), imageheight(v1_vclayer2), transcolor, v1_vclayer2); //clear vclayer2
		LabPutIMG("res/images/story_fx/SPARKLE8.PCX",GetEntScrX(crystal),GetEntScrY(crystal));
		Wait(20);
		rectfill(0, 0, imagewidth(v1_vclayer2), imageheight(v1_vclayer2), transcolor, v1_vclayer2); //clear vclayer2
	  
		entity.get(crystal).specframe = 25;
	
		TextBox(T_CRYSTAL,	"Ahh!",
							"Wha... what are you doing to me...?",
							"I feel so cold...");
	  	SoundHealingWell();
	  	entity.get(crystal).specframe = 24;
	
		FadeToColor( RGB(0,255,255), 100 );
	
		SoundIceShing();
		
		cameratracking = 0;
		entity.get(crystal).setx(0);
		entity.get(crystal).sety(0);
	
		settile(18,91, 0, 285);
		settile(18,92, 0, 287);
		
		//let's paint the top vc layer so it's colored the same while we wait a second...
		rectfill(0, 0, imagewidth(v1_vclayer2), imageheight(v1_vclayer2), RGB(0,255,255), v1_vclayer2);
		Wait(100);
		
		//...and then clear vclayer2 so we can see the fade.
		rectfill(0, 0, imagewidth(v1_vclayer2), imageheight(v1_vclayer2), transcolor, v1_vclayer2); 
	
		FadeFromColor( RGB(0,255,255), 100 );
		
		Wait(400);
	  
		FadeOut(60);
		
		V1_StopDualMode();
		
		// This is the end of the longass cutscene!
		//
		// Let's restore the party without touching their levels
		//only ad Dex if he's actually in the party!	
		if( flags[F_MOUNT_DEX_JOIN]!=0) 
		{
			AddPlayer( "Dexter" );
		}
	}
	
	
	
	public static void Nexit()
	{
		if( flags[F_LAB_COUNTDOWN]!=0 )
		{
			MenuOn();
			V1_StopDualMode();
			VCQuakeOff();
			hookretrace( "sully.vc.v1_rpg.V1_RPG.V1RPG_RenderFunc" );
		}
			
		V1_MapSwitch("mountain.map", 22, 6, TBLACK);
	}
	
	public static void switch_a()
	{
		flags[F_LAB_SWITCHCOUNT]++;
		Master3VertSwitch(F_LAB_SWITCH_A,14,5,10,7);
	}
	
	public static void switch_b()
	{
		int a, b = 0, c = 0;
		
		flags[F_LAB_SWITCHCOUNT]++;
		
		flags[F_LAB_SWITCH_B]++;
	
		if(flags[F_LAB_SWITCH_B] == 8)
		{
			flags[F_LAB_SWITCH_B] = 0;
		}
		
		for(a=0; a<=4; a++)
		{
			AlterBTile(18+a,17,532,1);
		}
	 
	
		//as far as I can tell, flags[F_LAB_4000] is NEVER EVER SET!
		//Hahn, what is this madness?! 
		//   -Grue
		if(flags[F_LAB_4000]==0) {  b=flags[F_LAB_SWITCH_B]; }
	 
	
		if( b==5 ) {  b=3; }
		if( b==6 ) {  b=2; }
		if( b==7 ) {  b=1; }
		AlterBTile(18+b,17,226,0);
	 
		//The Switch position
		if( b==0 ) {  c=281; }
		if( b==1 ) {  c=462; }
		if( b==2 ) {  c=464; }
		if( b==3 ) {  c=463; }
		if( b==4 ) {  c=284; }
	 
		AlterBTile(23,11,c,2);
	
		SoundSwitch();
		unpress( 0 );
	}
	
	public static void switch_c() /* 4 */
	{
		flags[F_LAB_SWITCHCOUNT]++;
		Master3VertSwitch( F_LAB_SWITCH_C ,24,9,24,13);
	}
	
	public static void switch_d() /* 5 */
	{
		flags[F_LAB_SWITCHCOUNT]++;
		Master3VertSwitch( F_LAB_SWITCH_D ,23,17,32,17);
	}
	
	public static void switch_e() /* 6 */
	{
		flags[F_LAB_SWITCHCOUNT]++;
		Master3HorzSwitch( F_LAB_SWITCH_E ,28,24,31,24);
	}
	
	public static void downstair_a() /* 7 */
	{
		Warp(42,44,TCROSS);
		Banner("B1",300);
	}
	
	public static void downstair_b() /* 8 */
	{
		Warp(58,44,TCROSS);
		Banner("B1",300);
	}
	
	public static void upstair_a() /* 9 */
	{
		Warp(14,12,TCROSS);
	}
	
	public static void upstair_b() /* 10 */
	{
		Warp(30,12,TCROSS);
	}
	
	public static void UNKNOWN_11() /* 11 */
	{
		Warp(4,15,TCROSS);
	}
	
	public static void downstair_c() /* 12 */
	{
		Warp(18,108,TCROSS);
		Banner("Thermal Chamber",300);
	}
	
	public static void upstair_c() /* 13 */
	{
		Warp(20,54,TCROSS);
	}
	
	public static void upstair_d() /* 14 */
	{
		Warp(1,6,TCROSS);
	}
	
	public static void downstair_d() /* 15 */
	{
		Warp(5,70,TCROSS);
	}
	
	public static void chest_a() /* 16 */
	{
		if( OpenTreasure(CHEST_LAB_A, 14,75, 280) )
		{
			FindItem( "Starlight", 1 );
		}
	
	}
	
	public static void chest_b() /* 17 */
	{
		if( OpenTreasure(CHEST_LAB_B, 22,75, 280) )
		{
			FindItem( "Bronze_Armor", 1 );
		}
	}
	
	public static void chest_c() /* 18 */
	{
		if( OpenTreasure(CHEST_LAB_C, 14,80, 280) )
		{
			FindItem( "Laser_Shield", 1 );
		}
	}
	
	public static void chest_d() /* 19 */
	{
		if( OpenTreasure(CHEST_LAB_D, 22,80, 280) )
		{
			FindItem( "Silver_Brace", 1 );
		}
	}
	
	public static void machine() /* 20 */
	{
		int a, crystal;
		
		EntStart(); //rbp MenuOff();
		
		crystal=2;  //she's 2 on this map.
		
	 	// If we don't have Sara....
	
	 	//
		if(flags[F_LOVE_SARA_JOIN]==0)
		{
			TextBox(T_DEXTER,	"Darin, I know more about magic than I",
								"do about machines.",
								"We shouldn't try to operate this.");
			EntFinish();
			return;
		}
	
		//if we haven't used the machine, and we don't have the activator,
		//  but we have Sara...
		if( !HasItem("Thermal_Activator") 
		    && CharInParty( "Sara" ) 
		    && flags[F_LAB_USED_MACHINE]==0 )
		{
			TextBox(T_SARA,	"Darin, let's go to my home in Rodne.",
							"There's a [Thermal Activator] in the",
							"basement.");
			EntFinish();
			return;
		}
		
		// If we've gotten this far, and we haven't used the machine, 
		//   it's time to use the machine!
		if(flags[F_LAB_USED_MACHINE]==0)
		{
		
			TextBox(T_SARA,	"Here are controls for the device.",
							"I can insert the [Thermal Activator]", 
							"here.");
			
			
			SoundDoubleClick();
			Banner( "Used Thermal Activator!", 500 );
			DestroyItem( "Thermal_Activator" );
		
			EntStart(); // rbp
			TextBox(T_DARIN,	"Sara, will this work?",
								"It will just melt the crystal and Crystal",
								"will be OK, right?");
								
			TextBox(T_SARA,	"Umm... huh?",
							"Oh yeah, sure.",
							"Let me just initialize the machine... THERE!");
			SoundQuake();
	
			//16 iterations of 20/100ths of a second in decreasing shakes.
			for(a=16; a>=0; a--) 
			{
				Earthquake(a,0,20);
			}
	
			// Change a bunch of tiles!
			//
			AlterBTile(12,92,289,2);
			AlterBTile(13,92,289,2);
			AlterBTile(14,92,289,2);
			AlterBTile(15,92,289,2);
			AlterBTile(16,92,289,2);
			AlterBTile(17,92,289,2);
			AlterBTile(19,92,293,2);
			AlterBTile(20,92,293,2);
			AlterBTile(21,92,293,2);
			AlterBTile(22,92,293,2);
			AlterBTile(23,92,293,2);
			AlterBTile(24,92,293,2);
			AlterBTile(18,94,296,2);
			AlterBTile(18,95,296,2);
			AlterBTile(18,96,296,2);
			
			AlterBTile(18,100,0,1);
			AlterBTile(18,101,0,1);
			AlterBTile(18,102,0,1);
	
			TextBox(T_SARA,	"A ha! It worked.",
							"I knew I wasn't a fool for taking shop",
							"instead of Home Economics!");
	
	
			TextBox(T_SARA,"Umm... oops.","","");
			TextBox(T_DARIN,"Oops?!","","");
	
			TextBoxM(T_SARA,	"It's no problem!",
								"I just accidentally cut a minor plasma",
								"conduit cable.");
			TextBox(T_SARA,	"It's surely nothing important like the",
							"self-destruct control or anything.", "");
			TextBox(T_DARIN,"But look, Sara! The commotion created",
							"by the machine made the walkway collapse!", "");
			TextBox(T_SARA,	"It's ok, Darin.",
							"We'll just find some other way out.",
							"RPG heroes always find a way.");
	
			//we're gonna scroll the camera centered on crystal.  
			//She's at tile coordinate [18,92] on the map, so to get her absolute x position
			// we multiply 18 by 16 to get the topleft corner of her tile and then add 8 to get the center of her tile.
			camCtrScrollToS((18*16)+8, (92*16), 150);
	
			//tint the screen Cyan!
			FadeToColor( RGB(255,255,0) ,100 );
	
			//move crystal over her crystal!
			entity.get(crystal).setx(18*16);
			entity.get(crystal).sety(92*16);
	
			//remove Crystal's crystal, leaving only Crystal!
			AlterBTile(18,91,226,0);
			AlterBTile(18,92,226,0);
	
			//show her in the eyes-closed frame.
			entity.get(crystal).specframe = 24;
	
			//fade back from cyan	
			FadeFromColor( RGB(255,255,0) ,100 );
	
			//move the camera back to darin
			camReturnToPlayerS(playerent, 100);
	
			TextBox(T_DARIN,	"Look! The machine melted the crystal",
								"around Crystal!",
								"Thank you so much, Sara!");
	
			//flag this event, yo.  We're done here!
			flags[F_LAB_USED_MACHINE]=1;
		}
		
		EntFinish(); //rbp MenuOn();
	}
	
	public static void crystal_awakens() /* 21 */
	{
		int a, b, c;
		int sara;
		int darin;
		int crystal = 2; //she's #2 on this map.

		EntStart(); //rbp MenuOff();
		
		
		//if we've done this event already, stop!
		if(flags[F_LAB_SAVE_CRYSTAL]!=0)
		{
			EntFinish(); //rbp MenuOn();
			return;
		}
		
		//if we haven't done this event yet, 
		//and we've freed Crystal from her crystal
		// then do the following scene...
		if(flags[F_LAB_USED_MACHINE]!=0)
		{
			sara 	= GetPartyEntity( "Sara" );
			darin	= GetPartyEntity( "Darin" );
			
			//open her eyes
			entity.get(crystal).specframe=25;
			Wait(300);
			entity.get(crystal).specframe=0;
			Wait(100);
			
			TextBox(T_CRYSTAL,	"Whu... am I awake?",
								"Darin, is that you?","");
			
			//blinking
			entity.get(crystal).specframe=24;
			Wait(30);
			entity.get(crystal).specframe=0;
			Wait(30);
			entity.get(crystal).specframe=24;
			Wait(30);
			entity.get(crystal).specframe=0;
			
			
			TextBox(T_DARIN,	"Crystal!",
								"I'm so glad you're awake.",
								"I was worried about you!");
			
			//more blinking
			entity.get(crystal).specframe=24;
			Wait(30);
			entity.get(crystal).specframe=0;
			Wait(30);
			entity.get(crystal).specframe=24;
			Wait(30);
			entity.get(crystal).specframe=0;
			Wait(100);
			entity.get(crystal).specframe=23;
			Wait(100);
			entity.get(crystal).specframe=22;
			
			
			TextBox(T_CRYSTAL,	"My vision is coming back...",
								"I see you... and... and...",
								"...another woman?! Grr!");
			AutoOn();
			
			// FIXME SEE IF THIS IS RIGHT
			//       AND IF SO MAKE IT PRETTIER
			a=darin;
			b=sara;
	
			entitymove(sara,"X18");
			
			WaitForEntity(sara);
			
			entitymove(crystal,"U1");
			entitymove(sara,"U2");
	
			while(entity.get(sara).movecode!=0)
			{
				Wait(5);
				entity.get(crystal).face = FACE_DOWN;
			}
			
			entitymove(crystal,"L2");
			entitymove(sara,"R2");
			
			while(entity.get(sara).movecode!=0)
			{
				Wait(5);
				
				entity.get(crystal).face = FACE_RIGHT;
				entity.get(sara).face = FACE_LEFT;
			}
			
			Wait(20);
			entity.get(sara).specframe = 26;
			
			TextBox(T_SARA,	"Yo, take it easy, sister.",
							"If it weren't for me, you'd be stuck in",
							"that rock forever!");
							
			entity.get(crystal).specframe = 27;
			
			TextBox(T_CRYSTAL,	"Alright, listen up you little wench.",
								"Darin is ALL mine, you got that?",
								"You leave him alone.");
			
			TextBox(T_SARA,	"Bitch!","","");
	
			entity.get(crystal).speed 	= 200; //doublespeed!
			entity.get(sara).speed		= 200; //doublespeed!
	
			entity.get(darin).specframe = 21;
	
			entity.get(crystal).specframe = 21;
			SoundShing();
			
			entitymove(crystal,"R2 L2");
			entitymove(sara,"L2 R2");
			
			while(entity.get(sara).movecode !=0)
			{
				Wait(20);
			}
			
			TextBox(T_CRYSTAL,	"Slut!","","");
			SoundShing();
			
			entitymove(crystal,"R2 L2");
			entitymove(sara,"L2 R2");
			WaitForEntity(sara);
			
			
			TextBox(T_SARA,	"Whore!","","");
			SoundShing();
			
			entitymove(crystal,"R2 L2");
			entitymove(sara,"L2 R2");
			WaitForEntity(sara);
	
			
			TextBox(T_CRYSTAL,	"Tramp!","","");
			SoundShing();
			
			entitymove(crystal,"R2 L2");
			entitymove(sara,"L2 R2");
			
			Wait(100);
			entity.get(sara).face=FACE_DOWN;
			entity.get(sara).specframe=0;
			entity.get(darin).face=FACE_UP;
			entity.get(darin).specframe=0;
			
			TextBox(T_SARA,	"Darin, are you sure we melted the right",
							"crystal?","");
			
			entitymove(darin,"X18 U2");
			WaitForEntity(darin);
				
			camReturnToPlayer(darin, 100);
			
			entitymove(a,"F3 W60 F2 W60 F2");
			WaitForEntity(darin);
			
			TextBox(1,	"Girls, girls, please.",
						"We have to work together now.",
						"Try to get along!");
						
			entity.get(crystal).face = FACE_DOWN;
			
			entity.get(crystal).specframe=0;
			Wait(50);
			entity.get(crystal).specframe=25;
			Wait(100);
			entity.get(crystal).specframe=27;
			
			TextBox(T_CRYSTAL,	"I suppose you're right... but you better",
								"sleep with one eye open, you hussy!", "");
								
			entity.get(crystal).specframe=0;
			
			TextBox(T_DARIN,	"Now it is time for us to go to Castle Heck",
								"and defeat Lord Stan once and for all!", "");
			
			entity.get(crystal).face=FACE_RIGHT;
			
			TextBox(T_CRYSTAL,	"Right.",
								"It's time we fight for truth and justice",
								"in this world! Count me in!");
			
			entity.get(b).face = FACE_LEFT;
			
			TextBox(T_SARA,	"No enemy can stand in the face of me!",
							"I shall fight to my dying breath!", "");
							
			TextBox(T_DARIN,"Let's bravely march forth!",
							"Come, troops! Our destination:",
							"the dreaded Castle Heck.");
							
			entity.get(crystal).speed 	= 50; //halfspeed
			entity.get(sara).speed 		= 50; //halfspeed
			
			entitymove(crystal,"R2");
			entitymove(sara,"L2");
			WaitForEntity(sara);
			
			EntFinish(); //rbp MenuOn();
			
			AddPlayer( "Crystal" );		
			entity.get(crystal).visible  = false; //Ditch the mapent-crystal		
			
			GiveXP( "Crystal" ,92 );
			FullHeal( IsCharacter("Crystal") ); //fully heals crystal on her rejoin.
	
			AutoOff();
			
			entity.get(sara).speed 		= 100; //
			
			flags[F_LAB_SAVE_CRYSTAL]=1;
			
			return;
		}
		
		TextBox( T_DARIN, "Crystal!  Hold on!", "", "" );
		
		if( CharInParty("Dexter") )
		{
			TextBox( T_DEXTER, 	"I don't think she's going anywhere.", 
								"Let's go to Rodne to get the", 
								"[Thermal Activator] to free her!" );
		}
		
		if( CharInParty("Sara") )
		{
			if( HasItem("Thermal_Activator") )
			{
				TextBox( T_SARA, 	"We can free her with the",
									"[Thermal Activator]!", 
									"...now where's the console?" );
	
				TextBox( T_DARIN, 	"What about those spinny gears in the ",
									"fromt?", "" );
									
				TextBox( T_SARA, "...hrmmm...","","" );
			}
			else
			{
				TextBox( T_SARA, 	"We forgot the [Thermal Activator]!",
									"It's in my hidden storeroom back in Rodne!", "" );
									
				TextBox( T_DARIN, 	"Whoops.  Back to Rodne!",
									"", "" );
			}
		}
		
		EntFinish(); //rbp MenuOn();
	}
	
	public static void LabMasterChestCleanup( int var_0, int var_1, int var_2 ) /* 23 */                                  // Master Chest Auto-Exec
	{
		if(flags[var_0]!=0)
		{
			AlterBTile(var_1,var_2,280,2);
		}
	}
	
	
	public static void switch_f() /* 27 */
	{
		flags[F_LAB_SWITCHCOUNT]++;
		Master3HorzSwitch(F_LAB_SWITCH_F,20,23,24,26);
	}
	
	public static void switch_g() /* 28 */
	{
		int a, b = 0, c = 0;
	
		flags[F_LAB_SWITCHCOUNT]++;
		flags[F_LAB_SWITCH_G]+=1;
		
		if(flags[F_LAB_SWITCH_G]==8)
		{
			flags[F_LAB_SWITCH_G]=0;
		}
		
		for(a=0; a<=4; a++)
		{
			AlterBTile(13,22+a,533,1);
		}
		
		if(flags[F_LAB_4000]==0) {   b=flags[F_LAB_SWITCH_G]; }
	
		if(b==5)	{	b=3;	}
		if(b==6)	{	b=2;	}
		if(b==7)	{	b=1;	}
		
		AlterBTile(13,22+b,226,0);
		
		if(b==0)	{	c=281;	}
		if(b==1)	{	c=462;	}
		if(b==2)	{	c=464;	}
		if(b==3)	{	c=463;	}
		if(b==4)	{	c=284;	}
		
		AlterBTile(18,28,c,2);
		SoundSwitch();
		unpress( 0 );
	}
	
	public static void switch_h() /* 29 */
	{
		flags[F_LAB_SWITCHCOUNT]++;
		Master3HorzSwitch(F_LAB_SWITCH_H,18,36,24,36);
	}
	
	public static void switch_i() /* 30 */
	{
		flags[F_LAB_SWITCHCOUNT]++;
		Master3HorzSwitch(F_LAB_SWITCH_I,28,29,26,36);
	}
	
	public static void switch_j() /* 31 */
	{
		flags[F_LAB_SWITCHCOUNT]++;
		Master3HorzSwitch(F_LAB_SWITCH_J,14,41,23,40);
	}
	
	public static void switch_k() /* 32 */
	{
		flags[F_LAB_SWITCHCOUNT]++;
		if(flags[F_LAB_SWITCH_K]==0)
		{
			SoundSwitch();
			AlterBTile(20,46,226,0);
			AlterBTile(20,47,533,1);
			AlterBTile(20,45,284,2);
			AlterBTile(19,47,284,2);
	
			if( entity.get(playerent).face == FACE_LEFT )
			{
				Warp(20,46,TNONE);
			} 
	
			unpress( 0 );
			flags[F_LAB_SWITCH_K]=1;
			return;
		}
		
		if(flags[F_LAB_SWITCH_K]!=0)
		{
			SoundSwitch();
			AlterBTile(20,46,533,1);
			AlterBTile(20,47,226,0);
			AlterBTile(20,45,281,2);
			AlterBTile(19,47,281,2);
	
			if( entity.get(playerent).face == FACE_UP )
			{
				Warp(20,47,TNONE);
			}  
	
	
			unpress( 0 );
			flags[F_LAB_SWITCH_K]=0;
		}
	}
	
	
	public static void downstair_e() /* 35 */
	{
		Warp(42,57,TCROSS);
		Banner("B1",300);
	}
	
	public static void downstair_f() /* 36 */
	{
		Warp(46,57,TCROSS);
		Banner("B1",300);
	}
	
	public static void upstair_e() /* 37 */
	{
		Warp(18,37,TCROSS);
	}
	
	public static void upstair_f() /* 38 */
	{
		Warp(22,37,TCROSS);
	}
	
	public static void Master3VertSwitch( int var_0, int var_1, int var_2, int var_3, int var_4 ) /* 39 */                                  // Master 3-Vert Switch
	{
		int a, b = 0, c = 0;
	
	
		flags[var_0]+=1;
		if(flags[var_0]==4)
		{
			flags[var_0]=0;
		}
		
	
		for(a=0; a<=2; a++)
		{
			AlterBTile(var_1,var_2+a,533,1);
		}
		
	
		if(flags[F_LAB_4000]==0) {   b=flags[var_0]; }
	
		if(b==3)	{	b=1;	}
		
		AlterBTile(var_1,var_2+b,226,0);
		
	
		if(b==0)	{	c=281;	}
		if(b==1)	{	c=464;	}
		if(b==2)	{	c=284;	}
	
		AlterBTile(var_3,var_4,c,2);
		SoundSwitch();
		unpress( 0 );
	}
	
	public static void Master3HorzSwitch( int var_0, int var_1, int var_2, int var_3, int var_4 ) /* 40 */                                  // Master 3-Horiz Switch
	{
		int a,b = 0,c = 0;
	
		flags[var_0]+=1;
		
		if(flags[var_0]==4)
		{
			flags[var_0]=0;
		}
	
		for(a=0; a<=2; a++)
		{
			AlterBTile(var_1+a,var_2,532,1);
		}
		
		if(flags[F_LAB_4000]==0) { b=flags[var_0]; }
	
		if(b==3) {  b=1; }
	
		AlterBTile(var_1+b,var_2,226,0);
	
		if(b==0) {  c=281; }
		if(b==1) {  c=464; }
		if(b==2) {  c=284; }
	
		AlterBTile(var_3,var_4,c,2);
		SoundSwitch();
		unpress( 0 );
	}
	
	public static void escape() /* 41 */
	{
		int sara, crystal, darin, i, x, y;
		
		sara	= GetPartyEntity( "Sara" );
		crystal	= GetPartyEntity( "Crystal" );
		darin	= GetPartyEntity( "Darin" );
	
		if( flags[F_LAB_COUNTDOWN]==0 )
		{
			EntStart(); //rbp MenuOff();
			
			AlterBTile(18, 6, 226, 0);
			AlterBTile(18,76, 226, 0);
			setzone(18,76,21);
			
			entity.get(darin).specframe		= 23;
			entity.get(crystal).specframe	= 23;
			entity.get(sara).specframe		= 23;
			
			Wait(200);
			
			VCQuake( 5,5 );
			
			entity.get(darin).specframe		= 21;
			entity.get(crystal).specframe	= 21;
			entity.get(sara).specframe		= 21;
	
			Wait(100);
	
			entity.get(darin).specframe		= 0;
			entity.get(crystal).specframe	= 0;
			entity.get(sara).specframe		= 0;
	
			entity.get(crystal).specframe = 26;
			
			TextBox(T_CRYSTAL,	"Umm... what's going on, Sara?","","");
	
			entity.get(sara).specframe=20;
			Wait(50);
			
			TextBox(T_SARA,	"Tee hee. Maybe that plasma",
							"conduit I severed was a bit",
							"more important than I thought.");
						
			entity.get(sara).specframe = 0;
			
			TextBox(T_CRYSTAL,	"You *idiot*!",
								"Darin, let's get out of here as soon as",
								"possible!");
			
			entity.get(crystal).specframe = 0;
	
			flags[F_LAB_COUNTDOWN] = 30; //30 seconds!!!
	
			V1_StartDualMode( "3,R,1,E,2,R" );
			EntFinish();
			VCQuake( 2,2 );
			hookretrace( "hooker_washington" );
		}
	}
	
	public static void hooker_washington() /* 42 */
	{
		int x, y = 0;
		
		//do normal renderstuff first.  Remember, we're in dualmode right now.
		V1RPG_RenderFunc_DUALMODE();
	
		if( timer>200 )
		{
			if(y==0)
			{
				flags[F_LAB_COUNTDOWN]--;
	
				CountDown( flags[F_LAB_COUNTDOWN] );
	
				timer=0;
				y=1;
				
				return;
			}
			
			if(y==1)
			{
				timer=0;
				
				return;
			}
		}
	}
	
	public static void CountDown( int x ) /* 43 */
	{
		
		MenuAngryBuzz();
		
		if( arTemp[0] != x )
		{
			rectfill(0, 0, imagewidth(v1_vclayer2), imageheight(v1_vclayer2), transcolor, v1_vclayer2); 
		}
		
		if( x >= 10 ) 
		{
			printstring( 5,190, v1_vclayer2, v1rpg_SmallFont, "00:"+str(x));
			arTemp[0] = x;
		} else {
			printstring( 5,190, v1_vclayer2, v1rpg_SmallFont, "00:0"+str(x));
			arTemp[0] = x;
		}
		
		if(x==0) //GAME OVER
		{
			VCText(5,190,"00:00");
	
			V1_StopDualMode();
			
			SoundExplosion();
			
			FadeToColor( RGB(255,0,0), 100 );
			
			timer = 0;
			while( timer < 200 )
			{
				render();
				rectfill(0, 0, imagewidth(screen), imageheight(screen), RGB(255,0,0), screen);
				showpage();
			}
			
			MenuOff();
			
			DoIntro();
		}
	}
	
	public static void reset_switch() /* 44 */
	{
		int q;
	
		if(flags[F_LAB_LEVERBANK_1]==1)
		{	
			for( q=8;q<=12;q++ )
			{
				AlterBTile(4,q,281,2);
				flags[F_LAB_LEVERBANK_1]=0;
			}
		}
		
		if(flags[F_LAB_LEVERBANK_2]==1)
		{
			for( q=8;q<=12;q++ )
			{
				AlterBTile(7,q,281,2);
				flags[F_LAB_LEVERBANK_2]=0;
			}
		}
		
		if(flags[F_LAB_LEVERBANK_3]==1)
		{
		
	
			for( q=8;q<=12;q++ )
			{
				AlterBTile(10,q,281,2);
				flags[F_LAB_LEVERBANK_3]=0;
			}
		}
		
		SoundSwitch();
		AlterBTile( 6,28, 284, 2 );
		unpress( 0 );
	}
	
	public static void escape_lever_a() /* 45 */
	{
		if(flags[F_LAB_LEVERBANK_1]==0)
		{
			SoundSwitch();
			AlterBTile(4,9,284,2);
			AlterBTile(14,8,533,1);
			AlterBTile(14,7,226,1);
			flags[F_LAB_LEVERBANK_1]=2;
		}
	}
	
	public static void escape_lever_b() /* 46 */
	{
		if(flags[F_LAB_LEVERBANK_2]==0 && flags[F_LAB_LEVERBANK_1]==2)
		{
			SoundSwitch();
			AlterBTile(7,12,284,2);
			AlterBTile(14,7,533,1);
			AlterBTile(14,6,226,1);
			flags[F_LAB_LEVERBANK_2]=2;
		}
	}
	
	public static void escape_lever_c() /* 47 */
	{
		if(flags[F_LAB_LEVERBANK_3]==0 && flags[F_LAB_LEVERBANK_2]==2)
		{
			SoundSwitch();
			AlterBTile(10,11,284,2);
			AlterBTile(14,6,533,1);
			AlterBTile(14,5,226,0);
			flags[F_LAB_LEVERBANK_3]=2;
		}
	}
	
	public static void evil_lever_a() /* 48 */
	{
		int q;
	
		if(flags[F_LAB_LEVERBANK_1]==0)
		{
			SoundSwitch();
			
			for(q=8;q<=12;q++)
			{
				AlterBTile(4,q,284,2);
			}
			
			flags[F_LAB_LEVERBANK_1]=1;
		}
	}
	
	public static void evil_lever_b() /* 49 */
	{ 
		int q;
	
		if(flags[F_LAB_LEVERBANK_2]==0 && flags[F_LAB_LEVERBANK_1]==2)
		{
			SoundSwitch();
	
			for(q=8;q<=12;q++)
			{
				AlterBTile(7,q,284,2);
			}
			
			flags[F_LAB_LEVERBANK_2]=1;
		}
	}
	
	public static void evil_lever_c() /* 50 */
	{
		int q;
		
		if(flags[F_LAB_LEVERBANK_3]==0 && flags[F_LAB_LEVERBANK_2]==2)
		{
			SoundSwitch();
	
			for(q=8;q<=12;q++)
			{
				AlterBTile(10,q,284,2);
			}
			
			flags[F_LAB_LEVERBANK_3]=1;
		}
	}
	
	
	public static void crystal_sad() /* 51 */
	{
		int darin, dexter, c ,q;
		VImage im;
		
		EntStart(); //rbp
		//MenuOff();
	
		if(flags[F_LAB_FOUND_CRYSTAL]==0)
		{
			
			AutoOn();
			darin 	= GetPartyEntity("Darin");
			dexter	= GetPartyEntity("Dexter");
			
			//ObsMode[a]=1;
			//ObsMode[b]=1;
			
			if(entity.get(dexter).gety()<entity.get(darin).gety())
			{
				entitymove(darin,"U1");
				entitymove(dexter,"D1 F1");
				Wait(100);
			}
			
			entity.get(darin).specframe=21;
			Wait(100);
			entity.get(darin).specframe=0;
			
			entitymove(darin,"L1 U5");
			entitymove(dexter,"U1 R1 U5");
			
			//we're gonna scroll the camera centered on crystal.  
			//She's at tile coordinate [18,92] on the map, so to get her absolute x position
			// we multiply 18 by 16 to get the topleft corner of her tile and then add 8 to get the center of her tile.
			camCtrScrollToS((18*16)+8, (92*16), 150);
			WaitForEntity( dexter );
							
			//V1_FadeOutMusic( 150 );
			FadeOut( 50 );
			
			//since we're already using hte vclayer for the fog-effect, time to break out....
			//  ....vclayer2!
			
			//let's start up dualmode vclayers!  OMG, two R's!
			V1_StartDualMode( "3,R,1,E,2,R" );
			
			//Let's fill this vc layer with BLACKNESS
			rectfill(0, 0, imagewidth(v1_vclayer2), imageheight(v1_vclayer2), RGB(0,0,0), v1_vclayer2); 
			
			//blits the cel to vclayer2
			im = new VImage( load("res/images/cells/FROZEN.PCX" ));
			blit( 100,5, im, v1_vclayer2 );
			//FreeImage(im);
					
			playmusic("res/music/CR_GUIT.S3M");
	
			TextBox(T_DARIN,	"No! This can't be!",
								"Lord Stan has imprisoned Crystal in some",
								"sort of... crystal!");
			TextBox(T_DEXTER,	"This is tragic!",
								"Neat translucency effect, though,",
								"don't you think?");
			TextBoxM(T_DARIN,	"He must be planning to keep her here",
								"until he comes up with an evil enough",
								"scheme.");
			TextBox(T_DARIN,	"Do you suppose these strange machines",
								"might be able to free her, Dexter?", "");
			TextBoxM(T_DEXTER,	"Well, I don't know much about engineering,",
								"but I have an idea about how this works.", "");
			TextBox(T_DEXTER,	"But it would require a device known as",
								"a [Thermal Activator], and I'm not sure",
								"what to do.");
	
			V1_StopDualMode();
			FadeIn( 50 );
			
			entity.get(darin).speed = 50; //halfspeed
			entitymove(darin,"F0 W50 D2");
			Wait(20);
			WaitForEntity(darin);
			Wait(35);
			entity.get(darin).specframe=15;
			Wait(50);
			entity.get(darin).specframe=28;
			TextBox(T_DARIN,	"But we can't just leave her like this,",
								"Dexter!",
								"There must be a way.");
			TextBox(T_DEXTER,	"Well, it is said that there is a machinist",
								"living in the forest city of Rodne.", "");
			
			entity.get(dexter).speed= 50; //halfspeed
			entitymove(dexter,"L2 D1");
			
			Wait(20);
			WaitForEntity(dexter);
			
			TextBox(T_DEXTER,	"Rodne is beyond the southern edge of ",
								"Raven Gulch. Shall we go?",
								"We can find help there.");
			TextBox(T_DARIN,	"Rodne? That's not... based on some sort ",
								"of Star Wars location name, is it?", "");
			TextBox(T_DEXTER,	"Of course not.","","");
			
			entity.get(darin).face=1;
			entity.get(darin).specframe=0;
			
			TextBox(T_DARIN,	"Raven Gulch, eh?",
								"How will we cross the chasm to reach",
								"the southern ridge?");
			TextBox(T_DEXTER,	"That's no problem.",
								"I have a ranger friend there.",
								"He will let us cross.");
			
			entitymove(darin,"U2 R1 F1");
			Wait(20);
			WaitForEntity(darin);
			
			Wait(100);
			entity.get(darin).specframe = 15;
			Wait(30);
			entity.get(darin).face = FACE_DOWN;
			entity.get(darin).specframe=0;
			Wait(30);
			entity.get(darin).specframe = 25;
			Wait(150);
			entity.get(darin).specframe = 10;
			Wait(30);
			entity.get(darin).face = FACE_UP;
			entity.get(darin).specframe = 0;
			
			playmusic("res/music/NONEXIST.MOD");
			
			TextBox(T_DARIN,	"Alright, then. Let's go!",
								"Crystal, sit tight.",
								"I'll be back for you, I promise!");
			TextBox(T_DEXTER,	"I'm coming too!","","");
			
			entitymove(dexter,"U1");
			
			
			camReturnToPlayer(playerent, 50);
			WaitForEntity(dexter);
					
			AutoOff();
			
			flags[F_LAB_FOUND_CRYSTAL]=1;
			
			entity.get(darin).speed 	= 100; // resume normal walking speed
			entity.get(dexter).speed 	= 100; //
		}
		
		EntFinish(); //rbp MenuOn();
	}
	
	
	// This is the master list of changes to make to the map
	//  dependant upon the flags array.  It makes sure that if
	//  you flipped a swtch to a certain position, or opened 
	//  a treasure chest, that when you leave the map and come back,
	//  the switch is where you left it, and the chest is still open.
	public static void upkeepery() /* 0 */
	{
	
		if( flags[F_LAB_START]==0 )
		{
			flags[F_LAB_SWITCH_A]=2;
			flags[F_LAB_SWITCH_B]=1;
			flags[F_LAB_SWITCH_E]=1;
			flags[F_LAB_SWITCH_F]=2;
			flags[F_LAB_SWITCH_G]=4;
			flags[F_LAB_SWITCH_I]=1;
			flags[F_LAB_SWITCH_J]=1;
	
			flags[F_LAB_START]=1;
		}
	
		if( flags[F_LAB_SWITCH_A]==3) 
		{
			flags[F_LAB_SWITCH_A]=1;
		}
	
		if( flags[F_LAB_SWITCH_A]==1 )
		{
			AlterBTile(14,6,226,0);
			AlterBTile(14,7,533,1);
			AlterBTile(10,7,464,2);
		}
		
		if( flags[F_LAB_SWITCH_B]==0 )
		{
			AlterBTile(18,17,226,0);
			AlterBTile(19,17,532,1);
			AlterBTile(23,11,281,2);
		}
		
		if( flags[F_LAB_SWITCH_B]==2 )
		{
			AlterBTile(20,17,226,0);
			AlterBTile(19,17,532,1);
			AlterBTile(23,11,464,2);
		}
		
		if( flags[F_LAB_SWITCH_B]==6 )
		{
			AlterBTile(20,17,226,0);
			AlterBTile(19,17,532,1);
			AlterBTile(23,11,464,2);
		}
		
		if( flags[F_LAB_SWITCH_B]==3 )
		{
			AlterBTile(21,17,226,0);
			AlterBTile(19,17,532,1);
			AlterBTile(23,11,463,2);
		}
		
		if( flags[F_LAB_SWITCH_B]==5 )
		{
			AlterBTile(21,17,226,0);
			AlterBTile(19,17,532,1);
			AlterBTile(23,11,463,2);
			AlterBTile(22,17,532,1);
		}
	
		if( flags[F_LAB_SWITCH_B]==4 )
		{
			AlterBTile(22,17,226,0);
			AlterBTile(19,17,532,1);
			AlterBTile(23,11,284,2);
		}
	
		if( flags[F_LAB_SWITCH_C]==0 )
		{
			AlterBTile(24,9,226,0);
			AlterBTile(24,10,533,1);
			AlterBTile(24,13,281,2);
		}
	
		if( flags[F_LAB_SWITCH_C]==2 )
		{
			AlterBTile(24,11,226,0);
			AlterBTile(24,10,533,1);
			AlterBTile(24,13,284,2);
		}
		
		if( flags[F_LAB_SWITCH_D]==3 )
		{
			flags[F_LAB_SWITCH_D]=1;
		}
	
		if( flags[F_LAB_SWITCH_D]==1 )
		{
			AlterBTile(23,18,226,0);
			AlterBTile(23,17,533,1);
			AlterBTile(32,17,464,2);
		}
	
		if( flags[F_LAB_SWITCH_D]==2 )
		{
			AlterBTile(23,19,226,0);
			AlterBTile(23,17,533,1);
			AlterBTile(32,17,284,2);
		}
	
		if( flags[F_LAB_SWITCH_E]==0 )
		{
			AlterBTile(28,24,226,0);
			AlterBTile(29,24,532,1);
			AlterBTile(31,24,281,2);
		}
	
		if( flags[F_LAB_SWITCH_E]==2 )
		{
			AlterBTile(30,24,226,0);
			AlterBTile(29,24,532,1);
			AlterBTile(31,24,284,2);
		}
		
		if( flags[F_LAB_SWITCH_F]==3 )
		{
			flags[F_LAB_SWITCH_F]=1;
		}
	
		if( flags[F_LAB_SWITCH_F]==0 )
		{
			AlterBTile(20,23,226,0);
			AlterBTile(22,23,532,1);
			AlterBTile(24,26,281,2);
		}
		
		if( flags[F_LAB_SWITCH_F]==1 )
		{
			AlterBTile(21,23,226,0);
			AlterBTile(22,23,532,1);
			AlterBTile(24,26,464,2);
		}
		
		if( flags[F_LAB_SWITCH_G]==0 )
		{
			AlterBTile(13,22,226,0);
			AlterBTile(17,22,533,1);
			AlterBTile(18,28,281,2);
		}
		
		if( flags[F_LAB_SWITCH_G]==7 )
		{
			flags[F_LAB_SWITCH_G]=1;
		}
		
		if( flags[F_LAB_SWITCH_G]==6 )
		{
			flags[F_LAB_SWITCH_G]=2;
		}
	
		if( flags[F_LAB_SWITCH_G]==5 )
		{
			flags[F_LAB_SWITCH_G]=3;
		}
	
		if( flags[F_LAB_SWITCH_G]==1 )
		{
			AlterBTile(14,22,226,0);
			AlterBTile(17,22,533,1);
			AlterBTile(18,28,462,2);
		}
		if( flags[F_LAB_SWITCH_G]==2 )
		{
			AlterBTile(15,22,226,0);
			AlterBTile(17,22,533,1);
			AlterBTile(18,28,464,2);
		}
	
		if( flags[F_LAB_SWITCH_G]==3 )
		{
			AlterBTile(16,22,226,0);
			AlterBTile(17,22,533,1);
			AlterBTile(18,28,463,2);
		}
		
		if( flags[F_LAB_SWITCH_H]==3 )
		{
			flags[F_LAB_SWITCH_H]=1;
		}
		
		if( flags[F_LAB_SWITCH_H]==1 )
		{
			AlterBTile(19,36,226,0);
			AlterBTile(18,36,532,1);
			AlterBTile(24,36,464,2);
		}
	
		if( flags[F_LAB_SWITCH_H]==2 )
		{
			AlterBTile(20,36,226,0);
			AlterBTile(18,36,532,1);
			AlterBTile(24,36,284,2);
		}
	
		if( flags[F_LAB_SWITCH_I]==0 )
		{
			AlterBTile(28,29,226,0);
			AlterBTile(29,29,532,1);
			AlterBTile(26,36,281,2);
		}
		
		if( flags[F_LAB_SWITCH_I]==2 )
		{
			AlterBTile(30,29,226,0);
			AlterBTile(29,29,532,1);
			AlterBTile(26,36,284,2);
		}
	
		if( flags[F_LAB_SWITCH_J]==0 )
		{
			AlterBTile(14,41,226,0);
			AlterBTile(15,41,532,1);
			AlterBTile(23,40,281,2);
		}
	
		if( flags[F_LAB_SWITCH_J]==2 )
		{
			AlterBTile(16,41,226,0);
			AlterBTile(15,41,532,1);
			AlterBTile(23,40,284,2);
		}
	
		if( flags[F_LAB_SWITCH_K]!=0 )
		{
			AlterBTile(20,46,226,0);
			AlterBTile(20,47,533,1);
			AlterBTile(20,45,284,2);
			AlterBTile(19,47,284,2);
		}
	
		LabMasterChestCleanup( CHEST_LAB_A,14,75 );
		LabMasterChestCleanup( CHEST_LAB_B,22,75 );
		LabMasterChestCleanup( CHEST_LAB_C,14,80 );
		LabMasterChestCleanup( CHEST_LAB_D,22,80 );
	
		if( flags[F_LAB_USED_MACHINE] !=0)
		{
			AlterBTile(18,100,0,1);
			AlterBTile(18,101,0,1);
			AlterBTile(18,102,0,1);
		}
		
		if( flags[F_LAB_SAVE_CRYSTAL]!=0 )
		{
			AlterBTile(18,91,226,0);
			AlterBTile(18,92,226,0);
		} 
	}
	
	// Like VCPutIMG but for vc layer 2.
	//
	public static void LabPutIMG( String img_name, int x_pos, int y_pos )
	{
		VImage img;
		
		img = new VImage(load( img_name) );
		tblit( x_pos, y_pos, img, v1_vclayer2 );
		//FreeImage(img);
	}
	
	
	public static void Sancho()
	{
		EntStart();
		if( flags[F_LAB_SANCHO]==0 )
		{
			TextBox( T_DARIN, "What the deuce?","What are you doing here, Sancho?","" );
			
			AutoText( T_SANCHO, "*sigh*|McGrue put me here to tell you all that you can skip the upcoming puzzle if you want.~He said that since it discouraged people from finishing the game, and since the purpose here is to demonstrate VERGE's capabilities, that it should become an optional task." );
			
			TextBoxM( T_DARIN, "That was real nice of him!","","" );
			TextBoxM( T_DARIN, "...and I'm not just saying that because he's","the one that wrote this Textbox()","statement!" );
			AutoText( T_DARIN, "...~...~...Okay, actually, it is." );
			
			AutoText( T_SANCHO, "Oh, and since it's now optional, you now get a prize for completing the puzzle.  The coolness of the prize depends on how efficiently you went through the puzzle." );
			TextBox( T_DARIN, "Oh boy!  Minigames!","","" );
			
			flags[F_LAB_SANCHO] = 1;
		}
		
		int answer;
		
		if( flags[F_LAB_SANCHO] == 1 )
		{
			if( flags[F_LAB_SULLY] ==0 )
			{
			
				answer = Prompt( T_SANCHO, "So, would you like to skip the switch","puzzle?","", "Yes, it's hard.&No.  I am a MAN." );
	
				if( answer==0 )
				{
					flags[F_LAB_SANCHO] = 2;
					flags[F_LAB_SULLY] = 2;
	
					flags[F_LAB_SWITCH_A] = 2;
					flags[F_LAB_SWITCH_B] = 2;
					flags[F_LAB_SWITCH_C] = 0;
					flags[F_LAB_SWITCH_D] = 2;
					flags[F_LAB_SWITCH_E] = 0;
					flags[F_LAB_SWITCH_F] = 0;
					flags[F_LAB_SWITCH_G] = 4;
					flags[F_LAB_SWITCH_H] = 2;
					flags[F_LAB_SWITCH_I] = 0;
					flags[F_LAB_SWITCH_J] = 2;
					flags[F_LAB_SWITCH_K] = 1;
	
					upkeepery();
	
					TextBox( T_SANCHO, "Well, the levers have all been set to","let you pass","Just walk down the middle path there." );
				}
				else
				{
					TextBox( T_SANCHO, "Well, good for you.","","" );
				}
			}
			else
			{
				TextBoxM( T_SANCHO, "You completed the puzzle all by","yourself.","" );
				TextBox( T_SANCHO, "Go you.","","" );
			}
		}
		else
		{
			TextBox( T_SANCHO, "Had I had any respect for you,","it would now be gone.","" );
		}
		
		EntFinish();
	}
	
	public static void sully()
	{
		int s_dawg, dex, dar;
		
		if( flags[F_LAB_SULLY]==0 )
		{
			//Warp sully down!
			
			setmusicvolume(0);
			SoundFanfare();
			
			dar = GetPartyEntity( "Darin" );
			dex = GetPartyEntity( "Dexter" );
			
			entitymove(dex, "D2 U0");
			entitymove(dar, "U0");
			
			AutoOn();
			
			s_dawg = entityspawn( 20,39,"res/chrs/sully.chr" );
			entity.get(s_dawg).speed=100;
			entity.get(s_dawg).obstructable =false;
			entity.get(s_dawg).obstruction =false;
			entitymove(s_dawg, "D10");
			WaitForEntity( s_dawg );
			entity.get(s_dawg).face = FACE_DOWN;
			
			if( flags[F_LAB_SWITCHCOUNT] < 32 )
			{
				AutoText( T_SULLY, "Wow!  You solved the puzzle in less moves than McGrue knows how to!~If this was legitimate, he wants to know how you did it, so email him at mcgrue@verge-rpg.com. ~However, I'll just assume you cheated for now, and not give you anything!~Byeeee!" );
			}
			else if( flags[F_LAB_SWITCHCOUNT] == 32 )
			{
				AutoText( T_SULLY, "WOW!~You solved the puzzle in "+str(flags[F_LAB_SWITCHCOUNT])+" switches!  That's *EXACTLY* the amount you needed to.~You're *AWESOME*!|Here, have a whole bunch of stuff!" );
				
				FindItem( "Medicine", 8);
				FindItem( "Starlight", 4);
				FindItem( "Miracle_Brew", 2);
				
				FindItem( "Golden_Switch", 1);
				
				TextBox( T_SULLY, "Bye now!","","" );
			}
			else if( flags[F_LAB_SWITCHCOUNT] < 38 ) //within 5 switches of the goal
			{
				AutoText( T_SULLY, "You solved the puzzle in "+str(flags[F_LAB_SWITCHCOUNT])+" switches!  That's "+str(flags[F_LAB_SWITCHCOUNT]-32)+" more than you needed to.~That's really good!|Here, have a few Miracle Brews and starlights for your efforts." );
				
				FindItem( "Starlight", 4);
				FindItem( "Miracle_Brew", 2);
				
				TextBox( T_SULLY, "Hasta la vista, Darin.","","" );
			}
			else if( flags[F_LAB_SWITCHCOUNT] < 43 ) //within 10 switches of the goal
			{
				AutoText( T_SULLY, "You solved the puzzle in "+str(flags[F_LAB_SWITCHCOUNT])+" switches!  That's "+str(flags[F_LAB_SWITCHCOUNT]-32)+" more than you needed to.~That's pretty good, but I know you can do better next time!|Here, have a few Miracle Brews for your efforts." );
				FindItem( "Miracle_Brew", 2 );
				
				TextBox( T_SULLY, "I'm outtie.  See ya!","","" );
			}
			else if( flags[F_LAB_SWITCHCOUNT] < 63 ) //within 30 switches of the goal
			{
				AutoText( T_SULLY, "You solved the puzzle in "+str(flags[F_LAB_SWITCHCOUNT])+" switches!  That's "+str(flags[F_LAB_SWITCHCOUNT]-32)+" more than you needed to.~That's okay, but I know you can do better next time!|Here, have a few Starlights for your efforts." );
				FindItem( "Starlight", 2 );
				
				TextBox( T_SULLY, "Ciao babe!","","" );
			}
			else //more than 30 extra switchflips!
			{
				AutoText( T_SULLY, "Wow!  You flipped "+str(flags[F_LAB_SWITCHCOUNT])+" switches!  That's "+str(flags[F_LAB_SWITCHCOUNT]-32)+" more than you needed to!~That totally sucks!|Here, have an Herb for your efforts." );
				
				FindItem( "Herb", 1 );
				
				TextBox( T_SULLY, "Bye now!","","" );
			}
			
			entitymove(s_dawg, "U10");
			entitymove(dex, "U2 D0");
			entitymove(dar, "D0");
			WaitForEntity( s_dawg );
			
			entity.get(s_dawg).visible = false;//hide Sully!
			
			AutoOff();
			
			setmusicvolume(global_music_volume);
			
			//Warp Sully away!
			flags[F_LAB_SULLY] = 1;
		}
	}
}