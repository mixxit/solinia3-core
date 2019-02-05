package com.solinia.solinia.Managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.IChannelManager;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Models.SoliniaZone;
import com.solinia.solinia.Utils.ItemStackUtils;
import com.solinia.solinia.Utils.Utils;

import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class ChannelManager implements IChannelManager {

	@Override
	public void sendToLocalChannelDecorated(ISoliniaPlayer source, String message, String coremessage, ItemStack itemStack) {
		
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
						TextComponent tc = new TextComponent(TextComponent.fromLegacyText(message));
						tc = decorateTextComponentsWithHovers(tc, itemStack);
						player.spigot().sendMessage(tc);
					} else {
						TextComponent tc = new TextComponent(TextComponent.fromLegacyText(decorateLocalPlayerMessage(source, Utils.ConvertToRunic(coremessage)) + " [" + source.getLanguage() + "]"));
						tc = decorateTextComponentsWithHovers(tc, itemStack);
						player.spigot().sendMessage(tc);

						SoliniaPlayerAdapter.Adapt(player).tryImproveLanguage(source.getLanguage());
					}
				} catch (CoreStateInitException e)
				{
					TextComponent tc = new TextComponent(TextComponent.fromLegacyText("You could not understand what " + source.getFullNameWithTitle() + " was saying as your character is currently uninitialised"));
					tc = decorateTextComponentsWithHovers(tc, itemStack);
					player.spigot().sendMessage(tc);
					e.printStackTrace();
				}
			}
		}
		
		System.out.println(message);
	}
	
	@Override
	public void sendToWhisperChannelDecorated(ISoliniaPlayer source, String message, String coremessage, ItemStack itemStack) {
		
		message = decorateWhisperPlayerMessage(source, message);
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.getLocation().distance(source.getBukkitPlayer().getLocation()) <= 15)
			{
				try
				{
					ISoliniaPlayer solTargetPlayer = SoliniaPlayerAdapter.Adapt(player);
					if (solTargetPlayer.hasIgnored(source.getBukkitPlayer().getUniqueId()))
						continue;
					
					if (player.isOp() || source.getBukkitPlayer().isOp() || SoliniaPlayerAdapter.Adapt(player).understandsLanguage(source.getLanguage()))
					{
						TextComponent tc = new TextComponent(TextComponent.fromLegacyText(message));
						tc = decorateTextComponentsWithHovers(tc, itemStack);
						player.spigot().sendMessage(tc);
					} else {
						TextComponent tc = new TextComponent(TextComponent.fromLegacyText(decorateLocalPlayerMessage(source, Utils.ConvertToRunic(coremessage)) + " [" + source.getLanguage() + "]"));
						tc = decorateTextComponentsWithHovers(tc, itemStack);
						player.spigot().sendMessage(tc);

						SoliniaPlayerAdapter.Adapt(player).tryImproveLanguage(source.getLanguage());
					}
				} catch (CoreStateInitException e)
				{
					TextComponent tc = new TextComponent(TextComponent.fromLegacyText("You could not understand what " + source.getFullNameWithTitle() + " was saying as your character is currently uninitialised"));
					tc = decorateTextComponentsWithHovers(tc, itemStack);
					player.spigot().sendMessage(tc);
					e.printStackTrace();
				}
			}
		}
		
		System.out.println(message);
	}
	
	@Override
	public void sendToShoutChannelDecorated(ISoliniaPlayer source, String message, String coremessage, ItemStack itemStack) {
		
		message = decorateShoutPlayerMessage(source, message.toUpperCase());
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.getLocation().distance(source.getBukkitPlayer().getLocation()) <= 200)
			{
				try
				{
					ISoliniaPlayer solTargetPlayer = SoliniaPlayerAdapter.Adapt(player);
					if (solTargetPlayer.hasIgnored(source.getBukkitPlayer().getUniqueId()))
						continue;
					
					if (player.isOp() || source.getBukkitPlayer().isOp() || SoliniaPlayerAdapter.Adapt(player).understandsLanguage(source.getLanguage()))
					{
						TextComponent tc = new TextComponent(TextComponent.fromLegacyText(message));
						tc = decorateTextComponentsWithHovers(tc, itemStack);
						player.spigot().sendMessage(tc);
					} else {
						TextComponent tc = new TextComponent(TextComponent.fromLegacyText(decorateLocalPlayerMessage(source, Utils.ConvertToRunic(coremessage)) + " [" + source.getLanguage() + "]"));
						tc = decorateTextComponentsWithHovers(tc, itemStack);
						player.spigot().sendMessage(tc);

						SoliniaPlayerAdapter.Adapt(player).tryImproveLanguage(source.getLanguage());
					}
				} catch (CoreStateInitException e)
				{
					TextComponent tc = new TextComponent(TextComponent.fromLegacyText("You could not understand what " + source.getFullNameWithTitle() + " was saying as your character is currently uninitialised"));
					tc = decorateTextComponentsWithHovers(tc, itemStack);
					player.spigot().sendMessage(tc);
					e.printStackTrace();
				}
			}
		}
		
		System.out.println(message);
	}

	private TextComponent decorateTextComponentsWithHovers(TextComponent tc, ItemStack itemStack) {
		if (itemStack != null && tc.toLegacyText().contains("itemlink"))
		{
			try
			{
				TextComponent itemLinkComponent = new TextComponent();
				String title = " <" + itemStack.getItemMeta().getDisplayName() + ">";
				itemLinkComponent.setText(title);
				itemLinkComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new ComponentBuilder(ItemStackUtils.ConvertItemStackToJsonRegular(itemStack)).create()));
				tc.addExtra(itemLinkComponent);
			} catch (Exception e)
			{
				System.out.println("Could not create itemlink for message: " + tc.getText() + " with itemStack");
			}
		}
		
		return tc;
	}

	@Override
	public void sendToGlobalChannelDecorated(ISoliniaPlayer source, String message, ItemStack itemStack) {
		String originalmessage = message;
		message = decorateGlobalPlayerMessage(source, message);
		for (Player player : Bukkit.getOnlinePlayers()) {
			try
			{
				ISoliniaPlayer solTargetPlayer = SoliniaPlayerAdapter.Adapt(player);
				if (!solTargetPlayer.isOocEnabled())
					continue;
				
				if (solTargetPlayer.hasIgnored(source.getBukkitPlayer().getUniqueId()))
					continue;
			} catch (CoreStateInitException e)
			{
				continue;
			}
				
			TextComponent tc = new TextComponent(TextComponent.fromLegacyText(message));
			tc = decorateTextComponentsWithHovers(tc, itemStack);
			player.spigot().sendMessage(tc);
		}
		
		System.out.println(message);
	}
	
	private String decorateLocalPlayerMessage(ISoliniaPlayer player, String message) {
		String channel = "L";
		String gender = "U";
		String race = "UNK";
		String profession = "UNK";
		String vampire = ChatColor.GRAY + "N" + ChatColor.RESET;
		
		if (player.isVampire())
		{
			vampire = ChatColor.RED + "V" + ChatColor.RESET;
		}

		String name = player.getFullNameWithTitle();
		ChatColor racealignmentcolour = ChatColor.GRAY;

		if (player.getRace() != null) {
			race = player.getRace().getShortName().toUpperCase();
			if (player.getRace().getAlignment().equals("EVIL"))
			{
				racealignmentcolour = ChatColor.RED;
			}
			if (player.getRace().getAlignment().equals("NEUTRAL"))
			{
				racealignmentcolour = ChatColor.YELLOW;
			}
			if (player.getRace().getAlignment().equals("GOOD"))
			{
				racealignmentcolour = ChatColor.GREEN;
			}
		}

		if (player.getClassObj() != null) {
			profession = player.getClassObj().getShortName().toUpperCase();
		}
		
		if (player.getGender() != null)
		{
			if (player.getGender().toUpperCase().equals("MALE"))
				gender = "M";
			
			if (player.getGender().toUpperCase().equals("FEMALE"))
				gender = "F";
		}

		String title = name;
		ChatColor nameColour = ChatColor.YELLOW;
		
		String messageheader = ChatColor.RESET + "[" + channel + gender + vampire + racealignmentcolour + race
				+ ChatColor.RESET + "" + profession + "]" + nameColour + "~" + title + ChatColor.RESET + ": "
				+ ChatColor.RESET;
		message = messageheader + ChatColor.AQUA + message + ChatColor.RESET;
		return message;
	}
	
	private String decorateWhisperPlayerMessage(ISoliniaPlayer player, String message) {
		String channel = "W";
		String gender = "U";
		String race = "UNK";
		String profession = "UNK";
		String vampire = ChatColor.GRAY + "N" + ChatColor.RESET;
		
		if (player.isVampire())
		{
			vampire = ChatColor.RED + "V" + ChatColor.RESET;
		}

		String name = player.getFullNameWithTitle();
		ChatColor racealignmentcolour = ChatColor.GRAY;

		if (player.getRace() != null) {
			race = player.getRace().getShortName().toUpperCase();
			if (player.getRace().getAlignment().equals("EVIL"))
			{
				racealignmentcolour = ChatColor.RED;
			}
			if (player.getRace().getAlignment().equals("NEUTRAL"))
			{
				racealignmentcolour = ChatColor.YELLOW;
			}
			if (player.getRace().getAlignment().equals("GOOD"))
			{
				racealignmentcolour = ChatColor.GREEN;
			}
		}

		if (player.getClassObj() != null) {
			profession = player.getClassObj().getShortName().toUpperCase();
		}
		
		if (player.getGender() != null)
		{
			if (player.getGender().toUpperCase().equals("MALE"))
				gender = "M";
			
			if (player.getGender().toUpperCase().equals("FEMALE"))
				gender = "F";
		}

		String title = name;
		ChatColor nameColour = ChatColor.YELLOW;
		
		String messageheader = ChatColor.RESET + "[" + channel + gender + vampire + racealignmentcolour + race
				+ ChatColor.RESET + "" + profession + "]" + nameColour + "~" + title + ChatColor.RESET + ": "
				+ ChatColor.RESET;
		message = messageheader + ChatColor.AQUA + message + ChatColor.RESET;
		return message;
	}
	
	private String decorateShoutPlayerMessage(ISoliniaPlayer player, String message) {
		String channel = "S";
		String gender = "U";
		String race = "UNK";
		String profession = "UNK";
		String vampire = ChatColor.GRAY + "N" + ChatColor.RESET;
		
		if (player.isVampire())
		{
			vampire = ChatColor.RED + "V" + ChatColor.RESET;
		}

		String name = player.getFullNameWithTitle();
		ChatColor racealignmentcolour = ChatColor.GRAY;

		if (player.getRace() != null) {
			race = player.getRace().getShortName().toUpperCase();
			if (player.getRace().getAlignment().equals("EVIL"))
			{
				racealignmentcolour = ChatColor.RED;
			}
			if (player.getRace().getAlignment().equals("NEUTRAL"))
			{
				racealignmentcolour = ChatColor.YELLOW;
			}
			if (player.getRace().getAlignment().equals("GOOD"))
			{
				racealignmentcolour = ChatColor.GREEN;
			}
		}

		if (player.getClassObj() != null) {
			profession = player.getClassObj().getShortName().toUpperCase();
		}
		
		if (player.getGender() != null)
		{
			if (player.getGender().toUpperCase().equals("MALE"))
				gender = "M";
			
			if (player.getGender().toUpperCase().equals("FEMALE"))
				gender = "F";
		}

		String title = name;
		ChatColor nameColour = ChatColor.YELLOW;
		
		String messageheader = ChatColor.RESET + "[" + channel + gender + vampire + racealignmentcolour + race
				+ ChatColor.RESET + "" + profession + "]" + nameColour + "~" + title + ChatColor.RESET + ": "
				+ ChatColor.RESET;
		message = messageheader + ChatColor.AQUA + message + ChatColor.RESET;
		return message;
	}
	
	private String decorateGlobalPlayerMessage(ISoliniaPlayer player, String message) {
		String channel = "O";
		String gender = "U";
		String vampire = ChatColor.GRAY + "N" + ChatColor.RESET;
		
		if (player.isVampire())
		{
			vampire = ChatColor.RED + "V" + ChatColor.RESET;
		}
		
		String race = "UNKNOWN";
		String profession = "UNKNOWN";

		String name = player.getFullNameWithTitle();
		ChatColor racealignmentcolour = ChatColor.GRAY;

		if (player.getRace() != null) {
			race = player.getRace().getShortName().toUpperCase();
			if (player.getRace().getAlignment().equals("EVIL"))
			{
				racealignmentcolour = ChatColor.RED;
			}
			if (player.getRace().getAlignment().equals("NEUTRAL"))
			{
				racealignmentcolour = ChatColor.YELLOW;
			}
			if (player.getRace().getAlignment().equals("GOOD"))
			{
				racealignmentcolour = ChatColor.GREEN;
			}
		}

		if (player.getClassObj() != null) {
			profession = player.getClassObj().getShortName().toUpperCase();
		}
		
		if (player.getGender() != null)
		{
			if (player.getGender().toUpperCase().equals("MALE"))
				gender = "M";
			
			if (player.getGender().toUpperCase().equals("FEMALE"))
				gender = "F";
		}

		String title = name;
		ChatColor nameColour = ChatColor.YELLOW;

		String messageheader = ChatColor.RESET + "[" + channel + gender + vampire + racealignmentcolour + race
				+ ChatColor.RESET + "" + profession + "]" + nameColour + "~" + title + ChatColor.RESET + ": "
				+ ChatColor.RESET;
		message = messageheader + message;
		return message;
	}

	@Override
	public void sendToLocalChannel(ISoliniaPlayer source, String message, boolean isBardSongFilterable, ItemStack itemStack) {
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

				TextComponent tc = new TextComponent(TextComponent.fromLegacyText(message));
				tc = decorateTextComponentsWithHovers(tc, itemStack);
				player.spigot().sendMessage(tc);
			}
		}
		
		System.out.println(message);
	}

	@Override
	public void sendToGlobalChannel(ISoliniaPlayer source, String message, ItemStack itemStack) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			TextComponent tc = new TextComponent(TextComponent.fromLegacyText(message));
			tc = decorateTextComponentsWithHovers(tc, itemStack);
			player.spigot().sendMessage(tc);
		}
		
		System.out.println(message);
	}
	
	@Override
	public void sendToLocalChannel(ISoliniaLivingEntity source, String message, boolean isBardSongFilterable, ItemStack itemStack) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.getLocation().distance(source.getBukkitLivingEntity().getLocation()) <= 100)
			{
				try
				{
					ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
					if (isBardSongFilterable && !solPlayer.isSongsEnabled())
						continue;
				} catch (CoreStateInitException e)
				{
					
				}
				
				TextComponent tc = new TextComponent(TextComponent.fromLegacyText(message));
				tc = decorateTextComponentsWithHovers(tc, itemStack);
				player.spigot().sendMessage(tc);
			}
		}
	}
	
	@Override
	public void sendToLocalChannelLivingEntityChat(ISoliniaLivingEntity source, String message, boolean allowlanguagelearn, String coremessage, ItemStack itemStack)
	{
		try
		{
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (player.getLocation().distance(source.getBukkitLivingEntity().getLocation()) <= 100)
				{
					if (player.isOp() || (source.getLanguage() == null || source.isSpeaksAllLanguages() || SoliniaPlayerAdapter.Adapt(player).understandsLanguage(source.getLanguage())))
					{
						TextComponent tc = new TextComponent(TextComponent.fromLegacyText(message));
						tc = decorateTextComponentsWithHovers(tc, itemStack);
						player.spigot().sendMessage(tc);
					} else {
						TextComponent tc = new TextComponent(TextComponent.fromLegacyText(ChatColor.AQUA + source.getName() + " says '" + Utils.ConvertToRunic(coremessage) + "' [" + source.getLanguage() + "]" + ChatColor.RESET));
						tc = decorateTextComponentsWithHovers(tc, itemStack);
						player.spigot().sendMessage(tc);
						
						if (allowlanguagelearn == true)
						{
							if (source.getLanguage() != null && !source.getLanguage().equals(""))
							SoliniaPlayerAdapter.Adapt(player).tryImproveLanguage(source.getLanguage());
						}
					} 
				}
			}
		} catch (CoreStateInitException e)
		{
			// skip
		}
	}
	
	@Override
	public void sendToGlobalChannel(String name, String message, ItemStack itemStack) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			
			
			TextComponent tc = new TextComponent(TextComponent.fromLegacyText(name + ": " + message));
			tc = decorateTextComponentsWithHovers(tc, itemStack);
			player.spigot().sendMessage(tc);
		}
	}
	
	@Override
	public void sendToOps(String name, String message, ItemStack itemStack) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (!player.isOp())
				continue;
			
			TextComponent tc = new TextComponent(TextComponent.fromLegacyText(name + ": " + message));
			tc = decorateTextComponentsWithHovers(tc, itemStack);
			player.spigot().sendMessage(tc);

		}
	}
}
