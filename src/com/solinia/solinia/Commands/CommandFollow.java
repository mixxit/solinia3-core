package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Managers.StateManager;

import net.md_5.bungee.api.ChatColor;

public class CommandFollow implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player))
			return false;

		Player player = (Player)sender;
		
		try
		{
			boolean following = StateManager.getInstance().getEntityManager().isFollowing(player.getUniqueId());
			if (following)
			{
				StateManager.getInstance().getEntityManager().setFollowing(player.getUniqueId(), null);
				player.sendMessage("You are no long following someone");
				return true;
			} else {
				LivingEntity targetmob = StateManager.getInstance().getEntityManager()
						.getEntityTarget(player);
				
				if (targetmob == null)
				{
					player.sendMessage("You need to target someone to follow them");
					return true;
				}

				if (targetmob.getUniqueId().equals(player.getUniqueId()))
				{
					player.sendMessage("You can't follow yourself!");
					return true;
				}
				
				if (!(targetmob instanceof Player))
				{
					player.sendMessage("You can only follow players");
					return true;
				}
				
				StateManager.getInstance().getEntityManager().setFollowing(player.getUniqueId(), targetmob.getUniqueId());
				player.sendMessage("You are now following " + targetmob.getName());
				player.sendMessage(ChatColor.RED + "WARNING: FOLLOW MAY BE DANGEROUS NEAR HIGH CLIFFS, LAVA AND OTHER DANGEROUS AREAS, ALWAYS WATCH YOUR CHARACTER" + ChatColor.RESET);
				player.sendMessage("To cancel follow use /follow again");
			}
			
			
		} catch (CoreStateInitException e)
		{
			
		}
		
		return true;
	}

}
