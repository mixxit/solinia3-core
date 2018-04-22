package com.solinia.solinia.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.solinia.solinia.Solinia3CorePlugin;
import com.solinia.solinia.Events.SoliniaNPCUpdatedEvent;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Managers.StateManager;

public class Solinia3CoreNPCUpdatedListener implements Listener {
	Solinia3CorePlugin plugin;

	public Solinia3CoreNPCUpdatedListener(Solinia3CorePlugin plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void OnNPCUpdated(SoliniaNPCUpdatedEvent event) {
		if (event.isCancelled())
			return;
		
		try {
			StateManager.getInstance().getEntityManager().getNPCEntityProvider().updateNpc(event.getNPC());
			if (event.getReloadMythicMobs() == true)
				StateManager.getInstance().getEntityManager().getNPCEntityProvider().reloadProvider();
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}