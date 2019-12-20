package com.solinia.solinia.Models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Color;
import org.bukkit.command.CommandSender;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidClassSettingException;
import com.solinia.solinia.Interfaces.ISoliniaClass;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaRace;
import com.solinia.solinia.Managers.ConfigurationManager;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Utils.ColorUtil;

import net.md_5.bungee.api.ChatColor;

public class SoliniaClass implements ISoliniaClass {

	private int id;
	private boolean isadmin = true;
	private String name = "";
	private int leatherRgbDecimal = -1;
	private String description = "";
	private ConcurrentHashMap<Integer, RaceClass> validRaceClasses = new ConcurrentHashMap<Integer, RaceClass>();
	private String defaultHeadMaterial = "LEATHER_HELMET";
	private String defaultChestMaterial = "LEATHER_CHESTPLATE";
	private String defaultLegsMaterial = "LEATHER_LEGGINGS";
	private String defaultFeetMaterial = "LEATHER_BOOTS";
	private String defaulthandMaterial = "WOODEN_SWORD";
	private ItemType defaultHandItemType = ItemType.OneHandSlashing;
	private String defaultoffHandMaterial = "SHIELD";
	private ItemType defaultOffHandItemType = ItemType.None;
	private String defaultAlternateHandMaterial = "NONE";
	private ItemType defaultAlternateHandItemType = ItemType.None;
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
	private int dualwieldlevel = 0;
	private String shortName = "";
	private String classItemPrefix = "";
	private int acitembonus = 0;
	private int specialiselevel = 0;
	private int npcspelllist = 0;
	private boolean sneakFromCrouch = false;
	private String level51Title = "";
	private String level55Title = "";
	private String level60Title = "";
	private List<Integer> oaths = new ArrayList<Integer>();
	private int weaponDelayItemBonus = 0;
	private boolean canPray = false;
	
	
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
		sender.sendMessage("- id: " + ChatColor.GOLD + getId() + ChatColor.RESET + " name: " + ChatColor.GOLD + getName() + ChatColor.RESET);
		sender.sendMessage("- shortname: " + ChatColor.GOLD + getShortName() + ChatColor.RESET + " admin: " + ChatColor.GOLD + isAdmin() + ChatColor.RESET);
		sender.sendMessage("- description: " + ChatColor.GOLD + getDescription() + ChatColor.RESET);
		sender.sendMessage("----------------------------");
		sender.sendMessage("- defaultheadmaterial: " + ChatColor.GOLD + getDefaultHeadMaterial() + ChatColor.RESET);
		sender.sendMessage("- defaultchestmaterial: " + ChatColor.GOLD + getDefaultChestMaterial() + ChatColor.RESET);
		sender.sendMessage("- defaultlegsmaterial: " + ChatColor.GOLD + getDefaultLegsMaterial() + ChatColor.RESET);
		sender.sendMessage("- defaultfeetmaterial: " + ChatColor.GOLD + getDefaultFeetMaterial() + ChatColor.RESET);
		sender.sendMessage("- defaulthandmaterial: " + ChatColor.GOLD + getDefaulthandMaterial() + ChatColor.RESET);
		sender.sendMessage("- defaultoffhandmaterial: " + ChatColor.GOLD + getDefaultoffHandMaterial() + ChatColor.RESET);
		sender.sendMessage("- defaultalternatehandmaterial: " + ChatColor.GOLD + getDefaultAlternateHandMaterial() + ChatColor.RESET);
		sender.sendMessage("- defaulthanditemtype: " + ChatColor.GOLD + getDefaultHandItemType().name() + ChatColor.RESET);
		sender.sendMessage("- defaultoffhanditemtype: " + ChatColor.GOLD + getDefaultOffHandItemType().name() + ChatColor.RESET);
		sender.sendMessage("- defaultalternatehanditemtype: " + ChatColor.GOLD + getDefaultAlternateHandItemType().name() + ChatColor.RESET);
		sender.sendMessage("----------------------------");
		sender.sendMessage("- strengthitembonus: " + ChatColor.GOLD + getStrengthitembonus() + ChatColor.RESET);
		sender.sendMessage("- staminaitembonus: " + ChatColor.GOLD + getStaminaitembonus() + ChatColor.RESET);
		sender.sendMessage("- agilityitembonus: " + ChatColor.GOLD + getAgilityitembonus() + ChatColor.RESET);
		sender.sendMessage("- dexterityitembonus: " + ChatColor.GOLD + getDexterityitembonus() + ChatColor.RESET);
		sender.sendMessage("- intelligenceitembonus: " + ChatColor.GOLD + getIntelligenceitembonus() + ChatColor.RESET);
		sender.sendMessage("- wisdomitembonus: " + ChatColor.GOLD + getWisdomitembonus() + ChatColor.RESET);
		sender.sendMessage("- charismaitembonus: " + ChatColor.GOLD + getCharismaitembonus() + ChatColor.RESET);
		sender.sendMessage("- acitembonus: " + ChatColor.GOLD + getACItemBonus() + ChatColor.RESET);
		sender.sendMessage("- weapondelayitembonus: " + ChatColor.GOLD + getWeaponDelayItemBonus() + ChatColor.RESET);
		sender.sendMessage("----------------------------");
		sender.sendMessage("- classitemprefix: " + ChatColor.GOLD + getClassItemPrefix() + ChatColor.RESET);
		sender.sendMessage("- helmtypename: " + ChatColor.GOLD + getHelmtypename() + ChatColor.RESET);
		sender.sendMessage("- chesttypename: " + ChatColor.GOLD + getChesttypename() + ChatColor.RESET);
		sender.sendMessage("- legstypename: " + ChatColor.GOLD + getLegstypename() + ChatColor.RESET);
		sender.sendMessage("- bootstypename: " + ChatColor.GOLD + getBootstypename() + ChatColor.RESET);
		sender.sendMessage("- swordtypename: " + ChatColor.GOLD + getSwordtypename() + ChatColor.RESET);
		sender.sendMessage("- axetypename: " + ChatColor.GOLD + getAxetypename() + ChatColor.RESET);
		sender.sendMessage("- spadetypename: " + ChatColor.GOLD + getSpadetypename() + ChatColor.RESET);
		sender.sendMessage("- shieldtypename: " + ChatColor.GOLD + getShieldtypename() + ChatColor.RESET);
		sender.sendMessage("- bowtypename: " + ChatColor.GOLD + getBowtypename() + ChatColor.RESET);
		String leathercolor = "NONE";
		if (getLeatherRgbDecimal() > 0)
		{
			Color colorTmp = Color.fromRGB(getLeatherRgbDecimal());
			leathercolor = ColorUtil.fromRGB(colorTmp.getRed(),colorTmp.getGreen(), colorTmp.getBlue()) + "(Closest)" + ChatColor.RESET;
		}
		sender.sendMessage("- leatherrgbdecimal: " + ChatColor.GOLD + getLeatherRgbDecimal() + ChatColor.RESET + leathercolor + " See: https://bit.ly/2i02I8k");
		sender.sendMessage("----------------------------");
		sender.sendMessage("- dodgelevel: " + ChatColor.GOLD + getDodgelevel() + ChatColor.RESET);
		sender.sendMessage("- ripostelevel: " + ChatColor.GOLD + getRipostelevel() + ChatColor.RESET);
		sender.sendMessage("- doubleattacklevel: " + ChatColor.GOLD + getDoubleattacklevel() + ChatColor.RESET);
		sender.sendMessage("- safefalllevel: " + ChatColor.GOLD + getSafefalllevel() + ChatColor.RESET);
		sender.sendMessage("- specialiselevel: " + ChatColor.GOLD + getSpecialiselevel() + ChatColor.RESET);
		sender.sendMessage("- dualwieldlevel: " + ChatColor.GOLD + getDualwieldlevel() + ChatColor.RESET);
		sender.sendMessage("- canpray: " + ChatColor.GOLD + canPray() + ChatColor.RESET);
		sender.sendMessage("- sneakfromcrouch: " + ChatColor.GOLD + isSneakFromCrouch() + ChatColor.RESET);
		sender.sendMessage("- oaths: " + ChatColor.GOLD + getOaths().toString() + ChatColor.RESET);
		sender.sendMessage("----------------------------");
		sender.sendMessage("- level51title: " + ChatColor.GOLD + getLevel51Title() + ChatColor.RESET);
		sender.sendMessage("- level55title: " + ChatColor.GOLD + getLevel55Title() + ChatColor.RESET);
		sender.sendMessage("- level60title: " + ChatColor.GOLD + getLevel60Title() + ChatColor.RESET);
		if (StateManager.getInstance().getConfigurationManager().getNPCSpellList(getNpcspelllist()) != null)
		{
			sender.sendMessage("- npcspelllist: " + ChatColor.GOLD + getNpcspelllist() + "(" + StateManager.getInstance().getConfigurationManager().getNPCSpellList(getNpcspelllist()).getName() + ")" + ChatColor.RESET);
		} else {
			sender.sendMessage("- npcspelllist: " + ChatColor.GOLD + getNpcspelllist() + ChatColor.RESET);
		}
		sender.sendMessage("----------------------------");
		sender.sendMessage("Allowed Races:");
		for(Integer raceId : getValidRaceClasses().keySet())
		{
			RaceClass raceClass = getValidRaceClasses().get(raceId);
			ISoliniaRace race = StateManager.getInstance().getConfigurationManager().getRace(raceClass.RaceId);
			sender.sendMessage(raceId + " " + race.getName() + " startlocation: " + raceId + "," + raceClass.getStartWorld() + "," + raceClass.getStartX() + "," + raceClass.getStartY() + "," + raceClass.getStartZ());
		}
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
			if (!ConfigurationManager.ArmourMaterials.contains(value.toUpperCase()))
				throw new InvalidClassSettingException("Invalid material type");
			setDefaultHeadMaterial(value.toUpperCase());
			break;
		case "defaultchestmaterial":
			if (!ConfigurationManager.ArmourMaterials.contains(value.toUpperCase()))
				throw new InvalidClassSettingException("Invalid material type");
			setDefaultChestMaterial(value.toUpperCase());
			break;
		case "defaultlegsmaterial":
			if (!ConfigurationManager.ArmourMaterials.contains(value.toUpperCase()))
				throw new InvalidClassSettingException("Invalid material type");
			setDefaultLegsMaterial(value.toUpperCase());
			break;
		case "defaultfeetmaterial":
			if (!ConfigurationManager.ArmourMaterials.contains(value.toUpperCase()))
				throw new InvalidClassSettingException("Invalid material type");
			setDefaultFeetMaterial(value.toUpperCase());
			break;
		case "defaulthandmaterial":
			if (!ConfigurationManager.HandMaterials.contains(value.toUpperCase()))
				throw new InvalidClassSettingException("Invalid material type");
			setDefaulthandMaterial(value.toUpperCase());
			break;
		case "defaultoffhandmaterial":
			if (!ConfigurationManager.HandMaterials.contains(value.toUpperCase()))
				throw new InvalidClassSettingException("Invalid material type");
			setDefaultoffHandMaterial(value.toUpperCase());
			break;
		case "leatherrgbdecimal":
			setLeatherRgbDecimal(Integer.parseInt(value));
			updateAllLeatherGear();
			break;
		case "defaultalternatehandmaterial":
			if (!ConfigurationManager.HandMaterials.contains(value.toUpperCase()))
				throw new InvalidClassSettingException("Invalid material type");
			setDefaultAlternateHandMaterial(value.toUpperCase());
			break;
		case "canpray":
			setCanPray(Boolean.valueOf(value));
			break;
		case "defaulthanditemtype":
			setDefaultHandItemType(ItemType.valueOf(value));
			break;
		case "defaultoffhanditemtype":
			setDefaultOffHandItemType(ItemType.valueOf(value));
			break;
		case "defaultalternatehanditemtype":
			setDefaultAlternateHandItemType(ItemType.valueOf(value));
			break;
		case "strengthitembonus":
			this.setStrengthitembonus(Integer.parseInt(value));
			break;
		case "shortname":
			this.setShortName(value);
			break;
		case "admin":
			this.setAdmin(Boolean.parseBoolean(value));
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
		case "acitembonus":
			this.setACItemBonus(Integer.parseInt(value));
			break;
		case "weapondelayitembonus":
			this.setWeaponDelayItemBonus(Integer.parseInt(value));
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
		case "dualwieldlevel":
			this.setDualwieldlevel(Integer.parseInt(value));
			break;
		case "safefalllevel":
			this.setSafefalllevel(Integer.parseInt(value));
			break;
		case "classitemprefix":
			this.setClassItemPrefix(value);
			break;
		case "specialiselevel":
			this.setSpecialiselevel(Integer.parseInt(value));
			break;
		case "npcspelllist":
			this.setNpcspelllist(Integer.parseInt(value));
			break;
		case "sneakfromcrouch":
			this.setSneakFromCrouch(Boolean.parseBoolean(value));
			break;
		case "description":
			this.setDescription(value);
			break;
		case "level51title":
			this.setLevel51Title(value);
			break;
		case "level55title":
			this.setLevel55Title(value);
			break;
		case "level60title":
			this.setLevel60Title(value);
			break;
		case "startlocationall":
			try {
				String[] zonedata = value.split(",");
				// Dissasemble the value to ensure it is correct
				String world = zonedata[0];
				int x = Integer.parseInt(zonedata[1]);
				int y = Integer.parseInt(zonedata[2]);
				int z = Integer.parseInt(zonedata[3]);

				for(Integer raceId : this.getValidRaceClasses().keySet())
				{
					this.getRaceClass(raceId).setStartWorld(world);
					this.getRaceClass(raceId).setStartX(x);
					this.getRaceClass(raceId).setStartY(y);
					this.getRaceClass(raceId).setStartZ(z);
				}
				break;
			} catch (Exception e) {
				throw new InvalidClassSettingException("Invalid Class setting. startlocation value must be in format: world,x,y,z [" + e.getMessage() + "]");
			}
		case "startlocation":
			try {
				String[] zonedata = value.split(",");
				// Dissasemble the value to ensure it is correct
				Integer raceId = Integer.parseInt(zonedata[0]);
				String world = zonedata[1];
				int x = Integer.parseInt(zonedata[2]);
				int y = Integer.parseInt(zonedata[3]);
				int z = Integer.parseInt(zonedata[4]);

				this.getRaceClass(raceId).setStartWorld(world);
				this.getRaceClass(raceId).setStartX(x);
				this.getRaceClass(raceId).setStartY(y);
				this.getRaceClass(raceId).setStartZ(z);
				break;
			} catch (Exception e) {
				throw new InvalidClassSettingException("Invalid Class setting. startlocation value must be in format: raceid, world,x,y,z [" + e.getMessage() + "]");
			}
		case "oaths":
			List<Integer> oathIdsList = new ArrayList<Integer>();
			int[] oathIds = Arrays.stream(value.split(",")).mapToInt(Integer::parseInt).toArray();
			for(int i = 0; i < oathIds.length; i++)
			{
				oathIdsList.add(oathIds[i]);
			}
			
			this.setOaths(oathIdsList);
			break;
		default:
			throw new InvalidClassSettingException("Invalid Class setting. Valid Options are: name, defaultheadmaterial, defaultchestmaterial,defaultlegsmaterial,defaultfeetmaterial,classitemprefix,specialiselevel,level51title,level55title,level60title");
		}
	}

	private void updateAllLeatherGear() {
		try
		{
			int count = 0;
			for(ISoliniaItem item : StateManager.getInstance().getConfigurationManager().getItems())
			{
				if (
						!item.getBasename().toUpperCase().equals("LEATHER_HELMET") &&
						!item.getBasename().toUpperCase().equals("LEATHER_CHESTPLATE") &&
						!item.getBasename().toUpperCase().equals("LEATHER_LEGGINGS") &&
						!item.getBasename().toUpperCase().equals("LEATHER_BOOTS")
						)
					continue;
				
				if (item.getAllowedClassNames().size() != 1)
					continue;
				
				if (!item.getAllowedClassNames().contains(this.getName().toUpperCase()))
					continue;
				
				item.setLeatherRgbDecimal(this.getLeatherRgbDecimal());
				item.setLastUpdatedTimeNow();
				count++;
			}
			
			StateManager.getInstance().getConfigurationManager().setItemsChanged(true);
			System.out.println("Updated " + count + " class items with new leather color");
		} catch (CoreStateInitException e)
		{
			
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
			case "GOLDEN_HELMET": return getHelmtypename();
			case "GOLDEN_CHESTPLATE": return getChesttypename();
			case "GOLDEN_LEGGINGS": return getLegstypename();
			case "GOLDEN_BOOTS": return getBootstypename();
			case "WOOD_SWORD": return getSwordtypename(); 
			case "WOODEN_SWORD": return getSwordtypename(); 
			case "STONE_SWORD": return getSwordtypename(); 
			case "IRON_SWORD": return getSwordtypename(); 
			case "GOLD_SWORD": return getSwordtypename(); 
			case "GOLDEN_SWORD": return getSwordtypename(); 
			case "DIAMOND_SWORD": return getSwordtypename(); 
			case "WOODEN_AXE": return getAxetypename(); 
			case "WOOD_AXE": return getAxetypename(); 
			case "STONE_AXE": return getAxetypename(); 
			case "IRON_AXE": return getAxetypename(); 
			case "GOLD_AXE": return getAxetypename();
			case "GOLDEN_AXE": return getAxetypename();
			case "DIAMOND_AXE": return getAxetypename(); 
			case "WOODEN_SHOVEL": return getSpadetypename(); 
			case "WOOD_SHOVEL": return getSpadetypename(); 
			case "STONE_SHOVEL": return getSpadetypename(); 
			case "IRON_SHOVEL": return getSpadetypename(); 
			case "GOLD_SHOVEL": return getSpadetypename(); 
			case "GOLDEN_SHOVEL": return getSpadetypename(); 
			case "DIAMOND_SHOVEL": return getSpadetypename(); 
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
			case "ac":
				return this.getACItemBonus();
			case "weapondelay":
				return this.getWeaponDelayItemBonus();
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
	public boolean canDualWield() {
		if (getDualwieldlevel() < 1)
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

	@Override
	public boolean isWarriorClass() {
		switch(getName())
		{
			case "WARRIOR":
			case "WARRIORGM":
			case "ROGUE":
			case "ROGUEGM":
			case "MONK":
			case "MONKGM":
			case "PALADIN":
			case "PALADINGM":
			case "SHADOWKNIGHT":
			case "SHADOWKNIGHTGM":
			case "RANGER":
			case "RANGERGM":
			case "BEASTLORD":
			case "BEASTLORDGM":
			case "BERSERKER":
			case "BERSERKERGM":
			case "BARD":
			case "BARDGM":
			{
				return true;
			}
			default:
			{
				return false;
			}
		}
	}

	@Override
	public String getClassItemPrefix() {
		return classItemPrefix;
	}

	@Override
	public void setClassItemPrefix(String classItemPrefix) {
		this.classItemPrefix = classItemPrefix;
	}
	
	@Override
	public int getACItemBonus() {
		return acitembonus;
	}

	@Override
	public void setACItemBonus(int acitembonus) {
		this.acitembonus = acitembonus;
	}

	@Override
	public int getSpecialiselevel() {
		return specialiselevel;
	}

	@Override
	public void setSpecialiselevel(int specialiselevel) {
		this.specialiselevel = specialiselevel;
	}

	@Override
	public int getNpcspelllist() {
		return npcspelllist;
	}

	@Override
	public void setNpcspelllist(int npcspelllist) {
		this.npcspelllist = npcspelllist;
	}

	@Override
	public boolean isSneakFromCrouch() {
		return sneakFromCrouch;
	}

	@Override
	public void setSneakFromCrouch(boolean sneakFromCrouch) {
		this.sneakFromCrouch = sneakFromCrouch;
	}

	@Override
	public String getLevel51Title() {
		return level51Title;
	}

	@Override
	public void setLevel51Title(String level51Title) {
		this.level51Title = level51Title;
	}

	@Override
	public String getLevel55Title() {
		return level55Title;
	}

	@Override
	public void setLevel55Title(String level55Title) {
		this.level55Title = level55Title;
	}

	@Override
	public String getLevel60Title() {
		return level60Title;
	}

	@Override
	public void setLevel60Title(String level60Title) {
		this.level60Title = level60Title;
	}

	@Override
	public int getDualwieldlevel() {
		return dualwieldlevel;
	}

	@Override
	public void setDualwieldlevel(int dualwieldlevel) {
		this.dualwieldlevel = dualwieldlevel;
	}

	@Override
	public List<Integer> getOaths() {
		return oaths;
	}

	@Override
	public void setOaths(List<Integer> oaths) {
		this.oaths = oaths;
	}

	@Override
	public ItemType getDefaultHandItemType() {
		return defaultHandItemType;
	}

	@Override
	public void setDefaultHandItemType(ItemType defaultHandItemType) {
		this.defaultHandItemType = defaultHandItemType;
	}

	@Override
	public ItemType getDefaultOffHandItemType() {
		return defaultOffHandItemType;
	}

	@Override
	public void setDefaultOffHandItemType(ItemType defaultOffHandItemType) {
		this.defaultOffHandItemType = defaultOffHandItemType;
	}

	@Override
	public int getWeaponDelayItemBonus() {
		return weaponDelayItemBonus;
	}

	@Override
	public void setWeaponDelayItemBonus(int weaponDelayItemBonus) {
		this.weaponDelayItemBonus = weaponDelayItemBonus;
	}

	@Override
	public String getDefaultAlternateHandMaterial() {
		return defaultAlternateHandMaterial;
	}

	@Override
	public void setDefaultAlternateHandMaterial(String defaultAlternateHandMaterial) {
		this.defaultAlternateHandMaterial = defaultAlternateHandMaterial;
	}

	@Override
	public ItemType getDefaultAlternateHandItemType() {
		return defaultAlternateHandItemType;
	}

	@Override
	public void setDefaultAlternateHandItemType(ItemType defaultAlternateHandItemType) {
		this.defaultAlternateHandItemType = defaultAlternateHandItemType;
	}

	@Override
	public boolean canPray() {
		return canPray;
	}

	@Override
	public void setCanPray(boolean canPray) {
		this.canPray = canPray;
	}

	@Override
	public int getLeatherRgbDecimal() {
		return leatherRgbDecimal;
	}

	@Override
	public void setLeatherRgbDecimal(int leatherRgbDecimal) {
		this.leatherRgbDecimal = leatherRgbDecimal;
	}

	@Override
	public ConcurrentHashMap<Integer, RaceClass> getValidRaceClasses() {
		return validRaceClasses;
	}

	@Override
	public void setValidRaceClasses(ConcurrentHashMap<Integer, RaceClass> validRaceClasses) {
		this.validRaceClasses = validRaceClasses;
	}

	@Override
	public RaceClass getRaceClass(int raceId) {
		return getValidRaceClasses().get(raceId);
	}
}
