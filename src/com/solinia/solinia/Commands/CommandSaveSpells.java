package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Solinia3CorePlugin;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;

public class CommandSaveSpells implements CommandExecutor {

	Solinia3CorePlugin plugin;
	
	public CommandSaveSpells(Solinia3CorePlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
			return false;

		Player player = (Player) sender;

		try {
			ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(player);
			
			if (StateManager.getInstance().charChange == false)
			{
				sender.sendMessage("This command cannot be used when the server has been set to charchange false (ie a restart is coming)");
				return true;
			}
			
			if (args.length == 0)
			{
				player.sendMessage("You must provide a word to identify the loadout Ie /savespells lvl50damagespells");
			} else {
				String name = args[0].toUpperCase();
				solplayer.getSpellLoadout().put(name, solplayer.getActiveSpellLoadout());
				player.sendMessage("Spell loadout saved as : " + name);
			}
		} catch (CoreStateInitException e) {

		}
		return true;
	}
}