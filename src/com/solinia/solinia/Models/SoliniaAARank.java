package com.solinia.solinia.Models;

import java.util.ArrayList;
import java.util.List;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaAAAbility;
import com.solinia.solinia.Interfaces.ISoliniaAARank;
import com.solinia.solinia.Managers.StateManager;

public class SoliniaAARank implements ISoliniaAARank {
	private int id;
	private int abilityid;
	private int cost;
	private int level_req;
	private int spell;
	private int spell_type;
	private int recast_time;
	private int expansion;
	private int position;
	private List<SoliniaAAPrereq> prereqs = new ArrayList<SoliniaAAPrereq>();
	
	@Override
	public int getLevel_req() {
		return level_req;
	}
	@Override
	public void setLevel_req(int level_req) {
		this.level_req = level_req;
	}
	@Override
	public int getId() {
		return id;
	}
	@Override
	public void setId(int id) {
		this.id = id;
	}
	@Override
	public int getCost() {
		return cost;
	}
	@Override
	public void setCost(int cost) {
		this.cost = cost;
	}
	@Override
	public int getSpell() {
		return spell;
	}
	@Override
	public void setSpell(int spell) {
		this.spell = spell;
	}
	@Override
	public int getSpell_type() {
		return spell_type;
	}
	@Override
	public void setSpell_type(int spell_type) {
		this.spell_type = spell_type;
	}
	@Override
	public int getRecast_time() {
		return recast_time;
	}
	@Override
	public void setRecast_time(int recast_time) {
		this.recast_time = recast_time;
	}
	@Override
	public int getExpansion() {
		return expansion;
	}
	@Override
	public void setExpansion(int expansion) {
		this.expansion = expansion;
	}
	@Override
	public int getPosition() {
		return position;
	}
	@Override
	public void setPosition(int position) {
		this.position = position;
	}
	@Override
	public int getAbilityid() {
		return abilityid;
	}
	@Override
	public void setAbilityid(int abilityid) {
		this.abilityid = abilityid;
	}
	@Override
	public List<SoliniaAAPrereq> getPrereqs() {
		return prereqs;
	}
	@Override
	public void setPrereqs(List<SoliniaAAPrereq> prereqs) {
		this.prereqs = prereqs;
	}
}
