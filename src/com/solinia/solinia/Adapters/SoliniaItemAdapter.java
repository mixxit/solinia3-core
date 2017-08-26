package com.solinia.solinia.Adapters;

import org.bukkit.inventory.ItemStack;

import com.solinia.solinia.Factories.SoliniaItemFactory;
import com.solinia.solinia.Interfaces.ISoliniaItem;

public class SoliniaItemAdapter {
	public ISoliniaItem Adapt(ItemStack itemStack)
	{
		return SoliniaItemFactory.CreateItem(itemStack);
	}
}
