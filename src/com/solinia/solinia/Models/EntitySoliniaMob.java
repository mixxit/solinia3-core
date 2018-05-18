package com.solinia.solinia.Models;

import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;

import net.minecraft.server.v1_12_R1.EntitySkeleton;

public class EntitySoliniaMob extends EntitySkeleton {

	public EntitySoliniaMob(org.bukkit.World world)
    {
        super(((CraftWorld)world).getHandle());
    }
	/**
     * Overrides the entity's combat AI.
     */
	/*
    public void setCombatTask()
    {
        if (this.world != null && !this.world.isClientSide)
        {
            this.tasks.removeTask(this.aiAttackOnCollide);
            this.tasks.removeTask(this.aiArrowAttack);
            net.minecraft.server.v1_12_R1.ItemStack itemstack = this.getHeldItemMainhand();

            if (itemstack.getItem() == net.minecraft.server.v1_12_R1.Items.BOW)
            {
                int i = 20;

                if (this.world.getDifficulty() != net.minecraft.server.v1_12_R1.EnumDifficulty.HARD)
                {
                    i = 40;
                }

                this.aiArrowAttack.setAttackCooldown(i);
                this.tasks.addTask(4, this.aiArrowAttack);
            }
            else
            {
                this.tasks.addTask(4, this.aiAttackOnCollide);
            }
        }
    }
*/
}
