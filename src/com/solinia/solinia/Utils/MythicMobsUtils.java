package com.solinia.solinia.Utils;

import org.bukkit.entity.LivingEntity;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;

public class MythicMobsUtils {

	public static ActiveMob getActiveMob(LivingEntity bukkitLivingEntity) {
		if (MythicMobs.inst().getAPIHelper() != null && bukkitLivingEntity != null)
			return MythicMobs.inst().getAPIHelper().getMythicMobInstance(bukkitLivingEntity);

		return null;

	}

}
