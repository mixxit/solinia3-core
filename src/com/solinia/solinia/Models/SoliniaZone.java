package com.solinia.solinia.Models;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidZoneSettingException;
import com.solinia.solinia.Interfaces.IPersistable;
import com.solinia.solinia.Interfaces.ISoliniaLootTable;
import com.solinia.solinia.Interfaces.ISoliniaRace;
import com.solinia.solinia.Interfaces.ISoliniaSpawnGroup;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Utils.SpellTargetType;
import com.solinia.solinia.Utils.Utils;

import net.md_5.bungee.api.ChatColor;

public class SoliniaZone implements IPersistable {
	private int id;
	private String name;
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
	private boolean noUnstuck = false;
	private int manaRegen = 0;
	private int hpRegen = 0;
	private String requiresAlignment = "NONE";
	private int requiresRaceId = 0;
	private int passiveAbilityId = 0;
	private String music;
	private String world = "world";
	private int bottomLeftCornerX = 0;
	private int bottomLeftCornerY = 0;
	private int bottomLeftCornerZ = 0;
	private int topRightCornerX = 0;
	private int topRightCornerY = 0;
	private int topRightCornerZ = 0;
	private int zoneExperienceModifier = 0;
	

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
		sender.sendMessage("- music: " + ChatColor.GOLD + getMusic() + ChatColor.RESET);
		sender.sendMessage("- world: " + ChatColor.GOLD + getWorld() + ChatColor.RESET);
		sender.sendMessage("- zoneexperiencemodifier: " + ChatColor.GOLD + this.getZoneExperienceModifier() + ChatColor.RESET);
		sender.sendMessage("- bottomleftcornerx: " + ChatColor.GOLD + this.getBottomLeftCornerX() + ChatColor.RESET);
		sender.sendMessage("- bottomleftcornery: " + ChatColor.GOLD + this.getBottomLeftCornerY() + ChatColor.RESET);
		sender.sendMessage("- bottomleftcornerz: " + ChatColor.GOLD + this.getBottomLeftCornerZ() + ChatColor.RESET);
		sender.sendMessage("- toprightcornerx: " + ChatColor.GOLD + this.getTopRightCornerX() + ChatColor.RESET);
		sender.sendMessage("- toprightcornery: " + ChatColor.GOLD + this.getTopRightCornerY() + ChatColor.RESET);
		sender.sendMessage("- toprightcornerz: " + ChatColor.GOLD + this.getTopRightCornerZ() + ChatColor.RESET);
		sender.sendMessage("- nounstuck: " + ChatColor.GOLD + isNoUnstuck() + ChatColor.RESET);
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
		sender.sendMessage("- requiresalignment: " + ChatColor.GOLD + getRequiresAlignment() + ChatColor.RESET);
		sender.sendMessage("- requiresraceid: " + ChatColor.GOLD + getRequiresRaceId() + ChatColor.RESET);
		
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
		
		if (getPassiveAbilityId() != 0) {
			sender.sendMessage("- passiveabilityid: " + ChatColor.GOLD + getPassiveAbilityId() + " ("
					+ StateManager.getInstance().getConfigurationManager().getSpell(getPassiveAbilityId()).getName()
					+ ")" + ChatColor.RESET);
		} else {
			sender.sendMessage(
					"- passiveabilityid: " + ChatColor.GOLD + getPassiveAbilityId() + " (No Ability)" + ChatColor.RESET);
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
		case "zoneexperiencemodifier":
			setZoneExperienceModifier(Integer.parseInt(value));
			break;
		case "requiresraceid":
			int raceid = Integer.parseInt(value);
			if (raceid < 1)
			{
				setRequiresRaceId(0);
				break;
			}
			try
			{
				ISoliniaRace race = StateManager.getInstance().getConfigurationManager().getRace(raceid);
				if (race == null)
					throw new InvalidZoneSettingException("Invalid race id");
			} catch (CoreStateInitException e)
			{
				throw new InvalidZoneSettingException("State not initialised");
			}
			setRequiresRaceId(raceid);
			break;
		case "world":
			setWorld(value);
			break;
		case "bottomleftcornerx":
			setBottomLeftCornerX(Integer.parseInt(value));
			break;
		case "bottomleftcornery":
			setBottomLeftCornerY(Integer.parseInt(value));
			break;
		case "bottomleftcornerz":
			setBottomLeftCornerZ(Integer.parseInt(value));
			break;
		case "toprightcornerx":
			setTopRightCornerX(Integer.parseInt(value));
			break;
		case "toprightcornery":
			setTopRightCornerY(Integer.parseInt(value));
			break;
		case "toprightcornerz":
			setTopRightCornerZ(Integer.parseInt(value));
			break;		case "hotzone":
			setHotzone(Boolean.parseBoolean(value));
			break;
		case "nounstuck":
			setNoUnstuck(Boolean.parseBoolean(value));
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
		case "music":
			setMusic(value);
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
		case "requiresalignment":
			if (!value.equals("GOOD") && !value.equals("NEUTRAL") && !value.equals("EVIL") && !value.equals("NONE"))
				throw new InvalidZoneSettingException("Invalid alignment - must be GOOD NEUTRAL EVIL or NONE");			
			setRequiresAlignment(value);
			break;
		case "passiveabilityid":
			int abilityid = Integer.parseInt(value);
			if (abilityid < 1)
			{
				setPassiveAbilityId(0);
				break;
			}
			try
			{
				ISoliniaSpell ability = StateManager.getInstance().getConfigurationManager().getSpell(abilityid);
				if (ability == null)
					throw new InvalidZoneSettingException("Invalid id");
				
				if (!ability.isBuffSpell() || !Utils.getSpellTargetType(ability.getTargettype()).equals(SpellTargetType.Self))
					throw new InvalidZoneSettingException("Only Self only buff type spells can be set as a passive spell");
				
			} catch (CoreStateInitException e)
			{
				throw new InvalidZoneSettingException("State not initialised");
			}
			setPassiveAbilityId(abilityid);
			break;
		default:
			throw new InvalidZoneSettingException(
					"Invalid zone setting. Valid Options are: name,bottomleftcornerx,bottomleftcornery,bottomleftcornerz,toprightcornerx,toprightcornery,toprightcornerz,requiresalignment,hotzone,succorx,succory,succorz,forestryloottableid,fishingloottableid,miningloottableid,forestryminskill,miningminskill,fishingminskill,music,nounstuck");
		}
	}
	
	private int getMidPoint(int x, int y)
	{
		return x/2 + y/2 + (x%2 + y%2)/2;
	}
	
	public int getMiddleX()
	{
		return getMidPoint(getBottomLeftCornerX(),getTopRightCornerX());
		
	}
	public int getMiddleY()
	{
		return getMidPoint(getBottomLeftCornerY(),getTopRightCornerY());
	}
	public int getMiddleZ()
	{
		return getMidPoint(getBottomLeftCornerZ(),getTopRightCornerZ());
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
	public String getRequiresAlignment() {
		return requiresAlignment;
	}
	public void setRequiresAlignment(String requiresAlignment) {
		this.requiresAlignment = requiresAlignment;
	}
	public int getRequiresRaceId() {
		return requiresRaceId;
	}
	public void setRequiresRaceId(int requiresRaceId) {
		this.requiresRaceId = requiresRaceId;
	}
	public int getPassiveAbilityId() {
		return passiveAbilityId;
	}
	public void setPassiveAbilityId(int passiveAbilityId) {
		this.passiveAbilityId = passiveAbilityId;
	}
	public boolean isNoUnstuck() {
		return noUnstuck;
	}
	public void setNoUnstuck(boolean noUnstuck) {
		this.noUnstuck = noUnstuck;
	}
	public String getMusic() {
		return music;
	}
	public void setMusic(String music) {
		this.music = music;
	}
	public List<ISoliniaSpawnGroup> getSpawnGroups() {
		List<ISoliniaSpawnGroup> spawnGroups = new ArrayList<ISoliniaSpawnGroup>();
		try
		{
			for(ISoliniaSpawnGroup spawnGroup : StateManager.getInstance().getConfigurationManager().getSpawnGroups())
			{
				if (!this.isLocationInside(spawnGroup.getLocation()))
					continue;
				
				spawnGroups.add(spawnGroup);
			}
		} catch (CoreStateInitException e)
		{
			
		}
		return spawnGroups;
	}
	public String getWorld() {
		return world;
	}
	
	public void setWorld(String world) {
		this.world = world;
	}

	public int getBottomLeftCornerX() {
		return bottomLeftCornerX;
	}
	
	public void setBottomLeftCornerX(int bottomLeftCornerX) {
		this.bottomLeftCornerX = bottomLeftCornerX;
	}
	
	public int getBottomLeftCornerY() {
		return bottomLeftCornerY;
	}
	
	public void setBottomLeftCornerY(int bottomLeftCornerY) {
		this.bottomLeftCornerY = bottomLeftCornerY;
	}
	
	public int getTopRightCornerX() {
		return topRightCornerX;
	}
	
	public void setTopRightCornerX(int topRightCornerX) {
		this.topRightCornerX = topRightCornerX;
	}
	
	public int getTopRightCornerY() {
		return topRightCornerY;
	}
	
	public void setTopRightCornerY(int topRightCornerY) {
		this.topRightCornerY = topRightCornerY;
	}
	public int getBottomLeftCornerZ() {
		return bottomLeftCornerZ;
	}
	public void setBottomLeftCornerZ(int bottomLeftCornerZ) {
		this.bottomLeftCornerZ = bottomLeftCornerZ;
	}
	public int getTopRightCornerZ() {
		return topRightCornerZ;
	}
	public void setTopRightCornerZ(int topRightCornerZ) {
		this.topRightCornerZ = topRightCornerZ;
	}
	
	public double getCornerDistances()
	{
		return new Location(Bukkit.getWorld(world), Double.valueOf(this.getBottomLeftCornerX()), this.getBottomLeftCornerY(), this.getBottomLeftCornerZ()).distance(new Location(Bukkit.getWorld(world), this.getTopRightCornerX(), this.getTopRightCornerY(), this.getTopRightCornerZ()));
	}
	
	public boolean isLocationInside(Location loc) {
		if (!loc.getWorld().getName().toUpperCase().equals(this.getWorld().toUpperCase()))
			return false;

		int minX = Math.min(this.getBottomLeftCornerX(), this.getTopRightCornerX());
		int minY = Math.min(this.getBottomLeftCornerY(), this.getTopRightCornerY());
		int minZ = Math.min(this.getBottomLeftCornerZ(), this.getTopRightCornerZ());
		
		int maxX = Math.max(this.getBottomLeftCornerX(), this.getTopRightCornerX());
		int maxY = Math.max(this.getBottomLeftCornerY(), this.getTopRightCornerY());
		int maxZ = Math.max(this.getBottomLeftCornerZ(), this.getTopRightCornerZ());
		
		return loc.getX() > minX && loc.getX() < maxX
                && loc.getY() > minY && loc.getY() < maxY
                && loc.getZ() > minZ && loc.getZ() < maxZ;
	}
	public boolean isPlayersInZone() {
		for(Player player : Bukkit.getOnlinePlayers())
			if (isLocationInside(player.getLocation()))
				return true;
		
		return false;
	}
	public int getZoneExperienceModifier() {
		return zoneExperienceModifier;
	}
	public void setZoneExperienceModifier(int zoneExperienceModifier) {
		this.zoneExperienceModifier = zoneExperienceModifier;
	}
	
}
