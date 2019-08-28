package com.solinia.solinia.Models;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidNPCMerchantListSettingException;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaNPCMerchant;
import com.solinia.solinia.Interfaces.ISoliniaNPCMerchantEntry;
import com.solinia.solinia.Managers.StateManager;
import net.md_5.bungee.api.ChatColor;

public class SoliniaNPCMerchant implements ISoliniaNPCMerchant {
	private int id;
	private String name;
	private List<ISoliniaNPCMerchantEntry> entries = new ArrayList<ISoliniaNPCMerchantEntry>();	
	private boolean publishedBookStore = false;
	private String requiresPermissionNode = "";
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
	public List<ISoliniaNPCMerchantEntry> getEntries() {
		return entries;
	}

	@Override
	public void setEntries(List<ISoliniaNPCMerchantEntry> entries) {
		this.entries = entries;
	}

	@Override
	public void editSetting(String setting, String value) throws InvalidNPCMerchantListSettingException, NumberFormatException, CoreStateInitException {
		switch (setting.toLowerCase()) 
		{
			case "name":
				if (value.equals(""))
					throw new InvalidNPCMerchantListSettingException("Name is empty");

				if (value.length() > 25)
					throw new InvalidNPCMerchantListSettingException("Name is longer than 25 characters");
				setName(value);
				break;
			case "publishedbookstore":
				setPublishedBookStore(Boolean.parseBoolean(value));
				break;
			case "requirespermissionnode":
				setRequiresPermissionNode(value);
				break;
			case "remove":
				int itemIdToRemove = Integer.parseInt(value);
				if (itemIdToRemove < 1)
					throw new InvalidNPCMerchantListSettingException("Invalid item id to remove");
				for(int i = 0; i < getEntries().size(); i++)
				{
					if (getEntries().get(i).getItemid() == itemIdToRemove)
						getEntries().remove(i);
				}
				break;
			default:
				throw new InvalidNPCMerchantListSettingException("Invalid setting. Valid Options are: name,remove");
		}
	}

	@Override
	public void sendMerchantSettingsToSender(CommandSender sender) {
		try
		{
			sender.sendMessage(ChatColor.GOLD + getName().toUpperCase() + ChatColor.RESET + "[" + getId() + "]:");
			sender.sendMessage(" - publishedbookstore: " + ChatColor.GOLD + this.isPublishedBookStore() + ChatColor.RESET);
			sender.sendMessage(" - requirespermissionnode: " + ChatColor.GOLD + getRequiresPermissionNode() + ChatColor.RESET);
			for(ISoliniaNPCMerchantEntry lde : getEntries())
			{
				ISoliniaItem i = StateManager.getInstance().getConfigurationManager().getItem(lde.getItemid());
				if (i != null)
				{
					sender.sendMessage("- [" + lde.getId() + "] " + ChatColor.GOLD + i.getDisplayname() + ChatColor.RESET + "[" + i.getId() + "] - ");
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
	public boolean isPublishedBookStore() {
		return publishedBookStore;
	}

	@Override
	public void setPublishedBookStore(boolean publishedBookStore) {
		this.publishedBookStore = publishedBookStore;
	}

	@Override
	public String getRequiresPermissionNode() {
		return requiresPermissionNode;
	}

	@Override
	public void setRequiresPermissionNode(String requiresPermissionNode) {
		this.requiresPermissionNode = requiresPermissionNode;
	}
}
