package com.adamki11s.itemexchange.exchange;

import org.bukkit.Material;

import com.adamki11s.itemexchange.database.Database;
import com.adamki11s.itemexchange.sql.SQLQueries;

public class SellEntry implements Comparable<SellEntry> {
	
	/*
	 * An exchange entry to the system
	 */
	
	private final String sellerUUID;
	private final int quantity, costPerUnit, itemdata;
	private final Material item;
	private final long time;
	private int sold;
	
	public SellEntry(String sellerUUID, Material item, int itemdata, int quantity, int costPerUnit, int sold, long time) {
		this.sellerUUID = sellerUUID;
		this.item = item;
		this.itemdata = itemdata;
		this.quantity = quantity;
		this.costPerUnit = costPerUnit;
		this.sold = sold;
		this.time = time;
	}

	public String getSellerUUID() {
		return sellerUUID;
	}

	public Material getItem() {
		return item;
	}
	
	public int getItemData(){
		return itemdata;
	}

	public int getListedQuantity() {
		return quantity;
	}

	public int getCostPerUnit() {
		return costPerUnit;
	}

	public long getTimeListed() {
		return time;
	}

	public int getQuantitySold() {
		return sold;
	}
	
	public int getQuantityRemaining(){
		return quantity - sold;
	}
	
	/*
	 * Used to stop searching through entries which have already been sold out
	 */
	public boolean isPurchasable(){
		return sold < quantity;
	}
	
	public void itemsSold(int quantity){
		//increment the entry by the amount given and update sql database
		sold += quantity;
		SQLQueries.addQuery("UPDATE " + Database.ITEM_TABLE + " SET sold=" + sold + " WHERE seller='" + sellerUUID + "' AND time=" + time + ";");
	}

	@Override
	public int compareTo(SellEntry se) {
		//ascending order
				//return this.quantity - compareQuantity;
		
				//descending order
				//return compareQuantity - this.quantity;
		return getCostPerUnit() - se.getCostPerUnit();
	}
}
