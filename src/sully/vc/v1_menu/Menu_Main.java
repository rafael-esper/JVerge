package sully.vc.v1_menu;

import static core.Script.*;
import static sully.vc.v1_menu.Menu_System.*;
import static sully.vc.v1_menu.Menu_Save.*;
import static sully.vc.v1_menu.Menu_Item.*;
import static sully.vc.v1_rpg.V1_RPG.*;

public class Menu_Main {
	// menu_main.vc for Sully www.verge-rpg.com
	// Zip 05/09/2004
	// Last update 06/10/2004
	
	//        ---------------
	//       Main Menu
	//        ---------------
	
	// Control function for the Main screen of the menu
	// Called from MenuEntry() based on global menu_idx variable
	// Takes player selection, and redirects to the other menus
	public static void MenuControlMain()
	{
		// Checks arrow keys and loops through 7 options
		Menu1ArrowSetSounds( "MenuPageTurn" );
		menu_option = MenuControlArrows(menu_option, 7);
	
		if (MenuConfirm()) // If the confirmation button has been pressed
		{
			switch( menu_option )
			{
				// Item selected
				case 0:	menu_idx = MenuGet("Item");
						menu_sub = 0;
						menu_item = 0;
						if (MenuGetItemCount() == 0) menu_item = 0-1; // If no items, set to null
						else menu_item = 0; // Otherwise have first item selected
						MenuHappyBeep();
						break;
	
				// Skill selected
				case 1:	menu_idx = MenuGet("Cast");
						menu_cast = 0;
						MenuHappyBeep();
						break;
	
				// Equip selected
				case 2:	menu_idx = MenuGet("Cast");
						menu_cast = 0;
						MenuHappyBeep();
						break;
	
				// Status selected
				case 3:	menu_idx = MenuGet("Cast");
						menu_cast = 0;
						MenuHappyBeep();
						break;
	
				// Option selected
				case 4:	menu_idx = MenuGet("Option");
						menu_sub = 0;
						menu_item = 0;
						MenuHappyBeep();
						break;
	
				// Save selected
				case 5:	if (can_save) // If in area where save is allowed
						{
							menu_idx = MenuGet("Save");
							menu_sub = MenuInitSave();
							menu_item = 0;
							MenuHappyBeep();
						}
						else MenuAngryBuzz();
						break;
				// Load selected
				case 6:	menu_idx = MenuGet("Save");
						menu_sub = MenuInitSave();
						menu_item = 0;
						MenuHappyBeep();
						break;
			}
		}
		if (MenuCancel())
		{
			menu_done = true; // Close the menu
			MenuHappyBeep();
		}
	}
	
	// Draw function for the Main screen of the menu
	// Called from MenuEntry() based on global menu_idx variable
	// Very simple, displays current party in central window
	public static void MenuDrawMain()
	{
		MenuBlitRight(MenuIsActive("Main"), menu_option);
		MenuBlitCenter(false);
	}

}