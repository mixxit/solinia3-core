package com.solinia.solinia.Models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.solinia.solinia.Interfaces.ISoliniaGroup;
import com.solinia.solinia.Managers.StateManager;

public class SoliniaGroup implements ISoliniaGroup {

	private UUID id;
	private UUID owner;
	private List<UUID> members = new ArrayList<UUID>();
	
	@Override
	public void invitePlayer(Player player) {
		StateManager.getInstance().invitePlayerToGroup(Bukkit.getPlayer(owner), player);
	}

	@Override
	public void removePlayer(Player player) {
		StateManager.getInstance().removePlayerFromGroup(player);
	}

	@Override
	public void sendGroupList(Player player) {
		ISoliniaGroup group = StateManager.getInstance().getGroupByMember(player.getUniqueId());
		if (group == null) {
			player.sendMessage("Your group doesn't exist");
			return;
		}

		if (group.getMembers().size() > 0) {
			for (UUID memberid : group.getMembers()) {
				Player member = Bukkit.getPlayer(memberid);
				if (member != null) {
					String leader = "";
					if (member.getUniqueId().equals(group.getId())) {
						leader = " - Leader";
					}
					player.sendMessage(member.getDisplayName() + leader);
				}
			}
		} else {
			player.sendMessage("Your group has no members");
		}
	}

	@Override
	public UUID getOwner() {
		// TODO Auto-generated method stub
		return this.owner;
	}

	@Override
	public List<UUID> getMembers() {
		return this.members;
	}

	@Override
	public void sendGroupMessage(Player player, String message) {
		StateManager.getInstance().sendGroupMessage(player, message);
	}

	@Override
	public void setId(UUID newgroupid) {
		this.id = newgroupid;
	}

	@Override
	public void setOwner(UUID uniqueId) {
		this.owner = uniqueId;
	}

	@Override
	public UUID getId() {
		return this.id;
	}

}
