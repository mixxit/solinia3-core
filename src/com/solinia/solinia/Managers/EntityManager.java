package com.solinia.solinia.Managers;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
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
import com.solinia.solinia.Models.SoliniaActiveSpell;
import com.solinia.solinia.Models.SoliniaAlignmentChunk;
import com.solinia.solinia.Models.SoliniaEntitySpells;
import com.solinia.solinia.Models.SoliniaLivingEntity;
import com.solinia.solinia.Models.UniversalMerchant;
import com.solinia.solinia.Models.UniversalMerchantEntry;
import com.solinia.solinia.Models.SoliniaSpell;
import com.solinia.solinia.Models.SpellEffectType;
import com.solinia.solinia.Models.SpellType;
import com.solinia.solinia.Utils.ScoreboardUtils;
import com.solinia.solinia.Utils.Utils;

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;
import me.libraryaddict.disguise.disguisetypes.TargetedDisguise;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_12_R1.GenericAttributes;

public class EntityManager implements IEntityManager {
	INPCEntityProvider npcEntityProvider;
	private ConcurrentHashMap<UUID, SoliniaEntitySpells> entitySpells = new ConcurrentHashMap<UUID, SoliniaEntitySpells>();
	private ConcurrentHashMap<UUID, Integer> entitySinging = new ConcurrentHashMap<UUID, Integer>();
	private ConcurrentHashMap<UUID, Timestamp> dontHealMe = new ConcurrentHashMap<UUID, Timestamp>();
	private ConcurrentHashMap<UUID, Timestamp> dontRootMe = new ConcurrentHashMap<UUID, Timestamp>();
	private ConcurrentHashMap<UUID, Timestamp> dontBuffMe = new ConcurrentHashMap<UUID, Timestamp>();
	private ConcurrentHashMap<UUID, Timestamp> dontSnareMe = new ConcurrentHashMap<UUID, Timestamp>();
	private ConcurrentHashMap<UUID, Timestamp> dontDotMe = new ConcurrentHashMap<UUID, Timestamp>();
	private ConcurrentHashMap<UUID, Integer> entityManaLevels = new ConcurrentHashMap<UUID, Integer>();
	private ConcurrentHashMap<UUID, Timestamp> entityMezzed = new ConcurrentHashMap<UUID, Timestamp>();
	private ConcurrentHashMap<UUID, UUID> playerpetsdata = new ConcurrentHashMap<UUID, UUID>();
	private ConcurrentHashMap<String, Timestamp> entitySpellCooldown = new ConcurrentHashMap<String, Timestamp>();
	private ConcurrentHashMap<UUID, Boolean> trance = new ConcurrentHashMap<UUID, Boolean>();
	//private ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Integer>> temporaryMerchantItems = new ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Integer>>();
	private ConcurrentHashMap<UUID, Inventory> merchantInventories = new ConcurrentHashMap<UUID, Inventory>();
	private ConcurrentHashMap<UUID, UniversalMerchant> universalMerchant = new ConcurrentHashMap<UUID, UniversalMerchant>();
	private ConcurrentHashMap<UUID, Boolean> playerInTerritory = new ConcurrentHashMap<UUID, Boolean>();
	private ConcurrentHashMap<UUID, Boolean> playerSetMain = new ConcurrentHashMap<UUID, Boolean>();
	private ConcurrentHashMap<UUID, Boolean> playerAutoAttack = new ConcurrentHashMap<UUID, Boolean>();
	private ConcurrentHashMap<UUID, UUID> entityTargets = new ConcurrentHashMap<UUID, UUID>();
	private ConcurrentHashMap<UUID, Boolean> feignedDeath = new ConcurrentHashMap<UUID, Boolean>();
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
	public Inventory getTradeShopMerchantInventory(UUID playerUUID, SoliniaAlignmentChunk alignmentChunk, int pageno)
	{
		UniversalMerchant merchant = new UniversalMerchant();
		merchant.fullmerchantentries = alignmentChunk.getUniversalMerchantEntries(alignmentChunk);
		merchant.merchantName = alignmentChunk.getChunkX() + "_" + alignmentChunk.getChunkZ() + "_TradeShop";
		
		// Cache
		universalMerchant.put(merchant.universalMerchant, merchant);
		return getMerchantInventory(playerUUID, pageno, merchant);
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
							itemStack = new ItemStack(Material.SKULL_ITEM);
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
						itemStack = new ItemStack(Material.SKULL_ITEM);
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
							itemStack = new ItemStack(Material.SKULL_ITEM);
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
	public boolean getTrance(UUID uuid)
	{
		if (trance.get(uuid) == null)
		{
			return false;
		} else {
			return trance.get(uuid);
		}
	}
	
	@Override
	public void setTrance(UUID uuid, Boolean enabled)
	{
		trance.put(uuid, enabled);
		if (enabled == true)
		{
			Bukkit.getPlayer(uuid).sendMessage("You fall into a deep trance");
		} else {
			Bukkit.getPlayer(uuid).sendMessage("You fall out of your trance");
		}
	}

	@Override
	public void toggleTrance(UUID uuid) {
		Boolean current = getTrance(uuid);
		if (current == true)
		{
			setTrance(uuid, false);
		} else {
			setTrance(uuid, true);
		}
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
		
		if (entitySpells.get(targetEntity.getUniqueId()) == null)
			entitySpells.put(targetEntity.getUniqueId(), new SoliniaEntitySpells(targetEntity));
		
		int duration = Utils.getDurationFromSpell(soliniaSpell);
		if (soliniaSpell.isBardSong() && duration == 0)
		{
			duration = 18;
		}
		
		return entitySpells.get(targetEntity.getUniqueId()).addSpell(plugin, soliniaSpell, sourceEntity, duration);
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
			removeSpellEffects(uuid);
		}
		
		
		for(SoliniaEntitySpells entityEffects : entitySpells.values())
		{
			entityEffects.run(plugin);
		}
	}
	
	@Override 
	public void removeSpellEffects(UUID uuid)
	{
		if (entitySpells.get(uuid) != null)
			entitySpells.get(uuid).removeAllSpells(plugin);
		
		entitySpells.remove(uuid);
	}
	
	@Override 
	public void removeSpellEffectsOfSpellId(UUID uuid, int spellId)
	{
		if (entitySpells.get(uuid) != null)
			entitySpells.get(uuid).removeAllSpellsOfId(plugin, spellId);
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
		List<Integer> completedNpcsIds = new ArrayList<Integer>();
		for(Player player : Bukkit.getOnlinePlayers())
		{
			for(Entity entity : player.getNearbyEntities(50, 50, 50))
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
				
				try {
					ISoliniaLivingEntity solle = SoliniaLivingEntityAdapter.Adapt(le);
					if (completedNpcsIds.contains(solle.getNpcid()))
						continue;
					
					completedNpcsIds.add(solle.getNpcid());
					solle.doCheckForEnemies();
					
				} catch (CoreStateInitException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
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
	public LivingEntity getPet(Player player) {
		UUID entityuuid = this.playerpetsdata.get(player.getUniqueId());
		if (this.playerpetsdata.get(player.getUniqueId()) == null)
			return null;
		
		LivingEntity entity = (LivingEntity)Bukkit.getEntity(entityuuid);
		if (entity != null)
			return entity;
		
		return null;
	}

	@Override
	public void killPet(Player player) {
		UUID entityuuid = this.playerpetsdata.get(player.getUniqueId());
		if (this.playerpetsdata.get(player.getUniqueId()) == null)
			return;

		LivingEntity entity = (LivingEntity)Bukkit.getEntity(entityuuid);
		if (entity != null)
			entity.remove();
		this.playerpetsdata.remove(player.getUniqueId());
		player.sendMessage("Your pet has been removed");
	}

	@Override
	public List<LivingEntity> getAllWorldPets() {
		List<LivingEntity> pets = new ArrayList<LivingEntity>();

		for (Map.Entry<UUID, UUID> entry : playerpetsdata.entrySet()) {
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
		try
		{
			LivingEntity pet = StateManager.getInstance().getEntityManager().getPet(owner);
			if (pet != null)
			{
				StateManager.getInstance().getEntityManager().killPet(owner);
			}
			
			ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getPetNPCByName(spell.getTeleportZone());
			
			if (npc == null)
				return null;

			if (npc.isPet() == false)
				return null;
			
			Wolf entity = (Wolf) owner.getWorld().spawnEntity(owner.getLocation(), EntityType.WOLF);
			entity.setMetadata("npcid", new FixedMetadataValue(plugin, "NPCID_" + npc.getId()));
			StateManager.getInstance().getEntityManager().setPet(owner,entity);
			entity.setAdult();
			entity.setTamed(true);
			entity.setOwner(owner);
			entity.setBreed(false);
			ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(owner);
			entity.setCustomName(solplayer.getForename() + "'s Pet");
			entity.setCustomNameVisible(true);
			entity.setCanPickupItems(false);
			
			ISoliniaLivingEntity solentity = SoliniaLivingEntityAdapter.Adapt(entity);
			solentity.configurePetGoals();
			
			double maxHp = solentity.getMaxHP();
			if (npc.getForcedMaxHp() > 0)
			{
				maxHp = (double)npc.getForcedMaxHp();
			}
			
			entity.setMaxHealth(maxHp);
			entity.setHealth(maxHp);
			net.minecraft.server.v1_12_R1.EntityInsentient entityhandle = (net.minecraft.server.v1_12_R1.EntityInsentient) ((org.bukkit.craftbukkit.v1_12_R1.entity.CraftLivingEntity) entity).getHandle();
			entityhandle.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue((double)solentity.getMaxDamage());
			entityhandle.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue((double)0.4D);
			owner.sendMessage("New Pet spawned with HP: " + entity.getMaxHealth() + " and " + solentity.getMaxDamage() + " dmg");
			

			TargetedDisguise mob = new MobDisguise(DisguiseType.WOLF);
			
			switch(npc.getMctype().toUpperCase())
			{
				case "WOLF":
					mob = new MobDisguise(DisguiseType.WOLF);
					break;
				case "SQUID":
					mob = new PlayerDisguise("addicct");
					break;
				case "PARROT":
					mob = new PlayerDisguise("ExoticBengal");
					break;
				case "SKELETON":
					mob = new MobDisguise(DisguiseType.SKELETON);
					break;
				case "BLAZE":
					mob = new PlayerDisguise("Detze");
					break;
				case "IRON_GOLEM":
					mob = new PlayerDisguise("katxxx");
					break;
				case "GUARDIAN":
					mob = new MobDisguise(DisguiseType.PLAYER);
					break;
				default:
					mob = new MobDisguise(DisguiseType.WOLF);
					break;
			}
						
			DisguiseAPI.disguiseEntity(entity, mob);
			
			solentity.configurePetGoals();
			
			return entity;
		} catch (CoreStateInitException e)
		{
			return null;
		}
	}

	@Override
	public void killAllPets() {
		for (Map.Entry<UUID, UUID> entry : playerpetsdata.entrySet()) {
			UUID key = entry.getKey();
			LivingEntity entity = (LivingEntity)Bukkit.getEntity(entry.getValue());
			if (entity != null)
				entity.remove();
			this.playerpetsdata.remove(key);
		}
	}

	@Override
	public LivingEntity setPet(Player player, LivingEntity entity) {
		if (getPet(player) != null) {
			killPet(player);
		}
		player.sendMessage("You have summoned a new pet");
		this.playerpetsdata.put(player.getUniqueId(), entity.getUniqueId());
		return entity;
	}

	@Override
	public void clearEntityEffects(UUID uniqueId) {
		if (entitySpells.get(uniqueId) != null)
			removeSpellEffects(uniqueId);
	}

	@Override
	public void clearEntityFirstEffectOfType(LivingEntity livingEntity, SpellEffectType type) {
		if (entitySpells.get(livingEntity.getUniqueId()) == null)
			return;
		
		entitySpells.get(livingEntity.getUniqueId()).removeFirstSpellOfEffectType(plugin, type);
	}
	
	@Override
	public void clearEntityFirstEffect(LivingEntity livingEntity) {
		if (entitySpells.get(livingEntity.getUniqueId()) == null)
			return;
		
		entitySpells.get(livingEntity.getUniqueId()).removeFirstSpell(plugin);
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
		
		UUID target = entityTargets.get(entitySource.getUniqueId());
		if (target == null)
			return null;
		
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
				this.setPlayerAutoAttack((Player)source, false);
			} else {
				// get current target
				if (entityTargets.get(source.getUniqueId()) != null)
				{
					if (source != null && target != null && !entityTargets.get(source.getUniqueId()).toString().equals(target.getUniqueId().toString()))
					{
						this.setPlayerAutoAttack((Player)source, false);
					}
				}
			}
		}
		
		if (source instanceof Creature)
		{
			((Creature)source).setTarget(target);
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
							GlowAPI.setGlowing((Entity)currentTarget, false, (Player)source);
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
							GlowAPI.setGlowing((Entity)currentTarget, false, (Player)source);
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
					
					if (solPlayer != null && solPlayer.isGlowTargetting())
					{
						toggleGlow = solPlayer.isGlowTargetting();
						GlowAPI.setGlowing((Entity)target, GlowAPI.Color.DARK_AQUA, (Player)source);
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
	public Boolean getPlayerAutoAttack(Player player) {
		if (playerAutoAttack.get(player.getUniqueId()) == null)
		{
			playerAutoAttack.put(player.getUniqueId(), false);
			return false;
		} else {
			return playerAutoAttack.get(player.getUniqueId());
		}
				
	}

	@Override
	public void setPlayerAutoAttack(Player player, boolean playerAutoAttack) {
		this.playerAutoAttack.put(player.getUniqueId(), playerAutoAttack);
	}
	
	@Override
	public void toggleAutoAttack(Player player) {
		Boolean autoAttackState = playerAutoAttack.get(player.getUniqueId());
		if (autoAttackState == null)
		{
			autoAttackState = false;
		}
		
		if (!autoAttackState == false)
		{
			player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
					new TextComponent(ChatColor.GRAY + "* You stop auto attacking"));
		} else {
			
			player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
					new TextComponent(ChatColor.GRAY + "* You start auto attacking"));
		}
		
		playerAutoAttack.put(player.getUniqueId(), !autoAttackState);
		
	}
}
