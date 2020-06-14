package com.solinia.solinia.Models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Utils.ChatUtils;
import io.javalin.http.Context;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.logging.Logger;

public class ServerApi {
	private static final Logger log = Bukkit.getLogger();
	
	public static void metricsGet(Context ctx) {
		try
		{
			SoliniaMetrics metrics = new SoliniaMetrics();

			if (!StateManager.getInstance().getPlugin().isEnabled())
				metrics = new SoliniaMetrics();
			else
				metrics = StateManager.getInstance().getConfigurationManager().getSoliniaMetrics();

			GsonBuilder gsonbuilder = new GsonBuilder();
			Gson gson = gsonbuilder.create();
			ctx.result(gson.toJson(metrics));
		} catch (Exception e)
		{
			e.printStackTrace();
		}
    }

	public static void discordPost(Context ctx) {
		
		try
		{
			if (!StateManager.getInstance().getPlugin().isEnabled())
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
								
								ChatUtils.SendHint(player, HINT.DISCORD_MESSAGE, ChatColor.GRAY + "~" + message + ChatColor.RESET, false);
							}
						}
			        } catch (CoreStateInitException e)
			        {
			        	
			        }
				}

			}.runTaskLater(StateManager.getInstance().getPlugin(), 1);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
    }
	
	
}
