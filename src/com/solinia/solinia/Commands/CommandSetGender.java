package com.solinia.solinia.Commands;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;

public class CommandSetGender implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
            Player player = (Player) sender;
            
            if (args.length == 0)
            {
            	player.sendMessage("Insufficient arguments, must provide MALE or FEMALE");
            	return false;
            }
            
            String gender = args[0].toUpperCase();
            
            if (!gender.equals("MALE") && !gender.equals("FEMALE"))
            {
            	player.sendMessage("Gender command failed, solinia only has male/female");
            	return false;
            }
            
            try {
				SoliniaPlayerAdapter.Adapt(player).setGender(gender);
				player.sendMessage("* Gender set to: "+gender);
			} catch (CoreStateInitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				player.sendMessage(e.getMessage());
			}
        	
        }

        return true;
	}
}
