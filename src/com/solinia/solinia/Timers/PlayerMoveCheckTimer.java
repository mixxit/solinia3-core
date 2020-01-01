package com.solinia.solinia.Timers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.PacketInZone;
import com.solinia.solinia.Models.Solinia3UIChannelNames;
import com.solinia.solinia.Models.Solinia3UIPacketDiscriminators;
import com.solinia.solinia.Models.SoliniaZone;
import com.solinia.solinia.Utils.ForgeUtils;

public class PlayerMoveCheckTimer  extends BukkitRunnable {
	@Override
	public void run() {
		try
		{
			runMoveChecker();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void runMoveChecker() {
		for(Player player : Bukkit.getOnlinePlayers())
		{
			notifyZoneChange(player);
		}
	}
	
	private void notifyZoneChange(Player player) {
		try {
			ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
			SoliniaZone zone = solPlayer.getFirstZone();
			int zoneId = 0;
			String zoneMusic = "";
			String zoneName = "";

			if (zone != null)
			{
				zoneId = zone.getId();
				zoneName = zone.getName();
				zoneMusic = zone.getMusic();
			}
			
			System.out.println("Checking zone of player " + StateManager.getInstance().getPlayerManager().getPlayerLastZone(player));
			if (zoneId == StateManager.getInstance().getPlayerManager().getPlayerLastZone(player))
				return;
			
			StateManager.getInstance().getPlayerManager().setPlayerLastZone(player,zoneId);
			if (zoneName != null && !zoneName.equals(""))
				player.sendMessage("You have entered zone: " + zoneName);
			PacketInZone packet = new PacketInZone();
			packet.fromData(zoneId,zoneMusic);
			ForgeUtils.sendForgeMessage(player,Solinia3UIChannelNames.Outgoing,Solinia3UIPacketDiscriminators.INZONE,packet.toPacketData());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}