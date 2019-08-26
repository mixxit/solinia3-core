package com.solinia.solinia.Utils;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaGroup;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Models.Solinia3UIChannelNames;
import com.solinia.solinia.Models.Solinia3UIPacketDiscriminators;

public class PartyWindowUtils {
	
	public static void UpdateWindow(Player player) {
		try {
			// myself
			ISoliniaLivingEntity soliniaLivingEntity = SoliniaLivingEntityAdapter.Adapt(player);
			ForgeUtils.sendForgeMessage(player,Solinia3UIChannelNames.Outgoing,Solinia3UIPacketDiscriminators.VITALS,soliniaLivingEntity.toPacketMobVitals(0).toPacketData());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void SendGroupToMember(Player player, ISoliniaGroup group)
	{
		UpdateWindow(player);
		
		try {
			if (group != null)
			{
				for (int i = 0; i < group.getMembersWithoutPlayer(player).size(); i++) {
					UUID uuid = group.getMembersWithoutPlayer(player).get(i);
					if (uuid == null)
						continue;
					
					Entity entity = Bukkit.getEntity(uuid);
					if (entity == null)
						continue; 
					
					LivingEntity le = (LivingEntity)entity;
					if (le == null || !(le instanceof Player))
						continue;
					
					ISoliniaLivingEntity soliniaLivingEntity = SoliniaLivingEntityAdapter.Adapt(le);
					if (soliniaLivingEntity == null)
						continue;
					
					ForgeUtils.sendForgeMessage(player,Solinia3UIChannelNames.Outgoing,Solinia3UIPacketDiscriminators.VITALS,soliniaLivingEntity.toPacketMobVitals(i+1).toPacketData());
				}
			}
		} catch (CoreStateInitException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public static void UpdateGroupWindow(UUID uuid, ISoliniaGroup group) {
		Player player = Bukkit.getPlayer(uuid);
		if (player == null)
			return;
		
		try {
			SendGroupToMember(player, group);
			// Now send to groupies, remember to change the party members nad Me
			if (group != null)
			for (UUID groupmemberuuid : group.getMembers()) {
				Player sendToPlayer = Bukkit.getPlayer(groupmemberuuid);
				SendGroupToMember(sendToPlayer, group);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void UpdateGroupWindowForEveryone(UUID uniqueId, ISoliniaGroup group) {
		PartyWindowUtils.UpdateGroupWindow(uniqueId, group);
		if (group != null) {
			for (UUID uuid : group.getMembers()) {
				PartyWindowUtils.UpdateGroupWindow(uuid, group);
			}
		}

	}
}
