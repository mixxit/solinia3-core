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
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Utils.EntityUtils;

public class EntityAutoAttackTimer extends BukkitRunnable {
	@Override
	public void run() {
		try
		{
			runEntitiesAutoAttack();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void runEntitiesAutoAttack() {
List<String> completedEntities = new ArrayList<String>();
		
		// Check each player and check entities near player
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			try
			{
				// Player first
				ISoliniaLivingEntity solLivingEntity = SoliniaLivingEntityAdapter.Adapt(player);
				if (solLivingEntity != null)
					solLivingEntity.processAutoAttack(false);

				
				// Then nearby npcs
				for(Entity entityThatWillAutoAttack : player.getNearbyEntities(25, 25, 25))
				{
					if (entityThatWillAutoAttack instanceof Player)
						continue;
					
					if (!(entityThatWillAutoAttack instanceof LivingEntity))
						continue;
					
					LivingEntity livingEntityThatWillAutoAttack = (LivingEntity)entityThatWillAutoAttack;
					
					if (!(entityThatWillAutoAttack instanceof Creature))
						continue;
					
					if(entityThatWillAutoAttack.isDead())
						continue;
					
					Creature creatureThatWillAttack = (Creature)entityThatWillAutoAttack;
					if (creatureThatWillAttack.getTarget() == null)
						continue;
					
					if (!EntityUtils.isLivingEntityNPC(livingEntityThatWillAutoAttack))
						continue;
					
					if (completedEntities.contains(livingEntityThatWillAutoAttack.getUniqueId().toString()))
						continue;
					
					completedEntities.add(livingEntityThatWillAutoAttack.getUniqueId().toString());
					ISoliniaLivingEntity solLe = SoliniaLivingEntityAdapter.Adapt(creatureThatWillAttack);
					if (solLe == null)
						continue;
					
					solLe.processAutoAttack(false);
				}
			
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}		
	}
}
