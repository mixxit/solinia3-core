package com.solinia.solinia.Commands;

import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Models.PacketOpenSpellbook;
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
				return true;
			} else {
				// Start Track
				String spawngroupId = args[0];
				player.sendMessage("You start tracking...");
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
}
