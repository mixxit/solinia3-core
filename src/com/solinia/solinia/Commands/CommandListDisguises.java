package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaDisguise;
import com.solinia.solinia.Utils.ChatUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class CommandListDisguises implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;
		
		if (!sender.isOp() && !sender.hasPermission("solinia.listdisguises"))
		{
			sender.sendMessage("You do not have permission to access this command");
			return false;
		}
		
		try
		{
		
		if (args.length == 0)
		{
			// Return all
			for(SoliniaDisguise entity : StateManager.getInstance().getConfigurationManager().getSoliniaDisguises())
			{
				TextComponent textComponent = new TextComponent();
				String title = "DisguiseId: " + ChatColor.GOLD + entity.getId() + ChatColor.RESET + " - " + entity.getDisguiseName();
				textComponent.setText(title);
				String transfertext = "/editdisguise" + entity.getId()  + "";
				textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, transfertext));
				sender.spigot().sendMessage(textComponent);
			}
			
			return true;
		}
		
		if (args.length > 0 && args[0].equals(".criteria"))
		{
			try {
				ChatUtils.sendFilterByCriteria(StateManager.getInstance().getConfigurationManager().getSoliniaDisguises(), sender, args,SoliniaDisguise.class);
			return true;
			} catch (CoreStateInitException e) {
				// TODO Auto-generated catch block
				sender.sendMessage(e.getMessage());
				e.printStackTrace();
			}
		}
		
		// Filter for name
		for(SoliniaDisguise entity : StateManager.getInstance().getConfigurationManager().getSoliniaDisguises())
		{
			if (entity.getDisguiseName().toUpperCase().contains(args[0].toUpperCase()))
			{
				TextComponent textComponent = new TextComponent();
				String title = "DisguiseID: " + ChatColor.GOLD + entity.getId() + ChatColor.RESET + " - " + entity.getDisguiseName();
				textComponent.setText(title);
				String transfertext = "/editdisguise " + entity.getId()  + "";
				textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, transfertext));
				sender.spigot().sendMessage(textComponent);
			}
		}
		} catch (CoreStateInitException e)
		{
			sender.sendMessage(e.getMessage());
		}
		
		return true;
	}

}
