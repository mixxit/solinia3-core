package com.solinia.solinia.Adapters;

import com.solinia.solinia.Factories.SoliniaNPCFactory;
import com.solinia.solinia.Factories.SoliniaPlayerFactory;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Models.EQMob;
import com.solinia.solinia.Utils.EQUtils;

public class SoliniaNPCAdapter {
	public static ISoliniaNPC Adapt(EQMob entity, boolean commit) throws Exception {
		int raceId = 1;
		int factionId = 0;
		int classId = EQUtils.getSolClassIdFromEQClassId((int)entity.getClasses());
		ISoliniaNPC npc = SoliniaNPCFactory.CreateNPC(entity.getName(), (int)Math.floor(entity.getLevel()), raceId, classId, factionId, false, false, commit);
		String randomString = SoliniaPlayerFactory.getRandomNames(5, 1)[0];
		npc.setName(entity.getName()+"_"+randomString);
		
		// we need to get the class from the mapping
		npc.setClassid(classId);
		
		npc.setLevel((int)entity.getLevel());
		npc.setForcedMaxHp((int)entity.getHp());
		npc.setHpRegenRate((int)entity.getHp_regen_rate());
		npc.setMana((int)entity.getMana());
		npc.setManaRegenRate((int)entity.getMana_regen_rate());
		npc.setNpcSpellList((int)entity.getNpc_spells_id());
		npc.setMinInternalDmg((int)entity.getMindmg());
		npc.setMaxInternalDmg((int)entity.getMaxdmg());
		npc.setMagicresist((int)entity.getMR());
		npc.setColdresist((int)entity.getCR());
		npc.setDiseaseresist((int)entity.getDR());
		npc.setFireresist((int)entity.getFR());
		npc.setPoisonresist((int)entity.getPR());
		npc.setAC((int)entity.getAC());
		npc.setAtk((int)entity.getATK());
		npc.setAccuracyRating((int)entity.getAccuracy());
		npc.setAvoidanceRating((int)entity.getAvoidance());
		npc.setCanSeeInvis(false);
		if ((int)entity.getSee_invis() > 0)
			npc.setCanSeeInvis(true);
		return npc;
	}
}
