package com.solinia.solinia.Interfaces;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidAASettingException;

public interface ISoliniaAAAbility {

	String getSysname();

	void setSysname(String sysname);

	String getName();

	void setName(String name);

	int getId();

	void setId(int id);

	List<String> getClasses();

	void setClasses(List<String> classes);

	List<ISoliniaAARank> getRanks();

	void setRanks(List<ISoliniaAARank> ranks);

	boolean canClassUseAbility(ISoliniaClass iSoliniaClass);

	boolean isEnabled();

	void setEnabled(boolean isEnabled);

	void sendAASettingsToSender(CommandSender sender) throws CoreStateInitException;

	void editSetting(String setting, String value) throws InvalidAASettingException;

	List<ISoliniaAAEffect> getEffects();

	void setEffects(List<ISoliniaAAEffect> effects);

}
