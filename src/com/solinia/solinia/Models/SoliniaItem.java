package com.solinia.solinia.Models;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.solinia.solinia.Adapters.ItemStackAdapter;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidItemSettingException;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Utils.*;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_12_R1.NBTTagCompound;

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
	private int intelligence = 0;
	private int wisdom = 0;
	private int charisma = 0;
	private List<String> allowedClassNames = new ArrayList<String>();
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
	private byte color;
	private boolean isTemporary;
	private boolean isConsumable;
	private int baneUndead = 0;
	private boolean isPetControlRod = false;
	private boolean isAugmentation = false;
	private boolean isCrafting = false;
	private boolean isQuest = false;
	private AugmentationSlotType augmentationFitsSlotType = AugmentationSlotType.NONE;
	private String discoverer = "";

	@Override
	public ItemStack asItemStack() {
		return ItemStackAdapter.Adapt(this);
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
	public String getTexturebase64() {
		return texturebase64;
	}

	@Override
	public void setTexturebase64(String texturebase64) {
		this.texturebase64 = texturebase64;
	}

	@Override
	public boolean getQuestitem() {
		return questitem;
	}

	@Override
	public void setQuestitem(boolean questitem) {
		this.questitem = questitem;
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
	public int getWorth() {
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
	public boolean useItemOnEntity(Plugin plugin, Player player, ISoliniaItem item, LivingEntity targetentity, boolean isConsumable)
			throws CoreStateInitException {
		
		if (item.isPetControlRod())
		{
			LivingEntity pet = StateManager.getInstance().getEntityManager().getPet(player);
			if (pet != null)
			{
				if (pet instanceof Wolf)
				{
					Wolf wolf = (Wolf)pet;
					wolf.setTarget(targetentity);
					player.sendMessage("You send your pet to attack!");
				}
			}
		}
		
		ISoliniaSpell spell = StateManager.getInstance().getConfigurationManager().getSpell(item.getAbilityid());
		if (spell == null) {
			return false;
		}
		
		// TODO - Implement toggling of bard songs
		if (spell.isBardSong())
		{
			player.sendMessage("Bard songs are not currently implemented");
			return false;
		}

		if (!isConsumable)
		if (spell.getMana() > SoliniaPlayerAdapter.Adapt(player).getMana()) {
			player.sendMessage(ChatColor.GRAY + "Insufficient Mana  [E]");
			return false;
		}
		
		try
		{
			if (StateManager.getInstance().getEntityManager().getEntitySpellCooldown(player, spell.getId()) != null)
			{
				Calendar calendar = Calendar.getInstance();
				java.util.Date now = calendar.getTime();
				Timestamp nowtimestamp = new Timestamp(now.getTime());
				Timestamp expiretimestamp = StateManager.getInstance().getEntityManager().getEntitySpellCooldown(player, spell.getId());
	
				if (expiretimestamp != null)
				if (!nowtimestamp.after(expiretimestamp))
				{
					player.sendMessage("You do not have enough willpower to cast " + spell.getName() + " (Wait: " + ((expiretimestamp.getTime() - nowtimestamp.getTime())/1000) + "s");
					return false;
				}
			}
		} catch (CoreStateInitException e)
		{
			// skip
			return false;
		}
		

		boolean itemUseSuccess = spell.tryApplyOnEntity(plugin, player, targetentity);

		if (itemUseSuccess) {
			if (spell.getRecastTime() > 0)
			{
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.MILLISECOND, spell.getRecastTime());
				java.util.Date expire = calendar.getTime();
				Timestamp expiretimestamp = new Timestamp(expire.getTime());
				
				StateManager.getInstance().getEntityManager().addEntitySpellCooldown(player, spell.getId(),expiretimestamp);
			}
			if (!isConsumable)
				SoliniaPlayerAdapter.Adapt(player).reducePlayerMana(spell.getMana());
		}

		return itemUseSuccess;
	}

	@Override
	public boolean useItemOnBlock(Player player, ISoliniaItem item, Block clickedBlock, boolean isConsumable) throws CoreStateInitException {
		ISoliniaSpell spell = StateManager.getInstance().getConfigurationManager().getSpell(item.getAbilityid());
		if (spell == null) {
			return false;
		}

		if (!isConsumable)
		if (spell.getMana() > SoliniaPlayerAdapter.Adapt(player).getMana()) {
			player.sendMessage(ChatColor.GRAY + "Insufficient Mana  [E]");
			return false;
		}

		boolean itemUseSuccess = false;

		itemUseSuccess = spell.tryApplyOnBlock(player, clickedBlock);

		if (itemUseSuccess) {
			SoliniaPlayerAdapter.Adapt(player).reducePlayerMana(spell.getMana());
		}

		return itemUseSuccess;
	}

	private String convertItemStackToJsonRegular() {
        // First we convert the item stack into an NMS itemstack
        net.minecraft.server.v1_12_R1.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(asItemStack());
        NBTTagCompound compound = new NBTTagCompound();
        compound = nmsItemStack.save(compound);

        return compound.toString();
    }
	
	@Override
	public String asJsonString() {
		String out = convertItemStackToJsonRegular();
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
		sender.sendMessage("- id: " + ChatColor.GOLD + getId() + ChatColor.RESET);
		sender.sendMessage("- displayname: " + ChatColor.GOLD + getDisplayname() + ChatColor.RESET);
		sender.sendMessage("- basename: " + ChatColor.GOLD + getBasename() + ChatColor.RESET);
		sender.sendMessage("- temporary: " + ChatColor.GOLD + isTemporary() + ChatColor.RESET);
		sender.sendMessage("- worth: " + ChatColor.GOLD + getWorth() + ChatColor.RESET);
		sender.sendMessage("- abilityid: " + ChatColor.GOLD + getAbilityid() + ChatColor.RESET);		
		sender.sendMessage("- consumable: " + ChatColor.GOLD + isConsumable() + ChatColor.RESET);
		sender.sendMessage("- petcontrolrod: " + ChatColor.GOLD + isPetControlRod() + ChatColor.RESET);
		sender.sendMessage("- crafting: " + ChatColor.GOLD + isCrafting() + ChatColor.RESET);
		sender.sendMessage("- augmentation: " + ChatColor.GOLD + isAugmentation() + ChatColor.RESET);
		sender.sendMessage("- quest: " + ChatColor.GOLD + isQuest() + ChatColor.RESET);
		sender.sendMessage("- acceptsaugmentationslottype: " + ChatColor.GOLD + getAcceptsAugmentationSlotType() + ChatColor.RESET);
		sender.sendMessage("- augmentationfitsslottype: " + ChatColor.GOLD + this.getAugmentationFitsSlotType().name() + ChatColor.RESET);
		sender.sendMessage("----------------------------");
		sender.sendMessage("- damage: " + ChatColor.GOLD + getDamage() + ChatColor.RESET);
		sender.sendMessage("- baneundead: " + ChatColor.GOLD + getBaneUndead() + ChatColor.RESET);
		sender.sendMessage("- weaponabilityid: " + ChatColor.GOLD + getWeaponabilityid() + ChatColor.RESET);
		sender.sendMessage("----------------------------");
		sender.sendMessage("- strength: " + ChatColor.GOLD + getStrength() + ChatColor.RESET);
		sender.sendMessage("- stamina: " + ChatColor.GOLD + getStamina() + ChatColor.RESET);
		sender.sendMessage("- agility: " + ChatColor.GOLD + getAgility() + ChatColor.RESET);
		sender.sendMessage("- dexterity: " + ChatColor.GOLD + getDexterity() + ChatColor.RESET);
		sender.sendMessage("- intelligence: " + ChatColor.GOLD + getIntelligence() + ChatColor.RESET);
		sender.sendMessage("- wisdom: " + ChatColor.GOLD + getWisdom() + ChatColor.RESET);
		sender.sendMessage("- charisma: " + ChatColor.GOLD + getCharisma() + ChatColor.RESET);
		sender.sendMessage("----------------------------");
		sender.sendMessage("- magicresist: " + ChatColor.GOLD + getMagicResist() + ChatColor.RESET);
		sender.sendMessage("- coldresist: " + ChatColor.GOLD + getColdResist() + ChatColor.RESET);
		sender.sendMessage("- fireresist: " + ChatColor.GOLD + getFireResist() + ChatColor.RESET);
		sender.sendMessage("- diseaseresist: " + ChatColor.GOLD + getDiseaseResist() + ChatColor.RESET);
		sender.sendMessage("- poisonresist: " + ChatColor.GOLD + getPoisonResist() + ChatColor.RESET);
		sender.sendMessage("----------------------------");
		sender.sendMessage("- hpregen: " + ChatColor.GOLD + getHpregen() + ChatColor.RESET);
		sender.sendMessage("- mpregen: " + ChatColor.GOLD + getMpregen() + ChatColor.RESET);
	}

	@Override
	public void editSetting(String setting, String value)
			throws InvalidItemSettingException, NumberFormatException, CoreStateInitException {

		switch (setting.toLowerCase()) {
		case "displayname":
			if (value.equals(""))
				throw new InvalidItemSettingException("Name is empty");

			if (value.length() > 36)
				throw new InvalidItemSettingException("Name is longer than 36 characters");
			setDisplayname(value);
			break;
		case "worth":
			setWorth(Integer.parseInt(value));
			break;
		case "color":
			setColor((byte)Integer.parseInt(value));
			break;
		case "damage":
			setDamage(Integer.parseInt(value));
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
		case "weaponabilityid":
			setWeaponabilityid(Integer.parseInt(value));
			break;
		case "petcontrolrod":
			setPetControlRod(Boolean.parseBoolean(value));
			break;
		case "consumable":
			setConsumable(Boolean.parseBoolean(value));
			break;
		case "crafting":
			setCrafting(Boolean.parseBoolean(value));
			break;
		case "quest":
			setQuest(Boolean.parseBoolean(value));
			break;
		case "augmentation":
			setAugmentation(Boolean.parseBoolean(value));
			break;
		case "clearallowedclasses":
			setAllowedClassNames(new ArrayList<String>());
			break;
		case "augmentationfitsslottype":
			setAugmentationFitsSlotType(AugmentationSlotType.valueOf(value));
			break;
		default:
			throw new InvalidItemSettingException("Invalid Item setting. Valid Options are: displayname,worth,color,damage,hpregen,mpregen,strength,stamina,agility,dexterity,intelligence,wisdom,charisma,abilityid,consumable,crafting,quest,augmentation");
		}
	}

	@Override
	public byte getColor() {
		return color;
	}

	@Override
	public void setColor(byte color) {
		this.color = color;
	}

	@Override
	public void consume(Plugin plugin, Player player) throws CoreStateInitException {
		if (this.getAbilityid() < 1)
			return;
		
		useItemOnEntity(plugin, player,this,player,true);
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
	public boolean isPetControlRod() {
		return isPetControlRod;
	}

	@Override
	public void setPetControlRod(boolean isPetControlRod) {
		this.isPetControlRod = isPetControlRod;
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
		return isCrafting;
	}

	@Override
	public void setCrafting(boolean isCrafting) {
		this.isCrafting = isCrafting;
	}

	@Override
	public boolean isQuest() {
		return isQuest;
	}
	
	@Override
	public void setQuest(boolean isQuest) {
		this.isQuest = isQuest;
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
}
