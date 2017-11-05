package com.solinia.solinia.Timers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.solinia.solinia.Adapters.SoliniaItemAdapter;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaItemException;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;

import net.md_5.bungee.api.ChatColor;

public class PlayerInventoryValidatorTimer extends BukkitRunnable {

	@Override
	public void run() {

		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			validatePlayerItems(player);
		}
	}

	private void validatePlayerItems(Player player) {
		try
		{
		
			ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(player);
			
			List<Integer> slots = new ArrayList<Integer>();
			slots.add(36);
			slots.add(37);
			slots.add(38);
			slots.add(39);
			slots.add(40);
			
			for (int slotId = 0; slotId < 36; slotId++)
			{
				if (player.getInventory().getItem(slotId) == null)
					continue;
				
				try
				{
					ISoliniaItem i = SoliniaItemAdapter.Adapt(player.getInventory().getItem(slotId));
					
					if (i.isTemporary())
					{
						boolean foundFadedItem = false;
						for(String loreLine : player.getInventory().getItem(slotId).getItemMeta().getLore())
						{
							if (!loreLine.startsWith("Temporary: "))
								continue;
							
							if (!loreLine.equals("Temporary: " + StateManager.getInstance().getInstanceGuid()))
							{
								// Delete temporary item
								player.sendMessage("Your temporary item has faded from existence");
								player.getInventory().setItem(slotId, null);
								player.updateInventory();
								break;
							}
						}
						
						if (foundFadedItem)
							continue;
					}
					
					// Only monitor the defined slots
					if (!slots.contains(slotId))
						continue;
					
					if (i.getAllowedClassNames().size() < 1)
						continue;
					
					if (solplayer.getClassObj() == null)
					{
						player.getWorld().dropItemNaturally(player.getLocation(), player.getInventory().getItem(slotId));
						player.getInventory().setItem(slotId, null);
						player.updateInventory();
						player.sendMessage(ChatColor.GRAY + "You cannot wear " + i.getDisplayname() + " so it has been dropped");
						continue;
					}
					
					if (!i.getAllowedClassNames().contains(solplayer.getClassObj().getName().toUpperCase()))
					{
						player.getWorld().dropItemNaturally(player.getLocation(), player.getInventory().getItem(slotId));
						player.getInventory().setItem(slotId, null);
						player.updateInventory();
						player.sendMessage(ChatColor.GRAY + "You cannot wear " + i.getDisplayname() + " so it has been dropped");
						continue;
					}
					
					if (i.getMinLevel() > solplayer.getLevel())
		    		{
						player.getWorld().dropItemNaturally(player.getLocation(), player.getInventory().getItem(slotId));
						player.getInventory().setItem(slotId, null);
						player.updateInventory();
						player.sendMessage(ChatColor.GRAY + "You cannot wear " + i.getDisplayname() + " so it has been dropped");
						continue;
		    		}
					
				} catch (SoliniaItemException e) {
					continue;
				}
			}
		
		} catch (CoreStateInitException e)
		{
			// try next loop
			return;
		}
	}
}
