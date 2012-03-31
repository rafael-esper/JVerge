package sully.vc.v1_menu;

import static core.Script.*;
import static sully.vc.v1_menu.Menu_System.*;
import static sully.vc.v1_menu.Menu_Status.*;
import static sully.vc.simpletype_rpg.Cast.*;
import static sully.vc.simpletype_rpg.Party.*;
import static sully.vc.simpletype_rpg.Data.*;
import static sully.vc.simpletype_rpg.Equipment.*;
import static sully.vc.simpletype_rpg.parser.Data_load.*;
import static sully.vc.util.Icons.*;
import domain.VImage;


public class Menu_Equip {
	
	// menu_equip.vc for Sully www.verge-rpg.com
	// Zip 05/09/2004
	// Last update 06/10/2004
	
	//        ----------------
	//       Equip Menu
	//        ----------------
	
	void MenuControlEquip()
	{
		//Menu2ArrowSetSounds( ,"MenuPageTurn" );
		Menu2ArrowSetSounds( "MenuHappyBeep","MenuPageTurn" );
		MenuControlTwoArrows("menu_sub", MAX_EQUIP_SLOTS, "menu_cast", PartySize());
		if (MenuConfirm())
		{
			MenuHappyBeep();
			menu_item = 0;
			menu_idx = MenuGet("EquipSub");
		}
		if (MenuCancel())
		{
			Menu2ArrowSetSounds( "","" );
			MenuHappyBeep();
			menu_sub = 0;
			menu_idx = MenuGet("Cast");
		}
	}
	
	void MenuControlEquipSub()
	{
		Menu1ArrowSetSounds("MenuHappyBeep");
		menu_item = MenuControlArrows(menu_item, EquCountBySlot(menu_sub) + 1);
		if (menu_start + 4 < menu_item) menu_start = menu_item - 4;
		else if (menu_start > menu_item) menu_start = menu_item;
		if (MenuConfirm())
		{
			if (menu_item == EquCountBySlot(menu_sub))
			{
				if (HasEquipment(party[menu_cast], menu_sub))
				{
					DequipItemI(party[menu_cast], menu_sub);
					MenuForceEquip();
				}
				
				MenuHappyBeep();
				menu_item = 0-1;
				menu_idx = MenuGet("Equip");
			}
			else if (CanEquipI(party[menu_cast], GetEquBySlot(menu_sub, menu_item))!=0)
			{
				MenuForceEquip();
				EquipItemI(party[menu_cast], GetEquBySlot(menu_sub, menu_item), menu_sub);
				MenuMinibox(master_items[master_cast[party[menu_cast]].equipment[menu_sub]].name+" equipped!", "MenuDrawEquip");
				menu_item = 0-1;
				menu_idx = MenuGet("Equip");
			}
			else MenuAngryBuzz();
		}
		if (MenuCancel())
		{
			MenuHappyBeep();
			
			menu_item = 0-1;
			menu_start = 0;
			
			Menu1ArrowSetSounds("");
			
			menu_idx = MenuGet("Equip");
		}
	}
	
	void MenuDrawEquip()
	{
		int i;
		MenuBlitRight(false, menu_option);
		MenuDrawBackground(MENU_A_X1, MENU_A_Y1, MENU_A_X2, MENU_A_Y2, MenuIsActive("Equip"));
		printright(220, 15, screen, menu_font[0], "Equip");
		MenuBlitCast(menu_cast, 0, 0);
		for (i = 0; i < MAX_STATS; i++)
		{
			MenuPrintStat(MENU_CAST_X, MENU_CAST_Y, i, 0, master_cast[party[menu_cast]].stats[i]);
		}
	
		line(20, 110, 220, 110, menu_colour[2], screen);
	
		for (i = 0; i < MAX_EQUIP_SLOTS; i++)
		{
			printstring(25, 115 + (15 * i),
			 screen, menu_font[0], GetSlotName(i));
			printstring(75, 115 + (15 * i),
			 screen, menu_font[0], master_items[master_cast[party[menu_cast]].equipment[i]].name);
		}
		printstring(15, 115 + (15 * menu_sub), screen, menu_font[0], ">");
	
	
		if (HasEquipment(party[menu_cast], menu_sub)) MenuPrintDesc(menu_font[0], master_items[master_cast[party[menu_cast]].equipment[menu_sub]].desc, 180);
		else MenuPrintDesc(menu_font[0], "No item", 180);
		line(20, 225 - (2 * (menu_fonth + 2)), 220, 225 - (2 * (menu_fonth + 2)), menu_colour[2], screen);
	}
	
	
	void MenuDrawEquipSub()
	{
		int i, equ_count, equip;
		VImage equipImage;
		MenuBlitRight(false, menu_option);
		MenuDrawBackground(MENU_A_X1, MENU_A_Y1, MENU_A_X2, MENU_A_Y2, MenuIsActive("EquipSub"));
		printright(220, 15, screen, menu_font[0], "Equip");
		MenuBlitCast(menu_cast, 0, 0);
		line(20, 110, 220, 110, menu_colour[2], screen);
		printstring(25, 115, screen, menu_font[0], GetSlotName(menu_sub));
		printstring(75, 115, screen, menu_font[0], master_items[master_cast[party[menu_cast]].equipment[menu_sub]].name);
	
		equ_count = EquCountBySlot(menu_sub);
	
		if (menu_item == equ_count)
		{
			for (i = 0; i < MAX_STATS; i++)
			{
				equip = GetMyStatPretendDequipI(party[menu_cast], menu_sub, i);
				MenuPrintStat(MENU_CAST_X, MENU_CAST_Y, i, MenuEquipFont(equip, getStat(party[menu_cast], i)), equip);
			}
		}
		else if (CanEquipI(party[menu_cast], GetEquBySlot(menu_sub, menu_item))!=0)
		{
			for (i = 0; i < MAX_STATS; i++)
			{
				equip = GetMyStatPretendEquipI(party[menu_cast], GetEquBySlot(menu_sub, menu_item), menu_sub, i);
				MenuPrintStat(MENU_CAST_X, MENU_CAST_Y, i, MenuEquipFont(equip, getStat(party[menu_cast], i)), equip);
			}
		}
		else
		{
			for (i = 0; i < MAX_STATS; i++)
			{
				MenuPrintStat(MENU_CAST_X, MENU_CAST_Y, i, 0, getStat(party[menu_cast], i));
			}
		}
	
		if (menu_item < equ_count) MenuPrintDesc(menu_font[0], master_items[GetEquBySlot(menu_sub, menu_item)].desc, 180);
		else MenuPrintDesc(menu_font[0], "No item", 180);
		MenuDrawSubWindow(20, 130, 220, 225 - (2 * (menu_fonth + 2)), menu_item, menu_fonth + 2, equ_count + 1, menu_start, 3);
	
		for (i = menu_start; i < equ_count; i++)
		{
			if (CanEquipI(party[menu_cast], GetEquBySlot(menu_sub, i))!=0) equip = 0;
			else equip = 1;
			printstring(55, 133 + ((menu_fonth + 2) * (i - menu_start)), screen, menu_font[equip], master_items[GetEquBySlot(menu_sub, i)].name);
			printright(205, 133 + ((menu_fonth + 2) * (i - menu_start)), screen, menu_font[equip], str(GetEquQuantBySlot(menu_sub, i)));
			equipImage = icon_get(master_items[GetEquBySlot(menu_sub, i)].icon);
			if (i == menu_item) tblit(35, 131 + ((menu_fonth + 2) * (i - menu_start)), equipImage, screen);
			else tscaleblit(35, 135 + ((menu_fonth + 2) * (i - menu_start)), 8, 8, equipImage, screen);
			//FreeImage(equip);
			if (menu_start + 4 <= i) i = equ_count + 1;
		}
		if (i == equ_count) printstring(55, 133 + ((menu_fonth + 2) * (i - menu_start)), screen, menu_font[0], "(none)"); // if near end
	}
	
	// Draws a sub window with vertical scroll bar and cursor, but draws none of the contents
	// This function is a god amoungst men
	public static void MenuDrawSubWindow(int x1, int y1, int x2, int y2, int entry_current, int entry_size, int entry_total, int entry_start, int entry_mod)
	// Pass: Dimensions of box by top left and bottom right coords, current selected entry (pass negative for none),
	//			height of one entry, total number of entries, entry at top of window, and size modifier
	// No error checking, and can display strangely if odd values are passed
	{
		int ydiff = y2 - y1 - 8;
		int entry_fit = (y2 - y1) / entry_size;
		if (entry_total < entry_fit)  entry_fit = entry_total;
		rect(x1, y1, x2, y2, menu_colour[2], screen);
		rect(x2 - 10, y1 + 2, x2 - 2, y2 - 2, menu_colour[2], screen);
		rectfill(x2 - 8, y1 + 4 + ((ydiff * entry_start) / entry_total),
		 x2 - 4, y1 + 4 + ((ydiff * (entry_fit + entry_start)) / entry_total), menu_colour[2], screen);
		if (entry_current >= 0)
		{
			rect(x1 + 4, y1 + 4 + (entry_size * (entry_current - entry_start)), x1 + 10, y1 + entry_size  - entry_mod + (entry_size * (entry_current - entry_start)), menu_colour[2], screen);
			rectfill(x1 + 6, y1 + 6 + (entry_size * (entry_current - entry_start)), x1 + 8, y1 + entry_size - entry_mod - 2 + (entry_size * (entry_current - entry_start)), menu_colour[2], screen);
		}
	}
	
	// Decides what colour font to use based on comparision of current and new values
	public static int MenuEquipFont(int newv, int current)
	{
		//if both values are less than or equal to 0, there is no effective change.  Keep them white.
		if( newv <= 0 && current <= 0  )	return 0;
		
		if (newv > current) return 2; // Higher value green font
		else if (newv < current) return 3; // Lower value red font
		return 0; // Same value white font
	}
	
	// Displays a stat on the screen based on type
	public static void MenuPrintStat(int x, int y, int stat, int font, int value)
	{
	
		int xpos = (stat + (stat / 10)) / 2;
		int ypos = (stat + (stat / 10)) % 2;
		if (stat == STAT_MAX_HP)
		{
			if( value > 1 )
				printright(x + 185, y + 10, screen, menu_font[font], str(value));
			else
				printright(x + 185, y + 10, screen, menu_font[font], "1");
		}
		else if (stat == STAT_MAX_MP)
		{
			if( value > 0 )		
				printright(x + 185, y + 20, screen, menu_font[font], str(value));
			else
				printright(x + 185, y + 20, screen, menu_font[font], "0");
		}
		else
		{
			printstring(x + (32 * xpos) - 32, y + 35 + (24 * ypos), screen, menu_font[font], GetStatName(stat));
			
			if(value > 0)
				printstring(x + (32 * xpos) - 32, y + 45 + (24 * ypos), screen, menu_font[font], str(value));
			else 
				printstring(x + (32 * xpos) - 32, y + 45 + (24 * ypos), screen, menu_font[font], "0");
		}
	}
}