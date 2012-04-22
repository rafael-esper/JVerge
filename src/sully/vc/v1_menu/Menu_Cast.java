package sully.vc.v1_menu;

import static core.Script.*;
import static sully.vc.v1_menu.Menu_System.*;
import static sully.vc.simpletype_rpg.Inventory.*;
import static sully.vc.simpletype_rpg.Party.*;
import static sully.vc.simpletype_rpg.Data.*;

public class Menu_Cast { 
	
	// menu_cast.vc for Sully www.verge-rpg.com
	// Zip 05/09/2004
	// Last update 06/10/2004
	
	//        ---------------
	//       Cast Menu
	//        ---------------
	
	// Control function for the Cast screen of the menu
	// Called from MenuEntry() based on global menu_idx variable
	// Mostly simple, but has to deal with being called from several different origins
	// Also includes various item use code for this reason
	public static void MenuControlCast()
	{
		menu_cast = MenuControlArrows(menu_cast, PartySize());
		if (MenuConfirm())
		{
			MenuHappyBeep();
			switch (menu_option)
			{
				case 0:	if (menu_sub == 0)
						{
							callfunction(master_items[supply_inventory[menu_item].item_ref].effect_func);
							MenuMinibox(master_items[supply_inventory[menu_item].item_ref].name+" used on "+master_cast[party[menu_cast]].name+"!", "sully.vc.v1_menu.Menu_Cast.MenuDrawCast");
							GiveItemI(supply_inventory[menu_item].item_ref, 0-1);
						}
						else if (menu_sub == 1)
						{
							callfunction(master_items[equipment_inventory[menu_item].item_ref].effect_func);
							MenuMinibox(master_items[equipment_inventory[menu_item].item_ref].name+" used on "+master_cast[party[menu_cast]].name+"!", "sully.vc.v1_menu.Menu_Cast.MenuDrawCast");
						}
						else if (menu_sub == 2)
						{
							callfunction(master_items[key_item_inventory[menu_item].item_ref].effect_func);
							MenuMinibox(master_items[key_item_inventory[menu_item].item_ref].name+" used on "+master_cast[party[menu_cast]].name+"!", "sully.vc.v1_menu.Menu_Cast.MenuDrawCast");
						}
						menu_cast = 0-1;
						menu_idx = MenuGet("Item");
						break;
				case 1: menu_idx = MenuGet("Skill");
						break;
				case 2: menu_idx = MenuGet("Equip");
						menu_sub = 0;
						break;
				case 3: menu_idx = MenuGet("Status");
						break;
			}
		}
		if (MenuCancel())
		{
			MenuHappyBeep();
			
			if (menu_option == 0) // If here from Item sub menu
			{
				menu_cast = 0-1; // Set selected party member to null
				menu_idx = MenuGet("Item"); // Go back to Item menu
			}
			else MenuRoot(); // Otherwise, return to root
		}
	}
	
	// Draw function for the Cast screen of the menu
	// Called from MenuEntry() based on global menu_idx variable
	// Nice and straightforward
	public static void MenuDrawCast()
	{
		// Display background, party, and prompt
		MenuBlitRight(false, menu_option);
		MenuBlitCenter(MenuIsActive("Cast"));
		printright(220, 15, screen, menu_font[0], "Select Party Member");
	
		// Display cursor by currently selected part member
		printstring(16, 30 + (menu_cast * 42), screen, menu_font[0], ">");
	}
}