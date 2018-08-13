package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;

public class CommandSetMain implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
		{
			sender.sendMessage("This is a player only command");
			return true;
		}
		
		if (args.length < 1)
		{
			sender.sendMessage("This command will reset your old characters votes and set your current character as main. To confirm use /setmain confirm");
			return true;
		}
		
		if (!args[0].toUpperCase().equals("CONFIRM"))
		{
			sender.sendMessage("This command will reset your old characters votes and set your current character as main. To confirm use /setmain confirm");
			return true;
		}
		
		try
		{
			ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player)sender);
			if (solPlayer.isMain())
			{
				sender.sendMessage("This character is already your main");
				return true;
			}
			
			if (StateManager.getInstance().getEntityManager().getPlayerSetMain().get(((Player)sender).getUniqueId()) != null)
			{
				sender.sendMessage("You can only set your main once per session");
				return true;
			}
			
			StateManager.getInstance().getEntityManager().getPlayerSetMain().put(((Player)sender).getUniqueId(), true);
			solPlayer.setMainAndCleanup();
			sender.sendMessage("This character is now your main");
			return true;
			
		} catch (CoreStateInitException e)
		{
			
		}
		
		return false;
	}
	
}
