package sully;

import static core.Script.*;
import static sully.Flags.*;

import static sully.vc.simpletype_rpg.Party.*;
import static sully.vc.simpletype_rpg.Cast.*;
import static sully.vc.simpletype_rpg.Data.*;
import static sully.vc.simpletype_rpg.Item.*;
import static sully.vc.v1_rpg.V1_Textbox.*;
import static sully.vc.v1_menu.Menu_System.*;
import static sully.vc.Sfx.*;
import static sully.vc.util.Targetting.*;

//effects.vc!
//
//This the the home for functions referred to in datafiles, 
//like item/spell targetting and effect functions!
//

public class Effects {
	
	////////////////////////////////
	//  Effect-specific sound effects!
	////////////////////////////////
	static String sfx_drink	= ( "res/sfx/items/drink.wav" );
	static String sfx_drip	= ( "res/sfx/items/drip.wav" );
	static String sfx_eat		= ( "res/sfx/items/eat.wav" );
	static String sfx_holy	= ( "res/sfx/items/sun.wav" );
	
	public static void SoundDrink() {
		playsound( sfx_drink, sfx_volume );
	}
	
	public static void SoundDrip() {
		playsound( sfx_drip, sfx_volume );
	}
	
	public static void SoundEat() {
		playsound( sfx_eat, sfx_volume );
	}
	
	public static void SoundHoly() {
		playsound( sfx_holy, sfx_volume );
	}
	
	
	
	////////////////////////////////
	//  Custom targetting functions
	////////////////////////////////
	
	// This asks for a single party member, and requires that their (cur_hp < max_hp) and cur_hp != 0
	// Uses the vc/util/targetting.vc helper functions
	public static void HP_OC_targ()
	{
		int cast_idx = 0-2;
	
		while( cast_idx < (0-1) )
		{
	
			cast_idx = MenuPartyBox();
	
			if( cast_idx == (0-1) ) //we've been cancelled!
			{
				CancelTargetting();
				MenuHappyBeep();
			}
			else //we've got a valid cast_idx.  Let's check for validity!
			{
				// If this guy isn't at full HP and isn't dead, 
				// He's a valid target!
				if( master_cast[cast_idx].stats[STAT_MAX_HP] != master_cast[cast_idx].cur_hp 
					&& master_cast[cast_idx].cur_hp > 0 )
				{
					AddTarget( cast_idx, 0, master_cast[cast_idx].name );
					TargettingDone();
					MenuHappyBeep();
				}
				else
				{
					cast_idx = 0-2; //set this to 
					MenuAngryBuzz();
					MenuPartyBoxRestorePosition();
				}
			}
		}
	}
	
	
	// This asks for a single party member, and requires that their (cur_mp < max_mp)
	// Uses the vc/util/targetting.vc helper functions
	public static void MP_OC_targ()
	{
		int cast_idx = 0-2;
		
		while( cast_idx < (0-1) )
		{
			cast_idx = MenuPartyBox();
			
			if( cast_idx == (0-1) ) //we've been cancelled!
			{
				CancelTargetting();
				MenuHappyBeep();
			}
			else //we've got a valid cast_idx.  Let's check for validity!
			{
				// If this guy isn't at full HP and isn't dead, 
				// He's a valid target!
				if( master_cast[cast_idx].stats[STAT_MAX_MP] != master_cast[cast_idx].cur_mp )
				{
					AddTarget( cast_idx, 0, master_cast[cast_idx].name );
					TargettingDone();
					MenuHappyBeep();
				}
				else
				{
					cast_idx = 0-2; //set this to 
					MenuAngryBuzz();
					MenuPartyBoxRestorePosition();
				}
			}
		}
	}
	
	
	// This asks for a single party member, and requires that their (cur_hp == 0)
	// Uses the vc/util/targetting.vc helper functions
	public static void DEAD_OC_targ()
	{
		int cast_idx = 0-2;
		
		while( cast_idx < (0-1) )
		{
			cast_idx = MenuPartyBox();
			
			if( cast_idx == (0-1) ) //we've been cancelled!
			{
				CancelTargetting();
				MenuHappyBeep();
			}
			else //we've got a valid cast_idx.  Let's check for validity!
			{
				// If this guy is dead, He's a valid target!
				if( 0 >= master_cast[cast_idx].cur_mp )
				{
					AddTarget( cast_idx, 0, master_cast[cast_idx].name );
					TargettingDone();
					MenuHappyBeep();
				}
				else
				{
					cast_idx = 0-2; //set this to 
					MenuAngryBuzz();
					MenuPartyBoxRestorePosition();
				}
			}
		}
	}
	
	// This asks for a single party member, and requires that their (cur_hp > 0)
	// Uses the vc/util/targetting.vc helper functions
	public static void LIVE_OC_targ()
	{
	log( "LIVE_OC" );
		
		int cast_idx = 0-2;
		
		while( cast_idx < (0-1) )
		{
			cast_idx = MenuPartyBox();
			
			if( cast_idx == (0-1) ) //we've been cancelled!
			{
				CancelTargetting();
				MenuHappyBeep();
			}
			else //we've got a valid cast_idx.  Let's check for validity!
			{
				// If this guy is alive, He's a valid target!
				if( 0 < master_cast[cast_idx].cur_mp )
				{
					AddTarget( cast_idx, 0, master_cast[cast_idx].name );
					TargettingDone();
					MenuHappyBeep();
				}
				else
				{
					cast_idx = 0-2; //set this to 
					MenuAngryBuzz();
					MenuPartyBoxRestorePosition();
				}
			}
		}	
	}
	
	
	
	
	//heal 15 MP
	public static void starlight_use()
	{
		int i;
		SoundHoly();
		
		for(i=0; i<GetTargettingCount(); i++)
		{
			HealMP( GetTargID(i), 15 );
		}
	}
	
	
	public static void herb_use()
	{
		int i;
		SoundEat();
		
		for(i=0; i<GetTargettingCount(); i++)
		{
			HealHP( GetTargID(i), 15 );
		}	
	}
	
	
	public static void pearl_use()
	{
		String s;
		
		SoundHoly();
		
		if( GetTargettingCount() != 1 )
		{
			AutoText( T_SULLY, "Dear sweet vecna!  Grue, or someone altering the system, screwed something up!~How the heck did the Pearl of Truth get used on multiple people?!" );
		}
		else
		{
			s = master_cast[GetTargID(0)].name;
			
			if( s.equals("darin") )
			{
				TextBox( T_DARIN, "Sometimes, late at night, I think unheroic","thoughts!","...I'm so ashamed." );
				
				if( CharInParty("Crystal") )
				{
					TextBox( T_CRYSTAL, "*gasp*!","","" );
				}
			}
			else if( s.equals("dexter") )
			{
				TextBox( T_DEXTER, "This one time, at Magic Camp,","I stuck a wand up m...","" );
				TextBox( T_DARIN, "That's enough Truth from the pearl","for today, I think.","" );
			}
			else if( s.equals("crystal") )
			{
				AutoText( T_CRYSTAL, "I am good and pure, and have never done anything wrong ever." );
				
				if( CharInParty("Darin") )
				{
					AutoText( T_DARIN, "*snort*" );
					AutoText( T_CRYSTAL, "You are *so* on the couch tonight after we save the world, mister." );
				}
			}
			else if( s.equals("sara") )
			{
				TextBox( T_SARA, "I put a 'kick me' sign on Crystal's","back when she wasn't looking.","" );
				
				if( CharInParty("Crystal") )
				{
					TextBox( T_CRYSTAL, "WHAT?!","","" );
				}
				
				TextBox( T_SARA, "...and by 'sign' I mean 'wrote on her","robes in machine grease'.","" );
				
				if( CharInParty("Crystal") )
				{
					TextBoxM( T_CRYSTAL, "...","","" );
					TextBoxM( T_CRYSTAL, "...you gonna die, bitch!","","" );
				}			
			}
			else if( s.equals("galfrey") )
			{
				TextBoxM( T_GALFREY, "...","","" );
				AutoText( T_GALFREY, "...sometimes... when I get nervous... I put my fingers under my armpits, and then I sniff'm." );
				
				if( CharInParty("Crystal") )
				{
					AutoText( T_CRYSTAL, "Eww!" );
					AutoText( T_GALFREY, "...Hey.  If you didn't want to hear it, you shouldn't've handed me the Pearl." );
				}
			}
			else
			{
				AutoText( T_SULLY, "We're looking for a guy named '"+s+"'.  Anyone by that name here?  No?  Bummer."  );
			}		
		}
	}
	
	
	public static void medicine_use()
	{
		int i;
		
		SoundDrink();
		
		for(i=0; i<GetTargettingCount(); i++)
		{
			HealHP( GetTargID(i), 45 );
		}	
	}
	
	
	public static void brew_use()
	{
		int i;
		
		SoundDrink();
		
		for(i=0; i<GetTargettingCount(); i++)
		{
			if( master_cast[GetTargID(i)].cur_hp == 0 )
			{
				HealHP( GetTargID(i), 1 );
			}
		}	
	}
	
	
	public static void worldmap_use()
	{
		MenuPageTurn();
		
		TextBox( T_STAN, "I have cursed this map!","No location for YOU!","Mwahahaha!" );
	}
	
	public static void gswitch_use()
	{
		int idx = IsItem( "Golden_Switch" );
		
		if( flags[F_GSWITCH]==0 )
		{
			TextBox( T_DARIN, "Wow!  A real golden switch!","And it even flips on and off!","" );
		}
		
		if( master_items[idx].icon == 43 )
		{
			master_items[idx].icon = 44;
		}
		else
		{
			master_items[idx].icon = 43;
		}
		
		SoundSwitch();
		unpress( 0 );
		
		flags[F_GSWITCH]++;
	}
}
