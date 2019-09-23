package com.solinia.solinia.Utils;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaGroup;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.PacketMobVitals;
import com.solinia.solinia.Models.Solinia3UIChannelNames;
import com.solinia.solinia.Models.Solinia3UIPacketDiscriminators;

public class PartyWindowUtils {
	
	public static void UpdateWindow(Player player) {
		try {
			// myself (vital: 0)
			ISoliniaLivingEntity soliniaLivingEntity = SoliniaLivingEntityAdapter.Adapt(player);
			ForgeUtils.sendForgeMessage(player,Solinia3UIChannelNames.Outgoing,Solinia3UIPacketDiscriminators.VITALS,soliniaLivingEntity.toPacketMobVitals(0).toPacketData());
			ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
			if (solPlayer != null)
			{
				ForgeUtils.sendForgeMessage(player,Solinia3UIChannelNames.Outgoing,Solinia3UIPacketDiscriminators.CASTINGPERCENT,solPlayer.toPacketCastingPercent().toPacketData());
			}

			// Has a target set (vital: -1)
			LivingEntity entityTarget = solPlayer.getEntityTarget();
			
			if (entityTarget != null) {
				ISoliniaLivingEntity soliniaLivingEntityTarget = SoliniaLivingEntityAdapter.Adapt(entityTarget);
				if (soliniaLivingEntityTarget != null)
				ForgeUtils.sendForgeMessage(player,Solinia3UIChannelNames.Outgoing,Solinia3UIPacketDiscriminators.VITALS,soliniaLivingEntityTarget.toPacketMobVitals(-1).toPacketData());
			} else {
				SendEmptyVital(player,(-1));
			}
			
			// Pet (vital: -2)
			LivingEntity entityPet = StateManager.getInstance().getEntityManager().getPet(player.getUniqueId());
			
			if (entityPet != null) {
				ISoliniaLivingEntity soliniaLivingEntityPet = SoliniaLivingEntityAdapter.Adapt(entityPet);
				if (soliniaLivingEntityPet != null)
				ForgeUtils.sendForgeMessage(player,Solinia3UIChannelNames.Outgoing,Solinia3UIPacketDiscriminators.VITALS,soliniaLivingEntityPet.toPacketMobVitals(-2).toPacketData());
			} else {
				SendEmptyVital(player,(-2));
			}

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
				for (int i = 0; i < 5; i++) {
					UUID uuid = null;
					
					if (group.getMembersWithoutPlayer(player).size() >= (i+1))
						uuid = group.getMembersWithoutPlayer(player).get(i);
					
					// 
					if (uuid == null)
					{
						SendEmptyVital(player,(i+1));
						continue;
					}
					
					Entity entity = Bukkit.getEntity(uuid);
					if (entity == null)
					{
						SendEmptyVital(player,(i+1));
						continue; 
					}
					
					LivingEntity le = (LivingEntity)entity;
					if (le == null || !(le instanceof Player))
					{
						SendEmptyVital(player,(i+1));
						continue;
					}
					
					ISoliniaLivingEntity soliniaLivingEntity = SoliniaLivingEntityAdapter.Adapt(le);
					if (soliniaLivingEntity == null)
					{
						SendEmptyVital(player,(i+1));
						continue;
					}
					
					ForgeUtils.sendForgeMessage(player,Solinia3UIChannelNames.Outgoing,Solinia3UIPacketDiscriminators.VITALS,soliniaLivingEntity.toPacketMobVitals((i+1)).toPacketData());
				}
			}
		} catch (CoreStateInitException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void SendEmptyVital(Player player, int partyMember)
	{
		PacketMobVitals vitals = new PacketMobVitals();
		vitals.fromData(partyMember, 0F, 0F, null, "");
		try {
			ForgeUtils.sendForgeMessage(player,Solinia3UIChannelNames.Outgoing,Solinia3UIPacketDiscriminators.VITALS,vitals.toPacketData());
		} catch (Exception e) {
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
