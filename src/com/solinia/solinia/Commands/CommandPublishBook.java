package com.solinia.solinia.Commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import com.solinia.solinia.Factories.SoliniaItemFactory;
import com.solinia.solinia.Factories.SoliniaNPCMerchantFactory;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaNPCMerchant;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Utils.Utils;

public class CommandPublishBook implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;
		
		if (!(sender instanceof Player))
		{
			sender.sendMessage("This is a player only command");
			return false;
		}
		
		Player player = (Player)sender;
		
		ItemStack itemstack = null;
        
		itemstack = player.getInventory().getItemInMainHand();
        if (itemstack.getType().equals(Material.AIR))
        {
        	player.sendMessage(ChatColor.GRAY+"Empty item in hand. You must hold the item you want to publish a book from");
        	return false;
        }
        
        if (Utils.IsSoliniaItem(itemstack))
        {
        	player.sendMessage("You can only create a new item from core minecraft items, not solinia items");
        	return true;
        }
		
        if (!itemstack.getType().name().equals("WRITTEN_BOOK"))
        {
        	player.sendMessage("You are not holding a written book in your hand");
        	return true;
        }
        
        sender.sendMessage("Building new item based on: " + itemstack.getType().name());
        
        try
        {
        	List<ISoliniaItem> matchingItems = StateManager.getInstance().getConfigurationManager().getItemsByPartialName(((BookMeta)itemstack.getItemMeta()).getTitle());
        	if (matchingItems.size() > 0)
        	{
        		player.sendMessage("An item with this name is already published");
        		return true;
        	}
        	
        	ISoliniaItem item = SoliniaItemFactory.CreateItem(itemstack,true);
        	sender.sendMessage("Book is a brand new title! " + item.getId());
        	
        	List<Integer> merchantItemList = new ArrayList<Integer>();
        	
        	for(ISoliniaNPCMerchant merchant : StateManager.getInstance().getConfigurationManager().getNPCMerchants())
        	{
        		if (!merchant.isPublishedBookStore())
        			continue;

        		merchantItemList.add(merchant.getId());
        	}
        	
        	for(Integer merchantId : merchantItemList)
        	{
        		SoliniaNPCMerchantFactory.AddNPCMerchantItem(merchantId, item.getId());
        	}
        	
        	sender.sendMessage("New Book Published! (ID: " + item.getId() + ")");
        } catch (Exception e)
        {
        	e.printStackTrace();
        	sender.sendMessage(e.getMessage());
        }

		return true;
	}
}
