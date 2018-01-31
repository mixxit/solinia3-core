package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Models.SpellResistType;
import com.solinia.solinia.Utils.Utils;

public class CommandStats implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			try {
	            Player player = (Player) sender;
	            ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(player);
	            ISoliniaLivingEntity solentity = SoliniaLivingEntityAdapter.Adapt(player);

				player.sendMessage("Your Level: " + solplayer.getLevel());
				player.sendMessage("STR: " + solentity.getStrength() + " STA: " + solentity.getStamina() + " AGI: " + solentity.getAgility() + " DEX: " + solentity.getDexterity() + " INT: " + solentity.getIntelligence() + " WIS: " + solentity.getWisdom() + " CHA: " + solentity.getCharisma());
	            player.sendMessage("You have a maximum HP of: " + player.getMaxHealth());
	            player.sendMessage("You have a maximum MP of: " + solentity.getMaxMP());
	            player.sendMessage("You currently have a Armour Class Mitigation of: " + solentity.getMitigationAC());
	            player.sendMessage("You currently have a Attack Value of: " + solentity.getAttk());
	            player.sendMessage("You currently have a Total Rune of: " + solentity.getRune());
	            player.sendMessage("FR: " + solplayer.getResist(SpellResistType.RESIST_FIRE) + " CR: " + solplayer.getResist(SpellResistType.RESIST_COLD) + " MR: " + solplayer.getResist(SpellResistType.RESIST_MAGIC) + " PR: " + solplayer.getResist(SpellResistType.RESIST_POISON) + " DR: " + solplayer.getResist(SpellResistType.RESIST_DISEASE));

	            Double newlevel = (double) solplayer.getLevel();
	            Double xpneededforcurrentlevel = Utils.getExperienceRequirementForLevel((int) (newlevel + 0));
	    		Double xpneededfornextlevel = Utils.getExperienceRequirementForLevel((int) (newlevel + 1));
	    		Double totalxpneeded = xpneededfornextlevel - xpneededforcurrentlevel;
	    		Double currentxpprogress = solplayer.getExperience() - xpneededforcurrentlevel;
	    		
	            Double percenttolevel = Math.floor((currentxpprogress / totalxpneeded) * 100);
	            Double percenttoaa = Math.floor((solplayer.getAAExperience() / Utils.getMaxAAXP()) * 100);
	    		int ipercenttolevel = percenttolevel.intValue();
	    		int ipercenttoaa = percenttoaa.intValue();
	    		player.sendMessage("Level progress: " + ipercenttolevel + "% into level - Have: " + solplayer.getExperience().longValue() + " Need: " + xpneededfornextlevel.longValue());
	    		player.sendMessage("AA points: " + solplayer.getAAPoints());
	    		player.sendMessage("Next AA progress: " + ipercenttoaa + "% into AA - Have: " + solplayer.getAAExperience().longValue() + " Need: " + Utils.getMaxAAXP().longValue());
	    			
			} catch (CoreStateInitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
		}
		return true;
	}
}
