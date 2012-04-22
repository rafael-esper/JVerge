package sully.vc.v1_rpg;

import static core.Script.*;
import static sully.vc.simpletype_rpg.Data.master_items;
import static sully.vc.simpletype_rpg.Inventory.GiveItem;
import static sully.vc.simpletype_rpg.Item.IsItem;
import static sully.vc.simpletype_rpg.Party.RecoverPartyHP;
import static sully.vc.simpletype_rpg.Party.RecoverPartyMP;
import static sully.vc.simpletype_rpg.Party.money;
import static sully.vc.simpletype_rpg.Party.moneyname;
import static sully.vc.util.Icons.icon_get;
import static sully.vc.v1_menu.Menu_System.MenuCanBeOn;
import static sully.vc.v1_menu.Menu_System.MenuHappyBeep;
import static sully.vc.v1_menu.Menu_System.MenuOff;
import static sully.vc.v1_menu.Menu_System.MenuOn;
import static sully.vc.v1_rpg.V1_Maineffects.*;
import static sully.vc.v1_rpg.V1_Music.V1_CurrentMusicName;
import static sully.vc.v1_rpg.V1_Music.V1_StartMusic;
import static sully.vc.v1_rpg.V1_RPG.*;
import static sully.Sully.*;

import java.awt.Color;

import sully.vc.Sfx;
import domain.VImage;

// These are functions that are hybrids between the v1-rpg library and the 
// Simpletype Systems library.
//
// Basically, the graphical-drawing-ness/message-displayery is in the v1_rpg lib, and 
//  all of the stuff actually being altered (money/items/etc) is in the simpletype lib.
//////////////////////////////////
public class V1_Simpletype {
	
	// Removes an amount of money from the party's coffers.
	// 
	// lets you remove more than you have, resulting in a negative amount.
	//  so check before you do it!
	public static void TakeMoney(int amount)
	{
		Banner( "Lost "+str(amount)+" "+moneyname+"!", 300 );
		
		money -= amount;
	}
	
	// Adds an amount of money to the party's coffers.
	//
	public static void FindMoney(int amt) {
		Banner( "Got "+str(amt)+" "+moneyname+"!", 300 );
		
		money += amt;
	}
	
	
	// Takes an itemname and a quantity and give it to the party with a pretty banner 
	// with the item name, quatity and icon
	//
	// Does all errorchecking as SimpleType's GiveItem()... cuz it calls that at the top. ;)
	public static void FindItem( String itemname, int quantity )
	{
		int idx;
		String msgstr;
		
		boolean menu_mode = MenuCanBeOn();
		MenuOff();
		
		GiveItem( itemname, quantity );
		
		idx=IsItem(itemname);
		
		msgstr = "Procured "+master_items[idx].name;
		
		if( quantity > 1 ) {
			msgstr = msgstr + " x"+str(quantity)+"!";
		} else {
			msgstr = msgstr + "!";
		}
		
		int wid, high;
		VImage icon;
		
		icon = icon_get( master_items[idx].icon );
		
		high = fontheight( v1rpg_SmallFont );
		wid = textwidth( v1rpg_SmallFont, msgstr );
		
		timer = 0;
		EntStart(); // rbp
		while( timer<300 && !b1 )
		{
			render();
		
			V1_Box( (imagewidth(screen)/2)-(wid/2)-high, ((imageheight(screen)/2)-high)-16, wid+(high*2), high*3+32 );
	
			printcenter(imagewidth(screen)/2, (imageheight(screen)/2)-16, screen, v1rpg_SmallFont, msgstr);
	
			tblit( (imagewidth(screen)/2)-8, (imageheight(screen)/2)+8, icon, screen );
			
			showpage();
		}
		EntFinish(); // rbp
		
		unpress(0);
		
		icon = null;
		
		if( menu_mode )	MenuOn();
	}
	
	
	// Special for Sully: all clean water sources heal.
	//
	public static void Heal_Well()
	{
		Sfx.SoundHealingWell(); //playsound(sfx_well, 100);
		WhiteIn(50);
		//recover HP/MP
		RecoverPartyHP();
		RecoverPartyMP();
		Banner( "Recovered full HP and MP!", 300 );
		
	
		unpress(0); //all
	}
	
	
	
	// Completely encapsulates the inn process.
	//
	// The only time this is currently showed off in Sully is in the Bumsville Inn,
	// and only when the flashback sequence is not initiated.
	public static void Inn() {
		String old_music;
		int end_song_time;
		
		MenuOff();
	
		//store the original music
		old_music = V1_CurrentMusicName();
		stopmusic();
	
		//play sleep music
		//PlayMusic( "res/music/SLEEP2.IT" );
		end_song_time = systemtime + 890;
		playmusic( "res/music/WA_INN.MP3" );
		//fadeout
		FadeOut( 250 );
		
		//wait a second for effect
		WaitOut(500, Color.BLACK);
		
		//recover HP/MP
		RecoverPartyHP();
		RecoverPartyMP();
		
		//cure any status ailments that get removed at an inn
		
		
		//play a little heal chirp
		MenuHappyBeep();
		
		//cue a dialog box saying that HP/MP recovered!
		while ( !b1 ) 
		{
			render();
			rectfill(0, 0, imagewidth(screen), imageheight(screen), Color.BLACK, screen);
			CenterMessageBox( v1rpg_SmallFont, "Recovered full HP and MP!" );
	
			if( systemtime > end_song_time ) {
				stopmusic();
			}
	
			showpage();
		}	
		
		//fadein
		FadeIn(100);
		
		stopmusic();
		
		//play the original music
		V1_StartMusic( old_music );
		
		MenuOn();
	}
	
	
	// Encapsulates the SavePoint process
	//
	//
	public static void SavePoint()
	{
		V1_RPG.can_save = true;
		Sfx.SoundSavePoint();//PlaySound(Sfx.sfx_save, 100);
		WhiteIn(50);
		Banner("Save Point", 300);
	}
}