package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Managers.StateManager;

public class CommandCastSpell implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		if (!sender.isOp() && !sender.hasPermission("solinia.castspell"))
		{
			sender.sendMessage("You do not have permission to access this command");
			return false;
		}

		Player player = (Player) sender;
		
		if (args.length == 0)
			return false;
		
		int spellId = Integer.parseInt(args[0]);
		if (spellId < 1)
			return false;
		
		// Check item in spellbook
		try
		{
			ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
			if (solPlayer == null)
				return false;
			
			ISoliniaSpell solSpell = StateManager.getInstance().getConfigurationManager().getSpell(spellId);
			if (solSpell == null)
			{
				player.sendMessage("That is not a valid spell");
				return true;
			}
			
			solPlayer.tryCastSpell(solSpell, false, false, true);
		} catch (CoreStateInitException e)
		{
			return true;
		}
		return true;
	}

}