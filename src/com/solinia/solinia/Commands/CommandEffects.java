package com.solinia.solinia.Commands;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Solinia3CorePlugin;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaActiveSpell;
import com.solinia.solinia.Models.SoliniaEntitySpells;
import com.solinia.solinia.Utils.TimeUtils;
import com.solinia.solinia.Utils.Utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class CommandEffects implements CommandExecutor {
	Solinia3CorePlugin plugin;

	public CommandEffects(Solinia3CorePlugin plugin) {
		this.plugin = plugin;
	}
	
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
			    		LocalDateTime datetime = LocalDateTime.now();
			    		Timestamp nowtimestamp = Timestamp.valueOf(datetime);
	        			Timestamp expiretimestamp = solplayer.getExperienceBonusExpires();

	        			if (expiretimestamp != null)
	        			{
	        				if (!nowtimestamp.after(expiretimestamp))
	        				{
	        					int secondsleft = (int)Math.floor(TimeUtils.compareTwoTimeStamps(expiretimestamp,nowtimestamp));
	        					TextComponent tc = new TextComponent();
	        					tc.setText("- " + ChatColor.GREEN + "100% Experience Potion" + ChatColor.RESET + " " + secondsleft + " seconds");
	        					sender.spigot().sendMessage(tc);
	        				}
	        			}
	            	} else {
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
		            	
		            	String requiresType = "";
		            	if (activeSpell.getRequiredWeaponSkillType() != null && activeSpell.getRequiredWeaponSkillType().equals(""))
		            		requiresType = "Requires Weapon: " + requiresType + " ";
		            	
		            	TextComponent tc = new TextComponent();
						tc.setText("- " + spellcolor + spell.getName() + ChatColor.RESET + " " + activeSpell.getTicksLeft() + " ticks left " + requiresType + "  - ");
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

	            	if (!spell.isBeneficial())
	            	{
	            		player.sendMessage("Can only remove beneficial spells");
	            		return true;
	            	}

	            	StateManager.getInstance().getEntityManager().removeSpellEffectsOfSpellId(player.getUniqueId(), spell.getId(), true, true);
	            	
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
