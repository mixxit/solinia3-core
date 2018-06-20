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
import com.solinia.solinia.Managers.ConfigurationManager;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.PlayerAutoAttack;

import net.md_5.bungee.api.ChatColor;

public class PlayerAutoAttackTimer extends BukkitRunnable {
	@Override
	public void run() {
		try
		{
			for (Player player : Bukkit.getServer().getOnlinePlayers()) {
				PlayerAutoAttack autoAttack = StateManager.getInstance().getEntityManager().getPlayerAutoAttack(player);
				
				if (autoAttack.isAutoAttacking() == true)
				{
					if (player.isDead())
					{
						StateManager.getInstance().getEntityManager().setPlayerAutoAttack(player, false);
						continue;
					}
					
					if (player.getInventory().getItemInMainHand() != null)
					{
						if (!ConfigurationManager.WeaponMaterials.contains(player.getInventory().getItemInMainHand().getType().name()))
						{
							StateManager.getInstance().getEntityManager().setPlayerAutoAttack(player, false);
							continue;
						}
					} else {
						StateManager.getInstance().getEntityManager().setPlayerAutoAttack(player, false);
						continue;
					}
					
					if (autoAttack.getTimer() > 0)
					{
						autoAttack.setTimer(autoAttack.getTimer() - 1);
						continue;
					}
					
					LivingEntity target = StateManager.getInstance().getEntityManager().getEntityTarget((LivingEntity)player);
					if (target != null)
					{
						if (target.isDead())
						{
							player.sendMessage(ChatColor.GRAY + "* Your target is dead!");
							StateManager.getInstance().getEntityManager().setPlayerAutoAttack(player, false);
							continue;
						}
						
						ISoliniaLivingEntity solLivingEntity = SoliniaLivingEntityAdapter.Adapt(target);
						ISoliniaLivingEntity solLivingEntityPlayer = SoliniaLivingEntityAdapter.Adapt(player);
						ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
						
						if (solLivingEntity != null && solPlayer != null && solLivingEntityPlayer != null)
						{
							// reset timer
							autoAttack.setTimerFromAttackSpeed(solLivingEntityPlayer.getAttackSpeed());
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
