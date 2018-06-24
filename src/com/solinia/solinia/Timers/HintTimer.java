package com.solinia.solinia.Timers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class HintTimer extends BukkitRunnable {
	@Override
	public void run() {
		for(Player player : Bukkit.getOnlinePlayers())
		{
			player.sendMessage("[Hint] You can gain experience by leaving your character online, you will gain the equivelent of killing your same level mob every 60 seconds - You do not need to be in combat to gain this experience, simply idling will count. See /stats on how to claim your attendence XP");
			player.sendMessage("[Hint] See the /inspiration command for rewards you can earn from voting for our server");
		}
	}
}
