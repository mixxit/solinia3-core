package com.solinia.solinia.Managers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

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
					if (player.isOp() || source.getBukkitPlayer().isOp() || SoliniaPlayerAdapter.Adapt(player).understandsLanguage(source.getLanguage()))
					{
						player.sendMessage(message);
					} else {
						player.sendMessage(" * " + source.getFullName() + " says something in a language you do not understand");
						SoliniaPlayerAdapter.Adapt(player).tryImproveLanguage(source.getLanguage());
					}
				} catch (CoreStateInitException e)
				{
					player.sendMessage("You could not understand what " + source.getFullName() + " was saying as your character is currently uninitialised");
					e.printStackTrace();
				}
			}
		}
		
		System.out.println(message);
	}

	@Override
	public void sendToGlobalChannelDecorated(ISoliniaPlayer source, String message) {
		message = decorateGlobalPlayerMessage(source, message);
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.sendMessage(message);
		}
		
		System.out.println(message);
	}

	private String decorateLocalPlayerMessage(ISoliniaPlayer player, String message) {
		String channel = "L";
		String gender = "U";
		String race = "UNKNOWN";
		String profession = "UNKNOWN";

		String name = player.getFullName();

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

		String name = player.getFullName();

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
			player.sendMessage(message);
		}
		
		System.out.println(message);
	}

	@Override
	public void sendToGlobalChannel(ISoliniaPlayer source, String message) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.sendMessage(message);
		}
		
		System.out.println(message);
	}

	@Override
	public void sendToLocalChannel(ISoliniaLivingEntity source, String message) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.getLocation().distance(source.getBukkitLivingEntity().getLocation()) <= 100)
			player.sendMessage(message);
		}
	}

}
