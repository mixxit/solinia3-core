package com.solinia.solinia.Commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaItemException;
import com.solinia.solinia.Factories.SoliniaItemFactory;
import com.solinia.solinia.Factories.SoliniaLootFactory;
import com.solinia.solinia.Interfaces.ISoliniaClass;
import com.solinia.solinia.Interfaces.ISoliniaLootDrop;
import com.solinia.solinia.Managers.StateManager;

public class CommandCreateAllArmourSets implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;

		try {
			if (!sender.isOp() && !sender.hasPermission("solinia.createallarmorsets"))
			{
				sender.sendMessage("You do not have permission to access this command");
				return false;
			}
			
			if (args.length < 4) {
				return false;
			}

			// args
			// lootdropid
			// armourtier
			// chance
			// suffixname

			int lootdropid = Integer.parseInt(args[0]);
			int armourtier = Integer.parseInt(args[1]);
			ISoliniaLootDrop lootdrop = StateManager.getInstance().getConfigurationManager().getLootDrop(lootdropid);
			if (lootdrop == null) {
				sender.sendMessage("Lootdrop ID does not exist");
				return true;
			}
			
			int chance = Integer.parseInt(args[2]);
			if (chance < 1 || chance > 100)
			{
				sender.sendMessage("Chance must be between 1 and 100");
				return true;
			}
			
			boolean isprefix = Boolean.parseBoolean(args[3]);
			
			String partialname = "";
			int count = 0;
			for (String entry : args) {
				if (count < 4) {
					count++;
					continue;
				}

				partialname += entry + " ";
				count++;
			}
			
			partialname = partialname.trim();

			if (partialname.equals("")) {
				sender.sendMessage("Blank prefix/suffix name not allowed when creating armour set");
				return false;
			}
			
			int itemscreated = 0;
			for(ISoliniaClass classEntry : StateManager.getInstance().getConfigurationManager().getClasses())
			{
				List<Integer> items = SoliniaItemFactory.CreateClassItemSet(classEntry, armourtier, partialname, true,"", isprefix);
				for (Integer item : items) {
					SoliniaLootFactory.CreateLootDropItem(lootdropid, item, 1, false, chance);
					itemscreated++;
				}
			}
			sender.sendMessage("Added item count " + itemscreated + " with " + chance + "% chance in lootdrop: " + lootdropid);
		} catch (CoreStateInitException e) {
			sender.sendMessage(e.getMessage());
		} catch (SoliniaItemException e) {
			// TODO Auto-generated catch block
			sender.sendMessage(e.getMessage());
		}
		return true;
	}
}
