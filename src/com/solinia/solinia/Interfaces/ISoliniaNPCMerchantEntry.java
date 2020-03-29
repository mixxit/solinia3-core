package com.solinia.solinia.Interfaces;

public interface ISoliniaNPCMerchantEntry extends IPersistable {

	int getId();

	void setId(int id);

	int getItemid();

	void setItemid(int itemid);

	int getMerchantid();

	void setMerchantid(int merchantid);

	int getTemporaryquantitylimit();

	void setTemporaryquantitylimit(int temporaryquantitylimit);
}
