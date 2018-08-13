package com.solinia.solinia.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.inventivetalent.glow.GlowAPI;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;

public class CommandToggleGlow implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (sender instanceof Player) {
	            Player player = (Player) sender;
	            try
	            {
	            
	            ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(player);
	            
	            if (solplayer == null)
	            {
	            	player.sendMessage("Failed to toggle Glow, player does not exist");
	            	return false;	
	            }
	            
	            solplayer.setGlowTargetting(!solplayer.isGlowTargetting());
	            
	            if (solplayer.isGlowTargetting() == false)
	            {
	            	if (StateManager.getInstance().getEntityManager().getEntityTargets().get(player.getUniqueId()) != null)
	    			{
	    				Entity currentTarget = Bukkit.getEntity(StateManager.getInstance().getEntityManager().getEntityTargets().get(player.getUniqueId()));
	    				if (currentTarget != null)
	    				{
	    					GlowAPI.setGlowing((Entity)currentTarget, false, player);
	    				}
	    			}
	            }
	            
	            player.sendMessage("Toggled your glow targetting to : " + solplayer.isGlowTargetting());
	            
            } catch (CoreStateInitException e)
            {
            	e.printStackTrace();
            }
        }

        return true;
	}
}
