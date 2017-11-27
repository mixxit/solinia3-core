package com.solinia.solinia.Managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventException;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.IChannelManager;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Interfaces.ISoliniaLootDrop;
import com.solinia.solinia.Interfaces.ISoliniaLootDropEntry;
import com.solinia.solinia.Interfaces.ISoliniaLootTable;
import com.solinia.solinia.Interfaces.ISoliniaLootTableEntry;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Models.DiscordChannel;
import com.solinia.solinia.Models.QueuedDiscordMessage;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;

public class ChannelManager implements IChannelManager {

	private AtomicInteger discordMessageCount = new AtomicInteger(0);
	private String discordmainchannelid;
	private String discordadminchannelid;
	private ConcurrentHashMap<Integer, QueuedDiscordMessage> queuedDiscordMessages = new ConcurrentHashMap<Integer, QueuedDiscordMessage>();
	
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
		sendToDiscordMC(source,getDefaultDiscordChannel(),source.getFullNameWithTitle() + ": " + originalmessage);
	}

	@Override
	public String getDefaultDiscordChannel() {
		return this.discordmainchannelid;
	}

	@Override
	public String getAdminDiscordChannel() {
		return this.discordadminchannelid;
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
		if (player.isRacialKing())
		{
			nameColour = ChatColor.GOLD;
		}
		
		if (player.isAlignmentEmperor())
		{
			nameColour = ChatColor.LIGHT_PURPLE;
		}

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
		if (player.isRacialKing())
		{
			nameColour = ChatColor.GOLD;
		}
		
		if (player.isAlignmentEmperor())
		{
			nameColour = ChatColor.LIGHT_PURPLE;
		}

		String messageheader = ChatColor.RESET + "[" + channel + gender + vampire + racealignmentcolour + race
				+ ChatColor.RESET + "" + profession + "]" + nameColour + "~" + title + ChatColor.RESET + ": "
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
		sendToDiscordMC(source,getDefaultDiscordChannel(),source.getFullNameWithTitle() + ": " + message);
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
	public void sendToDiscordMC(ISoliniaPlayer source, String channelId, String message)
	{
		if (channelId == null || channelId.equals(""))
			return;
		
		UUID uuid = null;
		if (source != null)
			uuid = source.getUUID();
		message = ChatColor.stripColor(message);
		QueuedDiscordMessage discordMessage = new QueuedDiscordMessage(uuid, channelId, message);
		int nextMessage = discordMessageCount.getAndIncrement();
		
		
		queuedDiscordMessages.put(nextMessage,discordMessage);
	}

	@Override
	public void sendToLocalChannel(ISoliniaLivingEntity source, String message) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.getLocation().distance(source.getBukkitLivingEntity().getLocation()) <= 100)
			player.sendMessage(message);
		}
	}

	@Override
	public void sendToGlobalChannel(String name, String message) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.sendMessage(name + ":" + message);
		}
	}
	
	@Override
	public void sendToOps(String name, String message) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (!player.isOp())
				continue;
			
			player.sendMessage(name + ":" + message);
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
			case "?top":
				if (commands.length > 1)
					sendTopToDiscordChannel(discordChannel,commands[1]);
				else
					sendTopToDiscordChannel(discordChannel,"");

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

	private void sendItemListToDiscordChannel(DiscordChannel discordChannel, String itemMatch) {
		try
		{
			String targetChannelId = getDefaultDiscordChannel();
			if (discordChannel.equals(DiscordChannel.ADMIN))
				targetChannelId = getAdminDiscordChannel();
			
			int itemId = 0;
			try
			{
				itemId = StateManager.getInstance().getConfigurationManager().getItem(Integer.parseInt(itemMatch)).getId();
			} catch (Exception e)
			{
				
			}
			
			if (itemId > 0)
			{
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(itemId);
				sendItemToDiscordChannel(discordChannel,item);
				return;
			}
			
			if (itemMatch.length() < 3)
			{
				sendToDiscordMC(null,targetChannelId,"Item search must be at least 3 characters: " + itemMatch);
				return;
			}
			
			List<ISoliniaItem> items = StateManager.getInstance().getConfigurationManager().getItemsByPartialName(itemMatch);
			
			if (items.size() > 20)
			{
				sendToDiscordMC(null,targetChannelId,"Item matched more than 20 items, please be more specific than " + itemMatch);
				return;
			}
			
			if (items.size() != 1)
			{
				List<String> matchingItemList = new ArrayList<String>();
				String currentLine = "";
				for(ISoliniaItem item : items)
				{
					if ((currentLine + "[" + item.getId() + "] " + item.getDisplayname() + " ").length() > 2000)
					{
						matchingItemList.add(currentLine);
						currentLine = "";
					}
					
					currentLine += "[" + item.getId() + "] " + item.getDisplayname() + " ";
				}
				
				if (!currentLine.equals(""))
				{
					matchingItemList.add(currentLine);
				}
				
				for(String line : matchingItemList)
				{
					sendToDiscordMC(null,targetChannelId,"Item [" + itemMatch + "] matched with: " + line);
				}
			} else {
				for(ISoliniaItem item : items)
				{
					sendItemToDiscordChannel(discordChannel, item);
				}
			}
			
		} catch (CoreStateInitException e)
		{
			// ignore it
		}
		
	}

	
	private void sendItemToDiscordChannel(DiscordChannel discordChannel, ISoliniaItem item) {
		String targetChannelId = getDefaultDiscordChannel();
		if (discordChannel.equals(DiscordChannel.ADMIN))
			targetChannelId = getAdminDiscordChannel();
		
		String proc = "";
		if (item.getWeaponabilityid() > 0)
		{
			try
			{
				ISoliniaSpell spell = StateManager.getInstance().getConfigurationManager().getSpell(item.getWeaponabilityid());
				proc = "Chance to Proc on Hit: " + ChatColor.YELLOW+spell.getName() + ChatColor.RESET;
			} catch (CoreStateInitException e)
			{
				//
			}
		}
		
		sendToDiscordMC(null,targetChannelId,"Item " + item.getId() + " ("+ item.getDisplayname() + ") Base: " + item.getBasename() + " MinLevel: " + item.getMinLevel());
		sendToDiscordMC(null,targetChannelId,"Damage " + item.getDamage() + " UndeadBaneDmg: " + item.getBaneUndead() + " AC: " + item.getAC() + " Proc: " + proc + " Worth: $" + item.getWorth());
		sendToDiscordMC(null,targetChannelId,"Strength: " + item.getStrength() + 
				" Stamina: " + item.getStamina() + 
				" Agility: " + item.getAgility() + 
				" Dexterity: " + item.getDexterity() + 
				" Intelligence: " + item.getIntelligence() + 
				" Wisdom: " + item.getWisdom() + 
				" Charisma: " + item.getCharisma());
		sendToDiscordMC(null,targetChannelId,"DR: " + item.getDiseaseResist() + " CR: " + item.getColdResist() + " FR: " + item.getFireResist() + " PR: " + item.getPoisonResist()  + " MR: " + item.getMagicResist());
		sendToDiscordMC(null,targetChannelId,"HP: " + item.getHp() + " Mana: " + item.getMana() +  " HPRegen: " + item.getHpregen() + " MPRegen: " + item.getMpregen() + " Temporary: " + item.isTemporary() + " Augmentation " + item.isAugmentation() + " Quest: " + item.isQuest());
		sendToDiscordMC(null,targetChannelId,"Accepts Aug Type: " + item.getAcceptsAugmentationSlotType().name() + " Fits Aug Types: " + item.getAugmentationFitsSlotType().name());
	}

	private void sendLootListToDiscordChannel(DiscordChannel discordChannel, String itemMatch) {
		try
		{
			String targetChannelId = getDefaultDiscordChannel();
			if (discordChannel.equals(DiscordChannel.ADMIN))
				targetChannelId = getAdminDiscordChannel();
			
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
				sendToDiscordMC(null,targetChannelId,"More than one item found with this string, please be more specific: " + itemMatch);
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
			String targetChannelId = getDefaultDiscordChannel();
			if (discordChannel.equals(DiscordChannel.ADMIN))
				targetChannelId = getAdminDiscordChannel();
			
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
	public void setDiscordMainChannelId(String discordmainchannelid) {
		this.discordmainchannelid = discordmainchannelid;
	}

	@Override
	public void setDiscordAdminChannelId(String discordadminchannelid) {
		this.discordadminchannelid = discordadminchannelid;
	}

	@Override
	public void processNextDiscordMessage() {
		List<Integer> messageIds = Collections.list(this.queuedDiscordMessages.keys());
		if (messageIds.size() < 1)
			return;
		
		int minIndex = Collections.min(messageIds);
		sendToDiscordQueuedMessage(minIndex);
	}

}
