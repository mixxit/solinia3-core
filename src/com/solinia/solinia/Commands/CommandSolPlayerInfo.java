package com.solinia.solinia.Commands;

import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class CommandSolPlayerInfo implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
		{
			sender.sendMessage("This is a player only command");
			return true;
		}
		
		if (args.length < 1)
			return false;
		
		UUID playerUUID = UUID.fromString(args[0]);
		sender.sendMessage("Fetching information about player");
		
		try
		{
			for (ISoliniaPlayer character : StateManager.getInstance().getPlayerManager().getArchivedCharactersByPlayerUUID(playerUUID))
			{
				String locked  = "";
				
				if (character.isDeleted())
					continue;
				
				TextComponent tc = new TextComponent();
				tc.setText("- " + ChatColor.LIGHT_PURPLE + character.getFullNameWithTitle() + ChatColor.RESET + " - ");
				
				String charclass = "";
				
				if (character.getClassObj() != null)
				{
					charclass = character.getClassObj().getName();
				}
				
				String details = ChatColor.GOLD + character.getFullNameWithTitle() + " " + charclass + " Level: " + character.getLevel() + " " + locked + ChatColor.RESET;
				
				TextComponent tc2 = new TextComponent();
				String changetext = "/copycharacter " + character.getCharacterId().toString() + " " + sender.getName();
				
		        if (!sender.isOp() && !sender.hasPermission("solinia.copycharacter"))
				{
					tc2.setText(ChatColor.AQUA + "[Only admin can copy]" + ChatColor.RESET);
				} else {
					tc2.setText(ChatColor.AQUA + "[Click to Copy to you]" + ChatColor.RESET);
					tc2.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, changetext));
				}
				
				tc.addExtra(tc2);
				
				tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
						new ComponentBuilder(details).create()));
				sender.spigot().sendMessage(tc);
			}
			
		} catch (CoreStateInitException e)
		{
			sender.sendMessage("Could not fetch information");
		} 

		return true;
	}

}
