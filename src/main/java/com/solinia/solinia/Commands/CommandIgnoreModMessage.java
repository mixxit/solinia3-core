package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Solinia3CorePlugin;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;

public class CommandIgnoreModMessage implements CommandExecutor {
	Solinia3CorePlugin plugin;

	public CommandIgnoreModMessage(Solinia3CorePlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
if (sender instanceof Player) {
			
			try
			{
				ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player)sender);
				if (solPlayer.isModMessageEnabled() == false)
				{
					solPlayer.setModMessageEnabled(true);
					sender.sendMessage("Mod Message enabled");
				} else {
					solPlayer.setModMessageEnabled(false);
					sender.sendMessage("Mod Message disabled");
				}
			} catch (CoreStateInitException e)
			{
				return false;
			}
            return true;
		}
		return false;
	}

}
