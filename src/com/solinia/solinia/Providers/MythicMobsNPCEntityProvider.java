package com.solinia.solinia.Providers;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.INPCEntityProvider;
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
		updateSpawngroupLater(StateManager.getInstance().getPlugin(), spawngroup);
	}
	
	public static void updateSpawngroupLater(Plugin plugin, ISoliniaSpawnGroup spawngroup) {
		final Plugin pluginToSend = plugin;
		final CommandSender senderToSend = pluginToSend.getServer().getConsoleSender();
		final String commandToSend = "mm spawners delete SPAWNGROUPID_" + spawngroup.getId();
		final String spawngroupPath = "plugins/MythicMobs/Spawners/SPAWNGROUPID_" + spawngroup.getId() + ".yml";
		final int spawnGroupId = spawngroup.getId();

		new BukkitRunnable() {

			@Override
			public void run() {
				pluginToSend.getServer().dispatchCommand(senderToSend, commandToSend);
				writeSpawnerDefinitionbySpawnGroupId(spawngroupPath, spawnGroupId);
			}

		}.runTaskLater(plugin, 10);
	}

	public static void writeSpawnerDefinitionbySpawnGroupId(String fileName, int spawngroupId) {
		try
		{
			ISoliniaSpawnGroup spawnGroup = StateManager.getInstance().getConfigurationManager().getSpawnGroup(spawngroupId);
			if (spawnGroup == null)
				return;
			
			writeSpawnerDefinition(fileName, spawnGroup);
		} catch (CoreStateInitException e)
		{
			
		}
		
	}
	
	@Override
	public void reloadProvider() {
		Utils.dispatchCommandLater(StateManager.getInstance().getPlugin(), "mm reload");
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

			spawner = uniquename + ":\r" + System.lineSeparator();
			spawner += "  MobType: " + mobtype + "\r" + System.lineSeparator();
			spawner += "  Worlds: world\r" + System.lineSeparator();
			spawner += "  Chance: 1\r" + System.lineSeparator();
			spawner += "  Priority: 1\r" + System.lineSeparator();
			spawner += "  Action: add\r" + System.lineSeparator();
		}
		return spawner;
	}

	public static void writeSpawnerDefinition(String fileName, ISoliniaSpawnGroup spawngroup) {
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

	public static String createSpawnerFile(ISoliniaSpawnGroup spawngroup) {
		String spawner = "";

		String uniquename = "SPAWNGROUPID_" + spawngroup.getId();
		String mobname = "NPCID_" + spawngroup.getNpcid();
		String world = spawngroup.getWorld();
		Double x = spawngroup.getX();
		Double y = spawngroup.getY();
		Double z = spawngroup.getZ();
		int respawnTime = spawngroup.getRespawntime();

		spawner = uniquename + ":\r" + System.lineSeparator();
		spawner += "  MobName: " + mobname + "\r" + System.lineSeparator();
		spawner += "  World: " + world + "\r" + System.lineSeparator();
		spawner += "  X: " + x + "\r" + System.lineSeparator();
		spawner += "  Y: " + y + "\r" + System.lineSeparator();
		spawner += "  Z: " + z + "\r" + System.lineSeparator();
		spawner += "  Radius: 0\r" + System.lineSeparator();
		spawner += "  UseTimer: true\r" + System.lineSeparator();
		spawner += "  MaxMobs: 1\r" + System.lineSeparator();
		spawner += "  MobLevel: 1\r" + System.lineSeparator();
		spawner += "  MobsPerSpawn: 1\r" + System.lineSeparator();
		// todo
		// spawner += " Cooldown: 0\r" + System.lineSeparator();
		// spawner += " CooldownTimer: 0\r" + System.lineSeparator();
		// spawner += " Warmup: " + spawngroup.respawnseconds + "\r" + System.lineSeparator();
		// spawner += " WarmupTimer: 0\r" + System.lineSeparator();
		spawner += "  Warmup: " + respawnTime + "\r" + System.lineSeparator();
		spawner += "  WarmupTimer: 0\r" + System.lineSeparator();
		spawner += "  CheckForPlayers: false\r" + System.lineSeparator();
		spawner += "  ActivationRange: 112\r" + System.lineSeparator();
		spawner += "  LeashRange: 112\r" + System.lineSeparator();
		spawner += "  HealOnLeash: true\r" + System.lineSeparator();
		spawner += "  ResetThreatOnLeash: true\r" + System.lineSeparator();
		spawner += "  ShowFlames: false\r" + System.lineSeparator();
		spawner += "  Breakable: false\r" + System.lineSeparator();
		spawner += "  Conditions: []\r" + System.lineSeparator();
		spawner += "  ActiveMobs: 0\r" + System.lineSeparator();
		return spawner;
	}

	public String createCustomHeadFile(ISoliniaNPC npc) {
		String customheaditem = "";

		if (npc.isCustomhead()) {
			customheaditem += "CUSTOMHEADNPCID_" + npc.getId() + ":\r" + System.lineSeparator();
			customheaditem += "  Id: 397\r" + System.lineSeparator();
			customheaditem += "  Data: 3\r" + System.lineSeparator();
			customheaditem += "  Options:\r" + System.lineSeparator();
			customheaditem += "    SkinTexture: " + npc.getCustomheaddata() + "\r" + System.lineSeparator();
		}

		return customheaditem;
	}

	public String createCustomItemFile(ISoliniaItem item) {
		String customitem = "";

		customitem += "CUSTOMITEMID_" + item.getId() + ":\r" + System.lineSeparator();
		customitem += "  Id: " + item.asItemStack().getType().name() + "\r" + System.lineSeparator();
		String displayName = "";
		if (item.getDisplayname() != null)
			displayName = item.getDisplayname().replaceAll("[^A-Za-z0-9_]", "");
		customitem += "  Display: 'CUSTOMITEMID_" + item.getId() + "_" + displayName + "'\r" + System.lineSeparator();
		if (item.getBasename().toUpperCase().equals("SHIELD"))
		{
			customitem += "  Options:\r" + System.lineSeparator();
			customitem += "    Color: GRAY\r" + System.lineSeparator();
		}
		customitem += "  Data: " + item.getColor() + "\r" + System.lineSeparator();
		if (item.getItemWeaponDamage(false, null) > 0) {
			customitem += "  Damage: " + item.getItemWeaponDamage(false, null) + "\r" + System.lineSeparator();
		}
		
		// Appearance
		customitem += "  NBT:\r" + System.lineSeparator();
		customitem += "    PublicBukkitValues:\r" + System.lineSeparator();
		customitem += "      solinia3core:appearanceid: "+item.getAppearanceId()+"\r" + System.lineSeparator();
		customitem += "      solinia3core:soliniaid: "+item.getId()+"\r" + System.lineSeparator();
		customitem += "      solinia3core:sollastupdated: "+item.getLastUpdatedTime()+"\r" + System.lineSeparator();
		
		customitem += "  Enchantments:\r" + System.lineSeparator();
		customitem += "    DURABILITY: " + (1000 + item.getId()) + "\r" + System.lineSeparator();
		return customitem;
	}

	public String createNpcFile(ISoliniaNPC npc) {
		String mob = "";

		String uniquename = "NPCID_" + npc.getId();

		if (npc.getMctype() == null)
			return "";

		mob = uniquename + ":\r" + System.lineSeparator();
		mob = mob + "  Type: SKELETON\r" + System.lineSeparator();
		if (npc.isUpsidedown() == true) {
			mob = mob + "  Display: Dinnerbone\r" + System.lineSeparator();
		} else {
			mob = mob + "  Display: " + npc.getName() + "\r" + System.lineSeparator();
		}

		double hp = Utils.getStatMaxHP(npc.getClassObj(), npc.getLevel(), 75);
		double damage = Utils.getNPCDefaultDamage(npc);

		if (npc.isHeroic()) {
			hp += (Utils.getHeroicHPMultiplier() * npc.getLevel());
		}

		if (npc.isBoss()) {
			hp += (Utils.getBossHPMultiplier(npc.isHeroic()) * npc.getLevel());
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
		
		if (npc.getForcedMaxHp() > 0)
			hp = npc.getForcedMaxHp();

		mob = mob + "  Health: " + hp + "\r" + System.lineSeparator();
		mob = mob + "  Damage: " + damage + "\r" + System.lineSeparator();
		mob = mob + "  PreventOtherDrops: true\r" + System.lineSeparator();
		mob = mob + "  PreventRandomEquipment: true\r" + System.lineSeparator();
		mob = mob + "  Options:\r" + System.lineSeparator();
		mob = mob + "    MaxCombatDistance: "+Utils.MAX_ENTITY_AGGRORANGE+"\r" + System.lineSeparator();
		mob = mob + "    FollowRange: "+Utils.MAX_ENTITY_AGGRORANGE+"\r" + System.lineSeparator();
		mob = mob + "    MovementSpeed: " + movementSpeed + "\r" + System.lineSeparator();
		mob = mob + "    KnockbackResistance: 1\r" + System.lineSeparator();
		mob = mob + "    PreventMobKillDrops: true\r" + System.lineSeparator();
		mob = mob + "    PreventOtherDrops: true\r" + System.lineSeparator();
		mob = mob + "    Silent: true\r" + System.lineSeparator();
		if (npc.isCorePet())
			mob = mob + "    Despawn: false\r" + System.lineSeparator();
		else
			mob = mob + "    Despawn: true\r" + System.lineSeparator();
		mob = mob + "    ShowHealth: true\r" + System.lineSeparator();
		mob = mob + "    PreventRenaming: true\r" + System.lineSeparator();
		mob = mob + "    PreventRandomEquipment: true\r" + System.lineSeparator();
		mob = mob + "    AlwaysShowName: true\r" + System.lineSeparator();
		mob = mob + "  Modules:\r" + System.lineSeparator();
		mob = mob + "    ThreatTable: false\r" + System.lineSeparator();

		mob = mob + "  AIGoalSelectors:\r" + System.lineSeparator();
		mob = mob + "  - 0 clear\r" + System.lineSeparator();
		mob = mob + "  - 1 float\r" + System.lineSeparator();
		mob = mob + "  - 2 skeletonbowattack\r" + System.lineSeparator();
		mob = mob + "  - 3 meleeattack\r" + System.lineSeparator();
		
		mob = mob + "  - 4 gotoowner\r" + System.lineSeparator();
		mob = mob + "  - 5 lookatplayers\r" + System.lineSeparator();
		mob = mob + "  - 6 randomlookaround\r" + System.lineSeparator();
		if (npc.isRoamer())
		mob = mob + "  - 7 randomstroll\r" + System.lineSeparator();
		
		mob = mob + "  AITargetSelectors:\r" + System.lineSeparator();
		mob = mob + "  - 0 clear\r" + System.lineSeparator();
		mob = mob + "  - 1 attacker\r" + System.lineSeparator();

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
								if (item.getAllowedClassNamesUpper().size() == 0) {
									if (item.getBasename().contains("CHESTPLATE"))
										potentialChestArmour.add(item);
									if (item.getBasename().contains("LEGGINGS"))
										potentialLegsArmour.add(item);
									if (item.getBasename().contains("BOOTS"))
										potentialFeetArmour.add(item);
									continue;
								}

								// TODO We should check race here also
								if (npc.getClassObj() != null) {
									if (item.getAllowedClassNamesUpper().contains(npc.getClassObj().getName())) {
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
								if (item.getAllowedClassNamesUpper().size() == 0) {
									if (item.getBasename().contains("SHIELD"))
										potentialShields.add(item);
									else if (item.getBasename().contains("BOW") || item.getBasename().contains("CROSSBOW"))
										potentialBows.add(item);
									else
										potentialWeapons.add(item);
									continue;
								}

								// We should check race here too
								if (npc.getClassObj() != null) {
									if (item.getAllowedClassNamesUpper().contains(npc.getClassObj().getName())) {
										if (item.getBasename().contains("SHIELD"))
											potentialShields.add(item);
										else if (item.getBasename().contains("BOW") || item.getBasename().contains("CROSSBOW"))
											potentialBows.add(item);
										else
											potentialWeapons.add(item);
										continue;
									}
								}
							}

							if (ConfigurationManager.HandMaterials.contains(item.getBasename().toUpperCase())) {
								if (item.getAllowedClassNamesUpper().size() == 0) {
									if (item.getBasename().contains("SHIELD"))
										potentialShields.add(item);
									else if (item.getBasename().contains("BOW") || item.getBasename().contains("CROSSBOW"))
										potentialBows.add(item);
									else
										potentialWeapons.add(item);
									continue;
								}

								// We should check race here too
								if (npc.getClassObj() != null) {
									if (item.getAllowedClassNamesUpper().contains(npc.getClassObj().getName())) {
										if (item.getBasename().contains("SHIELD"))
											potentialShields.add(item);
										else if (item.getBasename().contains("BOW") || item.getBasename().contains("CROSSBOW"))
											potentialBows.add(item);
										else
											potentialWeapons.add(item);
										continue;
									}
								}
							}
						}

						mob = mob + "  Equipment:\r" + System.lineSeparator();

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
							mob = mob + "  - " + "CUSTOMITEMID_" + potentialShields.get(0).getId() + ":5\r" + System.lineSeparator();
						} else {
							if (npc.getOffhanditem() != null)
								mob = mob + "  - " + npc.getOffhanditem() + ":5\r" + System.lineSeparator();
						}

						if (npc.isCustomhead() == true) {
							if (npc.getCustomheaddata() != null) {
								mob = mob + "  - CUSTOMHEADNPCID_" + npc.getId() + ":4\r" + System.lineSeparator();
							} else {
								if (npc.getHeaditem() != null)
									mob = mob + "  - " + npc.getHeaditem() + ":4\r" + System.lineSeparator();
							}
						} else {
							if (npc.getHeaditem() != null)
								mob = mob + "  - " + npc.getHeaditem() + ":4\r" + System.lineSeparator();
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
							mob = mob + "  - " + "CUSTOMITEMID_" + potentialChestArmour.get(0).getId() + ":3\r" + System.lineSeparator();
						} else {
							if (npc.getChestitem() != null)
								mob = mob + "  - " + npc.getChestitem() + ":3\r" + System.lineSeparator();
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
							mob = mob + "  - " + "CUSTOMITEMID_" + potentialLegsArmour.get(0).getId() + ":2\r" + System.lineSeparator();
						} else {
							if (npc.getLegsitem() != null)
								mob = mob + "  - " + npc.getLegsitem() + ":2\r" + System.lineSeparator();
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
							mob = mob + "  - " + "CUSTOMITEMID_" + potentialFeetArmour.get(0).getId() + ":1\r" + System.lineSeparator();
						} else {
							if (npc.getFeetitem() != null)
								mob = mob + "  - " + npc.getFeetitem() + ":1\r" + System.lineSeparator();
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
							mob = mob + "  - " + "CUSTOMITEMID_" + potentialBows.get(0).getId() + ":0\r" + System.lineSeparator();
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
								mob = mob + "  - " + "CUSTOMITEMID_" + potentialWeapons.get(0).getId() + ":0\r" + System.lineSeparator();

							} else {
								if (npc.getHanditem() != null)
									mob = mob + "  - " + npc.getHanditem() + ":0\r" + System.lineSeparator();
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
				mob = mob + "  Equipment:\r" + System.lineSeparator();
				if (npc.getOffhanditem() != null && !npc.getOffhanditem().equals(""))
					mob = mob + "  - " + npc.getOffhanditem() + ":5\r" + System.lineSeparator();

				if (npc.isCustomhead() == true) {
					if (npc.getCustomheaddata() != null && !npc.getCustomheaddata().equals("")) {
						mob = mob + "  - CUSTOMHEADNPCID_" + npc.getId() + ":4\r" + System.lineSeparator();
					} else {
						if (npc.getHeaditem() != null && !npc.getHeaditem().equals(""))
							mob = mob + "  - " + npc.getHeaditem() + ":4\r" + System.lineSeparator();
					}
				} else {
					if (npc.getHeaditem() != null && !npc.getHeaditem().equals(""))
						mob = mob + "  - " + npc.getHeaditem() + ":4\r" + System.lineSeparator();
				}

				if (npc.getChestitem() != null && !npc.getChestitem().equals(""))
					mob = mob + "  - " + npc.getChestitem() + ":3\r" + System.lineSeparator();
				if (npc.getLegsitem() != null && !npc.getLegsitem().equals(""))
					mob = mob + "  - " + npc.getLegsitem() + ":2\r" + System.lineSeparator();
				if (npc.getFeetitem() != null && !npc.getFeetitem().equals(""))
					mob = mob + "  - " + npc.getFeetitem() + ":1\r" + System.lineSeparator();
				if (npc.getHanditem() != null && !npc.getHanditem().equals(""))
					mob = mob + "  - " + npc.getHanditem() + ":0\r" + System.lineSeparator();
			}
		}

		if (npc.isUsedisguise() == true && npc.getDisguisetype() != null && !npc.getDisguisetype().equals("")) {
			mob = mob + "  Disguise:\r" + System.lineSeparator();
			if (npc.getDisguisetype().toLowerCase().contains("player-")) {
				mob = mob + "    Type: player\r" + System.lineSeparator();
			} else {
				mob = mob + "    Type: " + npc.getDisguisetype() + "\r" + System.lineSeparator();
			}
			if (npc.isBurning() == true) {
				mob = mob + "    Burning: true\r" + System.lineSeparator();
			}
			if (npc.getDisguisetype().toLowerCase().contains("player-")) {
				String[] disguisedata = npc.getDisguisetype().split("-");
				mob = mob + "    Player: " + npc.getName() + "\r" + System.lineSeparator();
				mob = mob + "    Skin: '" + disguisedata[1] + "'\r" + System.lineSeparator();
			}
		}
		if (npc.isMounted() == true) {
			mob = mob + "  Riding: Horse\r" + System.lineSeparator();
		}

		mob = mob + "  Skills:\r" + System.lineSeparator();
		if (npc.isInvisible() == true) {
			mob = mob + "  - potion{t=INVISIBILITY;d=2147483647;l=1} @self ~onSpawn\r" + System.lineSeparator();
		}
		return mob;
	}

	@Override
	public void removeSpawnGroup(ISoliniaSpawnGroup spawngroup) {
		Utils.dispatchCommandLater(StateManager.getInstance().getPlugin(),
				"mm spawners delete SPAWNGROUPID_" + spawngroup.getId());
		writeSpawnerDefinition("plugins/MythicMobs/Spawners/SPAWNGROUPID_" + spawngroup.getId() + ".yml", spawngroup);
	}

	@Override
	public void spawnNPC(ISoliniaNPC npc, int amount, String world, int x, int y, int z) {
		Utils.dispatchCommandLater(StateManager.getInstance().getPlugin(),
				"mm mobs spawn NPCID_" + npc.getId() + " " + amount + " " + world + "," + x + "," + y + "," + z);
	}

}
