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
		
		if (itemStack.getData() != null)
		{
			try
			{
				item.setColor(itemStack.getData().getData());
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
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
			if (material.toUpperCase().contains("AXE")) {
				itemTypeName = "Axe";
				damage = 9;
			}
			if (material.toUpperCase().contains("SWORD")) {
				itemTypeName = "Sword";
				damage = 7;
			}
			if (material.toUpperCase().contains("SPADE")) {
				itemTypeName = "Staff";
				damage = 5;
			}

			damage = damage + rarityBonus;
			item.setDamage(damage);
		}

		if (!weapon) {
			if (material.toUpperCase().contains("HELM")) {
				itemTypeName = "Helm";
			}
			if (material.toUpperCase().contains("CHEST")) {
				itemTypeName = "Chestguard";
			}
			if (material.toUpperCase().contains("LEGGINGS")) {
				itemTypeName = "Greaves";
			}
			if (material.toUpperCase().contains("BOOTS")) {
				itemTypeName = "Boots";
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
		int special = Utils.RandomBetween(0, 9);
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
		case 8:
			specialName = "Of Health";
			item.setHpregen(1+rarityBonus);
			break;
		case 9:
			specialName = "Of Magic";
			item.setMpregen(1+rarityBonus);
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

	public static List<Integer> CreateClassItemSet(ISoliniaClass classtype, int armourtier, String partialname) throws SoliniaItemException {
		List<Integer> items = new ArrayList<Integer>();
		
		try
		{
		
			// Get the appropriate material for the class and generate the base item
			ISoliniaItem headItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.valueOf(classtype.getDefaultHeadMaterial().toUpperCase())));
			ISoliniaItem chestItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.valueOf(classtype.getDefaultChestMaterial().toUpperCase())));
			ISoliniaItem legsItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.valueOf(classtype.getDefaultLegsMaterial().toUpperCase())));
			ISoliniaItem feetItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.valueOf(classtype.getDefaultFeetMaterial().toUpperCase())));
			ISoliniaItem handItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.valueOf(classtype.getDefaulthandMaterial().toUpperCase())));
			ISoliniaItem offhandItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.valueOf(classtype.getDefaultoffHandMaterial().toUpperCase())));

			items.add(headItem.getId());
			items.add(chestItem.getId());
			items.add(legsItem.getId());
			items.add(feetItem.getId());
			items.add(handItem.getId());
			items.add(offhandItem.getId());
			
			for(Integer i : items)
			{
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(i);
				List<String> classNames = new ArrayList<String>();
				classNames.add(classtype.getName().toUpperCase());
				item.setAllowedClassNames(classNames);

				item.setWorth(armourtier*25);
				// Randomise the stats of the class armour so we get more unique content in each dungeon
				int rarityChance = Utils.RandomBetween(1, 100);
				int rarityBonus = 0;
				String rarityName = "";

				if (rarityChance > 80) {
					rarityName = "Uncommon ";
					rarityBonus = 1;
				}

				if (rarityChance > 90) {
					rarityName = "Rare ";
					rarityBonus = 3;
				}

				if (rarityChance > 97) {
					rarityName = "Legendary ";
					rarityBonus = 5;
				}
				
				item.setDisplayname(rarityName + classtype.getItemArmorTypeName(item.getBasename().toUpperCase()) + " " + partialname);
				
				int tierMin = 0;
				int tierMax = armourtier * 5;
				if (armourtier > 1)
					tierMin =- 5;
				
				int classStrBonus = classtype.getItemGenerationBonus("strength");
				int classStaBonus = classtype.getItemGenerationBonus("stamina");
				int classAgiBonus = classtype.getItemGenerationBonus("agility");
				int classDexBonus = classtype.getItemGenerationBonus("dexterity");
				int classIntBonus = classtype.getItemGenerationBonus("intelligence");
				int classWisBonus = classtype.getItemGenerationBonus("wisdom");
				int classChaBonus = classtype.getItemGenerationBonus("charisma");

				// Unless there is a bonus defined, the class doesnt seem to use that statistic
				
				if (classStrBonus > 0)
					item.setStrength(Utils.RandomBetween(tierMin, tierMax + rarityBonus + classStrBonus));
				if (classStaBonus > 0)
					item.setStamina(Utils.RandomBetween(tierMin, tierMax + rarityBonus + classStaBonus));
				if (classAgiBonus > 0)
					item.setAgility(Utils.RandomBetween(tierMin, tierMax + rarityBonus + classAgiBonus));
				if (classDexBonus > 0)
					item.setDexterity(Utils.RandomBetween(tierMin, tierMax + rarityBonus + classDexBonus));
				if (classIntBonus > 0)
					item.setIntelligence(Utils.RandomBetween(tierMin, tierMax + rarityBonus+classIntBonus));
				if (classWisBonus > 0)
					item.setWisdom(Utils.RandomBetween(tierMin, tierMax + rarityBonus+classWisBonus));
				if (classChaBonus > 0)
					item.setCharisma(Utils.RandomBetween(tierMin, tierMax + rarityBonus+classChaBonus));
				
				// mana regen
				// hp regen
				// TODO class procs?
			}
		
		} catch (CoreStateInitException e)
		{
			return new ArrayList<Integer>();
		}
		
		return items;
	}
}
