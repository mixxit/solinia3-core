package com.solinia.solinia.Tests;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import com.solinia.solinia.Exceptions.InvalidPacketException;
import com.solinia.solinia.Models.PacketCastingPercent;

public class PacketCastingPercentTest {
	@Test
	public void WhenGivenPacketDataReturnExpectedFormat() {
		String testForPacketData = "1.0^";
		String foundPacketData = "";
		try {
			PacketCastingPercent vitals = new PacketCastingPercent();
			vitals.fromPacketData(testForPacketData);
			foundPacketData = vitals.toPacketData();
		} catch (InvalidPacketException e) {
			e.printStackTrace();
		}
        assertEquals(testForPacketData, foundPacketData);
    }
	
	@Test
	public void IfMissingSeperatorsThrowInvalidPacket() {
		String expectedException = "Packet data is wrong format";
		String actualException = "";
		try {
			new PacketCastingPercent().fromPacketData("moo");
		} catch (InvalidPacketException e) {
			actualException = e.getMessage();
		}
        assertEquals(expectedException, actualException);
    }
	
	@Test
	public void IfMissingNullThrowInvalidPacket() {
		String expectedException = "Packet data is empty";
		String actualException = "";
		try {
			new PacketCastingPercent().fromPacketData(null);
		} catch (InvalidPacketException e) {
			actualException = e.getMessage();
		}
        assertEquals(expectedException, actualException);
    }
}
