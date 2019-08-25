package com.solinia.solinia.Adapters;

import org.bukkit.inventory.ItemStack;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaItemException;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Utils.ItemStackUtils;

public class SoliniaItemAdapter {
	public static ISoliniaItem Adapt(ItemStack itemStack) throws SoliniaItemException, CoreStateInitException
	{
		if (!ItemStackUtils.IsSoliniaItem(itemStack))
			throw new SoliniaItemException("Not a valid solinia item");
		
		return StateManager.getInstance().getConfigurationManager().getItem(itemStack);
		
	}
}
