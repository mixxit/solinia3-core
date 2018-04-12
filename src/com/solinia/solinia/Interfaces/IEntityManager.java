package com.solinia.solinia.Interfaces;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import com.solinia.solinia.Exceptions.InsufficientTemporaryMerchantItemException;
import com.solinia.solinia.Models.UniversalMerchantEntry;
import com.solinia.solinia.Models.SoliniaAlignmentChunk;
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

	LivingEntity getPet(Player player);

	void killPet(Player player);

	void killAllPets();

	LivingEntity setPet(Player player, LivingEntity entity);

	List<LivingEntity> getAllWorldPets();

	void clearEntityEffects(Plugin plugin, UUID uniqueId);

	void clearEntityFirstEffectOfType(Plugin plugin, LivingEntity livingEntity, SpellEffectType poisoncounter);

	void toggleTrance(UUID uniqueId);

	boolean getTrance(UUID uuid);

	void setTrance(UUID uuid, Boolean enabled);

	LivingEntity SpawnPet(Plugin plugin, Player owner, ISoliniaSpell spell);

	void spellTick(Plugin plugin);

	boolean addActiveEntitySpell(Plugin plugin, LivingEntity targetEntity, SoliniaSpell soliniaSpell,
			LivingEntity sourceEntity);

	void doNPCSpellCast(Plugin plugin);

	void removeSpellEffects(Plugin plugin, UUID uuid);

	//void addTemporaryMerchantItem(int npcid, int itemid, int amount);

	//void removeTemporaryMerchantItem(int npcid, int itemid, int amount) throws InsufficientTemporaryMerchantItemException;

	List<ISoliniaNPCMerchantEntry> getNPCMerchantCombinedEntries(ISoliniaNPC npc);

	//List<ISoliniaNPCMerchantEntry> getTemporaryMerchantItems(ISoliniaNPC npc);

	boolean hasEntityEffectType(LivingEntity livingEntity, SpellEffectType type);

	void removeMezzed(LivingEntity livingEntity, Timestamp expiretimestamp);

	void doNPCSummon(Plugin plugin);

	void clearEntityFirstEffect(Plugin plugin, LivingEntity livingEntity);

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

	void removeSpellEffectsOfSpellId(Plugin plugin, UUID uuid, int spellId);

	void doNPCCheckForEnemies();

	UniversalMerchant getUniversalMerchant(UUID universalMerchant);

	Inventory getMerchantInventory(UUID playerUUID, int pageno, UniversalMerchant universalMerchant);

	Inventory getNPCMerchantInventory(UUID playerUUID, ISoliniaNPC npc, int pageno);

	Inventory getTradeShopMerchantInventory(UUID playerUUID, SoliniaAlignmentChunk alignmentChunk, int pageno);

	ConcurrentHashMap<UUID, Boolean> getPlayerInTerritory();

	void setPlayerInTerritory(ConcurrentHashMap<UUID, Boolean> playerInTerritory);

}
