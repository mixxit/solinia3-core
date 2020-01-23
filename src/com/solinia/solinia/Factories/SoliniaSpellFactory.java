package com.solinia.solinia.Factories;

import com.google.gson.Gson;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaNPC;
import com.solinia.solinia.Models.SoliniaSpell;

public class SoliniaSpellFactory {

	public static void CreateSpellCopy(int spellid, String name) throws Exception {
		try
		{
			SoliniaSpell source = (SoliniaSpell) StateManager.getInstance().getConfigurationManager().getSpell(spellid);
			
			if (source == null)
				throw new Exception("Source Spell could not be found!");
			
			Gson gson= new Gson();
			String tmp = gson.toJson(source);
			SoliniaSpell obj = gson.fromJson(tmp,SoliniaSpell.class);
			obj.setId(StateManager.getInstance().getConfigurationManager().getNextSpellId());
			obj.setName(name);
			StateManager.getInstance().getConfigurationManager().addSpell(obj);
			
			System.out.println("New Spell Added: " + obj.getId() + " - " + obj.getName());
			StateManager.getInstance().getConfigurationManager().setSpellsChanged(true);

		} catch (CoreStateInitException e)
		{
			
		}
	}

}
