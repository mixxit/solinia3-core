package com.solinia.solinia.Commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaZone;
import com.solinia.solinia.Utils.EntityUtils;

public class CommandZoneTp implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender send, Command cmd, String label, String[] args) {
		if (!(send instanceof Player))
			return false;
		
		Player player = (Player) send;
		
		if (!player.isOp() && !player.hasPermission("solinia.editzone"))
		{
			player.sendMessage("You do not have permission to access this command");
			return false;
		}
		
		if (args.length < 1)
		{
			player.sendMessage("Insufficient arguments: zoneid");
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
		
		try
		{

			SoliniaZone zone = StateManager.getInstance().getConfigurationManager().getZone(zoneid);
			if (zone == null)
			{
				player.sendMessage("Cannot locate zone id: " + zoneid);
				return false;
			}
			
			if (zone.getSuccorx() == 0 && zone.getSuccory() == 0 && zone.getSuccorz() == 0) {
				player.sendMessage(
						"Succor failed! Your body could not be pulled by an astral anchor (zone has no succor point)!");
				return true;
			}

			double x = (zone.getSuccorx());
			double y = (zone.getSuccory());
			double z = (zone.getSuccorz());
			Location loc = new Location(Bukkit.getWorld("world"), x, y, z);
			EntityUtils.teleportSafely(player,loc);

		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			player.sendMessage(e.getMessage());
		}
		return true;
	}
}
