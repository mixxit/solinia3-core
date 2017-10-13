package com.solinia.solinia.Listeners;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;

import com.solinia.solinia.Solinia3CorePlugin;
import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Models.SoliniaLivingEntity;

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
				if (solEntity.getNpcid() > 0)
					event.setCancelled(true);
				} catch (CoreStateInitException e)
			{
				// do nothing
			}
		}
	}
}
