package com.solinia.solinia.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.Fellowship;
import com.solinia.solinia.Models.PlayerState;

public class PatchUtils {
	// Used for one off patching, added in /solinia patch command for console sender
	public static void Patcher() {
		try {

			for (ISoliniaItem item : StateManager.getInstance().getConfigurationManager().getItems()) {
				boolean changed = false;

				// neck
				if (item.getTexturebase64().equals(
						"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODRhYjc3ZWVmYWQwYjBjZGJkZjMyNjFhN2E0NzI5ZDU1MDRkNmY5NmQzYzE2MjgzMjE5NzQ0M2ViZTM0NmU2In19fQ==")) {
					ISoliniaItem newItem = StateManager.getInstance().getConfigurationManager().getItem(226497);
					item.setTexturebase64(newItem.getTexturebase64());
					changed = true;
				}

				// shoulders
				if (item.getTexturebase64().equals(
						"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDFjYTdjZWY3YmMyOTI3ZWI5NGQ0YTY5MGE0MTQ4YTIxNDk4MjJlM2E2MGMwNjExYWEyYTNhNjUzM2I3NzE1In19fQ==")) {
					ISoliniaItem newItem = StateManager.getInstance().getConfigurationManager().getItem(226500);
					item.setTexturebase64(newItem.getTexturebase64());
					changed = true;
				}

				// fingers
				if (item.getTexturebase64().equals(
						"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjE4M2M4OGRiOTg0MjZjNjRjMzdlNmQ3ODlkNGVjMWUzZGU0M2VmYWFmZTRiZTE2MTk2MWVmOTQzZGJlODMifX19")) {
					ISoliniaItem newItem = StateManager.getInstance().getConfigurationManager().getItem(226498);
					item.setTexturebase64(newItem.getTexturebase64());
					changed = true;
				}

				// ears
				if (item.getTexturebase64().equals(
						"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmFiYTc0ZDgxMmYzYzVlOTdhZDBmMWU2Y2IxZDI0ZmM5ZTEzNzg4MTk2Y2YxYmM0NzMyMTFmZjE0MmJlYWIifX19")) {
					ISoliniaItem newItem = StateManager.getInstance().getConfigurationManager().getItem(226499);
					item.setTexturebase64(newItem.getTexturebase64());
					changed = true;
				}

				// forearms
				if (item.getTexturebase64().equals(
						"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDk2NDk2ODVjM2FkZmJkN2U2NWY5OTA1ZjcwNWZjNTY3NGJlNGM4ZWE1YTVkNmY1ZjcyZThlYmFkMTkyOSJ9fX0=")) {
					ISoliniaItem newItem = StateManager.getInstance().getConfigurationManager().getItem(226503

					);
					item.setTexturebase64(newItem.getTexturebase64());
					changed = true;
				}

				// arms
				if (item.getTexturebase64().equals(
						"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDk2NDk2ODVjM2FkZmJkN2U2NWY5OTA1ZjcwNWZjNTY3NGJlNGM4ZWE1YTVkNmY1ZjcyZThlYmFkMTkyOSJ9fX0=")) {
					ISoliniaItem newItem = StateManager.getInstance().getConfigurationManager().getItem(226504

					);
					item.setTexturebase64(newItem.getTexturebase64());
					changed = true;
				}

				// hands
				if (item.getTexturebase64().equals(
						"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDk2NDk2ODVjM2FkZmJkN2U2NWY5OTA1ZjcwNWZjNTY3NGJlNGM4ZWE1YTVkNmY1ZjcyZThlYmFkMTkyOSJ9fX0=")) {
					ISoliniaItem newItem = StateManager.getInstance().getConfigurationManager().getItem(226502);
					item.setTexturebase64(newItem.getTexturebase64());
					changed = true;
				}

				// waist
				if (item.getTexturebase64().equals(
						"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDk2NDk2ODVjM2FkZmJkN2U2NWY5OTA1ZjcwNWZjNTY3NGJlNGM4ZWE1YTVkNmY1ZjcyZThlYmFkMTkyOSJ9fX0=")) {
					ISoliniaItem newItem = StateManager.getInstance().getConfigurationManager().getItem(226501);
					item.setTexturebase64(newItem.getTexturebase64());
					changed = true;
				}
				
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
