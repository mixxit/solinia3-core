package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Models.HINT;

public class CommandTogglePassive implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (sender instanceof Player) {
	            Player player = (Player) sender;
	            try
	            {
		            ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(player);
		            if (solplayer == null)
		            {
		            	player.sendMessage("Failed to toggle, player does not exist");
		            	return false;	
		            }
		            
		            solplayer.setPassiveEnabled(!solplayer.isPassiveEnabled());
		            
		            player.sendMessage("* Passive Abilities Toggled to : " + solplayer.isPassiveEnabled());
		            player.sendMessage("If you have the buff effect active, remember to right click it (or toggle off with /effects)");
		            
            } catch (CoreStateInitException e)
            {
            	e.printStackTrace();
            }
        } else {
        	sender.sendMessage("For players only");
        }

        return true;
	}

	private void sendHintStrings(Player player) {
		String hintStrings = "";
		for(HINT hintval : HINT.values())
        {
        	hintStrings += hintval.name() + ", "; 
        }
		
		player.sendMessage("Available HINT toggles: " + hintStrings);
	}
}