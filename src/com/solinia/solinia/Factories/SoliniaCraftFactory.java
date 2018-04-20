package com.solinia.solinia.Factories;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaCraftCreationException;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaCraft;

public class SoliniaCraftFactory {
	public static SoliniaCraft Create(String craftname, int itemid1, int itemid2, int outputitemid, boolean operatorCreated) throws CoreStateInitException, SoliniaCraftCreationException {
		if (StateManager.getInstance().getConfigurationManager().getCraft(craftname.toUpperCase()) != null)
			throw new SoliniaCraftCreationException("Craft already exists");
		
		SoliniaCraft craft = new SoliniaCraft();
		craft.setId(StateManager.getInstance().getConfigurationManager().getNextCraftId());
		craft.setRecipeName(craftname.toUpperCase());
		craft.setItem1(itemid1);
		craft.setItem2(itemid2);
		craft.setOutputItem(outputitemid);
		craft.setOperatorCreated(operatorCreated);
		
		StateManager.getInstance().getConfigurationManager().addCraft(craft);
		return craft;
	}
}
