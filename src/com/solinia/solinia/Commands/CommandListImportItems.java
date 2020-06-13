package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Solinia3CorePlugin;
import com.solinia.solinia.Adapters.SoliniaItemAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaItemException;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.EQItem;
import com.solinia.solinia.Utils.ItemStackUtils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class CommandListImportItems implements CommandExecutor {
	private Solinia3CorePlugin plugin;

	public CommandListImportItems(Solinia3CorePlugin solinia3CorePlugin) {
		this.plugin = solinia3CorePlugin;
	}
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;
		
		if (!sender.isOp() && !sender.hasPermission("solinia.importitem"))
		{
			sender.sendMessage("You do not have permission to access this command");
			return false;
		}
		
		if (args.length < 1)
        {
        	sender.sendMessage("Insufficient arguments (<name>)");
        	return false;
        }
    	
        String name = args[0];
        
		try {
			for(EQItem eqitem : StateManager.getInstance().getConfigurationManager().getImportItems(name))
			{
				
				TextComponent textComponent = new TextComponent();
				String title = "" + ChatColor.GOLD + (int)eqitem.getId() + ChatColor.RESET + " - " + eqitem.getName();
				textComponent.setText(title);
				ISoliniaItem item = null;
				try {
					item = SoliniaItemAdapter.Adapt(eqitem, false);
					if (item != null)
					textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new ComponentBuilder(ItemStackUtils.ConvertItemStackToJsonRegular(item.asItemStack())).create()));
				} catch (CoreStateInitException e) {
					
				} catch (SoliniaItemException e) {
					
				}

				sender.spigot().sendMessage(textComponent);
			}
			return true;
		} catch (CoreStateInitException e) {
			sender.sendMessage(e.getMessage());
		}
		return false;
	}

}
