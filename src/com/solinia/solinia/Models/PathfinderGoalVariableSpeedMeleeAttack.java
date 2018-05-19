package com.solinia.solinia.Models;

import org.bukkit.Bukkit;

import net.minecraft.server.v1_12_R1.EntityCreature;
import net.minecraft.server.v1_12_R1.EntityLiving;
import net.minecraft.server.v1_12_R1.EnumHand;
import net.minecraft.server.v1_12_R1.PathfinderGoalMeleeAttack;

public class PathfinderGoalVariableSpeedMeleeAttack extends PathfinderGoalMeleeAttack 
{
	
    public PathfinderGoalVariableSpeedMeleeAttack(MythicEntitySoliniaMob entitycreature, double speedIn, boolean useLongMemory) {
        super(entitycreature, speedIn, useLongMemory);
    }
    
    public int getAttackTickSpeed()
    {
    	if (this.b == null)
    		return 20;
    	
    	if (!(this.b instanceof MythicEntitySoliniaMob))
    		return 20;
    	
    	return ((MythicEntitySoliniaMob)this.b).getAttackRate();
    }
    
    @Override
    protected void a(EntityLiving entityliving, double d0) {
        double d1 = this.a(entityliving);

        if (d0 <= d1 && this.c <= 0) {
            this.c = getAttackTickSpeed();
            
            this.b.a(EnumHand.MAIN_HAND);
            this.b.B(entityliving);
        }
    }
}
