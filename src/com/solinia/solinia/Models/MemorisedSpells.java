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

	public MemorisedSpells(List<Integer> itemIdsOnPage) {
		if (itemIdsOnPage.size() >= 1)
		setSlot(1,itemIdsOnPage.get(0));
		if (itemIdsOnPage.size() >= 2)
		setSlot(2,itemIdsOnPage.get(1));
		if (itemIdsOnPage.size() >= 3)
		setSlot(3,itemIdsOnPage.get(2));
		if (itemIdsOnPage.size() >= 4)
		setSlot(4,itemIdsOnPage.get(3));
		if (itemIdsOnPage.size() >= 5)
		setSlot(5,itemIdsOnPage.get(4));
		if (itemIdsOnPage.size() >= 6)
		setSlot(6,itemIdsOnPage.get(5));
		if (itemIdsOnPage.size() >= 7)
		setSlot(7,itemIdsOnPage.get(6));
		if (itemIdsOnPage.size() >= 8)
		setSlot(8,itemIdsOnPage.get(7));
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
	
	private void setSlot(int slot, int spellId) {
		if (spellId < 1)
			return;
		
		try
		{
			ISoliniaSpell spell = StateManager.getInstance().getConfigurationManager().getSpell(spellId);
			if (spell == null)
				return;
			
			switch(slot)
			{
				case 1:
					SpellSlot1Id = spell.getId();
					SpellSlot1Name = spell.getName();
					SpellSlot1Icon = spell.getIcon();
					SpellSlot1MemIcon = spell.getMemicon();
					SpellSlot1NewIcon = spell.getNewIcon();
				break;
				case 2:
					SpellSlot2Id = spell.getId();
					SpellSlot2Name = spell.getName();
					SpellSlot2Icon = spell.getIcon();
					SpellSlot2MemIcon = spell.getMemicon();
					SpellSlot2NewIcon = spell.getNewIcon();
				break;
				case 3:
					SpellSlot3Id = spell.getId();
					SpellSlot3Name = spell.getName();
					SpellSlot3Icon = spell.getIcon();
					SpellSlot3MemIcon = spell.getMemicon();
					SpellSlot3NewIcon = spell.getNewIcon();
				break;
				case 4:
					SpellSlot4Id = spell.getId();
					SpellSlot4Name = spell.getName();
					SpellSlot4Icon = spell.getIcon();
					SpellSlot4MemIcon = spell.getMemicon();
					SpellSlot4NewIcon = spell.getNewIcon();
				break;
				case 5:
					SpellSlot5Id = spell.getId();
					SpellSlot5Name = spell.getName();
					SpellSlot5Icon = spell.getIcon();
					SpellSlot5MemIcon = spell.getMemicon();
					SpellSlot5NewIcon = spell.getNewIcon();
				break;
				case 6:
					SpellSlot6Id = spell.getId();
					SpellSlot6Name = spell.getName();
					SpellSlot6Icon = spell.getIcon();
					SpellSlot6MemIcon = spell.getMemicon();
					SpellSlot6NewIcon = spell.getNewIcon();
				break;
				case 7:
					SpellSlot7Id = spell.getId();
					SpellSlot7Name = spell.getName();
					SpellSlot7Icon = spell.getIcon();
					SpellSlot7MemIcon = spell.getMemicon();
					SpellSlot7NewIcon = spell.getNewIcon();
				break;
				case 8:
					SpellSlot8Id = spell.getId();
					SpellSlot8Name = spell.getName();
					SpellSlot8Icon = spell.getIcon();
					SpellSlot8MemIcon = spell.getMemicon();
					SpellSlot8NewIcon = spell.getNewIcon();
				break;
				default:
					return;
			}
		} catch (CoreStateInitException e)
		{
			
		}
	}
}
