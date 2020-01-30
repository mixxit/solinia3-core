package com.solinia.solinia.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.EntityBlockFormEvent;
import com.solinia.solinia.Solinia3CorePlugin;
import com.solinia.solinia.Events.PlayerTrackEvent;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaZone;
import com.solinia.solinia.Utils.Utils;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_14_R1.Entity;

public class Solinia3CoreBlockListener implements Listener {
	Solinia3CorePlugin plugin;

	public Solinia3CoreBlockListener(Solinia3CorePlugin solinia3CorePlugin) {
		// TODO Auto-generated constructor stub
		plugin = solinia3CorePlugin;
	}
	
	@EventHandler
	public void onEntityBlockFormEvent(EntityBlockFormEvent event) {
		try {
			for (SoliniaZone zone : StateManager.getInstance().getConfigurationManager().getZones()) {
				if (!zone.getWorld().equals(event.getEntity().getLocation().getWorld().getName()))
					continue;
					
				if (zone.isLocationInside(event.getEntity().getLocation()))
				{
					event.getEntity().sendMessage(ChatColor.GRAY + "* Blocks cannot be formed in defined Zones (Frostwalk etc)");
					Utils.CancelEvent(event);
				}
			}
		} catch (CoreStateInitException e) {

		}
		
	}
}
