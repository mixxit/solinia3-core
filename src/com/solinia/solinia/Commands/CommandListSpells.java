package com.solinia.solinia.Commands;

import com.solinia.solinia.Utils.ChatUtils;
import com.solinia.solinia.Utils.SpellUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaSpell;
import com.solinia.solinia.Models.SpellEffectType;
import com.solinia.solinia.Utils.Utils;

import net.md_5.bungee.api.ChatColor;

public class CommandListSpells implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;
		
		if (!sender.isOp() && !sender.hasPermission("solinia.listspells"))
		{
			sender.sendMessage("You do not have permission to access this command");
			return false;
		}
		
		if (args.length == 0)
		{
			sender.sendMessage("You must provide some text to filter the spells by");
			return true;
		}
		
		if (args.length > 0 && args[0].equals(".criteria"))
		{
			try {
				ChatUtils.sendFilterByCriteria(StateManager.getInstance().getConfigurationManager().getSpells(), sender, args,SoliniaSpell.class);
			return true;
			} catch (CoreStateInitException e) {
				// TODO Auto-generated catch block
				sender.sendMessage(e.getMessage());
				e.printStackTrace();
			}
		}
		
		if (args.length > 0 && args[0].equals(".effectid"))
		{
			sender.sendMessage("Seeking spells by effect");
			try {
				SpellEffectType type = SpellUtils.getSpellEffectType(Integer.parseInt(args[1]));
				for(ISoliniaSpell spell : StateManager.getInstance().getConfigurationManager().getSpells())
				{
					if (spell.isEffectInSpell(type))
					{
						String spellmessage = "" + ChatColor.GOLD + spell.getId() + ChatColor.RESET + " - " + spell.getName();
						sender.sendMessage(spellmessage);
					}
				}
				sender.sendMessage("Done");
			return true;
			} catch (CoreStateInitException e) {
				// TODO Auto-generated catch block
				sender.sendMessage(e.getMessage());
				e.printStackTrace();
			}
		}
		
		// Filter for name
		
		int found = 0;
		try {
			
			for(ISoliniaSpell spell : StateManager.getInstance().getConfigurationManager().getSpells())
			{
				if (spell.getName().toUpperCase().contains(StringUtils.join(args, " ").toUpperCase()))
				{
					found++;
					String spellmessage = "" + ChatColor.GOLD + spell.getId() + ChatColor.RESET + " - " + spell.getName();
					sender.sendMessage(spellmessage);
				}
			}
			
			if (found == 0)
			{
				sender.sendMessage("Spell could not be located by search string");
			}
			
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			sender.sendMessage(e.getMessage());
			e.printStackTrace();
		}
		
		return true;
	}
}
