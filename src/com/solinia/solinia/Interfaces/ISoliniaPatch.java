package com.solinia.solinia.Interfaces;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public interface ISoliniaPatch extends IPersistable {

	int getId();

	void setId(int id);

	List<String> getClasses();

	void setClasses(List<String> classes);

	ConcurrentHashMap<String, String> getPlayerRestoreInventory();

	void setPlayerRestoreInventory(ConcurrentHashMap<String, String> playerRestoreInventory);

}
