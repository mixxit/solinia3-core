package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaZoneCreationException;
import com.solinia.solinia.Factories.SoliniaZoneFactory;
import com.solinia.solinia.Models.SoliniaZone;

public class CommandCreateZone implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;
		
		if (sender instanceof Player)
		{
			Player player = (Player) sender;
			if (!player.isOp() && !player.hasPermission("solinia.createzone"))
			{
				player.sendMessage("You do not have permission to access this command");
				return false;
			}
		}
		
		// Args
		// NameNoSpaces
		// X
		// Y
		// Z
		
		if (args.length < 4)
		{
			sender.sendMessage("Insufficient arguments: namenospaces x y z");
			return false;
		}
		
		String zonename = args[0].toUpperCase();
		int x = Integer.parseInt(args[1]);
		int y = Integer.parseInt(args[2]);
		int z = Integer.parseInt(args[3]);
		
		if (zonename.equals(""))
		{
			sender.sendMessage("Name of Zone cannot be null");
			return false;
		}
		
		try {
			SoliniaZone zone = SoliniaZoneFactory.Create(zonename,x, y, z);
			
			sender.sendMessage("Created Zone: " + zone.getId());
		} catch (CoreStateInitException | SoliniaZoneCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			sender.sendMessage(e.getMessage());
		}
		return true;
	}
}
