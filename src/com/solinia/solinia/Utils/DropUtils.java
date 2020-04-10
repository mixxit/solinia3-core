package com.solinia.solinia.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.World;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaLootDrop;
import com.solinia.solinia.Interfaces.ISoliniaLootDropEntry;
import com.solinia.solinia.Interfaces.ISoliniaLootTable;
import com.solinia.solinia.Interfaces.ISoliniaLootTableEntry;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Managers.StateManager;

public class DropUtils {
	public static void DropLoot(int lootTableId, World world, Location location, String className, int levelLimit) {
		try {
			ISoliniaLootTable table = StateManager.getInstance().getConfigurationManager().getLootTable(lootTableId);

			List<ISoliniaLootDropEntry> absoluteitems = new ArrayList<ISoliniaLootDropEntry>();
			List<ISoliniaLootDropEntry> rollitems = new ArrayList<ISoliniaLootDropEntry>();
			List<ISoliniaLootDropEntry> alwaysrollitems = new ArrayList<ISoliniaLootDropEntry>();
			List<ISoliniaLootDropEntry> alwaysrollspells = new ArrayList<ISoliniaLootDropEntry>();

			for (ISoliniaLootTableEntry entry : StateManager.getInstance().getConfigurationManager()
					.getLootTable(table.getId()).getEntries()) {
				ISoliniaLootDrop droptable = StateManager.getInstance().getConfigurationManager()
						.getLootDrop(entry.getLootdropid());
				for (ISoliniaLootDropEntry dropentry : StateManager.getInstance().getConfigurationManager()
						.getLootDrop(droptable.getId()).getEntries()) {

					// this is only used for spells
					if (className != null && !className.equals("") && levelLimit > 0)
					{
						// validate item
						ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(dropentry.getItemid());
						if (item == null)
							continue;
						
						if (!item.isSpellscroll())
							continue;

						if (item.getAbilityid() < 1)
							continue;
						
						ISoliniaSpell spell = StateManager.getInstance().getConfigurationManager().getSpell(item.getAbilityid());
						if (spell == null)
							continue;
						
						if (spell.getMinLevelClass(className) > levelLimit)
							continue;
						
						alwaysrollspells.add(dropentry);
					}
					
					if (dropentry.isAlways() == true) {
						if (dropentry.getChance() == 100)
						{
							absoluteitems.add(dropentry);
							continue;
						} else {
							alwaysrollitems.add(dropentry);
							continue;
						}
					}

					rollitems.add(dropentry);
				}
			}

			// Now we have prepared our loot list items let's choose which will
			// drop

			// System.out.println("Prepared a Loot List of ABS: " + absoluteitems.size() + "
			// and ROLL: " + rollitems.size());

			if (absoluteitems.size() == 0 && rollitems.size() == 0)
				return;

			int dropcount = 1;
			
			Random r = new Random();
			int randomInt = r.nextInt(100) + 1;

			if (rollitems.size() > 0) {
				// Based on the chance attempt to drop this item
				for (int i = 0; i < dropcount; i++) {
					ISoliniaLootDropEntry droptableentry = rollitems.get(new Random().nextInt(rollitems.size()));
					ISoliniaItem item = StateManager.getInstance().getConfigurationManager()
							.getItem(droptableentry.getItemid());

					if (item == null) {
						System.out.println("Missing item id [" + droptableentry.getItemid() + "] in lootdrop id ["
								+ droptableentry.getLootdropid() + "].. skipping..");
						continue;
					}

					if (item.isNeverDrop())
						continue;

					randomInt = r.nextInt(100) + 1;
					// System.out.println("Rolled a " + randomInt + " against a max of " +
					// droptableentry.getChance()+ " for item: " + item.getDisplayname());

					// Handle unique item checking also
					if (item.isArtifact() == true && item.isArtifactFound() == true)
						continue;

					if (randomInt <= droptableentry.getChance()) {

						// Handle unique item setting also
						if (item.isArtifact() == true && item.isArtifactFound() == false) {
							item.setArtifactFound(true);
							StateManager.getInstance().getConfigurationManager().setItemsChanged(true);
						}

						if (item.isArtifact() == true) {
							PlayerUtils.BroadcastPlayers(
									"A unique artifact [" + item.getDisplayname() + "] has been discovered!",item.asItemStack());
						}
						
						world.dropItemNaturally(location, item.asItemStack());

					}
				}
			}
			
			// always roll items
			if (alwaysrollitems.size() > 0) {
				for (int i = 0; i < alwaysrollitems.size(); i++) {
					ISoliniaItem item = StateManager.getInstance().getConfigurationManager()
							.getItem(alwaysrollitems.get(i).getItemid());
					
					for (int c = 0; c < alwaysrollitems.get(i).getCount(); c++) {

						if (item.isNeverDrop())
							continue;
						
						randomInt = r.nextInt(100) + 1;

						// Handle unique item checking also
						if (item.isArtifact() == true && item.isArtifactFound() == true)
							continue;

						if (randomInt <= alwaysrollitems.get(i).getChance()) {
							if (item.isArtifact() == true) {
								PlayerUtils.BroadcastPlayers(
										"A unique artifact [" + item.getDisplayname() + "] has been discovered!");
							}
							
							world.dropItemNaturally(location, item.asItemStack());
	
							// Handle unique item setting also
							if (item.isArtifact() == true && item.isArtifactFound() == false) {
								StateManager.getInstance().getConfigurationManager().setItemsChanged(true);
								item.setArtifactFound(true);
							}
						}

					}

				}
			}

			// we should try to roll spells one in every 8 mobs
			randomInt = r.nextInt(100) + 1;
			if (randomInt <= 13) 
			if (alwaysrollspells.size() > 0) {
				for (int i = 0; i < alwaysrollspells.size(); i++) {
					ISoliniaItem item = StateManager.getInstance().getConfigurationManager()
							.getItem(alwaysrollspells.get(i).getItemid());
					
					boolean dropped = false;
					for (int c = 0; c < alwaysrollspells.get(i).getCount(); c++) {

						if (item.isNeverDrop())
							continue;
						
						randomInt = r.nextInt(100) + 1;

						// Handle unique item checking also
						if (item.isArtifact() == true && item.isArtifactFound() == true)
							continue;
						
						if (!item.isSpellscroll())
							continue;

						System.out.println("Spell drop chance: " + randomInt + " vs " + alwaysrollspells.get(i).getChance());
						
						if (randomInt <= alwaysrollspells.get(i).getChance()) {
							if (item.isArtifact() == true) {
								PlayerUtils.BroadcastPlayers(
										"A unique artifact [" + item.getDisplayname() + "] has been discovered!");
							}
							
							world.dropItemNaturally(location, item.asItemStack());
	
							// Handle unique item setting also
							if (item.isArtifact() == true && item.isArtifactFound() == false) {
								StateManager.getInstance().getConfigurationManager().setItemsChanged(true);
								item.setArtifactFound(true);
							}
							
							dropped = true;
						}

					}
					
					// Only ever drop one spell
					if (dropped)
						break;
				}
			}

			// Always drop these items
			if (absoluteitems.size() > 0) {
				for (int i = 0; i < absoluteitems.size(); i++) {
					ISoliniaItem item = StateManager.getInstance().getConfigurationManager()
							.getItem(absoluteitems.get(i).getItemid());
					for (int c = 0; c < absoluteitems.get(i).getCount(); c++) {

						if (item.isNeverDrop())
							continue;

						// Handle unique item checking also
						if (item.isArtifact() == true && item.isArtifactFound() == true)
							continue;

						if (item.isArtifact() == true) {
							PlayerUtils.BroadcastPlayers(
									"A unique artifact [" + item.getDisplayname() + "] has been discovered!");
						}
						
						world.dropItemNaturally(location, item.asItemStack());

						// Handle unique item setting also
						if (item.isArtifact() == true && item.isArtifactFound() == false) {
							StateManager.getInstance().getConfigurationManager().setItemsChanged(true);
							item.setArtifactFound(true);
						}
					}
				}
			}
		} catch (CoreStateInitException e) {
			//
		}
	}
}
