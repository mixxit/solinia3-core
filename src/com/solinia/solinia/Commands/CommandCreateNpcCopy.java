package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Factories.SoliniaNPCFactory;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Managers.StateManager;

public class CommandCreateNpcCopy implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;
		
		if (!sender.isOp() && !sender.hasPermission("solinia.createnpccopy"))
		{
			sender.sendMessage("You do not have permission to access this command");
			return false;
		}
		
		// Args
		// Level
		// NPC Name
		
		if (args.length < 2)
		{
			sender.sendMessage("Insufficient arguments: sourcenpcid npcname");
			return false;
		}
		
		int npcid = Integer.parseInt(args[0]);
		if (npcid < 1)
		{
			sender.sendMessage("Invalid npc id");
			return false;
		}
		
		try {
			if (StateManager.getInstance().getConfigurationManager().getNPC(npcid) == null)
			{
				sender.sendMessage("Cannot locate npc id: " + npcid);
				return false;
			}
			
			String name = "";
			int i = 0;
			for(String element : args)
			{
				if (i <= 0)
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
			
			name = name.replace(" ", "_");
		
			ISoliniaNPC npc = SoliniaNPCFactory.CreateNPCCopy(npcid,name, false);
			sender.sendMessage("Created NPC Copy: " + npc.getId());
			sender.sendMessage("You have updated a mythic mob npc, for changes to appear in game you will need to do /mm reload");
		} catch (Exception e) {
			sender.sendMessage(e.getMessage());
		}
		return true;
	}

}
