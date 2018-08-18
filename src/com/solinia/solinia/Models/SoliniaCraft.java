package com.solinia.solinia.Models;

import org.bukkit.command.CommandSender;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidCraftSettingException;
import com.solinia.solinia.Interfaces.ISoliniaClass;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaLootDrop;
import com.solinia.solinia.Interfaces.ISoliniaLootTable;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Utils.Utils;

import net.md_5.bungee.api.ChatColor;

public class SoliniaCraft {
	private int id = 0;
	private String recipeName = "";
	private int item1 = 0;
	private int item2 = 0;
	private String skill = "";
	private int minSkill = 0;
	private boolean nearForge = false;
	private int classId = 0;
	private int outputItem = 0;
	private int outputLootTableId = 0;
	
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
	public String getSkill() {
		return skill;
	}
	public void setSkill(String skill) {
		this.skill = skill;
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
		sender.sendMessage("- outputloottableid: " + ChatColor.GOLD + getOutputLootTableId() + ChatColor.RESET);
		sender.sendMessage("- classid: " + ChatColor.GOLD + getClassId() + ChatColor.RESET);
		sender.sendMessage("- skill: " + ChatColor.GOLD + getSkill() + ChatColor.RESET);
		sender.sendMessage("- minskill: " + ChatColor.GOLD + getMinSkill() + ChatColor.RESET);
	}

	public void editSetting(String setting, String value)
			throws InvalidCraftSettingException, NumberFormatException, CoreStateInitException {

		switch (setting.toLowerCase()) {
		case "recipename":
			if (value.equals(""))
				throw new InvalidCraftSettingException("Name is empty");
			if (StateManager.getInstance().getConfigurationManager().getCraft(value.toUpperCase()) != null)
				throw new InvalidCraftSettingException("Recipe already exists with this name");
			setRecipeName(value.toUpperCase());
			break;
		case "classid":
			ISoliniaClass classObj = StateManager.getInstance().getConfigurationManager().getClassObj(Integer.parseInt(value));
			if (classObj == null)
				throw new InvalidCraftSettingException("Class does not exist");
			setClassId(Integer.parseInt(value));
			break;
		case "skill":
			if (!Utils.isValidSkill(value))
				throw new InvalidCraftSettingException("Invalid skill");

			setSkill(value.toUpperCase());
			break;
		case "minskill":
			if (Integer.parseInt(value) < 0)
				throw new InvalidCraftSettingException("Skill must be greater than or equal to 0");
			setMinSkill(Integer.parseInt(value));
			break;
		case "item1":
			int itemId1 = Integer.parseInt(value);
			ISoliniaItem solitem1 = StateManager.getInstance().getConfigurationManager().getItem(itemId1);
			if (solitem1 == null)
			{
				throw new InvalidCraftSettingException("Invalid item id (in item 1)");
			}
			setItem1(itemId1);
			break;
		case "item2":
			int itemId2 = Integer.parseInt(value);
			ISoliniaItem solitem2 = StateManager.getInstance().getConfigurationManager().getItem(itemId2);
			if (solitem2 == null)
			{
				throw new InvalidCraftSettingException("Invalid item id (in item 2)");
			}
			setItem2(itemId2);
			break;
		case "outputitem":
			int outputitem = Integer.parseInt(value);
			
			if (outputitem == 0)
			{
				setOutputItem(0);
				break;
			}
			
			ISoliniaItem soloutitem = StateManager.getInstance().getConfigurationManager().getItem(outputitem);
			if (soloutitem == null)
			{
				throw new InvalidCraftSettingException("Invalid item id (out item)");
			}

			setOutputItem(outputitem);
			break;
		case "outputloottableid":
			int outputloottableid = Integer.parseInt(value);
			if (outputloottableid == 0)
			{
				setOutputLootTableId(0);
				break;
			}
			
			ISoliniaLootTable solloottable = StateManager.getInstance().getConfigurationManager().getLootTable(outputloottableid);
			if (solloottable == null)
			{
				throw new InvalidCraftSettingException("Invalid item outputloottableid (out loottable)");
			}

			setOutputLootTableId(outputloottableid);
			break;
		default:
			throw new InvalidCraftSettingException(
					"Invalid craft setting. Valid Options are: recipename,item1,item2,outputitem,outputloottableid,skill,classid,minskill");
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
	public int getOutputLootTableId() {
		return outputLootTableId;
	}
	public void setOutputLootTableId(int outputLootTableId) {
		this.outputLootTableId = outputLootTableId;
	}
}
