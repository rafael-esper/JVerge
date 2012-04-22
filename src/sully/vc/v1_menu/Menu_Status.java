package sully.vc.v1_menu;

import static core.Script.*;
import static sully.vc.v1_menu.Menu_System.*;
import static sully.vc.v1_menu.Menu_Equip.*;
import static sully.vc.simpletype_rpg.Party.*;
import static sully.vc.simpletype_rpg.Data.*;
import static sully.vc.simpletype_rpg.Cast.*;
import domain.VFont;

public class Menu_Status {

	// menu_status.vc for Sully www.verge-rpg.com
	// Zip 05/09/2004
	// Last update 06/10/2004
	
	//        -----------------
	//       Status Menu
	//        -----------------
	
	// Control function for the Status screen of the menu
	// Called from MenuEntry() based on global menu_idx variable
	// Very simple at the moment, room for additions
	public static void MenuControlStatus()
	{
		//setting the angry buzz here as a reminder that it does, in fact, 
		// have room for additions.
		Menu2ArrowSetSounds( "MenuAngryBuzz","MenuPageTurn" );
		int ret[] = MenuControlTwoArrows(menu_item, 1, menu_cast, PartySize());
		menu_item = ret[1]; // rbp
		menu_cast = ret[2]; // rbp	
		
		if (MenuConfirm())
		{
			Menu2ArrowSetSounds( "","" );
			MenuRoot();
		}
		if (MenuCancel())
		{
			Menu2ArrowSetSounds( "","" );
			MenuHappyBeep();
			MenuRoot();
		}
	}
	
	// Drawing function for the Status screen of the menu
	// Called from MenuEntry() based on global menu_idx variable
	// Very simple at the moment, room for additions
	public static void MenuDrawStatus()
	{
		int i;
	
		// Draw the background and title
		MenuBlitRight(false, menu_option);
		MenuDrawBackground(MENU_A_X1, MENU_A_Y1, MENU_A_X2, MENU_A_Y2, MenuIsActive("Status"));
		printright(220, 15, screen, menu_font[0], "Status");
	
		// Draw the current selected party member and stats
		MenuBlitCast(menu_cast, 0, 0);
		for (i = 0; i < MAX_STATS; i++)
		{
			MenuPrintStat(MENU_CAST_X, MENU_CAST_Y, i, 0, master_cast[party[menu_cast]].stats[i]);
		}
	
		// Print party member description
		MenuPrintDescVar(menu_font[0], master_cast[party[menu_cast]].desc, 190);
	}
	
	
	//        -------------------------------
	//       Description Functions
	//        -------------------------------
	// A few functions for wrapping and displaying short lines of text
	
	// Displays a description of up to two lines, at position set by defines
	public static int MenuPrintDesc(VFont desc_font, String desc_text, int desc_width)
	// Pass: The font that will be used, the text to be displayed, and the width available
	// Return: The number of lines that were displayed
	// No error checking
	{
		return MenuPrintDescFull(MENU_DESCRIPTION_X, MENU_DESCRIPTION_Y, desc_font, desc_text, desc_width);
	}
	
	// Displays a description of up to two lines, at position set by defines
	static int MenuPrintDescFull(int x, int y, VFont desc_font, String desc_text, int desc_width)
	// Pass: The x,y coords of the bottom left had corner, the font that will be used, the text to be displayed, and the width available
	// Return: The number of lines that were displayed
	// No error checking
	{
		// RBP: New implementation!
		java.util.List<String> rows = wraptext(menu_font[0], desc_text, desc_width);
		
		int lines = rows.size();
		if (lines == 1)
		{
			printstring(x, y - (3 * (menu_fonth + 2) / 2), screen, desc_font, rows.get(0));
		}
		else
		{
			printstring(x, y - ((menu_fonth + 2) * 2), screen, desc_font, rows.get(0));
			printstring(x, y - ((menu_fonth + 2) * 1), screen, desc_font, rows.get(1));
		}
		return lines;
	}
	
	// Prints description of any number of lines, working upwards. You get a free line with this one.
	static int MenuPrintDescVar(VFont desc_font, String desc_text, int desc_width)
	{
		// RBP: New implementation!
		java.util.List<String> rows = wraptext(menu_font[0], desc_text, desc_width);

		for (int i=0; i<rows.size(); i++)
		{
			printstring(MENU_DESCRIPTION_X, MENU_DESCRIPTION_Y - ((menu_fonth + 2) * (rows.size() - i)), screen, desc_font, rows.get(i));
		}
		line(MENU_DESCRIPTION_X - 5, MENU_DESCRIPTION_Y - 4 - (rows.size() * (menu_fonth + 2)),
			MENU_DESCRIPTION_X + desc_width + 5, MENU_DESCRIPTION_Y - 4 - (rows.size() * (menu_fonth + 2)), menu_colour[2], screen);
		return rows.size();
	}
}