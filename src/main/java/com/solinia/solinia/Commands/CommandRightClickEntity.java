package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Managers.StateManager;

public class CommandRightClickEntity implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		Player player = (Player)sender;
		
		if (tryRightClickTarget(player, Integer.parseInt(args[0])))
		{
			// cancel feigened if targetting
			try
			{
				boolean feigned = StateManager.getInstance().getEntityManager().isFeignedDeath(player.getUniqueId());
				if (feigned == true)
				{
					StateManager.getInstance().getEntityManager().setFeignedDeath(player.getUniqueId(), false);
				}
			} catch (CoreStateInitException e)
			{
						
			}
			
			return true;
		}
		
		return true;
	}
	
	private boolean tryRightClickTarget(Player player, int entityId) {
		try
		{
			LivingEntity targetmob = null;
			
			if (entityId > 0)
			for(Entity entity : player.getNearbyEntities(200D, 200D, 200D))
			{
				if (!(entity instanceof LivingEntity))
					continue;
				
				if (entity.getEntityId() != entityId)
					continue;
				
				targetmob = (LivingEntity)entity;
				break;
			}
			
			if (targetmob == null)
				return true;
			
			ISoliniaLivingEntity solLivingEntityPlayer = SoliniaLivingEntityAdapter.Adapt((LivingEntity)player);
			if (solLivingEntityPlayer != null)
			{
				solLivingEntityPlayer.setEntityTarget(targetmob);
				return true;
			}
		} catch (CoreStateInitException e)
		{
			
		}
		return true;
	}
}
