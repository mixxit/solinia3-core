package com.solinia.solinia.Goals;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.AbstractLocation;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import net.minecraft.server.v1_13_R2.EntityCreature;
import net.minecraft.server.v1_13_R2.EntityInsentient;
import net.minecraft.server.v1_13_R2.PathfinderGoal;;

public class PathfinderGoalGoToOwner
extends PathfinderGoal {
    private double speed;
    private EntityInsentient entity;

    public PathfinderGoalGoToOwner(EntityInsentient entity, double speed) {
        this.entity = entity;
        this.speed = speed;
    }

    public boolean a() {
        try {
            ActiveMob am = MythicMobs.inst().getMobManager().getMythicMobInstance((Entity)((LivingEntity)this.entity.getBukkitEntity()));
            if (!am.getOwner().isPresent()) {
                return false;
            }
            
            if (this.entity instanceof EntityCreature && ((EntityCreature)this.entity).getGoalTarget() != null)
            	return false; 
            
            Player owner = Bukkit.getPlayer((UUID)am.getOwner().get());
            if (owner == null) {
                return false;
            }
            AbstractLocation destination = BukkitAdapter.adapt(owner.getLocation());
            AbstractLocation eLocation = new AbstractLocation(destination.getWorld(), this.entity.locX, this.entity.locY, this.entity.locZ);
            if (eLocation.distanceSquared(destination) > 1024.0) {
                am.getEntity().teleport(destination);
                return true;
            }
            if (eLocation.distanceSquared(destination) > this.speed) {
                this.entity.getNavigation().a(destination.getX(), destination.getY(), destination.getZ(), 1.0);
                return true;
            }
            return false;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}