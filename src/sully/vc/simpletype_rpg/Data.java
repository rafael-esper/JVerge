package sully.vc.simpletype_rpg;

import static core.Script.*;
import static sully.vc.simpletype_rpg.parser.Data_load.*;
import static sully.vc.simpletype_rpg.Cast.*;

// Data.vc, datatype definitions for the Simpletype RPG Subsystems Library
//
// by McGrue
// Started 2004.08.30
/////////////////////////////////////////////////////////////////////////
// Best viewed with tab of 4 in a text-editor with syntax-highlighted vc
/////////////////////////////////////////////////////////////////////////
// These are the master arrays of structs that the datafiles parse into.
// 
// The Load/Save system alters none of them *except* master_cast[], for
// reasons that I hope are obvious.
//
// Accessor functions might be a nice revision to add for all these member
// variables, based on index, but were left out of this verion.
//
/////////////////////////////////////////////////////////////////////////
public class Data {
	
	// The directory that general-datafiles live in.
	public static String dat_directory 		= "res/data/";
	
	// The directory that cast-datafiles live in.
	public static String cast_dat_directory 	= "res/data/Cast/";
	
	// The directory that .chr files live in.
	public static String chr_dir 				= "res/chrs/";
	
	
	// These define the maximum number of entries in specific datafiles.
	// If you alter datafiles too much, you may want to increase these values
	public static final int  MAX_SKILLTYPES	=10;
	public static final int MAX_CLASSES		=10;
	public static final int MAX_SKILLS		=100;
	public static final int MAX_CAST		=10;
	public static final int MAX_ITEMS		=100;
	
	// the maximum number of skillgroups a single class can have
	public static final int MAX_SKILLGROUPS_PER_CLASS	= 4;
	
	// maxhp   maxmp   str    end    mag   mgr    hit%   dod%   mbl   fer  rea
	//
	// The growable stats are the ones that can be altered by level-gain as defined in the
	// growth tables of a specific cast member's datafile.
	//
	// The non-growable stats are ATT and DEF.
	public static final int MAX_GROWABLE_STATS	= 11;
	public static final int MAX_STATS			= 13;
	
	// the maximum level attainable.  minimum is 1.
	public static final int MAX_LEVELS	= 99;
	
	
	//USEFLAG DEFINES - Bit positions for item/spell use
	// ie, something battle-only is 1, menu-only is 2, and both is 3.
	public static final int USE_BATTLE	= 1;
	public static final int USE_MENU	= 2;
	
	
	// left-hand, right-hand, body, acc1, acc2
	public static final int MAX_EQUIP_SLOTS = 5;
	
	//EQUIP SLOT DEFINES - indices for equipment arrays.
	public static final int SLOT_RHAND	= 0;
	public static final int SLOT_LHAND	= 1;
	public static final int SLOT_BODY	= 2;
	public static final int SLOT_ACC1	= 3;
	public static final int SLOT_ACC2	= 4;
	
	public static final int MAX_SLOTS	= 5;
	
	
	
	//STAT DEFINES - indexes for growable-stats arrays.
	public static final int STAT_MAX_HP	=0;
	public static final int STAT_MAX_MP	=1;
	public static final int STAT_STR	=2;
	public static final int STAT_END	=3;
	public static final int STAT_MAG	=4;
	public static final int STAT_MGR	=5;
	public static final int STAT_HIT	=6; 
	public static final int STAT_DOD	=7;
	public static final int STAT_MBL	=8;
	public static final int STAT_FER	=9;
	public static final int STAT_REA	=10;
	
	//additional indexes for total-stats arrays.
	public static final int STAT_ATK	=11;
	public static final int STAT_DEF	=12;
	
	///////////////////////////////////////////////////////////////////////////////
	// SkillType
	///////////////////////////////////////////////////////////////////////////////
	//
	// this struct holds the data for a family of skills
	//
	// IE: White Magic, Black Magic, Karate, Swordtech, Gambling... anything you want.
	//
	public class SkillType {
		public String 	name; //remember to make sure names are unique.
		public String 	desc; //the description string.
		public int 	icon; //the index of the icon in the iconfile. (see icons.vc)
	}
	
	//the master array of skilltypes
	public static SkillType master_skilltypes[] = new SkillType[MAX_SKILLTYPES];
	
	///////////////////////////////////////////////////////////////////////////////
	// Class
	///////////////////////////////////////////////////////////////////////////////
	//
	// this struct holds the data for a specific character class.
	//
	// IE: Knight, White Mage, Guru, Funkmaster, etc...
	//
	public class CharClass {
		public String 	name; //names must be unique
		public String 	desc; //the description string
		public int 	icon; //the index of the icon in the iconfile. (see icons.vc)
		public int 	skill_groups[] = new int [MAX_SKILLGROUPS_PER_CLASS];
	}
	
	//the master array of classes
	public static CharClass master_classes[] = new CharClass[MAX_CLASSES];
	
	//the master array of skills
	public static Skill master_skills[] = new Skill[MAX_SKILLS];
	
	//the master array of cast members.
	public static Cast master_cast[] = new Cast[MAX_CAST];	
	
	//the master array of items.
	public static Item master_items[] = new Item[MAX_ITEMS];
	static {for(int i=0; i<master_items.length; i++) master_items[i] = new Item();}
	
	// Load Zip's Awesome (Yet Scary) Dataparsers!
	//
	//#include "vc/simpletype_rpg/parser/flags.vh" 			// Zip
	//#include "vc/simpletype_rpg/parser/data_front.vc" 		// Zip
	//#include "vc/simpletype_rpg/parser/data_load.vc" 		// Zip
	
	// Load the rest of the RPG Subsystems
	//
	//#include "vc/simpletype_rpg/inventory.vc" 	// Grue
	//#include "vc/simpletype_rpg/equipment.vc" 	// Grue
	//#include "vc/simpletype_rpg/cast.vc" 		// Grue
	//#include "vc/simpletype_rpg/party.vc" 		// Grue
	//#include "vc/simpletype_rpg/item.vc" 		// Grue
	//#include "vc/simpletype_rpg/skills.vc" 		// Grue
	
	
	
	void debugItem( int idx )
	{
		int i;
	
		String classes = "";
		
		log( "" );
		log( "==============================" );
		log( "debugItem( "+str(idx)+" )" );
		log( "==============================" );
		
		if( idx < 0 || idx > MAX_ITEMS )
		{
			log( str(idx)+" is an invalid index.  master_items[] has a valid range of 0 through " + str(MAX_ITEMS) );
		}
		else
		{
			log( "       master_items["+str(idx)+"].name: " + master_items[idx].name );
			log( "       master_items["+str(idx)+"].desc: " + master_items[idx].desc );
			log( "       master_items["+str(idx)+"].icon: " + str(master_items[idx].icon) );
			log( "   master_items["+str(idx)+"].use_flag: " + str(master_items[idx].use_flag) );
			log( "master_items["+str(idx)+"].target_func: " + master_items[idx].target_func );
			log( "master_items["+str(idx)+"].effect_func: " + master_items[idx].effect_func );
			log( "   master_items["+str(idx)+"].equ_slot: " + str(master_items[idx].equ_slot) );
			log( "master_items["+str(idx)+"].equ_modcode: " + master_items[idx].equ_modcode );
			
			
			for( i=0; i<MAX_CLASSES; i++ )
			{
				if( master_items[idx].equ_classes[i] > 0 )
				{
					if(classes == null || classes.isEmpty() )
					{
						classes = str(master_items[idx].equ_classes[i]);
					}
					else
					{
						classes = classes + "," + str(master_items[idx].equ_classes[i]);
					}
				}
			}
			
			log( "    master_items["+str(idx)+"].classes: " + classes );
		
		}
		
		log( "==============================" );
	}
	
	
	
	
	void debugCast( int idx )
	{
		int i;
	
		
		log( "" );
		log( "==============================" );
		log( "debugCast( "+str(idx)+" )" );
		log( "==============================" );
		
		if( idx < 0 || idx > MAX_CAST )
		{
			log( str(idx)+" is an invalid index.  master_cast[] has a valid range of 0 through " + str(MAX_CAST) );
		}
		else
		{
				
			log( "        master_cast["+str(idx)+"].name: " + master_cast[idx].name );
			log( "        master_cast["+str(idx)+"].desc: " + master_cast[idx].desc );
			log( "     master_cast["+str(idx)+"].chrname: " + master_cast[idx].chrname );
			log( "      master_cast["+str(idx)+"].entity: " + str(master_cast[idx].entity) );
			log( "master_cast["+str(idx)+"].portrait_idx: " + str(master_cast[idx].portrait_idx) );
			log( "       master_cast["+str(idx)+"].class: " + master_classes[master_cast[idx].class_ref].name );
			log( "       master_cast["+str(idx)+"].level: " + str(master_cast[idx].level) );
			log( "         master_cast["+str(idx)+"].exp: " + str(master_cast[idx].exp) );
			log( "      master_cast["+str(idx)+"].cur_hp: " + str(master_cast[idx].cur_hp) );
			log( "      master_cast["+str(idx)+"].cur_mp: " + str(master_cast[idx].cur_mp) );
			
			log( "STATS: " );
			
			for( i=0; i<MAX_STATS; i++ )
			{
				log( GetStatName(i)+": real: "+str(master_cast[idx].stats[i])+"; getStat(): " +str(getStat(idx, i)) );
			}
			
			log( "Equipment: " );
			
			for( i=0; i<MAX_EQUIP_SLOTS; i++ )
			{
				if( master_cast[idx].equipment[i] < 0 )
				{
					log( GetSlotName(i)+": NOTHING " );
				}
				else
				{
					log( GetSlotName(i)+": "+master_items[master_cast[idx].equipment[i]].name+" (id: "+str(master_cast[idx].equipment[i])+")" );
				}
			}	
		}
		
		log( "==============================" );
	}

}