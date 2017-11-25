package com.solinia.solinia.Factories;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaNPC;

public class SoliniaNPCFactory {

	public static ISoliniaNPC CreateNPC(String name, int level, int factionid) throws CoreStateInitException {
		SoliniaNPC npc = new SoliniaNPC();
		npc.setId(StateManager.getInstance().getConfigurationManager().getNextNPCId());
		npc.setName(name);
		npc.setLevel(level);
		npc.setFactionid(factionid);
		return StateManager.getInstance().getConfigurationManager().addNPC(npc);
	}

	public static ISoliniaNPC CreateNPCCopy(int npcid, String name) throws CoreStateInitException {
		ISoliniaNPC sourcenpc = StateManager.getInstance().getConfigurationManager().getNPC(npcid);
		
		SoliniaNPC npc = new SoliniaNPC();
		npc.setId(StateManager.getInstance().getConfigurationManager().getNextNPCId());
		npc.setName(name);
		npc.setBoss(sourcenpc.isBoss());
		npc.setHeroic(sourcenpc.isHeroic());
		npc.setBurning(sourcenpc.isBurning());
		npc.setChestitem(sourcenpc.getChestitem());
		npc.setClassid(sourcenpc.getClassid());
		npc.setCustomhead(sourcenpc.isCustomhead());
		npc.setCustomheaddata(sourcenpc.getCustomheaddata());
		npc.setDisguisetype(sourcenpc.getDisguisetype());
		npc.setFactionid(sourcenpc.getFactionid());
		npc.setFeetitem(sourcenpc.getFeetitem());
		npc.setGuard(sourcenpc.isGuard());
		npc.setHanditem(sourcenpc.getHanditem());
		npc.setHeaditem(sourcenpc.getHeaditem());
		npc.setInvisible(sourcenpc.isInvisible());
		npc.setKillTriggerText(sourcenpc.getKillTriggerText());
		npc.setLegsitem(sourcenpc.getLegsitem());
		npc.setLevel(sourcenpc.getLevel());
		npc.setLoottableid(sourcenpc.getLoottableid());
		npc.setMctype(sourcenpc.getMctype());
		npc.setMerchantid(sourcenpc.getMerchantid());
		npc.setOffhanditem(sourcenpc.getOffhanditem());
		npc.setRaceid(sourcenpc.getRaceid());
		npc.setRandomchatTriggerText(sourcenpc.getRandomchatTriggerText());
		npc.setRandomSpawn(sourcenpc.isRandomSpawn());
		npc.setRoamer(sourcenpc.isRoamer());
		npc.setUpsidedown(sourcenpc.isUpsidedown());
		npc.setUsedisguise(sourcenpc.isUsedisguise());
		return StateManager.getInstance().getConfigurationManager().addNPC(npc);
	}

}
