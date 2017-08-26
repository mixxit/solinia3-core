package com.solinia.solinia.Factories;

import org.bukkit.inventory.ItemStack;

import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Models.SoliniaItem;

public class SoliniaItemFactory {
	public static ISoliniaItem CreateItem(ItemStack itemStack)
	{
		SoliniaItem item = new SoliniaItem();
		
		return item;
		
	}

}
