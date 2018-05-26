package com.solinia.solinia.Listeners;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Animals;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import com.solinia.solinia.Solinia3CorePlugin;
import com.solinia.solinia.Adapters.SoliniaItemAdapter;
import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaItemException;
import com.solinia.solinia.Interfaces.ISoliniaFaction;
import com.solinia.solinia.Interfaces.ISoliniaGroup;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.ConfigurationManager;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.FactionStandingEntry;
import com.solinia.solinia.Models.InteractionType;
import com.solinia.solinia.Models.SoliniaActiveSpell;
import com.solinia.solinia.Models.SpellEffectType;
import com.solinia.solinia.Utils.Utils;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class Solinia3CoreEntityListener implements Listener {
	Solinia3CorePlugin plugin;

	public Solinia3CoreEntityListener(Solinia3CorePlugin solinia3CorePlugin) {
		// TODO Auto-generated constructor stub
		plugin = solinia3CorePlugin;
	}
	
	// Needs to occur before anything else
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityTargetEvent(EntityTargetEvent event) {
		if (event.isCancelled())
			return;

		if (!(event.getEntity() instanceof Creature))
			return;
		
		// cancel feigened if targetting
		try
		{
			boolean feigned = StateManager.getInstance().getEntityManager().isFeignedDeath(event.getEntity().getUniqueId());
			if (feigned == true)
			{
				StateManager.getInstance().getEntityManager().setFeignedDeath(event.getEntity().getUniqueId(), false);
			}
		} catch (CoreStateInitException e)
		{
			
		}
		
		try
		{
			Timestamp mzExpiry = StateManager.getInstance().getEntityManager().getMezzed((LivingEntity) event.getEntity());
			if (mzExpiry != null)
			{
				if (event.getEntity() instanceof Player)
				{
					event.getEntity().sendMessage("* You are mezzed!");
				}
				Utils.CancelEvent(event);
				return;
			}
		} catch (CoreStateInitException e)
		{
			
		}
		
		try
		{
			Timestamp stExpiry = StateManager.getInstance().getEntityManager().getStunned((LivingEntity) event.getEntity());
			if (stExpiry != null)
			{
				if (event.getEntity() instanceof Player)
				{
					event.getEntity().sendMessage("* You are stunned!");
				}
				Utils.CancelEvent(event);
				return;
			}
		} catch (CoreStateInitException e)
		{
			
		}
		
		try {
			// Me
			ISoliniaLivingEntity solEntity = SoliniaLivingEntityAdapter.Adapt((LivingEntity) event.getEntity());
			if (solEntity.isUndead() && !(event.getEntity() instanceof Player) && event.getTarget() instanceof LivingEntity) {
				if (StateManager.getInstance().getEntityManager().hasEntityEffectType((LivingEntity) event.getTarget(),SpellEffectType.InvisVsUndead)
				 || StateManager.getInstance().getEntityManager().hasEntityEffectType((LivingEntity) event.getTarget(), SpellEffectType.InvisVsUndead2)) {
					((Creature) event.getEntity()).setTarget(null);
					Utils.CancelEvent(event);
					return;
				}
			}
			
			if (!solEntity.isUndead()  && !(event.getEntity() instanceof Player) && !solEntity.isAnimal() && event.getTarget() instanceof LivingEntity) {
				if (StateManager.getInstance().getEntityManager().hasEntityEffectType((LivingEntity) event.getTarget(),SpellEffectType.Invisibility)
				 || StateManager.getInstance().getEntityManager().hasEntityEffectType((LivingEntity) event.getTarget(), SpellEffectType.Invisibility2)) {
					((Creature) event.getEntity()).setTarget(null);
					Utils.CancelEvent(event);
					return;
				}
			}
			
			if (solEntity.isAnimal()  && !(event.getEntity() instanceof Player) &&  event.getTarget() instanceof LivingEntity) {
				if (StateManager.getInstance().getEntityManager().hasEntityEffectType((LivingEntity) event.getTarget(),SpellEffectType.InvisVsAnimals)
				 || StateManager.getInstance().getEntityManager().hasEntityEffectType((LivingEntity) event.getTarget(), SpellEffectType.ImprovedInvisAnimals)) {
					((Creature) event.getEntity()).setTarget(null);
					Utils.CancelEvent(event);
					return;
				}
			}
			
			// rogue sneak
			if (event.getTarget() instanceof Player && !(event.getEntity() instanceof Player))
			{
				Player targetPlayer = (Player)event.getTarget();
				if (targetPlayer.isSneaking())
				{
					ISoliniaPlayer player = SoliniaPlayerAdapter.Adapt((Player)event.getTarget());
					if (player.getClassObj() != null)
					{
						if (player.getClassObj().isSneakFromCrouch())
						{
							Utils.CancelEvent(event);
							return;
						}
					}
				}
			}
			
			if (event.getEntity() != null && event.getTarget() != null)
			{
				if (!(event.getEntity() instanceof Player))
				{
					// Mez cancel target
					Timestamp mezExpiry = StateManager.getInstance().getEntityManager().getMezzed((LivingEntity) event.getTarget());
	
					if (mezExpiry != null) {
						((Creature) event.getEntity()).setTarget(null);
						event.getEntity().sendMessage("The target is mezzed, you cannot hit it");
						Utils.CancelEvent(event);
						return;
					}
				}
			}
			
			if (event.getEntity() != null && event.getTarget() != null)
			{
				if (!(event.getEntity() instanceof Player))
				{
					// Feigned death cancel target
					boolean feigned = StateManager.getInstance().getEntityManager().isFeignedDeath(event.getTarget().getUniqueId());
	
					if (feigned == true) {
						((Creature) event.getEntity()).setTarget(null);
						event.getEntity().sendMessage("The target is feigned, you cannot hit it");
						Utils.CancelEvent(event);
						return;
					}
				}
			}
			
		} catch (CoreStateInitException e) {
			return;
		}

	}

	@EventHandler
	public void onEntitySpawn(EntitySpawnEvent event) {
		if (event.getEntity() instanceof LivingEntity)
		{
			// if this is a skeleton entity, remove the chase task frmo the mobs AI
			event.getEntity().spigot();
		}
	}

	@EventHandler
	public void onEntityDamageEvent(EntityDamageEvent event) {
		if (event.isCancelled())
			return;
		
		// CLOSE RANGE TARGETTING - USED TO WORK AROUND MIN TARGET DISTANCE
		
		// Close range weapon
		if ((event instanceof EntityDamageByEntityEvent)) {
			EntityDamageByEntityEvent damagecause = (EntityDamageByEntityEvent) event;
			if (damagecause.getDamager() instanceof Player && (event.getEntity() instanceof LivingEntity))
			{
				try
				{
					Player damager = (Player)damagecause.getDamager();
					ItemStack itemstack = damager.getEquipment().getItemInMainHand();
					if (itemstack != null && damager.isSneaking() && 
							ConfigurationManager.WeaponMaterials.contains(itemstack.getType().name()))
					{
						StateManager.getInstance().getEntityManager().setEntityTarget((LivingEntity)damager,(LivingEntity)event.getEntity());
						Utils.CancelEvent(event);
						return;
					}
				} catch (CoreStateInitException e)
				{
					
				}
			}
		}
		
		// Close range spell/petcontrol rod
		if ((event instanceof EntityDamageByEntityEvent)) {
			EntityDamageByEntityEvent damagecause = (EntityDamageByEntityEvent) event;
			if (damagecause.getDamager() instanceof Player && (event.getEntity() instanceof LivingEntity))
			{
				try
				{
					Player damager = (Player)damagecause.getDamager();
					ItemStack itemstack = damager.getEquipment().getItemInMainHand();
					if (itemstack != null)
					{
						ISoliniaItem solItem = StateManager.getInstance().getConfigurationManager().getItem(itemstack);
						if (damager.isSneaking() && solItem != null && (solItem.isSpellscroll() || solItem.isPetControlRod()))
						{
							StateManager.getInstance().getEntityManager().setEntityTarget((LivingEntity)damager,(LivingEntity)event.getEntity());
							Utils.CancelEvent(event);
							return;
						}
					}
				} catch (CoreStateInitException e)
				{
					
				}
			}
		}
		
		// END CLOSE RANGE TARGETTING

		if ((event.getEntity() instanceof Player)) {
			if (!(event.getCause().equals(EntityDamageEvent.DamageCause.FALL)))
				return;

			Player player = (Player) event.getEntity();
			ISoliniaPlayer solplayer;
			try {
				solplayer = SoliniaPlayerAdapter.Adapt(player);
				if (solplayer == null)
					return;

				boolean cancelFall = solplayer.getSafefallCheck();
				if (cancelFall == true) {
					Utils.CancelEvent(event);;
					solplayer.emote(
							ChatColor.GRAY + "* " + solplayer.getFullName() + " lands softly, breaking their fall");
					solplayer.tryIncreaseSkill("SAFEFALL", 1);
					return;
				}
			} catch (CoreStateInitException e) {
				return;
			}
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.isCancelled())
			return;

		if (!(event instanceof EntityDamageByEntityEvent)) {
			return;
		}
		
		EntityDamageByEntityEvent damagecause = (EntityDamageByEntityEvent) event;
		
		// if this is a melee attack and the attacker is too far from the defender cancel teh event
		if (damagecause.getDamager() instanceof LivingEntity && !event.getCause().equals(DamageCause.THORNS) && !(damagecause.getDamager() instanceof Arrow) && event.getEntity() instanceof LivingEntity)
		{
			
			LivingEntity defender = (LivingEntity) event.getEntity();
			LivingEntity attacker = (LivingEntity) damagecause.getDamager();
			if (defender.getLocation().distance(attacker.getLocation()) > 3)
			{
				attacker.sendMessage(ChatColor.GRAY + "* You are too far away to attack!");
				Utils.CancelEvent(event);
				return;
			}
		}
		
		// If the event is being blocked by a shield negate 85% of it unless its thorns then always allow it through
		if (damagecause.getDamage(DamageModifier.BLOCKING) < 0)
		{
			if (event.getCause().equals(DamageCause.THORNS))
			{
				damagecause.setDamage(DamageModifier.BLOCKING, 0);
			} else {
				// Only give them 15% blocking
				double newarmour = (damagecause.getDamage(DamageModifier.BLOCKING) / 100) * 15;
				damagecause.setDamage(DamageModifier.BLOCKING, newarmour);
			}
		}
		
		// Remove buffs on attacker (invis should drop)
		// and check they are not mezzed
		try {
			if (damagecause.getDamager() instanceof LivingEntity) {
				
				LivingEntity attacker = (LivingEntity) damagecause.getDamager();
				// Change attacker to archer
				if (damagecause.getDamager() instanceof Arrow) {
					Arrow arr = (Arrow)attacker;
					if (arr.getShooter() instanceof LivingEntity) {
						attacker = (LivingEntity) arr.getShooter();
					} else {
					}
				}
				
				// cancel attacks on mobs mezzed
				if (attacker instanceof Creature && attacker instanceof LivingEntity && event.getEntity() instanceof LivingEntity)
				{
					ISoliniaLivingEntity solCreatureEntity = SoliniaLivingEntityAdapter.Adapt(attacker);
					if (solCreatureEntity.isPet() || !solCreatureEntity.isPlayer())
					{
						Timestamp mezExpiry = StateManager.getInstance().getEntityManager().getMezzed((LivingEntity)event.getEntity());
						
						if (mezExpiry != null) {
							((Creature) attacker).setTarget(null);
							
							if (solCreatureEntity.isPet())
							{
								Wolf wolf = (Wolf)attacker;
								wolf.setTarget(null);
								solCreatureEntity.say("Stopping attacking master, the target is mesmerized");
							}
							
							Utils.CancelEvent(event);
							return;
						}
					}
				}
				
				try
				{
					Timestamp mzExpiry = StateManager.getInstance().getEntityManager().getMezzed((LivingEntity) attacker);
					if (mzExpiry != null)
					{
						if (attacker instanceof Player)
						{
							attacker.sendMessage("* You are mezzed!");
						}
						
						Utils.CancelEvent(event);;
						return;
					}
					
					
					
				} catch (CoreStateInitException e)
				{
					
				}
				
				try
				{
					Timestamp stExpiry = StateManager.getInstance().getEntityManager().getStunned((LivingEntity) attacker);
					if (stExpiry != null)
					{
						if (attacker instanceof Player)
						{
							attacker.sendMessage("* You are stunned!");
						}
						
						Utils.CancelEvent(event);;
						return;
					}
					
					
					
				} catch (CoreStateInitException e)
				{
					
				}
				
				List<Integer> removeSpells = new ArrayList<Integer>();
				for (SoliniaActiveSpell spell : StateManager.getInstance().getEntityManager()
						.getActiveEntitySpells((LivingEntity) attacker).getActiveSpells()) {
					if (spell.getSpell().getSpellEffectTypes().contains(SpellEffectType.InvisVsUndead) ||

							spell.getSpell().getSpellEffectTypes().contains(SpellEffectType.Mez)
							|| spell.getSpell().getSpellEffectTypes().contains(SpellEffectType.InvisVsUndead2)
							|| spell.getSpell().getSpellEffectTypes().contains(SpellEffectType.Invisibility)
							|| spell.getSpell().getSpellEffectTypes().contains(SpellEffectType.Invisibility2)
							|| spell.getSpell().getSpellEffectTypes().contains(SpellEffectType.InvisVsAnimals)
							|| spell.getSpell().getSpellEffectTypes().contains(SpellEffectType.ImprovedInvisAnimals)) {
						if (!removeSpells.contains(spell.getSpell().getId()))
							removeSpells.add(spell.getSpell().getId());

					}
				}

				for (Integer spellId : removeSpells) {
					StateManager.getInstance().getEntityManager()
							.removeSpellEffectsOfSpellId(((LivingEntity) attacker).getUniqueId(), spellId);
				}
			}
		} catch (CoreStateInitException e) {
			// skip
		}
		
		

		// Remove buffs on recipient (invis should drop)
		try {
			if (event.getEntity() instanceof LivingEntity) {
				List<Integer> removeSpells = new ArrayList<Integer>();
				for (SoliniaActiveSpell spell : StateManager.getInstance().getEntityManager()
						.getActiveEntitySpells((LivingEntity) event.getEntity()).getActiveSpells()) {
					if (
							spell.getSpell().getSpellEffectTypes().contains(SpellEffectType.Mez) ||
							spell.getSpell().getSpellEffectTypes().contains(SpellEffectType.InvisVsUndead) ||
							spell.getSpell().getSpellEffectTypes().contains(SpellEffectType.InvisVsUndead)
							|| spell.getSpell().getSpellEffectTypes().contains(SpellEffectType.InvisVsUndead2)
							|| spell.getSpell().getSpellEffectTypes().contains(SpellEffectType.Invisibility)
							|| spell.getSpell().getSpellEffectTypes().contains(SpellEffectType.Invisibility2)
							|| spell.getSpell().getSpellEffectTypes().contains(SpellEffectType.InvisVsAnimals)
							|| spell.getSpell().getSpellEffectTypes().contains(SpellEffectType.ImprovedInvisAnimals)) {
						if (!removeSpells.contains(spell.getSpell().getId()))
							removeSpells.add(spell.getSpell().getId());

					}
				}

				for (Integer spellId : removeSpells) {
					StateManager.getInstance().getEntityManager()
							.removeSpellEffectsOfSpellId(((LivingEntity) event.getEntity()).getUniqueId(), spellId);
				}
			}
			
			// Check for rune damage
			if (event.getEntity() instanceof LivingEntity) {
				ISoliniaLivingEntity soldefender = SoliniaLivingEntityAdapter.Adapt((LivingEntity) event.getEntity());
				if (soldefender.isInvulnerable()) {
					event.setDamage(0);
					Utils.CancelEvent(event);;
					if (damagecause.getDamager() instanceof Player) {
						((Player) damagecause.getDamager())
						.sendMessage("* Your attack was prevented as the target is Invulnerable!");
					}
					if (event.getEntity() instanceof Player) {
						((Player) event.getEntity()).sendMessage("* Your invulnerability prevented the targets attack!");
					}
				}
			}
			
			//see if any runes want to reduce this damage
			if (event.getEntity() instanceof LivingEntity)
			{
				if (!event.getCause().equals(DamageCause.THORNS))
				{
					ISoliniaLivingEntity soldefender = SoliniaLivingEntityAdapter.Adapt((LivingEntity) event.getEntity());
					event.setDamage(Utils.reduceDamage(soldefender,event.getDamage()));
				}
			}
			
			// Check for rune damage
			if (event.getEntity() instanceof LivingEntity)
			{
				ISoliniaLivingEntity soldefender = SoliniaLivingEntityAdapter.Adapt((LivingEntity) event.getEntity());
				if (soldefender.getRune() > 0)
				{
					event.setDamage(soldefender.reduceAndRemoveRunesAndReturnLeftover(plugin, (int)event.getDamage()));
					
					if (event.getDamage() <= 0)
					{
						Utils.CancelEvent(event);;
						if (damagecause.getDamager() instanceof Player)
						{
							((Player)damagecause.getDamager()).sendMessage("* Your attack was absorbed by the targets Rune");
						}
						if (event.getEntity() instanceof Player)
						{
							((Player)event.getEntity()).sendMessage("* Your rune spell absorbed the targets attack!");
						}
						
						return;
					}
				}
			}
			
		} catch (CoreStateInitException e) {
			// skip
		}

		if ((damagecause.getDamager() instanceof LivingEntity || damagecause.getDamager() instanceof Arrow) && event.getEntity() instanceof LivingEntity) {
			// Never forward magic spell damage (cause thorns) to melee damage calculation
			// code
			if (event.getCause().equals(DamageCause.THORNS))
			{
				if (damagecause.getDamager() instanceof Player)
				{
					LivingEntity recipient = (LivingEntity)event.getEntity();
					DecimalFormat df = new DecimalFormat();
					df.setMaximumFractionDigits(2);
					String name = recipient.getName();
					if (recipient.getCustomName() != null)
						name = recipient.getCustomName();
					
					((Player)damagecause.getDamager()).spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("You SPELLDMG'd " + name + " for " + df.format(event.getDamage()) + " [" + df.format(recipient.getHealth()-event.getDamage()) + "/" + df.format(recipient.getMaxHealth()) + "]"));
				}
				
				if (event.getEntity() instanceof LivingEntity)
				{
					ISoliniaLivingEntity solentity;
					try {
						solentity = SoliniaLivingEntityAdapter.Adapt((LivingEntity)event.getEntity());
						
						if (event.getDamage() > solentity.getBukkitLivingEntity().getHealth() && solentity.hasDeathSave() > 0)
						{
							Utils.CancelEvent(event);
							solentity.removeDeathSaves(plugin);
							solentity.getBukkitLivingEntity().sendMessage("* Your death/divine save boon has saved you from death!");
							return;
						}
						
						solentity.damageHook(event.getDamage(),damagecause.getDamager());
					} catch (CoreStateInitException e) {
						// skip
					}
				}
				return;
			}
			try {
				Entity damager = damagecause.getDamager();
				// Change attacker to archer
				if (damagecause.getDamager() instanceof Arrow) {
					Arrow arr = (Arrow) damagecause.getDamager();
					if (arr.getShooter() instanceof LivingEntity) {
						damager = (LivingEntity) arr.getShooter();
						
						// Modify Player Bow Damage
						if (arr.getShooter() instanceof Player) {
							Player shooter = (Player) arr.getShooter();
							ItemStack mainitem = shooter.getInventory().getItemInMainHand();
							if (mainitem != null) {
								if (mainitem.getType() == Material.BOW) {
									int dmgmodifier = 0;

									if (Utils.IsSoliniaItem(mainitem)) {
										try
										{
										ISoliniaItem item = SoliniaItemAdapter.Adapt(mainitem);
										if (item.getDamage() > 0 && item.getBasename().equals("BOW")) {
											dmgmodifier = item.getDamage();
										}
										} catch (SoliniaItemException e) {
											// sok just skip
										}
									}

									event.setDamage(event.getDamage() + dmgmodifier);
								}
							}
						}
						
					} else {
					}
				}
				
				if (!(damager instanceof LivingEntity))
					return;
				
				LivingEntity attacker = (LivingEntity)damager;

				// Change attacker to archer
				if (damagecause.getDamager() instanceof Arrow) {
					Arrow arr = (Arrow) damagecause.getDamager();
					if (arr.getShooter() instanceof LivingEntity) {
						attacker = (LivingEntity) arr.getShooter();
					} else {
					}
				}
				
				if (attacker == null)
					return;
				
				ISoliniaLivingEntity soldefender = SoliniaLivingEntityAdapter.Adapt((LivingEntity) event.getEntity());
				ISoliniaLivingEntity solattacker = SoliniaLivingEntityAdapter.Adapt((LivingEntity) attacker);

				if (attacker instanceof Player && event.getEntity() instanceof Wolf) {
					if (soldefender.isPet()) {
						Wolf wolf = (Wolf) event.getEntity();
						if (wolf != null) {
							if (wolf.getTarget() == null || !wolf.getTarget().equals(attacker)) {
								Utils.CancelEvent(event);
								return;
							}
						} else {
							Utils.CancelEvent(event);
							return;
						}
					}
				}
				
				if (!(event instanceof EntityDamageByEntityEvent))
				{
					soldefender.damageHook(event.getDamage(),damagecause.getDamager());
					return;
				}
				
				solattacker.Attack(soldefender, event, damagecause.getDamager() instanceof Arrow, plugin);
				
			} catch (CoreStateInitException e) {

			}
		}

	}

	@EventHandler
	public void onShootBow(EntityShootBowEvent event) {
		if (event.isCancelled())
			return;
		
		try
		{
			Timestamp mzExpiry = StateManager.getInstance().getEntityManager().getMezzed((LivingEntity) event.getEntity());
			if (mzExpiry != null)
			{
				if (event.getEntity() instanceof Player)
				{
					event.getEntity().sendMessage("* You are mezzed!");
				}
				Utils.CancelEvent(event);;
				return;
			}
		} catch (CoreStateInitException e)
		{
			
		}
		
		try
		{
			Timestamp stExpiry = StateManager.getInstance().getEntityManager().getStunned((LivingEntity) event.getEntity());
			if (stExpiry != null)
			{
				if (event.getEntity() instanceof Player)
				{
					event.getEntity().sendMessage("* You are stunned!");
				}
				Utils.CancelEvent(event);;
				return;
			}
		} catch (CoreStateInitException e)
		{
			
		}

		if (event.getEntity() instanceof Player) {
			Player shooter = (Player) event.getEntity();
			{
				ItemStack seconditem = shooter.getInventory().getItemInOffHand();

				if (seconditem != null) {
					if (seconditem.getType() == Material.BOW) {
						shooter.sendMessage("You cannot shoot while you have a bow in your offhand");
						Utils.CancelEvent(event);;
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		
		try
		{
			Timestamp mzExpiry = StateManager.getInstance().getEntityManager().getMezzed((LivingEntity) event.getPlayer());
			if (mzExpiry != null)
			{
				if (event.getPlayer() instanceof Player)
				{
					event.getPlayer().sendMessage("* You are mezzed!");
				}
				Utils.CancelEvent(event);;
				return;
			}
		} catch (CoreStateInitException e)
		{
			
		}
		
		try
		{
			Timestamp stExpiry = StateManager.getInstance().getEntityManager().getStunned((LivingEntity) event.getPlayer());
			if (stExpiry != null)
			{
				if (event.getPlayer() instanceof Player)
				{
					event.getPlayer().sendMessage("* You are stunned!");
				}
				Utils.CancelEvent(event);;
				return;
			}
		} catch (CoreStateInitException e)
		{
			
		}
		
		
		if (!(event.getRightClicked() instanceof LivingEntity)) {
			return;
		}

		if (!(event.getRightClicked() instanceof LivingEntity)) {
			return;
		}

		if (event.getRightClicked() instanceof Player) {
			return;
		}

		if (event.getHand() != EquipmentSlot.HAND || event.getRightClicked() == null) {
			return;
		}

		try {
			// if its in combat dont respond
			if (event.getRightClicked() instanceof Creature)
			{
				Creature le = (Creature)event.getRightClicked();
				if (le.getTarget() == null)
				{
					ISoliniaLivingEntity solentity = SoliniaLivingEntityAdapter.Adapt((LivingEntity) event.getRightClicked());
					
					if (solentity.getNpcid() > 0) {
						SoliniaPlayerAdapter.Adapt(event.getPlayer()).setInteraction(
								solentity.getBukkitLivingEntity().getUniqueId(),
								StateManager.getInstance().getConfigurationManager().getNPC(solentity.getNpcid()));
						solentity.processInteractionEvent(event.getPlayer(), InteractionType.CHAT, "hail");
					}
				} else {
					// too spammy, move ot action bar
					event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR,
							new TextComponent(ChatColor.GRAY + "* This npc will not respond to interactions while it is in combat (has a target)"));
				}
			} else {
				ISoliniaLivingEntity solentity = SoliniaLivingEntityAdapter.Adapt((LivingEntity) event.getRightClicked());
				if (solentity.getNpcid() > 0) {
					SoliniaPlayerAdapter.Adapt(event.getPlayer()).setInteraction(
							solentity.getBukkitLivingEntity().getUniqueId(),
							StateManager.getInstance().getConfigurationManager().getNPC(solentity.getNpcid()));
					solentity.processInteractionEvent(event.getPlayer(), InteractionType.CHAT, "hail");
				}
			}
		} catch (CoreStateInitException e) {
			e.printStackTrace();
			return;
		}
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		if ((event.getEntity() instanceof ArmorStand)) {
			return;
		}

		if (!(event.getEntity() instanceof LivingEntity))
			return;
		
		try {
			StateManager.getInstance().getEntityManager().clearTargetsAgainstMe((LivingEntity)event.getEntity());
		} catch (CoreStateInitException e) {
			
		}

		if (event.getEntity() instanceof Player)
			return;

		if (!(event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent))
			return;

		if (event.getEntity() instanceof Animals && !Utils.isLivingEntityNPC((LivingEntity) event.getEntity()))
			return;

		EntityDamageByEntityEvent entitykiller = (EntityDamageByEntityEvent) event.getEntity().getLastDamageCause();
		Entity damager = entitykiller.getDamager();
		if (damager instanceof Projectile) {

			Projectile projectile = (Projectile) damager;
			damager = (Entity) projectile.getShooter();
		}

		if (!(damager instanceof LivingEntity))
			return;

		ISoliniaLivingEntity soldamagerentity = null;
		try {
			soldamagerentity = SoliniaLivingEntityAdapter.Adapt((LivingEntity) damager);
		} catch (CoreStateInitException e) {

		}

		// If damager is npc, have a chance to trigger its chat text for slaying
		// something
		if ((!(damager instanceof Player)) && Utils.isLivingEntityNPC((LivingEntity) damager)) {
			soldamagerentity.doSlayChat();
		}

		if (!(damager instanceof Player) && !soldamagerentity.isPet())
			return;

		try {
			ISoliniaLivingEntity livingEntity = SoliniaLivingEntityAdapter.Adapt(event.getEntity());
			ISoliniaPlayer player = null;
			if (damager instanceof Player) {
				player = SoliniaPlayerAdapter.Adapt((Player) damager);
			}
			if (soldamagerentity.isPet()) {
				if (damager instanceof Wolf) {
					Wolf w = (Wolf) damager;
					player = SoliniaPlayerAdapter.Adapt((Player) w.getOwner());
				}
			}
			if (player == null) {
				return;
			}

			Double experience = Utils.getExperienceRewardAverageForLevel(livingEntity.getLevel());

			// try to share with group
			ISoliniaGroup group = StateManager.getInstance().getGroupByMember(player.getUUID());
			if (group != null) {
				Integer dhighestlevel = 0;

				List<Integer> levelranges = new ArrayList<Integer>();

				for (UUID member : group.getMembers()) {
					ISoliniaPlayer playerchecked = SoliniaPlayerAdapter.Adapt(Bukkit.getPlayer(member));
					int checkedlevel = playerchecked.getLevel();
					levelranges.add(checkedlevel);
				}

				Collections.sort(levelranges);

				// get the highest person in the group
				dhighestlevel = levelranges.get(levelranges.size() - 1);

				int ihighlvl = (int) Math.floor(dhighestlevel);
				int ilowlvl = Utils.getMinLevelFromLevel(ihighlvl);

				if (ilowlvl < 1) {
					ilowlvl = 1;
				}

				if (player.getLevel() < ilowlvl) {
					// Only award player the experience
					// as they are out of range of the group
					if (livingEntity.getLevel() >= Utils.getMinLevelFromLevel(player.getLevel())) {
						player.increasePlayerExperience(experience, true);
						
						// Grant title for killing mob
						if (livingEntity.getNpcid() > 0)
						{
							ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(livingEntity.getNpcid());
							if (npc != null && !npc.getDeathGrantsTitle().equals(""))
							{
								player.grantTitle(npc.getDeathGrantsTitle());
							}
							
							if (npc.isBoss() || npc.isRaidboss())
							{
								player.grantTitle("the Vanquisher");
							}
						}
						
					} else {
						//player.getBukkitPlayer().sendMessage(ChatColor.GRAY + "* The npc was too low level to gain experience from - Was: " + livingEntity.getLevel() + " Min: " + Utils.getMinLevelFromLevel(player.getLevel()));
					}

				} else {
					for (UUID member : group.getMembers()) {
						Player tgtplayer = Bukkit.getPlayer(member);
						if (tgtplayer != null) {
							ISoliniaPlayer tgtsolplayer = SoliniaPlayerAdapter.Adapt(tgtplayer);
							int tgtlevel = tgtsolplayer.getLevel();

							if (tgtlevel < ilowlvl) {
								tgtplayer.sendMessage(ChatColor.GRAY + "You were out of level range to gain experience in this group (Min: " + ilowlvl
												+ " Max: " + ihighlvl + ")");
								continue;
							}

							if (!tgtplayer.getWorld().equals(player.getBukkitPlayer().getWorld())) {
								//tgtplayer.sendMessage("You were out of range for shared group xp (world)");
								continue;
							}

							if (tgtplayer.getLocation().distance(player.getBukkitPlayer().getLocation()) <= 100) {
								if (livingEntity.getLevel() >= (Utils.getMinLevelFromLevel(tgtsolplayer.getLevel()))) {
									tgtsolplayer.increasePlayerExperience(experience, true);
									
									// Grant title for killing mob
									if (livingEntity.getNpcid() > 0)
									{
										ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(livingEntity.getNpcid());
										if (npc != null && !npc.getDeathGrantsTitle().equals(""))
										{
											tgtsolplayer.grantTitle(npc.getDeathGrantsTitle());
										}
										
										if (npc.isBoss() || npc.isRaidboss())
										{
											tgtsolplayer.grantTitle("the Vanquisher");
										}
									}
									
								} else {
									//tgtplayer.sendMessage(ChatColor.GRAY + "* The npc was too low level to gain experience from - Was: " + livingEntity.getLevel() + " Min: " + Utils.getMinLevelFromLevel(tgtsolplayer.getLevel()));
								}

							} else {
								//tgtplayer.sendMessage(ChatColor.GRAY + "You were out of range for shared group xp (distance)");
								continue;
							}
						}
					}
				}
			} else {
				if (livingEntity.getLevel() >= (Utils.getMinLevelFromLevel(player.getLevel()))) {
					player.increasePlayerExperience(experience, true);
					
					// Grant title for killing mob
					if (livingEntity.getNpcid() > 0)
					{
						ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(livingEntity.getNpcid());
						if (npc != null && !npc.getDeathGrantsTitle().equals(""))
						{
							player.grantTitle(npc.getDeathGrantsTitle());
						}
						
						if (npc.isBoss() || npc.isRaidboss())
						{
							player.grantTitle("the Vanquisher");
						}
					}
					
				} else {
					//player.getBukkitPlayer().sendMessage(ChatColor.GRAY + "* The npc was too low level to gain experience from - Was: " + livingEntity.getLevel() + " Min: " + Utils.getMinLevelFromLevel(player.getLevel()));
				}
			}

			if (livingEntity.getNpcid() > 0) {
				ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(livingEntity.getNpcid());
				if (npc.getFactionid() > 0)
				{
					ISoliniaFaction faction = StateManager.getInstance().getConfigurationManager().getFaction(npc.getFactionid());
					player.decreaseFactionStanding(npc.getFactionid(),50);
					for (FactionStandingEntry factionEntry : faction.getFactionEntries())
					{
						if (factionEntry.getValue() >= 1500)
						{
							// If this is an ally of the faction, grant negative faction
							player.decreaseFactionStanding(factionEntry.getFactionId(),10);
						}
						
						if (factionEntry.getValue() <= -1500)
						{
							// If this is an enemy of the faction, grant positive faction
							player.increaseFactionStanding(factionEntry.getFactionId(),1);
						}
					}
				}
			}
			
			if (livingEntity.getNpcid() > 0)
			{
				ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(livingEntity.getNpcid());
				if (npc != null && !npc.getDeathGrantsTitle().equals(""))
				{
					player.grantTitle(npc.getDeathGrantsTitle());
				}
				
				if (npc.isBoss() || npc.isRaidboss())
				{
					player.grantTitle("the Vanquisher");
				}
				
				if (npc.isBoss() || npc.isRaidboss())
				{
					Utils.BroadcastPlayers("[VICTORY] The foundations of the earth shake following the destruction of " + npc.getName() + " at the hands of " + player.getFullNameWithTitle() + "!");
				}
			}
			
			player.giveMoney(1);
			livingEntity.dropLoot();
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
