package com.solinia.solinia.Interfaces;

import java.io.IOException;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidNPCEventSettingException;
import com.solinia.solinia.Exceptions.InvalidNpcSettingException;
import com.solinia.solinia.Models.FactionStandingType;
import com.solinia.solinia.Models.InteractionType;
import com.solinia.solinia.Models.SkillType;
import com.solinia.solinia.Models.SoliniaDisguise;
import com.solinia.solinia.Models.SoliniaLivingEntity;
import com.solinia.solinia.Models.SoliniaNPCEventHandler;
import com.solinia.solinia.Models.SpellResistType;

public interface ISoliniaNPC extends IPersistable {

	int getId();

	void setId(int id);

	String getName();

	void setName(String name);

	void sendNpcSettingsToSender(CommandSender sender) throws CoreStateInitException;

	boolean editSetting(String setting, String value) throws InvalidNpcSettingException, NumberFormatException, CoreStateInitException, IOException;

	String getMctype();

	void setMctype(String mctype);

	int getLevel();

	void setLevel(int level);

	int getFactionid();

	void setFactionid(int factionid);

	String getHeaditem();

	void setHeaditem(String headitem);

	String getChestitem();

	void setChestitem(String chestitem);

	String getLegsitem();

	void setLegsitem(String legsitem);

	String getFeetitem();

	void setFeetitem(String feetitem);

	String getHanditem();

	void setHanditem(String handitem);

	String getOffhanditem();

	void setOffhanditem(String offhanditem);

	boolean isBoss();

	void setBoss(boolean boss);

	boolean isBurning();

	void setBurning(boolean burning);

	boolean isInvisible();

	void setInvisible(boolean invisible);

	boolean isCustomhead();

	void setCustomhead(boolean customhead);

	String getCustomheaddata();

	void setCustomheaddata(String customheaddata);

	int getMerchantid();

	void setMerchantid(int merchantid);

	boolean isUpsidedown();

	void setUpsidedown(boolean upsidedown);

	int getLoottableid();

	void setLoottableid(int loottableid);

	int getRaceid();

	void setRaceid(int raceid);

	int getClassid();

	void setClassid(int classid);

	boolean isRandomSpawn();

	void setRandomSpawn(boolean isRandomSpawn);

	String getKillTriggerText();

	void setKillTriggerText(String killTriggerText);

	String getRandomchatTriggerText();

	void setRandomchatTriggerText(String randomchatTriggerText);

	boolean isGuard();

	void setGuard(boolean isGuard);

	boolean isRoamer();

	void setRoamer(boolean isRoamer);

	ISoliniaClass getClassObj();
	
	void setCorePet(boolean isCorePet);

	List<ISoliniaNPCEventHandler> getEventHandlers();

	void setEventHandlers(List<ISoliniaNPCEventHandler> eventHandlers);

	void processInteractionEvent(SoliniaLivingEntity solentity, LivingEntity triggerentity, InteractionType type,
			String data);

	void processChatInteractionEvent(SoliniaLivingEntity solentity, LivingEntity triggerentity, String data);

	void addEventHandler(SoliniaNPCEventHandler eventhandler);

	void sendMerchantItemListToPlayer(Player player, int pageno);

	boolean canDodge();

	boolean canRiposte();

	boolean canDoubleAttack();

	boolean getDodgeCheck();

	boolean getRiposteCheck();

	boolean getDoubleAttackCheck();

	boolean isUndead();

	void setUndead(boolean isUndead);
	
	boolean isAnimal();

	void setAnimal(boolean isAnimal);

	String getDeathGrantsTitle();

	void setDeathGrantsTitle(String deathGrantsTitle);

	void sendNPCEvent(CommandSender sender, String triggertext);

	void sendNPCEvents(CommandSender sender);

	void editTriggerEventSetting(String triggertext, String setting, String value) throws InvalidNPCEventSettingException;

	List<String> getEventHandlerTriggerDatas();

	boolean isSummoner();

	void setSummoner(boolean isSummoner);

	String replaceChatWordsWithHints(String message);

	void disableAllSpawners(boolean parseBoolean);

	int getAccuracyRating();

	int getSkill(SkillType skillType);

	int getSkillCap(SkillType skillType);

	int getAvoidanceRating();

	int getAC();

	void setAC(int ac);

	boolean isHeroic();

	void setHeroic(boolean heroic);

	boolean isRaidboss();

	void setRaidboss(boolean raidboss);

	boolean isRaidheroic();

	void setRaidheroic(boolean raidheroic);

	boolean isSpeaksAllLanguages();

	void setSpeaksAllLanguages(boolean speaksAllLanguages);

	List<ISoliniaItem> getEquippedSoliniaItems(ISoliniaLivingEntity solLivingEntity, boolean excludeMainHand);

	List<ISoliniaItem> getEquippedSoliniaItems(ISoliniaLivingEntity solLivingEntity);

	boolean isPetControllable();

	void setPetControllable(boolean isPetControllable);

	int getForcedMaxHp();

	void setForcedMaxHp(int forcedMaxHp);

	void Spawn(Location location, int amount);

	int getNpcSpellList();

	void setNpcSpellList(int npcSpellList);

	boolean canDualWield();

	boolean isPlant();

	void setPlant(boolean isPlant);

	int getChanceToRespawnOnDeath();

	void setChanceToRespawnOnDeath(int chanceToRespawnOnDeath);

	boolean isTeleportAttack();

	void setTeleportAttack(boolean teleportAttack);

	String getTeleportAttackLocation();

	void setTeleportAttackLocation(String teleportAttackLocation);

	boolean isCorePet();

	long getTimefrom();

	void setTimefrom(long timefrom);

	long getTimeto();

	void setTimeto(long timeto);

	ISoliniaRace getRace();

	boolean isMounted();

	void setMounted(boolean isMounted);

	boolean isSocial();

	void setSocial(boolean isSocial);

	FactionStandingType checkNPCFactionAlly(int factionid);

	boolean canDisarm();

	int getNPCDefaultAtk();

	int getBaseDamage();
	int getMinDamage();

	boolean isBanker();

	void setBanker(boolean isBanker);

	SpellResistType getPetElementalTypeId();

	int getNPCHPRegen();
	int getNPCMPRegen();
	int getNPCMana();

	int getMaxDamage();

	int getPR();

	int getDR();

	int getMR();

	int getCR();

	int getFR();

	int getNPCDefaultAccuracyRating();

	int getMinInternalDmg();

	void setMinInternalDmg(int minDmg);

	int getMaxInternalDmg();

	void setMaxInternalDmg(int maxDmg);

	void setMagicresist(int magicresist);

	void setPoisonresist(int poisonresist);

	void setColdresist(int coldresist);

	void setDiseaseresist(int diseaseresist);

	void setFireresist(int fireresist);

	void setAvoidanceRating(int avoidanceRating);

	void setAccuracyRating(int accuracyRating);

	void setAtk(int atk);

	int getAtk();

	void setHpRegenRate(int hpRegenRate);

	void setManaRegenRate(int manaRegenRate);

	void setMana(int mana);

	int getInternalFireresist();

	int getInternalDiseaseresist();

	int getInternalColdresist();

	int getInternalPoisonresist();

	int getInternalMagicresist();

	boolean isCanSeeInvis();

	void setCanSeeInvis(boolean canSeeInvis);

	boolean isEventUsable();

	void setEventUsable(boolean eventUsable);

	double getStatMaxHP(int stamina);

	int getDisguiseId();

	void setDisguiseId(int disguiseId);

	SoliniaDisguise getDisguise();

	boolean isRacialPet();

	void setRacialPet(boolean isRacialPet);

	boolean canAreaRampage();

	void setCanAreaRampage(boolean canAreaRampage);

	boolean canEnrage();

	void setCanEnrage(boolean canEnrage);

	boolean canFlurry();

	boolean canTriple();

	boolean canRampage();

	boolean canQuad();

	void setCanFlurry(boolean canFlurry);

	void setCanQuad(boolean canQuad);

	void setCanRampage(boolean canRampage);

	void setCanTriple(boolean canTriple);
}
