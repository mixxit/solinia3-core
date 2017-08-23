package com.solinia.solinia.Managers;

import java.util.List;

import com.solinia.solinia.Interfaces.IConfigurationManager;
import com.solinia.solinia.Interfaces.IRepository;
import com.solinia.solinia.Interfaces.ISoliniaRace;

public class ConfigurationManager implements IConfigurationManager {

	IRepository<ISoliniaRace> raceRepository;
	
	public ConfigurationManager(IRepository<ISoliniaRace> raceContext)
	{
		this.raceRepository = raceContext;
	}
	
	@Override
	public List<ISoliniaRace> getRaces() {
		// TODO Auto-generated method stub
		return raceRepository.query(q ->q.getName() != null);
	}

	@Override
	public ISoliniaRace getRace(int raceId) {
		// TODO Auto-generated method stub
		return raceRepository.query(q ->q.getId() == raceId).get(0);
	}

	@Override
	public ISoliniaRace getRace(String race) {
		// TODO Auto-generated method stub
		return raceRepository.query(q ->q.getName().equals(race)).get(0);
	}

	@Override
	public void commit() {
		this.raceRepository.commit();
	}
}
