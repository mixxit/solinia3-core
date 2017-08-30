package com.solinia.solinia.Providers;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.solinia.solinia.Interfaces.INPCEntityProvider;
import com.solinia.solinia.Interfaces.ISoliniaNPC;

public class MythicMobsNPCEntityProvider implements INPCEntityProvider {

	@Override
	public void updateNpc(ISoliniaNPC npc) {
		writeNpcDefinition("plugins/MythicMobs/Mobs/NPC_" + npc.getId() + ".yml", npc);
	}

	private void writeNpcDefinition(String fileName, ISoliniaNPC npc) {
		String fileData = createNpcFile(npc);
		try {
			FileOutputStream fooStream = new FileOutputStream(fileName, false);

			byte[] myBytes = fileData.getBytes();
			fooStream.write(myBytes);
			fooStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String createNpcFile(ISoliniaNPC npc) {
		String mob = "";

		String uniquename = "NPCID_" + npc.getId();

		if (npc.getMctype() == null)
			return "";
		
		mob = uniquename + ":\r\n";
		mob = mob + "  Type: " + npc.getMctype() + "\r\n";
		if (npc.isUpsidedown() == true) {
			mob = mob + "  Display: Dinnerbone\r\n";
		} else {
			mob = mob + "  Display: " + npc.getName() + "\r\n";
		}
		mob = mob + "  Health: " + npc.getMaxHP() + "\r\n";
		mob = mob + "  Damage: " + npc.getMaxDamage() + "\r\n";
		mob = mob + "  MovementSpeed: 0.2\r\n";
		mob = mob + "  MaxCombatDistance: 25\r\n";
		mob = mob + "  PreventOtherDrops: true\r\n";
		mob = mob + "  PreventRandomEquipment: true\r\n";
		mob = mob + "  Options:\r\n";
		mob = mob + "    PreventMobKillDrops: true\r\n";
		mob = mob + "    PreventOtherDrops: true\r\n";
		mob = mob + "    Silent: true\r\n";
		mob = mob + "    ShowHealth: true\r\n";
		mob = mob + "    PreventRenaming: true\r\n";
		mob = mob + "    PreventRandomEquipment: true\r\n";
		mob = mob + "    AlwaysShowName: true\r\n";
		mob = mob + "  Modules:\r\n";
		mob = mob + "    ThreatTable: true\r\n";
		
		// Act as normal mob if without faction
		if (npc.getFactionid() > 0)
		{
			mob = mob + "  AIGoalSelectors:\r\n";
			mob = mob + "  - 0 clear\r\n";
			mob = mob + "  - 1 skeletonbowattack\r\n";
			mob = mob + "  - 2 meleeattack\r\n";
			mob = mob + "  AITargetSelectors:\r\n";
			mob = mob + "  - 0 clear\r\n";
			mob = mob + "  - 1 attacker\r\n";
		}

		if (npc.getHeaditem() != null || npc.getChestitem() != null || npc.getLegsitem() != null
				|| npc.getFeetitem() != null || npc.getHanditem() != null || npc.getOffhanditem() != null) {
			mob = mob + "  Equipment:\r\n";
			if (npc.getOffhanditem() != null)
				mob = mob + "  - " + npc.getOffhanditem() + ":5\r\n";

			if (npc.isCustomhead() == true) {
				if (npc.getCustomheaddata() != null) {
					mob = mob + "  - CUSTOMHEADNPCID_" + npc.getId() + ":4\r\n";
				} else {
					if (npc.getHeaditem() != null)
						mob = mob + "  - " + npc.getHeaditem() + ":4\r\n";
				}
			} else {
				if (npc.getHeaditem() != null)
					mob = mob + "  - " + npc.getHeaditem() + ":4\r\n";
			}

			if (npc.getChestitem() != null)
				mob = mob + "  - " + npc.getChestitem() + ":3\r\n";
			if (npc.getLegsitem() != null)
				mob = mob + "  - " + npc.getLegsitem() + ":2\r\n";
			if (npc.getFeetitem() != null)
				mob = mob + "  - " + npc.getFeetitem() + ":1\r\n";
			if (npc.getHanditem() != null)
				mob = mob + "  - " + npc.getHanditem() + ":0\r\n";
		}

		if (npc.isUsedisguise() == true) {
			mob = mob + "  Disguise:\r\n";
			if (npc.getDisguisetype().toLowerCase().contains("player-")) {
				mob = mob + "    Type: player\r\n";
			} else {
				mob = mob + "    Type: " + npc.getDisguisetype() + "\r\n";
			}
			if (npc.isBurning() == true) {
				mob = mob + "    Burning: true\r\n";
			}
			if (npc.getDisguisetype().toLowerCase().contains("player-")) {
				String[] disguisedata = npc.getDisguisetype().split("-");
				mob = mob + "    Player: " + npc.getName() + "\r\n";
				mob = mob + "    Skin: '" + disguisedata[1] + "'\r\n";
			}
		}

		if (npc.isBoss() == true) {
			mob = mob + "  BossBar:\r\n";
			mob = mob + "    Enabled: true\r\n";
			mob = mob + "    Title: " + npc.getName() + "\r\n";
			mob = mob + "    Range: 200\r\n";
			mob = mob + "    CreateFog: true\r\n";
			mob = mob + "    DarkenSky: true\r\n";
			mob = mob + "    PlayMusic: true\r\n";
			if (npc.getDisguisetype().toLowerCase().contains("player-")) {
				String[] disguisedata = npc.getDisguisetype().split("-");
				mob = mob + "    Player: " + npc.getName() + "\r\n";
				mob = mob + "    Skin: '" + disguisedata[1] + "'\r\n";
			}
		}

		if (npc.isInvisible() == true) {
			mob = mob + "  - potion{t=INVISIBILITY;d=2147483647;l=1} @self ~onSpawn\r\n";
		}

		return mob;
	}

}
