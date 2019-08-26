package com.solinia.solinia.Interfaces;

import com.solinia.solinia.Exceptions.InvalidPacketException;

public interface ISoliniaPacket {
	public String toPacketData();
	void fromPacketData(String data) throws InvalidPacketException;
	public void handle();
}