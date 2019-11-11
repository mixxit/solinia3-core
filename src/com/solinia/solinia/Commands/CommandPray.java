package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;

public class CommandPray implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;
		
		if (!(sender instanceof Player))
		{
			sender.sendMessage("This is a player only command");
			return false;
		}
		
		if (args.length == 0)
        {
			sender.sendMessage("You must say something for your prayer");
			return false;
        }
		
		Player player = (Player)sender;
        try
        {
        	ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
        	if (!solPlayer.getClassObj().canPray())
        	{
        		player.sendMessage("* The Gods are silent (only a religious or priestly class may act as a conduit for the Gods)");
        		return false;
        	}
        	
        	String prayerText = "";
        	for (String entry : args) {
        		prayerText = prayerText + entry + " ";
			}
        	prayerText.trim();
        	
        	String message = "[" + solPlayer.getClassObj().getName() + "/" + player.getName() + "] " + player.getDisplayName() + " is praying to the Gods! " + prayerText;
        		
        	sender.sendMessage("* The Gods have heard your prayer");
        } catch (Exception e)
        {
        	e.printStackTrace();
        	sender.sendMessage(e.getMessage());
        }

		return true;
	}
}
