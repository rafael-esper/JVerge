package sully.vc.util;

import static core.Script.*;

// McGrue's General Use Targetting System.
//
// this library is very, very general-use.  
// To use it, use it's functions in your personal targetting functions.  
// It basically allows a global interface to pass back and forth targetting-information
// 

public class Targetting {
	// Global 
	//
	static int targetting_success;
	
	// a global variable for the callfunc name of the current targetting session.
	static String cur_targ_sess_callfunc;
	
	// Defines for targetting_success's values.
	public static final int  TARG_UNSET		= 0;
	public static final int  TARG_SUCCESS	= 1;
	public static final int  TARG_CANCEL	= 2;
	public static final int  TARG_ERROR 	= 3;
	
	
	// A very generic structure for targetting data!  
	// What you do with it is your own call.  Filfre to change all of this data
	// to your own content, but remember to change the Targetting accessors to reflect the changes,
	// and to change ClearTargetting() to unset everything inside.
	class target_data
	{
		int id;			//intended for a reference to some index somewhere.  
						// in the SimpleType RPG Library (Sully's default) this holds
						// a master_cast index.  (master_cast is an array of Cast structures,
						// defined in  "vc/simpletype_rpg/data.vc" by default.)
		
	
		int mode;		//intended for a reference to the context of your ID.  
						//for instance, in the upcoming v1_rpg library, 
						//
		
		
		String text;	//special parameters to attach to this target.  
						//* Could be "Monster" or "PC", to tell the effect function to look in 
						//  a different array than normal with the id above.
						//
						//* Could be "Direct" or "Shrapnel" to tell the effect function who a 
						//  grenade was launched at, and who was only hit by the "splash" zone.
						//
						//* these are just suggestions.  It's up to you as a targetting-function and
						//  effect-function scripter to use these tools.
	}
	
	
	// The maximum number of targets for your game.
	// Generally you want this to be at least equal to the 
	// total number of combatants (monsters and party members) 
	// involved in the biggest battle possible.
	public static final int  MAX_TARGETS = 32;
	
	// The global array of targetting data.
	// reset at the beginning of every targetting session by ClearTargetting().
	static target_data master_targetting[] = new target_data[MAX_TARGETS];
	
	// the counter for the current targetting session.
	// reset at the beginning of every targetting session by ClearTargetting().
	static int master_targetting_count;
	
	
	// The Entrypoint for a Targetting Session.  
	// It clears all previous targetting data, verifies that the user-defined targetting 
	//  function exists, and calls it!
	//
	// Errors if the function named in callfunc does not exist.  Targetting callfuncs are manditory.
	// Errors if the callfunc was called and never set targetting_success.
	public static void DoTargetting( String callfunc )
	{
		ClearTargetting();
	
		
	
		if( !functionexists(callfunc) )
		{
			error( "DoTargetting(): '"+callfunc+"()' is not a defined function. This is the fault of this game's programmer.  Please alert him of this oversight." );
			return;		
		}
		
		//set the global copy of the callfunction about to be called for this targetting session.
		cur_targ_sess_callfunc = callfunc;
	
		//call your private targetting function.
		callfunction(callfunc);
	
		//if your function never changed the targetting_success variable from the UNSET state, let's yell out an error.
		if( targetting_success == TARG_UNSET )
		{
			error( "DoTargetting(): The targetting state was never set for this targetting session.  This is the fault of whomever made the '"+cur_targ_sess_callfunc+"()' function." );
		}	
	}
	
	
	// returns 1 if the targetting callfunction is satisfied that everything's set 
	// with the targetting.
	//
	// returns 0 if the targetting failed, was cancelled.
	//
	// errors if the targetting was never set.
	public static boolean ValidTargetting()
	{
		if( targetting_success == TARG_SUCCESS ) 
		{
			return true;
		}
		else if( targetting_success == TARG_UNSET )
		{
			error( "ValidTargetting(): The targetting state was never set for this targetting session.  This is the fault of whomever made the '"+cur_targ_sess_callfunc+"()' function." );
		}
		
		return false;
	}
	
	
	// Cleans up all targetting-related variables.
	// Automatically called at the top of DoTargetting();
	public static void ClearTargetting()
	{
		targetting_success = TARG_UNSET;
		
		cur_targ_sess_callfunc = "";
		
		int i;
		//clearing the master targetting array.  
		//Remember to change this if you alter the target_data struct.
		for( i=0; i<MAX_TARGETS; i++ )
		{
			master_targetting[i].id		= 0-1;
			master_targetting[i].mode	= 0-1;
			master_targetting[i].text	= "";
		}
		
		master_targetting_count = 0;	
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// Functions for use in your private targetting function (you know, the one you're sending into DoTargeting()? )
	//
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	// A wrapper around error() (defined in "error_handler.vc" in this same directory)
	// use this function for all fatal errors in your personal targetting callfuncs.
	// It takes care of all error overhead for this system.
	//
	public static void TargettingError( String error_msg )
	{
		error( "Error in custom targetting function "+cur_targ_sess_callfunc+"(): " + error_msg );
		targetting_success = TARG_ERROR;
	}
	
	// When your targetting function adds a valid target, please do it through this function.
	//
	// If you alter the target_data struct, please alter the arguments accordingly.
	//
	// Errors if you've filled the master_targetting array to it's capacity.
	public static void AddTarget( int _id, int _mode, String _text )
	{
		if( master_targetting_count >= MAX_TARGETS )
		{
			error( "AddTarget(), Error in custom targetting function "+cur_targ_sess_callfunc+"(): Attempted to add more targets than the targetting array could hold as defined by MAX_TARGETS ("+str(MAX_TARGETS)+")."  );
			return;
		}
		
		int i = master_targetting_count;
	
		master_targetting[i].id		= _id;
		master_targetting[i].mode	= _mode;
		master_targetting[i].text	= _text;
		
		master_targetting_count++;
	}
	
	
	// When your targetting function adds a valid target, please do it through this function.
	// This function makes sure each target is unique.
	//
	// The criteria in the default build of this library is that id and mode together create a  
	// key of uniqueness.  For example, id: 0, mode: 0 is a different target than id: 0, mode: 1.
	//
	// If your game has different criteria for the uniqueness of a target, You should alter the body of 
	// this function.
	//
	// Errors if you've filled the master_targetting array to it's capacity.
	public static void AddUniqueTarget( int _id, int _mode, String _text )
	{
		int i;
		for( i=0; i<master_targetting_count; i++ )
		{
			if( master_targetting[i].id == _id ) //if _id is already in the targetting array, let's check _mode
			{
				if( master_targetting[i].mode == _mode ) //if _mode is there too, this unique target is already in the master_targetting array.  stop the function.
				{
					return;
				}
			}
		}
		
		//if we got this far, it's safe to add this target.
		AddTarget( _id, _mode, _text );
	}
	
	// If your targetting function cancels peacefully (like the user decided not to
	//   use the potion after all, etc), then you should call this function before 
	//   ending the targetting function.  If you do not, various error messages will flow.
	//
	// This basically tells the rest of the system to not do anything peacefully.
	public static void CancelTargetting()
	{
		targetting_success = TARG_CANCEL;
	}
	
	
	// Call this function at the end of your function if everything is A-OK!
	//
	// It sets the targetting state to TARG_SUCCESS (a very good thing) if there are 
	// valid targets in the array, and sets it to TARG_CANCEL if there are no targets 
	// selected through accident or injury.
	public static void TargettingDone()
	{
		if( master_targetting_count > 0 )
		{
			targetting_success = TARG_SUCCESS;
		}
		else
		{
			targetting_success = TARG_CANCEL;
		}	
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Functions for use in whatever function cares about the targets that your custom targetting function just selected.
	//
	// Largely just accessors to master_targetting
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	//TargettingError() in the above function can be used gladly in the effect functions too.
	
	public static int GetTargettingCount()
	{
		return master_targetting_count;
	}
	
	public static int GetTargID( int t_idx )
	{
		if( !ValidTargetting() )
		{
			error( "GetTargID(), Tried to access data from an invalid targetting session"  );
			return 0-1;
		}
		else if( t_idx < 0 || t_idx >= master_targetting_count )
		{
			error( "GetTargID(), Tried to access data from an invalid index ("+str(t_idx)+" of "+str(master_targetting_count)+")"  );
			return 0-1;
		}
		
		return master_targetting[t_idx].id;
	}
	
	
	public static int GetTargMode( int t_idx )
	{
		if( !ValidTargetting() )
		{
			error( "GetTargMode(), Tried to access data from an invalid targetting session"  );
			return 0-1;
		}
		else if( t_idx < 0 || t_idx >= master_targetting_count )
		{
			error( "GetTargMode(), Tried to access data from an invalid index ("+str(t_idx)+" of "+str(master_targetting_count)+")"  );
			return 0-1;
		}
		
		return master_targetting[t_idx].mode;
	}
	
	
	public static String GetTargText( int t_idx )
	{
		if( !ValidTargetting() )
		{
			error( "GetTargText(), Tried to access data from an invalid targetting session"  );
			return "";
		}
		else if( t_idx < 0 || t_idx >= master_targetting_count )
		{
			error( "GetTargText(), Tried to access data from an invalid index ("+str(t_idx)+" of "+str(master_targetting_count)+")"  );
			return "";
		}
		
		return master_targetting[t_idx].text;
	}
}
