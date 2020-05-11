package com.solinia.solinia.Interfaces;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.solinia.solinia.Models.ActiveSongs;
import com.solinia.solinia.Models.CastingSpell;
import com.solinia.solinia.Models.EntityAutoAttack;
import com.solinia.solinia.Models.SoliniaActiveSpell;
import com.solinia.solinia.Models.SoliniaEntitySpells;
import com.solinia.solinia.Models.SpellEffectType;
import com.solinia.solinia.Models.UniversalMerchant;

import net.minecraft.server.v1_14_R1.Tuple;

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

	void removeAllPets(boolean runImmediately);

	LivingEntity setPet(UUID petOwnerUUID, LivingEntity entity);

	List<LivingEntity> getAllWorldPets();

	void clearEntityEffects(UUID uniqueId);

	LivingEntity SpawnPet(Player owner, ISoliniaSpell spell);

	void spellTick();

	boolean addActiveEntitySpell(LivingEntity targetEntity, ISoliniaSpell soliniaSpell,
			LivingEntity sourceEntity, boolean sendMessages, String requiredWeaponSkillType, boolean racialPassive);
	
	boolean addActiveEntitySpell(LivingEntity targetEntity, ISoliniaSpell soliniaSpell,
			LivingEntity sourceEntity, boolean sendMessages, String requiredWeaponSkillType);

	void doNPCSpellCast();

	List<ISoliniaNPCMerchantEntry> getNPCMerchantCombinedEntries(ISoliniaNPCMerchant merchant);

	boolean hasEntityEffectType(LivingEntity livingEntity, SpellEffectType type);

	void doNPCSummon();

	void clearEntityFirstEffect(LivingEntity livingEntity);
	void clearEntityFirstEffect(LivingEntity livingEntity, boolean forceDoNotLoopBardSong);

	void addEntitySpellCooldown(LivingEntity livingEntity, int spellId, Timestamp expiretimestamp);

	Timestamp getEntitySpellCooldown(LivingEntity livingEntity, int spellId);

	int getAIEngagedBeneficialSelfChance();

	int getAIEngagedBeneficialOtherChance();

	int getAIEngagedDetrimentalChance();

	int getAIBeneficialBuffSpellRange();

	Timestamp getDontSpellTypeMeBefore(LivingEntity bukkitLivingEntity, int spellType);

	void setDontSpellTypeMeBefore(LivingEntity bukkitLivingEntity, int spellType, Timestamp timestamp);

	void removeSpellEffectsOfSpellId(UUID uuid, int spellId, boolean forceDoNotLoopBardSpell, boolean removeNonCombatEffects);

	void doNPCCheckForEnemies();

	UniversalMerchant getUniversalMerchant(UUID universalMerchant);

	Inventory getMerchantInventory(UUID playerUUID, int pageno, UniversalMerchant universalMerchant);

	Inventory getNPCMerchantInventory(UUID playerUUID, ISoliniaNPCMerchant soliniaNpcMerchant, int pageno);

	ConcurrentHashMap<UUID, Boolean> getPlayerInTerritory();

	void setPlayerInTerritory(ConcurrentHashMap<UUID, Boolean> playerInTerritory);

	ConcurrentHashMap<UUID, Boolean> getPlayerSetMain();

	void setPlayerSetMain(ConcurrentHashMap<UUID, Boolean> playerSetMain);

	ConcurrentHashMap<UUID, UUID> getEntityTargets();

	void setEntityTargets(ConcurrentHashMap<UUID, UUID> entityTarget);

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

	EntityAutoAttack getEntityAutoAttack(LivingEntity livingEntity);

	void setEntityAutoAttack(LivingEntity livingEntity, boolean autoAttacking);

	ConcurrentHashMap<UUID, Timestamp> getLastDoubleAttack();

	void setLastDoubleAttack(UUID uuid, Timestamp lasttimestamp);

	ConcurrentHashMap<UUID, Timestamp> getLastRiposte();

	void setLastRiposte(UUID uuid, Timestamp lasttimestamp);

	void addToHateList(UUID entity, UUID provoker, int hate, boolean isYellForHelp);

	Tuple<Integer, Boolean> getHateListEntry(UUID entity, UUID provoker);

	void doNPCTeleportAttack();

	void clearHateList(UUID uuid);

	List<UUID> getActiveHateListUUIDs();

	void removePet(UUID petOwner, boolean kill);

	LivingEntity getPet(UUID ownerUuid);

	ConcurrentHashMap<UUID, Timestamp> getLastBindwound();

	void setLastBindwound(UUID uuid, Timestamp lasttimestamp);

	UUID getFollowing(UUID entityUuid);

	void setFollowing(UUID entityUuid, UUID following);

	Boolean isFollowing(UUID entityUuid);

	List<UUID> getFollowers(UUID uniqueId);

	void removeMezzed(LivingEntity livingEntity);

	void removeStunned(LivingEntity livingEntity);


	void clearEntityFirstEffectOfType(LivingEntity livingEntity, SpellEffectType type, boolean forceDoNotLoopBardSpell,
			boolean removeNonCombatEffects);

	void removeSpellEffects(UUID uuid, boolean forceDoNotLoopBardSpell, boolean removeNonCombatEffects);
	void removeSpellEffectsExcept(UUID uuid,  boolean forceDoNotLoopBardSpell, boolean removeNonCombatEffects, List<SpellEffectType> exclude);

	ConcurrentHashMap<UUID, Timestamp> getLastMeleeAttack();

	void setLastMeleeAttack(UUID uuid, Timestamp lasttimestamp);

	void forceClearTargetsAgainstMe(LivingEntity me);

	void forceSetEntityTarget(LivingEntity me, LivingEntity target);

	LivingEntity forceGetEntityTarget(LivingEntity me);

	List<UUID> getAllWorldPetUUIDs();

	void playSpellFinishedSoundEffect(LivingEntity targetEntity, ISoliniaSpell spell);

	void doNPCYellForAssist();

	ConcurrentHashMap<UUID, Timestamp> getLastCallForAssist();

	void setLastCallForAssist(UUID uuid, Timestamp lasttimestamp);

	ConcurrentHashMap<UUID, Timestamp> getLastDisarm();

	void setLastDisarm(UUID uuid, Timestamp lasttimestamp);

	void startTracking(LivingEntity bukkitPlayer, Location location);

	void stopTracking(UUID entityUUID);

	Location getEntityTracking(LivingEntity bukkitPlayer);

	boolean hasHate(UUID uniqueId);

	List<UUID> getHateListUUIDs(UUID uuid);

	int getHateListAmount(UUID uniqueId, UUID target);

	boolean isInHateList(UUID uniqueId, UUID target);

	void removeFromHateList(UUID entityUuid, UUID target);

	boolean hasAssistHate(UUID uniqueId);

	long getReverseAggroCount(UUID uniqueId);

	void resetReverseAggro(UUID uniqueId);

	SoliniaActiveSpell getFirstActiveSpellOfSpellEffectType(LivingEntity livingEntity, SpellEffectType type);

	ActiveSongs getEntitySinging(UUID uniqueId);

	void setEntitySinging(UUID uniqueId, Integer spellId);

	List<UUID> getReverseEntityTarget(UUID uniqueId);

	void clearEntityEffectsOfType(LivingEntity livingEntity, SpellEffectType type, boolean forceDoNotLoopBardSpell,
			boolean removeNonCombatEffects);

	void setPetFocus(UUID uniqueId, int petFocus);

	int getPetFocus(UUID uniqueId);

	ConcurrentHashMap<UUID, UUID> getPetOwnerData();


}
