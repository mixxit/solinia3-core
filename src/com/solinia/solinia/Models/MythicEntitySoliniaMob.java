package com.solinia.solinia.Models;

import net.minecraft.server.v1_12_R1.EntitySkeleton;
import net.minecraft.server.v1_12_R1.World;

public class MythicEntitySoliniaMob extends EntitySkeleton {
	public int baseAttackRate = 20;
	public int meleeAttackPercent = 100;
	
    public MythicEntitySoliniaMob(World world) {
        super(world);
    }

    public int getMeleeAttackPercent()
    {
		return meleeAttackPercent;
    }
    
    public void setMeleeAttackPercent(int meleeAttackPercent)
    {
		this.meleeAttackPercent= meleeAttackPercent;
    }
    
    public int getAttackRate() {
    	int rate = (int)Math.ceil(baseAttackRate + ((100d - meleeAttackPercent) * (baseAttackRate / 100d)));
    	// Our lowest attack cap
    	if (rate < 2)
    		rate = 2;
    	
		return rate;
	}
	
	
}
