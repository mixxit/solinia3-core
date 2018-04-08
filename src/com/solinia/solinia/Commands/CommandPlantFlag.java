package com.solinia.solinia.Commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.solinia.solinia.Adapters.SoliniaItemAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaItemException;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Utils.Utils;

public class CommandPlantFlag implements CommandExecutor  {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
			return false;
				
		ItemStack itemstack = null;
		Player player = (Player)sender;
		itemstack = player.getInventory().getItemInMainHand();
        if (itemstack.getType().equals(Material.AIR))
        {
        	player.sendMessage("The item in your main hand is not a territory flag");
        	return false;
        }
        
        if (!Utils.IsSoliniaItem(itemstack))
        {
        	player.sendMessage("The item in your main hand is not a territory flag");
        	return true;
        }
        
        try
        {
            ISoliniaItem item = SoliniaItemAdapter.Adapt(itemstack);
            
	        if (!item.isTerritoryFlag())
	        {
	        	player.sendMessage("The item in your main hand is not a territory flag");
	        	return true;
	        }
		
        
	        return true;
        } catch (CoreStateInitException e)
        {
        	
        } catch (SoliniaItemException e) {

		}

		return true;
	}
}
