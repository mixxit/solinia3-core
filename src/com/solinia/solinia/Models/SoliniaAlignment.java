package com.solinia.solinia.Models;

import org.bukkit.command.CommandSender;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidAlignmentSettingException;
import com.solinia.solinia.Interfaces.ISoliniaAlignment;
import net.md_5.bungee.api.ChatColor;

public class SoliniaAlignment implements ISoliniaAlignment {

	private int id;
	private String name;
	
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
		this.name = name.toUpperCase();
	}
	
	@Override
	public void sendAlignmentSettingsToSender(CommandSender sender) throws CoreStateInitException {
		sender.sendMessage(ChatColor.RED + "Alignment Settings for " + ChatColor.GOLD + getName() + ChatColor.RESET);
		sender.sendMessage("----------------------------");
		sender.sendMessage("- id: " + ChatColor.GOLD + getId() + ChatColor.RESET);
		sender.sendMessage("- name: " + ChatColor.GOLD + getName() + ChatColor.RESET);
		sender.sendMessage("----------------------------");
	}
	
	@Override
	public void editSetting(String setting, String value)
			throws InvalidAlignmentSettingException, NumberFormatException, CoreStateInitException {

		switch (setting.toLowerCase()) {
		default:
			throw new InvalidAlignmentSettingException("Invalid Alignment setting. Valid Options are: ");
		}
	}
}
