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
			arg0.sendMessage(zone.getName() + ": " + zone.getWorld() + ":" +  zone.getMiddleX() + "," + zone.getMiddleY() + "," + zone.getMiddleZ());
		}
		
		if (arg3.length >= 2)
		{
			if (arg0.isOp())
			{
				if (arg3[0].equals("forcehotzone"))
				{
					Integer hotzone = Integer.parseInt(arg3[1]);
					try {
						StateManager.getInstance().forceHotzone(hotzone, true);
						arg0.sendMessage("First new hotzone set");
					} catch (Exception e) {
						arg0.sendMessage(e.getMessage());
						return true;
					}
					
					if (arg3.length > 2)
					{
						hotzone = Integer.parseInt(arg3[2]);
						try {
							StateManager.getInstance().forceHotzone(hotzone, false);
							arg0.sendMessage("Second new hotzone set");
						} catch (Exception e) {
							arg0.sendMessage(e.getMessage());
							return true;
						}
					}
					
					
				}
			}
		}
		
		return true;
	}

}
