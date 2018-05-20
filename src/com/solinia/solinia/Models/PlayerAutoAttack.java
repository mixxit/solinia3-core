package com.solinia.solinia.Models;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerAutoAttack {
	UUID playerUuid;
	private boolean autoAttacking = false;
	private int timer = 20;
	private int baseAttackRate = 20;

	public PlayerAutoAttack(Player player) {
		this.playerUuid = player.getUniqueId();
	}
	
	public Player getPlayer()
	{
		return Bukkit.getPlayer(playerUuid);
	}

	public boolean isAutoAttacking() {
		return autoAttacking;
	}

	public void setAutoAttacking(boolean autoAttacking) {
		this.autoAttacking = autoAttacking;
	}

	public int getTimer() {
		return timer;
	}

	public void setTimer(int timer) {
		this.timer = timer;
	}

	public void setTimerFromAttackSpeed(int attackSpeed) {
		int rate = (int)Math.ceil(baseAttackRate + ((100d - attackSpeed) * (baseAttackRate / 100d)));
    	// Our lowest attack cap
    	if (rate < 2)
    		rate = 2;
		
    	setTimer(rate);
	}

}
