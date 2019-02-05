package com.solinia.solinia.Interfaces;

import org.bukkit.inventory.ItemStack;

public interface IChannelManager {

	void sendToLocalChannel(ISoliniaPlayer source, String message, boolean isBardSongFilterable, ItemStack itemStack);

	void sendToLocalChannel(ISoliniaLivingEntity source, String message, boolean isBardSongFilterable, ItemStack itemStack);

	void sendToGlobalChannel(ISoliniaPlayer source, String message, ItemStack itemStack);

	void sendToLocalChannelDecorated(ISoliniaPlayer source, String message, String coremessage, ItemStack itemStack);

	void sendToGlobalChannelDecorated(ISoliniaPlayer source, String message, ItemStack itemStack);

	void sendToGlobalChannel(String name, String message, ItemStack itemStack);

	void sendToOps(String name, String message, ItemStack itemStack);

	void sendToLocalChannelLivingEntityChat(ISoliniaLivingEntity source, String message, boolean allowlanguagelearn, String coremessage, ItemStack itemStack);

	void sendToWhisperChannelDecorated(ISoliniaPlayer source, String message, String coremessage, ItemStack itemStack);

	void sendToShoutChannelDecorated(ISoliniaPlayer source, String message, String coremessage, ItemStack itemStack);
}
