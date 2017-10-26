package com.solinia.solinia.Models;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaAAAbility;
import com.solinia.solinia.Interfaces.ISoliniaAARank;
import com.solinia.solinia.Interfaces.ISoliniaClass;
import com.solinia.solinia.Interfaces.ISoliniaFaction;
import com.solinia.solinia.Interfaces.ISoliniaGroup;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Interfaces.ISoliniaNPCEventHandler;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Interfaces.ISoliniaRace;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Utils.ItemStackUtils;
import com.solinia.solinia.Utils.ScoreboardUtils;
import com.solinia.solinia.Utils.Utils;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class SoliniaPlayer implements ISoliniaPlayer {

	private static final long serialVersionUID = 9075039437399478391L;
	private UUID uuid;
	private String forename = "";
	private String lastname = "";
	private int mana = 0;
	private Double experience = 0d;
	private int aapct;
	private Double aaexperience = 0d;
	private int aapoints = 0;
	private int raceid = 0;
	private boolean haschosenrace = false;
	private boolean haschosenclass = false;
	private int classid = 0;
	private String language;
	private String gender = "MALE";
	private List<SoliniaPlayerSkill> skills = new ArrayList<SoliniaPlayerSkill>();
	private UUID interaction;
	private String currentChannel = "OOC";
	private List<Integer> ranks = new ArrayList<Integer>();
	private List<Integer> aas = new ArrayList<Integer>();
	private List<PlayerFactionEntry> factionEntries = new ArrayList<PlayerFactionEntry>();
	private List<UUID> ignoredPlayers = new ArrayList<UUID>();
	private List<String> availableTitles = new ArrayList<String>();
	private String title = "";
	private List<PlayerQuest> playerQuests = new ArrayList<PlayerQuest>();
	private List<String> playerQuestFlags = new ArrayList<String>();
	
	@Override
	public List<UUID> getIgnoredPlayers()
	{
		return ignoredPlayers;
	}
	
	@Override
	public boolean hasIgnored(UUID uuid)
	{
		return ignoredPlayers.contains(uuid);
	}
	
	@Override
	public void setIgnoredPlayers(List<UUID> ignoredPlayers)
	{
		this.ignoredPlayers = ignoredPlayers;
	}
	
	@Override
	public UUID getUUID() {
		return uuid;
	}
	
	@Override
	public boolean grantTitle(String title)
	{
		if (getAvailableTitles().contains(title))
			return false;
		
		getAvailableTitles().add(title);
		getBukkitPlayer().sendMessage(ChatColor.YELLOW + "* You have earned the title: " + title + ChatColor.RESET + " See /settitle");		
		return true;
	}
	
	@Override
	public void setUUID(UUID uuid) {
		this.uuid = uuid;
	}

	@Override
	public String getForename() {
		return forename;
	}

	@Override
	public void setForename(String forename) {
		this.forename = forename;
	}

	@Override
	public String getLastname() {
		return lastname;
	}

	@Override
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	
	@Override
	public void updateDisplayName()
	{
		if (getBukkitPlayer() != null)
		{
			getBukkitPlayer().setDisplayName(getFullName());
			getBukkitPlayer().setCustomName(getFullName());
			getBukkitPlayer().setPlayerListName(getFullName());
			
			if (this.getGroup() != null) {
				StateManager.getInstance().removePlayerFromGroup(this.getBukkitPlayer());
			} else {
				ScoreboardUtils.RemoveScoreboard(this.getBukkitPlayer().getUniqueId());
			}
		}
	}
	
	@Override
	public Player getBukkitPlayer()
	{
		Player player = Bukkit.getPlayer(uuid);
		return player;
	}
	
	@Override
	public String getFullName()
	{
		String displayName = forename;
		if (lastname != null && !lastname.equals(""))
			displayName = forename + "_" + lastname;
		
		return displayName;
	}
	
	@Override
	public String getFullNameWithTitle()
	{
		String displayName = getFullName();
		if (getTitle() != null && !(getTitle().equals(""))) {
			displayName += " " + getTitle();
		}
		
		return displayName;
	}

	@Override
	public int getMana() {
		// TODO Auto-generated method stub
		return this.mana;
	}
	
	@Override
	public void setMana(int mana)
	{
		this.mana = mana;
		try
		{
			ScoreboardUtils.UpdateScoreboard(this.getBukkitPlayer(), SoliniaLivingEntityAdapter.Adapt(getBukkitPlayer()).getMaxMP(), mana);
		} catch (CoreStateInitException e)
		{
			//
		}
	}
	
	@Override
	public Double getAAExperience() {
		return this.aaexperience;
	}
	
	@Override
	public void setAAExperience(Double aaexperience) {
		this.aaexperience = aaexperience;
	}
	
	@Override
	public Double getExperience() {
		return this.experience;
	}
	
	@Override
	public void setExperience(Double experience) {
		this.experience = experience;
	}
	
	@Override
	public int getLevel()
	{
		return Utils.getLevelFromExperience(this.experience);		
	}

	@Override
	public int getRaceId() {
		// TODO Auto-generated method stub
		return this.raceid;
	}

	@Override
	public boolean hasChosenRace() {
		return this.haschosenrace;
	}
	
	@Override
	public void setChosenRace(boolean chosen) {
		this.haschosenrace = chosen;
	}

	@Override
	public void setRaceId(int raceid) {
		// TODO Auto-generated method stub
		this.raceid = raceid;
		this.language = getRace().getName().toUpperCase();
		updateMaxHp();
	}
	
	@Override
	public ISoliniaRace getRace()
	{
		try {
			return StateManager.getInstance().getConfigurationManager().getRace(getRaceId());
		} catch (CoreStateInitException e) {
			return null;
		}
	}

	@Override
	public int getClassId() {
		return classid;
	}

	@Override
	public void setClassId(int classid) {
		this.classid = classid;
	}

	@Override
	public boolean hasChosenClass() {
		return haschosenclass;
	}

	@Override
	public void setChosenClass(boolean haschosenclass) {
		this.haschosenclass = haschosenclass;
	}
	
	@Override
	public ISoliniaClass getClassObj()
	{
		try {
			return StateManager.getInstance().getConfigurationManager().getClassObj(getClassId());
		} catch (CoreStateInitException e) {
			return null;
		}
	}

	@Override
	public void updateMaxHp() {
		if (getBukkitPlayer() != null && getExperience() != null)
		{		
			try
			{
				ISoliniaLivingEntity solentity = SoliniaLivingEntityAdapter.Adapt(getBukkitPlayer());
				double calculatedhp = solentity.getMaxHP();
				getBukkitPlayer().setMaxHealth(calculatedhp);
				getBukkitPlayer().setHealthScaled(true);
				getBukkitPlayer().setHealthScale(40D);
			} catch (CoreStateInitException e)
			{
				
			}
		}
	}
		
	@Override
	public void increasePlayerExperience(Double experience) {
		if (!isAAOn()) {
			increasePlayerNormalExperience(experience);
		} else {
			int normalpct = 100-getAapct();
			if (normalpct > 0)
			{
				Double normalexperience = (experience / 100) * normalpct;
				increasePlayerNormalExperience(normalexperience);
			}
			
			Double aaexperience = (experience / 100) * getAapct();
			increasePlayerAAExperience(aaexperience);
		}
	}

	private boolean isAAOn() {
		if (this.getAapct() > 0)
			return true;
		
		return false;
	}

	@Override
	public void increasePlayerNormalExperience(Double experience) {
		double classxpmodifier = Utils.getClassXPModifier(getClassObj());
		experience = experience * (classxpmodifier / 100);

		boolean modified = false;
		double modifier = StateManager.getInstance().getWorldPerkXPModifier();
		if (modifier > 100) {
			modified = true;
		}
		experience = experience * (modifier / 100);

		Double currentexperience = getExperience();

		// make sure someone never gets more than a level per kill
		double clevel = Utils.getLevelFromExperience(currentexperience);
		double nlevel = Utils.getLevelFromExperience((currentexperience + experience));

		if (nlevel > (clevel + 1)) {
			// xp is way too high, drop to proper amount

			double xp = Utils.getExperienceRequirementForLevel((int) clevel + 1);
			experience = xp - currentexperience;
		}

		if (getClassObj() == null) {
			if (nlevel > 10) {
				double xp = Utils.getExperienceRequirementForLevel(10);
				experience = xp - currentexperience;
			}
		}

		if ((currentexperience + experience) > Utils.getExperienceRequirementForLevel(Utils.getMaxLevel())) {
			//System.out.println("XP: " + experience);
			currentexperience = Utils.getExperienceRequirementForLevel(Utils.getMaxLevel());
		} else {
			//System.out.println("XP: " + experience);
			currentexperience = currentexperience + experience;
		}
		setExperience(currentexperience, experience,modified);
	}
	
	public void setExperience(Double experience, Double changeamount, boolean modified) {
		Double level = (double) getLevel();

		this.experience = experience;
		
		Double newlevel = (double) getLevel();

		Double xpneededforcurrentlevel = Utils.getExperienceRequirementForLevel((int) (newlevel + 0));
		Double xpneededfornextlevel = Utils.getExperienceRequirementForLevel((int) (newlevel + 1));
		Double totalxpneeded = xpneededfornextlevel - xpneededforcurrentlevel;
		Double currentxpprogress = experience - xpneededforcurrentlevel;

		Double percenttolevel = Math.floor((currentxpprogress / totalxpneeded) * 100);
		int ipercenttolevel = percenttolevel.intValue();
		if (changeamount > 0) {
			getBukkitPlayer().sendMessage(ChatColor.YELLOW + "* You gain experience (" + ipercenttolevel + "% into level)");
			if (modified == true)
				getBukkitPlayer().sendMessage(
						ChatColor.YELLOW + "* You were given bonus XP from a player donation perk! (/perks)");
		}

		if (changeamount < 0) {
			getBukkitPlayer().sendMessage(ChatColor.RED + "* You lost experience (" + ipercenttolevel + "% into level)");
		}
		if (Double.compare(newlevel, level) > 0) {
			String classname = "Hero";
			if (getClassObj() != null)
				classname = getClassObj().getName();
			
			StateManager.getInstance().getChannelManager().sendToDiscordMC(null,StateManager.getInstance().getChannelManager().getDefaultDiscordChannel(),getFullName() + " has reached new heights as a level " + (int)Math.floor(newlevel) + " " + classname.toLowerCase() + "!");
			getBukkitPlayer().sendMessage(ChatColor.DARK_PURPLE + "* You gained a level (" + newlevel + ")!");
			getBukkitPlayer().getWorld().playEffect(getBukkitPlayer().getLocation(), Effect.FIREWORK_SHOOT, 1);
			
			// Title rewards
			if (newlevel >= 10)
			{
				if (!getAvailableTitles().contains("the Apprentice"))
				{
					grantTitle("the Apprentice");
				}
			}
			
			if (newlevel >= 20)
			{
				if (!getAvailableTitles().contains("the Journeyman"))
				{
					grantTitle("the Journeyman");
				}
			}
			
			if (newlevel >= 30)
			{
				if (!getAvailableTitles().contains("the Expert"))
				{
					grantTitle("the Expert");
				}
			}
			
			if (newlevel >= 40)
			{
				if (!getAvailableTitles().contains("the Hero"))
				{
					grantTitle("the Hero");
				}
			}
			
			if (newlevel >= 50)
			{
				if (!getAvailableTitles().contains("the Legend"))
				{
					grantTitle("the Legend");
				}
			}

            updateMaxHp();
		}

		if (Double.compare(newlevel, level) < 0) {
			getBukkitPlayer().sendMessage(ChatColor.DARK_PURPLE + "* You lost a level (" + newlevel + ")!");

            updateMaxHp();
		}
	}
	
	@Override
	public void increasePlayerAAExperience(Double experience) {

		boolean modified = false;
		double modifier = StateManager.getInstance().getWorldPerkXPModifier();
		if (modifier > 100) {
			modified = true;
		}
		experience = experience * (modifier / 100);
		
		// Cap at max just under a quarter of an AA experience point
		if (experience > Utils.getMaxAAXP()) {
			experience = Utils.getMaxAAXP();
		}

		//System.out.println("AA XP: " + experience);
		
		Double currentaaexperience = getAAExperience();
		currentaaexperience = currentaaexperience + experience;
		
		setAAExperience(currentaaexperience, modified);
	}
	
	private void setAAExperience(Double aaexperience, Boolean modified) {
		// One AA level is always equal to the same experience as is needed for
		// 39 to level 40
		// AA xp should never be above 2313441000
		
		if (getClassObj() == null)
			return;

		// Max limit on AA points right now is 100
		if (getAAPoints() > 100) {
			return;
		}

		boolean givenaapoint = false;
		// Every time they get aa xp of 2313441000, give them an AA point
		if (aaexperience > Utils.getMaxAAXP()) {
			aaexperience = aaexperience - Utils.getMaxAAXP();
			setAAPoints(getAAPoints() + 1);
			givenaapoint = true;
		}

		setAAExperience(aaexperience);

		Double percenttoaa = Math.floor((aaexperience / Utils.getMaxAAXP()) * 100);
		int ipercenttoaa = percenttoaa.intValue();

		getBukkitPlayer().sendMessage(ChatColor.YELLOW + "* You gain alternate experience (" + ipercenttoaa + "% into AA)!");
		if (modified == true)
			getBukkitPlayer().sendMessage(
					ChatColor.YELLOW + "* You were given bonus XP from a player donation perk! (/perks)");

		if (givenaapoint) {
			getBukkitPlayer().sendMessage(ChatColor.YELLOW + "* You gained an Alternate Experience Point!");
		}
	}

	@Override
	public void giveMoney(int i) {
		try
		{
			StateManager.getInstance().giveMoney(getBukkitPlayer(),i);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public int getAAPoints() {
		return aapoints;
	}

	@Override
	public void setAAPoints(int aapoints) {
		this.aapoints = aapoints;
	}

	@Override
	public String getLanguage() {
		if (language == null || language.equals("UNKNOWN"))
				if (getRace() != null)
					language = getRace().getName();
		return language;
	}

	@Override
	public void setLanguage(String language) {
		this.language = language;
	}

	@Override
	public String getGender() {
		return gender;
	}

	@Override
	public void setGender(String gender) {
		this.gender = gender;
	}

	@Override
	public int getSkillCap(String skillName) {
		return Utils.getSkillCap(this,skillName);
	}

	@Override
	public List<SoliniaPlayerSkill> getSkills() {
		// TODO Auto-generated method stub
		return this.skills;
	}

	@Override
	public void emote(String string) {
		StateManager.getInstance().getChannelManager().sendToLocalChannel(this,string);
	}
	
	@Override
	public void ooc(String string) {
		if (getLanguage() == null || getLanguage().equals("UNKNOWN"))
		{
			if (getRace() == null)
			{
				getBukkitPlayer().sendMessage("You cannot speak until you set a race /setrace");
				return;
			} else {
				setLanguage(getRace().getName().toUpperCase());
			}
		}
		StateManager.getInstance().getChannelManager().sendToGlobalChannelDecorated(this,string);
	}

	@Override
	public void say(String string) {
		if (getLanguage() == null || getLanguage().equals("UNKNOWN"))
		{
			if (getRace() == null)
			{
				getBukkitPlayer().sendMessage("You cannot speak until you set a race /setrace");
				return;
			} else {
				setLanguage(getRace().getName().toUpperCase());
			}
		}
		StateManager.getInstance().getChannelManager().sendToLocalChannelDecorated(this,string);
	}
	
	@Override
	public SoliniaPlayerSkill getSkill(String skillname) {
		for(SoliniaPlayerSkill skill : this.skills)
		{
			if (skill.getSkillName().toUpperCase().equals(skillname.toUpperCase()))
				return skill;
		}
		
		// If we got this far the skill doesn't exist, create it with 0
		SoliniaPlayerSkill skill = new SoliniaPlayerSkill(skillname.toUpperCase(),0);
		skills.add(skill);
		return skill;
	}

	@Override
	public void tryIncreaseSkill(String skillname, int skillupamount) {
		SoliniaPlayerSkill skill = getSkill(skillname);
		int currentskill = 0;
		if (skill != null) {
			currentskill = skill.getValue();
		}

		int skillcap = getSkillCap(skillname);
		if ((currentskill + skillupamount) > skillcap) {
			return;
		}

		int chance = 10 + ((252 - currentskill) / 20);
		if (chance < 1) {
			chance = 1;
		}

		Random r = new Random();
		int randomInt = r.nextInt(100) + 1;
		if (randomInt < chance) {
			setSkill(skillname, currentskill + skillupamount);
		}
		
	}

	@Override
	public void setSkill(String skillname, int value) 
	{
		if (value > Integer.MAX_VALUE) 
			return;

		// max skill point
		if (value > 255)
			return;

		skillname = skillname.toUpperCase();
		
		if (this.skills == null)
			this.skills = new ArrayList<SoliniaPlayerSkill>();

		boolean updated = false;

		for (SoliniaPlayerSkill skill : this.skills) 
		{
			if (skill.getSkillName().toUpperCase().equals(skillname.toUpperCase())) 
			{
				skill.setValue(value);
				updated = true;
				getBukkitPlayer().sendMessage(ChatColor.YELLOW + "* You get better at " + skillname + " (" + value + ")");
				return;
			}
		}

		if (updated == false) {
			SoliniaPlayerSkill skill = new SoliniaPlayerSkill(skillname.toUpperCase(), value);
			skills.add(skill);
		}
		
		getBukkitPlayer().sendMessage(ChatColor.YELLOW + "* You get better at " + skillname + " (" + value + ")");
	}
	
	@Override
	public void reducePlayerMana(int mana) {

		int currentmana = getMana();
		if ((currentmana - mana) < 1) {
			currentmana = 0;
		} else {
			currentmana = currentmana - mana;
		}
		setMana(currentmana);
	}

	@Override
	public void increasePlayerMana(int mana) {
		int currentmana = getMana();
		int maxmp = 1;
		try
		{
			ISoliniaLivingEntity solentity = SoliniaLivingEntityAdapter.Adapt(this.getBukkitPlayer());
			maxmp = solentity.getMaxMP();
		} catch (CoreStateInitException e)
		{
			// do nothing
		}

		if ((currentmana + mana) > maxmp) {
			currentmana = maxmp;
		} else {
			currentmana = currentmana + mana;
		}
		
		setMana(currentmana);
	}

	@Override
	public void interact(Plugin plugin, PlayerInteractEvent event) {
		// TODO Auto-generated method stub
		ItemStack itemstack = event.getItem();
	    
		try
		{
		    // this is the item not in offhand
		    if (event.getHand() == EquipmentSlot.HAND && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK))
		    {
			    if (itemstack == null)
			    {
				    return;
			    }

			    if ((!(itemstack.getEnchantmentLevel(Enchantment.OXYGEN) > 999)))
		    		return;

		    	// We have our custom item id, lets check it exists
		    	ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(itemstack);
		    	
		    	if (item == null)
		    	{
		    		return;
		    	}
		    	
		    	// Start applying an augmentation
		    	if((event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && item.isAugmentation()) 
			    {
		    		if (StateManager.getInstance().getPlayerManager().getApplyingAugmentation(event.getPlayer().getUniqueId()) == null || 
		    				StateManager.getInstance().getPlayerManager().getApplyingAugmentation(event.getPlayer().getUniqueId()) == 0
		    				)
		    		{
			    		StateManager.getInstance().getPlayerManager().setApplyingAugmentation(event.getPlayer().getUniqueId(), item.getId());
			    		event.getPlayer().sendMessage("* Applying " + item.getDisplayname() + " to an item, please right click the item you wish to apply this augmentation to. . To cancel applying, right click while holding this item again");
		    		} else {
		    			StateManager.getInstance().getPlayerManager().setApplyingAugmentation(event.getPlayer().getUniqueId(), 0);
		    			event.getPlayer().sendMessage("* Cancelled applying augmentation");
		    		}
		    		return;
			    }
		    	
		    	if((event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && item.isPetControlRod()) 
			    {
		    		LivingEntity targetmob = Utils.getTargettedLivingEntity(event.getPlayer(), 50);
		    		if (targetmob != null)
		    		{
		    			item.useItemOnEntity(plugin, event.getPlayer(),item,targetmob,false);
		    			return;
		    		}
			    }
		    	
		    	if (item.getAbilityid() < 1)
		    	{
		    		return;
		    	}

		    	ISoliniaSpell spell = StateManager.getInstance().getConfigurationManager().getSpell(item.getAbilityid());
			    
			    // Only applies to consumable items
		    	if((event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && item.isConsumable()) 
			    {
		    		LivingEntity targetmob = Utils.getTargettedLivingEntity(event.getPlayer(), spell.getRange());
		    		if (targetmob != null)
		    		{
		    			item.useItemOnEntity(plugin, event.getPlayer(),item,targetmob,true);
		    			event.getPlayer().setItemInHand(null);
		    			event.getPlayer().updateInventory();
		    			return;
		    		} else {
		    			item.useItemOnEntity(plugin, event.getPlayer(),item,event.getPlayer(),true);
		    			event.getPlayer().setItemInHand(null);
		    			event.getPlayer().updateInventory();
		    			return;
		    		}
			    }
			    
			    // Only applies to spell effects
			    if (!itemstack.getType().equals(Material.ENCHANTED_BOOK))
			    {
			    	return;
			    }
		    	
		    	if (spell.isAASpell())
		    	{
		    		event.getPlayer().sendMessage("You require the correct AA to use this spell");
		    		return;
		    	}
		    	
		    	if (spell.getAllowedClasses().size() > 0)
		    	{
		    		if (getClassObj() == null)
		    		{
		    			event.getPlayer().sendMessage(ChatColor.GRAY + " * This item cannot be used by your profession");
		    			return;
		    		}
		    		
		    		boolean foundprofession = false;
		    		String professions = "";
		    		int foundlevel = 0;
		    		for (SoliniaSpellClass spellclass : spell.getAllowedClasses())
		    		{
		    			if (spellclass.getClassname().toUpperCase().equals(getClassObj().getName().toUpperCase()))
		    			{
		    				foundprofession = true;
		    				foundlevel = spellclass.getMinlevel();
		    				break;
		    			}
		    			professions += spellclass.getClassname() + " "; 
		    		}
		    		
		    		if (foundprofession == false)
		    		{
		    			event.getPlayer().sendMessage(ChatColor.GRAY + " * This item can only be used by " + professions);
		    			return;
		    		} else {
		    			if (foundlevel >  0)
		    			{
		    				Double level = (double) getLevel();
		    				if (level < foundlevel)
		    				{
		    					event.getPlayer().sendMessage(ChatColor.GRAY + " * This item requires level " + foundlevel);
				    			return;
		    				}
		    			}
		    		}
		    		
		    		
		    	}
		    	
		    	// Reroute action depending on target
		    	if(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) 
			    {
		    		LivingEntity targetmob = Utils.getTargettedLivingEntity(event.getPlayer(), spell.getRange());
		    		if (targetmob != null)
		    		{
		    			item.useItemOnEntity(plugin, event.getPlayer(),item,targetmob,false);
		    			return;
		    		} else {
		    			item.useItemOnEntity(plugin, event.getPlayer(),item,event.getPlayer(),false);
		    			return;
		    		}
			    }
		    	
		    	// TODO - Implement block functionality
		    	/*
		    	if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
		    	{
		    		item.useItemOnBlock(event.getPlayer(),item,event.getClickedBlock());
		    		return;
		    	}
		    	*/
			    
		    }
		} catch (CoreStateInitException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public UUID getInteraction() {
		return interaction;
	}

	@Override
	public void setInteraction(UUID interaction, ISoliniaNPC npc) {
		if (interaction == null)
		{
			this.interaction = interaction;
			this.getBukkitPlayer().sendMessage(ChatColor.GRAY + "* You are no longer interacting");
			return;
		}
		
		Entity e = Bukkit.getEntity(interaction);
		if (e == null)
			return;
		
		if (!(e instanceof LivingEntity))
			return;
		
		if (((Creature)e).getTarget() != null)
		{
			if (interaction != null)
			{
				this.getBukkitPlayer().sendMessage(ChatColor.GRAY + "* You are no longer interacting");
				interaction = null;
			}
			return;
		}
		
		if (Bukkit.getEntity(interaction) instanceof Wolf)
		{
			Wolf w = (Wolf)Bukkit.getEntity(interaction);
			if (w.getOwner() != null)
				return;
		}
		
		this.interaction = interaction;
		
		if (npc != null)
		{
			this.getBukkitPlayer().sendMessage(ChatColor.GRAY + "* You are now interacting with " + Bukkit.getEntity(interaction).getName() + " [" + npc.getId() + "] - Anything you type will be heared by the NPC and possibly responded to. Words in pink are trigger words you can type");

			if (npc.getMerchantid() > 0)
			{
				try
				{
					StateManager.getInstance().getEntityManager().getLivingEntity((LivingEntity)e).say("i have a [" + ChatColor.LIGHT_PURPLE + "SHOP" + ChatColor.AQUA + "] available if you are interested in buying or selling something", getBukkitPlayer());
				} catch (CoreStateInitException cse)
				{
					//
				}
			}
			
			for (ISoliniaNPCEventHandler eventHandler : npc.getEventHandlers())
			{
				if (!eventHandler.getInteractiontype().equals(InteractionType.ITEM))
					continue;
				
				// See if player has any items that are wanted
				int itemId = Integer.parseInt(eventHandler.getTriggerdata());
				if (itemId == 0)
					continue;
				
				if (Utils.getPlayerTotalCountOfItemId(getBukkitPlayer(),itemId) < 1)
					continue;
				
				try
				{
					ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(itemId);
					
					TextComponent tc = new TextComponent();
					tc.setText(ChatColor.YELLOW + "[QUEST] ");
					TextComponent tc2 = new TextComponent();
					tc2.setText(ChatColor.GRAY + "- Click here to give " + item.getDisplayname() + ChatColor.RESET);
					tc2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/npcgive " + itemId));
					tc.addExtra(tc2);
					getBukkitPlayer().spigot().sendMessage(tc);	
				} catch (CoreStateInitException eNotInitialised)
				{
					continue;
				}
			}
		}

	}

	@Override
	public String getCurrentChannel() {
		return currentChannel;
	}

	@Override	
	public void setCurrentChannel(String currentChannel) {
		this.currentChannel = currentChannel;
	}

	@Override
	public boolean understandsLanguage(String language) {
		if (getRace() != null)
			if (getRace().getName().toUpperCase().equals(language.toUpperCase()))
				return true;
		
		SoliniaPlayerSkill soliniaskill = getSkill(language);
        if (soliniaskill != null && soliniaskill.getValue() >= 100)
        {
            return true;
        }
		return false;
	}

	@Override
	public void tryImproveLanguage(String language) {
		if (getRace() != null)
			if (getRace().getName().toUpperCase().equals(language))
				return;
		
		if (getSkill(language).getValue() >= 100)
			return;
		
		tryIncreaseSkill(language,1);
	}

	@Override
	public ISoliniaGroup getGroup() {
		// TODO Auto-generated method stub
		return StateManager.getInstance().getGroupByMember(this.getBukkitPlayer().getUniqueId());
	}

	@Override
	public int getResist(SpellResistType type) {
		int resist = 25;
		// Get sum of all item resists
		{
			resist += getTotalResist(type);
		}
		return resist;
	}
	
	@Override
	public int getTotalResist(SpellResistType type) {
		int total = 0;
		// Get resist total from all active effects
		try
		{
			total += SoliniaLivingEntityAdapter.Adapt(getBukkitPlayer()).getResistsFromActiveEffects(type);
		} catch (CoreStateInitException e)
		{
			// Skip
		}
		
		for (ItemStack itemstack : getBukkitPlayer().getInventory().getArmorContents()) {
			if (itemstack == null)
				continue;

			if (itemstack.getEnchantmentLevel(Enchantment.OXYGEN) > 999
					&& !itemstack.getType().equals(Material.ENCHANTED_BOOK)) {
				try
				{
					ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(itemstack);
					Integer augmentationId = ItemStackUtils.getAugmentationItemId(itemstack);
					ISoliniaItem augItem = null;
					if (augmentationId != null && augmentationId != 0)
					{
						augItem = StateManager.getInstance().getConfigurationManager().getItem(augmentationId);
					}
					
					switch (type) {
					case RESIST_FIRE:
						if (item.getFireResist() > 0) {
							total += item.getFireResist();
						}
						if (augItem != null)
							if(augItem.getFireResist() > 0)
								total += item.getFireResist();
						break;
					case RESIST_COLD:
						if (item.getColdResist() > 0) {
							total += item.getColdResist();
						}
						if (augItem != null)
							if(augItem.getColdResist() > 0)
								total += item.getColdResist();
						break;
					case RESIST_MAGIC:
						if (item.getMagicResist() > 0) {
							total += item.getMagicResist();
						}
						if (augItem != null)
							if(augItem.getMagicResist() > 0)
								total += item.getMagicResist();
						break;
					case RESIST_POISON:
						if (item.getPoisonResist() > 0) {
							total += item.getPoisonResist();
						}
						if (augItem != null)
							if(augItem.getPoisonResist() > 0)
								total += item.getPoisonResist();
						break;
					case RESIST_DISEASE:
						if (item.getDiseaseResist() > 0) {
							total += item.getDiseaseResist();
						}
						if (augItem != null)
							if(augItem.getDiseaseResist() > 0)
								total += item.getDiseaseResist();
						break;
					default:
						break;
					}
				} catch (CoreStateInitException initException)
				{
					
				}

			}
		}
		
		if (total > 255)
			return 255;

		return total;
	}

	@Override
	public int getAapct() {
		return aapct;
	}

	@Override
	public void setAapct(int aapct) {
		this.aapct = aapct;
	}

	@Override
	public List<ISoliniaAARank> getBuyableAARanks() {
		List<ISoliniaAARank> buyableRanks = new ArrayList<ISoliniaAARank>();
		if (getLevel() < 20)
			return buyableRanks;
		
		try
		{
		for(ISoliniaAAAbility ability : StateManager.getInstance().getConfigurationManager().getAAAbilities())
		{
			for(ISoliniaAARank rank : ability.getRanks())
			{
				if (canPurchaseAlternateAdvancementRank(ability, rank))
				{
					buyableRanks.add(rank);
					break;
				}
			}
		}
		} catch (CoreStateInitException e)
		{
			// ignore
		}
		return buyableRanks;
		
	}

	@Override
	public boolean canPurchaseAlternateAdvancementRank(ISoliniaAAAbility ability, ISoliniaAARank rank) {
		if (rank.getAbilityid() == 0)
			return false;
		
		if (rank.getCost() == 0)
			return false;
		
		if(rank.getCost() > getAAPoints()) 
			return false;
		
		if(!canUseAlternateAdvancementRank(ability, rank)) {
			return false;
		}

		if(rank.getLevel_req() > getLevel()) {
			return false;
		}
		
		if (hasRank(rank))
			return false;

		if (!hasPreviousRanks(ability, rank))
			return false;
		
		if (!hasPrerequisites(ability, rank))
			return false;
		
		return true;
	}
	
	@Override
	public boolean hasPrerequisites(ISoliniaAAAbility ability, ISoliniaAARank rank) {
		for(SoliniaAAPrereq prereq : rank.getPrereqs())
		{
			if (!hasAAAbility(prereq.getAbilityid()))
				return false;
		}
			
		return true;
	}

	@Override
	public boolean hasAAAbility(int abilityid) {
		return aas.contains(abilityid);
	}

	@Override
	public boolean canUseAlternateAdvancementRank(ISoliniaAAAbility ability, ISoliniaAARank rank) {
		if (!ability.isEnabled())
			return false;
		
		if (!ability.canClassUseAbility(getClassObj()))
			return false;
			
		return true;
	}

	@Override
	public boolean hasRank(ISoliniaAARank rank) {
		return ranks.contains(rank.getId());
	}

	@Override
	public boolean hasPreviousRanks(ISoliniaAAAbility ability, ISoliniaAARank rank) {
		for (ISoliniaAARank curRank : ability.getRanks())
		{
			if (curRank.getPosition() >= rank.getPosition())
				continue;
			
			if (!hasRank(curRank))
				return false;
		}
		
		return true;
	}

	@Override
	public void purchaseAlternateAdvancementRank(ISoliniaAAAbility ability, ISoliniaAARank rank) {
		if(!canPurchaseAlternateAdvancementRank(ability, rank))
			return;
		
		setAAPoints(getAAPoints() - rank.getCost());
		ranks.add(rank.getId());
		if (!aas.contains(rank.getAbilityid()))
			aas.add(rank.getAbilityid());
		getBukkitPlayer().sendMessage("You have gained the AA " + ability.getName() + " (rank " + rank.getPosition() + ")");
	}

	@Override
	public boolean canDodge() {
		if (getClassObj() == null)
			return false;
		
		if (getClassObj().canDodge() == false)
			return false;
		
		if (getClassObj().getDodgelevel() > getLevel())
			return false;
		
		return true;
	}

	@Override
	public boolean canRiposte() {
		if (getClassObj() == null)
			return false;
		
		if (getClassObj().canRiposte() == false)
			return false;
		
		if (getClassObj().getRipostelevel() > getLevel())
			return false;
		
		return true;
	}

	@Override
	public boolean canDoubleAttack() {
		if (getClassObj() == null)
			return false;

		if (getClassObj().canDoubleAttack() == false)
			return false;
		
		if (getClassObj().getDoubleattacklevel() > getLevel())
			return false;
		
		return true;
	}
	
	@Override 
	public boolean getDodgeCheck()
	{
		if (canDodge() == false)
			return false;
		
		int chance = getSkill("DODGE").getValue();
		chance += 100;
		chance /= 40;

		return Utils.RandomBetween(1, 500) <= chance;
	}
	
	@Override 
	public boolean getRiposteCheck()
	{
		if (canRiposte() == false)
			return false;
		
		int chance = getSkill("RIPOSTE").getValue();
		chance += 100;
		chance /= 50;

		return Utils.RandomBetween(1, 500) <= chance;
	}
	
	@Override 
	public boolean getDoubleAttackCheck()
	{
		if (canDoubleAttack() == false)
			return false;
		
		int chance = getSkill("DOUBLEATTACK").getValue();
		if (getLevel() > 35)
		{
			chance += getLevel();
		}
		chance /= 5;

		return Utils.RandomBetween(1, 500) <= chance;
	}

	@Override
	public boolean getSafefallCheck() {
		if (canSafefall() == false)
			return false;
		
		int chance = getSkill("SAFEFALL").getValue();
		chance += 10;
		chance += getLevel();
		
		return Utils.RandomBetween(1, 500) <= chance;
	}

	private boolean canSafefall() {
		if (getClassObj() == null)
			return false;
		
		if (getClassObj().canSafeFall() == false)
			return false;
		
		if (getClassObj().getSafefalllevel() > getLevel())
			return false;
		
		return true;
	}

	@Override
	public List<PlayerFactionEntry> getFactionEntries() {
		return factionEntries;
	}

	@Override
	public void setFactionEntries(List<PlayerFactionEntry> factionEntries) {
		this.factionEntries = factionEntries;
	}
	
	@Override
	public PlayerFactionEntry getFactionEntry(int factionId) {
		for(PlayerFactionEntry entry : getFactionEntries())
		{
			if (entry.getFactionId() == factionId)
				return entry;
		}
		
		return null;
	}
	
	@Override
	public PlayerFactionEntry createPlayerFactionEntry(int factionId)
	{
		PlayerFactionEntry entry = new PlayerFactionEntry();
		entry.setFactionId(factionId);
		entry.setValue(0);
		getFactionEntries().add(entry);
		return getFactionEntry(factionId);
	}
	
	@Override
	public void increaseFactionStanding(int factionId, int amount) {
		if (factionId == 0)
			return;
		
		if (amount == 0)
			return;
		
		PlayerFactionEntry playerFactionEntry = getFactionEntry(factionId);
		if (playerFactionEntry == null)
			playerFactionEntry = createPlayerFactionEntry(factionId);
		
		// Never handle these special faction types		
		if (playerFactionEntry.getFaction().getName().toUpperCase().equals("NEUTRAL") || playerFactionEntry.getFaction().getName().toUpperCase().equals("KOS"))
			return;
		
		int newValue = playerFactionEntry.getValue() + amount;
		boolean hitCap = false;
		if (newValue > 1500)
		{
			newValue = 1500;
			hitCap = true;
			
			if (!playerFactionEntry.getFaction().getAllyGrantsTitle().equals(""))
			{
				grantTitle(playerFactionEntry.getFaction().getAllyGrantsTitle());
			}
			
			getBukkitPlayer().sendMessage(ChatColor.GRAY + "* Your faction standing with " + playerFactionEntry.getFaction().getName().toLowerCase() + " could not possibly get any better");
		}
		
		if (!hitCap)
		{
			getBukkitPlayer().sendMessage(ChatColor.GRAY + "* Your faction standing with " + playerFactionEntry.getFaction().getName().toLowerCase() + " got better");
		}
		
		playerFactionEntry.setValue(newValue);
	}

	@Override
	public void decreaseFactionStanding(int factionId, int amount) {
		if (factionId == 0)
			return;
		
		if (amount == 0)
			return;
		
		PlayerFactionEntry playerFactionEntry = getFactionEntry(factionId);
		if (playerFactionEntry == null)
			playerFactionEntry = createPlayerFactionEntry(factionId);
		
		// Never handle these special faction types		
		if (playerFactionEntry.getFaction().getName().toUpperCase().equals("NEUTRAL") || playerFactionEntry.getFaction().getName().toUpperCase().equals("KOS"))
			return;
		
		int newValue = playerFactionEntry.getValue() - amount;
		boolean hitCap = false;
		if (newValue < -1500)
		{
			newValue = -1500;
			hitCap = true;
			
			if (!playerFactionEntry.getFaction().getScowlsGrantsTitle().equals(""))
			{
				grantTitle(playerFactionEntry.getFaction().getScowlsGrantsTitle());
			}
			
			getBukkitPlayer().sendMessage(ChatColor.GRAY + "* Your faction standing with " + playerFactionEntry.getFaction().getName().toLowerCase() + " could not possibly get any worse");
		}
		
		if (!hitCap)
		{
			getBukkitPlayer().sendMessage(ChatColor.GRAY + "* Your faction standing with " + playerFactionEntry.getFaction().getName().toLowerCase() + " got worse");
		}
		
		playerFactionEntry.setValue(newValue);
	}

	@Override
	public void ignorePlayer(Player player) {
		if(ignoredPlayers.contains(player.getUniqueId()))
		{
			ignoredPlayers.remove(player.getUniqueId());
		} else {
			ignoredPlayers.add(player.getUniqueId());
		}
	}

	@Override
	public List<String> getAvailableTitles() {
		return availableTitles;
	}

	@Override
	public void setAvailableTitles(List<String> availableTitles) {
		this.availableTitles = availableTitles;
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
	public boolean isMezzed()
	{
		try
		{
			Timestamp mezExpiry = StateManager.getInstance().getEntityManager()
					.getMezzed((LivingEntity) getBukkitPlayer());
	
			if (mezExpiry != null) {
				return true;
			}
		} catch (CoreStateInitException e)
		{
			return false;
		}
		
		return false;
	}

	@Override
	public List<PlayerQuest> getPlayerQuests() {
		return playerQuests;
	}

	@Override
	public void setPlayerQuests(List<PlayerQuest> playerQuests) {
		this.playerQuests = playerQuests;
	}

	@Override
	public void addPlayerQuest(int questId) {
		
		PlayerQuest quest = new PlayerQuest();
		quest.setComplete(false);
		quest.setQuestId(questId);
		this.getPlayerQuests().add(quest);
		this.getBukkitPlayer().sendMessage(ChatColor.YELLOW + " * You have received a new Quest [" + quest.getQuest().getName() + "]!");
	}

	@Override
	public List<String> getPlayerQuestFlags() {
		// TODO Auto-generated method stub
		return playerQuestFlags;
	}
	
	@Override
	public void setPlayerQuestFlags(List<String> playerQuestFlags) {
		this.playerQuestFlags = playerQuestFlags;
	}

	@Override
	public void addPlayerQuestFlag(String questFlag) {
		playerQuestFlags.add(questFlag);
		this.getBukkitPlayer().sendMessage(ChatColor.YELLOW + " * You have received a new Quest Flag!");
	}
}
