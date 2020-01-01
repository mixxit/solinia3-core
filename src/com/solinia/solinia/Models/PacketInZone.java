package com.solinia.solinia.Models;

import com.solinia.solinia.Exceptions.InvalidPacketException;
import com.solinia.solinia.Interfaces.ISoliniaPacket;

public class PacketInZone implements ISoliniaPacket {
	private int zoneId = 0;
	private String zoneMusic = "";
	
	public void fromData(int zoneId, String zoneMusic) {
		this.zoneId = zoneId;
		this.zoneMusic = zoneMusic;
	}
	
	public void fromPacketData(String data) throws InvalidPacketException
	{
		if (data == null)
			throw new InvalidPacketException("Packet data is empty");

		if (!data.contains("^"))
			throw new InvalidPacketException("Packet data is wrong format");

		String[] dataArray = data.split("\\^",-1);
		if (dataArray.length < 2)
			throw new InvalidPacketException("Packet data missing elements");
		
		this.zoneId = Integer.parseInt(dataArray[0]);
		this.zoneMusic = dataArray[1];
	}
	
	public int getZoneId()
	{
		return this.zoneId;
	}

	public String getZoneMusic()
	{
		return this.zoneMusic;
	}
	
	public String toPacketData()
	{
		String packetData = "";
		
		packetData += getZoneId() 
				+ "^" + getZoneMusic();
		return packetData;
	}
}
