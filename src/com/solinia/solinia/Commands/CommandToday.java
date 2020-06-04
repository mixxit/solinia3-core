package com.solinia.solinia.Commands;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandToday implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		sender.sendMessage("It is currently the Year " + getCurrentUTYear() + " UT");
		return true;
	}
	
	public static int getCurrentUTYear()
	{
		String text = "2020-01-01 00:00:00.00";
		LocalDateTime fromDate = Timestamp.valueOf(text).toLocalDateTime();
		LocalDateTime toDate = LocalDateTime.now();
		
		return getUTYear(fromDate, toDate);
	}

	public static int getUTYear(LocalDateTime fromDate, LocalDateTime toDate)
	{
		int baseYear = 197134;
		
		ZoneId zoneId = ZoneId.systemDefault();
		long from = fromDate.atZone(zoneId).toEpochSecond();
		long to = toDate.atZone(zoneId).toEpochSecond();
		
		long weeks = getWeeksSince(from,to);
		long year = weeks / 12;
		int iYear = (int) Math.floor(year);
		return iYear + baseYear;
	}
	
	public static long getWeeksSince(long fromUnixTime, long toUnixTime) {
		return ((toUnixTime - fromUnixTime) / 3600 / 24 / 7);
	}
}
