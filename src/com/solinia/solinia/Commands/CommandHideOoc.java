package com.solinia.solinia.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;

public class CommandHideOoc implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			
			try
			{
				ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player)sender);
				if (solPlayer.isOocEnabled() == false)
				{
					solPlayer.setOocEnabled(true);
					sender.sendMessage("Ooc enabled");
				} else {
					solPlayer.setOocEnabled(false);
					sender.sendMessage("Ooc disabled");
				}
			} catch (CoreStateInitException e)
			{
				return false;
			}
            return true;
		}
		return false;
	}
}
