package com.solinia.solinia.Factories;

import org.bukkit.inventory.ItemStack;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaItemException;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaItem;

public class SoliniaItemFactory {
	public static ISoliniaItem CreateItem(ItemStack itemStack) throws SoliniaItemException, CoreStateInitException
	{
		SoliniaItem item = new SoliniaItem();
		item.setId(StateManager.getInstance().getConfigurationManager().getNextItemId());
		item.setBasename(itemStack.getType().name());
		item.setDisplayname(itemStack.getType().name());
		
		return item;
	}

}
