package com.solinia.solinia.Models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.solinia.solinia.Interfaces.IPersistable;
import com.solinia.solinia.Interfaces.ISoliniaPatch;

public class SoliniaPatch implements ISoliniaPatch,IPersistable {
	private int id;
	private UUID primaryUUID = UUID.randomUUID();
	private UUID secondaryUUID = UUID.randomUUID();
	private ConcurrentHashMap<String, String> playerRestoreInventory = new ConcurrentHashMap<String, String>();
	
	private List<String> classes = new ArrayList<String>();
	@Override
	public int getId() {
		return id;
	}
	@Override
	public void setId(int id) {
		this.id = id;
	}
	
	@Override
	public UUID getPrimaryUUID() {
		// TODO Auto-generated method stub
		return this.primaryUUID;
	}
	@Override
	public void setPrimaryUUID(UUID uuid) {
		// TODO Auto-generated method stub
		this.primaryUUID = uuid;
	}
	@Override
	public UUID getSecondaryUUID() {
		// TODO Auto-generated method stub
		return this.secondaryUUID;
	}
	@Override
	public void setSecondaryUUID(UUID uuid) {
		// TODO Auto-generated method stub
		this.secondaryUUID = uuid;
	}
	
	@Override
	public List<String> getClasses() {
		return classes;
	}
	@Override
	public void setClasses(List<String> classes) {
		this.classes = classes;
	}
	@Override
	public ConcurrentHashMap<String, String> getPlayerRestoreInventory() {
		return playerRestoreInventory;
	}
	@Override
	public void setPlayerRestoreInventory(ConcurrentHashMap<String, String> playerRestoreInventory) {
		this.playerRestoreInventory = playerRestoreInventory;
	}
}
