package com.solinia.solinia.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.EquipmentSlot;
import com.solinia.solinia.Models.Fellowship;
import com.solinia.solinia.Models.PlayerState;

public class PatchUtils {
	// Used for one off patching, added in /solinia patch command for console sender
	public static void Patcher() {
		try {

			for (ISoliniaPlayer entity : StateManager.getInstance().getConfigurationManager().getCharacters()) {
				List<Integer> spellsToRemove = new ArrayList<Integer>();
				for (Integer spellId : entity.getSpellBookSpellIds())
				{
					ISoliniaSpell spell = StateManager.getInstance().getConfigurationManager().getSpell(spellId);
					if (spell == null && !spellsToRemove.contains(spellId))
						spellsToRemove.add(spellId);
				}
				
				if (spellsToRemove.size() < 1)
					continue;
				
				for(Integer spellId : spellsToRemove)
				{
					entity.destroySpellbookSpellId(spellId);
				}
			}

		} catch (CoreStateInitException e) {

		}

	}
}
