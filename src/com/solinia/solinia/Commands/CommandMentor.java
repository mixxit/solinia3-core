package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;

public class CommandMentor implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!(sender instanceof Player)) {
			sender.sendMessage("This is a player only command");
			return true;
		}
		
		try
		{
			ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player)sender);
			
			if (solPlayer.getEntityTarget() == null) {
				sender.sendMessage("* You must select a target []");
				return true;
			}
			
			if (!(solPlayer.getEntityTarget() instanceof Player))
			{
				sender.sendMessage("* You may only mentor a player");
				return true;
			}
			
			ISoliniaPlayer solTarget = SoliniaPlayerAdapter.Adapt((Player)solPlayer.getEntityTarget());
			if (solTarget.isMentoring())
			{
				sender.sendMessage("You cannot mentor someone that is mentoring");
				return true;
			}
			
			if (solTarget.getActualLevel() >= solPlayer.getActualLevel())
			{
				sender.sendMessage("You cannot mentor someone higher or equal to you");
				return true;
			}
			
			if (!solTarget.isInGroup(solPlayer.getBukkitPlayer()))
			{
				sender.sendMessage("You must be in a group to mentor someone");
				return true;
			}
			
			solPlayer.setMentor(solTarget);
			sender.sendMessage("You are now mentoring " + solTarget.getFullName() + " ["+solTarget.getActualLevel()+"]");
		} catch (CoreStateInitException e)
		{
		}
		
		return true;
	}
}