package com.solinia.solinia.Models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.FellowshipMemberNotFoundException;
import com.solinia.solinia.Exceptions.PlayerDoesNotExistException;
import com.solinia.solinia.Interfaces.IPersistable;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Utils.PlayerUtils;

import net.md_5.bungee.api.ChatColor;

public class Fellowship implements IPersistable {
	private int Id = 0;
	private List<UUID> members = new ArrayList<UUID>();
	private UUID ownerUuid = null;
	private UUID primaryUUID;
	private UUID secondaryUUID;
	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
	}
	public List<UUID> getMembers() {
		return members;
	}
	public void setMembers(List<UUID> members) {
		this.members = members;
	}
	public UUID getOwnerUuid() {
		return ownerUuid;
	}
	public void setOwnerUuid(UUID ownerUuid) {
		this.ownerUuid = ownerUuid;
	}
	public void sendMessage(ISoliniaPlayer player, String message) {
		if (player.getFellowship() == null)
			return;
		
		if (player.getCharacterFellowshipId() != this.getId())
			return;
		
		if (message == null || message.equals("")) {
			player.getBukkitPlayer().sendMessage("You cannot send an empty message");
			return;
		}
		
		String type = "";
		if (getOwnerUuid().equals(player.getCharacterId()))
		{
			type = "[" + ChatColor.LIGHT_PURPLE + "L" + ChatColor.RESET + "]";
		} else {
			type = "[" + ChatColor.LIGHT_PURPLE + "M" + ChatColor.RESET + "]";
		}

		if (getMembers().size() > 0) {
			for (UUID memberid : getMembers()) {
				try
				{
					Player member = getMemberPlayerIfOnline(memberid);
					if (member != null) {
						System.out.println(
								"fellowship: " + getId() + ":" + player.getFullName() + ": " + ChatColor.AQUA + message);
						member.sendMessage(type + " " + ChatColor.WHITE + player.getFullName() + ChatColor.RESET + " ["+ChatColor.RED + (int)player.getBukkitPlayer().getHealth()+ ChatColor.RESET + "/" +ChatColor.BLUE + player.getMana() + ChatColor.RESET+ "]: " + ChatColor.LIGHT_PURPLE + message);
					}
				} catch (FellowshipMemberNotFoundException e)
				{
					continue;
				}
			}
		} else {
			player.getBukkitPlayer().sendMessage("There were no members of that fellowship");
			return;
		}
	}
	
	public void grantFellowshipXPBonus(double experience, int ilowlvl, int ihighlvl) {
		experience = experience / (double)getMembers().size();
		for (UUID memberid : getMembers())
		{
			try
			{
				ISoliniaPlayer character = StateManager.getInstance().getPlayerManager().getArchivedCharacterOrActivePlayerByCharacterUUID(memberid);
				if (character == null)
					continue;
				
				// No longer grant xp if they are max
				if (character.getExperience() >= StateManager.getInstance().getConfigurationManager().getMaxExperience())
					continue;
				
				// Out of range for xp
				if (character.getLevel() < ilowlvl)
					continue;
				
				if (character.getPendingXp() >= PlayerUtils.getMaxAAXP())
				{
					Player player = Bukkit.getPlayer(character.getUUID());
					if (player != null)
					player.sendMessage("You have exceeded your maximum pending XP for fellowship rewards! Please /claimxp your additional XP before more can be gained (max: " + character.getPendingXp().longValue() + ")");
				} else {
					if (experience < 0)
						experience = 1d;
					
					character.addXpToPendingXp(experience);
				}
			} catch (PlayerDoesNotExistException e)
			{
				continue;
			} catch (CoreStateInitException e) {
				continue;
			}
			
		}
	}
	
	public boolean isPlayerAlreadyInFellowship(Player player)
	{
		if (getMembers().size() < 1)
			return false;
		
		try
		{
			for (UUID memberid : getMembers()) {
				try
				{
					ISoliniaPlayer character = StateManager.getInstance().getPlayerManager().getArchivedCharacterOrActivePlayerByCharacterUUID(memberid);
					if (character == null)
						continue;
					
					if (character.getUUID().equals(player.getUniqueId()))
						return true;
					} catch (PlayerDoesNotExistException e)
					{
						continue;
					}
			}
		} catch (CoreStateInitException e)
		{
			
		}
		
		return false;
	}
	
	public Player getMemberPlayerIfOnline(UUID memberCharacter) throws FellowshipMemberNotFoundException {
		if (!this.members.contains(memberCharacter))
			throw new FellowshipMemberNotFoundException();
		
		try {
			
			
			try
			{
				ISoliniaPlayer character = StateManager.getInstance().getPlayerManager().getArchivedCharacterOrActivePlayerByCharacterUUID(memberCharacter);
				if (character == null)
					return null;
				
				Player player = Bukkit.getPlayer(character.getUUID());
				
				if (player == null)
					return null;
				
				ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
				if (solPlayer == null)
					return null;
				
				if (!solPlayer.getCharacterId().equals(character.getCharacterId()))
					return null;
				
				return player;
			} catch (PlayerDoesNotExistException e)
			{
				return null;
			}
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public void sendGroupList(ISoliniaPlayer player) throws FellowshipMemberNotFoundException {
		if (!this.members.contains(player.getCharacterId()))
			throw new FellowshipMemberNotFoundException();

		try
		{
			if (getMembers().size() > 0) {
				for (UUID memberCharacterId : getMembers()) {
					try
					{
						boolean online = this.getMemberPlayerIfOnline(memberCharacterId) != null;
						ISoliniaPlayer member = StateManager.getInstance().getPlayerManager().getArchivedCharacterOrActivePlayerByCharacterUUID(memberCharacterId);
						if (member != null) {
							String leader = " - Member - Online: " + online;
							if (member.getCharacterId().equals(this.getOwnerUuid())) {
								leader = " - Leader - Online: " + online;
							}
							
							player.getBukkitPlayer().sendMessage(member.getFullName() + leader);
						}
					} catch (PlayerDoesNotExistException e)
					{
						continue;
					}
				}
			} else {
				player.getBukkitPlayer().sendMessage("Your fellowship has no members");
			}
		} catch (CoreStateInitException e)
		{
			
		}
	}
	
	public void removePlayer(ISoliniaPlayer memberPlayer) {
		if (!this.members.contains(memberPlayer.getCharacterId()))
		{
			System.out.println("Could not remove member htat was not in fellowship");
			return;
		}
		
		StateManager.getInstance().removeFellowshipInvite(memberPlayer);
		
		System.out.println("fellowship: " + getId() + " lost a member: " + memberPlayer.getFullName());
		sendMessage(memberPlayer, "has left the fellowship!");
		
		this.members.remove(memberPlayer.getCharacterId());
		memberPlayer.setCharacterFellowshipId(0);

		if (getOwnerUuid().equals(memberPlayer.getCharacterId())) 
			trySetNextLeader();

		destroyIfEmpty();
	}
	
	public void addPlayer(ISoliniaPlayer player) {
		if (this.members.contains(player.getCharacterId()))
			return;
		
		StateManager.getInstance().removeFellowshipInvite(player);
		
		this.members.add(player.getCharacterId());
		player.setCharacterFellowshipId(this.getId());
		System.out.println("fellowship: " + getId() + " gained a member: " + player.getFullName());
		sendMessage(player, "has joined the fellowship!");
	}
	
	public boolean trySetNextLeader()
	{
		if (this.members.size() < 1)
			return false;
		
		try
		{
			for (int i = 0; i < this.members.size(); i++) {
				try
				{
					UUID newownerCharacterId = this.members.get(i);
					ISoliniaPlayer member = StateManager.getInstance().getPlayerManager().getArchivedCharacterOrActivePlayerByCharacterUUID(newownerCharacterId);
					
					Player player = Bukkit.getPlayer(member.getUUID());
					
					if (player == null)
						continue;
					
					ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
					if (solPlayer == null)
						continue;
					
					if (!solPlayer.getCharacterId().equals(member.getCharacterId()))
						continue;
					
					if (member != null) {
						setOwnerUuid(newownerCharacterId);
						System.out.println(
								"fellowship: " + getId() + " has a new leader: " + member.getFullName());
						sendMessage(member, "is now the fellowship leader!");
						return true;
					}
				} catch (PlayerDoesNotExistException e)
				{
					continue;
				}
			}
			
			// If we got this far we couldnt set the new leader so kick everyone out
			List<UUID> charsToRemove = new ArrayList<UUID>();
			
			for (int i = 0; i < this.members.size(); i++) {
				charsToRemove.add(this.members.get(i));
			}

			for(UUID charUuidToRemove : charsToRemove)
			{
				ISoliniaPlayer member = null;
				try {
					member = StateManager.getInstance().getPlayerManager().getArchivedCharacterOrActivePlayerByCharacterUUID(charUuidToRemove);
				} catch (PlayerDoesNotExistException e) {
					
				}
				if (member == null)
					continue;
				
				member.setCharacterFellowshipId(0);
				
				Player player = Bukkit.getPlayer(member.getUUID());
				if (player == null)
					continue;
				
				player.sendMessage("You have left the fellowship");
			}
			this.members.clear();
			
			return false;
				
		} catch (CoreStateInitException e)
		{
			return false;
		}
	}
		
	public void destroyIfEmpty() {
		if (this.members.size() > 0)
			return;
		
		try {
			StateManager.getInstance().getConfigurationManager().removeFellowship(getId());
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
	}
	public List<UUID> getMembersOnline() {
		List<UUID> membersOnline  = new ArrayList<UUID>();
		for (UUID member : getMembers())
		{
			try {
				if (this.getMemberPlayerIfOnline(member) != null)
					membersOnline.add(member);
			} catch (FellowshipMemberNotFoundException e) {
				
			}
		}
		
		return membersOnline;
	}
	
	@Override
	public UUID getPrimaryUUID() {
		// TODO Auto-generated method stub
		return this.primaryUUID;
	}
	@Override
	public void setPrimaryUUID(UUID uuid) {
		// TODO Auto-generated method stub
		this.primaryUUID = uuid;
	}
	@Override
	public UUID getSecondaryUUID() {
		// TODO Auto-generated method stub
		return this.secondaryUUID;
	}
	@Override
	public void setSecondaryUUID(UUID uuid) {
		// TODO Auto-generated method stub
		this.secondaryUUID = uuid;
	}
}
