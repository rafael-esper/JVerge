package sully.vc.simpletype_rpg;

import static core.Script.*;
import static sully.vc.simpletype_rpg.Data.*;
import static sully.vc.simpletype_rpg.Inventory.*;
import static sully.vc.v1_menu.Menu_System.*;
import static sully.vc.v1_battle.Battle.*;
import static sully.vc.util.Targetting.*;

public class Item {
	// Item.vc, functions for using items.
	//
	// by McGrue
	// Started 2004.08.30
	/////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////
	// Item
	///////////////////////////////////////////////////////////////////////////////
	//
	// this struct holds the data for all items, be they supplies, equipment, or key
	//
	
	public String	name;	//name.  Must be unique.
	public String	desc;	//description.  FLUFF.
	public int		icon;	//index to icon
	public int		price;	//base price for the item.  value of zero or less prevents sale.
	
	public int		use_flag;	//specifies if it can be used in battle, menu, or both.
	
	public String	target_func;	//the name of the function that deals with targetting for this 
	public String	effect_func;	//the name of the function that deals with effect resolution.
	
	public int		equ_classes[] = new int[MAX_CLASSES]; //master_class[] index references to the classes that can equip this item

	public int		equ_slot;		//the slot this equipment can be put in.
	public String	equ_modcode;	//a special string representing what happens when this equipment is put on or taken off.

	
	//
	// Takes a String name of an item.  
	// returns master_items idx if it's a valid item name.  -1 if not.
	public static int IsItem( String name ) 
	{
		int i;
		
		for( i=0; i<MAX_ITEMS; i++ ) 
		{
			if( name.equals(master_items[i].name) ) 
			{
				return i;
			}
		}
		
		return 0-1;
	}
	
	
	// takes a master_items[] index
	// returns true if it's a key item, false if not.
	//
	static boolean IsKeyItem( int idx ) {
		
	
		//bounds checking.
		if( idx < 0 || idx > MAX_ITEMS ) {
			error( "IsKeyItem(): "+str(idx)+" is not a valid index." );
			return false;
		}
		
		//this is the current, very bad criteria for Key Itemness.
		// If the price is 0, it's key.  
		if( master_items[idx].price == 0 ) {
			return true;
		} else {
			return false;
		}
	}
	
	// takes a master_items[] index
	// returns true if it's a piece of equipment, false if not.
	//
	public static boolean IsEquipmentItem( int idx ) {
		//bounds checking.
		if( idx < 0 || idx > MAX_ITEMS ) {
			error( "IsEquipmentItem(): "+str(idx)+" is not a valid index." );
			return false;
		}
		
		//this is the current, very bad criteria for Equipmentness.
		// If the modcode is not an empty String, it's a piece of equipment.
		if( master_items[idx].equ_modcode.equals("" ) ) {
			return false;
		} else {
			return true;
		}
	}
	
	
	// takes a master_items[] index
	// returns true if it's a supply, false if not.
	//
	public static boolean IsSupplyItem( int idx ) {
		//bounds checking.
		if( idx < 0 || idx > MAX_ITEMS ) {
			error( "IsSupplyItem(): "+str(idx)+" is not a valid index." );
			return false;
		}
		
		//this is the current, very *VERY* bad criteria for Supplyness.
		// If the item is not a Key Item, and it's not an Equipment item, it's a Supply item!
		if( IsKeyItem(idx) || IsEquipmentItem(idx) ) {
			return false;
		} else {
			return true;
		}
	}
	
	
	// Triggers the use of an item with the specified master_items name.
	//
	// Errors if the name is not a valid member of master_items[]
	// Errors if the item is not usable in the current context (Menu or Battle)
	// Errors if the item has no defined effect function (after all, how can you use something that doesn't have a way to be used?)
	// Errors if there's a targetting function specified, but it does not exist.
	//
	static void UseItem( String name )
	{
		int idx = IsItem( name );
		
		if( idx < 0 )
		{
			error( "UseItem(): "+name+" is not a valid item name." );
			return;
		}
		
		UseItemI( idx );
	}
	
	
	// Triggers the use of an item with the specified master_items index.
	//
	// Errors if the index is not valid.
	// Errors if the item is not usable in the current context (Menu or Battle)
	// Errors if the item has no defined effect function (after all, how can you use something that doesn't have a way to be used?)
	// Errors if there's a targetting function specified, but it does not exist.
	// Errors if the item is not in your inventory.
	public static void UseItemI( int idx ) 
	{
		if( idx < 0 || idx >= MAX_ITEMS )
		{
			error( "UseItemI(): "+str(idx)+" is not a valid index (max: "+str(MAX_ITEMS)+")" );
			return;
		}
		
		String name = master_items[idx].name;
		String fx_func = master_items[idx].effect_func;
	
		if( !HasItemI(idx) )
		{
			error( "UseItemI("+str(idx)+"): "+name+" is not in your party's inventory.  You cannot use it." );
			return;
		}
		
		if( !ItemIsUsableI(idx) )
		{
			error( "UseItemI(): '"+name+"' IS NOT USABLE!  Why are you attempting to use it?" );
			return;
		}
		
		else if( !functionexists(fx_func) )
		{
			error( "UseItemI(): '"+name+"' is *supposed* to be usable, BUT it's effect function '"+fx_func+"()' DOES NOT EXIST.  Bad oversight.  If you're the gamemaker, you might want to make it. ;)" );
			return;
		}
		
		if( IsInMenu() )
		{
			if( CheckItemUseflagI(idx,USE_MENU) == 0 )
			{
				error( "UseItemI(): '"+name+"' is not allowed to be used in menus. If this is wrong, check its useflag permissions in the datafile." );
				return;
			}
		}
		else if( IsInBattle() )
		{
			if( CheckItemUseflagI(idx,USE_BATTLE)==0 )
			{
				error( "UseItemI(): '"+name+"' is not allowed to be used in battles. If this is wrong, check its useflag permissions in the datafile." );
				return;
			}
		}
		else
		{
			error( "UseItemI(): Items cannot be used outside of menus or battles at this time.  Bring it up with Grue or alter the library to your needs." );
			return;
		}
		
		//okay, if we got here, we can use it!
		
	
		//if the target_func variable is not blank, it has targetting!  Check it and call it!
		if( !master_items[idx].target_func.isEmpty() ) 
		{
			DoTargetting( master_items[idx].target_func );
	
			if( ValidTargetting() )
			{
				callfunction(fx_func);
				
				//lastly, if it was a supply, deduct one from the inventory.
				
				if( IsSupplyItem(idx) )
				{
					TakeItemI( idx, 1 );
					
					//if there are more, call it again!
					if( HasItemI(idx) )
					{
						UseItemI(idx);
					}
				}			
			}
			
			//if the targetting was invalid, we assume it was from cancelling.  
			//If an error happened there, it's the fault of the targetting system, and it 
			//should be the one to report the error.
		}
		else //if there's no targetting, 
		{
			callfunction(fx_func);
			
			//lastly, if it was a supply, deduct one from the inventory.
			if( IsSupplyItem(idx) )
			{
				TakeItemI( idx, 1 );
			}
			
		}
		
		//there.  We used it.  Yay.  
		
	
	}
	
	static int CheckItemUseflagI( int idx, int flag ) 
	{
		int i;
		
		i = master_items[idx].use_flag & flag;
		
		return i;
	}
	
	static boolean ItemIsUsableI( int idx )
	{
		//if the effect_func is empty, it cannot be used.
		if( master_items[idx].effect_func.isEmpty() )
		{
			return false;
		}
		else
		{
			return true;
		}
	}
}
