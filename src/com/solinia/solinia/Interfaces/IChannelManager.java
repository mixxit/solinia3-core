package com.solinia.solinia.Interfaces;

import com.solinia.solinia.Models.DiscordChannel;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public interface IChannelManager {

	void sendToLocalChannel(ISoliniaPlayer source, String message);

	void sendToLocalChannel(ISoliniaLivingEntity source, String message);

	void sendToGlobalChannel(ISoliniaPlayer source, String message);

	void sendToLocalChannelDecorated(ISoliniaPlayer source, String message);

	void sendToGlobalChannelDecorated(ISoliniaPlayer source, String message);

	void sendToGlobalChannel(String name, String message, boolean isFromDiscord);

	void sendToOps(String name, String message, boolean isFromDiscord);

	void sendToDiscordMC(ISoliniaPlayer source, String channelId, String message);

	String getDefaultDiscordChannel();

	String getAdminDiscordChannel();

	String getInCharacterDiscordChannel();

	void handleDiscordCommand(DiscordChannel default1, MessageReceivedEvent event);

	void setDiscordMainChannelId(String discordmainchannelid);

	void setDiscordAdminChannelId(String discordadminchannelid);
	void sendToDiscordQueuedMessage(Integer messageId);

	void processNextDiscordMessage();

	void setDiscordInCharacterChannelId(String discordincharacterchannelid);

	void sendToLocalChannelLivingEntityChat(ISoliniaLivingEntity source, String message, boolean allowlanguagelearn);
}
