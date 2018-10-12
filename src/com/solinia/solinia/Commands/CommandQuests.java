package com.solinia.solinia.Commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.PlayerQuest;
import com.solinia.solinia.Models.QuestStep;

public class CommandQuests implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		try
		{
			Player player = (Player)sender;
			ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(player);
			player.sendMessage("Aquired Quest Flags:");
			String flags = "";
			
			for(String questFlag : solplayer.getPlayerQuestFlags())
			{
				flags += questFlag.toUpperCase() + " ";
			}

			if (flags.length() > 32767)
			{
				flags = flags.substring(0, 32760) + "...";
			}
			
			player.sendMessage(flags.trim());
			
			player.sendMessage("Active Quests:");
			List<String> questFlags = solplayer.getPlayerQuestFlags();
			
			for(PlayerQuest playerQuest : solplayer.getPlayerQuests())
			{
				if (questFlags.contains(playerQuest.getQuest().getQuestFlagCompletion()))
					continue;
				
				player.sendMessage(playerQuest.getQuest().getName() + " Complete: " + playerQuest.isComplete());
				sendQuestSteps(player,playerQuest,questFlags);
			}
		} catch (CoreStateInitException e)
		{
			
		}
		
		return true;
	}

	private void sendQuestSteps(Player player, PlayerQuest playerQuest, List<String> questFlags) {
		for(int stepId : playerQuest.getQuest().getQuestSteps().keySet())
		{
			QuestStep questStep = playerQuest.getQuest().getQuestSteps().get(stepId);
			if (!questFlags.contains(questStep.getTriggerQuestFlag()))
				continue;
			
			if (questFlags.contains(questStep.getCompleteQuestFlag()))
				continue;
			
			player.sendMessage("  [" +  questStep.getSequence() + "] - Description: " + questStep.getDescription());
		}
	}

}
