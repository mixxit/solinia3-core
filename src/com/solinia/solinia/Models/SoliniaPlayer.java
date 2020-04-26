package com.solinia.solinia.Models;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaAAAbility;
import com.solinia.solinia.Interfaces.ISoliniaAARank;
import com.solinia.solinia.Interfaces.ISoliniaClass;
import com.solinia.solinia.Interfaces.ISoliniaFaction;
import com.solinia.solinia.Interfaces.ISoliniaGod;
import com.solinia.solinia.Interfaces.ISoliniaGroup;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Interfaces.ISoliniaRace;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Utils.EntityUtils;
import com.solinia.solinia.Utils.ForgeUtils;
import com.solinia.solinia.Utils.ItemStackUtils;
import com.solinia.solinia.Utils.PartyWindowUtils;
import com.solinia.solinia.Utils.PlayerUtils;
import com.solinia.solinia.Utils.SpellTargetType;
import com.solinia.solinia.Utils.TextUtils;
import com.solinia.solinia.Utils.Utils;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.spawning.spawners.MythicSpawner;
import net.md_5.bungee.api.ChatMessageType;
import net.minecraft.server.v1_14_R1.Tuple;

public class SoliniaPlayer implements ISoliniaPlayer {

	private static final long serialVersionUID = 9075039437399478391L;
	private int id = 0;
	private UUID primaryUUID = UUID.randomUUID();
	private UUID secondaryUUID = UUID.randomUUID();
	private Timestamp lastUpdatedTime;
	private int motherCharacterId = 0;
	private int characterFellowshipId = 0;
	private int spouseCharacterId = 0;
	private String forename = "";
	private String lastname = "";
	private int mana = 0;
	private Double experience = 0d;
	private int aapct;
	private Double aaexperience = 0d;
	private int aapoints = 0;
	private int raceid = 0;
	private int godId = 0;
	private boolean hasChosenGod = false;
	private boolean haschosenrace = false;
	private boolean haschosenclass = false;
	private int classid = 0;
	private String gender = "MALE";
	private String base64BankContents = "";
	private String base64InventoryContents = "";
	private String base64ArmorContents = "";
	private boolean experienceOn = true;
	private boolean forceNewAlt = false;

	private List<SoliniaPlayerSkill> skills = new ArrayList<SoliniaPlayerSkill>();
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
	private boolean modMessageEnabled = true;
	private int inspiration = 0;
	private Timestamp experienceBonusExpires;
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
	private int memorisedSpellSlot1 = 0;
	private int memorisedSpellSlot2 = 0;
	private int memorisedSpellSlot3 = 0;
	private int memorisedSpellSlot4 = 0;
	private int memorisedSpellSlot5 = 0;
	private int memorisedSpellSlot6 = 0;
	private int memorisedSpellSlot7 = 0;
	private int memorisedSpellSlot8 = 0;
	
	private SkillType languageSkillType = SkillType.UnknownTongue;

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
	private boolean deleted = false;

	private List<Integer> spellBookItems = new ArrayList<Integer>();
	private ConcurrentHashMap<String, HintSetting> hintSettings = new ConcurrentHashMap<String, HintSetting>();
	private ConcurrentHashMap<String, Integer> monthlyVote = new ConcurrentHashMap<String, Integer>();
	private ConcurrentHashMap<Integer, SoliniaReagent> reagentsPouch = new ConcurrentHashMap<Integer, SoliniaReagent>();
	private Double pendingXp = 0d;
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
	public Oath getOath() {
		try {
			return StateManager.getInstance().getConfigurationManager().getOath(this.getOathId());
		} catch (CoreStateInitException e) {
			return null;
		}
	}

	@Override
	public PacketCastingPercent toPacketCastingPercent() {
		PacketCastingPercent vitals = new PacketCastingPercent();
		vitals.fromData((float) this.getCastingProgress());
		return vitals;
	}

	@Override
	public void setLastLocation(Location location) {
		this.lastX = location.getBlockX();
		this.lastY = location.getBlockY();
		this.lastZ = location.getBlockZ();
		this.setLastUpdatedTimeNow();
	}

	@Override
	public Location getLastLocation() {
		return new Location(this.getBukkitPlayer().getWorld(), this.lastX, this.lastY, this.lastZ);
	}

	@Override
	public void setIgnoredPlayers(List<UUID> ignoredPlayers) {
		this.ignoredPlayers = ignoredPlayers;
		this.setLastUpdatedTimeNow();
	}

	@Override
	public SoliniaWorld getWorld() {
		try {
			return StateManager.getInstance().getConfigurationManager()
					.getWorld(getBukkitPlayer().getWorld().getName().toUpperCase());
		} catch (CoreStateInitException e) {
			return null;
		}
	}
	
	@Override
	public boolean grantTitle(String title) {
		if (getAvailableTitles().contains(title))
			return false;

		getAvailableTitles().add(title);
		getBukkitPlayer().sendMessage(
				ChatColor.YELLOW + "* You have earned the title: " + title + ChatColor.RESET + " See /settitle");
		this.setLastUpdatedTimeNow();
		return true;
	}

	@Override
	public Timestamp getLastLogin() {
		return new Timestamp(Bukkit.getOfflinePlayer(this.getOwnerUUID()).getLastPlayed());
	}

	@Override
	public String getForename() {
		return forename;
	}

	@Override
	public void setForename(String forename) {
		this.forename = forename;
		this.setLastUpdatedTimeNow();
	}

	@Override
	public String getLastname() {
		return lastname;
	}

	@Override
	public void setLastname(String lastname) {
		this.lastname = lastname;
		this.setLastUpdatedTimeNow();
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
				PartyWindowUtils.UpdateGroupWindow(getBukkitPlayer().getUniqueId(), this.getGroup(), false, false);
			}
			this.setLastUpdatedTimeNow();
		}
	}

	@Override
	public Player getBukkitPlayer() {
		try
		{
			Player player = Bukkit.getPlayer(getOwnerUUID());
			return player;
		} catch (NullPointerException e)
		{
			return null;
		}
		
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
		if (this.mana == mana)
			return;

		this.mana = mana;
		PartyWindowUtils.UpdateWindow(this.getBukkitPlayer(), true, false);
		this.setLastUpdatedTimeNow();
	}

	@Override
	public Double getAAExperience() {
		return this.aaexperience;
	}

	@Override
	public void setAAExperience(Double aaexperience) {
		this.aaexperience = aaexperience;
		this.setLastUpdatedTimeNow();
	}

	@Override
	public Double getExperience() {
		return this.experience;
	}

	@Override
	public void setExperience(Double experience) {
		this.experience = experience;
		this.setLastUpdatedTimeNow();
	}

	@Override
	public int getLevel() {
		return PlayerUtils.getLevelFromExperience(this.experience);
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
		this.setLastUpdatedTimeNow();
	}

	@Override
	public void setRaceId(int raceid) {
		// TODO Auto-generated method stub
		this.raceid = raceid;
		this.languageSkillType = getRace().getLanguage();
		updateMaxHp();
		this.setLastUpdatedTimeNow();
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
	public ISoliniaGod getGod() {
		try {
			return StateManager.getInstance().getConfigurationManager().getGod(getGodId());
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
		this.setLastUpdatedTimeNow();
	}

	@Override
	public boolean hasChosenClass() {
		return haschosenclass;
	}

	@Override
	public void setChosenClass(boolean haschosenclass) {
		this.haschosenclass = haschosenclass;
		this.setLastUpdatedTimeNow();
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

				PartyWindowUtils.UpdateGroupWindowForEveryone(getBukkitPlayer().getUniqueId(), getGroup(), false);
			} catch (CoreStateInitException e) {

			}
		}
	}

	@Override
	public void increasePlayerExperience(Double experience, boolean applyModifiers, boolean ignoreIfExperienceOff) {
		if (ignoreIfExperienceOff && !this.isExperienceOn())
			return;

		if (!isAAOn()) {
			increasePlayerNormalExperience(experience, applyModifiers, ignoreIfExperienceOff);
			
		} else {
			int normalpct = 100 - getAapct();
			if (normalpct > 0) {
				Double normalexperience = (experience / 100) * normalpct;
				increasePlayerNormalExperience(normalexperience, applyModifiers, ignoreIfExperienceOff);
			}

			Double aaexperience = (experience / 100) * getAapct();
			increasePlayerAAExperience(aaexperience, applyModifiers);
		}
		this.setLastUpdatedTimeNow();
	}

	@Override
	public boolean isAAOn() {
		if (this.getAapct() > 0)
			return true;

		return false;
	}

	@Override
	public void increasePlayerNormalExperience(Double experience, boolean applyModifiers,
			boolean ignoreIfExperienceOff) {
		if (ignoreIfExperienceOff && !this.isExperienceOn())
			return;

		double classxpmodifier = 0;
		boolean modified = false;
		if (applyModifiers) {
			classxpmodifier = PlayerUtils.getClassXPModifier(getClassObj());
			experience = experience * (classxpmodifier / 100);

			double modifier = StateManager.getInstance().getXPDayModifier();
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
			
			if (this.hasSpellEffectType(SpellEffectType.PercentXPIncrease))
				modifier += getSpellBonuses(SpellEffectType.PercentXPIncrease);

			SoliniaZone zone = this.getFirstZone();
			if (zone != null)
			{
				if (zone.isHotzone() == true) {
					modifier += 100;
				}

				if (zone.getZoneExperienceModifier() > 0)
					modifier += zone.getZoneExperienceModifier();
			}

			if (modifier > 100) {
				modified = true;
			}
			experience = experience * (modifier / 100);
		}

		Double currentexperience = getExperience();

		// make sure someone never gets more than a level per kill
		double clevel = PlayerUtils.getLevelFromExperience(currentexperience);
		double nlevel = PlayerUtils.getLevelFromExperience((currentexperience + experience));

		if (nlevel > (clevel + 1)) {
			// xp is way too high, drop to proper amount

			double xp = PlayerUtils.getExperienceRequirementForLevel((int) clevel + 1);
			experience = xp - currentexperience;
		}

		if (getClassObj() == null) {
			if (nlevel > 10) {
				double xp = PlayerUtils.getExperienceRequirementForLevel(10);
				experience = xp - currentexperience;
			}
		}
		;

		double maxExperience = currentexperience;
		try {
			maxExperience = StateManager.getInstance().getConfigurationManager().getMaxExperience();
		} catch (CoreStateInitException e) {

		}

		if ((currentexperience + experience) > maxExperience) {
			// System.out.println("XP: " + experience);
			currentexperience = maxExperience;
		} else {
			// System.out.println("XP: " + experience);
			currentexperience = currentexperience + experience;
		}

		setExperience(currentexperience, experience, modified);
		this.setLastUpdatedTimeNow();
	}
	
	@Override
	public void increasePlayerAAExperience(Double experience, boolean applyModifiers) {

		boolean modified = false;

		if (applyModifiers) {
			double modifier = StateManager.getInstance().getXPDayModifier();
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
			
			if (this.hasSpellEffectType(SpellEffectType.PercentXPIncrease))
				modifier += getSpellBonuses(SpellEffectType.PercentXPIncrease);

			if (isInHotzone() == true) {
				modifier += 100;
			}
			
			SoliniaZone zone = this.getFirstZone();
			if (zone != null)
			{
				if (zone.isHotzone() == true) {
					modifier += 100;
				}

				if (zone.getZoneExperienceModifier() > 0)
					modifier += zone.getZoneExperienceModifier();
			}

			if (modifier > 100) {
				modified = true;
			}
			experience = experience * (modifier / 100);
		}

		// Cap at max just under a quarter of an AA experience point
		if (experience > PlayerUtils.getMaxAAXP()) {
			experience = PlayerUtils.getMaxAAXP();
		}

		// System.out.println("AA XP: " + experience);

		Double currentaaexperience = getAAExperience();

		currentaaexperience = currentaaexperience + experience;

		setAAExperience(currentaaexperience, modified, experience);
		this.setLastUpdatedTimeNow();

	}

	
	@Override
	public int getSpellBonuses(SpellEffectType spellEffectType) {
		int bonus = 0;
		for (ActiveSpellEffect effect : Utils.getActiveSpellEffects(getBukkitPlayer(), spellEffectType)) {
			bonus += effect.getRemainingValue();
		}

		return bonus;
	}

	private boolean hasSpellEffectType(SpellEffectType type) {
		if (this.getBukkitPlayer() == null)
			return false;
		
		try
		{
		return StateManager.getInstance().getEntityManager().hasEntityEffectType(this.getBukkitPlayer(),
				type);
		} catch (CoreStateInitException e)
		{
			return false;
		}
	}

	@Override
	public List<SoliniaZone> getZones() {
		List<SoliniaZone> zones = new ArrayList<SoliniaZone>();
		try {
			for (SoliniaZone zone : StateManager.getInstance().getConfigurationManager().getZones()) {

				if (zone.isLocationInside(this.getBukkitPlayer().getLocation()))
					zones.add(zone);
			}
		} catch (CoreStateInitException e) {

		}

		return zones;
	}

	@Override
	public boolean isInHotzone() {
		for (SoliniaZone zone : StateManager.getInstance().getCurrentHotzones()) {
			if (!this.getBukkitPlayer().getWorld().getName().equals(zone.getWorld()))
				continue;

			if (zone.isLocationInside(this.getBukkitPlayer().getLocation()))
				return true;
		}

		return false;
	}
	
	@Override
	public SoliniaZone getZone() {
		return getFirstZone();
	}
	
	@Override
	public boolean isInZone(int zoneId) {
		if (zoneId < 1)
			return false;
		
		return Utils.isLocationInZone(this.getBukkitPlayer().getLocation(), zoneId);
	}

	@Override
	public boolean isInZone(SoliniaZone zone) {
		if (zone == null)
			return false;

		return Utils.isLocationInZone(this.getBukkitPlayer().getLocation(), zone);
	}

	@Override
	public SoliniaZone getFirstZone() {
		try {
			List<SoliniaZone> potentialZones = new ArrayList<SoliniaZone>();

			for (SoliniaZone zone : StateManager.getInstance().getConfigurationManager().getZones()) {

				if (zone.isLocationInside(this.getBukkitPlayer().getLocation()))
					potentialZones.add(zone);
			}

			potentialZones = potentialZones.stream()
					.sorted((o1, o2) -> Double.compare(o1.getCornerDistances(), o2.getCornerDistances()))
					.collect(Collectors.toList());

			if (potentialZones.size() > 0)
				return potentialZones.get(0);
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

		double clevel = PlayerUtils.getLevelFromExperience(originalexperience);
		double nlevel = PlayerUtils.getLevelFromExperience(newexperience);

		if (nlevel < (clevel - 1)) {
			// xp loss is way too high, drop to proper amount
			System.out.println("XP loss was dropped for being way too high");

			newexperience = PlayerUtils.getExperienceRequirementForLevel((int) clevel - 1);
			experiencechange = newexperience - originalexperience;
		}

		setExperience(newexperience, experiencechange, false);
		this.setLastUpdatedTimeNow();
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
		if (!isPlayable()) {
			getBukkitPlayer().sendMessage(
					ChatColor.YELLOW + "* You cannot gain or lose experience for a character that is marked as LOCKED");
			return;
		}

		Double level = (double) getLevel();

		this.experience = experience;

		Double newlevel = (double) getLevel();

		Double xpneededforcurrentlevel = PlayerUtils.getExperienceRequirementForLevel((int) (newlevel + 0));
		Double xpneededfornextlevel = PlayerUtils.getExperienceRequirementForLevel((int) (newlevel + 1));
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
						+ "* You were given bonus XP from a xp bonus /hotzone or potion! (See /stats && /hotzones)");
		}

		if (changeamount < 0) {
			getBukkitPlayer()
					.sendMessage(ChatColor.RED + "* You lost experience (" + ipercenttolevel + "% into level)");
		}
		if (Double.compare(newlevel, level) > 0) {
			String classname = "Hero";
			if (getClassObj() != null)
				classname = getClassObj().getName();

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
		this.setLastUpdatedTimeNow();

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
		if (aaexperience > PlayerUtils.getMaxAAXP()) {
			aaexperience = aaexperience - PlayerUtils.getMaxAAXP();
			setAAPoints(getAAPoints() + 1);
			givenaapoint = true;
		}

		setAAExperience(aaexperience);

		Double percenttoaa = Math.floor((aaexperience / PlayerUtils.getMaxAAXP()) * 100);
		int ipercenttoaa = percenttoaa.intValue();

		getBukkitPlayer()
				.sendMessage(ChatColor.YELLOW + "* You gain alternate experience (" + ipercenttoaa + "% into AA)!");
		getBukkitPlayer().sendMessage(ChatColor.GRAY + "AAExp Gained: " + amountincreased);
		if (modified == true)
			getBukkitPlayer().sendMessage(ChatColor.YELLOW
					+ "* You were given bonus XP from a player xp bonus/hotzone or potion! (See /stats && /hotzones)");

		if (givenaapoint) {
			getBukkitPlayer().sendMessage(ChatColor.YELLOW + "* You gained an Alternate Experience Point!");
		}
		this.setLastUpdatedTimeNow();

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
		this.setLastUpdatedTimeNow();

	}

	@Override
	public String getGender() {
		return gender;
	}

	@Override
	public void setGender(String gender) {
		this.gender = gender;
		this.setLastUpdatedTimeNow();

	}

	@Override
	public int getSkillCap(SkillType skillType) {
		return EntityUtils.getSkillCap(skillType, getClassObj(), getLevel(), getSpecialisation(),
				this.getSkill(skillType).getValue());
	}

	@Override
	public List<SoliniaPlayerSkill> getSkills() {
		// TODO Auto-generated method stub
		return this.skills;
	}

	@Override
	public void emote(String string, boolean isBardSongFilterable, boolean isManual) {
		int numberReached = StateManager.getInstance().getChannelManager().sendToLocalChannel(this,
				ChatColor.AQUA + "* " + string, isBardSongFilterable,
				getBukkitPlayer().getInventory().getItemInMainHand());

		if (isManual && numberReached < 1)
			getBukkitPlayer().sendMessage("You feel like nobody could see you");
	}

	@Override
	public void ooc(String string) {
		if (getLanguageSkillType() == null || !Utils.IsValidLanguage(getLanguageSkillType())) {
			if (getRace() != null)
				setLanguageSkillType(getRace().getLanguage());
		}
		StateManager.getInstance().getChannelManager().sendToGlobalChannelDecorated(this, string,
				getBukkitPlayer().getInventory().getItemInMainHand());
	}

	@Override
	public void say(String message) {
		if (getLanguageSkillType() == null || !Utils.IsValidLanguage(getLanguageSkillType())) {
			if (getRace() == null) {
				getBukkitPlayer().sendMessage(
						"You must set your race to speak in local chat - /opencharcreation - If you need help you can ask in OOC chat (/o <msg>)");
				return;
			} else {
				setLanguageSkillType(getRace().getLanguage());
			}
		}

		boolean onlySendToSource = false;
		// filter hails
		if (getEntityTarget() != null && !(getEntityTarget() instanceof Player)) {
			if (message.toUpperCase().equals("HAIL"))
				onlySendToSource = true;
		}

		int numberReached = StateManager.getInstance().getChannelManager().sendToLocalChannelDecorated(this, message,
				message, getBukkitPlayer().getInventory().getItemInMainHand(), onlySendToSource);

		if (numberReached < 1)
			getBukkitPlayer().sendMessage("You feel like nobody could hear you");

		// NPC responses (if applicable)
		if (getEntityTarget() != null) {
			Entity entity = getEntityTarget();
			if (entity != null && entity instanceof LivingEntity
					&& getBukkitPlayer().getLocation().distance(entity.getLocation()) < 4) {
				LivingEntity livingEntity = (LivingEntity) entity;
				ISoliniaLivingEntity solentity;
				try {
					solentity = StateManager.getInstance().getEntityManager().getLivingEntity(livingEntity);
					solentity.processInteractionEvent(getBukkitPlayer(), InteractionType.CHAT, message);
				} catch (CoreStateInitException e) {
					// skip
				}
			}
		}
	}

	@Override
	public void whisper(String string) {
		if (getLanguageSkillType() == null || !Utils.IsValidLanguage(getLanguageSkillType())) {
			if (getRace() == null) {
				getBukkitPlayer().sendMessage(
						"You must set your race to speak in local chat - /opencharcreation - If you need help you can ask in OOC chat (/o <msg>)");
				return;
			} else {
				setLanguageSkillType(getRace().getLanguage());
			}
		}
		int numberReached = StateManager.getInstance().getChannelManager().sendToWhisperChannelDecorated(this, string,
				string, getBukkitPlayer().getInventory().getItemInMainHand());

		if (numberReached < 1)
			getBukkitPlayer().sendMessage("You feel like nobody could hear you");
	}

	@Override
	public void shout(String string) {
		if (getLanguageSkillType() == null || !Utils.IsValidLanguage(getLanguageSkillType())) {
			if (getRace() == null) {
				getBukkitPlayer().sendMessage(
						"You must set your race to speak in local chat - /opencharcreation - If you need help you can ask in OOC chat (/o <msg>)");
				return;
			} else {
				setLanguageSkillType(getRace().getLanguage());
			}
		}
		int numberReached = StateManager.getInstance().getChannelManager().sendToShoutChannelDecorated(this, string,
				string, getBukkitPlayer().getInventory().getItemInMainHand());
		if (numberReached < 1)
			getBukkitPlayer().sendMessage("You feel like nobody could hear you");

	}

	@Override
	public SoliniaPlayerSkill getSkill(SkillType skillType) {
		if (!Utils.isValidSkill(skillType.name().toUpperCase())) {
			sendMessage(
					"ADMIN ALERT, Please inform Moderators that you have called getSkill for an unknown skill: '"
							+ skillType.name().toUpperCase() + "'");
			if (getBukkitPlayerName() != null)
			System.out.println("ADMIN ALERT, " + getBukkitPlayerName() + " getSkill for an unknown skill: '"
					+ skillType.name().toUpperCase() + "'");
			return null;
		}

		for (SoliniaPlayerSkill skill : this.skills) {
			if (skill.getSkillName().toUpperCase().equals(skillType.name().toUpperCase().toUpperCase()))
				return skill;
		}

		// If we got this far the skill doesn't exist, create it with 0
		SoliniaPlayerSkill skill = new SoliniaPlayerSkill(skillType.name().toUpperCase().toUpperCase(),skillType, 0);
		skills.add(skill);
		return skill;
	}
	
	private String getBukkitPlayerName()
	{
		if (getBukkitPlayer() == null)
			return null;
		
		return getBukkitPlayer().getName();
	}

	private void sendMessage(String string) {
		if (this.getBukkitPlayer() != null)
			this.getBukkitPlayer().sendMessage(string);
	}

	@Override
	public void tryIncreaseSkill(SkillType skillType, int skillupamount) {
		SoliniaPlayerSkill skill = getSkill(skillType);
		int currentskill = 0;
		if (skill != null) {
			currentskill = skill.getValue();

			// Fix any higher than it should be
			if (currentskill > this.getSkillCap(skillType)) {
				setSkill(skillType, this.getSkillCap(skillType));
				currentskill = skill.getValue();
			}
		}

		int skillcap = getSkillCap(skillType);
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
			setSkill(skillType, currentskill + skillupamount);
		}
		this.setLastUpdatedTimeNow();

		if (getSpecialisation() != null && !getSpecialisation().equals("")) {
			if (!skillType.name().toUpperCase().equals(getSpecialisation().toUpperCase()))
				return;

			skill = getSkill(Utils.getSkillType2("SPECIALISE" + skillType.name().toUpperCase()));

			currentskill = 0;
			if (skill != null) {
				currentskill = skill.getValue();
			}

			skillcap = getSkillCap(Utils.getSkillType2("SPECIALISE" + skillType.name().toUpperCase()));
			if ((currentskill + skillupamount) > skillcap) {
				return;
			}

			chance = 10 + ((252 - currentskill) / 20);
			if (chance < 1) {
				chance = 1;
			}

			randomInt = r.nextInt(100) + 1;
			if (randomInt < chance) {

				setSkill(Utils.getSkillType2("SPECIALISE" + skillType.name().toUpperCase()), currentskill + skillupamount);
			}
			this.setLastUpdatedTimeNow();

		}

	}

	@Override
	public void setSkill(SkillType skillType, int value) {
		if (value > Integer.MAX_VALUE)
			return;

		// max skill point
		if (value > Utils.HIGHESTSKILL)
			return;

		if (this.skills == null)
			this.skills = new ArrayList<SoliniaPlayerSkill>();

		boolean updated = false;

		for (SoliniaPlayerSkill skill : this.skills) {
			if (skill.getSkillName().toUpperCase().equals(skillType.name().toUpperCase())) {
				skill.setValue(value);
				updated = true;
				if (skill.getValue() > 0)
					sendMessage(ChatColor.YELLOW + "* You get better at " + skillType.name().toUpperCase() + " (" + value + ")");
				return;
			}
		}

		if (updated == false) {
			SoliniaPlayerSkill skill = new SoliniaPlayerSkill(skillType.name().toUpperCase(), skillType, value);
			skills.add(skill);
		}

		if (value > 0)
			sendMessage(ChatColor.YELLOW + "* You get better at " + skillType.name().toLowerCase() + " (" + value + ")");
		this.setLastUpdatedTimeNow();

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
		this.setLastUpdatedTimeNow();

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
		this.setLastUpdatedTimeNow();

	}
	
	@Override
	public void tryCastFromMemorySlot(int slotId) {
		try {
			Utils.DebugLog("SoliniaPlayer", "tryCastFromMemorySlot", this.getBukkitPlayer().getName(),
					"Trying to cast from item in memory slot: " + slotId);
			int spellId = this.getMemorisedSpellSlot(slotId);
			if (spellId < 1) {
				return;
			}
			
			if (this.isMezzed())
			{
				getBukkitPlayer().sendMessage("* You cannot use an ability while mezzed!");
				return;
			}
			
			if (this.isStunned())
			{
				getBukkitPlayer().sendMessage("* You cannot use an ability while stunned!");
				return;
			}

			if (!getMemorisedSpellSlots().getAllSpellIds().contains(spellId)) {
				Utils.DebugLog("SoliniaPlayer", "tryCastFromMemorySlot", this.getBukkitPlayer().getName(),
						"Spell not in spellbook");
				getBukkitPlayer().sendMessage("* This spell is no longer in your spellbook");
				return;
			}

			Utils.DebugLog("SoliniaPlayer", "tryCastFromMemorySlot", this.getBukkitPlayer().getName(),
					"SoliniaSpell in slot: " + spellId + " target status: " + (getEntityTarget() == null));

			// Some spells auto target self, if they don't have a target try to do that
			ISoliniaSpell spell = StateManager.getInstance().getConfigurationManager().getSpell(spellId);
			if (spell != null) {
				this.tryForceTargetIfNeededForSpell(spell);

				if (getEntityTarget() == null) {
					if (!tryFixSpellTarget(spell)) {
						getBukkitPlayer().sendMessage("* You must select a target [See keybinds]");
						return;
					}
				}
			}
			
			if (!spell.isBardSong())
			if (!this.hasReagents(spell,getBukkitPlayer()))
			{
				Utils.DebugLog("SoliniaPlayer", "tryCastFromMemorySlot", this.getBukkitPlayer().getName(),
						"SoliniaSpell in slot: " + spellId + " Missing reagents");
				return;
			}
			
			// Special check for ability Bind Wound
			if (spell.getSpellEffectTypes().contains(SpellEffectType.BindWound)) {
				if (!hasSufficientBandageReagents(1))
				{
					Utils.DebugLog("SoliniaPlayer", "tryCastFromMemorySlot", this.getBukkitPlayer().getName(),
							"SoliniaSpell in slot: " + spellId + " Missing reagents for Bind Wound");
					this.getBukkitPlayer().sendMessage("You do not have enough bandages in your /reagent pouch");
					return;
				}
			}

			if (getEntityTarget() == null) {
				getBukkitPlayer().sendMessage("* You must select a target [See keybinds]");
				return;
			}

			// Reroute action depending on target
			ISoliniaLivingEntity solentity = SoliniaLivingEntityAdapter.Adapt((LivingEntity) getBukkitPlayer());
			if (spell.getActSpellCost(solentity) > SoliniaPlayerAdapter.Adapt(getBukkitPlayer()).getMana()) {
				getBukkitPlayer().sendMessage(ChatColor.GRAY + "Insufficient Mana  [E]");
				return;
			}

			if (!spell.getRequiresPermissionNode().equals("")) {
				if (!getBukkitPlayer().hasPermission(spell.getRequiresPermissionNode())) {
					getBukkitPlayer().sendMessage("This requires a permission node you do not have");
					return;
				}
			}

			if (StateManager.getInstance().getEntityManager().getEntitySpellCooldown(getBukkitPlayer(),
					spell.getId()) != null) {
				LocalDateTime datetime = LocalDateTime.now();
				Timestamp nowtimestamp = Timestamp.valueOf(datetime);
				Timestamp expiretimestamp = StateManager.getInstance().getEntityManager()
						.getEntitySpellCooldown(getBukkitPlayer(), spell.getId());

				if (expiretimestamp != null)
					if (!nowtimestamp.after(expiretimestamp)) {
						Utils.DebugLog("SoliniaPlayer", "tryCastFromMemorySlot", this.getBukkitPlayer().getName(),
								"SoliniaSpell in slot: " + spellId + " Lack of willpower");

						getBukkitPlayer().sendMessage("You do not have enough willpower to cast " + spell.getName()
								+ " (Wait: " + ((expiretimestamp.getTime() - nowtimestamp.getTime()) / 1000) + "s");
						return;
					}
			}

			Utils.DebugLog("SoliniaPlayer", "tryCastFromMemorySlot", this.getBukkitPlayer().getName(),
					"SoliniaSpell in slot: " + spellId + " Starting casting");

			startCasting(spell, getBukkitPlayer(), true, true, false, "");
		} catch (CoreStateInitException e) {

		}
	}

	@Override
	public void tryCastSpell(ISoliniaSpell spell, boolean useMana, boolean useReagents,
			boolean ignoreProfessionAndLevel, String requiredWeaponSkillType) {
		if (spell == null)
			return;

		try {
			// Some spells auto target self, if they don't have a target try to do that
			// Some spells auto target self, if they don't have a target try to do that
			if (spell != null) {
				tryForceTargetIfNeededForSpell(spell);

				if (getEntityTarget() == null) {
					if (!tryFixSpellTarget(spell)) {
						getBukkitPlayer().sendMessage("* You must select a target [See keybinds]");
						return;
					}
				}
			}

			if (getEntityTarget() == null) {
				getBukkitPlayer().sendMessage("* You must select a target [See keybinds]");
				return;
			}

			// Reroute action depending on target
			ISoliniaLivingEntity solentity = SoliniaLivingEntityAdapter.Adapt((LivingEntity) getBukkitPlayer());
			if (useMana && spell.getActSpellCost(solentity) > SoliniaPlayerAdapter.Adapt(getBukkitPlayer()).getMana()) {
				getBukkitPlayer().sendMessage(ChatColor.GRAY + "Insufficient Mana  [E]");
				return;
			}

			if (!spell.getRequiresPermissionNode().equals("")) {
				if (!getBukkitPlayer().hasPermission(spell.getRequiresPermissionNode())) {
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
						getBukkitPlayer().sendMessage("You do not have enough willpower to cast " + spell.getName()
								+ " (Wait: " + ((expiretimestamp.getTime() - nowtimestamp.getTime()) / 1000) + "s");
						return;
					}
			}
			
			if (this.isMezzed())
			{
				getBukkitPlayer().sendMessage("* You cannot use an ability while mezzed!");
				return;
			}
			
			if (this.isStunned())
			{
				getBukkitPlayer().sendMessage("* You cannot use an ability while stunned!");
				return;
			}

			startCasting(spell, getBukkitPlayer(), useMana, useReagents, ignoreProfessionAndLevel,
					requiredWeaponSkillType);
		} catch (CoreStateInitException e) {

		}
	}

	private void tryForceTargetIfNeededForSpell(ISoliniaSpell spell) {
		try
		{
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
					|| Utils.getSpellTargetType(spell.getTargettype()).equals(SpellTargetType.UndeadAE)) {
				this.setEntityTarget(getBukkitPlayer());
			} else if (Utils.getSpellTargetType(spell.getTargettype()).equals(SpellTargetType.Pet)) {
				LivingEntity pet = StateManager.getInstance().getEntityManager()
						.getPet(getBukkitPlayer().getUniqueId());
				if (pet != null) {
					this.setEntityTarget(pet);
					return;
				} 
			} 
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

		try {
			tryCastSpell(StateManager.getInstance().getConfigurationManager().getSpell(item.getAbilityid()), true, true,
					false, item.getRequiredWeaponSkillType());
		} catch (CoreStateInitException e) {

		}
	}

	

	@Override
	public void tryThrowItemInMainHand(Cancellable cancellableEvent) {
		try {
			ItemStack itemstack = this.getBukkitPlayer().getEquipment().getItemInMainHand();
			if ((!ItemStackUtils.IsSoliniaItem(itemstack)))
				return;

			if (this.isMezzed())
			{
				getBukkitPlayer().sendMessage("* You cannot do this while mezzed!!");
				return;
			}
			
			if (this.isStunned())
			{
				getBukkitPlayer().sendMessage("* You cannot do this while stunned!");
				return;
			}
			
			// We have our custom item id, lets check it exists
			ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(itemstack);

			if (item == null)
				return;

			if (!item.isThrowing())
				return;

			int newAmount = itemstack.getAmount() - 1;

			if (getEntityTarget() == null) {
				getBukkitPlayer().sendMessage("* You must select a target [See keybinds]");
			}
			LivingEntity targetmob = getEntityTarget();
			if (targetmob != null && !targetmob.getUniqueId().equals(getBukkitPlayer().getUniqueId())) {
				if (item.useItemOnEntity(getBukkitPlayer(), targetmob, false) == true) {
					if (newAmount < 1) {
						// To prevent a trap you must cancel event here
						Utils.CancelEvent(cancellableEvent);
						getBukkitPlayer().getEquipment().setItemInMainHand(null);
						getBukkitPlayer().updateInventory();
					} else {
						// To prevent a trap you must cancel event here
						Utils.CancelEvent(cancellableEvent);
						itemstack.setAmount(newAmount);
						getBukkitPlayer().getEquipment().setItemInMainHand(itemstack);
						getBukkitPlayer().updateInventory();
					}
					return;
				} else {
					return;
				}
			}
		} catch (CoreStateInitException e) {

		}
	}

	@Override
	public void tryApplyAugmentation(ISoliniaItem item) {
		try {
			// Start applying an augmentation
			if (item.isAugmentation()) {
				if (StateManager.getInstance().getPlayerManager()
						.getApplyingAugmentation(getBukkitPlayer().getUniqueId()) == null
						|| StateManager.getInstance().getPlayerManager()
								.getApplyingAugmentation(getBukkitPlayer().getUniqueId()) == 0) {
					StateManager.getInstance().getPlayerManager()
							.setApplyingAugmentation(getBukkitPlayer().getUniqueId(), item.getId());
					getBukkitPlayer().sendMessage("* Applying " + item.getDisplayname()
							+ " to an item, please right click the item you wish to apply this augmentation to. . To cancel applying, right click while holding this item again");
				} else {
					StateManager.getInstance().getPlayerManager()
							.setApplyingAugmentation(getBukkitPlayer().getUniqueId(), 0);
					getBukkitPlayer().sendMessage("* Cancelled applying augmentation");
				}
			}
		} catch (CoreStateInitException e) {

		}
		this.setLastUpdatedTimeNow();

		return;
	}

	@Override
	public void interact(PlayerInteractEvent event) {
		if (event.getItem() == null)
			return;

		if (event.getHand() != EquipmentSlot.HAND)
			return;

		if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;

		if (event.getItem() == null || event.getItem().getType() == null
				|| event.getItem().getType().equals(Material.AIR))
			return;

		if (!ItemStackUtils.IsSoliniaItem(event.getItem()))
			return;

		InteractUsingSoliniaItem(event.getItem(), event);
	}

	private void InteractUsingSoliniaItem(ItemStack itemstack, Cancellable cancellableEvent) {

		try {
			if (itemstack == null)
				return;
			
			if (this.isMezzed())
			{
				getBukkitPlayer().sendMessage("* You cannot do this while mezzed!");
				return;
			}
			
			if (this.isStunned())
			{
				getBukkitPlayer().sendMessage("* You cannot do this while stunned!");
				return;
			}

			// We have our custom item id, lets check it exists
			ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(itemstack);

			if (ItemStackUtils.isPotion(itemstack)) {
				// Handled by on consume event
				return;
			}

			if (item == null)
				return;

			if (item.isAugmentation()) {
				tryApplyAugmentation(item);
				return;
			}

			if (item.getMinLevel() > getLevel()) {
				getBukkitPlayer().sendMessage("This item requires minimum level: " + item.getMinLevel());
				return;
			}

			// Not a clicky!
			if (!item.isThrowing() && item.getLanguagePrimer().equals("") && (item.getAbilityid() < 1))
				return;

			if (item.isThrowing()) {
				tryThrowItemInMainHand(cancellableEvent);
				return;
			}

			// Some spells auto target self, if they don't have a target try to do that
			if (getEntityTarget() == null) {
				if (item.getAbilityid() > 0) {
					ISoliniaSpell spell = StateManager.getInstance().getConfigurationManager()
							.getSpell(item.getAbilityid());
					if (spell != null) {
						if (!tryFixSpellTarget(spell)) {
							getBukkitPlayer().sendMessage("* You must select a target [See keybinds]");
							return;
						}
					}
				}
			}

			if (getEntityTarget() == null) {
				getBukkitPlayer().sendMessage("* You must select a target [See keybinds]");
				return;
			}

			// we should probably check line of sight here for detrimentals, or maybe in the
			// spell

			// try consume language primer
			if (!item.getLanguagePrimer().equals("")) {
				item.useItemOnEntity(getBukkitPlayer(), getBukkitPlayer(), item.isConsumable());
				if (item.isConsumable()) {
					// To prevent a trap you must cancel event here
					Utils.CancelEvent(cancellableEvent);
					// cant be stacked so no need to test stacking
					getBukkitPlayer().getInventory().setItemInMainHand(null);
					getBukkitPlayer().updateInventory();
				}
				return;
			}
			
			if (StateManager.getInstance().getEntityManager().getEntitySpellCooldown(this.getBukkitPlayer(),
					item.getAbilityid()) != null) {
				LocalDateTime datetime = LocalDateTime.now();
				Timestamp nowtimestamp = Timestamp.valueOf(datetime);
				Timestamp expiretimestamp = StateManager.getInstance().getEntityManager()
						.getEntitySpellCooldown(this.getBukkitPlayer(), item.getAbilityid());

				if (expiretimestamp != null)
					if (!nowtimestamp.after(expiretimestamp)) {
						this.getBukkitPlayer().sendMessage("You do not have enough willpower to use this item " + item.getDisplayname()
								+ " (Wait: " + ((expiretimestamp.getTime() - nowtimestamp.getTime()) / 1000) + "s");
						return;
					}
			}
			
			if (item.isConsumable() == true && !item.getConsumableRequireQuestFlag().equals(""))
			{
				if (!SoliniaPlayerAdapter.Adapt(getBukkitPlayer()).hasQuestFlag(item.getConsumableRequireQuestFlag()))
				{
					getBukkitPlayer().sendMessage("* This item does not appear to work [missing queststep]");
					return;
				}
			}

			if (item.isConsumable() == true && !item.getConsumableRequireNotQuestFlag().equals(""))
			{
				if (SoliniaPlayerAdapter.Adapt(getBukkitPlayer()).hasQuestFlag(item.getConsumableRequireNotQuestFlag()))
				{
					getBukkitPlayer().sendMessage("* This item appears to no longer work [questitem no longer needed]");
					return;
				}
			}

			// Spell scrolls no longer support right click
			if (item.getAbilityid() < 1) {
				return;
			}

			if (item.isSpellscroll()) {
				getBukkitPlayer().sendMessage(
						"* To use this spell you must first pick up the spell and drop it into the spells button in the top right corner of your inventory screen. You can then memorise it from your spellbook (By default K button)");
				return;
			}

			// Some spells auto target self, if they don't have a target try to do that
			ISoliniaSpell spell = StateManager.getInstance().getConfigurationManager().getSpell(item.getAbilityid());
			if (spell != null) {
				this.tryForceTargetIfNeededForSpell(spell);

				if (getEntityTarget() == null) {
					if (!tryFixSpellTarget(spell)) {
						getBukkitPlayer().sendMessage("* You must select a target [See keybinds]");
						return;
					}
				}
			}

			if (getEntityTarget() == null) {
				getBukkitPlayer().sendMessage("* You must select a target [See keybinds]");
				return;
			}

			Utils.DebugLog("SoliniaPlayer", "interact", this.getBukkitPlayer().getName(),
					"consumable status: " + item.isConsumable());

			// Only applies to consumable items
			if (item.isConsumable()) {

				int newAmount = itemstack.getAmount() - 1;
				Utils.DebugLog("SoliniaPlayer", "interact", this.getBukkitPlayer().getName(), "using consumable item");
				item.useItemOnEntity(getBukkitPlayer(), getEntityTarget(), true);
				if (newAmount < 1) {
					// To prevent a trap you must cancel event here
					Utils.CancelEvent(cancellableEvent);
					getBukkitPlayer().getInventory().setItem(getBukkitPlayer().getInventory().getHeldItemSlot(), null);
					getBukkitPlayer().updateInventory();
					return;
				} else {
					// To prevent a trap you must cancel event here
					Utils.CancelEvent(cancellableEvent);
					itemstack.setAmount(newAmount);
					getBukkitPlayer().getInventory().setItem(getBukkitPlayer().getInventory().getHeldItemSlot(), itemstack);
					getBukkitPlayer().updateInventory();
					return;
				}
			}

			// Only applies to non-consumable items
			if (!item.isConsumable() && !itemstack.getType().equals(Material.ENCHANTED_BOOK)) {
				Utils.DebugLog("SoliniaPlayer", "interact", this.getBukkitPlayer().getName(),
						"using non consumable item");
				item.useItemOnEntity(getBukkitPlayer(), getEntityTarget(), true);
				return;
			}

			// Only applies to spell effects
			if (!itemstack.getType().equals(Material.ENCHANTED_BOOK)) {
				return;
			}

			// Reroute action depending on target
			ISoliniaLivingEntity solentity = SoliniaLivingEntityAdapter.Adapt((LivingEntity) getBukkitPlayer());
			if (!item.isConsumable()
					&& spell.getActSpellCost(solentity) > SoliniaPlayerAdapter.Adapt(getBukkitPlayer()).getMana()) {
				getBukkitPlayer().sendMessage(ChatColor.GRAY + "Insufficient Mana  [E]");
				return;
			}

			if (!spell.getRequiresPermissionNode().equals("")) {
				if (!getBukkitPlayer().hasPermission(spell.getRequiresPermissionNode())) {
					getBukkitPlayer().sendMessage("This requires a permission node you do not have");
					return;
				}
			}

			if (!item.isConsumable() && StateManager.getInstance().getEntityManager()
					.getEntitySpellCooldown(getBukkitPlayer(), spell.getId()) != null) {
				LocalDateTime datetime = LocalDateTime.now();
				Timestamp nowtimestamp = Timestamp.valueOf(datetime);
				Timestamp expiretimestamp = StateManager.getInstance().getEntityManager()
						.getEntitySpellCooldown(getBukkitPlayer(), spell.getId());

				if (expiretimestamp != null)
					if (!nowtimestamp.after(expiretimestamp)) {
						getBukkitPlayer().sendMessage("You do not have enough willpower to cast " + spell.getName()
								+ " (Wait: " + ((expiretimestamp.getTime() - nowtimestamp.getTime()) / 1000) + "s");
						return;
					}
			}
			
			if (this.isMezzed())
			{
				getBukkitPlayer().sendMessage("* You cannot use an ability while mezzed!");
				return;
			}
			
			if (this.isStunned())
			{
				getBukkitPlayer().sendMessage("* You cannot use an ability while stunned!");
				return;
			}

			startCasting(spell, getBukkitPlayer(), !item.isConsumable(), !item.isConsumable(), false,
					item.getRequiredWeaponSkillType());

		} catch (CoreStateInitException e) {
			e.printStackTrace();
		}
	}

	private boolean tryFixSpellTarget(ISoliniaSpell spell) {
		try {
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
					|| Utils.getSpellTargetType(spell.getTargettype()).equals(SpellTargetType.UndeadAE)) {
				this.setEntityTarget(getBukkitPlayer());
				return true;
			} else if (Utils.getSpellTargetType(spell.getTargettype()).equals(SpellTargetType.Pet)) {
				LivingEntity pet = StateManager.getInstance().getEntityManager()
						.getPet(getBukkitPlayer().getUniqueId());
				if (pet != null) {
					this.setEntityTarget(pet);
					return true;
				} else {
					Utils.SendHint(getBukkitPlayer(), HINT.NEED_TARGET, "fixspelltargetI", false);
					return false;
				}
			} else {
				Utils.SendHint(getBukkitPlayer(), HINT.NEED_TARGET, "fixspelltargetII", false);
				return false;
			}
		} catch (CoreStateInitException e) {

		}
		return false;
	}

	@Override
	public void startCasting(ISoliniaSpell spell, Player player, boolean useMana, boolean useReagents,
			boolean ignoreProfessionAndLevel, String requiredWeaponSkillType) {
		try {

			CastingSpell castingSpell = new CastingSpell(player.getUniqueId(), spell.getId(), useMana, useReagents,
					ignoreProfessionAndLevel, requiredWeaponSkillType);
			StateManager.getInstance().getEntityManager().startCasting((LivingEntity) player, castingSpell);
		} catch (CoreStateInitException e) {

		}
	}
	
	@Override
	public void startTracking(Location location) {
		try {
		StateManager.getInstance().getEntityManager().startTracking((LivingEntity)this.getBukkitPlayer(), location);
		} catch (CoreStateInitException e) {

		}
	}
	
	@Override
	public Location getTrackingLocation() {
		try {
		return StateManager.getInstance().getEntityManager().getEntityTracking((LivingEntity)this.getBukkitPlayer());
		} catch (CoreStateInitException e) {

		}
		
		return null;
	}
	
	@Override
	public boolean isTrackingLocation() {
		if (getTrackingLocation() != null)
			return true;
		
		return false;
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

		/*
		 * if (castingSpell.getItem() == null) return;
		 */

		doCastSpell(castingSpell.getSpell(), player, castingSpell.useMana, castingSpell.useReagents,
				castingSpell.ignoreProfessionAndLevel, castingSpell.getRequiredWeaponSkillType());
	}

	@Override
	public void doCastSpell(ISoliniaSpell spell, Player player, boolean useMana, boolean useReagents,
			boolean ignoreProfessionAndLevel, String requiredWeaponSkillType) {
		
		Utils.DebugLog("SoliniaPlayer", "doCastSpell", getBukkitPlayer().getName(), "Start doCastSpell");
		
		if (!ignoreProfessionAndLevel && spell.isAASpell() && !canUseAASpell(spell)) {
			player.sendMessage("You require the correct AA to use this spell");
			return;
		}

		LivingEntity targetmob = null;

		if (getEntityTarget() == null) {
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
					|| Utils.getSpellTargetType(spell.getTargettype()).equals(SpellTargetType.UndeadAE)) {
				setEntityTarget(getBukkitPlayer());
				Utils.DebugLog("SoliniaPlayer", "doCastSpell", getBukkitPlayer().getName(), "Set target to self");

			} else {
				Utils.DebugLog("SoliniaPlayer", "doCastSpell", getBukkitPlayer().getName(), "Not target");
				Utils.SendHint(getBukkitPlayer(), HINT.NEED_TARGET, "spellitem", false);
				return;
			}
		}

		targetmob = getEntityTarget();

		if (targetmob != null) {
			double distanceOverLimit = Utils.DistanceOverAggroLimit((LivingEntity) getBukkitPlayer(), targetmob);

			if (distanceOverLimit > 0) {
				Utils.DebugLog("SoliniaPlayer", "doCastSpell", getBukkitPlayer().getName(), "Too far to interact");
				getBukkitPlayer().sendMessage("You were too far to interact with that entity");
				return;
			}
		}

		if (!ignoreProfessionAndLevel && spell.getAllowedClasses().size() > 0) {
			Utils.DebugLog("SoliniaPlayer", "doCastSpell", getBukkitPlayer().getName(), "Allowed classes over 0 and not ignoring profession and level");

			if (getClassObj() == null) {
				Utils.DebugLog("SoliniaPlayer", "doCastSpell", getBukkitPlayer().getName(), "Had no class");
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
				Utils.DebugLog("SoliniaPlayer", "doCastSpell", getBukkitPlayer().getName(), "No profession found");
				player.sendMessage(ChatColor.GRAY + " * This effect can only be used by " + professions);
				return;
			} else {
				Utils.DebugLog("SoliniaPlayer", "doCastSpell", getBukkitPlayer().getName(), "Found profession");
				if (foundlevel > 0) {
					Double level = (double) getLevel();
					if (level < foundlevel) {
						Utils.DebugLog("SoliniaPlayer", "doCastSpell", getBukkitPlayer().getName(), "Missing needed level");
						player.sendMessage(ChatColor.GRAY + " * This effect requires level " + foundlevel);
						return;
					}
				}
			}

		}

		try {
			ISoliniaLivingEntity solentity = SoliniaLivingEntityAdapter.Adapt((LivingEntity) player);
			if (solentity == null)
			{
				Utils.DebugLog("SoliniaPlayer", "doCastSpell", getBukkitPlayer().getName(), "Player had no solinialivingentity object");
				return;
			}

			if (useMana && spell.getActSpellCost(solentity) > SoliniaPlayerAdapter.Adapt(player).getMana()) {
				Utils.DebugLog("SoliniaPlayer", "doCastSpell", getBukkitPlayer().getName(), "No mana");
				player.sendMessage(ChatColor.GRAY + "Insufficient Mana [E]");
				return;
			}

			if (!spell.isBardSong()) {
				if (!hasReagents(spell,player))
				{
					Utils.DebugLog("SoliniaPlayer", "doCastSpell", getBukkitPlayer().getName(), "No reagents");
					return;
				}
			}
			
			if (targetmob != null && player != null && spell != null)
			{
				Tuple<Boolean,String> result = SoliniaSpell.isValidEffectForEntity(targetmob, player, spell);
				if (!result.a())
				{
					Utils.SendHint(player, HINT.SPELL_INVALIDEFFECT, result.b(), false);
					return;
				}
			}
		} catch (CoreStateInitException e) {
			return;
		}
		if (player != null && !player.isDead())
			if (targetmob != null && !targetmob.isDead()) {
				boolean success = spell.tryCast(player, targetmob, useMana, useReagents, requiredWeaponSkillType);
				Utils.DebugLog("SoliniaPlayer", "doCastSpell", getBukkitPlayer().getName(), "Cast success for spell id ["+spell.getId()+"] state: " + success);
				if (success == true) {
					Utils.DebugLog("SoliniaPlayer", "doCastSpell", getBukkitPlayer().getName(), "Trying to increase skill");
					tryIncreaseSkill(Utils.getSkillType(spell.getSkill()), 1);
				}
				return;
			} else {
				boolean success = spell.tryCast(player, player, useMana, useReagents, requiredWeaponSkillType);
				if (success == true) {
					Utils.DebugLog("SoliniaPlayer", "doCastSpell", getBukkitPlayer().getName(), "Trying to increase skill non dead/null mob");
					tryIncreaseSkill(Utils.getSkillType(spell.getSkill()), 1);
				}
				return;
			}
	}
	
	@Override
	public boolean hasReagents(ISoliniaSpell spell, Player player)
	{
		try
		{
			if (spell.getComponents1() > 0) {
				ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager()
						.getItem(spell.getComponents1());
				if (item == null || !item.isReagent()) {
					player.sendMessage(ChatColor.RED + "ERROR: " + ChatColor.YELLOW + "ERROR-ALERT-ADMIN-SPELL"
							+ spell.getId() + "-ID" + spell.getComponents1());
					return false;
				}
				if (!solPlayer.hasSufficientReagents(spell.getComponents1(), spell.getComponentCounts1())) {
					Utils.SendHint(player,HINT.INSUFFICIENT_REAGENTS,item.getDisplayname(),false);
					return false;
				}
			}
	
			if (spell.getComponents2() > 0) {
				ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager()
						.getItem(spell.getComponents2());
				if (item == null || !item.isReagent()) {
					player.sendMessage(ChatColor.RED + "ERROR: " + ChatColor.YELLOW + "ERROR-ALERT-ADMIN-SPELL"
							+ spell.getId() + "-ID" + spell.getComponents2());
					return false;
				}
				if (!solPlayer.hasSufficientReagents(spell.getComponents2(), spell.getComponentCounts2())) {
					Utils.SendHint(player,HINT.INSUFFICIENT_REAGENTS,item.getDisplayname(),false);
					return false;
				}
			}
	
			if (spell.getComponents3() > 0) {
				ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager()
						.getItem(spell.getComponents3());
				if (item == null || !item.isReagent()) {
					player.sendMessage(ChatColor.RED + "ERROR: " + ChatColor.YELLOW + "ERROR-ALERT-ADMIN-SPELL"
							+ spell.getId() + "-ID" + spell.getComponents3());
					return false;
				}
				if (!solPlayer.hasSufficientReagents(spell.getComponents3(), spell.getComponentCounts3())) {
					Utils.SendHint(player,HINT.INSUFFICIENT_REAGENTS,item.getDisplayname(),false);
					return false;
				}
			}
	
			if (spell.getComponents4() > 0) {
				ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager()
						.getItem(spell.getComponents4());
				if (item == null || !item.isReagent()) {
					player.sendMessage(ChatColor.RED + "ERROR: " + ChatColor.YELLOW + "ERROR-ALERT-ADMIN-SPELL"
							+ spell.getId() + "-ID" + spell.getComponents4());
					return false;
				}
				if (!solPlayer.hasSufficientReagents(spell.getComponents4(), spell.getComponentCounts4())) {
					Utils.SendHint(player,HINT.INSUFFICIENT_REAGENTS,item.getDisplayname(),false);
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
		Utils.DebugLog("SoliniaPlayer", "checkDoesntFizzle", this.getBukkitPlayer().getName(),
				"Starting checkDoesntFizzle for " + getBukkitPlayer().getName());

		if (getBukkitPlayer().isOp()) {
			Utils.DebugLog("SoliniaPlayer", "checkDoesntFizzle", this.getBukkitPlayer().getName(),
					"checkDoesntFizzle was never fizzle as player is an Op");
			return true;
		}

		// todo fizzle free features

		int no_fizzle_level = 0;
		ISoliniaAAAbility aa = null;
		try {
			if (hasAaRanks()) {
				for (ISoliniaAAAbility ability : StateManager.getInstance().getConfigurationManager()
						.getAAbilitiesBySysname("SPELLCASTINGEXPERTISE")) {
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
					Utils.DebugLog("SoliniaPlayer", "checkDoesntFizzle", this.getBukkitPlayer().getName(),
							"FoundSpellCastingExpertise level: " + rankEffect.getBase1());

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
						Utils.DebugLog("SoliniaPlayer", "checkDoesntFizzle", this.getBukkitPlayer().getName(),
								"FoundMasteryOfPast level: " + rankEffect.getBase1());

						if (rankEffect.getBase1() > no_fizzle_level)
							no_fizzle_level = rankEffect.getBase1();
					}
				}
			}
		} catch (CoreStateInitException e) {

		}

		Utils.DebugLog("SoliniaPlayer", "checkDoesntFizzle", this.getBukkitPlayer().getName(),
				"MinLevelClass check: " + spell.getMinLevelClass(getClassObj().getName())
						+ " is less than no_fizzle_level " + no_fizzle_level + "?");
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

			SoliniaPlayerSkill playerSkill = getSkill(Utils.getSkillType(spell.getSkill()));

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
	public boolean understandsLanguage(SkillType languageSkillType) {

		if (getRace() != null)
			if (getRace().getLanguage().equals(languageSkillType))
				return true;

		SoliniaPlayerSkill soliniaskill = getSkill(languageSkillType);
		if (soliniaskill != null && soliniaskill.getValue() >= 100) {
			return true;
		}
		return false;
	}

	@Override
	public int getLanguageLearnedPercent(SkillType languageSkillType) {

		if (getRace() != null)
			if (getRace().getLanguage().equals(languageSkillType))
				return 100;

		SoliniaPlayerSkill soliniaskill = getSkill(languageSkillType);
		if (soliniaskill != null && soliniaskill.getValue() >= 0) {
			return soliniaskill.getValue();
		}
		return 0;
	}

	@Override
	public void tryImproveLanguage(SkillType languageSkillType) {
		if (getRace() != null)
			if (getRace().getLanguage().equals(languageSkillType))
				return;

		if (getSkill(languageSkillType).getValue() >= 100)
			return;

		tryIncreaseSkill(languageSkillType, 1);
		this.setLastUpdatedTimeNow();

	}

	@Override
	public ISoliniaGroup getGroup() {
		// TODO Auto-generated method stub
		return StateManager.getInstance().getGroupByMember(this.getBukkitPlayer().getUniqueId());
	}
	
	@Override
	public Fellowship getFellowship() {
		// TODO Auto-generated method stub
		if (this.getCharacterFellowshipId() < 1)
			return null;
		
		try {
			return StateManager.getInstance().getConfigurationManager().getFellowship(this.getCharacterFellowshipId());
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
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
		this.setLastUpdatedTimeNow();

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
		this.setLastUpdatedTimeNow();

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
	public boolean hasAaRanks() {
		return (ranks.size() > 0);
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
		this.setLastUpdatedTimeNow();

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
	public boolean canDisarm() {
		if (getClassObj() == null)
			return false;

		if (getClassObj().canDisarm() == false)
			return false;

		if (getClassObj().getDisarmLevel() > getLevel())
			return false;

		return true;
	}

	@Override
	public boolean getDodgeCheck() {
		if (canDodge() == false)
			return false;

		int chance = getSkill(SkillType.Dodge).getValue();
		chance += 100;
		chance /= 40;

		return Utils.RandomBetween(1, 500) <= chance;
	}

	@Override
	public boolean getRiposteCheck() {
		if (canRiposte() == false)
			return false;

		int chance = getSkill(SkillType.Riposte).getValue();
		chance += 100;
		chance /= 50;

		return Utils.RandomBetween(1, 500) <= chance;
	}

	@Override
	public boolean getDoubleAttackCheck() {
		if (canDoubleAttack() == false)
			return false;

		int chance = getSkill(SkillType.DoubleAttack).getValue();
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

		int chance = getSkill(SkillType.SafeFall).getValue();
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
		this.setLastUpdatedTimeNow();

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
		this.setLastUpdatedTimeNow();

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
		int upperCap = 1500 - playerFactionEntry.getFaction().getBase();

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
		this.setLastUpdatedTimeNow();

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
		int lowerCap = -1500 - playerFactionEntry.getFaction().getBase();

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
		this.setLastUpdatedTimeNow();

	}

	@Override
	public void ignorePlayer(Player player) {
		if (ignoredPlayers.contains(player.getUniqueId())) {
			ignoredPlayers.remove(player.getUniqueId());
		} else {
			ignoredPlayers.add(player.getUniqueId());
		}
		this.setLastUpdatedTimeNow();

	}

	@Override
	public List<String> getAvailableTitles() {
		return availableTitles;
	}

	@Override
	public void setAvailableTitles(List<String> availableTitles) {
		this.availableTitles = availableTitles;
		this.setLastUpdatedTimeNow();

	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public void setTitle(String title) {
		this.title = title;
		this.setLastUpdatedTimeNow();

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
		this.setLastUpdatedTimeNow();

	}

	@Override
	public void addPlayerQuest(int questId) {

		PlayerQuest quest = new PlayerQuest();
		quest.setComplete(false);
		quest.setQuestId(questId);
		this.getPlayerQuests().add(quest);
		this.getBukkitPlayer().sendMessage(ChatColor.YELLOW + " * You have received a new Quest ["
				+ quest.getQuest().getName() + "]! See /quests for more info");
		this.setLastUpdatedTimeNow();

	}

	@Override
	public List<String> getPlayerQuestFlags() {
		// TODO Auto-generated method stub
		return playerQuestFlags;
	}

	@Override
	public void setPlayerQuestFlags(List<String> playerQuestFlags) {
		this.playerQuestFlags = playerQuestFlags;
		this.setLastUpdatedTimeNow();

	}

	@Override
	public void addPlayerQuestFlag(String questFlag) {
		playerQuestFlags.add(questFlag);
		this.getBukkitPlayer()
				.sendMessage(ChatColor.YELLOW + " * You have received a new Quest Flag! See /quests for more info");
		this.setLastUpdatedTimeNow();

	}

	@Override
	public boolean isMeditating() {
		Location currentLocation = getBukkitPlayer().getLocation();
		if (currentLocation.getBlockX() == lastX && currentLocation.getBlockY() == lastY
				&& currentLocation.getBlockZ() == lastZ) {
			return true;

		} else {
			return false;
		}
	}

	@Override
	public void setSkills(List<SoliniaPlayerSkill> skills) {
		this.skills = skills;
		this.setLastUpdatedTimeNow();

	}

	@Override
	public String getSpecialisation() {
		return specialisation;
	}

	@Override
	public void setSpecialisation(String specialisation) {
		this.specialisation = specialisation;
		this.setLastUpdatedTimeNow();

	}
	
	@Override
	public boolean isUndead() {
		if (getRace() != null)
			if (getRace().isUndead())
				return true;

		return false;
	}
	
	@Override
	public boolean isPlant() {
		if (getRace() != null)
			if (getRace().isPlant())
				return true;

		return false;
	}
	
	@Override
	public boolean isAnimal() {
		if (getRace() != null)
			if (getRace().isUndead())
				return true;

		return false;
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
		this.setLastUpdatedTimeNow();

	}

	@Override
	public int getInspiration() {
		return inspiration;
	}

	@Override
	public void setInspiration(int inspiration) {
		this.inspiration = inspiration;
		this.setLastUpdatedTimeNow();

	}

	@Override
	public Timestamp getExperienceBonusExpires() {
		return experienceBonusExpires;
	}

	@Override
	public void setExperienceBonusExpires(Timestamp experienceBonusExpires) {
		this.experienceBonusExpires = experienceBonusExpires;
		this.setLastUpdatedTimeNow();

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
		this.setLastUpdatedTimeNow();

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
	public boolean isModMessageEnabled() {
		return modMessageEnabled;
	}

	@Override
	public void setModMessageEnabled(boolean oocEnabled) {
		this.modMessageEnabled = oocEnabled;
		this.setLastUpdatedTimeNow();

	}

	@Override
	public void setBindPoint(String teleportlocation) {
		this.bindPoint = teleportlocation;
		this.setLastUpdatedTimeNow();

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
		this.setLastUpdatedTimeNow();

	}

	@Override
	public void killAllPets() {
		EntityUtils.KillAllPets(this.getBukkitPlayer().getUniqueId());
		this.setLastUpdatedTimeNow();

	}

	@Override
	public int getFingersItem() {
		return fingersItem;
	}

	@Override
	public void setFingersItem(int fingersItem) {
		this.fingersItem = fingersItem;
		sendSlotsAsPacket();
		this.setLastUpdatedTimeNow();

	}

	@Override
	public int getShouldersItem() {
		return shouldersItem;
	}

	@Override
	public void setShouldersItem(int shouldersItem) {
		this.shouldersItem = shouldersItem;
		sendSlotsAsPacket();
		this.setLastUpdatedTimeNow();

	}

	@Override
	public int getEarsItem() {
		return earsItem;
	}

	@Override
	public void setEarsItem(int earsItem) {
		this.earsItem = earsItem;
		sendSlotsAsPacket();
		this.setLastUpdatedTimeNow();

	}

	@Override
	public int getNeckItem() {
		return neckItem;
	}

	@Override
	public void setNeckItem(int neckItem) {
		this.neckItem = neckItem;
		sendSlotsAsPacket();
		this.setLastUpdatedTimeNow();

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
	public void sendSlotsAsPacket() {
		try {
			PacketEquipSlots packet = new PacketEquipSlots();
			packet.fromData(getEquipSlots());
			ForgeUtils.QueueSendForgeMessage(((Player) getBukkitPlayer()), Solinia3UIChannelNames.Outgoing,
					Solinia3UIPacketDiscriminators.EQUIPSLOTS, packet.toPacketData(), 0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void setFeigned(boolean feigned) {
		try {
			StateManager.getInstance().getEntityManager().setFeignedDeath(getBukkitPlayer().getUniqueId(), feigned);
			if (feigned == true) {
				getBukkitPlayer().sendMessage(ChatColor.GRAY + "* You feign your death");
				clearTargetsAgainstMe();

			} else {
				getBukkitPlayer().sendMessage(ChatColor.GRAY + "* You are no longer feigning death!");
			}
		} catch (CoreStateInitException e) {

		}
		this.setLastUpdatedTimeNow();

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

			if (this.getForearmsItem() > 0) {
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager()
						.getItem(this.getForearmsItem());
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

			if (this.getWaistItem() > 0) {
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(this.getWaistItem());
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
	public boolean getSkillCheck(SkillType skillType, int trivial) {
		SoliniaPlayerSkill skill = this.getSkill(skillType);

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
		this.setLastUpdatedTimeNow();

	}

	@Override
	public int getMotherCharacterId() {
		return motherCharacterId;
	}

	@Override
	public void setMotherCharacterId(int motherCharacterId) {
		this.motherCharacterId = motherCharacterId;
		this.setLastUpdatedTimeNow();

	}

	@Override
	public int getSpouseCharacterId() {
		return spouseCharacterId;
	}

	@Override
	public void setSpouseCharacterId(int spouseCharacterId) {
		this.spouseCharacterId = spouseCharacterId;
		this.setLastUpdatedTimeNow();

	}

	@Override
	public void sendFamilyTree() {
		this.getBukkitPlayer().sendMessage("Family Tree for " + this.getFullNameWithTitle());
		this.getBukkitPlayer().sendMessage("--------------------");
		String self = this.getFullName();
		String spouse = "";
		try {
			if (spouseCharacterId > 0) {

				ISoliniaPlayer spousePlayer = StateManager.getInstance().getConfigurationManager()
						.getCharacterById(spouseCharacterId);
				spouse = spousePlayer.getFullName();

			}

			this.getBukkitPlayer().sendMessage(self + " -> " + spouse);

			for (ISoliniaPlayer player : StateManager.getInstance().getConfigurationManager().getCharacters()) {
				if (player.getMotherCharacterId() < 1)
					continue;

				if (player.getMotherCharacterId() == getId()) {
					this.getBukkitPlayer().sendMessage("Child: " + player.getFullName());
				} else {
					if (spouseCharacterId > 0) {
						if (player.getMotherCharacterId() == spouseCharacterId) {
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
	public boolean hasArrowsInInventory() {
		if (this.getBukkitPlayer().getInventory().first(Material.ARROW) != -1)
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
		this.setLastUpdatedTimeNow();

	}

	// Used to schedule a HP update after an event
	// For example if a player has changed his items
	@Override
	public void scheduleUpdateMaxHp() {
		if (this.getBukkitPlayer() == null)
			return;

		final UUID playerUUID = this.getBukkitPlayer().getUniqueId();

		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(StateManager.getInstance().getPlugin(),
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
		this.setLastUpdatedTimeNow();

	}

	@Override
	public void toggleAutoAttack() {
		try {
			StateManager.getInstance().getEntityManager().toggleAutoAttack(this.getBukkitPlayer());
		} catch (CoreStateInitException e) {

		}
	}

	@Override
	public boolean bindWound(ISoliniaLivingEntity solLivingEntity) {
		if (solLivingEntity == null)
			return false;

		if (solLivingEntity.getBukkitLivingEntity() == null)
			return false;

		if (solLivingEntity.getBukkitLivingEntity().isDead()) {
			getBukkitPlayer().sendMessage("It is pointless to bind wounds for that which is dead");
			return false;
		}

		if (this.isBindWoundCountdown()) {
			getBukkitPlayer().sendMessage(
					"You are still applying your previous bandages, you must wait before attempting to bandage more (10 seconds to complete)");
			return false;
		}

		if (this.isFeignedDeath()) {
			getBukkitPlayer().sendMessage("You cannot bind while feigned");
			return false;
		}

		try {
			ISoliniaLivingEntity playersolLivingEntity = SoliniaLivingEntityAdapter
					.Adapt((LivingEntity) this.getBukkitPlayer());

			if (solLivingEntity.getBukkitLivingEntity().getLocation().distance(getBukkitPlayer().getLocation()) > 4) {
				getBukkitPlayer().sendMessage("Your target is too far away to bind wound");
				return false;
			}

			// give some chance to try to bind wound early on in life
			// later on only do this after success
			boolean triedSkillIncrease = false;
			if (this.getSkill(SkillType.BindWound).getValue() < 30) {
				tryIncreaseSkill(SkillType.BindWound, 1);
				triedSkillIncrease = true;
			}

			int percent_base = 50;

			this.getSkill(SkillType.BindWound);

			String className = "UNKNOWN";
			if (getClassObj() != null)
				className = getClassObj().getName().toUpperCase();

			if (getSkill(SkillType.BindWound).getValue() > 200) {
				if (className.equals("MONK") || className.equals("BEASTLORD"))
					percent_base = 70;
				else if ((getLevel() > 50)
						&& (className.equals("WARRIOR") || (className.equals("ROGUE") || (className.equals("CLERIC")))))
					percent_base = 70;
			}

			int percent_bonus = 0;
			// how could this possibly work for other classes
			// if (percent_base >= 70)
			// {
			if (playersolLivingEntity != null)
				percent_bonus = playersolLivingEntity.getMaxBindWound_SE();
			// }

			int max_percent = percent_base + percent_bonus;
			if (max_percent < 0)
				max_percent = 0;
			if (max_percent > 100)
				max_percent = 100;

			int max_hp = (int) Math.floor(
					(solLivingEntity.getBukkitLivingEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()
							* max_percent) / 100);
			if (max_hp > (int) Math.floor(
					solLivingEntity.getBukkitLivingEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()))
				max_hp = (int) Math.floor(
						solLivingEntity.getBukkitLivingEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());

			if (solLivingEntity.getBukkitLivingEntity().getHealth() < solLivingEntity.getBukkitLivingEntity()
					.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()
					&& solLivingEntity.getBukkitLivingEntity().getHealth() < max_hp) {
				int bindhps = 3; // base bind hp
				if (percent_base >= 70)
					bindhps = (getSkill(SkillType.BindWound).getValue() * 4) / 10; // 8:5 skill-to-hp ratio
				else if (getSkill(SkillType.BindWound).getValue() >= 12)
					bindhps = getSkill(SkillType.BindWound).getValue() / 4; // 4:1 skill-to-hp ratio

				int bonus_hp_percent = 0;
				// if (percent_base >= 70)
				// {
				if (playersolLivingEntity != null)
					bonus_hp_percent = playersolLivingEntity.getBindWound_SE();
				// }

				// getBukkitPlayer().sendMessage("Your percentbase was " + percent_base);
				// getBukkitPlayer().sendMessage("Your spell/aa effects add a binding
				// calculation was " + bindhps + " * " + bonus_hp_percent + " / 100");
				int spellModifierBenefit = (bindhps * bonus_hp_percent) / 100;
				// getBukkitPlayer().sendMessage("Your spell/aa effects add a binding addition
				// of " + spellModifierBenefit + " hp");

				bindhps += spellModifierBenefit;

				if (bindhps < 3)
					bindhps = 3;

				if (triedSkillIncrease == false) {
					tryIncreaseSkill(SkillType.BindWound, 1);
					triedSkillIncrease = true;
				}

				LocalDateTime datetime = LocalDateTime.now();
				Timestamp nowtimestamp = Timestamp.valueOf(datetime);
				StateManager.getInstance().getEntityManager().setLastBindwound(this.getBukkitPlayer().getUniqueId(),
						nowtimestamp);

				getBukkitPlayer().sendMessage(
						"You bind " + solLivingEntity.getName() + "'s wounds for " + (int) bindhps + " hp");
				if (solLivingEntity.getBukkitLivingEntity() instanceof Player
						&& !solLivingEntity.getBukkitLivingEntity().getUniqueId().toString()
								.equals(getBukkitPlayer().getUniqueId().toString()))
					solLivingEntity.getBukkitLivingEntity()
							.sendMessage("Your wounds are being bound by " + getBukkitPlayer().getDisplayName());

				if (!solLivingEntity.getBukkitLivingEntity().isDead()) {
					solLivingEntity.setHPChange(bindhps,getBukkitPlayer());
				}
				this.setLastUpdatedTimeNow();

				return true;
			} else {
				getBukkitPlayer().sendMessage("You cannot bind wounds above " + max_percent + "% hitpoints");
				if (solLivingEntity.getBukkitLivingEntity() instanceof Player
						&& !solLivingEntity.getBukkitLivingEntity().getUniqueId().toString()
								.equals(getBukkitPlayer().getUniqueId().toString()))
					solLivingEntity.getBukkitLivingEntity()
							.sendMessage("You cannot have your wounds bound above " + max_percent + "% hitpoints");

				this.setLastUpdatedTimeNow();

				return false;
			}

		} catch (CoreStateInitException e) {
		}
		this.setLastUpdatedTimeNow();

		return false;
	}

	private boolean isBindWoundCountdown() {
		try {
			Timestamp expiretimestamp = StateManager.getInstance().getEntityManager().getLastBindwound()
					.get(this.getBukkitPlayer().getUniqueId());
			if (expiretimestamp != null) {
				LocalDateTime datetime = LocalDateTime.now();
				Timestamp nowtimestamp = Timestamp.valueOf(datetime);
				Timestamp mintimestamp = Timestamp
						.valueOf(expiretimestamp.toLocalDateTime().plus(10, ChronoUnit.SECONDS));

				if (nowtimestamp.before(mintimestamp))
					return true;
			}
		} catch (CoreStateInitException e) {

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
		this.setLastUpdatedTimeNow();

	}

	@Override
	public void addXpToPendingXp(Double experience) {
		if (experience < 0)
			experience = 0d;
		this.pendingXp += experience;
		this.setLastUpdatedTimeNow();

	}

	@Override
	public int getForearmsItem() {
		return forearmsItem;
	}

	@Override
	public void setForearmsItem(int forearmsItem) {
		this.forearmsItem = forearmsItem;
		sendSlotsAsPacket();
		this.setLastUpdatedTimeNow();

	}

	@Override
	public int getArmsItem() {
		return armsItem;
	}

	@Override
	public void setArmsItem(int armsItem) {
		this.armsItem = armsItem;
		sendSlotsAsPacket();
		this.setLastUpdatedTimeNow();

	}

	@Override
	public int getHandsItem() {
		return handsItem;
	}

	@Override
	public void setHandsItem(int handsItem) {
		this.handsItem = handsItem;
		sendSlotsAsPacket();
		this.setLastUpdatedTimeNow();

	}

	@Override
	public int getWaistItem() {
		return waistItem;
	}

	@Override
	public void setWaistItem(int waistItem) {
		this.waistItem = waistItem;
		sendSlotsAsPacket();
		this.setLastUpdatedTimeNow();

	}

	@Override
	public String getFingersItemInstance() {
		return fingersItemInstance;
	}

	@Override
	public void setFingersItemInstance(String fingersItemInstance) {
		this.fingersItemInstance = fingersItemInstance;
		this.setLastUpdatedTimeNow();

	}

	@Override
	public String getShouldersItemInstance() {
		return shouldersItemInstance;
	}

	@Override
	public void setShouldersItemInstance(String shouldersItemInstance) {
		this.shouldersItemInstance = shouldersItemInstance;
		this.setLastUpdatedTimeNow();

	}

	@Override
	public String getNeckItemInstance() {
		return neckItemInstance;
	}

	@Override
	public void setNeckItemInstance(String neckItemInstance) {
		this.neckItemInstance = neckItemInstance;
		this.setLastUpdatedTimeNow();

	}

	@Override
	public String getEarsItemInstance() {
		return earsItemInstance;
	}

	@Override
	public void setEarsItemInstance(String earsItemInstance) {
		this.earsItemInstance = earsItemInstance;
		this.setLastUpdatedTimeNow();

	}

	@Override
	public String getForearmsItemInstance() {
		return forearmsItemInstance;
	}

	@Override
	public void setForearmsItemInstance(String forearmsItemInstance) {
		this.forearmsItemInstance = forearmsItemInstance;
		this.setLastUpdatedTimeNow();

	}

	@Override
	public String getArmsItemInstance() {
		return armsItemInstance;
	}

	@Override
	public void setArmsItemInstance(String armsItemInstance) {
		this.armsItemInstance = armsItemInstance;
		this.setLastUpdatedTimeNow();

	}

	@Override
	public String getHandsItemInstance() {
		return handsItemInstance;
	}

	@Override
	public void setHandsItemInstance(String handsItemInstance) {
		this.handsItemInstance = handsItemInstance;
		this.setLastUpdatedTimeNow();

	}

	@Override
	public String getWaistItemInstance() {
		return waistItemInstance;
	}

	@Override
	public void setWaistItemInstance(String waistItemInstance) {
		this.waistItemInstance = waistItemInstance;
		this.setLastUpdatedTimeNow();

	}

	@Override
	public ISoliniaLivingEntity getSoliniaLivingEntity() {
		try {
			return SoliniaLivingEntityAdapter.Adapt((LivingEntity) getBukkitPlayer());
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
		this.setLastUpdatedTimeNow();

	}

	@Override
	public Personality getPersonality() {
		return personality;
	}

	@Override
	public void setPersonality(Personality personality) {
		this.personality = personality;
		this.setLastUpdatedTimeNow();

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
		this.setLastUpdatedTimeNow();

	}

	@Override
	public void increaseMonthlyVote(Integer amount) {
		Calendar now = Calendar.getInstance();
		String month = now.get(Calendar.YEAR) + "-" + now.get(Calendar.MONTH);
		if (this.monthlyVote.get(month) == null) {
			this.monthlyVote.put(month, 0);
		}
		this.monthlyVote.put(month, this.monthlyVote.get(month) + amount);
		this.setLastUpdatedTimeNow();

	}

	@Override
	public Integer getMonthlyVote() {
		Calendar now = Calendar.getInstance();
		String month = now.get(Calendar.YEAR) + "-" + now.get(Calendar.MONTH);
		if (this.monthlyVote.get(month) == null) {
			return 0;
		}
		return this.monthlyVote.get(month);
	}

	@Override
	public void setEquipSlotItem(com.solinia.solinia.Models.EquipmentSlot slot, int itemId) {

		switch (slot) {
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
		this.setLastUpdatedTimeNow();

	}

	@Override
	public String getBase64InventoryContents() {
		return base64InventoryContents;
	}

	@Override
	public void setBase64InventoryContents(String base64InventoryContents) {
		this.base64InventoryContents = base64InventoryContents;
		this.setLastUpdatedTimeNow();

	}

	@Override
	public String getBase64ArmorContents() {
		return base64ArmorContents;
	}

	@Override
	public void setBase64ArmorContents(String base64ArmorContents) {
		this.base64ArmorContents = base64ArmorContents;
		this.setLastUpdatedTimeNow();

	}

	@Override
	public ItemStack[] getStoredArmorContents() {
		if (getBase64ArmorContents() == null || getBase64ArmorContents().equals(""))
			return new ItemStack[0];

		try {
			String yaml = TextUtils.FromBase64UTF8(getBase64ArmorContents());
			return ItemStackUtils.itemStackArrayFromYamlString(yaml);
		} catch (Exception e) {
			System.out.println("Exception converting base64 to itemstack array [armorcontents] for player "
					+ getBukkitPlayer().getName() + ": " + getBase64ArmorContents());
			e.printStackTrace();
			return new ItemStack[0];
		}
	}
	
	@Override
	public ItemStack[] getStoredBankContents() {
		if (getBase64BankContents() == null || getBase64BankContents().equals(""))
			return new ItemStack[0];

		try {
			String yaml = TextUtils.FromBase64UTF8(getBase64BankContents());
			return ItemStackUtils.itemStackArrayFromYamlString(yaml);
		} catch (Exception e) {
			System.out.println("Exception converting base64 to itemstack array [bankcontents] for player "
					+ getBukkitPlayer().getName() + ": " + getBase64BankContents());
			e.printStackTrace();
			return new ItemStack[0];
		}
	}

	@Override
	public ItemStack[] getStoredInventoryContents() {
		if (getBase64InventoryContents() == null || getBase64InventoryContents().equals(""))
			return new ItemStack[0];

		try {
			String yaml = TextUtils.FromBase64UTF8(getBase64InventoryContents());
			return ItemStackUtils.itemStackArrayFromYamlString(yaml);
		} catch (Exception e) {
			System.out.println("Exception converting base64 to itemstack array [inventorycontents] for player "
					+ getBukkitPlayer().getName() + ": " + getBase64InventoryContents());
			e.printStackTrace();
			return new ItemStack[0];
		}
	}

	@Override
	public void storeInventoryContents() {
		this.setBase64InventoryContents(TextUtils.ToBase64UTF8(ItemStackUtils
				.itemStackArrayToYamlString(this.getBukkitPlayer().getInventory().getContents())));
		this.setLastUpdatedTimeNow();

	}

	@Override
	public void storeArmorContents() {
		this.setBase64ArmorContents(TextUtils.ToBase64UTF8(ItemStackUtils
				.itemStackArrayToYamlString(this.getBukkitPlayer().getInventory().getArmorContents())));
		this.setLastUpdatedTimeNow();

	}
	
	@Override
	public void storeBankContents(Inventory inventory) {
		this.setBase64BankContents(TextUtils.ToBase64UTF8(ItemStackUtils
				.itemStackArrayToYamlString(inventory.getContents())));
		this.setLastUpdatedTimeNow();

	}

	@Override
	public void doHPRegenTick() {
		if (getBukkitPlayer().isDead())
			return;

		// Apply Crouch Mana Regen Bonus
		int manaregen = 1;

		int sleephpregen = 0;
		// Sleep regen
		if (getBukkitPlayer().isSleeping()) {
			sleephpregen += 50;
		}

		// Hp and Mana Regen from Items
		int hpregen = 0;

		ISoliniaAAAbility hpaa = null;
		try {
			if (hasAaRanks()) {
				for (ISoliniaAAAbility ability : StateManager.getInstance().getConfigurationManager()
						.getAAbilitiesBySysname("INNATEREGENERATION")) {
					if (!hasAAAbility(ability.getId()))
						continue;

					hpaa = ability;
				}
			}
		} catch (CoreStateInitException e) {

		}

		int aahpregenrank = 0;

		if (hpaa != null) {
			aahpregenrank = Utils.getRankPositionOfAAAbility(getBukkitPlayer(), hpaa);
			hpregen += aahpregenrank;
		}

		hpregen += sleephpregen;

		// Process HP Regeneration
		if (hpregen > 0) {
			if (!getBukkitPlayer().isDead())
				getSoliniaLivingEntity().setHPChange(hpregen, this.getBukkitPlayer());
		}
		this.setLastUpdatedTimeNow();


	}

	@Override
	public void doMPRegenTick() {
		if (getBukkitPlayer().isDead())
			return;

		// Apply Crouch Mana Regen Bonus
		int manaregen = 1;

		int sleepmpregen = 0;
		// Sleep regen
		if (getBukkitPlayer().isSleeping()) {
			sleepmpregen += 50;
		}

		manaregen += sleepmpregen;

		// a players mana regen based on if they are meditating (sneaking)
		manaregen += getPlayerMeditatingManaBonus();

		ISoliniaAAAbility aa = null;
		try {
			if (hasAaRanks()) {
				for (ISoliniaAAAbility ability : StateManager.getInstance().getConfigurationManager()
						.getAAbilitiesBySysname("MENTALCLARITY")) {
					if (!hasAAAbility(ability.getId()))
						continue;

					aa = ability;
				}
			}

		} catch (CoreStateInitException e) {

		}

		int aamanaregenrank = 0;

		if (aa != null) {
			if (hasAaRanks())
				aamanaregenrank = Utils.getRankPositionOfAAAbility(getBukkitPlayer(), aa);
			manaregen += aamanaregenrank;
		}

		ISoliniaAAAbility emaa = null;
		try {
			if (hasAaRanks()) {
				for (ISoliniaAAAbility ability : StateManager.getInstance().getConfigurationManager()
						.getAAbilitiesBySysname("MENTALCLARITY")) {
					if (!hasAAAbility(ability.getId()))
						continue;

					emaa = ability;
				}
			}
		} catch (CoreStateInitException e) {

		}

		int emaamanaregenrank = 0;

		if (emaa != null) {
			if (hasAaRanks())
				emaamanaregenrank = Utils.getRankPositionOfAAAbility(getBukkitPlayer(), emaa);
			manaregen += emaamanaregenrank;
		}

		// Process Mana Regeneration
		// System.out.println(player.getName() + " was found to have " + manaregen + "
		// mana regen");
		increasePlayerMana(manaregen);
		this.setLastUpdatedTimeNow();

	}

	private int getPlayerMeditatingManaBonus() {
		int manaregen = 0;
		if (isMeditating()) {
			SoliniaPlayerSkill meditationskill = getSkill(SkillType.Meditation);
			int bonusmana = 3 + (meditationskill.getValue() / 15);

			manaregen += bonusmana;

			// apply meditation skill increase
			Random r = new Random();
			int randomInt = r.nextInt(100) + 1;
			if (randomInt > 90) {
				int currentvalue = 0;
				SoliniaPlayerSkill skill = getSkill(SkillType.Meditation);
				if (skill != null) {
					currentvalue = skill.getValue();
				}

				if ((currentvalue + 1) <= getSkillCap(SkillType.Meditation)) {
					setSkill(SkillType.Meditation, currentvalue + 1);
				}

			}
		}

		return manaregen;
	}

	@Override
	public void doEquipmentRegenTick(List<ISoliniaItem> items) {
		if (getBukkitPlayer().isDead())
			return;

		int hpAmount = 0;
		int mpAmount = 0;

		for (ISoliniaItem item : items) {
			// Process HP Regeneration
			if (item.getHpregen() > 0) {
				hpAmount += item.getHpregen();
			}

			if (item.getMpregen() > 0) {
				mpAmount += item.getMpregen();
			}
		}

		if (hpAmount > 0) {
			if (hpAmount > Utils.HP_REGEN_CAP)
				hpAmount = Utils.HP_REGEN_CAP;

			hpAmount = (int) Math.round(hpAmount);
			if (hpAmount < 0)
				hpAmount = 0;

			if (!getBukkitPlayer().isDead())
				getSoliniaLivingEntity().setHPChange(hpAmount, this.getBukkitPlayer());
		}

		if (mpAmount > 0) {
			if (mpAmount > Utils.MP_REGEN_CAP)
				mpAmount = Utils.MP_REGEN_CAP;

			increasePlayerMana(mpAmount);
		}
		this.setLastUpdatedTimeNow();

	}

	@Override
	public boolean isInGroup(LivingEntity targetentity) {
		try {
			if (getGroup() != null) {
				// If group members contain entity
				if (targetentity instanceof Player) {
					if (getGroup().getMembers().contains(targetentity.getUniqueId()))
						return true;
					else
						return false;
				}

				// Must be npc, check if pet
				ISoliniaLivingEntity solLivingEntity = SoliniaLivingEntityAdapter.Adapt(targetentity);
				if (solLivingEntity != null && solLivingEntity.isCurrentlyNPCPet()
						&& solLivingEntity.getOwnerEntity() != null) {
					if (getGroup().getMembers().contains(solLivingEntity.getOwnerEntity().getUniqueId())) {
						return true;
					}
				}
			}

		} catch (CoreStateInitException e) {

		}
		return false;
	}

	@Override
	public int getGodId() {
		return godId;
	}

	@Override
	public void setGodId(int godId) {
		this.godId = godId;
		this.setLastUpdatedTimeNow();

	}

	@Override
	public boolean hasChosenGod() {
		return hasChosenGod;
	}

	@Override
	public void setHasChosenGod(boolean hasChosenGod) {
		this.hasChosenGod = hasChosenGod;
		this.setLastUpdatedTimeNow();

	}

	@Override
	public MemorisedSpells getMemorisedSpellSlots() {
		List<Integer> spells = new ArrayList<Integer>();
		spells.add(memorisedSpellSlot1);
		spells.add(memorisedSpellSlot2);
		spells.add(memorisedSpellSlot3);
		spells.add(memorisedSpellSlot4);
		spells.add(memorisedSpellSlot5);
		spells.add(memorisedSpellSlot6);
		spells.add(memorisedSpellSlot7);
		spells.add(memorisedSpellSlot8);
		String className = "";
		if (this.getClassObj() != null)
			className = this.getClassObj().getName();

		return new MemorisedSpells(spells, className);
	}

	@Override
	public Effects getEffects() {
		Effects effects = new Effects();

		try {
			SoliniaEntitySpells spells = StateManager.getInstance().getEntityManager()
					.getActiveEntitySpells(this.getBukkitPlayer());

			if (spells == null)
				return effects;

			for (SoliniaActiveSpell activeSpell : spells.getActiveSpells()) {
				ISoliniaSpell spell = StateManager.getInstance().getConfigurationManager()
						.getSpell(activeSpell.getSpellId());
				effects.effectSlots.put(activeSpell.getSpellId(), new EffectSlot(activeSpell.getSpellId(),
						spell.getIcon(), spell.getMemicon(), spell.getNewIcon(), spell.getName(), activeSpell.getTicksLeft()));
			}

		} catch (CoreStateInitException e) {

		}
		return effects;
	}

	@Override
	public int getMemorisedSpellSlot1() {
		return memorisedSpellSlot1;
	}

	@Override
	public void setMemorisedSpellSlot1(int memorisedSpellSlot1) {
		this.memorisedSpellSlot1 = memorisedSpellSlot1;
		this.setLastUpdatedTimeNow();

	}

	@Override
	public int getMemorisedSpellSlot2() {
		return memorisedSpellSlot2;
	}

	@Override
	public void setMemorisedSpellSlot2(int memorisedSpellSlot2) {
		this.memorisedSpellSlot2 = memorisedSpellSlot2;
		this.setLastUpdatedTimeNow();

	}

	@Override
	public int getMemorisedSpellSlot3() {
		return memorisedSpellSlot3;
	}

	@Override
	public void setMemorisedSpellSlot3(int memorisedSpellSlot3) {
		this.memorisedSpellSlot3 = memorisedSpellSlot3;
		this.setLastUpdatedTimeNow();

	}

	@Override
	public int getMemorisedSpellSlot4() {
		return memorisedSpellSlot4;
	}

	@Override
	public void setMemorisedSpellSlot4(int memorisedSpellSlot4) {
		this.memorisedSpellSlot4 = memorisedSpellSlot4;
		this.setLastUpdatedTimeNow();

	}

	@Override
	public int getMemorisedSpellSlot5() {
		return memorisedSpellSlot5;
	}

	@Override
	public void setMemorisedSpellSlot5(int memorisedSpellSlot5) {
		this.memorisedSpellSlot5 = memorisedSpellSlot5;
		this.setLastUpdatedTimeNow();

	}

	@Override
	public int getMemorisedSpellSlot6() {
		return memorisedSpellSlot6;
	}

	@Override
	public void setMemorisedSpellSlot6(int memorisedSpellSlot6) {
		this.memorisedSpellSlot6 = memorisedSpellSlot6;
		this.setLastUpdatedTimeNow();

	}

	@Override
	public int getMemorisedSpellSlot7() {
		return memorisedSpellSlot7;
	}

	@Override
	public void setMemorisedSpellSlot7(int memorisedSpellSlot7) {
		this.memorisedSpellSlot7 = memorisedSpellSlot7;
		this.setLastUpdatedTimeNow();

	}

	@Override
	public int getMemorisedSpellSlot8() {
		return memorisedSpellSlot8;
	}

	@Override
	public void setMemorisedSpellSlot8(int memorisedSpellSlot8) {
		this.memorisedSpellSlot8 = memorisedSpellSlot8;
		this.setLastUpdatedTimeNow();

	}

	@Override
	public boolean canUseSpell(ISoliniaSpell spell) {

		if (this.getClassObj() == null)
			return false;

		for (SoliniaSpellClass spellClass : spell.getAllowedClasses()) {
			if (spellClass.getClassname().toUpperCase().equals(this.getClassObj().getName().toUpperCase())
					&& getLevel() >= spellClass.getMinlevel()) {
				return true;
			}
		}

		return false;
	}

	@Override
	public List<Integer> getSpellBookSpellIds() {
		List<Integer> spellBookIds = new ArrayList<Integer>();

		try {
			for (int itemId : getSpellBookItems()) {
				ISoliniaItem spellbookItem = StateManager.getInstance().getConfigurationManager().getItem(itemId);
				if (spellbookItem == null)
					continue;

				if (!spellbookItem.isSpellscroll())
					continue;

				if (spellBookIds.contains(spellbookItem.getAbilityid()))
					continue;

				spellBookIds.add(spellbookItem.getAbilityid());
			}
		} catch (CoreStateInitException e) {
			e.printStackTrace();
		}

		return spellBookIds;
	}

	@Override
	public boolean memoriseSpell(int spellSlot, int spellId) {

		if (spellId > 0) {
			try {
				ISoliniaSpell spell = StateManager.getInstance().getConfigurationManager().getSpell(spellId);
				if (spell == null)
					return false;

				if (!this.canUseSpell(spell))
					return false;
			} catch (CoreStateInitException e) {
				return false;
			}

			if (getMaxSpellSlots() < spellSlot)
				return false;

			if (!getSpellBookSpellIds().contains(spellId))
				return false;
		}

		this.setLastUpdatedTimeNow();

		return this.setMemorisedSpellSlot(spellSlot, spellId);
	}

	@Override
	public int getMaxSpellSlots() {
		// TODO Auto-generated method stub
		return 8;
	}

	@Override
	public int getMemorisedSpellSlot(int spellSlot) throws IllegalArgumentException {
		switch (spellSlot) {
		case 1:
			return this.getMemorisedSpellSlot1();
		case 2:
			return this.getMemorisedSpellSlot2();
		case 3:
			return this.getMemorisedSpellSlot3();
		case 4:
			return this.getMemorisedSpellSlot4();
		case 5:
			return this.getMemorisedSpellSlot5();
		case 6:
			return this.getMemorisedSpellSlot6();
		case 7:
			return this.getMemorisedSpellSlot7();
		case 8:
			return this.getMemorisedSpellSlot8();
		default:
			throw new IllegalArgumentException("Cannot find spell slot of that number");
		}
	}

	public boolean setMemorisedSpellSlot(int spellSlot, int spellId) throws IllegalArgumentException {
		switch (spellSlot) {
		case 1:
			this.setMemorisedSpellSlot1(spellId);
			return true;
		case 2:
			this.setMemorisedSpellSlot2(spellId);
			return true;
		case 3:
			this.setMemorisedSpellSlot3(spellId);
			return true;
		case 4:
			this.setMemorisedSpellSlot4(spellId);
			return true;
		case 5:
			this.setMemorisedSpellSlot5(spellId);
			return true;
		case 6:
			this.setMemorisedSpellSlot6(spellId);
			return true;
		case 7:
			this.setMemorisedSpellSlot7(spellId);
			return true;
		case 8:
			this.setMemorisedSpellSlot8(spellId);
			return true;
		default:
			throw new IllegalArgumentException("Cannot find spell slot of that number");
		}
	}

	@Override
	public boolean isSpellMemoriseSlotFree(int spellSlot) {
		try {
			int value = this.getMemorisedSpellSlot(spellSlot);
			if (value > 0)
				return true;
		} catch (IllegalArgumentException e) {
			return false;
		}

		return false;
	}

	@Override
	public SpellbookPage getSpellbookPage(int pageNo) {
		List<Integer> spellBookPage = new ArrayList<Integer>();

		System.out.println("Found spellbook size of : " + this.getSpellBookItems().size());

		List<List<Integer>> spellBookItemPages = Utils.getPages(this.getSpellBookItems(), 16);
		if (spellBookItemPages.size() >= (pageNo + 1))
			spellBookPage = spellBookItemPages.get(pageNo);

		return new SpellbookPage(pageNo, spellBookPage, this.getClassObj().getName().toUpperCase());
	}

	@Override
	public void sendEffects() {
		try {
			PacketEffects packet = new PacketEffects();
			packet.fromData(getEffects());
			ForgeUtils.QueueSendForgeMessage(((Player) getBukkitPlayer()), Solinia3UIChannelNames.Outgoing,
					Solinia3UIPacketDiscriminators.EFFECTS, packet.toPacketData(), 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void sendMemorisedSpellSlots() {
		try {
			PacketMemorisedSpells spells = new PacketMemorisedSpells();
			spells.fromData(getMemorisedSpellSlots());
			ForgeUtils.QueueSendForgeMessage(((Player) getBukkitPlayer()), Solinia3UIChannelNames.Outgoing,
					Solinia3UIPacketDiscriminators.MEMORISEDSPELLS, spells.toPacketData(), 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public double getCastingProgress() {
		try {
			CastingSpell casting = StateManager.getInstance().getEntityManager().getCasting(this.getBukkitPlayer());

			double progress = 0d;
			if (casting != null && casting.timeLeftMilliseconds > 0 && casting.getSpell() != null) {
				double progressmilliseconds = ((double) casting.getSpell().getCastTime()
						- casting.timeLeftMilliseconds);
				progress = (double) ((double) progressmilliseconds / (double) casting.getSpell().getCastTime());

				if (progress < 0d)
					progress = 0d;

				if (progress > 1d)
					progress = 1d;
			}

			return progress;
		} catch (CoreStateInitException e) {

		}

		return 0D;
	}

	@Override
	public LivingEntity getEntityTarget() {
		if (getBukkitPlayer() != null) {
			try {
				ISoliniaLivingEntity solentity = SoliniaLivingEntityAdapter.Adapt(getBukkitPlayer());
				return solentity.getEntityTarget();
			} catch (CoreStateInitException e) {

			}
		}

		return null;
	}

	@Override
	public void setEntityTarget(LivingEntity target) {
		if (getBukkitPlayer() != null) {
			try {
				ISoliniaLivingEntity solentity = SoliniaLivingEntityAdapter.Adapt(getBukkitPlayer());
				solentity.setEntityTarget(target);
			} catch (CoreStateInitException e) {

			}
		}
		this.setLastUpdatedTimeNow();

	}

	@Override
	public void clearTargetsAgainstMe() {
		if (getBukkitPlayer() != null) {
			try {
				ISoliniaLivingEntity solentity = SoliniaLivingEntityAdapter.Adapt(getBukkitPlayer());
				solentity.clearTargetsAgainstMe();
			} catch (CoreStateInitException e) {
			}
		}
		this.setLastUpdatedTimeNow();

	}

	@Override
	public EquipSlots getEquipSlots() {
		EquipSlots equipSlots = new EquipSlots();

		try {
			if (getArmsItem() > 0) {
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(getArmsItem());
				if (item != null)
					equipSlots.ArmsItemBase64 = TextUtils.ToBase64UTF8(
							ItemStackUtils.ConvertItemStackToJsonRegular(item.asItemStack()));
			}

			if (getEarsItem() > 0) {
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(getEarsItem());
				if (item != null)
					equipSlots.EarsItemBase64 = TextUtils.ToBase64UTF8(
							ItemStackUtils.ConvertItemStackToJsonRegular(item.asItemStack()));
			}

			if (getFingersItem() > 0) {
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(getFingersItem());
				if (item != null)
					equipSlots.FingersItemBase64 = TextUtils.ToBase64UTF8(
							ItemStackUtils.ConvertItemStackToJsonRegular(item.asItemStack()));
			}

			if (getForearmsItem() > 0) {
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(getForearmsItem());
				if (item != null)
					equipSlots.ForearmsItemBase64 = TextUtils.ToBase64UTF8(
							ItemStackUtils.ConvertItemStackToJsonRegular(item.asItemStack()));
			}

			if (getHandsItem() > 0) {
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(getHandsItem());
				if (item != null)
					equipSlots.HandsItemBase64 = TextUtils.ToBase64UTF8(
							ItemStackUtils.ConvertItemStackToJsonRegular(item.asItemStack()));
			}

			if (getNeckItem() > 0) {
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(getNeckItem());
				if (item != null)
					equipSlots.NeckItemBase64 = TextUtils.ToBase64UTF8(
							ItemStackUtils.ConvertItemStackToJsonRegular(item.asItemStack()));
			}

			if (getShouldersItem() > 0) {
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(getShouldersItem());
				if (item != null)
					equipSlots.ShouldersItemBase64 = TextUtils.ToBase64UTF8(
							ItemStackUtils.ConvertItemStackToJsonRegular(item.asItemStack()));
			}

			if (getWaistItem() > 0) {
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(getWaistItem());
				if (item != null)
					equipSlots.WaistItemBase64 = TextUtils.ToBase64UTF8(
							ItemStackUtils.ConvertItemStackToJsonRegular(item.asItemStack()));
			}
		} catch (CoreStateInitException e) {

		}

		return equipSlots;
	}

	@Override
	public void setForenameAndLastName(String forename, String lastname) {
		this.forename = forename;
		this.lastname = lastname;
		this.setLastUpdatedTimeNow();

	}

	@Override
	public boolean isExperienceOn() {
		return experienceOn;
	}

	@Override
	public void setExperienceOn(boolean experienceOn) {
		this.experienceOn = experienceOn;
		this.setLastUpdatedTimeNow();

	}

	@Override
	public void unMemoriseSpell(int abilityid) {
		// Remove from memorised spells
		boolean foundSpell = false;
		if (getMemorisedSpellSlots().getAllSpellIds().contains(abilityid)) {
			for (int i = 1; i <= 8; i++) {
				if (getMemorisedSpellSlots().getSlotId(i) != abilityid)
					continue;

				memoriseSpell(i, 0);
				foundSpell = true;
				getBukkitPlayer()
						.sendMessage(ChatColor.GRAY + "Debug: Unmemorised spell: " + abilityid + ChatColor.RESET);
			}
		}

		if (foundSpell)
			this.sendMemorisedSpellSlots();
		
		this.setLastUpdatedTimeNow();

	}

	@Override
	public boolean isForceNewAlt() {
		return forceNewAlt;
	}

	@Override
	public void setForceNewAlt(boolean forceNewAlt) {
		this.forceNewAlt = forceNewAlt;
		this.setLastUpdatedTimeNow();

	}

	@Override
	public boolean isPlayable() {
		try {
			if (this.isDeleted())
				return false;
			
			if (getLevel() > StateManager.getInstance().getConfigurationManager().getMaxLevel())
				return false;

			if (getLevel() < 50)
				if (hasAaRanks())
					return false;

		} catch (CoreStateInitException e) {
			return false;
		}

		return true;
	}

	@Override
	public boolean isDeleted() {
		return deleted;
	}

	@Override
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
		this.setLastUpdatedTimeNow();

	}

	@Override
	public List<TrackingChoice> getTrackingChoices() {
		List<TrackingChoice> tracking_list = new ArrayList<TrackingChoice>();
		int distance = 0;

		if (this.getClassObj() == null)
			return new ArrayList<TrackingChoice>();
		
		if (this.getClassObj().getName().equals("DRUID"))
			distance = ((getSkill(SkillType.Tracking).getValue() + 1) * 10);
		else if (this.getClassObj().getName().equals("RANGER"))
			distance = ((getSkill(SkillType.Tracking).getValue() + 1) * 12);
		else if (this.getClassObj().getName().equals("BARD"))
			distance = ((getSkill(SkillType.Tracking).getValue() + 1) * 7);
		if (distance <= 0)
			return new ArrayList<TrackingChoice>();
		
		if (distance < 300)
			distance = 300;
		
		// NPCs first
		for(MythicSpawner msl : MythicMobs.inst().getSpawnerManager().getSpawnersByString(BukkitAdapter.adapt(this.getBukkitPlayer().getLocation()),"r:"+distance))
		{
			if (msl.isOnCooldown() || msl.isOnWarmup())
				continue;
			
			if (!msl.getTypeName().contains("NPCID_"))
				continue;

			int npcId = Integer.parseInt(msl.getTypeName().substring(6));
			try {
				ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(npcId);

				if (npc == null)
					continue;

				TrackingChoice trackingChoice = new TrackingChoice();
				trackingChoice.Name = npc.getName();
				// cast to int as its never more than skill limit of tracking
				trackingChoice.Distance = (int) msl.getLocation().distance(BukkitAdapter.adapt(this.getBukkitPlayer().getLocation()));
				trackingChoice.Color = Utils.getLevelCon(this.getLevel(), npc.getLevel()).name();
				trackingChoice.Id = msl.getName();
				
				tracking_list.add(trackingChoice);
				
			} catch (CoreStateInitException e) {
				e.printStackTrace();
			}
		}

		// SORT IT
		Collections.sort(tracking_list, (e1, e2) -> ((Integer)((TrackingChoice)e1).Distance).compareTo((Integer)((TrackingChoice)e2).Distance));
		return tracking_list;
	}

	@Override
	public boolean isTracking() {
		return isTrackingLocation();
	}

	@Override
	public void resetReverseAggro() {
		try {
			ISoliniaLivingEntity solentity = SoliniaLivingEntityAdapter.Adapt(this.getBukkitPlayer());
			solentity.resetReverseAggro();
		} catch (CoreStateInitException e) {
			// do nothing
		}	
		this.setLastUpdatedTimeNow();

	}

	@Override
	public void fellowshipchat(String message) {
		if (getFellowship() == null)
		{
			this.getBukkitPlayer().sendMessage("Fellowship does not exist");
			return;
		}
		
		if (getFellowship().getMemberCharacterIds().size() < 2)
		{
			this.getBukkitPlayer().sendMessage("There is only 1 person in the fellowship");
			return;
		}
		
		if (getFellowship().getMembersOnline().size() < 2)
		{
			this.getBukkitPlayer().sendMessage("There is only 1 person in the fellowship online");
			return;
		}
		
		getFellowship().sendMessage(this,ChatColor.AQUA + message + ChatColor.RESET);
	}

	@Override
	public int getCharacterFellowshipId() {
		return characterFellowshipId;
	}

	@Override
	public void setCharacterFellowshipId(int characterFellowshipId) {
		this.characterFellowshipId = characterFellowshipId;
		this.setLastUpdatedTimeNow();

	}

	@Override
	public void grantFellowshipXPBonusToFellowship(Double experience) {
		if (getFellowship() == null)
			return;
			
		try
		{
			List<Integer> levelRanges = new ArrayList<Integer>();
			for(int memberId : this.getFellowship().getMemberCharacterIds())
			{
				ISoliniaPlayer memberPlayer = StateManager.getInstance().getConfigurationManager().getCharacterById(memberId);
				if (memberPlayer == null)
					continue;
				levelRanges.add(memberPlayer.getLevel());
			}
			
			Tuple<Integer,Integer> lowhighlvl = Utils.GetGroupExpMinAndMaxLevel(levelRanges);
			int ilowlvl = lowhighlvl.a();
			int ihighlvl = lowhighlvl.b();
			
			if (getLevel() < ilowlvl || getLevel() > ihighlvl) {
				// Only award player the experience
				// as they are out of range of the group
				return;
			}
			
			getFellowship().grantFellowshipXPBonus(experience,ilowlvl,ihighlvl);
			if (getBukkitPlayer() != null)
				getBukkitPlayer().sendMessage(ChatColor.GRAY + "* Some of your XP has been granted to your fellowship members (see /claimxp)");
		} catch (CoreStateInitException e)
		{
			
		}
	}

	@Override
	public SkillType getLanguageSkillType() {
		return languageSkillType;
	}

	@Override
	public void setLanguageSkillType(SkillType languageSkillType) {
		this.languageSkillType = languageSkillType;
		this.setLastUpdatedTimeNow();

	}

	@Override
	public HintSetting getHintSetting(HINT hint) {
		if (hint == null)
			return HintSetting.Off;
		
		if (this.hintSettings.get(hint.name()) == null)
			this.hintSettings.put(hint.name(), Utils.getDefaultHintLocation(hint));
		
		return this.hintSettings.get(hint.name());
	}
	
	@Override
	public ChatMessageType getHintSettingAsChatMessageType(HINT hint)
	{
		switch (getHintSetting(hint))
		{
			case Off:
				return null;
			case ActionBar:
				return ChatMessageType.ACTION_BAR;
			case Chat:
				return ChatMessageType.CHAT;
			default:
				return null;
		}
	}

	@Override
	public void setHintSetting(HINT hint, HintSetting newType) {
		if (hint == null)
			return;
		
		if (newType == null)
			newType = HintSetting.Off;
		else
			this.hintSettings.put(hint.name(), newType);
		
		this.setLastUpdatedTimeNow();

	}

	@Override
	public void resetHintSetting() {
		this.hintSettings.clear();
		this.setLastUpdatedTimeNow();

	}

	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
		this.setLastUpdatedTimeNow();

	}
	
	@Override
	public UUID getPrimaryUUID() {
		// TODO Auto-generated method stub
		return this.primaryUUID;
	}
	@Override
	public void setPrimaryUUID(UUID uuid) {
		// TODO Auto-generated method stub
		this.primaryUUID = uuid;
		this.setLastUpdatedTimeNow();

	}
	@Override
	public UUID getSecondaryUUID() {
		// TODO Auto-generated method stub
		return this.secondaryUUID;
	}
	@Override
	public void setSecondaryUUID(UUID uuid) {
		// TODO Auto-generated method stub
		this.secondaryUUID = uuid;
		this.setLastUpdatedTimeNow();

	}

	@Override
	public void setOwnerUUID(UUID uniqueId) {
		this.setSecondaryUUID(uniqueId);
		this.setLastUpdatedTimeNow();

	}
	
	@Override
	public UUID getOwnerUUID()
	{
		return this.getSecondaryUUID();
	}

	@Override
	public boolean hasQuestFlag(String questFlag) {
		for (String playerQuestFlag : getPlayerQuestFlags()) {
			if (playerQuestFlag.toUpperCase().equals(questFlag.toUpperCase())) 
				return true;
		}

		return false;
	}

	@Override
	public void destroySpellbookSpellId(Integer spellId) {
		List<Integer> spellBookItemIdsToRemove = new ArrayList<Integer>();

		try {
			for (int itemId : getSpellBookItems()) {
				ISoliniaItem spellbookItem = StateManager.getInstance().getConfigurationManager().getItem(itemId);
				if (spellbookItem == null)
				{
					System.out.println("Found an item in someones spell book that doesnt exist");
					spellBookItemIdsToRemove.add(itemId);
					continue;
				}

				if (!spellbookItem.isSpellscroll())
				{
					System.out.println("Found an item in someones spell book that wasnt a spell");
					spellBookItemIdsToRemove.add(itemId);
					continue;
				}

				if (spellbookItem.getAbilityid() != spellId)
					continue;

				spellBookItemIdsToRemove.add(spellbookItem.getId());
			}
			
			for(Integer itemId : spellBookItemIdsToRemove)
			{
				if (!this.spellBookItems.contains(itemId))
					continue;
					
				this.spellBookItems.remove(itemId);
				System.out.println("Destroyed spell from players spellbook");
			}
		} catch (CoreStateInitException e) {
			e.printStackTrace();
		}
		this.setLastUpdatedTimeNow();

	}

	@Override
	public String getBase64BankContents() {
		return base64BankContents;
	}

	@Override
	public void setBase64BankContents(String base64BankContents) {
		this.base64BankContents = base64BankContents;
		this.setLastUpdatedTimeNow();

	}

	@Override
	public void openBank() {
		Inventory inventory = Bukkit.createInventory(new SoliniaBankHolder(), 45, "Solinia International Bank");
		inventory.setContents(getStoredBankContents());
		this.getBukkitPlayer().openInventory(inventory);
	}

	@Override
	public void openCraft() {
		Inventory inventory = Bukkit.createInventory(new SoliniaCraftHolder(), 9, "Craft");
		ItemStack[] defaultItems = new ItemStack[9];
		defaultItems[2] = new ItemStack(Material.BARRIER);
		defaultItems[3] = new ItemStack(Material.BARRIER);
		defaultItems[4] = new ItemStack(Material.BARRIER);
		defaultItems[5] = new ItemStack(Material.BARRIER);
		defaultItems[6] = new ItemStack(Material.BARRIER);
		defaultItems[7] = new ItemStack(Material.BARRIER);
		defaultItems[8] = new ItemStack(Material.CHEST);
		inventory.setContents(defaultItems);
		this.getBukkitPlayer().openInventory(inventory);
	}
	
	@Override
	public Timestamp getLastUpdatedTime() {
		if (lastUpdatedTime == null)
			setLastUpdatedTimeNow();
		
		return lastUpdatedTime;
	}

	@Override
	public void setLastUpdatedTime(Timestamp lastUpdatedTime) {
		this.lastUpdatedTime = lastUpdatedTime;
	}
	
	@Override
	public void setLastUpdatedTimeNow() {
		LocalDateTime datetime = LocalDateTime.now();
		Timestamp nowtimestamp = Timestamp.valueOf(datetime);
		//System.out.println("Set LastUpdatedTime on " + getId());
		this.setLastUpdatedTime(nowtimestamp);
	}

	@Override
	public void grantExperienceAndLoot(ISoliniaLivingEntity livingEntity) {
		try {
			Double experience = PlayerUtils.getExperienceRewardAverageForLevel(livingEntity.getEffectiveLevel());

			// try to share with group
			ISoliniaGroup group = StateManager.getInstance().getGroupByMember(getOwnerUUID());
			if (group != null) {

				List<Integer> levelRanges = new ArrayList<Integer>();
				for (UUID member : group.getMembers()) {
					ISoliniaPlayer playerchecked = SoliniaPlayerAdapter.Adapt(Bukkit.getPlayer(member));
					int checkedlevel = playerchecked.getLevel();
					levelRanges.add(checkedlevel);
				}
				
				Tuple<Integer,Integer> lowhighlvl = Utils.GetGroupExpMinAndMaxLevel(levelRanges);
				int ilowlvl = lowhighlvl.a();
				int ihighlvl = lowhighlvl.b();

				if (getLevel() < ilowlvl || getLevel() > ihighlvl) {
					// Only award player the experience
					// as they are out of range of the group
					if (livingEntity.getEffectiveLevel() >= Utils.getMinLevelFromLevel(getLevel())) {
						// Due to being out of range they get the full xp
						increasePlayerExperience(experience, true, true);
						if (getFellowship() != null)
							grantFellowshipXPBonusToFellowship(experience);

						// Grant title for killing mob
						if (livingEntity.getNpcid() > 0) {
							ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager()
									.getNPC(livingEntity.getNpcid());
							if (npc != null && !npc.getDeathGrantsTitle().equals("")) {
								grantTitle(npc.getDeathGrantsTitle());
							}

							if (npc.isBoss() || npc.isRaidboss()) {
								grantTitle("the Vanquisher");
							}
						}

					} 

				} else {
					double experienceReward = experience / group.getMembers().size();
					double groupBonus = (experienceReward/100)*(group.getMembers().size()*10);
					
					List<Integer> awardsFellowshipIds = new ArrayList<Integer>();
					
					for (UUID member : group.getMembers()) {
						Player tgtplayer = Bukkit.getPlayer(member);
						if (tgtplayer != null) {
							ISoliniaPlayer tgtsolplayer = SoliniaPlayerAdapter.Adapt(tgtplayer);
							int tgtlevel = tgtsolplayer.getLevel();

							if (tgtlevel < ilowlvl || tgtlevel > ihighlvl) {
								tgtplayer.sendMessage(ChatColor.GRAY
										+ "You were out of level range to gain experience in this group (Min: "
										+ ilowlvl + " Max: " + ihighlvl + ")");
								continue;
							}

							if (!tgtplayer.getWorld().equals(getBukkitPlayer().getWorld())) {
								// tgtplayer.sendMessage("You were out of range for shared group xp (world)");
								continue;
							}

							if (tgtplayer.getLocation().distance(getBukkitPlayer().getLocation()) <= Utils.MaxRangeForExperience) {
								if (livingEntity.getEffectiveLevel() >= (Utils.getMinLevelFromLevel(tgtsolplayer.getLevel()))) {
									// they split the overall experience across the group size
									
									// add 10% bonus per player
									
									tgtsolplayer.increasePlayerExperience(experienceReward+groupBonus, true, true);
									
									if (getFellowship() != null)
									if (!awardsFellowshipIds.contains(getFellowship().getId()))
									{
										awardsFellowshipIds.add(getFellowship().getId());
										grantFellowshipXPBonusToFellowship(experience);
									}

									// Grant title for killing mob
									if (livingEntity.getNpcid() > 0) {
										ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager()
												.getNPC(livingEntity.getNpcid());
										if (npc != null && !npc.getDeathGrantsTitle().equals("")) {
											tgtsolplayer.grantTitle(npc.getDeathGrantsTitle());
										}

										if (npc.isBoss() || npc.isRaidboss()) {
											tgtsolplayer.grantTitle("the Vanquisher");
										}
									}

								} else {
									// The npc was too low level to gain
									// experience from - Was: " + livingEntity.getLevel() + " Min: " +
									// Utils.getMinLevelFromLevel(tgtsolplayer.getLevel()));
								}

							} else {
								// tgtplayer.sendMessage(ChatColor.GRAY + "You were out of range for shared
								// group xp (distance)");
								continue;
							}
						}
					}
				}
			} else {
				if (livingEntity.getEffectiveLevel() >= (Utils.getMinLevelFromLevel(getLevel()))) {
					// they are on their own so get the full amount of xp
					increasePlayerExperience(experience, true, true);
					
					if (getFellowship() != null)
					grantFellowshipXPBonusToFellowship(experience);

					// Grant title for killing mob
					if (livingEntity.getNpcid() > 0) {
						ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager()
								.getNPC(livingEntity.getNpcid());
						if (npc != null && !npc.getDeathGrantsTitle().equals("")) {
							grantTitle(npc.getDeathGrantsTitle());
						}

						if (npc.isBoss() || npc.isRaidboss()) {
							grantTitle("the Vanquisher");
						}
					}

				} else {
					// player.getBukkitPlayer().sendMessage(ChatColor.GRAY + "* The npc was too low
					// level to gain experience from - Was: " + livingEntity.getLevel() + " Min: " +
					// Utils.getMinLevelFromLevel(player.getLevel()));
				}
			}

			if (livingEntity.getNpcid() > 0) {
				ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(livingEntity.getNpcid());
				if (npc.getFactionid() > 0) {
					ISoliniaFaction faction = StateManager.getInstance().getConfigurationManager()
							.getFaction(npc.getFactionid());
					decreaseFactionStanding(npc.getFactionid(), 50);
					for (FactionStandingEntry factionEntry : faction.getFactionEntries()) {
						if (factionEntry.getValue() >= 1500) {
							// If this is an ally of the faction, grant negative faction
							decreaseFactionStanding(factionEntry.getFactionId(), 10);
						}

						if (factionEntry.getValue() <= -1500) {
							// If this is an enemy of the faction, grant positive faction
							increaseFactionStanding(factionEntry.getFactionId(), 1);
						}
					}
				}
			}

			if (livingEntity.getNpcid() > 0) {
				ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(livingEntity.getNpcid());

				if (npc != null) {
					if (npc.getChanceToRespawnOnDeath() > 0)
						if (Utils.RandomBetween(1, 100) <= npc.getChanceToRespawnOnDeath()) {
							npc.Spawn(getBukkitPlayer().getLocation(), 1);
						}

					if (!npc.getDeathGrantsTitle().equals("")) {
						grantTitle(npc.getDeathGrantsTitle());
					}

					if (npc.isBoss() || npc.isRaidboss()) {
						grantTitle("the Vanquisher");
					}

					// Dern wants this back
					 if (
							 //npc.isBoss() || 
							 npc.isRaidboss()) { PlayerUtils.BroadcastPlayers("[VICTORY] The foundations of the earth shake following the destruction of " + npc.getName() + " at the hands of " + getFullNameWithTitle() + "!");
					 }
					 
				}
			}

			giveMoney(1);
			livingEntity.dropLoot();
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
