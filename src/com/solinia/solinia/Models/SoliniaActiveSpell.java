package com.solinia.solinia.Models;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Horse.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.solinia.solinia.Adapters.SoliniaItemAdapter;
import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaItemException;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Utils.ChatUtils;
import com.solinia.solinia.Utils.EntityUtils;
import com.solinia.solinia.Utils.ItemStackUtils;
import com.solinia.solinia.Utils.MathUtils;
import com.solinia.solinia.Utils.PlayerUtils;
import com.solinia.solinia.Utils.Utils;

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MiscDisguise;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;
import net.md_5.bungee.api.ChatColor;
import com.solinia.solinia.Utils.SkillUtils;
import com.solinia.solinia.Utils.SpellUtils;
public class SoliniaActiveSpell {
	private int spellId;
	private int ticksLeft;
	private boolean isOwnerPlayer = false;
	private boolean isSourcePlayer = false;
	private UUID sourceUuid;
	private UUID ownerUuid;
	private boolean isFirstRun = true;
	private List<ActiveSpellEffect> activeSpellEffects = new ArrayList<ActiveSpellEffect>();
	private int numHits = 0;
	private String requiredWeaponSkillType = "";
	private boolean isRacialPassive = false;
	public int rootBreakChance = 100;

	public SoliniaActiveSpell(UUID owneruuid, int spellId, boolean isOwnerPlayer, UUID sourceuuid,
			boolean sourceIsPlayer, int ticksLeft, int numHits, String requiredWeaponSkillType, boolean racialPassive,
			int rootBreakChance) {
		setOwnerUuid(owneruuid);
		setOwnerPlayer(isOwnerPlayer);
		setSourceUuid(sourceuuid);
		this.setSourcePlayer(sourceIsPlayer);
		setSpellId(spellId);
		setTicksLeft(ticksLeft);
		setActiveSpellEffects();
		setNumHits(numHits);
		setRequiredWeaponSkillType(requiredWeaponSkillType);
		setRacialPassive(racialPassive);
		setRootBreakChance(rootBreakChance);
	}

	private void setActiveSpellEffects() {
		activeSpellEffects = new ArrayList<ActiveSpellEffect>();

		try {
			if (Bukkit.getEntity(ownerUuid) == null)
				return;

			ISoliniaLivingEntity solOwner = SoliniaLivingEntityAdapter
					.Adapt((LivingEntity) Bukkit.getEntity(ownerUuid));

			if (Bukkit.getEntity(sourceUuid) == null)
				return;

			ISoliniaLivingEntity solSource = SoliniaLivingEntityAdapter
					.Adapt((LivingEntity) Bukkit.getEntity(sourceUuid));

			if (solOwner == null)
				return;

			if (solSource == null)
				return;

			for (SpellEffect spellEffect : getSpell().getBaseSpellEffects()) {
				ActiveSpellEffect activeSpellEffect = new ActiveSpellEffect(getSpell(), spellEffect,
						solSource.getBukkitLivingEntity(), solOwner.getBukkitLivingEntity(),
						solSource.getEffectiveLevel(true), getTicksLeft(), solSource.getInstrumentMod(getSpell()));
				activeSpellEffects.add(activeSpellEffect);
			}
		} catch (CoreStateInitException e) {

		}
	}

	public List<ActiveSpellEffect> getActiveSpellEffects() {
		return activeSpellEffects;
	}

	public boolean isOwnerPlayer() {
		return isOwnerPlayer;
	}

	public void setOwnerPlayer(boolean isOwnerPlayer) {
		this.isOwnerPlayer = isOwnerPlayer;
	}

	public void setRootBreakChance(int rootBreakChance) {
		this.rootBreakChance = rootBreakChance;
	}

	public int getRootBreakChance() {
		return rootBreakChance;
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

	public int getSourceLevel() {
		Entity sourceEntity = Bukkit.getEntity(this.getSourceUuid());
		if (sourceEntity == null || (!(sourceEntity instanceof LivingEntity)))
			return 1;

		try {
			ISoliniaLivingEntity solsource = SoliniaLivingEntityAdapter.Adapt((LivingEntity) sourceEntity);
			if (solsource == null)
				return 1;

			return solsource.getEffectiveLevel(true);
		} catch (CoreStateInitException e) {

		}

		return 1;
	}

	public void apply(Plugin plugin, boolean sendMessages) {
		try {
			ISoliniaSpell soliniaSpell = StateManager.getInstance().getConfigurationManager().getSpell(getSpellId());
			if (soliniaSpell == null) {
				System.out.print("Spell not found");
				return;
			}

			Entity sourceEntity = Bukkit.getEntity(this.getSourceUuid());
			if (sourceEntity == null || (!(sourceEntity instanceof LivingEntity)))
				return;

			ISoliniaLivingEntity solsource = SoliniaLivingEntityAdapter.Adapt((LivingEntity) sourceEntity);
			if (solsource == null)
				return;

			if (isFirstRun) {
				if (soliniaSpell.getCastOnYou() != null && !soliniaSpell.getCastOnYou().equals("") && isOwnerPlayer) {
					Player player = Bukkit.getPlayer(getOwnerUuid());
					if (soliniaSpell.isBardSong()) {
						if (sendMessages) {
							ChatUtils.SendHint(player, HINT.CAST_ON_YOU_SONG, soliniaSpell.getCastOnYou(), false);
						}
					} else {
						if (sendMessages)
							ChatUtils.SendHint(player, HINT.CAST_ON_YOU, soliniaSpell.getCastOnYou(), false);
					}
				}

				if (soliniaSpell.getCastOnOther() != null && !soliniaSpell.getCastOnOther().equals("")) {
					if (sendMessages) {
						if (soliniaSpell.isBardSong())
							ChatUtils.SendHint((LivingEntity) Bukkit.getEntity(getOwnerUuid()), HINT.CAST_ON_OTHER_SONG,
									this.getLivingEntity().getCustomName() + "^" + soliniaSpell.getCastOnOther(), true);
						else
							ChatUtils.SendHint((LivingEntity) Bukkit.getEntity(getOwnerUuid()), HINT.CAST_ON_OTHER,
									this.getLivingEntity().getCustomName() + "^" + soliniaSpell.getCastOnOther(), true);
					}
				}
			}

			for (ActiveSpellEffect spellEffect : getActiveSpellEffects()) {
				applySpellEffect(plugin, spellEffect, soliniaSpell, isFirstRun, solsource.getEffectiveLevel(true),
						sendMessages);
			}

			if (soliniaSpell.getRecourseLink() > 0) {
				ISoliniaSpell soliniaRecourseSpell = StateManager.getInstance().getConfigurationManager()
						.getSpell(soliniaSpell.getRecourseLink());
				if (soliniaRecourseSpell == null) {
					System.out.print("Recourse Spell not found");
					return;
				}

				applyRecourseSpell(solsource, soliniaRecourseSpell, requiredWeaponSkillType);
			}

		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void applyRecourseSpell(ISoliniaLivingEntity solsource, ISoliniaSpell soliniaRecourseSpell,
			String requiredWeaponSkillType2) {
		try {
			StateManager.getInstance().getEntityManager().addActiveEntitySpell(solsource.getBukkitLivingEntity(),
					soliniaRecourseSpell, solsource.getBukkitLivingEntity(), true, requiredWeaponSkillType2);
		} catch (CoreStateInitException e) {

		}
	}

	private void applySpellEffect(Plugin plugin, SpellEffect spellEffect, ISoliniaSpell soliniaSpell,
			boolean isFirstRun, int casterLevel, boolean sendMessages) {

		switch (spellEffect.getSpellEffectType()) {
		case CurrentHP:
			applyCurrentHpSpellEffect(spellEffect, soliniaSpell, casterLevel);
			return;
		case ArmorClass:
			return;
		case ATK:
			return;
		case MovementSpeed:
			applyMovementSpeedEffect(spellEffect, soliniaSpell, casterLevel);
			return;
		case STR:
			return;
		case DEX:
			return;
		case AGI:
			return;
		case STA:
			if (isFirstRun && getLivingEntity() != null)
				if (getLivingEntity() instanceof LivingEntity)
					try {
						ISoliniaLivingEntity solLivingEntity = SoliniaLivingEntityAdapter.Adapt(getLivingEntity());
						if (solLivingEntity != null)
							solLivingEntity.updateMaxHp();
					} catch (CoreStateInitException e) {

					}
			return;
		case INT:
			return;
		case WIS:
			return;
		case CHA:
			return;
		case AttackSpeed:
			applyAttackSpeed(spellEffect, soliniaSpell, casterLevel);
			return;
		case Invisibility:
			applyInvisibility(spellEffect, soliniaSpell, casterLevel);
			return;
		case SeeInvis:
			applySeeInvis(spellEffect, soliniaSpell, casterLevel);
			return;
		case WaterBreathing:
			applyWaterBreathing(spellEffect, soliniaSpell, casterLevel);
			return;
		case CurrentMana:
			applyCurrentMpSpellEffect(spellEffect, soliniaSpell, casterLevel);
			return;
		case NPCFrenzy:
			return;
		case NPCAwareness:
			return;
		case Lull:
			return;
		case AddFaction:
			applyAddFaction(spellEffect, soliniaSpell, casterLevel);
			return;
		case Blind:
			applyBlind(spellEffect, soliniaSpell, casterLevel);
			return;
		case Stun:
			applyStunSpellEffect(spellEffect, soliniaSpell, casterLevel);
			return;
		case Charm:
			applyCharm(spellEffect, soliniaSpell, casterLevel);
			return;
		case Fear:
			applyFear(spellEffect, soliniaSpell, casterLevel);
			return;
		case Stamina:
			return;
		case BindAffinity:
			applyBindAffinty(spellEffect, soliniaSpell, casterLevel);
			return;
		case GateToHomeCity:
			applyGateHome(spellEffect, soliniaSpell, casterLevel);
			return;
		case Gate:
			applyGate(spellEffect, soliniaSpell, casterLevel);
			return;
		case CancelMagic:
			applyCancelMagic(plugin, spellEffect, soliniaSpell, casterLevel);
			return;
		case InvisVsUndead:
			return;
		case InvisVsAnimals:
			return;
		case ChangeFrenzyRad:
			return;
		case Mez:
			applyMezSpellEffect(spellEffect, soliniaSpell, casterLevel);
			return;
		case SummonItem:
			for (int i = 0; i < spellEffect.getFormula(); i++) {
				applySummonItem(spellEffect, soliniaSpell, casterLevel);
			}
			return;
		case SummonPet:
			applySummonPet(plugin, spellEffect, soliniaSpell, casterLevel);
			return;
		case Confuse:
			applyConfusion(spellEffect, soliniaSpell, casterLevel);
			return;
		case DiseaseCounter:
			applyDiseaseCounter(plugin, spellEffect, soliniaSpell, casterLevel);
			return;
		case PoisonCounter:
			applyPoisonCounter(plugin, spellEffect, soliniaSpell, casterLevel);
			return;
		case DetectHostile:
			return;
		case DetectMagic:
			return;
		case DetectPoison:
			return;
		case DivineAura:
			return;
		case Destroy:
			return;
		case ShadowStep:
			applyShadowStep(spellEffect, soliniaSpell, casterLevel);
			return;
		case Berserk:
			return;
		case Lycanthropy:
			return;
		case Vampirism:
			return;
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
		case DetectTraps:
			return;
		case SenseDead:
			applySenseDead(spellEffect, soliniaSpell, casterLevel);
			return;
		case SenseSummoned:
			applySenseSummoned(spellEffect, soliniaSpell, casterLevel);
			return;
		case SenseAnimals:
			applySenseAnimal(spellEffect, soliniaSpell, casterLevel);
			return;
		case Rune:
			applyRune(spellEffect, soliniaSpell, casterLevel);
			return;
		case TrueNorth:
			applyTrueNorthSpellEffect(spellEffect, soliniaSpell, casterLevel);
			return;
		case Levitate:
			applyLevitateSpellEffect(spellEffect, soliniaSpell, casterLevel);
			return;
		case Illusion:
			applyIllusion(spellEffect, soliniaSpell, casterLevel);
			return;
		case DamageShield:
			// This is passive
			return;
		case TransferItem:
			return;
		case Identify:
			applyIdentify(spellEffect, soliniaSpell, casterLevel);
			return;
		case ItemID:
			return;
		case WipeHateList:
			applyWipeHateList(spellEffect, soliniaSpell, casterLevel);
			return;
		case SpinTarget:
			applySpinTarget(spellEffect, soliniaSpell, casterLevel);
			return;
		case InfraVision:
			applyVision(spellEffect, soliniaSpell, casterLevel);
			return;
		case UltraVision:
			applyVision(spellEffect, soliniaSpell, casterLevel);
			return;
		case EyeOfZomm:
			return;
		case ReclaimPet:
			applyReclaimPet(spellEffect, soliniaSpell, casterLevel);
			return;
		case TotalHP:
			if (isFirstRun && getLivingEntity() != null)
				if (getLivingEntity() instanceof LivingEntity)
					try {
						ISoliniaLivingEntity solLivingEntity = SoliniaLivingEntityAdapter.Adapt(getLivingEntity());
						if (solLivingEntity != null)
							solLivingEntity.updateMaxHp();
					} catch (CoreStateInitException e) {

					}
			return;
		case CorpseBomb:
			return;
		case NecPet:
			applySummonPet(plugin, spellEffect, soliniaSpell, casterLevel);
			return;
		case PreserveCorpse:
			return;
		case BindSight:
			return;
		case FeignDeath:
			applyFeignDeath(plugin, spellEffect, soliniaSpell, casterLevel);
			return;
		case VoiceGraft:
			return;
		case Sentinel:
			return;
		case LocateCorpse:
			return;
		case AbsorbMagicAtt:
			return;
		case CurrentHPOnce:
			if (!isFirstRun)
				return;

			applyCurrentHpOnceSpellEffect(spellEffect, soliniaSpell, casterLevel);
			return;
		case EnchantLight:
			return;
		case Revive:
			applyRevive(spellEffect, soliniaSpell, casterLevel);
			return;
		case SummonPC:
			applySummonGroup(spellEffect, soliniaSpell, casterLevel);
			return;
		case Teleport:
			if (getLivingEntity() instanceof Player)
				applyTeleport(spellEffect, soliniaSpell, casterLevel);
			return;
		case TossUp:
			applyTossUpEffect(spellEffect, soliniaSpell, casterLevel);
			return;
		case WeaponProc:
			applyProc(spellEffect, soliniaSpell, casterLevel);
			return;
		case Harmony:
			return;
		case MagnifyVision:
			return;
		case Succor:
			if (getLivingEntity() instanceof Player)
				applySuccor(spellEffect, soliniaSpell, casterLevel);
			return;
		case ModelSize:
			return;
		case Cloak:
			applyInvisibility(spellEffect, soliniaSpell, casterLevel);
			return;
		case SummonCorpse:
			return;
		case InstantHate:
			applyTauntSpell(spellEffect, soliniaSpell, casterLevel);
			return;
		case StopRain:
			applyStopRain(spellEffect, soliniaSpell, casterLevel);
			return;
		case NegateIfCombat:
			return;
		case Sacrifice:
			return;
		case Silence:
			return;
		case ManaPool:
			return;
		case AttackSpeed2:
			applyAttackSpeed(spellEffect, soliniaSpell, casterLevel);
			return;
		case Root:
			applyRootSpellEffect(spellEffect, soliniaSpell, casterLevel);
			return;
		case HealOverTime:
			applyCurrentHpSpellEffect(spellEffect, soliniaSpell, casterLevel);
			return;
		case CompleteHeal:
			return;
		case Fearless:
			return;
		case CallPet:
			return;
		case Translocate:
			if (getLivingEntity() instanceof Player)
				applyTeleport(spellEffect, soliniaSpell, casterLevel);
			return;
		case AntiGate:
			return;
		case SummonBSTPet:
			applySummonPet(plugin, spellEffect, soliniaSpell, casterLevel);
			return;
		case AlterNPCLevel:
			return;
		case Familiar:
			return;
		case SummonItemIntoBag:
			// handled by summonitem
			return;
		case IncreaseArchery:
			return;
		case ResistAll:
			return;
		case CastingLevel:
			return;
		case SummonHorse:
			if (getLivingEntity() instanceof Player)
				applySummonHorse(spellEffect, soliniaSpell, casterLevel);
			return;
		case ChangeAggro:
			return;
		case Hunger:
			if (getLivingEntity() instanceof Player)
				applyFoodDrink(spellEffect, soliniaSpell, casterLevel);
			return;
		case CurseCounter:
			return;
		case MagicWeapon:
			return;
		case Amplification:
			return;
		case AttackSpeed3:
			applyAttackSpeed(spellEffect, soliniaSpell, casterLevel);
			return;
		case HealRate:
			return;
		case ReverseDS:
			return;
		case ReduceSkill:
			return;
		case Screech:
			return;
		case ImprovedDamage:
			return;
		case ImprovedHeal:
			return;
		case SpellResistReduction:
			return;
		case IncreaseSpellHaste:
			return;
		case IncreaseSpellDuration:
			return;
		case IncreaseRange:
			return;
		case SpellHateMod:
			return;
		case ReduceReagentCost:
			return;
		case ReduceManaCost:
			return;
		case FcStunTimeMod:
			return;
		case LimitMaxLevel:
			return;
		case LimitResist:
			return;
		case LimitTarget:
			return;
		case LimitEffect:
			return;
		case LimitSpellType:
			return;
		case LimitSpell:
			return;
		case LimitMinDur:
			return;
		case LimitInstant:
			return;
		case LimitMinLevel:
			return;
		case LimitCastTimeMin:
			return;
		case LimitCastTimeMax:
			return;
		case Teleport2:
			if (getLivingEntity() instanceof Player)
				applyTeleport(spellEffect, soliniaSpell, casterLevel);
			return;
		case ElectricityResist:
			return;
		case PercentalHeal:
			return;
		case StackingCommand_Block:
			return;
		case StackingCommand_Overwrite:
			return;
		case DeathSave:
			return;
		case SuspendPet:
			return;
		case TemporaryPets:
			return;
		case BalanceHP:
			return;
		case DispelDetrimental:
			return;
		case SpellCritDmgIncrease:
			return;
		case IllusionCopy:
			applyIllusion(spellEffect, soliniaSpell, casterLevel);
			return;
		case SpellDamageShield:
			return;
		case Reflect:
			return;
		case AllStats:
			return;
		case MakeDrunk:
			return;
		case MitigateSpellDamage:
			return;
		case MitigateMeleeDamage:
			return;
		case NegateAttacks:
			return;
		case AppraiseLDonChest:
			return;
		case DisarmLDoNTrap:
			return;
		case UnlockLDoNChest:
			return;
		case PetPowerIncrease:
			return;
		case MeleeMitigation:
			return;
		case CriticalHitChance:
			return;
		case SpellCritChance:
			return;
		case CrippBlowChance:
			return;
		case AvoidMeleeChance:
			return;
		case RiposteChance:
			return;
		case DodgeChance:
			return;
		case ParryChance:
			return;
		case DualWieldChance:
			return;
		case DoubleAttackChance:
			return;
		case MeleeLifetap:
			return;
		case AllInstrumentMod:
			return;
		case ResistSpellChance:
			return;
		case ResistFearChance:
			return;
		case HundredHands:
			return;
		case MeleeSkillCheck:
			return;
		case HitChance:
			return;
		case DamageModifier:
			return;
		case MinDamageModifier:
			return;
		case BalanceMana:
			return;
		case IncreaseBlockChance:
			return;
		case CurrentEndurance:
			return;
		case EndurancePool:
			return;
		case Amnesia:
			applyWipeHateList(spellEffect, soliniaSpell, casterLevel);
			return;
		case Hate:
			applyTauntSpell(spellEffect, soliniaSpell, casterLevel);
			return;
		case SkillAttack:
			applySkillAttack(spellEffect, soliniaSpell, casterLevel);
			return;
		case FadingMemories:
			applyWipeHateList(spellEffect, soliniaSpell, casterLevel);
			return;
		case StunResist:
			return;
		case StrikeThrough:
			return;
		case SkillDamageTaken:
			return;
		case CurrentEnduranceOnce:
			if (!isFirstRun)
				return;
			return;
		case Taunt:
			applyTauntSpell(spellEffect, soliniaSpell, casterLevel);
			return;
		case ProcChance:
			return;
		case RangedProc:
			return;
		case IllusionOther:
			applyIllusion(spellEffect, soliniaSpell, casterLevel);
			return;
		case MassGroupBuff:
			return;
		case GroupFearImmunity:
			return;
		case Rampage:
			return;
		case AETaunt:
			return;
		case FleshToBone:
			return;
		case PurgePoison:
			return;
		case DispelBeneficial:
			return;
		case PetShield:
			return;
		case AEMelee:
			return;
		case FrenziedDevastation:
			return;
		case PetMaxHP:
			return;
		case MaxHPChange:
			return;
		case PetAvoidance:
			return;
		case Accuracy:
			return;
		case HeadShot:
			return;
		case PetCriticalHit:
			return;
		case SlayUndead:
			return;
		case SkillDamageAmount:
			return;
		case Packrat:
			return;
		case BlockBehind:
			return;
		case DoubleRiposte:
			return;
		case GiveDoubleRiposte:
			return;
		case GiveDoubleAttack:
			return;
		case TwoHandBash:
			return;
		case ReduceSkillTimer:
			return;
		case ReduceFallDamage:
			return;
		case PersistantCasting:
			return;
		case ExtendedShielding:
			return;
		case StunBashChance:
			return;
		case DivineSave:
			return;
		case Metabolism:
			return;
		case ReduceApplyPoisonTime:
			return;
		case ChannelChanceSpells:
			return;
		case FreePet:
			return;
		case GivePetGroupTarget:
			return;
		case IllusionPersistence:
			applyIllusion(spellEffect, soliniaSpell, casterLevel);
			return;
		case FeignedCastOnChance:
			return;
		case StringUnbreakable:
			return;
		case ImprovedReclaimEnergy:
			applyReclaimPet(spellEffect, soliniaSpell, casterLevel);
			return;
		case IncreaseChanceMemwipe:
			return;
		case CharmBreakChance:
			return;
		case RootBreakChance:
			return;
		case TrapCircumvention:
			return;
		case SetBreathLevel:
			return;
		case RaiseSkillCap:
			return;
		case SecondaryForte:
			return;
		case SecondaryDmgInc:
			return;
		case SpellProcChance:
			return;
		case ConsumeProjectile:
			return;
		case FrontalBackstabChance:
			return;
		case FrontalBackstabMinDmg:
			return;
		case Blank:
			return;
		case ShieldDuration:
			return;
		case ShroudofStealth:
			return;
		case PetDiscipline:
			return;
		case TripleBackstab:
			return;
		case CombatStability:
			return;
		case AddSingingMod:
			return;
		case SongModCap:
			return;
		case RaiseStatCap:
			return;
		case TradeSkillMastery:
			return;
		case HastenedAASkill:
			return;
		case MasteryofPast:
			return;
		case ExtraAttackChance:
			return;
		case AddPetCommand:
			return;
		case ReduceTradeskillFail:
			return;
		case MaxBindWound:
			return;
		case BardSongRange:
			return;
		case BaseMovementSpeed:
			return;
		case CastingLevel2:
			return;
		case CriticalDoTChance:
			return;
		case CriticalHealChance:
			return;
		case CriticalMend:
			return;
		case Ambidexterity:
			return;
		case UnfailingDivinity:
			return;
		case FinishingBlow:
			return;
		case Flurry:
			return;
		case PetFlurry:
			return;
		case FeignedMinion:
			return;
		case ImprovedBindWound:
			return;
		case DoubleSpecialAttack:
			return;
		case LoHSetHeal:
			return;
		case NimbleEvasion:
			return;
		case FcDamageAmt:
			return;
		case SpellDurationIncByTic:
			return;
		case SkillAttackProc:
			return;
		case CastOnFadeEffect:
			return;
		case IncreaseRunSpeedCap:
			return;
		case Purify:
			return;
		case StrikeThrough2:
			return;
		case FrontalStunResist:
			return;
		case CriticalSpellChance:
			return;
		case ReduceTimerSpecial:
			return;
		case FcSpellVulnerability:
			return;
		case FcDamageAmtIncoming:
			return;
		case ChangeHeight:
			return;
		case WakeTheDead:
			return;
		case Doppelganger:
			return;
		case ArcheryDamageModifier:
			return;
		case FcDamagePctCrit:
			return;
		case FcDamageAmtCrit:
			return;
		case OffhandRiposteFail:
			return;
		case MitigateDamageShield:
			return;
		case ArmyOfTheDead:
			return;
		case Appraisal:
			applyAppraisal(spellEffect, soliniaSpell, casterLevel);
			return;
		case SuspendMinion:
			return;
		case GateCastersBindpoint:
			return;
		case ReduceReuseTimer:
			return;
		case LimitCombatSkills:
			return;
		case Sanctuary:
			return;
		case ForageAdditionalItems:
			return;
		case Invisibility2:
			return;
		case InvisVsUndead2:
			return;
		case ImprovedInvisAnimals:
			return;
		case ItemHPRegenCapIncrease:
			return;
		case ItemManaRegenCapIncrease:
			return;
		case CriticalHealOverTime:
			return;
		case ShieldBlock:
			return;
		case ReduceHate:
			return;
		case DefensiveProc:
			return;
		case HPToMana:
			return;
		case NoBreakAESneak:
			return;
		case SpellSlotIncrease:
			return;
		case MysticalAttune:
			return;
		case DelayDeath:
			return;
		case ManaAbsorbPercentDamage:
			return;
		case CriticalDamageMob:
			return;
		case Salvage:
			return;
		case SummonToCorpse:
			return;
		case CastOnRuneFadeEffect:
			return;
		case BardAEDot:
			return;
		case BlockNextSpellFocus:
			return;
		case IllusionaryTarget:
			applyIllusion(spellEffect, soliniaSpell, casterLevel);
			return;
		case PercentXPIncrease:
			return;
		case SummonAndResAllCorpses:
			return;
		case TriggerOnCast:
			return;
		case SpellTrigger:
			return;
		case ItemAttackCapIncrease:
			return;
		case ImmuneFleeing:
			return;
		case InterruptCasting:
			return;
		case ChannelChanceItems:
			return;
		case AssassinateLevel:
			return;
		case HeadShotLevel:
			return;
		case DoubleRangedAttack:
			return;
		case LimitManaMin:
			return;
		case ShieldEquipDmgMod:
			return;
		case ManaBurn:
			applyManaBurn(spellEffect, soliniaSpell, casterLevel);
			return;
		case PersistentEffect:
			return;
		case IncreaseTrapCount:
			return;
		case AdditionalAura:
			return;
		case DeactivateAllTraps:
			return;
		case LearnTrap:
			return;
		case ChangeTriggerType:
			return;
		case FcMute:
			return;
		case CurrentManaOnce:
			if (!isFirstRun)
				return;

			applyCurrentMpSpellEffect(spellEffect, soliniaSpell, casterLevel);
			return;
		case PassiveSenseTrap:
			return;
		case ProcOnKillShot:
			return;
		case SpellOnDeath:
			return;
		case PotionBeltSlots:
			return;
		case BandolierSlots:
			return;
		case TripleAttackChance:
			return;
		case ProcOnSpellKillShot:
			return;
		case GroupShielding:
			return;
		case SetBodyType:
			return;
		case FactionMod:
			return;
		case CorruptionCounter:
			return;
		case ResistCorruption:
			return;
		case AttackSpeed4:
			applyAttackSpeed(spellEffect, soliniaSpell, casterLevel);
			return;
		case ForageSkill:
			return;
		case CastOnFadeEffectAlways:
			return;
		case ApplyEffect:
			applyApplyEffect(spellEffect, soliniaSpell, casterLevel);
			return;
		case DotCritDmgIncrease:
			return;
		case Fling:
			return;
		case CastOnFadeEffectNPC:
			return;
		case SpellEffectResistChance:
			return;
		case ShadowStepDirectional:
			applyShadowStep(spellEffect, soliniaSpell, casterLevel);
			return;
		case Knockdown:
			applyKnockback(spellEffect, soliniaSpell, casterLevel);
			return;
		case KnockTowardCaster:
			return;
		case NegateSpellEffect:
			return;
		case SympatheticProc:
			return;
		case Leap:
			return;
		case LimitSpellGroup:
			return;
		case CastOnCurer:
			return;
		case CastOnCure:
			return;
		case SummonCorpseZone:
			return;
		case FcTimerRefresh:
			return;
		case FcTimerLockout:
			return;
		case LimitManaMax:
			return;
		case FcHealAmt:
			return;
		case FcHealPctIncoming:
			return;
		case FcHealAmtIncoming:
			return;
		case FcHealPctCritIncoming:
			return;
		case FcHealAmtCrit:
			return;
		case PetMeleeMitigation:
			return;
		case SwarmPetDuration:
			return;
		case FcTwincast:
			return;
		case HealGroupFromMana:
			return;
		case ManaDrainWithDmg:
			return;
		case EndDrainWithDmg:
			return;
		case LimitSpellClass:
			return;
		case LimitSpellSubclass:
			return;
		case TwoHandBluntBlock:
			return;
		case CastonNumHitFade:
			return;
		case CastonFocusEffect:
			return;
		case LimitHPPercent:
			return;
		case LimitManaPercent:
			return;
		case LimitEndPercent:
			return;
		case LimitClass:
			return;
		case LimitRace:
			return;
		case FcBaseEffects:
			return;
		case LimitCastingSkill:
			return;
		case FFItemClass:
			return;
		case ACv2:
			return;
		case ManaRegen_v2:
			return;
		case SkillDamageAmount2:
			return;
		case AddMeleeProc:
			return;
		case FcLimitUse:
			return;
		case FcIncreaseNumHits:
			return;
		case LimitUseMin:
			return;
		case LimitUseType:
			return;
		case GravityEffect:
			return;
		case Display:
			return;
		case IncreaseExtTargetWindow:
			return;
		case SkillProc:
			return;
		case LimitToSkill:
			return;
		case SkillProcSuccess:
			return;
		case PostEffect:
			return;
		case PostEffectData:
			return;
		case ExpandMaxActiveTrophyBen:
			return;
		case CriticalDotDecay:
			return;
		case CriticalHealDecay:
			return;
		case CriticalRegenDecay:
			return;
		case BeneficialCountDownHold:
			return;
		case TeleporttoAnchor:
			if (getLivingEntity() instanceof Player)
				applyTeleport(spellEffect, soliniaSpell, casterLevel);
			return;
		case TranslocatetoAnchor:
			return;
		case Assassinate:
			return;
		case FinishingBlowLvl:
			return;
		case DistanceRemoval:
			return;
		case TriggerOnReqTarget:
			return;
		case TriggerOnReqCaster:
			return;
		case ImprovedTaunt:
			return;
		case AddMercSlot:
			return;
		case AStacker:
			return;
		case BStacker:
			return;
		case CStacker:
			return;
		case DStacker:
			return;
		case MitigateDotDamage:
			return;
		case MeleeThresholdGuard:
			return;
		case SpellThresholdGuard:
			return;
		case TriggerMeleeThreshold:
			return;
		case TriggerSpellThreshold:
			return;
		case AddHatePct:
			return;
		case AddHateOverTimePct:
			return;
		case ResourceTap:
			return;
		case FactionModPct:
			return;
		case DamageModifier2:
			return;
		case Ff_Override_NotFocusable:
			return;
		case ImprovedDamage2:
			return;
		case FcDamageAmt2:
			return;
		case Shield_Target:
			return;
		case PC_Pet_Rampage:
			return;
		case PC_Pet_AE_Rampage:
			return;
		case PC_Pet_Flurry_Chance:
			return;
		case DS_Mitigation_Amount:
			return;
		case DS_Mitigation_Percentage:
			return;
		case Chance_Best_in_Spell_Grp:
			return;
		case SE_Trigger_Best_in_Spell_Grp:
			return;
		case Double_Melee_Round:
			return;
		case Backstab:
			applyBackstab(spellEffect, soliniaSpell, casterLevel);
			return;
		case Disarm:
			applyDisarm(spellEffect, soliniaSpell, casterLevel);
			return;
		case BindWound:
			applyBindWound(spellEffect, soliniaSpell, casterLevel);
			return;
		case Kick:
			applyKick(spellEffect, soliniaSpell, casterLevel);
			return;
		case CureVampirism:
			applyCureVampirism(spellEffect, soliniaSpell, casterLevel);
			return;
		case Mend:
			applyMend(spellEffect, soliniaSpell, casterLevel);
			return;
		case RoundKick:
			applyRoundKick(spellEffect, soliniaSpell, casterLevel);
			return;
		case TigerClaw:
			applyTigerClaw(spellEffect, soliniaSpell, casterLevel);
			return;
		case Intimidation:
			applyIntimidation(spellEffect, soliniaSpell, casterLevel);
			return;
		case EagleStrike:
			applyEagleStrike(spellEffect, soliniaSpell, casterLevel);
			return;
		case DragonPunch:
			applyDragonPunch(spellEffect, soliniaSpell, casterLevel);
			return;
		case TailRake:
			applyTailRake(spellEffect, soliniaSpell, casterLevel);
			return;
		case FlyingKick:
			applyFlyingKick(spellEffect, soliniaSpell, casterLevel);
			return;
		case SummonNPCID:
			applySummonNPCID(spellEffect, soliniaSpell, casterLevel);
			return;
		case FeatherFall:
			applyFeatherFallSpellEffect(spellEffect, soliniaSpell, casterLevel);
			return;
		case MiningHaste:
			applyMiningHasteSpellEffect(spellEffect, soliniaSpell, casterLevel);
			return;
		case Bash:
			applyBash(spellEffect, soliniaSpell, casterLevel);
			return;
		case Slam:
			applyBash(spellEffect, soliniaSpell, casterLevel);
			return;
		case Picklock:
			applyPicklock(spellEffect, soliniaSpell, casterLevel);
			return;
		case Frenzy:
			applyFrenzy(spellEffect, soliniaSpell, casterLevel);
			return;
		default:
			return;
		}
	}

	private void applyAppraisal(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		if (!this.isSourcePlayer())
			return;
		
		Entity sourceEntity = Bukkit.getEntity(getSourceUuid());
		if (sourceEntity == null)
			return;

		if (!(sourceEntity instanceof LivingEntity))
			return;
		
		LivingEntity sourceLivingEntity = (LivingEntity) sourceEntity;
		try {

			ISoliniaLivingEntity sourceSoliniaLivingEntity = SoliniaLivingEntityAdapter.Adapt(sourceLivingEntity);
			if (sourceSoliniaLivingEntity != null && getLivingEntity() != null) {
				
				ItemStack itemStack = getLivingEntity().getEquipment().getItemInMainHand();
				if (itemStack != null)
				{
					try
					{
						ISoliniaItem solItem = SoliniaItemAdapter.Adapt(itemStack);
						sourceLivingEntity.sendMessage("Looking at each item you believe a single one is worth: " + solItem.getWorth());
					} catch (CoreStateInitException e)
					{
					} catch (SoliniaItemException e) {
						if (ItemStackUtils.getAllowedVanillaItemStacks().contains(itemStack.getType()))
						{
							double individualprice = ItemStackUtils.getWorthOfVanillaMaterial(itemStack);
							sourceLivingEntity.sendMessage("Looking at each item you believe a single one is worth: " + individualprice + " 0 sols");
						} else {
							sourceLivingEntity.sendMessage("Looking at each item you believe a single one is worth: 0 sols");
						}
					}
				} else {
					sourceLivingEntity.sendMessage("Looking at each item you believe a single one is worth: 0 sols");
				}
			}
		} catch (CoreStateInitException e) {
			// just skip it
		}
	}

	private void applyApplyEffect(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		if (!this.isSourcePlayer())
			return;
		
		Entity sourceEntity = Bukkit.getEntity(getSourceUuid());
		if (sourceEntity == null)
			return;

		if (!(sourceEntity instanceof LivingEntity))
			return;
		
		LivingEntity sourceLivingEntity = (LivingEntity) sourceEntity;
		try {
			ISoliniaSpell spell = StateManager.getInstance().getConfigurationManager().getSpell(spellEffect.getBase());
			if (spell == null)
				return;

			ISoliniaLivingEntity sourceSoliniaLivingEntity = SoliniaLivingEntityAdapter.Adapt(sourceLivingEntity);
			ISoliniaLivingEntity targetSoliniaLivingEntity = SoliniaLivingEntityAdapter.Adapt(getLivingEntity());
			if (sourceSoliniaLivingEntity != null && targetSoliniaLivingEntity != null) {
				sourceSoliniaLivingEntity.SpellFinished(spellEffect.getBase2(), targetSoliniaLivingEntity, CastingSlot.Item, 0, -1, spell.getResistDiff());
			}
		} catch (CoreStateInitException e) {
			// just skip it
		}
	}

	private void applyCureVampirism(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		if (!this.isSourcePlayer())
			return;

		Entity sourceEntity = Bukkit.getEntity(getSourceUuid());
		if (sourceEntity == null)
			return;

		if (!(sourceEntity instanceof LivingEntity))
			return;

		LivingEntity sourceLivingEntity = (LivingEntity) sourceEntity;

		try {
			ISoliniaLivingEntity sourceSoliniaLivingEntity = SoliniaLivingEntityAdapter.Adapt(sourceLivingEntity);
			ISoliniaLivingEntity targetSoliniaLivingEntity = SoliniaLivingEntityAdapter.Adapt(getLivingEntity());
			if (sourceSoliniaLivingEntity != null && targetSoliniaLivingEntity != null) {
				targetSoliniaLivingEntity.cureVampirism();
			}
		} catch (CoreStateInitException e) {
			// just skip it
		}
	}

	private void applyAddFaction(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		// done via effects
	}

	private void applyPicklock(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		if (!this.isSourcePlayer())
			return;

		Entity sourceEntity = Bukkit.getEntity(getSourceUuid());
		if (sourceEntity == null)
			return;

		if (!(sourceEntity instanceof LivingEntity))
			return;

		// get targetted door
		sourceEntity.sendMessage("This ability is not currently active");
	}

	private void applySummonNPCID(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		if (!this.isSourcePlayer())
			return;

		Entity sourceEntity = Bukkit.getEntity(getSourceUuid());
		if (sourceEntity == null)
			return;

		if (!(sourceEntity instanceof LivingEntity))
			return;

		try {
			ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(spellEffect.getBase());

			if (npc != null) {

				npc.Spawn(sourceEntity.getLocation(), 1);
			} else {
				sourceEntity.sendMessage("Cannot find NPC by ID");
				return;
			}
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void applyRoundKick(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		if (!this.isSourcePlayer())
			return;

		Entity sourceEntity = Bukkit.getEntity(getSourceUuid());
		if (sourceEntity == null)
			return;

		if (!(sourceEntity instanceof LivingEntity))
			return;

		LivingEntity sourceLivingEntity = (LivingEntity) sourceEntity;

		if (sourceLivingEntity.getUniqueId().equals(getLivingEntity().getUniqueId()))
			return;

		try {
			ISoliniaLivingEntity sourceSoliniaLivingEntity = SoliniaLivingEntityAdapter.Adapt(sourceLivingEntity);
			ISoliniaLivingEntity targetSoliniaLivingEntity = SoliniaLivingEntityAdapter.Adapt(getLivingEntity());
			if (sourceSoliniaLivingEntity != null && targetSoliniaLivingEntity != null) {
				sourceSoliniaLivingEntity.doClassAttacks(targetSoliniaLivingEntity, SkillType.RoundKick, false);
			}
		} catch (CoreStateInitException e) {
			// just skip it
		}
	}

	private void applyDragonPunch(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		if (!this.isSourcePlayer())
			return;

		Entity sourceEntity = Bukkit.getEntity(getSourceUuid());
		if (sourceEntity == null)
			return;

		if (!(sourceEntity instanceof LivingEntity))
			return;

		LivingEntity sourceLivingEntity = (LivingEntity) sourceEntity;

		if (sourceLivingEntity.getUniqueId().equals(getLivingEntity().getUniqueId()))
			return;

		try {
			ISoliniaLivingEntity sourceSoliniaLivingEntity = SoliniaLivingEntityAdapter.Adapt(sourceLivingEntity);
			ISoliniaLivingEntity targetSoliniaLivingEntity = SoliniaLivingEntityAdapter.Adapt(getLivingEntity());
			if (sourceSoliniaLivingEntity != null && targetSoliniaLivingEntity != null) {
				sourceSoliniaLivingEntity.doClassAttacks(targetSoliniaLivingEntity, SkillType.DragonPunch, false);
			}
		} catch (CoreStateInitException e) {
			// just skip it
		}
	}

	private void applyEagleStrike(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		if (!this.isSourcePlayer())
			return;

		Entity sourceEntity = Bukkit.getEntity(getSourceUuid());
		if (sourceEntity == null)
			return;

		if (!(sourceEntity instanceof LivingEntity))
			return;

		LivingEntity sourceLivingEntity = (LivingEntity) sourceEntity;

		if (sourceLivingEntity.getUniqueId().equals(getLivingEntity().getUniqueId()))
			return;

		try {
			ISoliniaLivingEntity sourceSoliniaLivingEntity = SoliniaLivingEntityAdapter.Adapt(sourceLivingEntity);
			ISoliniaLivingEntity targetSoliniaLivingEntity = SoliniaLivingEntityAdapter.Adapt(getLivingEntity());
			if (sourceSoliniaLivingEntity != null && targetSoliniaLivingEntity != null) {
				sourceSoliniaLivingEntity.doClassAttacks(targetSoliniaLivingEntity, SkillType.EagleStrike, false);
			}
		} catch (CoreStateInitException e) {
			// just skip it
		}
	}

	private void applyFlyingKick(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		if (!this.isSourcePlayer())
			return;

		Entity sourceEntity = Bukkit.getEntity(getSourceUuid());
		if (sourceEntity == null)
			return;

		if (!(sourceEntity instanceof LivingEntity))
			return;

		LivingEntity sourceLivingEntity = (LivingEntity) sourceEntity;

		if (sourceLivingEntity.getUniqueId().equals(getLivingEntity().getUniqueId()))
			return;

		try {
			ISoliniaLivingEntity sourceSoliniaLivingEntity = SoliniaLivingEntityAdapter.Adapt(sourceLivingEntity);
			ISoliniaLivingEntity targetSoliniaLivingEntity = SoliniaLivingEntityAdapter.Adapt(getLivingEntity());
			if (sourceSoliniaLivingEntity != null && targetSoliniaLivingEntity != null) {
				sourceSoliniaLivingEntity.doClassAttacks(targetSoliniaLivingEntity, SkillType.FlyingKick, false);
			}
		} catch (CoreStateInitException e) {
			// just skip it
		}
	}

	private void applyTigerClaw(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		if (!this.isSourcePlayer())
			return;

		Entity sourceEntity = Bukkit.getEntity(getSourceUuid());
		if (sourceEntity == null)
			return;

		if (!(sourceEntity instanceof LivingEntity))
			return;

		LivingEntity sourceLivingEntity = (LivingEntity) sourceEntity;

		if (sourceLivingEntity.getUniqueId().equals(getLivingEntity().getUniqueId()))
			return;

		try {
			ISoliniaLivingEntity sourceSoliniaLivingEntity = SoliniaLivingEntityAdapter.Adapt(sourceLivingEntity);
			ISoliniaLivingEntity targetSoliniaLivingEntity = SoliniaLivingEntityAdapter.Adapt(getLivingEntity());
			if (sourceSoliniaLivingEntity != null && targetSoliniaLivingEntity != null) {
				sourceSoliniaLivingEntity.doClassAttacks(targetSoliniaLivingEntity, SkillType.TigerClaw, false);
			}
		} catch (CoreStateInitException e) {
			// just skip it
		}
	}

	private void applyTailRake(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		if (!this.isSourcePlayer())
			return;

		Entity sourceEntity = Bukkit.getEntity(getSourceUuid());
		if (sourceEntity == null)
			return;

		if (!(sourceEntity instanceof LivingEntity))
			return;

		LivingEntity sourceLivingEntity = (LivingEntity) sourceEntity;

		if (sourceLivingEntity.getUniqueId().equals(getLivingEntity().getUniqueId()))
			return;

		try {
			ISoliniaLivingEntity sourceSoliniaLivingEntity = SoliniaLivingEntityAdapter.Adapt(sourceLivingEntity);
			ISoliniaLivingEntity targetSoliniaLivingEntity = SoliniaLivingEntityAdapter.Adapt(getLivingEntity());
			if (sourceSoliniaLivingEntity != null && targetSoliniaLivingEntity != null) {
				sourceSoliniaLivingEntity.doClassAttacks(targetSoliniaLivingEntity, SkillType.TailRake, false);
			}
		} catch (CoreStateInitException e) {
			// just skip it
		}
	}

	private void applyMend(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		if (!this.isSourcePlayer())
			return;

		Entity sourceEntity = Bukkit.getEntity(getSourceUuid());
		if (sourceEntity == null)
			return;

		if (!(sourceEntity instanceof LivingEntity))
			return;

		LivingEntity sourceLivingEntity = (LivingEntity) sourceEntity;

		try {
			ISoliniaLivingEntity sourceSoliniaLivingEntity = SoliniaLivingEntityAdapter.Adapt(sourceLivingEntity);
			sourceSoliniaLivingEntity.doMend();
			return;

		} catch (CoreStateInitException e) {
			// just skip it
		}

	}

	private void applyStunSpellEffect(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		if (!this.isSourcePlayer())
			return;

		Entity sourceEntity = Bukkit.getEntity(getSourceUuid());
		if (sourceEntity == null)
			return;

		if (!(sourceEntity instanceof LivingEntity))
			return;

		LivingEntity sourceLivingEntity = (LivingEntity) sourceEntity;

		if (sourceLivingEntity.getUniqueId().equals(getLivingEntity().getUniqueId()))
			return;

		try {
			if (getLivingEntity().isDead() || sourceLivingEntity.isDead())
				return;

			ISoliniaLivingEntity caster = SoliniaLivingEntityAdapter.Adapt(sourceLivingEntity);
			ISoliniaLivingEntity target = SoliniaLivingEntityAdapter.Adapt(getLivingEntity());

			if (caster != null && target != null) {
				int effect_value = soliniaSpell.calcSpellEffectValue(spellEffect, sourceLivingEntity, getLivingEntity(),
						caster.getEffectiveLevel(true), this.getTicksLeft(), 0);
				// Typically we check for immunities else where but since stun immunities are
				// different and only
				// Block the stun part and not the whole spell, we do it here, also do the
				// message here so we wont get the message on a resist
				int max_level = spellEffect.getMax();
				// max_level of 0 means we assume a default of 55.
				if (max_level == 0)
					max_level = 55;
				// NPCs get to ignore max_level for their spells.
				// Ignore if spell is beneficial (ex. Harvest)
				if (soliniaSpell.isDetrimental() && (target.getSpecialAbility(SpecialAbility.UNSTUNABLE) > 0
						|| ((target.getMentorLevel() > max_level) && caster != null
								&& (!caster.isNPC() || (caster.isNPC()))))) {
					if (caster != null)
						caster.sendMessage("Target is immune to stun");
				} else {
					int stun_resist = target.getItemBonuses(SpellEffectType.StunResist)
							+ target.getSpellBonuses(SpellEffectType.StunResist);
					if (target.isPlayer())
						stun_resist += target.getAABonuses(SpellEffectType.StunResist);

					if (stun_resist <= 0 || MathUtils.RandomBetween(0, 99) >= stun_resist) {
						// Log(Logs::Detail, Logs::Combat, "Stunned. We had %d percent resist chance.",
						// stun_resist);

						if (caster != null && caster.isPlayer())
							effect_value += effect_value
									* caster.getFocusEffect(FocusEffect.FcStunTimeMod, this.getSpell()) / 100;

						target.Stun(effect_value);
					} else {
						if (target.isPlayer())
							target.sendMessage("You shake off the stun");

						// Log(Logs::Detail, Logs::Combat, "Stun Resisted. We had %d percent resist
						// chance.", stun_resist);
					}
				}
				return;
			}
		} catch (CoreStateInitException e) {
			// just skip it
		}
	}

	private void applyBash(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		if (!this.isSourcePlayer())
			return;

		Entity sourceEntity = Bukkit.getEntity(getSourceUuid());
		if (sourceEntity == null)
			return;

		if (!(sourceEntity instanceof LivingEntity))
			return;

		LivingEntity sourceLivingEntity = (LivingEntity) sourceEntity;

		if (sourceLivingEntity.getUniqueId().equals(getLivingEntity().getUniqueId()))
			return;

		try {
			ISoliniaLivingEntity sourceSoliniaLivingEntity = SoliniaLivingEntityAdapter.Adapt(sourceLivingEntity);
			ISoliniaLivingEntity targetSoliniaLivingEntity = SoliniaLivingEntityAdapter.Adapt(getLivingEntity());
			if (sourceSoliniaLivingEntity != null && targetSoliniaLivingEntity != null) {
				sourceSoliniaLivingEntity.doClassAttacks(targetSoliniaLivingEntity, SkillType.Bash, false);
			}
		} catch (CoreStateInitException e) {
			// just skip it
		}
	}

	private void applyFrenzy(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		if (!this.isSourcePlayer())
			return;

		Entity sourceEntity = Bukkit.getEntity(getSourceUuid());
		if (sourceEntity == null)
			return;

		if (!(sourceEntity instanceof LivingEntity))
			return;

		LivingEntity sourceLivingEntity = (LivingEntity) sourceEntity;

		if (sourceLivingEntity.getUniqueId().equals(getLivingEntity().getUniqueId()))
			return;

		try {
			ISoliniaLivingEntity sourceSoliniaLivingEntity = SoliniaLivingEntityAdapter.Adapt(sourceLivingEntity);
			ISoliniaLivingEntity targetSoliniaLivingEntity = SoliniaLivingEntityAdapter.Adapt(getLivingEntity());
			if (sourceSoliniaLivingEntity != null && targetSoliniaLivingEntity != null) {
				sourceSoliniaLivingEntity.doClassAttacks(targetSoliniaLivingEntity, SkillType.Frenzy, false);
			}
		} catch (CoreStateInitException e) {
			// just skip it
		}
	}

	private void applyKick(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		if (!this.isSourcePlayer())
			return;

		Entity sourceEntity = Bukkit.getEntity(getSourceUuid());
		if (sourceEntity == null)
			return;

		if (!(sourceEntity instanceof LivingEntity))
			return;

		LivingEntity sourceLivingEntity = (LivingEntity) sourceEntity;

		if (sourceLivingEntity.getUniqueId().equals(getLivingEntity().getUniqueId()))
			return;

		try {
			ISoliniaLivingEntity sourceSoliniaLivingEntity = SoliniaLivingEntityAdapter.Adapt(sourceLivingEntity);
			ISoliniaLivingEntity targetSoliniaLivingEntity = SoliniaLivingEntityAdapter.Adapt(getLivingEntity());
			if (sourceSoliniaLivingEntity != null && targetSoliniaLivingEntity != null) {
				sourceSoliniaLivingEntity.doClassAttacks(targetSoliniaLivingEntity, SkillType.Kick, false);
			}
		} catch (CoreStateInitException e) {
			// just skip it
		}

	}

	private void applyDisarm(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		if (!isOwnerPlayer())
			return;

		Entity sourceEntity = Bukkit.getEntity(getSourceUuid());
		if (sourceEntity == null)
			return;

		if (!(sourceEntity instanceof LivingEntity))
			return;

		if (!(getLivingEntity() instanceof Creature))
			return;

		LivingEntity sourceLivingEntity = (LivingEntity) sourceEntity;

		if (sourceLivingEntity.getUniqueId().equals(getLivingEntity().getUniqueId()))
			return;

		try {
			ISoliniaLivingEntity sourceSoliniaLivingEntity = SoliniaLivingEntityAdapter.Adapt(sourceLivingEntity);
			ISoliniaLivingEntity targetSoliniaLivingEntity = SoliniaLivingEntityAdapter.Adapt(getLivingEntity());
			if (sourceSoliniaLivingEntity != null && targetSoliniaLivingEntity != null) {
				sourceSoliniaLivingEntity.tryDisarm(targetSoliniaLivingEntity);
			}
		} catch (CoreStateInitException e) {
			// just skip it
		}
	}

	private void applySkillAttack(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		if (!(getLivingEntity() instanceof Creature))
			return;

		Entity source = Bukkit.getEntity(getSourceUuid());
		if (source instanceof LivingEntity) {
			try {
				int focus = 0;
				ISoliniaLivingEntity solLivingEntity = SoliniaLivingEntityAdapter.Adapt((LivingEntity) source);
				focus = solLivingEntity.getFocusEffect(FocusEffect.FcBaseEffects, soliniaSpell);
				int reuseTime = soliniaSpell.getRecastTime() + soliniaSpell.getRecoveryTime();
				solLivingEntity.doMeleeSkillAttackDmg(getLivingEntity(), spellEffect.getBase(),
						SkillUtils.getSkillType(soliniaSpell.getSkill()), spellEffect.getBase2(), focus, false, reuseTime);
			} catch (CoreStateInitException e) {

			}
		}
	}

	private void applyCharm(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		if (getLivingEntity() == null)
			return;

		if (getLivingEntity().isDead())
			return;

		if (!(getLivingEntity() instanceof LivingEntity))
			return;

		// Cannot be cast by NPCs
		Entity source = Bukkit.getEntity(getSourceUuid());
		if (!(source instanceof Player))
			return;

		// Not possible for players right now
		if (getLivingEntity() instanceof Player)
			return;

		try {
			// Already has a pet
			LivingEntity currentPet = StateManager.getInstance().getEntityManager().getPet(source.getUniqueId());
			if (currentPet != null)
				return;

			ISoliniaLivingEntity targetsolLivingEntity = SoliniaLivingEntityAdapter.Adapt(getLivingEntity());
			// Cannot charm a mob that is already a pet
			if (targetsolLivingEntity.isCurrentlyNPCPet())
				return;

			if (targetsolLivingEntity.getMentorLevel() > spellEffect.getMax()) {
				source.sendMessage("This is too high for this spell");
				return;
			}

			EntityUtils.dismountEntity(getLivingEntity());

			ISoliniaLivingEntity sourceSolLivingEntity = SoliniaLivingEntityAdapter.Adapt(getLivingEntity());
			sourceSolLivingEntity.InterruptSpell();

			StateManager.getInstance().getEntityManager().setPet(source.getUniqueId(), getLivingEntity());

			if (getLivingEntity() instanceof Creature) {
				sourceSolLivingEntity.setAttackTarget(null);
			}

			EntityUtils.dismountEntity(getLivingEntity());

			Entity vehicle = getLivingEntity().getVehicle();
			if (vehicle != null) {
				vehicle.eject();
			}

		} catch (CoreStateInitException e) {
			return;
		}

	}

	private void applyAttackSpeed(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		/*
		 * if (Utils.isSoliniaMob(getLivingEntity())) { MythicEntitySoliniaMob mob =
		 * Utils.GetSoliniaMob(getLivingEntity()); if (mob != null) { try {
		 * ISoliniaLivingEntity solEntity =
		 * SoliniaLivingEntityAdapter.Adapt(getLivingEntity());
		 * mob.setMeleeAttackPercent(solEntity.getAttackSpeed()); } catch
		 * (CoreStateInitException e) {
		 * 
		 * }
		 * 
		 * } }
		 */
	}

	private void applyIdentify(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		if (!(getLivingEntity() instanceof Player)) {
			return;
		}

		Entity source = Bukkit.getEntity(getSourceUuid());
		if (!(source instanceof Player))
			return;

		Player sourcePlayer = (Player) source;

		if (sourcePlayer.getInventory().getItemInOffHand() == null) {
			sourcePlayer.sendMessage("You are not holding an item to identify in your offhand");
			return;
		}

		if (sourcePlayer.getInventory().getItemInOffHand().getType() == null
				|| sourcePlayer.getInventory().getItemInOffHand().getType().equals(Material.AIR)) {
			sourcePlayer.sendMessage("You are not holding an item to identify in your offhand");
			return;
		}

		ItemStack item = sourcePlayer.getInventory().getItemInOffHand();

		if (!ItemStackUtils.IsSoliniaItem(item)) {
			sourcePlayer.sendMessage("You do not recognise anything particularly interesting about this item");
			return;
		}

		try {
			ISoliniaItem solItem = SoliniaItemAdapter.Adapt(item);
			if (solItem == null) {
				sourcePlayer.sendMessage("You do not recognise anything particularly interesting about this item");
				return;
			}

			if (solItem.getIdentifyMessage().equals("")) {
				sourcePlayer.sendMessage("You do not recognise anything particularly interesting about this item");
				return;
			}

			sourcePlayer.sendMessage(solItem.getIdentifyMessage());
		} catch (CoreStateInitException | SoliniaItemException e) {

		}
		return;
	}

	private void applyFeignDeath(Plugin plugin, SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		if (!(getLivingEntity() instanceof Player))
			return;

		ISoliniaPlayer solPlayer;
		try {
			solPlayer = SoliniaPlayerAdapter.Adapt((Player) getLivingEntity());

			solPlayer.resetReverseAggro();

			if (solPlayer != null) {
				solPlayer.setFeigned(true);
				EntityUtils.teleportSafely(solPlayer.getBukkitPlayer(),
						solPlayer.getBukkitPlayer().getLocation().subtract(0, 0.5, 0));
				EntityUtils.sit(solPlayer.getBukkitPlayer());
			}

		} catch (CoreStateInitException e) {

		}
	}

	private void applySuccor(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		if (!(getLivingEntity() instanceof Player))
			return;

		ISoliniaPlayer solPlayer;
		try {
			solPlayer = SoliniaPlayerAdapter.Adapt((Player) getLivingEntity());

			SoliniaZone zone = solPlayer.getFirstZone();
			if (zone == null) {
				getLivingEntity().sendMessage(
						"Succor failed! Your body could not be pulled by an astral anchor (not found in a zone)!");
				return;
			}

			if (zone.getSuccorx() == 0 && zone.getSuccory() == 0 && zone.getSuccorz() == 0) {
				getLivingEntity().sendMessage(
						"Succor failed! Your body could not be pulled by an astral anchor (zone has no succor point)!");
				return;
			}

			double x = (zone.getSuccorx());
			double y = (zone.getSuccory());
			double z = (zone.getSuccorz());
			Location loc = new Location(Bukkit.getWorld("world"), x, y, z);
			EntityUtils.teleportSafely(getLivingEntity(), loc);

		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void applyStopRain(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		getLivingEntity().getWorld().setStorm(false);
	}

	private void applyTossUpEffect(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		EntityUtils.dismountEntity(getLivingEntity());
		getLivingEntity().setVelocity(new Vector(0, 0.75, 0));

		return;
	}

	private void applyKnockback(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		double pitch = ((90 - getLivingEntity().getLocation().getPitch()) * Math.PI) / 180;
		double yaw = ((getLivingEntity().getLocation().getYaw() + 90 + 180) * Math.PI) / 180;

		double x = Math.sin(pitch) * Math.cos(yaw);
		double y = Math.sin(pitch) * Math.sin(yaw);
		double z = Math.cos(pitch);

		EntityUtils.dismountEntity(getLivingEntity());
		getLivingEntity().setVelocity(new Vector(x, z, y));

		return;
	}

	private void applySummonHorse(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		if (getLivingEntity() instanceof Player) {
			LocalDateTime datetime = LocalDateTime.now();
			Timestamp nowtimestamp = Timestamp.valueOf(datetime);

			Player player = (Player) getLivingEntity();

			try {
				if (StateManager.getInstance().getPlayerManager().getPlayerLastSteed(player.getUniqueId()) == null) {
					Horse h = (Horse) player.getWorld().spawnEntity(player.getLocation(), EntityType.HORSE);
					h.setCustomName("Holy_Steed");
					h.setCustomNameVisible(true);
					h.setBreed(false);
					h.setColor(Color.WHITE);

					AttributeInstance healthAttribute = h.getAttribute(Attribute.GENERIC_MAX_HEALTH);
					healthAttribute.setBaseValue(1000);

					h.setHealth(1000);
					h.setTamed(true);
					h.setOwner(player);
					h.getInventory().setSaddle(new ItemStack(Material.SADDLE, 1));
					System.out.println("Summoned Holy Steed for player: " + player.getDisplayName());
					player.sendMessage(
							"Your steed has been summoned! You must wait for some time before another steed can be summoned ((server restart))");
					StateManager.getInstance().getPlayerManager().setPlayerLastSteed(player.getUniqueId(),
							nowtimestamp);
				} else {
					// skip, already summoned
				}
			} catch (CoreStateInitException e) {
				// skip
			}

		}
	}

	private void applyTrueNorthSpellEffect(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		Vector dir = new Location(getLivingEntity().getWorld(), 0, 64, -5000000)
				.subtract(getLivingEntity().getEyeLocation()).toVector();
		Location loc = getLivingEntity().getLocation().setDirection(dir);
		EntityUtils.teleportSafely(getLivingEntity(), loc);
		return;
	}

	private void applySenseAnimal(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		try {

			for (Entity e : this.getLivingEntity().getNearbyEntities(100, 100, 100)) {
				if (!(e instanceof LivingEntity))
					continue;

				ISoliniaLivingEntity solEntity = SoliniaLivingEntityAdapter.Adapt((LivingEntity) e);
				if (!solEntity.isAnimal())
					continue;

				Vector dir = ((LivingEntity) e).getLocation().clone().subtract(getLivingEntity().getEyeLocation())
						.toVector();
				Location loc = getLivingEntity().getLocation().setDirection(dir);
				EntityUtils.teleportSafely(getLivingEntity(), loc);
				return;
			}
		} catch (CoreStateInitException e) {

		}
	}

	private void applySenseDead(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		try {

			for (Entity e : this.getLivingEntity().getNearbyEntities(100, 100, 100)) {
				if (!(e instanceof LivingEntity))
					continue;

				ISoliniaLivingEntity solEntity = SoliniaLivingEntityAdapter.Adapt((LivingEntity) e);
				if (!solEntity.isUndead())
					continue;

				Vector dir = ((LivingEntity) e).getLocation().clone().subtract(getLivingEntity().getEyeLocation())
						.toVector();
				Location loc = getLivingEntity().getLocation().setDirection(dir);
				EntityUtils.teleportSafely(getLivingEntity(), loc);
				return;
			}
		} catch (CoreStateInitException e) {

		}
	}

	private void applySenseSummoned(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		try {

			for (Entity e : this.getLivingEntity().getNearbyEntities(100, 100, 100)) {
				if (!(e instanceof LivingEntity))
					continue;

				if (!(e instanceof Creature))
					continue;

				ISoliniaLivingEntity solEntity = SoliniaLivingEntityAdapter.Adapt((LivingEntity) e);
				if (!solEntity.isCurrentlyNPCPet() && !solEntity.isCharmed())
					continue;

				Vector dir = ((LivingEntity) e).getLocation().clone().subtract(getLivingEntity().getEyeLocation())
						.toVector();
				Location loc = getLivingEntity().getLocation().setDirection(dir);
				EntityUtils.teleportSafely(getLivingEntity(), loc);
			}
		} catch (CoreStateInitException e) {

		}
	}

	private void applyRune(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		// nothing to apply on initial creation
	}

	private void applyRevive(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		if (!(getLivingEntity() instanceof Player)) {
			return;
		}

		Entity source = Bukkit.getEntity(getSourceUuid());
		if (!(source instanceof Player))
			return;

		Player sourcePlayer = (Player) source;

		if (!sourcePlayer.getInventory().getItemInOffHand().getType().equals(Material.NAME_TAG)) {
			sourcePlayer.sendMessage("You are not holding a Signaculum in your offhand (MC): "
					+ sourcePlayer.getInventory().getItemInOffHand().getType().name());
			return;
		}

		ItemStack item = sourcePlayer.getInventory().getItemInOffHand();
		if (item.getEnchantmentLevel(Enchantment.DURABILITY) != 1) {
			sourcePlayer.sendMessage("You are not holding a Signaculum in your offhand (EC)");
			return;
		}

		if (!item.getItemMeta().getDisplayName().equals("Signaculum")) {
			sourcePlayer.sendMessage("You are not holding a Signaculum in your offhand (NC)");
			return;
		}

		if (item.getItemMeta().getLore().size() < 5) {
			sourcePlayer.sendMessage("You are not holding a Signaculum in your offhand (LC)");
			return;
		}

		String sigdataholder = item.getItemMeta().getLore().get(3);
		String[] sigdata = sigdataholder.split("\\|");

		if (sigdata.length != 2) {
			sourcePlayer.sendMessage("You are not holding a Signaculum in your offhand (SD)");
			return;
		}

		String str_experience = sigdata[0];
		String str_stimetsamp = sigdata[1];

		int experience = Integer.parseInt(str_experience);
		Timestamp timestamp = Timestamp.valueOf(str_stimetsamp);
		LocalDateTime datetime = LocalDateTime.now();
		Timestamp currenttimestamp = Timestamp.valueOf(datetime);

		long maxminutes = 60 * 7;
		if ((currenttimestamp.getTime() - timestamp.getTime()) >= maxminutes * 60 * 1000) {
			sourcePlayer.sendMessage("This Signaculum has lost its binding to the soul");
			return;
		}

		String characterId = item.getItemMeta().getLore().get(4);

		try {
			ISoliniaPlayer soliniaCharacter = StateManager.getInstance().getConfigurationManager()
					.getCharacterById(Integer.parseInt(characterId));
			if (soliniaCharacter == null) {
				sourcePlayer.sendMessage("You cannot resurrect that player that no longer exists");
				return;
			}

			Player targetplayer = Bukkit.getPlayer(soliniaCharacter.getOwnerUUID());
			if (targetplayer == null || !targetplayer.isOnline()) {
				sourcePlayer.sendMessage("You cannot resurrect that player as they are offline");
				return;
			}

			int multiplier = 1;
			if (spellEffect.getBase() > 0 && spellEffect.getBase() < 100)
				multiplier = spellEffect.getBase();

			try {
				double finalexperience = (experience / 100) * multiplier;
				SoliniaPlayerAdapter.Adapt(targetplayer).increasePlayerNormalExperience(finalexperience, false, false);
				targetplayer.sendMessage("You have been resurrected by " + sourcePlayer.getCustomName() + "!");
				EntityUtils.teleportSafely(targetplayer, sourcePlayer.getLocation());
				sourcePlayer.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
			} catch (CoreStateInitException e) {
				return;
			}
		} catch (CoreStateInitException e) {
			sourcePlayer.sendMessage("The server is not initialised");
			return;
		}

		return;
	}

	private void applyReclaimPet(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		try {
			ISoliniaLivingEntity solEntity = SoliniaLivingEntityAdapter.Adapt(getLivingEntity());
			if (solEntity == null || !solEntity.isCurrentlyNPCPet() || solEntity.isCharmed())
				return;

			LivingEntity owner = (LivingEntity) solEntity.getOwnerEntity();

			if (owner == null)
				return;

			if (!(owner instanceof Player))
				return;

			ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player) owner);

			LivingEntity pet = StateManager.getInstance().getEntityManager().getPet(owner.getUniqueId());
			if (pet == null)
				return;

			solplayer.killAllPets();

			SoliniaPlayerAdapter.Adapt((Player) owner).increasePlayerMana(20);
		} catch (CoreStateInitException e) {
			// do nothing
		}
	}

	private void applyFear(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		// run to a nearby mob
		try {
			StateManager.getInstance().getEntityManager().clearHateList(this.getLivingEntity().getUniqueId());

			/*
			 * disable potentially non-thread safe if
			 * (!SoliniaLivingEntityAdapter.Adapt(getLivingEntity()).isPlayer()) {
			 * ((CraftCreature) getLivingEntity()).getHandle().getNavigation().a(0,0,0,
			 * 1.5); return; }
			 */
		} catch (CoreStateInitException e) {
			//
		}
	}

	private void applyProc(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		// do nothing, this occurs during attack events
	}

	private void applyVision(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		SpellUtils.AddPotionEffect(getLivingEntity(), PotionEffectType.NIGHT_VISION, 1);
	}

	private void applySummonItem(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		int itemId = spellEffect.getBase();
		try {
			ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(itemId);
			if (item == null)
				return;

			if (!item.isTemporary() && !soliniaSpell.isIgnoreSummonItemTemporaryCheck())
				return;

			Entity ownerEntity = Bukkit.getEntity(this.getOwnerUuid());
			if (ownerEntity == null)
				return;

			// This is not really appropriate for anyone but a player
			if (!(ownerEntity instanceof Player))
				return;

			if (!(ownerEntity instanceof LivingEntity))
				return;

			PlayerUtils.addToPlayersInventory((Player) ownerEntity, item.asItemStack());

			// Check if there are any SUMMONITEM_INTO_BAG
			for (SpellEffect effect : soliniaSpell.getBaseSpellEffects()) {
				if (effect.getSpellEffectType().equals(SpellEffectType.SummonItemIntoBag)) {
					try {
						ISoliniaItem subItem = StateManager.getInstance().getConfigurationManager()
								.getItem(effect.getBase());
						if (subItem == null)
							continue;

						if (!subItem.isTemporary() && !soliniaSpell.isIgnoreSummonItemTemporaryCheck())
							continue;

						PlayerUtils.addToPlayersInventory((Player) ownerEntity, subItem.asItemStack());
					} catch (Exception e) {

					}
				}
			}

		} catch (CoreStateInitException e) {
			return;
		}
	}

	private void applyIllusion(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		int type = 0; // earth
		if (soliniaSpell.getName().contains("Water Elemental"))
			type = 1;
		if (soliniaSpell.getName().contains("Air Elemental"))
			type = 2;
		if (soliniaSpell.getName().contains("Fire Elemental"))
			type = 3;

		DisguisePackage disguise = Utils.getDisguiseTypeFromDisguiseId(spellEffect.getBase(), type);
		if (disguise.getDisguisetype() == null || disguise.getDisguisetype() == null
				|| disguise.getDisguisetype().equals(DisguiseType.UNKNOWN)) {
			System.out.println("Could not find illusion: " + spellEffect.getBase());
			return;
		}

		if (DisguiseAPI.isDisguised(getLivingEntity())) {
			Disguise dis = DisguiseAPI.getDisguise(getLivingEntity());
			if (dis instanceof PlayerDisguise) {
				if (disguise.getDisguisedata() != null && !disguise.getDisguisedata().equals("")) {
					if (((PlayerDisguise) dis).getSkin().equals(disguise.getDisguisedata()))
						return;

					// If we get here we can let the player change their skin as it doesnt match
					// their existing player skin name
				} else {
					return;
				}
			} else {
				if (dis.getType().equals(disguise.getDisguisetype()))
					return;
			}
		}

		if (disguise.getDisguisetype().equals(DisguiseType.PLAYER)) {
			String disguisename = disguise.getDisguisedata();
			if (disguisename == null || disguisename.equals(""))
				disguisename = "RomanPraetor";

			PlayerDisguise playerdisguise = new PlayerDisguise(getLivingEntity().getName(), disguisename);
			DisguiseAPI.disguiseEntity(getLivingEntity(), playerdisguise);
			getLivingEntity().sendMessage(
					ChatColor.GRAY + "* Illusion applied, use /disguiseviewself to toggle your illusion for you only");
		} else {
			switch (disguise.getDisguisetype()) {
			case THROWN_EXP_BOTTLE:
				MiscDisguise bottle = new MiscDisguise(disguise.getDisguisetype(), 75);
				DisguiseAPI.disguiseEntity(getLivingEntity(), bottle);
				getLivingEntity().sendMessage(ChatColor.GRAY
						+ "* Illusion applied, use /disguiseviewself to toggle your illusion for you only");
				break;
			default:
				MobDisguise mob = new MobDisguise(disguise.getDisguisetype());
				DisguiseAPI.disguiseEntity(getLivingEntity(), mob);
				getLivingEntity().sendMessage(ChatColor.GRAY
						+ "* Illusion applied, use /disguiseviewself to toggle your illusion for you only");
				break;
			}
		}

	}

	private void applySummonPet(Plugin plugin, SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		if (!isOwnerPlayer())
			return;

		try {
			StateManager.getInstance().getEntityManager().SpawnPet(Bukkit.getPlayer(getOwnerUuid()), soliniaSpell);
		} catch (CoreStateInitException e) {
			return;
		}
	}

	private void applyBindWound(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		if (!isOwnerPlayer())
			return;

		Entity sourceEntity = Bukkit.getEntity(getSourceUuid());
		if (sourceEntity == null)
			return;

		if (!(sourceEntity instanceof LivingEntity))
			return;

		LivingEntity sourceLivingEntity = (LivingEntity) sourceEntity;

		if (!(sourceLivingEntity instanceof Player))
			return;

		try {
			ISoliniaLivingEntity sourceSoliniaLivingEntity = SoliniaLivingEntityAdapter.Adapt(sourceLivingEntity);
			ISoliniaLivingEntity targetSoliniaLivingEntity = SoliniaLivingEntityAdapter.Adapt(getLivingEntity());
			if (sourceSoliniaLivingEntity == null || targetSoliniaLivingEntity == null) {
				return;
			}

			Player player = (Player) sourceLivingEntity;

			ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
			if (!solPlayer.hasSufficientBandageReagents(1)) {
				player.sendMessage("You do not have enough bandages in your /reagent pouch");
				return;
			}

			LivingEntity targetmob = getLivingEntity();

			if (targetmob == null) {
				player.sendMessage("Selecting yourself");
				solPlayer.setEntityTarget(player);
				targetmob = player;
			}

			if (targetmob instanceof Creature) {
				if (((Creature) targetmob).getTarget() != null)
					if (((Creature) targetmob).getTarget().getUniqueId().equals(player.getUniqueId())) {
						player.sendMessage(
								"You cannot bind wound a mob that is currently trying to attack you! Perhaps you meant to target yourself first? [See keybinds]");
						return;
					}
			}

			ISoliniaLivingEntity solLivingEntity = SoliniaLivingEntityAdapter.Adapt(targetmob);
			if (solLivingEntity == null) {
				player.sendMessage("You must select a target to bind wound");
				return;
			}

			if (solPlayer.bindWound(solLivingEntity) == true) {
				// Remove one of the reagents
				int itemid = solPlayer.getBandageReagents().get(0);
				solPlayer.reduceReagents(itemid, 1);
			}

		} catch (CoreStateInitException e) {
			// just skip it
		}
	}

	private void applyIntimidation(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		if (!(getLivingEntity() instanceof Creature))
			return;

		Entity source = Bukkit.getEntity(getSourceUuid());
		if (getLivingEntity() instanceof Creature) {
			try {
				ISoliniaLivingEntity solLivingEntityTarget = SoliniaLivingEntityAdapter.Adapt(getLivingEntity());
				solLivingEntityTarget.addToHateList(source.getUniqueId(), spellEffect.getBase(), true);
			} catch (CoreStateInitException e) {

			}
		}
	}

	private void applyTauntSpell(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		if (!(getLivingEntity() instanceof Creature))
			return;

		Entity source = Bukkit.getEntity(getSourceUuid());
		if (getLivingEntity() instanceof Creature) {
			try {
				ISoliniaLivingEntity solLivingEntityTarget = SoliniaLivingEntityAdapter.Adapt(getLivingEntity());
				solLivingEntityTarget.addToHateList(source.getUniqueId(), spellEffect.getBase(), true);
			} catch (CoreStateInitException e) {

			}
		}
	}

	private void applySummonGroup(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		Entity source = Bukkit.getEntity(getSourceUuid());
		if (source != null && source instanceof Player && getLivingEntity() instanceof Player) {
			if (!source.getUniqueId().equals(getLivingEntity().getUniqueId()))
				EntityUtils.teleportSafely(getLivingEntity(), source.getLocation());
		}
	}

	private void applyTeleport(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		if (!isOwnerPlayer())
			return;

		String[] zonedata = soliniaSpell.getTeleportZone().split(",");
		// Dissasemble the value to ensure it is correct

		if (zonedata.length < 4)
			return;

		String world = zonedata[0];

		double x = Double.parseDouble(zonedata[1]);
		double y = Double.parseDouble(zonedata[2]);
		double z = Double.parseDouble(zonedata[3]);

		Location loc = new Location(Bukkit.getWorld(world), x, y, z);
		EntityUtils.teleportSafely(getLivingEntity(), loc);
	}

	private void applyShadowStep(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		if (!isOwnerPlayer())
			return;

		try {
			Block block = getLivingEntity().getTargetBlock(null, soliniaSpell.getRange());
			Location targetLocation = block.getLocation();
			targetLocation.setDirection(getLivingEntity().getLocation().getDirection());
			if (block != null) {
				EntityUtils.dismountEntity(getLivingEntity());
				EntityUtils.teleportSafely(getLivingEntity(), targetLocation);
			}
		} catch (Exception e) {
			// out of world block
		}
	}

	private void applyBlind(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		SpellUtils.AddPotionEffect(getLivingEntity(), PotionEffectType.BLINDNESS, 1);
	}

	private void applyInvisibility(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		SpellUtils.AddPotionEffect(getLivingEntity(), PotionEffectType.INVISIBILITY, 1);
	}

	private void applyWaterBreathing(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		SpellUtils.AddPotionEffect(getLivingEntity(), PotionEffectType.WATER_BREATHING, 1);
	}

	private void applyConfusion(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		SpellUtils.AddPotionEffect(getLivingEntity(), PotionEffectType.CONFUSION, 1);
	}

	private void applyLevitateSpellEffect(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		if (this.getLivingEntity() instanceof Player)
			SpellUtils.AddPotionEffect(getLivingEntity(), PotionEffectType.LEVITATION, 255);

	}

	private void applyRootSpellEffect(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		EntityUtils.dismountEntity(getLivingEntity());

		SpellUtils.AddPotionEffect(getLivingEntity(), PotionEffectType.SLOW, 10);
	}

	private void applyWipeHateList(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		try {
			StateManager.getInstance().getEntityManager().clearHateList(getLivingEntity().getUniqueId());
		} catch (CoreStateInitException e) {

		}
	}

	private void applyMezSpellEffect(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		EntityUtils.dismountEntity(getLivingEntity());

		if (!(getLivingEntity() instanceof LivingEntity))
			return;

		try {
			ISoliniaLivingEntity livingEntity = SoliniaLivingEntityAdapter.Adapt(getLivingEntity());
			livingEntity.InterruptSpell();

			LocalDateTime datetime = LocalDateTime.now();
			Timestamp expiretimestamp = Timestamp.valueOf(datetime.plus(6, ChronoUnit.SECONDS));

			StateManager.getInstance().getEntityManager().addMezzed(getLivingEntity(), expiretimestamp);

			if (getLivingEntity() instanceof Creature) {
				livingEntity.setAttackTarget(null);

			}

			EntityUtils.dismountEntity(getLivingEntity());

			Entity vehicle = getLivingEntity().getVehicle();
			if (vehicle != null) {
				vehicle.eject();
			}

			SpellUtils.AddPotionEffect(getLivingEntity(), PotionEffectType.CONFUSION, 1);
		} catch (CoreStateInitException e) {
			return;
		}
	}

	private void applyBindAffinty(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		if (!isOwnerPlayer())
			return;

		Player player = (Player) getLivingEntity();
		try {
			ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
			solPlayer.setBindPoint(player.getLocation().getWorld().getName() + "," + player.getLocation().getX() + ","
					+ player.getLocation().getY() + "," + player.getLocation().getZ());
			player.sendMessage("You feel yourself bind to the area");

		} catch (CoreStateInitException e) {
			// skip
		}

	}

	private void applyFoodDrink(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		if (!isOwnerPlayer())
			return;

		Player player = (Player) getLivingEntity();
		player.setFoodLevel(20);
	}

	private void applyGateHome(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		if (!isOwnerPlayer())
			return;

		Player player = (Player) getLivingEntity();
		try {
			ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);

			if (solPlayer.getClassObj() == null || solPlayer.getRace() == null) {
				player.sendMessage(
						"Could not teleport to your races home city as you have not set your race and class");
				return;
			}

			Location location = solPlayer.getClassObj().getRaceClass(solPlayer.getRace().getId()).getStartLocation();
			if (location == null) {
				player.sendMessage(
						"Could not teleport to your races home city as your race class combination does not appear to have a home point");
				return;
			}

			EntityUtils.teleportSafely(player, location);
		} catch (CoreStateInitException e) {

		}

	}

	private void applyGate(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		if (!isOwnerPlayer())
			return;

		Player player = (Player) getLivingEntity();
		try {
			ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);

			String blocation = solPlayer.getBindPoint();
			if (blocation == null || solPlayer.getBindPoint().equals("")) {
				player.sendMessage("Could not teleport, you are not bound to a location (by bind affinity)");
				return;
			}

			String[] loc = solPlayer.getBindPoint().split(",");

			Location location = new Location(Bukkit.getWorld(loc[0]), Double.parseDouble(loc[1]),
					Double.parseDouble(loc[2]), Double.parseDouble(loc[3]));

			player.setBedSpawnLocation(location, true);
			EntityUtils.teleportSafely(player, location);
		} catch (CoreStateInitException e) {

		}
	}

	private void applyCurrentMpSpellEffect(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		//if (!isOwnerPlayer())
		//	return;
		Entity sourceEntity = Bukkit.getEntity(getSourceUuid());
		if (sourceEntity == null)
			return;

		if (!(sourceEntity instanceof LivingEntity))
			return;

		if (this.getLivingEntity() == null)
			return;

		
		LivingEntity sourceLivingEntity = (LivingEntity) sourceEntity;

		int instrument_mod = 0;

		try {
			ISoliniaLivingEntity sourceSoliniaLivingEntity = SoliniaLivingEntityAdapter.Adapt(sourceLivingEntity);
			if (sourceSoliniaLivingEntity != null) {
				instrument_mod = sourceSoliniaLivingEntity.getInstrumentMod(this.getSpell());
			}
		} catch (CoreStateInitException e) {
			// just skip it
		}

		int mpAmount = soliniaSpell.calcSpellEffectValue(spellEffect, sourceLivingEntity, getLivingEntity(),
				casterLevel, getTicksLeft(), instrument_mod);

		try {
			ISoliniaLivingEntity solentity = SoliniaLivingEntityAdapter.Adapt(this.getLivingEntity());

			if (mpAmount == 0)
				return;

			if (mpAmount > 0)
				solentity.increaseMana(mpAmount);
			else
				solentity.reduceMana(mpAmount);

		} catch (CoreStateInitException e) {
			e.printStackTrace();
		}
	}

	private void applySpinTarget(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		EntityUtils.spinLivingEntity(getLivingEntity());
	}

	private void applyMovementSpeedEffect(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		EntityUtils.dismountEntity(getLivingEntity());

		int normalize = spellEffect.getBase();
		// value is a percentage but we range from 1-5 (we can stretch to 10)
		normalize = normalize / 10;
		if (spellEffect.getBase() > 0) {
			SpellUtils.AddPotionEffect(getLivingEntity(), PotionEffectType.SPEED, normalize);
		} else {
			SpellUtils.AddPotionEffect(getLivingEntity(), PotionEffectType.SLOW, (normalize * -1));
		}
	}

	private void applyMiningHasteSpellEffect(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		if (this.getLivingEntity() instanceof Player)
			SpellUtils.AddPotionEffect(getLivingEntity(), PotionEffectType.FAST_DIGGING, spellEffect.getBase());
	}

	private void applyFeatherFallSpellEffect(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		if (this.getLivingEntity() instanceof Player)
			SpellUtils.AddPotionEffect(getLivingEntity(), PotionEffectType.SLOW_FALLING, spellEffect.getBase());
	}

	private void applyCurrentHpSpellEffect(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		applyCurrentHpOnceSpellEffect(spellEffect, soliniaSpell, casterLevel);
	}

	private LivingEntity getLivingEntity() {
		if (isOwnerPlayer())
			return Bukkit.getPlayer(getOwnerUuid());

		if (Bukkit.getEntity(getOwnerUuid()) instanceof LivingEntity)
			return (LivingEntity) Bukkit.getEntity(getOwnerUuid());

		return null;
	}

	private void applyCancelMagic(Plugin plugin, SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		try {
			// do not loop song (else cancel may cancel out cancel may cancel out cancel...)
			if (this.getSourceUuid().equals(this.getOwnerUuid()))
				StateManager.getInstance().getEntityManager().clearEntityFirstEffect(getLivingEntity(), true);
			else
				StateManager.getInstance().getEntityManager().clearEntityFirstEffect(getLivingEntity());
		} catch (CoreStateInitException e) {
			return;
		}
	}

	private void applyPoisonCounter(Plugin plugin, SpellEffect spellEffect, ISoliniaSpell soliniaSpell,
			int casterLevel) {
		if (!soliniaSpell.isCure())
			return;

		try {
			StateManager.getInstance().getEntityManager().clearEntityFirstEffectOfType(getLivingEntity(),
					SpellEffectType.PoisonCounter, false, false);
			if (isOwnerPlayer()) {
				Player player = (Player) Bukkit.getPlayer(getOwnerUuid());
				if (player != null) {
					if (player.hasPotionEffect(PotionEffectType.POISON))
						player.removePotionEffect(PotionEffectType.POISON);
					if (player.hasPotionEffect(PotionEffectType.HUNGER))
						player.removePotionEffect(PotionEffectType.HUNGER);
					player.sendMessage(ChatColor.GRAY + "* You have been cured of some poison");
				}
			}
			if (isSourcePlayer()) {
				Player player = (Player) Bukkit.getPlayer(getSourceUuid());
				if (player != null)
					player.sendMessage(ChatColor.GRAY + "* You cured your target of some poison");
			}
		} catch (CoreStateInitException e) {
			return;
		}
	}

	private void applyDiseaseCounter(Plugin plugin, SpellEffect spellEffect, ISoliniaSpell soliniaSpell,
			int casterLevel) {
		if (!soliniaSpell.isCure())
			return;

		try {
			StateManager.getInstance().getEntityManager().clearEntityFirstEffectOfType(getLivingEntity(),
					SpellEffectType.DiseaseCounter, false, false);
			if (isOwnerPlayer()) {
				Player player = (Player) Bukkit.getPlayer(getOwnerUuid());
				if (player != null)
					player.sendMessage(ChatColor.GRAY + "* You have been cured of some disease");
			}
			if (isSourcePlayer()) {
				Player player = (Player) Bukkit.getPlayer(getSourceUuid());
				if (player != null)
					player.sendMessage(ChatColor.GRAY + "* You cured your target of some poison");
			}
		} catch (CoreStateInitException e) {
			return;
		}
	}

	private void applyBackstab(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {
		if (getLivingEntity().isDead())
			return;

		if (Bukkit.getEntity(getSourceUuid()) == null)
			return;

		Entity sourceEntity = Bukkit.getEntity(getSourceUuid());
		if (sourceEntity == null)
			return;

		if (!(sourceEntity instanceof LivingEntity))
			return;

		LivingEntity sourceLivingEntity = (LivingEntity) sourceEntity;

		try {
			ISoliniaLivingEntity solSourceEntity = SoliniaLivingEntityAdapter.Adapt(sourceLivingEntity);
			if (solSourceEntity == null)
				return;

			int backstabSkill = solSourceEntity.getSkill(SkillType.Backstab);
			if (backstabSkill < 1)
				backstabSkill = 1;

			int weaponDamage = 0;

			// Offhand item only
			ItemStack mainitem = sourceLivingEntity.getEquipment().getItemInOffHand();

			if (mainitem != null) {
				try {
					ISoliniaItem item = SoliniaItemAdapter.Adapt(mainitem);
					if (item != null)
						if (item.getItemWeaponDamage(false, mainitem) > 0) {
							weaponDamage = item.getItemWeaponDamage(false, mainitem);
						}
				} catch (SoliniaItemException e) {

				}
			}

			if (weaponDamage < 1)
				weaponDamage = 1;

			if (!solSourceEntity.isBehindEntity(this.getLivingEntity())) {
				solSourceEntity.sendMessage("You are not behind the enemy");
				return;
			}

			ISoliniaLivingEntity targetOfSpell = SoliniaLivingEntityAdapter.Adapt(this.getLivingEntity());
			if (targetOfSpell != null)
				targetOfSpell.addToHateList(getSourceUuid(), weaponDamage, false);

			solSourceEntity.doSpecialAttackDamage(targetOfSpell, SkillType.Backstab, weaponDamage, 0, weaponDamage, 0);

			solSourceEntity.tryIncreaseSkill(SkillType.Backstab, 1);

		} catch (CoreStateInitException e) {

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

		if (!(sourceEntity instanceof LivingEntity))
			return;

		LivingEntity sourceLivingEntity = (LivingEntity) sourceEntity;

		int instrument_mod = 0;

		try {
			ISoliniaLivingEntity sourceSoliniaLivingEntity = SoliniaLivingEntityAdapter.Adapt(sourceLivingEntity);
			if (sourceSoliniaLivingEntity != null) {
				instrument_mod = sourceSoliniaLivingEntity.getInstrumentMod(this.getSpell());
			}
		} catch (CoreStateInitException e) {
			// just skip it
		}

		// HP spells also get calculated based on the caster and the recipient
		int hpToAdd = soliniaSpell.calcSpellEffectValue(spellEffect, sourceLivingEntity, getLivingEntity(),
				caster_level, getTicksLeft(), instrument_mod);

		try {
			ISoliniaLivingEntity sourceSoliniaLivingEntity = SoliniaLivingEntityAdapter.Adapt(sourceLivingEntity);
			ISoliniaLivingEntity targetSoliniaLivingEntity = SoliniaLivingEntityAdapter.Adapt(getLivingEntity());

			// Damage
			// damage should be < 0
			// hpToRemove should really be called hpToAdd
			if (hpToAdd < 0) {
				if (sourceSoliniaLivingEntity != null && targetSoliniaLivingEntity != null) {
					// reverse to positive then pass it back reversed
					hpToAdd = (sourceSoliniaLivingEntity.getActSpellDamage(soliniaSpell, (hpToAdd * -1), spellEffect,
							targetSoliniaLivingEntity) * -1);
				}

				targetSoliniaLivingEntity.addToHateList(getSourceUuid(), hpToAdd * -1, false);
				boolean isLichSpell = soliniaSpell.isLichSpell();
				double newHp = targetSoliniaLivingEntity.setHPChange(hpToAdd, sourceLivingEntity, !isLichSpell);
				if (newHp <= 0D)
					sourceSoliniaLivingEntity.removeAggro(getLivingEntity().getUniqueId());
				// getLivingEntity().damage(hpToRemove, Bukkit.getEntity(getSourceUuid()));
				if (soliniaSpell.isLifetapSpell()) {

					if (!(sourceEntity instanceof LivingEntity))
						return;

					if (!sourceLivingEntity.isDead()) {
						newHp = sourceSoliniaLivingEntity.setHPChange(hpToAdd * -1,
								targetSoliniaLivingEntity.getBukkitLivingEntity());
						if (newHp <= 0D)
							targetSoliniaLivingEntity.removeAggro(sourceLivingEntity.getUniqueId());
					}
				}
			}
			// Heal
			else {
				if (sourceSoliniaLivingEntity != null && targetSoliniaLivingEntity != null) {
					hpToAdd = sourceSoliniaLivingEntity.getActSpellHealing(soliniaSpell, hpToAdd);
				}

				if (!getLivingEntity().isDead())
					targetSoliniaLivingEntity.setHPChange(hpToAdd, sourceSoliniaLivingEntity.getBukkitLivingEntity());
			}
		} catch (CoreStateInitException e) {

		}
	}

	private void applySeeInvis(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int casterLevel) {

		if (!(getLivingEntity() instanceof Player))
			return;

		// Resend packet for everyone in range
		for (Entity entity : getLivingEntity().getNearbyEntities(200, 200, 200)) {
			if (!(entity instanceof LivingEntity))
				continue;

			if (!((LivingEntity) entity).hasPotionEffect(PotionEffectType.INVISIBILITY))
				continue;

			EntityUtils.sendEntityDataPacket((LivingEntity) entity, (Player) getLivingEntity());
		}
	}

	private void applyManaBurn(SpellEffect spellEffect, ISoliniaSpell soliniaSpell, int caster_level) {
		if (getLivingEntity().isDead())
			return;

		if (Bukkit.getEntity(getSourceUuid()) == null)
			return;

		Entity sourceEntity = Bukkit.getEntity(getSourceUuid());
		if (sourceEntity == null)
			return;

		if (!(sourceEntity instanceof LivingEntity))
			return;

		LivingEntity sourceLivingEntity = (LivingEntity) sourceEntity;

		if (!(sourceLivingEntity instanceof Player))
			return;

		Player sourcePlayer = (Player) sourceLivingEntity;
		try {
			ISoliniaPlayer sourceSolPlayer = SoliniaPlayerAdapter.Adapt(sourcePlayer);
			if (sourceSolPlayer == null)
				return;

			ISoliniaLivingEntity sourceSoliniaLivingEntity = SoliniaLivingEntityAdapter.Adapt(sourceLivingEntity);
			ISoliniaLivingEntity targetSoliniaLivingEntity = SoliniaLivingEntityAdapter.Adapt(getLivingEntity());

			int max_mana = spellEffect.getBase();
			int ratio = 100; // TODO this should be Base2?
			int dmg = 0;

			if (sourceSolPlayer.getMana() < sourceSoliniaLivingEntity.getMaxMP())
				dmg = ratio * sourceSolPlayer.getMana() / 10;
			else
				dmg = ratio * max_mana / 10;

			sourceSolPlayer.setMana(0);

			if (soliniaSpell.isDetrimental()) {
				if (!getLivingEntity().isDead()) {
					double newHp = targetSoliniaLivingEntity.setHPChange(dmg * -1, sourceSolPlayer.getBukkitPlayer());
					if (newHp <= 0D)
						sourceSoliniaLivingEntity.removeAggro(getLivingEntity().getUniqueId());
				}
			} else {
				if (!getLivingEntity().isDead()) {
					double newHp = targetSoliniaLivingEntity.setHPChange(dmg, sourceSolPlayer.getBukkitPlayer());
					if (newHp <= 0D)
						sourceSoliniaLivingEntity.removeAggro(getLivingEntity().getUniqueId());
				}
			}

		} catch (CoreStateInitException e) {
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

	public int getNumHits() {
		return numHits;
	}

	public void setNumHits(int numHits) {
		this.numHits = numHits;
	}

	public void tryFadeEffect() {
		try {
			Entity owner = Bukkit.getEntity(ownerUuid);
			if (owner == null)
				return;

			if (!(owner instanceof LivingEntity))
				return;

			if (owner.isDead())
				return;

			/*
			 * System.out.println( "Attempting fade effect for " + this.getSpellId() +
			 * " for owner: " + owner.getCustomName());
			 */

			StateManager.getInstance().getEntityManager().removeSpellEffectsOfSpellId(ownerUuid, spellId, true, true);
		} catch (Exception e) {

		}
	}

	public void buffTick() {
		for (ActiveSpellEffect spellEffect : this.getActiveSpellEffects()) {
			buffEffectTick(spellEffect);
		}
	}

	private void buffEffectTick(ActiveSpellEffect spellEffect) {
		try {
			switch (spellEffect.getSpellEffectType()) {
			case Charm:
				Entity source = Bukkit.getEntity(this.getSourceUuid());
				if (source == null) {
					removeActiveSpellNextTick();
					return;
				}

				ISoliniaLivingEntity solLivingEntity = SoliniaLivingEntityAdapter.Adapt((LivingEntity) source);
				if (solLivingEntity == null) {
					removeActiveSpellNextTick();
					return;
				}

				if (!solLivingEntity.passCharismaCheck(this.getLivingEntity(), this.getSpell())) {
					ChatUtils.SendHint((LivingEntity) source, HINT.CHARM_CHA_FAIL, "", false);
					removeActiveSpellNextTick();
					return;
				}

				break;
			default:
				break;
			}
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void removeActiveSpellNextTick() {
		this.setTicksLeft(0);
	}

	public String getRequiredWeaponSkillType() {
		return requiredWeaponSkillType;
	}

	public void setRequiredWeaponSkillType(String requiredWeaponSkillType) {
		this.requiredWeaponSkillType = requiredWeaponSkillType;
	}

	public boolean isRacialPassive() {
		// TODO Auto-generated method stub
		return isRacialPassive;
	}

	public void setRacialPassive(boolean isRacialPassive) {
		this.isRacialPassive = isRacialPassive;
	}
}
