package com.solinia.solinia.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

import com.solinia.solinia.Solinia3CorePlugin;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Events.SoliniaEntitySpellsRemovalEvent;
import com.solinia.solinia.Events.SoliniaEntitySpellsRunEvent;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaEntitySpells;

public class SoliniaEntitySpellsRemovalEventListener implements Listener {
	Solinia3CorePlugin plugin;

	public SoliniaEntitySpellsRemovalEventListener(Solinia3CorePlugin solinia3CorePlugin) {
		this.plugin = solinia3CorePlugin;
	}
	
	@EventHandler
    public void onEntityEffectRun(SoliniaEntitySpellsRemovalEvent event) {
		try
		{
			StateManager.getInstance().getEntityManager().removeSpellEffects(event.getUuid(), false, false);
		} catch (CoreStateInitException e)
		{
			// plugin not ready yet, doesnt matter though
		}
	}

}