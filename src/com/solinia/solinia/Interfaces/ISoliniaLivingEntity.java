package com.solinia.solinia.Interfaces;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
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
import com.solinia.solinia.Models.StatType;

import io.lumine.xikage.mythicmobs.mobs.ActiveMob;

public interface ISoliniaLivingEntity  {
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

	boolean getDodgeCheck();

	boolean getRiposteCheck();

	int getMaxDamage();

	int getStrength();

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

	int getTotalDefense();

	int computeDefense();

	boolean isBerserk();

	int getDamageCaps(int base_damage);

	void tryIncreaseSkill(SkillType skillType, int amount);

	int getWeaponDamageBonus(ItemStack itemStack);

	public void addToHateList(UUID uniqueId, int hate, boolean isYellForHelp);

	public DamageHitInfo avoidDamage(ISoliniaLivingEntity soliniaLivingEntity, DamageHitInfo hit);

	public boolean checkHitChance(ISoliniaLivingEntity soliniaLivingEntity, DamageHitInfo hit);

	public DamageHitInfo meleeMitigation(ISoliniaLivingEntity soliniaLivingEntity, DamageHitInfo hit);

	public int getMitigationAC();

	public int getSkillDmgTaken(SkillType skillType);

	public int getFcDamageAmtIncoming(ISoliniaLivingEntity soliniaLivingEntity, int i, boolean b, SkillType skillType);

	int getActSpellDamage(ISoliniaSpell soliniaSpell, int value, SpellEffect spellEffect, ISoliniaLivingEntity target);

	int getActSpellHealing(ISoliniaSpell soliniaSpell, int value);

	int getMaxStat(StatType statType);

	int getRune();

	int reduceAndRemoveRunesAndReturnLeftover(int damage);

	public boolean isInvulnerable();

	public int ACSum();

	double getHPRatio();

	double getManaRatio();

	boolean aiCastSpell(Plugin plugin, ISoliniaNPC npc, LivingEntity target, int iChance, int iSpellTypes)
			throws CoreStateInitException;

	boolean aiDoSpellCast(Plugin plugin, ISoliniaSpell spell, ISoliniaLivingEntity target, int manaCost);

	void aiEngagedCastCheck(Plugin plugin, ISoliniaNPC npc, LivingEntity castingAtEntity) throws CoreStateInitException;

	boolean aiCheckCloseBeneficialSpells(Plugin plugin, ISoliniaNPC npc, int iChance, int iRange, int iSpellTypes)
			throws CoreStateInitException;

	public boolean isRooted();

	public int countDispellableBuffs();

	Collection<SoliniaActiveSpell> getActiveSpells();

	public int getInstrumentMod(ISoliniaSpell iSoliniaSpell);

	public void doCheckForEnemies();

	int getSpellBonuses(SpellEffectType spellEffectType);

	public SkillType getLanguage();

	public String getName();

	public boolean isSpeaksAllLanguages();

	public void setSpeaksAllLanguages(boolean speaksAllLanguages);

	public int hasDeathSave();

	void removeDeathSaves();

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

	boolean isPlant();

	int getMaxBindWound_SE();

	int getBindWound_SE();

	int getAttackSpeed();

	public void PetThink(Player playerOwner);

	public void PetFastThink(Player playerOwner);

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

	void damageAlertHook(double damage, Entity sourceEntity);

	void autoAttackEnemy(ISoliniaLivingEntity solLivingEntity);

	SoliniaWorld getWorld();

	boolean canUseItem(ItemStack itemInMainHand);

	int getMaxItemAttackSpeedPct();

	Timestamp getLastDoubleAttack();

	void setLastDoubleAttack();

	Timestamp getLastRiposte();

	void setLastRiposte();

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

	public boolean hasSpellEffectType(SpellEffectType type);

	Timestamp getLastDisarm();

	void tryDisarm(ISoliniaLivingEntity target);

	boolean canDisarm();

	boolean isAttackAllowed(ISoliniaLivingEntity target, boolean isSpellAttack);

	public void disarm(ISoliniaLivingEntity disarmer, int chance);

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

	int getSpecialAbility(int specialAbilityId);

	int getItemBonuses(SpellEffectType spellEffectType);

	boolean getInvul();

	boolean combatRange(ISoliniaLivingEntity ca_target);

	int checkBaneDamage(ISoliniaItem item, ItemStack itemStack);

	int getBaseSkillDamage(SkillType skill_to_use, LivingEntity entityTarget);

	int getSkill(SkillType skilltype);

	int offense(SkillType skillType);

	int getWeaponDamage(ISoliniaLivingEntity against, ItemStack weaponItemStack, int hate);

	public int resistElementalWeaponDmg(ISoliniaItem weapon_item, ItemStack weaponItemStack);

	boolean Attack(ISoliniaLivingEntity defender, int Hand, boolean bRiposte, boolean IsStrikethrough,
			boolean IsFromSpell);

	boolean isCasting();

	void Damage(ISoliniaLivingEntity mob, int damage, int spell_id, SkillType attack_skill, boolean avoidable,
			int buffslot, boolean iBuffTic);

	boolean DivineAura();

	boolean IsValidSpell(int spell_id);

	boolean isSneaking();

	void setHPChange(int hpchange, LivingEntity causeOfEntityHpChange);

	public boolean isFacingMob(SoliniaLivingEntity soliniaLivingEntity);

	void buffFadeByEffect(SpellEffectType type);

	void healDamage(double amount, ISoliniaLivingEntity caster, int spell_id);

	void tryWeaponProc(ItemStack itemInMainHand, ISoliniaLivingEntity defender, int hand);

	void tryWeaponProc(ItemStack inst, ISoliniaItem weapon, ISoliniaLivingEntity on, int hand);

	boolean IsPet();

	boolean hasSufficientArrowReagents(int count);

	boolean checkDoubleAttack();

	public void tryDefensiveProc(SoliniaLivingEntity soliniaLivingEntity, int hand);

	SoliniaActiveSpell getFirstActiveSpellWithSpellEffectType(SpellEffectType type);

	int getAABonuses(SpellEffectType atk);

	boolean canDualWield();

	boolean canDoubleAttack();

	int computeToHit(SkillType skillType);

	int getTotalToHit(SkillType skillType, int hitChanceBonus);

	int getTotalItemStat(StatType stat);

	void doClassAttacks(ISoliniaLivingEntity ca_target, SkillType skillType, boolean isRiposte);

	void doMend();

	void sendVitalsPacketsToAnyoneTargettingMe();

	public boolean checkLosFN(ISoliniaLivingEntity soliniaLivingEntity);

	void BreakInvis();

	public boolean checkLosFN(ISoliniaLivingEntity solTarget, boolean checkDirection);

}
