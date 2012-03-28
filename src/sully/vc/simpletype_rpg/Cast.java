package sully.vc.simpletype_rpg;

import static core.Script.*;
import static sully.vc.simpletype_rpg.Data.*;
import static sully.vc.simpletype_rpg.Party.*;
import static sully.vc.simpletype_rpg.Skill.*;

// Cast.vc, functions that deal with the PCs.
//
// by McGrue
// Started 2004.08.30

public class Cast {
	// The caps placed on HP and MP 
	// 
	public static final int MAXIMUM_HP_VALUE	= 999;
	public static final int MAXIMUM_MP_VALUE	= 999;
	
	
	///////////////////////////////////////////////////////////////////////////////
	// Cast
	///////////////////////////////////////////////////////////////////////////////
	//
	// this holds the data for specific cast members and their specifics.
	//
	// this is probably the master-array you will deal with the most, unless you're a
	// wacky systems coder.
	//
		public String	name;		//The name is a second unique identifier for this character.  
		public String	desc;		//description.
		
		public String	chrname;	//relative path and filename of this cast member's chr.
		public int		entity;		//the entity reference of this character on the current map!
							//
							//NOTE: this system only sets this to the mapentity reference when 
							//      SpawnParty() or AddPlayer() are called.  Please refer to the 
							//      Party.vc docs on their proper usage.
		
		public int portrait_idx;	//the speech portrait index for this character.
							// NOTE: these are presently set by the completely hacky
							//       initSpeechPortsInData() in the Sully system.vc file.
							//       It is advised that any future revisions include this index
							//       in the datafile.
		
		public int	class_ref;		//index reference to the master_classes[] array.
		
		public int level;			// the level of this cast member. Obvious, no?
		public int exp;			// the experience point total of this cast member.
		public int cur_hp;	// current HP and MP.
		public int cur_mp;
		
		
		public int		stats[] = new int[MAX_STATS];	//the current stats for this cast member
									//refer to STAT_MAX_HP through STAT_DEF for index defines for this array.
		
		
		public int		stat_growth[][] = new int[MAX_LEVELS][MAX_GROWABLE_STATS];	
									//the stat-growth table for this character.
									//refer to STAT_MAX_HP through STAT_REA for index defines for this array.
									
		
		public int		exp_require[] = new int[MAX_LEVELS];	
									//the XP requirements for each level for this character
	
	
		public int		equipment[] = new int[MAX_EQUIP_SLOTS];	
									//the equipment slots for this character
									//contains master_items[] references for each slot.
		
										
		int		skills[] = new int[MAX_SKILLS];	//the skills this cast member knows.
									//contains master_skills[] references for each slot.
		
	
	// Takes a cast index and an amount to increase the HP by.
	//
	// adds the specified amount to cur_hp, will not set above max_hp.
	// accepts negative values, will not set below 0.
	//
	// errors if the cast_idx was invalid.
	static void HealHP( int cast_idx, int amount ) {
		if( cast_idx <0 || cast_idx >= MAX_CAST ) {
			error( "HealHP(): "+str(cast_idx)+" is not a valid cast index." );
			return;
		}
		
		master_cast[cast_idx].cur_hp += amount;
		
		if( master_cast[cast_idx].cur_hp > master_cast[cast_idx].stats[STAT_MAX_HP] ) {
			master_cast[cast_idx].cur_hp = master_cast[cast_idx].stats[STAT_MAX_HP];
		} else if( master_cast[cast_idx].cur_hp < 0 ) {
			master_cast[cast_idx].cur_hp = 0;
		}
	}
	
	// Takes a cast index and an amount to decrease the HP by.
	//
	// adds the specified amount to cur_hp, will not set below 0.
	// accepts negative values, will not set above MAX_HP.
	//
	// errors if the cast_idx was invalid.
	static void HurtHP( int cast_idx, int amount ) {
		if( cast_idx <0 || cast_idx >= MAX_CAST ) {
			error( "HurtHP(): "+str(cast_idx)+" is not a valid cast index." );
			return;
		}
		
		HealHP( cast_idx, 0-amount );
	}
	
	
	// Takes a cast index and an amount to increase the MP by.
	//
	// adds the specified amount to cur_mp, will not set above max_mp.
	// accepts negative values, will not set below 0.
	//
	// errors if the cast_idx was invalid.
	static void HealMP( int cast_idx, int amount ) {
		if( cast_idx <0 || cast_idx >= MAX_CAST ) {
			error( "HealMP(): "+str(cast_idx)+" is not a valid cast index." );
			return;
		}
		
		master_cast[cast_idx].cur_mp += amount;
		
		if( master_cast[cast_idx].cur_mp > master_cast[cast_idx].stats[STAT_MAX_MP] ) {
			master_cast[cast_idx].cur_mp = master_cast[cast_idx].stats[STAT_MAX_MP];
		} else if( master_cast[cast_idx].cur_mp < 0 ) {
			master_cast[cast_idx].cur_mp = 0;
		}
	}
	
	// Takes a cast index and an amount to decrease the MP by.
	//
	// adds the specified amount to cur_mp, will not set below 0.
	// accepts negative values, will not set above MAX_MP.
	//
	// errors if the cast_idx was invalid.
	static void TakeMP( int cast_idx, int amount ) {
		if( cast_idx <0 || cast_idx >= MAX_CAST ) {
			error( "TakeMP(): "+str(cast_idx)+" is not a valid cast index." );
			return;
		}
		
		HealMP( cast_idx, 0-amount );
	}
	
	// gives a cast member a=n amount of XP based on their master_cast[] index
	// levels the cast member up to the appropriate level for that amount of XP.
	//
	// errors if the XP amount was less than or equal to zero, or if 
	// the master_cast[] index was bad.
	void GiveXP( String name, int xp ) {
		if (!CharInParty(name)) {
			error( "GiveXP('"+name+"'): '"+name+"' is not in the party." );
			return;
		}
		
		if( xp <= 0 ) {
			error( "GiveXP('"+str(xp)+"'): cannot give a negative or zero amount of XP." );
			return;
		}
		
		GiveXPI( party[FindPartyPos(name)], xp );
	}
	
	
	// gives a cast member a=n amount of XP based on their master_cast[] index
	// levels the cast member up to the appropriate level for that amount of XP.
	//
	// errors if the XP amount was less than or equal to zero, or if 
	// the master_cast[] index was bad.
	void GiveXPI( int cast_idx, int xp ) {
		if ( !CharInPartyI(cast_idx) ) {
			error( "GiveXPI(): cast idx '"+str(cast_idx)+"' is not in the party." );
			return;
		}
		
		if( xp <= 0 ) {
			error( "GiveXPI('"+str(xp)+"'): cannot give a negative or zero amount of XP." );
			return;
		}
		
		master_cast[cast_idx].exp += xp;
		
		//level up if neccesary!
		while( master_cast[cast_idx].level < _calcMyLevelFromMyXP(cast_idx) ) {
			_doLevelup( cast_idx );
		}
	}
	
	// Full HP/MP recovery for a single person.
	void FullHeal( int cast_idx )
	{
		HealHP( cast_idx, MAXIMUM_HP_VALUE );
		HealMP( cast_idx, MAXIMUM_MP_VALUE );
	}
	
	
	
	// Although Temporary effects and equipment can mod a stat below 0, 
	// when displaying or basing effect upon a stat, we want any negative stat
	// to count as zero (excepting HP which has an absolute minimum of 1... 
	// for hopefully obvious reasons).
	//
	// The following functions are accessors for cast-members stats.  They take 
	// master_cast indexes and do no bounds checking. 
	//
	// They are named short-form because they'll oft be used inside other functions.
	// if this hurts your paradigm, feel free to do a global search/replace personally.
	
	static int getMaxHP( int idx ) {
		if( master_cast[idx].stats[STAT_MAX_HP] < 1 ) return 1;
		return master_cast[idx].stats[STAT_MAX_HP];	
	}
	
	static int getMaxMP( int idx ) {
		if( master_cast[idx].stats[STAT_MAX_MP] < 0 ) return 0;
		return master_cast[idx].stats[STAT_MAX_MP];	
	}
	
	static int getSTR( int idx ) {
		if( master_cast[idx].stats[STAT_STR] < 0 ) return 0;
		return master_cast[idx].stats[STAT_STR];
	}
	
	static int getEND( int idx ) {
		if( master_cast[idx].stats[STAT_END] < 0 ) return 0;
		return master_cast[idx].stats[STAT_END];	
	}
	
	static int getMAG( int idx ) {
		if( master_cast[idx].stats[STAT_MAG] < 0 ) return 0;
		return master_cast[idx].stats[STAT_MAG];
	}
	
	static int getMGR( int idx ) {
		if( master_cast[idx].stats[STAT_MGR] < 0 ) return 0;
		return master_cast[idx].stats[STAT_MGR];
	}
	
	static int getHIT( int idx ) {
		if( master_cast[idx].stats[STAT_HIT] < 0 ) return 0;
		return master_cast[idx].stats[STAT_HIT];	
	}
	
	static int getDOD( int idx ) {
		if( master_cast[idx].stats[STAT_DOD] < 0 ) return 0;
		return master_cast[idx].stats[STAT_DOD];	
	}
	
	static int getMBL( int idx ) {
		if( master_cast[idx].stats[STAT_MBL] < 0 ) return 0;
		return master_cast[idx].stats[STAT_MBL];	
	}
	
	static int getFER( int idx ) {
		if( master_cast[idx].stats[STAT_FER] < 0 ) return 0;
		return master_cast[idx].stats[STAT_FER];	
	}
	
	static int getREA( int idx ) {
		if( master_cast[idx].stats[STAT_REA] < 0 ) return 0;
		return master_cast[idx].stats[STAT_REA];	
	}
	
	
	static int getATK( int idx ) {
		if( master_cast[idx].stats[STAT_ATK] < 0 ) return 0;
		return master_cast[idx].stats[STAT_ATK];	
	}
	
	
	static int getDEF( int idx ) {
		if( master_cast[idx].stats[STAT_DEF] < 0 ) return 0;
		return master_cast[idx].stats[STAT_DEF];	
	}
	
	
	// takes a master_cast[] index.
	// returns how many valid Skill Group Slots that character presently has.
	//
	public static int getMySkillGroupCount( int cast_idx )
	{
		int i;
		
		for( i=0; i<MAX_SKILLGROUPS_PER_CLASS; i++ )
		{
			if( GetMySkillGroup(cast_idx,i) < 0 )
			{
				return i;
			}
		}
		
		return i;
	}
	
	// takes a master_cast[] index and another index,
	// returns the master_skilltypes[] index for that Skill Group Slot, if the character has one.
	// returns -1 if the character has no skill group in that slot.
	//
	// no error-checking at present.  Sorry.
	public static int GetMySkillGroup( int cast_idx, int my_group_index )
	{
		int tmp = master_classes[master_cast[cast_idx].class_ref].skill_groups[my_group_index];
		
		if( !IsSkillType(tmp) ) return 0-1;
		
	
		return tmp;
	}
	
	
	// And this is for people who like to iterate over the stats array!
	
	public static int getStat( int cast_idx, int stat_idx ) 
	{
		int i=0;
		
		switch( stat_idx )
		{
			case STAT_MAX_HP:	i = getMaxHP(cast_idx);break;
			case STAT_MAX_MP:	i = getMaxMP(cast_idx);break;
			case STAT_STR:		i = getSTR(cast_idx);break;
			case STAT_END:		i = getEND(cast_idx);break;
			case STAT_MAG:		i = getMAG(cast_idx);break;
			case STAT_MGR:		i = getMGR(cast_idx);break;
			case STAT_HIT:		i = getHIT(cast_idx);break;
			case STAT_DOD:		i = getDOD(cast_idx);break;
			case STAT_MBL:		i = getMBL(cast_idx);break;
			case STAT_FER:		i = getFER(cast_idx);break;
			case STAT_REA:		i = getREA(cast_idx);break;
			case STAT_ATK:		i = getATK(cast_idx);break;
			case STAT_DEF:		i = getDEF(cast_idx);break;
		}
		
		return i;
	}
	
	// This clears all of the non-permanent parts of master_cast.
	// Really, we should probably redesign the system so the 'master' and 'variable' 
	// parts of PCs and enemies live in seperate places... thoughts for the 
	// future.
	//
	// currently clears the following fields:
	//
	// level, exp, cur_hp, cur_mp, and all stats, equipment, and skills.
	public static void ClearCast()
	{
		int i,j;
		for(i=0; i<MAX_CAST; i++)
		{
			master_cast[i].level	= 0;
			master_cast[i].exp		= 0;
			master_cast[i].cur_hp	= 0;
			master_cast[i].cur_mp	= 0;	
			
			for( j=0; j<MAX_STATS; j++ )
			{
				master_cast[i].stats[j] = 0;
			}
	
			master_cast[i].equipment = new int[MAX_EQUIP_SLOTS];
			for( j=0; j<MAX_EQUIP_SLOTS; j++ )
			{
				master_cast[i].equipment[j] = 0-1;
			}
			
			for( j=0; j<MAX_SKILLS; j++ )
			{
				master_cast[i].skills[j] = 0-1;
			}
		}
	}
	
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	// Helper fuction.  Levels up a master_cast entry once.
	//
	void _doLevelup( int cast_idx ) {
		int i;
	
		for (i = 0; i < MAX_GROWABLE_STATS; i++) { 
			master_cast[cast_idx].stats[i] += master_cast[cast_idx].stat_growth[master_cast[cast_idx].level][i]; 
		}
		
		master_cast[cast_idx].level++;
	}
	
	// Helper function.  Calculates what level a master_cast entry should be 
	//
	int _calcMyLevelFromMyXP( int cast_idx ) {
	
		int i=0, xp_total_thusfar=0;
		
		while( i < MAX_LEVELS ) {
	
			i++;
			
			xp_total_thusfar +=  master_cast[cast_idx].exp_require[i];
			
			if( xp_total_thusfar > master_cast[cast_idx].exp ) {
				return i;
			} 
		}
		
		//we've reached max. Damn!
		return MAX_LEVELS;
	}
}
