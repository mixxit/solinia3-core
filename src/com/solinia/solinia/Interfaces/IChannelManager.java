package com.solinia.solinia.Interfaces;

import com.solinia.solinia.Models.DiscordChannel;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public interface IChannelManager {

	void sendToLocalChannel(ISoliniaPlayer source, String message, boolean isBardSongFilterable);

	void sendToLocalChannel(ISoliniaLivingEntity source, String message, boolean isBardSongFilterable);

	void sendToGlobalChannel(ISoliniaPlayer source, String message);

	void sendToLocalChannelDecorated(ISoliniaPlayer source, String message, String coremessage);

	void sendToGlobalChannelDecorated(ISoliniaPlayer source, String message);

	void sendToGlobalChannel(String name, String message, boolean isFromDiscord);

	void sendToOps(String name, String message, boolean isFromDiscord);

	void sendToDiscordMC(ISoliniaPlayer source, String channelId, String message);

	void handleDiscordCommand(DiscordChannel default1, MessageReceivedEvent event);

	void sendToDiscordQueuedMessage(Integer messageId);

	void processNextDiscordMessage();

	void sendToLocalChannelLivingEntityChat(ISoliniaLivingEntity source, String message, boolean allowlanguagelearn, String coremessage);

	String getChannelId(DiscordChannel discordChannel);

	String getDiscordMainChannelId();

	void setDiscordMainChannelId(String discordMainChannelId);

	String getDiscordAdminChannelId();

	void setDiscordAdminChannelId(String discordAdminChannelId);

	String getDiscordContentTeamChannelId();

	void setDiscordContentTeamChannelId(String discordContentTeamChannelId);

	String getDiscordInCharacterChannelId();

	void setDiscordInCharacterChannelId(String discordInCharacterChannelId);
}
