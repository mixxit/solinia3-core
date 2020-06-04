package com.solinia.solinia.Commands;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.solinia.solinia.Solinia3CorePlugin;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;

public class CommandBirthday implements CommandExecutor {
	Plugin plugin;
	public CommandBirthday(Solinia3CorePlugin solinia3CorePlugin) {
		this.plugin = solinia3CorePlugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;
		
		// Args
		// playername
		// websiteurl
		
		if (args.length < 1)
		{
			SendPlayerBirthday((Player)sender);
			return false;
		}
		
		LocalDateTime birthday = LocalDate.parse(args[0]).atStartOfDay();
		
		try
		{
			ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player)sender);
			if (solPlayer.getBirthday() != null)
			{
				sender.sendMessage("You have already set your birthday");
				return true;
			}
			
			solPlayer.setBirthday(Timestamp.valueOf(birthday));
		
		} catch (CoreStateInitException e)
		{
			
		}

		return true;
	}

	private void SendPlayerBirthday(Player sender) {
		try
		{
			ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player)sender);
			if (solPlayer.getBirthday() == null)
			{
				sender.sendMessage("You have not set your birthday");
				return;
			}
			
			String text = "2020-01-01 00:00:00.00";
			LocalDateTime fromDate = Timestamp.valueOf(text).toLocalDateTime();
			LocalDateTime toDate = solPlayer.getBirthday().toLocalDateTime();
			
			int characterbirthyear = CommandToday.getUTYear(fromDate, toDate);
			int currentutyear = CommandToday.getCurrentUTYear();
			
			sender.sendMessage("You were born in " + characterbirthyear + " UT making you  " + (currentutyear-characterbirthyear) + " years old");
		
		} catch (CoreStateInitException e)
		{
			
		}
		
		
	}

	
}
