package com.solinia.solinia.Models;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
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
import com.solinia.solinia.Interfaces.ISoliniaAlignment;
import com.solinia.solinia.Interfaces.ISoliniaClass;
import com.solinia.solinia.Interfaces.ISoliniaGroup;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Interfaces.ISoliniaNPCEventHandler;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Interfaces.ISoliniaRace;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Models.SoliniaAlignmentChunk;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Utils.ItemStackUtils;
import com.solinia.solinia.Utils.ScoreboardUtils;
import com.solinia.solinia.Utils.SpellTargetType;
import com.solinia.solinia.Utils.Utils;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.PacketPlayOutAnimation;

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
	private UUID fealty;
	private UUID voteemperor;
	private String specialisation = "";
	private boolean vampire = false;
	private boolean main = true;
	private int inspiration = 0;
	private Timestamp experienceBonusExpires;
	private boolean oocEnabled = true;
	private String bindPoint;
	private int fingersItem = 0;
	private int shouldersItem = 0;
	private int neckItem = 0;
	private int earsItem = 0;
	private List<Integer> spellBookItems = new ArrayList<Integer>();
	private ConcurrentHashMap<Integer, Integer> reagents = new ConcurrentHashMap<Integer, Integer>();
	private boolean glowTargetting = true;
	private Double pendingXp = 0d;

	@Override
	public List<UUID> getIgnoredPlayers() {
		return ignoredPlayers;
	}

	@Override
	public boolean hasIgnored(UUID uuid) {
		return ignoredPlayers.contains(uuid);
	}

	@Override
	public void setIgnoredPlayers(List<UUID> ignoredPlayers) {
		this.ignoredPlayers = ignoredPlayers;
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
		String king = "";
		if (isRacialKingChild()) {
			if (this.getGender().equals("MALE"))
				king = "Prince ";
			else
				king = "Princess ";
		}

		if (isAlignmentEmperorChild()) {
			if (this.getGender().equals("MALE"))
				king = "Prince ";
			else
				king = "Princess ";
		}

		if (isRacialKingSpouse()) {
			if (this.getGender().equals("MALE"))
				king = "Consort ";
			else
				king = "Queen ";
		}

		if (isAlignmentEmperorSpouse()) {
			if (this.getGender().equals("MALE"))
				king = "Consort ";
			else
				king = "Empress ";
		}

		if (isRacialKing()) {
			if (this.getGender().equals("MALE"))
				king = "King ";
			else
				king = "Queen ";
		}

		if (isAlignmentEmperor()) {
			if (this.getGender().equals("MALE"))
				king = "Emperor ";
			else
				king = "Empress ";
		}

		String displayName = king + getFullName();
		if (getTitle() != null && !(getTitle().equals(""))) {
			displayName += " " + getTitle();
		}

		return displayName;
	}

	@Override
	public boolean isAlignmentEmperorChild() {
		if (this.getMotherId() != null) {
			try {
				ISoliniaPlayer solPlayer = StateManager.getInstance().getConfigurationManager()
						.getCharacterByCharacterUUID(this.getMotherId());
				if (solPlayer == null)
					return false;
				if (solPlayer.isAlignmentEmperor() || solPlayer.isAlignmentEmperorSpouse()) {
					return true;
				}
			} catch (CoreStateInitException e) {

			}

		}
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRacialKingChild() {

		if (this.getMotherId() != null) {
			try {
				ISoliniaPlayer solPlayer = StateManager.getInstance().getConfigurationManager()
						.getCharacterByCharacterUUID(this.getMotherId());
				if (solPlayer == null)
					return false;
				if (solPlayer.isRacialKing() || solPlayer.isRacialKingSpouse()) {
					return true;
				}
			} catch (CoreStateInitException e) {

			}

		}
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAlignmentEmperorSpouse() {
		if (this.getSpouseId() != null) {
			try {
				ISoliniaPlayer solPlayer = StateManager.getInstance().getConfigurationManager()
						.getCharacterByCharacterUUID(this.getSpouseId());
				if (solPlayer == null)
					return false;
				if (solPlayer.isAlignmentEmperor()) {
					return true;
				}
			} catch (CoreStateInitException e) {

			}

		}
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRacialKingSpouse() {
		if (this.getSpouseId() != null) {
			try {
				ISoliniaPlayer solPlayer = StateManager.getInstance().getConfigurationManager()
						.getCharacterByCharacterUUID(this.getSpouseId());
				if (solPlayer == null)
					return false;
				if (solPlayer.isRacialKing()) {
					return true;
				}
			} catch (CoreStateInitException e) {

			}
		}
		return false;
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
				getBukkitPlayer().setMaxHealth(calculatedhp);
				getBukkitPlayer().setHealthScaled(true);
				getBukkitPlayer().setHealthScale(40D);
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
	public SoliniaZone isInZone() {
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
					StateManager.getInstance().getChannelManager().getDefaultDiscordChannel(),
					getFullName() + " has reached new heights as a level " + (int) Math.floor(newlevel) + " "
							+ classname.toLowerCase() + "!");
			getBukkitPlayer().sendMessage(ChatColor.DARK_PURPLE + "* You gained a level (" + newlevel + ")!");

			if (newlevel < 6)
				getBukkitPlayer().sendMessage(ChatColor.GRAY
						+ "[Hint] Bored of this class? You rename this player and create a new class with the /character command");

			getBukkitPlayer().getWorld().playEffect(getBukkitPlayer().getLocation(), Effect.FIREWORK_SHOOT, 1);

			if (getGroup() != null) {
				for (UUID uuid : getGroup().getMembers()) {
					ScoreboardUtils.UpdateGroupScoreboard(uuid, getGroup());
				}
			}

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

			if (getGroup() != null) {
				for (UUID uuid : getGroup().getMembers()) {
					ScoreboardUtils.UpdateGroupScoreboard(uuid, getGroup());
				}
			}

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
	public void emote(String string) {
		StateManager.getInstance().getChannelManager().sendToLocalChannel(this, string);
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
		StateManager.getInstance().getChannelManager().sendToGlobalChannelDecorated(this, string);
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
		StateManager.getInstance().getChannelManager().sendToLocalChannelDecorated(this, string);
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
	public void interact(Plugin plugin, PlayerInteractEvent event) {
		// TODO Auto-generated method stub
		ItemStack itemstack = event.getItem();

		try {
			// this is the item not in offhand
			if (event.getHand() == EquipmentSlot.HAND
					&& (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
				if (itemstack == null) {
					return;
				}

				if ((!Utils.IsSoliniaItem(itemstack)))
					return;

				// We have our custom item id, lets check it exists
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(itemstack);

				if (item == null) {
					return;
				}

				// Start applying an augmentation
				if ((event.getAction().equals(Action.RIGHT_CLICK_AIR)
						|| event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && item.isAugmentation()) {
					if (StateManager.getInstance().getPlayerManager()
							.getApplyingAugmentation(event.getPlayer().getUniqueId()) == null
							|| StateManager.getInstance().getPlayerManager()
									.getApplyingAugmentation(event.getPlayer().getUniqueId()) == 0) {
						StateManager.getInstance().getPlayerManager()
								.setApplyingAugmentation(event.getPlayer().getUniqueId(), item.getId());
						event.getPlayer().sendMessage("* Applying " + item.getDisplayname()
								+ " to an item, please right click the item you wish to apply this augmentation to. . To cancel applying, right click while holding this item again");
					} else {
						StateManager.getInstance().getPlayerManager()
								.setApplyingAugmentation(event.getPlayer().getUniqueId(), 0);
						event.getPlayer().sendMessage("* Cancelled applying augmentation");
					}
					return;
				}

				// Min level check
				if ((event.getAction().equals(Action.RIGHT_CLICK_AIR)
						|| event.getAction().equals(Action.RIGHT_CLICK_BLOCK))) 
				{
					ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(event.getPlayer());
					
					if (item.getMinLevel() > solPlayer.getLevel())
					{
						event.getPlayer().sendMessage("This item requires minimum level: " + item.getMinLevel());
						return;
					}
				}
				
				if ((event.getAction().equals(Action.RIGHT_CLICK_AIR)
						|| event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && item.isThrowing()) {

					if (itemstack.getAmount() > 1) {
						event.getPlayer().sendMessage(
								"Tried to use an entire stack at once! Cancelling, did you forget to split them?");
						return;
					}

					LivingEntity targetmob = Utils.getTargettedLivingEntity(event.getPlayer(), 50);
					if (targetmob != null && !targetmob.getUniqueId().equals(event.getPlayer().getUniqueId())) {
						if (item.useItemOnEntity(event.getPlayer(), targetmob, false) == true) {
							event.getPlayer().setItemInHand(null);
							event.getPlayer().updateInventory();
							return;
						} else {
							return;
						}
					}
				}

				// This now all uses the targetting system
				// If the player is using a self only spell or AE switch target to themselves if
				// they have no target right now
				if (StateManager.getInstance().getEntityManager().getEntityTarget(getBukkitPlayer()) == null) {
					if (item.getAbilityid() > 0) {
						ISoliniaSpell spell = StateManager.getInstance().getConfigurationManager()
								.getSpell(item.getAbilityid());
						if (spell != null) {
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
							} else if (Utils.getSpellTargetType(spell.getTargettype()).equals(SpellTargetType.Pet))
							{
								LivingEntity pet = StateManager.getInstance().getEntityManager().getPet(getBukkitPlayer());
								if (pet != null)
								{
									StateManager.getInstance().getEntityManager().setEntityTarget(getBukkitPlayer(),
										pet);
								} else {
									getBukkitPlayer().sendMessage(
											"* You must select a target (shift+left click with spell or use /ts for group or shift-f for self");
								}
							} else {
								getBukkitPlayer().sendMessage(
										"* You must select a target (shift+left click with spell or use /ts for group or shift-f for self");
								return;
							}
						} else {
							getBukkitPlayer().sendMessage(
									"* You must select a target (shift+left click with spell or use /ts for group or shift-f for self");
							return;
						}
					}
				}

				LivingEntity targetmob = StateManager.getInstance().getEntityManager()
						.getEntityTarget(getBukkitPlayer());

				// we should probably check line of sight here for detrimentals, or maybe in the
				// spell

				if ((event.getAction().equals(Action.RIGHT_CLICK_AIR)
						|| event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && item.isPetControlRod()) {
					if (targetmob != null) {
						item.useItemOnEntity(event.getPlayer(), targetmob, false);
						return;
					} else {
						getBukkitPlayer().sendMessage(
								"* You must select a target (shift+left click with pet control rod or use /ts for group or shift-f for self");
						return;
					}
				}

				if (item.getAbilityid() < 1) {
					return;
				}

				if (ItemStackUtils.isPotion(itemstack)) {
					// Handled by on consume event
					return;
				}

				ISoliniaSpell spell = StateManager.getInstance().getConfigurationManager()
						.getSpell(item.getAbilityid());

				// Only applies to consumable items
				if ((event.getAction().equals(Action.RIGHT_CLICK_AIR)
						|| event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && item.isConsumable()) {

					if (itemstack.getAmount() > 1) {
						event.getPlayer().sendMessage(
								"Tried to use an entire stack at once! Cancelling, did you forget to split them?");
						return;
					}

					if (targetmob != null) {
						item.useItemOnEntity(event.getPlayer(), targetmob, true);
						event.getPlayer().setItemInHand(null);
						event.getPlayer().updateInventory();
						return;
					} else {
						getBukkitPlayer().sendMessage(
								"* You must select a target (shift+left click with consumable or use /ts for group or shift-f for self");
						return;
					}
				}

				// Only applies to non-consumable items
				if ((event.getAction().equals(Action.RIGHT_CLICK_AIR)
						|| event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && !item.isConsumable()
						&& !itemstack.getType().equals(Material.ENCHANTED_BOOK)) {
					if (targetmob != null) {
						item.useItemOnEntity(event.getPlayer(), targetmob, true);
						return;
					} else {
						getBukkitPlayer().sendMessage(
								"* You must select a target (shift+left click with usable item or use /ts for group or shift-f for self");
						return;
					}
				}

				// Only applies to spell effects
				if (!itemstack.getType().equals(Material.ENCHANTED_BOOK)) {
					return;
				}

				// Reroute action depending on target
				if (event.getAction().equals(Action.RIGHT_CLICK_AIR)
						|| event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
					// This now attempts casting

					ISoliniaLivingEntity solentity = SoliniaLivingEntityAdapter.Adapt((LivingEntity) event.getPlayer());
					if (spell.getActSpellCost(solentity) > SoliniaPlayerAdapter.Adapt(event.getPlayer()).getMana()) {
						event.getPlayer().sendMessage(
								ChatColor.GRAY + "Insufficient Mana  [E] (Hold crouch or use /trance to meditate)");
						return;
					}

					if (StateManager.getInstance().getEntityManager().getEntitySpellCooldown(event.getPlayer(),
							spell.getId()) != null) {
						LocalDateTime datetime = LocalDateTime.now();
						Timestamp nowtimestamp = Timestamp.valueOf(datetime);
						Timestamp expiretimestamp = StateManager.getInstance().getEntityManager()
								.getEntitySpellCooldown(event.getPlayer(), spell.getId());

						if (expiretimestamp != null)
							if (!nowtimestamp.after(expiretimestamp)) {
								event.getPlayer()
										.sendMessage("You do not have enough willpower to cast " + spell.getName()
												+ " (Wait: "
												+ ((expiretimestamp.getTime() - nowtimestamp.getTime()) / 1000) + "s");
								return;
							}
					}

					startCasting(plugin, spell, event.getPlayer(), item);
				}
			}
		} catch (CoreStateInitException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void startCasting(Plugin plugin, ISoliniaSpell spell, Player player, ISoliniaItem item) {
		try {

			CastingSpell castingSpell = new CastingSpell(player.getUniqueId(), spell.getId(), item.getId());
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

		if (castingSpell.getItem() == null)
			return;

		doCastSpellItem(castingSpell.getSpell(), player, castingSpell.getItem());
	}

	@Override
	public void doCastSpellItem(ISoliniaSpell spell, Player player, ISoliniaItem spellSourceItem) {
		if (spell.isAASpell() && !canUseAASpell(spell)) {
			player.sendMessage("You require the correct AA to use this spell");
			return;
		}

		LivingEntity targetmob = null;

		// This now all uses the targetting system
		// If the player is using a self only spell or AE switch target to themselves if
		// they have no target right now
		try {
			if (StateManager.getInstance().getEntityManager().getEntityTarget(getBukkitPlayer()) == null) {
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
							"* You must select a target (shift+left click with spell or use /ts for group or shift-f for self");
					return;
				}
			}

			targetmob = StateManager.getInstance().getEntityManager().getEntityTarget(getBukkitPlayer());
		} catch (CoreStateInitException e) {

		}

		if (spell.getAllowedClasses().size() > 0) {
			if (getClassObj() == null) {
				player.sendMessage(ChatColor.GRAY + " * This item cannot be used by your profession");
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
				player.sendMessage(ChatColor.GRAY + " * This item can only be used by " + professions);
				return;
			} else {
				if (foundlevel > 0) {
					Double level = (double) getLevel();
					if (level < foundlevel) {
						player.sendMessage(ChatColor.GRAY + " * This item requires level " + foundlevel);
						return;
					}
				}
			}

		}

		try {
			ISoliniaLivingEntity solentity = SoliniaLivingEntityAdapter.Adapt((LivingEntity) player);
			if (solentity == null)
				return;

			if (spell.getActSpellCost(solentity) > SoliniaPlayerAdapter.Adapt(player).getMana()) {
				player.sendMessage(ChatColor.GRAY + "Insufficient Mana [E]  (Hold crouch or use /trance to meditate)");
				return;
			}

			if (!spell.isBardSong()) {
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

			if (!checkDoesntFizzle(spell)) {
				emote("* " + getFullName() + "'s spell fizzles");
				SoliniaPlayerAdapter.Adapt(player).reducePlayerMana(spell.getActSpellCost(solentity));
				return;
			}
		} catch (CoreStateInitException e) {
			return;
		}

		// Reroute action depending on target
		//
		try {
			if (targetmob != null) {
				boolean success = spellSourceItem.useItemOnEntity(player, targetmob, false);
				if (success == true) {
					tryIncreaseSkill(Utils.getSkillType(spell.getSkill()).name().toUpperCase(), 1);
				}
				return;
			} else {
				boolean success = spellSourceItem.useItemOnEntity(player, player, false);
				if (success == true) {
					tryIncreaseSkill(Utils.getSkillType(spell.getSkill()).name().toUpperCase(), 1);
				}
				return;
			}
		} catch (CoreStateInitException e) {
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
		if (getBukkitPlayer().isOp())
			return true;

		// todo fizzle free features

		int no_fizzle_level = 0;
		ISoliniaAAAbility aa = null;
		try {
			aa = StateManager.getInstance().getConfigurationManager()
					.getFirstAAAbilityBySysname("SPELLCASTINGEXPERTISE");
		} catch (CoreStateInitException e) {

		}

		if (aa != null) {
			ISoliniaAARank AArank = Utils.getRankOfAAAbility(getBukkitPlayer(), aa);
			if (AArank != null) {
				for (SoliniaAARankEffect rankEffect : AArank.getEffects()) {
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
						if (rankEffect.getBase1() > no_fizzle_level)
							no_fizzle_level = rankEffect.getBase1();
					}
				}
			}
		} catch (CoreStateInitException e) {

		}

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

		if (Bukkit.getEntity(interaction) instanceof Wolf) {
			Wolf w = (Wolf) Bukkit.getEntity(interaction);
			if (w.getOwner() != null)
				return;
		}

		this.interaction = interaction;

		if (npc != null) {
			this.getBukkitPlayer().sendMessage(ChatColor.GRAY + "* You are now interacting with "
					+ Bukkit.getEntity(interaction).getName() + " [" + npc.getId()
					+ "] - Anything you type will be heared by the NPC and possibly responded to. Words in pink are trigger words you can type");

			if (npc.getMerchantid() > 0) {
				try {
					StateManager.getInstance().getEntityManager().getLivingEntity((LivingEntity) e)
							.say("i have a [" + ChatColor.LIGHT_PURPLE + "SHOP" + ChatColor.AQUA
									+ "] available if you are interested in buying or selling something",
									getBukkitPlayer(), true);
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

					TextComponent tc = new TextComponent();
					tc.setText(ChatColor.YELLOW + "[QUEST] ");
					TextComponent tc2 = new TextComponent();
					tc2.setText(ChatColor.YELLOW + "- Click here to give " + item.getDisplayname() + ChatColor.RESET);
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

		return null;
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
		if (newValue > 1500) {
			newValue = 1500;
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
		if (newValue < -1500) {
			newValue = -1500;
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
				ChatColor.YELLOW + " * You have received a new Quest [" + quest.getQuest().getName() + "]!");
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

	@Override
	public boolean isMeditating() {
		try {
			if (getBukkitPlayer().isSneaking()
					|| StateManager.getInstance().getEntityManager().getTrance(getUUID()) == true) {
				return true;
			}
		} catch (CoreStateInitException e) {
			return false;
		}

		return false;
	}

	@Override
	public void setSkills(List<SoliniaPlayerSkill> skills) {
		this.skills = skills;
	}

	@Override
	public void setFealty(UUID uniqueId) {
		Entity fealtyTo = Bukkit.getEntity(uniqueId);

		if (uniqueId.equals(this.getUUID()))
			return;

		if (fealtyTo == null)
			return;

		if (!(fealtyTo instanceof Player))
			return;

		fealty = fealtyTo.getUniqueId();
		getBukkitPlayer().sendMessage("* You have sworn fealty to " + fealtyTo.getCustomName() + "!");
	}

	@Override
	public UUID getFealty() {
		return fealty;
	}

	@Override
	public boolean isRacialKing() {
		if (!isMain())
			return false;

		try {
			for (ISoliniaRace race : StateManager.getInstance().getConfigurationManager().getRaces()) {
				if (race.getKing() == null)
					continue;

				if (race.getKing().equals(getUUID()))
					return true;
			}
		} catch (CoreStateInitException e) {
			return false;
		}

		return false;
	}

	@Override
	public boolean isAlignmentEmperor() {
		if (!isMain())
			return false;

		try {
			if (getRace() == null)
				return false;

			ISoliniaAlignment alignment = StateManager.getInstance().getConfigurationManager()
					.getAlignment(getRace().getAlignment());

			if (alignment == null)
				return false;

			if (alignment.getEmperor() == null)
				return false;

			if (alignment.getEmperor().equals(getUUID()))
				return true;
		} catch (CoreStateInitException e) {
			return false;
		}

		return false;
	}

	@Override
	public UUID getVoteEmperor() {
		return voteemperor;
	}

	@Override
	public void setVoteEmperor(UUID uniqueId) {
		Entity fealtyTo = Bukkit.getEntity(uniqueId);

		if (uniqueId.equals(this.getUUID()))
			return;

		if (fealtyTo == null)
			return;

		if (!(fealtyTo instanceof Player))
			return;

		this.voteemperor = uniqueId;
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
			LivingEntity pet = StateManager.getInstance().getEntityManager().getPet(this.getBukkitPlayer());
			if (pet == null)
				return;

			StateManager.getInstance().getEntityManager().killPet(this.getBukkitPlayer());
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

					Integer augmentationId = ItemStackUtils.getNBTAugmentationItemId(itemstack);
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
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager()
						.getItem(this.getShouldersItem());
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
	public ConcurrentHashMap<Integer, Integer> getReagents() {
		return reagents;
	}

	@Override
	public void setReagents(ConcurrentHashMap<Integer, Integer> reagents) {
		this.reagents = reagents;
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

		if (getReagents().get(itemId) < neededCount)
			return false;

		return true;
	}

	@Override
	public boolean hasSufficientBandageReagents(int countNeeded) {
		int totalCount = 0;

		for (Entry<Integer, Integer> entry : getReagents().entrySet()) {
			try {
				int itemId = entry.getKey();
				int count = entry.getValue();
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
	public List<Integer> getBandageReagents() {

		List<Integer> itemIds = new ArrayList<Integer>();
		for (Entry<Integer, Integer> entry : getReagents().entrySet()) {
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

		int currentCount = getReagents().get(itemId);
		currentCount = currentCount - reduceAmount;
		if (currentCount < 0)
			currentCount = 0;
		getReagents().put(itemId, currentCount);
	}

	@Override
	public SoliniaAlignmentChunk getCurrentAlignmentChunk() {
		SoliniaAlignmentChunk alignmentChunk = null;
		try {
			for (ISoliniaAlignment alignment : StateManager.getInstance().getConfigurationManager().getAlignments()) {
				Chunk chunk = getBukkitPlayer().getLocation().getChunk();
				if (alignment.getChunks().get(
						chunk.getWorld().getName().toUpperCase() + "_" + chunk.getX() + "_" + chunk.getZ()) != null)
					return alignment.getChunks()
							.get(chunk.getWorld().getName().toUpperCase() + "_" + chunk.getX() + "_" + chunk.getZ());
			}
		} catch (CoreStateInitException e) {

		}
		return alignmentChunk;
	}

	@Override
	public void setMainAndCleanup() {
		try {
			// Set alt to all other characters
			for (ISoliniaPlayer solPlayer : StateManager.getInstance().getConfigurationManager()
					.getCharactersByPlayerUUID(this.getBukkitPlayer().getUniqueId())) {
				solPlayer.setMain(false);

				for (ISoliniaPlayer otherSolPlayer : StateManager.getInstance().getConfigurationManager()
						.getCharacters()) {
					if (otherSolPlayer.getFealty() != null)
						if (otherSolPlayer.getFealty().toString().equals(solPlayer.getCharacterId().toString())) {
							otherSolPlayer.setFealty(null);
						}
				}

				for (ISoliniaRace race : StateManager.getInstance().getConfigurationManager().getRaces()) {
					if (race.getKing() != null)
						if (race.getKing().toString().equals(solPlayer.getUUID().toString())) {
							Utils.BroadcastPlayers("The " + race.getName() + " race no longer has a King!");
							race.setKing(null);
						}
				}

				for (ISoliniaAlignment alignment : StateManager.getInstance().getConfigurationManager()
						.getAlignments()) {
					if (alignment.getEmperor() != null)
						if (alignment.getEmperor().toString().equals(solPlayer.getUUID().toString())) {
							Utils.BroadcastPlayers("The " + alignment.getName() + " empire no longer has an emperor!");
							alignment.setEmperor(null);
						}
				}

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
	public void autoAttackEnemy(ISoliniaLivingEntity solLivingEntity) {
		if (getBukkitPlayer().isDead()) {
			try {
				StateManager.getInstance().getEntityManager().setPlayerAutoAttack(getBukkitPlayer(), false);
				return;
			} catch (CoreStateInitException e) {

			}
		}

		ItemStack itemStack = getBukkitPlayer().getInventory().getItemInMainHand();

		if (solLivingEntity.getBukkitLivingEntity().getUniqueId().toString()
				.equals(getBukkitPlayer().getUniqueId().toString())) {
			getBukkitPlayer().sendMessage(ChatColor.GRAY + "* You cannot auto attack yourself!");
			return;
		}

		if (solLivingEntity.getBukkitLivingEntity() instanceof Wolf) {
			Wolf wolf = (Wolf) solLivingEntity.getBukkitLivingEntity();
			if (wolf.getOwner().getUniqueId().toString().equals(getBukkitPlayer().getUniqueId().toString())) {
				getBukkitPlayer().sendMessage(ChatColor.GRAY + "* You cannot auto attack your pet!");
			}
			return;
		}

		if (solLivingEntity.getBukkitLivingEntity().getLocation().distance(getBukkitPlayer().getLocation()) > 3) {
			getBukkitPlayer().sendMessage(ChatColor.GRAY + "* You are too far away to auto attack!");
			return;
		}

		EntityPlayer ep = ((CraftPlayer) getBukkitPlayer()).getHandle();
		PacketPlayOutAnimation packet = new PacketPlayOutAnimation(ep, 0);
		getBukkitPlayer().getWorld().playSound(getBukkitPlayer().getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0F,
				1.0F);

		((CraftPlayer) getBukkitPlayer()).getHandle().playerConnection.sendPacket(packet);

		for (Entity listening : getBukkitPlayer().getNearbyEntities(100, 100, 100)) {
			if (listening instanceof Player)
				((CraftPlayer) listening).getHandle().playerConnection.sendPacket(packet);
		}

		((CraftPlayer) getBukkitPlayer()).getHandle()
				.attack(((CraftEntity) solLivingEntity.getBukkitLivingEntity()).getHandle());
		/*
		 * DamageSource source =
		 * EntityDamageSource.playerAttack(((CraftPlayer)getBukkitPlayer()).getHandle())
		 * ;
		 * 
		 * 
		 * ((CraftEntity)
		 * solLivingEntity.getBukkitLivingEntity()).getHandle().damageEntity(source,
		 * (int)ItemStackUtils.getWeaponDamage(itemStack));
		 */
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
			
			tryIncreaseSkill("BINDWOUND", 1);
			
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
			if (percent_base >= 70)
			{
				if (playersolLivingEntity != null)
				percent_bonus = playersolLivingEntity.getMaxBindWound_SE();
			}
	
			int max_percent = percent_base + percent_bonus;
			if (max_percent < 0)
				max_percent = 0;
			if (max_percent > 100)
				max_percent = 100;
	
			int max_hp = (int)Math.floor((solLivingEntity.getBukkitLivingEntity().getMaxHealth() * max_percent) / 100);
			if (max_hp > (int)Math.floor(solLivingEntity.getBukkitLivingEntity().getMaxHealth()))
				max_hp = (int)Math.floor(solLivingEntity.getBukkitLivingEntity().getMaxHealth());
	
			if (solLivingEntity.getBukkitLivingEntity().getHealth() < solLivingEntity.getBukkitLivingEntity().getMaxHealth() && solLivingEntity.getBukkitLivingEntity().getHealth() < max_hp) {
				int bindhps = 3; // base bind hp
				if (percent_base >= 70)
					bindhps = (getSkill("BINDWOUND").getValue() * 4) / 10; // 8:5 skill-to-hp ratio
				else if (getSkill("BINDWOUND").getValue() >= 12)
					bindhps = getSkill("BINDWOUND").getValue() / 4; // 4:1 skill-to-hp ratio
	
				int bonus_hp_percent = 0;
				if (percent_base >= 70)
				{
					if (playersolLivingEntity != null)
						bonus_hp_percent = playersolLivingEntity.getBindWound_SE();
				}
				
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
				if (amount > solLivingEntity.getBukkitLivingEntity().getMaxHealth()) {
					amount = (int) Math.round(solLivingEntity.getBukkitLivingEntity().getMaxHealth());
				}
	
				if (amount < 0)
					amount = 0;
				
				double boundHealth = amount - originalHealth;
				
				getBukkitPlayer().sendMessage("You bind " + solLivingEntity.getName() + "'s wounds for " + (int)boundHealth + " hp");
				if (solLivingEntity.getBukkitLivingEntity() instanceof Player && !solLivingEntity.getBukkitLivingEntity().getUniqueId().toString().equals(getBukkitPlayer().getUniqueId().toString()))
					solLivingEntity.getBukkitLivingEntity().sendMessage("Your wounds are being bound by " + getBukkitPlayer().getDisplayName());
				
				solLivingEntity.getBukkitLivingEntity().setHealth(amount);
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
}
