package com.solinia.solinia.Adapters;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaItemException;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Managers.StateManager;

public class SoliniaItemAdapter {
	public static ISoliniaItem Adapt(ItemStack itemStack) throws SoliniaItemException, CoreStateInitException
	{
		if (!(itemStack.getEnchantmentLevel(Enchantment.OXYGEN) > 999))
			throw new SoliniaItemException("Not a valid solinia item");
		
		return StateManager.getInstance().getConfigurationManager().getItem(itemStack);
		
	}
}
