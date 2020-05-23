package com.solinia.solinia.Factories;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaNPC;

public class SoliniaNPCFactory {

	public static ISoliniaNPC CreateNPC(String name, int level, int raceid, int classid, int factionid, boolean reloadProvider) throws Exception {
		int lootTableId = 0;
		try
		{
			lootTableId = CreateLootTableAndLootDrop(name);
		} catch (Exception e)
		{
			throw new Exception("Failed to create NPC: " + e.getMessage());
		}
		
		SoliniaNPC npc = new SoliniaNPC();
		npc.setId(StateManager.getInstance().getConfigurationManager().getNextNPCId());
		npc.setName(name);
		npc.setRaceid(raceid);
		npc.setClassid(classid);
		npc.setLevel(level);
		npc.setFactionid(factionid);
		npc.setLoottableid(lootTableId);
		
		return StateManager.getInstance().getConfigurationManager().addNPC(npc, reloadProvider);
	}
	
	public static int CreateLootTableAndLootDrop(String name) throws CoreStateInitException, Exception
	{
		if (StateManager.getInstance().getConfigurationManager().getLootTable(name) != null)
			throw new Exception("Could not create loot table/lootdrop group as a loottable already exists with the same name");

		if (StateManager.getInstance().getConfigurationManager().getLootDrop(name) != null)
			throw new Exception("Could not create loot table/lootdrop group as a lootdrop already exists with the same name");
		
		SoliniaLootFactory.CreateLootTable(name);
		SoliniaLootFactory.CreateLootDrop(name);
		int lootDropId = StateManager.getInstance().getConfigurationManager().getLootDrop(name).getId();
		int lootTableId = StateManager.getInstance().getConfigurationManager().getLootTable(name).getId();
		SoliniaLootFactory.CreateLootTableDrop(lootTableId, lootDropId);
		
		return lootTableId;
	}

	public static ISoliniaNPC CreateNPCCopy(int npcid, String name, boolean reloadPlugin) throws Exception {
		
		ISoliniaNPC sourcenpc = StateManager.getInstance().getConfigurationManager().getNPC(npcid);
		
		if (sourcenpc == null)
			throw new Exception("Source NPC could not be found!");
		int lootTableId = 0;
		try
		{
			lootTableId = CreateLootTableAndLootDrop(name);
		} catch (Exception e)
		{
			throw new Exception("Failed to create NPC: " + e.getMessage());
		}
		
		SoliniaNPC npc = new SoliniaNPC();
		npc.setId(StateManager.getInstance().getConfigurationManager().getNextNPCId());
		npc.setName(name);
		npc.setBoss(sourcenpc.isBoss());
		npc.setRaidboss(sourcenpc.isRaidboss());
		npc.setRaidheroic(sourcenpc.isRaidheroic());
		npc.setAC(sourcenpc.getAC());
		npc.setAccuracyRating(sourcenpc.getAccuracyRating());
		npc.setAvoidanceRating(sourcenpc.getAvoidanceRating());
		npc.setAnimal(sourcenpc.isAnimal());
		npc.setUndead(sourcenpc.isUndead());
		npc.setClassid(sourcenpc.getClassid());
		npc.setRaceid(sourcenpc.getRaceid());
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
		// dont copy loottable use a newly generated one
		//npc.setLoottableid(sourcenpc.getLoottableid());
		npc.setLoottableid(lootTableId);
		
		npc.setMctype(sourcenpc.getMctype());
		npc.setMerchantid(sourcenpc.getMerchantid());
		npc.setOffhanditem(sourcenpc.getOffhanditem());
		npc.setRaceid(sourcenpc.getRaceid());
		npc.setRandomchatTriggerText(sourcenpc.getRandomchatTriggerText());
		npc.setRandomSpawn(sourcenpc.isRandomSpawn());
		npc.setRoamer(sourcenpc.isRoamer());
		npc.setUpsidedown(sourcenpc.isUpsidedown());
		npc.setUsedisguise(sourcenpc.isUsedisguise());
		return StateManager.getInstance().getConfigurationManager().addNPC(npc,reloadPlugin);
	}

}
