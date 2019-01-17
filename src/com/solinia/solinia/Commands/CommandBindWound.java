package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;

public class CommandBindWound implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
			return false;

		Player player = (Player)sender;
		
		try
		{
			ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
			if (!solPlayer.hasSufficientBandageReagents(1))
			{
				player.sendMessage("You do not have enough bandages in your /reagent pouch");
				return true;
			}
			
			LivingEntity targetmob = StateManager.getInstance().getEntityManager()
					.getEntityTarget(player);
			
			if (targetmob == null)
			{
				player.sendMessage("Selecting yourself");
				StateManager.getInstance().getEntityManager().setEntityTarget(player,player);
				targetmob = player;
			}
			
			if (targetmob instanceof Creature)
			{
				if (((Creature)targetmob).getTarget() != null)
				if (((Creature)targetmob).getTarget().getUniqueId().equals(player.getUniqueId()))
				{
					player.sendMessage("You cannot bind wound a mob that is currently trying to attack you! Perhaps you meant to target yourself first? [Shift-F]");
					return true;
				}
			}
			
			ISoliniaLivingEntity solLivingEntity = SoliniaLivingEntityAdapter.Adapt(targetmob);
			if (solLivingEntity == null)
			{
				player.sendMessage("You must select a target to bind wound");
				return true;
			}
			
			if (solPlayer.bindWound(solLivingEntity) == true)
			{
				// Remove one of the reagents
				int itemid = solPlayer.getBandageReagents().get(0);
				solPlayer.reduceReagents(itemid, 1);
			}
			
		} catch (CoreStateInitException e)
		{
			
		}

		return true;
	}
}
