package com.adamki11s.itemexchange.exchange;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.adamki11s.itemexchange.database.Database;

public class ExchangeActions {
	
	public static void addSellEntry(Player p, int cpu){
		PlayerProfile pp = ProfileManager.getPlayerProfile(p);
		if(pp.hasMaxEntries()){
			p.sendMessage(ChatColor.RED + "You do not have any free slots!");
		} else {
			ItemStack is = p.getItemInHand();
			if(is == null || is.getType().equals(Material.AIR)){
				p.sendMessage(ChatColor.RED + "You are not holding an item!");
			} else {
				//item is not null
				if(is.hasItemMeta() && is.getItemMeta().hasEnchants()){
					p.sendMessage(ChatColor.RED + "Enchanted items cannot be sold!");
				} else {
					//item is not enchanted
					SellEntry e = new SellEntry(p.getUniqueId().toString(), is.getType(), is.getData().getData(), is.getAmount(), cpu, 0, System.currentTimeMillis());
					//add to player profile
					pp.addSellEntry(e);
					//remove itemstack
					p.setItemInHand(null);
					//add to sql database, once added to sql the data will be pushed to the live list
					Database.addSellEntryAsync(e, p);
				}
			}
		}
	}
	
	public static void addBuyEntry(Player p, Material m, int itemdata, int quantity, int maxCPU){
		PlayerProfile pp = ProfileManager.getPlayerProfile(p);
		if(pp.hasMaxEntries()){
			p.sendMessage(ChatColor.RED + "You do not have any free slots!");
		} else {
			BuyEntry e = new BuyEntry(p.getUniqueId().toString(), m, itemdata, quantity, maxCPU, 0, System.currentTimeMillis());
			pp.addBuyEntry(e);
			Database.addBuyEntryAsync(e, p);
		}
	}

}
