package com.solinia.solinia.Interfaces;

import java.sql.Timestamp;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidSpellSettingException;
import com.solinia.solinia.Models.SoliniaSpellClass;
import com.solinia.solinia.Models.SpellEffect;
import com.solinia.solinia.Models.SpellEffectType;
import com.solinia.solinia.Utils.SpellTargetType;

public interface ISoliniaSpell extends IPersistable {

	int getId();

	void setId(int id);

	String getName();

	void setName(String name);

	String getPlayer1();

	void setPlayer1(String player1);

	String getTeleportZone();

	void setTeleportZone(String teleportZone);

	String getYouCast();

	void setYouCast(String youCast);

	String getOtherCasts();

	void setOtherCasts(String otherCasts);

	String getCastOnYou();

	void setCastOnYou(String castOnYou);

	String getCastOnOther();

	void setCastOnOther(String castOnOther);

	String getSpellFades();

	void setSpellFades(String spellFades);

	Integer getRange();

	void setRange(Integer range);

	Integer getAoerange();

	void setAoerange(Integer aoerange);

	Integer getPushback();

	void setPushback(Integer pushback);

	Integer getPushup();

	void setPushup(Integer pushup);

	Integer getCastTime();

	void setCastTime(Integer castTime);

	Integer getRecoveryTime();

	void setRecoveryTime(Integer recoveryTime);

	Integer getRecastTime();

	void setRecastTime(Integer recastTime);

	Integer getBuffdurationformula();

	void setBuffdurationformula(Integer buffdurationformula);

	Integer getBuffduration();

	void setBuffduration(Integer buffduration);

	Integer getAEDuration();

	void setAEDuration(Integer aEDuration);

	Integer getMana();

	void setMana(Integer mana);

	Integer getEffectBaseValue1();

	void setEffectBaseValue1(Integer effectBaseValue1);

	Integer getEffectBaseValue2();

	void setEffectBaseValue2(Integer effectBaseValue2);

	Integer getEffectBaseValue3();

	void setEffectBaseValue3(Integer effectBaseValue3);

	Integer getEffectBaseValue4();

	void setEffectBaseValue4(Integer effectBaseValue4);

	Integer getEffectBaseValue5();

	void setEffectBaseValue5(Integer effectBaseValue5);

	Integer getEffectBaseValue6();

	void setEffectBaseValue6(Integer effectBaseValue6);

	Integer getEffectBaseValue7();

	void setEffectBaseValue7(Integer effectBaseValue7);

	Integer getEffectBaseValue8();

	void setEffectBaseValue8(Integer effectBaseValue8);

	Integer getEffectBaseValue9();

	void setEffectBaseValue9(Integer effectBaseValue9);

	Integer getEffectBaseValue10();

	void setEffectBaseValue10(Integer effectBaseValue10);

	Integer getEffectBaseValue11();

	void setEffectBaseValue11(Integer effectBaseValue11);

	Integer getEffectBaseValue12();

	void setEffectBaseValue12(Integer effectBaseValue12);

	Integer getEffectLimitValue1();

	void setEffectLimitValue1(Integer effectLimitValue1);

	Integer getEffectLimitValue2();

	void setEffectLimitValue2(Integer effectLimitValue2);

	Integer getEffectLimitValue3();

	void setEffectLimitValue3(Integer effectLimitValue3);

	Integer getEffectLimitValue4();

	void setEffectLimitValue4(Integer effectLimitValue4);

	Integer getEffectLimitValue5();

	void setEffectLimitValue5(Integer effectLimitValue5);

	Integer getEffectLimitValue6();

	void setEffectLimitValue6(Integer effectLimitValue6);

	Integer getEffectLimitValue7();

	void setEffectLimitValue7(Integer effectLimitValue7);

	Integer getEffectLimitValue8();

	void setEffectLimitValue8(Integer effectLimitValue8);

	Integer getEffectLimitValue9();

	void setEffectLimitValue9(Integer effectLimitValue9);

	Integer getEffectLimitValue10();

	void setEffectLimitValue10(Integer effectLimitValue10);

	Integer getEffectLimitValue11();

	void setEffectLimitValue11(Integer effectLimitValue11);

	Integer getEffectLimitValue12();

	void setEffectLimitValue12(Integer effectLimitValue12);

	Integer getMax1();

	void setMax1(Integer max1);

	Integer getMax2();

	void setMax2(Integer max2);

	Integer getMax3();

	void setMax3(Integer max3);

	Integer getMax4();

	void setMax4(Integer max4);

	Integer getMax5();

	void setMax5(Integer max5);

	Integer getMax6();

	void setMax6(Integer max6);

	Integer getMax7();

	void setMax7(Integer max7);

	Integer getMax8();

	void setMax8(Integer max8);

	Integer getMax9();

	void setMax9(Integer max9);

	Integer getMax10();

	void setMax10(Integer max10);

	Integer getMax11();

	void setMax11(Integer max11);

	Integer getMax12();

	void setMax12(Integer max12);

	Integer getIcon();

	void setIcon(Integer icon);

	Integer getMemicon();

	void setMemicon(Integer memicon);

	Integer getComponents1();

	void setComponents1(Integer components1);

	Integer getComponents2();

	void setComponents2(Integer components2);

	Integer getComponents3();

	void setComponents3(Integer components3);

	Integer getComponents4();

	void setComponents4(Integer components4);

	Integer getComponentCounts1();

	void setComponentCounts1(Integer componentCounts1);

	Integer getComponentCounts2();

	void setComponentCounts2(Integer componentCounts2);

	Integer getComponentCounts3();

	void setComponentCounts3(Integer componentCounts3);

	Integer getComponentCounts4();

	void setComponentCounts4(Integer componentCounts4);

	Integer getNoexpendReagent1();

	void setNoexpendReagent1(Integer noexpendReagent1);

	Integer getNoexpendReagent2();

	void setNoexpendReagent2(Integer noexpendReagent2);

	Integer getNoexpendReagent3();

	void setNoexpendReagent3(Integer noexpendReagent3);

	Integer getNoexpendReagent4();

	void setNoexpendReagent4(Integer noexpendReagent4);

	Integer getFormula1();

	void setFormula1(Integer formula1);

	Integer getFormula2();

	void setFormula2(Integer formula2);

	Integer getFormula3();

	void setFormula3(Integer formula3);

	Integer getFormula4();

	void setFormula4(Integer formula4);

	Integer getFormula5();

	void setFormula5(Integer formula5);

	Integer getFormula6();

	void setFormula6(Integer formula6);

	Integer getFormula7();

	void setFormula7(Integer formula7);

	Integer getFormula8();

	void setFormula8(Integer formula8);

	Integer getFormula9();

	void setFormula9(Integer formula9);

	Integer getFormula10();

	void setFormula10(Integer formula10);

	Integer getFormula11();

	void setFormula11(Integer formula11);

	Integer getFormula12();

	void setFormula12(Integer formula12);

	Integer getLightType();

	void setLightType(Integer lightType);

	Integer getGoodEffect();

	void setGoodEffect(Integer goodEffect);

	Integer getActivated();

	void setActivated(Integer activated);

	Integer getResisttype();

	void setResisttype(Integer resisttype);

	Integer getEffectid1();

	void setEffectid1(Integer effectid1);

	Integer getEffectid2();

	void setEffectid2(Integer effectid2);

	Integer getEffectid3();

	void setEffectid3(Integer effectid3);

	Integer getEffectid4();

	void setEffectid4(Integer effectid4);

	Integer getEffectid5();

	void setEffectid5(Integer effectid5);

	Integer getEffectid6();

	void setEffectid6(Integer effectid6);

	Integer getEffectid7();

	void setEffectid7(Integer effectid7);

	Integer getEffectid8();

	void setEffectid8(Integer effectid8);

	Integer getEffectid9();

	void setEffectid9(Integer effectid9);

	Integer getEffectid10();

	void setEffectid10(Integer effectid10);

	Integer getEffectid11();

	void setEffectid11(Integer effectid11);

	Integer getEffectid12();

	void setEffectid12(Integer effectid12);

	Integer getTargettype();

	void setTargettype(Integer targettype);

	Integer getBasediff();

	void setBasediff(Integer basediff);

	Integer getSkill();

	void setSkill(Integer skill);

	Integer getZonetype();

	void setZonetype(Integer zonetype);

	Integer getEnvironmentType();

	void setEnvironmentType(Integer environmentType);

	Integer getTimeOfDay();

	void setTimeOfDay(Integer timeOfDay);

	Integer getClasses1();

	void setClasses1(Integer classes1);

	Integer getClasses2();

	void setClasses2(Integer classes2);

	Integer getClasses3();

	void setClasses3(Integer classes3);

	Integer getClasses4();

	void setClasses4(Integer classes4);

	Integer getClasses5();

	void setClasses5(Integer classes5);

	Integer getClasses6();

	void setClasses6(Integer classes6);

	Integer getClasses7();

	void setClasses7(Integer classes7);

	Integer getClasses8();

	void setClasses8(Integer classes8);

	Integer getClasses9();

	void setClasses9(Integer classes9);

	Integer getClasses10();

	void setClasses10(Integer classes10);

	Integer getClasses11();

	void setClasses11(Integer classes11);

	Integer getClasses12();

	void setClasses12(Integer classes12);

	Integer getClasses13();

	void setClasses13(Integer classes13);

	Integer getClasses14();

	void setClasses14(Integer classes14);

	Integer getClasses15();

	void setClasses15(Integer classes15);

	Integer getClasses16();

	void setClasses16(Integer classes16);

	Integer getCastingAnim();

	void setCastingAnim(Integer castingAnim);

	Integer getTargetAnim();

	void setTargetAnim(Integer targetAnim);

	Integer getTravelType();

	void setTravelType(Integer travelType);

	Integer getSpellAffectIndex();

	void setSpellAffectIndex(Integer spellAffectIndex);

	Integer getDisallowSit();

	void setDisallowSit(Integer disallowSit);

	Integer getDeities0();

	void setDeities0(Integer deities0);

	Integer getDeities1();

	void setDeities1(Integer deities1);

	Integer getDeities2();

	void setDeities2(Integer deities2);

	Integer getDeities3();

	void setDeities3(Integer deities3);

	Integer getDeities4();

	void setDeities4(Integer deities4);

	Integer getDeities5();

	void setDeities5(Integer deities5);

	Integer getDeities6();

	void setDeities6(Integer deities6);

	Integer getDeities7();

	void setDeities7(Integer deities7);

	Integer getDeities8();

	void setDeities8(Integer deities8);

	Integer getDeities9();

	void setDeities9(Integer deities9);

	Integer getDeities10();

	void setDeities10(Integer deities10);

	Integer getDeities11();

	void setDeities11(Integer deities11);

	Integer getDeities12();

	void setDeities12(Integer deities12);

	Integer getDeities13();

	void setDeities13(Integer deities13);

	Integer getDeities14();

	void setDeities14(Integer deities14);

	Integer getDeities15();

	void setDeities15(Integer deities15);

	Integer getDeities16();

	void setDeities16(Integer deities16);

	Integer getField142();

	void setField142(Integer field142);

	Integer getField143();

	void setField143(Integer field143);

	Integer getNewIcon();

	void setNewIcon(Integer newIcon);

	Integer getSpellanim();

	void setSpellanim(Integer spellanim);

	Integer getUninterruptable();

	void setUninterruptable(Integer uninterruptable);

	Integer getResistDiff();

	void setResistDiff(Integer resistDiff);

	Integer getDotStackingExempt();

	void setDotStackingExempt(Integer dotStackingExempt);

	Integer getDeleteable();

	void setDeleteable(Integer deleteable);

	Integer getRecourseLink();

	void setRecourseLink(Integer recourseLink);

	Integer getNoPartialResist();

	void setNoPartialResist(Integer noPartialResist);

	Integer getField152();

	void setField152(Integer field152);

	Integer getField153();

	void setField153(Integer field153);

	Integer getShortBuffBox();

	void setShortBuffBox(Integer shortBuffBox);

	Integer getDescnum();

	void setDescnum(Integer descnum);

	Integer getTypedescnum();

	void setTypedescnum(Integer typedescnum);

	Integer getEffectdescnum();

	void setEffectdescnum(Integer effectdescnum);

	Integer getEffectdescnum2();

	void setEffectdescnum2(Integer effectdescnum2);

	Integer getNpcNoLos();

	void setNpcNoLos(Integer npcNoLos);

	Integer getField160();

	void setField160(Integer field160);

	Integer getReflectable();

	void setReflectable(Integer reflectable);

	Integer getBonushate();

	void setBonushate(Integer bonushate);

	Integer getField163();

	void setField163(Integer field163);

	Integer getField164();

	void setField164(Integer field164);

	Integer getLdonTrap();

	void setLdonTrap(Integer ldonTrap);

	Integer getEndurCost();

	void setEndurCost(Integer endurCost);

	Integer getEndurTimerIndex();

	void setEndurTimerIndex(Integer endurTimerIndex);

	Integer getIsDiscipline();

	void setIsDiscipline(Integer isDiscipline);

	Integer getField169();

	void setField169(Integer field169);

	Integer getField170();

	void setField170(Integer field170);

	Integer getField171();

	void setField171(Integer field171);

	Integer getField172();

	void setField172(Integer field172);

	Integer getHateAdded();

	void setHateAdded(Integer hateAdded);

	Integer getEndurUpkeep();

	void setEndurUpkeep(Integer endurUpkeep);

	Integer getNumhitstype();

	void setNumhitstype(Integer numhitstype);

	Integer getNumhits();

	void setNumhits(Integer numhits);

	Integer getPvpresistbase();

	void setPvpresistbase(Integer pvpresistbase);

	Integer getPvpresistcalc();

	void setPvpresistcalc(Integer pvpresistcalc);

	Integer getPvpresistcap();

	void setPvpresistcap(Integer pvpresistcap);

	Integer getSpellCategory();

	void setSpellCategory(Integer spellCategory);

	Integer getField181();

	void setField181(Integer field181);

	Integer getField182();

	void setField182(Integer field182);

	Integer getField183();

	void setField183(Integer field183);

	Integer getField184();

	void setField184(Integer field184);

	Integer getCanMgb();

	void setCanMgb(Integer canMgb);

	Integer getNodispell();

	void setNodispell(Integer nodispell);

	Integer getNpcCategory();

	void setNpcCategory(Integer npcCategory);

	Integer getNpcUsefulness();

	void setNpcUsefulness(Integer npcUsefulness);

	Integer getMinResist();

	void setMinResist(Integer minResist);

	Integer getMaxResist();

	void setMaxResist(Integer maxResist);

	Integer getViralTargets();

	void setViralTargets(Integer viralTargets);

	Integer getViralTimer();

	void setViralTimer(Integer viralTimer);

	Integer getNimbuseffect();

	void setNimbuseffect(Integer nimbuseffect);

	Integer getConeStartAngle();

	void setConeStartAngle(Integer coneStartAngle);

	Integer getConeStopAngle();

	void setConeStopAngle(Integer coneStopAngle);

	Integer getSneaking();

	void setSneaking(Integer sneaking);

	Integer getNotExtendable();

	void setNotExtendable(Integer notExtendable);

	Integer getField198();

	void setField198(Integer field198);

	Integer getField199();

	void setField199(Integer field199);

	Integer getSuspendable();

	void setSuspendable(Integer suspendable);

	Integer getViralRange();

	void setViralRange(Integer viralRange);

	Integer getSongcap();

	void setSongcap(Integer songcap);

	Integer getField203();

	void setField203(Integer field203);

	Integer getField204();

	void setField204(Integer field204);

	Integer getNoBlock();

	void setNoBlock(Integer noBlock);

	Integer getField206();

	void setField206(Integer field206);

	Integer getSpellgroup();

	void setSpellgroup(Integer spellgroup);

	Integer getRank();

	void setRank(Integer rank);

	Integer getField209();

	void setField209(Integer field209);

	Integer getField210();

	void setField210(Integer field210);

	Integer getCastRestriction();

	void setCastRestriction(Integer castRestriction);

	Integer getAllowrest();

	void setAllowrest(Integer allowrest);

	Integer getInCombat();

	void setInCombat(Integer inCombat);

	Integer getOutofCombat();

	void setOutofCombat(Integer outofCombat);

	Integer getField215();

	void setField215(Integer field215);

	Integer getField216();

	void setField216(Integer field216);

	Integer getField217();

	void setField217(Integer field217);

	Integer getAemaxtargets();

	void setAemaxtargets(Integer aemaxtargets);

	Integer getMaxtargets();

	void setMaxtargets(Integer maxtargets);

	Integer getField220();

	void setField220(Integer field220);

	Integer getField221();

	void setField221(Integer field221);

	Integer getField222();

	void setField222(Integer field222);

	Integer getField223();

	void setField223(Integer field223);

	Integer getPersistdeath();

	void setPersistdeath(Integer persistdeath);

	Integer getField225();

	void setField225(Integer field225);

	Integer getField226();

	void setField226(Integer field226);

	Double getMinDist();

	void setMinDist(Double minDist);

	Double getMinDistMod();

	void setMinDistMod(Double minDistMod);

	Double getMaxDist();

	void setMaxDist(Double maxDist);

	Double getMaxDistMod();

	void setMaxDistMod(Double maxDistMod);

	Integer getMinRange();

	void setMinRange(Integer minRange);

	Integer getField232();

	void setField232(Integer field232);

	Integer getField233();

	void setField233(Integer field233);

	Integer getField234();

	void setField234(Integer field234);

	Integer getField235();

	void setField235(Integer field235);

	Integer getField236();

	void setField236(Integer field236);

	List<SoliniaSpellClass> getAllowedClasses();

	void setAllowedClasses(List<SoliniaSpellClass> allowedClasses);

	void sendSpellSettingsToSender(CommandSender sender);

	void editSetting(String setting, String value, String[] additional) throws InvalidSpellSettingException, NumberFormatException, CoreStateInitException;

	boolean tryApplyOnBlock(LivingEntity sourceEntity, Block clickedBlock, boolean sendMessages);

	SpellEffectType getEffectType1();

	SpellEffectType getEffectType2();

	SpellEffectType getEffectType3();

	SpellEffectType getEffectType4();

	SpellEffectType getEffectType5();

	SpellEffectType getEffectType6();

	SpellEffectType getEffectType7();

	SpellEffectType getEffectType8();

	SpellEffectType getEffectType9();

	SpellEffectType getEffectType10();

	SpellEffectType getEffectType11();

	SpellEffectType getEffectType12();

	boolean isBuffSpell();

	boolean isDamageSpell();

	List<SpellEffect> getBaseSpellEffects();

	List<SpellEffectType> getSpellEffectTypes();

	boolean isAASpell();

	boolean isBeneficial();

	boolean isLifetapSpell();

	boolean isDamageShield();

	boolean isDot();

	boolean isGroupSpell();

	boolean isEffectInSpell(SpellEffectType effecttype);

	boolean tryApplyOnEntity(LivingEntity sourceEntity, LivingEntity targetentity, boolean sendMessages, String requiredWeaponSkillType);

	boolean isWeaponProc();

	int calcSpellEffectValueFormula(SpellEffect spellEffect, LivingEntity sourceEntity, LivingEntity targetEntity,
			int sourceLevel, int ticksLeft);

	int calcSpellEffectValue(SpellEffect spellEffect, LivingEntity sourceEntity, LivingEntity targetEntity,
			int sourceLevel, int ticksleft, int instrument_mod);

	boolean isBardSong();
	
	boolean isCure();

	boolean isNuke();

	boolean isResistable();

	boolean isResistDebuffSpell();

	boolean isDetrimental();

	boolean isInvisSpell();

	int getMinLevelClass(String name);

	int getActSpellCost(ISoliniaLivingEntity solEntity);

	List<SoliniaSpellClass> getSoliniaSpellClassesFromClassesData();

	List<SoliniaSpellClass> getSoliniaSpellClassesFromClassesAAData();

	int getAARecastTime(ISoliniaPlayer solPlayer);

	Integer getNotFocusable();

	boolean isSacrificeSpell();

	boolean isCombatSkill();

	boolean isRangedProc();

	int getActSpellDuration(ISoliniaLivingEntity solEntity, int duration);

	boolean isBossApplyable();

	boolean isRaidApplyable();

	String getRequiresPermissionNode();

	void setRequiresPermissionNode(String requiresPermissionNode);

	boolean isCharmSpell();

	boolean tryCast(LivingEntity sourcemob, LivingEntity targetmob, boolean consumeMana, boolean consumeReagents, String requiredWeaponSkillType);

	boolean isEffectIgnoredInStacking(int spellEffectId);

	boolean isStackableDot();

	int calcBuffDuration(ISoliniaLivingEntity source, ISoliniaLivingEntity target);

	Timestamp getLastUpdatedTime();

	void setLastUpdatedTime(Timestamp lastUpdatedTime);

	void setLastUpdatedTimeNow();

	String getShortDescription();

	boolean tryApplyOnEntity(LivingEntity sourceEntity, LivingEntity targetentity, boolean sendMessages,
			String requiredWeaponSkillType, boolean racialPassive);

	boolean isLichSpell();

	boolean isSelfConversionSpell();

	SpellTargetType getSpellTargetType();

	int getSpellEffectBase(SpellEffectType spellEffectType);
	int getSpellEffectBase2(SpellEffectType spellEffectType);

	SpellEffect getSpellEffect(SpellEffectType spellEffectType);

	int calcBuffDurationFormula(int level, int formula, int duration);

	SpellEffectType getEffect(int effect_index);

	boolean checkSpellCategory(int base1, SpellEffectType effectType);

	boolean isSummonPet();

	boolean isSummonSkeleton();

	boolean isRoot();

	boolean isIgnoreSummonItemTemporaryCheck();

	void setIgnoreSummonItemTemporaryCheck(boolean ignoreSummonItemTemporaryCheck);

	boolean isCanBeMemorised();

	void setCanBeMemorised(boolean canBeMemorised);

}