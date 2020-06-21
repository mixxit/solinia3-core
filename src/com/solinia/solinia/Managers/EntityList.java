package com.solinia.solinia.Managers;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;

public class EntityList {
	public ConcurrentHashMap<UUID,Boolean> mobList = new ConcurrentHashMap<UUID,Boolean>();
	public EntityList(List<UUID> mobList)
	{
		for(UUID uuid : mobList)
			this.mobList.put(uuid,true);
	}
	
	public void AEAttack(ISoliniaLivingEntity attacker, float dist, int Hand, int count, boolean IsFromSpell)
	{
		try
		{
			ISoliniaLivingEntity curmob = null;
	
			float dist2 = dist * dist;
	
			int hit = 0;
	
			for (UUID mobUUID : mobList.keySet()) {
				Entity mob = Bukkit.getEntity(mobUUID);
				
				if (mob == null || !(mob instanceof LivingEntity))
					return;
				
				curmob = SoliniaLivingEntityAdapter.Adapt((LivingEntity)mob);
				
				//curmob = it->second;
				if (curmob.isNPC()
						&& curmob != attacker //this is not needed unless NPCs can use this
						&&(attacker.isAttackAllowed(curmob))
						//&& curmob.getRaceId() != 216 && curmob.getRaceId() != 472 /* dont attack horses */
						&& (curmob.getLocation().distanceSquared(attacker.getLocation()) <= dist2)
				) {
					if (!attacker.isPlayer() || attacker.getClassObj() != null && (attacker.getClassObj().getName().equals("MONK") || attacker.getClassObj().getName().equals("RANGER")))
						attacker.Attack(curmob, Hand, false, false, IsFromSpell);
					else
						attacker.doAttackRounds(curmob, Hand, IsFromSpell);
					hit++;
					if (count != 0 && hit >= count)
						return;
				}
			}
		} catch (CoreStateInitException e)
		{
			
		}
	}
}
