package sully.vc.v1_rpg;

import static core.Script.*;
import static sully.vc.v1_rpg.V1_RPG.*;
import static sully.vc.v1_menu.Menu_System.*;
import sully.vc.util.Animation;

import domain.VFont;
import domain.VImage;

// All the various textbox functionality for the v1rpg library.
//
// Originally by vec, converted by zip, then extended and completely 
// grossified by Grue.
//
// At present, 
//
//
public class V1_Textbox {
	
	/****** Textbox variables *******/
	
	//how many lines of font per textbox
	public static int TEXTBOX_LINES = 3;
	
	//how much y-space to add to the bottom of a textbox line.
	public static int TEXTBOX_Y_BUFF = 2;
	
	
	//the drawing area of the textbox's background
	public static int TEXTBOX_BORDER_X1 = 1;
	public static int TEXTBOX_BORDER_Y1 = 189;
	public static int TEXTBOX_BORDER_WIDTH	= 317;
	public static int TEXTBOX_BORDER_HIGH		= 50;
	
	//the drawing-area of the textbox 
	//(the boundry you want the text to actually sit in)
	
	public static int TEXTBOX_TEXTAREA_X		= 6;
	public static int TEXTBOX_TEXTAREA_Y		= 194;
	public static int TEXTBOX_TEXTAREA_WIDTH	= 307;
	public static int TEXTBOX_TEXTAREA_HIGH	= 40;
	
	//the coordinates of the bobbing arrow for TextBoxM()
	public static int TEXTMORE_X	=	300;
	static int TEXTMORE_Y	=	232;
	
	
	
	
	// Specifies how much y-space to put between a Textbox or Promptbox line.
	//
	void SetV1TextboxYBuffer( int y )
	{
		TEXTBOX_Y_BUFF = y;
	}
	
	// Sets how many lines of text a textbox maxes out at (AutoText(), 
	// PromptDirect(), and TextboxDirect() care mostly about this.  TextBox(), 
	// TextBoxM(), and Prompt() max out at 3 lines without some abuse or changes.
	//
	void SetV1MaxTextLines( int i )
	{
		TEXTBOX_LINES = i;
	}
	
	
	// Sets the bounding box for the background box of the v1rpg textbox and 
	// promptbox functions.
	//
	void SetV1TextboxBG( int x, int y, int w, int h )
	{
		TEXTBOX_BORDER_X1 = x;
		TEXTBOX_BORDER_Y1 = y;
	
		TEXTBOX_BORDER_WIDTH	= w;
		TEXTBOX_BORDER_HIGH		= h;
	}
	
	// Sets the bounding box for the text of the v1rpg textbox and promptbox 
	// functions.
	//
	void SetV1TextArea( int x, int y, int w, int h  )
	{
		TEXTBOX_TEXTAREA_X		= x;
		TEXTBOX_TEXTAREA_Y		= y;
		TEXTBOX_TEXTAREA_WIDTH	= w;
		TEXTBOX_TEXTAREA_HIGH	= h;	
	}
	
	// Changes the coordinates of the bobby moretext arrow.
	//
	void SetV1MoreTextArrowPos( int x, int y )
	{
		TEXTMORE_X = x;
		TEXTMORE_Y = y;
	}
	
	
	
	
	// Sets the Small Font for the v1rpg library.
	//
	void v1_SetSmallFont( VFont fnt_handle )
	{
		v1rpg_SmallFont = fnt_handle;
	}
	
	// Restores the Small Font for the v1rpg library to it's original font.
	//
	void v1_RestoreSmallFont()
	{
		v1rpg_SmallFont = _v1rpg_SmallFont;
	}
	
	// returns the font handle of the v1rpg library's current small font.
	//
	void v1_GetSmallFont()
	{
		v1rpg_SmallFont = _v1rpg_SmallFont;
	}
	
	// Sets the Large Font for the v1rpg library.
	//
	void v1_SetLargeFont( VFont fnt_handle )
	{
		v1rpg_LargeFont = fnt_handle;
	}
	
	// Restores the Large Font for the v1rpg library to it's original font.
	//
	void v1_RestoreLargeFont()
	{
		v1rpg_LargeFont = _v1rpg_LargeFont;
	}
	
	// returns the font handle of the v1rpg library's current Large font.
	//
	void v1_GetLargeFont()
	{
		v1rpg_LargeFont = _v1rpg_LargeFont;
	}
	
	
	
	
	///////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////
	/****** Speech portrait defines *******/
	
	static //the speech portraits
	int speechprt = Animation.LoadAnimation("res\\images\\speech\\speech.pcx", 32, 32);
	
	//the x/y coordinates of the topleft corner of the speech portrait
	public static final int SPEECH_X	=	1;
	public static final int SPEECH_Y	=	189-33;	
	
	// textbox speech portrait defines. 
	// these are just indexes on the image loaded into 
	// speechprt.
	public static final int T_NONE		=	0;
	public static final int T_DARIN		=	1;
	public static final int T_SARA		=	2;
	public static final int T_DEXTER	=	3;
	public static final int T_CRYSTAL	=	4;
	public static final int T_GALFREY	=	5;
	public static final int T_STAN		=	6;
	public static final int T_SULLY		=	7;
	public static final int T_BUNNY 	=	8;
	public static final int T_MANTA		=	9;
	public static final int T_SLASHER	=	10;
	public static final int T_BIRD		=	11;
	public static final int T_BUBBA		=	12;
	public static final int T_SANCHO	=	13;
	
	
	///////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////
	
	/****** Promptbox defines *******/
	
	
	//the x of the left-side of the promptbox.  
	//  to auto-right-align with the textbox, set to any value higher than the current 
	//  x-resolution.  10000 is a safe value for this for the next few years at least. ;)
	public static int PROMPT_X		= 10000;
	
	//the fixed width of the promptbox.  set to 0 for dynamic mode!
	public static int PROMPT_WIDTH	= 0;
	
	//the y of the bottom edge of the promptbox.  If you'd rather define the top edge, 
	//  set this to 0 and PROMPT_Y will be used for the top edge.  If PROMPT_BOTM_Y is nonzero, 
	//  PROMPT_Y is completely ignored.
	public static int PROMPT_BOTM_Y	= 190;
	public static int PROMPT_Y		= 1;
	
	
	
	//how much y-space to add to the bottom of a promptbox line.
	public static int PROMPT_Y_BUFF =		2;
	
	//max promptbox options
	public static int MAX_PROMPT_OPTIONS =	8;
	
	//the padding on the interior of the promptbox.
	public static int PROMPT_PADDING =	4;
	
	
	///////////////////////////////////////////////////////////////////////////////
	//
	//
	//
	// There should probably be a whole passel of Promptbox accessors too, but I 
	// really don't want to make'm at this juncture.  Mainly because the 
	// promptbox's implementation is kinda icky in general at the moment.
	//
	// -Grue
	//
	//
	//
	///////////////////////////////////////////////////////////////////////////////
	
	
	
	// 1 sets Textscrolling on, 
	// 0 turns it off.
	void SetTextBoxScroll( boolean on )
	{
		if( on ) {
			global_noscroll = false;
		} else {
			global_noscroll = true;
		}
	}
	
	//returns 1 if text scroll mode is on, 0 if it's off.
	int GetTextBoxScrollMode()
	{
		if( global_noscroll ) return 0;
		return 1;
	}
	
	public static final int  MAX_AUTOTEXT_PAGES = 32;
	static String autoTextPages[] = new String[MAX_AUTOTEXT_PAGES];
	static String AT_temp;
	static int autoTextPageCount;

	static int atTempCount;
	
	// Takes a single String of text and does as many TextBoxM()'s and a 
	// final TextBox() as needed.
	//
	// | and/or TAB will force a newline.
	// ~ and/or NEWLINE will force an entirely new box.
	//
	// VERGEDev doesn't advocate the use of Autotext for most cases because it 
	// ruins the dramatic effect of well-plotted out textboxes.  Done correctly,
	// your game is awesomely poetic and quotable.  However, having massive boxes 
	// of text that don't have complete thoughts in one screen ruins the 
	// illusion.
	//
	// Ask vec for a more in-depth explanation sometime. ;)
	//
	// However, autotext is very useful if you're dynamically resizing your boxes,
	// or pulling dynamic content from somewhere, etc etc, so here's the function anyways.
	//
	public static void AutoText( int sp, String master_text )
	{
		ClearATBuffer();
		String page_delim = "~"+chr(10);
		
		int i;
		String tok;
		
		//for each page chunk...
		for( i=0; i<tokencount(master_text,page_delim); i++ ) {
			
			tok = gettoken( master_text,page_delim,i );
			
			HandlePageChunk(tok);
		}
		
		for( i=0; i<autoTextPageCount; i++ ) {
			
			if( i==(autoTextPageCount-1) ) {
				TextBoxDirect(sp, 0, autoTextPages[i]);
			} else {
				TextBoxDirect(sp, 1, autoTextPages[i]);
			}
		}
	}
	
	// Helper Function.  Handles pageChunks.
	//
	static void HandlePageChunk( String text ) {
		String line_delim = "&"+chr(9);
		
		int i;
		String tok;
		
		for( i=0; i<tokencount(text,line_delim); i++ ) {
			
			tok = gettoken( text,line_delim,i );
			
			HandleLineChunk( tok );
			
			if( atTempCount == TEXTBOX_LINES ) {
				SaveATPage();
			}
		}	
		
		if( !AT_temp.equals("") ) {
		
			SaveATPage();
		}
	}
	
	//helper function.  Handles lineChunks
	static void HandleLineChunk( String text ) {
	
		String temp="", tok;
		int i;
		
		for( i=0; i<tokencount(text," "); i++ ) {
			
			tok = gettoken(text," ",i);
			if( textwidth(v1rpg_LargeFont,temp+tok+" ") > TEXTBOX_TEXTAREA_WIDTH ) {
				
				AT_temp = AT_temp + temp + "&";
				atTempCount++;
				temp = tok + " ";
			} else {
				temp = temp + tok + " ";
			}
			
			if( atTempCount == TEXTBOX_LINES ) {
				SaveATPage();
			}
		}
		
		AT_temp = AT_temp + temp + "&";
		atTempCount++;
	}
	
	// Helper function. 
	// Adds a page to the AT array.  clears temp vars.
	static void SaveATPage() {
		
		String line_delim = "&"; //+chr(9);
		
		while( tokencount(AT_temp,line_delim) < TEXTBOX_LINES ) {
			AT_temp = AT_temp + line_delim + " "; //"&_";
		}
		
		autoTextPages[autoTextPageCount] = AT_temp;
	
		autoTextPageCount++;
		
		AT_temp = "";
		atTempCount = 0;
	}
	
	// Helper Function.  
	// Clears all the globals used by the AutoText() function. 
	static void ClearATBuffer() {
		int i;
		
		for( i=0; i<MAX_AUTOTEXT_PAGES; i++ ) {
			autoTextPages[i] = "";
		}
		
		AT_temp = "";
		autoTextPageCount = 0;
		atTempCount = 0;
	}
	
	// Helper function, logs autotext stuff.
	//
	void DiagnoseAT() {
		
		log( "=============" );
		log( "DiagnoseAT()" );
		log( "=============" );
		log( "autoTextPageCount: " + str(autoTextPageCount) );
		
		int i;
		for( i=0; i<autoTextPageCount; i++ ) {
			log( str(i) + ": '"+ autoTextPages[i] +"'" );
		}
	
		log( "=============" );	
	}
	
	///////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////
	
	
	// The main textbox.  Takes a speech portrait index, and three Strings and 
	// prints them out,
	public static void TextBox(int sp, String s1, String s2, String s3)
	{
		TextBoxDirect(sp, 0, s1 + "&" + s2 + "&" + s3);
	}
	
	// Like Textbox, but makes a little arrow bob up and down in the corner to 
	// denote that there will be more text to come.
	//
	public static void TextBoxM(int sp, String s1, String s2, String s3)
	{
		TextBoxDirect(sp, 1, s1 + "&" + s2 + "&" + s3);
	}
	
	// The encapsulated textbox-drawing process. Called by TextBox(), TextBoxM(), 
	// and PromptDirect(), and AutoText()
	static void TextBoxDirect(int sp, int m, String s)
	{
		int line_it, line_num, time_last;
		
	
		int font_h = fontheight(v1rpg_LargeFont) + TEXTBOX_Y_BUFF;
		
	
		if (global_noscroll)
		{
			line_num = TEXTBOX_LINES-1;
			time_last = 0;
		}
		else
		{
			line_num = 0;
			time_last = systemtime;
		}
		
		while (line_num < TEXTBOX_LINES)
		{
			MenuBackGroundDraw(); // Draw universal things
			
			//draw the textbox bg.
			V1_Box( TEXTBOX_BORDER_X1,TEXTBOX_BORDER_Y1,  TEXTBOX_BORDER_WIDTH,TEXTBOX_BORDER_HIGH );
			
			setclip( 	TEXTBOX_TEXTAREA_X,TEXTBOX_TEXTAREA_Y, 
						TEXTBOX_TEXTAREA_X+TEXTBOX_TEXTAREA_WIDTH,TEXTBOX_TEXTAREA_Y+TEXTBOX_TEXTAREA_HIGH, screen );
						
			for (line_it = 0; line_it < line_num; line_it++)
			{ 
				printstring(TEXTBOX_TEXTAREA_X,TEXTBOX_TEXTAREA_Y + (font_h * line_it), screen, v1rpg_LargeFont, gettoken(s, "&", line_it)); 
			}
					
			printstring(TEXTBOX_TEXTAREA_X,TEXTBOX_TEXTAREA_Y + (font_h * line_num), screen, v1rpg_LargeFont, left(gettoken(s, "&", line_num), (systemtime - time_last) / 2));
			
			setclip(0, 0, imagewidth(screen), imageheight(screen), screen);
			
			if (MenuConfirm()) { line_num++; time_last = systemtime; }
			else if ((systemtime - time_last) / 2 >= len(gettoken(s, "&", line_num)))
			{
				if (line_num < (TEXTBOX_LINES-1)) { line_num++; time_last = systemtime; }
				else if (m == 1) tblit(TEXTMORE_X,TEXTMORE_Y + (cos(systemtime * 2) * 4 / 65536), V1_RPG.textmore, screen);
				else if (m == 2 && global_noscroll) line_num++;
			}
			
			DrawSpeechPortrait( sp );
			
			showpage();	
		}
		if (m == 1)
		{
			time_last = systemtime;
			while ((systemtime - time_last) < 30)
			{
				MenuBackGroundDraw(); // Draw universal things
	
				DrawSpeechPortrait( sp );
				
				//draw the textbox bg.
				V1_Box( TEXTBOX_BORDER_X1,TEXTBOX_BORDER_Y1,  TEXTBOX_BORDER_WIDTH,TEXTBOX_BORDER_HIGH );
				
				//setclip( 	TEXTBOX_TEXTAREA_X,TEXTBOX_TEXTAREA_Y, 
					//		TEXTBOX_TEXTAREA_X+TEXTBOX_TEXTAREA_WIDTH,TEXTBOX_TEXTAREA_Y+TEXTBOX_TEXTAREA_HIGH, screen );
				
				for (line_it = 0; line_it < line_num; line_it++)
				{ 
					if(TEXTBOX_TEXTAREA_Y + (font_h * line_it) - ((systemtime - time_last) * 2) > TEXTBOX_TEXTAREA_Y) // rbp (Easier to do Setclip things)
					printstring(TEXTBOX_TEXTAREA_X,TEXTBOX_TEXTAREA_Y + (font_h * line_it) - ((systemtime - time_last) * 2), screen, v1rpg_LargeFont, gettoken(s, "&", line_it)); 
				}
				
				//setclip(0,0, imagewidth(screen), imageheight(screen), screen);
				
				DrawSpeechPortrait( sp );
				
				showpage();
			}
		}
	}
	
	
	// Create a promptbox.  Takes a speech portrat and three lines of text and 
	// displays a textbox of them.
	//
	// Then takes a |-delineated list and creates a promptbox.  
	// Returns the token index of the choice selected.  
	// Doesn't allow cancelling at this time.
	//
	// Example:
	// if choices is "Yes|No|Maybe" makes a promptbox with "Yes", "No", and "Maybe"
	// as choosable choices.  If the player chooses "Yes", it returns 0, if the 
	// player chooses "No" it returns 1, and if the player chooses "Maybe" it 
	// returns 2.
	public static int Prompt(int sp, String s1, String s2, String s3, String choices)
	{
		return PromptDirect(sp, s1 + "&" + s2 + "&" + s3, choices);
	}
	
	
	static // The encapsulated prompbox-drawing process.  Called by Prompt() and PromptWrap()
	int PromptDirect(int sp, String question, String choices)
	{
		int i;
		int font_h = fontheight(v1rpg_LargeFont) + PROMPT_Y_BUFF;
		int tex_font_h = fontheight(v1rpg_LargeFont) + TEXTBOX_Y_BUFF;
		int prompt_wid, prompt_high;
		int prompt_x1, prompt_x2;
		
		int ptr_w = textwidth(v1rpg_LargeFont, "> ");
		
		int count = tokencount(choices, "&");
		if (count > MAX_PROMPT_OPTIONS) error("Moar than "+str(MAX_PROMPT_OPTIONS)+" options passed to the choicebox. This may cause graphical oddness");
	//exit( "longest: " +str(_LongestWidth(choices))  );
		int prompt_y1;
		
	
		if( PROMPT_BOTM_Y !=0) {
	
			
			prompt_y1 = PROMPT_BOTM_Y - (font_h * count) - (PROMPT_PADDING*2);
			
			prompt_high = PROMPT_BOTM_Y - prompt_y1;
			
		} else {
			prompt_y1 = PROMPT_Y;
			
			prompt_high = (PROMPT_PADDING*2)+TEXTBOX_Y_BUFF+(count * font_h);
		}
		
		if( PROMPT_WIDTH !=0) {
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
			
		menu_item = 0;
		
		TextBoxDirect(sp, 2, question);
	
		while(!MenuConfirm())
		{
			MenuBackGroundDraw(); // Draw universal things
	
			//draw the textbox bg.
			V1_Box( TEXTBOX_BORDER_X1,TEXTBOX_BORDER_Y1,  TEXTBOX_BORDER_WIDTH,TEXTBOX_BORDER_HIGH );
			
			//set clipping so we cannot draw outside the textbox
			setclip( 	TEXTBOX_TEXTAREA_X,TEXTBOX_TEXTAREA_Y, 
						TEXTBOX_TEXTAREA_X+TEXTBOX_TEXTAREA_WIDTH,TEXTBOX_TEXTAREA_Y+TEXTBOX_TEXTAREA_HIGH, screen );
			//print out the textbox lines.
			for (i = 0; i < TEXTBOX_LINES; i++)
			{
				printstring(TEXTBOX_TEXTAREA_X,TEXTBOX_TEXTAREA_Y + (tex_font_h * i), screen, v1rpg_LargeFont, gettoken(question, "&", i)); 
			}
	
			//restore clipping
			setclip(0,0, imagewidth(screen), imageheight(screen), screen);
	
			menu_item = MenuControlArrows(menu_item, count);
	
			V1_Box( prompt_x1, prompt_y1-TEXTBOX_Y_BUFF, prompt_wid, prompt_high );
	
			//set the clipping rectangle so we cannot draw outside the promptbox's area!
			setclip( 	prompt_x1+PROMPT_PADDING, prompt_y1+PROMPT_PADDING, 
						prompt_x2-PROMPT_PADDING,prompt_y1+prompt_high-PROMPT_PADDING, screen );
	
			//print out the options.
			for(i = 0; i <= count; i++)	
			{
				printstring(prompt_x1+PROMPT_PADDING+ptr_w+TEXTBOX_Y_BUFF, prompt_y1+PROMPT_PADDING+((i) * font_h), screen, v1rpg_LargeFont, gettoken(choices, "&", i));
			}
			
			//print the pointer...
			printstring(prompt_x1+PROMPT_PADDING+TEXTBOX_Y_BUFF, prompt_y1+PROMPT_PADDING+((menu_item) * font_h), screen, v1rpg_LargeFont, ">");
			
			//restore the clipping rectangle.
			setclip(0,0, imagewidth(screen), imageheight(screen), screen);
			
			
			DrawSpeechPortrait( sp );
			
			showpage();
		}
		
		return menu_item;
	}
	
	// Helper function.
	// determines which choice in a prompt box is the longest.
	public static int _LongestWidth( String choices ) {
		int i, longest = 0, temp;
		
		for( i=0; i<tokencount(choices,"&"); i++ ) {
			temp = textwidth(v1rpg_LargeFont, gettoken(choices, "&", i) );
			
			if( temp > longest ) longest = temp;
		}
		
		return longest;
	}
	
	
	// Draws the Speech Portrait.
	//
	static void DrawSpeechPortrait( int speech_idx )
	{
		Animation.TBlitFrame(SPEECH_X, SPEECH_Y, speechprt, speech_idx, screen);
	}
	
}