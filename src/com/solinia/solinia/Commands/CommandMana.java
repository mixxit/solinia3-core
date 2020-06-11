package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;

import net.md_5.bungee.api.ChatColor;

public class CommandMana implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			try {
				if (args.length > 0 && sender.isOp())
				{
					if (args[0].toUpperCase().equals("RESET"))
						SoliniaPlayerAdapter.Adapt((Player)sender).setMana(1000000000);
				}
				
				String message = ChatColor.GRAY + "* Your mana is currently: " + SoliniaPlayerAdapter.Adapt((Player)sender).getMana();
	            sender.sendMessage(message);
			} catch (CoreStateInitException e) {
				// TODO Auto-generated catch block
				sender.sendMessage(e.getMessage());
			}
        }
        return true;
	}
}
