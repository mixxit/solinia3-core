package com.solinia.solinia.Managers;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.inventivetalent.glow.GlowAPI;

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
import com.solinia.solinia.Models.ActiveSpellEffect;
import com.solinia.solinia.Models.CastingSpell;
import com.solinia.solinia.Models.EntityAutoAttack;
import com.solinia.solinia.Models.SoliniaActiveSpell;
import com.solinia.solinia.Models.SoliniaEntitySpells;
import com.solinia.solinia.Models.SoliniaLivingEntity;
import com.solinia.solinia.Models.UniversalMerchant;
import com.solinia.solinia.Models.UniversalMerchantEntry;
import com.solinia.solinia.Models.SoliniaSpell;
import com.solinia.solinia.Models.SpellEffectType;
import com.solinia.solinia.Models.SpellType;
import com.solinia.solinia.Utils.ScoreboardUtils;
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
import net.minecraft.server.v1_13_R2.GenericAttributes;

public class EntityManager implements IEntityManager {
	INPCEntityProvider npcEntityProvider;
	private ConcurrentHashMap<UUID, SoliniaEntitySpells> entitySpells = new ConcurrentHashMap<UUID, SoliniaEntitySpells>();
	private ConcurrentHashMap<UUID, Integer> entitySinging = new ConcurrentHashMap<UUID, Integer>();
	private ConcurrentHashMap<UUID, Timestamp> lastDualWield = new ConcurrentHashMap<UUID, Timestamp>();
	private ConcurrentHashMap<UUID, Timestamp> lastDoubleAttack = new ConcurrentHashMap<UUID, Timestamp>();
	private ConcurrentHashMap<UUID, ConcurrentHashMap<UUID, Integer>> hateList = new ConcurrentHashMap<UUID, ConcurrentHashMap<UUID, Integer>>();
	private ConcurrentHashMap<UUID, Timestamp> lastRiposte = new ConcurrentHashMap<UUID, Timestamp>();
	private ConcurrentHashMap<UUID, Timestamp> lastBindwound = new ConcurrentHashMap<UUID, Timestamp>();
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
	
	private Plugin plugin;
	
	public EntityManager(Plugin plugin, INPCEntityProvider npcEntityProvider) {
		this.npcEntityProvider = npcEntityProvider;
		this.plugin = plugin;
	}
	
	@Override
	public Inventory getNPCMerchantInventory(UUID playerUUID, ISoliniaNPC npc, int pageno)
	{
		if (npc.getMerchantid() < 1)
			return null;
		
		try
		{
			ISoliniaNPCMerchant soliniaNpcMerchant = StateManager.getInstance().getConfigurationManager().getNPCMerchant(npc.getMerchantid());
			
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
					.getNPCMerchantCombinedEntries(npc);
			
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
	public boolean addActiveEntitySpell(LivingEntity targetEntity, SoliniaSpell soliniaSpell, LivingEntity sourceEntity) {
		try {
			if (soliniaSpell.isCharmSpell() && getPet(sourceEntity.getUniqueId()) != null && !getPet(sourceEntity.getUniqueId()).getUniqueId().equals(targetEntity.getUniqueId()))
				return false;
		
			if (entitySpells.get(targetEntity.getUniqueId()) == null)
				entitySpells.put(targetEntity.getUniqueId(), new SoliniaEntitySpells(targetEntity));
		
		
			ISoliniaLivingEntity solLivingSourceEntity = SoliniaLivingEntityAdapter.Adapt(sourceEntity);
			int duration = Utils.getDurationFromSpell(solLivingSourceEntity, soliniaSpell);
			if (soliniaSpell.isBardSong() && duration == 0)
			{
				duration = 18;
			}
			
			return entitySpells.get(targetEntity.getUniqueId()).addSpell(plugin, soliniaSpell, sourceEntity, duration);
		} catch (CoreStateInitException e) {
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
			removeSpellEffects(uuid, false);
		}
		
		
		for(SoliniaEntitySpells entityEffects : entitySpells.values())
		{
			entityEffects.run(plugin);
		}
	}
	
	@Override 
	public void removeSpellEffects(UUID uuid, boolean forceDoNotLoopBardSpell)
	{
		if (entitySpells.get(uuid) != null)
			entitySpells.get(uuid).removeAllSpells(plugin, forceDoNotLoopBardSpell);
		
		entitySpells.remove(uuid);
	}
	
	@Override 
	public void removeSpellEffectsOfSpellId(UUID uuid, int spellId, boolean forceDoNotLoopBardSpell)
	{
		if (entitySpells.get(uuid) != null)
			entitySpells.get(uuid).removeAllSpellsOfId(plugin, spellId, forceDoNotLoopBardSpell);
	}
	
	@Override
	public void doNPCRandomChat() {
		List<Integer> completedNpcsIds = new ArrayList<Integer>();
		for(Player player : Bukkit.getOnlinePlayers())
		{
			for(Entity entity : player.getNearbyEntities(50, 50, 50))
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
		
		for(Player player : Bukkit.getOnlinePlayers())
		{
			for(Entity entity : player.getNearbyEntities(50, 50, 50))
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
						continue;
					
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
		
		// Clear and reset all entities that are not near players
		Utils.ClearHateAndResetNpcsNotInList(entitiesNearPlayers);
	}
	
	@Override
	public void doNPCSpellCast() {
		List<Integer> completedNpcsIds = new ArrayList<Integer>();
		for(Player player : Bukkit.getOnlinePlayers())
		{
			for(Entity entityThatWillCast : player.getNearbyEntities(50, 50, 50))
			{
				if (entityThatWillCast instanceof Player)
					continue;
				
				if (!(entityThatWillCast instanceof LivingEntity))
					continue;
				
				LivingEntity livingEntityThatWillCast = (LivingEntity)entityThatWillCast;
				
				if (!(entityThatWillCast instanceof Creature))
					continue;
				
				if(entityThatWillCast.isDead())
					continue;
				
				Creature creatureThatWillCast = (Creature)entityThatWillCast;
				if (creatureThatWillCast.getTarget() == null)
					continue;
				
				if (!Utils.isLivingEntityNPC(livingEntityThatWillCast))
					continue;
				
				try {
					ISoliniaLivingEntity solLivingEntityThatWillCast = SoliniaLivingEntityAdapter.Adapt(livingEntityThatWillCast);
					if (completedNpcsIds.contains(solLivingEntityThatWillCast.getNpcid()))
						continue;
					
					completedNpcsIds.add(solLivingEntityThatWillCast.getNpcid());
					
					if (Utils.isEntityInLineOfSight(livingEntityThatWillCast, creatureThatWillCast.getTarget()))
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
	public void removeMezzed(LivingEntity livingEntity, Timestamp expiretimestamp) {
		this.entityMezzed.remove(livingEntity.getUniqueId());
	}
	
	@Override
	public void removeStunned(LivingEntity livingEntity, Timestamp expiretimestamp) {
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
	public void removePet(UUID petOwnerUUID, boolean kill) {
		UUID entityuuid = this.petownerdata.get(petOwnerUUID);
		if (this.petownerdata.get(petOwnerUUID) == null)
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
				if (solLivingEntity != null && solLivingEntity.getActiveMob() != null)
					solLivingEntity.getActiveMob().removeOwner();
				} catch (CoreStateInitException e)
				{
					
				}
			}
			
			if (kill == true)
				Utils.RemoveEntity(entity,"KILLPET");
		}
			
		this.petownerdata.remove(petOwnerUUID);
		Entity owner = Bukkit.getEntity(petOwnerUUID);
		if (owner != null)
			owner.sendMessage("You have lost your pet");
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
			
			LivingEntity spawnedMob = (LivingEntity)MythicMobs.inst().getAPIHelper().spawnMythicMob("NPCID_" + npc.getId(), owner.getLocation());
			ISoliniaLivingEntity solPet = SoliniaLivingEntityAdapter.Adapt((LivingEntity)spawnedMob);
			ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(owner);
			StateManager.getInstance().getEntityManager().setPet(owner.getUniqueId(),spawnedMob);
			spawnedMob.setCustomName(solplayer.getForename() + "'s Pet");
			spawnedMob.setCustomNameVisible(true);
			spawnedMob.setCanPickupItems(false);
			
			double maxHp = solPet.getMaxHP();
			if (npc.getForcedMaxHp() > 0)
			{
				maxHp = (double)npc.getForcedMaxHp();
			}
			
			AttributeInstance healthAttribute = spawnedMob.getAttribute(Attribute.GENERIC_MAX_HEALTH);
			healthAttribute.setBaseValue(maxHp);
			
			if (!spawnedMob.isDead())
				spawnedMob.setHealth(maxHp);
			
			net.minecraft.server.v1_13_R2.EntityInsentient entityhandle = (net.minecraft.server.v1_13_R2.EntityInsentient) ((org.bukkit.craftbukkit.v1_13_R2.entity.CraftLivingEntity) spawnedMob).getHandle();
			entityhandle.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue((double)solPet.getMaxDamage());
			entityhandle.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue((double)0.4D);
			owner.sendMessage("New Pet spawned with HP: " + spawnedMob.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() + " and " + solPet.getMaxDamage() + " dmg");
			

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
				livingEntityPet.remove();
			this.petownerdata.remove(key);
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
			if (solEntity != null)
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
			removeSpellEffects(uniqueId, false);
	}

	@Override
	public void clearEntityFirstEffectOfType(LivingEntity livingEntity, SpellEffectType type, boolean forceDoNotLoopBardSpell) {
		if (entitySpells.get(livingEntity.getUniqueId()) == null)
			return;
		
		entitySpells.get(livingEntity.getUniqueId()).removeFirstSpellOfEffectType(plugin, type, forceDoNotLoopBardSpell);
	}
	
	@Override
	public void clearEntityFirstEffect(LivingEntity livingEntity) {
		if (entitySpells.get(livingEntity.getUniqueId()) == null)
			return;
		
		entitySpells.get(livingEntity.getUniqueId()).removeFirstSpell(plugin, false);
	}
	/*
	@Override
	public void addTemporaryMerchantItem(int npcid, int itemid, int amount) {
		if (temporaryMerchantItems.get(npcid) == null)
			temporaryMerchantItems.put(npcid, new ConcurrentHashMap<Integer, Integer>());
		
		if (temporaryMerchantItems.get(npcid).get(itemid) == null)
			temporaryMerchantItems.get(npcid).put(itemid, 0);
		
		int currentCount = temporaryMerchantItems.get(npcid).get(itemid);
		temporaryMerchantItems.get(npcid).put(itemid, currentCount + amount);
	}
	
	@Override
	public List<ISoliniaNPCMerchantEntry> getTemporaryMerchantItems(ISoliniaNPC npc) {
		if (temporaryMerchantItems.get(npc.getId()) == null)
			temporaryMerchantItems.put(npc.getId(), new ConcurrentHashMap<Integer, Integer>());
		
		List<ISoliniaNPCMerchantEntry> entries = new ArrayList<ISoliniaNPCMerchantEntry>();
		for(Integer key : temporaryMerchantItems.get(npc.getId()).keySet())
		{
			if (temporaryMerchantItems.get(npc.getId()).get(key) < 1)
				continue;
			
			ISoliniaNPCMerchantEntry entry = new SoliniaNPCMerchantEntry();
			entry.setId(0);
			entry.setItemid(key);
			entry.setMerchantid(npc.getMerchantid());
			entry.setTemporaryquantitylimit(temporaryMerchantItems.get(npc.getId()).get(key));
			entries.add(entry);
		}
		return entries;
	}
	*/
	@Override
	public List<ISoliniaNPCMerchantEntry> getNPCMerchantCombinedEntries(ISoliniaNPC npc) {
		List<ISoliniaNPCMerchantEntry> combinedEntries = new ArrayList<ISoliniaNPCMerchantEntry>();
		if (npc.getMerchantid() < 1)
			return combinedEntries;
			
		try
		{
			ISoliniaNPCMerchant merchant = StateManager.getInstance().getConfigurationManager().getNPCMerchant(npc.getMerchantid());
			
			if (merchant == null)
				return combinedEntries;
	
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
	/*
	@Override
	public void removeTemporaryMerchantItem(int npcid, int itemid, int amount) throws InsufficientTemporaryMerchantItemException {
		if (temporaryMerchantItems.get(npcid) == null)
			temporaryMerchantItems.put(npcid, new ConcurrentHashMap<Integer, Integer>());
		
		if (temporaryMerchantItems.get(npcid).get(itemid) == null)
			temporaryMerchantItems.get(npcid).put(itemid, 0);
		
		int currentCount = temporaryMerchantItems.get(npcid).get(itemid);
		
		if (currentCount < amount)
			throw new InsufficientTemporaryMerchantItemException("Vendor does not have sufficient items");
		
		int newCount = currentCount - amount;
		if (newCount < 0)
			newCount = 0;
		
		temporaryMerchantItems.get(npcid).put(itemid, newCount);
	}
	*/
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
			for(Entity entityThatWillTeleportAttack : player.getNearbyEntities(50, 50, 50))
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
	public Integer getEntitySinging(UUID entityUUID) {
		return entitySinging.get(entityUUID);
	}

	@Override
	public void setEntitySinging(UUID entityUUID, Integer spellId) {
		if (spellId == null || spellId == 0)
			this.entitySinging.remove(entityUUID);
		else
		this.entitySinging.put(entityUUID,spellId);
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
	public LivingEntity getEntityTarget(LivingEntity entitySource)
	{
		if (entitySource == null)
			return null;
		
		// If i'm a creature, return creature target
		if (entitySource instanceof Creature)
		{
			return ((Creature)entitySource).getTarget();
		}
		
		UUID target = entityTargets.get(entitySource.getUniqueId());
		if (target == null)
		{
			return null;
		}
		
		Entity entity = Bukkit.getEntity(target);
		if (entity == null)
		{
			setEntityTarget(entitySource,null);
			return null;
		}
		
		if (!(entity instanceof LivingEntity))
		{
			setEntityTarget(entitySource,null);
			return null;
		}

		if (((LivingEntity)entity).isDead())
		{
			setEntityTarget(entitySource,null);
			return null;
		}
		
		return ((LivingEntity)entity);		
	}
	
	@Override
	public void setEntityTarget(LivingEntity source, LivingEntity target)
	{
		// When changing target always clear auto attack
		if (source instanceof Player)
		{
			if (target == null)
			{
				this.setEntityAutoAttack((Player)source, false);
			} else {
				// get current target
				if (entityTargets.get(source.getUniqueId()) != null)
				{
					if (source != null && target != null && !entityTargets.get(source.getUniqueId()).toString().equals(target.getUniqueId().toString()))
					{
						this.setEntityAutoAttack((Player)source, false);
					}
				}
			}
		}
		
		if (source instanceof Creature)
		{
			try {
				SoliniaLivingEntityAdapter.Adapt(source).setAttackTarget(target);
			} catch (CoreStateInitException e) {
			}
		}
		
		if (target == null)
		{
			if (entityTargets.get(source.getUniqueId()) != null)
			{
				Entity currentTarget = Bukkit.getEntity(entityTargets.get(source.getUniqueId()));
				if (source instanceof Player && currentTarget != null)
				{
					try {
						ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player)source);
						
						if (solPlayer != null && solPlayer.isGlowTargetting())
							Utils.setGlowing((Entity)currentTarget, false, (Player)source);
					} catch (CoreStateInitException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
			
			// no need, is on the boss bar
			//source.sendMessage(ChatColor.GRAY + "Cleared your target");
			entityTargets.remove(source.getUniqueId());
			
	        if (source instanceof Player)
			{
				try
				{
					ISoliniaLivingEntity solPlayer = SoliniaLivingEntityAdapter.Adapt(source);
					ScoreboardUtils.UpdateScoreboard((Player)source,solPlayer.getMana());
				} catch (CoreStateInitException e)
				{
					
				}
			}
		} else {
			
			// if already has a target turn it off
			if (entityTargets.get(source.getUniqueId()) != null)
			{
				Entity currentTarget = Bukkit.getEntity(entityTargets.get(source.getUniqueId()));
				if (source instanceof Player && currentTarget != null)
				{
					try {
						ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player)source);
						
						if (solPlayer != null && solPlayer.isGlowTargetting())
							Utils.setGlowing((Entity)currentTarget, false, (Player)source);
					} catch (CoreStateInitException e)
					{
						
					}
				}
			}
			
			boolean toggleGlow = false;
			
			if (source instanceof Player && target != null)
			{
				try {
					ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player)source);
					ISoliniaLivingEntity solEntity = SoliniaLivingEntityAdapter.Adapt(target);
					ISoliniaLivingEntity solPlayerEntity = SoliniaLivingEntityAdapter.Adapt((Player)source);
					
					if (solPlayer != null && solEntity != null && solPlayerEntity != null && solPlayer.isGlowTargetting())
					{
						toggleGlow = solPlayer.isGlowTargetting();
						Utils.setGlowing((Entity)target, Utils.getGlowColor(solEntity.getLevelCon(solPlayerEntity)), (Player)source);
					}
				} catch (CoreStateInitException e)
				{
					
				}
			}
			
			// no need, is on the bossbar
			//source.sendMessage(ChatColor.GRAY + "Set target to " + target.getName() + " [/toggleglow: " + toggleGlow + "]");
			entityTargets.put(source.getUniqueId(), target.getUniqueId());
			if (source instanceof Player)
			{
				try
				{
					ScoreboardUtils.UpdateScoreboard((Player)source,SoliniaLivingEntityAdapter.Adapt(source).getMana());
				} catch (CoreStateInitException e)
				{
					
				}
			}
		}
	}

	@Override
	public void clearTargetsAgainstMe(LivingEntity livingEntity) {
		for (Player player : livingEntity.getWorld().getPlayers())
		{
			if (getEntityTarget(player) == null)
				continue;
			
			if (getEntityTarget(player).getUniqueId().toString().equals(livingEntity.getUniqueId().toString()))
				setEntityTarget(player,null);
		}
		
		for(Entity entity : livingEntity.getNearbyEntities(50, 50, 50))
		{
			if (entity instanceof Creature)
			{
				if (((Creature) entity).getTarget() != null)
				if (((Creature) entity).getTarget().getUniqueId().toString().equals(livingEntity.getUniqueId().toString()))
				{
					setEntityTarget((LivingEntity)entity,null);
				}
			}
		}

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
			this.following.remove(entityUuid);
		
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
	public void startCasting(LivingEntity livingEntity, CastingSpell castingSpell)
	{
		interruptCasting(livingEntity);
		if (livingEntity instanceof Player)
		{
			livingEntity.sendMessage("You begin casting " + castingSpell.getSpell().getName());
			entitySpellCasting.put(livingEntity.getUniqueId(), castingSpell);
		}
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
				player.sendMessage("You finish casting");
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
					try {
						ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player)entity);
						ScoreboardUtils.UpdateScoreboard((Player)entity, solPlayer.getMana());
					} catch (CoreStateInitException e) {
						
					}
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
		getEntityAutoAttack(livingEntity).setAutoAttacking(autoAttacking);
	}
	
	@Override
	public void toggleAutoAttack(Player player) {
		Boolean autoAttackState = getEntityAutoAttack(player).isAutoAttacking();
		
		if (!autoAttackState == false)
		{
			player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
					new TextComponent(ChatColor.GRAY + "* You stop auto attacking"));
		} else {
			
			player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
					new TextComponent(ChatColor.GRAY + "* You start auto attacking"));
		}
		
		setEntityAutoAttack(player, !autoAttackState);
	}

	@Override
	public ConcurrentHashMap<UUID, Timestamp> getLastDualWield() {
		return lastDualWield;
	}

	@Override
	public void setLastDualWield(UUID uuid, Timestamp lasttimestamp) {
		this.lastDualWield.put(uuid, lasttimestamp);
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
	public ConcurrentHashMap<UUID, Timestamp> getLastRiposte() {
		return lastRiposte;
	}

	@Override
	public void setLastRiposte(UUID uuid, Timestamp lasttimestamp) {
		this.lastRiposte.put(uuid, lasttimestamp);
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
	public void addToHateList(UUID entity, UUID provoker, int hate) {
		if (hateList.get(entity) == null)
			hateList.put(entity, new ConcurrentHashMap<UUID, Integer>());
		
		if (hateList.get(entity).get(provoker) == null)
		{
			hateList.get(entity).put(provoker, hate);
			return;
		}
		
		int newvalue = hateList.get(entity).get(provoker);
		if ((newvalue + hate) > Integer.MAX_VALUE)
			newvalue = Integer.MAX_VALUE;
		else if ((newvalue + hate) < 0)
			newvalue = 0;
		else
			newvalue += hate;
		
		if (newvalue == 0)
			hateList.get(entity).remove(provoker);
		else
			hateList.get(entity).put(provoker, newvalue);
	}
	
	@Override
	public ConcurrentHashMap<UUID, Integer> getHateList(UUID entity)
	{
		if (hateList.get(entity) == null)
			hateList.put(entity, new ConcurrentHashMap<UUID, Integer>());
		
		return hateList.get(entity);
	}

	@Override
	public List<UUID> getActiveHateListUUIDs()
	{
		return Collections.list(hateList.keys());
	}
	
	@Override
	public Integer getHateListEntry(UUID entity, UUID provoker)
	{
		if (hateList.get(entity) == null)
			hateList.put(entity, new ConcurrentHashMap<UUID, Integer>());
		
		if (hateList.get(entity).get(provoker) == null)
			return 0;
		
		return hateList.get(entity).get(provoker);
	}

	@Override
	public void clearHateList(UUID entityUuid) {
		hateList.put(entityUuid,  new ConcurrentHashMap<UUID, Integer>());
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
}
