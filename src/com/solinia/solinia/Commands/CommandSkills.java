package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Models.SoliniaPlayerSkill;
import net.md_5.bungee.api.ChatColor;

public class CommandSkills implements CommandExecutor 
{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
            Player player = (Player) sender;
            
            try
            {
	            ISoliniaPlayer soliniaplayer = SoliniaPlayerAdapter.Adapt(player);
	            
	            if (soliniaplayer.getRace() != null)
	            	player.sendMessage(ChatColor.BLUE+soliniaplayer.getRace().getName() + ": " + 100);

	            if (soliniaplayer.getClassObj() != null)
	            {
	            	if (soliniaplayer.getClassObj().getDodgelevel() > 0)
	            	player.sendMessage(ChatColor.GRAY + "You gain the Dodge Skill at: " + soliniaplayer.getClassObj().getDodgelevel());	            	
	            	if (soliniaplayer.getClassObj().getRipostelevel() > 0)
	            	player.sendMessage(ChatColor.GRAY + "You gain the Riposte Skill at: " + soliniaplayer.getClassObj().getRipostelevel());	            	
	            	if (soliniaplayer.getClassObj().getDoubleattacklevel() > 0)
	            	player.sendMessage(ChatColor.GRAY + "You gain the Double Attack Skill at: " + soliniaplayer.getClassObj().getDoubleattacklevel());	            	
	            	if (soliniaplayer.getClassObj().getSafefalllevel() > 0)
	            	player.sendMessage(ChatColor.GRAY + "You gain the Safefall Skill at: " + soliniaplayer.getClassObj().getSafefalllevel());	            	
	            	if (soliniaplayer.getClassObj().getSpecialiselevel() > 0)
	            	player.sendMessage(ChatColor.GRAY + "You gain the Spell Specilisation Skill (/specialise) at: " + soliniaplayer.getClassObj().getSpecialiselevel());	            	
	            	if (soliniaplayer.getClassObj().getDualwieldlevel() > 0)
	            	player.sendMessage(ChatColor.GRAY + "You gain the Dual Wield Skill at: " + soliniaplayer.getClassObj().getDualwieldlevel());	            	
	            	if (soliniaplayer.getClassObj().getTrackingLevel() > 0)
	            	player.sendMessage(ChatColor.GRAY + "You gain the Tracking Skill at: " + soliniaplayer.getClassObj().getTrackingLevel());	            	
	            	if (soliniaplayer.getClassObj().getDisarmLevel() > 0)
	            	player.sendMessage(ChatColor.GRAY + "You gain the Disarm Skill at: " + soliniaplayer.getClassObj().getDisarmLevel());	            	
	            	if (soliniaplayer.getClassObj().getMakePoisonLevel() > 0)
	            	player.sendMessage(ChatColor.GRAY + "You gain the Make Poison Skill at: " + soliniaplayer.getClassObj().getMakePoisonLevel());	            	
	            	if (soliniaplayer.getClassObj().getMeditationLevel() > 0)
	            	player.sendMessage(ChatColor.GRAY + "You gain the Meditation Skill at: " + soliniaplayer.getClassObj().getMeditationLevel());	            	
	            }
	            
	            for(SoliniaPlayerSkill skill : soliniaplayer.getSkills())
	            {
	            	if (skill.getValue() == 0)
	            		continue;
	            	
	            	if (soliniaplayer.getSkillCap(skill.getSkillType()) == 0)
	            		continue;
	            	
	            	player.sendMessage(ChatColor.BLUE + skill.getSkillType().name() + ": " + skill.getValue() + "/" + soliniaplayer.getSkillCap(skill.getSkillType()));
	            }
	            
	            
            } catch (Exception e)
            {
            	e.printStackTrace();
            	player.sendMessage(e.getMessage());
            }
        }

        return true;
	}

}
