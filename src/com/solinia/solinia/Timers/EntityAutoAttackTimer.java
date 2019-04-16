package com.solinia.solinia.Timers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.EntityAutoAttack;
import com.solinia.solinia.Utils.Utils;

import net.md_5.bungee.api.ChatColor;

public class EntityAutoAttackTimer extends BukkitRunnable {
	@Override
	public void run() {
		List<String> completedEntities = new ArrayList<String>();
		
		// Check each player and check entities near player
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			// Player first
			processLivingEntityAutoAttack(player);
			
			try
			{
				// Then nearby npcs
				for(Entity entityThatWillAutoAttack : player.getNearbyEntities(50, 50, 50))
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
					
					if (!Utils.isLivingEntityNPC(livingEntityThatWillAutoAttack))
						continue;
					
					if (completedEntities.contains(livingEntityThatWillAutoAttack.getUniqueId().toString()))
						continue;
					
					completedEntities.add(livingEntityThatWillAutoAttack.getUniqueId().toString());
					processLivingEntityAutoAttack(creatureThatWillAttack);
				}
			
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	private void processLivingEntityAutoAttack(LivingEntity entityForAutoAttack) {
		try
		{
			EntityAutoAttack autoAttack = StateManager.getInstance().getEntityManager().getEntityAutoAttack(entityForAutoAttack);
	
			if (!autoAttack.isAutoAttacking())
				return;
				
			if (entityForAutoAttack.isDead())
			{
				StateManager.getInstance().getEntityManager().setEntityAutoAttack(entityForAutoAttack, false);
				return;
			}
			
			if (autoAttack.getTimer() > 0)
			{
				autoAttack.setTimer(autoAttack.getTimer() - 1);
				return;
			}
			
			LivingEntity target = StateManager.getInstance().getEntityManager().getEntityTarget((LivingEntity)entityForAutoAttack);
			if (target != null)
			{
				if (target instanceof Player)
				{
					if (((Player)target).getGameMode() != GameMode.SURVIVAL)
					{
						if (entityForAutoAttack instanceof Player)
							entityForAutoAttack.sendMessage(ChatColor.GRAY + "* Your target is not in SURVIVAL gamemode!");
						
						StateManager.getInstance().getEntityManager().setEntityAutoAttack(entityForAutoAttack, false);
					}
				}
				
				if (target.isDead())
				{
					if (entityForAutoAttack instanceof Player)
					entityForAutoAttack.sendMessage(ChatColor.GRAY + "* Your target is dead!");
					StateManager.getInstance().getEntityManager().setEntityAutoAttack(entityForAutoAttack, false);
					return;
				}
				
				ISoliniaLivingEntity solLivingEntityTarget = SoliniaLivingEntityAdapter.Adapt(target);
				ISoliniaLivingEntity solLivingEntityAttacker = SoliniaLivingEntityAdapter.Adapt(entityForAutoAttack);
				
				if (solLivingEntityTarget != null && solLivingEntityAttacker != null)
				{
					// reset timer
					autoAttack.setTimerFromSoliniaLivingEntity(solLivingEntityAttacker);
					solLivingEntityAttacker.autoAttackEnemy(solLivingEntityTarget);
				} else {
					if (entityForAutoAttack instanceof Player)
					entityForAutoAttack.sendMessage(ChatColor.GRAY + "* Could not find target to attack!");
					StateManager.getInstance().getEntityManager().setEntityAutoAttack(entityForAutoAttack, false);
					return;
				}
			} else {
				if (entityForAutoAttack instanceof Player)
				entityForAutoAttack.sendMessage(ChatColor.GRAY + "* You have no target to auto attack");
				StateManager.getInstance().getEntityManager().setEntityAutoAttack(entityForAutoAttack, false);
				return;
			}
		} catch (CoreStateInitException e)
		{
			e.printStackTrace();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
