package com.solinia.solinia.Interfaces;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.solinia.solinia.Models.SoliniaEntitySpellEffects;
import com.solinia.solinia.Models.SoliniaSpell;

public interface IEntityManager {
	ISoliniaLivingEntity getLivingEntity(LivingEntity livingentity);

	INPCEntityProvider getNPCEntityProvider();

	boolean addActiveEntityEffect(LivingEntity targetEntity, SoliniaSpell soliniaSpell, LivingEntity sourceEntity);

	void spellTick();

	SoliniaEntitySpellEffects getActiveEntityEffects(LivingEntity entity);

	void doNPCRandomChat();

	void doNPCSpellCast();

	Integer getNPCMana(LivingEntity bukkitLivingEntity, ISoliniaNPC npc);

	void setNPCMana(LivingEntity bukkitLivingEntity, ISoliniaNPC npc, int amount);

	void addMezzed(LivingEntity livingEntity, Timestamp expiretimestamp);

	Timestamp getMezzed(LivingEntity livingEntity);

	LivingEntity getPet(Player player);

	void killPet(Player player);

	void killAllPets();

	LivingEntity setPet(Player player, LivingEntity entity);

	List<LivingEntity> getAllWorldPets();

	LivingEntity SpawnPet(Player owner, ISoliniaSpell spell);
}
