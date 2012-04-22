package sully;

import static core.Script.*;
import static sully.Flags.*;
import static sully.Sully.*;

import static sully.vc.v1_rpg.V1_RPG.*;
import static sully.vc.v1_rpg.V1_Music.*;

import static sully.vc.simpletype_rpg.Party.*;
import static sully.vc.simpletype_rpg.Cast.*;
import static sully.vc.simpletype_rpg.Inventory.*;
import static sully.vc.v1_rpg.V1_Maineffects.*;
import static sully.vc.v1_rpg.V1_Textbox.*;
import static sully.vc.v1_menu.Menu_System.*;
import static sully.vc.v1_menu.Menu_Shop.*;
import static sully.vc.Sfx.*;
import static sully.vc.util.General.*;
import static sully.vc.v1_rpg.V1_Simpletype.*;

public class Bumville {
	

	public static void start()
	{
		int a, b;
		
		if( flags[F_BUM_FLASHBACK] == 2 ) 
		{
			flags[F_BUM_NIGHT] = 3; //set this flag so the next if-statement
									//doesn't trigger again.
									
			flags[F_BUM_FLASHBACK] = 3; //and make it so this if doesn't trigger
										//again either!
	
			Sully.SaveDisable(); //cannot save in towns.
			InitMap();
			
			setmusicvolume(0);
			V1_StartMusic( "res/music/SIMPL2.S3M" );
	
			cameratracking = 1;
	    
			FadeInWSound();
	
			
		
	 		TextBox(0,	"Feeling better this morning, sir? ",
	          			"I hope you had a good rest and please",
	          			"come again!");
	          			
	          			
			EntFinish();
			return;
		}
		
		//
		// If this flag equals 2, we just came from the continued cutscene 
		// from Pardise Isle (the same one that started in Ent_1 on this map
		// earlier.  Now let's continue it!
		if( flags[F_BUM_NIGHT] == 2 ) 
		{
			InitMap();
			
			FadeIn(30);
			
			timer=0;
			hooktimer( "ChirpyChirp" );	//crickettime!
			
			TextBox(T_DARIN,	"I should have put my fears aside and ",
								"attacked Stan with all the might",
								"I could muster.");
								
			TextBoxM(0,			"Oh? Would your sweetheart be so proud",
								"of you if you were dead?", "");
								
			TextBox(0,			"You know, sometimes you have to miss",
								"someone before you can know how",
								"much you love them.");
								
			TextBox(T_DARIN,	"Perhaps...",
								"but I know she's out there... somewhere...",
								"waiting for me to come...");
						
			cameratracking=0;
			a = xwin;
			
			timer=0;
			while( timer<180 )
			{
				render();
				xwin = (a-(timer/2));
				showpage();
			}
			
			a = xwin;
			b = ywin;
			timer=0;
			while( timer<200 )
			{
				render();
				xwin = (a-(timer/2));
				ywin = (b-(timer/2));
				showpage();
			}
			
			Wait(200);
			
			hooktimer( "" ); //turn off the crickets!
			flags[F_BUM_FLASHBACK] = 1; //trigger the flashback flag-of-honor!
			
			FadeOutWSound(30);
			
			FillVCLayer( RGB(0,0,0) );
			printcenter(imagewidth(screen)/2, imageheight(screen)/2, v1_vclayer, v1rpg_SmallFont, "Meanwhile...");
	
			VCScreenFilterOff(); //turn the nighttime effect off finally...
			
			FadeIn(30);
	
			timer=0;
			while( timer < 200 ) 
			{
				render();
				showpage();
			}
	
			FadeOut(30);
			
			FillVCLayer( RGB(0,0,0) );
			
			// Now we continue this cutscene over on lab.map!
			// Whee, we're all over the place here! :D
			V1_MapSwitch("LAB.MAP",18,92,TNONE);
		}
		
		Sully.SaveDisable(); //cannot save in towns.
		InitMap();
		V1_StartMusic( "res/music/SIMPL2.S3M" );
		
		Banner("Bumsville: City of Bums.",300);
	}
	
	public static void Overworld()
	{
		//clear the fashion-flags as we leave!
		flags[F_FASHION_PURCHASE]	= 0;
		flags[F_FASHION_HAIR]		= 0;
		flags[F_FASHION_CAPE]		= 0;
		flags[F_FASHION_CLOTHES]	= 0;
		flags[F_FASHION_MADEOVER]	= 0;
		
	
		V1_MapSwitch("overworld.map", 58, 21, TBLACK);
	}
	
	public static void Idol()
	{
		EntStart();
		
		TextBox(0,	"LORD VECNA: ARCHITECT OF THE COSMOS. HIS",
	           		"DIVINE TRUTH AND LIGHT SHOWERS UPON ALL.", "");
	
		TextBox(T_DARIN,	"Wow! A statue of Lord Vecna! I can only",
							"hope to be half as cool as him someday.", "");           
	 
		if ( CharInParty("Crystal") )
		{
	 
			TextBox(T_CRYSTAL,	"Aww... Darin. I think you're twice as cool",
								" as vecna.","");
			TextBox(T_DARIN,	"Blasphemy! Vecna rules over all!",
								"Repent immediately, you heathen temptress!", "");
		}
	 
		if ( CharInParty("Dexter") )
		{
			TextBoxM(T_DEXTER,	"Ah... yes. We learned about Vecna in",
							 	"school. His power over our world is",
							 	"absolute.");
			TextBox(T_DEXTER,	"I wasn't sure if I believed in him for",
								"awhile.",
								"I was a bit of a vecnostic.");
			TextBox(T_DARIN,	"...but you have faith again?","","");
			TextBox(T_DEXTER,	"Sure!  He finally gave me the power",
								"to blow things up with my MIND!",
								"That's hella cool!" );
			TextBox(T_DARIN,	"Christ, this dialogue needs to","be awesomer.","");
			TextBox(T_DEXTER,	"...yes.","","");
			TextBox(T_DARIN,	"...","","");
			TextBox(T_SULLY,	"Don't look at me.","I'm not even IN bumsville.","");
		}
		
		if( CharInParty("Sara") ) 
		{
			TextBoxM(T_SARA,	"I find all this divine figure stuff ",
								"rather archaic. Truth lies in technology.", "");
			TextBox(T_SARA,	"I wonder if this Vecna fellow even knows",
							"how to use a simple computer...", "");
		}
		
		if( CharInParty("Galfrey") ) 
		{
			TextBox(T_GALFREY,	"Hey! I think he's copying my helmet style. ",
								"What do you think?", "");
		}
		
		EntFinish();
	}
	
	public static void Ent0()
	{
		EntStart();
		
		if( flags[F_BUM_NIGHT]!=0 && flags[F_BUM_NIGHT] != 3 ) //this starts the chain of nighttime events! :D
		{
			TextBox(0,			"Aren't you up past your bedtime, junior?",
								"Say, why are you looking so sad?", "");
	
	
			TextBox(T_DARIN,	"Huh...? Oh... it's nothing.","","");
	
	
			TextBox(0,			"Aww... who are you kidding?",
								"Come on, kid. You look like your puppy",
								"just got killed!");
	
			TextBox(T_DARIN,	"Well... a friend of mine is in danger and",
								"I have to rescue her.",
								"I'm very worried.");
	
			TextBox(0,			"Ah! I see. Well, I see bravery in them",
								"thar eyes, sonny. ",
								"I'm sure everything with turn out.");
	
			TextBoxM(T_DARIN,	"(sigh) Crystal... I shouldn't have failed you.",
								"...", 
								"...I wish you were here right now.");
	
			TextBox(T_DARIN,	"This breeze is so cool and balmy... ",
								"...Just like the nights we used to spend",
								"together...");
	
			hooktimer( "" ); //turn off the crickets!
			V1_MapSwitch("ISLAND.MAP",11,1,0);
		}
	    
	    
	    TextBox(0,	"Hello there, young adventurer. Keep your",
	              	"hopes up! Never give up and you're sure",
	              	"to succeed.");
	              	
		EntFinish();
	}
	
	public static void Ent1()
	{
		EntStart();
	 	
	 	TextBox(0,"We get all of our carrots from that nice",
	              "farm family that lives to the northwest.", "");         
	 	
	 	EntFinish();
	}
	
	public static void Ent3()
	{
		EntStart();
		
	 	TextBox(0,"I'm going on vacation next fall to",
	              "Paradise Isle. I hear they have",
	              "great clam hunting.");
		
	
		EntFinish();
	}
	
	public static void Ent4()
	{
		EntStart();
		
	 	TextBox(0,"Lord Stan cursed our earth so we never",
		          "get any ground water. You need to rest",
		          "at the inn.");
		
	
		EntFinish();
	}
	
	public static void Ent5()
	{
		EntStart();
		
	 	TextBoxM(0,"There are actually two smiths that work",
	               "in this town.","");
	 	TextBoxM(0,"However, the one up on the hill is a",
	               "complete and total weirdo.", "");
	    TextBox(0, "He only fashions equipment out of",
	               "vegetables. He missed the Bronze Age",
	               "I guess.");
	
		EntFinish();
	}
	
	public static void Ent6()
	{
		EntStart();
		
	 	TextBoxM(0,"There was an old sage that once lived",
	               "here, giving out sage advice.", "");         
	 	TextBox(0,"He disappeared a few years back. I think",
	              "he headed to Mt. Jujube to meditate.", "");
	
		EntFinish();
	}
	
	public static void Ent7()
	{
		int answer;
	
		EntStart();
		
		answer = Prompt(0,	"I'm a wandering storyteller!",
							"Do you want to hear a great",
							"and epic tale?","Sure&Nope");
		if( answer !=0 )
		{
			TextBox(0,	"Well I didn't want to tell it",
						"to ya anyway, ya bum!","");
			EntFinish();
			return;
		}
		
		TextBoxM(0,	"Great! Okay, there was once a",
					"great desert and a king that",
					"ruled a mighty castle.");
		TextBoxM(0,	"He heard of some magic jewels",
					"so he sent the prince, his",
					"second son, to fetch them.");
		TextBoxM(0,	"The prince rode for five days",
					"and nights before he",
					"arrived at the magical cave.");
		TextBoxM(0,	"Inside the cave were four",
					"brilliant gems. Yellow, blue",
					"green, and red.");
		TextBox(0,	"And everyone lived happily",
					"ever after! So what do you",
					"think? Suspenseful, huh?");
		EntFinish();
	}
	
	public static void Ent8()
	{
		EntStart();
		
		TextBoxM(0,	"Welcome to the city of Bumsville!",
		           	"We're at the hub of the world's",
		           	"civilization!");
		TextBox(0,	"... Pretty sad, huh?","","");
		
		EntFinish();
	}
	
	public static void Ent9()
	{
		EntStart();
		
		TextBoxM(0,"This is the mountain city, not like the",
	               "tree hugging hippies from Rodne to the",
	               "south.");
	    TextBox(0,"Watch your step on the cliffs and there's",
	              "a nice view of Mt. Jujube on the highest",
	              "plateau.");
	
		EntFinish();
	}
	
	public static void Ent27()
	{
		EntStart();
		
		TextBoxM(0,	"Mister, you gotta help me! My sister keeps",
					"running in circles around the school!", "");
		TextBoxM(0,	"Ever since she got kicked by that cow,",
					"she does nothing but tell stupid stories!","");              
		TextBox(0,	"Why do all her stories have so many weird",
					"extraneous numerical references in them?","");               
		
	
		EntFinish();
	}
	
	public static void Zealot1()
	{
		ZealotStart();
		
		if( flags[F_BUM_ZEALOT_1] == 0 ) {
			TextBox(0,	"We go about our lives in this city",
						"worshipping the profound and great",
						"deity named Vecna.");
			
			flags[F_BUM_ZEALOT_1]++;
		
	
		} else if( flags[F_BUM_ZEALOT_1] == 1 ) {
	 	
	 		TextBox(0,	"All hail vecna, King of all Bums!","","");
	 		flags[F_BUM_ZEALOT_1]++;
	 		
		} else if( flags[F_BUM_ZEALOT_1] == 2 ) {
			
	 		TextBox(0,	"We pay tribute to vecna by leaving",
	 					"His favorite foods by his statue:",
	 					"Cold beer and firm melons!");
	 					
	 		flags[F_BUM_ZEALOT_1] = 0;
		}
		
		ZealotFinish();
	}
	
	public static void Zealot2()
	{
		ZealotStart();
		
	    TextBoxM(0,"You shall kneel before our mighty god,",
	               "creator of the land and skies!","");         
	    
	    TextBox(0, "If you fail to pray, you shall be",
	               "smoten by his cruel and heartless",
	               "vengeance!");
	               
		ZealotFinish();
	}
	
	public static void Zealot3()
	{
		ZealotStart();
		
	    TextBoxM(0,"Yeah, I guess Vecna's pretty cool. He",
	               "did, like, make the universe and stuff.", "");
	    TextBox(0, "Worshipping, though? I mean, what did",
	               "he do for me, like, recently, ya know?", "");
	               
		ZealotFinish();
	}
	
	public static void Zealot4()
	{
		ZealotStart();
		
	    TextBox(0,"Isn't Vecna just the dreamiest? ##",
	              "His sandals and his toga are so...",
	              "revealing!");
	              
		ZealotFinish();
	}
	
	
	//
	// Special function to call at the start of any Zealot's event.
	public static void ZealotStart() {
		
		EntStart(); // rbp
		MenuOff(); //turn off the menu...
		
	//	entity.speed[2]  = 0;	//STOP ALL ZEALOTS!
	//	entity.speed[28] = 0;
	//	entity.speed[29] = 0;
	//	entity.speed[30] = 0;
	}
	
	//
	// Special function to call at the end of any Zealot's event.
	public static void ZealotFinish() {
	
		EntFinish(); // rbp
		MenuOn(); //turn off the menu...
		
	//	entity.speed[2]  = 75;	//START ALL ZEALOTS AGAIN!
	//	entity.speed[28] = 75;
	//	entity.speed[29] = 75;
	//	entity.speed[30] = 75;
	}
	
	
	
	public static void InnEnter()
	{
		Warp(3, 103, TCROSS);
		Banner("Inn",300);
	}
	
	public static void InnExit()
	{
		Warp(8, 63, TCROSS);
	}
	
	public static void InnUpstair()
	{
		Warp(17, 119, TCROSS);
		Banner("2F",300);
	
	}
	
	public static void InnDownstair()
	{
		Warp(17, 95, TCROSS);
	}
	
	public static void SchoolEnter()
	{
		Warp(44, 106, TCROSS);
		Banner("University",300);
	}
	
	public static void SchoolExit()
	{
		Warp(38, 66, TCROSS);
	}
	
	public static void HouseEnter()
	{
		Warp(13, 151, TCROSS);
		Banner("Dexter's Home",300);
	}
	
	public static void HouseExit()
	{
		Warp(38, 29, TCROSS);
	}
	
	public static void StudioEnter()
	{
		Warp(48, 179, TCROSS);
		Banner("Fashion Studio",300);
		
		//set the flag to a value between 0 and 10 inclusive.
		flags[F_BUM_JUKE_VOLUME] = global_music_volume;
		flags[F_BUM_JUKE_VOLUME] = flags[F_BUM_JUKE_VOLUME]/10;
		
		if(flags[F_BUM_JUKE_VOLUME]==10) {
			flags[F_BUM_JUKE_VOLUME]--;
		}
		
		//set the volume control tile!
		settile(57,164,0,507+flags[F_BUM_JUKE_VOLUME]);
	}
	
	public static void StudioExit()
	{
		Warp(18, 48, TCROSS);
		setmusicvolume( global_music_volume );
	}
	
	public static void ItemEnter()
	{
		Warp(48, 152, TCROSS);
		Banner("Pharmacy",300);
	}
	
	public static void ItemExit()
	{
		Warp(16, 30, TCROSS);
	}
	
	public static void WeaponEnter()
	{
		Warp(46, 127, TCROSS);
		Banner("Armory",300);
	}
	
	public static void WeaponExit()
	{
		Warp(44, 50, TCROSS);
	}
	
	public static void SmithEnter()
	{
		Warp(10, 174, TCROSS);
		Banner("Blacksmith",300);
	}
	
	public static void SmithExit()
	{
		Warp(47, 15, TCROSS);
	}
	
	public static void Innkeeper()
	{
		int i, dex_ent; //temp vars we may need later in the function...
		
		EntStart();
		
		if( flags[F_BUM_NIGHT]!=0 && flags[F_BUM_NIGHT] != 3 ) // if it's 3, the scene's over
		{
			TextBox(0,	"I trust everything is ok, sir?",
						"You might want to take a walk on such",
						"a beautiful night.");
			EntFinish();
			return;
		}
		
		TextBoxM(0,	"Welcome to the Lazy Anteater Hotel!",
					"We charge by the night, hour, or minute",
					"here!");
					
		flags[F_BUM_INN] =	Prompt(0,	"Want a good night's sleep?",
										"It will only set you back a mere 20 "+moneyname+".","",
										"Yes&No");
		if( flags[F_BUM_INN]!=0 )
		{
			TextBox(0,	"Sorry to hear that.",
						"Be sure to come back for all of your",
						"lodging needs!");
			EntFinish();
			return;
		}
	
		if( money < 20 )
		{
			TextBox(0,	"You don't have enough money!",
						"Get out of here, ya deadbeat!","");
			EntFinish();
			return;
		}
		
		SoundChaChing(); //take the money and play the chachin sound!
	 	TakeMoney(20);
	 	
		TextBox(0,	"Thank you very much, sir!",
					"You will find your room upstairs.",
					"Have a pleasant stay!");
	
		// If we've already done the flashback, or if we're past the point
		// where the flashback can happen, we just sleep like a regular inn.
		if(  flags[F_BUM_FLASHBACK]!=0 || flags[F_LAB_FOUND_CRYSTAL]!=0 )
		{
			Inn();
			
			TextBox(0,	"Feeling better this morning, sir?",
						"I hope you had a good rest",
						"and please come again!");
			EntFinish();
			return;
		}
		
		FadeOutWSound( 200 ); //fade to black
		stopmusic();
		
		entity.get(playerent).setx(1*16);
		entity.get(playerent).sety(120*16); //warp darin to (1,120)
		
		VCCustomFilter( RGB(0,0,0), RGB(0,0,128) );
		VCScreenFilterLucent( 20 );
		
		
		// Dexter's the only party member that can be with you
		// at this point.  If he's in the party, kick'm out.
		if( CharInParty("Dexter") )
		{
			dex_ent = GetPartyEntity( "Dexter" ); //get dex's entity reference.
			RemovePlayer( "Dexter" );	//remove him from the party for now
			
			entity.get(dex_ent).sety(30000);
			
			settile( 6,119, 0, 750); //set the sleeping dexter tiles!
			settile( 7,119, 0, 751);
		}
	
		//
		// Move most of the townsfolk off the map.
		// They're all in bed, you see...
		// Ent #0 gets to stay, tho!
		for( i=1; i<entity.size(); i++ )
		{
			if( i != playerent ) {
				entity.get(i).speed = 0;
				entity.get(i).setx(58*16);
				entity.get(i).sety(100*16);
			}
		}
		
		FadeIn( 200 ); //fade in, now it's all BLUE! :o
	
		timer = 0;
		hooktimer( "ChirpyChirp" );
		
		//
		// We have a bunch of tiles to change and obstructions to place.
		//
		ChangeTilesForScene( 46,13 );
		ChangeTilesForScene( 37,27 );
		ChangeTilesForScene( 15,28 );
		ChangeTilesForScene( 17,46 );
		ChangeTilesForScene( 43,48 );
		ChangeTilesForScene( 37,64 );
		
		settile(37,9,1,0);
		settile(37,10,0,541);	setobs(37,10, 0);
		settile(63,64,0,101);	setobs(63,64, 1);
		settile(63,65,0,0);		setobs(63,65, 1);
		settile(63,66,0,103);	setobs(63,66, 1);
	
	
		entity.get(playerent).face = FACE_DOWN;
	
		TextBox(T_DARIN,	"I can't sleep... ",
							"...not when Crystal is in trouble.", 
					 		"I need to get some fresh air.");
	
		flags[F_BUM_NIGHT] = 1;
		EntFinish();
	}
	
	public static void ChangeTilesForScene( int x, int y ) {
		settile(x,y,		0, 586);
		settile(x+1,y,		0, 587);
		settile(x+2,y,		0, 588);
		settile(x,y+1,		0, 583);
	
	
		settile(x+1,y+1,	0, 584);
		setobs(x+1,y+1, 1); //obstruct this tile.
	
		settile(x+2,y+1,	0, 585);
	}
	
	public static void ChirpyChirp()
	{
		if( timer == 400 )
		{
			SoundChirpChirp();
		}
		
	
		if( timer == 550 )
		{
			SoundChirpChirp();
			timer=0;
		}
	}
	
	public static void Weapon_Shop()
	{
		EntStart();
		TextBox(0,	"I'm your friendly neighborhood",
					"arms dealer! Buy what you see,",
					"but you didn't get it here!");
	
		SetSellEquipmentShop(true);
		SetSellSupplyShop(false);
	
		MenuShop("Wand,Dagger,Staff,Brass_Pipe,Spear,Bracer,Cap,Hood,Headband,Leather_Vest,Garment,Buckler");
	
		TextBox(0,	"Thanks for stopping by.",
					"Remember, you don't know me!","");
		EntFinish();
	}
	
	public static void Item_Shop()
	{
		EntStart();
		TextBox(0,	"Welcome to my apothecary! Do",
					"you need a potion to help you",
					"join a deceased loved one?");
				
		SetSellEquipmentShop(false);
		SetSellSupplyShop(true);
				
		MenuShop("Herb,Medicine,Miracle_Brew,Starlight,Blur_Ring,Running_Boots");
	
		//MenuShopI("2,38,39,1,40,42");
		
	
		TextBox(0,	"Thank you. Come again!","","");
		EntFinish();
	}
	
	
	public static void Jukebox()
	{
		EntStart();
		jukebox_playa();
		EntFinish();
	}
	
	public static void Volume_Control()
	{
		int answer;
	
		
	
		EntStart();
		
		answer = Prompt( 0, "Would you like to turn to music","up or down?","",
							"PUMP IT UP!&Ow, my ears.");
		
		if( answer != 0) { //this is down
			
			if( flags[F_BUM_JUKE_VOLUME] == 0 ) {
				MenuAngryBuzz();
			} else {
				SoundSwitch();
				flags[F_BUM_JUKE_VOLUME]--;
			}
			
		} else { //this is up
			
			if( flags[F_BUM_JUKE_VOLUME] == 9 ) {
				MenuAngryBuzz();
			} else {
				SoundSwitch();
				flags[F_BUM_JUKE_VOLUME]++;
			}
		}
		
		setmusicvolume( flags[F_BUM_JUKE_VOLUME]*10 );
		settile(57,164,0,507+flags[F_BUM_JUKE_VOLUME]);
			
		EntFinish();
	}
	
	public static void Principal() 
	{
		EntStart();
		
		TextBoxM(0,	"Welcome to our proud and",
					"distinguished university! We",
					"provide great training.");
		TextBox(0,	"Visit our professors if you",
					"wish to enroll in a course.","");
					
		EntFinish();
	}
	
	public static void Tank() 
	{
		int answer;
		
		EntStart();
		
		TextBox(0,	"We train young people to use",
					"arms and to fight. Second",
					"amendment forever, man!");
					
		answer = Prompt(0,	"Do you want to train here and",
							"become manly and hulking like",
							"us? 150 "+moneyname+" per course.", "Yes&No");
		if( answer != 0)
		{
			TextBox(0,	"Fine! Run away from a good challenge",
						"like the communist hippie wimps you are!", "");
			EntFinish();
			return;
		}
		
		if( money < 150 )
		{
			TextBox(0,	"There are no intelligence requirements ",
						"for this course but you still need",
						"money!");
			EntFinish();
			return;
		}
		
		answer = 0;
	
		if( CharInParty("Galfrey") )
		{
			answer = Prompt(0,	"Who would like to train and",
								"increase their power?","",
								"Darin&Galfrey");	
		}
		
		SoundChaChing();
		TakeMoney(150);
	
		FadeOut(30);
		FadeIn(30);
		
		if( answer != 0)
		{
			GiveXP( "Galfrey",20 );
		}
		else 
		{
			GiveXP( "Darin",20 );
		}
		
	
		TextBox(0,	"Thanks! I hope to see you",
					"come take a course here again",
					"some time soon!");
					
		EntFinish();
	}
	
	public static void Geek() 
	{
		int answer;
		
		EntStart();
		
		TextBox(0,	"We train mechanics and",
					"gadgeteers here. All things",
					"good come from science!");
					
		if(	CharInParty("Sara") )
		{
			answer	= Prompt(0,	"Would Sara like to sit in on",
								"a class to sharpen her skills",
								"and expertise? 150 "+moneyname+".","Yes!&No.");
			if( answer != 0)
			{
				TextBox(0,	"Do not fear the future! You",
							"people shall be the first to",
							"die in the... er... bye!");
				EntFinish();
				return;
			}
	
			if( money<150 )
			{
				TextBox(0,	"The pursuit of truth is nice",
							"and all, but no money, no",
							"class. Scram!");
				EntFinish();
				return;
			}
	
			SoundChaChing();
			TakeMoney(150);
	
			FadeOut(30);
			FadeIn(30);
	
			GiveXP( "Sara",20 );
	
			TextBox(0,	"Thanks, Sara! It's good to",
						"know there's a few things I",
						"can teach you yet.");
		}
		else //no sara!
		{
			TextBox(0,	"Uh, no offense, but I wouldn't",
						"trust a-one of ya to screw in a ",
						"lightbulb.");
			TextBox(0,	"Come back when you have an engineer.",
						"or at least someone who can spell",
						"'engineer'.  I'm not picky.");
		}
		
		EntFinish();
	}
	
	public static void Wimp() 
	{
		String choices;
		int answer;
		
	
		EntStart();
		
		TextBox(0,	"Here is where we instruct the",
					"young people in the ways of",
					"magic.");
	
		if( CharInParty("Dexter") || CharInParty("Crystal") ) {
			
			choices = "nobody kthx!&";
			
			if( CharInParty("Dexter") ) {
				choices = choices + "Dexter&";
			}
			
			if( CharInParty("Crystal") ) {
				choices = choices + "Crystal";
			}
	
				answer = Prompt(0,	"So, who would like to spend a",
									"moment and better their mind?", 
									"A course costs 150 "+moneyname+"."
									,choices);
	
				if( answer != 0) //you chose someone, let's check 
				{
	
					if( money<150 )
					{
						TextBox(0,	"Trying to learn magic tricks",
									"for free now? Get out, you",
									"cheapskate kids!" );
						EntFinish();
						return;
					}
					else
					{
						SoundChaChing();
						TakeMoney(150);
						
						FadeOut(30);
	 					FadeIn(30);
	 					
	 					if( answer == 2 || CharInParty("Crystal") ) //has to be Crystal.
	 					{ 
							  GiveXP( "Crystal",20 );
						} 
						else //has to be dexter.
						{
							GiveXP( "Dexter",20 );
						}
						
						TextBox(0,	"I hope this was enlightening for you. ",
									"Please come again to learn even deeper",
									"wisdom!");
					}
	
				}
				else //you said 'no'
				{
					TextBox(0,	"I understand.",
								"Tampering with the fabric of reality",
								"may not be everyone's cup of tea.");
					EntFinish();
					return;	
				}
	
		} else {
			TextBox(0,	"Hey sonny, stop wastin' my time.",
						"You're about as magical as",
						"A bowl of Lucky Charms.");
			TextBox(T_DARIN, "But they're magi...","","" );
			TextBox(0,	"DON'T finish that sentance, boy.",
						"I can vaporize you, your family, and your", 
						"dog without batting a lash.");
			TextBox(T_DARIN, "...Leave Fluffykins out of this!","","" );
	 	}
	 	EntFinish();
	}
	
	public static void Dexters_Dad() {
		EntStart();
		TextBoxM(0,	"Where is that worthless little puke? ",
					"I oughta disown that ungrateful ninny",
					"of a son!");
					
		TextBoxM(0,	"I paid good money for him to go to school," ,
					"and all he studies is magic!", "");
		TextBoxM(0,	"Muttering nonsense with others wearing",
					"silk bathrobes...",
					"it's just not right!");
		TextBoxM(0,	"I hope I never see that wimpy,",
					"worthless boy again!",
					"He'll never be a real man!");
		TextBox(0,	"Oh, my job? I'm the florist.","","");
	
		
		if( CharInParty("Dexter") )
		{
			TextBoxM(T_DEXTER,	"But, Dad! The world is danger of being",
								"destroyed by an emissary from the",
								"Dark World!");
	
			TextBox(T_DEXTER,	"I want to help in any way I can with the ",
								"forces of magic!","");
	
	
			TextBox(0,	"So there you are, you ungratefuly pansy!",
						"Where are your sissy mage friends?", "");
	
			TextBox(T_DEXTER,	"He's just not listening.",
								"Let's go, Darin.","");
		}
		EntFinish();
	}
	
	public static void Dexters_Mom() {
		EntStart();
		TextBox(0,	"My husband goes on like that,",
					"but I think he loves Dexter",
					"very much in his heart.");
					
		if( CharInParty("Dexter") )
		{
			TextBox(T_DEXTER,	"I know that, Mom. Maybe after",
								"I help Darin destroy Lord",
								"Stan he'll respect me more.");
		}	
		EntFinish();
	}
	
	public static void little_daddy_bruce() {
		int answer, darin;
	
		
		EntStart();
		
		if( flags[F_FASHION_MADEOVER] !=0) //if you're already 'pretty'
		{
			TextBox(0,	"How's the new attire working",
						"out for you today? You look",
						"stunning, you really do!");
			EntFinish();
			return;
		}
		
	
		if( flags[F_FASHION_PURCHASE] !=0) //we've bought the salon time!
		{
			if( flags[F_FASHION_CLOTHES]==0 )
			{
				TextBox(0,	"Go tell the stylists what you",
							"want done, then return here.",
							"You haven't even gotten new duds!");
				EntFinish();
				return;
			}
			
			if( flags[F_FASHION_HAIR]==0 )
			{
				TextBox(0,	"Go tell the stylists what you",
							"want done, then return here.",
							"You haven't even gotten a haircut!");
				EntFinish();
				return;
			}
			
			if( flags[F_FASHION_CAPE]==0 )
			{
				TextBox(0,	"Go tell the stylists what you",
							"want done, then return here.","");
				EntFinish();
				return;
			}
		
	
			TextBox(0,	"Are you all ready? Ok, just",
						"close your eyes and let us",
						"do our magic!");
		
	
			FadeOut(30);
			
			darin = GetPartyEntity( "Darin" );
			
			if(flags[F_FASHION_CLOTHES]==1 && flags[F_FASHION_HAIR]==1 && flags[F_FASHION_CAPE]==1)
			{
				changeCHR(darin,"res\\chrs\\DARIN2.CHR");
			}
			
			if(flags[F_FASHION_CLOTHES]==1 && flags[F_FASHION_HAIR]==1 && flags[F_FASHION_CAPE]==2)
			{
				changeCHR(darin,"res\\chrs\\DARIN5.CHR");
			}
			
			if(flags[F_FASHION_CLOTHES]==2 && flags[F_FASHION_HAIR]==1 && flags[F_FASHION_CAPE]==1)
			{
				changeCHR(darin,"res\\chrs\\DARIN7.CHR");
			}
			
			if(flags[F_FASHION_CLOTHES]==2 && flags[F_FASHION_HAIR]==1 && flags[F_FASHION_CAPE]==2)
			{
				changeCHR(darin,"res\\chrs\\DARIN6.CHR");
			}
			
			if(flags[F_FASHION_CLOTHES]==1 && flags[F_FASHION_HAIR]==2 && flags[F_FASHION_CAPE]==1)
			{
				changeCHR(darin,"res\\chrs\\DARIN3.CHR");
			}
			
			if(flags[F_FASHION_CLOTHES]==1 && flags[F_FASHION_HAIR]==2 && flags[F_FASHION_CAPE]==2)
			{
				changeCHR(darin,"res\\chrs\\DARIN4.CHR");
			}
			
			if(flags[F_FASHION_CLOTHES]==2 && flags[F_FASHION_HAIR]==2 && flags[F_FASHION_CAPE]==1)
			{
				changeCHR(darin,"res\\chrs\\DARIN9.CHR");
			}
			
			if(flags[F_FASHION_CLOTHES]==2 && flags[F_FASHION_HAIR]==2 && flags[F_FASHION_CAPE]==2)
			{
				changeCHR(darin,"res\\chrs\\DARIN8.CHR");
			}
			
			FadeIn(30);
			TextBox(0,	"You look absolutely fabulous!",
						"I hope you're happy with your",
						"new look!");
			
			if( PartySize() > 1 )
			{
				TextBox(T_DARIN,	"I dunno, guys. What do you",
									"think?","");
			}
			if( CharInParty("Sara") )
			{
				TextBox(T_SARA,	"Umm... what's a nice word",
								"for complete and total dork?","");
			}
			
			if( CharInParty("Dexter") )
			{
				TextBox(T_DEXTER,	"Err... I guess it's fine,",
									"Darin. Not my taste, though.","");
			}
	
			if( CharInParty("Crystal") )
			{
				TextBox(T_CRYSTAL,	"Darin, I love you for what's",
									"on the inside!","");
			}
			
			if( CharInParty("Galfrey") )
			{
				TextBox(T_GALFREY,"Ha ha ha ha ha ha ha!","","");
			}
			
			TextBox(T_DARIN,	"Umm...","","");
			
			TextBox(0,	"I gave your old clothes to",
						"Charlene over there. You have",
						"a nice day now!");
			
			flags[F_FASHION_MADEOVER] = 1;
			
			EntFinish();
			return;
		}
		
		if(flags[F_FASHION_PURCHASE]==0)
		{
			
			TextBox(0,	"Well, hello there! My name is",
						"Little Daddy Bruce and this is",
						"my fabulous fashion studio!");
			TextBox(0,	"Here we dare you to explore",
						"the stylish and trendy You.",
						"Please have a look around.");
			TextBox(0,	"We have beauticians from all",
						"over the world who want to",
						"make you look your best.");
			answer = Prompt(0,	"Would you like a complete",
								"hair and wardrobe makeover?",
								"It's only 30 "+moneyname+"!", "Yes&No");
	
	
			if( answer != 0)
			{
				TextBox(0,	"Oh, well I am so sorry to",
							"hear that. Please come back",
							"when you want to be pretty.");
				EntFinish();
				return;
			}
	
			if( money<30 )
			{
				TextBox(0,	"Oh sorry, hon. We can't make",
							"you into a creature of beauty",
							"without money. Sorry.");
				EntFinish();
				return;
			}
			
			SoundChaChing();
			TakeMoney( 30 );
			
			TextBox(0,	"Fabulous! Well, when you are",
						"ready, go and meet the skilled",
						"people you'll be working with!");
			TextBox(0,	"In the left booth is Trevor,",
						"our wardrobe consultant. He'll",
						"pick out some nice clothes.");
			TextBox(0,	"In the center booth is Alan.",
						"He'll be working with your",
						"hair to create a new you!");
			TextBox(0,	"In the right booth is James.",
						"He has dyes from faraway lands",
						"to fix up that cape of yours.");
			TextBox(0,	"After you've visited each of",
						"them, return here and we'll",
						"turn you into a new man!");
			TextBox(0,	"Before you begin, feel free to",
						"set the mood with our jukebox",
						"over there. Thanks again!");
	
			flags[F_FASHION_PURCHASE] = 1; //we've purchased the goods!
		} 
		
		EntFinish();
	}
	
	public static void fashion_babe() {
		int answer, darin;
		
		EntStart();
		
		if(flags[F_FASHION_MADEOVER]==0)
		{
			TextBox(0,	"Hi! My name is Charlene. If",
						"there's anything you need in",
						"the studio, let me know.");
						
			EntFinish();
			return;
		}
		
		answer = Prompt(0,	"I have those ratty old clothes",
							"of yours right here. Do you",
							"really want them back?", "Yes&No");
		if( answer != 0)
		{
			TextBox(0,	"Smart move! You look better",
						"now anyway.","");
						
			EntFinish();
			return;
		}
		
		TextBox(0,"Oh, very well...","","");
		FadeOut(30);
		
		darin = GetPartyEntity( "Darin" );
		changeCHR(darin,"res\\chrs\\DARIN.CHR");
	
		FadeIn(30);
		
		TextBox(0,"I hope you come back soon!","","");
		
		//reset all the fashion-flags!
		flags[F_FASHION_PURCHASE]	= 0;
		flags[F_FASHION_HAIR]		= 0;
		flags[F_FASHION_CAPE]		= 0;
		flags[F_FASHION_CLOTHES]	= 0;
		flags[F_FASHION_MADEOVER]	= 0;
		
		EntFinish();
	}
	
	public static void clothes_guy() {
		int answer;
		
		EntStart();
		
		if(flags[F_FASHION_MADEOVER]!=0)
		{
			TextBox(0,	"Hello again! How are those",
						"new threads working out for",
						"you?");
						
			EntFinish();
			return;
		}
		
		if(flags[F_FASHION_PURCHASE]==0)
		{
			TextBox(0,	"Well, hello! My name is Trevor",
						"and clothes are my thing.",
						"Please go see Bruce up front.");
						
			EntFinish();
			return;
		}
		
		if(flags[F_FASHION_CLOTHES] !=0)
		{
			TextBox(0,	"I'm getting ready with the",
						"clothes you requested. See",
						"you in a bit!");
			
			EntFinish();
			return;
		}
		
		TextBoxM(0,	"Hello! Let me welcome you as",
					"a valued customer. Oh boy! Are",
					"you dressed in rags or what?");
		TextBoxM(0,	"I think I can help you. Are",
					"you into the greek toga look?",
					"I have some great designs.");
		TextBoxM(0,	"Oh! I also have the cutest",
					"purple tunic you've ever seen!",
					"It just screams 'violet'.");
		
	
		answer = Prompt(0,	"So what will it be? The white",
							"toga or the purple tunic?","",
							"Toga&Tunic");
		flags[F_FASHION_CLOTHES] = answer+1;
		
		TextBox(0,	"Fabulous! I'll get that sized",
					"for your measurements then.","");
		
		EntFinish();
	}
	
	public static void hair_stylist() {
		int answer;
	
		
	
		EntStart();
		
		if( flags[F_FASHION_MADEOVER] !=0 )
		{
			TextBox(0,	"Hello again! How is that",
						"new haircut working out for",
						"you?");
			
			EntFinish();
			return;
		}
		
	
		if( flags[F_FASHION_PURCHASE]==0 )
		{
			TextBox(0,	"Well, hello! My name is Alan",
						"and hair is my thing.",
						"Please go see Bruce up front.");
			
			EntFinish();
			return;
		}
		
		if( flags[F_FASHION_HAIR]!=0 )
		{
			TextBox(0,	"I'm getting ready with the",
						"style you requested. See",
						"you in a bit!");	
			EntFinish();
			return;
		}
		
		TextBox(0,	"Hello! Let me welcome you as",
					"a valued customer. Yeesh! I",
					"guess you don't use shampoo?");
		
	
		TextBox(0,	"I think I can help you. Have",
					"you even considered red? With",
					"a purple band, it'll be cute.");
		TextBox(0,	"Or we could just chuck the",
					"headband and go with the black",
					"slicked-back look.");
		
	
		answer = Prompt(0,	"So what will it be? The rad",
							"red punk or the cool slicked",
							"black image?","Punk Red&Slick Black");
							
		flags[F_FASHION_HAIR] = answer+1;
		
		TextBox(0,	"Lovely! I'll get the coloring",
					"and scissors ready.","");
		
		EntFinish();
	}
	
	public static void cape_dyer() {
		int answer;
	
		
	
		EntStart();
		
		if(flags[F_FASHION_MADEOVER]!=0)
		{
			TextBox(0,	"Greetings! How is that",
						"new cape working out for",
						"you?");
			EntFinish();
			return;
		}
		
		if(flags[F_FASHION_PURCHASE]==0)
		{
			TextBox(0,	"Hi, there! My name is James",
						"and I specialize in dyes.",
						"Please go see Bruce up front.");
			EntFinish();
			return;
		}
		
		if(flags[F_FASHION_CAPE]!=0)
		{
			TextBox(0,	"I'm getting ready with the",
						"color you requested. See",
						"you in a bit!");
			EntFinish();
			return;
		}
		
		TextBoxM(0,	"Hello! Let me welcome you as",
					"a valued customer. Ooo! I do",
					"love a man wearing a cape!");
		TextBoxM(0,	"Capes are stylish for you hero",
					"types, but blue is *so* last",
					"week.");
		TextBox(0,	"I have both orange and black",
					"dyes here which I could use to",
					"salvage that fine fabric.");
		
	
		answer = Prompt(0,	"So what will it be? Orange",
							"dye or black dye?","","Orange&Black");
		
	
		flags[F_FASHION_CAPE]=answer+1;
		
	
		TextBox(0,	"Splendid! I'll get the water",
					"and dye bases ready.","");
	
		EntFinish();
	}
	
	public static void music_man() {
		EntStart();
		
		TextBox(0,	"This is our bitchin' stereo",
					"system! Feel free to set the",
					"mood for your experience.");
		TextBox(0,	"Use the left dial to select",
					"a song, and the right dial to",
					"set the volume.");
		
		EntFinish();
	}
	
	public static void student_a() {
		EntStart();
		TextBox(0,	"We're real fighters! Our",
					"classes involve bench pressing",
					"cattle and eating gravel!");
		EntFinish();
	}
	
	public static void student_b() {
		EntStart();
		TextBox(0,	"The boys and I are gonna stay",
					"after class and beat up the",
					"mages for their lunch money.");
		EntFinish();
	}
	
	public static void student_c() {
		EntStart();
		TextBox(0,	"There was a girl in this class",
					"once, but she was smarter than",
					"the teacher, so she left.");
		EntFinish();
	}
	
	public static void student_d() {
		EntStart();
		TextBox(0,	"I'm working on a top secret",
					"project for next week's",
					"science fair!");
		TextBox(0,	"It's called a 'video game' and",
					"will rot and control the minds",
					"of all it enslaves!");
		EntFinish();
	}
	
	public static void student_e() {
		EntStart();
		TextBox(0,	"I'm pretty dumb for signing up",
					"in a magic class in a world",
					"that has no mana.");
		TextBox(0,	"*sigh*... and last month's",
					"tuition is overdue as well.","");
		EntFinish();
	}
	
	public static void student_f() {
		EntStart();
		TextBox(0,	"I am, like, so totally killer",
					"with magic. I should, like,",
					"go adventuring or whatever.");	
		EntFinish();
	}
	
	
	
	public static void Sleeping_Dexter() {
		EntStart();
		
		// If Dexter has joined your party *and* we're during the 
		// Bumsville nighttime scene, then this event can execute!
		if( flags[F_MOUNT_DEX_JOIN]!=0 && flags[F_BUM_NIGHT]!=0 )
		{
			TextBox( T_DEXTER, "...M...meteo?", "That Light has bestowed upon me...", "the greatest... mmph.. mmm..." );
			TextBox( T_DARIN, 	"Aww, he looks like he's having sweet","dreams.","Best not to wake him.");
		}
		
		EntFinish();
	}
	
	public static void veggiesmith() {
	
		int save_vol = V1_GetCurVolume(); //save the current volume to restore later	
	
		EntStart();
	
		if( flags[F_CARROT_QUEST] == 2 )
		{
			TextBox(0,	"Hello again, mister!",
						"I hope the sacred weapon is serving",
						"you well.");
			EntFinish();
			return;
		}
		
		TextBoxM(0,	"Bah! What do you want from me? ",
					"I ain't got nothin' to sell folks anymore.", "");
		TextBoxM(0,	"Everyone's making weapons from",
					"metal nowadays!",
					"The people don't respect veggies no more.");
		TextBoxM(0,	"I remember when wars were fought with",
					"radish bombs and celery spears!", "");
		TextBox(0,	"What I'd give for a chance to re-discover",
					"the destructive potential of veggies",
					"again...");
		
	
		if( flags[F_VEGGIESMITH]==0 )
		{	
			TextBox( T_DARIN, "So, you're a Vegetable Smith?","","");
			TextBox( 0, "Yes sir. My whole life.","","");
			TextBox( T_DARIN, "...we usually call you guys 'chefs'.","","" );
			TextBox( 0, "If you prefer, you can call me","'High Lord Sandwich Engineer'.","" );
		}
	
	
		if( HasItem("Sacred_Carrot") )
		{
			TextBoxM(0,	"Wait a minute... what's that you have",
						"there?",
						"My god, that is one mighty carrot!");
						
			TextBox(0,	"Allow me to construct a weapon of mass",
						"destruction with it!",
						"You will be pleased!");
						
			FadeOut(30);
			FillVCLayer( RGB(0,0,0) );
			
			SoundShing();
			Wait(50);
			SoundShing();
			Wait(50);
			SoundShing();
			Wait(50);
			
			ClearVCLayer();
			FadeIn(30);
			
			TextBox(0,	"Here you are, sir!",
						"Cherish this sword and keep it at your",
						"side always." );
	
			V1_FadeOutMusic( 100 );
	
			DestroyItem( "Sacred_Carrot" );
			SoundGrandFanfare();
		
			FindItem( "Carrot_Blade", 1 );
			Wait(500);
			
			V1_FadeInMusic( 100, save_vol );//restore the volume
			
			
			flags[F_CARROT_QUEST] = 2;
		}	
		
		V1_SetCurVolume(save_vol);
		
		EntFinish();
	}
	
	
	
	public static void person_a()
	{
		EntStart();
		TextBox(0,"Hi! Are you room service?","","");
		EntFinish();
	}
	
	public static void person_b()
	{
		EntStart();
		TextBox(0,	"Isn't this just the cutest,",
					 "quaintest bed and breakfast",
					 "you've ever seen?");
		EntFinish();
	}
	
	public static void person_c()
	{
		EntStart();
		TextBox(0,	"Trust me, dude. Do NOT steal",
			         "towels from this place. They",
			         "come after you with knives.");
		EntFinish();
	}
	
	public static void jukebox_playa() 
	{
		int switch_var;
	
		MenuHappyBeep();
		
		setmusicvolume( flags[F_BUM_JUKE_VOLUME]*10 );
		
		switch_var = flags[F_BUM_JUKEBOX];
	
		switch( switch_var ) 
		{
			case 0:
				settile(55,164,0,508);
				playmusic("res/music/AURORA.MOD");
				TextBox(0,"Now playing 'Hymn to Aurora'.","","");
				break;
			case 1:
				settile(55,164,0,509);
				playmusic("res/music/VANGELIS.MOD");
				TextBox(0,	"Now playing 'Inventions of History'.","","");
				break;
			case 2:
				settile(55,164,0,510);
				playmusic("res/music/EXAGE.MOD");
				TextBox(0,"Now playing 'Flying Into Darkness'.","", "");
				break;
			case 3:
				settile(55,164,0,511);
				playmusic("res/music/NONEXIST.MOD");
				TextBox(0,"Now playing 'Sparks and Piping'.","","");
				break;
			case 4:		
				settile(55,164,0,512);
				playmusic("res/music/MEDIOEVA.MOD");
				TextBox(0,"Now playing 'Dance of the Sunlight'.","", "");
				break;
			case 5:
				settile(55,164,0,513);
				playmusic("res/music/CR_GUIT.S3M");
				TextBox(0,"Now playing 'Cold Separation'.","","");
				break;
			case 6:
				settile(55,164,0,514);
				playmusic("res/music/DISCO.S3M");
				TextBox(0,"Now playing 'Shack of Love'.","","");
				break;
			case 7:
				settile(55,164,0,515);
				playmusic("res/music/DREAMS2.S3M");
				TextBox(0,"Now playing 'Ascent of the Mountain Eagle'.","", "");
				break;
			case 8:
				settile(55,164,0,516);
				playmusic("res/music/MYSTWATR.S3M");
				TextBox(0,"Now playing 'Ocean Rhapsody'.","","");
				break;
			case 9:
				settile(55,164,0,507);
				playmusic("res/music/SYMPHONY.S3M");
				TextBox(0,"Now playing 'Clash of the Titans'.","","");
				flags[F_BUM_JUKEBOX] = 0-1;
				break;
		}
		
		flags[F_BUM_JUKEBOX]++;
	}
	
}	
