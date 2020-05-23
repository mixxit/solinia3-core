package com.solinia.solinia.Timers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Utils.EntityUtils;
import com.solinia.solinia.Utils.Utils;

public class SoliniaLivingEntityUpdateTargetWindowTimer extends BukkitRunnable {

	@Override
	public void run() {
		List<String> completedEntities = new ArrayList<String>();
		// Check each player and check entities near player
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			try {
				// Player first
				SoliniaLivingEntityAdapter.Adapt(player).sendVitalsPacketsToAnyoneTargettingMe();

				// Then nearby npcs
				for (Entity entityThatWillAutoAttack : player.getNearbyEntities(25, 25, 25)) {
					if (entityThatWillAutoAttack instanceof Player)
						continue;

					if (!(entityThatWillAutoAttack instanceof LivingEntity))
						continue;

					LivingEntity livingEntityThatWillAutoAttack = (LivingEntity) entityThatWillAutoAttack;

					if (!(entityThatWillAutoAttack instanceof Creature))
						continue;

					if (entityThatWillAutoAttack.isDead())
						continue;

					if (!EntityUtils.isLivingEntityNPC(livingEntityThatWillAutoAttack))
						continue;

					if (completedEntities.contains(livingEntityThatWillAutoAttack.getUniqueId().toString()))
						continue;

					completedEntities.add(livingEntityThatWillAutoAttack.getUniqueId().toString());
					SoliniaLivingEntityAdapter.Adapt(livingEntityThatWillAutoAttack)
							.sendVitalsPacketsToAnyoneTargettingMe();

				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
