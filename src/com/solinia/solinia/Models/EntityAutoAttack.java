package com.solinia.solinia.Models;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Utils.Utils;

public class EntityAutoAttack {
	UUID entityUuid;
	private boolean playerAutoAttacking = false;
	private Timestamp nextAttackTime;

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
	
	public void setLastUpdatedTimeNow(ISoliniaLivingEntity me) {
		LocalDateTime datetime = LocalDateTime.now();
		Timestamp nowtimestamp = Timestamp.valueOf(datetime);
		
		// NB - The auto attack runs every 50MS (1 tick), there are 20 ticks in a second (1000ms)
		int numberOfTicks = (int) (me.getAutoAttackTimerFrequencySeconds() * Utils.TICKS_PER_SECOND);
    	// Our lowest attack cap is two ticks
    	if (numberOfTicks < 2)
    		numberOfTicks = 2;
    	
    	int timeToWaitInMilliseconds = numberOfTicks * 50;
    	
		this.setNextAttackTime(new Timestamp(nowtimestamp.getTime() + timeToWaitInMilliseconds));
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

	public boolean canAutoAttack() {
		if (this.nextAttackTime == null)
			return true;
		
		LocalDateTime datetime = LocalDateTime.now();
		Timestamp nowtimestamp = Timestamp.valueOf(datetime);
		//
		return nowtimestamp.after(this.nextAttackTime);
	}

	public Timestamp getNextAttackTime() {
		return nextAttackTime;
	}

	public void setNextAttackTime(Timestamp nextAttackTime) {
		this.nextAttackTime = nextAttackTime;
	}

	/*public void setTimer(int timer) {
		this.timer = timer;
	}

	public void setTimerFromSoliniaLivingEntity(ISoliniaLivingEntity solLivingEntity) {
		int rate = (int) (solLivingEntity.getAutoAttackTimerFrequencySeconds() * Utils.TICKS_PER_SECOND);
    	// Our lowest attack cap
    	if (rate < 2)
    		rate = 2;
		
    	setTimer(rate);
	}*/
	
}
