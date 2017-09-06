package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;

public class CommandSetChannel implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 0)
            {
            	player.sendMessage("Insufficient arguments [OOC,LOCAL]");
            	return false;
            }
            
            if (args[0].toUpperCase().equals("OOC") && args[0].toUpperCase().equals("LOCAL"))
            {
            	player.sendMessage("Insufficient arguments [OOC,LOCAL]");
            	return false;
            }
            
            try
            {
	            ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(player);
	        	solplayer.setCurrentChannel(args[0].toUpperCase());
	        	player.sendMessage("* Channel set to " + args[0].toUpperCase());
	        	return true;
            } catch (CoreStateInitException e)
            {
            	sender.sendMessage(e.getMessage());
            }
        }

        return false;
	}
	

}
