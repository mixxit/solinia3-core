package com.solinia.solinia.Managers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Scoreboard;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.IChannelManager;
import com.solinia.solinia.Interfaces.IConfigurationManager;
import com.solinia.solinia.Interfaces.IEntityManager;
import com.solinia.solinia.Interfaces.IPlayerManager;
import com.solinia.solinia.Interfaces.ISoliniaGroup;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Models.SoliniaGroup;
import com.solinia.solinia.Models.SoliniaSpell;
import com.solinia.solinia.Models.WorldWidePerk;
import com.solinia.solinia.Providers.DiscordAdminChannelCommandSender;
import com.solinia.solinia.Providers.DiscordDefaultChannelCommandSender;
import com.solinia.solinia.Utils.ScoreboardUtils;
import com.solinia.solinia.Utils.Utils;

import de.slikey.effectlib.EffectManager;
import me.dadus33.chatitem.api.ChatItemAPI;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import sx.blah.discord.api.IDiscordClient;

public class CoreState {
	private boolean isInitialised = false;
	private IPlayerManager playerManager;
	private IEntityManager entityManager;
	private IConfigurationManager configurationManager;
	private Economy economy;
	private IChannelManager channelManager;
	private ConcurrentHashMap<UUID, BossBar> bossbars = new ConcurrentHashMap<UUID, BossBar>();
	private ConcurrentHashMap<UUID, ISoliniaGroup> groups = new ConcurrentHashMap<UUID, ISoliniaGroup>();
	private ConcurrentHashMap<UUID, UUID> groupinvites = new ConcurrentHashMap<UUID, UUID>();
	private ConcurrentHashMap<UUID, Scoreboard> scoreboards = new ConcurrentHashMap<UUID, Scoreboard>();
	private String instanceGuid;
	private ChatItemAPI chatitemapi;
	private IDiscordClient discordClient;
	private DiscordAdminChannelCommandSender discordAdminChannelCommandSender;
	private DiscordDefaultChannelCommandSender discordDefaultChannelCommandSender;
	private EffectManager effectManager;
	
	public CoreState()
	{
		isInitialised = false;
		final int SHORT_ID_LENGTH = 8;
		this.instanceGuid = RandomStringUtils.random(SHORT_ID_LENGTH);
		this.discordAdminChannelCommandSender = new DiscordAdminChannelCommandSender();
		this.discordDefaultChannelCommandSender = new DiscordDefaultChannelCommandSender();
	}
	
	public Scoreboard getScoreboard(Player player)
	{
		if (scoreboards.get(player.getUniqueId()) == null)
		{
			scoreboards.put(player.getUniqueId(),Bukkit.getScoreboardManager().getNewScoreboard());
			player.setScoreboard(scoreboards.get(player.getUniqueId()));
		}
		
		return scoreboards.get(player.getUniqueId());
	}
	
	
	public BossBar getBossBar(UUID uuid) {
		return this.bossbars.get(uuid);
	}

	public void setBossBar(UUID uuid, BossBar bossbar) {
		// TODO Auto-generated method stub
		this.bossbars.put(uuid, bossbar);
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
			playerManager.commit();
			configurationManager.commit();
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

	public double getWorldPerkXPModifier() {
		return Utils.getWorldPerkXPModifier();
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

	public int getWorldPerkDropCountModifier() {
		return Utils.GetWorldPerkDropCountModifier();
	}

	public boolean addActiveBlockEffect(Block clickedBlock, SoliniaSpell soliniaSpell, LivingEntity sourceEntity) {
		// TODO Auto-generated method stub
		return false;
	}

	public void spellTick(Plugin plugin) {
		entityManager.spellTick(plugin);
	}

	public ChatItemAPI getChatItem() {
		return chatitemapi;
	}

	public void setChatItem(ChatItemAPI chatitemapi) {
		this.chatitemapi = chatitemapi;
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

	public UUID removePlayerGroupInvite(Player player) {
		return groupinvites.remove(player.getUniqueId());
	}
	
	public void removePlayerFromGroup(Player player) {
		ISoliniaGroup group = getGroupByMember(player.getUniqueId());

		if (group == null) {
			removePlayerGroupInvite(player);
			player.sendMessage("That group doesn't exist");
			return;
		}

		System.out.println("group: " + group.getId() + " lost a member: " + player.getDisplayName());
		sendGroupMessage(player, "has left the group!");
		group.getMembers().remove(player.getUniqueId());

		ScoreboardUtils.RemoveScoreboard(player.getUniqueId());
		for(UUID uuid : group.getMembers())
		{
			ScoreboardUtils.UpdateGroupScoreboard(uuid,group);
		}

		if (group.getOwner().equals(player.getUniqueId())) {
			if (group.getMembers().size() > 0) {
				boolean foundnewowner = false;

				for (int i = 0; i < group.getMembers().size(); i++) {
					UUID newowner = group.getMembers().get(i);
					Player newownerplayer = Bukkit.getPlayer(newowner);
					if (newownerplayer != null) {
						group.setOwner(newowner);
						System.out.println(
								"group: " + group.getId() + " has a new leader: " + newownerplayer.getDisplayName());
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
		
		for(UUID uuid : group.getMembers())
		{
			ScoreboardUtils.UpdateGroupScoreboard(uuid,group);
		}
		
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
						System.out.println(
								"group: " + group.getId() + ":" + player.getDisplayName() + ": " + ChatColor.AQUA + message);
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

	public void reloadPerks() {
		try {
			StateManager.getInstance().getConfigurationManager().reloadPerks();
			Utils.broadcastPerks();
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public List<WorldWidePerk> getWorldWidePerks()
	{
		List<WorldWidePerk> perks = new ArrayList<WorldWidePerk>();
		try {
			return StateManager.getInstance().getConfigurationManager().getWorldWidePerks();
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return perks;
	}

	public String getInstanceGuid() {
		return this.instanceGuid;
	}

	public void setDiscordClient(IDiscordClient discordClient) {
		this.discordClient = discordClient;
	}

	public IDiscordClient getDiscordClient() {
		return this.discordClient;
	}

	public DiscordDefaultChannelCommandSender getDiscordDefaultChannelCommandSender() {
		// TODO Auto-generated method stub
		return this.discordDefaultChannelCommandSender;
	}

	
	public DiscordAdminChannelCommandSender getDiscordAdminChannelCommandSender() {
		// TODO Auto-generated method stub
		return this.discordAdminChannelCommandSender;
	}
}
