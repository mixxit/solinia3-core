package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaActiveSpellEffect;
import com.solinia.solinia.Models.SoliniaEntitySpellEffects;

import net.md_5.bungee.api.ChatColor;

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
	            
	            if (args.length == 0)
	            {
		            player.sendMessage(ChatColor.GOLD + "Active Spell Effects on you:" + ChatColor.WHITE);
		            
		            for(SoliniaActiveSpellEffect effect : effects.getActiveSpells())
		            {
		            	ISoliniaSpell spell = StateManager.getInstance().getConfigurationManager().getSpell(effect.getSpellId());
		            	String removetext = "";
		            	ChatColor spellcolor = ChatColor.GREEN;
		            	if (spell.isBeneficial())
		            	{
		            		removetext = "/effects remove " + spell.getId();
		            	} else {
		            		removetext = "Unremovable spell";
		            		spellcolor = ChatColor.RED;
		            	}
		            	
		            	player.sendMessage("- Spell: " + spellcolor + spell.getName() + ChatColor.RESET + " " + effect.getTicksLeft() + " ticks left - " + removetext);
		            }
	            } else {
	            	int spellid = Integer.parseInt(args[0]);
	            	ISoliniaSpell spell = StateManager.getInstance().getConfigurationManager().getSpell(spellid);
	            	if (spell == null)
	            	{
	            		player.sendMessage("That spell does not exist");
	            		return true;
	            	}
	            	
	            	StateManager.getInstance().getEntityManager().removeSpellEffectsOfSpellId(player.getUniqueId(), spell.getId());
	            	
	            	if (!spell.isBeneficial())
	            	{
	            		player.sendMessage("Can only remove beneficial spells");
	            		return true;
	            	}
	            	
	            	player.sendMessage("Spell Effect removed");
	            }
	            
            } catch (CoreStateInitException e)
            {
            	player.sendMessage(e.getMessage());
            }
        }

        return true;
	}

}
