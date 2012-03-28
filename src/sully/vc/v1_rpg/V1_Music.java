package sully.vc.v1_rpg;

import static core.Script.*;
import static core.VergeEngine.*;
//
// Grue's Amazing simple music wrapper library!
public class V1_Music {

	static String curmusic; // RBP from v1_rpg.vc
	static int global_music_volume; // RBP from v1_rpg.vc
	
	//
	// Just a little wrapper so we can have our map music
	// and switch easily between it and battle/incidental/whatever
	//
	// Stores the music's filename, and plays it at the v1rpg global music volume..
	public static void V1_StartMusic( String name ) {
		curmusic = name;
		playmusic(name);
		V1_SetCurVolume( global_music_volume );
	}
	
	
	// Returns the currently playing music's name.
	// Only accurate if the currently playing music was started by V1_StartMusic()
	public static String V1_CurrentMusicName() {
		return curmusic;
	}
	
	// returns the current v1rpg global music volume..
	//
	public static int V1_GetCurVolume() {
		return global_music_volume;
	}
	
	//sets the current v1rpg global music volume and changes the currently 
	// playing music to that volume.
	public static void V1_SetCurVolume( int new_vol ) {
		global_music_volume = new_vol;
		setmusicvolume( global_music_volume );
	}
	
	// Fades the music to volume 0 over a period of delay time.
	//
	public static void V1_FadeOutMusic( int delay ) {
		int chunk, endval, startvol;
		
		startvol = global_music_volume;
		endval = 0-global_music_volume;
		endval = endval*1000;
		chunk = endval/delay; //chunk is now actually 1000*chunk
	
		timer = 0;	
		while (timer<delay)
		{
			render();
			
			global_music_volume = startvol - abs( (chunk*timer)/1000 );
	
			setmusicvolume( global_music_volume );
			showpage();
		}
		
		setmusicvolume( 0 );
	}
	
	// Fades the music to volume vol over a period of delay time.
	//
	// the v1RPG global music volume is = vol at the end
	public static void V1_FadeInMusic( int delay, int vol ) {
		int chunk, endval;
		
		endval = vol-global_music_volume;
		endval = endval*1000;
		chunk = endval/delay; //chunk is now actually 1000*chunk
	
		timer = 0;	
		while (timer<delay)
		{
			render();
			setmusicvolume( (chunk*timer)/1000 );
			showpage();
		}
		
		setmusicvolume( vol );
	}
}