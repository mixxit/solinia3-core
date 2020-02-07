package com.solinia.solinia.Models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.FellowshipMemberNotFoundException;
import com.solinia.solinia.Interfaces.ISoliniaGroup;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Utils.PartyWindowUtils;

import net.md_5.bungee.api.ChatColor;

public class Fellowship {
	private int Id = 0;
	private List<UUID> members = new ArrayList<UUID>();
	private UUID ownerUuid = null;
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
		
		if (player.getFellowshipId() != this.getId())
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
	
	public boolean isPlayerAlreadyInFellowship(Player player)
	{
		if (getMembers().size() < 1)
			return false;
		
		try
		{
			for (UUID memberid : getMembers()) {
				ISoliniaPlayer character = StateManager.getInstance().getPlayerManager().getCharacterByCharacterUUID(memberid);
				if (character == null)
					continue;
				
				if (character.getUUID().equals(player.getUniqueId()))
					return true;
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
			ISoliniaPlayer character = StateManager.getInstance().getPlayerManager().getCharacterByCharacterUUID(memberCharacter);
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
					ISoliniaPlayer member = StateManager.getInstance().getPlayerManager().getCharacterByCharacterUUID(memberCharacterId);
					if (member != null) {
						String leader = "";
						if (member.getCharacterId().equals(this.getOwnerUuid())) {
							leader = " - Leader";
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
		if (!this.members.contains(memberPlayer.getCharacterId()))
		{
			System.out.println("Could not remove member htat was not in fellowship");
			return;
		}
		
		StateManager.getInstance().removeFellowshipInvite(memberPlayer);
		
		System.out.println("fellowship: " + getId() + " lost a member: " + memberPlayer.getFullName());
		sendMessage(memberPlayer, "has left the fellowship!");
		
		this.members.remove(memberPlayer.getCharacterId());
		memberPlayer.setFellowshipId(0);

		try
		{
			if (getOwnerUuid().equals(memberPlayer.getCharacterId())) {
				if (this.members.size() > 0) {
					boolean foundnewowner = false;
	
					for (int i = 0; i < this.members.size(); i++) {
						UUID newownerCharacterId = this.members.get(i);
						ISoliniaPlayer member = StateManager.getInstance().getPlayerManager().getCharacterByCharacterUUID(newownerCharacterId);
						
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
							foundnewowner = true;
							break;
						}
					}
	
					if (foundnewowner == false) {
						memberPlayer.getBukkitPlayer().sendMessage("The fellowship has disbanded");
						StateManager.getInstance().getConfigurationManager().removeFellowship(getId());
						return;
					}
	
				} else {
					memberPlayer.getBukkitPlayer().sendMessage("The fellowship has disbanded");
					StateManager.getInstance().getConfigurationManager().removeFellowship(getId());
					return;
				}
			}
		} catch (CoreStateInitException e)
		{
			
		}
	}
	public void addPlayer(ISoliniaPlayer player) {
		if (this.members.contains(player.getCharacterId()))
			return;
		
		StateManager.getInstance().removeFellowshipInvite(player);
		
		this.members.add(player.getCharacterId());
		player.setFellowshipId(this.getId());
		System.out.println("fellowship: " + getId() + " gained a member: " + player.getFullName());
		sendMessage(player, "has joined the fellowship!");
	}
}
