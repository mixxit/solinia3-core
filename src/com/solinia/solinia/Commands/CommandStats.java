package com.solinia.solinia.Commands;

import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SpellResistType;
import com.solinia.solinia.Utils.Utils;

import net.md_5.bungee.api.ChatColor;

public class CommandStats implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			try {
	            Player player = (Player) sender;
	            ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(player);
	            ISoliniaLivingEntity solentity = SoliniaLivingEntityAdapter.Adapt(player);

				player.sendMessage("Level: " + ChatColor.GOLD + solplayer.getLevel() + ChatColor.RESET);

				if (solplayer.getClassObj() != null)
				{
					player.sendMessage("Your Class: " + ChatColor.GOLD + solplayer.getClassObj().getName() + ChatColor.RESET);
				} else {
					player.sendMessage("Your Class: " + ChatColor.GOLD + "Unknown" + ChatColor.RESET);
				}
				if (solplayer.getRace() != null)
				{
					player.sendMessage("Your Race: " + ChatColor.GOLD + solplayer.getRace().getName() + ChatColor.RESET);
				} else {
					player.sendMessage("Your Race: " + ChatColor.GOLD + "Unknown" + ChatColor.RESET);
				}
				
				player.sendMessage(
						"STR: " + ChatColor.GOLD + solentity.getStrength() + ChatColor.RESET + 
						" STA: " + ChatColor.GOLD + solentity.getStamina() + ChatColor.RESET + 
						" AGI: " + ChatColor.GOLD + solentity.getAgility() + ChatColor.RESET + 
						" DEX: " + ChatColor.GOLD + solentity.getDexterity() + ChatColor.RESET + 
						" INT: " + ChatColor.GOLD + solentity.getIntelligence() + ChatColor.RESET + 
						" WIS: " + ChatColor.GOLD + solentity.getWisdom() + ChatColor.RESET + 
						" CHA: " + ChatColor.GOLD + solentity.getCharisma() + ChatColor.RESET 
						);
	            player.sendMessage("You have a maximum HP of: " + ChatColor.RED + player.getHealth() + "/" + player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() + ChatColor.RESET);
	            player.sendMessage("You have a maximum MP of: " + ChatColor.AQUA +solplayer.getMana() + "/" + solentity.getMaxMP() + " (" + solplayer.getSoliniaLivingEntity().getItemMana() + " of this is from items)" + ChatColor.RESET);
	            player.sendMessage("You currently have a Armour Class Mitigation of: " + ChatColor.GOLD + solentity.getMitigationAC() + ChatColor.RESET);
	            player.sendMessage("You currently have a Attack Value of: " + ChatColor.GOLD+ solentity.getAttk() + ChatColor.RESET);
	            player.sendMessage("You currently have a Attack Speed of: " + ChatColor.GOLD+ solentity.getAttackSpeed() + "%" + ChatColor.RESET);
	            player.sendMessage("You currently have a MainWeapon Attack Rate (Seconds) of: " + ChatColor.GOLD+ solentity.getAutoAttackTimerFrequencySeconds() + ChatColor.RESET);
	            player.sendMessage("You currently have a Total Rune of: " + ChatColor.GOLD + solentity.getRune() + ChatColor.RESET);
	            player.sendMessage(
	            		"FR: " + ChatColor.GOLD + solplayer.getResist(SpellResistType.RESIST_FIRE) + ChatColor.RESET + 
	            		" CR: " + ChatColor.GOLD + solplayer.getResist(SpellResistType.RESIST_COLD) + ChatColor.RESET + 
	            		" MR: " + ChatColor.GOLD + solplayer.getResist(SpellResistType.RESIST_MAGIC) + ChatColor.RESET + 
	            		" PR: " + ChatColor.GOLD + solplayer.getResist(SpellResistType.RESIST_POISON) + ChatColor.RESET + 
	            		" DR: " + ChatColor.GOLD + solplayer.getResist(SpellResistType.RESIST_DISEASE) + ChatColor.RESET
	            		);

	            Double newlevel = (double) solplayer.getLevel();
	            Double xpneededforcurrentlevel = Utils.getExperienceRequirementForLevel((int) (newlevel + 0));
	    		Double xpneededfornextlevel = Utils.getExperienceRequirementForLevel((int) (newlevel + 1));
	    		Double totalxpneeded = xpneededfornextlevel - xpneededforcurrentlevel;
	    		Double currentxpprogress = solplayer.getExperience() - xpneededforcurrentlevel;
	    		
	            Double percenttolevel = Math.floor((currentxpprogress / totalxpneeded) * 100);
	            Double percenttoaa = Math.floor((solplayer.getAAExperience() / Utils.getMaxAAXP()) * 100);
	    		int ipercenttolevel = percenttolevel.intValue();
	    		int ipercenttoaa = percenttoaa.intValue();
	    		player.sendMessage("Level progress: " + ipercenttolevel + "% into level - Have: " + ChatColor.GOLD+ solplayer.getExperience().longValue() + ChatColor.RESET + " Need: " + ChatColor.GOLD+ xpneededfornextlevel.longValue() + ChatColor.RESET);
	    		player.sendMessage("AA points: " + solplayer.getAAPoints());
	    		player.sendMessage("Next AA progress: " + ipercenttoaa + "% into AA - Have: " + ChatColor.GOLD+solplayer.getAAExperience().longValue() + ChatColor.RESET + " Need: " + ChatColor.GOLD+ Utils.getMaxAAXP().longValue() + ChatColor.RESET);
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
				
			} catch (CoreStateInitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
		}
		return true;
	}
}
