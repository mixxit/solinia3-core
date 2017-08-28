package com.solinia.solinia.Interfaces;

import java.util.List;

import org.bukkit.inventory.ItemStack;

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

}
