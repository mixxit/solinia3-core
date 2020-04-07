package com.solinia.solinia.Commands;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;

public class CommandTopPlayers implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		try
		{
			sender.sendMessage("Top voting players this month:");
			for(ISoliniaPlayer player : StateManager.getInstance().getPlayerManager().getTopVotingPlayers().stream().limit(5).collect(Collectors.toList()))
			{
				sender.sendMessage(player.getFullName() + " - " + player.getMonthlyVote() + " votes");
			}
		} catch (CoreStateInitException e)
		{
			
		}
		return true;
	}

	public static int getCurrentYear(LocalDateTime fromDate, LocalDateTime toDate)
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
