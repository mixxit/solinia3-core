package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;

public class CommandAssist implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
		{
			sender.sendMessage("This is a player only command");
			return true;
		}
		
		Player player = (Player)sender;
		
		try
		{
			ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
			LivingEntity targetmob = solPlayer.getEntityTarget();
			if (targetmob == null)
			{
				player.sendMessage("You need to target an entity to assist it");
				return true;
			}
			ISoliniaLivingEntity solLivingEntity = SoliniaLivingEntityAdapter.Adapt(targetmob);
			if (solLivingEntity.getEntityTarget() != null)
			{
				solPlayer.setEntityTarget(solLivingEntity.getEntityTarget());
				player.sendMessage("Targetted: " + solLivingEntity.getEntityTarget().getCustomName());
			}
		} catch (CoreStateInitException e)
		{
		} 

		return true;
	}

}