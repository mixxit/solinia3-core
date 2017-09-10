package com.solinia.solinia.Interfaces;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidClassSettingException;

public interface ISoliniaClass {

	public String getName();

	public int getId();

	void setId(int id);

	void setName(String name);

	void setAdmin(boolean adminonly);

	boolean isAdmin();

	public String getDescription();

	void setDescription(String description);

	void setValidRaces(List<Integer> validRaces);

	List<Integer> getValidRaces();

	String getDefaultLegsMaterial();

	void setDefaultLegsMaterial(String defaultLegsMaterial);

	String getDefaultFeetMaterial();

	void setDefaultFeetMaterial(String defaultFeetMaterial);

	String getDefaultHeadMaterial();

	void setDefaultHeadMaterial(String defaultHeadMaterial);

	String getDefaultChestMaterial();

	void setDefaultChestMaterial(String defaultChestMaterial);

	void sendClassSettingsToSender(CommandSender sender) throws CoreStateInitException;

	void editSetting(String setting, String value)
			throws InvalidClassSettingException, NumberFormatException, CoreStateInitException;

	public String getItemArmorTypeName(String basename);

	public int getItemGenerationBonus(String string);

}
