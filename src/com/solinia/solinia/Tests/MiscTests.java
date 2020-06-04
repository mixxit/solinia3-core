package com.solinia.solinia.Tests;


import org.junit.Test;

import com.solinia.solinia.Commands.CommandToday;
import com.solinia.solinia.Timers.SoliniaZonesDynmapTimer;

import static org.junit.Assert.assertEquals;

import java.awt.Point;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;

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
		
		HashMap<String, List<Point>> strips = SoliniaZonesDynmapTimer.GetStripsX(rows);
		// Should return four strips
		assertEquals(4, strips.size());
		// First Strip should be 3 in length
		assertEquals(3, strips.get("0_0").size());
		assertEquals(1, strips.get("1_0").size());
		assertEquals(1, strips.get("1_2").size());
		assertEquals(2, strips.get("2_0").size());
    }
	
	@Test
	public void getCurrentYear() {
		String fromDateStr = "2020-01-01 00:00:00.00";
		LocalDateTime fromDate = Timestamp.valueOf(fromDateStr).toLocalDateTime();

		String toDateStr = "2020-01-01 00:00:00.00";
		LocalDateTime toDate = Timestamp.valueOf(toDateStr).toLocalDateTime();

		assertEquals(197134, CommandToday.getUTYear(fromDate, toDate));

		toDateStr = "2020-02-13 00:00:00.00";
		toDate = Timestamp.valueOf(toDateStr).toLocalDateTime();

		assertEquals(197135, CommandToday.getUTYear(fromDate, toDate));
	}
	
	@Test
	public void getCurrentYear2() {
		String fromDateStr = "2020-01-01 00:00:00.00";
		LocalDateTime fromDate = Timestamp.valueOf(fromDateStr).toLocalDateTime();

		String toDateStr = "2020-03-25 00:00:00.00";
		LocalDateTime toDate = Timestamp.valueOf(toDateStr).toLocalDateTime();

		assertEquals(197135, CommandToday.getUTYear(fromDate, toDate));
	}
	
	
	@Test
	public void getWeeksSince() {
		ZoneId zoneId = ZoneId.systemDefault();
		
		String fromDateStr = "2020-01-01 00:00:00.00";
		ZonedDateTime fromDate = Timestamp.valueOf(fromDateStr).toLocalDateTime().atZone(zoneId);

		String toDateStr = "2020-02-12 00:00:00.00";
		ZonedDateTime toDate = Timestamp.valueOf(toDateStr).toLocalDateTime().atZone(zoneId);

		assertEquals(6, CommandToday.getWeeksSince(fromDate.toEpochSecond(), toDate.toEpochSecond()));
	}
}
