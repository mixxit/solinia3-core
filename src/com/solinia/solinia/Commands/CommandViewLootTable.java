package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaLootDrop;
import com.solinia.solinia.Interfaces.ISoliniaLootDropEntry;
import com.solinia.solinia.Interfaces.ISoliniaLootTable;
import com.solinia.solinia.Interfaces.ISoliniaLootTableEntry;
import com.solinia.solinia.Managers.StateManager;

import net.md_5.bungee.api.ChatColor;

public class CommandViewLootTable implements CommandExecutor {
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
		
		if (args.length < 1)
		{
			sender.sendMessage("Insufficient arguments: loottableid");
			return false;
		}
		
		int id = Integer.parseInt(args[0]);
		
		try
		{
		sender.sendMessage(ChatColor.RED + "("+StateManager.getInstance().getConfigurationManager().getLootTable(id).getName()+")" + ChatColor.RESET + "[" + id + "]");
		ISoliniaLootTable loottable = StateManager.getInstance().getConfigurationManager().getLootTable(id);
		for(ISoliniaLootTableEntry le : loottable.getEntries())
		{
			ISoliniaLootDrop ld = StateManager.getInstance().getConfigurationManager().getLootDrop(le.getLootdropid());
			sender.sendMessage("- " + ChatColor.GOLD + ld.getName().toUpperCase() + ChatColor.RESET + "[" + ld.getId() + "]:");
			for(ISoliniaLootDropEntry lde : ld.getEntries())
			{
				ISoliniaItem i = StateManager.getInstance().getConfigurationManager().getItem(lde.getItemid());
				sender.sendMessage("  - " + ChatColor.GOLD + i.getDisplayname() + ChatColor.RESET + "[" + i.getId() + "] - " + lde.getChance() + "% chance Count: " + lde.getCount() + " Always: " + lde.isAlways());
			}
		}
		} catch (CoreStateInitException e)
		{
			sender.sendMessage(e.getMessage());
		}
		
		return true;
	}
}
