package com.solinia.solinia.Factories;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaItemException;
import com.solinia.solinia.Interfaces.ISoliniaClass;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaItem;
import com.solinia.solinia.Utils.Utils;

public class SoliniaItemFactory {
	public static ISoliniaItem CreateItem(ItemStack itemStack) throws SoliniaItemException, CoreStateInitException {
		SoliniaItem item = new SoliniaItem();
		item.setId(StateManager.getInstance().getConfigurationManager().getNextItemId());
		item.setBasename(itemStack.getType().name());
		item.setDisplayname(itemStack.getType().name());

		StateManager.getInstance().getConfigurationManager().addItem(item);
		System.out.println("New Item Added: " + item.getId() + " - " + item.getDisplayname());
		return item;
	}

	public static ISoliniaItem GenerateRandomLoot() throws CoreStateInitException, SoliniaItemException {
		boolean weapon = true;
		List<String> materialChoice = StateManager.getInstance().getConfigurationManager().WeaponMaterials;
		if (Utils.RandomChance(50)) {
			weapon = false;
			materialChoice = StateManager.getInstance().getConfigurationManager().ArmourMaterials;
		}

		String material = Utils.getRandomItemFromList(materialChoice);

		ItemStack itemStack = new ItemStack(Material.valueOf(material.toUpperCase()));

		SoliniaItem item = new SoliniaItem();
		item.setId(StateManager.getInstance().getConfigurationManager().getNextItemId());
		item.setBasename(itemStack.getType().name());
		item.setDisplayname(itemStack.getType().name());

		int rarityChance = Utils.RandomBetween(1, 100);
		int rarityBonus = 0;
		String rarityName = "";

		if (rarityChance > 80) {
			rarityName = "Uncommon";
			rarityBonus = 1;
		}

		if (rarityChance > 90) {
			rarityName = "Rare";
			rarityBonus = 3;
		}

		if (rarityChance > 97) {
			rarityName = "Legendary";
			rarityBonus = 5;
		}

		String itemTypeName = "";
		if (weapon) {
			int damage = 1;

			// Base Damage
			if (material.contains("AXE")) {
				itemTypeName += "Axe";
				damage = 9;
			}
			if (material.contains("SWORD")) {
				itemTypeName += "Sword";
				damage = 7;
			}
			if (material.contains("SPADE")) {
				itemTypeName += "Staff";
				damage = 5;
			}

			damage = damage + rarityBonus;
			item.setDamage(damage);
		}

		if (!weapon) {
			if (material.contains("HELM")) {
				itemTypeName += "Helm";
			}
			if (material.contains("CHEST")) {
				itemTypeName += "Chestguard";
			}
			if (material.contains("LEGGINGS")) {
				itemTypeName += "Greaves";
			}
			if (material.contains("BOOTS")) {
				itemTypeName += "Boots";
			}
		}

		String classname = "";

		// Class
		if (!Utils.RandomChance(90) && StateManager.getInstance().getConfigurationManager().getClasses().size() != 0) {
			// Add a class limit
			ISoliniaClass classobj = Utils
					.getRandomItemFromList(StateManager.getInstance().getConfigurationManager().getClasses());
			item.setAllowedClassNames(new ArrayList<String>() {
				{
					add(classobj.getName().toUpperCase());
				}
			});
			classname = classobj.getName().toLowerCase() + "s";
			classname = Utils.FormatAsName(classname);
		}

		// Stats
		item.setStrength(Utils.RandomBetween(0, 5 + rarityBonus));
		item.setStamina(Utils.RandomBetween(0, 5 + rarityBonus));
		item.setAgility(Utils.RandomBetween(0, 5 + rarityBonus));
		item.setDexterity(Utils.RandomBetween(0, 5 + rarityBonus));
		item.setIntelligence(Utils.RandomBetween(0, 5 + rarityBonus));
		item.setWisdom(Utils.RandomBetween(0, 5 + rarityBonus));
		item.setCharisma(Utils.RandomBetween(0, 5 + rarityBonus));

		// Extra special stuff
		int special = Utils.RandomBetween(0, 1);
		String specialName = "";
		switch (special) {
		case 1:
			specialName = "Of Vengeance";
			item.setStrength(item.getStrength() + 2);
			break;
		case 2:
			specialName = "Of Bravery";
			item.setStrength(item.getStamina() + 2);
			break;
		case 3:
			specialName = "Of Power";
			item.setStrength(item.getAgility() + 2);
			break;
		case 4:
			specialName = "Of Speed";
			item.setStrength(item.getDexterity() + 2);
			break;
		case 5:
			specialName = "Of Thought";
			item.setStrength(item.getIntelligence() + 2);
			break;
		case 6:
			specialName = "Of Belief";
			item.setStrength(item.getWisdom() + 2);
			break;
		case 7:
			specialName = "Of Fancy";
			item.setStrength(item.getCharisma() + 2);
			break;
		default:
			break;
		}

		// Name Builder
		String name = "";
		if (!rarityName.equals(""))
			name += rarityName + " ";

		if (!classname.equals(""))
			name += classname + " ";

		name += itemTypeName;

		if (!specialName.equals(""))
			name += " " + specialName;

		item.setDisplayname(name);

		StateManager.getInstance().getConfigurationManager().addItem(item);
		System.out.println("New Item Added: " + item.getId() + " - " + item.getDisplayname());
		return item;
	}

}
