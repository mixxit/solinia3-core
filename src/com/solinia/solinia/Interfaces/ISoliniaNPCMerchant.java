package com.solinia.solinia.Interfaces;

import java.util.List;

public interface ISoliniaNPCMerchant {

	int getId();

	void setId(int id);

	String getName();

	void setName(String name);

	List<ISoliniaNPCMerchantEntry> getEntries();

	void setEntries(List<ISoliniaNPCMerchantEntry> entries);
}
