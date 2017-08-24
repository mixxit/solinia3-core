package com.solinia.solinia.Interfaces;

import java.io.Serializable;
import java.util.UUID;

import com.solinia.solinia.Exceptions.CoreStateInitException;

public interface ISoliniaPlayer extends Serializable {
	public UUID getUUID();

	void setUUID(UUID uuid);

	public String getForename();

	void setForename(String forename);

	public String getLastname();

	void setLastname(String lastname);

	public void updateDisplayName();

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
}
