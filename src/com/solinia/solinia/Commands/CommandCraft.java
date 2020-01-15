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
import com.solinia.solinia.Interfaces.ISoliniaLootDrop;
import com.solinia.solinia.Interfaces.ISoliniaLootDropEntry;
import com.solinia.solinia.Interfaces.ISoliniaLootTable;
import com.solinia.solinia.Interfaces.ISoliniaLootTableEntry;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaCraft;
import com.solinia.solinia.Models.SoliniaPlayerSkill;
import com.solinia.solinia.Utils.ItemStackUtils;
import com.solinia.solinia.Utils.PlayerUtils;
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
        if (primaryItem.getType() == null || primaryItem.getType().equals(Material.AIR))
        {
        	player.sendMessage(ChatColor.GRAY+"Empty item in primary hand. You must hold the item you want to use in your crafting recipe");
        	return false;
        }
        
		ItemStack secondaryItem = player.getInventory().getItemInOffHand();
        if (secondaryItem.getType() == null || secondaryItem.getType().equals(Material.AIR))
        {
        	player.sendMessage(ChatColor.GRAY+"Empty item in offhand. You must hold the item you want to use in your crafting recipe");
        	return false;
        }
        
        if (!ItemStackUtils.IsSoliniaItem(primaryItem))
        {
        	player.sendMessage("You can only create a new item from solinia items, not minecraft items");
        	return true;
        }
        
        if (!ItemStackUtils.IsSoliniaItem(secondaryItem))
        {
        	player.sendMessage("You can only create a new item from solinia items, not minecraft items");
        	return true;
        }
        
        int amountToCraft = 1;
        
        if (primaryItem.getAmount() > 1 && secondaryItem.getAmount() > 1)
        {
        	// get the highest of the two numbers
        	amountToCraft = Math.max(primaryItem.getAmount(), primaryItem.getAmount());
        }
        
        for (int i = 0; i < amountToCraft; i++)
        {
        	try
            {
        		primaryItem = player.getInventory().getItemInMainHand();
        		if (primaryItem == null || primaryItem.getType() == null || primaryItem.getType().equals(Material.AIR))
        		{
        			player.sendMessage(ChatColor.GRAY+"Empty item in offhand. You must hold the item you want to use in your crafting recipe");
                	return false;
        		}
        		secondaryItem = player.getInventory().getItemInOffHand();
        		if (secondaryItem == null || secondaryItem.getType() == null || secondaryItem.getType().equals(Material.AIR))
        		{
        			player.sendMessage(ChatColor.GRAY+"Empty item in offhand. You must hold the item you want to use in your crafting recipe");
                	return false;
        		}
        		
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
    					
    					if (craftEntry.getMinLevel() > player.getLevel())
    					{
    						player.sendMessage("You have insufficient level to produce " + craftEntry.getRecipeName());
    						continue;
    					}
    				}
    				
    				if (craftEntry.getOutputItem() > 0 || craftEntry.getOutputLootTableId() > 0)
    				{
    					if (craftEntry.getOutputItem() > 0)
    					{
    						ISoliniaItem outputItem = StateManager.getInstance().getConfigurationManager().getItem(craftEntry.getOutputItem());
    						if (outputItem == null)
    						{
    							player.sendMessage("That craft is not possible as the recipe is for an item that no longer is possible");
    							continue;
    						}
    					}
    					
    					if (craftEntry.getOutputLootTableId() > 0)
    					{
    						ISoliniaLootTable outputLootTable = StateManager.getInstance().getConfigurationManager().getLootTable(craftEntry.getOutputLootTableId());
    						if (outputLootTable == null)
    						{
    							player.sendMessage("That craft is not possible as the recipe is for a random loot list that no longer exists");
    							continue;
    						}
    						
    						if (outputLootTable.getEntries().size() < 1)
    						{
    							player.sendMessage("That craft is not possible as the recipe is for a random loot list that is empty");
    							continue;
    						}
    						
    						int lootdropcount = 0;
    						
    						for(ISoliniaLootTableEntry entry : outputLootTable.getEntries())
    						{
	    						ISoliniaLootDrop lootdrop = StateManager.getInstance().getConfigurationManager().getLootDrop(entry.getLootdropid());
	    						if (lootdrop == null)
	    							continue;
	    						
	    						if (lootdrop.getEntries().size() < 1)
	    							continue;
	    						
	    						for (ISoliniaLootDropEntry lentry : lootdrop.getEntries())
	    						{
	    							ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(lentry.getItemid());
	    							if (item == null)
	    								continue;
	    							
	    							lootdropcount++;
	    						}
    						}
    						
    						if (lootdropcount < 1)
    						{
    							player.sendMessage("That craft is not possible as the recipe is for a random loot list with loot drops that are empty");
    							continue;
    						}
    					}
    					
	    				if (craftEntry.getSkill() != null && !craftEntry.getSkill().equals(""))
						{
							solPlayer.tryIncreaseSkill(craftEntry.getSkill().toUpperCase(), 1);
	
							if (!solPlayer.getSkillCheck(craftEntry.getSkill().toUpperCase(),craftEntry.getMinSkill()+50))
							{
								player.sendMessage("Your lack of skill resulted in failure!");
								// remove components
			    				ItemStack stack = ((Player) sender).getEquipment().getItemInMainHand();
			    				stack.setAmount(stack.getAmount() - 1);
			    		        player.getInventory().setItemInMainHand(stack);
	
			    				ItemStack stack2 = ((Player) sender).getEquipment().getItemInOffHand();
			    				stack2.setAmount(stack2.getAmount() - 1);
			    		        player.getInventory().setItemInOffHand(stack2);
			    				
								player.updateInventory();
								break;
							}
						}
	    				
	    				if (craftEntry.getOutputItem() > 0)
	    				{
	    					ISoliniaItem outputItem = StateManager.getInstance().getConfigurationManager().getItem(craftEntry.getOutputItem());
	    					PlayerUtils.addToPlayersInventory(player, outputItem.asItemStack());
							player.sendMessage("You fashion the items together to make something new!");
							createCount++;
	    				} else {
		    				if (craftEntry.getOutputLootTableId() > 0)
	    					{
	    						ISoliniaLootTable loottable = StateManager.getInstance().getConfigurationManager().getLootTable(craftEntry.getOutputLootTableId());
	    						if (loottable != null)
	    						{
	    							Utils.DropLoot(loottable.getId(), player.getWorld(), player.getLocation());
	    							player.sendMessage("You fashion the items together to attempt to make something random!");
	    							createCount++;
	    						}
	    					}
	    				}
    				}

    				if (createCount > 0)
        			{
        				// remove components
        				ItemStack stack = ((Player) sender).getEquipment().getItemInMainHand();
        				stack.setAmount(stack.getAmount() - 1);
        		        player.getInventory().setItemInMainHand(stack);

        				ItemStack stack2 = ((Player) sender).getEquipment().getItemInOffHand();
        				stack2.setAmount(stack2.getAmount() - 1);
        		        player.getInventory().setItemInOffHand(stack2);
        				
        				player.updateInventory();
        				
        			}
    			}
            } catch (CoreStateInitException e)
            {
            	
            } catch (SoliniaItemException e) {
    			player.sendMessage("Item no longer exists");
    		}
        }
		
        
		return true;
	}

}
