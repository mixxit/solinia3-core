package com.solinia.solinia.Utils;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.rit.sucy.player.TargetHelper;

public class RaycastUtils {
	public static boolean isEntityInLineOfSight(LivingEntity entityfrom, Entity entityto, boolean checkDirection) {
		if (entityfrom == null || entityfrom.isDead())
			return false;
		if (entityto == null || entityto.isDead())
			return false;

		if (entityto instanceof LivingEntity) {
			if (((LivingEntity)entityto).hasPotionEffect(PotionEffectType.INVISIBILITY))
				return false;
			
			entityto = (LivingEntity) entityto;
			if (checkDirection)
			{
				double x = entityfrom.getLocation().toVector().distance(entityto.getLocation().toVector());
				Vector direction = entityfrom.getLocation().getDirection().multiply(x);
				Vector answer = direction.add(entityfrom.getLocation().toVector());
				if (answer.distance(entityto.getLocation().toVector()) < 1.37) {
					if (entityfrom.hasLineOfSight(entityto)) {
						return true;
					}
				}
			} else {
				if (entityfrom.hasLineOfSight(entityto)) {
					return true;
				}
			}
		}

		return false;
	}
	
	public static boolean isEntityInLineOfSightCone(LivingEntity entity, Entity target, int arc, int range) {
		if (!TargetHelper.getConeTargets(entity, arc, range).contains(target))
			return false;

		if (entity.hasLineOfSight(target))
			return true;

		return false;
	}
}
