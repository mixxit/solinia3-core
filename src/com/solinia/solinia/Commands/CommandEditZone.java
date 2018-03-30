package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidZoneSettingException;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaZone;

public class CommandEditZone implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;
		
		if (sender instanceof Player)
		{

			Player player = (Player) sender;
			
			if (!player.isOp() && !player.hasPermission("solinia.editzone"))
			{
				player.sendMessage("You do not have permission to access this command");
				return false;
			}
		}
		
		if (args.length < 1)
		{
			sender.sendMessage("Insufficient arguments: zoneid");
			return false;
		}
		
		// Args
		// zoneid
		// Setting
		// NewValue
		
		if (args.length == 0)
		{
			return false;
		}

		int zoneid = Integer.parseInt(args[0]);
		
		if (args.length == 1)
		{
			try
			{
				SoliniaZone zone = StateManager.getInstance().getConfigurationManager().getZone(zoneid);
				if (zone != null)
				{
					zone.sendZoneSettingsToSender(sender);
				} else {
					sender.sendMessage("Zone ID doesnt exist");
				}
				return true;
			} catch (CoreStateInitException e)
			{
				sender.sendMessage(e.getMessage());
			}
		}

		
		if (args.length < 3)
		{
			sender.sendMessage("Insufficient arguments: zoneid setting value");
			return false;
		}
		
		String setting = args[1];
		
		String value = args[2];
		
		if (zoneid < 1)
		{
			sender.sendMessage("Invalid zoneid");
			return false;
		}
		
		try
		{

			if (StateManager.getInstance().getConfigurationManager().getZone(zoneid) == null)
			{
				sender.sendMessage("Cannot locate zone id: " + zoneid);
				return false;
			}
			
			/*if (StateManager.getInstance().getConfigurationManager().getZone(zoneid).isOperatorCreated() && !sender.isOp())
			{
				sender.sendMessage("This zone was op created and you are not an op. Only ops can edit op zone items");
				return false;
			}*/

			StateManager.getInstance().getConfigurationManager().editZone(zoneid,setting,value);
			sender.sendMessage("Updating setting on Zone");
		} catch (InvalidZoneSettingException ne)
		{
			sender.sendMessage("Invalid zone setting: " + ne.getMessage());
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			sender.sendMessage(e.getMessage());
		}
		return true;
	}
}
