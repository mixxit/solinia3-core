package com.solinia.solinia.Commands;

import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;

public class CommandDeleteCharacter implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
		{
			sender.sendMessage("This is a player only command");
			return true;
		}

        Player player = (Player) sender;

		if (args.length < 1)
		{
			player.sendMessage("You must provide the character ID");
			return true;
		}
		
        int characterId = Integer.parseInt(args[0]);
        
        try
        {
        	ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
			if (solPlayer.getId() == characterId)
			{
				player.sendMessage("You cannot delete a character that you are currently active on, please change character first with the /character command");
				return true;
			}
			
        	ISoliniaPlayer solPlayerToDelete = StateManager.getInstance().getConfigurationManager().getCharacterById(characterId);
        	if (!(solPlayerToDelete.getOwnerUUID().equals(player.getUniqueId())))
        	{
        		player.sendMessage("This is not your character to delete");
        		return true;
        	}
        	
        	solPlayerToDelete.setDeleted(true);
        	player.sendMessage("* You have deleted character [" + solPlayerToDelete.getFullName() + "]");
        } catch (Exception e)
        {
        	
        }
        
        
        return true;
	}
}
