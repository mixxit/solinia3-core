package com.solinia.solinia.Factories;

import org.bukkit.Location;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaCraftCreationException;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaCraft;

public class SoliniaCraftFactory {
	public static SoliniaCraft Create(String craftname, int x, int y, int z, boolean operatorCreated) throws CoreStateInitException, SoliniaCraftCreationException {
		if (StateManager.getInstance().getConfigurationManager().getCraft(craftname.toUpperCase()) != null)
			throw new SoliniaCraftCreationException("Craft already exists");
		
		SoliniaCraft craft = new SoliniaCraft();
		craft.setId(StateManager.getInstance().getConfigurationManager().getNextCraftId());
		craft.setRecipeName(craftname.toUpperCase());
		craft.setItem1(x);
		craft.setItem2(y);
		craft.setOutputItem(z);
		craft.setOperatorCreated(operatorCreated);
		
		StateManager.getInstance().getConfigurationManager().addCraft(craft);
		return craft;
	}
}
