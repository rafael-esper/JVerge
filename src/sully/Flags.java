package sully;

// To make our gameplay flags more readable
// we'll define them!
//
// the flags[] array (defined right below on line ##) is a vital part of your
// gamemaking experience.  It basically is a universal stack of data that gets 
// loaded and saved in savegames... if you do anything in your game that you 
// want to remember, you make a flag for it.
//
// It's an array, so if you don't know what that is, go look it up in the docs 
// or the tutorial.
//
// We public static final int human-readable names to the array indexes so your code is a lot 
// more readable.  Seeing:
//
// if( flags[F_CARROT_QUEST] == 1 )
//
// makes a lot more sense than:
//
// if( flags[95] == 1 )
//
// as a space-saving effort, we'll prepend flag-defines
// with F_ instead of FLAG_
//
// you can do your flag-defines however you want.  Or use the raw indexes.
// or you don't need to use flags at all!  We aren't gonna force ya... but they 
// sure are handy! ;)
//
// Treasure-flags will be special, starting with CHEST_ and 
// being in a group of flags[] indexes past everything else, 
// just in case we want to do tricky/leet stuff with them 
// at a later time.
//
// -Grue, (2004.09.07 - 2004.10.01)

public class Flags {
	
	public static final int MAX_FLAGS	=		1100;
	
	public static int flags[] = new int[MAX_FLAGS]; //the actual flags array!
	
	// Only do this on Newgame... or risk destroying all everythings!
	//
	public static void ClearFlags()
	{
		int i;
		for(i=0; i<MAX_FLAGS; i++)
		{
			flags[i] = 0;
		}
	}
	
	
	//
	// Flag definitions below
	
	public static final int F_SULLY_INTRO		=0;   //state of the Sully introductory speech!
	
	public static final int F_CRYS_JOIN			=1;   //CRYSTAL joined you at ISLAND.MAP
	
	public static final int F_SEA_BERT			=2;   //counter for bert's speech.
	public static final int F_SEA_WARP			=3;   //state of the undersea multi-destination warper
	public static final int F_SEA_LEVER_1		=4;   //state of undersea lever 1
	public static final int F_SEA_LEVER_2		=5;   //state of undersea lever 2
	public static final int F_SEA_LEVER_3		=6;   //state of undersea lever 3
	public static final int F_SEA_LEVER_CNT		=7;   //counter of levers flipped
	public static final int F_SEA_IN_CYCLOPS_HALL	=8;   //toggles the rstring for the seweed layer
	
	public static final int F_COT_INTRO			=9;   //Cottage 'enterspeak' event occured
	public static final int F_COT_STAN			=10;   //Cottage ENCOUNTER occured. party loses crystal
	
	public static final int F_MOUNT_DEX_JOIN	=11;	//DEXTER joins you at MOUNTAIN.MAP
	public static final int F_MNT_GREEDY_SAGE	=12;	//The Sage demands 20,000 or Ponytail-babeage (Crystal)
	public static final int F_MNT_SAGE_DONE		=13;	//The Sage has spilled the carrot-beans!
	public static final int F_MNT_EVIL_SIGN		=14;	//The construction sign, trigger this if we've killed it.
	
	public static final int F_BUM_NIGHT			=15;	// If this is 1, it's nighttime in bumsville! :o
	public static final int F_BUM_INN			=16;	// The response to the bumsville inn question (0=yes, 1=no)
	public static final int F_BUM_ZEALOT_1		=17;  // Zealot 1 has cycling dialog!
	public static final int F_BUM_FLASHBACK		=18;	// this flag is triggered if the long-arse optional flashback in Bumsville has happened.
	public static final int F_BUM_JUKEBOX		=19;	// the bumsville jukebox flag
	public static final int F_BUM_JUKE_VOLUME	=20;	// the bumsville jukebox volume flag
	
	public static final int F_FASHION_PURCHASE	=21;	//Have you bought the Little Daddy Bruce treatment yet?
	public static final int F_FASHION_HAIR		=22;	//Have you chosen a hairstyle yet? =1 is Red Punk, =2 is Black Slick
	public static final int F_FASHION_CAPE		=23;	//Have you chosen a cape color yet?
	public static final int F_FASHION_CLOTHES	=24;	//Have you chosen a new tunic yet? =1 is Toga, =2 is Purple Tunic
	public static final int F_FASHION_MADEOVER	=25;	//While this is set, you're a different you!
	
	public static final int F_LAB_START			=26;	//the flag that starts our world of hurt.  Sets all the starting positions for the levers.
	
	public static final int F_LAB_SWITCH_A		=27; //the state of all ten of the lab's puzzle switches!
	public static final int F_LAB_SWITCH_B		=28;
	public static final int F_LAB_SWITCH_C		=29;
	public static final int F_LAB_SWITCH_D		=30;
	public static final int F_LAB_SWITCH_E		=31;
	public static final int F_LAB_SWITCH_F		=32;
	public static final int F_LAB_SWITCH_G		=33;
	public static final int F_LAB_SWITCH_H		=34;
	public static final int F_LAB_SWITCH_I		=35;
	public static final int F_LAB_SWITCH_J		=36;
	public static final int F_LAB_SWITCH_K		=37;
	
	public static final int F_LAB_USED_MACHINE	=38; // Have we used the Thermal Activator to save Crystal?
	public static final int F_LAB_FOUND_CRYSTAL	=39; // Have we found Crystal imprisioned in her crystal?
	public static final int F_LAB_SAVE_CRYSTAL	=40; // Have we talked to the saved crystal yet (and accidentally started the horrible 'we're gonna die' sequence?)
	
	public static final int F_LAB_LEVERBANK_1	=41; //these flags control the escape levers in the Lab Escape Sequence
	public static final int F_LAB_LEVERBANK_2	=42;
	public static final int F_LAB_LEVERBANK_3	=43;
	
	public static final int F_LAB_4000			=44;	//THIS FLAG SEEMS TO NEVER BE SET!!!
	
	public static final int F_LAB_COUNTDOWN		=45;  //Is the countdown to DOOM on?
	public static final int F_LAB_BLOWN_UP		=46;  //Is the countdown to DOOM on?
	
	
	public static final int F_RAVEN_GULCH		=47;	//we've crossed Raven Gulch!  Which means it's forever too late to do the bumsville flashback.  SUCKER!
	
	
	public static final int F_RODNE_FLASHBACK	=48; // 1 if we're in flashback mode, 0 if not!
	public static final int F_RODNE_FLASH_OVER	=49; // 1 if we're all done with flashing back FOREVER.	
	public static final int F_RODNE_CLEAN_DUST	=50; // Have we allowed passage to the basement?
	public static final int F_RODNE_TALKRAT		=51; // Have we talked to the rat?
	
	public static final int F_FLASH_INTRO		=52; //the rodne-flashback's opening speech!
	public static final int F_FLASH_WATER		=53; //water in the flashback-quest.  1= has it, 2= used it.
	public static final int F_FLASH_GEAR		=54; //gear in the flashback-quest.   1= has it, 2= used it.
	public static final int F_FLASH_TWIG		=55; //twig in the flashback-quest.   1= has it, 2= used it.
	public static final int F_FLASH_TOEJAM		=56; //toejam in the flashback-quest. 1= has it, 2= used it.
	public static final int F_FLASH_SOLAR		=57; //gear in the flashback-quest.   1= has it, and this flag ends the flashback when used on the machine.
	public static final int F_FLASH_GALFREY		=58; //just a conversational flag.
	
	public static final int F_LOVE_INTRO		=59;	//have we heard funky music yet?!
	public static final int F_LOVE_PITFALL		=60;	//marks when Darin and Dexter fall into the Dungeon of Love.
	public static final int F_LOVE_SARA_JOIN	=61;	//when Sara joins up in the Loveshack dungeon!
	public static final int F_LOVE_GATE_OPEN	=62;	//have we opened the gate yet?
	public static final int F_LOVE_LEVER		=63;  //the state of the dungeonlever.
	public static final int F_LOVE_SWITCH_A		=64;  //dungeon switch a's state
	public static final int F_LOVE_SWITCH_B		=65;  //dungeon switch b's state
	public static final int F_LOVE_SWITCH_C		=66;  //dungeon switch c's state
	public static final int F_LOVE_ESCAPE		=67;  //We've escaped the dungeon!!!
	public static final int F_LOVE_DEX_GONE		=68;	//Dexter's left us!
	public static final int F_LOVE_REMOVE_ROCK	=69;	//If Darin and Sara have destroyed the rock.
	public static final int F_LOVE_FUNKSWITCH	=70;	//THE SWITCH OF ULTIMATE FUNK.
	
	public static final int F_HECK_GALFREY_JOIN	=71;	//when Galfrey JOINS THE GOODGUYS.  Also, the bridge is done.
	public static final int F_HECK_INTROPAN		=72;	//if we've done the intro-to-heck sequence
	public static final int F_HECK_OPEN_GATE	=73;	//if we've used the PoT in the statue and opened the gate
	public static final int F_HECK_WALL_CLOSE	=74;	//if we're sealed into castle heck!
	public static final int F_HECK_B1_TORCH		=75;	//Has the B1 torch been lit (allowing access to the West Tower Key)?
	public static final int F_HECK_WEST_DOOR	=76;	//Has the west door been opened?
	public static final int F_HECK_WEST_GATE	=77;	//Have you opened the west-tower gate?
	public static final int F_HECK_DEXTER_JOIN	=78;	//Have you gotten dexter back yet?
	public static final int F_HECK_EAST_DOOR	=79;	//Has the east door been opened?
	public static final int F_HECK_TORCH_A		=80;	//the six torches for the east tower's puzzle
	public static final int F_HECK_TORCH_B		=81;
	public static final int F_HECK_TORCH_C		=82;
	public static final int F_HECK_TORCH_D		=83;
	public static final int F_HECK_TORCH_E		=84;
	public static final int F_HECK_TORCH_F		=85;
	public static final int F_HECK_SPIRE_DOOR	=86;  //Has the spire door been opened?
	public static final int F_HECK_STAN_TAUNT	=87;	//Has stan TAUNTED us yet?
	public static final int F_HECK_WALLCRUSH	=88;
	public static final int F_HECK_ARMOR_ATTACK =89; //When Galfrey leaves to hold off some scarey metal statue.
	public static final int F_HECK_PINNACLE		=90;	//have we SEALED darins FATE by locking him in with STAN?  Tune in next week, same Clam-time, same Clam-Channel!
	public static final int F_HECK_FINAL_SPEECH	=91;
	public static final int F_HECK_FINAL_LEVER	=92;
	
	public static final int F_SULLY_OPEN_PEARL	=93; //when Sully reveals the path to the Pearl of Truth
	public static final int F_PROCURED_PEARL	=94;	//if we've got the pearl of AWESOME.
	
	public static final int F_CARROT_QUEST		=95;	// =1 after using the Pearl to get the CoUP, =2 after procuring the carrot blade.
	
	public static final int F_SANCHO			=96;	// Flags for our cute little depressive octopus.
	public static final int F_SANCHO_EXPLAIN	=97;	// if Sancho has explained about the tagboard yet.
	public static final int F_SANCHO_MENU		=98;	// A flag to see if you get to shortcut to any part of the sancho-tree!
	public static final int F_LAB_SANCHO		=99;	//have we talked to Sancho in the lab yet?
	public static final int F_LAB_SULLY			=100;	//have we triggered the clam-award in the lab?
	public static final int F_LAB_SWITCHCOUNT	=101;
	public static final int F_GSWITCH			=102; //how many times have *you* flipped the Golden Switch? ;)
	public static final int F_VEGGIESMITH		=103; //just a flag for some extra sass.
	public static final int F_COT_COMPUTER		=104;	//Is the computer in the cottage active?
	
	
	//START OF CHEST INDEXES
	public static final int CHEST_UNDERSEA_A	=1000;   //Chest A undersea.map
	public static final int CHEST_UNDERSEA_B	=1001;   //Chest B undersea.map
	public static final int CHEST_UNDERSEA_C	=1002;   //Chest C undersea.map
	public static final int CHEST_COTTAGE_A		=1003;   //Chest A cottage.map
	public static final int CHEST_COTTAGE_B		=1004;   //Chest B cottage.map
	public static final int CHEST_COTTAGE_C		=1005;   //Chest C cottage.map
	public static final int CHEST_COTTAGE_D		=1006;   //Chest D cottage.map
	public static final int CHEST_COTTAGE_E		=1007;   //Chest E cottage.map
	public static final int CHEST_COTTAGE_F		=1008;   //Chest F cottage.map
	public static final int CHEST_JUJUBE_A		=1009;   //Chest A lab.map
	public static final int CHEST_JUJUBE_B		=1010;   //Chest B lab.map
	public static final int CHEST_JUJUBE_C		=1011;   //Chest C lab.map
	public static final int CHEST_JUJUBE_D		=1012;   //Chest D lab.map
	public static final int CHEST_LAB_A			=1013;   //Chest A lab.map
	public static final int CHEST_LAB_B			=1014;   //Chest B lab.map
	public static final int CHEST_LAB_C			=1015;   //Chest C lab.map
	public static final int CHEST_LAB_D			=1016;   //Chest D lab.map
	public static final int CHEST_RODNE_A		=1017;   //Chest A village.map
	public static final int CHEST_RODNE_B		=1018;   //Chest B village.map
	public static final int CHEST_RODNE_C		=1019;   //Chest C village.map
	public static final int CHEST_RODNE_D		=1020;   //Chest D village.map
	public static final int CHEST_RODNE_E		=1021;   //Chest E village.map
	public static final int CHEST_LOVE_A		=1022;   //Chest A dungeon.map
	public static final int CHEST_LOVE_B		=1023;   //Chest B dungeon.map
	public static final int CHEST_LOVE_C		=1024;   //Chest C dungeon.map
	public static final int CHEST_LOVE_D		=1025;   //Chest D dungeon.map
	public static final int CHEST_HECK_B1A		=1026;   //Chest A in B1 of castle.map
	public static final int CHEST_HECK_B1B		=1027;   //Chest B in B1 of castle.map
	public static final int CHEST_HECK_B1C		=1028;   //Chest C in B1 of castle.map
	public static final int CHEST_HECK_B1D		=1029;   //Chest D in B1 of castle.map
	public static final int CHEST_HECK_B1E		=1030;   //Chest E in B1 of castle.map
	public static final int CHEST_HECK_WEST		=1031;   //West Tower Chest of Castle.map
	public static final int CHEST_HECK_EAST		=1032;   //East Tower Chest of Castle.map
}