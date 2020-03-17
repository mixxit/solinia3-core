package com.solinia.solinia.Interfaces;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.solinia.solinia.Models.CastingSpell;
import com.solinia.solinia.Models.Effects;
import com.solinia.solinia.Models.EquipSlots;
import com.solinia.solinia.Models.EquipmentSlot;
import com.solinia.solinia.Models.Fellowship;
import com.solinia.solinia.Models.MemorisedSpells;
import com.solinia.solinia.Models.Oath;
import com.solinia.solinia.Models.PacketCastingPercent;
import com.solinia.solinia.Models.Personality;
import com.solinia.solinia.Models.PlayerFactionEntry;
import com.solinia.solinia.Models.PlayerQuest;
import com.solinia.solinia.Models.SkillType;
import com.solinia.solinia.Models.SoliniaAARankEffect;
import com.solinia.solinia.Models.SoliniaAccountClaim;
import com.solinia.solinia.Models.SoliniaPlayerSkill;
import com.solinia.solinia.Models.SoliniaReagent;
import com.solinia.solinia.Models.SoliniaWorld;
import com.solinia.solinia.Models.SoliniaZone;
import com.solinia.solinia.Models.SpellResistType;
import com.solinia.solinia.Models.SpellbookPage;
import com.solinia.solinia.Models.TrackingChoice;

public interface ISoliniaPlayer extends Serializable {
	public UUID getUUID();

	void setUUID(UUID uuid);

	public String getForename();

	public ISoliniaLivingEntity getSoliniaLivingEntity();
	
	void setForenameAndLastName(String forename, String lastname);
	
	void setForename(String forename);

	public String getLastname();

	void setLastname(String lastname);

	public void updateDisplayName();
	
	public void updateMaxHp();

	public String getFullName();

	public int getMana();

	void setMana(int mana);

	public Double getAAExperience();

	void setAAExperience(Double aaexperience);

	public Double getExperience();

	void setExperience(Double experience);

	public int getLevel();

	public int getRaceId();

	public boolean hasChosenRace();

	void setChosenRace(boolean chosen);

	void setRaceId(int raceid);

	public ISoliniaRace getRace();

	public boolean hasChosenClass();

	public int getClassId();

	public ISoliniaClass getClassObj();
	
	void setClassId(int classid);

	void setChosenClass(boolean haschosenclass);

	Player getBukkitPlayer();

	void increasePlayerExperience(Double experience, boolean applyModifiers, boolean ignoreIfExperienceOf);

	void increasePlayerNormalExperience(Double experience, boolean applyModifiers, boolean ignoreIfExperienceOf);
	
	void increasePlayerAAExperience(Double experience, boolean applyModifiers);

	public void giveMoney(int i);

	int getAAPoints();

	void setAAPoints(int aapoints);

	public int getSkillCap(SkillType skillType);

	public List<SoliniaPlayerSkill> getSkills();

	String getLanguage();

	void setLanguage(String language);

	String getGender();

	void setGender(String gender);

	public void say(String string);

	public SoliniaPlayerSkill getSkill(SkillType skillType);

	public void tryIncreaseSkill(SkillType skillType, int xp);
	
	public void setSkill(SkillType skillType, int value);

	void reducePlayerMana(int mana);

	void increasePlayerMana(int mana);

	public void ooc(String message);

	public boolean understandsLanguage(String language);

	public void tryImproveLanguage(String language);

	public ISoliniaGroup getGroup();

	public int getResist(SpellResistType type);

	int getTotalResist(SpellResistType type);

	int getAapct();

	void setAapct(int aapct);

	public List<ISoliniaAARank> getBuyableAARanks();

	boolean canPurchaseAlternateAdvancementRank(ISoliniaAAAbility ability, ISoliniaAARank rank);

	boolean hasRank(ISoliniaAARank rank);

	boolean hasPreviousRanks(ISoliniaAAAbility ability, ISoliniaAARank rank);

	boolean canUseAlternateAdvancementRank(ISoliniaAAAbility ability, ISoliniaAARank rank);

	void purchaseAlternateAdvancementRank(ISoliniaAAAbility ability, ISoliniaAARank rank);

	boolean hasPrerequisites(ISoliniaAAAbility ability, ISoliniaAARank rank);

	boolean hasAAAbility(int abilityid);

	void interact(PlayerInteractEvent event);

	public boolean canDodge();

	public boolean canRiposte();

	public boolean canDoubleAttack();

	boolean getDoubleAttackCheck();

	boolean getDodgeCheck();

	boolean getRiposteCheck();

	public boolean getSafefallCheck();

	List<PlayerFactionEntry> getFactionEntries();

	void setFactionEntries(List<PlayerFactionEntry> factionEntries);

	public void decreaseFactionStanding(int factionid, int i);

	PlayerFactionEntry getFactionEntry(int factionId);

	PlayerFactionEntry createPlayerFactionEntry(int factionId);

	void ignorePlayer(Player player);

	List<UUID> getIgnoredPlayers();

	void setIgnoredPlayers(List<UUID> ignoredPlayers);

	boolean hasIgnored(UUID uuid);

	List<String> getAvailableTitles();

	void setAvailableTitles(List<String> availableTitles);

	String getTitle();

	void setTitle(String title);

	String getFullNameWithTitle();

	boolean grantTitle(String title);

	void increaseFactionStanding(int factionId, int amount);

	boolean isMezzed();
	
	List<PlayerQuest> getPlayerQuests();

	void setPlayerQuests(List<PlayerQuest> playerQuests);

	public void addPlayerQuest(int questId);

	public List<String> getPlayerQuestFlags();

	public void addPlayerQuestFlag(String questFlag);

	void setPlayerQuestFlags(List<String> playerQuestFlags);
	
	List<SoliniaAARankEffect> getRanksEffectsOfEffectType(int effectId);

	public boolean isMeditating();

	public void setSkills(List<SoliniaPlayerSkill> skillCache);

	boolean checkDoesntFizzle(ISoliniaSpell spell);

	void dropResurrectionItem(int experienceamount);

	void reducePlayerNormalExperience(Double experience);

	String getSpecialisation();

	void setSpecialisation(String specialisation);

	boolean isVampire();

	void setVampire(boolean vampire);

	List<ISoliniaAARank> getAARanks();

	UUID getCharacterId();

	void setCharacterId(UUID characterId);

	int getInspiration();

	void setInspiration(int inspiration);

	void setExperienceBonusExpires(Timestamp experienceBonusExpires);

	Timestamp getExperienceBonusExpires();

	public void grantExperienceBonusFromItem();

	public List<SoliniaAccountClaim> getAccountClaims();

	boolean isOocEnabled();

	void setOocEnabled(boolean oocEnabled);

	public void setBindPoint(String teleportlocation);
	public String getBindPoint();

	public void removeAllEntityEffects(Plugin plugin);

	public void killAllPets();

	boolean isInHotzone();

	List<ISoliniaItem> getEquippedSoliniaItems(boolean excludeMainHand);

	List<ISoliniaItem> getEquippedSoliniaItems();

	int getFingersItem();

	void setFingersItem(int fingersItem);

	int getShouldersItem();

	void setShouldersItem(int earsItem);
	
	int getEarsItem();

	void setEarsItem(int earsItem);

	int getNeckItem();

	void setNeckItem(int neckItem);

	void clearAAs();

	boolean isInZone(SoliniaZone zone);
	
	SoliniaWorld getSoliniaWorld();

	ConcurrentHashMap<Integer, SoliniaReagent> getReagents();

	void setReagents(ConcurrentHashMap<Integer, SoliniaReagent> reagents);

	UUID getMotherId();

	void setMotherId(UUID motherId);

	UUID getSpouseId();

	void setSpouseId(UUID spouseId);

	public void sendFamilyTree();

	public boolean hasSufficientReagents(Integer components1, Integer componentCounts1);

	public void reduceReagents(Integer components1, Integer componentCounts1);

	boolean canUseAASpell(ISoliniaSpell spell);

	public void scheduleUpdateMaxHp();

	List<Integer> getSpellBookItems();
	List<Integer> getSpellBookSpellIds();

	void setSpellBookItems(List<Integer> spellBookItems);

	List<ISoliniaAAAbility> getAAAbilitiesWithEffectType(int effectId);

	boolean isFeignedDeath();

	void setFeigned(boolean feigned);

	void castingComplete(CastingSpell castingSpell);

	void toggleAutoAttack();

	public boolean hasSufficientBandageReagents(int count);
	
	public boolean hasSufficientArrowReagents(int count);

	List<Integer> getArrowReagents();
	
	List<Integer> getBandageReagents();

	boolean bindWound(ISoliniaLivingEntity solLivingEntity);

	public void addXpToPendingXp(Double experience);

	Double getPendingXp();

	void setPendingXp(Double pendingXp);

	boolean isAAOn();

	boolean isStunned();

	Timestamp getLastLogin();

	int getForearmsItem();

	void setForearmsItem(int forearmsItem);

	int getArmsItem();

	void setArmsItem(int armsItem);

	int getHandsItem();

	void setHandsItem(int handsItem);

	public String getEarsItemInstance();

	public String getNeckItemInstance();

	public String getFingersItemInstance();

	public String getShouldersItemInstance();

	public String getForearmsItemInstance();

	public String getArmsItemInstance();

	public String getHandsItemInstance();

	void setFingersItemInstance(String fingersItemInstance);

	void setShouldersItemInstance(String shouldersItemInstance);

	void setNeckItemInstance(String neckItemInstance);

	void setEarsItemInstance(String earsItemInstance);

	void setForearmsItemInstance(String forearmsItemInstance);

	void setArmsItemInstance(String armsItemInstance);

	void setHandsItemInstance(String handsItemInstance);

	void setLastLocation(Location location);

	SoliniaWorld getWorld();

	int getOathId();

	void setOathId(int oathId);

	Personality getPersonality();

	void setPersonality(Personality personality);

	Oath getOath();

	public void StopSinging();

	boolean isSongsEnabled();

	void setSongsEnabled(boolean songsEnabled);

	void increaseMonthlyVote(Integer amount);

	Integer getMonthlyVote();

	int getWaistItem();

	void setWaistItem(int waistItem);

	String getWaistItemInstance();

	void setWaistItemInstance(String waistItemInstance);

	public void setEquipSlotItem(EquipmentSlot slot, int itemId);

	String getBase64InventoryContents();

	void setBase64InventoryContents(String base64InventoryContents);

	String getBase64ArmorContents();

	void setBase64ArmorContents(String base64ArmorContents);

	public void storeInventoryContents();

	public void storeArmorContents();

	ItemStack[] getStoredArmorContents();

	ItemStack[] getStoredInventoryContents();

	void tryApplyAugmentation(ISoliniaItem item);

	public void tryCastFromSpellbook(ISoliniaItem solItem);

	Location getLastLocation();


	void doCastSpell(ISoliniaSpell spell, Player player, boolean useMana, boolean useReagents,
			boolean ignoreProfessionAndLevel, String requiredWeaponSkillType);

	void startCasting(ISoliniaSpell spell, Player player, boolean useMana, boolean useReagents,
			boolean ignoreProfessionAndLevel,String requiredWeaponSkillType);

	void tryCastSpell(ISoliniaSpell spell, boolean useMana, boolean useReagents, boolean ignoreProfessionAndLevel, String requiredWeaponSkillType);

	SoliniaZone getFirstZone();

	public void doHPRegenTick();
	public void doMPRegenTick();

	public void doEquipmentRegenTick(List<ISoliniaItem> items);

	void whisper(String string);

	void shout(String string);

	public boolean isInGroup(LivingEntity targetentity);

	int getGodId();

	void setGodId(int godId);

	void setHasChosenGod(boolean hasChosenGod);

	boolean hasChosenGod();

	ISoliniaGod getGod();

	public boolean canUseSpell(ISoliniaSpell spell);

	int getMemorisedSpellSlot1();

	int getMemorisedSpellSlot2();

	void setMemorisedSpellSlot1(int memorisedSpellSlot1);

	void setMemorisedSpellSlot2(int memorisedSpellSlot2);

	int getMemorisedSpellSlot3();

	void setMemorisedSpellSlot3(int memorisedSpellSlot3);

	void setMemorisedSpellSlot4(int memorisedSpellSlot4);

	int getMemorisedSpellSlot4();

	int getMemorisedSpellSlot5();

	void setMemorisedSpellSlot5(int memorisedSpellSlot5);

	int getMemorisedSpellSlot6();

	int getMemorisedSpellSlot7();

	void setMemorisedSpellSlot6(int memorisedSpellSlot6);

	void setMemorisedSpellSlot7(int memorisedSpellSlot7);

	int getMemorisedSpellSlot8();

	void setMemorisedSpellSlot8(int memorisedSpellSlot8);

	public boolean memoriseSpell(int spellSlot, int spellId);

	public int getMaxSpellSlots();

	public boolean isSpellMemoriseSlotFree(int spellSlot);

	int getMemorisedSpellSlot(int spellSlot) throws IllegalArgumentException;

	void tryCastFromMemorySlot(int slotId);

	public SpellbookPage getSpellbookPage(int pageNo);

	MemorisedSpells getMemorisedSpellSlots();

	public void sendMemorisedSpellSlots();

	public double getCastingProgress();

	public PacketCastingPercent toPacketCastingPercent();

	public LivingEntity getEntityTarget();

	void setEntityTarget(LivingEntity target);

	void clearTargetsAgainstMe();

	public EquipSlots getEquipSlots();

	void sendSlotsAsPacket();

	Effects getEffects();

	void sendEffects();

	boolean isExperienceOn();

	void setExperienceOn(boolean experienceOn);

	int getLanguageLearnedPercent(String language);

	public void unMemoriseSpell(int abilityid);

	public boolean isForceNewAlt();

	void setForceNewAlt(boolean forceNewAlt);

	public boolean isPlayable();

	void tryThrowItemInMainHand(Cancellable cancellableEvent);

	boolean hasAaRanks();

	boolean isDeleted();

	void setDeleted(boolean deleted);

	boolean isShowDiscord();

	void setShowDiscord(boolean showDiscord);

	List<SoliniaZone> getZones();

	boolean canDisarm();

	void emote(String string, boolean isBardSongFilterable, boolean isManual);

	public void setModMessageEnabled(boolean enabled);

	public boolean isModMessageEnabled();

	public List<TrackingChoice> getTrackingChoices();

	public void startTracking(Location location);

	public boolean isTracking();

	Location getTrackingLocation();

	boolean isTrackingLocation();

	public void resetReverseAggro();

	public void fellowshipchat(String message);

	Fellowship getFellowship();

	int getCharacterFellowshipId();

	void setCharacterFellowshipId(int characterFellowshipId);

	public void grantFellowshipXPBonusToFellowship(Double experience);

	boolean hasReagents(ISoliniaSpell spell, Player player);

	boolean getSkillCheck(SkillType skillType, int trivial);



}
