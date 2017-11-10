package com.solinia.solinia.Models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaAlignment;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Utils.Utils;

import net.md_5.bungee.api.ChatColor;

public class SoliniaAlignment implements ISoliniaAlignment {

	private int id;
	private String name;
	private UUID emperor;
	
	@Override
	public int getId() {
		return id;
	}
	
	@Override
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name.toUpperCase();
	}

	@Override
	public UUID getEmperor() {
		return emperor;
	}

	@Override
	public void setEmperor(UUID emperor) {
		try {
			String playerName = StateManager.getInstance().getPlayerManager().getPlayerNameByUUID(emperor);
			if (playerName != null && !playerName.equals(""))
			{
				this.emperor = emperor;
				Bukkit.broadcastMessage(ChatColor.RED + playerName + " has been declared Emperor! (" + getName() + ")");
			}
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
