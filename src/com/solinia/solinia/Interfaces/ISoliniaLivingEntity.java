package com.solinia.solinia.Interfaces;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.solinia.solinia.Solinia3CorePlugin;
import com.solinia.solinia.Models.DamageHitInfo;
import com.solinia.solinia.Models.InteractionType;
import com.solinia.solinia.Models.SoliniaLivingEntity;
import com.solinia.solinia.Models.SpellEffect;
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

	void say(String message, LivingEntity messageto);

	void doSpellCast(Plugin plugin, LivingEntity livingEntity);

	public int getProcChancePct();

	boolean getDodgeCheck();

	boolean getDoubleAttackCheck();

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

	void configurePetGoals();

	public void doSummon(Plugin plugin, LivingEntity target);

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

	int reduceAndRemoveRunesAndReturnLeftover(int damage);
}
