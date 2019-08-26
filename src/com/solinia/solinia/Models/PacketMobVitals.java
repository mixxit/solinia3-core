package com.solinia.solinia.Models;

import java.util.UUID;

import com.solinia.solinia.Exceptions.InvalidPacketException;
import com.solinia.solinia.Interfaces.ISoliniaPacket;

public class PacketMobVitals implements ISoliniaPacket {
	private int partyMember = 0;
	private float healthPercent = 0F;
	private float manaPercent = 0F;
	private UUID uniqueId = null;
	private String name = "";
	
	public PacketMobVitals()
	{
		
	}
	
	public void fromPacketData(String data) throws InvalidPacketException
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
		UUID uniqueId = null;
		try
		{
			uniqueId = UUID.fromString(dataArray[3]);
		} catch (Exception e)
		{
			// not valid UUID (ie null
		}
		String name = dataArray[4];
		
		this.partyMember = partyMember;
		this.healthPercent = healthPercent;
		this.manaPercent = manaPercent;
		this.uniqueId = uniqueId;
		this.name = name;
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

	public String getName()
	{
		return this.name;
	}
	
	public String toPacketData()
	{
		String packetData = "";
		String uniqueString = "";
		if (this.getUniqueId() != null)
			uniqueString = this.getUniqueId().toString();
		packetData += getPartyMember() 
				+ "^" + getHealthPercent() 
				+ "^" + getManaPercent()
				+ "^" + uniqueString
				+ "^" + getName();
		return packetData;
	}

	public void fromData(int partyMember, float healthPercent, float manaPercent, UUID uniqueId, String name) {
		this.partyMember = partyMember;
		this.healthPercent = healthPercent;
		this.manaPercent = manaPercent;
		this.uniqueId = uniqueId;
		this.name = name.replaceAll("\\^", "").replaceAll("\\|", "");
	}
}
