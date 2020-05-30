package com.solinia.solinia.Commands;

import com.solinia.solinia.Utils.SkillUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Models.SkillType;
import com.solinia.solinia.Models.SoliniaPlayerSkill;

public class CommandSetLanguage implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
            Player player = (Player) sender;
            
			try {
				ISoliniaPlayer soliniaplayer = SoliniaPlayerAdapter.Adapt(player);
	            if (soliniaplayer.getRace() == null)
	            {
	            	player.sendMessage("You cannot set your language until you /setrace");
	            	return true;
	            }
	            
	            if (args.length == 0)
	            {
	                // always has 100% skill on own race
	                player.sendMessage(ChatColor.GRAY + "Native Languages: ");
	                player.sendMessage(ChatColor.BLUE + soliniaplayer.getRace().getLanguage().name().toUpperCase() + ": " + 100);
	                player.sendMessage(ChatColor.GRAY + "See /skills for other languages you have learned");
	                player.sendMessage(ChatColor.GRAY + "To set your language use /language languagename");
	                
	            	return false;
	            }
	            
	            String language = args[0].toUpperCase();
	            SkillType targetLanguage = SkillUtils.getSkillType2(language);
	            if (!SkillUtils.IsValidLanguage(targetLanguage))
	            {
	            	player.sendMessage("That is not a valid tongue.");
	            	return false;
	            }
	            if (targetLanguage.equals(SkillType.None))
	            {
	            	player.sendMessage("That is not a valid tongue.");
	            	return false;
	            }
	            
	            if (soliniaplayer.getLanguageSkillType().equals(targetLanguage))
	            {
	            	player.sendMessage("That is already your current tongue.");
	            	return false;
	            }
	            
	            if (targetLanguage.equals(soliniaplayer.getRace().getLanguage()))
	            {
	            	soliniaplayer.setLanguageSkillType(targetLanguage);
	                player.sendMessage("* You will now speak in " + targetLanguage.name().toUpperCase());
	                return true;
	            }
	            
	            SoliniaPlayerSkill soliniaskill = soliniaplayer.getSkill(targetLanguage);
	            if (soliniaskill != null && soliniaskill.getValue() >= 100)
	            {
	            	soliniaplayer.setLanguageSkillType(targetLanguage);
	            	player.sendMessage("* You will now speak in " + targetLanguage.name().toUpperCase());
	                return true;
	            }
	                        
	            player.sendMessage("Language change failed. Default for you is /language " + soliniaplayer.getRace().getLanguage().name().toUpperCase() + " or any other language you have mastered to 100 in /skills");
	        	return false;
			} catch (CoreStateInitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
        }

        return true;
	}

}
