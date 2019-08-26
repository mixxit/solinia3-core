package com.solinia.solinia.Interfaces;

import java.util.function.Supplier;

import com.solinia.solinia.Exceptions.InvalidPacketException;

public interface ISoliniaPacket {
	String toPacketData();
	void fromPacketData(String data) throws InvalidPacketException;
}
