package com.solinia.solinia.Commands;

import org.bukkit.Bukkit;
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
		
		if (!sender.isOp() && !sender.hasPermission("solinia.createzone"))
		{
			sender.sendMessage("You do not have permission to access this command");
			return false;
		}
		
		// Args
		// NameNoSpaces
		// X
		// Y
		// Z
		// SuccorX
		// SuccorY
		// SuccorZ
		// worldname
		
		if (args.length < 8)
		{
			sender.sendMessage("Insufficient arguments: namenospaces bottomleftx bottomlefty bottomleftz toprightx toprighty toprightz succorx succory succorz worldname");
			return false;
		}
		
		String zonename = args[0].toUpperCase();
		int bottomleftx = Integer.parseInt(args[1]);
		int bottomlefty = Integer.parseInt(args[2]);
		int bottomleftz = Integer.parseInt(args[3]);

		int toprightx = Integer.parseInt(args[4]);
		int toprighty = Integer.parseInt(args[5]);
		int toprightz = Integer.parseInt(args[6]);
		
		int succorx = Integer.parseInt(args[7]);
		int succory = Integer.parseInt(args[8]);
		int succorz = Integer.parseInt(args[9]);
		
		String world = Bukkit.getWorld(args[10]).getName();
		
		if (zonename.equals(""))
		{
			sender.sendMessage("Name of Zone cannot be null");
			return false;
		}
		
		try {
			SoliniaZone zone = SoliniaZoneFactory.Create(zonename,world, bottomleftx, bottomlefty, bottomleftz,toprightx,toprighty,toprightz, succorx, succory, succorz);
			
			sender.sendMessage("Created Zone: " + zone.getId());
		} catch (CoreStateInitException | SoliniaZoneCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			sender.sendMessage(e.getMessage());
		}
		return true;
	}
}
