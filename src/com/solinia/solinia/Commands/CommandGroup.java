package com.solinia.solinia.Commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;

public class CommandGroup implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// TODO Auto-generated method stub
		if (!(sender instanceof Player)) {
			return false;
		}
		Player player = (Player)sender;
		
		try
		{
			ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(player);
			
			String groupname = "You have no group";
			if (solplayer.getGroup() != null)
			{
				UUID ownerid = solplayer.getGroup().getOwner();
				Player owner = Bukkit.getPlayer(ownerid);
				if (owner != null)
				{
					groupname = owner.getDisplayName() + "'s group";
				}
			}
			
			if (args.length == 0) {
				player.sendMessage("Your group: " + groupname);
				player.sendMessage("Valid commands are: /group invite <name>, /group list, /group accept, /group decline, /group leave,/g <msg>");
				return true;
			}
			
			if (args[0].equals("invite"))
			{
				if (args.length == 2)
				{
					Player target = Bukkit.getPlayer(args[1]);
					if (target != null)
					{
						if (!target.equals(player))
						{
							StateManager.getInstance().invitePlayerToGroup(player,target);					
						} else {
							player.sendMessage("You cannot invite yourself to a group");
						}
					} else {
						player.sendMessage(args[1] + " is not online");
					}
					return true;
				} else {
					player.sendMessage("Incorrect arguments ["+args.length+"] for group invite - see /group");
					return true;
				}
			}
			
			if (args[0].equals("leave"))
			{
				if (solplayer.getGroup() == null)
					return true;
				
				solplayer.getGroup().removePlayer(player);
				return true;
			}
			if (args[0].equals("list"))
			{
				if (solplayer.getGroup() == null)
					return true;
				
				solplayer.getGroup().sendGroupList(player);
				return true;
			}
			if (args[0].equals("accept"))
			{
				StateManager.getInstance().acceptGroupInvite(player);
				return true;
			}
			if (args[0].equals("decline"))
			{
				StateManager.getInstance().declineGroupInvite(player);
				return true;
			}
		} catch (CoreStateInitException e)
		{
			sender.sendMessage(e.getMessage());
		}
		
		return true;
	}

}
