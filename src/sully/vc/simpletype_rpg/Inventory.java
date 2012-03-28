package sully.vc.simpletype_rpg;

import static core.Script.*;
import static sully.vc.simpletype_rpg.Data.*;
import static sully.vc.simpletype_rpg.Item.*;

public class Inventory {
	// inventory.vc
	//
	// Started by McGrue on 2004.08.26
	// Last updated by McGrue on 2004.08.26
	//
	// Internal Handling and Common Interface for the Sully Simple inventory system
	//
	
	
	//MAX_ITEMS defined in data.vc
	
	public static final int MAX_SUPPLIES	= 25;
	public static final int MAX_EQUIPMENT	= 50;
	public static final int MAX_KEY_ITEMS	= 25;
	
	//this is the most of a single item you can have.
	public static final int MAX_INV_SLOT	= 99;
	
	public int item_ref;	//the index of the item
	public int quant;	//the number of this item the party currently possesses.
	
	public static Inventory supply_inventory[] = new Inventory[MAX_SUPPLIES];
	public static Inventory equipment_inventory[] = new Inventory[MAX_EQUIPMENT];
	public static Inventory key_item_inventory[] = new Inventory[MAX_KEY_ITEMS];
	
	public static int _supply_count;
	public static int _equip_count;
	public static int _key_count;
	
	// Accessors for how many unique items of each type 
	//   are currently in inventory.
	public static int SupplyCount()		{	return _supply_count; }
	public static int EquipmentCount()	{	return _equip_count; }
	public static int KeyItemCount()	{	return _key_count; }
	
	
	// Nukes the inventory.  Best only to use on newgame ;)
	//
	public static void ClearInventory()
	{
		int i;
		
		for( i=0; i<MAX_SUPPLIES; i++ )
		{
			supply_inventory[i].item_ref = 0;
			supply_inventory[i].quant = 0;
		}
		
		for( i=0; i<MAX_EQUIPMENT; i++ )
		{
			equipment_inventory[i].item_ref = 0;
			equipment_inventory[i].quant = 0;
		}
		
		for( i=0; i<MAX_KEY_ITEMS; i++ )
		{
			key_item_inventory[i].item_ref = 0;
			key_item_inventory[i].quant = 0;
		}
		
		_supply_count	= 0;
		_equip_count	= 0;
		_key_count		= 0;
	}
	
	
	// returns the quantity of a specific item you have.
	// errors and returns 0 if the name is not a valid item name.
	int ItemCount( String name )
	{
		int i = IsItem(name);
		int j;
	
		if( i < 0 )
		{
			error( "ItemCount(): "+name+" is not a valid item name." );
			return 0;
		}
		
		if( !HasItemI(i) )
		{
			return 0;
		}
		else
		{
			j = InvIdxI(i);
	
			if( IsSupplyItem(i) )
			{
				return supply_inventory[j].quant;
			}
			else if( IsEquipmentItem(i) )
			{
				i = InvIdxI(i);
				return equipment_inventory[j].quant;
			}
			else //it's a key item
			{
				i = InvIdxI(i);
				return key_item_inventory[j].quant;
			}
		}
	}
	
	// Takes a supply inventory index and returns a master_items index
	// Errors on illegal bounds and returns -1
	int GetSupply( int idx ) { 
		if( idx < 0 || idx >= SupplyCount() ) {
			error( "GetSupply(): "+str(idx)+" is not a valid index." );
			return 0-1;
		}
		
		return supply_inventory[idx].item_ref;
	}
	
	// Takes a supply inventory index and returns a master_items index
	// Errors on illegal bounds and returns -1
	static int GetEquipment( int idx ) { 
		if( idx < 0 || idx >= EquipmentCount() ) {
			error( "GetEquipment(): "+str(idx)+" is not a valid index." );
			return 0-1;
		}
		
		return equipment_inventory[idx].item_ref;
	}
	
	// Takes a supply inventory index and returns a master_items index
	// Errors on illegal bounds and returns -1
	int GetKeyItem( int idx ) { 
		if( idx < 0 || idx >= KeyItemCount() ) {
			error( "GetKeyItem(): "+str(idx)+" is not a valid index." );
			return 0-1;
		}
		
		return key_item_inventory[idx].item_ref;
	}
	
	
	// Takes a name of an Item and a number.
	// If the name is a valid item, adds the number to your inventory.
	// Otherwise, errors.
	//
	public static void GiveItem( String name, int num ) {
		int i, idx;
		idx = 0-1;
		
		for( i=0; i<MAX_ITEMS; i++ ) {
			if( name.equals(master_items[i].name) ) {
				idx = i;
			}
		}
		
		if( idx < 0 ) {
			error( "GiveItem(): '"+name+"' is not a valid itemname." );
			return;
		} else {
			GiveItemI( idx, num );
		}
	}
	
	
	public static // Takes a master_items index and a number.
	// If the index is valid, adds the number to your inventory.
	// Otherwise, errors.
	//
	void GiveItemI( int idx, int num ) {
		//bounds checking.
		if( idx < 0 || idx > MAX_ITEMS ) {
			error( "GiveItemI(): "+str(idx)+" is not a valid index." );
			return;
		}
		
		//we get to grant items.  Oh boy!
		if( IsKeyItem(idx) ) {
			_AddKeyItem( idx, num );
		} else if( IsEquipmentItem(idx) ) {
			_AddEquipment( idx, num );
		} else { //it must be a supply!
			_AddSupply( idx, num );
		}
	}
	
	//
	// Takes a String name of an item.  
	// Returns true if that item is in inventory.  returns false if not.
	// Errors if the item isn't an item at all.
	boolean HasItem( String name ) {
		int idx;
		
		idx = IsItem(name);
	
		if( idx < 0 ) {
			error( "HasItem(): '"+name+"' is not a valid item name." );
			return false;
		} else {		
			return HasItemI( idx );
		}
	}
	
	//
	// Takes a master_items idx
	// Returns true if there is at least one of that item in your inventory, false if not
	// Errors if idx is invalid.
	static boolean HasItemI( int idx ) {
		int i;
		
		if( idx < 0 || idx >= MAX_ITEMS ) {
			error( "HasItemI(): '"+str(idx)+"' is not a valid item index." );
			return false;
		}
	
		if( IsKeyItem(idx) ) {
			for(i=0; i<_key_count; i++) {
				if( key_item_inventory[i].item_ref == idx ) {
					return true;
				}
			}
			return false;
			
		} else if( IsEquipmentItem(idx) ) {
			for(i=0; i<_equip_count; i++) {
				if( equipment_inventory[i].item_ref == idx ) {
					return true;
				}
			}
			return false;
		} else { //it must be a supply!
			for(i=0; i<_supply_count; i++) {
				if( supply_inventory[i].item_ref == idx ) {
					return true;
				}
			}
			return false;
		}
	}
	
	
	// Takes a master_items name
	// Returns the appropriate inventory index, if it's in inventory.   -1 if not.
	// Errors if name is invalid.
	int InvIdx( String name ) {
		return InvIdxI(IsItem(name));
	}
	
	
	//
	// Takes a master_items idx
	// Returns the appropriate inventory index, if it's in inventory.   -1 if not.
	// Errors if idx is invalid.
	int InvIdxI( int idx ) {
		int i;
		
		if( idx < 0 || idx >= MAX_ITEMS ) {
			error( "InvIdxI(): '"+str(idx)+"' is not a valid item index." );
			
			return 0;
		}
	
		if( IsKeyItem(idx) ) {
			for(i=0; i<_key_count; i++) {
				if( key_item_inventory[i].item_ref == idx ) {
					return i;
				}
			}
			
			return 0-1;
			
		} else if( IsEquipmentItem(idx) ) {
			for(i=0; i<_equip_count; i++) {
				if( equipment_inventory[i].item_ref == idx ) {
					return i;
				}
			}
			
			return 0-1;
			
		} else { //it must be a supply!
			for(i=0; i<_supply_count; i++) {
				if( supply_inventory[i].item_ref == idx ) {
					return i;
				}
			}
			
			return 0-1;
		}
	}
	
	
	// Like GiveItem, but removes.
	// num must be a positive non-zero integer.  
	//   If you want positive and negative functionality, use GiveItem.
	//   This is just a semantic wrapper, dammit!
	
	//note, could be done 'better'.  Very tired now.  -Grue.
	static void TakeItem( String name, int num ) {
		int i, idx;
		
		if( num <= 0 ) {
			error( "TakeItem(): 'you cannot take a negative or null amount from your inventory." );
		}
		
		idx = IsItem( name );
		
		if( idx < 0 ) {
			error( "TakeItem(): '"+name+"' is not a valid itemname." );
			return;
		} else {
			
			if( !HasItemI(idx) ) {
				error( "TakeItem(): 'Cannot remove an item your party does not have." );
			} else {
				GiveItemI( idx, 0-num );
			}
		}
	}
	
	// Like GiveItemI, but removes.
	// num must be a positive non-zero integer.  
	//   If you want positive and negative functionality, use GiveItem.
	//   This is just a semantic wrapper, dammit!
	
	//note, could be done 'better'.  Very tired now.  -Grue.
	public static void TakeItemI( int idx, int num ) {
		if( num <= 0 ) {
			error( "TakeItemI(): 'you cannot take a negative or null amount from your inventory." );
			return;
		}
		
		if( !HasItemI(idx) ) {
			error( "TakeItemI(): 'Cannot remove an item your party does not have." );
		} else {
			GiveItemI( idx, 0-num );
		}
	}
	
	
	// Destroys all of this item in inventory.  Simple wrapper around TakeItem.
	//
	// name must be a valid item name and must have at least one in inventory.
	static void DestroyItem( String name )
	{
		TakeItem( name, MAX_INV_SLOT );
	}
	
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////
	//BELOW HERE ARE HELPER FUNCTIONS!  DO NOT ALTER UNLESS YOU UNDERSTAND THE JUJU AT WORK!
	///////////////////////////////////////////////////////////////////////////////////////////////////
	
	//int _supply_count, _equip_count, _key_count;
	//InventorySlot supply_inventory[MAX_SUPPLIES];
	//InventorySlot equipment_inventory[MAX_EQUIPMENT];
	//InventorySlot key_item_inventory[MAX_KEY_ITEMS];
	
	static void _AddSupply( int idx, int quant ) {
		int i,j;
		
		if( _supply_count >= MAX_SUPPLIES ) {
			error( "VERY BAD ERROR." );
			error( "_AddSupply(), the internal master-array count was greater or equal to MAX_SUPPLIES ("+str(MAX_SUPPLIES)+")" );
			return;
		}
		
		for(i=0; i<_supply_count; i++) {
			if( supply_inventory[i].item_ref == idx ) {
				supply_inventory[i].quant += quant;
				
				if( supply_inventory[i].quant <= 0 ) {
					// If we're eliminating this entry, move everything else up.
					// and decrement the internal counter
					
					for( j=i; j<=_supply_count; j++ ) {
						supply_inventory[j].item_ref = supply_inventory[j+1].item_ref;
						supply_inventory[j].quant = supply_inventory[j+1].quant;
					}
					
					_supply_count--;
					
					supply_inventory[_supply_count].item_ref = 0-1;
					supply_inventory[_supply_count].quant = 0;
					
				} else if( supply_inventory[i].quant > MAX_INV_SLOT ) {
					supply_inventory[i].quant = MAX_INV_SLOT;
				}
				
				return;
			}
		}
		
		//if we get here, add a new entry.  No negs allowed.
		if( quant > 0 ) {
			supply_inventory[_supply_count].item_ref = idx;
			supply_inventory[_supply_count].quant = quant;
			
			_supply_count++;
		}
	}
	
	static void _AddEquipment( int idx, int quant ) {
		int i,j;
		
		if( _equip_count >= MAX_EQUIPMENT ) {
			error( "VERY BAD ERROR." );
			error( "_AddEquipment(), the internal master-array count was greater or equal to MAX_EQUIPMENT ("+str(MAX_EQUIPMENT)+")" );
			return;
		}
		
		for(i=0; i<_equip_count; i++) {
			if( equipment_inventory[i].item_ref == idx ) {
				equipment_inventory[i].quant += quant;
				
				if( equipment_inventory[i].quant <= 0 ) {
					// If we're eliminating this entry, move everything else up.
					// and decrement the internal counter
					
					for( j=i; j<=_equip_count; j++ ) {
						equipment_inventory[j].item_ref = equipment_inventory[j+1].item_ref;
						equipment_inventory[j].quant = equipment_inventory[j+1].quant;
					}
	
					_equip_count--;
					
					equipment_inventory[_equip_count].item_ref = 0-1;
					equipment_inventory[_equip_count].quant = 0;
					
				} else if( equipment_inventory[i].quant > MAX_INV_SLOT ) {
					equipment_inventory[i].quant = MAX_INV_SLOT;
				}
				
				return;
			}
		}
		
		//if we get here, add a new entry.  No negs allowed.
		if( quant > 0 ) {
			equipment_inventory[_equip_count].item_ref = idx;
			equipment_inventory[_equip_count].quant = quant;
			
			_equip_count++;
		}
	}
	
	static void _AddKeyItem( int idx, int quant ) {
		int i,j;
		
		if( _key_count >= MAX_KEY_ITEMS ) {
			error( "VERY BAD ERROR." );
			error( "_AddKeyItem(), the internal master-array count was greater or equal to MAX_KEY_ITEMS ("+str(MAX_KEY_ITEMS)+")" );
			return;
		}
		
		for(i=0; i<_key_count; i++) {
			if( key_item_inventory[i].item_ref == idx ) {
				key_item_inventory[i].quant += quant;
				
				if( key_item_inventory[i].quant <= 0 ) {
					// If we're eliminating this entry, move everything else up.
					// and decrement the internal counter
					
					for( j=i; j<=_key_count; j++ ) {
						key_item_inventory[j].item_ref = key_item_inventory[j+1].item_ref;
						key_item_inventory[j].quant = key_item_inventory[j+1].quant;
					}
					
					_key_count--;
					
					key_item_inventory[_key_count].item_ref = 0-1;
					key_item_inventory[_key_count].quant = 0;
					
					
				} else if( key_item_inventory[i].quant > MAX_INV_SLOT ) {
					key_item_inventory[i].quant = MAX_INV_SLOT;
				}
				
				return;
			}
		}
		
		//if we get here, add a new entry.  No negs allowed.
		if( quant > 0 ) {
			key_item_inventory[_key_count].item_ref = idx;
			key_item_inventory[_key_count].quant = quant;
			
			_key_count++;
		}
	}
	
	public static void initInventories() {
		int i;
		for( i=0; i<MAX_SUPPLIES; i++ ) {
			supply_inventory[i] = new Inventory();
			supply_inventory[i].item_ref = 0-1;
			supply_inventory[i].quant = 0;
		}
	
		for( i=0; i<MAX_EQUIPMENT; i++ ) {
			equipment_inventory[i] = new Inventory();
			equipment_inventory[i].item_ref = 0-1;
			equipment_inventory[i].quant = 0;
		}
		
		for( i=0; i<MAX_KEY_ITEMS; i++ ) {
			key_item_inventory[i] = new Inventory();
			key_item_inventory[i].item_ref = 0-1;
			key_item_inventory[i].quant = 0;
		}	
	}
	
	void ReportSupplyInventory() {
		int i;
		
		log( "" );
		log( "ReportSupplyInventory" );
		log( "=====================" );
		log( "_supply_count: "+ str(_supply_count) );
		
		for(i=0; i<_supply_count; i++) {
			log( str(i)+": "+master_items[supply_inventory[i].item_ref].name+" ("+str(supply_inventory[i].quant)+")" );
		}
		log( "=====================" );
	}
	
	
	void ReportEquipmentInventory() {
		int i;
		
		log( "" );
		log( "ReportEquipmentInventory" );
		log( "=====================" );
		log( "_equip_count: "+ str(_equip_count) );
		
		for(i=0; i<_equip_count; i++) {
			log( str(i)+": "+master_items[equipment_inventory[i].item_ref].name+" ("+str(equipment_inventory[i].quant)+")" );
		}
		log( "=====================" );
	}
	
	
	void ReportKeyInventory() {
		int i;
		
		log( "" );
		log( "ReportKeyInventory" );
		log( "=====================" );
		log( "_key_count: "+ str(_key_count) );
		
		for(i=0; i<_key_count; i++) {
			log( str(i)+": "+master_items[key_item_inventory[i].item_ref].name+" ("+str(key_item_inventory[i].quant)+")" );
		}
		log( "=====================" );
	}
}