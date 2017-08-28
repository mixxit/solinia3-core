package com.solinia.solinia.Commands;

import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.solinia.solinia.Factories.SoliniaItemFactory;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Managers.StateManager;

public class CommandRebuildSpellItems implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof ConsoleCommandSender))
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
				// Spell exists
				if (StateManager.getInstance().getConfigurationManager().getSpellItem(spell.getId()).size() > 0)
				{
					for(ISoliniaItem item : StateManager.getInstance().getConfigurationManager().getSpellItem(spell.getId()))
					{
						item.setAbilityid(spell.getId());
						item.setDisplayname("Spell: " + spell.getName());
						item.setSpellscroll(true);
						item.setAllowedClassNames(new ArrayList<String>());
						item.setLore("This appears to be some sort of magical spell that could be learned");
						StateManager.getInstance().getConfigurationManager().updateItem(item);
						updated++;
					}
				} else {
					// Doesnt exist, create it
					ISoliniaItem item = SoliniaItemFactory.CreateItem(new ItemStack(Material.ENCHANTED_BOOK));
					item.setAbilityid(spell.getId());
					item.setDisplayname("Spell: " + spell.getName());
					item.setSpellscroll(true);
					item.setAllowedClassNames(new ArrayList<String>());
					item.setLore("This appears to be some sort of magical spell that could be learned");
					StateManager.getInstance().getConfigurationManager().updateItem(item);
					updated++;
				}
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
