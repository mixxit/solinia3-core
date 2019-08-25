package com.solinia.solinia.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaGroup;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.CastingSpell;
import com.solinia.solinia.Models.GenericPacketMessage;
import com.solinia.solinia.Models.PartyWindow;
import com.solinia.solinia.Models.PartyWindowPlayer;
import com.solinia.solinia.Models.Solinia3UIChannelNames;
import com.solinia.solinia.Models.Solinia3UIPacketDiscriminators;

import net.md_5.bungee.api.ChatColor;

public class PartyWindowUtils {
	
	public static GenericPacketMessage GenerateGenericPacketMessageForPlayer(Player player)
	{
		if (player == null)
			return null;
		
		GenericPacketMessage message = null;
		
		try
		{
			ISoliniaPlayer playerLivingEntity = SoliniaPlayerAdapter.Adapt(player);
			if (playerLivingEntity == null)
				return null;
			
			message = new GenericPacketMessage(playerLivingEntity);
			message.PartyWindow = new PartyWindow(playerLivingEntity);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return message;
	}
	
	
	public static void UpdateWindow(Player player, int mana) {
		try {
			GenericPacketMessage message = GenerateGenericPacketMessageForPlayer(player);
			if (message == null)
				return;
			
			String json = JsonUtils.getObjectAsJson(message);
			ForgeUtils.sendForgeMessage(player,Solinia3UIChannelNames.Outgoing,Solinia3UIPacketDiscriminators.GENERIC_MESSAGE,json);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void UpdateGroupWindow(UUID uuid, ISoliniaGroup group) {
		try {
			Player player = Bukkit.getPlayer(uuid);
			if (player == null)
				return;
			
			GenericPacketMessage message = GenerateGenericPacketMessageForPlayer(player);
			if (message == null)
				return;
			
			if (message.PartyWindow.PartyMembers == null)
				message.PartyWindow.PartyMembers = new ArrayList<PartyWindowPlayer>();
			
			if (group != null)
			{
				for (UUID groupmemberuuid : group.getMembers()) {
					if (groupmemberuuid.toString().equals(uuid.toString()))
						continue;

					ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(Bukkit.getPlayer(groupmemberuuid));
					if (solplayer == null)
						continue;
					
					message.PartyWindow.AddPartyMember(solplayer);
				}
			}
			
			String json = JsonUtils.getObjectAsJson(message);
			

			ForgeUtils.sendForgeMessage(player,Solinia3UIChannelNames.Outgoing,Solinia3UIPacketDiscriminators.GENERIC_MESSAGE,json);
			if (group != null)
			for (UUID groupmemberuuid : group.getMembers()) {
				Player sendToPlayer = Bukkit.getPlayer(groupmemberuuid);
				ForgeUtils.sendForgeMessage(sendToPlayer,Solinia3UIChannelNames.Outgoing,Solinia3UIPacketDiscriminators.GENERIC_MESSAGE,json);
			}
		} catch (CoreStateInitException e) {
			e.printStackTrace();
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
