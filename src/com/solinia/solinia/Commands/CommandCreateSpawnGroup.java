package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaSpawnGroupCreationException;
import com.solinia.solinia.Factories.SoliniaSpawnGroupFactory;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Managers.StateManager;

public class CommandCreateSpawnGroup implements CommandExecutor{
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		Player player = (Player)sender;
		
		if (!player.isOp() && !player.hasPermission("solinia.createspawngroup"))
		{
			player.sendMessage("You do not have permission to access this command");
			return false;
		}
		
		if (args.length < 2)
		{
			sender.sendMessage("Insufficient arguments: npcid, spawngroupname");
			return false;
		}
		
		// args
		// defaultnpcid
		// spawngroupname
		
		int npcid = Integer.parseInt(args[0]);
		
		if (npcid < 1)
		{
			sender.sendMessage("NPCID is invalid");
			return false;
		}
		
		ISoliniaNPC npc;
		try {
			npc = StateManager.getInstance().getConfigurationManager().getNPC(npcid);
		} catch (CoreStateInitException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			sender.sendMessage(e1.getMessage());
			return true;
		}
		if (npc == null)
		{
			sender.sendMessage("The ID number you provided for the NPC is invalid");
			return false;
		}
		
		String spawngroupname = "";
		int count = 0;
		for(String entry : args)
		{
			if (count == 0)
			{
				count++;
				continue;
			}
			
			spawngroupname += entry;
			count++;
		}
		
		if (spawngroupname.equals(""))
		{
			sender.sendMessage("Blank name not allowed when creating spawngroup");
			return false;
		}
		
		spawngroupname.replace(" ", "_");
		try {
			int id = SoliniaSpawnGroupFactory.Create(spawngroupname, npcid, player.getLocation());
			sender.sendMessage("SpawnGroup ID " + id + " created at your location");

		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			sender.sendMessage(e.getMessage());
			return true;
		} catch (SoliniaSpawnGroupCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			sender.sendMessage(e.getMessage());
			return true;
		}
		return true;
	}
}
