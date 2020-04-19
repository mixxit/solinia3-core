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
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.PlayerState;
import com.solinia.solinia.Models.SoliniaPatch;
import com.solinia.solinia.Models.SoliniaSpellClass;

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
			for(ISoliniaItem ent : StateManager.getInstance().getConfigurationManager().getItems())
			{
				if (ent.getWorth()  <2)
					continue;
				
				if (ent.isSpellscroll())
				{
					int worth = 1;
					ISoliniaSpell spell = StateManager.getInstance().getConfigurationManager().getSpell(ent.getAbilityid());
					if (spell == null)
						continue;
					
					int lowestLevel = 1000;
					
					for(SoliniaSpellClass spellClass : spell.getAllowedClasses())
					{
						if (spellClass.getMinlevel() < lowestLevel)
							lowestLevel = spellClass.getMinlevel();
					}
					int minLevel = 1;
					
					if (lowestLevel > minLevel && lowestLevel < 100)
						minLevel = lowestLevel;
					
					ent.setWorth(worth * minLevel);
					ent.setLastUpdatedTimeNow();
					continue;
				}
				
				if (ent.isArmour() || ent.isAdditionalArmour() || ent.isJewelry() || ent.isWeaponOrBowOrShield())
				{
					ent.setWorth(ent.getTier()*15);
					ent.setLastUpdatedTimeNow();
					continue;
				}
			}
			
			System.out.println("Finished");
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
