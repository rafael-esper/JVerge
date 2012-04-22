package sully.vc.simpletype_rpg;

import static core.Script.*;
import static sully.vc.simpletype_rpg.Data.*;
import static sully.vc.simpletype_rpg.Item.*;
import static sully.vc.simpletype_rpg.Inventory.*;
import static sully.vc.simpletype_rpg.Party.*;
import static sully.vc.simpletype_rpg.Cast.*;

public class Equipment {
	
	// equipment.vc
	//
	// Started by McGrue on 2004.08.27
	// Last Updated by McGrue on 2004.08.29
	//
	// Internal Handling and Common Interface for the Sully Simple equipment system
	//
	
	
	// Takes a master_items index
	// returns the number of modcodes.
	// errors if there are no modcodes, as that's not equipment.
	// errors if the index is invalid.
	static int EquModcodeCount( int idx ) {
		//bounds checking.
		if( idx < 0 || idx >= MAX_ITEMS ) {
			error( "EquModcodeCount(): "+str(idx)+" is not a valid index." );
			return 0;
		}
		
		if( strcmp( master_items[idx].equ_modcode,"" )) {
			error( "EquModcodeCount(): item idx "+str(idx)+" is not valid equipment." );
			return 0;
		}
		
		return tokencount( master_items[idx].equ_modcode , ";" );
	}
	
	// Takes a master_items index, and a token index.
	// returns the stat-define value for this token.
	// errors only if the modcode is not valid, does not validate anything else.  returns -1 in this case.
	static int EquGetModcodeStat( int item_idx, int token_idx ) {
		int stat;
		String tok = gettoken( master_items[item_idx].equ_modcode , ";", token_idx );
		tok = gettoken( tok, ",", 0 ).trim().toUpperCase();
		
		if 		("MAX_HP".equals(tok)) stat = STAT_MAX_HP; // 0
		else if ("MAX_MP".equals(tok)) stat = STAT_MAX_MP; // 1
		else if ("STR".equals(tok))	stat = STAT_STR; // 2
		else if ("END".equals(tok)) 	stat = STAT_END; // 3
		else if ("MAG".equals(tok)) 	stat = STAT_MAG; // 4 
		else if ("MGR".equals(tok)) 	stat = STAT_MGR; // 5 
		else if ("HIT".equals(tok)) 	stat = STAT_HIT; // 6
		else if ("DOD".equals(tok)) 	stat = STAT_DOD; // 7 
		else if ("MBL".equals(tok)) 	stat = STAT_MBL; // 8
		else if ("FER".equals(tok)) 	stat = STAT_FER; // 9
		else if ("REA".equals(tok)) 	stat = STAT_REA; // 10
		else if ("ATK".equals(tok)) 	stat = STAT_ATK; // 11
		else if ("DEF".equals(tok)) 	stat = STAT_DEF; // 12
		else {
			error( "EquGetModcodeStat(): stat token '"+tok+"' is not a valid stat." );
			return 0-1;
		}

		return stat;
	}
	
	// Takes a master_items index, and a token index.
	// returns the value for this token's mod.
	// does no errorchecking.
	static int EquGetModcodeVal( int item_idx, int token_idx ) {
		String tok = gettoken( master_items[item_idx].equ_modcode , ";", token_idx );
		tok = gettoken( tok, ",", 1 );
		
		return val(tok);
	}
	
	// takes a cast index and a slot index.
	// returns true if there's a piece of equipment there, otherwise false.
	// errors if a bad index or a bad slot index are given.
	public static boolean HasEquipment( int cast_idx, int equ_slot ) {
		if( cast_idx < 0 || cast_idx >= MAX_CAST ) {
			error( "HasEquipment(): "+str(cast_idx)+" is not a valid index." );
			return false;
		}
		
		if( equ_slot < 0 || equ_slot >= MAX_EQUIP_SLOTS ) {
			error( "HasEquipment(): "+str(equ_slot)+" is not a valid equip slot index." );
			return false;
		}
		
		if( master_cast[cast_idx].equipment[equ_slot] >= 0 ) {
			return true;
		} else {
			return false;
		}
	}
	
	// takes an equipment index and a stat
	// returns the bonus this equipment gives that stat.
	// errors if it's not valid equipment or if the slot is invalid
	static int GetEquipmentStatMod( int equ_idx, int stat_idx ) {
		int i, stat, mod;
	
		if( !IsEquipmentItem(equ_idx) ) {
			error( "GetEquipmentStatMod(): "+str(equ_idx)+" is not a valid piece of equipment." );
			return 0;
		}
		
	//	if( stat_idx < 0 || stat_idx >= MAX_STATS ) {
	//		error( "GetEquipmentStatMod(): "+str(stat_idx)+" is not a valid stat define." );
	//		return 0;
	//	}
		
		for( i=0; i<EquModcodeCount(equ_idx); i++ ) {
			stat = EquGetModcodeStat( equ_idx, i );
			
			if( stat_idx == stat ) {
				return EquGetModcodeVal( equ_idx, i );
			}
		}
		
		return 0;
	}
	
	//
	// takes a cast idx, equipment idx, and stat field.
	// returns the cast member's stat if he equipped the specified equipment, 
	//   without permanently effecting anything.
	// errors if cast, equip, slot, or stat are invalid.
	static int GetMyStatPretendEquip( String cast_name, String equ_name, int slot_idx, int stat_idx ) {
		int chr_idx, equ_idx;
		
		chr_idx = IsCharacter( cast_name );
		
		if( chr_idx < 0 ) {
			error( "GetMyStatPretendEquip(): '"+cast_name+"' is not a valid cast name." );
			return 0;		
		}
		
		if( slot_idx < 0 || slot_idx >= MAX_EQUIP_SLOTS ) {
			error( "GetMyStatPretendEquip(): "+str(slot_idx)+" is not a valid slot define." );
			return 0-1;
		}
		
		equ_idx = IsItem( equ_name );
		
		if( equ_idx < 0 ) {
			error( "GetMyStatPretendEquip(): '"+equ_name+"' is not a valid item name." );
			return 0;		
		}
		
		if( !IsEquipmentItem(equ_idx) ) {
			error( "GetMyStatPretendEquip(): '"+equ_name+"' is not a piece of equipment." );
			return 0;		
		}
		
		if( stat_idx < 0 || stat_idx >= MAX_STATS ) {
			error( "GetMyStatPretendEquip(): "+str(stat_idx)+" is not a valid stat define." );
			return 0;
		}
		
		return GetMyStatPretendEquipI( chr_idx, equ_idx, slot_idx, stat_idx );
	}
	
	
	//
	// takes a cast idx, equipment idx, and stat field.
	// returns the cast member's stat if he equipped the specified equipment, 
	//   without permanently effecting anything.
	// errors if cast, equip, slot, or stat are invalid.
	public static int GetMyStatPretendEquipI( int cast_idx, int equ_idx, int slot_idx, int stat_idx ) {
		int cur_stat_bonus=0, cur_stat;
		
		if( cast_idx <0 || cast_idx >= MAX_CAST ) {
			error( "GetMyStatPretendEquipI(): "+str(cast_idx)+" is not a valid cast index." );
			return 0;		
		}
		
		if( slot_idx < 0 || slot_idx >= MAX_EQUIP_SLOTS ) {
			error( "GetMyStatPretendEquipI(): "+str(slot_idx)+" is not a valid slot define." );
			return 0-1;
		}
		
		if( !IsEquipmentItem(equ_idx) ) {
			error( "GetMyStatPretendEquipI(): "+str(equ_idx)+" is not a valid piece of equipment." );
			return 0;
		}
		
		if( stat_idx < 0 || stat_idx >= MAX_STATS ) {
			error( "GetMyStatPretendEquipI(): "+str(stat_idx)+" is not a valid stat define." );
			return 0;
		}
		
		if( HasEquipment(cast_idx, slot_idx) ) {
			cur_stat_bonus = GetEquipmentStatMod( master_cast[cast_idx].equipment[slot_idx], stat_idx );
		}
		
		cur_stat = master_cast[cast_idx].stats[stat_idx];
		
		cur_stat = cur_stat - cur_stat_bonus;
		
		cur_stat += GetEquipmentStatMod( equ_idx, stat_idx );
		
		return cur_stat;
	}
	
	//
	// takes a cast idx, equipment idx, and stat field.
	// returns the cast member's stat if he removed the specified equipment, 
	//   without permanently effecting anything.
	// errors if cast, stat, or slot are invalid and returns -1.
	static int GetMyStatPretendDequip( String cast_name, int slot_idx, int stat_idx ) {
		int chr_idx;
		
		chr_idx = IsCharacter( cast_name );
		
		if( chr_idx < 0 ) {
			error( "GetMyStatPretendDequip(): '"+cast_name+"' is not a valid cast name." );
			return 0-1;		
		}
		
		if( stat_idx < 0 || stat_idx >= MAX_STATS ) {
			error( "GetMyStatPretendDequip(): "+str(stat_idx)+" is not a valid stat define." );
			return 0-1;
		}
		
		if( slot_idx < 0 || slot_idx >= MAX_EQUIP_SLOTS ) {
			error( "GetMyStatPretendDequipI(): "+str(slot_idx)+" is not a valid slot define." );
			return 0-1;
		}
		
		if( master_cast[chr_idx].equipment[slot_idx] < 0 ) {
			return master_cast[chr_idx].stats[stat_idx];
		}
		
		return GetMyStatPretendDequipI( chr_idx, slot_idx, stat_idx );
	}
	
	
	//
	// takes a cast idx, slot idx, and stat field.
	// returns the cast member's stat if he equipped the specified equipment, 
	//   without permanently effecting anything.
	// errors if cast, slot, or stat are invalid and returns -1.
	public static int GetMyStatPretendDequipI( int cast_idx, int slot_idx, int stat_idx ) {
		int cur_stat_bonus, cur_stat;
		
		if( cast_idx <0 || cast_idx >= MAX_CAST ) {
			error( "GetMyStatPretendDequipI(): "+str(cast_idx)+" is not a valid cast index." );
			return 0-1;		
		}
		
		if( stat_idx < 0 || stat_idx >= MAX_STATS ) {
			error( "GetMyStatPretendDequipI(): "+str(stat_idx)+" is not a valid stat define." );
			return 0-1;
		}
		
		if( slot_idx < 0 || slot_idx >= MAX_EQUIP_SLOTS ) {
			error( "GetMyStatPretendDequipI(): "+str(slot_idx)+" is not a valid slot define." );
			return 0-1;
		}
		
		//no equipment!
		if( master_cast[cast_idx].equipment[slot_idx] < 0 ) {
			return master_cast[cast_idx].stats[stat_idx];
		} else {
			cur_stat_bonus = GetEquipmentStatMod( master_cast[cast_idx].equipment[slot_idx], stat_idx );
		}
			
		cur_stat = master_cast[cast_idx].stats[stat_idx];
		
		cur_stat = cur_stat - cur_stat_bonus;
		
		return cur_stat;
	}
	
	
	
	// Takes a cast name and an item name
	// returns true if this cast member can equip this item, false if not.
	// errors if cast or item do not exist.
	static int CanEquip( String cast_name, String equ_name ) {
		int cast_idx, equ_idx;
	
		
		cast_idx = IsCharacter( cast_name );
		
	
		if( cast_idx < 0  ) {
			error( "CanEquip(): '"+cast_name+"' is not a valid cast name." );
			return 0;		
		}
		
		equ_idx = IsItem( equ_name );
		
		if( !IsEquipmentItem(equ_idx) ) {
			error( "CanEquip(): '"+equ_name+"' is not a valid piece of equipment." );
			return 0;
		}
		
		return CanEquipI( cast_idx, equ_idx );
	}
	
	// Takes a cast idx and an equipment idx
	// returns true if this cast member can equip this item, false if not.
	// errors if cast or item do not exist.
	public static int CanEquipI( int cast_idx, int equ_idx ) {
		if( cast_idx <0 || cast_idx >= MAX_CAST ) {
			error( "CanEquipI(): "+str(cast_idx)+" is not a valid cast index." );
			return 0;		
		}
		
		if( !IsEquipmentItem(equ_idx) ) {
			error( "CanEquipI(): "+str(equ_idx)+" is not a valid piece of equipment." );
			return 0;
		}
		
		return master_items[equ_idx].equ_classes[master_cast[cast_idx].class_ref];
	}
	
	// takes a slot index.
	// returns the number of unique pieces of equipment presently in inventory that can be 
	//   equipped in the given slot
	//
	// no real errorchecking.
	public static int EquCountBySlot( int slot_idx ) {
		int i, count=0, _slot;
		
		if( slot_idx == SLOT_ACC2 ) {
			_slot = SLOT_ACC1;
		} else {
			_slot = slot_idx;
		}
		
	
		for( i=0; i<EquipmentCount(); i++ ) {
			if( master_items[GetEquipment(i)].equ_slot == _slot ) {
				count++;
			}
		}
		
		return count;
	}
	
	
	// Takes a slottype and the contextual index of the set of unique equipment 
	//   in inventory that may be equipped in that slot
	//
	// returns master_items index of that equipment.
	//
	// To be used with EquCountBySlot();
	//
	// errors and returns -1 if out of bounds.
	public static int GetEquBySlot( int slot, int idx ) {
		int i, count=0, _slot;
		
		if( idx < 0 || idx >= EquCountBySlot(slot) ) {
			error( "GetEquBySlot(): '"+str(idx)+"' is not a valid index for this inventory context." );	
		}
		
		//SLOT_ACC1 is the classification for ACC-slot items
		if( slot == SLOT_ACC2 ) {
			_slot = SLOT_ACC1;
		} else {
			_slot = slot;
		}
		
		for( i=0; i<EquipmentCount(); i++ ) {
			if( master_items[GetEquipment(i)].equ_slot == _slot ) {
				
				if( idx == count ) {
					return GetEquipment(i);
				} else {
					count++;
				}
			}
		}
		
		error( "GetEquBySlot(): index '"+str(idx)+"' caused something truely terrible to happen." );
		return 0-1;
	}
	
	
	// Takes a slottype and the contextual index of the set of unique equipment 
	//   in inventory that may be equipped in that slot
	//
	// returns the inventory quantity of that equipment.
	//
	// To be used with EquCountBySlot();
	//
	// errors and returns -1 if out of bounds.
	public static int GetEquQuantBySlot( int slot, int idx ) {
		int i, count=0, _slot;
		
		if( idx < 0 || idx >= EquCountBySlot(slot) ) {
			error( "GetEquQuantBySlot(): '"+str(idx)+"' is not a valid index for this inventory context." );	
		}
		
		//SLOT_ACC1 is the classification for ACC-slot items
		if( slot == SLOT_ACC2 ) {
			_slot = SLOT_ACC1;
		} else {
			_slot = slot;
		}
		
		for( i=0; i<EquipmentCount(); i++ ) {
			if( master_items[GetEquipment(i)].equ_slot == _slot ) {
				if( idx == count ) {
					return equipment_inventory[i].quant;
				} else {
					count++;
				}
			}
		}
		
		error( "GetEquBySlot(): index '"+str(idx)+"' caused something truely terrible to happen." );
		return 0-1;
	}
	
	// Gives the specified piece of equipment to the party, then force-equips it
	//
	//
	public static void ForceEquip( String cast_name, String equ_name, int slot )
	{
		GiveItem( equ_name,1);
		EquipItem( cast_name, equ_name, slot );
	}
	
	
	// Encapsulates the entire equipping process
	//
	// errors if cast or equipment doesn't exist, 
	// or if the cast member cannot equip this piece.
	static void EquipItem( String cast_name, String equ_name, int slot ) {
		int cast_idx, equ_idx;
		
		cast_idx = IsCharacter( cast_name );
		
		if( cast_idx < 0 ) {
			error( "EquipItem(): '"+cast_name+"' is not a valid cast name." );
			return;		
		}
		
		equ_idx = IsItem( equ_name );
	
		if( !IsEquipmentItem(equ_idx) ) {
			error( "EquipItem(): '"+equ_name+"' is not a valid piece of equipment." );
			return;
		}
		
		EquipItemI( cast_idx, equ_idx, slot );
	}
	
	// Encapsulates the entire equipping process
	//
	// errors if cast or equipment don't exist, 
	// or if the cast member cannot equip this piece.
	// or if there isn't any of that in the inventory.
	public static void EquipItemI( int cast_idx, int equ_idx, int slot ) {
		if( cast_idx <0 || cast_idx >= MAX_CAST ) {
			error( "EquipItemI(): "+str(cast_idx)+" is not a valid cast index." );
			return;
		}
		
		if( !IsEquipmentItem(equ_idx) ) {
			error( "EquipItemI(): "+str(equ_idx)+" is not a valid piece of equipment." );
			return;
		}
		
		if( CanEquipI(cast_idx, equ_idx) == 0 ) {
			error( "EquipItemI(): The cast member ("+master_cast[cast_idx].name+") cannot equip the specified equipment ("+master_items[equ_idx].name+")." );
			return;		
		}
		
		if( slot != master_items[equ_idx].equ_slot ) {
			if( master_items[equ_idx].equ_slot == SLOT_ACC1 && slot == SLOT_ACC2 ) {
				
			} else {
				error( "EquipItemI(): The specified equipment ("+master_items[equ_idx].name+") cannot be equipped in slot "+str(slot)+"." );
				return;
			}
		}
		
		if( !HasItemI(equ_idx) ) {
			error( "EquipItemI(): Item "+str(equ_idx)+" is not in the inventory." );
			return;
		}
		
		//oh boy, it can actually go on now!
		
		if( HasEquipment(cast_idx, slot) ) {
			_removeEquipment( cast_idx, slot );
		}
		
		_applyEquipment( cast_idx, equ_idx, slot );
		TakeItemI( equ_idx, 1 );
		
		_update_curs(cast_idx);
	}
	
	// Encapsulates the entire dequipping process
	//
	// ONLY USE TO MAKE SOMEONE NAKED IN THAT SLOT.
	//
	// errors if cast member doesn't exist, 
	// or if the cast member is not wearing equipment in that slot.
	static void DequipItem( String cast_name, int slot ) {
		int cast_idx, equ_idx;
		
		cast_idx = IsCharacter( cast_name );
		
		if( cast_idx < 0 ) {
			error( "DequipItem(): '"+cast_name+"' is not a valid cast name." );
			return;		
		}
		
		if( slot < 0 || slot >= MAX_EQUIP_SLOTS ) {
			error( "DequipItem(): '"+str(slot)+"' is not a valid slot index." );
			return;				
		}
		
		if( master_cast[cast_idx].equipment[slot] < 0 ) {
			error( "DequipItem(): '"+cast_name+"' is not wearing any equipment in that slot." );
			return;		
		}
		
		DequipItemI( cast_idx, slot );
	}
	
	
	// takes a cast index and an equipment slot and removes the equipment
	//
	// ONLY USE TO MAKE SOMEONE NAKED IN THAT SLOT.
	//
	// errors if the cast_idx is invalid, if slot is out of bounds,
	// or if the cast member is not wearing equipment in that slot.
	public static void DequipItemI( int cast_idx, int slot ) {
		
		if( cast_idx < 0 || cast_idx >= MAX_CAST ) {
			error( "DequipItemI(): '"+str(cast_idx)+"' is not a valid cast index." );
			return;		
		}
		
		if( slot < 0 || slot >= MAX_EQUIP_SLOTS ) {
			error( "DequipItemI(): '"+str(slot)+"' is not a valid slot index." );
			return;				
		}
		
		if( master_cast[cast_idx].equipment[slot] < 0 ) {
			error( "DequipItem(): '"+master_cast[cast_idx].name+"' is not wearing any equipment in that slot." );
			return;		
		}
		
		_removeEquipment( cast_idx, slot );
		
		_update_curs(cast_idx);
	}
	
	
	 //////////////////////////////////////////////////////////////////////////////////////
	// private functions,  Not for you!  Unless you *know*. Do you know?  No?  Then go! //
	//////////////////////////////////////////////////////////////////////////////////////
	
	public static void _removeEquipment( int cast_idx, int slot ) {
		int _equ;
	
		if( !HasEquipment( cast_idx, slot ) ) {
			return;
		}
		
		_equ = master_cast[cast_idx].equipment[slot];
		
		GiveItemI( _equ, 1 );
		
		master_cast[cast_idx].equipment[slot] = 0-1;
		
		_applyModcode( cast_idx, _equ, false );
	}
	
	static void _applyEquipment( int cast_idx, int equ_idx, int slot ) {
		master_cast[cast_idx].equipment[slot] = equ_idx;
		
		_applyModcode( cast_idx, equ_idx, true );
	}
	
	static void _applyModcode( int cast_idx, int equ_idx, boolean onFlag ) {
		int i, stat, mod;
		for( i=0; i<EquModcodeCount(equ_idx); i++ ) {
			stat = EquGetModcodeStat( equ_idx, i );
			mod = EquGetModcodeVal( equ_idx, i );
			
			if( !onFlag ) {
				mod = 0-mod;
			}
			
			master_cast[cast_idx].stats[stat] += mod;
		}
	}
	
	static void _update_curs( int idx )
	{
		if( getMaxHP(idx) < master_cast[idx].cur_hp )
			master_cast[idx].cur_hp = getMaxHP(idx);
			
		if( getMaxMP(idx) < master_cast[idx].cur_mp )
			master_cast[idx].cur_mp = getMaxMP(idx);
	}
}