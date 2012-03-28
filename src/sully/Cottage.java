package sully;

import static core.Script.*;
import static sully.Flags.*;
import static sully.Sully.*;

import java.awt.Color;

import static sully.vc.simpletype_rpg.Party.*;
import static sully.vc.v1_rpg.V1_RPG.*;
import static sully.vc.v1_rpg.V1_Maineffects.*;
import static sully.vc.v1_rpg.V1_Weather.*;
import static sully.vc.v1_rpg.V1_Textbox.*;
import static sully.vc.v1_rpg.V1_Music.*;
import static sully.vc.v1_menu.Menu_System.*;
import static sully.vc.v1_menu.Menu_Choice.*;
import static sully.vc.Sfx.*;
import static sully.vc.util.Inet_Talk.*;
import static sully.vc.v1_rpg.V1_Simpletype.*;

public class Cottage {
	
	public static void start()
	{
		SaveDisable(); //cannot save in towns.
		
		//open chest checks
	
		if( flags[CHEST_COTTAGE_A] !=0 ) settile(48,73,0,177);
		if( flags[CHEST_COTTAGE_B] !=0) settile(48,74,0,177);
		if( flags[CHEST_COTTAGE_C] !=0) settile(57,73,0,177);
		if( flags[CHEST_COTTAGE_D] !=0) settile(57,74,0,177);
		if( flags[CHEST_COTTAGE_E] !=0) settile(46,82,0,177);
		if( flags[CHEST_COTTAGE_F] !=0) settile(53,82,0,177);
	
	
		InitMap();
		V1_StartMusic( "res/music/DOS3L.S3M" );
		
		Banner("McDarin Family Cottage",300);
	}
	
	public static void southexit()
	{
		V1_MapSwitch("overworld.map",35,9,TCROSS);
	}
	
	public static void enterspeak()
	{
		MenuOff(); //always with the menu offing...
		pauseplayerinput(); // rbp
		
		//if we haven't done this scene before, let's do it!
		if (flags[F_COT_INTRO] == 0)
		{
			playermove("U4 W10 F3 W40 F1 W20 F2 W40 F0");
			AutoOn();
			Wait(100);
			
			TextBoxM(T_DARIN,	"Ahhh... Home Sweet Home at last! We",
			            		"should rest up here for our trip.", "");
			TextBox(T_DARIN,	"My parents went to Acapulco for the",
			           			"month, so we have the place to ourselves.", "");
			           
			entitymove(3, "F0 W40 Z24 W20 Z0 W20 Z24 W20 Z0 W60 Z23 W100 Z22");
			while (entity.get(3).movecode != 0)
				Wait(10);
				
			TextBox(T_CRYSTAL,	"What exactly are you implying, Darin?","","");
			
			playermove("Z21 W60 Z0 S200 R5 S100 Z20 W100 Z0 L5 F0");
			
			TextBox(T_DARIN,	"Oh! Er... ummm... nothing. I just think ",
			           			"there's some gear we can use in the attic.","");
			           
			entity.get(3).specframe = 0;
			entitymove(3,"U1");
			
			while (entity.get(3).movecode != 0)
				Wait(5);
			
			//entity.x[2]=entity.x[2];
			
			AutoOff();
			
			flags[F_COT_INTRO]=1; //trip the flag so this can't happen again...
		}
		
		MenuOn(); //remember to turn the menu on again!
		unpauseplayerinput(); // rbp
	}
	
	public static void houseenter()
	{
		Warp(15,83, TCROSS);
	}
	
	public static void houseexit()
	{
		Warp(27,13, TCROSS);
	}
	
	public static void backdoorenter()
	{
		Warp(15,73, TCROSS);
	}
	
	public static void backdoorexit()
	{
		Warp(27,8, TCROSS);
	}
	
	public static void upstairs()
	{
		Warp(45,72, TCROSS);
	}
	
	public static void downstairs()
	{
		Warp(7,73, TCROSS);
	}
	
	public static void northexit()
	{
		V1_MapSwitch("overworld.map", 35,7,TBLACK);
	}
	
	public static void rabbit()
	{
		MenuOff(); //menu off before dialog
	
		TextBox(T_BUNNY,	"Oh man... I'm in heaven!",
	              			"Look at all these carrots!","");
	 	TextBox(T_DARIN,	"Hey, get away from those! My family",
	            			"worked long and hard to grow those.", "");              
	   	
	   	TextBox(T_BUNNY,	"Hey, you have no call to get snippy",
	         	  			"with me, man! I have valuable",
	              			"information!");
	 	
	 	TextBox(T_DARIN,	"Alright, spill it!","","");
	 	
	 	if (CharInParty("Crystal"))
	 	{
	  		TextBox(T_CRYSTAL,	"Aww... Darin, be nice to the little cutie.","","");
	 	}
	 	
	 	TextBox(T_BUNNY,	"You can use any fresh water source to",
		          			"heal the group. Like wells and waterfalls.", "");
		          
	 	TextBox(T_DARIN,	"Oh... great. Bye now!","","");
	 	
	 	MenuOn(); //menu on before exiting a function you turned it off in!
	}
	
	// Enter... the Stan.
	//
	public static void encounter()
	{
		int stan, galfrey, darin, crystal; //some temp variables to make the code read-easier.
		
		if (flags[F_COT_STAN] != 0)	//if we've already done this scene, 
			return;		//quit this function  without doing anything
		
		MenuOff(); //hey, this is a DRAMATIC scene.  No calling menus during it!
		
		// We're going to use these temp-variables later on when we deal with
		// Stan and Galfrey's on-map entities.  When you deal with a map's entities
		// you have to refer to their entity list indexes.
		// To see what entities are on a map, what their indexes are, or to add/edit/delete
		// entities on a map, you need to use MapEd.
		
	
		stan = 0; 		// Stan is Entity #0 on Cottage.map
		galfrey = 1;	// Galfrey is Entity #1  on Cottage.map... but don't take my word for it.
						// check it out yourself! :)
		
		//let's get darin and crystal's entity references too!
		darin = GetPartyEntity( "Darin" );
		crystal = GetPartyEntity( "Crystal" );
		
		//now we have temp-variable references to all four entites on the screen.  We can
		//now make them dance in our demented puppet-show!
		
		playermove("X27Y13"); // take a few steps forward into position...
		int i;
		int saveVol = V1_GetCurVolume();
		
		timer=0;
		while ( timer<100 ) //the timer automatically increments by 1 every 
		{					//hundreth of a second, so the following block makes the screen 
							//go half-dark and the music go quiet over the course of a second.
							
			VCLayerTintScreen(Color.BLACK, 100-(timer/2));
			setmusicvolume(100-timer);
			render();
			showpage();
		}
		
		//stops the music completely for DRAMATIC EFFECT.
		stopmusic(); 
		
		//keeps the screen tinted at the same level we just lowered it to.
		//if we didn't do this, the screen would just pop back to it's normal shade.
		// comment this line out and recompile to see for yourself!
		VCLayerTintScreen(Color.BLACK, 50); 
		
		
		TextBox(T_CRYSTAL,	"Darin, I sense something here...",
		          			"an evil presence...", "");
		          
		playmusic("res/music/badexper.mod"); //cue DRAMATIC music.
	
		//play a sound-effect to accompany the SPARKLIES
		SoundMagic1();
	
		
		
		PutSparkleStart();
			
		//we now move the chr's into position halfway between two sections of 
		//sparklie-making code!  This is to make it look like they magicked 
		//themselves in.  Little does the player know it was only a trick of VC!
		
		//the following two lines move Stan and Galfrey from their off-screen starting 
		//positions (open the map in maped and look at entity 0 and 1's starting 
		//positions to see this) to the front of the cottage.  For drama!
		entity.get(0).setx(27*16);
		entity.get(1).setx(28*16);
		
		//This continues the sparklies, now with Stan and Galfrey on-screen, 
		// completing the 'magically warped in' illusion.
		PutSparkleFinish();
		
		//Ooh, and now we do the dramatic dialogue, setting up 
		//the intricate conflict to plague the player for the 
		//rest of the game
	
		TextBox(T_STAN,"Boogie boogie boogie!","","");
	
		//Set Darin and Crystal to frame 21 (surprised)
		entity.get(darin).specframe = 21;
		entity.get(crystal).specframe = 21;
		
	
		TextBox(T_CRYSTAL,"Ahhhhhhhh!","","");
		
	
		//return them to normal
		entity.get(darin).specframe = 5;
		entity.get(crystal).specframe = 5;
	
		TextBox(T_STAN,	"Hi!  I'm the obligatory unexplained evil",
						"guy who lacks all motivation, other than",
						"'being evil'!");
	
		TextBox(T_STAN,	"Mwahahahaha!",
						"See?  Mine is an *EVIL* laugh!", "");
	
		TextBox(T_DARIN,	"Oooh, an arch-rival!",
							"And it's not even my birthday yet!", "");
	
		TextBox(T_STAN,	"Yeah... anyway, I'm just here to kidnap",
						"Crystal, taunt Darin, and introduce my",
						"sidekick.");
	
		entity.get(galfrey).specframe = 22; 
		
		TextBox(T_GALFREY,	"Yeah! I'm Galfrey, the head sidekick!",
							"You better watch your backs!", "");
		  
		entity.get(galfrey).specframe=0;
		
		TextBox(T_DARIN,	"Who are you and why have you come to",
							"this land of wonder?","");
	
	
		VCPutIMG("res/images/cells/stan.pcx",40,2);
	
		TextBox(T_STAN,	"I am Lord Stan, of course.",
			  			"Prince of Darkness!",
			  			"Bringer of Chaos and Mayhem! Cower!");
	
		entity.get(crystal).specframe = 23;
		Wait(100);
		entity.get(crystal).specframe = 5;
		TextBox(T_CRYSTAL,	"Stan...? Don't you mean...?","","");
	
		entity.get(galfrey).specframe = 21;
		Wait(150);
		entity.get(galfrey).specframe = 22;
		
		VCPutIMG("res/images/cells/galfrey.pcx",200,30);
	
		TextBox(T_GALFREY,	"Quiet! With all the right wing yahoos in",
							"Congress right now, you must be careful.", "");
		
	
		TextBox(T_GALFREY,	"We don't want VERGE to be the next",
							"target of their censorship.", "");
			  				
		entity.get(galfrey).specframe = 0;
		
		TextBox(T_STAN,	"And now I shall teleport Crystal to my",
						"secret lab in the Jujube mountains!", "");
		
	
		TextBox(T_DARIN,	"Why are you telling me all this?","", "");
		TextBox(T_STAN,	"Well, the plot would come to a grinding",
						"halt if you didn't know where to go next.", "");
		  			
		entity.get(crystal).specframe=21;
		TextBox(T_CRYSTAL,	"Oh, so am I supposed to be all panicky",
							"and helpless now?",
							"Eek! Eek! Darin, save me!");
		
	
		ClearVCLayer();
	
		TextBox(T_STAN,	"Mwa ha ha ha! Taste my power!","","");
		
	
		entity.get(crystal).specframe=25;
	
			
		// Stop the music and start the 'warping crystal away'
		// effect!
		stopmusic();
		SoundWarpZone();
		timer=0;
		while (timer<50)
		{		
			render();
			setlucent(100-(timer*2));
			rectfill(0, 0, 320, 240, RGB(255,0,0), screen);
			setlucent(0);
			showpage();
		}
	
		RemovePlayer( "Crystal" );	//remove crystal from the party...
		
	
		entity.get(crystal).setx(30000);	//...then warp her entity offscreen!
		
	
		//finish the 'warping crystal away' effect!
		timer=0;
		while (timer<50)
		{		
			render();
			setlucent(timer*2);
			rectfill(0, 0, 320, 240, RGB(255,0,0), screen);
			setlucent(0);
			showpage();
		}
		
		//
		// Let's do a little puppet-play of darin expressing SHOCK!
		//
			
		entity.get(darin).specframe=0;
		Wait(40);
		entity.get(darin).specframe=24;
		Wait(20);
		entity.get(darin).specframe=0;
		Wait(20);
		entity.get(darin).specframe=24;
		Wait(20);
		entity.get(darin).specframe=0;
		Wait(40);
		entity.get(darin).specframe=15;
		Wait(20);
		entity.get(darin).specframe=5;
		
		//
		//now for the post you-stole-my-girlfriend-you-DICK dialogue
		//
		TextBox(T_DARIN,	"Crystal! No! She just... vanished!",
		  					"Lord Stan!",
		  					"I vow to beat you and restore justice!");
	
		TextBox(T_STAN,	"You don't stand a chance, puny mortal!",
						"My lair in Castle Heck is quite secure!", "");
	
		entity.get(galfrey).specframe=22;
	
		TextBox(T_GALFREY,	"Yeah! Castle Heck!",
							"The gate is locked, so you'll never get in!", 
							"Neener Neener Neener!");
		entity.get(galfrey).specframe=0;
		
		TextBox(T_DARIN,	"Well, what's stopping me from beating the",
							"snot out of you right now?", "");
	
		TextBox(T_STAN,	"Because I'm going to vanish again before",
						"you can strike me! Mwa ha ha!", "");
	
		TextBox(T_DARIN,"No, you're not. Look.", "*BANG*  *POW*  *BIFF*","");
		
		SoundHit();
		Earthquake( 4, 0, 80 );
	
		TextBox(T_STAN,"Ow! Ow! Stop that!","","");
	
		entity.get(galfrey).specframe=23;
		Wait(100);
		entity.get(galfrey).specframe=0;
	
		TextBox(T_GALFREY,	"Geez... Prince of Darkness indeed.",
							"You sure have let yourself go.", "");
			  
		TextBox(T_DARIN,	"Yeah, and what's with that retarded ",
							"purple cloak?","");
	
		TextBox(T_STAN,	"You win this round, Darin, but if ",
						"Vecna ever develops a combat system...", "");
		TextBox(T_STAN,	"I will crush you like a bug!",
						"MWA HA HA HA HA!","");	
		
		entity.get(player).face=2; // was playerent	
		
		
		//let's do the sparklie trick again, except this time warping them away!
		SoundMagic1();
		
		PutSparkleStart();
		
		//warp Stan and Galfrey away!
		entity.get(0).setx(30000);
		entity.get(1).setx(30000);
		
		//the finishing sparklies
		PutSparkleFinish();
	
		timer = 0;
		while (timer<100)
		{
			VCLayerTintScreen(Color.BLACK, timer/2+50);
			render();
			showpage();
		}
		
		VCLayerTintOff();
		
		entity.get(darin).specframe=0;
		TextBox(T_DARIN,"They're gone...","","");
		entity.get(darin).specframe=25;
		
		playmusic("res/music/cr_guit.s3m");	//play HEARTBREAKINGLY SAD music
		V1_SetCurVolume(saveVol);
		Wait(100);
		
		//now for the post-drama monologue!
		TextBox(T_DARIN,	"(sniff) Crystal... ",
							"...why did you have to suffer?",
							"...WHY?!");
	  
		entity.get(darin).specframe=15;
		Wait(20);
	
		
	
		flags[F_COT_STAN]=1; //trip this flag so it cannot happen again.
		
		MenuOn();	// After DRAMA, enable menus just so you can be sure 
					// Crystal actually is gone.
					
		entity.get(darin).specframe = 0;	//finally, set the specframe back to 0,
									 	//or else darin will be stuck in the last 
									 	//specframe that he was set to.
									 	//
									 	//comment this line out to see him glide around
									 	//willy-nilly like a moonwalkin' freak!
	}
	
	// The function for the stan-sparklies when he appears and disappears.
	//
	public static void PutSparkleStart()
	{
		do_sparklies(1);
		do_sparklies(2);
		do_sparklies(1);
		do_sparklies(2);
		do_sparklies(3);
		do_sparklies(4);
		do_sparklies(3);
		do_sparklies(4);
		
		/*
		int i,ofs;
		timer = 0;
		while (timer<120) //this is the code to start Stan's sparklies!
		{
			render();
			i = timer/10;
			
			switch (i)
			{
				case 0:	TBlitFrame(152, 80, sparkle3, 0, screen);
						TBlitFrame(168, 80, sparkle3, 0, screen);
				case 1: TBlitFrame(152, 80, sparkle3, 1, screen);
						TBlitFrame(168, 80, sparkle3, 1, screen);
				case 2: TBlitFrame(152, 80, sparkle3, 2, screen);
						TBlitFrame(168, 80, sparkle3, 2, screen);
				case 3:	TBlitFrame(152, 80, sparkle3, 3, screen);
						TBlitFrame(168, 80, sparkle3, 3, screen);		
				case 4:	TBlitFrame(152, 80, sparkle3, 0, screen);
						TBlitFrame(168, 80, sparkle3, 0, screen);
				case 5: TBlitFrame(152, 80, sparkle3, 1, screen);
						TBlitFrame(168, 80, sparkle3, 1, screen);
				case 6: TBlitFrame(152, 80, sparkle3, 2, screen);
						TBlitFrame(168, 80, sparkle3, 2, screen);
				case 7:	TBlitFrame(152, 80, sparkle3, 3, screen);
						TBlitFrame(168, 80, sparkle3, 3, screen);		
				
				case 8: TBlitFrame(152, 80, sparkle2, 0, screen);
						TBlitFrame(152, 96, sparkle2, 1, screen);
						TBlitFrame(168, 80, sparkle2, 0, screen);
						TBlitFrame(168, 96, sparkle2, 1, screen);
						
				case 9: TBlitFrame(152, 80, sparkle2, 1, screen);
						TBlitFrame(152, 96, sparkle2, 0, screen);
						TBlitFrame(168, 80, sparkle2, 1, screen);
						TBlitFrame(168, 96, sparkle2, 0, screen);
				case 10:
						TBlitFrame(152, 80, sparkle2, 0, screen);
						TBlitFrame(152, 96, sparkle2, 1, screen);
						TBlitFrame(168, 80, sparkle2, 0, screen);
						TBlitFrame(168, 96, sparkle2, 1, screen);
				case 11:
						TBlitFrame(152, 80, sparkle2, 1, screen);
						TBlitFrame(152, 96, sparkle2, 0, screen);
						TBlitFrame(168, 80, sparkle2, 1, screen);
						TBlitFrame(168, 96, sparkle2, 0, screen);
	
			}			
			
			showpage();
		}*/
	}
	
	public static void PutSparkleFinish() 
	{
		/*
	
		int i,ofs;
		timer = 0;
		while (timer<40) //this is the code to start Stan's sparklies!
		{
			render();
			i=0;
			
			i = timer/10;
			if( i>=2 ) i-=2;
			
			switch(i) 
			{
				case 0: TBlitFrame(152, 80, sparkle1, 0+ofs, screen);
						TBlitFrame(168, 80, sparkle1, 0+ofs, screen);
				case 1: TBlitFrame(152, 80, sparkle1, 1+ofs, screen);
						TBlitFrame(168, 80, sparkle1, 1+ofs, screen);
			}
			
			showpage();
		}*/
		
		do_sparklies(5);
		do_sparklies(6);
		do_sparklies(5);
		do_sparklies(6);
	}
	
	
	public static void chest_a()
	{
		if( OpenTreasure(CHEST_COTTAGE_A, 48,73, 177) ) 
		{
			FindItem( "Atlas_Scroll", 1 );
		}
	}
	
	public static void chest_b()
	{
		if( OpenTreasure(CHEST_COTTAGE_B, 48,74, 177) ) 
		{
			FindItem( "Medicine", 2 );
		}
	}
	
	public static void chest_c()
	{
		if( OpenTreasure(CHEST_COTTAGE_C, 57,73, 177) ) 
		{
			FindItem( "Miracle_Brew", 1 );
		}
	}
	
	public static void chest_d()
	{
		if( OpenTreasure(CHEST_COTTAGE_D, 57,74, 177) ) 
		{
			FindMoney( 200 );
		}
	}
	
	public static void chest_e()
	{
		if( OpenTreasure(CHEST_COTTAGE_E, 46,82, 177) ) 
		{
			FindItem( "Herb", 3 );
		}
	}
	
	
	public static void chest_f()
	{
		if( OpenTreasure(CHEST_COTTAGE_F, 53,82, 177) ) 
		{
			FindItem( "Herb", 2 );
		}
	}
	
	public static void do_sparklies( int idx ) /* 27 */
	{
		int x, y;
		
		for(x=152; x<=168; x+=16)
		{
			for(y=72; y<=72; y+=16)
			{
				if(idx==1)
				{
					VCPutIMG("res/images/story_fx/SPARKLE1.PCX",x,y+16);
				}
				
				if(idx==2)
				{
					VCPutIMG("res/images/story_fx/SPARKLE2.PCX",x,y+16);
				}
				
				if(idx==3)
				{
					VCPutIMG("res/images/story_fx/SPARKLE3.PCX",x,y+16);
				}
				
				if(idx==4)
				{
					VCPutIMG("res/images/story_fx/SPARKLE4.PCX",x,y+16);
				}
				
				if(idx==5)
				{
					VCPutIMG("res/images/story_fx/SPARKLE5.PCX",x,y-8+16);
					VCPutIMG("res/images/story_fx/SPARKLE6.PCX",x,y+8+16);
				}
				
				if(idx==6)
				{
					VCPutIMG("res/images/story_fx/SPARKLE6.PCX",x,y-8+16);
					VCPutIMG("res/images/story_fx/SPARKLE5.PCX",x,y+8+16);
				}
			}
		}
		
		Wait(10);
		ClearVCLayer();
	}
	
	public static void Computer()
	{
		EntStart();
		
		int answer, done = 0, msg_done;
		String prev_tags = "", tmp_s;
		
		String user;
		String pass;
		String msg;
		
		if( flags[F_COT_COMPUTER] == 0 )
		{
			if( !CharInParty("Sara") )
			{
				TextBoxM( T_DARIN, "What the...?", "...looks like Dad's bought a new toy!","" );
				TextBoxM( T_DARIN, "...", "","" );
				TextBoxM( T_DARIN, "...whatever it is, it seems to be broken.","I'm sure I'll meet someone in my travels to", "fix it" );
			}
			else
			{
				TextBox( T_SARA, "!!!","A computer!","" );
				TextBox( T_DARIN, "I think it's broken.","It just sits there doing nothing!","" );
				SoundSwitch();
				TextBox( T_SARA, "There.","","" );
				TextBox( T_DARIN, "Wow!  You truly are a mechanical genius!","","" );
				TextBox( T_SARA, "Yes.  Years and years of study to be","able to press an 'on' switch.","" );
				
				flags[F_COT_COMPUTER] = 1;
			}
		}
		else
		{
			answer = Prompt( 0, "Good morning, master.","Shall we continue?","(requires internet connection)", "Yes|No" );
			
			if( answer == 0 )
			{
			
				while( done==0 )
				{
					answer = Prompt( 0, "Good morning, master.","What would you like to do today?","", "Read Tagboard|Put Msg on Tagboard|Complain about the game|Quit" );
	
					if( answer==0 )
					{
						tmp_s = GetTag( prev_tags );
						
						if( tmp_s.equals("") ) //resetmode!
							prev_tags =	tmp_s;
						else
							prev_tags = prev_tags +","+tmp_s;
					}
					else if( answer == 1 )
					{
						Banner( "Enter your verge-rpg.com username:", 300 );
						unpress(0);
						Wait(2);
						user = MenuInputBox("");
						unpress(0);
	
						Banner( "Enter your verge-rpg.com password:", 300 );
						unpress(0);
						Wait(2);
						pass = MenuInputBox("");
						unpress(0);
						
						answer = Prompt( 0, "Username: '"+user+"'", "Password: '"+pass+"'","...Is this correct?", "Yes|No" );
						
						if( answer==0 )
						{
							msg_done = 0;
							
							while( msg_done == 0 )
							{
								
							
								Banner( "Enter your msg:", 300 );
								VCPutIMG( "res/system/input_key.pcx",10,10 );
								unpress(0);
								Wait(2);
								msg = MenuInputBox("");
								ClearVCLayer();
								
								if( len(msg) > 255 )
								{
									answer = Prompt( 0, "Your message was too long.","Please limit yourself to 255 characters.","...Try again?", "Yes|No" );
									
									if( answer != 0 )
									{
										msg_done = 1;
									}
								}
								else
								{
	
									Banner( "Playing back message...",100 );
									AutoText( 0, msg );
									Banner( "Done playing back message...",100 );
									
									AutoText(0,"Shall I send that message to verge-rpg.com?  You only get the one chance, and the admins may decline your message if it is profane or otherwise inappropriate.");
									answer = Prompt( 0, "Shall I send that message to verge-rpg?","You can only do this once ever,","so be sure!", "Yes&No" );
									
									if( answer == 0 )
									{
										SendMessage( user, pass, msg );
										msg_done = 1;
									}
									else
									{
										msg_done = 1;
									}
								}
							}						
						}
					}
					else if( answer == 2 )
					{
						TextBox( T_DARIN, "That's odd.","It seems to not be responding!","" );
					}
					else
					{
						done = 1;
					}
				}
			}
		}
		
		EntFinish();
	}
}