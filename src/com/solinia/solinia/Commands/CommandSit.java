package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;

public class CommandSit implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (sender instanceof Player) {
	            Player player = (Player) sender;
	            try
	            {
	            
	            ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(player);
	            
	            if (solplayer == null)
	            	return false;	
	            
	            player.sendMessage("The sit command is not currently implemented");
	            
            } catch (CoreStateInitException e)
            {
            	e.printStackTrace();
            }
        }

        return true;
	}
}