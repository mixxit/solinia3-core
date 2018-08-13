package com.solinia.solinia.Models;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class EntityAutoAttack {
	UUID entityUuid;
	private boolean playerAutoAttacking = false;
	private int timer = 20;
	private int baseAttackRate = 20;

	public EntityAutoAttack(LivingEntity entity) {
		this.entityUuid = entity.getUniqueId();
	}
	
	public Player getLivingEntityAsPlayer()
	{
		Entity entity = Bukkit.getEntity(entityUuid);
		if (entity != null && entity instanceof Player)
		return (Player)entity;
		
		return null;
	}
	
	public LivingEntity getLivingEntity()
	{
		Entity entity = Bukkit.getEntity(entityUuid);
		if (!(entity instanceof LivingEntity))
			return null;
		
		return (LivingEntity)entity;
	}
	
	public boolean isPlayer()
	{
		if (getLivingEntityAsPlayer() != null)
			return true;
		
		return false;
	}

	public boolean isAutoAttacking() {
		if (isPlayer())
		{
			if (getLivingEntity().isDead())
			{
				playerAutoAttacking = false;
			}
			return playerAutoAttacking;
		}
		
		LivingEntity livingEntity = getLivingEntity();
		
		if (getLivingEntity() == null)
			return false;
		
		if (livingEntity.isDead())
			return false;
		
		if (livingEntity != null && livingEntity instanceof Creature)
		{
			if (((Creature)livingEntity).getTarget() != null)
				return true;
		}
		
		return false;
	}

	public void setAutoAttacking(boolean autoAttacking) {
		if (isPlayer())
			this.playerAutoAttacking = autoAttacking;
	}

	public int getTimer() {
		return timer;
	}

	public void setTimer(int timer) {
		this.timer = timer;
	}

	public void setTimerFromAttackSpeed(int attackSpeed) {
		int rate = (int)Math.ceil(baseAttackRate + ((100d - attackSpeed) * (baseAttackRate / 100d)));
    	// Our lowest attack cap
    	if (rate < 2)
    		rate = 2;
		
    	setTimer(rate);
	}

}
