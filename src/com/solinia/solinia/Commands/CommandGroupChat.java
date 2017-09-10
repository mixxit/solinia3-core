package com.solinia.solinia.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;

public class CommandGroupChat implements CommandExecutor {
	public static String arrayToString(String[] a, String separator) {
	     String result = "";
	     if (a.length > 0) {
	       result = a[0];
	       for (int i = 1; i < a.length; i++) {
	         result = result + separator + a[i];
	       }
	     }
	     return result;
	   }
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// TODO Auto-generated method stub
		if (!(sender instanceof Player)) {
			return false;
		}
		Player player = (Player)sender;
		if (args.length == 0) {
			player.sendMessage("You did not supply a message to send");
			return true;
		}
		
		try
		{
			ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(player);
			
			if (solplayer.getGroup() == null)
			{
				player.sendMessage("Group does not exist");
				return true;
			}
			
			if (solplayer.getGroup().getMembers().size() < 2)
			{
				player.sendMessage("There is only 1 person in the group");
				return true;
			}
			
			String message = arrayToString(args," ");
			solplayer.getGroup().sendGroupMessage(player,ChatColor.AQUA + message + ChatColor.RESET);
			return true;
		} catch (CoreStateInitException e)
		{
			player.sendMessage(e.getMessage());
			return true;
		}
	}

}
