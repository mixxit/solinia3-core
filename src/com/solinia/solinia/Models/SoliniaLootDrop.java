package com.solinia.solinia.Models;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.command.CommandSender;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidLootDropSettingException;
import com.solinia.solinia.Interfaces.IPersistable;
import com.solinia.solinia.Interfaces.ISoliniaClass;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaLootDrop;
import com.solinia.solinia.Interfaces.ISoliniaLootDropEntry;
import com.solinia.solinia.Managers.StateManager;
import net.md_5.bungee.api.ChatColor;

public class SoliniaLootDrop implements ISoliniaLootDrop,IPersistable {
	private int id;
	private UUID primaryUUID = UUID.randomUUID();
	private UUID secondaryUUID = UUID.randomUUID();
	private Timestamp lastUpdatedTime;

	private String name;
	private List<ISoliniaLootDropEntry> entries = new ArrayList<ISoliniaLootDropEntry>();	

	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
		setLastUpdatedTimeNow();
	}
	
	@Override
	public UUID getPrimaryUUID() {
		// TODO Auto-generated method stub
		return this.primaryUUID;
	}
	@Override
	public void setPrimaryUUID(UUID uuid) {
		// TODO Auto-generated method stub
		this.primaryUUID = uuid;
		setLastUpdatedTimeNow();
	}
	@Override
	public UUID getSecondaryUUID() {
		// TODO Auto-generated method stub
		return this.secondaryUUID;
	}
	@Override
	public void setSecondaryUUID(UUID uuid) {
		// TODO Auto-generated method stub
		this.secondaryUUID = uuid;
		setLastUpdatedTimeNow();
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
		setLastUpdatedTimeNow();
	}

	@Override
	public List<ISoliniaLootDropEntry> getEntries() {
		return this.entries;
	}

	@Override
	public void setEntries(List<ISoliniaLootDropEntry> entries) {
		this.entries = entries;
		setLastUpdatedTimeNow();
	}
	
	@Override
	public Timestamp getLastUpdatedTime() {
		if (lastUpdatedTime == null)
			setLastUpdatedTimeNow();
		
		return lastUpdatedTime;
	}

	@Override
	public void setLastUpdatedTime(Timestamp lastUpdatedTime) {
		this.lastUpdatedTime = lastUpdatedTime;
	}
	
	@Override
	public void setLastUpdatedTimeNow() {
		LocalDateTime datetime = LocalDateTime.now();
		Timestamp nowtimestamp = Timestamp.valueOf(datetime);
		//System.out.println("Set LastUpdatedTime on " + getId());
		this.setLastUpdatedTime(nowtimestamp);
	}

	@Override
	public void sendLootDropSettingsToSender(CommandSender sender) {
		try
		{
			sender.sendMessage(ChatColor.GOLD + getName().toUpperCase() + ChatColor.RESET + "[" + getId() + "]:");
			for(ISoliniaLootDropEntry lde : getEntries())
			{
				ISoliniaItem i = StateManager.getInstance().getConfigurationManager().getItem(lde.getItemid());
				if (i != null)
				{
					sender.sendMessage("- [" + lde.getId() + "] " + ChatColor.GOLD + i.getDisplayname() + ChatColor.RESET + "[" + i.getId() + "] - " + lde.getChance() + "% chance Count: " + lde.getCount() + " Always: " + lde.isAlways());
				} else {
					sender.sendMessage("- MISSING [" + lde.getId() + "] " + ChatColor.GOLD + lde.getItemid() + ChatColor.RESET);
				}
			}
		} catch (CoreStateInitException e)
		{
			sender.sendMessage(e.getMessage());
		}
	}
	
	@Override
	public void editSetting(String setting, String value)
			throws InvalidLootDropSettingException, NumberFormatException, CoreStateInitException {

		switch (setting.toLowerCase()) {
		case "name":
			if (value.equals(""))
				throw new InvalidLootDropSettingException("Name is empty");
			setName(value);
			break;
		case "remove":
			int itemIdToRemove = Integer.parseInt(value);
			if (itemIdToRemove < 1)
				throw new InvalidLootDropSettingException("Invalid item id to remove");
			for(int i = 0; i < getEntries().size(); i++)
			{
				if (getEntries().get(i).getItemid() == itemIdToRemove)
					getEntries().remove(i);
			}
			break;
		case "setitemchance":
			String dataChance[] = value.split(" ");
			if (dataChance.length < 2)
				throw new InvalidLootDropSettingException("Missing itemid and chance");
			
			int itemIdChance = Integer.parseInt(dataChance[0]);
			if (itemIdChance < 1)
				throw new InvalidLootDropSettingException("Invalid item id to set chance of");
			int itemChance = Integer.parseInt(dataChance[1]);
			if (itemChance < 1 || itemChance > 100)
				throw new InvalidLootDropSettingException("Invalid chance");
			
			for(ISoliniaLootDropEntry entry : getEntries())
			{
				if (entry.getItemid() != itemIdChance)
					continue;
				
				entry.setChance(itemChance);
			}
			break;
		case "removeall":
			getEntries().clear();
			break;
		case "removeallmissing":
			for(int i = 0; i < getEntries().size(); i++)
			{
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(getEntries().get(i).getItemid());
				if (item == null)
					getEntries().remove(i);
			}
			break;
		case "setallchance":
			int newChance = Integer.parseInt(value);
			for(int i = 0; i < getEntries().size(); i++)
			{
				getEntries().get(i).setChance(newChance);
			}
			break;
		case "setallitemminlevel":
			int minLevel = Integer.parseInt(value);
			if (minLevel < 1 || minLevel > StateManager.getInstance().getConfigurationManager().getMaxLevel())
				throw new InvalidLootDropSettingException("Invalid minlevel");
			
			for(int i = 0; i < getEntries().size(); i++)
			{
				int itemId = getEntries().get(i).getItemid();
				StateManager.getInstance().getConfigurationManager().getItem(itemId).setMinLevel(minLevel);
			}
			break;
		case "setallitemfireresist":
			int fireresist = Integer.parseInt(value);
			if (fireresist < 0 || fireresist > ((StateManager.getInstance().getConfigurationManager().getMaxLevel() / 10) * 5))
				throw new InvalidLootDropSettingException("Invalid fireresist value is it too low or too high?");
			
			for(int i = 0; i < getEntries().size(); i++)
			{
				int itemId = getEntries().get(i).getItemid();
				StateManager.getInstance().getConfigurationManager().getItem(itemId).setFireResist(fireresist);
			}
			break;
		case "setallitemcoldresist":
			int coldresist = Integer.parseInt(value);
			if (coldresist < 0 || coldresist > ((StateManager.getInstance().getConfigurationManager().getMaxLevel() / 10) * 5))
				throw new InvalidLootDropSettingException("Invalid coldresist value is it too low or too high?");
			
			for(int i = 0; i < getEntries().size(); i++)
			{
				int itemId = getEntries().get(i).getItemid();
				StateManager.getInstance().getConfigurationManager().getItem(itemId).setColdResist(coldresist);
			}
			break;
		case "setallitemmagicresist":
			int magicresist = Integer.parseInt(value);
			if (magicresist < 0 || magicresist > ((StateManager.getInstance().getConfigurationManager().getMaxLevel() / 10) * 5))
				throw new InvalidLootDropSettingException("Invalid magicresist value is it too low or too high?");
			
			for(int i = 0; i < getEntries().size(); i++)
			{
				int itemId = getEntries().get(i).getItemid();
				StateManager.getInstance().getConfigurationManager().getItem(itemId).setMagicResist(magicresist);
			}
			break;
		case "setallitempoisonresist":
			int poisonresist = Integer.parseInt(value);
			if (poisonresist < 0 || poisonresist > ((StateManager.getInstance().getConfigurationManager().getMaxLevel() / 10) * 5))
				throw new InvalidLootDropSettingException("Invalid poisonresist value is it too low or too high?");
			
			for(int i = 0; i < getEntries().size(); i++)
			{
				int itemId = getEntries().get(i).getItemid();
				StateManager.getInstance().getConfigurationManager().getItem(itemId).setPoisonResist(poisonresist);
			}
			break;
		case "setallitemdiseaseresist":
			int diseaseresist = Integer.parseInt(value);
			if (diseaseresist < 0 || diseaseresist > ((StateManager.getInstance().getConfigurationManager().getMaxLevel() / 10) * 5))
				throw new InvalidLootDropSettingException("Invalid diseaseresist value is it too low or too high?");
			
			for(int i = 0; i < getEntries().size(); i++)
			{
				int itemId = getEntries().get(i).getItemid();
				StateManager.getInstance().getConfigurationManager().getItem(itemId).setDiseaseResist(diseaseresist);
			}
			break;
		case "setallitemchance":
			int chance = Integer.parseInt(value);
			if (chance < 1 || chance > 100)
				throw new InvalidLootDropSettingException("Invalid chance");
			
			for(ISoliniaLootDropEntry entry : getEntries())
			{
				entry.setChance(chance);
			}
			
			break;
		case "setallcount":
			int count = Integer.parseInt(value);
			if (count < 0 || count > 100)
				throw new InvalidLootDropSettingException("Invalid count");
			
			for(ISoliniaLootDropEntry entry : getEntries())
			{
				entry.setCount(count);
			}
			
			break;		
		default:
			throw new InvalidLootDropSettingException(
					"Invalid LootDrop setting. Valid Options are: name,remove,setallchance,setallitemchance,setallcount,setallitemminlevel,setallitemfireresist,setallitemcoldresist,setallitemmagicresist,setallitempoisonresist,setallitemdiseaseresist,setitemchance");
		}
		
		setLastUpdatedTimeNow();
	}

	@Override
	public List<ISoliniaLootDropEntry> getEntriesForClass(ISoliniaClass classObj) {
		if (classObj == null)
			return new ArrayList<ISoliniaLootDropEntry>();
		
		List<ISoliniaLootDropEntry> entries = new ArrayList<ISoliniaLootDropEntry>();
		
		for (ISoliniaLootDropEntry entry : this.getEntries())
		{
			if (entry.getItem() == null)
				continue;
			
			if (!entry.getItem().getAllowedClassNamesUpper().contains(classObj.getName().toUpperCase()))
				continue;
			
			entries.add(entry);
		}
		
		return entries;
	}
	
}
