package sully.vc.v1_menu;

import static core.Script.*;
import static sully.vc.v1_menu.Menu_System.*;
import static sully.vc.simpletype_rpg.Party.*;
import static sully.vc.simpletype_rpg.Cast.*;
import static sully.vc.simpletype_rpg.Data.*;

public class Menu_Skill {
	// menu_skill.vc for Sully www.verge-rpg.com
	// Zip 05/09/2004
	// Last update 06/10/2004
	
	//        ---------------
	//       Skill Menu
	//        ---------------
	
	// Control function for the Skill screen of the menu
	// Called from MenuEntry() based on global menu_idx variable
	// Very simple at the moment, room for additions
	void MenuControlSkill()
	{
		//Right now it's an angry buzz because the selector's sposta be there allowing you to choose a 
	
		// skill group to view the contents of... but yeah, that's not like done or anything yet
		// -Grue
		Menu2ArrowSetSounds( "MenuAngryBuzz","MenuPageTurn" );
		MenuControlTwoArrows("menu_item", 1, "menu_cast", PartySize());
		
		if (MenuConfirm())
		{
			Menu2ArrowSetSounds( "","" );
			MenuHappyBeep();
			MenuRoot();
		}
		if (MenuCancel())
		{
			Menu2ArrowSetSounds( "","" );
			MenuHappyBeep();
			MenuRoot();
		}
	}
	
	// Drawing function for the Skill screen of the menu
	// Called from MenuEntry() based on global menu_idx variable
	// Very simple at the moment, room for additions
	void MenuDrawSkill()
	{
		// Draw the background and title
		MenuBlitRight(false, menu_option);
		MenuDrawBackground(MENU_A_X1, MENU_A_Y1, MENU_A_X2, MENU_A_Y2, MenuIsActive("Skill"));
		printstring(80, 15, screen, menu_font[0], "Skill");
	
		// Draw the current selected party member and skill type
		MenuBlitCast(menu_cast, 0, 0);
		
		int y_ptr = 120;
		int sg_count = 0, tmp, i;
		
		printstring(25, y_ptr, screen, menu_font[0], "Skill Groups: ");
		y_ptr+=fontheight(menu_font[0])+1;
		
		for( i=0; i<getMySkillGroupCount(party[menu_cast]); i++ )
		{	
			printstring(25, y_ptr, screen, menu_font[0], master_skilltypes[GetMySkillGroup(party[menu_cast],i)].name );
			y_ptr+=fontheight(menu_font[0])+1;
			sg_count++;
		}
		
		if( sg_count == 0 )
		{
			printstring(25, y_ptr, screen, menu_font[0], "NONE" );
		}
	}
}