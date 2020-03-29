package com.solinia.solinia.Models;

import java.util.UUID;

public class PlayerState {
	private UUID id;
	private int characterId;
		
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
}
