package sully;

import static core.Script.*;
import static sully.Flags.*;
import static sully.Sully.*;
import sully.vc.v1_rpg.V1_RPG;
import sully.vc.v1_rpg.V1_Simpletype;

import static sully.vc.v1_rpg.V1_RPG.*;
import static sully.vc.v1_rpg.V1_Music.*;

import static sully.vc.simpletype_rpg.Party.*;
import static sully.vc.simpletype_rpg.Data.*;
import static sully.vc.simpletype_rpg.Inventory.*;
import static sully.vc.v1_rpg.V1_Maineffects.*;
import static sully.vc.v1_rpg.V1_Weather.*;
import static sully.vc.v1_rpg.V1_Textbox.*;
import static sully.vc.v1_menu.Menu_System.*;
import static sully.vc.Sfx.*;
import static sully.vc.util.Camscroll.*;
import static sully.vc.util.General.*;
import static sully.vc.v1_rpg.V1_Simpletype.*;

public class Dungeon {
	
	public static void start() 
	{
		Sully.SaveDisable(); //cannot save in towns.
			
		InitMap();
	
		V1_StartMusic("res/music/BADEXPER.MOD");
	
		//don't show the banner when we return from the flashback
		if( flags[F_LOVE_SARA_JOIN]!=0 )
		{
			Banner("Dungeon of Loooove",300);
		}
	
		//sets all the tiles as they should be.
		dungeon_upkeep();
	}
	
	public static void dungeon_upkeep() /* 0 */
	{
	
		if(flags[F_LOVE_SARA_JOIN]!=0)
		{
			AlterFTile(25,39,0,0);
			AlterBTile(25,40,273,0);
			AlterBTile(25,41,273,0);
		}
		
		if( flags[F_LOVE_GATE_OPEN]!=0 )
		{
			AlterFTile(25,15,0,0);
			AlterBTile(25,16,273,0);
		}
		
	
		if( flags[F_LOVE_LEVER]!=0 ) 
		{
			AlterBTile(35,17,518,2);
			AlterBTile(36,16,273,0);
			AlterFTile(36,15,0,0);
		}
	
		UpdateDial( F_LOVE_SWITCH_A,40 );
		UpdateDial( F_LOVE_SWITCH_B,41 );
		UpdateDial( F_LOVE_SWITCH_C,42 );	
		
		LoveDungMasterChestUpkeep(CHEST_LOVE_A,43,3);
		LoveDungMasterChestUpkeep(CHEST_LOVE_B,45,3);
		LoveDungMasterChestUpkeep(CHEST_LOVE_C,21,45);
		LoveDungMasterChestUpkeep(CHEST_LOVE_D,29,45);
		
	
		if(flags[F_LOVE_SARA_JOIN]==0)
		{
			Sara_event();
		}
	}
	
	public static void Upstair() /* 1 */
	{
		flags[F_LOVE_ESCAPE] = 1; //we've escaped!
		V1_MapSwitch("SHACK.MAP",71,5,0);
	}
	
	public static void Sara_event() /* 2 */
	{
		int darin, dexter, sara;
		int i;
		
		EntStart(); //rbp
		
		darin	= GetPartyEntity( "Darin" );
		dexter	= GetPartyEntity( "Dexter" );
		sara	= 0;	//sara's mapent #0 on this map
	
		entity.get(sara).incx(-(40*16)); //move the mapentity sara onscreen.
		entity.get(sara).face = FACE_UP;
		
		AutoOn();
		
		//cameratracker = sara; //let's put the camera on Sara.
		//cameratracking = 2;
		setplayer( sara );
		render();
		cameratracking = 0;
		ywin += 60;
		render();
		
		entity.get(darin).setx(24*16);
		entity.get(darin).sety(33*16);
		entity.get(dexter).setx(24*16);
		entity.get(dexter).sety(33*16);
		
		entity.get(darin).speed=125;
		entity.get(dexter).speed=155;
		entity.get(darin).specframe=21;
		entity.get(dexter).specframe=21;
		
	
		current_map.renderstring = "1,2,E,R";
		
		FadeIn(60);
		
	
		SoundFall();
		
		entity.get(darin).obstructable = false; // rbp
		entitymove(darin,"D12"); // Rbp was "D12 D0"
		WaitForEntity(darin);
		
		SoundBomb();
		
		for(i=10; i>1; i--)
		{
			Earthquake(0,i,10);
		}
		
		entity.get(darin).specframe = 0;
		entity.get(sara).specframe = 21;
		
	
		Wait(75);
		
		entity.get(sara).face=FACE_DOWN;
		
		entity.get(sara).specframe=0;
		Wait(30);
		
		entity.get(sara).specframe=24;
		Wait(30);
		
		entity.get(sara).specframe=0;
		Wait(30);
		
		entity.get(sara).specframe=24;
		Wait(30);
		
		entity.get(sara).specframe=0;
		
		entity.get(darin).face=FACE_RIGHT;
		Wait(50);
		
		entity.get(darin).face=FACE_UP;
		Wait(100);
		
		entity.get(darin).specframe=21;
		entity.get(sara).specframe=21;
		
		SoundFall();
		
		entitymove(dexter,"D12");
		WaitForEntity(dexter);
		
		entitymove(darin,"R2 F2");
		SoundBomb();
	
		for(i=10; i>1; i--)
		{
			Earthquake(0,i,10);
		}
	
		entity.get(darin).specframe=0;
		entity.get(dexter).specframe=0;
		entity.get(sara).specframe=0;
		Wait(30);
		entity.get(dexter).specframe=24;
		Wait(30);
		entity.get(dexter).specframe=0;
		Wait(30);
		entity.get(dexter).specframe=24;
		Wait(30);
		entity.get(dexter).specframe=0;
		
		entity.get(dexter).face = FACE_RIGHT;
		Wait(30);
	
		entity.get(darin).face  = FACE_UP;
		entity.get(dexter).face = FACE_UP;
		Wait(100);
		
		current_map.renderstring = "1,E,2,R";
	
		TextBox(T_SARA,	"Huh? Who are you?",
						"Oh, don't tell me you fell into that leaf",
						"patch trap as well!");
						
		entity.get(darin).speed=75;
		entity.get(dexter).speed=75;
		entity.get(sara).speed=75;
		
		entitymove(darin,"U3");
		WaitForEntity(darin);
		
		TextBox(T_DARIN,	"Yep. What a nasty trick!",
							"Who are you and where are we?", "");
		
		TextBox(T_SARA,	"My name is Sara, from the town of Rodne.",
						"We are beneath the love shack.", "");
						
		entity.get(darin).specframe=21;
		Wait(75);
		entity.get(darin).specframe=0;
		
		TextBox(T_DARIN,	"Sara! We've been looking for you.",
							"We need you to help us melt a crystal",
							"in Mt. Jujube.");
		entitymove(dexter,"U3");
		WaitForEntity(dexter);
	
		TextBox(T_DEXTER,	"Might you, by any chance, have access ",
							"to a [Thermal Activator], Sara?", "");
		
	
		entity.get(sara).specframe=23;
		Wait(100);
		entity.get(sara).specframe=0;
		
		TextBox(T_SARA,	"Yes, I have one stored in my basement",
						"behind a secret passage.", "");
						
		TextBox(T_DARIN,	"Splendid! Can you help us, then? ",
							"I will gladly pay you all that I have and ",
							"more!");
							
		TextBox(T_SARA,	"Keep your money. The [Thermal Activator]",
						"is yours if you can just get me out ",
						"of here!");
		TextBox(T_DEXTER,	"Is Big Daddy Bubba in that small dwelling",
							"above us?","");
		Wait(30);
		entity.get(sara).specframe=25;
		
		TextBoxM(T_SARA,	"Yes, it's horrible. He comes down here each",
							"night and drags me up to the cabin.", "");
		TextBox(T_SARA,		"I have to wear a leather thong and dance",
							"to Barry White music!",
							"It's torture!");
							
		TextBox(T_DARIN,	"Then it's time we teach him a thing or",
							"two about justice!","");
		
	
		TextBox(T_DEXTER,	"But how can we get out of this cell?",
							"Those bars look thick and sturdy.", "");
							
		entity.get(sara).specframe=0;
		
		TextBox(T_SARA,	"I can almost break them, but with two",
						"people, I bet I could do it!", "");
						
		entity.get(dexter).specframe=23;
		Wait(100);
		entity.get(dexter).specframe=0;
		
		TextBox(T_DEXTER,	"Two people? But I can help too!",
							"Just watch me!","");
							
		entitymove(darin,"L1");
		WaitForEntity(darin);
		
		TextBox(T_DARIN,	"Step aside, wizard boy. This",
							"is a job for me!","");
		
		entitymove(darin,"D3 F3 W30 F0");
		entitymove(sara,"D3 F2 W30 F0");
		WaitForEntity(sara);
		
		entity.get(sara).speed  = 150;
		entity.get(darin).speed = 150;
		
		TextBox(T_SARA,"Hieeeee-yah!","","");
		
		entitymove(sara,"U3 Z9");
		WaitForEntity(sara);
	
		SoundShing();
		
		for(i=10; i>1; i--)
		{
			Earthquake(0,i,10);
		}
		
		entity.get(sara).speed= 75;
		entity.get(sara).specframe=0;
		
		entitymove(sara,"D1 R1 F0 W30 F3");
		WaitForEntity(sara);
		
		TextBox(T_DARIN,"Sho-Ryu-Ken!","","");
		
		entitymove(darin,"U4 Z7");
		WaitForEntity(darin);
		
		SoundShing();
		SoundSwitch();
		
		AlterFTile(25,39,0,0);
		AlterBTile(25,40,273,0);
		
		for(i=10; i>1; i--)
		{
			Earthquake(0,i,10);
		}
		
	
		entity.get(darin).speed = 75;
		entity.get(darin).specframe=0;
		
		entitymove(darin,"D1");
		WaitForEntity(darin);
		
		entity.get(dexter).face = FACE_RIGHT;
		Wait(50);
		
		TextBox(T_SARA,	"All right! We did it. Now",
						"let's escape from this",
						"terrible place.");
		Wait(50);
		entity.get(darin).specframe=25;
		Wait(20);
		entity.get(darin).specframe=0;
		Wait(50);
		entity.get(dexter).specframe=29;
		entity.get(sara).specframe=28;
		Wait(20);
		entity.get(dexter).specframe=0;
		entity.get(sara).specframe=0;
		Wait(50);
		entity.get(darin).face=FACE_LEFT;
		Wait(20);
		entity.get(darin).face=FACE_UP;
		
		entitymove(dexter,"R1");
		entitymove(sara,"L1");
		WaitForEntity(sara);
		
		i = IsCharacter( "Darin" );   	//get darin's cast index.
		i = master_cast[i].level + 1; 	//use the index to get his level, and add one to it.
		
	
		JoinParty("Sara", i );			//sara joins the party a level higher than Darin.  
										// 'Cuz she's rockin'.
		
	
		entity.get(0).setx(3000); //warp the mapent of Sara away.
		entity.get(0).visible = false; // rbp
		entity.get(0).obstruction = false; // rbp
		
	
		Warp(25,42,TNONE);
		
		setplayer( darin );
		
		AutoOff();
		
		camReturnToPlayer( playerent, 50 );
		
		entity.get(darin).speed=100;
		entity.get(dexter).speed=100;
		entity.get(sara).speed=100;
		
		flags[F_LOVE_SARA_JOIN]=1;
		
		EntFinish(); //rbp
	}
	
	public static void gate() /* 3 */
	{
		if( flags[F_LOVE_GATE_OPEN]!=0 )
		{
			return;
		}
		
		if( flags[CHEST_LOVE_A] ==0)
		{
			EntStart();
			TextBox(SpcByPos(0),"Oh pooh. This gate is locked.","","");
			EntFinish();
			return;
		}
		
		EntStart();
		SoundSwitch();
		AlterFTile(25,15,0,0);
		AlterBTile(25,16,273,0);
		TextBox(SpcByPos(0),"The Bronze Key fits. Let's go.","","");
		DestroyItem( "Bronze_Key" );
		EntFinish();
	
		flags[F_LOVE_GATE_OPEN] = 1;
	}
	
	public static void Chest_A() /* 4 */
	{
		if( OpenTreasure(CHEST_LOVE_A,43,3,367) )
		{
			FindItem( "Bronze_Key", 1 );
		}
	}
	
	public static void Chest_B() /* 5 */
	{
		if( OpenTreasure(CHEST_LOVE_B, 45,3,367) )
		{
			FindMoney(325);
		}
	}
	
	public static void Chest_C() /* 6 */
	{
		if( OpenTreasure(CHEST_LOVE_C,21,45,367) )
		{
			FindItem( "Herb", 2 );
		}
	}
	
	public static void Chest_D() /* 7 */
	{
		if( OpenTreasure(CHEST_LOVE_D,29,45,367) )
		{
			FindItem( "Herb", 1 );
		}
	}
	
	// This bridge can be easier done by just setting the obs properly and 
	// changing Rstrings.  However, I'm keeping the old way because it's done.
	// If anyone wants to replace this with a much better example, go ahead and
	// do it and send it my way. -Grue
	public static void Bridge_On() /* 8 */
	{
		AlterFTile(15,26,0,0);
		AlterFTile(35,26,0,0);
		AlterFTile(11,24,0,1);
		AlterFTile(11,25,0,1);
		AlterFTile(12,23,0,1);
		AlterFTile(13,23,0,1);
		AlterFTile(14,24,0,1);
		AlterFTile(15,24,0,1);
		AlterFTile(16,24,0,1);
		AlterFTile(17,23,0,1);
		AlterFTile(18,23,0,1);
		AlterFTile(19,23,0,1);
		AlterFTile(20,23,0,1);
		AlterFTile(21,23,0,1);
		AlterFTile(23,23,0,1);
		AlterFTile(24,23,0,1);
		AlterFTile(25,23,0,1);
		AlterFTile(26,23,0,1);
		AlterFTile(27,23,0,1);
		AlterFTile(29,23,0,1);
		AlterFTile(30,23,0,1);
		AlterFTile(31,23,0,1);
		AlterFTile(32,23,0,1);
		AlterFTile(33,23,0,1);
		AlterFTile(34,24,0,1);
		AlterFTile(35,24,0,1);
		AlterFTile(36,24,0,1);
		AlterFTile(37,23,0,1);
		AlterFTile(38,23,0,1);
		AlterFTile(39,23,0,1);
		AlterFTile(39,24,0,1);
		AlterFTile(39,25,0,1);
		AlterFTile(25,26,352,1);
		AlterFTile(12,25,153,0);
		AlterFTile(13,25,153,0);
		AlterFTile(17,25,153,0);
		AlterFTile(18,25,153,0);
		AlterFTile(19,25,153,0);
		AlterFTile(20,25,153,0);
		AlterFTile(21,25,153,0);
		AlterFTile(29,25,153,0);
		AlterFTile(30,25,153,0);
		AlterFTile(31,25,153,0);
		AlterFTile(32,25,153,0);
		AlterFTile(33,25,153,0);
		AlterFTile(37,25,153,0);
		AlterFTile(38,25,153,0);
		AlterFTile(23,25,153,0);
		AlterFTile(24,25,153,0);
		AlterFTile(26,25,153,0);
		AlterFTile(27,25,153,0);
		AlterFTile(22,24,153,0);
		AlterFTile(28,24,153,0);
		
		current_map.renderstring = "1,2,E,R";
	}
	
	public static void Bridge_Off() /* 9 */
	{
		AlterFTile(15,26,0,1);
		AlterFTile(35,26,0,1);
		AlterFTile(11,24,0,0);
		AlterFTile(11,25,0,0);
		AlterFTile(12,23,0,0);
		AlterFTile(13,23,0,0);
		AlterFTile(14,24,0,0);
		AlterFTile(15,24,0,0);
		AlterFTile(16,24,0,0);
		AlterFTile(17,23,0,0);
		AlterFTile(18,23,0,0);
		AlterFTile(19,23,0,0);
		AlterFTile(20,23,0,0);
		AlterFTile(21,23,0,0);
		AlterFTile(23,23,0,0);
		AlterFTile(24,23,0,0);
		AlterFTile(25,23,0,0);
		AlterFTile(26,23,0,0);
		AlterFTile(27,23,0,0);
		AlterFTile(29,23,0,0);
		AlterFTile(30,23,0,0);
		AlterFTile(31,23,0,0);
		AlterFTile(32,23,0,0);
		AlterFTile(33,23,0,0);
		AlterFTile(34,24,0,0);
		AlterFTile(35,24,0,0);
		AlterFTile(36,24,0,0);
		AlterFTile(37,23,0,0);
		AlterFTile(38,23,0,0);
		AlterFTile(39,23,0,0);
		AlterFTile(39,24,0,0);
		AlterFTile(39,25,0,0);
		AlterFTile(25,26,352,0);
		AlterFTile(12,25,153,1);
		AlterFTile(13,25,153,1);
		AlterFTile(17,25,153,1);
		AlterFTile(18,25,153,1);
		AlterFTile(19,25,153,1);
		AlterFTile(20,25,153,1);
		AlterFTile(21,25,153,1);
		AlterFTile(29,25,153,1);
		AlterFTile(30,25,153,1);
		AlterFTile(31,25,153,1);
		AlterFTile(32,25,153,1);
		AlterFTile(33,25,153,1);
		AlterFTile(37,25,153,1);
		AlterFTile(38,25,153,1);
		AlterFTile(23,25,153,1);
		AlterFTile(24,25,153,1);
		AlterFTile(26,25,153,1);
		AlterFTile(27,25,153,1);
		AlterFTile(22,24,153,1);
		AlterFTile(28,24,153,1);
		
		current_map.renderstring = "1,E,2,R";
	}
	
	public static void LoveDungMasterChestUpkeep( int flag_idx, int x, int y ) // Chest Cleanup
	{
		if(flags[flag_idx]!=0)
		{
			AlterBTile(x,y,367,2);
		}
	}
	
	public static void Console_Lever() /* 15 */
	{
		EntStart();
		
		if(flags[F_LOVE_LEVER]==0)
		{
			//if this is true, we've solved it!
			if( flags[F_LOVE_SWITCH_A] == 4 && 
				flags[F_LOVE_SWITCH_B] == 2 && 
				flags[F_LOVE_SWITCH_C] == 7 )
			{
				
				SoundSwitch();
				
				AlterBTile(35,17,518,2);
				AlterBTile(36,16,273,0);
				AlterFTile(36,15,0,0);
				
				flags[F_LOVE_LEVER]=1;
				
				MenuOn();
				EntFinish();
				return;
			}
			
			//if we got here, we didn't solve it ;(
			MenuAngryBuzz();
			TextBox( SpcByPos(0), "The lever is stuck.","","" );
		}
		
		EntFinish();
	}
	
	public static void Console_A() /* 16 */
	{
		MasterDial( Flags.F_LOVE_SWITCH_A,40 );
	}
	
	public static void Console_B() /* 17 */
	{
		MasterDial( Flags.F_LOVE_SWITCH_B,41 );
	}
	
	public static void Console_C() /* 18 */
	{
		MasterDial( Flags.F_LOVE_SWITCH_C,42 );
	}
	
	
	public static void UpdateDial( int flag_idx, int pos )
	{
		AlterFTile(pos,15, flags[flag_idx]+507,2);
	}
	
	public static void MasterDial( int flag_idx, int pos ) /* 19 */
	{
		int answer, old_state;
		
		EntStart();
		
		old_state = GetTextBoxScrollMode(); //set the original TextBox Scroll State.
		
		SetTextBoxScroll( false ); //set the Scroll off for this question.  We do this a lot after all.
		
		answer = Prompt(SpcByPos(0),	"The red button decreases.",
										"The green button increases.", "", 
										"Red&Green");
		
	
		SetTextBoxScroll( old_state==1?true:false ); //and now we restore the original state!
		
		if( answer!=0 )
		{
			if(flags[flag_idx]==9)
			{
				MenuAngryBuzz();
				return;
			}
			
			flags[flag_idx]+=1;
			AlterFTile(pos,15,flags[flag_idx]+507,2);
			
			MenuPageTurn();
			
			EntFinish();
			return;
		}
			
		if(flags[flag_idx]==0)
		{
			MenuAngryBuzz();
			
			EntFinish();
			return;
		}
		
		flags[flag_idx]-=1;
		
		UpdateDial(flag_idx, pos);
		
		MenuPageTurn();
		
		EntFinish();
	}
	
	public static void Skull_A() /* 20 */
	{
		EntStart();
		TextBox(0,	"You fell into Big Daddy Bubba's",
					"trap! Ha ha!","");
		EntFinish();	
	}
	
	public static void Skull_B() /* 21 */
	{
		EntStart();
		TextBox(0,	"Please don't leave this cell.",
					"We're so bored and lonely!","");
		EntFinish();	
	}
	
	public static void Skull_C() /* 22 */
	{
		EntStart();
		TextBox(0,	"Don't look at me. I'm just a dumb skull!","","");
		EntFinish();	
	}
	
	public static void Skull_D() /* 23 */
	{
		EntStart();
		TextBox(0,"The sum of the numbers is 13.","","");
		EntFinish();	
	}
	
	public static void Skull_E() /* 24 */
	{
		EntStart();
		TextBox(0,"The second number is not odd.","","");
		EntFinish();	
	}
	
	public static void Skull_F() /* 25 */
	{
		EntStart();
		TextBox(0,	"The first number is greater than 3.","","");
		EntFinish();	
	}
	
	public static void Skull_G() /* 26 */
	{
		EntStart();
		TextBox(0,	"The first and second numbers",
					"are not the same.","");
		EntFinish();	
	}
	
	public static void Skull_H() /* 27 */
	{
		EntStart();
		TextBox(0,	"The third number is greater",
					"than the sum of the first two.","");
		EntFinish();	
	}
	
	
	public static void Skull_I()
	{
		EntStart();
		TextBox(0,	"None of the numbers are zero.","","");
		EntFinish();	
	}
}