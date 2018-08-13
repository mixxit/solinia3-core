package com.solinia.solinia.Timers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Managers.StateManager;

public class PetFastCheckTimer extends BukkitRunnable {
	@Override
	public void run() {
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			// Check Pets
			try
			{
				LivingEntity pet = StateManager.getInstance().getEntityManager().getPet(player);
				if (pet != null) {
					
					if (!pet.getWorld().equals(player.getWorld())) {
						StateManager.getInstance().getEntityManager().killPet(player);
					} else {
						
						if (pet.getLocation().distance(player.getLocation()) > 50) {
							if (pet instanceof Creature)
							{
								if (((Creature)pet).getTarget() == null)
								{
									pet.teleport(player);
								}
							} else {
								StateManager.getInstance().getEntityManager().killPet(player);
							}
						}
						
						ISoliniaLivingEntity solLivingEntity = SoliniaLivingEntityAdapter.Adapt(pet);

						if (solLivingEntity != null)
						{
							if (solLivingEntity.isPet())
							{
								solLivingEntity.PetFastThink(player);
							}
						}
					}
				}
			} catch (CoreStateInitException e)
			{
				e.printStackTrace();
			}
		}
	}
}
