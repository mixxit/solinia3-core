package com.solinia.solinia;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.solinia.solinia.Commands.CommandAA;
import com.solinia.solinia.Commands.CommandAddClass;
import com.solinia.solinia.Commands.CommandAddLootDropItem;
import com.solinia.solinia.Commands.CommandAddLootTableLootDrop;
import com.solinia.solinia.Commands.CommandAddMerchantItem;
import com.solinia.solinia.Commands.CommandAddRace;
import com.solinia.solinia.Commands.CommandAddRaceClass;
import com.solinia.solinia.Commands.CommandCommit;
import com.solinia.solinia.Commands.CommandConvertMerchantToLootDrop;
import com.solinia.solinia.Commands.CommandCreateArmourSet;
import com.solinia.solinia.Commands.CommandCreateFaction;
import com.solinia.solinia.Commands.CommandCreateItem;
import com.solinia.solinia.Commands.CommandCreateLootDrop;
import com.solinia.solinia.Commands.CommandCreateLootTable;
import com.solinia.solinia.Commands.CommandCreateMerchantList;
import com.solinia.solinia.Commands.CommandCreateNPCEvent;
import com.solinia.solinia.Commands.CommandCreateNpc;
import com.solinia.solinia.Commands.CommandCreateNpcCopy;
import com.solinia.solinia.Commands.CommandCreateQuest;
import com.solinia.solinia.Commands.CommandCreateSpawnGroup;
import com.solinia.solinia.Commands.CommandEditClass;
import com.solinia.solinia.Commands.CommandEditFaction;
import com.solinia.solinia.Commands.CommandEditItem;
import com.solinia.solinia.Commands.CommandEditLootDrop;
import com.solinia.solinia.Commands.CommandEditLootTable;
import com.solinia.solinia.Commands.CommandEditNpc;
import com.solinia.solinia.Commands.CommandEditNpcEvent;
import com.solinia.solinia.Commands.CommandEditRace;
import com.solinia.solinia.Commands.CommandEditSpawngroup;
import com.solinia.solinia.Commands.CommandEditSpell;
import com.solinia.solinia.Commands.CommandEffects;
import com.solinia.solinia.Commands.CommandEmote;
import com.solinia.solinia.Commands.CommandFaction;
import com.solinia.solinia.Commands.CommandForceLevel;
import com.solinia.solinia.Commands.CommandForename;
import com.solinia.solinia.Commands.CommandGrantTitle;
import com.solinia.solinia.Commands.CommandGroup;
import com.solinia.solinia.Commands.CommandGroupChat;
import com.solinia.solinia.Commands.CommandIgnore;
import com.solinia.solinia.Commands.CommandLastname;
import com.solinia.solinia.Commands.CommandListFactions;
import com.solinia.solinia.Commands.CommandListItems;
import com.solinia.solinia.Commands.CommandListLootDrops;
import com.solinia.solinia.Commands.CommandListLootTables;
import com.solinia.solinia.Commands.CommandListMerchantLists;
import com.solinia.solinia.Commands.CommandListNPCs;
import com.solinia.solinia.Commands.CommandListSpawnGroups;
import com.solinia.solinia.Commands.CommandListSpells;
import com.solinia.solinia.Commands.CommandLocal;
import com.solinia.solinia.Commands.CommandMana;
import com.solinia.solinia.Commands.CommandNPCBuy;
import com.solinia.solinia.Commands.CommandNPCSell;
import com.solinia.solinia.Commands.CommandOoc;
import com.solinia.solinia.Commands.CommandPerks;
import com.solinia.solinia.Commands.CommandPet;
import com.solinia.solinia.Commands.CommandQuests;
import com.solinia.solinia.Commands.CommandRaceInfo;
import com.solinia.solinia.Commands.CommandRebuildSpellItems;
import com.solinia.solinia.Commands.CommandResetPlayer;
import com.solinia.solinia.Commands.CommandRoll;
import com.solinia.solinia.Commands.CommandSetChannel;
import com.solinia.solinia.Commands.CommandSetClass;
import com.solinia.solinia.Commands.CommandSetGender;
import com.solinia.solinia.Commands.CommandSetLanguage;
import com.solinia.solinia.Commands.CommandSetRace;
import com.solinia.solinia.Commands.CommandSetTitle;
import com.solinia.solinia.Commands.CommandSkills;
import com.solinia.solinia.Commands.CommandSolinia;
import com.solinia.solinia.Commands.CommandSpawnItem;
import com.solinia.solinia.Commands.CommandSpawnRandomItem;
import com.solinia.solinia.Commands.CommandStats;
import com.solinia.solinia.Commands.CommandTarot;
import com.solinia.solinia.Commands.CommandToggleAA;
import com.solinia.solinia.Commands.CommandTrance;
import com.solinia.solinia.Commands.CommandUpdateSpawnGroupLoc;
import com.solinia.solinia.Commands.CommandWho;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Listeners.Solinia3CoreEntityListener;
import com.solinia.solinia.Listeners.Solinia3CoreItemPickupListener;
import com.solinia.solinia.Listeners.Solinia3CoreNPCUpdatedListener;
import com.solinia.solinia.Listeners.Solinia3CorePlayerChatListener;
import com.solinia.solinia.Listeners.Solinia3CorePlayerListener;
import com.solinia.solinia.Listeners.Solinia3CoreSpawnGroupUpdatedListener;
import com.solinia.solinia.Listeners.Solinia3CoreVehicleListener;
import com.solinia.solinia.Managers.ChannelManager;
import com.solinia.solinia.Managers.ConfigurationManager;
import com.solinia.solinia.Managers.EntityManager;
import com.solinia.solinia.Managers.PlayerManager;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Providers.MythicMobsNPCEntityProvider;
import com.solinia.solinia.Repositories.JsonAAAbilityRepository;
import com.solinia.solinia.Repositories.JsonClassRepository;
import com.solinia.solinia.Repositories.JsonFactionRepository;
import com.solinia.solinia.Repositories.JsonItemRepository;
import com.solinia.solinia.Repositories.JsonLootDropRepository;
import com.solinia.solinia.Repositories.JsonLootTableRepository;
import com.solinia.solinia.Repositories.JsonNPCMerchantRepository;
import com.solinia.solinia.Repositories.JsonNPCRepository;
import com.solinia.solinia.Repositories.JsonPatchRepository;
import com.solinia.solinia.Repositories.JsonPlayerRepository;
import com.solinia.solinia.Repositories.JsonQuestRepository;
import com.solinia.solinia.Repositories.JsonRaceRepository;
import com.solinia.solinia.Repositories.JsonSpawnGroupRepository;
import com.solinia.solinia.Repositories.JsonSpellRepository;
import com.solinia.solinia.Repositories.JsonWorldWidePerkRepository;
import com.solinia.solinia.Timers.NPCRandomChatTimer;
import com.solinia.solinia.Timers.NPCSpellCastTimer;
import com.solinia.solinia.Timers.PerkLoadTimer;
import com.solinia.solinia.Timers.PetCheckTickTimer;
import com.solinia.solinia.Timers.PlayerInteractionTimer;
import com.solinia.solinia.Timers.PlayerInventoryValidatorTimer;
import com.solinia.solinia.Timers.PlayerRegenTickTimer;
import com.solinia.solinia.Timers.SpellTickTimer;
import com.solinia.solinia.Timers.StateCommitTimer;

import me.dadus33.chatitem.api.ChatItemAPI;
import net.milkbowl.vault.economy.Economy;

public class Solinia3CorePlugin extends JavaPlugin {

	private StateCommitTimer commitTimer;
	private PlayerRegenTickTimer playerRegenTimer;
	private SpellTickTimer spellTickTimer;
	private NPCSpellCastTimer npcSpellCastTimer;
	private PlayerInteractionTimer playerInteractionTimer;
	private PlayerInventoryValidatorTimer playerInventoryValidatorTimer;
	private NPCRandomChatTimer npcRandomChatTimer;
	private PetCheckTickTimer petCheckTickTimer;

	private Economy economy;
	private ChatItemAPI ciApi;
	private PerkLoadTimer perkLoadTimer;

	@Override
	public void onEnable() {
		System.out.println("[Solinia3Core] Plugin Enabled");
		createConfigDir();
		initialise();
		registerEvents();

		setupEconomy();
		setupChatItem();

		StateManager.getInstance().setEconomy(this.economy);
		StateManager.getInstance().setChatItem(this.ciApi);
	}

	@Override
	public void onDisable() {
		try {
			StateManager.getInstance().getEntityManager().killAllPets();
			StateManager.getInstance().Commit();
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("[Solinia3Core] Plugin Disabled");
	}

	private void setupChatItem() {
		ciApi = Bukkit.getServicesManager().getRegistration(ChatItemAPI.class).getProvider();
	}

	private boolean setupEconomy() {
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager()
				.getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null)
			economy = economyProvider.getProvider();
		return (economy != null);
	}

	private void initialise() {
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

			JsonWorldWidePerkRepository perkrepo = new JsonWorldWidePerkRepository();
			perkrepo.setJsonFile(getDataFolder() + "/" + "worldwideperks.json");
			perkrepo.reload();

			JsonAAAbilityRepository aaabilityrepo = new JsonAAAbilityRepository();
			aaabilityrepo.setJsonFile(getDataFolder() + "/" + "aaabilities.json");
			aaabilityrepo.reload();

			JsonPatchRepository patchesrepo = new JsonPatchRepository();
			patchesrepo.setJsonFile(getDataFolder() + "/" + "patches.json");
			patchesrepo.reload();

			JsonQuestRepository questsrepo = new JsonQuestRepository();
			questsrepo.setJsonFile(getDataFolder() + "/" + "quests.json");
			questsrepo.reload();
			
			PlayerManager playerManager = new PlayerManager(repo);
			EntityManager entityManager = new EntityManager(new MythicMobsNPCEntityProvider());

			ConfigurationManager configurationManager = new ConfigurationManager(racerepo, classrepo, itemrepo,
					spellrepo, factionrepo, npcrepo, npcmerchantrepo, loottablerepo, lootdroprepo, spawngrouprepo,
					perkrepo, aaabilityrepo, patchesrepo, questsrepo);

			ChannelManager channelManager = new ChannelManager();

			StateManager.getInstance().Initialise(playerManager, entityManager, configurationManager, channelManager);

			commitTimer = new StateCommitTimer();
			commitTimer.runTaskTimer(this, 6 * 20L, 300 * 20L);

			playerRegenTimer = new PlayerRegenTickTimer();
			playerRegenTimer.runTaskTimer(this, 6 * 20L, 6 * 20L);

			spellTickTimer = new SpellTickTimer(this);
			spellTickTimer.runTaskTimer(this, 6 * 20L, 6 * 20L);

			playerInteractionTimer = new PlayerInteractionTimer();
			playerInteractionTimer.runTaskTimer(this, 6 * 20L, 6 * 20L);

			perkLoadTimer = new PerkLoadTimer();
			perkLoadTimer.runTaskTimer(this, 6 * 20L, 120 * 20L);

			// Only validate these things every 2 minutes
			playerInventoryValidatorTimer = new PlayerInventoryValidatorTimer();
			playerInventoryValidatorTimer.runTaskTimer(this, 120 * 20L, 120 * 20L);

			npcRandomChatTimer = new NPCRandomChatTimer();
			npcRandomChatTimer.runTaskTimer(this, 6 * 20L, 60 * 20L);

			npcSpellCastTimer = new NPCSpellCastTimer(this);
			npcSpellCastTimer.runTaskTimer(this, 6 * 20L, 6 * 20L);

			petCheckTickTimer = new PetCheckTickTimer();
			petCheckTickTimer.runTaskTimer(this, 6 * 20L, 6 * 20L);

		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void registerEvents() {
		getServer().getPluginManager().registerEvents(new Solinia3CorePlayerListener(this), this);
		getServer().getPluginManager().registerEvents(new Solinia3CoreEntityListener(this), this);
		getServer().getPluginManager().registerEvents(new Solinia3CorePlayerChatListener(this), this);
		getServer().getPluginManager().registerEvents(new Solinia3CoreNPCUpdatedListener(this), this);
		getServer().getPluginManager().registerEvents(new Solinia3CoreSpawnGroupUpdatedListener(this), this);
		getServer().getPluginManager().registerEvents(new Solinia3CoreItemPickupListener(this), this);
		getServer().getPluginManager().registerEvents(new Solinia3CoreVehicleListener(this), this);

		this.getCommand("solinia").setExecutor(new CommandSolinia());
		this.getCommand("commit").setExecutor(new CommandCommit());
		this.getCommand("forename").setExecutor(new CommandForename());
		this.getCommand("lastname").setExecutor(new CommandLastname());
		this.getCommand("mana").setExecutor(new CommandMana());
		this.getCommand("addrace").setExecutor(new CommandAddRace());
		this.getCommand("setrace").setExecutor(new CommandSetRace());
		this.getCommand("addclass").setExecutor(new CommandAddClass());
		this.getCommand("setclass").setExecutor(new CommandSetClass());
		this.getCommand("addraceclass").setExecutor(new CommandAddRaceClass());
		this.getCommand("stats").setExecutor(new CommandStats());
		this.getCommand("resetplayer").setExecutor(new CommandResetPlayer());
		this.getCommand("who").setExecutor(new CommandWho());
		this.getCommand("emote").setExecutor(new CommandEmote());
		this.getCommand("roll").setExecutor(new CommandRoll());
		this.getCommand("setgender").setExecutor(new CommandSetGender());
		this.getCommand("setlanguage").setExecutor(new CommandSetLanguage());
		this.getCommand("tarot").setExecutor(new CommandTarot());
		this.getCommand("skills").setExecutor(new CommandSkills());
		this.getCommand("createitem").setExecutor(new CommandCreateItem());
		this.getCommand("listitems").setExecutor(new CommandListItems());
		this.getCommand("spawnitem").setExecutor(new CommandSpawnItem());
		this.getCommand("spawnrandomitem").setExecutor(new CommandSpawnRandomItem());
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
		this.getCommand("forcelevel").setExecutor(new CommandForceLevel());
		this.getCommand("createmerchantlist").setExecutor(new CommandCreateMerchantList());
		this.getCommand("addmerchantitem").setExecutor(new CommandAddMerchantItem());
		this.getCommand("npcbuy").setExecutor(new CommandNPCBuy());
		this.getCommand("npcsell").setExecutor(new CommandNPCSell());
		this.getCommand("listmerchantlists").setExecutor(new CommandListMerchantLists());
		this.getCommand("edititem").setExecutor(new CommandEditItem());
		this.getCommand("createspawngroup").setExecutor(new CommandCreateSpawnGroup());
		this.getCommand("listspawngroups").setExecutor(new CommandListSpawnGroups());
		this.getCommand("updatespawngrouploc").setExecutor(new CommandUpdateSpawnGroupLoc());
		this.getCommand("ooc").setExecutor(new CommandOoc());
		this.getCommand("setchannel").setExecutor(new CommandSetChannel());
		this.getCommand("group").setExecutor(new CommandGroup());
		this.getCommand("groupchat").setExecutor(new CommandGroupChat());
		this.getCommand("convertmerchanttolootdrop").setExecutor(new CommandConvertMerchantToLootDrop());
		this.getCommand("perks").setExecutor(new CommandPerks());
		this.getCommand("effects").setExecutor(new CommandEffects());
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
