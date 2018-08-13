package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandPerks implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// TODO Auto-generated method stub
		if (sender instanceof Player) {
			Player player = (Player)sender;
			player.sendMessage("World Wide Perks give every player online a benefit and help pay for the server! See our page for more details:");
			player.sendMessage("http://www.fallofanempire.com/index.php/server-wide-perks/");
		}
		return true;
	}
}
