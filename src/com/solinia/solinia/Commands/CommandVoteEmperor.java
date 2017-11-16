package com.solinia.solinia.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;

public class CommandVoteEmperor implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
		{
			return false;
		}
		
		if (args.length < 1)
			return false;
		
		if (Bukkit.getPlayer(args[0]) == null)
		{
			sender.sendMessage("Cannot find player");
			return true;
		}
		
		try {
			Player fealtyTo = Bukkit.getPlayer(args[0]);
			ISoliniaPlayer sourcePlayer = SoliniaPlayerAdapter.Adapt((Player)sender);
			
			ISoliniaPlayer fealtyPlayer = SoliniaPlayerAdapter.Adapt(fealtyTo);

			if (!sourcePlayer.isMain())
			{
				sender.sendMessage("You must be your main character to vote");
				return true;
			}
			
			if (!fealtyPlayer.isMain())
			{
				sender.sendMessage("You can only vote for a main character");
				return true;
			}
			
			if (sourcePlayer.getRaceId() < 1 || fealtyPlayer.getRaceId() < 1)
			{
				sender.sendMessage("You and your target must both have a race set");
				return true;
			}
			if (!sourcePlayer.getRace().getAlignment().equals(fealtyPlayer.getRace().getAlignment()))
			{
				sender.sendMessage("You can only vote for an emperor of the same alignment");
				return true;
			}
			
			if (!sourcePlayer.isRacialKing())
			{
				sender.sendMessage("Only a King may vote for an emperor");
				return true;
			}
			
			if (fealtyPlayer.isRacialKing())
			{
				sender.sendMessage("A King may not be an Emperor");
			}
			
			sourcePlayer.setVoteEmperor(fealtyTo.getUniqueId());
			return true;
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			sender.sendMessage(e.getMessage());
			return true;
		}
	}
}