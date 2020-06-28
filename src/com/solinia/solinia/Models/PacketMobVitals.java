package com.solinia.solinia.Models;

import com.solinia.solinia.Exceptions.InvalidPacketException;
import com.solinia.solinia.Interfaces.ISoliniaPacket;

public class PacketMobVitals implements ISoliniaPacket {
	private int partyMember = 0;
	private float healthPercent = 0F;
	private float manaPercent = 0F;
	private int entityId = 0;
	private String name = "";
	private int level = 0;
	private int xp = 0;
	
	public PacketMobVitals()
	{
		
	}
	
	public void fromPacketData(String data) throws InvalidPacketException
	{
		if (data == null)
			throw new InvalidPacketException("Packet data is empty");

		if (!data.contains("^"))
			throw new InvalidPacketException("Packet data is wrong format");

		String[] dataArray = data.split("\\^",-1);
		if (dataArray.length < 4)
			throw new InvalidPacketException("Packet data missing elements");
		
		int partyMember = Integer.parseInt(dataArray[0]);
		float healthPercent = Float.parseFloat(dataArray[1]);
		float manaPercent = Float.parseFloat(dataArray[2]);
		int entityId = 0;
		try
		{
			entityId = Integer.parseInt(dataArray[3]);
		} catch (Exception e)
		{
			// not valid UUID (ie null
		}
		String name = dataArray[4];
		int level = Integer.parseInt(dataArray[5]);
		int xp = Integer.parseInt(dataArray[6]);
		
		this.partyMember = partyMember;
		this.healthPercent = healthPercent;
		this.manaPercent = manaPercent;
		this.entityId = entityId;
		this.name = name;
		this.level = level;
		this.xp = xp;
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

	public int getEntityId()
	{
		return this.entityId;
	}

	public String getName()
	{
		return this.name;
	}
	
	public int getLevel()
	{
		return this.level;
	}
	
	public int getXP()
	{
		return this.xp;
	}
	
	public String toPacketData()
	{
		String packetData = "";
		String uniqueString = "";
		if (this.getEntityId() > 0)
			uniqueString = Integer.toString(this.getEntityId());
		packetData += getPartyMember() 
				+ "^" + getHealthPercent() 
				+ "^" + getManaPercent()
				+ "^" + uniqueString
				+ "^" + getName()
		+ "^" + getLevel()
		+ "^" + getXP();
		return packetData;
	}

	public void fromData(int partyMember, float healthPercent, float manaPercent, int entityId, String name, int level, int xp) {
		this.partyMember = partyMember;
		this.healthPercent = healthPercent;
		this.manaPercent = manaPercent;
		this.entityId = entityId;
		this.name = name.replaceAll("\\^", "").replaceAll("\\|", "");
		this.level = level;
		this.xp = xp;
	}
}
