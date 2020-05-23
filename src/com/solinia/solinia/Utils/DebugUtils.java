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
	
	public static void DebugLog(String coreclass, String method, Entity focusEntity, String message) {
		if (focusEntity == null)
			return;
		
		try
		{
			coreclass = coreclass.toUpperCase();
			method = method.toUpperCase();
			String focusid = focusEntity.getName().toUpperCase();
			//System.out.println(coreclass + ":" + method + ":" + focusid + ":" + message);
			try {
				for (UUID debuggerUuid : StateManager.getInstance().getPlayerManager().getDebugger().keySet()) {
					Entity entity = Bukkit.getEntity(debuggerUuid);
					if (entity == null)
						continue;
	
					DebuggerSettings settings = StateManager.getInstance().getPlayerManager().getDebugger()
							.get(debuggerUuid);

					if (!settings.isDebugging(coreclass, method, focusid))
						continue;
	
					entity.sendMessage(coreclass + ":" + method + ":" + focusid + ":" + message);
	
				}
			} catch (CoreStateInitException e) {
	
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}

	}
}
