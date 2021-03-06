package com.solinia.solinia.Interfaces;

import org.bukkit.command.CommandSender;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidRaceSettingException;
import com.solinia.solinia.Models.SkillType;
import com.solinia.solinia.Models.SoliniaDisguise;

public interface ISoliniaRace extends IPersistable {

	public String getName();

	public boolean isAdmin();

	public int getId();

	void setAdmin(boolean isadmin);

	void setAgility(int agility);

	void setCharisma(int charisma);

	void setDexterity(int dexterity);

	void setIntelligence(int intelligence);

	void setStamina(int stamina);

	void setStrength(int strength);

	void setWisdom(int wisdom);

	public int getAgility();

	public int getCharisma();

	public int getDexterity();

	public int getIntelligence();

	public int getStamina();

	public int getStrength();

	public int getWisdom();

	void setId(int id);

	void setName(String name);

	void editSetting(String setting, String value)
			throws NumberFormatException, CoreStateInitException, InvalidRaceSettingException;

	void sendRaceSettingsToSender(CommandSender sender) throws CoreStateInitException;

	String getDescription();

	void setDescription(String description);

	String getAlignment();

	void setAlignment(String alignment);

	String getShortName();

	void setShortName(String shortName);

	boolean isVampire();

	void setVampire(boolean vampire);

	int getPassiveAbilityId();

	void setPassiveAbilityId(int passiveAbilityId);

	int getRaceLootTableId();

	void setRaceLootTableId(int raceLootTableId);

	int getBodyType();

	void setBodyType(int bodyType);

	SkillType getLanguage();

	void setLanguage(SkillType language);

	boolean isUndead();

	void setUndead(boolean undead);

	void setPlant(boolean plant);

	boolean isPlant();

	boolean isAnimal();

	void setAnimal(boolean animal);

	void setArmouredSkin(boolean armouredSkin);

	boolean isArmouredSkin();

	int getRacePetDisguiseId();

	void setRacePetDisguiseId(int racePetDisguiseId);

	SoliniaDisguise getRacePetDisguise();
}
