package Tests;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import com.solinia.solinia.Exceptions.InvalidPacketException;
import com.solinia.solinia.Models.MemorisedSpells;
import com.solinia.solinia.Models.PacketMemorisedSpells;

public class PacketMemorisedSpellsTest {
	@Test
	public void WhenGivenPacketDataReturnExpectedFormat() {
		String testForPacketData = "1|1|1|1|1|Name^";
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
		for (int i = 1; i <= testSlots; i++)
		{
			testPacketData += 
					i+"|"+i+"000|"+i+"111|"+i+"222|"+i+"333|Name"+i+"^";
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

	
	@Test
	public void IfMissingSeperatorsThrowInvalidPacket() {
		String expectedException = "Packet data is wrong format";
		String actualException = "";
		try {
			new PacketMemorisedSpells().fromPacketData("moo");
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
			new PacketMemorisedSpells().fromPacketData(null);
		} catch (InvalidPacketException e) {
			actualException = e.getMessage();
		}
        assertEquals(expectedException, actualException);
    }
}
