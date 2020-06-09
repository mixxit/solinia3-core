package com.solinia.solinia.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaAAAbility;
import com.solinia.solinia.Interfaces.ISoliniaAARank;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.ActiveSpellEffect;
import com.solinia.solinia.Models.FocusEffect;
import com.solinia.solinia.Models.SoliniaAARankEffect;
import com.solinia.solinia.Models.SoliniaActiveSpell;
import com.solinia.solinia.Models.SoliniaEntitySpells;
import com.solinia.solinia.Models.SpellEffectIndex;
import com.solinia.solinia.Models.SpellEffectType;
import com.solinia.solinia.Models.SpellResistType;
import com.solinia.solinia.Models.SpellTargetType;
import com.solinia.solinia.Models.StatType;

import net.minecraft.server.v1_14_R1.Tuple;

public class SpellUtils {

	public static void playSpecialEffect(Entity entity, SoliniaActiveSpell activeSpell) {
		if (activeSpell.isRacialPassive())
			return;
		
		int sai = activeSpell.getSpell().getSpellAffectIndex();

		SpellResistType resistType = SpellUtils.getSpellResistType(activeSpell.getSpell().getResisttype());
		SpellEffectIndex effectType = SpellUtils.getSpellEffectIndex(sai);
		if (effectType == null) {
			SpecialEffectUtils.playLegacy(entity);
			return;
		}

		switch (effectType) {
		case Summon_Mount_Unclass:
			SpecialEffectUtils.playLegacy(entity);
			break;
		case Direct_Damage:
			switch(resistType)
			{
			case RESIST_FIRE:
			SpecialEffectUtils.playCustomStarEffect(entity, Particle.SPELL_MOB, Color.ORANGE);
			break;
			case RESIST_COLD:
			SpecialEffectUtils.playCustomStarEffect(entity, Particle.SPELL_MOB, Color.AQUA);
			break;
			case RESIST_DISEASE:
			SpecialEffectUtils.playCustomStarEffect(entity, Particle.SPELL_MOB, Color.BLACK);
			break;
			case RESIST_MAGIC:
			SpecialEffectUtils.playCustomStarEffect(entity, Particle.SPELL_MOB, Color.BLUE);
			break;
			case RESIST_POISON:
			SpecialEffectUtils.playCustomStarEffect(entity, Particle.SPELL_MOB, Color.LIME);
			break;
			default:
				SpecialEffectUtils.playCustomStarEffect(entity, Particle.SPELL_MOB, Color.RED);
				break;	
			}
			break;
		case Heal_Cure:
			SpecialEffectUtils.playLoveEffect(entity);
			break;
		case AC_Buff:
			SpecialEffectUtils.playCustomShieldEffect(entity, Particle.VILLAGER_HAPPY, Color.YELLOW);
			break;
		case AE_Damage:
			SpecialEffectUtils.playCustomStarEffect(entity, Particle.SPELL_MOB, Color.LIME);
			break;
		case Summon:
			SpecialEffectUtils.playLegacy(entity);
			break;
		case Sight:
			SpecialEffectUtils.playLegacy(entity);
			break;
		case Mana_Regen_Resist_Song:
			SpecialEffectUtils.playLegacy(entity);
			break;
		case Stat_Buff:
			SpecialEffectUtils.playShieldEffect(entity);
			break;
		case Vanish:
			SpecialEffectUtils.playPortalEffect(entity);
			break;
		case Appearance:
			SpecialEffectUtils.playSmokeEffect(entity);
			break;
		case Enchanter_Pet:
			SpecialEffectUtils.playSmokeEffect(entity);
			break;
		case Calm:
			SpecialEffectUtils.playLegacy(entity);
			break;
		case Fear:
			SpecialEffectUtils.playLegacy(entity);
			break;
		case Dispell_Sight:
			SpecialEffectUtils.playLegacy(entity);
			break;
		case Stun:
			SpecialEffectUtils.playCustomStarEffect(entity, Particle.SPELL_MOB, Color.FUCHSIA);
			break;
		case Haste_Runspeed:
			SpecialEffectUtils.playCustomVortexEffect(entity, Particle.ENCHANTMENT_TABLE, Color.BLUE);
			break;
		case Combat_Slow:
			SpecialEffectUtils.playLegacy(entity);
			break;
		case Damage_Shield:
			SpecialEffectUtils.playCustomVortexEffect(entity, Particle.ENCHANTMENT_TABLE, Color.ORANGE);
			break;
		case Cannibalize_Weapon_Proc:
			SpecialEffectUtils.playLegacy(entity);
			break;
		case Weaken:
			SpecialEffectUtils.playCustomConeEffect(entity, Particle.SPELL_MOB, Color.RED);
			break;
		case Banish:
			SpecialEffectUtils.playLegacy(entity);
			break;
		case Blind_Poison:
			if (activeSpell.getSpell() != null && activeSpell.getSpell().isEffectInSpell(SpellEffectType.Blind))
				SpecialEffectUtils.playCustomShieldEffect(entity, Particle.FLASH, Color.AQUA);
			else
				SpecialEffectUtils.playCustomFlameEffect(entity, Particle.NAUTILUS,null);
			break;
		case Cold_DD:
			SpecialEffectUtils.playCustomBallEffect(entity, Particle.BARRIER, Color.ORANGE);
			break;
		case Poison_Disease_DD:
			SpecialEffectUtils.playPoisonEffect(entity);
			break;
		case Fire_DD:
			SpecialEffectUtils.playFlameEffect(entity);
			break;
		case Memory_Blur:
			SpecialEffectUtils.playLegacy(entity);
			break;
		case Gravity_Fling:
			SpecialEffectUtils.playLegacy(entity);
			break;
		case Suffocate:
			SpecialEffectUtils.playLegacy(entity);
			break;
		case Lifetap_Over_Time:
			SpecialEffectUtils.playBleedEffect(entity);
			break;
		case Fire_AE:
			SpecialEffectUtils.playFlameEffect(entity);
			break;
		case Cold_AE:
			SpecialEffectUtils.playCustomBallEffect(entity, Particle.BARRIER, Color.ORANGE);
			break;
		case Poison_Disease_AE:
			SpecialEffectUtils.playPoisonEffect(entity);
			break;
		case Teleport:
			SpecialEffectUtils.playPortalEffect(entity);
			break;
		case Direct_Damage_Song:
			SpecialEffectUtils.playCustomMusicEffect(entity, Color.RED);
			break;
		case Combat_Buff_Song:
			SpecialEffectUtils.playCustomMusicEffect(entity, Color.LIME);
			break;
		case Calm_Song:
			SpecialEffectUtils.playCustomMusicEffect(entity, Color.BLUE);
			break;
		case Firework:
			SpecialEffectUtils.playLegacy(entity);
			break;
		case Firework_AE:
			SpecialEffectUtils.playLegacy(entity);
			break;
		case Weather_Rocket:
			SpecialEffectUtils.playLegacy(entity);
			break;
		case Convert_Vitals:
			SpecialEffectUtils.playLegacy(entity);
			break;
		case NPC_Special_60:
			SpecialEffectUtils.playLegacy(entity);
			break;
		case NPC_Special_61:
			SpecialEffectUtils.playLegacy(entity);
			break;
		case NPC_Special_62:
			SpecialEffectUtils.playLegacy(entity);
			break;
		case NPC_Special_63:
			SpecialEffectUtils.playLegacy(entity);
			break;
		case NPC_Special_70:
			SpecialEffectUtils.playLegacy(entity);
			break;
		case NPC_Special_71:
			SpecialEffectUtils.playLegacy(entity);
			break;
		case NPC_Special_80:
			SpecialEffectUtils.playLegacy(entity);
			break;
		case Trap_Lock:
			SpecialEffectUtils.playLegacy(entity);
			break;
		default:
			SpecialEffectUtils.playLegacy(entity);
		}
	}

	public static void AddPotionEffect(LivingEntity entity, PotionEffectType effectType, int amplifier) {
		entity.addPotionEffect(new PotionEffect(effectType, SpellUtils.GetPotionEffectTickLength(effectType), amplifier),
				true);
	}
	
	public static void RemovePotionEffect(LivingEntity entity, PotionEffectType effectType) {
		entity.removePotionEffect(effectType);
	}


	public static int GetPotionEffectTickLength(PotionEffectType effectType) {
		if (effectType == PotionEffectType.NIGHT_VISION) {
			return 30 * 20;
		}

		return 8 * 20;
	}
	public static boolean hasSpellActive(ISoliniaLivingEntity target, ISoliniaSpell spell) {
		for (SoliniaActiveSpell activeSpell : target.getActiveSpells()) {
			if (activeSpell.getSpell().getId() == spell.getId())
				return true;
			
			continue;
		}

		return false;
	}
	
	public static boolean isInvalidNpcSpell(ISoliniaSpell spell) {
		if (spell.getSpellEffectTypes().contains(SpellEffectType.Gate)
				|| spell.getSpellEffectTypes().contains(SpellEffectType.Teleport)
				|| spell.getSpellEffectTypes().contains(SpellEffectType.Teleport2)
				|| spell.getSpellEffectTypes().contains(SpellEffectType.TeleporttoAnchor)
				|| spell.getSpellEffectTypes().contains(SpellEffectType.Translocate)
				|| spell.getSpellEffectTypes().contains(SpellEffectType.TranslocatetoAnchor) || spell.isCharmSpell()
				|| spell.getSpellEffectTypes().contains(SpellEffectType.SummonItem)
				|| spell.getSpellEffectTypes().contains(SpellEffectType.BindAffinity)
				|| spell.getSpellEffectTypes().contains(SpellEffectType.Levitate)
				|| spell.getSpellEffectTypes().contains(SpellEffectType.FeignDeath)
				|| spell.getSpellEffectTypes().contains(SpellEffectType.ShadowStep)
				|| spell.getSpellEffectTypes().contains(SpellEffectType.ShadowStepDirectional)
				|| spell.getSpellEffectTypes().contains(SpellEffectType.Familiar)
				|| spell.getSpellEffectTypes().contains(SpellEffectType.SummonPet))
			return true;

		return false;
	}


	public static List<ISoliniaAARank> getHighestRanksForFocusEffect(ISoliniaPlayer soliniaPlayer,
			FocusEffect focusEffect) {
		List<ISoliniaAARank> ranks = new ArrayList<ISoliniaAARank>();
		if (focusEffect == FocusEffect.None)
			return ranks;
		
		for (ISoliniaAAAbility aaAbility : soliniaPlayer.getAAAbilities()) {
			int currentRank = 0;
			ISoliniaAARank highestRank = null;
			
			for (ISoliniaAARank rank : aaAbility.getRanks()) {
				if (!soliniaPlayer.hasRank(rank))
					continue;
				
				if (rank.getPosition() > currentRank) {
					currentRank = rank.getPosition();
					for (SoliniaAARankEffect rankEffect : rank.getEffects())
					{
						// we default to 0 (SE_CurrentHP) for the effect, so if there aren't any base1/2 values, we'll just skip it
						if (rankEffect.getEffectId() == 0 && rankEffect.getBase1() == 0 && rankEffect.getBase2() == 0)
							continue;

						SpellEffectType effect = SpellUtils.getSpellEffectType(rankEffect.getEffectId());
						
						// IsBlankSpellEffect()
						if (effect == SpellEffectType.Blank || (effect == SpellEffectType.CHA && rankEffect.getBase1() == 0) || effect == SpellEffectType.StackingCommand_Block ||
						    effect == SpellEffectType.StackingCommand_Overwrite)
							continue;

						if (IsFocusEffect(null,0,true,effect) != focusEffect)
							continue;
						
						highestRank = rank;
					}
				}
			}

			if (highestRank != null) {
				ranks.add(highestRank);
			}
		}
		return ranks;
	}

	public static FocusEffect IsFocusEffect(ISoliniaSpell spell,int effect_index, boolean AA,SpellEffectType aa_effect) {
		SpellEffectType effect = null;

		if (!AA)
			effect = spell.getEffect(effect_index);
		else
			effect = aa_effect;
		
		if (effect == null)
			return FocusEffect.None;

		switch (effect)
		{
		case ImprovedDamage:
			return FocusEffect.ImprovedDamage;
		case ImprovedDamage2:
			return FocusEffect.ImprovedDamage2;
		case ImprovedHeal:
			return FocusEffect.ImprovedHeal;
		case ReduceManaCost:
			return FocusEffect.ManaCost;
		case IncreaseSpellHaste:
			return FocusEffect.SpellHaste;
		case IncreaseSpellDuration:
			return FocusEffect.SpellDuration;
		case SpellDurationIncByTic:
			return FocusEffect.SpellDurByTic;
		case SwarmPetDuration:
			return FocusEffect.SwarmPetDuration;
		case IncreaseRange:
			return FocusEffect.Range;
		case ReduceReagentCost:
			return FocusEffect.ReagentCost;
		case PetPowerIncrease:
			return FocusEffect.PetPower;
		case SpellResistReduction:
			return FocusEffect.ResistRate;
		case SpellHateMod:
			return FocusEffect.SpellHateMod;
		case ReduceReuseTimer:
			return FocusEffect.ReduceRecastTime;
		case TriggerOnCast:
			//return focusTriggerOnCast;
			return FocusEffect.None; //This is calculated as an actual bonus
		case FcSpellVulnerability:
			return FocusEffect.SpellVulnerability;
		case BlockNextSpellFocus:
			//return focusBlockNextSpell;
			return FocusEffect.None; //This is calculated as an actual bonus
		case FcTwincast:
			return FocusEffect.Twincast;
		case SympatheticProc:
			return FocusEffect.SympatheticProc;
		case FcDamageAmt:
			return FocusEffect.FcDamageAmt;
		case FcDamageAmt2:
			return FocusEffect.FcDamageAmt2;
		case FcDamageAmtCrit:
			return FocusEffect.FcDamageAmtCrit;
		case FcDamagePctCrit:
			return FocusEffect.FcDamagePctCrit;
		case FcDamageAmtIncoming:
			return FocusEffect.FcDamageAmtIncoming;
		case FcHealAmtIncoming:
			return FocusEffect.FcHealAmtIncoming;
		case FcHealPctIncoming:
			return FocusEffect.FcHealPctIncoming;
		case FcBaseEffects:
			return FocusEffect.FcBaseEffects;
		case FcIncreaseNumHits:
			return FocusEffect.IncreaseNumHits;
		case FcLimitUse:
			return FocusEffect.FcLimitUse;
		case FcMute:
			return FocusEffect.FcMute;
		case FcTimerRefresh:
			return FocusEffect.FcTimerRefresh;
		case FcStunTimeMod:
			return FocusEffect.FcStunTimeMod;
		case FcHealPctCritIncoming:
			return FocusEffect.FcHealPctCritIncoming;
		case FcHealAmt:
			return FocusEffect.FcHealAmt;
		case FcHealAmtCrit:
			return FocusEffect.FcHealAmtCrit;
		default:
			return FocusEffect.None;
		}
	}

	public static List<SoliniaAARankEffect> getHighestRankEffectsForEffectId(ISoliniaPlayer soliniaPlayer,
			int effectId) {
		List<SoliniaAARankEffect> rankEffects = new ArrayList<SoliniaAARankEffect>();

		for (ISoliniaAAAbility aaAbility : soliniaPlayer.getAAAbilitiesWithEffectType(effectId)) {
			int currentRank = 0;
			SoliniaAARankEffect highestEffect = null;
			for (ISoliniaAARank rank : aaAbility.getRanks()) {
				if (soliniaPlayer.hasRank(rank))
					if (rank.getPosition() > currentRank) {
						currentRank = rank.getPosition();
						for (SoliniaAARankEffect effect : rank.getEffects())
							if (effect.getEffectId() == effectId)
								highestEffect = effect;
					}
			}

			if (highestEffect != null) {
				rankEffects.add(highestEffect);
			}
		}
		return rankEffects;
	}
	
	public static Tuple<Integer,Integer> getHighestAAEffectEffectTypeTuple(LivingEntity bukkitLivingEntity, SpellEffectType effectType) {
		return getHighestAAEffectEffectTypeTuple(bukkitLivingEntity, effectType, null);
	}

	public static Tuple<Integer,Integer> getHighestAAEffectEffectTypeTuple(LivingEntity bukkitLivingEntity, SpellEffectType effectType, Integer filterbase2) {
		if (!(bukkitLivingEntity instanceof Player))
			return new Tuple<Integer,Integer>(0,0);

		// This is actually read from the CriticalSpellChance
		boolean enforceSpellCritFormula = false;
		if (effectType.equals(SpellEffectType.SpellCritDmgIncrease)) {
			effectType = SpellEffectType.CriticalSpellChance;
			enforceSpellCritFormula = true;
		}		
		
		int highest = 0;
		int highest2 = 0;

		boolean firstRun = true;
		
		try {
			ISoliniaPlayer player = SoliniaPlayerAdapter.Adapt((Player) bukkitLivingEntity);
			for (SoliniaAARankEffect effect : player
					.getRanksEffectsOfEffectType(SpellUtils.getEffectIdFromEffectType(effectType),true)) 
			{
				if (filterbase2 != null && effect.getBase2() != filterbase2)
					continue;
				
				// Everything else
				if (enforceSpellCritFormula) {
					int base = 0;
					if (effect.getBase2() > 100)
						base = effect.getBase2() - 100;

					if (base > highest2 || firstRun)
					{
						highest = effect.getBase1();
						highest2 = base;
						firstRun = false;
					}
				} else {
					if (effect.getBase1() > highest || firstRun) {
						highest = effect.getBase1();
						highest2 = effect.getBase2();
						firstRun = false;
					}
				}
			}
			
			return new Tuple<Integer,Integer>(highest,highest2);

		} catch (CoreStateInitException e) {
			return new Tuple<Integer,Integer>(0,0);
		}

	}

	
	public static int getTotalAAEffectEffectType(LivingEntity bukkitLivingEntity, SpellEffectType effectType) {
		if (!(bukkitLivingEntity instanceof Player))
			return 0;

		try {
			ISoliniaPlayer player = SoliniaPlayerAdapter.Adapt((Player) bukkitLivingEntity);
			int total = 0;
			for (SoliniaAARankEffect effect : player
					.getRanksEffectsOfEffectType(SpellUtils.getEffectIdFromEffectType(effectType),true)) {
				total += effect.getBase1();
			}
			return total;
		} catch (CoreStateInitException e) {
			return 0;
		}

	}

	public static ISoliniaAARank getRankOfAAAbility(LivingEntity bukkitLivingEntity, ISoliniaAAAbility aa) {
		if (!(bukkitLivingEntity instanceof Player))
			return null;

		ISoliniaAARank foundRank = null;

		int position = 0;

		try {
			ISoliniaPlayer player = SoliniaPlayerAdapter.Adapt((Player) bukkitLivingEntity);
			for (ISoliniaAARank rank : player.getAARanks()) {
				if (aa.getId() != rank.getAbilityid())
					continue;

				if (rank.getPosition() > position) {
					position = rank.getPosition();
					foundRank = rank;
				}
			}
		} catch (CoreStateInitException e) {
			return null;
		}

		return foundRank;
	}

	public static int getRankPositionOfAAAbility(LivingEntity bukkitLivingEntity, ISoliniaAAAbility aa) {
		if (!(bukkitLivingEntity instanceof Player))
			return 0;

		int position = 0;

		try {
			ISoliniaPlayer player = SoliniaPlayerAdapter.Adapt((Player) bukkitLivingEntity);
			for (ISoliniaAARank rank : player.getAARanks()) {
				if (aa.getId() != rank.getAbilityid())
					continue;

				if (rank.getPosition() > position)
					position = rank.getPosition();
			}
		} catch (CoreStateInitException e) {
			return 0;
		}

		return position;
	}

	public static int getTotalAAEffectStat(LivingEntity bukkitLivingEntity, StatType stat) {
		if (!(bukkitLivingEntity instanceof Player))
			return 0;

		try {
			ISoliniaPlayer player = SoliniaPlayerAdapter.Adapt((Player) bukkitLivingEntity);
			int total = 0;

			int effectIdLookup = 0;

			switch (stat) {
			case Strength:
				effectIdLookup = SpellUtils.getEffectIdFromEffectType(SpellEffectType.STR);
				break;
			case Stamina:
				effectIdLookup = SpellUtils.getEffectIdFromEffectType(SpellEffectType.STA);
				break;
			case Agility:
				effectIdLookup = SpellUtils.getEffectIdFromEffectType(SpellEffectType.AGI);
				break;
			case Dexterity:
				effectIdLookup = SpellUtils.getEffectIdFromEffectType(SpellEffectType.DEX);
				break;
			case Intelligence:
				effectIdLookup = SpellUtils.getEffectIdFromEffectType(SpellEffectType.INT);
				break;
			case Wisdom:
				effectIdLookup = SpellUtils.getEffectIdFromEffectType(SpellEffectType.WIS);
				break;
			case Charisma:
				effectIdLookup = SpellUtils.getEffectIdFromEffectType(SpellEffectType.CHA);
				break;
			default:
				break;
			}

			if (effectIdLookup == 0)
				return 0;

			ConcurrentHashMap<Integer, Integer> abilityMaxValue = new ConcurrentHashMap<Integer, Integer>();

			for (SoliniaAARankEffect effect : player.getRanksEffectsOfEffectType(effectIdLookup,true)) {
				ISoliniaAARank rank = StateManager.getInstance().getConfigurationManager()
						.getAARankCache(effect.getRankId());

				if (rank != null)
					abilityMaxValue.put(rank.getAbilityid(), effect.getBase1());
			}

			for (Integer key : abilityMaxValue.keySet()) {
				total += abilityMaxValue.get(key);
			}

			return total;
		} catch (CoreStateInitException e) {
			return 0;
		}
	}

	public static double getTotalAAEffectMaxHp(LivingEntity bukkitLivingEntity) {
		if (!(bukkitLivingEntity instanceof Player))
			return 0;

		try {
			ISoliniaPlayer player = SoliniaPlayerAdapter.Adapt((Player) bukkitLivingEntity);
			int total = 0;

			int effectIdLookup = 0;
			effectIdLookup = SpellUtils.getEffectIdFromEffectType(SpellEffectType.MaxHPChange);

			if (effectIdLookup > 0) {
				for (SoliniaAARankEffect effect : player.getRanksEffectsOfEffectType(effectIdLookup,true)) {
					total += effect.getBase1();
				}
			}
			effectIdLookup = SpellUtils.getEffectIdFromEffectType(SpellEffectType.TotalHP);

			if (effectIdLookup > 0) {
				for (SoliniaAARankEffect effect : player.getRanksEffectsOfEffectType(effectIdLookup, true)) {
					total += effect.getBase1();
				}
			}

			return total;
		} catch (CoreStateInitException e) {
			return 0;
		}
	}

	
	public static double getTotalEffectTotalHP(LivingEntity livingEntity) {
		double allTotalHpEffects = 0;
		try {
			SoliniaEntitySpells effects = StateManager.getInstance().getEntityManager()
					.getActiveEntitySpells(livingEntity);

			for (SoliniaActiveSpell activeSpell : effects.getActiveSpells()) {
				for (ActiveSpellEffect effect : activeSpell.getActiveSpellEffects()) {
					if (!(effect.getSpellEffectType().equals(SpellEffectType.TotalHP)))
						continue;

					allTotalHpEffects += effect.getCalculatedValue();
				}
			}

		} catch (CoreStateInitException e) {
			return 0;
		}
		return allTotalHpEffects;
	}

	public static List<ActiveSpellEffect> getActiveSpellEffects(LivingEntity livingEntity, SpellEffectType effectType) {
		List<ActiveSpellEffect> returnEffects = new ArrayList<ActiveSpellEffect>();

		SoliniaEntitySpells effects;
		try {
			effects = StateManager.getInstance().getEntityManager().getActiveEntitySpells(livingEntity);

			for (SoliniaActiveSpell activeSpell : effects.getActiveSpells()) {
				for (ActiveSpellEffect effect : activeSpell.getActiveSpellEffects()) {
					if (!(effect.getSpellEffectType().equals(effectType)))
						continue;

					returnEffects.add(effect);
				}
			}
		} catch (CoreStateInitException e) {
			// skip
		}

		return returnEffects;
	}

	public static List<ActiveSpellEffect> getActiveSpellEffects(LivingEntity livingEntity,
			List<SpellEffectType> effectTypes) {
		List<ActiveSpellEffect> returnEffects = new ArrayList<ActiveSpellEffect>();

		SoliniaEntitySpells effects;
		try {
			effects = StateManager.getInstance().getEntityManager().getActiveEntitySpells(livingEntity);

			for (SoliniaActiveSpell activeSpell : effects.getActiveSpells()) {
				for (ActiveSpellEffect effect : activeSpell.getActiveSpellEffects()) {
					if (!(effectTypes.contains(effect.getSpellEffectType())))
						continue;

					returnEffects.add(effect);
				}
			}
		} catch (CoreStateInitException e) {
			// skip
		}

		return returnEffects;
	}

	public static int getActiveSpellEffectsRemainingValue(LivingEntity livingEntity, SpellEffectType effectType) {
		int totalRemaining = 0;

		SoliniaEntitySpells effects;
		try {
			effects = StateManager.getInstance().getEntityManager().getActiveEntitySpells(livingEntity);

			for (SoliniaActiveSpell activeSpell : effects.getActiveSpells()) {
				for (ActiveSpellEffect effect : activeSpell.getActiveSpellEffects()) {
					if (!(effect.getSpellEffectType().equals(effectType)))
						continue;

					totalRemaining += effect.getRemainingValue();
				}
			}
		} catch (CoreStateInitException e) {
			// skip
		}

		return totalRemaining;
	}

	public static int getTotalEffectStat(LivingEntity livingEntity, StatType stat) {
		int statTotal = 0;

		try {
			SoliniaEntitySpells effects = StateManager.getInstance().getEntityManager()
					.getActiveEntitySpells(livingEntity);

			for (SoliniaActiveSpell activeSpell : effects.getActiveSpells()) {
				for (ActiveSpellEffect effect : activeSpell.getActiveSpellEffects()) {
					if (!(effect.getSpellEffectType().equals(SpellEffectType.STR))
							&& !(effect.getSpellEffectType().equals(SpellEffectType.STA))
							&& !(effect.getSpellEffectType().equals(SpellEffectType.AGI))
							&& !(effect.getSpellEffectType().equals(SpellEffectType.DEX))
							&& !(effect.getSpellEffectType().equals(SpellEffectType.INT))
							&& !(effect.getSpellEffectType().equals(SpellEffectType.WIS))
							&& !(effect.getSpellEffectType().equals(SpellEffectType.CHA)))
						continue;

					switch (stat) {
					case Strength:
						if (!(effect.getSpellEffectType().equals(SpellEffectType.STR)))
							break;
						statTotal += effect.getCalculatedValue();
						break;
					case Stamina:
						if (!(effect.getSpellEffectType().equals(SpellEffectType.STA)))
							break;
						statTotal += effect.getCalculatedValue();
						break;
					case Agility:
						if (!(effect.getSpellEffectType().equals(SpellEffectType.AGI)))
							break;
						statTotal += effect.getCalculatedValue();
						break;
					case Dexterity:
						if (!(effect.getSpellEffectType().equals(SpellEffectType.DEX)))
							break;
						statTotal += effect.getCalculatedValue();
						break;
					case Intelligence:
						if (!(effect.getSpellEffectType().equals(SpellEffectType.INT)))
							break;
						statTotal += effect.getCalculatedValue();
						break;
					case Wisdom:
						if (!(effect.getSpellEffectType().equals(SpellEffectType.WIS)))
							break;
						statTotal += effect.getCalculatedValue();
						break;
					case Charisma:
						if (!(effect.getSpellEffectType().equals(SpellEffectType.CHA)))
							break;
						statTotal += effect.getCalculatedValue();
						break;
					default:
						break;
					}
				}
			}

		} catch (CoreStateInitException e) {
			return 0;
		}
		return statTotal;
	}

	
	public static SpellResistType getSpellResistType(Integer resisttype) {
		switch (resisttype) {
		case 0:
			return SpellResistType.RESIST_NONE;
		case 1:
			return SpellResistType.RESIST_MAGIC;
		case 2:
			return SpellResistType.RESIST_FIRE;
		case 3:
			return SpellResistType.RESIST_COLD;
		case 4:
			return SpellResistType.RESIST_POISON;
		case 5:
			return SpellResistType.RESIST_DISEASE;
		case 6:
			return SpellResistType.RESIST_CHROMATIC;
		case 7:
			return SpellResistType.RESIST_PRISMATIC;
		case 8:
			return SpellResistType.RESIST_PHYSICAL;
		case 9:
			return SpellResistType.RESIST_CORRUPTION;
		default:
			return SpellResistType.RESIST_NONE;
		}
	}

	// Graphical effects
	public static SpellEffectIndex getSpellEffectIndex(int sai) {
		switch (sai) {
		case -1:
			return SpellEffectIndex.Summon_Mount_Unclass;
		case 0:
			return SpellEffectIndex.Direct_Damage;
		case 1:
			return SpellEffectIndex.Heal_Cure;
		case 2:
			return SpellEffectIndex.AC_Buff;
		case 3:
			return SpellEffectIndex.AE_Damage;
		case 4:
			return SpellEffectIndex.Summon;
		case 5:
			return SpellEffectIndex.Sight;
		case 6:
			return SpellEffectIndex.Mana_Regen_Resist_Song;
		case 7:
			return SpellEffectIndex.Stat_Buff;
		case 9:
			return SpellEffectIndex.Vanish;
		case 10:
			return SpellEffectIndex.Appearance;
		case 11:
			return SpellEffectIndex.Enchanter_Pet;
		case 12:
			return SpellEffectIndex.Calm;
		case 13:
			return SpellEffectIndex.Fear;
		case 14:
			return SpellEffectIndex.Dispell_Sight;
		case 15:
			return SpellEffectIndex.Stun;
		case 16:
			return SpellEffectIndex.Haste_Runspeed;
		case 17:
			return SpellEffectIndex.Combat_Slow;
		case 18:
			return SpellEffectIndex.Damage_Shield;
		case 19:
			return SpellEffectIndex.Cannibalize_Weapon_Proc;
		case 20:
			return SpellEffectIndex.Weaken;
		case 21:
			return SpellEffectIndex.Banish;
		case 22:
			return SpellEffectIndex.Blind_Poison;
		case 23:
			return SpellEffectIndex.Cold_DD;
		case 24:
			return SpellEffectIndex.Poison_Disease_DD;
		case 25:
			return SpellEffectIndex.Fire_DD;
		case 27:
			return SpellEffectIndex.Memory_Blur;
		case 28:
			return SpellEffectIndex.Gravity_Fling;
		case 29:
			return SpellEffectIndex.Suffocate;
		case 30:
			return SpellEffectIndex.Lifetap_Over_Time;
		case 31:
			return SpellEffectIndex.Fire_AE;
		case 33:
			return SpellEffectIndex.Cold_AE;
		case 34:
			return SpellEffectIndex.Poison_Disease_AE;
		case 40:
			return SpellEffectIndex.Teleport;
		case 41:
			return SpellEffectIndex.Direct_Damage_Song;
		case 42:
			return SpellEffectIndex.Combat_Buff_Song;
		case 43:
			return SpellEffectIndex.Calm_Song;
		case 45:
			return SpellEffectIndex.Firework;
		case 46:
			return SpellEffectIndex.Firework_AE;
		case 47:
			return SpellEffectIndex.Weather_Rocket;
		case 50:
			return SpellEffectIndex.Convert_Vitals;
		case 60:
			return SpellEffectIndex.NPC_Special_60;
		case 61:
			return SpellEffectIndex.NPC_Special_61;
		case 62:
			return SpellEffectIndex.NPC_Special_62;
		case 63:
			return SpellEffectIndex.NPC_Special_63;
		case 70:
			return SpellEffectIndex.NPC_Special_70;
		case 71:
			return SpellEffectIndex.NPC_Special_71;
		case 80:
			return SpellEffectIndex.NPC_Special_80;
		case 88:
			return SpellEffectIndex.Trap_Lock;
		}

		return null;
	}

	
	public static int getEffectIdFromEffectType(SpellEffectType spellEffectType) {
		switch (spellEffectType) {
		case CurrentHP:
			return 0;
		case ArmorClass:
			return 1;
		case ATK:
			return 2;
		case MovementSpeed:
			return 3;
		case STR:
			return 4;
		case DEX:
			return 5;
		case AGI:
			return 6;
		case STA:
			return 7;
		case INT:
			return 8;
		case WIS:
			return 9;
		case CHA:
			return 10;
		case AttackSpeed:
			return 11;
		case Invisibility:
			return 12;
		case SeeInvis:
			return 13;
		case WaterBreathing:
			return 14;
		case CurrentMana:
			return 15;
		case NPCFrenzy:
			return 16;
		case NPCAwareness:
			return 17;
		case Lull:
			return 18;
		case AddFaction:
			return 19;
		case Blind:
			return 20;
		case Stun:
			return 21;
		case Charm:
			return 22;
		case Fear:
			return 23;
		case Stamina:
			return 24;
		case BindAffinity:
			return 25;
		case Gate:
			return 26;
		case CancelMagic:
			return 27;
		case InvisVsUndead:
			return 28;
		case InvisVsAnimals:
			return 29;
		case ChangeFrenzyRad:
			return 30;
		case Mez:
			return 31;
		case SummonItem:
			return 32;
		case SummonPet:
			return 33;
		case Confuse:
			return 34;
		case DiseaseCounter:
			return 35;
		case PoisonCounter:
			return 36;
		case DetectHostile:
			return 37;
		case DetectMagic:
			return 38;
		case DetectPoison:
			return 39;
		case DivineAura:
			return 40;
		case Destroy:
			return 41;
		case ShadowStep:
			return 42;
		case Berserk:
			return 43;
		case Lycanthropy:
			return 44;
		case Vampirism:
			return 45;
		case ResistFire:
			return 46;
		case ResistCold:
			return 47;
		case ResistPoison:
			return 48;
		case ResistDisease:
			return 49;
		case ResistMagic:
			return 50;
		case DetectTraps:
			return 51;
		case SenseDead:
			return 52;
		case SenseSummoned:
			return 53;
		case SenseAnimals:
			return 54;
		case Rune:
			return 55;
		case TrueNorth:
			return 56;
		case Levitate:
			return 57;
		case Illusion:
			return 58;
		case DamageShield:
			return 59;
		case TransferItem:
			return 60;
		case Identify:
			return 61;
		case ItemID:
			return 62;
		case WipeHateList:
			return 63;
		case SpinTarget:
			return 64;
		case InfraVision:
			return 65;
		case UltraVision:
			return 66;
		case EyeOfZomm:
			return 67;
		case ReclaimPet:
			return 68;
		case TotalHP:
			return 69;
		case CorpseBomb:
			return 70;
		case NecPet:
			return 71;
		case PreserveCorpse:
			return 72;
		case BindSight:
			return 73;
		case FeignDeath:
			return 74;
		case VoiceGraft:
			return 75;
		case Sentinel:
			return 76;
		case LocateCorpse:
			return 77;
		case AbsorbMagicAtt:
			return 78;
		case CurrentHPOnce:
			return 79;
		case EnchantLight:
			return 80;
		case Revive:
			return 81;
		case SummonPC:
			return 82;
		case Teleport:
			return 83;
		case TossUp:
			return 84;
		case WeaponProc:
			return 85;
		case Harmony:
			return 86;
		case MagnifyVision:
			return 87;
		case Succor:
			return 88;
		case ModelSize:
			return 89;
		case Cloak:
			return 90;
		case SummonCorpse:
			return 91;
		case InstantHate:
			return 92;
		case StopRain:
			return 93;
		case NegateIfCombat:
			return 94;
		case Sacrifice:
			return 95;
		case Silence:
			return 96;
		case ManaPool:
			return 97;
		case AttackSpeed2:
			return 98;
		case Root:
			return 99;
		case HealOverTime:
			return 100;
		case CompleteHeal:
			return 101;
		case Fearless:
			return 102;
		case CallPet:
			return 103;
		case Translocate:
			return 104;
		case AntiGate:
			return 105;
		case SummonBSTPet:
			return 106;
		case AlterNPCLevel:
			return 107;
		case Familiar:
			return 108;
		case SummonItemIntoBag:
			return 109;
		case IncreaseArchery:
			return 110;
		case ResistAll:
			return 111;
		case CastingLevel:
			return 112;
		case SummonHorse:
			return 113;
		case ChangeAggro:
			return 114;
		case Hunger:
			return 115;
		case CurseCounter:
			return 116;
		case MagicWeapon:
			return 117;
		case Amplification:
			return 118;
		case AttackSpeed3:
			return 119;
		case HealRate:
			return 120;
		case ReverseDS:
			return 121;
		case ReduceSkill:
			return 122;
		case Screech:
			return 123;
		case ImprovedDamage:
			return 124;
		case ImprovedHeal:
			return 125;
		case SpellResistReduction:
			return 126;
		case IncreaseSpellHaste:
			return 127;
		case IncreaseSpellDuration:
			return 128;
		case IncreaseRange:
			return 129;
		case SpellHateMod:
			return 130;
		case ReduceReagentCost:
			return 131;
		case ReduceManaCost:
			return 132;
		case FcStunTimeMod:
			return 133;
		case LimitMaxLevel:
			return 134;
		case LimitResist:
			return 135;
		case LimitTarget:
			return 136;
		case LimitEffect:
			return 137;
		case LimitSpellType:
			return 138;
		case LimitSpell:
			return 139;
		case LimitMinDur:
			return 140;
		case LimitInstant:
			return 141;
		case LimitMinLevel:
			return 142;
		case LimitCastTimeMin:
			return 143;
		case LimitCastTimeMax:
			return 144;
		case Teleport2:
			return 145;
		case ElectricityResist:
			return 146;
		case PercentalHeal:
			return 147;
		case StackingCommand_Block:
			return 148;
		case StackingCommand_Overwrite:
			return 149;
		case DeathSave:
			return 150;
		case SuspendPet:
			return 151;
		case TemporaryPets:
			return 152;
		case BalanceHP:
			return 153;
		case DispelDetrimental:
			return 154;
		case SpellCritDmgIncrease:
			return 155;
		case IllusionCopy:
			return 156;
		case SpellDamageShield:
			return 157;
		case Reflect:
			return 158;
		case AllStats:
			return 159;
		case MakeDrunk:
			return 160;
		case MitigateSpellDamage:
			return 161;
		case MitigateMeleeDamage:
			return 162;
		case NegateAttacks:
			return 163;
		case AppraiseLDonChest:
			return 164;
		case DisarmLDoNTrap:
			return 165;
		case UnlockLDoNChest:
			return 166;
		case PetPowerIncrease:
			return 167;
		case MeleeMitigation:
			return 168;
		case CriticalHitChance:
			return 169;
		case SpellCritChance:
			return 170;
		case CrippBlowChance:
			return 171;
		case AvoidMeleeChance:
			return 172;
		case RiposteChance:
			return 173;
		case DodgeChance:
			return 174;
		case ParryChance:
			return 175;
		case DualWieldChance:
			return 176;
		case DoubleAttackChance:
			return 177;
		case MeleeLifetap:
			return 178;
		case AllInstrumentMod:
			return 179;
		case ResistSpellChance:
			return 180;
		case ResistFearChance:
			return 181;
		case HundredHands:
			return 182;
		case MeleeSkillCheck:
			return 183;
		case HitChance:
			return 184;
		case DamageModifier:
			return 185;
		case MinDamageModifier:
			return 186;
		case BalanceMana:
			return 187;
		case IncreaseBlockChance:
			return 188;
		case CurrentEndurance:
			return 189;
		case EndurancePool:
			return 190;
		case Amnesia:
			return 191;
		case Hate:
			return 192;
		case SkillAttack:
			return 193;
		case FadingMemories:
			return 194;
		case StunResist:
			return 195;
		case StrikeThrough:
			return 196;
		case SkillDamageTaken:
			return 197;
		case CurrentEnduranceOnce:
			return 198;
		case Taunt:
			return 199;
		case ProcChance:
			return 200;
		case RangedProc:
			return 201;
		case IllusionOther:
			return 202;
		case MassGroupBuff:
			return 203;
		case GroupFearImmunity:
			return 204;
		case Rampage:
			return 205;
		case AETaunt:
			return 206;
		case FleshToBone:
			return 207;
		case PurgePoison:
			return 208;
		case DispelBeneficial:
			return 209;
		case PetShield:
			return 210;
		case AEMelee:
			return 211;
		case FrenziedDevastation:
			return 212;
		case PetMaxHP:
			return 213;
		case MaxHPChange:
			return 214;
		case PetAvoidance:
			return 215;
		case Accuracy:
			return 216;
		case HeadShot:
			return 217;
		case PetCriticalHit:
			return 218;
		case SlayUndead:
			return 219;
		case SkillDamageAmount:
			return 220;
		case Packrat:
			return 221;
		case BlockBehind:
			return 222;
		case DoubleRiposte:
			return 223;
		case GiveDoubleRiposte:
			return 224;
		case GiveDoubleAttack:
			return 225;
		case TwoHandBash:
			return 226;
		case ReduceSkillTimer:
			return 227;
		case ReduceFallDamage:
			return 228;
		case PersistantCasting:
			return 229;
		case ExtendedShielding:
			return 230;
		case StunBashChance:
			return 231;
		case DivineSave:
			return 232;
		case Metabolism:
			return 233;
		case ReduceApplyPoisonTime:
			return 234;
		case ChannelChanceSpells:
			return 235;
		case FreePet:
			return 236;
		case GivePetGroupTarget:
			return 237;
		case IllusionPersistence:
			return 238;
		case FeignedCastOnChance:
			return 239;
		case StringUnbreakable:
			return 240;
		case ImprovedReclaimEnergy:
			return 241;
		case IncreaseChanceMemwipe:
			return 242;
		case CharmBreakChance:
			return 243;
		case RootBreakChance:
			return 244;
		case TrapCircumvention:
			return 245;
		case SetBreathLevel:
			return 246;
		case RaiseSkillCap:
			return 247;
		case SecondaryForte:
			return 248;
		case SecondaryDmgInc:
			return 249;
		case SpellProcChance:
			return 250;
		case ConsumeProjectile:
			return 251;
		case FrontalBackstabChance:
			return 252;
		case FrontalBackstabMinDmg:
			return 253;
		case Blank:
			return 254;
		case ShieldDuration:
			return 255;
		case ShroudofStealth:
			return 256;
		case PetDiscipline:
			return 257;
		case TripleBackstab:
			return 258;
		case CombatStability:
			return 259;
		case AddSingingMod:
			return 260;
		case SongModCap:
			return 261;
		case RaiseStatCap:
			return 262;
		case TradeSkillMastery:
			return 263;
		case HastenedAASkill:
			return 264;
		case MasteryofPast:
			return 265;
		case ExtraAttackChance:
			return 266;
		case AddPetCommand:
			return 267;
		case ReduceTradeskillFail:
			return 268;
		case MaxBindWound:
			return 269;
		case BardSongRange:
			return 270;
		case BaseMovementSpeed:
			return 271;
		case CastingLevel2:
			return 272;
		case CriticalDoTChance:
			return 273;
		case CriticalHealChance:
			return 274;
		case CriticalMend:
			return 275;
		case Ambidexterity:
			return 276;
		case UnfailingDivinity:
			return 277;
		case FinishingBlow:
			return 278;
		case Flurry:
			return 279;
		case PetFlurry:
			return 280;
		case FeignedMinion:
			return 281;
		case ImprovedBindWound:
			return 282;
		case DoubleSpecialAttack:
			return 283;
		case LoHSetHeal:
			return 284;
		case NimbleEvasion:
			return 285;
		case FcDamageAmt:
			return 286;
		case SpellDurationIncByTic:
			return 287;
		case SkillAttackProc:
			return 288;
		case CastOnFadeEffect:
			return 289;
		case IncreaseRunSpeedCap:
			return 290;
		case Purify:
			return 291;
		case StrikeThrough2:
			return 292;
		case FrontalStunResist:
			return 293;
		case CriticalSpellChance:
			return 294;
		case ReduceTimerSpecial:
			return 295;
		case FcSpellVulnerability:
			return 296;
		case FcDamageAmtIncoming:
			return 297;
		case ChangeHeight:
			return 298;
		case WakeTheDead:
			return 299;
		case Doppelganger:
			return 300;
		case ArcheryDamageModifier:
			return 301;
		case FcDamagePctCrit:
			return 302;
		case FcDamageAmtCrit:
			return 303;
		case OffhandRiposteFail:
			return 304;
		case MitigateDamageShield:
			return 305;
		case ArmyOfTheDead:
			return 306;
		case Appraisal:
			return 307;
		case SuspendMinion:
			return 308;
		case GateCastersBindpoint:
			return 309;
		case ReduceReuseTimer:
			return 310;
		case LimitCombatSkills:
			return 311;
		case Sanctuary:
			return 312;
		case ForageAdditionalItems:
			return 313;
		case Invisibility2:
			return 314;
		case InvisVsUndead2:
			return 315;
		case ImprovedInvisAnimals:
			return 316;
		case ItemHPRegenCapIncrease:
			return 317;
		case ItemManaRegenCapIncrease:
			return 318;
		case CriticalHealOverTime:
			return 319;
		case ShieldBlock:
			return 320;
		case ReduceHate:
			return 321;
		case GateToHomeCity:
			return 322;
		case DefensiveProc:
			return 323;
		case HPToMana:
			return 324;
		case NoBreakAESneak:
			return 325;
		case SpellSlotIncrease:
			return 326;
		case MysticalAttune:
			return 327;
		case DelayDeath:
			return 328;
		case ManaAbsorbPercentDamage:
			return 329;
		case CriticalDamageMob:
			return 330;
		case Salvage:
			return 331;
		case SummonToCorpse:
			return 332;
		case CastOnRuneFadeEffect:
			return 333;
		case BardAEDot:
			return 334;
		case BlockNextSpellFocus:
			return 335;
		case IllusionaryTarget:
			return 336;
		case PercentXPIncrease:
			return 337;
		case SummonAndResAllCorpses:
			return 338;
		case TriggerOnCast:
			return 339;
		case SpellTrigger:
			return 340;
		case ItemAttackCapIncrease:
			return 341;
		case ImmuneFleeing:
			return 342;
		case InterruptCasting:
			return 343;
		case ChannelChanceItems:
			return 344;
		case AssassinateLevel:
			return 345;
		case HeadShotLevel:
			return 346;
		case DoubleRangedAttack:
			return 347;
		case LimitManaMin:
			return 348;
		case ShieldEquipDmgMod:
			return 349;
		case ManaBurn:
			return 350;
		case PersistentEffect:
			return 351;
		case IncreaseTrapCount:
			return 352;
		case AdditionalAura:
			return 353;
		case DeactivateAllTraps:
			return 354;
		case LearnTrap:
			return 355;
		case ChangeTriggerType:
			return 356;
		case FcMute:
			return 357;
		case CurrentManaOnce:
			return 358;
		case PassiveSenseTrap:
			return 359;
		case ProcOnKillShot:
			return 360;
		case SpellOnDeath:
			return 361;
		case PotionBeltSlots:
			return 362;
		case BandolierSlots:
			return 363;
		case TripleAttackChance:
			return 364;
		case ProcOnSpellKillShot:
			return 365;
		case GroupShielding:
			return 366;
		case SetBodyType:
			return 367;
		case FactionMod:
			return 368;
		case CorruptionCounter:
			return 369;
		case ResistCorruption:
			return 370;
		case AttackSpeed4:
			return 371;
		case ForageSkill:
			return 372;
		case CastOnFadeEffectAlways:
			return 373;
		case ApplyEffect:
			return 374;
		case DotCritDmgIncrease:
			return 375;
		case Fling:
			return 376;
		case CastOnFadeEffectNPC:
			return 377;
		case SpellEffectResistChance:
			return 378;
		case ShadowStepDirectional:
			return 379;
		case Knockdown:
			return 380;
		case KnockTowardCaster:
			return 381;
		case NegateSpellEffect:
			return 382;
		case SympatheticProc:
			return 383;
		case Leap:
			return 384;
		case LimitSpellGroup:
			return 385;
		case CastOnCurer:
			return 386;
		case CastOnCure:
			return 387;
		case SummonCorpseZone:
			return 388;
		case FcTimerRefresh:
			return 389;
		case FcTimerLockout:
			return 390;
		case LimitManaMax:
			return 391;
		case FcHealAmt:
			return 392;
		case FcHealPctIncoming:
			return 393;
		case FcHealAmtIncoming:
			return 394;
		case FcHealPctCritIncoming:
			return 395;
		case FcHealAmtCrit:
			return 396;
		case PetMeleeMitigation:
			return 397;
		case SwarmPetDuration:
			return 398;
		case FcTwincast:
			return 399;
		case HealGroupFromMana:
			return 400;
		case ManaDrainWithDmg:
			return 401;
		case EndDrainWithDmg:
			return 402;
		case LimitSpellClass:
			return 403;
		case LimitSpellSubclass:
			return 404;
		case TwoHandBluntBlock:
			return 405;
		case CastonNumHitFade:
			return 406;
		case CastonFocusEffect:
			return 407;
		case LimitHPPercent:
			return 408;
		case LimitManaPercent:
			return 409;
		case LimitEndPercent:
			return 410;
		case LimitClass:
			return 411;
		case LimitRace:
			return 412;
		case FcBaseEffects:
			return 413;
		case LimitCastingSkill:
			return 414;
		case FFItemClass:
			return 415;
		case ACv2:
			return 416;
		case ManaRegen_v2:
			return 417;
		case SkillDamageAmount2:
			return 418;
		case AddMeleeProc:
			return 419;
		case FcLimitUse:
			return 420;
		case FcIncreaseNumHits:
			return 421;
		case LimitUseMin:
			return 422;
		case LimitUseType:
			return 423;
		case GravityEffect:
			return 424;
		case Display:
			return 425;
		case IncreaseExtTargetWindow:
			return 426;
		case SkillProc:
			return 427;
		case LimitToSkill:
			return 428;
		case SkillProcSuccess:
			return 429;
		case PostEffect:
			return 430;
		case PostEffectData:
			return 431;
		case ExpandMaxActiveTrophyBen:
			return 432;
		case CriticalDotDecay:
			return 433;
		case CriticalHealDecay:
			return 434;
		case CriticalRegenDecay:
			return 435;
		case BeneficialCountDownHold:
			return 436;
		case TeleporttoAnchor:
			return 437;
		case TranslocatetoAnchor:
			return 438;
		case Assassinate:
			return 439;
		case FinishingBlowLvl:
			return 440;
		case DistanceRemoval:
			return 441;
		case TriggerOnReqTarget:
			return 442;
		case TriggerOnReqCaster:
			return 443;
		case ImprovedTaunt:
			return 444;
		case AddMercSlot:
			return 445;
		case AStacker:
			return 446;
		case BStacker:
			return 447;
		case CStacker:
			return 448;
		case DStacker:
			return 449;
		case MitigateDotDamage:
			return 450;
		case MeleeThresholdGuard:
			return 451;
		case SpellThresholdGuard:
			return 452;
		case TriggerMeleeThreshold:
			return 453;
		case TriggerSpellThreshold:
			return 454;
		case AddHatePct:
			return 455;
		case AddHateOverTimePct:
			return 456;
		case ResourceTap:
			return 457;
		case FactionModPct:
			return 458;
		case DamageModifier2:
			return 459;
		case Ff_Override_NotFocusable:
			return 460;
		case ImprovedDamage2:
			return 461;
		case FcDamageAmt2:
			return 462;
		case Shield_Target:
			return 463;
		case PC_Pet_Rampage:
			return 464;
		case PC_Pet_AE_Rampage:
			return 465;
		case PC_Pet_Flurry_Chance:
			return 466;
		case DS_Mitigation_Amount:
			return 467;
		case DS_Mitigation_Percentage:
			return 468;
		case Chance_Best_in_Spell_Grp:
			return 469;
		case SE_Trigger_Best_in_Spell_Grp:
			return 470;
		case Double_Melee_Round:
			return 471;
		case Backstab:
			return 472;
		case Disarm:
			return 473;
		case BindWound:
			return 474;
		case Kick:
			return 475;
		case Mend:
			return 476;
		case RoundKick:
			return 477;
		case TigerClaw:
			return 478;
		case Intimidation:
			return 479;
		case EagleStrike:
			return 480;
		case DragonPunch:
			return 481;
		case FlyingKick:
			return 482;
		case ERROR:
			return -1;
		case TailRake:
			return 484;
		case Block:
			return 485;
		case SummonNPCID:
			return 486;
		case FeatherFall:
			return 487;
		case MiningHaste:
			return 488;
		case Bash:
			return 489;
		case Slam:
			return 490;
		case Picklock:
			return 491;
		case Frenzy:
			return 492;
		case CureVampirism:
			return 493;
		default:
			return -1;
		}
	}

	public static SpellEffectType getSpellEffectType(Integer typeId) {
		switch (typeId) {
		case 0:
			return SpellEffectType.CurrentHP;
		case 1:
			return SpellEffectType.ArmorClass;
		case 2:
			return SpellEffectType.ATK;
		case 3:
			return SpellEffectType.MovementSpeed;
		case 4:
			return SpellEffectType.STR;
		case 5:
			return SpellEffectType.DEX;
		case 6:
			return SpellEffectType.AGI;
		case 7:
			return SpellEffectType.STA;
		case 8:
			return SpellEffectType.INT;
		case 9:
			return SpellEffectType.WIS;
		case 10:
			return SpellEffectType.CHA;
		case 11:
			return SpellEffectType.AttackSpeed;
		case 12:
			return SpellEffectType.Invisibility;
		case 13:
			return SpellEffectType.SeeInvis;
		case 14:
			return SpellEffectType.WaterBreathing;
		case 15:
			return SpellEffectType.CurrentMana;
		case 16:
			return SpellEffectType.NPCFrenzy;
		case 17:
			return SpellEffectType.NPCAwareness;
		case 18:
			return SpellEffectType.Lull;
		case 19:
			return SpellEffectType.AddFaction;
		case 20:
			return SpellEffectType.Blind;
		case 21:
			return SpellEffectType.Stun;
		case 22:
			return SpellEffectType.Charm;
		case 23:
			return SpellEffectType.Fear;
		case 24:
			return SpellEffectType.Stamina;
		case 25:
			return SpellEffectType.BindAffinity;
		case 26:
			return SpellEffectType.Gate;
		case 27:
			return SpellEffectType.CancelMagic;
		case 28:
			return SpellEffectType.InvisVsUndead;
		case 29:
			return SpellEffectType.InvisVsAnimals;
		case 30:
			return SpellEffectType.ChangeFrenzyRad;
		case 31:
			return SpellEffectType.Mez;
		case 32:
			return SpellEffectType.SummonItem;
		case 33:
			return SpellEffectType.SummonPet;
		case 34:
			return SpellEffectType.Confuse;
		case 35:
			return SpellEffectType.DiseaseCounter;
		case 36:
			return SpellEffectType.PoisonCounter;
		case 37:
			return SpellEffectType.DetectHostile;
		case 38:
			return SpellEffectType.DetectMagic;
		case 39:
			return SpellEffectType.DetectPoison;
		case 40:
			return SpellEffectType.DivineAura;
		case 41:
			return SpellEffectType.Destroy;
		case 42:
			return SpellEffectType.ShadowStep;
		case 43:
			return SpellEffectType.Berserk;
		case 44:
			return SpellEffectType.Lycanthropy;
		case 45:
			return SpellEffectType.Vampirism;
		case 46:
			return SpellEffectType.ResistFire;
		case 47:
			return SpellEffectType.ResistCold;
		case 48:
			return SpellEffectType.ResistPoison;
		case 49:
			return SpellEffectType.ResistDisease;
		case 50:
			return SpellEffectType.ResistMagic;
		case 51:
			return SpellEffectType.DetectTraps;
		case 52:
			return SpellEffectType.SenseDead;
		case 53:
			return SpellEffectType.SenseSummoned;
		case 54:
			return SpellEffectType.SenseAnimals;
		case 55:
			return SpellEffectType.Rune;
		case 56:
			return SpellEffectType.TrueNorth;
		case 57:
			return SpellEffectType.Levitate;
		case 58:
			return SpellEffectType.Illusion;
		case 59:
			return SpellEffectType.DamageShield;
		case 60:
			return SpellEffectType.TransferItem;
		case 61:
			return SpellEffectType.Identify;
		case 62:
			return SpellEffectType.ItemID;
		case 63:
			return SpellEffectType.WipeHateList;
		case 64:
			return SpellEffectType.SpinTarget;
		case 65:
			return SpellEffectType.InfraVision;
		case 66:
			return SpellEffectType.UltraVision;
		case 67:
			return SpellEffectType.EyeOfZomm;
		case 68:
			return SpellEffectType.ReclaimPet;
		case 69:
			return SpellEffectType.TotalHP;
		case 70:
			return SpellEffectType.CorpseBomb;
		case 71:
			return SpellEffectType.NecPet;
		case 72:
			return SpellEffectType.PreserveCorpse;
		case 73:
			return SpellEffectType.BindSight;
		case 74:
			return SpellEffectType.FeignDeath;
		case 75:
			return SpellEffectType.VoiceGraft;
		case 76:
			return SpellEffectType.Sentinel;
		case 77:
			return SpellEffectType.LocateCorpse;
		case 78:
			return SpellEffectType.AbsorbMagicAtt;
		case 79:
			return SpellEffectType.CurrentHPOnce;
		case 80:
			return SpellEffectType.EnchantLight;
		case 81:
			return SpellEffectType.Revive;
		case 82:
			return SpellEffectType.SummonPC;
		case 83:
			return SpellEffectType.Teleport;
		case 84:
			return SpellEffectType.TossUp;
		case 85:
			return SpellEffectType.WeaponProc;
		case 86:
			return SpellEffectType.Harmony;
		case 87:
			return SpellEffectType.MagnifyVision;
		case 88:
			return SpellEffectType.Succor;
		case 89:
			return SpellEffectType.ModelSize;
		case 90:
			return SpellEffectType.Cloak;
		case 91:
			return SpellEffectType.SummonCorpse;
		case 92:
			return SpellEffectType.InstantHate;
		case 93:
			return SpellEffectType.StopRain;
		case 94:
			return SpellEffectType.NegateIfCombat;
		case 95:
			return SpellEffectType.Sacrifice;
		case 96:
			return SpellEffectType.Silence;
		case 97:
			return SpellEffectType.ManaPool;
		case 98:
			return SpellEffectType.AttackSpeed2;
		case 99:
			return SpellEffectType.Root;
		case 100:
			return SpellEffectType.HealOverTime;
		case 101:
			return SpellEffectType.CompleteHeal;
		case 102:
			return SpellEffectType.Fearless;
		case 103:
			return SpellEffectType.CallPet;
		case 104:
			return SpellEffectType.Translocate;
		case 105:
			return SpellEffectType.AntiGate;
		case 106:
			return SpellEffectType.SummonBSTPet;
		case 107:
			return SpellEffectType.AlterNPCLevel;
		case 108:
			return SpellEffectType.Familiar;
		case 109:
			return SpellEffectType.SummonItemIntoBag;
		case 110:
			return SpellEffectType.IncreaseArchery;
		case 111:
			return SpellEffectType.ResistAll;
		case 112:
			return SpellEffectType.CastingLevel;
		case 113:
			return SpellEffectType.SummonHorse;
		case 114:
			return SpellEffectType.ChangeAggro;
		case 115:
			return SpellEffectType.Hunger;
		case 116:
			return SpellEffectType.CurseCounter;
		case 117:
			return SpellEffectType.MagicWeapon;
		case 118:
			return SpellEffectType.Amplification;
		case 119:
			return SpellEffectType.AttackSpeed3;
		case 120:
			return SpellEffectType.HealRate;
		case 121:
			return SpellEffectType.ReverseDS;
		case 122:
			return SpellEffectType.ReduceSkill;
		case 123:
			return SpellEffectType.Screech;
		case 124:
			return SpellEffectType.ImprovedDamage;
		case 125:
			return SpellEffectType.ImprovedHeal;
		case 126:
			return SpellEffectType.SpellResistReduction;
		case 127:
			return SpellEffectType.IncreaseSpellHaste;
		case 128:
			return SpellEffectType.IncreaseSpellDuration;
		case 129:
			return SpellEffectType.IncreaseRange;
		case 130:
			return SpellEffectType.SpellHateMod;
		case 131:
			return SpellEffectType.ReduceReagentCost;
		case 132:
			return SpellEffectType.ReduceManaCost;
		case 133:
			return SpellEffectType.FcStunTimeMod;
		case 134:
			return SpellEffectType.LimitMaxLevel;
		case 135:
			return SpellEffectType.LimitResist;
		case 136:
			return SpellEffectType.LimitTarget;
		case 137:
			return SpellEffectType.LimitEffect;
		case 138:
			return SpellEffectType.LimitSpellType;
		case 139:
			return SpellEffectType.LimitSpell;
		case 140:
			return SpellEffectType.LimitMinDur;
		case 141:
			return SpellEffectType.LimitInstant;
		case 142:
			return SpellEffectType.LimitMinLevel;
		case 143:
			return SpellEffectType.LimitCastTimeMin;
		case 144:
			return SpellEffectType.LimitCastTimeMax;
		case 145:
			return SpellEffectType.Teleport2;
		case 146:
			return SpellEffectType.ElectricityResist;
		case 147:
			return SpellEffectType.PercentalHeal;
		case 148:
			return SpellEffectType.StackingCommand_Block;
		case 149:
			return SpellEffectType.StackingCommand_Overwrite;
		case 150:
			return SpellEffectType.DeathSave;
		case 151:
			return SpellEffectType.SuspendPet;
		case 152:
			return SpellEffectType.TemporaryPets;
		case 153:
			return SpellEffectType.BalanceHP;
		case 154:
			return SpellEffectType.DispelDetrimental;
		case 155:
			return SpellEffectType.SpellCritDmgIncrease;
		case 156:
			return SpellEffectType.IllusionCopy;
		case 157:
			return SpellEffectType.SpellDamageShield;
		case 158:
			return SpellEffectType.Reflect;
		case 159:
			return SpellEffectType.AllStats;
		case 160:
			return SpellEffectType.MakeDrunk;
		case 161:
			return SpellEffectType.MitigateSpellDamage;
		case 162:
			return SpellEffectType.MitigateMeleeDamage;
		case 163:
			return SpellEffectType.NegateAttacks;
		case 164:
			return SpellEffectType.AppraiseLDonChest;
		case 165:
			return SpellEffectType.DisarmLDoNTrap;
		case 166:
			return SpellEffectType.UnlockLDoNChest;
		case 167:
			return SpellEffectType.PetPowerIncrease;
		case 168:
			return SpellEffectType.MeleeMitigation;
		case 169:
			return SpellEffectType.CriticalHitChance;
		case 170:
			return SpellEffectType.SpellCritChance;
		case 171:
			return SpellEffectType.CrippBlowChance;
		case 172:
			return SpellEffectType.AvoidMeleeChance;
		case 173:
			return SpellEffectType.RiposteChance;
		case 174:
			return SpellEffectType.DodgeChance;
		case 175:
			return SpellEffectType.ParryChance;
		case 176:
			return SpellEffectType.DualWieldChance;
		case 177:
			return SpellEffectType.DoubleAttackChance;
		case 178:
			return SpellEffectType.MeleeLifetap;
		case 179:
			return SpellEffectType.AllInstrumentMod;
		case 180:
			return SpellEffectType.ResistSpellChance;
		case 181:
			return SpellEffectType.ResistFearChance;
		case 182:
			return SpellEffectType.HundredHands;
		case 183:
			return SpellEffectType.MeleeSkillCheck;
		case 184:
			return SpellEffectType.HitChance;
		case 185:
			return SpellEffectType.DamageModifier;
		case 186:
			return SpellEffectType.MinDamageModifier;
		case 187:
			return SpellEffectType.BalanceMana;
		case 188:
			return SpellEffectType.IncreaseBlockChance;
		case 189:
			return SpellEffectType.CurrentEndurance;
		case 190:
			return SpellEffectType.EndurancePool;
		case 191:
			return SpellEffectType.Amnesia;
		case 192:
			return SpellEffectType.Hate;
		case 193:
			return SpellEffectType.SkillAttack;
		case 194:
			return SpellEffectType.FadingMemories;
		case 195:
			return SpellEffectType.StunResist;
		case 196:
			return SpellEffectType.StrikeThrough;
		case 197:
			return SpellEffectType.SkillDamageTaken;
		case 198:
			return SpellEffectType.CurrentEnduranceOnce;
		case 199:
			return SpellEffectType.Taunt;
		case 200:
			return SpellEffectType.ProcChance;
		case 201:
			return SpellEffectType.RangedProc;
		case 202:
			return SpellEffectType.IllusionOther;
		case 203:
			return SpellEffectType.MassGroupBuff;
		case 204:
			return SpellEffectType.GroupFearImmunity;
		case 205:
			return SpellEffectType.Rampage;
		case 206:
			return SpellEffectType.AETaunt;
		case 207:
			return SpellEffectType.FleshToBone;
		case 208:
			return SpellEffectType.PurgePoison;
		case 209:
			return SpellEffectType.DispelBeneficial;
		case 210:
			return SpellEffectType.PetShield;
		case 211:
			return SpellEffectType.AEMelee;
		case 212:
			return SpellEffectType.FrenziedDevastation;
		case 213:
			return SpellEffectType.PetMaxHP;
		case 214:
			return SpellEffectType.MaxHPChange;
		case 215:
			return SpellEffectType.PetAvoidance;
		case 216:
			return SpellEffectType.Accuracy;
		case 217:
			return SpellEffectType.HeadShot;
		case 218:
			return SpellEffectType.PetCriticalHit;
		case 219:
			return SpellEffectType.SlayUndead;
		case 220:
			return SpellEffectType.SkillDamageAmount;
		case 221:
			return SpellEffectType.Packrat;
		case 222:
			return SpellEffectType.BlockBehind;
		case 223:
			return SpellEffectType.DoubleRiposte;
		case 224:
			return SpellEffectType.GiveDoubleRiposte;
		case 225:
			return SpellEffectType.GiveDoubleAttack;
		case 226:
			return SpellEffectType.TwoHandBash;
		case 227:
			return SpellEffectType.ReduceSkillTimer;
		case 228:
			return SpellEffectType.ReduceFallDamage;
		case 229:
			return SpellEffectType.PersistantCasting;
		case 230:
			return SpellEffectType.ExtendedShielding;
		case 231:
			return SpellEffectType.StunBashChance;
		case 232:
			return SpellEffectType.DivineSave;
		case 233:
			return SpellEffectType.Metabolism;
		case 234:
			return SpellEffectType.ReduceApplyPoisonTime;
		case 235:
			return SpellEffectType.ChannelChanceSpells;
		case 236:
			return SpellEffectType.FreePet;
		case 237:
			return SpellEffectType.GivePetGroupTarget;
		case 238:
			return SpellEffectType.IllusionPersistence;
		case 239:
			return SpellEffectType.FeignedCastOnChance;
		case 240:
			return SpellEffectType.StringUnbreakable;
		case 241:
			return SpellEffectType.ImprovedReclaimEnergy;
		case 242:
			return SpellEffectType.IncreaseChanceMemwipe;
		case 243:
			return SpellEffectType.CharmBreakChance;
		case 244:
			return SpellEffectType.RootBreakChance;
		case 245:
			return SpellEffectType.TrapCircumvention;
		case 246:
			return SpellEffectType.SetBreathLevel;
		case 247:
			return SpellEffectType.RaiseSkillCap;
		case 248:
			return SpellEffectType.SecondaryForte;
		case 249:
			return SpellEffectType.SecondaryDmgInc;
		case 250:
			return SpellEffectType.SpellProcChance;
		case 251:
			return SpellEffectType.ConsumeProjectile;
		case 252:
			return SpellEffectType.FrontalBackstabChance;
		case 253:
			return SpellEffectType.FrontalBackstabMinDmg;
		case 254:
			return SpellEffectType.Blank;
		case 255:
			return SpellEffectType.ShieldDuration;
		case 256:
			return SpellEffectType.ShroudofStealth;
		case 257:
			return SpellEffectType.PetDiscipline;
		case 258:
			return SpellEffectType.TripleBackstab;
		case 259:
			return SpellEffectType.CombatStability;
		case 260:
			return SpellEffectType.AddSingingMod;
		case 261:
			return SpellEffectType.SongModCap;
		case 262:
			return SpellEffectType.RaiseStatCap;
		case 263:
			return SpellEffectType.TradeSkillMastery;
		case 264:
			return SpellEffectType.HastenedAASkill;
		case 265:
			return SpellEffectType.MasteryofPast;
		case 266:
			return SpellEffectType.ExtraAttackChance;
		case 267:
			return SpellEffectType.AddPetCommand;
		case 268:
			return SpellEffectType.ReduceTradeskillFail;
		case 269:
			return SpellEffectType.MaxBindWound;
		case 270:
			return SpellEffectType.BardSongRange;
		case 271:
			return SpellEffectType.BaseMovementSpeed;
		case 272:
			return SpellEffectType.CastingLevel2;
		case 273:
			return SpellEffectType.CriticalDoTChance;
		case 274:
			return SpellEffectType.CriticalHealChance;
		case 275:
			return SpellEffectType.CriticalMend;
		case 276:
			return SpellEffectType.Ambidexterity;
		case 277:
			return SpellEffectType.UnfailingDivinity;
		case 278:
			return SpellEffectType.FinishingBlow;
		case 279:
			return SpellEffectType.Flurry;
		case 280:
			return SpellEffectType.PetFlurry;
		case 281:
			return SpellEffectType.FeignedMinion;
		case 282:
			return SpellEffectType.ImprovedBindWound;
		case 283:
			return SpellEffectType.DoubleSpecialAttack;
		case 284:
			return SpellEffectType.LoHSetHeal;
		case 285:
			return SpellEffectType.NimbleEvasion;
		case 286:
			return SpellEffectType.FcDamageAmt;
		case 287:
			return SpellEffectType.SpellDurationIncByTic;
		case 288:
			return SpellEffectType.SkillAttackProc;
		case 289:
			return SpellEffectType.CastOnFadeEffect;
		case 290:
			return SpellEffectType.IncreaseRunSpeedCap;
		case 291:
			return SpellEffectType.Purify;
		case 292:
			return SpellEffectType.StrikeThrough2;
		case 293:
			return SpellEffectType.FrontalStunResist;
		case 294:
			return SpellEffectType.CriticalSpellChance;
		case 295:
			return SpellEffectType.ReduceTimerSpecial;
		case 296:
			return SpellEffectType.FcSpellVulnerability;
		case 297:
			return SpellEffectType.FcDamageAmtIncoming;
		case 298:
			return SpellEffectType.ChangeHeight;
		case 299:
			return SpellEffectType.WakeTheDead;
		case 300:
			return SpellEffectType.Doppelganger;
		case 301:
			return SpellEffectType.ArcheryDamageModifier;
		case 302:
			return SpellEffectType.FcDamagePctCrit;
		case 303:
			return SpellEffectType.FcDamageAmtCrit;
		case 304:
			return SpellEffectType.OffhandRiposteFail;
		case 305:
			return SpellEffectType.MitigateDamageShield;
		case 306:
			return SpellEffectType.ArmyOfTheDead;
		case 307:
			return SpellEffectType.Appraisal;
		case 308:
			return SpellEffectType.SuspendMinion;
		case 309:
			return SpellEffectType.GateCastersBindpoint;
		case 310:
			return SpellEffectType.ReduceReuseTimer;
		case 311:
			return SpellEffectType.LimitCombatSkills;
		case 312:
			return SpellEffectType.Sanctuary;
		case 313:
			return SpellEffectType.ForageAdditionalItems;
		case 314:
			return SpellEffectType.Invisibility2;
		case 315:
			return SpellEffectType.InvisVsUndead2;
		case 316:
			return SpellEffectType.ImprovedInvisAnimals;
		case 317:
			return SpellEffectType.ItemHPRegenCapIncrease;
		case 318:
			return SpellEffectType.ItemManaRegenCapIncrease;
		case 319:
			return SpellEffectType.CriticalHealOverTime;
		case 320:
			return SpellEffectType.ShieldBlock;
		case 321:
			return SpellEffectType.ReduceHate;
		case 322:
			return SpellEffectType.GateToHomeCity;
		case 323:
			return SpellEffectType.DefensiveProc;
		case 324:
			return SpellEffectType.HPToMana;
		case 325:
			return SpellEffectType.NoBreakAESneak;
		case 326:
			return SpellEffectType.SpellSlotIncrease;
		case 327:
			return SpellEffectType.MysticalAttune;
		case 328:
			return SpellEffectType.DelayDeath;
		case 329:
			return SpellEffectType.ManaAbsorbPercentDamage;
		case 330:
			return SpellEffectType.CriticalDamageMob;
		case 331:
			return SpellEffectType.Salvage;
		case 332:
			return SpellEffectType.SummonToCorpse;
		case 333:
			return SpellEffectType.CastOnRuneFadeEffect;
		case 334:
			return SpellEffectType.BardAEDot;
		case 335:
			return SpellEffectType.BlockNextSpellFocus;
		case 336:
			return SpellEffectType.IllusionaryTarget;
		case 337:
			return SpellEffectType.PercentXPIncrease;
		case 338:
			return SpellEffectType.SummonAndResAllCorpses;
		case 339:
			return SpellEffectType.TriggerOnCast;
		case 340:
			return SpellEffectType.SpellTrigger;
		case 341:
			return SpellEffectType.ItemAttackCapIncrease;
		case 342:
			return SpellEffectType.ImmuneFleeing;
		case 343:
			return SpellEffectType.InterruptCasting;
		case 344:
			return SpellEffectType.ChannelChanceItems;
		case 345:
			return SpellEffectType.AssassinateLevel;
		case 346:
			return SpellEffectType.HeadShotLevel;
		case 347:
			return SpellEffectType.DoubleRangedAttack;
		case 348:
			return SpellEffectType.LimitManaMin;
		case 349:
			return SpellEffectType.ShieldEquipDmgMod;
		case 350:
			return SpellEffectType.ManaBurn;
		case 351:
			return SpellEffectType.PersistentEffect;
		case 352:
			return SpellEffectType.IncreaseTrapCount;
		case 353:
			return SpellEffectType.AdditionalAura;
		case 354:
			return SpellEffectType.DeactivateAllTraps;
		case 355:
			return SpellEffectType.LearnTrap;
		case 356:
			return SpellEffectType.ChangeTriggerType;
		case 357:
			return SpellEffectType.FcMute;
		case 358:
			return SpellEffectType.CurrentManaOnce;
		case 359:
			return SpellEffectType.PassiveSenseTrap;
		case 360:
			return SpellEffectType.ProcOnKillShot;
		case 361:
			return SpellEffectType.SpellOnDeath;
		case 362:
			return SpellEffectType.PotionBeltSlots;
		case 363:
			return SpellEffectType.BandolierSlots;
		case 364:
			return SpellEffectType.TripleAttackChance;
		case 365:
			return SpellEffectType.ProcOnSpellKillShot;
		case 366:
			return SpellEffectType.GroupShielding;
		case 367:
			return SpellEffectType.SetBodyType;
		case 368:
			return SpellEffectType.FactionMod;
		case 369:
			return SpellEffectType.CorruptionCounter;
		case 370:
			return SpellEffectType.ResistCorruption;
		case 371:
			return SpellEffectType.AttackSpeed4;
		case 372:
			return SpellEffectType.ForageSkill;
		case 373:
			return SpellEffectType.CastOnFadeEffectAlways;
		case 374:
			return SpellEffectType.ApplyEffect;
		case 375:
			return SpellEffectType.DotCritDmgIncrease;
		case 376:
			return SpellEffectType.Fling;
		case 377:
			return SpellEffectType.CastOnFadeEffectNPC;
		case 378:
			return SpellEffectType.SpellEffectResistChance;
		case 379:
			return SpellEffectType.ShadowStepDirectional;
		case 380:
			return SpellEffectType.Knockdown;
		case 381:
			return SpellEffectType.KnockTowardCaster;
		case 382:
			return SpellEffectType.NegateSpellEffect;
		case 383:
			return SpellEffectType.SympatheticProc;
		case 384:
			return SpellEffectType.Leap;
		case 385:
			return SpellEffectType.LimitSpellGroup;
		case 386:
			return SpellEffectType.CastOnCurer;
		case 387:
			return SpellEffectType.CastOnCure;
		case 388:
			return SpellEffectType.SummonCorpseZone;
		case 389:
			return SpellEffectType.FcTimerRefresh;
		case 390:
			return SpellEffectType.FcTimerLockout;
		case 391:
			return SpellEffectType.LimitManaMax;
		case 392:
			return SpellEffectType.FcHealAmt;
		case 393:
			return SpellEffectType.FcHealPctIncoming;
		case 394:
			return SpellEffectType.FcHealAmtIncoming;
		case 395:
			return SpellEffectType.FcHealPctCritIncoming;
		case 396:
			return SpellEffectType.FcHealAmtCrit;
		case 397:
			return SpellEffectType.PetMeleeMitigation;
		case 398:
			return SpellEffectType.SwarmPetDuration;
		case 399:
			return SpellEffectType.FcTwincast;
		case 400:
			return SpellEffectType.HealGroupFromMana;
		case 401:
			return SpellEffectType.ManaDrainWithDmg;
		case 402:
			return SpellEffectType.EndDrainWithDmg;
		case 403:
			return SpellEffectType.LimitSpellClass;
		case 404:
			return SpellEffectType.LimitSpellSubclass;
		case 405:
			return SpellEffectType.TwoHandBluntBlock;
		case 406:
			return SpellEffectType.CastonNumHitFade;
		case 407:
			return SpellEffectType.CastonFocusEffect;
		case 408:
			return SpellEffectType.LimitHPPercent;
		case 409:
			return SpellEffectType.LimitManaPercent;
		case 410:
			return SpellEffectType.LimitEndPercent;
		case 411:
			return SpellEffectType.LimitClass;
		case 412:
			return SpellEffectType.LimitRace;
		case 413:
			return SpellEffectType.FcBaseEffects;
		case 414:
			return SpellEffectType.LimitCastingSkill;
		case 415:
			return SpellEffectType.FFItemClass;
		case 416:
			return SpellEffectType.ACv2;
		case 417:
			return SpellEffectType.ManaRegen_v2;
		case 418:
			return SpellEffectType.SkillDamageAmount2;
		case 419:
			return SpellEffectType.AddMeleeProc;
		case 420:
			return SpellEffectType.FcLimitUse;
		case 421:
			return SpellEffectType.FcIncreaseNumHits;
		case 422:
			return SpellEffectType.LimitUseMin;
		case 423:
			return SpellEffectType.LimitUseType;
		case 424:
			return SpellEffectType.GravityEffect;
		case 425:
			return SpellEffectType.Display;
		case 426:
			return SpellEffectType.IncreaseExtTargetWindow;
		case 427:
			return SpellEffectType.SkillProc;
		case 428:
			return SpellEffectType.LimitToSkill;
		case 429:
			return SpellEffectType.SkillProcSuccess;
		case 430:
			return SpellEffectType.PostEffect;
		case 431:
			return SpellEffectType.PostEffectData;
		case 432:
			return SpellEffectType.ExpandMaxActiveTrophyBen;
		case 433:
			return SpellEffectType.CriticalDotDecay;
		case 434:
			return SpellEffectType.CriticalHealDecay;
		case 435:
			return SpellEffectType.CriticalRegenDecay;
		case 436:
			return SpellEffectType.BeneficialCountDownHold;
		case 437:
			return SpellEffectType.TeleporttoAnchor;
		case 438:
			return SpellEffectType.TranslocatetoAnchor;
		case 439:
			return SpellEffectType.Assassinate;
		case 440:
			return SpellEffectType.FinishingBlowLvl;
		case 441:
			return SpellEffectType.DistanceRemoval;
		case 442:
			return SpellEffectType.TriggerOnReqTarget;
		case 443:
			return SpellEffectType.TriggerOnReqCaster;
		case 444:
			return SpellEffectType.ImprovedTaunt;
		case 445:
			return SpellEffectType.AddMercSlot;
		case 446:
			return SpellEffectType.AStacker;
		case 447:
			return SpellEffectType.BStacker;
		case 448:
			return SpellEffectType.CStacker;
		case 449:
			return SpellEffectType.DStacker;
		case 450:
			return SpellEffectType.MitigateDotDamage;
		case 451:
			return SpellEffectType.MeleeThresholdGuard;
		case 452:
			return SpellEffectType.SpellThresholdGuard;
		case 453:
			return SpellEffectType.TriggerMeleeThreshold;
		case 454:
			return SpellEffectType.TriggerSpellThreshold;
		case 455:
			return SpellEffectType.AddHatePct;
		case 456:
			return SpellEffectType.AddHateOverTimePct;
		case 457:
			return SpellEffectType.ResourceTap;
		case 458:
			return SpellEffectType.FactionModPct;
		case 459:
			return SpellEffectType.DamageModifier2;
		case 460:
			return SpellEffectType.Ff_Override_NotFocusable;
		case 461:
			return SpellEffectType.ImprovedDamage2;
		case 462:
			return SpellEffectType.FcDamageAmt2;
		case 463:
			return SpellEffectType.Shield_Target;
		case 464:
			return SpellEffectType.PC_Pet_Rampage;
		case 465:
			return SpellEffectType.PC_Pet_AE_Rampage;
		case 466:
			return SpellEffectType.PC_Pet_Flurry_Chance;
		case 467:
			return SpellEffectType.DS_Mitigation_Amount;
		case 468:
			return SpellEffectType.DS_Mitigation_Percentage;
		case 469:
			return SpellEffectType.Chance_Best_in_Spell_Grp;
		case 470:
			return SpellEffectType.SE_Trigger_Best_in_Spell_Grp;
		case 471:
			return SpellEffectType.Double_Melee_Round;
		case 472:
			return SpellEffectType.Backstab;
		case 473:
			return SpellEffectType.Disarm;
		case 474:
			return SpellEffectType.BindWound;
		case 475:
			return SpellEffectType.Kick;
		case 476:
			return SpellEffectType.Mend;
		case 477:
			return SpellEffectType.RoundKick;
		case 478:
			return SpellEffectType.TigerClaw;
		case 479:
			return SpellEffectType.Intimidation;
		case 480:
			return SpellEffectType.EagleStrike;
		case 481:
			return SpellEffectType.DragonPunch;
		case 482:
			return SpellEffectType.FlyingKick;
		case 483:
			return SpellEffectType.ERROR;
		case 484:
			return SpellEffectType.TailRake;
		case 485:
			return SpellEffectType.Block;
		case 486:
			return SpellEffectType.SummonNPCID;
		case 487:
			return SpellEffectType.FeatherFall;
		case 488:
			return SpellEffectType.MiningHaste;
		case 489:
			return SpellEffectType.Bash;
		case 490:
			return SpellEffectType.Slam;
		case 491:
			return SpellEffectType.Picklock;
		case 492:
			return SpellEffectType.Frenzy;
		case 493:
			return SpellEffectType.CureVampirism;
		default:
			return SpellEffectType.ERROR;
		}
	}
	
	public static SpellEffectType getSpellEffectTypeFromResistType(SpellResistType type) {
		switch (type) {
		case RESIST_COLD:
			return SpellEffectType.ResistCold;
		case RESIST_FIRE:
			return SpellEffectType.ResistFire;
		case RESIST_POISON:
			return SpellEffectType.ResistPoison;
		case RESIST_DISEASE:
			return SpellEffectType.ResistDisease;
		case RESIST_MAGIC:
			return SpellEffectType.ResistMagic;
		case RESIST_CORRUPTION:
			return SpellEffectType.ResistCorruption;
		case RESIST_NONE:
			return null;
		default:
			return null;

		}
	}

	public static int getDurationFromSpell(ISoliniaLivingEntity source,ISoliniaLivingEntity target, ISoliniaSpell soliniaSpell) {
		int duration = soliniaSpell.calcBuffDuration(source, target);
		if (duration > 0) {
			duration = soliniaSpell.getActSpellDuration(source, duration);
		}

		return duration;
	}
	
	public static SpellTargetType getSpellTargetType(int spellTargetId) {
		switch (spellTargetId) {
		case 1:
			return SpellTargetType.TargetOptional;
		case 2:
			return SpellTargetType.AEClientV1;
		case 3:
			return SpellTargetType.GroupTeleport;
		case 4:
			return SpellTargetType.AECaster;
		case 5:
			return SpellTargetType.Target;
		case 6:
			return SpellTargetType.Self;
		case 8:
			return SpellTargetType.AETarget;
		case 9:
			return SpellTargetType.Animal;
		case 10:
			return SpellTargetType.Undead;
		case 11:
			return SpellTargetType.Summoned;
		case 13:
			return SpellTargetType.Tap;
		case 14:
			return SpellTargetType.Pet;
		case 15:
			return SpellTargetType.Corpse;
		case 16:
			return SpellTargetType.Plant;
		case 17:
			return SpellTargetType.Giant;
		case 18:
			return SpellTargetType.Dragon;
		case 20:
			return SpellTargetType.TargetAETap;
		case 24:
			return SpellTargetType.UndeadAE;
		case 25:
			return SpellTargetType.SummonedAE;
		case 32:
			return SpellTargetType.AETargetHateList;
		case 33:
			return SpellTargetType.HateList;
		case 36:
			return SpellTargetType.AreaClientOnly;
		case 37:
			return SpellTargetType.AreaNPCOnly;
		case 38:
			return SpellTargetType.SummonedPet;
		case 39:
			return SpellTargetType.GroupNoPets;
		case 40:
			return SpellTargetType.AEBard;
		case 41:
			return SpellTargetType.Group;
		case 42:
			return SpellTargetType.Directional;
		case 43:
			return SpellTargetType.GroupClientAndPet;
		case 44:
			return SpellTargetType.Beam;
		case 45:
			return SpellTargetType.Ring;
		case 46:
			return SpellTargetType.TargetsTarget;
		case 47:
			return SpellTargetType.PetMaster;
		case 50:
			return SpellTargetType.TargetAENoPlayersPets;
		default:
			return SpellTargetType.Error;
		}

	}

}
