package com.solinia.solinia.Models;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.solinia.solinia.Adapters.ItemStackAdapter;
import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidItemSettingException;
import com.solinia.solinia.Interfaces.ISoliniaClass;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Interfaces.ISoliniaQuest;
import com.solinia.solinia.Interfaces.ISoliniaRace;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Managers.ConfigurationManager;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Utils.*;

import net.md_5.bungee.api.ChatColor;

public class SoliniaItem implements ISoliniaItem {

	private int id;
	private String displayname;
	private String basename;
	private int abilityid = 0;
	private String lore;
	private int strength = 0;
	private int stamina = 0;
	private int agility = 0;
	private int dexterity = 0;
	private boolean placeable = true;
	private int intelligence = 0;
	private int wisdom = 0;
	private int charisma = 0;
	private List<String> allowedClassNames = new ArrayList<String>();
	private List<String> allowedRaceNames = new ArrayList<String>();
	private String texturebase64;
	private boolean questitem = false;
	private int damage = 0;
	private int weaponabilityid = 0;
	private int attackspeed = 0;
	private String enchantment1;
	private int enchantment1val;
	private String enchantment2;
	private int enchantment2val;
	private String enchantment3;
	private int enchantment3val;
	private String enchantment4;
	private int enchantment4val;
	private int hpregen = 0;
	private int mpregen = 0;
	private int worth = 1;
	private boolean coreitem = false;
	private int fireResist = 0;
	private int coldResist = 0;
	private int magicResist = 0;
	private int poisonResist = 0;
	private int diseaseResist = 0;
	private boolean spellscroll = false;
	private short color;
	private int dye;
	private boolean isTemporary;
	private boolean isConsumable;
	private int baneUndead = 0;
	private boolean isAugmentation = false;
	private AugmentationSlotType augmentationFitsSlotType = AugmentationSlotType.NONE;
	private String discoverer = "";
	private int minLevel = 0;
	private int ac = 0;
	private int hp = 0;
	private int mana = 0;
	private boolean isExperienceBonus = false;
	private SkillType skillModType = SkillType.None;
	private int skillModValue = 0;
	private boolean reagent = false;
	private String languagePrimer = "";
	private int focusEffectId = 0;
	private int weaponDelay = 30;
	private int leatherRgbDecimal = -1;
	
	private boolean artifact = false;
	private boolean artifactFound = false;
	
	private SkillType skillModType2 = SkillType.None;
	private int skillModValue2 = 0;

	private SkillType skillModType3 = SkillType.None;
	private int skillModValue3 = 0;
	
	private SkillType skillModType4 = SkillType.None;
	private int skillModValue4 = 0;

	private String identifyMessage = "";
	private boolean bandage = false;
	private EquipmentSlot equipmentSlot = EquipmentSlot.None;
	
	private String bookAuthor = "";
	private List<String> bookPages = new ArrayList<String>();
	private boolean neverDrop = false;
	
	private Timestamp lastUpdatedTime;
	private ItemType itemType = ItemType.None;
	
	private int QuestId = 0;
	
	@Override
	public ItemStack asItemStack() {
		return ItemStackAdapter.Adapt(this, 1, false);
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String getDisplayname() {
		return displayname;
	}

	@Override
	public void setDisplayname(String displayname) {
		this.displayname = displayname;
	}

	@Override
	public int getAbilityid() {
		return abilityid;
	}

	@Override
	public void setAbilityid(int abilityid) {
		this.abilityid = abilityid;
	}

	@Override
	public String getBasename() {
		return basename;
	}

	@Override
	public void setBasename(String basename) {
		this.basename = basename;
	}

	@Override
	public String getLore() {
		return lore;
	}

	@Override
	public void setLore(String lore) {
		this.lore = lore;
	}

	@Override
	public int getStrength() {
		return strength;
	}

	@Override
	public void setStrength(int strength) {
		this.strength = strength;
	}

	@Override
	public int getStamina() {
		return stamina;
	}

	@Override
	public void setStamina(int stamina) {
		this.stamina = stamina;
	}

	@Override
	public int getAgility() {
		return agility;
	}

	@Override
	public void setAgility(int agility) {
		this.agility = agility;
	}

	@Override
	public int getDexterity() {
		return dexterity;
	}

	@Override
	public void setDexterity(int dexterity) {
		this.dexterity = dexterity;
	}

	@Override
	public int getIntelligence() {
		return intelligence;
	}

	@Override
	public void setIntelligence(int intelligence) {
		this.intelligence = intelligence;
	}

	@Override
	public int getWisdom() {
		return wisdom;
	}

	@Override
	public void setWisdom(int wisdom) {
		this.wisdom = wisdom;
	}

	@Override
	public int getCharisma() {
		return charisma;
	}

	@Override
	public void setCharisma(int charisma) {
		this.charisma = charisma;
	}

	@Override
	public List<String> getAllowedClassNames() {
		return allowedClassNames;
	}

	@Override
	public void setAllowedClassNames(List<String> allowedClassesNames) {
		this.allowedClassNames = allowedClassesNames;
	}

	@Override
	public List<String> getAllowedRaceNames() {
		return allowedRaceNames;
	}

	@Override
	public void setAllowedRaceNames(List<String> allowedRaceNames) {
		this.allowedRaceNames = allowedRaceNames;
	}
	
	@Override
	public String getTexturebase64() {
		return texturebase64;
	}

	@Override
	public void setTexturebase64(String texturebase64) {
		this.texturebase64 = texturebase64;
	}

	@Override
	public int getDamage() {
		return damage;
	}

	@Override
	public void setDamage(int damage) {
		this.damage = damage;
	}

	@Override
	public int getWeaponabilityid() {
		return weaponabilityid;
	}

	@Override
	public void setWeaponabilityid(int weaponabilityid) {
		this.weaponabilityid = weaponabilityid;
	}

	@Override
	public int getAttackspeed() {
		return attackspeed;
	}

	@Override
	public void setAttackspeed(int attackspeed) {
		this.attackspeed = attackspeed;
	}

	@Override
	public String getEnchantment1() {
		return enchantment1;
	}

	@Override
	public void setEnchantment1(String enchantment1) {
		this.enchantment1 = enchantment1;
	}

	@Override
	public int getEnchantment1val() {
		return enchantment1val;
	}

	@Override
	public void setEnchantment1val(int enchantment1val) {
		this.enchantment1val = enchantment1val;
	}

	@Override
	public String getEnchantment2() {
		return enchantment2;
	}

	@Override
	public void setEnchantment2(String enchantment2) {
		this.enchantment2 = enchantment2;
	}

	@Override
	public int getEnchantment2val() {
		return enchantment2val;
	}

	@Override
	public void setEnchantment2val(int enchantment2val) {
		this.enchantment2val = enchantment2val;
	}

	@Override
	public String getEnchantment3() {
		return enchantment3;
	}

	@Override
	public void setEnchantment3(String enchantment3) {
		this.enchantment3 = enchantment3;
	}

	@Override
	public int getEnchantment3val() {
		return enchantment3val;
	}

	@Override
	public void setEnchantment3val(int enchantment3val) {
		this.enchantment3val = enchantment3val;
	}

	@Override
	public String getEnchantment4() {
		return enchantment4;
	}

	@Override
	public void setEnchantment4(String enchantment4) {
		this.enchantment4 = enchantment4;
	}

	@Override
	public int getEnchantment4val() {
		return enchantment4val;
	}

	@Override
	public void setEnchantment4val(int enchantment4val) {
		this.enchantment4val = enchantment4val;
	}

	@Override
	public int getHpregen() {
		return hpregen;
	}

	@Override
	public void setHpregen(int hpregen) {
		this.hpregen = hpregen;
	}

	@Override
	public int getMpregen() {
		return mpregen;
	}

	@Override
	public void setMpregen(int mpregen) {
		this.mpregen = mpregen;
	}

	@Override
	public boolean isCoreitem() {
		return coreitem;
	}

	@Override
	public void setCoreitem(boolean coreitem) {
		this.coreitem = coreitem;
	}

	@Override
	public int getFireResist() {
		return fireResist;
	}

	@Override
	public void setFireResist(int fireResist) {
		this.fireResist = fireResist;
	}

	@Override
	public int getColdResist() {
		return coldResist;
	}

	@Override
	public void setColdResist(int coldResist) {
		this.coldResist = coldResist;
	}

	@Override
	public int getMagicResist() {
		return magicResist;
	}

	@Override
	public void setMagicResist(int magicResist) {
		this.magicResist = magicResist;
	}

	@Override
	public long getWorth() {
		return worth;
	}

	@Override
	public void setWorth(int worth) {
		this.worth = worth;
	}

	@Override
	public int getPoisonResist() {
		return poisonResist;
	}

	@Override
	public void setPoisonResist(int poisonResist) {
		this.poisonResist = poisonResist;
	}

	@Override
	public boolean isSpellscroll() {
		return spellscroll;
	}

	@Override
	public void setSpellscroll(boolean spellscroll) {
		this.spellscroll = spellscroll;
	}

	@Override
	public boolean useItemOnEntity(Player player, LivingEntity targetentity, boolean isConsumable)
			throws CoreStateInitException {
		
		if (targetentity.isDead() || player.isDead())
			return false;
		
		double distanceOverLimit = Utils.DistanceOverAggroLimit((LivingEntity) player,
				targetentity);

		if (distanceOverLimit > 0)
		{
			player.sendMessage("You were too far to use this item on that entity");
			return false;
		}
		
		if (isThrowing())
		{
			if (targetentity instanceof Player)
			{
				ISoliniaPlayer solsourceplayer = SoliniaPlayerAdapter.Adapt(player);
				if (solsourceplayer.getGroup() != null)
				{
					if (solsourceplayer.getGroup().getMembers().contains(targetentity.getUniqueId()))
					{
						return false;
					}
				}
			}
			
			player.sendMessage("You throw a " + getDisplayname() + " for [" + getDamage() + "] damage");
			targetentity.damage(getDamage());
			return true;
		}
		
		if (isConsumable == true && isExperienceBonus())
		{
			SoliniaPlayerAdapter.Adapt(player).grantExperienceBonusFromItem();
			System.out.println("Granted " + player.getName() + " experience bonus from item [" + SoliniaPlayerAdapter.Adapt(player).getExperienceBonusExpires().toString() + "]");
			return true;
		}
		
		if (isConsumable == true && !getLanguagePrimer().equals(""))
		{
			SoliniaPlayerAdapter.Adapt(player).setSkill(getLanguagePrimer(), 100);
			System.out.println("Granted " + player.getName() + " language skill from item [" + getLanguagePrimer() + "]");
			return true;
		}
		
		if (getAllowedClassNames() != null && getAllowedClassNames().size() > 0)
			if (!getAllowedClassNames().contains(SoliniaPlayerAdapter.Adapt(player).getClassObj().getName().toUpperCase())) {
				player.sendMessage(ChatColor.GRAY + "Your class cannot consume this item");
				return true;
			}
		
		if (getAllowedRaceNames() != null && getAllowedRaceNames().size() > 0)
			if (!getAllowedRaceNames().contains(SoliniaPlayerAdapter.Adapt(player).getRace().getName().toUpperCase())) {
				player.sendMessage(ChatColor.GRAY + "Your race cannot consume this item");
				return true;
			}
		
		if (getMinLevel() > SoliniaPlayerAdapter.Adapt(player).getLevel())
		{
			player.sendMessage("This item requires minimum level: " + getMinLevel());
			return false;
		}
		
		ISoliniaSpell spell = StateManager.getInstance().getConfigurationManager().getSpell(getAbilityid());
		if (spell == null) {
			return false;
		}

		return spell.tryCast(player,targetentity,!isConsumable,!isConsumable);
	}

	
	/*
	 * Nothing is calling this so lets remove it
	 * 
	@Override
	public boolean useItemOnBlock(Player player, ISoliniaItem solitem, Block clickedBlock, boolean isConsumable) throws CoreStateInitException {
		ISoliniaSpell spell = StateManager.getInstance().getConfigurationManager().getSpell(solitem.getAbilityid());
		if (spell == null) {
			return false;
		}
		
		ISoliniaLivingEntity solentity = SoliniaLivingEntityAdapter.Adapt((LivingEntity)player);
		
		if (solentity == null)
			return false;


		if (!isConsumable)
		if (spell.getActSpellCost(solentity) > SoliniaPlayerAdapter.Adapt(player).getMana()) {
			player.sendMessage(ChatColor.GRAY + "Insufficient Mana [E]");
			return false;
		}
		
		if (!spell.isBardSong())
		{
			if (spell.getComponents1() > 0)
			{
				ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(spell.getComponents1());
				if (item == null || !item.isReagent())
				{
					player.sendMessage(ChatColor.RED + "ERROR: " + ChatColor.YELLOW + "ERROR-ALERT-ADMIN-SPELL"+ spell.getId() + "-ID" + spell.getComponents1());
					return false;
				}
				if(!solPlayer.hasSufficientReagents(spell.getComponents1(),spell.getComponentCounts1()))
				{
					player.sendMessage(ChatColor.GRAY + "Insufficient Reagents (Check spell and see /reagents)");
					return false;
				}
			}
			
			if (spell.getComponents2() > 0)
			{
				ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(spell.getComponents2());
				if (item == null || !item.isReagent())
				{
					player.sendMessage(ChatColor.RED + "ERROR: " + ChatColor.YELLOW + "ERROR-ALERT-ADMIN-SPELL"+ spell.getId() + "-ID" + spell.getComponents2());
					return false;
				}
				if(!solPlayer.hasSufficientReagents(spell.getComponents2(),spell.getComponentCounts2()))
				{
					player.sendMessage(ChatColor.GRAY + "Insufficient Reagents (Check spell and see /reagents)");
					return false;
				}
			}
			
			if (spell.getComponents3() > 0)
			{
				ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(spell.getComponents3());
				if (item == null || !item.isReagent())
				{
					player.sendMessage(ChatColor.RED + "ERROR: " + ChatColor.YELLOW + "ERROR-ALERT-ADMIN-SPELL"+ spell.getId() + "-ID" + spell.getComponents3());
					return false;
				}
				if(!solPlayer.hasSufficientReagents(spell.getComponents3(),spell.getComponentCounts3()))
				{
					player.sendMessage(ChatColor.GRAY + "Insufficient Reagents (Check spell and see /reagents)");
					return false;
				}
			}
			
			if (spell.getComponents4() > 0)
			{
				ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(spell.getComponents4());
				if (item == null || !item.isReagent())
				{
					player.sendMessage(ChatColor.RED + "ERROR: " + ChatColor.YELLOW + "ERROR-ALERT-ADMIN-SPELL"+ spell.getId() + "-ID" + spell.getComponents4());
					return false;
				}
				if(!solPlayer.hasSufficientReagents(spell.getComponents4(),spell.getComponentCounts4()))
				{
					player.sendMessage(ChatColor.GRAY + "Insufficient Reagents (Check spell and see /reagents)");
					return false;
				}
			}
		}

		boolean itemUseSuccess = false;

		itemUseSuccess = spell.tryApplyOnBlock(player, clickedBlock, true);

		if (itemUseSuccess) {
			SoliniaPlayerAdapter.Adapt(player).reducePlayerMana(spell.getActSpellCost(solentity));
		
			if (!spell.isBardSong())
			{
				if (spell.getComponents1() > 0)
				{
					SoliniaPlayerAdapter.Adapt(player).reduceReagents(spell.getComponents1(),spell.getComponentCounts1());
				}
				if (spell.getComponents2() > 0)
				{
					SoliniaPlayerAdapter.Adapt(player).reduceReagents(spell.getComponents2(),spell.getComponentCounts2());
				}
				if (spell.getComponents3() > 0)
				{
					SoliniaPlayerAdapter.Adapt(player).reduceReagents(spell.getComponents3(),spell.getComponentCounts3());
				}
				if (spell.getComponents4() > 0)
				{
					SoliniaPlayerAdapter.Adapt(player).reduceReagents(spell.getComponents4(),spell.getComponentCounts4());
				}
			}
			
		}

		return itemUseSuccess;
	}
	*/
	@Override
	public String asJsonString() {
		String out = ItemStackUtils.ConvertItemStackToJsonRegular(asItemStack());
		return out;
	}

	@Override
	public String asJsonStringEscaped() {
		String out = asJsonString();
		return out.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "\\r").replace("\n", "\\n");
	}
	
	@Override
	public void sendItemSettingsToSender(CommandSender sender) throws CoreStateInitException {
		sender.sendMessage(ChatColor.RED + "Item Settings for " + ChatColor.GOLD + getDisplayname() + ChatColor.RESET);
		sender.sendMessage("----------------------------");
		sender.sendMessage("- id: " + ChatColor.GOLD + getId() + ChatColor.RESET + " basename: " + ChatColor.GOLD + getBasename() + ChatColor.RESET + " - minlevel: " + ChatColor.GOLD + getMinLevel() + ChatColor.RESET);
		sender.sendMessage("- displayname: " + ChatColor.GOLD + getDisplayname() + ChatColor.RESET);
		sender.sendMessage("- lastupdated: " + ChatColor.GOLD + this.getLastUpdatedTimeAsString() + ChatColor.RESET);
		sender.sendMessage("- worth: " + ChatColor.GOLD + getWorth() + ChatColor.RESET + " placeable: " + ChatColor.GOLD + isPlaceable() + ChatColor.RESET);
		sender.sendMessage("- color (blocktype): " + ChatColor.GOLD + getColor() + ChatColor.RESET + " dye (armour color): " + ChatColor.GOLD + getDye() + ChatColor.RESET);
		String leathercolor = "NONE";
		if (getLeatherRgbDecimal() > 0)
		{
			Color colorTmp = Color.fromRGB(getLeatherRgbDecimal());
			leathercolor = ColorUtil.fromRGB(colorTmp.getRed(),colorTmp.getGreen(), colorTmp.getBlue()) + "(Closest)" + ChatColor.RESET;
		}
		sender.sendMessage("- leatherrgbdecimal: " + ChatColor.GOLD + getLeatherRgbDecimal() + ChatColor.RESET + leathercolor + " See: https://bit.ly/2i02I8k");
		sender.sendMessage("- reagent: " + ChatColor.GOLD + isReagent() + ChatColor.RESET);
		sender.sendMessage("- temporary: " + ChatColor.GOLD + isTemporary() + ChatColor.RESET + " - consumable: " + ChatColor.GOLD + isConsumable() + ChatColor.RESET);
		sender.sendMessage("- bandage: " + ChatColor.GOLD + isBandage() + ChatColor.RESET + " languageprimer: " + ChatColor.GOLD + getLanguagePrimer() + ChatColor.RESET);
		sender.sendMessage("- augmentation: " + ChatColor.GOLD + isAugmentation() + ChatColor.RESET);
		sender.sendMessage("- discoverer: " + ChatColor.GOLD + getDiscoverer() + ChatColor.RESET + " - artifact: " + ChatColor.GOLD + isArtifact() + ChatColor.RESET + " Found: (" + isArtifactFound() + ")"+ ChatColor.RESET);
		sender.sendMessage("----------------------------");
		sender.sendMessage("- acceptsaugmentationslottype: " + ChatColor.GOLD + getAcceptsAugmentationSlotType() + ChatColor.RESET);
		sender.sendMessage("- augmentationfitsslottype: " + ChatColor.GOLD + this.getAugmentationFitsSlotType().name() + ChatColor.RESET);
		sender.sendMessage("----------------------------");
		sender.sendMessage("- hpregen: " + ChatColor.GOLD + getHpregen() + ChatColor.RESET + " mpregen: " + ChatColor.GOLD + getMpregen() + ChatColor.RESET);
		sender.sendMessage("- ac: " + ChatColor.GOLD + getAC() + ChatColor.RESET + "hp: " + ChatColor.GOLD + getHp() + ChatColor.RESET + " mana: " + ChatColor.GOLD + getMana() + ChatColor.RESET);
		sender.sendMessage("- damage: " + ChatColor.GOLD + getDamage() + ChatColor.RESET + " weapondelay: " + ChatColor.GOLD + getWeaponDelay() + ChatColor.RESET + " baneundead: " + ChatColor.GOLD + getBaneUndead() + ChatColor.RESET);
		sender.sendMessage("- abilityid: " + ChatColor.GOLD + getAbilityid() + ChatColor.RESET + " - weaponabilityid: " + ChatColor.GOLD + getWeaponabilityid() + ChatColor.RESET + " focuseffectid: " + ChatColor.GOLD + getFocusEffectId() + ChatColor.RESET);
		sender.sendMessage("- attackspeedpct: " + ChatColor.GOLD + getAttackspeed() + "%" + ChatColor.RESET);
		sender.sendMessage("- strength: " + ChatColor.GOLD + getStrength() + ChatColor.RESET +
		" - stamina: " + ChatColor.GOLD + getStamina() + ChatColor.RESET + 
		" - agility: " + ChatColor.GOLD + getAgility() + ChatColor.RESET + 
		" - dexterity: " + ChatColor.GOLD + getDexterity() + ChatColor.RESET);
		sender.sendMessage("- intelligence: " + ChatColor.GOLD + getIntelligence() + ChatColor.RESET +
		" - wisdom: " + ChatColor.GOLD + getWisdom() + ChatColor.RESET + 
		" - charisma: " + ChatColor.GOLD + getCharisma() + ChatColor.RESET);
		sender.sendMessage("- MR: " + ChatColor.GOLD + getMagicResist() + ChatColor.RESET + " CR: " + ChatColor.GOLD + getColdResist() + ChatColor.RESET + " FR: " + ChatColor.GOLD + getFireResist() + ChatColor.RESET + " DR: " + ChatColor.GOLD + getDiseaseResist() + ChatColor.RESET + " PR: " + ChatColor.GOLD + getPoisonResist() + ChatColor.RESET);
		sender.sendMessage("----------------------------");
		sender.sendMessage("- skillmodtype: " + ChatColor.GOLD + getSkillModType().toString() + ChatColor.RESET + " skillmodvalue: " + ChatColor.GOLD + getSkillModValue() + ChatColor.RESET);
		sender.sendMessage("- skillmodtype2: " + ChatColor.GOLD + getSkillModType2().toString() + ChatColor.RESET + " skillmodvalue2: " + ChatColor.GOLD + getSkillModValue2() + ChatColor.RESET);
		sender.sendMessage("- skillmodtype3: " + ChatColor.GOLD + getSkillModType3().toString() + ChatColor.RESET + " skillmodvalue3: " + ChatColor.GOLD + getSkillModValue3() + ChatColor.RESET);
		sender.sendMessage("- skillmodtype4: " + ChatColor.GOLD + getSkillModType4().toString() + ChatColor.RESET + " skillmodvalue4: " + ChatColor.GOLD + getSkillModValue4() + ChatColor.RESET);
		sender.sendMessage("----------------------------");
		sender.sendMessage("- equipmentslot: " + ChatColor.GOLD + getEquipmentSlot().name() + ChatColor.RESET);
		sender.sendMessage("- itemtype: " + ChatColor.GOLD + getItemType().name() + ChatColor.RESET);
		sender.sendMessage("----------------------------");
		sender.sendMessage("- identifymessage: " + ChatColor.GOLD + getIdentifyMessage() + ChatColor.RESET);
		sender.sendMessage("- bookauthor: " + ChatColor.GOLD + getBookAuthor() + ChatColor.RESET);
		String allowedClassNames = "";
		for(String classname : this.getAllowedClassNames())
		{
			allowedClassNames += classname + ",";
		}
		sender.sendMessage("- allowedclassnames: " + allowedClassNames.trim());

		String allowedRaceNames = "";
		for(String racename : this.getAllowedRaceNames())
		{
			allowedRaceNames += racename + ",";
		}
		sender.sendMessage("- allowedracenames: " + allowedRaceNames.trim());

	}

	private String getLastUpdatedTimeAsString() {
		if (this.lastUpdatedTime == null)
			setLastUpdatedTimeNow();
		
		String lastItemTimestampAsString= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS").format(this.getLastUpdatedTime());
		return lastItemTimestampAsString;
	    
	}

	@Override
	public void editSetting(String setting, String value)
			throws InvalidItemSettingException, NumberFormatException, CoreStateInitException {

		StateManager.getInstance().getConfigurationManager().setItemsChanged(true);
		
		switch (setting.toLowerCase()) {
		case "displayname":
			if (value.equals(""))
				throw new InvalidItemSettingException("Name is empty");

			if (value.length() > 36)
				throw new InvalidItemSettingException("Name is longer than 36 characters");
			setDisplayname(value);
			break;
		case "bookauthor":
			setBookAuthor(value);
			break;
		case "attackspeedpct":
			setAttackspeed(Integer.parseInt(value));
			break;
		case "worth":
			setWorth(Integer.parseInt(value));
			break;
		case "dye":
			setDye(Integer.parseInt(value));
			break;
		case "allowedclassnames":
			String[] allowedclasses = value.split(",");
			
			for (String classname : allowedclasses)
			{
				boolean foundClass = false;
				for (ISoliniaClass solClass : StateManager.getInstance().getConfigurationManager().getClasses())
				{
					if (solClass.getName().toUpperCase().equals(classname.toUpperCase()))
						foundClass = true;
				}
				
				if (foundClass == false)
				{
					throw new InvalidItemSettingException("Invalid class in allowedclasses array [" + classname + "]");
				}
			}
			
			setAllowedClassNames(Arrays.asList(allowedclasses));
			break;
		case "allowedracenames":
			String[] allowedraces = value.split(",");
			
			for (String racename : allowedraces)
			{
				boolean foundRace = false;
				for (ISoliniaRace solRace : StateManager.getInstance().getConfigurationManager().getRaces())
				{
					if (solRace.getName().toUpperCase().equals(racename.toUpperCase()))
						foundRace = true;
				}
				
				if (foundRace == false)
				{
					throw new InvalidItemSettingException("Invalid race in allowedclasses array [" + racename + "]");
				}
			}
			
			setAllowedRaceNames(Arrays.asList(allowedraces));
			break;
		case "color":
			setColor(Short.parseShort(value));
			break;
		case "bandage":
			setBandage(Boolean.parseBoolean(value));
			break;
		case "damage":
			setDamage(Integer.parseInt(value));
			break;
		case "leatherrgbdecimal":
			setLeatherRgbDecimal(Integer.parseInt(value));
			break;
		case "spellscroll":
			this.setSpellscroll(Boolean.parseBoolean(value));
			break;
		case "placeable":
			this.setPlaceable(Boolean.parseBoolean(value));
			break;
		case "artifact":
			setArtifact(Boolean.parseBoolean(value));
			break;
		case "baneundead":
			setBaneUndead(Integer.parseInt(value));
			break;
		case "hpregen":
			setHpregen(Integer.parseInt(value));
			break;
		case "mpregen":
			setMpregen(Integer.parseInt(value));
			break;
		case "strength":
			setStrength(Integer.parseInt(value));
			break;
		case "stamina":
			setStamina(Integer.parseInt(value));
			break;
		case "agility":
			setAgility(Integer.parseInt(value));
			break;
		case "dexterity":
			setDexterity(Integer.parseInt(value));
			break;
		case "intelligence":
			setIntelligence(Integer.parseInt(value));
			break;
		case "wisdom":
			setWisdom(Integer.parseInt(value));
			break;
		case "questid":
			ISoliniaQuest quest = StateManager.getInstance().getConfigurationManager().getQuest(Integer.parseInt(value));
			setQuestId(quest.getId());
			break;
		case "basename":
			Material material = Material.valueOf(value.toUpperCase());
			setBasename(material.name());
			break;
		case "charisma":
			setCharisma(Integer.parseInt(value));
			break;
		case "magicresist":
			setMagicResist(Integer.parseInt(value));
			break;
		case "coldresist":
			setColdResist(Integer.parseInt(value));
			break;
		case "fireresist":
			setFireResist(Integer.parseInt(value));
			break;
		case "diseaseresist":
			setDiseaseResist(Integer.parseInt(value));
			break;
		case "poisonresist":
			setPoisonResist(Integer.parseInt(value));
			break;
		case "temporary":
			setTemporary(Boolean.parseBoolean(value));
			break;
		case "abilityid":
			setAbilityid(Integer.parseInt(value));
			break;
		case "focuseffectid":
			setFocusEffectId(Integer.parseInt(value));
			break;
		case "weaponabilityid":
			setWeaponabilityid(Integer.parseInt(value));
			break;
		case "consumable":
			setConsumable(Boolean.parseBoolean(value));
			break;
		case "augmentation":
			setAugmentation(Boolean.parseBoolean(value));
			break;
		case "clearallowedclasses":
			setAllowedClassNames(new ArrayList<String>());
			break;
		case "clearallowedraces":
			setAllowedRaceNames(new ArrayList<String>());
			break;
		case "cleardiscoverer":
			setDiscoverer("");
			break;
		case "minlevel":
			setMinLevel(Integer.parseInt(value));
			break;
		case "augmentationfitsslottype":
			setAugmentationFitsSlotType(AugmentationSlotType.valueOf(value));
			break;
		case "ac":
			setAC(Integer.parseInt(value));
			break;
		case "hp":
			setHp(Integer.parseInt(value));
			break;
		case "mana":
			setMana(Integer.parseInt(value));
			break;
		case "experiencebonus":
			setExperienceBonus(Boolean.parseBoolean(value));
			break;
		case "skillmodtype":
			setSkillModType(SkillType.valueOf(TextUtils.CapitaliseFirstLetter(value)));
			break;
		case "skillmodvalue":
			setSkillModValue(Integer.parseInt(value));
			break;
		case "skillmodtype2":
			setSkillModType2(SkillType.valueOf(TextUtils.CapitaliseFirstLetter(value)));
			break;
		case "skillmodvalue2":
			setSkillModValue2(Integer.parseInt(value));
			break;
		case "skillmodtype3":
			setSkillModType3(SkillType.valueOf(TextUtils.CapitaliseFirstLetter(value)));
			break;
		case "skillmodvalue3":
			setSkillModValue3(Integer.parseInt(value));
			break;
		case "skillmodtype4":
			setSkillModType4(SkillType.valueOf(TextUtils.CapitaliseFirstLetter(value)));
			break;
		case "skillmodvalue4":
			setSkillModValue4(Integer.parseInt(value));
			break;
		case "weapondelay":
			setWeaponDelay(Integer.parseInt(value));
			break;
		case "equipmentslot":
			setEquipmentSlot(EquipmentSlot.valueOf(TextUtils.CapitaliseFirstLetter(value)));
			break;
		case "itemtype":
			setItemType(ItemType.valueOf(TextUtils.CapitaliseFirstLetter(value)));
			break;
		case "reagent":
			setReagent(Boolean.parseBoolean(value));
			break;
		case "identifymessage":
			setIdentifyMessage(value);
			break;
		case "languageprimer":
			boolean foundRace = false;
			for (ISoliniaRace solRace : StateManager.getInstance().getConfigurationManager().getRaces())
			{
				if (solRace.getName().toUpperCase().equals(value.toUpperCase()))
					foundRace = true;
			}
			
			if (foundRace == false)
			{
				throw new InvalidItemSettingException("Invalid language");
			}
			
			setLanguagePrimer(value.toUpperCase());
			break;
		case "clearlanguageprimer":
			setLanguagePrimer("");
			break;
		default:
			throw new InvalidItemSettingException("Invalid Item setting. Valid Options are: displayname,worth,color,damage,hpregen,mpregen,strength,stamina,agility,dexterity,intelligence,wisdom,charisma,abilityid,consumable,crafting,quest,augmentation,cleardiscoverer,clearallowedclasses,clearallowedraces,ac,hp,mana,experiencebonus,skillmodtype,skillmodvalue,skillmodtype2,skillmodvalue2,skillmodtype3,skillmodvalue3,skillmodtype4,skillmodvalue4,artifact,spellscroll,territoryflag,reagent,allowedclassnames,allowedracenames,identifymessage,languageprimer,clearlanguageprimer");
		}
		
		this.setLastUpdatedTimeNow();
		StateManager.getInstance().getConfigurationManager().setItemsChanged(true);
	}

	@Override
	public short getColor() {
		return color;
	}

	@Override
	public void setColor(short color) {
		this.color = color;
	}

	@Override
	public void consume(Plugin plugin, Player player) throws CoreStateInitException {
		if (this.getAbilityid() < 1)
			return;
		
		useItemOnEntity(player,player,true);
	}

	@Override
	public int getDiseaseResist() {
		return diseaseResist;
	}

	@Override
	public void setDiseaseResist(int diseaseResist) {
		this.diseaseResist = diseaseResist;
	}

	@Override
	public boolean isTemporary() {
		return isTemporary;
	}

	@Override
	public void setTemporary(boolean isTemporary) {
		this.isTemporary = isTemporary;
	}

	@Override
	public boolean isConsumable() {
		return isConsumable;
	}

	@Override
	public void setConsumable(boolean isConsumable) {
		this.isConsumable = isConsumable;
	}

	@Override
	public int getBaneUndead() {
		return baneUndead;
	}

	@Override
	public void setBaneUndead(int baneUndead) {
		this.baneUndead = baneUndead;
	}

	@Override
	public boolean isAugmentation() {
		return isAugmentation;
	}

	@Override
	public void setAugmentation(boolean isAugmentation) {
		this.isAugmentation = isAugmentation;
	}

	@Override
	public boolean isCrafting() {
		try {
			return StateManager.getInstance().getConfigurationManager().isCraftsHasComponent(this.getId());
		} catch (CoreStateInitException e) {
			return false;
		}
	}
	
	@Override
	public AugmentationSlotType getAcceptsAugmentationSlotType() {
		return Utils.getItemStackAugSlotType(getBasename(), isAugmentation);
	}

	@Override
	public AugmentationSlotType getAugmentationFitsSlotType() {
		return this.augmentationFitsSlotType;
	}

	@Override
	public void setAugmentationFitsSlotType(AugmentationSlotType augmentationFitsSlotType) {
		this.augmentationFitsSlotType = augmentationFitsSlotType;
	}

	@Override
	public String getDiscoverer() {
		return discoverer;
	}

	@Override
	public void setDiscoverer(String discoverer) {
		this.discoverer = discoverer;
	}

	@Override
	public int getMinLevel() {
		return minLevel;
	}

	@Override
	public void setMinLevel(int minLevel) {
		this.minLevel = minLevel;
	}

	@Override
	public int getAC() {
		return ac;
	}

	@Override
	public void setAC(int ac) {
		this.ac = ac;
	}

	@Override
	public int getHp() {
		return hp;
	}

	@Override
	public void setHp(int hp) {
		this.hp = hp;
	}

	@Override
	public int getMana() {
		return mana;
	}

	@Override
	public void setMana(int mana) {
		this.mana = mana;
	}

	@Override
	public boolean isExperienceBonus() {
		return isExperienceBonus;
	}

	@Override
	public void setExperienceBonus(boolean isExperienceBonus) {
		this.isExperienceBonus = isExperienceBonus;
	}

	@Override
	public SkillType getSkillModType() {
		return skillModType;
	}

	@Override
	public void setSkillModType(SkillType skillModType) {
		this.skillModType = skillModType;
	}

	@Override
	public int getSkillModValue() {
		return skillModValue;
	}

	@Override
	public void setSkillModValue(int skillModValue) {
		this.skillModValue = skillModValue;
	}

	@Override
	public SkillType getSkillModType2() {
		return skillModType2;
	}

	@Override
	public void setSkillModType2(SkillType skillModType2) {
		this.skillModType2 = skillModType2;
	}

	@Override
	public int getSkillModValue2() {
		return skillModValue2;
	}

	@Override
	public void setSkillModValue2(int skillModValue2) {
		this.skillModValue2 = skillModValue2;
	}

	@Override
	public SkillType getSkillModType3() {
		return skillModType3;
	}

	@Override
	public void setSkillModType3(SkillType skillModType3) {
		this.skillModType3 = skillModType3;
	}

	@Override
	public int getSkillModValue3() {
		return skillModValue3;
	}

	@Override
	public void setSkillModValue3(int skillModValue3) {
		this.skillModValue3 = skillModValue3;
	}

	@Override
	public SkillType getSkillModType4() {
		return skillModType4;
	}

	@Override
	public void setSkillModType4(SkillType skillModType4) {
		this.skillModType4 = skillModType4;
	}

	@Override
	public int getSkillModValue4() {
		return skillModValue4;
	}

	public void setSkillModValue4(int skillModValue4) {
		this.skillModValue4 = skillModValue4;
	}

	@Override
	public boolean isArtifact() {
		return artifact;
	}

	@Override
	public void setArtifact(boolean artifact) {
		this.artifact = artifact;
	}

	@Override
	public boolean isArtifactFound() {
		return artifactFound;
	}

	@Override
	public void setArtifactFound(boolean artifactFound) {
		this.artifactFound = artifactFound;
	}

	@Override
	public int getDye() {
		return dye;
	}

	@Override
	public void setDye(int dye) {
		this.dye = dye;
	}

	@Override
	public ItemStack asItemStackForMerchant(long costmultiplier) {
		return ItemStackAdapter.Adapt(this, costmultiplier, true);
	}

	@Override
	public boolean isReagent() {
		return reagent;
	}

	@Override
	public void setReagent(boolean reagent) {
		this.reagent = reagent;
	}

	@Override
	public boolean isWeaponOrBowOrShield() {
		return ConfigurationManager.HandMaterials.contains(this.getBasename().toUpperCase());
	}

	@Override
	public boolean isArmour() {
		return ConfigurationManager.ArmourMaterials.contains(this.getBasename().toUpperCase());
	}

	@Override
	public boolean isJewelry() {
		return (this.getEquipmentSlot().equals(EquipmentSlot.Fingers) || this.getEquipmentSlot().equals(EquipmentSlot.Neck) || this.getEquipmentSlot().equals(EquipmentSlot.Shoulders) || this.getEquipmentSlot().equals(EquipmentSlot.Ears));
	}

	
	@Override
	public boolean isThrowing() {
		return this.getItemType().equals(ItemType.ThrowingWeapon);
	}

	@Override
	public String getIdentifyMessage() {
		return identifyMessage;
	}

	@Override
	public void setIdentifyMessage(String identifyMessage) {
		this.identifyMessage = identifyMessage;
	}

	@Override
	public boolean isBandage() {
		return bandage;
	}

	@Override
	public void setBandage(boolean bandage) {
		this.bandage = bandage;
	}

	@Override
	public String getLanguagePrimer() {
		return languagePrimer;
	}

	@Override
	public void setLanguagePrimer(String languagePrimer) {
		this.languagePrimer = languagePrimer;
	}

	@Override
	public String getBookAuthor() {
		return bookAuthor;
	}

	@Override
	public void setBookAuthor(String bookAuthor) {
		this.bookAuthor = bookAuthor;
	}

	@Override
	public List<String> getBookPages() {
		return bookPages;
	}

	@Override
	public void setBookPages(List<String> bookPages) {
		this.bookPages = bookPages;
	}

	@Override
	public int getFocusEffectId() {
		return focusEffectId;
	}

	@Override
	public void setFocusEffectId(int focusEffectId) {
		this.focusEffectId = focusEffectId;
	}

	@Override
	public boolean isNeverDrop() {
		return neverDrop;
	}

	@Override
	public void setNeverDrop(boolean neverDrop) {
		this.neverDrop = neverDrop;
	}

	@Override
	public boolean isAdditionalArmour() {
		// TODO Auto-generated method stub
		return (equipmentSlot.equals(EquipmentSlot.Forearms) || equipmentSlot.equals(EquipmentSlot.Arms) || equipmentSlot.equals(EquipmentSlot.Hands) || equipmentSlot.equals(EquipmentSlot.Waist));
	}

	@Override
	public boolean isArrow()
	{
		if (this.getBasename() == null)
			return false;
		
		if (this.getBasename().toUpperCase().equals("ARROW"))
			return true;
		
		return false;
	}

	@Override
	public boolean isSkullItem() {
		if (this.getBasename() == null)
			return false;

		if (this.getBasename().toUpperCase().equals("SKULL_ITEM"))
			return true;

		if (this.getBasename().toUpperCase().equals("LEGACY_SKULL_ITEM"))
			return true;
		
		if (this.getBasename().toUpperCase().equals("PLAYER_HEAD"))
			return true;
		
		return false;
	}

	@Override
	public boolean isMeleeWeapon() {
		return ConfigurationManager.HandMaterials.contains(this.getBasename().toUpperCase());
	}

	@Override
	public EquipmentSlot getEquipmentSlot() {
		return equipmentSlot;
	}

	@Override
	public void setEquipmentSlot(EquipmentSlot equipmentSlot) {
		this.equipmentSlot = equipmentSlot;
	}

	@Override
	public Timestamp getLastUpdatedTime() {
		if (lastUpdatedTime == null)
			setLastUpdatedTimeNow();
		
		return lastUpdatedTime;
	}

	@Override
	public void setLastUpdatedTime(Timestamp lastUpdatedTime) {
		this.lastUpdatedTime = lastUpdatedTime;
		hookGlobalItemsChanged();
	}
	
	@Override
	public void setLastUpdatedTimeNow() {
		LocalDateTime datetime = LocalDateTime.now();
		Timestamp nowtimestamp = Timestamp.valueOf(datetime);
		System.out.println("Set LastUpdatedTime on " + getId());
		this.setLastUpdatedTime(nowtimestamp);
	}

	public void hookGlobalItemsChanged()
	{
		try
		{
			StateManager.getInstance().getConfigurationManager().setItemsChanged(true);
		} catch (CoreStateInitException e)
		{
			
		}
	}
	
	@Override
	public boolean isPlaceable() {
		return placeable;
	}

	@Override
	public void setPlaceable(boolean placeable) {
		this.placeable = placeable;
	}

	@Override
	public ItemType getItemType() {
		return itemType;
	}

	@Override
	public void setItemType(ItemType itemType) {
		this.itemType = itemType;
	}

	
	@Override
	public int getWeaponDelay() {
		return weaponDelay;
	}

	@Override
	public void setWeaponDelay(int weaponDelay) {
		this.weaponDelay = weaponDelay;
	}

	@Override
	public int getLeatherRgbDecimal() {
		return leatherRgbDecimal;
	}

	@Override
	public void setLeatherRgbDecimal(int leatherRgbDecimal) {
		this.leatherRgbDecimal = leatherRgbDecimal;
	}

	@Override
	public int getQuestId() {
		return QuestId;
	}

	@Override
	public void setQuestId(int questId) {
		QuestId = questId;
	}
}
