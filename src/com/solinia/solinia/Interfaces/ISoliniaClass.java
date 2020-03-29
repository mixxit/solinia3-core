package com.solinia.solinia.Interfaces;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.command.CommandSender;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidClassSettingException;
import com.solinia.solinia.Models.ItemGenBonusStatType;
import com.solinia.solinia.Models.ItemType;
import com.solinia.solinia.Models.RaceClass;

public interface ISoliniaClass extends IPersistable {

	public String getName();

	public int getId();

	void setId(int id);

	void setName(String name);

	void setAdmin(boolean adminonly);

	boolean isAdmin();

	public String getDescription();

	void setDescription(String description);

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

	public int getItemGenerationBonus(ItemGenBonusStatType statType);

	String getDefaulthandMaterial();

	void setDefaulthandMaterial(String defaulthandMaterial);

	String getDefaultoffHandMaterial();

	void setDefaultoffHandMaterial(String defaultoffHandMaterial);

	String getSwordtypename();

	void setSwordtypename(String swordtypename);

	String getShieldtypename();

	void setShieldtypename(String shieldtypename);

	String getAxetypename();

	void setAxetypename(String axetypename);

	String getSpadetypename();

	void setSpadetypename(String spadetypename);

	String getBowtypename();

	void setBowtypename(String bowtypename);

	public boolean canDodge();

	public boolean canDoubleAttack();

	int getDodgelevel();

	void setDodgelevel(int dodgelevel);

	int getRipostelevel();

	void setRipostelevel(int ripostelevel);

	int getDoubleattacklevel();

	void setDoubleattacklevel(int doubleattacklevel);

	boolean canRiposte();

	int getSafefalllevel();

	void setSafefalllevel(int safefalllevel);

	boolean canSafeFall();

	String getShortName();

	void setShortName(String shortName);

	public boolean isWarriorClass();

	String getClassItemPrefix();

	void setClassItemPrefix(String classItemPrefix);

	int getACItemBonus();

	void setACItemBonus(int acitembonus);

	int getSpecialiselevel();

	void setSpecialiselevel(int specialiselevel);

	int getNpcspelllist();

	void setNpcspelllist(int npcspelllist);

	boolean isSneakFromCrouch();

	void setSneakFromCrouch(boolean sneakFromCrouch);

	String getLevel51Title();

	void setLevel51Title(String level51Title);

	String getLevel55Title();

	void setLevel55Title(String level55Title);

	String getLevel60Title();

	void setLevel60Title(String level60Title);

	boolean canDualWield();

	int getDualwieldlevel();

	void setDualwieldlevel(int dualwieldlevel);

	List<Integer> getOaths();

	void setOaths(List<Integer> oaths);

	ItemType getDefaultHandItemType();

	void setDefaultHandItemType(ItemType defaultHandItemType);

	ItemType getDefaultOffHandItemType();

	void setDefaultOffHandItemType(ItemType defaultOffHandItemType);

	int getWeaponDelayItemBonus();

	void setWeaponDelayItemBonus(int weaponDelayItemBonus);

	String getDefaultAlternateHandMaterial();

	void setDefaultAlternateHandMaterial(String defaultAlternateHandMaterial);

	ItemType getDefaultAlternateHandItemType();

	void setDefaultAlternateHandItemType(ItemType defaultAlternateHandItemType);

	boolean canPray();

	void setCanPray(boolean canPray);

	int getLeatherRgbDecimal();

	void setLeatherRgbDecimal(int leatherRgbDecimal);

	ConcurrentHashMap<Integer, RaceClass> getValidRaceClasses();

	void setValidRaceClasses(ConcurrentHashMap<Integer, RaceClass> validRaceClasses);

	RaceClass getRaceClass(int raceId);

	String getCrossbowtypename();

	void setCrossbowtypename(String crossbowtypename);

	int getTrackingLevel();

	void setTrackingLevel(int trackingLevel);

	int getDisarmLevel();

	void setDisarmLevel(int disarmLevel);

	public boolean canDisarm();

	int getMakePoisonLevel();

	void setMakePoisonLevel(int makePoisonLevel);

	int getMeditationLevel();

	void setMeditationLevel(int meditationLevel);

	int getDropSpellsLootTableId();

	void setDropSpellsLootTableId(int dropSpellsLootTableId);

	int getAppearanceId();

	void setAppearanceId(int appearanceId);

}
