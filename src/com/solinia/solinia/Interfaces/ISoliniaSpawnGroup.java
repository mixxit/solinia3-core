package com.solinia.solinia.Interfaces;

import org.bukkit.Location;

public interface ISoliniaSpawnGroup {

	int getId();

	void setId(int id);

	String getName();

	void setName(String name);

	String getWorld();

	void setWorld(String world);

	double getX();

	void setX(double x);

	double getY();

	void setY(double y);

	double getZ();

	void setZ(double z);

	int getNpcid();

	void setNpcid(int npcid);

	void setLocation(Location location);

	double getYaw();

	void setYaw(double yaw);

	double getPitch();

	void setPitch(double pitch);

	int getRespawntime();

	void setRespawntime(int respawntime);

}