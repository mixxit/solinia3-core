package com.solinia.solinia.Models;

import com.solinia.solinia.Exceptions.InvalidPacketException;
import com.solinia.solinia.Interfaces.ISoliniaPacket;

public class PacketCastingPercent implements ISoliniaPacket {
	private float castingPercent = 0F;

	public PacketCastingPercent()
	{
		
	}
	
	public void fromPacketData(String data) throws InvalidPacketException
	{
		if (data == null)
			throw new InvalidPacketException("Packet data is empty");

		if (!data.contains("^"))
			throw new InvalidPacketException("Packet data is wrong format");

		String[] dataArray = data.split("\\^");
		
		float castingPercent = Float.parseFloat(dataArray[0]);
		
		this.castingPercent = castingPercent;
	}
	
	public float getCastingPercent()
	{
		return this.castingPercent;
	}

	
	public String toPacketData()
	{
		String packetData = "";
		packetData += getCastingPercent() + "^";
		return packetData;
	}

	public void fromData(float castingPercent) {
		this.castingPercent = castingPercent;
	}
}
