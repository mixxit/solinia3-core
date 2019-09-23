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
		String expectedException = "Packet data is wrong format";
		String actualException = "";
		try {
			new PacketMobVitals().fromPacketData("moo");
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
			new PacketMobVitals().fromPacketData(null);
		} catch (InvalidPacketException e) {
			actualException = e.getMessage();
		}
        assertEquals(expectedException, actualException);
    }
	
	@Test
	public void IfMissingElementsThrowInvalidPacket() {
		String expectedException = "Packet data missing elements";
		String actualException = "";
		try {
			new PacketMobVitals().fromPacketData("1^");
		} catch (InvalidPacketException e) {
			actualException = e.getMessage();
		}
        assertEquals(expectedException, actualException);
    }
}
