package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Factories.SoliniaNPCMerchantFactory;
import com.solinia.solinia.Managers.StateManager;

public class CommandCreateMerchantList implements CommandExecutor {
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
		
		if (args.length < 1)
		{
			sender.sendMessage("Insufficient arguments: name");
			return false;
		}
		
		String merchantlistname = "";
		for(String entry : args)
		{
			merchantlistname  += entry;
		}
		
		if (merchantlistname.equals(""))
		{
			sender.sendMessage("Blank name not allowed when creating merchant list");
			return false;
		}
		
		merchantlistname.replace(" ", "_");
		
		try
		{
			if (StateManager.getInstance().getConfigurationManager().getNPCMerchant(merchantlistname.toUpperCase()) != null)
			{
				sender.sendMessage("MerchantList already exists with the same name");
				return true;
			}
			
			SoliniaNPCMerchantFactory.CreateNPCMerchant(merchantlistname);
			sender.sendMessage("MerchantList created");
		} catch (CoreStateInitException e)
		{
			sender.sendMessage(e.getMessage());
		}
		return true;
	}
}
