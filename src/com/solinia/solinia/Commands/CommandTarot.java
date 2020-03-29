package com.solinia.solinia.Commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;

import net.md_5.bungee.api.ChatColor;

public class CommandTarot implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (sender instanceof Player) {
            Player player = (Player) sender;
            
            try
            {
	            ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(player);
	            
	            if (solplayer == null)
	            {
	            	player.sendMessage("Failed to emote, player does not exist");
	            	return false;	
	            }
	            
	            List<String> tarot = new ArrayList<String>();
	            tarot.add("The Fool");
	            tarot.add("The Magician");
	            tarot.add("The High Priestess");
	            tarot.add("The Empress");
	            tarot.add("The Emperor");
	            tarot.add("The Hierophant");
	            tarot.add("The Lovers");
	            tarot.add("The Chariot");
	            tarot.add("Strength");
	            tarot.add("The Hermit");
	            tarot.add("Wheel of Fortune");
	            tarot.add("Justice");
	            tarot.add("The Hanged Man");
	            tarot.add("Death");
	            tarot.add("Temperance");
	            tarot.add("The Devil");
	            tarot.add("The Tower");
	            tarot.add("The Star");
	            tarot.add("The Moon");
	            tarot.add("The Sun");
	            tarot.add("Judgement");
	            tarot.add("The World");
	            Collections.shuffle(tarot);
	            Collections.shuffle(tarot);
	            Collections.shuffle(tarot);
	            
	            String past = tarot.get(0);
	            String present = tarot.get(1);
	            String future = tarot.get(2);
	            
	            String message = ChatColor.AQUA + " * " + solplayer.getFullName() + " shuffles a deck of tarot cards and slowly draws. Drawing the Card of the Past: " + past + ", Card of the Present: " + present + " and finally, Card of the " + future +"." + ChatColor.RESET;
	            solplayer.emote(message, false, false);
            } catch (CoreStateInitException e)
            {
            	player.sendMessage(e.getMessage());
            }
        }

        return true;
	}
}
