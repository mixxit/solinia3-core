package com.solinia.solinia.Commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Utils.Utils;

public class CommandSpecialise implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!(sender instanceof Player)) {
			return false;
		}
		
		Player player = (Player) sender;
        if (args.length < 1)
        {
        	player.sendMessage("Insufficient arguments (<skillname>)");
        	return false;
        }
        
        String skillName = args[0].toUpperCase();
        
        List<String> validSpecialisationSkills = Utils.getSpecialisationSkills();
            
        if (!validSpecialisationSkills.contains(skillName))
        {
        	player.sendMessage("You can only specialise in spell school skills");
        	return false;
        }
        
        try
        {
            ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(player);
            if (solplayer == null)
            {
            	player.sendMessage("Failed to emote, player does not exist");
            	return false;	
            }
            
            if (solplayer.getClassObj() == null)
            {
            	player.sendMessage("Your class cannot specialise");
            	return false;
            }
            
            if (solplayer.getClassObj().getSpecialiselevel() < 1)
            {
            	player.sendMessage("Your class cannot specialise");
            	return false;
            }

            if (solplayer.getClassObj().getSpecialiselevel() > solplayer.getLevel())
            {
            	player.sendMessage("You cannot specialise until level: " + solplayer.getLevel());
            	return false;
            }
            
            if (!solplayer.getSpecialisation().equals(""))
            {
            	player.sendMessage("You have already chosen your specialisation: " + solplayer.getSpecialisation());
            	return false;	
            }
            
            solplayer.setSpecialisation(skillName);
        	player.sendMessage("Specialisation Set to: " + solplayer.getSpecialisation());
            return true;
        } catch (CoreStateInitException e)
        {
        	player.sendMessage(e.getMessage());
        }

        return true;
	}
}
