package com.solinia.solinia.Models;

import com.solinia.solinia.Exceptions.InvalidPacketException;
import com.solinia.solinia.Interfaces.ISoliniaPacket;

public class PacketMemorisedSpells implements ISoliniaPacket {
	MemorisedSpells memorisedSpells;
	
	public MemorisedSpells getMemorisedSpells()
	{
		return this.memorisedSpells;
	}
	
	public void fromPacketData(String data) throws InvalidPacketException
	{
		if (data == null)
			throw new InvalidPacketException("Packet data is empty");

		// now pages
		this.memorisedSpells = new MemorisedSpells();

		if (data.equals(""))
			return;
		
		String[] dataArray = data.split("\\^",-1);
		
		for(int i = 0; i < dataArray.length; i++)
		{
			String[] spellArray = dataArray[i].split("\\|",-1);
			int slotNo = Integer.parseInt(spellArray[0]);
			int Id = Integer.parseInt(spellArray[1]);
			int Icon = Integer.parseInt(spellArray[2]);
			int NewIcon = Integer.parseInt(spellArray[3]);
			int MemIcon = Integer.parseInt(spellArray[4]);
			String Name = spellArray[5];
			int Level = Integer.parseInt(spellArray[6]);
			this.memorisedSpells.setSlot(slotNo,Id,Icon,NewIcon,MemIcon,Name,Level);
		}
	}
	
	public String toPacketData()
	{
		String packetData = "";
		boolean first = true;
		for(int i = 1; i <= 16; i++)
		{
			if (this.memorisedSpells.getSlotId(i) < 1)
				continue;
			
			if (first)
				first = false;
			else
				packetData += "^";
			
			packetData += i + "|"
					+ this.memorisedSpells.getSlotId(i) + "|" 
					+ this.memorisedSpells.getSlotIcon(i)+ "|" 
					+ this.memorisedSpells.getSlotNewIcon(i)+ "|" 
					+ this.memorisedSpells.getSlotMemIcon(i)+ "|" 
					+ this.memorisedSpells.getSlotName(i) + "|"
					+ this.memorisedSpells.getSlotLevel(i);
		}
		
		return packetData;
	}
	
	public void fromData(MemorisedSpells memorisedSpells) {
		this.memorisedSpells = memorisedSpells;
	}

}
