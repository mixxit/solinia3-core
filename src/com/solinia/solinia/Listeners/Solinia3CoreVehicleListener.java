package com.solinia.solinia.Listeners;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.potion.PotionEffectType;

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
