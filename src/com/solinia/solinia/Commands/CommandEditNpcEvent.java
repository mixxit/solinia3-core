package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidNPCEventSettingException;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Interfaces.ISoliniaNPCEventHandler;
import com.solinia.solinia.Managers.StateManager;

public class CommandEditNpcEvent implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;
		
		if (sender instanceof Player)
		{

			Player player = (Player) sender;
			
			if (!player.isOp() && !player.hasPermission("solinia.editnpcevent"))
			{
				player.sendMessage("You do not have permission to access this command");
				return false;
			}
		}
		
		// Args
		// NPCID
		// TriggerText
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
					npc.sendNPCEvents(sender);
				} else {
					sender.sendMessage("NPC ID doesnt exist");
				}
				return true;
			} catch (CoreStateInitException e)
			{
				sender.sendMessage(e.getMessage());
			}
		}
		
		String triggertext = args[1];
		
		if (args.length == 2)
		{
			try
			{
				ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(npcid);
				if (npc != null)
				{
					boolean found = false;
					for(ISoliniaNPCEventHandler handler : npc.getEventHandlers())
					{
						if (handler.getTriggerdata().toUpperCase().equals(triggertext.toUpperCase()))
						{
							found = true;
							npc.sendNPCEvent(sender, triggertext);
						}
					}
					
					if (found == false)
					{
						sender.sendMessage("Trigger event doesnt exist on npc");
					}
					
				} else {
					sender.sendMessage("NPC ID doesnt exist");
				}
				return true;
			} catch (CoreStateInitException e)
			{
				sender.sendMessage(e.getMessage());
			}
		}

		
		if (args.length < 4)
		{
			sender.sendMessage("Insufficient arguments: npcid triggertext setting value");
			return false;
		}
		
		String setting = args[2];
		String value = args[3];
		
		// for 'text' based npc settings like trigger texts etc, get the whole thing as a string
		if (args.length > 4 && (setting.toLowerCase().equals("chatresponse") || setting.toLowerCase().equals("title") || setting.toLowerCase().equals("randomisedgearsuffix")))
		{
			value = "";
			int current = 0;
			for(String entry : args)
			{
				current++;
				if (current <= 3)
					continue;
				value = value + entry + " ";
			}	
			value = value.trim();
		}
		
		if (npcid < 1)
		{
			sender.sendMessage("Invalid NPC id");
			return false;
		}
		
		try
		{
			ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(npcid);
			
			if (npc != null)
			{
				boolean found = false;
				for(ISoliniaNPCEventHandler handler : npc.getEventHandlers())
				{
					if (handler.getTriggerdata().toUpperCase().equals(triggertext.toUpperCase()))
					{
						found = true;
						if (handler.isOperatorCreated() && !sender.isOp())
						{
							sender.sendMessage("This npcevent was op created and you are not an op. Only ops can edit op npcevent items");
							return false;
						}
					}
				}
				
				if (found == false)
				{
					sender.sendMessage("Trigger event doesnt exist on npc");
					return false;
				}
				
			} else {
				sender.sendMessage("NPC ID doesnt exist");
				return false;
			}
			
			

			StateManager.getInstance().getConfigurationManager().editNpcTriggerEvent(npcid,triggertext,setting,value);
			sender.sendMessage("Updating setting on NPC Event");
		} catch (InvalidNPCEventSettingException ne)
		{
			sender.sendMessage("Invalid NPC Event Setting: " + ne.getMessage());
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			sender.sendMessage(e.getMessage());
		}
		return true;
	}
}
