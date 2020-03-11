package com.solinia.solinia.Commands;

import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SpellResistType;
import com.solinia.solinia.Utils.PlayerUtils;
import net.md_5.bungee.api.ChatColor;

public class CommandStats implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			try {
	            Player player = (Player) sender;
	            ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(player);
	            ISoliniaLivingEntity solentity = SoliniaLivingEntityAdapter.Adapt(player);
	            solentity.sendStats((LivingEntity)player);

	            Double newlevel = (double) solplayer.getLevel();
	            Double xpneededforcurrentlevel = PlayerUtils.getExperienceRequirementForLevel((int) (newlevel + 0));
	    		Double xpneededfornextlevel = PlayerUtils.getExperienceRequirementForLevel((int) (newlevel + 1));
	    		Double totalxpneeded = xpneededfornextlevel - xpneededforcurrentlevel;
	    		Double currentxpprogress = solplayer.getExperience() - xpneededforcurrentlevel;
	    		
	            Double percenttolevel = Math.floor((currentxpprogress / totalxpneeded) * 100);
	            Double percenttoaa = Math.floor((solplayer.getAAExperience() / PlayerUtils.getMaxAAXP()) * 100);
	    		int ipercenttolevel = percenttolevel.intValue();
	    		int ipercenttoaa = percenttoaa.intValue();
	    		player.sendMessage("Level progress: " + ipercenttolevel + "% into level - Have: " + ChatColor.GOLD+ solplayer.getExperience().longValue() + ChatColor.RESET + " Need: " + ChatColor.GOLD+ xpneededfornextlevel.longValue() + ChatColor.RESET);
	    		player.sendMessage("AA points: " + solplayer.getAAPoints());
	    		player.sendMessage("Next AA progress: " + ipercenttoaa + "% into AA - Have: " + ChatColor.GOLD+solplayer.getAAExperience().longValue() + ChatColor.RESET + " Need: " + ChatColor.GOLD+ PlayerUtils.getMaxAAXP().longValue() + ChatColor.RESET);
	    		player.sendMessage("From being online you have saved up Attendence Xp every minute: " + ChatColor.GOLD+ solplayer.getPendingXp().longValue() + ChatColor.RESET);
	    		player.sendMessage("Use /claimxp to claim your attendence xp");
	    		player.sendMessage("Trancing/Meditating: " + ChatColor.GOLD + solplayer.isMeditating() + ChatColor.RESET);
	    		
	    		ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
				if (solPlayer.getPersonality().getBondId() == 0 || 
						solPlayer.getPersonality().getFirstTraitId() == 0 ||
						solPlayer.getPersonality().getSecondTraitId() == 0 ||
						solPlayer.getPersonality().getFlawId() == 0 ||
						solPlayer.getPersonality().getIdealId() == 0
						)
				{
					player.sendMessage(ChatColor.GRAY + "* You have not set your personality. Please see /personality" + ChatColor.RESET);
				}
				
				if (solPlayer.getClassObj() != null && solPlayer.getClassObj().getOaths().size() > 0 && solPlayer.getOathId() == 0)
				{
					player.sendMessage(ChatColor.GRAY + "* You have not set your Oath. Please see /oath" + ChatColor.RESET);
				}
				
				player.sendMessage("* Your currently XP Day modifier sits at (" + StateManager.getInstance().getXPDayModifier() + ")%");
				player.sendMessage("* ReverseAggroCount: " + solPlayer.getSoliniaLivingEntity().getReverseAggroCount());
				
			} catch (CoreStateInitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
		}
		return true;
	}
}
