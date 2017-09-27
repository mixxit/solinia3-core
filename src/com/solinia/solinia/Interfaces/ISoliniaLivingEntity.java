package com.solinia.solinia.Interfaces;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.Plugin;

import com.solinia.solinia.Models.InteractionType;
import com.solinia.solinia.Models.SpellResistType;

public interface ISoliniaLivingEntity 
{
	public LivingEntity getBukkitLivingEntity();

	public void modifyDamageEvent(Plugin plugin, LivingEntity damager, EntityDamageByEntityEvent damagecause);
	
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
}
