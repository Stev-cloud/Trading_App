package com.adamki11s.itemexchange.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.adamki11s.itemexchange.exchange.ExchangeActions;
import com.adamki11s.itemexchange.exchange.PlayerProfile;
import com.adamki11s.itemexchange.exchange.ProfileManager;
import com.adamki11s.itemexchange.gui.ExchangeGUI;

public class ExchangeCommands implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(label.equalsIgnoreCase("itemexchange") || label.equalsIgnoreCase("iex")){
			if(sender instanceof Player){
				Player p = (Player) sender;
				
				//iex sell <cpu>
				if(args.length == 2 && args[0].equalsIgnoreCase("sell")){
					int cpu = 0;
					try{
						cpu = Integer.parseInt(args[1]);
					} catch (NumberFormatException e){
						p.sendMessage(ChatColor.RED + "Cost per unit (cpu) must be an integer!");
						return true;
					}
					ExchangeActions.addSellEntry(p, cpu);
					return true;
				} else if(args.length == 1 && args[0].equalsIgnoreCase("buy")){
					ExchangeGUI.openMenu(p, 1);
					return true;
				}
				
			}
			return true;
		}
		return false;
	}

}
