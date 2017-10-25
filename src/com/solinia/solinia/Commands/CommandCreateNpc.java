package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Factories.SoliniaNPCFactory;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Managers.StateManager;

public class CommandCreateNpc implements CommandExecutor {

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
		
		// Args
		// Level
		// NPC Name
		
		if (args.length < 3)
		{
			sender.sendMessage("Insufficient arguments: factionid level npcname");
			sender.sendMessage("Note: factionid can be 0 to denote KOS to everything");
			return false;
		}
		
		int factionid = Integer.parseInt(args[0]);
		int level = Integer.parseInt(args[1]);
		
		if (level > 60)
		{
			sender.sendMessage("NPC should not be greater than level 60");
			return false;
		}
		
		if (factionid < 0)
		{
			sender.sendMessage("Invalid faction id");
			return false;
		}

		try {
			if (factionid > 0 && StateManager.getInstance().getConfigurationManager().getFaction(factionid) == null)
			{
				sender.sendMessage("Cannot locate faction id: " + factionid);
				return false;
			}
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			sender.sendMessage(e.getMessage());
			return false;
		}
		
		String name = "";
		int i = 0;
		for(String element : args)
		{
			if (i <= 1)
			{
				i++;
				continue;
			}
			
			name += element;
			i++;
		}
		
		if (name.equals(""))
		{
			sender.sendMessage("Name of NPC cannot be null");
			return false;
		}
		
		if (name.length() > 16)
		{
			sender.sendMessage("Name of NPC cannot exceed 16 characters");
			return false;
		}
		
		name = name.replace(" ", "_");
		
		try {
			ISoliniaNPC npc = SoliniaNPCFactory.CreateNPC(name,level, factionid);
			sender.sendMessage("Created NPC: " + npc.getId());
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			sender.sendMessage(e.getMessage());
		}
		return true;
	}

}
