package com.solinia.solinia.Interfaces;

import java.io.Serializable;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;

public interface ISoliniaPlayer extends Serializable {
	public UUID getUUID();

	void setUUID(UUID uuid);

	public String getForename();

	void setForename(String forename);

	public String getLastname();

	void setLastname(String lastname);

	public void updateDisplayName();
	
	public void updateMaxHp();

	public ISoliniaEntity getEntity() throws CoreStateInitException;

	public String getFullName();

	public int getMana();

	void setMana(int mana);

	public Double getAAExperience();

	void setAAExperience(Double aaexperience);

	public Double getExperience();

	void setExperience(Double experience);

	public int getLevel();

	public int getRaceId();

	public boolean hasChosenRace();

	void setChosenRace(boolean chosen);

	void setRaceId(int raceid);

	public ISoliniaRace getRace();

	public boolean hasChosenClass();

	public int getClassId();

	public ISoliniaClass getClassObj();
	
	void setClassId(int classid);

	void setChosenClass(boolean haschosenclass);

	Player getBukkitPlayer();

	int getStrength();

	int getStamina();
	
	int getAgility();
	
	int getDexterity();
	
	int getIntelligence();
	
	int getWisdom();
	
	int getCharisma();

	void increasePlayerExperience(Double experience);

	void increasePlayerNormalExperience(Double experience);
	
	void increasePlayerAAExperience(Double experience);

	public void giveMoney(int i);

	int getAAPoints();

	void setAAPoints(int aapoints);
}
