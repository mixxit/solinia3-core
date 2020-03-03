package com.solinia.solinia.Models;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sittable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import com.solinia.solinia.Adapters.SoliniaItemAdapter;
import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaItemException;
import com.solinia.solinia.Interfaces.ISoliniaAAAbility;
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
import com.solinia.solinia.Utils.DropUtils;
import com.solinia.solinia.Utils.ItemStackUtils;
import com.solinia.solinia.Utils.MythicMobsUtils;
import com.solinia.solinia.Utils.PartyWindowUtils;
import com.solinia.solinia.Utils.SpellTargetType;
import com.solinia.solinia.Utils.Utils;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_14_R1.EntityCreature;
import net.minecraft.server.v1_14_R1.EntityDamageSource;
import net.minecraft.server.v1_14_R1.EnumItemSlot;
import net.minecraft.server.v1_14_R1.GenericAttributes;
import net.minecraft.server.v1_14_R1.PacketPlayOutAnimation;
import net.minecraft.server.v1_14_R1.Tuple;

public class SoliniaLivingEntity implements ISoliniaLivingEntity {
	LivingEntity livingentity;
	private int level = 1;
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
			if (Utils.RandomBetween(0, 99) > charmBreakChance)
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
					totalDominationBonus += Utils.getHighestAAEffectEffectType(getBukkitLivingEntity(),
							SpellEffectType.CharmBreakChance);

					if (Utils.RandomBetween(0, 99) < totalDominationBonus)
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
			casterlevel = SoliniaPlayerAdapter.Adapt((Player) caster).getLevel();
		} else {
			if (Utils.isLivingEntityNPC(caster)) {
				ISoliniaLivingEntity solentity = SoliniaLivingEntityAdapter.Adapt((LivingEntity) caster);
				ISoliniaNPC casternpc = StateManager.getInstance().getConfigurationManager()
						.getNPC(solentity.getNpcid());
				casterlevel = casternpc.getLevel();
				isnpccaster = true;
			}
		}

		boolean isnpcvictim = false;
		int victimlevel = 1;

		if (getBukkitLivingEntity() instanceof Player) {
			victimlevel = SoliniaPlayerAdapter.Adapt((Player) getBukkitLivingEntity()).getLevel();
			targetresist = SoliniaPlayerAdapter.Adapt((Player) getBukkitLivingEntity())
					.getResist(Utils.getSpellResistType(spell.getResisttype()));
		} else {
			if (this.isNPC()) {
				ISoliniaLivingEntity solentity = SoliniaLivingEntityAdapter
						.Adapt((LivingEntity) getBukkitLivingEntity());
				ISoliniaNPC victimnpc = StateManager.getInstance().getConfigurationManager()
						.getNPC(solentity.getNpcid());
				targetresist = solentity.getResists(Utils.getSpellResistType(spell.getResisttype()));
				victimlevel = victimnpc.getLevel();
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

		int roll = Utils.RandomBetween(0, 200);

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
	public void autoAttackEnemy(ISoliniaLivingEntity defender) {
		if (getBukkitLivingEntity().isDead()) {
			try {
				StateManager.getInstance().getEntityManager().setEntityAutoAttack(getBukkitLivingEntity(), false);
				return;
			} catch (CoreStateInitException e) {

			}
			return;
		}

		if (defender.getBukkitLivingEntity().isDead()) {
			try {
				StateManager.getInstance().getEntityManager().setEntityAutoAttack(getBukkitLivingEntity(), false);
				return;
			} catch (CoreStateInitException e) {

			}
			return;
		}

		if (defender.getBukkitLivingEntity().getUniqueId().toString()
				.equals(getBukkitLivingEntity().getUniqueId().toString())) {
			getBukkitLivingEntity().sendMessage(ChatColor.GRAY + "* You cannot auto attack yourself!");
			return;
		}

		if (defender.isCurrentlyNPCPet()) {
			if (defender.getOwnerEntity().getUniqueId().toString()
					.equals(getBukkitLivingEntity().getUniqueId().toString())) {
				getBukkitLivingEntity().sendMessage(ChatColor.GRAY + "* You cannot auto attack your pet!");
				return;
			}
		}

		if (!ItemStackUtils.isRangedWeapon(getBukkitLivingEntity().getEquipment().getItemInMainHand()))
			if (defender.getBukkitLivingEntity().getLocation().distance(getBukkitLivingEntity().getLocation()) > 3) {
				getBukkitLivingEntity().sendMessage(ChatColor.GRAY + "* You are too far away to auto attack!");
				return;
			}

		if (!ItemStackUtils.isRangedWeapon(getBukkitLivingEntity().getEquipment().getItemInMainHand()))
			if (this.getLocation().distance(defender.getLocation()) > 3) {
				this.sendMessage(ChatColor.GRAY + "* You are too far away to attack!");
				return;
			}

		// Remove buffs on attacker (invis should drop)
		// and check they are not mezzed

		// MEZZED
		if (this.isMezzed()) {
			if (this instanceof Player) {
				((Player) this.getBukkitLivingEntity()).spigot().sendMessage(ChatMessageType.ACTION_BAR,
						new TextComponent(ChatColor.GRAY + "* You are mezzed!"));
			}
			return;
		}

		// STUNNED
		if (this.isStunned()) {
			if (this instanceof Player) {
				((Player) this.getBukkitLivingEntity()).spigot().sendMessage(ChatMessageType.ACTION_BAR,
						new TextComponent(ChatColor.GRAY + "* You are stunned!"));
			}
			return;
		}

		// CAN ATTACK / MEZ, STUN - PETS CHECKING IF CAN ATTACK ETC
		if (!this.canAttackTarget(defender)) {
			return;
		}

		if (!this.canUseItem(getBukkitLivingEntity().getEquipment().getItemInMainHand())) {
			if (getBukkitLivingEntity() instanceof Player) {
				getBukkitLivingEntity().sendMessage("Your cannot use this item (level or class)");
			}
		}

		if (getBukkitLivingEntity() instanceof Player) {
			// BOW
			if (ItemStackUtils.isRangedWeapon(getBukkitLivingEntity().getEquipment().getItemInMainHand()))
			{
				try {
					ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player) getBukkitLivingEntity());

					if (!solPlayer.hasSufficientArrowReagents(1)) {
						getBukkitLivingEntity().sendMessage(
								"* You do not have sufficient arrows in your /reagents to auto fire your bow!");
						return;
					}

					// Remove one of the reagents
					int itemid = solPlayer.getArrowReagents().get(0);
					solPlayer.reduceReagents(itemid, 1);

					net.minecraft.server.v1_14_R1.Entity ep = ((CraftEntity) getBukkitLivingEntity()).getHandle();
					PacketPlayOutAnimation packet = new PacketPlayOutAnimation(ep, 0);
					getBukkitLivingEntity().getWorld().playSound(getBukkitLivingEntity().getLocation(),
							Sound.ENTITY_ARROW_SHOOT, 1.0F, 1.0F);

					for (Entity listening : getBukkitLivingEntity().getNearbyEntities(20, 20, 20)) {
						if (listening instanceof Player)
							((CraftPlayer) listening).getHandle().playerConnection.sendPacket(packet);
					}

					if (getBukkitLivingEntity() instanceof Player)
						((CraftPlayer) getBukkitLivingEntity()).getHandle().playerConnection.sendPacket(packet);

					Arrow projectile = getBukkitLivingEntity().launchProjectile(Arrow.class);
					projectile.setPickupStatus(org.bukkit.entity.AbstractArrow.PickupStatus.DISALLOWED);
					projectile.setVelocity(defender.getBukkitLivingEntity().getEyeLocation().toVector()
							.subtract(projectile.getLocation().toVector()).normalize().multiply(4));
					// .attack(((CraftEntity) defender.getBukkitLivingEntity()).getHandle());
				} catch (CoreStateInitException e) {

				}

			} else {
				net.minecraft.server.v1_14_R1.Entity ep = ((CraftEntity) getBukkitLivingEntity()).getHandle();
				PacketPlayOutAnimation packet = new PacketPlayOutAnimation(ep, 0);
				getBukkitLivingEntity().getWorld().playSound(getBukkitLivingEntity().getLocation(),
						Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0F, 1.0F);

				for (Entity listening : getBukkitLivingEntity().getNearbyEntities(20, 20, 20)) {
					if (listening instanceof Player)
						((CraftPlayer) listening).getHandle().playerConnection.sendPacket(packet);
				}

				float f = 1;
				if (getBukkitLivingEntity() instanceof Player)
					((CraftPlayer) getBukkitLivingEntity()).getHandle().playerConnection.sendPacket(packet);

				if (getBukkitLivingEntity() instanceof CraftPlayer)
				{
					double baseValue = getBukkitLivingEntity().getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getBaseValue();
					double value = getBukkitLivingEntity().getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue();
					double defaultValue = getBukkitLivingEntity().getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getDefaultValue();
					Utils.DebugLog("SoliniaLivingEntity", "autoAttackEnemy", getBukkitLivingEntity().getName(), "Triggering an auto attack damage event base: " + baseValue + " value: " + value + " default: " + defaultValue);
				}
				
				((CraftPlayer) getBukkitLivingEntity()).getHandle()
						.attack(((CraftEntity) defender.getBukkitLivingEntity()).getHandle());
			}
		} else {
			double damage = 1;

			if (this.isNPC()) {
				ISoliniaNPC npc = getNPC();
				if (npc != null) {
					damage = Utils.getNPCDefaultDamage(npc);
					
					if (getBukkitLivingEntity().getEquipment().getItemInMainHand() != null)
					{
						if (ItemStackUtils.IsSoliniaItem(getBukkitLivingEntity().getEquipment().getItemInMainHand()))
						try {
							ISoliniaItem item = SoliniaItemAdapter.Adapt(getBukkitLivingEntity().getEquipment().getItemInMainHand());
							if (item.getDamage() > damage)
								damage = item.getDamage();
						} catch (SoliniaItemException e) {
							
						} catch (CoreStateInitException e) {
							
						}
					}
					
				}
			}

			// BOW
			if (ItemStackUtils.isRangedWeapon(getBukkitLivingEntity().getEquipment().getItemInMainHand()))
			{
				net.minecraft.server.v1_14_R1.Entity ep = ((CraftEntity) getBukkitLivingEntity()).getHandle();
				PacketPlayOutAnimation packet = new PacketPlayOutAnimation(ep, 0);
				getBukkitLivingEntity().getWorld().playSound(getBukkitLivingEntity().getLocation(),
						Sound.ENTITY_ARROW_SHOOT, 1.0F, 1.0F);

				for (Entity listening : getBukkitLivingEntity().getNearbyEntities(20, 20, 20)) {
					if (listening instanceof Player)
						((CraftPlayer) listening).getHandle().playerConnection.sendPacket(packet);
				}

				if (getBukkitLivingEntity() instanceof Player)
					((CraftPlayer) getBukkitLivingEntity()).getHandle().playerConnection.sendPacket(packet);
				EntityDamageSource source = new EntityDamageSource("mob",
						((CraftEntity) getBukkitLivingEntity()).getHandle());
				source.sweep();
				source.ignoresArmor();

				Arrow projectile = getBukkitLivingEntity().launchProjectile(Arrow.class);
				projectile.setVelocity(defender.getBukkitLivingEntity().getEyeLocation().toVector()
						.subtract(projectile.getLocation().toVector()).normalize().multiply(4));
				projectile.setPickupStatus(org.bukkit.entity.AbstractArrow.PickupStatus.DISALLOWED);

				// ((CraftEntity)
				// defender.getBukkitLivingEntity()).getHandle().damageEntity(source,
				// (float)damage);

			} else {
				net.minecraft.server.v1_14_R1.Entity ep = ((CraftEntity) getBukkitLivingEntity()).getHandle();
				PacketPlayOutAnimation packet = new PacketPlayOutAnimation(ep, 0);
				getBukkitLivingEntity().getWorld().playSound(getBukkitLivingEntity().getLocation(),
						Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0F, 1.0F);

				for (Entity listening : getBukkitLivingEntity().getNearbyEntities(20, 20, 20)) {
					if (listening instanceof Player)
						((CraftPlayer) listening).getHandle().playerConnection.sendPacket(packet);
				}

				if (getBukkitLivingEntity() instanceof Player)
					((CraftPlayer) getBukkitLivingEntity()).getHandle().playerConnection.sendPacket(packet);
				EntityDamageSource source = new EntityDamageSource("mob",
						((CraftEntity) getBukkitLivingEntity()).getHandle());
				source.sweep();
				source.ignoresArmor();
				
				defender.addToHateList(source.getEntity().getUniqueID(), (int)damage, false);
				Utils.DebugLog("SoliniaLivingEntity", "autoAttackEnemy", source.getEntity().getName(), "Triggering an auto attack damage event: " + damage);
				((CraftEntity) defender.getBukkitLivingEntity()).getHandle().damageEntity(source, (float) damage);
			}

			// solLivingEntity.getBukkitLivingEntity().damage(damage,
			// getBukkitLivingEntity());
		}

		/*
		 * DamageSource source =
		 * EntityDamageSource.playerAttack(((CraftPlayer)getBukkitPlayer()).getHandle())
		 * ;
		 * 
		 * 
		 * ((CraftEntity)
		 * solLivingEntity.getBukkitLivingEntity()).getHandle().damageEntity(source,
		 * (int)ItemStackUtils.getWeaponDamage(itemStack));
		 */
	}

	@Override
	public boolean canUseItem(ItemStack itemStack) {
		try {
			ISoliniaItem item = SoliniaItemAdapter.Adapt(itemStack);
			if (item == null)
				return true;

			if (item.getAllowedClassNames().size() > 0) {
				if (getClassObj() == null) {
					return false;
				}

				if (!item.getAllowedClassNames().contains(getClassObj().getName())) {
					return false;
				}
			}
			
			if (item.getAllowedRaceNames().size() > 0) {
				if (getRace() == null) {
					return false;
				}

				if (!item.getAllowedRaceNames().contains(getRace().getName())) {
					return false;
				}
			}

			if (item.getMinLevel() > 0) {
				if (item.getMinLevel() > getLevel()) {
					return false;
				}
			}

			return true;
		} catch (CoreStateInitException e) {
			return false;
		} catch (SoliniaItemException e) {
			return true;
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

			setLevel(npc.getLevel());
			setNpcid(npc.getId());
		} catch (CoreStateInitException e) {
			e.printStackTrace();
		}
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
					TextComponent tc = new TextComponent(TextComponent
							.fromLegacyText(ChatColor.GRAY + "* Your " + UsedItem.getDisplayname() + " " + string_id));
					((Player) getBukkitLivingEntity()).spigot().sendMessage(tc);
				}
			}
		}

		// TODO spell focuses

		// TODO aa focuses

		if (type == FocusEffect.ReagentCost
				&& (spell.isEffectInSpell(SpellEffectType.SummonItem) || spell.isSacrificeSpell()))
			return 0;

		return realTotal + realTotal2 + realTotal3;
	}

	private int CalcFocusEffect(FocusEffect type, int focusEffectId, ISoliniaSpell spell, boolean best_focus) {
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
		int MaxLimitInclude = Utils.getMaxLimitInclude();
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
				if (focusSpellEffect.getBase() > spell.calcBuffDurationFormula(getLevel(),
						spell.getBuffdurationformula(), spell.getBuffduration()))
					return (0);
				break;

			case LimitEffect:
				if (focusSpellEffect.getBase() < 0) {
					if (spell.isEffectInSpell(Utils.getSpellEffectType(-focusSpellEffect.getBase()))) // Exclude
						return 0;
				} else {
					LimitInclude[4] = true;
					if (spell.isEffectInSpell(Utils.getSpellEffectType(focusSpellEffect.getBase()))) // Include
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
						value = Utils.RandomBetween(focusSpellEffect.getBase(), focusSpellEffect.getLimit());
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
						value = Utils.RandomBetween(focusSpellEffect.getBase(), focusSpellEffect.getLimit());
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
						value = Utils.RandomBetween(focusSpellEffect.getBase(), focusSpellEffect.getLimit());
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
						value = Utils.RandomBetween(focusSpellEffect.getBase(), focusSpellEffect.getLimit());
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
						value = Utils.RandomBetween(focusSpellEffect.getBase(), focusSpellEffect.getLimit());
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
					if (Utils.RandomRoll(focusSpellEffect.getBase()))
						value = focusSpellEffect.getLimit();
					else
						value = 0;
				}
				break;

			case BlockNextSpellFocus:
				if (type == FocusEffect.BlockNextSpell) {
					if (Utils.RandomRoll(focusSpellEffect.getBase()))
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
						value = Utils.RandomBetween(focusSpellEffect.getBase(), focusSpellEffect.getLimit());
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
			int level = getLevel();

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

		if (ItemStackUtils.IsSoliniaItem(getBukkitLivingEntity().getEquipment().getItemInMainHand())) {
			try {
				ISoliniaItem soliniaitem = StateManager.getInstance().getConfigurationManager()
						.getItem(getBukkitLivingEntity().getEquipment().getItemInMainHand());
				if (soliniaitem != null) {
					if (soliniaitem.isThrowing()) {
						getBukkitLivingEntity().sendMessage(
								ChatColor.GRAY + "This is a throwing weapon! (you must right click to throw it)");
					}

					if (soliniaitem.getAllowedClassNames().size() > 0) {
						if (getClassObj() == null) {
							getBukkitLivingEntity().sendMessage(ChatColor.GRAY + "Your class cannot use this item");
							return false;
						}

						if (!soliniaitem.getAllowedClassNames().contains(getClassObj().getName())) {
							getBukkitLivingEntity().sendMessage(ChatColor.GRAY + "Your class cannot use this item");
							return false;
						}
					}
					
					if (soliniaitem.getAllowedRaceNames().size() > 0) {
						if (getRace() == null) {
							getBukkitLivingEntity().sendMessage(ChatColor.GRAY + "Your race cannot use this item");
							return false;
						}

						if (!soliniaitem.getAllowedRaceNames().contains(getRace().getName())) {
							getBukkitLivingEntity().sendMessage(ChatColor.GRAY + "Your race cannot use this item");
							return false;
						}
					}

					if (soliniaitem.getMinLevel() > 0) {
						if (soliniaitem.getMinLevel() > getLevel()) {
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
	public int AttackWithMainHand(ISoliniaLivingEntity defender, boolean arrowHit, int baseDamage) {
		try {
			if (defender.isPlayer() && isPlayer()) {
				ISoliniaPlayer defenderPlayer = SoliniaPlayerAdapter.Adapt((Player) defender.getBukkitLivingEntity());
				ISoliniaPlayer attackerPlayer = SoliniaPlayerAdapter.Adapt((Player) this.getBukkitLivingEntity());

				if (defenderPlayer.getGroup() != null && attackerPlayer.getGroup() != null) {
					if (defenderPlayer.getGroup().getId().equals(attackerPlayer.getGroup().getId())) {
						return 0;
					}
				}
			}
		} catch (CoreStateInitException e) {
			// ignore
		}

		if (isPlayer()) {
			Player player = (Player) this.getBukkitLivingEntity();
			if (player.isSneaking()) {
				try {
					ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(player);
					if (solplayer.getClassObj() != null) {
						if (solplayer.getClassObj().isSneakFromCrouch()) {
							player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GRAY
									+ "* You cannot concentrate on combat while meditating or sneaking"));
							return 0;
						}
					}
				} catch (CoreStateInitException e) {
					// do nothing
				}
			}
		}

		if (usingValidWeapon() == false) {
			return 0;
		} else {
			// Add Bane Undead Damage
			if (ItemStackUtils.IsSoliniaItem(getBukkitLivingEntity().getEquipment().getItemInMainHand())) {
				try {
					ISoliniaItem soliniaitem = StateManager.getInstance().getConfigurationManager()
							.getItem(getBukkitLivingEntity().getEquipment().getItemInMainHand());
					
					if (soliniaitem != null)
					{
						// TODO move this
						if (soliniaitem.getBaneUndead() > 0 && defender.isUndead())
							baseDamage += soliniaitem.getBaneUndead();
						}
				} catch (CoreStateInitException e) {
					return 0;
				}
			}
		}

		if (baseDamage < 1)
			baseDamage = 1;

		if (defender.getBukkitLivingEntity().isDead() || this.getBukkitLivingEntity().isDead()
				|| this.getBukkitLivingEntity().getHealth() < 0) {
			return 0;
		}

		if (isInulvnerable()) {
			return 0;
		}

		if (isFeigned()) {
			return 0;
		}

		ItemStack weapon = this.getBukkitLivingEntity().getEquipment().getItemInMainHand();

		DamageHitInfo my_hit = this.GetHitInfo(weapon, baseDamage, arrowHit, defender);

		///////////////////////////////////////////////////////////
		////// Send Attack Damage
		///////////////////////////////////////////////////////////

		// TODO Skill Procs

		if (getBukkitLivingEntity().isDead()) {
			return 0;
		}

		int finaldamage = 0;
		
		// Sets last melee attack so we can check if a user has melee attacked previously
		setLastMeleeAttack();

		if (my_hit.damage_done > 0) {
			triggerDefensiveProcs(defender, my_hit.damage_done, arrowHit);

			finaldamage = my_hit.damage_done;

			DecimalFormat df = new DecimalFormat();
			df.setMaximumFractionDigits(2);

			LivingEntity attackerEntity = getBukkitLivingEntity();

			// Moved from player to support creatures too

			String name = defender.getBukkitLivingEntity().getName();
			if (defender.getBukkitLivingEntity().getCustomName() != null)
				name = defender.getBukkitLivingEntity().getCustomName();

			if (attackerEntity instanceof Player)
				((Player) attackerEntity).spigot()
						.sendMessage(ChatMessageType.ACTION_BAR,
								new TextComponent("You hit " + name + " for " + df.format(finaldamage) + " "
										+ df.format(defender.getBukkitLivingEntity().getHealth() - finaldamage) + "/"
										+ df.format(defender.getBukkitLivingEntity()
												.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue())
										+ " " + my_hit.skill + " damage"));

			if (this.isCurrentlyNPCPet()) {
				if (getOwnerEntity() != null && getOwnerEntity() instanceof Player) {
					Player owner = (Player) getOwnerEntity();
					owner.spigot().sendMessage(ChatMessageType.ACTION_BAR,
							new TextComponent("Your pet hit " + name + " for " + df.format(finaldamage) + " "
									+ df.format(defender.getBukkitLivingEntity().getHealth() - finaldamage) + "/"
									+ df.format(defender.getBukkitLivingEntity()
											.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue())
									+ " " + my_hit.skill + " damage [PetHP:" + attackerEntity.getHealth() + "]"));
				}
			}

			if (!arrowHit)
				if (getDoubleAttackCheck() && !attackerEntity.isDead() && !defender.getBukkitLivingEntity().isDead()) {

					final UUID defenderUUID = defender.getBukkitLivingEntity().getUniqueId();
					final UUID attackerUUID = this.getBukkitLivingEntity().getUniqueId();
					final int final_damagedone = my_hit.damage_done;

					Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("Solinia3Core"),
							new Runnable() {
								public void run() {
									try {
										Entity entity = Bukkit.getEntity(attackerUUID);

										if (entity == null || !(entity instanceof LivingEntity))
											return;

										ISoliniaLivingEntity solLivingEntity = null;
										try {
											solLivingEntity = SoliniaLivingEntityAdapter.Adapt((LivingEntity) entity);
										} catch (CoreStateInitException e) {

										}

										if (solLivingEntity == null)
											return;

										solLivingEntity.doDoubleAttack(defenderUUID, final_damagedone);
									} catch (Exception e) {
										System.out.println("An error occured during the doDoubleAttack scheduler: "
												+ e.getMessage() + " " + e.getStackTrace());
									}
								}
							});
				}

			if (!arrowHit)
				TryDualWield(this.getBukkitLivingEntity(),defender);

			// end of what was old only player code

			if (defender.getBukkitLivingEntity() instanceof Player) {
				((Player) defender.getBukkitLivingEntity()).spigot().sendMessage(ChatMessageType.ACTION_BAR,
						new TextComponent("You were hit by " + getBukkitLivingEntity().getCustomName() + " for "
								+ df.format(finaldamage) + " " + my_hit.skill + " damage"));

			}

			if (finaldamage > getBukkitLivingEntity().getHealth() && hasDeathSave() > 0) {
				removeDeathSaves();
				attackerEntity.sendMessage("* Your target was protected by a death save boon!");
				getBukkitLivingEntity().sendMessage("* Your death save boon has saved you from death!");
				return 0;
			}
			
			// This is the only place to proc attacks
			if (true)
			{
				try {
					// try weapon item procs

					ISoliniaItem attackItem = null;
					
					if (ItemStackUtils.IsSoliniaItem(this.getBukkitLivingEntity().getEquipment().getItemInMainHand())) {
						attackItem = SoliniaItemAdapter.Adapt(this.getBukkitLivingEntity().getEquipment().getItemInMainHand());
					}
					
					if (attackItem != null) {
						TryProcItem(attackItem, this, defender, false);
					}
					
					// try spell procs
					// Check if attacker has any WeaponProc effects
					SoliniaEntitySpells effects = StateManager.getInstance().getEntityManager()
							.getActiveEntitySpells(this.getBukkitLivingEntity());

					if (effects != null) {
						for (SoliniaActiveSpell activeSpell : effects.getActiveSpells()) {
							ISoliniaSpell spell = StateManager.getInstance().getConfigurationManager()
									.getSpell(activeSpell.getSpellId());
							if (spell == null)
								continue;

							if (!spell.isWeaponProc())
								continue;

							for (ActiveSpellEffect spelleffect : activeSpell.getActiveSpellEffects()) {
								if (spelleffect.getSpellEffectType().equals(SpellEffectType.WeaponProc)
										|| spelleffect.getSpellEffectType().equals(SpellEffectType.AddMeleeProc)) {
									if (spelleffect.getBase() < 0)
										continue;
									
									if (!activeSpell.getRequiredWeaponSkillType().equals(""))
									{
										Utils.DebugLog("SoliniaLivingEntity","AttackWithMainHand",this.getBukkitLivingEntity().getName(),"RequiredWeaponSkillType check : " + activeSpell.getRequiredWeaponSkillType() + " vs " + my_hit.skill);
										if (!my_hit.skill.equals(activeSpell.getRequiredWeaponSkillType()))
											continue;
									}

									ISoliniaSpell procSpell = StateManager.getInstance().getConfigurationManager()
											.getSpell(spelleffect.getBase());
									if (spell == null)
										continue;

									// Chance to proc
									int procChance = getProcChancePct();
									int roll = Utils.RandomBetween(0, 100);

									if (roll < procChance) {
										boolean itemUseSuccess = procSpell.tryApplyOnEntity(this.getBukkitLivingEntity(),
												defender.getBukkitLivingEntity(), true, "");

										if (itemUseSuccess) {
											checkNumHitsRemaining(NumHit.OffensiveSpellProcs, 0, procSpell.getId());
										}

										if (procSpell.getActSpellCost(this) > 0)
											if (itemUseSuccess) {
												if (this.getBukkitLivingEntity() instanceof Player) {
													SoliniaPlayerAdapter.Adapt((Player) this.getBukkitLivingEntity())
															.reducePlayerMana(procSpell.getActSpellCost(this));
												}
											}
									}
								}
							}
						}
					}
				} catch (CoreStateInitException e) {
					
				} catch (SoliniaItemException e) {
					
				} catch (Exception e)
				{
					System.out.println("Failed to perform proc in damage routine");
					e.printStackTrace();
				}
			}

			defender.damageAlertHook(finaldamage, getBukkitLivingEntity());

			return finaldamage;
		} else {
			return 0;
		}
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
		float weaponDelayInSeconds = ((float)getMainWeaponDelay())/10F;
		float onePercentWeaponDelay = weaponDelayInSeconds/100F;
		float hastedWeaponDelay = onePercentWeaponDelay * (float)getAttackSpeed();
		float hastedWeaponDelayMinusDelay = hastedWeaponDelay - weaponDelayInSeconds;
		
		float frequency = weaponDelayInSeconds-hastedWeaponDelayMinusDelay;
		if (frequency < 0.1F)
			frequency = 0.10F;
		
		Utils.DebugLog("SoliniaLivingEntity","getAutoAttackTimerFrequencySeconds",getBukkitLivingEntity().getName(),"WeaponDelayInSeconds: " + weaponDelayInSeconds + " onePercentWeaponDelay: " + onePercentWeaponDelay + " hastedWeaponDelay: " + hastedWeaponDelay + " hastedWeaponDelayMinusDelay: " + hastedWeaponDelayMinusDelay + " frequency: " + frequency);

		return frequency;
	}

	
	@Override
	public ISoliniaItem getSoliniaItemInMainHand()
	{
		ItemStack main = getBukkitLivingEntity().getEquipment().getItemInMainHand();
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
		ItemStack main = getBukkitLivingEntity().getEquipment().getItemInOffHand();
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
	

	private void TryDualWield(LivingEntity attackerEntity, ISoliniaLivingEntity defender) {
		if (getDualWieldCheck() && !attackerEntity.isDead() && !defender.getBukkitLivingEntity().isDead()) {
			ItemStack weapon2 = attackerEntity.getEquipment().getItemInOffHand();
			int baseDamage2 = (int) ItemStackUtils.getWeaponDamage(weapon2, EnumItemSlot.OFFHAND);

			DamageHitInfo my_hit2 = this.GetHitInfo(weapon2, baseDamage2, false, defender);

			final UUID defenderUUID = defender.getBukkitLivingEntity().getUniqueId();
			final UUID attackerUUID = this.getBukkitLivingEntity().getUniqueId();
			final int final_damagedone = my_hit2.damage_done;

			int offHandItemId = 0;
			if (ItemStackUtils.IsSoliniaItem(weapon2)) {

				try {
					ISoliniaItem item = SoliniaItemAdapter.Adapt(weapon2);
					offHandItemId = item.getId();
				} catch (SoliniaItemException | CoreStateInitException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			final int final_offhandItemId = offHandItemId;

			Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("Solinia3Core"),
					new Runnable() {
						public void run() {
							try {
								Entity entity = Bukkit.getEntity(attackerUUID);

								if (entity == null || !(entity instanceof LivingEntity))
									return;

								ISoliniaLivingEntity solLivingEntity = null;
								try {
									solLivingEntity = SoliniaLivingEntityAdapter.Adapt((LivingEntity) entity);
								} catch (CoreStateInitException e) {

								}

								if (solLivingEntity == null)
									return;

								solLivingEntity.doDualWield(defenderUUID, final_damagedone,
										final_offhandItemId);
							} catch (Exception e) {
								System.out.println("An error occured during the doDualWield scheduler: "
										+ e.getMessage() + " " + e.getStackTrace());
							}
						}
					});

		}
	}

	private void TryProcItem(ISoliniaItem soliniaitem, ISoliniaLivingEntity attackerSolEntity,
			ISoliniaLivingEntity defender, boolean isDualWield) {
		final UUID defenderUUID = defender.getBukkitLivingEntity().getUniqueId();
		final UUID attackerUUID = attackerSolEntity.getBukkitLivingEntity().getUniqueId();

		final int finalitemid = soliniaitem.getId();
		final boolean fisDualWield = isDualWield;

		Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("Solinia3Core"),
				new Runnable() {
					public void run() {
						try {
							Entity entity = Bukkit.getEntity(attackerUUID);

							if (entity == null || !(entity instanceof LivingEntity))
								return;

							ISoliniaLivingEntity solLivingEntity = null;
							try {
								solLivingEntity = SoliniaLivingEntityAdapter.Adapt((LivingEntity) entity);
							} catch (CoreStateInitException e) {

							}

							if (solLivingEntity == null)
								return;

							solLivingEntity.doProcItem(finalitemid, attackerUUID, defenderUUID, fisDualWield);
						} catch (Exception e) {
							System.out.println("An error occured during the doProcItem scheduler: " + e.getMessage()
									+ " " + e.getStackTrace());
						}
					}
				});
	}

	@Override
	public void doProcItem(int procItemId, UUID attackerEntityUUID, UUID defenderEntityUUID, boolean isDualWield) {
		if (procItemId < 1)
			return;
		
		Entity attackerEntity = Bukkit.getEntity(attackerEntityUUID);
		Entity defenderEntity = Bukkit.getEntity(defenderEntityUUID);

		if (attackerEntity == null || defenderEntity == null)
			return;

		Utils.DebugLog("SoliniaLivingEntity", "doProcItem", attackerEntity.getName(), "Starting doProcItem for " + attackerEntity.getName());
		
		if (attackerEntity.isDead() || defenderEntity.isDead())
			return;

		if (!(attackerEntity instanceof LivingEntity) || !(defenderEntity instanceof LivingEntity))
			return;

		ISoliniaLivingEntity attackerSolEntity = null;
		ISoliniaLivingEntity defenderSolEntity = null;

		try {
			attackerSolEntity = SoliniaLivingEntityAdapter.Adapt((LivingEntity) attackerEntity);
			defenderSolEntity = SoliniaLivingEntityAdapter.Adapt((LivingEntity) defenderEntity);

			if (attackerSolEntity == null || defenderSolEntity == null)
				return;

			ISoliniaItem handItem = StateManager.getInstance().getConfigurationManager().getItem(procItemId);
			if (handItem == null)
				return;
			
			Utils.DebugLog("SoliniaLivingEntity", "doProcItem", attackerEntity.getName(), " is using Weapon : " + handItem.getDisplayname() + " with weapon ability id: " + handItem.getWeaponabilityid());

			// Check if item has any proc effects
			if (handItem.getWeaponabilityid() > 0) {
				ISoliniaSpell procSpell = StateManager.getInstance().getConfigurationManager()
						.getSpell(handItem.getWeaponabilityid());

				if (procSpell != null && attackerSolEntity.getLevel() >= handItem.getMinLevel()) {
					// Chance to proc
					int procChance = getProcChancePct();
					int roll = Utils.RandomBetween(0, 100);
					
					Utils.DebugLog("SoliniaLivingEntity", "doProcItem", attackerEntity.getName(), "Chance to proc on hit: " + procChance + " roll: " + roll + " isDualWield: " + isDualWield);

					if (roll < procChance) {

						// TODO - For now apply self and group to attacker, else attach to target
						switch (Utils.getSpellTargetType(procSpell.getTargettype())) {
						case Self:
							procSpell.tryApplyOnEntity(attackerSolEntity.getBukkitLivingEntity(),
									attackerSolEntity.getBukkitLivingEntity(), true,"");
							break;
						case Group:
							procSpell.tryApplyOnEntity(attackerSolEntity.getBukkitLivingEntity(),
									attackerSolEntity.getBukkitLivingEntity(), true,"");
							break;
						default:
							procSpell.tryApplyOnEntity(attackerSolEntity.getBukkitLivingEntity(),
									defenderSolEntity.getBukkitLivingEntity(), true, "");
						}

					}
				}
			}

		} catch (CoreStateInitException e) {

		}
	}

	private DamageHitInfo GetHitInfo(ItemStack weapon, int baseDamage, boolean arrowHit,
			ISoliniaLivingEntity defender) {
		
		Utils.DebugLog("SoliniaLivingEntity", "GetHitInfo", this.getBukkitLivingEntity().getName(), "GetHitInfo starts with baseDamage: " + baseDamage + " arrowHit: " + arrowHit);
		DamageHitInfo my_hit = new DamageHitInfo();
		my_hit.skill = ItemStackUtils.getMeleeSkillForItemStack(weapon).getSkillname();
		if (arrowHit) {
			my_hit.skill = "ARCHERY";
		}
		Utils.DebugLog("SoliniaLivingEntity", "GetHitInfo", this.getBukkitLivingEntity().getName(), "GetHitInfo skill will be: " + my_hit.skill);

		// Now figure out damage
		my_hit.damage_done = 1;
		my_hit.min_damage = 0;
		//getLevel();
		int hate = 0;

		my_hit.base_damage = baseDamage;
		Utils.DebugLog("SoliniaLivingEntity", "GetHitInfo", this.getBukkitLivingEntity().getName(), "GetHitInfo base damage: " + my_hit.base_damage);
		// amount of hate is based on the damage done
		if (hate == 0 && my_hit.base_damage > 1)
		{
			hate = my_hit.base_damage;
			Utils.DebugLog("SoliniaLivingEntity", "GetHitInfo", this.getBukkitLivingEntity().getName(), "GetHitInfo hate set to: " + my_hit.base_damage);
		}

		if (my_hit.base_damage > 0) {
			Utils.DebugLog("SoliniaLivingEntity", "GetHitInfo", this.getBukkitLivingEntity().getName(), "GetHitInfo hate set to: " + my_hit.base_damage);
			int damageCaps = getDamageCaps(my_hit.base_damage);
			Utils.DebugLog("SoliniaLivingEntity", "GetHitInfo", this.getBukkitLivingEntity().getName(), "GetHitInfo ran through damage caps and output is now: " + damageCaps);
			my_hit.base_damage = damageCaps;

			tryIncreaseSkill(my_hit.skill, 1);
			tryIncreaseSkill("OFFENSE", 1);

			int ucDamageBonus = 0;

			if (getClassObj() != null && getClassObj().isWarriorClass() && getLevel() >= 28) {
				ucDamageBonus = getWeaponDamageBonus(weapon);
				Utils.DebugLog("SoliniaLivingEntity", "GetHitInfo", this.getBukkitLivingEntity().getName(), "GetHitInfo ucDamageBonus from weapon is: " + ucDamageBonus);
				my_hit.min_damage = ucDamageBonus;
				hate += ucDamageBonus;
			}

			// TODO Sinister Strikes

			int hit_chance_bonus = 0;
			my_hit.offense = getOffense(my_hit.skill); // we need this a few times
			my_hit.tohit = getTotalToHit(my_hit.skill, hit_chance_bonus);
			Utils.DebugLog("SoliniaLivingEntity", "GetHitInfo", this.getBukkitLivingEntity().getName(), "GetHitInfo my_hit.offense + " + my_hit.offense + " my_hit.tohit " + my_hit.tohit);

			my_hit = doAttack(defender, my_hit);
		}

		if (my_hit.damage_done > 0 && hate < 1)
			hate = 1;

		defender.addToHateList(getBukkitLivingEntity().getUniqueId(), hate, true);

		Utils.DebugLog("SoliniaLivingEntity", "GetHitInfo", this.getBukkitLivingEntity().getName(), "GetHitInfo final my_hit is my_hit.damage_done " + my_hit.damage_done + " my_hit.base_damage " + my_hit.base_damage + " my_hit.offense " + my_hit.offense + " my_hit.tohit " + my_hit.tohit);
		return my_hit;
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
	public void damage(double damage, Entity sourceEntity, boolean tryProc, boolean isMelee, boolean isOffhand) {
		if (sourceEntity == null || getBukkitLivingEntity() == null || sourceEntity.isDead()
				|| getBukkitLivingEntity().isDead())
			return;

		damageAlertHook(damage, sourceEntity);

		if (damage > getBukkitLivingEntity().getHealth() && hasDeathSave() > 0) {
			removeDeathSaves();
			getBukkitLivingEntity().sendMessage("* Your death save boon has saved you from death!");
			return;
		}

		try {
			if (Utils.RandomBetween(1, 100) > 70) {
				StateManager.getInstance().getEntityManager().interruptCasting(getBukkitLivingEntity());
			}
		} catch (CoreStateInitException e) {

		}
		
		getBukkitLivingEntity().damage(damage, sourceEntity);
		
		// Try melee procs
		if (isMelee && sourceEntity instanceof LivingEntity && !this.getBukkitLivingEntity().isDead() && !sourceEntity.isDead())
		{
			try {
				// try weapon item procs

				ISoliniaItem attackItem = null;
				ItemStack attackStack = null;
				
				ISoliniaLivingEntity attackerSolEntity = SoliniaLivingEntityAdapter.Adapt((LivingEntity)sourceEntity);
				
				if (!isOffhand && ItemStackUtils.IsSoliniaItem(attackerSolEntity.getBukkitLivingEntity().getEquipment().getItemInMainHand())) {
					attackItem = SoliniaItemAdapter.Adapt(attackerSolEntity.getBukkitLivingEntity().getEquipment().getItemInMainHand());
					attackStack = attackerSolEntity.getBukkitLivingEntity().getEquipment().getItemInMainHand();
				}
	
				if (isOffhand && ItemStackUtils.IsSoliniaItem(attackerSolEntity.getBukkitLivingEntity().getEquipment().getItemInOffHand())) {
					attackItem = SoliniaItemAdapter.Adapt(attackerSolEntity.getBukkitLivingEntity().getEquipment().getItemInOffHand());
					attackStack = attackerSolEntity.getBukkitLivingEntity().getEquipment().getItemInOffHand();
				}
				
				if (attackItem != null) {
					TryProcItem(attackItem, attackerSolEntity, this, isOffhand);
				}
				
				// try spell procs
				// Check if attacker has any WeaponProc effects
				damageEffects(attackStack, sourceEntity, StateManager.getInstance().getEntityManager().getActiveEntitySpells((LivingEntity)sourceEntity));
			} catch (CoreStateInitException e) {
				
			} catch (SoliniaItemException e) {
				
			} catch (Exception e)
			{
				System.out.println("Failed to perform proc in damage routine");
				e.printStackTrace();
			}
		}

		checkNumHitsRemaining(NumHit.IncomingHitAttempts);
		if (!sourceEntity.isDead() && sourceEntity instanceof LivingEntity) {
			ISoliniaLivingEntity solLivingEntity = null;
			try {
				solLivingEntity = SoliniaLivingEntityAdapter.Adapt((LivingEntity) sourceEntity);
				solLivingEntity.checkNumHitsRemaining(NumHit.OutgoingHitAttempts);
			} catch (CoreStateInitException e) {

			}

		}
	}
	
	private void damageEffectActiveSpell(ItemStack attackStack, Entity sourceEntity, SoliniaActiveSpell activeSpell )
	{
		try
		{
			ISoliniaSpell spell = StateManager.getInstance().getConfigurationManager()
					.getSpell(activeSpell.getSpellId());
			if (spell == null)
				return;
	
			if (!spell.isWeaponProc())
				return;
	
			for (ActiveSpellEffect spelleffect : activeSpell.getActiveSpellEffects()) {
				damageEffectActiveSpellEffect(attackStack, sourceEntity,activeSpell,spelleffect);
			}
		} catch (CoreStateInitException e)
		{
			
		}
	}
	
	private void damageEffectActiveSpellEffect(ItemStack attackStack, Entity sourceEntity,SoliniaActiveSpell activeSpell, ActiveSpellEffect spelleffect)
	{
		try
		{
		if (spelleffect.getSpellEffectType().equals(SpellEffectType.WeaponProc)
				|| spelleffect.getSpellEffectType().equals(SpellEffectType.AddMeleeProc))
		{
			if (spelleffect.getBase() < 0)
				return;

			ISoliniaSpell procSpell = StateManager.getInstance().getConfigurationManager()
					.getSpell(spelleffect.getBase());
			
			if (attackStack != null && !activeSpell.getRequiredWeaponSkillType().equals(""))
			{
				if (!ItemStackUtils.getMeleeSkillForItemStack(attackStack).getSkillname().toUpperCase().equals(activeSpell.getRequiredWeaponSkillType().toUpperCase()))
					return;
			}

			// Chance to proc
			int procChance = getProcChancePct();
			int roll = Utils.RandomBetween(0, 100);

			if (roll < procChance) {
				boolean itemUseSuccess = procSpell.tryApplyOnEntity((LivingEntity)sourceEntity,
						getBukkitLivingEntity(), true, "");

				if (itemUseSuccess) {
					checkNumHitsRemaining(NumHit.OffensiveSpellProcs, 0, procSpell.getId());
				}

				if (procSpell.getActSpellCost(this) > 0)
					if (itemUseSuccess) {
						if (sourceEntity instanceof Player) {
							SoliniaPlayerAdapter.Adapt((Player) sourceEntity)
									.reducePlayerMana(procSpell.getActSpellCost(this));
						}
					}
			}
		}
		} catch (CoreStateInitException e)
		{
			
		}
	}

	private void damageEffects(ItemStack attackStack, Entity sourceEntity, SoliniaEntitySpells activeEntitySpells)
	{
		if (activeEntitySpells != null) {
			for (SoliniaActiveSpell activeSpell : activeEntitySpells.getActiveSpells()) {
				damageEffectActiveSpell(attackStack, sourceEntity, activeSpell);
			}
		}
	}

	private void triggerDefensiveProcs(ISoliniaLivingEntity defender, int damage, boolean arrowHit) {
		if (damage < 0)
			return;

		if (arrowHit)
			return;

		if (this.getBukkitLivingEntity().isDead() || defender.getBukkitLivingEntity().isDead())
			return;

		try {
			SoliniaEntitySpells effects = StateManager.getInstance().getEntityManager()
					.getActiveEntitySpells(defender.getBukkitLivingEntity());
			if (effects == null)
				return;

			for (SoliniaActiveSpell activeSpell : effects.getActiveSpells()) {
				ISoliniaSpell spell = StateManager.getInstance().getConfigurationManager()
						.getSpell(activeSpell.getSpellId());

				if (spell == null)
					continue;

				if (!spell.isDamageShield())
					continue;

				for (ActiveSpellEffect spelleffect : activeSpell.getActiveSpellEffects()) {
					if (spelleffect.getSpellEffectType().equals(SpellEffectType.DamageShield)) {
						// hurt enemy with damage shield
						if (spelleffect.getCalculatedValue() < 0) {
							EntityDamageSource source = new EntityDamageSource("thorns",
									((CraftEntity) defender.getBukkitLivingEntity()).getHandle());
							source.setMagic();
							source.ignoresArmor();

							addToHateList(source.getEntity().getUniqueID(), (spelleffect.getCalculatedValue() * -1), false);

							((CraftEntity) this.getBukkitLivingEntity()).getHandle().damageEntity(source,
									spelleffect.getCalculatedValue() * -1);
							// attacker.damage(spelleffect.getBase() * -1);

							DecimalFormat df = new DecimalFormat();
							df.setMaximumFractionDigits(2);

							defender.checkNumHitsRemaining(NumHit.DefensiveSpellProcs, 0, activeSpell.getSpellId());

							if (defender instanceof Player) {
								((Player) defender.getBukkitLivingEntity()).spigot().sendMessage(
										ChatMessageType.ACTION_BAR,
										new TextComponent("Your damage shield hit "
												+ this.getBukkitLivingEntity().getName() + " for "
												+ df.format(spelleffect.getCalculatedValue() * -1) + "["
												+ df.format(this.getBukkitLivingEntity().getHealth()
														- (spelleffect.getCalculatedValue() * -1))
												+ "/" + df.format(this.getBukkitLivingEntity()
														.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue())
												+ "]"));
							}
						}
					}
				}
			}
		} catch (CoreStateInitException e) {
			return;
		}
	}

	private DamageHitInfo doAttack(ISoliniaLivingEntity defender, DamageHitInfo hit) {
		if (defender == null)
			return hit;
		
		Utils.DebugLog("SoliniaLivingEntity", "doAttack", this.getBukkitLivingEntity().getName(), "Starting doAttack hit.base_damage: " + hit.base_damage + " hit.damage_done:" + hit.damage_done + " hit.offense:" + hit.offense);

		// for riposte
		int originalDamage = hit.damage_done;
		Utils.DebugLog("SoliniaLivingEntity", "doAttack", this.getBukkitLivingEntity().getName(), "originalDamage: " + hit.damage_done);

		hit = defender.avoidDamage(this, hit);
		Utils.DebugLog("SoliniaLivingEntity", "doAttack", this.getBukkitLivingEntity().getName(), "After avoidDamage hit.damage_done: " + hit.damage_done);
		if (hit.avoided == true) {
			// TODO Strike through

			if (hit.riposted == true && originalDamage > 0) {
				final UUID defenderUUID = defender.getBukkitLivingEntity().getUniqueId();
				final UUID attackerUUID = this.getBukkitLivingEntity().getUniqueId();
				final int finaloriginaldamage = originalDamage;

				Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("Solinia3Core"),
						new Runnable() {
							public void run() {
								try {
									Entity entity = Bukkit.getEntity(attackerUUID);

									if (entity == null || !(entity instanceof LivingEntity))
										return;

									ISoliniaLivingEntity solLivingEntity = null;
									try {
										solLivingEntity = SoliniaLivingEntityAdapter.Adapt((LivingEntity) entity);
									} catch (CoreStateInitException e) {

									}

									if (solLivingEntity == null)
										return;

									solLivingEntity.doRiposte(defenderUUID, finaloriginaldamage);
								} catch (Exception e) {
									System.out.println("An error occured during the doRiposte scheduler: "
											+ e.getMessage() + " " + e.getStackTrace());
								}
							}
						});

			}

			if (hit.dodged == true) {
				if (defender.isPlayer()) {
					((Player) (defender.getBukkitLivingEntity())).spigot().sendMessage(ChatMessageType.ACTION_BAR,
							new TextComponent(ChatColor.GRAY + "* You dodge the attack!"));

					defender.tryIncreaseSkill("DODGE", 1);
				}

				if (isPlayer()) {
					((Player) getBukkitLivingEntity()).sendMessage(ChatColor.GRAY + "* "
							+ defender.getBukkitLivingEntity().getCustomName() + " dodges your attack!");
				}
			}
		}

		if (hit.damage_done >= 0) {
			if (defender.checkHitChance(this, hit)) {
				hit = defender.meleeMitigation(this, hit);
				Utils.DebugLog("SoliniaLivingEntity", "doAttack", this.getBukkitLivingEntity().getName(), "After meleeMitigation hit.damage_done: " + hit.damage_done);
				if (hit.damage_done > 0) {
					hit = applyDamageTable(hit);
					Utils.DebugLog("SoliniaLivingEntity", "doAttack", this.getBukkitLivingEntity().getName(), "After applyDamageTable hit.damage_done: " + hit.damage_done);
					hit = commonOutgoingHitSuccess(defender, hit);
					Utils.DebugLog("SoliniaLivingEntity", "doAttack", this.getBukkitLivingEntity().getName(), "After commonOutgoingSuccess hit.damage_done: " + hit.damage_done);
				}
			} else {
				if (getBukkitLivingEntity() instanceof Player) {
					((Player) getBukkitLivingEntity()).spigot().sendMessage(ChatMessageType.ACTION_BAR,
							new TextComponent("You tried to hit " + defender.getBukkitLivingEntity().getCustomName()
									+ ", but missed!"));
				}
				if (defender.getBukkitLivingEntity() instanceof Player) {
					((Player) defender.getBukkitLivingEntity()).spigot().sendMessage(ChatMessageType.ACTION_BAR,
							new TextComponent(
									getBukkitLivingEntity().getCustomName() + " tried to hit you, but missed!"));
					try {
						ISoliniaPlayer solplayer = SoliniaPlayerAdapter
								.Adapt((Player) defender.getBukkitLivingEntity());
						solplayer.tryIncreaseSkill("DEFENSE", 1);
					} catch (CoreStateInitException e) {
						// skip
					}
				}
				hit.damage_done = 0;
			}
		}

		return hit;
	}

	@Override
	public void doDualWield(UUID defenderUuid, int damageDone, int offhandItemId) {
		// HACK: This should happen first to prevent multi firing endless loop
		setLastDualWield();

		Entity defenderEntity = Bukkit.getEntity(defenderUuid);
		if (defenderEntity == null || !(defenderEntity instanceof LivingEntity))
			return;

		ISoliniaLivingEntity defender = null;

		try {
			defender = SoliniaLivingEntityAdapter.Adapt((LivingEntity) defenderEntity);
		} catch (CoreStateInitException e) {

		}

		if (defender == null)
			return;

		if (defender.getBukkitLivingEntity().isDead())
			return;

		defender.damage(damageDone, getBukkitLivingEntity(), true, true, true);

		// Only players get skill rise
		if (getBukkitLivingEntity() instanceof Player) {
			((Player) getBukkitLivingEntity()).spigot().sendMessage(ChatMessageType.CHAT,
					new TextComponent(ChatColor.GRAY + "* You dual wield [" + damageDone + "]!"));
			tryIncreaseSkill("DUALWIELD", 1);

			PacketPlayOutAnimation packet = new PacketPlayOutAnimation(
					((CraftPlayer) getBukkitLivingEntity()).getHandle(), 3);
			((CraftPlayer) getBukkitLivingEntity()).getHandle().playerConnection.sendPacket(packet);
			getBukkitLivingEntity().getWorld().playSound(getBukkitLivingEntity().getLocation(),
					Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0F, 1.0F);
			((CraftPlayer) getBukkitLivingEntity()).getHandle().playerConnection.sendPacket(packet);

			for (Entity listening : getBukkitLivingEntity().getNearbyEntities(25, 25, 25)) {
				if (listening instanceof Player)
					((CraftPlayer) listening).getHandle().playerConnection.sendPacket(packet);
			}
		}
	}

	@Override
	public void doDoubleAttack(UUID defenderUuid, int damageDone) {
		// HACK: This should happen first to prevent multi firing endless loop
		setLastDoubleAttack();

		Entity defenderEntity = Bukkit.getEntity(defenderUuid);
		if (defenderEntity == null || !(defenderEntity instanceof LivingEntity))
			return;

		ISoliniaLivingEntity defender = null;

		try {
			defender = SoliniaLivingEntityAdapter.Adapt((LivingEntity) defenderEntity);
		} catch (CoreStateInitException e) {

		}

		if (defender == null)
			return;

		if (defender.getBukkitLivingEntity().isDead())
			return;

		// Only players get skill rise
		if (getBukkitLivingEntity() instanceof Player) {

			((Player) getBukkitLivingEntity()).spigot().sendMessage(ChatMessageType.CHAT,
					new TextComponent(ChatColor.GRAY + "* You double attack!"));
			tryIncreaseSkill("DOUBLEATTACK", 1);
		}

		defender.damage(damageDone, getBukkitLivingEntity(), false, true, false);
	}

	@Override
	public void doRiposte(UUID defenderUuid, int originalDamage) {
		setLastRiposte();

		Entity defenderEntity = Bukkit.getEntity(defenderUuid);
		if (defenderEntity == null || !(defenderEntity instanceof LivingEntity))
			return;

		ISoliniaLivingEntity defender = null;

		try {
			defender = SoliniaLivingEntityAdapter.Adapt((LivingEntity) defenderEntity);
		} catch (CoreStateInitException e) {

		}

		if (defender == null)
			return;

		if (defender.getBukkitLivingEntity().isDead())
			return;

		if (defender.isPlayer()) {

			((Player) (defender.getBukkitLivingEntity())).spigot().sendMessage(ChatMessageType.CHAT,
					new TextComponent(ChatColor.GRAY + "* You riposte the attack!"));
			defender.tryIncreaseSkill("RIPOSTE", 1);
		}

		if (isPlayer()) {
			((Player) getBukkitLivingEntity()).sendMessage(ChatColor.GRAY + "* "
					+ defender.getBukkitLivingEntity().getCustomName() + " ripostes your attack!");
			damage(originalDamage, getBukkitLivingEntity(), false, true, false);
		}
	}

	private DamageHitInfo commonOutgoingHitSuccess(ISoliniaLivingEntity defender, DamageHitInfo hit) {
		if (defender == null)
			return hit;

		if (hit.skill.equals("ARCHERY"))
			hit.damage_done /= 2;

		if (hit.damage_done < 1)
			hit.damage_done = 1;

		// TODO Archery head shots
		if (hit.skill.equals("ARCHERY")) {

			int bonus = 0;
			int spellArcheryDamageModifier = getSpellBonuses(SpellEffectType.ArcheryDamageModifier);
			int aaArcheryDamageModifier = Utils.getHighestAAEffectEffectType(getBukkitLivingEntity(),
					SpellEffectType.ArcheryDamageModifier);
			bonus = spellArcheryDamageModifier + aaArcheryDamageModifier;

			hit.damage_done += hit.damage_done * bonus / 100;
			int headshot = tryHeadShot(defender, hit.skill);

			if (headshot > 0) {
				hit.damage_done = headshot;
			}

			else if (getClassObj() != null && getClassObj().getClass().getName().equals("RANGER") && getLevel() > 50) { // no
																														// double
																														// dmg
																														// on
																														// headshot
				if (defender.isNPC() && !defender.isRooted()) {
					hit.damage_done *= 2;
				}
			}
		}

		int extra_mincap = 0;
		int min_mod = hit.base_damage * getMeleeMinDamageMod_SE(hit.skill) / 100;
		// TODO Backstab

		hit.min_damage += getSkillDmgAmt(hit.skill);

		// TODO shielding mod2
		// TODO item melee mitigation

		hit.damage_done = applyMeleeDamageMods(hit.skill, hit.damage_done, defender);
		min_mod = Math.max(min_mod, extra_mincap);

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

					if (!Utils.getNumHitsType(spell.getSpell().getNumhitstype()).name().toLowerCase()
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

	private int tryHeadShot(ISoliniaLivingEntity defender, String skill) {
		// Only works on YOUR target.
		if (skill.equals("ARCHERY")
				&& !getBukkitLivingEntity().getUniqueId().equals(defender.getBukkitLivingEntity().getUniqueId())) {
			int HeadShot_Dmg = 0;
			int spellHeadShotModifier = getSpellBonuses(SpellEffectType.HeadShot);
			int aaHeadShotModifier = Utils.getHighestAAEffectEffectType(getBukkitLivingEntity(),
					SpellEffectType.HeadShot);
			HeadShot_Dmg = spellHeadShotModifier + aaHeadShotModifier;

			int HeadShot_Level = 0; // Get Highest Headshot Level

			int spellHeadShotLevelModifier = getSpellBonuses(SpellEffectType.HeadShotLevel);
			int aaHeadShotLevelModifier = Utils.getHighestAAEffectEffectType(getBukkitLivingEntity(),
					SpellEffectType.HeadShotLevel);

			HeadShot_Level = Math.max(spellHeadShotLevelModifier, aaHeadShotLevelModifier);

			if (HeadShot_Dmg > 0 && HeadShot_Level > 0 && (defender.getLevel() <= HeadShot_Level)) {
				int chance = getDexterity();
				chance = 100 * chance / (chance + 3500);

				chance *= 10;
				/*
				 * TODO reading base2 values from AAs int norm = aabonuses.HSLevel[1]; if (norm
				 * > 0) chance = chance * norm / 100;
				 */
				chance += aaHeadShotLevelModifier + spellHeadShotLevelModifier;
				if (Utils.RandomBetween(1, 1000) <= chance) {
					emote(" is hit by a fatal blow", false);
					return HeadShot_Dmg;
				}
			}
		}

		return 0;
	}

	private int getMeleeMinDamageMod_SE(String skill) {
		int dmg_mod = 0;

		// dmg_mod = itembonuses.MinDamageModifier[skill] +
		// spellbonuses.MinDamageModifier[skill] +
		// itembonuses.MinDamageModifier[EQEmu::skills::HIGHEST_SKILL + 1] +
		// spellbonuses.MinDamageModifier[EQEmu::skills::HIGHEST_SKILL + 1];

		if (dmg_mod < -100)
			dmg_mod = -100;

		return dmg_mod;
	}

	@Override
	public int getMaxBindWound_SE() {
		int bindmod = 0;

		int spellMaxBindWound = getSpellBonuses(SpellEffectType.MaxBindWound);
		int aaMaxBindWound = Utils.getHighestAAEffectEffectType(getBukkitLivingEntity(), SpellEffectType.MaxBindWound);

		bindmod += spellMaxBindWound;
		bindmod += aaMaxBindWound;

		return bindmod;
	}

	@Override
	public int getBindWound_SE() {
		int bindmod = 0;

		int spellMaxBindWound = getSpellBonuses(SpellEffectType.ImprovedBindWound);
		int aaMaxBindWound = Utils.getHighestAAEffectEffectType(getBukkitLivingEntity(),
				SpellEffectType.ImprovedBindWound);

		bindmod += spellMaxBindWound;
		bindmod += aaMaxBindWound;

		return bindmod;
	}

	private int getMeleeDamageMod_SE(String skill) {
		int dmg_mod = 0;

		int spellDamageModifier = getSpellBonuses(SpellEffectType.DamageModifier);
		int aaDamageModifier = Utils.getHighestAAEffectEffectType(getBukkitLivingEntity(),
				SpellEffectType.DamageModifier);

		dmg_mod += spellDamageModifier;
		dmg_mod += aaDamageModifier;

		if (dmg_mod < -100)
			dmg_mod = -100;

		return dmg_mod;
	}

	private int applyMeleeDamageMods(String skill, int damage_done, ISoliniaLivingEntity defender) {
		int dmgbonusmod = 0;

		dmgbonusmod += getMeleeDamageMod_SE(skill);

		if (defender != null) {
			if (defender.isPlayer() && defender.getClassObj() != null
					&& defender.getClassObj().getName().equals("WARRIOR"))
				dmgbonusmod -= 5;
			// 168 defensive
			// dmgbonusmod += (defender->spellbonuses.MeleeMitigationEffect +
			// itembonuses.MeleeMitigationEffect + aabonuses.MeleeMitigationEffect);
		}

		damage_done += damage_done * dmgbonusmod / 100;

		return damage_done;
	}

	private int getSkillDmgAmt(String skill) {
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

		// 2: Try Melee Critical

		boolean innateCritical = false;
		int critChance = Utils.getCriticalChanceBonus(this, hit.skill);
		if ((className.equals("WARRIOR") || className.equals("BERSERKER")) && getLevel() >= 12)
			innateCritical = true;
		else if (className.equals("RANGER") && getLevel() >= 12 && hit.skill.equals("ARCHERY"))
			innateCritical = true;
		else if (className.equals("ROGUE") && getLevel() >= 12 && hit.skill.equals("THROWING"))
			innateCritical = true;

		// we have a chance to crit!
		if (innateCritical || critChance > 0) {
			int difficulty = 0;
			if (hit.skill.equals("ARCHERY"))
				difficulty = 3400;
			else if (hit.skill.equals("THROWING"))
				difficulty = 1100;
			else
				difficulty = 8900;

			// attacker.sendMessage("You have a chance to cause a critical (Diffulty dice
			// roll: " + difficulty);
			int roll = Utils.RandomBetween(1, difficulty);
			// attacker.sendMessage("Critical chance roll ended up as: " + roll);

			int dex_bonus = getDexterity();
			if (dex_bonus > 255)
				dex_bonus = 255 + ((dex_bonus - 255) / 5);
			dex_bonus += 45;
			// attacker.sendMessage("Critical dex bonus was: " + dex_bonus);

			// so if we have an innate crit we have a better chance, except for ber throwing
			if (!innateCritical || (className.equals("BERSERKER") && hit.skill.equals("THROWING")))
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
				int crit_mod = 170 + Utils.getCritDmgMod(hit.skill);
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
						((Player) getBukkitLivingEntity()).spigot().sendMessage(ChatMessageType.ACTION_BAR,
								new TextComponent("* Your berserker status causes additional critical blow damage ["
										+ df.format(totalCritBonus) + "]!"));
					}

					return hit;
				}

				if (getBukkitLivingEntity() instanceof Player) {
					((Player) getBukkitLivingEntity()).spigot().sendMessage(ChatMessageType.ACTION_BAR,
							new TextComponent(
									"You scored additional critical damage! [" + df.format(totalCritBonus) + "]"));
				}

				// attacker.sendMessage("* Your score a critical hit (" + damageDone + ")!");
				return hit;
			}

		}

		return hit;
	}

	private DamageHitInfo applyDamageTable(DamageHitInfo hit) {
		Utils.DebugLog("SoliniaLivingEntity", "applyDamageTable", this.getBukkitLivingEntity().getName(), "Starting applyDamageTable (hit.offense: " + hit.offense + " + hit.damage_done: " + hit.damage_done + ")");
		
		if (hit.offense < 115)
			return hit;

		if (hit.damage_done < 2)
			return hit;

		// 0 = max_extra
		// 1 = chance
		// 2 = minusfactor
		int[] damage_table = getDamageTable();
		Utils.DebugLog("SoliniaLivingEntity", "applyDamageTable", this.getBukkitLivingEntity().getName(), "Damage table [" + damage_table[0] + "," + damage_table[1] + "," + damage_table[2] + "]");


		if (Utils.RandomBetween(0, 100) < (damage_table[1]))
			return hit;

		Utils.DebugLog("SoliniaLivingEntity", "applyDamageTable", this.getBukkitLivingEntity().getName(), "Damage table rolled less than " + damage_table[1]);

		
		int basebonus = hit.offense - damage_table[2];

		Utils.DebugLog("SoliniaLivingEntity", "applyDamageTable", this.getBukkitLivingEntity().getName(), "basebonus: " + basebonus);

		basebonus = Math.max(10, basebonus / 2);
		Utils.DebugLog("SoliniaLivingEntity", "applyDamageTable", this.getBukkitLivingEntity().getName(), "basebonus2: " + basebonus);

		int extrapercent = Utils.RandomBetween(0, basebonus);
		Utils.DebugLog("SoliniaLivingEntity", "applyDamageTable", this.getBukkitLivingEntity().getName(), "extrapercent: " + extrapercent);

		int percent = Math.min(100 + extrapercent, damage_table[0]);
		Utils.DebugLog("SoliniaLivingEntity", "applyDamageTable", this.getBukkitLivingEntity().getName(), "percent: " + percent);

		hit.damage_done = (hit.damage_done * percent) / 100;
		Utils.DebugLog("SoliniaLivingEntity", "applyDamageTable", this.getBukkitLivingEntity().getName(), "damage_done: " + hit.damage_done);

		if (getClassObj() != null) {
			if (getClassObj().isWarriorClass() && getLevel() > 54)
				hit.damage_done++;
		}

		Utils.DebugLog("SoliniaLivingEntity", "applyDamageTable", this.getBukkitLivingEntity().getName(), "damage_done2: " + hit.damage_done);
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
		
		int level = Math.min(getLevel(), maxLevel);

		if (!melee || (!monk && level < 51))
			return dmg_table[0];

		if (monk && level < 51)
			return mnk_table[0];

		int[][] which = monk ? mnk_table : dmg_table;
		return which[level - 50];
	}

	@Override
	public int getTotalItemStat(String stat) {
		int total = 0;

		try {
			List<ISoliniaItem> items = new ArrayList<ISoliniaItem>();

			if (!stat.equals("STAMINA"))
				items = getEquippedSoliniaItems();
			else
				items = getEquippedSoliniaItems(true);

			Utils.DebugLog("SoliniaLivingEntity", "getTotalItemStat", this.getBukkitLivingEntity().getName(), "Found Equipped Item Count: " + items.size());
			
			for (ISoliniaItem item : items) {
				Utils.DebugLog("SoliniaLivingEntity", "getTotalItemStat", this.getBukkitLivingEntity().getName(), "Found Equipped Item for TotalItemStat: " + item.getId());

				switch (stat) {
				case "STRENGTH":
					if (item.getStrength() > 0) {
						total += item.getStrength();
					}
					break;
				case "STAMINA":
					if (item.getStamina() > 0) {
						total += item.getStamina();
					}
					break;
				case "AGILITY":
					if (item.getAgility() > 0) {
						total += item.getAgility();
					}
					break;
				case "DEXTERITY":
					if (item.getDexterity() > 0) {
						total += item.getDexterity();
					}
					break;
				case "INTELLIGENCE":
					if (item.getIntelligence() > 0) {
						total += item.getIntelligence();
					}
					break;
				case "WISDOM":
					if (item.getWisdom() > 0) {
						total += item.getWisdom();
					}
					break;
				case "CHARISMA":
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
	public int getOffense(String skillname) {
		Utils.DebugLog("SoliniaLivingEntity","getOffense",this.getBukkitLivingEntity().getName(),"getOffense starts for " + skillname);
		int offense = getSkill(skillname);
		Utils.DebugLog("SoliniaLivingEntity","getOffense",this.getBukkitLivingEntity().getName(),"getSkill value found " + offense);
		int stat_bonus = 0;
		if (skillname.equals("ARCHERY") || skillname.equals("THROWING"))
		{
			stat_bonus = getDexterity();
			Utils.DebugLog("SoliniaLivingEntity","getOffense",this.getBukkitLivingEntity().getName(),"Using dexterity value for stat bonus " + stat_bonus);
		}
		else
		{
			stat_bonus = getStrength();
			Utils.DebugLog("SoliniaLivingEntity","getOffense",this.getBukkitLivingEntity().getName(),"Using strength value for stat bonus " + stat_bonus);
		}
		if (stat_bonus >= 75)
		{
			Utils.DebugLog("SoliniaLivingEntity","getOffense",this.getBukkitLivingEntity().getName(),stat_bonus + " was found to be greater than 75 so capping");
			offense += (2 * stat_bonus - 150) / 3;
			Utils.DebugLog("SoliniaLivingEntity","getOffense",this.getBukkitLivingEntity().getName(),stat_bonus + " offense now capped at " + offense);
		}

		// TODO do ATTK
		int attk = getTotalAtk();
		offense += attk;
		Utils.DebugLog("SoliniaLivingEntity","getOffense",this.getBukkitLivingEntity().getName(),stat_bonus + " added attk (" +attk + ") to offense - final offense value is: " + offense);
		return offense;
	}
	
	@Override
	public int getTotalAtk()
	{
		int attackRating = 0;
		
		int itemBonusesAtk = 0; // todo
		int aabonusesAtk = 0; // todo
		int spellbonusesAtk = getSpellBonuses(SpellEffectType.ATK);
		
		int WornCap = itemBonusesAtk;

		if(this.isPlayer()) {
			
			double attackRatingDbl = Math.floor(((WornCap * 1.342) + (getSkill("OFFENSE") * 1.345) + ((getStrength() - 66) * 0.9) + (getPrimarySkillValue() * 2.69)));
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
		
		String skill = ItemStackUtils.getMeleeSkillForItemStack(this.getBukkitLivingEntity().getEquipment().getItemInMainHand()).getSkillname().toUpperCase();
		return getSkill(skill);
	}

	public int getAtk() {
		// this should really only be happening for npcs
		int attackItemBonuses = 0;
		// todo, item bonuses

		int attackSpellBonsues = 0;

		attackSpellBonsues += getSpellBonuses(SpellEffectType.ATK);

		// TODO, find a place for this base value, possibly on race?
		int ATK = 0;
		// this is from the bot code..
		return ATK + attackItemBonuses + attackSpellBonsues + ((getStrength() + getSkill("OFFENSE")) * 9 / 10);
	}
	
	@Override
	public void tryIncreaseSkill(String skillName, int amount) {
		if (!isPlayer())
			return;

		try {
			ISoliniaPlayer solplayerReward = SoliniaPlayerAdapter.Adapt((Player) this.getBukkitLivingEntity());
			solplayerReward.tryIncreaseSkill(skillName, amount);
		} catch (CoreStateInitException e) {
			// dont increase skill
		}
	}

	@Override
	public int getWeaponDamageBonus(ItemStack itemStack) {
		int level = getLevel();
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
	public int getSkill(String skillname) {
		int defaultskill = 0;

		try {
			if (isPlayer()) {
				ISoliniaPlayer player = SoliniaPlayerAdapter.Adapt((Player) getBukkitLivingEntity());
				return player.getSkill(skillname.toUpperCase()).getValue();
			}

			if (isNPC()) {
				ISoliniaNPC npc = getNPC();
				return npc.getSkill(skillname.toUpperCase());
			}
		} catch (CoreStateInitException e) {
			return defaultskill;
		}

		return defaultskill;
	}

	@Override
	public int computeToHit(String skillname) {
		double tohit = getSkill("OFFENSE") + 7;
		tohit += getSkill(skillname.toUpperCase());
		if (isNPC()) {
			ISoliniaNPC npc = getNPC();
			if (npc != null)
				tohit += npc.getAccuracyRating();
		}
		if (isPlayer()) {
			double reduction = getIntoxication() / 2.0;
			if (reduction > 20.0) {
				reduction = Math.min((110 - reduction) / 100.0, 1.0);
				tohit = reduction * (double) (tohit);
			} else if (isBerserk()) {
				tohit += (getLevel() * 2) / 5;
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
				if (this.getClassObj().getName().equals("WARRIOR"))
					return true;
			}

		return false;
	}

	@Override
	public int getTotalToHit(String skillname, int hitChanceBonus) {
		if (hitChanceBonus >= 10000) // override for stuff like SE_SkillAttack
			return -1;

		skillname = skillname.toUpperCase();

		// calculate attacker's accuracy
		double accuracy = computeToHit(skillname) + 10;
		if (hitChanceBonus > 0) // multiplier
			accuracy *= hitChanceBonus;

		accuracy = (accuracy * 121) / 100;

		/*
		 * if (!skillname.equals("ARCHERY") && !skillname.equals("THROWING")) { accuracy
		 * += getItemBonuses("HITCHANCE"); }
		 */

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
	public int getDefenseByDefenseSkill() {
		double defense = getSkill("DEFENSE") * 400 / 225;
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
		int bonus = 0;
		for (ActiveSpellEffect effect : Utils.getActiveSpellEffects(getBukkitLivingEntity(), spellEffectType)) {
			bonus += effect.getRemainingValue();
		}

		return bonus;
	}

	@Override
	public int getAttackSpeed() {
		int lowestAttackSpeedBuff = 100;
		int highestAttackSpeedBuff = 100;

		List<SpellEffectType> effectTypes = new ArrayList<SpellEffectType>();
		effectTypes.add(SpellEffectType.AttackSpeed);
		effectTypes.add(SpellEffectType.AttackSpeed2);
		effectTypes.add(SpellEffectType.AttackSpeed3);
		effectTypes.add(SpellEffectType.AttackSpeed4);

		int maxItemAttackSpeed = getMaxItemAttackSpeedPct();

		// Include item passive hastes but only if the item provides more than 1%
		if (maxItemAttackSpeed > 100) {
			if (maxItemAttackSpeed > highestAttackSpeedBuff)
				highestAttackSpeedBuff = maxItemAttackSpeed;
		}

		// Takes into account slows (< 100 attackspeed)
		for (ActiveSpellEffect effect : Utils.getActiveSpellEffects(getBukkitLivingEntity(),
				SpellEffectType.AttackSpeed)) {
			if (effect.getRemainingValue() > 100) {
				if (effect.getRemainingValue() > highestAttackSpeedBuff)
					highestAttackSpeedBuff = effect.getRemainingValue();
			} else {
				if (effect.getRemainingValue() < lowestAttackSpeedBuff)
					lowestAttackSpeedBuff = effect.getRemainingValue();
			}
		}

		if (lowestAttackSpeedBuff < 100)
			return lowestAttackSpeedBuff;

		return highestAttackSpeedBuff;
	}

	@Override
	public int getTotalDefense() {
		double avoidance = getDefenseByDefenseSkill() + 10;

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
		if (isPlayer())
			return false;

		if (this.getNpcid() < 1)
			return false;

		ISoliniaNPC npc = getNPC();
		if (npc.isUndead())
			return true;

		return false;
	}

	@Override
	public boolean isPlant() {
		if (isPlayer())
			return false;

		if (this.getNpcid() < 1)
			return false;

		ISoliniaNPC npc = getNPC();
		if (npc.isPlant())
			return true;

		return false;
	}

	@Override
	public boolean isAnimal() {
		if (isPlayer())
			return false;

		if (this.getNpcid() < 1)
			return false;

		ISoliniaNPC npc = getNPC();
		if (npc.isAnimal())
			return true;

		return false;
	}

	@Override
	public int getLevel() {
		if (isPlayer()) {
			try {
				ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player) this.getBukkitLivingEntity());
				return solPlayer.getLevel();
			} catch (CoreStateInitException e) {
				return 0;
			}
		}

		return level;
	}

	@Override
	public void setLevel(int level) {
		if (isPlayer())
			return;

		this.level = level;
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
						DropUtils.DropLoot(npc.getClassObj().getDropSpellsLootTableId(),this.getBukkitLivingEntity().getWorld(),this.getBukkitLivingEntity().getLocation(),npc.getClassObj().getName().toUpperCase(),npc.getLevel());
				
				SoliniaWorld world = StateManager.getInstance().getConfigurationManager().getWorld(this.getBukkitLivingEntity().getWorld().getName());
				if (world != null)
					if (world.getGlobalLootTableId() > 0)
						DropUtils.DropLoot(world.getGlobalLootTableId(),this.getBukkitLivingEntity().getWorld(),this.getBukkitLivingEntity().getLocation(),"",0);
						
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
	public void emote(String message, boolean isBardSongFilterable) {
		StateManager.getInstance().getChannelManager().sendToLocalChannel(this, ChatColor.AQUA + "* " + message,
				isBardSongFilterable, getBukkitLivingEntity().getEquipment().getItemInMainHand());
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
		int random = Utils.RandomBetween(1, 100);
		if (random < 2) {
			this.say(npc.getRandomchatTriggerText());
		}
	}

	@Override
	public void say(String message) {
		if (isPlayer())
			return;

		if (this.getNpcid() < 1)
			return;

		ISoliniaNPC npc = getNPC();
		if (npc == null)
			return;

		String decoratedMessage = ChatColor.AQUA + npc.getName() + " says '" + message + "'" + ChatColor.RESET;
		StateManager.getInstance().getChannelManager().sendToLocalChannelLivingEntityChat(this, decoratedMessage,
				true, message, getBukkitLivingEntity().getEquipment().getItemInMainHand());
	}

	@Override
	public void sayto(Player player, String message, boolean allowlanguagelearn) {
		if (isPlayer())
			return;

		if (this.getNpcid() < 1)
			return;

		try {
			ISoliniaNPC npc = getNPC();
			if (npc == null)
				return;
			
			if (player.isOp() || (getLanguage() == null || isSpeaksAllLanguages()
					|| SoliniaPlayerAdapter.Adapt(player).understandsLanguage(getLanguage()))) {
				String decoratedMessage = ChatColor.AQUA + npc.getName() + " says to " + player.getDisplayName() + " '" + message
						+ "'"  + " [" + getLanguage() + "]" + ChatColor.RESET;
				player.sendMessage(decoratedMessage);
			} else {
				String decoratedMessage = ChatColor.AQUA + npc.getName() + " says to " + player.getDisplayName() + " '" + Utils.garbleText(message,SoliniaPlayerAdapter.Adapt(player).getLanguageLearnedPercent(getLanguage()))
						+ "' (You do not fully understand this language)" + ChatColor.RESET;
				player.sendMessage(decoratedMessage);

				if (allowlanguagelearn == true) {
					if (getLanguage() != null && !getLanguage().equals(""))
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

			if (player.isOp() || (getLanguage() == null || isSpeaksAllLanguages()
					|| SoliniaPlayerAdapter.Adapt(player).understandsLanguage(getLanguage()))) {
				String decoratedMessage = ChatColor.AQUA + npc.getName() + " says '" + message + "'"  + " [" + getLanguage() + "]" + ChatColor.RESET;
				player.sendMessage(decoratedMessage);
			} else {
				String decoratedMessage = ChatColor.AQUA + npc.getName() + " says '" + Utils.garbleText(message,SoliniaPlayerAdapter.Adapt(player).getLanguageLearnedPercent(getLanguage())) + "' (You do not fully understand this language)" + ChatColor.RESET;
				player.sendMessage(decoratedMessage);

				if (getLanguage() != null && !getLanguage().equals(""))
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
					allowlanguagelearn, message, getBukkitLivingEntity().getEquipment().getItemInMainHand());
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

			this.emote(npc.getName() + " says '" + npc.getKillTriggerText() + "'" + ChatColor.RESET, false);
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void aiEngagedCastCheck(Plugin plugin, ISoliniaNPC npc, LivingEntity castingAtEntity)
			throws CoreStateInitException {
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

		// Try self buff, then nearby then target detrimental
		Utils.DebugMessage("NPC: " + npc.getName() + this.getBukkitLivingEntity().getUniqueId().toString()
				+ " attempting to cast self buff");
		if (!aiCastSpell(plugin, npc, this.getBukkitLivingEntity(), engagedBeneficialSelfChance,
				beneficialSelfSpells)) {
			Utils.DebugMessage("NPC: " + npc.getName() + this.getBukkitLivingEntity().getUniqueId().toString()
					+ " attempting to cast others buff");
			if (!aiCheckCloseBeneficialSpells(plugin, npc, engagedBeneficialOtherChance,
					StateManager.getInstance().getEntityManager().getAIBeneficialBuffSpellRange(),
					beneficialOtherSpells)) {
				Utils.DebugMessage("NPC: " + npc.getName() + this.getBukkitLivingEntity().getUniqueId().toString()
						+ " attempting to cast detrimental");
				if (!aiCastSpell(plugin, npc, castingAtEntity, engagedDetrimentalOtherChance, detrimentalSpells)) {
					Utils.DebugMessage("NPC: " + npc.getName() + this.getBukkitLivingEntity().getUniqueId().toString()
							+ " cannot cast at all");
				}
			}
		}
	}

	@Override
	public boolean aiCheckCloseBeneficialSpells(Plugin plugin, ISoliniaNPC npc, int iChance, int iRange,
			int iSpellTypes) throws CoreStateInitException {
		if (((iSpellTypes & SpellType.Detrimental)) == SpellType.Detrimental) {
			Utils.DebugMessage("NPC: " + npc.getName() + this.getBukkitLivingEntity().getUniqueId().toString()
					+ " cannot cast a close beneficial spell as I have a detrimental in my list");
			return false;
		}

		if (this.getClassObj() == null) {
			Utils.DebugMessage("NPC: " + npc.getName() + this.getBukkitLivingEntity().getUniqueId().toString()
					+ " cannot cast a close beneficial spell as I have no class");
			return false;
		}

		if (iChance < 100) {
			if (Utils.RandomBetween(0, 99) >= iChance) {
				Utils.DebugMessage("NPC: " + npc.getName() + this.getBukkitLivingEntity().getUniqueId().toString()
						+ " cannot cast a close beneficial spell as i rolled less than the chance");
				return false;
			}
		}

		if (npc.getFactionid() == 0) {
			Utils.DebugMessage("NPC: " + npc.getName() + this.getBukkitLivingEntity().getUniqueId().toString()
					+ " cannot cast a close beneficial spell as I have no faction");
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

			if (aiCastSpell(plugin, npc, mob.getBukkitLivingEntity(), 100, iSpellTypes))
				return true;
		}
		return false;
	}

	@Override
	public boolean aiDoSpellCast(Plugin plugin, ISoliniaSpell spell, ISoliniaLivingEntity target, int manaCost) {
		// TODO Spell Casting Times
		// if(!spell.isBardSong()) {
		// SetCurrentSpeed(0);
		// }

		boolean success = false;
		if (getMana() > spell.getActSpellCost(this)) {
			success = spell.tryApplyOnEntity(this.getBukkitLivingEntity(), target.getBukkitLivingEntity(), true, "");
		}

		if (success) {
			this.setMana(this.getMana() - spell.getActSpellCost(this));
		}

		return success;
	}

	@Override
	public boolean aiCastSpell(Plugin plugin, ISoliniaNPC npc, LivingEntity target, int iChance, int iSpellTypes)
			throws CoreStateInitException {
		if (this.getClassObj() == null) {
			Utils.DebugMessage("NPC: " + npc.getName() + this.getBukkitLivingEntity().getUniqueId().toString()
					+ " cannot cast a spell as I have no class");
			return false;
		}

		NPCSpellList npcSpellList = null;

		if (npc.getNpcSpellList() < 1 && this.getClassObj().getNpcspelllist() < 1)
			return false;

		if (getClassObj().getNpcspelllist() > 0) {
			npcSpellList = StateManager.getInstance().getConfigurationManager()
					.getNPCSpellList(getClassObj().getNpcspelllist());
		}

		if (npc.getNpcSpellList() > 0) {
			npcSpellList = StateManager.getInstance().getConfigurationManager().getNPCSpellList(npc.getNpcSpellList());
		}

		if (iChance < 100) {
			int roll = Utils.RandomBetween(0, 100);
			if (roll >= iChance) {
				Utils.DebugMessage("NPC: " + npc.getName() + this.getBukkitLivingEntity().getUniqueId().toString()
						+ " cannot cast a spell as i rolled badly roll: " + roll + " vs chance: " + iChance);
				return false;
			}
		}

		// TODO escape distance

		boolean checked_los = false;

		double manaR = getManaRatio();

		List<NPCSpellListEntry> spells = new ArrayList<NPCSpellListEntry>();
		for (NPCSpellListEntry entry : npcSpellList.getSpellListEntry()) {
			if (npc.isCorePet())
				spells.add(entry);
			else if (npc.getLevel() >= entry.getMinlevel() && npc.getLevel() <= entry.getMaxlevel()) {
				spells.add(entry);
			}
		}

		Collections.sort(spells, new Comparator<NPCSpellListEntry>() {
			public int compare(NPCSpellListEntry o1, NPCSpellListEntry o2) {
				if (o1.getPriority() == o2.getPriority())
					return 0;

				return o1.getPriority() > o2.getPriority() ? -1 : 1;
			}
		});

		// AI has spells?
		if (spells.size() == 0) {
			Utils.DebugMessage("NPC: " + npc.getName() + this.getBukkitLivingEntity().getUniqueId().toString()
					+ " cannot cast a spell as I have no spells");
			return false;
		}

		for (NPCSpellListEntry spelllistentry : spells) {
			// Does spell types contain spelltype
			if ((iSpellTypes & spelllistentry.getType()) == spelllistentry.getType()) {
				ISoliniaSpell spell = StateManager.getInstance().getConfigurationManager()
						.getSpell(spelllistentry.getSpellid());
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
					Utils.DebugMessage("NPC: " + npc.getName() + this.getBukkitLivingEntity().getUniqueId().toString()
							+ " attempting to cast heal " + spell.getName());
					if ((SoliniaSpell.isValidEffectForEntity(target, this.getBukkitLivingEntity(), spell))
							&& !Utils.hasSpellActive(soltarget, spell)
							&& (Utils.getSpellTargetType(spell.getTargettype()).equals(SpellTargetType.Target)
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
					Utils.DebugMessage("NPC: " + npc.getName() + this.getBukkitLivingEntity().getUniqueId().toString()
							+ " attempting to cast root " + spell.getName());
					// TODO - Pick at random
					if ((SoliniaSpell.isValidEffectForEntity(target, this.getBukkitLivingEntity(), spell))
							&& !Utils.hasSpellActive(soltarget, spell) && !soltarget.isRooted() && Utils.RandomRoll(50)
							&& nowtimestamp.after(StateManager.getInstance().getEntityManager()
									.getDontSpellTypeMeBefore(target, SpellType.Root))
					// TODO buff stacking
					) {
						if (!checked_los) {
							if (!this.getBukkitLivingEntity().hasLineOfSight(target)) {
								Utils.DebugMessage(
										"NPC: " + npc.getName() + this.getBukkitLivingEntity().getUniqueId().toString()
												+ " could not cast as i could not see the arget");
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
					Utils.DebugMessage("NPC: " + npc.getName() + this.getBukkitLivingEntity().getUniqueId().toString()
							+ " attempting to cast buff " + spell.getName());
					if (((SoliniaSpell.isValidEffectForEntity(target, this.getBukkitLivingEntity(), spell))
							&& !Utils.hasSpellActive(soltarget, spell)
							&& Utils.getSpellTargetType(spell.getTargettype()).equals(SpellTargetType.Target)
							|| target.getUniqueId().equals(getBukkitLivingEntity().getUniqueId()))
							&& nowtimestamp.after(StateManager.getInstance().getEntityManager()
									.getDontSpellTypeMeBefore(target, SpellType.Buff))
							&& !spell.isInvisSpell()
					// TODO Spell immunities
					// TODO Spell stacking
					// TODO NPC Pets
					) {

						if (!checked_los) {
							if (!this.getBukkitLivingEntity().hasLineOfSight(target)) {
								Utils.DebugMessage(
										"NPC: " + npc.getName() + this.getBukkitLivingEntity().getUniqueId().toString()
												+ " could not cast as i could not see the arget");
								return false;
							}
							checked_los = true;
						} else {
							Utils.DebugMessage(
									"NPC: " + npc.getName() + this.getBukkitLivingEntity().getUniqueId().toString()
											+ " could not cast as i could not see the target (already)");
						}
						aiDoSpellCast(plugin, spell, soltarget, mana_cost);
						StateManager.getInstance().getEntityManager().setDontSpellTypeMeBefore(target, SpellType.Buff,
								expiretimestamp);
						Utils.DebugMessage(
								"NPC: " + npc.getName() + this.getBukkitLivingEntity().getUniqueId().toString()
										+ " buff appears to be successful");
						return true;
					} else {
						Utils.DebugMessage("NPC: " + npc.getName()
								+ this.getBukkitLivingEntity().getUniqueId().toString()
								+ " could not cast as either the spell target was wrong, the target was not me or i have already buffed myself recently");
					}
					break;
				case SpellType.Escape:
					// TODO Gate/Escape
					return false;
				case SpellType.Slow:
				case SpellType.Debuff:
					Utils.DebugMessage("NPC: " + npc.getName() + this.getBukkitLivingEntity().getUniqueId().toString()
							+ " attempting to cast debuff " + spell.getName());
					// TODO debuff at random
					if ((SoliniaSpell.isValidEffectForEntity(target, this.getBukkitLivingEntity(), spell))
							&& !Utils.hasSpellActive(soltarget, spell) && manaR >= 10 && Utils.RandomRoll(70)
					// TODO buff stacking
					) {
						if (!checked_los) {
							if (!this.getBukkitLivingEntity().hasLineOfSight(target)) {
								Utils.DebugMessage(
										"NPC: " + npc.getName() + this.getBukkitLivingEntity().getUniqueId().toString()
												+ " could not cast as i could not see the arget");
								return false;
							}
							checked_los = true;
						}
						aiDoSpellCast(plugin, spell, soltarget, mana_cost);
						return true;
					}
					break;
				case SpellType.Nuke:
					Utils.DebugMessage("NPC: " + npc.getName() + this.getBukkitLivingEntity().getUniqueId().toString()
							+ " attempting to cast nuke " + spell.getName());
					boolean nukeRoll = Utils.RandomRoll(70);
					if ((SoliniaSpell.isValidEffectForEntity(target, this.getBukkitLivingEntity(), spell))
							&& !Utils.hasSpellActive(soltarget, spell) && manaR >= 10 && nukeRoll
					// TODO Buff Stacking check
					) {
						if (!checked_los) {
							if (!this.getBukkitLivingEntity().hasLineOfSight(target)) {
								Utils.DebugMessage(
										"NPC: " + npc.getName() + this.getBukkitLivingEntity().getUniqueId().toString()
												+ " could not cast as i could not see the arget");
								return false;
							}
							checked_los = true;
						} else {
							Utils.DebugMessage(
									"NPC: " + npc.getName() + this.getBukkitLivingEntity().getUniqueId().toString()
											+ " could not cast as i could not see the target (previously checked)");
						}
						Utils.DebugMessage(
								"NPC: " + npc.getName() + this.getBukkitLivingEntity().getUniqueId().toString()
										+ " nuke appears to be successful");
						aiDoSpellCast(plugin, spell, soltarget, mana_cost);
						return true;
					} else {
						Utils.DebugMessage(
								"NPC: " + npc.getName() + this.getBukkitLivingEntity().getUniqueId().toString()
										+ " could not cast nuke as either my mana ratio was too high (" + (manaR >= 10)
										+ ") or i rolled badly roll failure: (" + nukeRoll + ")");
					}
					break;
				case SpellType.Dispel:
					Utils.DebugMessage("NPC: " + npc.getName() + this.getBukkitLivingEntity().getUniqueId().toString()
							+ " attempting to cast dispell " + spell.getName());
					if ((SoliniaSpell.isValidEffectForEntity(target, this.getBukkitLivingEntity(), spell))
							&& !Utils.hasSpellActive(soltarget, spell) && Utils.RandomRoll(15)) {
						if (!checked_los) {
							if (!this.getBukkitLivingEntity().hasLineOfSight(target)) {
								Utils.DebugMessage(
										"NPC: " + npc.getName() + this.getBukkitLivingEntity().getUniqueId().toString()
												+ " could not cast as i could not see the arget");
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
					Utils.DebugMessage("NPC: " + npc.getName() + this.getBukkitLivingEntity().getUniqueId().toString()
							+ " attempting to cast mez " + spell.getName());
					if ((SoliniaSpell.isValidEffectForEntity(target, this.getBukkitLivingEntity(), spell))
							&& !Utils.hasSpellActive(soltarget, spell) && Utils.RandomRoll(20)) {
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
					Utils.DebugMessage("NPC: " + npc.getName() + this.getBukkitLivingEntity().getUniqueId().toString()
							+ " attempting to cast lifetap " + spell.getName());
					if ((SoliniaSpell.isValidEffectForEntity(target, this.getBukkitLivingEntity(), spell))
							&& !Utils.hasSpellActive(soltarget, spell) && getHPRatio() <= 95 && Utils.RandomRoll(50)
					// TODO Buff stacking
					) {
						if (!checked_los) {
							if (!this.getBukkitLivingEntity().hasLineOfSight(target)) {
								Utils.DebugMessage(
										"NPC: " + npc.getName() + this.getBukkitLivingEntity().getUniqueId().toString()
												+ " could not cast as i could not see the arget");
								return false;
							}
							checked_los = true;
						}

						aiDoSpellCast(plugin, spell, soltarget, mana_cost);
						return true;
					}
					break;
				case SpellType.Snare:
					Utils.DebugMessage("NPC: " + npc.getName() + this.getBukkitLivingEntity().getUniqueId().toString()
							+ " attempting to cast snare " + spell.getName());
					if ((SoliniaSpell.isValidEffectForEntity(target, this.getBukkitLivingEntity(), spell))
							&& !Utils.hasSpellActive(soltarget, spell) && !soltarget.isRooted() && Utils.RandomRoll(50)
							&& (nowtimestamp.after(StateManager.getInstance().getEntityManager()
									.getDontSpellTypeMeBefore(target, SpellType.Snare)))
					// TODO Buff stacking
					) {
						if (!checked_los) {
							if (!this.getBukkitLivingEntity().hasLineOfSight(target)) {
								Utils.DebugMessage(
										"NPC: " + npc.getName() + this.getBukkitLivingEntity().getUniqueId().toString()
												+ " could not cast as i could not see the arget");
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
					Utils.DebugMessage("NPC: " + npc.getName() + this.getBukkitLivingEntity().getUniqueId().toString()
							+ " attempting to cast dot " + spell.getName());
					if ((SoliniaSpell.isValidEffectForEntity(target, this.getBukkitLivingEntity(), spell))
							&& !Utils.hasSpellActive(soltarget, spell) && (Utils.RandomRoll(60))
							&& (nowtimestamp.after(StateManager.getInstance().getEntityManager()
									.getDontSpellTypeMeBefore(target, SpellType.DOT)))
					// TODO buff stacking
					) {

						if (!checked_los) {
							if (!this.getBukkitLivingEntity().hasLineOfSight(target)) {
								Utils.DebugMessage(
										"NPC: " + npc.getName() + this.getBukkitLivingEntity().getUniqueId().toString()
												+ " could not cast as i could not see the arget");
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
	public void doSpellCast(Plugin plugin, LivingEntity castingAtEntity) {
		if (isPlayer())
			return;

		if (!isNPC())
			return;

		if (castingAtEntity == null || this.livingentity == null)
			return;

		ISoliniaNPC npc;
		try {
			npc = StateManager.getInstance().getConfigurationManager().getNPC(this.getNpcid());
			if (npc.getClassid() < 1)
				return;

			this.setMana(this.getMana() + Utils.getDefaultNPCManaRegen(npc));

			aiEngagedCastCheck(plugin, npc, castingAtEntity);
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setMana(int amount) {
		if (isPlayer())
			return;

		if (this.getNpcid() < 1)
			return;

		try {
			ISoliniaNPC npc = getNPC();
			if (npc == null)
				return;
			StateManager.getInstance().getEntityManager().setNPCMana(this.getBukkitLivingEntity(), npc, amount);
		} catch (CoreStateInitException e) {
			return;
		}
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
		SpellEffectType seekSpellEffectType = Utils.getSpellEffectTypeFromResistType(type);

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
			return 25 + getResistsFromActiveEffects(type);
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

	@Override
	public int getProcChancePct() {
		int dexterity = 75;
		int procchanceextra = 0;
		
		if (this.getBukkitLivingEntity() instanceof Player) {
			try {
				ISoliniaPlayer player = SoliniaPlayerAdapter.Adapt((Player) this.getBukkitLivingEntity());
				if (player != null)
					dexterity = getDexterity();

				ISoliniaAAAbility procchanceaa = null;
				try {
					if (player.hasAaRanks()) {
						for (ISoliniaAAAbility ability : StateManager.getInstance().getConfigurationManager()
								.getAAbilitiesBySysname("WEAPONAFFINITY")) {
							if (!player.hasAAAbility(ability.getId()))
								continue;

							procchanceaa = ability;
						}
					}
				} catch (CoreStateInitException e) {

				}

				if (procchanceaa != null) {
					int procchancerank = Utils.getRankPositionOfAAAbility(this.getBukkitLivingEntity(), procchanceaa);
					procchanceextra += (procchancerank * 10);
				}

			} catch (CoreStateInitException e) {

			}
		}

		if (this.getNpcid() > 0)
			dexterity = getDexterity();
		
		// settings
		int averageProcsPerMinute = 2;
		float procsPerMinDexContrib = 0.07f;
		int weaponSpeed = 35;
		float procChance = (weaponSpeed * averageProcsPerMinute) / 6;
		float procBonus = dexterity * procsPerMinDexContrib;
		int procChanceFinal = (int)Math.floor(procChance + procBonus);

		int lowestProcChanceSpellBuff = 0;
		int highestProcChanceSpellBuff = 100;

		for (ActiveSpellEffect effect : Utils.getActiveSpellEffects(getBukkitLivingEntity(),
				SpellEffectType.ProcChance)) {
			if (effect.getRemainingValue() > 100) {
				if (effect.getRemainingValue() > highestProcChanceSpellBuff)
					highestProcChanceSpellBuff = effect.getRemainingValue();
			} else {
				if (effect.getRemainingValue() < lowestProcChanceSpellBuff)
					lowestProcChanceSpellBuff = effect.getRemainingValue();
			}
		}

		if (lowestProcChanceSpellBuff < 100)
			procchanceextra += lowestProcChanceSpellBuff;
		else
			procchanceextra += highestProcChanceSpellBuff;

		int finalprocchanceextra = (int)Math.floor(procchanceextra / 10);
		
		return (int)Math.floor(procChanceFinal + finalprocchanceextra);
	}

	@Override
	public int getMaxDamage() {
		return Utils.getMaxDamage(getLevel(), getStrength());
	}

	@Override
	public double getMaxHP() {

		if (getNpcid() < 1 && !isPlayer())
			return 1;

		double statHp = Utils.getStatMaxHP(getClassObj(), getLevel(), getStamina());
		double itemHp = getItemHp();
		double totalHp = statHp + itemHp;
		Utils.DebugLog("SoliniaLivingEntity", "getMaxHp", this.getBukkitLivingEntity().getName(), "getMaxHP called with statHp: " + statHp + " itemHp " + itemHp + " totalHp:" + totalHp);
		try {
			if (getNpcid() > 0) 
			{
				ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(getNpcid());
				if (npc == null)
					return totalHp;

				if (npc.getForcedMaxHp() > 0) {
					Utils.DebugLog("SoliniaLivingEntity", "getMaxHp", this.getBukkitLivingEntity().getName(), "Forced getMaxHp to " + npc.getForcedMaxHp());
					return npc.getForcedMaxHp();
				}
				
				totalHp += Utils.getTotalEffectTotalHP(this.getBukkitLivingEntity());

				if (npc.isBoss()) {
					totalHp += (Utils.getBossHPMultiplier(npc.isHeroic()) * npc.getLevel());
					Utils.DebugLog("SoliniaLivingEntity", "getMaxHp", this.getBukkitLivingEntity().getName(), "Boss hp is now: " + totalHp);
				}

				if (npc.isHeroic()) {
					totalHp += (Utils.getHeroicHPMultiplier() * npc.getLevel());
					Utils.DebugLog("SoliniaLivingEntity", "getMaxHp", this.getBukkitLivingEntity().getName(), "Heroic hp is now: " + totalHp);
				}

				if (npc.isRaidboss()) {
					Utils.DebugLog("SoliniaLivingEntity", "getMaxHp", this.getBukkitLivingEntity().getName(), "Raidboss hp is now: " + totalHp);
					totalHp += (Utils.getRaidBossHPMultiplier() * npc.getLevel());
				}

				if (npc.isRaidheroic()) {
					Utils.DebugLog("SoliniaLivingEntity", "getMaxHp", this.getBukkitLivingEntity().getName(), "Raid heroic hp is now: " + totalHp);
					totalHp += (Utils.getRaidHeroicHPMultiplier() * npc.getLevel());
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
							int effectIdLookup = Utils.getEffectIdFromEffectType(SpellEffectType.PetMaxHP);
							if (effectIdLookup > 0)
							for (SoliniaAARankEffect effect : solPlayer.getRanksEffectsOfEffectType(effectIdLookup)) 
							{
								percentHP += effect.getBase1();
							}
						}
					}
				}

				Utils.DebugLog("SoliniaLivingEntity", "getMaxHp", this.getBukkitLivingEntity().getName(), "Final Hp is now: " + (totalHp/100) * percentHP);
				return (totalHp/100) * percentHP;
			}

			if (isPlayer()) {
				ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player) getBukkitLivingEntity());
				if (solplayer == null)
					return totalHp;

				totalHp += Utils.getTotalEffectTotalHP(this.getBukkitLivingEntity());
				// get AA hp bonus
				totalHp += Utils.getTotalAAEffectMaxHp(this.getBukkitLivingEntity());

				return totalHp;
			}
		} catch (CoreStateInitException e) {
			return totalHp;
		}

		return totalHp;
	}

	@Override
	public boolean getDualWieldCheck() {
		if (getNpcid() < 1 && !isPlayer())
			return false;
		
		if (holdingTwoHander())
			return false;

		// If dual wield less than 3 second ago return false
		// Ugly hack to work around looping dual wields (cant get source of offhand on
		// damage event)
		Timestamp expiretimestamp = getLastDualWield();
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

				boolean result = npc.getDualWieldCheck(this);

				return result;
			}

			if (isPlayer()) {
				ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player) getBukkitLivingEntity());
				if (solplayer == null)
					return false;

				boolean result = solplayer.getDualWieldCheck(this);

				return result;
			}
		} catch (CoreStateInitException e) {
			return false;
		}

		return false;
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
	public Timestamp getLastDualWield() {
		try {
			return StateManager.getInstance().getEntityManager().getLastDualWield()
					.get(this.getBukkitLivingEntity().getUniqueId());
		} catch (CoreStateInitException e) {
		}
		return null;
	}

	@Override
	public void setLastDualWield() {
		try {
			LocalDateTime datetime = LocalDateTime.now();
			Timestamp nowtimestamp = Timestamp.valueOf(datetime);
			StateManager.getInstance().getEntityManager().setLastDualWield(this.getBukkitLivingEntity().getUniqueId(),
					nowtimestamp);
		} catch (CoreStateInitException e) {

		}
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
				stat += getTotalItemStat("STRENGTH");
				stat += Utils.getTotalEffectStat(this.getBukkitLivingEntity(), "STRENGTH");

				if (stat > getMaxStat("STRENGTH"))
					stat = getMaxStat("STRENGTH");
				return stat;
			}

			if (isPlayer()) {
				ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player) getBukkitLivingEntity());
				if (solplayer == null)
					return 1;

				int stat = 1;

				if (solplayer.getRace() != null)
					stat += solplayer.getRace().getStrength();

				stat += getTotalItemStat("STRENGTH");
				stat += Utils.getTotalEffectStat(this.getBukkitLivingEntity(), "STRENGTH");
				stat += Utils.getTotalAAEffectStat(this.getBukkitLivingEntity(), "STRENGTH");

				if (stat > getMaxStat("STRENGTH"))
					stat = getMaxStat("STRENGTH");

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
		return getEquippedSoliniaItems(false);
	}

	@Override
	public List<ISoliniaItem> getEquippedSoliniaItems(boolean ignoreMainhand) {
		if (isPlayer()) {
			try {
				Utils.DebugLog("SoliniaLivingEntity", "getEquippedSoliniaItems", getBukkitLivingEntity().getName(), "Found Player, seeking equipped soliniaitems");
				ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player) this.getBukkitLivingEntity());
				return solplayer.getEquippedSoliniaItems(ignoreMainhand);
			} catch (CoreStateInitException e) {

			}
		}

		if (isNPC()) {
			Utils.DebugLog("SoliniaLivingEntity", "getEquippedSoliniaItems", getBukkitLivingEntity().getName(), "Found NPC, seeking equipped soliniaitems");
			ISoliniaNPC npc = getNPC();
			return npc.getEquippedSoliniaItems(getBukkitLivingEntity(), ignoreMainhand);
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

				int stat = npc.getLevel() * 5;
				Utils.DebugLog("SoliniaLivingEntity", "getStamina", this.getBukkitLivingEntity().getName(), "base: " + stat);

				int totalItemStat = getTotalItemStat("STAMINA");
				Utils.DebugLog("SoliniaLivingEntity", "getStamina", this.getBukkitLivingEntity().getName(), "totalItemStat: " + totalItemStat);
				int totalEffectStat = Utils.getTotalEffectStat(this.getBukkitLivingEntity(), "STAMINA");
				Utils.DebugLog("SoliniaLivingEntity", "getStamina", this.getBukkitLivingEntity().getName(), "totalEffectStat: " + totalEffectStat);
				int maxStat = getMaxStat("STAMINA");
				Utils.DebugLog("SoliniaLivingEntity", "getStamina", this.getBukkitLivingEntity().getName(), "maxStamina allowed: " + maxStat);
				stat += totalItemStat;
				stat += totalEffectStat;

				if (stat > maxStat)
					stat = maxStat;
				Utils.DebugLog("SoliniaLivingEntity", "getStamina", this.getBukkitLivingEntity().getName(), "final stat: " + stat);

				return stat;
			}

			if (isPlayer()) {
				ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player) getBukkitLivingEntity());
				if (solplayer == null)
					return 1;

				int stat = 1;

				if (solplayer.getRace() != null)
					stat += solplayer.getRace().getStamina();


				Utils.DebugLog("SoliniaLivingEntity", "getStamina", this.getBukkitLivingEntity().getName(), "base: " + stat);

				int totalItemStat = getTotalItemStat("STAMINA");
				Utils.DebugLog("SoliniaLivingEntity", "getStamina", this.getBukkitLivingEntity().getName(), "totalItemStat: " + totalItemStat);
				stat += totalItemStat;

				int totalEffectStat = Utils.getTotalEffectStat(this.getBukkitLivingEntity(), "STAMINA");
				Utils.DebugLog("SoliniaLivingEntity", "getStamina", this.getBukkitLivingEntity().getName(), "totalEffectStat: " + totalEffectStat);
				stat += totalEffectStat;
				
				int totalAAEffectStat = Utils.getTotalAAEffectStat(this.getBukkitLivingEntity(), "STAMINA");
				Utils.DebugLog("SoliniaLivingEntity", "getStamina", this.getBukkitLivingEntity().getName(), "totalAAEffectStat: " + totalAAEffectStat);
				stat += totalAAEffectStat;
				
				int maxStat = getMaxStat("STAMINA");
				Utils.DebugLog("SoliniaLivingEntity", "getStamina", this.getBukkitLivingEntity().getName(), "maxStamina allowed: " + maxStat);

				if (stat > maxStat)
					stat = maxStat;
				Utils.DebugLog("SoliniaLivingEntity", "getStamina", this.getBukkitLivingEntity().getName(), "final stat: " + stat);
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
				stat += getTotalItemStat("AGILITY");
				stat += Utils.getTotalEffectStat(this.getBukkitLivingEntity(), "AGILITY");

				if (stat > getMaxStat("AGILITY"))
					stat = getMaxStat("AGILITY");

				return stat;
			}

			if (isPlayer()) {
				ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player) getBukkitLivingEntity());
				if (solplayer == null)
					return 1;

				int stat = 1;

				if (solplayer.getRace() != null)
					stat += solplayer.getRace().getAgility();

				stat += getTotalItemStat("AGILITY");
				stat += Utils.getTotalEffectStat(this.getBukkitLivingEntity(), "AGILITY");
				stat += Utils.getTotalAAEffectStat(this.getBukkitLivingEntity(), "AGILITY");

				if (stat > getMaxStat("AGILITY"))
					stat = getMaxStat("AGILITY");

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
				stat += getTotalItemStat("DEXTERITY");
				stat += Utils.getTotalEffectStat(this.getBukkitLivingEntity(), "DEXTERITY");

				if (stat > getMaxStat("DEXTERITY"))
					stat = getMaxStat("DEXTERITY");

				return stat;
			}

			if (isPlayer()) {
				ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player) getBukkitLivingEntity());
				if (solplayer == null)
					return 1;

				int stat = 1;

				if (solplayer.getRace() != null)
					stat += solplayer.getRace().getDexterity();

				stat += getTotalItemStat("DEXTERITY");
				stat += Utils.getTotalEffectStat(this.getBukkitLivingEntity(), "DEXTERITY");
				stat += Utils.getTotalAAEffectStat(this.getBukkitLivingEntity(), "DEXTERITY");

				if (stat > getMaxStat("DEXTERITY"))
					stat = getMaxStat("DEXTERITY");

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
				stat += getTotalItemStat("INTELLIGENCE");
				stat += Utils.getTotalEffectStat(this.getBukkitLivingEntity(), "INTELLIGENCE");

				if (stat > getMaxStat("INTELLIGENCE"))
					stat = getMaxStat("INTELLIGENCE");

				return stat;
			}

			if (isPlayer()) {
				ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player) getBukkitLivingEntity());
				if (solplayer == null)
					return 1;
				int stat = 1;

				if (solplayer.getRace() != null)
					stat += solplayer.getRace().getIntelligence();

				
				stat += getTotalItemStat("INTELLIGENCE");

				stat += Utils.getTotalEffectStat(this.getBukkitLivingEntity(), "INTELLIGENCE");
				stat += Utils.getTotalAAEffectStat(this.getBukkitLivingEntity(), "INTELLIGENCE");

				if (stat > getMaxStat("INTELLIGENCE"))
					stat = getMaxStat("INTELLIGENCE");

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
				stat += getTotalItemStat("WISDOM");
				stat += Utils.getTotalEffectStat(this.getBukkitLivingEntity(), "WISDOM");

				if (stat > getMaxStat("WISDOM"))
					stat = getMaxStat("WISDOM");

				return stat;
			}

			if (isPlayer()) {

				ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player) getBukkitLivingEntity());
				if (solplayer == null)
					return 1;

				int stat = 1;

				if (solplayer.getRace() != null)
					stat += solplayer.getRace().getWisdom();

				stat += getTotalItemStat("WISDOM");
				stat += Utils.getTotalEffectStat(this.getBukkitLivingEntity(), "WISDOM");
				stat += Utils.getTotalAAEffectStat(this.getBukkitLivingEntity(), "WISDOM");

				if (stat > getMaxStat("WISDOM"))
					stat = getMaxStat("WISDOM");
		
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
				stat += getTotalItemStat("CHARISMA");
				stat += Utils.getTotalEffectStat(this.getBukkitLivingEntity(), "CHARISMA");

				if (stat > getMaxStat("CHARISMA"))
					stat = getMaxStat("CHARISMA");

				return stat;
			}

			if (isPlayer()) {
				ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player) getBukkitLivingEntity());
				if (solplayer == null)
					return 1;

				int stat = 1;

				if (solplayer.getRace() != null)
					stat += solplayer.getRace().getCharisma();

				stat += getTotalItemStat("CHARISMA");
				stat += Utils.getTotalEffectStat(this.getBukkitLivingEntity(), "CHARISMA");
				stat += Utils.getTotalAAEffectStat(this.getBukkitLivingEntity(), "CHARISMA");

				if (stat > getMaxStat("CHARISMA"))
					stat = getMaxStat("CHARISMA");

				return stat;
			}
		} catch (CoreStateInitException e) {
			return 1;
		}

		return 1;
	}

	@Override
	public int getMaxStat(String skillname) {
		int baseMaxStat = 255;
		ISoliniaAAAbility aa = null;
		try {
			if (getBukkitLivingEntity() instanceof Player) {
				ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player) getBukkitLivingEntity());
				if (solplayer != null && solplayer.hasAaRanks()) {
					for (ISoliniaAAAbility ability : StateManager.getInstance().getConfigurationManager()
							.getAAbilitiesBySysname("PLANARPOWER")) {
						if (!solplayer.hasAAAbility(ability.getId()))
							continue;

						aa = ability;
					}
				}
			}
		} catch (CoreStateInitException e) {

		}

		int rank = 0;

		if (aa != null) {
			rank = Utils.getRankPositionOfAAAbility(getBukkitLivingEntity(), aa);
		}

		if (rank == 0) {
			return baseMaxStat;
		} else {
			return baseMaxStat + (rank * 5);
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
	public boolean getDoubleAttackCheck() {
		if (getNpcid() < 1 && !isPlayer())
			return false;

		// If dual wield less than 3 second ago return false
		// Ugly hack to work around looping dual wields (cant get source of offhand on
		// damage event)
		Timestamp expiretimestamp = getLastDoubleAttack();
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

				return npc.getDoubleAttackCheck();
			}

			if (isPlayer()) {
				ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player) getBukkitLivingEntity());
				if (solplayer == null)
					return false;
				return solplayer.getDoubleAttackCheck();
			}
		} catch (CoreStateInitException e) {
			return false;
		}

		return false;
	}

	@Override
	public int getMaxMP() {
		if (getClassObj() == null)
			return 1;

		String profession = getClassObj().getName().toUpperCase();

		int wisintagi = 0;
		if (Utils.getCasterClass(profession).equals("W"))
			wisintagi = getWisdom();
		if (Utils.getCasterClass(profession).equals("I"))
			wisintagi = getIntelligence();
		if (Utils.getCasterClass(profession).equals("N"))
			wisintagi = getAgility();

		double maxmana = ((850 * getLevel()) + (85 * wisintagi * getLevel())) / 425;
		double itemMana = getItemMana();
		maxmana += itemMana;
		if (this.getNpcid() > 0) {
			maxmana = maxmana + (50 * getLevel());

			ISoliniaNPC npc = getNPC();
			if (npc != null) {
				if (npc.isBoss()) {
					maxmana += (Utils.getBossMPRegenMultipler(npc.isHeroic()) * npc.getLevel());
				}
				if (npc.isHeroic()) {
					maxmana += (Utils.getHeroicMPRegenMultipler() * npc.getLevel());
				}

				if (npc.isRaidboss()) {
					maxmana += (Utils.getRaidBossMPRegenMultipler() * npc.getLevel());
				}
				if (npc.isRaidheroic()) {
					maxmana += (Utils.getRaidHeroicMPRegenMultipler() * npc.getLevel());
				}
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

			int chanceToSummon = Utils.RandomBetween(1, 10);

			if (chanceToSummon > 8) {
				if (summoningEntity instanceof Player) {
					this.say("You will not evade me " + ((Player) summoningEntity).getDisplayName() + "!");
				} else {
					this.say("You will not evade me " + summoningEntity.getCustomName() + "!");

				}
				summoningEntity.teleport(getBukkitLivingEntity().getLocation());
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

			int chanceToSummon = Utils.RandomBetween(1, 10);

			if (chanceToSummon > 8) {
				teleportedEntity.teleport(getBukkitLivingEntity().getLocation());
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
				if (group.getMembers().contains(uniqueId))
					return;
			}
		}

		Entity entity = Bukkit.getEntity(uniqueId);
		if (entity.isInvulnerable() || entity.isDead())
			return;
		
		if (entity instanceof Player)
			if (((Player)entity).getGameMode() != GameMode.SURVIVAL)
				return;

		try {
			StateManager.getInstance().getEntityManager().addToHateList(this.getBukkitLivingEntity().getUniqueId(),
					uniqueId, hate, true);
			checkHateTargets();
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
				resetPosition(true);
				return false;
			}
		}
		
		if (this.isMezzed() || this.isStunned())
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
	public DamageHitInfo avoidDamage(SoliniaLivingEntity attacker, DamageHitInfo hit) {

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
	public boolean checkHitChance(SoliniaLivingEntity attacker, DamageHitInfo hit) {
		ISoliniaLivingEntity defender = this;

		int avoidance = defender.getTotalDefense();

		int accuracy = hit.tohit;
		// if (accuracy == -1)
		// return true;

		double hitRoll = Utils.RandomBetween(0, (int) Math.floor(accuracy));
		double avoidRoll = Utils.RandomBetween(0, (int) Math.floor(avoidance));

		// tie breaker? Don't want to be biased any one way
		return hitRoll > avoidRoll;
	}

	@Override
	public DamageHitInfo meleeMitigation(SoliniaLivingEntity attacker, DamageHitInfo hit) {
		Utils.DebugLog("SoliniaLivingEntity", "meleeMitigation", this.getBukkitLivingEntity().getName(), "Melee Mitigation starts with hit: offense " + hit.offense + " damagedone " + hit.damage_done + " base_damage " + hit.base_damage);

		if (hit.damage_done < 0 || hit.base_damage == 0)
			return hit;

		ISoliniaLivingEntity defender = this;
		int mitigation = defender.getMitigationAC();

		Utils.DebugLog("SoliniaLivingEntity", "meleeMitigation", this.getBukkitLivingEntity().getName(), "mitigationAC : " + mitigation);

		if (isPlayer() && attacker.isPlayer())
		{
			mitigation = mitigation * 80 / 100; // PvP
			Utils.DebugLog("SoliniaLivingEntity", "meleeMitigation", this.getBukkitLivingEntity().getName(), "PVP mitigationAC capped over to: " + mitigation);
		}

		int roll = (int) rollD20(hit.offense, mitigation);
		Utils.DebugLog("SoliniaLivingEntity", "meleeMitigation", this.getBukkitLivingEntity().getName(), "mitigation dice d20 roll was: " + roll);

		// +0.5 for rounding, min to 1 dmg
		hit.damage_done = Math.max((int) (roll * (double) (hit.base_damage) + 0.5), 1);
		Utils.DebugLog("SoliniaLivingEntity", "meleeMitigation", this.getBukkitLivingEntity().getName(), "new damage done is: " + hit.damage_done);

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

		int atk_roll = Utils.RandomBetween(0, offense + 5);
		int def_roll = Utils.RandomBetween(0, mitigation + 5);

		int avg = (offense + mitigation + 10) / 2;
		int index = Math.max(0, (atk_roll - def_roll) + (avg / 2));

		index = (int) Utils.clamp((index * 20) / avg, 0, 19);

		return mods[index];
	}

	private boolean isMeditating() {
		if (isPlayer()) {
			ISoliniaPlayer player;
			try {
				player = SoliniaPlayerAdapter.Adapt((Player) getBukkitLivingEntity());
				return player.isMeditating();
			} catch (CoreStateInitException e) {
				return false;
			}
		}
		return false;
	}

	@Override
	public int getMitigationAC() {
		return ACSum();
	}

	@Override
	public int ACSum() {
		double ac = 0;
		ac += getTotalItemAC();
		double shield_ac = 0;

		// EQ math
		ac = (ac * 4) / 3;
		// anti-twink
		if (isPlayer() && getLevel() < 50)
			ac = Math.min(ac, 25 + 6 * getLevel());
		ac = Math.max(0, ac + getClassRaceACBonus());

		if (isNPC()) {
			try {
				ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(getNpcid());
				ac += npc.getAC();
			} catch (CoreStateInitException e) {
				// dont get ac from npc type
			}

			// TODO Pet avoidance
			ac += getSkill("DEFENSE") / 5;

			double spell_aa_ac = 0;
			// TODO AC AA and Spell bonuses

			spell_aa_ac += getSpellBonuses(SpellEffectType.ArmorClass);

			spell_aa_ac += Utils.getHighestAAEffectEffectType(getBukkitLivingEntity(), SpellEffectType.ArmorClass);

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

			spell_aa_ac += Utils.getHighestAAEffectEffectType(getBukkitLivingEntity(), SpellEffectType.ArmorClass);

			if (getClassObj() != null) {
				if (getClassObj().getName().equals("ENCHANTER") || getClassObj().getName().equals("ENCHANTER")) {
					ac += getSkill("DEFENSE") / 2 + spell_aa_ac / 3;
				} else {
					ac += getSkill("DEFENSE") / 3 + spell_aa_ac / 4;
				}
			} else {
				ac += getSkill("DEFENSE") / 3 + spell_aa_ac / 4;
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

			total_aclimitmod += Utils.getHighestAAEffectEffectType(getBukkitLivingEntity(),
					SpellEffectType.CombatStability);

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

		int level = Math.min(105, getLevel()) - 1;

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
		// TODO Class Race Bonus
		return 1;
	}

	@Override
	public int getSkillDmgTaken(String skill) {
		int skilldmg_mod = 0;

		if (skilldmg_mod < -100)
			skilldmg_mod = -100;

		return skilldmg_mod;
	}

	@Override
	public int getFcDamageAmtIncoming(SoliniaLivingEntity caster, int spell_id, boolean use_skill, String skill) {
		// Used to check focus derived from SE_FcDamageAmtIncoming which adds direct
		// damage to Spells or Skill based attacks.
		int dmg = 0;
		return dmg;
	}

	@Override
	public int getActSpellDamage(ISoliniaSpell soliniaSpell, int value, SpellEffect spellEffect,
			ISoliniaLivingEntity target) {
		if (Utils.getSpellTargetType(soliniaSpell.getTargettype()).equals(SpellTargetType.Self))
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
		if ((soliniaSpell.getName().startsWith("Harm Touch")) && getLevel() > 40) {
			value_BaseEffect += (getLevel() - 40) * 20;
		}

		chance = 0;

		// TODO take into account item,spell,aa bonuses
		if (isPlayer()) {
			chance += Utils.getHighestAAEffectEffectType(getBukkitLivingEntity(), SpellEffectType.CriticalSpellChance);
		}

		// TODO Items/aabonuses
		if (chance > 0 || (getClassObj().getName().equals("WIZARD") && getLevel() >= 12)) {

			int ratio = 100;

			// TODO Harm Touch

			// TODO Crit Chance Overrides

			if (Utils.RandomBetween(0, 100) < ratio) {
				critical = true;
				if (isPlayer()) {
					ratio += Utils.getHighestAAEffectEffectType(getBukkitLivingEntity(),
							SpellEffectType.SpellCritDmgIncrease);
				}
				// TODO add ratio bonuses from spells, aas
			} else if (getClassObj().getName().equals("WIZARD")) {
				if ((getLevel() >= 12) && Utils.RandomBetween(0, 100) < 7) {
					ratio += Utils.RandomBetween(20, 70);
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
			if (getLevel() >= 51 && casttime >= 3000 && !spell.isBeneficial()
					&& (getClassObj().getName().equals("SHADOWKNIGHT") || getClassObj().getName().equals("RANGER")
							|| getClassObj().getName().equals("PALADIN")
							|| getClassObj().getName().equals("BEASTLORD")))
				cast_reducer += (getLevel() - 50) * 3;

		// LIVE AA SpellCastingDeftness, QuickBuff, QuickSummoning, QuickEvacuation,
		// QuickDamage

		// TODO MAX Reducer for cast time

		casttime = (casttime * (100 - cast_reducer) / 100);

		if (casttime < 1)
			return 1;

		return casttime;
	}

	@Override
	public int getActSpellHealing(ISoliniaSpell soliniaSpell, int value, SpellEffect spellEffect,
			ISoliniaLivingEntity target) {
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
				chance += Utils.getHighestAAEffectEffectType(getBukkitLivingEntity(),
						SpellEffectType.CriticalHealChance);
			}

			// TODO FOcuses

			if (chance > -1 && (Utils.RandomBetween(0, 100) < chance)) {
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

			if (chance > -1 && Utils.RandomBetween(0, 100) < chance)
				value *= 2;
		}

		// TODO Npc Heal Scale

		return value;
	}

	@Override
	public int getRune() {
		return Utils.getActiveSpellEffectsRemainingValue(this.getBukkitLivingEntity(), SpellEffectType.Rune);
	}

	@Override
	public void removeActiveSpellsWithEffectType(SpellEffectType spellEffectType) {
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

				/*
				 * if (skillinuse == SkillType.Bash) { if (isPlayer()) { ItemStack item =
				 * this.getBukkitLivingEntity().getEquipment().getItemInOffHand(); if (item !=
				 * null) { try { ISoliniaItem solItem = SoliniaItemAdapter.Adapt(item); if
				 * (solItem != null) { if (item.getType().equals(Material.SHIELD)) { hate +=
				 * solItem.getAC(); } hate = hate * (100 +
				 * getFuriousBash(solItem.getFocusEffectId())) / 100; } } catch
				 * (SoliniaItemException si) {
				 * 
				 * } } } }
				 */

				DamageHitInfo my_hit = new DamageHitInfo();
				my_hit.base_damage = weapon_damage;
				my_hit.min_damage = 0;
				my_hit.damage_done = 1;

				my_hit.skill = skillinuse.name().toUpperCase();
				my_hit.offense = getOffense(my_hit.skill);
				my_hit.tohit = getTotalToHit(my_hit.skill, chance_mod);
				// slot range exclude ripe etc ...

				if (this.getClassObj() != null && this.getClassObj().canRiposte())
					my_hit.hand = 1;
				else
					my_hit.hand = 0;

				if (isNPC())
					my_hit.min_damage = 1;

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

			other.damage(damage, (Entity) this.getBukkitLivingEntity());

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
	public boolean isInvulnerable() {
		for (ActiveSpellEffect effect : Utils.getActiveSpellEffects(getBukkitLivingEntity(),
				SpellEffectType.DivineAura)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isRooted() {
		for (ActiveSpellEffect effect : Utils.getActiveSpellEffects(getBukkitLivingEntity(), SpellEffectType.Root)) {
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
		effectmodcap = Utils.getBaseInstrumentSoftCap();

		// TODO Spell and AA bonuses
		switch (Utils.getSkillType(spell.getSkill())) {
		case PercussionInstruments:
			if (getTotalItemSkillMod(SkillType.PercussionInstruments) == 0)
				effectmod = 10;
			else if (getSkill("PERCUSSIONINSTRUMENTS") == 0)
				effectmod = 10;
			else
				effectmod = getTotalItemSkillMod(SkillType.PercussionInstruments);
			break;
		case StringedInstruments:
			if (getTotalItemSkillMod(SkillType.StringedInstruments) == 0)
				effectmod = 10;
			else if (getSkill("STRINGEDINSTRUMENTS") == 0)
				effectmod = 10;
			else
				effectmod = getTotalItemSkillMod(SkillType.StringedInstruments);
			break;
		case WindInstruments:
			if (getTotalItemSkillMod(SkillType.WindInstruments) == 0)
				effectmod = 10;
			else if (getSkill("WINDINSTRUMENTS") == 0)
				effectmod = 10;
			else
				effectmod = getTotalItemSkillMod(SkillType.WindInstruments);
			break;
		case BrassInstruments:
			if (getTotalItemSkillMod(SkillType.BrassInstruments) == 0)
				effectmod = 10;
			else if (getSkill("BRASSINSTRUMENTS") == 0)
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
				
				Utils.DebugLog("SoliniaLivingEntity", "setAttackTarget", this.getBukkitLivingEntity().getName(), "i am being told to set my target to " + null);
				((Creature) this.getBukkitLivingEntity()).setTarget(null);

				Utils.DebugLog("SoliniaLivingEntity", "setAttackTarget", this.getBukkitLivingEntity().getName(), "my new target is " + ((Creature) this.getBukkitLivingEntity()).getTarget());
			}
			return;
		}

		if (this.getBukkitLivingEntity() instanceof Creature) {
			if (entity != null && (!this.hasHate() || !this.isInHateList(entity.getUniqueId()))) {
				this.addToHateList(entity.getUniqueId(), 1, true);
			}
			
			Utils.DebugLog("SoliniaLivingEntity", "setAttackTarget", this.getBukkitLivingEntity().getName(), "i am being told to set my target to " + entity);
			if (entity == null)
				this.getBukkitLivingEntity().setLastDamageCause(null);
			((Creature) this.getBukkitLivingEntity()).setTarget(entity);
			Utils.DebugLog("SoliniaLivingEntity", "setAttackTarget", this.getBukkitLivingEntity().getName(), "my new target is " + ((Creature) this.getBukkitLivingEntity()).getTarget());
		}
	}

	@Override
	public void doCheckForEnemies() {
		if (isPlayer())
			return;

		if (this.getNpcid() < 1)
			return;
		
		if (this.isMezzed() || this.isStunned())
			return;

		if (getBukkitLivingEntity().isDead())
			return;

		if (!(getBukkitLivingEntity() instanceof Creature))
			return;

		if (doCheckForDespawn() == true)
			return;

		if (((Creature) getBukkitLivingEntity()).getTarget() != null && ((Creature) getBukkitLivingEntity()).getTarget()
				.getLocation().distance(this.getBukkitLivingEntity().getLocation()) < 150)
			return;

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
							if (Utils.isEntityInLineOfSight(getBukkitLivingEntity(),le)) {
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
					if (faction.getName().equals("KOS")) {
						if (Utils.isEntityInLineOfSight(getBukkitLivingEntity(),player)) {
							addToHateList(player.getUniqueId(), 1, true);
							return;
						}
					}
					
					Utils.DebugLog("SoliniaLivingEntity","doCheckForEnemies",Integer.toString(this.getNpcid()),"Checking for hate against player: " + player.getName() + ":" + player.getUniqueId());
					ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
					PlayerFactionEntry factionEntry = solPlayer.getFactionEntry(npc.getFactionid());
					if (factionEntry != null) {
						Utils.DebugLog("SoliniaLivingEntity","doCheckForEnemies",Integer.toString(this.getNpcid()),"Found faction entry for SoliniaPlayer: " + player.getName() + ":" + player.getUniqueId() + " with standing: " + Utils.getFactionStandingType(factionEntry.getFactionId(),factionEntry.getValueWithEffectsOnEntity(this.getBukkitLivingEntity(), player)).name());
						
						switch (Utils.getFactionStandingType(factionEntry.getFactionId(),
								factionEntry.getValueWithEffectsOnEntity(this.getBukkitLivingEntity(), player))) {
						case FACTION_THREATENLY:
						case FACTION_SCOWLS:
							if (Utils.isEntityInLineOfSight(getBukkitLivingEntity(),player)) {
								addToHateList(player.getUniqueId(), 1, true);
								return;
							} else {
								continue;
							}
						default:
							continue;
						}
					} else {
						Utils.DebugLog("SoliniaLivingEntity","doCheckForEnemies",Integer.toString(this.getNpcid()),"Failed to find faction entry for SoliniaPlayer: " + player.getName() + ":" + player.getUniqueId());
						
					}
				}
			}

		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.resetPosition(true);
	}

	@Override
	public boolean doCheckForDespawn() {
		if (despawnIfWrongTime())
			return true;

		return false;
	}
	
	private boolean isRoamer() {
		if (this.isCurrentlyNPCPet())
			return false;
		
		if (Utils.isLivingEntityNPC(this.getBukkitLivingEntity())) {
			
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

		if (Utils.isLivingEntityNPC(this.getBukkitLivingEntity())) {
			
			try {
				ISoliniaLivingEntity solEntity = SoliniaLivingEntityAdapter.Adapt(this.getBukkitLivingEntity());
				ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(solEntity.getNpcid());
				if (!Utils.IsTimeRangeActive(this.getBukkitLivingEntity().getWorld(), npc.getTimefrom(), npc.getTimeto())) {
					Utils.RemoveEntity(this.getBukkitLivingEntity(), "DESPAWNIFDAY");
					return true;
				}
			} catch (CoreStateInitException e) {

			}
		}

		return false;
	}

	@Override
	public String getLanguage() {
		if (isNPC()) {
			try {
				ISoliniaNPC npc = getNPC();
				if (npc.getRaceid() > 0) {
					ISoliniaRace race = StateManager.getInstance().getConfigurationManager().getRace(npc.getRaceid());
					return race.getName().toUpperCase();
				}
			} catch (CoreStateInitException e) {
				//
			}
		}

		return null;
	}

	@Override
	public String getName() {
		if (getBukkitLivingEntity().getCustomName() != null)
		return getBukkitLivingEntity().getCustomName();
		
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
		List<ActiveSpellEffect> spellEffects = Utils.getActiveSpellEffects(getBukkitLivingEntity(),
				SpellEffectType.DivineSave);
		List<ActiveSpellEffect> spellEffects2 = Utils.getActiveSpellEffects(getBukkitLivingEntity(),
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
		int iOtherLevel = getLevel();
		return Utils.getLevelCon(mylevel,iOtherLevel);
	}

	@Override
	public void PetThink(Player playerOwner) {
		if (this.getBukkitLivingEntity() instanceof Sittable) {
			if (((Sittable) this.getBukkitLivingEntity()).isSitting())
				((Sittable) this.getBukkitLivingEntity()).setSitting(false);
		}

		if (this.getBukkitLivingEntity().getHealth() < this.getBukkitLivingEntity()
				.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()) {

			// Pet regen is slow
			double newHealth = this.getBukkitLivingEntity().getHealth() + 1;
			if (newHealth < this.getBukkitLivingEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()) {
				if (!this.getBukkitLivingEntity().isDead())
					setHealth(newHealth);
			} else {
				if (!this.getBukkitLivingEntity().isDead())
					setHealth(this.getBukkitLivingEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
			}
		}

		// Mp Regen
		if (this.getMana() < this.getMaxMP()) {
			this.setMana(this.getMana() + 1);
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
	public int calculateDamageFromDamageEvent(Entity originalDamager, boolean ismagic, int damage) {
		try {
			Entity attackerEntity = originalDamager;

			// Override damager from arrow
			if (originalDamager instanceof Arrow) {
				Arrow arr = (Arrow) originalDamager;
				if (arr.getShooter() instanceof LivingEntity)
					attackerEntity = (LivingEntity) arr.getShooter();
			}

			if (!(attackerEntity instanceof LivingEntity))
				return 0;

			ISoliniaLivingEntity attacker = SoliniaLivingEntityAdapter.Adapt((LivingEntity) attackerEntity);
			ISoliniaLivingEntity defender = this;

			// if this is a melee attack and the attacker is too far from the defender
			// cancel the event

			if (!ismagic && !(originalDamager instanceof Arrow) && 					
					!ItemStackUtils.isRangedWeapon(((LivingEntity)attackerEntity).getEquipment().getItemInMainHand())) 
			{
				if (attacker.getLocation().distance(defender.getLocation()) > 3) {
					attacker.sendMessage(ChatColor.GRAY + "* You are too far away to attack!");
					return 0;
				}
			}

			// Remove buffs on attacker (invis should drop)
			// and check they are not mezzed

			// MEZZED
			if (attacker.isMezzed()) {
				if (attacker.getBukkitLivingEntity() instanceof Player)
					((Player) attacker.getBukkitLivingEntity()).spigot().sendMessage(ChatMessageType.ACTION_BAR,
							new TextComponent(ChatColor.GRAY + "* You are mezzed!"));
				return 0;
			}

			// STUNNED
			if (attacker.isStunned()) {
				if (attacker.getBukkitLivingEntity() instanceof Player)
					attacker.getBukkitLivingEntity().sendMessage("* You are stunned!");
				return 0;
			}

			// CAN ATTACK / MEZ, STUN - PETS CHECKING IF CAN ATTACK ETC
			if (!attacker.canAttackTarget(defender)) {
				return 0;
			}
			
			if (!ismagic && !attacker.getLastMeleeAttackCheck())
			{
				/* Spammy
				if (attacker.getBukkitLivingEntity() instanceof Player)
					attacker.sendMessage(ChatColor.GRAY + "* This weapon is too slow to attack this frequently [" + getAutoAttackTimerFrequencySeconds() + " seconds]");
					*/
				return 0;
			}

			// MODIFY DAMAGE TO BOW DAMAGE IF ARROW
			if (originalDamager instanceof Arrow) {
				Arrow arr = (Arrow) originalDamager;
				ItemStack mainitem = attacker.getBukkitLivingEntity().getEquipment().getItemInMainHand();
				if (mainitem != null && mainitem.getType() == Material.BOW) {
					int dmgmodifier = 0;

					if (ItemStackUtils.IsSoliniaItem(mainitem)) {
						try {
							ISoliniaItem item = SoliniaItemAdapter.Adapt(mainitem);
							if (item.getDamage() > 0 && (item.getBasename().equals("BOW") || item.getBasename().equals("CROSSBOW"))) {
								dmgmodifier = item.getDamage();
							}
						} catch (SoliniaItemException e) {
							// sok just skip
						}
					}

					damage = damage + dmgmodifier;
				}
			}

			// INVULNERABILITY / DIVINE AURA
			if (defender.isInvulnerable() || defender.getBukkitLivingEntity().isInvulnerable()) {
				if (attacker.isPlayer())
					attacker.getBukkitLivingEntity()
							.sendMessage("* Your attack was prevented as the target is Invulnerable!");

				if (defender.isPlayer())
					defender.getBukkitLivingEntity()
							.sendMessage("* Your invulnerability prevented the targets attack!");

				return 0;
			}

			// RUNE
			if (defender.getRune() > 0) {
				damage = defender.reduceAndRemoveRunesAndReturnLeftover(damage);

				if (damage <= 0) {
					if (attacker.isPlayer())
						attacker.getBukkitLivingEntity().sendMessage("* Your attack was absorbed by the targets Rune");

					if (defender.isPlayer())
						defender.getBukkitLivingEntity().sendMessage("* Your rune spell absorbed the targets attack!");

					return 0;
				}
			}

			// AA MITIGATION ETC
			damage = getDamageAfterMitigation(damage);

			if (damage <= 0)
				return 0;

			// We should move all this to commonDamage
			if (!ismagic)
				checkNumHitsRemaining(NumHit.IncomingHitSuccess);

			attacker.removeNonCombatSpells();
			defender.removeNonCombatSpells();

			// DEATH SAVES
			if (damage > defender.getBukkitLivingEntity().getHealth()) {
				if (defender.hasDeathSave() > 0) {
					defender.removeDeathSaves();
					if (defender.isPlayer()) {
						defender.sendMessage("* Your death/divine save boon has saved you from death!");
					}
					return 0;
				}
			}

			checkNumHitsRemaining(NumHit.IncomingDamage);

			// MAGIC ENDS HERE
			if (ismagic) {
				if (attacker.isPlayer()) {
					DecimalFormat df = new DecimalFormat();
					df.setMaximumFractionDigits(2);
					String name = defender.getBukkitLivingEntity().getName();
					if (defender.getBukkitLivingEntity().getCustomName() != null)
						name = defender.getBukkitLivingEntity().getCustomName();

					((Player) attacker.getBukkitLivingEntity()).spigot()
							.sendMessage(ChatMessageType.ACTION_BAR,
									new TextComponent(
											"You SPELLDMG'd " + name + " for " + df.format(damage) + " ["
													+ df.format(defender.getBukkitLivingEntity().getHealth() - damage)
													+ "/"
													+ df.format(defender.getBukkitLivingEntity()
															.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue())
													+ "]"));
				}

				defender.damageAlertHook(damage, attacker.getBukkitLivingEntity());
				return damage;
			}

			// MELEE / BOW STARTS HERE
			defender.damageAlertHook(damage, attacker.getBukkitLivingEntity());
			return attacker.AttackWithMainHand(defender, (originalDamager instanceof Arrow), damage);
		} catch (CoreStateInitException e) {
			return 0;
		}
	}

	private int getDamageAfterMitigation(int damage) {
		if (damage <= 0)
			return damage;

		int spellMitigateMeleeDamage = 0;

		for (ActiveSpellEffect effect : Utils.getActiveSpellEffects(getBukkitLivingEntity(),
				SpellEffectType.MitigateMeleeDamage)) {
			spellMitigateMeleeDamage += effect.getRemainingValue();
		}

		// We should check this in advance really
		if (spellMitigateMeleeDamage < 1)
			return damage;

		double damage_to_reduce = damage * (spellMitigateMeleeDamage / 100);

		return (int) Math.floor((damage - damage_to_reduce));
	}

	@Override
	public boolean canAttackTarget(ISoliniaLivingEntity defender) {
		// IM MEZZED SO CANT DO ANYTHING
		if (isStunned())
			return false;

		// IM STUNNED SO CANT DO ANYTHING
		if (isMezzed())
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
		} catch (CoreStateInitException e) {

		}

	}

	@Override
	public void setHealth(double health) {
		if (!getBukkitLivingEntity().isDead())
			getBukkitLivingEntity().setHealth(health);

		if (isCurrentlyNPCPet() && this.getActiveMob() != null && this.getOwnerEntity() instanceof Player) {
			PartyWindowUtils.UpdateWindow(((Player)this.getOwnerEntity()),false,false);
		}

		if (getBukkitLivingEntity() instanceof Player) {
			try {
				ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player) getBukkitLivingEntity());
				PartyWindowUtils.UpdateGroupWindowForEveryone(getBukkitLivingEntity().getUniqueId(),
						solPlayer.getGroup(),false);
			} catch (CoreStateInitException e) {

			}
		}
	}

	@Override
	public void StopSinging() {
		try {
			Integer singingId = StateManager.getInstance().getEntityManager()
					.getEntitySinging(getBukkitLivingEntity().getUniqueId());
			if (singingId != null) {
				ISoliniaSpell spell = StateManager.getInstance().getConfigurationManager().getSpell(singingId);
				StateManager.getInstance().getEntityManager()
						.removeSpellEffectsOfSpellId(getBukkitLivingEntity().getUniqueId(), singingId, true, true);
				emote(getBukkitLivingEntity().getCustomName() + "'s song comes to a close [" + spell.getName() + "]",
						true);
				StateManager.getInstance().getEntityManager().setEntitySinging(getBukkitLivingEntity().getUniqueId(),
						null);
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

		ActiveMob activeMob = MythicMobs.inst().getAPIHelper().getMythicMobInstance(this.getBukkitLivingEntity());
		if (activeMob == null) {
			// Why does this npc exist without a MM entity?
			Utils.RemoveEntity(this.getBukkitLivingEntity(), "RESETPOSITION");
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
				this.getBukkitLivingEntity()
						.setHealth(this.getBukkitLivingEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
		}

		if (this.getBukkitLivingEntity().getLocation().distance(BukkitAdapter.adapt(activeMob.getSpawner().getLocation())) < 5)
			return;

		if (!this.isRoamer())
		{
			this.getBukkitLivingEntity().teleport(BukkitAdapter.adapt(activeMob.getSpawner().getLocation()));
		}
		else
		{
			/* Throwing exception java.lang.NoSuchMethodError: net.minecraft.server.v1_14_R1.NavigationAbstract.a(DDD)Lnet/minecraft/server/v1_14_R1/PathEntity;
			CraftCreature nmsEntity = ((CraftCreature) this.getBukkitLivingEntity());
	        // Create a path to the location
	        PathEntity path = nmsEntity.getHandle().getNavigation().a(activeMob.getSpawner().getLocation().getX(), activeMob.getSpawner().getLocation().getY(), activeMob.getSpawner().getLocation().getZ());
	        // Move to that path at 'speed' speed.
	        nmsEntity.getHandle().getNavigation().a(path, nmsEntity.getHandle().getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue());
	        */
			this.getBukkitLivingEntity().teleport(BukkitAdapter.adapt(activeMob.getSpawner().getLocation()));
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
			Utils.RemoveEntity(this.getBukkitLivingEntity(), "ISCURRENTLYNPCPET");
			return false;
		}

		if (this.getActiveMob().getOwner() != null) {
			if (!this.getActiveMob().getOwner().isPresent())
				return false;

			Entity ownerEntity = Bukkit.getEntity(this.getActiveMob().getOwner().get());
			if (ownerEntity == null)
				return false;

			if (ownerEntity.isDead()) {
				this.getActiveMob().removeOwner();
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
				if (getLevel() > effect.getMax())
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
				if (attacker.getLevelCon(nearbySolEntity.getLevel()).equals(ChatColor.GRAY) && nearbySolEntity.getLevel() < 50)
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
					//if (!Utils.isEntityInLineOfSight(nearbySolEntity.getBukkitLivingEntity(), this.getBukkitLivingEntity()))
					//	continue;
					
					nearbySolEntity.addToHateList(attacker.getBukkitLivingEntity().getUniqueId(), 25,false);
				}
				
			}
		} catch (CoreStateInitException e)
		{
			
		}
	}

	@Override
	public void tryApplySpellOnSelf(int spellId, String requiredWeaponSkillType) {
		try
		{
			ISoliniaSpell spell = StateManager.getInstance().getConfigurationManager().getSpell(spellId);
			if (spell == null)
				return;
			
			spell.tryApplyOnEntity(getBukkitLivingEntity(), getBukkitLivingEntity(), false, requiredWeaponSkillType);
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
				
				ISoliniaLivingEntity solEntity = SoliniaLivingEntityAdapter.Adapt((LivingEntity)entity);
				if (!solEntity.isNPC())
					continue;
				
				if (solEntity.getNpcid() != npcid)
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
		
		vitals.fromData(partyMember, ((float)getHPRatio())/100F, manaPercent, this.getBukkitLivingEntity().getEntityId(), this.getName().replaceAll("\\^", "").replaceAll("\\|",""));
		return vitals;
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
		try
		{
			StateManager.getInstance().getEntityManager().forceClearTargetsAgainstMe(this.getBukkitLivingEntity());
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
	public boolean hasSpellEffectType(SpellEffectType type) {
		if (this.getBukkitLivingEntity() == null)
			return false;
		
		try
		{
		return StateManager.getInstance().getEntityManager().hasEntityEffectType(this.getBukkitLivingEntity(),
				type);
		} catch (CoreStateInitException e)
		{
			return false;
		}
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
			int p_level = pmob.getLevel();
			if (p_level < 1)
				p_level = 1;
			int t_level = tmob.getLevel();
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
			
			int chance = getSkill("DISARM"); // (1% @ 0 skill) (11% @ 200 skill) - against even con
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

		if (target.getBukkitLivingEntity().isOp())
			return false;
		
		
		/*if(target->GetSpecialAbility(NO_HARM_FROM_CLIENT)){
			return false;
		}*/

		// can't damage own pet (applies to everthing)
		if (target.isCurrentlyNPCPet() && this.getOwnerSoliniaLivingEntity() != null && this.getOwnerSoliniaLivingEntity().getBukkitLivingEntity().getUniqueId().equals(this.getBukkitLivingEntity().getUniqueId()))
			return false;
		
		return true;
	}

	@Override
	public void disarm(SoliniaLivingEntity disarmer, int chance) {
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

		ItemStack mainHandItemStack = this.getBukkitLivingEntity().getEquipment().getItemInMainHand();
		ItemStack offHandItemStack = this.getBukkitLivingEntity().getEquipment().getItemInOffHand();

		
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
		if (Utils.RandomBetween(0, 1000) <= chance)
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
						Utils.AddAccountClaim(player.getName(),augItem.getId());
					}
				}
				
				int count = itemStack.getAmount();
				if (count == 0)
					count = 1;
				for (int x = 0; x < count; x++)
				{
					Utils.AddAccountClaim(player.getName(),item.getId());
				}
				
				((Player) (this.getBukkitLivingEntity())).spigot().sendMessage(ChatMessageType.ACTION_BAR,
						new TextComponent(ChatColor.GRAY + "* You have been disarmed!"));
				player.sendMessage(ChatColor.GRAY + "Your item " + item.getDisplayname() + " has been added to your claims");
				
			}
			
			if (primary == true)
				this.getBukkitLivingEntity().getEquipment().setItemInMainHand(null);
			else
				this.getBukkitLivingEntity().getEquipment().setItemInOffHand(null);
			if (this.isPlayer())
			{
				Player player = (Player)this.getBukkitLivingEntity();
				player.updateInventory();
			}

			if (disarmer.isPlayer()) {
				((Player) getBukkitLivingEntity()).sendMessage(ChatColor.GRAY + "* "
						+ this.getBukkitLivingEntity().getCustomName() + " is disarmed!");
				
				disarmer.tryIncreaseSkill("DISARM",1);
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
}