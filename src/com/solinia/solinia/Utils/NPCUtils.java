package com.solinia.solinia.Utils;

import java.io.IOException;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidNpcSettingException;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Interfaces.ISoliniaSpawnGroup;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaNPC;

public class NPCUtils {
	public static int getDefaultNPCHPRegen(SoliniaNPC npc) {
		int baseHpRegen = (npc.getLevel() / 10)/2;
		
		if (npc.isBoss())
			return npc.getLevel() / 8;
		if (npc.isHeroic())
			return npc.getLevel() / 9;
		if (npc.isRaidboss())
			return npc.getLevel() / 5;
		if (npc.isRaidheroic())
			return npc.getLevel() / 8;
		return baseHpRegen;
	}


	public static void RecommitSpawnGroups() {
		try {
			System.out.println("Recommiting all SpawnGroups via provider");
			for (ISoliniaSpawnGroup spawngroup : StateManager.getInstance().getConfigurationManager().getSpawnGroups()) {
				try {
					
					StateManager.getInstance().getEntityManager().getNPCEntityProvider().updateSpawnGroup(spawngroup);
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (CoreStateInitException e) {

		}
	}
	
	public static void RecommitNpcs() {
		try {
			System.out.println("Recommiting all NPCs via provider");
			for (ISoliniaNPC npc : StateManager.getInstance().getConfigurationManager().getNPCs()) {
				try {
					npc.editSetting("name", npc.getName());
					StateManager.getInstance().getEntityManager().getNPCEntityProvider().updateNpc(npc);
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvalidNpcSettingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (CoreStateInitException e) {

		}
	}
	
	// Heroic

		public static float getHeroicRunSpeed() {
			// TODO Auto-generated method stub
			return 0.4f;
		}

		public static int getHeroicDamageMultiplier() {
			// TODO Auto-generated method stub
			return 4;
		}

		public static int getHeroicHPMultiplier() {
			// TODO Auto-generated method stub
			return 20;
		}

		// Boss

		public static float getBossRunSpeed() {
			// TODO Auto-generated method stub
			return 0.4f;
		}

		public static int getBossDamageMultiplier(boolean heroicBoss) {
			// TODO Auto-generated method stub
			if (heroicBoss)
				return getHeroicDamageMultiplier() * 6;
			else
				return getHeroicDamageMultiplier() * 2;
		}

		public static int getBossHPMultiplier(boolean heroicBoss) {
			// TODO Auto-generated method stub
			if (heroicBoss)
				return getHeroicHPMultiplier() * 6;
			else
				return getHeroicHPMultiplier() * 2;
		}

		// Raid Heroic

		public static float getRaidHeroicRunSpeed() {
			// TODO Auto-generated method stub
			return 0.4f;
		}

		public static int getRaidHeroicDamageMultiplier() {
			// TODO Auto-generated method stub
			return 10;
		}

		public static int getRaidHeroicHPMultiplier() {
			// TODO Auto-generated method stub
			return 200;
		}

		// Raid Boss

		public static float getRaidBossRunSpeed() {
			// TODO Auto-generated method stub
			return 0.5f;
		}

		public static int getRaidBossDamageMultiplier() {
			// TODO Auto-generated method stub
			return 30;
		}

		public static int getRaidBossHPMultiplier() {
			// TODO Auto-generated method stub
			return 1000;
		}

	
	public static Integer getDefaultNPCManaRegen(ISoliniaNPC npc) {
		int baseMpRegen = npc.getLevel() / 35;
		if (npc.isBoss())
			return npc.getLevel() / 15;
		if (npc.isHeroic())
			return npc.getLevel() / 25;
		if (npc.isRaidboss())
			return npc.getLevel() / 3;
		if (npc.isRaidheroic())
			return npc.getLevel() / 15;
		return baseMpRegen;
	}
	
	public static Integer getDefaultNPCResist(ISoliniaNPC npc) {
		int base = 10 + (npc.getLevel() / 2);
		if (npc.isBoss())
			return 30 + (npc.getLevel() / 2);
		if (npc.isHeroic())
			return 20 + (npc.getLevel() / 2);
		if (npc.isRaidboss())
			return 10 + (npc.getLevel() * 2);
		if (npc.isRaidheroic())
			return 30 + (npc.getLevel() / 2);
		return base;
	}
	
	public static Integer getDefaultNPCMana(ISoliniaNPC npc) {
		return getDefaultNPCManaRegen(npc) * 1500;
	}
}
