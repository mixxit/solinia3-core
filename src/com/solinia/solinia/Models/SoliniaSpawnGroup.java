package com.solinia.solinia.Models;

import org.bukkit.Location;

import com.solinia.solinia.Interfaces.ISoliniaSpawnGroup;

public class SoliniaSpawnGroup implements ISoliniaSpawnGroup {
	private int id;
	private String name;
	private String world;
	private double x;
	private double y;
	private double z;
	private int npcid;
	private double yaw;
	private double pitch;
	private int respawntime = 360;
	
	@Override
	public int getId() {
		return id;
	}
	@Override
	public void setId(int id) {
		this.id = id;
	}
	@Override
	public String getName() {
		return name;
	}
	@Override
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String getWorld() {
		return world;
	}
	@Override
	public void setWorld(String world) {
		this.world = world;
	}
	@Override
	public double getX() {
		return x;
	}
	@Override
	public void setX(double x) {
		this.x = x;
	}
	@Override
	public double getY() {
		return y;
	}
	@Override
	public void setY(double y) {
		this.y = y;
	}
	@Override
	public double getZ() {
		return z;
	}
	@Override
	public void setZ(double z) {
		this.z = z;
	}
	
	@Override
	public int getNpcid() {
		return npcid;
	}

	@Override
	public void setNpcid(int npcid) {
		this.npcid = npcid;
	}
	@Override
	public void setLocation(Location location) {
		// TODO Auto-generated method stub
		this.x = location.getX();
		this.y = location.getY();
		this.z = location.getZ();
		this.world = location.getWorld().getName();
		this.yaw = location.getYaw();
		this.pitch = location.getPitch();
	}
	
	@Override
	public double getYaw() {
		return yaw;
	}
	@Override
	public void setYaw(double yaw) {
		this.yaw = yaw;
	}
	@Override
	public double getPitch() {
		return pitch;
	}
	@Override
	public void setPitch(double pitch) {
		this.pitch = pitch;
	}
	
	@Override
	public int getRespawntime() {
		return respawntime;
	}
	@Override
	public void setRespawntime(int respawntime) {
		this.respawntime = respawntime;
	}
}
