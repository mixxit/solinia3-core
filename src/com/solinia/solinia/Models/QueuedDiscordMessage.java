package com.solinia.solinia.Models;

import java.util.UUID;

public class QueuedDiscordMessage {
	private UUID playerUuid;
	private String channelId;
	private String message;
	private boolean isSent;
	
	public QueuedDiscordMessage(UUID playerUuid, String channelId, String message) {
		this.setPlayerUuid(playerUuid);
		this.setChannelId(channelId);
		this.setMessage(message);
		this.isSent = false;
	}

	public UUID getPlayerUuid() {
		return playerUuid;
	}

	public void setPlayerUuid(UUID playerUuid) {
		this.playerUuid = playerUuid;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isSent() {
		return isSent;
	}

	public void setSent(boolean isSent) {
		this.isSent = isSent;
	}
}
