package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaItemException;
import com.solinia.solinia.Factories.SoliniaItemFactory;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Managers.StateManager;

public class CommandSpawnRandomItem implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
			return false;

		Player player = (Player) sender;

		if (player.isOp()) {
			try {
				ISoliniaItem item = SoliniaItemFactory.GenerateRandomLoot();
				if (item != null) {
					player.getLocation().getWorld().dropItem((player).getLocation(),
							item.asItemStack());
				} else {
					player.sendMessage("Cannot find item by ID");
					return true;
				}
			} catch (CoreStateInitException | SoliniaItemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} else {
			player.sendMessage("This is an OP only command");
			return false;
		}

		return true;
	}
}
