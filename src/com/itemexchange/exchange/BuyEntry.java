package com.adamki11s.itemexchange.exchange;

import org.bukkit.Material;

import com.adamki11s.itemexchange.database.Database;
import com.adamki11s.itemexchange.sql.SQLQueries;

public class BuyEntry {
	
	private final String buyerUUID;
	private final Material item;
	private final int quantity, maxCPU, itemdata;
	private final long timeSubmitted;
	private int quantityBought;
	
	public BuyEntry(String buyerUUID, Material item, int itemdata, int quantity, int maxCPU, int quantityBought, long timeSubmitted) {
		this.buyerUUID = buyerUUID;
		this.item = item;
		this.itemdata = itemdata;
		this.quantity = quantity;
		this.maxCPU = maxCPU;
		this.quantityBought = quantityBought;
		this.timeSubmitted = timeSubmitted;
	}
	
	public long getTimeSubmitted(){
		return timeSubmitted;
	}

	public String getBuyerUUID() {
		return buyerUUID;
	}

	public Material getItem() {
		return item;
	}
	
	public int getItemData(){
		return itemdata;
	}

	public int getQuantity() {
		return quantity;
	}
	
	public int getRemainingQuantity(){
		return quantity - quantityBought;
	}

	public int getMaxCPU() {
		return maxCPU;
	}

	public int getQuantityBought() {
		return quantityBought;
	}
	
	public void itemsBought(int quantity){
		//increment the entry by the amount given and update sql database
		quantityBought += quantity;
		//update SQL DB
		SQLQueries.addQuery("UPDATE " + Database.OFFER_TABLE + " SET bought=" + quantityBought + " WHERE buyer='" + buyerUUID + "' AND time=" + timeSubmitted + ";");
	}

}
