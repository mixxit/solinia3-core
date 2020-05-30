package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Utils.PlayerUtils;

public class CommandOpenCharacterCreation implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!(sender instanceof Player))
			return false;
		
	    PlayerUtils.sendCharCreation((Player)sender);
		return true;
	}

}
