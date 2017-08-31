package com.solinia.solinia.Utils;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;

public class ScoreboardUtils {
public static void UpdateScoreboard(Player player, ISoliniaPlayer soliniaplayer) {
		
		if (player != null && soliniaplayer != null)
		{
			BossBar bossbar = StateManager.getInstance().getBossBar(player.getUniqueId());
			if (bossbar == null)
			{
				bossbar = Bukkit.createBossBar(player.getUniqueId().toString(),BarColor.BLUE,BarStyle.SOLID);
				bossbar.addPlayer(player);
				StateManager.getInstance().setBossBar(player.getUniqueId(), bossbar);
			}
			
			try
			{
				boolean found = false;
				for(Player bossbarplayer : bossbar.getPlayers())
				{
					if (bossbarplayer == player)
					{
						found = true;
					}
				}
				
				if (found == false)
					bossbar.addPlayer(player);
				
				double maxmana = soliniaplayer.getMaxMP();
				bossbar.setTitle("MANA: " + soliniaplayer.getMana());
				bossbar.setProgress((double)((double)soliniaplayer.getMana() / (double)maxmana));

			} catch (Exception e)
			{
				System.out.println(e.getMessage() + " " + e.getStackTrace());				
			}
		}
		
	}
}
