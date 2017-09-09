package com.solinia.solinia.Models;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.solinia.solinia.Adapters.ItemStackAdapter;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidItemSettingException;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Managers.StateManager;

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
	private boolean spellscroll = false;
	private byte color;

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
	public void useItemOnEntity(Player player, ISoliniaItem item, LivingEntity targetentity)
			throws CoreStateInitException {
		ISoliniaSpell spell = StateManager.getInstance().getConfigurationManager().getSpell(item.getAbilityid());
		if (spell == null) {
			return;
		}

		if (spell.getMana() > SoliniaPlayerAdapter.Adapt(player).getMana()) {
			player.sendMessage(ChatColor.GRAY + "Insufficient Mana  [E]");
			return;
		}

		boolean itemUseSuccess = spell.tryApplyOnEntity(player, targetentity);

		if (itemUseSuccess) {
			SoliniaPlayerAdapter.Adapt(player).reducePlayerMana(spell.getMana());
		}

		return;
	}

	@Override
	public void useItemOnBlock(Player player, ISoliniaItem item, Block clickedBlock) throws CoreStateInitException {
		ISoliniaSpell spell = StateManager.getInstance().getConfigurationManager().getSpell(item.getAbilityid());
		if (spell == null) {
			return;
		}

		if (spell.getMana() > SoliniaPlayerAdapter.Adapt(player).getMana()) {
			player.sendMessage(ChatColor.GRAY + "Insufficient Mana  [E]");
			return;
		}

		boolean itemUseSuccess = false;

		itemUseSuccess = spell.tryApplyOnBlock(player, clickedBlock);

		if (itemUseSuccess) {
			SoliniaPlayerAdapter.Adapt(player).reducePlayerMana(spell.getMana());
		}

		return;
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
		sender.sendMessage("- worth: " + ChatColor.GOLD + getWorth() + ChatColor.RESET);
		sender.sendMessage("- abilityid: " + ChatColor.GOLD + getAbilityid() + ChatColor.RESET);
	}

	@Override
	public void editSetting(String setting, String value)
			throws InvalidItemSettingException, NumberFormatException, CoreStateInitException {

		switch (setting.toLowerCase()) {
		case "displayname":
			if (value.equals(""))
				throw new InvalidItemSettingException("Name is empty");

			if (value.length() > 15)
				throw new InvalidItemSettingException("Name is longer than 15 characters");
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
		case "charisma":
			setCharisma(Integer.parseInt(value));
			break;
		default:
			throw new InvalidItemSettingException("Invalid Item setting. Valid Options are: displayname,worth,color,damage,hpregen,mpregen,strength,stamina,agility,dexterity,intelligence,wisdom,charisma");
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

}
