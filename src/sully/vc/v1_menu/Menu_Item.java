package sully.vc.v1_menu;

import static core.Script.*;
import static sully.vc.v1_menu.Menu_System.*;
import static sully.vc.v1_menu.Menu_Equip.*;
import static sully.vc.v1_menu.Menu_Status.*;
import static sully.vc.simpletype_rpg.Data.*;
import static sully.vc.util.Icons.*;
import static sully.vc.simpletype_rpg.Item.*;
import static sully.vc.simpletype_rpg.Inventory.*;
import domain.VImage;

public class Menu_Item {
	
	// menu_item.vc for Sully www.verge-rpg.com
	// Zip 05/09/2004
	// Last update 06/10/2004
	
	//        ---------------
	//       Item Menu
	//        ---------------
	
	// Gets the number of items of the currently selected type
	static int MenuGetItemCount()
	{
		if (menu_sub == 0) return _supply_count;
		else if (menu_sub == 1) return _equip_count;
		//else if (menu_sub == 2) 
		return _key_count;
	}
	
	void MenuControlItem()
	{
		int number = MenuGetItemCount();
		
		Menu2ArrowSetSounds( "MenuHappyBeep","MenuPageTurn" );
		number = MenuControlTwoArrows("menu_item", number, "menu_sub", 3);
		
		if ((number & 1)!=0)
		{
			if (menu_start  + 10 < menu_item) menu_start = menu_item - 10;
			else if (menu_start > menu_item && menu_item >= 0) menu_start = menu_item;
		}
		
		if ((number & 2)!=0)
		{
			menu_item = 0;
			menu_start = 0;
		}
		
		if (MenuGetItemCount() == 0)
		{
			menu_item = 0-1;
		}
		
	
		if (MenuConfirm() && menu_item >= 0)
		{
			if (menu_sub == 0)
			{
				if (master_items[supply_inventory[menu_item].item_ref].use_flag!=0 & USE_MENU!=0)
				{
					//MenuHappyBeep();
					UseItemI( supply_inventory[menu_item].item_ref );
					menu_idx = MenuGet("Item");
	//				menu_cast = 0;
	//				menu_idx = MenuGet("Cast");
	
					//if we're past the end of the list due to using the last amount of the last entry,
					//decrement the entry pointer.
					if( menu_item >= SupplyCount() ) {
						menu_item--;
					}
				}
				else MenuAngryBuzz();
			}
			if (menu_sub == 1)
			{
				if (master_items[equipment_inventory[menu_item].item_ref].use_flag!=0 & USE_MENU!=0)
				{
					MenuHappyBeep();
					UseItemI( equipment_inventory[menu_item].item_ref );
					menu_idx = MenuGet("Item");
	//				menu_cast = 0;
	//				menu_idx = MenuGet("Cast");
				}
				else MenuAngryBuzz();
			}
			if (menu_sub == 2)
			{
				if (master_items[key_item_inventory[menu_item].item_ref].use_flag!=0 & USE_MENU!=0)
				{
					//MenuHappyBeep();
					
					UseItemI( key_item_inventory[menu_item].item_ref );
					
					menu_idx = MenuGet("Item");
	//				menu_cast = 0;
	//				menu_idx = MenuGet("Cast");
				}
				else MenuAngryBuzz();
			}
		}
		if (MenuCancel())
		{
			MenuHappyBeep();
			Menu2ArrowSetSounds("","");
			MenuRoot();
		}
	}
	
	void MenuDrawItem()
	{
		int i, use;
		VImage useImage; // RBP
		int count = MenuGetItemCount();
		MenuBlitRight(false, menu_option);
		MenuDrawBackground(MENU_A_X1, MENU_A_Y1, MENU_A_X2, MENU_A_Y2, MenuIsActive("Item"));
		printright(220, 15, screen, menu_font[0], "Item");
		//Line(105, 35, 135, 35, menu_colour[2], screen);
		printcenter(50, 40, screen, menu_font[0], "Supply");
		printcenter(120, 40, screen, menu_font[0], "Equip");
		printcenter(190, 40, screen, menu_font[0], "Key");
		rect(24 + (menu_sub * 70), 36, 76 + (menu_sub * 70), 53, menu_colour[2], screen);
		if (count == 0)
		{
			MenuDrawSubWindow(20, 53, 220, 225 - (2 * (menu_fonth + 2)), menu_item, menu_fonth + 2, 1, menu_start, 3);
			MenuPrintDesc(menu_font[0], "No item", 180);
		}
		else
		{
			MenuDrawSubWindow(20, 53, 220, 226 - (2 * (menu_fonth + 2)), menu_item, menu_fonth + 2, count, menu_start, 3);
			if (menu_sub == 0)
			{
				MenuPrintDesc(menu_font[0], master_items[supply_inventory[menu_item].item_ref].desc, 180);
				for (i = menu_start; i < _supply_count; i++)
				{
					if (master_items[supply_inventory[i].item_ref].use_flag!=0 & USE_MENU!=0) use = 0;
					else use = 1;
					printstring(55, 56 + (13 * (i - menu_start)), screen, menu_font[use], master_items[supply_inventory[i].item_ref].name);
					printright(205, 56 + (13 * (i - menu_start)), screen, menu_font[use], str(supply_inventory[i].quant));
					useImage = icon_get(master_items[supply_inventory[i].item_ref].icon);
					if (i == menu_item) tblit(35, 54 + (13 * (i - menu_start)), useImage, screen);
					else tscaleblit(35, 58 + (13 * (i - menu_start)), 8, 8, useImage, screen);
					//FreeImage(use);
					if (menu_start + 10 <= i) i = _supply_count + 1;
				}
				//printstring(25, 214, screen, menu_font[0], master_items[supply_inventory[menu_item].item_ref].desc);
			}
			if (menu_sub == 1)
			{
				MenuPrintDesc(menu_font[0], master_items[equipment_inventory[menu_item].item_ref].desc, 180);
				for (i = menu_start; i < _equip_count; i++)
				{
					if (master_items[equipment_inventory[i].item_ref].use_flag!=0 & USE_MENU!=0) use = 0;
					else use = 1;
					printstring(55, 56 + (13 * (i - menu_start)), screen, menu_font[use], master_items[equipment_inventory[i].item_ref].name);
					printright(205, 56 + (13 * (i - menu_start)), screen, menu_font[use], str(equipment_inventory[i].quant));
					useImage = icon_get(master_items[equipment_inventory[i].item_ref].icon);
					if (i == menu_item) tblit(35, 54 + (13 * (i - menu_start)), useImage, screen);
					else tscaleblit(35, 58 + (13 * (i - menu_start)), 8, 8, useImage, screen);
					//FreeImage(use);
					if (menu_start + 10 <= i) i = _equip_count + 1;
				}
				//printstring(25, 214, screen, menu_font[0], master_items[equipment_inventory[menu_item].item_ref].desc);
			}
			if (menu_sub == 2)
			{
				MenuPrintDesc(menu_font[0], master_items[key_item_inventory[menu_item].item_ref].desc, 180);
				for (i = menu_start; i < _key_count; i++)
				{
					if (master_items[key_item_inventory[i].item_ref].use_flag!=0 & USE_MENU!=0) use = 0;
					else use = 1;
					printstring(55, 56 + (13 * (i - menu_start)), screen, menu_font[use], master_items[key_item_inventory[i].item_ref].name);
					printright(205, 56 + (13 * (i - menu_start)), screen, menu_font[use], str(key_item_inventory[i].quant));
					useImage = icon_get(master_items[key_item_inventory[i].item_ref].icon);
					if (i == menu_item) tblit(35, 54 + (13 * (i - menu_start)), useImage, screen);
					else tscaleblit(35, 58 + (13 * (i - menu_start)), 8, 8, useImage, screen);
					//FreeImage(use);
					if (menu_start + 10 <= i) i = _key_count + 1;
				}
				//printstring(25, 214, screen, menu_font[0], master_items[key_item_inventory[menu_item].item_ref].desc);
			}
		}
	}
}