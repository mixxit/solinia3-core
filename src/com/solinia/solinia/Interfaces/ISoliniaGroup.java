package com.solinia.solinia.Interfaces;

import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

public interface ISoliniaGroup {

	void invitePlayer(Player player);

	void removePlayer(Player player);

	void sendGroupList(Player player);

	UUID getOwner();

	void sendGroupMessage(Player player, String string);

	void setId(UUID newgroupid);

	void setOwner(UUID uniqueId);

	UUID getId();

	List<UUID> getMembersWithoutPlayer(Player player);

	List<UUID> getMembersWithoutPets();

	List<UUID> getUnmodifiableGroupMembersForBuffs(boolean includePets, boolean filterOutNonPetAffinityPets);

}
