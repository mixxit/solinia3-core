package com.solinia.solinia.Models;

import java.util.Map;

import com.solinia.solinia.Exceptions.InvalidPacketException;
import com.solinia.solinia.Interfaces.ISoliniaPacket;

public class PacketEffects implements ISoliniaPacket {

	Effects effects = new Effects();
	
	public Effects getEffects()
	{
		return this.effects;
	}
	
	public void fromPacketData(String data) throws InvalidPacketException
	{
		if (data == null)
			throw new InvalidPacketException("Packet data is empty");

		// now pages
		this.effects = new Effects();
		
		if (data.equals(""))
			return;
		
		String[] dataArray = data.split("\\^",-1);
		
		for(int i = 0; i < dataArray.length; i++)
		{
			String[] effectArray = dataArray[i].split("\\|",-1);
			int SpellId = Integer.parseInt(effectArray[0]);
			int Icon = Integer.parseInt(effectArray[1]);
			int NewIcon = Integer.parseInt(effectArray[2]);
			int MemIcon = Integer.parseInt(effectArray[3]);
			String Name = effectArray[4];
			int TicksLeft = Integer.parseInt(effectArray[5]);
			this.effects.effectSlots.put(SpellId, new EffectSlot(SpellId,Icon,MemIcon,NewIcon,Name,TicksLeft));
		}
	}
	
	public String toPacketData()
	{
		String packetData = "";
		boolean first = true;
		for(Map.Entry<Integer, EffectSlot> entry : this.effects.effectSlots.entrySet())
		{
			if (first)
				first = false;
			else
				packetData += "^";
			
			packetData += 
					+ entry.getValue().SpellId + "|" 
					+ entry.getValue().Icon + "|" 
					+ entry.getValue().NewIcon + "|" 
					+ entry.getValue().MemIcon + "|" 
					+ entry.getValue().Name + "|"
					+ entry.getValue().TicksLeft;
		}
		
		return packetData;
	}
	
	public void fromData(Effects effects) {
		this.effects = effects;
	}
}