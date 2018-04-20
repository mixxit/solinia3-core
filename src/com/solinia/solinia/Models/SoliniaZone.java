package com.solinia.solinia.Models;

import org.bukkit.command.CommandSender;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidZoneSettingException;
import com.solinia.solinia.Interfaces.ISoliniaLootTable;
import com.solinia.solinia.Managers.StateManager;

import net.md_5.bungee.api.ChatColor;

public class SoliniaZone {
	private int id;
	private String name;
	private int x;
	private int y;
	private int z;
	private boolean operatorCreated = true;
	private boolean hotzone = false;
	private int succorx;
	private int succory;
	private int succorz;
	private int forestryMinSkill = 0;
	private int forestryLootTableId = 0;
	private int fishingMinSkill = 0;
	private int fishingLootTableId = 0;
	private int miningMinSkill = 0;
	private int miningLootTableId = 0;
	private int foragingMinSkill = 0;
	private int foragingLootTableId = 0;
	private int size = 500;
	private int manaRegen = 0;
	private int hpRegen = 0;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public int getZ() {
		return z;
	}
	public void setX(int x) {
		this.x = x;
	}
	public void setY(int y) {
		this.y = y;
	}
	public void setZ(int z) {
		this.z = z;
	}
	public void setOperatorCreated(boolean operatorCreated) {
		this.operatorCreated = operatorCreated;
	}
	
	public boolean isOperatorCreated()
	{
		return this.operatorCreated;
	}
	
	public boolean isHotzone() {
		return hotzone;
	}
	public void setHotzone(boolean hotzone) {
		this.hotzone = hotzone;
	}
	
	public void sendZoneSettingsToSender(CommandSender sender) throws CoreStateInitException {
		sender.sendMessage(ChatColor.RED + "Zone Settings for " + ChatColor.GOLD + getName() + ChatColor.RESET);
		sender.sendMessage("----------------------------");
		sender.sendMessage("- id: " + ChatColor.GOLD + getId() + ChatColor.RESET);
		sender.sendMessage("- name: " + ChatColor.GOLD + getName() + ChatColor.RESET);
		sender.sendMessage("- x: " + ChatColor.GOLD + getX() + ChatColor.RESET);
		sender.sendMessage("- y: " + ChatColor.GOLD + getY() + ChatColor.RESET);
		sender.sendMessage("- z: " + ChatColor.GOLD + getZ() + ChatColor.RESET);
		sender.sendMessage("- size: " + ChatColor.GOLD + getSize() + ChatColor.RESET);
		sender.sendMessage("- hotzone: " + ChatColor.GOLD + isHotzone() + ChatColor.RESET);
		sender.sendMessage("- succorx: " + ChatColor.GOLD + getSuccorx() + ChatColor.RESET);
		sender.sendMessage("- succory: " + ChatColor.GOLD + getSuccory() + ChatColor.RESET);
		sender.sendMessage("- succorz: " + ChatColor.GOLD + getSuccorz() + ChatColor.RESET);
		sender.sendMessage("- forestryminskill: " + ChatColor.GOLD + getForestryMinSkill() + ChatColor.RESET);
		sender.sendMessage("- fishingminskill: " + ChatColor.GOLD + getFishingMinSkill() + ChatColor.RESET);
		sender.sendMessage("- miningminskill: " + ChatColor.GOLD + getMiningMinSkill() + ChatColor.RESET);
		sender.sendMessage("- foragingminskill: " + ChatColor.GOLD + getForagingMinSkill() + ChatColor.RESET);
		sender.sendMessage("- manaregen: " + ChatColor.GOLD + getManaRegen() + ChatColor.RESET);
		sender.sendMessage("- hpregen: " + ChatColor.GOLD + getHpRegen() + ChatColor.RESET);
		
		if (getForagingLootTableId() != 0) {
			sender.sendMessage("- foragingloottableid: " + ChatColor.GOLD + getForagingLootTableId() + " ("
					+ StateManager.getInstance().getConfigurationManager().getLootTable(getForagingLootTableId()).getName()
					+ ")" + ChatColor.RESET);
		} else {
			sender.sendMessage(
					"- foragingloottableid: " + ChatColor.GOLD + getForagingLootTableId() + " (No Loot Table)" + ChatColor.RESET);
		}		
		
		if (getForestryLootTableId() != 0) {
			sender.sendMessage("- forestryloottableid: " + ChatColor.GOLD + getForestryLootTableId() + " ("
					+ StateManager.getInstance().getConfigurationManager().getLootTable(getForestryLootTableId()).getName()
					+ ")" + ChatColor.RESET);
		} else {
			sender.sendMessage(
					"- forestryloottableid: " + ChatColor.GOLD + getForestryLootTableId() + " (No Loot Table)" + ChatColor.RESET);
		}

		if (getFishingLootTableId() != 0) {
			sender.sendMessage("- fishingloottableid: " + ChatColor.GOLD + getFishingLootTableId() + " ("
					+ StateManager.getInstance().getConfigurationManager().getLootTable(getFishingLootTableId()).getName()
					+ ")" + ChatColor.RESET);
		} else {
			sender.sendMessage(
					"- fishingloottableid: " + ChatColor.GOLD + getFishingLootTableId() + " (No Loot Table)" + ChatColor.RESET);
		}

		if (getMiningLootTableId() != 0) {
			sender.sendMessage("- miningloottableid: " + ChatColor.GOLD + getMiningLootTableId() + " ("
					+ StateManager.getInstance().getConfigurationManager().getLootTable(getMiningLootTableId()).getName()
					+ ")" + ChatColor.RESET);
		} else {
			sender.sendMessage(
					"- miningloottableid: " + ChatColor.GOLD + getMiningLootTableId() + " (No Loot Table)" + ChatColor.RESET);
		}
	}

	public void editSetting(String setting, String value)
			throws InvalidZoneSettingException, NumberFormatException, CoreStateInitException {

		switch (setting.toLowerCase()) {
		case "name":
			if (value.equals(""))
				throw new InvalidZoneSettingException("Name is empty");
			setName(value);
			break;
		case "x":
			setX(Integer.parseInt(value));
			break;
		case "y":
			setY(Integer.parseInt(value));
			break;
		case "z":
			setZ(Integer.parseInt(value));
			break;
		case "hotzone":
			setHotzone(Boolean.parseBoolean(value));
			break;
		case "size":
			setSize(Integer.parseInt(value));
			break;
		case "succorx":
			setSuccorx(Integer.parseInt(value));
			break;
		case "succory":
			setSuccory(Integer.parseInt(value));
			break;
		case "succorz":
			setSuccorz(Integer.parseInt(value));
			break;
		case "hpregen":
			setHpRegen(Integer.parseInt(value));
			break;
		case "manaregen":
			setManaRegen(Integer.parseInt(value));
			break;
		case "forestryminskill":
			setForestryMinSkill(Integer.parseInt(value));
			break;
		case "miningminskill":
			setMiningMinSkill(Integer.parseInt(value));
			break;
		case "fishingminskill":
			setFishingMinSkill(Integer.parseInt(value));
			break;
		case "foragingminskill":
			setForagingMinSkill(Integer.parseInt(value));
			break;
		case "foragingloottableid":
			if (Integer.parseInt(value) == 0)
			{
				setForagingLootTableId(0);
				break;
			}
			
			ISoliniaLootTable loottable0 = StateManager.getInstance().getConfigurationManager()
			.getLootTable(Integer.parseInt(value));
			if (loottable0 == null)
				throw new InvalidZoneSettingException("Loottable ID does not exist");
			setForagingLootTableId(Integer.parseInt(value));
			break;
		case "forestryloottableid":
			if (Integer.parseInt(value) == 0)
			{
				setForestryLootTableId(0);
				break;
			}
			
			ISoliniaLootTable loottable1 = StateManager.getInstance().getConfigurationManager()
			.getLootTable(Integer.parseInt(value));
			if (loottable1 == null)
				throw new InvalidZoneSettingException("Loottable ID does not exist");
			setForestryLootTableId(Integer.parseInt(value));
			break;
		case "fishingloottableid":
			if (Integer.parseInt(value) == 0)
			{
				setFishingLootTableId(0);
				break;
			}
			
			ISoliniaLootTable loottable2 = StateManager.getInstance().getConfigurationManager()
			.getLootTable(Integer.parseInt(value));
			if (loottable2 == null)
				throw new InvalidZoneSettingException("Loottable ID does not exist");
			setFishingLootTableId(Integer.parseInt(value));
			break;
		case "miningloottableid":
			if (Integer.parseInt(value) == 0)
			{
				setMiningLootTableId(0);
				break;
			}
			
			ISoliniaLootTable loottable3 = StateManager.getInstance().getConfigurationManager()
			.getLootTable(Integer.parseInt(value));
			if (loottable3 == null)
				throw new InvalidZoneSettingException("Loottable ID does not exist");
			setMiningLootTableId(Integer.parseInt(value));
			break;
		default:
			throw new InvalidZoneSettingException(
					"Invalid zone setting. Valid Options are: name,x,y,z,hotzone,succorx,succory,succorz,forestryloottableid,fishingloottableid,miningloottableid,forestryminskill,miningminskill,fishingminskill");
		}
	}
	public int getSuccorx() {
		return succorx;
	}
	public void setSuccorx(int succorx) {
		this.succorx = succorx;
	}
	public int getSuccory() {
		return succory;
	}
	public void setSuccory(int succory) {
		this.succory = succory;
	}
	public int getSuccorz() {
		return succorz;
	}
	public void setSuccorz(int succorz) {
		this.succorz = succorz;
	}
	public int getForestryLootTableId() {
		return forestryLootTableId;
	}
	public void setForestryLootTableId(int forestryLootTableId) {
		this.forestryLootTableId = forestryLootTableId;
	}
	public int getMiningLootTableId() {
		return miningLootTableId;
	}
	public void setMiningLootTableId(int miningLootTableId) {
		this.miningLootTableId = miningLootTableId;
	}
	public int getFishingLootTableId() {
		return fishingLootTableId;
	}
	public void setFishingLootTableId(int fishingLootTableId) {
		this.fishingLootTableId = fishingLootTableId;
	}
	public int getFishingMinSkill() {
		return fishingMinSkill;
	}
	public void setFishingMinSkill(int fishingMinSkill) {
		this.fishingMinSkill = fishingMinSkill;
	}
	public int getMiningMinSkill() {
		return miningMinSkill;
	}
	public void setMiningMinSkill(int miningMinSkill) {
		this.miningMinSkill = miningMinSkill;
	}
	public int getForestryMinSkill() {
		return forestryMinSkill;
	}
	public void setForestryMinSkill(int forestryMinSkill) {
		this.forestryMinSkill = forestryMinSkill;
	}
	public int getForagingLootTableId() {
		return foragingLootTableId;
	}
	public void setForagingLootTableId(int foragingLootTableId) {
		this.foragingLootTableId = foragingLootTableId;
	}
	public int getForagingMinSkill() {
		return foragingMinSkill;
	}
	public void setForagingMinSkill(int foragingMinSkill) {
		this.foragingMinSkill = foragingMinSkill;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public int getManaRegen() {
		return manaRegen;
	}
	public void setManaRegen(int manaRegen) {
		this.manaRegen = manaRegen;
	}
	public int getHpRegen() {
		return hpRegen;
	}
	public void setHpRegen(int hpRegen) {
		this.hpRegen = hpRegen;
	}
}
