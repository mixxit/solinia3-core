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
		
		if (!sender.isOp() && !sender.hasPermission("solinia.playeremote"))
		{
			sender.sendMessage("You do not have permission to access this command");
			return true;
		}
		
		if (args.length < 2)
			return false;
		
		if (!args[0].equals("*"))
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
			if (!args[0].equals("*"))
				SendEmote(Bukkit.getPlayer(args[0]), emote);
			else
				SendEmoteAll(emote);

			sender.sendMessage("Forced player emote");
				
			return true;
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			sender.sendMessage(e.getMessage());
			return true;
		}
	}

	private void SendEmoteAll(String emote) throws CoreStateInitException {
		for(Player player : Bukkit.getOnlinePlayers())
		{
			SendEmote(player, emote);
		}
		
	}

	private void SendEmote(Player player, String emote) throws CoreStateInitException {
		ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(player);
		solplayer.emote(emote, false, false);
	}
}
