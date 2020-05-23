package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.metadata.MetadataValue;

import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.ActiveSpellEffect;
import com.solinia.solinia.Models.SoliniaActiveSpell;

public class CommandAssist implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
		{
			sender.sendMessage("This is a player only command");
			return true;
		}
		
		Player player = (Player)sender;
		
		try
		{
			ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
			LivingEntity targetmob = solPlayer.getEntityTarget();
			if (targetmob == null)
			{
				player.sendMessage("You need to target an entity to assist it");
				return true;
			}

			ISoliniaLivingEntity solLivingEntity = SoliniaLivingEntityAdapter.Adapt(targetmob);
			if (solLivingEntity.getAttackTarget() != null)
			{
				solPlayer.setEntityTarget(solLivingEntity.getEntityTarget());
			}
		} catch (CoreStateInitException e)
		{
		} 

		return true;
	}

}