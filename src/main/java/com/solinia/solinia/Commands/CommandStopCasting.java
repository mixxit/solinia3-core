package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.CastingSpell;

public class CommandStopCasting implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		try {
			Player player = (Player) sender;
			CastingSpell castingSpell = StateManager.getInstance().getEntityManager().getCasting(player);
			if (castingSpell != null) {
				if (castingSpell.getSpell() != null && castingSpell.getSpell().getUninterruptable() == 0) {
					StateManager.getInstance().getEntityManager().interruptCasting(player);
				}
			}
			
		} catch (CoreStateInitException e)
		{
			
		}
		return true;
	}
}
