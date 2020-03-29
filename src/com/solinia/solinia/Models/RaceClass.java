package com.solinia.solinia.Models;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class RaceClass {
	public int RaceId;
	private int startX = 169;
	private int startY = 78;
	private int startZ = -3672;
	private String startWorld = "world";
	
	public int getStartX() {
		return startX;
	}

	public void setStartX(int startX) {
		this.startX = startX;
	}

	public int getStartY() {
		return startY;
	}

	public void setStartY(int startY) {
		this.startY = startY;
	}

	public int getStartZ() {
		return startZ;
	}

	public void setStartZ(int startZ) {
		this.startZ = startZ;
	}

	public String getStartWorld() {
		return startWorld;
	}

	public void setStartWorld(String startWorld) {
		this.startWorld = startWorld;
	}

	public Location getStartLocation() {
		return new Location(Bukkit.getWorld(getStartWorld()), getStartX(), getStartY(), getStartZ());
	}
}
