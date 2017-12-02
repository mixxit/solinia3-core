package com.solinia.solinia.Models;

public class NPCSpellListEntry {
	private int id;
	private int npc_spells_id;
	private int spellid;
	private int type;
	private int minlevel;
	private int maxlevel;
	private int manacost;
	private int recast_delay;
	private int priority;
	private int resist_adjust;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getNpc_spells_id() {
		return npc_spells_id;
	}
	public void setNpc_spells_id(int npc_spells_id) {
		this.npc_spells_id = npc_spells_id;
	}
	public int getSpellid() {
		return spellid;
	}
	public void setSpellid(int spellid) {
		this.spellid = spellid;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getMinlevel() {
		return minlevel;
	}
	public void setMinlevel(int minlevel) {
		this.minlevel = minlevel;
	}
	public int getMaxlevel() {
		return maxlevel;
	}
	public void setMaxlevel(int maxlevel) {
		this.maxlevel = maxlevel;
	}
	public int getManacost() {
		return manacost;
	}
	public void setManacost(int manacost) {
		this.manacost = manacost;
	}
	public int getRecast_delay() {
		return recast_delay;
	}
	public void setRecast_delay(int recast_delay) {
		this.recast_delay = recast_delay;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public int getResist_adjust() {
		return resist_adjust;
	}
	public void setResist_adjust(int resist_adjust) {
		this.resist_adjust = resist_adjust;
	}
}
