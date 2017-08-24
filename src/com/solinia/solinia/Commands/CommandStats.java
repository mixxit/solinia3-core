package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;

public class CommandStats implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			try {
	            Player player = (Player) sender;
	            ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(player);

				player.sendMessage("STR: " + solplayer.getStrength() + " STA: " + solplayer.getStamina() + " AGI: " + solplayer.getAgility() + " DEX: " + solplayer.getDexterity() + " INT: " + solplayer.getIntelligence() + " WIS: " + solplayer.getWisdom() + " CHA: " + solplayer.getCharisma());
	            player.sendMessage("You have a maximum HP of: " + player.getMaxHealth());
	            //player.sendMessage("You have a maximum MP of: " + solplayer.getMaxMP());
			} catch (CoreStateInitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
		}
		return true;
	}
}
