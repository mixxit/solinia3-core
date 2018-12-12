package com.solinia.solinia.Commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaCraftCreationException;
import com.solinia.solinia.Factories.SoliniaCraftFactory;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaCraft;

public class CommandCreateCraft implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;
		
		if (sender instanceof Player)
		{
			Player player = (Player) sender;
			if (!player.isOp() && !player.hasPermission("solinia.createcraft"))
			{
				player.sendMessage("You do not have permission to access this command");
				return false;
			}
		}
		
		// Args
		// NameNoSpaces
		// itemid1
		// itemid2
		// outputitemid
		
		if (args.length < 4)
		{
			sender.sendMessage("Insufficient arguments: namenospaces itemid1 itemid2 outpitemid");
			return false;
		}
		
		String craftname = args[0].toUpperCase();
		int item1 = Integer.parseInt(args[1]);
		int item2 = Integer.parseInt(args[2]);
		int outputitem = Integer.parseInt(args[3]);
		
		if (craftname.equals(""))
		{
			sender.sendMessage("Name of Craft cannot be null");
			return false;
		}
		
		try {
			
			List<SoliniaCraft> existing = StateManager.getInstance().getConfigurationManager().getCrafts(item1,item2);
			if (existing.size() > 0)
			{
				sender.sendMessage("A recipe already exists that take these two items");
				return false;
			}
			
			SoliniaCraft craft = SoliniaCraftFactory.Create(craftname,item1, item2, outputitem);
			
			sender.sendMessage("Created Craft: " + craft.getId());
		} catch (CoreStateInitException | SoliniaCraftCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			sender.sendMessage(e.getMessage());
		}
		return true;
	}
}
