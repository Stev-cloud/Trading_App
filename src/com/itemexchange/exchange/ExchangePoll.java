package com.adamki11s.itemexchange.exchange;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExchangePoll implements Runnable {

	/*
	 * This will poll every second, but only if there have been updates to the
	 * system.
	 * 
	 * The sellers price will always remain locked, and will seek to find the
	 * buyer the lowest possible price for their items
	 */

	private static volatile boolean changed = true;

	@Override
	public void run() {
		if (changed) {
			changed = false;

			// run checks
			for (BuyEntry be : Exchange.offers) {
				// for each buy offer

				if (be.getQuantityBought() >= be.getQuantity()) {
					// purchase already a success but not claimed by buyer
					continue;
				}

				List<SellEntry> matches = new ArrayList<SellEntry>();
				for (SellEntry se : Exchange.buyableEntries) {
					// check each sell entry
					if (se.isPurchasable() && se.getItem().equals(be.getItem())) {
						matches.add(se);
					}
				}

				// sort by lowest price first
				Collections.sort(matches);

				int quantityRemainingInit = be.getRemainingQuantity(), remaining = be.getRemainingQuantity();

				for (SellEntry se : matches) {
					// for each matched item
					if (remaining > 0 && se.getCostPerUnit() <= be.getMaxCPU()) {
						// if has not completed offer

						// buy all
						if (se.getQuantityRemaining() <= remaining) {
							int rem = se.getQuantityRemaining();
							se.itemsSold(rem);
							remaining -= rem;
						} else {
							// seller has more than we need, so just buy what we
							// need to complete offer
							se.itemsSold(remaining);
							remaining = 0;
							break;
						}
					} else {
						// doesn't need any more, or cost is higher than the max
						// client is willing to pay
						break;
					}
				}

				int bought = quantityRemainingInit - remaining;
				if (bought > 0) {
					// register items have been bought, if any...
					be.itemsBought(bought);
				}
			}
		}
	}

	public static void markChanged() {
		changed = true;
	}

}
