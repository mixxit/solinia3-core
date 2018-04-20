package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Factories.SoliniaLootFactory;
import com.solinia.solinia.Managers.StateManager;

public class CommandConvertMerchantToLootDrop implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
		{
			sender.sendMessage("This is a Player/Console only command");
			return false;
		}
		
		if (sender instanceof Player)
		{
			Player player = (Player)sender;
			if (!player.isOp() && !player.hasPermission("solinia.convertmerchanttolootdrop"))
			{
				player.sendMessage("You do not have permission to access this command");
				return false;
			}
		}
		
		if (args.length < 5)
		{
			sender.sendMessage("args: merchantid newlootdropname count always chance");
			return true;
		}
		
		int merchant = Integer.parseInt(args[0]);
		String name = args[1].toUpperCase();
		int count = Integer.parseInt(args[2]);
		boolean always = Boolean.parseBoolean(args[3]);
		int chance = Integer.parseInt(args[4]);
		
		if (name.length() > 25)
		{
			sender.sendMessage("Lootdrop name cannot be longer than 25 characters");
			return true;
		}
		
		try
		{
			if (StateManager.getInstance().getConfigurationManager().getNPCMerchant(merchant) == null)
			{
				sender.sendMessage("Cannot find merchantid");
				return true;
			}
			
			if (StateManager.getInstance().getConfigurationManager().getLootDrop(name.toUpperCase()) != null)
			{
				sender.sendMessage("Lootdrop name already in use");
				return true;
			}
		
			SoliniaLootFactory.CreateLootDropFromMerchant(StateManager.getInstance().getConfigurationManager().getNPCMerchant(merchant), name, count, always, chance, sender.isOp());
			sender.sendMessage("LootDrop Created: " + StateManager.getInstance().getConfigurationManager().getLootDrop(name.toUpperCase()).getId());
			return true;
		} catch (CoreStateInitException e)
		{
			sender.sendMessage(e.getMessage());
		}
		return false;
	}
}
