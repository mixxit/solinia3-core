package com.solinia.solinia.Interfaces;

import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidItemSettingException;
import com.solinia.solinia.Models.AugmentationSlotType;
import com.solinia.solinia.Models.SkillType;

public interface ISoliniaItem {
	ItemStack asItemStack();

	int getId();

	void setId(int id);

	String getDisplayname();

	void setDisplayname(String displayname);

	int getAbilityid();

	void setAbilityid(int abilityid);

	String getBasename();

	void setBasename(String basename);

	String getLore();

	void setLore(String lore);

	int getStrength();

	void setStrength(int strength);

	int getStamina();

	void setStamina(int stamina);

	int getAgility();

	void setAgility(int agility);

	int getDexterity();

	void setDexterity(int dexterity);

	int getIntelligence();

	void setIntelligence(int intelligence);

	int getWisdom();

	void setWisdom(int wisdom);

	int getCharisma();

	void setCharisma(int charisma);

	List<String> getAllowedClassNames();

	void setAllowedClassNames(List<String> allowedClassesNames);

	String getTexturebase64();

	void setTexturebase64(String texturebase64);

	boolean getQuestitem();

	void setQuestitem(boolean questitem);

	int getDamage();

	void setDamage(int damage);

	int getWeaponabilityid();

	void setWeaponabilityid(int weaponabilityid);

	int getAttackspeed();

	void setAttackspeed(int attackspeed);

	String getEnchantment1();

	void setEnchantment1(String enchantment1);

	int getEnchantment1val();

	void setEnchantment1val(int enchantment1val);

	String getEnchantment2();

	void setEnchantment2(String enchantment2);

	int getEnchantment2val();

	void setEnchantment2val(int enchantment2val);

	String getEnchantment3();

	void setEnchantment3(String enchantment3);

	int getEnchantment3val();

	void setEnchantment3val(int enchantment3val);

	String getEnchantment4();

	void setEnchantment4(String enchantment4);

	int getEnchantment4val();

	void setEnchantment4val(int enchantment4val);

	int getHpregen();

	void setHpregen(int hpregen);

	int getMpregen();

	void setMpregen(int mpregen);

	boolean isCoreitem();

	void setCoreitem(boolean coreitem);

	int getFireResist();

	void setFireResist(int fireResist);

	int getColdResist();

	void setColdResist(int coldResist);

	int getMagicResist();

	void setMagicResist(int magicResist);

	int getWorth();

	void setWorth(int worth);

	int getPoisonResist();

	void setPoisonResist(int poisonResist);

	boolean isSpellscroll();

	void setSpellscroll(boolean spellscroll);

	boolean useItemOnBlock(Player player, ISoliniaItem item, Block clickedBlock, boolean isConsumable) throws CoreStateInitException;

	String asJsonString();

	String asJsonStringEscaped();

	void sendItemSettingsToSender(CommandSender sender) throws CoreStateInitException;

	void editSetting(String setting, String value)
			throws InvalidItemSettingException, NumberFormatException, CoreStateInitException;

	short getColor();

	void setColor(short color);

	int getDiseaseResist();

	void setDiseaseResist(int diseaseResist);

	boolean useItemOnEntity(Plugin plugin, Player player, LivingEntity targetentity,
			boolean isConsumable) throws CoreStateInitException;

	void consume(Plugin plugin, Player player) throws CoreStateInitException;

	boolean isTemporary();

	void setTemporary(boolean isTemporary);

	boolean isConsumable();

	void setConsumable(boolean isConsumable);

	int getBaneUndead();

	void setBaneUndead(int baneUndead);

	boolean isPetControlRod();

	void setPetControlRod(boolean isPetControlRod);

	boolean isAugmentation();

	void setAugmentation(boolean isAugmentation);

	boolean isCrafting();

	boolean isQuest();

	void setQuest(boolean isQuest);

	AugmentationSlotType getAcceptsAugmentationSlotType();

	AugmentationSlotType getAugmentationFitsSlotType();

	void setAugmentationFitsSlotType(AugmentationSlotType augmentationFitsSlotType);

	String getDiscoverer();

	void setDiscoverer(String discoverer);

	int getMinLevel();

	void setMinLevel(int minLevel);

	int getAC();

	void setAC(int ac);

	int getHp();

	void setHp(int hp);

	int getMana();

	void setMana(int mana);

	boolean isExperienceBonus();

	void setExperienceBonus(boolean isExperienceBonus);

	SkillType getSkillModType();

	void setSkillModType(SkillType skillModType);

	int getSkillModValue();

	void setSkillModValue(int skillModValue);

	int getSkillModValue2();

	void setSkillModValue2(int skillModValue2);

	SkillType getSkillModType3();

	void setSkillModType3(SkillType skillModType3);

	int getSkillModValue3();

	void setSkillModValue3(int skillModValue3);

	SkillType getSkillModType4();

	void setSkillModType4(SkillType skillModType4);

	SkillType getSkillModType2();

	void setSkillModType2(SkillType skillModType2);

	int getSkillModValue4();

	boolean isOperatorCreated();

	void setOperatorCreated(boolean operatorCreated);

	boolean isArtifact();

	void setArtifact(boolean artifact);

	boolean isArtifactFound();

	void setArtifactFound(boolean artifactFound);

	int getDye();

	void setDye(int dye);

	boolean isFingersItem();

	void setFingersItem(boolean isFingersItem);

	boolean isNeckItem();

	void setNeckItem(boolean isNeckItem);

	boolean isShouldersItem();

	void setShouldersItem(boolean isShouldersItem);

	boolean isTerritoryFlag();

	void setTerritoryFlag(boolean territoryFlag);

	ItemStack asItemStackForMerchant(int costmultiplier);

	boolean isReagent();

	void setReagent(boolean reagent);

	boolean isArmour();

	boolean isJewelry();

	boolean isWeaponOrBowOrShield();
}
