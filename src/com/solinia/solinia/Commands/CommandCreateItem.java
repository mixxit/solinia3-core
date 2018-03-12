package com.solinia.solinia.Commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.solinia.solinia.Factories.SoliniaItemFactory;
import com.solinia.solinia.Interfaces.ISoliniaItem;

public class CommandCreateItem implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;
		
		if (sender instanceof Player)
		{
			Player player = (Player)sender;
			if (!player.isOp() && !player.hasPermission("solinia.createitem"))
			{
				player.sendMessage("You do not have permission to access this command");
				return false;
			}
		}
		
		ItemStack itemstack = null;
        
		if (args.length == 0 && sender instanceof Player)
		{
			Player player = (Player)sender;
			itemstack = player.getInventory().getItemInMainHand();
	        if (itemstack.getType().equals(Material.AIR))
	        {
	        	player.sendMessage(ChatColor.GRAY+"Empty item in hand. You must hold the item you want to make a new item from");
	        	return false;
	        }
	        
	        if (itemstack.getEnchantmentLevel(Enchantment.DURABILITY) > 999)
	        {
	        	player.sendMessage("You can only create a new item from core minecraft items, not solinia items");
	        	return true;
	        }
		} else {
			itemstack = new ItemStack(Material.valueOf(args[0]));
		}
		
        sender.sendMessage("Building new item based on: " + itemstack.getType().name());
        
        try
        {
        	ISoliniaItem item = SoliniaItemFactory.CreateItem(itemstack,sender.isOp());
        	sender.sendMessage("New Item Created with ID: " + item.getId());
        	sender.sendMessage("Use /edititem ID to further edit this item");
        } catch (Exception e)
        {
        	sender.sendMessage(e.getMessage());
        }

		return true;
	}
}
