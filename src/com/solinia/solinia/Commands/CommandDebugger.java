package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidAASettingException;
import com.solinia.solinia.Interfaces.ISoliniaAAAbility;
import com.solinia.solinia.Managers.StateManager;

public class CommandDebugger implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;

		if (!sender.isOp() && !sender.hasPermission("solinia.debugger"))
		{
			sender.sendMessage("You do not have permission to access this command");
			return false;
		}
		
		if (args.length < 3)
		{
			sender.sendMessage("Incorrect arguments");
			return false;
		}
		
		String classToDebug = args[0];
		String methodToDebug = args[1];
		String focusId = args[2];
		
		if (!(sender instanceof Player))
		{
			sender.sendMessage("Only a sender of type Player can use this command");
		}
		try
		{
			Player player = (Player)sender;
			
			StateManager.getInstance().getPlayerManager().toggleDebugger(player.getUniqueId(),classToDebug.toUpperCase(),methodToDebug.toUpperCase(),focusId.toUpperCase());
		} catch (CoreStateInitException e)
		{
			
		}
		
		return true;
	}
}
