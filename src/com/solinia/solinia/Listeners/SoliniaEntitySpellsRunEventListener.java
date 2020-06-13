package com.solinia.solinia.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import com.solinia.solinia.Solinia3CorePlugin;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Events.SoliniaEntitySpellsRunEvent;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaEntitySpells;

public class SoliniaEntitySpellsRunEventListener implements Listener {
	Solinia3CorePlugin plugin;

	public SoliniaEntitySpellsRunEventListener(Solinia3CorePlugin solinia3CorePlugin) {
		this.plugin = solinia3CorePlugin;
	}
	
	@EventHandler
    public void onEntityEffectRun(SoliniaEntitySpellsRunEvent event) {
		Entity entity = Bukkit.getEntity(event.getUuid());
		if (entity == null || entity.isDead() || (!(entity instanceof LivingEntity)))
			return;
		
		LivingEntity livingEntity = (LivingEntity)entity;
		
		try
		{
			SoliniaEntitySpells entityEffects = StateManager.getInstance().getEntityManager().getActiveEntitySpells(livingEntity);
			entityEffects.run(plugin, true);
			
			if (entityEffects.getLivingEntity() != null && entityEffects.getLivingEntity() instanceof Player)
				SoliniaPlayerAdapter.Adapt((Player)entityEffects.getLivingEntity()).sendEffects();
		} catch (CoreStateInitException e)
		{
			// plugin not ready yet, doesnt matter though
		}
	}

}
