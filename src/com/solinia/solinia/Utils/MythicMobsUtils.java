package com.solinia.solinia.Utils;

import org.bukkit.entity.LivingEntity;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import io.lumine.xikage.mythicmobs.mobs.MobRegistry;

public class MythicMobsUtils {

	public static ActiveMob getActiveMob(LivingEntity bukkitLivingEntity) {
		if (MythicMobs.inst().getAPIHelper() != null && bukkitLivingEntity != null)
		try
		{
			return MythicMobs.inst().getAPIHelper().getMythicMobInstance(bukkitLivingEntity);
		} catch (NullPointerException e)
		{
			
		}

		return null;

	}

}
