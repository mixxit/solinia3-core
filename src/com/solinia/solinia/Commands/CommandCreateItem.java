package com.solinia.solinia.Commands;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.solinia.solinia.Factories.SoliniaItemFactory;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Utils.ItemStackUtils;

public class CommandCreateItem implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;
		
		if (!sender.isOp() && !sender.hasPermission("solinia.createitem"))
		{
			sender.sendMessage("You do not have permission to access this command");
			return false;
		}
		
		ItemStack itemstack = null;
        
		if (args.length == 0 && sender instanceof Player)
		{
			Player player = (Player)sender;
			itemstack = player.getInventory().getItemInMainHand();
	        if (itemstack.getType() == null || itemstack.getType().equals(Material.AIR))
	        {
	        	player.sendMessage(ChatColor.GRAY+"Empty item in hand. You must hold the item you want to make a new item from");
	        	return false;
	        }
	        
	        if (ItemStackUtils.IsSoliniaItem(itemstack))
	        {
	        	player.sendMessage("You can only create a new item from core minecraft items, not solinia items");
	        	return true;
	        }
		} else {
			try
			{
				itemstack = new ItemStack(Material.valueOf(args[0]));
			} catch (Exception e)
			{
				sender.sendMessage("You are trying to create an item from the name of the minecraft item instead of the item you are holding it in. But the name you provided is invalid. Here are the possible values:");
				sender.sendMessage(Arrays.asList(Material.values()).stream()
					      .map(t -> t.toString())
					      .collect(Collectors.joining(",")));
				sender.sendMessage("Optionally use /createitem with no arguments to take the item in your hand and use to create an item from");
				return true;
			}
		}
		
        sender.sendMessage("Building new item based on: " + itemstack.getType().name());
        
        try
        {
        	ISoliniaItem item = SoliniaItemFactory.CreateItem(itemstack, true);
        	sender.sendMessage("New Item Created with ID: " + item.getId());
        	sender.sendMessage("Use /edititem ID to further edit this item");
        } catch (Exception e)
        {
        	sender.sendMessage(e.getMessage() + " " + e.getStackTrace());
        }

		return true;
	}
}
