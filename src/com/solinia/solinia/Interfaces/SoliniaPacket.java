package com.solinia.solinia.Interfaces;

import com.solinia.solinia.Exceptions.InvalidPacketException;

public interface SoliniaPacket {
	public String toPacketData();
	void fromPacketData(String data) throws InvalidPacketException;
}