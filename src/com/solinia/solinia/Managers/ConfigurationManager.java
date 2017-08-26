package com.solinia.solinia.Managers;

import java.util.ArrayList;
import java.util.List;

import com.solinia.solinia.Interfaces.IConfigurationManager;
import com.solinia.solinia.Interfaces.IRepository;
import com.solinia.solinia.Interfaces.ISoliniaClass;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaRace;

public class ConfigurationManager implements IConfigurationManager {

	private IRepository<ISoliniaRace> raceRepository;
	private IRepository<ISoliniaClass> classRepository;
	private IRepository<ISoliniaItem> itemRepository;
	
	public ConfigurationManager(IRepository<ISoliniaRace> raceContext, IRepository<ISoliniaClass> classContext, IRepository<ISoliniaItem> itemContext)
	{
		this.raceRepository = raceContext;
		this.classRepository = classContext;
		this.itemRepository = itemContext;
	}
	
	@Override
	public List<ISoliniaRace> getRaces() {
		// TODO Auto-generated method stub
		return raceRepository.query(q ->q.getName() != null);
	}
	
	@Override
	public List<ISoliniaClass> getClasses() {
		// TODO Auto-generated method stub
		return classRepository.query(q ->q.getName() != null);
	}
	
	@Override
	public List<ISoliniaItem> getItems() {
		// TODO Auto-generated method stub
		return itemRepository.query(q ->q.getId() > 0);
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
	public ISoliniaClass getClassObj(int classId) {
		// TODO Auto-generated method stub
		List<ISoliniaClass> classes = classRepository.query(q ->q.getId() == classId);
		if (classes.size() > 0)
			return classes.get(0);
			
		return null;
	}

	@Override
	public ISoliniaRace getRace(String race) {
		
		List<ISoliniaRace> races = raceRepository.query(q ->q.getName().toUpperCase().equals(race.toUpperCase()));
		if (races.size() > 0)
			return races.get(0);
			
		return null;
	}

	@Override
	public ISoliniaItem getItem(int Id) {
		
		List<ISoliniaItem> items = itemRepository.query(q ->q.getId() == Id);
		if (items.size() > 0)
			return items.get(0);
			
		return null;
	}
	
	@Override
	public ISoliniaClass getClassObj(String classname) {
		
		List<ISoliniaClass> classes = classRepository.query(q ->q.getName().toUpperCase().equals(classname.toUpperCase()));
		if (classes.size() > 0)
			return classes.get(0);
			
		return null;
	}
	
	@Override
	public void commit() {
		this.raceRepository.commit();
		this.classRepository.commit();
		this.itemRepository.commit();
	}

	@Override
	public void addRace(ISoliniaRace race) {
		this.raceRepository.add(race);
		
	}
	
	@Override
	public void addClass(ISoliniaClass classobj) {
		this.classRepository.add(classobj);
		
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
	
	@Override
	public int getNextClassId() {
		int maxClass = 0;
		for(ISoliniaClass classInstance : getClasses())
		{
			if (classInstance.getId() > maxClass)
				maxClass = classInstance.getId();
		}
		
		return maxClass + 1;
	}
	
	@Override
	public int getNextItemId() {
		int maxItem = 0;
		for(ISoliniaItem itemInstance : getItems())
		{
			if (itemInstance.getId() > maxItem)
				maxItem = itemInstance.getId();
		}
		
		return maxItem + 1;
	}

	@Override
	public boolean isValidRaceClass(int raceId, int classId) {
		ISoliniaClass classes = getClassObj(classId);
		if (classes == null)
			return false;
		
		if (classes.getValidRaces() == null)
			return false;
		
		if (classes.getValidRaces().contains(raceId))
			return true;

		return false;
	}

	@Override
	public void addRaceClass(int raceId, int classId) {
		if (getClassObj(classId) == null)
			return;
		
		if (getRace(raceId) == null)
			return;
		
		List<Integer> validRaces = getClassObj(classId).getValidRaces();
		if (validRaces == null)
			validRaces = new ArrayList<Integer>();
		
		if (validRaces.contains(raceId))
			return;
		
		validRaces.add(raceId);
		getClassObj(classId).setValidRaces(validRaces);
		
	}

	@Override
	public void addItem(ISoliniaItem item) {
		this.itemRepository.add(item);		
	}
}
