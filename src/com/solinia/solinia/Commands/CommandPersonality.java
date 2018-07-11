package com.solinia.solinia.Commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;

import net.md_5.bungee.api.ChatColor;


public class CommandPersonality implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
		{
			sender.sendMessage("This is a Player only command");
			return false;
		}
		
		try {
			ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player)sender);
			solPlayer.getPersonality();
			
			sender.sendMessage(ChatColor.GOLD + "Ideals" + ChatColor.RESET);
			if (solPlayer.getPersonality().getIdealId() > 0)
			{
				sender.sendMessage("- " + solPlayer.getPersonality().getIdeal().description);
			} else {
				sender.sendMessage("- You have no ideal set [click here to select]");
			}
			
			sender.sendMessage(ChatColor.GOLD + "Traits" + ChatColor.RESET);
			if (solPlayer.getPersonality().getFirstTraitId() > 0)
			{
				sender.sendMessage("- " + solPlayer.getPersonality().getFirstTrait().description);
			} else {
				sender.sendMessage("- You have no first trait set [click here to select]");
			}
			if (solPlayer.getPersonality().getSecondTraitId() > 0)
			{
				sender.sendMessage("- " + solPlayer.getPersonality().getSecondTrait().description);
			} else {
				sender.sendMessage("- You have no second trait set [click here to select]");
			}
			
			sender.sendMessage(ChatColor.GOLD + "Bonds" + ChatColor.RESET);
			if (solPlayer.getPersonality().getBondId() > 0)
			{
				sender.sendMessage("- " + solPlayer.getPersonality().getBond().description);
			} else {
				sender.sendMessage("- You have no bond set [click here to select]");
			}
			
			sender.sendMessage(ChatColor.GOLD + "Flaws" + ChatColor.RESET);
			if (solPlayer.getPersonality().getFlawId() > 0)
			{
				sender.sendMessage("- " + solPlayer.getPersonality().getFlaw().description);
			} else {
				sender.sendMessage("- You have no flaw set [click here to select]");
			}
			
			
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		return true;
	}
}
