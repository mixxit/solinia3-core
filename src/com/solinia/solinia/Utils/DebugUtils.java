package com.solinia.solinia.Utils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.DebuggerSettings;

public class DebugUtils {
	public static void ConsoleLogTimingDifference(LocalDateTime datetime)
	{
		LocalDateTime afterdatetime = LocalDateTime.now();

		System.out.println("Timings after: " + ChronoUnit.MICROS.between(datetime, afterdatetime));

	}
	
	
}
