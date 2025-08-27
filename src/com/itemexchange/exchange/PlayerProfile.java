package com.adamki11s.itemexchange.exchange;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PlayerProfile {

	private List<SellEntry> sellEntries = new ArrayList<SellEntry>();
	private List<BuyEntry> buyEntries = new ArrayList<BuyEntry>();

	public void addSellEntry(SellEntry e) {
		this.sellEntries.add(e);
	}

	public boolean removeSellEntry(SellEntry e) {
		Iterator<SellEntry> it = sellEntries.iterator();
		while (it.hasNext()) {
			SellEntry e1 = it.next();
			if (e1.getTimeListed() == e.getTimeListed()) {
				it.remove();
				return true;
			}
		}
		return false;
	}
	
	public void addBuyEntry(BuyEntry e) {
		this.buyEntries.add(e);
	}

	public boolean removeBuyEntry(BuyEntry e) {
		Iterator<BuyEntry> it = buyEntries.iterator();
		while (it.hasNext()) {
			BuyEntry e1 = it.next();
			if (e1.getTimeSubmitted() == e.getTimeSubmitted()) {
				it.remove();
				return true;
			}
		}
		return false;
	}
	
	public int getTotalEntries(){
		return sellEntries.size() + buyEntries.size();
	}
	
	public boolean hasMaxEntries(){
		return sellEntries.size() + buyEntries.size() >= 9;
	}

}
