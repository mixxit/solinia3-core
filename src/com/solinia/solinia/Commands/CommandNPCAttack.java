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

public class CommandNPCAttack implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		Player player = (Player) sender;
		
		if (!sender.isOp() && !sender.hasPermission("solinia.npcattack"))
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
			sender.sendMessage("Insufficient arguments: npcid targetentityname");
			return false;
		}
		
		String targetentityname = args[1];
		
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
				
				for(Entity targetEntity : player.getNearbyEntities(25, 25, 25))
				{
					if (!targetEntity.getName().equals(targetentityname))
						continue;
					
					solEntity.addToHateList(targetEntity.getUniqueId(), 999999);
					sender.sendMessage("Added " + targetEntity.getName() + " to hate list of " + entity.getName() + " with 999999 hate");
				}
				
			}
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			sender.sendMessage(e.getMessage());
		}
		return true;
	}
}
