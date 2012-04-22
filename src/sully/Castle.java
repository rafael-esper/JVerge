package sully;

import static core.Script.*;
import static sully.Flags.*;
import static sully.Sully.*;

import static sully.vc.v1_rpg.V1_RPG.*;
import static sully.vc.v1_rpg.V1_Music.*;

import static sully.vc.simpletype_rpg.Data.*;
import static sully.vc.simpletype_rpg.Party.*;
import static sully.vc.simpletype_rpg.Inventory.*;
import static sully.vc.v1_rpg.V1_Maineffects.*;
import static sully.vc.v1_rpg.V1_Textbox.*;
import static sully.vc.v1_rpg.V1_Weather.*;
import static sully.vc.v1_rpg.V1_Simpletype.*;
import static sully.vc.Sfx.*;
import static sully.vc.util.Camscroll.*;
import static sully.vc.util.General.*;

public class Castle {
	
		
	public static void start() 
	{
		//if( ! (flags[F_RODNE_FLASHBACK] ||flags[F_LAB_SAVE_CRYSTAL]) )	 //bad for compiler
		//DeMorgan, I'll see you in HELL!
		//if( (flags[F_RODNE_FLASHBACK] || flags[F_LAB_SAVE_CRYSTAL]) == 0 ) //also bad
		
		Sully.SaveDisable();
		
		heck_upkeep();
		
		InitMap();
	
		// if we're not in rodne-flashback mode
		// then we'll play our music
		if( flags[F_RODNE_FLASHBACK]==0 )
		{
			V1_StartMusic("res/music/EXAGE.MOD");
			Banner("Castle Heck",300);
		}
	}
	
	public static void galfrey_event() {
	
	
		if( flags[F_RODNE_FLASHBACK]!=0 )
		{
			if( flags[F_FLASH_GALFREY]==0 )
			{
				TextBox(T_GALFREY,	"Hey, do you know if this is where",
									"applicants for the job are supposed to",
									"wait?" );
				TextBox(T_SARA,	"Err, no.  Sorry.","","" );
				
				flags[F_FLASH_GALFREY] = 1;
			}
			else if( flags[F_FLASH_GALFREY] == 1 )
			{
				TextBoxM(T_GALFREY,	"Mwahahahaha!!!","(ahem)","" );
				TextBoxM(T_GALFREY,	"","(ahem)","" );
				TextBox(T_GALFREY,	"_","_","...MWAHAHAHAHA!!!" );
				
				TextBox(T_SARA,	"What are you doing?!","","" );
				
				TextBox(T_GALFREY,	"Oh, I'm practicing for the interview.",
									"Man, I'm nervous.","" );
									
				TextBox(T_SARA,	"Ooookay...","","" );
				
				flags[F_FLASH_GALFREY] = 2;
			}
			else 
			{
				TextBox(T_SARA,		"No, I think I *won't* talk to the weirdo","again.","" );
				TextBox(T_GALFREY,	"Weirdo?","You're the one talking to yourself.","" );
				TextBox(T_SARA,		"Touche.","","" );
			}
			
			return;
		}
		
		EntStart();
		
		if( flags[F_HECK_ARMOR_ATTACK]!=0 )
		{
			TextBox(T_GALFREY,	"Run while you can, Darin!",
								"Everything depends on you now.",
								"Be brave, Darin!");
		}
		else //this is when you get Galfrey to join!  Yay!
		{
			Galfrey_Scene();
		}
		
		EntFinish();
	}
	
	public static void cyclops_plaque() // 3 //
	{
		if( flags[F_RODNE_FLASHBACK]!=0 )
		{
			TextBox(0,	"The cyclops guardian can show you the",
						"entrance. He needs the [Pearl of Truth] to",
						"see it.");
			
			TextBox(T_SARA,	"Hrmmm...","...Religious metaphor or Clever Advertising?", "" );
		}
		else
		{
			EntStart();
			
			TextBox(0,	"The cyclops guardian can show you the",
						"entrance. He needs the [Pearl of Truth] to",
						"see it.");
						
			EntFinish();
		}
	}
	
	public static void cyclops_statue() // 4 //
	{
		if( flags[F_RODNE_FLASHBACK]!=0 && flags[F_FLASH_TOEJAM]!=0 )
		{
			TextBox(T_SARA,	"I don't need to deal with this weird",
							"petrified Cyclops anymore.", "");
			return;
		}
	
		if( flags[F_RODNE_FLASHBACK]!=0 )
		{
			TextBox(T_SARA,	"Hmm... what a strange statue.",
							"I wonder where I am anyway?",
							"There's something down here.");
			TextBox(T_SARA,	"A ha! This slime under its feet seems like",
							"the ideal lubricant!",
							"I'll take some.");
		
	
			SoundTwinkle();
			
			Banner("Took Lubricant!",500);
			
			flags[F_FLASH_TOEJAM] = 1;
			return;
		}
		
		EntStart();
	 
		//Do this before Galfrey's joined.
		if( !CharInParty("Galfrey") )
		{
			TextBox(T_DARIN,	"What a suspicious looking statue!","","");
						
			EntFinish();
			return;
		}
		
		
		if( flags[F_HECK_OPEN_GATE]==0 ) //if the gate's open, do this...
		{
			if( HasItem("Pearl_of_Truth") )  //if we have the Pearl, let's open the gate!
			{
				SoundHealingWell(); //play the healing well sound!
	
				AlterFTile(20,30,423,2);
				AlterFTile(19,18,398,2);
				AlterFTile(20,18,399,2);
				AlterFTile(21,18,399,2);
				AlterFTile(22,18,400,2);
				AlterFTile(19,19,396,2);
				AlterFTile(20,19,210,0);
				AlterFTile(21,19,210,0);
				AlterFTile(22,19,397,2);
				
				AlterFTile(23,24,495,2);
				setzone( 23,24, 74 );
	
				TextBox(T_DARIN,	"A ha! The [Pearl of Truth] fits ",
									"perfectly, and the gate has opened!",
									"Hooray!");
	
				DestroyItem( "Pearl_of_Truth" );
	
				flags[F_HECK_OPEN_GATE] = 1;
			}
			else //if we don't have the pearl, insult darin! :D
			{
				TextBox(T_DARIN,	"So... we need the [Pearl of Truth]",
									"to get in?  Where could it be?",
									"");
				
				if( CharInParty("Galfrey") )
				{
					TextBox(T_GALFREY,	"Do you not listen, you infernal ",
										"dogooder?",
										"IT'S IN PARADISE ISLE.");
				}
			}
		}
		else //if we've already opened the gate, do this!
		{
				TextBox(T_DARIN,	"The [Pearl of Truth] opened the way.",
									"Lord Stan will feel the crushing blow",
									"of our justice now!");
		}
		
		EntFinish();
	}
	
	public static void heck_upkeep() // 0 //
	{
		if( flags[F_RODNE_FLASHBACK]==0 && flags[F_LAB_SAVE_CRYSTAL]==0 )
		{
			entity.get(0).setx(30000); //ditch galfrey if he's not sposta be there.
		}
		
		if(flags[F_HECK_GALFREY_JOIN]!=0)
		{
			entity.get(0).setx(30000); //move galfrey's mapent into THE NETHER ZONE.
		}
		
		if(flags[F_HECK_OPEN_GATE]!=0)
		{
			AlterFTile(20,30,423,2);
			AlterFTile(19,18,398,2);
			AlterFTile(20,18,399,2);
			AlterFTile(21,18,399,2);
			AlterFTile(22,18,400,2);
			AlterFTile(19,19,396,2);
			AlterFTile(20,19,210,0);
			AlterFTile(21,19,210,0);
			AlterFTile(22,19,397,2);
			
			AlterFTile(23,24,495,2);
			setzone( 23,24, 74 );
		}
	
		if(flags[F_HECK_WALL_CLOSE]!=0)
		{
			AlterFTile(32,90,378,0);
			AlterFTile(33,90,378,0);
			AlterBTile(32,91,376,1);
			AlterBTile(33,91,376,1);
			AlterBTile(32,92,376,1);
			AlterBTile(33,92,376,1);
		}
	
		if( flags[F_HECK_B1_TORCH]!=0 )
		{
			AlterBTile(11,97,390,2);
			AlterBTile(8,103,379,0);
			AlterBTile(8,104,379,0);
		}
		
		
		if (flags[CHEST_HECK_B1A]!=0) settile(5,100, 0, 443);	
		if (flags[CHEST_HECK_B1B]!=0) settile(5,110, 0, 443);	
		if (flags[CHEST_HECK_B1C]!=0) settile(6,110, 0, 443);
		if (flags[CHEST_HECK_B1D]!=0) settile(7,110, 0, 443);
		if (flags[CHEST_HECK_B1E]!=0) settile(8,110, 0, 443);
		if (flags[CHEST_HECK_WEST]!=0) settile(49,101, 0, 443);
	
	
		if(flags[F_HECK_WEST_GATE]!=0)
		{
			AlterBTile(35,122,379,0);
			AlterBTile(35,123,379,0);
		}
	
		if( flags[F_HECK_DEXTER_JOIN]!=0 )
		{
			entity.get(1).setx(30000);
		}
	
	
	 Torch_Cleanup(F_HECK_TORCH_A,84,89,69);
	 Torch_Cleanup(F_HECK_TORCH_B,85,92,69);
	 Torch_Cleanup(F_HECK_TORCH_C,86,87,78);
	 Torch_Cleanup(F_HECK_TORCH_D,88,90,69);
	 Torch_Cleanup(F_HECK_TORCH_E,89,91,78);
	 Torch_Cleanup(F_HECK_TORCH_F,90,88,69);
	
		if(flags[F_HECK_PINNACLE]!=0)
		{
			AlterFTile(74,30,379,2);
		}
	
		int a;
	
		if(flags[F_HECK_WALLCRUSH]!=0)
		{
			for(a=48; a<=54; a++)
			{
				AlterBTile(112,a,195,1);
				AlterBTile(113,a,378,1);
				AlterBTile(116,a,378,1);
				AlterBTile(117,a,195,1);
			}
		}
	
	/* 
	 AlterParallax(flags[54],2,3);
	 
	
	
	 
	 CallEvent(62,57,5,100);
	 for(a,0,3,1)
	 {
	  CallEvent(62,a+58,b+5,110);
	 }
	 if(flags[63])
	 {
	  entity.LocY[6]+=30;
	 }
	 CallEvent(62,64,49,101);
	 CallEvent(62,65,84,54);
	 
	
	 
	 
	
	
		
		if(flags[74])
		{
			AlterFTile(62,128,0,2);
			AlterBTile(62,129,379,0);
			AlterFTile(63,128,451,0);
			AlterBTile(63,128,450,1);
			AlterFTile(70,128,0,2);
			AlterBTile(70,129,379,0);
			AlterFTile(69,128,451,0);
			AlterBTile(69,129,450,1);
			AlterFTile(63,129,457,1);
			AlterBTile(63,130,456,1);
			AlterFTile(69,129,459,1);
			AlterBTile(69,130,458,1);
		}
		
		if(flags[76])
		{
			AlterBTile(74,14,448,2);
			AlterBTile(74,11,376,2);
			AlterBTile(74,12,210,2);
		}
		CallEvent(64,77,3,68);
		CallEvent(64,79,31,61);
		CallEvent(64,78,59,67);
	
	
		if(flags[89])
		{
			AlterBTile(88,67,390,2);
			AlterBTile(80,71,501,2);
		}
	*/
	}
	
	public static void Galfrey_Scene() 
	{
		int galfrey, darin, sara, crystal;
	
		
		//This can only happen once the lab blows up!
		if( flags[F_LAB_BLOWN_UP]==0 )
		{
			return;
		}
		
		//only do this event ever once.
		if( flags[F_HECK_GALFREY_JOIN]!=0 ) {
			return;
		}
		
		galfrey = 0; //galfrey is mapentity idx 0 on this map.
		darin	= GetPartyEntity( "Darin" );
		sara	= GetPartyEntity( "Sara" );
		crystal	= GetPartyEntity( "Crystal" );
	
		entity.get(galfrey).specframe = 21;
		Wait(100);
		entity.get(galfrey).specframe = 0;
		entity.get(galfrey).face = FACE_DOWN;
		AutoOn();
	
	
		entitymove(darin,"X21 Y23 L0");
		entitymove(sara,"X20 Y24 U0");
		entitymove(crystal,"X20 Y22 D0");
		
		WaitForEntity( crystal );
		
		Wait(100);
		
	
		entity.get(galfrey).face = FACE_RIGHT;
		
	
		Wait(50);
		
	
		entity.get(galfrey).face = FACE_RIGHT;
		
	
		Wait(50);
		
	
		entity.get(galfrey).face = FACE_RIGHT;
		
	
		Wait(50);
		
	
		entity.get(galfrey).specframe = 27;
		
		TextBox(T_GALFREY,	"Darin! What are you doing here, you nosy",
							"do-gooder?","");
		TextBox(T_CRYSTAL,	"We've come to beat up your boss, Galfrey!",
							"There is no escape for you this time!", "");
							
		entity.get(galfrey).specframe=0;
		entity.get(darin).specframe=23;
		Wait(100);
		entity.get(darin).specframe=0;
		
		TextBox(T_DARIN,	"Wait... what are you doing outside the",
							"fortress? ",
							"You are vulnerable to heroes!");
							
		entity.get(galfrey).specframe=20;
		Wait(50);
		TextBox(T_GALFREY,	"Er... well... I accidentally locked my keys",
							"inside, and I've been stuck out here.", "");
		TextBox(T_SARA,	"Ha! What a pathetic excuse for a head",
						"sidekick.","");
		
	
		entity.get(galfrey).specframe=0;
		entity.get(darin).specframe=26;
		
		TextBox(T_DARIN,	"Galfrey, you evil person!",
							"You shall feel my the sharp sting of my",
							"blade's justice!");
							
		entity.get(darin).specframe=0;
		TextBox(T_CRYSTAL,	"Wait, Darin. If Galfrey cannot get us inside",
							"the castle, then defeating him is pointless.", "");
	
		entity.get(galfrey).face = FACE_UP;
		Wait(75);
		entity.get(galfrey).face= FACE_RIGHT;
		
		TextBox(T_GALFREY,	"Umm... yeah! That's right.",
							"Your only hope is the [Pearl of Truth].", "");
		
	
		TextBox(T_DARIN,	"What are you talking about?","","");
		
		TextBoxM(T_GALFREY,	"The ancient legends say that the cyclops",
							"can open the entrance to the castle.", "");
		TextBoxM(T_GALFREY,	"But it sees with the [Pearl of Truth],",
							"an artifact buried deep under Paradise",
							"Isle.");
		TextBox(T_GALFREY,	"I've been trying to locate the Pearl, but",
							"I cannot find the way to Paradise Isle.", "");
		
		entity.get(crystal).specframe=23;
		Wait(100);
		entity.get(crystal).specframe=0;
		
		TextBox(T_CRYSTAL,	"Geez... you're pretty stupid, then.",
							"Darin and I hang out there all the time.", "");
		
		entity.get(sara).face = FACE_LEFT;
		Wait(30);
		entity.get(sara).face = FACE_DOWN;
		Wait(50);
		
		entity.get(sara).specframe=25;
		
	
		TextBox(T_SARA,	"Well, it seems that neither us nor Galfrey",
						"is entering the castle without that Pearl.", "");
		
	
		entity.get(sara).specframe=0;
		
		entity.get(sara).face = FACE_RIGHT;
		Wait(30);
		entity.get(sara).face = FACE_UP;
		
		TextBox(T_DARIN,	"We can take you to Paradise Isle, but only",
							"if you let us in to destroy your evil army.", "");
		
		entity.get(galfrey).specframe=23;
		Wait(100);
		entity.get(galfrey).specframe=0;
		Wait(50);
		entity.get(galfrey).specframe=29;
		
		TextBox(T_GALFREY,	"Well, I don't trust you one bit, Darin, but ",
							"it seems I have no choice.", "");
		
		entity.get(crystal).specframe=22;
		
		TextBox(T_CRYSTAL,	"I don't know, Darin. Can we trust him?",
							"He's Lord Stan's big lackey.", "");
					
		TextBox(T_DARIN,	"It's ok, Crystal. Once we get the",
							"[Pearl of Truth], we can go inside and",
							"defeat Lord Stan!");
		
		entity.get(crystal).specframe=0;
		entity.get(galfrey).specframe=0;
		
		TextBox(T_GALFREY,	"Once I get to Paradise Isle, I can show",
							"you how to find the [Pearl of Truth].", "");
							
		TextBox(T_DARIN,	"You're pretty desperate, Galfrey.",
							"Anyway, we better get going!", "");
		
		entity.get(sara).speed = 50;
		entity.get(crystal).speed= 50;
		
		entitymove(sara,"R1 U0");
		entitymove(crystal,"R1 D0");
		WaitForEntity( crystal );
		
		Wait(100);
		entity.get(galfrey).specframe = 29;
		Wait(100);
		entity.get(galfrey).specframe = 0;
		Wait(100);
		
		entity.get(galfrey).obstruction 		= false;
		entity.get(galfrey).obstructable	= false;
		entity.get(galfrey).speed			= 50;
	
		
		entitymove(sara,"U2");
		//entitymove(crystal,"D1");
		entitymove(galfrey,"R1 U1");
		
		WaitForEntity(galfrey);
		
		entity.get(galfrey).setx(30000);
		
		entity.get(darin).face = FACE_DOWN;
	
		int i = IsCharacter( "Darin" );   	//get darin's cast index.
		i = master_cast[i].level + 1; 		//use the index to get his level, and add one to it.
		JoinParty("Galfrey", i );			//galfrey joins at Darin's level.
		
		AutoOff();
		
		TextBox(1,	"You know... I'm willing to bet the bridge",
					"across the North River is completed by",
					"now." ); 
					
		flags[F_HECK_GALFREY_JOIN] = 1;
		
		entity.get(sara).speed = 100;
		entity.get(crystal).speed= 100;
	}
	
	
	
	public static void to_overworld() // 2 //
	{
		V1_MapSwitch( "overworld.map",93,41,TBLACK );
	}
	
	public static void Main_Gate() {
		
		if( flags[F_RODNE_FLASHBACK]!=0 )
		{
			TextBox(T_SARA, "Hrm.  I guess nobody's home...","","");
		}
		else if( entity.get(playerent).gety() < (20*16) ) 
		{
			//There should be a nicer way to say "if this was stepped on to activate"
			
			WalkInside( 32,99 );
			Banner("Great Hall",300 );
	
			/*
			log( "======================================" );
			log( "Steppy!" );
			MessageBox( "Steppy!" );
			log( "event.tx: " + str(event.tx) );
			log( "event.ty: " + str(event.ty) );
			log( "event.zone: " + str(event.zone) );
			log( "event.entity: " + str(event.entity) );
			log( "event.param: " + str(event.param) );
			log( "");
			*/
	
			
		} 
		else //if we talked to the gates!
		{
			EntStart();
			
			TextBox(T_DARIN, "Oh man, he was right!","The gates *are* locked!","");
			
			EntFinish();
	 
	 		/*
			log( "======================================" );
			log( "AA" );
			MessageBox( "AA" );
			log( "event.tx: " + str(event.tx) );
			log( "event.ty: " + str(event.ty) );
			log( "event.zone: " + str(event.zone) );
			log( "event.entity: " + str(event.entity) );
			log( "event.param: " + str(event.param) );
			log( "" );
			*/
		}
	}
	
	
	public static void Main_Exit() // 6 //
	{
		WalkOutside(20,20);
	}
	
	public static void down_b1a() // 7 //
	{
		Warp( 2,106, TCROSS );
		Banner( "B1",300 );
	}
	
	public static void down_b1b() // 8 //
	{
		Warp( 16,106, TCROSS );
		Banner( "B1",300 );
	}
	
	public static void West_Ledge_Exit() 
	{
		Gold_Door_Open( "West_Tower_Key", 3,68, F_HECK_WEST_DOOR, 15,11, true );
	}
	
	public static void Spire_Enter() // 10 //
	{
		Gold_Door_Open( "Spire_Key", 31,61, F_HECK_SPIRE_DOOR, 86,112, false );
	}
	
	public static void East_Ledge_Exit() // 11 //
	{
		Gold_Door_Open( "East_Tower_Key", 59,67, F_HECK_EAST_DOOR, 25,11, true );
	}
	
	
	public static void up_1fa() // 12 //
	{
		Warp( 23,68,TCROSS );
	}
	
	public static void up_1fb() // 13 //
	{
		Warp( 42,68,TCROSS );
	}
	
	
	public static void West_Hall_Enter() // 14 //
	{
		WalkInside( 4,70 );
	}
	
	public static void West_Tower_Enter() // 15 //
	{
		WalkInside( 35,128 );
		Banner( "West Tower",300 );
	}
	
	public static void East_Hall_Enter() // 16 //
	{
		WalkInside( 61,69 );
	}
	
	public static void East_Tower_Enter() // 17 //
	{
		WalkInside( 87,81 );
		Banner( "East Tower",300 );
	}
	
	
	public static void West_Tower_Exit() // 18 //
	{
		if( flags[F_HECK_WEST_GATE]!=0 && !CharInParty("Dexter") )
		{
			TextBox( T_CRYSTAL, "Shouldn't we talk to Dexter?","","" );
			TextBox( T_DARIN, 	"...oh, is that who that was?","","" );
			
			entitymove(playerent, "U1");
			WaitForEntity(playerent);
		} 
		else 
		{
			WalkOutside( 4,11 );
		}
	}
	
	public static void West_Tower_2f()
	{
		Warp( 65,103, TCROSS );
		Banner( "2F",300 );
	}
	
	public static void West_Tower_1f()  // 20 //
	{
		Warp( 43,120, TCROSS );
	}
	
	public static void East_Tower_Exit() // 21 //
	{
		WalkOutside( 36,11 );
	}
	
	public static void East_Tower_2F() // 22 //
	{
		Warp( 86,47, TCROSS );
		Banner( "2F",300 );
	}
	
	public static void East_Tower_1F() // 23 //
	{
		Warp( 87,72, TCROSS );
	}
	
	
	public static void Spire_Exit() // 24 //
	{
		Warp( 32,63, TCROSS );
	}
	
	public static void Spire_Up_2F() // 25 //
	{
		Warp( 114,44,TCROSS );
		Banner( "Spire 2F",300 );
	}
	
	public static void Spire_Down_1F() // 26 //
	{
		Warp( 86,97,TCROSS );
	}
	
	public static void Spire_Up_3F() // 27 //
	{
		Warp( 66,134, TCROSS );
		Banner( "Spire 3F",300 );
	}
	
	public static void Spire_Down_2F() // 28 //
	{
	 Warp( 115,59,TCROSS );
	}
	
	public static void Spire_Down_4F() // 29 //
	{
		WalkOutside( 74,29 );
		Banner( "Pinnacle",300 );
	}
	
	public static void Spire_Down_3F() // 30 //
	{
		if( flags[F_HECK_PINNACLE]!=0 )
		{
			return;
		}
		
		WalkOutside(66,125);
	}
	
	
	public static void Wall_Close() // 31 //
	{
		int i, j;
		
		if(flags[F_HECK_WALL_CLOSE]==0)
		{
			EntStart();
			
			SoundBomb();
			
			AlterFTile(32,90,378,0);
			AlterFTile(33,90,378,0);
			AlterBTile(32,91,376,1);
			AlterBTile(33,91,376,1);
			AlterBTile(32,92,376,1);
			AlterBTile(33,92,376,1);
			
			
			for( i=20; i>=1; i-- )
			{
				Earthquake( 0,i, 5 );
			}
			
			TextBox(T_CRYSTAL,	"Oh no!",
								"The entry way has been sealed behind us!", "" );
			TextBox(T_DARIN,	"There's no turning back now, gang!","","" );
			
			flags[F_HECK_WALL_CLOSE]=1;
			
			EntFinish();
		}
	}
	
	
	public static void b1_torch() // 32 //
	{
		if( flags[F_HECK_B1_TORCH]==0 )
		{
			SoundHealingWell();
			
			AlterBTile(11,97,390,2);
			AlterBTile(8,103,379,0);
			AlterBTile(8,104,379,0);
			
			unpress(0);
			
			flags[F_HECK_B1_TORCH] = 1;
		}
	}
	
	
	public static void Chest_A() 
	{
		if( OpenTreasure(CHEST_HECK_B1A, 5,100, 443) )
		{
			FindItem( "West_Tower_Key", 1 );
		}
	}
	
	public static void Chest_B() 
	{
		if( OpenTreasure(CHEST_HECK_B1B, 5,110, 443) )
		{
			FindItem( "Pharaoh_Sceptre", 1 );
		}
	}
	
	public static void Chest_C() 
	{
		if( OpenTreasure(CHEST_HECK_B1C, 6,110, 443) )
		{
			FindItem( "Bronze_Armor", 1 );
		}
	}
	
	public static void Chest_D() 
	{
		if( OpenTreasure(CHEST_HECK_B1D, 7,110, 443) )
		{
			FindItem( "Miracle_Brew", 1 );
		}
	}
	
	public static void Chest_E() 
	{
		if( OpenTreasure(CHEST_HECK_B1E, 8,110, 443) )
		{
			FindItem( "Running_Boots", 1 );
		}
	}
	
	
	public static void West_Tower_Gate() // 38 //
	{
		if( flags[F_HECK_WEST_GATE]==0 )
		{
			EntStart();
			
			TextBox(T_DARIN,	"This gate is locked.","","");
			TextBox(T_GALFREY,	"No problem. They're latched from the",
								"outside, so getting in from this side",
								"is easy.");
			
			SoundSwitch();
			
			AlterBTile(35,122,379,0);
			AlterBTile(35,123,379,0);
			
			TextBox(T_GALFREY,	"Told you so.","","");
			flags[F_HECK_WEST_GATE]=1;
			
			EntFinish();
		}
	}
	
	
	public static void dexter_event() // 39 //
	{
		int i, j;
		
		if( flags[F_HECK_DEXTER_JOIN]!=0 ) return;
		
		int darin	= GetPartyEntity( "Darin" );
		int sara 	= GetPartyEntity( "Sara" );
		int crystal	= GetPartyEntity( "Crystal" );
		int galfrey	= GetPartyEntity( "Galfrey" );
		
	
		int bubba 	= 2; //bubba is mapent 0
		int dexter 	= 1; //dexter is mapent 1
		
		EntStart();
		
		AutoOn();
		entity.get(darin).face 		= FACE_UP;
		entity.get(crystal).face 	= FACE_UP;
		entity.get(sara).face 		= FACE_UP;
		entity.get(galfrey).face 	= FACE_UP;
	
		
		entity.get(dexter).specframe=21;
		Wait(50);
		entity.get(dexter).specframe=0;
		
		
		TextBox(T_DEXTER,	"Darin! You've come to rescue us!",
							"Thank you!","");
		TextBox(T_DARIN,	"Dexter! Big Daddy Bubba!",
							"What are you two doing here?","");
		TextBox(T_BUBBA,	"Like, that ungroovy Lord Stan launched",
							"an attack on my land and took us",
							"prisoner!");
		TextBox(T_DEXTER,	"Lord Stan felt threatened by Bubba being",
							"the only other great power of the world.", "");
		TextBoxM(T_CRYSTAL,	"This has serious implications for the world.",
							"The House of Bubba is well recognized, ",
							"and holds a great amount of respect.");
		TextBoxM(T_CRYSTAL,	"This action may create instability in the",
							"Imperial senate.  The Shelian Luminaries", 
							"are sure to use the confusion to stage a");
		TextBoxM(T_CRYSTAL,	"revolt.  ",
							"Furthermore, Lord Stan will probably lose",
							"some respect in the council of Remus.");
		TextBoxM(T_CRYSTAL,	"We may be looking at an",
							"unprecedented shift in the flow of power",
							"in this region.");
		TextBoxM(T_CRYSTAL,	"However, Lord Stan's clout in the kingdom",
							"of Vicaria will allow him to maintain",
							"himself, and possibly fight House Bubba ");
		TextBox(T_CRYSTAL,	"from other economic angles.", "", "");
		
	
		entity.get(sara).face=FACE_RIGHT;
		Wait(30);
		entity.get(sara).face=FACE_DOWN;
		Wait(30);
		entity.get(sara).specframe=24;
		Wait(30);
		entity.get(sara).specframe=0;
		Wait(30);
		entity.get(sara).specframe=24;
		Wait(30);
		entity.get(sara).specframe=0;
		Wait(30);
		entity.get(sara).specframe=23;
		Wait(150);
		
		TextBox(T_SARA,	"What the hell are you talking",
						"about?","");
						
		entity.get(sara).specframe=0;
		
		entity.get(sara).face = FACE_LEFT;
		Wait(30);
		entity.get(sara).face = FACE_UP;
		
		TextBox(T_DARIN,	"Umm... anyway, Dexter, we're raiding",
							"the castle right now.",
							"Care to join us?");
		
	
		entity.get(dexter).specframe=25;
		
		TextBox(T_DEXTER,	"I would like to fight with you, but I must",
							"stay and care for Big Daddy Bubba.",						"");
							
		entity.get(dexter).specframe = 0;
		entity.get(dexter).face = FACE_RIGHT;
		entity.get(bubba).face  = FACE_LEFT;
		
	
		TextBox(T_BUBBA,	"No, that's ok little funk master...",
							"I can't walk in these platform shoes",
							"anyway.");
		
	
		entity.get(dexter).face = FACE_DOWN;
		entity.get(bubba).face  = FACE_DOWN;
		
		TextBox(T_DEXTER,	"Well, all right then. ",
							"Now we stand a chance against Lord Stan.",
							"Count me in!");
		TextBox(T_DARIN,	"Right. We'll be back for you, Bubba!",
							"You can count on it.",
							"Let's go, team!");
		
		i = IsCharacter( "Darin" );   	//get darin's cast index.
		i = master_cast[i].level - 1; 	//use the index to get his level, and take one from it.
		
		j = IsCharacter( "Dexter" ); //get dexter's cast index.
		j = master_cast[j].level; 	//use the index to get his level.
	
		//if darins level minus i is greater than dexter's current level,
		//make him join at that level.
		if( i > j ) 
		{
			JoinParty( "Dexter", i ); //dexter joins at a level one less than darin's current.
		}
		else //this clause is here just for people that are wankers and like to break things! :D
		{
			JoinParty( "Dexter", j ); //let dexter keep his old level!	
		}
		
	
		entity.get(dexter).sety(30000);
		entity.get(dexter).visible = false; // rbp
		
	
		AutoOff();
		flags[F_HECK_DEXTER_JOIN] = 1; 
		
		EntFinish();
	}
	
	public static void bubba_event() // 40 //
	{
		EntStart();
	
		
	
		if( flags[F_HECK_DEXTER_JOIN]!=0 )
		{
			TextBox(T_BUBBA,	"Don't you worry about me, my little love",
								"kittens.",
								"I'll be fine until you return.");
		}
		else
		{
			TextBox(T_BUBBA,	"At last!  A most groovy rescue!", "", "");
		}
		
		entity.get(1).face = FACE_DOWN;
		
		EntFinish();
	}
	
	
	public static void West_Tower_Sign() // 41 //
	{
		EntStart();
		
		TextBox(0,	"It is said that when it looks",
					"hopeless, the hero Zorro will",
					"bring the wind to your steps.");
					
		EntFinish();
	}
	
	
	public static void West_Tower_Box() // 42 //
	{
		if( OpenTreasure(CHEST_HECK_WEST, 49,101, 443) )
		{
			FindItem( "East_Tower_Key", 1 );
		}
	}
	
	
	public static void East_Tower_Sign() // 43 //
	{
		EntStart();
		
		TextBox(0,	"Light the torches with care.",
					"Some will illuminate and",
					"others will burn.");
	         
		EntFinish();
	}
	
	public static void East_Tower_Box() // 44 //
	{
		if( OpenTreasure(CHEST_HECK_EAST, 84,54, 443) )
		{
			FindItem( "Spire_Key", 1 );
		}
	}
	
	public static void Torch_A() // 45 //
	{
		Master_Torch( F_HECK_TORCH_A,84,89,0 );
	}
	
	public static void Torch_B() // 46 //
	{
		Master_Torch( F_HECK_TORCH_B,85,92,0 );
	}
	
	public static void Torch_C() // 47 //
	{
		Master_Torch( F_HECK_TORCH_C,86,87,1 );
	}
	
	public static void Torch_D() // 48 //
	{
		Master_Torch( F_HECK_TORCH_D,88,90,0 );
	}
	
	public static void Torch_E() // 49 //
	{
		Master_Torch( F_HECK_TORCH_E,89,91,1 );
	}
	
	public static void Torch_F() // 50 //
	{
		Master_Torch( F_HECK_TORCH_F,90,88,0 );
	}
	
	
	public static void start_sparklies( int x, int y ) 
	{
		SoundMagic2();
	
		VCPutIMG("res/images/story_fx/SPARKLE1.PCX",x, y);
		Wait(10);
		ClearVCLayer();
		VCPutIMG("res/images/story_fx/SPARKLE2.PCX",x, y);
		Wait(10);
		ClearVCLayer();
		VCPutIMG("res/images/story_fx/SPARKLE1.PCX",x, y);
		Wait(10);
		ClearVCLayer();
		VCPutIMG("res/images/story_fx/SPARKLE2.PCX",x, y);
		Wait(10);
		ClearVCLayer();
		VCPutIMG("res/images/story_fx/SPARKLE3.PCX",x, y);
		Wait(10);
		ClearVCLayer();
		VCPutIMG("res/images/story_fx/SPARKLE4.PCX",x, y);
		Wait(10);
		ClearVCLayer();
		VCPutIMG("res/images/story_fx/SPARKLE3.PCX",x, y);
		Wait(10);
		ClearVCLayer();
		VCPutIMG("res/images/story_fx/SPARKLE4.PCX",x, y);
		Wait(10);
		ClearVCLayer();
	}
	
	public static void finish_sparklies( int x, int y )
	{
		VCPutIMG("res/images/story_fx/SPARKLE5.PCX",x,y-8);
		VCPutIMG("res/images/story_fx/SPARKLE6.PCX",x,y+8);
		Wait(10);
		ClearVCLayer();
		VCPutIMG("res/images/story_fx/SPARKLE5.PCX",x,y-8);
		VCPutIMG("res/images/story_fx/SPARKLE6.PCX",x,y+8);
		Wait(10);
		ClearVCLayer();
		VCPutIMG("res/images/story_fx/SPARKLE5.PCX",x,y+8);
		VCPutIMG("res/images/story_fx/SPARKLE6.PCX",x,y-8);
		Wait(10);
		ClearVCLayer();
		VCPutIMG("res/images/story_fx/SPARKLE5.PCX",x,y-8);
		VCPutIMG("res/images/story_fx/SPARKLE6.PCX",x,y+8);
		Wait(10);
		ClearVCLayer();
	}
	
	//
	//
	//
	//
	public static void Stan_Taunt() 
	{
		int i, sara;
		
		if( flags[F_HECK_STAN_TAUNT]==0 )
		{
			EntStart();
			
			sara = GetPartyEntity( "Sara" );
			
			entitymove( playerent, "X86" );
			WaitForEntity( playerent );
	
			AutoOn();
			
			for(i=0; i<PartySize(); i++)
			{
				entity.get(master_cast[party[i]].entity).face = FACE_UP;
			}
			
			timer = 0;
			while( timer < 100 )
			{
				render();
				VCLayerTintScreen(RGB(0,0,0), 100-(timer/4));
				showpage();
			}
			
			V1_FadeOutMusic( 100 );
			
			start_sparklies( (86*16)-xwin, (100*16)-ywin-8 );
			
			entity.get(5).setx(86*16);
			entity.get(5).sety(100*16);
			
			render();
			
			finish_sparklies( (86*16)-xwin, (100*16)-ywin-8 );
			
			global_music_volume = 100;
			V1_StartMusic("res/music/BADEXPER.MOD");
			
			TextBox(T_STAN,		"Well, well. It seems that the rats infesting",
								"my castle made it farther than I thought.",
								"");
			TextBox(T_CRYSTAL,	"It's no use resisting us, Lord Stan!",
								"You will be defeated!", "");
			TextBox(T_STAN,		"Hmm... seems that brat Darin found where",
								"I was keeping you after all.",
								"Clever boy.");
			TextBox(T_GALFREY,	"Prepare to die, evil master of pooh!", "","");
			TextBoxM(T_STAN,	"Mwa ha ha! Come back to your senses,",
								"Galfrey?",
								"You were useful once.");
			TextBox(T_STAN,		"But now you will die with the rest of",
								"these scum!","");
			TextBox(T_DEXTER,	"I will teach you that there are",
								"consequences for trying to take over our",
								"land!");
			TextBox(T_DARIN,	"Come on, team! It's time for a Super",
								"Dooper Pooper Trooper Transform Extreme!", "");
			
			entity.get(sara).specframe=23;
			Wait(100);
			TextBox(T_SARA,	"Umm... no thank you.","","");
			entity.get(sara).specframe=0;
			
			TextBoxM(T_STAN,	"You can give up now, fools!",
								"You must have been clever to get this", "far... But you will never be able to survive");
			TextBoxM(T_STAN,	"the traps I have laid throughout this spire!",
								"I'll be waiting for you on the rooftop,", 
								"Darin.");
			TextBox(T_STAN,		"That is, if you make it there! Hahaha!", "", "");
	
	
			start_sparklies( (86*16)-xwin, (100*16)-ywin-8 );
			
			entity.get(5).setx(74*16);
			entity.get(5).sety(12*16); //move stanley into position
			render();
			
			finish_sparklies( (86*16)-xwin, (100*16)-ywin-8 );
			
			timer = 0;
			while( timer < 100 )
			{
				render();
				VCLayerTintScreen(RGB(0,0,0), 75+(timer/4));
				showpage();
			}
			
			VCLayerTintOff();
		
			V1_FadeOutMusic( 50 );
			V1_StartMusic( "res/music/EXAGE.MOD" );
			V1_FadeInMusic( 50, 100 );
			
			TextBox(T_DEXTER,	"What a geek!","","" );
			TextBox(T_DARIN,	"Agreed, Dexter, but we must be cautious from",
								"now on.",
								"Onward, team!");
								
			flags[F_HECK_STAN_TAUNT]=1;
			AutoOff();
			
			EntFinish();
		}
	}
	
	
	public static void Wall_Crush() // 52 //
	{
		int i,x,y,x1,x2,y1;
		int darin, sara, dexter, crystal, galfrey;
		
		EntStart();
		
		if( flags[F_HECK_WALLCRUSH]==0 )
		{
			darin	= GetPartyEntity( "Darin" );
			sara	= GetPartyEntity( "Sara" );
			dexter	= GetPartyEntity( "Dexter" );
			crystal	= GetPartyEntity( "Crystal" );
			galfrey	= GetPartyEntity( "Galfrey" );
			
			V1_StartMusic("res/music/BADEXPER.MOD");
			
			AutoOn();
			
			for(i=0; i<PartySize(); i++)
			{
				entitymove( master_cast[party[i]].entity ,"X114");
			}
			
			for(i=0; i<PartySize(); i++)
			{
				WaitForEntity(master_cast[party[i]].entity);
			}
			
			
			for(i=0; i<PartySize(); i++)
			{
				if( i%2!=0 ) {
					entity.get(master_cast[party[i]].entity).face = FACE_RIGHT;	
				} else {
					entity.get(master_cast[party[i]].entity).face = FACE_LEFT;	
				}
			}
			
			Wait(100);
			
			for(i=0; i<PartySize(); i++)
			{
				if( i%2!=0 ) {
					entity.get(master_cast[party[i]].entity).face = FACE_LEFT;	
				} else {
					entity.get(master_cast[party[i]].entity).face = FACE_RIGHT;	
				}
			}
			Wait(100);
			
			SoundBomb();
			
			for(i=0; i<PartySize(); i++)
			{
				entity.get(master_cast[party[i]].entity).specframe = 21;
			}
			
			for(i=10; i>0; i--)
			{
				Earthquake(i,0,15);
			}
			
			for(i=48; i<=54; i++)
			{
				AlterFTile(112,i,0,1);
				AlterFTile(117,i,0,1);
			}
			
			x1 = (112*16) - xwin;
			x2 = (117*16) - xwin;
			y1 = (48*16) - ywin;
			
			for( x=1; x<=16; x++ )
			{
				for( y=y1; y<=(y1+103); y+=16 )
				{
					blittile(x1+x,y,378, v1_vclayer);
					blittile(x2-x,y,378, v1_vclayer);
				}
				
				Wait(3);
				ClearVCLayer();
			}
			
			ClearVCLayer();
			
			for(i=48;i<=54;i++)
			{
				AlterFTile(113,i,378,1);
				AlterFTile(116,i,378,1);
			}
			
			AlterBTile(113,55,376,1);
			AlterBTile(113,56,376,1);
			AlterBTile(116,55,376,1);
			AlterBTile(116,56,376,1);
			
			for(i=0; i<PartySize(); i++)
			{
				entity.get(master_cast[party[i]].entity).specframe = 0;
			}
			
			TextBox(T_DEXTER,	"Egad! The walls have started",
								"to move inward. They're going",
								"to crush us! Help!");
			TextBox(T_GALFREY,	"Hey! At least they scroll",
								"smoothly this time! Count",
								"your blessings, mage boy!");
			TextBox(T_DARIN,	"They're moving fast! Galfrey!",
								"Dexter! You guys are strong.",
								"Try to hold the walls back!");
			TextBox(T_GALFREY,	"Naa... my arms are tired. Let",
								"the girls do it.","");
			TextBox(T_SARA,		"Crystal! Ready? It's time to",
								"cast the magic of Stone on",
								"us to hold the walls!");
			TextBox(T_CRYSTAL,	"Umm... wrong game. Let's just",
								"push on them instead.","");
	
			entitymove(galfrey,"X114 Y52 F1");
			entitymove(darin,"X114 Y53 F1");
			entitymove(dexter,"X114 Y54 F1");
			entitymove(sara,"X115 Y50 Z27");
			entitymove(crystal,"X114 Y50 Z26");
			
			WaitForEntity( dexter );
			
			TextBox(T_SARA,		"Urmph! This is a strong trap!",
								"Can't you push any harder,",
								"you delicate flower?");
			TextBoxM(T_CRYSTAL,	"Shut up! I'm doing the best",
								"I can, you fiend!","");
			TextBox(T_CRYSTAL,	"Darin, hurry! Take the guys",
								"with you and defeat Lord",
								"Stan on the rooftop!");
			TextBox(T_SARA,		"Don't worry about us, Darin.",
								"We'll hold this trap at bay.",
								"Go!");
			
			V1_StartMusic("res/music/EXAGE.MOD");
			
			entity.get(galfrey).face = FACE_DOWN;
			
			Wait(50);
			
			entity.get(galfrey).speed	= 75;
			entity.get(dexter).speed	= 75;
			
			entitymove(galfrey,"D1");
			entitymove(dexter,"U1");
			
			WaitForEntity( galfrey );
			
			RemovePlayer( "Sara" );
			RemovePlayer( "Crystal" );
			
			entity.get(3).incx(-(20*16)); //move the mapentity versions into place.
			entity.get(4).incx(-(20*16));
			
			entity.get(3).specframe=26; //set the mapents properly
			entity.get(4).specframe=27;
			AutoOff();
			
			entity.get(galfrey).speed=100;
			entity.get(dexter).speed =100;
			
			flags[F_HECK_WALLCRUSH] = 1;
		}
	 
	 	EntFinish();
	}
	
	public static void crystal_event() // 53 //
	{
		EntStart();
		
		if(flags[F_HECK_WALLCRUSH]!=0)
		{
			TextBox(T_CRYSTAL,	"Darin, save yourself! Hurry upstairs.",
								"And see if you can get R2-D2 to stop",
								"these walls.");
		}
		
		EntStart();
	}
	
	public static void sara_event() // 54 //
	{
		EntFinish();
		
		if(flags[F_HECK_WALLCRUSH]!=0)
		{
			TextBox(T_SARA,	"Darin, why did you have to have such",
							"a wimp for a girlfriend?",
							"Push harder, wuss!");
		}
		
		EntFinish();
	}
	
	
	
	public static void Living_Statues() // 55 //
	{
		EntStart();
		
		int dexter, galfrey;
	
		if( flags[F_HECK_ARMOR_ATTACK]==0 )
		{
			V1_StartMusic("res/music/BADEXPER.MOD");
	
			dexter	= GetPartyEntity( "Dexter" );
			galfrey	= GetPartyEntity( "Galfrey" );
			
			SoundShing();
			
			AlterFTile(62,128,0,2);
			AlterBTile(62,129,379,0);
			AlterFTile(63,128,451,0);
			AlterBTile(63,129,450,1);
			AlterFTile(70,128,0,2);
			AlterBTile(70,129,379,0);
			AlterFTile(69,128,451,0);
			AlterBTile(69,129,450,1);
			
			TextBox(T_GALFREY,	"Yikes!",
								"The suits of armor have come alive!",
								"They're attacking!");
			
			TextBox(T_DEXTER,	"I know! It's like some bad episode of ",
								"Hercules or something.", "");
								
			TextBox(T_DARIN,	"They're blocking the corridor.",
								"They won't let us pass!","");
			TextBox(T_GALFREY,	"Dexter!",
								"Break and attack pattern Delta Omega 3!","");
			TextBox(T_DEXTER,	"Check!","","");
			
			AutoOn();
			entitymove( galfrey,"X63 Y130" );
			entitymove( dexter,"X69 Y130" );
			WaitForEntity( dexter );
			
			entity.get(galfrey).specframe = 7;
			entity.get(dexter).specframe = 9;
			
			TextBox(T_GALFREY,	"Grr... my weapon has no",
								"effect on them!","");
			TextBox(T_DEXTER,	"Argh! My flame magic isn't",
								"affecting them at all!","");
			TextBox(T_GALFREY,	"But at least we seem to be",
								"holding them off.","");
			TextBox(T_DEXTER,	"Right. Darin! It's all up to",
								"you now. Hurry to the roof",
								"and confront Lord Stan!");
			TextBox(T_GALFREY,	"Our hopes go with you, Darin.",
								"We'll take care of these",
								"ninnies. Good luck!");
								
			TextBox(T_DARIN,	"...","","");
			
			RemovePlayer("Dexter");
			RemovePlayer("Galfrey");
			AutoOff();
			
			entity.get(0).setx(63*16);
			entity.get(0).sety(130*16);
			entity.get(1).setx(69*16);
			entity.get(1).sety(130*16);
			entity.get(0).specframe = 7;
			entity.get(1).specframe = 9;
			
			entity.get(1).script = "dex_finish";
			
			entity.get(playerent).face = FACE_DOWN;
			
			V1_StartMusic("res/music/EXAGE.MOD");
			flags[F_HECK_ARMOR_ATTACK]=1;
		}
		
		EntFinish();
	}
	
	
	
	public static void dex_finish() // 57 //
	{
		EntStart();
		
		TextBox(T_DEXTER,	"This is your only chance, Darin! ",
							"We'll be fine.",
							"Go now and face Lord Stan!");
							
		EntFinish();
	}
	
	
	
	public static void Final_Encounter() // 58 //
	{
		
		int save_vol = V1_GetCurVolume();
	 
		if(flags[F_HECK_FINAL_SPEECH]==0)
		{
			EntStart();
			
			camZoomToEntity(5, 50);
			
			V1_FadeOutMusic(100);
			stopmusic();
			setmusicvolume( save_vol );
			playmusic("res/music/SYMPHONY.S3M");
			
			TextBoxM(T_STAN,	"Welcome to your doom, you fool, Darin!",
								"Witness the last moments of your life!", "");
			TextBoxM(T_STAN,	"So it comes to this: ",
								"what is to be our decisive confrontation!",
								"");
			TextBoxM(T_STAN,	"I am Lord Stan,",
								"creator and destroyer of worlds!",
								"You can never hope to defeat me!");
			TextBoxM(T_STAN,	"You will be crushed beneath",
								"my omnipotence! There is no",
								"hope left for you!");
			TextBox(T_STAN,		"I am INVINCIBLE!",
								"Do you hear me?",
								"INVINCIBLE! INVINCIBLE! INVINCIBLE!");
			
			camReturnToPlayer(playerent, 50);
			
			flags[F_HECK_FINAL_SPEECH]=1;
			
			EntFinish();
		}
	
	}
	
	public static void Final_Lever() // 59 //
	{
		int i;
		EntStart();
		
		if(flags[F_HECK_FINAL_LEVER]==0)
		{
			SoundSwitch();
			
			AlterFTile(74,14,448,2);
			AlterFTile(74,12,195,2);
			
			entity.get(5).visible = false;
			
			SoundFall();
			TextBox(T_STAN,	"Ahhhhhhhhhhhhhh!","","");
			Wait(180); 
			SoundCrash();
			
			for(i=20; i<=0; i--)
			{
				Earthquake(0,i,12);
			}
			
			TextBox(1,"Hmm... that was pretty lame.","","");
			playmusic("res/music/AURORA.MOD");
			
			TextBox(0,			"And thus did Darin defeat the evil Lord Stan",
								"and restore peace, justice, and truth!", "");
			TextBox(T_DARIN,	"And I didn't even get to explore any caves. Dang.",
								"Oh well.", "" );
			
			FadeOut(60);
	
			
			/*
			FillVCLayer( RGB(0,0,0) );
	
			PrintCenter(160, 92, v1_vclayer, v1rpg_SmallFont, "- THE END -" );
			
			FadeIn(60);
			Wait(300);
			FadeOut(60);
			*/
			
			credits();
		}
		
		EntFinish();
	}
	
	public static void Stair_Close() // 60 //
	{
		if(flags[F_HECK_PINNACLE]==0)
		{
			SoundSwitch();
			
			AlterFTile(74,30,379,2);
			flags[F_HECK_PINNACLE]=1;
		}
	}
	
	
	// A master script to handle the Locked gold doors.
	// item_needed: 	the master_items name of the item required to open this door.
	// top_tx, top_ty:	the tile-based coordinates of the top-left corner of the door.
	// my_flag:			the flags index for this door-event.
	// Warp_x,Warp_y:	the coordinates to Warp to
	// int going_outside: 1 if going outside, 0 if going inside.
	public static void Gold_Door_Open( String item_needed, int top_tx, int top_ty, int my_flag, int Warp_x, int Warp_y, boolean going_outside ) // 63 //                                  // Master Gold Door Open
	{
		EntStart();
		
		if( HasItem(item_needed) )
		{
			SoundSwitch();
			
			AlterBTile( top_tx,top_ty,398,2);
			AlterBTile( top_tx+1,top_ty,399,2);
			AlterBTile( top_tx+2,top_ty,399,2);
			AlterBTile( top_tx+3,top_ty,400,2);
			AlterBTile( top_tx,top_ty+1,396,2);
			AlterBTile( top_tx+1,top_ty+1,195,0);
			AlterBTile( top_tx+2,top_ty+1,195,0);
			AlterBTile( top_tx+3,top_ty+1,397,2);
			
			TextBox(SpcByPos(0), "The key fits!","","");
			
			DestroyItem( item_needed );
			
			flags[my_flag]=1;
			
			EntFinish();
			return;
		}
			
		if( flags[my_flag]!=0 )
		{
			//this if-clause is a bad hack to prevent Warping from adj. act.
	//		if( entity.y[playerent] < (top_ty+32) )
	//		{
				if( going_outside )
				{
					WalkOutside( Warp_x, Warp_y );
				}
				else
				{
					WalkInside( Warp_x, Warp_y );
				}
	
				EntFinish();
				return;
	//		}
		}
		
		TextBox(SpcByPos(0), "Hmm... locked.","","");
		
		EntFinish();
	}
	
	
	/*
	
	event // 64 //                                  // Master Gold Door Auto-Exec
	{
	 if(flags[var(0)])
	 {
	  AlterBTile(var(1),var(2),398,2);
	  AlterBTile(var(1)+1,var(2),399,2);
	  AlterBTile(var(1)+2,var(2),399,2);
	  AlterBTile(var(1)+3,var(2),400,2);
	  AlterBTile(var(1),var(2)+1,396,2);
	  AlterBTile(var(1)+1,var(2)+1,195,0);
	  AlterBTile(var(1)+2,var(2)+1,195,0);
	  AlterBTile(var(1)+3,var(2)+1,397,2);
	 }
	}
	
	*/
	public static void Master_Torch( int flag, int x1, int x2, int zone ) // 65 //                                  // Master Tower Torch
	{
		int a,b,c,d;
	
		if( zone!=0 )
		{
			a = 195;
			b = 377;
			c = 78;
			d = 69;
		} 
		else 
		{
			a = 377;
			b = 195;
			c = 69;
			d = 78;
		}
		
		if( flags[flag]==0 )
		{
			SoundHealingWell();
			
			AlterBTile(x1,43,390,2);
			AlterFTile(x2,51,a,0);
			setzone(x2,51,c);
			flags[flag]=1;
			unpress(0);
		}
		else 
		{
			AlterBTile(x1,43,389,2);
			AlterFTile(x2,51,b,0);
			setzone(x2,51,d);
			unpress(0);
			flags[flag]=0;
		}
	 
	}
	
	public static void Torch_Cleanup( int flag, int x1, int x2, int zone ) // Master Torch Auto-Exec
	{
		if( flags[flag]!=0 )
		{
			AlterBTile( x1,43,390,2 );
			AlterFTile( x2,51,377,0 );
			setzone( x2,51, zone );
		}
	}
	
	
	
	public static void WalkOutside( int x, int y )
	{
	//flags[54]=0;
	//AlterParallax(flags[54],2,3);
	//MessageBox( "WalkOutside: needs parallax changing!" );
	
		current_map.renderstring = "1,2,E,R";
		Warp(x,y,TCROSS);
	}
	
	public static void WalkInside( int x, int y )
	{
	//flags[54]=0;
	//AlterParallax(flags[54],2,3);
	
	//MessageBox( "WalkInside: needs parallax changing!" );
	
		current_map.renderstring = "1,E,2,R";
	
		Warp(x,y,TCROSS);
	}
	
	/*
	
	event // 69 //
	{
	 if(flags[89]==0)
	 {
	  soundeffect(4);
	  AlterBTile(88,67,390,2);
	  AlterBTile(80,71,501,2);
	  flags[89]=1;
	 }
	}
	
	event // 70 //
	{
	 if(flags[89])
	 {
	  soundeffect(7);
	  Timer=0;
	  Wait(5);
	  While(Timer<100 AND Timer>4)
	  {
	   PaletteMorph(63,63,63,100-Timer,63);
	  }
	  Banner("Save Point",3);
	  EnableSave();
	 }
	}
	
	event // 71 //
	{
	 DisableSave();
	}
	
	event // 72 //
	{
	 if(flags[150]==0)
	 {
	  SoundEffect(8);
	 }
	 FakeBattle();
	}
	
	event // 75 //
	{
	 SoundEffect(22);
	 Warp(87,77,0);
	}
	*/
	
	public static void Zorro_Fall()
	{
		 Master_Fall( 35,120 );
	}
	
	public static void East_Fall()
	{
		Master_Fall( 87,77 );
	}
	
	
	public static void Master_Fall( int x, int y ) // 73 //
	{
		int face, lasttime = 0, i, y_pos;
		
	
		face = FACE_DOWN;
			
		y_pos = 112*16;
	
		EntStart();
		
		FadeOut( 5 );
		SoundFall();
	
		Warp( 133,113, TNONE );
	
		for( i=1; i<PartySize(); i++ )
		{
			entity.get(master_cast[party[i]].entity).sety(y_pos);
			
			y_pos -= 16;
		}
	
	 	FadeIn( 5 );
	 	
	 	timer=0;
	 	while( timer < 190 )
	 	{
			render();
			
			if( timer > (lasttime + 5) )
			{
				lasttime = timer;
				
				if( face == FACE_UP ) face = FACE_RIGHT;
				else if( face == FACE_RIGHT ) face = FACE_DOWN;
				else if( face == FACE_DOWN ) face = FACE_LEFT;
				else if( face == FACE_LEFT ) face = FACE_UP;
				
				for( i=0; i<PartySize(); i++ )
				{
					entity.get(master_cast[party[i]].entity).face = face;
				}
			}
			
			showpage();
		}
	
	 	FadeOut(5);
	
		Warp(x,y,TBLACK);
		
		EntFinish();
	}
	
	
	// The first time we visit this place with Darin, we do a dramatic pan-scene!
	//
	//
	public static void Pan_Intro()
	{
		EntStart();
		
		if( CharInParty("Darin") && flags[F_HECK_INTROPAN]==0 )
		{
			TextBox(T_DARIN,	"So this is it... the mighty Castle Heck.",
								"Lord Stan rules his evil empire from here.", "");
			
			V1_StartMusic("res/music/SYMPHONY.S3M");
			
			camCtrScrollToS(11*16, 8*16, 150); //scroll the screen to be ceneted on tile 11,8 at a rate of 150 px/sec
			camCtrScrollToS(35*16, 8*16, 150); //then on to 35,8...
			camReturnToPlayerS(playerent, 150); //and then back to the player.
			
			if( CharInParty("Sara") )
			{
				TextBox(T_SARA,	"What an impractical, ancient design!",
								"Hrmph.","");
			}
			
			if( CharInParty("Dexter") )
			{
				TextBox(T_DEXTER,	"It is indeed an immense and dreadful",
									"structure.","");
			}
			
			if( CharInParty("Crystal") )
			{
				TextBox(T_CRYSTAL,	"It is foreboding, but we must be strong if ",
									"we are to release Stan's grip on this", 
									"world.");
			}
			
			V1_StartMusic("res/music/EXAGE.MOD");
			
			flags[F_HECK_INTROPAN] = 1;
		}	
		
		EntFinish();
	}
}