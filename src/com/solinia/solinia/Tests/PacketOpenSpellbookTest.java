package com.solinia.solinia.Tests;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import com.solinia.solinia.Exceptions.InvalidPacketException;
import com.solinia.solinia.Models.PacketOpenSpellbook;
import com.solinia.solinia.Models.SpellbookPage;

public class PacketOpenSpellbookTest {
	@Test
	public void WhenGivenPacketDataReturnExpectedFormat() {
		String testForPacketData = "1^1|1|1|1|1|Name";
		String foundPacketData = "";
		try {
			PacketOpenSpellbook vitals = new PacketOpenSpellbook();
			vitals.fromPacketData(testForPacketData);
			foundPacketData = vitals.toPacketData();
		} catch (InvalidPacketException e) {
			e.printStackTrace();
		}
        assertEquals(testForPacketData, foundPacketData);
    }
	
	@Test
	public void WhenGivenPacketDataReturnsAllValues() {
		int testSlots = 16;
		
		String testPacketData = "1";
		for (int i = 1; i <= testSlots; i++)
		{
			testPacketData += 
					"^"+i+"|"+i+"000|"+i+"111|"+i+"222|"+i+"333|Name"+i;
		}
		
		for (int i = 1; i <= testSlots; i++)
		{
			try {
				PacketOpenSpellbook vitals = new PacketOpenSpellbook();
				vitals.fromPacketData(testPacketData);
				SpellbookPage page = vitals.getSpellBookPage();
				
				assertEquals(Integer.parseInt(i+"000"), page.getSlotId(i));
		        assertEquals(Integer.parseInt(i+"111"), page.getSlotIcon(i));
		        assertEquals(Integer.parseInt(i+"222"), page.getSlotNewIcon(i));
		        assertEquals(Integer.parseInt(i+"333"), page.getSlotMemIcon(i));
		        assertEquals("Name"+i, page.getSlotName(i));
			} catch (InvalidPacketException e) {
				e.printStackTrace();
			}
	        
		}
    }

	
	@Test
	public void IfMissingSeperatorsThrowInvalidPacket() {
		String expectedException = "Packet data is wrong format";
		String actualException = "";
		try {
			new PacketOpenSpellbook().fromPacketData("moo");
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
			new PacketOpenSpellbook().fromPacketData(null);
		} catch (InvalidPacketException e) {
			actualException = e.getMessage();
		}
        assertEquals(expectedException, actualException);
    }
}
