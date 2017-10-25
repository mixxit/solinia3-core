package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Factories.SoliniaLootFactory;
import com.solinia.solinia.Managers.StateManager;

public class CommandAddLootTableLootDrop implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
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
			sender.sendMessage("Insufficient arguments: loottableid lootdropid");
			return false;
		}
		
		int loottableid = Integer.parseInt(args[0]);
		int lootdropid = Integer.parseInt(args[1]);
		
		try
		{
			if (StateManager.getInstance().getConfigurationManager().getLootTable(loottableid) == null)
			{
				sender.sendMessage("LootTable does not exist");
				return true;
			}
			
			if (StateManager.getInstance().getConfigurationManager().getLootDrop(lootdropid) == null)
			{
				sender.sendMessage("LootDrop does not exist");
				return true;
			}
			
			SoliniaLootFactory.CreateLootTableDrop(loottableid, lootdropid);
			sender.sendMessage("LootTable updated");
		} catch (CoreStateInitException e)
		{
			sender.sendMessage(e.getMessage());
		}
		
		return true;
	}
}
