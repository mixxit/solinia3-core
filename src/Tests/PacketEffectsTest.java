package Tests;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.util.Map;

import com.solinia.solinia.Exceptions.InvalidPacketException;
import com.solinia.solinia.Models.EffectSlot;
import com.solinia.solinia.Models.Effects;
import com.solinia.solinia.Models.MemorisedSpells;
import com.solinia.solinia.Models.PacketEffects;

public class PacketEffectsTest {
	@Test
	public void WhenGivenPacketDataReturnExpectedFormat() {
		String testForPacketData = "1|1|1|1|Name";
		String foundPacketData = "";
		try {
			PacketEffects vitals = new PacketEffects();
			vitals.fromPacketData(testForPacketData);
			foundPacketData = vitals.toPacketData();
		} catch (InvalidPacketException e) {
			e.printStackTrace();
		}
        assertEquals(testForPacketData, foundPacketData);
    }
	
	@Test
	public void WhenGivenPacketDataReturnsAllValues() {
		int testSlots = Effects.TotalBuffsLimit;
		
		String testPacketData = "";
		boolean first = true;
		
		for (int i = 1; i <= testSlots; i++)
		{
			if (first)
				first = false;
			else
				testPacketData += "^";
			
			testPacketData += 
					i+"|"+i+"000|"+i+"111|"+i+"222|Name"+i;
		}
		
		try {
			PacketEffects vitals = new PacketEffects();
			vitals.fromPacketData(testPacketData);
			
			for(Map.Entry<Integer, EffectSlot> entry : vitals.getEffects().getSlots().entrySet())
			{
				int i = entry.getValue().SpellId;
				assertEquals(true,i>0);
				assertEquals(Integer.parseInt(i+"000"), entry.getValue().Icon);
		        assertEquals(Integer.parseInt(i+"111"), entry.getValue().NewIcon);
		        assertEquals(Integer.parseInt(i+"222"), entry.getValue().MemIcon);
		        assertEquals("Name"+i, entry.getValue().Name);
			}
		} catch (InvalidPacketException e) {
			e.printStackTrace();
		}
    }
}
