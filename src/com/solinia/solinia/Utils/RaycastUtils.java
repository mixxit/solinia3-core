package com.solinia.solinia.Utils;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.rit.sucy.player.TargetHelper;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Models.SpellEffectType;

public class RaycastUtils {
	public static boolean isEntityInLineOfSight(ISoliniaLivingEntity entityfrom, ISoliniaLivingEntity entityto, boolean checkDirection) {
		if (entityfrom == null || entityfrom.getBukkitLivingEntity() == null || entityfrom.getBukkitLivingEntity().isDead())
			return false;
		if (entityto == null || entityto.getBukkitLivingEntity() == null || entityto.getBukkitLivingEntity().isDead())
			return false;

		if (entityto.getBukkitLivingEntity() instanceof LivingEntity) {
			
			if (!entityfrom.canSeeInvis() && !entityfrom.hasSpellEffectType(SpellEffectType.SeeInvis))
			if (((LivingEntity)entityto.getBukkitLivingEntity()).hasPotionEffect(PotionEffectType.INVISIBILITY))
				return false;
			
			// if they are 1 block away they are in line of sight
			if (entityfrom.getBukkitLivingEntity().getLocation().distance(entityto.getBukkitLivingEntity().getLocation()) < 2)
				return true;
			
			if (checkDirection)
			{
				double x = entityfrom.getLocation().toVector().distance(entityto.getLocation().toVector());
				Vector direction = entityfrom.getLocation().getDirection().multiply(x);
				Vector answer = direction.add(entityfrom.getLocation().toVector());
				if (answer.distance(entityto.getLocation().toVector()) < 1.37) {
					if (entityfrom.getBukkitLivingEntity().hasLineOfSight(entityto.getBukkitLivingEntity())) {
						return true;
					}
				}
			} else {
				if (entityfrom.getBukkitLivingEntity().hasLineOfSight(entityto.getBukkitLivingEntity())) {
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
