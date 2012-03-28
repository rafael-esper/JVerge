package sully.vc.simpletype_rpg;

import static core.Script.*;
import static sully.vc.simpletype_rpg.Data.*;
import static sully.vc.simpletype_rpg.Cast.*;
import static sully.vc.v1_menu.Menu_System.*;
import static sully.vc.v1_rpg.V1_RPG.*;
import domain.VFont;
import domain.VImage;

// Party.vc
//
// Party and character-management functions
//
// Original code by vecna
// modified by McGrue
////////////////////////////////////////////
public class Party {
	
	// The maximum members in a party.  
	// You may alter this, but do not expect menus built on expectations 
	// of 5 to work properly.
	public static final int MAX_PARTY_SIZE = 5;
	
	// an array of master_cast[] indexes for the party.
	//
	//eg:	party[3] is equal to the 4th party member's master_cast[] idx.
	//		so, for example, if you wanted to access that character's name based 
	//		on their party position, you would do:
	//
	//		master_cast[ party[4] ].name;
	public static int party[] = new int[MAX_PARTY_SIZE];
	
	
	// the party's money!
	public static int money;
	
	// the name of money in this game (ie "GP", "Gold", "Gil", "Gella", "Zenny", "Meseta")
	public static String moneyname = "Clams";
	
	
	public static // The current party size.  
	// This is altered by AddPlayer, RemovePlayer, and any function that calls them.
	// please do not alter it by hand unless you Know What You Are Doing (tm).
	int curpartysize;	
	
	
	// The current party size.  
	// This is set by SpawnParty, and contains the entitys[] index of the party's leader.
	// please do not alter it by hand unless you Know What You Are Doing (tm).
	public static int playerent;
	
	
	// returns the current size of the party.
	//
	// It's just a protective sheild around curpartysize for those of you 
	// who do not trust yourself with read/write vars. ;)
	public static int PartySize() {
		return curpartysize;
	}
	
	// returns the indice of the player entity.  
	// this indice is only set by SpawnParty().  
	// If SetPlayer is called after SpawnParty(), 
	// the index will point the the party's leader, 
	// *not* the current player-controlled entity.
	//
	// It's just a protective sheild around playerent for those of you 
	// who do not trust yourself with read/write vars. ;)
	public static int GetPlayer() {
		return playerent;
	}
	
	
	// Takes a string name of a Cast member.  
	// returns master_cast idx if it's a valid cast name.  -1 if not.
	static int IsCharacter( String name ) {
		int i;
		
		for( i=0; i<MAX_CAST; i++ ) {
			if( master_cast[i].name.equalsIgnoreCase(name) ) {
				return i;
			}
		}
		
		return 0-1;
	}
	
	// adds a member of master_cast to the party by their name.
	//
	// errors if given name isn't in master_cast.
	public static void JoinParty( String name, int level ) {
		int i;
		
		i = IsCharacter( name );
		
		if( i<0 ) {
			error( "JoinParty(): '"+name+"' is not a valid cast member." );
			return;
		} else {
			JoinPartyI( i, level );
		}
	}
	
	// takes a master_cast index and a level.
	// adds the cast member to the party and levels him to the specified level
	//
	// errors on a bad index, if the character is already in the party, 
	// on a bad level, or if the party is already at max size.
	static void JoinPartyI( int cst_idx, int level ) { 
		int i, j; 
		if ( CharInPartyI(cst_idx) ) {
			error( "JoinPartyI(): "+master_cast[cst_idx].name+" is already in the party." );
			return;
		}
		
		if( cst_idx < 0 || cst_idx >= MAX_CAST ) {
			error( "JoinPartyI(): "+str(cst_idx)+" is an invalid cast index." );
			return;
		}
		
		if ( PartySize() >= MAX_PARTY_SIZE ) {
			error( "JoinPartyI(): The party is already at maximum capacity." );
			return;
		}
		
	
		// Level sanity check
		if( level <= 0 || level > MAX_LEVELS ) {
			error( "JoinPartyI(): "+str(level)+" is not a valid level." );
			return;
		}
	
		for (i = 0; i < MAX_GROWABLE_STATS; i++) { 
			master_cast[cst_idx].stats[i] += master_cast[cst_idx].stat_growth[0][i]; 
		} 
		master_cast[cst_idx].exp += master_cast[cst_idx].exp_require[0];
	
		for (j = 1; j < level; j++) { 
			for (i = 0; i < MAX_GROWABLE_STATS; i++) { 
				master_cast[cst_idx].stats[i] += master_cast[cst_idx].stat_growth[j][i]; 
			}
	
			master_cast[cst_idx].exp += master_cast[cst_idx].exp_require[j];
		}
	
		master_cast[cst_idx].level = level; 
		master_cast[cst_idx].cur_hp = master_cast[cst_idx].stats[STAT_MAX_HP]; 
		master_cast[cst_idx].cur_mp = master_cast[cst_idx].stats[STAT_MAX_MP]; 
	
		AddPlayerI( cst_idx );
	}
	
	// Takes a name of a party member.
	// returns the entity index of that party member.
	//
	// errors if the name is not a valid party member and returns -1.
	public static int GetPartyEntity( String name ) {
		int idx;
		idx = FindPartyPos(name);
		
		if(idx<0) {
			error( "GetPartyEntity('"+name+"'): '"+name+"' is not a valid character's name." );
		} else {
			return master_cast[party[idx]].entity;
		}
		
		return 0-1;
	}
	
	// takes a master_cast name 
	// adds cast member to the active party.  If the party was empty, 
	//   this member becomes leader.
	//
	// errors if the specified name isn't valid.
	// errors if the character was already in the party
	// errors if the party is full.
	public static void AddPlayer(String name) {
		int i;
		
		if( IsCharacter(name) < 0 ) {
			error( "AddPlayer('"+name+"'): '"+name+"' is not a valid character's name." );
			return;
		}
		
		for( i=0; i<MAX_CAST; i++ ) {
			if( name.equals(master_cast[i].name) ) {
				AddPlayerI(i);
				return;
			}
		}
	}
	
	// takes a master_cast index.
	// adds cast member to the active party.  If the party was empty, 
	//   this member becomes leader.
	//
	// errors if the character was already in the party
	// errors if the party is full.
	static void AddPlayerI(int i) {
	
		if (CharInPartyI(i)) {
			error( "AddPlayerI("+str(i)+"): '"+str(i)+"' is not a valid party member's cast index." );
			return;	
		}
	
		if (PartySize() >= MAX_PARTY_SIZE) {
			error( "AddPlayerI("+str(i)+"): The party is already full." );
			return;
		}

		party[PartySize()] = i;	
	
		if(PartySize() > 0) { // rbp
			int e = entityspawn(0, 0, master_cast[i].chrname);
			entitystalk(e, master_cast[party[PartySize()-1]].entity);
			master_cast[party[PartySize()]].entity = e;
		}
		curpartysize++;
		
	
		//what's this line for?  -Grue
		//entity.x[playerent] = entity.x[playerent];
	}
	
	// takes a master_cast index
	// returns 1 if the cast member is in the party, 0 if not.
	static boolean CharInPartyI(int cast_idx)
	{
		int j;
		for (j=0; j<PartySize(); j++)
			if (party[j]==cast_idx) return true;
		return false;
	}
	
	// takes a master_cast name
	// returns 1 if the cast member is in the party, 0 if not.
	public static boolean CharInParty( String name )
	{
		int j;
		for (j=0; j<PartySize(); j++) {
			if( name.equals(master_cast[party[j]].name) ) {
				return true;
			}
		}
		return false;
	}
	
	// takes a master_cast index
	// returns the party position if the character is in the party, -1 if not.
	static int FindPartyPosI(int cast_idx)
	{
		int j;
		for (j=0; j<PartySize(); j++)
			if (party[j]==cast_idx) return j;
		return 0-1;
	}
	
	// takes a master_cast name
	// returns the party position if the character is in the party, -1 if not.
	static int FindPartyPos(String s)
	{
		int j;
		for (j=0; j<PartySize(); j++) {
			if( s.equals(master_cast[party[j]].name) ) {
				return j;
			}
		}
	
		return 0-1;
	}
	
	// takes a cast member's name.
	// removes that cast member from the active party.
	//
	// errors if the character is not in the party.
	public static void RemovePlayer(String name)
	{
		if (!CharInParty(name)) 
		{
			error( "RemovePlayer('"+name+"'): '"+name+"' is not in the party." );
			return;
		}
		
		AutoOn();
		int j=0;
		int ent;
		
		j=FindPartyPos(name);
	
		ent = master_cast[party[j]].entity;
			
		for (j=FindPartyPos(name); j<PartySize()-1; j++) 
		{
			party[j]=party[j+1];
		}
		
		curpartysize--;
		AutoOff();
		
		//disappear the character!
		entity.get(ent).obstruction		= false;
		entity.get(ent).visible			= false;
		entity.get(ent).obstructable	= false;
		entity.get(ent).script		= "";
	}
	
	//turns partyfollowing on.  
	//Works best if everyone's stacked on the same tile at the time.
	public static void AutoOn()
	{
		int i;
		for (i=1; i<PartySize(); i++)
			entitystalk(master_cast[party[i]].entity, 0-1);
	}
	
	// Turns partyfollowing off.
	public static void AutoOff()
	{
		int i;
		for (i=1; i<PartySize(); i++)
			entitystalk(master_cast[party[i]].entity, master_cast[party[i-1]].entity);
	}
	
	// sets every party member's current HP to their max HP (healing them completely).
	public static void RecoverPartyHP() {
		int i;
		
		for( i=0; i<PartySize(); i++ ) {
			HealHP( party[i], MAXIMUM_HP_VALUE );
		}
	} 
	
	// sets every party member's current MP to their max MP (recovering their MP completely).
	public static void RecoverPartyMP() {
		int i;
		
		for( i=0; i<PartySize(); i++ ) {
			HealMP( party[i], MAXIMUM_MP_VALUE );		
		}
	} 
	
	
	// Spawns a party based on the contents of the party[] array.
	// Party[0] becomes the Player, and each following party slot stalks the previous.
	//
	// This function should be called in the autoexec function of any map you wish to 
	// have your party to exist in.
	//
	// The party is spawned at x,y.  This function does not change cameratracking, 
	// so the camera will not automatically jump to the party if not in mode 1.
	//
	// Errors if the current party size is 0 or less or greater than MAX_PARTY_SIZE.
	public static void SpawnParty( int x, int y ) {
		
	
		// Initialize entities
		int i, e, last=0-1;
		
		if( PartySize()==0 || PartySize() > MAX_PARTY_SIZE ) {
			error( "SpawnParty(): your party has an invalid size to span ( "+str(PartySize())+" of "+str(MAX_PARTY_SIZE)+" max)" );
			return;
		}
		
		for (i=0; i<PartySize(); i++) {
	
			e = entityspawn( x,y, master_cast[party[i]].chrname );
			entitystalk(e, last);
			master_cast[party[i]].entity = e;
			if (last<0) 
			{
				setplayer(e);
				playerent = e;
			}
	
			last = e;
		}
	}
	
	
	static VImage _MPB_panes[] = new VImage[MAX_PARTY_SIZE];
	static int _MPB_restore;

	static int _MPB_store;
	
	public static void MenuPartyBoxRestorePosition()
	{
		_MPB_restore = 1;
	}
	
	
	
	// Draws a graphical box that lets you select someone presently in the party.
	// returns their master_cast index.
	public static int MenuPartyBox()
	{
		int i, selected = 0, frame;
		int mini_hold = menu_idx;
		menu_idx = 10000; // To make a grue happy
		
		int bordersize = 5;
		VFont f = new VFont(load("res\\system\\menu_font_white.png")); //make this alterable. // TODO RBP Load a default font
			
		int x1 = textwidth(f, "_HP");
		int x2 = x1 + textwidth(f, "X");
		int x3 = x2 + textwidth(f, "000");
		int x4 = x3 + textwidth(f, "/");
		int x5 = x4 + textwidth(f, "000");
		
		int y_spacer = 1; //make this alterable.
		int y1 = y_spacer;
		int y2 = y1+ fontheight(f)+y_spacer;
		int y3 = y2+ fontheight(f)+y_spacer; 
		int y4 = y3+ fontheight(f)+y_spacer;
		
		int temp;
		
		if( _MPB_restore!=0 )
		{
			_MPB_restore = 0;
			selected = _MPB_store;
		}
	
		for( i=0; i<PartySize(); i++ )
		{
			_MPB_panes[i] = new VImage(x5,y4);
			
			rectfill(0,0, x5,y4, transcolor, _MPB_panes[i]);
			
			printstring(x1, y1, _MPB_panes[i], f, master_cast[party[i]].name);
			printstring(x3, y2, _MPB_panes[i], f, "/");
			printstring(x3, y3, _MPB_panes[i], f, "/");
			
			printright(x1, y2, _MPB_panes[i], f, "HP");
			printright(x1, y3, _MPB_panes[i], f, "MP");
			
			printright(x3, y2, _MPB_panes[i], f, str(master_cast[party[i]].cur_hp));
			printright(x5, y2, _MPB_panes[i], f, str(master_cast[party[i]].stats[STAT_MAX_HP]));
			
			printright(x3, y3, _MPB_panes[i], f, str(master_cast[party[i]].cur_mp));
			printright(x5, y3, _MPB_panes[i], f, str(master_cast[party[i]].stats[STAT_MAX_MP]));
		}
		
		int high = PartySize() * (y4+fontheight(f)) + (bordersize*2);
		int wid = ( x5+textwidth(f, "000") ) + (bordersize*2);
		int xpos = (imagewidth(screen) / 2) - (wid / 2) - bordersize;
		int ypos = (imageheight(screen) / 2) - (high / 2) - bordersize;
		
		int x_in = xpos+textwidth(f, "00");
		int y_in = ypos+fontheight(f);
		
		unpress(0);
	
		while(!MenuConfirm())
		{
			MenuBackGroundDraw(); // Draw universal things
			
			if (IsInMenu()) callfunction(master_menus[mini_hold].draw_func);
	
			V1_Box(xpos, ypos, wid, high);
	
			for(i = 0; i < PartySize(); i++)
			{
				tblit( x_in, y_in + (i*(y4+fontheight(f))), _MPB_panes[i], screen );
	
				if (i == selected) {
					printstring( x_in, y_in+(i*(y4+fontheight(f))), screen, f, "_>" );
				}
			}
			
			showpage();
			selected = MenuControlArrows(selected, PartySize());
			if (!MenuCancel()) return 0-1;
		}
		
		//free the temporary images...
		for( i=0; i<PartySize(); i++ )
		{
			_MPB_panes[i] = null; // Freeimage
		}
		
		_MPB_store = selected;
		
		menu_idx = mini_hold;
		
		unpress(0);
	
		return party[selected];
	}
}