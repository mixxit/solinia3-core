package com.solinia.solinia.Models;

import java.util.UUID;

import com.solinia.solinia.Exceptions.InvalidPacketException;

public class PacketMobVitals {
	private int partyMember = 0;
	private float healthPercent = 0F;
	private float manaPercent = 0F;
	private UUID uniqueId = null;

	public PacketMobVitals(int partyMember, float healthPercent, float manaPercent, UUID uniqueId)
	{
		this.partyMember = partyMember;
		this.healthPercent = healthPercent;
		this.manaPercent = manaPercent;
		this.uniqueId = uniqueId;
	}
	
	public static PacketMobVitals fromPacketData(String data) throws InvalidPacketException
	{
		if (data == null)
			throw new InvalidPacketException("Packet data is empty");

		if (!data.contains("^"))
			throw new InvalidPacketException("Packet data is wrong format");

		String[] dataArray = data.split("\\^");
		if (dataArray.length < 4)
			throw new InvalidPacketException("Packet data missing elements");
		
		int partyMember = Integer.parseInt(dataArray[0]);
		float healthPercent = Float.parseFloat(dataArray[1]);
		float manaPercent = Float.parseFloat(dataArray[2]);
		UUID uniqueId = UUID.fromString(dataArray[3]);
		
		PacketMobVitals vitals = new PacketMobVitals(partyMember, healthPercent, manaPercent, uniqueId);
		return vitals;
	}
	
	public int getPartyMember()
	{
		return this.partyMember;
	}

	public float getHealthPercent()
	{
		return this.healthPercent;
	}

	public float getManaPercent()
	{
		return this.manaPercent;
	}

	public UUID getUniqueId()
	{
		return this.uniqueId;
	}

	
	public String toPacketData()
	{
		String packetData = "";
		packetData += getPartyMember() 
				+ "^" + getHealthPercent() 
				+ "^" + getManaPercent()
				+ "^" + getUniqueId().toString();
		return packetData;
	}
}
