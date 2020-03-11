package com.solinia.solinia.Commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Factories.SoliniaPlayerFactory;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaPlayer;

public class CommandCopyCharacter implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
		{
			sender.sendMessage("This is a player only command");
			return true;
		}

        Player player = (Player) sender;
        
        if (!player.isOp() && !player.hasPermission("solinia.copycharacter"))
		{
			player.sendMessage("You do not have permission to access this command");
			return false;
		}

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
        	SoliniaPlayer solPlayerToCopy = (SoliniaPlayer)StateManager.getInstance().getConfigurationManager().getArchivedCharacterByCharacterUUID(characterUUID);
        	ISoliniaPlayer solPlayerNew = SoliniaPlayerFactory.CreatePlayerCopy(solPlayerToCopy,targetPlayer.getUniqueId());
        	
        	player.sendMessage("* You have copied character [" + solPlayerToCopy.getFullName() + "] to " + targetPlayer.getCustomName());
        	targetPlayer.sendMessage("* You have received a new character [" + solPlayerNew.getFullName() + "] from " + player.getCustomName());
        } catch (Exception e)
        {
        	
        }
        
        
        return true;
	}
}
