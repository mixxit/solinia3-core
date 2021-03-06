package com.solinia.solinia.Models;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sittable;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.rit.sucy.player.TargetHelper;
import com.solinia.solinia.Adapters.ItemStackAdapter;
import com.solinia.solinia.Adapters.SoliniaItemAdapter;
import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaItemException;
import com.solinia.solinia.Interfaces.ISoliniaAAAbility;
import com.solinia.solinia.Interfaces.ISoliniaAARank;
import com.solinia.solinia.Interfaces.ISoliniaClass;
import com.solinia.solinia.Interfaces.ISoliniaFaction;
import com.solinia.solinia.Interfaces.ISoliniaGod;
import com.solinia.solinia.Interfaces.ISoliniaGroup;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Interfaces.ISoliniaRace;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Utils.ChatUtils;
import com.solinia.solinia.Utils.DebugUtils;
import com.solinia.solinia.Utils.DropUtils;
import com.solinia.solinia.Utils.EntityUtils;
import com.solinia.solinia.Utils.ForgeUtils;
import com.solinia.solinia.Utils.ItemStackUtils;
import com.solinia.solinia.Utils.MathUtils;
import com.solinia.solinia.Utils.MythicMobsUtils;
import com.solinia.solinia.Utils.NPCUtils;
import com.solinia.solinia.Utils.PartyWindowUtils;
import com.solinia.solinia.Utils.PlayerUtils;
import com.solinia.solinia.Utils.RaycastUtils;
import com.solinia.solinia.Utils.SkillUtils;
import com.solinia.solinia.Utils.SpellUtils;
import com.solinia.solinia.Utils.TimeUtils;
import com.solinia.solinia.Utils.Utils;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_15_R1.Tuple;

public class SoliniaLivingEntity implements ISoliniaLivingEntity {
	LivingEntity livingentity;
	private int actualLevel = 1;
	private int npcid;

	public SoliniaLivingEntity(LivingEntity livingentity) {
		this.livingentity = livingentity;

		String metaid = "";
		if (livingentity != null) {

			for (MetadataValue val : livingentity.getMetadata("mobname")) {
				metaid = val.asString();
			}

			for (MetadataValue val : livingentity.getMetadata("npcid")) {
				metaid = val.asString();
			}
		}

		if (metaid != null)
			if (!metaid.equals(""))
				installNpcByMetaName(metaid);
	}

	@Override
	public boolean passCharismaCheck(LivingEntity caster, ISoliniaSpell spell) throws CoreStateInitException {
		if (caster == null)
			return false;

		if (spell.getResistDiff() <= -600)
			return true;

		double resist_check = 0;
		int charmBreakChance = 25; // 25%

		if (spell.isCharmSpell()) {
			if (!spell.isResistable()) // If charm spell has this set(-1), it can not break till end of duration.
				return true;

			// 1: The mob has a default 25% chance of being allowed a resistance check
			// against the charm.
			if (MathUtils.RandomBetween(0, 99) > charmBreakChance)
				return true;

			resist_check = getResistSpell(spell, caster);

			// 2: The mob makes a resistance check against the charm
			if (resist_check == 100)
				return true;

			else {
				if (caster instanceof Player) {
					// 3: At maxed ability, Total Domination has a 50% chance of preventing the
					// charm break that otherwise would have occurred.
					int totalDominationBonus = 0;

					totalDominationBonus += getSpellBonuses(SpellEffectType.CharmBreakChance);
					totalDominationBonus += getAABonuses(SpellEffectType.CharmBreakChance);

					if (MathUtils.RandomBetween(0, 99) < totalDominationBonus)
						return true;

				}
			}
		} else {
			resist_check = getResistSpell(spell, caster);
			if (resist_check == 100)
				return true;
		}

		return false;
	}

	@Override
	public float getResistSpell(ISoliniaSpell spell, LivingEntity caster) throws CoreStateInitException {
		// TODO Auto-generated method stub
		int resistmodifier = spell.getResistDiff();
		int casterlevel = 1;
		int targetresist = 0;
		
		boolean isnpccaster = false;

		if (caster instanceof Player) {
			casterlevel = SoliniaPlayerAdapter.Adapt((Player) caster).getMentorLevel();
		} else {
			if (EntityUtils.isLivingEntityNPC(caster)) {
				ISoliniaLivingEntity solentity = SoliniaLivingEntityAdapter.Adapt((LivingEntity) caster);
				casterlevel = solentity.getEffectiveLevel(true);
				isnpccaster = true;
			}
		}

		boolean isnpcvictim = false;
		int victimlevel = 1;

		if (getBukkitLivingEntity() instanceof Player) {
			victimlevel = SoliniaPlayerAdapter.Adapt((Player) getBukkitLivingEntity()).getActualLevel();
			targetresist = SoliniaPlayerAdapter.Adapt((Player) getBukkitLivingEntity())
					.getResist(SpellUtils.getSpellResistType(spell.getResisttype()));
		} else {
			if (this.isNPC()) {
				ISoliniaLivingEntity solentity = SoliniaLivingEntityAdapter
						.Adapt((LivingEntity) getBukkitLivingEntity());
				targetresist = solentity.getResists(SpellUtils.getSpellResistType(spell.getResisttype()));
				victimlevel = solentity.getMentorLevel();
				isnpcvictim = true;
			}
		}

		int resist_chance = 0;
		int level_mod = 0;
		int temp_level_diff = victimlevel - casterlevel;

		if (isnpcvictim == false && victimlevel >= 21 && temp_level_diff > 15) {
			temp_level_diff = 15;
		}

		if (isnpcvictim == true && temp_level_diff < -9) {
			temp_level_diff = -9;
		}

		level_mod = temp_level_diff * temp_level_diff / 2;
		if (temp_level_diff < 0) {
			level_mod = -level_mod;
		}

		if (isnpcvictim && (casterlevel - victimlevel < -20)) {
			level_mod = 1000;
		}

		// Even more level stuff this time dealing with damage spells
		if (isnpcvictim && spell.isDamageSpell() && victimlevel >= 17) {
			int level_diff;
			level_diff = victimlevel - casterlevel;
			level_mod += (2 * level_diff);
		}

		resist_chance += level_mod;
		resist_chance += resistmodifier;
		resist_chance += targetresist;

		if (resist_chance > 255) {
			resist_chance = 255;
		}

		if (resist_chance < 0) {
			resist_chance = 0;
		}

		int roll = MathUtils.RandomBetween(0, 200);

		if (roll > resist_chance) {
			return 100;
		} else {
			if (resist_chance < 1) {
				resist_chance = 1;
			}


			int partial_modifier = ((150 * (resist_chance - roll)) / resist_chance);

			if (isnpcvictim == true) {
				if (victimlevel > casterlevel && victimlevel >= 17 && casterlevel <= 50) {
					partial_modifier += 5;
				}

				if (victimlevel >= 30 && casterlevel < 50) {
					partial_modifier += (casterlevel - 25);
				}

				if (victimlevel < 15) {
					partial_modifier -= 5;
				}
			}

			if (isnpccaster) {
				if ((victimlevel - casterlevel) >= 20) {
					partial_modifier += (victimlevel - casterlevel) * 1.5;
				}
			}

			if (partial_modifier <= 0) {
				return 100F;
			} else if (partial_modifier >= 100) {
				return 0;
			}

			return (100.0f - partial_modifier);
		}
	}

	@Override
	public SoliniaWorld getWorld() {
		try {
			return StateManager.getInstance().getConfigurationManager()
					.getWorld(getBukkitLivingEntity().getWorld().getName().toUpperCase());
		} catch (CoreStateInitException e) {
			return null;
		}
	}
	
	@Override
	public void NPCProcess() {
		// stun removal check
		// depop check
		
		//SpellProcess();

		// hp regen
		// mana regen
		
		// send hp update
		
		// npc pathing

		if (isMezzed())
			return;

		if(isStunned()) {
			//if(spun_timer.Check())
				//Spin();
			return;
		}

		// enrage timer
		/*
		if (enraged_timer.Check()){
			ProcessEnrage();

			if (!GetSpecialAbility(SPECATK_ENRAGE)) {
				enraged_timer.Disable();
			}
		}
		 */

		/*
		//Handle assists...
		if (assist_cap_timer.Check()) {
			if (NPCAssistCap() > 0)
				DelAssistCap();
			else
				assist_cap_timer.Disable();
		}

		if (assist_timer.Check() && IsEngaged() && !Charmed() && !HasAssistAggro() &&
		    NPCAssistCap() < RuleI(Combat, NPCAssistCap)) {
			entity_list.AIYellForHelp(this, GetTarget());
			if (NPCAssistCap() > 0 && !assist_cap_timer.Enabled())
				assist_cap_timer.Start(RuleI(Combat, NPCAssistCapTimer));
		}
		*/

		AI_Process();
	}
	
	@Override
	public void AI_Process()
	{
		try
		{
			if (!this.isNPC() && !this.isCurrentlyNPCPet())
				return;
			
			// TODO Check last think timer
			//if (!(AI_think_timer->Check() || attack_timer.Check(false)))
				//return;
	
			if (isCasting())
				return;
	
			boolean engaged = isEngaged();
			boolean doranged = false;
	
			/*
			if (!zone->CanDoCombat() || IsPetStop() || IsPetRegroup()) {
				engaged = false;
			}
			*/
	
			// door opening code
	
			// fear pathing/fleeing
			
			EntityAutoAttack autoAttack = StateManager.getInstance().getEntityManager().getEntityAutoAttack(this.getBukkitLivingEntity());
			LivingEntity target = getEntityTarget();
	
			ISoliniaLivingEntity defender = null;
			if (target != null && target instanceof LivingEntity)
				defender = SoliniaLivingEntityAdapter.Adapt(target);
			
			if (engaged) {
				// todo fix height
				// NPCs will forget people after 10 mins of not interacting with them or out of range
				// both of these maybe zone specific, hardcoded for now
				
				/*
				if (hate_list_cleanup_timer.Check()) {
					hate_list.RemoveStaleEntries(600000, 600.0f);
					if (hate_list.IsHateListEmpty()) {
						AI_Event_NoLongerEngaged();
						zone->DelAggroMob();
						if (IsNPC() && !RuleB(Aggro, AllowTickPulling))
							ResetAssistCap();
					}
				}
				*/
				// we are prevented from getting here if we are blind and don't have a target in range
				// from above, so no extra blind checks needed
				
				/* Attacking nearer mobs
				if ((IsRooted() && !GetSpecialAbility(IGNORE_ROOT_AGGRO_RULES)) || IsBlind())
					SetTarget(hate_list.GetClosestEntOnHateList(this));
				else
				{
					if (AI_target_check_timer->Check())
					{
						if (IsFocused()) {
							if (!target) {
								SetTarget(hate_list.GetEntWithMostHateOnList(this));
							}
						}
						else {
							if (!ImprovedTaunt())
								SetTarget(hate_list.GetEntWithMostHateOnList(this));
						}
	
					}
				}
				*/
	
				if (target == null || defender == null)
					return;
	
				if (defender.IsCorpse()) {
					removeFromHateList(target.getUniqueId());
					return;
				}
	
				if (DivineAura())
					return;
	
				//ProjectileAttack();
	
				/* Spawn point tethering
				auto npcSpawnPoint = getSpawnPoint();
				if (GetSpecialAbility(TETHER)) {
					float tether_range = static_cast<float>(GetSpecialAbilityParam(TETHER, 0));
					tether_range = tether_range > 0.0f ? tether_range * tether_range : pAggroRange * pAggroRange;
	
					if (DistanceSquaredNoZ(m_Position, npcSpawnPoint) > tether_range) {
						GMMove(npcSpawnPoint.x, npcSpawnPoint.y, npcSpawnPoint.z, npcSpawnPoint.w);
					}
				}
				else if (GetSpecialAbility(LEASH)) {
					float leash_range = static_cast<float>(GetSpecialAbilityParam(LEASH, 0));
					leash_range = leash_range > 0.0f ? leash_range * leash_range : pAggroRange * pAggroRange;
	
					if (DistanceSquaredNoZ(m_Position, npcSpawnPoint) > leash_range) {
						GMMove(npcSpawnPoint.x, npcSpawnPoint.y, npcSpawnPoint.z, npcSpawnPoint.w);
						SetHP(GetMaxHP());
						BuffFadeAll();
						WipeHateList();
						return;
					}
				}
				*/
	
				//StartEnrage();
	
				boolean is_combat_range = combatRange(defender);
				
				if (is_combat_range)
				{
					/*if (AI_movement_timer->Check())
					{
						if (CalculateHeadingToTarget(GetTarget()->GetX(), GetTarget()->GetY()) != m_Position.w)
						{
							SetHeading(CalculateHeadingToTarget(GetTarget()->GetX(), GetTarget()->GetY()));
							SendPosition();
						}
						SetCurrentSpeed(0);
					}*/
					/*
					if (IsMoving())
					{
						if (CalculateHeadingToTarget(GetTarget()->GetX(), GetTarget()->GetY()) != m_Position.w)
						{
							SetHeading(CalculateHeadingToTarget(GetTarget()->GetX(), GetTarget()->GetY()));
							SendPosition();
						}
						SetCurrentSpeed(0);
					}
					*/
	
					//casting checked above...
					if (
							target != null && 
							!isStunned() && 
							!isMezzed() //&& 
							//GetAppearance() != eaDead && 
							//!IsMeleeDisabled()
						) {
	
						if (autoAttack.canAutoAttack())
						{
							//we should check to see if they die mid-attacks, previous
							//crap of checking target for null was not gunna cut it
		
							//try main hand first
							if (autoAttack.canAutoAttack()) {
								DoMainHandAttackRounds(target, null);
								//TriggerDefensiveProcs(target, InventorySlot.Primary, false);
		
								boolean specialed = false; // NPCs can only do one of these a round
								if (getSpecialAbility(SpecialAbility.SPECATK_FLURRY) > 0) {
									int flurry_chance = 0;
									//int flurry_chance = GetSpecialAbilityParam(SPECATK_FLURRY, 0);
									flurry_chance = flurry_chance > 0 ? flurry_chance : Utils.NPCFlurryChance;
		
									if (MathUtils.Roll(flurry_chance)) {
										ExtraAttackOptions opts = new ExtraAttackOptions();
										/*int cur = GetSpecialAbilityParam(SPECATK_FLURRY, 2);
										if (cur > 0)
											opts.damage_percent = cur / 100.0f;
		
										cur = GetSpecialAbilityParam(SPECATK_FLURRY, 3);
										if (cur > 0)
											opts.damage_flat = cur;
		
										cur = GetSpecialAbilityParam(SPECATK_FLURRY, 4);
										if (cur > 0)
											opts.armor_pen_percent = cur / 100.0f;
		
										cur = GetSpecialAbilityParam(SPECATK_FLURRY, 5);
										if (cur > 0)
											opts.armor_pen_flat = cur;
		
										cur = GetSpecialAbilityParam(SPECATK_FLURRY, 6);
										if (cur > 0)
											opts.crit_percent = cur / 100.0f;
		
										cur = GetSpecialAbilityParam(SPECATK_FLURRY, 7);
										if (cur > 0)
											opts.crit_flat = cur;*/
		
										Flurry(opts);
										specialed = true;
									}
								}
		
								if (this.isCurrentlyNPCPet()) {
									ISoliniaLivingEntity owner = this.getOwnerSoliniaLivingEntity();
									if (owner != null) {
										int flurry_chance = owner.getAABonuses(SpellEffectType.PetFlurry) +
											owner.getSpellBonuses(SpellEffectType.PetFlurry) + owner.getItemBonuses(SpellEffectType.PetFlurry);
		
										if (flurry_chance > 0 && MathUtils.Roll(flurry_chance))
											Flurry(null);
									}
								}
		
								if ((this.isCurrentlyNPCPet()) && this.getOwnerEntity() instanceof Player) {
									ISoliniaLivingEntity owner = this.getOwnerSoliniaLivingEntity();
									if (owner.getSpellBonuses(SpellEffectType.PC_Pet_Rampage) > 0 || owner.getItemBonuses(SpellEffectType.PC_Pet_Rampage) > 0 || owner.getAABonuses(SpellEffectType.PC_Pet_Rampage) > 0) {
										int chance = owner.getSpellBonuses(SpellEffectType.PC_Pet_Rampage) + owner.getItemBonuses(SpellEffectType.PC_Pet_Rampage) + owner.getAABonuses(SpellEffectType.PC_Pet_Rampage);
										if (MathUtils.Roll(chance)) {
											Rampage(null);
										}
									}
								}
		
								if (getSpecialAbility(SpecialAbility.SPECATK_RAMPAGE) > 0 && !specialed)
								{
									int rampage_chance = 0;
									//int rampage_chance = GetSpecialAbilityParam(SPECATK_RAMPAGE, 0);
									rampage_chance = rampage_chance > 0 ? rampage_chance : 20;
									if (MathUtils.Roll(rampage_chance)) {
										ExtraAttackOptions opts = new ExtraAttackOptions();
										/*
										int cur = GetSpecialAbilityParam(SPECATK_RAMPAGE, 3);
										if (cur > 0) {
											opts.damage_flat = cur;
										}
		
										cur = GetSpecialAbilityParam(SPECATK_RAMPAGE, 4);
										if (cur > 0) {
											opts.armor_pen_percent = cur / 100.0f;
										}
		
										cur = GetSpecialAbilityParam(SPECATK_RAMPAGE, 5);
										if (cur > 0) {
											opts.armor_pen_flat = cur;
										}
		
										cur = GetSpecialAbilityParam(SPECATK_RAMPAGE, 6);
										if (cur > 0) {
											opts.crit_percent = cur / 100.0f;
										}
		
										cur = GetSpecialAbilityParam(SPECATK_RAMPAGE, 7);
										if (cur > 0) {
											opts.crit_flat = cur;
										}*/
										Rampage(opts);
										specialed = true;
									}
								}
		
								if (getSpecialAbility(SpecialAbility.SPECATK_AREA_RAMPAGE) > 0 && !specialed)
								{
									int rampage_chance = 0;
									//int rampage_chance = GetSpecialAbilityParam(SPECATK_AREA_RAMPAGE, 0);
									rampage_chance = rampage_chance > 0 ? rampage_chance : 20;
									if (MathUtils.Roll(rampage_chance)) {
										ExtraAttackOptions opts;
										/*
										int cur = GetSpecialAbilityParam(SPECATK_AREA_RAMPAGE, 3);
										if (cur > 0) {
											opts.damage_flat = cur;
										}
		
										cur = GetSpecialAbilityParam(SPECATK_AREA_RAMPAGE, 4);
										if (cur > 0) {
											opts.armor_pen_percent = cur / 100.0f;
										}
		
										cur = GetSpecialAbilityParam(SPECATK_AREA_RAMPAGE, 5);
										if (cur > 0) {
											opts.armor_pen_flat = cur;
										}
		
										cur = GetSpecialAbilityParam(SPECATK_AREA_RAMPAGE, 6);
										if (cur > 0) {
											opts.crit_percent = cur / 100.0f;
										}
		
										cur = GetSpecialAbilityParam(SPECATK_AREA_RAMPAGE, 7);
										if (cur > 0) {
											opts.crit_flat = cur;
										}*/
		
										// TODO AreaRampage
										//AreaRampage(opts);
										specialed = true;
									}
								}
							}
		
							//now off hand
							if (
									//attack_dw_timer.Check() && 
									canThisClassDualWield()
									)
								DoOffHandAttackRounds(target, null);
		
							//now special attacks (kick, etc)
							if (isNPC())
								doClassAttacks(defender);
						
							autoAttack.setLastUpdatedTimeNow(this);
						}
	
					}
					
					if (getAiLastCastCheck())
					{
						AI_EngagedCastCheck();
						this.setAiLastCast();
					}
	
				}	//end is within combat rangepet
				else {
					//we cannot reach our target...
					//underwater stuff only works with water maps in the zone!
					
					/*
					if (IsNPC() && IsUnderwaterOnly() && zone->HasWaterMap()) {
						auto targetPosition = glm::vec3(target->GetX(), target->GetY(), target->GetZ());
						if (!zone->watermap->InLiquid(targetPosition)) {
							Mob *tar = hate_list.GetEntWithMostHateOnList(this);
							if (tar == target) {
								WipeHateList();
								Heal();
								BuffFadeAll();
								AI_walking_timer->Start(100);
								pLastFightingDelayMoving = Timer::GetCurrentTime();
								return;
							}
							else if (tar != nullptr) {
								SetTarget(tar);
								return;
							}
						}
					}
					*/
	
					/*
					// See if we can summon the mob to us
					if (!HateSummon())
					{
						//could not summon them, check ranged...
						if (GetSpecialAbility(SPECATK_RANGED_ATK))
							doranged = true;
	
						// Now pursue
						// TODO: Check here for another person on hate list with close hate value
						if (AI_PursueCastCheck()) {
							//we did something, so do not process movement.
						}
						else if (AI_movement_timer->Check() && target)
						{
							if (!IsRooted()) {
								Log(Logs::Detail, Logs::AI, "Pursuing %s while engaged.", target->GetName());
								if (!RuleB(Pathing, Aggro) || !zone->pathing)
									CalculateNewPosition(target->GetX(), target->GetY(), target->GetZ(), GetRunspeed());
								else
								{
									bool WaypointChanged, NodeReached;
	
									glm::vec3 Goal = UpdatePath(target->GetX(), target->GetY(), target->GetZ(),
										GetRunspeed(), WaypointChanged, NodeReached);
	
									if (WaypointChanged)
										tar_ndx = 20;
	
									CalculateNewPosition(Goal.x, Goal.y, Goal.z, GetRunspeed());
								}
	
							}
							else if (IsMoving()) {
								SetHeading(CalculateHeadingToTarget(target->GetX(), target->GetY()));
								SetCurrentSpeed(0);
	
							}
						}
					}
					
					*/
				}
			}
			else {
				//if (m_PlayerState & static_cast<uint32>(PlayerState::Aggressive))
					//SendRemovePlayerState(PlayerState::Aggressive);
	
				//if (IsPetStop()) // pet stop won't be engaged, so we will always get here and we want the above branch to execute
					//return;
	
				/*
				if(
						//zone->CanDoCombat() 
						&& AI_feign_remember_timer->Check()) {
					// 6/14/06
					// Improved Feign Death Memory
					// check to see if any of our previous feigned targets have gotten up.
					std::set<uint32>::iterator RememberedCharID;
					RememberedCharID = feign_memory_list.begin();
					while (RememberedCharID != feign_memory_list.end()) {
						Client* remember_client = entity_list.GetClientByCharID(*RememberedCharID);
						if (remember_client == nullptr) {
							//they are gone now...
							RememberedCharID = feign_memory_list.erase(RememberedCharID);
						} else if (!remember_client->GetFeigned()) {
							AddToHateList(remember_client->CastToMob(),1);
							RememberedCharID = feign_memory_list.erase(RememberedCharID);
							break;
						} else {
							//they are still feigned, carry on...
							++RememberedCharID;
						}
					}
				}
				*/
				
				if (AI_IdleCastCheck())
				{
					//we processed a spell action, so do nothing else.
				}
				// Aquire target
				else if (
						this.getEntityTarget() == null
						//zone->CanDoCombat() && 
						//CastToNPC()->WillAggroNPCs() && 
						//AI_scan_area_timer->Check()
						)
				{
					/*
					* NPC to NPC aggro checking, npc needs npc_aggro flag
					*/
					
					// handled by NPC Check for enemies timer for now
	
					//Mob* temp_target = entity_list.AICheckNPCtoNPCAggro(this, GetAggroRange(), GetAssistRange());
					//if (temp_target){
					//	AddToHateList(temp_target);
					//}
	
					//AI_scan_area_timer->Disable();
					//AI_scan_area_timer->Start(RandomTimer(RuleI(NPC, NPCToNPCAggroTimerMin), RuleI(NPC, NPCToNPCAggroTimerMax)), false);
	
				}
				// TODO - Manually AI movement, following etc
			}
	
			//Do Ranged attack here
			if(doranged)
				RangedAttack(defender);
		} catch (CoreStateInitException e)
		{
			
		}
	}
	
	@Override
	public void AI_EngagedCastCheck() {
		if (isPlayer())
			return;

		if (!isNPC())
			return;

		if (this.livingentity == null)
			return;

		ISoliniaNPC npc;
		try {
			npc = StateManager.getInstance().getConfigurationManager().getNPC(this.getNpcid());
			if (npc.getClassid() < 1)
				return;

			aiEngagedCastCheck(StateManager.getInstance().getPlugin(), npc, this.getAttackTarget(), this.getEffectiveLevel(true));
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public EntityEquipment getEquipment()
	{
		return this.getBukkitLivingEntity().getEquipment();
	}

	@Override
	public void ClientProcess(boolean wasTriggeredManually) {
		try
		{
			if (this.getBukkitLivingEntity() == null)
				return;
			
			if (!(this.getBukkitLivingEntity() instanceof Player))
				return;
			
			// Tidy up 
			if (this.getBukkitLivingEntity().isDead())
			{
				EntityAutoAttack autoAttack = StateManager.getInstance().getEntityManager().getEntityAutoAttack(this.getBukkitLivingEntity());
				if (!wasTriggeredManually && autoAttack.isAutoAttacking())
				{
					StateManager.getInstance().getEntityManager().setEntityAutoAttack(this.getBukkitLivingEntity(), false);
				}
				return;
			}
	
			// TODO if (RespawnFromHoverTimer.Check())
			
			// TODO Tracking handled elsewhere now
			//if (IsTracking() && (ClientVersion() >= EQEmu::versions::ClientVersion::SoD) && TrackingTimer.Check())
			
			// TODO
			// SendHPUpdate calls hpupdate_timer.Start so it can delay this timer, so lets not reset with the check
			// since the function will anyways
			
			// TODO if (linkdead_timer.Check()) {
			// LeaveGroup();
			
			if (!wasTriggeredManually && !this.isPlayer())
				AI_Process();
			
			boolean may_use_attacks = false;
			/*
			Things which prevent us from attacking:
				- being under AI control, the AI does attacks
				- being dead
				- casting a spell and bard check
				- not having a target
				- being stunned or mezzed
				- having used a ranged weapon recently
			 */
		
			LivingEntity target = getEntityTarget();
			boolean targetIsDead = false;
			boolean targetIsInvul = false;
			
			ISoliniaLivingEntity defender = null;
			if (target != null && target instanceof LivingEntity)
				defender = SoliniaLivingEntityAdapter.Adapt(target);

			EntityAutoAttack autoAttack = StateManager.getInstance().getEntityManager().getEntityAutoAttack(this.getBukkitLivingEntity());
			
			if (autoAttack.canAutoAttack())
			{
				if (autoAttack.isAutoAttacking() && autoAttack.canAutoAttack())
				{
					if (target != null)
					{
						if (target.isDead())
						{
							if (this.getBukkitLivingEntity() instanceof Player)
							this.getBukkitLivingEntity().sendMessage(ChatColor.GRAY + "* Your target is dead!");
							StateManager.getInstance().getEntityManager().setEntityAutoAttack(this.getBukkitLivingEntity(), false);
							targetIsDead = true;
						}
						
						if (defender != null && defender.isFeignedDeath())
						{
							if (this.getBukkitLivingEntity() instanceof Player)
								this.getBukkitLivingEntity().sendMessage(ChatColor.GRAY + "* Your target dead!?");
							StateManager.getInstance().getEntityManager().setEntityAutoAttack(this.getBukkitLivingEntity(), false);
							targetIsDead = true;
						}
						
						if (defender != null && defender.isInvulnerable())
						{
							if (this.getBukkitLivingEntity() instanceof Player)
								this.getBukkitLivingEntity().sendMessage(ChatColor.GRAY + "* Your target invulnerable!");
							StateManager.getInstance().getEntityManager().setEntityAutoAttack(this.getBukkitLivingEntity(), false);
							targetIsInvul = true;
						}
						
						if (target instanceof Player)
						{
							if (((Player)target).getGameMode() != GameMode.SURVIVAL)
							{
								if (this.getBukkitLivingEntity() instanceof Player)
									this.getBukkitLivingEntity().sendMessage(ChatColor.GRAY + "* Your target is not in SURVIVAL gamemode!");
								
								StateManager.getInstance().getEntityManager().setEntityAutoAttack(this.getBukkitLivingEntity(), false);
								targetIsInvul = true;
							}
						}
					} else {
						if (this.getBukkitLivingEntity() instanceof Player)
						{
							this.sendMessage(ChatColor.GRAY + "* You have no target to auto attack");
							StateManager.getInstance().getEntityManager().setEntityAutoAttack(this.getBukkitLivingEntity(), false);
						}
					}
				}
				
				if (autoAttack.canAutoAttack() && !targetIsInvul && !targetIsDead && canAttackTarget(defender))
				{
					if (isPlayer()
						&& !this.isCasting()
						&& !isStunned() && !isFeared() && !isMezzed() && (wasTriggeredManually || autoAttack.isAutoAttacking())
						)
						may_use_attacks = true;
	
					/* TODO - Ranged has its own timer
						if (may_use_attacks && ranged_timer.Enabled()) {
							//if the range timer is enabled, we need to consider it
							if (!ranged_timer.Check(false)) {
								//the ranged timer has not elapsed, cannot attack.
								may_use_attacks = false;
							}
						}*/
				}
				
				//if (AutoFireEnabled()) {
				if (autoAttack.canAutoAttack() && may_use_attacks && ItemStackUtils.isRangedWeapon(getEquipment().getItemInMainHand()))
				{
					//if (ranged->GetItem() && ranged->GetItem()->ItemType == EQEmu::item::ItemTypeBow) {
					if (!getSoliniaItemInMainHand().isThrowing())
					{
						// if (GetTarget() && (GetTarget()->IsNPC() || GetTarget()->IsClient())) {
						if (target != null && target != this.getBukkitLivingEntity() && !target.isDead() && defender != null)
						{
							if (this.checkLosFN(defender,false)) {
								// if (AutoFireEnabled()) {
								RangedAttack(defender, false);
								autoAttack.setLastUpdatedTimeNow(this);
								//if (CheckDoubleRangedAttack())
								//	RangedAttack(GetTarget(), true);
							} else {
								getBukkitLivingEntity().sendMessage(ChatColor.GRAY + "* You do not have line of sight to your target!");
								autoAttack.setLastUpdatedTimeNow(this);
							}
						} else {
							autoAttack.setLastUpdatedTimeNow(this);
						}
					} else {
						//else if (ranged->GetItem() && (ranged->GetItem()->ItemType == EQEmu::item::ItemTypeLargeThrowing || ranged->GetItem()->ItemType == EQEmu::item::ItemTypeSmallThrowing)) {
						// Throwing weapon
						// if (GetTarget() && (GetTarget()->IsNPC() || GetTarget()->IsClient())) {
						if (target != null && target != this.getBukkitLivingEntity() && !target.isDead() && defender != null)
						{
							if (this.checkLosFN(defender,false)) {
								// if (AutoFireEnabled()) {
								ThrowingAttack(defender, false);
								autoAttack.setLastUpdatedTimeNow(this);
								//if (CheckDoubleRangedAttack())
								//	RangedAttack(GetTarget(), true);
							} else {
								getBukkitLivingEntity().sendMessage(ChatColor.GRAY + "* You do not have line of sight to your target!");
								autoAttack.setLastUpdatedTimeNow(this);
							}
						} else {
							autoAttack.setLastUpdatedTimeNow(this);
						}
					}
				}
				
				if (autoAttack.canAutoAttack() && target != null && may_use_attacks && !ItemStackUtils.isRangedWeapon(getEquipment().getItemInMainHand()))
				{
					Tuple<Boolean, String> canUseItem = this.canUseItem(getEquipment().getItemInMainHand());
					
					if (!combatRange(defender))
					{
						this.getBukkitLivingEntity().sendMessage("* You are too far from your target");
					} else if (target == this.getBukkitLivingEntity())
					{
						this.getBukkitLivingEntity().sendMessage("* Try attack something other than yourself");
					} else if (!this.isNPC() && !canUseItem.a())
					{
						if (getBukkitLivingEntity() instanceof Player)
							getBukkitLivingEntity().sendMessage(ChatColor.GRAY + "You cannot use this item ["+canUseItem.b()+"]");
					} else if (target.getHealth() > 0D)
					{
						ItemStack wpn = getEquipment().getItemInMainHand();
						tryWeaponProc(wpn, defender, InventorySlot.Primary);
						//TriggerDefensiveProcs(target, InventorySlot.Primary, false);
	
						doAttackRounds(defender, InventorySlot.Primary, false);
						//if (CheckAATimer(aaTimerRampage))
						//	entity_list.AEAttack(this, 30);
					}
	
				}
				
				if (autoAttack.canAutoAttack() && target != null && may_use_attacks && canThisClassDualWield() && !ItemStackUtils.isRangedWeapon(getEquipment().getItemInMainHand()))
				{
					boolean los_status = this.checkLosFN(defender,false);
					boolean los_status_facing= this.isFacingMob(defender);
	
					// Range check
					if (!combatRange(defender)) {
						// this is a duplicate message don't use it.
						//Message_StringID(MT_TooFarAway,TARGET_TOO_FAR);
					}
					// Don't attack yourself
					else if (target == this.getBukkitLivingEntity()) {
						//Message_StringID(MT_TooFarAway,TRY_ATTACKING_SOMEONE);
					}
					else if (!los_status || !los_status_facing)
					{
						//you can't see your target
					}
					else if (target.getHealth() > 0D) {
						tryIncreaseSkill(SkillType.DualWield, 1);
						if (checkDualWield()) {
							ItemStack wpn = getEquipment().getItemInOffHand();
							tryWeaponProc(wpn, defender, InventorySlot.Secondary);
	
							doAttackRounds(defender, InventorySlot.Secondary, false);
						}
					}
				}
				
				autoAttack.setLastUpdatedTimeNow(this);
			} else {
				// waiting on auto attack timer
			}

			
		} catch (CoreStateInitException e)
		{
			
		}
	}
	
	private void ThrowingAttack(ISoliniaLivingEntity other, boolean CanDoubleAttack) {
		//conditions to use an attack checked before we are called
		if (other == null)
			return;
		
		if (this.getBukkitLivingEntity() == null)
			return;
		
		try
		{
			//make sure the attack and ranged timers are up
			//if the ranged timer is disabled, then they have no ranged weapon and shouldent be attacking anyhow
			EntityAutoAttack autoAttack = StateManager.getInstance().getEntityManager().getEntityAutoAttack(this.getBukkitLivingEntity());
			if(!CanDoubleAttack && !autoAttack.canAutoAttack()) {
				//Log(Logs::Detail, Logs::Combat, "Throwing attack canceled. Timer not up. Attack %d, ranged %d", attack_timer.GetRemainingTime(), ranged_timer.GetRemainingTime());
				// The server and client timers are not exact matches currently, so this would spam too often if enabled
				//Message(0, "Error: Timer not up. Attack %d, ranged %d", attack_timer.GetRemainingTime(), ranged_timer.GetRemainingTime());
				return;
			}
	
			//int ammo_slot = EQEmu::invslot::slotRange;
			//const EQEmu::ItemInstance* RangeWeapon = m_inv[EQEmu::invslot::slotRange];
			
			ItemStack RangeWeapon = getEquipment().getItemInMainHand();
			if (RangeWeapon == null)
				return;
			ISoliniaItem item = null;
			try {
				item = SoliniaItemAdapter.Adapt(RangeWeapon);
			} catch (SoliniaItemException e) {
				return;
			}
			
			if (item == null || !ItemStackUtils.isRangedWeapon(RangeWeapon)) {
				//Log(Logs::Detail, Logs::Combat, "Ranged attack canceled. Missing or invalid ranged weapon (%d) in slot %d", GetItemIDAt(EQEmu::invslot::slotRange), EQEmu::invslot::slotRange);
				//Message(0, "Error: Rangeweapon: GetItem(%i)==0, you have nothing to throw!", GetItemIDAt(EQEmu::invslot::slotRange));
				this.getBukkitLivingEntity().sendMessage("You have nothing to throw!");
				return;
			}
	
			if (!item.isThrowing()) {
				//Log(Logs::Detail, Logs::Combat, "Ranged attack canceled. Ranged item %d is not a throwing weapon. type %d.", item->ItemType);
				//Message(0, "Error: Rangeweapon: GetItem(%i)==0, you have nothing useful to throw!", GetItemIDAt(EQEmu::invslot::slotRange));
				this.getBukkitLivingEntity().sendMessage("You have nothing useful to throw!");
				return;
			}
	
			//float range = item->Range + GetRangeDistTargetSizeMod(other);
			//Log(Logs::Detail, Logs::Combat, "Calculated bow range to be %.1f", range);
			//range *= range;
			/*float dist = DistanceSquared(m_Position, other->GetPosition());
			if(dist > range) {
				Log(Logs::Detail, Logs::Combat, "Throwing attack out of range... client should catch this. (%f > %f).\n", dist, range);
				Message_StringID(13,TARGET_OUT_OF_RANGE);//Client enforces range and sends the message, this is a backup just incase.
				return;
			}
			else if(dist < (RuleI(Combat, MinRangedAttackDist)*RuleI(Combat, MinRangedAttackDist))){
				Message_StringID(15,RANGED_TOO_CLOSE);//Client enforces range and sends the message, this is a backup just incase.
			}*/
			
			double dist = other.getLocation().distanceSquared(this.getBukkitLivingEntity().getLocation());
	
			if (dist < Utils.MinRangedAttackDist)
			{
				this.getBukkitLivingEntity().sendMessage("Target too close");
				return;
			}
	
			if(!isAttackAllowed(other, false) ||
					isCasting() ||
					//isSitting() ||
					(DivineAura() /*&& !GetGM()*/) ||
					isStunned() ||
					//isFeared() ||
					isMezzed() //||
					//(GetAppearance() == eaDead)
					){
					return;
				}
			
			DoThrowingAttackDmg(other, RangeWeapon, item);
			autoAttack.setLastUpdatedTimeNow(this);
			
			//consume ammo
			
			if (isPlayer())
			{
				Player player = (Player)this.getBukkitLivingEntity();
				int newAmount = RangeWeapon.getAmount() - 1;
	
				if (newAmount < 1) {
					getEquipment().setItemInMainHand(null);
					player.updateInventory();
	
				} else {
					// To prevent a trap you must cancel event here
					RangeWeapon.setAmount(newAmount);
					getEquipment().setItemInMainHand(RangeWeapon);
					player.updateInventory();
				}
			}
			
			tryIncreaseSkill(SkillType.Throwing,1);
			commonBreakInvisibleFromCombat();
		} catch (CoreStateInitException e)
		{
			
		}
	}

	private void DoThrowingAttackDmg(ISoliniaLivingEntity other, ItemStack rangeWeapon, ISoliniaItem item) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public RampageList getRampageArray()
	{
		try {
			return StateManager.getInstance().getEntityManager().getRampageList(this.getBukkitLivingEntity().getUniqueId());
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}
		
	@Override
	public boolean Rampage(ExtraAttackOptions opts)
	{
		RampageList rampageArray = getRampageArray();
		if (rampageArray == null)
			return false;
		
		int index_hit = 0;
		if (!IsPet())
			ChatUtils.SendHint(this.getBukkitLivingEntity(), HINT.NPC_RAMPAGE, "", true);
		else
			ChatUtils.SendHint(this.getBukkitLivingEntity(), HINT.PET_FLURRY, "", true);

		// TODO
		int rampage_targets = 0;
		//int rampage_targets = GetSpecialAbilityParam(SpecialAbility.SPECATK_RAMPAGE, 1);
		
		if (rampage_targets == 0) // if set to 0 or not set in the DB
			rampage_targets = 1; // DefaultRampageTargets
		if (rampage_targets > 3) // MaxRampageTargets
			rampage_targets = 3;

		// TODO Active tracking of rampaging
		//m_specialattacks = eSpecialAttacks::Rampage;
		for (Integer hashKey : rampageArray.mobList.keySet()) {
			if (index_hit >= rampage_targets)
				break;
			
			UUID uuid = rampageArray.mobList.get(hashKey);
			if (uuid == null)
				continue;
			
			// range is important
			Entity m_target = Bukkit.getEntity(uuid);
			if (m_target != null) {
				// never rampage my target
				if (m_target == this.getEntityTarget())
					continue;
				
				if (this.getBukkitLivingEntity().getLocation().distanceSquared(m_target.getLocation()) <= Utils.NPC_RAMPAGE_RANGE2)
				{
					ProcessAttackRounds(m_target, opts);
					index_hit++;
				}
			}
		}

		/*
		if (index_hit < rampage_targets) {
			// so we go over in reverse order and skip range check
			// lets do it this way to still support non-live-like >1 rampage targets
			// likely live is just a fall through of the last valid mob
			for (auto i = getRampageArray().crbegin(); i != RampageArray.crend(); ++i) {
				if (index_hit >= rampage_targets)
					break;
				auto m_target = entity_list.GetMob(*i);
				if (m_target) {
					if (m_target == GetTarget())
						continue;
					ProcessAttackRounds(m_target, opts);
					index_hit++;
				}
			}
		}
		*/

		// TODO Active tracking of rampaging
		//m_specialattacks = eSpecialAttacks::None;

		return true;
	}

	private void ProcessAttackRounds(Entity target, ExtraAttackOptions opts) {
		if (target != null) {
			DoMainHandAttackRounds(target, opts);
			if (canThisClassDualWield())
				DoOffHandAttackRounds(target, opts);
		}
		return;
	}
	
	void DoMainHandAttackRounds(Entity target, ExtraAttackOptions opts)
	{
		try
		{
			if (target == null || !(target instanceof LivingEntity))
				return;
			
			ISoliniaLivingEntity solTarget = SoliniaLivingEntityAdapter.Adapt((LivingEntity)target);
			if (solTarget == null)
				return;
	
			/* TODO
			if (RuleB(Combat, UseLiveCombatRounds)) {
				// A "quad" on live really is just a successful dual wield where both double attack
				// The mobs that could triple lost the ability to when the triple attack skill was added in
				Attack(target, EQEmu::invslot::slotPrimary, false, false, false, opts);
				if (CanThisClassDoubleAttack() && CheckDoubleAttack()) {
					Attack(target, EQEmu::invslot::slotPrimary, false, false, false, opts);
					if ((IsPet() || IsTempPet()) && IsPetOwnerClient()) {
						int chance = spellbonuses.PC_Pet_Flurry + itembonuses.PC_Pet_Flurry + aabonuses.PC_Pet_Flurry;
						if (chance && zone->random.Roll(chance))
							Flurry(nullptr);
					}
				}
				return;
			}
			*/
	
			if (isNPC()) {
				int n_atk = 0;
				//TODO int16 n_atk = CastToNPC()->GetNumberOfAttacks();
				if (n_atk <= 1) {
					Attack(solTarget, InventorySlot.Primary, false, false, false);
				}
				else {
					for (int i = 0; i < n_atk; ++i) {
						Attack(solTarget, InventorySlot.Primary, false, false, false);
					}
				}
			}
			else {
				Attack(solTarget, InventorySlot.Primary, false, false, false);
			}
	
			// we use this random value in three comparisons with different
			// thresholds, and if its truely random, then this should work
			// out reasonably and will save us compute resources.
			int RandRoll = MathUtils.RandomBetween(0, 99);
			if ((canThisClassDoubleAttack() || getSpecialAbility(SpecialAbility.SPECATK_TRIPLE) > 0 || getSpecialAbility(SpecialAbility.SPECATK_QUAD) > 0)
				// check double attack, this is NOT the same rules that clients use...
				&&
				RandRoll < (getMentorLevel() + Utils.NPCDualAttackModifier)) {
				Attack(solTarget, InventorySlot.Primary, false, false, false);
				// lets see if we can do a triple attack with the main hand
				// pets are excluded from triple and quads...
				if ((getSpecialAbility(SpecialAbility.SPECATK_TRIPLE) > 0 || getSpecialAbility(SpecialAbility.SPECATK_QUAD) > 0) && !IsPet() &&
					RandRoll < (getMentorLevel() + Utils.NPCTripleAttackModifier)) {
					Attack(solTarget, InventorySlot.Primary, false, false, false);
					// now lets check the quad attack
					if (getSpecialAbility(SpecialAbility.SPECATK_QUAD) > 0 && RandRoll < (getMentorLevel() + Utils.NPCQuadAttackModifier)) {
						Attack(solTarget, InventorySlot.Primary, false, false, false);
					}
				}
			}
		} catch (CoreStateInitException e)
		{
			
		}
	}
	
	void DoOffHandAttackRounds(Entity target, ExtraAttackOptions opts)
	{
		if (target == null || !(target instanceof LivingEntity))
			return;
			
			try
			{
			ISoliniaLivingEntity solTarget = SoliniaLivingEntityAdapter.Adapt((LivingEntity)target);
			if (solTarget == null)
				return;
			// Mobs will only dual wield w/ the flag or have a secondary weapon
			// For now, SPECATK_QUAD means innate DW when Combat:UseLiveCombatRounds is true
			/*if (
					//(GetSpecialAbility(SPECATK_INNATE_DW) ||
					//(RuleB(Combat, UseLiveCombatRounds) && GetSpecialAbility(SPECATK_QUAD))) ||
					//GetEquipment(EQEmu::textures::weaponSecondary) != 0) 
			{			
			}*/
			
			if (checkDualWield()) {
				Attack(solTarget, InventorySlot.Secondary, false, false, false);
				if (canThisClassDoubleAttack() && getMentorLevel() > 35 && checkDoubleAttack()) {
					Attack(solTarget, InventorySlot.Secondary, false, false, false);
	
					if ((IsPet() || this.isCurrentlyNPCPet())// && IsPetOwnerClient()
							) {
						int chance = getSpellBonuses(SpellEffectType.PetFlurry) + getItemBonuses(SpellEffectType.PetFlurry) + getAABonuses(SpellEffectType.PetFlurry);
						if (chance > 0 && MathUtils.Roll(chance))
							Flurry(null);
					}
				}
			}
		} catch (CoreStateInitException e)
		{
			
		}
	}

	private boolean Flurry(ExtraAttackOptions opts) {
		// this is wrong, flurry is extra attacks on the current target
		LivingEntity target = getEntityTarget();
		try
		{
			if (target != null) {
				ISoliniaLivingEntity defender = null;
				if (target != null && target instanceof LivingEntity)
					defender = SoliniaLivingEntityAdapter.Adapt(target);
				
				if (defender == null)
					return false;
				
				if (!isCurrentlyNPCPet()) {
					ChatUtils.SendHint(this.getBukkitLivingEntity(), HINT.NPC_FLURRY, "", true);
				} else {
					ChatUtils.SendHint(this.getBukkitLivingEntity(), HINT.PET_FLURRY, "", true);
				}
	
				//int num_attacks = GetSpecialAbilityParam(SPECATK_FLURRY, 1);
				int num_attacks = 0;
				//MaxFlurryHits = 2
				num_attacks = num_attacks > 0 ? num_attacks : 2;
				for (int i = 0; i < num_attacks; i++)
					Attack(defender, InventorySlot.Primary, false, false, false);
			} else {
				return false;
			}
			return true;
		} catch (CoreStateInitException e)
		{
			return false;
		}
	}

	@Override
	public boolean canDualWield() {
		if (getClassObj() == null)
			return false;

		if (getClassObj().canDualWield() == false)
			return false;

		if (getClassObj().getDualwieldlevel() > getMentorLevel())
			return false;

		return true;
	}

	private boolean checkDualWield() {
		int chance = getSkill(SkillType.DualWield) + getMentorLevel();

		chance += getSpellBonuses(SpellEffectType.Ambidexterity) + getItemBonuses(SpellEffectType.Ambidexterity) + getAABonuses(SpellEffectType.Ambidexterity);
		int per_inc = getSpellBonuses(SpellEffectType.DualWieldChance) + getItemBonuses(SpellEffectType.DualWieldChance) + getAABonuses(SpellEffectType.DualWieldChance);
		if (per_inc > 0)
			chance += chance * per_inc / 100;

		return MathUtils.RandomBetween(1, 375) <= chance;
	}

	private void RangedAttack(ISoliniaLivingEntity other) {
		RangedAttack(other, false);
	}
	
	private void RangedAttack(ISoliniaLivingEntity other, boolean canDoubleAttack) {
		//conditions to use an attack checked before we are called
		if (other == null)
			return;
		//make sure the attack and ranged timers are up
		//if the ranged timer is disabled, then they have no ranged weapon and shouldent be attacking anyhow
		
		ItemStack rangedItemStack = getEquipment().getItemInMainHand();
		try
		{
			ISoliniaItem RangeWeapon = null;
			try
			{
				RangeWeapon = SoliniaItemAdapter.Adapt(rangedItemStack);
			} catch (SoliniaItemException e) {
				
			}
	
			//locate ammo
	
			if (RangeWeapon == null) {
				//Log(Logs::Detail, Logs::Combat, "Ranged attack canceled. Missing or invalid ranged weapon (%d) in slot %d", GetItemIDAt(EQEmu::invslot::slotRange), EQEmu::invslot::slotRange);
				//Message(0, "Error: Rangeweapon: GetItem(%i)==0, you have no bow!", GetItemIDAt(EQEmu::invslot::slotRange));
				return;
			}
			if (!this.hasArrowsInInventory()) {
				getBukkitLivingEntity().sendMessage(
						"* You do not have sufficient arrows in your inventory to auto fire your bow!");
				return;
			}	
	
			if (RangeWeapon.getItemType() != ItemType.BowArchery) {
				//Log(Logs::Detail, Logs::Combat, "Ranged attack canceled. Ranged item is not a bow. type %d.", RangeItem->ItemType);
				//Message(0, "Error: Rangeweapon: Item %d is not a bow.", RangeWeapon->GetID());
				return;
			}

			//Log(Logs::Detail, Logs::Combat, "Shooting %s with bow %s (%d) and arrow %s (%d)", other->GetName(), RangeItem->Name, RangeItem->ID, AmmoItem->Name, AmmoItem->ID);
	
			// todo get range of item
			double range = Utils.MAX_ENTITY_AGGRORANGE;
			//Log(Logs::Detail, Logs::Combat, "Calculated bow range to be %.1f", range);
			//range *= range;
			double dist = other.getLocation().distance(this.getBukkitLivingEntity().getLocation());
			if(dist > range) {
				//Log(Logs::Detail, Logs::Combat, "Ranged attack out of range... client should catch this. (%f > %f).\n", dist, range);
				//Message_StringID(13,TARGET_OUT_OF_RANGE);//Client enforces range and sends the message, this is a backup just incase.
				this.getBukkitLivingEntity().sendMessage("Target out of range");
				return;
			}
			else if(!this.isNPC() && dist < Utils.MinRangedAttackDist){
				this.getBukkitLivingEntity().sendMessage("Target too close");
				return;
			}
	
			if(!isAttackAllowed(other, false) ||
				isCasting() ||
				//isSitting() ||
				(DivineAura() /*&& !GetGM()*/) ||
				isStunned() ||
				//isFeared() ||
				isMezzed() //||
				//(GetAppearance() == eaDead)
				){
				return;
			}
	
			//Shoots projectile and/or applies the archery damage
			doArcheryAttackDmg(other, rangedItemStack,RangeWeapon,0,0,0,0,0, 4.0F);
	
			//EndlessQuiver AA base1 = 100% Chance to avoid consumption arrow.
			int ChanceAvoidConsume = getItemBonuses(SpellEffectType.ConsumeProjectile) + getSpellBonuses(SpellEffectType.ConsumeProjectile) + getSpellBonuses(SpellEffectType.ConsumeProjectile);
	
			if (isPlayer())
			if (/*RangeItem->ExpendableArrow || */ChanceAvoidConsume < 0 || (ChanceAvoidConsume < 100 && MathUtils.RandomBetween(0,99) > ChanceAvoidConsume)){
				ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player)this.getBukkitLivingEntity());
				if (solPlayer != null)
				{
					// last minute double check
					if (this.hasArrowsInInventory())
					{
						ItemStack is = ((Player)this.getBukkitLivingEntity()).getInventory().getItem(((Player)this.getBukkitLivingEntity()).getInventory().first(Material.ARROW));
				        is.setAmount(is.getAmount()-1);
				        ((Player)this.getBukkitLivingEntity()).updateInventory();
					}
				}
				//Log(Logs::Detail, Logs::Combat, "Consumed one arrow from slot %d", ammo_slot);
			} else {
				//Log(Logs::Detail, Logs::Combat, "Endless Quiver prevented ammo consumption.");
			}
	
			tryIncreaseSkill(SkillType.Archery,1);
			commonBreakInvisibleFromCombat();
		} catch (CoreStateInitException e)
		{
			
		}
	}

	private void commonBreakInvisibleFromCombat() {
		// TODO Auto-generated method stub
		//break invis when you attack
		if (this.isInvisible()) {
			//Log(Logs::Detail, Logs::Combat, "Removing invisibility due to melee attack.");
			buffFadeByEffect(SpellEffectType.Invisibility);
			buffFadeByEffect(SpellEffectType.Invisibility2);
		}
		if (this.isInvisibleToUndead()) {
			//Log(Logs::Detail, Logs::Combat, "Removing invisibility vs. undead due to melee attack.");
			buffFadeByEffect(SpellEffectType.InvisVsUndead);
			buffFadeByEffect(SpellEffectType.InvisVsUndead2);
		}
		if (this.isInvisibleToAnimals()) {
			//Log(Logs::Detail, Logs::Combat, "Removing invisibility vs. animals due to melee attack.");
			buffFadeByEffect(SpellEffectType.InvisVsAnimals);
		}

		cancelSneakHide();

		if (getSpellBonuses(SpellEffectType.NegateIfCombat) > 0)
			buffFadeByEffect(SpellEffectType.NegateIfCombat);

	}

	private void cancelSneakHide() {
		if (this.getBukkitLivingEntity() instanceof Player)
			((Player)this.getBukkitLivingEntity()).setSneaking(false);
		if (this.getBukkitLivingEntity().hasPotionEffect(PotionEffectType.INVISIBILITY))
			this.getBukkitLivingEntity().removePotionEffect(PotionEffectType.INVISIBILITY);
	}

	private boolean isInvisibleToAnimals() {
		return this.hasSpellEffectType(SpellEffectType.InvisVsAnimals);
	}

	private boolean isInvisibleToUndead() {
		return this.hasSpellEffectType(SpellEffectType.InvisVsUndead) || this.hasSpellEffectType(SpellEffectType.InvisVsUndead2);
	}

	private boolean isInvisible() {
		return this.hasSpellEffectType(SpellEffectType.Invisibility) || this.hasSpellEffectType(SpellEffectType.Invisibility2);
	}

	private void doArcheryAttackDmg(ISoliniaLivingEntity other, ItemStack rangeWeaponItemStack, ISoliniaItem rangedWeapon, int weapon_damage, int chance_mod, int focus, int ReuseTime, int range_id, float speed) {
		if ((other == null ||
			     ((isPlayer() && this.getBukkitLivingEntity().isDead()) || (other.isPlayer() && other.getBukkitLivingEntity().isDead())) ||
			     (!isAttackAllowed(other, false)) || (other.getInvul() || other.getSpecialAbility(SpecialAbility.IMMUNE_MELEE) > 0))) {
				return;
			}
		
		
		sendItemAnimation(other, SkillType.Archery);
		//Log(Logs::Detail, Logs::Combat, "Ranged attack hit %s.", other->GetName());
		
		int hate = 0;
		int TotalDmg = 0;
		int WDmg = 0;
		int ADmg = 0;
		//int ADmg = 0;
		if (weapon_damage < 1) {
			WDmg = getWeaponDamage(other, rangeWeaponItemStack, 0);
			ADmg = 4;
			//ADmg = getWeaponDamage(other, Ammo);
		} else {
			WDmg = weapon_damage;
		}
		
		if (focus > 0) // From FcBaseEffects
			WDmg += WDmg * focus / 100;
		
		if (WDmg > 0 || ADmg > 0) {
			if (WDmg < 0)
				WDmg = 0;
			if (ADmg < 0)
				ADmg = 0;
			int MaxDmg = WDmg + ADmg;
			hate = ((WDmg + ADmg));
			
			if (MaxDmg == 0)
				MaxDmg = 1;
			
			DamageHitInfo my_hit = new DamageHitInfo();
			my_hit.base_damage = MaxDmg;

			my_hit.min_damage = 0;
			my_hit.damage_done = 1;

			my_hit.skill = SkillType.Archery;
			my_hit.offense = offense(my_hit.skill);
			my_hit.tohit = getTotalToHit(my_hit.skill, chance_mod);
			my_hit.hand = InventorySlot.Range;
			
			doAttack(other, my_hit);
			TotalDmg = my_hit.damage_done;
		} else {
			TotalDmg = Utils.DMG_INVULNERABLE;
		}
		
		if (isPlayer() && !isFeigned())
			other.addToHateList(this.getBukkitLivingEntity().getUniqueId(), hate, false);
		
		other.Damage(this, TotalDmg, Utils.SPELL_UNKNOWN, SkillType.Archery,true,-1,false);
		
		// TODO Skill Proc Success
		
		// Weapon Proc
		if (rangedWeapon != null && other.getBukkitLivingEntity() != null && !other.getBukkitLivingEntity().isDead())
			tryWeaponProc(rangeWeaponItemStack, other, InventorySlot.Range);
		
		
	}

	private void sendItemAnimation(ISoliniaLivingEntity other, SkillType skillType) {
		if (skillType.equals(SkillType.Archery))
		{
			net.minecraft.server.v1_15_R1.Entity ep = ((CraftEntity) getBukkitLivingEntity()).getHandle();
			
			
			getBukkitLivingEntity().getWorld().playSound(getBukkitLivingEntity().getLocation(),
					Sound.ENTITY_ARROW_SHOOT, 1.0F, 1.0F);

			// Send shoot arrow to nearby people
			for (Entity listening : getBukkitLivingEntity().getNearbyEntities(20, 20, 20)) {
				if (listening instanceof Player)
					EntityUtils.sendAnimationPacket(getBukkitLivingEntity(),(Player)listening, SolAnimationType.SwingArm);
			}
			
			// Self send shoot arrow
			if (getBukkitLivingEntity() instanceof Player)
				EntityUtils.sendAnimationPacket(getBukkitLivingEntity(),(Player)getBukkitLivingEntity(), SolAnimationType.SwingArm);
			
			Arrow arrow = getBukkitLivingEntity().launchProjectile(Arrow.class);
			arrow.setPickupStatus(org.bukkit.entity.AbstractArrow.PickupStatus.DISALLOWED);
			arrow.setBounce(false);
			arrow.setVelocity(other.getBukkitLivingEntity().getEyeLocation().toVector()
                     .subtract(arrow.getLocation().toVector()).normalize().multiply(4));
		}
	}

	@Override
	public boolean hasArrowsInInventory() {
		if (!this.isPlayer())
			return true;
		
		try
		{
			ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player) getBukkitLivingEntity());
			return solPlayer.hasArrowsInInventory();
		} catch (CoreStateInitException e)
		{
			
		}
		
		return false;
	}
	
	@Override
	public void cureVampirism() {
		if (!this.isPlayer())
			return;
		
		try
		{
			if (getBukkitLivingEntity() != null)
			{
				ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player) getBukkitLivingEntity());
				if (solPlayer != null)
					solPlayer.cureVampirism();
			}
		} catch (CoreStateInitException e)
		{
			
		}
	}

	@Override
	public void tryWeaponProc(ItemStack weaponItemStack, ISoliniaLivingEntity on, int hand) {
		if (on == null) {
			setAttackTarget(null);
			return;
		}

		if (!isAttackAllowed(on, false)) {
			return;
		}

		if (DivineAura()) {
			return;
		}

		if (weaponItemStack == null) {
			trySpellProc(null, null, on, InventorySlot.Primary);
			return;
		}
		
		if (!ItemStackUtils.IsSoliniaItem(weaponItemStack))
			return;
		
		try
		{
			ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(weaponItemStack);
			if (item == null)
				return;
	
			// Innate + aug procs from weapons
			// TODO: powersource procs -- powersource procs are on invis augs, so shouldn't need anything extra
			tryWeaponProc(weaponItemStack, item, on, hand);
			// Procs from Buffs and AA both melee and range
			trySpellProc(weaponItemStack, item, on, hand);
		} catch (CoreStateInitException e)
		{
			
		}

		return;
	}

	private void trySpellProc(ItemStack inst, ISoliniaItem weapon, ISoliniaLivingEntity on, int hand) {
		float ProcBonus = (float) (this.getSpellBonuses(SpellEffectType.SpellProcChance)
				+ getItemBonuses(SpellEffectType.SpellProcChance) + getAABonuses(SpellEffectType.SpellProcChance) );

		float ProcChance = 0.0f;
		ProcChance = getProcChances(ProcBonus, hand);

		if (hand != InventorySlot.Primary) // Is Archery intened to proc at 50% rate?
			ProcChance /= 2;

		boolean rangedattk = false;
		if (weapon != null && hand == InventorySlot.Range) {
			if (// weapon.getItemType() == ItemType.Arrow ||
			weapon.getItemType() == ItemType.ThrowingWeapon || weapon.getItemType() == ItemType.BowArchery) {
				rangedattk = true;
			}
		}

		if (weapon == null && hand == InventorySlot.Range && getSpecialAbility(SpecialAbility.SPECATK_RANGED_ATK) > 0)
			rangedattk = true;

		/*
		 * for (int i = 0; i < MAX_PROCS; i++) {
		 * 
		 * }
		 */
		try {
			for (SoliniaActiveSpell activeSpell : StateManager.getInstance().getEntityManager()
					.getActiveEntitySpells(this.getBukkitLivingEntity()).getActiveSpells()) {
				if (!activeSpell.getSpell().isWeaponProc())
					continue;

				// Now we need to get the other proc
				int level_override = activeSpell.getSourceLevel();
				
				if (IsPet() && hand != InventorySlot.Primary)
					continue;

				for (ActiveSpellEffect effect : activeSpell.getActiveSpellEffects()) {
					if (effect.getSpellEffectType() != SpellEffectType.AddMeleeProc
							&& effect.getSpellEffectType() != SpellEffectType.WeaponProc)
						continue;

					ISoliniaSpell procSpell = StateManager.getInstance().getConfigurationManager().getSpell(effect.getBase());
					if (procSpell == null)
						continue;
					
					for(SpellEffect procSpellEffects : procSpell.getBaseSpellEffects())
					{
						if (!rangedattk) {
							// TODO Perma procs (AAs)

							// Spell procs (buffs)
							int echance = 100;
							if (procSpellEffects.getBase2() == 0)
								echance = 100;
							else
								echance = procSpellEffects.getBase2() + 100;


							float chance = ProcChance * (float) (echance / 100.0f);
							if (MathUtils.Roll(chance)) {
								ExecWeaponProc(null, procSpell, on, level_override);
								checkNumHitsRemaining(NumHit.OffensiveSpellProcs, 0, procSpell.getId());
							}
						}
					}
				}

			}
		} catch (CoreStateInitException e) {

		}

		/*
		 * TODO Skill Procs if (HasSkillProcs() && hand != EQEmu::invslot::slotRange) {
		 * //We check ranged skill procs within the attack functions. uint16 skillinuse
		 * = 28; if (weapon) skillinuse = GetSkillByItemType(weapon->ItemType);
		 * 
		 * TrySkillProc(on, skillinuse, 0, false, hand); }
		 */

		return;
	}
	
	@Override
	public boolean IsPet()
	{
		return isCurrentlyNPCPet();
	}
	
	@Override
	public boolean IsCorePet()
	{
		if (!this.isNPC())
			return false;
		
		if (!isCurrentlyNPCPet())
			return false;
		
		if (this.getNPC() == null)
			return false;
		
		return this.getNPC().isCorePet();
	}

	@Override
	public void tryWeaponProc(ItemStack inst, ISoliniaItem weapon, ISoliniaLivingEntity on, int hand) {
		if (weapon == null)
			return;

		SkillType skillinuse = SkillType.HandtoHand;
		int ourlevel = getMentorLevel();
		/*float ProcBonus = static_cast<float>(aabonuses.ProcChanceSPA +
			spellbonuses.ProcChanceSPA + itembonuses.ProcChanceSPA);*/
		
		float ProcBonus = (float)this.getAABonuses(SpellEffectType.ProcChance);
		// We only use the highest proc
		if ((float)this.getSpellBonuses(SpellEffectType.ProcChance) > (float)this.getSpellBonuses(SpellEffectType.ProcChance))
		{
			ProcBonus = (float)this.getSpellBonuses(SpellEffectType.ProcChance);
		}
		// Items are additive proc chance
		ProcBonus += (float)this.getItemBonuses(SpellEffectType.ProcChance) / 10.0f; // Combat Effects
		float ProcChance = getProcChances(ProcBonus, hand);

		if (hand != InventorySlot.Primary) //Is Archery intened to proc at 50% rate?
			ProcChance /= 2;

		// Try innate proc on weapon
		// We can proc once here, either weapon or one aug
		boolean proced = false; // silly bool to prevent augs from going if weapon does
		skillinuse = getSkillByItemType(weapon.getItemType());

		if (weapon.getWeaponabilityid() > 0 && IsValidSpell(weapon.getWeaponabilityid())) {
			try
			{
				ISoliniaSpell weaponSpell = StateManager.getInstance().getConfigurationManager().getSpell(weapon.getWeaponabilityid());
				if (weaponSpell != null)
				{
					float WPC = ProcChance * (100.0f + // Proc chance for this weapon
						(float)(weapon.getProcRate())) / 100.0f;
					boolean roll = MathUtils.Roll(WPC);

					if (roll) {	// 255 dex = 0.084 chance of proc. No idea what this number should be really.
						//if (weapon->Proc.Level2 > ourlevel) { TODO - Specific proc level
						if (weapon.getMinLevel() > getMentorLevel())
						{
							//Log(Logs::Detail, Logs::Combat, "Tried to proc (%s), but our level (%d) is lower than required (%d)",weapon->Name, ourlevel, weapon->Proc.Level2);
							/*if (isPet()) {
								Entity own = this.getOwnerEntity();
								if (own != null)
									own.sendMessage("Pet cannot use Proc");
							}
							else {
								Message_StringID(13, PROC_TOOLOW);
							}*/
						}
						else {
							/*Log(Logs::Detail, Logs::Combat,
								"Attacking weapon (%s) successfully procing spell %d (%.2f percent chance)",
								weapon->Name, weapon->Proc.Effect, WPC * 100);*/
							ExecWeaponProc(inst, weaponSpell, on, -1);
							proced = true;
						}
					}
				}
			} catch (CoreStateInitException e)
			{
				
			}
		}
		/* TODO Aug Procs
		if (!proced && inst) {
			for (int r = EQEmu::invaug::SOCKET_BEGIN; r <= EQEmu::invaug::SOCKET_END; r++) {
				const EQEmu::ItemInstance *aug_i = inst->GetAugment(r);
				if (!aug_i) // no aug, try next slot!
					continue;
				const EQEmu::ItemData *aug = aug_i->GetItem();
				if (!aug)
					continue;

				if (aug->Proc.Type == EQEmu::item::ItemEffectCombatProc && IsValidSpell(aug->Proc.Effect)) {
					float APC = ProcChance * (100.0f + // Proc chance for this aug
						static_cast<float>(aug->ProcRate)) / 100.0f;
					if (zone->random.Roll(APC)) {
						if (aug->Proc.Level2 > ourlevel) {
							if (IsPet()) {
								Mob *own = GetOwner();
								if (own)
									own->Message_StringID(13, PROC_PETTOOLOW);
							}
							else {
								Message_StringID(13, PROC_TOOLOW);
							}
						}
						else {
							ExecWeaponProc(aug_i, aug->Proc.Effect, on);
							if (RuleB(Combat, OneProcPerWeapon))
								break;
						}
					}
				}
			}
		}
		*/
		// TODO: Powersource procs -- powersource procs are from augs so shouldn't need anything extra

		return;
	}

	private void ExecWeaponProc(ItemStack inst, ISoliniaSpell weaponSpell, ISoliniaLivingEntity on, int level_override) {
		if(weaponSpell == null || on.getSpecialAbility(SpecialAbility.NO_HARM_FROM_CLIENT) > 0) {
			return;
		}

		/*if (IsNoCast())
			return;
*/
		if(!IsValidSpell(weaponSpell.getId())) { // Check for a valid spell otherwise it will crash through the function
			if(isPlayer()){
				getBukkitLivingEntity().sendMessage("Invalid spell proc " + weaponSpell.getId());
				//Log(Logs::Detail, Logs::Spells, "Player %s, Weapon Procced invalid spell %u", this->GetName(), spell_id);
			}
			return;
		}
		
		// TODO Twin Cast
		
		if (weaponSpell.isBeneficial() && (!isNPC())) { // TODO NPC innate procs don't take this path ever
			SpellFinished(weaponSpell.getId(), this, CastingSlot.Item, 0, -1, weaponSpell.getResistDiff(), true, level_override);
		}
		else if(!(on.isPlayer() && on.getBukkitLivingEntity().isDead())) { //dont proc on dead clients
			SpellFinished(weaponSpell.getId(), on, CastingSlot.Item, 0, -1, weaponSpell.getResistDiff(), true, level_override);
		}
	}

	@Override
	public boolean SpellFinished(int spell_id, ISoliniaLivingEntity spell_target, int castingslot, int mana_used,
			int inventory_slot, int resist_adjust) {
		return SpellFinished(spell_id, spell_target, castingslot, mana_used,
				inventory_slot, resist_adjust, false, -1);
	}
	
	@Override
	public boolean SpellFinished(int spell_id, ISoliniaLivingEntity spell_target, int castingslot, int mana_used,
			int inventory_slot, int resist_adjust, boolean isproc, int level_override) {
		
		if (spell_target == null)
			return false;
		
		if (!this.IsValidSpell(spell_id))
			return false;
		
		ISoliniaLivingEntity ae_center = null;
		
		// TODO
		
		try
		{
			//determine the type of spell target we have
			ISoliniaSpell spell = StateManager.getInstance().getConfigurationManager().getSpell(spell_id);
			// TODO check if outdoors
			// TODO check if levitate in non levtiate area
			// Check if spell blocked in area
			// Check if Blocked to GM
			// 
			
			CastAction_type CastAction = new CastAction_type();
			// TODO Determine Targets
			// TODO: AE Duration
			// TODO check line of sight to target if it's a detrimental spell
			// TODO check to see if target is a caster mob before performing a mana tap
			// TODO range check our target, if we have one and it is not us
			
			/*if(spell_target == null) {
				//Log(Logs::Detail, Logs::Spells, "Spell %d: Targeted spell, but we have no target", spell_id);
				return(false);
			}*/
			if (isproc) {
				spellOnTarget(spell_id, spell_target, false, true, resist_adjust, true, level_override);
			}/* else {
				if (SpellUtils.getSpellTargetType(spell.getTargettype()).equals(SpellTargetType.TargetOptional)){
					if (!trySpellProjectile(spell_target, spell_id))
						return false;
				}

				else if(!spellOnTarget(spell_id, spell_target, false, true, resist_adjust, false, level_override)) {
					if(spell.isBuffSpell() && spell.isBeneficialSpell()) {
						// Prevent mana usage/timers being set for beneficial buffs
						if(casting_spell_aa_id)
							interruptSpell();
						return false;
					}
				}
			}*/
			

			switch (SpellUtils.getSpellTargetType(spell.getTargettype())) {
			case Self:
				spell.tryApplyOnEntity(getBukkitLivingEntity(),
						getBukkitLivingEntity(), true,"");
				break;
			case Group:
				spell.tryApplyOnEntity(getBukkitLivingEntity(),
						getBukkitLivingEntity(), true,"");
				break;
			default:
				spell.tryApplyOnEntity(getBukkitLivingEntity(),
						spell_target.getBukkitLivingEntity(), true, "");		
				break;
			}
			
		} catch (CoreStateInitException e)
		{
			
		}
		
		return true;
	}

	private boolean spellOnTarget(int spell_id, ISoliniaLivingEntity spelltar, boolean reflect, boolean use_resist_adjust, int resist_adjust,
			boolean isproc, int level_override) {
		// TODO Auto-generated method stub
		// well we can't cast a spell on target without a target
		if(spelltar == null)
		{
			//Log(Logs::Detail, Logs::Spells, "Unable to apply spell %d without a target", spell_id);
			//Message(13, "SOT: You must have a target for this spell.");
			this.getBukkitLivingEntity().sendMessage("You must have a target for this spell");
			return false;
		}
		
		if(spelltar.isPlayer() && spelltar.getBukkitLivingEntity() != null && spelltar.getBukkitLivingEntity().isDead())
			return false;
		
		// TODO Check resurrection effect
		
		if(!IsValidSpell(spell_id))
			return false;
		
		//int caster_level = level_override > 0 ? level_override : getCasterLevel(spell_id);
		
		//mod_spell_cast(spell_id, spelltar, reflect, use_resist_adjust, resist_adjust, isproc);
		if(spelltar.getInvul() || spelltar.DivineAura()) {
			//Log(Logs::Detail, Logs::Spells, "Casting spell %d on %s aborted: they are invulnerable.", spell_id, spelltar->GetName());
			return false;
		}
		
		//cannot hurt untargetable mobs
		// resist check - every spell can be resisted, beneficial or not
		// add: ok this isn't true, eqlive's spell data is fucked up, buffs are
		// not all unresistable, so changing this to only check certain spells
		
		/* RECOURSE
		if (IsValidSpell(spells[spell_id].RecourseLink) && spells[spell_id].RecourseLink != spell_id)
			SpellFinished(spells[spell_id].RecourseLink, this, CastingSlot::Item, 0, -1, spells[spells[spell_id].RecourseLink].ResistDiff);
			*/
		
		return true;
	}

	private SkillType getSkillByItemType(ItemType itemType) {
		switch (itemType) {
		case OneHandSlashing:
			return SkillType.Slashing;
		case TwoHandSlashing:
			return SkillType.TwoHandSlashing;
		case OneHandPiercing:
			return SkillType.Piercing;
		case OneHandBlunt:
			return SkillType.Crushing;
		case TwoHandBlunt:
			return SkillType.TwoHandBlunt;
		case TwoHandPiercing:
			return SkillType.TwoHandPiercing;
		case BowArchery:
			return SkillType.Archery;
		case ThrowingWeapon:
			return SkillType.Throwing;
		case Martial:
			return SkillType.HandtoHand;
		default:
			return SkillType.HandtoHand;
		}
	}

	@Override
	public void doAttackRounds(ISoliniaLivingEntity target, int hand, boolean isFromSpell) 
	{
		if (target == null)
				return;
		
		Attack(target, hand, false, false, isFromSpell);
		
		boolean candouble = canThisClassDoubleAttack() && canDoubleAttack();
		// extra off hand non-sense, can only double with skill of 150 or above
		// or you have any amount of GiveDoubleAttack
		if (candouble && hand == InventorySlot.Secondary)
			candouble =
			    getSkill(SkillType.DoubleAttack) > 149 ||
			    (getAABonuses(SpellEffectType.GiveDoubleAttack) + getSpellBonuses(SpellEffectType.GiveDoubleAttack) + getItemBonuses(SpellEffectType.GiveDoubleAttack)) > 0;

		if (candouble) {
			tryIncreaseSkill(SkillType.DoubleAttack, 1);
			if (checkDoubleAttack()) {
				ChatUtils.SendHint(this.getBukkitLivingEntity(), HINT.DOUBLEATTACK, "", false);
				Attack(target, hand, false, false, isFromSpell);

				// Modern AA description: Increases your chance of ... performing one additional hit with a 2-handed weapon when double attacking by 2%.
				if (hand == InventorySlot.Primary) {
					int extraattackchance = getAABonuses(SpellEffectType.ExtraAttackChance) + getSpellBonuses(SpellEffectType.ExtraAttackChance) + getItemBonuses(SpellEffectType.ExtraAttackChance);
					if (extraattackchance > 0 && hasTwoHanderEquipped() && MathUtils.Roll(extraattackchance))
					{
						ChatUtils.SendHint(this.getBukkitLivingEntity(), HINT.DOUBLEATTACK, "", false);
						Attack(target, hand, false, false, isFromSpell);
					}
				}
				
				if (hand == InventorySlot.Primary && canThisClassTripleAttack()) {
					tryIncreaseSkill(SkillType.TripleAttack, 1);
					if (checkTripleAttack()) {
						Attack(target, hand, false, false, isFromSpell);
						int flurrychance = getAABonuses(SpellEffectType.Flurry) + getSpellBonuses(SpellEffectType.Flurry) +
							    getItemBonuses(SpellEffectType.Flurry);
						
						if (flurrychance > 0 && MathUtils.Roll(flurrychance)) {
							Attack(target, hand, false, false, isFromSpell);
							if (MathUtils.Roll(flurrychance))
								Attack(target, hand, false, false, isFromSpell);
							
							ChatUtils.SendHint(this.getBukkitLivingEntity(), HINT.YOU_FLURRY, "", false);
						}
					}
				}
			}
		}
	}
	
	@Override
	public boolean canDoubleAttack() {
		if (getClassObj() == null)
			return false;

		if (getClassObj().canDoubleAttack() == false)
			return false;

		if (getClassObj().getDoubleattacklevel() > getMentorLevel())
			return false;

		return true;
	}
	
	@Override
	public boolean canTripleAttack() {
		if (getClassObj() == null)
			return false;

		if (getClassObj().canTripleAttack() == false)
			return false;

		if (getClassObj().getTripleattacklevel() > getMentorLevel())
			return false;

		return true;
	}

	private boolean hasTwoHanderEquipped() {
		return holdingTwoHander();
	}

	private boolean canThisClassDoubleAttack() {
		return canDoubleAttack();
	}
	private boolean canThisClassTripleAttack() {
		return canTripleAttack();
	}

	private boolean canThisClassDualWield() {
		if (this.getClassObj() != null)
			return this.getClassObj().canDualWield();
		return false;
	}
	
	@Override
	public boolean Attack(ISoliniaLivingEntity other, int Hand, boolean bRiposte, boolean IsStrikethrough, boolean IsFromSpell) {
		try
		{

			if (other == null)
			{
				setAttackTarget(null);
				return false;
			}
			
			if (this.getAttackTarget() == null)
				this.setAttackTarget(other.getBukkitLivingEntity());
			
			if (isCasting() && getClassObj() != null && !getClassObj().getName().equals("BARD") && !IsFromSpell)
			{
				return false; // Only bards can attack while casting
			}

			/*if (other == null)
			{
				return false; // Only bards can attack while casting
			}*/

			if ((isPlayer() && this.getBukkitLivingEntity() != null && this.getBukkitLivingEntity().isDead()) || (other.isPlayer() && other.getBukkitLivingEntity() != null && other.getBukkitLivingEntity().isDead()))
			{
				return false; // Only bards can attack while casting
			}

			if(this.getBukkitLivingEntity() != null && this.getBukkitLivingEntity().getHealth() < 0)
			{
				return false; // Only bards can attack while casting
			}

			if(!isAttackAllowed(other,IsFromSpell))
			{
				return false; // Only bards can attack while casting
			}
			
			if (divineAura() && !getGM()) {//cant attack while invulnerable unless your a gm
				return false;
			}
			
			if (other.getBukkitLivingEntity().isDead() || this.getBukkitLivingEntity().isDead()
					|| this.getBukkitLivingEntity().getHealth() < 0) {
				return false;
			}
	
			if (isInulvnerable()) {
				return false;
			}
			
			if (other.isInvulnerable()) {
				if (isPlayer())
					getBukkitLivingEntity()
							.sendMessage("* Your attack was prevented as the target is Invulnerable!");
	
				if (other.isPlayer())
					other.getBukkitLivingEntity()
							.sendMessage("* Your invulnerability prevented the targets attack!");
	
				return false;
			}
	
			ItemStack weaponItemStack = null;
			ISoliniaItem weapon = null;
			if (isFeigned()) {
				return false;
			}
	
			if (Hand == InventorySlot.Secondary) { // Kaiyodo - Pick weapon from the attacking hand
				weaponItemStack = this.getEquipment().getItemInOffHand();
				try
				{
					weapon = SoliniaItemAdapter.Adapt(weaponItemStack);
				} catch (SoliniaItemException e)
				{
					
				}
				//todo OffHandAtk(true);
			} else {
				weaponItemStack = this.getEquipment().getItemInMainHand();
				try
				{
					weapon = SoliniaItemAdapter.Adapt(weaponItemStack);
				} catch (SoliniaItemException e)
				{
					
				}
				//todo OffHandAtk(false);
			}
	
			if (weapon != null) {
				if (!weapon.isWeapon())
					return false;
			}
	
			// Since there was two pieces of logc in eq, lets do the same
			if (this.isPlayer())
				return CalcPlayerAttack(Hand, weapon, other, weaponItemStack,bRiposte);
			
			return CalcNPCAttack(Hand, weapon, other, weaponItemStack, bRiposte);
		} catch (CoreStateInitException e)
		{
			return false;
		}
	}
	
	private boolean CalcNPCAttack(int Hand, ISoliniaItem weapon, ISoliniaLivingEntity other,
			ItemStack weaponItemStack, boolean bRiposte) {
		// This is really just the rest of the Attack() method, it should be moved into its relevant class really

		DamageHitInfo my_hit = new DamageHitInfo();
		my_hit.skill = AttackAnimation(Hand, weapon, SkillType.HandtoHand);
		
		// Now figure out damage
		my_hit.damage_done = 1;
		my_hit.min_damage = 0;
		// getLevel();
		
		/*if (weapon != null)
			hate = (weapon.getDefinedItemDamage() + weapon.getElementalDamageAmount());*/

		int weapon_damage = getWeaponDamage(other, weaponItemStack, 0);

		if (weapon_damage > 0) {
			// bane damage for body type
			// bane damage for race
			// elemental damage
			int eleBane = 0;
			
			int otherlevel = other.getMentorLevel();
			int mylevel = this.getMentorLevel();

			if (otherlevel < 1)
				otherlevel = 1;
			if (mylevel < 1)
				otherlevel = 1;

			//damage = mod_npc_damage(damage, skillinuse, Hand, weapon, other);

			my_hit.base_damage = this.getNPC().getBaseDamage() + eleBane;
			my_hit.min_damage = this.getNPC().getMinDamage();
			int hate = my_hit.base_damage + my_hit.min_damage;
			
			int hit_chance_bonus = 0;

			/*if (opts) {
				my_hit.base_damage *= opts->damage_percent;
				my_hit.base_damage += opts->damage_flat;
				hate *= opts->hate_percent;
				hate += opts->hate_flat;
				hit_chance_bonus += opts->hit_chance;
			}*/
			
			my_hit.offense = offense(my_hit.skill);
			my_hit.tohit = getTotalToHit(my_hit.skill, hit_chance_bonus);

			doAttack(other, my_hit);
			
			other.addToHateList(this.getBukkitLivingEntity().getUniqueId(), hate, false);
			
			if (other.isPlayer() && IsPet() && other.getOwnerEntity() instanceof Player) {
				//pets do half damage to clients in pvp
				my_hit.damage_done /= 2;
				if (my_hit.damage_done < 1)
					my_hit.damage_done = 1;
			}
			
		} else {
			my_hit.damage_done = Utils.DMG_INVULNERABLE;
		}
		
		// TODO Skill Procs

		if (getBukkitLivingEntity().isDead()) {
			return false;
		}
		
		// Sets last melee attack so we can check if a user has melee attacked previously
		setLastMeleeAttack();
		if (other.getBukkitLivingEntity() != null || !other.getBukkitLivingEntity().isDead())
		{
			other.Damage(this, my_hit.damage_done, Utils.SPELL_UNKNOWN, my_hit.skill, true, -1, false); // Not avoidable client already had thier chance to Avoid
		}
		else
		{
			return false;
		}
		
		if (other.getBukkitLivingEntity() != null || !other.getBukkitLivingEntity().isDead()) //killed by damage shield ect
			return false;
		
		// melee life tap goes here
		
		commonBreakInvisibleFromCombat();
		
		if (other.getBukkitLivingEntity() == null || other.getBukkitLivingEntity().isDead())
			return true; //We killed them
		
		// TODO riposte here
		if (!bRiposte && !other.getBukkitLivingEntity().isDead()) {
			tryWeaponProc(null, weapon, other, Hand);	//no weapon

			/* TODO skill procs and spell procs
			 * if (!other.getBukkitLivingEntity().isDead())
				TrySpellProc(null, weapon, other, Hand);

			if (my_hit.damage_done > 0 && HasSkillProcSuccess() && !other->HasDied())
				TrySkillProc(other, my_hit.skill, 0, true, Hand);
				*/
		}
		
		if (!other.getBukkitLivingEntity().isDead())
		{
			triggerDefensiveProcs(other, Hand, true, my_hit.damage_done);
		}
		
		if (my_hit.damage_done > 0)
			return true;

		else
			return false;
	}

	private boolean CalcPlayerAttack(int Hand, ISoliniaItem weapon, ISoliniaLivingEntity other, ItemStack weaponItemStack, boolean bRiposte)
	{
		// This is really just the rest of the Attack() method, it should be moved into its relevant class really
		
		DamageHitInfo my_hit = new DamageHitInfo();
		my_hit.skill = AttackAnimation(Hand, weapon, SkillType.HandtoHand);
		
		// Now figure out damage
		my_hit.damage_done = 1;
		my_hit.min_damage = 0;
		// getLevel();
		int hate = 0;
		if (weapon != null)
			hate = (weapon.getDefinedItemDamage() + weapon.getElementalDamageAmount());

		my_hit.base_damage = getWeaponDamage(other, weaponItemStack, hate);

		// amount of hate is based on the damage done
		if (hate == 0 && my_hit.base_damage > 1) {
			hate = my_hit.base_damage;
		}

		if (my_hit.base_damage > 0) {
			if (Hand == InventorySlot.Primary || Hand == InventorySlot.Secondary)
				my_hit.base_damage = getDamageCaps(my_hit.base_damage);

			int shield_inc = this.getSpellBonuses(SpellEffectType.ShieldEquipDmgMod) + this.getItemBonuses(SpellEffectType.ShieldEquipDmgMod) + getAABonuses(SpellEffectType.ShieldEquipDmgMod);
			if (shield_inc > 0 && hasShieldEquiped() && Hand == InventorySlot.Primary) {
				my_hit.base_damage = my_hit.base_damage * (100 + shield_inc) / 100;
				hate = hate * (100 + shield_inc) / 100;
			}
			
			tryIncreaseSkill(my_hit.skill, 1);
			tryIncreaseSkill(SkillType.Offense, 1);

			int ucDamageBonus = 0;

			if (Hand == InventorySlot.Primary && getClassObj() != null && getClassObj().isWarriorClass() && getMentorLevel() >= 28) {
				ucDamageBonus = getWeaponDamageBonus(weaponItemStack);
				my_hit.min_damage = ucDamageBonus;
				hate += ucDamageBonus;
			}

			// TODO Sinister Strikes

			int hit_chance_bonus = 0;
			my_hit.offense = offense(my_hit.skill); // we need this a few times
			my_hit.hand = Hand;

			my_hit.tohit = getTotalToHit(my_hit.skill, hit_chance_bonus);
			doAttack(other, my_hit);
		} else {
			my_hit.damage_done = Utils.DMG_INVULNERABLE;
		}
		
		other.addToHateList(this.getBukkitLivingEntity().getUniqueId(), hate, true);
		
		///////////////////////////////////////////////////////////
		////// Send Attack Damage
		///////////////////////////////////////////////////////////
		
		// TODO Skill Procs

		if (getBukkitLivingEntity().isDead()) {
			return false;
		}
		
		// Sets last melee attack so we can check if a user has melee attacked previously
		setLastMeleeAttack();
		
		other.Damage(this, my_hit.damage_done, Utils.SPELL_UNKNOWN, my_hit.skill, true, -1, false);
		
		commonBreakInvisibleFromCombat();
		if (!other.getBukkitLivingEntity().isDead())
		{
			triggerDefensiveProcs(other, Hand, true, my_hit.damage_done);
		}
		
		if (my_hit.damage_done > 0)
			return true;

		else
			return false;
	}
	
	private void triggerDefensiveProcs(ISoliniaLivingEntity on, int hand, boolean fromSkillProc, int damage) {
		if (on == null || on.getBukkitLivingEntity() == null)
			return;

		if (!fromSkillProc)
			on.tryDefensiveProc(this, hand);

		//Defensive Skill Procs
		/*
		if (damage < 0 && damage >= -4) {
			SkillType skillinuse = SkillType.None;
			switch (damage) {
				case (-1):
					skillinuse = SkillType.Block;
				break;

				case (-2):
					skillinuse = SkillType.Parry;
				break;

				case (-3):
					skillinuse = SkillType.Riposte;
				break;

				case (-4):
					skillinuse = SkillType.Dodge;
				break;
			}

			if (on.hasSkillProcs())
				on.trySkillProc(this, skillinuse, 0, false, hand, true);

			if (on.hasSkillProcSuccess())
				on.trySkillProc(this, skillinuse, 0, true, hand, true);
		}
		*/
	}

	@Override
	public void Damage(ISoliniaLivingEntity other, int damage, int spell_id, SkillType attack_skill, boolean avoidable, int buffslot, boolean iBuffTic)
	{
		if (this.getBukkitLivingEntity() != null && this.getBukkitLivingEntity().isDead())
			return;
		if (other.getBukkitLivingEntity() != null && other.getBukkitLivingEntity().isDead())
			return;
		if (this.isInvulnerable())
			return;
		if (other.isInvulnerable())
			return;
		
		if (spell_id == 0)
			spell_id = Utils.SPELL_UNKNOWN;

		// cut all PVP spell damage to 2/3
		// Blasting ourselfs is considered PvP
		//Don't do PvP mitigation if the caster is damaging himself
		//should this be applied to all damage? comments sound like some is for spell DMG
		//patch notes on PVP reductions only mention archery/throwing ... not normal dmg
		if (other != null && other.isPlayer() && (!other.getBukkitLivingEntity().getUniqueId().equals(this.getBukkitLivingEntity().getUniqueId())) && damage > 0) {
			int PvPMitigation = 100;
			if (attack_skill.equals(SkillType.Archery) || attack_skill.equals(SkillType.Throwing))
				PvPMitigation = 80;
			else
				PvPMitigation = 67;
			damage = Math.max((damage * PvPMitigation) / 100, 1);
		}

		/*if (!ClientFinishedLoading())
			damage = -5;*/

		//do a majority of the work...
		CommonDamage(other, damage, spell_id, attack_skill, avoidable, buffslot, iBuffTic);

		if (damage > 0) {
			if (spell_id == Utils.SPELL_UNKNOWN)
				this.tryIncreaseSkill(SkillType.Defense, 1);
		}
	}
	
	@Override
	public ISoliniaLivingEntity getPet()
	{
		if (this.getBukkitLivingEntity() == null)
			return null;
		
		try
		{
			LivingEntity pet = StateManager.getInstance().getEntityManager().getPet(getBukkitLivingEntity().getUniqueId());
			if (pet == null)
				return null;
			
			ISoliniaLivingEntity solLivingEntity = SoliniaLivingEntityAdapter.Adapt(pet);
			return solLivingEntity;
		} catch (CoreStateInitException e)
		{
			return null;
		}
	}
	

	private void CommonDamage(ISoliniaLivingEntity attacker, int damage, int spell_id, SkillType skillType,
			boolean avoidable, int buffslot, boolean iBuffTic) {
		// This method is called with skill_used=ABJURE for Damage Shield damage.
		if (this.getBukkitLivingEntity() == null || attacker.getBukkitLivingEntity() == null)
			return;
		
		boolean FromDamageShield = (skillType.equals(SkillType.Abjuration));
		boolean ignore_invul = false;
		if (IsValidSpell(spell_id))
			ignore_invul = spell_id == 982 /* TODO || spells[spell_id].cast_not_standing*/; // cazic touch

		if (!ignore_invul && (this.isInvulnerable() || DivineAura())) {
			damage = Utils.DMG_INVULNERABLE;
		}
		
		// this should actually happen MUCH sooner, need to investigate though -- good enough for now
		if ((skillType.equals(SkillType.Archery) || skillType.equals(SkillType.Throwing)) && getSpecialAbility(SpecialAbility.IMMUNE_RANGED_ATTACKS) > 0) {
			damage = Utils.DMG_INVULNERABLE;
		}

		if (spell_id != Utils.SPELL_UNKNOWN || attacker == null)
			avoidable = false;
		
		// only apply DS if physical damage (no spell damage)
		// damage shield calls this function with spell_id set, so its unavoidable
		if (attacker != null && attacker.getBukkitLivingEntity() != null && damage > 0 && spell_id == Utils.SPELL_UNKNOWN && !skillType.equals(SkillType.Archery) && !skillType.equals(SkillType.Throwing)) {
			damageShield(attacker, false);
		}
		
		if (spell_id == Utils.SPELL_UNKNOWN && !skillType.equals(SkillType.None)) {
			checkNumHitsRemaining(NumHit.IncomingHitAttempts);

			if (attacker != null)
				attacker.checkNumHitsRemaining(NumHit.OutgoingHitAttempts);
		}
		
		if (attacker.getBukkitLivingEntity() != null) {
			if (attacker.isPlayer()) {
				if (!attacker.isFeignedDeath())
					addToHateList(attacker.getBukkitLivingEntity().getUniqueId(), damage, true);
			}
			else
				addToHateList(attacker.getBukkitLivingEntity().getUniqueId(), damage, true);
		}
		
		if (damage > 0) {
			//if there is some damage being done and theres an attacker involved
			if (attacker.getBukkitLivingEntity() != null) {
				// if spell is lifetap add hp to the caster
				if (spell_id != Utils.SPELL_UNKNOWN) {
					try
					{
						ISoliniaSpell spell = StateManager.getInstance().getConfigurationManager().getSpell(spell_id);
						if (spell != null && spell.isLifetapSpell())
						 {
							int healed = damage;
							healed = attacker.getActSpellHealing(spell, healed);
							attacker.healDamage(healed, null, Utils.SPELL_UNKNOWN);
		
							//we used to do a message to the client, but its gone now.
							// emote goes with every one ... even npcs
							this.filteredMessageClose(attacker.getBukkitLivingEntity(),attacker.getName() + " beams a smile at " + this.getName(), false);
						 }
					} catch (CoreStateInitException e)
					{
						
					}
				}
			}	//end `if there is some damage being done and theres anattacker person involved`

			ISoliniaLivingEntity pet = this.getPet();
			// pets that have GHold will never automatically add NPCs
			// pets that have Hold and no Focus will add NPCs if they're engaged
			// pets that have Hold and Focus will not add NPCs
			if (pet != null /* TODO && !pet.isFamiliar() */ && !(pet.getSpecialAbility(SpecialAbility.IMMUNE_AGGRO) > 0) && !pet.isEngaged() && attacker.getBukkitLivingEntity() != null && this.getBukkitLivingEntity() != null && !attacker.getBukkitLivingEntity().getUniqueId().equals(this.getBukkitLivingEntity().getUniqueId()) && !attacker.getBukkitLivingEntity().isDead() /* TODO && !pet.IsGHeld() && !attacker.IsTrap()*/)
			{
				//if (!pet->IsHeld()) {
					//Log(Logs::Detail, Logs::Aggro, "Sending pet %s into battle due to attack.", pet->GetName());
					pet.addToHateList(attacker.getBukkitLivingEntity().getUniqueId(), 1,true);
					pet.setAttackTarget(attacker.getBukkitLivingEntity());
					//ChatUtils.SendHint((LivingEntity)pet.getOwnerEntity(),HINT.PET_ATTACKINGTGT,attacker.getName(),false);
					//Message_StringID(10, PET_ATTACKING, pet->GetCleanName(), attacker->GetCleanName());
				//}
			}

			//see if any runes want to reduce this damage
			if (spell_id == Utils.SPELL_UNKNOWN) {
				damage = reduceDamage(damage);
				//Log(Logs::Detail, Logs::Combat, "Melee Damage reduced to %d", damage);
				//damage = reduceAllDamage(damage);
				//tryTriggerThreshHold(damage, SpellEffectType.TriggerMeleeThreshold, attacker);

				if (!skillType.equals(SkillType.None))
					checkNumHitsRemaining(NumHit.IncomingHitSuccess);
			}
			// TODO OTHER RUNE STUFF
			else {
				int origdmg = damage;
				damage = affectMagicalDamage(damage, spell_id, iBuffTic, attacker);
				/*
				damage = affectMagicalDamage(damage, spell_id, iBuffTic, attacker);
				if (origdmg != damage && attacker != null && attacker.isPlayer()) {
					//if (attacker->CastToClient()->GetFilter(FilterDamageShields) != FilterHide)
						this.getBukkitLivingEntity().sendMessage("The Spellshield absorbed " + (origdmg - damage) + " of " + origdmg +" points of damage");
				}
				if (damage == 0 && attacker != null && origdmg != damage && isPlayer()) {
					this.getBukkitLivingEntity().sendMessage(attacker.getName() + " tries to cast on YOU, but YOUR magical skin absorbs the spell");
				}
				damage = reduceAllDamage(damage);
				tryTriggerThreshHold(damage, SpellEffectType.TriggerSpellThreshold, attacker);
				*/
			}

			if (isPlayer() && isSneaking()) {
				((Player)this.getBukkitLivingEntity()).setSneaking(false);
			}
			if (attacker != null && attacker.isPlayer() && attacker.isSneaking()) {
				((Player)attacker.getBukkitLivingEntity()).setSneaking(false);
			}

			//final damage has been determined.

			double newHp = setHPChange(damage*-1, attacker.getBukkitLivingEntity());
			if (newHp <= 0D)
				attacker.removeAggro(this.getBukkitLivingEntity().getUniqueId());

			/* TODO DEATH SAVE
			if (this.getBukkitLivingEntity().isDead()) {
				boolean IsSaved = false;

				if (TryDivineSave())
					IsSaved = true;

				if (!IsSaved && !TrySpellOnDeath()) {
					SetHP(-500);

					if (Death(attacker, damage, spell_id, skill_used)) {
						return;
					}
				}
			}
			else {
				if (this.getHPRatio() < 16)
					tryDeathSave();
			}
			*/

			// TODO Triggers on value amounts
			//TryTriggerOnValueAmount(true);

			//fade mez if we are mezzed
			if (isMezzed() && attacker.getBukkitLivingEntity() != null) {
				this.filteredMessageClose(this.getBukkitLivingEntity(),this.getName() + " has been awaked by " + attacker.getName(), false);
				buffFadeByEffect(SpellEffectType.Mez);
			}

			// broken up for readability
			// This is based on what the client is doing
			// We had a bunch of stuff like BaseImmunityLevel checks, which I think is suppose to just be for spells
			// This is missing some merc checks, but those mostly just skipped the spell bonuses I think ...
			// TODO STUNS
			boolean can_stun = false;
			int stunbash_chance = 0; // bonus
			if (attacker != null) {
				if (skillType.equals(SkillType.Bash)) {
					can_stun = true;
					if (attacker.isPlayer())
						stunbash_chance = attacker.getSpellBonuses(SpellEffectType.StunBashChance) +
								attacker.getItemBonuses(SpellEffectType.StunBashChance) +
								attacker.getAABonuses(SpellEffectType.StunBashChance);
				}
				else if (skillType.equals(SkillType.Kick) &&
					(attacker.getMentorLevel() > 55 || attacker.isNPC()) && this.getClassObj() != null && this.getClassObj().getName().toUpperCase().equals("WARRIOR")) {
					can_stun = true;
				}

				/*if ((GetBaseRace() == OGRE || GetBaseRace() == OGGOK_CITIZEN) &&
					!attacker->BehindMob(this, attacker->GetX(), attacker->GetY()))
					can_stun = false;*/
				if (getSpecialAbility(SpecialAbility.UNSTUNABLE) > 0)
					can_stun = false;
				
			}
			if (can_stun) {
				int bashsave_roll = MathUtils.RandomBetween(0, 100);
				if (bashsave_roll > 98 || bashsave_roll > (55 - stunbash_chance)) {
					// did stun -- roll other resists
					// SE_FrontalStunResist description says any angle now a days
					int stun_resist2 = getSpellBonuses(SpellEffectType.FrontalStunResist) + getItemBonuses(SpellEffectType.FrontalStunResist) +
							getAABonuses(SpellEffectType.FrontalStunResist);
					if (MathUtils.RandomBetween(1, 100) > stun_resist2) {
						// stun resist 2 failed
						// time to check SE_StunResist and mod2 stun resist
						int stun_resist =
								getSpellBonuses(SpellEffectType.StunResist) + getSpellBonuses(SpellEffectType.StunResist) + getSpellBonuses(SpellEffectType.StunResist);
						if (MathUtils.RandomBetween(0, 100) >= stun_resist) {
							// did stun
							// nothing else to check!
							Stun(2); // straight 2 seconds every time
						}
						else {
							// stun resist passed!
							if (isPlayer())
								this.getBukkitLivingEntity().sendMessage("You shake off the stun");
								//Message_StringID(MT_Stun, SHAKE_OFF_STUN);
						}
					}
					else {
						// stun resist 2 passed!
						if (isPlayer())
							this.getBukkitLivingEntity().sendMessage("You avoid the stunning blow");
							//Message_StringID(MT_Stun, AVOID_STUNNING_BLOW);
					}
				}
				else {
					// main stun failed -- extra interrupt roll
					if (isCasting() &&
						!MathUtils.ValueWithin(spell_id, 859, 1023)) // these spells are excluded
																		  // 90% chance >< -- stun immune won't reach this branch though :(
						if (MathUtils.RandomBetween(0, 9) > 1)
							InterruptSpell();
				}
			}
			

			if (spell_id != Utils.SPELL_UNKNOWN && !iBuffTic) {
				//see if root will break
				if (isRooted() && !FromDamageShield)  // neotoyko: only spells cancel root
					TryRootFadeByDamage(buffslot, attacker);
			}
			else if (spell_id == Utils.SPELL_UNKNOWN)
			{
				//TODO
				//increment chances of interrupting
				/*if (isCasting()) { //shouldnt interrupt on regular spell damage
					attacked_count++;
					//Log(Logs::Detail, Logs::Combat, "Melee attack while casting. Attack count %d", attacked_count);
				}*/
			}

			/* TODO - Senmd HP UPdate
			 * ACtually this is already done in changehp
			//send an HP update if we are hurt
			if (getHP() < getMaxHP())
				SendHPUpdate(!iBuffTic); // the OP_Damage actually updates the client in these cases, so we skip the HP update for them
				*/
			
			if (!iBuffTic) { //buff ticks do not send damage, instead they just call SendHPUpdate(), which is done above
				//Note: if players can become pets, they will not receive damage messages of their own
				//this was done to simplify the code here (since we can only effectively skip one mob on queue)
				
				ChatUtils.SendHint(getBukkitLivingEntity(), HINT.HITFORDMGBY,getBukkitLivingEntity().getCustomName()+","+damage+","+skillType.name().toUpperCase()+","+attacker.getName(),true);
				
				ISoliniaLivingEntity skip = attacker;
				if (attacker != null && attacker.getOwnerEntity() != null) {
					//attacker is a pet, let pet owners see their pet's damage
					ISoliniaLivingEntity owner = attacker.getOwnerSoliniaLivingEntity();
					if (owner != null && owner.isPlayer()) {
						if (((spell_id != Utils.SPELL_UNKNOWN) || (FromDamageShield)) && damage>0) {
							//special crap for spell damage, looks hackish to me
							if (owner.getBukkitLivingEntity() instanceof Player)
							((Player)owner.getBukkitLivingEntity()).spigot().sendMessage(ChatMessageType.ACTION_BAR,new TextComponent(this.getName() + " was hit by non-melee for "+ damage + " points of damage."));
						}
						else {
							// ?
						}
					}
					skip = owner;
				}
				else {
					//attacker is not a pet, send to the attacker

					//if the attacker is a client, try them with the correct filter
					if (attacker != null && attacker.isPlayer()) {
						if ((spell_id != Utils.SPELL_UNKNOWN || FromDamageShield) && damage > 0) {
							if (FromDamageShield) {
								if (attacker.getBukkitLivingEntity() instanceof Player)
									((Player)attacker.getBukkitLivingEntity()).spigot().sendMessage(ChatMessageType.ACTION_BAR,new TextComponent(this.getName() + " was hit by non-melee for "+ damage + " points of damage."));
							}
							else {
								this.filteredMessageClose(this.getBukkitLivingEntity(),attacker.getName() + " hit "+getName()+" for "+damage+" points of non-melee damage.", true);
							}
						}
						else {
							// ?
						}
					}
					skip = attacker;
				}
			}
			else {
				//else, it is a buff tic...
				// So we can see our dot dmg like live shows it.
				if (spell_id != Utils.SPELL_UNKNOWN && damage > 0 && attacker != null && !(attacker.getBukkitLivingEntity().getUniqueId().equals(this.getBukkitLivingEntity().getUniqueId()) && attacker.isPlayer())) {
					//might filter on (attack_skill>200 && attack_skill<250), but I dont think we need it
					attacker.sendMessage(getName() + " has taken " + damage +" damage from your DOT.");

					this.filteredMessageClose(this.getBukkitLivingEntity(),getName() +" has taken "+damage+" damage from DOT by "+attacker.getName()+".", true);
				}
			} //end packet sending
		}
	}

	private boolean TryRootFadeByDamage(int buffslot, ISoliniaLivingEntity attacker) {
		SoliniaActiveSpell activeSpell = this.getFirstActiveSpellWithSpellEffectType(SpellEffectType.Root);
		
		if (attacker == null || activeSpell == null)
			return false;

		if (activeSpell.getSpell().isDetrimental()) {
			int BreakChance = 55;//RuleI(Spells, RootBreakFromSpells);

			BreakChance -= BreakChance*activeSpell.getRootBreakChance() / 100;
			int level_diff = attacker.getMentorLevel() - getMentorLevel();

			//Use baseline if level difference <= 1 (ie. If target is (1) level less than you, or equal or greater level)

			if (level_diff == 2)
				BreakChance = (BreakChance * 80) / 100; //Decrease by 20%;

			else if (level_diff >= 3 && level_diff <= 20)
				BreakChance = (BreakChance * 60) / 100; //Decrease by 40%;

			else if (level_diff > 21)
				BreakChance = (BreakChance * 20) / 100; //Decrease by 80%;

			if (BreakChance < 1)
				BreakChance = 1;

			if (MathUtils.Roll(BreakChance)) {

				activeSpell.tryFadeEffect();
				return true;
			}
		}

		//Log(Logs::Detail, Logs::Combat, "Spell did not break root. BreakChance percent chance");
		return false;
	}

	@Override
	public void Stun(int seconds) {
		try
		{
			if (getBukkitLivingEntity().isDead())
				return;
			
			EntityUtils.dismountEntity(getBukkitLivingEntity());
	
			if (!(getBukkitLivingEntity() instanceof LivingEntity))
				return;
	
			try {
				LocalDateTime datetime = LocalDateTime.now();
				Timestamp expiretimestamp = Timestamp.valueOf(datetime.plus(seconds, ChronoUnit.MILLIS));
	
				StateManager.getInstance().getEntityManager().addStunned(getBukkitLivingEntity(), expiretimestamp);
	
				InterruptSpell();
				
				if (getBukkitLivingEntity() instanceof Creature) {
					setAttackTarget(null);
				}
	
				EntityUtils.dismountEntity(getBukkitLivingEntity());
	
				Entity vehicle = getBukkitLivingEntity().getVehicle();
				if (vehicle != null) {
					vehicle.eject();
				}
	
				SpellUtils.AddPotionEffect(getBukkitLivingEntity(), PotionEffectType.CONFUSION, 1);
			} catch (CoreStateInitException e) {
				return;
			}
		} catch (Exception e)
		{
			return;
		}
	}

	@Override
	public void InterruptSpell() {
		// Interrupt casting
					try {
						StopSinging();
						
						CastingSpell castingSpell = StateManager.getInstance().getEntityManager().getCasting(getBukkitLivingEntity());
						if (castingSpell != null) {
							if (castingSpell.getSpell() != null && castingSpell.getSpell().getUninterruptable() == 0) {
								StateManager.getInstance().getEntityManager().interruptCasting(getBukkitLivingEntity());
							}
						}
					} catch (CoreStateInitException e) {

					}
	}

	private int affectMagicalDamage(int damage, int spell_id, boolean iBuffTic, ISoliniaLivingEntity attacker) {
		if (damage <= 0)
			return damage;

		boolean DisableSpellRune = false;
		int slot = -1;
		
		// TODO NegateAttacks
		
		// TODO Dot Shielding
		if (iBuffTic) {
			
		} else {
			// TODO SPELL SHIELDING
			
			// TODO MITIGATE SPELL RUNE
			// Do runes now.
			
			if (damage < 1)
				return 0;

			/*//Regular runes absorb spell damage (except dots) - Confirmed on live.
			if (spellbonuses.MeleeRune[0] && spellbonuses.MeleeRune[1] >= 0)
				damage = RuneAbsorb(damage, SE_Rune);

			if (spellbonuses.AbsorbMagicAtt[0] && spellbonuses.AbsorbMagicAtt[1] >= 0)
				damage = RuneAbsorb(damage, SE_AbsorbMagicAtt);
			*/
			if (this.getSpellBonuses(SpellEffectType.Rune) > 0)
				damage = runeAbsorb(damage, SpellEffectType.Rune);

			if (damage < 1)
				return 0;
		}
		
		return damage;
	}

	private int reduceDamage(int damage) {
		if (damage <= 0)
			return damage;

		int slot = -1;
		boolean DisableMeleeRune = false;

		// TODO MELEE THRESHOLD
		
		// TODO MITIGATE MELLEE DAMAGE
		
		if (damage < 1)
			return Utils.DMG_RUNE;

		if (this.getSpellBonuses(SpellEffectType.Rune) > 0/* && spellbonuses.MeleeRune[1] >= 0*/)
		{
			int original = damage;
			damage = runeAbsorb(damage, SpellEffectType.Rune);
			ChatUtils.SendHint(this.getBukkitLivingEntity(), HINT.RUNE_ABSORBED, Integer.toString(original - damage), false);
		}

		if (damage < 1)
			return Utils.DMG_RUNE;

		return(damage);
	}
	

	private int runeAbsorb(int damage, SpellEffectType spellEffectType) {
		// TODO Auto-generated method stub
		return reduceAndRemoveRunesAndReturnLeftover(damage);
	}

	// this is called from Damage() when 'this' is attacked by 'other.
	// 'this' is the one being attacked
	// 'other' is the attacker
	// a damage shield causes damage (or healing) to whoever attacks the wearer
	// a reverse ds causes damage to the wearer whenever it attack someone
	// given this, a reverse ds must be checked each time the wearer is attacking
	// and not when they're attacked
	//a damage shield on a spell is a negative value but on an item it's a positive value so add the spell value and subtract the item value to get the end ds value
	private void damageShield(ISoliniaLivingEntity attacker, boolean spell_ds) {
		if (attacker == null || attacker.getBukkitLivingEntity() == null || this.getBukkitLivingEntity().getUniqueId().equals(attacker.getBukkitLivingEntity().getUniqueId()))
			return;

		int DS = 0;
		int rev_ds = 0;
		int spellid = 0;

		if (!spell_ds)
		{
			DS = getSpellBonuses(SpellEffectType.DamageShield);
			rev_ds = attacker.getSpellBonuses(SpellEffectType.ReverseDS);

			SoliniaActiveSpell spell = getFirstActiveSpellWithSpellEffectType(SpellEffectType.DamageShield);
			if (spell != null && spell.getSpell() != null && spell.getSpellId() != Utils.SPELL_UNKNOWN)
				spellid = spell.getSpellId();
		}
		else {
			DS = getSpellBonuses(SpellEffectType.DamageShield);
			rev_ds = 0;
			// This ID returns "you are burned", seemed most appropriate for spell DS
			spellid = 2166;
		}

		if (DS == 0 && rev_ds == 0)
			return;

		//Log(Logs::Detail, Logs::Combat, "Applying Damage Shield of value %d to %s", DS, attacker->GetName());

		//invert DS... spells yield negative values for a true damage shield
		if (DS < 0) {
//			if (!spell_ds) {
//
//				// TODO AA DS += aabonuses.DamageShield; //Live AA - coat of thistles. (negative value)
//				DS -= getItemBonuses(SpellEffectType.DamageShield); //+Damage Shield should only work when you already have a DS spell
//
//												//Spell data for damage shield mitigation shows a negative value for spells for clients and positive
//												//value for spells that effect pets. Unclear as to why. For now will convert all positive to be consistent.
//				if (attacker->IsOffHandAtk()) {
//					int mitigation = attacker.getItemBonuses(SpellEffectType.DSMitigationOffHand) +
//						attacker.getSpellBonuses(SpellEffectType.DSMitigationOffHand) /*+
//						attacker->aabonuses.DSMitigationOffHand*/;
//					DS -= DS*mitigation / 100;
//				}
//				DS -= DS * attacker.getItemBonuses(SpellEffectType.DSMitigation) / 100;
//			}
			attacker.Damage(this, -DS, spellid, SkillType.Abjuration, false, -1, false);
			//we can assume there is a spell now
			// TOODO Send DS Message
			/*auto outapp = new EQApplicationPacket(OP_Damage, sizeof(CombatDamage_Struct));
			CombatDamage_Struct* cds = (CombatDamage_Struct*)outapp->pBuffer;
			cds->target = attacker->GetID();
			cds->source = GetID();
			cds->type = spellbonuses.DamageShieldType;
			cds->spellid = 0x0;
			cds->damage = DS;
			entity_list.QueueCloseClients(this, outapp);
			safe_delete(outapp);*/
		}
		else if (DS > 0 && !spell_ds) {
			//we are healing the attacker...
			attacker.healDamage(DS,null,Utils.SPELL_UNKNOWN);
			//TODO: send a packet???
		}

		//Reverse DS
		//this is basically a DS, but the spell is on the attacker, not the attackee
		//if we've gotten to this point, we know we know "attacker" hit "this" (us) for damage & we aren't invulnerable
		int rev_ds_spell_id = Utils.SPELL_UNKNOWN;

		SoliniaActiveSpell spell = getFirstActiveSpellWithSpellEffectType(SpellEffectType.ReverseDS);
		if (spell != null && spell.getSpell() != null && spell.getSpellId() != Utils.SPELL_UNKNOWN)
			rev_ds_spell_id = spell.getSpellId();

		if (rev_ds < 0) {
			//Log(Logs::Detail, Logs::Combat, "Applying Reverse Damage Shield of value %d to %s", rev_ds, attacker->GetName());
			attacker.Damage(this, -rev_ds, rev_ds_spell_id, SkillType.Abjuration, false, -1, false); //"this" (us) will get the hate, etc. not sure how this works on Live, but it'll works for now, and tanks will love us for this
																												//do we need to send a damage packet here also?
		}
	}

	private void filteredMessageClose(LivingEntity source, String message, boolean actionBar) {
		ChatMessageType type = ChatMessageType.CHAT;
		if (actionBar)
			type = ChatMessageType.ACTION_BAR;
		
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.getLocation().distance(source.getLocation()) <= ChatUtils.GetLocalSayRange(source.getLocation().getWorld().getName()))
				(player).spigot().sendMessage(ChatMessageType.ACTION_BAR,new TextComponent(message));
		}
	}

	@Override
	public void buffFadeByEffect(SpellEffectType type) {
		this.removeActiveSpellsWithEffectType(type);
	}

	@Override
	public double setHPChange(int hpchange, LivingEntity causeOfEntityHpChange) {
		return this.setHPChange(hpchange, causeOfEntityHpChange, true);
	}
	
	@Override
	public double setHPChange(int hpchange, LivingEntity causeOfEntityHpChange, boolean playHurtSound) {
		if (hpchange == 0)
			return this.getHP();

		EntityUtils.PSetHPChange(this.getBukkitLivingEntity(), hpchange, causeOfEntityHpChange, playHurtSound);

		// SEND ENTITY HP CHANGES
		if (isCurrentlyNPCPet() && this.getActiveMob() != null && this.getOwnerEntity() instanceof Player) {
			PartyWindowUtils.UpdateWindow(((Player) this.getOwnerEntity()), false, false);
		}

		if (getBukkitLivingEntity() instanceof Player) {
			try {
				ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player) getBukkitLivingEntity());
				PartyWindowUtils.UpdateGroupWindowForEveryone(getBukkitLivingEntity().getUniqueId(),
						solPlayer.getGroup(), false);
			} catch (CoreStateInitException e) {

			}
		}
		
		if (hpchange < 0)
		damageAlertHook(hpchange,causeOfEntityHpChange);
		
		return this.getHP();
	}

	private double getHP() {
		// TODO Auto-generated method stub
		return this.getBukkitLivingEntity().getHealth();
	}

	@Override
	public boolean isSneaking() {
		if (!(this.getBukkitLivingEntity() instanceof Player))
			return false;
		
		Player player = (Player) this.getBukkitLivingEntity();
		if (player.isSneaking()) {
			try
			{
				ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
				if (solPlayer != null && solPlayer.getClassObj() != null) {
					if (solPlayer.getClassObj().isSneakFromCrouch()) {
						return true;
					}
				}
			} catch (CoreStateInitException e)
			{
				
			}
		}
		
		return false;
	}

	@Override
	public boolean IsValidSpell(int spell_id) {
		if (spell_id > 0 && spell_id != Utils.SPELL_UNKNOWN)
		{
			try
			{
				ISoliniaSpell spell = StateManager.getInstance().getConfigurationManager().getSpell(spell_id);
				if (spell != null)
					return true;
			} catch (CoreStateInitException e)
			{
				return false;
			}
		}
		return false;
	}

	private boolean hasShieldEquiped() {
		if (this.getBukkitLivingEntity() != null)
		{
			if (this.getEquipment().getItemInOffHand() != null)
				return this.getEquipment().getItemInOffHand().getType().equals(Material.SHIELD) || this.getEquipment().getItemInOffHand().getType().equals(Material.LEGACY_SHIELD);
		}
		
		return false;
	}

	private SkillType AttackAnimation(int Hand, ISoliniaItem weapon, SkillType skillinuse) {
		// Determine animation
		int type = 0;
		if (weapon != null) {
			switch (weapon.getItemType()) {
			case OneHandSlashing: // 1H Slashing
				skillinuse = SkillType.Slashing;
				type = AnimType.anim1HWeapon;
				break;
			case TwoHandSlashing: // 2H Slashing
				skillinuse = SkillType.TwoHandSlashing;
				type = AnimType.anim2HSlashing;
				break;
			case OneHandPiercing: // Piercing
				skillinuse = SkillType.Piercing;
				type = AnimType.anim1HPiercing;
				break;
			case OneHandBlunt: // 1H Blunt
				skillinuse = SkillType.Crushing;
				type = AnimType.anim1HWeapon;
				break;
			case TwoHandBlunt: // 2H Blunt
				skillinuse = SkillType.TwoHandBlunt;
				type = AnimType.anim2HSlashing; //anim2HWeapon
				break;
			case TwoHandPiercing: // 2H Piercing
				skillinuse = SkillType.TwoHandPiercing;
				type = AnimType.anim2HWeapon;
				break;
			case Martial:
				skillinuse = SkillType.HandtoHand;
				type = AnimType.animHand2Hand;
				break;
			default:
				skillinuse = SkillType.HandtoHand;
				type = AnimType.animHand2Hand;
				break;
			}// switch
		}
		else if (isNPC()) {
			switch (skillinuse) {
			case Slashing: // 1H Slashing
				type = AnimType.anim1HWeapon;
				break;
			case TwoHandSlashing: // 2H Slashing
				type = AnimType.anim2HSlashing;
				break;
			case Piercing: // Piercing
				type = AnimType.anim1HPiercing;
				break;
			case Crushing: // 1H Blunt
				type = AnimType.anim1HWeapon;
				break;
			case TwoHandBlunt: // 2H Blunt
				type = AnimType.anim2HSlashing; //anim2HWeapon
				break;
			case TwoHandPiercing: // 2H Piercing
				type = AnimType.anim2HWeapon;
				break;
			case HandtoHand:
				type = AnimType.animHand2Hand;
				break;
			default:
				type = AnimType.animHand2Hand;
				break;
			}// switch
		}
		else {
			skillinuse = SkillType.HandtoHand;
			type = AnimType.animHand2Hand;
		}

		// If we're attacking with the secondary hand, play the dual wield anim
		if (Hand == InventorySlot.Secondary)	// DW anim
			type = AnimType.animDualWield;

		DoAnim(type, 0, false);

		return skillinuse;
	}

	private void DoAnim(int animnum, int type, boolean ackreq) {
		SolAnimationType animationType = null;
		
		switch (animnum)
		{
			case AnimType.anim1HWeapon:
			case AnimType.anim1HPiercing:
			case AnimType.anim2HSlashing:
			case AnimType.anim2HWeapon:
			case AnimType.animHand2Hand:
				animationType = SolAnimationType.SwingArm;
				break;
			case AnimType.animDualWield:
				animationType = SolAnimationType.SwingOffArm;
				break;
			default:
				animationType = SolAnimationType.SwingArm;
				break;
		}

		if (this.getBukkitLivingEntity() instanceof Player)
			EntityUtils.sendAnimationPacket(this.getBukkitLivingEntity(), (Player)this.getBukkitLivingEntity(), animationType);
		
		// Nearby entities
		// Send shoot arrow to nearby people
		for (Entity listening : getBukkitLivingEntity().getNearbyEntities(20, 20, 20)) {
			if (listening instanceof Player)
				EntityUtils.sendAnimationPacket(getBukkitLivingEntity(),(Player)listening, animationType);
		}
	}

	private boolean getGM() {
		if (this.getBukkitLivingEntity() != null && this.getBukkitLivingEntity().isOp())
			return true;
		
		return false;
	}

	private boolean divineAura() {
		// TODO Auto-generated method stub
		return getSpellBonuses(SpellEffectType.DivineAura) > 0;
	}
	
	@Override
	public Tuple<Boolean,String> canUseItem(ISoliniaItem itemStack) {
		return this.canUseItem(itemStack.asItemStack());
	}

	@Override
	public Tuple<Boolean,String> canUseItem(ItemStack itemStack) {
		
		try {
			ISoliniaItem item = SoliniaItemAdapter.Adapt(itemStack);
			if (item == null)
				return new Tuple<Boolean,String>(true,"Not solinia item");
			
			if (!this.isNPC() && item.isWeaponOrBowOrShield() && this.isPlayer() && this.getPlayer() != null) {
				if (this.getPlayer().getSkillCap(getSkillByItemType(item.getItemType())) < 1) {
					return new Tuple<Boolean,String>(false,item.getItemType() + " has wrong class");
				}
			}
			
			if (!this.isNPC() && item.getAllowedClassNamesUpper().size() > 0) {
				if (getClassObj() == null) {
					return new Tuple<Boolean,String>(false,"Wrong class");
				}

				if (!item.getAllowedClassNamesUpper().contains(getClassObj().getName())) {
					return new Tuple<Boolean,String>(false,"Wrong class");
				}
			}
			
			if (!this.isNPC() && item.getAllowedRaceNamesUpper().size() > 0) {
				if (getRace() == null) {
					return new Tuple<Boolean,String>(false,"Wrong race");
				}

				if (!item.getAllowedRaceNamesUpper().contains(getRace().getName())) {
					return new Tuple<Boolean,String>(false,"Wrong race");
				}
			}

			// npc can use level item :-)
			if (!this.isNPC() && item.getMinLevel() > 0) {
				if (item.getMinLevel() > getActualLevel()) {
					return new Tuple<Boolean,String>(false,"Wrong level");
				}
			}
			
			// Is trying to  use a two hand but has item in offhand
			if (!this.isNPC() && item.getItemType().equals(ItemType.TwoHandBlunt) || item.getItemType().equals(ItemType.TwoHandPiercing) || item.getItemType().equals(ItemType.TwoHandSlashing) )
			{
				if (this.getEquipment().getItemInOffHand() != null && !this.getBukkitLivingEntity().getEquipment().getItemInOffHand().getType().equals(Material.AIR))
					return new Tuple<Boolean,String>(false,"Two hander with offhand");
			}
			
			//TODO Check reverse - offhand vs primary for 2hander

			return new Tuple<Boolean,String>(true,"");
		} catch (CoreStateInitException e) {
			return new Tuple<Boolean,String>(false,"Plugin not loaded");
		} catch (SoliniaItemException e) {
			return new Tuple<Boolean,String>(true,"Not solinia item");
		}
	}

	@Override
	public boolean isFeignedDeath() {
		try {
			return StateManager.getInstance().getEntityManager().isFeignedDeath(getBukkitLivingEntity().getUniqueId());
		} catch (CoreStateInitException e) {
			return false;
		}
	}

	@Override
	public void setFeigned(boolean feigned) {
		try {
			StateManager.getInstance().getEntityManager().setFeignedDeath(getBukkitLivingEntity().getUniqueId(),
					feigned);
			if (feigned == true) {
				getBukkitLivingEntity().sendMessage(ChatColor.GRAY + "* You feign your death");
				clearTargetsAgainstMe();

			} else {
				getBukkitLivingEntity().sendMessage(ChatColor.GRAY + "* You are no longer feigning death!");
			}
		} catch (CoreStateInitException e) {

		}
	}

	private void installNpcByMetaName(String metaid) {
		if (isPlayer())
			return;

		if (!metaid.contains("NPCID_"))
			return;

		int npcId = Integer.parseInt(metaid.substring(6));
		try {
			ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(npcId);

			if (npc == null)
				return;

			setActualLevel(npc.getLevel());
			setNpcid(npc.getId());
		} catch (CoreStateInitException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void setActualLevel(int level) {
		this.actualLevel = level;
	}

	@Override
	public FactionStandingType getNPCvsNPCReverseFactionCon(ISoliniaLivingEntity iOther)
	{
		if (iOther == null)
			return FactionStandingType.FACTION_INDIFFERENT;
		
		if (!this.isNPC())
			return FactionStandingType.FACTION_INDIFFERENT;
		
		if (!iOther.isNPC())
			return FactionStandingType.FACTION_INDIFFERENT;
			
		iOther = iOther.getOwnerOrSelf();
		int primaryFaction= iOther.getNPC().getFactionid();

		//I am pretty sure that this special faction call is backwards
		//and should be iOther->GetSpecialFactionCon(this)
		if (primaryFaction < 0)
			return GetSpecialFactionCon(iOther);

		if (primaryFaction == 0)
			return FactionStandingType.FACTION_INDIFFERENT;

		//if we are a pet, use our owner's faction stuff
		ISoliniaLivingEntity owner = getOwnerSoliniaLivingEntity();
		if (owner != null)
			return owner.getNPCvsNPCReverseFactionCon(iOther);

		//make sure iOther is an npc
		//also, if we dont have a faction, then they arnt gunna think anything of us either
		if(!iOther.isNPC() || this.getNPC().getFactionid() == 0)
			return(FactionStandingType.FACTION_INDIFFERENT);

		//if we get here, iOther is an NPC too

		//otherwise, employ the npc faction stuff
		//so we need to look at iOther's faction table to see
		//what iOther thinks about our primary faction
		return(iOther.getNPC().checkNPCFactionAlly(this.getNPC().getFactionid()));
	}

	private FactionStandingType GetSpecialFactionCon(ISoliniaLivingEntity iOther) {
		// TODO Auto-generated method stub
		return FactionStandingType.FACTION_INDIFFERENT;
	}

	@Override
	public int getFocusEffect(FocusEffect type, ISoliniaSpell spell) {
		if (spell.isBardSong() && type != FocusEffect.FcBaseEffects && type != FocusEffect.SpellDuration)
			return 0;

		if (spell.getNotFocusable() > 0)
			return 0;

		int realTotal = 0;
		int realTotal2 = 0;
		int realTotal3 = 0;
		boolean rand_effectiveness = false;

		List<ISoliniaItem> equippedItems = this.getEquippedSoliniaItems();

		// Check if item focus effect exists for the client.
		if (equippedItems.size() > 0) {

			ISoliniaItem TempItem = null;
			ISoliniaItem UsedItem = null;
			int UsedFocusID = 0;
			int Total = 0;
			int focus_max = 0;
			int focus_max_real = 0;

			// item focus
			for (ISoliniaItem item : equippedItems) {
				TempItem = item;

				if (TempItem == null)
					continue;

				if (TempItem.getFocusEffectId() > 0) {
					if (rand_effectiveness) {
						focus_max = CalcFocusEffect(type, TempItem.getFocusEffectId(), spell, true);
						if (focus_max > 0 && focus_max_real >= 0 && focus_max > focus_max_real) {
							focus_max_real = focus_max;
							UsedItem = TempItem;
							UsedFocusID = TempItem.getFocusEffectId();
						} else if (focus_max < 0 && focus_max < focus_max_real) {
							focus_max_real = focus_max;
							UsedItem = TempItem;
							UsedFocusID = TempItem.getFocusEffectId();
						}
					} else {
						Total = CalcFocusEffect(type, TempItem.getFocusEffectId(), spell, false);
						if (Total > 0 && realTotal >= 0 && Total > realTotal) {
							realTotal = Total;
							UsedItem = TempItem;
							UsedFocusID = TempItem.getFocusEffectId();
						} else if (Total < 0 && Total < realTotal) {
							realTotal = Total;
							UsedItem = TempItem;
							UsedFocusID = TempItem.getFocusEffectId();
						}
					}
				}

				// TODO Augs with focus effects support
			}

			if (UsedItem != null && rand_effectiveness && focus_max_real != 0)
				realTotal = CalcFocusEffect(type, UsedFocusID, spell, false);

			if ((rand_effectiveness && UsedItem != null) || (realTotal != 0 && UsedItem != null)) {
				// there are a crap ton more of these, I was able to verify these ones though
				// the RNG effective ones appear to have a different message for failing to
				// focus
				String string_id = "begins to glow"; // this is really just clicky message ...
				switch (type) {
				case SpellHaste:
					string_id = "shimmers briefly";
					break;
				case ManaCost: // this might be GROWS_DIM for fail
					string_id = "flickers with a pale light";
					break;
				case SpellDuration:
					string_id = "sparkles";
					break;
				case ImprovedDamage:
				case ImprovedDamage2:
					if (realTotal > 0)
						string_id = "feels alive with power";
					else
						string_id = "seems drained of power";
					break;
				case Range:
					string_id = "pulses with light as your vision sharpens";
					break;
				case SpellHateMod: // GLOWS_RED for increasing hate
					string_id = "glows blue";
					break;
				case ImprovedHeal:
					if (realTotal > 0)
						string_id = "feeds you with power";
					else
						string_id = "seems to drain your power into it";
					break;
				case ReagentCost: // this might be GROWS_DIM for fail as well ...
					string_id = "begins to shine";
					break;
				default:
					break;
				}

				if (getBukkitLivingEntity() instanceof Player) {
					ChatUtils.SendHint(getBukkitLivingEntity(), HINT.FOCUSEFFECTFLICKER, UsedItem.getDisplayname()+"^"+string_id, false);
				}
			}
		}

		// TODO spell focuses
		//

		
		
		// TODO aa focuses
		if (this.isPlayer())
		{
			int Total3 = 0;
			for (ISoliniaAARank aaFocusEffect : SpellUtils.getHighestRanksForFocusEffect(this.getSoliniaPlayer(), type))
			{
				Total3 = CalcAAFocus(type, aaFocusEffect, spell);
				if (Total3 > 0 && realTotal3 >= 0 && Total3 > realTotal3) {
					realTotal3 = Total3;
				}
				else if (Total3 < 0 && Total3 < realTotal3) {
					realTotal3 = Total3;
				}
			}
		}
		if (type == FocusEffect.ReagentCost
				&& (spell.isEffectInSpell(SpellEffectType.SummonItem) || spell.isSacrificeSpell()))
			return 0;

		return realTotal + realTotal2 + realTotal3;
	}

	private int CalcAAFocus(FocusEffect type, ISoliniaAARank aaRank, ISoliniaSpell spell) {
		//const SPDat_Spell_Struct &spell = spells[spell_id];
		
		try
		{

		int value = 0;
		int lvlModifier = 100;
		int spell_level = 0;
		int lvldiff = 0;
		int effect = 0;
		int base1 = 0;
		int base2 = 0;
		int slot = 0;

		boolean LimitFailure = false;
		//Todo LimitInclude
		boolean[] LimitInclude = new boolean[Utils.MAX_LIMIT_INCLUDE];
		Arrays.fill(LimitInclude, false);
		
		/* Certain limits require only one of several Include conditions to be true. Ie. Add damage to fire OR ice
		spells.
		0/1   SE_LimitResist
		2/3   SE_LimitSpell
		4/5   SE_LimitEffect
		6/7   SE_LimitTarget
		8/9   SE_LimitSpellGroup:
		10/11 SE_LimitCastingSkill:
		12/13 SE_LimitSpellClass:
		14/15 SE_LimitSpellSubClass:
		Remember: Update MaxLimitInclude in spdat.h if adding new limits that require Includes
		*/
		int FocusCount = 0;

		for (SoliniaAARankEffect rankEffect : aaRank.getEffects()) 
		{
			effect = rankEffect.getEffectId();
			base1 = rankEffect.getBase1();
			base2 = rankEffect.getBase2();
			slot = rankEffect.getSlotId();

			/*
			AA Foci's can contain multiple focus effects within the same AA.
			To handle this we will not automatically return zero if a limit is found.
			Instead if limit is found and multiple focus effects, we will reset the limit check
			when the next valid focus effect is found.
			*/

			if (
					SpellUtils.IsFocusEffect(null, 0, true, SpellUtils.getSpellEffectType(effect)) != FocusEffect.None 
					|| SpellUtils.getSpellEffectType(effect) == SpellEffectType.TriggerOnCast
				) {
				FocusCount++;
				// If limit found on prior check next, else end loop.
				if (FocusCount > 1) {

					for (int e = 0; e < Utils.MAX_LIMIT_INCLUDE; e += 2) {
						if (LimitInclude[e] && !LimitInclude[e + 1])
							LimitFailure = true;
					}

					if (LimitFailure) {
						value = 0;
						LimitFailure = false;

						for (int e = 0; e < Utils.MAX_LIMIT_INCLUDE; e++) {
							LimitInclude[e] = false; // Reset array
						}
					}

					else {
						break;
					}
				}
			}

			switch (SpellUtils.getSpellEffectType(effect)) 
			{
				case Blank:
					break;
	
				// Handle Focus Limits
	
				case LimitResist:
					if (base1 < 0) {
						if (spell.getResisttype() == -base1) // Exclude
							LimitFailure = true;
					} else {
						LimitInclude[0] = true;
						if (spell.getResisttype() == base1) // Include
							LimitInclude[1] = true;
					}
					break;
	
				case LimitInstant:
					if (base1 == 1 && spell.getBuffduration() > 0) // Fail if not instant
						LimitFailure = true;
					if (base1 == 0 && (spell.getBuffduration() == 0)) // Fail if instant
						LimitFailure = true;
	
					break;
	
				case LimitMaxLevel:
					if (getClassObj() != null)
					{
						spell_level = spell.getMinLevelClass(getClassObj().getName().toUpperCase());
						lvldiff = spell_level - base1;
						// every level over cap reduces the effect by base2 percent unless from a clicky when
						// ItemCastsUseFocus is true
						if (lvldiff > 0 && (spell_level <= StateManager.getInstance().getConfigurationManager().getMaxLevel())) {
							if (base2 > 0) {
								lvlModifier -= base2 * lvldiff;
								if (lvlModifier < 1)
									LimitFailure = true;
							} else
								LimitFailure = true;
						}
					}
					break;
	
				case LimitMinLevel:
					/*TODO LIMIT MIN LEVEL*/
					break;
	
				case LimitCastTimeMin:
					if (spell.getCastTime() < base1)
						LimitFailure = true;
					break;
	
				case LimitCastTimeMax:
					if (spell.getCastTime() > base1)
						LimitFailure = true;
					break;
	
				case LimitSpell:
					if (base1 < 0) { // Exclude
						if (spell.getId() == -base1)
							LimitFailure = true;
					} else {
						LimitInclude[2] = true;
						if (spell.getId() == base1) // Include
							LimitInclude[3] = true;
					}
					break;
	
				case LimitMinDur:
					if (base1 > spell.calcBuffDurationFormula(getMentorLevel(), spell.getBuffdurationformula(), spell.getAEDuration()))
						LimitFailure = true;
	
					break;
	
				case LimitEffect:
					if (base1 < 0) {
						if (spell.isEffectInSpell(SpellUtils.getSpellEffectType(-base1))) // Exclude
							LimitFailure = true;
					} else {
						LimitInclude[4] = true;
						// they use 33 here for all classes ... unsure if the type check is really needed
						if (SpellUtils.getSpellEffectType(base1) == SpellEffectType.SummonPet && type == FocusEffect.ReagentCost) {
							if (spell.isSummonPet() || spell.isSummonSkeleton())
								LimitInclude[5] = true;
						} else {
							if (spell.isEffectInSpell(SpellUtils.getSpellEffectType(base1))) // Include
								LimitInclude[5] = true;
						}
					}
					break;
	
				case LimitSpellType:
					switch (base1) {
					case 0:
						if (!spell.isDetrimental())
							LimitFailure = true;
						break;
					case 1:
						if (!spell.isBeneficial())
							LimitFailure = true;
						break;
					}
					break;
	
				case LimitManaMin:
					if (spell.getMana() < base1)
						LimitFailure = true;
					break;
	
				case LimitManaMax:
					if (spell.getMana() > base1)
						LimitFailure = true;
					break;
	
				case LimitTarget:
					if (base1 < 0) {
						if (-base1 == spell.getTargettype()) // Exclude
							LimitFailure = true;
					} else {
						LimitInclude[6] = true;
						if (base1 == spell.getTargettype()) // Include
							LimitInclude[7] = true;
					}
					break;
	
				case LimitCombatSkills:
					if (base1 == 0 && (spell.isCombatSkill() || isCombatProc(spell))) // Exclude Discs / Procs
						LimitFailure = true;
					else if (base1 == 1 && (!spell.isCombatSkill() || !isCombatProc(spell))) // Exclude Spells
						LimitFailure = true;
	
					break;
	
				case LimitSpellGroup:
					if (base1 < 0) {
						if (-base1 == spell.getSpellgroup()) // Exclude
							LimitFailure = true;
					} else {
						LimitInclude[8] = true;
						if (base1 == spell.getSpellgroup()) // Include
							LimitInclude[9] = true;
					}
					break;
	
				case LimitCastingSkill:
					if (base1 < 0) {
						if (-base1 == spell.getSkill())
							LimitFailure = true;
					} else {
						LimitInclude[10] = true;
						if (base1 == spell.getSkill())
							LimitInclude[11] = true;
					}
					break;
	
				case LimitSpellClass:
					if (base1 < 0) { // Exclude
						if (spell.checkSpellCategory(base1, SpellEffectType.LimitSpellClass))
							return (0);
					} else {
						LimitInclude[12] = true;
						if (spell.checkSpellCategory(base1, SpellEffectType.LimitSpellClass)) // Include
							LimitInclude[13] = true;
					}
					break;
	
				case LimitSpellSubclass:
					/* TODO LimitSPellSubClass
					if (base1 < 0) { // Exclude
						if (spell.checkSpellCategory(base1, SpellEffectType.LimitSpellSubclass))
							return (0);
					} else {
						LimitInclude[14] = true;
						if (spell.checkSpellCategory(base1, SpellEffectType.LimitSpellSubclass)) // Include
							LimitInclude[15] = true;
					}
					*/
					break;
	
				case LimitClass:
					/* TODO LimitClass
					// Do not use this limit more then once per spell. If multiple class, treat value like items
					// would.
					if (!PassLimitClass(base1, GetClass()))
						LimitFailure = true;
						*/
					break;
	
				case LimitRace:
					// TODO LIMIT Race
					/*
					if (base1 != GetRace())
						LimitFailure = true;
						*/
					break;
	
				case LimitUseMin:
					if (base1 > spell.getNumhits())
						LimitFailure = true;
					break;
	
				case LimitUseType:
					if (base1 != spell.getNumhitstype())
						LimitFailure = true;
					break;
	
				// Handle Focus Effects
				case ImprovedDamage:
					if (type == FocusEffect.ImprovedDamage && base1 > value)
						value = base1;
					break;
	
				case ImprovedDamage2:
					if (type == FocusEffect.ImprovedDamage2 && base1 > value)
						value = base1;
					break;
	
				case ImprovedHeal:
					if (type == FocusEffect.ImprovedHeal && base1 > value)
						value = base1;
					break;
	
				case ReduceManaCost:
					if (type == FocusEffect.ManaCost)
						value = base1;
					break;
	
				case IncreaseSpellHaste:
					if (type == FocusEffect.SpellHaste && base1 > value)
						value = base1;
					break;
	
				case IncreaseSpellDuration:
					if (type == FocusEffect.SpellDuration && base1 > value)
						value = base1;
					break;
	
				case SpellDurationIncByTic:
					if (type == FocusEffect.SpellDurByTic && base1 > value)
						value = base1;
					break;
	
				case SwarmPetDuration:
					if (type == FocusEffect.SwarmPetDuration && base1 > value)
						value = base1;
					break;
	
				case IncreaseRange:
					if (type == FocusEffect.Range && base1 > value)
						value = base1;
					break;
	
				case ReduceReagentCost:
					if (type == FocusEffect.ReagentCost && base1 > value)
						value = base1;
					break;
	
				case PetPowerIncrease:
					if (type == FocusEffect.PetPower && base1 > value)
						value = base1;
					break;
	
				case SpellResistReduction:
					if (type == FocusEffect.ResistRate && base1 > value)
						value = base1;
					break;
	
				case SpellHateMod:
					if (type == FocusEffect.SpellHateMod) {
						if (value != 0) {
							if (value > 0) {
								if (base1 > value)
									value = base1;
							} else {
								if (base1 < value)
									value = base1;
							}
						} else
							value = base1;
					}
					break;
	
				case ReduceReuseTimer:
					if (type == FocusEffect.ReduceRecastTime)
						value = base1 / 1000;
					break;
	
				case TriggerOnCast:
					if (type == FocusEffect.TriggerOnCast) {
						if (MathUtils.Roll(base1)) {
							value = base2;
						} else {
							value = 0;
							LimitFailure = true;
						}
						break;
					}
	
				case FcSpellVulnerability:
					if (type == FocusEffect.SpellVulnerability)
						value = base1;
					break;
	
				case BlockNextSpellFocus:
					if (type == FocusEffect.BlockNextSpell) {
						if (MathUtils.Roll(base1))
							value = 1;
					}
					break;
	
				case FcTwincast:
					if (type == FocusEffect.Twincast)
						value = base1;
					break;
	
				// Note if using these as AA, make sure this is first focus used.
				case SympatheticProc:
					if (type == FocusEffect.SympatheticProc)
						value = base2;
					break;
	
				case FcDamageAmt:
					if (type == FocusEffect.FcDamageAmt)
						value = base1;
					break;
	
				case FcDamageAmt2:
					if (type == FocusEffect.FcDamageAmt2)
						value = base1;
					break;
	
				case FcDamageAmtCrit:
					if (type == FocusEffect.FcDamageAmtCrit)
						value = base1;
					break;
	
				case FcDamageAmtIncoming:
					if (type == FocusEffect.FcDamageAmtIncoming)
						value = base1;
					break;
	
				case FcHealAmtIncoming:
					if (type == FocusEffect.FcHealAmtIncoming)
						value = base1;
					break;
	
				case FcHealPctCritIncoming:
					if (type == FocusEffect.FcHealPctCritIncoming)
						value = base1;
					break;
	
				case FcHealAmtCrit:
					if (type == FocusEffect.FcHealAmtCrit)
						value = base1;
					break;
	
				case FcHealAmt:
					if (type == FocusEffect.FcHealAmt)
						value = base1;
					break;
	
				case FcHealPctIncoming:
					if (type == FocusEffect.FcHealPctIncoming)
						value = base1;
					break;
	
				case FcBaseEffects:
					if (type == FocusEffect.FcBaseEffects)
						value = base1;
					break;
	
				case FcDamagePctCrit:
					if (type == FocusEffect.FcDamagePctCrit)
						value = base1;
					break;
	
				case FcIncreaseNumHits:
					if (type == FocusEffect.IncreaseNumHits)
						value = base1;
					break;
	
				case FcLimitUse:
					if (type == FocusEffect.FcLimitUse)
						value = base1;
					break;
	
				case FcMute:
					if (type == FocusEffect.FcMute)
						value = base1;
					break;
	
				case FcStunTimeMod:
					if (type == FocusEffect.FcStunTimeMod)
						value = base1;
					break;
				}
		}

		for (int e = 0; e < Utils.MAX_LIMIT_INCLUDE; e += 2) {
			if (LimitInclude[e] && !LimitInclude[e + 1])
				return 0;
		}

		if (LimitFailure)
			return 0;
		
		return (value * lvlModifier / 100);
		
		} catch (CoreStateInitException e)
		{
			
		}

		return 0;
	}

	public int CalcFocusEffect(FocusEffect type, int focusEffectId, ISoliniaSpell spell, boolean best_focus) {
		ISoliniaSpell focus_spell = null;

		try {
			focus_spell = StateManager.getInstance().getConfigurationManager().getSpell(focusEffectId);
		} catch (CoreStateInitException e) {

		}

		if (focus_spell == null || spell == null)
			return 0;

		int value = 0;
		int lvlModifier = 100;
		int lvldiff = 0;
		int MaxLimitInclude = EntityUtils.getMaxLimitInclude();
		boolean[] LimitInclude = new boolean[MaxLimitInclude];
		Arrays.fill(LimitInclude, false);

		// TODO SE Limiting

		// TODO max player effect counts

		for (SpellEffect focusSpellEffect : focus_spell.getBaseSpellEffects()) {
			switch (focusSpellEffect.getSpellEffectType()) {

			case Blank:
				break;

			case LimitResist:
				if (focusSpellEffect.getBase() < 0) {
					if (spell.getResisttype() == -focusSpellEffect.getBase()) // Exclude
						return 0;
				} else {
					LimitInclude[0] = true;
					if (spell.getResisttype() == focusSpellEffect.getBase()) // Include
						LimitInclude[1] = true;
				}
				break;

			case LimitInstant:
				if (focusSpellEffect.getBase() == 1 && spell.getBuffduration() > 0) // Fail if not instant
					return 0;
				if (focusSpellEffect.getBase() == 0 && (spell.getBuffduration() == 0)) // Fail if instant
					return 0;

				break;

			case LimitMaxLevel:
				if (isNPC())
					break;

				if (getClassObj() != null) {
					for (SoliniaSpellClass sclass : spell.getAllowedClasses()) {
						if (!sclass.getClassname().equals(this.getClassObj().getName()))
							continue;

						lvldiff = sclass.getMinlevel() - focusSpellEffect.getBase();
						// every level over cap reduces the effect by focusSpellEffect.getLimit()
						// percent unless from a clicky
						// when ItemCastsUseFocus is true
						try
						{
							if (lvldiff > 0 && (sclass.getMinlevel() <= StateManager.getInstance().getConfigurationManager().getMaxLevel())) {
								if (focusSpellEffect.getLimit() > 0) {
									lvlModifier -= focusSpellEffect.getLimit() * lvldiff;
									if (lvlModifier < 1)
										return 0;
								} else
									return 0;
							}
						} catch (CoreStateInitException e)
						{
							return 0;
						}
					}
				}

				break;

			case LimitMinLevel:
				if (isNPC())
					break;

				// TODO Limit classes

				if (getClassObj() != null) {
					for (SoliniaSpellClass sclass : spell.getAllowedClasses()) {
						if (!sclass.getClassname().equals(this.getClassObj().getName()))
							continue;

						if (sclass.getMinlevel() < focusSpellEffect.getBase())
							return 0;
					}
				}

				break;

			case LimitCastTimeMin:
				if (spell.getCastTime() < focusSpellEffect.getBase())
					return (0);
				break;

			case LimitCastTimeMax:
				if (spell.getCastTime() > focusSpellEffect.getBase())
					return (0);
				break;

			case LimitSpell:
				if (focusSpellEffect.getBase() < 0) { // Exclude
					if (spell.getId() == -focusSpellEffect.getBase())
						return (0);
				} else {
					LimitInclude[2] = true;
					if (spell.getId() == focusSpellEffect.getBase()) // Include
						LimitInclude[3] = true;
				}
				break;

			case LimitMinDur:
				if (focusSpellEffect.getBase() > spell.calcBuffDurationFormula(getEffectiveLevel(true),
						spell.getBuffdurationformula(), spell.getBuffduration()))
					return (0);
				break;

			case LimitEffect:
				if (focusSpellEffect.getBase() < 0) {
					if (spell.isEffectInSpell(SpellUtils.getSpellEffectType(-focusSpellEffect.getBase()))) // Exclude
						return 0;
				} else {
					LimitInclude[4] = true;
					if (spell.isEffectInSpell(SpellUtils.getSpellEffectType(focusSpellEffect.getBase()))) // Include
						LimitInclude[5] = true;
				}
				break;

			case LimitSpellType:
				switch (focusSpellEffect.getBase()) {
				case 0:
					if (!spell.isDetrimental())
						return 0;
					break;
				case 1:
					if (!spell.isBeneficial())
						return 0;
					break;
				default:
					focusSpellEffect.getBase();
				}
				break;

			case LimitManaMin:
				if (spell.getMana() < focusSpellEffect.getBase())
					return 0;
				break;

			case LimitManaMax:
				if (spell.getMana() > focusSpellEffect.getBase())
					return 0;
				break;

			case LimitTarget:
				if (focusSpellEffect.getBase() < 0) {
					if (-focusSpellEffect.getBase() == spell.getTargettype()) // Exclude
						return 0;
				} else {
					LimitInclude[6] = true;
					if (focusSpellEffect.getBase() == spell.getTargettype()) // Include
						LimitInclude[7] = true;
				}
				break;

			case LimitCombatSkills:
				if (focusSpellEffect.getBase() == 0 && (spell.isCombatSkill() || isCombatProc(spell))) // Exclude Discs
																										// / Procs
					return 0;
				else if (focusSpellEffect.getBase() == 1 && (!spell.isCombatSkill() || !isCombatProc(spell))) // Exclude
																												// Spells
					return 0;

				break;

			case LimitSpellGroup:
				if (focusSpellEffect.getBase() < 0) {
					if (-focusSpellEffect.getBase() == spell.getSpellgroup()) // Exclude
						return 0;
				} else {
					LimitInclude[8] = true;
					if (focusSpellEffect.getBase() == spell.getSpellgroup()) // Include
						LimitInclude[9] = true;
				}
				break;

			case LimitCastingSkill:
				if (focusSpellEffect.getBase() < 0) {
					if (-focusSpellEffect.getBase() == spell.getSkill())
						return 0;
				} else {
					LimitInclude[10] = true;
					if (focusSpellEffect.getBase() == spell.getSkill())
						LimitInclude[11] = true;
				}
				break;

			case LimitClass:
				// Do not use this limit more then once per spell. If multiple class, treat
				// value like items
				// would.
				if (!PassLimitClass(focusSpellEffect.getBase(), getClassObj()))
					return 0;
				break;

			case LimitRace:
				if (focusSpellEffect.getBase() != getRaceId())
					return 0;
				break;

			case LimitUseMin:
				if (focusSpellEffect.getBase() > spell.getNumhits())
					return 0;
				break;

			case LimitUseType:
				if (focusSpellEffect.getBase() != spell.getNumhitstype())
					return 0;
				break;

			case CastonFocusEffect:
				if (focusSpellEffect.getBase() > 0)
					focusSpellEffect.getBase();
				break;

			case LimitSpellClass:
				if (focusSpellEffect.getBase() < 0) { // Exclude
					if (CheckSpellCategory(spell, focusSpellEffect.getBase(), SpellEffectType.LimitSpellClass))
						return (0);
				} else {
					LimitInclude[12] = true;
					if (CheckSpellCategory(spell, focusSpellEffect.getBase(), SpellEffectType.LimitSpellClass)) // Include
						LimitInclude[13] = true;
				}
				break;

			case LimitSpellSubclass:
				if (focusSpellEffect.getBase() < 0) { // Exclude
					if (CheckSpellCategory(spell, focusSpellEffect.getBase(), SpellEffectType.LimitSpellSubclass))
						return (0);
				} else {
					LimitInclude[14] = true;
					if (CheckSpellCategory(spell, focusSpellEffect.getBase(), SpellEffectType.LimitSpellSubclass)) // Include
						LimitInclude[15] = true;
				}
				break;

			// handle effects
			case ImprovedDamage:
				if (type == FocusEffect.ImprovedDamage) {
					// This is used to determine which focus should be used for the random
					// calculation
					if (best_focus) {
						// If the spell contains a value in the base2 field then that is the max value
						if (focusSpellEffect.getLimit() != 0) {
							value = focusSpellEffect.getLimit();
						}
						// If the spell does not contain a base2 value, then its a straight non random
						// value
						else {
							value = focusSpellEffect.getBase();
						}
					}
					// Actual focus calculation starts here
					else if (focusSpellEffect.getLimit() == 0
							|| focusSpellEffect.getBase() == focusSpellEffect.getLimit()) {
						value = focusSpellEffect.getBase();
					} else {
						value = MathUtils.RandomBetween(focusSpellEffect.getBase(), focusSpellEffect.getLimit());
					}
				}
				break;

			case ImprovedDamage2:
				if (type == FocusEffect.ImprovedDamage2) {
					if (best_focus) {
						if (focusSpellEffect.getLimit() != 0) {
							value = focusSpellEffect.getLimit();
						} else {
							value = focusSpellEffect.getBase();
						}
					} else if (focusSpellEffect.getLimit() == 0
							|| focusSpellEffect.getBase() == focusSpellEffect.getLimit()) {
						value = focusSpellEffect.getBase();
					} else {
						value = MathUtils.RandomBetween(focusSpellEffect.getBase(), focusSpellEffect.getLimit());
					}
				}
				break;

			case ImprovedHeal:
				if (type == FocusEffect.ImprovedHeal) {
					if (best_focus) {
						if (focusSpellEffect.getLimit() != 0) {
							value = focusSpellEffect.getLimit();
						} else {
							value = focusSpellEffect.getBase();
						}
					} else if (focusSpellEffect.getLimit() == 0
							|| focusSpellEffect.getBase() == focusSpellEffect.getLimit()) {
						value = focusSpellEffect.getBase();
					} else {
						value = MathUtils.RandomBetween(focusSpellEffect.getBase(), focusSpellEffect.getLimit());
					}
				}
				break;

			case ReduceManaCost:
				if (type == FocusEffect.ManaCost) {
					if (best_focus) {
						if (focusSpellEffect.getLimit() != 0) {
							value = focusSpellEffect.getLimit();
						} else {
							value = focusSpellEffect.getBase();
						}
					} else if (focusSpellEffect.getLimit() == 0
							|| focusSpellEffect.getBase() == focusSpellEffect.getLimit()) {
						value = focusSpellEffect.getBase();
					} else {
						value = MathUtils.RandomBetween(focusSpellEffect.getBase(), focusSpellEffect.getLimit());
					}
				}
				break;

			case IncreaseSpellHaste:
				if (type == FocusEffect.SpellHaste && focusSpellEffect.getBase() > value)
					value = focusSpellEffect.getBase();
				break;

			case IncreaseSpellDuration:
				if (type == FocusEffect.SpellDuration && focusSpellEffect.getBase() > value)
					value = focusSpellEffect.getBase();
				break;

			case SpellDurationIncByTic:
				if (type == FocusEffect.SpellDurByTic && focusSpellEffect.getBase() > value)
					value = focusSpellEffect.getBase();
				break;

			case SwarmPetDuration:
				if (type == FocusEffect.SwarmPetDuration && focusSpellEffect.getBase() > value)
					value = focusSpellEffect.getBase();
				break;

			case IncreaseRange:
				if (type == FocusEffect.Range && focusSpellEffect.getBase() > value)
					value = focusSpellEffect.getBase();
				break;

			case ReduceReagentCost:
				if (type == FocusEffect.ReagentCost && focusSpellEffect.getBase() > value)
					value = focusSpellEffect.getBase();
				break;

			case PetPowerIncrease:
				if (type == FocusEffect.PetPower && focusSpellEffect.getBase() > value)
					value = focusSpellEffect.getBase();
				break;

			case SpellResistReduction:
				if (type == FocusEffect.ResistRate) {
					if (best_focus) {
						if (focusSpellEffect.getLimit() != 0) {
							value = focusSpellEffect.getLimit();
						} else {
							value = focusSpellEffect.getBase();
						}
					} else if (focusSpellEffect.getLimit() == 0
							|| focusSpellEffect.getBase() == focusSpellEffect.getLimit()) {
						value = focusSpellEffect.getBase();
					} else {
						value = MathUtils.RandomBetween(focusSpellEffect.getBase(), focusSpellEffect.getLimit());
					}
				}
				break;

			case SpellHateMod:
				if (type == FocusEffect.SpellHateMod) {
					if (value != 0) {
						if (value > 0) {
							if (focusSpellEffect.getBase() > value)
								value = focusSpellEffect.getBase();
						} else {
							if (focusSpellEffect.getBase() < value)
								value = focusSpellEffect.getBase();
						}
					} else
						value = focusSpellEffect.getBase();
				}
				break;

			case ReduceReuseTimer:
				if (type == FocusEffect.ReduceRecastTime)
					value = focusSpellEffect.getBase() / 1000;
				break;

			case TriggerOnCast:
				if (type == FocusEffect.TriggerOnCast) {
					if (MathUtils.RandomRoll(focusSpellEffect.getBase()))
						value = focusSpellEffect.getLimit();
					else
						value = 0;
				}
				break;

			case BlockNextSpellFocus:
				if (type == FocusEffect.BlockNextSpell) {
					if (MathUtils.RandomRoll(focusSpellEffect.getBase()))
						value = 1;
				}
				break;

			case SympatheticProc:
				if (type == FocusEffect.SympatheticProc) {
					value = focus_spell.getId();
				}
				break;

			case FcSpellVulnerability:
				if (type == FocusEffect.SpellVulnerability) {
					if (best_focus) {
						if (focusSpellEffect.getLimit() != 0)
							value = focusSpellEffect.getLimit();
						else
							value = focusSpellEffect.getBase();
					} else if (focusSpellEffect.getLimit() == 0
							|| focusSpellEffect.getBase() == focusSpellEffect.getLimit()) {
						value = focusSpellEffect.getBase();
					} else {
						value = MathUtils.RandomBetween(focusSpellEffect.getBase(), focusSpellEffect.getLimit());
					}
				}
				break;

			case FcTwincast:
				if (type == FocusEffect.Twincast)
					value = focusSpellEffect.getBase();
				break;

			case FcDamageAmt:
				if (type == FocusEffect.FcDamageAmt)
					value = focusSpellEffect.getBase();
				break;

			case FcDamageAmt2:
				if (type == FocusEffect.FcDamageAmt2)
					value = focusSpellEffect.getBase();
				break;

			case FcDamageAmtCrit:
				if (type == FocusEffect.FcDamageAmtCrit)
					value = focusSpellEffect.getBase();
				break;

			case FcDamageAmtIncoming:
				if (type == FocusEffect.FcDamageAmtIncoming)
					value = focusSpellEffect.getBase();
				break;

			case FcHealAmtIncoming:
				if (type == FocusEffect.FcHealAmtIncoming)
					value = focusSpellEffect.getBase();
				break;

			case FcDamagePctCrit:
				if (type == FocusEffect.FcDamagePctCrit)
					value = focusSpellEffect.getBase();
				break;

			case FcHealPctCritIncoming:
				if (type == FocusEffect.FcHealPctCritIncoming)
					value = focusSpellEffect.getBase();
				break;

			case FcHealAmtCrit:
				if (type == FocusEffect.FcHealAmtCrit)
					value = focusSpellEffect.getBase();
				break;

			case FcHealAmt:
				if (type == FocusEffect.FcHealAmt)
					value = focusSpellEffect.getBase();
				break;

			case FcHealPctIncoming:
				if (type == FocusEffect.FcHealPctIncoming)
					value = focusSpellEffect.getBase();
				break;

			case FcBaseEffects:
				if (type == FocusEffect.FcBaseEffects)
					value = focusSpellEffect.getBase();
				break;

			case FcIncreaseNumHits:
				if (type == FocusEffect.IncreaseNumHits)
					value = focusSpellEffect.getBase();
				break;

			case FcLimitUse:
				if (type == FocusEffect.FcLimitUse)
					value = focusSpellEffect.getBase();
				break;

			case FcMute:
				if (type == FocusEffect.FcMute)
					value = focusSpellEffect.getBase();
				break;

			case FcStunTimeMod:
				if (type == FocusEffect.FcStunTimeMod)
					value = focusSpellEffect.getBase();
				break;

			case FcTimerRefresh:
				if (type == FocusEffect.FcTimerRefresh)
					value = focusSpellEffect.getBase();
				break;
			default:
				break;
			}
		}

		for (int e = 0; e < MaxLimitInclude; e += 2) {
			if (LimitInclude[e] && !LimitInclude[e + 1])
				return 0;
		}

		// TODO Spell Finished

		return (value * lvlModifier / 100);
	}

	private boolean CheckSpellCategory(ISoliniaSpell spell, int base, SpellEffectType limitspellsubclass) {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean PassLimitClass(int base, ISoliniaClass classObj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getDamageCaps(int base_damage) {
		try
		{
			// this is based on a client function that caps melee base_damage
			int level = getMentorLevel();

			int stop_level = StateManager.getInstance().getConfigurationManager().getMaxLevel() + 1;
			if (stop_level <= level)
				return base_damage;
			int cap = 0;
			if (level >= 125) {
				cap = 7 * level;
			} else if (level >= 110) {
				cap = 6 * level;
			} else if (level >= 90) {
				cap = 5 * level;
			} else if (level >= 70) {
				cap = 4 * level;
			} else if (level >= 40) {
				if (getClassObj() != null)
					switch (getClassObj().getName()) {
					case "CLERIC":
					case "DRUID":
					case "SHAMAN":
						cap = 80;
						break;
					case "NECROMANCER":
					case "WIZARD":
					case "MAGICIAN":
					case "ENCHANTER":
						cap = 40;
						break;
					default:
						cap = 200;
						break;
					}
				else
					cap = 200;
			} else if (level >= 30) {
				if (getClassObj() != null)
					switch (getClassObj().getName()) {
					case "CLERIC":
					case "DRUID":
					case "SHAMAN":
						cap = 26;
						break;
					case "NECROMANCER":
					case "WIZARD":
					case "MAGICIAN":
					case "ENCHANTER":
						cap = 18;
						break;
					default:
						cap = 60;
						break;
					}
				else
					cap = 60;
			} else if (level >= 20) {
				if (getClassObj() != null)
					switch (getClassObj().getName()) {
					case "CLERIC":
					case "DRUID":
					case "SHAMAN":
						cap = 20;
						break;
					case "NECROMANCER":
					case "WIZARD":
					case "MAGICIAN":
					case "ENCHANTER":
						cap = 12;
						break;
					default:
						cap = 30;
						break;
					}
				else
					cap = 30;
			} else if (level >= 10) {
				if (getClassObj() != null)
					switch (getClassObj().getName()) {
					case "CLERIC":
					case "DRUID":
					case "SHAMAN":
						cap = 12;
						break;
					case "NECROMANCER":
					case "WIZARD":
					case "MAGICIAN":
					case "ENCHANTER":
						cap = 10;
						break;
					default:
						cap = 14;
						break;
					}
				else
					cap = 14;
			} else {
				if (getClassObj() != null)
					switch (getClassObj().getName()) {
					case "CLERIC":
					case "DRUID":
					case "SHAMAN":
						cap = 9;
						break;
					case "NECROMANCER":
					case "WIZARD":
					case "MAGICIAN":
					case "ENCHANTER":
						cap = 6;
						break;
					default:
						cap = 10; // this is where the 20 damage cap comes from
						break;
					}
				else
					cap = 10;
			}
	
			return Math.min(cap, base_damage);
		} catch (CoreStateInitException e)
		{
			return Math.min(10, base_damage);
		}
		
	}

	public boolean usingValidWeapon() {
		if (this.isCurrentlyNPCPet() || this.isNPC())
			return true;

		if (ItemStackUtils.IsSoliniaItem(getEquipment().getItemInMainHand())) {
			try {
				ISoliniaItem soliniaitem = StateManager.getInstance().getConfigurationManager()
						.getItem(getEquipment().getItemInMainHand());
				if (soliniaitem != null) {
					if (soliniaitem.isThrowing()) {
						getBukkitLivingEntity().sendMessage(
								ChatColor.GRAY + "This is a throwing weapon! (you must right click to throw it)");
					}
					
					if (!this.isNPC() && soliniaitem.isWeaponOrBowOrShield() && this.isPlayer() && this.getPlayer() != null) {
						if (this.getPlayer().getSkillCap(getSkillByItemType(soliniaitem.getItemType())) < 1) {
							getBukkitLivingEntity().sendMessage(
									ChatColor.GRAY + "" + soliniaitem.getItemType() + " has wrong class");
						}
					}

					if (soliniaitem.getAllowedClassNamesUpper().size() > 0) {
						if (getClassObj() == null) {
							getBukkitLivingEntity().sendMessage(ChatColor.GRAY + "Your class cannot use this item");
							return false;
						}

						if (!soliniaitem.getAllowedClassNamesUpper().contains(getClassObj().getName())) {
							getBukkitLivingEntity().sendMessage(ChatColor.GRAY + "Your class cannot use this item");
							return false;
						}
					}
					
					if (soliniaitem.getAllowedRaceNamesUpper().size() > 0) {
						if (getRace() == null) {
							getBukkitLivingEntity().sendMessage(ChatColor.GRAY + "Your race cannot use this item");
							return false;
						}

						if (!soliniaitem.getAllowedRaceNamesUpper().contains(getRace().getName())) {
							getBukkitLivingEntity().sendMessage(ChatColor.GRAY + "Your race cannot use this item");
							return false;
						}
					}

					if (soliniaitem.getMinLevel() > 0) {
						if (soliniaitem.getMinLevel() > getMentorLevel()) {
							getBukkitLivingEntity()
									.sendMessage(ChatColor.GRAY + "You are not sufficient level to use this item");
							return false;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return true;
	}

	@Override
	public boolean isCombatProc(ISoliniaSpell spell) {
		if ((spell.getCastTime() == 0) && (spell.getRecastTime() == 0) && (spell.getRecoveryTime() == 0)) {

			for (SoliniaActiveSpell activeSpell : getActiveSpells()) {
				try {
					ISoliniaSpell currentSpell = StateManager.getInstance().getConfigurationManager()
							.getSpell(activeSpell.getSpellId());
					if (currentSpell == null)
						continue;

					if (!currentSpell.isWeaponProc() && !currentSpell.isRangedProc())
						continue;

					for (ActiveSpellEffect spelleffect : activeSpell.getActiveSpellEffects()) {
						if (spelleffect.getSpellEffectType().equals(SpellEffectType.WeaponProc)
								|| spelleffect.getSpellEffectType().equals(SpellEffectType.RangedProc)) {
							if (currentSpell.getId() == spell.getId()) {
								return true;
							}
						}
					}
				} catch (CoreStateInitException e) {

				}
			}
		}

		return false;
	}
	
	@Override
	public int getMainWeaponDelay()
	{
		int delay = 30;
		ISoliniaItem item = getSoliniaItemInMainHand();
		if (item != null)
			delay = item.getWeaponDelay();
			
		return delay;
	}
	

	@Override
	public float getAutoAttackTimerFrequencySeconds() {
		float haste_mod = getHaste() * 0.01f;

		int hhe = getItemBonuses(SpellEffectType.HundredHands) + getSpellBonuses(SpellEffectType.HundredHands);
		float speed = 0;
		float delay = 3500;

		//we have a weapon, use its delay
		delay = 100 * getMainWeaponDelay();

		speed = delay / haste_mod;

		// TODO do quiver haste
		speed = (int)(speed + ((hhe / 1000.0f) * speed));
		
		return Math.max(Utils.MinHastedDelay,speed)/1000;
	}

	
	@Override
	public ISoliniaItem getSoliniaItemInMainHand()
	{
		ItemStack main = getEquipment().getItemInMainHand();
		if (main == null)
			return null;
		
		if (!ItemStackUtils.IsSoliniaItem(main))
			return null;
		
		try {
			return SoliniaItemAdapter.Adapt(main);
		} catch (SoliniaItemException | CoreStateInitException e) {
			return null;
		}
	}
	
	@Override
	public ISoliniaItem getSoliniaItemInOffHand()
	{
		ItemStack main = getEquipment().getItemInOffHand();
		if (main == null)
			return null;
		
		if (!ItemStackUtils.IsSoliniaItem(main))
			return null;
		
		try {
			return SoliniaItemAdapter.Adapt(main);
		} catch (SoliniaItemException | CoreStateInitException e) {
			return null;
		}
	}

	@Override
	public void damageAlertHook(double damage, Entity sourceEntity) {
		if (isCurrentlyNPCPet() && this.getActiveMob() != null && this.getActiveMob().getOwner() != null && this.getActiveMob().getOwner().isPresent()) {
			Entity owner = this.getOwnerEntity();
			if (owner != null) {
				if (owner != null && owner instanceof Player) {
					// HP warning to owner
					if (this.getHPRatio() < 10) {
						owner.sendMessage(ChatColor.GRAY + "* Your pet says 'Master I am low on health!'");
					}
				}
			}
		}

		if (isPlayer()) {
			try {
				ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player) this.getBukkitLivingEntity());

				PartyWindowUtils.UpdateGroupWindowForEveryone(getBukkitLivingEntity().getUniqueId(),
						solPlayer.getGroup(),false);

				// Update group
				if (solPlayer.getGroup() != null) {
					if (this.getBukkitLivingEntity().getHealth() < ((this.getBukkitLivingEntity()
							.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() / 100) * 10))
						solPlayer.getGroup().sendGroupMessage(solPlayer.getBukkitPlayer(),
								"[Notification] I am low on health!");
				}

			} catch (CoreStateInitException e) {
				// skip
			}
		}
	}
	
	@Override
	public boolean isCasting()
	{
		if (getBukkitLivingEntity() == null)
			return false;
		
		try
		{
			CastingSpell castingSpell = StateManager.getInstance().getEntityManager().getCasting(getBukkitLivingEntity());
			if (castingSpell != null) {
				return true;
			}
		} catch (CoreStateInitException e)
		{
			
		}
		
		return false;
	}
	
	@Override
	public void doClassAttacks(ISoliniaLivingEntity ca_target)
	{
		doClassAttacks(ca_target, SkillType.None, false);
	}

	@Override
	public void doClassAttacks(ISoliniaLivingEntity ca_target, SkillType skillType, boolean isRiposte)
	{
		if (ca_target == null)
			return;
		
		if (ca_target.getBukkitLivingEntity() == null || ca_target.getBukkitLivingEntity().isDead())
			return;
		
		if (this.isFeigned() || this.isFeared() || this.isStunned() || this.isMezzed())
			return;
		
		if(!this.isAttackAllowed(ca_target, false))
			return;
		
		if(!this.combatRange(ca_target)){
			return;
		}
		
		// TODO REUSE TIME AND HASTE MOD
		int ReuseTime = 0;
		//float HasteMod = getHaste() * 0.01f;
		
		SkillType skill_to_use = SkillType.None;
		
		if (skillType.equals(SkillType.ZZSpecialTypeMinusOne) && getClassObj() != null) {
			switch(getClassObj().getName()){
			case "WARRIOR":
			case "RANGER":
			case "BEASTLORD":
				skill_to_use = SkillType.Kick;
				break;
			case "BERSERKER":
				skill_to_use = SkillType.Frenzy;
				break;
			case "SHADOWKNIGHT":
			case "PALADIN":
				skill_to_use = SkillType.Bash;
				break;
			case "MONK":
				if(getMentorLevel() >= 30)
				{
					skill_to_use = SkillType.FlyingKick;
				}
				else if(getMentorLevel() >= 25)
				{
					skill_to_use = SkillType.DragonPunch;
				}
				else if(getMentorLevel() >= 20)
				{
					skill_to_use = SkillType.EagleStrike;
				}
				else if(getMentorLevel() >= 10)
				{
					skill_to_use = SkillType.TigerClaw;
				}
				else if(getMentorLevel() >= 5)
				{
					skill_to_use = SkillType.RoundKick;
				}
				else
				{
					skill_to_use = SkillType.Kick;
				}
				break;
			case "ROGUE":
				skill_to_use = SkillType.Backstab;
				break;
			}
		} else {
			if (SkillUtils.isValidSkill(skillType.name().toUpperCase()))
				skill_to_use = skillType;
		}

		if(skill_to_use.equals(SkillType.None))
			return;
		
		int dmg = getBaseSkillDamage(skill_to_use, getEntityTarget());	
		if (skill_to_use == SkillType.Bash) {
			if (ca_target!=this) {
				//DoAnim(animTailRake, 0, false);

				if (getWeaponDamage(ca_target, getEquipment().getItemInOffHand(), 0) <= 0 
						//&& getWeaponDamage(ca_target, GetInv().GetItem(EQEmu::invslot::slotShoulders)) <= 0
						)
					dmg = Utils.DMG_INVULNERABLE;

				//ReuseTime = (BashReuseTime - 1) / HasteMod;

				doSpecialAttackDamage(ca_target, SkillType.Bash, dmg, 0, -1, ReuseTime);

				/*if(ReuseTime > 0 && !isRiposte) {
					p_timers.Start(pTimerCombatAbility, ReuseTime);
				}*/
			}
			return;
		}
		
		if (skill_to_use.equals(SkillType.Frenzy)) {
			tryIncreaseSkill(SkillType.Frenzy, 10);
			int AtkRounds = 1;
			//DoAnim(anim2HSlashing, 0, false);

			//ReuseTime = (Utils.FRENZY_REUSETIME - 1) / HasteMod;

			// bards can do riposte frenzy for some reason
			if (getClassObj() != null)
			if (!isRiposte && getClassObj().getName().equals("BERSERKER"))
			{
				int chance = getMentorLevel() * 2 + getSkill(SkillType.Frenzy);
				if (MathUtils.RandomBetween(0,450) < chance)
					AtkRounds++;
				if (MathUtils.RandomBetween(0,450) < chance)
					AtkRounds++;
			}

			while(AtkRounds > 0) {
				if (ca_target!=this)
					doSpecialAttackDamage(ca_target, SkillType.Frenzy, dmg, 0, dmg, ReuseTime);
				AtkRounds--;
			}

			/*if(ReuseTime > 0 && !isRiposte) {
				p_timers.Start(pTimerCombatAbility, ReuseTime);
			}*/
			return;
		}
		
		if (skill_to_use.equals(SkillType.Kick))
		{
			if(!ca_target.getBukkitLivingEntity().getUniqueId().equals(this.getBukkitLivingEntity().getUniqueId()) 
					&& getBukkitLivingEntity() != null){
				//DoAnim(animKick, 0, false);

				if (getWeaponDamage(ca_target, getEquipment().getBoots(), 0) <= 0)
					dmg = Utils.DMG_INVULNERABLE;

				// TODO - REUSE TIME
				//reuseTime = KickReuseTime-1;

				doSpecialAttackDamage(ca_target, SkillType.Kick, dmg, 0, -1, ReuseTime);
			}
		}
		
		if (skill_to_use == SkillType.FlyingKick || skill_to_use == SkillType.DragonPunch || skill_to_use ==SkillType.EagleStrike || skill_to_use == SkillType.TigerClaw || skill_to_use == SkillType.RoundKick) {
			//ReuseTime = monkSpecialAttack(ca_target, skill_to_use) - 1;
			monkSpecialAttack(ca_target, skill_to_use);

			if (isRiposte)
				return;

			//Live AA - Technique of Master Wu
			int wuchance = getItemBonuses(SpellEffectType.DoubleSpecialAttack) + getSpellBonuses(SpellEffectType.DoubleSpecialAttack) + getAABonuses(SpellEffectType.DoubleSpecialAttack);
			if (wuchance > 0) {
				int[] MonkSPA = {SkillUtils.getSkillTypeId(SkillType.FlyingKick), SkillUtils.getSkillTypeId(SkillType.DragonPunch),
						SkillUtils.getSkillTypeId(SkillType.EagleStrike), SkillUtils.getSkillTypeId(SkillType.TigerClaw),
						SkillUtils.getSkillTypeId(SkillType.RoundKick)};
				int extra = 0;
				// always 1/4 of the double attack chance, 25% at rank 5 (100/4)
				while (wuchance > 0) {
					if (MathUtils.Roll(wuchance))
						extra++;
					else
						break;
					wuchance /= 4;
				}
				// They didn't add a string ID for this.
				ChatUtils.SendHint(this.getBukkitLivingEntity(), HINT.MASTERWUFULL, Integer.toString(extra), false);
				//std::string msg = StringFormat("The spirit of Master Wu fills you!  You gain %d additional attack(s).", extra);
				// live uses 400 here -- not sure if it's the best for all clients though
				//SendColoredText(400, msg);
				boolean classic = Utils.ClassicMasterWu;
				while (extra > 0) {
					monkSpecialAttack(ca_target,
							  classic ? SkillUtils.getSkillType(MonkSPA[MathUtils.RandomBetween(0, 4)]) : skill_to_use);
					extra--;
				}
			}
		}
	}
	
	private int monkSpecialAttack(ISoliniaLivingEntity other, SkillType unchecked_type) {
		if (other == null)
			return 0;

		int ndamage = 0;
		int max_dmg = 0;
		int min_dmg = 0;
		int reuse = 0;
		SkillType skill_type; // to avoid casting... even though it "would work"
		int itemslot = InventorySlot.Feet;
		if (isNPC()) {
			ISoliniaNPC npc = this.getNPC();
			if (npc != null)
			{
				min_dmg = npc.getMinDamage();
			}
		}

		switch (unchecked_type) {
		case FlyingKick:
			skill_type = SkillType.FlyingKick;
			max_dmg = getBaseSkillDamage(skill_type,null);
			min_dmg = 0; // revamped FK formula is missing the min mod?
			//DoAnim(animFlyingKick, 0, false);
			//reuse = FlyingKickReuseTime;
			break;
		case DragonPunch:
			skill_type = SkillType.DragonPunch;
			max_dmg = getBaseSkillDamage(skill_type,null);
			itemslot = InventorySlot.Hands;
			//DoAnim(animTailRake, 0, false);
			//reuse = TailRakeReuseTime;
			break;
		case EagleStrike:
			skill_type = SkillType.EagleStrike;
			max_dmg = getBaseSkillDamage(skill_type,null);
			itemslot = InventorySlot.Hands;
			//DoAnim(animEagleStrike, 0, false);
			//reuse = EagleStrikeReuseTime;
			break;
		case TigerClaw:
			skill_type = SkillType.TigerClaw;
			max_dmg = getBaseSkillDamage(skill_type,null);
			itemslot = InventorySlot.Hands;
			//DoAnim(animTigerClaw, 0, false);
			//reuse = TigerClawReuseTime;
			break;
		case RoundKick:
			skill_type = SkillType.RoundKick;
			max_dmg = getBaseSkillDamage(skill_type,null);
			//DoAnim(animRoundKick, 0, false);
			//reuse = RoundKickReuseTime;
			break;
		case Kick:
			skill_type = SkillType.Kick;
			max_dmg = getBaseSkillDamage(skill_type,null);
			//DoAnim(animKick, 0, false);
			//reuse = KickReuseTime;
			break;
		default:
			//Log(Logs::Detail, Logs::Attack, "Invalid special attack type %d attempted", unchecked_type);
			return (1000); /* nice long delay for them, the caller depends on this! */
		}

		if (isPlayer()) {
			if (getWeaponDamage(other, getItemStackBySlot(itemslot),0) <= 0) {
				max_dmg = Utils.DMG_INVULNERABLE;
			}
		} else {
			if (getWeaponDamage(other, getItemStackBySlot(itemslot),0) <= 0) {
				max_dmg = Utils.DMG_INVULNERABLE;
			}
		}

		int ht = 0;
		if (max_dmg > 0)
			ht = max_dmg;

		// This can potentially stack with changes to kick damage
		//ht = ndamage = mod_monk_special_damage(ndamage, skill_type);
		ht = ndamage;
		doSpecialAttackDamage(other, skill_type, max_dmg, min_dmg, ht, reuse);

		return reuse;
	}

	private int mod_monk_special_damage(int ndamage, SkillType skill_type) {
		// TODO Auto-generated method stub
		return 0;
	}

	private ItemStack getItemStackBySlot(int itemslot) {
		switch (itemslot)
		{
		case InventorySlot.Charm: return null;
		case InventorySlot.Ear1: return null;
		case InventorySlot.Head: return this.getEquipment().getHelmet();
		case InventorySlot.Face: return null;
		case InventorySlot.Ear2: return null;
		case InventorySlot.Neck: return null;
		case InventorySlot.Shoulders: return null;
		case InventorySlot.Arms: return null;
		case InventorySlot.Back: return null;
		case InventorySlot.Wrist1: return null;
		case InventorySlot.Wrist2: return null;
		case InventorySlot.Range: return this.getEquipment().getItemInOffHand();
		case InventorySlot.Hands: return null;
		case InventorySlot.Primary: return this.getEquipment().getItemInMainHand();
		case InventorySlot.Secondary: return this.getEquipment().getItemInOffHand();
		case InventorySlot.Finger1: return null;
		case InventorySlot.Finger2: return null;
		case InventorySlot.Chest: return this.getEquipment().getChestplate();
		case InventorySlot.Legs: return this.getEquipment().getLeggings();
		case InventorySlot.Feet: return this.getEquipment().getBoots();
		case InventorySlot.Waist: return null;
		case InventorySlot.Ammo: return null;
		case InventorySlot.General1: return null;
		case InventorySlot.General2: return null;
		case InventorySlot.General3: return null;
		case InventorySlot.General4: return null;
		case InventorySlot.General5: return null;
		case InventorySlot.General6: return null;
		case InventorySlot.General7: return null;
		case InventorySlot.General8: return null;
		case InventorySlot.Cursor: return null;
		default:
			return null;
		}
	}

	@Override
	public void doSpecialAttackDamage(ISoliniaLivingEntity who, SkillType skill, int base_damage, int min_damage, int hate_override,
			int ReuseTime) {
		// this really should go through the same code as normal melee damage to
		// pick up all the special behavior there

		if ((who == null ||
		     ((isPlayer() && this.getBukkitLivingEntity() != null && this.getBukkitLivingEntity().isDead()) || (who.isPlayer() && who.getBukkitLivingEntity() != null && who.getBukkitLivingEntity().isDead())) || this.getBukkitLivingEntity() != null && this.getBukkitLivingEntity().isDead() ||
		     (!isAttackAllowed(who, false))))
			return;
		
		if (!SkillUtils.isValidSkill(skill.name().toUpperCase()))
			return;

		DamageHitInfo my_hit = new DamageHitInfo();
		my_hit.damage_done = 1; // min 1 dmg
		my_hit.base_damage = base_damage;
		my_hit.min_damage = min_damage;
		my_hit.skill = skill;

		if (my_hit.base_damage == 0)
			my_hit.base_damage = getBaseSkillDamage(skill,who.getBukkitLivingEntity());

		if (who.getInvul() || who.getSpecialAbility(SpecialAbility.IMMUNE_MELEE) > 0)
			my_hit.damage_done = Utils.DMG_INVULNERABLE;

		if (who.getSpecialAbility(SpecialAbility.IMMUNE_MELEE_EXCEPT_BANE) > 0 && !skill.equals(SkillType.Backstab))
			my_hit.damage_done = Utils.DMG_INVULNERABLE;

		int hate = my_hit.base_damage;
		if (hate_override > -1)
			hate = hate_override;

		if (skill.equals(SkillType.Bash)) {
			if (isPlayer()) {
				ItemStack item = null;
				if (isPlayer()) {
					if (hasShieldEquiped())
					{
						item = this.getEquipment().getItemInOffHand();
					}
					else if (hasTwoHanderEquipped())
					{
						item = this.getEquipment().getItemInMainHand();
					}
				}
				if (item != null)
				{
					try
					{
						ISoliniaItem solItem = SoliniaItemAdapter.Adapt(item);
						
						if (solItem != null) {
							if (item.getType().equals(Material.SHIELD)) {
								hate += solItem.getAC();
							}
							int fbash = GetFuriousBash(solItem.getFocusEffectId());
							hate = hate * (100 + fbash) / 100;
							if (fbash > 0)
							{
								this.getBukkitLivingEntity().sendMessage("Your offhand weapon glows red");
								//Message_StringID(MT_Spells, GLOWS_RED, itm->Name);
							}
						}
					} catch (SoliniaItemException e)
					{
						
					} catch (CoreStateInitException e) {
						
					}
				}
			}
		}
		

		my_hit.offense = offense(my_hit.skill);
		my_hit.tohit = getTotalToHit(my_hit.skill, 0);

		my_hit.hand = InventorySlot.Primary; // Avoid checks hand for throwing/archery exclusion, primary should
							  // work for most
		if (skill.equals(SkillType.Throwing) || skill.equals(SkillType.Archery))
			my_hit.hand = InventorySlot.Range;

		doAttack(who, my_hit);

		who.addToHateList(this.getBukkitLivingEntity().getUniqueId(), hate, false);
		
		/* TODO - Procs after use of skill
		if (my_hit.damage_done > 0 && aabonuses.SkillAttackProc[0] && aabonuses.SkillAttackProc[1] == skill &&
		    IsValidSpell(aabonuses.SkillAttackProc[2])) {
			float chance = aabonuses.SkillAttackProc[0] / 1000.0f;
			if (MathUtils.Roll(chance))
				spellFinished(aabonuses.SkillAttackProc[2], who, EQEmu::CastingSlot::Item, 0, -1,
					      spells[aabonuses.SkillAttackProc[2]].ResistDiff);
		}
		*/

		who.Damage(this, my_hit.damage_done, Utils.SPELL_UNKNOWN, skill, false, hate, false);

		// We do procs above so no need to do the below
		/*
		// Make sure 'this' has not killed the target and 'this' is not dead (Damage shield ect).
		if (getEntityTarget() != null)
			return;
		
		if (this.getBukkitLivingEntity() != null && this.getBukkitLivingEntity().isDead())
			return;

		if (hasSkillProcs())
			trySkillProc(who, skill, ReuseTime * 1000);

		if (my_hit.damage_done > 0 && hasSkillProcSuccess())
			trySkillProc(who, skill, ReuseTime * 1000, true);*/
	}

	private int GetFuriousBash(int focusEffectId) {
		// TODO Auto-generated method stub
		if (focusEffectId < 1)
			return 0;
		
		if (!IsValidSpell(focusEffectId))
			return 0;

		boolean found_effect_limit = false;
		int mod = 0;

		mod += this.getSpellBonuses(SpellEffectType.SpellHateMod);
		
		// TODO Limiter
		//else if (spells[spell_id].effectid[i] == SE_LimitEffect && spells[spell_id].base[i] == 999)
		//found_effect_limit = true;

		if (found_effect_limit)
			return mod;
		else
			return 0;
	}

	@Override
	public int getBaseSkillDamage(SkillType skill, LivingEntity entityTarget) {
		try
		{
			int base = getBaseDamage(skill);
			int skill_level = getSkill(skill);
			switch (skill) {
			case DragonPunch:
			case EagleStrike:
			case TigerClaw:
			case RoundKick:
				if (skill_level >= 25)
					base++;
				if (skill_level >= 75)
					base++;
				if (skill_level >= 125)
					base++;
				if (skill_level >= 175)
					base++;
				return base;
			case Frenzy:
				if (isPlayer() && getEquipment().getItemInMainHand() != null) {
					if (getMentorLevel() > 15)
						base += getMentorLevel() - 15;
					if (base > 23)
						base = 23;
					if (getMentorLevel() > 50)
						base += 2;
					if (getMentorLevel() > 54)
						base++;
					if (getMentorLevel() > 59)
						base++;
				}
				return base;
			case FlyingKick: {
				float skill_bonus = skill_level / 9.0f;
				float ac_bonus = 0.0f;
				if (isPlayer()) {
					ItemStack inst = getEquipment().getBoots();
					if (inst != null)
					{
						try
						{
							ISoliniaItem solItem = SoliniaItemAdapter.Adapt(inst);
							ac_bonus = solItem.getAC() / 25.0f;
						} catch (SoliniaItemException e)
						{
							
						}
						
					}
				}
				if (ac_bonus > skill_bonus)
					ac_bonus = skill_bonus;
				return (int)(ac_bonus + skill_bonus);
			}
			case Kick: {
				// there is some base *= 4 case in here?
				float skill_bonus = skill_level / 10.0f;
				float ac_bonus = 0.0f;
				if (isPlayer()) {
					
					if (getEquipment().getBoots() != null)
					{
						try
						{
							ISoliniaItem item = SoliniaItemAdapter.Adapt(getEquipment().getBoots());
							if (item != null)
								ac_bonus = item.getAC() / 25.0f;
						} catch (CoreStateInitException e)
						{
							
						} catch (SoliniaItemException e) {
							// not a valid sol item
						}
					}
				}
				if (ac_bonus > skill_bonus)
					ac_bonus = skill_bonus;
				return (int)(ac_bonus + skill_bonus);
			}
			case Bash: {
				float skill_bonus = skill_level / 10.0f;
				float ac_bonus = 0.0f;
				ItemStack inst = null;
				if (isPlayer()) {
					if (hasShieldEquiped())
					{
						inst = this.getEquipment().getItemInOffHand();
					}
					else if (hasTwoHanderEquipped())
					{
						inst = this.getEquipment().getItemInMainHand();
					}
				}
				if (inst != null)
				{
					try
					{
						ISoliniaItem solItem = SoliniaItemAdapter.Adapt(inst);
						ac_bonus = solItem.getAC() / 25.0f;
					} catch (SoliniaItemException e)
					{
						
					}
				}
				else
					return 0; // return 0 in cases where we don't have an item
				if (ac_bonus > skill_bonus)
					ac_bonus = skill_bonus;
				return (int)(ac_bonus + skill_bonus);
			}
			/*case SkillType.Backstab: {
				float skill_bonus = (float)(skill_level) * 0.02f;
				base = 3; // There seems to be a base 3 for NPCs or some how BS w/o weapon?
				// until we get a better inv system for NPCs they get nerfed!
				if (isPlayer()) {
					auto *inst = CastToClient()->GetInv().GetItem(EQEmu::invslot::slotPrimary);
					if (inst && inst->GetItem() && inst->GetItem()->ItemType == EQEmu::item::ItemType1HPiercing) {
						base = inst->GetItemBackstabDamage(true);
						if (!inst->GetItemBackstabDamage())
							base += inst->GetItemWeaponDamage(true);
						if (target) {
							if (inst->GetItemElementalFlag(true) && inst->GetItemElementalDamage(true))
								base += target->ResistElementalWeaponDmg(inst);
							if (inst->GetItemBaneDamageBody(true) || inst->GetItemBaneDamageRace(true))
								base += target->CheckBaneDamage(inst);
						}
					}
				} else if (isNPC()) {
					auto *npc = CastToNPC();
					base = std::max(base, npc->GetBaseDamage());
					// parses show relatively low BS mods from lots of NPCs, so either their BS skill is super low
					// or their mod is divided again, this is probably not the right mod, but it's better
					skill_bonus /= 3.0f;
				}
				// ahh lets make sure everything is casted right :P ugly but w/e
				return static_cast<int>(static_cast<float>(base) * (skill_bonus + 2.0f));
			}*/
			default:
				return 0;
			}
		} catch (CoreStateInitException e)
		{
			return 0;
		}
	}

	private int getBaseDamage(SkillType skill) {
		switch (skill) {
		case Bash:
			return 2;
		case DragonPunch:
			return 12;
		case EagleStrike:
			return 7;
		case FlyingKick:
			return 25;
		case Kick:
			return 3;
		case RoundKick:
			return 5;
		case TigerClaw:
			return 4;
		case Frenzy:
			return 10;
		default:
			return 0;
		}
	}

	@Override
	public boolean combatRange(ISoliniaLivingEntity ca_target) {
		if (ca_target == null)
			return false;
		if (ca_target.getBukkitLivingEntity() == null)
			return false;
		if (this.getBukkitLivingEntity() == null)
			return false;
		
		// only account for x and z
		return java.awt.geom.Point2D.distance(ca_target.getBukkitLivingEntity().getLocation().getX(), ca_target.getBukkitLivingEntity().getLocation().getZ(), getBukkitLivingEntity().getLocation().getX(), getBukkitLivingEntity().getLocation().getZ())  <= 3D;
		//return ca_target.getBukkitLivingEntity().getLocation().distance(getBukkitLivingEntity().getLocation()) <= 3;
	}

	private DamageHitInfo doAttack(ISoliniaLivingEntity other, DamageHitInfo hit) {
		if (other == null)
			return hit;
		
		// for riposte
		int originalDamage = hit.damage_done;
		hit = other.avoidDamage(this, hit);

		if (hit.avoided == true) {
			// TODO Strike through

			if (hit.riposted == true && originalDamage > 0) {
				doRiposte(other);
				//if (IsDead())
				return hit;
			}

			if (hit.dodged == true) {
				if (other.isPlayer()) {
					((Player) (other.getBukkitLivingEntity())).spigot().sendMessage(ChatMessageType.ACTION_BAR,
							new TextComponent(ChatColor.GRAY + "* You dodge the attack!"));

					other.tryIncreaseSkill(SkillType.Dodge, 1);
				}

				if (isPlayer()) {
					((Player) getBukkitLivingEntity()).sendMessage(ChatColor.GRAY + "* "
							+ other.getBukkitLivingEntity().getCustomName() + " dodges your attack!");
				}
			}
		}

		if (hit.damage_done >= 0) {
			if (other.checkHitChance(this, hit)) {
				// npc chance to stun
				
				if (isNPC() && other.isPlayer() && /*other->animation > 0 && */getMentorLevel() >= 5 && isBehindEntity(other.getBukkitLivingEntity())) {
					// ~ 12% chance
					if (MathUtils.RandomRoll(12)) {
						int stun_resist2 = other.getSpellBonuses(SpellEffectType.FrontalStunResist) + other.getItemBonuses(SpellEffectType.FrontalStunResist) + other.getAABonuses(SpellEffectType.FrontalStunResist);
						int stun_resist = other.getSpellBonuses(SpellEffectType.StunResist) + other.getItemBonuses(SpellEffectType.StunResist) + other.getAABonuses(SpellEffectType.StunResist);
						if (MathUtils.RandomRoll(stun_resist2)) {
							other.sendMessage("You avoid a stunning blow");
							//other->Message_StringID(MT_Stun, AVOID_STUNNING_BLOW);
						} else if (MathUtils.RandomRoll(stun_resist)) {
							other.sendMessage("You shake off the stun");
							//other->Message_StringID(MT_Stun, SHAKE_OFF_STUN);
						} else {
							other.Stun(3000); // yuck -- 3 seconds
						}
					}
				}
				
				hit = other.meleeMitigation(this, hit);
				if (hit.damage_done > 0) {
					hit = applyDamageTable(hit);
					hit = commonOutgoingHitSuccess(other, hit);
				}
			} else {
				if (getBukkitLivingEntity() instanceof Player) {

					ChatUtils.SendHint(getBukkitLivingEntity(), HINT.HITTHEMBUTMISSED, other.getBukkitLivingEntity().getCustomName(),false);
				}
				if (this.isCurrentlyNPCPet() && this.getOwnerEntity() instanceof Player)
				{
					ChatUtils.SendHint((Player)this.getOwnerEntity(), HINT.PETHITTHEMBUTMISSED, other.getBukkitLivingEntity().getCustomName(),false);
				}
				
				if (other.getBukkitLivingEntity() instanceof Player) {
					ChatUtils.SendHint(other.getBukkitLivingEntity(), HINT.HITYOUBUTMISSED, getBukkitLivingEntity().getCustomName(), false);
					try {
						ISoliniaPlayer solplayer = SoliniaPlayerAdapter
								.Adapt((Player) other.getBukkitLivingEntity());
						solplayer.tryIncreaseSkill(SkillType.Defense, 1);
					} catch (CoreStateInitException e) {
						// skip
					}
				}
				hit.damage_done = 0;
			}
		}

		return hit;
	}

	private void doRiposte(ISoliniaLivingEntity defender) {
		if (defender == null || defender.getBukkitLivingEntity() == null)
			return;

		// so ahhh the angle you can riposte is larger than the angle you can hit :P
		if (!defender.isFacingMob(this)) {
			defender.sendMessage("Cant see target or too far away to riposte");
			return;
		}

		defender.Attack(this, InventorySlot.Primary, true, false,false);
		if (this.getBukkitLivingEntity().isDead())
			return;

		// this effect isn't used on live? See no AAs or spells
		int DoubleRipChance = getAABonuses(SpellEffectType.DoubleRiposte) + this.getSpellBonuses(SpellEffectType.DoubleRiposte) + this.getItemBonuses(SpellEffectType.DoubleRiposte);

		if (DoubleRipChance > 0 && MathUtils.Roll(DoubleRipChance)) {
			defender.Attack(this, InventorySlot.Primary, true,false,false);
			if (this.getBukkitLivingEntity().isDead())
				return;
		}

		DoubleRipChance = defender.getAABonuses(SpellEffectType.GiveDoubleRiposte) + defender.getSpellBonuses(SpellEffectType.GiveDoubleRiposte) + defender.getItemBonuses(SpellEffectType.GiveDoubleRiposte);

		// Live AA - Double Riposte
		if (DoubleRipChance > 0 && MathUtils.Roll(DoubleRipChance)) {
			defender.Attack(this, InventorySlot.Primary, true, false,false);
			if (this.getBukkitLivingEntity().isDead())
				return;
		}

		// Double Riposte effect, allows for a chance to do RIPOSTE with a skill specific special attack (ie Return Kick).
		// Coded narrowly: Limit to one per client. Limit AA only. [1 = Skill Attack Chance, 2 = Skill]

		/* TODO AA 
		 * DoubleRipChance = defender->aabonuses.GiveDoubleRiposte[1];

		if (DoubleRipChance && zone->random.Roll(DoubleRipChance)) {
			Log(Logs::Detail, Logs::Combat, "Preforming a return SPECIAL ATTACK (%d percent chance)",
				DoubleRipChance);

			if (defender->GetClass() == MONK)
				defender->MonkSpecialAttack(this, defender->aabonuses.GiveDoubleRiposte[2]);
			else if (defender->isPlayer()) // so yeah, even if you don't have the skill you can still do the attack :P (and we don't crash anymore)
				defender->CastToClient()->DoClassAttacks(this, defender->aabonuses.GiveDoubleRiposte[2], true);
		}
		*/
	}

	private DamageHitInfo commonOutgoingHitSuccess(ISoliniaLivingEntity defender, DamageHitInfo hit) {
		if (defender == null)
			return hit;

		if (hit.skill.equals(SkillType.Archery)
				||
				(hit.skill.equals(SkillType.Throwing) && getClassObj() != null && getClassObj().getName().equals("BERSERKER"))
				)
			hit.damage_done /= 2;

		if (hit.damage_done < 1)
			hit.damage_done = 1;

		// TODO Archery head shots
		if (hit.skill.equals(SkillType.Archery)) {

			int bonus = getItemBonuses(SpellEffectType.ArcheryDamageModifier) + getSpellBonuses(SpellEffectType.ArcheryDamageModifier) + getAABonuses(SpellEffectType.ArcheryDamageModifier);
			hit.damage_done += hit.damage_done * bonus / 100;
			
			int headshot = tryHeadShot(defender, hit.skill);

			if (headshot > 0) {
				hit.damage_done = headshot;
			}

			else if (getClassObj() != null && getClassObj().getClass().getName().equals("RANGER") && getMentorLevel() > 50) { // no
																														// double
																														// dmg
																														// on
																														// headshot
				if (defender.isNPC() && !defender.isRooted()) {
					this.getBukkitLivingEntity().sendMessage(ChatColor.GRAY + "* Your bow shot did double dmg!" + ChatColor.RESET);
					hit.damage_done *= 2;
				}
			}
		}

		int extra_mincap = 0;
		int min_mod = hit.base_damage * getMeleeMinDamageMod_SE(hit.skill) / 100;
		// TODO Backstab
		if (hit.skill.equals(SkillType.Backstab)) {
			extra_mincap = getMentorLevel() < 7 ? 7 : getMentorLevel();
			if (getMentorLevel() >= 60)
				extra_mincap = getMentorLevel() * 2;
			else if (getMentorLevel() > 50)
				extra_mincap = getMentorLevel() * 3 / 2;
			// TODO Chaotic stab 
			/*if (IsSpecialAttack(SpecialAttack.::ChaoticStab)) {
				hit.damage_done = extra_mincap;
			}
			else {
				int ass = TryAssassinate(defender, hit.skill);
				if (ass > 0)
					hit.damage_done = ass;
			}
			*/
			
			int ass = tryAssassinate(defender, hit.skill);
			if (ass > 0)
				hit.damage_done = ass;
		}
		else if (hit.skill.equals(SkillType.Frenzy) && getClassObj() != null && getClassObj().getName().equals("BERSERKER") && getMentorLevel() > 50) {
			extra_mincap = 4 * getMentorLevel() / 5;
		}

		// this has some weird ordering
		// Seems the crit message is generated before some of them :P
		
		// worn item +skill dmg, SPA 220, 418. Live has a normalized version that should be here too
		hit.min_damage += getSkillDmgAmt(hit.skill);

		// TODO shielding mod2
		if (defender.getItemBonuses(SpellEffectType.MeleeMitigation) > 0)
			hit.min_damage -= hit.min_damage * defender.getItemBonuses(SpellEffectType.MeleeMitigation) / 100;
		
		hit.damage_done = applyMeleeDamageMods(hit.skill, hit.damage_done, defender);
		min_mod = Math.max(min_mod, extra_mincap);
		if (min_mod > 0 && hit.damage_done < min_mod) // SPA 186
			hit.damage_done = min_mod;
		
		hit = tryCriticalHit(defender, hit);

		
		hit.damage_done += hit.min_damage;

		// this appears where they do special attack dmg mods
		int spec_mod = 0;

		// TODO RAMPAGE

		if (spec_mod > 0)
			hit.damage_done = (hit.damage_done * spec_mod) / 100;

		hit.damage_done += (hit.damage_done * defender.getSkillDmgTaken(hit.skill) / 100)
				+ (defender.getFcDamageAmtIncoming(this, 0, true, hit.skill));

		checkNumHitsRemaining(NumHit.OutgoingHitSuccess);
		return hit;
	}

	private int tryAssassinate(ISoliniaLivingEntity defender, SkillType skillInUse) {
		if (defender != null && (defender.getBodyType() == BodyType.BT_Humanoid) && !defender.isPlayer() && getMentorLevel() >= 60 &&
			    (skillInUse.equals(SkillType.Backstab) || skillInUse.equals(SkillType.Throwing))) {
				int chance = getDexterity();
				if (skillInUse.equals(SkillType.Backstab)) {
					chance = 100 * chance / (chance + 3500);
					chance *= 10;
					int norm = getAABonusesTuple(SpellEffectType.AssassinateLevel).b();
					if (norm > 0)
						chance = chance * norm / 100;
				} else if (skillInUse.equals(SkillType.Throwing)) {
					if (chance > 255)
						chance = 260;
					else
						chance += 5;
				}

				chance += getAABonuses(SpellEffectType.Assassinate) + getSpellBonuses(SpellEffectType.Assassinate) + getItemBonuses(SpellEffectType.Assassinate);

				int Assassinate_Dmg = Collections.max(new ArrayList<Integer>(Arrays.asList(getAABonusesTuple(SpellEffectType.Assassinate).b(), getSpellBonusesTuple(SpellEffectType.Assassinate).b(), getItemBonusesTuple(SpellEffectType.Assassinate).b())));

				int Assassinate_Level = 0; // Get Highest Headshot Level
				Assassinate_Level = Collections.max(new ArrayList<Integer>(Arrays.asList(getAABonuses(SpellEffectType.AssassinateLevel), getSpellBonuses(SpellEffectType.AssassinateLevel), getItemBonuses(SpellEffectType.AssassinateLevel))));

				// revamped AAs require AA line I believe?
				if (Assassinate_Level < 1)
					return 0;

				if (Assassinate_Dmg > 0 && Assassinate_Level > 0 && (defender.getMentorLevel() <= Assassinate_Level)) {
					if (MathUtils.RandomBetween(1, 1000) <= chance) {
						ChatUtils.SendHint(defender.getBukkitLivingEntity(), HINT.ASSASSINATES, this.getName(), true);
						/*entity_list.MessageClose_StringID(this, false, 200, MT_CritMelee, ASSASSINATES,
										  GetName());*/
						return Assassinate_Dmg;
					}
				}
			}

			return 0;
	}

	@Override
	public void checkNumHitsRemaining(NumHit type) {
		checkNumHitsRemaining(type, -1, null);
	}

	@Override
	public void checkNumHitsRemaining(NumHit type, int buffSlot, Integer spellId) {
		try {
			boolean depleted = false;
			if (spellId == null) {
				for (SoliniaActiveSpell spell : StateManager.getInstance().getEntityManager()
						.getActiveEntitySpells(getBukkitLivingEntity()).getActiveSpells()) {

					if (spellId != null && spellId > 0)
						if (spell.getSpellId() != spellId)
							continue;

					if (spell.getSpell().getNumhits() < 1)
						continue;

					if (!EntityUtils.getNumHitsType(spell.getSpell().getNumhitstype()).name().toLowerCase()
							.equals(type.name().toLowerCase()))
						continue;

					spell.setNumHits(spell.getNumHits() - 1);

					if (spell.getNumHits() < 1) {
						spell.tryFadeEffect();
					}

				}
			}
		} catch (CoreStateInitException e) {

		}
	}

	private int tryHeadShot(ISoliniaLivingEntity defender, SkillType skillType) {
		// Only works on YOUR target.
		if (skillType.equals(SkillType.Archery)
				&& !getBukkitLivingEntity().getUniqueId().equals(defender.getBukkitLivingEntity().getUniqueId())) {
			int HeadShot_Dmg = 0;
			int spellHeadShotModifier = getSpellBonuses(SpellEffectType.HeadShot);
			int aaHeadShotModifier = getAABonuses(SpellEffectType.HeadShot);
			HeadShot_Dmg = spellHeadShotModifier + aaHeadShotModifier;

			int HeadShot_Level = 0; // Get Highest Headshot Level

			int spellHeadShotLevelModifier = getSpellBonuses(SpellEffectType.HeadShotLevel);
			int aaHeadShotLevelModifier = getAABonuses(SpellEffectType.HeadShotLevel);

			HeadShot_Level = Math.max(spellHeadShotLevelModifier, aaHeadShotLevelModifier);

			if (HeadShot_Dmg > 0 && HeadShot_Level > 0 && (defender.getMentorLevel() <= HeadShot_Level)) {
				int chance = getDexterity();
				chance = 100 * chance / (chance + 3500);

				chance *= 10;
				/*
				 * TODO reading base2 values from AAs int norm = aabonuses.HSLevel[1]; if (norm
				 * > 0) chance = chance * norm / 100;
				 */
				chance += aaHeadShotLevelModifier + spellHeadShotLevelModifier;
				if (MathUtils.RandomBetween(1, 1000) <= chance) {
					emote(" is hit by a fatal blow");
					return HeadShot_Dmg;
				}
			}
		}

		return 0;
	}

	private int getMeleeMinDamageMod_SE(SkillType skillType) {
		int dmg_mod = 0;
		
		/*
		// Needs to pass skill as parameter
		dmg_mod = getItemBonuses(SpellEffectType.MinDamageModifier, skillType) +
		getSpellBonuses(SpellEffectType.MinDamageModifier, skillType) //+
		// itembonuses.MinDamageModifier[EQEmu::skills::HIGHEST_SKILL + 1] +
		// spellbonuses.MinDamageModifier[EQEmu::skills::HIGHEST_SKILL + 1]
				;
		 */
		if (dmg_mod < -100)
			dmg_mod = -100;

		return dmg_mod;
	}

	@Override
	public int getMaxBindWound_SE() {
		int bindmod = 0;

		int spellMaxBindWound = getSpellBonuses(SpellEffectType.MaxBindWound);
		int aaMaxBindWound = getAABonuses(SpellEffectType.MaxBindWound);

		bindmod += spellMaxBindWound;
		bindmod += aaMaxBindWound;

		return bindmod;
	}

	@Override
	public int getBindWound_SE() {
		int bindmod = 0;

		int spellMaxBindWound = getSpellBonuses(SpellEffectType.ImprovedBindWound);
		int aaMaxBindWound = getAABonuses(SpellEffectType.ImprovedBindWound);

		bindmod += spellMaxBindWound;
		bindmod += aaMaxBindWound;

		return bindmod;
	}

	private int getMeleeDamageMod_SE(SkillType skillType) {
		int dmg_mod = 0;

		int spellDamageModifier = getSpellBonuses(SpellEffectType.DamageModifier);
		int aaDamageModifier = getAABonuses(SpellEffectType.DamageModifier);

		dmg_mod += spellDamageModifier;
		dmg_mod += aaDamageModifier;

		if (dmg_mod < -100)
			dmg_mod = -100;

		return dmg_mod;
	}

	private int applyMeleeDamageMods(SkillType skillType, int damage_done, ISoliniaLivingEntity defender) {
		int dmgbonusmod = 0;

		dmgbonusmod += getMeleeDamageMod_SE(skillType);

		if (defender != null) {
			if (defender.isPlayer() && defender.getClassObj() != null
					&& defender.getClassObj().getName().equals("WARRIOR"))
				dmgbonusmod -= 5;
			// 168 defensive MeleeMitigation
			dmgbonusmod += (defender.getSpellBonuses(SpellEffectType.MeleeMitigation) +
			getItemBonuses(SpellEffectType.MeleeMitigation) + getAABonuses(SpellEffectType.MeleeMitigation));
		}

		damage_done += damage_done * dmgbonusmod / 100;

		return damage_done;
	}

	private int getSkillDmgAmt(SkillType skillType) {
		int skill_dmg = 0;
		return skill_dmg;
	}

	private DamageHitInfo tryCriticalHit(ISoliniaLivingEntity defender, DamageHitInfo hit) {
		if (defender == null)
			return hit;

		if (hit.damage_done < 1)
			return hit;

		String className = "UNKNOWN";
		if (getClassObj() != null) {
			className = getClassObj().getName();
		}

		// TODO Slay Undead AA
		// 1: Try Slay Undead
		if (defender.getBodyType() == BodyType.BT_Undead || defender.getBodyType() == BodyType.BT_SummonedUndead || defender.getBodyType() == BodyType.BT_Vampire) 
		{
			int SlayRateBonus = getAABonusesTuple(SpellEffectType.SlayUndead).a() + getItemBonusesTuple(SpellEffectType.SlayUndead).a() + getSpellBonusesTuple(SpellEffectType.SlayUndead).a();
			if (SlayRateBonus > 0) {
				float slayChance = (float)(SlayRateBonus) / 10000.0f;
				if (MathUtils.Roll(slayChance)) {
					
					int SlayDmgBonus = Collections.max(new ArrayList<Integer>(Arrays.asList(getAABonusesTuple(SpellEffectType.SlayUndead).b(), getSpellBonusesTuple(SpellEffectType.SlayUndead).b(), getItemBonusesTuple(SpellEffectType.SlayUndead).b())));

					hit.damage_done = Math.max(hit.damage_done, hit.base_damage) + 5;
					hit.damage_done = (hit.damage_done * SlayDmgBonus) / 100;

					ChatUtils.SendHint(this.getBukkitLivingEntity(), HINT.SLAYUNDEAD, this.getBukkitLivingEntity().getName(), true);
					return hit;
				}
			}
		}

		// 2: Try Melee Critical

		boolean innateCritical = false;
		int critChance = SkillUtils.getCriticalChanceBonus(this, hit.skill);
		if ((className.equals("WARRIOR") || className.equals("BERSERKER")) && getMentorLevel() >= 12)
			innateCritical = true;
		else if (className.equals("RANGER") && getMentorLevel() >= 12 && hit.skill.equals(SkillType.Archery))
			innateCritical = true;
		else if (className.equals("ROGUE") && getMentorLevel() >= 12 && hit.skill.equals(SkillType.Throwing))
			innateCritical = true;

		// we have a chance to crit!
		if (innateCritical || critChance > 0) {
			int difficulty = 0;
			if (hit.skill.equals(SkillType.Archery))
				difficulty = 3400;
			else if (hit.skill.equals(SkillType.Throwing))
				difficulty = 1100;
			else
				difficulty = 8900;

			// attacker.sendMessage("You have a chance to cause a critical (Diffulty dice
			// roll: " + difficulty);
			int roll = MathUtils.RandomBetween(1, difficulty);
			// attacker.sendMessage("Critical chance roll ended up as: " + roll);

			int dex_bonus = getDexterity();
			if (dex_bonus > 255)
				dex_bonus = 255 + ((dex_bonus - 255) / 5);
			dex_bonus += 45;
			// attacker.sendMessage("Critical dex bonus was: " + dex_bonus);

			// so if we have an innate crit we have a better chance, except for ber throwing
			if (!innateCritical || (className.equals("BERSERKER") && hit.skill.equals(SkillType.Throwing)))
				dex_bonus = dex_bonus * 3 / 5;

			if (critChance > 0)
				dex_bonus += dex_bonus * critChance / 100;

			// attacker.sendMessage("Checking if your roll: " + roll + " is less than the
			// dexbonus: " + dex_bonus);

			// check if we crited
			if (roll < dex_bonus) {

				// TODO: Finishing Blow

				// step 2: calculate damage
				hit.damage_done = Math.max(hit.damage_done, hit.base_damage) + 5;
				// attacker.sendMessage("Taking the maximum out of damageDone: " + damageDone +
				// " vs baseDamage: " + baseDamage + " adding 5 to it");

				double og_damage = hit.damage_done;
				int crit_mod = 170 + SkillUtils.getCritDmgMod(hit.skill);
				if (crit_mod < 100) {
					crit_mod = 100;
				}
				// attacker.sendMessage("Crit mod was: " + crit_mod);

				hit.damage_done = hit.damage_done * crit_mod / 100;
				// attacker.sendMessage("DamageDone was calculated at: " + damageDone);

				// TODO Spell bonuses && holyforge
				double totalCritBonus = (hit.damage_done - hit.base_damage);

				DecimalFormat df = new DecimalFormat();
				df.setMaximumFractionDigits(2);

				// Berserker
				if (isBerserk()) {
					hit.damage_done += og_damage * 119 / 100;
					// attacker.sendMessage("You are also berserker: damageDone now is: " +
					// damageDone);
					// attacker.sendMessage("* Your berserker status causes a critical blow!");

					totalCritBonus = (hit.damage_done - hit.base_damage);

					if (getBukkitLivingEntity() instanceof Player) {
						ChatUtils.SendHint(getBukkitLivingEntity(), HINT.BER_CRITICAL_DMG, ""+df.format(totalCritBonus), false);
					}

					return hit;
				}

				if (getBukkitLivingEntity() instanceof Player) {
					ChatUtils.SendHint(getBukkitLivingEntity(), HINT.CRITICAL_DMG, ""+df.format(totalCritBonus), false);
				}

				// attacker.sendMessage("* Your score a critical hit (" + damageDone + ")!");
				return hit;
			}

		}

		return hit;
	}

	private DamageHitInfo applyDamageTable(DamageHitInfo hit) {
		// someone may want to add this to custom servers, can remove this if that's the case
		if (!this.isPlayer())
			return hit;
		
		if (hit.offense < 115)
			return hit;

		if (hit.damage_done < 2)
			return hit;

		// 0 = max_extra
		// 1 = chance
		// 2 = minusfactor
		int[] damage_table = getDamageTable();
		if (MathUtils.Roll(damage_table[1]))
			return hit;

		int basebonus = hit.offense - damage_table[2];

		basebonus = Math.max(10, basebonus / 2);
		int extrapercent = MathUtils.RandomBetween(0, basebonus);
		int percent = Math.min(100 + extrapercent, damage_table[0]);
		hit.damage_done = (hit.damage_done * percent) / 100;
		if (getClassObj() != null) {
			if (getClassObj().isWarriorClass() && getMentorLevel() > 54)
				hit.damage_done++;
		}
		return hit;
	}

	public int[] getDamageTable() {
		int[][] dmg_table = { { 210, 49, 105 }, // 1-50
				{ 245, 35, 80 }, // 51
				{ 245, 35, 80 }, // 52
				{ 245, 35, 80 }, // 53
				{ 245, 35, 80 }, // 54
				{ 245, 35, 80 }, // 55
				{ 265, 28, 70 }, // 56
				{ 265, 28, 70 }, // 57
				{ 265, 28, 70 }, // 58
				{ 265, 28, 70 }, // 59
				{ 285, 23, 65 }, // 60
				{ 285, 23, 65 }, // 61
				{ 285, 23, 65 }, // 62
				{ 290, 21, 60 }, // 63
				{ 290, 21, 60 }, // 64
				{ 295, 19, 55 }, // 65
				{ 295, 19, 55 }, // 66
				{ 300, 19, 55 }, // 67
				{ 300, 19, 55 }, // 68
				{ 300, 19, 55 }, // 69
				{ 305, 19, 55 }, // 70
				{ 305, 19, 55 }, // 71
				{ 310, 17, 50 }, // 72
				{ 310, 17, 50 }, // 73
				{ 310, 17, 50 }, // 74
				{ 315, 17, 50 }, // 75
				{ 315, 17, 50 }, // 76
				{ 325, 17, 45 }, // 77
				{ 325, 17, 45 }, // 78
				{ 325, 17, 45 }, // 79
				{ 335, 17, 45 }, // 80
				{ 335, 17, 45 }, // 81
				{ 345, 17, 45 }, // 82
				{ 345, 17, 45 }, // 83
				{ 345, 17, 45 }, // 84
				{ 355, 17, 45 }, // 85
				{ 355, 17, 45 }, // 86
				{ 365, 17, 45 }, // 87
				{ 365, 17, 45 }, // 88
				{ 365, 17, 45 }, // 89
				{ 375, 17, 45 }, // 90
				{ 375, 17, 45 }, // 91
				{ 380, 17, 45 }, // 92
				{ 380, 17, 45 }, // 93
				{ 380, 17, 45 }, // 94
				{ 385, 17, 45 }, // 95
				{ 385, 17, 45 }, // 96
				{ 390, 17, 45 }, // 97
				{ 390, 17, 45 }, // 98
				{ 390, 17, 45 }, // 99
				{ 395, 17, 45 }, // 100
				{ 395, 17, 45 }, // 101
				{ 400, 17, 45 }, // 102
				{ 400, 17, 45 }, // 103
				{ 400, 17, 45 }, // 104
				{ 405, 17, 45 } // 105
		};

		int[][] mnk_table = { { 220, 45, 100 }, // 1-50
				{ 245, 35, 80 }, // 51
				{ 245, 35, 80 }, // 52
				{ 245, 35, 80 }, // 53
				{ 245, 35, 80 }, // 54
				{ 245, 35, 80 }, // 55
				{ 285, 23, 65 }, // 56
				{ 285, 23, 65 }, // 57
				{ 285, 23, 65 }, // 58
				{ 285, 23, 65 }, // 59
				{ 290, 21, 60 }, // 60
				{ 290, 21, 60 }, // 61
				{ 290, 21, 60 }, // 62
				{ 295, 19, 55 }, // 63
				{ 295, 19, 55 }, // 64
				{ 300, 17, 50 }, // 65
				{ 300, 17, 50 }, // 66
				{ 310, 17, 50 }, // 67
				{ 310, 17, 50 }, // 68
				{ 310, 17, 50 }, // 69
				{ 320, 17, 50 }, // 70
				{ 320, 17, 50 }, // 71
				{ 325, 15, 45 }, // 72
				{ 325, 15, 45 }, // 73
				{ 325, 15, 45 }, // 74
				{ 330, 15, 45 }, // 75
				{ 330, 15, 45 }, // 76
				{ 335, 15, 40 }, // 77
				{ 335, 15, 40 }, // 78
				{ 335, 15, 40 }, // 79
				{ 345, 15, 40 }, // 80
				{ 345, 15, 40 }, // 81
				{ 355, 15, 40 }, // 82
				{ 355, 15, 40 }, // 83
				{ 355, 15, 40 }, // 84
				{ 365, 15, 40 }, // 85
				{ 365, 15, 40 }, // 86
				{ 375, 15, 40 }, // 87
				{ 375, 15, 40 }, // 88
				{ 375, 15, 40 }, // 89
				{ 385, 15, 40 }, // 90
				{ 385, 15, 40 }, // 91
				{ 390, 15, 40 }, // 92
				{ 390, 15, 40 }, // 93
				{ 390, 15, 40 }, // 94
				{ 395, 15, 40 }, // 95
				{ 395, 15, 40 }, // 96
				{ 400, 15, 40 }, // 97
				{ 400, 15, 40 }, // 98
				{ 400, 15, 40 }, // 99
				{ 405, 15, 40 }, // 100
				{ 405, 15, 40 }, // 101
				{ 410, 15, 40 }, // 102
				{ 410, 15, 40 }, // 103
				{ 410, 15, 40 }, // 104
				{ 415, 15, 40 }, // 105
		};

		boolean monk = false;
		boolean melee = false;

		if (getClassObj() != null) {
			monk = getClassObj().getName().equals("MONK");
			melee = getClassObj().isWarriorClass();
		}

		// tables caped at 105 for now -- future proofed for a while at least :P
		
		int maxLevel = 70;
		try
		{
			maxLevel = StateManager.getInstance().getConfigurationManager().getMaxLevel();
		} catch (CoreStateInitException e)
		{
			
		}
		
		int level = Math.min(getMentorLevel(), maxLevel);

		if (!melee || (!monk && level < 51))
			return dmg_table[0];

		if (monk && level < 51)
			return mnk_table[0];

		int[][] which = monk ? mnk_table : dmg_table;
		return which[level - 50];
	}

	@Override
	public int getTotalItemStat(StatType stat) {
		int total = 0;

		try {
			List<ISoliniaItem> items = new ArrayList<ISoliniaItem>();

			if (!stat.equals(StatType.Stamina))
				items = getEquippedSoliniaItems();
			else
				items = getEquippedSoliniaItems(true);

			boolean allowNpc = false;
			// only allow hp from gear if heroic/raid
			if (this.isNPC())
			{
				ISoliniaNPC npc = this.getNPC();
				if (npc.isHeroic() || npc.isRaidboss() || npc.isRaidheroic())
					allowNpc = true;
			}
			if  (this.IsCorePet())
				allowNpc = true;
			
			if (isPlayer() || allowNpc)
			for (ISoliniaItem item : items) {
				switch (stat) {
				case Strength:
					if (item.getStrength() > 0) {
						total += item.getStrength();
					}
					break;
				case Stamina:
					if (item.getStamina() > 0) {
						total += item.getStamina();
					}
					break;
				case Agility:
					if (item.getAgility() > 0) {
						total += item.getAgility();
					}
					break;
				case Dexterity:
					if (item.getDexterity() > 0) {
						total += item.getDexterity();
					}
					break;
				case Intelligence:
					if (item.getIntelligence() > 0) {
						total += item.getIntelligence();
					}
					break;
				case Wisdom:
					if (item.getWisdom() > 0) {
						total += item.getWisdom();
					}
					break;
				case Charisma:
					if (item.getCharisma() > 0) {
						total += item.getCharisma();
					}
					break;
				default:
					break;
				}

			}

			return total;
		} catch (Exception e) {
			e.printStackTrace();
			return total;
		}
	}

	@Override
	public int getMaxItemAttackSpeedPct() {
		int max = 0;

		try {
			List<ISoliniaItem> items = new ArrayList<ISoliniaItem>();
			items = getEquippedSoliniaItems();

			for (ISoliniaItem item : items) {
				if (item.getAttackspeed() > 0 && item.getAttackspeed() > max)
					max = item.getAttackspeed();
			}

			return max;
		} catch (Exception e) {
			e.printStackTrace();
			return max;
		}
	}

	@Override
	public int offense(SkillType skillType) {
		int offense = getSkill(skillType);
		int stat_bonus = getStrength();
		if (skillType.equals(SkillType.Archery) || skillType.equals(SkillType.Throwing))
		{
			stat_bonus = getDexterity();
		}
		
		if (stat_bonus >= 75)
		{
			offense += (2 * stat_bonus - 150) / 3;
		}

		// TODO do ATTK
		int attk = getAtk();
		offense += attk;
		return offense;
	}
	
	@Override
	public int getTotalAtk()
	{
		int attackRating = 0;
		
		int itemBonusesAtk = getItemBonuses(SpellEffectType.ATK) + getItemsRawAttack(); // todo
		int aabonusesAtk = getAABonuses(SpellEffectType.ATK); // todo
		int spellbonusesAtk = getSpellBonuses(SpellEffectType.ATK);
		
		int WornCap = itemBonusesAtk;

		if(this.isPlayer()) {
			
			double attackRatingDbl = Math.floor(((WornCap * 1.342) + (getSkill(SkillType.Offense) * 1.345) + ((getStrength() - 66) * 0.9) + (getPrimarySkillValue() * 2.69)));
			if (attackRatingDbl > Integer.MAX_VALUE)
				attackRatingDbl = Integer.MAX_VALUE;
			
			attackRating = (int)attackRatingDbl;
			attackRating += aabonusesAtk; //+ GroupLeadershipAAOffenseEnhancement();

			if (attackRating < 10)
				attackRating = 10;
		}
		else
			attackRating = getAtk();

		attackRating += spellbonusesAtk;

		return attackRating;
	}

	public int getPrimarySkillValue()
	{
		if (this.getBukkitLivingEntity() == null)
			return 0;
		
		SkillReward skill = ItemStackUtils.getMeleeSkillForItemStack(this.getEquipment().getItemInMainHand());
		return getSkill(skill.getSkillType());
	}

	public int getAtk() {
		// this should really only be happening for npcs
		int attackItemBonuses = 0;
		// todo, item bonuses

		int attackSpellBonsues = 0;

		attackSpellBonsues += getSpellBonuses(SpellEffectType.ATK);

		// TODO, find a place for this base value, possibly on race?
		int ATK = 0;
		if (this.isNPC() && this.getNPC() != null)
		{
			ATK = this.getNPC().getNPCDefaultAtk();
		}
		// this is from the bot code..
		return ATK + attackItemBonuses + attackSpellBonsues; // todo AA bonuses;
	}
	
	@Override
	public void tryIncreaseSkill(SkillType skillType, int amount) {
		if (!isPlayer())
			return;

		try {
			ISoliniaPlayer solplayerReward = SoliniaPlayerAdapter.Adapt((Player) this.getBukkitLivingEntity());
			solplayerReward.tryIncreaseSkill(skillType, amount);
		} catch (CoreStateInitException e) {
			// dont increase skill
		}
	}
	
	@Override
	public int getWeaponDamage(ISoliniaLivingEntity against, ItemStack weaponItemStack, int hate)
	{
		int dmg = 0;
		int banedmg = 0;
		int x = 0;
		
		ISoliniaItem weapon_item = null;
		if (weaponItemStack != null)
		{
			try {
				weapon_item = SoliniaItemAdapter.Adapt(weaponItemStack);
			} catch (SoliniaItemException e) {
				
			} catch (CoreStateInitException e) {
			}
		}

		if (against == null || against.getInvul() || against.getSpecialAbility(SpecialAbility.IMMUNE_MELEE) > 0)
			return 0;

		// check for items being illegally attained
		if (weapon_item != null) {
			if (!this.isNPC() && weapon_item.getMinLevel() > getActualLevel())
				return 0;

			if (!this.isNPC() && !weapon_item.isEquipable(this.getRace(),this.getClassObj()))
				return 0;
		}

		if (against.getSpecialAbility(SpecialAbility.IMMUNE_MELEE_NONMAGICAL) > 0) {
			if (weapon_item != null) {
				// check to see if the weapon is magic
				boolean MagicWeapon = weapon_item.isItemMagical(weaponItemStack) || getAABonuses(SpellEffectType.MagicWeapon) > 0 || getSpellBonuses(SpellEffectType.MagicWeapon) > 0 || getItemBonuses(SpellEffectType.MagicWeapon) > 0;
				if (MagicWeapon) {
					/*TODO RECOmmended Levels
					 * int rec_level = weapon_item->GetItemRecommendedLevel(true);
					if (isPlayer() && GetLevel() < rec_level)
						dmg = CastToClient()->CalcRecommendedLevelBonus(
							GetLevel(), rec_level, weapon_item->GetItemWeaponDamage(true));
					else*/
						dmg = weapon_item.getItemWeaponDamage(true, weaponItemStack);
						
					if (dmg <= 0)
						dmg = 1;
				}
				else {
					return 0;
				}
			}
			else {
				boolean MagicGloves = false;
				if (isPlayer()) {
					try
					{
						ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player) getBukkitLivingEntity());
						if (solPlayer.getHandsItem() > 0) {
							ISoliniaItem gloves = StateManager.getInstance().getConfigurationManager()
									.getItem(solPlayer.getHandsItem());
						
							if (gloves != null)
								MagicGloves = gloves.isItemMagical(null);
						}
					} catch (CoreStateInitException e)
					{
						
					}
				}

				if (getClassObj() != null && getClassObj().getName().equals("MONK") || getClassObj().getName().equals("BEASTLORD")) {
					if (MagicGloves || getMentorLevel() >= 30) {
						dmg = getHandToHandDamage();
						if (hate > 0)
							hate += dmg;
					}
				}
				else if (getOwnerEntity() != null &&
						getMentorLevel() >=
					Utils.PetAttackMagicLevel) { // pets wouldn't actually use this but...
					dmg = 1; // it gives us an idea if we can hit
				}
				else if (MagicGloves || getSpecialAbility(SpecialAbility.SPECATK_MAGICAL)  > 0) {
					dmg = 1;
				}
				else
					return 0;
			}
		}
		else {
			if (weapon_item != null) {
				/*auto rec_level = weapon_item->GetItemRecommendedLevel(true);
				if (isPlayer() && GetLevel() < rec_level) {
					dmg = calcRecommendedLevelBonus(
						getLevel(), rec_level, weapon_item.getItemWeaponDamage(true));
				}
				else {*/
				dmg = weapon_item.getItemWeaponDamage(true, weaponItemStack);
				
				if (dmg <= 0)
					dmg = 1;
			}
			else {
				dmg = getHandToHandDamage();
				if (hate > 0)
					hate += dmg;
			}
		}

		int eledmg = 0;
		if (!(against.getSpecialAbility(SpecialAbility.IMMUNE_MAGIC)  > 0)) {
			if (weapon_item != null && weapon_item.getItemElementalFlag(true, weaponItemStack) > 0)
				// the client actually has the way this is done, it does not appear to check req!
				eledmg = against.resistElementalWeaponDmg(weapon_item, weaponItemStack);
		}

		if (weapon_item != null && 
			(weapon_item.getItemBaneDamageBody(true, weaponItemStack) > 0 || weapon_item.getItemBaneDamageRace(true, weaponItemStack) > 0))
			banedmg = against.checkBaneDamage(weapon_item, weaponItemStack);

		if (against.getSpecialAbility(SpecialAbility.IMMUNE_MELEE_EXCEPT_BANE) > 0) {
			if (banedmg < 1) {
				if (!(getSpecialAbility(SpecialAbility.SPECATK_BANE) > 0))
					return 0;
				else
					return 1;
			}
			else {
				dmg += (banedmg + eledmg);
				if (hate > 0)
					hate += banedmg;
			}
		}
		else {
			dmg += (banedmg + eledmg);
			if (hate > 0)
				hate += banedmg;
		}

		return (int)Math.max(0, dmg);
	}
	
	private int getHandToHandDamage() {
		
		int mnk_dmg[] = { 99,
			4, 4, 4, 4, 5, 5, 5, 5, 5, 6,           // 1-10
			6, 6, 6, 6, 7, 7, 7, 7, 7, 8,           // 11-20
			8, 8, 8, 8, 9, 9, 9, 9, 9, 10,          // 21-30
			10, 10, 10, 10, 11, 11, 11, 11, 11, 12, // 31-40
			12, 12, 12, 12, 13, 13, 13, 13, 13, 14, // 41-50
			14, 14, 14, 14, 14, 14, 14, 14, 14, 14, // 51-60
			14, 14 };                                // 61-62
		int bst_dmg[] = { 99,
			4, 4, 4, 4, 4, 5, 5, 5, 5, 5,        // 1-10
			5, 6, 6, 6, 6, 6, 6, 7, 7, 7,        // 11-20
			7, 7, 7, 8, 8, 8, 8, 8, 8, 9,        // 21-30
			9, 9, 9, 9, 9, 10, 10, 10, 10, 10,   // 31-40
			10, 11, 11, 11, 11, 11, 11, 12, 12 }; // 41-49
		if (getClassObj() != null && getClassObj().getName().equals("MONK")) {
			if (this.getMentorLevel() > 62)
				return 15;
			return mnk_dmg[this.getMentorLevel()];
		}
		else if (getClassObj() != null && getClassObj().getName().equals("BEASTLORD")) {
			if (this.getMentorLevel() > 49)
				return 13;
			return bst_dmg[this.getMentorLevel()];
		}
		return 2;
	}

	@Override
	public int resistElementalWeaponDmg(ISoliniaItem weapon_item, ItemStack weaponItemStack) {
		if (weapon_item == null)
			return 0;
		
		int magic = 0, fire = 0, cold = 0, poison = 0, disease = 0, chromatic = 0, prismatic = 0, physical = 0,
		    corruption = 0;
		int resist = 0;
		int roll = 0;
		/*  this is how the client does the resist rolls for these.
		 *  Given the difficulty of parsing out these resists, I'll trust the client
		 */
		if (weapon_item.getItemElementalDamage(magic, fire, cold, poison, disease, chromatic, prismatic, physical, corruption, true, weaponItemStack) > 0) 
		{
			if (magic > 0) {
				resist = this.getResists(SpellResistType.RESIST_MAGIC);
				if (resist >= 201) {
					magic = 0;
				} else {
					roll = MathUtils.RandomBetween(0, 200) - resist;
					if (roll < 1)
						magic = 0;
					else if (roll < 100)
						magic = magic * roll / 100;
				}
			}

			if (fire > 0) {
				resist = this.getResists(SpellResistType.RESIST_FIRE);
				if (resist >= 201) {
					fire = 0;
				} else {
					roll = MathUtils.RandomBetween(0, 200) - resist;
					if (roll < 1)
						fire = 0;
					else if (roll < 100)
						fire = fire * roll / 100;
				}
			}

			if (cold > 0) {
				resist = this.getResists(SpellResistType.RESIST_COLD);
				if (resist >= 201) {
					cold = 0;
				} else {
					roll = MathUtils.RandomBetween(0, 200) - resist;
					if (roll < 1)
						cold = 0;
					else if (roll < 100)
						cold = cold * roll / 100;
				}
			}

			if (poison > 0) {
				resist = this.getResists(SpellResistType.RESIST_POISON);
				if (resist >= 201) {
					poison = 0;
				} else {
					roll = MathUtils.RandomBetween(0, 200) - resist;
					if (roll < 1)
						poison = 0;
					else if (roll < 100)
						poison = poison * roll / 100;
				}
			}

			if (disease > 0) {
				resist = this.getResists(SpellResistType.RESIST_DISEASE);
				if (resist >= 201) {
					disease = 0;
				} else {
					roll = MathUtils.RandomBetween(0, 200) - resist;
					if (roll < 1)
						disease = 0;
					else if (roll < 100)
						disease = disease * roll / 100;
				}
			}

			if (corruption > 0) {
				resist = this.getResists(SpellResistType.RESIST_CORRUPTION);
				if (resist >= 201) {
					corruption = 0;
				} else {
					roll = MathUtils.RandomBetween(0, 200) - resist;
					if (roll < 1)
						corruption = 0;
					else if (roll < 100)
						corruption = corruption * roll / 100;
				}
			}

			if (chromatic > 0) {
				resist = this.getResists(SpellResistType.RESIST_FIRE);
				int temp = this.getResists(SpellResistType.RESIST_COLD);
				if (temp < resist)
					resist = temp;

				temp = this.getResists(SpellResistType.RESIST_MAGIC);
				if (temp < resist)
					resist = temp;

				temp = this.getResists(SpellResistType.RESIST_DISEASE);
				if (temp < resist)
					resist = temp;

				temp = this.getResists(SpellResistType.RESIST_POISON);
				if (temp < resist)
					resist = temp;

				if (resist >= 201) {
					chromatic = 0;
				} else {
					roll = MathUtils.RandomBetween(0, 200) - resist;
					if (roll < 1)
						chromatic = 0;
					else if (roll < 100)
						chromatic = chromatic * roll / 100;
				}
			}

			if (prismatic > 0) {
				resist = (this.getResists(SpellResistType.RESIST_FIRE) + this.getResists(SpellResistType.RESIST_COLD) + this.getResists(SpellResistType.RESIST_MAGIC) + this.getResists(SpellResistType.RESIST_DISEASE) + this.getResists(SpellResistType.RESIST_POISON)) / 5;
				if (resist >= 201) {
					prismatic = 0;
				} else {
					roll = MathUtils.RandomBetween(0, 200) - resist;
					if (roll < 1)
						prismatic = 0;
					else if (roll < 100)
						prismatic = prismatic * roll / 100;
				}
			}

			if (physical > 0) {
				resist = this.getResists(SpellResistType.RESIST_PHYSICAL);
				if (resist >= 201) {
					physical = 0;
				} else {
					roll = MathUtils.RandomBetween(0, 200) - resist;
					if (roll < 1)
						physical = 0;
					else if (roll < 100)
						physical = physical * roll / 100;
				}
			}
		}

		return magic + fire + cold + poison + disease + chromatic + prismatic + physical + corruption;
	}

	@Override
	public int checkBaneDamage(ISoliniaItem item, ItemStack itemStack) {
		if (item == null)
			return 0;

		int damage = item.getItemBaneDamageBody(getBodyType(), true,itemStack);
		damage += item.getItemBaneDamageRace(getRace(), true, itemStack);

		return damage;
	}

	@Override
	public int getBodyType() {
		try {
			if (isPlayer()) {
				ISoliniaPlayer player = SoliniaPlayerAdapter.Adapt((Player) getBukkitLivingEntity());
				if (player.isUndead())
					return BodyType.BT_Undead;
				if (player.isPlant())
					return BodyType.BT_Plant;
				if (player.isAnimal())
					return BodyType.BT_Animal;
				if (player.getRace() == null)
					return BodyType.BT_Humanoid;
				return player.getRace().getBodyType();
			}

			if (isNPC()) {
				ISoliniaNPC npc = getNPC();
				// summoned types
				if (npc.isCorePet())
				{
					if (npc.isUndead())
						return BodyType.BT_SummonedUndead;
					else
						return BodyType.BT_Summoned;
				}
				if (npc.isUndead())
					return BodyType.BT_Undead;
				if (npc.isPlant())
					return BodyType.BT_Plant;
				if (npc.isAnimal())
					return BodyType.BT_Animal;
				if (npc.getRace() == null)
					return BodyType.BT_Humanoid;
				return npc.getRace().getBodyType();
			}
		} catch (CoreStateInitException e) {
			return BodyType.BT_Humanoid;
		}

		return BodyType.BT_Humanoid;
	}

	@Override
	public int getSpecialAbility(int ability) {
		if(ability >= SpecialAbility.MAX_SPECIAL_ATTACK || ability < 0) {
			return 0;
		}
		
		if (!this.isNPC())
			return 0;
		
		ISoliniaNPC npc = this.getNPC();
		if (npc == null)
			return 0;
		
		// TOOD, store special abiltiyies
		//return SpecialAbilities[ability].level;
		switch (ability)
		{
		case SpecialAbility.SPECATK_SUMMON:
			if (npc.isSummoner())
				return 1;
			break;
		case SpecialAbility.SPECATK_ENRAGE:
			if (npc.canEnrage())
				return 1;
			break;
		case SpecialAbility.SPECATK_RAMPAGE:
			if (npc.canRampage())
				return 1;
			break;
		case SpecialAbility.SPECATK_AREA_RAMPAGE:
			if (npc.canAreaRampage())
				return 1;
			break;
		case SpecialAbility.SPECATK_FLURRY:
			if (npc.canFlurry())
				return 1;
			break;
		case SpecialAbility.SPECATK_TRIPLE:
			if (npc.canTriple())
				return 1;
			break;
		case SpecialAbility.SPECATK_QUAD:
			if (npc.canQuad())
				return 1;
			break;
		case SpecialAbility.SPECATK_INNATE_DW:
			break;
		case SpecialAbility.SPECATK_BANE:
			break;
		case SpecialAbility.SPECATK_MAGICAL:
			break;
		case SpecialAbility.SPECATK_RANGED_ATK:
			break;
		case SpecialAbility.UNSLOWABLE:
			break;
		case SpecialAbility.UNMEZABLE:
			break;
		case SpecialAbility.UNCHARMABLE:
			break;
		case SpecialAbility.UNSTUNABLE:
			break;
		case SpecialAbility.UNSNAREABLE:
			break;
		case SpecialAbility.UNFEARABLE:
			break;
		case SpecialAbility.UNDISPELLABLE:
			break;
		case SpecialAbility.IMMUNE_MELEE:
			break;
		case SpecialAbility.IMMUNE_MAGIC:
			break;
		case SpecialAbility.IMMUNE_FLEEING:
			break;
		case SpecialAbility.IMMUNE_MELEE_EXCEPT_BANE:
			break;
		case SpecialAbility.IMMUNE_MELEE_NONMAGICAL:
			break;
		case SpecialAbility.IMMUNE_AGGRO:
			break;
		case SpecialAbility.IMMUNE_AGGRO_ON:
			break;
		case SpecialAbility.IMMUNE_CASTING_FROM_RANGE:
			break;
		case SpecialAbility.IMMUNE_FEIGN_DEATH:
			break;
		case SpecialAbility.IMMUNE_TAUNT:
			break;
		case SpecialAbility.NPC_TUNNELVISION:
			break;
		case SpecialAbility.NPC_NO_BUFFHEAL_FRIENDS:
			break;
		case SpecialAbility.IMMUNE_PACIFY:
			break;
		case SpecialAbility.LEASH:
			break;
		case SpecialAbility.TETHER:
			break;
		case SpecialAbility.DESTRUCTIBLE_OBJECT:
			break;
		case SpecialAbility.NO_HARM_FROM_CLIENT:
			break;
		case SpecialAbility.ALWAYS_FLEE:
			break;
		case SpecialAbility.FLEE_PERCENT:
			break;
		case SpecialAbility.ALLOW_BENEFICIAL:
			break;
		case SpecialAbility.DISABLE_MELEE:
			break;
		case SpecialAbility.NPC_CHASE_DISTANCE:
			break;
		case SpecialAbility.ALLOW_TO_TANK:
			break;
		case SpecialAbility.IGNORE_ROOT_AGGRO_RULES:
			break;
		case SpecialAbility.CASTING_RESIST_DIFF:
			break;
		case SpecialAbility.COUNTER_AVOID_DAMAGE:
			break;
		case SpecialAbility.PROX_AGGRO:
			break;
		case SpecialAbility.IMMUNE_RANGED_ATTACKS:
			break;
		case SpecialAbility.MAX_SPECIAL_ATTACK:
			break;
		}
		return 0;
	}

	@Override
	public boolean getInvul() {
		return this.isInvulnerable();
	}

	@Override
	public int getWeaponDamageBonus(ItemStack itemStack) {
		int level = getMentorLevel();
		if (itemStack == null)
			return 1 + ((level - 28) / 3);

		// TODO include weapon delays
		int delay = 30;
		try {
			ISoliniaItem item = SoliniaItemAdapter.Adapt(itemStack);
			delay = item.getWeaponDelay();
		} catch (SoliniaItemException | CoreStateInitException e) {
			// continue on
		}

		// 1hand weapons
		// we assume sinister strikes is checked before calling here
		if (delay <= 39)
			return 1 + ((level - 28) / 3);
		else if (delay < 43)
			return 2 + ((level - 28) / 3) + ((delay - 40) / 3);
		else if (delay < 45)
			return 3 + ((level - 28) / 3) + ((delay - 40) / 3);
		else if (delay >= 45)
			return 4 + ((level - 28) / 3) + ((delay - 40) / 3);

		// TODO do 2hand items

		return 0;
	}

	private boolean isFeigned() {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean isInulvnerable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public LivingEntity getBukkitLivingEntity() {
		// TODO Auto-generated method stub
		return this.livingentity;
	}
	
	@Override
	public int getSkill(SkillType skilltype) {
		int defaultskill = 0;
		
		if (!SkillUtils.isValidSkill(skilltype.name().toUpperCase()))
			return 0;

		try {
			if (isPlayer()) {
				ISoliniaPlayer player = SoliniaPlayerAdapter.Adapt((Player) getBukkitLivingEntity());
				return player.getSkill(skilltype).getValue();
			}

			if (isNPC()) {
				ISoliniaNPC npc = getNPC();
				return npc.getSkill(skilltype);
			}
		} catch (CoreStateInitException e) {
			return defaultskill;
		}

		return defaultskill;
	}

	@Override
	public int computeToHit(SkillType skillType){
		double tohit = getSkill(SkillType.Offense) + 7;
		tohit += getSkill(skillType);
		if (isNPC()) {
			ISoliniaNPC npc = getNPC();
			if (npc != null)
				tohit += npc.getNPCDefaultAccuracyRating();
		}
		if (isPlayer()) {
			double reduction = getIntoxication() / 2.0;
			if (reduction > 20.0) {
				reduction = Math.min((110 - reduction) / 100.0, 1.0);
				tohit = reduction * (double) (tohit);
			} else if (isBerserk()) {
				tohit += (getMentorLevel() * 2) / 5;
			}
		}

		return (int) Math.max(tohit, 1);
	}

	private double getIntoxication() {
		// TODO - Drinking increases intoxication
		return 0;
	}

	@Override
	public boolean isBerserk() {
		// if less than 10% health and warrior, is in berserk mode
		if (this.getBukkitLivingEntity()
				.getHealth() < ((this.getBukkitLivingEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()
						/ 100) * 36))
			if (this.getClassObj() != null) {
				if (this.getClassObj().getName().equals("WARRIOR") || this.getClassObj().getName().equals("BERSERKER"))
					return true;
			}

		return false;
	}

	@Override
	public int getTotalToHit(SkillType skillType, int hitChanceBonus) {
		if (hitChanceBonus >= 10000) // override for stuff like SE_SkillAttack
			return -1;

		// calculate attacker's accuracy
		double accuracy = computeToHit(skillType) + 10;
		if (hitChanceBonus > 0) // multiplier
			accuracy *= hitChanceBonus;

		accuracy = (accuracy * 121) / 100;

		if (!skillType.equals(SkillType.Archery) && !skillType.equals(SkillType.Throwing)) 
		{ 
			accuracy += getItemBonuses(SpellEffectType.HitChance); 
		}

		// TODO
		double accuracySkill = 0;
		accuracy += accuracySkill;

		// TODO
		double buffItemAndAABonus = 0;
		buffItemAndAABonus += getSpellBonuses(SpellEffectType.HitChance);

		accuracy = (accuracy * (100 + buffItemAndAABonus)) / 100;
		return (int) Math.floor(accuracy);
	}

	@Override
	public int computeDefense() {
		double defense = getSkill(SkillType.Defense) * 400 / 225;
		defense += (8000 * (getAgility() - 40)) / 36000;

		// TODO Item bonsues
		// defense += itembonuses.AvoidMeleeChance; // item mod2
		if (isNPC()) {
			ISoliniaNPC npc = getNPC();
			defense += npc.getAvoidanceRating();
		}

		if (isPlayer()) {
			double reduction = getIntoxication() / 2.0;
			if (reduction > 20.0) {
				reduction = Math.min((110 - reduction) / 100.0, 1.0);
				defense = reduction * (double) (defense);
			}
		}

		return (int) Math.max(1, defense);
	}

	@Override
	public int getSpellBonuses(SpellEffectType spellEffectType) {
		return getSpellBonusesTuple(spellEffectType).a();
	}
	
	@Override
	public int getSpellBonuses(SpellEffectType spellEffectType, Integer base2filter) {
		return getSpellBonusesTuple(spellEffectType, base2filter).a();
	}
	
	@Override
	public int getResistCap(SpellResistType type) {
		int baseMaxStat = 255;
		
		int filter = -1;
		
		switch(type)
		{
		case RESIST_MAGIC: // mr
			filter = 7;
			break;
		case RESIST_COLD: // cr
			filter = 8;
			break;
		case RESIST_FIRE: // fr
			filter = 9;
			break;
		case RESIST_POISON: // pr
			filter = 10;
			break;
		case RESIST_DISEASE: // dr
			filter = 11;
			break;
		case RESIST_CORRUPTION: // corruption
			filter = 12;
			break;
		default:
			break;
		}
		
		int bonuses = 0;
		if (filter > -1)
		{
			bonuses = this.getAABonuses(SpellEffectType.RaiseStatCap, filter) + this.getSpellBonuses(SpellEffectType.RaiseStatCap, filter);
			bonuses += this.getAABonuses(SpellEffectType.RaiseStatCap,13); // all
		}

		if (bonuses == 0) {
			return baseMaxStat;
		} else {
			return baseMaxStat+bonuses;
		}
	}
	
	@Override
	public Tuple<Integer,Integer> getSpellBonusesTuple(SpellEffectType spellEffectType) {
		return getSpellBonusesTuple(spellEffectType,null);
	}
	
	// In EQEmu this gets done only once (ApplySpellsBonuses/NegateSpellBonuses), for now we should just work around it
	@Override
	public Tuple<Integer,Integer> getSpellBonusesTuple(SpellEffectType spellEffectType, Integer base2filter) {
		int bonus = 0;
		int bonus2 = 0;
		for (ActiveSpellEffect effect : SpellUtils.getActiveSpellEffects(getBukkitLivingEntity(), spellEffectType)) {
			Entity sentity = Bukkit.getEntity(effect.getSourceEntityUUID());
			Entity tentity = Bukkit.getEntity(effect.getTargetEntityUUID());
			
			if (sentity == null || tentity == null)
				continue;
			
			if (
					!(sentity instanceof LivingEntity) ||
					!(sentity instanceof LivingEntity)
					)
				continue;
			
			if (base2filter != null && effect.getBase2() != base2filter)
				continue;
			
			Tuple<Integer,Integer> spellBonuses = ApplySpellsBonuses(effect.getSpellId(), (LivingEntity)sentity, (LivingEntity)tentity,effect,effect.getSourceLevel(),effect.getTicksLeft(),effect.getInstrumentMod());
			
			bonus += spellBonuses.a();
			bonus2 += spellBonuses.b();
		}

		return new Tuple<Integer,Integer>(bonus,bonus2);
	}
	
	
	
	private Tuple<Integer,Integer> ApplySpellsBonuses(int spellId, LivingEntity sourceEntity, LivingEntity targetEntity, SpellEffect effect, int sourceLevel, int ticksleft, int instrument_mod) {
		int newbonus = 0;
		int newbonus2 = 0;
		try
		{
			ISoliniaSpell spell = StateManager.getInstance().getConfigurationManager().getSpell(spellId);
			
			int effectid = effect.getSpellEffectId();
			int effect_value = spell.calcSpellEffectValue(effect, sourceEntity, targetEntity, sourceLevel, ticksleft, instrument_mod); // this is remaining
			int base2 = effect.getBase2();
			int max = effect.getMax();
		
			switch(effect.getSpellEffectType())
			{
				case AttackSpeed:
				{
					if ((effect_value - 100) > 0) { // Haste
						if (newbonus < 0) break; // Slowed - Don't apply haste
						if ((effect_value - 100) > newbonus) {
							newbonus = effect_value - 100;
						}
					}
				else if ((effect_value - 100) < 0) { // Slow
						int real_slow_value = (100 - effect_value) * -1;
						real_slow_value -= ((real_slow_value * GetSlowMitigation()/100));
						if (real_slow_value < newbonus)
							newbonus = real_slow_value;
				}
				break;
			}
	
			case AttackSpeed2:
			{
				if ((effect_value - 100) > 0) { // Haste V2 - Stacks with V1 but does not Overcap
					if (newbonus < 0) break; //Slowed - Don't apply haste2
					if ((effect_value - 100) > newbonus) {
						newbonus = effect_value - 100;
					}
				}
				else if ((effect_value - 100) < 0) { // Slow
					int real_slow_value = (100 - effect_value) * -1;
					real_slow_value -= ((real_slow_value * GetSlowMitigation()/100));
					if (real_slow_value < newbonus)
						newbonus = real_slow_value;
				}
				break;
			}
	
			case AttackSpeed3:
			{
				if (effect_value < 0){ //Slow
					effect_value -= ((effect_value * GetSlowMitigation()/100));
					if (effect_value < newbonus)
						newbonus = effect_value;
				}
	
				else if (effect_value > 0) { // Haste V3 - Stacks and Overcaps
					if (effect_value > newbonus) {
						newbonus = effect_value;
					}
				}
				break;
			}
	
			case AttackSpeed4:
			{
				// These don't generate the IMMUNE_ATKSPEED message and the icon shows up
				// but have no effect on the mobs attack speed
				if (getSpecialAbility(SpecialAbility.UNSLOWABLE) > 0)
					break;
	
				if (effect_value < 0) //A few spells use negative values(Descriptions all indicate it should be a slow)
					effect_value = effect_value * -1;
	
				if (effect_value > 0 && effect_value > newbonus) {
					effect_value -= ((effect_value * GetSlowMitigation()/100));
					if (effect_value > newbonus)
						newbonus = effect_value;
				}
	
				break;
			}
			default:
				newbonus = spell.calcSpellEffectValue(effect, sourceEntity, targetEntity, sourceLevel, ticksleft, instrument_mod); // this is remaining
				newbonus2 = effect.getBase2();
				break;
			}
		
		} catch (CoreStateInitException e)
		{
			
		}
		return new Tuple<Integer,Integer>(newbonus,newbonus2);
	}

	private int GetSlowMitigation() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getHaste() {
		return CalcHaste();
	}
	
	@Override
	public int CalcHaste()
	{
		/*  Tests: (based on results in newer char window)
		    68 v1 + 46 item + 25 over + 35 inhib = 204%
		    46 item + 5 v2 + 25 over + 35 inhib = 65%
		    68 v1 + 46 item + 5 v2 + 25 over + 35 inhib = 209%
		    75% slow + 35 inhib = 25%
		    35 inhib = 65%
		    75% slow = 25%
		    Conclusions:
		    the bigger effect in slow v. inhib wins
		    slow negates all other hastes
		    inhib will only negate all other hastes if you don't have v1 (ex. VQ)
		*/
		// slow beats all! Besides a better inhibit
		int Haste = 0;
		
		int itemHardSpeed = getMaxItemAttackSpeedPct();
		if (itemHardSpeed > 100)
			itemHardSpeed = itemHardSpeed - 100;
		
		int itembonusesHaste = getItemBonuses(SpellEffectType.AttackSpeed)+itemHardSpeed;
		int spellbonusesHaste = getSpellBonuses(SpellEffectType.AttackSpeed);
		int spellbonusesHaste2 = getSpellBonuses(SpellEffectType.AttackSpeed2);
		int spellbonusesHaste3 = getSpellBonuses(SpellEffectType.AttackSpeed3);
		int spellbonusesInhibitMelee = getSpellBonuses(SpellEffectType.AttackSpeed4);
		
		if (spellbonusesHaste < 0) {
			if (-spellbonusesHaste <= spellbonusesInhibitMelee) {
				Haste = 100 - spellbonusesInhibitMelee;
			}
			else {
				Haste = 100 + spellbonusesHaste;
			}
			return Haste;
		}
		// No haste and inhibit, kills all other hastes
		if (spellbonusesHaste == 0 && spellbonusesInhibitMelee > 0) {
			Haste = 100 - spellbonusesInhibitMelee;
			return Haste;
		}
		int h = 0;
		int cap = 0;
		int level = getMentorLevel();
		// we know we have a haste spell and not slowed, no extra inhibit melee checks needed
		if (spellbonusesHaste > 0) {
			h += spellbonusesHaste - spellbonusesInhibitMelee;
		}
		if (spellbonusesHaste2 > 0 && level > 49) { // type 2 is capped at 10% and only available to 50+
			h += spellbonusesHaste2 > 10 ? 10 : spellbonusesHaste2;
		}
		// 26+ no cap, 1-25 10
		if (level > 25) { // 26+
			h += itembonusesHaste;
		}
		else {   // 1-25
			h += itembonusesHaste > 10 ? 10 : itembonusesHaste;
		}
		// 60+ 100, 51-59 85, 1-50 level+25
		if (level > 59) { // 60+
			cap = Utils.HASTE_CAP;
		}
		else if (level > 50) {  // 51-59
			cap = 85;
		}
		else {   // 1-50
			cap = level + 25;
		}
		
		//cap = mod_client_haste_cap(cap);
		if (h > cap) {
			h = cap;
		}
		// 51+ 25 (despite there being higher spells...), 1-50 10
		if (level > 50) { // 51+
			h += spellbonusesHaste3 > 25 ? 25 : spellbonusesHaste3;
		}
		else {   // 1-50
			h += spellbonusesHaste3 > 10 ? 10 : spellbonusesHaste3;
		}
		//h += ExtraHaste;	//GM granted haste.
		//h = mod_client_haste(h);
		Haste = 100 + h;
		return Haste;
	}

	@Override
	public int getTotalDefense() {
		double avoidance = computeDefense() + 10;

		int evasion_bonus = getSpellBonuses(SpellEffectType.AvoidMeleeChance);

		// if (evasion_bonus >= * 10000) return -1;

		double aaItemAAAvoidance = 0;

		// Todo item bonuses
		// aaItemAAAvoidance += itembonuses.AvoidMeleeChanceEffect +
		// aabonuses.AvoidMeleeChanceEffect; // item bonus here isn't mod2 avoidance

		// Evasion is a percentage bonus according to AA descriptions
		if (evasion_bonus > 0)
			avoidance = (avoidance * (100 + aaItemAAAvoidance)) / 100;

		return (int) Math.floor(avoidance);
	}

	@Override
	public boolean isUndead() {
		if (this.getRace() != null)
			if (this.getRace().isUndead())
				return true;

		if (isPlayer())
		{
			ISoliniaPlayer solPlayer = this.getPlayer();
			if (solPlayer != null)
				return solPlayer.isUndead();
			return false;
		}

		if (this.getNpcid() < 1)
			return false;

		ISoliniaNPC npc = getNPC();
		return npc.isUndead();
	}

	@Override
	public boolean isPlant() {
		if (this.getRace() != null)
			if (this.getRace().isPlant())
				return true;
		
		if (isPlayer())
		{
			ISoliniaPlayer solPlayer = this.getPlayer();
			if (solPlayer != null)
				return solPlayer.isPlant();
			return false;
		}

		if (this.getNpcid() < 1)
			return false;

		ISoliniaNPC npc = getNPC();
		return npc.isPlant();
	}

	@Override
	public boolean isAnimal() {
		if (this.getRace() != null)
			if (this.getRace().isAnimal())
				return true;

		if (isPlayer())
		{
			ISoliniaPlayer solPlayer = this.getPlayer();
			if (solPlayer != null)
				return solPlayer.isAnimal();
			return false;
		}

		if (this.getNpcid() < 1)
			return false;

		ISoliniaNPC npc = getNPC();
		return npc.isAnimal();
	}

	@Override
	public int getEffectiveLevel(boolean forSpells) {
		if (isPlayer()) {
			try {
				ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player) this.getBukkitLivingEntity());
				
				if (!forSpells)
				{
					return solPlayer.getMentorLevel();
				} else {
					int level = solPlayer.getMentorLevel();
					level += this.getAABonuses(SpellEffectType.CastingLevel) + this.getSpellBonuses(SpellEffectType.CastingLevel) + this.getItemBonuses(SpellEffectType.CastingLevel);
					return level;
					
				}
			} catch (CoreStateInitException e) {
				return 0;
			}
		}

		if (this.IsCorePet())
		{
			try {
				int petFocus = StateManager.getInstance().getEntityManager().getPetFocus(this.getBukkitLivingEntity().getUniqueId());
				int levelBonus = (int) Math.floor(petFocus / 2);
				return this.getActualLevel()+levelBonus;
			} catch (CoreStateInitException e) {
				return this.getActualLevel();
			}
		}
		
		return this.getActualLevel();
	}

	
	@Override
	public int getActualLevel() {
		if (this.isPlayer() && this.getPlayer() != null)
			return this.getPlayer().getActualLevel();
		
		return this.actualLevel;
	}
	
	@Override
	public int getMentorLevel() 
	{
		if (this.isPlayer() && this.getPlayer() != null)
			return this.getPlayer().getMentorLevel();
		
		return this.actualLevel;
	}

	@Override
	public void dropLoot() {
		if (isPlayer())
			return;

		try {
			if (getNpcid() > 0) {
				ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(getNpcid());
				if (npc.getLoottableid() == 0)
					return;
				
				DropUtils.DropLoot(npc.getLoottableid(),this.getBukkitLivingEntity().getWorld(),this.getBukkitLivingEntity().getLocation(),"",0);
				if (npc.getRace() != null)
					if (npc.getRace().getRaceLootTableId() > 0)
						DropUtils.DropLoot(npc.getRace().getRaceLootTableId(),this.getBukkitLivingEntity().getWorld(),this.getBukkitLivingEntity().getLocation(),"",0);
				if (npc.getClassObj() != null)
					if (npc.getClassObj().getDropSpellsLootTableId() > 0)
						DropUtils.DropLoot(npc.getClassObj().getDropSpellsLootTableId(),this.getBukkitLivingEntity().getWorld(),this.getBukkitLivingEntity().getLocation(),npc.getClassObj().getName().toUpperCase(),getMentorLevel());
				
				SoliniaWorld world = StateManager.getInstance().getConfigurationManager().getWorld(this.getBukkitLivingEntity().getWorld().getName());
				if (world != null)
				{
					if (world.getGlobalLootTableId() > 0)
						DropUtils.DropLoot(world.getGlobalLootTableId(),this.getBukkitLivingEntity().getWorld(),this.getBukkitLivingEntity().getLocation(),"",0);

					if (world.getGlobalLevelLootTableId() > 0)
						DropUtils.DropLoot(world.getGlobalLevelLootTableId(),this.getBukkitLivingEntity().getWorld(),this.getBukkitLivingEntity().getLocation(),"",getMentorLevel());
				}
						
			}
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public int getNpcid() {
		return npcid;
	}
	
	@Override
	public ISoliniaNPC getNPC()
	{
		try
		{
			return StateManager.getInstance().getConfigurationManager().getNPC(this.getNpcid());
		} catch (CoreStateInitException e)
		{
			return null;
		}
	}
	
	@Override
	public ISoliniaPlayer getPlayer()
	{
		if (this.getBukkitLivingEntity() == null)
			return null;
		
		if (!(this.getBukkitLivingEntity() instanceof Player))
			return null;
		
		try
		{
			return SoliniaPlayerAdapter.Adapt((Player)this.getBukkitLivingEntity());
		} catch (CoreStateInitException e)
		{
			return null;
		}
	}

	@Override
	public void setNpcid(int npcid) {
		this.npcid = npcid;
	}

	@Override
	public boolean isPlayer() {
		if (this.getBukkitLivingEntity() == null)
			return false;

		if (this.getBukkitLivingEntity() instanceof Player)
			return true;

		return false;
	}

	@Override
	public void emote(String message) {
		if (getBukkitLivingEntity() == null)
			return;
		
		if (this.isNPC())
		System.out.println("NPCEMOTE|"+message);
		StateManager.getInstance().getChannelManager().sendToLocalChannel(this, ChatColor.AQUA + "<LC> * " + message,
				getEquipment().getItemInMainHand());
	}

	@Override
	public void doRandomChat() {
		if (isPlayer())
			return;

		if (this.getNpcid() < 1)
			return;

		ISoliniaNPC npc = getNPC();
		if (npc.getRandomchatTriggerText() == null || npc.getRandomchatTriggerText().equals(""))
			return;

		// 2% chance of saying something
		int random = MathUtils.RandomBetween(1, 100);
		if (random < 2) {
			this.say(npc.getRandomchatTriggerText());
		}
	}

	@Override
	public void say(String message) {
		if (getBukkitLivingEntity() == null)
			return;

		if (isPlayer())
			return;

		if (this.getNpcid() < 1)
			return;

		ISoliniaNPC npc = getNPC();
		if (npc == null)
			return;

		System.out.println("NPCSAY|"+npc.getName() + " says '" + message);
		String decoratedMessage = ChatColor.AQUA + npc.getName() + " says '" + message + "'" + ChatColor.RESET;
		StateManager.getInstance().getChannelManager().sendToLocalChannelLivingEntityChat(this, decoratedMessage,
				true, message, getEquipment().getItemInMainHand());
	}

	@Override
	public void sayto(Player player, String message, boolean allowlanguagelearn) {
		if (isPlayer())
			return;

		if (this.getNpcid() < 1)
			return;
		
		if (this.getBukkitLivingEntity() == null)
			return;

		try {
			ISoliniaNPC npc = getNPC();
			if (npc == null)
				return;
			
			if (player.isOp() || (getLanguage() == null || getLanguage().equals(SkillType.UnknownTongue) || getLanguage().equals(SkillType.None) || isSpeaksAllLanguages()
					|| SoliniaPlayerAdapter.Adapt(player).understandsLanguage(getLanguage()))) {
				String decoratedMessage = ChatColor.AQUA + npc.getName() + " says to " + player.getCustomName() + " '" + message
						+ "'"  + " [" + getLanguage() + "]" + ChatColor.RESET;
				player.sendMessage(decoratedMessage);
			} else {
				String decoratedMessage = ChatColor.AQUA + npc.getName() + " says to " + player.getCustomName() + " '" + ChatUtils.garbleText(message,SoliniaPlayerAdapter.Adapt(player).getLanguageLearnedPercent(getLanguage()))
						+ "' (You do not fully understand this language)" + ChatColor.RESET;
				player.sendMessage(decoratedMessage);

				if (allowlanguagelearn == true) {
					if (getLanguage() != null && !getLanguage().equals(SkillType.None))
						SoliniaPlayerAdapter.Adapt(player).tryImproveLanguage(getLanguage());
				}
			}

		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void sayto(Player player, String message) {
		if (isPlayer())
			return;

		if (this.getNpcid() < 1)
			return;

		try {
			ISoliniaNPC npc = getNPC();
			if (npc == null)
				return;

			if (player.isOp() || (getLanguage() == null || getLanguage().equals(SkillType.UnknownTongue) ||getLanguage().equals(SkillType.None) || isSpeaksAllLanguages()
					|| SoliniaPlayerAdapter.Adapt(player).understandsLanguage(getLanguage()))) {
				String decoratedMessage = ChatColor.AQUA + npc.getName() + " says '" + message + "'"  + " [" + getLanguage() + "]" + ChatColor.RESET;
				player.sendMessage(decoratedMessage);
			} else {
				String decoratedMessage = ChatColor.AQUA + npc.getName() + " says '" + ChatUtils.garbleText(message,SoliniaPlayerAdapter.Adapt(player).getLanguageLearnedPercent(getLanguage())) + "' (You do not fully understand this language)" + ChatColor.RESET;
				player.sendMessage(decoratedMessage);

				if (getLanguage() != null && !getLanguage().equals(SkillType.None))
					SoliniaPlayerAdapter.Adapt(player).tryImproveLanguage(getLanguage());
			}
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public double getHPRatio() {
		return getBukkitLivingEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() == 0 ? 0
				: (getBukkitLivingEntity().getHealth()
						/ getBukkitLivingEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * 100);
	}

	@Override
	public double getManaRatio() {
		if (getMaxMP() == 0)
		{
			return 0D;
		}
		
		return ((double)getMana() / (double)getMaxMP()) * 100D;
	}

	@Override
	public void say(String message, LivingEntity messageto, boolean allowlanguagelearn) {
		if (isPlayer())
			return;

		if (this.getNpcid() < 1)
			return;

		try {
			ISoliniaNPC npc = getNPC();
			if (npc == null)
				return;
			
			String name = messageto.getName();
			ISoliniaLivingEntity solLivingEntity = SoliniaLivingEntityAdapter.Adapt(messageto);
			if (solLivingEntity != null)
				name = solLivingEntity.getName();
			

			String decoratedMessage = ChatColor.AQUA + npc.getName() + " says to " + name + " '"
					+ message + "'" + ChatColor.RESET;
			StateManager.getInstance().getChannelManager().sendToLocalChannelLivingEntityChat(this, decoratedMessage,
					allowlanguagelearn, message, getEquipment().getItemInMainHand());
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void doSlayChat() {
		if (isPlayer())
			return;

		ISoliniaNPC npc;
		try {
			npc = StateManager.getInstance().getConfigurationManager().getNPC(this.getNpcid());
			if (npc.getKillTriggerText() == null || npc.getKillTriggerText().equals(""))
				return;

			this.emote(npc.getName() + " says '" + npc.getKillTriggerText() + "'" + ChatColor.RESET);
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private boolean AI_IdleCastCheck() {
		if (isPlayer())
			return false;

		if (!isNPC())
			return false;

		if (this.getBukkitLivingEntity() == null)
			return false;

		ISoliniaNPC npc;
		try {
			npc = StateManager.getInstance().getConfigurationManager().getNPC(this.getNpcid());
			if (npc.getClassid() < 1)
				return false;
			
			if (!aiCastSpell(StateManager.getInstance().getPlugin(),npc,this.getBukkitLivingEntity(), Utils.AI_IdleBeneficialChance, SpellType.Heal | SpellType.Buff | SpellType.Pet,this.getEffectiveLevel(true))) {
				/* - TODO , Ally buffs
				if(!entity_list.AICheckCloseBeneficialSpells(this, 33, MobAISpellRange, SpellType_Heal | SpellType_Buff)) {
					//if we didnt cast any spells, our autocast timer just resets to the
					//last duration it was set to... try to put up a more reasonable timer...
					AIautocastspell_timer->Start(RandomTimer(AISpellVar.idle_no_sp_recast_min, AISpellVar.idle_no_sp_recast_max), false);

					Log(Logs::Moderate, Logs::Spells, "Triggering AI_IdleCastCheck :: Mob %s - Min : %u Max : %u", this->GetCleanName(), AISpellVar.idle_no_sp_recast_min, AISpellVar.idle_no_sp_recast_max);

				}	//else, spell casting finishing will reset the timer.
				*/
				
				return false;
			} else {
				return(true);
			}
			
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}

	@Override
	public void aiEngagedCastCheck(Plugin plugin, ISoliniaNPC npc, LivingEntity castingAtEntity, int npcEffectiveLevel)
			throws CoreStateInitException {

		if (this.getClassObj() == null)
			return;
		
		int beneficialSelfSpells = SpellType.Heal | SpellType.InCombatBuff | SpellType.Buff;
		int beneficialOtherSpells = SpellType.Heal;
		int detrimentalSpells = SpellType.Nuke | SpellType.Lifetap | SpellType.DOT | SpellType.Dispel | SpellType.Mez
				| SpellType.Slow | SpellType.Debuff | SpellType.Root;

		int engagedBeneficialSelfChance = StateManager.getInstance().getEntityManager()
				.getAIEngagedBeneficialSelfChance();
		int engagedBeneficialOtherChance = StateManager.getInstance().getEntityManager()
				.getAIEngagedBeneficialOtherChance();
		int engagedDetrimentalOtherChance = StateManager.getInstance().getEntityManager()
				.getAIEngagedDetrimentalChance();

		if (npc.isCorePet()) {
			engagedBeneficialSelfChance = 10;
			engagedBeneficialOtherChance = 0;
			engagedDetrimentalOtherChance = 90;
		}
		
		// if already casting abort
		if (this.isCasting())
			return;

		// Try self buff, then nearby then target detrimental
		
		if (!aiCastSpell(plugin, npc, this.getBukkitLivingEntity(), engagedBeneficialSelfChance,beneficialSelfSpells, npcEffectiveLevel)) {
			if (castingAtEntity != null && !aiCheckCloseBeneficialSpells(plugin, npc, engagedBeneficialOtherChance, StateManager.getInstance().getEntityManager().getAIBeneficialBuffSpellRange(),beneficialOtherSpells, npcEffectiveLevel)) {
				if (castingAtEntity != null && !aiCastSpell(plugin, npc, castingAtEntity, engagedDetrimentalOtherChance, detrimentalSpells, npcEffectiveLevel)) {
				}
			}
		}
	}

	@Override
	public boolean aiCheckCloseBeneficialSpells(Plugin plugin, ISoliniaNPC npc, int iChance, int iRange,
			int iSpellTypes, int npcEffectiveLevel) throws CoreStateInitException {
		if (((iSpellTypes & SpellType.Detrimental)) == SpellType.Detrimental) {
			return false;
		}

		if (this.getClassObj() == null) {
			return false;
		}

		if (iChance < 100) {
			if (MathUtils.RandomBetween(0, 99) >= iChance) {
				return false;
			}
		}

		if (npc.getFactionid() == 0) {
			return false;
		}

		// Only iterate through NPCs
		for (Entity nearbyEntity : getBukkitLivingEntity().getNearbyEntities(iRange, iRange, iRange)) {
			if (!(nearbyEntity instanceof LivingEntity))
				continue;

			if (nearbyEntity.isDead())
				continue;

			ISoliniaLivingEntity mob = SoliniaLivingEntityAdapter.Adapt((LivingEntity) nearbyEntity);

			// TODO Reverse faction cons

			if (!mob.isNPC())
				continue;

			ISoliniaNPC mobNpc = StateManager.getInstance().getConfigurationManager().getNPC(mob.getNpcid());

			if (mobNpc.getFactionid() != npc.getFactionid())
				continue;

			if (((iSpellTypes & SpellType.Buff) == SpellType.Buff)) {
				iSpellTypes = SpellType.Heal;
			}

			if (aiCastSpell(plugin, npc, mob.getBukkitLivingEntity(), 100, iSpellTypes, npcEffectiveLevel))
				return true;
		}
		return false;
	}

	@Override
	public boolean aiCastSpell(Plugin plugin, ISoliniaNPC npc, LivingEntity target, int iChance, int iSpellTypes, int npcEffectiveLevel)
			throws CoreStateInitException {
		if (this.getClassObj() == null) {
			return false;
		}

		NPCSpellList npcSpellList = null;

		if (npc.getNpcSpellList() < 1 && this.getClassObj().getNpcspelllist() < 1)
			return false;

		ISoliniaLivingEntity tar = SoliniaLivingEntityAdapter.Adapt(target);
		if (tar == null)
			return false;
		
		if (getClassObj().getNpcspelllist() > 0) {
			npcSpellList = StateManager.getInstance().getConfigurationManager()
					.getNPCSpellList(getClassObj().getNpcspelllist());
		}

		if (npc.getNpcSpellList() > 0) {
			npcSpellList = StateManager.getInstance().getConfigurationManager().getNPCSpellList(npc.getNpcSpellList());
		}
		
		boolean cast_only_option = (isRooted() && !combatRange(tar));

		if (!cast_only_option && iChance < 100) {
			int roll = MathUtils.RandomBetween(0, 100);
			if (roll >= iChance) {
				return false;
			}
		}
		// TODO escape distance

		boolean checked_los = false;

		double manaR = getManaRatio();

		List<NPCSpellListEntry> spells = new ArrayList<NPCSpellListEntry>();
		for (NPCSpellListEntry entry : npcSpellList.getSpellListEntry()) {
			if (npcEffectiveLevel >= entry.getMinlevel() && npcEffectiveLevel <= entry.getMaxlevel()) {
				spells.add(entry);
			}
		}
		
		List<Integer> activeSpellIds = tar.getActiveSpells().stream().map(e -> e.getSpellId()).collect(Collectors.toList());

		spells = spells.stream().filter(e -> !activeSpellIds.contains(e.getSpellid())).collect(Collectors.toList());

		Collections.shuffle(spells);
		
		/*Seems to always cast the same spell so lets just take this out for now
		 * 
		 * Collections.sort(spells, new Comparator<NPCSpellListEntry>() {
			public int compare(NPCSpellListEntry o1, NPCSpellListEntry o2) {
				if (o1.getPriority() == o2.getPriority())
					return 0;

				return o1.getPriority() > o2.getPriority() ? -1 : 1;
			}
		});*/

		
		
		// AI has spells?
		if (spells.size() == 0) {
			return false;
		}

		for (NPCSpellListEntry spelllistentry : spells) {
			if ((iSpellTypes & spelllistentry.getType()) == spelllistentry.getType()) {
				// Does spell types contain spelltype
				ISoliniaSpell spell = StateManager.getInstance().getConfigurationManager().getSpell(spelllistentry.getSpellid());

				if (spell == null)
				{
					continue;
				}
				
				// Lets not cast anything thats really short lasting if we are outside of combat
				if (!this.isInCombat())
				{
					if (!spell.isBeneficial())
						continue;
					
					if (spell.getBuffduration() < 150)
						continue;
					
					boolean noStack = false;
					boolean foundBefore = false;
					// this is just me so lets see if my spell can stack against me
					for (SoliniaActiveSpell activeSpell : StateManager.getInstance().getEntityManager().getActiveEntitySpells(getBukkitLivingEntity()).getActiveSpells())
					{
						if (activeSpell.getSpellId() == spell.getId())
						{
							foundBefore = true;
							continue;
						}
						
						int stackableResponse = StateManager.getInstance().getEntityManager().getActiveEntitySpells(getBukkitLivingEntity()).checkStackConflict(activeSpell.getSpell(),getBukkitLivingEntity().getUniqueId(),spell,getBukkitLivingEntity(),getBukkitLivingEntity());
						if(stackableResponse == -1)
							noStack = true;
					}
					if (foundBefore)
						continue;
					if (noStack)
						continue;
				}
				
				// TODO Check mana
				int mana_cost = spell.getActSpellCost(this);
				if (mana_cost < 0)
					mana_cost = 0;

				ISoliniaLivingEntity soltarget = SoliniaLivingEntityAdapter.Adapt(target);
				LocalDateTime datetime = LocalDateTime.now();
				Timestamp nowtimestamp = Timestamp.valueOf(datetime);
				Timestamp expiretimestamp = Timestamp
						.valueOf(datetime.plus(spell.getRecastTime() + 1000, ChronoUnit.MILLIS));

				switch (spelllistentry.getType()) {
				case SpellType.Heal:
					if ((SoliniaSpell.isValidEffectForEntity(target, this.getBukkitLivingEntity(), spell).a())
							&& !SpellUtils.hasSpellActive(soltarget, spell)
							&& (SpellUtils.getSpellTargetType(spell.getTargettype()).equals(SpellTargetType.Target)
									|| target.getUniqueId().equals(getBukkitLivingEntity().getUniqueId()))
							&& (nowtimestamp.after(StateManager.getInstance().getEntityManager()
									.getDontSpellTypeMeBefore(target, SpellType.Heal)))
							&& !(soltarget.isCurrentlyNPCPet())) {
						double hpr = soltarget.getHPRatio();
						// TODO player healing and non engaged healing of less than 50% hp
						if (hpr <= 35) {
							aiDoSpellCast(plugin, spell, soltarget, mana_cost);
							StateManager.getInstance().getEntityManager().setDontSpellTypeMeBefore(target,
									SpellType.Heal, expiretimestamp);
							return true;
						}
					}
					break;
				case SpellType.Root:
					// TODO - Pick at random
					if ((SoliniaSpell.isValidEffectForEntity(target, this.getBukkitLivingEntity(), spell).a())
							&& !SpellUtils.hasSpellActive(soltarget, spell) && !soltarget.isRooted() && MathUtils.RandomRoll(50)
							&& nowtimestamp.after(StateManager.getInstance().getEntityManager()
									.getDontSpellTypeMeBefore(target, SpellType.Root))
					// TODO buff stacking
					) {
						if (!checked_los) {
							if (!this.checkLosFN(tar)) {
								return false;
							}
							checked_los = true;
						}
						aiDoSpellCast(plugin, spell, soltarget, mana_cost);
						StateManager.getInstance().getEntityManager().setDontSpellTypeMeBefore(target, SpellType.Root,
								expiretimestamp);
						return true;
					}
					break;
				case SpellType.InCombatBuff:
				case SpellType.Buff:
					if (((SoliniaSpell.isValidEffectForEntity(target, this.getBukkitLivingEntity(), spell).a())
							&& !SpellUtils.hasSpellActive(soltarget, spell)
							&& SpellUtils.getSpellTargetType(spell.getTargettype()).equals(SpellTargetType.Target)
							|| target.getUniqueId().equals(getBukkitLivingEntity().getUniqueId()))
							&& nowtimestamp.after(StateManager.getInstance().getEntityManager()
									.getDontSpellTypeMeBefore(target, SpellType.Buff))
							&& !spell.isInvisSpell()
					// TODO Spell immunities
					// TODO Spell stacking
					// TODO NPC Pets
					) {
						if (!checked_los) {
							if (!this.checkLosFN(tar)) {
								return false;
							}
							checked_los = true;
						} else {
						}
						boolean success = aiDoSpellCast(plugin, spell, soltarget, mana_cost);
						StateManager.getInstance().getEntityManager().setDontSpellTypeMeBefore(target, SpellType.Buff,
								expiretimestamp);
						return true;
					} else {
						//could not cast as either the spell target was wrong, the target was not me or i have already buffed myself recently
					}
					break;
				case SpellType.Escape:
					// TODO Gate/Escape
					return false;
				case SpellType.Slow:
				case SpellType.Debuff:
					// TODO debuff at random
					if ((SoliniaSpell.isValidEffectForEntity(target, this.getBukkitLivingEntity(), spell).a())
							&& !SpellUtils.hasSpellActive(soltarget, spell) && manaR >= 10 && MathUtils.RandomRoll(70)
					// TODO buff stacking
					) {
						if (!checked_los) {
							if (!RaycastUtils.isEntityInLineOfSight(this,tar, true)) {
								return false;
							}
							checked_los = true;
						}
						aiDoSpellCast(plugin, spell, soltarget, mana_cost);
						return true;
					}
					break;
				case SpellType.Nuke:
					boolean nukeRoll = MathUtils.RandomRoll(70);
					Tuple<Boolean, String> valid = SoliniaSpell.isValidEffectForEntity(target, this.getBukkitLivingEntity(), spell);
					boolean hasSpellTargetActive = SpellUtils.hasSpellActive(soltarget, spell);
					if (valid.a()
							&& !hasSpellTargetActive && manaR >= 10 && nukeRoll
					// TODO Buff Stacking check
					) {
						if (!checked_los) {
							if (!RaycastUtils.isEntityInLineOfSight(this,tar, true)) {
								return false;
							}
							checked_los = true;
						} else {
						}
						aiDoSpellCast(plugin, spell, soltarget, mana_cost);
						return true;
					} else {
						/*DebugUtils.DebugLog("SoliniaLivingEntity","aiCastSpell",this.getBukkitLivingEntity(),
								"NPC: " + npc.getName() + this.getBukkitLivingEntity().getUniqueId().toString()
										+ " could not cast nuke as either my mana ratio was too low (" + !(manaR >= 10)
										+ ") or i rolled badly roll failure: (" + nukeRoll + ")"
												+ " or was not valid - valid: " + valid.a() + "["+valid.b()+"]"
												+ " or has spell already " + hasSpellTargetActive);*/
					}
					break;
				case SpellType.Dispel:
					if ((SoliniaSpell.isValidEffectForEntity(target, this.getBukkitLivingEntity(), spell).a())
							&& !SpellUtils.hasSpellActive(soltarget, spell) && MathUtils.RandomRoll(15)) {
						if (!checked_los) {
							if (!RaycastUtils.isEntityInLineOfSight(this,tar, true)) {
								return false;
							}
							checked_los = true;
						}
						if (soltarget.countDispellableBuffs() > 0) {
							aiDoSpellCast(plugin, spell, soltarget, mana_cost);
							return true;
						}
					}
					break;
				case SpellType.Mez:
					if ((SoliniaSpell.isValidEffectForEntity(target, this.getBukkitLivingEntity(), spell).a())
							&& !SpellUtils.hasSpellActive(soltarget, spell) && MathUtils.RandomRoll(20)) {
						aiDoSpellCast(plugin, spell, soltarget, mana_cost);
					}
					break;
				case SpellType.Charm:
					// TODO Charms
					break;
				case SpellType.Pet:
					// TODO Pets
					break;
				case SpellType.Lifetap:
					if ((SoliniaSpell.isValidEffectForEntity(target, this.getBukkitLivingEntity(), spell).a())
							&& !SpellUtils.hasSpellActive(soltarget, spell) && getHPRatio() <= 95 && MathUtils.RandomRoll(50)
					// TODO Buff stacking
					) {
						if (!checked_los) {
							if (!RaycastUtils.isEntityInLineOfSight(this,tar, true)) {
								return false;
							}
							checked_los = true;
						}

						aiDoSpellCast(plugin, spell, soltarget, mana_cost);
						return true;
					}
					break;
				case SpellType.Snare:
					if ((SoliniaSpell.isValidEffectForEntity(target, this.getBukkitLivingEntity(), spell).a())
							&& !SpellUtils.hasSpellActive(soltarget, spell) && !soltarget.isRooted() && MathUtils.RandomRoll(50)
							&& (nowtimestamp.after(StateManager.getInstance().getEntityManager()
									.getDontSpellTypeMeBefore(target, SpellType.Snare)))
					// TODO Buff stacking
					) {
						if (!checked_los) {
							if (!RaycastUtils.isEntityInLineOfSight(this,tar, true)) {
								return false;
							}
							checked_los = true;
						}

						aiDoSpellCast(plugin, spell, soltarget, mana_cost);
						StateManager.getInstance().getEntityManager().setDontSpellTypeMeBefore(target, SpellType.Snare,
								expiretimestamp);
						return true;
					}

					break;
				case SpellType.DOT:
					if ((SoliniaSpell.isValidEffectForEntity(target, this.getBukkitLivingEntity(), spell).a())
							&& !SpellUtils.hasSpellActive(soltarget, spell) && (MathUtils.RandomRoll(60))
							&& (nowtimestamp.after(StateManager.getInstance().getEntityManager()
									.getDontSpellTypeMeBefore(target, SpellType.DOT)))
					// TODO buff stacking
					) {

						if (!checked_los) {
							if (!RaycastUtils.isEntityInLineOfSight(this,tar, true)) {
								return false;
							}
							checked_los = true;
						}
						aiDoSpellCast(plugin, spell, soltarget, mana_cost);
						StateManager.getInstance().getEntityManager().setDontSpellTypeMeBefore(target, SpellType.DOT,
								expiretimestamp);
						return true;
					}
					break;
				default:
					// unknown spell type
					break;
				}
			}
		}

		return false;
	}
	
	@Override
	public boolean isSocial() {
		if (isPlayer())
			return false;

		if (!isNPC())
			return false;

		if (this.livingentity == null)
			return false;

		ISoliniaNPC npc;
		try {
			npc = StateManager.getInstance().getConfigurationManager().getNPC(this.getNpcid());
			
			if (npc == null)
				return false;
			
			if (npc.getClassid() < 1)
				return false;

			return npc.isSocial();
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	
	@Override
	public boolean aiDoSpellCast(Plugin plugin, ISoliniaSpell spell, ISoliniaLivingEntity target, int manaCost) {
		// TODO Spell Casting Times
		// if(!spell.isBardSong()) {
		// SetCurrentSpeed(0);
		// }

		/*boolean success = false;
		if (getMana() > spell.getActSpellCost(this)) {
			success = spell.tryApplyOnEntity(this.getBukkitLivingEntity(), target.getBukkitLivingEntity(), true, "");
		}

		if (success) {
			this.increaseMana(-spell.getActSpellCost(this));
		}

		return success;*/
		
		if (this.getBukkitLivingEntity() == null || this.getBukkitLivingEntity().isDead())
			return false;

		if (target.getBukkitLivingEntity() == null || target.getBukkitLivingEntity().isDead())
			return false;

		// if already casting abort
		if (this.isCasting())
			return false;
		
		if
		(
			!this.IsValidSpell(spell.getId()) ||
			isStunned() ||
			isFeared() ||
			isMezzed() ||
			isSilenced()
			
			//			(IsSilenced() && !IsDiscipline(spell_id)) ||
//			(IsAmnesiad() && IsDiscipline(spell_id))
		)
			return false;
		
		CastingSpell castingSpell = new CastingSpell(this.getBukkitLivingEntity().getUniqueId(), spell.getId(), true, false,
				true, "", target.getBukkitLivingEntity().getUniqueId());
		
		try {
			return StateManager.getInstance().getEntityManager().startCasting(this.getBukkitLivingEntity(), castingSpell);
		} catch (CoreStateInitException e) {
			return false;
		}
	}
	
	@Override
	public boolean isSilenced()
	{
		return this.hasSpellEffectType(SpellEffectType.Silence);
	}

	@Override
	public Integer getMana() {
		if (isPlayer()) {
			try {
				ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player) this.getBukkitLivingEntity());
				return solPlayer.getMana();
			} catch (CoreStateInitException e) {
				return 0;
			}
		}

		if (this.getNpcid() < 1)
			return 0;

		try {
			ISoliniaNPC npc = getNPC();
			if (npc == null)
				return 0;
			return StateManager.getInstance().getEntityManager().getNPCMana(this.getBukkitLivingEntity(), npc);
		} catch (CoreStateInitException e) {
			return 0;
		}
	}

	@Override
	public int getResistsFromActiveEffects(SpellResistType type) {
		int total = 0;
		SpellEffectType seekSpellEffectType = SpellUtils.getSpellEffectTypeFromResistType(type);

		if (seekSpellEffectType != null) {
			try {
				SoliniaEntitySpells effects = StateManager.getInstance().getEntityManager()
						.getActiveEntitySpells(getBukkitLivingEntity());

				for (SoliniaActiveSpell activeSpell : effects.getActiveSpells()) {
					for (ActiveSpellEffect spelleffect : activeSpell.getActiveSpellEffects()) {
						if (spelleffect.getSpellEffectType().equals(SpellEffectType.ResistAll)
								|| spelleffect.getSpellEffectType().equals(seekSpellEffectType)) {
							total += spelleffect.getCalculatedValue();
						}
					}
				}
			} catch (CoreStateInitException e) {
				// skip over
			}
		}

		return total;
	}

	@Override
	public int getResists(SpellResistType type) {
		if (isPlayer()) {
			try {
				return SoliniaPlayerAdapter.Adapt((Player) getBukkitLivingEntity()).getResist(type);
			} catch (CoreStateInitException e) {
				return 25;
			}
		} else {
			int effects = getResistsFromActiveEffects(type);
			if (isNPC())
			{
				ISoliniaNPC npc = this.getNPC();
				if (npc != null)
				{
					switch(type)
					{
						case RESIST_FIRE:
							effects += npc.getFR();
							break;
						case RESIST_COLD:
							effects += npc.getCR();
							break;
						case RESIST_MAGIC:
							effects += npc.getMR();
							break;
						case RESIST_DISEASE:
							effects += npc.getDR();
							break;
						case RESIST_POISON:
							effects += npc.getPR();
							break;
					}
				}
			}
			return effects;
		}
	}

	@Override
	public void processInteractionEvent(LivingEntity triggerentity, InteractionType type, String data) {
		if (this.getNpcid() > 0) {
			ISoliniaNPC npc;
			try {
				npc = StateManager.getInstance().getConfigurationManager().getNPC(this.getNpcid());
				npc.processInteractionEvent(this, triggerentity, type, data);
			} catch (CoreStateInitException e) {
				e.printStackTrace();
			}
		}
	}
	
	private float getProcChances(float ProcBonus, int hand) {
		int mydex = getDexterity();
		float ProcChance = 0.0f;

		ProcChance = (float) (Utils.BaseProcChance +
				(float)(mydex) / Utils.ProcDexDivideBy);
			ProcChance += ProcChance * ProcBonus / 100.0f;

		//Log(Logs::Detail, Logs::Combat, "Proc chance %.2f (%.2f from bonuses)", ProcChance, ProcBonus);
		return ProcChance;
	}
	
	@Override
	public double getStatMaxHP(int stamina) {
		if (this.isPlayer() && this.getPlayer() != null)
			return this.getPlayer().getStatMaxHP(stamina);
		
		if (this.isNPC() && this.getNPC() != null)
			return this.getNPC().getStatMaxHP(stamina);
		
		return 4;
	}

	@Override
	public double getMaxHP() {

		if (getNpcid() < 1 && !isPlayer())
			return 1;

		double statHp = getStatMaxHP(getStamina());
		
		double itemHp = getItemHp();
		double totalHp = statHp + itemHp;
		
		if (this.IsCorePet())
		{
			try {
				int petFocus = StateManager.getInstance().getEntityManager().getPetFocus(this.getBukkitLivingEntity().getUniqueId());
				int hpBonus = (int) Math.floor(petFocus / 2)*50;
				totalHp += hpBonus;
			} catch (CoreStateInitException e) {
			}
		}
		
		try {
			if (getNpcid() > 0) 
			{
				ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(getNpcid());
				if (npc == null)
					return totalHp;

				if (npc.getForcedMaxHp() > 0) {
					if (npc.isCorePet())
					{
						// npc pets work differently
						if (totalHp < npc.getForcedMaxHp())
							totalHp = npc.getForcedMaxHp();
					} else {
						return npc.getForcedMaxHp();
					}
				}
				
				totalHp += SpellUtils.getTotalEffectTotalHP(this.getBukkitLivingEntity());

				if (npc.isBoss()) {
					totalHp += (NPCUtils.getBossHPMultiplier(npc.isHeroic()) * npc.getLevel());
				}

				if (npc.isHeroic()) {
					totalHp += (NPCUtils.getHeroicHPMultiplier() * npc.getLevel());
				}

				if (npc.isRaidboss()) {
					totalHp += (NPCUtils.getRaidBossHPMultiplier() * npc.getLevel());
				}

				if (npc.isRaidheroic()) {
					totalHp += (NPCUtils.getRaidHeroicHPMultiplier() * npc.getLevel());
				}
				
				int percentHP = 100;
				if (this.isCurrentlyNPCPet() && !this.isCharmed() && npc.isCorePet())
				{
					if (this.getActiveMob().getOwner().isPresent() && Bukkit.getEntity(this.getActiveMob().getOwner().get()) != null)
					{
						Entity playerEntity = Bukkit.getEntity(this.getActiveMob().getOwner().get());
						if (playerEntity != null && playerEntity instanceof Player)
						{
							ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player)playerEntity);
							int effectIdLookup = SpellUtils.getEffectIdFromEffectType(SpellEffectType.PetMaxHP);
							if (effectIdLookup > 0)
							{
								percentHP += SpellUtils.getHighestAAEffectEffectTypeTuple((Player)playerEntity, SpellEffectType.PetMaxHP).a();
							}
						}
					}
				}

				return (totalHp/100) * percentHP;
			}

			if (isPlayer()) {
				ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player) getBukkitLivingEntity());
				if (solplayer == null)
					return totalHp;

				totalHp += SpellUtils.getTotalEffectTotalHP(this.getBukkitLivingEntity());
				// get AA hp bonus
				totalHp += SpellUtils.getTotalAAEffectMaxHp(this.getBukkitLivingEntity());

				return totalHp;
			}
		} catch (CoreStateInitException e) {
			return totalHp;
		}

		return totalHp;
	}

	private boolean holdingTwoHander() {
		if (this.getSoliniaItemInMainHand() != null)
		{
			if (
					this.getSoliniaItemInMainHand().getItemType().equals(ItemType.TwoHandBlunt) ||
					this.getSoliniaItemInMainHand().getItemType().equals(ItemType.TwoHandPiercing) ||
					this.getSoliniaItemInMainHand().getItemType().equals(ItemType.TwoHandSlashing)
				) { 
				return true;
			}
		}

		if (this.getSoliniaItemInOffHand() != null)
		{
			if (
					this.getSoliniaItemInOffHand().getItemType().equals(ItemType.TwoHandBlunt) ||
					this.getSoliniaItemInOffHand().getItemType().equals(ItemType.TwoHandPiercing) ||
					this.getSoliniaItemInOffHand().getItemType().equals(ItemType.TwoHandSlashing)
				) { 
				return true;
			}
		}

		
		return false;
	}
	
	@Override
	public Timestamp getLastCallForAssist() {
		try {
			return StateManager.getInstance().getEntityManager().getLastCallForAssist()
					.get(this.getBukkitLivingEntity().getUniqueId());
		} catch (CoreStateInitException e) {
		}
		return null;
	}
	
	@Override
	public void setLastCallForAssist() {
		try {
			LocalDateTime datetime = LocalDateTime.now();
			Timestamp nowtimestamp = Timestamp.valueOf(datetime);
			StateManager.getInstance().getEntityManager().setLastCallForAssist(this.getBukkitLivingEntity().getUniqueId(),
					nowtimestamp);
		} catch (CoreStateInitException e) {

		}
	}
	
	@Override
	public boolean canCallForAssist()
	{
		Timestamp expiretimestamp = getLastCallForAssist();
		if (expiretimestamp != null) {
			LocalDateTime datetime = LocalDateTime.now();
			Timestamp nowtimestamp = Timestamp.valueOf(datetime);
			Timestamp mintimestamp = Timestamp.valueOf(expiretimestamp.toLocalDateTime().plus(3, ChronoUnit.SECONDS));

			if (nowtimestamp.before(mintimestamp))
				return false;
		}
		
		return true;
	}

	@Override
	public Timestamp getLastDoubleAttack() {
		try {
			return StateManager.getInstance().getEntityManager().getLastDoubleAttack()
					.get(this.getBukkitLivingEntity().getUniqueId());
		} catch (CoreStateInitException e) {
		}
		return null;
	}
	
	@Override
	public Timestamp getLastDisarm() {
		try {
			return StateManager.getInstance().getEntityManager().getLastDisarm()
					.get(this.getBukkitLivingEntity().getUniqueId());
		} catch (CoreStateInitException e) {
		}
		return null;
	}
	
	@Override
	public Timestamp getLastMeleeAttack() {
		try {
			return StateManager.getInstance().getEntityManager().getLastMeleeAttack()
					.get(this.getBukkitLivingEntity().getUniqueId());
		} catch (CoreStateInitException e) {
		}
		return null;
	}
	
	@Override
	public Timestamp getAiLastCast() {
		try {
			return StateManager.getInstance().getEntityManager().getAiLastCast()
					.get(this.getBukkitLivingEntity().getUniqueId());
		} catch (CoreStateInitException e) {
		}
		return null;
	}

	@Override
	public void setLastDoubleAttack() {
		try {
			LocalDateTime datetime = LocalDateTime.now();
			Timestamp nowtimestamp = Timestamp.valueOf(datetime);
			StateManager.getInstance().getEntityManager()
					.setLastDoubleAttack(this.getBukkitLivingEntity().getUniqueId(), nowtimestamp);
		} catch (CoreStateInitException e) {

		}
	}

	@Override
	public Timestamp getLastRiposte() {
		try {
			return StateManager.getInstance().getEntityManager().getLastRiposte()
					.get(this.getBukkitLivingEntity().getUniqueId());
		} catch (CoreStateInitException e) {
		}
		return null;
	}

	@Override
	public void setLastRiposte() {
		try {
			LocalDateTime datetime = LocalDateTime.now();
			Timestamp nowtimestamp = Timestamp.valueOf(datetime);
			StateManager.getInstance().getEntityManager().setLastRiposte(this.getBukkitLivingEntity().getUniqueId(),
					nowtimestamp);
		} catch (CoreStateInitException e) {

		}
	}
	
	@Override
	public void setAiLastCast() {
		try {
			LocalDateTime datetime = LocalDateTime.now();
			Timestamp nowtimestamp = Timestamp.valueOf(datetime);
			StateManager.getInstance().getEntityManager().setAILastCast(this.getBukkitLivingEntity().getUniqueId(),
					nowtimestamp);
		} catch (CoreStateInitException e) {

		}
	}
	
	@Override
	public void setLastMeleeAttack() {
		try {
			LocalDateTime datetime = LocalDateTime.now();
			Timestamp nowtimestamp = Timestamp.valueOf(datetime);
			StateManager.getInstance().getEntityManager().setLastMeleeAttack(this.getBukkitLivingEntity().getUniqueId(),
					nowtimestamp);
		} catch (CoreStateInitException e) {

		}
	}

	@Override
	public boolean getDodgeCheck() {

		if (getNpcid() < 1 && !isPlayer())
			return false;

		try {
			if (getNpcid() > 0) {
				ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(getNpcid());
				if (npc == null)
					return false;

				boolean result = npc.getDodgeCheck();

				return result;
			}

			if (isPlayer()) {
				ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player) getBukkitLivingEntity());
				if (solplayer == null)
					return false;

				boolean result = solplayer.getDodgeCheck();

				return result;
			}
		} catch (CoreStateInitException e) {
			return false;
		}

		return false;
	}

	@Override
	public int getStrength() {

		if (getNpcid() < 1 && !isPlayer())
			return 1;

		try {
			if (getNpcid() > 0) {
				ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(getNpcid());
				if (npc == null)
					return 1;

				int stat = npc.getLevel() * 5;
				stat += getTotalItemStat(StatType.Strength);
				stat += SpellUtils.getTotalEffectStat(this.getBukkitLivingEntity(), StatType.Strength);

				if (stat > getMaxStat(StatType.Strength))
					stat = getMaxStat(StatType.Strength);
				return stat;
			}

			if (isPlayer()) {
				ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player) getBukkitLivingEntity());
				if (solplayer == null)
					return 1;

				int stat = 1;

				if (solplayer.getRace() != null)
					stat += solplayer.getRace().getStrength();

				stat += getTotalItemStat(StatType.Strength);
				stat += SpellUtils.getTotalEffectStat(this.getBukkitLivingEntity(), StatType.Strength);
				stat += SpellUtils.getTotalAAEffectStat(this.getBukkitLivingEntity(), StatType.Strength);

				if (stat > getMaxStat(StatType.Strength))
					stat = getMaxStat(StatType.Strength);

				return stat;
			}
		} catch (CoreStateInitException e) {
			return 1;
		}

		return 1;
	}

	@Override
	public int getTotalItemAC() {
		int total = 0;

		try {
			// does not use item in hand
			boolean allowNpc = false;
			// only allow hp from gear if heroic/raid
			if (this.isNPC())
			{
				ISoliniaNPC npc = this.getNPC();
				if (npc.isHeroic() || npc.isRaidboss() || npc.isRaidheroic())
					allowNpc = true;
			}
			if  (this.IsCorePet())
				allowNpc = true;
			
			if (this.isPlayer() || allowNpc)
			for (ISoliniaItem item : getEquippedSoliniaItems(true)) {
				if (item != null)
					total += item.getAC();
			}
			return total;
		} catch (Exception e) {
			e.printStackTrace();
			return total;
		}
	}

	@Override
	public List<ISoliniaItem> getEquippedSoliniaItems() {
		// This includes augs as seperate items
		return getEquippedSoliniaItems(false);
	}

	@Override
	public List<ISoliniaItem> getEquippedSoliniaItems(boolean ignoreMainhand) {
		return getEquippedSoliniaItems(ignoreMainhand, false);
	}
	
	
	@Override
	public List<ISoliniaItem> getEquippedSoliniaItems(boolean ignoreMainhand, boolean excludeUnwearable) {
		// This includes augs as seperate items
		if (isPlayer()) {
			try {
				ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player) this.getBukkitLivingEntity());
				return solplayer.getEquippedSoliniaItems(ignoreMainhand, excludeUnwearable);
			} catch (CoreStateInitException e) {

			}
		}

		if (isNPC()) {
			ISoliniaNPC npc = getNPC();
			return npc.getEquippedSoliniaItems(this, ignoreMainhand);
		}

		return new ArrayList<ISoliniaItem>();
	}

	@Override
	public void updateMaxHp() {
		double calculatedhp = getMaxHP();
		AttributeInstance healthAttribute = getBukkitLivingEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH);
		healthAttribute.setBaseValue(calculatedhp);
	}

	@Override
	public boolean isNPC() {
		if (isPlayer())
			return false;

		if (getNpcid() < 1) {
			return false;
		}

		if (getNpcid() > 0) {
			try {
				ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(getNpcid());
				if (npc == null)
					return false;

			} catch (CoreStateInitException e) {
				return false;
			}
			return true;
		}

		return false;
	}

	@Override
	public int getStamina() {

		if (getNpcid() < 1 && !isPlayer())
			return 1;

		try {
			if (getNpcid() > 0) {
				ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(getNpcid());
				if (npc == null)
					return 1;

				int stat = getMentorLevel() * 5;
				int totalItemStat = getTotalItemStat(StatType.Stamina);
				int totalEffectStat = SpellUtils.getTotalEffectStat(this.getBukkitLivingEntity(), StatType.Stamina);
				int maxStat = getMaxStat(StatType.Stamina);
				stat += totalItemStat;
				stat += totalEffectStat;

				if (stat > maxStat)
					stat = maxStat;
				return stat;
			}

			if (isPlayer()) {
				ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player) getBukkitLivingEntity());
				if (solplayer == null)
					return 1;

				int stat = 1;

				if (solplayer.getRace() != null)
					stat += solplayer.getRace().getStamina();

				int totalItemStat = getTotalItemStat(StatType.Stamina);
				stat += totalItemStat;

				int totalEffectStat = SpellUtils.getTotalEffectStat(this.getBukkitLivingEntity(), StatType.Stamina);
				stat += totalEffectStat;
				
				int totalAAEffectStat = SpellUtils.getTotalAAEffectStat(this.getBukkitLivingEntity(), StatType.Stamina);
				stat += totalAAEffectStat;
				
				int maxStat = getMaxStat(StatType.Stamina);

				if (stat > maxStat)
					stat = maxStat;
				return stat;
			}
		} catch (CoreStateInitException e) {
			return 1;
		}

		return 1;
	}

	@Override
	public int getAgility() {

		if (getNpcid() < 1 && !isPlayer())
			return 1;

		try {
			if (getNpcid() > 0) {
				ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(getNpcid());
				if (npc == null)
					return 1;

				int stat = npc.getLevel() * 5;
				stat += getTotalItemStat(StatType.Agility);
				stat += SpellUtils.getTotalEffectStat(this.getBukkitLivingEntity(),StatType.Agility);

				if (stat > getMaxStat(StatType.Agility))
					stat = getMaxStat(StatType.Agility);

				return stat;
			}

			if (isPlayer()) {
				ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player) getBukkitLivingEntity());
				if (solplayer == null)
					return 1;

				int stat = 1;

				if (solplayer.getRace() != null)
					stat += solplayer.getRace().getAgility();

				stat += getTotalItemStat(StatType.Agility);
				stat += SpellUtils.getTotalEffectStat(this.getBukkitLivingEntity(), StatType.Agility);
				stat += SpellUtils.getTotalAAEffectStat(this.getBukkitLivingEntity(), StatType.Agility);

				if (stat > getMaxStat(StatType.Agility))
					stat = getMaxStat(StatType.Agility);

				return stat;
			}
		} catch (CoreStateInitException e) {
			return 1;
		}

		return 1;
	}

	@Override
	public int getDexterity() {

		if (getNpcid() < 1 && !isPlayer())
			return 1;

		try {
			if (getNpcid() > 0) {
				ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(getNpcid());
				if (npc == null)
					return 1;

				int stat = npc.getLevel() * 5;
				stat += getTotalItemStat(StatType.Dexterity);
				stat += SpellUtils.getTotalEffectStat(this.getBukkitLivingEntity(), StatType.Dexterity);

				if (stat > getMaxStat(StatType.Dexterity))
					stat = getMaxStat(StatType.Dexterity);

				return stat;
			}

			if (isPlayer()) {
				ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player) getBukkitLivingEntity());
				if (solplayer == null)
					return 1;

				int stat = 1;

				if (solplayer.getRace() != null)
					stat += solplayer.getRace().getDexterity();

				stat += getTotalItemStat(StatType.Dexterity);
				stat += SpellUtils.getTotalEffectStat(this.getBukkitLivingEntity(), StatType.Dexterity);
				stat += SpellUtils.getTotalAAEffectStat(this.getBukkitLivingEntity(), StatType.Dexterity);

				if (stat > getMaxStat(StatType.Dexterity))
					stat = getMaxStat(StatType.Dexterity);

				return stat;
			}
		} catch (CoreStateInitException e) {
			return 1;
		}

		return 1;
	}

	@Override
	public int getIntelligence() {
		if (getNpcid() < 1 && !isPlayer())
			return 1;

		try {
			if (getNpcid() > 0) {
				ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(getNpcid());
				if (npc == null)
					return 1;

				int stat = npc.getLevel() * 5;
				stat += getTotalItemStat(StatType.Intelligence);
				stat += SpellUtils.getTotalEffectStat(this.getBukkitLivingEntity(), StatType.Intelligence);

				if (stat > getMaxStat(StatType.Intelligence))
					stat = getMaxStat(StatType.Intelligence);

				return stat;
			}

			if (isPlayer()) {
				ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player) getBukkitLivingEntity());
				if (solplayer == null)
					return 1;
				int stat = 1;

				if (solplayer.getRace() != null)
					stat += solplayer.getRace().getIntelligence();

				
				stat += getTotalItemStat(StatType.Intelligence);

				stat += SpellUtils.getTotalEffectStat(this.getBukkitLivingEntity(), StatType.Intelligence);
				stat += SpellUtils.getTotalAAEffectStat(this.getBukkitLivingEntity(), StatType.Intelligence);

				if (stat > getMaxStat(StatType.Intelligence))
					stat = getMaxStat(StatType.Intelligence);

				return stat;
			}
		} catch (CoreStateInitException e) {
			return 1;
		}

		return 1;
	}

	@Override
	public int getWisdom() {
		if (getNpcid() < 1 && !isPlayer())
			return 1;

		try {
			if (getNpcid() > 0) {
				ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(getNpcid());
				if (npc == null)
					return 1;

				int stat = npc.getLevel() * 5;
				stat += getTotalItemStat(StatType.Wisdom);
				stat += SpellUtils.getTotalEffectStat(this.getBukkitLivingEntity(), StatType.Wisdom);

				if (stat > getMaxStat(StatType.Wisdom))
					stat = getMaxStat(StatType.Wisdom);

				return stat;
			}

			if (isPlayer()) {

				ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player) getBukkitLivingEntity());
				if (solplayer == null)
					return 1;

				int stat = 1;

				if (solplayer.getRace() != null)
					stat += solplayer.getRace().getWisdom();

				stat += getTotalItemStat(StatType.Wisdom);
				stat += SpellUtils.getTotalEffectStat(this.getBukkitLivingEntity(), StatType.Wisdom);
				stat += SpellUtils.getTotalAAEffectStat(this.getBukkitLivingEntity(), StatType.Wisdom);

				if (stat > getMaxStat(StatType.Wisdom))
					stat = getMaxStat(StatType.Wisdom);
		
				return stat;
			}
			
		} catch (CoreStateInitException e) {
			return 1;
		}

		return 1;
	}

	@Override
	public int getCharisma() {

		if (getNpcid() < 1 && !isPlayer())
			return 1;

		try {
			if (getNpcid() > 0) {
				ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(getNpcid());
				if (npc == null)
					return 1;

				int stat = npc.getLevel() * 5;
				stat += getTotalItemStat(StatType.Charisma);
				stat += SpellUtils.getTotalEffectStat(this.getBukkitLivingEntity(),StatType.Charisma);

				if (stat > getMaxStat(StatType.Charisma))
					stat = getMaxStat(StatType.Charisma);

				return stat;
			}

			if (isPlayer()) {
				ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player) getBukkitLivingEntity());
				if (solplayer == null)
					return 1;

				int stat = 1;

				if (solplayer.getRace() != null)
					stat += solplayer.getRace().getCharisma();

				stat += getTotalItemStat(StatType.Charisma);
				stat += SpellUtils.getTotalEffectStat(this.getBukkitLivingEntity(), StatType.Charisma);
				stat += SpellUtils.getTotalAAEffectStat(this.getBukkitLivingEntity(), StatType.Charisma);

				if (stat > getMaxStat(StatType.Charisma))
					stat = getMaxStat(StatType.Charisma);

				return stat;
			}
		} catch (CoreStateInitException e) {
			return 1;
		}

		return 1;
	}

	@Override
	public int getMaxStat(StatType statType) {
		int baseMaxStat = 255;
		
		int filter = -1;
		
		switch(statType)
		{
		case Strength: // str
			filter = 0;
			break;
		case Stamina: // sta
			filter = 1;
			break;
		case Agility: // agi
			filter = 2;
			break;
		case Dexterity: // dex
			filter = 3;
			break;
		case Wisdom: // wis
			filter = 4;
			break;
		case Intelligence: // int
			filter = 5;
			break;
		case Charisma: // cha
			filter = 6;
			break;
		default:
			break;
		}
		
		int bonuses = 0;
		if (filter > -1)
		{
			int aabonus = this.getAABonuses(SpellEffectType.RaiseStatCap,filter);
			aabonus += this.getAABonuses(SpellEffectType.RaiseStatCap,13); // all
			bonuses = aabonus + this.getSpellBonuses(SpellEffectType.RaiseStatCap,filter);
		}

		
		if (bonuses == 0) {
			return baseMaxStat;
		} else {
			return baseMaxStat + bonuses;
		}
	}

	@Override
	public boolean getRiposteCheck() {
		if (getNpcid() < 1 && !isPlayer())
			return false;

		// If dual wield less than 3 second ago return false
		// Ugly hack to work around looping dual wields (cant get source of offhand on
		// damage event)
		Timestamp expiretimestamp = getLastRiposte();
		if (expiretimestamp != null) {
			LocalDateTime datetime = LocalDateTime.now();
			Timestamp nowtimestamp = Timestamp.valueOf(datetime);
			Timestamp mintimestamp = Timestamp.valueOf(expiretimestamp.toLocalDateTime().plus(3, ChronoUnit.SECONDS));

			if (nowtimestamp.before(mintimestamp))
				return false;
		}

		try {
			if (getNpcid() > 0) {
				ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(getNpcid());
				if (npc == null)
					return false;

				return npc.getRiposteCheck();
			}

			if (isPlayer()) {
				ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player) getBukkitLivingEntity());
				if (solplayer == null)
					return false;

				return solplayer.getRiposteCheck();
			}
		} catch (CoreStateInitException e) {
			return false;
		}

		return false;
	}
	
	@Override
	public boolean getAiLastCastCheck()
	{
		if (isPlayer())
			return false;

		// If dual wield less than 3 second ago return false
		// Ugly hack to work around looping dual wields (cant get source of offhand on
		// damage event)
		Timestamp expiretimestamp = getAiLastCast();
		if (expiretimestamp != null) {
			LocalDateTime datetime = LocalDateTime.now();
			Timestamp nowtimestamp = Timestamp.valueOf(datetime);
			Timestamp mintimestamp = Timestamp.valueOf(expiretimestamp.toLocalDateTime().plus((long) (getAiCastFrequency()*1000), ChronoUnit.MILLIS));

			if (nowtimestamp.before(mintimestamp))
				return false;
		}

		return true;
	}
	
	private int getAiCastFrequency() {
		// TODO Auto-generated method stub
		return 6;
	}

	@Override
	public boolean getLastMeleeAttackCheck()
	{
		if (getNpcid() < 1 && !isPlayer())
			return false;

		// If dual wield less than 3 second ago return false
		// Ugly hack to work around looping dual wields (cant get source of offhand on
		// damage event)
		Timestamp expiretimestamp = getLastMeleeAttack();
		if (expiretimestamp != null) {
			LocalDateTime datetime = LocalDateTime.now();
			Timestamp nowtimestamp = Timestamp.valueOf(datetime);
			Timestamp mintimestamp = Timestamp.valueOf(expiretimestamp.toLocalDateTime().plus((long) (getAutoAttackTimerFrequencySeconds()*1000), ChronoUnit.MILLIS));

			if (nowtimestamp.before(mintimestamp))
				return false;
		}

		return true;
	}

	@Override
	public boolean checkTripleAttack()
	{
		int chance = getSkill(SkillType.TripleAttack);
		if (chance < 1)
			return false;

		int inc = getAABonuses(SpellEffectType.TripleAttackChance) + getSpellBonuses(SpellEffectType.TripleAttackChance) + getItemBonuses(SpellEffectType.TripleAttackChance);
		chance = (int)(chance * (1 + inc / 100.0f));
		chance = (chance * 100) / (chance + 800);

		return MathUtils.RandomBetween(1, 100) <= chance;
	}
	
	@Override
	public boolean checkDoubleAttack()
	{
		// Not 100% certain pets follow this or if it's just from pets not always
		// having the same skills as most mobs
		int chance = getSkill(SkillType.DoubleAttack);
		if (getMentorLevel() > 35)
			chance += getMentorLevel();

		int per_inc = getAABonuses(SpellEffectType.DoubleAttackChance) + getSpellBonuses(SpellEffectType.DoubleAttackChance) + getItemBonuses(SpellEffectType.DoubleAttackChance);
		if (per_inc > 0)
			chance += chance * per_inc / 100;

		return MathUtils.RandomBetween(1, 500) <= chance;
	}

	@Override
	public int getMaxMP() {
		if (getClassObj() == null)
			return 1;

		String profession = getClassObj().getName().toUpperCase();

		int wisintagi = 0;
		if (EntityUtils.getCasterClass(profession).equals("W"))
			wisintagi = getWisdom();
		if (EntityUtils.getCasterClass(profession).equals("I"))
			wisintagi = getIntelligence();
		if (EntityUtils.getCasterClass(profession).equals("N"))
			wisintagi = getAgility();

		double maxmana = ((850 * getMentorLevel()) + (85 * wisintagi * getMentorLevel())) / 425;
		double itemMana = getItemMana();
		maxmana += itemMana;
		if (this.getNpcid() > 0) {
			maxmana = maxmana + (50 * getMentorLevel());

			ISoliniaNPC npc = getNPC();
			if (npc != null) {
				maxmana += npc.getNPCMana();
			}

		}

		return (int) Math.floor(maxmana);
	}

	@Override
	public ISoliniaClass getClassObj() {
		try {
			if (isPlayer()) {
				return SoliniaPlayerAdapter.Adapt((Player) getBukkitLivingEntity()).getClassObj();
			}

			if (this.getNpcid() > 0) {
				return StateManager.getInstance().getConfigurationManager().getNPC(getNpcid()).getClassObj();
			}
		} catch (CoreStateInitException e) {
			return null;
		}
		return null;
	}

	@Override
	public ISoliniaRace getRace() {
		try {
			if (isPlayer()) {
				return SoliniaPlayerAdapter.Adapt((Player) getBukkitLivingEntity()).getRace();
			}

			if (this.getNpcid() > 0) {
				return StateManager.getInstance().getConfigurationManager().getNPC(getNpcid()).getRace();
			}
		} catch (CoreStateInitException e) {
			return null;
		}
		return null;
	}
	
	@Override
	public ISoliniaGod getGod() {
		try {
			if (isPlayer()) {
				return SoliniaPlayerAdapter.Adapt((Player) getBukkitLivingEntity()).getGod();
			}

			// only players have gods
			if (this.getNpcid() > 0) {
				return null;
			}
		} catch (CoreStateInitException e) {
			return null;
		}
		return null;
	}
	
	@Override
	public int getRaceId() {
		try {
			if (isPlayer()) {
				return SoliniaPlayerAdapter.Adapt((Player) getBukkitLivingEntity()).getRaceId();
			}

			if (this.getNpcid() > 0) {
				return StateManager.getInstance().getConfigurationManager().getNPC(getNpcid()).getRaceid();
			}
		} catch (CoreStateInitException e) {
			return 0;
		}
		return 0;
	}

	@Override
	public void doSummon(LivingEntity summoningEntity) {
		if (isPlayer())
			return;

		if (summoningEntity == null || this.livingentity == null)
			return;

		if (summoningEntity.isDead() || this.livingentity.isDead())
			return;

		if (summoningEntity.getLocation().distance(this.livingentity.getLocation()) > 150)
			return;

		ISoliniaNPC npc;
		try {
			npc = StateManager.getInstance().getConfigurationManager().getNPC(this.getNpcid());
			if (!npc.isSummoner())
				return;

			int chanceToSummon = MathUtils.RandomBetween(1, 10);

			if (chanceToSummon > 8) {
				if (summoningEntity instanceof Player) {
					this.say("You will not evade me " + ((Player) summoningEntity).getCustomName() + "!");
				} else {
					this.say("You will not evade me " + summoningEntity.getCustomName() + "!");

				}
				
				Location targetLocation = getBukkitLivingEntity().getLocation();
				
				Vector dir = targetLocation.clone().subtract(summoningEntity.getEyeLocation()).toVector();
				targetLocation.setDirection(dir);
				
				EntityUtils.teleportSafely(summoningEntity,targetLocation);
			}
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void doTeleportAttack(LivingEntity teleportedEntity) {
		if (isPlayer())
			return;

		if (teleportedEntity == null || this.livingentity == null)
			return;

		ISoliniaNPC npc;
		try {
			npc = StateManager.getInstance().getConfigurationManager().getNPC(this.getNpcid());
			if (!npc.isTeleportAttack())
				return;

			if (npc.getTeleportAttackLocation() == null || npc.getTeleportAttackLocation().equals(""))
				return;

			String[] zonedata = npc.getTeleportAttackLocation().split(",");
			// Dissasemble the value to ensure it is correct
			String world = zonedata[0];
			double x = Double.parseDouble(zonedata[1]);
			double y = Double.parseDouble(zonedata[2]);
			double z = Double.parseDouble(zonedata[3]);
			Location loc = new Location(Bukkit.getWorld(world), x, y, z);

			int chanceToSummon = MathUtils.RandomBetween(1, 10);

			if (chanceToSummon > 8) {
				EntityUtils.teleportSafely(teleportedEntity,getBukkitLivingEntity().getLocation());
				teleportedEntity.sendMessage("You have been taken!");
			}
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void addToHateList(UUID uniqueId, int hate, boolean isYellForHelp) {
		if (this.isCurrentlyNPCPet()) {
			// Never add a member to hate list that is a friend of owner or pet
			if (this.getOwnerEntity().getUniqueId().equals(uniqueId))
				return;

			ISoliniaGroup group = StateManager.getInstance().getGroupByMember(this.getOwnerEntity().getUniqueId());
			if (group != null) {
				if (group.getUnmodifiableGroupMembersForBuffs(true,false).contains(uniqueId))
					return;
			}
		}

		Entity other = Bukkit.getEntity(uniqueId);
		if (!(other instanceof LivingEntity))
			return;
		
		// just check bukkit ivnulnerbality here, we want to still get aggro
		// if they are only in /godmode but not creative
		if (other.isInvulnerable() || other.isDead())
			return;
		
		if (this.isMezzed() || this.isFeared() || this.isFeigned() || this.hasSpellEffectType(SpellEffectType.Fear) || this.isStunned())
			return;
		
		if (other instanceof Player)
			if (((Player)other).getGameMode() != GameMode.SURVIVAL)
				return;

		try {
			StateManager.getInstance().getEntityManager().addToHateList(this.getBukkitLivingEntity().getUniqueId(),
					uniqueId, hate, true);
			checkHateTargets();
			
			ISoliniaLivingEntity solOther = SoliniaLivingEntityAdapter.Adapt((LivingEntity)other);
			if (solOther != null && solOther.getBukkitLivingEntity() != null && !solOther.getBukkitLivingEntity().isDead())
			{
				// then add pet owner if there's one
				if (solOther.isCurrentlyNPCPet()) { // Other is a pet, add him and it
					ISoliniaLivingEntity owner = solOther.getOwnerSoliniaLivingEntity();
							 // EverHood 6/12/06
							 // Can't add a feigned owner to hate list
					if (owner != null)
					if (owner.isPlayer() && owner.isFeignedDeath()) {
						//they avoid hate due to feign death...
					}
					else {
						// cb:2007-08-17
						// owner must get on list, but he's not actually gained any hate yet
						if (owner.getSpecialAbility(SpecialAbility.IMMUNE_AGGRO) < 1)
						{
							if (owner.isPlayer() && !checkAggro(owner))
							{
								//owner->CastToClient()->AddAutoXTarget(this);
								this.addToHateList(owner.getBukkitLivingEntity().getUniqueId(), 1, false);
							}
						}
					}
				}
			}
		} catch (CoreStateInitException e) {
		}
	}

	@Override
	public void clearHateList() {
		try {
			StateManager.getInstance().getEntityManager().clearHateList(this.getBukkitLivingEntity().getUniqueId());
		} catch (CoreStateInitException e) {
		}
	}
	
	@Override
	public int getHateListAmount(UUID target)
	{
		try {
			return StateManager.getInstance().getEntityManager().getHateListAmount(this.getBukkitLivingEntity().getUniqueId(), target);
		} catch (CoreStateInitException e) {
		}
		
		return 0;
	}
	
	@Override
	public boolean isInHateList(UUID target) {
		try {
			return StateManager.getInstance().getEntityManager().isInHateList(this.getBukkitLivingEntity().getUniqueId(), target);
		} catch (CoreStateInitException e) {
		}
		
		return false;
	}

	@Override
	public boolean checkHateTargets() {
		if (this.getBukkitLivingEntity().isDead())
		{
			return false;
		}
		

		if (!(this.getBukkitLivingEntity() instanceof Creature))
		{
			return false;
		}

		if (this.getAttackTarget() != null)
		{
			if (!this.hasHate()) {
				setAttackTarget(null);
				//resetPosition(true);
				return false;
			}
		}
		
		if (this.isMezzed() || this.isFeared() || this.isStunned())
			return false;
		
		List<UUID> removeUuids = new ArrayList<UUID>();

		int maxHate = 0;
		UUID bestUUID = null;
		for (UUID uuid : this.getHateListUUIDs()) {
			int hate = this.getHateListAmount(uuid);
			if (hate < 1) {
				removeUuids.add(uuid);
				continue;
			}

			Entity entity = Bukkit.getEntity(uuid);
			if (entity == null) {
				removeUuids.add(uuid);
				continue;
			}

			if (entity.isDead() || entity.isInvulnerable()) {
				removeUuids.add(uuid);
				continue;
			}
			
			if (entity instanceof Player)
			if (((Player)entity).getGameMode() != GameMode.SURVIVAL)
			{
				removeUuids.add(uuid);
				continue;
			}

			if (entity.getLocation().distance(this.getBukkitLivingEntity().getLocation()) > 150) {
				removeUuids.add(uuid);
				continue;
			}

			if (!(entity instanceof LivingEntity)) {
				removeUuids.add(uuid);
				continue;
			}
			
			try
			{
				ISoliniaLivingEntity solLivingEntity = SoliniaLivingEntityAdapter.Adapt((LivingEntity)entity);
				if (solLivingEntity != null && solLivingEntity.isFeignedDeath())
				{
					removeUuids.add(uuid);
					continue;
				}
				
			} catch (CoreStateInitException e)
			{
				
			}

			if (hate > maxHate) {
				maxHate = hate;
				bestUUID = uuid;
			}
		}

		for (UUID uuid : removeUuids) {
			this.removeFromHateList(uuid);
		}

		if (bestUUID != null) {
			LivingEntity entity = (LivingEntity) Bukkit.getEntity(bestUUID);
			setAttackTarget(entity);
			return true;
		}

		return false;
	}

	@Override
	public List<UUID> getHateListUUIDs() {
		try
		{
			return StateManager.getInstance().getEntityManager().getHateListUUIDs(this.getBukkitLivingEntity().getUniqueId());
		} catch (CoreStateInitException e)
		{
			return new ArrayList<UUID>();
		}
		
	}
	
	@Override
	public void removeFromHateList(UUID targetUUID) {
		try
		{
			StateManager.getInstance().getEntityManager().removeFromHateList(this.getBukkitLivingEntity().getUniqueId(), targetUUID);
		} catch (CoreStateInitException e)
		{
		}
		
	}

	@Override
	public DamageHitInfo avoidDamage(ISoliniaLivingEntity attacker, DamageHitInfo hit) {

		// TODO Block from rear check

		// TODO Parry Check

		if (getDodgeCheck()) {
			hit.damage_done = 0;
			hit.avoided = true;
			hit.dodged = true;
			return hit;
		}

		if (getRiposteCheck()) {
			hit.damage_done = 0;
			hit.riposted = true;
			hit.avoided = true;
			return hit;
		}

		// TODO Shield Block

		// TODO Two Hand Block

		hit.avoided = false;
		return hit;
	}

	@Override
	public boolean checkHitChance(ISoliniaLivingEntity attacker, DamageHitInfo hit) {
		ISoliniaLivingEntity defender = this;

		int avoidance = defender.getTotalDefense();

		int accuracy = hit.tohit;
		// if (accuracy == -1)
		// return true;

		double hitRoll = MathUtils.RandomBetween(0, (int) Math.floor(accuracy));
		double avoidRoll = MathUtils.RandomBetween(0, (int) Math.floor(avoidance));

		// tie breaker? Don't want to be biased any one way
		return hitRoll > avoidRoll;
	}

	@Override
	public DamageHitInfo meleeMitigation(ISoliniaLivingEntity attacker, DamageHitInfo hit) {
		if (hit.damage_done < 0 || hit.base_damage == 0)
			return hit;

		ISoliniaLivingEntity defender = this;
		int mitigation = defender.getMitigationAC();

		if (isPlayer() && attacker.isPlayer())
		{
			mitigation = mitigation * 80 / 100; // PvP
		}

		int roll = (int) rollD20(hit.offense, mitigation);

		// +0.5 for rounding, min to 1 dmg
		hit.damage_done = Math.max((int) (roll * (double) (hit.base_damage) + 0.5), 1);
		return hit;
	}

	double rollD20(int offense, int mitigation) {
		double mods[] = { 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0, 1.1, 1.2, 1.3, 1.4, 1.5, 1.6, 1.7, 1.8, 1.9,
				2.0 };

		// always meditating, ignore this
		// if (isPlayer() && isMeditating())
		// return mods[19];
		
		if (offense < 1)
			offense = 1;
		
		if (mitigation < 1)
			mitigation = 1;

		int atk_roll = MathUtils.RandomBetween(0, offense + 5);
		int def_roll = MathUtils.RandomBetween(0, mitigation + 5);

		int avg = (offense + mitigation + 10) / 2;
		int index = Math.max(0, (atk_roll - def_roll) + (avg / 2));

		index = (int) Utils.clamp((index * 20) / avg, 0, 19);

		return mods[index];
	}

	@Override
	public int getMitigationAC() {
		return ACSum();
	}

	@Override
	public int ACSum() {
		double ac = 0;
		// nm everything gets it
		// players and core pets get AC from Items they are wearing
		//if (this.isPlayer() || IsCorePet())
		ac += getTotalItemAC();
		
		double shield_ac = 0;

		// EQ math
		ac = (ac * 4) / 3;
		// anti-twink
		if (isPlayer() && getMentorLevel() < 50)
			ac = Math.min(ac, 25 + 6 * getMentorLevel());

		ac = Math.max(0, ac + getClassRaceACBonus());

		if (isNPC()) {
			try {
				ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(getNpcid());
				ac += npc.getAC();
			} catch (CoreStateInitException e) {
				// dont get ac from npc type
			}

			// TODO Pet avoidance
			ac += getSkill(SkillType.Defense) / 5;

			double spell_aa_ac = 0;
			// TODO AC AA and Spell bonuses

			spell_aa_ac += getSpellBonuses(SpellEffectType.ArmorClass);

			spell_aa_ac += getAABonuses(SpellEffectType.ArmorClass);

			if (getClassObj() != null) {
				if (getClassObj().getName().equals("ENCHANTER") || getClassObj().getName().equals("ENCHANTER")) {
					ac += spell_aa_ac / 3;
				} else {
					ac += spell_aa_ac / 4;
				}
			} else {
				ac += spell_aa_ac / 4;
			}

		} else {
			double spell_aa_ac = 0;
			// TODO AC AA and Spell bonuses
			spell_aa_ac += getSpellBonuses(SpellEffectType.ArmorClass);

			spell_aa_ac += getAABonuses(SpellEffectType.ArmorClass);

			if (getClassObj() != null) {
				if (getClassObj().getName().equals("ENCHANTER") || getClassObj().getName().equals("ENCHANTER")) {
					ac += getSkill(SkillType.Defense) / 2 + spell_aa_ac / 3;
				} else {
					ac += getSkill(SkillType.Defense) / 3 + spell_aa_ac / 4;
				}
			} else {
				ac += getSkill(SkillType.Defense) / 3 + spell_aa_ac / 4;
			}
		}

		if (getAgility() > 70)
			ac += getAgility() / 20;
		if (ac < 0)
			ac = 0;

		if (isPlayer()) {
			double softcap = getACSoftcap();
			double returns = getSoftcapReturns();

			// TODO itembonuses

			int total_aclimitmod = 0;

			total_aclimitmod += getSpellBonuses(SpellEffectType.CombatStability);

			total_aclimitmod += getAABonuses(SpellEffectType.CombatStability);

			if (total_aclimitmod > 0)
				softcap = (softcap * (100 + total_aclimitmod)) / 100;
			softcap += shield_ac;

			if (ac > softcap) {
				double over_cap = ac - softcap;
				ac = softcap + (over_cap * returns);
			}
		}

		return (int) ac;
	}

	private double getSoftcapReturns() {
		if (getClassObj() == null)
			return 0.3;

		switch (getClassObj().getName().toUpperCase()) {
		case "WARRIOR":
			return 0.35;
		case "CLERIC":
		case "BARD":
		case "MONK":
			return 0.3;
		case "PALADIN":
		case "SHADOWKNIGHT":
			return 0.33;
		case "RANGER":
			return 0.315;
		case "DRUID":
			return 0.265;
		case "ROGUE":
		case "SHAMAN":
		case "BEASTLORD":
		case "BERSERKER":
			return 0.28;
		case "NECROMANCER":
		case "WIZARD":
		case "MAGICIAN":
		case "ENCHANTER":
			return 0.25;
		default:
			return 0.3;
		}
	}

	private int getACSoftcap() {
		if (getClassObj() == null)
			return 350;

		int[] war_softcaps = { 312, 314, 316, 318, 320, 322, 324, 326, 328, 330, 332, 334, 336, 338, 340, 342, 344, 346,
				348, 350, 352, 354, 356, 358, 360, 362, 364, 366, 368, 370, 372, 374, 376, 378, 380, 382, 384, 386, 388,
				390, 392, 394, 396, 398, 400, 402, 404, 406, 408, 410, 412, 414, 416, 418, 420, 422, 424, 426, 428, 430,
				432, 434, 436, 438, 440, 442, 444, 446, 448, 450, 452, 454, 456, 458, 460, 462, 464, 466, 468, 470, 472,
				474, 476, 478, 480, 482, 484, 486, 488, 490, 492, 494, 496, 498, 500, 502, 504, 506, 508, 510, 512, 514,
				516, 518, 520 };

		int[] clrbrdmnk_softcaps = { 274, 276, 278, 278, 280, 282, 284, 286, 288, 290, 292, 292, 294, 296, 298, 300,
				302, 304, 306, 308, 308, 310, 312, 314, 316, 318, 320, 322, 322, 324, 326, 328, 330, 332, 334, 336, 336,
				338, 340, 342, 344, 346, 348, 350, 352, 352, 354, 356, 358, 360, 362, 364, 366, 366, 368, 370, 372, 374,
				376, 378, 380, 380, 382, 384, 386, 388, 390, 392, 394, 396, 396, 398, 400, 402, 404, 406, 408, 410, 410,
				412, 414, 416, 418, 420, 422, 424, 424, 426, 428, 430, 432, 434, 436, 438, 440, 440, 442, 444, 446, 448,
				450, 452, 454, 454, 456 };

		int[] palshd_softcaps = { 298, 300, 302, 304, 306, 308, 310, 312, 314, 316, 318, 320, 322, 324, 326, 328, 330,
				332, 334, 336, 336, 338, 340, 342, 344, 346, 348, 350, 352, 354, 356, 358, 360, 362, 364, 366, 368, 370,
				372, 374, 376, 378, 380, 382, 384, 384, 386, 388, 390, 392, 394, 396, 398, 400, 402, 404, 406, 408, 410,
				412, 414, 416, 418, 420, 422, 424, 426, 428, 430, 432, 432, 434, 436, 438, 440, 442, 444, 446, 448, 450,
				452, 454, 456, 458, 460, 462, 464, 466, 468, 470, 472, 474, 476, 478, 480, 480, 482, 484, 486, 488, 490,
				492, 494, 496, 498 };

		int[] rng_softcaps = { 286, 288, 290, 292, 294, 296, 298, 298, 300, 302, 304, 306, 308, 310, 312, 314, 316, 318,
				320, 322, 322, 324, 326, 328, 330, 332, 334, 336, 338, 340, 342, 344, 344, 346, 348, 350, 352, 354, 356,
				358, 360, 362, 364, 366, 368, 368, 370, 372, 374, 376, 378, 380, 382, 384, 386, 388, 390, 390, 392, 394,
				396, 398, 400, 402, 404, 406, 408, 410, 412, 414, 414, 416, 418, 420, 422, 424, 426, 428, 430, 432, 434,
				436, 436, 438, 440, 442, 444, 446, 448, 450, 452, 454, 456, 458, 460, 460, 462, 464, 466, 468, 470, 472,
				474, 476, 478 };

		int[] dru_softcaps = { 254, 256, 258, 260, 262, 264, 264, 266, 268, 270, 272, 272, 274, 276, 278, 280, 282, 282,
				284, 286, 288, 290, 290, 292, 294, 296, 298, 300, 300, 302, 304, 306, 308, 308, 310, 312, 314, 316, 318,
				318, 320, 322, 324, 326, 328, 328, 330, 332, 334, 336, 336, 338, 340, 342, 344, 346, 346, 348, 350, 352,
				354, 354, 356, 358, 360, 362, 364, 364, 366, 368, 370, 372, 372, 374, 376, 378, 380, 382, 382, 384, 386,
				388, 390, 390, 392, 394, 396, 398, 400, 400, 402, 404, 406, 408, 410, 410, 412, 414, 416, 418, 418, 420,
				422, 424, 426 };

		int[] rogshmbstber_softcaps = { 264, 266, 268, 270, 272, 272, 274, 276, 278, 280, 282, 282, 284, 286, 288, 290,
				292, 294, 294, 296, 298, 300, 302, 304, 306, 306, 308, 310, 312, 314, 316, 316, 318, 320, 322, 324, 326,
				328, 328, 330, 332, 334, 336, 338, 340, 340, 342, 344, 346, 348, 350, 350, 352, 354, 356, 358, 360, 362,
				362, 364, 366, 368, 370, 372, 374, 374, 376, 378, 380, 382, 384, 384, 386, 388, 390, 392, 394, 396, 396,
				398, 400, 402, 404, 406, 408, 408, 410, 412, 414, 416, 418, 418, 420, 422, 424, 426, 428, 430, 430, 432,
				434, 436, 438, 440, 442 };

		int[] necwizmagenc_softcaps = { 248, 250, 252, 254, 256, 256, 258, 260, 262, 264, 264, 266, 268, 270, 272, 272,
				274, 276, 278, 280, 280, 282, 284, 286, 288, 288, 290, 292, 294, 296, 296, 298, 300, 302, 304, 304, 306,
				308, 310, 312, 312, 314, 316, 318, 320, 320, 322, 324, 326, 328, 328, 330, 332, 334, 336, 336, 338, 340,
				342, 344, 344, 346, 348, 350, 352, 352, 354, 356, 358, 360, 360, 362, 364, 366, 368, 368, 370, 372, 374,
				376, 376, 378, 380, 382, 384, 384, 386, 388, 390, 392, 392, 394, 396, 398, 400, 400, 402, 404, 406, 408,
				408, 410, 412, 414, 416 };

		int level = Math.min(105, getMentorLevel()) - 1;

		switch (getClassObj().getName().toUpperCase()) {
		case "WARRIOR":
			return war_softcaps[level];
		case "CLERIC":
		case "BARD":
		case "MONK":
			return clrbrdmnk_softcaps[level];
		case "PALADIN":
		case "SHADOWKNIGHT":
			return palshd_softcaps[level];
		case "RANGER":
			return rng_softcaps[level];
		case "DRUID":
			return dru_softcaps[level];
		case "ROGUE":
		case "SHAMAN":
		case "BEASTLORD":
		case "BERSERKER":
			return rogshmbstber_softcaps[level];
		case "NECROMANCER":
		case "WIZARD":
		case "MAGICIAN":
		case "ENCHANTER":
			return necwizmagenc_softcaps[level];
		default:
			return 350;
		}
	}

	private int getClassRaceACBonus() {
		int ac_bonus = 0;
		int level = getMentorLevel();
		
		if (this.getClassObj() == null)
			return 0;
		
		if (getClassObj().getName().equals("MONK")) 
		{
			int hardcap = 30;
			int softcap = 14;
			if (level > 99) {
				hardcap = 58;
				softcap = 35;
			}
			else if (level > 94) {
				hardcap = 57;
				softcap = 34;
			}
			else if (level > 89) {
				hardcap = 56;
				softcap = 33;
			}
			else if (level > 84) {
				hardcap = 55;
				softcap = 32;
			}
			else if (level > 79) {
				hardcap = 54;
				softcap = 31;
			}
			else if (level > 74) {
				hardcap = 53;
				softcap = 30;
			}
			else if (level > 69) {
				hardcap = 53;
				softcap = 28;
			}
			else if (level > 64) {
				hardcap = 53;
				softcap = 26;
			}
			else if (level > 63) {
				hardcap = 50;
				softcap = 24;
			}
			else if (level > 61) {
				hardcap = 47;
				softcap = 24;
			}
			else if (level > 59) {
				hardcap = 45;
				softcap = 24;
			}
			else if (level > 54) {
				hardcap = 40;
				softcap = 20;
			}
			else if (level > 50) {
				hardcap = 38;
				softcap = 18;
			}
			else if (level > 44) {
				hardcap = 36;
				softcap = 17;
			}
			else if (level > 29) {
				hardcap = 34;
				softcap = 16;
			}
			else if (level > 14) {
				hardcap = 32;
				softcap = 15;
			}
			
			//TODO Item Weight
			int weight = 1;
			if (weight < hardcap - 1) {
				int temp = level + 5;
				if (weight > softcap) {
					double redux = (weight - softcap) * 6.66667;
					redux = (100.0 - Math.min(100.0, redux)) * 0.01;
					temp = Math.max(0, (int)(temp * redux));
				}
				ac_bonus = (4 * temp) / 3;
			}
			else if (weight > hardcap + 1) {
				int temp = level + 5;
				double multiplier = Math.min(1.0, (weight - (hardcap - 10.0)) / 100.0);
				temp = (4 * temp) / 3;
				ac_bonus -= (int)(temp * multiplier);
			}
		}

		if (getClassObj().getName().equals("ROGUE")) {
			int level_scaler = level - 26;
			if (getAgility() < 80)
				ac_bonus = level_scaler / 4;
			else if (getAgility() < 85)
				ac_bonus = (level_scaler * 2) / 4;
			else if (getAgility() < 90)
				ac_bonus = (level_scaler * 3) / 4;
			else if (getAgility() < 100)
				ac_bonus = (level_scaler * 4) / 4;
			else if (getAgility() >= 100)
				ac_bonus = (level_scaler * 5) / 4;
			if (ac_bonus > 12)
				ac_bonus = 12;
		}

		if (getClassObj().getName().equals("BEASTLORD")) {
			int level_scaler = level - 6;
			if (getAgility() < 80)
				ac_bonus = level_scaler / 5;
			else if (getAgility() < 85)
				ac_bonus = (level_scaler * 2) / 5;
			else if (getAgility() < 90)
				ac_bonus = (level_scaler * 3) / 5;
			else if (getAgility() < 100)
				ac_bonus = (level_scaler * 4) / 5;
			else if (getAgility() >= 100)
				ac_bonus = (level_scaler * 5) / 5;
			if (ac_bonus > 16)
				ac_bonus = 16;
		}

		// racial benefit
		if (getRace() != null && getRace().isArmouredSkin())
			ac_bonus += MathUtils.clamp(level, 10, 35);
			
		//if (GetRace() == IKSAR)
		//	ac_bonus += EQEmu::Clamp(static_cast<int>(level), 10, 35);

		return ac_bonus;
	}

	@Override
	public int getSkillDmgTaken(SkillType skillType) {
		int skilldmg_mod = 0;

		if (skilldmg_mod < -100)
			skilldmg_mod = -100;

		return skilldmg_mod;
	}

	@Override
	public int getFcDamageAmtIncoming(ISoliniaLivingEntity caster, int spell_id, boolean use_skill, SkillType skillType) {
		// Used to check focus derived from SE_FcDamageAmtIncoming which adds direct
		// damage to Spells or Skill based attacks.
		int dmg = 0;
		return dmg;
	}

	@Override
	public int getActSpellDamage(ISoliniaSpell soliniaSpell, int value, SpellEffect spellEffect,
			ISoliniaLivingEntity target) {
		if (SpellUtils.getSpellTargetType(soliniaSpell.getTargettype()).equals(SpellTargetType.Self))
			return value;

		if (getClassObj() == null)
			return value;

		boolean critical = false;
		int value_BaseEffect = 0;
		value_BaseEffect = value;
		int chance = 0;

		// TODO Focus effects
		value_BaseEffect = value + (value * getFocusEffect(FocusEffect.FcBaseEffects, soliniaSpell) / 100);

		// TODO Harm Touch Scaling
		if ((soliniaSpell.getName().startsWith("Harm Touch")) && getEffectiveLevel(true) > 40) {
			value_BaseEffect += (getEffectiveLevel(true) - 40) * 20;
		}

		chance = 0;

		// TODO take into account item,spell,aa bonuses
		if (isPlayer()) {
			chance += getAABonuses(SpellEffectType.CriticalSpellChance);
		}

		// TODO Items/aabonuses
		if (chance > 0 || (getClassObj().getName().equals("WIZARD") && getEffectiveLevel(true) >= 12)) {

			int ratio = 100;

			// TODO Harm Touch

			// TODO Crit Chance Overrides

			if (MathUtils.RandomBetween(0, 100) < ratio) {
				critical = true;
				if (isPlayer()) {
					ratio += getAABonusesTuple(SpellEffectType.SpellCritDmgIncrease).b();
				}
				// TODO add ratio bonuses from spells, aas
			} else if (getClassObj().getName().equals("WIZARD")) {
				if ((getEffectiveLevel(true) >= 12) && MathUtils.RandomBetween(0, 100) < 7) {
					ratio += MathUtils.RandomBetween(20, 70);
					critical = true;
				}
			}

			if (critical) {

				value = value_BaseEffect * ratio / 100;

				value += value_BaseEffect * getFocusEffect(FocusEffect.ImprovedDamage, soliniaSpell) / 100;
				value += value_BaseEffect * getFocusEffect(FocusEffect.ImprovedDamage2, soliniaSpell) / 100;
				value += (int) (value_BaseEffect * getFocusEffect(FocusEffect.FcDamagePctCrit, soliniaSpell) / 100)
						* ratio / 100;

				// TODO Vulnerabilities

				// TODO spell dmg level restriction
				// TODO NPC Spell Scale

				if (isPlayer()) {
					getBukkitLivingEntity().sendMessage("* You critical blast for " + value);
				}

				return value;
			}
		}

		// Non Crtical Hit Calculation pathway
		value = value_BaseEffect;

		value += value_BaseEffect * getFocusEffect(FocusEffect.ImprovedDamage, soliniaSpell) / 100;
		value += value_BaseEffect * getFocusEffect(FocusEffect.ImprovedDamage2, soliniaSpell) / 100;

		value += value_BaseEffect * getFocusEffect(FocusEffect.FcDamagePctCrit, soliniaSpell) / 100;

		// TODO Vulnerabilities

		value -= getFocusEffect(FocusEffect.FcDamageAmtCrit, soliniaSpell);

		value -= getFocusEffect(FocusEffect.FcDamageAmt, soliniaSpell);
		value -= getFocusEffect(FocusEffect.FcDamageAmt2, soliniaSpell);

		// TODO SPell damage lvl restriction
		// TODO NPC Spell Scale

		return value;
	}

	@Override
	public int getActSpellCasttime(ISoliniaSpell spell, int casttime) {
		int cast_reducer = 0;
		cast_reducer += getFocusEffect(FocusEffect.SpellHaste, spell);

		// this function loops through the effects of spell_id many times
		// could easily be consolidated.

		if (getClassObj() != null)
			if (getEffectiveLevel(true) >= 51 && casttime >= 3000 && !spell.isBeneficial()
					&& (getClassObj().getName().equals("SHADOWKNIGHT") || getClassObj().getName().equals("RANGER")
							|| getClassObj().getName().equals("PALADIN")
							|| getClassObj().getName().equals("BEASTLORD")))
				cast_reducer += (getEffectiveLevel(true) - 50) * 3;

		// LIVE AA SpellCastingDeftness, QuickBuff, QuickSummoning, QuickEvacuation,
		// QuickDamage

		// TODO MAX Reducer for cast time

		casttime = (casttime * (100 - cast_reducer) / 100);

		if (casttime < 1)
			return 1;

		return casttime;
	}

	@Override
	public int getActSpellHealing(ISoliniaSpell soliniaSpell, int value) {
		if (getClassObj() == null)
			return value;

		int value_BaseEffect = 0;
		int chance = 0;
		int modifier = 1;
		boolean critical = false;

		// TODO FOcus effects

		value_BaseEffect = value + (value * getFocusEffect(FocusEffect.FcBaseEffects, soliniaSpell) / 100);

		value = value_BaseEffect;

		// TODO FOcus effects
		value += (int) (value_BaseEffect * getFocusEffect(FocusEffect.ImprovedHeal, soliniaSpell) / 100);

		// Instant Heals
		if (soliniaSpell.getBuffduration() < 1) {

			// TODO Items/aabonuses
			if (isPlayer()) {
				chance += getAABonuses(SpellEffectType.CriticalHealChance);
			}

			// TODO FOcuses

			if (chance > -1 && (MathUtils.RandomBetween(0, 100) < chance)) {
				critical = true;
				modifier = 2;
			}

			value *= modifier;

			value += getFocusEffect(FocusEffect.FcHealAmtCrit, soliniaSpell) * modifier;
			value += getFocusEffect(FocusEffect.FcHealAmt, soliniaSpell);

			// TODO No heal items

			// TODO NPC Heal Scale

			if (critical) {
				if (isPlayer())
					getBukkitLivingEntity().sendMessage("* You perform an exception heal for " + value);
			}

			return value;
		} else {
			// Heal over time spells. [Heal Rate and Additional Healing effects do not
			// increase this value]
			// TODO Item bonuses

			// TODO FOcuses

			// TOOD Spell Bonuses

			if (chance > -1 && MathUtils.RandomBetween(0, 100) < chance)
				value *= 2;
		}

		// TODO Npc Heal Scale

		return value;
	}

	@Override
	public int getRune() {
		return SpellUtils.getActiveSpellEffectsRemainingValue(this.getBukkitLivingEntity(), SpellEffectType.Rune);
	}

	private void removeActiveSpellsWithEffectType(SpellEffectType spellEffectType) {
		for (SoliniaActiveSpell activeSpell : getActiveSpells()) {
			for (ActiveSpellEffect effect : activeSpell.getActiveSpellEffects()) {
				if (effect.getSpellEffectType().equals(spellEffectType)) {
					try {
						StateManager.getInstance().getEntityManager().removeSpellEffectsOfSpellId(
								getBukkitLivingEntity().getUniqueId(), activeSpell.getSpellId(), false, false);
					} catch (CoreStateInitException e) {

					}
					break;
				}
			}
		}
	}

	@Override
	public int reduceAndRemoveRunesAndReturnLeftover(int damage) {
		int dmgLeft = damage;
		List<Integer> removeSpells = new ArrayList<Integer>();
		try {
			for (SoliniaActiveSpell spell : StateManager.getInstance().getEntityManager()
					.getActiveEntitySpells(getBukkitLivingEntity()).getActiveSpells()) {
				if (!spell.getSpell().getSpellEffectTypes().contains(SpellEffectType.Rune))
					continue;

				for (ActiveSpellEffect effect : spell.getActiveSpellEffects()) {
					if (!(effect.getSpellEffectType().equals(SpellEffectType.Rune)))
						continue;

					if (dmgLeft <= 0)
						break;

					if (effect.getRemainingValue() <= dmgLeft) {
						dmgLeft -= effect.getRemainingValue();
						effect.setRemainingValue(0);
						removeSpells.add(spell.getSpellId());
					} else {
						effect.setRemainingValue(effect.getRemainingValue() - dmgLeft);
						dmgLeft = 0;
						break;
					}
				}
			}

			for (Integer spellId : removeSpells) {
				StateManager.getInstance().getEntityManager()
						.removeSpellEffectsOfSpellId(getBukkitLivingEntity().getUniqueId(), spellId, false, false);

			}
		} catch (CoreStateInitException e) {
			// ignore and return full amount
		}

		return dmgLeft;
	}

	@Override
	public void doMeleeSkillAttackDmg(LivingEntity other, int weapon_damage, SkillType skillinuse, int chance_mod,
			int focus, boolean canRiposte, int reuseTime) {
		try {
			if (!canDoSpecialAttack(other)) return;
			 
			ISoliniaLivingEntity solOther = SoliniaLivingEntityAdapter.Adapt(other);
			if (solOther == null)
				return;

			if (skillinuse == SkillType.Begging)
				skillinuse = SkillType.Offense;

			int damage = 0;
			int hate = 0;
			if (hate == 0 && weapon_damage > 1)
				hate = weapon_damage;

			if (weapon_damage > 0) {
				if (focus > 0) // From FcBaseEffects
					weapon_damage += weapon_damage * focus / 100;

				if (skillinuse == SkillType.Bash) 
					if (isPlayer()) 
					{
						ItemStack item = this.getEquipment().getItemInOffHand();
						if (item != null) 
						{ 
							try { 
								ISoliniaItem solItem = SoliniaItemAdapter.Adapt(item); 
								if (solItem != null) 
								{ 
									if (item.getType().equals(Material.SHIELD)) 
									{ 
										hate += solItem.getAC(); 
									} 
									
									hate = hate * (100 + GetFuriousBash(solItem.getFocusEffectId())) / 100; 
									
								} 
							} catch (SoliniaItemException si) 
							{
									
							} 
						} 
					} 
				
				
				DamageHitInfo my_hit = new DamageHitInfo();
				my_hit.base_damage = weapon_damage;
				my_hit.min_damage = 0;
				my_hit.damage_done = 1;

				my_hit.skill = skillinuse;
				my_hit.offense = offense(my_hit.skill);
				my_hit.tohit = getTotalToHit(my_hit.skill, chance_mod);
				// slot range exclude ripe etc ...

				if (this.getClassObj() != null && this.getClassObj().canRiposte())
					my_hit.hand = InventorySlot.Range;
				else
					my_hit.hand = InventorySlot.Primary;

				if (isNPC())
				{
					ISoliniaNPC npc = this.getNPC();
					if (npc != null)
						my_hit.min_damage = 1;
				}

				doAttack(solOther, my_hit);
				damage = my_hit.damage_done;
			} else {
				damage = Utils.DMG_INVULNERABLE;
			}

			boolean CanSkillProc = true;
			if (skillinuse == SkillType.Offense) { // Hack to allow damage to display.
				skillinuse = SkillType.TigerClaw; // 'strike' your opponent - Arbitrary choice for message.
				CanSkillProc = false; // Disable skill procs
			}

			solOther.addToHateList(this.getBukkitLivingEntity().getUniqueId(), hate, true);
			/*
			 * skill attack proc AAs) if (damage > 0 && aabonuses.SkillAttackProc[0] &&
			 * aabonuses.SkillAttackProc[1] == skillinuse &&
			 * IsValidSpell(aabonuses.SkillAttackProc[2])) { float chance =
			 * aabonuses.SkillAttackProc[0] / 1000.0f; if (zone->random.Roll(chance))
			 * SpellFinished(aabonuses.SkillAttackProc[2], other, EQEmu::CastingSlot::Item,
			 * 0, -1, spells[aabonuses.SkillAttackProc[2]].ResistDiff); }
			 */

			double newHp = solOther.setHPChange(damage * -1, this.getBukkitLivingEntity());
			if (newHp <= 0D)
				this.removeAggro(other.getUniqueId());


			if (this.getBukkitLivingEntity().isDead())
				return;

			/*
			 * skill procs if (CanSkillProc && HasSkillProcs()) TrySkillProc(other,
			 * skillinuse, ReuseTime);
			 * 
			 * if (CanSkillProc && (damage > 0) && HasSkillProcSuccess())
			 * TrySkillProc(other, skillinuse, ReuseTime, true);
			 */
		} catch (CoreStateInitException e) {

		}
	}

	@Override
	public boolean canDoSpecialAttack(LivingEntity other) {
		//Make sure everything is valid before doing any attacks.
		if (other == null) {
			return false;
		}

		if (other.isDead())
			return false;
		
		if (other.isInvulnerable())
			return false;
		
		// TODO check things like divine aura
		return true;
	}
	
	@Override
	public boolean DivineAura()
	{
		for (ActiveSpellEffect effect : SpellUtils.getActiveSpellEffects(getBukkitLivingEntity(),
				SpellEffectType.DivineAura)) {
			return true;
		}
		
		return false;
	}

	@Override
	public boolean isInvulnerable() {
		for (ActiveSpellEffect effect : SpellUtils.getActiveSpellEffects(getBukkitLivingEntity(),
				SpellEffectType.DivineAura)) {
			return true;
		}
		
		if (this.getBukkitLivingEntity() != null)
			if (EntityUtils.IsInvulnerable(this.getBukkitLivingEntity()))
				return true;
		
		return false;
	}

	@Override
	public boolean isRooted() {
		for (ActiveSpellEffect effect : SpellUtils.getActiveSpellEffects(getBukkitLivingEntity(), SpellEffectType.Root)) {
			return true;
		}
		return false;
	}

	@Override
	public int countDispellableBuffs() {
		int count = 0;
		try {
			for (SoliniaActiveSpell activeSpell : StateManager.getInstance().getEntityManager()
					.getActiveEntitySpells(getBukkitLivingEntity()).getActiveSpells()) {
				if (activeSpell.getSpell().isBuffSpell())
					count++;
			}
		} catch (CoreStateInitException e) {
		}
		return count;
	}

	@Override
	public Collection<SoliniaActiveSpell> getActiveSpells() {
		try {
			return StateManager.getInstance().getEntityManager().getActiveEntitySpells(getBukkitLivingEntity())
					.getActiveSpells();
		} catch (CoreStateInitException e) {
			return new ArrayList<SoliniaActiveSpell>();
		}
	}

	@Override
	public int getInstrumentMod(ISoliniaSpell spell) {
		if (getClassObj() == null || !getClassObj().getName().equals("BARD"))
			return 10;

		int effectmod = 10;
		int effectmodcap = 0;
		boolean nocap = false;
		effectmodcap = SkillUtils.getBaseInstrumentSoftCap();

		// TODO Spell and AA bonuses
		switch (SkillUtils.getSkillType(spell.getSkill())) {
		case PercussionInstruments:
			if (getTotalItemSkillMod(SkillType.PercussionInstruments) == 0)
				effectmod = 10;
			else if (getSkill(SkillType.PercussionInstruments) == 0)
				effectmod = 10;
			else
				effectmod = getTotalItemSkillMod(SkillType.PercussionInstruments);
			break;
		case StringedInstruments:
			if (getTotalItemSkillMod(SkillType.StringedInstruments) == 0)
				effectmod = 10;
			else if (getSkill(SkillType.StringedInstruments) == 0)
				effectmod = 10;
			else
				effectmod = getTotalItemSkillMod(SkillType.StringedInstruments);
			break;
		case WindInstruments:
			if (getTotalItemSkillMod(SkillType.WindInstruments) == 0)
				effectmod = 10;
			else if (getSkill(SkillType.WindInstruments) == 0)
				effectmod = 10;
			else
				effectmod = getTotalItemSkillMod(SkillType.WindInstruments);
			break;
		case BrassInstruments:
			if (getTotalItemSkillMod(SkillType.BrassInstruments) == 0)
				effectmod = 10;
			else if (getSkill(SkillType.BrassInstruments) == 0)
				effectmod = 10;
			else
				effectmod = getTotalItemSkillMod(SkillType.BrassInstruments);
			break;
		case Singing:
			if (getTotalItemSkillMod(SkillType.Singing) == 0)
				effectmod = 10;
			else
				effectmod = getTotalItemSkillMod(SkillType.Singing);
			break;
		default:
			effectmod = 10;
			return effectmod;
		}

		// TODO effect mod cap

		if (effectmod < 10)
			effectmod = 10;
		if (!nocap && effectmod > effectmodcap)
			effectmod = effectmodcap;

		return effectmod;
	}

	@Override
	public LivingEntity getAttackTarget() {
		if (this.getBukkitLivingEntity() == null)
			return null;

		if (this.getBukkitLivingEntity().isDead())
			return null;

		if (this.getBukkitLivingEntity() instanceof Creature) {
			return ((Creature) this.getBukkitLivingEntity()).getTarget();
		}

		return null;
	}

	@Override
	public void setAttackTarget(LivingEntity entity) {
		if (this.getBukkitLivingEntity() instanceof ArmorStand || entity instanceof ArmorStand)
			return;
		
		if (this.getBukkitLivingEntity() == null)
			return;

		if (this.getBukkitLivingEntity().isDead()) {
			return;
		}
		
		if (entity instanceof Player)
			if (((Player)entity).getGameMode() != GameMode.SURVIVAL)
				return;

		if (entity != null && (entity.isDead() || entity.isInvulnerable())) {
			if (this.getBukkitLivingEntity() instanceof Creature) {
				this.getBukkitLivingEntity().setLastDamageCause(null);
				
				((Creature) this.getBukkitLivingEntity()).setTarget(null);
			}
			return;
		}

		if (this.getBukkitLivingEntity() instanceof Creature) {
			if (entity != null && (!this.hasHate() || !this.isInHateList(entity.getUniqueId()))) {
				this.addToHateList(entity.getUniqueId(), 1, true);
			}
			
			if (entity == null)
				this.getBukkitLivingEntity().setLastDamageCause(null);
			((Creature) this.getBukkitLivingEntity()).setTarget(entity);
		}
	}

	@Override
	public void doCheckForEnemies() {
		if (isPlayer())
			return;

		if (this.getNpcid() < 1)
			return;
		
		if (this.isMezzed() || this.isFeared() || this.isStunned())
			return;

		if (getBukkitLivingEntity().isDead())
			return;

		if (!(getBukkitLivingEntity() instanceof Creature))
			return;

		if (doCheckForDespawn() == true)
			return;

		if (((Creature) getBukkitLivingEntity()).getTarget() != null && ((Creature) getBukkitLivingEntity()).getTarget()
				.getLocation().distance(this.getBukkitLivingEntity().getLocation()) < 150)
		{
			if (!this.hasHate())
			{
				this.say("Hmm, he must have gone...");
				this.setAttackTarget(null);
				//this.resetPosition(true);
			}
			
			return;
		}

		// Go for hate list first
		if (checkHateTargets() == true)
			return;

		if (this.isCurrentlyNPCPet() == true)
			return;

		try {
			ISoliniaNPC npc = getNPC();
			if (npc.getFactionid() == 0)
				return;

			ISoliniaFaction faction = StateManager.getInstance().getConfigurationManager()
					.getFaction(npc.getFactionid());
			if (faction.getName().equals("NEUTRAL"))
				return;

			List<Integer> hatedFactions = new ArrayList<Integer>();
			if (faction.getName().equals("KOS")) {
				for (ISoliniaFaction targetFaction : StateManager.getInstance().getConfigurationManager()
						.getFactions()) {
					if (targetFaction.getId() != faction.getId())
						hatedFactions.add(((Integer) targetFaction.getId()));
				}
			} else {
				for (FactionStandingEntry factionEntry : faction.getFactionEntries()) {
					if (factionEntry.getValue() != -1500)
						continue;

					hatedFactions.add(((Integer) factionEntry.getFactionId()));
				}

				// then add KOS on top
				for (ISoliniaFaction targetFaction : StateManager.getInstance().getConfigurationManager()
						.getFactions()) {
					if (targetFaction.getName().equals("KOS"))
						hatedFactions.add(((Integer) targetFaction.getId()));
				}
			}

			for (Entity entity : getBukkitLivingEntity().getNearbyEntities(10, 10, 10)) {
				if (entity.isDead())
					continue;

				if (!(entity instanceof Player)) {
					if (!npc.isGuard() && !faction.getName().equals("KOS"))
						continue;

					if (hatedFactions.size() == 0)
						continue;

					try {
						if (!(entity instanceof LivingEntity))
							continue;

						// NPC VS NPC

						LivingEntity le = (LivingEntity) entity;
						ISoliniaLivingEntity solEntity = SoliniaLivingEntityAdapter.Adapt(le);

						if (!solEntity.isNPC())
							continue;

						ISoliniaNPC targetNpc = StateManager.getInstance().getConfigurationManager()
								.getNPC(solEntity.getNpcid());
						if (targetNpc.getFactionid() < 1) {
							continue;
						}

						if (hatedFactions.contains((Integer) targetNpc.getFactionid())) {
							if (RaycastUtils.isEntityInLineOfSight(this,solEntity, true)) {
								addToHateList(le.getUniqueId(), 1, true);
								return;
							}
						}
					} catch (Exception e) {

						System.out.println(npc.getName() + " " + npc.getFactionid() + " " + e.getMessage());
						e.printStackTrace();
					}

				} else {
					// NPC VS PLAYER
					Player player = (Player) entity;
					ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);

					if (faction.getName().equals("KOS") && solPlayer != null) {
						if (RaycastUtils.isEntityInLineOfSight(this,solPlayer.getSoliniaLivingEntity(), true)) {
							addToHateList(player.getUniqueId(), 1, true);
							return;
						}
					}
					
					PlayerFactionEntry factionEntry = solPlayer.getFactionEntry(npc.getFactionid());
					if (factionEntry != null && solPlayer != null) {
						switch (EntityUtils.getFactionStandingType(factionEntry.getValueWithEffectsOnEntityAndBase(this.getBukkitLivingEntity(), player))) {
						case FACTION_THREATENLY:
						case FACTION_SCOWLS:
							if (RaycastUtils.isEntityInLineOfSight(this,solPlayer.getSoliniaLivingEntity(), true)) {
								addToHateList(player.getUniqueId(), 1, true);
								return;
							} else {
								continue;
							}
						default:
							continue;
						}
					} else {
					}
				}
			}

		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//this.resetPosition(true);
	}

	@Override
	public boolean doCheckForDespawn() {
		if (despawnIfWrongTime())
			return true;
		
		// Pet cleanup
		if (this.getBukkitLivingEntity() != null && this.getBukkitLivingEntity() instanceof Creature)
		{
			try
			{
				if (this.getActiveMob().getOwner() != null && this.getActiveMob().getOwner().isPresent())
				if (this.getOwnerEntity() == null || this.getOwnerEntity().isDead() || StateManager.getInstance().getEntityManager().getPet(this.getOwnerEntity().getUniqueId()) == null)
				{
					EntityUtils.RemoveEntity(this.getBukkitLivingEntity(), "doCheckForDespawn");
					return true;
				}
			} catch (CoreStateInitException e)
			{
				
			}
		}

		return false;
	}
	
	private boolean isRoamer() {
		if (this.isCurrentlyNPCPet())
			return false;
		
		if (EntityUtils.isLivingEntityNPC(this.getBukkitLivingEntity())) {
			
			try {
				ISoliniaLivingEntity solEntity = SoliniaLivingEntityAdapter.Adapt(this.getBukkitLivingEntity());
				ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(solEntity.getNpcid());
				return npc.isRoamer();
			} catch (CoreStateInitException e) {

			}
		}
		
		return false;
	}

	private boolean despawnIfWrongTime() {
		if (this.isCurrentlyNPCPet())
			return false;

		if (EntityUtils.isLivingEntityNPC(this.getBukkitLivingEntity())) {
			
			try {
				ISoliniaLivingEntity solEntity = SoliniaLivingEntityAdapter.Adapt(this.getBukkitLivingEntity());
				ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(solEntity.getNpcid());
				if (!TimeUtils.IsTimeRangeActive(this.getBukkitLivingEntity().getWorld(), npc.getTimefrom(), npc.getTimeto())) {
					EntityUtils.RemoveEntity(this.getBukkitLivingEntity(), "DESPAWNIFDAY");
					return true;
				}
			} catch (CoreStateInitException e) {

			}
		}

		return false;
	}

	@Override
	public SkillType getLanguage() {
		if (isNPC()) {
			try {
				ISoliniaNPC npc = getNPC();
				if (npc.getRaceid() > 0) {
					ISoliniaRace race = StateManager.getInstance().getConfigurationManager().getRace(npc.getRaceid());
					return race.getLanguage();
				}
			} catch (CoreStateInitException e) {
				//
			}
		}

		return SkillType.UnknownTongue;
	}

	@Override
	public String getName() {
		if (getBukkitLivingEntity() == null)
			return "";
		
		if (getBukkitLivingEntity().getCustomName() != null)
		return getBukkitLivingEntity().getCustomName();
		
		if (getBukkitLivingEntity().getName() == null)
			return "";
		
		return getBukkitLivingEntity().getName();
	}

	@Override
	public boolean isSpeaksAllLanguages() {
		if (isNPC()) {
			ISoliniaNPC npc = getNPC();
			return npc.isSpeaksAllLanguages();
		}

		return false;
	}

	@Override
	public void setSpeaksAllLanguages(boolean speaksAllLanguages) {
		if (isNPC()) {
			ISoliniaNPC npc = getNPC();
			if(npc == null)
				return;
			
			npc.setSpeaksAllLanguages(speaksAllLanguages);
		}
	}

	@Override
	public int hasDeathSave() {
		List<ActiveSpellEffect> spellEffects = SpellUtils.getActiveSpellEffects(getBukkitLivingEntity(),
				SpellEffectType.DivineSave);
		List<ActiveSpellEffect> spellEffects2 = SpellUtils.getActiveSpellEffects(getBukkitLivingEntity(),
				SpellEffectType.DeathSave);
		return spellEffects.size() + spellEffects2.size();
	}

	@Override
	public void removeDeathSaves() {
		List<Integer> removeSpells = new ArrayList<Integer>();
		try {
			for (SoliniaActiveSpell spell : StateManager.getInstance().getEntityManager()
					.getActiveEntitySpells(getBukkitLivingEntity()).getActiveSpells()) {
				if (!spell.getSpell().getSpellEffectTypes().contains(SpellEffectType.DivineSave)
						&& !spell.getSpell().getSpellEffectTypes().contains(SpellEffectType.DeathSave))
					continue;

				for (ActiveSpellEffect effect : spell.getActiveSpellEffects()) {
					if (!(effect.getSpellEffectType().equals(SpellEffectType.Rune))
							&& !(effect.getSpellEffectType().equals(SpellEffectType.DeathSave)))
						continue;

					removeSpells.add(spell.getSpellId());
				}
			}

			for (Integer spellId : removeSpells) {
				StateManager.getInstance().getEntityManager()
						.removeSpellEffectsOfSpellId(getBukkitLivingEntity().getUniqueId(), spellId, false, false);

			}
		} catch (CoreStateInitException e) {
			// ignore and return full amount
		}
	}

	@Override
	public boolean isBehindEntity(LivingEntity livingEntity) {
		Vector targetDirection = livingEntity.getLocation().getDirection();
		Vector myDirection = getBukkitLivingEntity().getLocation().getDirection();
		// determine if the dot product between the vectors is greater than 0
		if (myDirection.dot(targetDirection) > 0) {
			return true;
		}

		return false;
	}

	@Override
	public double getItemHp() {
		int total = 0;

		if (!isNPC() && !isPlayer())
			return 0;
		
		boolean allowNpc = false;
		// only allow hp from gear if heroic/raid
		if (this.isNPC())
		{
			ISoliniaNPC npc = this.getNPC();
			if (npc.isHeroic() || npc.isRaidboss() || npc.isRaidheroic())
				allowNpc = true;
		}
		if  (this.IsCorePet())
			allowNpc = true;

		if (isPlayer() || allowNpc)
		for (ISoliniaItem item : getEquippedSoliniaItems(true)) {
			if (item.getHp() > 0) {
				total += item.getHp();
			}
		}

		return total;
	}

	@Override
	public double getItemMana() {
		int total = 0;

		if (!isNPC() && !isPlayer())
			return 0;

		boolean allowNpc = false;
		// only allow hp from gear if heroic/raid
		if (this.isNPC())
		{
			ISoliniaNPC npc = this.getNPC();
			if (npc.isHeroic() || npc.isRaidboss() || npc.isRaidheroic())
				allowNpc = true;
		}
		if  (this.IsCorePet())
			allowNpc = true;

		if (isPlayer() || allowNpc)
		for (ISoliniaItem item : getEquippedSoliniaItems(true)) {
			if (item.getMana() > 0) {
				total += item.getMana();
			}
		}

		return total;
	}

	@Override
	public int getTotalItemSkillMod(SkillType skilltype) {
		int total = 0;

		try {
			for (ISoliniaItem item : getEquippedSoliniaItems(false)) {
				if (item != null && item.getSkillModType().equals(skilltype))
					total += item.getSkillModValue();
				if (item != null && item.getSkillModType2().equals(skilltype))
					total += item.getSkillModValue2();
				if (item != null && item.getSkillModType3().equals(skilltype))
					total += item.getSkillModValue3();
				if (item != null && item.getSkillModType4().equals(skilltype))
					total += item.getSkillModValue4();
			}
			return total;
		} catch (Exception e) {
			e.printStackTrace();
			return total;
		}
	}

	@Override
	public SoliniaWorld getSoliniaWorld() {
		try {
			return StateManager.getInstance().getConfigurationManager()
					.getWorld(this.getBukkitLivingEntity().getWorld().getName().toUpperCase());
		} catch (CoreStateInitException e) {
			return null;
		}
	}

	@Override
	public org.bukkit.ChatColor getLevelCon(int mylevel) {
		int iOtherLevel = getMentorLevel();
		return EntityUtils.getLevelCon(mylevel,iOtherLevel);
	}

	@Override
	public void PetThink(Player playerOwner) {
		if (this.getBukkitLivingEntity() instanceof Sittable) {
			if (((Sittable) this.getBukkitLivingEntity()).isSitting())
				((Sittable) this.getBukkitLivingEntity()).setSitting(false);
		}
	}

	@Override
	public void PetFastThink(Player playerOwner) {
		if (this.getBukkitLivingEntity() instanceof Creature) {
			if (((Creature) this.getBukkitLivingEntity()).getTarget() == null) {
				List<Entity> nearby = playerOwner.getNearbyEntities(10, 10, 10);

				// Attack mobs that have my master targetted primary
				for (Entity entity : nearby) {
					if (entity instanceof Player)
						continue;

					if (!(entity instanceof Creature))
						continue;

					if (((Creature) entity).getTarget() == null)
						continue;

					if (((Creature) entity).getTarget().getUniqueId().equals(playerOwner.getUniqueId())) {
						setAttackTarget((Creature) entity);
						return;
					}
				}
			}
		}
	}

	@Override
	public void sendMessage(String message) {
		getBukkitLivingEntity().sendMessage(message);
	}

	@Override
	public Location getLocation() {
		return getBukkitLivingEntity().getLocation();
	}

	@Override
	public boolean canAttackTarget(ISoliniaLivingEntity defender) {
		if (defender == null)
			return false;
		
		if (this.getBukkitLivingEntity() == null)
			return false;
		
		if (defender.getBukkitLivingEntity() == null)
			return false;
		
		if (defender.getBukkitLivingEntity().isDead())
			return false;
		
		if (this.getBukkitLivingEntity().isDead())
			return false;
		
		// IM MEZZED SO CANT DO ANYTHING
		if (isStunned())
			return false;

		// IM STUNNED SO CANT DO ANYTHING
		if (isMezzed())
			return false;

		if (isFeared())
			return false;
		
		// This seems to stop players from attacking players pets
		// unless the pet is set to attack the player
		if (isPlayer() && defender.getBukkitLivingEntity() instanceof Creature && defender.isCurrentlyNPCPet()) {
			Creature creature = (Creature) defender.getBukkitLivingEntity();
			if (creature != null) {
				if (creature.getTarget() == null || !creature.getTarget().equals(getBukkitLivingEntity())) {
					return false;
				}
			} else {
				return false;
			}
		}

		// Both players are in same group
		if (isPlayer() && (defender.getBukkitLivingEntity() instanceof Player || defender.isCurrentlyNPCPet())) {
			try {
				ISoliniaPlayer solAttackerPlayer = SoliniaPlayerAdapter.Adapt((Player) this.getBukkitLivingEntity());

				if (solAttackerPlayer.getGroup() != null) {
					ISoliniaPlayer solDefenderPlayer = null;
					if (defender.isCurrentlyNPCPet()) {
						if (defender.getActiveMob() != null && defender.getOwnerEntity() instanceof Player)
							solDefenderPlayer = SoliniaPlayerAdapter.Adapt((Player) defender.getOwnerEntity());
					} else {
						solDefenderPlayer = SoliniaPlayerAdapter.Adapt((Player) defender.getBukkitLivingEntity());
					}

					if (solDefenderPlayer != null)
						if (solDefenderPlayer.getGroup() != null) {
							if (solAttackerPlayer.getGroup().getId().toString()
									.equals(solDefenderPlayer.getGroup().getId().toString())) {
								return false;
							}
						}
				}

			} catch (CoreStateInitException e) {

			}

		}

		// Player can always attack even if mob is mezzed (it will break mez)
		if (isPlayer())
			return true;

		// Mobs and pets wont attack mezzed creatures
		if (defender.isMezzed()) {
			if (isCurrentlyNPCPet()) {
				setAttackTarget(null);
				say("Stopping attacking master, the target is mesmerized");
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean isMezzed() {
		try {
			Timestamp expiry = StateManager.getInstance().getEntityManager().getMezzed(getBukkitLivingEntity());
			if (expiry != null) {
				return true;
			}

		} catch (CoreStateInitException e) {
			return false;
		}

		return false;
	}
	
	@Override
	public boolean isFeared() {
		return this.hasSpellEffectType(SpellEffectType.Fear);
	}

	@Override
	public boolean isStunned() {
		try {
			Timestamp expiry = StateManager.getInstance().getEntityManager().getStunned(getBukkitLivingEntity());
			if (expiry != null) {
				return true;
			}

		} catch (CoreStateInitException e) {
			return false;
		}

		return false;
	}

	@Override
	public void removeNonCombatSpells() {
		try {
			List<Integer> removeSpells = new ArrayList<Integer>();
			for (SoliniaActiveSpell spell : StateManager.getInstance().getEntityManager()
					.getActiveEntitySpells(getBukkitLivingEntity()).getActiveSpells()) {
				if (	spell.getSpell().getSpellEffectTypes().contains(SpellEffectType.Mez)
						|| spell.getSpell().getSpellEffectTypes().contains(SpellEffectType.NegateIfCombat)
						) {
					if (!removeSpells.contains(spell.getSpell().getId()))
						removeSpells.add(spell.getSpell().getId());
				}
			}
			
			BreakInvis();

			for (Integer spellId : removeSpells) {
				StateManager.getInstance().getEntityManager()
						.removeSpellEffectsOfSpellId(getBukkitLivingEntity().getUniqueId(), spellId, false, true);
			}
		} catch (CoreStateInitException e) {

		}
	}

	@Override
	public void BreakInvis() {
		List<Integer> removeSpells = new ArrayList<Integer>();
		try
		{
		for (SoliniaActiveSpell spell : StateManager.getInstance().getEntityManager()
				.getActiveEntitySpells(getBukkitLivingEntity()).getActiveSpells()) {
			if (spell.getSpell().getSpellEffectTypes().contains(SpellEffectType.InvisVsUndead)
					|| spell.getSpell().getSpellEffectTypes().contains(SpellEffectType.InvisVsUndead2)
					|| spell.getSpell().getSpellEffectTypes().contains(SpellEffectType.Invisibility)
					|| spell.getSpell().getSpellEffectTypes().contains(SpellEffectType.Invisibility2)
					|| spell.getSpell().getSpellEffectTypes().contains(SpellEffectType.InvisVsAnimals)
					|| spell.getSpell().getSpellEffectTypes().contains(SpellEffectType.NegateIfCombat)
					|| spell.getSpell().getSpellEffectTypes().contains(SpellEffectType.ImprovedInvisAnimals)) {
				if (!removeSpells.contains(spell.getSpell().getId()))
					removeSpells.add(spell.getSpell().getId());
			}
		}
		

		for (Integer spellId : removeSpells) {
						StateManager.getInstance().getEntityManager()
								.removeSpellEffectsOfSpellId(getBukkitLivingEntity().getUniqueId(), spellId, false, true);
					}
		
		if (this.getBukkitLivingEntity().hasPotionEffect(PotionEffectType.INVISIBILITY))
			this.getBukkitLivingEntity().removePotionEffect(PotionEffectType.INVISIBILITY);
		} catch (CoreStateInitException e) {

		}

	}

	@Override
	public void StopSinging() {
		try {
			boolean isSinging = StateManager.getInstance().getEntityManager()
					.getEntitySinging(getBukkitLivingEntity().getUniqueId()).isSinging();
			if (isSinging) {
				for (int singingId : StateManager.getInstance().getEntityManager()
						.getEntitySinging(getBukkitLivingEntity().getUniqueId()).getSpellIds())
				{
					ISoliniaSpell spell = StateManager.getInstance().getConfigurationManager().getSpell(singingId);
					StateManager.getInstance().getEntityManager()
							.removeSpellEffectsOfSpellId(getBukkitLivingEntity().getUniqueId(), singingId, true, true);
					ChatUtils.SendHint(getBukkitLivingEntity(), HINT.STOPS_SINGING, getBukkitLivingEntity().getCustomName()+"^"+spell.getName(), true);
					StateManager.getInstance().getEntityManager().getEntitySinging(getBukkitLivingEntity().getUniqueId()).stopSinging(singingId);
				}
			}
		} catch (CoreStateInitException e) {

		}
	}

	@Override
	public void resetPosition(boolean resetHealth) {
		if (this.getBukkitLivingEntity().isDead())
			return;

		if (!isNPC())
			return;

		if (isCurrentlyNPCPet())
			return;

		if (this.hasHate())
			return;
		
		try
		{
			// Always clear all active spells when resetting
			// EXCEPT SOOTHE/LULL/PACIFY
			List<SpellEffectType> typesToExclude = new ArrayList<SpellEffectType>();
			typesToExclude.add(SpellEffectType.Lull);
			typesToExclude.add(SpellEffectType.Harmony);
			typesToExclude.add(SpellEffectType.ChangeFrenzyRad);
			typesToExclude.add(SpellEffectType.ChangeAggro);
			typesToExclude.add(SpellEffectType.AddFaction);
			StateManager.getInstance().getEntityManager().removeSpellEffectsExcept(this.getBukkitLivingEntity().getUniqueId(), true, true, typesToExclude, false);
		} catch (CoreStateInitException e)
		{
			
		}

		ActiveMob activeMob = MythicMobs.inst().getAPIHelper().getMythicMobInstance(this.getBukkitLivingEntity());
		if (activeMob == null) {
			// Why does this npc exist without a MM entity?
			EntityUtils.RemoveEntity(this.getBukkitLivingEntity(), "RESETPOSITION");
			return;
		}

		if (activeMob.getSpawner() == null) {
			// Why would this have no spawn point?
			// BECAUSE IT MAY HAVE BEEN NPCSPAWNED, SKIP
			return;
		}

		if (resetHealth == true)
		{
			if (this.getBukkitLivingEntity().getHealth() < this.getBukkitLivingEntity()
					.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue())
			{
				this.getBukkitLivingEntity()
						.setHealth(this.getBukkitLivingEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
			}
		}

		if (this.getBukkitLivingEntity().getLocation().distance(BukkitAdapter.adapt(activeMob.getSpawner().getLocation())) < 5)
			return;

		if (!this.isRoamer())
		{
			EntityUtils.teleportSafely(this.getBukkitLivingEntity(),BukkitAdapter.adapt(activeMob.getSpawner().getLocation()));
		}
		else
		{
			/* Throwing exception java.lang.NoSuchMethodError: net.minecraft.server.v1_15_R1.NavigationAbstract.a(DDD)Lnet/minecraft/server/v1_15_R1/PathEntity;
			CraftCreature nmsEntity = ((CraftCreature) this.getBukkitLivingEntity());
	        // Create a path to the location
	        PathEntity path = nmsEntity.getHandle().getNavigation().a(activeMob.getSpawner().getLocation().getX(), activeMob.getSpawner().getLocation().getY(), activeMob.getSpawner().getLocation().getZ());
	        // Move to that path at 'speed' speed.
	        nmsEntity.getHandle().getNavigation().a(path, nmsEntity.getHandle().getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue());
	        */
			EntityUtils.teleportSafely(this.getBukkitLivingEntity(),BukkitAdapter.adapt(activeMob.getSpawner().getLocation()));
		}
		
	}

	@Override
	public Location getSpawnPoint() {
		if (!isNPC())
			return null;

		ActiveMob activeMob = MythicMobs.inst().getAPIHelper().getMythicMobInstance(this.getBukkitLivingEntity());
		if (activeMob == null)
			return null;

		if (activeMob.getSpawner() == null || activeMob.getSpawner().getLocation() == null)
			return null;

		return BukkitAdapter.adapt(activeMob.getSpawner().getLocation());
	}

	@Override
	public ActiveMob getActiveMob() {
		return MythicMobsUtils.getActiveMob(this.getBukkitLivingEntity());
	}

	@Override
	public boolean isCurrentlyNPCPet() {
		if (!this.isNPC())
			return false;

		if (this.getActiveMob() == null) {
			// How can this be?
			EntityUtils.RemoveEntity(this.getBukkitLivingEntity(), "ISCURRENTLYNPCPET");
			return false;
		}

		if (this.getActiveMob().getOwner() != null) {
			if (!this.getActiveMob().getOwner().isPresent())
				return false;

			Entity ownerEntity = Bukkit.getEntity(this.getActiveMob().getOwner().get());
			if (ownerEntity == null)
				return false;

			if (ownerEntity.isDead()) {
				// effectively remove owner
				//this.getActiveMob().setOwner(UUID.randomUUID());
				this.getActiveMob().removeOwner();
				this.getActiveMob().resetTarget();
				return false;
			}

			if (ownerEntity instanceof Player)
				return true;
		}

		return false;
	}

	@Override
	public boolean isCharmed() {
		// TODO Auto-generated method stub
		try {
			return StateManager.getInstance().getEntityManager().hasEntityEffectType(getBukkitLivingEntity(),
					SpellEffectType.Charm);
		} catch (CoreStateInitException e) {
			return false;
		}
	}

	@Override
	public Entity getOwnerEntity() {
		if (!isCurrentlyNPCPet())
			return null;

		return Bukkit.getEntity(this.getActiveMob().getOwner().get());
	}
	
	@Override
	public ISoliniaLivingEntity getOwnerSoliniaLivingEntity() {
		if (!isCurrentlyNPCPet())
			return null;

		try {
			return SoliniaLivingEntityAdapter.Adapt((LivingEntity)(Bukkit.getEntity(this.getActiveMob().getOwner().get())));
		} catch (CoreStateInitException e) {
			return null;
		}
	}

	@Override
	public int getMaxTotalSlots() {
		return Effects.TotalBuffsLimit;
	}

	@Override
	public boolean isImmuneToSpell(ISoliniaSpell spell)
	{
		for(SpellEffect effect : spell.getBaseSpellEffects())
		{
			if (effect.getSpellEffectType().equals(SpellEffectType.Mez) || 
					effect.getSpellEffectType().equals(SpellEffectType.Charm) || 
					effect.getSpellEffectType().equals(SpellEffectType.Fear)
					)
			{
				if (getMentorLevel() > effect.getMax())
					return true;
			}
		}
		
		return false;
	}
	
	@Override
	public boolean isSpecialKOSOrNeutralFaction()
	{
		if (!isNPC())
			return false;
		
		ISoliniaNPC npc = getNPC();
		if (npc == null)
			return false;
		
		if (npc.getFactionid() <= 0)
			return true;

		try
		{
		ISoliniaFaction faction = StateManager.getInstance().getConfigurationManager()
				.getFaction(npc.getFactionid());
		if (faction.getName().equals("NEUTRAL") || faction.getName().toUpperCase().equals("KOS"))
			return true;
		} catch (CoreStateInitException e)
		{
			return true;
		}
		
		return false;
	}
	
	@Override
	public void aIYellForHelp(ISoliniaLivingEntity attacker) {
		try
		{
			if (this.getBukkitLivingEntity() == null)
				return;
			
			if (this.getBukkitLivingEntity().isDead())
				return;
			
			if (!(this.getBukkitLivingEntity() instanceof Creature))
				return;
			
			if (attacker == null || attacker.getBukkitLivingEntity() == null || attacker.getBukkitLivingEntity().isDead())
				return;
	
			if (!isNPC())
				return;
			
			ISoliniaNPC npc = getNPC();
			if (npc == null)
				return;
			
			if (this.isSpecialKOSOrNeutralFaction())
				return;
			
			if (hasAssistHate())
				return;
			
			for(ISoliniaLivingEntity nearbySolEntity : getNPCsInRange(Utils.CALL_FOR_ASSIST_RANGE))
			{
				if (nearbySolEntity == null || nearbySolEntity.getBukkitLivingEntity() == null || nearbySolEntity.getBukkitLivingEntity().isDead())
					continue;
				
				if (!(nearbySolEntity.getBukkitLivingEntity() instanceof Creature))
					continue;
				
				if (nearbySolEntity.getBukkitLivingEntity().getUniqueId().equals(this.getBukkitLivingEntity().getUniqueId()))
					continue;
				
				if ((((Creature)nearbySolEntity.getBukkitLivingEntity()).getTarget()) != null)
					continue;
	
				if (!nearbySolEntity.isSocial())
					continue;
				
				if (nearbySolEntity.isCharmed() || nearbySolEntity.isRooted())
					continue;
				
				if (nearbySolEntity.isSpecialKOSOrNeutralFaction())
					continue;
				
				if (nearbySolEntity.getBukkitLivingEntity().getUniqueId().equals(attacker.getBukkitLivingEntity().getUniqueId()))
					continue;
	
				if (nearbySolEntity.isCurrentlyNPCPet())
					continue;
	
				if (nearbySolEntity.checkAggro(attacker))
					continue;
				
				if (nearbySolEntity.hasSpellEffectType(SpellEffectType.Harmony))
					continue;
				
				// TODO assist cap check here
				
				if (nearbySolEntity.getBukkitLivingEntity().getLocation().distance(this.getBukkitLivingEntity().getLocation()) > Utils.MAX_ENTITY_AGGRORANGE)
					continue;
					
				//if they are in range, make sure we are not green...
				//then jump in if they are our friend
				if (attacker.getLevelCon(nearbySolEntity.getMentorLevel()).equals(ChatColor.GRAY) && nearbySolEntity.getMentorLevel() < 50)
					continue;
				
				if (nearbySolEntity.getNPC() == null)
					continue;
				
				if (nearbySolEntity.getNPC().getFactionid() < 1)
					continue;
				
				boolean useprimfaction = false;
				
				if (nearbySolEntity.getNPC().getFactionid() == this.getNpcid())
					useprimfaction = true;
				
				ISoliniaFaction faction = StateManager.getInstance().getConfigurationManager().getFaction(nearbySolEntity.getNPC().getFactionid());
				if (faction == null)
					continue;
				
				if (useprimfaction == true || 
						!(
								this.getNPCvsNPCReverseFactionCon(nearbySolEntity).equals(FactionStandingType.FACTION_ALLY) ||
								this.getNPCvsNPCReverseFactionCon(nearbySolEntity).equals(FactionStandingType.FACTION_WARMLY) ||
								this.getNPCvsNPCReverseFactionCon(nearbySolEntity).equals(FactionStandingType.FACTION_KINDLY) ||
								this.getNPCvsNPCReverseFactionCon(nearbySolEntity).equals(FactionStandingType.FACTION_AMIABLE)
						)
						)
				{

					//nearbySolEntity.getBukkitLivingEntity().face
					//if (!RaycastUtils.isEntityInLineOfSight(nearbySolEntity.getBukkitLivingEntity(), this.getBukkitLivingEntity()))
					//	continue;
					if (nearbySolEntity.checkLosFN(this, false))
					{
						nearbySolEntity.addToHateList(attacker.getBukkitLivingEntity().getUniqueId(), 25,false);
					}
				}
				
			}
		} catch (CoreStateInitException e)
		{
			
		}
	}
	
	@Override
	public void tryApplySpellOnSelf(int spellId, String requiredWeaponSkillType) {
		this.tryApplySpellOnSelf(spellId, requiredWeaponSkillType, false);
	}

	@Override
	public void tryApplySpellOnSelf(int spellId, String requiredWeaponSkillType, boolean racialPassive) {
		try
		{
			ISoliniaSpell spell = StateManager.getInstance().getConfigurationManager().getSpell(spellId);
			if (spell == null)
				return;
			
			spell.tryApplyOnEntity(getBukkitLivingEntity(), getBukkitLivingEntity(), false, requiredWeaponSkillType, racialPassive);
		} catch (CoreStateInitException e)
		{
			
		}
	}
	
	@Override
	public List<ISoliniaLivingEntity> getNPCsInRange(int iRange)
	{
		List<ISoliniaLivingEntity> entities = new ArrayList<ISoliniaLivingEntity>();
		try
		{
			for(Entity entity : this.getBukkitLivingEntity().getNearbyEntities(iRange,iRange,iRange))
			{
				if (!(entity instanceof LivingEntity))
					continue;
				
				if (entity.getUniqueId() == this.getBukkitLivingEntity().getUniqueId())
					continue;
				
				ISoliniaLivingEntity solEntity = SoliniaLivingEntityAdapter.Adapt((LivingEntity)entity);
				if (!solEntity.isNPC())
					continue;
				
				entities.add(solEntity);
			}
		} catch (CoreStateInitException e)
		{
			
		}
		
		return entities;
	}

	@Override
	public PacketMobVitals toPacketMobVitals(int partyMember, boolean withMana) {
		PacketMobVitals vitals = new PacketMobVitals();
		// the default draw state of the mana bar is FULL mana
		float manaPercent = 100F;
		// try to use last cached value
		if (partyMember == 0)
		{
			try
			{
				if (StateManager.getInstance().getConfigurationManager().getLastSentPlayerManaPercent().get(this.getBukkitLivingEntity().getUniqueId()) != null)
					manaPercent = StateManager.getInstance().getConfigurationManager().getLastSentPlayerManaPercent().get(this.getBukkitLivingEntity().getUniqueId());
					
			} catch (CoreStateInitException e)
			{
				
			}
		}
		if (withMana)
		{
			manaPercent = ((float)getManaRatio())/100F;
			try
			{
				if (partyMember == 0)
					StateManager.getInstance().getConfigurationManager().getLastSentPlayerManaPercent().put(this.getBukkitLivingEntity().getUniqueId(),manaPercent);
			} catch (CoreStateInitException e)
			{
				
			}
		}
		
		vitals.fromData(partyMember, ((float)getHPRatio())/100F, manaPercent, this.getBukkitLivingEntity().getEntityId(), this.getName().replaceAll("\\^", "").replaceAll("\\|",""),this.getMentorLevel(),this.getExperiencePercentage());
		return vitals;
	}

	@Override
	public int getExperiencePercentage() {
		if (!this.isPlayer())
			return 0;
		
		ISoliniaPlayer player = this.getPlayer();
		if (player == null)
			return 0;
		
		return player.getExperiencePercentage();
	}

	@Override
	public void sendHateList(LivingEntity recipient) {
		recipient.sendMessage("HateList:");
		if (recipient instanceof Player) {
			if (this.hasHate())
			for(UUID uuid : this.getHateListUUIDs())
			{
				int hate = this.getHateListAmount(uuid);
				String name = "";
				org.bukkit.entity.Entity entity = Bukkit.getEntity(uuid);
				if (entity != null)
					name = entity.getName();
				recipient.sendMessage("UUID: " + uuid + "(" + name + ") Value: " + hate);
			}
		}
	}
	
	@Override
	public void setEntityTarget(LivingEntity target)
	{
		try
		{
			StateManager.getInstance().getEntityManager().forceSetEntityTarget(this.getBukkitLivingEntity(), target);
		} catch (CoreStateInitException e)
		{
			
		}
	}
	
	@Override
	public void clearTargetsAgainstMe() {
		clearTargetsAgainstMeWithoutEffect(null);
	}
	
	@Override
	public void clearTargetsAgainstMeWithoutEffect(SpellEffectType spellEffectType) {
		try
		{
			StateManager.getInstance().getEntityManager().forceClearTargetsAgainstMeWithoutEffect(this.getBukkitLivingEntity(),spellEffectType);
		} catch (CoreStateInitException e)
		{
			
		}

	}

	@Override
	public LivingEntity getEntityTarget() {
		try
		{
			return StateManager.getInstance().getEntityManager().forceGetEntityTarget(this.getBukkitLivingEntity());
		} catch (CoreStateInitException e)
		{
			
		}
		return null;
	}

	@Override
	public void doCallForAssist(LivingEntity target) {
		// have assist cap

		if (canCallForAssist() && this.hasHate() && !isCharmed() && !hasAssistHate()
			    // && NPCAssistCap() < RuleI(Combat, NPCAssistCap)
				) 
		{
			if (target == null)
				return;
			
			if (target.isDead())
				return;
			
			try
			{
				ISoliniaLivingEntity solTarget = SoliniaLivingEntityAdapter.Adapt(target);
				if (solTarget == null)
					return;
				
				aIYellForHelp(solTarget);
				this.setLastCallForAssist();
			} catch (CoreStateInitException e)
			{
				
			}
		}
	}

	@Override
	public ISoliniaLivingEntity getOwnerOrSelf() {
		if (this.getOwnerEntity() == null)
			return this;
		
		if (!(this.getOwnerEntity() instanceof LivingEntity))
			return this;
		
		try {
			return SoliniaLivingEntityAdapter.Adapt((LivingEntity)this.getOwnerEntity());
		} catch (CoreStateInitException e) {
			return this;
		} 
	}

	@Override
	public boolean checkAggro(ISoliniaLivingEntity attacker) {
		if (attacker == null)
			return false;
		
		if (attacker.getBukkitLivingEntity() == null)
			return false;

		if (attacker.getBukkitLivingEntity().isDead())
			return false;
		
		if (!hasHate())
			return false;
		
		return this.isInHateList(attacker.getBukkitLivingEntity().getUniqueId());
	}
	
	@Override
	public boolean hasHate() {
		try
		{
			return StateManager.getInstance().getEntityManager().hasHate(this.getBukkitLivingEntity().getUniqueId());
		} catch (CoreStateInitException e)
		{
			return false;
		}
	}

	@Override
	public SoliniaActiveSpell getFirstActiveSpellWithSpellEffectType(SpellEffectType type)
	{
		if (this.getBukkitLivingEntity() == null)
			return null;
		
		try
		{
		return StateManager.getInstance().getEntityManager().getFirstActiveSpellOfSpellEffectType(this.getBukkitLivingEntity(),
				type);
		} catch (CoreStateInitException e)
		{
			return null;
		}
	}
	
	@Override
	public boolean hasSpellEffectType(SpellEffectType type) {
		if (this.getBukkitLivingEntity() == null)
			return false;
		
		return EntityUtils.hasSpellEffectActive(this.getBukkitLivingEntity(), type);
	}
	
	@Override
	public boolean canDisarm() {
		if (getNpcid() < 1 && !isPlayer())
			return false;

		Timestamp expiretimestamp = getLastDisarm();
		if (expiretimestamp != null) {
			LocalDateTime datetime = LocalDateTime.now();
			Timestamp nowtimestamp = Timestamp.valueOf(datetime);
			Timestamp mintimestamp = Timestamp.valueOf(expiretimestamp.toLocalDateTime().plus(3, ChronoUnit.SECONDS));

			if (nowtimestamp.before(mintimestamp))
				return false;
		}

		try {
			if (getNpcid() > 0) {
				ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(getNpcid());
				if (npc == null)
					return false;

				return npc.canDisarm();
			}

			if (isPlayer()) {
				ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player) getBukkitLivingEntity());
				if (solplayer == null)
					return false;
				return solplayer.canDisarm();
			}
		} catch (CoreStateInitException e) {
			return false;
		}

		return false;
	}
	
	@Override
	public void tryDisarm(ISoliniaLivingEntity tmob) {
		if (this.getBukkitLivingEntity().isDead())
			return;
		
		if (!this.canDisarm())
		{
			this.getBukkitLivingEntity().sendMessage("You cannot disarm this target right now");
			return;
		}
		
		BreakInvis();
		ISoliniaLivingEntity pmob = this;
		
		if (pmob == null || tmob == null)
			return;
		
		// No disarm on corpses
		if (tmob.getBukkitLivingEntity().isDead())
			return;
		
		// No target
		// Targets don't match (possible hack, but not flagging)
		if (!this.getAttackTarget().equals(tmob.getBukkitLivingEntity()))
		{
			this.getBukkitLivingEntity().sendMessage("You target does not match your attack target for disarm");
			return;
		}
		
		// Too far away
		if (pmob.getLocation().distance(tmob.getLocation()) > 4)
		{
			this.getBukkitLivingEntity().sendMessage("You target for disarm is too far away");
			return;
		}

		// How can we disarm someone if we are feigned.
		if (this.isFeigned())
		{
			this.getBukkitLivingEntity().sendMessage("You cannot disarm when feigned");
			return;
		}
		
		// We can't disarm someone who is feigned.
		if (tmob.isFeignedDeath())
		{
			this.getBukkitLivingEntity().sendMessage("You cannot disarm a feigned target");
			return;
		}

		if (isAttackAllowed(tmob, false)) {
			int p_level = pmob.getMentorLevel();
			if (p_level < 1)
				p_level = 1;
			int t_level = tmob.getMentorLevel();
			if (t_level < 1)
				t_level = 1;

			// We have a disarmable target - sucess or fail, we always aggro the mob
			
			// why
			if (tmob.isNPC()) {
				if (!tmob.checkAggro(pmob)) {
					tmob.addToHateList(pmob.getBukkitLivingEntity().getUniqueId(), 1, false);
				}
				else {
					tmob.addToHateList(pmob.getBukkitLivingEntity().getUniqueId(), 1, false);
				}
			}
			
			int chance = getSkill(SkillType.Disarm); // (1% @ 0 skill) (11% @ 200 skill) - against even con
			chance /= 2;
			chance += 10;
			// Modify chance based on level difference
			float lvl_mod = p_level / t_level;
			chance *= lvl_mod;
			if (chance > 300)
				chance = 300; // max chance of 30%

			tmob.disarm(this, chance);
			return;
		} else {
			this.getBukkitLivingEntity().sendMessage("You are not allowed to attack this target");
		}

		return;
	}

	@Override
	public boolean isAttackAllowed(ISoliniaLivingEntity target)
	{
		return isAttackAllowed(target,false);
	}
	
	@Override
	public boolean isAttackAllowed(ISoliniaLivingEntity target, boolean isSpellAttack)
	{

		int reverse;

		// some special cases
		if(target == null)
			return false;

		if (target.getBukkitLivingEntity() == null)
			return false;
		
		if (target.getBukkitLivingEntity().isDead())
			return false;
		
		if (this.getBukkitLivingEntity() == null)
			return false;
		
		if (this.getBukkitLivingEntity().isDead())
			return false;
		
		if(this.getBukkitLivingEntity().getUniqueId().equals(target.getBukkitLivingEntity().getUniqueId()))	// you can attack yourself
			return false;

		if (target.isInvulnerable())
			return false;
		
		if(target.getSpecialAbility(SpecialAbility.NO_HARM_FROM_CLIENT) > 0){
			return false;
		}

		// can't damage own pet (applies to everthing)
		if (target.isCurrentlyNPCPet() && this.getOwnerSoliniaLivingEntity() != null && this.getOwnerSoliniaLivingEntity().getBukkitLivingEntity().getUniqueId().equals(this.getBukkitLivingEntity().getUniqueId()))
			return false;
		
		return true;
	}

	@Override
	public void disarm(ISoliniaLivingEntity disarmer, int chance) {
		if (this.getBukkitLivingEntity() == null)
			return;
		
		if (this.getBukkitLivingEntity().isDead())
			return;
		
		if (disarmer == null)
			return;
		
		if (disarmer.getBukkitLivingEntity() == null)
			return;
		
		if (disarmer.getBukkitLivingEntity().isDead())
			return;

		ItemStack mainHandItemStack = this.getEquipment().getItemInMainHand();
		ItemStack offHandItemStack = this.getEquipment().getItemInOffHand();

		
		ISoliniaItem mainHandItem = getSoliniaItemInMainHand();
		ISoliniaItem offHandItem = getSoliniaItemInOffHand();
		
		if (mainHandItem == null && offHandItem == null)
			return;
		
		boolean primary = true;
		if (!mainHandItem.isWeaponOrBowOrShield())
			primary = false;
		
		if (primary == false && !offHandItem.isWeaponOrBowOrShield())
			return;
		
		
		ISoliniaItem item = null;
		ItemStack itemStack = null;
		if (primary == true)
		{
			item = mainHandItem;
			itemStack = mainHandItemStack;
		}
		else
		{
			item = offHandItem;
			itemStack = offHandItemStack;
		}
		
		
		try
		{
		if (MathUtils.RandomBetween(0, 1000) <= chance)
		{
			// We have an item that can be disarmed
				
			if (this.isPlayer()) {
				
				Player player = (Player)this.getBukkitLivingEntity();

				
				if (ItemStackUtils.getAugmentationItemId(itemStack) != null)
				{
					Integer augmentationId = ItemStackUtils.getAugmentationItemId(itemStack);
					ISoliniaItem augItem = null;
					if (augmentationId != null && augmentationId != 0) {
						augItem = StateManager.getInstance().getConfigurationManager().getItem(augmentationId);
						PlayerUtils.AddAccountClaim(player.getName(),augItem.getId());
					}
				}
				
				int count = itemStack.getAmount();
				if (count == 0)
					count = 1;
				for (int x = 0; x < count; x++)
				{
					PlayerUtils.AddAccountClaim(player.getName(),item.getId());
				}
				
				((Player) (this.getBukkitLivingEntity())).spigot().sendMessage(ChatMessageType.ACTION_BAR,
						new TextComponent(ChatColor.GRAY + "* You have been disarmed!"));
				player.sendMessage(ChatColor.GRAY + "Your item " + item.getDisplayname() + " has been added to your claims");
				
			}
			
			if (primary == true)
				this.getEquipment().setItemInMainHand(null);
			else
				this.getEquipment().setItemInOffHand(null);
			if (this.isPlayer())
			{
				Player player = (Player)this.getBukkitLivingEntity();
				player.updateInventory();
			}

			if (disarmer.isPlayer()) {
				((Player) getBukkitLivingEntity()).sendMessage(ChatColor.GRAY + "* "
						+ this.getBukkitLivingEntity().getCustomName() + " is disarmed!");
				
				disarmer.tryIncreaseSkill(SkillType.Disarm,1);
			}
		} else {
			if (disarmer.isPlayer()) {
				((Player) disarmer.getBukkitLivingEntity()).sendMessage(ChatColor.GRAY + "* You failed to disarm "
						+ this.getBukkitLivingEntity().getCustomName() + "!");
			}
		}
		} catch (CoreStateInitException e)
		{
			
		}
	}

	@Override
	public boolean isInCombat() {
		if (this.isNPC() && this.isEngaged())
			return true;
		if (this.isPlayer() && this.getReverseAggroCount() > 0)
			return true;
		
		return false;
	}
	
	@Override
	public long getReverseAggroCount() {
		try
		{
			return StateManager.getInstance().getEntityManager().getReverseAggroCount(this.getBukkitLivingEntity().getUniqueId());
		} catch (CoreStateInitException e)
		{
			return 0;
		}
	}
	

	private boolean hasAssistHate() {
		if (!this.hasHate())
			return false;
		
		try
		{
			return StateManager.getInstance().getEntityManager().hasAssistHate(this.getBukkitLivingEntity().getUniqueId());
		} catch (CoreStateInitException e)
		{
			return false;
		}
	}

	@Override
	public boolean isEngaged() {
		if (this.hasHate())
			return true;

		if (this.getReverseAggroCount() > 0)
			return true;

		return false;
	}

	@Override
	public void wipeHateList() {
		this.clearHateList();
	}

	@Override
	public void resetReverseAggro() {
		try {
			StateManager.getInstance().getEntityManager().resetReverseAggro(this.getBukkitLivingEntity().getUniqueId());
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void sendStats(LivingEntity targetMessage) {
		targetMessage.sendMessage("STATS:");
		targetMessage.sendMessage("----------------------------");
		String strlevel = "Level: " + ChatColor.GOLD + getMentorLevel() + " / " + getActualLevel() + " SCL: " + getEffectiveLevel(true)+ ChatColor.RESET;

		String strclass = "";
		if (getClassObj() != null)
		{
			strclass = "Class: " + ChatColor.GOLD + getClassObj().getShortName() + ChatColor.RESET;
		} else {
			strclass = "Class: " + ChatColor.GOLD + "Unknown" + ChatColor.RESET;
		}
		
		String strrace = "";
		if (getRace() != null)
		{
			strrace = "Race: " + ChatColor.GOLD + getRace().getShortName() + ChatColor.RESET;
		} else {
			strrace = "Race: " + ChatColor.GOLD + "Unknown" + ChatColor.RESET;
		}

		targetMessage.sendMessage(strlevel + " " + strclass + " " + strrace);

		
		
		targetMessage.sendMessage(
				"STR: " + ChatColor.GOLD + getStrength() + ChatColor.RESET + 
				" STA: " + ChatColor.GOLD + getStamina() + ChatColor.RESET + 
				" AGI: " + ChatColor.GOLD + getAgility() + ChatColor.RESET + 
				" DEX: " + ChatColor.GOLD + getDexterity() + ChatColor.RESET + 
				" INT: " + ChatColor.GOLD + getIntelligence() + ChatColor.RESET + 
				" WIS: " + ChatColor.GOLD + getWisdom() + ChatColor.RESET + 
				" CHA: " + ChatColor.GOLD + getCharisma() + ChatColor.RESET 
				);
		String strmaxhp = "MaxHP: " + ChatColor.RED + getBukkitLivingEntity().getHealth() + "/" + getBukkitLivingEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() + ChatColor.RESET;
		String strmaxmp = "MaxMP: " + ChatColor.AQUA +getMana() + "/" + getMaxMP() + " (Items:" + getItemMana()+")" + ChatColor.RESET;
		String hpregen = "HPRegen:" + ChatColor.GOLD + this.getHPRegen() + ChatColor.RESET;
		String mpregen = "MPRegen:" + ChatColor.GOLD + this.getMPRegen() + ChatColor.RESET;
		targetMessage.sendMessage(strmaxhp + " " + hpregen);
		targetMessage.sendMessage(strmaxmp + " " + mpregen);
        String resistsstr = "RESISTS: Fire: " + ChatColor.GOLD + getResists(SpellResistType.RESIST_FIRE) + ChatColor.RESET + 
        		" Cold: " + ChatColor.GOLD + getResists(SpellResistType.RESIST_COLD) + ChatColor.RESET + 
        		" Magic: " + ChatColor.GOLD + getResists(SpellResistType.RESIST_MAGIC) + ChatColor.RESET + 
        		" Poison: " + ChatColor.GOLD + getResists(SpellResistType.RESIST_POISON) + ChatColor.RESET + 
        		" Disease: " + ChatColor.GOLD + getResists(SpellResistType.RESIST_DISEASE) + ChatColor.RESET
        		;
        targetMessage.sendMessage(resistsstr);
		String strmitigationac = "MitigationAC: " + ChatColor.GOLD + getMitigationAC() + ChatColor.RESET;
		String strtotalatk = "TotalATK: " + ChatColor.GOLD+ getTotalAtk() + ChatColor.RESET;
        String runestr = "Rune: " + ChatColor.GOLD + getRune() + ChatColor.RESET;
        ItemStack weapon = this.getEquipment().getItemInMainHand();
        String skill = ItemStackUtils.getMeleeSkillForItemStack(weapon).getSkillType().name();
        String strmainweaponskill = "MainWeapSkill: " + skill;
		targetMessage.sendMessage(runestr + " " + strmitigationac + " " + strtotalatk + " " + strmainweaponskill);
		String strattackspeed = "AttackSpeedPct: " + ChatColor.GOLD+ getHaste() + "%" + ChatColor.RESET;
        
		String strmainweaponattackratesec = "MainWeapAttkRate: " + ChatColor.GOLD+ getAutoAttackTimerFrequencySeconds() +"s"+ ChatColor.RESET;
		targetMessage.sendMessage(strattackspeed + " " + strmainweaponattackratesec);
        //targetMessage.sendMessage("DEBUG: tohit: " + this.computeToHit(skill) + " / " + this.getTotalToHit(skill, 0));
        //targetMessage.sendMessage("DEBUG: Offense: " + this.offense(skill) + " | Item: " + 0 /*todo itembonuses for atk*/ + " ~Used: " + (0 /*todo itembonuses for atk*/ * 1.342) + " | Spell: " + getSpellBonuses(SpellEffectType.ATK));
        //targetMessage.sendMessage("DEBUG: ATK: " + this.getAtk());
        //targetMessage.sendMessage("DEBUG: mitigation AC: " + this.getMitigationAC());
        //targetMessage.sendMessage("DEBUG: defense: " + this.computeDefense() + "/" + this.getTotalDefense() + " Spell: " + getSpellBonuses(SpellEffectType.ArmorClass) );
		targetMessage.sendMessage("----------------------------");
	}

	@Override
	public boolean isFacingMob(ISoliniaLivingEntity soliniaLivingEntity) {
		// is my target - not - behind me
		return !TargetHelper.isBehind(soliniaLivingEntity.getBukkitLivingEntity(),this.getBukkitLivingEntity());
	}

	@Override
	public void healDamage(double damount, ISoliniaLivingEntity caster, int spell_id) {
		int amount = (int)Math.floor(damount);
		double maxhp = getMaxHP();
		double curhp = getHP();
		int acthealed = 0;

		if (amount > (maxhp - curhp))
			acthealed = (int)Math.floor(maxhp - curhp);
		else
			acthealed = (int)Math.floor(amount);

		if (acthealed > 100) {
			if (caster != null && spell_id > 0 && spell_id != Utils.SPELL_UNKNOWN) {
				try
				{
					ISoliniaSpell spell = StateManager.getInstance().getConfigurationManager().getSpell(spell_id);
					if (spell != null)
					{
					if (spell.isBuffSpell()) 
					{ // hots
												 // message to caster
						if (caster.isPlayer() && caster.getBukkitLivingEntity().getUniqueId().equals(this.getBukkitLivingEntity().getUniqueId())) {
							getBukkitLivingEntity().sendMessage("You have been healed for "+acthealed+" hit points by your spell.");
						}
						else if (caster.isPlayer() && !caster.getBukkitLivingEntity().getUniqueId().equals(this.getBukkitLivingEntity().getUniqueId())) {
							caster.getBukkitLivingEntity().sendMessage("You have healed " +getName()+ " for "+acthealed+" hit points with your spell.");
						}
						// message to target
						if (isPlayer() && !caster.getBukkitLivingEntity().getUniqueId().equals(this.getBukkitLivingEntity().getUniqueId())) {
							getBukkitLivingEntity().sendMessage(caster.getName() +" healed you for " + acthealed+ " hit points by a spell.");
						}
					}
					else { // normal heals
						getBukkitLivingEntity().sendMessage(caster.getName() + " has healed you for "+acthealed+" points of damage.");
						if (!caster.getBukkitLivingEntity().getUniqueId().equals(this.getBukkitLivingEntity().getUniqueId()))
							caster.getBukkitLivingEntity().sendMessage("You have healed "+getName()+" for "+acthealed+" points of damage.");
					}
					}
				} catch (CoreStateInitException e)
				{
					
				}
			}
			else {
				this.getBukkitLivingEntity().sendMessage("You have been healed for " + acthealed + " points of damage.");
			}
		}

		this.setHPChange(amount, this.getBukkitLivingEntity());
	}

	@Override
	public void tryDefensiveProc(SoliniaLivingEntity on, int hand) {
		if (on == null || on.getBukkitLivingEntity() == null) {
			setAttackTarget(null);
			//Log(Logs::General, Logs::Error, "A null Mob object was passed to Mob::TryDefensiveProc for evaluation!");
			return;
		}

		if (!hasDefensiveProcs())
			return;

		if (!on.getBukkitLivingEntity().isDead() && on.getHP() > 0) {

			float ProcChance = 0F, ProcBonus = 0F;
			on.getDefensiveProcChances(ProcBonus, ProcChance, hand, this);

			if (hand != InventorySlot.Primary)
				ProcChance /= 2;

			int level_penalty = 0;
			int level_diff = getMentorLevel() - on.getMentorLevel();
			if (level_diff > 6)//10% penalty per level if > 6 levels over target.
				level_penalty = (level_diff - 6) * 10;

			ProcChance -= ProcChance*level_penalty / 100;

			if (ProcChance < 0)
				return;
			
			try
			{
				//for (int i = 0; i < MAX_PROCS; i++) {
				for (SoliniaActiveSpell activeSpell : StateManager.getInstance().getEntityManager()
						.getActiveEntitySpells(this.getBukkitLivingEntity()).getActiveSpells()) 
				{
					
					int level_override = activeSpell.getSourceLevel();
					
					for (ActiveSpellEffect effect : activeSpell.getActiveSpellEffects()) {
						if (effect.getSpellEffectType() != SpellEffectType.DefensiveProc)
							continue;
	
						ISoliniaSpell procSpell = StateManager.getInstance().getConfigurationManager().getSpell(effect.getBase());
						if (procSpell == null)
							continue;
						
						for(SpellEffect procSpellEffects : procSpell.getBaseSpellEffects())
						{
							int echance = 100;
							if (procSpellEffects.getBase2() == 0)
								echance = 100;
							else
								echance = procSpellEffects.getBase2() + 100;
							
							float chance = ProcChance * ((float)(echance) / 100.0f);
							if (MathUtils.Roll(chance)) {
								ExecWeaponProc(null, procSpell, on, level_override);
								checkNumHitsRemaining(NumHit.DefensiveSpellProcs, 0, procSpell.getId());
							}
						}
					}
				}
			} catch (CoreStateInitException e)
			{
				
			}
		}
	}

	private float getDefensiveProcChances(float ProcBonus, float ProcChance, int hand,
			SoliniaLivingEntity on) {
		if (on == null || on.getBukkitLivingEntity() == null)
			return ProcChance;

		int myagi = on.getAgility();
		ProcBonus = 0;
		ProcChance = 0;

		int weapon_speed = getWeaponSpeedbyHand(hand);

		ProcChance = ((float)(weapon_speed) * Utils.AvgDefProcsPerMinute / 60000.0f); // compensate for weapon_speed being in ms
		ProcBonus += (float)(myagi) * Utils.DefProcPerMinAgiContrib / 100.0f;
		ProcChance = ProcChance + (ProcChance * ProcBonus);

		//Log(Logs::Detail, Logs::Combat, "Defensive Proc chance %.2f (%.2f from bonuses)", ProcChance, ProcBonus);
		return ProcChance;
	}

	private int getWeaponSpeedbyHand(int hand) {
		int weapon_speed = 0;
		// So these attack timers are generally in the 1000s range (1000 = 1sec?)
		switch (hand) {
			case 13:
				// TODO weapon_speed = attack_timer.GetDuration();
				weapon_speed = (int)(getAutoAttackTimerFrequencySeconds() * 1000);
				break;
			case 14:
				// TODO weapon_speed = attack_dw_timer.GetDuration();
				weapon_speed = (int)(getAutoAttackTimerFrequencySeconds() * 1000);
				break;
			case 11:
				// TODO weapon_speed = ranged_timer.GetDuration();
				weapon_speed = (int)(getAutoAttackTimerFrequencySeconds() * 1000);
				break;
		}

		if (weapon_speed < Utils.MinHastedDelay)
			weapon_speed = Utils.MinHastedDelay;

		return weapon_speed;
	}

	private boolean hasDefensiveProcs() {
		return this.hasSpellEffectType(SpellEffectType.DefensiveProc);
	}

	@Override
	public void doMend() {
		int mendhp = (int)getMaxHP() / 4;
		int currenthp = (int)getHP();
		if (MathUtils.RandomBetween(0, 199) < (int)getSkill(SkillType.Mend)) {

			int criticalchance = getSpellBonuses(SpellEffectType.CriticalMend) + getItemBonuses(SpellEffectType.CriticalMend) + getAABonuses(SpellEffectType.CriticalMend);

			if (MathUtils.RandomBetween(0, 99) < criticalchance) {
				mendhp *= 2;
				this.getBukkitLivingEntity().sendMessage("You magically mend your wounds and heal considerable damage");
			} else {
			}
			
			setHPChange(mendhp, this.getBukkitLivingEntity());
			this.getBukkitLivingEntity().sendMessage("You mend your wounds and heal some damage");
		}
		else {
			/* the purpose of the following is to make the chance to worsen wounds much less common,
			which is more consistent with the way eq live works.
			according to my math, this should result in the following probability:
			0 skill - 25% chance to worsen
			20 skill - 23% chance to worsen
			50 skill - 16% chance to worsen */
			if ((getSkill(SkillType.Mend) <= 75) && (MathUtils.RandomBetween(getSkill(SkillType.Mend), 100) < 75) && (MathUtils.RandomBetween(1, 3) == 1))
			{
				int change = -1;
				if (currenthp > mendhp)
					change = mendhp*-1;

				setHPChange(change, this.getBukkitLivingEntity());
				
				this.getBukkitLivingEntity().sendMessage("You have worsened your wounds!");
			}
			else
			{
				this.getBukkitLivingEntity().sendMessage("You have failed to mend your wounds");
			}
		}

		tryIncreaseSkill(SkillType.Mend, 1);
	}

	@Override
	public void sendVitalsPacketsToAnyoneTargettingMe() {
		try {
			for (UUID uuid : StateManager.getInstance().getEntityManager().getReverseEntityTarget(this.getBukkitLivingEntity().getUniqueId()))
				if (Bukkit.getEntity(uuid) instanceof Player)
					ForgeUtils.QueueSendForgeMessage((Player)Bukkit.getEntity(uuid),Solinia3UIChannelNames.Outgoing,Solinia3UIPacketDiscriminators.VITALS,this.toPacketMobVitals(-1, false).toPacketData(),-1);
		} catch (CoreStateInitException e) {
			
		}
	}
	
	@Override
	public boolean checkLosFN(ISoliniaLivingEntity soliniaLivingEntity) {

		return checkLosFN(soliniaLivingEntity,true);
	}

	@Override
	public boolean checkLosFN(ISoliniaLivingEntity soliniaLivingEntity, boolean checkDirection) {
		if(soliniaLivingEntity == null || soliniaLivingEntity.getBukkitLivingEntity() == null || soliniaLivingEntity.getBukkitLivingEntity().isDead())
			return false;

		if (this.hasSpellEffectType(SpellEffectType.Blind))
			return false;
		
		return RaycastUtils.isEntityInLineOfSight(this, soliniaLivingEntity,checkDirection);
	}
	
	@Override
	public int getItemBonuses(SpellEffectType spellEffectType) {
		return getItemBonusesTuple(spellEffectType,SpellResistType.RESIST_NONE).a();
	}
	
	@Override
	public Tuple<Integer,Integer> getItemBonusesTuple(SpellEffectType spellEffectType) {
		return getItemBonusesTuple(spellEffectType,SpellResistType.RESIST_NONE);
	}
	
	@Override
	public int getItemsRawAttack() {
		int attack = 0;
		for (ISoliniaItem item : this.getEquippedSoliniaItems()) {
			if (item == null)
				continue;
			
			if (item.getAttack() < 1)
				continue;
			
			attack += item.getAttack();
		}
		return attack;
	}
	
	public Tuple<Integer,Integer> getItemBonusesTuple(SpellEffectType spellEffectType, SpellResistType filterResistType)
	{
		List<ISoliniaItem> equippedItems = this.getEquippedSoliniaItems();
		
		int highest = 0;
		int highest2 = 0;

		// Check if item focus effect exists for the client.
		if (equippedItems.size() > 0) {

			ISoliniaItem TempItem = null;
			// item focus
			for (ISoliniaItem item : equippedItems) {
				TempItem = item;
				
				if (TempItem == null)
					continue;
				
				if (TempItem.getFocusEffectId() < 1) {
					continue;
				}
				
				ISoliniaSpell spell = null;

				try {
					spell = StateManager.getInstance().getConfigurationManager().getSpell(item.getFocusEffectId());
					if (spell == null)
						continue;
					
					if (!filterResistType.equals(SpellResistType.RESIST_NONE))
					if (spell.isEffectInSpell(SpellEffectType.LimitResist))
					{
						try
						{
							// For spells like Pet Focus where the spell type has type limits (ie fire resist = fire pet)
							int resistTypeId = spell.getSpellEffectBase(SpellEffectType.LimitResist);
							if (SpellUtils.getSpellResistType(resistTypeId) != filterResistType)
								continue;
						} catch (Exception e)
						{
							e.printStackTrace();							
						}
					}
					
					if (!spell.isEffectInSpell(spellEffectType))
						continue;
					
					
					Tuple<Integer,Integer> spellBonuses = ApplySpellsBonuses(spell.getId(), getBukkitLivingEntity(), getBukkitLivingEntity(),spell.getSpellEffect(spellEffectType),getMentorLevel(),0,0);
					
					if (spellBonuses.a() > highest)
					{
						highest = spellBonuses.a();
						highest2 = spellBonuses.b();
					}
				} catch (CoreStateInitException e) {
					
				}
			}
		}
		return new Tuple<Integer,Integer>(highest,highest2);
	}
	
	@Override
	public int getItemBonuses(SpellEffectType spellEffectType, SpellResistType filterResistType) {
		return getItemBonusesTuple(spellEffectType,filterResistType).a();
	}
	
	@Override
	public int getAABonuses(SpellEffectType effect) {
		return getAABonusesTuple(effect).a();
	}
	
	@Override
	public int getAABonuses(SpellEffectType effect, Integer filterBase2) {
		return getAABonusesTuple(effect,filterBase2).a();
	}
	
	@Override
	public Tuple<Integer,Integer> getAABonusesTuple(SpellEffectType effect, Integer filterbase2) {
		return SpellUtils.getHighestAAEffectEffectTypeTuple(getBukkitLivingEntity(),
				effect,  filterbase2);
	}
	
	@Override
	public Tuple<Integer,Integer> getAABonusesTuple(SpellEffectType effect) {
		return SpellUtils.getHighestAAEffectEffectTypeTuple(getBukkitLivingEntity(),
				effect);
	}
	
	@Override
	public ISoliniaPlayer getSoliniaPlayer()
	{
		try {
			if (this.isPlayer() && this.getBukkitLivingEntity() != null)
			return SoliniaPlayerAdapter.Adapt((Player)this.getBukkitLivingEntity());
		} catch (CoreStateInitException e) {
		}
		
		return null;
	}

	@Override
	public void removeAggro(UUID uniqueId) {
		removeFromHateList(uniqueId);
	}

	@Override
	public boolean isPassiveEnabled() {
		if (!this.isPlayer())
			return false;
		
		ISoliniaPlayer solPlayer = getSoliniaPlayer();
		if (solPlayer != null)
		{
			return solPlayer.isPassiveEnabled();
		}
		
		return true;
	}
	
	@Override
	public int getHPRegen()
	{
		if (getBukkitLivingEntity() == null || getBukkitLivingEntity().isDead())
			return 0;

		int sleephpregen = 0;
		// Sleep regen
		if (getBukkitLivingEntity().isSleeping()) {
			sleephpregen += 50;
		}

		// Hp and Mana Regen from Items
		int hpregen = 0;
		
		if (this.isNPC())
		{
			ISoliniaNPC npc = this.getNPC();
			if(npc != null)
				hpregen += npc.getNPCHPRegen();
		}

		ISoliniaAAAbility hpaa = null;
		try {
			if (hasAaRanks()) {
				for (ISoliniaAAAbility ability : StateManager.getInstance().getConfigurationManager()
						.getAAbilitiesBySysname("INNATEREGENERATION")) {
					if (!hasAAAbility(ability.getId()))
						continue;

					hpaa = ability;
				}
			}
		} catch (CoreStateInitException e) {

		}

		int aahpregenrank = 0;

		if (hpaa != null) {
			aahpregenrank = SpellUtils.getRankPositionOfAAAbility(getBukkitLivingEntity(), hpaa);
			hpregen += aahpregenrank;
		}

		hpregen += sleephpregen;
		
		return hpregen;
	}

	@Override
	public void doHPRegenTick() {
		if (getBukkitLivingEntity() == null || getBukkitLivingEntity().isDead())
			return;
		int hpregen = getHPRegen();

		// Process HP Regeneration
		if (hpregen > 0) {
			if (!getBukkitLivingEntity().isDead())
				setHPChange(hpregen, this.getBukkitLivingEntity());
		}
		this.setLastUpdatedTimeNow();


	}
	
	@Override
	public int getMPRegen()
	{
		if (getBukkitLivingEntity() == null || getBukkitLivingEntity().isDead())
			return 0;
		
		// Apply Crouch Mana Regen Bonus
				int manaregen = 1;

				if (this.isNPC())
				{
					ISoliniaNPC npc = this.getNPC();
					if(npc != null)
						manaregen += npc.getNPCMPRegen();
				}
				
				int sleepmpregen = 0;
				// Sleep regen
				if (getBukkitLivingEntity().isSleeping()) {
					sleepmpregen += 50;
				}

				manaregen += sleepmpregen;

				// a players mana regen based on if they are meditating (sneaking)
				manaregen += getMeditatingManaBonus();

				ISoliniaAAAbility aa = null;
				try {
					if (hasAaRanks()) {
						for (ISoliniaAAAbility ability : StateManager.getInstance().getConfigurationManager()
								.getAAbilitiesBySysname("MENTALCLARITY")) {
							if (!hasAAAbility(ability.getId()))
								continue;

							aa = ability;
						}
					}

				} catch (CoreStateInitException e) {

				}

				int aamanaregenrank = 0;

				if (aa != null) {
					if (hasAaRanks())
						aamanaregenrank = SpellUtils.getRankPositionOfAAAbility(getBukkitLivingEntity(), aa);
					manaregen += aamanaregenrank;
				}

				ISoliniaAAAbility emaa = null;
				try {
					if (hasAaRanks()) {
						for (ISoliniaAAAbility ability : StateManager.getInstance().getConfigurationManager()
								.getAAbilitiesBySysname("MENTALCLARITY")) {
							if (!hasAAAbility(ability.getId()))
								continue;

							emaa = ability;
						}
					}
				} catch (CoreStateInitException e) {

				}

				int emaamanaregenrank = 0;

				if (emaa != null) {
					if (hasAaRanks())
						emaamanaregenrank = SpellUtils.getRankPositionOfAAAbility(getBukkitLivingEntity(), emaa);
					manaregen += emaamanaregenrank;
				}
			return manaregen;
	}

	@Override
	public void doMPRegenTick() {
		if (getBukkitLivingEntity() == null || getBukkitLivingEntity().isDead())
			return;
		
		increaseMana(getMPRegen());
		
		this.setLastUpdatedTimeNow();
	}
	
	private int getMeditatingManaBonus() {
		if (this.isPlayer())
		{
			ISoliniaPlayer solPlayer = getSoliniaPlayer();
			if (solPlayer != null)
			{
				return solPlayer.getPlayerMeditatingManaBonus();
			}		
		}
		return 0;
	}

	@Override
	public void increaseMana(int amount) {
		if (amount < 0)
			amount = amount*-1;
		
		if (this.isPlayer())
		{
			ISoliniaPlayer solPlayer = getSoliniaPlayer();
			if (solPlayer != null)
			{
				solPlayer.increasePlayerMana(amount);
			}		
		}
		
		if (this.getNpcid() < 1)
			return;

		try {
			ISoliniaNPC npc = getNPC();
			if (npc == null)
				return;
			
			int currentMana = this.getMana();
			currentMana = currentMana + amount;
			
			if (currentMana > getMaxMP()) {
				currentMana = getMaxMP();
			}

			if (currentMana < 0)
				currentMana = 0;
			
			StateManager.getInstance().getEntityManager().setNPCMana(this.getBukkitLivingEntity(), npc, currentMana);
		} catch (CoreStateInitException e) {
			return;
		}
		
		
	}
	
	@Override
	public void reduceMana(int amount) {
		if (amount < 0)
			amount = amount*-1;
		
		if (this.isPlayer())
		{
			ISoliniaPlayer solPlayer = getSoliniaPlayer();
			if (solPlayer != null)
			{
				solPlayer.reducePlayerMana(amount);
			}		
		}
		
		if (this.getNpcid() < 1)
			return;

		try {
			ISoliniaNPC npc = getNPC();
			if (npc == null)
				return;
			
			int currentMana = this.getMana();
			currentMana = currentMana - amount;
			
			if (currentMana > getMaxMP()) {
				currentMana = getMaxMP();
			}

			if (currentMana < 0)
				currentMana = 0;
			
			StateManager.getInstance().getEntityManager().setNPCMana(this.getBukkitLivingEntity(), npc, currentMana);
		} catch (CoreStateInitException e) {
			return;
		}
		
		
	}

	private boolean hasAAAbility(int id) {
		if (!this.isPlayer())
			return false;
		
		ISoliniaPlayer solPlayer = getSoliniaPlayer();
		if (solPlayer != null)
		{
			return solPlayer.hasAAAbility(id);
		}		
		
		return false;
	}

	public void setLastUpdatedTimeNow()
	{
		if (this.isPlayer())
		{
			ISoliniaPlayer solPlayer = getSoliniaPlayer();
			if (solPlayer != null)
			{
				solPlayer.setLastUpdatedTimeNow();
			}		
		}
	}

	private boolean hasAaRanks() {
		if (!this.isPlayer())
			return false;
		
		ISoliniaPlayer solPlayer = getSoliniaPlayer();
		if (solPlayer != null)
		{
			return solPlayer.hasAaRanks();
		}		
		
		return false;
	}

	@Override
	public boolean canSeeInvis() {
		boolean canSeeInvis = hasSpellEffectType(SpellEffectType.SeeInvis);
		if (!canSeeInvis && this.isUndead())
		{
			if (!canSeeInvis)
				canSeeInvis = this.isUndead();
		}
		
		if (!canSeeInvis && this.isNPC())
		{
			if (this.getNPC() != null)
				canSeeInvis = this.getNPC().isCanSeeInvis();
		}
		return false;
	}

	@Override
	public void finishCasting(CastingSpell castingSpell) {
		if (getBukkitLivingEntity() == null || getBukkitLivingEntity().isDead())
			return;
		
		if (isPlayer())
			ChatUtils.SendHint(getBukkitLivingEntity(), HINT.FINISH_ABILITY, "", false);
		
		this.castingComplete(castingSpell);
	}

	private void castingComplete(CastingSpell castingSpell) {
		Entity entity = Bukkit.getEntity(castingSpell.livingEntityUUID);

		if (entity == null)
			return;

		if (entity.isDead())
			return;

		if (castingSpell.getSpell() == null)
			return;

		/*
		 * if (castingSpell.getItem() == null) return;
		 */

		doCastSpell(castingSpell.getSpell(), castingSpell.useMana, castingSpell.useReagents,
				castingSpell.ignoreProfessionAndLevel, castingSpell.getRequiredWeaponSkillType(), castingSpell.getNPCTarget());
	}

	@Override
	public void doCastSpell(ISoliniaSpell spell, boolean useMana, boolean useReagents,
			boolean ignoreProfessionAndLevel, String requiredWeaponSkillType, Entity npcTargetEntity) {
		
		if (getBukkitLivingEntity() == null || getBukkitLivingEntity().isDead())
			return;
		
		if (this.isPlayer() && !ignoreProfessionAndLevel && spell.isAASpell() && !canUseAASpell(spell)) {
			getBukkitLivingEntity().sendMessage("You require the correct AA to use this spell");
			return;
		}

		LivingEntity targetmob = null;

		if (this.isPlayer() && getEntityTarget() == null) {
			if (SpellUtils.getSpellTargetType(spell.getTargettype()).equals(SpellTargetType.Self)
					|| SpellUtils.getSpellTargetType(spell.getTargettype()).equals(SpellTargetType.AEBard)
					|| SpellUtils.getSpellTargetType(spell.getTargettype()).equals(SpellTargetType.AECaster)
					|| SpellUtils.getSpellTargetType(spell.getTargettype()).equals(SpellTargetType.AEClientV1)
					|| SpellUtils.getSpellTargetType(spell.getTargettype()).equals(SpellTargetType.AreaClientOnly)
					|| SpellUtils.getSpellTargetType(spell.getTargettype()).equals(SpellTargetType.Directional)
					|| SpellUtils.getSpellTargetType(spell.getTargettype()).equals(SpellTargetType.Group)
					|| SpellUtils.getSpellTargetType(spell.getTargettype()).equals(SpellTargetType.GroupClientAndPet)
					|| SpellUtils.getSpellTargetType(spell.getTargettype()).equals(SpellTargetType.GroupNoPets)
					|| SpellUtils.getSpellTargetType(spell.getTargettype()).equals(SpellTargetType.GroupTeleport)
					|| SpellUtils.getSpellTargetType(spell.getTargettype()).equals(SpellTargetType.UndeadAE)) {
				setEntityTarget(getBukkitLivingEntity());

			} else {
				ChatUtils.SendHint(getBukkitLivingEntity(), HINT.NEED_TARGET, "spellitem", false);
				return;
			}
		}

		targetmob = getEntityTarget();
		if (!this.isPlayer())
		{
			if (npcTargetEntity == null)
				return;
			
			if (!(npcTargetEntity instanceof LivingEntity))
				return;
			
			targetmob = (LivingEntity)npcTargetEntity;
		}
			
		if (this.isPlayer() && targetmob != null) {
			double distanceOverLimit = EntityUtils.DistanceOverAggroLimit((LivingEntity) getBukkitLivingEntity(), targetmob);

			if (distanceOverLimit > 0) {
				getBukkitLivingEntity().sendMessage("You were too far to interact with that entity");
				return;
			}
		}

		if (this.isPlayer() && !ignoreProfessionAndLevel && spell.getAllowedClasses().size() > 0) {
			if (getClassObj() == null) {
				getBukkitLivingEntity().sendMessage(ChatColor.GRAY + " * This effect cannot be used by your profession");
				return;
			}

			boolean foundprofession = false;
			String professions = "";
			int foundlevel = 0;
			for (SoliniaSpellClass spellclass : spell.getAllowedClasses()) {
				if (spellclass.getClassname().toUpperCase().equals(getClassObj().getName().toUpperCase())) {
					foundprofession = true;
					foundlevel = spellclass.getMinlevel();
					break;
				}
				professions += spellclass.getClassname() + " ";
			}

			if (foundprofession == false) {
				getBukkitLivingEntity().sendMessage(ChatColor.GRAY + " * This effect can only be used by " + professions);
				return;
			} else {
				if (foundlevel > 0) {
					Double level = (double) getMentorLevel();
					if (level < foundlevel) {
						getBukkitLivingEntity().sendMessage(ChatColor.GRAY + " * This effect requires level " + foundlevel);
						return;
					}
				}
			}

		}

		try {
			if (useMana && spell.getActSpellCost(this) > getMana()) {
				getBukkitLivingEntity().sendMessage(ChatColor.GRAY + "Insufficient Mana [E]");
				return;
			}

			if (!spell.isBardSong() && this.isPlayer()) {
				if (!hasReagents(spell))
				{
					return;
				}
			}
			
			if (targetmob != null && getBukkitLivingEntity() != null && spell != null)
			{
				// dont check these ones this early
				if (SpellUtils.getSpellTargetType(spell.getTargettype()).equals(SpellTargetType.AEBard)
						|| SpellUtils.getSpellTargetType(spell.getTargettype()).equals(SpellTargetType.AECaster)
						|| SpellUtils.getSpellTargetType(spell.getTargettype()).equals(SpellTargetType.AEClientV1)
						|| SpellUtils.getSpellTargetType(spell.getTargettype()).equals(SpellTargetType.AreaClientOnly)
						|| SpellUtils.getSpellTargetType(spell.getTargettype()).equals(SpellTargetType.Directional)
						|| SpellUtils.getSpellTargetType(spell.getTargettype()).equals(SpellTargetType.UndeadAE)
						|| SpellUtils.getSpellTargetType(spell.getTargettype()).equals(SpellTargetType.AETarget)
						|| SpellUtils.getSpellTargetType(spell.getTargettype()).equals(SpellTargetType.AETargetHateList
								)
						)
				{
					// dont check
				} else {
					Tuple<Boolean,String> result = SoliniaSpell.isValidEffectForEntity(targetmob, getBukkitLivingEntity(), spell);
					if (!result.a())
					{
						ChatUtils.SendHint(getBukkitLivingEntity(), HINT.SPELL_INVALIDEFFECT, result.b(), false);
						return;
					}
					
				}
			}
		} catch (CoreStateInitException e) {
			return;
		}
		if (getBukkitLivingEntity() != null && !getBukkitLivingEntity().isDead())
			if (targetmob != null && !targetmob.isDead()) {
				boolean success = spell.tryCast(getBukkitLivingEntity(), targetmob, useMana, useReagents, requiredWeaponSkillType);
				if (success == true) {
					tryIncreaseSkill(SkillUtils.getSkillType(spell.getSkill()), 1);
				}
				return;
			} else {
				boolean success = spell.tryCast(getBukkitLivingEntity(), getBukkitLivingEntity(), useMana, useReagents, requiredWeaponSkillType);
				if (success == true) {
					tryIncreaseSkill(SkillUtils.getSkillType(spell.getSkill()), 1);
				}
				return;
			}
	}
	
	private boolean canUseAASpell(ISoliniaSpell spell) {
		if (this.isPlayer())
		{
			if (this.getPlayer() != null)
				return this.getPlayer().canUseAASpell(spell);
		}
		// TODO Auto-generated method stub
		return false;
	}

	private boolean hasReagents(ISoliniaSpell spell) {
		if (this.isPlayer())
		{
			if (this.getPlayer() != null)
				return this.getPlayer().hasReagents(spell);
		}
		return true;
	}

	@Override
	public boolean IsCorpse() {
		if (this.getBukkitLivingEntity() == null)
			return true;
		
		return this.getBukkitLivingEntity().isDead();
		
	}
}