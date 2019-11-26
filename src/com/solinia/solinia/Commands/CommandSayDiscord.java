package com.solinia.solinia.Commands;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class CommandSayDiscord implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof ConsoleCommandSender)) {
			return false;
		}
		
		StringBuilder builder = new StringBuilder();
        for (String value : args) {
            builder.append(value + " ");
        }
        String message = builder.toString();
        message = message.substring(0, message.length());
		
        for (World world : Bukkit.getWorlds()) {
			for (Player player : world.getPlayers()) {
				player.sendMessage(ChatColor.GRAY + "[Discord]~" + message + ChatColor.RESET);
			}
		}
		
		return true;
	}
}
