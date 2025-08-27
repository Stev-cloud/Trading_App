package com.adamki11s.itemexchange;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.adamki11s.itemexchange.database.Database;

public class Config {

	// directories
	public static final File ROOT = new File("plugins" + File.separator + "ItemExchange"), DATABASE = new File(ROOT + File.separator
			+ "sqlite");

	// files
	public static final File CONFIG = new File(ROOT + File.separator + "config.yml"), SQLITE = new File(DATABASE + File.separator
			+ "db.dat");

	public static void init() {
		ROOT.mkdir();
		DATABASE.mkdir();

		FileConfiguration io = YamlConfiguration.loadConfiguration(CONFIG);

		if (!SQLITE.exists()) {
			try {
				SQLITE.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (!CONFIG.exists()) {
			try {
				CONFIG.createNewFile();

				// if false then SQLite will be used instead
				io.set("database.mysql.use", true);
				io.set("database.mysql.host", "localhost");
				io.set("database.mysql.database", "minecraft");
				io.set("database.mysql.username", "root");
				io.set("database.mysql.password", "alpine");
				io.save(CONFIG);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		boolean useSQL = io.getBoolean("database.mysql.use");

		String[] data = useSQL ? new String[] { io.getString("database.mysql.host"), io.getString("database.mysql.database"),
				io.getString("database.mysql.username"), io.getString("database.mysql.password") } : null;
		
		Database.initDatabase(useSQL, data);
	}

}
