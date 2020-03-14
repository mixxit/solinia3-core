package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.PacketTrackingChoices;
import com.solinia.solinia.Models.Solinia3UIChannelNames;
import com.solinia.solinia.Models.Solinia3UIPacketDiscriminators;
import com.solinia.solinia.Utils.ForgeUtils;

public class CommandTrack implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;

		if (!(sender instanceof Player)) {
			sender.sendMessage("This is a player only command");
			return false;
		}

		Player player = (Player) sender;
		try {
			ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player) sender);
			if (solPlayer.getClassObj() == null)
			{
				player.sendMessage("You do not have the tracking ability");
				return true;
			}

			if (solPlayer.getLevel() < solPlayer.getClassObj().getTrackingLevel())
			{
				player.sendMessage("You do not have the tracking ability");
				return true;
			}
			if (args.length < 1) {
				// Send Tracking Choices
				PacketTrackingChoices trackingChoicesPacket = new PacketTrackingChoices();
				trackingChoicesPacket.fromData(solPlayer.getTrackingChoices());
				ForgeUtils.sendForgeMessage(((Player) solPlayer.getBukkitPlayer()),
						Solinia3UIChannelNames.Outgoing, Solinia3UIPacketDiscriminators.TRACKING,
						trackingChoicesPacket.toPacketData());
				player.sendMessage("You check for tracks...");
				player.sendMessage("/track stop to stop tracking");
				return true;
			} else {
				if (args[0].toUpperCase().equals("STOP"))
				{
					try {
						StateManager.getInstance().getEntityManager().stopTracking(player.getUniqueId());

						} catch (CoreStateInitException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					return true;
				}
				
				// Start Track
				String spawngroupId = args[0];
				int spawnGroup = Integer.parseInt(spawngroupId.split("SPAWNGROUPID_")[1]);
				player.sendMessage("Debug Tracking... ["+spawnGroup+"]");
				// Get spawngroup
				solPlayer.startTracking(StateManager.getInstance().getConfigurationManager().getSpawnGroup(spawnGroup).getLocation());
				
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
}
