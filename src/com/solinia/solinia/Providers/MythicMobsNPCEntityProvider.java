package com.solinia.solinia.Providers;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.bukkit.Bukkit;

import com.solinia.solinia.Interfaces.INPCEntityProvider;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Interfaces.ISoliniaSpawnGroup;

public class MythicMobsNPCEntityProvider implements INPCEntityProvider {

	@Override
	public void updateNpc(ISoliniaNPC npc) {
		writeNpcDefinition("plugins/MythicMobs/Mobs/NPCID_" + npc.getId() + ".yml", npc);
		writeRandomSpawn("plugins/MythicMobs/RandomSpawns/RANDOMSPAWNNPCID_" + npc.getId() + ".yml", npc);
		writeCustomHead("plugins/MythicMobs/Items/CUSTOMHEADNPCID_" + npc.getId() + ".yml", npc);
		reloadProvider();
	}
	
	@Override
	public void updateSpawnGroup(ISoliniaSpawnGroup spawngroup) {
		writeSpawnerDefinition("plugins/MythicMobs/Spawners/SPAWNGROUPID_" + spawngroup.getId() + ".yml", spawngroup);
		reloadProvider();
	}
	
	@Override
	public void reloadProvider()
	{
		Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "mm reload");
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
	
	private void writeCustomHead(String fileName, ISoliniaNPC npc) {
		String fileData = createCustomHeadFile(npc);
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
	
	private void writeRandomSpawn(String fileName, ISoliniaNPC npc) {
		String fileData = createRandomSpawnFile(npc);
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
	
	private String createRandomSpawnFile(ISoliniaNPC npc) {
		String spawner = "";
		
		if (npc.isRandomSpawn())
		{
			String uniquename = "RANDOMNPCID_" + npc.getId();
			String mobtype = "NPCID_" + npc.getId();
	
			spawner = uniquename + ":\r\n";
			spawner += "  MobType: " + mobtype + "\r\n";
			spawner += "  Worlds: world\r\n";
			spawner += "  Chance: 0.05\r\n";
			spawner += "  Priority: 1\r\n";
			spawner += "  Action: replace\r\n";
		}
		return spawner;
	}

	private void writeSpawnerDefinition(String fileName, ISoliniaSpawnGroup spawngroup) {
		String fileData = createSpawnerFile(spawngroup);
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
	
	private String createSpawnerFile(ISoliniaSpawnGroup spawngroup) {
		String spawner = "";

		String uniquename = "SPAWNGROUPID_" + spawngroup.getId();
		String mobname = "NPCID_" + spawngroup.getNpcid();
		String world = spawngroup.getWorld();
		Double x = spawngroup.getX();
		Double y = spawngroup.getY();
		Double z = spawngroup.getZ();
		int respawnTime = spawngroup.getRespawntime();

		spawner = uniquename + ":\r\n";
		spawner += "  MobName: " + mobname + "\r\n";
		spawner += "  World: " + world + "\r\n";
		spawner += "  X: " + x + "\r\n";
		spawner += "  Y: " + y + "\r\n";
		spawner += "  Z: " + z + "\r\n";
		spawner += "  Radius: 0\r\n";
		spawner += "  UseTimer: true\r\n";
		spawner += "  MaxMobs: 1\r\n";
		spawner += "  MobLevel: 1\r\n";
		spawner += "  MobsPerSpawn: 1\r\n";
		// todo
		//spawner += "  Cooldown: 0\r\n";
		//spawner += "  CooldownTimer: 0\r\n";
		//spawner += "  Warmup: " + spawngroup.respawnseconds + "\r\n";
		//spawner += "  WarmupTimer: 0\r\n";
		spawner += "  Warmup: " + respawnTime + "\r\n";
		spawner += "  WarmupTimer: 0\r\n";
		spawner += "  CheckForPlayers: false\r\n";
		spawner += "  ActivationRange: 112\r\n";
		spawner += "  LeashRange: 112\r\n";
		spawner += "  HealOnLeash: true\r\n";
		spawner += "  ResetThreatOnLeash: true\r\n";
		spawner += "  ShowFlames: false\r\n";
		spawner += "  Breakable: false\r\n";
		spawner += "  Conditions: []\r\n";
		spawner += "  ActiveMobs: 0\r\n";
		return spawner;
	}
	
	public static String createCustomHeadFile(ISoliniaNPC npc) {
		String customheaditem = "";

		if (npc.isCustomhead())
		{
			customheaditem += "CUSTOMHEADNPCID_" + npc.getId() + ":\r\n";
			customheaditem += "  Id: 397\r\n";
			customheaditem += "  Data: 3\r\n";
			customheaditem += "  Options:\r\n";
			customheaditem += "    SkinTexture: " + npc.getCustomheaddata() + "\r\n";
		}

		return customheaditem;
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
