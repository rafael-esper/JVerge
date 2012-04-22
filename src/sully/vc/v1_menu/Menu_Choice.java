package sully.vc.v1_menu;

import static core.Script.*;
import static core.Controls.*;
import static sully.vc.v1_menu.Menu_System.*;
import static sully.vc.v1_menu.Menu_Equip.*;
import static sully.vc.v1_rpg.V1_RPG.*;

import java.awt.Color;

import domain.VFont;

public class Menu_Choice {
	// REDUNDANT FILE
	// menu_choice.vc for Sully www.verge-rpg.com
	// Zip 05/09/2004
	// Last update 06/10/2004
	
	
	//        -----------------
	//       Choice Box
	//        -----------------
	// Some different versions as they are all horrible
	static int cursor_pos;
	public static String MenuInputBox( String s )
	{
		
		
		int max_lines = 5;
		String inputtext, wrapinput;
		
		inputtext = s;
		
		int linenum, i, start = 0, current;
		int wid = 180;
		int high = 80;
		int xpos = (imagewidth(screen) / 2) - (wid / 2);
		int ypos = (imageheight(screen) / 2) - (high / 2);
		int mini_hold = menu_idx;
		menu_idx = 1000000; // To make a grue happy
		
		unpress(0);
		unpress(1);
		
		
		clearLastKey(); // lastpressed = 0;
		cursor_pos = 0;
		while(cursor_pos > 0-1)
		{
	//log( "MIB 1" );
			MenuBackGroundDraw(); // Draw universal things
	//log( "MIB 2" );
			if (IsInMenu()) 
				callfunction(master_menus[mini_hold].draw_func); // RBP: Added callfunction
	//log( "MIB 3" );
			V1_Box(xpos, ypos, wid, high);
	//log( "MIB 4" );
			wrapinput = wraptext(menu_font[1], inputtext, wid - 30);
	//log( "MIB 5" );
			linenum = tokencount(wrapinput, "&");
	//log( "MIB 6" );
			current = GetCurrentToken(wrapinput, "&", cursor_pos);
	//log( "MIB 7" );
			if (linenum == 0) linenum = 1;
			else if (current > max_lines - 2) start = current - max_lines + 1;
			else start = 0;
	//log( "MIB 8" );
			MenuDrawSubWindow(xpos + 8, ypos + 8, xpos + wid - 8, ypos + high - 8, 0-1, menu_fonth, linenum, start, 0);
	//log( "MIB 9" );
			for (i = start; i < linenum; i++)
			{
				printstring(xpos + 12, ypos + 12 + ((i - start) * menu_fonth), screen, menu_font[0], gettoken(wrapinput, "&", i));
				if (i >= start + 4) i = linenum + 1;
			}
	//log( "MIB 10" );
			BlitCursor(xpos + 12, ypos + 12 + ((current - start) * menu_fonth), menu_font[0], cursor_pos - gettokenPos(wrapinput, "&", current, 0), gettoken(wrapinput, "&", current));
	//log( "MIB 11" );
			showpage();
	//log( "MIB 12" );
			inputtext = InputReadString(inputtext, 1000);
	//log( "MIB 13" );
			if (!MenuCancel()) return "";
	//log( "MIB 14" );
		}
	//log( "MIB RETURNING: "+inputtext );
		return inputtext;
	}
	
	static void BlitCursor(int x, int y, VFont fnt, int pos, String text)
	{
		if (systemtime % 100 > 50)
		{
			line(x+textwidth(fnt, left(text, pos)), y, x+textwidth(fnt, left(text, pos)), y+fontheight(fnt)-1, new Color(255, 255, 255), screen);
			line(x+textwidth(fnt, left(text, pos))+1, y, x+textwidth(fnt, left(text, pos))+1, y+fontheight(fnt)-1, Color.BLACK, screen);
		}
	
	}
	
	static String InputReadString(String current_String, int max_length)
	// Pass: Variable name of String, Maximum allow characters
	// Returns: The String after input
	// Requires: Global int cursor_pos
	{
	
	//log( "IRS: 1" );
		UpdateControls(); // Just In Case
		
	//log( "IRS: 2" );
		if(key[SCAN_BACKSP]) // Delete chr before cursor
		{
			
	//log( "IRS: 3" );
			clearLastKey(); // lastpressed = 0;
			
	//log( "IRS: 4" );
			key[SCAN_BACKSP] = 0;
			
	//log( "IRS: 5" );
			if (cursor_pos > 0) // If there is a chr before the cursor
			{
	//log( "IRS: 6" );
				max_length = len(current_String) - cursor_pos;
	//log( "IRS: 7" );
				cursor_pos--;
	//log( "IRS: 8" );
				return left(current_String, cursor_pos) + right(current_String, max_length);
			}
	//log( "IRS: 9" );
		}
		else if(key[SCAN_DEL] || key[211]) // Delete chr after cursor
		{
	//log( "IRS: 10" );
			clearLastKey(); // lastpressed = 0;
	//log( "IRS: 11" );
			key[SCAN_DEL] = 0; // Num pad DEL key
	//log( "IRS: 12" );
			key[211] = 0; // Delete key
	//log( "IRS: 13" );
			if (cursor_pos < len(current_String)) // If there is a chr after the cursor
			{
	//log( "IRS: 14" );
				max_length = len(current_String) - cursor_pos - 1;
	//log( "IRS: 15" );
				return left(current_String, cursor_pos) + right(current_String, max_length);
			}
	//log( "IRS: 16" );
		}
		else if(b1) // Input completed
		{
	//log( "IRS: 17" );
			clearLastKey(); // lastpressed = 0;
	//log( "IRS: 18" );
			if(key[SCAN_RSHIFT] || key[SCAN_LSHIFT])
			{
	//log( "IRS: 19" );
				unpress(1);
	//log( "IRS: 20" );
				max_length = len(current_String) - cursor_pos;
	//log( "IRS: 21" );
				//cursor_pos++;
				return left(current_String, cursor_pos) + "&" + right(current_String, max_length);
			}
			else cursor_pos = 0-1; // Check for 0-1 as a confirm of String input
	//log( "IRS: 22" );
		}
		else if(key[SCAN_TAB]) // Move to next input
		{
	//log( "IRS: 23" );
			key[SCAN_TAB] = 0;
	//log( "IRS: 24" );
			cursor_pos = 0-1; // Check for 0-1 as a input move
	//log( "IRS: 25" );
		}
		else if(up) // Up to start of String
		{
	//log( "IRS: 26" );
			unpress(UP);
	//log( "IRS: 27" );
			cursor_pos = 0;
	//log( "IRS: 28" );		
		}
		else if(down) // Down to end of String
		{
	//log( "IRS: 29" );
			unpress(DOWN);
	//log( "IRS: 30" );
			cursor_pos = len(current_String);
	//log( "IRS: 31" );
		}
		else if(left) // Move cursor left one space
		{
	//log( "IRS: 32" );
			unpress(LEFT);
	//log( "IRS: 33" );
			if (cursor_pos > 0) cursor_pos--;
	//log( "IRS: 34" );
		}
		else if(right) // Move cursor right one space
		{
	//log( "IRS: 35" );
			unpress(RIGHT);
	//log( "IRS: 36" );
			if (cursor_pos < len(current_String)) cursor_pos++;
	//log( "IRS: 37" );
		}
		else if(lastpressed)
		{
	//log( "IRS: 38 (lastpressed)" );
			clearLastKey(); // lastpressed = 0;
	//log( "IRS: 39" );
			if(key[SCAN_V]) // Move cursor right one space
			{
	//log( "IRS: 40" );
				if (key[SCAN_CTRL] && len(clipboard.text))
				{
	//log( "IRS: 41" );
					key[SCAN_V] = 0;
	//log( "IRS: 42" );
					cursor_pos = len(clipboard.text);
	//log( "IRS: 43" );
					if (cursor_pos > max_length)
					{
	//log( "IRS: 44" );
						cursor_pos = max_length;
	//log( "IRS: 45" );
						return left(clipboard.text, max_length);
					}
					else return clipboard.text;
	//log( "IRS: 46" );
				}
	//log( "IRS: 47" );
			}
			if (len(chr(lastkey)) && len(current_String) < max_length) // If it's a String-able chr, and String is not too long
			{
	//log( "IRS: 48" );
				max_length = len(current_String) - cursor_pos;
	//log( "IRS: 49" );
				cursor_pos++;
	//log( "IRS: 50" );
				return left(current_String, cursor_pos - 1) + chr(lastkey) + right(current_String, max_length);
			}
	//log( "IRS: 51" );
		}
	//log( "IRS: 52 (returning: '" +current_String+"')" );
	
		clearLastKey(); // lastpressed = 0;
		return current_String;
	}
	
	
	String TokenLeft(String full, String tokens, int pos) // Excludes token
	{
		if (pos < 1) return "";
		pos = gettokenPos(full, tokens, pos, 0);
		if (pos == len(full)) return full;
		else return left(full, pos);
	}
	
	String TokenRight(String full, String tokens, int pos) // Includes token
	{
		if (pos < 1) return full;
		pos = gettokenPos(full, tokens, pos, 1);
		if (pos == len(full)) return "";
		else return right(full, len(full) - pos + 1);
	}
	
	static int GetCurrentToken(String teststr, String tokens, int pos)
	{
		int i, t;
		boolean last = false;
		int count = 0;
		if (pos == 0) return 0;
		for (i = 0; i < pos; i++)
		{
			if (chrstrq(mid(teststr, i, 1), tokens)!=0)
			{
				if (!last)
				{
					count++;
					last = true;
				}
			}
			else if (last)
			{
				last = false;
			}
		}
		return count;
	}
	
	static int gettokenPos(String teststr, String tokens, int pos, int tok)
	{
		int i;
		boolean last = false;
		int count = 0;
		int length = len(teststr);
		if (pos == 0) return 0;
		for (i = 0; i < length; i++)
		{
			if (chrstrq(mid(teststr, i, 1), tokens)!=0)
			{
				if (!last)
				{
					count++;
					if (count == pos && tok == 0) return i;
					last = true;
				}
			}
			else if (last)
			{
				if (count == pos) return i; //  && first == 0
				last = false;
			}
		}
		return i;
	}
	
	static int chrstrq(String achr, String astr)
	{
		int i;
		int alen = len(astr);
		for (i = 0; i < alen; i++)
		{
			if (strcmp(achr, mid(astr, i, 1))) return 1;
		}
		return 0;
	}
	
	// Returns number of occurrences of one String within another
	int strstr(String asub, String astr)
	{
		int i;
		int count = 0;
		int al = len(asub);
		int alen = len(astr);
		for (i = 0; i < alen; i++)
		{
			if (strcmp(asub, mid(astr, i, al))) count++;
		}
		return count;
	}
	
	int strstrp(String asub, String astr, int start)
	{
		int i;
		int al = len(asub);
		int alen = len(astr);
		for (i = start; i < alen; i++)
		{
			if (strcmp(asub, mid(astr, i, al))) return i;
		}
		return alen;
	}
	
	/*
	int asc(String char)
	{
		int i;
		for (i = 0; i < 256; i++)
		{
			// To optimise, create an array[256] and sort front to back by commonest occurence
			if(!strcmp(char, chr(i))) return i;
		}
		return 0;
	}
	*/
	
	String strrep(String sub, String rep, String source)
	{
		int i;
		String dest = "";
		int count = 0;
		int sublen = len(sub);
		int sourcelen = len(source);
		for (i = 0; i < sourcelen; i++)
		{
			if (strcmp(sub, mid(source, i, sublen)))
			{
				dest = dest + mid(source, i - count, count) + rep;
				count = 0;
				i += sublen - 1;
			}
			else count++;
		}
		return dest + right(source, count);
	}
	
	/*
	int MenuPartyBox()
	{
		int i, selected, frame;
		int high = 56;
		int w = 56;
		int wid = PartySize() * w;
		int xpos = (imagewidth(screen) / 2) - (wid / 2);
		int ypos = (imageheight(screen) / 2) - (high / 2);
		int mini_hold = menu_idx;
		menu_idx = 1000000; // To make a grue happy
	
		while(!MenuConfirm())
		{
			MenuBackGroundDraw(); // Draw universal things
			if (IsInMenu()) (master_menus[mini_hold].draw_func);
			V1_Box(xpos - (w / 4), ypos, wid +(w / 2), high);
			for(i = 0; i < PartySize(); i++)
			{
				if (i == selected) frame = GetFrameWalk();
				else frame = GetFrameSad();
				BlitEntityFrame(xpos + (w * i), ypos + 10, master_cast[party[i]].entity, frame, screen);
				printstring(xpos + 18 + (w * i), ypos + 5, screen, menu_font[0], master_cast[party[i]].name);
				//printstring(xpos + (w * i), ypos + 10, screen, menu_font[0], master_classes[master_cast[party[i]].class_ref].name);
				printstring(xpos + 18 + (w * i), ypos + 18, screen, menu_font[0], "Level: ");
				PrintRight(xpos + 56 + (w * i), ypos + 18, screen, menu_font[0], str(master_cast[party[i]].level));
				printstring(xpos + (w * i), ypos + 30, screen, menu_font[0], "HP:");
				PrintRight(xpos + 40 + (w * i), ypos + 30, screen, menu_font[0], str(master_cast[party[i]].cur_hp)+"/");
				printstring(xpos + (w * i), ypos + 40, screen, menu_font[0], "MP:");
				PrintRight(xpos + 40 + (w * i), ypos + 40, screen, menu_font[0], str(master_cast[party[i]].cur_mp)+"/");
				PrintRight(xpos + 56 + (w * i), ypos + 30, screen, menu_font[0], str(master_cast[party[i]].stats[STAT_MAX_HP]));
				PrintRight(xpos + 56 + (w * i), ypos + 40, screen, menu_font[0], str(master_cast[party[i]].stats[STAT_MAX_MP]));
			}
			showpage();
			selected = MenuControlArrows(selected, PartySize());
			if (MenuCancel()) return 0-1;
		}
	
		return party[selected];
	}*/
	
	// Basic ugly choice box. Pants
	int MenuChoiceBox(String question, String choices)
	{
		int i;
		int count = tokencount(choices, "&");
		if (count > 4) error("Moar than 4 options passed to the choicebox. This will cause graphical oddness");
		menu_item = 0;
		while(MenuConfirm())
		{
			MenuBackGroundDraw(); // Draw universal things
	
			menu_item = MenuControlArrows(menu_item, count);
			MenuDrawBackground(5, 185, 315, 235, true);
			printstring(12, 192, screen, v1rpg_LargeFont, question);
			for(i = 0; i < count; i++)
			{
				printstring((i / 2) * 150 + 20, (i % 2) * 14 + 206, screen, v1rpg_LargeFont, gettoken(choices, "&", i));
			}
			printstring((menu_item / 2) * 150 + 10, (menu_item % 2) * 14 + 206, screen, v1rpg_LargeFont, ">");
			showpage();
		}
		return menu_item;
	}
	
	// Expand-to-fit choice box
	int MenuChoiceBoxVar(String question, String choices)
	{
		int i;
		int count = tokencount(choices, "&");
		if (count > 6) error("Moar than 6 options passed to the choicebox. This will cause graphical oddness");
		menu_item = 0;
		while(MenuConfirm())
		{
			MenuBackGroundDraw(); // Draw universal things
	
			menu_item = MenuControlArrows(menu_item, count);
			MenuDrawBackground(5, 215 - (14 * count), 315, 235, true);
			printstring(12, 220 - (14 * count), screen, v1rpg_LargeFont, question);
			for(i = 0; i < count; i++)
			{
				printstring(20, 232 - ((count - i)  * 14), screen, v1rpg_LargeFont, gettoken(choices, "&", i));
			}
			printstring(10, 232 - ((count - menu_item)  * 14), screen, v1rpg_LargeFont, ">");
			showpage();
		}
		return menu_item;
	}
	
	// Uses subwindow, but still doesn't fit in skimpy textbox space well
	int MenuChoiceBoxWin(String question, String choices)
	{
		int i;
		int count = tokencount(choices, "&");
		menu_item = 0;
		while(MenuConfirm())
		{
			MenuBackGroundDraw(); // Draw universal things
	
			menu_item = MenuControlArrows(menu_item, count);
			if (menu_start + 1 < menu_item) menu_start = menu_item - 1;
			else if (menu_start > menu_item) menu_start = menu_item;
			MenuDrawBackground(5, 185, 315, 235, true);
			MenuDrawSubWindow(10, 200, 310, 230, menu_item, 14, count, menu_start, 4);
			printstring(12, 190, screen, v1rpg_LargeFont, question);
			for(i = menu_start; i < count; i++)
			{
				printstring(25, (i - menu_start) * 14  + 204, screen, v1rpg_LargeFont, gettoken(choices, "&", i));
				if (menu_start + 1 <= i) i = count + 1;
			}
			showpage();
		}
		return menu_item;
	}
	
	// vec's lovely tbox for reference
	/*
	void MyTextBox(int sp, String s1, String s2, String s3)
	{
		timer=0;
		while (!b1 && timer/2<=len(s1))
		{
			Render();
			TBlitFrame(1, SPEECH_Y, speechprt, sp, screen);
			SetLucent(50);
			RectFill(3, 187, 317, 236, RGB(0,0,255), screen);
			SetLucent(0);
			TBlit(0, 184, textboxframe, screen);
			printstring(6, 190, screen, v1rpg_LargeFont, left(s1,timer/2));
			showpage();
		}
	
		timer=0;
		while (!b1 && timer/2<=len(s2))
		{
			Render();
			TBlitFrame(1, SPEECH_Y, speechprt, sp, screen);
			SetLucent(50);
			RectFill(3, 187, 317, 236, RGB(0,0,255), screen);
			SetLucent(0);
			TBlit(0, 184, textboxframe, screen);
			printstring(6, 190, screen, v1rpg_LargeFont, s1);
			printstring(6, 204, screen, v1rpg_LargeFont, left(s2,timer/2));
			showpage();
		}
	
		timer=0;
		while (!b1 && timer/2<=len(s3))
		{
			Render();
			TBlitFrame(1, SPEECH_Y, speechprt, sp, screen);
			SetLucent(50);
			RectFill(3, 187, 317, 236, RGB(0,0,255), screen);
			SetLucent(0);
			TBlit(0, 184, textboxframe, screen);
			printstring(6, 190, screen, v1rpg_LargeFont, s1);
			printstring(6, 204, screen, v1rpg_LargeFont, s2);
			printstring(6, 218, screen, v1rpg_LargeFont, left(s3,timer/2));
			showpage();
		}
		unpress(1);
	
		while (!b1)
		{
			Render();
			TBlitFrame(1, SPEECH_Y, speechprt, sp, screen);
			SetLucent(50);
			RectFill(3, 187, 317, 236, RGB(0,0,255), screen);
			SetLucent(0);
			TBlit(0, 184, textboxframe, screen);
			printstring(6, 190, screen, v1rpg_LargeFont, s1);
			printstring(6, 204, screen, v1rpg_LargeFont, s2);
			printstring(6, 218, screen, v1rpg_LargeFont, s3);
			showpage();
		}
		unpress(1);
	}
	
	*/
}