package com.solinia.solinia.Commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaGroup;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;

import net.md_5.bungee.api.ChatColor;

public class CommandTarget implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
			return false;

		Player player = (Player) sender;

		if (args.length < 1)
		{
			player.sendMessage("Valid arguments are: /target entityname OR /target nearestnpc OR /target self OR /target 2 etc for group member no 2 OR /target pet for target pet OR /target clear to clear target");
			return true;
		}
		
		String target = args[0];
		
		try
		{
			if (target.equals("clear"))
			{
				player.sendMessage("Clearing target");
				StateManager.getInstance().getEntityManager().setEntityTarget(player,null);
				return true;
			}
			
			if (target.equals("self"))
			{
				player.sendMessage("Selecting yourself");
				StateManager.getInstance().getEntityManager().setEntityTarget(player,player);
				return true;
			}
			
			if (target.equals("nearestnpc"))
			{
				player.sendMessage("Selecting nearest npc");

				LivingEntity currentTarget = StateManager.getInstance().getEntityManager().getEntityTarget(player);

				for (Entity entity : player.getNearbyEntities(64.0D, 64.0D, 64.0D)) 
				{
					if (entity instanceof Player)
						continue;
					
					if (!(entity instanceof LivingEntity))
						continue;

					if (!player.hasLineOfSight(entity))
						continue;
					
					// Skip over existing target
					if (currentTarget != null && currentTarget.getUniqueId().equals(entity.getUniqueId()))
						continue;
					
					StateManager.getInstance().getEntityManager().setEntityTarget(player,(LivingEntity)entity);
					return true;
					
				}
				
				return true;
			}
			
			if (target.equals("pet"))
			{
				player.sendMessage("Selecting pet");
				LivingEntity pet = StateManager.getInstance().getEntityManager().getPet(player.getUniqueId());
				if (pet == null)
				{
					player.sendMessage(ChatColor.RED + "Could not find pet to target");
					return true;
				}
				
				StateManager.getInstance().getEntityManager().setEntityTarget(player,pet);
				return true;
			}
			
			if (target.equals("1") ||
					target.equals("2") ||
					target.equals("3") ||
					target.equals("4") ||
					target.equals("5") ||
					target.equals("6")
					)
			{
				player.sendMessage("Selecting group member no: " + target);
				ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
				
				if (solPlayer == null)
					return true;
				
				if (solPlayer.getGroup() == null)
				{
					player.sendMessage(ChatColor.RED + "You are not in a group!");
					return true;
				}
				
				ISoliniaGroup group = solPlayer.getGroup();
				
				int groupNumber = Integer.parseInt(target);
				
				if (group.getMembers().size() < groupNumber)
				{
					player.sendMessage(ChatColor.RED + "Cannot find member number: " + groupNumber);
					return true;
				}
				
				UUID uuid = group.getMembers().get(groupNumber-1);
				if (uuid == null)
				{
					player.sendMessage(ChatColor.RED + "Cannot find member number: " + groupNumber);
					return true;
				}
				
				LivingEntity le = (LivingEntity)Bukkit.getEntity(uuid);

				if (le.getLocation().distance(player.getLocation()) > 50)
				{
					player.sendMessage(ChatColor.RED + "That person is too far away! (max 50 blocks)");
					return true;
				}
					
				StateManager.getInstance().getEntityManager().setEntityTarget(player,le);
				return true;
			}
			
			// IF WE GET THIS FAR TRY TO FIND BY ENTITY NAME
			LivingEntity currentTarget = StateManager.getInstance().getEntityManager().getEntityTarget(player);
			
			for (Entity entity : player.getNearbyEntities(64.0D, 64.0D, 64.0D)) 
			{
				if (!(entity instanceof LivingEntity))
					continue;
				
				if (!entity.getName().equals(target))
					continue;
				
				// Skip over existing target
				if (currentTarget != null && currentTarget.getUniqueId().equals(entity.getUniqueId()))
					continue;
				
				StateManager.getInstance().getEntityManager().setEntityTarget(player,(LivingEntity)entity);
				return true;
				
			}
			
		} catch (CoreStateInitException e)
		{
			
		}
		
		return true;
	}
}
