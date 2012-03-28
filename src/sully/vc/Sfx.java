package sully.vc;

import static core.Script.*;

public class Sfx {
	
	// Three global variables for the global music, interface, 
	// and sound effect volumes.
	// Note: The music volume is only enforced by using the V1_sound.vc
	// functions, and is not directly tied to any builtin sound functions.
	public static int interface_volume;
	public static int sfx_volume;
	public static int global_music_volume;
	
	/**************************** public resources ****************************/
	
	static String sfx_open 	= ("res/sfx/open03.wav");
	static String sfx_warp 	= ("res/sfx/shadow.wav");
	static String sfx_switch	= ("res/sfx/boxopen.wav");
	static String sfx_save 	= ("res/sfx/ding.wav");
	static String sfx_magic1	= ("res/sfx/magic05.mp3");
	static String sfx_hit		= ("res/sfx/HIT01.WAV");
	static String sfx_well	= ("res/sfx/MAGIC.WAV");
	static String sfx_magic2	= ("res/sfx/MAGIC02.WAV");
	static String sfx_magic4	= ("res/sfx/MAGIC04.WAV");
	static String sfx_bling	= ("res/sfx/BUYSELL.WAV");
	static String sfx_bomb	= ("res/sfx/BOMB.WAV");
	static String sfx_crikts	= ("res/sfx/CRICKETS.WAV");
	static String sfx_shing	= ("res/sfx/SHING.WAV");
	static String sfx_quake	= ("res/sfx/QUAKE.WAV");
	static String sfx_xplode	= ("res/sfx/EXPLOSION.WAV");
	static String sfx_fanfare	= ("res/sfx/SFANFARE.mp3");
	static String sfx_splash	= ("res/sfx/WATER02.WAV");
	static String sfx_dblclck	= ("res/sfx/CLICK01.WAV");
	static String sfx_crash	= ("res/sfx/crash05.WAV");
	static String sfx_twinkle	= ("res/sfx/twinkle.WAV");
	static String sfx_grandfn	= ("res/sfx/BIGFNFR.mp3");
	static String sfx_fall	= ("res/sfx/fall02.wav");
	
	/**************************** functions ****************************/
	
	//
	// In-game sound effects.
	// all play at the global sfx_volume volume.
	
	public static void SoundChaChing() {
		playsound( sfx_bling, sfx_volume );
	}
	
	public static void SoundOpenBox() {
		playsound( sfx_open, sfx_volume );
	}
	
	public static void SoundWarpZone() {
		playsound( sfx_warp, sfx_volume );
	}
	
	public static void SoundSwitch() {
		playsound( sfx_switch, sfx_volume );
	}
	
	public static void SoundSavePoint() {
		playsound( sfx_save, sfx_volume );
	}
	
	public static void SoundMagic1() {
		playsound( sfx_magic1, sfx_volume );
	}
	
	public static void SoundMagic2() {
		playsound( sfx_magic2, sfx_volume );
	}
	
	public static void SoundHit() {
		playsound( sfx_hit, sfx_volume );
	}
	
	public static void SoundHealingWell() {
		playsound( sfx_well, sfx_volume );
	}
	
	public static void SoundBomb() {
		playsound( sfx_bomb, sfx_volume );
	}
	
	public static void SoundChirpChirp() {
		playsound( sfx_crikts, sfx_volume );
	}
	
	public static void SoundIceShing() {
		playsound( sfx_magic4, sfx_volume );
	}
	
	public static void SoundShing() {
		playsound( sfx_shing, sfx_volume );
	}
	
	public static void SoundFanfare() {
		playsound( sfx_fanfare, sfx_volume );
	}
	
	public static void SoundGrandFanfare() {
		playsound( sfx_grandfn, sfx_volume );
	}
	
	public static void SoundQuake() {
		playsound( sfx_quake, sfx_volume );
	}
	
	public static void SoundExplosion() {
		playsound( sfx_xplode, sfx_volume );
	}
	
	public static void SoundSplash() {
		playsound( sfx_splash, sfx_volume );
	}
	
	public static void SoundDoubleClick() {
		playsound( sfx_dblclck, sfx_volume );
	}
	
	public static void SoundCrash() {
		playsound( sfx_crash, sfx_volume );
	}
	
	public static void SoundTwinkle() {
		playsound( sfx_twinkle, sfx_volume );
	}
	
	public static void SoundFall() {
		playsound( sfx_fall, sfx_volume );
	}
}