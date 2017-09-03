package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Factories.SoliniaNPCMerchantFactory;
import com.solinia.solinia.Managers.StateManager;

public class CommandAddMerchantItem implements CommandExecutor {
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
			sender.sendMessage("Insufficient arguments: merchantid itemid");
			return false;
		}
		
		try
		{
			int merchantid = Integer.parseInt(args[0]);
			int itemid = Integer.parseInt(args[1]);
			
			if (StateManager.getInstance().getConfigurationManager().getNPCMerchant(merchantid) == null)
			{
				sender.sendMessage("MerchantList does not exist");
				return true;
			}
			
			if (StateManager.getInstance().getConfigurationManager().getItem(itemid) == null)
			{
				sender.sendMessage("Item does not exist");
				return true;
			}
			
			SoliniaNPCMerchantFactory.AddNPCMerchantItem(merchantid, itemid);
			sender.sendMessage("MerchantList updated");
		} catch (CoreStateInitException e)
		{
			sender.sendMessage(e.getMessage());
		}
		return true;
	}
}
