package com.solinia.solinia.Utils;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class BukkitUtils {
	public static void dispatchCommandLater(Plugin plugin, String command) {
		final Plugin pluginToSend = plugin;
		final CommandSender senderToSend = pluginToSend.getServer().getConsoleSender();
		final String commandToSend = command;
		new BukkitRunnable() {

			@Override
			public void run() {
				pluginToSend.getServer().dispatchCommand(senderToSend, commandToSend);
			}

		}.runTaskLater(plugin, 10);
	}

	public static void dispatchCommandLater(Plugin plugin, CommandSender sender, String command) {
		final Plugin pluginToSend = plugin;
		final CommandSender senderToSend = sender;
		final String commandToSend = command;
		new BukkitRunnable() {

			@Override
			public void run() {
				pluginToSend.getServer().dispatchCommand(senderToSend, commandToSend);
			}

		}.runTaskLater(plugin, 10);

	}
}
