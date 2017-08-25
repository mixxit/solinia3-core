package com.solinia.solinia.Models;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaClass;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Interfaces.ISoliniaRace;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Utils.Utils;

public class SoliniaPlayer implements ISoliniaPlayer {

	private static final long serialVersionUID = 9075039437399478391L;
	private UUID uuid;
	private String forename = "";
	private String lastname = "";
	private int mana = 0;
	private Double experience = 0d;
	private Double aaexperience = 0d;
	private int aapoints = 0;
	private int raceid = 0;
	private boolean haschosenrace = false;
	private boolean haschosenclass = false;
	private int classid = 0;
	private String language = "UNKNOWN";
	private String gender = "MALE";
	private List<SoliniaPlayerSkill> skills = new ArrayList<SoliniaPlayerSkill>();

	@Override
	public UUID getUUID() {
		return uuid;
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
		updateDisplayName();
	}

	@Override
	public String getLastname() {
		return lastname;
	}

	@Override
	public void setLastname(String lastname) {
		this.lastname = lastname;
		updateDisplayName();
	}
	
	@Override
	public void updateDisplayName()
	{
		if (getBukkitPlayer() != null)
		{
			getBukkitPlayer().setDisplayName(getFullName());
			getBukkitPlayer().setPlayerListName(getFullName());
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
	public int getMana() {
		// TODO Auto-generated method stub
		return this.mana;
	}
	
	@Override
	public void setMana(int mana)
	{
		this.mana = mana;
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
			double calculatedhp = Utils.getStatMaxHP(this);
			getBukkitPlayer().setMaxHealth(calculatedhp);
			getBukkitPlayer().setHealthScaled(true);
			getBukkitPlayer().setHealthScale(40D);
		}
	}
	
	@Override
	public int getStrength() {
		int stat = 1;
		
		if (getRace() != null)
			stat += getRace().getStrength();

		return stat;
	}

	@Override
	public int getStamina() {
		int stat = 1;
		
		if (getRace() != null)
			stat += getRace().getStamina();
		return stat;
	}

	@Override
	public int getAgility() {
		int stat = 1;
		
		if (getRace() != null)
			stat += getRace().getAgility();
		return stat;
	}

	@Override
	public int getDexterity() {
		int stat = 1;
		
		if (getRace() != null)
			stat += getRace().getDexterity();
		return stat;
	}

	@Override
	public int getIntelligence() {
		int stat = 1;
		
		if (getRace() != null)
			stat += getRace().getIntelligence();
		return stat;
	}

	@Override
	public int getWisdom() {
		int stat = 1;
		
		if (getRace() != null)
			stat += getRace().getWisdom();
		return stat;
	}
	
	@Override
	public int getCharisma() {
		int stat = 1;
		
		if (getRace() != null)
			stat += getRace().getCharisma();
		return stat;
	}
	
	@Override
	public void increasePlayerExperience(Double experience) {
		if (!isAAOn()) {
			increasePlayerNormalExperience(experience);
		} else {
			increasePlayerAAExperience(experience);
		}
	}

	private boolean isAAOn() {
		// TODO Replace with AA toggle
		return false;
	}

	@Override
	public void increasePlayerNormalExperience(Double experience) {
		System.out.println("Determing player normal xp from experience value: " + experience);
		double classxpmodifier = Utils.getClassXPModifier(getClassObj());
		experience = experience * (classxpmodifier / 100);

		boolean modified = false;
		double modifier = StateManager.getInstance().getWorldPerkXPModifier();
		if (modifier > 100) {
			System.out.println("This is modified experience from a world perk");
			modified = true;
		}
		experience = experience * (modifier / 100);

		Double currentexperience = getExperience();
		System.out.println("Normal xp would be " + currentexperience);

		// make sure someone never gets more than a level per kill
		double clevel = Utils.getLevelFromExperience(currentexperience);
		double nlevel = Utils.getLevelFromExperience((currentexperience + experience));

		if (nlevel > (clevel + 1)) {
			// xp is way too high, drop to proper amount
			System.out.println("XP was dropped for being way too high");

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
			currentexperience = Utils.getExperienceRequirementForLevel(Utils.getMaxLevel());
			System.out.println(
					"XP was higher than than that needed to level, getting minimum for level: " + currentexperience);

		} else {
			currentexperience = currentexperience + experience;
			System.out.println(
					"XP was not higher than level needed so granting normal increase of: " + currentexperience);
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
			getBukkitPlayer().sendMessage(ChatColor.DARK_PURPLE + "* You gained a level (" + newlevel + ")!");
			getBukkitPlayer().getWorld().playEffect(getBukkitPlayer().getLocation(), Effect.FIREWORK_SHOOT, 1);

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
			StateManager.getInstance().giveEssentialsMoney(getBukkitPlayer(),i);
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
	public void say(String string) {
		StateManager.getInstance().getChannelManager().sendToLocalChannel(this,string);
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
}
