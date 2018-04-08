package com.solinia.solinia.Commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.solinia.solinia.Adapters.SoliniaChunkAdapter;
import com.solinia.solinia.Adapters.SoliniaItemAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaItemException;
import com.solinia.solinia.Interfaces.ISoliniaAlignment;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaAlignment;
import com.solinia.solinia.Models.SoliniaChunk;
import com.solinia.solinia.Utils.Utils;

public class CommandTrader implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
			return false;
				
		Player player = (Player)sender;
		try
        {
			player.sendMessage("Territory Overview:");
			player.sendMessage("-----------------------------");
			
			SoliniaChunk chunk = SoliniaChunkAdapter.Adapt(player.getWorld().getChunkAt(player.getLocation()));
			if (chunk == null)
			{
				player.sendMessage("Territory does not exist!");
				return true;
			}
			
			player.sendMessage("Economic Zone: [" + chunk.getChunkX() + "," + chunk.getChunkZ() + "]");
			player.sendMessage("Material Zone: " + chunk.isInZoneWithMaterials(player.getWorld()));
			if (chunk.getAlignmentId() > 0)
			{
				ISoliniaAlignment alignment = StateManager.getInstance().getConfigurationManager().getAlignment(chunk.getAlignmentId());
				player.sendMessage("Controlled by alliance: " + alignment.getName().toUpperCase());
			} else {
				player.sendMessage("Controlled by alliance: NONE");
			}
			
			player.sendMessage("Coin generated: " + chunk.getCoinGenerated());
			player.sendMessage("Total Trade routes passing through: " + chunk.getTotalTradeRoutes());
        
	        return true;
        } catch (CoreStateInitException e)
        {
        	
        } catch (SoliniaItemException e) {

		}

		return true;
	}
}
