package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;

public class CommandAutoCast implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		Player player = (Player) sender;
		
		if (args.length == 0)
			return false;
		
		int slotId = Integer.parseInt(args[0]);
		if (slotId < 0 || slotId > 10)
			return false;
		
		try
		{
			ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
			solPlayer.setAutoCast(slotId);
			sender.sendMessage("Auto cast toggled to : " + slotId);
		} catch (CoreStateInitException e)
		{
			
		}
		
		return true;
	}
}
