package com.solinia.solinia.Commands;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Events.SoliniaNPCUpdatedEvent;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidNpcSettingException;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Interfaces.ISoliniaSpawnGroup;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Providers.DiscordAdminChannelCommandSender;
import com.solinia.solinia.Providers.DiscordDefaultChannelCommandSender;
import com.solinia.solinia.Utils.Utils;

public class CommandCommit implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender)) {
			sender.sendMessage("This is a Player/Console only command");
			return false;
		}

		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (!player.isOp()) {
				player.sendMessage("This is an operator only command");
				return true;
			}
		}

		sender.sendMessage("* Executing State Commit");

		try {
			StateManager.getInstance().Commit();
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			sender.sendMessage("Error: " + e.getMessage());
			return true;
		}

		if (args.length > 0 && (sender instanceof ConsoleCommandSender || sender instanceof DiscordDefaultChannelCommandSender || sender instanceof DiscordAdminChannelCommandSender)) {
			if (args[0].equals("provider")) {
				try {
					System.out.println("Recommiting all NPCs via provider");
					for (ISoliniaNPC npc : StateManager.getInstance().getConfigurationManager().getNPCs()) {
						try {
							npc.editSetting("name", npc.getName());
							StateManager.getInstance().getEntityManager().getNPCEntityProvider().updateNpc(npc);
						} catch (NumberFormatException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InvalidNpcSettingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				} catch (CoreStateInitException e) {

				}
			}
			
			if (args[0].equals("patch")) {
				System.out.println("Patching");
				Utils.Patcher();
			}
		}

		sender.sendMessage("* Completed State Commit");
		return true;
	}
}
