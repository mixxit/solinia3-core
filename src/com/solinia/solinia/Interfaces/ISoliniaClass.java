package com.solinia.solinia.Interfaces;

import java.util.List;

public interface ISoliniaClass {

	public String getName();

	public int getId();

	void setId(int id);

	void setName(String name);

	void setAdmin(boolean adminonly);

	boolean isAdmin();

	public String getDescription();

	void setDescription(String description);

	void setValidRaces(List<Integer> validRaces);

	List<Integer> getValidRaces();

}
