package com.solinia.solinia.Commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.solinia.solinia.Adapters.SoliniaItemAdapter;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaItemException;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SkillType;
import com.solinia.solinia.Models.SoliniaCraft;
import com.solinia.solinia.Models.SoliniaPlayerSkill;
import com.solinia.solinia.Utils.Utils;

public class CommandCraft implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!(sender instanceof Player))
		{
			return false;
		}
		
		Player player = (Player)sender;
		ItemStack primaryItem = player.getInventory().getItemInMainHand();
        if (primaryItem.getType().equals(Material.AIR))
        {
        	player.sendMessage(ChatColor.GRAY+"Empty item in primary hand. You must hold the item you want to use in your crafting recipe");
        	return false;
        }
        
		ItemStack secondaryItem = player.getInventory().getItemInOffHand();
        if (secondaryItem.getType().equals(Material.AIR))
        {
        	player.sendMessage(ChatColor.GRAY+"Empty item in offhand. You must hold the item you want to use in your crafting recipe");
        	return false;
        }
        
        if (primaryItem.getAmount() > 1)
        {
        	player.sendMessage(ChatColor.GRAY+"Stack size in primary hand is too high (max 1)");
        	return false;
        }

        if (secondaryItem.getAmount() > 1)
        {
        	player.sendMessage(ChatColor.GRAY+"Stack size in secondary hand is too high (max 1)");
        	return false;
        }
        
        if (!Utils.IsSoliniaItem(primaryItem))
        {
        	player.sendMessage("You can only create a new item from solinia items, not minecraft items");
        	return true;
        }
        
        if (!Utils.IsSoliniaItem(secondaryItem))
        {
        	player.sendMessage("You can only create a new item from solinia items, not minecraft items");
        	return true;
        }
		
        try
        {
    		ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);

	        ISoliniaItem primarysolItem = SoliniaItemAdapter.Adapt(primaryItem);
	        ISoliniaItem secondarysolItem = SoliniaItemAdapter.Adapt(secondaryItem);
	        
	        List<SoliniaCraft> craft = StateManager.getInstance().getConfigurationManager().getCrafts(primarysolItem.getId(),secondarysolItem.getId());
	        if (craft.size() < 1)
	        {
	        	player.sendMessage("You do not seem to know how to make anything with these items");
	        	return true;
	        }
	        
			int createCount = 0;
			
			for(SoliniaCraft craftEntry : craft)
			{
				if (craftEntry.getClassId() > 0)
				{
					if (solPlayer.getClassObj() == null)
					{
						player.sendMessage("You are not the correct class to produce " + craftEntry.getRecipeName());
						continue;
					}
					
					if (solPlayer.getClassObj().getId() != craftEntry.getClassId())
					{
						player.sendMessage("You are not the correct class to produce " + craftEntry.getRecipeName());
						continue;
					}
				}
				
				if (craftEntry.getSkill() != null && !craftEntry.getSkill().equals(""))
				{
					if (craftEntry.getMinSkill() > 0)
					{
						SoliniaPlayerSkill skill = solPlayer.getSkill(craftEntry.getSkill().toUpperCase());
						if (skill == null)
						{
				        	player.sendMessage("You have insufficient skill to produce " + craftEntry.getRecipeName());
							continue;
						}
						
						if (skill.getValue() < craftEntry.getMinSkill())
						{
							player.sendMessage("You have insufficient skill to produce " + craftEntry.getRecipeName());
							continue;
						}
					}
				}
				
				ISoliniaItem outputItem = StateManager.getInstance().getConfigurationManager().getItem(craftEntry.getOutputItem());
				if (outputItem != null)
				{
					if (craftEntry.getSkill() != null && !craftEntry.getSkill().equals(""))
					{
						solPlayer.tryIncreaseSkill(craftEntry.getSkill().toUpperCase(), 1);

						if (!solPlayer.getSkillCheck(craftEntry.getSkill().toUpperCase(),craftEntry.getMinSkill()+50))
						{
							player.sendMessage("Your lack of skill resulted in failure!");
					        player.getInventory().setItemInMainHand(null);
					        player.getInventory().setItemInOffHand(null);
							player.updateInventory();
							return true;
						}
					}

					player.getWorld().dropItemNaturally(player.getLocation(), outputItem.asItemStack());
					player.sendMessage("You fashion the items together to make something new!");
					createCount++;

				}
			}
			
			if (createCount > 0)
			{
		        player.getInventory().setItemInMainHand(null);
		        player.getInventory().setItemInOffHand(null);
				player.updateInventory();
				
			}

        } catch (CoreStateInitException e)
        {
        	
        } catch (SoliniaItemException e) {
			player.sendMessage("Item no longer exists");
		}
		return true;
	}

}
