package com.solinia.solinia.Commands;

import java.sql.Timestamp;
import java.util.Calendar;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaActiveSpell;
import com.solinia.solinia.Models.SoliniaEntitySpells;
import com.solinia.solinia.Utils.Utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class CommandEffects implements CommandExecutor {

	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (sender instanceof Player) {
            Player player = (Player) sender;
            
            try
            {
	            SoliniaEntitySpells spells = StateManager.getInstance().getEntityManager().getActiveEntitySpells(player);
	            
	            if (spells == null)
	            	return true;
	            
	            if (args.length == 0)
	            {
		            player.sendMessage(ChatColor.GOLD + "Active Spell Effects on you:" + ChatColor.WHITE);
		            
		            ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(player);
	            	if (solplayer.getExperienceBonusExpires() != null)
	            	{
	            		Calendar calendar = Calendar.getInstance();
	        			java.util.Date now = calendar.getTime();
	        			Timestamp nowtimestamp = new Timestamp(now.getTime());
	        			Timestamp expiretimestamp = solplayer.getExperienceBonusExpires();

	        			if (expiretimestamp != null)
	        			{
	        				if (!nowtimestamp.after(expiretimestamp))
	        				{
	        					int secondsleft = (int)Math.floor(Utils.compareTwoTimeStamps(expiretimestamp,nowtimestamp));
	        					TextComponent tc = new TextComponent();
	        					tc.setText("- " + ChatColor.GREEN + "100% Experience Potion" + ChatColor.RESET + " " + secondsleft + " seconds");
	        					sender.spigot().sendMessage(tc);
	        				}
	        			}
	            	}
		            
		            for(SoliniaActiveSpell activeSpell : spells.getActiveSpells())
		            {
		            	ISoliniaSpell spell = StateManager.getInstance().getConfigurationManager().getSpell(activeSpell.getSpellId());
		            	String removetext = "";
		            	ChatColor spellcolor = ChatColor.GREEN;
		            	if (spell.isBeneficial())
		            	{
		            		removetext = "/effects remove " + spell.getId();
		            	} else {
		            		removetext = "Unremovable spell";
		            		spellcolor = ChatColor.RED;
		            	}
		            	
		            	TextComponent tc = new TextComponent();
						tc.setText("- " + spellcolor + spell.getName() + ChatColor.RESET + " " + activeSpell.getTicksLeft() + " ticks left - ");
						TextComponent tc2 = new TextComponent();
						tc2.setText(removetext);
						if (spell.isBeneficial())
						{
							tc2.setText(ChatColor.GRAY + "Click here to remove" + ChatColor.RESET);
							tc2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, removetext));
						}
						tc.addExtra(tc2);
						sender.spigot().sendMessage(tc);	
		            	
		            }
	            } else {
	            	if (args.length < 2)
	            		return true;
	            	
	            	int spellid = Integer.parseInt(args[1]);
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
