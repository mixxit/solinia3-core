package com.solinia.solinia.Commands;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaAAAbility;
import com.solinia.solinia.Interfaces.ISoliniaAARank;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Utils.Utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class CommandCharacter implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
			return false;

		Player player = (Player) sender;

		try {
			ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(player);
			
			if (args.length == 0)
			{
				player.sendMessage("Your Characters: ");
	
				String main = "ALT";
				if (solplayer.isMain())
				{
					main = "MAIN";
				}
				player.sendMessage("- " + ChatColor.LIGHT_PURPLE + solplayer.getFullNameWithTitle() + ChatColor.RESET + " " + main + " - " + ChatColor.GREEN + "ACTIVE" + ChatColor.RESET);
				
				for (ISoliniaPlayer character : StateManager.getInstance().getPlayerManager().getCharactersByPlayerUUID(player.getUniqueId()))
				{
					if (character.getCharacterId().equals(solplayer.getCharacterId()))
						continue;
					
					if (character.isMain())
					{
						main = "MAIN";
					} else {
						main = "ALT";
					}
					
					TextComponent tc = new TextComponent();
					tc.setText("- " + ChatColor.LIGHT_PURPLE + character.getFullNameWithTitle() + ChatColor.RESET + " " + main + " - ");
					String details = ChatColor.GOLD + character.getFullNameWithTitle() + " Level: " + character.getLevel() + ChatColor.RESET;
					
					TextComponent tc2 = new TextComponent();
					String changetext = "/character load " + character.getCharacterId().toString();
					
					if (StateManager.getInstance().getPlayerManager().getPlayerLastChangeChar(player.getUniqueId()) != null)
					{
						tc2.setText(ChatColor.RED + "Already changed this session" + ChatColor.RESET);
					} else {
						tc2.setText(ChatColor.AQUA + "[Click to Switch]" + ChatColor.RESET);
						tc2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, changetext));						
					}
					
					tc.addExtra(tc2);
					
					tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
							new ComponentBuilder(details).create()));
					sender.spigot().sendMessage(tc);
	
				}
	
				player.sendMessage("Note, new characters may not be visible for up to 15 minutes");
				player.sendMessage("Use '/character new' to create a new character");
			} else {
				switch(args[0].toUpperCase())
				{
					case "NEW":
						if (StateManager.getInstance().getPlayerManager().getPlayerLastChangeChar(player.getUniqueId()) != null)
						{
							player.sendMessage("You can only change your character once per server session. Please wait for the next 4 hourly restart");
							return true;
						}
							
						
						ISoliniaPlayer newPlayer = StateManager.getInstance().getPlayerManager().createNewPlayerAlt(player);
						if (newPlayer != null)
						{
							player.sendMessage("Your character has been stored and a new character created");
							player.sendMessage("Please check you have not dropped any items due to your character change");
						} else {
							player.sendMessage("Problem creating your new character");
						}
						break;
					case "LOAD":
						if (args.length < 2)
						{
							player.sendMessage("You must provide the character UUID");
							return true;
						}
						
						if (StateManager.getInstance().getPlayerManager().getPlayerLastChangeChar(player.getUniqueId()) != null)
						{
							player.sendMessage("You can only change your character once per server session. Please wait for the next 4 hourly restart");
							return true;
						}
						
						UUID characterUUID = UUID.fromString(args[1]);
						
						ISoliniaPlayer loadedPlayer = StateManager.getInstance().getPlayerManager().loadPlayerAlt(player,characterUUID);
						if (loadedPlayer != null)
						{
							player.sendMessage("Your character has been stored and your new character loaded");
							player.sendMessage("Please check you have not dropped any items due to your character change");
						} else {
							player.sendMessage("Problem loading requested character");
						}
						break;
					default:
						player.sendMessage("Unknown character subcommand");
						break;				
				}
			}
		} catch (CoreStateInitException e) {

		}
		return true;
	}

}
