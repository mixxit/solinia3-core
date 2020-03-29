package com.solinia.solinia.Models;

public class SpellEffect {
	private int spellEffectId;
	private SpellEffectType spellEffectType;
	private int base;
	private int limit;
	private int spellEffectNo;
	private int formula;
	private int max;
	private int base2;
	
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
	
	public int getBase2() {
		return base2;
	}
	
	public void setBase2(int base2) {
		this.base2 = base2;
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

	public int getSpellEffectNo() {
		return spellEffectNo;
	}

	public void setSpellEffectNo(int spellEffectNo) {
		this.spellEffectNo = spellEffectNo;
	}

	public int getFormula() {
		return formula;
	}

	public void setFormula(int formula) {
		this.formula = formula;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public boolean allowsStacking(SpellEffect activeEffect) {
		if (this.getSpellEffectType().equals(SpellEffectType.Screech))
		{
			if (!(this.getBase() == -1 && activeEffect.getBase() < 1))
				return false;
		}
		
		return true;
	}
}
