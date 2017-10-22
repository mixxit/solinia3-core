package com.solinia.solinia.Interfaces;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.solinia.solinia.Exceptions.InsufficientTemporaryMerchantItemException;
import com.solinia.solinia.Models.SoliniaEntitySpells;
import com.solinia.solinia.Models.SoliniaSpell;
import com.solinia.solinia.Models.SpellEffectType;

public interface IEntityManager {
	ISoliniaLivingEntity getLivingEntity(LivingEntity livingentity);

	INPCEntityProvider getNPCEntityProvider();

	SoliniaEntitySpells getActiveEntitySpells(LivingEntity entity);

	void doNPCRandomChat();

	Integer getNPCMana(LivingEntity bukkitLivingEntity, ISoliniaNPC npc);

	void setNPCMana(LivingEntity bukkitLivingEntity, ISoliniaNPC npc, int amount);

	void addMezzed(LivingEntity livingEntity, Timestamp expiretimestamp);

	Timestamp getMezzed(LivingEntity livingEntity);

	LivingEntity getPet(Player player);

	void killPet(Player player);

	void killAllPets();

	LivingEntity setPet(Player player, LivingEntity entity);

	List<LivingEntity> getAllWorldPets();

	void clearEntityEffects(UUID uniqueId);

	void clearEntityFirstEffectOfType(LivingEntity livingEntity, SpellEffectType poisoncounter);

	void toggleTrance(UUID uniqueId);

	boolean getTrance(UUID uuid);

	void setTrance(UUID uuid, Boolean enabled);

	LivingEntity SpawnPet(Plugin plugin, Player owner, ISoliniaSpell spell);

	void spellTick(Plugin plugin);

	boolean addActiveEntitySpell(Plugin plugin, LivingEntity targetEntity, SoliniaSpell soliniaSpell,
			LivingEntity sourceEntity);

	void doNPCSpellCast(Plugin plugin);

	void removeSpellEffects(UUID uuid);

	void removeSpellEffectsOfSpellId(UUID uuid, int spellId);

	void addTemporaryMerchantItem(int npcid, int itemid, int amount);

	void removeTemporaryMerchantItem(int npcid, int itemid, int amount) throws InsufficientTemporaryMerchantItemException;

	List<ISoliniaNPCMerchantEntry> getNPCMerchantCombinedEntries(ISoliniaNPC npc);

	List<ISoliniaNPCMerchantEntry> getTemporaryMerchantItems(ISoliniaNPC npc);

	boolean hasEntityEffectType(LivingEntity livingEntity, SpellEffectType type);

	void removeMezzed(LivingEntity livingEntity, Timestamp expiretimestamp);

	void doNPCSummon(Plugin plugin);

	void clearEntityFirstEffect(LivingEntity livingEntity);
}
