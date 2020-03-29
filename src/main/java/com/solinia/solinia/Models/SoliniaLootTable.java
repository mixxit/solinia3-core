package com.solinia.solinia.Models;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidLootTableSettingException;
import com.solinia.solinia.Interfaces.IPersistable;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaLootDrop;
import com.solinia.solinia.Interfaces.ISoliniaLootDropEntry;
import com.solinia.solinia.Interfaces.ISoliniaLootTable;
import com.solinia.solinia.Interfaces.ISoliniaLootTableEntry;
import com.solinia.solinia.Managers.StateManager;

import net.md_5.bungee.api.ChatColor;

public class SoliniaLootTable implements ISoliniaLootTable,IPersistable {
	private int id;
	private String name;
	private List<ISoliniaLootTableEntry> entries = new ArrayList<ISoliniaLootTableEntry>();	
	
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
	public List<ISoliniaLootTableEntry> getEntries() {
		return this.entries;
	}

	@Override
	public void setEntries(List<ISoliniaLootTableEntry> entries) {
		this.entries = entries;	
	}

	@Override
	public void sendLootTableSettingsToSender(CommandSender sender) {
		try
		{
			sender.sendMessage(ChatColor.RED + "Loot Table Settings for " + ChatColor.GOLD + getName() + ChatColor.RESET);
			sender.sendMessage("----------------------------");
			sender.sendMessage("- id: " + ChatColor.GOLD + getId() + ChatColor.RESET);
			sender.sendMessage("- name: " + ChatColor.GOLD + getName() + ChatColor.RESET);

			for(ISoliniaLootTableEntry le : getEntries())
			{
				ISoliniaLootDrop ld = StateManager.getInstance().getConfigurationManager().getLootDrop(le.getLootdropid());
				sender.sendMessage("- " + ChatColor.GOLD + ld.getName().toUpperCase() + ChatColor.RESET + "[" + ld.getId() + "]:");
				for(ISoliniaLootDropEntry lde : ld.getEntries())
				{
					ISoliniaItem i = StateManager.getInstance().getConfigurationManager().getItem(lde.getItemid());
					sender.sendMessage("  - " + ChatColor.GOLD + i.getDisplayname() + ChatColor.RESET + "[" + i.getId() + "] - " + lde.getChance() + "% chance Count: " + lde.getCount() + " Always: " + lde.isAlways());
				}
			}
		} catch (CoreStateInitException e)
		{
			sender.sendMessage(e.getMessage());
		}
	}

	@Override
	public void editSetting(String setting, String value)
			throws InvalidLootTableSettingException, NumberFormatException, CoreStateInitException {

		switch (setting.toLowerCase()) {
		case "name":
			if (value.equals(""))
				throw new InvalidLootTableSettingException("Name is empty");

			if (value.length() > 25)
				throw new InvalidLootTableSettingException("Name is longer than 25 characters");
			setName(value);
			break;
		case "remove":
			int lootDropIdToRemove = Integer.parseInt(value);
			if (lootDropIdToRemove < 1)
				throw new InvalidLootTableSettingException("Invalid lootdrop id to remove");

			for(int i = 0; i < getEntries().size(); i++)
			{
				if (getEntries().get(i).getLootdropid() == lootDropIdToRemove)
					getEntries().remove(i);
			}
			break;
		default:
			throw new InvalidLootTableSettingException(
					"Invalid LootTable setting. Valid Options are: name,remove");
		}
	}

}
