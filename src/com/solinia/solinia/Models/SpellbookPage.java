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
	public String SpellSlot11Name;
	public String SpellSlot12Name;
	public String SpellSlot13Name;
	public String SpellSlot14Name;
	public String SpellSlot15Name;
	public String SpellSlot16Name;
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
	public int SpellSlot11Icon;
	public int SpellSlot12Icon;
	public int SpellSlot13Icon;
	public int SpellSlot14Icon;
	public int SpellSlot15Icon;
	public int SpellSlot16Icon;
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
	public int SpellSlot11MemIcon;
	public int SpellSlot12MemIcon;
	public int SpellSlot13MemIcon;
	public int SpellSlot14MemIcon;
	public int SpellSlot15MemIcon;
	public int SpellSlot16MemIcon;
	public int SpellSlot1NewIcon;
	public int SpellSlot2NewIcon;
	public int SpellSlot3NewIcon;
	public int SpellSlot4NewIcon;
	public int SpellSlot5NewIcon;
	public int SpellSlot6NewIcon;
	public int SpellSlot7NewIcon;
	public int SpellSlot8NewIcon;
	public int SpellSlot9NewIcon;
	public int SpellSlot10NewIcon;
	public int SpellSlot11NewIcon;
	public int SpellSlot12NewIcon;
	public int SpellSlot13NewIcon;
	public int SpellSlot14NewIcon;
	public int SpellSlot15NewIcon;
	public int SpellSlot16NewIcon;

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
		if (itemIdsOnPage.size() >= 11)
		setSlot(11,itemIdsOnPage.get(10));
		if (itemIdsOnPage.size() >= 12)
		setSlot(12,itemIdsOnPage.get(11));
		if (itemIdsOnPage.size() >= 13)
		setSlot(13,itemIdsOnPage.get(12));
		if (itemIdsOnPage.size() >= 14)
		setSlot(14,itemIdsOnPage.get(13));
		if (itemIdsOnPage.size() >= 15)
		setSlot(15,itemIdsOnPage.get(14));
		if (itemIdsOnPage.size() >= 16)
		setSlot(16,itemIdsOnPage.get(15));
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
					SpellSlot1NewIcon = spell.getNewIcon();
				break;
				case 2:
					SpellSlot2Name = spell.getName();
					SpellSlot2Icon = spell.getIcon();
					SpellSlot2MemIcon = spell.getMemicon();
					SpellSlot2NewIcon = spell.getNewIcon();
				break;
				case 3:
					SpellSlot3Name = spell.getName();
					SpellSlot3Icon = spell.getIcon();
					SpellSlot3MemIcon = spell.getMemicon();
					SpellSlot3NewIcon = spell.getNewIcon();
				break;
				case 4:
					SpellSlot4Name = spell.getName();
					SpellSlot4Icon = spell.getIcon();
					SpellSlot4MemIcon = spell.getMemicon();
					SpellSlot4NewIcon = spell.getNewIcon();
				break;
				case 5:
					SpellSlot5Name = spell.getName();
					SpellSlot5Icon = spell.getIcon();
					SpellSlot5MemIcon = spell.getMemicon();
					SpellSlot5NewIcon = spell.getNewIcon();
				break;
				case 6:
					SpellSlot6Name = spell.getName();
					SpellSlot6Icon = spell.getIcon();
					SpellSlot6MemIcon = spell.getMemicon();
					SpellSlot6NewIcon = spell.getNewIcon();
				break;
				case 7:
					SpellSlot7Name = spell.getName();
					SpellSlot7Icon = spell.getIcon();
					SpellSlot7MemIcon = spell.getMemicon();
					SpellSlot7NewIcon = spell.getNewIcon();
				break;
				case 8:
					SpellSlot8Name = spell.getName();
					SpellSlot8Icon = spell.getIcon();
					SpellSlot8MemIcon = spell.getMemicon();
					SpellSlot8NewIcon = spell.getNewIcon();
				break;
				case 9:
					SpellSlot9Name = spell.getName();
					SpellSlot9Icon = spell.getIcon();
					SpellSlot9MemIcon = spell.getMemicon();
					SpellSlot9NewIcon = spell.getNewIcon();
					break;
				case 10:
					SpellSlot10Name = spell.getName();
					SpellSlot10Icon = spell.getIcon();
					SpellSlot10MemIcon = spell.getMemicon();
					SpellSlot10NewIcon = spell.getNewIcon();
					break;
				case 11:
					SpellSlot11Name = spell.getName();
					SpellSlot11Icon = spell.getIcon();
					SpellSlot11MemIcon = spell.getMemicon();
					SpellSlot11NewIcon = spell.getNewIcon();
					break;
				case 12:
					SpellSlot12Name = spell.getName();
					SpellSlot12Icon = spell.getIcon();
					SpellSlot12MemIcon = spell.getMemicon();
					SpellSlot12NewIcon = spell.getNewIcon();
					break;
				case 13:
					SpellSlot13Name = spell.getName();
					SpellSlot13Icon = spell.getIcon();
					SpellSlot13MemIcon = spell.getMemicon();
					SpellSlot13NewIcon = spell.getNewIcon();
					break;
				case 14:
					SpellSlot14Name = spell.getName();
					SpellSlot14Icon = spell.getIcon();
					SpellSlot14MemIcon = spell.getMemicon();
					SpellSlot14NewIcon = spell.getNewIcon();
					break;
				case 15:
					SpellSlot15Name = spell.getName();
					SpellSlot15Icon = spell.getIcon();
					SpellSlot15MemIcon = spell.getMemicon();
					SpellSlot15NewIcon = spell.getNewIcon();
					break;
				case 16:
					SpellSlot16Name = spell.getName();
					SpellSlot16Icon = spell.getIcon();
					SpellSlot16MemIcon = spell.getMemicon();
					SpellSlot16NewIcon = spell.getNewIcon();
					break;
				default:
					return;
			}
		} catch (CoreStateInitException e)
		{
			
		}
	}
}
