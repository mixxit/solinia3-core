package com.solinia.solinia.Factories;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaItemException;
import com.solinia.solinia.Interfaces.ISoliniaClass;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Managers.ConfigurationManager;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.EquipmentSlot;
import com.solinia.solinia.Models.ItemType;
import com.solinia.solinia.Models.SoliniaItem;
import com.solinia.solinia.Utils.ItemStackUtils;
import com.solinia.solinia.Utils.TextUtils;
import com.solinia.solinia.Utils.Utils;

public class SoliniaItemFactory {
	public static ISoliniaItem CreateItem(ItemStack itemStack) throws SoliniaItemException, CoreStateInitException {
		StateManager.getInstance().getConfigurationManager().setItemsChanged(true);
		SoliniaItem item = new SoliniaItem();
		item.setId(StateManager.getInstance().getConfigurationManager().getNextItemId());
		item.setBasename(itemStack.getType().name());
		item.setDisplayname(itemStack.getType().name());
		item.setLastUpdatedTimeNow();
		
		item.setItemType(ItemType.None);
		
		// ItemType auto configuration
		if (ConfigurationManager.HandMaterials.contains(item.getBasename().toUpperCase()))
		{
			switch (Utils.getDefaultSkillForMaterial(item.asItemStack().getType()))
			{
				case "SLASHING":
					item.setItemType(ItemType.OneHandSlashing);
					break;
				case "PIERCING":
					item.setItemType(ItemType.OneHandPiercing);
					break;
				case "CRUSHING":
					item.setItemType(ItemType.OneHandBlunt);
					break;
				case "ARCHERY":
					item.setItemType(ItemType.BowArchery);
					break;
				default:
					item.setItemType(ItemType.OneHandBlunt);
					break;
			}
		}
		
		if (ConfigurationManager.ArmourMaterials.contains(item.getBasename().toUpperCase()))
		{
			item.setItemType(ItemType.Clothing);
		}
		
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
		
		if (ItemStackUtils.isSkullItem(itemStack))
			item.setTexturebase64(ItemStackUtils.getSkullTexture(itemStack));
		
		if (itemStack.getType().name().equals("WRITTEN_BOOK"))
	    {
			BookMeta bookMeta = (BookMeta) itemStack.getItemMeta();
			item.setBookAuthor(bookMeta.getAuthor());
			item.setBookPages(bookMeta.getPages());
			item.setDisplayname(bookMeta.getTitle());
	    }
		
		StateManager.getInstance().getConfigurationManager().addItem(item);
		//System.out.println("New Item Added: " + item.getId() + " - " + item.getDisplayname());
		StateManager.getInstance().getConfigurationManager().setItemsChanged(true);
		return item;
	}
	
	public static ISoliniaItem CreateItemCopy(ISoliniaItem originalItem) throws SoliniaItemException, CoreStateInitException {
		StateManager.getInstance().getConfigurationManager().setItemsChanged(true);
		ItemStack itemStack = originalItem.asItemStack();		
		
		SoliniaItem item = new SoliniaItem();
		item.setId(StateManager.getInstance().getConfigurationManager().getNextItemId());
		item.setBasename(itemStack.getType().name());
		item.setDisplayname(originalItem.getDisplayname());
		item.setEquipmentSlot(originalItem.getEquipmentSlot());
		item.setDamage(originalItem.getDamage());
		item.setAC(originalItem.getAC());
		item.setLastUpdatedTimeNow();
		item.setItemType(originalItem.getItemType());
		item.setWeaponDelay(originalItem.getWeaponDelay());
		item.setAugmentation(originalItem.isAugmentation());
		item.setAugmentationFitsSlotType(originalItem.getAugmentationFitsSlotType());
		item.setAllowedClassNames(originalItem.getAllowedClassNames());
		item.setAllowedRaceNames(originalItem.getAllowedRaceNames());
		
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
		
		if (ItemStackUtils.isSkullItem(itemStack))
			item.setTexturebase64(ItemStackUtils.getSkullTexture(itemStack));
		
		if (itemStack.getType().name().equals("WRITTEN_BOOK"))
	    {
			BookMeta bookMeta = (BookMeta) itemStack.getItemMeta();
			item.setBookAuthor(bookMeta.getAuthor());
			item.setBookPages(bookMeta.getPages());
			item.setDisplayname(bookMeta.getTitle());
	    }
		
		StateManager.getInstance().getConfigurationManager().addItem(item);
		//System.out.println("New Item Added: " + item.getId() + " - " + item.getDisplayname());
		StateManager.getInstance().getConfigurationManager().setItemsChanged(true);
		return item;
	}

	public static List<Integer> CreateClassItemSet(ISoliniaClass classtype, int armourtier, String partialname, boolean prefixClassName, String discoverer) throws SoliniaItemException {
		if (classtype == null)
			return new ArrayList<Integer>();
		
		List<Integer> items = new ArrayList<Integer>();
		
		try
		{
			StateManager.getInstance().getConfigurationManager().setItemsChanged(true);
			// Get the appropriate material for the class and generate the base item
			ISoliniaItem headItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.valueOf(classtype.getDefaultHeadMaterial().toUpperCase())));
			headItem.setDiscoverer(discoverer);
			headItem.setAppearanceId(classtype.getAppearanceId());
			if (classtype.getDefaultHeadMaterial().toUpperCase().equals("LEATHER_HELMET") && classtype.getLeatherRgbDecimal() > 0)
				headItem.setLeatherRgbDecimal(classtype.getLeatherRgbDecimal());
			
			ISoliniaItem chestItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.valueOf(classtype.getDefaultChestMaterial().toUpperCase())));
			chestItem.setDiscoverer(discoverer);
			chestItem.setAppearanceId(classtype.getAppearanceId());
			if (classtype.getDefaultChestMaterial().toUpperCase().equals("LEATHER_CHESTPLATE") && classtype.getLeatherRgbDecimal() > 0)
				chestItem.setLeatherRgbDecimal(classtype.getLeatherRgbDecimal());

			ISoliniaItem legsItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.valueOf(classtype.getDefaultLegsMaterial().toUpperCase())));
			legsItem.setDiscoverer(discoverer);
			legsItem.setAppearanceId(classtype.getAppearanceId());
			if (classtype.getDefaultLegsMaterial().toUpperCase().equals("LEATHER_LEGGINGS") && classtype.getLeatherRgbDecimal() > 0)
				legsItem.setLeatherRgbDecimal(classtype.getLeatherRgbDecimal());
			
			ISoliniaItem feetItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.valueOf(classtype.getDefaultFeetMaterial().toUpperCase())));
			feetItem.setDiscoverer(discoverer);
			feetItem.setAppearanceId(classtype.getAppearanceId());
			if (classtype.getDefaultFeetMaterial().toUpperCase().equals("LEATHER_BOOTS") && classtype.getLeatherRgbDecimal() > 0)
				feetItem.setLeatherRgbDecimal(classtype.getLeatherRgbDecimal());
			
			ISoliniaItem handItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.valueOf(classtype.getDefaulthandMaterial().toUpperCase())));
			handItem.setDiscoverer(discoverer);
			handItem.setItemType(classtype.getDefaultHandItemType());
			handItem.setAppearanceId(classtype.getAppearanceId());
			ISoliniaItem offhandItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.valueOf(classtype.getDefaultoffHandMaterial().toUpperCase())));
			offhandItem.setDiscoverer(discoverer);
			offhandItem.setItemType(classtype.getDefaultOffHandItemType());
			offhandItem.setAppearanceId(classtype.getAppearanceId());
			
			ISoliniaItem alternateHandItem = null;
			if (!classtype.getDefaultAlternateHandMaterial().toUpperCase().equals("NONE"))
			{
				alternateHandItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.valueOf(classtype.getDefaultAlternateHandMaterial().toUpperCase())));
				alternateHandItem.setDiscoverer(discoverer);
				alternateHandItem.setItemType(classtype.getDefaultAlternateHandItemType());
				alternateHandItem.setAppearanceId(classtype.getAppearanceId());
			}

			
			// Jewelry!
			ISoliniaItem neckItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.LEGACY_SKULL_ITEM));
			neckItem.setEquipmentSlot(EquipmentSlot.Neck);
			neckItem.setDiscoverer(discoverer);
			neckItem.setTexturebase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODRhYjc3ZWVmYWQwYjBjZGJkZjMyNjFhN2E0NzI5ZDU1MDRkNmY5NmQzYzE2MjgzMjE5NzQ0M2ViZTM0NmU2In19fQ==");
			ISoliniaItem shouldersItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.LEGACY_SKULL_ITEM));
			shouldersItem.setEquipmentSlot(EquipmentSlot.Shoulders);
			shouldersItem.setDiscoverer(discoverer);
			shouldersItem.setTexturebase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDFjYTdjZWY3YmMyOTI3ZWI5NGQ0YTY5MGE0MTQ4YTIxNDk4MjJlM2E2MGMwNjExYWEyYTNhNjUzM2I3NzE1In19fQ==");
			ISoliniaItem fingersItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.LEGACY_SKULL_ITEM));
			fingersItem.setEquipmentSlot(EquipmentSlot.Fingers);
			fingersItem.setDiscoverer(discoverer);
			fingersItem.setTexturebase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjE4M2M4OGRiOTg0MjZjNjRjMzdlNmQ3ODlkNGVjMWUzZGU0M2VmYWFmZTRiZTE2MTk2MWVmOTQzZGJlODMifX19");
			ISoliniaItem earsItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.LEGACY_SKULL_ITEM));
			earsItem.setEquipmentSlot(EquipmentSlot.Ears);
			earsItem.setDiscoverer(discoverer);
			earsItem.setTexturebase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmFiYTc0ZDgxMmYzYzVlOTdhZDBmMWU2Y2IxZDI0ZmM5ZTEzNzg4MTk2Y2YxYmM0NzMyMTFmZjE0MmJlYWIifX19");

			// Additional Armour!
			ISoliniaItem forearmsItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.LEGACY_SKULL_ITEM));
			forearmsItem.setEquipmentSlot(EquipmentSlot.Forearms);
			forearmsItem.setDiscoverer(discoverer);
			forearmsItem.setTexturebase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDk2NDk2ODVjM2FkZmJkN2U2NWY5OTA1ZjcwNWZjNTY3NGJlNGM4ZWE1YTVkNmY1ZjcyZThlYmFkMTkyOSJ9fX0=");
			
			ISoliniaItem armsItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.LEGACY_SKULL_ITEM));
			armsItem.setEquipmentSlot(EquipmentSlot.Arms);
			armsItem.setDiscoverer(discoverer);
			armsItem.setTexturebase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDk2NDk2ODVjM2FkZmJkN2U2NWY5OTA1ZjcwNWZjNTY3NGJlNGM4ZWE1YTVkNmY1ZjcyZThlYmFkMTkyOSJ9fX0=");
			
			ISoliniaItem handsItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.LEGACY_SKULL_ITEM));
			handsItem.setEquipmentSlot(EquipmentSlot.Hands);
			handsItem.setDiscoverer(discoverer);
			handsItem.setTexturebase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDk2NDk2ODVjM2FkZmJkN2U2NWY5OTA1ZjcwNWZjNTY3NGJlNGM4ZWE1YTVkNmY1ZjcyZThlYmFkMTkyOSJ9fX0=");

			ISoliniaItem waistItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.LEGACY_SKULL_ITEM));
			waistItem.setEquipmentSlot(EquipmentSlot.Waist);
			waistItem.setDiscoverer(discoverer);
			waistItem.setTexturebase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDk2NDk2ODVjM2FkZmJkN2U2NWY5OTA1ZjcwNWZjNTY3NGJlNGM4ZWE1YTVkNmY1ZjcyZThlYmFkMTkyOSJ9fX0=");
			
			items.add(headItem.getId());
			items.add(chestItem.getId());
			items.add(legsItem.getId());
			items.add(feetItem.getId());
			items.add(handItem.getId());
			items.add(offhandItem.getId());
			if (alternateHandItem != null)
			items.add(alternateHandItem.getId());
			items.add(neckItem.getId());
			items.add(shouldersItem.getId());
			items.add(fingersItem.getId());
			items.add(earsItem.getId());
			items.add(forearmsItem.getId());
			items.add(armsItem.getId());
			items.add(handsItem.getId());
			items.add(waistItem.getId());
			
			for(Integer i : items)
			{
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(i);
				List<String> classNames = new ArrayList<String>();
				List<String> raceNames = new ArrayList<String>();
				classNames.add(classtype.getName().toUpperCase());
				item.setAllowedClassNames(classNames);
				item.setAllowedRaceNames(raceNames);
				
				item.setWorth(armourtier*25);
				// Randomise the stats of the class armour so we get more unique content in each dungeon
				int rarityChance = Utils.RandomBetween(1, 100);
				int rarityBonus = 0;
				String rarityName = "";

				if (rarityChance > 80) {
					rarityName = "Uncommon ";
					rarityBonus = 1;
				}

				if (rarityChance > 85) {
					rarityName = "Rare ";
					rarityBonus = 2;
				}

				if (rarityChance > 92) {
					rarityName = "Legendary ";
					rarityBonus = 3;
				}
				
				if (rarityChance > 96) {
					rarityName = "Mythical ";
					rarityBonus = 4;
				}

				if (rarityChance > 98) {
					rarityName = "Ancient ";
					rarityBonus = 5;
				}

				
				String className = "";
				if (prefixClassName == true)
				{
					className = classtype.getClassItemPrefix().toLowerCase();
					className = TextUtils.FormatAsName(className);
					className += " ";
				}
				
				if (!item.isJewelry() && !item.isAdditionalArmour())
				{
					item.setDisplayname(rarityName + className + classtype.getItemArmorTypeName(item.getBasename().toUpperCase()) + " " + partialname);
				} else {
					if (item.isJewelry())
					{
						String jewelryTypeName = "Jewelry";
						
						if (item.getEquipmentSlot().equals(EquipmentSlot.Ears))
							jewelryTypeName = "Earrings";
	
						if (item.getEquipmentSlot().equals(EquipmentSlot.Neck))
							jewelryTypeName = "Necklace";
						
						if (item.getEquipmentSlot().equals(EquipmentSlot.Fingers))
							jewelryTypeName = "Rings";
	
						if (item.getEquipmentSlot().equals(EquipmentSlot.Shoulders))
							jewelryTypeName = "Cloak";
						
						
						item.setDisplayname(rarityName + className + jewelryTypeName + " " + partialname);
					}
					
					if (item.isAdditionalArmour())
					{
						String jewelryTypeName = "Clothing";
						
						if (item.getEquipmentSlot().equals(EquipmentSlot.Forearms))
							jewelryTypeName = "Bracers";
	
						if (item.getEquipmentSlot().equals(EquipmentSlot.Arms))
							jewelryTypeName = "Sleeves";
						
						if (item.getEquipmentSlot().equals(EquipmentSlot.Hands))
							jewelryTypeName = "Gloves";
	
						if (item.getEquipmentSlot().equals(EquipmentSlot.Waist))
							jewelryTypeName = "Belt";
						
						item.setDisplayname(rarityName + className + jewelryTypeName + " " + partialname);
					}
				}
				
				int baseAmount = getBaseAmount(item);
				
				int tierMin = getTierMin(item, armourtier, baseAmount);
				int tierMax = getTierMax(item, armourtier, baseAmount);
				
				setMinLevel(item, armourtier);
				
				int classStrBonus = classtype.getItemGenerationBonus("strength");
				int classStaBonus = classtype.getItemGenerationBonus("stamina");
				int classAgiBonus = classtype.getItemGenerationBonus("agility");
				int classDexBonus = classtype.getItemGenerationBonus("dexterity");
				int classIntBonus = classtype.getItemGenerationBonus("intelligence");
				int classWisBonus = classtype.getItemGenerationBonus("wisdom");
				int classChaBonus = classtype.getItemGenerationBonus("charisma");
				int classAcBonus = classtype.getItemGenerationBonus("ac");
				int classDelayBonus = classtype.getItemGenerationBonus("weapondelay");
				

				// Unless there is a bonus defined, the class doesnt seem to use that statistic
				
				if (classStrBonus > 0)
					item.setStrength(Utils.RandomBetween(tierMin, tierMax) + rarityBonus + classStrBonus);
				if (classStaBonus > 0)
					item.setStamina(Utils.RandomBetween(tierMin, tierMax) + rarityBonus + classStaBonus);
				if (classAgiBonus > 0)
					item.setAgility(Utils.RandomBetween(tierMin, tierMax) + rarityBonus + classAgiBonus);
				if (classDexBonus > 0)
					item.setDexterity(Utils.RandomBetween(tierMin, tierMax) + rarityBonus + classDexBonus);
				if (classIntBonus > 0)
					item.setIntelligence(Utils.RandomBetween(tierMin, tierMax) + rarityBonus+classIntBonus);
				if (classWisBonus > 0)
					item.setWisdom(Utils.RandomBetween(tierMin, tierMax) + rarityBonus+classWisBonus);
				if (classChaBonus > 0)
					item.setCharisma(Utils.RandomBetween(tierMin, tierMax) + rarityBonus+classChaBonus);
				
				
				
				setItemDamageAndAcAndDelay(item, armourtier, tierMin, tierMax, classAcBonus, rarityBonus, classStrBonus, classDelayBonus, item.getItemType());
				
				// mana
				item.setMana(Utils.RandomBetween(0,armourtier * 20)+rarityBonus);
								
				// hp
				item.setHp(Utils.RandomBetween(0,armourtier * 20)+rarityBonus);
				
				// resists
				item.setColdResist(Utils.RandomBetween(0,(int)Math.floor(armourtier * 0.7) + rarityBonus));
				item.setFireResist(Utils.RandomBetween(0,(int)Math.floor(armourtier * 0.7) + rarityBonus));
				item.setMagicResist(Utils.RandomBetween(0,(int)Math.floor(armourtier * 0.7) + rarityBonus));
				item.setPoisonResist(Utils.RandomBetween(0,(int)Math.floor(armourtier * 0.7) + rarityBonus));
				item.setDiseaseResist(Utils.RandomBetween(0,(int)Math.floor(armourtier * 0.7) + rarityBonus));
				
				// TODO class procs?
				StateManager.getInstance().getConfigurationManager().setItemsChanged(true);
			}
		
		} catch (CoreStateInitException e)
		{
			return new ArrayList<Integer>();
		}
		
		return items;
	}
	
	public static void setMinLevel(ISoliniaItem item, int tier) {
		int lvlMin = 1;
		if (tier > 1)
		{
			lvlMin = (tier-1) * 10;
		}
		
		item.setMinLevel(lvlMin);
	}

	public static int getBaseAmount(ISoliniaItem item) {
		int baseAmount = 2;
		
		if (item.isJewelry())
		{
			baseAmount = 1;
		}
		return baseAmount;
	}

	public static int getTierMin(ISoliniaItem item, int tier, int baseAmount)
	{
		int tierMin = 0;
		if (tier > 1)
			tierMin =+ (baseAmount * tier) - baseAmount;
		
		return tierMin;
	}
	
	public static int getTierMax(ISoliniaItem item, int tier, int baseAmount)
	{
		int tierMax = tier * baseAmount;
		return tierMax;
	}

	public static void setItemDamageAndAcAndDelay(ISoliniaItem item, int tier, int tierMin, int tierMax, int acBonus, int rarityBonus, int damageBonus, int delayBonus, ItemType itemType) {
		// Damage
		if (ConfigurationManager.HandMaterials.contains(item.getBasename().toUpperCase()))
		{
			if (!item.getBasename().toUpperCase().equals("SHIELD"))
			{
				int dmgMin = tierMin;
				int dmgMax = tierMax;
				if (dmgMin < 6)
				{
					dmgMin = 6;
				}
				
				if (dmgMax < 7)
					dmgMax = 7;
				
				int damage = Utils.RandomBetween(dmgMin, dmgMax) + rarityBonus + damageBonus;
				if (itemType.equals(ItemType.TwoHandBlunt) || itemType.equals(ItemType.TwoHandPiercing) || itemType.equals(ItemType.TwoHandSlashing) )
					damage = damage*2;
				
				item.setDamage(damage);
				
				int delay = (item.getWeaponDelay() - delayBonus);
				if (itemType.equals(ItemType.TwoHandBlunt) || itemType.equals(ItemType.TwoHandPiercing) || itemType.equals(ItemType.TwoHandSlashing) )
					delay = delay+((delay/5)*3);
				
				item.setWeaponDelay(delay);
				
			} else {
				item.setAC(SoliniaItemFactory.generateArmourClass(acBonus, tier, rarityBonus));
			}
		}
		if (ConfigurationManager.ArmourMaterials.contains(item.getBasename().toUpperCase()) || item.isAdditionalArmour())
		{
			item.setAC(SoliniaItemFactory.generateArmourClass(acBonus, tier, rarityBonus));
		}
		if (item.isJewelry())
		{
			int ac = SoliniaItemFactory.generateArmourClass(acBonus, tier, rarityBonus);
			if (ac > 0)
			{
				ac = (int)Math.floor(ac/2);
				item.setAC(ac);
			}
		}
	}

	public static int generateArmourClass(int classAcBonus, int armourTier, int rarityBonus) {
		// AC
		int acMultiplier = classAcBonus;
		if (acMultiplier < 1)
			acMultiplier = 1;
		
		int acMin = 0;
		int acMax = armourTier * acMultiplier;
		if (armourTier > 1)
			acMin =+ (acMultiplier * armourTier) - acMultiplier;

		int armourClass = Utils.RandomBetween(acMin,acMax)  + rarityBonus;
		return armourClass;
	}
}
