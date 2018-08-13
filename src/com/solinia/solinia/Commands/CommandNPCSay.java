package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Managers.StateManager;

public class CommandNPCSay implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		Player player = (Player) sender;
		
		if (!player.isOp() && !player.hasPermission("solinia.npcsay"))
		{
			player.sendMessage("You do not have permission to access this command");
			return false;
		}
		
		// Args
		// NPCID
		// Setting
		// NewValue
		
		if (args.length == 0)
		{
			return false;
		}

		int npcid = Integer.parseInt(args[0]);
		
		if (args.length == 1)
		{
			try
			{
				ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(npcid);
				if (npc != null)
				{
					npc.sendNpcSettingsToSender(sender);
				} else {
					sender.sendMessage("NPC ID doesnt exist");
				}
				return true;
			} catch (CoreStateInitException e)
			{
				sender.sendMessage(e.getMessage());
			}
		}

		
		if (args.length < 2)
		{
			sender.sendMessage("Insufficient arguments: npcid message");
			return false;
		}
		
		String setting = args[1];
		
		String message = args[2];
		
		// for 'text' based npc settings like trigger texts etc, get the whole thing as a string
		if (args.length > 2)
		{
			message = "";
			int current = 0;
			for(String entry : args)
			{
				current++;
				if (current <= 1)
					continue;
				
				message = message + entry + " ";
			}
			
			message = message.trim();
		}
		
		if (npcid < 1)
		{
			sender.sendMessage("Invalid npc id");
			return false;
		}
		
		try
		{

			if (StateManager.getInstance().getConfigurationManager().getNPC(npcid) == null)
			{
				sender.sendMessage("Cannot locate npc id: " + npcid);
				return false;
			}
			
			for(Entity entity : player.getNearbyEntities(25, 25, 25))
			{
				if (!(entity instanceof LivingEntity))
					continue;
				
				
				ISoliniaLivingEntity solEntity = SoliniaLivingEntityAdapter.Adapt((LivingEntity)entity);
				if (!solEntity.isNPC())
					continue;
				
				if (solEntity.getNpcid() != npcid)
					continue;
				
				solEntity.say(message);
			}
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			sender.sendMessage(e.getMessage());
		}
		return true;
	}
}
