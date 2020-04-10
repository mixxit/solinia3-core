package com.solinia.solinia.Models;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Color;
import org.bukkit.Material;
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
import com.solinia.solinia.Interfaces.IPersistable;
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
import net.minecraft.server.v1_14_R1.Tuple;

public class SoliniaItem implements ISoliniaItem,IPersistable {

	private int id;
	private UUID primaryUUID = UUID.randomUUID();
	private UUID secondaryUUID = UUID.randomUUID();

	private String displayname;
	private String basename;
	private int abilityid = 0;
	private String lore;
	private int strength = 0;
	private int stamina = 0;
	private int agility = 0;
	private int dexterity = 0;
	private int procRate = 0;
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
	private boolean magic = false;
	private int elementalDamageType = 0;
	private int elementalDamageAmount = 0;
	private boolean spellscroll = false;
	private short color;
	private int dye;
	private boolean isTemporary;
	private boolean isConsumable;
	private String consumableRequireQuestFlag = "";
	private String consumableRequireNotQuestFlag = "";
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
	private String requiredWeaponSkillType = "";
	
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
	
	private int appearanceId = 0;
	
	private int baneDmgBody = 0;
	private int baneDmgRace = 0;
	private int baneDmgBodyAmount = 0;
	private int baneDmgRaceAmount = 0;
	
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
	public UUID getPrimaryUUID() {
		// TODO Auto-generated method stub
		return this.primaryUUID;
	}
	@Override
	public void setPrimaryUUID(UUID uuid) {
		// TODO Auto-generated method stub
		this.primaryUUID = uuid;
	}
	@Override
	public UUID getSecondaryUUID() {
		// TODO Auto-generated method stub
		return this.secondaryUUID;
	}
	@Override
	public void setSecondaryUUID(UUID uuid) {
		// TODO Auto-generated method stub
		this.secondaryUUID = uuid;
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
	public List<String> getAllowedClassNamesUpper() {
		return getAllowedClassNames().stream().map(String::toUpperCase).collect(Collectors.toList());
	}
	
	@Override
	public void setAllowedClassNames(List<String> allowedClassesNames) {
		this.allowedClassNames = allowedClassesNames;
	}
	
	@Override
	public List<String> getAllowedRaceNamesUpper() {
		return getAllowedRaceNames().stream().map(String::toUpperCase).collect(Collectors.toList());
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
	public int getDefinedItemDamage() {
		return damage;
	}

	@Override
	public void setDefinedItemDamage(int damage) {
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
		
		if (EntityUtils.isMezzed(player))
		{
			player.sendMessage("* You cannot do this while mezzed!");
			return false;
		}
		
		if (EntityUtils.isStunned(player))
		{
			player.sendMessage("* You cannot do this while stunned!");
			return false;
		}
		
		double distanceOverLimit = Utils.DistanceOverAggroLimit((LivingEntity) player,
				targetentity);

		if (distanceOverLimit > 0)
		{
			player.sendMessage("You were too far to use this item on that entity");
			return false;
		}
		
		if (StateManager.getInstance().getEntityManager().getEntitySpellCooldown(player,
				this.getAbilityid()) != null) {
			LocalDateTime datetime = LocalDateTime.now();
			Timestamp nowtimestamp = Timestamp.valueOf(datetime);
			Timestamp expiretimestamp = StateManager.getInstance().getEntityManager()
					.getEntitySpellCooldown(player, this.getAbilityid());

			if (expiretimestamp != null)
				if (!nowtimestamp.after(expiretimestamp)) {
					player.sendMessage("You do not have enough willpower to use this item " + getDisplayname()
							+ " (Wait: " + ((expiretimestamp.getTime() - nowtimestamp.getTime()) / 1000) + "s");
					return false;
				}
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
			
			ISoliniaLivingEntity target = SoliniaLivingEntityAdapter.Adapt(targetentity);
			if (target != null)
			{
				if (target.isNPC())
					target.addToHateList(player.getUniqueId(), 1, false);
				player.sendMessage("You throw a " + getDisplayname() + " for [" + getItemWeaponDamage(false, null) + "] damage");
				
				target.setHPChange(getItemWeaponDamage(false, null)*-1, player);
				return true;
			}
			
			return false;
		}
		
		if (isConsumable == true && !getConsumableRequireQuestFlag().equals(""))
		{
			if (!SoliniaPlayerAdapter.Adapt(player).hasQuestFlag(getConsumableRequireQuestFlag()))
			{
				player.sendMessage("* This item does not appear to work [missing queststep]");
				return false;
			}
		}

		if (isConsumable == true && !this.getConsumableRequireNotQuestFlag().equals(""))
		{
			if (SoliniaPlayerAdapter.Adapt(player).hasQuestFlag(getConsumableRequireNotQuestFlag()))
			{
				player.sendMessage("* This item appears to no longer work [questitem no longer needed]");
				return false;
			}
		}
		
		if (isConsumable == true && isExperienceBonus())
		{
			SoliniaPlayerAdapter.Adapt(player).grantExperienceBonusFromItem();
			System.out.println("Granted " + player.getName() + " experience bonus from item [" + SoliniaPlayerAdapter.Adapt(player).getExperienceBonusExpires().toString() + "]");
			return true;
		}
		
		if (isConsumable == true && !getLanguagePrimer().equals(""))
		{
			SoliniaPlayerAdapter.Adapt(player).setSkill(Utils.getSkillType2(getLanguagePrimer()), 100);
			System.out.println("Granted " + player.getName() + " language skill from item [" + getLanguagePrimer() + "]");
			return true;
		}
		
		if (getAllowedClassNamesUpper() != null && getAllowedClassNamesUpper().size() > 0)
			if (!getAllowedClassNamesUpper().contains(SoliniaPlayerAdapter.Adapt(player).getClassObj().getName().toUpperCase())) {
				player.sendMessage(ChatColor.GRAY + "Your class cannot consume this item");
				return false;
			}
		
		if (getAllowedRaceNamesUpper() != null &&getAllowedRaceNamesUpper().size() > 0)
			if (!getAllowedRaceNamesUpper().contains(SoliniaPlayerAdapter.Adapt(player).getRace().getName().toUpperCase())) {
				player.sendMessage(ChatColor.GRAY + "Your race cannot consume this item");
				return false;
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
		
		if (targetentity != null && player != null && spell != null)
		{
			Tuple<Boolean,String> result = SoliniaSpell.isValidEffectForEntity(targetentity, player, spell);

			if (!result.a())
			{
				Utils.SendHint(player, HINT.SPELL_INVALIDEFFECT, result.b(), false);
				return false;
			}
		}
		
		return spell.tryCast(player,targetentity,!isConsumable,!isConsumable,this.getRequiredWeaponSkillType());
	}
	
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
		sender.sendMessage("- displayname: " + ChatColor.GOLD + getDisplayname() + ChatColor.RESET + " tier: " + ChatColor.GOLD + getTier() + ChatColor.RESET);
		sender.sendMessage("- lastupdated: " + ChatColor.GOLD + this.getLastUpdatedTimeAsString() + ChatColor.RESET + " appearanceid: " + ChatColor.GOLD + this.getAppearanceId() + ChatColor.RESET);
		sender.sendMessage("- worth: " + ChatColor.GOLD + getWorth() + ChatColor.RESET + " placeable: " + ChatColor.GOLD + isPlaceable() + ChatColor.RESET);
		sender.sendMessage("- color (blocktype): " + ChatColor.GOLD + getColor() + ChatColor.RESET + " dye (armour color): " + ChatColor.GOLD + getDye() + ChatColor.RESET);
		String leathercolor = "NONE";
		if (getLeatherRgbDecimal() > 0)
		{
			Color colorTmp = Color.fromRGB(getLeatherRgbDecimal());
			leathercolor = ColorUtil.fromRGB(colorTmp.getRed(),colorTmp.getGreen(), colorTmp.getBlue()) + "(Closest)" + ChatColor.RESET;
		}
		sender.sendMessage("- requiredweaponskilltype: " + ChatColor.GOLD + getRequiredWeaponSkillType() + ChatColor.RESET);
		sender.sendMessage("- leatherrgbdecimal: " + ChatColor.GOLD + getLeatherRgbDecimal() + ChatColor.RESET + leathercolor + " See: https://bit.ly/2i02I8k");
		sender.sendMessage("- reagent: " + ChatColor.GOLD + isReagent() + ChatColor.RESET);
		sender.sendMessage("- temporary: " + ChatColor.GOLD + isTemporary() + ChatColor.RESET + " - consumable: " + ChatColor.GOLD + isConsumable() + ChatColor.RESET);
		sender.sendMessage("- consumablerequirenotquestflag: " + ChatColor.GOLD + getConsumableRequireNotQuestFlag() + ChatColor.RESET);
		sender.sendMessage("- consumablerequirequestflag: " + ChatColor.GOLD + getConsumableRequireQuestFlag() + ChatColor.RESET);
		sender.sendMessage("- bandage: " + ChatColor.GOLD + isBandage() + ChatColor.RESET + " languageprimer: " + ChatColor.GOLD + getLanguagePrimer() + ChatColor.RESET);
		sender.sendMessage("- augmentation: " + ChatColor.GOLD + isAugmentation() + ChatColor.RESET);
		sender.sendMessage("- discoverer: " + ChatColor.GOLD + getDiscoverer() + ChatColor.RESET + " - artifact: " + ChatColor.GOLD + isArtifact() + ChatColor.RESET + " Found: (" + isArtifactFound() + ")"+ ChatColor.RESET);
		sender.sendMessage("----------------------------");
		sender.sendMessage("- acceptsaugmentationslottype: " + ChatColor.GOLD + getAcceptsAugmentationSlotType() + ChatColor.RESET);
		sender.sendMessage("- augmentationfitsslottype: " + ChatColor.GOLD + this.getAugmentationFitsSlotType().name() + ChatColor.RESET);
		sender.sendMessage("----------------------------");
		sender.sendMessage("- hpregen: " + ChatColor.GOLD + getHpregen() + ChatColor.RESET + " mpregen: " + ChatColor.GOLD + getMpregen() + ChatColor.RESET);
		sender.sendMessage("- ac: " + ChatColor.GOLD + getAC() + ChatColor.RESET + "hp: " + ChatColor.GOLD + getHp() + ChatColor.RESET + " mana: " + ChatColor.GOLD + getMana() + ChatColor.RESET);
		sender.sendMessage("- damage: " + ChatColor.GOLD + getDefinedItemDamage() + ChatColor.RESET + " weapondelay: " + ChatColor.GOLD + getWeaponDelay() + ChatColor.RESET + " baneundead: " + ChatColor.GOLD + getBaneUndead() + ChatColor.RESET);
		sender.sendMessage("- abilityid: " + ChatColor.GOLD + getAbilityid() + ChatColor.RESET + " - weaponabilityid: " + ChatColor.GOLD + getWeaponabilityid() + ChatColor.RESET + " focuseffectid: " + ChatColor.GOLD + getFocusEffectId() + ChatColor.RESET);
		sender.sendMessage("- attackspeedpct: " + ChatColor.GOLD + getAttackspeed() + "%" + ChatColor.RESET + " procrate: " + ChatColor.GOLD + getProcRate() + ChatColor.RESET);
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
		for(String classname : this.getAllowedClassNamesUpper())
		{
			allowedClassNames += classname + ",";
		}
		sender.sendMessage("- allowedclassnames: " + allowedClassNames.trim());

		String allowedRaceNames = "";
		for(String racename : this.getAllowedRaceNamesUpper())
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
		case "appearanceid":
			setAppearanceId(Integer.parseInt(value));
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
		case "texturebase64fromitem":
			int itemid = Integer.parseInt(value);
			ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(itemid);
			if (item == null)
				throw new InvalidItemSettingException("ITEMID does not exist");
			// fetches custom head texture by existing npc
			this.setTexturebase64(item.getTexturebase64());
			break;
		case "requiredweaponskilltype":
			if (!value.toUpperCase().equals("PIERCING") && !value.toUpperCase().equals("CLEAR"))
				throw new InvalidItemSettingException("requiredweaponskilltype can only be PIERCING or CLEAR (clear removes requirement)");
			
			if (value.toUpperCase().equals("CLEAR"))
				value = "";
			
			setRequiredWeaponSkillType(value.toUpperCase());
			break;
		case "procrate":
			setProcRate(Integer.parseInt(value));
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
			setDefinedItemDamage(Integer.parseInt(value));
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
			if (Integer.parseInt(value) == 0)
			{
				setQuestId(0);
			}
			else
			{
				ISoliniaQuest quest = StateManager.getInstance().getConfigurationManager().getQuest(Integer.parseInt(value));
				setQuestId(quest.getId());
			}
			break;
		case "consumablerequirenotquestflag":
			if (this.getQuestId() < 1)
				throw new InvalidItemSettingException("You must first mark the questid on this item before assigning this flag");
			setConsumableRequireNotQuestFlag(value);
			break;
		case "consumablerequirequestflag":
			if (this.getQuestId() < 1)
				throw new InvalidItemSettingException("You must first mark the questid on this item before assigning this flag");
			setConsumableRequireQuestFlag(value);
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
			try
			{
			setSkillModType(SkillType.valueOf(value));
			} catch (IllegalArgumentException e)
			{
				String types = "";
				for(SkillType type: SkillType.values())
				{
					types += type+",";
				}
				throw new InvalidItemSettingException("Invalid type, type must be exactly the same case and can be one of the following: " + types);
			}
			break;
		case "skillmodvalue":
			setSkillModValue(Integer.parseInt(value));
			break;
		case "skillmodtype2":
			try
			{
			setSkillModType2(SkillType.valueOf(value));
			} catch (IllegalArgumentException e)
			{
				String types = "";
				for(SkillType type: SkillType.values())
				{
					types += type+",";
				}
				throw new InvalidItemSettingException("Invalid type, type must be exactly the same case and can be one of the following: " + types);
			}
			break;
		case "skillmodvalue2":
			setSkillModValue2(Integer.parseInt(value));
			break;
		case "skillmodtype3":
			try
			{
			setSkillModType3(SkillType.valueOf(value));
			} catch (IllegalArgumentException e)
			{
				String types = "";
				for(SkillType type: SkillType.values())
				{
					types += type+",";
				}
				throw new InvalidItemSettingException("Invalid type, type must be exactly the same case and can be one of the following: " + types);
			}
			break;
		case "skillmodvalue3":
			setSkillModValue3(Integer.parseInt(value));
			break;
		case "skillmodtype4":
			try
			{
			setSkillModType4(SkillType.valueOf(value));
			} catch (IllegalArgumentException e)
			{
				String types = "";
				for(SkillType type: SkillType.values())
				{
					types += type+",";
				}
				throw new InvalidItemSettingException("Invalid type, type must be exactly the same case and can be one of the following: " + types);
			}
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
			try
			{
				setItemType(ItemType.valueOf(value));
			} catch (IllegalArgumentException e)
			{
				String types = "";
				for(ItemType type: ItemType.values())
				{
					types += type+",";
				}
				throw new InvalidItemSettingException("Invalid Item Type, item type must be exactly the same case and can be one of the following: " + types);
			}
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
			throw new InvalidItemSettingException("Invalid Item setting. Valid Options are: displayname,worth,color,damage,hpregen,mpregen,strength,stamina,agility,dexterity,intelligence,wisdom,charisma,abilityid,consumable,crafting,quest,augmentation,cleardiscoverer,clearallowedclasses,clearallowedraces,ac,hp,mana,experiencebonus,skillmodtype,skillmodvalue,skillmodtype2,skillmodvalue2,skillmodtype3,skillmodvalue3,skillmodtype4,skillmodvalue4,artifact,spellscroll,territoryflag,reagent,allowedclassnames,allowedracenames,identifymessage,languageprimer,clearlanguageprimer,texturebase64fromitem");
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
		this.setLastUpdatedTimeNow();
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
		this.setLastUpdatedTimeNow();
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
		//System.out.println("Set LastUpdatedTime on " + getId());
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

	@Override
	public String getRequiredWeaponSkillType() {
		return requiredWeaponSkillType;
	}

	@Override
	public void setRequiredWeaponSkillType(String requiredWeaponSkillType) {
		this.requiredWeaponSkillType = requiredWeaponSkillType;
	}

	@Override
	public int getAppearanceId() {
		return appearanceId;
	}

	@Override
	public void setAppearanceId(int appearanceId) {
		this.appearanceId = appearanceId;
	}

	@Override
	public int getTier() {
		int tier = (int) Math.floor((this.getMinLevel() / 10)+1);
		if (tier < 1)
			tier = 1;
		return tier;
	}

	@Override
	public boolean isMagic() {
		return magic;
	}
	
	
	@Override
	public boolean isItemMagical(ItemStack itemStack)
	{
		if (isMagic())
			return true;
		
		if (itemStack != null)
		{
			Integer augItemId = ItemStackUtils.getAugmentationItemId(itemStack);
			if (augItemId != null
					&& augItemId != 0) 
			{
				try
				{
					ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(augItemId);
					if (item != null && item.isMagic())
						return true;
				} catch (CoreStateInitException e)
				{
					
				}
			}
		}
		
		return false;
	}

	@Override
	public void setMagic(boolean magic) {
		this.magic = magic;
	}

	@Override
	public boolean isEquipable(ISoliniaRace race, ISoliniaClass classObj) {
		if (getAllowedClassNamesUpper() != null && getAllowedClassNamesUpper().size() > 0)
			if (!getAllowedClassNamesUpper().contains(classObj.getName().toUpperCase())) {
				return false;
			}

		if (getAllowedRaceNamesUpper() != null && getAllowedRaceNamesUpper().size() > 0)
			if (!getAllowedRaceNamesUpper().contains(race.getName().toUpperCase())) {
				return false;
			}
		
		return true;
	}
	
	@Override
	public ISoliniaItem getAugmentation(ItemStack itemStack)
	{
		if (itemStack == null)
			return null;
		
		Integer augItemId = ItemStackUtils.getAugmentationItemId(itemStack);

		if (augItemId != null && augItemId != 0) {
			try
			{
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(augItemId);
				return item;
			} catch (CoreStateInitException e)
			{
				
			}
		}
		
		return null;
	}
	
	@Override
	public int getItemElementalFlag(boolean checkAugmentations, ItemStack itemStack) {
		int elementalDamageType = this.getElementalDamageType();
		
		if (checkAugmentations && itemStack != null)
		if (getAugmentation(itemStack) != null)
		{
			int elementalDamageTypeAug = getAugmentation(itemStack).getElementalDamageType();
			if (elementalDamageTypeAug > 0)
				return elementalDamageTypeAug;
		}
		return elementalDamageType;
	}

	@Override
	public int getItemWeaponDamage(boolean checkAugmentations, ItemStack itemStack) {
		int damage = this.getDefinedItemDamage();
		if (checkAugmentations && itemStack != null)
		if (getAugmentation(itemStack) != null)
		{
			damage += getAugmentation(itemStack).getDefinedItemDamage();
		}
		return damage;
	}

	@Override
	public int getElementalDamageType() {
		return elementalDamageType;
	}

	@Override
	public void setElementalDamageType(int elementalDamageType) {
		this.elementalDamageType = elementalDamageType;
	}

	@Override
	public int getItemElementalDamage(int magic, int fire, int cold, int poison, int disease, int chromatic,
			int prismatic, int physical, int corruption, boolean augments, ItemStack itemStack) {
		
		switch (Utils.getSpellResistType(this.elementalDamageType)) {
		case RESIST_MAGIC:
			magic += this.getElementalDamageAmount();
			break;
		case RESIST_FIRE:
			fire += this.getElementalDamageAmount();
			break;
		case RESIST_COLD:
			cold += this.getElementalDamageAmount();
			break;
		case RESIST_POISON:
			poison += this.getElementalDamageAmount();
			break;
		case RESIST_DISEASE:
			disease += this.getElementalDamageAmount();
			break;
		case RESIST_CHROMATIC:
			chromatic += this.getElementalDamageAmount();
			break;
		case RESIST_PRISMATIC:
			prismatic += this.getElementalDamageAmount();
			break;
		case RESIST_PHYSICAL:
			physical += this.getElementalDamageAmount();
			break;
		case RESIST_CORRUPTION:
			corruption += this.getElementalDamageAmount();
			break;
		default:
			break;
		}

		if (augments)
			if (getAugmentation(itemStack) != null)
				getAugmentation(itemStack).getItemElementalDamage(magic, fire, cold, poison, disease, chromatic, prismatic, physical, corruption, false, null);
		
		return magic + fire + cold + poison + disease + chromatic + prismatic + physical + corruption;
	}

	@Override
	public int getElementalDamageAmount() {
		return elementalDamageAmount;
	}

	@Override
	public void setElementalDamageAmount(int elementalDamageAmount) {
		this.elementalDamageAmount = elementalDamageAmount;
	}

	@Override
	public int getItemBaneDamageBody(int bodyType, boolean augmentations, ItemStack itemStack) {
		int damage = 0;
		
		if (this.getBaneDmgBody() == bodyType)
			damage += getBaneDmgBodyAmount();
		
		if (augmentations && itemStack != null)
		if (getAugmentation(itemStack) != null)
		{
			if (getAugmentation(itemStack).getBaneDmgBody() == bodyType)
			damage += getAugmentation(itemStack).getBaneDmgBodyAmount();
		}
		return damage;
	}

	@Override
	public int getItemBaneDamageRace(ISoliniaRace race, boolean augmentations, ItemStack itemStack) {
		int damage = 0;
		
		if (this.getBaneDmgRace() == race.getId())
			damage += getBaneDmgRaceAmount();
		
		if (augmentations && itemStack != null)
		if (getAugmentation(itemStack) != null)
		{
			if (getAugmentation(itemStack).getBaneDmgRace() == race.getId())
			damage += getAugmentation(itemStack).getBaneDmgRaceAmount();
		}
		return damage;
	}

	@Override
	public int getBaneDmgBody() {
		return baneDmgBody;
	}

	private void setBaneDmgBody(int baneDmgBody) {
		this.baneDmgBody = baneDmgBody;
	}

	@Override
	public int getBaneDmgRace() {
		return baneDmgRace;
	}

	private void setBaneDmgRace(int baneDmgRace) {
		this.baneDmgRace = baneDmgRace;
	}

	@Override
	public int getBaneDmgBodyAmount() {
		return baneDmgBodyAmount;
	}

	private void setBaneDmgBodyAmount(int baneDmgBodyAmount) {
		this.baneDmgBodyAmount = baneDmgBodyAmount;
	}

	@Override
	public  int getBaneDmgRaceAmount() {
		return baneDmgRaceAmount;
	}

	private void setBaneDmgRaceAmount(int baneDmgRaceAmount) {
		this.baneDmgRaceAmount = baneDmgRaceAmount;
	}

	@Override
	public int getItemBaneDamageBody(boolean augmentations, ItemStack itemStack) {
		int body = this.getBaneDmgBody();
		
		if (augmentations && itemStack != null)
		if (getAugmentation(itemStack) != null)
		{
			int elementalDamageTypeAug = getAugmentation(itemStack).getBaneDmgBody();
			if (elementalDamageTypeAug > 0)
				return elementalDamageTypeAug;
		}
		return body;
	}

	@Override
	public int getItemBaneDamageRace(boolean augmentations, ItemStack itemStack) {
		int race = this.getBaneDmgRace();
		
		if (augmentations && itemStack != null)
		if (getAugmentation(itemStack) != null)
		{
			int elementalDamageTypeAug = getAugmentation(itemStack).getBaneDmgRace();
			if (elementalDamageTypeAug > 0)
				return elementalDamageTypeAug;
		}
		return race;
	}

	@Override
	public boolean isWeapon() {
		/*if (!m_item || !m_item->IsClassCommon())
			return false;*/

		if (this.isArrow() && this.damage != 0)
			return true;
		else
			return ((this.damage != 0) && (this.weaponDelay != 0));
	}
	
	@Override
	public int getProcRate() {
		return procRate;
	}

	@Override
	public void setProcRate(int procRate) {
		this.procRate = procRate;
	}

	@Override
	public String getConsumableRequireQuestFlag() {
		return consumableRequireQuestFlag;
	}

	@Override
	public void setConsumableRequireQuestFlag(String consumableRequireQuestFlag) {
		this.consumableRequireQuestFlag = consumableRequireQuestFlag;
	}

	@Override
	public String getConsumableRequireNotQuestFlag() {
		return consumableRequireNotQuestFlag;
	}

	@Override
	public void setConsumableRequireNotQuestFlag(String consumableRequireNotQuestFlag) {
		this.consumableRequireNotQuestFlag = consumableRequireNotQuestFlag;
	}
}
