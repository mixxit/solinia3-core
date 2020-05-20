package com.solinia.solinia.Models;

import java.util.UUID;

import org.bukkit.entity.LivingEntity;

import com.solinia.solinia.Interfaces.ISoliniaSpell;

public class ActiveSpellEffect extends SpellEffect {
	private int calculatedValue;
	private int remainingValue;
	private int spellId;
	private UUID sourceEntityUUID;
	private UUID targetEntityUUID;
	private int sourceLevel;
	private int ticksLeft;
	private int instrumentMod;
	
	public ActiveSpellEffect(ISoliniaSpell spell, SpellEffect spellEffect, LivingEntity sourceEntity, LivingEntity targetEntity, int sourceLevel, int ticksleft, int instrumentMod) {
		setBase(spellEffect.getBase());
		setBase2(spellEffect.getBase2());
		setFormula(spellEffect.getFormula());
		setLimit(spellEffect.getLimit());
		setMax(spellEffect.getMax());
		setSpellEffectId(spellEffect.getSpellEffectId());
		setSpellEffectNo(spellEffect.getSpellEffectNo());
		setSpellEffectType(spellEffect.getSpellEffectType());
		setCalculatedValue(spell.calcSpellEffectValueFormula(spellEffect, sourceEntity, targetEntity, sourceLevel, ticksleft));
		setRemainingValue(getCalculatedValue());
		setSpellId(spell.getId());
		setSourceEntityUUID(sourceEntity.getUniqueId());
		setTargetEntityUUID(targetEntity.getUniqueId());
		setSourceLevel(sourceLevel);
		setTicksLeft(ticksleft);
		setInstrumentMod(instrumentMod);
	}

	public int getCalculatedValue() {
		return calculatedValue;
	}

	public void setCalculatedValue(int calculatedValue) {
		this.calculatedValue = calculatedValue;
	}
	
	public int getRemainingValue() {
		return remainingValue;
	}

	public void setRemainingValue(int remainingValue) {
		this.remainingValue = remainingValue;
	}

	public int getSpellId() {
		return spellId;
	}

	public void setSpellId(int spellId) {
		this.spellId = spellId;
	}

	public UUID getSourceEntityUUID() {
		return sourceEntityUUID;
	}

	public void setSourceEntityUUID(UUID sourceEntityUUID) {
		this.sourceEntityUUID = sourceEntityUUID;
	}

	public UUID getTargetEntityUUID() {
		return targetEntityUUID;
	}

	public void setTargetEntityUUID(UUID targetEntityUUID) {
		this.targetEntityUUID = targetEntityUUID;
	}

	public int getSourceLevel() {
		return sourceLevel;
	}

	public void setSourceLevel(int sourceLevel) {
		this.sourceLevel = sourceLevel;
	}
	
	public int getTicksLeft() {
		return ticksLeft;
	}

	public void setTicksLeft(int ticksLeft) {
		this.ticksLeft = ticksLeft;
	}

	public int getInstrumentMod() {
		return instrumentMod;
	}

	public void setInstrumentMod(int instrumentMod) {
		this.instrumentMod = instrumentMod;
	}
}
