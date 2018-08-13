package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Utils.Utils;

public class CommandClaimXp implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		try {
			if (!(sender instanceof Player))
				return false;
			
			Player player = (Player) sender;
			ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(player);
			
			Double pendingXp = solplayer.getPendingXp();
			
			sender.sendMessage("Claiming XP: " + solplayer.getPendingXp().longValue());
			
			if (solplayer.isAAOn())
			{
				if (pendingXp > Utils.getMaxAAXP())
				{
					solplayer.increasePlayerExperience(pendingXp, false);
					solplayer.setPendingXp(solplayer.getPendingXp() - Utils.getMaxAAXP());
				} else {
					solplayer.increasePlayerExperience(pendingXp, false);
					solplayer.setPendingXp(0d);
				}
			} else {
				if (solplayer.getLevel() >= Utils.getMaxLevel())
				{
					sender.sendMessage("Cancelled claiming XP, you are already max level. Did you mean to toggleaa first?");
					return true;
				}
				
				Double currentexperience = solplayer.getExperience();
				double clevel = Utils.getLevelFromExperience(currentexperience);
				double nlevel = Utils.getLevelFromExperience((currentexperience + pendingXp));
				Double experience = 0d;
				if (nlevel > (clevel + 1)) {
					double xp = Utils.getExperienceRequirementForLevel((int) clevel + 1);
					experience = xp - currentexperience;
					sender.sendMessage("You have more experience than a levels worth, claiming some of the xp: " + experience);
					sender.sendMessage("Remainder will be: " + (solplayer.getPendingXp()-experience));
				} else {
					experience = pendingXp;
				}
				
				if (experience < 0)
				{
					sender.sendMessage("Trouble calculating your claim xp");
					return true;
				}
				
				solplayer.increasePlayerExperience(experience, false);
				solplayer.setPendingXp(solplayer.getPendingXp()-experience);
			}

			sender.sendMessage("Remaining XP to be claimed: "+ solplayer.getPendingXp());
		} catch (CoreStateInitException e) {

		}
		return true;
	}

}
