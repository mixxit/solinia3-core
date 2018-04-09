package com.solinia.solinia.Commands;

import java.util.Map.Entry;

import org.bukkit.Material;
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
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Utils.Utils;

import net.md_5.bungee.api.ChatColor;

public class CommandReagent implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		try
		{
			Player player = (Player)sender;
			ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
			player.sendMessage("Reagent Pouch:");
			for(Entry<Integer, Integer> keyValuePair : solPlayer.getReagents().entrySet())
			{
				int itemId = keyValuePair.getKey();
				int count = keyValuePair.getValue();
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(itemId);
				
				if (item != null && item.isReagent())
					player.sendMessage(item.getDisplayname() + " Qty: " + ChatColor.GOLD + count + ChatColor.RESET);
			}
			
			if (args.length > 0)
			{
				String command = args[0];
				switch (command)
				{
					case "add":
						ItemStack itemstack = null;
						itemstack = player.getInventory().getItemInMainHand();
						if (itemstack.getType().equals(Material.AIR))
			            {
			            	player.sendMessage("The item in your main hand is not a reagent");
			    			return true;
			            }
						if (!Utils.IsSoliniaItem(itemstack))
			            {
			            	player.sendMessage("The item in your main hand is not a reagent");
			    			return true;
			            }
			        	
			            ISoliniaItem item = SoliniaItemAdapter.Adapt(itemstack);
			            
				        if (!item.isReagent())
				        {
				        	player.sendMessage("The item in your main hand is not a reagent");
				        	return true;
				        }
				        
				        int currentAmount = 0;
				        if (solPlayer.getReagents().get(item.getId()) != null)
				        {
				        	currentAmount = solPlayer.getReagents().get(item.getId());
				        }
				        solPlayer.getReagents().put(item.getId(),currentAmount + itemstack.getAmount());
				        
				        player.sendMessage("Item added to your reagent pouch");
		    	        player.getInventory().setItemInMainHand(null);
		    	        player.updateInventory();
				        
				        break;
					default:
						break;
				}
			}
			
			
			player.sendMessage("Sub Commands: /reagent add");
			
		} catch (CoreStateInitException e)
		{
			
		} catch (SoliniaItemException e) {
			Player player = (Player)sender;
			player.sendMessage("Unknown item");
		}
		
		return true;
	}
}
