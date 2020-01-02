package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Utils.PlayerUtils;

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
			
			if (!solplayer.isExperienceOn())
			{
				sender.sendMessage("You currently have experience toggled off");
				return true;
			}
			
			if (solplayer.isAAOn())
			{
				if (pendingXp > PlayerUtils.getMaxAAXP())
				{
					solplayer.increasePlayerExperience(pendingXp, false, false);
					solplayer.setPendingXp(solplayer.getPendingXp() - PlayerUtils.getMaxAAXP());
				} else {
					solplayer.increasePlayerExperience(pendingXp, false, false);
					solplayer.setPendingXp(0d);
				}
			} else {
				if (solplayer.getExperience() >= StateManager.getInstance().getConfigurationManager().getMaxExperience())
				{
					sender.sendMessage("Cancelled claiming XP, you are already max experience. Did you mean to toggleaa first?");
					return true;
				}
				
				Double currentexperience = solplayer.getExperience();
				double clevel = PlayerUtils.getLevelFromExperience(currentexperience);
				double nlevel = PlayerUtils.getLevelFromExperience((currentexperience + pendingXp));
				Double experience = 0d;
				if (nlevel > (clevel + 1)) {
					double xp = PlayerUtils.getExperienceRequirementForLevel((int) clevel + 1);
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
				
				solplayer.increasePlayerExperience(experience, false, false);
				solplayer.setPendingXp(solplayer.getPendingXp()-experience);
			}

			sender.sendMessage("Remaining XP to be claimed: "+ solplayer.getPendingXp());
		} catch (CoreStateInitException e) {

		}
		return true;
	}

}
