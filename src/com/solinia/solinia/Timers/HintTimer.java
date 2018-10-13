package com.solinia.solinia.Timers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class HintTimer extends BukkitRunnable {
	@Override
	public void run() {
		for(Player player : Bukkit.getOnlinePlayers())
		{
			player.sendMessage("[Hint] Staying online generates free experience - See /stats on how to claim this xp");
			player.sendMessage("[Hint] See the /inspiration command for rewards you can earn from voting for our server");
		}
	}
}
