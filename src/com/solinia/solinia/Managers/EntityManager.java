package com.solinia.solinia.Managers;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InsufficientTemporaryMerchantItemException;
import com.solinia.solinia.Interfaces.IEntityManager;
import com.solinia.solinia.Interfaces.INPCEntityProvider;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Interfaces.ISoliniaNPCMerchant;
import com.solinia.solinia.Interfaces.ISoliniaNPCMerchantEntry;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Models.ActiveSpellEffect;
import com.solinia.solinia.Models.SoliniaActiveSpell;
import com.solinia.solinia.Models.SoliniaEntitySpells;
import com.solinia.solinia.Models.SoliniaLivingEntity;
import com.solinia.solinia.Models.SoliniaNPCMerchantEntry;
import com.solinia.solinia.Models.SoliniaSpell;
import com.solinia.solinia.Models.SpellEffectType;
import com.solinia.solinia.Utils.Utils;

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_12_R1.GenericAttributes;

public class EntityManager implements IEntityManager {
	INPCEntityProvider npcEntityProvider;
	private ConcurrentHashMap<UUID, SoliniaEntitySpells> entitySpells = new ConcurrentHashMap<UUID, SoliniaEntitySpells>();
	private ConcurrentHashMap<UUID, Integer> entityManaLevels = new ConcurrentHashMap<UUID, Integer>();
	private ConcurrentHashMap<UUID, Timestamp> entityMezzed = new ConcurrentHashMap<UUID, Timestamp>();
	private ConcurrentHashMap<UUID, UUID> playerpetsdata = new ConcurrentHashMap<UUID, UUID>();
	private ConcurrentHashMap<UUID, Boolean> trance = new ConcurrentHashMap<UUID, Boolean>();
	private ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Integer>> temporaryMerchantItems = new ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Integer>>();
	public EntityManager(INPCEntityProvider npcEntityProvider) {
		this.npcEntityProvider = npcEntityProvider;
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
	public boolean addActiveEntitySpell(Plugin plugin, LivingEntity targetEntity, SoliniaSpell soliniaSpell, LivingEntity sourceEntity) {
		
		if (entitySpells.get(targetEntity.getUniqueId()) == null)
			entitySpells.put(targetEntity.getUniqueId(), new SoliniaEntitySpells(targetEntity));
		
		int duration = Utils.getDurationFromSpell(soliniaSpell);
		return entitySpells.get(targetEntity.getUniqueId()).addSpell(plugin, soliniaSpell, sourceEntity, duration);
	}
	
	@Override
	public SoliniaEntitySpells getActiveEntitySpells(LivingEntity entity) {
		if (entitySpells.get(entity.getUniqueId()) == null)
			entitySpells.put(entity.getUniqueId(), new SoliniaEntitySpells(entity));
		
		return entitySpells.get(entity.getUniqueId());
	}

	@Override
	public void spellTick(Plugin plugin) {
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
			entitySpells.get(uuid).removeAllSpells();
		
		entitySpells.remove(uuid);
	}
	
	@Override 
	public void removeSpellEffectsOfSpellId(UUID uuid, int spellId)
	{
		if (entitySpells.get(uuid) != null)
			entitySpells.get(uuid).removeAllSpellsOfId(spellId);
	}
	
	@Override
	public void doNPCRandomChat() {
		System.out.println("NPC Random Chat Tick");
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
		System.out.println("NPC Random Chat Tick completed");
	}

	@Override
	public void doNPCSpellCast(Plugin plugin) {
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
				
				if (!(entity instanceof Creature))
					continue;
				
				Creature c = (Creature)entity;
				if (c.getTarget() == null)
					continue;
				
				if (!Utils.isLivingEntityNPC(le))
					continue;
				
				try {
					ISoliniaLivingEntity solle = SoliniaLivingEntityAdapter.Adapt(le);
					if (completedNpcsIds.contains(solle.getNpcid()))
						continue;
					
					completedNpcsIds.add(solle.getNpcid());
					solle.doSpellCast(plugin, c.getTarget());
					
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
	public Timestamp getMezzed(LivingEntity livingEntity) {
		Calendar calendar = Calendar.getInstance();
		java.util.Date now = calendar.getTime();
		Timestamp nowtimestamp = new Timestamp(now.getTime());
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
	public LivingEntity SpawnPet(Plugin plugin, Player owner, ISoliniaSpell spell)
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
			entity.setMaxHealth(solentity.getMaxHP());
			entity.setHealth(solentity.getMaxHP());
			net.minecraft.server.v1_12_R1.EntityInsentient entityhandle = (net.minecraft.server.v1_12_R1.EntityInsentient) ((org.bukkit.craftbukkit.v1_12_R1.entity.CraftLivingEntity) entity).getHandle();
			entityhandle.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue((double)solentity.getMaxDamage());
			owner.sendMessage("New Pet spawned with HP: " + entity.getMaxHealth() + " and " + solentity.getMaxDamage() + " dmg");
			

			MobDisguise mob = new MobDisguise(DisguiseType.WOLF);
			
			switch(npc.getMctype().toUpperCase())
			{
				case "WOLF":
					mob = new MobDisguise(DisguiseType.WOLF);
					break;
				case "SQUID":
					mob = new MobDisguise(DisguiseType.SQUID);
					break;
				case "PARROT":
					mob = new MobDisguise(DisguiseType.PARROT);
					break;
				case "SKELETON":
					mob = new MobDisguise(DisguiseType.SKELETON);
					break;
				case "BLAZE":
					mob = new MobDisguise(DisguiseType.BLAZE);
					break;
				case "IRON_GOLEM":
					mob = new MobDisguise(DisguiseType.IRON_GOLEM);
					break;
				case "GUARDIAN":
					mob = new MobDisguise(DisguiseType.GUARDIAN);
					break;
				default:
					mob = new MobDisguise(DisguiseType.WOLF);
					break;
			}
						
			DisguiseAPI.disguiseEntity(entity, mob);
			
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
		
		entitySpells.get(livingEntity.getUniqueId()).removeFirstSpellOfEffectType(type);
	}

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
	
			if (merchant.getEntries().size() > 0)
				combinedEntries.addAll(merchant.getEntries());
	
			List<ISoliniaNPCMerchantEntry> tempItems = getTemporaryMerchantItems(npc);
			if (tempItems.size() > 0)
				combinedEntries.addAll(tempItems);
		} catch (CoreStateInitException e)
		{
			return new ArrayList<ISoliniaNPCMerchantEntry>();
		}
		return combinedEntries;
		
	}
	
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
}
