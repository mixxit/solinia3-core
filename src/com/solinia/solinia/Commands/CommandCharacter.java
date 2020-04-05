package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;

import com.solinia.solinia.Solinia3CorePlugin;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Utils.EntityUtils;
import com.solinia.solinia.Utils.Utils;

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
			
			if (StateManager.getInstance().charChange == false)
			{
				sender.sendMessage("This character cannot be used when the server has been set to charchange false (ie a restart is coming)");
				return true;
			}
			
			if (args.length == 0)
			{
				player.sendMessage("Your Characters: ");
	
				player.sendMessage("- " + ChatColor.LIGHT_PURPLE + solplayer.getFullName() + ChatColor.RESET + " - " + ChatColor.GREEN + "ACTIVE" + ChatColor.RESET);
				
				for (ISoliniaPlayer character : StateManager.getInstance().getPlayerManager().getCharactersByPlayerUUID(player.getUniqueId()))
				{
					
					if (character.getId() == solplayer.getId())
						continue;
					
					String locked  = "";
					
					if (character.isDeleted())
						continue;
					
					if (!character.isPlayable())
						if (!player.isOp() && !player.hasPermission("solinia.characterdonochangelocation"))
						{
							continue;
						} else {
							locked = "[LOCKED]";
						}
					
					TextComponent tc = new TextComponent();
					tc.setText("- " + ChatColor.LIGHT_PURPLE + character.getFullNameWithTitle() + ChatColor.RESET + " - ");
					
					String charclass = "";
					
					if (character.getClassObj() != null)
					{
						charclass = character.getClassObj().getName();
					}
					
					String details = ChatColor.GOLD + character.getFullNameWithTitle() + " " + charclass + " Level: " + character.getLevel() + " " + locked + ChatColor.RESET;
					
					TextComponent tc2 = new TextComponent();
					String changetext = "/character load " + character.getId();
					
					if (!player.isOp() && !player.hasPermission("solinia.characternewunlimited") && !Utils.canChangeCharacter(player))
					{
						tc2.setText(ChatColor.RED + "Wait 10 mins to change char" + ChatColor.RESET);
					} else {
						tc2.setText(ChatColor.AQUA + "[Click to Switch]" + ChatColor.RESET);
						tc2.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, changetext));						
					}
					
					tc.addExtra(tc2);
					
					TextComponent tc3 = new TextComponent();
					String transfertext = "/transfercharacter " + character.getId() + " playername";
					tc3.setText(ChatColor.LIGHT_PURPLE + " [Transfer]" + ChatColor.RESET);
					tc3.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, transfertext));
					tc.addExtra(tc3);
					
					TextComponent tc4 = new TextComponent();
					String deletetext = "/deletecharacter " + character.getId() + " playername";
					tc4.setText(ChatColor.RED + " [Delete]" + ChatColor.RESET);
					tc4.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, deletetext));
					tc.addExtra(tc4);
					
					tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
							new ComponentBuilder(details).create()));
					sender.spigot().sendMessage(tc);
	
				}
	
				player.sendMessage("Note, new characters may not be visible for up to 15 minutes");
				player.sendMessage("Use '/opencharcreation' to create a new character");
			} else {
				switch(args[0].toUpperCase())
				{
					case "LOAD":
						if (args.length < 2)
						{
							player.sendMessage("You must provide the character id");
							return true;
						}
						
						InventoryView view = player.getOpenInventory();

						if(view == null) {
							player.sendMessage("Cannot change char when in an inventory");
						  return true;
						}

						
			        	boolean resetLocation2 = true;
						if (args.length > 2 && (player.isOp() || player.hasPermission("solinia.characterdonochangelocation")))
						{
							if (args[2].toUpperCase().equals("false"))
								resetLocation2 = false;
						}
						
						if (!player.isOp() && !player.hasPermission("solinia.characternewunlimited") && !Utils.canChangeCharacter(player))
						{
							player.sendMessage("You can only change your character every 10 minutes");
							return true;
						}
						
						int characterId = Integer.parseInt(args[1]);
						
						ISoliniaPlayer targetCharacter = StateManager.getInstance().getConfigurationManager().getCharacterById(characterId);
			        	if (!(targetCharacter.getOwnerUUID().equals(player.getUniqueId())))
			        	{
			        		player.sendMessage("This is not your character to load");
			        		return true;
			        	}
			        	
						if (!targetCharacter.isPlayable())
						{
							player.sendMessage("This character is higher level than the character level limit");
							if (!player.isOp() && !player.hasPermission("solinia.characterdonochangelocation"))
							{
								return true;
							} else {
								player.sendMessage("However you are a staff member, so you can load it");
							}
						}

						ISoliniaPlayer loadedPlayer = StateManager.getInstance().getPlayerManager().loadPlayerAlt(plugin, player,characterId);
						if (loadedPlayer != null)
						{
							// Remove force new alt since we are now logged in
							loadedPlayer.setForceNewAlt(false);
							if (loadedPlayer.getLastLocation() != null)
								if (resetLocation2 == true)
									EntityUtils.teleportSafely(player,loadedPlayer.getLastLocation());
							
							player.sendMessage("Your character has been stored and your new character loaded");
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
