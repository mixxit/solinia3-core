package com.solinia.solinia.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Models.SolAnimationType;
import com.solinia.solinia.Utils.EntityUtils;

public class CommandSit implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			try {
				ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player)sender);
				
				if (!solPlayer.getBukkitPlayer().isOnGround())
				{
					solPlayer.getBukkitPlayer().sendMessage("You need to be on the ground to use this command");
					return true;
				}
				
				EntityUtils.sit(solPlayer.getBukkitPlayer());
				
			} catch (CoreStateInitException e) {
				// TODO Auto-generated catch block
				sender.sendMessage(e.getMessage());
			}
        }
        return true;
	}
}
