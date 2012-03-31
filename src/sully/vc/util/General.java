package sully.vc.util;

import static core.Script.*;

// A general-use vc function library!
//
// This is the home for functions that could pretty much belong in
// the builtins, but don't for one reason or another (usually duplication)
//

public class General {
	/************************** entity.face constants ****************************/
	
	// The valid values of Entity.Face[]
	//
	// These should really be predefined.
	public static final int FACE_UP		=	1;
	public static final int FACE_DOWN	=	2;
	public static final int FACE_LEFT	=	3;
	public static final int FACE_RIGHT	=	4;
	
	
	
	// These four functions return true if the player entity is facing in the specified
	// direction
	//
	// they all assume that GetPlayer() will return the current map entity index
	// of the player... which is a good thing to assume, IMO.
	boolean FacingUp() {	if( entity.get(getplayer()).face == FACE_UP ) return true;	return false;		}
	boolean FacingDown() {	if( entity.get(getplayer()).face == FACE_DOWN ) return true;	return false;	}
	boolean FacingLeft() {	if( entity.get(getplayer()).face == FACE_LEFT ) return true;	return false;	}
	boolean FacingRight() {	if( entity.get(getplayer()).face == FACE_RIGHT ) return true;	return false;	}
	
	
	
	
	// Takes an integer
	// Returns the absolute value of that integer.
	/* Not needed
	 * int abs(int i)
	{
		 if (i>=0) return i;
		 return 0-i; // Zip: Less foolish
	}*/
	
	
	// Takes a string, returns the string with leading and trailing whitespace
	// removed.
	//String trim( string s ) {} // Not needed. Java has trim method in Strings
	
	// Takes two strings.
	// Returns true if the two strings are the same.
	//
	// Note: This function ignores case,  IE "ABC" and "abc" will return true.
	//int equ( string a, string b ) {} // Not needed. Java has equals method in Strings
	
	
	// General-use entityhandler
	//
	//
	
	
	// returns the screen-relative x-position for the specified entity.
	// note: this is the x-coordinate of the top-left corner of the entity's image,
	//       *NOT* the x-coordinate of the top-left corner of the hotspot.
	public static int GetEntScrX( int ent_idx ) {
		return entity.get(ent_idx).getx() - xwin - entity.get(ent_idx).getHotX();
	}
	
	// returns the screen-relative y-position for the specified entity.
	// note: this is the y-coordinate of the top-left corner of the entity's image,
	//       *NOT* the y-coordinate of the top-left corner of the hotspot.
	public static int GetEntScrY( int ent_idx ) {
		return entity.get(ent_idx).gety() - ywin - entity.get(ent_idx).getHotY();
	}
	
	// Takes a tile's x-coordinate
	// returns the screen-relative x-position of that tile.
	public static int GetTileScrX( int tile_x ) {
		return (tile_x*16) - xwin;
	}
	
	// Takes a tile's x-coordinate
	// returns the screen-relative x-position of that tile.
	public static int GetTileScrY( int tile_y ) {
		return (tile_y*16) - ywin;
	}
	
	
	// Waits until an entity has finished its movecode
	// -Zip
	public static void WaitForEntity( int wait_entity ){
	
		// Just incase we get stuck somewhere, set a timeout
		int timeout = timer + 1000;
	
		// While the entity is still doing stuff
		while (entity.get(wait_entity).movecode != 0)
		{
			render(); // Render the map
			showpage(); // Display to screen

			// If they've got stuck
			if (timeout < timer) {
	
				// Stop the entity
				entitystop(wait_entity);
				// Notify you so you can fix your code
				error("Timeout for entity number "+str(wait_entity)); // Zip: Muxed for sully
			}
		}
	}
	
	
	
	// takes a mapentity index, and prints out all of the current information about 
	// it to the logfile.  Useful for diagnosing problems, or just for general 
	// education.
	/*void EntityDiagnose( int idx )
	{
		log( "EntityDiagnose("+str(idx)+")" );
		log( "====================" );
		
		if( idx < 0 || idx >= entities )
		{
			log( "error: " +str(idx)+ " is not a valid mapentity index on this map.  Current entity count: " + str(entities) );
		}
			
		log( "(x,y): (" +str(entity.x[idx])+","+str(entity.y[idx])+")" );
		log( "specframe: " + str(entity.specframe[idx]) );
		log( "frame: " + str(entity.frame[idx]) );
		log( "hotspot (x,y) (w,h): (" + str(entity.hotx[idx]) +","+str(entity.hoty[idx])+") ("+str(entity.hotw[idx])+","+str(entity.hoth[idx])+")" );
		log( "movecode: " + str(entity.movecode[idx]) );
		log( "face: " + str(entity.face[idx]) );
		log( "speed: " + str(entity.speed[idx]) );
		log( "visible: " + str(entity.visible[idx]) );
		log( "obstruct: " + str(entity.obstruct[idx]) );
		log( "obstructable: " + str(entity.obstructable[idx]) );
		
		log( "script: " + entity.script[idx] );	
	}*/

}