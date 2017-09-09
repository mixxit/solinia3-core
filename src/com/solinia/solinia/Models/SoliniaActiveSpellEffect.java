package com.solinia.solinia.Models;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Managers.StateManager;

import net.md_5.bungee.api.ChatColor;

public class SoliniaActiveSpellEffect {
	private int spellId;
	private int ticksLeft;
	private boolean isOwnerPlayer = false;
	private boolean isSourcePlayer = false;
	private UUID sourceUuid;
	private UUID ownerUuid;
	private boolean isFirstRun = true;
	
	public SoliniaActiveSpellEffect(UUID owneruuid, int spellId, boolean isOwnerPlayer, UUID sourceuuid, boolean sourceIsPlayer, int ticksLeft) {
		setOwnerUuid(owneruuid);
		setOwnerPlayer(isOwnerPlayer);
		setSourceUuid(sourceuuid);
		this.setSourcePlayer(sourceIsPlayer);
		setSpellId(spellId);
		setTicksLeft(ticksLeft);
	}

	public boolean isOwnerPlayer() {
		return isOwnerPlayer;
	}

	public void setOwnerPlayer(boolean isOwnerPlayer) {
		this.isOwnerPlayer = isOwnerPlayer;
	}
	
	public UUID getOwnerUuid() {
		return ownerUuid;
	}

	public void setOwnerUuid(UUID ownerUuid) {
		this.ownerUuid = ownerUuid;
	}

	public UUID getSourceUuid() {
		return sourceUuid;
	}

	public void setSourceUuid(UUID sourceUuid) {
		this.sourceUuid = sourceUuid;
	}

	public int getSpellId() {
		return spellId;
	}

	public void setSpellId(int spellId) {
		this.spellId = spellId;
	}

	public void apply() {
		try {
			ISoliniaSpell soliniaSpell = StateManager.getInstance().getConfigurationManager().getSpell(getSpellId());
			if (soliniaSpell == null)
			{
				System.out.print("Spell not found");
				return;
			}
			
			if (isFirstRun)
			{
				if (soliniaSpell.getCastOnYou() != null && !soliniaSpell.getCastOnYou().equals("") && isOwnerPlayer)
				{
					Player player = Bukkit.getPlayer(getOwnerUuid());
					player.sendMessage("* " + ChatColor.GRAY + soliniaSpell.getCastOnYou());
				}
					
				if (soliniaSpell.getCastOnOther() != null && !soliniaSpell.getCastOnOther().equals(""))
					SoliniaLivingEntityAdapter.Adapt((LivingEntity) Bukkit.getEntity(getOwnerUuid())).emote("* " + this.getLivingEntity().getName() + soliniaSpell.getCastOnOther());
			}

				
			for (SpellEffect spellEffect : soliniaSpell.getSpellEffects()) {
				applySpellEffect(spellEffect, soliniaSpell);
			}
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void applySpellEffect(SpellEffect spellEffect, ISoliniaSpell soliniaSpell) {
		
		switch (spellEffect.getSpellEffectType()) {
		case CurrentHP:
			applyCurrentHpSpellEffect(spellEffect,soliniaSpell);
			return;
		case ArmorClass: 
			return;
		case ATK: 
			return;
		case MovementSpeed:
			applyMovementSpeedEffect(spellEffect,soliniaSpell);
			return;
		case STR
			: return;
		case DEX
			: return;
		case AGI
			: return;
		case STA
			: return;
		case INT
			: return;
		case WIS
			: return;
		case CHA
			: return;
		case AttackSpeed
			: return;
		case Invisibility: 
			applyInvisibility(spellEffect,soliniaSpell);
			return;
		case SeeInvis
			: return;
		case WaterBreathing: 
			applyWaterBreathing(spellEffect,soliniaSpell);
			return;
		case CurrentMana:
			applyCurrentMpSpellEffect(spellEffect,soliniaSpell);
			return;
		case NPCFrenzy
			: return;
		case NPCAwareness
			: return;
		case Lull
			: return;
		case AddFaction
			: return;
		case Blind: 
			applyBlind(spellEffect,soliniaSpell);
			return;
		case Stun
			: return;
		case Charm
			: return;
		case Fear
			: return;
		case Stamina
			: return;
		case BindAffinity: 
			applyBindAffinty(spellEffect,soliniaSpell);
			return;
		case Gate:
			applyGate(spellEffect,soliniaSpell);
			return;
		case CancelMagic
			: return;
		case InvisVsUndead
			: return;
		case InvisVsAnimals
			: return;
		case ChangeFrenzyRad
			: return;
		case Mez
			: return;
		case SummonItem
			: return;
		case SummonPet
			: return;
		case Confuse: 
			applyConfusion(spellEffect,soliniaSpell);
			return;
		case DiseaseCounter
			: return;
		case PoisonCounter
			: return;
		case DetectHostile
			: return;
		case DetectMagic
			: return;
		case DetectPoison
			: return;
		case DivineAura
			: return;
		case Destroy
			: return;
		case ShadowStep
			: return;
		case Berserk
			: return;
		case Lycanthropy
			: return;
		case Vampirism
			: return;
		case ResistFire
			: return;
		case ResistCold
			: return;
		case ResistPoison
			: return;
		case ResistDisease
			: return;
		case ResistMagic
			: return;
		case DetectTraps
			: return;
		case SenseDead
			: return;
		case SenseSummoned
			: return;
		case SenseAnimals
			: return;
		case Rune
			: return;
		case TrueNorth
			: return;
		case Levitate: 
			applyLevitateSpellEffect(spellEffect,soliniaSpell);
			return;
		case Illusion
			: return;
		case DamageShield
			: return;
		case TransferItem
			: return;
		case Identify
			: return;
		case ItemID
			: return;
		case WipeHateList
			: return;
		case SpinTarget
			: return;
		case InfraVision
			: return;
		case UltraVision
			: return;
		case EyeOfZomm
			: return;
		case ReclaimPet
			: return;
		case TotalHP
			: return;
		case CorpseBomb
			: return;
		case NecPet
			: return;
		case PreserveCorpse
			: return;
		case BindSight
			: return;
		case FeignDeath
			: return;
		case VoiceGraft
			: return;
		case Sentinel
			: return;
		case LocateCorpse
			: return;
		case AbsorbMagicAtt
			: return;
		case CurrentHPOnce:
			applyCurrentHpOnceSpellEffect(spellEffect,soliniaSpell);
			return;
		case EnchantLight
			: return;
		case Revive
			: return;
		case SummonPC
			: return;
		case Teleport
			: return;
		case TossUp
			: return;
		case WeaponProc
			: return;
		case Harmony
			: return;
		case MagnifyVision
			: return;
		case Succor
			: return;
		case ModelSize
			: return;
		case Cloak
			: return;
		case SummonCorpse
			: return;
		case InstantHate
			: return;
		case StopRain
			: return;
		case NegateIfCombat
			: return;
		case Sacrifice
			: return;
		case Silence
			: return;
		case ManaPool
			: return;
		case AttackSpeed2
			: return;
		case Root: 
			applyRootSpellEffect(spellEffect,soliniaSpell);
			return;
		case HealOverTime
			: return;
		case CompleteHeal
			: return;
		case Fearless
			: return;
		case CallPet
			: return;
		case Translocate
			: return;
		case AntiGate
			: return;
		case SummonBSTPet
			: return;
		case AlterNPCLevel
			: return;
		case Familiar
			: return;
		case SummonItemIntoBag
			: return;
		case IncreaseArchery
			: return;
		case ResistAll
			: return;
		case CastingLevel
			: return;
		case SummonHorse
			: return;
		case ChangeAggro
			: return;
		case Hunger
			: return;
		case CurseCounter
			: return;
		case MagicWeapon
			: return;
		case Amplification
			: return;
		case AttackSpeed3
			: return;
		case HealRate
			: return;
		case ReverseDS
			: return;
		case ReduceSkill
			: return;
		case Screech
			: return;
		case ImprovedDamage
			: return;
		case ImprovedHeal
			: return;
		case SpellResistReduction
			: return;
		case IncreaseSpellHaste
			: return;
		case IncreaseSpellDuration
			: return;
		case IncreaseRange
			: return;
		case SpellHateMod
			: return;
		case ReduceReagentCost
			: return;
		case ReduceManaCost
			: return;
		case FcStunTimeMod
			: return;
		case LimitMaxLevel
			: return;
		case LimitResist
			: return;
		case LimitTarget
			: return;
		case LimitEffect
			: return;
		case LimitSpellType
			: return;
		case LimitSpell
			: return;
		case LimitMinDur
			: return;
		case LimitInstant
			: return;
		case LimitMinLevel
			: return;
		case LimitCastTimeMin
			: return;
		case LimitCastTimeMax
			: return;
		case Teleport2
			: return;
		case ElectricityResist
			: return;
		case PercentalHeal
			: return;
		case StackingCommand_Block
			: return;
		case StackingCommand_Overwrite
			: return;
		case DeathSave
			: return;
		case SuspendPet
			: return;
		case TemporaryPets
			: return;
		case BalanceHP
			: return;
		case DispelDetrimental
			: return;
		case SpellCritDmgIncrease
			: return;
		case IllusionCopy
			: return;
		case SpellDamageShield
			: return;
		case Reflect
			: return;
		case AllStats
			: return;
		case MakeDrunk
			: return;
		case MitigateSpellDamage
			: return;
		case MitigateMeleeDamage
			: return;
		case NegateAttacks
			: return;
		case AppraiseLDonChest
			: return;
		case DisarmLDoNTrap
			: return;
		case UnlockLDoNChest
			: return;
		case PetPowerIncrease
			: return;
		case MeleeMitigation
			: return;
		case CriticalHitChance
			: return;
		case SpellCritChance
			: return;
		case CrippBlowChance
			: return;
		case AvoidMeleeChance
			: return;
		case RiposteChance
			: return;
		case DodgeChance
			: return;
		case ParryChance
			: return;
		case DualWieldChance
			: return;
		case DoubleAttackChance
			: return;
		case MeleeLifetap
			: return;
		case AllInstrumentMod
			: return;
		case ResistSpellChance
			: return;
		case ResistFearChance
			: return;
		case HundredHands
			: return;
		case MeleeSkillCheck
			: return;
		case HitChance
			: return;
		case DamageModifier
			: return;
		case MinDamageModifier
			: return;
		case BalanceMana
			: return;
		case IncreaseBlockChance
			: return;
		case CurrentEndurance
			: return;
		case EndurancePool
			: return;
		case Amnesia
			: return;
		case Hate
			: return;
		case SkillAttack
			: return;
		case FadingMemories
			: return;
		case StunResist
			: return;
		case StrikeThrough
			: return;
		case SkillDamageTaken
			: return;
		case CurrentEnduranceOnce
			: return;
		case Taunt
			: return;
		case ProcChance
			: return;
		case RangedProc
			: return;
		case IllusionOther
			: return;
		case MassGroupBuff
			: return;
		case GroupFearImmunity
			: return;
		case Rampage
			: return;
		case AETaunt
			: return;
		case FleshToBone
			: return;
		case PurgePoison
			: return;
		case DispelBeneficial
			: return;
		case PetShield
			: return;
		case AEMelee
			: return;
		case FrenziedDevastation
			: return;
		case PetMaxHP
			: return;
		case MaxHPChange
			: return;
		case PetAvoidance
			: return;
		case Accuracy
			: return;
		case HeadShot
			: return;
		case PetCriticalHit
			: return;
		case SlayUndead
			: return;
		case SkillDamageAmount
			: return;
		case Packrat
			: return;
		case BlockBehind
			: return;
		case DoubleRiposte
			: return;
		case GiveDoubleRiposte
			: return;
		case GiveDoubleAttack
			: return;
		case TwoHandBash
			: return;
		case ReduceSkillTimer
			: return;
		case ReduceFallDamage
			: return;
		case PersistantCasting
			: return;
		case ExtendedShielding
			: return;
		case StunBashChance
			: return;
		case DivineSave
			: return;
		case Metabolism
			: return;
		case ReduceApplyPoisonTime
			: return;
		case ChannelChanceSpells
			: return;
		case FreePet
			: return;
		case GivePetGroupTarget
			: return;
		case IllusionPersistence
			: return;
		case FeignedCastOnChance
			: return;
		case StringUnbreakable
			: return;
		case ImprovedReclaimEnergy
			: return;
		case IncreaseChanceMemwipe
			: return;
		case CharmBreakChance
			: return;
		case RootBreakChance
			: return;
		case TrapCircumvention
			: return;
		case SetBreathLevel
			: return;
		case RaiseSkillCap
			: return;
		case SecondaryForte
			: return;
		case SecondaryDmgInc
			: return;
		case SpellProcChance
			: return;
		case ConsumeProjectile
			: return;
		case FrontalBackstabChance
			: return;
		case FrontalBackstabMinDmg
			: return;
		case Blank
			: return;
		case ShieldDuration
			: return;
		case ShroudofStealth
			: return;
		case PetDiscipline
			: return;
		case TripleBackstab
			: return;
		case CombatStability
			: return;
		case AddSingingMod
			: return;
		case SongModCap
			: return;
		case RaiseStatCap
			: return;
		case TradeSkillMastery
			: return;
		case HastenedAASkill
			: return;
		case MasteryofPast
			: return;
		case ExtraAttackChance
			: return;
		case AddPetCommand
			: return;
		case ReduceTradeskillFail
			: return;
		case MaxBindWound
			: return;
		case BardSongRange
			: return;
		case BaseMovementSpeed
			: return;
		case CastingLevel2
			: return;
		case CriticalDoTChance
			: return;
		case CriticalHealChance
			: return;
		case CriticalMend
			: return;
		case Ambidexterity
			: return;
		case UnfailingDivinity
			: return;
		case FinishingBlow
			: return;
		case Flurry
			: return;
		case PetFlurry
			: return;
		case FeignedMinion
			: return;
		case ImprovedBindWound
			: return;
		case DoubleSpecialAttack
			: return;
		case LoHSetHeal
			: return;
		case NimbleEvasion
			: return;
		case FcDamageAmt
			: return;
		case SpellDurationIncByTic
			: return;
		case SkillAttackProc
			: return;
		case CastOnFadeEffect
			: return;
		case IncreaseRunSpeedCap
			: return;
		case Purify
			: return;
		case StrikeThrough2
			: return;
		case FrontalStunResist
			: return;
		case CriticalSpellChance
			: return;
		case ReduceTimerSpecial
			: return;
		case FcSpellVulnerability
			: return;
		case FcDamageAmtIncoming
			: return;
		case ChangeHeight
			: return;
		case WakeTheDead
			: return;
		case Doppelganger
			: return;
		case ArcheryDamageModifier
			: return;
		case FcDamagePctCrit
			: return;
		case FcDamageAmtCrit
			: return;
		case OffhandRiposteFail
			: return;
		case MitigateDamageShield
			: return;
		case ArmyOfTheDead
			: return;
		case Appraisal
			: return;
		case SuspendMinion
			: return;
		case GateCastersBindpoint
			: return;
		case ReduceReuseTimer
			: return;
		case LimitCombatSkills
			: return;
		case Sanctuary
			: return;
		case ForageAdditionalItems
			: return;
		case Invisibility2
			: return;
		case InvisVsUndead2
			: return;
		case ImprovedInvisAnimals
			: return;
		case ItemHPRegenCapIncrease
			: return;
		case ItemManaRegenCapIncrease
			: return;
		case CriticalHealOverTime
			: return;
		case ShieldBlock
			: return;
		case ReduceHate
			: return;
		case GateToHomeCity
			: return;
		case DefensiveProc
			: return;
		case HPToMana
			: return;
		case NoBreakAESneak
			: return;
		case SpellSlotIncrease
			: return;
		case MysticalAttune
			: return;
		case DelayDeath
			: return;
		case ManaAbsorbPercentDamage
			: return;
		case CriticalDamageMob
			: return;
		case Salvage
			: return;
		case SummonToCorpse
			: return;
		case CastOnRuneFadeEffect
			: return;
		case BardAEDot
			: return;
		case BlockNextSpellFocus
			: return;
		case IllusionaryTarget
			: return;
		case PercentXPIncrease
			: return;
		case SummonAndResAllCorpses
			: return;
		case TriggerOnCast
			: return;
		case SpellTrigger
			: return;
		case ItemAttackCapIncrease
			: return;
		case ImmuneFleeing
			: return;
		case InterruptCasting
			: return;
		case ChannelChanceItems
			: return;
		case AssassinateLevel
			: return;
		case HeadShotLevel
			: return;
		case DoubleRangedAttack
			: return;
		case LimitManaMin
			: return;
		case ShieldEquipDmgMod
			: return;
		case ManaBurn
			: return;
		case PersistentEffect
			: return;
		case IncreaseTrapCount
			: return;
		case AdditionalAura
			: return;
		case DeactivateAllTraps
			: return;
		case LearnTrap
			: return;
		case ChangeTriggerType
			: return;
		case FcMute
			: return;
		case CurrentManaOnce
			: return;
		case PassiveSenseTrap
			: return;
		case ProcOnKillShot
			: return;
		case SpellOnDeath
			: return;
		case PotionBeltSlots
			: return;
		case BandolierSlots
			: return;
		case TripleAttackChance
			: return;
		case ProcOnSpellKillShot
			: return;
		case GroupShielding
			: return;
		case SetBodyType
			: return;
		case FactionMod
			: return;
		case CorruptionCounter
			: return;
		case ResistCorruption
			: return;
		case AttackSpeed4
			: return;
		case ForageSkill
			: return;
		case CastOnFadeEffectAlways
			: return;
		case ApplyEffect
			: return;
		case DotCritDmgIncrease
			: return;
		case Fling
			: return;
		case CastOnFadeEffectNPC
			: return;
		case SpellEffectResistChance
			: return;
		case ShadowStepDirectional
			: return;
		case Knockdown
			: return;
		case KnockTowardCaster
			: return;
		case NegateSpellEffect
			: return;
		case SympatheticProc
			: return;
		case Leap
			: return;
		case LimitSpellGroup
			: return;
		case CastOnCurer
			: return;
		case CastOnCure
			: return;
		case SummonCorpseZone
			: return;
		case FcTimerRefresh
			: return;
		case FcTimerLockout
			: return;
		case LimitManaMax
			: return;
		case FcHealAmt
			: return;
		case FcHealPctIncoming
			: return;
		case FcHealAmtIncoming
			: return;
		case FcHealPctCritIncoming
			: return;
		case FcHealAmtCrit
			: return;
		case PetMeleeMitigation
			: return;
		case SwarmPetDuration
			: return;
		case FcTwincast
			: return;
		case HealGroupFromMana
			: return;
		case ManaDrainWithDmg
			: return;
		case EndDrainWithDmg
			: return;
		case LimitSpellClass
			: return;
		case LimitSpellSubclass
			: return;
		case TwoHandBluntBlock
			: return;
		case CastonNumHitFade
			: return;
		case CastonFocusEffect
			: return;
		case LimitHPPercent
			: return;
		case LimitManaPercent
			: return;
		case LimitEndPercent
			: return;
		case LimitClass
			: return;
		case LimitRace
			: return;
		case FcBaseEffects
			: return;
		case LimitCastingSkill
			: return;
		case FFItemClass
			: return;
		case ACv2
			: return;
		case ManaRegen_v2
			: return;
		case SkillDamageAmount2
			: return;
		case AddMeleeProc
			: return;
		case FcLimitUse
			: return;
		case FcIncreaseNumHits
			: return;
		case LimitUseMin
			: return;
		case LimitUseType
			: return;
		case GravityEffect
			: return;
		case Display
			: return;
		case IncreaseExtTargetWindow
			: return;
		case SkillProc
			: return;
		case LimitToSkill
			: return;
		case SkillProcSuccess
			: return;
		case PostEffect
			: return;
		case PostEffectData
			: return;
		case ExpandMaxActiveTrophyBen
			: return;
		case CriticalDotDecay
			: return;
		case CriticalHealDecay
			: return;
		case CriticalRegenDecay
			: return;
		case BeneficialCountDownHold
			: return;
		case TeleporttoAnchor
			: return;
		case TranslocatetoAnchor
			: return;
		case Assassinate
			: return;
		case FinishingBlowLvl
			: return;
		case DistanceRemoval
			: return;
		case TriggerOnReqTarget
			: return;
		case TriggerOnReqCaster
			: return;
		case ImprovedTaunt
			: return;
		case AddMercSlot
			: return;
		case AStacker
			: return;
		case BStacker
			: return;
		case CStacker
			: return;
		case DStacker
			: return;
		case MitigateDotDamage
			: return;
		case MeleeThresholdGuard
			: return;
		case SpellThresholdGuard
			: return;
		case TriggerMeleeThreshold
			: return;
		case TriggerSpellThreshold
			: return;
		case AddHatePct
			: return;
		case AddHateOverTimePct
			: return;
		case ResourceTap
			: return;
		case FactionModPct
			: return;
		case DamageModifier2
			: return;
		case Ff_Override_NotFocusable
			: return;
		case ImprovedDamage2
			: return;
		case FcDamageAmt2
			: return;
		case Shield_Target
			: return;
		case PC_Pet_Rampage
			: return;
		case PC_Pet_AE_Rampage
			: return;
		case PC_Pet_Flurry_Chance
			: return;
		case DS_Mitigation_Amount
			: return;
		case DS_Mitigation_Percentage
			: return;
		case Chance_Best_in_Spell_Grp
			: return;
		case SE_Trigger_Best_in_Spell_Grp
			: return;
		case Double_Melee_Round
			: return;
		default:
			return;
		}
	}

	private void applyBlind(SpellEffect spellEffect, ISoliniaSpell soliniaSpell) {
		getLivingEntity().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 6 * 20, 1));
	}

	private void applyInvisibility(SpellEffect spellEffect, ISoliniaSpell soliniaSpell) {
		getLivingEntity().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 6 * 20, 1));
	}

	private void applyWaterBreathing(SpellEffect spellEffect, ISoliniaSpell soliniaSpell) {
		getLivingEntity().addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 6 * 20, 1));
	}

	private void applyConfusion(SpellEffect spellEffect, ISoliniaSpell soliniaSpell) {
		getLivingEntity().addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 6 * 20, 1));
	}

	private void applyLevitateSpellEffect(SpellEffect spellEffect, ISoliniaSpell soliniaSpell) {
		getLivingEntity().addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 6 * 20, 1));
	}

	private void applyRootSpellEffect(SpellEffect spellEffect, ISoliniaSpell soliniaSpell) {
		getLivingEntity().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 6 * 20, 10));
	}

	private void applyBindAffinty(SpellEffect spellEffect, ISoliniaSpell soliniaSpell) {
		if (!isOwnerPlayer())
			return;
		
		Player player = (Player)getLivingEntity();
		player.setBedSpawnLocation(player.getLocation());
	}

	private void applyGate(SpellEffect spellEffect, ISoliniaSpell soliniaSpell) {
		if (!isOwnerPlayer())
			return;

		Player player = (Player)getLivingEntity();
		
		Location blocation = player.getBedSpawnLocation();
		if (blocation == null)
		{
			player.sendMessage("Could not teleport, you are not bound to a location");
			return;
		}
		
		player.teleport(blocation);
		getLivingEntity().getLocation().getWorld().playEffect(getLivingEntity().getLocation().add(0.5,0.5,0.5), Effect.POTION_BREAK, 7);
		getLivingEntity().getWorld().playSound(getLivingEntity().getLocation(), Sound.ITEM_CHORUS_FRUIT_TELEPORT,1, 0);
	}

	private void applyCurrentMpSpellEffect(SpellEffect spellEffect, ISoliniaSpell soliniaSpell) {
		if (!isOwnerPlayer())
			return;
		
		int mpToRemove = spellEffect.getBase();
		
		try
		{
			ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(Bukkit.getPlayer(this.getOwnerUuid()));
			
			int amount = (int) Math.round(solplayer.getMana()) + mpToRemove;
			if (amount > solplayer.getMaxMP()) {
				amount = (int) Math.round(solplayer.getMaxMP());
			}
			
			if (amount < 0)
				amount = 0;
			
			System.out.println("Changing MP: " + amount + " on player " + getLivingEntity().getUniqueId());
			solplayer.setMana(amount);
			getLivingEntity().getLocation().getWorld().playEffect(getLivingEntity().getLocation().add(0.5,0.5,0.5), Effect.POTION_BREAK, 7);
			getLivingEntity().getWorld().playSound(getLivingEntity().getLocation(), Sound.ITEM_CHORUS_FRUIT_TELEPORT,1, 0);
		} catch (CoreStateInitException e)
		{
			e.printStackTrace();
		}
	}

	private void applyMovementSpeedEffect(SpellEffect spellEffect, ISoliniaSpell soliniaSpell) {
		int normalize = spellEffect.getBase();
		// value is a percentage but we range from 1-5 (we can stretch to 10)
		normalize = normalize / 10;
		if (spellEffect.getBase() > 0)
		{
			getLivingEntity().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 6 * 20, normalize));
		} else {
			getLivingEntity().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 6 * 20, (normalize * -1)));
		}
		
	}

	private void applyCurrentHpSpellEffect(SpellEffect spellEffect, ISoliniaSpell soliniaSpell) {
		applyCurrentHpOnceSpellEffect(spellEffect, soliniaSpell);
	}

	private LivingEntity getLivingEntity() {
		if (isOwnerPlayer())
			return Bukkit.getPlayer(getOwnerUuid());
		
		if (Bukkit.getEntity(getOwnerUuid()) instanceof LivingEntity)
			return (LivingEntity)Bukkit.getEntity(getOwnerUuid());
		
		return null;
	}

	private void applyCurrentHpOnceSpellEffect(SpellEffect spellEffect, ISoliniaSpell soliniaSpell) {
		int hpToRemove = spellEffect.getBase();
		
		// Damage
		if (hpToRemove < 0)
		{
			getLivingEntity().damage(hpToRemove * -1, Bukkit.getEntity(getSourceUuid()));
			System.out.println("Changing HP: " + hpToRemove + " on entity " + getLivingEntity().getUniqueId());
		}
		// Heal
		else 
		{
			int amount = (int) Math.round(getLivingEntity().getHealth()) + hpToRemove;
			if (amount > getLivingEntity().getMaxHealth()) {
				amount = (int) Math.round(getLivingEntity().getMaxHealth());
			}
			
			if (amount < 0)
				amount = 0;
			System.out.println("Changing HP: " + amount + " on entity " + getLivingEntity().getUniqueId());
			getLivingEntity().setHealth(amount);
		}
		
		getLivingEntity().getLocation().getWorld().playEffect(getLivingEntity().getLocation().add(0.5,0.5,0.5), Effect.POTION_BREAK, 7);
		getLivingEntity().getWorld().playSound(getLivingEntity().getLocation(), Sound.ITEM_CHORUS_FRUIT_TELEPORT,1, 0);
	}

	public int getTicksLeft() {
		return ticksLeft;
	}

	public void setTicksLeft(int ticksLeft) {
		this.ticksLeft = ticksLeft;
	}

	public boolean isFirstRun() {
		return isFirstRun;
	}

	public void setFirstRun(boolean isFirstRun) {
		this.isFirstRun = isFirstRun;
	}

	public boolean isSourcePlayer() {
		return isSourcePlayer;
	}

	public void setSourcePlayer(boolean isSourcePlayer) {
		this.isSourcePlayer = isSourcePlayer;
	}
}
