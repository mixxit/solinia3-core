package com.solinia.solinia.Interfaces;

import java.io.IOException;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidSpawnGroupSettingException;

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

	void sendSpawnGroupSettingsToSender(CommandSender sender) throws CoreStateInitException;

	void editSetting(String setting, String value)
			throws InvalidSpawnGroupSettingException, NumberFormatException, CoreStateInitException, IOException;

	boolean isDisabled();

	void setDisabled(boolean disabled);

	Location getLocation();
}