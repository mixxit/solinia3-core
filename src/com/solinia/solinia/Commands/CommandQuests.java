package com.solinia.solinia.Commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Models.PlayerQuest;
import com.solinia.solinia.Models.QuestStep;

import net.md_5.bungee.api.ChatColor;

public class CommandQuests implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		try
		{
			Player player = (Player)sender;
			ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(player);
			if (player.isOp() || player.hasPermission("solinia.editquest"))
			player.sendMessage("Debug: Aquired Quest Flags:");
			String flags = "";
			
			for(String questFlag : solplayer.getPlayerQuestFlags())
			{
				flags += questFlag.toUpperCase() + " ";
			}

			if (flags.length() > 32767)
			{
				flags = flags.substring(0, 32760) + "...";
			}
			
			if (player.isOp() || player.hasPermission("solinia.editquest"))
			player.sendMessage(flags.trim());
			
			player.sendMessage("Active Quests:");
			List<String> questFlags = solplayer.getPlayerQuestFlags();
			
			for(PlayerQuest playerQuest : solplayer.getPlayerQuests())
			{
				if (questFlags.contains(playerQuest.getQuest().getQuestFlagCompletion().toUpperCase()))
					continue;
				
				player.sendMessage(ChatColor.LIGHT_PURPLE + playerQuest.getQuest().getName() + ChatColor.RESET + " Complete: " + playerQuest.isComplete());
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
			if (!questFlags.contains(questStep.getTriggerQuestFlag().toUpperCase()))
				continue;
			
			if (questFlags.contains(questStep.getCompleteQuestFlag()))
				continue;
			
			player.sendMessage("- Step " +  ChatColor.YELLOW + questStep.getSequence() + ChatColor.RESET +  " - Description: " + questStep.getDescription());
		}
	}

}
