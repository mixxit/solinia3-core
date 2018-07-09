package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Managers.StateManager;

public class CommandSpawnItem implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
			return false;

		Player player = (Player) sender;

		if (args.length < 1) {
			player.sendMessage("Insufficient arguments (itemid) (count)");
			return false;
		}
		
		int count = 1;
		
		if (args.length == 2)
		{
			count = Integer.parseInt(args[1]);
		}

		if (player.isOp() || player.hasPermission("solinia.spawnitem")) {
			int itemid = Integer.parseInt(args[0]);
			try {
				for (int i = 0; i < count; i++)
				{
					ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(itemid);
					if (item != null) {
						player.getLocation().getWorld().dropItem((player).getLocation(),
								item.asItemStack());
					} else {
						player.sendMessage("Cannot find item by ID");
						return true;
					}
				}
			} catch (CoreStateInitException e) {
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
