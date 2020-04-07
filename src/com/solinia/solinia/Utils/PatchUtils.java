package com.solinia.solinia.Utils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.codec.binary.Base64;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftInventoryCustom;
import org.bukkit.inventory.Inventory;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPatch;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.PlayerState;
import com.solinia.solinia.Models.SoliniaPatch;

import net.minecraft.server.v1_14_R1.MojangsonParser;
import net.minecraft.server.v1_14_R1.NBTBase;
import net.minecraft.server.v1_14_R1.NBTCompressedStreamTools;
import net.minecraft.server.v1_14_R1.NBTTagCompound;
import net.minecraft.server.v1_14_R1.NBTTagList;
import net.minecraft.server.v1_14_R1.TileEntityChest;

public class PatchUtils {
	// Used for one off patching, added in /solinia patch command for console sender
	public static void Patcher() {
		try
		{
			for(ISoliniaPlayer player : StateManager.getInstance().getConfigurationManager().getCharacters())
			{
				player.setBase64BankContents("");
			}
			
			for (String entity : StateManager.getInstance().getConfigurationManager().getPatchesRepo().getByKey(1).getPlayerRestoreInventory().keySet()) {
				// First try to find the player we are going to give this to
				//System.out.println("Finding first player with UUID: " + entity);
				if (entity.startsWith("b7f0"))
					System.out.println("We doing methaebth");
				for(ISoliniaPlayer player : StateManager.getInstance().getConfigurationManager().getCharactersByPlayerUUID(UUID.fromString(entity)))
				{
					if (!player.isPlayable())
						continue;
						
					String base64inventory = StateManager.getInstance().getConfigurationManager().getPatchesRepo().getByKey(1).getPlayerRestoreInventory().get(entity);
					player.setBase64BankContents(base64inventory);
						if (entity.startsWith("b7f0"))
							System.out.println("Restored bank for player " + player.getFullName());
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
