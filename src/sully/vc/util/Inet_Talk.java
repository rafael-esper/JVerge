package sully.vc.util;

import static core.Script.*;
import static sully.Flags.*;
import static sully.Sully.*;

import java.awt.Color;

import domain.VImage;

import static sully.vc.simpletype_rpg.Party.*;
import static sully.vc.v1_rpg.V1_RPG.*;
import static sully.vc.v1_rpg.V1_Maineffects.*;
import static sully.vc.v1_rpg.V1_Weather.*;
import static sully.vc.v1_rpg.V1_Textbox.*;
import static sully.vc.v1_rpg.V1_Music.*;
import static sully.vc.v1_menu.Menu_System.*;
import static sully.vc.v1_menu.Menu_Choice.*;
import static sully.vc.Sfx.*;
import static sully.vc.v1_rpg.V1_Simpletype.*;

public class Inet_Talk {
	// returns 1 if a conversation happened, 0 if not.
	//
	int DoInternetConversation( String url )
	{
		String src = geturltext( url );	
		int linecount = tokencount( src, chr(10) );
		String s1,s2,s3;
		int prt, i = 0;
		boolean first;
		
		first = true;
		
		while( (i+4) <= linecount )
		{
			prt = NameToPort( gettoken(src,chr(10),i) );
			
			i++;
			s1 = trim( gettoken(src,chr(10),i) );
			i++;
			s2 = trim( gettoken(src,chr(10),i) );
			i++;
			s3 = trim( gettoken(src,chr(10),i) );
			i++;
			
			if( prt < 0 ) 
			{
				if( first )
				{
					return 0;				
				}
				
				return 1;
			}
			
			TextBox( prt, s1, s2, s3 );
			
			first = false; //if we got here, it's no longer the first time
		}
		
		if( first ) //if this is still true, no conversation happened!
		{
			return 0;
		}
		
		return 1;
	}
	
	// returns the speech index for a given name
	// returns -1 on a bad name.
	int NameToPort( String name )
	{
		if( name.equals("sully") )		return T_SULLY;
		if( name.equals("darin") )		return T_DARIN;
		if( name.equals("stan") )		return T_STAN;
		if( name.equals("crystal") )	return T_CRYSTAL;
		if( name.equals("dexter") )	return T_DEXTER;
		if( name.equals("galfrey") )	return T_GALFREY;
		if( name.equals("sara") )		return T_SARA;
		if( name.equals("slasher") )	return T_SLASHER;
		if( name.equals("bert") )		return T_MANTA;
		if( name.equals("bunny") )		return T_BUNNY;
		if( name.equals("bubba") )		return T_BUBBA;
		if( name.equals("sancho") )	return T_SANCHO;
		
		return 0-1;
	}
	
	
	
	/////////////////////////////////
	//
	// These two functions are for the sully tagboard functionality.
	//
	//
	////////////////////////////
	
	public static String GetTag( String s )
	{
		String src = geturltext( "http://www.verge-rpg.com/sully/tagboard.php?s="+s );
		String a_txt = null;
		int i;
	
		String img_url 	= gettoken(src, chr(10), 0);
		String name 	= gettoken(src, chr(10), 1);
		
		for( i=2; i<tokencount(src,chr(10)); i++ )
		{
			a_txt = a_txt + gettoken(src, chr(10), i) + "~";
		}
			
			
		if( src.equals("No messages!") )
		{
			AutoText(T_SANCHO,"There are no more new messages you have not read this session.  You have presently read " +str(tokencount(s, ","))+" tagboard entries.|Resetting the session now." );
			return "";
		}
	
	//log( "'"+src+"'" );
	
		VImage img = geturlimage( img_url );
	
		tblit( SPEECH_X, SPEECH_Y-68, img, v1_vclayer );
		MakeBox(SPEECH_X+2+imagewidth(img), TEXTBOX_BORDER_Y1-fontheight(textBox_font)-13, textwidth(textBox_font, name)+12, fontheight(textBox_font)+12, true, v1_vclayer);
		printstring( SPEECH_X+2+imagewidth(img)+6, TEXTBOX_BORDER_Y1-fontheight(textBox_font)-6, v1_vclayer, textBox_font, name );	
	
		//FreeImage(img);
		
		AutoText(0, a_txt);
		
	
		ClearVCLayer();
		
		return name;
	}
	
	public static void SendMessage( String user, String pass, String msg )
	{
		String src = geturltext( "http://www.verge-rpg.com/sully/tagboard.php?_user="+user+"&_pass="+pass+"&msg="+msg );
		AutoText(T_SANCHO,src);
	}
}