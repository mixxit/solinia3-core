package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Providers.DiscordAdminChannelCommandSender;
import com.solinia.solinia.Providers.DiscordDefaultChannelCommandSender;
import com.solinia.solinia.Utils.Utils;

public class CommandSolinia implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// TODO Auto-generated method stub
		sender.sendMessage("Solinia Stats:");
		try {
			sender.sendMessage("Cached Players: " + StateManager.getInstance().getPlayerManager().getCachedPlayersCount());
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if ((sender instanceof ConsoleCommandSender || sender instanceof DiscordDefaultChannelCommandSender || sender instanceof DiscordAdminChannelCommandSender))
		{
			Utils.Patcher();
		}
		return true;
	}
}
