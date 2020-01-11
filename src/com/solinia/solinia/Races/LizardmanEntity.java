// big help 
// https://github.com/minecraftabnormals/The-Endergetic-Expansion/blob/92b7f280dd6fd3255b6ef7c3c329e222b0844f39/src/main/java/endergeticexpansion/common/entities/EntityPoiseCluster.java
package com.solinia.solinia.Races;

import java.lang.reflect.Field;

import org.bukkit.entity.Villager.Profession;

import com.google.common.collect.Sets;

import net.minecraft.server.v1_14_R1.EntityTypes;
import net.minecraft.server.v1_14_R1.EntityVillager;
import net.minecraft.server.v1_14_R1.PathfinderGoalSelector;
import net.minecraft.server.v1_14_R1.VillagerType;
import net.minecraft.server.v1_14_R1.World;

public class LizardmanEntity extends EntityVillager 
{
    public LizardmanEntity(EntityTypes<? extends EntityVillager> entitytypes, World w) {
        super(entitytypes, w);
        clearPathfinders();

        //this.goalSelector.a(10, new PathfinderGoalFollowPlayer(this, 1.0D, 2.0F, 2.0F));
    }
    
    public LizardmanEntity(World w, Profession profession) {
        super(EntityTypes.VILLAGER, w);
        //clearPathfinders();
        this.setVillagerData(this.getVillagerData().withType(VillagerType.c).withProfession(VillagerUtils.fromBukkitProfession(profession)));
        //this.goalSelector.a(10, new PathfinderGoalFollowPlayer(this, 1.0D, 2.0F, 2.0F));
    }
    /*
    private void clearPathfinders() {
        try {
            Field bField = PathfinderGoalSelector.class.getDeclaredField("b");
            bField.setAccessible(true);
            Field cField = PathfinderGoalSelector.class.getDeclaredField("c");
            cField.setAccessible(true);
            bField.set(goalSelector, Sets.newLinkedHashSet());
            bField.set(targetSelector, Sets.newLinkedHashSet());
            cField.set(goalSelector, Sets.newLinkedHashSet());
            cField.set(targetSelector, Sets.newLinkedHashSet());
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }*/
}