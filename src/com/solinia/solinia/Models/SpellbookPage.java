package com.solinia.solinia.Models;

import java.util.List;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Managers.StateManager;

public class SpellbookPage {
	public int PageNo;
	public String SpellSlot1Name;
	public String SpellSlot2Name;
	public String SpellSlot3Name;
	public String SpellSlot4Name;
	public String SpellSlot5Name;
	public String SpellSlot6Name;
	public String SpellSlot7Name;
	public String SpellSlot8Name;
	public String SpellSlot9Name;
	public String SpellSlot10Name;
	public int SpellSlot1Icon;
	public int SpellSlot2Icon;
	public int SpellSlot3Icon;
	public int SpellSlot4Icon;
	public int SpellSlot5Icon;
	public int SpellSlot6Icon;
	public int SpellSlot7Icon;
	public int SpellSlot8Icon;
	public int SpellSlot9Icon;
	public int SpellSlot10Icon;
	public int SpellSlot1MemIcon;
	public int SpellSlot2MemIcon;
	public int SpellSlot3MemIcon;
	public int SpellSlot4MemIcon;
	public int SpellSlot5MemIcon;
	public int SpellSlot6MemIcon;
	public int SpellSlot7MemIcon;
	public int SpellSlot8MemIcon;
	public int SpellSlot9MemIcon;
	public int SpellSlot10MemIcon;


	public SpellbookPage(int pageNo, List<Integer> itemIdsOnPage) {
		this.PageNo = pageNo;
		
		System.out.println("Generating spell book with page size of: " + itemIdsOnPage.size());
		
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
		if (itemIdsOnPage.size() >= 9)
		setSlot(9,itemIdsOnPage.get(8));
		if (itemIdsOnPage.size() >= 10)
		setSlot(10,itemIdsOnPage.get(9));
	}

	private void setSlot(int slot, int itemId) {
		if (itemId < 1)
			return;
		
		try
		{
			ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(itemId);
			if (item == null)
				return;
			
			if (!item.isSpellscroll())
				return;
			
			ISoliniaSpell spell = StateManager.getInstance().getConfigurationManager().getSpell(item.getAbilityid());
			if (spell == null)
				return;
			
			switch(slot)
			{
				case 1:
					SpellSlot1Name = spell.getName();
					SpellSlot1Icon = spell.getIcon();
					SpellSlot1MemIcon = spell.getMemicon();
				break;
				case 2:
					SpellSlot2Name = spell.getName();
					SpellSlot2Icon = spell.getIcon();
					SpellSlot2MemIcon = spell.getMemicon();
				break;
				case 3:
					SpellSlot3Name = spell.getName();
					SpellSlot3Icon = spell.getIcon();
					SpellSlot3MemIcon = spell.getMemicon();
				break;
				case 4:
					SpellSlot4Name = spell.getName();
					SpellSlot4Icon = spell.getIcon();
					SpellSlot4MemIcon = spell.getMemicon();
				break;
				case 5:
					SpellSlot5Name = spell.getName();
					SpellSlot5Icon = spell.getIcon();
					SpellSlot5MemIcon = spell.getMemicon();
				break;
				case 6:
					SpellSlot6Name = spell.getName();
					SpellSlot6Icon = spell.getIcon();
					SpellSlot6MemIcon = spell.getMemicon();
				break;
				case 7:
					SpellSlot7Name = spell.getName();
					SpellSlot7Icon = spell.getIcon();
					SpellSlot7MemIcon = spell.getMemicon();
				break;
				case 8:
					SpellSlot8Name = spell.getName();
					SpellSlot8Icon = spell.getIcon();
					SpellSlot8MemIcon = spell.getMemicon();
				break;
				case 9:
					SpellSlot9Name = spell.getName();
					SpellSlot9Icon = spell.getIcon();
					SpellSlot9MemIcon = spell.getMemicon();
					break;
				case 10:
					SpellSlot10Name = spell.getName();
					SpellSlot10Icon = spell.getIcon();
					SpellSlot10MemIcon = spell.getMemicon();
					break;
				default:
					return;
			}
		} catch (CoreStateInitException e)
		{
			
		}
	}
}
