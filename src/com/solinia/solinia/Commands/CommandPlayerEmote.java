package com.solinia.solinia.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;

public class CommandPlayerEmote implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
		{
			sender.sendMessage("This is a Player/Console only command");
			return false;
		}
		
		if (sender instanceof Player)
		{
			Player player = (Player)sender;
			if (!player.isOp() && !player.hasPermission("solinia.playeremote"))
			{
				player.sendMessage("You do not have permission to access this command");
				return true;
			}
		}
		
		if (args.length < 2)
			return false;
		
		if (Bukkit.getPlayer(args[0]) == null)
		{
			sender.sendMessage("Cannot find player");
			return true;
		}
		
		String emote = "";
		int current = 0;
		for (String entry : args) {
			current++;
			if (current < 2)
				continue;
			
			emote = emote + entry + " ";
		}

		emote = emote.trim();
		
		try {
			ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(Bukkit.getPlayer(args[0]));
			solplayer.emote(emote, false);
			sender.sendMessage("Forced player emote");
				
			return true;
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			sender.sendMessage(e.getMessage());
			return true;
		}
	}
}
