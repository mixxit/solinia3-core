package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Factories.SoliniaFactionFactory;
import com.solinia.solinia.Managers.StateManager;

public class CommandCreateFaction implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof ConsoleCommandSender))
			return false;
		
		if (sender instanceof Player)
		{

			Player player = (Player) sender;
			
			if (!player.isOp())
			{
				player.sendMessage("This is an operator only command");
				return false;
			}
		}
		
		if (args.length < 2)
		{
			sender.sendMessage("Insufficient arguments: basestandingvalue name");
			return false;
		}
		
		// args
		// defaultnpcid
		// spawngroupname
		
		int base = Integer.parseInt(args[0]);
		
		String factionname = "";
		int count = 0;
		for(String entry : args)
		{
			if (count == 0)
			{
				count++;
				continue;
			}
			
			factionname  += entry;
			count++;
		}
		
		if (factionname.equals(""))
		{
			sender.sendMessage("Blank name not allowed when creating faction");
			return false;
		}
		
		factionname.replace(" ", "_");
		
		SoliniaFactionFactory.CreateFaction(factionname, base);
		return true;
	}
}
