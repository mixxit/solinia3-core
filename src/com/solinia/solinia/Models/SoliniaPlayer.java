package com.solinia.solinia.Models;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaEntityAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaClass;
import com.solinia.solinia.Interfaces.ISoliniaEntity;
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
	private int raceid = 0;
	private boolean haschosenrace = false;
	private boolean haschosenclass = false;
	private int classid = 0;

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
	public ISoliniaEntity getEntity() throws CoreStateInitException
	{
		return SoliniaEntityAdapter.Adapt(Bukkit.getPlayer(uuid));
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
		return Utils.GetLevelFromExperience(this.experience);		
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
			double calculatedhp = Utils.GetStatMaxHP(this);
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
}
