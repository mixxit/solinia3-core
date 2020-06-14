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
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
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
import com.solinia.solinia.Interfaces.ISoliniaGroup;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Models.Fellowship;
import com.solinia.solinia.Models.HINT;
import com.solinia.solinia.Models.SoliniaGroup;
import com.solinia.solinia.Models.SoliniaSpell;
import com.solinia.solinia.Models.SoliniaZone;
import com.solinia.solinia.Utils.ChatUtils;
import com.solinia.solinia.Utils.MathUtils;
import com.solinia.solinia.Utils.PartyWindowUtils;
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
		// BTW Clear no longer applies
		if (this.currentHotZones.contains(hotzone))
		{
			// already a hotzone
			return;
		}
		
		try {
			for(SoliniaZone zone : StateManager.getInstance().getConfigurationManager().getZones())
			{
				if (!zone.isHotzone())
					continue;
				
				if (zone.getId() == hotzone)
				{
					if (this.currentHotZones.size() == 0 || this.currentHotZones.size() == 1)
					{
						this.currentHotZones.add(hotzone);
						System.out.println("Hotzone set to: " + zone.getName());
						return;
					}
					
					if (this.currentHotZones.size() == 2)
					{
						// shuffle up
						this.currentHotZones.set(0, this.currentHotZones.get(1));
						this.currentHotZones.set(1, zone.getId());
						System.out.println("Hotzone set to: " + zone.getName() + "[Shuffle up]");
						return;
					}
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
		
		setXPBonus();

	}
	
	public void setXPBonus() {
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
		StateManager.getInstance().houseKeeping();
		StateManager.getInstance().patchVersion();
	}
	
	private void houseKeeping() {
		// TODO Auto-generated method stub
		try {
			StateManager.getInstance().getConfigurationManager().removeClaimedClaims();
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void patchVersion() {
		
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
			if (entry.getValue().getUnmodifiableGroupMembersForBuffs(false,false).contains(uniqueId))
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
		group.getMembersWithoutPets().add(leader.getUniqueId());
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
		for(UUID uuid : group.getMembersWithoutPets())
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
		group.getMembersWithoutPets().remove(player.getUniqueId());
		
		PartyWindowUtils.UpdateGroupWindow(player.getUniqueId(), null, false, true);
		sendGroupPacketToAllPlayers(group);
		
		try
		{
			if (playerCharacterId > 0)
			for (int i = 0; i < group.getMembersWithoutPets().size(); i++) {
				UUID member = group.getMembersWithoutPets().get(i);
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
			if (group.getMembersWithoutPets().size() > 0) {
				boolean foundnewowner = false;

				for (int i = 0; i < group.getMembersWithoutPets().size(); i++) {
					UUID newowner = group.getMembersWithoutPets().get(i);
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

		// players only
		if (group.getMembersWithoutPets().size() > 5) {
			removePlayerGroupInvite(player);
			player.sendMessage("That group is now full");
			return;
		}

		// players only
		group.getMembersWithoutPets().add(player.getUniqueId());
		System.out.println("group: " + group.getId() + " gained a member: " + player.getDisplayName());
		
		PartyWindowUtils.UpdateGroupWindowForEveryone(player.getUniqueId(),group, false);
		
		sendGroupMessage(player, "has joined the group!");
		groups.put(group.getId(), group);
	}
	
	public void sendGroupMessage(Player player, String message) {
		if (message == null || message.equals("")) {
			player.sendMessage("<GC>"+"You cannot send an empty message");
			return;
		}
		
		try
		{
			ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(player);
			if (message.contains("%t") &&solplayer.getEntityTarget() != null)
				message = message.replace("%t", ""+solplayer.getEntityTarget().getCustomName());

	
			ISoliniaGroup group = getGroupByMember(player.getUniqueId());
			if (group == null) {
				player.sendMessage("<GC>"+"You are not in a group");
				return;
			}
			
			String type = "";
			if (group.getOwner().equals(player.getUniqueId()))
			{
				type = "[" + ChatColor.LIGHT_PURPLE + "L" + ChatColor.RESET + "]";
			} else {
				type = "[" + ChatColor.LIGHT_PURPLE + "M" + ChatColor.RESET + "]";
			}
	
			// players only
			if (group.getMembersWithoutPets().size() > 0) {
				for (UUID memberid : group.getMembersWithoutPets()) {
					Player groupplayer = Bukkit.getPlayer(memberid);
					if (groupplayer != null) {
						groupplayer.sendMessage("<GC>"+ type + " " + ChatColor.WHITE + solplayer.getFullName() + ChatColor.RESET + " ["+ChatColor.RED + (int)player.getHealth()+ ChatColor.RESET + "/" +ChatColor.BLUE + solplayer.getMana() + ChatColor.RESET+ "]: " + ChatColor.AQUA + message);
					}
				}
			} else {
				player.sendMessage("<GC>"+"There were no members of that group");
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
			solplayer.getBukkitPlayer().sendMessage("<FC>"+"You have not been invited to join a fellowship");
			return;
		}

		try
		{
			Fellowship fellowship = StateManager.getInstance().getConfigurationManager().getFellowship(targetfellowshipid);
			if (fellowship == null) {
				this.removeFellowshipInvite(solplayer);
				solplayer.getBukkitPlayer().sendMessage("<FC>"+"That fellowship has disbanded");
				return;
			}
	
			try
			{
				Player owner = fellowship.getMemberPlayerIfOnline(fellowship.getOwnerCharacterId());
				if (owner != null) {
					System.out.println("<FC>"+"fellowship: " + fellowship.getId() + " got a membership decline: " + solplayer.getFullName());
					owner.sendMessage("<FC>"+solplayer.getFullName() + " declined your fellowship invite");
				}
			} catch (FellowshipMemberNotFoundException e)
			{
				System.out.println("<FC>"+"Tried to send decline message to a member of a fellowship that didnt exist - Fellowship ID: " + fellowship.getId());
			}
			
			
			removeFellowshipInvite(solplayer);
			solplayer.getBukkitPlayer().sendMessage("<FC>"+"You declined your fellowship invite");
			return;
		} catch (CoreStateInitException e)
		{
			
		}
	}

	public void declineGroupInvite(Player player) {
		UUID targetgroupid = getPlayerInviteGroupID(player);
		if (targetgroupid == null) {
			player.sendMessage("<GC>"+"You have not been invited to join a group");
			return;
		}

		ISoliniaGroup group = getGroup(targetgroupid);
		if (group == null) {
			removePlayerGroupInvite(player);
			player.sendMessage("<GC>"+"That group has disbanded");
			return;
		}

		Player owner = Bukkit.getPlayer(group.getOwner());
		if (owner != null) {
			System.out.println("<GC>"+"group: " + group.getId() + " got a membership decline: " + player.getDisplayName());
			owner.sendMessage(player.getDisplayName() + " declined your group invite");
		}

		removePlayerGroupInvite(player);
	}
	
	public void invitePlayerToFellowship(ISoliniaPlayer leader, ISoliniaPlayer member) {
		if (getCharacterInviteFellowshipID(member) != null) {
			leader.getBukkitPlayer().sendMessage("<FC>"+"You cannot invite that player, they are already pending a fellowship invite");
			return;
		}

		Fellowship inviteefellowship = member.getFellowship();
		Fellowship inviterfellowship = leader.getFellowship();
		
		if (inviteefellowship != null) {
			leader.getBukkitPlayer().sendMessage("<FC>"+"You cannot invite that player, they are already in a fellowship");
			return;
		}

		if (inviterfellowship != null) {
			if (inviterfellowship.isPlayerAlreadyInFellowship(member.getBukkitPlayer()))
			{
				leader.getBukkitPlayer().sendMessage("<FC>"+"You cannot join the fellowship as you already have another character in it");
				System.out.println("fellowship: " + inviterfellowship.getId() + " already had a character of same player in it : " + member.getFullName());
				return;
			}
		}

		if (inviterfellowship == null) {
			// No fellowship exists, create it
			inviterfellowship = createNewFellowship(leader);
			leader.getBukkitPlayer().sendMessage("<FC>"+"You have created a new fellowship!");
		}

		if (inviterfellowship == null) {
			leader.getBukkitPlayer().sendMessage("<FC>"+"Your fellowship does not exist");
			return;
		}

		if (inviterfellowship.getOwnerCharacterId() != leader.getId()) {
			leader.getBukkitPlayer().sendMessage("<FC>"+"You cannot invite that player, you are not the fellowship leader");
			return;
		}

		if (inviterfellowship.getMemberCharacterIds().size() > 5) {
			leader.getBukkitPlayer().sendMessage("<FC>"+"You cannot invite that player, your fellowship is already full");
			return;
		}

		fellowshipinvites.put(member.getId(), inviterfellowship.getId());
		leader.getBukkitPlayer().sendMessage("<FC>"+"Invited " + member.getFullName() + " to join your fellowship");
		member.getBukkitPlayer().sendMessage(
				"<FC>"+"You have been invited to join " + leader.getFullName() + "'s fellowship - /fellowship accept | /fellowship decline");
	}
	
	public void invitePlayerToGroup(Player leader, Player member) {
		if (getPlayerInviteGroupID(member) != null) {
			leader.sendMessage("<GC>"+"You cannot invite that player, they are already pending a group invite");
			return;
		}

		ISoliniaGroup inviteegroup = getGroupByMember(member.getUniqueId());
		ISoliniaGroup invitergroup = getGroupByMember(leader.getUniqueId());

		if (inviteegroup != null) {
			leader.sendMessage("<GC>"+"You cannot invite that player, they are already in a group");
			return;
		}

		if (invitergroup == null) {
			// No group exists, create it
			invitergroup = createNewGroup(leader);
			leader.sendMessage("<GC>"+"You have joined a new group");
		}

		if (invitergroup == null) {
			leader.sendMessage("<GC>"+"Your group does not exist");
			return;
		}

		if (!invitergroup.getOwner().equals(leader.getUniqueId())) {
			leader.sendMessage("<GC>"+"You cannot invite that player, you are not the group leader");
			return;
		}

		// players only
		if (invitergroup.getMembersWithoutPets().size() > 5) {
			leader.sendMessage("<GC>"+"You cannot invite that player, your group is already full");
			return;
		}

		groupinvites.put(member.getUniqueId(), invitergroup.getId());
		leader.sendMessage("<GC>"+"Invited " + member.getDisplayName() + " to join your group");
		member.sendMessage("<GC>"+
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
