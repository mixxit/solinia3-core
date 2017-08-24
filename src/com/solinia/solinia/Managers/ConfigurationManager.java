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
		List<ISoliniaRace> races = raceRepository.query(q ->q.getId() == raceId);
		if (races.size() > 0)
			return races.get(0);
			
		return null;
	}

	@Override
	public ISoliniaRace getRace(String race) {
		
		List<ISoliniaRace> races = raceRepository.query(q ->q.getName().equals(race));
		if (races.size() > 0)
			return races.get(0);
			
		return null;
	}

	@Override
	public void commit() {
		this.raceRepository.commit();
	}

	@Override
	public void addRace(ISoliniaRace race) {
		this.raceRepository.add(race);
		
	}

	@Override
	public int getNextRaceId() {
		int maxRace = 0;
		for(ISoliniaRace race : getRaces())
		{
			if (race.getId() > maxRace)
				maxRace = race.getId();
		}
		
		return maxRace + 1;
	}
}
