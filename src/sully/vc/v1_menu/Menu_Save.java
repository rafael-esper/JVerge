package sully.vc.v1_menu;

import static core.Script.*;
import static sully.Sully.*;
import static sully.vc.v1_menu.Menu_System.*;
import static sully.vc.v1_menu.Menu_Equip.*;
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
	Save_display_struct save_display[] = new Save_display_struct[5];
	
	// Control function for the Save and Load screens of the menu
	// Called from MenuEntry() based on global menu_idx variable
	// Some scary stuff here to navigate the sub window and ensure resources are freed
	void MenuControlSave()
	{
		// If saving, move between number of saves plus one (for 'New save' slot)
		if (menu_option == 5) menu_item = MenuControlArrows(menu_item, menu_sub + 1);
		// If loading move between number of saves
		else menu_item = MenuControlArrows(menu_item, menu_sub);
	
		// This handles if the focus of the sub window changes, necessitating freeing/loading slots
		if (menu_start + 5 == menu_item) // If moving one slot down
		{
			MenuFreeSaveDisplay(menu_start % 5); // Free slot at top of sub window
			if (menu_item < menu_sub) MenuLoadSaveDisplay(menu_item); // Load new slot if needed
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
				MenuMinibox("Game saved in slot "+str(menu_item + 1), "MenuDrawSave");
				MenuRoot(); // Return to main menu
			}
			else if (menu_sub!=0) // If loading, and there is 1 or more saved games
			{
				menu_done = true; // Close the menu when the game is loaded
				MenuHappyBeep();
				MenuMinibox("Loading game from slot "+str(menu_item + 1), "MenuDrawSave");
				MenuLoadGame(menu_item + 1);
			}
			else MenuAngryBuzz();
		}
	
		if (MenuCancel())
		{
			MenuHappyBeep();
			MenuClearDisplay(); // Free images and data used for display from memory
			
			if( _title_menu !=0 )
			{
				_title_menu_load_done = true;
				return;
			}
			
			MenuRoot(); // Return to main menu
		}
	}
	
	// Drawing function for the Save and Load screens of the menu
	// Called from MenuEntry() based on global menu_idx variable
	// Not quite as scary as it looks at first
	void MenuDrawSave()
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
			//REMOVERBPprintright(220, 35, screen, 0, save_display[(menu_item) % 5].time); // Print when game was saved
			//REMOVERBPprintstring(20, 35, screen, 0, GetTimeString(save_display[menu_item % 5].gametime)); // Print playtime of game
		}
	}
	
	// blits a save slot to the screen from passed information (does not look in save_display[] struct)
	void MenuBlitSaveSlot(int location, String text, VImage image)
	// Pass: Location within sub window, text to display, and image (pass negative for no image)
	// Clips long descriptions, no other error detection
	{
		int i;
		text = wraptext(menu_font[0], text, 125); // Wrap any text passed
		int lines = tokencount(text, "&"); // Count lines used
		if (lines > 2) // If there are more than two lines
		{
			//REMOVERBPtext = (text, "&", 0) + "&" + (text, "&", 1) + "..."; // Performs a little clipping
			lines = 2;
		}
		for (i = 0; i < lines; i++) // Loops throught text lines
		{
			// Displays the text
			//REMOVERBPprintstring(40, 52 + (menu_fonth * i) + (location * 31), screen, menu_font[0], (text, "&", i));
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
	// Checks for existance of save files. Can be fooled by renaming or deletion.
	{
		int count = 1; // Assume first save exists
		/*REMOVERBPint file = FileOpen("save/save001.sve", FILE_READ); // Trys to open the save file 001
		while(file!=0) // As long as the save can be opened
		{
			FileClose(file); // Close the current save
			count++; // Increase the number of saves found
			file = FileOpen("save/save"+ThreeDigit(count)+".sve", FILE_READ); // Try to open next save
		}
		file = count - 1; // Reuse file variable to store number of save games succesfully opened
		if (file > 5) file = 5; // If more than five saves exist, still only load 5 file headers
		while (file > 0) // Count down the save games
		{
			MenuLoadSaveDisplay(file - 1); // Load the save header, minus 1 as array starts at 0
			file--;
		}*/
		return count - 1; // Total number of saves is one less than count, as the last open must have failed
	}
	
	// Writes the system time at the current postion of a file
	void WriteSystemTime(int filehandle)
	// Pass: An open file handle in FILE_WRITE mode
	// No error checking
	{
		/*REMOVERBPFileWriteQuad(filehandle, vc_GetYear());	// the system clock year
		FileWriteQuad(filehandle, vc_GetMonth());	// current month
		FileWriteQuad(filehandle, vc_GetDay());	// current day-of-month
		//int sysdate.dayofweek; 	// current day of week (0=sunday, 6=saturday)
	
		FileWriteQuad(filehandle, vc_GetHour());	// system clock hour (24hour format)
		FileWriteQuad(filehandle, vc_GetMinute());	// current clock minute
		FileWriteQuad(filehandle, vc_GetSecond());	// current clock second*/
	}
	
	// Reads a date from current postion in file, and returns in String form
	String ReadSystemTime(int filehandle)
	// Pass: An open file handle in FILE_READ mode
	// Returns a String in pretty date format
	// No error checking
	{
		int year = 0, month = 0, day = 0, hour = 0, minute = 0, second = 0;
		/*REMOVERBPyear = FileReadQuad(filehandle);
		month = FileReadQuad(filehandle);
		day = FileReadQuad(filehandle);
	
		hour = FileReadQuad(filehandle);
		minute = FileReadQuad(filehandle);
		second = FileReadQuad(filehandle);*/
		return TwoDigit(hour)+":"+TwoDigit(minute)+":"+TwoDigit(second)+"  "+TwoDigit(day)+"/"+TwoDigit(month)+"/"+str(year);
	}
	
	// Saves the current position in the game to file specified
	void MenuSaveGame(int slot)
	{
		/*REMOVERBPint xc, yc;
		String gamedesc = "Save game in file: save/save"+ThreeDigit(slot)+".sve";
		int ver = (VERSION_4_BYTE << 24) + (VERSION_3_BYTE << 16) + (VERSION_2_BYTE << 8) + VERSION_1_BYTE;
		int screenshot = NewImage(ImageWidth(screen), ImageHeight(screen));
		VImage mini = new VImage(32, 24);
		RenderMap(xwin, ywin, screenshot);
		scaleblit(0, 0, 32, 24, screenshot, mini);
		int savegame = FileOpen("save/save"+ThreeDigit(slot)+".sve", FILE_WRITE);
		// [4] Header Size
			FileWriteQuad(savegame, 3120 + len(VERSION_STRING) + len(gamedesc));
		// [4] Version Number
			FileWriteQuad(savegame, ver);
		// [6 + LEN] Version String
			FileWriteQuad(savegame, len(VERSION_STRING));
			FileWriteString(savegame, VERSION_STRING);
		// [4] Game Time Count
			FileWriteQuad(savegame, global_gametime + systemtime);
		// [24] System Time
			WriteSystemTime(savegame);
		// [6 + LEN] Game Description
			FileWriteQuad(savegame, len(gamedesc));
			FileWriteString(savegame, gamedesc);
		// [3072] Mini Screenshot
			for (yc = 0; yc <  24; yc++)
			{
				for (xc = 0; xc <  32; xc++)
				{
					FileWriteQuad(savegame, GetPixel(xc, yc, mini));
				}
			}
		// Party Data
			FileWriteQuad(savegame, PartySize());
			FileWriteQuad(savegame, MAX_PARTY_SIZE);
			for (yc = 0; yc <  MAX_PARTY_SIZE; yc++)
			{
				FileWriteQuad(savegame, party[yc]);
			}
	
			FileWriteQuad(savegame, MAX_CAST);
			FileWriteQuad(savegame, MAX_STATS);
			FileWriteQuad(savegame, MAX_EQUIP_SLOTS);
			for (yc = 0; yc <  MAX_CAST; yc++)
			{
				FileWriteQuad(savegame, master_cast[yc].level);
				FileWriteQuad(savegame, master_cast[yc].exp);
				FileWriteQuad(savegame, master_cast[yc].cur_hp);
				FileWriteQuad(savegame, master_cast[yc].cur_mp);
				for (xc = 0; xc <  MAX_STATS; xc++)
				{
					FileWriteQuad(savegame, master_cast[yc].stats[xc]);
				}
				for (xc = 0; xc <  MAX_EQUIP_SLOTS; xc++)
				{
					FileWriteQuad(savegame, master_cast[yc].equipment[xc]);
				}
			}
		// Item Data
			FileWriteQuad(savegame, money); // Nearly forgot!
			FileWriteQuad(savegame, _supply_count);
			FileWriteQuad(savegame, MAX_SUPPLIES);
			for (yc = 0; yc <  MAX_SUPPLIES; yc++)
			{
				FileWriteQuad(savegame, supply_inventory[yc].item_ref);
				FileWriteQuad(savegame, supply_inventory[yc].quant);
			}
	
			FileWriteQuad(savegame, _equip_count);
			FileWriteQuad(savegame, MAX_EQUIPMENT);
			for (yc = 0; yc <  MAX_EQUIPMENT; yc++)
			{
				FileWriteQuad(savegame, equipment_inventory[yc].item_ref);
				FileWriteQuad(savegame, equipment_inventory[yc].quant);
			}
	
			FileWriteQuad(savegame, _key_count);
			FileWriteQuad(savegame, MAX_KEY_ITEMS);
			for (yc = 0; yc <  MAX_KEY_ITEMS; yc++)
			{
				FileWriteQuad(savegame, key_item_inventory[yc].item_ref);
				FileWriteQuad(savegame, key_item_inventory[yc].quant);
			}
		// Options Data
			FileWriteQuad(savegame, global_music_volume);
			FileWriteQuad(savegame, sfx_volume);
			FileWriteQuad(savegame, interface_volume);
			FileWriteQuad(savegame, global_noscroll);
			FileWriteQuad(savegame, global_menuluc);
			FileWriteQuad(savegame, _menu_on);
			FileWriteQuad(savegame, 0); // Padding
			FileWriteQuad(savegame, MENU_COLOUR_NUM);
			for (yc = 0; yc <  MENU_COLOUR_NUM; yc++)
			{
				FileWriteQuad(savegame, menu_colour[yc]);
			}
		// Flag Data
			FileWriteQuad(savegame, MAX_FLAGS);
			for (yc = 0; yc <  MAX_FLAGS; yc++)
			{
				FileWriteQuad(savegame, flags[yc]);
			}
		// Map Data
			FileWriteQuad(savegame, len(curmap.name));
			FileWriteString(savegame, curmap.name);
			FileWriteQuad(savegame, entity.x[playerent]);
			FileWriteQuad(savegame, entity.y[playerent]);
		// Footer
			FileWriteQuad(savegame, FileCurrentPos(savegame));
		FileClose(savegame);
		FreeImage(mini);
		FreeImage(screenshot);*/
	}
	
	// Loads a game position from file specified
	void MenuLoadGame(int slot)
	{/*REMOVERBP
		int xc, yc, max_xc, max_yc;
		String mapname;
		int savegame = FileOpen("save/save"+ThreeDigit(slot)+".sve", FILE_READ);
		// Header Size
			FileSeekPos(savegame, FileReadQuad(savegame), SEEK_SET);
		// Party Data
			curpartysize = FileReadQuad(savegame);
			if (FileReadQuad(savegame) != MAX_PARTY_SIZE) error("Your MAX_PARTY_SIZE is screwed");
			for (yc = 0; yc <  MAX_PARTY_SIZE; yc++)
			{
				party[yc] = FileReadQuad(savegame);
			}
	
			if (FileReadQuad(savegame) != MAX_CAST) error("Your MAX_CAST is screwed");
			if (FileReadQuad(savegame) != MAX_STATS) error("Your MAX_STATS is screwed");
			if (FileReadQuad(savegame) != MAX_EQUIP_SLOTS) error("Your MAX_EQUIP_SLOTS is screwed");
			for (yc = 0; yc <  MAX_CAST; yc++)
			{
				master_cast[yc].level = FileReadQuad(savegame);
				master_cast[yc].exp = FileReadQuad(savegame);
				master_cast[yc].cur_hp = FileReadQuad(savegame);
				master_cast[yc].cur_mp = FileReadQuad(savegame);
				for (xc = 0; xc <  MAX_STATS; xc++)
				{
					master_cast[yc].stats[xc] = FileReadQuad(savegame);
				}
				for (xc = 0; xc <  MAX_EQUIP_SLOTS; xc++)
				{
					master_cast[yc].equipment[xc] = FileReadQuad(savegame);
				}
			}
		// Item Data
			money = FileReadQuad(savegame); // Nearly forgot!
			_supply_count = FileReadQuad(savegame);
			if (FileReadQuad(savegame) != MAX_SUPPLIES) error("Your MAX_SUPPLIES is screwed");
			for (yc = 0; yc <  MAX_SUPPLIES; yc++)
			{
				supply_inventory[yc].item_ref = FileReadQuad(savegame);
				supply_inventory[yc].quant = FileReadQuad(savegame);
			}
	
			_equip_count = FileReadQuad(savegame);
			if (FileReadQuad(savegame) != MAX_EQUIPMENT) error("Your MAX_EQUIPMENT is screwed");
			for (yc = 0; yc <  MAX_EQUIPMENT; yc++)
			{
				equipment_inventory[yc].item_ref = FileReadQuad(savegame);
				equipment_inventory[yc].quant = FileReadQuad(savegame);
			}
	
			_key_count = FileReadQuad(savegame);
			if (FileReadQuad(savegame) != MAX_KEY_ITEMS) error("Your MAX_KEY_ITEMS is screwed");
			for (yc = 0; yc <  MAX_KEY_ITEMS; yc++)
			{
				key_item_inventory[yc].item_ref = FileReadQuad(savegame);
				key_item_inventory[yc].quant = FileReadQuad(savegame);
			}
		// Options Data
			global_music_volume = FileReadQuad(savegame);
			sfx_volume = FileReadQuad(savegame);
			interface_volume = FileReadQuad(savegame);
			global_noscroll = FileReadQuad(savegame);
			global_menuluc = FileReadQuad(savegame);
			_menu_on = FileReadQuad(savegame); // Ah well
			xc = FileReadQuad(savegame); // Padding
			if (FileReadQuad(savegame) != MENU_COLOUR_NUM) error("Your MENU_COLOUR_NUM is screwed");
			for (yc = 0; yc <  MENU_COLOUR_NUM; yc++)
			{
				menu_colour[yc] = FileReadQuad(savegame);
			}
		// Flag Data
			if (FileReadQuad(savegame) != MAX_FLAGS) error("Your MAX_FLAGS is screwed");
			for (yc = 0; yc <  MAX_FLAGS; yc++)
			{
				flags[yc] = FileReadQuad(savegame);
			}
		// Map Data
			xc = FileReadQuad(savegame);
			mapname = FileReadString(savegame);
			if (len(mapname) != xc) error("Incorrect size of map name found");
			xc = FileReadQuad(savegame);
			yc = FileReadQuad(savegame);
		// Footer
			if (FileCurrentPos(savegame) == FileReadQuad(savegame))
			{
				FileClose(savegame);
				global_gametime = save_display[(slot - 1) % 5].gametime - systemtime;
	
	//things to make loading smooth no matter what.
	// this should be in something like LoadUpkeep()
	// Meh.
	// -Grue
	ClearVCLayer();
	cameratracking = 1;
	_title_menu = 0;
				
				V1_MapSwitch(mapname+".map", xc / 16, yc / 16, TBLACK);
			}
			else
			{
				FileClose(savegame);
				error("Errors have occurred reading this save format, load aborted");
			}
			
			*/
	}
	
	// Loads the header of a save file for selection purposes
	static void MenuLoadSaveDisplay(int slot)
	{
		/*REMOVERBPint xc, yc;
		save_display[slot % 5].image = NewImage(32, 24);
		int savegame = FileOpen("save/save"+ThreeDigit(slot + 1)+".sve", FILE_READ);
		//Log("***Loading from: 'save/save"+ThreeDigit(slot + 1)+".sve'");
		// Header Size
			xc = FileReadQuad(savegame);
		// Version Number
			xc = FileReadQuad(savegame);
			if (xc < (VERSION_4_BYTE << 24) + (VERSION_3_BYTE << 16) + (VERSION_2_BYTE << 8) + VERSION_1_BYTE)
			{
				error("Attempting to load old savegame format, errors may occur.");
			}
			log("Quad version: "+str(xc));
		// Version String
			xc = FileReadQuad(savegame);
			save_display[slot % 5].text = FileReadString(savegame);
			if (len(save_display[slot % 5].text) != xc) error("Incorrect size of version String found");
			if (strcmp(save_display[slot % 5].text , VERSION_STRING))
			{
				error("Reading version String: '"+save_display[slot % 5].text+"' does not match that of current: '"+VERSION_STRING+"'");
			}
			//Log("String version: "+save_display[slot % 5].text);
		// Game Time Count
			save_display[slot % 5].gametime = FileReadQuad(savegame);
			//Log("Systime: "+str(xc));
		// System Time
			save_display[slot % 5].time = ReadSystemTime(savegame);
		// Game Description
			xc = FileReadQuad(savegame);
			save_display[slot % 5].text = FileReadString(savegame);
			if (len(save_display[slot % 5].text) != xc) error("Incorrect size of game description found");
			//Log("Text: '"+save_display[slot % 5].text+"'");
		// Mini Screenshot
			for (yc = 0; yc <  24; yc++)
			{
				for (xc = 0; xc <  32; xc++)
				{
					 SetPixel(xc, yc, FileReadQuad(savegame), save_display[slot % 5].image);
				}
			}
		FileClose(savegame);
		//Log("***Done with: 'save/save"+ThreeDigit(slot + 1)+".sve'");*/
	}
	
	// Frees all currently loaded save display slots
	void MenuClearDisplay()
	{
		int i;
		for (i = 0; i < 5; i++)
		{
			if (len(save_display[i].text) >0) MenuFreeSaveDisplay(i);
		}
	}
	
	// Frees the specified save display slot
	void MenuFreeSaveDisplay(int slot)
	{
		//FreeImage(save_display[slot % 5].image);
		save_display[slot % 5].text = "";
	}
	
	// Frees all loaded save display slots, and loads all slots required
	void MenuRefreshSlots()
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