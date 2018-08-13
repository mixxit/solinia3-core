package com.solinia.solinia.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;

public class CommandBite implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("This is a Player only command");
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
			Player biten = Bukkit.getPlayer(args[0]);
			
			if (((Player)sender).getLocation().distance(biten.getLocation()) > 2)
			{
				sender.sendMessage("You are too far from that person");
				return true;
			}
			
			ISoliniaPlayer sourcePlayer = SoliniaPlayerAdapter.Adapt((Player)sender);
			ISoliniaPlayer bitenPlayer = SoliniaPlayerAdapter.Adapt(biten);
			
			sourcePlayer.emote("* " + sourcePlayer.getFullNameWithTitle() + " sinks their teeth into " + bitenPlayer.getFullNameWithTitle());
			if (sourcePlayer.isVampire())
			{
				bitenPlayer.setVampire(true);
			}
			
			return true;
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			sender.sendMessage(e.getMessage());
			return true;
		}
	}

}
