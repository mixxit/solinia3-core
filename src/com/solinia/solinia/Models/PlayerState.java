package com.solinia.solinia.Models;

import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.solinia.solinia.Utils.ItemStackUtils;

public class PlayerState {
	private UUID id;
	private int characterId;
	private String base64EnderChestContents = "";
	
	public int getCharacterId() {
		return characterId;
	}
	public void setCharacterId(int characterId) {
		this.characterId = characterId;
	}
	
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
	public String getBase64EnderChestContents() {
		return base64EnderChestContents;
	}
	public void setBase64EnderChestContents(String base64EnderChestContents) {
		this.base64EnderChestContents = base64EnderChestContents;
	}
	
	public void storeEnderChestContents() {
		Player player = Bukkit.getPlayer(id);
		
		this.setBase64EnderChestContents(new String(Base64.encodeBase64(ItemStackUtils
				.itemStackArrayToYamlString(player.getEnderChest().getContents()).getBytes())));
	}
}
