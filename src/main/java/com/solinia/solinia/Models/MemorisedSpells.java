package com.solinia.solinia.Models;

import java.util.ArrayList;
import java.util.List;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Managers.StateManager;

public class MemorisedSpells {
	public int SpellSlot1Id = 0;
	public int SpellSlot2Id = 0;
	public int SpellSlot3Id = 0;
	public int SpellSlot4Id = 0;
	public int SpellSlot5Id = 0;
	public int SpellSlot6Id = 0;
	public int SpellSlot7Id = 0;
	public int SpellSlot8Id = 0;
	public String SpellSlot1Name;
	public String SpellSlot2Name;
	public String SpellSlot3Name;
	public String SpellSlot4Name;
	public String SpellSlot5Name;
	public String SpellSlot6Name;
	public String SpellSlot7Name;
	public String SpellSlot8Name;
	public int SpellSlot1Icon;
	public int SpellSlot2Icon;
	public int SpellSlot3Icon;
	public int SpellSlot4Icon;
	public int SpellSlot5Icon;
	public int SpellSlot6Icon;
	public int SpellSlot7Icon;
	public int SpellSlot8Icon;
	public int SpellSlot1MemIcon;
	public int SpellSlot2MemIcon;
	public int SpellSlot3MemIcon;
	public int SpellSlot4MemIcon;
	public int SpellSlot5MemIcon;
	public int SpellSlot6MemIcon;
	public int SpellSlot7MemIcon;
	public int SpellSlot8MemIcon;
	public int SpellSlot1NewIcon;
	public int SpellSlot2NewIcon;
	public int SpellSlot3NewIcon;
	public int SpellSlot4NewIcon;
	public int SpellSlot5NewIcon;
	public int SpellSlot6NewIcon;
	public int SpellSlot7NewIcon;
	public int SpellSlot8NewIcon;
	
	public int SpellSlot1Level;
	public int SpellSlot2Level;
	public int SpellSlot3Level;
	public int SpellSlot4Level;
	public int SpellSlot5Level;
	public int SpellSlot6Level;
	public int SpellSlot7Level;
	public int SpellSlot8Level;

	public MemorisedSpells(List<Integer> itemIdsOnPage, String className) {
		if (itemIdsOnPage.size() >= 1)
		setSlot(1,itemIdsOnPage.get(0),className);
		if (itemIdsOnPage.size() >= 2)
		setSlot(2,itemIdsOnPage.get(1),className);
		if (itemIdsOnPage.size() >= 3)
		setSlot(3,itemIdsOnPage.get(2),className);
		if (itemIdsOnPage.size() >= 4)
		setSlot(4,itemIdsOnPage.get(3),className);
		if (itemIdsOnPage.size() >= 5)
		setSlot(5,itemIdsOnPage.get(4),className);
		if (itemIdsOnPage.size() >= 6)
		setSlot(6,itemIdsOnPage.get(5),className);
		if (itemIdsOnPage.size() >= 7)
		setSlot(7,itemIdsOnPage.get(6),className);
		if (itemIdsOnPage.size() >= 8)
		setSlot(8,itemIdsOnPage.get(7),className);
	}
	
	public MemorisedSpells() {
		// TODO Auto-generated constructor stub
	}

	public List<Integer> getAllSpellIds() {
		List<Integer> returnIds = new ArrayList<Integer>();
		
		if (SpellSlot1Id > 0)
			returnIds.add(SpellSlot1Id);
		if (SpellSlot2Id > 0)
			returnIds.add(SpellSlot2Id);
		if (SpellSlot3Id > 0)
			returnIds.add(SpellSlot3Id);
		if (SpellSlot4Id > 0)
			returnIds.add(SpellSlot4Id);
		if (SpellSlot5Id > 0)
			returnIds.add(SpellSlot5Id);
		if (SpellSlot6Id > 0)
			returnIds.add(SpellSlot6Id);
		if (SpellSlot7Id > 0)
			returnIds.add(SpellSlot7Id);
		if (SpellSlot8Id > 0)
			returnIds.add(SpellSlot8Id);
		
		return returnIds;
	}
	
	public int getSlotId(int slot) {
		switch(slot)
		{
			case 1:
				return SpellSlot1Id;
			
			case 2:
				return SpellSlot2Id;
			
			case 3:
				return SpellSlot3Id;
			
			case 4:
				return SpellSlot4Id;
			
			case 5:
				return SpellSlot5Id;
			
			case 6:
				return SpellSlot6Id;
			
			case 7:
				return SpellSlot7Id;
			
			case 8:
				return SpellSlot8Id;
			
			default:
				return 0;
		}
	}
	
	public String getSlotName(int slot) {
		switch(slot)
		{
			case 1:
				return SpellSlot1Name;
			
			case 2:
				return SpellSlot2Name;
			
			case 3:
				return SpellSlot3Name;
			
			case 4:
				return SpellSlot4Name;
			
			case 5:
				return SpellSlot5Name;
			
			case 6:
				return SpellSlot6Name;
			
			case 7:
				return SpellSlot7Name;
			
			case 8:
				return SpellSlot8Name;
			default:
				return null;
		}
	}
	
	public int getSlotIcon(int slot) {
		switch(slot)
		{
			case 1:
				return SpellSlot1Icon;
			
			case 2:
				return SpellSlot2Icon;
			
			case 3:
				return SpellSlot3Icon;
			
			case 4:
				return SpellSlot4Icon;
			
			case 5:
				return SpellSlot5Icon;
			
			case 6:
				return SpellSlot6Icon;
			
			case 7:
				return SpellSlot7Icon;
			
			case 8:
				return SpellSlot8Icon;
			default:
				return 0;
		}
	}

	public int getSlotMemIcon(int slot) {
		switch(slot)
		{
			case 1:
				return SpellSlot1MemIcon;
			
			case 2:
				return SpellSlot2MemIcon;
			
			case 3:
				return SpellSlot3MemIcon;
			
			case 4:
				return SpellSlot4MemIcon;
			
			case 5:
				return SpellSlot5MemIcon;
			
			case 6:
				return SpellSlot6MemIcon;
			
			case 7:
				return SpellSlot7MemIcon;
			
			case 8:
				return SpellSlot8MemIcon;
			default:
				return 0;
		}
	}

	public int getSlotNewIcon(int slot) {
		switch(slot)
		{
			case 1:
				return SpellSlot1NewIcon;
			
			case 2:
				return SpellSlot2NewIcon;
			
			case 3:
				return SpellSlot3NewIcon;
			
			case 4:
				return SpellSlot4NewIcon;
			
			case 5:
				return SpellSlot5NewIcon;
			
			case 6:
				return SpellSlot6NewIcon;
			
			case 7:
				return SpellSlot7NewIcon;
			
			case 8:
				return SpellSlot8NewIcon;
				
			default:
				return 0;
		}
	}
	
	public int getSlotLevel(int slot) {
		switch(slot)
		{
			case 1:
				return SpellSlot1Level;
			
			case 2:
				return SpellSlot2Level;
			
			case 3:
				return SpellSlot3Level;
			
			case 4:
				return SpellSlot4Level;
			
			case 5:
				return SpellSlot5Level;
			
			case 6:
				return SpellSlot6Level;
			
			case 7:
				return SpellSlot7Level;
			
			case 8:
				return SpellSlot8Level;
				
			default:
				return 0;
		}
	}

	public void setSlot(int spellSlot, int id, int icon, int newIcon, int memIcon, String name, int level) {
		switch(spellSlot)
		{
			case 1:
				SpellSlot1Id = id;
				SpellSlot1Name = name;
				SpellSlot1Icon = icon;
				SpellSlot1MemIcon = memIcon;
				SpellSlot1NewIcon = newIcon;
				SpellSlot1Level = level;
			break;
			case 2:
				SpellSlot2Id = id;
				SpellSlot2Name = name;
				SpellSlot2Icon = icon;
				SpellSlot2MemIcon = memIcon;
				SpellSlot2NewIcon = newIcon;
				SpellSlot2Level = level;
			break;
			case 3:
				SpellSlot3Id = id;
				SpellSlot3Name = name;
				SpellSlot3Icon = icon;
				SpellSlot3MemIcon = memIcon;
				SpellSlot3NewIcon = newIcon;
				SpellSlot3Level = level;
			break;
			case 4:
				SpellSlot4Id = id;
				SpellSlot4Name = name;
				SpellSlot4Icon = icon;
				SpellSlot4MemIcon = memIcon;
				SpellSlot4NewIcon = newIcon;
				SpellSlot4Level = level;
			break;
			case 5:
				SpellSlot5Id = id;
				SpellSlot5Name = name;
				SpellSlot5Icon = icon;
				SpellSlot5MemIcon = memIcon;
				SpellSlot5NewIcon = newIcon;
				SpellSlot5Level = level;
			break;
			case 6:
				SpellSlot6Id = id;
				SpellSlot6Name = name;
				SpellSlot6Icon = icon;
				SpellSlot6MemIcon = memIcon;
				SpellSlot6NewIcon = newIcon;
				SpellSlot6Level = level;
			break;
			case 7:
				SpellSlot7Id = id;
				SpellSlot7Name = name;
				SpellSlot7Icon = icon;
				SpellSlot7MemIcon = memIcon;
				SpellSlot7NewIcon = newIcon;
				SpellSlot7Level = level;
			break;
			case 8:
				SpellSlot8Id = id;
				SpellSlot8Name = name;
				SpellSlot8Icon = icon;
				SpellSlot8MemIcon = memIcon;
				SpellSlot8NewIcon = newIcon;
				SpellSlot8Level = level;
			break;
			default:
				return;
		}
	}
	
	private void setSlot(int slot, int spellId, String classNameUpper) {
		if (spellId < 1)
			return;
		
		try
		{
			
			ISoliniaSpell spell = StateManager.getInstance().getConfigurationManager().getSpell(spellId);
			if (spell == null)
				return;
			int level = spell.getMinLevelClass(classNameUpper.toUpperCase());
			
			// escape symbols we use for data transfer
			setSlot(slot, spell.getId(), spell.getIcon(), spell.getNewIcon(), spell.getMemicon(), spell.getName().replaceAll("\\|", "").replaceAll("\\^", ""), level);
		} catch (CoreStateInitException e)
		{
			
		}
	}
}
