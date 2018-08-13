package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaZone;

public class CommandHotzones implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		
		arg0.sendMessage("Current 100% bonus XP Hotzones are: ");
		for(SoliniaZone zone : StateManager.getInstance().getCurrentHotzones())
		{
			arg0.sendMessage(zone.getName() + ": " + zone.getX() + "," + zone.getY() + "," + zone.getZ());
		}
		
		return true;
	}

}
