package com.solinia.solinia.Models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaGroup;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
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

		if (group.getMembersWithoutPets().size() > 0) {
			for (UUID memberid : group.getMembersWithoutPets()) {
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
	public List<UUID> getMembersWithoutPets() {
		return this.members;
	}
	
	@Override
	public List<UUID> getMembersWithoutPlayer(Player player)
	{
		List<UUID> members = new ArrayList<UUID>();

		if (player == null || player.isDead())
			return members;
		
		for(UUID member : this.members)
		{
			if (member.toString().equals(player.getUniqueId().toString()))
				continue;
			
			members.add(member);
		}
		
		return members;
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

	@Override
	public final List<UUID> getUnmodifiableGroupMembersForBuffs(boolean includePets, boolean filterOutNonPetAffinityPets) {
		List<UUID> uuids = new ArrayList<UUID>(); // players first
		for(UUID playerUuid : this.getMembersWithoutPets())
		{
			Entity entity = Bukkit.getEntity(playerUuid);
			if (entity == null)
				continue;
			
			if (!(entity instanceof Player))
				continue;
			
			Player player = ((Player)entity);
			uuids.add(player.getUniqueId());
			
			if (!includePets)
				continue;
			
			try
			{
				ISoliniaLivingEntity solplayerle = SoliniaLivingEntityAdapter.Adapt(player);
				if (solplayerle == null)
					continue;
				
				if (solplayerle.getPet() == null || solplayerle.getPet().getBukkitLivingEntity() == null)
					continue;
				
				if (filterOutNonPetAffinityPets)
				if (solplayerle.getAABonuses(SpellEffectType.GivePetGroupTarget) == 0)
					continue;
				
				uuids.add(solplayerle.getPet().getBukkitLivingEntity().getUniqueId());
			} catch (CoreStateInitException e)
			{
				
			}
		}
		Collections.unmodifiableList(uuids);
		return uuids;
	}

}
