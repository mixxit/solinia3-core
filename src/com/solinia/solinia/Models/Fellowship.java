package com.solinia.solinia.Models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.FellowshipMemberNotFoundException;
import com.solinia.solinia.Interfaces.IPersistable;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Utils.PlayerUtils;
import com.solinia.solinia.Utils.Utils;

import net.md_5.bungee.api.ChatColor;

public class Fellowship implements IPersistable {
	private int Id = 0;
	private List<Integer> memberCharacterIds = new ArrayList<Integer>();
	private int ownerCharacterId = 0;
	private UUID primaryUUID;
	private UUID secondaryUUID;
	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
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
		if (getOwnerCharacterId() == player.getId())
		{
			type = "[" + ChatColor.LIGHT_PURPLE + "L" + ChatColor.RESET + "]";
		} else {
			type = "[" + ChatColor.LIGHT_PURPLE + "M" + ChatColor.RESET + "]";
		}

		if (getMemberCharacterIds().size() > 0) {
			for (int memberid : getMemberCharacterIds()) {
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
		experience = experience / (double)getMemberCharacterIds().size();
		for (int memberid : getMemberCharacterIds())
		{
			try
			{
				ISoliniaPlayer character = StateManager.getInstance().getConfigurationManager().getCharacterById(memberid);
				if (character == null)
					continue;
				
				// No longer grant xp if they are max
				if (character.getExperience() >= StateManager.getInstance().getConfigurationManager().getMaxExperience())
					continue;
				
				// Out of range for xp
				if (character.getLevel() < ilowlvl)
					continue;
				
				if (character.getPendingXp() >= PlayerUtils.getMaxFellowshipXP())
				{
					Player player = Bukkit.getPlayer(character.getOwnerUUID());
					if (player != null)
						Utils.SendHint(player, HINT.EXCEEDED_CLAIMXP, Long.toString(character.getPendingXp().longValue()), false);
				} else {
					if (experience < 0)
						experience = 1d;
					
					character.addXpToPendingXp(experience);
				}
			} catch (CoreStateInitException e) {
				continue;
			}
			
		}
	}
	
	public boolean isPlayerAlreadyInFellowship(Player player)
	{
		if (getMemberCharacterIds().size() < 1)
			return false;
		
		try
		{
			for (int memberid : getMemberCharacterIds()) {
				ISoliniaPlayer character = StateManager.getInstance().getConfigurationManager().getCharacterById(memberid);
				if (character == null)
					continue;
				
				if (character.getOwnerUUID().equals(player.getUniqueId()))
					return true;
			}
		} catch (CoreStateInitException e)
		{
			
		}
		
		return false;
	}
	
	public Player getMemberPlayerIfOnline(int memberCharacter) throws FellowshipMemberNotFoundException {
		if (!this.getMemberCharacterIds().contains(memberCharacter))
			throw new FellowshipMemberNotFoundException();
		
		try {
			
			
			ISoliniaPlayer character = StateManager.getInstance().getConfigurationManager().getCharacterById(memberCharacter);
			if (character == null)
				return null;
			
			Player player = Bukkit.getPlayer(character.getOwnerUUID());
			
			if (player == null)
				return null;
			
			ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
			if (solPlayer == null)
				return null;
			
			if (solPlayer.getId() != character.getId())
				return null;
			
			return player;
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public void sendGroupList(ISoliniaPlayer player) throws FellowshipMemberNotFoundException {
		if (!this.getMemberCharacterIds().contains(player.getId()))
			throw new FellowshipMemberNotFoundException();

		try
		{
			if (getMemberCharacterIds().size() > 0) {
				for (int memberCharacterId : getMemberCharacterIds()) {
					boolean online = this.getMemberPlayerIfOnline(memberCharacterId) != null;
					ISoliniaPlayer member = StateManager.getInstance().getConfigurationManager().getCharacterById(memberCharacterId);
					if (member != null) {
						String leader = " - Member - Online: " + online;
						if (member.getId() == this.getOwnerCharacterId()) {
							leader = " - Leader - Online: " + online;
						}
						
						player.getBukkitPlayer().sendMessage(member.getFullName() + leader);
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
		if (!this.getMemberCharacterIds().contains(memberPlayer.getId()))
		{
			System.out.println("Could not remove member htat was not in fellowship");
			return;
		}
		
		StateManager.getInstance().removeFellowshipInvite(memberPlayer);
		
		System.out.println("fellowship: " + getId() + " lost a member: " + memberPlayer.getFullName());
		sendMessage(memberPlayer, "has left the fellowship!");
		
		this.getMemberCharacterIds().remove(memberPlayer.getId());
		memberPlayer.setCharacterFellowshipId(0);

		if (getOwnerCharacterId() == memberPlayer.getId()) 
			trySetNextLeader();

		destroyIfEmpty();
	}
	
	public void addPlayer(ISoliniaPlayer player) {
		if (this.getMemberCharacterIds().contains(player.getId()))
			return;
		
		StateManager.getInstance().removeFellowshipInvite(player);
		
		this.getMemberCharacterIds().add(player.getId());
		player.setCharacterFellowshipId(this.getId());
		System.out.println("fellowship: " + getId() + " gained a member: " + player.getFullName());
		sendMessage(player, "has joined the fellowship!");
	}
	
	public boolean trySetNextLeader()
	{
		if (this.getMemberCharacterIds().size() < 1)
			return false;
		
		try
		{
			for (int i = 0; i < this.getMemberCharacterIds().size(); i++) {
				int newownerCharacterId = this.getMemberCharacterIds().get(i);
				ISoliniaPlayer member = StateManager.getInstance().getConfigurationManager().getCharacterById(newownerCharacterId);
				
				Player player = Bukkit.getPlayer(member.getOwnerUUID());
				
				if (player == null)
					continue;
				
				ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
				if (solPlayer == null)
					continue;
				
				if (solPlayer.getId() != member.getId())
					continue;
				
				if (member != null) {
					setOwnerCharacterId(newownerCharacterId);
					System.out.println(
							"fellowship: " + getId() + " has a new leader: " + member.getFullName());
					sendMessage(member, "is now the fellowship leader!");
					return true;
				}
			}
			
			// If we got this far we couldnt set the new leader so kick everyone out
			List<Integer> charsToRemove = new ArrayList<Integer>();
			
			for (int i = 0; i < this.getMemberCharacterIds().size(); i++) {
				charsToRemove.add(this.getMemberCharacterIds().get(i));
			}

			for(int charUuidToRemove : charsToRemove)
			{
				ISoliniaPlayer member = null;
				member = StateManager.getInstance().getConfigurationManager().getCharacterById(charUuidToRemove);
				if (member == null)
					continue;
				
				member.setCharacterFellowshipId(0);
				
				Player player = Bukkit.getPlayer(member.getOwnerUUID());
				if (player == null)
					continue;
				
				player.sendMessage("You have left the fellowship");
			}
			this.getMemberCharacterIds().clear();
			
			return false;
				
		} catch (CoreStateInitException e)
		{
			return false;
		}
	}
		
	public void destroyIfEmpty() {
		if (this.getMemberCharacterIds().size() > 0)
			return;
		
		try {
			StateManager.getInstance().getConfigurationManager().removeFellowship(getId());
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
	}
	
	public List<Integer> getMembersOnline() {
		List<Integer> membersOnline  = new ArrayList<Integer>();
		for (int member : getMemberCharacterIds())
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
	public int getOwnerCharacterId() {
		return ownerCharacterId;
	}
	public void setOwnerCharacterId(int ownerCharacterId) {
		this.ownerCharacterId = ownerCharacterId;
	}
	public List<Integer> getMemberCharacterIds() {
		return memberCharacterIds;
	}
	public void setMemberCharacterIds(List<Integer> memberCharacterIds) {
		this.memberCharacterIds = memberCharacterIds;
	}
}
