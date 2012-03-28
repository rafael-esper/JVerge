package sully.vc.simpletype_rpg;

import static sully.vc.simpletype_rpg.Data.*;

///////////////////////////////////////////////////////////////////////////////
// Skill
///////////////////////////////////////////////////////////////////////////////
// this struct holds the data for specific skills
//
// IE: Fire, Cure, Aim, Slice
//
//Some simple functions.  More to come!
//

public class Skill {

	public String 	name;			//make sure this is unique.
	public String 	desc;			//description string
	public int 	icon;			//icon reference
	public int 	use_flag;		//can this skill be used from menus, battle, or both?
	public String 	target_func;	//the callfunc for the skill's targetting.
	public String 	effect_func;	//the callfunc for the skill's effect.
	public int 	mp_cost;		//the MP cost to use this skill.  Can be 0.
	public int 	type;			//index reference to the master_skilltypes array (see above)

	
	// Returns the number of skilltypes that there are presently in master_skilltypes
	//
	// this is a really shitty test ATM, btw.  
	static int SkillTypeCount()
	{
		int i;
		for(i=0; i<MAX_SKILLTYPES; i++)
		{
			if(master_skilltypes[i].name == null || master_skilltypes[i].name.isEmpty() )
				return i;
		}
		
		return i;
	}
	
	
	// Takes a skill type index and returns true if it's valid, false if it's not.
	static boolean IsSkillType( int idx )
	{
		if( idx < 0 || idx >= SkillTypeCount() ) return false;
		return true;
	}
}