package com.solinia.solinia.Commands;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;

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
		
        try
        {
	        for (World world : Bukkit.getWorlds()) {
				for (Player player : world.getPlayers()) {
					ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
					if (solPlayer == null)
						continue;
					
					if (!solPlayer.isShowDiscord())
						continue;
					
					player.sendMessage(ChatColor.GRAY + "[Discord]~" + message + ChatColor.RESET);
				}
			}
        } catch (CoreStateInitException e)
        {
        	
        }
		
		return true;
	}
}
