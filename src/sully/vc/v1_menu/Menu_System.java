package sully.vc.v1_menu;

import static core.Script.*;
import static sully.Sully.*;
import static sully.vc.v1_rpg.V1_RPG.*;
import static sully.vc.v1_rpg.V1_Textbox.*;
import static sully.vc.simpletype_rpg.Data.*;
import static sully.vc.simpletype_rpg.Party.*;
import static sully.vc.util.Icons.*;


import java.awt.Color;

import sully.vc.Sfx;

import domain.VFont;
import domain.VImage;

public class Menu_System {
	// menu_system.vc for Sully www.verge-rpg.com
	// Zip 05/09/2004
	// Last update 06/10/2004
	
	public static final int MAX_MENUS = 15;
	public static final int MENU_COLOUR_NUM = 4;
	
	public static final int MENU_PRESS_DELAY = 30;
	public static final int MENU_SLIDE_DELAY = 1;
	
	public static final int MENU_A_X1	= 10;
	public static final int MENU_A_Y1	= 10;
	public static final int MENU_A_X2	= 230;
	public static final int MENU_A_Y2	= 230;
	public static final int MENU_B_X1	= 240;
	public static final int MENU_B_Y1	= 10;
	public static final int MENU_B_X2	= 310;
	public static final int MENU_B_Y2	= 170;
	public static final int MENU_C_X1	= 240;
	public static final int MENU_C_Y1	= 180;
	public static final int MENU_C_X2	= 310;
	public static final int MENU_C_Y2	= 230;
	
	public static final int MENU_CAST_X	= 25;
	public static final int MENU_CAST_Y	= 25;
	public static final int MENU_STATS_X	= 25;
	public static final int MENU_STATS_Y	= 60;
	
	public static final int MENU_COLOR_BORDER_OUT	= 0;
	public static final int MENU_COLOR_BORDER_IN	= 1;
	public static final int MENU_COLOR_PASSIVE		= 2;
	public static final int MENU_COLOR_ACTIVE 		= 3;
	
	public static final int MENU_DESCRIPTION_X = 25;
	public static final int MENU_DESCRIPTION_Y = 228;
	
	
	public static final int CHR_SAD_FRAME = 21;
	
	/*#include "vc/v1_menu/menu_cast.vc"
	//#include "vc/v1_menu/menu_choice.vc"
	#include "vc/v1_menu/menu_equip.vc"
	#include "vc/v1_menu/menu_item.vc"
	#include "vc/v1_menu/menu_main.vc"
	#include "vc/v1_menu/menu_option.vc"
	#include "vc/v1_menu/menu_save.vc"
	#include "vc/v1_menu/menu_shop.vc"
	#include "vc/v1_menu/menu_skill.vc"
	#include "vc/v1_menu/menu_status.vc"
	*/
	//#include "vc/v1_menu/otherstuffs.vc"
	
	// Menu struct
	String name;	//my uid
	public String draw_func;		//the function that does all drawing for this menu
	String control_func;	//the function that does all control-listening for this menu

	
	public static Menu_System master_menus[] = new Menu_System[MAX_MENUS];
	
	static int _menuCount;
	
	static int menu_number;
	
	static boolean menu_done; // menu sentinel.  Whenever it's time for the entire menu to go back to the real world, we set this to 1.
	public static int menu_idx; // the index of the current menu.  Pass this around like a cheap whore!
	public static int menu_option = 0;
	public static int menu_sub = 0;
	public static int menu_cast = 0;
	public static int menu_item = 0;
	static int menu_start = 0;
	static int lastpress = 0;
	public static Color menu_colour[] = new Color[MENU_COLOUR_NUM];
	public static VFont menu_font[] = new VFont[4];
	static int menu_fonth;
	
	public static boolean global_noscroll = true;
	public static int global_menuluc = 0;
	public static int global_gametime;
	public static boolean _menu_on = true; 	// Grue = tit. Loveable, but tit -Zip
							// I HATE YOU SO VERY MUCH! -Grue
	static boolean _menu_is_on;
	
	//
	// Grue's amazing sound effect handles!
	static String sfx_roll = ( "res/system/menu/menu_roll.wav" ); // TODO RBP Change to Load
	static String sfx_beep = ( "res/system/menu/happy_beep.wav" );
	static String sfx_buzz = ( "res/system/menu/angry_buzz.wav" );
	static String sfx_page = ( "res/system/menu/pageturn.wav" );
	static String sfx_wush = ( "res/system/menu/woosh.wav" );
	static String sfx_mony = ( "res/system/menu/BUYSELL.WAV" );
	static String sfx_eqip = ( "res/system/menu/EQUIP.WAV" );
	
	//        --------------------------
	//        Menu Initialisation
	//        --------------------------
	
	// Load the menus into the struct and set defaults
	public static void SMENU_InitMenus()
	{
		//Rafael: changed to full path (sully.vc.v1_menu.etc)
		AddMenu( "main", "sully.vc.v1_menu.Menu_Main.MenuDrawMain", "sully.vc.v1_menu.Menu_Main.MenuControlMain" );
		AddMenu( "item", "sully.vc.v1_menu.Menu_Item.MenuDrawItem", "sully.vc.v1_menu.Menu_Item.MenuControlItem" );
		AddMenu( "skill", "sully.vc.v1_menu.Menu_Skill.MenuDrawSkill", "sully.vc.v1_menu.Menu_Skill.MenuControlSkill" );
		AddMenu( "equip", "sully.vc.v1_menu.Menu_Equip.MenuDrawEquip", "sully.vc.v1_menu.Menu_Equip.MenuControlEquip" );
		AddMenu( "status", "sully.vc.v1_menu.Menu_Status.MenuDrawStatus", "sully.vc.v1_menu.Menu_Status.MenuControlStatus" );
		AddMenu( "option", "sully.vc.v1_menu.Menu_Option.MenuDrawOption", "sully.vc.v1_menu.Menu_Option.MenuControlOption" );
		AddMenu( "save", "sully.vc.v1_menu.Menu_Save.MenuDrawSave", "sully.vc.v1_menu.Menu_Save.MenuControlSave" );
		AddMenu( "cast", "sully.vc.v1_menu.Menu_Cast.MenuDrawCast", "sully.vc.v1_menu.Menu_Cast.MenuControlCast" );
		AddMenu( "equipsub", "sully.vc.v1_menu.Menu_Equip.MenuDrawEquipSub", "sully.vc.v1_menu.Menu_Equip.MenuControlEquipSub" );
		AddMenu( "optionrgb", "sully.vc.v1_menu.Menu_Option.MenuDrawOptionRGB", "sully.vc.v1_menu.Menu_Option.MenuControlOptionRGB" );
		AddMenu( "optionvol", "sully.vc.v1_menu.Menu_Option.MenuDrawOptionVol", "sully.vc.v1_menu.Menu_Option.MenuControlOptionVol" );
	
		ValidateMenuFunctions();
	
		MenuInitColours();
		MenuInitFonts();
		icon_init("res/system/ITEMS.PCX");
	
		hookbutton( 3, "sully.vc.v1_menu.Menu_System.MenuEntry" );
	}
	
	// Add a menu to the game
	public static void AddMenu( String name, String draw_func, String control_func )
	{
		if( _menuCount >= MAX_MENUS )
		{
			error( "AddMenu(): tried to add more than MAX_MENUS ("+str(MAX_MENUS)+")" );
			return;
		}
		else
		{
			master_menus[_menuCount] = new Menu_System();
			master_menus[_menuCount].name 			= name;
			master_menus[_menuCount].draw_func 		= draw_func;
			master_menus[_menuCount].control_func 	= control_func;
	
			_menuCount++;
		}
	}
	
	// Check added menus have appropriate functions
	static void ValidateMenuFunctions()
	{
		int i;
		for( i=0; i<_menuCount; i++ )
		{
			if( !functionexists(master_menus[i].draw_func) )
			{
				error( "ValidateMenuFunctions(): Menu '"+master_menus[i].name+"' requires a callfunc to '"+master_menus[i].draw_func+"' to Draw.  And it doesn't exist." );
			}
	
			if( !functionexists(master_menus[i].control_func) )
			{
				error( "ValidateMenuFunctions(): Menu '"+master_menus[i].name+"' requires a callfunc to '"+master_menus[i].control_func+"' for control handling.  And it doesn't exist." );
			}
		}
	}
	
	// Load and set font variables
	public static void MenuInitFonts()
	{
		menu_font[0] = new VFont(load("res/system/menu_font_white.png"));
		menu_font[1] = new VFont(load("res/system/menu_font_grey1.png"));
		menu_font[2] = new VFont(load("res/system/menu_font_green.png"));
		menu_font[3] = new VFont(load("res/system/menu_font_red.png"));
		menu_fonth = fontheight(menu_font[0]);
		enablevariablewidth(menu_font[0]);
		enablevariablewidth(menu_font[1]);
		enablevariablewidth(menu_font[2]);
		enablevariablewidth(menu_font[3]);
	}
	
	// Set default colour scheme
	public static void MenuInitColours()
	{
		//menu_colour[0] = rgb(224, 224, 255);
		//menu_colour[1] = rgb(160, 224, 160);
		//menu_colour[2] = rgb(96, 96, 192);
		//menu_colour[3] = rgb(64, 160, 64);
		menu_colour[0] = new Color(0, 0, 0);
		menu_colour[1] = new Color(112, 112, 112);
		menu_colour[2] = new Color(144, 144, 144);
		menu_colour[3] = new Color(0, 0, 255);
	}
	
	//        -----------------------------
	//       Basic Menu Control
	//        -----------------------------
	
	public static boolean IsInMenu()
	{
		return _menu_is_on;
	}
	
	// Disable the user opening of the menu
	public static void MenuOff()
	{
		_menu_on = false;
		hookbutton( 3, "" );
	}
	
	// Enable the user opening of the menu
	public static void MenuOn()
	{
		_menu_on = true;
		hookbutton( 3, "sully.vc.v1_menu.Menu_System.MenuEntry" );
	}
	
	public static boolean MenuCanBeOn()
	{
		return _menu_on;
	}
	
	// Return the index of a menu name
	public static int MenuGet(String name)
	{
		int i;
		for (i = 0; i < _menuCount; i++)
		{
			if (master_menus[i].name.equalsIgnoreCase(name) // rbp	
					) return i;
		}
		error("POOOOOOOO!");
		System.exit(-1); 
		return -1;
	}
	
	// Determines if the passed menu name has focus
	static boolean MenuIsActive(String name)
	{
		if (menu_idx == MenuGet(name)) return true;
		else return false;
	}
	
	// Return to the root menu
	static void MenuRoot()
	{
		menu_idx = MenuGet("Main");
		menu_sub = 0-1;
		menu_item = 0-1;
		menu_cast = 0-1;
		menu_start = 0;
	}
	
	// Enter the menu system and control
	public static void MenuEntry()
	{
		unpress(3);
		_menu_is_on = true;
		menu_done = false;
		menu_option = 0;
		MenuRoot();
		hookbutton( 3, "" );
		
		MenuHappyBeep(); //hey, you've entered a menu!  Chirp happily about it!
		
		EntStart(); // rbp
		while( !menu_done )
		{
			MenuBackGroundDraw(); //draw universal things
			callfunction( master_menus[menu_idx].draw_func );
	
			showpage();
			callfunction( master_menus[menu_idx].control_func );
		}
		EntFinish(); // rbp
		_menu_is_on = false;
		hookbutton( 3, "sully.vc.v1_menu.Menu_System.MenuEntry" );
	}
	
	
	//        -----------------------------------
	//       Generic Menu Functions
	//        -----------------------------------
	
	// This function does the rendering stuff you want to happen before the custom render each cycle.
	public static void MenuBackGroundDraw()
	{
		render();
	}
	
	// A small notification box
	static void MenuMinibox(String text, String draw_func)
	{
		int wid = textwidth( menu_font[0], " "+text+" " );
		int border = 5;
		
		int mini_hold = menu_idx;
		//menu_idx = 1000000;
		while(!MenuConfirm() && !MenuCancel())
		{		
			MenuBackGroundDraw(); //draw universal things
			callfunction(draw_func);
			
			MenuDrawBackground(((imagewidth(screen)-wid)/2)-border, 110, ((imagewidth(screen)-wid)/2)+wid+border, 130, true); // CatchMe
			printcenter(160, 120 - (menu_fonth / 2) + 1, screen, menu_font[0], " "+text+" ");
	
			showpage();
		}
		//menu_idx = mini_hold;
	}
	
	
	// A small verification box
	// returns 1 if choice was accepted, 0 if cancelled.
	// the choice index is accessible via GetMenuChoiceAnswer();
	static int MenuMiniChoicebox(String text, String choices, String draw_func)
	{
		int wid = textwidth( menu_font[0], " "+text+" " );
		int border = 5;
		int done=0;
		//int mini_hold = menu_idx;
		//menu_idx = 1000000;
		while(done==0)
		{		
			MenuBackGroundDraw(); //draw universal things
			callfunction(draw_func);
			
			MenuDrawBackground(((imagewidth(screen)-wid)/2)-border, 110, ((imagewidth(screen)-wid)/2)+wid+border, 130, true); // CatchMe
			printcenter(160, 120 - (menu_fonth / 2) + 1, screen, menu_font[0], " "+text+" ");
			
			MenuSimplePrompt( choices );
	
			done = (MenuConfirm() ? 1: 0);
	
			if(done==0)
				done = (MenuCancel() ? -1: 0);
	
			showpage();
		}
		
		if( done > 0 )
			return 1;
		else
			return 0;
		//menu_idx = mini_hold;
	}
	
	
	static int _menu_simple_choice;
	
	static void MenuSimplePrompt(String choices)
	{
		int i;
		int font_h = fontheight(promptBox_font) + PROMPT_Y_BUFF;
		int tex_font_h = fontheight(textBox_font) + TEXTBOX_Y_BUFF;
		int prompt_wid, prompt_high;
		int prompt_x1, prompt_x2;
		
		int ptr_w = textwidth(promptBox_font, "> ");
		
		int count = tokencount(choices, "&");
		if (count > MAX_PROMPT_OPTIONS) error("Moar than "+str(MAX_PROMPT_OPTIONS)+" options passed to the choicebox. This may cause graphical oddness");
	
		int prompt_y1;
		
		if( PROMPT_BOTM_Y != 0) {
			prompt_y1 = PROMPT_BOTM_Y - (font_h * count) - (PROMPT_PADDING*2);
			
			prompt_high = PROMPT_BOTM_Y - prompt_y1;
			
		} else {
			prompt_y1 = PROMPT_Y;
			
			prompt_high = (PROMPT_PADDING*2)+TEXTBOX_Y_BUFF+(count * font_h);
		}
		
		if( PROMPT_WIDTH !=0 ) {
			prompt_wid = PROMPT_WIDTH;
	
		} else { //dynamic width!
			prompt_wid = _LongestWidth(choices) + (PROMPT_PADDING*2) + (font_h*2);
		}
		
		//if the promptbox would've gone off the screen... make it align with the textbox
		if( (PROMPT_X+prompt_wid) > imagewidth(screen) ) {
			prompt_x2 = TEXTBOX_BORDER_WIDTH+TEXTBOX_BORDER_X1;
			prompt_x1 = prompt_x2 - prompt_wid;
		} else {
			prompt_x2 = PROMPT_X+prompt_wid;
			prompt_x1 = PROMPT_X;
		}
			
		Menu1ArrowSetSounds("MenuHappyBeep" );
		_menu_simple_choice = MenuControlArrows(_menu_simple_choice, count);
	
		MenuDrawBackground(prompt_x1, prompt_y1-TEXTBOX_Y_BUFF, prompt_x1+prompt_wid, prompt_high+prompt_y1-TEXTBOX_Y_BUFF, true);
	
		//set the clipping rectangle so we cannot draw outside the promptbox's area!
		setclip( 	prompt_x1+PROMPT_PADDING, prompt_y1+PROMPT_PADDING, 
					prompt_x2-PROMPT_PADDING,prompt_y1+prompt_high-PROMPT_PADDING, screen );
	
		//print out the options.
		for(i = 0; i <= count; i++)	
		{
			printstring(prompt_x1+PROMPT_PADDING+ptr_w+TEXTBOX_Y_BUFF, prompt_y1+PROMPT_PADDING+((i) * font_h), screen, promptBox_font, gettoken(choices, "&", i));
		}
	
		//print the pointer...
		printstring(prompt_x1+PROMPT_PADDING+TEXTBOX_Y_BUFF, prompt_y1+PROMPT_PADDING+((_menu_simple_choice) * font_h), screen, promptBox_font, ">");
	
		//restore the clipping rectangle.
		setclip(0,0, imagewidth(screen), imageheight(screen), screen);
	
		Menu1ArrowSetSounds( "" );
	}
	
	public static int GetMenuChoiceAnswer() {
		return _menu_simple_choice;
	}
	
	
	//        -----------------------------------
	//        Menu Control Functions
	//        -----------------------------------
	
	// If the confirm button is pressed
	public static boolean MenuConfirm()
	{
		if (b1)
		{
			unpress(1);
			return true;
		}
		return false;
	}
	
	// If the cancel button is pressed
	public static boolean MenuCancel()
	{
		if (b2)
		{
			unpress(2);
			return true; // Was -1
		}
		if (b3)
		{
			unpress(3);
			return true; // Was -1
		}
		return false;
	}
	
	
	//global vars for the sound callfuncs set by Menu1ArrowSetSounds() and called by MenuControlArrows()
	static String _m1ASnd;
	
	// Sets the sounds to be played while using MenuControlArrows()
	// s1 is called whenever an arrow is pressed.
	//
	// to turn off a sound, just set it to ""
	//
	public static void Menu1ArrowSetSounds( String s1 )
	{
		_m1ASnd = s1;
	}
	
	// A basic 1 dimenional menu control
	public static int MenuControlArrows(int value, int limit)
	{
		if (up || left)
		{
			if (lastpress + MENU_PRESS_DELAY < timer)
			{
				value = (value - 1 + limit) % limit;
				lastpress = timer;
				
				callmenufunction(_m1ASnd); //call the sound function defined with Menu1ArrowSetSounds()
			}
		}
		else if (down || right)
		{
			if (lastpress + MENU_PRESS_DELAY < timer)
			{
				value = (value + 1) % limit;
				lastpress = timer;
				
				callmenufunction(_m1ASnd); //call the sound function defined with Menu1ArrowSetSounds()
			}
		}
		else lastpress = 0;
		return value;
	}
	
	
	static //global vars for the sound callfuncs set by Menu2ArrowSetSounds() and called by MenuControlTwoArrows()
	String _m2ASnd1;
	static String _m2ASnd2;
	
	// Sets the sounds to be played while using MenuControlTwoArrows()
	// s1 is called when the up/down control is changed
	// s2 is called when the left/right control is changed
	//
	// to turn off a sound, just set it to ""
	//
	static void Menu2ArrowSetSounds( String s1, String s2 )
	{
		_m2ASnd1 = s1;
		_m2ASnd2 = s2;
	}
	
	
	// A generic control method for 2 dimensions
	// Rafael: changed parameters to ints and return to int[] . GetInt() won't work in Java.
	static int[] MenuControlTwoArrows(int int_one, int lim_one, int int_two, int lim_two)
	{
		int change = 0, return_1 = int_one, return_2 = int_two;
		if (up)
		{
			if (lastpress + MENU_PRESS_DELAY < timer)
			{
				return_1 = lim_one==0? 0: ((int_one - 1 + lim_one) % lim_one);
				lastpress = timer;
				change = 1;
				
				callmenufunction(_m2ASnd1); //call the sound function defined with Menu2ArrowSetSounds()
			}
		}
		else if (down)
		{
			if (lastpress + MENU_PRESS_DELAY < timer)
			{
				return_1 = lim_one==0? 0: ((int_one + 1) % lim_one);
				lastpress = timer;
				change = 1;
				
				callmenufunction(_m2ASnd1); //call the sound function defined with Menu2ArrowSetSounds()
			}
		}
		else lastpress = 0;
		
		if (left)
		{
			return_2 = lim_two==0? 0: (((int_two) - 1 + lim_two) % lim_two);
			unpress(7);
			change += 2;
			
			callmenufunction(_m2ASnd2); //call the sound function defined with Menu2ArrowSetSounds()
		}
		else if (right)
		{
			return_2 = lim_two==0? 0: (((int_two) + 1) % lim_two);
			unpress(8);
			change += 2;
			callmenufunction(_m2ASnd2); //call the sound function defined with Menu2ArrowSetSounds()
		}
		return new int[]{change, return_1, return_2};
	}
	
	// A different version of the above. should probably be combined
	// Rafael: changed parameters to ints. No need to call GetInt()
	static int[] MenuControlFastArrows(int int_one, int max_one, int int_two, int lim_two)
	{
		int return_1 = int_one, return_2 = int_two;
		
		if (up)
		{
			if (lastpress + MENU_SLIDE_DELAY < timer && int_one < max_one)
			{
				return_1 = ((int_one) + 1);
				lastpress = timer;
			}
		}
		else if (down)
		{
			if (lastpress + MENU_SLIDE_DELAY < timer && int_one > 0)
			{
				return_1 = ((int_one) - 1);
				lastpress = timer;
			}
		}
		else lastpress = 0;
		if (left)
		{
			return_2 = ((int_two) - 1 + lim_two) % lim_two;
			unpress(7);
			return new int[]{1, return_1, return_2};
		}
		else if (right)
		{
			return_2 = ((int_two) + 1) % lim_two;
			unpress(8);
			return new int[]{1, return_1, return_2};
		}
		return new int[]{0, return_1, return_2};
	}
	
	
	//        -----------------------------------
	//        Menu Drawing Functions
	//        -----------------------------------
	
	// Draws a box. This needs moar options
	static void MenuDrawBackgroundSimple(int x1, int y1, int x2, int y2, boolean active)
	{
		setlucent(20);
		rect(x1, y1, x2 - 1, y2 - 1,
		 menu_colour[3], screen);
		rect(x1 + 1, y1 + 1, x2 - 2, y2 - 2,
		 menu_colour[0], screen);
		rect(x1 + 2, y1 + 2, x2 - 3, y2 - 3,
		 menu_colour[1], screen);
		rectfill(x1 + 3, y1 + 3, x2 - 4, y2 - 4,
		 menu_colour[2 + (active ? 1: 0)], screen);
		setlucent(0);
	}
	
	static Color ColourDivider(Color colour, int division)
	{
		return new Color(colour.getRed() / division, colour.getGreen() / division, colour.getBlue() / division);
	}
	
	// Draws a box. This needs moar options
	public static void MenuDrawBackground(int x1, int y1, int x2, int y2, boolean active)
	{
		setlucent(global_menuluc * 10);
		//RBP TODO: Make it work as a filter?
		//setcustomcolorfilter(ColourDivider(menu_colour[2 + (active ? 1: 0)], 4), menu_colour[2 + (active ? 1: 0)]);
		//VImage act = imageshell(x1 + 4, y1 + 4, x2 - 5 - x1, y2 - 5 - y1, screen);
		//colorfilter(CF_CUSTOM, act);
		//act = null;

		rectfill(x1+4, y1+4, x2-4, y2-4, menu_colour[2 + (active?1:0)], screen);
		setlucent(0);
	
		
		line(x1, y1 + 2, x1, y2 - 3, menu_colour[0], screen); // TL -> BL
		line(x1 + 2, y1, x2 - 3, y1, menu_colour[0], screen); // TL -> TR
		
		line(x2 - 1, y2 - 3, x2 - 1, y1 + 2, menu_colour[0], screen); // BR -> TR
		line(x2 - 3, y2 - 1, x1 + 2, y2 - 1, menu_colour[0], screen); // BR -> BL
		
	
		rect(x1 + 1, y1 + 1, x2 - 2, y2 - 2, menu_colour[1], screen);
		 setpixel(x1 + 1, y1 + 1, menu_colour[0], screen); // TL
		 setpixel(x2 - 2, y1 + 1, menu_colour[0], screen); // TR
		 setpixel(x1 + 1, y2 - 2, menu_colour[0], screen); // BL
		 setpixel(x2 - 2, y2 - 2, menu_colour[0], screen); // BR
		
	
		rect(x1 + 2, y1 + 2, x2 - 3, y2 - 3, menu_colour[2], screen);
		 setpixel(x1 + 2, y1 + 2, menu_colour[1], screen); // TL
		 setpixel(x2 - 3, y1 + 2, menu_colour[1], screen); // TR
		 setpixel(x1 + 2, y2 - 3, menu_colour[1], screen); // BL
		 setpixel(x2 - 3, y2 - 3, menu_colour[1], screen); // BR
		
	
		rect(x1 + 3, y1 + 3, x2 - 4, y2 - 4, menu_colour[0], screen);
		 setpixel(x1 + 3, y1 + 3, menu_colour[2], screen); // TL
		 setpixel(x2 - 4, y1 + 3, menu_colour[2], screen); // TR
		 setpixel(x1 + 3, y2 - 4, menu_colour[2], screen); // BL
		 setpixel(x2 - 4, y2 - 4, menu_colour[2], screen); // BR
		 
		if( VCCustomFilterOn() )
		{
			VCCustomFilterRestore();
		}
	}
	
	// Draws the main menu list on the right side of the screen
	static void MenuBlitRight(boolean active, int selected)
	{
		MenuDrawBackground(MENU_B_X1, MENU_B_Y1, MENU_B_X2, MENU_B_Y2, active);
		printstring(248, selected * 20 + 25, screen, menu_font[0], ">");
		printstring(255, 25, screen, menu_font[0], "ITEM");
		printstring(255, 45, screen, menu_font[0], "SKILL");
		printstring(255, 65, screen, menu_font[0], "EQUIP");
		printstring(255, 85, screen, menu_font[0], "STATUS");
		printstring(255, 105, screen, menu_font[0], "OPTION");
		printstring(255, 125, screen, menu_font[1 - (can_save ? 1: 0)], "SAVE");
		printstring(255, 145, screen, menu_font[0], "LOAD");
		MenuBlitBottom();
	}
	
	// Generic gold/timer thang
	static void MenuBlitBottom()
	{
		MenuDrawBackground(MENU_C_X1, MENU_C_Y1, MENU_C_X2, MENU_C_Y2, false);
		printstring(245, 185, screen, menu_font[0], moneyname + ":");
		printright(305, 195, screen, menu_font[0], str(money));
		printstring(245, 205, screen, menu_font[0], "Time:");
		printright(305, 215, screen, menu_font[0], GetTimeString(global_gametime + systemtime));
	}
	
	// Return time in (H)H:MM:SS format
	static String GetTimeString(int time)
	{
		return str(time/360000)+":"+TwoDigit((time/6000)%60)+":"+TwoDigit((time/100)%60);
	}
	
	// Displays the party in the main window
	static void MenuBlitCenter(boolean active)
	{
		int i, frame;
		MenuDrawBackground(MENU_A_X1, MENU_A_Y1, MENU_A_X2, MENU_A_Y2, active);
		for (i = 0; i < PartySize(); i++)
		{
			if(i == menu_cast) frame = GetFrameWalk();
			else frame = 0;
			MenuBlitCast(i, i, frame);
		}
	}
	
	// Makey little people walk
	static int GetFrameWalk()
	{
		int t = (timer / 10) % 8;
		// Zip: ignore this.  I had to do something cryptic lest I go insane
		return (((t % 4) + (t % 3)) / 2) + (2*(t / 5))  + (t / 6) - (2*(t / 7));
	}
	
	// Makey little people walk
	static int GetFrameSad()
	{
		return CHR_SAD_FRAME;
	}
	
	// Easy Wrapper
	static void MenuBlitCast(int member, int location, int frame)
	{
		MenuBlitCastFull(MENU_CAST_X, MENU_CAST_Y + (location * 42), member, frame);
	
		MenuBlitCastPoints(MENU_CAST_X, MENU_CAST_Y + (location * 42), member, location);
	}
	
	// Displays info about one party member
	static void MenuBlitCastFull(int x, int y, int member, int frame)
	{
		blitentityframe(x, y + 10, master_cast[party[member]].entity, frame, screen);
		printstring(x + 25, y, screen, menu_font[0], master_cast[party[member]].name);
	//	printstring(x + 35, y + 10, screen, menu_font[0], master_classes[master_cast[party[member]].class_ref].name);
		printstring(x + 115, y, screen, menu_font[0], "Level: ");
		printright(x + 185, y, screen, menu_font[0], str(master_cast[party[member]].level));
		printstring(x + 115, y + 10, screen, menu_font[0], "HP:");
		printright(x + 161, y + 10, screen, menu_font[0], str(master_cast[party[member]].cur_hp)+"/");
		printstring(x + 115, y + 20, screen, menu_font[0], "MP:");
		printright(x + 161, y + 20, screen, menu_font[0], str(master_cast[party[member]].cur_mp)+"/");
	}
	
	
	// Draw the max point separately, as equip needs to do them apart
	static void MenuBlitCastPoints(int x, int y, int member, int location)
	{
		printright(x + 185, y + 10, screen, menu_font[0], str(master_cast[party[member]].stats[STAT_MAX_HP]));
		printright(x + 185, y + 20, screen, menu_font[0], str(master_cast[party[member]].stats[STAT_MAX_MP]));
	}
	
	
	//        -----------------------------------
	//       Generic Useful Functions
	//        -----------------------------------
	// Should probably move to another file
	
	// Zip: My lovely text wrapper... not speed optimised
	String WrapText(VFont wt_font, String wt_s, int wt_linelen)
	{
		int wt_i;
		String wt_tpara = "";
		String wt_tline = "";
		String wt_output = "";
		int wt_breaks = tokencount(wt_s, "&");
		while (len(wt_s)!=0)
		{
			wt_tpara = gettoken(wt_s, "&", 0);
			wt_s = right(wt_s, len(wt_s) - len(wt_tpara) - 1);
			if (textwidth(wt_font, wt_tpara) < wt_linelen)
			{
				wt_output = wt_output + wt_tpara + "&";
			}
			else
			{
				while (len(wt_tpara)==0)
				{
					wt_tline = wt_tline + gettoken(wt_tpara, " ", 0);
					wt_tpara = right(wt_tpara, len(wt_tpara) - len(gettoken(wt_tpara, " ", 0)) - 1);
					if (textwidth(wt_font, wt_tline + gettoken(wt_tpara, " ", 0) + " ") > wt_linelen)
					{
						wt_output = wt_output + wt_tline + "&";
						wt_tline = "";
					}
					else wt_tline = wt_tline + " ";
				}
				if (len(wt_tline)==0)
				{
					wt_output = wt_output + left(wt_tline, len(wt_tline) - 1) + "&";
					wt_tline = "";
				}
			}
		}
		return wt_output + "&";
	}
	
	// Returns number in three digit format. Hyper useful
	static String ThreeDigit(int number)
	{
		if (number > 999) error("Number to convert to 3 digits past 999. This is bad.");
		if (number < 0) error("Number to convert to 3 digits negative. This is bad.");
		if (number < 10) return "00"+str(number);
		if (number < 100) return "0"+str(number);
		return str(number);
	}
	
	// Returns number in two digit format. Hyper useful
	static String TwoDigit(int number)
	{
		if (number > 99) error("Number to convert to 2 digits past 99. This is bad.");
		if (number < 0) error("Number to convert to 2 digits negative. This is bad.");
		if (number < 10) return "0"+str(number);
		return str(number);
	}
	
	
	//
	// Grue's amazing menu soundeffect wrappers!
	public static void MenuHappyBeep() {
		playsound( sfx_beep, Sfx.interface_volume );
	}
	
	public static void MenuAngryBuzz() {
		playsound( sfx_buzz, Sfx.interface_volume );
	}
	
	public static void MenuRollSound() {
		playsound( sfx_roll, Sfx.interface_volume );
	}
	
	public static void MenuWoosh() {
		playsound( sfx_wush, Sfx.interface_volume );
	}
	
	public static void MenuPageTurn() {
		playsound( sfx_page, Sfx.interface_volume );
	}
	
	public static void MenuPurchase() {
		playsound( sfx_mony, Sfx.interface_volume );
	}
	
	public static void MenuForceEquip() {
		playsound( sfx_eqip, Sfx.interface_volume );
	}
	
	
	
	
	public static void MakeBox(int x1, int y1, int w, int h, boolean active, VImage dest)
	{
		int x2 = x1+w;
		int y2 = y1+h; 

		setlucent(global_menuluc * 10);
		//RBP TODO: Make it work as a filter?
//		setcustomcolorfilter(ColourDivider(menu_colour[2 + (active ? 1: 0)], 4), menu_colour[2 + (active ? 1: 0)]);
//		VImage act = imageshell(x1 + 4, y1 + 4, x2 - 5 - x1, y2 - 5 - y1, dest);
//		colorfilter(CF_CUSTOM, act);
//		act = null;

		rectfill(x1+4, y1+4, x2-4, y2-4, menu_colour[2 + (active?1:0)], screen);
		setlucent(0); 
	
		
		line(x1, y1 + 2, x1, y2 - 3, menu_colour[0], dest); // TL -> BL
		line(x1 + 2, y1, x2 - 3, y1, menu_colour[0], dest); // TL -> TR
		
		line(x2 - 1, y2 - 3, x2 - 1, y1 + 2, menu_colour[0], dest); // BR -> TR
		line(x2 - 3, y2 - 1, x1 + 2, y2 - 1, menu_colour[0], dest); // BR -> BL
		
	
		rect(x1 + 1, y1 + 1, x2 - 2, y2 - 2, menu_colour[1], dest);
		 setpixel(x1 + 1, y1 + 1, menu_colour[0], dest); // TL
		 setpixel(x2 - 2, y1 + 1, menu_colour[0], dest); // TR
		 setpixel(x1 + 1, y2 - 2, menu_colour[0], dest); // BL
		 setpixel(x2 - 2, y2 - 2, menu_colour[0], dest); // BR
		
	
		rect(x1 + 2, y1 + 2, x2 - 3, y2 - 3, menu_colour[2], dest);
		 setpixel(x1 + 2, y1 + 2, menu_colour[1], dest); // TL
		 setpixel(x2 - 3, y1 + 2, menu_colour[1], dest); // TR
		 setpixel(x1 + 2, y2 - 3, menu_colour[1], dest); // BL
		 setpixel(x2 - 3, y2 - 3, menu_colour[1], dest); // BR
		
	
		rect(x1 + 3, y1 + 3, x2 - 4, y2 - 4, menu_colour[0], dest);
		 setpixel(x1 + 3, y1 + 3, menu_colour[2], dest); // TL
		 setpixel(x2 - 4, y1 + 3, menu_colour[2], dest); // TR
		 setpixel(x1 + 3, y2 - 4, menu_colour[2], dest); // BL
		 setpixel(x2 - 4, y2 - 4, menu_colour[2], dest); // BR

		if( VCCustomFilterOn() )
		{
			VCCustomFilterRestore();
		}
	}
	
	// RBP Wrapper for menu calling functions
	private static void callmenufunction(String function) {
		callfunction("sully.vc.v1_menu.Menu_System" + "." + function);
	}
	
}