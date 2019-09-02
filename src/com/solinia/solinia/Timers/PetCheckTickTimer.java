package com.solinia.solinia.Timers;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Managers.StateManager;

public class PetCheckTickTimer extends BukkitRunnable {
	@Override
	public void run() {
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			// Check Pets
			try
			{
				LivingEntity pet = StateManager.getInstance().getEntityManager().getPet(player.getUniqueId());
				if (pet != null) {
					ISoliniaLivingEntity solLivingEntity = SoliniaLivingEntityAdapter.Adapt(pet);

					if (solLivingEntity != null)
					{
						if (solLivingEntity.isCurrentlyNPCPet())
						{
							solLivingEntity.PetThink(player);
						}
					}
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
