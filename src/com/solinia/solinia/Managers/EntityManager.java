package com.solinia.solinia.Managers;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.IEntityManager;
import com.solinia.solinia.Interfaces.INPCEntityProvider;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Models.SoliniaEntitySpellEffects;
import com.solinia.solinia.Models.SoliniaLivingEntity;
import com.solinia.solinia.Models.SoliniaSpell;
import com.solinia.solinia.Utils.Utils;

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_12_R1.GenericAttributes;

public class EntityManager implements IEntityManager {
	INPCEntityProvider npcEntityProvider;
	private ConcurrentHashMap<UUID, SoliniaEntitySpellEffects> entitySpellEffects = new ConcurrentHashMap<UUID, SoliniaEntitySpellEffects>();
	private ConcurrentHashMap<UUID, Integer> entityManaLevels = new ConcurrentHashMap<UUID, Integer>();
	private ConcurrentHashMap<UUID, Timestamp> entityMezzed = new ConcurrentHashMap<UUID, Timestamp>();
	private ConcurrentHashMap<UUID, UUID> playerpetsdata = new ConcurrentHashMap<UUID, UUID>();
	
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
	public boolean addActiveEntityEffect(LivingEntity targetEntity, SoliniaSpell soliniaSpell, LivingEntity sourceEntity) {
		
		if (entitySpellEffects.get(targetEntity.getUniqueId()) == null)
			entitySpellEffects.put(targetEntity.getUniqueId(), new SoliniaEntitySpellEffects(targetEntity));
		
		int duration = Utils.getDurationFromSpell(soliniaSpell);
		return entitySpellEffects.get(targetEntity.getUniqueId()).addSpellEffect(soliniaSpell, sourceEntity, duration);
	}
	
	@Override
	public SoliniaEntitySpellEffects getActiveEntityEffects(LivingEntity entity) {
		return entitySpellEffects.get(entity.getUniqueId());
	}

	@Override
	public void spellTick() {
		List<UUID> uuidRemoval = new ArrayList<UUID>();
		for (SoliniaEntitySpellEffects entityEffects : entitySpellEffects.values())
		{
			Entity entity = Bukkit.getEntity(entityEffects.getLivingEntityUUID());
			if (entity == null)
			{
				uuidRemoval.add(entityEffects.getLivingEntityUUID());
			}
		}
		
		for(UUID uuid : uuidRemoval)
		{
			System.out.println("Cleared Entity Effects for invalid UUID: " + uuid);
			entitySpellEffects.remove(uuid);
		}
		
		
		for(SoliniaEntitySpellEffects entityEffects : entitySpellEffects.values())
		{
			entityEffects.run();
		}
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
	public void doNPCSpellCast() {
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
					solle.doSpellCast(c.getTarget());
					
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
			entityManaLevels.put(bukkitLivingEntity.getUniqueId(), npc.getMaxMP());
		
		return entityManaLevels.get(bukkitLivingEntity.getUniqueId());
	}
	
	@Override
	public void setNPCMana(LivingEntity bukkitLivingEntity, ISoliniaNPC npc, int amount) {
		if (bukkitLivingEntity instanceof Player)
			return;
		
		if (amount < 0)
			amount = 0;
		
		if (amount > npc.getMaxMP())
			amount = npc.getMaxMP();
		
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
			
			Wolf entity = (Wolf) owner.getWorld().spawnEntity(owner.getLocation(), EntityType.WOLF);
			StateManager.getInstance().getEntityManager().setPet(owner,entity);
			entity.setAdult();
			entity.setTamed(true);
			entity.setOwner(owner);
			entity.setBreed(false);
			entity.setCustomName(ChatColor.YELLOW + owner.getDisplayName() + "'s Pet");
			entity.setCustomNameVisible(true);
			entity.setCanPickupItems(false);
	        
			// TODO Use Illusion here with the npcs type
			
			entity.setMaxHealth(npc.getMaxHP());
			entity.setHealth(npc.getMaxHP());
			net.minecraft.server.v1_12_R1.EntityInsentient entityhandle = (net.minecraft.server.v1_12_R1.EntityInsentient) ((org.bukkit.craftbukkit.v1_12_R1.entity.CraftLivingEntity) entity).getHandle();
			entityhandle.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue((double)npc.getMaxDamage());
			owner.sendMessage("New Pet spawned with HP: " + entity.getMaxHealth() + " and " + npc.getMaxDamage() + " dmg");
			

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
				case "AREA_EFFECT_CLOUD":
					mob = new MobDisguise(DisguiseType.AREA_EFFECT_CLOUD);
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
}
