package com.solinia.solinia.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Models.SolAnimationType;
import com.solinia.solinia.Utils.EntityUtils;

public class CommandStand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			try {
				ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player)sender);
				solPlayer.getBukkitPlayer().teleport(solPlayer.getBukkitPlayer().getLocation().add(0, 0.5, 0));

				for (Entity listening : solPlayer.getBukkitPlayer().getNearbyEntities(20, 20, 20)) {
					if (listening instanceof Player)
					{
						EntityUtils.sendAnimationPacket(solPlayer.getBukkitPlayer(), (Player)listening,SolAnimationType.Stand);
					}
				}
				EntityUtils.sendAnimationPacket(solPlayer.getBukkitPlayer(), (Player)solPlayer.getBukkitPlayer(), SolAnimationType.Stand);
			} catch (CoreStateInitException e) {
				// TODO Auto-generated catch block
				sender.sendMessage(e.getMessage());
			}
        }
        return true;
	}
}
