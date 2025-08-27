package com.adamki11s.itemexchange;

import java.util.List;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.adamki11s.itemexchange.commands.ExchangeCommands;
import com.adamki11s.itemexchange.database.Database;
import com.adamki11s.itemexchange.exchange.BuyEntry;
import com.adamki11s.itemexchange.exchange.Exchange;
import com.adamki11s.itemexchange.exchange.ExchangePoll;
import com.adamki11s.itemexchange.exchange.ProfileManager;
import com.adamki11s.itemexchange.exchange.SellEntry;
import com.adamki11s.itemexchange.gui.ExchangeGUI;
import com.adamki11s.itemexchange.sql.SQLQueries;

public class ItemExchange extends JavaPlugin {

	private static Plugin p;
	private static Logger l;

	public static Economy economy;

	@Override
	public void onEnable() {
		p = this;
		l = getLogger();
		Config.init();

		List<SellEntry> sellEntries = Database.loadSellEntries();
		List<BuyEntry> buyEntries = Database.loadBuyEntries();
		Exchange.initExchange(sellEntries, buyEntries);
		ProfileManager.initProfiles(sellEntries, buyEntries);
		
		setupEconomy();
		
		getCommand("itemexchange").setExecutor(new ExchangeCommands());
		
		//init listener
		new ExchangeGUI(this);
		ExchangeGUI.initialise();
		
		Bukkit.getScheduler().runTaskTimerAsynchronously(this, new ExchangePoll(), 0, 20);
		Bukkit.getScheduler().runTaskTimerAsynchronously(this, new SQLQueries(), 0, 20);
	}
	
	@Override
	public void onDisable(){
		if(Database.sql != null){
			Database.sql.closeConnection();
		}
	}

	private boolean setupEconomy() {
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(
				net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
			economy = economyProvider.getProvider();
		}

		return (economy != null);
	}

	public static Plugin getPlugin() {
		return p;
	}

	public static Logger getLog() {
		return l;
	}

}
