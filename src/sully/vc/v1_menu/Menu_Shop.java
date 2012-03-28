package sully.vc.v1_menu;

import static core.Script.*;
import static sully.vc.v1_menu.Menu_System.*;
import static sully.vc.v1_menu.Menu_Equip.*;
import static sully.vc.v1_menu.Menu_Choice.*;
import static sully.vc.v1_menu.Menu_Item.*;
import static sully.vc.v1_rpg.V1_Textbox.*;
import static sully.vc.simpletype_rpg.Party.*;
import static sully.vc.simpletype_rpg.Data.*;
import static sully.vc.simpletype_rpg.Inventory.*;
import domain.VImage;

public class Menu_Shop {
	// menu_shop.vc for Sully www.verge-rpg.com
	// Zip 05/09/2004
	
	//        ----------------
	//       Shop Menu
	//        ----------------
	
	String _shop_too_poor_msg = "You don't have enough money for that.";
	String _shop_purchase_msg = "Thank you!";
	String _shop_too_many_msg = "Sorry, you own too many of that already.";
	
	void SetShopTooPoorMag( String s )
	{
		_shop_too_poor_msg = s;
	}
	
	void SetShopPurchaseMag( String s )
	{
		_shop_purchase_msg = s;
	}
	
	void SetShopTooManyMag( String s )
	{
		_shop_too_many_msg = s;
	}
	
	boolean _shop_can_sell_supplies;
	boolean _shop_can_sell_equipment;
	
	// Set to true if you want the next shop to allow supplies to be sold, false if not.
	//
	void SetSellSupplyShop( boolean mode )
	{
		_shop_can_sell_supplies = mode;
	}
	
	
	// Set to true if you want the next shop to allow supplies to be sold, false if not.
	//
	void SetSellEquipmentShop( boolean mode )
	{
		_shop_can_sell_equipment = mode;
	}
	
	
	void MenuShop(String items)
	{
		int shop_i = 0;
		int shop_count = 0;
		String item_list = "";
		String item_name = gettoken(items, "|, ", 0);
		while (len(item_name)>0)
		{
			for (shop_i = 0; shop_i < MAX_ITEMS; shop_i++) // Loops through items
			{
				if (master_items[shop_i].name.equals(item_name)) // Checks value against item names
				{
					item_list = item_list + str(shop_i) + "&";
					shop_i = MAX_ITEMS + 1; // Breaks out of loop
				}						
			}
			if (shop_i == MAX_ITEMS) // If no match was found
			{
				ErrorLoadType("MenuShop()", item_name); // Throws error
			}
			shop_count++;
			item_name = gettoken(items, "|, ", shop_count);
		}
		MenuShopI(item_list);
	}
	
	void MenuShopI(String items)
	{
		menu_done = 0;
		menu_option = 0;
		menu_item = 0;
		menu_cast = 0;
		menu_idx = 1;
		menu_number = 1;
		menu_sub = tokencount(items, "|, ");
		save_display[0].text = items;
		
		UpdateShopPretend();
	
		while(!menu_done)
		{
			MenuBackGroundDraw(); //draw universal things
			
			if (menu_idx == 0) MenuDrawShopMain();
			else if (menu_idx == 1) MenuDrawShopBuy();
			else if (menu_idx == 2) MenuDrawShopSellSupply();
			else if (menu_idx == 3) MenuDrawShopSellEquip();
			
			showpage();
			
			if (menu_idx == 0) MenuControlShopMain();
			else if (menu_idx == 1) MenuControlShopBuy();
			else if (menu_idx == 2) MenuControlShopSellSupply();
			else if (menu_idx == 3) MenuControlShopSellEquip();
			else if (MenuCancel())
			{
				MenuHappyBeep();
				menu_done = 1;
				menu_start = 0;
			}
		}
	}
	
	
	void MenuControlShopMain()
	{
		int i;
		
		Menu1ArrowSetSounds( "MenuHappyBeep" );
		menu_option = MenuControlArrows(menu_option, 4);
		if (MenuConfirm())
		{
			switch (menu_option)
			{
				case 0:	if(menu_sub > 0)
						{
							menu_idx = 1;
							UpdateShopPretend();
							MenuHappyBeep();
						}
						else MenuAngryBuzz();
					break;
	
				case 1:	if( SupplyCount() > 0 && _shop_can_sell_supplies )
						{
							menu_idx = 2;
							MenuHappyBeep();
						}
						else MenuAngryBuzz();
					break;
	
				case 2:	if( EquipmentCount() > 0 && _shop_can_sell_equipment )
						{
							menu_idx = 3;
							MenuHappyBeep();
						}
						else MenuAngryBuzz();
					break;
				case 3:	menu_done = 1;
					break;
	
			}
		}
		if (MenuCancel())
		{
			Menu1ArrowSetSounds( "" );
			MenuHappyBeep();
			menu_done = 1;
			menu_start = 0;
		}
	}
	
	int _shop_pretend_equip;
	void UpdateShopPretend()
	{
		int item_idx = val(gettoken(save_display[0].text, "&", menu_item));
			
		if(IsSupplyItem(item_idx))
		{
			_shop_pretend_equip = 0;
		}
		else 
		{
			if( CanEquipI(party[menu_cast], item_idx) )
			{
				_shop_pretend_equip = 1;
			}
			else
			{
				_shop_pretend_equip = 0;
			}
		}
	}
	
	
	void MenuControlShopBuy()
	{
		Menu2ArrowSetSounds( "MenuHappyBeep","MenuPageTurn" );
		int movey = MenuControlTwoArrows("menu_item", menu_sub, "menu_number", MAX_INV_SLOT);
		int q, item_idx;
		int _slot, choice;
		
		if( !menu_number )
		{
			menu_number = MAX_INV_SLOT;
		}
	
		if ((movey & 1)!=0) //what happens when up/down is done.
		{
			if (menu_start  + 6 < menu_item)
			{
				menu_start = menu_item - 6;
			}
			else if (menu_start > menu_item && menu_item >= 0) 
			{
				menu_start = menu_item;	
			}
	
			UpdateShopPretend();
				
		} 
		if (movey & 2) //what happens when left/right is done.
		{
			//nothing!
		}
		
		if (MenuConfirm())
		{
			item_idx = val(gettoken(save_display[0].text, "&", menu_item));
			q = ItemCount(master_items[item_idx].name);
			
			if( q==MAX_INV_SLOT )
			{
				MenuAngryBuzz();
				
				MenuMinibox( _shop_too_many_msg, "MenuDrawShopMain");
			}
			else if(q+menu_number > MAX_INV_SLOT)
			{
				MenuAngryBuzz();
				
				MenuMinibox( _shop_too_many_msg, "MenuDrawShopMain");
				
				if( q+menu_number >= MAX_INV_SLOT )
				{
					menu_number = MAX_INV_SLOT-q;
				}
	
				if( !menu_number )
				{
					menu_number = 1;
				}			
			}
			else if( IsSupplyItem(item_idx) )
			{
				if( money < (master_items[item_idx].price*menu_number) )
				{
					MenuAngryBuzz();
					MenuMinibox(_shop_too_poor_msg, "MenuDrawShopMain");
				}
				else
				{
					MenuHappyBeep();
					choice = MenuMiniChoicebox("That'll be "+str(master_items[item_idx].price*menu_number)+" "+moneyname+".","Okay!|No thanks." "MenuDrawShopMain");
					
					if( !GetMenuChoiceAnswer() && choice ) 
					{
						MenuPurchase();
						MenuMinibox( _shop_purchase_msg, "MenuDrawShopMain");
						money -= master_items[item_idx].price*menu_number;
						GiveItemI( item_idx, menu_number );
						
						q = ItemCount(master_items[item_idx].name);
						
						if( q+menu_number >= MAX_INV_SLOT )
						{
							menu_number = MAX_INV_SLOT-q;
						}
						
						if( !menu_number )
						{
							menu_number = 1;
						}
					}
				}
			}
			else if( IsEquipmentItem(item_idx) )
			{
				if( money < (master_items[item_idx].price*menu_number) )
				{
					MenuAngryBuzz();
					MenuMinibox(_shop_too_poor_msg, "MenuDrawShopMain");
				}
				else
				{
					MenuHappyBeep();
					choice = MenuMiniChoicebox("That'll be "+str(master_items[item_idx].price*menu_number)+" "+moneyname+".","Okay!|No thanks." "MenuDrawShopMain");
					
					if( !GetMenuChoiceAnswer() && choice )
					{
						MenuPurchase();
						MenuMinibox( _shop_purchase_msg, "MenuDrawShopMain");
						money -= master_items[item_idx].price*menu_number;
						GiveItemI( item_idx, menu_number );
						
						q = ItemCount(master_items[item_idx].name);
						
						if( menu_number == 1 )
						{
							if( CanEquipI(party[menu_cast], item_idx) !=0 )
							{
								MenuHappyBeep();
								choice = MenuMiniChoicebox("Would you like "+master_cast[party[menu_cast]].name+" to equip this?","Please.|Nope." "MenuDrawShopMain");
								
								if( !GetMenuChoiceAnswer() && choice )
								{
									MenuForceEquip();
									
									_slot = master_items[item_idx].equ_slot;
	
									if( _slot == SLOT_ACC1 )
										if( master_cast[party[menu_cast]].equipment[SLOT_ACC1] > 0 ) 
											if( master_cast[party[menu_cast]].equipment[SLOT_ACC2] < 0 )
												_slot = SLOT_ACC2;		
								
									EquipItemI( party[menu_cast], item_idx, _slot );
								}						
							}
						}
						else
						{					
							if( q+menu_number >= MAX_INV_SLOT )
							{
								menu_number = MAX_INV_SLOT-q;
							}
	
							if( !menu_number )
							{
								menu_number = 1;
							}
						}					
					}
				}
			}
			else
			{
				error( "MenuControlShopBuy(): These shops cannot presently sell Key Items.  Sorry. -Grue" );
			}
		}
		
		if (MenuCancel())
		{
			Menu2ArrowSetSounds( "","" );
			MenuHappyBeep();
			menu_idx = 0;
			menu_item = 0;
			menu_start = 0;
		}
		
		if (b4)
		{
			unpress(4);
			menu_cast = (menu_cast + 1) % PartySize();
			UpdateShopPretend();
		}
		
	}
	
	void MenuControlShopSellSupply()
	{
		int answer, item_idx, cleanup, choice;
		
		Menu2ArrowSetSounds( "MenuHappyBeep","MenuPageTurn" );
		int movey = MenuControlTwoArrows("menu_item", _supply_count, "menu_number", 99);
		if (movey & 1)
		{
			if (menu_start  + 10 < menu_item) menu_start = menu_item - 10;
			else if (menu_start > menu_item && menu_item >= 0) menu_start = menu_item;		
			menu_number = 1;
		}
		if (movey & 2)
		{
			
		}
		if (MenuConfirm())
		{
			item_idx = supply_inventory[menu_item].item_ref;
			answer = MenuSellingbox(item_idx, supply_inventory[menu_item].quant, "MenuDrawShopMain");
			
			if( answer > 0)
			{
				choice = MenuMiniChoicebox(	"Sell "+str(answer)+" "+master_items[item_idx].name+" for "+
									str((master_items[item_idx].price/2)*answer)+" "+moneyname+"?",
									"Okay!|No thanks.",
									"MenuDrawShopMain");
	
				if( !GetMenuChoiceAnswer() && choice ) 
				{
					MenuPurchase();
					
					if( answer == supply_inventory[menu_item].quant )
					{
						cleanup = 1;
					}
					
					money += (master_items[item_idx].price/2)*answer;
								
					TakeItemI( item_idx, answer );
					
					if( SupplyCount()==0 )
					{
						Menu2ArrowSetSounds( "","" );
						MenuHappyBeep();
						menu_idx = 0;
						menu_item = 0;
						menu_start = 0;
						return;
					}
					
					//terrible hack, I'm tired, -Grue
					if( menu_item >= EquipmentCount() || cleanup!=0 )
					{
						menu_item = 0;
						menu_number = 1;
						menu_start = 0;
					}
				}
			}
		}
		if (MenuCancel())
		{
			Menu2ArrowSetSounds( "","" );
			MenuHappyBeep();
			menu_idx = 0;
			menu_item = 0;
			menu_start = 0;
		}
	}
	
	void MenuControlShopSellEquip()
	{
		int item_idx,answer,cleanup, choice;
		
		Menu2ArrowSetSounds( "MenuHappyBeep","MenuPageTurn" );
		int movey = MenuControlTwoArrows("menu_item", _equip_count, "menu_number", 99);
		if (movey & 1)
		{
			if (menu_start  + 10 < menu_item) menu_start = menu_item - 10;
			else if (menu_start > menu_item && menu_item >= 0) menu_start = menu_item;		
			//menu_number = 1;
		}
		if (movey & 2)
		{
			
		}
		if (MenuConfirm())
		{
			item_idx = equipment_inventory[menu_item].item_ref;
			answer = MenuSellingbox(item_idx, equipment_inventory[menu_item].quant, "MenuDrawShopMain");
			
			if( answer > 0)
			{
				choice = MenuMiniChoicebox(	"Sell "+str(answer)+" "+master_items[item_idx].name+" for "+
									str((master_items[item_idx].price/2)*answer)+" "+moneyname+"?",
									"Okay!|No thanks.",
									"MenuDrawShopMain");
	
				if( !GetMenuChoiceAnswer() && choice ) 
				{
					MenuPurchase();
					
					if( answer == equipment_inventory[menu_item].quant )
					{
						cleanup = 1;
					}
					
					money += (master_items[item_idx].price/2)*answer;
					
					TakeItemI( item_idx, answer );
					
					if( !EquipmentCount() )
					{
						Menu2ArrowSetSounds( "","" );
						MenuHappyBeep();
						menu_idx = 0;
						menu_item = 0;
						menu_number = 1;
						menu_start = 0;
						return;
					}
					
					//terrible hack, I'm tired, -Grue
					if( menu_item >= EquipmentCount() || cleanup )
					{
						menu_item = 0;
						menu_number = 1;
						menu_start = 0;
					}
				}
			}
		}
		if (MenuCancel())
		{
			Menu2ArrowSetSounds( "","" );
			MenuHappyBeep();
			menu_idx = 0;
			menu_item = 0;
			menu_number = 1;
			menu_start = 0;
		}
	}
	
	void MenuDrawShopMain()
	{
		int summat = 0;
		if (menu_idx == 0) summat = 1;
		MenuBlitShopLeft(summat, menu_option);
		
		if (menu_option == 0)
		{
			MenuDrawBackground(90, 10, 310, 130, 0);
			MenuBlitShopBuy();
		}
		if (menu_option == 1)
		{
			MenuDrawBackground(90, 10, 310, 230, 0);
			MenuBlitShopSellSupply();
		}
		if (menu_option == 2)
		{
			MenuDrawBackground(90, 10, 310, 230, 0);
			MenuBlitShopSellEquip();
		}
	}
	
	void MenuDrawShopBuy()
	{
		boolean summat = false;
		if (menu_idx == 1) summat = true;
		MenuBlitShopLeft(0, menu_option);
		MenuDrawBackground(90, 10, 310, 130, summat);
		MenuBlitShopBuy();
		
	}
	
	void MenuDrawShopSellSupply()
	{
		boolean summat = false;
		if (menu_idx == 2) summat = true;
		MenuBlitShopLeft(0, menu_option);
		MenuDrawBackground(90, 10, 310, 230, summat);
		MenuBlitShopSellSupply();
		
	}
	
	void MenuDrawShopSellEquip()
	{
		boolean summat = false;
		if (menu_idx == 3) summat = true;
		MenuBlitShopLeft(0, menu_option);
		MenuDrawBackground(90, 10, 310, 230, summat);
		MenuBlitShopSellEquip();
		
	}
	
	
	void MenuBlitShopSellSupply()
	{
		int i, use, longest_x;
		MenuDrawSubWindow(100, 20, 300, 170, menu_item, menu_fonth + 2, _supply_count, menu_start, 3);
	
		for (i = 0; i < SupplyCount(); i++)
		{
			if( longest_x < TextWidth(menu_font[0], master_items[supply_inventory[i].item_ref].name) )
				longest_x = TextWidth(menu_font[0], master_items[supply_inventory[i].item_ref].name);
		}
	
		if (_supply_count == 0)
		{
			MenuPrintDescFull(115, 228, menu_font[0], "No supplies.", 180);
		}
		else MenuPrintDescFull(115, 228, menu_font[0], master_items[supply_inventory[menu_item].item_ref].desc, 180);
		
		for (i = menu_start; i < _supply_count; i++)
		{
			printstring(135, 23 + ((menu_fonth + 2) * (i - menu_start)), screen, menu_font[0], master_items[supply_inventory[i].item_ref].name);
			printstring(135+longest_x, 23 + ((menu_fonth + 2) * (i - menu_start)), screen, menu_font[2], "_x"+str(supply_inventory[i].quant));
			
			printright(285, 23 + ((menu_fonth + 2) * (i - menu_start)), screen, menu_font[0], str(master_items[supply_inventory[i].item_ref].price/2));
			
			use = icon_get(master_items[supply_inventory[i].item_ref].icon);
			if (i == menu_item) tblit(115, 21 + ((menu_fonth + 2) * (i - menu_start)), use, screen);
			else tscaleblit(115, 25 + ((menu_fonth + 2) * (i - menu_start)), 8, 8, use, screen);
			FreeImage(use);
			if (menu_start + 10 <= i) i = _supply_count + 1;
		}
	
	}
	
	void MenuBlitShopSellEquip()
	{
		int i, use, longest_x;
		MenuDrawSubWindow(100, 20, 300, 170, menu_item, menu_fonth + 2, _equip_count, menu_start, 3);
	
		for (i = 0; i < EquipmentCount(); i++)
		{		
			if( longest_x < TextWidth(menu_font[0], master_items[equipment_inventory[i].item_ref].name) )
				longest_x = TextWidth(menu_font[0], master_items[equipment_inventory[i].item_ref].name);
		}
	
		if (_equip_count == 0)
		{
			MenuPrintDescFull(115, 228, menu_font[0], "No equipment.", 180);
		}
		else MenuPrintDescFull(115, 228, menu_font[0], master_items[equipment_inventory[menu_item].item_ref].desc, 180);
		
		for (i = menu_start; i < _equip_count; i++)
		{
			printstring(135, 23 + ((menu_fonth + 2) * (i - menu_start)), screen, menu_font[0], master_items[equipment_inventory[i].item_ref].name);
			printstring(135+longest_x, 23 + ((menu_fonth + 2) * (i - menu_start)), screen, menu_font[2], "_x"+str(equipment_inventory[i].quant));
			
			printright(285, 23 + ((menu_fonth + 2) * (i - menu_start)), screen, menu_font[0], str(master_items[equipment_inventory[i].item_ref].price/2));
			
			use = icon_get(master_items[equipment_inventory[i].item_ref].icon);
			if (i == menu_item) tblit(115, 21 + ((menu_fonth + 2) * (i - menu_start)), use, screen);
			else tscaleblit(115, 25 + ((menu_fonth + 2) * (i - menu_start)), 8, 8, use, screen);
			FreeImage(use);
			if (menu_start + 10 <= i) i = _equip_count + 1;
		}
	
	}
	
	void MenuBlitShopBuy()
	{
		int i, equip, item_idx, longest_x;
		VImage equipImage;
		MenuDrawSubWindow(100, 20, 300, 120, menu_item, menu_fonth + 2, menu_sub, menu_start, 3);
		
		for (i = menu_start; i < menu_sub; i++)
		{
			item_idx = val(gettoken(save_display[0].text, "&", i));
			
			if( longest_x < TextWidth(menu_font[equip],master_items[item_idx].name) )
				longest_x = TextWidth(menu_font[equip],master_items[item_idx].name);
		}
		
	
		for (i = menu_start; i < menu_sub; i++)
		{
			item_idx = val(gettoken(save_display[0].text, "&", i));
			if ((master_items[item_idx].price*menu_number) <= money) equip = 0;
			else equip = 1;
			
	//		if( ItemCount(master_items[menu_item].name) == MAX_INV_SLOT )
	//		{
	//			equip = 1;
	//		}
			
			printstring(135, 23 + ((menu_fonth + 2) * (i - menu_start)), screen, menu_font[equip], master_items[item_idx].name);
			printstring(135+longest_x, 23 + ((menu_fonth + 2) * (i - menu_start)), screen, menu_font[equip], " x"+str(menu_number));
			
			printright(285, 23 + ((menu_fonth + 2) * (i - menu_start)), screen, menu_font[equip], str(master_items[item_idx].price * menu_number));
			equipImage = icon_get(master_items[item_idx].icon);
			if (i == menu_item) tblit(115, 21 + ((menu_fonth + 2) * (i - menu_start)), equipImage, screen);
			else tscaleblit(115, 25 + ((menu_fonth + 2) * (i - menu_start)), 8, 8, equipImage, screen);
			//FreeImage(equip);
			if (menu_start + 6 <= i) i = menu_sub + 1;
		}
		//if (i == equ_count) printstring(55, 133 + ((menu_fonth + 2) * (i - menu_start)), screen, menu_font[0], "(none)"); // if near end
	
	
		MenuDrawBackground(90, 140, 310, 230, 0);
		
		MenuBlitParty(105, 146, menu_cast, val(gettoken(save_display[0].text, "&", menu_item)));
		
		int _slot;
		if( _shop_pretend_equip )
		{
			item_idx = val(gettoken(save_display[0].text, "&", menu_item));
			
			if( IsEquipmentItem(item_idx) )
			{
			
				if( CanEquipI(party[menu_cast], item_idx) )
				{
					_slot = master_items[item_idx].equ_slot;
	
					if( _slot == SLOT_ACC1 )
						if( master_cast[party[menu_cast]].equipment[SLOT_ACC1] > 0 ) 
							if( master_cast[party[menu_cast]].equipment[SLOT_ACC2] < 0 )
								_slot = SLOT_ACC2;			
	
					for (i = 0; i < MAX_STATS; i++)
					{
						equip = GetMyStatPretendEquipI( party[menu_cast], item_idx, _slot, i );
						MenuPrintStat(105, 146, i, MenuEquipFont(equip, getStat(party[menu_cast], i)), equip);
						//MenuPrintStat(MENU_CAST_X, MENU_CAST_Y, i, MenuEquipFont(equip, getStat(party[menu_cast], i)), equip);
					}
				}
			}
		}
	}
	
	
	void MenuBlitParty(int x, int y, int member, int item_idx)
	{
		int i;
		
		if( menu_idx == 1 || menu_idx == 1 )
		{
		
			if (IsEquipmentItem(item_idx))
			{
				for (i = 0; i < curpartysize; i++)
				{
					if( i==member )
					{
						RectFill( x+(i*24)-1, y-1, x+(i*24)+17, y+33, menu_colour[MENU_COLOR_ACTIVE], screen );
					}
	
					if( CanEquipI(party[i],item_idx) )
					{
						BlitEntityFrame(x + (i * 24), y + 16, master_cast[party[i]].entity, GetFrameWalk(), screen);
					}
					else
					{
						BlitEntityFrame(x + (i * 24), y + 16, master_cast[party[i]].entity, GetFrameSad(), screen);
					}
				}
			}
			else
			{
				for (i = 0; i < curpartysize; i++)
				{
					if( i==member )
					{
						RectFill( x+(i*24)-1, y-1, x+(i*24)+17, y+33, menu_colour[MENU_COLOR_ACTIVE], screen );
					}
	
					BlitEntityFrame(x + (i * 24), y + 16, master_cast[party[i]].entity, GetFrameSad(), screen);
				}
			}
		}
		else
		{
			for (i = 0; i < curpartysize; i++)
			{
				if( i==member )
				{
					RectFill( x+(i*24)-1, y-1, x+(i*24)+17, y+33, menu_colour[MENU_COLOR_ACTIVE], screen );
				}
	
				BlitEntityFrame(x + (i * 24), y + 16, master_cast[party[i]].entity, GetFrameSad(), screen);
			}
		}
		
		
		
		printstring(x + 115, y, screen, menu_font[0], master_cast[party[member]].name);
		//printstring(x + 35, y + 10, screen, menu_font[0], master_classes[master_cast[party[member]].class_ref].name);
		//printstring(x + 115, y, screen, menu_font[0], "Level: ");
		//printright(x + 185, y, screen, menu_font[0], str(master_cast[party[member]].level));
		
		if( _shop_pretend_equip )
		{	
			printstring(x + 115, y + 10, screen, menu_font[0], "HP:");
			printright(x + 161, y + 10, screen, menu_font[0], str(master_cast[party[member]].cur_hp)+"/" );
			printstring(x + 115, y + 20, screen, menu_font[0], "MP:");
			printright(x + 161, y + 20, screen, menu_font[0], str(master_cast[party[member]].cur_mp)+"/" );
		}
		else
		{
			printstring(x + 115, y + 10, screen, menu_font[0], "Cannot equip.");
			
			if( menu_idx == 1 )
			{
				printstring(x-5, y + 50, screen, menu_font[0], "(Press [b4] to change party member)");
			}
		}
	}
	
	int _omg_shop_left_prev;
	void MenuBlitShopLeft(int active, int selected)
	{
			
		_omg_shop_left_prev = selected;
		
		MenuDrawBackground(10, 70, 80, 230, active);
		
		if( selected != 3 )
			printstring(16, selected * 30 + 85, screen, menu_font[0], ">");
		else
			printstring(16, (selected+1) * 30 + 85, screen, menu_font[0], ">");
			
		printstring(22, 85, screen, menu_font[0], "BUY");
		printright(68, 95, screen, menu_font[0], "ITEMS");
		
		
	
		if( _shop_can_sell_supplies ) 
	
		{
	
			printstring(22, 115, screen, menu_font[0], "SELL");
			printright(68, 125, screen, menu_font[0], "SUPPLY");
		}
		else
		{
			printstring(22, 115, screen, menu_font[1], "SELL");
			printright(68, 125, screen, menu_font[1], "SUPPLY");
		}
		
	
		if( _shop_can_sell_equipment ) 
		{
			printstring(22, 145, screen, menu_font[0], "SELL");
			printright(68, 155, screen, menu_font[0], "EQUIP");
		}
		else
		{
			printstring(22, 145, screen, menu_font[1], "SELL");
			printright(68, 155, screen, menu_font[1], "EQUIP");		
		}
		
		printstring(22, 205, screen, menu_font[0], "EXIT");
		MenuBlitShopTop();
	}
	
	void MenuBlitShopTop()
	{
		MenuDrawBackground(10, 10, 80, 60, 0);
		printright(70, 20, screen, menu_font[0], "A Shop");
		printstring(20, 35, screen, menu_font[0], moneyname + ":");
		printright(70, 45, screen, menu_font[0], str(money));
	}
	
	
	// A small notification box
	int MenuSellingbox(int item_idx, int max_quant, String draw_func)
	{
		int wid = TextWidth( menu_font[0], "selling: " + master_items[item_idx].name );
	
		if( wid < TextWidth(menu_font[0], "How many would you like to sell?") )
			wid = TextWidth( menu_font[0], "How many would you like to sell?" );
		
		if( wid < TextWidth(menu_font[0], "Quantity: " + str( MAX_INV_SLOT )) )
			wid = TextWidth( menu_font[0], "Quantity: " + str(MAX_INV_SLOT) );
	
		if( wid < TextWidth(menu_font[0], "Total "+moneyname+": "+str(9999999)) )
			wid = TextWidth( menu_font[0], "Total "+moneyname+": "+str(9999999) );
		
		int high = (FontHeight( menu_font[0] ) + 1)*6;
		
		int border = 5;
		
		int x1 = ((ImageWidth(screen)-wid)/2);
		int y1 = (ImageHeight(screen)/2)-(high/2);
		int x2 = ((ImageWidth(screen)-wid)/2)+wid+(high/2);
		int y2 = (ImageHeight(screen)/2)+(high/2);
		
		int quant = 1;
		int done;
		int mini_hold = menu_idx;
		//menu_idx = 1000000;
		while(!done)
		{				
			MenuBackGroundDraw(); //draw universal things
			CallFunction(draw_func);
			
			MenuDrawBackground(	x1,y1, x2,y2, 1);
			
			quant = MenuControlArrows(quant, max_quant);
			
			if( quant <= 0 ) {
				quant = max_quant;
			}
			
			printstring(x1+border, y1+border, screen, menu_font[0], "selling: " + master_items[item_idx].name);
			printstring(x1+border, y1+border+(FontHeight(menu_font[0])+1), screen, menu_font[0], "How many would you like to sell?");
			printstring(x1+border, y1+((FontHeight(menu_font[0])+1)*3), screen, menu_font[0], "Quantity: " + str(quant));
			printstring(x1+border, y1-border+((FontHeight(menu_font[0])+1)*5), screen, menu_font[0],  "Total "+moneyname+": "+str((master_items[item_idx].price/2)*quant));
			
			showpage();
			
			done = MenuConfirm();
			
			if( !done )
				done = MenuCancel();
		}
		
		if( done > 0 )
			return quant;
		else
			return done;
	}
	
}	

