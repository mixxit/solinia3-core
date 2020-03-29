package com.solinia.solinia.Models;

import java.util.UUID;

public class PlayerState {
	private UUID id;
	private UUID activeCharacterId;
		
	public UUID getActiveCharacterId() {
		return activeCharacterId;
	}
	public void setActiveCharacterId(UUID activeCharacterId) {
		this.activeCharacterId = activeCharacterId;
	}
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
}
