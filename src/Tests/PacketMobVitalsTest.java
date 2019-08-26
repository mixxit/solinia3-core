package Tests;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.util.UUID;

import com.solinia.solinia.Exceptions.InvalidPacketException;
import com.solinia.solinia.Models.PacketMobVitals;

public class PacketMobVitalsTest {
	@Test
	public void WhenGivenPacketDataReturnExpectedFormat() {
		String testForPacketData = "1^1.0^1.0^"+UUID.randomUUID().toString();
		String foundPacketData = "";
		try {
			PacketMobVitals vitals = new PacketMobVitals();
			vitals.fromPacketData(testForPacketData);
			foundPacketData = vitals.toPacketData();
		} catch (InvalidPacketException e) {
			e.printStackTrace();
		}
        assertEquals(testForPacketData, foundPacketData);
    }
	
	@Test
	public void IfMissingSeperatorsThrowInvalidPacket() {
		boolean threwException = false;
		try {
			new PacketMobVitals().fromPacketData("moo");
		} catch (InvalidPacketException e) {
			if (e.getMessage().equals("Packet data is wrong format"))
			threwException = true;
		}
        assertEquals(true, threwException);
    }
	
	@Test
	public void IfMissingNullThrowInvalidPacket() {
		boolean threwException = false;
		try {
			new PacketMobVitals().fromPacketData(null);
		} catch (InvalidPacketException e) {
			if (e.getMessage().equals("Packet data is empty"))
			threwException = true;
		}
        assertEquals(true, threwException);
    }
	
	@Test
	public void IfMissingElementsThrowInvalidPacket() {
		boolean threwException = false;
		try {
			new PacketMobVitals().fromPacketData("1^");
		} catch (InvalidPacketException e) {
			if (e.getMessage().equals("Packet data missing elements"))
			threwException = true;
		}
        assertEquals(true, threwException);
    }
}
