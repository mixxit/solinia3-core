package com.solinia.solinia.Models;

import org.bukkit.command.CommandSender;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidCraftSettingException;
import com.solinia.solinia.Exceptions.InvalidZoneSettingException;

import net.md_5.bungee.api.ChatColor;

public class SoliniaCraft {
	private int id = 0;
	private String recipeName = "";
	private int item1 = 0;
	private int item2 = 0;
	private SkillType skilltype = SkillType.None;
	private int minSkill = 0;
	private boolean nearForge = false;
	private int classId = 0;
	private int outputItem = 0;
	
	public int getItem1() {
		return item1;
	}
	public void setItem1(int item1) {
		this.item1 = item1;
	}
	public int getItem2() {
		return item2;
	}
	public void setItem2(int item2) {
		this.item2 = item2;
	}
	public SkillType getSkilltype() {
		return skilltype;
	}
	public void setSkilltype(SkillType skilltype) {
		this.skilltype = skilltype;
	}
	public int getMinSkill() {
		return minSkill;
	}
	public void setMinSkill(int minSkill) {
		this.minSkill = minSkill;
	}
	public boolean isNearForge() {
		return nearForge;
	}
	public void setNearForge(boolean nearForge) {
		this.nearForge = nearForge;
	}
	public int getClassId() {
		return classId;
	}
	public void setClassId(int classid) {
		this.classId = classid;
	}
	public int getOutputItem() {
		return outputItem;
	}
	public void setOutputItem(int outputItem) {
		this.outputItem = outputItem;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	private boolean operatorCreated = true;
	
	public void sendCraftSettingsToSender(CommandSender sender) throws CoreStateInitException {
		sender.sendMessage(ChatColor.RED + "Craft Settings for Recipe " + ChatColor.GOLD + getRecipeName() + ChatColor.RESET);
		sender.sendMessage("----------------------------");
		sender.sendMessage("- id: " + ChatColor.GOLD + getId() + ChatColor.RESET);
		sender.sendMessage("- recipename: " + ChatColor.GOLD + getRecipeName() + ChatColor.RESET);
		sender.sendMessage("- item1: " + ChatColor.GOLD + getItem1() + ChatColor.RESET);
		sender.sendMessage("- item2: " + ChatColor.GOLD + getItem2() + ChatColor.RESET);
		sender.sendMessage("- outputitem: " + ChatColor.GOLD + getOutputItem() + ChatColor.RESET);
	}

	public void editSetting(String setting, String value)
			throws InvalidCraftSettingException, NumberFormatException, CoreStateInitException {

		switch (setting.toLowerCase()) {
		case "recipename":
			if (value.equals(""))
				throw new InvalidCraftSettingException("Name is empty");
			setRecipeName(value);
			break;
		case "item1":
			setItem1(Integer.parseInt(value));
			break;
		case "item2":
			setItem2(Integer.parseInt(value));
			break;
		case "outputitem":
			setOutputItem(Integer.parseInt(value));
			break;
		default:
			throw new InvalidCraftSettingException(
					"Invalid zone setting. Valid Options are: recipename,item1,item2,outputitem");
		}
	}
	public String getRecipeName() {
		return recipeName;
	}
	public void setRecipeName(String recipeName) {
		this.recipeName = recipeName;
	}
	public boolean isOperatorCreated() {
		return operatorCreated;
	}
	public void setOperatorCreated(boolean operatorCreated) {
		this.operatorCreated = operatorCreated;
	}
}
