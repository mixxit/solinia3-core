package com.solinia.solinia.Commands;

import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.solinia.solinia.Factories.SoliniaItemFactory;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaSpellClass;

public class CommandRebuildSpellItems implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
		{
			sender.sendMessage("This is a Player/Console only command");
			return false;
		}
		
		if (sender instanceof Player)
		{
			Player player = (Player)sender;
			if (!player.isOp())
			{
				player.sendMessage("This is an operator only command");
				return true;
			}
		}
		
		int updated = 0;
		
		sender.sendMessage("Rebuilding Item Lists, this may take some time");
		try
		{
			for(ISoliniaSpell spell : StateManager.getInstance().getConfigurationManager().getSpells())
			{
				int worth = 10;
				int minLevel = 1;
				int lowestLevel = 1000;
				
				// If no class defined rip it from the classes columns
				if (spell.getAllowedClasses().size() == 0)
				{
					try
					{
						if (spell.getSoliniaSpellClassesFromClassesData().size() > 0)
						{
							spell.setAllowedClasses(spell.getSoliniaSpellClassesFromClassesData());
						}
					} catch (Exception e)
					{
						System.out.println("Problem parsing classes data");
					}
				}
				
				// If we get this far and the spell is classes are still empty they are AA spells
				if (spell.getAllowedClasses().size() == 0)
				{
					try
					{
						if (spell.getSoliniaSpellClassesFromClassesAAData().size() > 0)
						{
							spell.setAllowedClasses(spell.getSoliniaSpellClassesFromClassesAAData());
						}
					} catch (Exception e)
					{
						System.out.println("Problem parsing classes AA data");
					}
				}
				
				// Last pass, check if all allowed class levels are set to 255, if so attempt a rebuild on AA
				boolean anyClassLevelBelow255 = false;
				for (SoliniaSpellClass spellClass : spell.getAllowedClasses())
				{
					if (spellClass.getMinlevel() < 255)
					{
						anyClassLevelBelow255 = true;
					}
				}
				
				if (anyClassLevelBelow255 == false)
				{
					try
					{
						if (spell.getSoliniaSpellClassesFromClassesAAData().size() > 0)
						{
							spell.setAllowedClasses(spell.getSoliniaSpellClassesFromClassesAAData());
						}
					} catch (Exception e)
					{
						System.out.println("Problem parsing classes AA data");
					}
				}
				
				
				for(SoliniaSpellClass spellClass : spell.getAllowedClasses())
				{
					if (spellClass.getMinlevel() < lowestLevel)
						lowestLevel = spellClass.getMinlevel();
				}
				
				if (lowestLevel > minLevel && lowestLevel < 100)
					minLevel = lowestLevel;
				
				worth = worth * minLevel;
				
				// Spell exists
				if (StateManager.getInstance().getConfigurationManager().getSpellItem(spell.getId()).size() > 0)
				{
					for(ISoliniaItem item : StateManager.getInstance().getConfigurationManager().getSpellItem(spell.getId()))
					{
						item.setAbilityid(spell.getId());
						item.setDisplayname("Spell: " + spell.getName());
						item.setSpellscroll(true);
						item.setAllowedClassNames(new ArrayList<String>());
						item.setLore("This appears to be some sort of   magical spell that could be       learned");
						item.setWorth(worth);
						StateManager.getInstance().getConfigurationManager().updateItem(item);
						updated++;
					}
				} else {
					// Doesnt exist, create it
					ISoliniaItem item = SoliniaItemFactory.CreateItem(new ItemStack(Material.ENCHANTED_BOOK),sender.isOp());
					item.setAbilityid(spell.getId());
					item.setDisplayname("Spell: " + spell.getName());
					item.setSpellscroll(true);
					item.setAllowedClassNames(new ArrayList<String>());
					item.setLore("This appears to be some sort of   magical spell that could be       learned");
					item.setWorth(worth);
					StateManager.getInstance().getConfigurationManager().updateItem(item);
					updated++;
				}
				
				StateManager.getInstance().getConfigurationManager().setSpellsChanged(true);
				StateManager.getInstance().getConfigurationManager().setItemsChanged(true);
			}
			
		} catch (Exception e)
		{
			e.printStackTrace();
			sender.sendMessage(e.getMessage());
		}
		
		sender.sendMessage("Updated " + updated + " items");
		return true;
	}
}
