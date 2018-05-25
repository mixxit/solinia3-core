package com.solinia.solinia.Interfaces;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.solinia.solinia.Solinia3CorePlugin;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Models.DamageHitInfo;
import com.solinia.solinia.Models.InteractionType;
import com.solinia.solinia.Models.SkillType;
import com.solinia.solinia.Models.SoliniaActiveSpell;
import com.solinia.solinia.Models.SoliniaLivingEntity;
import com.solinia.solinia.Models.SoliniaWorld;
import com.solinia.solinia.Models.SpellEffect;
import com.solinia.solinia.Models.SpellEffectType;
import com.solinia.solinia.Models.SpellResistType;

public interface ISoliniaLivingEntity 
{
	public LivingEntity getBukkitLivingEntity();

	public boolean isPet();

	int getLevel();

	void setLevel(int level);

	public void dropLoot();

	int getNpcid();

	void setNpcid(int npcid);

	public void emote(String message);

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

	void configurePetGoals();

	public void doSummon(LivingEntity target);

	boolean isNPC();

	int getSkill(String skillname);

	String getSkillNameFromMaterialInHand(Material materialinhand);

	int getTotalDefense();

	int getDefenseByDefenseSkill();

	boolean isBerserk();

	int getDamageCaps(int base_damage);

	void tryIncreaseSkill(String skillName, int amount);

	int getWeaponDamageBonus(ItemStack itemStack);

	int getOffense(String skillname);

	public void addToHateList(UUID uniqueId, int hate);

	int getTotalToHit(String skillname, int hitChanceBonus);

	int computeToHit(String skillname);

	public DamageHitInfo avoidDamage(SoliniaLivingEntity soliniaLivingEntity, DamageHitInfo hit);

	public boolean checkHitChance(SoliniaLivingEntity soliniaLivingEntity, DamageHitInfo hit);

	public DamageHitInfo meleeMitigation(SoliniaLivingEntity soliniaLivingEntity, DamageHitInfo hit);

	public int getMitigationAC();

	boolean Attack(ISoliniaLivingEntity defender, EntityDamageEvent event, boolean arrowHit, Solinia3CorePlugin plugin);

	public int getSkillDmgTaken(String skill);

	public int getFcDamageAmtIncoming(SoliniaLivingEntity soliniaLivingEntity, int i, boolean b, String skill);

	int getActSpellDamage(ISoliniaSpell soliniaSpell, int value, SpellEffect spellEffect, ISoliniaLivingEntity target);

	int getActSpellHealing(ISoliniaSpell soliniaSpell, int value, SpellEffect spellEffect, ISoliniaLivingEntity target);

	int getMaxStat(String skillname);

	int getRune();

	int reduceAndRemoveRunesAndReturnLeftover(Plugin plugin, int damage);

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

	int getAttk();

	public int getInstrumentMod(ISoliniaSpell iSoliniaSpell);

	public void doCheckForEnemies();

	int getSpellBonuses(SpellEffectType spellEffectType);

	public String getLanguage();

	public String getName();

	public boolean isSpeaksAllLanguages();
	
	public void setSpeaksAllLanguages(boolean speaksAllLanguages);

	void damageHook(double damage, Entity sourceEntity);

	public int hasDeathSave();

	void removeDeathSaves(Plugin plugin);

	void damage(Plugin plugin, double damage, Entity sourceEntity);

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

	ChatColor getLevelCon(ISoliniaLivingEntity solLivingEntity);

	int getMaxBindWound_SE();

	int getBindWound_SE();

	int getAttackSpeed();

	public void PetThink(Player player);
}
