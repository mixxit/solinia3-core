package com.solinia.solinia.Models;

import java.util.ArrayList;
import java.util.List;

import com.solinia.solinia.Exceptions.InvalidPacketException;
import com.solinia.solinia.Interfaces.ISoliniaPacket;

public class PacketTrackingChoices implements ISoliniaPacket {
	private List<TrackingChoice> trackingChoices = new ArrayList<TrackingChoice>();

	public PacketTrackingChoices()
	{
		
	}
	
	public void fromPacketData(String data) throws InvalidPacketException
	{
		if (data == null)
			throw new InvalidPacketException("Packet data is empty");

		if (!data.contains("^"))
			throw new InvalidPacketException("Packet data is wrong format");

		List<TrackingChoice> trackingChoices = new ArrayList<TrackingChoice>();
		
		String[] dataArray = data.split("\\^");
		for(int i = 0; i < dataArray.length; i++)
		{
			String[] trackingArray = dataArray[i].split("\\|",-1);
			TrackingChoice trackingChoice = new TrackingChoice();
			trackingChoice.Distance = Integer.parseInt(trackingArray[0]);
			trackingChoice.Color = trackingArray[1];
			trackingChoice.Name = trackingArray[2];
			trackingChoice.Id = trackingArray[3];
			
			trackingChoices.add(trackingChoice);
		}
		
		this.trackingChoices = trackingChoices;
	}
	
	public List<TrackingChoice> getTrackingChoices()
	{
		return this.trackingChoices;
	}

	
	public String toPacketData()
	{
		String packetData = "";
		for(int i = 0; i < this.trackingChoices.size(); i++)
		{
			packetData +=
					this.trackingChoices.get(i).Distance + "|" +
					this.trackingChoices.get(i).Color + "|" +
					this.trackingChoices.get(i).Name + "|" +
					this.trackingChoices.get(i).Id + "^";
		}
		return packetData;
	}

	public void fromData(List<TrackingChoice> trackingChoices) {
		this.trackingChoices = trackingChoices;
	}
}