package sully;

import static core.Script.*;
import static sully.Flags.*;
import static sully.Sully.*;

import static sully.vc.simpletype_rpg.Party.*;
import static sully.vc.v1_rpg.V1_RPG.*;
import static sully.vc.v1_rpg.V1_Maineffects.*;
import static sully.vc.v1_rpg.V1_Textbox.*;
import static sully.vc.v1_rpg.V1_Music.*;
import static sully.vc.v1_menu.Menu_System.*;
import static sully.vc.util.Camscroll.*;
import static sully.vc.util.General.*;

public class Chasm {
	
	public static void start()
	{
		InitMap();
		
		SaveDisable();
		
		//If we're in the flashback, don't play our music or 
		// remove the boulder
		if( flags[F_RODNE_FLASHBACK] == 0 ) 
		{
			V1_StartMusic("res/music/DREAMS2.S3M");
	
			// If the boulder's been moved, keep it out of the way
			if( flags[F_RAVEN_GULCH] !=0 )	
			{
				settile( 41,29, 1, 182 );
				setobs( 41,29, 0 );
			}
		}
	}
	
	public static void nexit()
	{
		V1_MapSwitch("overworld.map", 33,25,TBLACK);
	}
	
	public static void sexit()
	{
		V1_MapSwitch("overworld.map", 39,26,TBLACK);
	}
	
	public static void vorn()
	{
		EntStart();
		
		TextBoxM(0,"Welcome to Raven Gulch! It is said that",
	               "he who leaps off the edge will fall to",
	               "his death!");
	    TextBox(0,"The forest village of Rodne is to the",
	               "south and Mount Jujube is to the",
	               "northeast.");
	    
	    entity.get(0).speed=50;
	    
	    EntFinish();
	}
	
	public static void ranger()
	{
		MenuOff();
	
		//EntStart();
		
		if( flags[F_LAB_FOUND_CRYSTAL] == 0 )
		{
			TextBox(0,	"Sorry. Nobody can cross the bridge to",
						"Rodne unless I say so.",
						"Now git outta here, kid!");
		}
		else if( flags[F_RAVEN_GULCH] !=0 )
		{
			TextBox(0,"You can cross anytime you wish",
			"now. Have fun, kid!","");
		}
		else
		{
		
			TextBox(0,	"This is the Raven Gulch Bridge that leads",
						"to Rodne. Do you have authorization to",
						"cross?");
	
			TextBox(T_DEXTER,	"It's me, old friend. Darin and I need to ",
								"cross in order to save the world from",
								"doom.");
	
			TextBoxM(0,	"Hi, Dexter. Well, you really should have a",
						"passport or something, but you can cross.",
						"");
	
			TextBox(0,	"I shall remove the rock from trail here.","","");
	
	
			entitymove( event_entity,"D1 R4 W30");
			WaitForEntity( event_entity );
	
			settile( 41,29, 1, 182 );
			setobs( 41,29, 0 );
	
			entitymove(event_entity,"W30 L4 U1 F0");
			WaitForEntity( event_entity );
	
			TextBox(0,"Stay out of trouble now, ya hear?","",""); 
			flags[F_RAVEN_GULCH] = 1;
		}
		
		MenuOn();
	}
	
	public static void sun() 
	{
		int a;
		
		//only do this in Rodne-flashback mode
		if( flags[F_RODNE_FLASHBACK] !=0 )	
		{
			//only do this if we have no solar power yet!
			if( flags[F_FLASH_SOLAR] == 0 )
			{
						
				//if we don't have all of the other pieces done, quit out early!
				if( flags[F_FLASH_WATER]	!= 2	|| 
					flags[F_FLASH_GEAR]		!= 2 	|| 
					flags[F_FLASH_TWIG]		!= 2	|| 
					flags[F_FLASH_TOEJAM]	!= 2 ) 
				{
					TextBoxM(T_SARA,	"My body suit can absorb the sun's energy,",
										"but it fades quickly.","");
					TextBox(T_SARA,		"I shouldn't prepare to ignite the machine",
										"until all parts are in place.", "");
					return;
				}
				
				
				// If we got this far, let's suck some sun!
				//
				
				TextBox(T_SARA,	"The sun in unusually bright today.",
								"Let me absorb the raw power into",
								"my suit...");
	
				cameratracking = 0;
				
				camScrollTo(xwin, (ywin-50), 100);
							
				FadeToColor( RGB(255,255,0), 100 );		// Fade to bright yellow...
				FadeFromColor( RGB(255,255,0), 100 );	// ...then fade back out to normal!
				
				camReturnToPlayer(playerent, 100);
				
				cameratracking = 1;
				
				TextBox(T_SARA,	"I feel totally powered up!",
								"Now I just need to touch the cloning",
								"engine to start it!");
				
				flags[F_FLASH_SOLAR] = 1; //yay!  we have the power of the SUN.
			}
		}
	}
}