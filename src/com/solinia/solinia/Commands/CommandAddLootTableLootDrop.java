package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
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
			
			if (!player.isOp() && !player.hasPermission("solinia.addloottablelootdrop"))
			{
				player.sendMessage("You do not have permission to access this command");
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
			
			if (StateManager.getInstance().getConfigurationManager().getLootTable(loottableid).isOperatorCreated() && !sender.isOp())
			{
				sender.sendMessage("This loottable was op created and you are not an op. Only ops can edit op loottable items");
				return false;
			}
			
			if (StateManager.getInstance().getConfigurationManager().getLootDrop(lootdropid) == null)
			{
				sender.sendMessage("LootDrop does not exist");
				return true;
			}
			
			// Editting the loot tbale so this shouldnt be necessary
			/*
			if (StateManager.getInstance().getConfigurationManager().getLootDrop(lootdropid).isOperatorCreated() && !sender.isOp())
			{
				sender.sendMessage("This lootdrop was op created and you are not an op. Only ops can edit op lootdrop items");
				return false;
			}
			*/
			
			SoliniaLootFactory.CreateLootTableDrop(loottableid, lootdropid, sender.isOp());
			sender.sendMessage("LootTable updated");
		} catch (CoreStateInitException e)
		{
			sender.sendMessage(e.getMessage());
		}
		
		return true;
	}
}
