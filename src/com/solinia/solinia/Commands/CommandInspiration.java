package com.solinia.solinia.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;

public class CommandInspiration implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("This is a Player only command");
			return false;
		}
		
		try {
			ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player)sender);
			
			sender.sendMessage("Inspiration");
						
			sender.sendMessage("Points Available: " + solPlayer.getInspiration());
			
			return true;
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			sender.sendMessage(e.getMessage());
			return true;
		}
	}
}
