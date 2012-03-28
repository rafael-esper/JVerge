package sully.vc.simpletype_rpg.parser;

import static core.Script.*;
import static sully.vc.simpletype_rpg.parser.Data_load.*;
import static sully.vc.util.Error_handler.*;

// data_front.vc for Sully www.verge-rpg.com
// Zip 28/08/2004
public class Data_front {

	static String global_console; // String for load information
	
	// Load data and display progress
	public static void SSAC_InitData()
	{
		int boo; // Because everyone needs a variable
		boo = LoadStructSkillTypes();
		if (boo>0) global_console = global_console + "Completed loading: "+str(boo)+" skilltype entries from file|";
		else  global_console = global_console + "Fatal error occurred on loading skilltype entries from file|";
		if ((ERROR_HANDLING & 1)!=0) DumpStructSkillTypes();
		boo = LoadStructClass();
		if (boo>0) global_console = global_console + "Completed loading: "+str(boo)+" class entries from file|";
		else  global_console = global_console + "Fatal error occurred on loading class entries from file|";
		if ((ERROR_HANDLING & 1)!=0) DumpStructClass();
		boo = LoadStructCast();
		if (boo>0) global_console = global_console + "Completed loading: "+str(boo)+" cast entries from file|";
		else  global_console = global_console + "Fatal error occurred on loading cast entries from file|";
		if ((ERROR_HANDLING & 1)!=0) DumpStructCast();
		boo = LoadStructSkills();
		if (boo>0) global_console = global_console + "Completed loading: "+str(boo)+" skill entries from file|";
		else  global_console = global_console + "Fatal error occurred on loading skill entries from file|";
		if ((ERROR_HANDLING & 1)!=0) DumpStructSkills();
		boo = LoadStructItems();
		if (boo>0) global_console = global_console + "Completed loading: "+str(boo)+" item entries from file|";
		else  global_console = global_console + "Fatal error occurred on loading item entries from file|";
		//if (ERROR_HANDLING & 1) DumpStructItems();
		boo = LoadStructEquip();
		if (boo>0) global_console = global_console + "Completed loading: "+str(boo)+" equip entries from file|";
		else  global_console = global_console + "Fatal error occurred on loading equip entries from file|";
		if ((ERROR_HANDLING & 1)!=0) DumpStructItems();
		global_console = global_console + "Time to load and log: "+str(timer);
		ShowFlagScreen();
		//if (ERROR_HANDLING & 2) MessageBox("Don't panic Grue: This is just the end of the world!");
		FlagsVH.FlagWipe();
		global_linenum = 0-1;
	}
	
	// Show progress of data load
	static void ShowFlagScreen()
	{
		/*
		
		int r, i;
		RectFill(0, 0, ImageWidth(screen), ImageHeight(screen), 0, screen);
		for (r = 0; r < 8; r++)
		{
			for (i = 0; i < 32; i++)
			{
				RectFill(2*(i+1)+2,  2+(4*r), 2*(i+1)+5,  5+(4*r), rgb(127,255*FlagLook(i+(r*32)),0), screen);
			}
		}
		for (i = TokenCount(global_console, "&"); i >= 0; i--)
		{
			PrintString(40, 120+(15*i), screen, 0, GetToken(global_console, "&", i));
		}
		ShowPage();
		
		*/
	}
}