package com.solinia.solinia.Commands;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Utils.Utils;

public class CommandUnstuck implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
            return false;
		}
		
		Player player = (Player)sender;
		if (!player.isOp() && !Utils.canUnstuck(player))
		{
			return true;
		}
		
		try {
			LocalDateTime datetime = LocalDateTime.now();
			Timestamp nowtimestamp = Timestamp.valueOf(datetime);
			StateManager.getInstance().getPlayerManager().setPlayerLastUnstuck(player.getUniqueId(), nowtimestamp);
			int highesty = player.getWorld().getHighestBlockAt(player.getLocation()).getY();
			int targety = highesty;
			for (int y = player.getLocation().getBlockY()+2; y <= highesty; y++)
			{
				if (!isSafeLocation(player.getLocation(),y))
					continue;
				
				targety = y;
				break;
			}
			
			player.teleport(new Location(player.getWorld(), player.getLocation().getBlockX(), targety, player.getLocation().getBlockZ()));
			
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}
	
	public boolean isSafeLocation(Location currentLocation,int y) {
		Location location = new Location(currentLocation.getWorld(),currentLocation.getBlockX(), y, currentLocation.getBlockZ());
        Block feet = location.getBlock();
        if (!feet.getType().isTransparent() && !feet.getLocation().add(0, 1, 0).getBlock().getType().isTransparent()) {
            return false; // not transparent (will suffocate)
        }
        Block head = feet.getRelative(BlockFace.UP);
        if (!head.getType().isTransparent()) {
            return false; // not transparent (will suffocate)
        }
        Block ground = feet.getRelative(BlockFace.DOWN);
        if (!ground.getType().isSolid()) {
            return false; // not solid
        }
        return true;
    }
}
