package sully;

import static core.Script.*;
import static sully.Flags.*;
import static sully.Sully.*;
import sully.vc.v1_rpg.V1_Simpletype;

import static sully.vc.v1_rpg.V1_RPG.*;
import static sully.vc.v1_rpg.V1_Music.*;

import static sully.vc.util.Camscroll.*;
import static sully.vc.simpletype_rpg.Party.*;
import static sully.vc.v1_rpg.V1_Maineffects.*;
import static sully.vc.v1_rpg.V1_Weather.*;
import static sully.vc.v1_rpg.V1_Textbox.*;
import static sully.vc.v1_menu.Menu_System.*;
import static sully.vc.Sfx.*;
import static sully.vc.util.General.*;

public class Shack {

	public static void start() 
	{
		Sully.SaveDisable(); //cannot save in towns.
			
		InitMap();
	
		V1_StartMusic("res/music/DISCO.S3M");
	
		//don't show the banner when we return from the flashback
		if( flags[F_LOVE_ESCAPE]==0 )
		{
			entity.get(1).visible=false;
			Banner("???",300);
		}
		else
		{
			if( flags[F_LOVE_DEX_GONE]==0 && CharInParty("Sara") )
			{
				entity.get(1).visible=false;
				Bubba_Scene();
			}
			else
			{
				entity.get(1).visible=true;
				Banner("Big Daddy Bubba's Funkaporium",300);
			}
		}
	
		//sets all the tiles as they should be.
		Shack_Upkeep();	
	}
	
	public static void Shack_Upkeep() /* 0 */
	{
		if(flags[F_LOVE_PITFALL]!=0)
		{
			AlterBTile(25,19,195,1);
			AlterBTile(26,19,195,1);
			AlterBTile(27,19,195,1);
		}
	
		if(flags[F_LOVE_REMOVE_ROCK]!=0)
		{
			AlterBTile(32,21,0,0);
		}
		
		
		if(flags[F_LOVE_DEX_GONE]!=0)
		{
			entity.get(1).specframe = 25;
		}
	}
	
	public static void Cabin_Enter() /* 1 */
	{
		Warp(79,23,TNONE);
	}
	
	public static void Cabin_Exit() /* 2 */
	{
		flags[F_LOVE_FUNKSWITCH] = 0;
		
		hookretrace( "sully.vc.v1_rpg.V1_RPG.V1RPG_RenderFunc" );
		
		AlterBTile(80,4,550,2);
		Warp(26,6, TNONE);
	}
	
	public static void Pit_Trap() /* 3 */
	{
		if( flags[F_LOVE_PITFALL]==0 )
		{
			EntStart();
	
			AlterBTile(25,19,195,1);
			AlterBTile(26,19,195,1);
			AlterBTile(27,19,195,1);
	
			TextBox(T_DEXTER,	"Oh no! The leaf patch has collapsed",
								"beneath us!","");
			TextBox(T_DARIN,	"Ahhhh!","","");
	
			flags[F_LOVE_PITFALL]=1;
	
			EntFinish();
	
			V1_MapSwitch("DUNGEON.MAP",0,0,TNONE);
		}
	}
	
	public static void Stone() /* 4 */
	{
		int darin, sara, i;
		
		EntStart();
		
		if( !CharInParty("Sara") )
		{
			TextBox(T_DEXTER, "Darin, this rock is too heavy",
							"for you and I to break. Let's",
							"just leave it alone for now.");
			EntFinish();
			return;
		}
		
		if( flags[F_LOVE_REMOVE_ROCK]==0 && CharInParty("Sara") )
		{
			darin	= GetPartyEntity( "Darin" );
			sara 	= GetPartyEntity( "Sara" );
			
			AutoOn();
			
			entitymove(darin,"X33 Y21 L0");
			entitymove(sara,"X34 Y21 L0");
			
			WaitForEntity(darin);
			WaitForEntity(sara);
			
			Wait(20);
			
			entity.get(darin).specframe = 28;
			Wait(50);
			entity.get(darin).specframe=0;
			Wait(30);
			entity.get(darin).face=FACE_DOWN;
			Wait(30);
			entity.get(darin).face=FACE_RIGHT;
			TextBox(T_DARIN,	"Sara, this rock is pretty",
								"heavy. I don't think we can",
								"break it.");
			entity.get(sara).specframe=22;
			
			TextBox(T_SARA,	"Sheesh! Men always give up",
							"so easily.","");
			entity.get(sara).specframe=0;
			
			entitymove(sara,"D1 L1 U0");
			
			WaitForEntity(sara);
			
			entity.get(darin).face=FACE_DOWN;
			
			TextBox(T_SARA,"Clear the runway!","","");
			entity.get(darin).specframe=21;
			
			entitymove(darin,"U1 D0");
			entitymove(sara,"U1");
			
			WaitForEntity(darin);
			WaitForEntity(sara);
			
			entity.get(darin).specframe=0;
			entitymove(sara,"R6 F0 W30 L0");
			
			WaitForEntity(sara);
			
			TextBox(T_SARA,"Hieeeee-yahhh!","","");
			
			entity.get(sara).speed = 150;
			
			entity.get(sara).specframe=26;
			
			entitymove(sara,"L6");
			
			WaitForEntity(sara);
			
			SoundCrash();
			AlterBTile(32,21,0,0);
			
			for(i=10; i>1; i--) {
				Earthquake( i,i, 5 );
			}
			
			entity.get(sara).specframe=0;
			entity.get(sara).face=FACE_UP;
			
			TextBox(T_SARA,"Yipee! We did it.","","");
			entitymove(darin,"D1");
			entity.get(darin).speed = 75;
			
			WaitForEntity(darin);
					
			AutoOff();
			
			Warp(33,21,TNONE);
			
			flags[F_LOVE_REMOVE_ROCK]=1;
			
			entity.get(darin).speed = 100;
			entity.get(sara).speed = 100;
		}
		
		EntFinish();
	}
	
	public static void Map_Exit() /* 5 */
	{
		V1_MapSwitch("overworld.map",42,60,TBLACK);
	}
	
	public static void Enter_Speak() /* 6 */
	{
		int darin, dexter;
		
		EntStart(); //rbp 
		
		if(flags[F_LOVE_INTRO]==0)
		{
			darin 	= GetPartyEntity( "Darin" );
			dexter	= GetPartyEntity( "Dexter" );
			
			entity.get(darin).specframe  = 23;
			entity.get(dexter).specframe = 23;
			
			Wait(100);
			
			entity.get(darin).specframe=0;
			entity.get(dexter).specframe=0;
			
			TextBox(T_DEXTER,	"There appears to be a strange little",
								"dwelling up ahead.","");
			TextBox(T_DARIN,	"Yeah, and where is that funky music",
								"coming from?","");
			
			flags[F_LOVE_INTRO]=1;
		}
		
		EntFinish(); //rbp 
	}
	
	public static void Dungeon() /* 7 */
	{
		V1_MapSwitch("DUNGEON.MAP",25,5,TNONE);
	}
	
	public static void Bubba_Talk() /* 8 */
	{
		EntStart();
		TextBox(T_BUBBA,	"Farewell my love children.",
					"Feel free to return whenever",
					"you want to groove to my love.");
					
		EntFinish();
		entity.get(0).face = FACE_DOWN;
	}
	
	public static void Dexter_Talk() /* 9 */
	{
		EntStart();
		entity.get(1).specframe=25;
		TextBoxM(T_DEXTER,	"Thanks a lot, guys. You go off and save",
							"the world while I'm trapped here",
							"with a funklord.");
		TextBox(T_DEXTER,	"I'll be sure to remember this when your",
							"birthday comes, Darin.", "");
		entity.get(1).specframe=0;
		EntFinish();
	}
	
	public static void Bubba_Scene() /* 10 */
	{
		int darin, dexter, sara, d,e ;
	
		
		EntStart();
		
		darin	= GetPartyEntity( "Darin" );
		dexter	= GetPartyEntity( "Dexter" );
		sara	= GetPartyEntity( "Sara" );
	
		FadeIn(30);
	
		entitymove(darin, "R4");
		WaitForEntity(darin);
			
		AutoOn();
		
		entity.get(darin).specframe=21;
		entity.get(sara).specframe=21;
		entity.get(dexter).specframe=21;
		
	
		Wait(100);
		
	
		entity.get(darin).specframe=0;
		entity.get(sara).specframe=0;
		entity.get(dexter).specframe=0;
		
	
		TextBox(T_BUBBA,	"Not so fast, my prisoners of love.",
		 			"You cannot leave my humble abode!", "");
		 			
		entitymove(darin,"X75 Y7 R0");
		entitymove(sara,"X76 Y6 R0");
		entitymove(dexter,"X75 Y5");
		
		WaitForEntity(sara);
		
		Wait(50);
		entity.get(sara).specframe=27;
		
		TextBox(T_SARA,	"We're leaving, Big Daddy Bubba!",
		 				"You can't stop me this time!", "");
		 			
		TextBoxM(T_BUBBA,	"You're ruining my groove, babe!",
		 			"Now I have no dancers for my funky",
					"dance floor.");
		
	
		entity.get(sara).specframe=0;
		
	
		TextBoxM(T_BUBBA,	"I once had dozens of dancers to boogie",
		 			"to the {musical{ language of #love#.", "");
		 			
		TextBox(T_BUBBA,	"But they all escaped from my dungeon.",
		 			"What am I to do now?",
		 			"I am incomplete without them.");
		 			
		entitymove(darin,"R1");
		WaitForEntity(darin);
		
		
		TextBox(T_DARIN,	"You cannot force people to stay here in",
							"this wretched little cabin!", "");
		
	
		TextBox(T_BUBBA,	"I really do need some heart-shaped",
		 			"pillows and a floating disco ball, don't I?", "");
		 			
		entity.get(sara).face=FACE_DOWN;
		TextBox(T_SARA,	"Darin, let's make a run for it!",
		 				"Now's our chance to escape from here!", "");
						 
		entity.get(sara).face=FACE_RIGHT;
		TextBox(T_BUBBA,	"No can do, honey babe.",
		 			"Nobody can ever leave this place unless",
		 			"it's groovy with me!");
		 			
		TextBox(T_BUBBA,	"Wait... the boy in the gold headband there.",
		 			"I feel the #love# is strong with him.", "");
		
	
		entity.get(dexter).specframe=21;
		Wait(50);
		entity.get(dexter).specframe=23;
		Wait(100);
		
	
		TextBox(T_DEXTER,	"What...? Me?","","");
		entity.get(dexter).specframe=0;
		
		TextBoxM(T_BUBBA,	"I sense a strong soul in him.",
		 			"Oh yeah... I can sense it.",
		 			"He shall be the next Big Daddy!");
		TextBoxM(T_BUBBA,	"When my groove runs flat, I will need",
		 			"an heir to carry on the love in this",
		 			"Forest.");
		TextBox(T_BUBBA,	"That's my condition.",
					"Leave the young funkmaster here and the",
					"rest of you may leave.");
		
	
		entitymove(darin,"D1 R1 L0");
		entitymove(sara,"D2 R0");
		
		cameratracking=0;
		d=xwin;
		e=ywin;
		
	
		timer=0;
		while(ywin<e+33)
		{
			ywin=e+timer;
			render();
			showpage();
		}
		
		timer=0;
		while(xwin<d+32)
		{
			xwin=d+timer;
			render();
			showpage();
		}
		
		Wait(50);
		entity.get(darin).specframe=28;
		Wait(20);
		entity.get(darin).specframe=0;
		Wait(50);
		entity.get(sara).specframe=29;
		Wait(20);
		entity.get(sara).specframe=0;
		Wait(50);
		
		entitymove(dexter,"R2 D0");
		WaitForEntity(dexter);
		
		TextBox(T_DEXTER,	"Come on, Darin! Let's attack!",
		 					"If we fight together, I bet we can defeat", 
		 					"him!");
		 					
		entity.get(darin).face=FACE_UP;
		entity.get(sara).face=FACE_UP;
		
		TextBox(T_DARIN,	"Actually, Dexter, Sara and I have talked it",
		 					"over and we're going to leave you here.", "");
		 					
		entity.get(dexter).specframe=21;
		Wait(50);
		entity.get(dexter).specframe=22;
		
		TextBox(T_DEXTER,	"What?! You can't be serious.","","");
		TextBox(T_SARA,	"Look at the size of those platforms,",
						"Dexter. He could totally kick our butts!", "");
		 				
		 				
		TextBox(T_DARIN,	"We know you'll do what is best for the",
		 					"good of the party, Dexter.", "");
		TextBox(T_DEXTER,	"This isn't very heroic, not to mention ","humane.","");
		
	
		TextBox(T_BUBBA,	"That's the spirit, my young love birds.",
		 			"I will instruct Dexter in the ways of...",
		 			"loooove.");
		
	
		entity.get(dexter).specframe=20;
		Wait(100);
		entity.get(dexter).specframe=22;
		
		TextBox(T_DEXTER,	"Eek! You guys aren't helping very much.","","");
		TextBox(T_SARA,	"Ah, calm down Dexter. You'll survive.",
		 				"We'll look you up if we ever come back",
		 				"here.");
		
	
		TextBox(T_DARIN,	"Seeya, Dex!","Come on, Sara.",
		 					"We have work to do!");
		
	
		TextBox(T_BUBBA,	"Later, sweet peas!",
		 			"If the groove is ever beating strong",
		 			"you can come back anytime.");
		
	
		entity.get(dexter).speed=75;
		entity.get(dexter).specframe=0;
		entitymove(dexter,"R2 U1 F2 W30 F0 W50 Z25");
		WaitForEntity(dexter);
		
		entity.get(darin).face=FACE_RIGHT;
		Wait(30);
		entity.get(darin).face=FACE_DOWN;
		Wait(30);
		entity.get(sara).face=FACE_RIGHT;
		Wait(30);
		entity.get(sara).speed=75;
		entitymove(sara,"R1");
		WaitForEntity(sara);
		
		Warp(77,8,TNONE);
		
		RemovePlayer( "Dexter" );	//remove dexter from the party...
		entity.get(dexter).setx(30000);		//...then warp his entity offscreen!
		
		
		
		//Entity.X[1] = (79*16);		// and move his mapentity doppleganger into place!
		//Entity.Y[1] = (4*16);		// and move his mapentity doppleganger into place!
		entity.get(1).visible=true;
		entity.get(1).specframe=25;
		
	
		
		AutoOff();
		camReturnToPlayerS(darin, 25);
		
		flags[F_LOVE_DEX_GONE]=1;
		EntFinish();
	}
	
	public static void Disco_Lever() /* 13 */
	{
		if(flags[F_LOVE_FUNKSWITCH]==0)
		{
			EntStart();
			TextBoxM(T_BUBBA,	"No, you fool!",
								"That's my super duper psycho-trip lever!",
								"It amplifies the dance floor!");
			TextBox(T_BUBBA,	"You cannot handle Funk of such great",
								"magnatude!",
								"You'll destroy your little ungroovy souls!!" );
			EntFinish();
		
			AlterBTile(80,4,551,2);
			SoundSwitch();
			
			flags[F_LOVE_FUNKSWITCH]=1;
			hookretrace( "sully.vc.Special_effects.Funk_o_rama.funkOrama" );	//RageCage's FUNKORAMA
			unpress(0);
			return;
		}
		
		if( flags[F_LOVE_FUNKSWITCH]!=0 )
		{
			SoundSwitch();
			AlterBTile(80,4,550,2);
			flags[F_LOVE_FUNKSWITCH]=0;
			unpress(0);
			hookretrace( "sully.vc.v1_rpg.V1_RPG.V1RPG_RenderFunc" );
		}
	}
	

}