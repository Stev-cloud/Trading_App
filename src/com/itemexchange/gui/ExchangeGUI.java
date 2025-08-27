package com.adamki11s.itemexchange.gui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class ExchangeGUI implements Listener {

	public static Map<Integer, int[]> data = new HashMap<Integer, int[]>();

	public static Set<Integer> itemIDs = new HashSet<Integer>();

	public static List<ItemStack> prefabPurchaseMenu = new LinkedList<ItemStack>();

	private static int pages;

	/*
	 * Display on 6 rows, 5 * 9 items = 45, bottom left and right for navigation
	 */

	public ExchangeGUI(Plugin p) {
		Bukkit.getPluginManager().registerEvents(this, p);
	}

	private final static String invName = ChatColor.BLUE + "ItemExchange";

	private static Map<String, Integer> playerPage = new HashMap<String, Integer>();

	private static Map<String, Integer> chosenIndex = new HashMap<String, Integer>();

	@EventHandler
	private void close(InventoryCloseEvent evt) {
		if (evt.getPlayer() instanceof Player && evt.getInventory().getName().equals(invName)) {
			Player p = (Player) evt.getPlayer();
			if (chosenIndex.containsKey(p.getUniqueId().toString())) {
				chosenIndex.remove(p.getUniqueId().toString());
			}
		}
	}
	
	private String getFormattedName(ItemStack i){
		String base = i.getType().toString();
		String[] words = base.split("_");
		StringBuilder b = new StringBuilder().append(words[0].substring(0, 1) + words[0].substring(1).toLowerCase());
		if(words.length > 1){
			for(int e = 1; e < words.length; e++){
				String w = words[e];
				b.append(" ").append(w.substring(0, 1)).append(w.substring(1).toLowerCase());
			}
		}
		if(i.getData().getData() != 0){
			b.append(":").append(i.getData().getData());
		}
		return b.toString();
	}

	@EventHandler
	private void inventoryClick(InventoryClickEvent evt) {
		String in = evt.getInventory().getName();
		if (evt.getWhoClicked() instanceof Player && in.equals(invName)) {
			// purchase browsing menu clicked
			Player p = (Player) evt.getWhoClicked();
			if(!playerPage.containsKey(p.getUniqueId().toString())){
				playerPage.put(p.getUniqueId().toString(), 1);
			}
			int page = playerPage.get(p.getUniqueId().toString());
			if (evt.getSlot() < 45) {
				// item clicked
				int i = ((page - 1) * 45) + evt.getSlot();
				chosenIndex.put(p.getUniqueId().toString(), i);
				p.sendMessage(ChatColor.GREEN + "Item Selected " + ChatColor.BLUE + "[" + ChatColor.RESET
						+ getFormattedName(evt.getInventory().getItem(evt.getSlot())) + ChatColor.BLUE + "]");
				p.closeInventory();
			} else if (evt.getSlot() == 45) {
				// previous
				if (page >= 2) {
					playerPage.put(p.getUniqueId().toString(), page - 1);
					repopulateMenu(evt.getInventory(), page - 1);
				} else {
					p.sendMessage(ChatColor.RED + "This is the first page.");
				}
			} else if (evt.getSlot() == 53) {
				// next
				if (page + 1 <= getPages()) {
					playerPage.put(p.getUniqueId().toString(), page + 1);
					repopulateMenu(evt.getInventory(), page + 1);
				} else {
					p.sendMessage(ChatColor.RED + "There are no more pages.");
				}
			}
			evt.setResult(Result.DENY);
			evt.setCancelled(true);
		}
	}

	private static void repopulateMenu(Inventory inventory, int page) {
		int startIndex = (page - 1) * 45;
		int localIndex = 0;
		for (int i = startIndex; i < (startIndex + 45); i++) {
			if (i < prefabPurchaseMenu.size()) {
				inventory.setItem(localIndex, prefabPurchaseMenu.get(i));
			} else {
				inventory.setItem(localIndex, null);
			}
			localIndex++;
		}
	}

	public static void openMenu(Player p, int page) {
		Inventory inventory = Bukkit.createInventory(p, 54, invName);
		int startIndex = (page - 1) * 45;
		int localIndex = 0;
		for (int i = startIndex; i < (startIndex + 45); i++) {
			if (i < prefabPurchaseMenu.size()) {
				inventory.setItem(localIndex, prefabPurchaseMenu.get(i));
				localIndex++;
			} else {
				break;
			}
		}
		inventory.setItem(45, new ItemStack(Material.COMPASS));
		inventory.setItem(53, new ItemStack(Material.COMPASS));
		playerPage.put(p.getUniqueId().toString(), 1);
		p.openInventory(inventory);
	}

	public static int getPages() {
		return pages;
	}

	/*
	 * Sequencing 1-175 256-408 417-422
	 */

	// commented data chunks are 1.8
	public static void initialise() {
		// data.put(1, new int[] { 0, 1, 2, 3, 4, 5, 6 });
		// data.put(3, new int[] { 0, 1, 2 });
		data.put(5, new int[] { 0, 1, 2, 3, 4, 5 });
		data.put(6, new int[] { 0, 1, 2, 3, 4, 5 });
		data.put(17, new int[] { 0, 1, 2, 3, 4, 5 });
		data.put(18, new int[] { 0, 1, 2, 3 });
		data.put(24, new int[] { 0, 1, 2 });
		data.put(31, new int[] { 0, 1, 2 });
		data.put(35, new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 });
		data.put(38, new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8 });
		data.put(43, new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 });
		data.put(44, new int[] { 0, 1, 2, 3, 4, 5, 6, 7 });
		data.put(95, new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 });
		data.put(97, new int[] { 0, 1, 2, 3, 4, 5 });
		data.put(98, new int[] { 0, 1, 2, 3 });
		data.put(125, new int[] { 0, 1, 2, 3, 4, 5 });
		data.put(126, new int[] { 0, 1, 2, 3, 4, 5 });
		data.put(139, new int[] { 0, 1 });
		// player head blocks excluded
		data.put(144, new int[] { 0, 1, 2, 4 });
		data.put(145, new int[] { 0, 1, 2 });
		data.put(159, new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 });
		data.put(160, new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 });
		data.put(162, new int[] { 0, 1 });
		data.put(171, new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 });
		data.put(175, new int[] { 0, 1, 2, 3, 4, 5 });
		data.put(349, new int[] { 0, 1, 2, 3 });
		data.put(350, new int[] { 0, 1, 2, 3 });
		data.put(351, new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 });
		// no potion support yet
		// data.put(383, new int[] { 50, 51, 52, 54, 55, 56, 57, 58, 59, 60, 61,
		// 62, 65, 66, 90, 91, 92, 93, 94, 95, 96, 98, 100, 120 });
		// data.put(397, new int[] { 0, 1, 2, 3, 4 });
		// no support for disks yet

		// populate ids
		for (int i = 1; i <= 175; i++) {
			if ((i >= 8 && i <= 11) || i == 51 || i == 90 || i == 26 || i == 34 || i == 36 || i == 55 || i == 59 || (i >= 62 && i <= 64)
					|| i == 68 || i == 71 || i == 74 || i == 75 || i == 83 || (i >= 92 && i <= 94) || i == 104 || i == 105 || i == 115
					|| i == 117 || i == 118 || i == 119 || i == 124 || i == 125 || i == 132 || (i >= 140 && i <= 142) || i == 144
					|| i == 149 || i == 150 || (i >= 165 && i <= 169)) {
				// exclude pure lava/water and portal blocks and bed blocks and
				// piston heads and piston(moving) and redstone wire and wheat
				// and smelting furnac,e door block, and sign(placed) and
				// sign(placed on wall)
				// and iron door block and redstone glowing block and torch off
				// and sugar cane block and cake block and redstone repeater
				continue;
			}
			itemIDs.add(i);
			populatePrefab(i);
		}

		for (int i = 256; i <= 408; i++) {
			if (i == 397) {
				continue;
			}
			itemIDs.add(i);
			populatePrefab(i);
		}

		for (int i = 417; i <= 422; i++) {
			if (i == 421) {
				continue;
			}
			itemIDs.add(i);
			populatePrefab(i);
		}

		pages = (int) Math.ceil((double) totItems / 45D);
	}

	static int totItems = 0;

	private static void populatePrefab(int i) {
		if (data.containsKey(i)) {
			for (int d : data.get(i)) {
				prefabPurchaseMenu.add(new ItemStack(i, 1, (short) d));
				totItems++;
			}
		} else {
			prefabPurchaseMenu.add(new ItemStack(i, 1, (short) 0));
			totItems++;
		}
	}

}
