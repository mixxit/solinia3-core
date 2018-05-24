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
    	try
    	{
    	if (this.b == null)
    		return 20;
    	
    	if (!(this.b instanceof MythicEntitySoliniaMob))
    		return 20;
    	
    	return ((MythicEntitySoliniaMob)this.b).getAttackRate();
    	} catch (Exception e)
    	{
    		System.out.println("Exception during MeleeAttack pathfinder (getAttackTickSpeed): " + e.getMessage() + " " + e.getStackTrace());
    		return 20;
    	}
    }
    
    @Override
    protected void a(EntityLiving entityliving, double d0) {
    	try
    	{
	    	if (entityliving == null)
	    		return;
	    	
	    	if (this.b == null)
	    		return;
	    	
	        double d1 = this.a(entityliving);
	
	        if (d0 <= d1 && this.c <= 0) {
	            this.c = getAttackTickSpeed();
	            
	            this.b.a(EnumHand.MAIN_HAND);
	            this.b.B(entityliving);
	        }
    	} catch (Exception e)
    	{
    		System.out.println("Exception during MeleeAttack pathfinder (a): " + e.getMessage() + " " + e.getStackTrace());
    	}
    }
}
