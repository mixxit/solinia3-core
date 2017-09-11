package com.solinia.solinia.Interfaces;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.solinia.solinia.Models.SoliniaEntitySpellEffects;
import com.solinia.solinia.Models.SoliniaSpell;

public interface IEntityManager {
	ISoliniaLivingEntity getLivingEntity(LivingEntity livingentity);

	INPCEntityProvider getNPCEntityProvider();

	boolean addActiveEntityEffect(LivingEntity targetentity, SoliniaSpell soliniaSpell, Player player);

	void spellTick();

	SoliniaEntitySpellEffects getActiveEntityEffects(LivingEntity entity);

	void doNPCRandomChat();
}
