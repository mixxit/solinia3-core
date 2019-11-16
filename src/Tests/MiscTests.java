package Tests;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.awt.Point;
import java.util.HashMap;
import java.util.List;

import com.solinia.solinia.Exceptions.InvalidPacketException;
import com.solinia.solinia.Models.PacketOpenSpellbook;
import com.solinia.solinia.Models.SpellbookPage;
import com.solinia.solinia.Timers.DynmapTimer;

public class MiscTests {
	@Test
	public void SplitIntoStripsX() {
		// XX0
		// X0X
		// XXX
		
		HashMap<Point, Boolean> rows = new HashMap<Point, Boolean>();
		rows.put(new Point(0,2),true); rows.put(new Point(1,2),true); /*rows.put(new Point(2,2),false);*/
		rows.put(new Point(0,1),true); /*rows.put(new Point(1,1),false);*/ rows.put(new Point(2,1),true);
		rows.put(new Point(0,0),true); rows.put(new Point(1,0),true); rows.put(new Point(2,0),true);
		
		HashMap<String, List<Point>> strips = DynmapTimer.GetStripsX(rows);
		// Should return four strips
		assertEquals(4, strips.size());
		// First Strip should be 3 in length
		assertEquals(3, strips.get("0_0").size());
		assertEquals(1, strips.get("1_0").size());
		assertEquals(1, strips.get("1_2").size());
		assertEquals(2, strips.get("2_0").size());
    }
}
