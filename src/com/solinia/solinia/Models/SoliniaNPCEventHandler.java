package com.solinia.solinia.Models;

import java.text.DecimalFormat;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidNPCEventSettingException;
import com.solinia.solinia.Exceptions.SoliniaItemException;
import com.solinia.solinia.Factories.SoliniaItemFactory;
import com.solinia.solinia.Interfaces.ISoliniaClass;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Interfaces.ISoliniaNPCEventHandler;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Interfaces.ISoliniaQuest;
import com.solinia.solinia.Interfaces.ISoliniaRace;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Utils.Utils;

import net.md_5.bungee.api.ChatColor;

public class SoliniaNPCEventHandler implements ISoliniaNPCEventHandler {
	private InteractionType interactiontype;
	private String triggerdata;
	private String chatresponse;
	private int requiresQuest = 0;
	private int awardsQuest = 0;
	private String requiresQuestFlag = null;
	private String awardsQuestFlag = null;
	private int requiresRaceId = 0;
	private int requiresClassId = 0;
	private int npcId = 0;
	private int summonsNpcId = 0;
	private int awardsItem = 0;
	private String teleportResponse = "";
	private boolean awardsRandomisedGear = false;
	private String randomisedGearSuffix = "";
	private boolean awardsTitle = false;
	private String title = "";
	private String responseType = "SAY";
	private double awardsExperience = 0;
	private boolean awardsBind = false;
	private boolean operatorCreated = true;
	private String requiresPermissionNode = "";

	@Override
	public InteractionType getInteractiontype() {
		return interactiontype;
	}

	@Override
	public void setInteractiontype(InteractionType interactiontype) {
		this.interactiontype = interactiontype;
	}

	@Override
	public String getTriggerdata() {
		return triggerdata;
	}

	@Override
	public void setTriggerdata(String triggerdata) {
		this.triggerdata = triggerdata;
	}

	@Override
	public String getChatresponse() {
		return chatresponse;
	}

	@Override
	public void setChatresponse(String chatresponse) {
		this.chatresponse = chatresponse;
	}

	@Override
	public int getRequiresQuest() {
		return requiresQuest;
	}

	@Override
	public void setRequiresQuest(int requiresQuest) {
		this.requiresQuest = requiresQuest;
	}

	@Override
	public int getAwardsQuest() {
		return awardsQuest;
	}

	@Override
	public void setAwardsQuest(int awardsQuest) {
		this.awardsQuest = awardsQuest;
	}

	@Override
	public String getRequiresQuestFlag() {
		return requiresQuestFlag;
	}

	@Override
	public void setRequiresQuestFlag(String requiresQuestFlag) {
		this.requiresQuestFlag = requiresQuestFlag;
	}

	@Override
	public String getAwardsQuestFlag() {
		return awardsQuestFlag;
	}

	@Override
	public void setAwardsQuestFlag(String awardsQuestFlag) {
		this.awardsQuestFlag = awardsQuestFlag;
	}

	@Override
	public void sendNPCEvent(CommandSender sender) {
		sender.sendMessage(ChatColor.RED + "NPC Trigger Event [" + getTriggerdata() + "]" + ChatColor.RESET);
		sender.sendMessage("----------------------------");
		sender.sendMessage("- triggerdata: " + ChatColor.GOLD + getTriggerdata() + ChatColor.RESET);
		sender.sendMessage("- chatresponse: " + ChatColor.GOLD + getChatresponse() + ChatColor.RESET);
		sender.sendMessage("- responsetype: " + ChatColor.GOLD + getResponseType() + ChatColor.RESET);
		sender.sendMessage("- teleportresponse: " + ChatColor.GOLD + getTeleportResponse() + ChatColor.RESET);
		sender.sendMessage("- awardsbind: " + ChatColor.GOLD + isAwardsBind() + ChatColor.RESET);
		sender.sendMessage("- interactiontype: " + ChatColor.GOLD + getInteractiontype() + ChatColor.RESET);
		sender.sendMessage("- requiresquest: " + ChatColor.GOLD + getRequiresQuest() + ChatColor.RESET);
		sender.sendMessage("- awardsquest: " + ChatColor.GOLD + getAwardsQuest() + ChatColor.RESET);
		sender.sendMessage("- requiresquestflag: " + ChatColor.GOLD + getRequiresQuestFlag() + ChatColor.RESET);
		sender.sendMessage("- awardsquestflag: " + ChatColor.GOLD + getAwardsQuestFlag() + ChatColor.RESET);
		sender.sendMessage("- requiresraceid: " + ChatColor.GOLD + getRequiresRaceId() + ChatColor.RESET);
		sender.sendMessage("- requiresclassid: " + ChatColor.GOLD + getRequiresClassId() + ChatColor.RESET);
		sender.sendMessage("- awardsitem: " + ChatColor.GOLD + getAwardsItem() + ChatColor.RESET);
		sender.sendMessage("- awardsrandomisedgear: " + ChatColor.GOLD + isAwardsRandomisedGear() + ChatColor.RESET);
		sender.sendMessage("- randomisedgearsuffix: " + ChatColor.GOLD + this.getRandomisedGearSuffix() + ChatColor.RESET);
		sender.sendMessage("- title: " + ChatColor.GOLD + this.getTitle() + ChatColor.RESET);
		sender.sendMessage("- awardstitle: " + ChatColor.GOLD + this.isAwardsTitle() + ChatColor.RESET);
		sender.sendMessage("- summonsnpcid: " + ChatColor.GOLD + this.getSummonsNpcId() + ChatColor.RESET);
		sender.sendMessage("- requirespermissionnode: " + ChatColor.GOLD + getRequiresPermissionNode() + ChatColor.RESET);

		DecimalFormat df = new DecimalFormat("#");
        df.setMaximumFractionDigits(8);
		sender.sendMessage("- awardsxp: " + ChatColor.GOLD + df.format(getAwardsExperience()) + ChatColor.RESET);
	}

	@Override
	public void editTriggerEventSetting(String setting, String value) throws InvalidNPCEventSettingException {
		switch (setting.toLowerCase()) {
		case "randomisedgearsuffix":
			setRandomisedGearSuffix(value);
			break;
		case "responsetype":
			if (!value.toUpperCase().equals("SAY") && !value.toUpperCase().equals("EMOTE"))
			{
				throw new InvalidNPCEventSettingException("responsetype can either be EMOTE or SAY");
			}
			setResponseType(value.toUpperCase());
			break;
		case "title":
			setTitle(value);
			break;
		case "awardstitle":
			setAwardsTitle(Boolean.parseBoolean(value));
			break;
		case "requirespermissionnode":
			setRequiresPermissionNode(value);
			break;
		case "awardsbind":
			setAwardsBind(Boolean.parseBoolean(value));
			break;
		case "triggerdata":
			if (value.equals(""))
				throw new InvalidNPCEventSettingException("Triggerdata is empty");
			if (value.contains(" "))
				throw new InvalidNPCEventSettingException("Triggerdata can only be one value");
			setTriggerdata(value.toUpperCase());
			break;
		case "chatresponse":
			if (value.equals(""))
				throw new InvalidNPCEventSettingException("Chatresponse is empty");
			setChatresponse(value);
			break;
		case "requiresclassid":
			int classid = Integer.parseInt(value);
			if (classid < 1)
			{
				setRequiresClassId(0);
				break;
			}
			try
			{
				ISoliniaClass classObj = StateManager.getInstance().getConfigurationManager().getClassObj(classid);
				if (classObj == null)
					throw new InvalidNPCEventSettingException("Invalid class id");
			} catch (CoreStateInitException e)
			{
				throw new InvalidNPCEventSettingException("State not initialised");
			}
			setRequiresClassId(classid);
			break;
		case "requiresraceid":
			int raceid = Integer.parseInt(value);
			if (raceid < 1)
			{
				setRequiresRaceId(0);
				break;
			}
			try
			{
				ISoliniaRace race = StateManager.getInstance().getConfigurationManager().getRace(raceid);
				if (race == null)
					throw new InvalidNPCEventSettingException("Invalid race id");
			} catch (CoreStateInitException e)
			{
				throw new InvalidNPCEventSettingException("State not initialised");
			}
			setRequiresRaceId(raceid);
			break;
		case "requiresquest":
			int questid = Integer.parseInt(value);
			if (questid < 1)
				throw new InvalidNPCEventSettingException("Invalid quest id");
			try
			{
			ISoliniaQuest quest = StateManager.getInstance().getConfigurationManager().getQuest(questid);
			if (quest == null)
				throw new InvalidNPCEventSettingException("Invalid quest id");
			} catch (CoreStateInitException e)
			{
				throw new InvalidNPCEventSettingException("State not initialised");
			}
			setRequiresQuest(questid);
			break;
		case "awardsquest":
			int aquestid = Integer.parseInt(value);
			if (aquestid < 1)
				throw new InvalidNPCEventSettingException("Invalid quest id");
			try
			{
			ISoliniaQuest quest = StateManager.getInstance().getConfigurationManager().getQuest(aquestid);
			if (quest == null)
				throw new InvalidNPCEventSettingException("Invalid quest id");
			} catch (CoreStateInitException e)
			{
				throw new InvalidNPCEventSettingException("State not initialised");
			}
			setAwardsQuest(aquestid);
			break;
		case "requiresquestflag":
			setRequiresQuestFlag(value);
			break;
		case "awardsquestflag":
			setAwardsQuestFlag(value);
			break;
		case "teleportresponse":
			try
			{
				String[] zonedata = value.split(",");
				// Dissasemble the value to ensure it is correct
				String world = zonedata[0];
				double x = Double.parseDouble(zonedata[1]);
				double y = Double.parseDouble(zonedata[2]);
				double z = Double.parseDouble(zonedata[3]);
				
				setTeleportResponse(world+","+x+","+y+","+z);
				break;
			} catch (Exception e)
			{
				throw new InvalidNPCEventSettingException("Teleport zone value must be in format: world,x,y,z");
			}
		case "awardsrandomisedgear":
			setAwardsRandomisedGear(Boolean.parseBoolean(value));
			break;
		case "summonsnpcid":
			int npcId = Integer.parseInt(value);
			try
			{
				ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(npcId);
				if (npc == null)
					throw new InvalidNPCEventSettingException("Invalid npc ID");
				setSummonsNpcId(npcId);
			} catch (CoreStateInitException e)
			{
				
			}
			break;
		case "awardsitem":
			int itemId = Integer.parseInt(value);
			if (itemId < 1)
				throw new InvalidNPCEventSettingException("Invalid item ID");
			
			if (getAwardsQuestFlag() == null || getAwardsQuestFlag().equals(""))
				throw new InvalidNPCEventSettingException("You cannot set an awardsitem to a npc event handler unless the npc awards a quest flag -  this is to prevent duplicated awards");
			
			try
			{
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(itemId);
				if (item == null)
					throw new InvalidNPCEventSettingException("Invalid item id");
				} catch (CoreStateInitException e)
				{
					throw new InvalidNPCEventSettingException("State not initialised");
				}
				setAwardsItem(itemId);
			break;
		case "awardsxp":
			double xp = Double.parseDouble(value);
			double maxXp = (Utils.getExperienceRequirementForLevel(Utils.getMaxLevel()) / Utils.getMaxLevel());
			if (xp < 0 || xp > maxXp)
			{
				DecimalFormat df = new DecimalFormat("#");
		        df.setMaximumFractionDigits(8);
				
				throw new InvalidNPCEventSettingException("XP must be greater than -1 and less than " + df.format(maxXp));
			}
			
			if (getAwardsQuestFlag() == null || getAwardsQuestFlag().equals(""))
				throw new InvalidNPCEventSettingException("You cannot set a rewardsxp to a npc event handler unless the npc awards a quest flag -  this is to prevent duplicated awards");
			
			setAwardsExperience(xp);
			break;
		default:
			throw new InvalidNPCEventSettingException(
					"Invalid NPC Event setting. Valid Options are: triggerdata,chatresponse,interactiontype,requiresquest,awardsquest,requiresquestflag,awardsquestflag,awardsitem,awardsxp,requiresraceid,requiresclassid");
		}
	}

	@Override
	public int getNpcId() {
		return npcId;
	}

	@Override
	public void setNpcId(int npcId) {
		this.npcId = npcId;
	}

	public int getAwardsItem() {
		return awardsItem;
	}

	public void setAwardsItem(int awardsItem) {
		this.awardsItem = awardsItem;
	}

	@Override
	public boolean playerMeetsRequirements(Player triggerentity) {
		try
		{
			ISoliniaPlayer player = SoliniaPlayerAdapter.Adapt(triggerentity);
			if (!getRequiresPermissionNode().equals(""))
			{
				if (!triggerentity.hasPermission(getRequiresPermissionNode()))
				{
					triggerentity.sendMessage("This requires a permission node you do not have");
					return false;
				}
			}
			if (getRequiresQuest() > 0)
			{
				boolean foundQuest = false;
				for(PlayerQuest playerQuest : player.getPlayerQuests())
				{
					if (playerQuest.getQuestId() == getRequiresQuest())
					{
						foundQuest = true;
					}
				}
				
				if (foundQuest == false)
					return false;
			}
			
			if (getRequiresQuestFlag() != null && !getRequiresQuestFlag().equals(""))
			{
				boolean foundQuestFlag = false;
				for(String playerQuestFlag : player.getPlayerQuestFlags())
				{
					if (playerQuestFlag.equals(getRequiresQuestFlag()))
					{
						foundQuestFlag = true;
					}
				}
				
				if (foundQuestFlag == false)
					return false;
			}
			
			if (getRequiresRaceId() > 0)
			{
				if (player.getRace() != null)
				{
					if (player.getRace().getId() != getRequiresRaceId())
						return false;
				}
			}
			
			if (getRequiresClassId() > 0)
			{
				if (player.getClassObj() != null)
				{
					if (player.getClassObj().getId() != getRequiresClassId())
						return false;
				}
			}
			
			return true;
		
		} catch (CoreStateInitException e)
		{
			return false;
		}
	}

	@Override
	public void awardPlayer(Player triggerentity) {
		try
		{
			ISoliniaPlayer player = SoliniaPlayerAdapter.Adapt(triggerentity);
			
			if (getAwardsQuest() > 0)
			{
				boolean foundQuest = false;
				for(PlayerQuest playerQuest : player.getPlayerQuests())
				{
					if (playerQuest.getQuestId() == getAwardsQuest())
					{
						foundQuest = true;
					}
				}
				
				if (foundQuest == false)
					player.addPlayerQuest(getAwardsQuest());
			}
			
			if (getAwardsQuestFlag() != null && !getAwardsQuestFlag().equals(""))
			{
				boolean foundQuestFlag = false;
				for(String playerQuestFlag : player.getPlayerQuestFlags())
				{
					if (playerQuestFlag.equals(getAwardsQuestFlag()))
					{
						foundQuestFlag = true;
					}
				}
				
				if (foundQuestFlag == false)
				{
					player.addPlayerQuestFlag(getAwardsQuestFlag());
					
					// All item awards must be accompanied with a quest flag else they will repeat the item return over and over
					if (getAwardsItem() > 0)
					{
						System.out.println("Awarding item with awardquestflag: " + getAwardsQuestFlag());
						ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(getAwardsItem());
						
						final int awarditemid = getAwardsItem();
						final UUID uuid = player.getBukkitPlayer().getUniqueId();
						
						if (item != null)
						{
							
							Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(
									Bukkit.getPluginManager().getPlugin("Solinia3Core"), new Runnable() {
										public void run() {
											try
											{
												ItemStack itemStack = item.asItemStack();
												ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(awarditemid);
												Bukkit.getPlayer(uuid).getWorld().dropItem(Bukkit.getPlayer(uuid).getLocation(), itemStack);
												System.out.println("Awarded item: " + item.getDisplayname());
											} catch (CoreStateInitException e)
											{
												// skip
											}
										}
									});
							
						}
					}
					
					// All xp awards must be accompanied with a quest flag else they will repeat the item return over and over
					if (getAwardsExperience() > 0)
					{
						System.out.println("Awarding experience with awardquestflag: " + getAwardsQuestFlag());
						player.increasePlayerExperience(getAwardsExperience(), false);
					}
					
					if (this.isAwardsTitle() == true)
					{
						if (this.getTitle() != null)
						{
							if (!this.getTitle().equals(""))
							{
								player.grantTitle(this.getTitle());
							}
						}
					}
					
					if (isAwardsRandomisedGear() == true)
					{
						String suffix = "of Randomisation";
						if (getRandomisedGearSuffix() != null)
						{
							if (!getRandomisedGearSuffix().equals(""))
							{
								suffix = getRandomisedGearSuffix();
							}
						}
						
						System.out.println("Awarding randomisedgear with awardquestflag: " + getAwardsQuestFlag());
						
						int playertier = 1;
						if (player.getLevel() >= 1 && player.getLevel() < 11)
							playertier = 1;
						if (player.getLevel() >= 11 && player.getLevel() < 21)
							playertier = 2;
						if (player.getLevel() >= 21 && player.getLevel() < 31)
							playertier = 3;
						if (player.getLevel() >= 31 && player.getLevel() < 41)
							playertier = 4;
						if (player.getLevel() >= 41 && player.getLevel() < 51)
							playertier = 5;
						if (player.getLevel() >= 51 && player.getLevel() < 61)
							playertier = 6;
						
						try {
							
							// always give the next tier up and then we will reset the player requirements ot current level
							// this ability is for special seasonal rewards only
							playertier += 1;
							List<Integer> items = SoliniaItemFactory.CreateClassItemSet(player.getClassObj(), playertier, suffix, false, true);
							
							for(int itemid : items)
							{
								ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(itemid);
								final String playerName = player.getBukkitPlayer().getName();
								final int minLevel = player.getLevel();
								final int finalitemid = itemid;
								if (item != null)
								{
									
									Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(
											Bukkit.getPluginManager().getPlugin("Solinia3Core"), new Runnable() {
												public void run() {
													try
													{
														ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(finalitemid);
														item.setMinLevel(minLevel);
														SoliniaAccountClaim claim = new SoliniaAccountClaim();
														claim.setId(StateManager.getInstance().getConfigurationManager().getNextAccountClaimId());
														claim.setMcname(playerName);
														claim.setItemid(finalitemid);
														claim.setClaimed(false);
														Player claimPlayer = Bukkit.getPlayer(playerName);
														if (claimPlayer != null)
														{
															claimPlayer.sendMessage(ChatColor.GOLD + "You have been awarded with a claim item! See /claim");
														}
														StateManager.getInstance().getConfigurationManager().addAccountClaim(claim);
														System.out.println("Awarded Claim: " + item.getDisplayname() + " to " + playerName);
													} catch (CoreStateInitException e)
													{
														// skip
													}
												}
											});
								}
							}
							
						} catch (SoliniaItemException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
			}
		} catch (CoreStateInitException e)
		{
			System.out.println(e.getMessage());
			return;
		}
	}

	@Override
	public String getTeleportResponse() {
		return teleportResponse;
	}

	@Override
	public void setTeleportResponse(String teleportResponse) {
		this.teleportResponse = teleportResponse;
	}

	@Override
	public boolean isAwardsRandomisedGear() {
		return awardsRandomisedGear;
	}

	@Override
	public void setAwardsRandomisedGear(boolean awardsRandomisedGear) {
		this.awardsRandomisedGear = awardsRandomisedGear;
	}

	@Override
	public String getRandomisedGearSuffix() {
		return randomisedGearSuffix;
	}

	@Override
	public void setRandomisedGearSuffix(String randomisedGearSuffix) {
		this.randomisedGearSuffix = randomisedGearSuffix;
	}

	@Override
	public boolean isAwardsTitle() {
		return awardsTitle;
	}

	@Override
	public void setAwardsTitle(boolean awardsTitle) {
		this.awardsTitle = awardsTitle;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public int getSummonsNpcId() {
		return summonsNpcId;
	}

	@Override
	public void setSummonsNpcId(int summonsNpcId) {
		this.summonsNpcId = summonsNpcId;
	}

	@Override
	public String getResponseType() {
		return responseType;
	}

	@Override
	public void setResponseType(String responseType) {
		this.responseType = responseType;
	}

	@Override
	public double getAwardsExperience() {
		return awardsExperience;
	}

	@Override
	public void setAwardsExperience(double awardsExperience) {
		this.awardsExperience = awardsExperience;
	}

	@Override
	public boolean isAwardsBind() {
		return awardsBind;
	}

	@Override
	public void setAwardsBind(boolean awardsBind) {
		this.awardsBind = awardsBind;
	}

	@Override
	public boolean isOperatorCreated() {
		return operatorCreated;
	}

	@Override
	public void setOperatorCreated(boolean operatorCreated) {
		this.operatorCreated = operatorCreated;
	}

	public int getRequiresRaceId() {
		return requiresRaceId;
	}

	public void setRequiresRaceId(int requiresRaceId) {
		this.requiresRaceId = requiresRaceId;
	}

	public int getRequiresClassId() {
		return requiresClassId;
	}

	public void setRequiresClassId(int requiresClassId) {
		this.requiresClassId = requiresClassId;
	}

	public String getRequiresPermissionNode() {
		return requiresPermissionNode;
	}

	public void setRequiresPermissionNode(String requiresPermissionNode) {
		this.requiresPermissionNode = requiresPermissionNode;
	}

}
