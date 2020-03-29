package com.solinia.solinia.Interfaces;

import java.util.List;

public interface ISoliniaPatch extends IPersistable {

	int getId();

	void setId(int id);

	List<String> getClasses();

	void setClasses(List<String> classes);

}
