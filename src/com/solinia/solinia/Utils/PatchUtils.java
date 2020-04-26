package com.solinia.solinia.Utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftInventory;
import org.bukkit.inventory.Inventory;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaPlayerSkill;
import net.minecraft.server.v1_14_R1.NBTTagCompound;
import net.minecraft.server.v1_14_R1.NBTTagList;
import net.minecraft.server.v1_14_R1.TileEntityChest;

public class PatchUtils {
	// Used for one off patching, added in /solinia patch command for console sender
	public static void Patcher() {
		try
		{
			for(ISoliniaPlayer solPlayer : StateManager.getInstance().getConfigurationManager().getCharacters())
			{
				boolean brokenSkills = false;
				for (SoliniaPlayerSkill skill : solPlayer.getSkills())
				{
					if (skill.getSkillName() != null && skill.getSkillType() == null)
						skill.setSkillType(Utils.getSkillType2(skill.getSkillName()));
					
					if (skill.getSkillName() == null)
						brokenSkills = true;
					
				}
				
				if (brokenSkills == true)
				{
					System.out.println(solPlayer.getFullName() + " had totally broken skills");
					solPlayer.setSkills(new ArrayList<SoliniaPlayerSkill>());
				}
				
				solPlayer.setLastUpdatedTimeNow();
			}
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
