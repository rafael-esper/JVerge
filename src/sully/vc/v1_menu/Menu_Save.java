package sully.vc.v1_menu;

import static core.Script.*;

import static sully.Sully.*;
import static sully.Flags.*;
import static sully.vc.Sfx.*;
import static sully.vc.v1_menu.Menu_Equip.MenuDrawSubWindow;
import static sully.vc.v1_menu.Menu_System.*;
import static sully.vc.simpletype_rpg.Inventory.*;
import static sully.vc.simpletype_rpg.Data.*;
import static sully.vc.simpletype_rpg.Party.*;
import static sully.vc.v1_rpg.V1_RPG.*;
import static sully.vc.v1_rpg.V1_Simpletype.FindItem;


import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import persist.ExtendedDataInputStream;
import persist.ExtendedDataOutputStream;
import sully.Sully;
import sully.vc.simpletype_rpg.Cast;
import sully.vc.simpletype_rpg.Inventory;
import domain.VImage;

public class Menu_Save {
	// menu_save.vc for Sully www.verge-rpg.com
	// Zip 05/09/2004
	// Last update 06/10/2004
	
	//        ---------------
	//       Save Menu
	//        ---------------
	
	// Data structure for holding core information on a savegame
	class Save_display_struct
	{
		VImage image; // Mini pic of location saved at
		int gametime; // How long that game has been underway
		String text; // Descriptive text about save
		String time; // Time saved, from computer clock
	}
	// Make
	static Save_display_struct save_display[] = new Save_display_struct[5];
	
	static {
		Menu_Save ms = new Menu_Save();
		save_display[0] = ms.new Save_display_struct();
		save_display[1] = ms.new Save_display_struct();
		save_display[2] = ms.new Save_display_struct();
		save_display[3] = ms.new Save_display_struct();
		save_display[4] = ms.new Save_display_struct();
	}
	
	
	// Control function for the Save and Load screens of the menu
	// Called from MenuEntry() based on global menu_idx variable
	// Some scary stuff here to navigate the sub window and ensure resources are freed
	public static void MenuControlSave()
	{
		
		try {
			// If saving, move between number of saves plus one (for 'New save' slot)
			if (menu_option == 5) menu_item = MenuControlArrows(menu_item, menu_sub + 1);
			// If loading move between number of saves
			else menu_item = MenuControlArrows(menu_item, menu_sub);
		
			// This handles if the focus of the sub window changes, necessitating freeing/loading slots
			if (menu_start + 5 == menu_item) // If moving one slot down
			{
				MenuFreeSaveDisplay(menu_start % 5); // Free slot at top of sub window
				if (menu_item < menu_sub) 
					MenuLoadSaveDisplay(menu_item); // Load new slot if needed
				menu_start++; // Increment first item displayed
			}
			else if (menu_start - 1 == menu_item) // If moving one slot up
			{
				if (menu_item + 5 < menu_sub) MenuFreeSaveDisplay(menu_item + 5); // Free slot at bottom of sub window if needed
				MenuLoadSaveDisplay(menu_item); // Load new slot
				menu_start = menu_item; // First item becomes current item
			}
			else if (menu_start + 4 < menu_item) // If 'gone off top' of sub window
			{
				menu_start = menu_item - 4; // First item becomes item four before current item
				MenuRefreshSlots(); // Clear all slots and reload
			}
			else if (menu_start > menu_item) // If 'gone off bottom' of sub window
			{
				menu_start = menu_item; // First item becomes current item
				MenuRefreshSlots(); // Clear all slots and reload
			}
		
			if (MenuConfirm())
			{
				if (menu_option == 5) // If saving
				{
					MenuSaveGame(menu_item + 1);
					MenuHappyBeep();
					MenuMinibox("Game saved in slot "+str(menu_item + 1), "sully.vc.v1_menu.Menu_Save.MenuDrawSave");
					MenuRoot(); // Return to main menu
				}
				else if (menu_sub!=0) // If loading, and there is 1 or more saved games
				{
					menu_done = true; // Close the menu when the game is loaded
					MenuHappyBeep();
					MenuMinibox("Loading game from slot "+str(menu_item + 1), "sully.vc.v1_menu.Menu_Save.MenuDrawSave");
					MenuLoadGame(menu_item + 1);
					_title_menu_load_done = true; // rbp
				}
				else MenuAngryBuzz();
			}
		
			if (MenuCancel())
			{
				MenuHappyBeep();
				MenuClearDisplay(); // Free images and data used for display from memory
				
				if( _title_menu !=0 )
				{
					_title_menu_load_cancel = true;
					return;
				}
				
				MenuRoot(); // Return to main menu
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Drawing function for the Save and Load screens of the menu
	// Called from MenuEntry() based on global menu_idx variable
	// Not quite as scary as it looks at first
	public static void MenuDrawSave()
	{
		int i;
	
		// Draw the background but no title
		MenuBlitRight(false, menu_option);
		MenuDrawBackground(MENU_A_X1, MENU_A_Y1, MENU_A_X2, MENU_A_Y2, MenuIsActive("Save"));
	
		if (menu_option == 5) // If saving
		{
			// Draw title and sub window for number of saves plus one (for 'New save' slot)
			printright(220, 15, screen, menu_font[0], "Save");
			MenuDrawSubWindow(20, 45, 220, 210, menu_item, 31, menu_sub + 1, menu_start, 0);
		}
		else // If Loading
		{
			// Draw title and sub window for number of saves
			printright(220, 15, screen, menu_font[0], "Load");
			MenuDrawSubWindow(20, 45, 220, 210, menu_item, 31, menu_sub, menu_start, 0);
		}
	
		// Loop through currently displayed slots
		for (i = menu_start; i < menu_sub; i++)
		{
			MenuBlitSaveSlot(i - menu_start, save_display[i % 5].text, save_display[i % 5].image); // Display the save info
			if (menu_start + 4 <= i) i = menu_sub + 1; // Break out after drawing 5
		}
		// If no break out occured, and if saving, display an 'empty' slot to save in
		if (i == menu_sub && menu_option == 5) MenuBlitSaveSlot(i - menu_start, "New Save Game", null);
	
		if (menu_item < menu_sub) // If a full save slot is currently selected
		{
			printright(220, 35, screen, menu_font[0], save_display[(menu_item) % 5].time); // Print when game was saved
			printstring(20, 35, screen, menu_font[0], GetTimeString(save_display[menu_item % 5].gametime)); // Print playtime of game
		}
	}
	
	// blits a save slot to the screen from passed information (does not look in save_display[] struct)
	public static void MenuBlitSaveSlot(int location, String text, VImage image)
	// Pass: Location within sub window, text to display, and image (pass negative for no image)
	// Clips long descriptions, no other error detection
	{
		int i;
		java.util.List<String> rows = wraptext(menu_font[0], text, 125); // Wrap any text passed
		int lines = rows.size(); // Count lines used
		if (lines > 2) // If there are more than two lines
		{
			text = rows.get(0) + "&" + rows.get(1) + "..."; // Performs a little clipping
			lines = 2;
		}
		for (i = 0; i < lines; i++) // Loops throught text lines
		{
			// Displays the text
			printstring(40, 52 + (menu_fonth * i) + (location * 31), screen, menu_font[0], rows.get(i));
		}
		// Displays a rectangle if no image was passed, otherwise blits the image
		if (image == null) rect(172, 51 + (location * 31), 203, 74 + (location * 31), menu_colour[2], screen);
		else blit(172, 51 + (location * 31), image, screen);
		// Pretty outer rectangle for framing purposes
		rect(35, 49 + (location * 31), 205, 76 + (location * 31), menu_colour[2], screen);
	}
	
	// Called before the saves are displayed, checks number of saves and loads data to slots
	public static int MenuInitSave()
	// Return: the number of save games
	// Checks for existence of save files. Can be fooled by renaming or deletion.
	{
		int count = 1;
		
		try {
			while(true) // As long as the save can be opened
			{
				URL url = load("save/save"+ThreeDigit(count+1)+".sve");
				if(url!=null) {
					File file = new File(url.getFile()); // Trys to open the save file 001
					if(file.exists())
						count++; // Increase the number of saves found
				}
				if(url==null)
					break;
			}
			int filecount = count;
			System.out.println("Found " + filecount + " files.");
			if (filecount > 5) filecount = 5; // If more than five saves exist, still only load 5 file headers
			while (filecount > 0) // Count down the save games
			{
				MenuLoadSaveDisplay(filecount-1); // Load the save header, minus 1 as array starts at 0
				filecount--;
			}
		} catch (Exception e) {
			e.printStackTrace();
			//error(e.getMessage());
		}
		return count; // Total number of saves is one less than count, as the last open must have failed
	}
	
	// Writes the system time at the current postion of a file
	static void WriteSystemTime(ExtendedDataOutputStream edos) throws IOException
	// Pass: An open file handle in FILE_WRITE mode
	// No error checking
	{
		
		edos.writeSignedIntegerLittleEndian(vc_GetYear());	// the system clock year
		edos.writeSignedIntegerLittleEndian(vc_GetMonth());	// current month
		edos.writeSignedIntegerLittleEndian(vc_GetDay());	// current day-of-month
		//int sysdate.dayofweek; 	// current day of week (0=sunday, 6=saturday)
	
		edos.writeSignedIntegerLittleEndian(vc_GetHour());	// system clock hour (24hour format)
		edos.writeSignedIntegerLittleEndian(vc_GetMinute());	// current clock minute
		edos.writeSignedIntegerLittleEndian(vc_GetSecond());	// current clock second
	}
	
	// Reads a date from current postion in file, and returns in String form
	static String ReadSystemTime(ExtendedDataInputStream edis) throws IOException
	// Pass: An open file handle in FILE_READ mode
	// Returns a String in pretty date format
	// No error checking
	{
		int year = 0, month = 0, day = 0, hour = 0, minute = 0, second = 0;
		year = edis.readSignedIntegerLittleEndian(); 
		month = edis.readSignedIntegerLittleEndian(); ;
		day = edis.readSignedIntegerLittleEndian(); ;
	
		hour = edis.readSignedIntegerLittleEndian(); ;
		minute = edis.readSignedIntegerLittleEndian(); ;
		second = edis.readSignedIntegerLittleEndian(); ;
		return TwoDigit(hour)+":"+TwoDigit(minute)+":"+TwoDigit(second)+"  "+TwoDigit(day)+"/"+TwoDigit(month)+"/"+str(year);
	}
	
	// Saves the current position in the game to file specified
	public static void MenuSaveGame(int slot) throws IOException
	{
		int xc, yc;
		String gamedesc = "Save game in file: save/save"+ThreeDigit(slot)+".sve";
		int ver = (VERSION_4_BYTE << 24) + (VERSION_3_BYTE << 16) + (VERSION_2_BYTE << 8) + VERSION_1_BYTE;
		VImage screenshot = new VImage(imagewidth(screen), imageheight(screen));
		VImage mini = new VImage(32, 24);
		rendermap(xwin, ywin, screenshot);
		scaleblit(0, 0, 32, 24, screenshot, mini);
		
		File savegame = new File(load(".").getFile() + "save/save"+ThreeDigit(slot)+".sve");
		log("Saving file at: " + savegame);
		savegame.createNewFile();
		
		FileOutputStream fos = new FileOutputStream(savegame);
		ExtendedDataOutputStream edos = new ExtendedDataOutputStream(fos); // rbp
		
		// [4] Header Size
			edos.writeSignedIntegerLittleEndian(3120 + len(VERSION_STRING) + len(gamedesc));
		// [4] Version Number
			edos.writeSignedIntegerLittleEndian(ver);
		// [6 + LEN] Version String
			edos.writeSignedIntegerLittleEndian(len(VERSION_STRING));
			edos.writeShort(0);//rbp
			//edos.writeFixedString(VERSION_STRING, len(VERSION_STRING));
			edos.writeSimpleString(VERSION_STRING);
		// [4] Game Time Count
			edos.writeSignedIntegerLittleEndian(global_gametime + systemtime);
		// [24] System Time
			WriteSystemTime(edos);
		// [6 + LEN] Game Description
			edos.writeSignedIntegerLittleEndian(len(gamedesc));
			edos.writeShort(0);//rbp
			edos.writeSimpleString(gamedesc);
		// [3072] Mini Screenshot
			for (yc = 0; yc <  24; yc++)
			{
				for (xc = 0; xc <  32; xc++)
				{
					edos.writeSignedIntegerLittleEndian(readpixel(xc, yc, mini));
				}
			}
		// Party Data
			edos.writeSignedIntegerLittleEndian(PartySize());
			edos.writeSignedIntegerLittleEndian(MAX_PARTY_SIZE);
			for (yc = 0; yc <  MAX_PARTY_SIZE; yc++)
			{
				edos.writeSignedIntegerLittleEndian(party[yc]);
			}
	
			edos.writeSignedIntegerLittleEndian(MAX_CAST);
			edos.writeSignedIntegerLittleEndian(MAX_STATS);
			edos.writeSignedIntegerLittleEndian(MAX_EQUIP_SLOTS);
			for (yc = 0; yc <  MAX_CAST; yc++)
			{
				edos.writeSignedIntegerLittleEndian(master_cast[yc].level);
				edos.writeSignedIntegerLittleEndian(master_cast[yc].exp);
				edos.writeSignedIntegerLittleEndian(master_cast[yc].cur_hp);
				edos.writeSignedIntegerLittleEndian(master_cast[yc].cur_mp);
				for (xc = 0; xc <  MAX_STATS; xc++)
				{
					edos.writeSignedIntegerLittleEndian(master_cast[yc].stats[xc]);
				}
				for (xc = 0; xc <  MAX_EQUIP_SLOTS; xc++)
				{
					edos.writeSignedIntegerLittleEndian(master_cast[yc].equipment[xc]);
				}
			}
		// Item Data
			edos.writeSignedIntegerLittleEndian(money); // Nearly forgot!
			edos.writeSignedIntegerLittleEndian(_supply_count);
			edos.writeSignedIntegerLittleEndian(MAX_SUPPLIES);
			for (yc = 0; yc <  MAX_SUPPLIES; yc++)
			{
				edos.writeSignedIntegerLittleEndian(supply_inventory[yc].item_ref);
				edos.writeSignedIntegerLittleEndian(supply_inventory[yc].quant);
			}
	
			edos.writeSignedIntegerLittleEndian(_equip_count);
			edos.writeSignedIntegerLittleEndian(MAX_EQUIPMENT);
			for (yc = 0; yc <  MAX_EQUIPMENT; yc++)
			{
				edos.writeSignedIntegerLittleEndian(equipment_inventory[yc].item_ref);
				edos.writeSignedIntegerLittleEndian(equipment_inventory[yc].quant);
			}
	
			edos.writeSignedIntegerLittleEndian(_key_count);
			edos.writeSignedIntegerLittleEndian(MAX_KEY_ITEMS);
			for (yc = 0; yc <  MAX_KEY_ITEMS; yc++)
			{
				edos.writeSignedIntegerLittleEndian(key_item_inventory[yc].item_ref);
				edos.writeSignedIntegerLittleEndian(key_item_inventory[yc].quant);
			}
		// Options Data
			edos.writeSignedIntegerLittleEndian(global_music_volume);
			edos.writeSignedIntegerLittleEndian(sfx_volume);
			edos.writeSignedIntegerLittleEndian(interface_volume);
			edos.writeSignedIntegerLittleEndian(global_noscroll?1:0);
			edos.writeSignedIntegerLittleEndian(global_menuluc);
			edos.writeSignedIntegerLittleEndian(_menu_on?1:0);
			edos.writeSignedIntegerLittleEndian(0); // Padding
			edos.writeSignedIntegerLittleEndian(MENU_COLOUR_NUM);
			for (yc = 0; yc <  MENU_COLOUR_NUM; yc++)
			{
				edos.writeSignedIntegerLittleEndian(menu_colour[yc].getRGB());
			}
		// Flag Data
			edos.writeSignedIntegerLittleEndian(MAX_FLAGS);
			for (yc = 0; yc <  MAX_FLAGS; yc++)
			{
				edos.writeSignedIntegerLittleEndian(flags[yc]);
			}
		// Map Data
			if(current_map == null) { // RBP: Just for robustness
				edos.writeSignedIntegerLittleEndian(len("NO_MAP"));
				edos.writeShort(0);
				edos.writeSimpleString("NO_MAP");
				edos.writeSignedIntegerLittleEndian(0);
				edos.writeSignedIntegerLittleEndian(0);
			}
			else {	
				edos.writeSignedIntegerLittleEndian(len(current_map.mapname));
				edos.writeShort(0);//rbp
				edos.writeSimpleString(current_map.mapname);
				edos.writeSignedIntegerLittleEndian(entity.get(playerent).getx());
				edos.writeSignedIntegerLittleEndian(entity.get(playerent).gety());
			}
		// Footer
			//RBP edos.writeSignedIntegerLittleEndian(FileCurrentPos(savegame));
		edos.close();
		//FreeImage(mini);
		//FreeImage(screenshot);
	}
	
	// Loads a game position from file specified
	public static void MenuLoadGame(int slot) throws IOException
	{
		int xc, yc, max_xc, max_yc;
		String mapname;
		File savegame = new File(load("save/save"+ThreeDigit(slot)+".sve").getFile());
		FileInputStream fis = new FileInputStream(savegame);
		ExtendedDataInputStream edis = new ExtendedDataInputStream(fis); // rbp
		
		// Header Size
		int hsize = edis.readSignedIntegerLittleEndian(); // rbp
		System.out.println("Header size: " + hsize);
		for(int i=0;i<hsize-4; i++) {
			edis.readByte();
		}
		
		//RBP FileSeekPos(savegame, edis.readSignedIntegerLittleEndian(), SEEK_SET);
		// Party Data
			curpartysize = edis.readSignedIntegerLittleEndian();
			System.out.println("curpartysize: " + curpartysize);
			
			if (edis.readSignedIntegerLittleEndian() != MAX_PARTY_SIZE) 
				error("Your MAX_PARTY_SIZE is screwed");
			
			for (yc = 0; yc <  MAX_PARTY_SIZE; yc++)
			{
				party[yc] = edis.readSignedIntegerLittleEndian();
			}
	
			if (edis.readSignedIntegerLittleEndian() != MAX_CAST) error("Your MAX_CAST is screwed");
			if (edis.readSignedIntegerLittleEndian() != MAX_STATS) error("Your MAX_STATS is screwed");
			if (edis.readSignedIntegerLittleEndian() != MAX_EQUIP_SLOTS) error("Your MAX_EQUIP_SLOTS is screwed");
			for (yc = 0; yc <  MAX_CAST; yc++)
			{
				if(master_cast[yc]==null) // RBP: Just for robustness
					master_cast[yc] = new Cast();
				
				master_cast[yc].level = edis.readSignedIntegerLittleEndian();
				master_cast[yc].exp = edis.readSignedIntegerLittleEndian();
				master_cast[yc].cur_hp = edis.readSignedIntegerLittleEndian();
				master_cast[yc].cur_mp = edis.readSignedIntegerLittleEndian();
				for (xc = 0; xc <  MAX_STATS; xc++)
				{
					master_cast[yc].stats[xc] = edis.readSignedIntegerLittleEndian();
				}
				for (xc = 0; xc <  MAX_EQUIP_SLOTS; xc++)
				{
					master_cast[yc].equipment[xc] = edis.readSignedIntegerLittleEndian();
				}
			}
		// Item Data
			money = edis.readSignedIntegerLittleEndian(); // Nearly forgot!
			_supply_count = edis.readSignedIntegerLittleEndian();
			if (edis.readSignedIntegerLittleEndian() != MAX_SUPPLIES) error("Your MAX_SUPPLIES is screwed");
			for (yc = 0; yc <  MAX_SUPPLIES; yc++)
			{
				supply_inventory[yc] = new Inventory();
				supply_inventory[yc].item_ref = edis.readSignedIntegerLittleEndian();
				supply_inventory[yc].quant = edis.readSignedIntegerLittleEndian();
			}
	
			_equip_count = edis.readSignedIntegerLittleEndian();
			if (edis.readSignedIntegerLittleEndian() != MAX_EQUIPMENT) error("Your MAX_EQUIPMENT is screwed");
			for (yc = 0; yc <  MAX_EQUIPMENT; yc++)
			{
				equipment_inventory[yc] = new Inventory();
				equipment_inventory[yc].item_ref = edis.readSignedIntegerLittleEndian();
				equipment_inventory[yc].quant = edis.readSignedIntegerLittleEndian();
			}
	
			_key_count = edis.readSignedIntegerLittleEndian();
			if (edis.readSignedIntegerLittleEndian() != MAX_KEY_ITEMS) error("Your MAX_KEY_ITEMS is screwed");
			for (yc = 0; yc <  MAX_KEY_ITEMS; yc++)
			{
				key_item_inventory[yc] = new Inventory();
				key_item_inventory[yc].item_ref = edis.readSignedIntegerLittleEndian();
				key_item_inventory[yc].quant = edis.readSignedIntegerLittleEndian();
			}
		// Options Data
			global_music_volume = edis.readSignedIntegerLittleEndian();
			sfx_volume = edis.readSignedIntegerLittleEndian();
			interface_volume = edis.readSignedIntegerLittleEndian();
			global_noscroll = edis.readSignedIntegerLittleEndian()==1?true:false;
			global_menuluc = edis.readSignedIntegerLittleEndian();
			_menu_on = edis.readSignedIntegerLittleEndian()==1?true:false; // Ah well
			xc = edis.readSignedIntegerLittleEndian(); // Padding
			if (edis.readSignedIntegerLittleEndian() != MENU_COLOUR_NUM) 
				error("Your MENU_COLOUR_NUM is screwed");
			for (yc = 0; yc <  MENU_COLOUR_NUM; yc++)
			{
				menu_colour[yc] = new Color(edis.readSignedIntegerLittleEndian());
			}
		// Flag Data
			if (edis.readSignedIntegerLittleEndian() != MAX_FLAGS) error("Your MAX_FLAGS is screwed");
			for (yc = 0; yc <  MAX_FLAGS; yc++)
			{
				flags[yc] = edis.readSignedIntegerLittleEndian();
			}
		// Map Data
			xc = edis.readSignedIntegerLittleEndian();
			edis.readShort(); // rbp
			mapname = edis.readFixedString(xc);
			log("Loaded Map: " + mapname);
			if (len(mapname) != xc) error("Incorrect size of map name found");
			xc = edis.readSignedIntegerLittleEndian();
			yc = edis.readSignedIntegerLittleEndian();
		// Footer
//RBP			if (FileCurrentPos(savegame) == edis.readSignedIntegerLittleEndian())
			//RBP			{
				edis.close();
				global_gametime = save_display[(slot - 1) % 5].gametime - systemtime;
	
				//things to make loading smooth no matter what.
				// this should be in something like LoadUpkeep()
				// Meh.
				// -Grue
				ClearVCLayer();
				cameratracking = 1;
				_title_menu = 0;

				hookretrace(""); // rbp ??
				V1_MapSwitch(mapname+".map", xc / 16, yc / 16, TBLACK);

				//RBP			}
				//RBPelse
				//RBP{
				//RBPedis.close();
				//RBPerror("Errors have occurred reading this save format, load aborted");
				//RBP}
			
	}
	
	// Loads the header of a save file for selection purposes
	public static void MenuLoadSaveDisplay(int slot) throws IOException
	{
		int xc, yc;
		save_display[slot % 5].image = new VImage(32, 24);

		File savegame = new File(load("save/save"+ThreeDigit(slot + 1)+".sve").getFile());
		FileInputStream fis = new FileInputStream(savegame);
		ExtendedDataInputStream edis = new ExtendedDataInputStream(fis); // rbp
		
		//Log("***Loading from: 'save/save"+ThreeDigit(slot + 1)+".sve'");
		// Header Size
			xc = edis.readSignedIntegerLittleEndian();
		// Version Number
			xc = edis.readSignedIntegerLittleEndian();
			if (xc < (VERSION_4_BYTE << 24) + (VERSION_3_BYTE << 16) + (VERSION_2_BYTE << 8) + VERSION_1_BYTE)
			{
				error("Attempting to load old savegame format, errors may occur.");
			}
		// Version String
			xc = edis.readSignedIntegerLittleEndian();
			edis.readShort(); // rbp
			
			save_display[slot % 5].text = edis.readFixedString(xc);

			if (len(save_display[slot % 5].text) != xc) error("Incorrect size of version String found");
			if (!strcmp(save_display[slot % 5].text , VERSION_STRING))
			{
				error("Reading version String: '"+save_display[slot % 5].text+"' does not match that of current: '"+VERSION_STRING+"'");
			}
			//Log("String version: "+save_display[slot % 5].text);
		// Game Time Count
			save_display[slot % 5].gametime = edis.readSignedIntegerLittleEndian();
			//Log("Systime: "+str(xc));
		// System Time
			save_display[slot % 5].time = ReadSystemTime(edis);
		// Game Description
			xc = edis.readSignedIntegerLittleEndian();
			edis.readShort(); // rbp
			save_display[slot % 5].text = edis.readFixedString(xc);
			if (len(save_display[slot % 5].text) != xc) error("Incorrect size of game description found");
			//Log("Text: '"+save_display[slot % 5].text+"'");
		// Mini Screenshot
			for (yc = 0; yc <  24; yc++)
			{
				for (xc = 0; xc <  32; xc++)
				{
					 setpixel(xc, yc, new Color(edis.readSignedIntegerLittleEndian()), save_display[slot % 5].image);
				}
			}
		edis.close();
		//Log("***Done with: 'save/save"+ThreeDigit(slot + 1)+".sve'");
	}
	
	// Frees all currently loaded save display slots
	static void MenuClearDisplay()
	{
		int i;
		for (i = 0; i < 5; i++)
		{
			if (len(save_display[i].text) >0) MenuFreeSaveDisplay(i);
		}
	}
	
	// Frees the specified save display slot
	static void MenuFreeSaveDisplay(int slot)
	{
		//FreeImage(save_display[slot % 5].image);
		save_display[slot % 5].text = "";
	}
	
	static // Frees all loaded save display slots, and loads all slots required
	void MenuRefreshSlots() throws IOException
	{
		int i;
		MenuClearDisplay();
		for (i = menu_start; i < menu_sub; i++)
		{
			MenuLoadSaveDisplay(i);
			log("--Refresh slot: "+str(i));
			if (menu_start + 4 <= i) i = menu_sub + 1;
		}
	}
}