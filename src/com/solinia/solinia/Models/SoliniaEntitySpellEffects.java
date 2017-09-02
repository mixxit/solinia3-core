package com.solinia.solinia.Models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;

public class SoliniaEntitySpellEffects {


	private UUID livingEntityUUID;
	private ConcurrentHashMap<Integer, SoliniaActiveSpellEffect> activeSpells = new ConcurrentHashMap<Integer,SoliniaActiveSpellEffect>();

	public SoliniaEntitySpellEffects(UUID uniqueId) {
		setLivingEntityUUID(uniqueId);
	}

	public UUID getLivingEntityUUID() {
		return livingEntityUUID;
	}

	public void setLivingEntityUUID(UUID livingEntityUUID) {
		this.livingEntityUUID = livingEntityUUID;
	}

	public Collection<SoliniaActiveSpellEffect> getActiveSpell() {
		return activeSpells.values();
	}

	public boolean addSpellEffect(SoliniaSpell soliniaSpell, Player player) {
		// This spell ID is already active
		if (activeSpells.get(soliniaSpell.getId()) != null)
			return false;
		
		SoliniaActiveSpellEffect activeEffect = new SoliniaActiveSpellEffect();
		activeEffect.setOwnerPlayer(true);
		activeEffect.setSourceUuid(player.getUniqueId());
		activeEffect.setSpellId(soliniaSpell.getId());
		activeSpells.put(soliniaSpell.getId(),activeEffect);
		activeEffect.apply();
		return true;
	}
}
