package com.solinia.solinia.Models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.command.CommandSender;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidNpcSpellListSettingException;
import com.solinia.solinia.Interfaces.IPersistable;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Managers.StateManager;
import net.md_5.bungee.api.ChatColor;

public class NPCSpellList implements IPersistable {
	private int id;
	private UUID primaryUUID = UUID.randomUUID();
	private UUID secondaryUUID = UUID.randomUUID();

	private String name;
	private int parent_list;
	private int attack_proc;
	private int proc_chance;
	private int range_proc;
	private int rproc_chance;
	private int defensive_proc;
	private int dproc_chance;
	private int fail_recast;
	private int engaged_no_sp_recast_min;
	private int engaged_no_sp_recast_max;
	private int engaged_b_self_chance;
	private int engaged_b_other_chance;
	private int engaged_d_chance;
	private int pursue_no_sp_recast_min;
	private int pursue_no_sp_recast_max;
	private int pursue_d_chance;
	private int idle_no_sp_recast_min;
	private int idle_no_sp_recast_max;
	private int idle_b_chance;
	
	private List<NPCSpellListEntry> spellListEntry = new ArrayList<NPCSpellListEntry>();
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getParent_list() {
		return parent_list;
	}
	public void setParent_list(int parent_list) {
		this.parent_list = parent_list;
	}
	public int getAttack_proc() {
		return attack_proc;
	}
	public void setAttack_proc(int attack_proc) {
		this.attack_proc = attack_proc;
	}
	public int getProc_chance() {
		return proc_chance;
	}
	public void setProc_chance(int proc_chance) {
		this.proc_chance = proc_chance;
	}
	public int getRange_proc() {
		return range_proc;
	}
	public void setRange_proc(int range_proc) {
		this.range_proc = range_proc;
	}
	public int getRproc_chance() {
		return rproc_chance;
	}
	public void setRproc_chance(int rproc_chance) {
		this.rproc_chance = rproc_chance;
	}
	public int getDefensive_proc() {
		return defensive_proc;
	}
	public void setDefensive_proc(int defensive_proc) {
		this.defensive_proc = defensive_proc;
	}
	public int getDproc_chance() {
		return dproc_chance;
	}
	public void setDproc_chance(int dproc_chance) {
		this.dproc_chance = dproc_chance;
	}
	public int getFail_recast() {
		return fail_recast;
	}
	public void setFail_recast(int fail_recast) {
		this.fail_recast = fail_recast;
	}
	public int getEngaged_no_sp_recast_min() {
		return engaged_no_sp_recast_min;
	}
	public void setEngaged_no_sp_recast_min(int engaged_no_sp_recast_min) {
		this.engaged_no_sp_recast_min = engaged_no_sp_recast_min;
	}
	public int getEngaged_no_sp_recast_max() {
		return engaged_no_sp_recast_max;
	}
	public void setEngaged_no_sp_recast_max(int engaged_no_sp_recast_max) {
		this.engaged_no_sp_recast_max = engaged_no_sp_recast_max;
	}
	public int getEngaged_b_self_chance() {
		return engaged_b_self_chance;
	}
	public void setEngaged_b_self_chance(int engaged_b_self_chance) {
		this.engaged_b_self_chance = engaged_b_self_chance;
	}
	public int getEngaged_b_other_chance() {
		return engaged_b_other_chance;
	}
	public void setEngaged_b_other_chance(int engaged_b_other_chance) {
		this.engaged_b_other_chance = engaged_b_other_chance;
	}
	public int getEngaged_d_chance() {
		return engaged_d_chance;
	}
	public void setEngaged_d_chance(int engaged_d_chance) {
		this.engaged_d_chance = engaged_d_chance;
	}
	public int getPursue_no_sp_recast_min() {
		return pursue_no_sp_recast_min;
	}
	public void setPursue_no_sp_recast_min(int pursue_no_sp_recast_min) {
		this.pursue_no_sp_recast_min = pursue_no_sp_recast_min;
	}
	public int getPursue_no_sp_recast_max() {
		return pursue_no_sp_recast_max;
	}
	public void setPursue_no_sp_recast_max(int pursue_no_sp_recast_max) {
		this.pursue_no_sp_recast_max = pursue_no_sp_recast_max;
	}
	public int getPursue_d_chance() {
		return pursue_d_chance;
	}
	public void setPursue_d_chance(int pursue_d_chance) {
		this.pursue_d_chance = pursue_d_chance;
	}
	public int getIdle_no_sp_recast_min() {
		return idle_no_sp_recast_min;
	}
	public void setIdle_no_sp_recast_min(int idle_no_sp_recast_min) {
		this.idle_no_sp_recast_min = idle_no_sp_recast_min;
	}
	public int getIdle_no_sp_recast_max() {
		return idle_no_sp_recast_max;
	}
	public void setIdle_no_sp_recast_max(int idle_no_sp_recast_max) {
		this.idle_no_sp_recast_max = idle_no_sp_recast_max;
	}
	public int getIdle_b_chance() {
		return idle_b_chance;
	}
	public void setIdle_b_chance(int idle_b_chance) {
		this.idle_b_chance = idle_b_chance;
	}
	public List<NPCSpellListEntry> getSpellListEntry() {
		return spellListEntry;
	}
	public void setSpellListEntry(List<NPCSpellListEntry> spellListEntry) {
		this.spellListEntry = spellListEntry;
	}
	public void sendSettingsToSender(CommandSender sender) {
		sender.sendMessage(ChatColor.RED + "NPC Spell List Settings for " + ChatColor.GOLD + getName() + ChatColor.RESET);
		sender.sendMessage("----------------------------");
		sender.sendMessage("- id: " + ChatColor.GOLD + getId() + ChatColor.RESET);
		sender.sendMessage("- name: " + ChatColor.GOLD + getName() + ChatColor.RESET);
		sender.sendMessage("----------------------------");
		sender.sendMessage(ChatColor.RED + "ENTRIES" + ChatColor.RESET);
		
		for (NPCSpellListEntry entry : this.getSpellListEntry())
		{
			ISoliniaSpell spell = null;
			try {
				spell = StateManager.getInstance().getConfigurationManager().getSpell(entry.getSpellid());
			} catch (CoreStateInitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (spell != null)
			{
				sender.sendMessage("- entry.getId() " + ChatColor.GOLD + spell.getName() + " (" + entry.getSpellid() + ")" + ChatColor.RESET);
			}
		}
	}
	
	public void editSetting(String setting, String value)
			throws InvalidNpcSpellListSettingException, NumberFormatException, CoreStateInitException {

		switch (setting.toLowerCase()) {
		default:
			throw new InvalidNpcSpellListSettingException(
					"Invalid NPC Spell List setting. Valid Options are: ");
		}
	}
}
