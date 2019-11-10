package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Utils.Utils;

public class CommandRightClickEntity implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		Player player = (Player)sender;
		
		if (tryRightClickTarget(player))
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
	
	private boolean tryRightClickTarget(Player player) {
		try
		{
			LivingEntity targetmob = Utils.getTargettedLivingEntity(player, 50);
			ISoliniaLivingEntity solLivingEntityPlayer = SoliniaLivingEntityAdapter.Adapt((LivingEntity)player);
			if (solLivingEntityPlayer != null)
			{
				if (targetmob != null)
				{
					solLivingEntityPlayer.setEntityTarget(targetmob);
					return true;
				}
			}
		} catch (CoreStateInitException e)
		{
			
		}
		return false;
	}
}
