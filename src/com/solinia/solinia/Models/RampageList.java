package com.solinia.solinia.Models;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RampageList {
	public ConcurrentHashMap<Integer,UUID> mobList = new ConcurrentHashMap<Integer,UUID>();
	public RampageList(List<UUID> mobList)
	{
		for(UUID uuid : mobList)
			addToList(uuid);
	}

	public void addToList(UUID provoker) {
		if (mobList.values().contains(provoker))
			return;
		
		this.mobList.put(mobList.size()+1, provoker);
	}
	
	public void removeFromList(UUID provoker) {
		if (!mobList.values().contains(provoker))
			return;
		
		int removeId = 0;
		for(Integer val : mobList.keySet())
		{
			if (mobList.get(val) != provoker)
				continue;
			
			removeId = val;
			break;
		}
		
		if (removeId > 0)
			this.mobList.remove(removeId);
	}

	public void clearRampageList() {
		this.mobList.clear();
	}
}
