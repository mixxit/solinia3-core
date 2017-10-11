package com.solinia.solinia.Managers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventException;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.IChannelManager;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;

public class ChannelManager implements IChannelManager {

	@Override
	public void sendToLocalChannelDecorated(ISoliniaPlayer source, String message) {
		
		message = decorateLocalPlayerMessage(source, message);
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.getLocation().distance(source.getBukkitPlayer().getLocation()) <= 100)
			{
				try
				{
					ISoliniaPlayer solTargetPlayer = SoliniaPlayerAdapter.Adapt(player);
					if (solTargetPlayer.hasIgnored(source.getBukkitPlayer().getUniqueId()))
						continue;
					
					if (player.isOp() || source.getBukkitPlayer().isOp() || SoliniaPlayerAdapter.Adapt(player).understandsLanguage(source.getLanguage()))
					{
						
						player.sendMessage(message);
					} else {
						player.sendMessage(ChatColor.AQUA + " * " + source.getFullNameWithTitle() + " says something in a language you do not understand" + ChatColor.RESET);
						SoliniaPlayerAdapter.Adapt(player).tryImproveLanguage(source.getLanguage());
					}
				} catch (CoreStateInitException e)
				{
					player.sendMessage("You could not understand what " + source.getFullNameWithTitle() + " was saying as your character is currently uninitialised");
					e.printStackTrace();
				}
			}
		}
		
		System.out.println(message);
	}

	@Override
	public void sendToGlobalChannelDecorated(ISoliniaPlayer source, String message) {
		String originalmessage = message;
		message = decorateGlobalPlayerMessage(source, message);
		for (Player player : Bukkit.getOnlinePlayers()) {
			try
			{
				ISoliniaPlayer solTargetPlayer = SoliniaPlayerAdapter.Adapt(player);
				if (solTargetPlayer.hasIgnored(source.getBukkitPlayer().getUniqueId()))
					continue;
			} catch (CoreStateInitException e)
			{
				continue;
			}
				
			player.sendMessage(message);
		}
		
		System.out.println(message);
		sendToDiscordMC(source,source.getFullNameWithTitle() + ": " + originalmessage);
	}

	private String decorateLocalPlayerMessage(ISoliniaPlayer player, String message) {
		String channel = "L";
		String gender = "U";
		String race = "UNKNOWN";
		String profession = "UNKNOWN";

		String name = player.getFullNameWithTitle();

		if (player.getRace() != null) {
			race = player.getRace().getName().toUpperCase();
		}

		if (player.getClassObj() != null) {
			profession = player.getClassObj().getName().toUpperCase();
		}
		
		if (player.getGender() != null)
		{
			if (player.getGender().toUpperCase().equals("MALE"))
				gender = "M";
			
			if (player.getGender().toUpperCase().equals("FEMALE"))
				gender = "F";
		}

		String title = name;

		String messageheader = ChatColor.RESET + "[" + channel + "" + gender + "" + ChatColor.RED + race
				+ ChatColor.RESET + "" + profession + "]" + ChatColor.YELLOW + "~" + title + ChatColor.RESET + ": "
				+ ChatColor.RESET;
		message = messageheader + ChatColor.AQUA + message + ChatColor.RESET;
		return message;
	}
	
	private String decorateGlobalPlayerMessage(ISoliniaPlayer player, String message) {
		String channel = "O";
		String gender = "U";
		String race = "UNKNOWN";
		String profession = "UNKNOWN";

		String name = player.getFullNameWithTitle();

		if (player.getRace() != null) {
			race = player.getRace().getName().toUpperCase();
		}

		if (player.getClassObj() != null) {
			profession = player.getClassObj().getName().toUpperCase();
		}
		
		if (player.getGender() != null)
		{
			if (player.getGender().toUpperCase().equals("MALE"))
				gender = "M";
			
			if (player.getGender().toUpperCase().equals("FEMALE"))
				gender = "F";
		}

		String title = name;

		String messageheader = ChatColor.RESET + "[" + channel + "" + gender + "" + ChatColor.RED + race
				+ ChatColor.RESET + "" + profession + "]" + ChatColor.YELLOW + "~" + title + ChatColor.RESET + ": "
				+ ChatColor.RESET;
		message = messageheader + message;
		return message;
	}

	@Override
	public void sendToLocalChannel(ISoliniaPlayer source, String message) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.getLocation().distance(source.getBukkitPlayer().getLocation()) <= 100)
			{
				try
				{
					ISoliniaPlayer solTargetPlayer = SoliniaPlayerAdapter.Adapt(player);
					if (solTargetPlayer.hasIgnored(source.getBukkitPlayer().getUniqueId()))
						continue;
				} catch (CoreStateInitException e)
				{
					continue;
				}

				player.sendMessage(message);
			}
		}
		
		System.out.println(message);
	}

	@Override
	public void sendToGlobalChannel(ISoliniaPlayer source, String message) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.sendMessage(message);
		}
		
		System.out.println(message);
		sendToDiscordMC(source,source.getFullNameWithTitle() + ": " + message);
	}
	
	@Override
	public void sendToDiscordMC(ISoliniaPlayer source, String message)
	{
		System.out.println("sendToDiscordMC called");
		if(Bukkit.getServer().getPluginManager().getPlugin("DiscordMC")!=null && message != null && !message.equals(""))
		{
			System.out.println("Dispatching OOC message to discord");
			try
			{
				Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "discord send conversations " + message);
			} catch (IndexOutOfBoundsException e)
			{
				// Skip message
			}
		}
	}

	@Override
	public void sendToLocalChannel(ISoliniaLivingEntity source, String message) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.getLocation().distance(source.getBukkitLivingEntity().getLocation()) <= 100)
			player.sendMessage(message);
		}
	}

}
