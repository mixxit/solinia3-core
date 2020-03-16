package com.solinia.solinia.Listeners;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.potion.PotionEffectType;
import org.spigotmc.event.entity.EntityDismountEvent;

import com.solinia.solinia.Solinia3CorePlugin;
import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Utils.Utils;

public class Solinia3CoreVehicleListener implements Listener {
	Solinia3CorePlugin plugin;

	public Solinia3CoreVehicleListener(Solinia3CorePlugin solinia3CorePlugin) {
		// TODO Auto-generated constructor stub
		plugin = solinia3CorePlugin;
	}
	
	@EventHandler
	public void onVehicleEnterEvent(EntityDismountEvent event) {
		if (event.getDismounted() instanceof Arrow)
		{
			final UUID entityUuid = event.getDismounted().getUniqueId();
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(
					Bukkit.getPluginManager().getPlugin("Solinia3Core"), new Runnable() {
						public void run() {
							org.bukkit.entity.Entity entity = Bukkit.getEntity(entityUuid);
							if (entity == null)
								return;
							
							entity.remove();
						}
					});
					
		}
		event.getDismounted().teleport(event.getDismounted().getLocation().add(0,0.5,0));
	}
	
	@EventHandler
	public void onVehicleEnterEvent(VehicleEnterEvent event) {
		if (event.getEntered() instanceof LivingEntity)
		{
			try
			{
				ISoliniaLivingEntity solEntity = SoliniaLivingEntityAdapter.Adapt((LivingEntity)event.getEntered());
				
				if (solEntity != null)
				{
					if (solEntity.getNpcid() > 0)
						Utils.CancelEvent(event);
					
					if (solEntity.isPlayer())
					{
						if (solEntity.getBukkitLivingEntity().hasPotionEffect(PotionEffectType.SPEED))
						{
							solEntity.getBukkitLivingEntity().sendMessage("You cannot use boats when a runspeed buff is active (it would kick you) - Cancel it with /effects");
							Utils.CancelEvent(event);
						}
						
					}
				}
				
				
			} catch (CoreStateInitException e)
			{
				// do nothing
			}
		}
	}
}
