package com.solinia.solinia.Interfaces;

import java.util.List;

public interface IConfigurationManager {

	List<ISoliniaRace> getRaces();

	ISoliniaRace getRace(int raceId);

	ISoliniaRace getRace(String race);

	void commit();
}
