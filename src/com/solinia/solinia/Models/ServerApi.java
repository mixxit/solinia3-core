package com.solinia.solinia.Models;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;

import io.javalin.http.Context;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.logging.Logger;

public class ServerApi {
	private static final Logger log = Bukkit.getLogger();

	public static void discordPost(Context ctx) {
		
		try
		{
			if (!Bukkit.getPluginManager().getPlugin("Solinia3Core").isEnabled())
				return;
			
			final String message = ctx.body();
			new BukkitRunnable() {

				@Override
				public void run() {
					try
			        {
				        for (World world : Bukkit.getWorlds()) {
							for (Player player : world.getPlayers()) {
								ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
								if (solPlayer == null)
									continue;
								
								if (!solPlayer.isShowDiscord())
									continue;
								
								player.sendMessage(ChatColor.GRAY + "[Discord]~" + message + ChatColor.RESET);
							}
						}
			        } catch (CoreStateInitException e)
			        {
			        	
			        }
				}

			}.runTaskLater(Bukkit.getPluginManager().getPlugin("Solinia3Core"), 1);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
    }
}
