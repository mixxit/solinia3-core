package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;

public class CommandHideSongs implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
			return false;
			
		try
		{
			ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player)sender);
			if (solPlayer.isSongsEnabled() == false)
			{
				solPlayer.setSongsEnabled(true);
				sender.sendMessage("Songs enabled");
			} else {
				solPlayer.setSongsEnabled(false);
				sender.sendMessage("Songs disabled");
			}
		} catch (CoreStateInitException e)
		{
			return false;
		}
        return true;
	}
}
