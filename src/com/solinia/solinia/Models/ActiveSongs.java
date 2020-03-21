package com.solinia.solinia.Models;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

public class ActiveSongs {
	ConcurrentHashMap<Integer,Timestamp> activeSongs = new ConcurrentHashMap<Integer,Timestamp>();

	public void startSinging(int spellId) {
		LocalDateTime datetime = LocalDateTime.now();
		Timestamp nowtimestamp = Timestamp.valueOf(datetime);
		activeSongs.put(spellId, nowtimestamp);
	}

	public boolean isSinging(int spellId) {
		if (activeSongs.get(spellId) == null)
			return false;
		
		return true;
	}
	
	public ArrayList<Integer> getSpellIds() {
		return new ArrayList<Integer>(activeSongs.keySet());
	}

	public void stopSinging(int spellId) {
		if (activeSongs.get(spellId) == null)
			return;
		
		activeSongs.remove(spellId);
	}

	public boolean isSinging() {
		if (activeSongs.size() < 1)
			return false;
		
		return true;
	}

	public boolean isLastSongSinging(int spellId) {
		if (!isSinging())
			return false;

		if (!isSinging(spellId))
			return false;
		Timestamp highest = null;
		Integer highestId = null;
		for(Integer key : this.activeSongs.keySet())
		{
			Timestamp timestamp = this.activeSongs.get(key);
			if (highest == null || timestamp.after(highest))
			{
				highest = timestamp;
				highestId = key;
			}
		}
		
		if (highest == null)
			return false;
		
		if (highestId == spellId)
			return true;
			
		return false;
	}
	
}
