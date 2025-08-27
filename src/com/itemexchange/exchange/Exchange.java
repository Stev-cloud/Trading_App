package com.adamki11s.itemexchange.exchange;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.adamki11s.itemexchange.ItemExchange;

public class Exchange {
	
	public static List<SellEntry> buyableEntries;
	public static List<SellEntry> soldEntries;
	
	public static List<BuyEntry> offers;
	
	public static void initExchange(List<SellEntry> loaded, List<BuyEntry> buyEntries){
		buyableEntries = Collections.synchronizedList(new ArrayList<SellEntry>());
		soldEntries = Collections.synchronizedList(new ArrayList<SellEntry>());
		offers = Collections.synchronizedList(new ArrayList<BuyEntry>());
		
		for(SellEntry e : loaded){
			if(e.isPurchasable()){
				buyableEntries.add(e);
			} else {
				soldEntries.add(e);
			}
		}
		
		offers.addAll(buyEntries);
		
		ItemExchange.getLog().info(String.format("Loaded a total of %d sale exchange entries.", loaded.size()));
		ItemExchange.getLog().info(String.format("Loaded a total of %d purchase exchange entries.", buyEntries.size()));
	}
	
	public static void addSellEntryAsync(SellEntry e){
		buyableEntries.add(e);
	}
	
	public static void addBuyEntryAsync(BuyEntry e){
		offers.add(e);
	}
	

}
