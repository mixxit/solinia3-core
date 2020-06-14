package com.solinia.solinia.Models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaGroup;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Utils.EntityUtils;
import com.solinia.solinia.Utils.PlayerUtils;
import com.solinia.solinia.Utils.Utils;

import net.minecraft.server.v1_15_R1.Tuple;

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

	@Override
	public void grantExperienceAndLoot(ISoliniaPlayer xpSource, double experience, ISoliniaLivingEntity killedEntity, int killerLevel) {
		try
		{
			if (xpSource.getMentorLevel() < (EntityUtils.getMinLevelFromLevel(killerLevel))) {
				xpSource.getBukkitPlayer().sendMessage(ChatColor.GRAY + "You are too low level to gain experience [due to the killer that killed this mob]" + ChatColor.RESET);
				return;
			}
			
			// TODO Auto-generated method stub
			List<Integer> levelRanges = new ArrayList<Integer>();
			// xp to players only
			for (UUID member : getMembersWithoutPets()) {
				ISoliniaPlayer playerchecked = SoliniaPlayerAdapter.Adapt(Bukkit.getPlayer(member));
				int checkedlevel = playerchecked.getMentorLevel();
				levelRanges.add(checkedlevel);
			}
			
			Tuple<Integer,Integer> lowhighlvl = PlayerUtils.GetGroupExpMinAndMaxLevel(levelRanges);
			int ilowlvl = lowhighlvl.a();
			int ihighlvl = lowhighlvl.b();
	
			if (xpSource.getMentorLevel() < ilowlvl || xpSource.getMentorLevel() > ihighlvl) {
				// Only award player the experience
				// as they are out of range of the group
				if (killedEntity.getMentorLevel() >= EntityUtils.getMinLevelFromLevel(xpSource.getMentorLevel())) {
					// Due to being out of range they get the full xp
					xpSource.increasePlayerExperience(experience, true, true);
					if (xpSource.getFellowship() != null)
						xpSource.grantFellowshipXPBonusToFellowship(experience);
	
					// Grant title for killing mob
					if (killedEntity.getNpcid() > 0) {
						ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager()
								.getNPC(killedEntity.getNpcid());
						if (npc != null && !npc.getDeathGrantsTitle().equals("")) {
							xpSource.grantTitle(npc.getDeathGrantsTitle());
						}
	
						if (npc.isBoss() || npc.isRaidboss()) {
							xpSource.grantTitle("the Vanquisher");
						}
					}
	
				} 
	
			} else {
				// we only only count player xp
				double experienceReward = experience / getMembersWithoutPets().size();
				double groupBonus = (experienceReward/100)*(getMembersWithoutPets().size()*10);
				
				List<Integer> awardsFellowshipIds = new ArrayList<Integer>();
				
				// players get xp only
				for (UUID member : getMembersWithoutPets()) {
					Player tgtplayer = Bukkit.getPlayer(member);
					if (tgtplayer != null) {
						ISoliniaPlayer tgtsolplayer = SoliniaPlayerAdapter.Adapt(tgtplayer);
						
						if (tgtsolplayer.getMentorLevel() < (EntityUtils.getMinLevelFromLevel(killerLevel))) {
							tgtplayer.sendMessage(ChatColor.GRAY + "You are too low level to gain experience [due to the killer that killed this mob]" + ChatColor.RESET);
							return;
						}
						
						int tgtlevel = tgtsolplayer.getMentorLevel();
	
						if (tgtlevel < ilowlvl || tgtlevel > ihighlvl) {
							tgtplayer.sendMessage(ChatColor.GRAY
									+ "You were out of level range to gain experience in this group (Min: "
									+ ilowlvl + " Max: " + ihighlvl + ")");
							continue;
						}
	
						if (!tgtplayer.getWorld().equals(xpSource.getBukkitPlayer().getWorld())) {
							tgtplayer.sendMessage("You were out of range for shared group xp (world)");
							continue;
						}
	
						if (tgtplayer.getLocation().distance(xpSource.getBukkitPlayer().getLocation()) <= Utils.MaxRangeForExperience) {
							if (killedEntity.getMentorLevel() >= (EntityUtils.getMinLevelFromLevel(tgtsolplayer.getMentorLevel()))) {
								// they split the overall experience across the group size
								
								// add 10% bonus per player
								
								tgtsolplayer.increasePlayerExperience(experienceReward+groupBonus, true, true);
								
								if (tgtsolplayer.getFellowship() != null)
								if (!awardsFellowshipIds.contains(tgtsolplayer.getFellowship().getId()))
								{
									awardsFellowshipIds.add(tgtsolplayer.getFellowship().getId());
									tgtsolplayer.grantFellowshipXPBonusToFellowship(experience);
								}
	
								// Grant title for killing mob
								if (killedEntity.getNpcid() > 0) {
									ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager()
											.getNPC(killedEntity.getNpcid());
									if (npc != null && !npc.getDeathGrantsTitle().equals("")) {
										tgtsolplayer.grantTitle(npc.getDeathGrantsTitle());
									}
	
									if (npc.isBoss() || npc.isRaidboss()) {
										tgtsolplayer.grantTitle("the Vanquisher");
									}
								}
	
							} else {
								// The npc was too low level to gain
								// experience from - Was: " + livingEntity.getLevel() + " Min: " +
								// EntityUtils.getMinLevelFromLevel(tgtsolplayer.getLevel()));
							}
	
						} else {
							// tgtplayer.sendMessage(ChatColor.GRAY + "You were out of range for shared
							// group xp (distance)");
							continue;
						}
					}
				}
			}
		} catch (CoreStateInitException e)
		{
			
		}
	}
}
