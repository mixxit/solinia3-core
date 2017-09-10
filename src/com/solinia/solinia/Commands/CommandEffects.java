package com.solinia.solinia.Commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaActiveSpellEffect;
import com.solinia.solinia.Models.SoliniaEntitySpellEffects;

public class CommandEffects implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (sender instanceof Player) {
            Player player = (Player) sender;
            
            try
            {
	            SoliniaEntitySpellEffects effects = StateManager.getInstance().getEntityManager().getActiveEntityEffects(player);
	            
	            if (effects == null)
	            	return true;
	            
	            player.sendMessage("Active Spell Effects on you:");
	            
	            for(SoliniaActiveSpellEffect effect : effects.getActiveSpell())
	            {
	            	ISoliniaSpell spell = StateManager.getInstance().getConfigurationManager().getSpell(effect.getSpellId());
	            	player.sendMessage("- Spell: " + spell.getName() + effect.getTicksLeft() + " ticks left");
	            }
	            
            } catch (CoreStateInitException e)
            {
            	player.sendMessage(e.getMessage());
            }
        }

        return true;
	}

}
