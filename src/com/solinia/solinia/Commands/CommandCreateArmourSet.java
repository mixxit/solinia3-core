package com.solinia.solinia.Commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaItemException;
import com.solinia.solinia.Factories.SoliniaItemFactory;
import com.solinia.solinia.Interfaces.ISoliniaClass;
import com.solinia.solinia.Managers.StateManager;

public class CommandCreateArmourSet implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;

		try {
			if (sender instanceof Player) {
				Player player = (Player) sender;

				if (!player.isOp()) {
					player.sendMessage("This is an operator only command");
					return false;
				}
			}

			if (args.length < 2) {
				return false;
			}

			// args
			// classid
			// armourtier
			// suffixname

			int classid = Integer.parseInt(args[0]);
			int armourtier = Integer.parseInt(args[1]);
			ISoliniaClass classtype = StateManager.getInstance().getConfigurationManager().getClassObj(classid);
			if (classtype == null) {
				sender.sendMessage("Class ID does not exist");
				return true;
			}

			String partialname = "";
			int count = 0;
			for (String entry : args) {
				if (count < 2) {
					count++;
					continue;
				}

				partialname += entry + " ";
				count++;
			}
			
			partialname = partialname.trim();

			if (partialname.equals("")) {
				sender.sendMessage("Blank suffix name not allowed when creating armour set");
				return false;
			}

			List<Integer> items = SoliniaItemFactory.CreateClassItemSet(classtype, armourtier, partialname, false, sender.isOp());
			String itemscreated = "";
			for (Integer item : items) {
				itemscreated += item + " ";
			}
			sender.sendMessage("Created items as IDs: " + itemscreated);
		} catch (CoreStateInitException e) {
			sender.sendMessage(e.getMessage());
		} catch (SoliniaItemException e) {
			// TODO Auto-generated catch block
			sender.sendMessage(e.getMessage());
		}
		return true;
	}
}
