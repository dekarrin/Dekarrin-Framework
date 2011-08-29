package com.dekarrin.wow;

import java.util.HashMap;

public class AuctionData {
	public long auc = 0L;
	public int item = 0;
	public String owner = null;
	public int quantity = 0;
	public AuctionTime timeLeft = null;
	public int bid = 0;
	public int buyout = 0;
	public String faction = null;
	public AuctionData() {super();}
	public AuctionData(HashMap<String,String> dataSource) {
		auc = Long.parseLong(dataSource.get("auc"));
		item = Integer.parseInt(dataSource.get("item"));
		owner = dataSource.get("owner");
		quantity = Integer.parseInt(dataSource.get("quantity"));
		timeLeft = AuctionTime.valueOf(dataSource.get("timeLeft"));
		bid = Integer.parseInt(dataSource.get("bid"));
		buyout = Integer.parseInt(dataSource.get("buyout"));
		faction = dataSource.get("faction");
	}
	public boolean equals(AuctionData ad) {
		if(
		 ad.auc == auc && ad.item == item &&
		 ad.owner.equals(owner) && ad.quantity == quantity &&
		 ad.timeLeft == timeLeft && ad.bid == bid &&
		 ad.buyout == buyout && ad.faction == faction
		) {
			return true;
		} else {
			return false;
		}
	}
}