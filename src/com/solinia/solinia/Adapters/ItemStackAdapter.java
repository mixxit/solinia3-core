package com.solinia.solinia.Adapters;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaClass;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaSpellClass;
import com.solinia.solinia.Utils.Utils;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagInt;
import net.minecraft.server.v1_12_R1.NBTTagList;
import net.minecraft.server.v1_12_R1.NBTTagString;

public class ItemStackAdapter {
	public static ItemStack Adapt(ISoliniaItem soliniaItem) {
		ItemStack stack = new ItemStack(Material.valueOf(soliniaItem.getBasename().toUpperCase()));

		if (soliniaItem.getDamage() > 0) {
			if (soliniaItem.getBasename().equals("WOOD_SWORD") || soliniaItem.getBasename().equals("STONE_SWORD")
					|| soliniaItem.getBasename().equals("IRON_SWORD") || soliniaItem.getBasename().equals("GOLD_SWORD")
					|| soliniaItem.getBasename().equals("DIAMOND_SWORD") || soliniaItem.getBasename().equals("WOOD_AXE")
					|| soliniaItem.getBasename().equals("STONE_AXE") || soliniaItem.getBasename().equals("IRON_AXE")
					|| soliniaItem.getBasename().equals("GOLD_AXE") || soliniaItem.getBasename().equals("DIAMOND_AXE")
					|| soliniaItem.getBasename().equals("WOOD_SPADE") || soliniaItem.getBasename().equals("STONE_SPADE")
					|| soliniaItem.getBasename().equals("IRON_SPADE") || soliniaItem.getBasename().equals("GOLD_SPADE")
					|| soliniaItem.getBasename().equals("DIAMOND_SPADE")) {
				net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(stack);

				NBTTagCompound compound = (nmsStack.hasTag()) ? nmsStack.getTag() : new NBTTagCompound();
				NBTTagList modifiers = new NBTTagList();
				NBTTagCompound damagecompound = new NBTTagCompound();
				damagecompound.set("AttributeName", new NBTTagString("generic.attackDamage"));
				damagecompound.set("Name", new NBTTagString("generic.attackDamage"));
				damagecompound.set("Amount", new NBTTagInt(soliniaItem.getDamage()));
				damagecompound.set("Operation", new NBTTagInt(0));
				damagecompound.set("UUIDLeast", new NBTTagInt(894654));
				damagecompound.set("UUIDMost", new NBTTagInt(2872));
				damagecompound.set("Slot", new NBTTagString("mainhand"));

				modifiers.add(damagecompound);
				compound.set("AttributeModifiers", modifiers);
				nmsStack.setTag(compound);
				stack = CraftItemStack.asBukkitCopy(nmsStack);
			}
		}

		ItemMeta i = stack.getItemMeta();
		if (soliniaItem.getBasename().equals("POTION") || soliniaItem.getBasename().equals("SPLASH_POTION")
				|| soliniaItem.getBasename().equals("LINGERING_POTION")) {
			i = (PotionMeta) stack.getItemMeta();
			PotionData data = new PotionData(PotionType.INSTANT_HEAL);
			((PotionMeta) i).setBasePotionData(data);
		}

		if (soliniaItem.getTexturebase64() != null && !soliniaItem.getTexturebase64().equals("")
				&& soliniaItem.getBasename().equals("SKULL_ITEM")) {
			UUID skinuuid = getUUIDFromString(soliniaItem.getTexturebase64());
			i = buildSkull((SkullMeta) i, skinuuid, soliniaItem.getTexturebase64(), null);
		}

		i.setUnbreakable(true);
		i.setDisplayName(soliniaItem.getDisplayname());
		List<String> loretxt = new ArrayList<String>();

		if (soliniaItem.getLore() != null) {
			String[] lorestr = soliniaItem.getLore().split("(?<=\\G.{34})");

			loretxt.addAll(Arrays.asList(lorestr));
		}

		if (soliniaItem.getDamage() > 0) {
			if (soliniaItem.getBasename().equals("BOW")) {
				loretxt.add("Modifies Arrow Dmg: " + ChatColor.GREEN + soliniaItem.getDamage() + ChatColor.RESET);
			}
		}

		String classtxt = "";
		if (soliniaItem.getAllowedClassNames().size() > 0) {
			classtxt = "Usable By: ";
			for (String classname : soliniaItem.getAllowedClassNames()) {
				classtxt += ChatColor.YELLOW + classname + ChatColor.RESET + " ";
			}
		}

		if (!classtxt.equals("")) {
			loretxt.add(classtxt);
		}

		String stattxt = "";
		if (soliniaItem.getStrength() > 0) {
			stattxt = "STR: " + ChatColor.GREEN + soliniaItem.getStrength() + ChatColor.RESET + " ";
		}

		if (soliniaItem.getStamina() > 0) {
			stattxt += "STA: " + ChatColor.GREEN + soliniaItem.getStamina() + ChatColor.RESET + " ";
		}

		if (soliniaItem.getAgility() > 0) {
			stattxt += "AGI: " + ChatColor.GREEN + soliniaItem.getAgility() + ChatColor.RESET + " ";
		}

		if (!stattxt.equals("")) {
			loretxt.add(stattxt);
		}

		stattxt = "";

		if (soliniaItem.getDexterity() > 0) {
			stattxt = "DEX: " + ChatColor.GREEN + soliniaItem.getDexterity() + ChatColor.RESET + " ";
		}

		if (soliniaItem.getIntelligence() > 0) {
			stattxt += "INT: " + ChatColor.GREEN + soliniaItem.getIntelligence() + ChatColor.RESET + " ";
		}

		if (soliniaItem.getWisdom() > 0) {
			stattxt += "WIS: " + ChatColor.GREEN + soliniaItem.getWisdom() + ChatColor.RESET + " ";
		}

		if (soliniaItem.getCharisma() > 0) {
			stattxt += "CHA: " + ChatColor.GREEN + soliniaItem.getCharisma() + ChatColor.RESET + " ";
		}

		if (!stattxt.equals("")) {
			loretxt.add(stattxt);
		}

		String resisttxt = "";

		if (soliniaItem.getFireResist() > 0) {
			resisttxt += "FR: " + ChatColor.AQUA + soliniaItem.getFireResist() + ChatColor.RESET + " ";
		}

		if (soliniaItem.getColdResist() > 0) {
			resisttxt += "CR: " + ChatColor.AQUA + soliniaItem.getColdResist() + ChatColor.RESET + " ";
		}

		if (soliniaItem.getMagicResist() > 0) {
			resisttxt += "MR: " + ChatColor.AQUA + soliniaItem.getMagicResist() + ChatColor.RESET + " ";
		}

		if (soliniaItem.getPoisonResist() > 0) {
			resisttxt += "PR: " + ChatColor.AQUA + soliniaItem.getPoisonResist() + ChatColor.RESET + " ";
		}

		if (!resisttxt.equals("")) {
			loretxt.add(resisttxt);
		}

		String regentxt = "";

		if (soliniaItem.getHpregen() > 0 || soliniaItem.getMpregen() > 0) {
			if (soliniaItem.getHpregen() > 0) {
				regentxt = ChatColor.WHITE + "HPRegen: " + ChatColor.YELLOW + soliniaItem.getHpregen()
						+ ChatColor.RESET;
			}

			if (soliniaItem.getMpregen() > 0) {
				if (!regentxt.equals(""))
					regentxt += " ";
				regentxt += ChatColor.WHITE + "MPRegen: " + ChatColor.YELLOW + soliniaItem.getMpregen()
						+ ChatColor.RESET;
			}
		}

		if (!regentxt.equals("")) {
			loretxt.add(regentxt);
		}
		
		
		if (soliniaItem.getAbilityid() > 0 && soliniaItem.isSpellscroll())
	    {
			loretxt.addAll(generateSpellLoreText(soliniaItem));
	    }
		

		i.setLore(loretxt);
		stack.setItemMeta(i);
		stack.addUnsafeEnchantment(Enchantment.OXYGEN, 1000 + soliniaItem.getId());

		if (soliniaItem.getEnchantment1() != null) {
			if (soliniaItem.getEnchantment1val() > 0) {
				try {
					Enchantment enchantment = Utils.getEnchantmentFromEnchantmentName(soliniaItem.getEnchantment1());
					stack.addUnsafeEnchantment(enchantment, soliniaItem.getEnchantment1val());
				} catch (Exception e) {
					System.out.println("WARNING: Invalid Enchantment Item on SoliniaItem: " + soliniaItem.getId());
				}
			}
		}

		if (soliniaItem.getEnchantment2() != null) {
			if (soliniaItem.getEnchantment2val() > 0) {
				try {
					Enchantment enchantment = Utils.getEnchantmentFromEnchantmentName(soliniaItem.getEnchantment2());
					stack.addUnsafeEnchantment(enchantment, soliniaItem.getEnchantment2val());
				} catch (Exception e) {
					System.out.println("WARNING: Invalid Enchantment Item on SoliniaItem: " + soliniaItem.getId());
				}
			}
		}

		if (soliniaItem.getEnchantment3() != null) {
			if (soliniaItem.getEnchantment3val() > 0) {
				try {
					Enchantment enchantment = Utils.getEnchantmentFromEnchantmentName(soliniaItem.getEnchantment3());
					stack.addUnsafeEnchantment(enchantment, soliniaItem.getEnchantment3val());
				} catch (Exception e) {
					System.out.println("WARNING: Invalid Enchantment Item on SoliniaItem: " + soliniaItem.getId());
				}
			}
		}

		if (soliniaItem.getEnchantment4() != null) {
			if (soliniaItem.getEnchantment4val() > 0) {
				try {
					Enchantment enchantment = Utils.getEnchantmentFromEnchantmentName(soliniaItem.getEnchantment4());
					stack.addUnsafeEnchantment(enchantment, soliniaItem.getEnchantment4val());
				} catch (Exception e) {
					System.out.println("WARNING: Invalid Enchantment Item on SoliniaItem: " + soliniaItem.getId());
				}
			}
		}

		if (soliniaItem.getTexturebase64() != null && !soliniaItem.getTexturebase64().equals("")
				&& soliniaItem.getBasename().equals("SKULL_ITEM")) {
			stack.setDurability((short) 3);
		}

		return stack;
	}

	private static List<String> generateSpellLoreText(ISoliniaItem soliniaItem) {
		List<String> loreTxt = new ArrayList<String>();
		ISoliniaSpell spell;
		try {
			spell = StateManager.getInstance().getConfigurationManager().getSpell(soliniaItem.getAbilityid());
			if (spell.getEffectid1() != 254 && !getSpellEffectTypeDescription(spell.getEffectid1()).contains("LIMIT_"))
				loreTxt.add(ChatColor.WHITE + "Ability: " + ChatColor.YELLOW+getSpellEffectTypeDescription(spell.getEffectid1()) + "(" + spell.getEffectBaseValue1() + ")" + ChatColor.RESET);
			if (spell.getEffectid2() != 254 && !getSpellEffectTypeDescription(spell.getEffectid2()).contains("LIMIT_"))
				loreTxt.add(ChatColor.WHITE + "Ability: " + ChatColor.YELLOW+getSpellEffectTypeDescription(spell.getEffectid2()) + "(" + spell.getEffectBaseValue2() + ")" + ChatColor.RESET);
			if (spell.getEffectid3() != 254 && !getSpellEffectTypeDescription(spell.getEffectid3()).contains("LIMIT_"))
				loreTxt.add(ChatColor.WHITE + "Ability: " + ChatColor.YELLOW+getSpellEffectTypeDescription(spell.getEffectid3()) + "(" + spell.getEffectBaseValue3() + ")" + ChatColor.RESET);
			if (spell.getEffectid4() != 254 && !getSpellEffectTypeDescription(spell.getEffectid4()).contains("LIMIT_"))
				loreTxt.add(ChatColor.WHITE + "Ability: " + ChatColor.YELLOW+getSpellEffectTypeDescription(spell.getEffectid4()) + "(" + spell.getEffectBaseValue4() + ")" + ChatColor.RESET);
			if (spell.getEffectid5() != 254 && !getSpellEffectTypeDescription(spell.getEffectid5()).contains("LIMIT_"))
				loreTxt.add(ChatColor.WHITE + "Ability: " + ChatColor.YELLOW+getSpellEffectTypeDescription(spell.getEffectid5()) + "(" + spell.getEffectBaseValue5() + ")" + ChatColor.RESET);
			if (spell.getEffectid6() != 254 && !getSpellEffectTypeDescription(spell.getEffectid6()).contains("LIMIT_"))
				loreTxt.add(ChatColor.WHITE + "Ability: " + ChatColor.YELLOW+getSpellEffectTypeDescription(spell.getEffectid6()) + "(" + spell.getEffectBaseValue6() + ")" + ChatColor.RESET);
			if (spell.getEffectid7() != 254 && !getSpellEffectTypeDescription(spell.getEffectid7()).contains("LIMIT_"))
				loreTxt.add(ChatColor.WHITE + "Ability: " + ChatColor.YELLOW+getSpellEffectTypeDescription(spell.getEffectid7()) + "(" + spell.getEffectBaseValue7() + ")" + ChatColor.RESET);
			if (spell.getEffectid8() != 254 && !getSpellEffectTypeDescription(spell.getEffectid8()).contains("LIMIT_"))
				loreTxt.add(ChatColor.WHITE + "Ability: " + ChatColor.YELLOW+getSpellEffectTypeDescription(spell.getEffectid8()) + "(" + spell.getEffectBaseValue8() + ")" + ChatColor.RESET);
			if (spell.getEffectid9() != 254 && !getSpellEffectTypeDescription(spell.getEffectid9()).contains("LIMIT_"))
				loreTxt.add(ChatColor.WHITE + "Ability: " + ChatColor.YELLOW+getSpellEffectTypeDescription(spell.getEffectid9()) + "(" + spell.getEffectBaseValue9() + ")" + ChatColor.RESET);
			if (spell.getEffectid10() != 254 && !getSpellEffectTypeDescription(spell.getEffectid10()).contains("LIMIT_"))
				loreTxt.add(ChatColor.WHITE + "Ability: " + ChatColor.YELLOW+getSpellEffectTypeDescription(spell.getEffectid10()) + "(" + spell.getEffectBaseValue10() + ")" + ChatColor.RESET);
			if (spell.getEffectid11() != 254 && !getSpellEffectTypeDescription(spell.getEffectid11()).contains("LIMIT_"))
				loreTxt.add(ChatColor.WHITE + "Ability: " + ChatColor.YELLOW+getSpellEffectTypeDescription(spell.getEffectid11()) + "(" + spell.getEffectBaseValue11() + ")" + ChatColor.RESET);
			if (spell.getEffectid12() != 254 && !getSpellEffectTypeDescription(spell.getEffectid12()).contains("LIMIT_"))
				loreTxt.add(ChatColor.WHITE + "Ability: " + ChatColor.YELLOW+getSpellEffectTypeDescription(spell.getEffectid12()) + "(" + spell.getEffectBaseValue12() + ")" + ChatColor.RESET);
			
			loreTxt.add(ChatColor.WHITE + "Mana/Power: " + ChatColor.YELLOW+spell.getMana() + ChatColor.RESET);
			loreTxt.add(ChatColor.WHITE + "Range: " + ChatColor.YELLOW+spell.getRange() + ChatColor.RESET);
			
			String classesBuilder = "";
			List<SoliniaSpellClass> allowedSpellClasses = spell.getAllowedClasses();
			int rowcount = 0;
			for (SoliniaSpellClass spellclass : allowedSpellClasses)
			{
				if (StateManager.getInstance().getConfigurationManager().getClassObj(spellclass.getClassname().toUpperCase()) == null)
					continue;
				
				classesBuilder += ChatColor.WHITE + spellclass.getClassname() + " (" + ChatColor.YELLOW + spellclass.getMinlevel() + ChatColor.WHITE + ") " + ChatColor.RESET;
				rowcount++;
				if (rowcount > 2)
				{
					loreTxt.add(classesBuilder);
					classesBuilder = "";
					
				}
			}
			
			// If we never reached 2 classes on a row, handle the overspill here			
			if (!classesBuilder.equals(""))
				loreTxt.add(classesBuilder);
			
			
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return loreTxt;
	}
	

	private static String getSpellEffectTypeDescription(Integer typeId) {
		switch(typeId)
		{
			case 0: return "CurrentHP"; 
			case 1: return "ArmorClass"; 
			case 2: return "ATK"; 
			case 3: return "MovementSpeed"; 
			case 4: return "STR"; 
			case 5: return "DEX"; 
			case 6: return "AGI"; 
			case 7: return "STA"; 
			case 8: return "INT"; 
			case 9: return "WIS"; 
			case 10: return "CHA"; 
			case 11: return "AttackSpeed"; 
			case 12: return "Invisibility"; 
			case 13: return "SeeInvis"; 
			case 14: return "WaterBreathing"; 
			case 15: return "CurrentMana"; 
			case 16: return "NPCFrenzy"; 
			case 17: return "NPCAwareness"; 
			case 18: return "Lull"; 
			case 19: return "AddFaction"; 
			case 20: return "Blind"; 
			case 21: return "Stun"; 
			case 22: return "Charm"; 
			case 23: return "Fear"; 
			case 24: return "Stamina"; 
			case 25: return "BindAffinity"; 
			case 26: return "Gate"; 
			case 27: return "CancelMagic"; 
			case 28: return "InvisVsUndead"; 
			case 29: return "InvisVsAnimals"; 
			case 30: return "ChangeFrenzyRad"; 
			case 31: return "Mez"; 
			case 32: return "SummonItem"; 
			case 33: return "SummonPet"; 
			case 34: return "Confuse"; 
			case 35: return "DiseaseCounter"; 
			case 36: return "PoisonCounter"; 
			case 37: return "DetectHostile"; 
			case 38: return "DetectMagic"; 
			case 39: return "DetectPoison"; 
			case 40: return "DivineAura"; 
			case 41: return "Destroy"; 
			case 42: return "ShadowStep"; 
			case 43: return "Berserk"; 
			case 44: return "Lycanthropy"; 
			case 45: return "Vampirism"; 
			case 46: return "ResistFire"; 
			case 47: return "ResistCold"; 
			case 48: return "ResistPoison"; 
			case 49: return "ResistDisease"; 
			case 50: return "ResistMagic"; 
			case 51: return "DetectTraps"; 
			case 52: return "SenseDead"; 
			case 53: return "SenseSummoned"; 
			case 54: return "SenseAnimals"; 
			case 55: return "Rune"; 
			case 56: return "TrueNorth"; 
			case 57: return "Levitate"; 
			case 58: return "Illusion"; 
			case 59: return "DamageShield"; 
			case 60: return "TransferItem"; 
			case 61: return "Identify"; 
			case 62: return "ItemID"; 
			case 63: return "WipeHateList"; 
			case 64: return "SpinTarget"; 
			case 65: return "InfraVision"; 
			case 66: return "UltraVision"; 
			case 67: return "EyeOfZomm"; 
			case 68: return "ReclaimPet"; 
			case 69: return "TotalHP"; 
			case 70: return "CorpseBomb"; 
			case 71: return "NecPet"; 
			case 72: return "PreserveCorpse"; 
			case 73: return "BindSight"; 
			case 74: return "FeignDeath"; 
			case 75: return "VoiceGraft"; 
			case 76: return "Sentinel"; 
			case 77: return "LocateCorpse"; 
			case 78: return "AbsorbMagicAtt"; 
			case 79: return "CurrentHPOnce"; 
			case 80: return "EnchantLight"; 
			case 81: return "Revive"; 
			case 82: return "SummonPC"; 
			case 83: return "Teleport"; 
			case 84: return "TossUp"; 
			case 85: return "WeaponProc"; 
			case 86: return "Harmony"; 
			case 87: return "MagnifyVision"; 
			case 88: return "Succor"; 
			case 89: return "ModelSize"; 
			case 90: return "Cloak"; 
			case 91: return "SummonCorpse"; 
			case 92: return "InstantHate"; 
			case 93: return "StopRain"; 
			case 94: return "NegateIfCombat"; 
			case 95: return "Sacrifice"; 
			case 96: return "Silence"; 
			case 97: return "ManaPool"; 
			case 98: return "AttackSpeed2"; 
			case 99: return "Root"; 
			case 100: return "HealOverTime"; 
			case 101: return "CompleteHeal"; 
			case 102: return "Fearless"; 
			case 103: return "CallPet"; 
			case 104: return "Translocate"; 
			case 105: return "AntiGate"; 
			case 106: return "SummonBSTPet"; 
			case 107: return "AlterNPCLevel"; 
			case 108: return "Familiar"; 
			case 109: return "SummonItemIntoBag"; 
			case 110: return "IncreaseArchery"; 
			case 111: return "ResistAll"; 
			case 112: return "CastingLevel"; 
			case 113: return "SummonHorse"; 
			case 114: return "ChangeAggro"; 
			case 115: return "Hunger"; 
			case 116: return "CurseCounter"; 
			case 117: return "MagicWeapon"; 
			case 118: return "Amplification"; 
			case 119: return "AttackSpeed3"; 
			case 120: return "HealRate"; 
			case 121: return "ReverseDS"; 
			case 122: return "ReduceSkill"; 
			case 123: return "Screech"; 
			case 124: return "ImprovedDamage"; 
			case 125: return "ImprovedHeal"; 
			case 126: return "SpellResistReduction"; 
			case 127: return "IncreaseSpellHaste"; 
			case 128: return "IncreaseSpellDuration"; 
			case 129: return "IncreaseRange"; 
			case 130: return "SpellHateMod"; 
			case 131: return "ReduceReagentCost"; 
			case 132: return "ReduceManaCost"; 
			case 133: return "FcStunTimeMod"; 
			case 134: return "LimitMaxLevel"; 
			case 135: return "LimitResist"; 
			case 136: return "LimitTarget"; 
			case 137: return "LimitEffect"; 
			case 138: return "LimitSpellType"; 
			case 139: return "LimitSpell"; 
			case 140: return "LimitMinDur"; 
			case 141: return "LimitInstant"; 
			case 142: return "LimitMinLevel"; 
			case 143: return "LimitCastTimeMin"; 
			case 144: return "LimitCastTimeMax"; 
			case 145: return "Teleport2"; 
			case 146: return "ElectricityResist"; 
			case 147: return "PercentalHeal"; 
			case 148: return "StackingCommand_Block"; 
			case 149: return "StackingCommand_Overwrite"; 
			case 150: return "DeathSave"; 
			case 151: return "SuspendPet"; 
			case 152: return "TemporaryPets"; 
			case 153: return "BalanceHP"; 
			case 154: return "DispelDetrimental"; 
			case 155: return "SpellCritDmgIncrease"; 
			case 156: return "IllusionCopy"; 
			case 157: return "SpellDamageShield"; 
			case 158: return "Reflect"; 
			case 159: return "AllStats"; 
			case 160: return "MakeDrunk"; 
			case 161: return "MitigateSpellDamage"; 
			case 162: return "MitigateMeleeDamage"; 
			case 163: return "NegateAttacks"; 
			case 164: return "AppraiseLDonChest"; 
			case 165: return "DisarmLDoNTrap"; 
			case 166: return "UnlockLDoNChest"; 
			case 167: return "PetPowerIncrease"; 
			case 168: return "MeleeMitigation"; 
			case 169: return "CriticalHitChance"; 
			case 170: return "SpellCritChance"; 
			case 171: return "CrippBlowChance"; 
			case 172: return "AvoidMeleeChance"; 
			case 173: return "RiposteChance"; 
			case 174: return "DodgeChance"; 
			case 175: return "ParryChance"; 
			case 176: return "DualWieldChance"; 
			case 177: return "DoubleAttackChance"; 
			case 178: return "MeleeLifetap"; 
			case 179: return "AllInstrumentMod"; 
			case 180: return "ResistSpellChance"; 
			case 181: return "ResistFearChance"; 
			case 182: return "HundredHands"; 
			case 183: return "MeleeSkillCheck"; 
			case 184: return "HitChance"; 
			case 185: return "DamageModifier"; 
			case 186: return "MinDamageModifier"; 
			case 187: return "BalanceMana"; 
			case 188: return "IncreaseBlockChance"; 
			case 189: return "CurrentEndurance"; 
			case 190: return "EndurancePool"; 
			case 191: return "Amnesia"; 
			case 192: return "Hate"; 
			case 193: return "SkillAttack"; 
			case 194: return "FadingMemories"; 
			case 195: return "StunResist"; 
			case 196: return "StrikeThrough"; 
			case 197: return "SkillDamageTaken"; 
			case 198: return "CurrentEnduranceOnce"; 
			case 199: return "Taunt"; 
			case 200: return "ProcChance"; 
			case 201: return "RangedProc"; 
			case 202: return "IllusionOther"; 
			case 203: return "MassGroupBuff"; 
			case 204: return "GroupFearImmunity"; 
			case 205: return "Rampage"; 
			case 206: return "AETaunt"; 
			case 207: return "FleshToBone"; 
			case 208: return "PurgePoison"; 
			case 209: return "DispelBeneficial"; 
			case 210: return "PetShield"; 
			case 211: return "AEMelee"; 
			case 212: return "FrenziedDevastation"; 
			case 213: return "PetMaxHP"; 
			case 214: return "MaxHPChange"; 
			case 215: return "PetAvoidance"; 
			case 216: return "Accuracy"; 
			case 217: return "HeadShot"; 
			case 218: return "PetCriticalHit"; 
			case 219: return "SlayUndead"; 
			case 220: return "SkillDamageAmount"; 
			case 221: return "Packrat"; 
			case 222: return "BlockBehind"; 
			case 223: return "DoubleRiposte"; 
			case 224: return "GiveDoubleRiposte"; 
			case 225: return "GiveDoubleAttack"; 
			case 226: return "TwoHandBash"; 
			case 227: return "ReduceSkillTimer"; 
			case 228: return "ReduceFallDamage"; 
			case 229: return "PersistantCasting"; 
			case 230: return "ExtendedShielding"; 
			case 231: return "StunBashChance"; 
			case 232: return "DivineSave"; 
			case 233: return "Metabolism"; 
			case 234: return "ReduceApplyPoisonTime"; 
			case 235: return "ChannelChanceSpells"; 
			case 236: return "FreePet"; 
			case 237: return "GivePetGroupTarget"; 
			case 238: return "IllusionPersistence"; 
			case 239: return "FeignedCastOnChance"; 
			case 240: return "StringUnbreakable"; 
			case 241: return "ImprovedReclaimEnergy"; 
			case 242: return "IncreaseChanceMemwipe"; 
			case 243: return "CharmBreakChance"; 
			case 244: return "RootBreakChance"; 
			case 245: return "TrapCircumvention"; 
			case 246: return "SetBreathLevel"; 
			case 247: return "RaiseSkillCap"; 
			case 248: return "SecondaryForte"; 
			case 249: return "SecondaryDmgInc"; 
			case 250: return "SpellProcChance"; 
			case 251: return "ConsumeProjectile"; 
			case 252: return "FrontalBackstabChance"; 
			case 253: return "FrontalBackstabMinDmg"; 
			case 254: return "Blank"; 
			case 255: return "ShieldDuration"; 
			case 256: return "ShroudofStealth"; 
			case 257: return "PetDiscipline"; 
			case 258: return "TripleBackstab"; 
			case 259: return "CombatStability"; 
			case 260: return "AddSingingMod"; 
			case 261: return "SongModCap"; 
			case 262: return "RaiseStatCap"; 
			case 263: return "TradeSkillMastery"; 
			case 264: return "HastenedAASkill"; 
			case 265: return "MasteryofPast"; 
			case 266: return "ExtraAttackChance"; 
			case 267: return "AddPetCommand"; 
			case 268: return "ReduceTradeskillFail"; 
			case 269: return "MaxBindWound"; 
			case 270: return "BardSongRange"; 
			case 271: return "BaseMovementSpeed"; 
			case 272: return "CastingLevel2"; 
			case 273: return "CriticalDoTChance"; 
			case 274: return "CriticalHealChance"; 
			case 275: return "CriticalMend"; 
			case 276: return "Ambidexterity"; 
			case 277: return "UnfailingDivinity"; 
			case 278: return "FinishingBlow"; 
			case 279: return "Flurry"; 
			case 280: return "PetFlurry"; 
			case 281: return "FeignedMinion"; 
			case 282: return "ImprovedBindWound"; 
			case 283: return "DoubleSpecialAttack"; 
			case 284: return "LoHSetHeal"; 
			case 285: return "NimbleEvasion"; 
			case 286: return "FcDamageAmt"; 
			case 287: return "SpellDurationIncByTic"; 
			case 288: return "SkillAttackProc"; 
			case 289: return "CastOnFadeEffect"; 
			case 290: return "IncreaseRunSpeedCap"; 
			case 291: return "Purify"; 
			case 292: return "StrikeThrough2"; 
			case 293: return "FrontalStunResist"; 
			case 294: return "CriticalSpellChance"; 
			case 295: return "ReduceTimerSpecial"; 
			case 296: return "FcSpellVulnerability"; 
			case 297: return "FcDamageAmtIncoming"; 
			case 298: return "ChangeHeight"; 
			case 299: return "WakeTheDead"; 
			case 300: return "Doppelganger"; 
			case 301: return "ArcheryDamageModifier"; 
			case 302: return "FcDamagePctCrit"; 
			case 303: return "FcDamageAmtCrit"; 
			case 304: return "OffhandRiposteFail"; 
			case 305: return "MitigateDamageShield"; 
			case 306: return "ArmyOfTheDead"; 
			case 307: return "Appraisal"; 
			case 308: return "SuspendMinion"; 
			case 309: return "GateCastersBindpoint"; 
			case 310: return "ReduceReuseTimer"; 
			case 311: return "LimitCombatSkills"; 
			case 312: return "Sanctuary"; 
			case 313: return "ForageAdditionalItems"; 
			case 314: return "Invisibility2"; 
			case 315: return "InvisVsUndead2"; 
			case 316: return "ImprovedInvisAnimals"; 
			case 317: return "ItemHPRegenCapIncrease"; 
			case 318: return "ItemManaRegenCapIncrease"; 
			case 319: return "CriticalHealOverTime"; 
			case 320: return "ShieldBlock"; 
			case 321: return "ReduceHate"; 
			case 322: return "GateToHomeCity"; 
			case 323: return "DefensiveProc"; 
			case 324: return "HPToMana"; 
			case 325: return "NoBreakAESneak"; 
			case 326: return "SpellSlotIncrease"; 
			case 327: return "MysticalAttune"; 
			case 328: return "DelayDeath"; 
			case 329: return "ManaAbsorbPercentDamage"; 
			case 330: return "CriticalDamageMob"; 
			case 331: return "Salvage"; 
			case 332: return "SummonToCorpse"; 
			case 333: return "CastOnRuneFadeEffect"; 
			case 334: return "BardAEDot"; 
			case 335: return "BlockNextSpellFocus"; 
			case 336: return "IllusionaryTarget"; 
			case 337: return "PercentXPIncrease"; 
			case 338: return "SummonAndResAllCorpses"; 
			case 339: return "TriggerOnCast"; 
			case 340: return "SpellTrigger"; 
			case 341: return "ItemAttackCapIncrease"; 
			case 342: return "ImmuneFleeing"; 
			case 343: return "InterruptCasting"; 
			case 344: return "ChannelChanceItems"; 
			case 345: return "AssassinateLevel"; 
			case 346: return "HeadShotLevel"; 
			case 347: return "DoubleRangedAttack"; 
			case 348: return "LimitManaMin"; 
			case 349: return "ShieldEquipDmgMod"; 
			case 350: return "ManaBurn"; 
			case 351: return "PersistentEffect"; 
			case 352: return "IncreaseTrapCount"; 
			case 353: return "AdditionalAura"; 
			case 354: return "DeactivateAllTraps"; 
			case 355: return "LearnTrap"; 
			case 356: return "ChangeTriggerType"; 
			case 357: return "FcMute"; 
			case 358: return "CurrentManaOnce"; 
			case 359: return "PassiveSenseTrap"; 
			case 360: return "ProcOnKillShot"; 
			case 361: return "SpellOnDeath"; 
			case 362: return "PotionBeltSlots"; 
			case 363: return "BandolierSlots"; 
			case 364: return "TripleAttackChance"; 
			case 365: return "ProcOnSpellKillShot"; 
			case 366: return "GroupShielding"; 
			case 367: return "SetBodyType"; 
			case 368: return "FactionMod"; 
			case 369: return "CorruptionCounter"; 
			case 370: return "ResistCorruption"; 
			case 371: return "AttackSpeed4"; 
			case 372: return "ForageSkill"; 
			case 373: return "CastOnFadeEffectAlways"; 
			case 374: return "ApplyEffect"; 
			case 375: return "DotCritDmgIncrease"; 
			case 376: return "Fling"; 
			case 377: return "CastOnFadeEffectNPC"; 
			case 378: return "SpellEffectResistChance"; 
			case 379: return "ShadowStepDirectional"; 
			case 380: return "Knockdown"; 
			case 381: return "KnockTowardCaster"; 
			case 382: return "NegateSpellEffect"; 
			case 383: return "SympatheticProc"; 
			case 384: return "Leap"; 
			case 385: return "LimitSpellGroup"; 
			case 386: return "CastOnCurer"; 
			case 387: return "CastOnCure"; 
			case 388: return "SummonCorpseZone"; 
			case 389: return "FcTimerRefresh"; 
			case 390: return "FcTimerLockout"; 
			case 391: return "LimitManaMax"; 
			case 392: return "FcHealAmt"; 
			case 393: return "FcHealPctIncoming"; 
			case 394: return "FcHealAmtIncoming"; 
			case 395: return "FcHealPctCritIncoming"; 
			case 396: return "FcHealAmtCrit"; 
			case 397: return "PetMeleeMitigation"; 
			case 398: return "SwarmPetDuration"; 
			case 399: return "FcTwincast"; 
			case 400: return "HealGroupFromMana"; 
			case 401: return "ManaDrainWithDmg"; 
			case 402: return "EndDrainWithDmg"; 
			case 403: return "LimitSpellClass"; 
			case 404: return "LimitSpellSubclass"; 
			case 405: return "TwoHandBluntBlock"; 
			case 406: return "CastonNumHitFade"; 
			case 407: return "CastonFocusEffect"; 
			case 408: return "LimitHPPercent"; 
			case 409: return "LimitManaPercent"; 
			case 410: return "LimitEndPercent"; 
			case 411: return "LimitClass"; 
			case 412: return "LimitRace"; 
			case 413: return "FcBaseEffects"; 
			case 414: return "LimitCastingSkill"; 
			case 415: return "FFItemClass"; 
			case 416: return "ACv2"; 
			case 417: return "ManaRegen_v2"; 
			case 418: return "SkillDamageAmount2"; 
			case 419: return "AddMeleeProc"; 
			case 420: return "FcLimitUse"; 
			case 421: return "FcIncreaseNumHits"; 
			case 422: return "LimitUseMin"; 
			case 423: return "LimitUseType"; 
			case 424: return "GravityEffect"; 
			case 425: return "Display"; 
			case 426: return "IncreaseExtTargetWindow"; 
			case 427: return "SkillProc"; 
			case 428: return "LimitToSkill"; 
			case 429: return "SkillProcSuccess"; 
			case 430: return "PostEffect"; 
			case 431: return "PostEffectData"; 
			case 432: return "ExpandMaxActiveTrophyBen"; 
			case 433: return "CriticalDotDecay"; 
			case 434: return "CriticalHealDecay"; 
			case 435: return "CriticalRegenDecay"; 
			case 436: return "BeneficialCountDownHold"; 
			case 437: return "TeleporttoAnchor"; 
			case 438: return "TranslocatetoAnchor"; 
			case 439: return "Assassinate"; 
			case 440: return "FinishingBlowLvl"; 
			case 441: return "DistanceRemoval"; 
			case 442: return "TriggerOnReqTarget"; 
			case 443: return "TriggerOnReqCaster"; 
			case 444: return "ImprovedTaunt"; 
			case 445: return "AddMercSlot"; 
			case 446: return "AStacker"; 
			case 447: return "BStacker"; 
			case 448: return "CStacker"; 
			case 449: return "DStacker"; 
			case 450: return "MitigateDotDamage"; 
			case 451: return "MeleeThresholdGuard"; 
			case 452: return "SpellThresholdGuard"; 
			case 453: return "TriggerMeleeThreshold"; 
			case 454: return "TriggerSpellThreshold"; 
			case 455: return "AddHatePct"; 
			case 456: return "AddHateOverTimePct"; 
			case 457: return "ResourceTap"; 
			case 458: return "FactionModPct"; 
			case 459: return "DamageModifier2"; 
			case 460: return "Ff_Override_NotFocusable"; 
			case 461: return "ImprovedDamage2"; 
			case 462: return "FcDamageAmt2"; 
			case 463: return "Shield_Target"; 
			case 464: return "PC_Pet_Rampage"; 
			case 465: return "PC_Pet_AE_Rampage"; 
			case 466: return "PC_Pet_Flurry_Chance"; 
			case 467: return "DS_Mitigation_Amount"; 
			case 468: return "DS_Mitigation_Percentage"; 
			case 469: return "Chance_Best_in_Spell_Grp"; 
			case 470: return "SE_Trigger_Best_in_Spell_Grp"; 
			case 471: return "Double_Melee_Round"; 
			default: return "";
		}
	}

	private static ItemMeta buildSkull(SkullMeta i, UUID skinuuid, String texturebase64, Object object) {
		// TODO Auto-generated method stub
		return null;
	}

	public static UUID getUUIDFromString(String s) {
		String md5 = getMD5(s);

		String uuid = md5.substring(0, 8) + "-" + md5.substring(8, 12) + "-" + md5.substring(12, 16) + "-"
				+ md5.substring(16, 20) + "-" + md5.substring(20);

		return UUID.fromString(uuid);
	}

	public static String getMD5(String input) {
		// TODO Auto-generated method stub
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] messageDigest = md.digest(input.getBytes());
			BigInteger number = new BigInteger(1, messageDigest);
			String hashtext = number.toString(16);
			while (hashtext.length() < 32) {
				hashtext = "0" + hashtext;
			}
			return hashtext;
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

}
