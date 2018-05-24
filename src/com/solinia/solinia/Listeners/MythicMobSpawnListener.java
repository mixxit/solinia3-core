package com.solinia.solinia.Listeners;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.scheduler.BukkitRunnable;

import com.solinia.solinia.Solinia3CorePlugin;
import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.MythicEntitySoliniaMob;
import com.solinia.solinia.Models.PathfinderGoalVariableSpeedMeleeAttack;
import com.solinia.solinia.Utils.Utils;

import io.lumine.utils.events.Events.Handler;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobSpawnEvent;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import io.lumine.xikage.mythicmobs.mobs.MobManager;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import io.lumine.xikage.mythicmobs.spawning.spawners.MythicSpawner;
import net.minecraft.server.v1_12_R1.EntityCreature;
import net.minecraft.server.v1_12_R1.EntityHuman;
import net.minecraft.server.v1_12_R1.EntityInsentient;
import net.minecraft.server.v1_12_R1.EntityMonster;
import net.minecraft.server.v1_12_R1.EntitySkeleton;
import net.minecraft.server.v1_12_R1.IRangedEntity;
import net.minecraft.server.v1_12_R1.PathfinderGoal;
import net.minecraft.server.v1_12_R1.PathfinderGoalBowShoot;
import net.minecraft.server.v1_12_R1.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_12_R1.PathfinderGoalSelector;

public class MythicMobSpawnListener implements Listener {
	
	public static MythicMobs mythicmobs;
	public static MobManager mobmanager;
	
	Solinia3CorePlugin plugin;

	public MythicMobSpawnListener(Solinia3CorePlugin solinia3CorePlugin) {
		// TODO Auto-generated constructor stub
		plugin = solinia3CorePlugin;
	}
	
	static {
		mythicmobs=MythicMobs.inst();
		mobmanager=mythicmobs.getMobManager();
	}
	
	public Monster spawnCustomMob(Location l1) {
		net.minecraft.server.v1_12_R1.World world=((CraftWorld)l1.getWorld()).getHandle();
		final MythicEntitySoliniaMob mep = new MythicEntitySoliniaMob(world);
		mep.setLocation(l1.getX(),l1.getY(),l1.getZ(),l1.getYaw(),l1.getPitch());
        world.addEntity(mep,SpawnReason.CUSTOM);
        
        return (Monster) mep.getBukkitEntity();
	}
	
	@EventHandler
	public void replaceMobEvent(MythicMobSpawnEvent event) {
		if (event.isCancelled()) 
			return;
		
		if (event.getEntity() instanceof Skeleton) {
			MythicMob mm = event.getMobType();
			LivingEntity p = spawnCustomMob(event.getLocation());
	        
			if (mm.getMythicEntity() !=null)
			{
				p = (LivingEntity)mm.getMythicEntity().applyOptions(p);
			}
			
	        final ActiveMob am = new ActiveMob(p.getUniqueId(), BukkitAdapter.adapt(p),mm,event.getMobLevel());
	        
	        mythicmobs.getMobManager().registerActiveMob(am);
	        
	        mm.applyMobOptions(am,am.getLevel());
	        mm.applyMobVolatileOptions(am);
	        
	        // Remove this old mob
	        final Entity entity = event.getEntity();
	        new BukkitRunnable() {
				@Override
				public void run() {
					ActiveMob am1=null;
					if ((am1=mythicmobs.getMobManager().getMythicMobInstance(entity))!=null) {
						MythicSpawner ms=null;
						if ((ms=am1.getSpawner())!=null) {
							am.setSpawner(ms);
							if (!ms.getAssociatedMobs().contains(am.getUniqueId())) ms.trackMob(am);
						};
						am1.getEntity().remove();
					}
				}
			}.runTaskLater(plugin,1l);
			
			
			// Replace bad goals
	        final UUID entityUuid = p.getUniqueId();
	        new BukkitRunnable() {
				@Override
				public void run() {
					try {
				        Entity entity = Bukkit.getEntity(entityUuid);
						if (!(entity instanceof LivingEntity))
							return;
						
						if (!(Utils.isSoliniaMob(entity)))
							return;
						
						ISoliniaLivingEntity solEntity = SoliniaLivingEntityAdapter.Adapt((LivingEntity)entity);
						if (!solEntity.isNPC())
							return;
						
						EntityInsentient e = (EntityInsentient)((CraftLivingEntity)entity).getHandle();
		                Field goalsField = EntityInsentient.class.getDeclaredField("goalSelector");
		                goalsField.setAccessible(true);
		                PathfinderGoalSelector goals = (PathfinderGoalSelector)goalsField.get((Object)e);
						
						
						ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(solEntity.getNpcid());
						int j = 0;
						if (npc.getFactionid() > 0 || npc.isPet())
						{
							// bow
							
							if (!(e instanceof IRangedEntity))
							{
								goals.a(j, (PathfinderGoal)new PathfinderGoalBowShoot((EntityMonster)((EntitySkeleton)e), 1.0, 20, 15.0f));
								j++;
							}
                            
                            // melee
                            
                            if (!(e instanceof EntityCreature));
                            {
                            	goals.a(j, (PathfinderGoal)new PathfinderGoalVariableSpeedMeleeAttack((MythicEntitySoliniaMob)((CraftEntity)entity).getHandle(), 1.0, true));
                            	j++;
                            }
                            
                            // look
                            goals.a(j, (PathfinderGoal)new PathfinderGoalLookAtPlayer(e, EntityHuman.class, 5.0f, 1.0f));
                            j++;
						}
			        } catch (Exception e)
			        {
			        	System.out.println("Exception with goals");
			        }
				}
			}.runTaskLater(plugin,1l);
		}
	}
}
