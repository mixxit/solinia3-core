package com.solinia.solinia.Utils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.codec.binary.Base64;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftInventoryCustom;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.tags.CustomItemTagContainer;
import org.bukkit.inventory.meta.tags.ItemTagType;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.solinia.solinia.Adapters.SoliniaItemAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaItemException;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaPatch;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.PlayerState;
import com.solinia.solinia.Models.SoliniaPatch;

import net.minecraft.server.v1_14_R1.InventoryEnderChest;
import net.minecraft.server.v1_14_R1.MojangsonParser;
import net.minecraft.server.v1_14_R1.NBTBase;
import net.minecraft.server.v1_14_R1.NBTCompressedStreamTools;
import net.minecraft.server.v1_14_R1.NBTTagCompound;
import net.minecraft.server.v1_14_R1.NBTTagList;
import net.minecraft.server.v1_14_R1.TileEntityChest;
import net.minecraft.server.v1_14_R1.TileEntityEnderChest;

public class PatchUtils {
	// Used for one off patching, added in /solinia patch command for console sender
	public static void Patcher() {
		try
		{
			StateManager.getInstance().getConfigurationManager().getPatchesRepo().getByKey(1).getPlayerRestoreInventory().clear();
			for(File file : new File(Bukkit.getPluginManager().getPlugin("Solinia3Core").getDataFolder()+"/playerdata").listFiles())
			{
				try
				{
					FileInputStream fileinputstream = new FileInputStream(file);
	                NBTTagCompound nbttagcompound = NBTCompressedStreamTools.a((InputStream) fileinputstream);
	                if (!nbttagcompound.hasKeyOfType("EnderItems",9))
	                	continue;
	                
	                fileinputstream.close();
	                String entity = file.getName().replace(".dat","");
	                
	                Inventory inventory = Bukkit.createInventory(null,45,"Solinia International Bank");
	                NBTTagList nbttaglist = nbttagcompound.getList("EnderItems", 10);
	                if  (nbttaglist.isEmpty())
	                	continue;
	                
	                for (int i = 0; i < nbttaglist.size(); i++) {
	                    NBTTagCompound comp = nbttaglist.getCompound(i);
	                    int j = comp.getByte("Slot") & 0xFF;
	                    if (j >= 0 && j < inventory.getSize())
	                    {
	                    	net.minecraft.server.v1_14_R1.ItemStack rawStock = net.minecraft.server.v1_14_R1.ItemStack.a(comp);
	                    	//rawStock.setTag(nbttagcompound);
	                    	ItemStack stack = CraftItemStack.asBukkitCopy(rawStock);
	                    	ISoliniaItem solItem = null;
	                    	try
	                    	{
	                    		solItem = SoliniaItemAdapter.Adapt(stack);
	                    	} catch (SoliniaItemException e)
	                    	{
	                    		
	                    	}
	                    	
	                    	if (solItem == null)
	                    	{
	                    		// Try old system
	                    		// old system
	                    		ItemMeta itemMeta = stack.getItemMeta();
	                    		if (stack.getItemMeta() != null)
	                    		{
		                    		NamespacedKey soliniaIdKey = new NamespacedKey(Bukkit.getPluginManager().getPlugin("Solinia3Core"), "soliniaid");
		                    		CustomItemTagContainer tagContainer = itemMeta.getCustomTagContainer();
		                    		if(tagContainer.hasCustomTag(soliniaIdKey , ItemTagType.STRING)) {
		                    			itemMeta.getCustomTagContainer().setCustomTag(soliniaIdKey, ItemTagType.INTEGER, Integer.parseInt(tagContainer.getCustomTag(soliniaIdKey, ItemTagType.STRING)));
		                    			stack.setItemMeta(itemMeta);
		                    		}
		                    		if(tagContainer.hasCustomTag(soliniaIdKey , ItemTagType.INTEGER)) {
		                    		    int itemId = tagContainer.getCustomTag(soliniaIdKey, ItemTagType.INTEGER);
		                    		    try
		                    			{
		                    				solItem = StateManager.getInstance().getConfigurationManager().getItem(itemId);
		                    			} catch (CoreStateInitException e)
		                    			{
		                    				
		                    			}
		                    		}
		                    		
		                    		if (solItem == null)
		                    		{
			                    		// Classic method
			                    		net.minecraft.server.v1_14_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(stack);
			                    		NBTTagCompound compound = (nmsStack.hasTag()) ? nmsStack.getTag() : new NBTTagCompound();
	
			                    		String soliniaid = compound.getString("soliniaid");
			                    		if (soliniaid.matches("-?\\d+"))
			                    		{
			                    			int itemId = Integer.parseInt(soliniaid);
			                    			try
			                    			{
			                    				solItem = StateManager.getInstance().getConfigurationManager().getItem(itemId);
			                    			} catch (CoreStateInitException e)
			                    			{
			                    				
			                    			}
			                    		}
		                    		}
	                    		}
	                    	}
	                    	
	                    	if (solItem != null)
	                    	{
	                    		stack = solItem.asItemStack();
	                    	}
	                    	//System.out.println(stack.getType().name());
	                      inventory.setItem(j,stack); 
	                    }
	                  } 
	                
	                String serial = new String(Base64.encodeBase64(ItemStackUtils
	        				.itemStackArrayToYamlString(inventory.getContents()).getBytes()));
	                
	                StateManager.getInstance().getConfigurationManager().getPatchesRepo().getByKey(1).getPlayerRestoreInventory().put(entity, serial);
	                System.out.println("Wrote " + entity);
	                
//	                return;
				} catch (Exception e)
				{
					e.printStackTrace();
				}
                
			}
		} catch (CoreStateInitException e)
		{
			
		}
		
		
		// Restore to chars
		try
		{
			for(ISoliniaPlayer player : StateManager.getInstance().getConfigurationManager().getCharacters())
			{
				player.setBase64BankContents("");
			}
			
			for (String entity : StateManager.getInstance().getConfigurationManager().getPatchesRepo().getByKey(1).getPlayerRestoreInventory().keySet()) {
				// First try to find the player we are going to give this to
				//System.out.println("Finding first player with UUID: " + entity);
				if (entity.startsWith("83bae10e-8353-4ed7-8fdb-8b37a981ab9a"))
					System.out.println("We doing target");
				for(ISoliniaPlayer player : StateManager.getInstance().getConfigurationManager().getCharactersByPlayerUUID(UUID.fromString(entity)))
				{
					if (!player.isPlayable())
						continue;
						
					String base64inventory = StateManager.getInstance().getConfigurationManager().getPatchesRepo().getByKey(1).getPlayerRestoreInventory().get(entity);
					player.setBase64BankContents(base64inventory);
						if (entity.startsWith("83bae10e-8353-4ed7-8fdb-8b37a981ab9a"))
						{
							System.out.println("Restored bank for player " + player.getFullName());
							System.out.println(base64inventory);
						}
						
					break;
				}
			}
			
			System.out.println("Done");
		} catch (CoreStateInitException e)
		{
			
		}
		
	}
	
	public Inventory listToInventory(NBTTagList nbttaglist) {
		  TileEntityChest tileentitychest = new TileEntityChest();
		  for(int i = 0; i < nbttaglist.size(); i++) {
		    NBTTagCompound nbttagcompound = (NBTTagCompound)nbttaglist.get(i);
		    int j = nbttagcompound.getByte("Slot") & 0xFF;
		    if(j >= 0 && j < 27) {
		      tileentitychest.setItem(j, net.minecraft.server.v1_14_R1.ItemStack.a(nbttagcompound));
		    }
		  }
		  return new CraftInventory(tileentitychest);
		}
	
	private static String usingBufferedReader(String filePath) 
	{
	    StringBuilder contentBuilder = new StringBuilder();
	    try (BufferedReader br = new BufferedReader(new FileReader(filePath))) 
	    {
	 
	        String sCurrentLine;
	        while ((sCurrentLine = br.readLine()) != null) 
	        {
	            contentBuilder.append(sCurrentLine).append("\n");
	        }
	    } 
	    catch (IOException e) 
	    {
	        e.printStackTrace();
	    }
	    return contentBuilder.toString();
	}
}
