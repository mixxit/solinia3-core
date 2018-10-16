package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;

public class CommandStopSinging implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		try {
			Player player = (Player) sender;
			ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(player);
			solplayer.StopSinging();
			
		} catch (CoreStateInitException e)
		{
			
		}
		return true;
	}
}
