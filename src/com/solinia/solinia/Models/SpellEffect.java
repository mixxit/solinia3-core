package com.solinia.solinia.Models;

public class SpellEffect {
	private int spellEffectId;
	private SpellEffectType spellEffectType;
	private int base;
	private int limit;
	public SpellEffectType getSpellEffectType() {
		return spellEffectType;
	}
	
	public void setSpellEffectType(SpellEffectType spellEffectType) {
		this.spellEffectType = spellEffectType;
	}
	
	public int getBase() {
		return base;
	}
	
	public void setBase(int base) {
		this.base = base;
	}
	
	public int getSpellEffectId() {
		return spellEffectId;
	}
	
	public void setSpellEffectId(int spellEffectId) {
		this.spellEffectId = spellEffectId;
	}
	
	public int getLimit() {
		return limit;
	}
	
	public void setLimit(int limit) {
		this.limit = limit;
	}
}
