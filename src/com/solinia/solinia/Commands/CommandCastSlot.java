package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.solinia.solinia.Adapters.SoliniaItemAdapter;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaItemException;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;

import net.minecraft.server.v1_13_R1.Material;

public class CommandCastSlot implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
			return false;

		Player player = (Player) sender;
		
		if (args.length == 0)
			return false;
		
		int hotBar = Integer.parseInt(args[0]);
		if (hotBar < 0 || hotBar > 9)
			return false;
		
		// Check item in slot is a spell
		try
		{
			int slotId = hotBarToSlotId(hotBar);
			ItemStack item = player.getInventory().getItem(slotId);
			if (item == null || item.getType().equals(Material.AIR))
			{
				player.sendMessage("Item in slot: " + hotBar + " is not a spell");
				return true;
			}
			
			ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
			if (solPlayer == null)
				return false;
			
			try {
				ISoliniaItem solItem = SoliniaItemAdapter.Adapt(item);
				if (solItem == null || !solItem.isSpellscroll())
				{
					player.sendMessage("Item in slot: " + hotBar + " is not a spell");
					return true;
				}
				
				solPlayer.tryCastFromItemInSlot(slotId);
			} catch (SoliniaItemException e) {
				player.sendMessage("Item in slot: " + hotBar + " is not a spell");
				return true;
			}
		} catch (CoreStateInitException e)
		{
			return true;
		}
		return true;
	}

	private int hotBarToSlotId(int hotBar) {
		switch(hotBar)
		{
			case 0:
				return 9;
			case 1:
				return 0;
			case 2:
				return 1;
			case 3:
				return 2;
			case 4:
				return 3;
			case 5:
				return 4;
			case 6:
				return 5;
			case 7:
				return 6;
			case 8:
				return 7;
			case 9:
				return 8;
		}
		return 0;
	}
}
