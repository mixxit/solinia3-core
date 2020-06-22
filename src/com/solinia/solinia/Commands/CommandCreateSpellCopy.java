package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Factories.SoliniaSpellFactory;
import com.solinia.solinia.Managers.StateManager;

public class CommandCreateSpellCopy implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;
		
		if (!sender.isOp() && !sender.hasPermission("solinia.createspellcopy"))
		{
			sender.sendMessage("You do not have permission to access this command");
			return false;
		}
		
		// Args
		// Level
		// NPC Name
		
		if (args.length < 2)
		{
			sender.sendMessage("Insufficient arguments: sourcespellid spell_name");
			return false;
		}
		
		int spellid = Integer.parseInt(args[0]);
		if (spellid < 1)
		{
			sender.sendMessage("Invalid id");
			return false;
		}
		
		try {
			if (StateManager.getInstance().getConfigurationManager().getSpell(spellid) == null)
			{
				sender.sendMessage("Cannot locate id: " + spellid);
				return false;
			}
			
			String name = "";
			int i = 0;
			for(String element : args)
			{
				if (i <= 0)
				{
					i++;
					continue;
				}
				
				name += element;
				i++;
			}
			
			if (name.equals(""))
			{
				sender.sendMessage("Name of spell cannot be null");
				return false;
			}
			
			name = name.replace(" ", "_");
		
			SoliniaSpellFactory.CreateSpellCopy(spellid,name);
		} catch (Exception e) {
			sender.sendMessage(e.getMessage());
		}
		return true;
	}

}
