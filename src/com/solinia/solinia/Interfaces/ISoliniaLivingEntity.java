package com.solinia.solinia.Interfaces;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public interface ISoliniaLivingEntity 
{
	public LivingEntity getBukkitLivingEntity();

	public void modifyDamageEvent(ISoliniaPlayer player, EntityDamageByEntityEvent damagecause);
	
	public boolean isPet();

	int getLevel();

	void setLevel(int level);

	public void dropLoot();
}
