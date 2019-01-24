package com.solinia.solinia.Models;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.codec.binary.Base64;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
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
import com.solinia.solinia.Utils.SpellTargetType;
import com.solinia.solinia.Utils.Utils;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class SoliniaPlayer implements ISoliniaPlayer {

	private static final long serialVersionUID = 9075039437399478391L;
	private UUID uuid;
	private UUID characterId = UUID.randomUUID();
	private UUID motherId;
	private UUID spouseId;
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
	private String base64InventoryContents = "";
	private String base64ArmorContents = "";
	
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
	private String specialisation = "";
	private boolean vampire = false;
	private boolean main = true;
	private int inspiration = 0;
	private Timestamp experienceBonusExpires;
	private boolean oocEnabled = true;
	private boolean songsEnabled = true;
	private String bindPoint;
	private int fingersItem = 0;
	private int shouldersItem = 0;
	private int neckItem = 0;
	private int earsItem = 0;
	private int forearmsItem = 0;
	private int armsItem = 0;
	private int handsItem = 0;
	private int waistItem = 0;
	private Personality personality = new Personality();
	
	private int lastX = 0;
	private int lastY = 0;
	private int lastZ = 0;
	
	private String fingersItemInstance = "";
	private String shouldersItemInstance = "";
	private String neckItemInstance = "";
	private String earsItemInstance = "";
	private String forearmsItemInstance = "";
	private String armsItemInstance = "";
	private String handsItemInstance = "";
	private String waistItemInstance = "";

	private List<Integer> spellBookItems = new ArrayList<Integer>();
	private ConcurrentHashMap<String, Integer> monthlyVote = new ConcurrentHashMap<String, Integer>();
	private ConcurrentHashMap<Integer, SoliniaReagent> reagentsPouch = new ConcurrentHashMap<Integer, SoliniaReagent>();
	private boolean glowTargetting = true;
	private Double pendingXp = 0d;
	private boolean showDiscord = true;
	private int oathId = 0;
	
	@Override
	public List<UUID> getIgnoredPlayers() {
		return ignoredPlayers;
	}

	@Override
	public boolean hasIgnored(UUID uuid) {
		return ignoredPlayers.contains(uuid);
	}
	
	@Override
	public Oath getOath()
	{
		try
		{
			return StateManager.getInstance().getConfigurationManager().getOath(this.getOathId());
		} catch (CoreStateInitException e)
		{
			return null;
		}
	}
	
	@Override
	public LivingEntity getTarget()
	{
		try {
			return StateManager.getInstance().getEntityManager().getEntityTarget(getBukkitPlayer());
		} catch (CoreStateInitException e) {
			return null;
		}
	}
	
	@Override
	public void setLastLocation(Location location)
	{
		this.lastX = location.getBlockX();
		this.lastY = location.getBlockY();
		this.lastZ = location.getBlockZ();
	}
	
	@Override
	public Location getLastLocation()
	{
		return new Location(this.getBukkitPlayer().getWorld(), this.lastX, this.lastY, this.lastZ);
	}

	@Override
	public void setIgnoredPlayers(List<UUID> ignoredPlayers) {
		this.ignoredPlayers = ignoredPlayers;
	}
	
	@Override
	public SoliniaWorld getWorld()
	{
		try
		{
			return StateManager.getInstance().getConfigurationManager().getWorld(getBukkitPlayer().getWorld().getName().toUpperCase());
		} catch (CoreStateInitException e)
		{
			return null;
		}
	}

	@Override
	public UUID getUUID() {
		return uuid;
	}

	@Override
	public boolean grantTitle(String title) {
		if (getAvailableTitles().contains(title))
			return false;

		getAvailableTitles().add(title);
		getBukkitPlayer().sendMessage(
				ChatColor.YELLOW + "* You have earned the title: " + title + ChatColor.RESET + " See /settitle");
		return true;
	}
	
	@Override
	public Timestamp getLastLogin()
	{
		return new Timestamp(Bukkit.getOfflinePlayer(this.getUUID()).getLastPlayed());
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
	public void updateDisplayName() {
		if (getBukkitPlayer() != null) {
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
	public Player getBukkitPlayer() {
		Player player = Bukkit.getPlayer(uuid);
		return player;
	}

	@Override
	public String getFullName() {
		String displayName = forename;
		if (lastname != null && !lastname.equals(""))
			displayName = forename + "_" + lastname;

		return displayName;
	}

	@Override
	public String getFullNameWithTitle() {
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
	public void setMana(int mana) {
		this.mana = mana;
		ScoreboardUtils.UpdateScoreboard(this.getBukkitPlayer(), mana);
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
	public int getLevel() {
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
	public ISoliniaRace getRace() {
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
	public ISoliniaClass getClassObj() {
		try {
			return StateManager.getInstance().getConfigurationManager().getClassObj(getClassId());
		} catch (CoreStateInitException e) {
			return null;
		}
	}

	@Override
	public void updateMaxHp() {
		if (getBukkitPlayer() != null && getExperience() != null) {
			try {
				ISoliniaLivingEntity solentity = SoliniaLivingEntityAdapter.Adapt(getBukkitPlayer());
				double calculatedhp = solentity.getMaxHP();
				
				AttributeInstance healthAttribute = getBukkitPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH);
				healthAttribute.setBaseValue(calculatedhp);
				
				getBukkitPlayer().setHealthScaled(true);
				getBukkitPlayer().setHealthScale(40D);
				
				ScoreboardUtils.UpdateGroupScoreboardForEveryone(getBukkitPlayer().getUniqueId(), getGroup());
			} catch (CoreStateInitException e) {

			}
		}
	}

	@Override
	public void increasePlayerExperience(Double experience, boolean applyModifiers) {
		if (!isAAOn()) {
			increasePlayerNormalExperience(experience,applyModifiers);
		} else {
			int normalpct = 100 - getAapct();
			if (normalpct > 0) {
				Double normalexperience = (experience / 100) * normalpct;
				increasePlayerNormalExperience(normalexperience,applyModifiers);
			}

			Double aaexperience = (experience / 100) * getAapct();
			increasePlayerAAExperience(aaexperience,applyModifiers);
		}
	}

	@Override
	public boolean isAAOn() {
		if (this.getAapct() > 0)
			return true;

		return false;
	}

	@Override
	public void increasePlayerNormalExperience(Double experience, boolean applyModifiers) {
		
		double classxpmodifier = 0;
		boolean modified = false;
		if (applyModifiers)
		{
			classxpmodifier = Utils.getClassXPModifier(getClassObj());
			experience = experience * (classxpmodifier / 100);

			
			
			double modifier = StateManager.getInstance().getWorldPerkXPModifier();
			if (getExperienceBonusExpires() != null) {
				LocalDateTime datetime = LocalDateTime.now();
				Timestamp nowtimestamp = Timestamp.valueOf(datetime);
				Timestamp expiretimestamp = getExperienceBonusExpires();
	
				if (expiretimestamp != null) {
					if (!nowtimestamp.after(expiretimestamp)) {
						modifier += 100;
					}
				}
			}
	
			if (isInHotzone() == true) {
				modifier += 100;
			}
	
			if (modifier > 100) {
				modified = true;
			}
			experience = experience * (modifier / 100);
		}

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
			// System.out.println("XP: " + experience);
			currentexperience = Utils.getExperienceRequirementForLevel(Utils.getMaxLevel());
		} else {
			// System.out.println("XP: " + experience);
			currentexperience = currentexperience + experience;
		}

		setExperience(currentexperience, experience, modified);
	}

	@Override
	public SoliniaZone getZone() {
		try {
			for (SoliniaZone zone : StateManager.getInstance().getConfigurationManager().getZones()) {
				if (this.getBukkitPlayer().getLocation().distance(
						new Location(this.getBukkitPlayer().getWorld(), zone.getX(), zone.getY(), zone.getZ())) < zone
								.getSize())
					return zone;
			}
		} catch (CoreStateInitException e) {

		}

		return null;
	}

	@Override
	public boolean isInHotzone() {
		for (SoliniaZone zone : StateManager.getInstance().getCurrentHotzones()) {
			if (this.getBukkitPlayer().getLocation().distance(
					new Location(this.getBukkitPlayer().getWorld(), zone.getX(), zone.getY(), zone.getZ())) < zone
							.getSize())
				return true;
		}

		return false;
	}

	@Override
	public boolean isInZone(SoliniaZone zone) {
		if (zone == null)
			return false;
		
		if (this.getBukkitPlayer().getLocation().distance(
				new Location(this.getBukkitPlayer().getWorld(), zone.getX(), zone.getY(), zone.getZ())) < zone
						.getSize())
			return true;

		return false;
	}

	@Override
	public SoliniaZone getFirstZone() {
		try {
			for (SoliniaZone zone : StateManager.getInstance().getConfigurationManager().getZones()) {
				if (this.getBukkitPlayer().getLocation().distance(
						new Location(this.getBukkitPlayer().getWorld(), zone.getX(), zone.getY(), zone.getZ())) < zone
								.getSize())
					return zone;
			}
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public void reducePlayerNormalExperience(Double experience) {

		Double originalexperience = getExperience();
		Double experiencechange = -experience;
		Double newexperience = originalexperience - experience;
		if (newexperience < 0d) {
			newexperience = 0d;
			experiencechange = newexperience - originalexperience;
		}

		double clevel = Utils.getLevelFromExperience(originalexperience);
		double nlevel = Utils.getLevelFromExperience(newexperience);

		if (nlevel < (clevel - 1)) {
			// xp loss is way too high, drop to proper amount
			System.out.println("XP loss was dropped for being way too high");

			newexperience = Utils.getExperienceRequirementForLevel((int) clevel - 1);
			experiencechange = newexperience - originalexperience;
		}

		setExperience(newexperience, experiencechange, false);
	}

	@Override
	public void dropResurrectionItem(int experienceamount) {
		if (experienceamount < 1)
			return;

		LocalDateTime datetime = LocalDateTime.now();
		Timestamp currenttimestamp = Timestamp.valueOf(datetime);

		SoliniaResurrectionItem resurrectionitem = new SoliniaResurrectionItem(this, experienceamount,
				currenttimestamp);
		ItemStack itemstack = resurrectionitem.AsItemStack();
		getBukkitPlayer().getWorld().dropItem(getBukkitPlayer().getLocation(), itemstack);
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
			getBukkitPlayer()
					.sendMessage(ChatColor.YELLOW + "* You gain experience (" + ipercenttolevel + "% into level)");
			getBukkitPlayer().sendMessage(ChatColor.GRAY + "Exp Gained: " + changeamount);
			if (modified == true)
				getBukkitPlayer().sendMessage(ChatColor.YELLOW
						+ "* You were given bonus XP from a player donation perk/hotzone or potion! (See /perks && /hotzones)");
		}

		if (changeamount < 0) {
			getBukkitPlayer()
					.sendMessage(ChatColor.RED + "* You lost experience (" + ipercenttolevel + "% into level)");
		}
		if (Double.compare(newlevel, level) > 0) {
			String classname = "Hero";
			if (getClassObj() != null)
				classname = getClassObj().getName();

			StateManager.getInstance().getChannelManager().sendToDiscordMC(null,
					StateManager.getInstance().getChannelManager().getDiscordMainChannelId(),
					getFullName() + " has reached new heights as a level " + (int) Math.floor(newlevel) + " "
							+ classname.toLowerCase() + "!");
			getBukkitPlayer().sendMessage(ChatColor.DARK_PURPLE + "* You gained a level (" + newlevel + ")!");

			if (newlevel < 6)
				getBukkitPlayer().sendMessage(ChatColor.GRAY
						+ "[Hint] Bored of this class? You rename this player and create a new class with the /character command");

			getBukkitPlayer().getWorld().playEffect(getBukkitPlayer().getLocation(), Effect.FIREWORK_SHOOT, 1);

			// Title rewards
			if (newlevel >= 10) {
				if (!getAvailableTitles().contains("the Apprentice")) {
					grantTitle("the Apprentice");
				}
			}

			if (newlevel >= 20) {
				if (!getAvailableTitles().contains("the Journeyman")) {
					grantTitle("the Journeyman");
				}
			}

			if (newlevel >= 30) {
				if (!getAvailableTitles().contains("the Expert")) {
					grantTitle("the Expert");
				}
			}

			if (newlevel >= 40) {
				if (!getAvailableTitles().contains("the Hero")) {
					grantTitle("the Hero");
				}
			}

			if (newlevel >= 50) {
				if (!getAvailableTitles().contains("the Legend")) {
					grantTitle("the Legend");
				}
			}

			if (newlevel >= 51) {
				if (getClassObj() != null)
					if (getClassObj().getLevel51Title() != null && !getClassObj().getLevel51Title().equals(""))
						if (!getAvailableTitles().contains(getClassObj().getLevel51Title())) {
							grantTitle(getClassObj().getLevel51Title());
						}
			}

			if (newlevel >= 55) {
				if (getClassObj() != null)
					if (getClassObj().getLevel55Title() != null && !getClassObj().getLevel55Title().equals(""))
						if (!getAvailableTitles().contains(getClassObj().getLevel55Title())) {
							grantTitle(getClassObj().getLevel55Title());
						}
			}

			if (newlevel >= 60) {
				if (getClassObj() != null)
					if (getClassObj().getLevel60Title() != null && !getClassObj().getLevel60Title().equals(""))
						if (!getAvailableTitles().contains(getClassObj().getLevel60Title())) {
							grantTitle(getClassObj().getLevel60Title());
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
	public void increasePlayerAAExperience(Double experience, boolean applyModifiers) {

		boolean modified = false;
		
		if (applyModifiers)
		{
			double modifier = StateManager.getInstance().getWorldPerkXPModifier();
			if (getExperienceBonusExpires() != null) {
				LocalDateTime datetime = LocalDateTime.now();
				Timestamp nowtimestamp = Timestamp.valueOf(datetime);
				Timestamp expiretimestamp = getExperienceBonusExpires();
	
				if (expiretimestamp != null) {
					if (!nowtimestamp.after(expiretimestamp)) {
						modifier += 100;
					}
				}
			}
	
			if (isInHotzone() == true) {
				modifier += 100;
			}
	
			if (modifier > 100) {
				modified = true;
			}
			experience = experience * (modifier / 100);
		}

		// Cap at max just under a quarter of an AA experience point
		if (experience > Utils.getMaxAAXP()) {
			experience = Utils.getMaxAAXP();
		}

		// System.out.println("AA XP: " + experience);

		Double currentaaexperience = getAAExperience();

		currentaaexperience = currentaaexperience + experience;

		setAAExperience(currentaaexperience, modified, experience);
	}

	private void setAAExperience(Double aaexperience, Boolean modified, Double amountincreased) {
		// One AA level is always equal to the same experience as is needed for
		// 39 to level 40
		// AA xp should never be above 2313441000

		if (getClassObj() == null)
			return;

		// Max limit on AA points right now is 100
		if (getAAPoints() > Utils.getMaxUnspentAAPoints()) {
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

		getBukkitPlayer()
				.sendMessage(ChatColor.YELLOW + "* You gain alternate experience (" + ipercenttoaa + "% into AA)!");
		getBukkitPlayer().sendMessage(ChatColor.GRAY + "AAExp Gained: " + amountincreased);
		if (modified == true)
			getBukkitPlayer().sendMessage(ChatColor.YELLOW
					+ "* You were given bonus XP from a player donation perk/hotzone or potion! (See /perks && /hotzones)");

		if (givenaapoint) {
			getBukkitPlayer().sendMessage(ChatColor.YELLOW + "* You gained an Alternate Experience Point!");
		}
	}

	@Override
	public void giveMoney(int i) {
		try {
			StateManager.getInstance().giveMoney(getBukkitPlayer(), i);
		} catch (Exception e) {
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
		return Utils.getSkillCap(skillName, getClassObj(), getLevel(), getSpecialisation());
	}

	@Override
	public List<SoliniaPlayerSkill> getSkills() {
		// TODO Auto-generated method stub
		return this.skills;
	}

	@Override
	public void emote(String string, boolean isBardSongFilterable) {
		StateManager.getInstance().getChannelManager().sendToLocalChannel(this, ChatColor.AQUA + "* " + string, isBardSongFilterable, getBukkitPlayer().getInventory().getItemInMainHand());
	}

	@Override
	public void ooc(String string) {
		if (getLanguage() == null || getLanguage().equals("UNKNOWN")) {
			if (getRace() == null) {
				getBukkitPlayer().sendMessage("You cannot speak until you set a race /setrace");
				return;
			} else {
				setLanguage(getRace().getName().toUpperCase());
			}
		}
		StateManager.getInstance().getChannelManager().sendToGlobalChannelDecorated(this, string, getBukkitPlayer().getInventory().getItemInMainHand());
	}

	@Override
	public void say(String string) {
		if (getLanguage() == null || getLanguage().equals("UNKNOWN")) {
			if (getRace() == null) {
				getBukkitPlayer().sendMessage("You cannot speak until you set a race /setrace");
				return;
			} else {
				setLanguage(getRace().getName().toUpperCase());
			}
		}
		StateManager.getInstance().getChannelManager().sendToLocalChannelDecorated(this, string, string, getBukkitPlayer().getInventory().getItemInMainHand());
	}
	
	@Override
	public void whisper(String string) {
		if (getLanguage() == null || getLanguage().equals("UNKNOWN")) {
			if (getRace() == null) {
				getBukkitPlayer().sendMessage("You cannot speak until you set a race /setrace");
				return;
			} else {
				setLanguage(getRace().getName().toUpperCase());
			}
		}
		StateManager.getInstance().getChannelManager().sendToWhisperChannelDecorated(this, string, string, getBukkitPlayer().getInventory().getItemInMainHand());
	}

	
	@Override
	public void shout(String string) {
		if (getLanguage() == null || getLanguage().equals("UNKNOWN")) {
			if (getRace() == null) {
				getBukkitPlayer().sendMessage("You cannot speak until you set a race /setrace");
				return;
			} else {
				setLanguage(getRace().getName().toUpperCase());
			}
		}
		StateManager.getInstance().getChannelManager().sendToShoutChannelDecorated(this, string, string, getBukkitPlayer().getInventory().getItemInMainHand());
	}


	@Override
	public SoliniaPlayerSkill getSkill(String skillname) {
		if (!Utils.isValidSkill(skillname))
			return null;

		for (SoliniaPlayerSkill skill : this.skills) {
			if (skill.getSkillName().toUpperCase().equals(skillname.toUpperCase()))
				return skill;
		}

		// If we got this far the skill doesn't exist, create it with 0
		SoliniaPlayerSkill skill = new SoliniaPlayerSkill(skillname.toUpperCase(), 0);
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

		if (getSpecialisation() != null && !getSpecialisation().equals("")) {
			if (!skillname.toUpperCase().equals(getSpecialisation().toUpperCase()))
				return;

			skill = getSkill("SPECIALISE" + skillname.toUpperCase());

			currentskill = 0;
			if (skill != null) {
				currentskill = skill.getValue();
			}

			skillcap = getSkillCap("SPECIALISE" + skillname.toUpperCase());
			if ((currentskill + skillupamount) > skillcap) {
				return;
			}

			chance = 10 + ((252 - currentskill) / 20);
			if (chance < 1) {
				chance = 1;
			}

			randomInt = r.nextInt(100) + 1;
			if (randomInt < chance) {

				setSkill("SPECIALISE" + skillname.toUpperCase(), currentskill + skillupamount);
			}
		}

	}

	@Override
	public void setSkill(String skillname, int value) {
		if (value > Integer.MAX_VALUE)
			return;

		// max skill point
		if (value > Utils.getMaxSkillValue())
			return;

		skillname = skillname.toUpperCase();

		if (this.skills == null)
			this.skills = new ArrayList<SoliniaPlayerSkill>();

		boolean updated = false;

		for (SoliniaPlayerSkill skill : this.skills) {
			if (skill.getSkillName().toUpperCase().equals(skillname.toUpperCase())) {
				skill.setValue(value);
				updated = true;
				getBukkitPlayer()
						.sendMessage(ChatColor.YELLOW + "* You get better at " + skillname + " (" + value + ")");
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
		try {
			ISoliniaLivingEntity solentity = SoliniaLivingEntityAdapter.Adapt(this.getBukkitPlayer());
			maxmp = solentity.getMaxMP();
		} catch (CoreStateInitException e) {
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
	public void tryCastSpell(ISoliniaSpell spell, boolean useMana, boolean useReagents, boolean ignoreProfessionAndLevel)
	{
		if (spell == null)
			return;
		
		try
		{
			// Some spells auto target self, if they don't have a target try to do that
			if (getTarget() == null) {
				if (!tryFixSpellTarget(spell))
				{
					getBukkitPlayer().sendMessage(
							"* You must select a target (shift+left click with spell or use /target for group or shift-f for self");
					return;
				}
			}

			if (getTarget() == null)
			{
				getBukkitPlayer().sendMessage("* You must select a target (shift+left click with consumable or use /target for group or shift-f for self");
				return;
			}
	
			// Reroute action depending on target
			ISoliniaLivingEntity solentity = SoliniaLivingEntityAdapter.Adapt((LivingEntity) getBukkitPlayer());
			if (useMana && spell.getActSpellCost(solentity) > SoliniaPlayerAdapter.Adapt(getBukkitPlayer()).getMana()) {
				getBukkitPlayer().sendMessage(
						ChatColor.GRAY + "Insufficient Mana  [E]");
				return;
			}
			
			if (!spell.getRequiresPermissionNode().equals(""))
			{
				if (!getBukkitPlayer().hasPermission(spell.getRequiresPermissionNode()))
				{
					getBukkitPlayer().sendMessage("This requires a permission node you do not have");
					return;
				}
			}
	
			if (useMana && StateManager.getInstance().getEntityManager().getEntitySpellCooldown(getBukkitPlayer(),
					spell.getId()) != null) {
				LocalDateTime datetime = LocalDateTime.now();
				Timestamp nowtimestamp = Timestamp.valueOf(datetime);
				Timestamp expiretimestamp = StateManager.getInstance().getEntityManager()
						.getEntitySpellCooldown(getBukkitPlayer(), spell.getId());
	
				if (expiretimestamp != null)
					if (!nowtimestamp.after(expiretimestamp)) {
						getBukkitPlayer()
								.sendMessage("You do not have enough willpower to cast " + spell.getName()
										+ " (Wait: "
										+ ((expiretimestamp.getTime() - nowtimestamp.getTime()) / 1000) + "s");
						return;
					}
			}
	
			startCasting(spell, getBukkitPlayer(), useMana, useReagents, ignoreProfessionAndLevel);
		} catch (CoreStateInitException e)
		{
			
		}
	}
	
	@Override
	public void tryCastFromSpellbook(ISoliniaItem item) {
		if (item == null) {
			return;
		}

		if (item.getAbilityid() < 1) {
			return;
		}
		
		// Spellbooks dont contain consumables
		if (item.isConsumable()) {
			return;
		}
		
		try
		{
			
			
			tryCastSpell(StateManager.getInstance().getConfigurationManager().getSpell(item.getAbilityid()), true, true, false);
		} catch (CoreStateInitException e)
		{
			
		}
	}
	
	// TODO REFACTOR ALL THIS TO USE TRYCASTSPELL
	@Override
	public void tryCastFromItemInSlot(int slotId) {
		try
		{
			Utils.DebugLog("SoliniaPlayer", "tryCastFromItemInSlot", this.getBukkitPlayer().getName(), "Trying to cast from item in slot: " + slotId);
			
			ItemStack itemstack = this.getBukkitPlayer().getInventory().getItem(slotId);
			Utils.DebugLog("SoliniaPlayer", "tryCastFromItemInSlot", this.getBukkitPlayer().getName(), "Item in slot: " + slotId + " null status: " + (itemstack == null));
			if (itemstack == null || itemstack.getType().equals(Material.AIR))
				return;
			
			if ((!Utils.IsSoliniaItem(itemstack)))
				return;
			
			if (ItemStackUtils.isPotion(itemstack))
			{
				return;
			}

			// We have our custom item id, lets check it exists
			ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(itemstack);
			Utils.DebugLog("SoliniaPlayer", "tryCastFromItemInSlot", this.getBukkitPlayer().getName(), "SoliniaItem in slot: " + slotId + " null status: " + (item == null));

			if (item == null) {
				return;
			}

			if (item.getAbilityid() < 1) {
				return;
			}
			
			Utils.DebugLog("SoliniaPlayer", "tryCastFromItemInSlot", this.getBukkitPlayer().getName(), "SoliniaItem in slot: " + slotId + " target status: " + (getTarget() == null));

			// Some spells auto target self, if they don't have a target try to do that
			if (getTarget() == null) {
				if (item.getAbilityid() > 0) {
					ISoliniaSpell spell = StateManager.getInstance().getConfigurationManager()
							.getSpell(item.getAbilityid());
					if (spell != null) {
						if (!tryFixSpellTarget(spell))
						{
							getBukkitPlayer().sendMessage(
									"* You must select a target (shift+left click with spell or use /ts for group or shift-f for self (tryCastFromItemInSlot-tryFixSpellTarget)");
							return;
						}
					}
				}
			}

			if (getTarget() == null)
			{
				getBukkitPlayer().sendMessage("* You must select a target (shift+left click with consumable or use /target for group or shift-f for self (tryCastFromItemInSlot)");
				return;
			}
			
			ISoliniaSpell spell = StateManager.getInstance().getConfigurationManager().getSpell(item.getAbilityid());

			Utils.DebugLog("SoliniaPlayer", "tryCastFromItemInSlot", this.getBukkitPlayer().getName(), "SoliniaItem in slot: " + slotId + " consumable status: " + item.isConsumable());

			// Only applies to consumable items
			if (item.isConsumable()) {
	
				int newAmount = itemstack.getAmount() -1;
				Utils.DebugLog("SoliniaPlayer", "tryCastFromItemInSlot", this.getBukkitPlayer().getName(), "SoliniaItem in slot: " + slotId + " using consumable item");
				item.useItemOnEntity(getBukkitPlayer(), getTarget(), true);
				if (newAmount < 1)
				{
					getBukkitPlayer().getInventory().setItem(slotId, null);
					getBukkitPlayer().updateInventory();
					return;
				} else {
					itemstack.setAmount(newAmount);
					getBukkitPlayer().getInventory().setItem(slotId, null);
					getBukkitPlayer().updateInventory();
					return;
				}
			}
	
			// Only applies to non-consumable items
			if (!item.isConsumable() && !itemstack.getType().equals(Material.ENCHANTED_BOOK)) {
				Utils.DebugLog("SoliniaPlayer", "tryCastFromItemInSlot", this.getBukkitPlayer().getName(), "SoliniaItem in slot: " + slotId + " using non consumable item");
				item.useItemOnEntity(getBukkitPlayer(), getTarget(), true);
				return;
			}
	
			// Only applies to spell effects
			if (!itemstack.getType().equals(Material.ENCHANTED_BOOK)) {
				return;
			}
	
			// Reroute action depending on target
			ISoliniaLivingEntity solentity = SoliniaLivingEntityAdapter.Adapt((LivingEntity) getBukkitPlayer());
			if (!item.isConsumable() && spell.getActSpellCost(solentity) > SoliniaPlayerAdapter.Adapt(getBukkitPlayer()).getMana()) {
				getBukkitPlayer().sendMessage(
						ChatColor.GRAY + "Insufficient Mana  [E]");
				return;
			}
			
			if (!spell.getRequiresPermissionNode().equals(""))
			{
				if (!getBukkitPlayer().hasPermission(spell.getRequiresPermissionNode()))
				{
					getBukkitPlayer().sendMessage("This requires a permission node you do not have");
					return;
				}
			}
	
			if (!item.isConsumable() && StateManager.getInstance().getEntityManager().getEntitySpellCooldown(getBukkitPlayer(),
					spell.getId()) != null) {
				LocalDateTime datetime = LocalDateTime.now();
				Timestamp nowtimestamp = Timestamp.valueOf(datetime);
				Timestamp expiretimestamp = StateManager.getInstance().getEntityManager()
						.getEntitySpellCooldown(getBukkitPlayer(), spell.getId());
	
				if (expiretimestamp != null)
					if (!nowtimestamp.after(expiretimestamp)) {
						getBukkitPlayer()
								.sendMessage("You do not have enough willpower to cast " + spell.getName()
										+ " (Wait: "
										+ ((expiretimestamp.getTime() - nowtimestamp.getTime()) / 1000) + "s");
						return;
					}
			}
	
			startCasting(spell, getBukkitPlayer(), !item.isConsumable(), !item.isConsumable(), false);
		} catch (CoreStateInitException e)
		{
			
		}
	}
	
	@Override
	public void tryThrowItemInMainHand()
	{
		try
		{
			ItemStack itemstack = this.getBukkitPlayer().getEquipment().getItemInMainHand();
			if ((!Utils.IsSoliniaItem(itemstack)))
				return;

			// We have our custom item id, lets check it exists
			ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(itemstack);

			if (item == null) {
				return;
			}
			
			if (!item.isThrowing())
				return;
			
			int newAmount = itemstack.getAmount() -1;
			
			LivingEntity targetmob = Utils.getTargettedLivingEntity(getBukkitPlayer(), 50);
			if (targetmob != null && !targetmob.getUniqueId().equals(getBukkitPlayer().getUniqueId())) {
				if (item.useItemOnEntity(getBukkitPlayer(), targetmob, false) == true) {
					if (newAmount < 1)
					{
						getBukkitPlayer().getEquipment().setItemInMainHand(null);
						getBukkitPlayer().updateInventory();
					} else {
						itemstack.setAmount(newAmount);
						getBukkitPlayer().getEquipment().setItemInMainHand(itemstack);
						getBukkitPlayer().updateInventory();
					}
					return;
				} else {
					return;
				}
			}
		} catch (CoreStateInitException e)
		{
			
		}
	}
	
	@Override
	public void tryApplyAugmentation(ISoliniaItem item)
	{
		try
		{
			// Start applying an augmentation
			if (item.isAugmentation()) {
				if (StateManager.getInstance().getPlayerManager().getApplyingAugmentation(getBukkitPlayer().getUniqueId()) == null
						|| StateManager.getInstance().getPlayerManager().getApplyingAugmentation(getBukkitPlayer().getUniqueId()) == 0) 
				{
					StateManager.getInstance().getPlayerManager().setApplyingAugmentation(getBukkitPlayer().getUniqueId(), item.getId());
					getBukkitPlayer().sendMessage("* Applying " + item.getDisplayname() + " to an item, please right click the item you wish to apply this augmentation to. . To cancel applying, right click while holding this item again");
				} else {
					StateManager.getInstance().getPlayerManager()
							.setApplyingAugmentation(getBukkitPlayer().getUniqueId(), 0);
					getBukkitPlayer().sendMessage("* Cancelled applying augmentation");
				}
			}
		} catch (CoreStateInitException e)
		{
			
		}
		
		return;
	}

	@Override
	public void interact(PlayerInteractEvent event) {
		// TODO Auto-generated method stub
		ItemStack itemstack = event.getItem();
		
		if (itemstack == null)
			return;
		
		if (event.getHand() != EquipmentSlot.HAND)
			return;
		
		if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;
			
		if (!Utils.IsSoliniaItem(itemstack))
			return;			
			
		try {
			// We have our custom item id, lets check it exists
			ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(itemstack);

			if (ItemStackUtils.isPotion(itemstack)) {
				// Handled by on consume event
				return;
			}
			
			if (item == null)
				return;

			if (item.isAugmentation())
			{
				tryApplyAugmentation(item);
				return;
			}

			if (item.getMinLevel() > getLevel())
			{
				getBukkitPlayer().sendMessage("This item requires minimum level: " + item.getMinLevel());
				return;
			}
			
			// Not a clicky!
			if (!item.isThrowing() && !item.isPetControlRod() && item.getLanguagePrimer().equals("") && (item.getAbilityid() < 1))
				return;
			
			if (item.isThrowing()) {
				tryThrowItemInMainHand();
				return;
			}

			// Some spells auto target self, if they don't have a target try to do that
			if (getTarget() == null) {
				if (item.getAbilityid() > 0) {
					ISoliniaSpell spell = StateManager.getInstance().getConfigurationManager()
							.getSpell(item.getAbilityid());
					if (spell != null) {
						if (!tryFixSpellTarget(spell))
						{
							getBukkitPlayer().sendMessage(
									"* You must select a target (shift+left click with spell or use /target for group or shift-f for self (interact-tryFixSpellTarget)");
							return;
						}
					}
				}
			}

			if (getTarget() == null)
			{
				getBukkitPlayer().sendMessage("* You must select a target (shift+left click with spell or use /target for group or shift-f for self (interact)");
				return;
			}
			
			if (!item.isPetControlRod())
				if (isOverAggroDistance(getTarget()))
				{
					getBukkitPlayer().sendMessage("You were too far to interact with that entity");
					return;
				}

			// we should probably check line of sight here for detrimentals, or maybe in the
			// spell

			if (item.isPetControlRod()) {
				item.useItemOnEntity(getBukkitPlayer(), getTarget(), false);
				return;
			}
			
			// try consume language primer
			if (!item.getLanguagePrimer().equals("")) {
				item.useItemOnEntity(getBukkitPlayer(), getBukkitPlayer(), item.isConsumable());
				if (item.isConsumable())
				{
					getBukkitPlayer().setItemInHand(null);
					getBukkitPlayer().updateInventory();
				}
				return;
			}

			tryCastFromItemInSlot(getBukkitPlayer().getInventory().getHeldItemSlot());
		} catch (CoreStateInitException e) 
		{
			e.printStackTrace();
		}
	}

	private boolean isOverAggroDistance(LivingEntity target) {
		double distanceOverLimit = Utils.DistanceOverAggroLimit((LivingEntity) getBukkitPlayer(),
				target);
		
		if (distanceOverLimit > 0)
			return true;
		
		return false;
	}

	private boolean tryFixSpellTarget(ISoliniaSpell spell) {
		try
		{
			if (Utils.getSpellTargetType(spell.getTargettype()).equals(SpellTargetType.Self)
					|| Utils.getSpellTargetType(spell.getTargettype()).equals(SpellTargetType.AEBard)
					|| Utils.getSpellTargetType(spell.getTargettype()).equals(SpellTargetType.AECaster)
					|| Utils.getSpellTargetType(spell.getTargettype())
							.equals(SpellTargetType.AEClientV1)
					|| Utils.getSpellTargetType(spell.getTargettype())
							.equals(SpellTargetType.AreaClientOnly)
					|| Utils.getSpellTargetType(spell.getTargettype())
							.equals(SpellTargetType.Directional)
					|| Utils.getSpellTargetType(spell.getTargettype()).equals(SpellTargetType.Group)
					|| Utils.getSpellTargetType(spell.getTargettype())
							.equals(SpellTargetType.GroupClientAndPet)
					|| Utils.getSpellTargetType(spell.getTargettype())
							.equals(SpellTargetType.GroupNoPets)
					|| Utils.getSpellTargetType(spell.getTargettype())
							.equals(SpellTargetType.GroupTeleport)
					|| Utils.getSpellTargetType(spell.getTargettype())
							.equals(SpellTargetType.TargetOptional)
					|| Utils.getSpellTargetType(spell.getTargettype())
							.equals(SpellTargetType.UndeadAE)) {
				StateManager.getInstance().getEntityManager().setEntityTarget(getBukkitPlayer(),
						getBukkitPlayer());
				return true;
			} else if (Utils.getSpellTargetType(spell.getTargettype()).equals(SpellTargetType.Pet))
			{
				LivingEntity pet = StateManager.getInstance().getEntityManager().getPet(getBukkitPlayer().getUniqueId());
				if (pet != null)
				{
					StateManager.getInstance().getEntityManager().setEntityTarget(getBukkitPlayer(),
						pet);
					return true;
				} else {
					getBukkitPlayer().sendMessage(
							"* You must select a target (shift+left click with spell or use /target for group or shift-f for self (fixspelltarget I)");
					return false;
				}
			} else {
				getBukkitPlayer().sendMessage(
						"* You must select a target (shift+left click with spell or use /target for group or shift-f for self (fixspelltarget II)");
				return false;
			}
		} catch (CoreStateInitException e)
		{
			
		}
		return false;
	}

	@Override
	public void startCasting(ISoliniaSpell spell, Player player, boolean useMana, boolean useReagents, boolean ignoreProfessionAndLevel) {
		try {

			CastingSpell castingSpell = new CastingSpell(player.getUniqueId(), spell.getId(), useMana, useReagents, ignoreProfessionAndLevel);
			StateManager.getInstance().getEntityManager().startCasting((LivingEntity) player, castingSpell);
		} catch (CoreStateInitException e) {

		}
	}

	@Override
	public void castingComplete(CastingSpell castingSpell) {
		Entity entity = Bukkit.getEntity(castingSpell.livingEntityUUID);

		if (entity == null)
			return;

		if (!(entity instanceof Player))
			return;

		Player player = (Player) entity;

		if (player.isDead())
			return;

		if (castingSpell.getSpell() == null)
			return;

		/*if (castingSpell.getItem() == null)
			return;*/

		doCastSpell(castingSpell.getSpell(), player, castingSpell.useMana, castingSpell.useReagents, castingSpell.ignoreProfessionAndLevel);
	}

	@Override
	public void doCastSpell(ISoliniaSpell spell, Player player, boolean useMana, boolean useReagents, boolean ignoreProfessionAndLevel) {
		if (!ignoreProfessionAndLevel && spell.isAASpell() && !canUseAASpell(spell)) {
			player.sendMessage("You require the correct AA to use this spell");
			return;
		}

		LivingEntity targetmob = null;

		// This now all uses the targetting system
		// If the player is using a self only spell or AE switch target to themselves if
		// they have no target right now
		try {
			if (getTarget() == null) {
				if (Utils.getSpellTargetType(spell.getTargettype()).equals(SpellTargetType.Self)
						|| Utils.getSpellTargetType(spell.getTargettype()).equals(SpellTargetType.AEBard)
						|| Utils.getSpellTargetType(spell.getTargettype()).equals(SpellTargetType.AECaster)
						|| Utils.getSpellTargetType(spell.getTargettype()).equals(SpellTargetType.AEClientV1)
						|| Utils.getSpellTargetType(spell.getTargettype()).equals(SpellTargetType.AreaClientOnly)
						|| Utils.getSpellTargetType(spell.getTargettype()).equals(SpellTargetType.Directional)
						|| Utils.getSpellTargetType(spell.getTargettype()).equals(SpellTargetType.Group)
						|| Utils.getSpellTargetType(spell.getTargettype()).equals(SpellTargetType.GroupClientAndPet)
						|| Utils.getSpellTargetType(spell.getTargettype()).equals(SpellTargetType.GroupNoPets)
						|| Utils.getSpellTargetType(spell.getTargettype()).equals(SpellTargetType.GroupTeleport)
						|| Utils.getSpellTargetType(spell.getTargettype()).equals(SpellTargetType.TargetOptional)
						|| Utils.getSpellTargetType(spell.getTargettype()).equals(SpellTargetType.UndeadAE)) {
					StateManager.getInstance().getEntityManager().setEntityTarget(getBukkitPlayer(), getBukkitPlayer());
				} else {
					getBukkitPlayer().sendMessage(
							"* You must select a target (shift+left click with spell or use /target for group or shift-f for self (spell item)");
					return;
				}
			}

			targetmob = getTarget();
		} catch (CoreStateInitException e) {

		}
		
		if (targetmob != null)
		{
			double distanceOverLimit = Utils.DistanceOverAggroLimit((LivingEntity) getBukkitPlayer(),
					targetmob);
			
			if (distanceOverLimit > 0)
			{
				getBukkitPlayer().sendMessage("You were too far to interact with that entity");
				return;
			}
		}

		if (!ignoreProfessionAndLevel && spell.getAllowedClasses().size() > 0) {
			if (getClassObj() == null) {
				player.sendMessage(ChatColor.GRAY + " * This effect cannot be used by your profession");
				return;
			}

			boolean foundprofession = false;
			String professions = "";
			int foundlevel = 0;
			for (SoliniaSpellClass spellclass : spell.getAllowedClasses()) {
				if (spellclass.getClassname().toUpperCase().equals(getClassObj().getName().toUpperCase())) {
					foundprofession = true;
					foundlevel = spellclass.getMinlevel();
					break;
				}
				professions += spellclass.getClassname() + " ";
			}

			if (foundprofession == false) {
				player.sendMessage(ChatColor.GRAY + " * This effect can only be used by " + professions);
				return;
			} else {
				if (foundlevel > 0) {
					Double level = (double) getLevel();
					if (level < foundlevel) {
						player.sendMessage(ChatColor.GRAY + " * This effect requires level " + foundlevel);
						return;
					}
				}
			}

		}

		try {
			ISoliniaLivingEntity solentity = SoliniaLivingEntityAdapter.Adapt((LivingEntity) player);
			if (solentity == null)
				return;

			if (useMana && spell.getActSpellCost(solentity) > SoliniaPlayerAdapter.Adapt(player).getMana()) {
				player.sendMessage(ChatColor.GRAY + "Insufficient Mana [E]");
				return;
			}

			if (!spell.isBardSong() && useReagents) {
				if (spell.getComponents1() > 0) {
					ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
					ISoliniaItem item = StateManager.getInstance().getConfigurationManager()
							.getItem(spell.getComponents1());
					if (item == null || !item.isReagent()) {
						player.sendMessage(ChatColor.RED + "ERROR: " + ChatColor.YELLOW + "ERROR-ALERT-ADMIN-SPELL"
								+ spell.getId() + "-ID" + spell.getComponents1());
						return;
					}
					if (!solPlayer.hasSufficientReagents(spell.getComponents1(), spell.getComponentCounts1())) {
						player.sendMessage(ChatColor.GRAY + "Insufficient Reagents (Check spell and see /reagents)");
						return;
					}
				}

				if (spell.getComponents2() > 0) {
					ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
					ISoliniaItem item = StateManager.getInstance().getConfigurationManager()
							.getItem(spell.getComponents2());
					if (item == null || !item.isReagent()) {
						player.sendMessage(ChatColor.RED + "ERROR: " + ChatColor.YELLOW + "ERROR-ALERT-ADMIN-SPELL"
								+ spell.getId() + "-ID" + spell.getComponents2());
						return;
					}
					if (!solPlayer.hasSufficientReagents(spell.getComponents2(), spell.getComponentCounts2())) {
						player.sendMessage(ChatColor.GRAY + "Insufficient Reagents (Check spell and see /reagents)");
						return;
					}
				}

				if (spell.getComponents3() > 0) {
					ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
					ISoliniaItem item = StateManager.getInstance().getConfigurationManager()
							.getItem(spell.getComponents3());
					if (item == null || !item.isReagent()) {
						player.sendMessage(ChatColor.RED + "ERROR: " + ChatColor.YELLOW + "ERROR-ALERT-ADMIN-SPELL"
								+ spell.getId() + "-ID" + spell.getComponents3());
						return;
					}
					if (!solPlayer.hasSufficientReagents(spell.getComponents3(), spell.getComponentCounts3())) {
						player.sendMessage(ChatColor.GRAY + "Insufficient Reagents (Check spell and see /reagents)");
						return;
					}
				}

				if (spell.getComponents4() > 0) {
					ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
					ISoliniaItem item = StateManager.getInstance().getConfigurationManager()
							.getItem(spell.getComponents4());
					if (item == null || !item.isReagent()) {
						player.sendMessage(ChatColor.RED + "ERROR: " + ChatColor.YELLOW + "ERROR-ALERT-ADMIN-SPELL"
								+ spell.getId() + "-ID" + spell.getComponents4());
						return;
					}
					if (!solPlayer.hasSufficientReagents(spell.getComponents4(), spell.getComponentCounts4())) {
						player.sendMessage(ChatColor.GRAY + "Insufficient Reagents (Check spell and see /reagents)");
						return;
					}
				}
			}
		} catch (CoreStateInitException e) {
			return;
		}

		if (player != null && !player.isDead())
		if (targetmob != null && !targetmob.isDead()) {
			boolean success = spell.tryCast(player, targetmob, useMana, useReagents);
			if (success == true) {
				tryIncreaseSkill(Utils.getSkillType(spell.getSkill()).name().toUpperCase(), 1);
			}
			return;
		} else {
			boolean success = spell.tryCast(player, player, useMana, useReagents);
			if (success == true) {
				tryIncreaseSkill(Utils.getSkillType(spell.getSkill()).name().toUpperCase(), 1);
			}
			return;
		}
	}

	@Override
	public boolean canUseAASpell(ISoliniaSpell spell) {
		// If its not an AA spell then sure they can use it
		if (!spell.isAASpell())
			return true;

		try {
			List<Integer> rankIds = StateManager.getInstance().getConfigurationManager()
					.getAASpellRankCache(spell.getId());
			if (rankIds.size() < 1)
				return true;

			for (Integer rankId : rankIds) {
				ISoliniaAARank rank = StateManager.getInstance().getConfigurationManager().getAARank(rankId);
				if (this.hasRank(rank))
					return true;
			}

		} catch (CoreStateInitException e) {
			return false;
		}

		return false;
	}

	@Override
	public boolean checkDoesntFizzle(ISoliniaSpell spell) {
		Utils.DebugLog("SoliniaPlayer", "checkDoesntFizzle", this.getBukkitPlayer().getName(), "Starting checkDoesntFizzle for " + getBukkitPlayer().getName());
		
		if (getBukkitPlayer().isOp())
		{
			Utils.DebugLog("SoliniaPlayer", "checkDoesntFizzle", this.getBukkitPlayer().getName(), "checkDoesntFizzle was never fizzle as player is an Op");
			return true;
		}
			
		// todo fizzle free features

		int no_fizzle_level = 0;
		ISoliniaAAAbility aa = null;
		try {
			if(getAARanks().size() > 0)
			{
				for(ISoliniaAAAbility ability : StateManager.getInstance().getConfigurationManager().getAAbilitiesBySysname("SPELLCASTINGEXPERTISE"))
				{
					if (!hasAAAbility(ability.getId()))
						continue;
					
					aa = ability;
				}
			}
		} catch (CoreStateInitException e) {

		}

		if (aa != null) {
			ISoliniaAARank AArank = Utils.getRankOfAAAbility(getBukkitPlayer(), aa);
			if (AArank != null) {
				for (SoliniaAARankEffect rankEffect : AArank.getEffects()) {
					Utils.DebugLog("SoliniaPlayer", "checkDoesntFizzle", this.getBukkitPlayer().getName(), "FoundSpellCastingExpertise level: " + rankEffect.getBase1());

					if (rankEffect.getBase1() > no_fizzle_level)
						no_fizzle_level = rankEffect.getBase1();
				}
			}
		}

		try {
			// there are two potential mastery of the pasts
			for (ISoliniaAAAbility aaMasteryOfThePast : StateManager.getInstance().getConfigurationManager()
					.getAAbilitiesBySysname("MASTERYOFTHEPAST")) {
				ISoliniaAARank AArank = Utils.getRankOfAAAbility(getBukkitPlayer(), aaMasteryOfThePast);
				if (AArank != null) {
					for (SoliniaAARankEffect rankEffect : AArank.getEffects()) {
						Utils.DebugLog("SoliniaPlayer", "checkDoesntFizzle", this.getBukkitPlayer().getName(), "FoundMasteryOfPast level: " + rankEffect.getBase1());

						if (rankEffect.getBase1() > no_fizzle_level)
							no_fizzle_level = rankEffect.getBase1();
					}
				}
			}
		} catch (CoreStateInitException e) {

		}

		Utils.DebugLog("SoliniaPlayer", "checkDoesntFizzle", this.getBukkitPlayer().getName(), "MinLevelClass check: " + spell.getMinLevelClass(getClassObj().getName()) + " is less than no_fizzle_level " + no_fizzle_level + "?");
		if (spell.getMinLevelClass(getClassObj().getName()) < no_fizzle_level) {
			return true;
		}

		// todo item and spell no fizzle levels

		try {

			ISoliniaLivingEntity entity = SoliniaLivingEntityAdapter.Adapt(getBukkitPlayer());

			if (entity == null)
				return false;

			if (getClassObj() == null)
				return false;

			// TODO item/aa/spells fizzle bonus

			int par_skill = 0;
			int act_skill = 0;

			int minLevel = 0;

			for (SoliniaSpellClass spellClass : spell.getAllowedClasses()) {
				if (!spellClass.getClassname().toUpperCase().equals(getClassObj().getName().toUpperCase()))
					continue;

				minLevel = spellClass.getMinlevel();
				break;
			}

			par_skill = minLevel * 5 - 10;

			if (par_skill > 235)
				par_skill = 235;

			par_skill += minLevel;

			SoliniaPlayerSkill playerSkill = getSkill(Utils.getSkillType(spell.getSkill()).name().toUpperCase());

			if (playerSkill != null)
				act_skill = playerSkill.getValue();

			act_skill += getLevel();

			// todo: spell specialisation
			int specialisation = 0;

			float diff = par_skill + (float) (spell.getBasediff()) - act_skill;

			if (getClassObj().getName().equals("BARD")) {
				diff -= (entity.getCharisma() - 110) / 20.0;
			} else if (Utils.getCasterClass(getClassObj().getName().toUpperCase()).equals("W")) {
				diff -= (entity.getWisdom() - 125) / 20.0;
			} else if (Utils.getCasterClass(getClassObj().getName().toUpperCase()).equals("I")) {
				diff -= (entity.getIntelligence() - 125) / 20.0;
			}

			float basefizzle = 10;
			float fizzlechance = (float) (basefizzle - specialisation + diff / 5.0);

			// always at least 1% chance to fail or 5% to succeed
			fizzlechance = fizzlechance < 1 ? 1 : (fizzlechance > 95 ? 95 : fizzlechance);

			float fizzle_roll = Utils.RandomBetween(0, 100);

			// System.out.println(getFullName() + " Fizzle Roll: " + fizzle_roll + " vs " +
			// fizzlechance);
			if (fizzle_roll > fizzlechance)
				return true;

		} catch (CoreStateInitException e) {
			return false;
		}

		return false;
	}

	@Override
	public UUID getInteraction() {
		return interaction;
	}

	@Override
	public void setInteraction(UUID interaction, ISoliniaNPC npc) {
		if (interaction == null) {
			this.interaction = interaction;
			this.getBukkitPlayer().sendMessage(ChatColor.GRAY + "* You are no longer interacting");
			return;
		}

		Entity e = Bukkit.getEntity(interaction);
		if (e == null)
			return;

		if (!(e instanceof LivingEntity))
			return;

		if (((Creature) e).getTarget() != null) {
			if (interaction != null) {
				this.getBukkitPlayer().sendMessage(ChatColor.GRAY + "* You are no longer interacting");
				interaction = null;
			}
			return;
		}

		if (e instanceof LivingEntity)
		{
			try
			{			
				ISoliniaLivingEntity solLivingEntity = SoliniaLivingEntityAdapter.Adapt((LivingEntity)e);
				if (solLivingEntity.isCurrentlyNPCPet()) {
					return;
				}
			} catch (CoreStateInitException eGetNpcPet)
			{
				
			}
		}
		this.interaction = interaction;

		if (npc != null) {
			String spawngroupId = "";
			
			this.getBukkitPlayer().sendMessage(ChatColor.GRAY + "* You are now interacting with "
					+ Bukkit.getEntity(interaction).getName() + " NID: " + npc.getId()
					+ " SID: " + spawngroupId +" - Anything you type will be heared by the NPC and possibly responded to. Words in pink are trigger words you can type");

			if (npc.getMerchantid() > 0) {
				try {
					StateManager.getInstance().getEntityManager().getLivingEntity((LivingEntity) e)
							.sayto(this.getBukkitPlayer(),"i have a [" + ChatColor.LIGHT_PURPLE + "SHOP" + ChatColor.AQUA + "] available if you are interested in buying or selling something",true);
				} catch (CoreStateInitException cse) {
					//
				}
			}

			for (ISoliniaNPCEventHandler eventHandler : npc.getEventHandlers()) {
				if (!eventHandler.getInteractiontype().equals(InteractionType.ITEM))
					continue;

				// See if player has any items that are wanted
				int itemId = Integer.parseInt(eventHandler.getTriggerdata());
				if (itemId == 0)
					continue;

				if (Utils.getPlayerTotalCountOfItemId(getBukkitPlayer(), itemId) < 1)
					continue;

				try {
					ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(itemId);

					TextComponent tc = new TextComponent(TextComponent.fromLegacyText(ChatColor.YELLOW + "[QUEST] "));
					TextComponent tc2 = new TextComponent(TextComponent.fromLegacyText(ChatColor.YELLOW + "- Click here to give " + item.getDisplayname() + ChatColor.RESET));
					tc2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/npcgive " + itemId));
					tc.addExtra(tc2);
					getBukkitPlayer().spigot().sendMessage(tc);
				} catch (CoreStateInitException eNotInitialised) {
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
		if (soliniaskill != null && soliniaskill.getValue() >= 100) {
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

		tryIncreaseSkill(language, 1);
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
		try {
			total += SoliniaLivingEntityAdapter.Adapt(getBukkitPlayer()).getResistsFromActiveEffects(type);

			int effectId = 0;
			switch (type) {
			case RESIST_FIRE:
				effectId = Utils.getEffectIdFromEffectType(SpellEffectType.ResistFire);
				break;
			case RESIST_COLD:
				effectId = Utils.getEffectIdFromEffectType(SpellEffectType.ResistCold);
				break;
			case RESIST_MAGIC:
				effectId = Utils.getEffectIdFromEffectType(SpellEffectType.ResistMagic);
				break;
			case RESIST_POISON:
				effectId = Utils.getEffectIdFromEffectType(SpellEffectType.ResistPoison);
				break;
			case RESIST_DISEASE:
				effectId = Utils.getEffectIdFromEffectType(SpellEffectType.ResistDisease);
				break;
			default:
				break;
			}

			if (effectId > 0) {
				int aaEffect = 0;

				for (SoliniaAARankEffect highestRankEffect : Utils.getHighestRankEffectsForEffectId(this, effectId)) {
					aaEffect += highestRankEffect.getBase1();
				}

				total += aaEffect;
			}

			int resistAllEffectId = Utils.getEffectIdFromEffectType(SpellEffectType.ResistAll);
			for (SoliniaAARankEffect effect : this.getRanksEffectsOfEffectType(resistAllEffectId)) {
				total += effect.getBase1();
			}

		} catch (CoreStateInitException e) {
			// Skip
		}

		for (ISoliniaItem item : this.getEquippedSoliniaItems()) {
			switch (type) {
			case RESIST_FIRE:
				if (item.getFireResist() > 0) {
					total += item.getFireResist();
				}
				break;
			case RESIST_COLD:
				if (item.getColdResist() > 0) {
					total += item.getColdResist();
				}
				break;
			case RESIST_MAGIC:
				if (item.getMagicResist() > 0) {
					total += item.getMagicResist();
				}
				break;
			case RESIST_POISON:
				if (item.getPoisonResist() > 0) {
					total += item.getPoisonResist();
				}
				break;
			case RESIST_DISEASE:
				if (item.getDiseaseResist() > 0) {
					total += item.getDiseaseResist();
				}
				break;
			default:
				break;
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
		if (getLevel() < 50)
			return buyableRanks;

		try {
			for (ISoliniaAAAbility ability : StateManager.getInstance().getConfigurationManager().getAAAbilities()) {
				for (ISoliniaAARank rank : ability.getRanks()) {
					if (this.ranks.contains(rank.getId()))
						continue;

					if (hasPrerequisites(ability, rank) && canUseAlternateAdvancementRank(ability, rank))
						buyableRanks.add(rank);
					break;
				}
			}
		} catch (CoreStateInitException e) {
			// ignore
		}
		return buyableRanks;

	}

	@Override
	public boolean canPurchaseAlternateAdvancementRank(ISoliniaAAAbility ability, ISoliniaAARank rank) {
		// Has already
		if (this.ranks.contains(rank.getId()))
			return false;

		if (rank.getAbilityid() == 0)
			return false;

		if (rank.getCost() == 0)
			return false;

		if (rank.getCost() > getAAPoints())
			return false;

		if (!canUseAlternateAdvancementRank(ability, rank)) {
			return false;
		}

		if (rank.getLevel_req() > getLevel()) {
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
		for (SoliniaAAPrereq prereq : rank.getPrereqs()) {
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
	public void clearAAs() {
		this.aas.clear();
		this.ranks.clear();
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
	public List<SoliniaAARankEffect> getRanksEffectsOfEffectType(int effectId) {
		List<SoliniaAARankEffect> effects = new ArrayList<SoliniaAARankEffect>();
		if (ranks.size() == 0)
			return effects;

		try {
			for (int rankId : ranks) {
				ISoliniaAARank rank = StateManager.getInstance().getConfigurationManager().getAARankCache(rankId);
				if (rank == null)
					continue;

				for (SoliniaAARankEffect effect : rank.getEffects()) {
					if (effect.getEffectId() != effectId)
						continue;

					effects.add(effect);
				}
			}
		} catch (CoreStateInitException e) {
			//
		}

		return effects;
	}

	@Override
	public List<ISoliniaAAAbility> getAAAbilitiesWithEffectType(int effectId) {
		List<Integer> aaIds = new ArrayList<Integer>();
		List<ISoliniaAAAbility> abilities = new ArrayList<ISoliniaAAAbility>();

		try {
			for (SoliniaAARankEffect rankEffect : getRanksEffectsOfEffectType(effectId)) {
				ISoliniaAARank rank = StateManager.getInstance().getConfigurationManager()
						.getAARank(rankEffect.getRankId());
				if (rank != null) {
					if (!aaIds.contains(rank.getAbilityid())) {
						aaIds.add(rank.getAbilityid());
						ISoliniaAAAbility ability = StateManager.getInstance().getConfigurationManager()
								.getAAAbility(rank.getAbilityid());
						abilities.add(ability);
					}
				}
			}
		} catch (CoreStateInitException e) {

		}

		return abilities;
	}

	@Override
	public List<ISoliniaAARank> getAARanks() {
		List<ISoliniaAARank> rankList = new ArrayList<ISoliniaAARank>();
		if (ranks.size() == 0)
			return rankList;

		try {
			for (int rankId : ranks) {
				ISoliniaAARank rank = StateManager.getInstance().getConfigurationManager().getAARankCache(rankId);
				if (rank == null)
					continue;

				rankList.add(rank);
			}
		} catch (CoreStateInitException e) {
			//
		}

		return rankList;
	}

	@Override
	public boolean hasPreviousRanks(ISoliniaAAAbility ability, ISoliniaAARank rank) {
		for (ISoliniaAARank curRank : ability.getRanks()) {
			if (curRank.getPosition() >= rank.getPosition())
				continue;

			if (!hasRank(curRank))
				return false;
		}

		return true;
	}

	@Override
	public void purchaseAlternateAdvancementRank(ISoliniaAAAbility ability, ISoliniaAARank rank) {
		if (!canPurchaseAlternateAdvancementRank(ability, rank))
			return;

		setAAPoints(getAAPoints() - rank.getCost());
		ranks.add(rank.getId());
		if (!aas.contains(rank.getAbilityid()))
			aas.add(rank.getAbilityid());
		getBukkitPlayer()
				.sendMessage("You have gained the AA " + ability.getName() + " (rank " + rank.getPosition() + ")");
	}

	@Override
	public boolean canDualWield() {
		if (getClassObj() == null)
			return false;

		if (getClassObj().canDualWield() == false)
			return false;

		if (getClassObj().getDualwieldlevel() > getLevel())
			return false;

		return true;
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
	public boolean getDualWieldCheck(ISoliniaLivingEntity solEntity) {
		if (canDualWield() == false)
			return false;

		int chance = getSkill("DUALWIELD").getValue();
		chance += 50;

		try {
			ISoliniaLivingEntity solLivingEntity = SoliniaLivingEntityAdapter.Adapt(getBukkitPlayer());
			if (solLivingEntity != null) {
				int spellAmbidexterity = solLivingEntity.getSpellBonuses(SpellEffectType.Ambidexterity);
				int aaAmbidexterity = Utils.getTotalAAEffectEffectType(getBukkitPlayer(),
						SpellEffectType.Ambidexterity);
				int spellDualWieldChance = solLivingEntity.getSpellBonuses(SpellEffectType.DualWieldChance);
				int aaDualWieldChance = Utils.getTotalAAEffectEffectType(getBukkitPlayer(),
						SpellEffectType.DualWieldChance);

				chance += aaAmbidexterity + spellAmbidexterity;
				int per_inc = spellDualWieldChance + aaDualWieldChance;
				if (per_inc > 0)
					chance += chance * per_inc / 100;
			}
		} catch (CoreStateInitException e) {

		}

		return Utils.RandomBetween(1, 375) <= chance;
	}

	@Override
	public boolean getDodgeCheck() {
		if (canDodge() == false)
			return false;

		int chance = getSkill("DODGE").getValue();
		chance += 100;
		chance /= 40;

		return Utils.RandomBetween(1, 500) <= chance;
	}

	@Override
	public boolean getRiposteCheck() {
		if (canRiposte() == false)
			return false;

		int chance = getSkill("RIPOSTE").getValue();
		chance += 100;
		chance /= 50;

		return Utils.RandomBetween(1, 500) <= chance;
	}

	@Override
	public boolean getDoubleAttackCheck() {
		if (canDoubleAttack() == false)
			return false;

		int chance = getSkill("DOUBLEATTACK").getValue();
		chance += 20;
		if (getLevel() > 35) {
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
		for (PlayerFactionEntry entry : getFactionEntries()) {
			if (entry.getFactionId() == factionId)
				return entry;
		}

		// If we get here go ahead and create a faction entry
		return createPlayerFactionEntry(factionId);
	}

	@Override
	public PlayerFactionEntry createPlayerFactionEntry(int factionId) {
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
		if (playerFactionEntry.getFaction().getName().toUpperCase().equals("NEUTRAL")
				|| playerFactionEntry.getFaction().getName().toUpperCase().equals("KOS"))
			return;

		int newValue = playerFactionEntry.getValue() + amount;
		boolean hitCap = false;
		int upperCap = 1500-playerFactionEntry.getFaction().getBase();
		
		if (newValue > upperCap) {
			newValue = upperCap;
			hitCap = true;

			if (!playerFactionEntry.getFaction().getAllyGrantsTitle().equals("")) {
				grantTitle(playerFactionEntry.getFaction().getAllyGrantsTitle());
			}

			getBukkitPlayer().sendMessage(ChatColor.GRAY + "* Your faction standing with "
					+ playerFactionEntry.getFaction().getName().toLowerCase() + " could not possibly get any better");
		}

		if (!hitCap) {
			getBukkitPlayer().sendMessage(ChatColor.GRAY + "* Your faction standing with "
					+ playerFactionEntry.getFaction().getName().toLowerCase() + " got better");
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
		if (playerFactionEntry.getFaction().getName().toUpperCase().equals("NEUTRAL")
				|| playerFactionEntry.getFaction().getName().toUpperCase().equals("KOS"))
			return;

		int newValue = playerFactionEntry.getValue() - amount;
		boolean hitCap = false;
		int lowerCap = -1500-playerFactionEntry.getFaction().getBase();

		if (newValue < lowerCap) {
			newValue = lowerCap;
			hitCap = true;

			if (!playerFactionEntry.getFaction().getScowlsGrantsTitle().equals("")) {
				grantTitle(playerFactionEntry.getFaction().getScowlsGrantsTitle());
			}

			getBukkitPlayer().sendMessage(ChatColor.GRAY + "* Your faction standing with "
					+ playerFactionEntry.getFaction().getName().toLowerCase() + " could not possibly get any worse");
		}

		if (!hitCap) {
			getBukkitPlayer().sendMessage(ChatColor.GRAY + "* Your faction standing with "
					+ playerFactionEntry.getFaction().getName().toLowerCase() + " got worse");
		}

		playerFactionEntry.setValue(newValue);
	}

	@Override
	public void ignorePlayer(Player player) {
		if (ignoredPlayers.contains(player.getUniqueId())) {
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
	public boolean isMezzed() {
		try {
			Timestamp mezExpiry = StateManager.getInstance().getEntityManager()
					.getMezzed((LivingEntity) getBukkitPlayer());

			if (mezExpiry != null) {
				return true;
			}
		} catch (CoreStateInitException e) {
			return false;
		}

		return false;
	}
	
	@Override
	public boolean isStunned() {
		try {
			Timestamp stunExpiry = StateManager.getInstance().getEntityManager()
					.getStunned((LivingEntity) getBukkitPlayer());

			if (stunExpiry != null) {
				return true;
			}
		} catch (CoreStateInitException e) {
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
		this.getBukkitPlayer().sendMessage(
				ChatColor.YELLOW + " * You have received a new Quest [" + quest.getQuest().getName() + "]! See /quests for more info");
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
		this.getBukkitPlayer().sendMessage(ChatColor.YELLOW + " * You have received a new Quest Flag! See /quests for more info");
	}

	@Override
	public boolean isMeditating() {
		Location currentLocation = getBukkitPlayer().getLocation();
		if (currentLocation.getBlockX() == lastX &&
			currentLocation.getBlockY() == lastY &&
			currentLocation.getBlockZ() == lastZ)
			{
				return true;
			
			} else {
				return false;
			}
	}

	@Override
	public void setSkills(List<SoliniaPlayerSkill> skills) {
		this.skills = skills;
	}
	
	@Override
	public String getSpecialisation() {
		return specialisation;
	}

	@Override
	public void setSpecialisation(String specialisation) {
		this.specialisation = specialisation;
	}

	@Override
	public boolean isVampire() {
		if (vampire == true)
			return true;

		if (getRace() != null)
			if (getRace().isVampire())
				return true;

		return false;
	}

	@Override
	public void setVampire(boolean vampire) {
		this.vampire = vampire;
	}

	@Override
	public boolean isMain() {
		return main;
	}

	@Override
	public void setMain(boolean main) {
		this.main = main;
	}

	@Override
	public UUID getCharacterId() {
		return characterId;
	}

	@Override
	public void setCharacterId(UUID characterId) {
		this.characterId = characterId;
	}

	@Override
	public int getInspiration() {
		return inspiration;
	}

	@Override
	public void setInspiration(int inspiration) {
		this.inspiration = inspiration;
	}

	@Override
	public Timestamp getExperienceBonusExpires() {
		return experienceBonusExpires;
	}

	@Override
	public void setExperienceBonusExpires(Timestamp experienceBonusExpires) {
		this.experienceBonusExpires = experienceBonusExpires;
	}

	@Override
	public void grantExperienceBonusFromItem() {
		LocalDateTime datetime = LocalDateTime.now();
		Timestamp nowtimestamp = Timestamp.valueOf(datetime);

		if (getExperienceBonusExpires() == null) {
			System.out.println(
					"Granted Experience Bonus From Item [Current expiry was null]: " + nowtimestamp.toString());
			setExperienceBonusExpires(nowtimestamp);
		}

		LocalDateTime expiredatetime = nowtimestamp.toLocalDateTime();

		Timestamp expiretimestamp = Timestamp.valueOf(expiredatetime.plus(1, ChronoUnit.HOURS));
		System.out.println(
				"Granted Experience Bonus From Item [Current expiry was not null]: " + expiretimestamp.toString());
		setExperienceBonusExpires(expiretimestamp);
		this.getBukkitPlayer().sendMessage(ChatColor.YELLOW + "You have gained 100% experience for 1 additional hour");
	}

	@Override
	public List<SoliniaAccountClaim> getAccountClaims() {
		try {
			return StateManager.getInstance().getConfigurationManager()
					.getAccountClaims(this.getBukkitPlayer().getName());
		} catch (CoreStateInitException e) {
			return new ArrayList<SoliniaAccountClaim>();
		}
	}

	@Override
	public boolean isOocEnabled() {
		return oocEnabled;
	}

	@Override
	public void setOocEnabled(boolean oocEnabled) {
		this.oocEnabled = oocEnabled;
	}

	@Override
	public void setBindPoint(String teleportlocation) {
		this.bindPoint = teleportlocation;
	}

	@Override
	public String getBindPoint() {
		return this.bindPoint;
	}

	@Override
	public void removeAllEntityEffects(Plugin plugin) {
		try {
			StateManager.getInstance().getEntityManager().clearEntityEffects(this.getBukkitPlayer().getUniqueId());
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void killAllPets() {
		try {
			LivingEntity pet = StateManager.getInstance().getEntityManager().getPet(this.getBukkitPlayer().getUniqueId());
			if (pet == null)
				return;

			ISoliniaLivingEntity petsolEntity = SoliniaLivingEntityAdapter.Adapt(pet);
			StateManager.getInstance().getEntityManager().removePet(this.getBukkitPlayer().getUniqueId(), !petsolEntity.isCharmed());
		} catch (CoreStateInitException e) {

		}
	}

	@Override
	public int getFingersItem() {
		return fingersItem;
	}

	@Override
	public void setFingersItem(int fingersItem) {
		this.fingersItem = fingersItem;
	}

	@Override
	public int getShouldersItem() {
		return shouldersItem;
	}

	@Override
	public void setShouldersItem(int shouldersItem) {
		this.shouldersItem = shouldersItem;
	}

	@Override
	public int getEarsItem() {
		return earsItem;
	}

	@Override
	public void setEarsItem(int earsItem) {
		this.earsItem = earsItem;
	}

	@Override
	public int getNeckItem() {
		return neckItem;
	}

	@Override
	public void setNeckItem(int neckItem) {
		this.neckItem = neckItem;
	}

	@Override
	public int getItemHpRegenBonuses() {
		int bonus = 0;
		for (ISoliniaItem item : getEquippedSoliniaItems()) {
			bonus += item.getHpregen();
		}
		return bonus;
	}

	@Override
	public int getItemMpRegenBonuses() {
		int bonus = 0;
		for (ISoliniaItem item : getEquippedSoliniaItems()) {
			bonus += item.getMpregen();
		}
		return bonus;
	}

	@Override
	public List<ISoliniaItem> getEquippedSoliniaItems() {
		return getEquippedSoliniaItems(false);
	}

	@Override
	public boolean isFeignedDeath() {
		try {
			return StateManager.getInstance().getEntityManager().isFeignedDeath(getBukkitPlayer().getUniqueId());
		} catch (CoreStateInitException e) {
			return false;
		}
	}

	@Override
	public void setFeigned(boolean feigned) {
		try {
			StateManager.getInstance().getEntityManager().setFeignedDeath(getBukkitPlayer().getUniqueId(), feigned);
			if (feigned == true) {
				getBukkitPlayer().sendMessage(ChatColor.GRAY + "* You feign your death");
				StateManager.getInstance().getEntityManager().clearTargetsAgainstMe(getBukkitPlayer());

			} else {
				getBukkitPlayer().sendMessage(ChatColor.GRAY + "* You are no longer feigning death!");
			}
		} catch (CoreStateInitException e) {

		}
	}

	@Override
	public List<ISoliniaItem> getEquippedSoliniaItems(boolean excludeMainHand) {
		List<ISoliniaItem> items = new ArrayList<ISoliniaItem>();

		try {
			List<ItemStack> itemStacks = new ArrayList<ItemStack>() {
				/**
				 * 
				 */
				private static final long serialVersionUID = 2958027330481470950L;

				{
					if (excludeMainHand == false) {
						add(getBukkitPlayer().getInventory().getItemInMainHand());
					}
					add(getBukkitPlayer().getInventory().getItemInOffHand());
					addAll(Arrays.asList(getBukkitPlayer().getInventory().getArmorContents()));
				}
			};
			for (ItemStack itemstack : itemStacks) {
				if (itemstack == null)
					continue;

				if (Utils.IsSoliniaItem(itemstack)) {

					ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(itemstack);
					if (item == null)
						continue;

					if (item.isSpellscroll())
						continue;

					items.add(item);

					Integer augmentationId = ItemStackUtils.getAugmentationItemId(itemstack);
					ISoliniaItem augItem = null;
					if (augmentationId != null && augmentationId != 0) {
						augItem = StateManager.getInstance().getConfigurationManager().getItem(augmentationId);
						items.add(augItem);
					}
				}
			}

			// Also check non-ui items
			if (this.getFingersItem() > 0) {
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(this.getFingersItem());
				if (item != null) {
					if (!item.isSpellscroll())
						items.add(item);
				}
			}

			if (this.getShouldersItem() > 0) {
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(this.getShouldersItem());
				if (item != null) {
					if (!item.isSpellscroll())
						items.add(item);
				}
			}

			if (this.getNeckItem() > 0) {
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(this.getNeckItem());
				if (item != null) {
					if (!item.isSpellscroll())
						items.add(item);
				}
			}

			if (this.getEarsItem() > 0) {
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(this.getEarsItem());
				if (item != null) {
					if (!item.isSpellscroll())
						items.add(item);
				}
			}
			
			if (this.getForearmsItem() > 0) {
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(this.getForearmsItem());
				if (item != null) {
					if (!item.isSpellscroll())
						items.add(item);
				}
			}
			
			if (this.getArmsItem() > 0) {
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(this.getArmsItem());
				if (item != null) {
					if (!item.isSpellscroll())
						items.add(item);
				}
			}
			
			if (this.getHandsItem() > 0) {
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(this.getHandsItem());
				if (item != null) {
					if (!item.isSpellscroll())
						items.add(item);
				}
			}

		} catch (CoreStateInitException e) {

		}

		return items;
	}

	@Override
	public boolean getSkillCheck(String skillname, int trivial) {
		SoliniaPlayerSkill skill = this.getSkill(skillname);

		if (skill == null)
			return false;

		float chance = (skill.getValue() - trivial) + 66;
		int over_trivial = skill.getValue() - trivial;

		if (over_trivial >= 0) {
			chance = 95.0f + ((float) (skill.getValue() - trivial) / 40.0f);
		} else if (chance < 5f) {
			chance = 5;
		} else if (chance > 95) {
			chance = 95;
		}

		float res = Utils.RandomBetween(0, 99);
		if (chance > res) {
			return true;
		}

		return false;
	}

	@Override
	public SoliniaWorld getSoliniaWorld() {
		try {
			return StateManager.getInstance().getConfigurationManager()
					.getWorld(this.getBukkitPlayer().getWorld().getName().toUpperCase());
		} catch (CoreStateInitException e) {
			return null;
		}
	}

	@Override
	public ConcurrentHashMap<Integer, SoliniaReagent> getReagents() {
		return reagentsPouch;
	}

	@Override
	public void setReagents(ConcurrentHashMap<Integer, SoliniaReagent> reagents) {
		this.reagentsPouch = reagents;
	}

	@Override
	public UUID getMotherId() {
		return motherId;
	}

	@Override
	public void setMotherId(UUID motherId) {
		this.motherId = motherId;
	}

	@Override
	public UUID getSpouseId() {
		return spouseId;
	}

	@Override
	public void setSpouseId(UUID spouseId) {
		this.spouseId = spouseId;
	}

	@Override
	public void sendFamilyTree() {
		this.getBukkitPlayer().sendMessage("Family Tree for " + this.getFullNameWithTitle());
		this.getBukkitPlayer().sendMessage("--------------------");
		String self = this.getFullName();
		String spouse = "";
		try {
			if (spouseId != null) {

				ISoliniaPlayer spousePlayer = StateManager.getInstance().getConfigurationManager()
						.getCharacterByCharacterUUID(spouseId);
				spouse = spousePlayer.getFullName();

			}

			this.getBukkitPlayer().sendMessage(self + " -> " + spouse);

			for (ISoliniaPlayer player : StateManager.getInstance().getConfigurationManager().getCharacters()) {
				if (player.getMotherId() == null)
					continue;

				if (player.getMotherId().toString().equals(getCharacterId().toString())) {
					this.getBukkitPlayer().sendMessage("Child: " + player.getFullName());
				} else {
					if (spouseId != null) {
						if (player.getMotherId().toString().equals(spouseId.toString())) {
							this.getBukkitPlayer().sendMessage("Child: " + player.getFullName());
						}
					}
				}
			}
		} catch (CoreStateInitException e) {

		}

	}

	@Override
	public boolean hasSufficientReagents(Integer itemId, Integer neededCount) {
		if (getReagents().get(itemId) == null)
			return false;

		if (getReagents().get(itemId).getQty() < neededCount)
			return false;

		return true;
	}
	

	@Override
	public boolean hasSufficientArrowReagents(int countNeeded) {
		int totalCount = 0;

		for (Entry<Integer, SoliniaReagent> entry : getReagents().entrySet()) {
			try {
				int itemId = entry.getKey();
				int count = entry.getValue().getQty();
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(itemId);
				if (item != null)
					if (item.isArrow()) {
						totalCount += count;
					}
			} catch (CoreStateInitException e) {

			}
		}

		if (totalCount >= countNeeded)
			return true;

		return false;
	}

	@Override
	public boolean hasSufficientBandageReagents(int countNeeded) {
		int totalCount = 0;

		for (Entry<Integer, SoliniaReagent> entry : getReagents().entrySet()) {
			try {
				int itemId = entry.getKey();
				int count = entry.getValue().getQty();
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(itemId);
				if (item != null)
					if (item.isBandage()) {
						totalCount += count;
					}
			} catch (CoreStateInitException e) {

			}
		}

		if (totalCount >= countNeeded)
			return true;

		return false;
	}
	
	@Override
	public List<Integer> getArrowReagents() {

		List<Integer> itemIds = new ArrayList<Integer>();
		for (Entry<Integer, SoliniaReagent> entry : getReagents().entrySet()) {
			try {
				int itemId = entry.getKey();
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(itemId);
				if (item != null)
					if (item.isArrow()) {
						itemIds.add(item.getId());
					}
			} catch (CoreStateInitException e) {

			}
		}

		return itemIds;
	}

	@Override
	public List<Integer> getBandageReagents() {

		List<Integer> itemIds = new ArrayList<Integer>();
		for (Entry<Integer, SoliniaReagent> entry : getReagents().entrySet()) {
			try {
				int itemId = entry.getKey();
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(itemId);
				if (item != null)
					if (item.isBandage()) {
						itemIds.add(item.getId());
					}
			} catch (CoreStateInitException e) {

			}
		}

		return itemIds;
	}

	@Override
	public void reduceReagents(Integer itemId, Integer reduceAmount) {
		if (getReagents().get(itemId) == null)
			return;

		getReagents().get(itemId).reduceQty(reduceAmount);
	}

	@Override
	public void setMainAndCleanup() {
		try {
			// Set alt to all other characters
			for (ISoliniaPlayer solPlayer : StateManager.getInstance().getConfigurationManager()
					.getCharactersByPlayerUUID(this.getBukkitPlayer().getUniqueId())) {
				solPlayer.setMain(false);

				// Set current to main
				setMain(true);
			}
		} catch (CoreStateInitException e) {

		}
	}

	// Used to schedule a HP update after an event
	// For example if a player has changed his items
	@Override
	public void scheduleUpdateMaxHp() {
		if (this.getBukkitPlayer() == null)
			return;

		final UUID playerUUID = this.getBukkitPlayer().getUniqueId();

		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("Solinia3Core"),
				new Runnable() {
					public void run() {
						try {
							ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(Bukkit.getPlayer(playerUUID));
							solPlayer.updateMaxHp();

						} catch (CoreStateInitException e) {
							// skip
						}
					}
				}, 20L);
	}

	@Override
	public List<Integer> getSpellBookItems() {
		return spellBookItems;
	}

	@Override
	public void setSpellBookItems(List<Integer> spellBookItems) {
		this.spellBookItems = spellBookItems;
	}

	@Override
	public void toggleAutoAttack() {
		try {
			StateManager.getInstance().getEntityManager().toggleAutoAttack(this.getBukkitPlayer());
		} catch (CoreStateInitException e) {

		}
	}

	@Override
	public boolean isGlowTargetting() {
		return glowTargetting;
	}

	@Override
	public void setGlowTargetting(boolean glowTargetting) {
		this.glowTargetting = glowTargetting;
	}

	@Override
	public boolean bindWound(ISoliniaLivingEntity solLivingEntity) {
		if (solLivingEntity == null)
			return false;

		if (solLivingEntity.getBukkitLivingEntity() == null)
			return false;
		
		if (solLivingEntity.getBukkitLivingEntity().isDead())
		{
			getBukkitPlayer().sendMessage("It is pointless to bind wounds for that which is dead");
			return false;
		}
		
		if (this.isBindWoundCountdown())
		{
			getBukkitPlayer().sendMessage("You are still applying your previous bandages, you must wait before attempting to bandage more (10 seconds to complete)");
			return false;
		}
		
		if (this.isFeignedDeath())
		{
			getBukkitPlayer().sendMessage("You cannot bind while feigned");
			return false;
		}
		
		try
		{
			ISoliniaLivingEntity playersolLivingEntity = SoliniaLivingEntityAdapter.Adapt((LivingEntity)this.getBukkitPlayer());
	
			
			if (solLivingEntity.getBukkitLivingEntity().getLocation().distance(getBukkitPlayer().getLocation()) > 4)
			{
				getBukkitPlayer().sendMessage("Your target is too far away to bind wound");
				return false;
			}
			
			// give some chance to try to bind wound early on in life
			// later on only do this after success
			boolean triedSkillIncrease = false;
			if (this.getSkill("BINDWOUND").getValue() < 30)
			{
				tryIncreaseSkill("BINDWOUND", 1);
				triedSkillIncrease = true;
			}
			
			int percent_base = 50;
			
			this.getSkill("BINDWOUND");
			
			String className = "UNKNOWN";
			if (getClassObj() != null)
				className = getClassObj().getName().toUpperCase();
			
			if (getSkill("BINDWOUND").getValue() > 200) {
				if (className.equals("MONK") || className.equals("BEASTLORD"))
					percent_base = 70;
				else if ((getLevel() > 50) && (className.equals("WARRIOR") || (className.equals("ROGUE") || (className.equals("CLERIC")))))
					percent_base = 70;
			}
	
			int percent_bonus = 0;
			// how could this possibly work for other classes
			//if (percent_base >= 70)
			//{
				if (playersolLivingEntity != null)
					percent_bonus = playersolLivingEntity.getMaxBindWound_SE();
			//}
	
			int max_percent = percent_base + percent_bonus;
			if (max_percent < 0)
				max_percent = 0;
			if (max_percent > 100)
				max_percent = 100;
	
			int max_hp = (int)Math.floor((solLivingEntity.getBukkitLivingEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * max_percent) / 100);
			if (max_hp > (int)Math.floor(solLivingEntity.getBukkitLivingEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()))
				max_hp = (int)Math.floor(solLivingEntity.getBukkitLivingEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
	
			if (solLivingEntity.getBukkitLivingEntity().getHealth() < solLivingEntity.getBukkitLivingEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() && solLivingEntity.getBukkitLivingEntity().getHealth() < max_hp) {
				int bindhps = 3; // base bind hp
				if (percent_base >= 70)
					bindhps = (getSkill("BINDWOUND").getValue() * 4) / 10; // 8:5 skill-to-hp ratio
				else if (getSkill("BINDWOUND").getValue() >= 12)
					bindhps = getSkill("BINDWOUND").getValue() / 4; // 4:1 skill-to-hp ratio
	
				int bonus_hp_percent = 0;
				//if (percent_base >= 70)
				//{
					if (playersolLivingEntity != null)
						bonus_hp_percent = playersolLivingEntity.getBindWound_SE();
				//}
				
				//getBukkitPlayer().sendMessage("Your percentbase was " + percent_base);
				//getBukkitPlayer().sendMessage("Your spell/aa effects add a binding calculation was " + bindhps + " * " + bonus_hp_percent  + " / 100");
				int spellModifierBenefit = (bindhps * bonus_hp_percent) / 100;
				//getBukkitPlayer().sendMessage("Your spell/aa effects add a binding addition of " + spellModifierBenefit + " hp");

				
				bindhps += spellModifierBenefit;
	
				if (bindhps < 3)
					bindhps = 3;

				double originalHealth = solLivingEntity.getBukkitLivingEntity().getHealth();
				
				bindhps += solLivingEntity.getBukkitLivingEntity().getHealth();
				if (bindhps > max_hp)
					bindhps = max_hp;
	
				
				
				int amount = bindhps;
				if (amount > solLivingEntity.getBukkitLivingEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()) {
					amount = (int) Math.round(solLivingEntity.getBukkitLivingEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
				}
	
				if (amount < 0)
					amount = 0;
				
				double boundHealth = amount - originalHealth;
				
				if (triedSkillIncrease == false)
				{
					tryIncreaseSkill("BINDWOUND", 1);
					triedSkillIncrease = true;
				}
				
				LocalDateTime datetime = LocalDateTime.now();
				Timestamp nowtimestamp = Timestamp.valueOf(datetime);
				StateManager.getInstance().getEntityManager().setLastBindwound(this.getBukkitPlayer().getUniqueId(),nowtimestamp);
				
				getBukkitPlayer().sendMessage("You bind " + solLivingEntity.getName() + "'s wounds for " + (int)boundHealth + " hp");
				if (solLivingEntity.getBukkitLivingEntity() instanceof Player && !solLivingEntity.getBukkitLivingEntity().getUniqueId().toString().equals(getBukkitPlayer().getUniqueId().toString()))
					solLivingEntity.getBukkitLivingEntity().sendMessage("Your wounds are being bound by " + getBukkitPlayer().getDisplayName());
				
				if (!solLivingEntity.getBukkitLivingEntity().isDead())
				{
					solLivingEntity.setHealth(amount);
				}
				return true;
			}
			else {
				getBukkitPlayer().sendMessage("You cannot bind wounds above " + max_percent + "% hitpoints");
				if (solLivingEntity.getBukkitLivingEntity() instanceof Player && !solLivingEntity.getBukkitLivingEntity().getUniqueId().toString().equals(getBukkitPlayer().getUniqueId().toString()))
					solLivingEntity.getBukkitLivingEntity().sendMessage("You cannot have your wounds bound above " + max_percent + "% hitpoints");
				
				return false;
			}
		
		} catch (CoreStateInitException e)
		{
		}

		return false;
	}

	private boolean isBindWoundCountdown() {
		try
		{
			Timestamp expiretimestamp = StateManager.getInstance().getEntityManager().getLastBindwound().get(this.getBukkitPlayer().getUniqueId());
			if (expiretimestamp != null) {
				LocalDateTime datetime = LocalDateTime.now();
				Timestamp nowtimestamp = Timestamp.valueOf(datetime);
				Timestamp mintimestamp = Timestamp.valueOf(expiretimestamp.toLocalDateTime().plus(10, ChronoUnit.SECONDS));
	
				if (nowtimestamp.before(mintimestamp))
					return true;
			}
		} catch (CoreStateInitException e)
		{
			
		}
		
		return false;
	}

	@Override
	public Double getPendingXp() {
		if (this.pendingXp < 0)
			this.pendingXp = 0d;
		return this.pendingXp;
	}

	@Override
	public void setPendingXp(Double pendingXp) {
		if (pendingXp < 0)
			pendingXp = 0d;
		this.pendingXp = pendingXp;
	}

	@Override
	public void addXpToPendingXp(Double experience) {
		if (experience < 0)
			experience = 0d;
		this.pendingXp += experience;
	}

	@Override
	public boolean isShowDiscord() {
		return showDiscord;
	}

	@Override
	public void setShowDiscord(boolean showDiscord) {
		this.showDiscord = showDiscord;
	}

	@Override
	public int getForearmsItem() {
		return forearmsItem;
	}

	@Override
	public void setForearmsItem(int forearmsItem) {
		this.forearmsItem = forearmsItem;
	}

	@Override
	public int getArmsItem() {
		return armsItem;
	}

	@Override
	public void setArmsItem(int armsItem) {
		this.armsItem = armsItem;
	}

	@Override
	public int getHandsItem() {
		return handsItem;
	}

	@Override
	public void setHandsItem(int handsItem) {
		this.handsItem = handsItem;
	}

	@Override
	public int getWaistItem() {
		return waistItem;
	}

	@Override
	public void setWaistItem(int waistItem) {
		this.waistItem = waistItem;
	}

	
	@Override
	public String getFingersItemInstance() {
		return fingersItemInstance;
	}

	@Override
	public void setFingersItemInstance(String fingersItemInstance) {
		this.fingersItemInstance = fingersItemInstance;
	}

	@Override
	public String getShouldersItemInstance() {
		return shouldersItemInstance;
	}

	@Override
	public void setShouldersItemInstance(String shouldersItemInstance) {
		this.shouldersItemInstance = shouldersItemInstance;
	}

	@Override
	public String getNeckItemInstance() {
		return neckItemInstance;
	}

	@Override
	public void setNeckItemInstance(String neckItemInstance) {
		this.neckItemInstance = neckItemInstance;
	}

	@Override
	public String getEarsItemInstance() {
		return earsItemInstance;
	}

	@Override
	public void setEarsItemInstance(String earsItemInstance) {
		this.earsItemInstance = earsItemInstance;
	}

	@Override
	public String getForearmsItemInstance() {
		return forearmsItemInstance;
	}

	@Override
	public void setForearmsItemInstance(String forearmsItemInstance) {
		this.forearmsItemInstance = forearmsItemInstance;
	}

	@Override
	public String getArmsItemInstance() {
		return armsItemInstance;
	}

	@Override
	public void setArmsItemInstance(String armsItemInstance) {
		this.armsItemInstance = armsItemInstance;
	}

	@Override
	public String getHandsItemInstance() {
		return handsItemInstance;
	}

	@Override
	public void setHandsItemInstance(String handsItemInstance) {
		this.handsItemInstance = handsItemInstance;
	}
	
	@Override
	public String getWaistItemInstance() {
		return waistItemInstance;
	}

	@Override
	public void setWaistItemInstance(String waistItemInstance) {
		this.waistItemInstance = waistItemInstance;
	}


	@Override
	public ISoliniaLivingEntity getSoliniaLivingEntity() {
		try {
			return SoliniaLivingEntityAdapter.Adapt((LivingEntity)getBukkitPlayer());
		} catch (CoreStateInitException e) {
			return null;
		}
	}

	@Override
	public int getOathId() {
		return oathId;
	}

	@Override
	public void setOathId(int oathId) {
		this.oathId = oathId;
	}

	@Override
	public Personality getPersonality() {
		return personality;
	}

	@Override
	public void setPersonality(Personality personality) {
		this.personality = personality;
	}

	@Override
	public void StopSinging() {
		ISoliniaLivingEntity solLivingEntity = getSoliniaLivingEntity();
		if (solLivingEntity != null)
			solLivingEntity.StopSinging();
	}

	@Override
	public boolean isSongsEnabled() {
		return songsEnabled;
	}

	@Override
	public void setSongsEnabled(boolean songsEnabled) {
		this.songsEnabled = songsEnabled;
	}

	@Override
	public void increaseMonthlyVote(Integer amount) {
		Calendar now = Calendar.getInstance();
		String month = now.get(Calendar.YEAR) + "-" + now.get(Calendar.MONTH);
		if (this.monthlyVote.get(month) == null)
		{
			this.monthlyVote.put(month, 0);
		}
		this.monthlyVote.put(month, this.monthlyVote.get(month)+amount);
	}
	
	@Override
	public Integer getMonthlyVote() {
		Calendar now = Calendar.getInstance();
		String month = now.get(Calendar.YEAR) + "-" + now.get(Calendar.MONTH);
		if (this.monthlyVote.get(month) == null)
		{
			return 0;
		}
		return this.monthlyVote.get(month);
	}

	@Override
	public void setEquipSlotItem(com.solinia.solinia.Models.EquipmentSlot slot, int itemId) {
		
		
		switch(slot)
		{
			case Arms:
				setArmsItem(0);
				break;
			case Ears:
				setEarsItem(0);
				break;
			case Fingers:
				setFingersItem(0);
				break;
			case Forearms:
				setForearmsItem(0);
				break;
			case Hands:
				setHandsItem(0);
				break;
			case Neck:
				setNeckItem(0);
				break;
			case Shoulders:
				setShouldersItem(0);
				break;
			case Waist:
				setWaistItem(0);
				break;
			case None:
			default:
				break;
		}
	}

	@Override
	public String getBase64InventoryContents() {
		return base64InventoryContents;
	}

	@Override
	public void setBase64InventoryContents(String base64InventoryContents) {
		this.base64InventoryContents = base64InventoryContents;
	}

	@Override
	public String getBase64ArmorContents() {
		return base64ArmorContents;
	}

	@Override
	public void setBase64ArmorContents(String base64ArmorContents) {
		this.base64ArmorContents = base64ArmorContents;
	}
	
	@Override
	public ItemStack[] getStoredArmorContents() {
		if (getBase64ArmorContents() == null || getBase64ArmorContents().equals(""))
			return new ItemStack[0];
		
		try
		{
		String yaml = new String(Base64.decodeBase64(getBase64ArmorContents().getBytes()));
		return ItemStackUtils.itemStackArrayFromYamlString(yaml);
		} catch (Exception e)
		{
			System.out.println("Exception converting base64 to itemstack array [armorcontents] for player " + getBukkitPlayer().getName() + ": " + getBase64InventoryContents());
			e.printStackTrace();
			return new ItemStack[0];
		}
	}
	
	@Override
	public ItemStack[] getStoredInventoryContents() {
		if (getBase64InventoryContents() == null || getBase64InventoryContents().equals(""))
			return new ItemStack[0];
		
		try
		{
			String yaml = new String(Base64.decodeBase64(getBase64InventoryContents().getBytes()));
			return ItemStackUtils.itemStackArrayFromYamlString(yaml);
		} catch (Exception e)
		{
			System.out.println("Exception converting base64 to itemstack array [inventorycontents] for player " + getBukkitPlayer().getName() + ": " + getBase64InventoryContents());
			e.printStackTrace();
			return new ItemStack[0];
		}
	}

	@Override
	public void storeInventoryContents() {
		this.setBase64InventoryContents(new String(Base64.encodeBase64(ItemStackUtils.itemStackArrayToYamlString(this.getBukkitPlayer().getInventory().getContents()).getBytes())));
	}

	@Override
	public void storeArmorContents() {
		this.setBase64ArmorContents(new String(Base64.encodeBase64(ItemStackUtils.itemStackArrayToYamlString(this.getBukkitPlayer().getInventory().getArmorContents()).getBytes())));
	}

	@Override
	public void doRegenTick() {
		if (getBukkitPlayer().isDead())
			return;
		
		// Apply Crouch Mana Regen Bonus
		int manaregen = 1;
		
		int sleephpregen = 0;
		int sleepmpregen = 0;
		// Sleep regen
		if (getBukkitPlayer().isSleeping())
		{
			sleephpregen += 50;
			sleepmpregen += 50;
		}
		
		manaregen += sleepmpregen;
		
		// a players mana regen based on if they are meditating (sneaking)
		manaregen += getPlayerMeditatingManaBonus();
		
		ISoliniaAAAbility aa = null;
		try
		{
			if(getAARanks().size() > 0)
			{
				for(ISoliniaAAAbility ability : StateManager.getInstance().getConfigurationManager().getAAbilitiesBySysname("MENTALCLARITY"))
				{
					if (!hasAAAbility(ability.getId()))
						continue;
					
					aa = ability;
				}
			}
			
		} catch (CoreStateInitException e)
		{
			
		}
		
		int aamanaregenrank = 0;
		
		if (aa != null)
		{
			if(getAARanks().size() > 0)
			aamanaregenrank = Utils.getRankPositionOfAAAbility(getBukkitPlayer(), aa);
			manaregen += aamanaregenrank;
		}
		
		ISoliniaAAAbility emaa = null;
		try
		{
			if(getAARanks().size() > 0)
			{
				for(ISoliniaAAAbility ability : StateManager.getInstance().getConfigurationManager().getAAbilitiesBySysname("MENTALCLARITY"))
				{
					if (!hasAAAbility(ability.getId()))
						continue;
					
					emaa = ability;
				}
			}
		} catch (CoreStateInitException e)
		{
			
		}
		
		int emaamanaregenrank = 0;
		
		if (emaa != null)
		{
			if(getAARanks().size() > 0)
			emaamanaregenrank = Utils.getRankPositionOfAAAbility(getBukkitPlayer(), emaa);
			manaregen += emaamanaregenrank;
		}

		// Hp and Mana Regen from Items
		int hpregen = 0;
		
		ISoliniaAAAbility hpaa = null;
		try
		{
			if(getAARanks().size() > 0)
			{
				for(ISoliniaAAAbility ability : StateManager.getInstance().getConfigurationManager().getAAbilitiesBySysname("INNATEREGENERATION"))
				{
					if (!hasAAAbility(ability.getId()))
						continue;
					
					hpaa = ability;
				}
			}
		} catch (CoreStateInitException e)
		{
			
		}
		
		int aahpregenrank = 0;
		
		if (hpaa != null)
		{
			aahpregenrank = Utils.getRankPositionOfAAAbility(getBukkitPlayer(), hpaa);
			hpregen += aahpregenrank;
		}
		
		hpregen += sleephpregen;

		// Process HP Regeneration
		if (hpregen > 0) {
			int amount = (int) Math.round(getBukkitPlayer().getHealth()) + hpregen;
			if (amount > getBukkitPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()) {
				amount = (int) Math.round(getBukkitPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
			}
			
			if (amount < 0)
				amount = 0;

			if (!getBukkitPlayer().isDead())
			getSoliniaLivingEntity().setHealth(amount);
		}
		
		// Process Mana Regeneration
		//System.out.println(player.getName() + " was found to have " + manaregen + " mana regen");
		increasePlayerMana(manaregen);
	}
	
	private int getPlayerMeditatingManaBonus() {
		int manaregen = 0;
		if (isMeditating())
		{
			SoliniaPlayerSkill meditationskill = getSkill("MEDITATION");
			int bonusmana = 3 + (meditationskill.getValue() / 15);

			manaregen += bonusmana;

			// apply meditation skill increase
			Random r = new Random();
			int randomInt = r.nextInt(100) + 1;
			if (randomInt > 90) {
				int currentvalue = 0;
				SoliniaPlayerSkill skill = getSkill("MEDITATION");
				if (skill != null) {
					currentvalue = skill.getValue();
				}

				if ((currentvalue + 1) <= getSkillCap("MEDITATION")) {
					setSkill("MEDITATION", currentvalue + 1);
				}

			}
		}
		
		return manaregen;
	}

	@Override
	public void doEquipmentRegenTick(ISoliniaItem item) {
		if (getBukkitPlayer().isDead())
			return;
		
		// Process HP Regeneration
		if (item.getHpregen() > 0) {
			int amount = (int) Math.round(getBukkitPlayer().getHealth()) + item.getHpregen();
			if (amount > getBukkitPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()) {
				amount = (int) Math.round(getBukkitPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
			}
			
			if (amount < 0)
				amount = 0;

			if (!getBukkitPlayer().isDead())
				getSoliniaLivingEntity().setHealth(amount);
		}
		
		if (item.getMpregen() > 0)
			increasePlayerMana(item.getMpregen());
	}

}
