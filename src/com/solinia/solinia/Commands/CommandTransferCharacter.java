package com.solinia.solinia.Commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;

public class CommandTransferCharacter implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
		{
			sender.sendMessage("This is a player only command");
			return true;
		}

        Player player = (Player) sender;

		if (args.length < 2)
		{
			player.sendMessage("You must provide the character UUID and the target playername");
			return true;
		}
		
        UUID characterUUID = UUID.fromString(args[0]);
        String targetPlayerName = args[1];
        Player targetPlayer = Bukkit.getPlayer(targetPlayerName);
        
        if (targetPlayer == null)
        {
        	player.sendMessage("The player: " + targetPlayerName + " does not appear to be online");
        	return true;
        }
        
        try
        {
        	ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
			if (solPlayer.getCharacterId().equals(characterUUID))
			{
				player.sendMessage("You cannot transfer a character that you are currently active on, please change character first with the /character command");
				return true;
			}
			
        	ISoliniaPlayer solPlayerToSell = StateManager.getInstance().getConfigurationManager().getCharacterByCharacterUUID(characterUUID);
        	if (!(solPlayerToSell.getUUID().equals(player.getUniqueId())))
        	{
        		player.sendMessage("This is not your character to transfer");
        		return true;
        	}
        	
        	solPlayerToSell.setUUID(targetPlayer.getUniqueId());
        	player.sendMessage("* You have sent character [" + solPlayerToSell.getFullName() + "] to " + targetPlayer.getCustomName());
        	targetPlayer.sendMessage("* You have received a new character [" + solPlayerToSell.getFullName() + "] from " + player.getCustomName());
        } catch (Exception e)
        {
        	
        }
        
        
        return true;
	}
}
