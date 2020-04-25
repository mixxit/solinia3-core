package com.solinia.solinia.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.FactionStandingType;
import com.solinia.solinia.Models.PlayerFactionEntry;
import com.solinia.solinia.Utils.Utils;

public class CommandConsider implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		try
		{
			ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player)sender);
			if (solPlayer.getEntityTarget() == null) {
				sender.sendMessage("You have no target right now");
				return true;
			}
			
			Entity target = solPlayer.getEntityTarget();
			LivingEntity le = (LivingEntity) target;
			ISoliniaLivingEntity solEntity = SoliniaLivingEntityAdapter.Adapt(le);

			
			ISoliniaLivingEntity solPlayerEntity = SoliniaLivingEntityAdapter.Adapt((LivingEntity)sender);
			ChatColor difficultyColor = Utils.getLevelCon(solPlayerEntity.getEffectiveLevel(),solEntity);
			
			String difficultyMessage = "Looks like an even fight.";
			switch (difficultyColor)
			{
				case AQUA:
					difficultyMessage = "Looks like an even fight.";
					break;
				case BLUE:
					difficultyMessage = "Looks like you would have the upper hand.";
					break;
				case GRAY:
					difficultyMessage = "Looks like a reasonably safe opponent.";
					break;
				case RED:
					difficultyMessage = "What would you like your tombstone to say?";
					break;
				case WHITE:
					difficultyMessage = "Looks like an even fight.";
					break;
				case YELLOW:
					difficultyMessage = "Looks like quite a gamble.";
					break;
				default:
					difficultyMessage = "Looks like an even fight.";
					break;
			}
			
			if (!solEntity.isNPC())
			{
				sender.sendMessage(difficultyColor + "* " + solEntity.getName() + " regards you indifferently. " + difficultyMessage);
				return true;
			}
			
			ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(solEntity.getNpcid());
			if (npc.isRaidheroic() || npc.isRaidboss())
			{
				difficultyMessage += " This creature would take an army to defeat!";
			}
			
			if (npc.getFactionid() < 1)
			{
				sender.sendMessage(difficultyColor + "* " + solEntity.getName() + " regards you indifferently. " + difficultyMessage);
				return true;
			}
			
			PlayerFactionEntry factionEntry = solPlayer.getFactionEntry(npc.getFactionid());
			
			FactionStandingType standing = Utils.getFactionStandingType(factionEntry.getFactionId(),factionEntry.getValueWithEffectsOnEntity(le, (Player)sender));
			switch (standing)
			{
				case FACTION_ALLY:
					sender.sendMessage(difficultyColor + "* " + solEntity.getName() + " regards you as an ally. " + difficultyMessage);
					return true;
				case FACTION_WARMLY:
					sender.sendMessage(difficultyColor + "* " + solEntity.getName() + " looks upon you warmly. " + difficultyMessage);
					return true;
				case FACTION_KINDLY:
					sender.sendMessage(difficultyColor + "* " + solEntity.getName() + " kindly considers you. " + difficultyMessage);
					return true;
				case FACTION_AMIABLE:
					sender.sendMessage(difficultyColor + "* " + solEntity.getName() + " judges you amiably. " + difficultyMessage);
					return true;
				case FACTION_INDIFFERENT:
					sender.sendMessage(difficultyColor + "* " + solEntity.getName() + " regards you indifferently. " + difficultyMessage);
					return true;
				case FACTION_APPREHENSIVE:
					sender.sendMessage(difficultyColor + "* " + solEntity.getName() + " looks your way apprehensively. " + difficultyMessage);
					return true;
				case FACTION_DUBIOUS:
					sender.sendMessage(difficultyColor + "* " + solEntity.getName() + " glowers at you dubiously. " + difficultyMessage);
					return true;
				case FACTION_THREATENLY:
					sender.sendMessage(difficultyColor + "* " + solEntity.getName() + " glares are you threateningly! " + difficultyMessage);
					return true;
				case FACTION_SCOWLS:
					sender.sendMessage(difficultyColor + "* " + solEntity.getName() + " scowls at you ready to attack! " + difficultyMessage);
					return true;
				default:
					sender.sendMessage(difficultyColor + "* " + solEntity.getName() + " regards you indifferently. " + difficultyMessage);
					return true;
			}
		
		} catch (CoreStateInitException e)
		{
			
		}
		return true;
	}
}
