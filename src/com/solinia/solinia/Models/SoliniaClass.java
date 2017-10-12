package com.solinia.solinia.Models;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidClassSettingException;
import com.solinia.solinia.Exceptions.InvalidItemSettingException;
import com.solinia.solinia.Interfaces.ISoliniaClass;
import com.solinia.solinia.Managers.StateManager;

import net.md_5.bungee.api.ChatColor;

public class SoliniaClass implements ISoliniaClass {

	private int id;
	private boolean isadmin = true;
	private String name = "";
	private String description = "";
	private List<Integer> validRaces = new ArrayList<Integer>();
	private String defaultHeadMaterial = "LEATHER_HELMET";
	private String defaultChestMaterial = "LEATHER_CHESTPLATE";
	private String defaultLegsMaterial = "LEATHER_LEGGINGS";
	private String defaultFeetMaterial = "LEATHER_BOOTS";
	private String defaulthandMaterial = "WOOD_SWORD";
	private String defaultoffHandMaterial = "SHIELD";
	private int strengthitembonus = 0;
	private int staminaitembonus = 0;
	private int agilityitembonus = 0;
	private int dexterityitembonus = 0;
	private int intelligenceitembonus = 0;
	private int wisdomitembonus = 0;
	private int charismaitembonus = 0;
	private String helmtypename = "Hat";
	private String chesttypename = "Tunic";
	private String legstypename = "Leggings";
	private String bootstypename = "Boots";
	private String swordtypename = "Sword";
	private String shieldtypename = "Shield";
	private String axetypename = "Axe";
	private String spadetypename = "Staff";
	private String bowtypename = "Bow";
	private int dodgelevel = 0;
	private int ripostelevel = 0;
	private int doubleattacklevel = 0;
	private int safefalllevel = 0;
	private String shortName = "";
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return this.name;
	}

	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return this.id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void setAdmin(boolean adminonly) {
		this.isadmin = adminonly;
	}
	
	@Override
	public boolean isAdmin()
	{
		return this.isadmin;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public List<Integer> getValidRaces() {
		return validRaces;
	}

	@Override
	public void setValidRaces(List<Integer> validRaces) {
		this.validRaces = validRaces;
	}

	@Override
	public String getDefaultHeadMaterial() {
		return defaultHeadMaterial;
	}

	@Override
	public void setDefaultHeadMaterial(String defaultHeadMaterial) {
		this.defaultHeadMaterial = defaultHeadMaterial;
	}

	@Override
	public String getDefaultChestMaterial() {
		return defaultChestMaterial;
	}

	@Override
	public void setDefaultChestMaterial(String defaultChestMaterial) {
		this.defaultChestMaterial = defaultChestMaterial;
	}

	@Override
	public String getDefaultLegsMaterial() {
		return defaultLegsMaterial;
	}

	@Override
	public void setDefaultLegsMaterial(String defaultLegsMaterial) {
		this.defaultLegsMaterial = defaultLegsMaterial;
	}

	@Override
	public String getDefaultFeetMaterial() {
		return defaultFeetMaterial;
	}

	@Override
	public void setDefaultFeetMaterial(String defaultFeetMaterial) {
		this.defaultFeetMaterial = defaultFeetMaterial;
	}

	@Override
	public void sendClassSettingsToSender(CommandSender sender) throws CoreStateInitException {
		sender.sendMessage(ChatColor.RED + "Class Settings for " + ChatColor.GOLD + getName() + ChatColor.RESET);
		sender.sendMessage("----------------------------");
		sender.sendMessage("- id: " + ChatColor.GOLD + getId() + ChatColor.RESET);
		sender.sendMessage("- name: " + ChatColor.GOLD + getName() + ChatColor.RESET);
		sender.sendMessage("- shortname: " + ChatColor.GOLD + getShortName() + ChatColor.RESET);
		sender.sendMessage("----------------------------");
		sender.sendMessage("- defaultheadmaterial: " + ChatColor.GOLD + getDefaultHeadMaterial() + ChatColor.RESET);
		sender.sendMessage("- defaultchestmaterial: " + ChatColor.GOLD + getDefaultChestMaterial() + ChatColor.RESET);
		sender.sendMessage("- defaultlegsmaterial: " + ChatColor.GOLD + getDefaultLegsMaterial() + ChatColor.RESET);
		sender.sendMessage("- defaultfeetmaterial: " + ChatColor.GOLD + getDefaultFeetMaterial() + ChatColor.RESET);
		sender.sendMessage("- defaulthandmaterial: " + ChatColor.GOLD + getDefaulthandMaterial() + ChatColor.RESET);
		sender.sendMessage("- defaultoffhandmaterial: " + ChatColor.GOLD + getDefaultoffHandMaterial() + ChatColor.RESET);
		sender.sendMessage("----------------------------");
		sender.sendMessage("- strengthitembonus: " + ChatColor.GOLD + getStrengthitembonus() + ChatColor.RESET);
		sender.sendMessage("- staminaitembonus: " + ChatColor.GOLD + getStaminaitembonus() + ChatColor.RESET);
		sender.sendMessage("- agilityitembonus: " + ChatColor.GOLD + getAgilityitembonus() + ChatColor.RESET);
		sender.sendMessage("- dexterityitembonus: " + ChatColor.GOLD + getDexterityitembonus() + ChatColor.RESET);
		sender.sendMessage("- intelligenceitembonus: " + ChatColor.GOLD + getIntelligenceitembonus() + ChatColor.RESET);
		sender.sendMessage("- wisdomitembonus: " + ChatColor.GOLD + getWisdomitembonus() + ChatColor.RESET);
		sender.sendMessage("- charismaitembonus: " + ChatColor.GOLD + getCharismaitembonus() + ChatColor.RESET);
		sender.sendMessage("----------------------------");
		sender.sendMessage("- helmtypename: " + ChatColor.GOLD + getHelmtypename() + ChatColor.RESET);
		sender.sendMessage("- chesttypename: " + ChatColor.GOLD + getChesttypename() + ChatColor.RESET);
		sender.sendMessage("- legstypename: " + ChatColor.GOLD + getLegstypename() + ChatColor.RESET);
		sender.sendMessage("- bootstypename: " + ChatColor.GOLD + getBootstypename() + ChatColor.RESET);
		sender.sendMessage("- swordtypename: " + ChatColor.GOLD + getSwordtypename() + ChatColor.RESET);
		sender.sendMessage("- axetypename: " + ChatColor.GOLD + getAxetypename() + ChatColor.RESET);
		sender.sendMessage("- spadetypename: " + ChatColor.GOLD + getSpadetypename() + ChatColor.RESET);
		sender.sendMessage("- shieldtypename: " + ChatColor.GOLD + getShieldtypename() + ChatColor.RESET);
		sender.sendMessage("- bowtypename: " + ChatColor.GOLD + getBowtypename() + ChatColor.RESET);
		sender.sendMessage("----------------------------");
		sender.sendMessage("- dodgelevel: " + ChatColor.GOLD + getDodgelevel() + ChatColor.RESET);
		sender.sendMessage("- ripostelevel: " + ChatColor.GOLD + getRipostelevel() + ChatColor.RESET);
		sender.sendMessage("- doubleattacklevel: " + ChatColor.GOLD + getDoubleattacklevel() + ChatColor.RESET);
		sender.sendMessage("- safefalllevel: " + ChatColor.GOLD + getSafefalllevel() + ChatColor.RESET);
		sender.sendMessage("----------------------------");
	}

	@Override
	public void editSetting(String setting, String value)
			throws InvalidClassSettingException, NumberFormatException, CoreStateInitException {

		switch (setting.toLowerCase()) {
		case "name":
			if (value.equals(""))
				throw new InvalidClassSettingException("Name is empty");

			if (value.length() > 15)
				throw new InvalidClassSettingException("Name is longer than 15 characters");
			setName(value);
			break;
		case "defaultheadmaterial":
			if (!StateManager.getInstance().getConfigurationManager().ArmourMaterials.contains(value.toUpperCase()))
				throw new InvalidClassSettingException("Invalid material type");
			setDefaultHeadMaterial(value.toUpperCase());
			break;
		case "defaultchestmaterial":
			if (!StateManager.getInstance().getConfigurationManager().ArmourMaterials.contains(value.toUpperCase()))
				throw new InvalidClassSettingException("Invalid material type");
			setDefaultChestMaterial(value.toUpperCase());
			break;
		case "defaultlegsmaterial":
			if (!StateManager.getInstance().getConfigurationManager().ArmourMaterials.contains(value.toUpperCase()))
				throw new InvalidClassSettingException("Invalid material type");
			setDefaultLegsMaterial(value.toUpperCase());
			break;
		case "defaultfeetmaterial":
			if (!StateManager.getInstance().getConfigurationManager().ArmourMaterials.contains(value.toUpperCase()))
				throw new InvalidClassSettingException("Invalid material type");
			setDefaultFeetMaterial(value.toUpperCase());
			break;
		case "defaulthandmaterial":
			if (!StateManager.getInstance().getConfigurationManager().HandMaterials.contains(value.toUpperCase()))
				throw new InvalidClassSettingException("Invalid material type");
			setDefaulthandMaterial(value.toUpperCase());
			break;
		case "defaultoffhandmaterial":
			if (!StateManager.getInstance().getConfigurationManager().HandMaterials.contains(value.toUpperCase()))
				throw new InvalidClassSettingException("Invalid material type");
			setDefaultoffHandMaterial(value.toUpperCase());
			break;
		case "strengthitembonus":
			this.setStrengthitembonus(Integer.parseInt(value));
			break;
		case "shortname":
			this.setShortName(value);
			break;
		case "staminaitembonus":
			this.setStaminaitembonus(Integer.parseInt(value));
			break;
		case "agilityitembonus":
			this.setAgilityitembonus(Integer.parseInt(value));
			break;
		case "dexterityitembonus":
			this.setDexterityitembonus(Integer.parseInt(value));
			break;
		case "intelligenceitembonus":
			this.setIntelligenceitembonus(Integer.parseInt(value));
			break;
		case "wisdomitembonus":
			this.setWisdomitembonus(Integer.parseInt(value));
			break;
		case "charismaitembonus":
			this.setCharismaitembonus(Integer.parseInt(value));
			break;
		case "helmtypename":
			this.setHelmtypename(value);
			break;
		case "chesttypename":
			this.setChesttypename(value);
			break;
		case "legstypename":
			this.setLegstypename(value);
			break;
		case "bootstypename":
			this.setBootstypename(value);
			break;
		case "swordtypename":
			this.setSwordtypename(value);
			break;
		case "axetypename":
			this.setAxetypename(value);
			break;
		case "spadetypename":
			this.setSpadetypename(value);
			break;
		case "shieldtypename":
			this.setShieldtypename(value);
			break;
		case "bowtypename":
			this.setBowtypename(value);
			break;
		case "ripostelevel":
			this.setRipostelevel(Integer.parseInt(value));
			break;
		case "dodgelevel":
			this.setDodgelevel(Integer.parseInt(value));
			break;
		case "doubleattacklevel":
			this.setDoubleattacklevel(Integer.parseInt(value));
			break;
		case "safefalllevel":
			this.setSafefalllevel(Integer.parseInt(value));
			break;
		default:
			throw new InvalidClassSettingException("Invalid Class setting. Valid Options are: name, defaultheadmaterial, defaultchestmaterial,defaultlegsmaterial,defaultfeetmaterial");
		}
	}

	public int getStrengthitembonus() {
		return strengthitembonus;
	}

	public void setStrengthitembonus(int strengthitembonus) {
		this.strengthitembonus = strengthitembonus;
	}

	public int getStaminaitembonus() {
		return staminaitembonus;
	}

	public void setStaminaitembonus(int staminaitembonus) {
		this.staminaitembonus = staminaitembonus;
	}

	public int getDexterityitembonus() {
		return dexterityitembonus;
	}

	public void setDexterityitembonus(int dexterityitembonus) {
		this.dexterityitembonus = dexterityitembonus;
	}

	public int getIntelligenceitembonus() {
		return intelligenceitembonus;
	}

	public void setIntelligenceitembonus(int intelligenceitembonus) {
		this.intelligenceitembonus = intelligenceitembonus;
	}

	public int getAgilityitembonus() {
		return agilityitembonus;
	}

	public void setAgilityitembonus(int agilityitembonus) {
		this.agilityitembonus = agilityitembonus;
	}

	public int getWisdomitembonus() {
		return wisdomitembonus;
	}

	public void setWisdomitembonus(int wisdomitembonus) {
		this.wisdomitembonus = wisdomitembonus;
	}

	public int getCharismaitembonus() {
		return charismaitembonus;
	}

	public void setCharismaitembonus(int charismaitembonus) {
		this.charismaitembonus = charismaitembonus;
	}

	@Override
	public String getItemArmorTypeName(String basename) {
		switch(basename.toUpperCase())
		{
			case "LEATHER_HELMET": return getHelmtypename();
			case "LEATHER_CHESTPLATE": return getChesttypename();
			case "LEATHER_LEGGINGS": return getLegstypename();
			case "LEATHER_BOOTS": return getBootstypename();
			case "CHAINMAIL_HELMET": return getHelmtypename();
			case "CHAINMAIL_CHESTPLATE": return getChesttypename();
			case "CHAINMAIL_LEGGINGS": return getLegstypename();
			case "CHAINMAIL_BOOTS": return getBootstypename();
			case "IRON_HELMET": return getHelmtypename();
			case "IRON_CHESTPLATE": return getChesttypename();
			case "IRON_LEGGINGS": return getLegstypename();
			case "IRON_BOOTS": return getBootstypename();
			case "DIAMOND_HELMET": return getHelmtypename();
			case "DIAMOND_CHESTPLATE": return getChesttypename();
			case "DIAMOND_LEGGINGS": return getLegstypename();
			case "DIAMOND_BOOTS": return getBootstypename();
			case "GOLD_HELMET": return getHelmtypename();
			case "GOLD_CHESTPLATE": return getChesttypename();
			case "GOLD_LEGGINGS": return getLegstypename();
			case "GOLD_BOOTS": return getBootstypename();
			case "WOOD_SWORD": return getSwordtypename(); 
			case "STONE_SWORD": return getSwordtypename(); 
			case "IRON_SWORD": return getSwordtypename(); 
			case "GOLD_SWORD": return getSwordtypename(); 
			case "DIAMOND_SWORD": return getSwordtypename(); 
			case "WOOD_AXE": return getAxetypename(); 
			case "STONE_AXE": return getAxetypename(); 
			case "IRON_AXE": return getAxetypename(); 
			case "GOLD_AXE": return getAxetypename();
			case "DIAMOND_AXE": return getAxetypename(); 
			case "WOOD_SPADE": return getSpadetypename(); 
			case "STONE_SPADE": return getSpadetypename(); 
			case "IRON_SPADE": return getSpadetypename(); 
			case "GOLD_SPADE": return getSpadetypename(); 
			case "DIAMOND_SPADE": return getSpadetypename(); 
			case "SHIELD": return getShieldtypename();
			case "BOW": return getBowtypename();
			
			default: return "unknown";
		}
	}

	@Override
	public int getItemGenerationBonus(String string) {
		switch(string.toLowerCase())
		{
			case "strength":
				return this.getStrengthitembonus();
			case "stamina":
				return this.getStaminaitembonus();
			case "dexterity":
				return this.getDexterityitembonus();
			case "agility":
				return this.getAgilityitembonus();
			case "intelligence":
				return this.getIntelligenceitembonus();
			case "wisdom":
				return this.getWisdomitembonus();
			case "charisma":
				return this.getCharismaitembonus();
			default:
				return 0;
		}
	}

	public String getHelmtypename() {
		return helmtypename;
	}

	public void setHelmtypename(String helmtypename) {
		this.helmtypename = helmtypename;
	}

	public String getChesttypename() {
		return chesttypename;
	}

	public void setChesttypename(String chesttypename) {
		this.chesttypename = chesttypename;
	}

	public String getLegstypename() {
		return legstypename;
	}

	public void setLegstypename(String legstypename) {
		this.legstypename = legstypename;
	}

	public String getBootstypename() {
		return bootstypename;
	}

	public void setBootstypename(String bootstypename) {
		this.bootstypename = bootstypename;
	}

	@Override
	public String getDefaulthandMaterial() {
		return defaulthandMaterial;
	}

	@Override
	public void setDefaulthandMaterial(String defaulthandMaterial) {
		this.defaulthandMaterial = defaulthandMaterial;
	}

	@Override
	public String getDefaultoffHandMaterial() {
		return defaultoffHandMaterial;
	}

	@Override
	public void setDefaultoffHandMaterial(String defaultoffHandMaterial) {
		this.defaultoffHandMaterial = defaultoffHandMaterial;
	}

	@Override
	public String getSwordtypename() {
		return swordtypename;
	}

	@Override
	public void setSwordtypename(String swordtypename) {
		this.swordtypename = swordtypename;
	}

	@Override
	public String getShieldtypename() {
		return shieldtypename;
	}

	@Override
	public void setShieldtypename(String shieldtypename) {
		this.shieldtypename = shieldtypename;
	}

	@Override
	public String getAxetypename() {
		return axetypename;
	}

	@Override
	public void setAxetypename(String axetypename) {
		this.axetypename = axetypename;
	}

	@Override
	public String getSpadetypename() {
		return spadetypename;
	}

	@Override
	public void setSpadetypename(String spadetypename) {
		this.spadetypename = spadetypename;
	}

	@Override
	public String getBowtypename() {
		return bowtypename;
	}

	@Override
	public void setBowtypename(String bowtypename) {
		this.bowtypename = bowtypename;
	}

	@Override
	public boolean canDodge() {
		if (getDodgelevel() < 1)
			return false;
		
		return true;
	}

	@Override
	public boolean canRiposte() {
		if (getRipostelevel() < 1)
			return false;

		return true;
	}

	@Override
	public boolean canDoubleAttack() {
		if (getDoubleattacklevel() < 1)
			return false;

		return true;
	}

	@Override
	public int getDodgelevel() {
		return dodgelevel;
	}

	@Override
	public void setDodgelevel(int dodgelevel) {
		this.dodgelevel = dodgelevel;
	}

	@Override
	public int getRipostelevel() {
		return ripostelevel;
	}

	@Override
	public void setRipostelevel(int ripostelevel) {
		this.ripostelevel = ripostelevel;
	}

	@Override
	public int getDoubleattacklevel() {
		return doubleattacklevel;
	}

	@Override
	public void setDoubleattacklevel(int doubleattacklevel) {
		this.doubleattacklevel = doubleattacklevel;
	}

	@Override
	public int getSafefalllevel() {
		return safefalllevel;
	}

	@Override
	public void setSafefalllevel(int safefalllevel) {
		this.safefalllevel = safefalllevel;
	}
	
	@Override
	public boolean canSafeFall() {
		if (getSafefalllevel() < 1)
			return false;

		return true;
	}

	@Override
	public String getShortName() {
		return shortName;
	}

	@Override
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
}
