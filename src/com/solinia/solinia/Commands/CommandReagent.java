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
import com.solinia.solinia.Models.SoliniaReagent;
import com.solinia.solinia.Utils.ItemStackUtils;
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
			
			if (args.length == 0)
			{
				sendReagentPouch(solPlayer);
			}
			
			
			if (args.length > 0)
			{
				String command = args[0];
				switch (command)
				{
					case "addcursor":
						performReagentAdd(solPlayer, true);
					return true;
					case "add":
						performReagentAdd(solPlayer, false);
				        break;
					default:
						break;
				}
			}
			
			
			player.sendMessage("Sub Commands: /reagent add");
			
		} catch (CoreStateInitException e)
		{
			
		}
		
		return true;
	}

	private void performReagentAdd(ISoliniaPlayer solplayer, boolean addByCursor) {
		try
		{
			ItemStack itemstack = null;
			itemstack = solplayer.getBukkitPlayer().getInventory().getItemInMainHand();
			if (addByCursor)
			{
				itemstack = solplayer.getBukkitPlayer().getItemOnCursor();
			}
			
			if (itemstack.getType() == null || itemstack.getType().equals(Material.AIR))
	        {
				if (!addByCursor)
				{
					solplayer.getBukkitPlayer().sendMessage(ChatColor.GRAY
						+ "The item you wish to add your reagents must be in your primary hand");
				} else {
					solplayer.getBukkitPlayer().sendMessage(ChatColor.GRAY
							+ "The item you wish to add your reagents must be in your cursor");
				}
				return;
	        }
			if (!ItemStackUtils.IsSoliniaItem(itemstack))
	        {
				solplayer.getBukkitPlayer().sendMessage("The item is not a reagent");
				return;
	        }
	    	
	        ISoliniaItem item = SoliniaItemAdapter.Adapt(itemstack);
	        
	        if (!item.isReagent())
	        {
	        	solplayer.getBukkitPlayer().sendMessage("The item is not a reagent");
	        	return;
	        }
	        
	        if (solplayer.getReagents().get(item.getId()) == null)
	        {
	        	solplayer.getReagents().put(item.getId(), new SoliniaReagent(item.getId(), 0, StateManager.getInstance().getInstanceGuid()));
	        }
	        
	        solplayer.getReagents().get(item.getId()).addQty(itemstack.getAmount());
	        
	        solplayer.getBukkitPlayer().sendMessage("Item added to your reagent pouch");
	        if (!addByCursor)
	        	solplayer.getBukkitPlayer().getInventory().setItemInMainHand(null);
	    		else
	    			solplayer.getBukkitPlayer().setItemOnCursor(null);
	        solplayer.getBukkitPlayer().updateInventory();
		} catch (CoreStateInitException | SoliniaItemException e)
		{
			
		}
        
	}

	private void sendReagentPouch(ISoliniaPlayer solPlayer) {
		try
		{
			solPlayer.getBukkitPlayer().sendMessage("Reagent Pouch:");
			for(Entry<Integer, SoliniaReagent> keyValuePair : solPlayer.getReagents().entrySet())
			{
				int itemId = keyValuePair.getKey();
				int count = keyValuePair.getValue().getQty();
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(itemId);
				
				if (item != null && item.isReagent())
					solPlayer.getBukkitPlayer().sendMessage(item.getDisplayname() + " Qty: " + ChatColor.GOLD + count + ChatColor.RESET);
			}
		} catch (CoreStateInitException e)
		{
			
		}
	}
}
