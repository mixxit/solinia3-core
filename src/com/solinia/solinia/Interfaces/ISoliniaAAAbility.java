package com.solinia.solinia.Interfaces;

import java.util.List;

public interface ISoliniaAAAbility {

	String getSysname();

	void setSysname(String sysname);

	String getName();

	void setName(String name);

	int getId();

	void setId(int id);

	List<String> getClasses();

	void setClasses(List<String> classes);

	List<ISoliniaAARank> getRanks();

	void setRanks(List<ISoliniaAARank> ranks);

	boolean canClassUseAbility(ISoliniaClass iSoliniaClass);

}
