package sully.vc.v1_menu;

import static core.Script.*;
import static sully.vc.v1_menu.Menu_System.*;

import java.awt.Color;

import sully.vc.Sfx;

public class Menu_Option {
	// menu_option.vc for Sully www.verge-rpg.com
	// Zip 05/09/2004
	// Last update 06/10/2004
	
	//        -----------------
	//       Option Menu
	//        -----------------
	
	void MenuControlOption()
	{
		Menu1ArrowSetSounds( "MenuHappyBeep" );
	
		Menu2ArrowSetSounds( "MenuHappyBeep","MenuPageTurn" );
	
		if (menu_sub == 0)
		{
			MenuControlTwoArrows("menu_sub", 4, "menu_item", 5);
			if (menu_sub != 0) menu_item = 0;
		}
		else if (menu_sub == 2)
		{
			MenuControlTwoArrows("menu_sub", 4, "global_noscroll", 2);
		}
		else if (menu_sub == 3)
		{
			MenuControlTwoArrows("menu_sub", 4, "global_menuluc", 11);
		}
		else 
		{
			menu_sub = MenuControlArrows(menu_sub, 4);
		}
		
		if (MenuConfirm())
		{
			if (menu_sub == 0)
			{
				menu_cast = 0;
				if (menu_item < 4)
				{
					menu_start = menu_colour[menu_item].getRGB(); // RBP Set
					menu_idx = MenuGet("OptionRGB");
				}
				else MenuInitColours();
			}
			else if (menu_sub == 1)
			{
				menu_idx = MenuGet("OptionVol");
			}
		}
		if (MenuCancel())
		{
			Menu1ArrowSetSounds( "" );
			Menu2ArrowSetSounds( "","" );
			MenuHappyBeep();
			MenuRoot();
		}
	}
	
	void MenuDrawOption()
	{
		MenuDisplayOption(MenuIsActive("Option"));
		if (menu_sub == 0)
		{
			printstring(20, 32, screen, menu_font[0], ">");
			if (menu_item < 4) rect(27 + (menu_item * 50), 47, 63 + (menu_item * 50), 83, menu_colour[2], screen);
			else rect(166, 88, 214, 90+menu_fonth, menu_colour[2], screen);
		}
		else if (menu_sub == 1)
		{
			printstring(20, 105, screen, menu_font[0], ">");
	
		}
		else if (menu_sub == 2)
		{
			printstring(20, 150, screen, menu_font[0], ">");
		}
		else if (menu_sub == 3)
		{
			printstring(20, 170, screen, menu_font[0], ">");
		}
	}
	
	void MenuDrawOptionVol()
	{
		MenuDisplayOption(MenuIsActive("OptionVol"));
		rect(20 + (menu_item * 65), 115, 86 + (menu_item * 65), 145, menu_colour[2], screen);
	}
	
	void MenuDisplayOption(boolean active)
	{
		MenuBlitRight(false, menu_option);
		MenuDrawBackground(MENU_A_X1, MENU_A_Y1, MENU_A_X2, MENU_A_Y2, active);
		printright(220, 15, screen, menu_font[0], "Option");
		printstring(30, 32, screen, menu_font[0], "Interface colours");
		printstring(170, 90, screen, menu_font[0], "Defaults");
		MenuBlitColour(0);
		MenuBlitColour(1);
		MenuBlitColour(2);
		MenuBlitColour(3);
		printstring(30, 105, screen, menu_font[0], "Volume levels");
		printstring(23, 118, screen, menu_font[0], "Music:");
		printstring(88, 118, screen, menu_font[0], "FX:");
		printstring(153, 118, screen, menu_font[0], "Menu:");
		MenuBlitSlider(0, Sfx.global_music_volume);
		MenuBlitSlider(1, Sfx.sfx_volume);
		MenuBlitSlider(2, Sfx.interface_volume);
		printstring(30, 150, screen, menu_font[0], "Text Scroll");
		printcenter(130, 150, screen, menu_font[0], "ON");
		printcenter(190, 150, screen, menu_font[0], "OFF");
		rect(110 + (global_noscroll?1:0 * 60), 148, 150 + (global_noscroll?1:0 * 60), 159, menu_colour[2], screen);
		printstring(30, 170, screen, menu_font[0], "Menu Lucency: " + str(global_menuluc*10) + "%");
	}
	
	
	void MenuControlOptionVol()
	{
		if (menu_item == 0) menu_sub = Sfx.global_music_volume;
		else if (menu_item == 1) menu_sub = Sfx.sfx_volume;
		else if (menu_item == 2) menu_sub = Sfx.interface_volume;
		if (MenuControlFastArrows("menu_sub", 100, "menu_item", 3) !=0)
		{
			if (menu_item == 0)
			{
				Sfx.global_music_volume =  menu_sub;
				setmusicvolume(Sfx.global_music_volume);
			}
			else if (menu_item == 1) Sfx.sfx_volume = menu_sub;
			else if (menu_item == 2) Sfx.interface_volume = menu_sub;
		}
		if (MenuConfirm() || MenuCancel())
		{
			MenuHappyBeep();
			menu_sub = 1;
			menu_idx = MenuGet("Option");
		}
	}
	
	void MenuControlOptionRGB()
	{
		if (menu_cast == 0) menu_sub = menu_colour[menu_item].getRed();
		else if (menu_cast == 1) menu_sub = menu_colour[menu_item].getGreen();
		else if (menu_cast == 2) menu_sub = menu_colour[menu_item].getBlue();
		if (MenuControlFastArrows("menu_sub", 255, "menu_cast", 3)!=0)
		{
			if (menu_cast == 0) menu_colour[menu_item] = RGB(menu_sub, menu_colour[menu_item].getGreen(), menu_colour[menu_item].getBlue());
			else if (menu_cast == 1) menu_colour[menu_item] = RGB(menu_colour[menu_item].getRed(), menu_sub, menu_colour[menu_item].getBlue());
			else if (menu_cast == 2) menu_colour[menu_item] = RGB(menu_colour[menu_item].getRed(), menu_colour[menu_item].getGreen(), menu_sub);
		}
		if (MenuConfirm())
		{
			menu_cast = 0-1;
			menu_start = 0;
			menu_sub = 0;
			menu_idx = MenuGet("Option");
		}
		if (MenuCancel())
		{
			MenuHappyBeep();
			menu_cast = 0-1;
			menu_sub = 0;
			menu_colour[menu_item] = new Color(menu_start); // RBP unSet
			menu_start = 0;
			menu_idx = MenuGet("Option");
		}
	}
	
	void MenuDrawOptionRGB()
	{
		MenuDisplayOption(MenuIsActive("OptionRGB"));
		rect(27 + (menu_item * 50), 47, 63 + (menu_item * 50), 83, menu_colour[2], screen);
		rect(23 + (menu_cast * 65), 88, 87 + (menu_cast * 65), 102, menu_colour[2], screen);
		rect(25, 90, 85, 100, RGB(menu_colour[menu_item].getRed(), 0, 0), screen);
		rect(90, 90, 150, 100, RGB(0, menu_colour[menu_item].getGreen(), 0), screen);
		rect(155, 90, 215, 100, RGB(0, 0, menu_colour[menu_item].getBlue()), screen);
		printstring(35, 92, screen, menu_font[0], "R:"+str(menu_colour[menu_item].getRed()));
		printstring(100, 92, screen, menu_font[0], "G:"+str(menu_colour[menu_item].getGreen()));
		printstring(165, 92, screen, menu_font[0], "B:"+str(menu_colour[menu_item].getBlue()));
	}
	
	// Displays a square of colour for options
	void MenuBlitColour(int number)
	{
		rect(30 + (number * 50), 50, 60 + (number * 50), 80, menu_colour[number], screen);
		rect(29 + (number * 50), 49, 61 + (number * 50), 81, Color.BLACK, screen);
	}
	
	// Displays a linear slider of pretty triangle type for options
	void MenuBlitSlider(int number, int value)
	{
		triangle(23 + (number * 65), 140,
		 23 + (number * 65) + 60, 140,
		 23 + (number * 65) + 60, 140 - 15, Color.BLACK, screen);
		triangle(20 + (number * 65), 137,
		 20 + (number * 65) + (value * 60 / 100), 137,
		 20 + (number * 65) + (value * 60 / 100), 137 - (value * 15 / 100), menu_colour[2], screen);
	}

}