package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Models.PlayerFactionEntry;
import com.solinia.solinia.Utils.Utils;

import net.md_5.bungee.api.ChatColor;

public class CommandFaction implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		try {
			ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player)sender);
			
			for (PlayerFactionEntry factionEntry : solPlayer.getFactionEntries())
			{
				sender.sendMessage(" * " + ChatColor.GOLD + factionEntry.getFaction().getName() + ChatColor.RESET + " - " + Utils.getFactionStandingType(factionEntry.getFactionId(), factionEntry.getValue()).name());
			}
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			sender.sendMessage(e.getMessage());
			e.printStackTrace();
		}
		
		return true;
	}
}
