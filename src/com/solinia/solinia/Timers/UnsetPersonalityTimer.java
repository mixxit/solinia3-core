package com.solinia.solinia.Timers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;

public class UnsetPersonalityTimer extends BukkitRunnable {
	Plugin plugin;
	public UnsetPersonalityTimer(Plugin plugin)
	{
		this.plugin = plugin;
	}
	
	@Override
	public void run() {
		for(Player player : Bukkit.getOnlinePlayers())
		{
			try
			{
				ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
				if (solPlayer.getPersonality().getBondId() == 0 || 
						solPlayer.getPersonality().getFirstTraitId() == 0 ||
						solPlayer.getPersonality().getSecondTraitId() == 0 ||
						solPlayer.getPersonality().getFlawId() == 0 ||
						solPlayer.getPersonality().getIdealId() == 0
						)
				{
					player.sendMessage("* You have not set your personality. Please see /personality");
				}
				
				if (solPlayer.getClassObj() != null && solPlayer.getClassObj().getOaths().size() > 0 && solPlayer.getOathId() == 0)
				{
					player.sendMessage("* You have not set your Oath. Please see /oath");
				}
			} catch (CoreStateInitException e)
			{
				
			}
		}
	}
}
