package com.solinia.solinia.Models;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

import com.solinia.solinia.Interfaces.ISoliniaEntity;

public class SoliniaEntity implements ISoliniaEntity {
	UUID bukkitEntityUUID;

	public SoliniaEntity(Entity entity) {
		bukkitEntityUUID = entity.getUniqueId();
	}

	@Override
	public Entity getBukkitEntity() {
		return Bukkit.getEntity(bukkitEntityUUID);
	}
}
