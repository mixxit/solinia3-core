package com.solinia.solinia.Utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.solinia.solinia.Adapters.ItemStackAdapter;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.EQItem;
import com.solinia.solinia.Models.EQItemType;
import com.solinia.solinia.Models.EQMob;
import com.solinia.solinia.Models.EquipmentSlot;
import com.solinia.solinia.Models.ItemType;

public class EQUtils {
	public static String getClassNameFromEQClassId(int id)
	{
		switch (id)
		{
		case EQCLASSIDWARRIOR: return "WARRIOR";
		case EQCLASSIDCLERIC: return "CLERIC";
		case EQCLASSIDPALADIN: return "PALADIN";
		case EQCLASSIDRANGER: return "RANGER";
		case EQCLASSIDSHADOWKNIGHT: return "SHADOWKNIGHT";
		case EQCLASSIDDRUID: return "DRUID";
		case EQCLASSIDMONK: return "MONK";
		case EQCLASSIDBARD: return "BARD";
		case EQCLASSIDROGUE: return "ROGUE";
		case EQCLASSIDSHAMAN: return "SHAMAN";
		case EQCLASSIDNECROMANCER: return "NECROMANCER";
		case EQCLASSIDWIZARD: return "WIZARD";
		case EQCLASSIDMAGICIAN: return "MAGICIAN";
		case EQCLASSIDENCHANTER: return "ENCHANTER";
		case EQCLASSIDBEASTLORD: return "BEASTLORD";
		case EQCLASSIDBERSERKER: return "BERSERKER";
		}
		
		return "";
	}
	
	public static List<Integer> getClassIdsFromClasses(EQItem eqitem)
	{
		List<Integer> classIds = new ArrayList<Integer>();
		if (((int)eqitem.getClasses() & (1L << EQCLASSIDWARRIOR-1)) != 0)
			classIds.add(EQCLASSIDWARRIOR);
		if (((int)eqitem.getClasses() & (1L << EQCLASSIDCLERIC-1 )) != 0)
			classIds.add(EQCLASSIDCLERIC );
		if (((int)eqitem.getClasses() & (1L << EQCLASSIDPALADIN-1 )) != 0)
			classIds.add(EQCLASSIDPALADIN );
		if (((int)eqitem.getClasses() & (1L << EQCLASSIDRANGER-1 )) != 0)
			classIds.add(EQCLASSIDRANGER );
		if (((int)eqitem.getClasses() & (1L << EQCLASSIDSHADOWKNIGHT-1 )) != 0)
			classIds.add(EQCLASSIDSHADOWKNIGHT );
		if (((int)eqitem.getClasses() & (1L << EQCLASSIDDRUID-1 )) != 0)
			classIds.add(EQCLASSIDDRUID );
		if (((int)eqitem.getClasses() & (1L << EQCLASSIDMONK-1 )) != 0)
			classIds.add(EQCLASSIDMONK );
		if (((int)eqitem.getClasses() & (1L << EQCLASSIDBARD-1 )) != 0)
			classIds.add(EQCLASSIDBARD );
		if (((int)eqitem.getClasses() & (1L << EQCLASSIDROGUE-1 )) != 0)
			classIds.add(EQCLASSIDROGUE );
		if (((int)eqitem.getClasses() & (1L << EQCLASSIDSHAMAN-1 )) != 0)
			classIds.add(EQCLASSIDSHAMAN );
		if (((int)eqitem.getClasses() & (1L << EQCLASSIDNECROMANCER-1 )) != 0)
			classIds.add(EQCLASSIDNECROMANCER );
		if (((int)eqitem.getClasses() & (1L << EQCLASSIDWIZARD-1 )) != 0)
			classIds.add(EQCLASSIDWIZARD );
		if (((int)eqitem.getClasses() & (1L << EQCLASSIDMAGICIAN-1 )) != 0)
			classIds.add(EQCLASSIDMAGICIAN );
		if (((int)eqitem.getClasses() & (1L << EQCLASSIDENCHANTER-1  )) != 0)
			classIds.add(EQCLASSIDENCHANTER  );
		if (((int)eqitem.getClasses() & (1L << EQCLASSIDMAGICIAN-1 )) != 0)
			classIds.add(EQCLASSIDMAGICIAN );
		if (((int)eqitem.getClasses() & (1L << EQCLASSIDBEASTLORD  )) != 0)
			classIds.add(EQCLASSIDBEASTLORD  );
		if (((int)eqitem.getClasses() & (1L << EQCLASSIDBERSERKER  )) != 0)
			classIds.add(EQCLASSIDBERSERKER  );
		return classIds;
	}
	
	public static final int EQCLASSIDWARRIOR =1;
	public static final int EQCLASSIDCLERIC =2;
	public static final int EQCLASSIDPALADIN =3;
	public static final int EQCLASSIDRANGER =4;
	public static final int EQCLASSIDSHADOWKNIGHT =5;
	public static final int EQCLASSIDDRUID =6;
	public static final int EQCLASSIDMONK =7;
	public static final int EQCLASSIDBARD =8;
	public static final int EQCLASSIDROGUE =9;
	public static final int EQCLASSIDSHAMAN =10;
	public static final int EQCLASSIDNECROMANCER =11;
	public static final int EQCLASSIDWIZARD =12;
	public static final int EQCLASSIDMAGICIAN =13;
	public static final int EQCLASSIDENCHANTER =14;
	public static final int EQCLASSIDBEASTLORD =15;
	public static final int EQCLASSIDBERSERKER =16;
	
	public static Material GetMaterialFromEQClassArmorType(EQArmorType armorType, String type)
	{
		if (type.equals("HELMET"))
		switch (armorType) {
			case PLATE:
				return Material.IRON_HELMET;
			case CHAIN:
				return Material.CHAINMAIL_HELMET;
			default:
				return Material.LEATHER_HELMET;
		}
		
		if (type.equals("CHESTPLATE"))
		switch (armorType) {
			case PLATE:
				return Material.IRON_CHESTPLATE;
			case CHAIN:
				return Material.CHAINMAIL_CHESTPLATE;
			default:
				return Material.LEATHER_CHESTPLATE;
		}
		
		if (type.equals("LEGGINGS"))
		switch (armorType) {
			case PLATE:
				return Material.IRON_LEGGINGS;
			case CHAIN:
				return Material.CHAINMAIL_LEGGINGS;
			default:
				return Material.LEATHER_LEGGINGS;
		}
		
		if (type.equals("BOOTS"))
		switch (armorType) {
			case PLATE:
				return Material.IRON_BOOTS;
			case CHAIN:
				return Material.CHAINMAIL_BOOTS;
			default:
				return Material.LEATHER_BOOTS;
		}
		
		return null;
	}
	
	public static EQArmorType EQClassArmorType(int class_id)
	{
		switch (class_id) {
		case 1:
		case 2:
		case 3:
		case 5:
		case 8:
			return EQArmorType.PLATE;
		case 4:
		case 9:
		case 10:
		case 16:
			return EQArmorType.CHAIN;
		case 6:
		case 7:
		case 15:
			return EQArmorType.LEATHER;
		case 11:
		case 12:
		case 13:
		case 14:
			return EQArmorType.CLOTH;
		default:
			return EQArmorType.CLOTH;
		}
	}
	
	public enum EQArmorType{
		PLATE,CHAIN,LEATHER,CLOTH
	}

	public static final int LEar = 1;
	public static final int Head = 2;
	public static final int Face = 3;
	public static final int REar = 4;
	public static final int Neck = 5;
	public static final int Shoulder = 6;
	public static final int Arms = 7;
	public static final int Back = 8;
	public static final int LWrist = 9;
	public static final int RWrist = 10;
	public static final int Range = 11;
	public static final int Hands = 12;
	public static final int Primary = 13;
	public static final int Secondary = 14; 
	public static final int LFinger = 15;
	public static final int RFinger = 16; 
	public static final int Chest = 17;
	public static final int Legs = 18;
	public static final int Feet = 19; 
	public static final int Belt = 20;
	public static final int Ammo = 21;
	
	public static EquipmentSlot getEquipmentSlotFromEQEmuSlot(int slots) {
		String slotStr = Integer.toBinaryString(slots);
		if ((slots & (1L << LEar)) != 0)
			return EquipmentSlot.Ears;
		if ((slots & (1L << Head)) != 0)
			return EquipmentSlot.None;
		if ((slots & (1L << Face)) != 0)
			return EquipmentSlot.None;
		if ((slots & (1L << REar)) != 0)
			return EquipmentSlot.Ears;
		if ((slots & (1L << Neck)) != 0)
			return EquipmentSlot.Neck;
		if ((slots & (1L << Shoulder)) != 0)
			return EquipmentSlot.Shoulders;
		if ((slots & (1L << Arms)) != 0)
			return EquipmentSlot.Arms;
		if ((slots & (1L << Back)) != 0)
			return EquipmentSlot.Shoulders;
		if ((slots & (1L << LWrist)) != 0)
			return EquipmentSlot.Forearms;
		if ((slots & (1L << RWrist)) != 0)
			return EquipmentSlot.Forearms;
		if ((slots & (1L << Range)) != 0)
			return EquipmentSlot.None;
		if ((slots & (1L << Hands)) != 0)
			return EquipmentSlot.Hands;
		if ((slots & (1L << Primary)) != 0)
			return EquipmentSlot.None;
		if ((slots & (1L << Secondary)) != 0)
			return EquipmentSlot.None;
		if ((slots & (1L << LFinger)) != 0)
			return EquipmentSlot.Fingers;
		if ((slots & (1L << RFinger)) != 0)
			return EquipmentSlot.Fingers;
		if ((slots & (1L << Chest)) != 0)
			return EquipmentSlot.None;
		if ((slots & (1L << Legs)) != 0)
			return EquipmentSlot.None;
		if ((slots & (1L << Feet)) != 0)
			return EquipmentSlot.None;
		if ((slots & (1L << Belt)) != 0)
			return EquipmentSlot.Waist;
		if ((slots & (1L << Ammo)) != 0)
			return EquipmentSlot.None;

		return EquipmentSlot.None;
	}
	
	public static ItemType getItemTypeFromEQItem(EQItem eqitem) {
		try
		{
		switch(EQItemType.values()[(int)eqitem.getItemtype()])
		{
		case EQItemType1HSlash:
			return ItemType.OneHandSlashing;
			case EQItemType2HSlash:
			return ItemType.TwoHandSlashing;
			case EQItemType1HPiercing:
			return ItemType.OneHandPiercing;
			case EQItemType1HBlunt:
			return ItemType.OneHandBlunt;
			case EQItemType2HBlunt:
			return ItemType.TwoHandBlunt;
			case EQItemTypeBow:
			return ItemType.BowArchery;
			case EQItemTypeUnknown1:
			return ItemType.None;
			case EQItemTypeLargeThrowing:
			return ItemType.ThrowingWeapon;
			case EQItemTypeShield:
			return ItemType.None;
			case EQItemTypeScroll:
			return ItemType.None;
			case EQItemTypeArmor:
			return ItemType.Clothing;
			case EQItemTypeMisc:
			return ItemType.None;
			case EQItemTypeLockPick:
			return ItemType.None;
			case EQItemTypeUnknown2:
			return ItemType.None;
			case EQItemTypeFood:
			return ItemType.Food;
			case EQItemTypeDrink:
			return ItemType.Drink;
			case EQItemTypeLight:
			return ItemType.None;
			case EQItemTypeCombinable:
			return ItemType.None;
			case EQItemTypeBandage:
			return ItemType.None;
			case EQItemTypeSmallThrowing:
			return ItemType.ThrowingWeapon;
			case EQItemTypeSpell:
			return ItemType.None;
			case EQItemTypePotion:
			return ItemType.Potion;
			case EQItemTypeUnknown3:
			return ItemType.None;
			case EQItemTypeWindInstrument:
			return ItemType.None;
			case EQItemTypeStringedInstrument:
			return ItemType.None;
			case EQItemTypeBrassInstrument:
			return ItemType.None;
			case EQItemTypePercussionInstrument:
			return ItemType.None;
			case EQItemTypeArrow:
			return ItemType.None;
			case EQItemTypeUnknown4:
			return ItemType.None;
			case EQItemTypeJewelry:
			return ItemType.None;
			case EQItemTypeSkull:
			return ItemType.None;
			case EQItemTypeBook:
			return ItemType.None;
			case EQItemTypeNote:
			return ItemType.None;
			case EQItemTypeKey:
			return ItemType.Key;
			case EQItemTypeCoin:
			return ItemType.None;
			case EQItemType2HPiercing:
			return ItemType.None;
			case EQItemTypeFishingPole:
			return ItemType.None;
			case EQItemTypeFishingBait:
			return ItemType.None;
			case EQItemTypeAlcohol:
			return ItemType.None;
			case EQItemTypeKey2:
			return ItemType.None;
			case EQItemTypeCompass:
			return ItemType.None;
			case EQItemTypeUnknown5:
			return ItemType.None;
			case EQItemTypePoison:
			return ItemType.None;
			case EQItemTypeUnknown6:
			return ItemType.None;
			case EQItemTypeUnknown7:
			return ItemType.None;
			case EQItemTypeMartial:
			return ItemType.Martial;
			case EQItemTypeUnknown8:
			return ItemType.None;
			case EQItemTypeUnknown9:
			return ItemType.None;
			case EQItemTypeUnknown10:
			return ItemType.None;
			case EQItemTypeUnknown11:
			return ItemType.None;
			case EQItemTypeSinging:
			return ItemType.None;
			case EQItemTypeAllInstrumentTypes:
			return ItemType.None;
			case EQItemTypeCharm:
			return ItemType.None;
			case EQItemTypeDye:
			return ItemType.None;
			case EQItemTypeAugmentation:
			return ItemType.None;
			case EQItemTypeAugmentationSolvent:
			return ItemType.None;
			case EQItemTypeAugmentationDistiller:
			return ItemType.None;
			case EQItemTypeUnknown12:
			return ItemType.None;
			case EQItemTypeFellowshipKit:
			return ItemType.None;
			case EQItemTypeUnknown13:
			return ItemType.None;
			case EQItemTypeRecipe:
			return ItemType.None;
			case EQItemTypeAdvancedRecipe:
			return ItemType.None;
			case EQItemTypeJournal:
			return ItemType.None;
			case EQItemTypeAltCurrency:
			return ItemType.None;
			case EQItemTypePerfectedAugmentationDistiller:
			return ItemType.None;
			case EQItemTypeCount:
				return ItemType.None;
		}
		} catch (ArrayIndexOutOfBoundsException e)
		{
			System.out.println("Could not find mapping for EQItemType Id: " + eqitem.getItemtype());
		}
		
		return ItemType.None;
	}

	private static ItemStack getItemStackForEQItemClothing(EQItem eqitem) {
		ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
		EquipmentSlot equipmentSlot = getEquipmentSlotFromEQEmuSlot((int)eqitem.getSlots());
		if (equipmentSlot != EquipmentSlot.None)
		{
			ItemMeta i = itemStack.getItemMeta();
			String textureBase64 = "";
			
			switch(equipmentSlot)
			{
			case Fingers:
				textureBase64 = ItemStackUtils.Fingers;
				break;
			case Neck:
				textureBase64 = ItemStackUtils.Neck;
			break;
			case Shoulders:
				textureBase64 = ItemStackUtils.Shoulders;
			break;
			case Ears:
				textureBase64 = ItemStackUtils.Ears;
			break;
			case Forearms:
				textureBase64 = ItemStackUtils.Forearms;
			break;
			case Arms:
				textureBase64 = ItemStackUtils.Arms;
			break;
			case Hands:
				textureBase64 = ItemStackUtils.Hands;
			break;
			case Waist:
				textureBase64 = ItemStackUtils.Waist;
			break;
			default:
				textureBase64 = "";
				break;
			}
			
			if (!textureBase64.equals(""))
			{
				UUID skinuuid = ItemStackAdapter.getUUIDFromString(textureBase64);
				i = ItemStackAdapter.buildSkull((SkullMeta) i, skinuuid, textureBase64, null);
			}
			itemStack.setItemMeta(i);
		} else {
			List<Integer> classes = getClassIdsFromClasses(eqitem);
			int classId = 14; // default to cloth wearing
			if (classes.size() > 0)
				classId = classes.get(0);
			
			if (((int)eqitem.getSlots() & (1L << Head)) != 0)
			{
				itemStack = new ItemStack(Material.LEATHER_HELMET);
				if (GetMaterialFromEQClassArmorType(EQClassArmorType(classId),"HELMET") != null)
					itemStack = new ItemStack(GetMaterialFromEQClassArmorType(EQClassArmorType(classId),"HELMET"));
			}
			// If not then we do armour
			if (((int)eqitem.getSlots() & (1L << Chest)) != 0)
			{
				itemStack = new ItemStack(Material.LEATHER_CHESTPLATE);
				if (GetMaterialFromEQClassArmorType(EQClassArmorType(classId),"CHESTPLATE") != null)
					itemStack = new ItemStack(GetMaterialFromEQClassArmorType(EQClassArmorType(classId),"CHESTPLATE"));
			}
			if (((int)eqitem.getSlots() & (1L << Legs)) != 0)
			{
				itemStack = new ItemStack(Material.LEATHER_LEGGINGS);
				if (GetMaterialFromEQClassArmorType(EQClassArmorType(classId),"LEGGINGS") != null)
					itemStack = new ItemStack(GetMaterialFromEQClassArmorType(EQClassArmorType(classId),"LEGGINGS"));
			}
			if (((int)eqitem.getSlots() & (1L << Feet)) != 0)
			{
				itemStack = new ItemStack(Material.LEATHER_BOOTS);
				if (GetMaterialFromEQClassArmorType(EQClassArmorType(classId),"BOOTS") != null)
					itemStack = new ItemStack(GetMaterialFromEQClassArmorType(EQClassArmorType(classId),"BOOTS"));
			}
			if (((int)eqitem.getSlots() & (1L << Secondary)) != 0)
			{
				itemStack = new ItemStack(Material.SHIELD);
			}
		}
		
		return itemStack;
	}
	
	public static ItemStack getItemStackFromEQItem(EQItem eqitem) {
		ItemType itemType = getItemTypeFromEQItem(eqitem);
		if (itemType.equals(ItemType.OneHandSlashing))
			return new ItemStack(Material.IRON_SWORD);
		if (itemType.equals(ItemType.TwoHandSlashing))
			return new ItemStack(Material.IRON_SWORD);
		if (itemType.equals(ItemType.OneHandBlunt))
			return new ItemStack(Material.IRON_SHOVEL);
		if (itemType.equals(ItemType.TwoHandBlunt))
			return new ItemStack(Material.IRON_SHOVEL);
		if (itemType.equals(ItemType.OneHandPiercing))
			return new ItemStack(Material.IRON_SWORD);
		if (itemType.equals(ItemType.TwoHandPiercing))
			return new ItemStack(Material.IRON_SWORD);
		if (itemType.equals(ItemType.BowArchery))
			return new ItemStack(Material.BOW);
		if (itemType.equals(ItemType.Clothing))
			return getItemStackForEQItemClothing(eqitem);
		
		return new ItemStack(Material.PLAYER_HEAD);
	}

	public static int getSolClassIdFromEQClassId(int classid) {
		switch (classid)
		{
		case EQCLASSIDWARRIOR: return 1;
		case EQCLASSIDCLERIC: return 2;
		case EQCLASSIDRANGER: return 3;
		case EQCLASSIDROGUE: return 4;
		case EQCLASSIDWIZARD: return 5;
		case EQCLASSIDPALADIN: return 6;
		case EQCLASSIDSHADOWKNIGHT: return 7;
		case EQCLASSIDSHAMAN: return 8;
		case EQCLASSIDDRUID: return 9;
		case EQCLASSIDBARD: return 10;
		case EQCLASSIDMAGICIAN: return 11;
		case EQCLASSIDMONK: return 12;
		case EQCLASSIDNECROMANCER: return 13;
		case EQCLASSIDENCHANTER: return 14;
		case EQCLASSIDBEASTLORD: return 15;
		case EQCLASSIDBERSERKER: return 16;
		}
		
		return 1;
	}

	public static EQMob getEQMobFromJsonFile(String filename) throws IOException {
		return JsonUtils.getEQMobFromJson(new String(Files.readAllBytes(Paths.get(StateManager.getInstance().getPlugin().getDataFolder() + "/" + "importmobs"+"/"+filename)), StandardCharsets.UTF_8));
	}
	
	public static EQItem getEQItemFromJsonFile(String filename) throws IOException {
		return JsonUtils.getEQItemFromJson(new String(Files.readAllBytes(Paths.get(StateManager.getInstance().getPlugin().getDataFolder() + "/" + "importitems"+"/"+filename)), StandardCharsets.UTF_8));
	}
}
