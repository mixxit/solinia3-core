package com.solinia.solinia.Models;

import java.util.List;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Managers.StateManager;

public class MemorisedSpells {
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
		System.out.println("Generating memorised list with page size of: " + itemIdsOnPage.size());
		
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
					SpellSlot1Name = spell.getId() + "|" + spell.getName();
					SpellSlot1Icon = spell.getIcon();
					SpellSlot1MemIcon = spell.getMemicon();
					SpellSlot1NewIcon = spell.getNewIcon();
				break;
				case 2:
					SpellSlot2Name = spell.getId() + "|" + spell.getName();
					SpellSlot2Icon = spell.getIcon();
					SpellSlot2MemIcon = spell.getMemicon();
					SpellSlot2NewIcon = spell.getNewIcon();
				break;
				case 3:
					SpellSlot3Name = spell.getId() + "|" + spell.getName();
					SpellSlot3Icon = spell.getIcon();
					SpellSlot3MemIcon = spell.getMemicon();
					SpellSlot3NewIcon = spell.getNewIcon();
				break;
				case 4:
					SpellSlot4Name = spell.getId() + "|" + spell.getName();
					SpellSlot4Icon = spell.getIcon();
					SpellSlot4MemIcon = spell.getMemicon();
					SpellSlot4NewIcon = spell.getNewIcon();
				break;
				case 5:
					SpellSlot5Name = spell.getId() + "|" + spell.getName();
					SpellSlot5Icon = spell.getIcon();
					SpellSlot5MemIcon = spell.getMemicon();
					SpellSlot5NewIcon = spell.getNewIcon();
				break;
				case 6:
					SpellSlot6Name = spell.getId() + "|" + spell.getName();
					SpellSlot6Icon = spell.getIcon();
					SpellSlot6MemIcon = spell.getMemicon();
					SpellSlot6NewIcon = spell.getNewIcon();
				break;
				case 7:
					SpellSlot7Name = spell.getId() + "|" + spell.getName();
					SpellSlot7Icon = spell.getIcon();
					SpellSlot7MemIcon = spell.getMemicon();
					SpellSlot7NewIcon = spell.getNewIcon();
				break;
				case 8:
					SpellSlot8Name = spell.getId() + "|" + spell.getName();
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
