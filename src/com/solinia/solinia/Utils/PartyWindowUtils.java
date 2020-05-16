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
	public static void UpdateWindowWithoutMod(Player player,boolean withMana, boolean sendPacketImmediately) {
		try
		{
			ISoliniaLivingEntity soliniaLivingEntity = SoliniaLivingEntityAdapter.Adapt(player);
			if (soliniaLivingEntity != null)
				ScoreboardUtils.UpdateScoreboard(player, soliniaLivingEntity.getMana());
		} catch (CoreStateInitException e)
		{
			
		}
	}
	
	public static void UpdateWindowWithMod(Player player,boolean withMana, boolean sendPacketImmediately) {
		try {
			// calls scoreboard to make sure boss bar is removed
			ScoreboardUtils.UpdateScoreboard(player, 0);
			
			// myself (vital: 0)
			ISoliniaLivingEntity soliniaLivingEntity = SoliniaLivingEntityAdapter.Adapt(player);
			if (!sendPacketImmediately)
				ForgeUtils.QueueSendForgeMessage(player,Solinia3UIChannelNames.Outgoing,Solinia3UIPacketDiscriminators.VITALS,soliniaLivingEntity.toPacketMobVitals(0, withMana).toPacketData(),0);
			else
				ForgeUtils.sendForgeMessage(player,Solinia3UIChannelNames.Outgoing,Solinia3UIPacketDiscriminators.VITALS,soliniaLivingEntity.toPacketMobVitals(0, withMana).toPacketData());
			
			ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
			if (solPlayer != null)
			{
				if (!sendPacketImmediately)
				ForgeUtils.QueueSendForgeMessage(player,Solinia3UIChannelNames.Outgoing,Solinia3UIPacketDiscriminators.CASTINGPERCENT,solPlayer.toPacketCastingPercent().toPacketData(),0);
				else
					ForgeUtils.sendForgeMessage(player,Solinia3UIChannelNames.Outgoing,Solinia3UIPacketDiscriminators.CASTINGPERCENT,solPlayer.toPacketCastingPercent().toPacketData());
			}

			// Has a target set (vital: -1)
			LivingEntity entityTarget = solPlayer.getEntityTarget();
			
			if (entityTarget != null) {
				ISoliniaLivingEntity soliniaLivingEntityTarget = SoliniaLivingEntityAdapter.Adapt(entityTarget);
				if (soliniaLivingEntityTarget != null)
					if (!sendPacketImmediately)
						ForgeUtils.QueueSendForgeMessage(player,Solinia3UIChannelNames.Outgoing,Solinia3UIPacketDiscriminators.VITALS,soliniaLivingEntityTarget.toPacketMobVitals(-1, false).toPacketData(),-1);
					else
						ForgeUtils.sendForgeMessage(player,Solinia3UIChannelNames.Outgoing,Solinia3UIPacketDiscriminators.VITALS,soliniaLivingEntityTarget.toPacketMobVitals(-1, false).toPacketData());
			} else {
				SendEmptyVital(player,(-1),sendPacketImmediately);
			}
			
			// Pet (vital: -2)
			LivingEntity entityPet = StateManager.getInstance().getEntityManager().getPet(player.getUniqueId());
			
			if (entityPet != null) {
				ISoliniaLivingEntity soliniaLivingEntityPet = SoliniaLivingEntityAdapter.Adapt(entityPet);
				if (soliniaLivingEntityPet != null)
					if (!sendPacketImmediately)
						ForgeUtils.QueueSendForgeMessage(player,Solinia3UIChannelNames.Outgoing,Solinia3UIPacketDiscriminators.VITALS,soliniaLivingEntityPet.toPacketMobVitals(-2,false).toPacketData(),-2);
					else
						ForgeUtils.sendForgeMessage(player,Solinia3UIChannelNames.Outgoing,Solinia3UIPacketDiscriminators.VITALS,soliniaLivingEntityPet.toPacketMobVitals(-2,false).toPacketData());
			} else {
				SendEmptyVital(player,(-2),sendPacketImmediately);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void SendGroupToMember(Player player, ISoliniaGroup group, boolean updatemana, boolean sendEmptyGroup)
	{
		try {
			if (StateManager.getInstance().getPlayerManager().hasValidMod(player))
				UpdateWindowWithMod(player, updatemana,false);

			if (sendEmptyGroup || group != null)
			{
				for (int i = 0; i < 5; i++) {
					UUID uuid = null;
					
					if (group != null)
					if (group.getMembersWithoutPlayer(player).size() >= (i+1))
						uuid = group.getMembersWithoutPlayer(player).get(i);
					
					// 
					if (uuid == null)
					{
						SendEmptyVital(player,(i+1),false);
						continue;
					}
					
					Entity entity = Bukkit.getEntity(uuid);
					if (entity == null)
					{
						SendEmptyVital(player,(i+1),false);
						continue; 
					}
					
					LivingEntity le = (LivingEntity)entity;
					if (le == null || !(le instanceof Player))
					{
						SendEmptyVital(player,(i+1),false);
						continue;
					}
					
					ISoliniaLivingEntity soliniaLivingEntity = SoliniaLivingEntityAdapter.Adapt(le);
					if (soliniaLivingEntity == null)
					{
						SendEmptyVital(player,(i+1),false);
						continue;
					}
					
					ForgeUtils.QueueSendForgeMessage(player,Solinia3UIChannelNames.Outgoing,Solinia3UIPacketDiscriminators.VITALS,soliniaLivingEntity.toPacketMobVitals((i+1),false).toPacketData(),i+1);
				}
			}
		} catch (CoreStateInitException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void SendEmptyVital(Player player, int partyMember, boolean sendPacketImmediately)
	{
		PacketMobVitals vitals = new PacketMobVitals();
		vitals.fromData(partyMember, 0F, 0F, 0, "", 0);
		try {
			if (!sendPacketImmediately)
			ForgeUtils.QueueSendForgeMessage(player,Solinia3UIChannelNames.Outgoing,Solinia3UIPacketDiscriminators.VITALS,vitals.toPacketData(),partyMember);
			else
				ForgeUtils.sendForgeMessage(player,Solinia3UIChannelNames.Outgoing,Solinia3UIPacketDiscriminators.VITALS,vitals.toPacketData());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void UpdateGroupWindow(UUID uuid, ISoliniaGroup group, boolean updatemana, boolean sendEmptyGroup) {
		Player player = Bukkit.getPlayer(uuid);
		if (player == null)
			return;
		
		try {
			SendGroupToMember(player, group,updatemana, sendEmptyGroup);
			// Now send to groupies, remember to change the party members nad Me
			if (group != null)
			for (UUID groupmemberuuid : group.getMembers()) {
				Player sendToPlayer = Bukkit.getPlayer(groupmemberuuid);
				SendGroupToMember(sendToPlayer, group, updatemana, sendEmptyGroup);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void UpdateGroupWindowForEveryone(UUID uniqueId, ISoliniaGroup group, boolean updatemana) {
		PartyWindowUtils.UpdateGroupWindow(uniqueId, group, updatemana, false);
		if (group != null) {
			for (UUID uuid : group.getMembers()) {
				PartyWindowUtils.UpdateGroupWindow(uuid, group,updatemana, false);
			}
		}

	}
}
