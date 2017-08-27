package com.solinia.solinia.Managers;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.solinia.solinia.Interfaces.IChannelManager;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Utils.ItemStackUtils;

public class ChannelManager implements IChannelManager {

	@Override
	public void sendToLocalChannelDecorated(ISoliniaPlayer source, String message) {
		boolean linkitem = false;
		if (message.contains("$itemlink"))
		{
			message = message.replaceAll("$itemlink", "");
			linkitem = true;
		}
		
		message = decorateLocalPlayerMessage(source, message);
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.getLocation().distance(source.getBukkitPlayer().getLocation()) <= 100)
			{			
				player.sendMessage(message);
				if (linkitem)
					sendLinkMessage(source,player);
			}
		}
		
		System.out.println(message);
	}

	@Override
	public void sendToGlobalChannelDecorated(ISoliniaPlayer source, String message) {
		boolean linkitem = false;
		if (message.contains("$itemlink"))
		{
			message = message.replaceAll("$itemlink", "");
			linkitem = true;
		}
		
		message = decorateGlobalPlayerMessage(source, message);
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.sendMessage(message);
			if (linkitem)
				sendLinkMessage(source,player);
		}

		
		
		System.out.println(message);
	}

	private void sendLinkMessage(ISoliniaPlayer source, Player player) {
		/*
		ItemStack item = source.getBukkitPlayer().getInventory().getItemInMainHand();
		String tmpjson = "";
		
		if (item != null)
		{
			if (item.getItemMeta() != null)
			{
				if (item.getItemMeta().getDisplayName() != null)
				{
					tmpjson = "{\"text\":\""+player.getDisplayName() + " item link: [" + item.getItemMeta().getDisplayName()+"]\",\"hoverEvent\":{\"action\":\"show_item\",\"value\":\""+ItemStackUtils.ItemStackAsJsonString(item)+"\"}}";
				}
			}
		}

		if (tmpjson.equals(""))
			return;

		final String json = tmpjson;

		PacketContainer chat = new PacketContainer(PacketType.Play.Server.CHAT);
		chat.getChatComponents().write(0, WrappedChatComponent.fromJson(json));

		try {
			ProtocolLibrary.getProtocolManager().sendServerPacket(player, chat);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		*/
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

}
