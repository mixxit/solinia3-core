package com.solinia.solinia.Interfaces;

import org.bukkit.inventory.ItemStack;

import com.solinia.solinia.Models.DiscordChannel;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public interface IChannelManager {

	void sendToLocalChannel(ISoliniaPlayer source, String message, boolean isBardSongFilterable, ItemStack itemStack);

	void sendToLocalChannel(ISoliniaLivingEntity source, String message, boolean isBardSongFilterable, ItemStack itemStack);

	void sendToGlobalChannel(ISoliniaPlayer source, String message, ItemStack itemStack);

	void sendToLocalChannelDecorated(ISoliniaPlayer source, String message, String coremessage, ItemStack itemStack);

	void sendToGlobalChannelDecorated(ISoliniaPlayer source, String message, ItemStack itemStack);

	void sendToGlobalChannel(String name, String message, boolean isFromDiscord, ItemStack itemStack);

	void sendToOps(String name, String message, boolean isFromDiscord, ItemStack itemStack);

	void sendToDiscordMC(ISoliniaPlayer source, String channelId, String message);

	void handleDiscordCommand(DiscordChannel default1, MessageReceivedEvent event);

	void sendToDiscordQueuedMessage(Integer messageId);

	void processNextDiscordMessage();

	void sendToLocalChannelLivingEntityChat(ISoliniaLivingEntity source, String message, boolean allowlanguagelearn, String coremessage, ItemStack itemStack);

	String getChannelId(DiscordChannel discordChannel);

	String getDiscordMainChannelId();

	void setDiscordMainChannelId(String discordMainChannelId);

	String getDiscordAdminChannelId();

	void setDiscordAdminChannelId(String discordAdminChannelId);

	String getDiscordContentTeamChannelId();

	void setDiscordContentTeamChannelId(String discordContentTeamChannelId);
	
	String getDiscordBotspamChannelId();
	
	String getDiscordInCharacterChannelId();

	void setDiscordInCharacterChannelId(String discordInCharacterChannelId);

	void clearDiscordQueue();

	void setDiscordBotspamChannelId(String discordBotspamChannelId);

	void sendToWhisperChannelDecorated(ISoliniaPlayer source, String message, String coremessage, ItemStack itemStack);

	void sendToShoutChannelDecorated(ISoliniaPlayer source, String message, String coremessage, ItemStack itemStack);
}
