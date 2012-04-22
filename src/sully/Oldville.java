package sully;

import static core.Script.*;
import static sully.Flags.*;
import static sully.Sully.*;

import static sully.vc.v1_rpg.V1_RPG.*;
import static sully.vc.v1_rpg.V1_Music.*;

import static sully.vc.simpletype_rpg.Party.*;
import static sully.vc.v1_rpg.V1_Maineffects.*;
import static sully.vc.v1_rpg.V1_Weather.*;
import static sully.vc.v1_rpg.V1_Textbox.*;
import static sully.vc.Sfx.*;
import static sully.vc.util.General.*;
import static sully.vc.v1_rpg.V1_Simpletype.*;

public class Oldville {

	public static void start()
	{
	 
		if( flags[F_FLASH_GEAR]!=0 )
		{
			AlterBTile(12,85,177,2);
		}
		
		InitMap();
	
		V1_StartMusic("res/music/VANGELIS.MOD");
		
		FlashIntro();
	
		Banner("Future Site of Rodent City",300);
	}
	
	public static void n_exit() /* 1 */
	{
		V1_MapSwitch("Overworld.MAP",25,74,0);
	}
	
	public static void rabbit() /* 2 */
	{
		EntStart();
		TextBoxM(T_BUNNY,	"Hey, Sara! What's up, toots?",
							"I've planted a blessed seed in",
							"the dirt patch over there.");
		TextBox(T_BUNNY,	"I'm hoping that a carrot grows,",
							"so don't change anything, ok?",
							"I'm counting on you!");
		EntFinish();
	}
	
	public static void bird() /* 3 */
	{
		EntStart();
		TextBoxM(T_BIRD,	"Hi, Sara! Some weird guy with",
							"a velvet jacket just passed by",
							"here, heading into the woods.");
		TextBox(T_BIRD,		"He mentioned something about",
							"building a cabin, and putting",
							"in some psychadelic lights.");
		EntFinish();
	}
	
	public static void rat() /* 4 */
	{
		EntStart();
		TextBox(T_SLASHER,	"Hi, Mrs. Sara! How are you today?",
							"I'm so lonely. When are you going to",
							"clone some rats?");
	
		TextBox(T_SARA,	"Oh hello, Slasher. I'm fine.",
						"The cloning engine is that big mass of",
						"steaming pipes there.");
	
		TextBox(T_SLASHER,	"Yay! Don't forget that you stored that",
							"Copper Gear in the tall cabin to the north.", "");
		EntFinish();
	}
	
	public static void Elder_Enter() /* 16 */
	{
		Warp( 8,67, TCROSS );
		Banner( "Rat Apartments",100 );
	}
	
	public static void Weap_Enter() /* 17 */
	{
		Warp( 30,69, TCROSS );
		Banner( "Ratcersize Hall",100 );
	}
	
	public static void Item_Enter() /* 18 */
	{
		Warp( 34,90, TCROSS );
		Banner( "Ratatorium",100 );
	}
	
	public static void Sara_Enter() /* 19 */
	{
		Warp( 53,69, TCROSS );
		Banner( "Laboratory",100 );
	}
	
	public static void Elder_Exit() /* 20 */
	{
		Warp(11,21, TCROSS);
	}
	
	public static void Elder_Upstair() /* 21 */
	{
		Warp( 11,79, TCROSS );
		Banner( "2F",100 );
	}
	
	public static void Elder_Down() /* 22 */
	{
		Warp( 13,61, TCROSS );
	}
	
	public static void Sara_Upstair() /* 23 */
	{
		Warp( 73,64, TCROSS );
	}
	
	public static void Sara_Down() /* 24 */
	{
		Warp( 53,80, TCROSS );
		Banner( "Basement",100 );
	}
	
	
	public static void Basement_Down() /* 14 */
	{
		Warp( 54,61, TCROSS );
	}
	
	public static void Weap_Exit() /* 27 */
	{
		Warp( 33,21, TCROSS );
	}
	
	public static void Item_Exit() /* 28 */
	{
		Warp( 29,33, TCROSS );
	}
	
	public static void Sara_Exit() /* 29 */
	{
		Warp( 7,35, TCROSS );
	}
	
	public static void The_Well() /* 18 */
	{
		Sully.Heal_Well();
		EntStart();
		if( flags[F_FLASH_WATER]==0 )
		{
			TextBox(T_SARA,	"I got some water.",
							"I better take it to the engine before it",
							"falls through my fingers!");
			
			flags[F_FLASH_WATER] = 1;
		}
		EntFinish();
	}
	
	public static void Basement_Up() /* 19 */
	{
	 	Warp( 73,68, TCROSS );
	}
	
	public static void engine() /* 20 */
	{
		int sara;
		
		if(flags[F_FLASH_WATER]==0)
		{
			TextBox(T_SARA,	"The steam engines will not start.",
							"I need to bring the machine some",
							"water.");
			return;
		}
		
		if( flags[F_FLASH_WATER]!=0 && flags[F_FLASH_WATER] != 2 )
		{
			
			SoundSplash();
			FadeFromColor(RGB(0,0,255),100);
			Banner("Poured the water",500);
			
			flags[F_FLASH_WATER] = 2; //we're done with the water!
			unpress(0);
		}
		
		if( flags[F_FLASH_GEAR]==0 )
		{
			TextBox(T_SARA,	"I'll need to install a gear made of ",
							"copper before I can activate the device.",
							"");
			return;
		}
		
		if( flags[F_FLASH_GEAR]!=2 && flags[F_FLASH_GEAR]!=0 )
		{
			SoundDoubleClick();
			Banner("Installed Gear",500);
			flags[F_FLASH_GEAR] = 2;
			unpress(0);
		}
		
		if( flags[F_FLASH_TWIG]==0 )
		{
			TextBox(T_SARA,	"I'll need to add some fuel.",
							"Perhaps there's a nest of twigs in the",
							"Forest.");
			return;
		}
		
		if( flags[F_FLASH_TWIG]!=2 && flags[F_FLASH_TWIG]!=0 )
		{
			SoundCrash();
			Banner("Added the twigs",500);
			flags[F_FLASH_TWIG]=2;
			unpress(0);
		}
		
		if( flags[F_FLASH_TOEJAM]==0 )
		{
			TextBox(T_SARA,	"I shouldn't start the machine until I",
							"lubricate the cogs with toe jam of",
							"Cyclops.");
			return;
		}
		
		if(flags[F_FLASH_TOEJAM]!=2 && flags[F_FLASH_TOEJAM]!=0)
		{
			SoundDoubleClick();
			Banner("Placed lubricant",500);
			flags[F_FLASH_TOEJAM] = 2;
			unpress(0);
		}
		
		if( flags[F_FLASH_SOLAR]==0 )
		{
			TextBox(T_SARA,	"To power the device, I need to gather",
							"solar energy in my suit and touch the",
							"engine.");
		
	
			return;
		}
		
		//Oh man!  Last part of this flashback!
		if( flags[F_FLASH_SOLAR]!=0 )
		{
			FadeToColor(RGB(255,255,0),100);
			FadeFromColor(RGB(255,255,0),100);
	
			TextBox(T_SARA,	"Alright! The ignition switch has started!",
							"This thing should start cloning rats",
							"in no time!");
			TextBox(T_SARA,	"Oops! I forgot to make one minor flow",
							"adjustment. Let me just reach in here",
							"quick...");
							
			FillVCLayer( RGB(255,0,0) );
			
			SoundHit();
			
			Warp(57,64, TNONE);
			Wait(10);
			
			ClearVCLayer();
			
			TextBox(T_SARA,	"Ouch! I cut my finger on one of those fast",
							"moving gears.",
							"I hope it's okay...");
			
			TextBox(0,	"Yay! Yay! Lots of rats to play with.",
						"Oh joy!","");
			
			TextBoxM(T_SARA,	"Oh no! That drop of blood must be jamming",
								"up the machine!",
								"It's really smoking now!");
			TextBox(T_SARA,		"No! According to this system monitor,",
								"it's starting to replicate human DNA!", "");
			
			VCQuake( 5, 0 );
			do_xplosions( 160,63 );
			do_xplosions( 152,58 );
			do_xplosions( 155,65 );
			VCQuakeOff();
			
			TextBox(T_SARA,	"Egad!","The whole structure is about to explode!","");
							
			SoundQuake();
			
			Earthquake( 15, 15, 300 );
			
			VCQuake( 5, 0 );
			do_xplosions( 155,65 );
			do_xplosions( 152,58 );
			do_xplosions( 160,63 );
			VCQuakeOff();
			
			SoundExplosion();
			
			do_sparklies(1);
			do_sparklies(2);
			do_sparklies(1);
			do_sparklies(2);
			do_sparklies(3);
			do_sparklies(4);
			do_sparklies(3);
			do_sparklies(4);
			
			AlterBTile(53,61,321,2);
			AlterBTile(54,61,546,2);
			AlterBTile(55,61,528,2);
			AlterBTile(53,62,547,2);
			AlterBTile(54,62,548,2);
			AlterBTile(55,62,549,2);
			AlterBTile(53,63,528,2);
			AlterBTile(54,63,546,2);
			AlterBTile(55,63,321,2);
			AlterFTile(53,60,320,2);
			AlterFTile(54,60,318,2);
			AlterFTile(55,60,529,2);
			AlterFTile(53,61,523,2);
			AlterFTile(54,61,525,2);
			AlterFTile(55,61,527,2);
			AlterFTile(53,62,529,2);
			AlterFTile(54,62,318,2);
			AlterFTile(55,62,320,2);
			
			do_sparklies(5);
			do_sparklies(6);
			do_sparklies(5);
			do_sparklies(6);
			
			entity.get(playerent).face = FACE_LEFT;
			
			TextBox(0,	"Hello.","","");
			TextBox(0,	"Hi! How are you today?","","");
			TextBox(0,	"Greetings! What's your name?","","");
			TextBox(T_SARA,	"Ahhhhhhhhhhhhhhhhhhh!","","");
			TextBox(0,	"What's wrong, Sara?",
						"Why aren't any rats coming out?","");
			TextBox(T_SARA,	"*sniff* It wasn't supposed to",
							"be this way... *sob*",
							"It was to be a thing of beauty...");
	
			flags[F_RODNE_FLASH_OVER] = 1; //we've done the flashback.  So rock out.
	
			RemovePlayer( "Sara" );
			AddPlayer( "Darin" );
			AddPlayer( "Dexter" );
	
			V1_MapSwitch("VILLAGE.MAP",9,84,0);
		}
	}
	
	public static void chest() /* 21 */
	{
		if( flags[F_FLASH_GEAR]==0 )
		{
			AlterBTile(12,85,177,2);
			SoundSwitch();
			Banner("Got Copper Gear!",500);
			flags[F_FLASH_GEAR]=1;
		}
	}
	
	public static void nest() /* 22 */
	{
		if(flags[F_FLASH_TWIG]==0)
		{
			SoundHit();
			Banner("Took dry twigs!",500);
			flags[F_FLASH_TWIG]=1;
		}
	}
	
	public static void notes() /* 23 */
	{
		EntStart();
		TextBoxM(T_SARA,	"Here's my notes on the clone machine.",
							"Let's see here...","");
		TextBoxM(T_SARA,	"The cloning device is a steam powered",
							"mechanism which will require both",
							"fuel and power.");
		TextBoxM(T_SARA,	"My special metal body suit can absorb",
							"solar power and use it to ignite the",
							"reaction.");
		TextBoxM(T_SARA,	"The machine cannot be safely operated",
							"unless the cogs are lubricated very",
							"well.");
		TextBox(T_SARA,		"Legends speak of the ancient race of",
							"Cyclops' whose toe jam has mystical",
							"lubricant powers.");
		EntFinish();
	}
	         
	public static void FlashIntro() /* 24 */
	{
		EntStart();
		if( flags[F_FLASH_INTRO]==0 )
		{
			FadeIn(30);
			
			TextBoxM(T_SARA,	"Ahh... I love that fresh air.",
								"Building this city all by myself certainly",
								"was fun.");
			TextBoxM(T_SARA,	"I'm glad I was able to use my free time",
								"constructively instead of committing",
								"crime.");
			TextBoxM(T_SARA,	"Now that the city is built I must finish my",
								"cloning engine to make lots of rats.", "");
			TextBoxM(T_SARA,	"My metropolis of rodents will be far",
								"superior to those geeks in Bumsville",
								"in every way!");
			TextBox(T_SARA,		"I better go review my notes.",
								"I think I locked them up in my basement.", "");
			flags[F_FLASH_INTRO] = 1;
		}
		EntFinish();
	}
	
	public static void do_xplosions( int x, int y ) /* 25 */
	{
		SoundBomb();
	
		VCPutIMG("res/images/story_fx/POP01.PCX",x,y);
		Wait(20);
		ClearVCLayer();
		VCPutIMG("res/images/story_fx/POP02.PCX",x,y);
		Wait(20);
		ClearVCLayer();
		VCPutIMG("res/images/story_fx/POP03.PCX",x,y);
		Wait(20);
		ClearVCLayer();
	}
	
	public static void do_sparklies( int idx ) /* 27 */
	{
		int x, y;
		
		for(x=91; x<=127; x+=16)
		{
			for(y=39; y<=71; y+=16)
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
}