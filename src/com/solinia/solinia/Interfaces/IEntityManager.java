package com.solinia.solinia.Interfaces;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.solinia.solinia.Models.CastingSpell;
import com.solinia.solinia.Models.EntityAutoAttack;
import com.solinia.solinia.Models.SoliniaEntitySpells;
import com.solinia.solinia.Models.SoliniaSpell;
import com.solinia.solinia.Models.SpellEffectType;
import com.solinia.solinia.Models.UniversalMerchant;

public interface IEntityManager {
	ISoliniaLivingEntity getLivingEntity(LivingEntity livingentity);

	INPCEntityProvider getNPCEntityProvider();

	SoliniaEntitySpells getActiveEntitySpells(LivingEntity entity);

	void doNPCRandomChat();

	Integer getNPCMana(LivingEntity bukkitLivingEntity, ISoliniaNPC npc);

	void setNPCMana(LivingEntity bukkitLivingEntity, ISoliniaNPC npc, int amount);

	void addMezzed(LivingEntity livingEntity, Timestamp expiretimestamp);

	Timestamp getMezzed(LivingEntity livingEntity);
	
	Timestamp getStunned(LivingEntity livingEntity);

	LivingEntity getPet(Player player);

	void killPet(Player player);

	void killAllPets();

	LivingEntity setPet(Player player, LivingEntity entity);

	List<LivingEntity> getAllWorldPets();

	void clearEntityEffects(UUID uniqueId);

	void clearEntityFirstEffectOfType(LivingEntity livingEntity, SpellEffectType poisoncounter, boolean forceDoNotLoopBardSpell);

	LivingEntity SpawnPet(Player owner, ISoliniaSpell spell);

	void spellTick();

	boolean addActiveEntitySpell(LivingEntity targetEntity, SoliniaSpell soliniaSpell,
			LivingEntity sourceEntity);

	void doNPCSpellCast();

	void removeSpellEffects(UUID uuid, boolean forceDoNotLoopBardSpell);

	List<ISoliniaNPCMerchantEntry> getNPCMerchantCombinedEntries(ISoliniaNPC npc);

	boolean hasEntityEffectType(LivingEntity livingEntity, SpellEffectType type);

	void removeMezzed(LivingEntity livingEntity, Timestamp expiretimestamp);

	void doNPCSummon();

	void clearEntityFirstEffect(LivingEntity livingEntity);

	void addEntitySpellCooldown(LivingEntity livingEntity, int spellId, Timestamp expiretimestamp);

	Timestamp getEntitySpellCooldown(LivingEntity livingEntity, int spellId);

	int getAIEngagedBeneficialSelfChance();

	int getAIEngagedBeneficialOtherChance();

	int getAIEngagedDetrimentalChance();

	int getAIBeneficialBuffSpellRange();

	Timestamp getDontSpellTypeMeBefore(LivingEntity bukkitLivingEntity, int spellType);

	void setDontSpellTypeMeBefore(LivingEntity bukkitLivingEntity, int spellType, Timestamp timestamp);

	Integer getEntitySinging(UUID entityUUID);

	void setEntitySinging(UUID entityUUID, Integer spellId);

	void removeSpellEffectsOfSpellId(UUID uuid, int spellId, boolean forceDoNotLoopBardSpell);

	void doNPCCheckForEnemies();

	UniversalMerchant getUniversalMerchant(UUID universalMerchant);

	Inventory getMerchantInventory(UUID playerUUID, int pageno, UniversalMerchant universalMerchant);

	Inventory getNPCMerchantInventory(UUID playerUUID, ISoliniaNPC npc, int pageno);

	ConcurrentHashMap<UUID, Boolean> getPlayerInTerritory();

	void setPlayerInTerritory(ConcurrentHashMap<UUID, Boolean> playerInTerritory);

	ConcurrentHashMap<UUID, Boolean> getPlayerSetMain();

	void setPlayerSetMain(ConcurrentHashMap<UUID, Boolean> playerSetMain);

	ConcurrentHashMap<UUID, UUID> getEntityTargets();

	void setEntityTargets(ConcurrentHashMap<UUID, UUID> entityTarget);

	LivingEntity getEntityTarget(LivingEntity source);

	void setEntityTarget(LivingEntity source, LivingEntity target);

	void clearTargetsAgainstMe(LivingEntity entity);

	boolean isFeignedDeath(UUID entityUuid);

	void setFeignedDeath(UUID entityUuid, boolean feigned);

	void startCasting(LivingEntity livingEntity, CastingSpell castingSpell);

	CastingSpell getCasting(LivingEntity livingEntity);
	
	boolean isCasting(LivingEntity livingEntity);

	void interruptCasting(LivingEntity livingEntity);

	void processCastingTimer();

	void finishCasting(UUID entityUUID);

	void toggleAutoAttack(Player bukkitPlayer);

	void addStunned(LivingEntity livingEntity, Timestamp expiretimestamp);

	void removeStunned(LivingEntity livingEntity, Timestamp expiretimestamp);

	EntityAutoAttack getEntityAutoAttack(LivingEntity livingEntity);

	void setEntityAutoAttack(LivingEntity livingEntity, boolean autoAttacking);

	ConcurrentHashMap<UUID, Timestamp> getLastDualWield();

	void setLastDualWield(UUID uuid, Timestamp timestamp);

	ConcurrentHashMap<UUID, Timestamp> getLastDoubleAttack();

	void setLastDoubleAttack(UUID uuid, Timestamp lasttimestamp);

	ConcurrentHashMap<UUID, Timestamp> getLastRiposte();

	void setLastRiposte(UUID uuid, Timestamp lasttimestamp);

	void addToHateList(UUID entity, UUID provoker, int hate);

	Integer getHateListEntry(UUID entity, UUID provoker);

	void doNPCTeleportAttack();

	ConcurrentHashMap<UUID, Integer> getHateList(UUID entity);

	void clearHateList(UUID uuid);

	List<UUID> getActiveHateListUUIDs();
}
