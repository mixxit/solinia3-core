package com.solinia.solinia.Models;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidLootDropSettingException;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaLootDrop;
import com.solinia.solinia.Interfaces.ISoliniaLootDropEntry;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Utils.Utils;

import net.md_5.bungee.api.ChatColor;

public class SoliniaLootDrop implements ISoliniaLootDrop {
	private int id;
	private String name;
	private List<ISoliniaLootDropEntry> entries = new ArrayList<ISoliniaLootDropEntry>();	

	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public List<ISoliniaLootDropEntry> getEntries() {
		return this.entries;
	}

	@Override
	public void setEntries(List<ISoliniaLootDropEntry> entries) {
		this.entries = entries;
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

			if (value.length() > 25)
				throw new InvalidLootDropSettingException("Name is longer than 25 characters");
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
			if (minLevel < 1 || minLevel > Utils.getMaxLevel())
				throw new InvalidLootDropSettingException("Invalid minlevel");
			
			for(int i = 0; i < getEntries().size(); i++)
			{
				int itemId = getEntries().get(i).getItemid();
				StateManager.getInstance().getConfigurationManager().getItem(itemId).setMinLevel(minLevel);
			}
			break;
		case "setallitemfireresist":
			int fireresist = Integer.parseInt(value);
			if (fireresist < 0 || fireresist > ((Utils.getMaxLevel() / 10) * 5))
				throw new InvalidLootDropSettingException("Invalid fireresist value is it too low or too high?");
			
			for(int i = 0; i < getEntries().size(); i++)
			{
				int itemId = getEntries().get(i).getItemid();
				StateManager.getInstance().getConfigurationManager().getItem(itemId).setFireResist(fireresist);
			}
			break;
		case "setallitemcoldresist":
			int coldresist = Integer.parseInt(value);
			if (coldresist < 0 || coldresist > ((Utils.getMaxLevel() / 10) * 5))
				throw new InvalidLootDropSettingException("Invalid coldresist value is it too low or too high?");
			
			for(int i = 0; i < getEntries().size(); i++)
			{
				int itemId = getEntries().get(i).getItemid();
				StateManager.getInstance().getConfigurationManager().getItem(itemId).setColdResist(coldresist);
			}
			break;
		case "setallitemmagicresist":
			int magicresist = Integer.parseInt(value);
			if (magicresist < 0 || magicresist > ((Utils.getMaxLevel() / 10) * 5))
				throw new InvalidLootDropSettingException("Invalid magicresist value is it too low or too high?");
			
			for(int i = 0; i < getEntries().size(); i++)
			{
				int itemId = getEntries().get(i).getItemid();
				StateManager.getInstance().getConfigurationManager().getItem(itemId).setMagicResist(magicresist);
			}
			break;
		case "setallitempoisonresist":
			int poisonresist = Integer.parseInt(value);
			if (poisonresist < 0 || poisonresist > ((Utils.getMaxLevel() / 10) * 5))
				throw new InvalidLootDropSettingException("Invalid poisonresist value is it too low or too high?");
			
			for(int i = 0; i < getEntries().size(); i++)
			{
				int itemId = getEntries().get(i).getItemid();
				StateManager.getInstance().getConfigurationManager().getItem(itemId).setPoisonResist(poisonresist);
			}
			break;
		case "setallitemdiseaseresist":
			int diseaseresist = Integer.parseInt(value);
			if (diseaseresist < 0 || diseaseresist > ((Utils.getMaxLevel() / 10) * 5))
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
			
			break;		default:
			throw new InvalidLootDropSettingException(
					"Invalid LootDrop setting. Valid Options are: name,remove,setallchance,setallitemchance,setallcount,setallitemminlevel,setallitemfireresist,setallitemcoldresist,setallitemmagicresist,setallitempoisonresist,setallitemdiseaseresist");
		}
	}
	
}
