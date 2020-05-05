package com.solinia.solinia.Managers;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Animals;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Vehicle;
import org.bukkit.plugin.Plugin;

import com.solinia.solinia.Adapters.ItemStackAdapter;
import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.IEntityManager;
import com.solinia.solinia.Interfaces.INPCEntityProvider;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Interfaces.ISoliniaNPCMerchant;
import com.solinia.solinia.Interfaces.ISoliniaNPCMerchantEntry;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Models.ActiveSongs;
import com.solinia.solinia.Models.ActiveSpellEffect;
import com.solinia.solinia.Models.CastingSpell;
import com.solinia.solinia.Models.EntityAutoAttack;
import com.solinia.solinia.Models.HINT;
import com.solinia.solinia.Models.SoliniaActiveSpell;
import com.solinia.solinia.Models.SoliniaEntitySpells;
import com.solinia.solinia.Models.SoliniaLivingEntity;
import com.solinia.solinia.Models.UniversalMerchant;
import com.solinia.solinia.Models.UniversalMerchantEntry;
import com.solinia.solinia.Models.SpellEffectType;
import com.solinia.solinia.Models.SpellType;
import com.solinia.solinia.Utils.DebugUtils;
import com.solinia.solinia.Utils.PartyWindowUtils;
import com.solinia.solinia.Utils.RaycastUtils;
import com.solinia.solinia.Utils.SpecialEffectUtils;
import com.solinia.solinia.Utils.Utils;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.api.exceptions.InvalidMobTypeException;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;
import me.libraryaddict.disguise.disguisetypes.TargetedDisguise;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_14_R1.GenericAttributes;
import net.minecraft.server.v1_14_R1.Tuple;

public class EntityManager implements IEntityManager {
	INPCEntityProvider npcEntityProvider;
	private ConcurrentHashMap<UUID, SoliniaEntitySpells> entitySpells = new ConcurrentHashMap<UUID, SoliniaEntitySpells>();
	private ConcurrentHashMap<UUID, ActiveSongs> entitySinging = new ConcurrentHashMap<UUID, ActiveSongs>();
	private ConcurrentHashMap<UUID, Timestamp> lastCallForAssist = new ConcurrentHashMap<UUID, Timestamp>();
	private ConcurrentHashMap<UUID, Integer> petFocus = new ConcurrentHashMap<UUID, Integer>();
	private ConcurrentHashMap<UUID, Timestamp> lastDoubleAttack = new ConcurrentHashMap<UUID, Timestamp>();
	private ConcurrentHashMap<UUID, Timestamp> lastDisarm = new ConcurrentHashMap<UUID, Timestamp>();
	private ConcurrentHashMap<UUID, ConcurrentHashMap<UUID, Tuple<Integer,Boolean>>> hateList = new ConcurrentHashMap<UUID, ConcurrentHashMap<UUID, Tuple<Integer,Boolean>>>();
	private ConcurrentHashMap<UUID, ConcurrentHashMap<UUID, Tuple<Integer,Boolean>>> reverseHateList = new ConcurrentHashMap<UUID, ConcurrentHashMap<UUID, Tuple<Integer,Boolean>>>();
	private ConcurrentHashMap<UUID, Timestamp> lastRiposte = new ConcurrentHashMap<UUID, Timestamp>();
	private ConcurrentHashMap<UUID, Timestamp> lastBindwound = new ConcurrentHashMap<UUID, Timestamp>();
	private ConcurrentHashMap<UUID, Timestamp> lastMeleeAttack = new ConcurrentHashMap<UUID, Timestamp>();
	private ConcurrentHashMap<UUID, Timestamp> dontHealMe = new ConcurrentHashMap<UUID, Timestamp>();
	private ConcurrentHashMap<UUID, Timestamp> dontRootMe = new ConcurrentHashMap<UUID, Timestamp>();
	private ConcurrentHashMap<UUID, Timestamp> dontBuffMe = new ConcurrentHashMap<UUID, Timestamp>();
	private ConcurrentHashMap<UUID, Timestamp> dontSnareMe = new ConcurrentHashMap<UUID, Timestamp>();
	private ConcurrentHashMap<UUID, Timestamp> dontDotMe = new ConcurrentHashMap<UUID, Timestamp>();
	private ConcurrentHashMap<UUID, Integer> entityManaLevels = new ConcurrentHashMap<UUID, Integer>();
	private ConcurrentHashMap<UUID, Timestamp> entityMezzed = new ConcurrentHashMap<UUID, Timestamp>();
	private ConcurrentHashMap<UUID, Timestamp> entityStunned = new ConcurrentHashMap<UUID, Timestamp>();
	private ConcurrentHashMap<UUID, UUID> petownerdata = new ConcurrentHashMap<UUID, UUID>();
	private ConcurrentHashMap<String, Timestamp> entitySpellCooldown = new ConcurrentHashMap<String, Timestamp>();
	//private ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Integer>> temporaryMerchantItems = new ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Integer>>();
	private ConcurrentHashMap<UUID, Inventory> merchantInventories = new ConcurrentHashMap<UUID, Inventory>();
	private ConcurrentHashMap<UUID, UniversalMerchant> universalMerchant = new ConcurrentHashMap<UUID, UniversalMerchant>();
	private ConcurrentHashMap<UUID, Boolean> playerInTerritory = new ConcurrentHashMap<UUID, Boolean>();
	private ConcurrentHashMap<UUID, Boolean> playerSetMain = new ConcurrentHashMap<UUID, Boolean>();
	private ConcurrentHashMap<UUID, EntityAutoAttack> entityAutoAttack = new ConcurrentHashMap<UUID, EntityAutoAttack>();
	private ConcurrentHashMap<UUID, UUID> entityTargets = new ConcurrentHashMap<UUID, UUID>();
	private ConcurrentHashMap<UUID, Boolean> feignedDeath = new ConcurrentHashMap<UUID, Boolean>();
	private ConcurrentHashMap<UUID, UUID> following = new ConcurrentHashMap<UUID, UUID>();
	private ConcurrentHashMap<UUID, CastingSpell> entitySpellCasting = new ConcurrentHashMap<UUID, CastingSpell>();
	private ConcurrentHashMap<UUID, Location> entityTracking = new ConcurrentHashMap<UUID, Location>();
	
	private Plugin plugin;
	
	public EntityManager(Plugin plugin, INPCEntityProvider npcEntityProvider) {
		this.npcEntityProvider = npcEntityProvider;
		this.plugin = plugin;
	}
	@Override
	public ConcurrentHashMap<UUID, UUID> getPetOwnerData()
	{
		return this.petownerdata;
	}
	
	@Override
	public void forceSetEntityTarget(LivingEntity me, LivingEntity target)
	{
		try
		{
			// no..
			if (target instanceof ArmorStand)
			{
				me.sendMessage("You cannot target ArmorStands");
				return;
			}
			
			// When changing target always clear auto attack
			if (me instanceof Player)
			{
				if (target == null)
				{
					StateManager.getInstance().getEntityManager().setEntityAutoAttack((Player)me, false);
				} else {
					// get current target
					if (StateManager.getInstance().getEntityManager().getEntityTargets().get(me.getUniqueId()) != null)
					{
						if (me != null && target != null && !StateManager.getInstance().getEntityManager().getEntityTargets().get(me.getUniqueId()).toString().equals(target.getUniqueId().toString()))
						{
							StateManager.getInstance().getEntityManager().setEntityAutoAttack((Player)me, false);
						}
					}
				}
			}
			
			if (me instanceof Creature)
			{
				try {
					SoliniaLivingEntityAdapter.Adapt(me).setAttackTarget(target);
				} catch (CoreStateInitException e) {
				}
			}
			
			if (target == null)
			{
				// no need, is on the boss bar
				//source.sendMessage(ChatColor.GRAY + "Cleared your target");
				StateManager.getInstance().getEntityManager().getEntityTargets().remove(me.getUniqueId());
				
		        if (me instanceof Player)
				{
					PartyWindowUtils.UpdateWindow((Player)me, false, true);
				}
			} else {
				StateManager.getInstance().getEntityManager().getEntityTargets().put(me.getUniqueId(), target.getUniqueId());
				if (me instanceof Player)
				{
					PartyWindowUtils.UpdateWindow((Player)me,false,true);
				}
			}
		} catch (CoreStateInitException e)
		{
			return;
		}
	}
	
	@Override
	public LivingEntity forceGetEntityTarget(LivingEntity me)
	{
		try
		{
			if (me == null)
				return null;
			
			// If i'm a creature, return creature target
			if (me instanceof Creature)
			{
				return ((Creature)me).getTarget();
			}
			
			UUID target = StateManager.getInstance().getEntityManager().getEntityTargets().get(me.getUniqueId());
			if (target == null)
			{
				return null;
			}
			
			Entity entity = Bukkit.getEntity(target);
			if (entity == null)
			{
				forceSetEntityTarget(me,null);
				return null;
			}
			
			if (!(entity instanceof LivingEntity))
			{
				forceSetEntityTarget(me,null);
				return null;
			}
	
			if (((LivingEntity)entity).isDead())
			{
				forceSetEntityTarget(me,null);
				return null;
			}
			
			return ((LivingEntity)entity);		
		} catch (CoreStateInitException e)
		{
			
		}
		
		return null;
	}
	
	@Override
	public void forceClearTargetsAgainstMe(LivingEntity me) {
		for (Player player : me.getWorld().getPlayers())
		{
			if (forceGetEntityTarget(me) == null)
				continue;
			
			if (forceGetEntityTarget(me).getUniqueId().toString().equals(me.getUniqueId().toString()))
				forceSetEntityTarget(me,null);
		}
		
		for(Entity entity : me.getNearbyEntities(25, 25, 25))
		{
			if (entity instanceof Creature)
			{
				if (((Creature) entity).getTarget() != null)
				if (((Creature) entity).getTarget().getUniqueId().toString().equals(me.getUniqueId().toString()))
				{
					forceSetEntityTarget(me,null);
				}
			}
		}

	}
	
	@Override
	public Inventory getNPCMerchantInventory(UUID playerUUID, ISoliniaNPCMerchant soliniaNpcMerchant, int pageno)
	{
		try
		{
			if (!soliniaNpcMerchant.getRequiresPermissionNode().equals(""))
			{
				Player player = Bukkit.getPlayer(playerUUID);
				if (player != null)
				{
					if (!player.hasPermission(soliniaNpcMerchant.getRequiresPermissionNode()))
					{					
						player.sendMessage("This requires a permission node you do not have");
						return null;
					}
				}
			}
			
			List<ISoliniaNPCMerchantEntry> fullmerchantentries = StateManager.getInstance().getEntityManager()
					.getNPCMerchantCombinedEntries(soliniaNpcMerchant);
			
			List<UniversalMerchantEntry> entries = new ArrayList<UniversalMerchantEntry>();
			
			for(ISoliniaNPCMerchantEntry entry : fullmerchantentries)
			{
				entries.add(UniversalMerchantEntry.FromNPCMerchantEntry(entry));
			}
			
			UniversalMerchant merchant = new UniversalMerchant();
			merchant.fullmerchantentries = entries;
			merchant.merchantName = soliniaNpcMerchant.getName();
			
			// Cache
			universalMerchant.put(merchant.universalMerchant, merchant);
			return getMerchantInventory(playerUUID, pageno, merchant);
		} catch (CoreStateInitException e)
		{
			return null;
		}
	}
	
	@Override
	public Inventory getMerchantInventory(UUID playerUUID, int pageno, UniversalMerchant universalMerchant)
	{
		if (universalMerchant.fullmerchantentries.size() < 1)
			return null;
		
		try
		{
			merchantInventories.put(playerUUID, Bukkit.createInventory(null, 27, universalMerchant.merchantName + " Page: " + pageno));
			
			pageno = pageno - 1;
			int sizePerPage = 18;
			
			List<UniversalMerchantEntry> merchantentries = universalMerchant.fullmerchantentries.stream()
					  .skip(pageno * sizePerPage)
					  .limit(sizePerPage)
					  .collect(Collectors.toCollection(ArrayList::new));
			
			int lastpage = (int)Math.ceil((float)universalMerchant.fullmerchantentries.size() / (float)sizePerPage);
			
			for (int i = 0; i < 27; i++)
			{
				ItemStack itemStack = new ItemStack(Material.BARRIER);
				ItemMeta itemMeta = itemStack.getItemMeta();
				itemMeta.setDisplayName("EMPTY SLOT #" + i);
				itemStack.setItemMeta(itemMeta);
				
				try
				{
					UniversalMerchantEntry entry = merchantentries.get(i);
					itemStack = StateManager.getInstance().getConfigurationManager().getItem(entry.getItemid()).asItemStackForMerchant(entry.getCostMultiplier());
					ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(entry.getItemid());
					ItemMeta meta = itemStack.getItemMeta();
					meta.setDisplayName("Display Item: " + itemStack.getItemMeta().getDisplayName());

					if (item != null)
					{
						Player tmpPlayer = Bukkit.getPlayer(playerUUID);

						if (item.isSpellscroll() && tmpPlayer != null)
						{
							ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(tmpPlayer);
							if (solPlayer != null)
							{
								if (solPlayer.getSpellBookItems().contains(item.getId()))
								{
									meta.setDisplayName("Display Item: " + itemStack.getItemMeta().getDisplayName() + ChatColor.RED + " [In Spellbook]" + ChatColor.RESET);
								}
							}
						}
					}
					
					// if item is a spell, show if the spell is in the players spell book
					itemStack.setItemMeta(meta);
					itemStack.setAmount(1);
					merchantInventories.get(playerUUID).addItem(itemStack);
				} catch (IndexOutOfBoundsException eOutOfBounds)
				{
					// Final Row left
					if(i == 18)
					{
						if (pageno != 0)
						{
							itemStack = new ItemStack(Material.LEGACY_SKULL_ITEM);
							itemStack.setItemMeta(ItemStackAdapter.buildSkull((SkullMeta) itemStack.getItemMeta(), UUID.fromString("1226610a-b7f8-47e5-a15d-126c4ef18635"), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjg0ZjU5NzEzMWJiZTI1ZGMwNThhZjg4OGNiMjk4MzFmNzk1OTliYzY3Yzk1YzgwMjkyNWNlNGFmYmEzMzJmYyJ9fX0=", null));
							itemStack.setDurability((short) 3);
							itemMeta = itemStack.getItemMeta();
							itemMeta.setDisplayName("<-- PREVIOUS PAGE");
							itemStack.setItemMeta(itemMeta);
						}
					}
					
					// Identifier Block
					if(i == 19)
					{
						itemStack = new ItemStack(Material.LEGACY_SKULL_ITEM);
						itemStack.setItemMeta(ItemStackAdapter.buildSkull((SkullMeta) itemStack.getItemMeta(), UUID.fromString("9c3bb224-bc6e-4da8-8b15-a35c97bc3b16"), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDI5NWE5MjkyMzZjMTc3OWVhYjhmNTcyNTdhODYwNzE0OThhNDg3MDE5Njk0MWY0YmZlMTk1MWU4YzZlZTIxYSJ9fX0=", null));
						itemMeta = itemStack.getItemMeta();
						List<String> lore = new ArrayList<String>();
						UUID universalmerchant = universalMerchant.universalMerchant;
						Integer page = pageno + 1;
						Integer nextpage = page + 1;
						lore.add(universalmerchant.toString());
						lore.add(page.toString());
						
						if (lastpage > nextpage)
						{
							lore.add(nextpage.toString());
						} else {
							lore.add("0");
						}
						
						itemMeta.setLore(lore);
						itemStack.setDurability((short) 3);
						itemMeta.setDisplayName("MERCHANT: " + universalMerchant.merchantName);
						itemStack.setItemMeta(itemMeta);
						itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, 999);
					}
					
					// Final Row right
					if(i == 26)
					{
						if ((pageno + 1) < lastpage)
						{
							itemStack = new ItemStack(Material.LEGACY_SKULL_ITEM);
							itemStack.setItemMeta(ItemStackAdapter.buildSkull((SkullMeta) itemStack.getItemMeta(), UUID.fromString("94fbab2d-668a-4a42-860a-c357f7acc19a"), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmNmZTg4NDVhOGQ1ZTYzNWZiODc3MjhjY2M5Mzg5NWQ0MmI0ZmMyZTZhNTNmMWJhNzhjODQ1MjI1ODIyIn19fQ==", null));
							itemMeta = itemStack.getItemMeta();
							itemStack.setDurability((short) 3);
							itemMeta.setDisplayName("NEXT PAGE -->");
							itemStack.setItemMeta(itemMeta);
						}
					}
					
					merchantInventories.get(playerUUID).addItem(itemStack);
				}
			}
			
			return merchantInventories.get(playerUUID);
		} catch (CoreStateInitException e)
		{
			return null;
		}
	}

	@Override
	public ISoliniaLivingEntity getLivingEntity(LivingEntity livingentity)
	{
		return new SoliniaLivingEntity(livingentity);
	}
	
	@Override
	public INPCEntityProvider getNPCEntityProvider()
	{
		return npcEntityProvider;
	}
	
	@Override 
	public boolean hasEntityEffectType(LivingEntity livingEntity, SpellEffectType type)
	{
		try
		{
			for (SoliniaActiveSpell activeSpell : StateManager.getInstance().getEntityManager().getActiveEntitySpells(livingEntity).getActiveSpells()) {
				for (ActiveSpellEffect spelleffect : activeSpell.getActiveSpellEffects()) {
					if (spelleffect.getSpellEffectType().equals(type)) {
						return true;
					}
				}
			}
		} catch (CoreStateInitException e)
		{
			return false;
		}
		
		return false;
	}

	@Override
	public boolean addActiveEntitySpell(LivingEntity targetEntity, ISoliniaSpell soliniaSpell, LivingEntity sourceEntity, boolean sendMessages, String requiredWeaponSkillType) {
		{
			return addActiveEntitySpell(targetEntity, soliniaSpell, sourceEntity, sendMessages, requiredWeaponSkillType, false);
		}
	}

	
	@Override
	public boolean addActiveEntitySpell(LivingEntity targetEntity, ISoliniaSpell soliniaSpell, LivingEntity sourceEntity, boolean sendMessages, String requiredWeaponSkillType, boolean racialPassive) {
		try {
			DebugUtils.DebugLog("EntityManager", "addActiveEntitySpell", sourceEntity, "Beginning adding spell to entity on behalf of caster");
			if (soliniaSpell.isCharmSpell() && getPet(sourceEntity.getUniqueId()) != null && !getPet(sourceEntity.getUniqueId()).getUniqueId().equals(targetEntity.getUniqueId()))
			{
				sourceEntity.sendMessage("This is already a pet");
				DebugUtils.DebugLog("EntityManager", "addActiveEntitySpell", sourceEntity, "Source aborting spell cast, was either charm with a pet or related to pet status");
				return false;
			}
			
		
			if (entitySpells.get(targetEntity.getUniqueId()) == null)
			{
				entitySpells.put(targetEntity.getUniqueId(), new SoliniaEntitySpells(targetEntity));
			}
		
			ISoliniaLivingEntity solLivingSourceEntity = SoliniaLivingEntityAdapter.Adapt(sourceEntity);
			int duration = Utils.getDurationFromSpell(solLivingSourceEntity, soliniaSpell);
			if (soliniaSpell.isBardSong() && duration == 0)
			{
				duration = 18;
			}
			
			boolean addSpellResult = entitySpells.get(targetEntity.getUniqueId()).addSpell(plugin, soliniaSpell, sourceEntity, duration, sendMessages, requiredWeaponSkillType, racialPassive);
			DebugUtils.DebugLog("EntityManager", "addActiveEntitySpell", sourceEntity, "addSpell result was: " + addSpellResult);

			if (targetEntity instanceof Player)
				SoliniaPlayerAdapter.Adapt((Player)targetEntity).sendEffects();
			return addSpellResult;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	@Override
	public SoliniaEntitySpells getActiveEntitySpells(LivingEntity entity) {
		if (entitySpells.get(entity.getUniqueId()) == null)
			entitySpells.put(entity.getUniqueId(), new SoliniaEntitySpells(entity));
		
		return entitySpells.get(entity.getUniqueId());
	}

	@Override
	public void spellTick() {
		List<UUID> uuidRemoval = new ArrayList<UUID>();
		for (SoliniaEntitySpells entityEffects : entitySpells.values())
		{
			Entity entity = Bukkit.getEntity(entityEffects.getLivingEntityUUID());
			if (entity == null)
			{
				uuidRemoval.add(entityEffects.getLivingEntityUUID());
			} else {
				if (entity.isDead())
					uuidRemoval.add(entityEffects.getLivingEntityUUID());
			}
		}
		
		for(UUID uuid : uuidRemoval)
		{
			removeSpellEffects(uuid, false, false);
		}
		
		
		for(SoliniaEntitySpells entityEffects : entitySpells.values())
		{
			try
			{
				entityEffects.run(plugin, true);
				
				if (entityEffects.getLivingEntity() != null && entityEffects.getLivingEntity() instanceof Player)
					SoliniaPlayerAdapter.Adapt((Player)entityEffects.getLivingEntity()).sendEffects();
			} catch (Exception e)
			{
				e.printStackTrace();
			}


		}
	}
	
	@Override 
	public void removeSpellEffects(UUID uuid, boolean forceDoNotLoopBardSpell, boolean removeNonCombatEffects)
	{
		removeSpellEffectsExcept(uuid,forceDoNotLoopBardSpell,removeNonCombatEffects,new ArrayList<SpellEffectType>());
	}
	
	@Override
	public void removeSpellEffectsExcept(UUID uuid, boolean forceDoNotLoopBardSpell, boolean removeNonCombatEffects,
			List<SpellEffectType> exclude) {

		// We should never do this again
		// as we want to handle it in the removeAllSpells section of the entity spells
		// - entitySpells.remove(uuid);

		if (entitySpells.get(uuid) != null)
			entitySpells.get(uuid).removeAllSpellsExcept(plugin, forceDoNotLoopBardSpell, removeNonCombatEffects, exclude);
		
		try
		{
			if (Bukkit.getEntity(uuid) != null && Bukkit.getEntity(uuid) instanceof Player)
				SoliniaPlayerAdapter.Adapt((Player)Bukkit.getEntity(uuid)).sendEffects();
		} catch (CoreStateInitException e)
		{
			e.printStackTrace();
		}
	}

	
	@Override 
	public void removeSpellEffectsOfSpellId(UUID uuid, int spellId, boolean forceDoNotLoopBardSpell, boolean removeNonCombatEffects)
	{
		if (entitySpells.get(uuid) != null)
			entitySpells.get(uuid).removeAllSpellsOfId(plugin, spellId, forceDoNotLoopBardSpell, removeNonCombatEffects);
		
		try
		{
			if (Bukkit.getEntity(uuid) != null && Bukkit.getEntity(uuid) instanceof Player)
				SoliniaPlayerAdapter.Adapt((Player)Bukkit.getEntity(uuid)).sendEffects();
		} catch (CoreStateInitException e)
		{
			e.printStackTrace();
		}

	}
	
	@Override
	public void doNPCRandomChat() {
		List<Integer> completedNpcsIds = new ArrayList<Integer>();
		for(Player player : Bukkit.getOnlinePlayers())
		{
			for(Entity entity : player.getNearbyEntities(25, 25, 25))
			{
				if (entity instanceof Player)
					continue;
				
				if (!(entity instanceof LivingEntity))
					continue;
				
				LivingEntity le = (LivingEntity)entity;
				if (!Utils.isLivingEntityNPC(le))
					continue;
				
				try {
					ISoliniaLivingEntity solle = SoliniaLivingEntityAdapter.Adapt(le);
					if (completedNpcsIds.contains(solle.getNpcid()))
						continue;
					
					completedNpcsIds.add(solle.getNpcid());
					solle.doRandomChat();
					
				} catch (CoreStateInitException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public void doNPCCheckForEnemies() {
		List<UUID> completedLivingEntities = new ArrayList<UUID>();
		List<UUID> entitiesNearPlayers = new ArrayList<UUID>();
		List<UUID> foundInvalidLivingEntity = new ArrayList<UUID>();
		
		for(Player player : Bukkit.getOnlinePlayers())
		{
			for(Entity entity : player.getNearbyEntities(25, 25, 25))
			{
				try
				{
					if (entity instanceof Player)
						continue;
					
					if (entity instanceof Boat)
					{
						Utils.despawnBoatIfNotNearWater((Boat)entity);
					}
					
					if (!(entity instanceof LivingEntity))
						continue;
					
					LivingEntity le = (LivingEntity)entity;
					if (!Utils.isLivingEntityNPC(le))
					{
						// all skeletons should be npcs
						// probably left around from the reboot and lost their tag?
						// get rid of them..
						if (le instanceof Skeleton)
							if (!foundInvalidLivingEntity.contains(le.getUniqueId()))
								foundInvalidLivingEntity.add(le.getUniqueId());
						
						continue;
					}
					
					if(!Utils.ValidatePet(le))
					{
						continue;
					}
					
					if (!entitiesNearPlayers.contains(le.getUniqueId()))
						entitiesNearPlayers.add(le.getUniqueId());
					
					try {
						ISoliniaLivingEntity solle = SoliniaLivingEntityAdapter.Adapt(le);
						if (completedLivingEntities.contains(le.getUniqueId()))
							continue;
						
						completedLivingEntities.add(le.getUniqueId());
						solle.doCheckForEnemies();
						
					} catch (CoreStateInitException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		
		// This cleans up mobs that have 'lost' their NPC identity
		for(UUID invalidEntity : foundInvalidLivingEntity)
		{
			try
			{
				Entity ent = Bukkit.getEntity(invalidEntity);
				if (ent instanceof Animals || ent instanceof Vehicle)
					continue;
				
				if (ent != null)
					Utils.RemoveEntity(ent,"doNPCCheckForEnemies");
			} catch (Exception e)
			{
				
			}
		}
		
		// Clear and reset all entities that are not near players
		Utils.ClearHateAndResetNpcsNotInList(entitiesNearPlayers);
	}
	
	@Override
	public void doNPCSpellCast() {
		List<Integer> completedNpcsIds = new ArrayList<Integer>();
		for(Player player : Bukkit.getOnlinePlayers())
		{
			for(Entity entityThatWillCast : player.getNearbyEntities(25, 25, 25))
			{
				if (entityThatWillCast instanceof Player)
					continue;
				
				if (!(entityThatWillCast instanceof LivingEntity))
					continue;
				
				LivingEntity livingEntityThatWillCast = (LivingEntity)entityThatWillCast;
				DebugUtils.DebugLog("SoliniaLivingEntity", "doNPCSpellCast", livingEntityThatWillCast, "Start doNPCSpellCast");
				
				if (!(entityThatWillCast instanceof Creature))
				{
					DebugUtils.DebugLog("SoliniaLivingEntity", "doNPCSpellCast", livingEntityThatWillCast, "Not a creature");
					continue;
				}
				
				if(entityThatWillCast.isDead())
				{
					DebugUtils.DebugLog("SoliniaLivingEntity", "doNPCSpellCast", livingEntityThatWillCast, "Im dead");
					continue;
				}
				
				Creature creatureThatWillCast = (Creature)entityThatWillCast;
				if (creatureThatWillCast.getTarget() == null)
				{
					DebugUtils.DebugLog("SoliniaLivingEntity", "doNPCSpellCast", livingEntityThatWillCast, "I have no target");
					continue;
				}
				
				if (!Utils.isLivingEntityNPC(livingEntityThatWillCast))
				{
					DebugUtils.DebugLog("SoliniaLivingEntity", "doNPCSpellCast", livingEntityThatWillCast, "I am not an NPC");
					continue;
				}
				
				try {
					ISoliniaLivingEntity solLivingEntityThatWillCast = SoliniaLivingEntityAdapter.Adapt(livingEntityThatWillCast);
					if (completedNpcsIds.contains(solLivingEntityThatWillCast.getNpcid()))
						continue;
					
					completedNpcsIds.add(solLivingEntityThatWillCast.getNpcid());
					
					ISoliniaLivingEntity solCreatureThatWillCastsTarget = SoliniaLivingEntityAdapter.Adapt(creatureThatWillCast.getTarget());
					if (RaycastUtils.isEntityInLineOfSight(solLivingEntityThatWillCast, solCreatureThatWillCastsTarget, true))
						solLivingEntityThatWillCast.doSpellCast(plugin, creatureThatWillCast.getTarget());
					
				} catch (CoreStateInitException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public Integer getNPCMana(LivingEntity bukkitLivingEntity, ISoliniaNPC npc) {
		if (bukkitLivingEntity instanceof Player)
			return 0;
		
		if (entityManaLevels.get(bukkitLivingEntity.getUniqueId()) == null)
		{
			try
			{
				ISoliniaLivingEntity soliniaLivingEntity = SoliniaLivingEntityAdapter.Adapt(bukkitLivingEntity);
				entityManaLevels.put(bukkitLivingEntity.getUniqueId(), soliniaLivingEntity.getMaxMP());
			} catch (CoreStateInitException e)
			{
				entityManaLevels.put(bukkitLivingEntity.getUniqueId(), 1);
			}
		} else {
			
		}
		
		return entityManaLevels.get(bukkitLivingEntity.getUniqueId());
	}
	
	@Override
	public void setNPCMana(LivingEntity bukkitLivingEntity, ISoliniaNPC npc, int amount) {
		if (bukkitLivingEntity instanceof Player)
			return;
		
		if (amount < 0)
			amount = 0;
		
		try
		{
			ISoliniaLivingEntity solLivingEntity = SoliniaLivingEntityAdapter.Adapt(bukkitLivingEntity);
			if (amount > solLivingEntity.getMaxMP())
				amount = solLivingEntity.getMaxMP();
		} catch (CoreStateInitException e)
		{
			
		}
		
		entityManaLevels.put(bukkitLivingEntity.getUniqueId(), amount);
	}

	@Override
	public void addMezzed(LivingEntity livingEntity, Timestamp expiretimestamp) {
		this.entityMezzed.put(livingEntity.getUniqueId(), expiretimestamp);
	}
	
	@Override
	public void addStunned(LivingEntity livingEntity, Timestamp expiretimestamp) {
		this.entityStunned.put(livingEntity.getUniqueId(), expiretimestamp);
	}
	
	@Override
	public void addEntitySpellCooldown(LivingEntity livingEntity, int spellId, Timestamp expiretimestamp)
	{
		this.entitySpellCooldown.put(livingEntity.getUniqueId()+"-"+spellId, expiretimestamp);
	}
	
	@Override
	public Timestamp getEntitySpellCooldown(LivingEntity livingEntity, int spellId)
	{
		return this.entitySpellCooldown.get(livingEntity.getUniqueId()+"-"+spellId);
	}
	
	@Override
	public void removeMezzed(LivingEntity livingEntity) {
		this.entityMezzed.remove(livingEntity.getUniqueId());
	}
	
	@Override
	public void removeStunned(LivingEntity livingEntity) {
		this.entityStunned.remove(livingEntity.getUniqueId());
	}

	@Override
	public Timestamp getMezzed(LivingEntity livingEntity) {
		LocalDateTime datetime = LocalDateTime.now();
		Timestamp nowtimestamp = Timestamp.valueOf(datetime);
		Timestamp expiretimestamp = this.entityMezzed.get(livingEntity.getUniqueId());

		if (expiretimestamp != null)
		if (nowtimestamp.after(expiretimestamp))
		{
			entityMezzed.remove(livingEntity.getUniqueId());
		}
		
		return entityMezzed.get(livingEntity.getUniqueId());
	}
	
	@Override
	public Timestamp getStunned(LivingEntity livingEntity) {
		LocalDateTime datetime = LocalDateTime.now();
		Timestamp nowtimestamp = Timestamp.valueOf(datetime);
		Timestamp expiretimestamp = this.entityStunned.get(livingEntity.getUniqueId());

		if (expiretimestamp != null)
		if (nowtimestamp.after(expiretimestamp))
		{
			entityStunned.remove(livingEntity.getUniqueId());
		}
		
		return entityStunned.get(livingEntity.getUniqueId());
	}
	
	@Override
	public LivingEntity getPet(UUID ownerUuid) {
		UUID entityuuid = this.petownerdata.get(ownerUuid);
		if (this.petownerdata.get(ownerUuid) == null)
			return null;
		
		LivingEntity entity = (LivingEntity)Bukkit.getEntity(entityuuid);
		if (entity != null)
		{
			if(!Utils.ValidatePet(entity))
			{
				this.petownerdata.remove(ownerUuid);
				if (Bukkit.getEntity(ownerUuid) != null)
				{
					Bukkit.getEntity(ownerUuid).sendMessage("Your pet was broken, please tell Mixxit (getPet)");
				}
				return null;
			}
		}
		
		entity = (LivingEntity)Bukkit.getEntity(entityuuid);
		if (entity != null)
		{
			return entity;
		}
		
		return null;
	}
	
	@Override
	public void removePet(final UUID petOwnerUUID, final boolean kill) {

		if (StateManager.getInstance().getPlugin().isEnabled())
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(StateManager.getInstance().getPlugin(),
				new Runnable() {
					public void run() {
						try
						{
							UUID entityuuid = StateManager.getInstance().getEntityManager().getPetOwnerData().get(petOwnerUUID);
							if (StateManager.getInstance().getEntityManager().getPetOwnerData().get(petOwnerUUID) == null)
								return;
							
							LivingEntity entity = (LivingEntity)Bukkit.getEntity(entityuuid);
							if (entity != null)
							{			
								if (entity instanceof LivingEntity)
								{
									// Remove MM pet
									try
									{
									ISoliniaLivingEntity solLivingEntity = SoliniaLivingEntityAdapter.Adapt((LivingEntity)entity);
									// Remove charm first else it will just reapply
									if (solLivingEntity.hasSpellEffectType(SpellEffectType.Charm))
									{
										StateManager.getInstance().getEntityManager().clearEntityEffectsOfType((LivingEntity)entity, SpellEffectType.Charm, true, false);
									}
									
									if (solLivingEntity != null && solLivingEntity.getActiveMob() != null)
									{
										// effectively remove owner
										//solLivingEntity.getActiveMob().setOwner(UUID.randomUUID());
										solLivingEntity.getActiveMob().removeOwner();
										solLivingEntity.getActiveMob().resetTarget();
									}
									} catch (CoreStateInitException e)
									{
										
									}
								}
								
								if (kill == true)
								{
									System.out.println("Killing pet " + entity.getName());
									Utils.RemoveEntity(entity,"KILLPET");
								}
							}
								
							StateManager.getInstance().getEntityManager().getPetOwnerData().remove(petOwnerUUID);
							Entity owner = Bukkit.getEntity(petOwnerUUID);
							if (owner != null)
								owner.sendMessage("You have lost your pet");
						} catch (CoreStateInitException e)
						{
							
						}
					}
		},20L);
		
		
		
	}

	@Override
	public List<LivingEntity> getAllWorldPets() {
		List<LivingEntity> pets = new ArrayList<LivingEntity>();

		for (Map.Entry<UUID, UUID> entry : petownerdata.entrySet()) {
			// UUID key = entry.getKey();
			if (entry.getValue() == null)
				continue;
			
			LivingEntity entity = (LivingEntity) Bukkit.getEntity(entry.getValue());
			if (entity != null)
				pets.add(entity);
		}

		return pets;
	}
	
	@Override
	public List<UUID> getAllWorldPetUUIDs() {
		List<UUID> pets = new ArrayList<UUID>();

		for (Map.Entry<UUID, UUID> entry : petownerdata.entrySet()) {
			// UUID key = entry.getKey();
			if (entry.getValue() == null)
				continue;
			
			LivingEntity entity = (LivingEntity) Bukkit.getEntity(entry.getValue());
			if (entity != null)
				pets.add(entity.getUniqueId());
		}

		return pets;
	}
	
	@Override
	public LivingEntity SpawnPet(Player owner, ISoliniaSpell spell)
	{
		if (owner.isDead())
			return null;
		
		try
		{
			LivingEntity pet = StateManager.getInstance().getEntityManager().getPet(owner.getUniqueId());
			if (pet != null)
			{
				ISoliniaLivingEntity solLivingEntity = SoliniaLivingEntityAdapter.Adapt(pet);
				StateManager.getInstance().getEntityManager().removePet(owner.getUniqueId(), !solLivingEntity.isCharmed());
			}
			
			ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getPetNPCByName(spell.getTeleportZone());
			
			if (npc == null)
				return null;

			if (npc.isCorePet() == false)
				return null;

			//
			// PET FOCUS ITEM
			// If owner is wearing a focus item at this point, and it is valid then we should record the mob is now
			// part of this focus
			
			LivingEntity spawnedMob = (LivingEntity)MythicMobs.inst().getAPIHelper().spawnMythicMob("NPCID_" + npc.getId(), owner.getLocation());
			ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(owner);

			int petFocus = solplayer.getPetFocus(npc);
			if (petFocus > 0)
			{
				StateManager.getInstance().getEntityManager().setPetFocus(spawnedMob.getUniqueId(),petFocus);
				solplayer.getBukkitPlayer().sendMessage("Your pet focus shimmers with a bright light");
				
			}
			
			ISoliniaLivingEntity solPet = SoliniaLivingEntityAdapter.Adapt((LivingEntity)spawnedMob);
			StateManager.getInstance().getEntityManager().setPet(owner.getUniqueId(),spawnedMob);
			spawnedMob.setCustomName(solplayer.getForename() + "s_Pet");
			spawnedMob.setCustomNameVisible(true);
			spawnedMob.setCanPickupItems(false);
			spawnedMob.setRemoveWhenFarAway(false);
			
			double maxHp = solPet.getMaxHP();
			if (npc.getForcedMaxHp() > 0)
			{
				maxHp = (double)npc.getForcedMaxHp();
			}
			
			AttributeInstance healthAttribute = spawnedMob.getAttribute(Attribute.GENERIC_MAX_HEALTH);
			healthAttribute.setBaseValue(maxHp);
			
			if (!spawnedMob.isDead())
				spawnedMob.setHealth(maxHp);
			
			net.minecraft.server.v1_14_R1.EntityInsentient entityhandle = (net.minecraft.server.v1_14_R1.EntityInsentient) ((org.bukkit.craftbukkit.v1_14_R1.entity.CraftLivingEntity) spawnedMob).getHandle();
			entityhandle.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue((double)npc.getBaseDamage());
			entityhandle.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue((double)0.4D);
			owner.sendMessage("New Pet spawned with HP: " + spawnedMob.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() + " and " + npc.getBaseDamage() + " dmg");
			

			TargetedDisguise mob = new MobDisguise(DisguiseType.WOLF);
			
			switch(npc.getMctype().toUpperCase())
			{
				case "WOLF":
					mob = new MobDisguise(DisguiseType.WOLF);
					break;
				case "SQUID": // Water Pet
					mob = new PlayerDisguise(spawnedMob.getCustomName(), "2hot2handle");
					break;
				case "PARROT": // Air Pet
					mob = new PlayerDisguise(spawnedMob.getCustomName(), "BimEinsMaedchen");
					break;
				case "SKELETON":
					mob = new MobDisguise(DisguiseType.SKELETON);
					break;
				case "BLAZE": // Fire Pet
					mob = new PlayerDisguise(spawnedMob.getCustomName(),  "xPan_Mks");
					break;
				case "IRON_GOLEM": // Earth Pet
					mob = new PlayerDisguise(spawnedMob.getCustomName(), "PremiumPilz");
					break;
				case "GUARDIAN":
					mob = new PlayerDisguise(spawnedMob.getCustomName(), "yamiaka");
					break;
				default:
					mob = new MobDisguise(DisguiseType.WOLF);
					break;
			}
						
			DisguiseAPI.disguiseEntity(spawnedMob, mob);
			return spawnedMob;
		} catch (CoreStateInitException e)
		{
			return null;
		} catch (InvalidMobTypeException e) {
			return null;
		}
	}

	@Override
	public void removeAllPets() {
		for (Map.Entry<UUID, UUID> entry : petownerdata.entrySet()) {
			UUID key = entry.getKey();
			LivingEntity livingEntityPet = (LivingEntity)Bukkit.getEntity(entry.getValue());
			if (livingEntityPet != null)
			{
				System.out.println("Cleaning Up pet: " + livingEntityPet.getName());
				Utils.RemoveEntity(livingEntityPet,"removeAllPets");
			}
			this.removePet(key, true);
		}
	}

	@Override
	public LivingEntity setPet(UUID petOwnerUuid, LivingEntity entity) {
		try
		{
			if (Bukkit.getEntity(petOwnerUuid) == null)
				return null;
			
			if (getPet(petOwnerUuid) != null) {
				ISoliniaLivingEntity solPetEntity = SoliniaLivingEntityAdapter.Adapt(getPet(petOwnerUuid));
				removePet(petOwnerUuid, !solPetEntity.isCharmed());
			}
			
			ISoliniaLivingEntity solEntity = SoliniaLivingEntityAdapter.Adapt(entity);
			if (solEntity != null && solEntity.getActiveMob() != null)
			{
				solEntity.getActiveMob().setOwner(petOwnerUuid);
				solEntity.clearHateList();
				solEntity.setAttackTarget(null);
			
				Entity petOwner = Bukkit.getEntity(petOwnerUuid);
				if (petOwner != null)
					petOwner.sendMessage("You have a new pet!");
				
				this.petownerdata.put(petOwnerUuid, entity.getUniqueId());
			}
		} catch (CoreStateInitException e)
		{
		
		}
		return entity;
	}

	@Override
	public void clearEntityEffects(UUID uniqueId) {
		if (entitySpells.get(uniqueId) != null)
			removeSpellEffects(uniqueId, false, false);
		
		try
		{
			if (Bukkit.getEntity(uniqueId) != null && Bukkit.getEntity(uniqueId) instanceof Player)
				SoliniaPlayerAdapter.Adapt((Player)Bukkit.getEntity(uniqueId)).sendEffects();
		} catch (CoreStateInitException e)
		{
			e.printStackTrace();
		}

	}
	
	@Override
	public SoliniaActiveSpell getFirstActiveSpellOfSpellEffectType(LivingEntity livingEntity, SpellEffectType type) {
		if (entitySpells.get(livingEntity.getUniqueId()) == null)
			return null;
		
		return entitySpells.get(livingEntity.getUniqueId()).getFirstActiveSpellOfEffectType(type);
	}

	@Override
	public void clearEntityFirstEffectOfType(LivingEntity livingEntity, SpellEffectType type, boolean forceDoNotLoopBardSpell, boolean removeNonCombatEffects) {
		if (entitySpells.get(livingEntity.getUniqueId()) == null)
			return;
		
		entitySpells.get(livingEntity.getUniqueId()).removeFirstSpellOfEffectType(plugin, type, forceDoNotLoopBardSpell, removeNonCombatEffects);
		
		try
		{
			if (livingEntity != null && livingEntity instanceof Player)
				SoliniaPlayerAdapter.Adapt((Player)livingEntity).sendEffects();
		} catch (CoreStateInitException e)
		{
			e.printStackTrace();
		}

	}
	
	@Override
	public void clearEntityEffectsOfType(LivingEntity livingEntity, SpellEffectType type, boolean forceDoNotLoopBardSpell, boolean removeNonCombatEffects) {
		if (entitySpells.get(livingEntity.getUniqueId()) == null)
			return;
		
		entitySpells.get(livingEntity.getUniqueId()).removeSpellsOfEffectType(plugin, type, forceDoNotLoopBardSpell, removeNonCombatEffects);
		
		try
		{
			if (livingEntity != null && livingEntity instanceof Player)
				SoliniaPlayerAdapter.Adapt((Player)livingEntity).sendEffects();
		} catch (CoreStateInitException e)
		{
			e.printStackTrace();
		}

	}
	
	@Override
	public void clearEntityFirstEffect(LivingEntity livingEntity) {
		if (entitySpells.get(livingEntity.getUniqueId()) == null)
			return;
		
		entitySpells.get(livingEntity.getUniqueId()).removeFirstSpell(plugin, false);
		
		try
		{
			if (livingEntity != null && livingEntity instanceof Player)
				SoliniaPlayerAdapter.Adapt((Player)livingEntity).sendEffects();
		} catch (CoreStateInitException e)
		{
			e.printStackTrace();
		}

	}
	
	@Override
	public List<ISoliniaNPCMerchantEntry> getNPCMerchantCombinedEntries(ISoliniaNPCMerchant merchant) {
		List<ISoliniaNPCMerchantEntry> combinedEntries = new ArrayList<ISoliniaNPCMerchantEntry>();
		if (merchant == null)
			return combinedEntries;
			
		try
		{
			List<Integer> existingItemIds = new ArrayList<Integer>();
			
			// Prevents items from being listed that dont exist as an item or are already in the list
			if (merchant.getEntries().size() > 0)
			for (ISoliniaNPCMerchantEntry entry : merchant.getEntries())
			{
				if (existingItemIds.contains(entry.getItemid()))
					continue;
				
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(entry.getItemid());
				if (item == null)
					continue;
				
				existingItemIds.add(entry.getItemid());
				combinedEntries.add(entry);
			}
	
			//List<ISoliniaNPCMerchantEntry> tempItems = getTemporaryMerchantItems(npc);
			//if (tempItems.size() > 0)
			//	combinedEntries.addAll(tempItems);
		} catch (CoreStateInitException e)
		{
			return new ArrayList<ISoliniaNPCMerchantEntry>();
		}
		return combinedEntries;
		
	}
	
	@Override
	public void doNPCSummon() {
		List<Integer> completedNpcsIds = new ArrayList<Integer>();
		for(Player player : Bukkit.getOnlinePlayers())
		{
			for(Entity entityThatWillSummon : player.getNearbyEntities(50, 50, 50))
			{
				if (entityThatWillSummon instanceof Player)
					continue;
				
				if (!(entityThatWillSummon instanceof LivingEntity))
					continue;
				
				LivingEntity livingEntityThatWillSummon = (LivingEntity)entityThatWillSummon;
				
				if (!(entityThatWillSummon instanceof Creature))
					continue;
				
				if(entityThatWillSummon.isDead())
					continue;
				
				Creature creatureThatWillSummon = (Creature)entityThatWillSummon;
				if (creatureThatWillSummon.getTarget() == null)
					continue;
				
				if (!Utils.isLivingEntityNPC(livingEntityThatWillSummon))
					continue;
				
				try {
					ISoliniaLivingEntity solLivingEntityThatWillSummon = SoliniaLivingEntityAdapter.Adapt(livingEntityThatWillSummon);
					if (completedNpcsIds.contains(solLivingEntityThatWillSummon.getNpcid()))
						continue;
					
					completedNpcsIds.add(solLivingEntityThatWillSummon.getNpcid());
					
					solLivingEntityThatWillSummon.doSummon(creatureThatWillSummon.getTarget());
					
				} catch (CoreStateInitException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public void doNPCTeleportAttack() {
		List<Integer> completedNpcsIds = new ArrayList<Integer>();
		for(Player player : Bukkit.getOnlinePlayers())
		{
			for(Entity entityThatWillTeleportAttack : player.getNearbyEntities(25, 25, 25))
			{
				if (entityThatWillTeleportAttack instanceof Player)
					continue;
				
				if (!(entityThatWillTeleportAttack instanceof LivingEntity))
					continue;
				
				LivingEntity livingEntityThatWillTeleportAttack = (LivingEntity)entityThatWillTeleportAttack;
				
				if (!(entityThatWillTeleportAttack instanceof Creature))
					continue;
				
				if(entityThatWillTeleportAttack.isDead())
					continue;
				
				Creature creatureThatWillTeleportAttack = (Creature)entityThatWillTeleportAttack;
				if (creatureThatWillTeleportAttack.getTarget() == null)
					continue;
				
				if (!Utils.isLivingEntityNPC(livingEntityThatWillTeleportAttack))
					continue;
				
				try {
					ISoliniaLivingEntity solLivingEntityThatWillTeleportAttack = SoliniaLivingEntityAdapter.Adapt(livingEntityThatWillTeleportAttack);
					if (completedNpcsIds.contains(solLivingEntityThatWillTeleportAttack.getNpcid()))
						continue;
					
					completedNpcsIds.add(solLivingEntityThatWillTeleportAttack.getNpcid());
					
					solLivingEntityThatWillTeleportAttack.doSummon(creatureThatWillTeleportAttack.getTarget());
					
				} catch (CoreStateInitException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public int getAIEngagedBeneficialSelfChance() {
		return 50;
	}

	@Override
	public int getAIEngagedBeneficialOtherChance() {
		return 25;
	}

	@Override
	public int getAIEngagedDetrimentalChance() {
		return 75;
	}

	@Override
	public int getAIBeneficialBuffSpellRange() {
		// TODO Auto-generated method stub
		return 10;
	}

	@Override
	public Timestamp getDontSpellTypeMeBefore(LivingEntity bukkitLivingEntity, int spellType) 
	{
		Timestamp timestamp = null;
		
		if (spellType == SpellType.Heal)
			timestamp = dontHealMe.get(bukkitLivingEntity.getUniqueId());
		
		if (spellType == SpellType.Root)
			timestamp = dontRootMe.get(bukkitLivingEntity.getUniqueId());
		
		if (spellType == SpellType.Buff)
			timestamp = dontBuffMe.get(bukkitLivingEntity.getUniqueId());
		
		if (spellType == SpellType.Snare)
			timestamp = dontSnareMe.get(bukkitLivingEntity.getUniqueId());
		
		if (spellType == SpellType.DOT)
			timestamp = dontDotMe.get(bukkitLivingEntity.getUniqueId());
		
		if (timestamp != null)
		{
			return timestamp;
		}
		else
		{
			LocalDateTime datetime = LocalDateTime.now();
			Timestamp nowtimestamp = Timestamp.valueOf(datetime);
			
			if (spellType == SpellType.Heal)
				dontHealMe.put(bukkitLivingEntity.getUniqueId(),nowtimestamp);
			
			if (spellType == SpellType.Root)
				dontRootMe.put(bukkitLivingEntity.getUniqueId(),nowtimestamp);
			
			if (spellType == SpellType.Buff)
				dontBuffMe.put(bukkitLivingEntity.getUniqueId(),nowtimestamp);
			
			if (spellType == SpellType.Snare)
				dontSnareMe.put(bukkitLivingEntity.getUniqueId(),nowtimestamp);
			
			if (spellType == SpellType.DOT)
				dontDotMe.put(bukkitLivingEntity.getUniqueId(),nowtimestamp);
				
			return nowtimestamp;
		}
	}

	@Override
	public void setDontSpellTypeMeBefore(LivingEntity bukkitLivingEntity, int spellType, Timestamp timestamp) {
		if (spellType == SpellType.Heal)
			dontHealMe.put(bukkitLivingEntity.getUniqueId(),timestamp);
		
		if (spellType == SpellType.Root)
			dontRootMe.put(bukkitLivingEntity.getUniqueId(),timestamp);
		
		if (spellType == SpellType.Buff)
			dontBuffMe.put(bukkitLivingEntity.getUniqueId(),timestamp);
		
		if (spellType == SpellType.Snare)
			dontSnareMe.put(bukkitLivingEntity.getUniqueId(),timestamp);
		
		if (spellType == SpellType.DOT)
			dontDotMe.put(bukkitLivingEntity.getUniqueId(),timestamp);

	}


	@Override
	public UniversalMerchant getUniversalMerchant(UUID universalMerchant) {
		// TODO Auto-generated method stub
		return this.universalMerchant.get(universalMerchant);
	}

	@Override
	public ConcurrentHashMap<UUID, Boolean> getPlayerInTerritory() {
		return playerInTerritory;
	}

	@Override
	public void setPlayerInTerritory(ConcurrentHashMap<UUID, Boolean> playerInTerritory) {
		this.playerInTerritory = playerInTerritory;
	}

	@Override
	public ConcurrentHashMap<UUID, Boolean> getPlayerSetMain() {
		return playerSetMain;
	}

	@Override
	public void setPlayerSetMain(ConcurrentHashMap<UUID, Boolean> playerSetMain) {
		this.playerSetMain = playerSetMain;
	}

	@Override
	public ConcurrentHashMap<UUID, UUID> getEntityTargets() {
		return entityTargets;
	}

	@Override
	public void setEntityTargets(ConcurrentHashMap<UUID, UUID> entityTarget) {
		this.entityTargets = entityTarget;
	}
	
	@Override
	public UUID getFollowing(UUID entityUuid)
	{
		if (entityUuid == null)
			return null;
		
		UUID followingSomeone = this.following.get(entityUuid);
		if (followingSomeone == null)
		{
			return null;
		}
		
		return this.following.get(entityUuid);
	}
	
	@Override
	public Boolean isFollowing(UUID entityUuid)
	{
		if (entityUuid == null)
			return false;
		
		UUID followingSomeone = this.following.get(entityUuid);
		if (followingSomeone == null)
		{
			return false;
		}
		
		return true;
	}
	
	@Override
	public void setFollowing(UUID entityUuid, UUID following)
	{
		if (entityUuid == null)
			return;
		
		if (following == null)
		{
			this.following.remove(entityUuid);
			return;
		}
		
		this.following.put(entityUuid, following);
	}
	
	public ConcurrentHashMap<UUID, Boolean> getFeignedDeath() {
		return feignedDeath;
	}

	public void setFeignedDeath(ConcurrentHashMap<UUID, Boolean> feignedDeath) {
		this.feignedDeath = feignedDeath;
	}
	
	@Override
	public boolean isFeignedDeath(UUID entityUuid)
	{
		Boolean feignedDeath = getFeignedDeath().get(entityUuid);
		if (feignedDeath == null)
		{
			return false;
		}
		
		return getFeignedDeath().get(entityUuid);
	}
	
	@Override
	public void setFeignedDeath(UUID entityUuid, boolean feigned)
	{
		getFeignedDeath().put(entityUuid, feigned);
	}

	public ConcurrentHashMap<UUID, CastingSpell> getEntitySpellCasting() {
		return entitySpellCasting;
	}

	public void setEntitySpellCasting(ConcurrentHashMap<UUID, CastingSpell> entitySpellCasting) {
		this.entitySpellCasting = entitySpellCasting;
	}
	
	@Override
	public void startTracking(LivingEntity livingEntity, Location location)
	{
		if (livingEntity instanceof Player)
		{
			livingEntity.sendMessage("You start tracking");
			entityTracking.put(livingEntity.getUniqueId(), location);
		}
	}
	
	@Override
	public Location getEntityTracking(LivingEntity livingEntity)
	{
		if (livingEntity instanceof Player)
		{
			return entityTracking.get(livingEntity.getUniqueId());
		}
		
		return null;
	}
	
	@Override
	public void stopTracking(UUID entityUUID) {
		if (entityTracking.get(entityUUID) != null)
		{
			Entity entity = Bukkit.getEntity(entityUUID);
			if (entity instanceof Player && (!((Player)entity).isDead()))
			{
				Player player = (Player)entity;
				player.sendMessage("You finish tracking");
				entityTracking.remove(entityUUID);
			}
		}
	}
	
	@Override
	public void startCasting(LivingEntity livingEntity, CastingSpell castingSpell) {
		interruptCasting(livingEntity);
		try {
			ISoliniaLivingEntity solLivingEntity = SoliniaLivingEntityAdapter.Adapt(livingEntity);
			if (solLivingEntity == null)
				return;

			solLivingEntity.BreakInvis();

			if (livingEntity instanceof Player) {

				// Move fizzle check to before casting
				ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player) livingEntity);
				if (solPlayer != null && !solPlayer.checkDoesntFizzle(castingSpell.getSpell())) {
					solPlayer.emote("* " + solPlayer.getFullName() + "'s spell fizzles", false, false);
					solPlayer.reducePlayerMana(
							castingSpell.getSpell().getActSpellCost(solPlayer.getSoliniaLivingEntity()));
					return;
				}

				Utils.SendHint(livingEntity, HINT.BEGIN_ABILITY, castingSpell.getSpell().getName(), false);

				playSpellCastingSoundEffect(livingEntity, castingSpell.getSpell());
				playSpellCastingSpellEffect(livingEntity, castingSpell.getSpell());

				entitySpellCasting.put(livingEntity.getUniqueId(), castingSpell);

			}
		} catch (CoreStateInitException e) {

		}
	}
	
	private void playSpellCastingSpellEffect(LivingEntity livingEntity, ISoliniaSpell spell) {
		if (livingEntity == null)
			return;
		if (!(livingEntity instanceof Player))
			return;
		
		if (spell == null)
			return;

		if (spell.isCombatSkill())
			return;
			
		SpecialEffectUtils.playCustomBallEffect(livingEntity, Particle.CRIT_MAGIC, Color.AQUA);
	}

	private void playSpellCastingSoundEffect(LivingEntity livingEntity, ISoliniaSpell spell) {
		if (livingEntity == null)
			return;
		if (!(livingEntity instanceof Player))
			return;
		
		if (spell == null)
			return;

		if (spell.isCombatSkill())
			return;
			
		String soundName = getSpellEffectIndexCastingSound(spell.getSpellAffectIndex());
		if (soundName == null)
			return;
		
		livingEntity.getWorld().playSound(livingEntity.getLocation(), soundName, 0.7F, 1);
	}
	
	@Override
	public void playSpellFinishedSoundEffect(LivingEntity targetEntity, ISoliniaSpell spell) {
		if (targetEntity == null)
			return;
		
		if (spell == null)
			return;
		
		if (spell.isCombatSkill())
			return;
		
		String soundName = getSpellEffectIndexFinishedSound(spell.getSpellAffectIndex());
		if (soundName == null)
			return;
		
		targetEntity.getWorld().playSound(targetEntity.getLocation(), soundName, 0.7F, 1);
	}
	
	public String getSpellEffectIndexFinishedSound(int spellAffectIndex) {
		if (spellAffectIndex < 0)
			return null;
		
		if (Utils.getSpellEffectIndex(spellAffectIndex) == null)
			return null;
		
		switch(Utils.getSpellEffectIndex(spellAffectIndex))
		{
			case AC_Buff:
				return "solinia3ui:spelgdht"; // done
			case Calm:
				return "solinia3ui:spelgdht"; // done
			case Vanish:
				return "solinia3ui:spelcast"; // done
			case Sight:
				return "solinia3ui:spelcast"; // done
			case Dispell_Sight:
				return "solinia3ui:spelcast"; // done
			case Stat_Buff:
				return "solinia3ui:spelhit3"; // done
			case Heal_Cure:
				return "solinia3ui:spelgdht"; // done
			case Direct_Damage:
				return "solinia3ui:spelhit1"; // done
			case Gravity_Fling:
				return "solinia3ui:spelhit2"; // done
			case Summon:
				return "solinia3ui:spell5"; // done
			case Combat_Slow:
				return "solinia3ui:spell5"; // done
			case Weaken:
				return "solinia3ui:spell5"; // done
			case Blind_Poison:
				return "solinia3ui:spell5"; // done
			case Teleport:
				return "solinia3ui:spell4"; // done
			case Haste_Runspeed:
				return "solinia3ui:spelhit2";// done
			case Fire_DD:
				return "solinia3ui:spelhit4";// done
			case Mana_Regen_Resist_Song:
				return "solinia3ui:spelgdht";// done
			case Appearance:
				return "solinia3ui:spelhit2";// done
			case Memory_Blur:
				return "solinia3ui:spelgdht";// done
			case AE_Damage:
				return "solinia3ui:spelhit1";// done
			case Banish:
				break; // no sound
			case Calm_Song:
				break; // no sound
			case Cannibalize_Weapon_Proc:
				return "solinia3ui:spelhit2";// done
			case Cold_AE:
				return "solinia3ui:spelhit4";// done
			case Cold_DD:
				return "solinia3ui:spell2";// done
			case Combat_Buff_Song:
				break;// no sound
			case Convert_Vitals:
				break;
			case Damage_Shield:
				return "solinia3ui:spelcast";// done
			case Direct_Damage_Song:
				break; // no sound
			case Enchanter_Pet:
				break; // no sound
			case Fear:
				return "solinia3ui:spelhit3";// should really be lightning
			case Fire_AE:
				return "solinia3ui:spelhit4";// done
			case Lifetap_Over_Time:
				break;
			case Poison_Disease_AE:
				return "solinia3ui:spelhit3";// should really be lightning
			case Poison_Disease_DD:
				return "solinia3ui:spell3";// should really be lightning
			case Stun:
				return "solinia3ui:spelhit1";// should really be lightning
			case Suffocate:
				break;
			default:
				break;
		}
		
		return null;
	}

	public String getSpellEffectIndexCastingSound(int spellAffectIndex) {
		if (spellAffectIndex < 0)
			return null;
		
		if (Utils.getSpellEffectIndex(spellAffectIndex) == null)
			return null;

		switch(Utils.getSpellEffectIndex(spellAffectIndex))
		{
			case AC_Buff:
				return "solinia3ui:spelcast"; // done
			case Calm:
				return "solinia3ui:spell4"; // done
			case Vanish:
				return "solinia3ui:spell4"; // done
			case Sight:
				return "solinia3ui:spelcast"; // done
			case Dispell_Sight:
				return "solinia3ui:spelcast"; // done
			case Stat_Buff:
				return "solinia3ui:spell4"; // done
			case Heal_Cure:
				return "solinia3ui:spelcast"; // done
			case Direct_Damage:
				return "solinia3ui:spell3"; // done
			case Gravity_Fling:
				return "solinia3ui:spell4"; // done
			case Summon:
				return "solinia3ui:spelhit3";// done
			case Combat_Slow:
				return "solinia3ui:spelhit3";// done
			case Weaken:
				return "solinia3ui:spelhit3";// done
			case Blind_Poison:
				return "solinia3ui:spelhit3";// done
			case Teleport:
				return "solinia3ui:spelhit3";// done
			case Haste_Runspeed:
				return "solinia3ui:spell4";// done
			case Fire_DD:
				return "solinia3ui:spell3";// done
			case Enchanter_Pet:
				return "solinia3ui:spell1";// done
			case Mana_Regen_Resist_Song:
				return "solinia3ui:spell4";// done
			case Appearance:
				return "solinia3ui:spelhit3";// done
			case Memory_Blur:
				return "solinia3ui:spell4";// done
			case AE_Damage:
				return "solinia3ui:spell3";// done
			case Fire_AE:
				return "solinia3ui:spell2";// supposed to actually be lightning
			case Cold_AE:
				return "solinia3ui:spell3";// done
			case Cold_DD:
				return "solinia3ui:spell3";// done
			case Damage_Shield:
				return "solinia3ui:spell4";// done
			case Stun:
				return "solinia3ui:spell3";// done
			case Poison_Disease_AE:
				return "solinia3ui:spell5";// done
			case Poison_Disease_DD:
				return "solinia3ui:spell4";// done
			case Fear:
				return "solinia3ui:spell5";// done
			case Banish:
				return "solinia3ui:spelcast";// done
			case Cannibalize_Weapon_Proc:
				return "solinia3ui:spelhit3";// done
			case Direct_Damage_Song:
				break; // no sound
			default:
				return null;// done
		}
		
		return null;
	}
	
	@Override
	public void interruptCasting(LivingEntity livingEntity) {
		if (entitySpellCasting.get(livingEntity.getUniqueId()) != null)
		{
			if (livingEntity instanceof Player)
				livingEntity.sendMessage("Your casting was interrupted");
			entitySpellCasting.remove(livingEntity.getUniqueId());
		}
	}
	
	@Override
	public void finishCasting(UUID entityUUID) {
		if (entitySpellCasting.get(entityUUID) != null)
		{
			Entity entity = Bukkit.getEntity(entityUUID);
			if (entity instanceof Player && (!((Player)entity).isDead()))
			{
				Player player = (Player)entity;
				// Do casting animation
				Utils.SendHint(player, HINT.FINISH_ABILITY, "", false);
				try {
					ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
					solPlayer.castingComplete(entitySpellCasting.get(entityUUID));
				} catch (CoreStateInitException e) {
				}
				
			}

			entitySpellCasting.remove(entityUUID);
		}
	}

	@Override
	public CastingSpell getCasting(LivingEntity livingEntity) {
		return entitySpellCasting.get(livingEntity.getUniqueId());
	}

	@Override
	public boolean isCasting(LivingEntity livingEntity) {
		if (entitySpellCasting.get(livingEntity.getUniqueId()) != null)
		{
			return true;
		}
		return false;
	}

	@Override
	public void processCastingTimer() {
		for (CastingSpell castingSpell : entitySpellCasting.values())
		{
			Entity entity = Bukkit.getEntity(castingSpell.getLivingEntityUUID());
			
			if (entity == null)
			{
				finishCasting(castingSpell.getLivingEntityUUID());
			}
			
			if (entity != null)
			{
				if (entity.isDead())
				{
					finishCasting(castingSpell.getLivingEntityUUID());
				}
			}
			
			if (castingSpell.timeLeftMilliseconds > 0)
			{
				castingSpell.timeLeftMilliseconds = (castingSpell.timeLeftMilliseconds - 100);
			} else {
				finishCasting(castingSpell.getLivingEntityUUID());
			}
			
			if (entity != null)
			{
				if (entity instanceof Player && (!((Player)entity).isDead()))
				{
					PartyWindowUtils.UpdateWindow((Player)entity,false,false);
				}
			}
		}
	}

	@Override
	public EntityAutoAttack getEntityAutoAttack(LivingEntity livingEntity) {
		if (entityAutoAttack.get(livingEntity.getUniqueId()) == null)
		{
			entityAutoAttack.put(livingEntity.getUniqueId(), new EntityAutoAttack(livingEntity));
		} 
		
		return entityAutoAttack.get(livingEntity.getUniqueId());
	}

	@Override
	public void setEntityAutoAttack(LivingEntity livingEntity, boolean autoAttacking) {
		if (autoAttacking == false && livingEntity instanceof Creature)
			((Creature)livingEntity).setTarget(null);
		
		getEntityAutoAttack(livingEntity).setAutoAttacking(autoAttacking);
	}
	
	@Override
	public void toggleAutoAttack(Player player) {
		Boolean autoAttackState = getEntityAutoAttack(player).isAutoAttacking();
		
		if (!autoAttackState == false)
		{
			player.spigot().sendMessage(ChatMessageType.CHAT,
					new TextComponent(ChatColor.GRAY + "* You stop auto attacking"));
		} else {
			
			player.spigot().sendMessage(ChatMessageType.CHAT,
					new TextComponent(ChatColor.GRAY + "* You start auto attacking"));
		}
		
		setEntityAutoAttack(player, !autoAttackState);
	}

	@Override
	public ConcurrentHashMap<UUID, Timestamp> getLastCallForAssist() {
		return lastCallForAssist;
	}

	@Override
	public void setLastCallForAssist(UUID uuid, Timestamp lasttimestamp) {
		this.lastCallForAssist.put(uuid, lasttimestamp);
	}
	
	@Override
	public ConcurrentHashMap<UUID, Timestamp> getLastDoubleAttack() {
		return lastDoubleAttack;
	}

	@Override
	public void setLastDoubleAttack(UUID uuid, Timestamp lasttimestamp) {
		this.lastDoubleAttack.put(uuid, lasttimestamp);
	}
	
	@Override
	public ConcurrentHashMap<UUID, Timestamp> getLastDisarm() {
		return lastDisarm;
	}

	@Override
	public void setLastDisarm(UUID uuid, Timestamp lasttimestamp) {
		this.lastDisarm.put(uuid, lasttimestamp);
	}
	
	@Override
	public ConcurrentHashMap<UUID, Timestamp> getLastRiposte() {
		return lastRiposte;
	}

	@Override
	public void setLastRiposte(UUID uuid, Timestamp lasttimestamp) {
		this.lastRiposte.put(uuid, lasttimestamp);
	}
	
	@Override
	public ConcurrentHashMap<UUID, Timestamp> getLastMeleeAttack() {
		return lastMeleeAttack;
	}

	@Override
	public void setLastMeleeAttack(UUID uuid, Timestamp lasttimestamp) {
		this.lastMeleeAttack.put(uuid, lasttimestamp);
	}

	@Override
	public ConcurrentHashMap<UUID, Timestamp> getLastBindwound() {
		return this.lastBindwound;
	}

	@Override
	public void setLastBindwound(UUID uuid, Timestamp lasttimestamp) {
		this.lastBindwound.put(uuid, lasttimestamp);
	}

	
	@Override
	public void addToHateList(UUID entity, UUID provoker, int hate, boolean isYellForHelp) {
		if (hateList.get(entity) == null)
			hateList.put(entity, new ConcurrentHashMap<UUID, Tuple<Integer,Boolean>>());

		if (reverseHateList.get(provoker) == null)
			reverseHateList.put(provoker, new ConcurrentHashMap<UUID, Tuple<Integer,Boolean>>());
		
		if (hateList.get(entity).get(provoker) == null)
		{
			hateList.get(entity).put(provoker, new Tuple<Integer,Boolean>(hate,isYellForHelp));
			
			if (reverseHateList.get(provoker).get(entity) == null)
			{
				reverseHateList.get(provoker).put(entity, new Tuple<Integer,Boolean>(hate,isYellForHelp));
			}
			return;
		}
		
		Tuple<Integer,Boolean> newvalueHate = hateList.get(entity).get(provoker);
		Tuple<Integer,Boolean> newvalueReverseHate = reverseHateList.get(provoker).get(entity);
		
		if ((newvalueHate.a() + hate) > Integer.MAX_VALUE)
		{
			newvalueHate = new Tuple<Integer,Boolean>(Integer.MAX_VALUE,newvalueHate.b());
			newvalueReverseHate = new Tuple<Integer,Boolean>(Integer.MAX_VALUE,newvalueReverseHate.b());
		}
		else if ((newvalueHate.a() + hate) < 0)
		{
			newvalueHate = new Tuple<Integer,Boolean>(0,newvalueHate.b());
			newvalueReverseHate = new Tuple<Integer,Boolean>(0,newvalueReverseHate.b());
		}
		else
		{
			newvalueHate = new Tuple<Integer,Boolean>(newvalueHate.a()+hate,newvalueHate.b());
			newvalueReverseHate = new Tuple<Integer,Boolean>(newvalueReverseHate.a()+hate,newvalueReverseHate.b());
		}
		
		if (newvalueHate.a() == 0)
		{
			hateList.get(entity).remove(provoker);
			reverseHateList.get(provoker).remove(entity);
		} else {
			hateList.get(entity).put(provoker, newvalueHate);
			reverseHateList.get(provoker).put(entity, newvalueReverseHate);
		}
	}
	
	private ConcurrentHashMap<UUID, Tuple<Integer,Boolean>> getHateList(UUID entity)
	{
		if (hateList.get(entity) == null)
			hateList.put(entity, new ConcurrentHashMap<UUID, Tuple<Integer,Boolean>>());
		
		return hateList.get(entity);
	}
	
	private ConcurrentHashMap<UUID, Tuple<Integer,Boolean>> getReverseHateList(UUID entity)
	{
		if (reverseHateList.get(entity) == null)
			reverseHateList.put(entity, new ConcurrentHashMap<UUID, Tuple<Integer,Boolean>>());
		
		return reverseHateList.get(entity);
	}

	@Override
	public List<UUID> getActiveHateListUUIDs()
	{
		return Collections.list(hateList.keys());
	}
	
	@Override
	public Tuple<Integer,Boolean> getHateListEntry(UUID entity, UUID provoker)
	{
		if (hateList.get(entity) == null)
			hateList.put(entity, new ConcurrentHashMap<UUID, Tuple<Integer,Boolean>>());
		if (reverseHateList.get(provoker) == null)
			reverseHateList.put(provoker, new ConcurrentHashMap<UUID, Tuple<Integer,Boolean>>());
		
		if (hateList.get(entity).get(provoker) == null)
			return new Tuple<Integer,Boolean>(0,true);

		if (reverseHateList.get(provoker).get(entity) == null)
			return new Tuple<Integer,Boolean>(0,true);

		return hateList.get(entity).get(provoker);
	}

	@Override
	public void clearHateList(UUID entityUuid) {
		if (hateList.get(entityUuid) != null)
		{
			if (hateList.get(entityUuid).size() == 0)
				return;
			
			// clear reverse hate list in advance
			for(Entry<UUID, Tuple<Integer, Boolean>> entitesHateList : hateList.get(entityUuid).entrySet())
			{
				if (reverseHateList.get(entitesHateList.getKey()) == null)
					continue;
				
				if (reverseHateList.get(entitesHateList.getKey()).get(entityUuid) != null)
					reverseHateList.get(entitesHateList.getKey()).remove(entityUuid);
			}
		}
		
		hateList.put(entityUuid,  new ConcurrentHashMap<UUID, Tuple<Integer,Boolean>>());
		
		
		Entity entity = Bukkit.getEntity(entityUuid);
		if (entity == null)
			return;
		
		if (entity instanceof Creature)
		{	
			try {
				SoliniaLivingEntityAdapter.Adapt((Creature)entity).setAttackTarget(null);
			} catch (CoreStateInitException e) {
			}
		}
	}
	
	@Override
	public void removeFromHateList(UUID entityUuid, UUID target) {
		if (hateList.get(entityUuid) == null)
			return;
		
		if (hateList.get(entityUuid).size() == 0)
			return;
		
		if (hateList.get(entityUuid).get(target) == null)
			return;

		// clear reverse hate first
		if (reverseHateList.get(target).get(entityUuid) != null)
			reverseHateList.get(target).remove(entityUuid);

		hateList.get(entityUuid).remove(target);
		
		Entity entity = Bukkit.getEntity(entityUuid);
		if (entity == null)
			return;
		
		if (entity instanceof Creature)
		{	
			try {
				if (((Creature) entity).getTarget() != null && ((Creature) entity).getTarget().getUniqueId().equals(target))
					SoliniaLivingEntityAdapter.Adapt((Creature)entity).setAttackTarget(null);
			} catch (CoreStateInitException e) {
			}
		}
	}

	@Override
	public List<UUID> getFollowers(UUID uniqueId) {
		if (!this.following.containsValue(uniqueId))
			return new ArrayList<UUID>();
		
		// this could get laggy on onmove we should probably reverse the storage key and value
		List<UUID> followers = new ArrayList<UUID>();
		for(Entry<UUID, UUID> uuid : this.following.entrySet())
		{
			if (!uuid.getValue().equals(uniqueId))
				continue;
			
			followers.add(uuid.getKey());
		}
		
		return followers;
	}

	@Override
	public void doNPCYellForAssist() {
		List<Integer> completedNpcsIds = new ArrayList<Integer>();
		for(Player player : Bukkit.getOnlinePlayers())
		{
			for(Entity entity : player.getNearbyEntities(50, 50, 50))
			{
				if (entity instanceof Player)
					continue;
				
				if (!(entity instanceof LivingEntity))
					continue;
				
				LivingEntity livingEntity = (LivingEntity)entity;
				
				if (!(entity instanceof Creature))
					continue;
				
				if(entity.isDead())
					continue;
				
				Creature creature = (Creature)entity;
				if (creature.getTarget() == null)
					continue;
				
				if (!Utils.isLivingEntityNPC(livingEntity))
					continue;
				
				try {
					ISoliniaLivingEntity solLivingEntity = SoliniaLivingEntityAdapter.Adapt(livingEntity);
					if (completedNpcsIds.contains(solLivingEntity.getNpcid()))
						continue;
					
					if (!solLivingEntity.isSocial())
						continue;
					
					completedNpcsIds.add(solLivingEntity.getNpcid());
					
					solLivingEntity.doCallForAssist(creature.getTarget());
					
				} catch (CoreStateInitException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public int getHateListAmount(UUID uniqueId, UUID target) {
		if (getHateList(uniqueId) == null || this.getHateList(uniqueId).keySet().size() == 0)
			return 0;
		
		if (getHateList(uniqueId).get(target) == null)
			return 0;
		
		Tuple<Integer,Boolean> newvalueHate = hateList.get(uniqueId).get(target);
		return newvalueHate.a();
	}
	
	@Override
	public boolean isInHateList(UUID uniqueId, UUID target) {
		if (getHateList(uniqueId) == null || this.getHateList(uniqueId).keySet().size() == 0)
			return false;
		
		if (getHateList(uniqueId).get(target) == null)
			return false;
		
		return true;
	}

	@Override
	public boolean hasHate(UUID uniqueId) {
		if (getHateList(uniqueId) == null || this.getHateList(uniqueId).keySet().size() == 0)
			return false;
		
		return true;
	}
	
	@Override
	public boolean hasAssistHate(UUID uniqueId) {
		if (getHateList(uniqueId) == null || this.getHateList(uniqueId).keySet().size() == 0)
			return false;
		
		return getHateList(uniqueId).entrySet().stream()
	            .anyMatch(t -> t.getValue().b() == false);
	}

	@Override
	public List<UUID> getHateListUUIDs(UUID uuid) {
		List<UUID> hatelist = new ArrayList<UUID>();
		if (!this.hasHate(uuid))
			return hatelist;
		
		for(Entry<UUID, Tuple<Integer, Boolean>> entitesHateList : hateList.get(uuid).entrySet())
		{
			if (!hatelist.contains(entitesHateList.getKey()))
				hatelist.add(entitesHateList.getKey());
		}
		
		return hatelist;
	}

	@Override
	public long getReverseAggroCount(UUID uniqueId) {
		if (reverseHateList.get(uniqueId) != null)
		{
			long hateList = getReverseHateList(uniqueId).entrySet().stream()
		            .filter(t -> t.getValue().a() > 0).count();
			return hateList;
		}

		return 0;
	}

	@Override
	public void resetReverseAggro(UUID uniqueId) {
		if (reverseHateList.get(uniqueId) != null)
		{
			Entity entity = Bukkit.getEntity(uniqueId);
			if (entity == null)
				return;
			
			if (reverseHateList.get(uniqueId).size() == 0)
			{
				return;
			}
			
			// clear reverse hate list in advance
			for(Entry<UUID, Tuple<Integer, Boolean>> entitesReverseHateList : reverseHateList.get(uniqueId).entrySet())
			{
				if (hateList.get(entitesReverseHateList.getKey()) == null)
				{
					continue;
				}
				
				if (hateList.get(entitesReverseHateList.getKey()).get(uniqueId) != null)
				{
					hateList.get(entitesReverseHateList.getKey()).remove(uniqueId);
					Entity bukkitEntity = Bukkit.getEntity(entitesReverseHateList.getKey());
					if (bukkitEntity != null && bukkitEntity instanceof LivingEntity)
					{
						try
						{
							ISoliniaLivingEntity solLivingEntity = SoliniaLivingEntityAdapter.Adapt((LivingEntity)bukkitEntity);
							if (solLivingEntity != null && solLivingEntity.getAttackTarget().getUniqueId().equals(uniqueId))
							{
								if (solLivingEntity.isInHateList(uniqueId))
									solLivingEntity.removeFromHateList(uniqueId);
								solLivingEntity.checkHateTargets();
							}
						} catch (CoreStateInitException e)
						{
							
						}
					}
					

				}
			}
		}
		
		reverseHateList.put(uniqueId,  new ConcurrentHashMap<UUID, Tuple<Integer,Boolean>>());
	}

	@Override
	public ActiveSongs getEntitySinging(UUID uniqueId) {
		if (this.entitySinging.get(uniqueId) == null)
			this.entitySinging.put(uniqueId, new ActiveSongs());
		
		return this.entitySinging.get(uniqueId);
			
	}

	@Override
	public void setEntitySinging(UUID uniqueId, Integer spellId) {
		this.getEntitySinging(uniqueId).startSinging(spellId);
	}

	@Override
	public List<UUID> getReverseEntityTarget(UUID uniqueId) {
		return this.getEntityTargets().entrySet().stream().
				filter(entry -> entry.getValue().equals(uniqueId)).map(Map.Entry::getKey).collect(Collectors.toList());
	}

	public int getPetFocus(UUID uuid) {
		if (petFocus.get(uuid) == null)
			return 0;
		
		return petFocus.get(uuid);
	}

	public void setPetFocus(UUID uuid, int petFocus) {
		this.petFocus.put(uuid, petFocus);
	}

}
