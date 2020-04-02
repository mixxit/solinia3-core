package com.solinia.solinia.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.EquipmentSlot;
import com.solinia.solinia.Models.Fellowship;
import com.solinia.solinia.Models.PlayerState;

public class PatchUtils {
	// Used for one off patching, added in /solinia patch command for console sender
	public static void Patcher() {
		try {

			for (ISoliniaItem item : StateManager.getInstance().getConfigurationManager().getItems()) {
				if (!item.isAdditionalArmour() && !item.isJewelry())
					continue;
				if (item.getTexturebase64() == null)
					continue;
				
				if (!item.getBasename().equals("LEGACY_SKULL_ITEM"))
					continue;

				item.setBasename("PLAYER_HEAD");

				boolean changed = true;


				if (changed == true)
				{
					item.setLastUpdatedTimeNow();
					System.out.println("updated " + item.getDisplayname());
				}
			}

		} catch (CoreStateInitException e) {

		}

	}
}
