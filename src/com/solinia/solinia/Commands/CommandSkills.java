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
	            	player.sendMessage(ChatColor.GRAY + "You gain Dodge Skill at: " + soliniaplayer.getClassObj().getDodgelevel());	            	
	            	if (soliniaplayer.getClassObj().getRipostelevel() > 0)
	            	player.sendMessage(ChatColor.GRAY + "You gain Riposte Skill at: " + soliniaplayer.getClassObj().getRipostelevel());	            	
	            	if (soliniaplayer.getClassObj().getDoubleattacklevel() > 0)
	            	player.sendMessage(ChatColor.GRAY + "You gain Double Attack Skill at: " + soliniaplayer.getClassObj().getDoubleattacklevel());	            	
	            }
	            
	            for(SoliniaPlayerSkill skill : soliniaplayer.getSkills())
	            {
	            	player.sendMessage(ChatColor.BLUE + skill.getSkillName() + ": " + skill.getValue() + "/" + soliniaplayer.getSkillCap(skill.getSkillName()));
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
