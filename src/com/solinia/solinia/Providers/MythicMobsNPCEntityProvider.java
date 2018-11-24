package com.solinia.solinia.Providers;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.bukkit.Bukkit;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.INPCEntityProvider;
import com.solinia.solinia.Interfaces.ISoliniaFaction;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaLootDrop;
import com.solinia.solinia.Interfaces.ISoliniaLootDropEntry;
import com.solinia.solinia.Interfaces.ISoliniaLootTable;
import com.solinia.solinia.Interfaces.ISoliniaLootTableEntry;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Interfaces.ISoliniaSpawnGroup;
import com.solinia.solinia.Managers.ConfigurationManager;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Utils.Utils;

public class MythicMobsNPCEntityProvider implements INPCEntityProvider {

	@Override
	public void updateNpc(ISoliniaNPC npc) {
		writeNpcDefinition("plugins/MythicMobs/Mobs/NPCID_" + npc.getId() + ".yml", npc);
		writeRandomSpawn("plugins/MythicMobs/RandomSpawns/RANDOMSPAWNNPCID_" + npc.getId() + ".yml", npc);
		writeCustomHead("plugins/MythicMobs/Items/CUSTOMHEADNPCID_" + npc.getId() + ".yml", npc);
	}

	@Override
	public void updateSpawnGroup(ISoliniaSpawnGroup spawngroup) {
		Utils.dispatchCommandLater(Bukkit.getPluginManager().getPlugin("Solinia3Core"),
				"mm spawners delete SPAWNGROUPID_" + spawngroup.getId());
		writeSpawnerDefinition("plugins/MythicMobs/Spawners/SPAWNGROUPID_" + spawngroup.getId() + ".yml", spawngroup);
	}

	@Override
	public void reloadProvider() {
		Utils.dispatchCommandLater(Bukkit.getPluginManager().getPlugin("Solinia3Core"), "mm reload");
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

	private void writeCustomItem(String fileName, ISoliniaItem item) {
		String fileData = createCustomItemFile(item);
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

		if (npc.isRandomSpawn()) {
			String uniquename = "RANDOMNPCID_" + npc.getId();
			String mobtype = "NPCID_" + npc.getId();

			spawner = uniquename + ":\r\n";
			spawner += "  MobType: " + mobtype + "\r\n";
			spawner += "  Worlds: world\r\n";
			spawner += "  Chance: 1\r\n";
			spawner += "  Priority: 1\r\n";
			spawner += "  Action: add\r\n";
		}
		return spawner;
	}

	private void writeSpawnerDefinition(String fileName, ISoliniaSpawnGroup spawngroup) {
		String fileData = createSpawnerFile(spawngroup);

		if (spawngroup.isDisabled()) {
			fileData = "";
		}

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
		// spawner += " Cooldown: 0\r\n";
		// spawner += " CooldownTimer: 0\r\n";
		// spawner += " Warmup: " + spawngroup.respawnseconds + "\r\n";
		// spawner += " WarmupTimer: 0\r\n";
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

	public String createCustomHeadFile(ISoliniaNPC npc) {
		String customheaditem = "";

		if (npc.isCustomhead()) {
			customheaditem += "CUSTOMHEADNPCID_" + npc.getId() + ":\r\n";
			customheaditem += "  Id: 397\r\n";
			customheaditem += "  Data: 3\r\n";
			customheaditem += "  Options:\r\n";
			customheaditem += "    SkinTexture: " + npc.getCustomheaddata() + "\r\n";
		}

		return customheaditem;
	}

	public String createCustomItemFile(ISoliniaItem item) {
		String customitem = "";

		customitem += "CUSTOMITEMID_" + item.getId() + ":\r\n";
		customitem += "  Id: " + item.asItemStack().getType().name() + "\r\n";
		customitem += "  Display: '" + item.getDisplayname().replaceAll("[^a-zA-Z0-9]", "") + "'\r\n";
		if (item.getBasename().toUpperCase().equals("SHIELD"))
			customitem += "  Color: SILVER\r\n";
		customitem += "  Data: " + item.getColor() + "\r\n";
		if (item.getDamage() > 0) {
			customitem += "  Damage: " + item.getDamage() + "\r\n";
		}
		customitem += "  Enchantments:\r\n";
		customitem += "    DURABILITY: " + (1000 + item.getId()) + "\r\n";
		return customitem;
	}

	public String createNpcFile(ISoliniaNPC npc) {
		String mob = "";

		String uniquename = "NPCID_" + npc.getId();

		if (npc.getMctype() == null)
			return "";

		mob = uniquename + ":\r\n";
		mob = mob + "  Type: SKELETON\r\n";
		if (npc.isUpsidedown() == true) {
			mob = mob + "  Display: Dinnerbone\r\n";
		} else {
			mob = mob + "  Display: " + npc.getName() + "\r\n";
		}

		double hp = Utils.getStatMaxHP(npc.getClassObj(), npc.getLevel(), 75);
		double damage = Utils.getNPCDefaultDamage(npc);

		if (npc.isHeroic()) {
			hp += (Utils.getHeroicHPMultiplier() * npc.getLevel());
		}

		if (npc.isBoss()) {
			hp += (Utils.getBossHPMultiplier() * npc.getLevel());
		}

		if (npc.isRaidheroic()) {
			hp += (Utils.getRaidHeroicHPMultiplier() * npc.getLevel());
		}

		if (npc.isRaidboss()) {
			hp += (Utils.getRaidBossHPMultiplier() * npc.getLevel());
		}

		float movementSpeed = 0.3f;
		if (npc.isHeroic()) {
			movementSpeed = Utils.getHeroicRunSpeed();
		}

		if (npc.isBoss()) {
			movementSpeed = Utils.getBossRunSpeed();
		}

		if (npc.isRaidheroic()) {
			movementSpeed = Utils.getRaidHeroicRunSpeed();
		}

		if (npc.isRaidboss()) {
			movementSpeed = Utils.getRaidBossRunSpeed();
		}

		mob = mob + "  Health: " + hp + "\r\n";
		mob = mob + "  Damage: " + damage + "\r\n";
		mob = mob + "  PreventOtherDrops: true\r\n";
		mob = mob + "  PreventRandomEquipment: true\r\n";
		mob = mob + "  Options:\r\n";
		mob = mob + "    MaxCombatDistance: "+Utils.MAX_ENTITY_AGGRORANGE+"\r\n";
		mob = mob + "    FollowRange: "+Utils.MAX_ENTITY_AGGRORANGE+"\r\n";
		mob = mob + "    MovementSpeed: " + movementSpeed + "\r\n";
		mob = mob + "    KnockbackResistance: 1\r\n";
		mob = mob + "    PreventMobKillDrops: true\r\n";
		mob = mob + "    PreventOtherDrops: true\r\n";
		mob = mob + "    Silent: true\r\n";
		mob = mob + "    ShowHealth: true\r\n";
		mob = mob + "    PreventRenaming: true\r\n";
		mob = mob + "    PreventRandomEquipment: true\r\n";
		mob = mob + "    AlwaysShowName: true\r\n";
		mob = mob + "  Modules:\r\n";
		mob = mob + "    ThreatTable: false\r\n";

		if (npc.isPet()) {
			mob = mob + "  Faction: FACTIONID_-1\r\n";
		} else {
			mob = mob + "  Faction: FACTIONID_" + npc.getFactionid() + "\r\n";
		}
		
		// KOS attack everything!!
		if (npc.getFactionid() == 0)
		{
			mob = mob + "  AIGoalSelectors:\r\n";
			mob = mob + "  - 0 clear\r\n";
			mob = mob + "  - 1 skeletonbowattack\r\n";
			mob = mob + "  - 2 meleeattack\r\n";
			mob = mob + "  - 3 lookatplayers\r\n";
			mob = mob + "  - 4 randomstroll\r\n";
			mob = mob + "  AITargetSelectors:\r\n";
			mob = mob + "  - 0 clear\r\n";
			mob = mob + "  - 1 attacker\r\n";
			mob = mob + "  - 2 players\r\n";
			int curnum = 4;
			try
			{
				for (ISoliniaFaction faction : StateManager.getInstance().getConfigurationManager().getFactions()) {
					mob = mob + "  - " + curnum + " SpecificFaction FACTIONID_" + faction.getId() + "\r\n";
					curnum++;
				}
			} catch (CoreStateInitException e)
			{
				
			}
		}

		// Act as normal mob if without faction
		if (npc.getFactionid() > 0 || npc.isPet()) {
			mob = mob + "  AIGoalSelectors:\r\n";
			mob = mob + "  - 0 clear\r\n";
			mob = mob + "  - 1 skeletonbowattack\r\n";
			mob = mob + "  - 2 meleeattack\r\n";
			mob = mob + "  - 3 lookatplayers\r\n";
			if (npc.isRoamer()) {
				mob = mob + "  - 4 randomstroll\r\n";
			}
			mob = mob + "  AITargetSelectors:\r\n";
			mob = mob + "  - 0 clear\r\n";
			mob = mob + "  - 1 attacker\r\n";

			// NPC attack players
			if (!npc.isPet()) {
				try {
					ISoliniaFaction npcfaction = StateManager.getInstance().getConfigurationManager()
							.getFaction(npc.getFactionid());
					if (npcfaction.getBase() == -1500) {
						mob = mob + "  - 2 players\r\n";
					}
				} catch (CoreStateInitException e) {
					// skip
				}
			}

			// NPC attack NPCs
			if (npc.isGuard() || npc.isPet()) {
				// Always attack mobs with factionid 0
				mob = mob + "  - 3 SpecificFaction FACTIONID_0\r\n";
				// Attack all mobs with -1500 faction
				try {
					int curnum = 4;
					for (ISoliniaFaction faction : StateManager.getInstance().getConfigurationManager().getFactions()) {
						if (faction.getBase() == -1500 && faction.getId() != npc.getFactionid()) {
							mob = mob + "  - " + curnum + " SpecificFaction FACTIONID_" + faction.getId() + "\r\n";
							curnum++;
						}
					}
				} catch (CoreStateInitException e) {
					// skip
				}
			}

		}

		// Here's the fun part, if the npc has decent loot in his loot drops to use for
		// himself
		// then go ahead and use it! Better than that crap we have right?
		if (npc.getLoottableid() > 0) {
			try {
				ISoliniaLootTable lootTable = StateManager.getInstance().getConfigurationManager()
						.getLootTable(npc.getLoottableid());
				if (lootTable != null) {
					List<ISoliniaItem> potentialChestArmour = new ArrayList<ISoliniaItem>();
					List<ISoliniaItem> potentialLegsArmour = new ArrayList<ISoliniaItem>();
					List<ISoliniaItem> potentialFeetArmour = new ArrayList<ISoliniaItem>();
					List<ISoliniaItem> potentialWeapons = new ArrayList<ISoliniaItem>();
					List<ISoliniaItem> potentialBows = new ArrayList<ISoliniaItem>();
					List<ISoliniaItem> potentialShields = new ArrayList<ISoliniaItem>();

					for (ISoliniaLootTableEntry loottableentry : lootTable.getEntries()) {
						ISoliniaLootDrop lootdrop = StateManager.getInstance().getConfigurationManager()
								.getLootDrop(loottableentry.getLootdropid());
						for (ISoliniaLootDropEntry lootdropentry : lootdrop.getEntries()) {
							ISoliniaItem item = StateManager.getInstance().getConfigurationManager()
									.getItem(lootdropentry.getItemid());
							if (ConfigurationManager.ArmourMaterials.contains(item.getBasename().toUpperCase())) {
								if (item.getAllowedClassNames().size() == 0) {
									if (item.getBasename().contains("CHESTPLATE"))
										potentialChestArmour.add(item);
									if (item.getBasename().contains("LEGGINGS"))
										potentialLegsArmour.add(item);
									if (item.getBasename().contains("BOOTS"))
										potentialFeetArmour.add(item);
									continue;
								}

								if (npc.getClassObj() != null) {
									if (item.getAllowedClassNames().contains(npc.getClassObj().getName())) {
										if (item.getBasename().contains("CHESTPLATE"))
											potentialChestArmour.add(item);
										if (item.getBasename().contains("LEGGINGS"))
											potentialLegsArmour.add(item);
										if (item.getBasename().contains("BOOTS"))
											potentialFeetArmour.add(item);
										continue;
									}
								}

							}

							if (ConfigurationManager.WeaponMaterials.contains(item.getBasename().toUpperCase())) {
								if (item.getAllowedClassNames().size() == 0) {
									if (item.getBasename().contains("SHIELD"))
										potentialShields.add(item);
									else if (item.getBasename().contains("BOW"))
										potentialBows.add(item);
									else
										potentialWeapons.add(item);
									continue;
								}

								if (npc.getClassObj() != null) {
									if (item.getAllowedClassNames().contains(npc.getClassObj().getName())) {
										if (item.getBasename().contains("SHIELD"))
											potentialShields.add(item);
										else if (item.getBasename().contains("BOW"))
											potentialBows.add(item);
										else
											potentialWeapons.add(item);
										continue;
									}
								}
							}

							if (ConfigurationManager.HandMaterials.contains(item.getBasename().toUpperCase())) {
								if (item.getAllowedClassNames().size() == 0) {
									if (item.getBasename().contains("SHIELD"))
										potentialShields.add(item);
									else if (item.getBasename().contains("BOW"))
										potentialBows.add(item);
									else
										potentialWeapons.add(item);
									continue;
								}

								if (npc.getClassObj() != null) {
									if (item.getAllowedClassNames().contains(npc.getClassObj().getName())) {
										if (item.getBasename().contains("SHIELD"))
											potentialShields.add(item);
										else if (item.getBasename().contains("BOW"))
											potentialBows.add(item);
										else
											potentialWeapons.add(item);
										continue;
									}
								}
							}
						}

						mob = mob + "  Equipment:\r\n";

						if (potentialShields.size() > 0) {
							Collections.sort(potentialShields, new Comparator<ISoliniaItem>() {
								public int compare(ISoliniaItem o1, ISoliniaItem o2) {
									if (o1.getMinLevel() == o2.getMinLevel())
										return 0;

									return o1.getMinLevel() > o2.getMinLevel() ? -1 : 1;
								}
							});

							Collections.reverse(potentialShields);

							writeCustomItem("plugins/MythicMobs/Items/CUSTOMITEMID" + potentialShields.get(0).getId() + ".yml",potentialShields.get(0));
							mob = mob + "  - " + "CUSTOMITEMID_" + potentialShields.get(0).getId() + ":5\r\n";
						} else {
							if (npc.getOffhanditem() != null)
								mob = mob + "  - " + npc.getOffhanditem() + ":5\r\n";
						}

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

						if (potentialChestArmour.size() > 0) {
							Collections.sort(potentialChestArmour, new Comparator<ISoliniaItem>() {
								public int compare(ISoliniaItem o1, ISoliniaItem o2) {
									if (o1.getMinLevel() == o2.getMinLevel())
										return 0;

									return o1.getMinLevel() > o2.getMinLevel() ? -1 : 1;
								}
							});

							Collections.reverse(potentialChestArmour);

							writeCustomItem("plugins/MythicMobs/Items/CUSTOMITEMID"
									+ potentialChestArmour.get(0).getId() + ".yml", potentialChestArmour.get(0));
							mob = mob + "  - " + "CUSTOMITEMID_" + potentialChestArmour.get(0).getId() + ":3\r\n";
						} else {
							if (npc.getChestitem() != null)
								mob = mob + "  - " + npc.getChestitem() + ":3\r\n";
						}

						if (potentialLegsArmour.size() > 0) {
							Collections.sort(potentialLegsArmour, new Comparator<ISoliniaItem>() {
								public int compare(ISoliniaItem o1, ISoliniaItem o2) {
									if (o1.getMinLevel() == o2.getMinLevel())
										return 0;

									return o1.getMinLevel() > o2.getMinLevel() ? -1 : 1;
								}
							});

							Collections.reverse(potentialLegsArmour);

							writeCustomItem("plugins/MythicMobs/Items/CUSTOMITEMID" + potentialLegsArmour.get(0).getId()
									+ ".yml", potentialLegsArmour.get(0));
							mob = mob + "  - " + "CUSTOMITEMID_" + potentialLegsArmour.get(0).getId() + ":2\r\n";
						} else {
							if (npc.getLegsitem() != null)
								mob = mob + "  - " + npc.getLegsitem() + ":2\r\n";
						}

						if (potentialFeetArmour.size() > 0) {
							Collections.sort(potentialFeetArmour, new Comparator<ISoliniaItem>() {
								public int compare(ISoliniaItem o1, ISoliniaItem o2) {
									if (o1.getMinLevel() == o2.getMinLevel())
										return 0;

									return o1.getMinLevel() > o2.getMinLevel() ? -1 : 1;
								}
							});

							Collections.reverse(potentialFeetArmour);

							writeCustomItem("plugins/MythicMobs/Items/CUSTOMITEMID" + potentialFeetArmour.get(0).getId()
									+ ".yml", potentialFeetArmour.get(0));
							mob = mob + "  - " + "CUSTOMITEMID_" + potentialFeetArmour.get(0).getId() + ":1\r\n";
						} else {
							if (npc.getFeetitem() != null)
								mob = mob + "  - " + npc.getFeetitem() + ":1\r\n";
						}

						if (npc.getClassObj() != null && npc.getClassObj().getName().toUpperCase().equals("RANGER")
								&& potentialBows.size() > 0) {
							Collections.sort(potentialBows, new Comparator<ISoliniaItem>() {
								public int compare(ISoliniaItem o1, ISoliniaItem o2) {
									if (o1.getMinLevel() == o2.getMinLevel())
										return 0;

									return o1.getMinLevel() > o2.getMinLevel() ? -1 : 1;
								}
							});

							Collections.reverse(potentialBows);

							writeCustomItem(
									"plugins/MythicMobs/Items/CUSTOMITEMID" + potentialBows.get(0).getId() + ".yml",
									potentialBows.get(0));
							mob = mob + "  - " + "CUSTOMITEMID_" + potentialBows.get(0).getId() + ":0\r\n";
						} else {
							if (potentialWeapons.size() > 0) {
								Collections.sort(potentialWeapons, new Comparator<ISoliniaItem>() {
									public int compare(ISoliniaItem o1, ISoliniaItem o2) {
										if (o1.getMinLevel() == o2.getMinLevel())
											return 0;

										return o1.getMinLevel() > o2.getMinLevel() ? -1 : 1;
									}
								});

								Collections.reverse(potentialWeapons);

								writeCustomItem("plugins/MythicMobs/Items/CUSTOMITEMID"
										+ potentialWeapons.get(0).getId() + ".yml", potentialWeapons.get(0));
								mob = mob + "  - " + "CUSTOMITEMID_" + potentialWeapons.get(0).getId() + ":0\r\n";

							} else {
								if (npc.getHanditem() != null)
									mob = mob + "  - " + npc.getHanditem() + ":0\r\n";
							}
						}
					}
				}

			} catch (CoreStateInitException e) {
				// skip
			}

		} else {
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

		if (npc.isBoss() == true || npc.isRaidboss()) {
			// we dont need to spam the screen
			/*
			 * mob = mob + "  BossBar:\r\n"; mob = mob + "    Enabled: true\r\n"; mob = mob
			 * + "    Title: " + npc.getName() + "\r\n"; mob = mob + "    Range: 200\r\n";
			 * mob = mob + "    CreateFog: true\r\n"; mob = mob + "    DarkenSky: true\r\n";
			 * mob = mob + "    PlayMusic: true\r\n";
			 */
			if (npc.getDisguisetype() != null)
				if (npc.getDisguisetype().toLowerCase().contains("player-")) {
					String[] disguisedata = npc.getDisguisetype().split("-");
					mob = mob + "    Player: " + npc.getName() + "\r\n";
					mob = mob + "    Skin: '" + disguisedata[1] + "'\r\n";
				}
		}

		mob = mob + "  Skills:\r\n";
		if (npc.isInvisible() == true) {
			mob = mob + "  - potion{t=INVISIBILITY;d=2147483647;l=1} @self ~onSpawn\r\n";
		}

		return mob;
	}

	@Override
	public void removeSpawnGroup(ISoliniaSpawnGroup spawngroup) {
		Utils.dispatchCommandLater(Bukkit.getPluginManager().getPlugin("Solinia3Core"),
				"mm spawners delete SPAWNGROUPID_" + spawngroup.getId());
		writeSpawnerDefinition("plugins/MythicMobs/Spawners/SPAWNGROUPID_" + spawngroup.getId() + ".yml", spawngroup);
	}

	@Override
	public void spawnNPC(ISoliniaNPC npc, int amount, String world, int x, int y, int z) {
		Utils.dispatchCommandLater(Bukkit.getPluginManager().getPlugin("Solinia3Core"),
				"mm mobs spawn NPCID_" + npc.getId() + " " + amount + " " + world + "," + x + "," + y + "," + z);
	}

}
