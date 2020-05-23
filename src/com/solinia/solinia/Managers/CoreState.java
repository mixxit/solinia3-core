package com.solinia.solinia.Managers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerSet;

import com.palmergames.bukkit.towny.Towny;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.FellowshipMemberNotFoundException;
import com.solinia.solinia.Exceptions.SoliniaWorldCreationException;
import com.solinia.solinia.Factories.FellowshipFactory;
import com.solinia.solinia.Factories.SoliniaWorldFactory;
import com.solinia.solinia.Interfaces.IChannelManager;
import com.solinia.solinia.Interfaces.IConfigurationManager;
import com.solinia.solinia.Interfaces.IEntityManager;
import com.solinia.solinia.Interfaces.IPlayerManager;
import com.solinia.solinia.Interfaces.ISoliniaClass;
import com.solinia.solinia.Interfaces.ISoliniaGroup;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Models.Fellowship;
import com.solinia.solinia.Models.HINT;
import com.solinia.solinia.Models.SoliniaGroup;
import com.solinia.solinia.Models.SoliniaSpell;
import com.solinia.solinia.Models.SoliniaZone;
import com.solinia.solinia.Utils.ChatUtils;
import com.solinia.solinia.Utils.MathUtils;
import com.solinia.solinia.Utils.NPCUtils;
import com.solinia.solinia.Utils.PartyWindowUtils;
import com.solinia.solinia.Utils.Utils;

import de.slikey.effectlib.EffectManager;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

public class CoreState {
	private boolean isInitialised = false;
	private IPlayerManager playerManager;
	private IEntityManager entityManager;
	private IConfigurationManager configurationManager;
	private Economy economy;
	private boolean isStopping = false;
	private IChannelManager channelManager;
	//private ConcurrentHashMap<UUID, BossBar> bossbars = new ConcurrentHashMap<UUID, BossBar>();
	private ConcurrentHashMap<UUID, ISoliniaGroup> groups = new ConcurrentHashMap<UUID, ISoliniaGroup>();
	private ConcurrentHashMap<UUID, UUID> groupinvites = new ConcurrentHashMap<UUID, UUID>();
	private ConcurrentHashMap<Integer, Integer> fellowshipinvites = new ConcurrentHashMap<Integer, Integer>();
	private String instanceGuid;
	private EffectManager effectManager;
	
	public Map<String, AreaMarker> soliniazonesresareas = new HashMap<String, AreaMarker>();
    public Map<String, Marker> soliniazonesresmark = new HashMap<String, Marker>();

	public Map<String, AreaMarker> regionextentsresareas = new HashMap<String, AreaMarker>();
    public Map<String, Marker> regionextentsresmark = new HashMap<String, Marker>();

    
	private List<Integer> currentHotZones = new ArrayList<Integer>();
	private double xpdaybonus = 0;
	private String requiredModVersion;
	private DynmapAPI dynmap;
	private MarkerSet regionExtentsMarkerSet;
	private MarkerSet soliniaZonesMarkerSet;
	private Towny towny;
	public String renderTownsOnDynmap = "";
	public boolean showSpawns = false;
	public boolean charChange = false;

	public String getRequiredModVersion()
	{
		return this.requiredModVersion;
	}
	
	public Plugin getPlugin()
	{
		return Bukkit.getPluginManager().getPlugin("Solinia3Core");
	}
		
	public List<SoliniaZone> getCurrentHotzones() {
		List<SoliniaZone> zones = new ArrayList<SoliniaZone>();
		for(Integer zoneid : currentHotZones)
		{
			try {
				zones.add(StateManager.getInstance().getConfigurationManager().getZone(zoneid));
			} catch (CoreStateInitException e) {
			}
		}
		
		return zones;
	}
	
	public void forceHotzone(int hotzone, boolean clear) throws Exception
	{
		try {
			for(SoliniaZone zone : StateManager.getInstance().getConfigurationManager().getZones())
			{
				if (!zone.isHotzone())
					continue;
				
				if (zone.getId() == hotzone)
				{
					if (clear == true)
					this.currentHotZones.clear();
					
					this.currentHotZones.add(zone.getId());
					System.out.println("Hotzone set to: " + zone.getName());
					return;
				}
				
			}
			throw new Exception("Could not set hotzone");
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setRandomHotzones()
	{
		currentHotZones.clear();
		try {
			int maxCheckSize = StateManager.getInstance().getConfigurationManager().getZones().size()*4;
			int currentCount = 0;
			
			List<SoliniaZone> possibleHotZones = new ArrayList<SoliniaZone>();
			
			for(SoliniaZone zone : StateManager.getInstance().getConfigurationManager().getZones())
			{
				if (!zone.isHotzone())
					continue;
				
				possibleHotZones.add(zone);
				
			}
		
			// if there isnt any just skip this
			if (possibleHotZones.size() < 1)
				return;
			
			
			// If only one, just always return it
			if (possibleHotZones.size() == 1)
			{
				currentHotZones.add(possibleHotZones.get(0).getId());
				return;
			}
			
			// Else try to pick two random ones
			while (currentHotZones.size() < 2)
			{
				if (currentCount > maxCheckSize)
					break;
				
					SoliniaZone zone = MathUtils.getRandomItemFromList(possibleHotZones);
					if (!zone.isHotzone())
						continue;
					
					if (currentHotZones.contains(zone.getId()))
						continue;
					
					System.out.println("Hotzone set to: " + zone.getName());
					currentHotZones.add(zone.getId());
			}
		
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public CoreState()
	{
		isInitialised = false;
		final int SHORT_ID_LENGTH = 8;
		this.instanceGuid = RandomStringUtils.random(SHORT_ID_LENGTH);
		
		// xp bonus every day now
		xpdaybonus = 100;
		
		// If its tuesday or sunday you will get 100% bonus with XP
		Date now = new Date();
		SimpleDateFormat simpleDateformat = new SimpleDateFormat("E");
		if (simpleDateformat.format(now).equals("Tue") || simpleDateformat.format(now).equals("Wed") || simpleDateformat.format(now).equals("Sun"))
		{
			xpdaybonus += 100;
		}
		simpleDateformat = new SimpleDateFormat("dd.MM");

		// Easter Holidays
		if (simpleDateformat.format(now).equals("10.04") || simpleDateformat.format(now).equals("11.04") || simpleDateformat.format(now).equals("12.04") || simpleDateformat.format(now).equals("13.04"))
		{
			xpdaybonus += 200;
		}
		
		if (simpleDateformat.format(now).equals("19.04") || simpleDateformat.format(now).equals("20.04") || simpleDateformat.format(now).equals("21.04") || simpleDateformat.format(now).equals("22.04"))
		{
			xpdaybonus += 200;
		}
		
		// Rogue mourning holiday
		if (simpleDateformat.format(now).equals("15.01") || simpleDateformat.format(now).equals("16.01") || simpleDateformat.format(now).equals("19.01"))
		{
			xpdaybonus += 200;
		}
				
		// Belara Day
		if (simpleDateformat.format(now).equals("18.03") || simpleDateformat.format(now).equals("19.03") || simpleDateformat.format(now).equals("20.03") || simpleDateformat.format(now).equals("21.03") || simpleDateformat.format(now).equals("22.03") || simpleDateformat.format(now).equals("23.03"))
		{
			xpdaybonus = 200;
		}

		// Xmas and New Year
		if (simpleDateformat.format(now).equals("24.12") || simpleDateformat.format(now).equals("25.12") || simpleDateformat.format(now).equals("26.12") || simpleDateformat.format(now).equals("27.12") || simpleDateformat.format(now).equals("28.12") || simpleDateformat.format(now).equals("29.12") || simpleDateformat.format(now).equals("30.12") || simpleDateformat.format(now).equals("31.12") || simpleDateformat.format(now).equals("01.01") || simpleDateformat.format(now).equals("02.01"))
		{
			xpdaybonus = 200;
		}

		// 4th of July
		if (simpleDateformat.format(now).equals("04.07") || simpleDateformat.format(now).equals("05.07") || simpleDateformat.format(now).equals("06.07") || simpleDateformat.format(now).equals("07.07"))
		{
			xpdaybonus = 200;
		}

		// 14th of Feb - Valentines
		if (simpleDateformat.format(now).equals("14.02") || simpleDateformat.format(now).equals("15.02"))
		{
			xpdaybonus = 200;
		}
		
		// Halloween
		if (simpleDateformat.format(now).equals("30.10") || simpleDateformat.format(now).equals("31.10") || simpleDateformat.format(now).equals("01.11"))
		{
			xpdaybonus = 200;
		}

	}
	
	public void setEconomy(Economy economy) {
		// TODO Auto-generated method stub
		this.economy = economy;
	}
	
	public Economy getEconomy() {
		// TODO Auto-generated method stub
		return this.economy;
	}
	
	public void Initialise(IPlayerManager playerManager, IEntityManager entityManager, IConfigurationManager configurationManager, ChannelManager channelManager, EffectManager effectManager) throws CoreStateInitException
	{
		if (isInitialised == true)
			throw new CoreStateInitException("State already initialised");
		
		this.playerManager = playerManager;
		this.entityManager = entityManager;
		this.configurationManager = configurationManager;
		this.channelManager = channelManager;
		this.effectManager = effectManager;
		
		isInitialised = true;
		
		OnInitialized();
	}
	
	public void OnInitialized()
	{
		for(World world : Bukkit.getWorlds())
		{
			if (this.configurationManager.getWorld(world.getName().toUpperCase()) == null)
			{
				try {
					SoliniaWorldFactory.Create(world.getName());
					System.out.println("World Created: " + world.getName());
					
				} catch (SoliniaWorldCreationException e) {
					
				} catch (CoreStateInitException e) {
					
				}
			}
		}
		
		StateManager.getInstance().setRandomHotzones();
		
		StateManager.getInstance().patchVersion();
	}
	
	private void patchVersion() {
		fixState();
		patchItems1_13();
		patchClasses1_13();
		fixPets();
		fixTimeto();
	}

	private void fixTimeto()
	{
		try {
			boolean updated = false;
			
			System.out.println("Attempting to fix incorrectly set npcs timeto spawns");
			
			List<String> unknownMaterialNames = new ArrayList<String>();
			
			for(ISoliniaNPC npc : StateManager.getInstance().getConfigurationManager().getNPCs())
			{
				if (npc.getTimeto() == 23850L)
				{
					npc.setTimeto(Utils.MAXDAYTICK);
					updated = true;
				}
			}
			
			if (updated == true)
			{
				System.out.println("Detected some internal npc changes, recommitting npcs (this may take some time)...");
				NPCUtils.RecommitNpcs();
			}
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void fixPets()
	{
		HashMap<String, Integer> pets = new HashMap<String, Integer>();
		pets.put("#Pixtt_Battlestrategist_pet",18000);pets.put("#Pixtt_Slaughteress_pet",18000);pets.put("#Pixtt_Venomblade_pet",18000);pets.put("animateDead",4500);pets.put("Animation1",11);pets.put("Animation10",668);pets.put("Animation11",815);pets.put("Animation12",1438);pets.put("Animation13",1550);pets.put("Animation14",1750);pets.put("Animation15",2275);pets.put("Animation2",46);pets.put("Animation3",126);pets.put("Animation4",159);pets.put("Animation5",251);pets.put("Animation6",324);pets.put("Animation7",404);pets.put("Animation8",483);pets.put("Animation9",568);pets.put("a_jester_of_bristlebane",100);pets.put("bardSwarm",99);pets.put("BestialAid",6000);pets.put("blood_skeleton",5000);pets.put("blood_skeleton2",5000);pets.put("BLpet09",190);pets.put("BLpet16",380);pets.put("BLpet22",686);pets.put("BLpet26",819);pets.put("BLpet31",1300);pets.put("BLpet39",2200);pets.put("BLpet41",2400);pets.put("BLpet43",2550);pets.put("BLpet45",2700);pets.put("BLpet47",2900);pets.put("BLpet49",4000);pets.put("BLpet51",4200);pets.put("BLpet67",5460);pets.put("Blpet70",5750);pets.put("Burnout",120);pets.put("CasterWolfFamiliar",100);pets.put("cleric_hammer_67_",400);pets.put("CollectorGnollPup",100);pets.put("CompanionOfNecessity",120000);pets.put("CompanionOfNecessity2",450000);pets.put("CompanionOfNecessity3",1500000);pets.put("CompanionOfNecessity4",1500000);pets.put("DruidPet",480);pets.put("Familiar1",1000);pets.put("Familiar2",1000);pets.put("Familiar3",1000);pets.put("Familiar4",1000);pets.put("Familiar5",1000);pets.put("Familiar6",1000);pets.put("FamiliarAquaGoblin",100);pets.put("FamiliarAzureDragon",100);pets.put("FamiliarBattleOgre",100);pets.put("FamiliarBazu",100);pets.put("FamiliarBitterBrownie",100);pets.put("FamiliarBixie",100);pets.put("FamiliarBluntForce",100);pets.put("FamiliarBrewworks",100);pets.put("FamiliarClickingBeetle",100);pets.put("FamiliarClockworkToy",100);pets.put("FamiliarCoralSerpent",100);pets.put("FamiliarCrimsonSpiroc",100);pets.put("FamiliarDragon",100);pets.put("FamiliarDragonSeer",100);pets.put("FamiliarEmeraldDragon",100);pets.put("FamiliarEmeraldSokokar",100);pets.put("FamiliarEmpatheticOoze",100);pets.put("FamiliarFairy",100);pets.put("FamiliarFieryPyrelin",100);pets.put("FamiliarFire",1000);pets.put("FamiliarFPBolrog",100);pets.put("FamiliarFrostDragon",100);pets.put("FamiliarGirplan",100);pets.put("FamiliarGorgedEye",100);pets.put("FamiliarGreaterFire",1000);pets.put("FamiliarGreaterIce",1000);pets.put("FamiliarGreaterMagic",1000);pets.put("FamiliarHoodedScrykin",100);pets.put("FamiliarIce",1000);pets.put("FamiliarIcyGelidran",100);pets.put("FamiliarKillerAttackDoll",100);pets.put("FamiliarKoboldKing",100);pets.put("FamiliarKunarkGoblin",100);pets.put("FamiliarLeopardCub",100);pets.put("FamiliarLesserAir",100);pets.put("FamiliarLesserEarth",100);pets.put("FamiliarLightningWarrior",100);pets.put("FamiliarLionCub",100);pets.put("FamiliarMagic",1000);pets.put("FamiliarMinotaur",100);pets.put("FamiliarOldFroglok",100);pets.put("FamiliarOrangeScorpion",100);pets.put("FamiliarPantherCub",100);pets.put("FamiliarPhasedScrykin",100);pets.put("FamiliarPolarBear",100);pets.put("FamiliarProtectiveMimic",100);pets.put("FamiliarQueenHarpy",100);pets.put("FamiliarRazorsEdge",100);pets.put("FamiliarRedBat",100);pets.put("FamiliarRuneboundGoblin",100);pets.put("FamiliarRunicGargoyle",100);pets.put("FamiliarSeaSerpent",100);pets.put("FamiliarShadowWolf",100);pets.put("FamiliarSpectralBanshee",100);pets.put("FamiliarSpikedWorg",100);pets.put("FamiliarStoneGargoyle",100);pets.put("FamiliarTigerCub",100);pets.put("FamiliarUndeadOldFroglok",100);pets.put("FamiliarVanishingPoint",100);pets.put("FamiliarWailingBanshee",100);pets.put("FamiliarZombieHostess",100);pets.put("GoblinFamiliar",100);pets.put("mageSwarm",5000);pets.put("mageSwarm2",7000);pets.put("MeleeWolfFamiliar",100);pets.put("Mistwalker",1);pets.put("MonsterSum1",1170);pets.put("MonsterSum2",3180);pets.put("MonsterSum3",3300);pets.put("MonsterSum4",4200);pets.put("PetVeteranRewardJester",100);pets.put("PETVeteranRewardServant",100);pets.put("PyronicAssault",20000);pets.put("RagingServant",9000);pets.put("ServantRo",600);pets.put("shaman_wolf_67_",2900);pets.put("skel_pet_11_",288);pets.put("skel_pet_16_",456);pets.put("skel_pet_19_",588);pets.put("skel_pet_1_",30);pets.put("skel_pet_22_",732);pets.put("skel_pet_25_",876);pets.put("skel_pet_29_",1032);pets.put("skel_pet_33_",1212);pets.put("skel_pet_37_",1481);pets.put("skel_pet_41_",2615);pets.put("skel_pet_43_",1440);pets.put("skel_pet_44_",1500);pets.put("skel_pet_47_",2760);pets.put("skel_pet_5_",84);pets.put("skel_pet_61_",3500);pets.put("skel_pet_63_",2800);pets.put("skel_pet_65_",4000);pets.put("skel_pet_66_",5000);pets.put("skel_pet_67_",5830);pets.put("skel_pet_70_",4100);pets.put("skel_pet_9_",228);pets.put("SKSkeletonSwarm",10000);pets.put("sk_pet_68_",2900);pets.put("SpiritWolf224",824);pets.put("SpiritWolf227",994);pets.put("SpiritWolf230",1256);pets.put("SpiritWolf234",1623);pets.put("SpiritWolf237",1803);pets.put("SpiritWolf242",2600);pets.put("SporaliFamiliar",50);pets.put("SporaliFamiliar2",100);pets.put("SporaliSwarmPet50",551);pets.put("SumAirR10",1015);pets.put("SumAirR11",1225);pets.put("SumAirR12",2205);pets.put("SumAirR13",2410);pets.put("SumAirR14",2700);pets.put("SumAirR15",3800);pets.put("SumAirR16",5600);pets.put("SumAirR2",75);pets.put("SumAirR3",175);pets.put("SumAirR4",230);pets.put("SumAirR5",360);pets.put("SumAirR6",460);pets.put("SumAirR7",580);pets.put("SumAirR8",700);pets.put("SumAirR9",800);pets.put("SumBearSpirit",500);pets.put("SumCelestialSpirit",500);pets.put("SumDecoy",3120);pets.put("SumDireWolf",6500);pets.put("SumEarthR10",1450);pets.put("SumEarthR11",1750);pets.put("SumEarthR12",3150);pets.put("SumEarthR13",3200);pets.put("SumEarthR14",3300);pets.put("SumEarthR15",5300);pets.put("SumEarthR16",7000);pets.put("SumEarthR2",95);pets.put("SumEarthR3",250);pets.put("SumEarthR4",350);pets.put("SumEarthR5",520);pets.put("SumEarthR6",675);pets.put("SumEarthR7",830);pets.put("SumEarthR8",1000);pets.put("SumEarthR9",1150);pets.put("SumFireR10",725);pets.put("SumFireR11",875);pets.put("SumFireR12",1575);pets.put("SumFireR13",1900);pets.put("SumFireR14",2080);pets.put("SumFireR15",2400);pets.put("SumFireR16",3060);pets.put("SumFireR2",50);pets.put("SumFireR3",125);pets.put("SumFireR4",180);pets.put("SumFireR5",260);pets.put("SumFireR6",340);pets.put("SumFireR7",415);pets.put("SumFireR8",500);pets.put("SumFireR9",575);pets.put("SumFireSword",300);pets.put("SumHammer",99);pets.put("SumMageMultiElement",4300);pets.put("SummonClockworkBanker",80);pets.put("SummonEye",1);pets.put("SummonResupplyAgent",80);pets.put("SumSword",1000);pets.put("SumWaterR10",1160);pets.put("SumWaterR11",1400);pets.put("SumWaterR12",2520);pets.put("SumWaterR13",2350);pets.put("SumWaterR14",2450);pets.put("SumWaterR15",3600);pets.put("SumWaterR16",5000);pets.put("SumWaterR2",80);pets.put("SumWaterR3",200);pets.put("SumWaterR4",280);pets.put("SumWaterR5",420);pets.put("SumWaterR6",540);pets.put("SumWaterR7",660);pets.put("SumWaterR8",800);pets.put("SumWaterR9",920);pets.put("SwarmGolem",10500);pets.put("SwarmPet809",10000);pets.put("SwarmPet810",20000);pets.put("SwarmPet811",20000);pets.put("SwarmPetBard",1900);pets.put("SwarmPetBeastlord",1900);pets.put("SwarmPetDG1",1000);pets.put("SwarmPetDG2",2000);pets.put("SwarmPetDG3",3000);pets.put("SwarmPetImp",400);pets.put("SwarmPetLDONSar",3500);pets.put("SwarmPetMonk",1900);pets.put("SwarmPetPaladin",1900);pets.put("SwarmPetRanger",1900);pets.put("SwarmPetRogue",1900);pets.put("SwarmPetShadowknight",1900);pets.put("SwarmPetSoF1",500);pets.put("SwarmPetSwarmofDecay2",400);pets.put("SwarmPetSwarmofDecay3",400);pets.put("SwarmPetSwarmofDecay4",400);pets.put("SwarmPetWarrior",1900);pets.put("TunareBane",2500);pets.put("UkunPet250",5895);pets.put("UkunPet256",6000);pets.put("VetReward12DistillerMerchant",906);pets.put("VetReward13TributeMaster",8084);pets.put("VSP2HoshkarPet",30000);pets.put("VSP2NexonaPet",5000);pets.put("VSP2SilverwingPet",50000);pets.put("WARDClericPet1",100000);pets.put("WARDClericPet10",100000);pets.put("WARDClericPet11",100000);pets.put("WARDClericPet2",100000);pets.put("WARDClericPet3",100000);pets.put("WARDClericPet4",100000);pets.put("WARDClericPet5",100000);pets.put("WARDClericPet6",100000);pets.put("WARDClericPet7",100000);pets.put("WARDClericPet8",100000);pets.put("WARDClericPet9",100000);pets.put("WARDDruidPet1",100000);pets.put("WARDDruidPet2",100000);pets.put("WARDDruidPet3",100000);pets.put("WARDDruidPet4",100000);pets.put("WARDDruidPet5",100000);pets.put("WARDDruidPet6",100000);pets.put("WARDDruidPet7",100000);pets.put("WARDDruidPet8",100000);pets.put("WARDShamanPet1",100000);pets.put("WARDShamanPet2",100000);pets.put("WARDShamanPet3",100000);pets.put("WARDShamanPet4",100000);pets.put("WARDShamanPet5",100000);pets.put("WARDShamanPet6",100000);pets.put("WARDShamanPet7",100000);pets.put("WARDShamanPet8",100000);pets.put("WARDWizardPet1",100000);pets.put("WARDWizardPet2",100000);pets.put("WARDWizardPet3",100000);pets.put("WARDWizardPet4",100000);pets.put("WARDWizardPet5",100000);pets.put("wizard_sword_67_",406); 
		for(String key : pets.keySet())
		{
			int hp = pets.get(key);
			try
			{
				ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getPetNPCByName(key);
				if (npc == null)
					continue;
				
				if (npc.getForcedMaxHp() != hp)
				{
					npc.setForcedMaxHp(hp);
					System.out.println("Patched Pet [" + key + "] HP: " + hp);
				}
			} catch (CoreStateInitException e)
			{
				
			}
		}
	}
	
	private void fixState()
	{
		
	}
	
	private void patchClasses1_13() {
		try {
			for(ISoliniaClass baseClass : StateManager.getInstance().getConfigurationManager().getClasses())
			{
				patchClass1_13(baseClass);
			}
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void patchClass1_13(ISoliniaClass baseClass) {
		if (!getNewMaterialName(baseClass.getDefaulthandMaterial()).equals(baseClass.getDefaulthandMaterial()))
		{
			baseClass.setDefaulthandMaterial(getNewMaterialName(baseClass.getDefaulthandMaterial()));
		}
		
		if (!getNewMaterialName(baseClass.getDefaultoffHandMaterial()).equals(baseClass.getDefaultoffHandMaterial()))
		{
			baseClass.setDefaultoffHandMaterial(getNewMaterialName(baseClass.getDefaultoffHandMaterial()));
		}
		
		if (!getNewMaterialName(baseClass.getDefaultHeadMaterial()).equals(baseClass.getDefaultHeadMaterial()))
		{
			baseClass.setDefaultHeadMaterial(getNewMaterialName(baseClass.getDefaultHeadMaterial()));
		}
		
		if (!getNewMaterialName(baseClass.getDefaultChestMaterial()).equals(baseClass.getDefaultChestMaterial()))
		{
			baseClass.setDefaultChestMaterial(getNewMaterialName(baseClass.getDefaultChestMaterial()));
		}
		
		if (!getNewMaterialName(baseClass.getDefaultLegsMaterial()).equals(baseClass.getDefaultLegsMaterial()))
		{
			baseClass.setDefaultLegsMaterial(getNewMaterialName(baseClass.getDefaultLegsMaterial()));
		}
		
		if (!getNewMaterialName(baseClass.getDefaultFeetMaterial()).equals(baseClass.getDefaultFeetMaterial()))
		{
			baseClass.setDefaultFeetMaterial(getNewMaterialName(baseClass.getDefaultFeetMaterial()));
		}
	}

	private String getNewMaterialName(String materialName) {
		if (materialName.toUpperCase().equals("WATCH"))
			return("CLOCK");
		
		if (materialName.toUpperCase().equals("CARROT_ITEM"))
			return("LEGACY_CARROT_ITEM");

		if (materialName.toUpperCase().equals("POTATO_ITEM"))
			return("LEGACY_POTATO_ITEM");
		
		if (materialName.toUpperCase().equals("WOOL"))
			return("LEGACY_WOOL");
		
		if (materialName.toUpperCase().equals("NETHER_STALK"))
			return("LEGACY_NETHER_STALK");
		
		if (materialName.toUpperCase().equals("INK_SACK"))
			return("LEGACY_INK_SACK");
		
		if (materialName.toUpperCase().equals("RECORD_3"))
			return("LEGACY_RECORD_3");
		if (materialName.toUpperCase().equals("RECORD_4"))
			return("LEGACY_RECORD_4");
		if (materialName.toUpperCase().equals("RECORD_5"))
			return("LEGACY_RECORD_5");
		if (materialName.toUpperCase().equals("RECORD_6"))
			return("LEGACY_RECORD_6");
		if (materialName.toUpperCase().equals("RECORD_7"))
			return("LEGACY_RECORD_7");
		if (materialName.toUpperCase().equals("RECORD_8"))
			return("LEGACY_RECORD_8");
		if (materialName.toUpperCase().equals("RECORD_9"))
			return("LEGACY_RECORD_9");
		if (materialName.toUpperCase().equals("RECORD_10"))
			return("LEGACY_RECORD_10");
		if (materialName.toUpperCase().equals("RECORD_11"))
			return("LEGACY_RECORD_11");
		if (materialName.toUpperCase().equals("RECORD_12"))
			return("LEGACY_RECORD_12");
		
		if (materialName.toUpperCase().equals("WATCH"))
			return("CLOCK");

		if (materialName.toUpperCase().equals("SKULL_ITEM"))
			return("LEGACY_SKULL_ITEM");
		
		if (materialName.toUpperCase().equals("RAW_FISH"))
			return("LEGACY_RAW_FISH");
		
		if (materialName.toUpperCase().equals("WOOD_HOE"))
			return("WOODEN_HOE");

		if (materialName.toUpperCase().equals("WOOD_AXE"))
			return("WOODEN_AXE");
		
		if (materialName.toUpperCase().equals("WOOD_SWORD"))
			return("WOODEN_SWORD");
		
		if (materialName.toUpperCase().equals("WOOD_PICKAXE"))
			return("WOODEN_PICKAXE");
		
		if (materialName.toUpperCase().equals("GOLD_HOE"))
			return("GOLDEN_HOE");
		
		if (materialName.toUpperCase().equals("GOLD_AXE"))
			return("GOLDEN_AXE");
		
		if (materialName.toUpperCase().equals("GOLD_SWORD"))
			return("GOLDEN_SWORD");
		
		if (materialName.toUpperCase().equals("GOLD_PICKAXE"))
			return("GOLDEN_PICKAXE");
		
		if (materialName.toUpperCase().equals("WOOD_SPADE"))
			return("WOODEN_SHOVEL");
		
		if (materialName.toUpperCase().equals("STONE_SPADE"))
			return("STONE_SHOVEL");
		
		if (materialName.toUpperCase().equals("IRON_SPADE"))
			return("IRON_SHOVEL");
			
		if (materialName.toUpperCase().equals("GOLD_SPADE"))
			return("GOLDEN_SHOVEL");
		
		if (materialName.toUpperCase().equals("DIAMOND_SPADE"))
			return("DIAMOND_SHOVEL");
		
		// Gold to Golden
		if (materialName.toUpperCase().equals("GOLD_HELMET"))
			return("GOLDEN_HELMET");
		
		if (materialName.toUpperCase().equals("GOLD_CHESTPLATE"))
			return("GOLDEN_CHESTPLATE");
		
		if (materialName.toUpperCase().equals("GOLD_LEGGINGS"))
			return("GOLDEN_LEGGINGS");
		
		if (materialName.toUpperCase().equals("GOLD_BOOTS"))
			return "GOLDEN_BOOTS";
		
		if (materialName.toUpperCase().equals("RED_ROSE"))
			return "LEGACY_RED_ROSE";
		
		if (materialName.toUpperCase().equals("RAW_BEEF"))
			return "LEGACY_RAW_BEEF";

		if (materialName.toUpperCase().equals("WEB"))
			return "LEGACY_WEB";

		if (materialName.toUpperCase().equals("COOKED_FISH"))
			return "LEGACY_COOKED_FISH";
		
		if (materialName.toUpperCase().equals("POTATO_ITEM"))
			return "LEGACY_POTATO_ITEM";

		if (materialName.toUpperCase().equals("SNOW_BALL"))
			return "LEGACY_SNOW_BALL";

		if (materialName.toUpperCase().equals("SKULL"))
			return "LEGACY_SKULL";

		if (materialName.toUpperCase().equals("FIREWORK"))
			return "LEGACY_FIREWORK";
		
		if (materialName.toUpperCase().equals("SULPHUR"))
			return "LEGACY_SULPHUR";

		if (materialName.toUpperCase().equals("GREEN_RECORD"))
			return "LEGACY_GREEN_RECORD";

		if (materialName.toUpperCase().equals("QUARTZ_ORE"))
			return "LEGACY_QUARTZ_ORE";

		if (materialName.toUpperCase().equals("BANNER"))
			return "LEGACY_BANNER";

		if (materialName.toUpperCase().equals("LOG"))
			return "LEGACY_LOG";

		if (materialName.toUpperCase().equals("SEEDS"))
			return "LEGACY_SEEDS";
		
		if (materialName.toUpperCase().equals("LOG_2"))
			return "LEGACY_LOG_2";

		if (materialName.toUpperCase().equals("BOAT"))
			return "LEGACY_BOAT";

		if (materialName.toUpperCase().equals("SAPLING"))
			return "LEGACY_SAPLING";

		if (materialName.toUpperCase().equals("CARROT_ITEM"))
			return "LEGACY_CARROT_ITEM";

		if (materialName.toUpperCase().equals("MONSTER_EGG"))
			return "LEGACY_MONSTER_EGG";

		if (materialName.toUpperCase().equals("REDSTONE_TORCH_ON"))
			return "LEGACY_REDSTONE_TORCH_ON";

		if (materialName.toUpperCase().equals("LONG_GRASS"))
			return "LEGACY_LONG_GRASS";

		return materialName.toUpperCase();
	}

	private void patchItems1_13() {
		try {
			boolean updated = false;
			
			System.out.println("Attempting to fix 1.13 items");
			
			List<String> unknownMaterialNames = new ArrayList<String>();
			
			for(ISoliniaItem item : StateManager.getInstance().getConfigurationManager().getItems())
			{
				// Try to find new materials we dont know about
				try
				{
					ItemStack stack = new ItemStack(Material.valueOf(item.getBasename().toUpperCase()), 1, item.getColor());
				} catch (Exception e)
				{
					if (!unknownMaterialNames.contains(item.getBasename()))
						unknownMaterialNames.add(item.getBasename());
				}
				
				if (!getNewMaterialName(item.getBasename()).equals(item.getBasename()))
				{
					item.setBasename(getNewMaterialName(item.getBasename()));
					updated = true;
				}
			}
			
			if (updated == true)
			{
				System.out.println("Detected some internal item changes, recommitting npcs (this may take some time)...");
				NPCUtils.RecommitNpcs();
			}
			
			System.out.println("[Solinia3Core] UNKNOWN MATERIAL NAMES:");
			for(String unknownMaterialName : unknownMaterialNames)
			{
				System.out.println("[UNKNOWNMAPPING] " + unknownMaterialName);
			}
			
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public IPlayerManager getPlayerManager() throws CoreStateInitException
	{
		if (isInitialised == false)
			throw new CoreStateInitException("State not initialised");
		
		return playerManager;
	}
	
	public IConfigurationManager getConfigurationManager() throws CoreStateInitException
	{
		if (isInitialised == false)
			throw new CoreStateInitException("State not initialised");
		
		return configurationManager;
	}
	
	public EffectManager getEffectManager()
	{
		return this.effectManager;
	}

	public void Commit() throws CoreStateInitException {
		if (isInitialised == false)
			throw new CoreStateInitException("State not initialised");
		System.out.println("Commit");
		try {
			ChatUtils.SendHintToServer(HINT.SERVER_SAVE_BEGIN, "");
			configurationManager.commit();
			ChatUtils.SendHintToServer(HINT.SERVER_SAVE_FINISH,"");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public IEntityManager getEntityManager() throws CoreStateInitException {
		if (isInitialised == false)
			throw new CoreStateInitException("State not initialised");
		
		return entityManager;
	}

	public void giveMoney(Player player, int amount) {
		if (getEconomy() == null)
			return;
		
    	EconomyResponse responsedeposit = getEconomy().depositPlayer(player, amount);
		if(responsedeposit.transactionSuccess()) 
		{
			player.sendMessage(ChatColor.YELLOW + "* You recieve $" + amount);
		} else {
			System.out.println("giveMoney - Error depositing money to users account " + String.format(responsedeposit.errorMessage));
		}
    	
		return;
		
	}

	public IChannelManager getChannelManager() {
		// TODO Auto-generated method stub
		return this.channelManager;
	}

	public boolean addActiveBlockEffect(Block clickedBlock, SoliniaSpell soliniaSpell, LivingEntity sourceEntity) {
		// TODO Auto-generated method stub
		return false;
	}

	public void spellTick(Plugin plugin) {
		entityManager.spellTick();
	}

	public ISoliniaGroup getGroupByMember(UUID uniqueId) {
		for (Map.Entry<UUID, ISoliniaGroup> entry : groups.entrySet()) {
			if (entry.getValue().getMembers().contains(uniqueId))
				return groups.get(entry.getKey());
		}
		
		return null;
	}
	
	public ISoliniaGroup getGroupByOwner(UUID uniqueId) {
		for (Map.Entry<UUID, ISoliniaGroup> entry : groups.entrySet()) {
			if (entry.getValue().getOwner() == uniqueId)
				return groups.get(entry.getKey());
		}
		
		return null;
	}
	
	public ISoliniaGroup getGroup(UUID groupId) {
		return groups.get(groupId);
	}
	
	public Fellowship createNewFellowship(ISoliniaPlayer leader) {
		try {
			Fellowship fellowship = FellowshipFactory.CreateFellowship(leader.getId());
			leader.setCharacterFellowshipId(fellowship.getId());
			return fellowship;
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public ISoliniaGroup createNewGroup(Player leader) {
		UUID newgroupid = UUID.randomUUID();
		SoliniaGroup group = new SoliniaGroup();
		group.setId(newgroupid);
		group.setOwner(leader.getUniqueId());
		group.getMembers().add(leader.getUniqueId());
		groups.put(newgroupid, group);
		return groups.get(newgroupid);
	}

	public UUID getPlayerInviteGroupID(Player player) {
		return groupinvites.get(player.getUniqueId());
	}
	
	public Integer getCharacterInviteFellowshipID(ISoliniaPlayer solplayer) {
		return fellowshipinvites.get(solplayer.getId());
	}
	
	public Integer removeFellowshipInvite(ISoliniaPlayer solplayer) {
		return fellowshipinvites.remove(solplayer.getId());
	}

	public UUID removePlayerGroupInvite(Player player) {
		return groupinvites.remove(player.getUniqueId());
	}
	
	public void sendGroupPacketToAllPlayers(ISoliniaGroup group)
	{
		for(UUID uuid : group.getMembers())
		{
			PartyWindowUtils.UpdateGroupWindow(uuid,group, false, false);
		}
	}
	
	public void removePlayerFromGroup(Player player) {
		int playerCharacterId = 0;
		try
		{
			if (player.isOnline() && player != null)
			{
				ISoliniaPlayer playerToRemove = SoliniaPlayerAdapter.Adapt(player);
				if (playerToRemove != null && playerToRemove.isMentoring())
					playerToRemove.setMentor(null);

				playerCharacterId = playerToRemove.getId();
				
				
			}
		} catch (CoreStateInitException e)
		{
			
		}
		
		ISoliniaGroup group = getGroupByMember(player.getUniqueId());

		if (group == null) {
			removePlayerGroupInvite(player);
			player.sendMessage("That group doesn't exist");
			return;
		}

		System.out.println("group: " + group.getId() + " lost a member: " + player.getDisplayName());
		sendGroupMessage(player, "has left the group!");
		group.getMembers().remove(player.getUniqueId());
		
		PartyWindowUtils.UpdateGroupWindow(player.getUniqueId(), null, false, true);
		sendGroupPacketToAllPlayers(group);
		
		try
		{
			if (playerCharacterId > 0)
			for (int i = 0; i < group.getMembers().size(); i++) {
				UUID member = group.getMembers().get(i);
				Player memberPlayer = Bukkit.getPlayer(member);
				if (memberPlayer != null) {
					ISoliniaPlayer playerMember = SoliniaPlayerAdapter.Adapt(memberPlayer);
					if (playerMember != null && playerMember.isMentoring() && playerMember.getMentoringCharacterId() == playerCharacterId)
						playerMember.setMentor(null);
				}
			}
		} catch (CoreStateInitException e)
		{
			
		}

		if (group.getOwner().equals(player.getUniqueId())) {
			if (group.getMembers().size() > 0) {
				boolean foundnewowner = false;

				for (int i = 0; i < group.getMembers().size(); i++) {
					UUID newowner = group.getMembers().get(i);
					Player newownerplayer = Bukkit.getPlayer(newowner);
					if (newownerplayer != null) {
						group.setOwner(newowner);
						sendGroupMessage(newownerplayer, "is now the group leader!");
						foundnewowner = true;
						break;
					}
				}

				if (foundnewowner == false) {
					player.sendMessage("The group has disbanded");
					removeGroup(group.getId());
					return;
				}

			} else {
				player.sendMessage("The group has disbanded");
				removeGroup(group.getId());
				return;
			}
		}

		groups.put(group.getId(), group);
	}
	
	private void removeGroup(UUID groupid) {
		System.out.println("group: " + groupid + " removed");
		
		/*
		for(UUID uuid : groups.get(groupid).getMembers())
		{
			RemoveScoreboard(uuid);
		}*/

		
		groups.remove(groupid);
	}
	
	public void acceptGroupInvite(Player player) {
		UUID targetgroupid = getPlayerInviteGroupID(player);
		if (targetgroupid == null) {
			player.sendMessage("You have not been invited to join a group");
			return;
		}

		removePlayerGroupInvite(player);
		System.out.println("group: " + targetgroupid + " got a membership accept: " + player.getDisplayName());
		addPlayerToGroup(targetgroupid, player);
	}
	
	
	public void acceptFellowshipInvite(ISoliniaPlayer player) {
		Integer targetFellowShipId = getCharacterInviteFellowshipID(player);
		if (targetFellowShipId == null || targetFellowShipId < 1) {
			player.getBukkitPlayer().sendMessage("You have not been invited to join a fellowship");
			return;
		}
		
		removeFellowshipInvite(player);
		
		try
		{
			Fellowship ship = StateManager.getInstance().getConfigurationManager().getFellowship(targetFellowShipId);
			
			if (ship.isPlayerAlreadyInFellowship(player.getBukkitPlayer()))
			{
				player.getBukkitPlayer().sendMessage("You cannot join the fellowship as you already have another character in it");
				System.out.println("fellowship: " + targetFellowShipId + " already had a character of same player in it : " + player.getBukkitPlayer());
				return;
			}
			
			System.out.println("fellowship: " + targetFellowShipId + " got a membership accept: " + player.getFullName());
			ship.addPlayer(player);
		} catch (CoreStateInitException e)
		{
			
		}
		
	}
	
	private void addPlayerToGroup(UUID targetgroupid, Player player) {
		// TODO Auto-generated method stub
		ISoliniaGroup group = getGroup(targetgroupid);

		ISoliniaGroup existingroup = getGroupByMember(player.getUniqueId());
		if (existingroup != null) {
			removePlayerGroupInvite(player);
			player.sendMessage("You are already in a group");
			return;
		}

		if (group == null) {
			removePlayerGroupInvite(player);
			player.sendMessage("That group has disbanded");
			return;
		}

		if (group.getMembers().size() > 5) {
			removePlayerGroupInvite(player);
			player.sendMessage("That group is now full");
			return;
		}

		group.getMembers().add(player.getUniqueId());
		System.out.println("group: " + group.getId() + " gained a member: " + player.getDisplayName());
		
		PartyWindowUtils.UpdateGroupWindowForEveryone(player.getUniqueId(),group, false);
		
		sendGroupMessage(player, "has joined the group!");
		groups.put(group.getId(), group);
	}
	
	public void sendGroupMessage(Player player, String message) {
		if (message == null || message.equals("")) {
			player.sendMessage("You cannot send an empty message");
			return;
		}
		
		try
		{
			ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(player);
			if (message.contains("%t") &&solplayer.getEntityTarget() != null)
				message = message.replace("%t", ""+solplayer.getEntityTarget().getCustomName());

	
			ISoliniaGroup group = getGroupByMember(player.getUniqueId());
			if (group == null) {
				player.sendMessage("You are not in a group");
				return;
			}
			
			String type = "";
			if (group.getOwner().equals(player.getUniqueId()))
			{
				type = "[" + ChatColor.LIGHT_PURPLE + "L" + ChatColor.RESET + "]";
			} else {
				type = "[" + ChatColor.LIGHT_PURPLE + "M" + ChatColor.RESET + "]";
			}
	
			if (group.getMembers().size() > 0) {
				for (UUID memberid : group.getMembers()) {
					Player groupplayer = Bukkit.getPlayer(memberid);
					if (groupplayer != null) {
						groupplayer.sendMessage(type + " " + ChatColor.WHITE + solplayer.getFullName() + ChatColor.RESET + " ["+ChatColor.RED + (int)player.getHealth()+ ChatColor.RESET + "/" +ChatColor.BLUE + solplayer.getMana() + ChatColor.RESET+ "]: " + ChatColor.AQUA + message);
					}
				}
			} else {
				player.sendMessage("There were no members of that group");
				return;
			}
		} catch (CoreStateInitException e)
		{
			e.printStackTrace();
		}
	}
	
	public void declineFellowshipInvite(ISoliniaPlayer solplayer) {
		Integer targetfellowshipid = getCharacterInviteFellowshipID(solplayer);
		if (targetfellowshipid == null || targetfellowshipid < 1) {
			solplayer.getBukkitPlayer().sendMessage("You have not been invited to join a fellowship");
			return;
		}

		try
		{
			Fellowship fellowship = StateManager.getInstance().getConfigurationManager().getFellowship(targetfellowshipid);
			if (fellowship == null) {
				this.removeFellowshipInvite(solplayer);
				solplayer.getBukkitPlayer().sendMessage("That fellowship has disbanded");
				return;
			}
	
			try
			{
				Player owner = fellowship.getMemberPlayerIfOnline(fellowship.getOwnerCharacterId());
				if (owner != null) {
					System.out.println("fellowship: " + fellowship.getId() + " got a membership decline: " + solplayer.getFullName());
					owner.sendMessage(solplayer.getFullName() + " declined your fellowship invite");
				}
			} catch (FellowshipMemberNotFoundException e)
			{
				System.out.println("Tried to send decline message to a member of a fellowship that didnt exist - Fellowship ID: " + fellowship.getId());
			}
			
			
			removeFellowshipInvite(solplayer);
			solplayer.getBukkitPlayer().sendMessage("You declined your fellowship invite");
			return;
		} catch (CoreStateInitException e)
		{
			
		}
	}

	public void declineGroupInvite(Player player) {
		UUID targetgroupid = getPlayerInviteGroupID(player);
		if (targetgroupid == null) {
			player.sendMessage("You have not been invited to join a group");
			return;
		}

		ISoliniaGroup group = getGroup(targetgroupid);
		if (group == null) {
			removePlayerGroupInvite(player);
			player.sendMessage("That group has disbanded");
			return;
		}

		Player owner = Bukkit.getPlayer(group.getOwner());
		if (owner != null) {
			System.out.println("group: " + group.getId() + " got a membership decline: " + player.getDisplayName());
			owner.sendMessage(player.getDisplayName() + " declined your group invite");
		}

		removePlayerGroupInvite(player);
	}
	
	public void invitePlayerToFellowship(ISoliniaPlayer leader, ISoliniaPlayer member) {
		if (getCharacterInviteFellowshipID(member) != null) {
			leader.getBukkitPlayer().sendMessage("You cannot invite that player, they are already pending a fellowship invite");
			return;
		}

		Fellowship inviteefellowship = member.getFellowship();
		Fellowship inviterfellowship = leader.getFellowship();
		
		if (inviteefellowship != null) {
			leader.getBukkitPlayer().sendMessage("You cannot invite that player, they are already in a fellowship");
			return;
		}

		if (inviterfellowship != null) {
			if (inviterfellowship.isPlayerAlreadyInFellowship(member.getBukkitPlayer()))
			{
				leader.getBukkitPlayer().sendMessage("You cannot join the fellowship as you already have another character in it");
				System.out.println("fellowship: " + inviterfellowship.getId() + " already had a character of same player in it : " + member.getFullName());
				return;
			}
		}

		if (inviterfellowship == null) {
			// No fellowship exists, create it
			inviterfellowship = createNewFellowship(leader);
			leader.getBukkitPlayer().sendMessage("You have created a new fellowship!");
		}

		if (inviterfellowship == null) {
			leader.getBukkitPlayer().sendMessage("Your fellowship does not exist");
			return;
		}

		if (inviterfellowship.getOwnerCharacterId() != leader.getId()) {
			leader.getBukkitPlayer().sendMessage("You cannot invite that player, you are not the fellowship leader");
			return;
		}

		if (inviterfellowship.getMemberCharacterIds().size() > 5) {
			leader.getBukkitPlayer().sendMessage("You cannot invite that player, your fellowship is already full");
			return;
		}

		fellowshipinvites.put(member.getId(), inviterfellowship.getId());
		leader.getBukkitPlayer().sendMessage("Invited " + member.getFullName() + " to join your fellowship");
		member.getBukkitPlayer().sendMessage(
				"You have been invited to join " + leader.getFullName() + "'s fellowship - /fellowship accept | /fellowship decline");
	}
	
	public void invitePlayerToGroup(Player leader, Player member) {
		if (getPlayerInviteGroupID(member) != null) {
			leader.sendMessage("You cannot invite that player, they are already pending a group invite");
			return;
		}

		ISoliniaGroup inviteegroup = getGroupByMember(member.getUniqueId());
		ISoliniaGroup invitergroup = getGroupByMember(leader.getUniqueId());

		if (inviteegroup != null) {
			leader.sendMessage("You cannot invite that player, they are already in a group");
			return;
		}

		if (invitergroup == null) {
			// No group exists, create it
			invitergroup = createNewGroup(leader);
			leader.sendMessage("You have joined a new group");
		}

		if (invitergroup == null) {
			leader.sendMessage("Your group does not exist");
			return;
		}

		if (!invitergroup.getOwner().equals(leader.getUniqueId())) {
			leader.sendMessage("You cannot invite that player, you are not the group leader");
			return;
		}

		if (invitergroup.getMembers().size() > 5) {
			leader.sendMessage("You cannot invite that player, your group is already full");
			return;
		}

		groupinvites.put(member.getUniqueId(), invitergroup.getId());
		leader.sendMessage("Invited " + member.getDisplayName() + " to join your group");
		member.sendMessage(
				"You have been invited to join " + leader.getName() + "'s group - /group accept | /group decline");
	}

	public String getInstanceGuid() {
		return this.instanceGuid;
	}

	public double getXPDayModifier() {
		double xppercent = 100;
		xppercent += this.xpdaybonus ;
		return xppercent;
		
	}

	public void setRequiredModVersion(String expectedClientModVersion) {
		this.requiredModVersion = expectedClientModVersion;
	}

	public void setDynmap(DynmapAPI api) {
		this.dynmap = api;
	}

	public DynmapAPI getDynmap() {
		return this.dynmap;
	}

	public void setSoliniaZonesMarkerSet(MarkerSet set) {
		this.soliniaZonesMarkerSet = set;
	}
	
	public MarkerSet getSoliniaZonesMarkerSet()
	{
		return this.soliniaZonesMarkerSet;
	}
	
	public void setRegionExtentsMarkerSet(MarkerSet set) {
		this.regionExtentsMarkerSet = set;
	}
	
	public MarkerSet getRegionExtentsMarkerSet()
	{
		return this.regionExtentsMarkerSet;
	}


	public void setTowny(Towny townyApi) {
		this.towny = townyApi;
	}
	
	public Towny getTowny()
	{
		return this.towny;
	}

	public void toggleShowSpawns() {
		this.showSpawns = !this.showSpawns;
	}
	
	public void setStopping() {
		this.isStopping = true;
	}

	public boolean isStopping() {
		return this.isStopping;
	}

}
