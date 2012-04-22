package sully;

import static core.Script.*;
import static sully.Flags.*;
import static sully.Sully.*;
import sully.vc.v1_rpg.V1_RPG;
import sully.vc.v1_rpg.V1_Simpletype;
import domain.VImage;

import static sully.vc.v1_rpg.V1_RPG.*;
import static sully.vc.v1_rpg.V1_Music.*;

import static sully.vc.simpletype_rpg.Party.*;
import static sully.vc.v1_rpg.V1_Maineffects.*;
import static sully.vc.v1_rpg.V1_Textbox.*;
import static sully.vc.Sfx.*;
import static sully.vc.util.General.*;
import static sully.vc.util.Animation.*;
import static sully.vc.util.Camscroll.*;
import static sully.vc.v1_rpg.V1_Simpletype.*;

public class Mountain {

		
	public static void start()
	{
		Sully.SaveDisable(); //cannot save in dungeons.
		
		if ( flags[F_MOUNT_DEX_JOIN] !=0)
			entity.get(0).setx(30000);	//if dexter has already joined, 
								//Warp his on-map doppleganger way off-screen
		
		if( flags[F_MNT_EVIL_SIGN]!=0 ) //if the sign's been destroyed,
			DestroySign();			 //make sure it stays destroyed on revisits to this map.
			
		if( flags[F_LAB_BLOWN_UP] !=0)
			DestroyLab();
			
		if (flags[CHEST_JUJUBE_A]!=0) settile(3, 31, 0, 271);
		if (flags[CHEST_JUJUBE_B]!=0) settile(5, 31, 0, 271);	
		if (flags[CHEST_JUJUBE_C]!=0) settile(7, 31, 0, 271);	
		if (flags[CHEST_JUJUBE_D]!=0) settile(9, 31, 0, 271);
	
		InitMap();
	
		if( flags[F_LAB_BLOWN_UP]==0 && flags[F_LAB_COUNTDOWN]!=0 )
			DoLabBlowup();
		
		V1_StartMusic( "res/music/DREAMS2.S3M" );
		
		Banner("Mount Jujube",300);
	}
	
	public static void overworld()
	{
		V1_MapSwitch("overworld.map", 81, 10, TBLACK);
	}
	
	public static void Meet_Dexter()
	{
		EntStart(); // rbp
		
		entity.get(0).speed=0;	//stop the map-dexter from pacing.
		
		TextBox(T_DEXTER,	"Hello? Oh, Darin my good friend!",
		           			"It's been such a long time, hasn't it?","");
		TextBox(T_DARIN,	"Dexter! You're the last person I expected",
							"to find here.","");
		
		VCPutIMG( "res/images/cells/dexter.gif" ,80,30 );
		
		TextBoxM(T_DEXTER,	"I left home after my father disapproved",
		           			"of my studies. I am searching for a sage.","");
		TextBoxM(T_DEXTER,	"Bumsville no longer gets water from the",
		           			"earth, and Rodne has no water from the",
		           			"sky.");
		TextBoxM(T_DEXTER,	"I am hoping the sage in these mountains",
		           			"may know who is responsible.", "");
		TextBoxM(T_DEXTER,	"Darin, what are you doing here? Don't you",
		           			"know that hiking is dangerous?","");
		TextBox(T_DARIN, 	"I'm exploring this world. I am on a quest",
		           			"to defeat Lord Stan and restore peace.","");
		TextBoxM(T_DEXTER,	"I see... well, it's too risky to tread",
		           			"around here by yourself.","");
		TextBoxM(T_DEXTER,	"My investigations have shown some Lord",
		           			"Stan activity around this area.","");
		TextBox(T_DARIN,	"Good. Then perhaps I can find some clue or",
		           			"lead that will help me in this quest.","");
		TextBox(T_DEXTER,	"Then I'll come with you. You can always",
		           			"use some extra help!","");
		
		ClearVCLayer();
		
		TextBox(T_DARIN,	"That's a good idea, Dexter.",
		           			"Thank you.","");
		           			
		
		//move the map-dexter onto darin (map-dexter is entity 0 on this map)
		entity.get(0).speed=100;
		entitymove(0, "L1");
		while(entity.get(0).movecode !=0)
		{
			render();
			showpage();
		}
		
		//add dexter to the party...
		JoinParty("Dexter", 4);
		
		//...and move the map-dexter out into never-never land.
		entity.get(0).setx(30000);	
		
		//set this event flag.
		flags[F_MOUNT_DEX_JOIN]=1;
		
		EntFinish(); // rbp
	}
	
	public static void watergate()
	{
		V1_MapSwitch("lab.map", 20, 5, TBLACK);
	}
	
	public static void ntunnel()
	{
		Warp(12,70,TCROSS);
	}
	
	public static void stunnel()
	{
		Warp(12,90,TCROSS);
	}
	
	public static void nexit()
	{
		Warp(3, 5, TCROSS);
	}
	
	public static void sexit()
	{
		Warp(3, 29, TCROSS);
	}
	
	public static void tunnelb()
	{
		Warp(38, 71, TCROSS);
	}
	
	public static void bexit()
	{
		Warp(30, 26, TCROSS);
	}
	
	public static void tunnelc()
	{
		Warp(36, 86, TCROSS);
	}
	
	public static void cexit()
	{
		Warp(31, 43, TCROSS);
	}
	
	public static void sageenter()
	{
		Warp(73, 85, TCROSS);
		Banner("Chez Sage",300);
	}
	
	public static void sageexit()
	{
		Warp(22, 54, TCROSS);
	}
	
	public static void plaque()
	{
		EntStart();
		
		TextBox(0,"Chez Sage ---",
		          "your one-stop forgotten mystic lore shop!","");
		          
		EntFinish();
	}
	
	public static void sage()
	{
		int sage_choice;
		
		EntStart();
	
		//the sage demands 20000 GP or Crystal. Hell, who doesn't?
		if( flags[F_MNT_GREEDY_SAGE]!=0 )
		{
			// Let's forever prevent the player from having enough cash!
			if( money >= flags[F_MNT_GREEDY_SAGE] && !CharInParty("Crystal") ) 
			{
				TextBox(0,	"I said scram unless you have",
							str(flags[F_MNT_GREEDY_SAGE])+" "+moneyname+", kiddo!", "");
				TextBox(T_DARIN,	"Bling bling, got it right here, 'pops'!","","");
				TextBoxM(0,	"!!!","","");
				TextBox(0,	"Did I say '"+str(flags[F_MNT_GREEDY_SAGE])+"'?",
							"I meant "+str(flags[F_MNT_GREEDY_SAGE]*2)+".",
							"Now get with the moolah or scram.");
				TextBox(T_DARIN,	"Sonnovah!","","");
				flags[F_MNT_GREEDY_SAGE] = flags[F_MNT_GREEDY_SAGE]*2;
				
			}
			else //if no Crystal... but yes Sara!
			if( !CharInParty("Crystal") && CharInParty("Sara") ) {
				TextBox(0,	"I no longer wish to speak to",
							"you unless you have a ponytail",
							"chick or just won the lottery.");
				TextBox(T_DARIN,	"Look, ponytail chick right here!","","");
				TextBox(T_SARA,		"...'chick'?","","");
				TextBox(0,	"Sorry Charlie.  No dice.",
							"You don't look at girls much, do ya?",
							"This babe ain't sportin' a 'tail.");
				TextBox(T_DARIN,	"Sara... could you pull your hair back?","","");
				TextBox(T_SARA,		"No.","","");
					
				
			} 
			else if( !CharInParty("Crystal") )//if no Crystal and no sara
			{
				TextBox(0,	"I no longer wish to speak to",
							"you unless you have a ponytail",
							"chick or just won the lottery.");
					
			}
		
		}
		else if( flags[F_MNT_GREEDY_SAGE]==0 && CharInParty("Dexter") )
		{
			TextBox(0,	"Eh? Who's there? I haven't had",
						"a custo... er... visitor in quite some time!", "");
			TextBoxM(3,	"Master Sage, my name is Dexter and I",
						"study magic at the university in Bumsville.", "");
			TextBox(3,	"I have come to seek your wise counsel and",
						"partake of your infinite and profound",
						"wisdom.");
			TextBox(0,	"Heh! Well, you seem like a nice enough kid.",
						"",
						"Tell you what...");
	
			sage_choice = Prompt( 0, 	"You fork over 100 "+moneyname+" and I'll tell ",
								  		"you the secret of the universe!", "", "Yes&No" );
	
			if( sage_choice !=0 ) 
			{
				TextBox(0,"Well, your loss I suppose.","","");
						
			} 
			else if( money < 100 ) 
			{
				TextBox(0,	"Are you trying to cheat me, you no good",
							"kids?",
							"Begone!");
				
			} 
			else 
			{
	
				SoundChaChing();
				TakeMoney( 100 );
	
				TextBoxM(0,	"Oh boy! Money! Mwa ha ha! I mean,",
							"(cough) (ahem)",
							"Yes, one wise enlightenment coming up.");
				TextBoxM(0,	"Pay attention to the ancient storytellers.",
							"They speak with lore from ages past.", "");
				TextBox(0,	"The hidden meaning in some of their",
							"stories just might save your life one day!", "");
				TextBox(T_DEXTER,	"...That's it?! That's the meaning of life?","","");
				TextBox(0,	"Sorry. 100 "+moneyname+" for the first minute,",
							"20,000 "+moneyname+" for each additional minute.","");
				TextBox(T_DARIN,	"20,000 "+moneyname+"?!","","");
				TextBoxM(0,	"Well, there is a 99% discount for cheeky ",
							"babes with pony tails.", "");
				TextBox(0, "Bye now!","","");
	
				flags[F_MNT_GREEDY_SAGE]=20000;
			}
			
		
		} 
		else if(CharInParty("Crystal"))
		{
			TextBox(0,	"Hey there, gorgeous! Is there anything you",
						"want me to teach you?","");
			TextBox(T_CRYSTAL,	"Watch it, you old fart. I've been known to",
								"strangle people with these arm tassles!", "");
	
			sage_choice = Prompt( 0,	"For you, a special price. I'll tell you about",
										"the greatest secret for only 200 gold!", "",
										"Yes&No" );
	
			if( sage_choice !=0 ) {
	
				TextBox(0,	"Aww... come on! Do you have any idea",
							"how hard it is for us philosophy majors",
							"to find work?");
				
			} 
			else if( money < 200 )
			{
				TextBoxM(0,	"You don't have enough money, and you're",
							"too dumb to even know it?", "");
				TextBox(0,	"Well, it's a good thing you got looks, babe.","", "");
				
			}
			
			SoundChaChing();
			TakeMoney( 200 );
			
			TextBoxM(0,	"Thank you, kind people! I shall quote from",
						"an ancient Vicarian scroll...", "");
			TextBoxM(0,	"Many years ago, the ancient gods found",
						"animals to be more worthy of the earth.","");
			TextBoxM(0,	"Humans were cursed with stupidity and",
						"animals were granted the Ultimate Power.","");
			TextBoxM(0,	"The animals desired peace, and decided not",
						"to use the Ultimate Power.","");
			TextBoxM(0,	"The animal tribe banished the Ultimate",
						"Power to the earth, so humans would",
						"never find it.");
			TextBoxM(0,	"However, one legend speaks of an icon of",
						"honesty and truth that controls this power.", "");
			TextBoxM(0,	"If a human could wield this sacred relic,",
						"the power would be wrested from the.", 
						"earth");
			TextBox(0,	"Pretty cool, huh?",
						"Well, bye now.",
						"Come again, ya hear?");
						
			flags[F_MNT_SAGE_DONE] = 1; //we're all saged out!
		}
		else
		{
			TextBox(0,	"Get out of the cave unless you have","business with the sage, babe.","");	
		}
		
		EntFinish();
	}
	
	// This function takes care of all the tile and obstruction-changing
	// work to remove the sign.
	public static void DestroySign() 
	{
		setobs( 35,7, 0 );
		setobs( 36,6, 0 );
		
		settile(35,6, 2, 0); // remove the sign from the 
		settile(35,7, 2, 0); // background overlay layer
		settile(36,6, 2, 0);
		settile(36,7, 2, 0);
		
		settile(35,6, 0, 744); // add the charred pit!
		settile(36,6, 0, 745); 
		settile(35,7, 0, 746); 
		settile(36,7, 0, 747);
		
	}
	
	// This function takes care of all the tile and obstruction-changing
	// work to remove the lab.
	public static void DestroyLab() 
	{
		int y;	
	
		AlterFTile(21,3,301,1);
		AlterFTile(22,3,302,1);
		AlterFTile(20,4,301,1);
		AlterBTile(21,4,199,1);
		AlterBTile(22,4,199,1);
		AlterFTile(23,4,302,1);
		
		for(y=20; y<=23; y++)
		{
			AlterBTile(y,5,199,1);
		}
		
		setzone(20,5,0);
		setzone(23,5,0);
	}
	
	public static void VictoryDance() {
		int dexter = GetPartyEntity( "Dexter" );
		
		if( flags[F_MNT_EVIL_SIGN]+35 > systemtime ) {
			entity.get(dexter).specframe = 45;
		} else {
			if( flags[F_MNT_EVIL_SIGN]+70 > systemtime ) {
				entity.get(dexter).specframe = 49;
			} else {
				flags[F_MNT_EVIL_SIGN] = systemtime;
			}
		}
	}
	
	public static void Roadblock() {
		int darin, dexter; //some temp references for their entity indexes
		int sparklies, pop;
		
		if( flags[F_MNT_EVIL_SIGN]==0 ) 
		{
			//MenuOff();
			EntStart(); // rbp
			
			darin	= GetPartyEntity( "Darin" );
			dexter	= GetPartyEntity( "Dexter" );
			
			sparklies = LoadAnimation( "res/images/story_fx/sparklies.pcx", 16,16);
			pop = LoadAnimation( "res/images/story_fx/pop.pcx", 16,16);
			
			
			TextBox(0,"'Evil Lab closed for Evil Renovations'","","            -The Management");
			
			TextBoxM(T_DARIN,	"Aw, nuts!",
								"A flimsy sign obstructing the way forward!","");
			
			entity.get(darin).specframe = 28;
			Wait(50);
			entity.get(darin).specframe = 30;
			Wait(100);
			
			TextBox(T_DARIN,	"There's no way we can get past this!", 
								"Crystal!  I've failed you! (sob)","");
			TextBox(T_DEXTER,	"There's no problem that a little", "WANTON DESTRUCTION couldn't solve!","Stand back!");
			
			entity.get(dexter).specframe = 31; //pose facing forward
			Wait(35);
			
			entity.get(dexter).specframe = 44; //get out the book
			Wait(50);
			entity.get(dexter).specframe = 40;
			Wait(100);
			
			entity.get(dexter).specframe = 34; //flipping the pages
			Wait(25);
			entity.get(dexter).specframe = 38;
			Wait(25);
			entity.get(dexter).specframe = 34;
			Wait(25);
			entity.get(dexter).specframe = 38;
			Wait(25);
			entity.get(dexter).specframe = 34; 
			Wait(25);
			entity.get(dexter).specframe = 38;
			Wait(25);
			
			entity.get(dexter).specframe = 43; //casting frame
			
			//magic sparklie on cane
			SoundMagic2();
			for( timer=0; timer<120; ) {
				render();
				BlitFrameAt(GetEntScrX(dexter)+1,GetEntScrY(dexter)+6, sparklies, timer/20, screen);	
				showpage();
			}
			
			//fire spell, beginning
			SoundBomb();
			for( timer=0; timer<140; ) {
				render();
				
				BlitFrameAt(GetTileScrX(36),	GetTileScrY(6)+6, pop, timer/20, screen);
				BlitFrameAt(GetTileScrX(35)+5, 	GetTileScrY(7), pop, (timer/20)-2, screen);
				BlitFrameAt(GetTileScrX(36)+4,	GetTileScrY(7)+8, pop, (timer/20)-4, screen);
				
				showpage();
				
				//let's use the flag as a sfx-counter!
				if( flags[F_MNT_EVIL_SIGN]==0 ) {
					
					if( timer > 40 ) {
						SoundBomb();
						
						flags[F_MNT_EVIL_SIGN]++;
					}
				} else if( flags[F_MNT_EVIL_SIGN] == 1 ) {
					if( timer > 80 ) {
						SoundBomb();
						
						flags[F_MNT_EVIL_SIGN]++;
					}	
				}
			}
			
			//POW!  Goodbye sign, hello smoking crater!
			DestroySign();
			
			flags[F_MNT_EVIL_SIGN] = systemtime; //using this event's flag as a timer again!
			hookretrace("VictoryDance"); 	//let's temporarily hook a map function so 
											//dexter dances while Darin gets up and talks! :D
			
			entity.get(darin).specframe = 28;
			Wait(50);
			entity.get(darin).specframe = 0; //set darin back to normal-mode.
			
			TextBox(T_DARIN,	"You know, we probably could've just",
								"moved it.","");
								
			hookretrace("sully.vc.v1_rpg.V1_RPG.V1RPG_RenderFunc"); 	//restore the default HookRetrace...
			entity.get(dexter).specframe = 0; 		//set dexter normal-mode
								
			TextBox(T_DEXTER,	"What's the point of spending years learning",
								"the arcane secrets of our universe if",
								"you're not going to abuse them?");
			
			//clean up the temp-variables
			//FreeAnimation( sparklies ); 
			//FreeAnimation( pop ); 
			
			flags[F_MNT_EVIL_SIGN] = 1;
			
			//MenuOn();
			EntFinish(); // rbp
		} 
	}
	
	public static void chest_a()
	{
		if( OpenTreasure(CHEST_JUJUBE_A, 3, 31, 271) )
		{
			FindItem( "Dagger", 1 );
		}	
	}
	
	
	public static void chest_b()
	{	
		if( OpenTreasure(CHEST_JUJUBE_B, 5, 31, 271) )
		{
			FindItem( "Medicine", 1 );
		}
	}
	
	
	public static void chest_c()
	{
		if( OpenTreasure(CHEST_JUJUBE_C, 7, 31, 271) )
		{
			FindItem( "Hood", 1 );
		}	
	}
	
	
	public static void chest_d()
	{
		if( OpenTreasure(CHEST_JUJUBE_D, 9, 31, 271) )
		{
			FindMoney( 300 );
		}
	}
	
	// The scene wherein we destory the lab.  Yaaay!
	//
	public static void DoLabBlowup() 
	{
		//MenuOff();
		EntStart(); // rbp
	
		int darin, sara, crystal;
		int a,b,c,y;
		VImage pop1, pop2, pop3;
		/*int temp[70];*/
	
		int save_sound = sfx_volume;
		
		if( sfx_volume > 30 )
			sfx_volume = 30;
		
		darin	= GetPartyEntity( "Darin" );
		sara	= GetPartyEntity( "Sara" );
		crystal	= GetPartyEntity( "Crystal" );
	
		cameratracking = 0;
		
		entitymove(darin, "R11 L3");
		
	
		entity.get(darin).specframe = 21;
		entity.get(sara).specframe = 21;
		entity.get(crystal).specframe = 21;
		
		SoundQuake();
		
		Wait(80);
		
		
		for( a=1; a<=29; a+=2 )
		{
			arTemp[a]=random(100,180);
			arTemp[1+a]=random(30,110);
		}
		
	
		pop1 = new VImage(load("res/images/story_fx/POP01.PCX"));
		pop2 = new VImage(load("res/images/story_fx/POP02.PCX"));
		pop3 = new VImage(load("res/images/story_fx/POP03.PCX"));
		
	
		for( a=1; a<=34; a+=2 )
		{
			for( b=1; b<=30; b+=2 )
			{
				SoundQuake();
				
				if( a >= b )
				{
					arTemp[30+b] += 1;
				}
			}
		
	
			for( c=31; c<=61; c+=2 )
			{
				if( arTemp[c]==1 )
				{
					tblit( arTemp[c-30], arTemp[c-29], pop1, v1_vclayer );
				}
			
				if( arTemp[c]==2 )
				{
					tblit( arTemp[c-30], arTemp[c-29], pop2, v1_vclayer );
				}
		
	
				if( arTemp[c]==3 )
				{
					tblit( arTemp[c-30], arTemp[c-29], pop3, v1_vclayer );
				}
			}
		
	
			Wait(30);
			ClearVCLayer();
		}
		
	
		ClearVCLayer();
		
	
		SoundExplosion();
	
		FadeToColor( RGB(255,0,0), 100 );
		
	
		AlterFTile(21,3,301,1);
		AlterFTile(22,3,302,1);
		AlterFTile(20,4,301,1);
		AlterBTile(21,4,199,1);
		AlterBTile(22,4,199,1);
		AlterFTile(23,4,302,1);
		
	
		for(y=20; y<=23; y++)
		{
			AlterBTile(y,5,199,1);
		}
		
	
		setzone(20,5,0);
		setzone(23,5,0);
		
		FadeFromColor( RGB(255,0,0), 100 );
	
		entity.get(darin).specframe 	= 0;
		entity.get(sara).specframe 		= 0;
		entity.get(crystal).specframe 	= 0;
	
		camReturnToPlayer(playerent, 100);	
	
		
	
		V1_StartMusic( "res/music/DREAMS2.S3M" );
		
		entity.get(darin).face 		= FACE_RIGHT;
	
		entity.get(crystal).face 	= FACE_LEFT;
		entity.get(sara).face 		= FACE_LEFT;	
		
		Wait(25);
		
	
		TextBox(T_DARIN,	"Whew, that was close!",
							"Is everyone OK?","");
		TextBox(T_CRYSTAL,	"Hooray!",
							"We destroyed Lord Stan's evil laboratory!", "");
		TextBox(T_SARA,		"We?! Excuse me, but I believe it was me",
							"who did all the work back there.", "");
		TextBox(T_CRYSTAL,	"Work?! You nearly got us all killed, ",
							"you airhead!", 
							"You are lucky Darin puts up with you.");
		TextBox(T_DARIN,	"With his lab destroyed, Stan might do",
							"something crazy.",
							"We better hurry to the Castle.");
							
		flags[F_LAB_BLOWN_UP] = 1;
		
		//FreeImage( pop1 );
		//FreeImage( pop2 );
		//FreeImage( pop3 );
		
		sfx_volume = save_sound;
		
		//MenuOn();
		EntFinish(); // rbp
	}
	
}