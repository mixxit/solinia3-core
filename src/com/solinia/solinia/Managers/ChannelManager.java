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
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Models.DiscordChannel;
import com.solinia.solinia.Models.QueuedDiscordMessage;
import com.solinia.solinia.Models.SoliniaZone;
import com.solinia.solinia.Models.WorldWidePerk;
import com.solinia.solinia.Utils.ItemStackUtils;
import com.solinia.solinia.Utils.Utils;

import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public class ChannelManager implements IChannelManager {

	private AtomicInteger discordMessageCount = new AtomicInteger(0);
	private String discordMainChannelId;
	private String discordAdminChannelId;
	private String discordContentTeamChannelId;
	private String discordInCharacterChannelId;
	private ConcurrentHashMap<Integer, QueuedDiscordMessage> queuedDiscordMessages = new ConcurrentHashMap<Integer, QueuedDiscordMessage>();
	
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
		sendToDiscordMC(source,getDiscordMainChannelId(),source.getFullNameWithTitle() + ": " + originalmessage);
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
		sendToDiscordMC(source,getDiscordMainChannelId(),source.getFullNameWithTitle() + ": " + message);
	}
	
	@Override
	public void sendToDiscordQueuedMessage(Integer messageId)
	{
		QueuedDiscordMessage message = queuedDiscordMessages.get(messageId);
		
		if (message != null)
		{
			try
			{
				StateManager.getInstance().getDiscordClient().getChannelByID(message.getChannelId()).sendMessage(message.getMessage());
			} catch (Exception e)
			{
				// Skip message
				System.out.println(e.getMessage());
			}
			
			queuedDiscordMessages.remove(messageId);
		}
	}
	
	@Override 
	public void clearDiscordQueue()
	{
		if (queuedDiscordMessages != null)
		queuedDiscordMessages.clear();
	}
	
	@Override
	public void sendToDiscordMC(ISoliniaPlayer source, String channelId, String message)
	{
		if (channelId == null || channelId.equals(""))
			return;
		
		if (message.contains("AutoRestart"))
		{
			System.out.println("Skipped AutoRestart message");
			return;
		}
		
		UUID uuid = null;
		if (source != null)
			uuid = source.getUUID();
		message = message.replaceAll("@", "");
		message = ChatColor.stripColor(message);
		QueuedDiscordMessage discordMessage = new QueuedDiscordMessage(uuid, Long.parseLong(channelId), message);
		int nextMessage = discordMessageCount.getAndIncrement();
		
		
		queuedDiscordMessages.put(nextMessage,discordMessage);
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
	public void sendToGlobalChannel(String name, String message, boolean isFromDiscord, ItemStack itemStack) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			
			if (isFromDiscord)
			{
				try
				{
					ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(player);
		            if (solplayer != null)
		            {
		            	// showdiscord command
		            	if (!solplayer.isShowDiscord())
		            		continue;
		            }
				} catch (CoreStateInitException e)
				{
					
				}
			}
			TextComponent tc = new TextComponent(TextComponent.fromLegacyText(name + ": " + message));
			tc = decorateTextComponentsWithHovers(tc, itemStack);
			player.spigot().sendMessage(tc);
		}
	}
	
	@Override
	public void sendToOps(String name, String message, boolean isFromDiscord, ItemStack itemStack) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (!player.isOp())
				continue;
			
			if (isFromDiscord)
			{
				try
				{
					ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(player);
		            if (solplayer != null)
		            {
		            	// showdiscord command
		            	if (!solplayer.isShowDiscord())
		            		continue;
		            }
				} catch (CoreStateInitException e)
				{
					
				}
			}
			
			TextComponent tc = new TextComponent(TextComponent.fromLegacyText(name + ": " + message));
			tc = decorateTextComponentsWithHovers(tc, itemStack);
			player.spigot().sendMessage(tc);

		}
	}

	@Override
	public void handleDiscordCommand(DiscordChannel discordChannel, MessageReceivedEvent event) {
		if (!event.getMessage().getContent().startsWith("?"))
			return;
		
		String[] commands = event.getMessage().getContent().split(" ");
		String command = commands[0];
		switch(command)
		{
			case "?hotzones":
					sendHotzonesToDiscordChannel(discordChannel);
				break;
			case "?online":
					sendOnlineToDiscordChannel(discordChannel);
				break;
			case "?top":
				if (commands.length > 1)
					sendTopToDiscordChannel(discordChannel,commands[1]);
				else
					sendTopToDiscordChannel(discordChannel,"");
				break;
			case "?character":
				if (commands.length > 1)
					sendCharacterToDiscordChannel(discordChannel,commands[1]);
				break;
			case "?moblvl":
				if (commands.length > 2)
					sendMobLvlToDiscordChannel(discordChannel,Integer.parseInt(commands[1]),Integer.parseInt(commands[2]));
				break;
			case "?donation":
					sendDonationToDiscordChannel(discordChannel);
				break;
			case "?votes":
				sendVotesToDiscordChannel(discordChannel);
			break;
			case "?skillcheck":
				sendSkillCheckToDiscordChannel(discordChannel,commands[1]);
				break;
			case "?roll":
				sendRollToDiscordChannel(discordChannel,commands[1]);
				break;
			case "?loot":
				if (commands.length > 1)
				{
					String search = "";
					for(int i = 0; i < commands.length; i++)
					{
						if (i == 0)
							continue;
						
						search += commands[i] + " ";
					}
					sendLootListToDiscordChannel(discordChannel,search.trim());
				}
				break;
			default:
				return;
		}
	}
	
	private void sendCharacterToDiscordChannel(DiscordChannel discordChannel, String argument) {
		String targetChannelId = getChannelId(discordChannel);
		
		if (argument == null || argument.equals(""))
        {
			sendToDiscordMC(null,targetChannelId,"Insufficient arguments, must provide character name");
        	return;
        }
		
		if (argument.length() < 4)
		{
			sendToDiscordMC(null,targetChannelId,"Character name must be at least 4 characters long");
        	return;
		}
		
		try
        {
        	for (ISoliniaPlayer character : StateManager.getInstance().getConfigurationManager().getCharacters())
        	{
        		if (!character.getFullName().toUpperCase().contains(argument.toUpperCase()))
        			continue;
        		
        		String characterClass = "Citizen";
        		if (character.getClassObj() != null)
        			characterClass = character.getClassObj().getName();
        		
        		String characterRace = "Unknown";
        		if (character.getRace() != null)
        			characterRace = character.getRace().getName();
        		
        		String accountName = "";
        		try
        		{
        			OfflinePlayer l = Bukkit.getOfflinePlayer(character.getUUID());
        			accountName = l.getName();
        		} catch (Exception e)
        		{
        			
        		}
        			
        		sendToDiscordMC(null,targetChannelId,"[" + accountName + "] " + character.getFullName() + " the Level " + character.getLevel() + " " + characterRace + " " + characterClass);
        	}
        } catch (Exception e)
        {
        	sendToDiscordMC(null,targetChannelId,"Invalid number");
        	return;            	
        }
	}
	
	@Override
	public String getChannelId(DiscordChannel discordChannel)
	{
		String targetChannelId = getDiscordMainChannelId();
		if (discordChannel.equals(DiscordChannel.ADMIN))
			targetChannelId = this.getDiscordAdminChannelId();
		if (discordChannel.equals(DiscordChannel.INCHARACTER))
			targetChannelId = getDiscordInCharacterChannelId();
		if (discordChannel.equals(DiscordChannel.CONTENTTEAM))
			targetChannelId = getDiscordContentTeamChannelId();
		
		return targetChannelId;
	}

	private void sendRollToDiscordChannel(DiscordChannel discordChannel, String argument) {
		String targetChannelId = getChannelId(discordChannel);

		if (argument == null || argument.equals(""))
        {
			sendToDiscordMC(null,targetChannelId,"Insufficient arguments, must provide MAXNUMBER");
        	return;
        }
        
        if (!StringUtils.isNumeric(argument))
        {
        	sendToDiscordMC(null,targetChannelId,"Invalid argument, must provide a number");
        	return;
        }
        
        int maxnumber = 1;
        try
        {
        	maxnumber = Integer.parseInt(argument);
        } catch (Exception e)
        {
        	sendToDiscordMC(null,targetChannelId,"Invalid number");
        	return;            	
        }

        int roll = 1 + (int)(Math.random() * ((maxnumber - 1) + 1));
        
        String message = ChatColor.AQUA + " * You roll 1d"+maxnumber+". It's a "+roll+"!";
		sendToDiscordMC(null,targetChannelId,message);
	}

	private void sendSkillCheckToDiscordChannel(DiscordChannel discordChannel, String argument) {
		String targetChannelId = getChannelId(discordChannel);

		List<String> skills = new ArrayList<String>();
		skills.add("athletics");
		skills.add("acrobatics");
		skills.add("sleightofhand");
		skills.add("stealth");
		skills.add("arcana");
		skills.add("history");
		skills.add("investigation");
		skills.add("nature");
		skills.add("religion");
		skills.add("animalhandling");
		skills.add("insight");
		skills.add("medicine");
		skills.add("perception");
		skills.add("survival");
		skills.add("deception");
		skills.add("intimidation");
		skills.add("performance");
		skills.add("persuasion");
		
		String skill = "perception";
		
		if (argument == null || argument.equals(""))
		{
			sendToDiscordMC(null,targetChannelId,"Insufficient arguments, must provide skill from this list: " + String.join(",", skills));
			return;
		} else {
			skill = argument.toLowerCase();
			if (!skills.contains(skill))
			{
				sendToDiscordMC(null,targetChannelId,"Invalid argument [" + skill + "], must provide skill from this list: " + String.join(", ", skills));
				return;
			}
		}
		
		String message = "* You make a skill check for " + skill + ". You roll: " + Utils.RandomBetween(0, 20) + "/20";
		sendToDiscordMC(null,targetChannelId,message);
	}

	private void sendOnlineToDiscordChannel(DiscordChannel discordChannel) {
		String list = "";
		String targetChannelId = getChannelId(discordChannel);

		try
		{
			for(Player currentplayer : Bukkit.getServer().getOnlinePlayers())
		    {
	        	if (currentplayer.hasPermission("essentials.silentjoin"))
	        		continue;
	        	
        		ISoliniaPlayer solplayer;
				try {
					solplayer = SoliniaPlayerAdapter.Adapt(currentplayer);
				
		        	int lvl = (int) Math.floor(solplayer.getLevel());
		        	
		        	String racename = "UNKNOWN";
		        	String classname = "UNKNOWN";
		        	
		        	if (solplayer.getRace() != null)
		        		racename = solplayer.getRace().getName();
		        	if (solplayer.getClassObj() != null)
			        	classname = solplayer.getClassObj().getName();
		        	
		        	sendToDiscordMC(null,targetChannelId,"["+currentplayer.getName()+"]"+ solplayer.getFullName().toUpperCase() + " ["+ currentplayer.getWorld().getName() +"] - LVL " + lvl + " " + racename + " " + classname);
				} catch (CoreStateInitException e) {
					
				}
		    }
			
		} catch (Exception e)
		{
			// ignore it
		}
	}
	
	private void sendHotzonesToDiscordChannel(DiscordChannel discordChannel) {
		String list = "";
		String targetChannelId = getChannelId(discordChannel);

		try
		{
			sendToDiscordMC(null,targetChannelId,"Current 100% bonus XP Hotzones are: ");
			for(SoliniaZone zone : StateManager.getInstance().getCurrentHotzones())
			{
				sendToDiscordMC(null,targetChannelId,zone.getName() + ": " + zone.getX() + "," + zone.getY() + "," + zone.getZ());
			}
			
			
		} catch (Exception e)
		{
			// ignore it
		}
	}
	
	private void sendVotesToDiscordChannel(DiscordChannel discordChannel) {
		try
		{
			String targetChannelId = getChannelId(discordChannel);

			int rank = 1;
			for(ISoliniaPlayer player : StateManager.getInstance().getPlayerManager().getTopVotingPlayers())
			{
				sendToDiscordMC(null,targetChannelId,rank + ": " + player.getFullName() + " " + player.getMonthlyVote());
				rank++;
			}
			
		} catch (CoreStateInitException e)
		{
			// ignore it
		}
	}
	
	private void sendDonationToDiscordChannel(DiscordChannel discordChannel) {
		String list = "";
		String targetChannelId = getChannelId(discordChannel);

		try
		{
			int total = 0;
			
			for(WorldWidePerk perk : StateManager.getInstance().getWorldWidePerks())
			{
				if (perk.getPerkname().equals("DROP100")) {
					total += 4;
				}
				
				if (perk.getPerkname().equals("DROP1000")) {
					total += 40;
				}

				if (perk.getPerkname().equals("XPBONUS50")) {
					total += 1;
				}

				if (perk.getPerkname().equals("XPBONUS100")) {
					total += 2;
				}

				if (perk.getPerkname().equals("XPBONUS150")) {
					total += 3;
				}

				if (perk.getPerkname().equals("XPBONUS200")) {
					total += 4;
				}

				if (perk.getPerkname().equals("XPBONUS1000")) {
					total += 40;
				}

			}
			
			sendToDiscordMC(null,targetChannelId,"Total Donations to-date (CAD): $" + total);
		} catch (Exception e)
		{
			// ignore it
		}
	}

	private void sendMobLvlToDiscordChannel(DiscordChannel discordChannel, int mobmin, int mobmax) {
		String list = "";
		String targetChannelId = getChannelId(discordChannel);

		try
		{
			int count = 0;
			
			for(ISoliniaNPC npc : StateManager.getInstance().getConfigurationManager().getNPCs())
			{
				if (npc.isCorePet())
					continue;
								
				if (npc.getLevel() >= mobmin && npc.getLevel() <= mobmax)
				{
					list += npc.getName() + "(" + npc.getLevel() + ") ";
					count++;
				}
			}
			
			if (count > 30)
			{
				sendToDiscordMC(null,targetChannelId,"Too many mobs found, try lowering your level range");
			} else {
				sendToDiscordMC(null,targetChannelId,"Matching Npcs: " + list);
			}
		} catch (CoreStateInitException e)
		{
			// ignore it
		}
	}

	private void sendLootListToDiscordChannel(DiscordChannel discordChannel, String itemMatch) {
		try
		{
			String targetChannelId = getChannelId(discordChannel);

			if (itemMatch.length() < 3)
			{
				sendToDiscordMC(null,targetChannelId,"Item search must be at least 3 characters: " + itemMatch);
				return;
			}
			
			List<ISoliniaItem> items = StateManager.getInstance().getConfigurationManager().getItemsByPartialName(itemMatch);
			
			int itemIdLookup = 0;
			try
			{
				itemIdLookup = Integer.parseInt(itemMatch);
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(itemIdLookup);
				items.add(item);
			} catch (Exception e)
			{
				
			}
			
			if (items.size() < 1)
			{
				sendToDiscordMC(null,targetChannelId,"Could not find item: " + itemMatch);
				return;
			}
			
			if (items.size() > 1)
			{
				String itemsMatched = "";
				for (ISoliniaItem item : items)
				{
					itemsMatched += item.getId() + " ";
				}
				
				if (itemsMatched.length() > 256)
				{
					itemsMatched = itemsMatched.substring(0, 250) + "...";
				}
				
				sendToDiscordMC(null,targetChannelId,"More than one item found with this string, please be more specific than '" + itemMatch + "' (Or Try ?loot with one of these IDs: " + itemsMatched.trim() + ")");
				return;
			}
			
			String itemname = "";
			for(ISoliniaItem item : items)
			{
				itemname = item.getDisplayname();
				List<Integer> lootDropIds = StateManager.getInstance().getConfigurationManager().getLootDropIdsWithItemId(item.getId());
				
				if (lootDropIds.size() < 1)
				{
					sendToDiscordMC(null,targetChannelId,"Item [" + itemname + "] not found in any loot drops");
					return;
				}
				
				List<Integer> lootTableIds = StateManager.getInstance().getConfigurationManager().getLootTablesWithLootDrops(lootDropIds);
				
				if (lootTableIds.size() < 1)
				{
					sendToDiscordMC(null,targetChannelId,"Item [" + itemname + "] not found in any loot tables");
					return;
				}
				
				List<String> matchingNpcList = new ArrayList<String>();
				String currentLine = "";
				
				for(ISoliniaNPC npc : StateManager.getInstance().getConfigurationManager().getNPCs())
				{
					if (!lootTableIds.contains(npc.getLoottableid()))
						continue;
					
					if ((currentLine + npc.getName() + " ").length() > 2000)
					{
						matchingNpcList.add(currentLine);
						currentLine = "";
					}
					
					currentLine += npc.getName() + " ";
				}
				
				if (!currentLine.equals(""))
				{
					matchingNpcList.add(currentLine);
				}
				
				for(String line : matchingNpcList)
				{
					sendToDiscordMC(null,targetChannelId,"Item [" + itemname + "] found on: " + line);
				}
			}
			
		} catch (CoreStateInitException e)
		{
			// ignore it
		}
		
	}

	private void sendTopToDiscordChannel(DiscordChannel discordChannel, String classname) {
		try
		{
			String targetChannelId = getChannelId(discordChannel);

			int rank = 1;
			for(ISoliniaPlayer player : StateManager.getInstance().getPlayerManager().getTopLevelPlayers(classname))
			{
				sendToDiscordMC(null,targetChannelId,rank + ": " + player.getFullName() + " " + player.getLevel() + " level " + player.getClassObj().getName() + " aas: " + player.getAARanks().size());
				rank++;
			}
			
		} catch (CoreStateInitException e)
		{
			// ignore it
		}
		
	}
	
	@Override
	public void processNextDiscordMessage() {
		List<Integer> messageIds = Collections.list(this.queuedDiscordMessages.keys());
		if (messageIds.size() < 1)
			return;
		
		int minIndex = Collections.min(messageIds);
		sendToDiscordQueuedMessage(minIndex);
	}

	@Override
	public String getDiscordMainChannelId() {
		return discordMainChannelId;
	}

	@Override
	public void setDiscordMainChannelId(String discordMainChannelId) {
		this.discordMainChannelId = discordMainChannelId;
	}

	@Override
	public String getDiscordAdminChannelId() {
		return discordAdminChannelId;
	}

	@Override
	public void setDiscordAdminChannelId(String discordAdminChannelId) {
		this.discordAdminChannelId = discordAdminChannelId;
	}

	@Override
	public String getDiscordContentTeamChannelId() {
		return discordContentTeamChannelId;
	}

	@Override
	public void setDiscordContentTeamChannelId(String discordContentTeamChannelId) {
		this.discordContentTeamChannelId = discordContentTeamChannelId;
	}

	@Override
	public String getDiscordInCharacterChannelId() {
		return discordInCharacterChannelId;
	}

	@Override
	public void setDiscordInCharacterChannelId(String discordInCharacterChannelId) {
		this.discordInCharacterChannelId = discordInCharacterChannelId;
	}
}
