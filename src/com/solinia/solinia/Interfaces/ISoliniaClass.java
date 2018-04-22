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

}
