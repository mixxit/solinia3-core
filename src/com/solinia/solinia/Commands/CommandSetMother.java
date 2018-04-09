package com.solinia.solinia.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;

public class CommandSetMother implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
		{
			return false;
		}
		
		try {
			ISoliniaPlayer sourcePlayer = SoliniaPlayerAdapter.Adapt((Player)sender);
			
			if (args.length < 1)
			{
				sourcePlayer.sendFamilyTree();
				return true;
			}
			
			if (Bukkit.getPlayer(args[0]) == null)
			{
				sender.sendMessage("Cannot find player");
				return true;
			}
		
			Player targetTo = Bukkit.getPlayer(args[0]);
			ISoliniaPlayer targetPlayer = SoliniaPlayerAdapter.Adapt(targetTo);
			
			sourcePlayer.setMotherId(targetPlayer.getCharacterId());
			sender.sendMessage("Your mother has been set to: " + targetPlayer.getFullNameWithTitle());
			return true;
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			sender.sendMessage(e.getMessage());
			return true;
		}
	}
}
