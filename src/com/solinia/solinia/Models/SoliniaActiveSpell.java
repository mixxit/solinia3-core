package com.solinia.solinia.Models;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender.Spigot;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftCreature;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftLivingEntity;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Utils.Utils;

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_12_R1.DamageSource;
import net.minecraft.server.v1_12_R1.EntityDamageSource;
import net.minecraft.server.v1_12_R1.EntityInsentient;
import net.minecraft.server.v1_12_R1.EntityLiving;
import net.minecraft.server.v1_12_R1.PathEntity;

public class SoliniaActiveSpell {
	private int spellId;
	private int ticksLeft;
	private boolean isOwnerPlayer = false;
	private boolean isSourcePlayer = false;
	private UUID sourceUuid;
	private UUID ownerUuid;
	private boolean isFirstRun = true;
	private List<ActiveSpellEffect> activeSpellEffects = new ArrayList<ActiveSpellEffect>();
	
	public SoliniaActiveSpell(UUID owneruuid, int spellId, boolean isOwnerPlayer, UUID sourceuuid, boolean sourceIsPlayer, int ticksLeft) {
		setOwnerUuid(owneruuid);
		setOwnerPlayer(isOwnerPlayer);
		setSourceUuid(sourceuuid);
		this.setSourcePlayer(sourceIsPlayer);
		setSpellId(spellId);
		setTicksLeft(ticksLeft);
		setActiveSpellEffects();
	}

	private void setActiveSpellEffects() {
		activeSpellEffects = new ArrayList<ActiveSpellEffect>();
		
		try
		{
			ISoliniaLivingEntity solOwner = SoliniaLivingEntityAdapter.Adapt((LivingEntity)Bukkit.getEntity(ownerUuid));
			ISoliniaLivingEntity solSource = SoliniaLivingEntityAdapter.Adapt((LivingEntity)Bukkit.getEntity(sourceUuid));
			
			if (solOwner == null)
				return;
			
			if (solSource == null)
				return;
			
			for(SpellEffect spellEffect : getSpell().getBaseSpellEffects())
			{
				ActiveSpellEffect activeSpellEffect = new ActiveSpellEffect(getSpell(), spellEffect, solSource.getBukkitLivingEntity(), solOwner.getBukkitLivingEntity(), solSource.getLevel(), getTicksLeft());
				activeSpellEffects.add(activeSpellEffect);
			}
		} catch (CoreStateInitException e)
		{
			
		}
	}
	
	public List<ActiveSpellEffect> getActiveSpellEffects()
	{
		return activeSpellEffects;
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

	public ISoliniaSpell getSpell() {
		try {
			ISoliniaSpell soliniaSpell = StateManager.getInstance().getConfigurationManager().getSpell(getSpellId());
			return soliniaSpell;
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public int getSpellId() {
		return spellId;
	}

	public void setSpellId(int spellId) {
		this.spellId = spellId;
	}

	public void apply(Plugin plugin) {
		try {
			ISoliniaSpell soliniaSpell = StateManager.getInstance().getConfigurationManager().getSpell(getSpellId());
			if (soliniaSpell == null)
			{
				System.out.print("Spell not found");
				return;
			}

			Entity sourceEntity = Bukkit.getEntity(this.getSourceUuid());
			if (sourceEntity == null || (!(sourceEntity instanceof LivingEntity)))
				return;
			
			ISoliniaLivingEntity solsource = SoliniaLivingEntityAdapter.Adapt((LivingEntity)sourceEntity);
			if (solsource == null)
				return;
			
			if (isFirstRun)
			{
				if (soliniaSpell.getCastOnYou() != null && !soliniaSpell.getCastOnYou().equals("") && isOwnerPlayer)
				{
					Player player = Bukkit.getPlayer(getOwnerUuid());
					player.sendMessage("* " + ChatColor.GRAY + soliniaSpell.getCastOnYou());
				}
					
				if (soliniaSpell.getCastOnOther() != null && !soliniaSpell.getCastOnOther().equals(""))
					SoliniaLivingEntityAdapter.Adapt((LivingEntity) Bukkit.getEntity(getOwnerUuid())).emote(ChatColor.GRAY + "* " + this.getLivingEntity().getName() + soliniaSpell.getCastOnOther());
			}
				
			for (ActiveSpellEffect spellEffect : getActiveSpellEffects()) {
				applySpellEffect(plugin, spellEffect, soliniaSpell, isFirstRun, solsource.getLevel());
			}
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void applySpellEffect(Plugin plugin, SpellEffect spellEffect, ISoliniaSpell soliniaSpell, boolean isFirstRun, int casterLevel) {
		
		switch (spellEffect.getSpellEffectType()) {
		case CurrentHP:
			applyCurrentHpSpellEffect(spellEffect,soliniaSpell,casterLevel);
			return;
		case ArmorClass: 
			return;
		case ATK: 
			return;
		case MovementSpeed:
			applyMovementSpeedEffect(spellEffect,soliniaSpell,casterLevel);
			return;
		case STR
			: return;
		case DEX
			: return;
		case AGI
			: return;
		case STA: 
			if (isFirstRun && getLivingEntity() != null && getLivingEntity() instanceof Player)
			{
				try
				{
					ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player)getLivingEntity());
					if (solplayer != null)
					solplayer.updateMaxHp();
				} catch (CoreStateInitException e)
				{
					
				}
			}
			return;
		case INT
			: return;
		case WIS
			: return;
		case CHA
			: return;
		case AttackSpeed
			: return;
		case Invisibility: 
			applyInvisibility(spellEffect,soliniaSpell,casterLevel);
			return;
		case SeeInvis
			: return;
		case WaterBreathing: 
			applyWaterBreathing(spellEffect,soliniaSpell,casterLevel);
			return;
		case CurrentMana:
			applyCurrentMpSpellEffect(spellEffect,soliniaSpell,casterLevel);
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
			applyBlind(spellEffect,soliniaSpell,casterLevel);
			return;
		case Stun: 
			applyStunSpellEffect(spellEffect,soliniaSpell,casterLevel);
			return;
		case Charm
			: return;
		case Fear: 
			applyFear(spellEffect,soliniaSpell,casterLevel);
			return;
		case Stamina
			: return;
		case BindAffinity: 
			applyBindAffinty(spellEffect,soliniaSpell,casterLevel);
			return;
		case Gate:
			applyGate(spellEffect,soliniaSpell,casterLevel);
			return;
		case CancelMagic
			: return;
		case InvisVsUndead
			: return;
		case InvisVsAnimals
			: return;
		case ChangeFrenzyRad
			: return;
		case Mez: 
			applyMezSpellEffect(spellEffect,soliniaSpell,casterLevel);
			return;
		case SummonItem: 
			applySummonItem(spellEffect, soliniaSpell, casterLevel);
			return;
		case SummonPet: 
			applySummonPet(plugin, spellEffect,soliniaSpell,casterLevel);
			return;
		case Confuse: 
			applyConfusion(spellEffect,soliniaSpell,casterLevel);
			return;
		case DiseaseCounter: 
			applyDiseaseCounter(spellEffect,soliniaSpell,casterLevel);
			return;
		case PoisonCounter: 
			applyPoisonCounter(spellEffect,soliniaSpell,casterLevel);
			return;
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
		case ShadowStep: 
			applyShadowStep(spellEffect,soliniaSpell,casterLevel);
			return;
		case Berserk
			: return;
		case Lycanthropy
			: return;
		case Vampirism
			: return;
		case ResistFire: 
			// this is passive
			return;
		case ResistCold: 
			// this is passive
			return;
		case ResistPoison: 
			// this is passive
			return;
		case ResistDisease: 
			// this is passive
			return;
		case ResistMagic: 
			// this is passive
			return;
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
			applyLevitateSpellEffect(spellEffect,soliniaSpell,casterLevel);
			return;
		case Illusion: 
			applyConfusion(spellEffect,soliniaSpell,casterLevel);
			return;
		case DamageShield: 
			// This is passive
			return;
		case TransferItem
			: return;
		case Identify
			: return;
		case ItemID
			: return;
		case WipeHateList: 
			applyWipeHateList(spellEffect,soliniaSpell,casterLevel);
			return;
		case SpinTarget
			: return;
		case InfraVision: 
			applyVision(spellEffect,soliniaSpell,casterLevel);
			return;
		case UltraVision: 
			applyVision(spellEffect,soliniaSpell,casterLevel);
			return;
		case EyeOfZomm
			: return;
		case ReclaimPet
			: return;
		case TotalHP: 
			if (isFirstRun && getLivingEntity() != null && getLivingEntity() instanceof Player)
			{
				try
				{
					ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player)getLivingEntity());
					if (solplayer != null)
					solplayer.updateMaxHp();
				} catch (CoreStateInitException e)
				{
					
				}
			}
			return;
		case CorpseBomb
			: return;
		case NecPet: 
			applySummonPet(plugin, spellEffect,soliniaSpell,casterLevel);
			return;
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
			applyCurrentHpOnceSpellEffect(spellEffect,soliniaSpell,casterLevel);
			return;
		case EnchantLight
			: return;
		case Revive
			: return;
		case SummonPC
			: return;
		case Teleport: 
			applyTeleport(spellEffect,soliniaSpell,casterLevel);
			return;
		case TossUp
			: return;
		case WeaponProc: 
			applyProc(spellEffect, soliniaSpell,casterLevel);
			return;
		case Harmony
			: return;
		case MagnifyVision
			: return;
		case Succor
			: return;
		case ModelSize
			: return;
		case Cloak: 
			applyInvisibility(spellEffect, soliniaSpell,casterLevel);
			return;
		case SummonCorpse
			: return;
		case InstantHate: 
			applyTauntSpell(spellEffect, soliniaSpell,casterLevel);
			return;
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
			applyRootSpellEffect(spellEffect,soliniaSpell,casterLevel);
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
		case Teleport2: 
			applyTeleport(spellEffect, soliniaSpell,casterLevel);
			return;
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
		case IllusionCopy: 
			applyIllusion(spellEffect,soliniaSpell,casterLevel);
			return;
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
		case Amnesia: 
			applyWipeHateList(spellEffect, soliniaSpell,casterLevel);
			return;
		case Hate:
			applyTauntSpell(spellEffect, soliniaSpell,casterLevel);
			return;
		case SkillAttack
			: return;
		case FadingMemories: 
			applyWipeHateList(spellEffect, soliniaSpell,casterLevel);
			return;
		case StunResist
			: return;
		case StrikeThrough
			: return;
		case SkillDamageTaken
			: return;
		case CurrentEnduranceOnce
			: return;
		case Taunt: 
			applyTauntSpell(spellEffect,soliniaSpell,casterLevel);
			return;
		case ProcChance
			: return;
		case RangedProc
			: return;
		case IllusionOther: 
			applyIllusion(spellEffect,soliniaSpell,casterLevel);
			return;
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
		case SlayUndead: 
			return;
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
		case IllusionPersistence: 
			applyIllusion(spellEffect,soliniaSpell,casterLevel);
			return;
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
		case RootBreakChance: 
			return;
		case TrapCircumvention: 
			return;
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
		case Blank: 
			return;
		case ShieldDuration: 
			return;
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
		case IllusionaryTarget: 
			applyIllusion(spellEffect,soliniaSpell,casterLevel);
			return;
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
		case CurrentManaOnce: 
			applyCurrentMpSpellEffect(spellEffect,soliniaSpell,casterLevel);
			return;
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
		case ShadowStepDirectional: 
			applyShadowStep(spellEffect,soliniaSpell,casterLevel);
			return;
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
		case ManaRegen_v2: 
			return;
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

	private void applyFear(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		// run to a nearby mob
		try
		{
			if (!SoliniaLivingEntityAdapter.Adapt(getLivingEntity()).isPlayer())
			for(Entity nearbyEntity : getLivingEntity().getNearbyEntities(20, 20, 20))
			{
				((CraftCreature)getLivingEntity()).setTarget(null);
				((CraftCreature)getLivingEntity()).getHandle().getNavigation().a(nearbyEntity.getLocation().getX(), nearbyEntity.getLocation().getY(), nearbyEntity.getLocation().getZ(), 1.5);
				return;
			}
		} catch (CoreStateInitException e)
		{
			//
		}
	}

	private void applyProc(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		// do nothing, this occurs during attack events
	}

	private void applyVision(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		getLivingEntity().addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 6 * 20, 1));
	}

	private void applySummonItem(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		int itemId = spellEffect.getBase();
		try
		{
			ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(itemId);
			if (item == null)
				return;
			
			if (!item.isTemporary())
				return;
			
			Entity ownerEntity = Bukkit.getEntity(this.getOwnerUuid());
			if (ownerEntity == null)
				return;
			
			if (!(ownerEntity instanceof LivingEntity))
				return;
			
			ownerEntity.getWorld().dropItem(ownerEntity.getLocation(), item.asItemStack());
			
		} catch (CoreStateInitException e)
		{
			return;
		}
	}
	
	private void applyIllusion(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		DisguisePackage disguise = Utils.getDisguiseTypeFromDisguiseId(spellEffect.getBase());
		if (disguise.getDisguisetype() == null || disguise.getDisguisetype() == null || disguise.getDisguisetype().equals(DisguiseType.UNKNOWN))
		{
			System.out.println("Could not find illusion: " + spellEffect.getBase());
			return;
		}
		
		if (DisguiseAPI.isDisguised(getLivingEntity()))
		{
			Disguise dis = DisguiseAPI.getDisguise(getLivingEntity());
			if (dis instanceof PlayerDisguise)
			{
				if (disguise.getDisguisedata() != null && !disguise.getDisguisedata().equals(""))
				{
					if (((PlayerDisguise)dis).getSkin().equals(disguise.getDisguisedata()))
						return;
					
					// If we get here we can let the player change their skin as it doesnt match their existing player skin name
				} else {
					return;
				}
			} else {
				if (dis.getType().equals(disguise.getDisguisetype()))
					return;
			}
		}
		
		
			if (disguise.getDisguisetype().equals(DisguiseType.PLAYER))
			{
				String disguisename = disguise.getDisguisedata();
				if (disguisename == null || disguisename.equals(""))
					disguisename = "RomanPraetor";
				
				PlayerDisguise playerdisguise = new PlayerDisguise(getLivingEntity().getName(), disguisename);
				DisguiseAPI.disguiseEntity(getLivingEntity(), playerdisguise);
			} else {
				MobDisguise mob = new MobDisguise(disguise.getDisguisetype());
				DisguiseAPI.disguiseEntity(getLivingEntity(), mob);
			}
		
	}

	private void applySummonPet(Plugin plugin, SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		if (!isOwnerPlayer())
			return;
		
		try
		{
			StateManager.getInstance().getEntityManager().SpawnPet(plugin, Bukkit.getPlayer(getOwnerUuid()), soliniaSpell);
		} catch (CoreStateInitException e)
		{
			return;
		}
	}

	private void applyTauntSpell(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		if (!isOwnerPlayer())
			return;
		
		if (!(getLivingEntity() instanceof Creature))
			return;
		
		Creature creature = (Creature)getLivingEntity();
		
		Entity source = Bukkit.getEntity(getSourceUuid());
		if (source instanceof LivingEntity)
			creature.setTarget((LivingEntity)source);
	}

	private void applyTeleport(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		if (!isOwnerPlayer())
			return;
		
		String[] zonedata = soliniaSpell.getTeleportZone().split(",");
		// Dissasemble the value to ensure it is correct
		String world = zonedata[0];
		double x = Double.parseDouble(zonedata[1]);
		double y = Double.parseDouble(zonedata[2]);
		double z = Double.parseDouble(zonedata[3]);
		Location loc = new Location(Bukkit.getWorld(world),x,y,z);
		getLivingEntity().teleport(loc);
	}
	
	private void applyShadowStep(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		if (!isOwnerPlayer())
			return;
		
		try
		{
			Block block = getLivingEntity().getTargetBlock(null, soliniaSpell.getRange());
			if (block != null)
				getLivingEntity().teleport(block.getLocation());
		} catch (Exception e)
		{
			// out of world block
		}
	}

	private void applyBlind(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		getLivingEntity().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 6 * 20, 1));
	}

	private void applyInvisibility(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		getLivingEntity().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 6 * 20, 1));
	}

	private void applyWaterBreathing(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		getLivingEntity().addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 6 * 20, 1));
	}

	private void applyConfusion(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		getLivingEntity().addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 6 * 20, 1));
	}

	private void applyLevitateSpellEffect(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		getLivingEntity().addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 6 * 20, 1));
	}

	private void applyRootSpellEffect(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		getLivingEntity().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 6 * 20, 10));
	}

	private void applyWipeHateList(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		if (!(getLivingEntity() instanceof Creature))
			return;
		
		Creature creature = (Creature)getLivingEntity();
		creature.setTarget(null);
	}

	private void applyStunSpellEffect(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		if (!(getLivingEntity() instanceof Creature))
			return;
		
		getLivingEntity().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 6 * 20, 10));
		getLivingEntity().addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 6 * 20, 1));
	}
	
	private void applyMezSpellEffect(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		if (!(getLivingEntity() instanceof Creature))
			return;
		
		try
		{
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.SECOND, 6);
			java.util.Date expire = calendar.getTime();
			Timestamp expiretimestamp = new Timestamp(expire.getTime());
			
			Creature creature = (Creature)getLivingEntity();
			
			StateManager.getInstance().getEntityManager().addMezzed(getLivingEntity(), expiretimestamp);
			
			creature.setTarget(null);
			getLivingEntity().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 6 * 20, 10));
			getLivingEntity().addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 6 * 20, 1));
		} catch (CoreStateInitException e)
		{
			return;
		}
	}
	
	private void applyBindAffinty(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		if (!isOwnerPlayer())
			return;
		
		Player player = (Player)getLivingEntity();
		player.setBedSpawnLocation(player.getLocation(), true);
	}

	private void applyGate(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
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
	}

	private void applyCurrentMpSpellEffect(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		if (!isOwnerPlayer())
			return;
		
		int mpToRemove = spellEffect.getBase();
		
		try
		{
			ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(Bukkit.getPlayer(this.getOwnerUuid()));
			ISoliniaLivingEntity solentity = SoliniaLivingEntityAdapter.Adapt(Bukkit.getPlayer(this.getOwnerUuid()));
			
			int amount = (int) Math.round(solplayer.getMana()) + mpToRemove;
			if (amount > solentity.getMaxMP()) {
				amount = (int) Math.round(solentity.getMaxMP());
			}
			
			if (amount < 0)
				amount = 0;
			
			solplayer.setMana(amount);
		} catch (CoreStateInitException e)
		{
			e.printStackTrace();
		}
	}

	private void applyMovementSpeedEffect(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
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

	private void applyCurrentHpSpellEffect(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		applyCurrentHpOnceSpellEffect(spellEffect, soliniaSpell, casterLevel);
	}

	private LivingEntity getLivingEntity() {
		if (isOwnerPlayer())
			return Bukkit.getPlayer(getOwnerUuid());
		
		if (Bukkit.getEntity(getOwnerUuid()) instanceof LivingEntity)
			return (LivingEntity)Bukkit.getEntity(getOwnerUuid());
		
		return null;
	}
	
	private void applyPoisonCounter(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		if (!soliniaSpell.isCure())
			return;
		
		try
		{
			StateManager.getInstance().getEntityManager().clearEntityFirstEffectOfType(getLivingEntity(),SpellEffectType.PoisonCounter);
			if (isOwnerPlayer())
			{
				Player player = (Player)Bukkit.getPlayer(getOwnerUuid());
				if (player != null)
				{
					if (player.hasPotionEffect(PotionEffectType.POISON))
						player.removePotionEffect(PotionEffectType.POISON);
					if (player.hasPotionEffect(PotionEffectType.HUNGER))
						player.removePotionEffect(PotionEffectType.HUNGER);
					player.sendMessage(ChatColor.GRAY + "* You have been cured of some poison");
				}
			}
			if (isSourcePlayer())
			{
				Player player = (Player)Bukkit.getPlayer(getSourceUuid());
				if (player != null)
				player.sendMessage(ChatColor.GRAY + "* You cured your target of some poison");
			}
		} catch (CoreStateInitException e)
		{
			return;
		}
	}

	private void applyDiseaseCounter(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		if (!soliniaSpell.isCure())
			return;

		try
		{
			StateManager.getInstance().getEntityManager().clearEntityFirstEffectOfType(getLivingEntity(),SpellEffectType.DiseaseCounter);
			if (isOwnerPlayer())
			{
				Player player = (Player)Bukkit.getPlayer(getOwnerUuid());
				if (player != null)
				player.sendMessage(ChatColor.GRAY + "* You have been cured of some disease");
			}
			if (isSourcePlayer())
			{
				Player player = (Player)Bukkit.getPlayer(getSourceUuid());
				if (player != null)
				player.sendMessage(ChatColor.GRAY + "* You cured your target of some poison");
			}
		} catch (CoreStateInitException e)
		{
			return;
		}
	}

	private void applyCurrentHpOnceSpellEffect(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int caster_level) {
		if (getLivingEntity().isDead())
			return;
		
		if (Bukkit.getEntity(getSourceUuid()) == null)
			return;
		
		Entity sourceEntity = Bukkit.getEntity(getSourceUuid());
		if (sourceEntity == null)
			return;
		
		if(!(sourceEntity instanceof LivingEntity))
			return;
		
		LivingEntity sourceLivingEntity = (LivingEntity)sourceEntity;
		
		// HP spells also get calculated based on the caster and the recipient
		int hpToRemove = soliniaSpell.calcSpellEffectValue(spellEffect, sourceLivingEntity, getLivingEntity(), caster_level, getTicksLeft());		
		
		// Damage
		if (hpToRemove < 0)
		{
			hpToRemove = hpToRemove * -1;
			EntityDamageSource source = new EntityDamageSource("thorns", ((CraftEntity)Bukkit.getEntity(getSourceUuid())).getHandle());
			source.setMagic();
			source.ignoresArmor();
			
			((CraftEntity)getLivingEntity()).getHandle().damageEntity(source, hpToRemove);
			//getLivingEntity().damage(hpToRemove, Bukkit.getEntity(getSourceUuid()));
			if (soliniaSpell.isLifetapSpell())
			{
				
				
				if (!(sourceEntity instanceof LivingEntity))
					return;
				
				int amount = (int) Math.round(sourceLivingEntity.getHealth()) + hpToRemove;
				if (amount > sourceLivingEntity.getMaxHealth()) {
					amount = (int) Math.round(sourceLivingEntity.getMaxHealth());
				}
				
				if (amount < 0)
					amount = 0;
				sourceLivingEntity.setHealth(amount);
			}
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
			getLivingEntity().setHealth(amount);
		}
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
