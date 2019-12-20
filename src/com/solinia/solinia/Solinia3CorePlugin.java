package com.solinia.solinia;

import java.io.IOException;
import org.dynmap.markers.MarkerSet;
import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.palmergames.bukkit.towny.Towny;
import com.solinia.solinia.Commands.*;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Listeners.DynmapListener;
import com.solinia.solinia.Listeners.PlayerValidatorModListener;
import com.solinia.solinia.Listeners.Solinia3CoreBlockListener;
import com.solinia.solinia.Listeners.Solinia3CoreChunkListener;
import com.solinia.solinia.Listeners.Solinia3CoreEntityListener;
import com.solinia.solinia.Listeners.Solinia3CoreItemPickupListener;
import com.solinia.solinia.Listeners.Solinia3CoreNPCUpdatedListener;
import com.solinia.solinia.Listeners.Solinia3CorePlayerChatListener;
import com.solinia.solinia.Listeners.Solinia3CorePlayerListener;
import com.solinia.solinia.Listeners.Solinia3CoreSpawnGroupUpdatedListener;
import com.solinia.solinia.Listeners.Solinia3CoreVehicleListener;
import com.solinia.solinia.Listeners.Solinia3CoreVoteListener;
import com.solinia.solinia.Listeners.Solinia3CoreZoneTickListener;
import com.solinia.solinia.Managers.ChannelManager;
import com.solinia.solinia.Managers.ConfigurationManager;
import com.solinia.solinia.Managers.EntityManager;
import com.solinia.solinia.Managers.PlayerManager;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.ConfigSettings;
import com.solinia.solinia.Models.Solinia3UIChannelNames;
import com.solinia.solinia.Providers.MythicMobsNPCEntityProvider;
import com.solinia.solinia.Repositories.JsonAAAbilityRepository;
import com.solinia.solinia.Repositories.JsonAccountClaimRepository;
import com.solinia.solinia.Repositories.JsonAlignmentRepository;
import com.solinia.solinia.Repositories.JsonCharacterListRepository;
import com.solinia.solinia.Repositories.JsonClassRepository;
import com.solinia.solinia.Repositories.JsonCraftRepository;
import com.solinia.solinia.Repositories.JsonFactionRepository;
import com.solinia.solinia.Repositories.JsonGodRepository;
import com.solinia.solinia.Repositories.JsonZoneRepository;
import com.solinia.solinia.Repositories.JsonItemRepository;
import com.solinia.solinia.Repositories.JsonLootDropRepository;
import com.solinia.solinia.Repositories.JsonLootTableRepository;
import com.solinia.solinia.Repositories.JsonNPCMerchantRepository;
import com.solinia.solinia.Repositories.JsonNPCRepository;
import com.solinia.solinia.Repositories.JsonNPCSpellListRepository;
import com.solinia.solinia.Repositories.JsonPatchRepository;
import com.solinia.solinia.Repositories.JsonPlayerRepository;
import com.solinia.solinia.Repositories.JsonQuestRepository;
import com.solinia.solinia.Repositories.JsonRaceRepository;
import com.solinia.solinia.Repositories.JsonSpawnGroupRepository;
import com.solinia.solinia.Repositories.JsonSpellRepository;
import com.solinia.solinia.Repositories.JsonWorldRepository;
import com.solinia.solinia.Timers.AttendenceXpBonusTimer;
import com.solinia.solinia.Timers.CastingTimer;
import com.solinia.solinia.Timers.ClientVersionTimer;
import com.solinia.solinia.Timers.DynmapTimer;
import com.solinia.solinia.Timers.EntityAutoAttackTimer;
import com.solinia.solinia.Timers.InvalidItemCheckerTimer;
import com.solinia.solinia.Timers.NPCCheckForEnemiesTimer;
import com.solinia.solinia.Timers.NPCRandomChatTimer;
import com.solinia.solinia.Timers.NPCSpellCastTimer;
import com.solinia.solinia.Timers.NPCSummonAndTeleportCastTimer;
import com.solinia.solinia.Timers.PetCheckTickTimer;
import com.solinia.solinia.Timers.PetFastCheckTimer;
import com.solinia.solinia.Timers.PlayerEquipmentTickTimer;
import com.solinia.solinia.Timers.PlayerInventoryValidatorTimer;
import com.solinia.solinia.Timers.PlayerRegenTickTimer;
import com.solinia.solinia.Timers.PlayerTickTimer;
import com.solinia.solinia.Timers.SoliniaLivingEntityPassiveEffectTimer;
import com.solinia.solinia.Timers.SpellTickTimer;
import com.solinia.solinia.Timers.StateCommitTimer;
import com.solinia.solinia.Timers.ZoneTickTimer;
import com.solinia.solinia.Utils.ForgeUtils;
import org.json.JSONException;

import de.slikey.effectlib.EffectManager;
import net.milkbowl.vault.economy.Economy;
import org.dynmap.DynmapAPI;
public class Solinia3CorePlugin extends JavaPlugin implements PluginMessageListener  {

	private CastingTimer castingTimer;
	private StateCommitTimer commitTimer;
	private PlayerTickTimer playerTickTimer;
	private PlayerRegenTickTimer playerRegenTickTimer;
	private PlayerEquipmentTickTimer playerEquipmentTickTimer;
	private ZoneTickTimer zoneTickTimer;
	private SpellTickTimer spellTickTimer;
	private NPCSpellCastTimer npcSpellCastTimer;
	private NPCSummonAndTeleportCastTimer npcSummonCastTimer;
	private PlayerInventoryValidatorTimer playerInventoryValidatorTimer;
	private NPCRandomChatTimer npcRandomChatTimer;
	private NPCCheckForEnemiesTimer npcCheckForEnemiesTimer;
	private PetCheckTickTimer petCheckTickTimer;
	private PetFastCheckTimer petFastCheckTickTimer;
	private InvalidItemCheckerTimer invalidItemCheckerTimer;
	private EntityAutoAttackTimer entityAutoAttackTimer;
	private SoliniaLivingEntityPassiveEffectTimer entityPassiveEffectTimer;
	FileConfiguration config = getConfig();
	private EffectManager effectManager;
	private AttendenceXpBonusTimer attendenceXpBonusTimer;
	private ClientVersionTimer clientVersionTimer;
	private DynmapTimer dynmapTimer;
	private Plugin dynmap;
	private DynmapAPI dynmapApi;
	private Plugin towny;
	private Towny townyApi;

	private Economy economy;
	private MarkerSet set;
	
	@Override
	public void onEnable() {
		dynmap = getServer().getPluginManager().getPlugin("dynmap");
        if(dynmap == null) {
        	System.out.println("Solinia3-Core! Cannot find dynmap! Disabling plugin...");
			Bukkit.getPluginManager().disablePlugin(this); 
			return;
        }
        dynmapApi = (DynmapAPI)dynmap; /* Get API */
        set = dynmapApi.getMarkerAPI().createMarkerSet("solinia.markerset", "SoliniaZones", dynmapApi.getMarkerAPI().getMarkerIcons(), false);
        set.setHideByDefault(true);
        towny = getServer().getPluginManager().getPlugin("Towny");
        if(towny == null) {
        	System.out.println("Solinia3-Core! Cannot find Towny! Disabling plugin...");
			Bukkit.getPluginManager().disablePlugin(this); 
			return;
        }
        townyApi = (Towny)towny; /* Get API */
        
		String expectedClientModVersion = null;
		try {
			expectedClientModVersion = ForgeUtils.fetchExpectedForgeClientModVersion();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (expectedClientModVersion == null || expectedClientModVersion.equals(""))
		{
			System.out.println("Solinia3-Core Error loading plugin!!! Could not find expected mod version! Disabling plugin...");
			Bukkit.getPluginManager().disablePlugin(this); 
			return;
		}
		
		System.out.println("Requiring ClientModVersion: " + expectedClientModVersion);
		
		createConfigDir();
		
		config.addDefault("maxlevel", "70");
		config.options().copyDefaults(true);
		saveConfig();
		
		ConfigSettings configSettings = new ConfigSettings();
		
		if (!config.getString("maxlevel").equals(""))
		{
			configSettings.MaxLevel = Integer.parseInt(config.getString("maxlevel"));
		}
		
		System.out.println("All local dates stored in format: " + Locale.getDefault().toLanguageTag());
		
		// For debugging
		//new RuntimeTransformer( EntityDamageEventTransformer.class );
		
		effectManager = new EffectManager(this);

		initialise(configSettings);
		registerEvents();

		setupEconomy();
		

		StateManager.getInstance().setEconomy(this.economy);
		StateManager.getInstance().setRequiredModVersion(expectedClientModVersion);
		StateManager.getInstance().setDynmap(this.dynmapApi);
		StateManager.getInstance().setMarkerSet(this.set);
		StateManager.getInstance().setTowny(this.townyApi);
		RegisterEntities();
		
		if (!getServer().getPluginManager().isPluginEnabled(this)) return;
		System.out.println("[Solinia3Core] Plugin Enabled");

		System.out.println("Registered outgoing plugin channel: " + Solinia3UIChannelNames.Outgoing);		
		getServer().getMessenger().registerOutgoingPluginChannel(this, Solinia3UIChannelNames.Outgoing); // we register the outgoing channel
	    
	}

	@Override
	public void onPluginMessageReceived(String channel, org.bukkit.entity.Player player, byte[] bytes) {
		if (!channel.equalsIgnoreCase("solinia3core:channel")) {
			return;
		}
		System.out.println("plugin message from channel: " + channel);
		/*
		ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
		String subChannel = in.readUTF();
		if (subChannel.equalsIgnoreCase("openspellbook")) {
			String data1 = in.readUTF();
			int data2 = in.readInt();

			// do things with the data
		}
		*/
	}
	
	private void RegisterEntities() {
		//NMSUtils.registerEntity("SoliniaMob", NMSUtils.Type.SKELETON, MythicEntitySoliniaMob.class, true);
		
		
	}
	
	private void UnregisterEntities() {
		
	}

	@Override
	public void onDisable() {
		try {
			StateManager.getInstance().getEntityManager().removeAllPets();
			StateManager.getInstance().Commit();
			
			
			// Cleanup Dynmap
	        StateManager.getInstance().resareas.clear(); 
	        if(set != null) {
	            set.deleteMarkerSet();
	            set = null;
	        }
	        
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		effectManager.dispose();
		
		UnregisterEntities();
		
		System.out.println("[Solinia3Core] Plugin Disabled");
	}

	private boolean setupEconomy() {
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager()
				.getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null)
			economy = economyProvider.getProvider();
		return (economy != null);
	}

	private void initialise(ConfigSettings configSettings) {
		// TODO Lets load all this from config settings at some point

		try {
			JsonPlayerRepository repo = new JsonPlayerRepository();
			repo.setJsonFile(getDataFolder() + "/" + "players.json");
			repo.reload();

			JsonRaceRepository racerepo = new JsonRaceRepository();
			racerepo.setJsonFile(getDataFolder() + "/" + "races.json");
			racerepo.reload();

			JsonClassRepository classrepo = new JsonClassRepository();
			classrepo.setJsonFile(getDataFolder() + "/" + "classes.json");
			classrepo.reload();

			JsonItemRepository itemrepo = new JsonItemRepository();
			itemrepo.setJsonFile(getDataFolder() + "/" + "items.json");
			itemrepo.reload();

			JsonSpellRepository spellrepo = new JsonSpellRepository();
			spellrepo.setJsonFile(getDataFolder() + "/" + "spells.json");
			spellrepo.reload();

			JsonFactionRepository factionrepo = new JsonFactionRepository();
			factionrepo.setJsonFile(getDataFolder() + "/" + "factions.json");
			factionrepo.reload();

			JsonNPCRepository npcrepo = new JsonNPCRepository();
			npcrepo.setJsonFile(getDataFolder() + "/" + "npcs.json");
			npcrepo.reload();

			JsonNPCMerchantRepository npcmerchantrepo = new JsonNPCMerchantRepository();
			npcmerchantrepo.setJsonFile(getDataFolder() + "/" + "npcmerchants.json");
			npcmerchantrepo.reload();

			JsonLootTableRepository loottablerepo = new JsonLootTableRepository();
			loottablerepo.setJsonFile(getDataFolder() + "/" + "loottables.json");
			loottablerepo.reload();

			JsonLootDropRepository lootdroprepo = new JsonLootDropRepository();
			lootdroprepo.setJsonFile(getDataFolder() + "/" + "lootdrops.json");
			lootdroprepo.reload();

			JsonSpawnGroupRepository spawngrouprepo = new JsonSpawnGroupRepository();
			spawngrouprepo.setJsonFile(getDataFolder() + "/" + "spawngroups.json");
			spawngrouprepo.reload();

			JsonAAAbilityRepository aaabilityrepo = new JsonAAAbilityRepository();
			aaabilityrepo.setJsonFile(getDataFolder() + "/" + "aaabilities.json");
			aaabilityrepo.reload();

			JsonPatchRepository patchesrepo = new JsonPatchRepository();
			patchesrepo.setJsonFile(getDataFolder() + "/" + "patches.json");
			patchesrepo.reload();

			JsonQuestRepository questsrepo = new JsonQuestRepository();
			questsrepo.setJsonFile(getDataFolder() + "/" + "quests.json");
			questsrepo.reload();

			JsonAlignmentRepository alignmentsrepo = new JsonAlignmentRepository();
			alignmentsrepo.setJsonFile(getDataFolder() + "/" + "alignments.json");
			alignmentsrepo.reload();

			JsonCharacterListRepository characterlistrepo = new JsonCharacterListRepository();
			characterlistrepo.setJsonFile(getDataFolder() + "/" + "characterlists.json");
			characterlistrepo.reload();
			
			JsonNPCSpellListRepository npcspelllistrepo = new JsonNPCSpellListRepository();
			npcspelllistrepo.setJsonFile(getDataFolder() + "/" + "npcspelllists.json");
			npcspelllistrepo.reload();

			JsonAccountClaimRepository accountclaimsrepo = new JsonAccountClaimRepository();
			accountclaimsrepo.setJsonFile(getDataFolder() + "/" + "accountclaimrepo.json");
			accountclaimsrepo.reload();

			JsonZoneRepository zonesrepo = new JsonZoneRepository();
			zonesrepo.setJsonFile(getDataFolder() + "/" + "zones.json");
			zonesrepo.reload();

			JsonCraftRepository craftrepo = new JsonCraftRepository();
			craftrepo.setJsonFile(getDataFolder() + "/" + "craft.json");
			craftrepo.reload();

			JsonWorldRepository worldrepo = new JsonWorldRepository();
			worldrepo.setJsonFile(getDataFolder() + "/" + "worlds.json");
			worldrepo.reload();

			JsonGodRepository godrepo = new JsonGodRepository();
			godrepo.setJsonFile(getDataFolder() + "/" + "gods.json");
			godrepo.reload();

			
			PlayerManager playerManager = new PlayerManager(repo);
			EntityManager entityManager = new EntityManager(this, new MythicMobsNPCEntityProvider());

			ConfigurationManager configurationManager = new ConfigurationManager(racerepo, classrepo, itemrepo,
					spellrepo, factionrepo, npcrepo, npcmerchantrepo, loottablerepo, lootdroprepo, spawngrouprepo,
					aaabilityrepo, patchesrepo, questsrepo, alignmentsrepo, characterlistrepo, npcspelllistrepo,
					accountclaimsrepo, zonesrepo, craftrepo, worldrepo,godrepo, configSettings);

			ChannelManager channelManager = new ChannelManager();
			
			StateManager.getInstance().Initialise(playerManager, entityManager, configurationManager, channelManager, effectManager);
			loadTimers();
			
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void loadTimers() {
		commitTimer = new StateCommitTimer();
		commitTimer.runTaskTimer(this, 300 * 20L, 300 * 20L);

		playerTickTimer = new PlayerTickTimer();
		playerTickTimer.runTaskTimer(this, 6 * 20L, 6 * 20L);

		playerRegenTickTimer = new PlayerRegenTickTimer();
		playerRegenTickTimer.runTaskTimer(this, 6 * 20L, 6 * 20L);
		
		playerEquipmentTickTimer = new PlayerEquipmentTickTimer();
		playerEquipmentTickTimer.runTaskTimer(this, 60 * 20L, 60 * 20L);

		zoneTickTimer = new ZoneTickTimer();
		zoneTickTimer.runTaskTimer(this, 6 * 20L, 6 * 20L);
		
		spellTickTimer = new SpellTickTimer(this);
		spellTickTimer.runTaskTimer(this, 6 * 20L, 6 * 20L);

		// Only validate these things every 2 minutes
		playerInventoryValidatorTimer = new PlayerInventoryValidatorTimer();
		playerInventoryValidatorTimer.runTaskTimer(this, 120 * 20L, 120 * 20L);

		npcRandomChatTimer = new NPCRandomChatTimer();
		npcRandomChatTimer.runTaskTimer(this, 6 * 20L, 60 * 20L);

		npcCheckForEnemiesTimer = new NPCCheckForEnemiesTimer();
		npcCheckForEnemiesTimer.runTaskTimer(this, 1 * 20L, 1 * 20L);
		
		npcSpellCastTimer = new NPCSpellCastTimer(this);
		npcSpellCastTimer.runTaskTimer(this, 3 * 20L, 3 * 20L);

		npcSummonCastTimer = new NPCSummonAndTeleportCastTimer(this);
		npcSummonCastTimer.runTaskTimer(this, 6 * 20L, 6 * 20L);
		
		petCheckTickTimer = new PetCheckTickTimer();
		petCheckTickTimer.runTaskTimer(this, 1 * 20L, 1 * 20L);

		petFastCheckTickTimer = new PetFastCheckTimer();
		petFastCheckTickTimer.runTaskTimer(this, 1 * 20L, 1 * 20L);
		
		attendenceXpBonusTimer = new AttendenceXpBonusTimer();
		attendenceXpBonusTimer.runTaskTimer(this, 60 * 20L, 60 * 20L);

		invalidItemCheckerTimer = new InvalidItemCheckerTimer();
		invalidItemCheckerTimer.runTaskTimer(this, 60 * 20L, 60 * 20L);
		
		castingTimer = new CastingTimer();
		// every 100 milliseconds
		castingTimer.runTaskTimer(this, 0L, 1 * 2L);
		
		entityAutoAttackTimer = new EntityAutoAttackTimer();
		entityAutoAttackTimer.runTaskTimer(this, 0L, 1L);

		entityPassiveEffectTimer = new SoliniaLivingEntityPassiveEffectTimer();
		entityPassiveEffectTimer.runTaskTimer(this, 6 * 20L, 6 * 20L);
		
		clientVersionTimer = new ClientVersionTimer();
		// every 30 seconds
		clientVersionTimer.runTaskTimer(this, 30 * 20L, 30 * 20L);
		
		dynmapTimer = new DynmapTimer();
		// every 5 seconds
		dynmapTimer.runTaskTimer(this, 5 * 20L, 5 * 20L);
	}

	private void registerEvents() {
		getServer().getPluginManager().registerEvents(new Solinia3CorePlayerListener(this), this);
		getServer().getPluginManager().registerEvents(new Solinia3CoreEntityListener(this), this);
		getServer().getPluginManager().registerEvents(new Solinia3CorePlayerChatListener(this), this);
		getServer().getPluginManager().registerEvents(new Solinia3CoreNPCUpdatedListener(this), this);
		getServer().getPluginManager().registerEvents(new Solinia3CoreSpawnGroupUpdatedListener(this), this);
		getServer().getPluginManager().registerEvents(new Solinia3CoreItemPickupListener(this), this);
		getServer().getPluginManager().registerEvents(new Solinia3CoreVehicleListener(this), this);
		getServer().getPluginManager().registerEvents(new Solinia3CoreVoteListener(this), this);
		getServer().getPluginManager().registerEvents(new Solinia3CoreBlockListener(this), this);
		getServer().getPluginManager().registerEvents(new Solinia3CoreBlockListener(this), this);
		getServer().getPluginManager().registerEvents(new Solinia3CoreChunkListener(this), this);
		getServer().getPluginManager().registerEvents(new Solinia3CoreZoneTickListener(this), this);
		getServer().getPluginManager().registerEvents(new PlayerValidatorModListener(this), this);
		getServer().getPluginManager().registerEvents(new DynmapListener(this), this);

		setupCommands();
	}

	private void setupCommands() {
		this.getCommand("saydiscord").setExecutor(new CommandSayDiscord());
		this.getCommand("createcharacterfull").setExecutor(new CommandCreateCharacterFull(this));
		this.getCommand("opencharcreation").setExecutor(new CommandOpenCharacterCreation());
		this.getCommand("memorisespell").setExecutor(new CommandMemoriseSpell());
		this.getCommand("openspellbook").setExecutor(new CommandOpenSpellbook());

		this.getCommand("autoattack").setExecutor(new CommandAutoAttack());
		this.getCommand("creategod").setExecutor(new CommandCreateGod());
		this.getCommand("listgods").setExecutor(new CommandListGods());
		this.getCommand("editgod").setExecutor(new CommandEditGod());

		this.getCommand("godinfo").setExecutor(new CommandGodInfo());
		this.getCommand("setgod").setExecutor(new CommandSetGod());
		this.getCommand("scribeallspells").setExecutor(new CommandScribeAllSpells());
		
		this.getCommand("cast").setExecutor(new CommandCast());
		this.getCommand("shout").setExecutor(new CommandShout());
		this.getCommand("stopcasting").setExecutor(new CommandStopCasting());
		this.getCommand("whisper").setExecutor(new CommandWhisper());
		this.getCommand("zonetp").setExecutor(new CommandZoneTp());
		this.getCommand("transfercharacter").setExecutor(new CommandTransferCharacter());
		this.getCommand("playeremote").setExecutor(new CommandPlayerEmote());
		this.getCommand("consider").setExecutor(new CommandConsider());
		this.getCommand("editchunk").setExecutor(new CommandEditChunk());
		this.getCommand("solinia").setExecutor(new CommandSolinia());
		this.getCommand("commit").setExecutor(new CommandCommit());
		this.getCommand("toggleexperience").setExecutor(new CommandToggleExperience());
		this.getCommand("forename").setExecutor(new CommandForename());
		this.getCommand("lastname").setExecutor(new CommandLastname());
		this.getCommand("mana").setExecutor(new CommandMana());
		this.getCommand("addrace").setExecutor(new CommandAddRace());
		this.getCommand("setrace").setExecutor(new CommandSetRace());
		this.getCommand("addclass").setExecutor(new CommandAddClass());
		this.getCommand("setclass").setExecutor(new CommandSetClass());
		this.getCommand("addraceclass").setExecutor(new CommandAddRaceClass());
		this.getCommand("stats").setExecutor(new CommandStats());
		this.getCommand("follow").setExecutor(new CommandFollow());
		this.getCommand("resetplayer").setExecutor(new CommandResetPlayer());
		this.getCommand("who").setExecutor(new CommandWho());
		this.getCommand("soliteminfo").setExecutor(new CommandSolItemInfo());
		this.getCommand("solnpcinfo").setExecutor(new CommandSolNPCInfo());
		this.getCommand("emote").setExecutor(new CommandEmote());
		this.getCommand("roll").setExecutor(new CommandRoll());
		this.getCommand("setgender").setExecutor(new CommandSetGender());
		this.getCommand("setlanguage").setExecutor(new CommandSetLanguage());
		this.getCommand("tarot").setExecutor(new CommandTarot());
		this.getCommand("skills").setExecutor(new CommandSkills());
		this.getCommand("createitem").setExecutor(new CommandCreateItem());
		this.getCommand("hidesongs").setExecutor(new CommandHideSongs());
		this.getCommand("listitems").setExecutor(new CommandListItems());
		this.getCommand("spawnitem").setExecutor(new CommandSpawnItem());
		this.getCommand("debugger").setExecutor(new CommandDebugger());
		this.getCommand("raceinfo").setExecutor(new CommandRaceInfo());
		this.getCommand("rebuildspellitems").setExecutor(new CommandRebuildSpellItems());
		this.getCommand("createfaction").setExecutor(new CommandCreateFaction());
		this.getCommand("createnpc").setExecutor(new CommandCreateNpc());
		this.getCommand("listfactions").setExecutor(new CommandListFactions());
		this.getCommand("listnpcs").setExecutor(new CommandListNPCs());
		this.getCommand("editnpc").setExecutor(new CommandEditNpc());
		this.getCommand("createloottable").setExecutor(new CommandCreateLootTable());
		this.getCommand("createlootdrop").setExecutor(new CommandCreateLootDrop());
		this.getCommand("addlootdropitem").setExecutor(new CommandAddLootDropItem());
		this.getCommand("addloottablelootdrop").setExecutor(new CommandAddLootTableLootDrop());
		this.getCommand("editspell").setExecutor(new CommandEditSpell());
		this.getCommand("listlootdrops").setExecutor(new CommandListLootDrops());
		this.getCommand("listloottables").setExecutor(new CommandListLootTables());
		this.getCommand("local").setExecutor(new CommandLocal());
		this.getCommand("removevampirism").setExecutor(new CommandRemoveVampirism());
		this.getCommand("forcelevel").setExecutor(new CommandForceLevel());
		this.getCommand("createmerchantlist").setExecutor(new CommandCreateMerchantList());
		this.getCommand("addmerchantitem").setExecutor(new CommandAddMerchantItem());
		this.getCommand("listmerchantlists").setExecutor(new CommandListMerchantLists());
		this.getCommand("stopsinging").setExecutor(new CommandStopSinging());
		this.getCommand("edititem").setExecutor(new CommandEditItem());
		this.getCommand("createspawngroup").setExecutor(new CommandCreateSpawnGroup());
		this.getCommand("listspawngroups").setExecutor(new CommandListSpawnGroups());
		this.getCommand("updatespawngrouploc").setExecutor(new CommandUpdateSpawnGroupLoc());
		this.getCommand("ooc").setExecutor(new CommandOoc());
		this.getCommand("group").setExecutor(new CommandGroup());
		this.getCommand("groupchat").setExecutor(new CommandGroupChat());
		this.getCommand("convertmerchanttolootdrop").setExecutor(new CommandConvertMerchantToLootDrop());
		this.getCommand("effects").setExecutor(new CommandEffects(this));
		this.getCommand("editclass").setExecutor(new CommandEditClass());
		this.getCommand("createarmorset").setExecutor(new CommandCreateArmourSet());
		this.getCommand("createnpccopy").setExecutor(new CommandCreateNpcCopy());
		this.getCommand("listspells").setExecutor(new CommandListSpells());
		this.getCommand("editloottable").setExecutor(new CommandEditLootTable());
		this.getCommand("editlootdrop").setExecutor(new CommandEditLootDrop());
		this.getCommand("pet").setExecutor(new CommandPet());
		this.getCommand("trance").setExecutor(new CommandTrance());
		this.getCommand("aa").setExecutor(new CommandAA());
		this.getCommand("toggleaa").setExecutor(new CommandToggleAA());
		this.getCommand("createnpcevent").setExecutor(new CommandCreateNPCEvent());
		this.getCommand("editrace").setExecutor(new CommandEditRace());
		this.getCommand("editspawngroup").setExecutor(new CommandEditSpawngroup());
		this.getCommand("faction").setExecutor(new CommandFaction());
		this.getCommand("solignore").setExecutor(new CommandIgnore());
		this.getCommand("editfaction").setExecutor(new CommandEditFaction());
		this.getCommand("settitle").setExecutor(new CommandSetTitle());
		this.getCommand("granttitle").setExecutor(new CommandGrantTitle());
		this.getCommand("editnpcevent").setExecutor(new CommandEditNpcEvent());
		this.getCommand("createquest").setExecutor(new CommandCreateQuest());
		this.getCommand("quests").setExecutor(new CommandQuests());
		this.getCommand("npcgive").setExecutor(new CommandNPCGive());		
		this.getCommand("today").setExecutor(new CommandToday());		
		this.getCommand("listaas").setExecutor(new CommandListAAs());		
		this.getCommand("editaa").setExecutor(new CommandEditAA());		
		this.getCommand("loot").setExecutor(new CommandLoot());		
		this.getCommand("createallarmoursets").setExecutor(new CommandCreateAllArmourSets());
		this.getCommand("createalignment").setExecutor(new CommandCreateAlignment());
		this.getCommand("specialise").setExecutor(new CommandSpecialise());
		this.getCommand("bite").setExecutor(new CommandBite());
		this.getCommand("character").setExecutor(new CommandCharacter(this));
		this.getCommand("inspiration").setExecutor(new CommandInspiration());
		this.getCommand("listnpcspells").setExecutor(new CommandListNpcSpells());
		this.getCommand("editnpcspelllist").setExecutor(new CommandEditNpcSpellList());
		this.getCommand("claim").setExecutor(new CommandClaim());
		this.getCommand("hideooc").setExecutor(new CommandHideOoc());
		this.getCommand("skillcheck").setExecutor(new CommandSkillCheck());
		this.getCommand("createzone").setExecutor(new CommandCreateZone());
		this.getCommand("editzone").setExecutor(new CommandEditZone());
		this.getCommand("listzones").setExecutor(new CommandListZones());
		this.getCommand("hotzones").setExecutor(new CommandHotzones());
		this.getCommand("listraces").setExecutor(new CommandListRaces());
		this.getCommand("listclasses").setExecutor(new CommandListClasses());
		this.getCommand("equip").setExecutor(new CommandEquip());
		this.getCommand("craft").setExecutor(new CommandCraft());
		this.getCommand("createcraft").setExecutor(new CommandCreateCraft());
		this.getCommand("listcrafts").setExecutor(new CommandListCrafts());
		this.getCommand("editcraft").setExecutor(new CommandEditCraft());
		this.getCommand("npcsay").setExecutor(new CommandNPCSay());
		this.getCommand("npcattack").setExecutor(new CommandNPCAttack());
		this.getCommand("listworlds").setExecutor(new CommandListWorlds());
		this.getCommand("editworld").setExecutor(new CommandEditWorld());
		this.getCommand("reagent").setExecutor(new CommandReagent());
		this.getCommand("setspouse").setExecutor(new CommandSetSpouse());
		this.getCommand("setmother").setExecutor(new CommandSetMother());
		this.getCommand("setmain").setExecutor(new CommandSetMain());
		this.getCommand("spellbook").setExecutor(new CommandSpellBook());
		this.getCommand("target").setExecutor(new CommandTarget());
		this.getCommand("listquests").setExecutor(new CommandListQuests());
		this.getCommand("editquest").setExecutor(new CommandEditQuest());
		this.getCommand("spawnnpc").setExecutor(new CommandSpawnNpc());
		this.getCommand("bindwound").setExecutor(new CommandBindWound());
		this.getCommand("editmerchantlist").setExecutor(new CommandEditMerchantList());
		this.getCommand("claimxp").setExecutor(new CommandClaimXp());
		this.getCommand("publishbook").setExecutor(new CommandPublishBook());
		this.getCommand("editalignment").setExecutor(new CommandEditAlignment());
		this.getCommand("oath").setExecutor(new CommandOath());
		this.getCommand("personality").setExecutor(new CommandPersonality());
		this.getCommand("resetpersonality").setExecutor(new CommandResetPersonality());
		this.getCommand("pray").setExecutor(new CommandPray());
		this.getCommand("givehead").setExecutor(new CommandGiveHead(this));
		this.getCommand("iamversion").setExecutor(new CommandIamVersion(this));
		this.getCommand("sit").setExecutor(new CommandSit());
		this.getCommand("rightclickentity").setExecutor(new CommandRightClickEntity());
	}

	private void createConfigDir() {
		try {
			if (!getDataFolder().exists()) {
				getDataFolder().mkdirs();
			}
		} catch (Exception e) {
			e.printStackTrace();

		}
	}
}
