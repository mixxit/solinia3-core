package com.solinia.solinia.Models;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidNPCEventSettingException;
import com.solinia.solinia.Interfaces.ISoliniaClass;
import com.solinia.solinia.Interfaces.ISoliniaFaction;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaLootDrop;
import com.solinia.solinia.Interfaces.ISoliniaLootDropEntry;
import com.solinia.solinia.Interfaces.ISoliniaLootTable;
import com.solinia.solinia.Interfaces.ISoliniaLootTableEntry;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Interfaces.ISoliniaNPCEventHandler;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Interfaces.ISoliniaQuest;
import com.solinia.solinia.Interfaces.ISoliniaRace;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Utils.EntityUtils;
import com.solinia.solinia.Utils.MathUtils;
import com.solinia.solinia.Utils.PlayerUtils;
import net.md_5.bungee.api.ChatColor;

public class SoliniaNPCEventHandler implements ISoliniaNPCEventHandler {
	private InteractionType interactiontype;
	private String triggerdata;
	private String chatresponse;
	private int requiresQuest = 0;
	private int awardsQuest = 0;
	private String requiresQuestFlag = null;
	private boolean requiresMod = true;
	private String awardsQuestFlag = null;
	private boolean awardsClassSpell = false;
	private int awardClassLootdropId = 0;
	private int requiresRaceId = 0;
	private int requiresClassId = 0;
	private int npcId = 0;
	private int summonsNpcId = 0;
	private int awardsItem = 0;
	private boolean awardsDespawn = false;
	private String teleportResponse = "";
	private boolean awardsTitle = false;
	private String title = "";
	private String responseType = "SAY";
	private double awardsExperience = 0;
	private boolean awardsBind = false;
	private String requiresPermissionNode = "";
	private int awardsFactionValue = 0;
	private int awardsFactionId = 0;
	private String requiresAlignment = "NONE";

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
		sender.sendMessage("- requiresmod: " + ChatColor.GOLD + isRequiresMod() + ChatColor.RESET);
		try
		{
			if (getAwardClassLootdropId() != 0) {
				sender.sendMessage("- awardclasslootdropid: " + ChatColor.GOLD + getAwardClassLootdropId() + " ("
						+ StateManager.getInstance().getConfigurationManager().getLootDrop(getAwardClassLootdropId()).getName()
						+ ")" + ChatColor.RESET);
			} else {
				sender.sendMessage(
						"- awardclasslootdropid: " + ChatColor.GOLD + getAwardClassLootdropId() + " (No Loot Table)" + ChatColor.RESET);
			}
		} catch (CoreStateInitException e)
		{
			
		}
		
		sender.sendMessage("- awardsclassspell: " + ChatColor.GOLD + isAwardsClassSpell() + ChatColor.RESET);
		sender.sendMessage("- awardsquest: " + ChatColor.GOLD + getAwardsQuest() + ChatColor.RESET);
		sender.sendMessage("- requiresquestflag: " + ChatColor.GOLD + getRequiresQuestFlag() + ChatColor.RESET);
		sender.sendMessage("- awardsquestflag: " + ChatColor.GOLD + getAwardsQuestFlag() + ChatColor.RESET);
		sender.sendMessage("- requiresraceid: " + ChatColor.GOLD + getRequiresRaceId() + ChatColor.RESET);
		sender.sendMessage("- requiresclassid: " + ChatColor.GOLD + getRequiresClassId() + ChatColor.RESET);
		sender.sendMessage("- requiresalignment: " + ChatColor.GOLD + getRequiresAlignment() + ChatColor.RESET);
		sender.sendMessage("- awardsitem: " + ChatColor.GOLD + getAwardsItem() + ChatColor.RESET);
		sender.sendMessage("- title: " + ChatColor.GOLD + this.getTitle() + ChatColor.RESET);
		sender.sendMessage("- awardstitle: " + ChatColor.GOLD + this.isAwardsTitle() + ChatColor.RESET);
		sender.sendMessage("- summonsnpcid: " + ChatColor.GOLD + this.getSummonsNpcId() + ChatColor.RESET);
		sender.sendMessage(
				"- requirespermissionnode: " + ChatColor.GOLD + getRequiresPermissionNode() + ChatColor.RESET);
		sender.sendMessage("- awardsfactionid: " + ChatColor.GOLD + this.getAwardsFactionId() + ChatColor.RESET);
		sender.sendMessage("- awardsfactionvalue: " + ChatColor.GOLD + this.getAwardsFactionValue() + ChatColor.RESET);
		sender.sendMessage("- awardsdespawn: " + ChatColor.GOLD + this.isAwardsDespawn() + ChatColor.RESET);

		DecimalFormat df = new DecimalFormat("#");
		df.setMaximumFractionDigits(8);
		sender.sendMessage("- awardsxp: " + ChatColor.GOLD + df.format(getAwardsExperience()) + ChatColor.RESET);
	}

	@Override
	public void editTriggerEventSetting(String setting, String value) throws InvalidNPCEventSettingException {
		switch (setting.toLowerCase()) {
		case "randomisedgearsuffix":
			throw new InvalidNPCEventSettingException("This functionality is now disabled due to the item counting reaching > 250000. Please instead use awardclassloottableid");
		case "responsetype":
			if (!value.toUpperCase().equals("SAY") && !value.toUpperCase().equals("EMOTE")) {
				throw new InvalidNPCEventSettingException("responsetype can either be EMOTE or SAY");
			}
			setResponseType(value.toUpperCase());
			break;
		case "title":
			setTitle(value);
			break;
		case "awardclasslootdropid":
			if (Integer.parseInt(value) == 0) {
				setAwardClassLootdropId(0);
				break;
			}

			try
			{
				ISoliniaLootDrop lootdrop = StateManager.getInstance().getConfigurationManager()
						.getLootDrop(Integer.parseInt(value));
				if (lootdrop == null)
					throw new InvalidNPCEventSettingException("Lootdrop ID does not exist");
	
				setAwardClassLootdropId(Integer.parseInt(value));
			} catch (CoreStateInitException e)
			{
				
			}
			break;
		case "awardsclassspell":
			setAwardsClassSpell(Boolean.parseBoolean(value));
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

			// now we need to check if its an item
			if (getInteractiontype().equals(InteractionType.ITEM)) {
				int itemId = Integer.parseInt(value);
				try {
					ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(itemId);
					if (item == null)
						throw new InvalidNPCEventSettingException("Triggerdata itemID does not exist");

					if (item.getQuestId() < 1 && getRequiresQuestFlag() != null && !getRequiresQuestFlag().equals(""))
						throw new InvalidNPCEventSettingException(
								"Cannot assign this item to the npc dialogue as you must set the questid on the item first with /edititem "
										+ itemId + " questid <questid>");

				} catch (CoreStateInitException e) {

				}
			}

			setTriggerdata(value.toUpperCase());
			break;
		case "chatresponse":
			if (value.equals(""))
				throw new InvalidNPCEventSettingException("Chatresponse is empty");
			setChatresponse(value);
			break;
		case "awardsfactionid":
			if (value.equals(""))
				throw new InvalidNPCEventSettingException("Faction ID is empty");

			if (!value.equals("0"))
			{
				try {
					ISoliniaFaction faction = StateManager.getInstance().getConfigurationManager()
							.getFaction(Integer.parseInt(value));
					if (faction == null)
						throw new InvalidNPCEventSettingException("Invalid faction");
				} catch (CoreStateInitException e) {
					throw new InvalidNPCEventSettingException("Faction data not available");
				}
			}

			setAwardsFactionId(Integer.parseInt(value));
			break;
		case "awardsdespawn":
			setAwardsDespawn(Boolean.parseBoolean(value));
			break;
		case "awardsfactionvalue":
			if (value.equals(""))
				throw new InvalidNPCEventSettingException("Faction value is empty");
			setAwardsFactionValue(Integer.parseInt(value));
			break;
		case "interactiontype":
			if (!value.equals("ITEM") && !value.equals("CHAT"))
				throw new InvalidNPCEventSettingException("interactiontype is neither ITEM or CHAT");
			setInteractiontype(InteractionType.valueOf(value));
			break;
		case "requiresclassid":
			int classid = Integer.parseInt(value);
			if (classid < 1) {
				setRequiresClassId(0);
				break;
			}
			try {
				ISoliniaClass classObj = StateManager.getInstance().getConfigurationManager().getClassObj(classid);
				if (classObj == null)
					throw new InvalidNPCEventSettingException("Invalid class id");
			} catch (CoreStateInitException e) {
				throw new InvalidNPCEventSettingException("State not initialised");
			}
			setRequiresClassId(classid);
			break;
		case "requiresalignment":
			if (!value.equals("GOOD") && !value.equals("NEUTRAL") && !value.equals("EVIL") && !value.equals("NONE"))
				throw new InvalidNPCEventSettingException("Invalid alignment - must be GOOD NEUTRAL EVIL or NONE");
			setRequiresAlignment(value);
			break;
		case "requiresmod":
			setRequiresMod(Boolean.parseBoolean(value));
			break;
		case "requiresraceid":
			int raceid = Integer.parseInt(value);
			if (raceid < 1) {
				setRequiresRaceId(0);
				break;
			}
			try {
				ISoliniaRace race = StateManager.getInstance().getConfigurationManager().getRace(raceid);
				if (race == null)
					throw new InvalidNPCEventSettingException("Invalid race id");
			} catch (CoreStateInitException e) {
				throw new InvalidNPCEventSettingException("State not initialised");
			}
			setRequiresRaceId(raceid);
			break;
		case "requiresquest":
			int questid = Integer.parseInt(value);
			if (questid < 1)
				throw new InvalidNPCEventSettingException("Invalid quest id");
			try {
				ISoliniaQuest quest = StateManager.getInstance().getConfigurationManager().getQuest(questid);
				if (quest == null)
					throw new InvalidNPCEventSettingException("Invalid quest id");

				// now we need to check if the item is a quest item
				if (getInteractiontype().equals(InteractionType.ITEM)) {
					int itemId = Integer.parseInt(this.getTriggerdata());
					try {
						ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(itemId);
						if (item == null)
							throw new InvalidNPCEventSettingException("Triggerdata itemID does not exist");

						if (item.getQuestId() < 1)
							throw new InvalidNPCEventSettingException(
									"The item event you are trying to make into a quest does not have the quest flag set on the Item - Please /edititem "
											+ itemId + " questid <questid>");

					} catch (CoreStateInitException e) {

					}
				}
			} catch (CoreStateInitException e) {
				throw new InvalidNPCEventSettingException("State not initialised");
			}
			setRequiresQuest(questid);
			break;
		case "awardsquest":
			int aquestid = Integer.parseInt(value);
			if (aquestid < 1)
				throw new InvalidNPCEventSettingException("Invalid quest id");
			try {
				ISoliniaQuest quest = StateManager.getInstance().getConfigurationManager().getQuest(aquestid);
				if (quest == null)
					throw new InvalidNPCEventSettingException("Invalid quest id");

				// now we need to check if the item is a quest item
				if (getInteractiontype().equals(InteractionType.ITEM)) {
					int itemId = Integer.parseInt(this.getTriggerdata());
					try {
						ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(itemId);
						if (item == null)
							throw new InvalidNPCEventSettingException("Triggerdata itemID does not exist");

						if (item.getQuestId() < 1)
							throw new InvalidNPCEventSettingException(
									"The item event you are trying to make into a quest does not have the quest flag set on the Item - Please edititem "
											+ itemId + " questid <questid>");

					} catch (CoreStateInitException e) {

					}
				}
			} catch (CoreStateInitException e) {
				throw new InvalidNPCEventSettingException("State not initialised");
			}
			setAwardsQuest(aquestid);
			break;
		case "requiresquestflag":
			// now we need to check if its an item
			if (getInteractiontype().equals(InteractionType.ITEM)) {
				int itemId = Integer.parseInt(this.getTriggerdata());
				try {
					ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(itemId);
					if (item == null)
						throw new InvalidNPCEventSettingException("Triggerdata itemID does not exist");

					if (item.getQuestId() < 1 && getRequiresQuestFlag() != null && !getRequiresQuestFlag().equals(""))
						throw new InvalidNPCEventSettingException(
								"Cannot set this npc event to require a quest flag as the item ID that triggers the event is not marked as requiresquest please set the quest id on the item with /edititem "
										+ itemId + " questid <questid>");

				} catch (CoreStateInitException e) {

				}
			}

			setRequiresQuestFlag(value);
			break;
		case "awardsquestflag":
			setAwardsQuestFlag(value);
			break;
		case "teleportresponse":
			try {
				String[] zonedata = value.split(",");
				// Dissasemble the value to ensure it is correct
				String world = zonedata[0];
				double x = Double.parseDouble(zonedata[1]);
				double y = Double.parseDouble(zonedata[2]);
				double z = Double.parseDouble(zonedata[3]);

				setTeleportResponse(world + "," + x + "," + y + "," + z);
				break;
			} catch (Exception e) {
				throw new InvalidNPCEventSettingException("Teleport zone value must be in format: world,x,y,z");
			}
		case "awardsrandomisedgear":
			/*setAwardsRandomisedGear(Boolean.parseBoolean(value));*/
			throw new InvalidNPCEventSettingException("This functionality is now disabled due to the item counting reaching > 250000. Please instead use awardclassloottableid");
		case "summonsnpcid":
			int npcId = Integer.parseInt(value);
			try {
				ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(npcId);
				if (npc == null)
					throw new InvalidNPCEventSettingException("Invalid npc ID");
				setSummonsNpcId(npcId);
			} catch (CoreStateInitException e) {

			}
			break;
		case "awardsitem":
			int itemId = Integer.parseInt(value);
			if (itemId < 1)
				throw new InvalidNPCEventSettingException("Invalid item ID");

			if (getAwardsQuestFlag() == null || getAwardsQuestFlag().equals(""))
				throw new InvalidNPCEventSettingException(
						"You cannot set an awardsitem to a npc event handler unless the npc awards a quest flag -  this is to prevent duplicated awards");

			try {
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(itemId);
				if (item == null)
					throw new InvalidNPCEventSettingException("Invalid item id");
			} catch (CoreStateInitException e) {
				throw new InvalidNPCEventSettingException("State not initialised");
			}
			setAwardsItem(itemId);
			break;
		case "awardsxp":
			double xp = Double.parseDouble(value);
			int maxLevel = 70;
			try {
				maxLevel = StateManager.getInstance().getConfigurationManager().getMaxLevel();
			} catch (CoreStateInitException e) {

			}
			double maxXp = (PlayerUtils.getExperienceRequirementForLevel(maxLevel) / maxLevel);
			if (xp < 0 || xp > maxXp) {
				DecimalFormat df = new DecimalFormat("#");
				df.setMaximumFractionDigits(8);

				throw new InvalidNPCEventSettingException(
						"XP must be greater than -1 and less than " + df.format(maxXp));
			}

			if (getAwardsQuestFlag() == null || getAwardsQuestFlag().equals(""))
				throw new InvalidNPCEventSettingException(
						"You cannot set a rewardsxp to a npc event handler unless the npc awards a quest flag -  this is to prevent duplicated awards");

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
		try {
			ISoliniaPlayer player = SoliniaPlayerAdapter.Adapt(triggerentity);
			if (!getRequiresPermissionNode().equals("")) {
				if (!triggerentity.hasPermission(getRequiresPermissionNode())) {
					triggerentity.sendMessage("This requires a permission node you do not have");
					return false;
				}
			}
			
			if (isRequiresMod()) {
				if (!StateManager.getInstance().getPlayerManager().hasValidMod(triggerentity))
				{
					triggerentity.sendMessage("This requires the Solinia3-UI Mod and you do not have the correct version");
					return false;
				}
			}
			
			if (getRequiresQuest() > 0) {
				boolean foundQuest = false;
				for (PlayerQuest playerQuest : player.getPlayerQuests()) {
					if (playerQuest.getQuestId() == getRequiresQuest()) {
						foundQuest = true;
					}
				}

				if (foundQuest == false)
					return false;
			}

			if (getRequiresAlignment() != null && !getRequiresAlignment().equals("")
					&& !getRequiresAlignment().equals("NONE")) {
				if (player.getRace() != null) {
					if (!player.getRace().getAlignment().toLowerCase().equals(getRequiresAlignment().toLowerCase()))
						return false;
				} else {
					return false;
				}
			}

			if (getRequiresQuestFlag() != null && !getRequiresQuestFlag().equals("")) {
				if (!player.hasQuestFlag(getRequiresQuestFlag()))
					return false;
			}

			if (getRequiresRaceId() > 0) {
				if (player.getRace() != null) {
					if (player.getRace().getId() != getRequiresRaceId())
						return false;
				} else {
					return false;
				}
			}

			if (getRequiresClassId() > 0) {
				if (player.getClassObj() != null) {
					if (player.getClassObj().getId() != getRequiresClassId())
						return false;
				} else {
					return false;
				}
			}

			return true;

		} catch (CoreStateInitException e) {
			return false;
		}
	}

	@Override
	public void awardPlayer(Player triggerentity, LivingEntity npcLivingEntity) {
		try {
			ISoliniaPlayer player = SoliniaPlayerAdapter.Adapt(triggerentity);

			boolean hasAwardedXp = false;
			boolean hasAwardedFaction = false;
			
			if (getAwardsQuest() > 0) {
				boolean foundQuest = false;
				for (PlayerQuest playerQuest : player.getPlayerQuests()) {
					if (playerQuest.getQuestId() == getAwardsQuest()) {
						foundQuest = true;
					}
				}

				if (foundQuest == false)
					player.addPlayerQuest(getAwardsQuest());
			}
			
			if (this.isAwardsDespawn() == true)
				EntityUtils.RemoveEntity(npcLivingEntity,"awardsdespawn");
			
			// We can support item hand in rewards multiple times without
			// a quest flag
			if (getInteractiontype().equals(InteractionType.ITEM))
			{
				if (player != null && player.getClassObj() != null && this.getAwardClassLootdropId() > 0)
				{
					ISoliniaLootDrop soliniaLootDrop = StateManager.getInstance().getConfigurationManager().getLootDrop(this.getAwardClassLootdropId());
					if (soliniaLootDrop != null)
					{
						List<ISoliniaLootDropEntry> entries = soliniaLootDrop.getEntriesForClass(player.getClassObj());
						if (entries.size() > 0)
						{
							List<ISoliniaLootDropEntry> pickedEntry = MathUtils.pickNRandom(entries, 1);
						
							final int awardclassitemid = pickedEntry.get(0).getItemid();
							final UUID awardclassplayeruuid = player.getBukkitPlayer().getUniqueId();
							
							Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(
									StateManager.getInstance().getPlugin(), new Runnable() {
										public void run() {
											try {
												ISoliniaItem item = StateManager.getInstance().getConfigurationManager()
														.getItem(awardclassitemid);
												if (item == null)
													return;
	
												PlayerUtils.addToPlayersInventory(Bukkit.getPlayer(awardclassplayeruuid), item.asItemStack());
												System.out.println("Awarded item: " + item.getDisplayname());
											} catch (CoreStateInitException e) {
												// skip
											}
										}
									});
							}
						
					}
				}
				
				if (this.isAwardsTitle() == true) {
					if (this.getTitle() != null) {
						if (!this.getTitle().equals("")) {
							player.grantTitle(this.getTitle());
						}
					}
				}
				
				// We can also awards spells
				if (player != null && player.getClassObj() != null && this.isAwardsClassSpell())
				{
					ISoliniaLootTable table = StateManager.getInstance().getConfigurationManager().getLootTable(player.getClassObj().getDropSpellsLootTableId());
					List<ISoliniaLootDropEntry> entries = new ArrayList<ISoliniaLootDropEntry>();
					
					for (ISoliniaLootTableEntry entry : StateManager.getInstance().getConfigurationManager()
							.getLootTable(table.getId()).getEntries()) {
						ISoliniaLootDrop droptable = StateManager.getInstance().getConfigurationManager()
								.getLootDrop(entry.getLootdropid());
						for (ISoliniaLootDropEntry dropentry : StateManager.getInstance().getConfigurationManager()
								.getLootDrop(droptable.getId()).getEntries()) {
							
							// this is only used for spells
							// validate item
							ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(dropentry.getItemid());
							if (item == null)
								continue;
							
							if (!item.isSpellscroll())
								continue;

							if (item.getAbilityid() < 1)
								continue;
							
							ISoliniaSpell spell = StateManager.getInstance().getConfigurationManager().getSpell(item.getAbilityid());
							if (spell == null)
								continue;
							
							if (spell.getMinLevelClass(player.getClassObj().getName().toUpperCase()) > player.getActualLevel())
								continue;
							
							entries.add(dropentry);
						}
					}
					
					if (entries.size() > 0)
					{
						List<ISoliniaLootDropEntry> pickedEntry = MathUtils.pickNRandom(entries, 1);
						final int awardclassitemid = pickedEntry.get(0).getItemid();
						final UUID awardclassplayeruuid = player.getBukkitPlayer().getUniqueId();
						
						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(
								StateManager.getInstance().getPlugin(), new Runnable() {
									public void run() {
										try {
											ISoliniaItem item = StateManager.getInstance().getConfigurationManager()
													.getItem(awardclassitemid);
											if (item == null)
												return;

											PlayerUtils.addToPlayersInventory(Bukkit.getPlayer(awardclassplayeruuid), item.asItemStack());
											System.out.println("Awarded item: " + item.getDisplayname());
										} catch (CoreStateInitException e) {
											// skip
										}
									}
								});
						
					}
				}
				
				// xp 
				if (getAwardsExperience() > 0) {
					player.increasePlayerExperience(getAwardsExperience(), false, true);
					hasAwardedXp = true;
				}

				// faction
				if (getAwardsFactionId() > 0) {
					if (getAwardsFactionValue() > 0)
					{
						player.increaseFactionStanding(getAwardsFactionId(), getAwardsFactionValue());
						hasAwardedFaction = true;
					}
					if (getAwardsFactionValue() < 0)
					{
						player.decreaseFactionStanding(getAwardsFactionId(), -1 * getAwardsFactionValue());
						hasAwardedFaction = true;
					}
				}
			}
			

			if (getAwardsQuestFlag() != null && !getAwardsQuestFlag().equals("")) {
				boolean foundQuestFlag = false;
				for (String playerQuestFlag : player.getPlayerQuestFlags()) {
					if (playerQuestFlag.equals(getAwardsQuestFlag())) {
						foundQuestFlag = true;
					}
				}

				if (foundQuestFlag == false) {
					player.addPlayerQuestFlag(getAwardsQuestFlag());

					// All item awards must be accompanied with a quest flag else they will repeat
					// the item return over and over
					if (getAwardsItem() > 0) {
						System.out.println("Awarding item with awardquestflag: " + getAwardsQuestFlag());
						ISoliniaItem item = StateManager.getInstance().getConfigurationManager()
								.getItem(getAwardsItem());

						final int awarditemid = getAwardsItem();
						final UUID uuid = player.getBukkitPlayer().getUniqueId();

						if (item != null) {

							Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(
									StateManager.getInstance().getPlugin(), new Runnable() {
										public void run() {
											try {
												ISoliniaItem item = StateManager.getInstance().getConfigurationManager()
														.getItem(awarditemid);

												PlayerUtils.addToPlayersInventory(Bukkit.getPlayer(uuid), item.asItemStack());
												System.out.println("Awarded item: " + item.getDisplayname());
											} catch (CoreStateInitException e) {
												// skip
											}
										}
									});

						}
					}

					if (this.isAwardsTitle() == true) {
						if (this.getTitle() != null) {
							if (!this.getTitle().equals("")) {
								player.grantTitle(this.getTitle());
							}
						}
					}
					
					// xp 
					if (!hasAwardedXp)
					if (getAwardsExperience() > 0) {
						player.increasePlayerExperience(getAwardsExperience(), false, true);
						hasAwardedXp = true;
					}

					// faction
					if (!hasAwardedFaction)
					if (getAwardsFactionId() > 0) {
						if (getAwardsFactionValue() > 0)
						{
							player.increaseFactionStanding(getAwardsFactionId(), getAwardsFactionValue());
							hasAwardedFaction = true;
						}
						if (getAwardsFactionValue() < 0)
						{
							player.decreaseFactionStanding(getAwardsFactionId(), -1 * getAwardsFactionValue());
							hasAwardedFaction = true;
						}
					}

					/*if (isAwardsRandomisedGear() == true) {
						awardRandomisedGear(player);
					}*/
				}
			}
		} catch (CoreStateInitException e) {
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

	public int getAwardsFactionValue() {
		return awardsFactionValue;
	}

	public void setAwardsFactionValue(int awardsFactionValue) {
		this.awardsFactionValue = awardsFactionValue;
	}

	public int getAwardsFactionId() {
		return awardsFactionId;
	}

	public void setAwardsFactionId(int awardsFactionId) {
		this.awardsFactionId = awardsFactionId;
	}

	public String getRequiresAlignment() {
		return requiresAlignment;
	}

	public void setRequiresAlignment(String requiresAlignment) {
		this.requiresAlignment = requiresAlignment;
	}

	public int getAwardClassLootdropId() {
		return awardClassLootdropId;
	}

	public void setAwardClassLootdropId(int awardClassLootdropId) {
		this.awardClassLootdropId = awardClassLootdropId;
	}

	public boolean isAwardsClassSpell() {
		return awardsClassSpell;
	}

	public void setAwardsClassSpell(boolean awardsClassSpell) {
		this.awardsClassSpell = awardsClassSpell;
	}

	public boolean isAwardsDespawn() {
		return awardsDespawn;
	}

	public void setAwardsDespawn(boolean awardsDespawn) {
		this.awardsDespawn = awardsDespawn;
	}

	public boolean isRequiresMod() {
		return requiresMod;
	}

	public void setRequiresMod(boolean requiresMod) {
		this.requiresMod = requiresMod;
	}
}
