package com.solinia.solinia.Commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.solinia.solinia.Solinia3CorePlugin;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaGroup;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Utils.Utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class CommandGroupSelect implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
			return false;

		Player player = (Player) sender;

		if (args.length < 1)
		{
			player.sendMessage("Valid arguments are: /gs 1 /gs 2 etc to toggle target through your group members. Alternatively you can use the players name ie /gs mixxit");
		}
		
		try
		{
			ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
			if (solPlayer != null)
			{
				ISoliniaGroup group = solPlayer.getGroup();
				if (group == null)
				{
					player.sendMessage(ChatColor.RED + "You are not in a group! Selecting yourself only");
					StateManager.getInstance().getEntityManager().setEntityTarget(player,player);
				} else {
					try {  
						int groupNumber = Integer.parseInt(args[0]);
						
						UUID uuid = group.getMembers().get(groupNumber);
						if (uuid == null)
						{
							player.sendMessage(ChatColor.RED + "That person is not in your group! Selecting yourself only");
							StateManager.getInstance().getEntityManager().setEntityTarget(player,player);
						} else {
							LivingEntity le = (LivingEntity)Bukkit.getEntity(uuid);
							StateManager.getInstance().getEntityManager().setEntityTarget(player,le);
						}
				         return true;  
				      } catch (NumberFormatException e) {  
				    	  String groupMemberName = args[0];
				    	  boolean found = false;
							for (UUID uuid : group.getMembers())
							{
								LivingEntity le = (LivingEntity)Bukkit.getEntity(uuid);
								if (!le.getName().toUpperCase().equals(groupMemberName))
									continue;
								
								StateManager.getInstance().getEntityManager().setEntityTarget(player,le);
								found = true;
							}
							
							if (found == false)
							{
								player.sendMessage(ChatColor.RED + "That person is not in your group! Selecting yourself only");
								StateManager.getInstance().getEntityManager().setEntityTarget(player,player);
							}
				    	  
				    	  
				         return true;
				      }
					
				}
			}
		} catch (CoreStateInitException e)
		{
			
		}
		
		return true;
	}

}
