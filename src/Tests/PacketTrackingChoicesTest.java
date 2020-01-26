package Tests;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import com.solinia.solinia.Exceptions.InvalidPacketException;
import com.solinia.solinia.Models.PacketTrackingChoices;

public class PacketTrackingChoicesTest {
	@Test
	public void WhenGivenPacketDataReturnExpectedFormat() {
		String testForPacketData = "1|RED|Him|NPCID_333^";
		String foundPacketData = "";
		try {
			PacketTrackingChoices vitals = new PacketTrackingChoices();
			vitals.fromPacketData(testForPacketData);
			foundPacketData = vitals.toPacketData();
		} catch (InvalidPacketException e) {
			e.printStackTrace();
		}
        assertEquals(testForPacketData, foundPacketData);
    }
	
	@Test
	public void WhenGivenMultiplePacketDataReturnExpectedFormat() {
		String testForPacketData = "1|RED|Name|NPCID_333^2|GREEN|Ted|NPCID_334^";
		String foundPacketData = "";
		try {
			PacketTrackingChoices vitals = new PacketTrackingChoices();
			vitals.fromPacketData(testForPacketData);
			foundPacketData = vitals.toPacketData();
		} catch (InvalidPacketException e) {
			e.printStackTrace();
		}
        assertEquals(testForPacketData, foundPacketData);
    }
}
