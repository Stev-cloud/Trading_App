package com.adamki11s.itemexchange.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.adamki11s.itemexchange.Config;
import com.adamki11s.itemexchange.ItemExchange;
import com.adamki11s.itemexchange.exchange.BuyEntry;
import com.adamki11s.itemexchange.exchange.Exchange;
import com.adamki11s.itemexchange.exchange.ExchangePoll;
import com.adamki11s.itemexchange.exchange.SellEntry;
import com.adamki11s.itemexchange.sql.SyncSQL;

public class Database {

	public static SyncSQL sql;

	public static String ITEM_TABLE = "iex_items", OFFER_TABLE = "iex_offers", HISTORY_TABLE = "iex_history", DATA_TABLE = "iex_data";

	public static void initDatabase(boolean useSQL, String... data) {
		// [host, database, username, password]
		sql = useSQL ? new SyncSQL(data[0], data[1], data[2], data[3]) : new SyncSQL(Config.SQLITE);

		ItemExchange.getLog().info(useSQL ? "Using MySQL database storage." : "Using SQLite database storage.");

		if (sql.initialise()) {
			ItemExchange.getLog().info("SQL connection successful.");

			try {
				String create;
				if (!sql.doesTableExist(ITEM_TABLE)) {
					/*
					 * VARCHAR(36) for storing UUID cpu = cost per unit sold
					 * integer represents how many of the item have been sold
					 * time is the unix timestamp of when the item was listed
					 */
					create = "CREATE TABLE "
							+ ITEM_TABLE
							+ "(id INT NOT NULL PRIMARY KEY AUTO_INCREMENT, seller VARCHAR(36), item VARCHAR(60), itemdata INTEGER, quantity INTEGER, cpu INTEGER, sold INTEGER, time LONG);";
					sql.standardQuery(create);
					ItemExchange.getLog().info("Item table created.");
				}
				
				if (!sql.doesTableExist(OFFER_TABLE)) {
					create = "CREATE TABLE "
							+ OFFER_TABLE
							+ "(id INT NOT NULL PRIMARY KEY AUTO_INCREMENT, buyer VARCHAR(36), item VARCHAR(60), itemdata INTEGER, quantity INTEGER, maxcpu INTEGER, bought INTEGER, time LONG);";
					sql.standardQuery(create);
					ItemExchange.getLog().info("Offer table created.");
				}

				if (!sql.doesTableExist(HISTORY_TABLE)) {
					create = "CREATE TABLE "
							+ HISTORY_TABLE
							+ "(id INT NOT NULL PRIMARY KEY AUTO_INCREMENT, seller VARCHAR(36), buyer VARCHAR(36), item VARCHAR(60), itemdata INTEGER, quantity INTEGER, cpu INTEGER, time LONG);";
					sql.standardQuery(create);
					ItemExchange.getLog().info("History table created.");
				}

				if (!sql.doesTableExist(DATA_TABLE)) {
					/*
					 * used to keep track of successful trades and record the
					 * average price point cumulative is the total cost of all
					 * sold items, and quantity is used to calculate an average
					 * price point
					 */
					create = "CREATE TABLE " + DATA_TABLE
							+ "(id INT NOT NULL PRIMARY KEY, item VARCHAR(60), itemdata INTEGER, cumulative LONG, quantity LONG);";
					sql.standardQuery(create);
					ItemExchange.getLog().info("Data table created.");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

		} else {
			ItemExchange.getLog().severe("SQL connection failed!");
		}
	}

	public static List<SellEntry> loadSellEntries() {
		ResultSet s;
		List<SellEntry> sellEntries = new ArrayList<SellEntry>();
		try {
			s = sql.sqlQuery("SELECT * FROM " + ITEM_TABLE + ";");
			while (s.next()) {
				SellEntry e = new SellEntry(s.getString("seller"), Material.valueOf(s.getString("item")), s.getInt("itemdata"), s.getInt("quantity"), s.getInt("cpu"),
						s.getInt("sold"), s.getLong("time"));
				sellEntries.add(e);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return sellEntries;
	}
	
	/*
	 * create = "CREATE TABLE "
							+ ITEM_TABLE
							+ "(id INT NOT NULL PRIMARY KEY, buyer VARCHAR(36), item VARCHAR(60), quantity INTEGER, maxcpu INTEGER, bought INTEGER, time LONG);";
					sql.standardQuery(create);
					ItemExchange.getLog().info("Offer table created.");
	 */
	
	public static List<BuyEntry> loadBuyEntries() {
		ResultSet s;
		List<BuyEntry> buyEntries = new ArrayList<BuyEntry>();
		try {
			s = sql.sqlQuery("SELECT * FROM " + OFFER_TABLE + ";");
			while (s.next()) {
				BuyEntry e = new BuyEntry(s.getString("buyer"), Material.valueOf(s.getString("item")), s.getInt("itemdata"), s.getInt("quantity"), s.getInt("maxcpu"),
						s.getInt("bought"), s.getLong("time"));
				buyEntries.add(e);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return buyEntries;
	}
	
	public static void addBuyEntryAsync(final BuyEntry e, final Player callback) {
		Bukkit.getScheduler().runTaskAsynchronously(ItemExchange.getPlugin(), new Runnable() {
			
			@Override
			public void run() {
				try {
					sql.standardQuery("INSERT INTO "
							+ OFFER_TABLE
							+ "(buyer, item, itemdata, quantity, maxcpu, bought, time) VALUES ("
							+ String.format("'%s', '%s', %d, %d, %d, %d", e.getBuyerUUID().toString(), e.getItem().toString(), e.getItemData(), e.getQuantity(),
									e.getMaxCPU(), e.getQuantityBought(), e.getTimeSubmitted()) + ");");
					Exchange.addBuyEntryAsync(e);
					ExchangePoll.markChanged();
					callback.sendMessage(ChatColor.GREEN + "Your offer was successfully listed on the exchange.");
				} catch (SQLException e1) {
					e1.printStackTrace();
					callback.sendMessage(ChatColor.RED + "There was an error syncing your offer with the database.");
				}
			}
		});
	}

	public static void addSellEntryAsync(final SellEntry e, final Player callback) {
		Bukkit.getScheduler().runTaskAsynchronously(ItemExchange.getPlugin(), new Runnable() {
			
			@Override
			public void run() {
				try {
					sql.standardQuery("INSERT INTO "
							+ ITEM_TABLE
							+ " (seller, item, itemdata, quantity, cpu, sold, time) VALUES ("
							+ String.format("'%s', '%s', %d, %d, %d, %d", e.getSellerUUID().toString(), e.getItem().toString(), e.getItemData(), e.getListedQuantity(),
									e.getCostPerUnit(), e.getQuantitySold(), e.getTimeListed()) + ");");
					Exchange.addSellEntryAsync(e);
					ExchangePoll.markChanged();
					callback.sendMessage(ChatColor.GREEN + "Your item was successfully listed on the exchange.");
				} catch (SQLException e1) {
					e1.printStackTrace();
					callback.sendMessage(ChatColor.RED + "There was an error syncing your item with the database.");
				}
			}
		});
	}

}
