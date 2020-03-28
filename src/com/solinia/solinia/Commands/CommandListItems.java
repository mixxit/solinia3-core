package com.solinia.solinia.Commands;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaItem;
import com.solinia.solinia.Utils.ItemStackUtils;
import com.solinia.solinia.Utils.Utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class CommandListItems implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;
		
		if (!sender.isOp() && !sender.hasPermission("solinia.listitems"))
		{
			sender.sendMessage("You do not have permission to access this command");
			return false;
		}
		
		if (args.length == 0)
		{
			sender.sendMessage("You must provide some text to filter the item name by");
			return true;
		}
		
		if (args.length > 0 && args[0].equals(".criteria"))
		{
			try {
				Utils.sendFilterByCriteria(StateManager.getInstance().getConfigurationManager().getItems(), sender, args,SoliniaItem.class);
			return true;
			} catch (CoreStateInitException e) {
				// TODO Auto-generated catch block
				sender.sendMessage(e.getMessage());
				e.printStackTrace();
			}
		}
		
		// Filter for name
		
		int found = 0;
		try {
				for(ISoliniaItem item : StateManager.getInstance().getConfigurationManager().getItems())
				{
					found++;
					if (item.getDisplayname().toUpperCase().contains(StringUtils.join(args, " ").toUpperCase()))
					{
						TextComponent textComponent = new TextComponent();
						String title = "" + ChatColor.GOLD + item.getId() + ChatColor.RESET + " - " + item.getDisplayname();
						textComponent.setText(title);
						textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new ComponentBuilder(ItemStackUtils.ConvertItemStackToJsonRegular(item.asItemStack())).create()));
						String transfertext = "/spawnitem " + item.getId()  + " 1";
						textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, transfertext));

						sender.spigot().sendMessage(textComponent);
					}
				}
			
			if (found == 0)
			{
				sender.sendMessage("Item could not be located by search string");
			}
			
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			sender.sendMessage(e.getMessage());
			e.printStackTrace();
		}
		
		return true;
	}
}
