package com.solinia.solinia.Models;

import com.solinia.solinia.Exceptions.InvalidPacketException;
import com.solinia.solinia.Interfaces.ISoliniaPacket;

public class PacketOpenSpellbook implements ISoliniaPacket {
	private SpellbookPage spellbookPage;

	public PacketOpenSpellbook()
	{
		
	}
	
	public SpellbookPage getSpellBookPage()
	{
		return this.spellbookPage;
	}
	
	public void fromPacketData(String data) throws InvalidPacketException
	{
		if (data == null)
			throw new InvalidPacketException("Packet data is empty");

		String[] dataArray = data.split("\\^");
		if (dataArray.length < 1)
			throw new InvalidPacketException("Packet data missing elements");
		
		int spellbookPage = Integer.parseInt(dataArray[0]);
		// now pages
		this.spellbookPage = new SpellbookPage();
		this.spellbookPage.PageNo = spellbookPage;
		
		for(int i = 1; i < dataArray.length; i++)
		{
			String[] spellArray = dataArray[i].split("\\|",-1);
			int slotNo = Integer.parseInt(spellArray[0]);
			int Id = Integer.parseInt(spellArray[1]);
			int Icon = Integer.parseInt(spellArray[2]);
			int NewIcon = Integer.parseInt(spellArray[3]);
			int MemIcon = Integer.parseInt(spellArray[4]);
			String Name = spellArray[5];
			int Level = Integer.parseInt(spellArray[6]);
			this.spellbookPage.setSpellSlot(slotNo,Id,Icon,NewIcon,MemIcon,Name,Level);
		}
	}
	
	public String toPacketData()
	{
		String packetData = "" + this.spellbookPage.PageNo;
		for(int i = 1; i <= 16; i++)
		{
			if (this.spellbookPage.getSlotId(i) < 1)
				continue;
			
			packetData += "^" + i
					+ "|" + this.spellbookPage.getSlotId(i) 
					+ "|" + this.spellbookPage.getSlotIcon(i)
					+ "|" + this.spellbookPage.getSlotNewIcon(i)
					+ "|" + this.spellbookPage.getSlotMemIcon(i)
					+ "|" + this.spellbookPage.getSlotName(i)
					+ "|" + this.spellbookPage.getSlotLevel(i);
		}
		return packetData;
	}

	public void fromData(SpellbookPage spellbookPage) {
		this.spellbookPage = spellbookPage;
	}
}
