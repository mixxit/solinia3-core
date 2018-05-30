package com.solinia.solinia.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.inventivetalent.glow.GlowAPI;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;

public class CommandShowDiscord implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (sender instanceof Player) {
	            Player player = (Player) sender;
	            try
	            {
	            
	            ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(player);
	            
	            if (solplayer == null)
	            {
	            	player.sendMessage("Failed to toggle Discord, player does not exist");
	            	return false;	
	            }
	            
	            solplayer.setShowDiscord(!solplayer.isShowDiscord());
	            
	            player.sendMessage("Toggled your showing of discord to : " + solplayer.isShowDiscord());
	            
            } catch (CoreStateInitException e)
            {
            	e.printStackTrace();
            }
        }

        return true;
	}
}
