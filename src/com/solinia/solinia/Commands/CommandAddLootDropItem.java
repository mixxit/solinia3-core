package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Factories.SoliniaLootFactory;
import com.solinia.solinia.Interfaces.ISoliniaLootDropEntry;
import com.solinia.solinia.Managers.StateManager;

public class CommandAddLootDropItem implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;
		
		if (sender instanceof Player)
		{

			Player player = (Player) sender;
			
			if (!player.isOp() && !player.hasPermission("solinia.addlootdropitem"))
			{
				player.sendMessage("You do not have permission to access this command");
				return false;
			}
		}
		
		if (args.length < 5)
		{
			sender.sendMessage("Insufficient arguments: lootdropid itemid count always chance");
			return false;
		}
		
		int lootdropid = Integer.parseInt(args[0]);
		int itemid = Integer.parseInt(args[1]);
		int count = Integer.parseInt(args[2]);

		boolean always = Boolean.parseBoolean(args[3]);
		int chance = Integer.parseInt(args[4]);

		
		try
		{
			if (StateManager.getInstance().getConfigurationManager().getLootDrop(lootdropid) == null)
			{
				sender.sendMessage("LootDrop does not exist");
				return true;
			}
			
			if (StateManager.getInstance().getConfigurationManager().getLootDrop(lootdropid).isOperatorCreated() && !sender.isOp())
			{
				sender.sendMessage("This lootdrop was op created and you are not an op. Only ops can edit op lootdrop items");
				return false;
			}
			
			if (StateManager.getInstance().getConfigurationManager().getItem(itemid) == null)
			{
				sender.sendMessage("Item does not exist");
				return true;
			}
			
			
			
			for(ISoliniaLootDropEntry lde : StateManager.getInstance().getConfigurationManager().getLootDrop(lootdropid).getEntries())
			{
				if (lde.getItemid() == itemid)
				{
					sender.sendMessage("Item already exists in lootdrop definition");
					return true;
				}
			}
			
			SoliniaLootFactory.CreateLootDropItem(lootdropid, itemid, count, always, chance, sender.isOp());
			sender.sendMessage("Item added to loot drop");
		} catch (CoreStateInitException e)
		{
			sender.sendMessage(e.getMessage());
		}
		return true;
	}
}
