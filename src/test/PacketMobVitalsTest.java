import com.solinia.solinia.Exceptions.InvalidPacketException;
import com.solinia.solinia.Models.PacketMobVitals;

import junit.framework.TestCase;

public class PacketMobVitalsTest extends TestCase {
	public void testSum() {
		boolean threwException = false;
		try {
			PacketMobVitals.fromPacketData("moo");
		} catch (InvalidPacketException e) {
			threwException = true;
		}
        assertEquals(threwException, true);
    }
}
