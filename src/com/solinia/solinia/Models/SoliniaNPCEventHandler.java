package com.solinia.solinia.Models;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidNPCEventSettingException;
import com.solinia.solinia.Exceptions.InvalidNpcSettingException;
import com.solinia.solinia.Interfaces.ISoliniaFaction;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaLootTable;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Interfaces.ISoliniaNPCEventHandler;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Interfaces.ISoliniaQuest;
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
	private int npcId;
	private int awardsItem = 0;

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
		sender.sendMessage("- interactiontype: " + ChatColor.GOLD + getInteractiontype() + ChatColor.RESET);
		sender.sendMessage("- requiresquest: " + ChatColor.GOLD + getRequiresQuest() + ChatColor.RESET);
		sender.sendMessage("- awardsquest: " + ChatColor.GOLD + getAwardsQuest() + ChatColor.RESET);
		sender.sendMessage("- requiresquestflag: " + ChatColor.GOLD + getRequiresQuestFlag() + ChatColor.RESET);
		sender.sendMessage("- awardsquestflag: " + ChatColor.GOLD + getAwardsQuestFlag() + ChatColor.RESET);
		sender.sendMessage("- awardsitem: " + ChatColor.GOLD + getAwardsItem() + ChatColor.RESET);
	}

	@Override
	public void editTriggerEventSetting(String setting, String value) throws InvalidNPCEventSettingException {
		switch (setting.toLowerCase()) {
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
		default:
			throw new InvalidNPCEventSettingException(
					"Invalid NPC Event setting. Valid Options are: triggerdata,chatresponse,interactiontype,requiresquest,awardsquest,requiresquestflag,awardsquestflag,awardsitem");
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
				}
			}
		} catch (CoreStateInitException e)
		{
			System.out.println(e.getMessage());
			return;
		}
	}

}
