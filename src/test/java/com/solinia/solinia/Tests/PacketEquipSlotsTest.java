package com.solinia.solinia.Tests;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import com.solinia.solinia.Exceptions.InvalidPacketException;
import com.solinia.solinia.Models.EquipSlots;
import com.solinia.solinia.Models.PacketEquipSlots;

public class PacketEquipSlotsTest {
	@Test
	public void WhenGivenPacketDataReturnExpectedFormat() {
		String testForPacketData = "1^1^1^1^^^^";
		String foundPacketData = "";
		try {
			PacketEquipSlots object = new PacketEquipSlots();
			object.fromPacketData(testForPacketData);
			foundPacketData = object.toPacketData();
		} catch (InvalidPacketException e) {
			e.printStackTrace();
		}
        assertEquals(testForPacketData, foundPacketData);
    }
	
	@Test
	public void WhenGivenPacketDataReturnsAllValues() {
		int testSlots = 8;
		
		String testPacketData = "";
		boolean first = true;
		
		for (int i = 0; i < testSlots; i++)
		{
			if (first)
				first = false;
			else
				testPacketData += "^";
			
			testPacketData += 
					"" + i+"000";
		}
		
		for (int i = 0; i < testSlots; i++)
		{
			try {
				PacketEquipSlots vitals = new PacketEquipSlots();
				vitals.fromPacketData(testPacketData);
				EquipSlots page = vitals.getEquipSlots();
				
				assertEquals("" + i+"000", page.getSlotByIndex(i));
			} catch (InvalidPacketException e) {
				e.printStackTrace();
			}
	        
		}
    }
}
