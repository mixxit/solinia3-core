package Tests;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import com.solinia.solinia.Exceptions.InvalidPacketException;
import com.solinia.solinia.Models.MemorisedSpells;
import com.solinia.solinia.Models.PacketMemorisedSpells;

public class PacketMemorisedSpellsTest {
	@Test
	public void WhenGivenPacketDataReturnExpectedFormat() {
		String testForPacketData = "1|1|1|1|1|Name";
		String foundPacketData = "";
		try {
			PacketMemorisedSpells vitals = new PacketMemorisedSpells();
			vitals.fromPacketData(testForPacketData);
			foundPacketData = vitals.toPacketData();
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
		
		for (int i = 1; i <= testSlots; i++)
		{
			if (first)
				first = false;
			else
				testPacketData += "^";
			
			testPacketData += 
					i+"|"+i+"000|"+i+"111|"+i+"222|"+i+"333|Name"+i;
		}
		
		for (int i = 1; i <= testSlots; i++)
		{
			try {
				PacketMemorisedSpells vitals = new PacketMemorisedSpells();
				vitals.fromPacketData(testPacketData);
				MemorisedSpells page = vitals.getMemorisedSpells();
				
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
}
