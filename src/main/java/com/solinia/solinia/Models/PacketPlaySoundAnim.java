package com.solinia.solinia.Models;

import com.solinia.solinia.Exceptions.InvalidPacketException;
import com.solinia.solinia.Interfaces.ISoliniaPacket;

public class PacketPlaySoundAnim implements ISoliniaPacket {
	private int soundAnim = 0;

	public PacketPlaySoundAnim()
	{
		
	}
	
	public void fromPacketData(String data) throws InvalidPacketException
	{
		if (data == null)
			throw new InvalidPacketException("Packet data is empty");

		if (!data.contains("^"))
			throw new InvalidPacketException("Packet data is wrong format");

		String[] dataArray = data.split("\\^",-1);
		
		int soundAnim = Integer.parseInt(dataArray[0]);
		
		this.soundAnim = soundAnim;
	}
	
	public int getSoundAnim()
	{
		return this.soundAnim;
	}

	
	public String toPacketData()
	{
		String packetData = "";
		packetData += getSoundAnim() + "^";
		return packetData;
	}

	public void fromData(int soundAnim) {
		this.soundAnim = soundAnim;
	}
}