package com.solinia.solinia.Interfaces;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Models.DamageHitInfo;
import com.solinia.solinia.Models.FactionStandingType;
import com.solinia.solinia.Models.FocusEffect;
import com.solinia.solinia.Models.InteractionType;
import com.solinia.solinia.Models.NumHit;
import com.solinia.solinia.Models.PacketMobVitals;
import com.solinia.solinia.Models.SkillType;
import com.solinia.solinia.Models.SoliniaActiveSpell;
import com.solinia.solinia.Models.SoliniaLivingEntity;
import com.solinia.solinia.Models.SoliniaWorld;
import com.solinia.solinia.Models.SpellEffect;
import com.solinia.solinia.Models.SpellEffectType;
import com.solinia.solinia.Models.SpellResistType;

import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import net.minecraft.server.v1_14_R1.Tuple;

public interface ISoliniaLivingEntity 
{
	public LivingEntity getBukkitLivingEntity();

	int getLevel();

	void setLevel(int level);

	public void dropLoot();

	int getNpcid();

	void setNpcid(int npcid);

	public void emote(String message, boolean isBardSongFilterable);

	public void doRandomChat();

	void doSlayChat();

	boolean isPlayer();

	Integer getMana();

	public int getResists(SpellResistType type);

	public int getResistsFromActiveEffects(SpellResistType type);

	void say(String message);

	void processInteractionEvent(LivingEntity triggerentity, InteractionType type, String data);

	void doSpellCast(Plugin plugin, LivingEntity livingEntity);

	public int getProcChancePct();

	boolean getDodgeCheck();

	boolean getDoubleAttackCheck();

	boolean getRiposteCheck();

	int getMaxDamage();

	int getStrength();
	
	int getTotalItemStat(String stat);

	int getStamina();

	int getAgility();

	int getDexterity();

	int getIntelligence();

	int getWisdom();

	int getCharisma();

	public int getMaxMP();

	ISoliniaClass getClassObj();

	public double getMaxHP();

	boolean isUndead();
	boolean isAnimal();

	public void doSummon(LivingEntity target);

	boolean isNPC();

	int getSkill(String skillname);

	int getTotalDefense();

	int computeDefense();

	boolean isBerserk();

	int getDamageCaps(int base_damage);

	void tryIncreaseSkill(String skillName, int amount);

	int getWeaponDamageBonus(ItemStack itemStack);

	int getOffense(String skillname);

	public void addToHateList(UUID uniqueId, int hate, boolean isYellForHelp);

	int getTotalToHit(String skillname, int hitChanceBonus);

	int computeToHit(String skillname);

	public DamageHitInfo avoidDamage(SoliniaLivingEntity soliniaLivingEntity, DamageHitInfo hit);

	public boolean checkHitChance(SoliniaLivingEntity soliniaLivingEntity, DamageHitInfo hit);

	public DamageHitInfo meleeMitigation(SoliniaLivingEntity soliniaLivingEntity, DamageHitInfo hit);

	public int getMitigationAC();

	public int getSkillDmgTaken(String skill);

	public int getFcDamageAmtIncoming(SoliniaLivingEntity soliniaLivingEntity, int i, boolean b, String skill);

	int getActSpellDamage(ISoliniaSpell soliniaSpell, int value, SpellEffect spellEffect, ISoliniaLivingEntity target);

	int getActSpellHealing(ISoliniaSpell soliniaSpell, int value, SpellEffect spellEffect, ISoliniaLivingEntity target);

	int getMaxStat(String skillname);

	int getRune();

	int reduceAndRemoveRunesAndReturnLeftover(int damage);

	public boolean isInvulnerable();

	public int ACSum();

	double getHPRatio();

	double getManaRatio();

	boolean aiCastSpell(Plugin plugin, ISoliniaNPC npc, LivingEntity target, int iChance, int iSpellTypes) throws CoreStateInitException;

	boolean aiDoSpellCast(Plugin plugin, ISoliniaSpell spell, ISoliniaLivingEntity target, int manaCost);

	void aiEngagedCastCheck(Plugin plugin, ISoliniaNPC npc, LivingEntity castingAtEntity) throws CoreStateInitException;

	boolean aiCheckCloseBeneficialSpells(Plugin plugin, ISoliniaNPC npc, int iChance, int iRange, int iSpellTypes) throws CoreStateInitException;

	public boolean isRooted();

	public int countDispellableBuffs();

	Collection<SoliniaActiveSpell> getActiveSpells();

	public int getInstrumentMod(ISoliniaSpell iSoliniaSpell);

	public void doCheckForEnemies();

	int getSpellBonuses(SpellEffectType spellEffectType);

	public String getLanguage();

	public String getName();

	public boolean isSpeaksAllLanguages();
	
	public void setSpeaksAllLanguages(boolean speaksAllLanguages);

	public int hasDeathSave();

	void removeDeathSaves();

	void damage(double damage, Entity sourceEntity, boolean tryProc, boolean isMelee, boolean isOffhand);

	public boolean isBehindEntity(LivingEntity livingEntity);

	int getTotalItemAC();

	List<ISoliniaItem> getEquippedSoliniaItems(boolean ignoreMainhand);

	List<ISoliniaItem> getEquippedSoliniaItems();

	double getItemHp();

	double getItemMana();

	int getTotalItemSkillMod(SkillType skilltype);

	SoliniaWorld getSoliniaWorld();

	void updateMaxHp();

	void say(String message, LivingEntity messageto, boolean allowlanguagelearn);

	void sayto(Player player, String message);

	void sayto(Player player, String message, boolean allowlanguagelearn);

	boolean isFeignedDeath();

	void setFeigned(boolean feigned);

	boolean getDualWieldCheck();

	boolean isPlant();

	int getMaxBindWound_SE();

	int getBindWound_SE();

	int getAttackSpeed();

	public void PetThink(Player playerOwner);

	public void PetFastThink(Player playerOwner);

	void removeActiveSpellsWithEffectType(SpellEffectType spellEffectType);

	int getFocusEffect(FocusEffect focusEffectType, ISoliniaSpell spell);

	boolean isCombatProc(ISoliniaSpell spell);

	int getRaceId();

	int getActSpellCasttime(ISoliniaSpell spell, int casttime);

	Location getLocation();

	void sendMessage(String message);

	public boolean canAttackTarget(ISoliniaLivingEntity defender);

	public boolean isMezzed();

	boolean isStunned();

	public void removeNonCombatSpells();

	int calculateDamageFromDamageEvent(Entity originalDamager, boolean ismagic, int damage);

	void damageAlertHook(double damage, Entity sourceEntity);

	void autoAttackEnemy(ISoliniaLivingEntity solLivingEntity);

	SoliniaWorld getWorld();

	public void setHealth(double health);

	boolean canUseItem(ItemStack itemInMainHand);

	int getMaxItemAttackSpeedPct();

	Timestamp getLastDualWield();

	void setLastDualWield();

	Timestamp getLastDoubleAttack();

	void setLastDoubleAttack();

	Timestamp getLastRiposte();

	void setLastRiposte();

	void doRiposte(UUID defenderUuid, int originalDamage);

	public void doDoubleAttack(UUID defenderUUID, int final_damagedone);

	public void doDualWield(UUID defenderUUID, int final_damagedone, int offHandItemId);

	void doProcItem(int procItemId, UUID attackerEntityUUID, UUID defenderEntityUUID, boolean offHand);

	void doTeleportAttack(LivingEntity teleportedEntity);

	boolean doCheckForDespawn();

	public void StopSinging();

	boolean checkHateTargets();

	void clearHateList();
	
	void wipeHateList();

	void setAttackTarget(LivingEntity entity);

	void checkNumHitsRemaining(NumHit type);

	void checkNumHitsRemaining(NumHit type, int buffSlot, Integer spellId);

	LivingEntity getAttackTarget();

	void resetPosition(boolean resetHealth);

	public ActiveMob getActiveMob();

	public boolean isCurrentlyNPCPet();

	public boolean isCharmed();

	public Entity getOwnerEntity();

	boolean passCharismaCheck(LivingEntity caster, ISoliniaSpell spell) throws CoreStateInitException;

	float getResistSpell(ISoliniaSpell spell, LivingEntity caster) throws CoreStateInitException;

	void doMeleeSkillAttackDmg(LivingEntity other, int weapon_damage, SkillType skillinuse, int chance_mod, int focus,
			boolean canRiposte, int reuseTime);

	boolean canDoSpecialAttack(LivingEntity other);

	public int getMaxTotalSlots();

	public boolean isImmuneToSpell(ISoliniaSpell spell);

	public int getMainWeaponDelay();

	ISoliniaItem getSoliniaItemInMainHand();

	ISoliniaItem getSoliniaItemInOffHand();

	void setLastMeleeAttack();

	boolean getLastMeleeAttackCheck();

	Timestamp getLastMeleeAttack();

	float getAutoAttackTimerFrequencySeconds();

	int AttackWithMainHand(ISoliniaLivingEntity defender, boolean arrowHit, int baseDamage);

	ISoliniaRace getRace();

	ISoliniaGod getGod();

	public void tryApplySpellOnSelf(int spellId, String requiredWeaponSkillType);

	public void sendHateList(LivingEntity recipient);

	void clearTargetsAgainstMe();

	void setEntityTarget(LivingEntity target);

	LivingEntity getEntityTarget();

	PacketMobVitals toPacketMobVitals(int partyMember, boolean withMana);

	void setLastCallForAssist();

	Timestamp getLastCallForAssist();

	boolean canCallForAssist();

	boolean isSocial();

	ISoliniaNPC getNPC();

	boolean isSpecialKOSOrNeutralFaction();

	List<ISoliniaLivingEntity> getNPCsInRange(int iRange);

	void aIYellForHelp(ISoliniaLivingEntity attacker);

	void doCallForAssist(LivingEntity livingEntity);

	public ISoliniaLivingEntity getOwnerOrSelf();

	FactionStandingType getNPCvsNPCReverseFactionCon(ISoliniaLivingEntity iOther);

	ISoliniaLivingEntity getOwnerSoliniaLivingEntity();

	public boolean checkAggro(ISoliniaLivingEntity attacker);

	public boolean hasSpellEffectType(SpellEffectType harmony);

	Timestamp getLastDisarm();

	void tryDisarm(ISoliniaLivingEntity target);

	boolean canDisarm();

	boolean isAttackAllowed(ISoliniaLivingEntity target, boolean isSpellAttack);

	public void disarm(SoliniaLivingEntity disarmer, int chance);

	public boolean isInCombat();
	public boolean isEngaged();

	Location getSpawnPoint();

	org.bukkit.ChatColor getLevelCon(int mylevel);

	boolean hasHate();

	List<UUID> getHateListUUIDs();

	int getHateListAmount(UUID target);

	boolean isInHateList(UUID uniqueId);

	void removeFromHateList(UUID targetUUID);

	long getReverseAggroCount();

	public void resetReverseAggro();

	int getTotalAtk();

	public void sendStats(LivingEntity player);
}
