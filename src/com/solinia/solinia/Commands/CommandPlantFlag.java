package com.solinia.solinia.Commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.solinia.solinia.Adapters.SoliniaChunkAdapter;
import com.solinia.solinia.Adapters.SoliniaItemAdapter;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaChunkCreationException;
import com.solinia.solinia.Exceptions.SoliniaItemException;
import com.solinia.solinia.Factories.SoliniaAlignmentChunkFactory;
import com.solinia.solinia.Interfaces.ISoliniaAlignment;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaChunk;
import com.solinia.solinia.Utils.Utils;

public class CommandPlantFlag implements CommandExecutor  {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
			return false;
				
		ItemStack itemstack = null;
		Player player = (Player)sender;
		
        try
        {
        	ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
    		
    		if (solPlayer.getRace() == null)
    		{
    			player.sendMessage("You must have set your race to plant a flag");
    			return true;
    		}
    		
    		String playeralignment = solPlayer.getRace().getAlignment();
    		
    		SoliniaChunk chunk = SoliniaChunkAdapter.Adapt(player.getWorld().getChunkAt(player.getLocation()));
			if (chunk == null)
			{
				player.sendMessage("Territory is not currently available for planting a flag!");
				return true;
			}
			
			ISoliniaAlignment chunkAlignment = chunk.getChunkAlignment();
			
			if (chunkAlignment != null)
			{
				player.sendMessage("Territory is already claimed! (see /trader)");
				return true;
			}
			
			ISoliniaAlignment alignment = StateManager.getInstance().getConfigurationManager().getAlignment(playeralignment.toUpperCase());
			
			if (alignment == null)
			{
				player.sendMessage("Could not find your alignment!");
				return true;
			}
    		
    		itemstack = player.getInventory().getItemInMainHand();
    		
            if (itemstack.getType().equals(Material.AIR))
            {
            	player.sendMessage("The item in your main hand is not a territory flag");
    			return true;
            }
            
            if (itemstack.getAmount() != 1)
            {
            	player.sendMessage("You can only have 1 of the item in your hand you wish to plant");
    			return true;
            }
            
            if (!Utils.IsSoliniaItem(itemstack))
            {
            	player.sendMessage("The item in your main hand is not a territory flag");
    			return true;
            }
        	
            ISoliniaItem item = SoliniaItemAdapter.Adapt(itemstack);
            
	        if (!item.isTerritoryFlag())
	        {
	        	player.sendMessage("The item in your main hand is not a territory flag");
	        	return true;
	        }
	        
	        for(ISoliniaAlignment currentAlignment : StateManager.getInstance().getConfigurationManager().getAlignments())
	        {
	        	if (currentAlignment.getId() != alignment.getId())
	        	{
	        		if (currentAlignment.getChunk(chunk) != null)
		        	{
	        			currentAlignment.removeChunk(chunk);
		        	}
	        	} else {
        			SoliniaAlignmentChunkFactory.Create(currentAlignment, chunk.getSoliniaWorldName(), chunk.getChunkX(), chunk.getChunkZ());
	        		
	        		player.sendMessage("This economic area is now under control of the " + alignment.getName() + " alliance. It will now generate gold when trade routes pass through it");
	    	        player.getInventory().setItemInMainHand(null);
	    	        player.updateInventory();
	        	}
	        }
	        return true;
        } catch (CoreStateInitException e)
        {
        	
        } catch (SoliniaItemException e) {

		} catch (SoliniaChunkCreationException e) {
			return true;
		}

		return true;
	}
}
