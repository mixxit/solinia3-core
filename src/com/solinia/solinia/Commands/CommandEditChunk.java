package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaChunkAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidChunkSettingException;
import com.solinia.solinia.Exceptions.InvalidLootDropSettingException;
import com.solinia.solinia.Exceptions.InvalidRaceSettingException;
import com.solinia.solinia.Interfaces.ISoliniaLootDrop;
import com.solinia.solinia.Interfaces.ISoliniaRace;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaChunk;

public class CommandEditChunk implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
			return false;

		Player player = (Player) sender;
		if (!player.isOp() && !player.hasPermission("solinia.editchunk")) {
			player.sendMessage("You do not have permission to access this command");
			return false;
		}

		// Args
		// Setting
		// NewValue

		try {
			SoliniaChunk chunk = SoliniaChunkAdapter.Adapt(player.getWorld().getChunkAt(player.getLocation()));
			if (chunk == null) {
				player.sendMessage("Chunk is not currently available for planting a flag!");
				return true;
			}

			if (args.length == 0) {
				chunk.sendChunkSettingsToSender(sender);
				return true;
			}

			if (args.length < 2) {
				sender.sendMessage("Insufficient arguments: setting value");
				return false;
			}

			String setting = args[1];

			String value = args[2];

			try {

				chunk.editSetting(setting, value);
				sender.sendMessage("Updating setting on chunk");
			} catch (InvalidChunkSettingException ne) {
				sender.sendMessage("Invalid chunk setting: " + ne.getMessage());
			}
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			sender.sendMessage(e.getMessage());
		}
		return true;
	}
}
