package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaQuest;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaQuest;

public class CommandCreateQuest implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;

		if (!sender.isOp() && !sender.hasPermission("solinia.createquest"))
		{
			sender.sendMessage("You do not have permission to access this command");
			return false;
		}

		if (args.length < 1) {
			sender.sendMessage("Insufficient arguments: name");
			return false;
		}

		String questname = args[0];

		if (questname.equals("")) {
			sender.sendMessage("Invalid quest name");
			return false;
		}
		
		try {
			for(ISoliniaQuest quest : StateManager.getInstance().getConfigurationManager().getQuests())
			{
				if (quest.getName().toUpperCase().equals(questname.toUpperCase()))
				{
					sender.sendMessage("That quest already exists");
					return false;
				}
			}
			
			SoliniaQuest quest = new SoliniaQuest();
			quest.setId(StateManager.getInstance().getConfigurationManager().getNextQuestId());
			quest.setName(questname);
			StateManager.getInstance().getConfigurationManager().addQuest(quest);
			sender.sendMessage("Quest created! [" + quest.getId() + "]");
		} catch (CoreStateInitException e) {
			sender.sendMessage(e.getMessage());
		}
		return true;
	}
}
