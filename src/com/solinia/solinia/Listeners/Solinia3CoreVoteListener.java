package com.solinia.solinia.Listeners;

import java.io.IOException;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.solinia.solinia.Solinia3CorePlugin;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Utils.Utils;
import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;

import net.md_5.bungee.api.ChatColor;

public class Solinia3CoreVoteListener implements Listener {
	
	Solinia3CorePlugin plugin;

	public Solinia3CoreVoteListener(Solinia3CorePlugin solinia3CorePlugin) {
		// TODO Auto-generated constructor stub
		plugin = solinia3CorePlugin;
	}
	
	@EventHandler(priority=EventPriority.NORMAL)
    public void onVotifierEvent(VotifierEvent event) {
        Vote vote = event.getVote();
        
        Player player = plugin.getServer().getPlayer(vote.getUsername());
        
        String uuid = "";
        
        if (player != null)
        	uuid = player.getUniqueId().toString();
		else
			try {
				uuid = Utils.getUUIDFromPlayerName(vote.getUsername());
				uuid = java.util.UUID.fromString(
						uuid
					    .replaceFirst( 
					        "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5" 
					    )
					).toString();
				
			} catch (IOException e1) {
				System.out.println("Cannot find player to associate inspiration to (" + vote.getUsername() + ") [WebLookup]");
			}

        if (uuid == null || uuid.equals(""))
        {
        	System.out.println("Cannot find player to associate inspiration to (" + vote.getUsername() + ")");
        	return;
        }
        
        System.out.println("Vote received: " + vote.getUsername() + " UUID: " + uuid);
        
        try {
			ISoliniaPlayer solplayer = StateManager.getInstance().getPlayerManager().getPlayerAndDoNotCreate(UUID.fromString(uuid));
			if (solplayer != null)
			{
				solplayer.setInspiration(solplayer.getInspiration()+1);
				
				if (player != null)
				{
					player.sendMessage(ChatColor.YELLOW + "* You have gained inspiration! See /inspiration");
				}
			} else {
				System.out.println("Cannot find player to associate inspiration to (" + vote.getUsername() + ")");
			}
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }
}
