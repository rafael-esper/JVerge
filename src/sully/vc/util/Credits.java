package sully.vc.util;

import static core.Script.*;
import static core.VergeEngine.*;
import domain.VFont;

public class Credits {
// <pre>
//Just a cheesy little credit-scrolling lib.
//
// For a much awesomer Credit solution, check out:
// <a href=http://www.verge-rpg.com/files/detail.php?id=414>Buckermann's Credits library</a>


static int _creditCount;

static int _creditOffs;

public static final int MAX_CREDIT_LINES = 100;

static int _arCred[] = new int[MAX_CREDIT_LINES];
static String _arCredStr[] = new String[MAX_CREDIT_LINES];

// adds a line of text to this credit sequence.
//
public void addIntroLine( int offs, String text )
{
	if( _creditCount >= MAX_CREDIT_LINES )
	{
		System.err.println( "credits.vc::addIntroLine(), You attempted to add too many lines to a credit sequence.  Try increasing MAX_CREDIT_LINES" );
		return;
	}
	
	_arCredStr[_creditCount] = text;	
	_arCred[_creditCount] = offs;
	
	_creditCount++;
	
}

// renders the credit sequence.
//
public static void doSimpleCredits( VFont fnt )
{
	int i;
	
	if(_creditCount==0) return;
	if(_creditCount>=MAX_CREDIT_LINES)
	
	timer = 0;
	while( timer < _arCred[(_creditCount-1)]*4 )
	{
		render();
		
		for( i=0; i<_creditCount; i++ )
		{
			printcenter(imagewidth(screen)/2, imageheight(screen)+_arCred[i]-(timer/4), screen, fnt, _arCredStr[i]);		
		}
		
		showpage();
	}
}


// Clears the simple credits library for a new credit sequence.
//
public void resetCredits()
{
	_creditCount =0;
	_creditOffs	=0;
}

// </pre>
}