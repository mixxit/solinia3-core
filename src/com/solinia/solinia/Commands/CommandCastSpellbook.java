package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;

public class CommandCastSpellbook implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
			return false;

		Player player = (Player) sender;
		
		if (args.length == 0)
			return false;
		
		int itemId = Integer.parseInt(args[0]);
		if (itemId < 1)
			return false;
		
		// Check item in spellbook
		try
		{
			ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
			if (solPlayer == null)
				return false;
			
			if (!solPlayer.getSpellBookItems().contains(itemId))
			{
				player.sendMessage("That spell is not in your spellbook");
				return true;
			}
						
			ISoliniaItem solItem = StateManager.getInstance().getConfigurationManager().getItem(itemId);
			if (solItem == null || !solItem.isSpellscroll())
			{
				player.sendMessage("That is not a valid spell");
				return true;
			}
			
			solPlayer.tryCastFromSpellbook(solItem);
		} catch (CoreStateInitException e)
		{
			return true;
		}
		return true;
	}

}
