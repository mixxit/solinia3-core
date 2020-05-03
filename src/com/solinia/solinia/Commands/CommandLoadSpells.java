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
import com.solinia.solinia.Models.SpellLoadout;

public class CommandLoadSpells implements CommandExecutor {

	Solinia3CorePlugin plugin;
	
	public CommandLoadSpells(Solinia3CorePlugin plugin) {
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
				player.sendMessage("Your Saved Spells: ");
				for (String key : solplayer.getSpellLoadout().keySet())
				{
					player.sendMessage("/loadspells " + key);
				}
			} else {
				String name = args[0].toUpperCase();
				SpellLoadout loadout = solplayer.getSpellLoadout().get(name);
				if (loadout == null)
				{
					player.sendMessage("Loadout doesnt exist");
					return true;
				}
				
				solplayer.loadSpellLoadout(loadout);
			}
		} catch (CoreStateInitException e) {

		}
		return true;
	}
}