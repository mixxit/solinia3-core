package com.solinia.solinia.Timers;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;

import net.md_5.bungee.api.ChatColor;

public class PlayerAutoAttackTimer extends BukkitRunnable {
	@Override
	public void run() {
		try
		{
			for (Player player : Bukkit.getServer().getOnlinePlayers()) {
				boolean autoAttacking = StateManager.getInstance().getEntityManager().getPlayerAutoAttack(player);
				LivingEntity target = StateManager.getInstance().getEntityManager().getEntityTarget((LivingEntity)player);
				if (autoAttacking == true)
				{
					if (player.isDead())
					{
						StateManager.getInstance().getEntityManager().setPlayerAutoAttack(player, false);
						continue;
					}
					
					if (target != null)
					{
						if (target.isDead())
						{
							player.sendMessage(ChatColor.GRAY + "* Your target is dead!");
							StateManager.getInstance().getEntityManager().setPlayerAutoAttack(player, false);
							continue;
						}
						
						ISoliniaLivingEntity solLivingEntity = SoliniaLivingEntityAdapter.Adapt(target);
						ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
						if (solLivingEntity != null && solPlayer != null)
						{
							solPlayer.autoAttackEnemy(solLivingEntity);
						} else {
							player.sendMessage(ChatColor.GRAY + "* Could not find target to attack!");
							StateManager.getInstance().getEntityManager().setPlayerAutoAttack(player, false);
							continue;
						}
					} else {
						player.sendMessage(ChatColor.GRAY + "* You have no target to auto attack");
						StateManager.getInstance().getEntityManager().setPlayerAutoAttack(player, false);
						continue;
					}
				}
			}
		} catch (CoreStateInitException e)
		{
			e.printStackTrace();
		}
	}
}
