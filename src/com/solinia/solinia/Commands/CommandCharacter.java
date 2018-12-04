package com.solinia.solinia.Commands;

import java.util.UUID;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Solinia3CorePlugin;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class CommandCharacter implements CommandExecutor {

	Solinia3CorePlugin plugin;
	
	public CommandCharacter(Solinia3CorePlugin plugin) {
		this.plugin = plugin;
	}

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
					
					String charclass = "";
					
					if (character.getClassObj() != null)
					{
						charclass = character.getClassObj().getName();
					}
					
					String details = ChatColor.GOLD + character.getFullNameWithTitle() + " " + charclass + " Level: " + character.getLevel() + ChatColor.RESET;
					
					TextComponent tc2 = new TextComponent();
					String changetext = "/character load " + character.getCharacterId().toString();
					
					if (!player.isOp() && !player.hasPermission("solinia.characternewunlimited") && StateManager.getInstance().getPlayerManager().getPlayerLastChangeChar(player.getUniqueId()) != null)
					{
						tc2.setText(ChatColor.RED + "Already changed this session" + ChatColor.RESET);
					} else {
						tc2.setText(ChatColor.AQUA + "[Click to Switch]" + ChatColor.RESET);
						tc2.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, changetext));						
					}
					
					tc.addExtra(tc2);
					
					TextComponent tc3 = new TextComponent();
					String transfertext = "/transfercharacter " + character.getCharacterId().toString() + " playername";
					tc3.setText(ChatColor.LIGHT_PURPLE + " [Transfer]" + ChatColor.RESET);
					tc3.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, transfertext));
					tc.addExtra(tc3);
					
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
						if (!player.isOp() && !player.hasPermission("solinia.characternewunlimited") && StateManager.getInstance().getPlayerManager().getPlayerLastChangeChar(player.getUniqueId()) != null)
						{
							player.sendMessage("You can only change your character once per server session. Please wait for the next 4 hourly restart");
							return true;
						}
							
						
						ISoliniaPlayer newPlayer = StateManager.getInstance().getPlayerManager().createNewPlayerAlt(plugin, player);
						if (newPlayer != null)
						{
							newPlayer.setBindPoint(player.getLocation().getWorld().getName() + "," + player.getLocation().getX() + ","
									+ player.getLocation().getY() + "," + player.getLocation().getZ());
							
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
						
						if (!player.isOp() && !player.hasPermission("solinia.characternewunlimited") && StateManager.getInstance().getPlayerManager().getPlayerLastChangeChar(player.getUniqueId()) != null)
						{
							player.sendMessage("You can only change your character once per server session. Please wait for the next 4 hourly restart");
							return true;
						}
						
						UUID characterUUID = UUID.fromString(args[1]);
						
						ISoliniaPlayer targetCharacter = StateManager.getInstance().getConfigurationManager().getCharacterByCharacterUUID(characterUUID);
			        	if (!(targetCharacter.getUUID().equals(player.getUniqueId())))
			        	{
			        		player.sendMessage("This is not your character to load");
			        		return true;
			        	}

						ISoliniaPlayer loadedPlayer = StateManager.getInstance().getPlayerManager().loadPlayerAlt(plugin, player,characterUUID);
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
