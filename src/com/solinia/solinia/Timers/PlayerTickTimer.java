package com.solinia.solinia.Timers;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Events.PlayerTickEvent;
import com.solinia.solinia.Events.ZoneTickEvent;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaAAAbility;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaPlayerSkill;
import com.solinia.solinia.Models.SoliniaZone;
import com.solinia.solinia.Utils.Utils;

public class PlayerTickTimer extends BukkitRunnable {

	@Override
	public void run() {

		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			try
			{
				ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
				if (solPlayer == null)
					continue;
				
				PlayerTickEvent soliniaevent = new PlayerTickEvent(solPlayer);
				Bukkit.getPluginManager().callEvent(soliniaevent);
			} catch (CoreStateInitException e)
			{
				
			}
		}
	}
}
