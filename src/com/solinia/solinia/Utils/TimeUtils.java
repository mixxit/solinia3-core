package com.solinia.solinia.Utils;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.bukkit.World;

public class TimeUtils {
	public static boolean IsDay(World world) {
		long time = world.getTime();
		return time < 12300 || time > Utils.MAXDAYTICK;
	}
	
	public static boolean IsTimeRangeActive(World world, long timefrom, long timeto) {
		long time = world.getTime();

		return time >= timefrom && time <= timeto;
	}

	public static boolean IsNight(World world) {
		return !IsDay(world);
	}
	
	
	public static String getStringFromTimestamp(Timestamp timestamp) {
		Instant instant = timestamp.toInstant();
		OffsetDateTime odt = OffsetDateTime.now();
		ZoneOffset zoneOffset = odt.getOffset();

		ZoneId zoneId = ZoneId.of(zoneOffset.getId());
		ZonedDateTime zdt = ZonedDateTime.ofInstant(instant, zoneId);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu.MM.dd.HH.mm.ss");
		String output = zdt.format(formatter);
		return output;
	}
	
	public static long compareTwoTimeStamps(java.sql.Timestamp currentTime, java.sql.Timestamp oldTime) {
		long milliseconds1 = oldTime.getTime();
		long milliseconds2 = currentTime.getTime();

		long diff = milliseconds2 - milliseconds1;
		long diffSeconds = diff / 1000;
		return diffSeconds;
	}
}
