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
					solplayer.increasePlayerExperience(pendingXp);
					solplayer.setPendingXp(solplayer.getPendingXp() - Utils.getMaxAAXP());
				} else {
					solplayer.increasePlayerExperience(pendingXp);
					solplayer.setPendingXp(0d);
				}
			} else {
				if (solplayer.getLevel() >= Utils.getMaxLevel())
				{
					sender.sendMessage("Cancelled claiming XP, you are already max level. Did you mean to toggleaa first?");
					return true;
				}
				
				Double currentexperience = solplayer.getExperience();
				double clevel = Utils.getLevelFromExperience(pendingXp);
				double nlevel = Utils.getLevelFromExperience((currentexperience + pendingXp));
				Double experience = 0d;
				if (nlevel > (clevel + 1)) {
					double xp = Utils.getExperienceRequirementForLevel((int) clevel + 1);
					experience = xp - currentexperience;
				} else {
					experience = currentexperience;
				}
				
				solplayer.increasePlayerExperience(experience);
				solplayer.setPendingXp(solplayer.getPendingXp()-experience);
			}

			sender.sendMessage("Remaining XP to be claimed: "+ solplayer.getPendingXp());
		} catch (CoreStateInitException e) {

		}
		return true;
	}

}
